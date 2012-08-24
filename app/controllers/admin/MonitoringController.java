package controllers.admin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import models.monitoring.MonitorExceptionsFine;
import models.monitoring.MonitorResponseTimeFine;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.cache.Cache;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http.Response;
import play.mvc.Security;
import play.mvc.Http.Request;
import play.mvc.Result;

import au.com.bytecode.opencsv.CSVWriter;
import authenticate.admin.AdminSecured;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

@Security.Authenticated(AdminSecured.class) 
public class MonitoringController extends Controller {

	private static final String QUERY_STATS = "sum(request_count) as request_count, " +
											  "avg(response_time_avg) as response_time_avg, " +
											  "sum(exceptions_sum) as exceptions_sum, " +
											  "avg(db_connections_open) as db_connections_open, " +
											  "avg(db_connections_leased) as db_connections_leased, " +
											  "avg(heap_used) as heap_used, " +
											  "avg(heap_max) as heap_max, " +
											  "avg(heap_free) as heap_free, " +
											  "avg(swap_used) as swap_used, " +
											  "sum(gc_count) as gc_count, " +
											  "sum(gc_time_avg) as gc_time_avg, " +
											  "avg(load_avg) as load_avg, " +
											  "avg(thread_count) as thread_count " +
											  "from monitor_fine ";
	
	private static final String QUERY_STATS_REQUEST = "request_method as request_method, " +
												  	  "sum(request_count) as request_count, " +
													  "avg(response_time) as response_time " +
													  "from monitor_response_time_fine ";
	
	private static final String QUERY_STATS_EXCEPTION = "exception_type as exception_type, " +
														"sum(exceptions_sum) as exceptions_sum " +
														"from monitor_exceptions_fine ";
	
	public enum MonitorResolution {
		MINUTE("YYYY-MM-DD HH24:MI"),  
		HOUR("YYYY-MM-DD HH24"), 
		DAY("YYYY-MM-DD"), 
		MONTH("YYYY-MM"), 
		YEAR("YYYY");
		
		private String pattern;
		private MonitorResolution(final String pattern) {
			this.pattern = pattern; 
		}
		
		public String getPattern() {
			return pattern;
		}
	}	
	
	public static Result index() {
		GregorianCalendar cal = new GregorianCalendar();
		final Timestamp fromTs = new Timestamp(cal.getTimeInMillis());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		final Timestamp toTs = new Timestamp(cal.getTimeInMillis());
		
		final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return ok(views.html.admin.monitoring.render(df.format(fromTs), df.format(toTs)));
	}

	public static Result getDateBoundariesJson() {
		final ObjectNode node = Json.newObject();
		final Timestamp t1, t2;  
		
		final SqlRow row = Ebean.createSqlQuery("select min(timestamp), max(timestamp) from monitor_fine").findUnique();		
		t1 = row.getTimestamp("min");
		t2 = row.getTimestamp("max");
		
		node.put("startTimestamp", t1.toString());
		node.put("endTimestamp", t2.toString());
		node.put("startTimestampMillis", t1.getTime());
		node.put("endTimestampMillis", t2.getTime());
		return ok(node);
	}
	
	public static Result getDistinctNodesJson() {
		return loadDistinctStuff("select distinct node_id as node from monitor_fine", "node",
				"MonitoringController.distinctNodes");
	}
	
	public static Result getRequestMethodsJson() {
		return loadDistinctStuff("select distinct request_method as method from monitor_response_time_fine", "method",
				"MonitoringController.distinctRequestMethods");
	}
	
	public static Result getExceptionTypesJson() {
		return loadDistinctStuff("select distinct exception_type as type from monitor_exceptions_fine", "type",
				"MonitoringController.distinctExceptionTypes");
	}
	
	private static Result loadDistinctStuff(final String query, final String paraName, final String cacheToken) {
		ArrayNode nodeList = (ArrayNode) Cache.get(cacheToken);
		if (nodeList == null) {
			nodeList = Json.newObject().arrayNode();
			for (final SqlRow r : Ebean.createSqlQuery(query).findList()) {
				nodeList.add(r.getString(paraName));
			}
			Cache.set(cacheToken, nodeList, 180);
		}
		
		//FIXME: remove me !! 
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return ok(nodeList);
	}
	
	private static class DefaultJsonPerformer implements Callback {
		public JsonPopulator jsonPopulator;

		public DefaultJsonPerformer(final JsonPopulator jsonPopulator) {
			this.jsonPopulator = jsonPopulator;
		}

		public String call(final List<SqlRow> rowList, final boolean groupByNodes) throws IOException {
			final ArrayNode nodeList = Json.newObject().arrayNode();
			
			for (final SqlRow row : rowList) {
				final ObjectNode node = Json.newObject();
				jsonPopulator.call(node, row, groupByNodes);
				nodeList.add(node);
			}
			return nodeList.toString();
		}
	}
	
	private static class DefaultCsvPerformer implements Callback {
		public CsvPopulator csvPopulator;
		
		public DefaultCsvPerformer(CsvPopulator _csvPopulator) {
			csvPopulator = _csvPopulator;
		}
		
		@Override
		public String call(final List<SqlRow> rowList, final boolean groupByNodes) throws IOException {
			final StringWriter writer = new StringWriter(512);
			final CSVWriter csvWriter = new CSVWriter(writer);

			try {
				for (final SqlRow row : rowList) {
					csvWriter.writeNext(csvPopulator.call(row, groupByNodes).toArray(new String[0]));
				}
				csvWriter.flush();
			}
			finally {
				IOUtils.closeQuietly(csvWriter);
			}
			return writer.toString();
		}
	}
	
	public static Result statsJson(final String from, final String to, final String resolutionName, final String byNode) throws IOException {
		
		final JsonPopulator jsonPopulator = new JsonPopulator() {
			@Override
			public void call(ObjectNode node, SqlRow row, boolean groupByNodes) {
				final String printTimestamp = row.getString("timestamp");
				if (groupByNodes) node.put("nodeId", row.getString("node_id"));
				
				node.put("timestamp", printTimestamp);
				node.put("timestampMillis", fillTimestamp(printTimestamp).getTime());
				node.put("requestCount", row.getLong("request_count"));
				node.put("responseTimeAvg", row.getLong("response_time_avg"));
				node.put("exceptionsSum", row.getLong("exceptions_sum"));
				node.put("dbConnectionsOpen", row.getLong("db_connections_open"));
				node.put("dbConnectionsLeased", row.getLong("db_connections_leased"));
				node.put("heapUsed", row.getLong("heap_used"));
				node.put("heapMax", row.getLong("heap_max"));
				node.put("heapFree", row.getLong("heap_free"));
				node.put("swapUsed", row.getLong("swap_used"));
				node.put("gcCount", row.getLong("gc_count"));
				node.put("gcTimeAvg", row.getLong("gc_time_avg"));
				node.put("loadAvg", row.getDouble("load_avg"));
				node.put("threadCount", row.getLong("thread_count"));
			}
		};
		
		return stats(from, to, resolutionName, byNode, QUERY_STATS, null, new DefaultJsonPerformer(jsonPopulator));
	}	
	
	public static Result statsCsv(final String from, final String to, final String resolutionName, final String byNode) throws IOException {
		
		final CsvPopulator csvPopulator = new CsvPopulator() {
			@Override
			public List<String> call(final SqlRow row, final boolean groupByNodes) {
				final List<String> list = new ArrayList<String>(16);
				
				final String printTimestamp = row.getString("timestamp");
				list.add(printTimestamp);
				list.add(String.valueOf(fillTimestamp(printTimestamp).getTime()));
				
				if (groupByNodes) list.add(row.getString("node_id"));
				list.add(String.valueOf(row.getLong("request_count")));
				list.add(String.valueOf(row.getLong("response_time_avg")));
				list.add(String.valueOf(row.getLong("exceptions_sum")));
				list.add(String.valueOf(row.getLong("db_connections_open")));
				list.add(String.valueOf(row.getLong("db_connections_leased")));
				list.add(String.valueOf(row.getLong("heap_used")));
				list.add(String.valueOf(row.getLong("heap_max")));
				list.add(String.valueOf(row.getLong("heap_free")));
				list.add(String.valueOf(row.getLong("swap_used")));
				list.add(String.valueOf(row.getLong("gc_count")));
				list.add(String.valueOf(row.getLong("gc_time_avg")));
				list.add(String.valueOf(row.getDouble("load_avg")));
				list.add(String.valueOf(row.getLong("thread_count")));
				
				return list;
			}
		};
		
		return stats(from, to, resolutionName, byNode, QUERY_STATS, null, new DefaultCsvPerformer(csvPopulator));
	}

	public static Result statsByRequestJson(final String from, final String to, final String resolutionName,
		final String byNode) throws IOException {

		final JsonPopulator jsonPopulator = new JsonPopulator() {
			@Override
			public void call(ObjectNode node, SqlRow row, boolean groupByNodes) {
				final String printTimestamp = row.getString("timestamp");
				if (groupByNodes) node.put("nodeId", row.getString("node_id"));

				node.put("timestamp", printTimestamp);
				node.put("timestampMillis", fillTimestamp(printTimestamp).getTime());
				node.put("requestMethod", row.getString("request_method"));
				node.put("requestCount", row.getLong("request_count"));
				node.put("responseTime", row.getLong("response_time"));
			}
		};
		return stats(from, to, resolutionName, byNode, QUERY_STATS_REQUEST, "request_method", new DefaultJsonPerformer(jsonPopulator));
	}
	
	public static Result statsByRequestCsv(final String from, final String to, final String resolutionName,
		final String byNode) throws IOException {

		final CsvPopulator csvPopulator = new CsvPopulator() {
			@Override
			public List<String> call(final SqlRow row, final boolean groupByNodes) {
				final List<String> list = new ArrayList<String>(6);
				
				final String printTimestamp = row.getString("timestamp");
				if (groupByNodes) list.add(row.getString("node_id"));

				list.add(printTimestamp);
				list.add(String.valueOf(fillTimestamp(printTimestamp).getTime()));
				list.add(row.getString("request_method"));
				list.add(String.valueOf(row.getLong("request_count")));
				list.add(String.valueOf(row.getLong("response_time")));
				
				return list;
			}
		};
		return stats(from, to, resolutionName, byNode, QUERY_STATS_REQUEST, "request_method", new DefaultCsvPerformer(csvPopulator));
	}	
	
	public static Result statsByExceptionJson(final String from, final String to, final String resolutionName, final String byNode) throws IOException {
		final JsonPopulator jsonPopulator = new JsonPopulator() {
			@Override
			public void call(ObjectNode node, SqlRow row, boolean groupByNodes) {
				final String printTimestamp = row.getString("timestamp");
				if (groupByNodes) node.put("nodeId", row.getString("node_id"));
				
				node.put("timestamp", printTimestamp);
				node.put("timestampMillis", fillTimestamp(printTimestamp).getTime());
				node.put("exceptionType", row.getString("exception_type"));
				node.put("exceptionsSum", row.getLong("exceptions_sum"));
			}
		};
		return stats(from, to, resolutionName, byNode, QUERY_STATS_EXCEPTION, "exception_type", new DefaultJsonPerformer(jsonPopulator));
	}	
	
	public static Result statsByExceptionCsv(final String from, final String to, final String resolutionName, final String byNode) throws IOException {
		final CsvPopulator csvPopulator = new CsvPopulator() {
			@Override
			public List<String> call(final SqlRow row, final boolean groupByNodes) {
				final List<String> list = new ArrayList<String>(5);
				
				final String printTimestamp = row.getString("timestamp");
				if (groupByNodes) list.add(row.getString("node_id"));
				
				list.add(printTimestamp);
				list.add(String.valueOf(fillTimestamp(printTimestamp).getTime()));
				list.add(row.getString("exception_type"));
				list.add(String.valueOf(row.getLong("exceptions_sum")));
				
				return list;
			}
		};
		return stats(from, to, resolutionName, byNode, QUERY_STATS_EXCEPTION, "exception_type", new DefaultCsvPerformer(csvPopulator));
	}	
	
	private static Result stats(final String from, final String to, final String resolutionName, final String byNode,
		final String queryPart, final String extraGroupBy, final Callback callback) throws IOException {
	
		final Timestamp fromTs;
		final Timestamp toTs;
		final boolean groupByNodes = byNode != null && byNode.equals("1");

		try {
			fromTs = new Timestamp(Long.parseLong(from));
			toTs = new Timestamp(Long.parseLong(to));
		}
		catch(Exception e) {
			return badRequest("invalid date format");
		}

		final MonitorResolution resolution;
		try {
			resolution = MonitorResolution.valueOf(resolutionName);
		}
		catch(final IllegalArgumentException e) {
			return badRequest("invalid resolution");
		}
		
		final StringBuilder bf = new StringBuilder(512);
		final String groupBy = "to_char(timestamp, '" + resolution.getPattern() + "')";
		
		bf.append("select ").append(groupBy).append(" as timestamp,");
		if (groupByNodes) bf.append("node_id,");
		bf.append(queryPart);
		bf.append( "where timestamp >= :from_date and timestamp  <= :to_date group by ").append(groupBy);
		
		if (extraGroupBy != null) bf.append(", ").append(extraGroupBy);
		
		if (groupByNodes) bf.append(", node_id");
		bf.append(" order by ").append(groupBy);
		
		if (Logger.isInfoEnabled()) {
			Logger.info("query=" + bf.toString());
		}
		
		final List<SqlRow> rowList = Ebean.createSqlQuery(bf.toString()).setParameter("from_date", fromTs)
				.setParameter("to_date", toTs).findList();

		return (deflateJson(callback.call(rowList, groupByNodes)));
	}
	
	private static interface Callback {
		String call(List<SqlRow> rowList, boolean groupByNodes) throws IOException;
	}
	
	private static interface JsonPopulator {
		void call(ObjectNode node, SqlRow row, boolean groupByNodes);
	}
	
	private static interface CsvPopulator {
		List<String> call(SqlRow row, boolean groupByNodes);
	}
	
	private static Result deflateJson(final String jsonText) throws IOException {
		final Request request = request();
		final String acceptEncoding = request.getHeader("Accept-Encoding");
		
		if (acceptEncoding != null && Pattern.compile("(?:^|,\\s?)deflate(?:,|$)").matcher(acceptEncoding).find()) {
			ByteArrayOutputStream out = null;
			OutputStream gzipOutputStream = null;
			
			try {
				out = new ByteArrayOutputStream((int) (jsonText.length() * 0.75));
				gzipOutputStream = new DeflaterOutputStream(out);
				
				gzipOutputStream.write(jsonText.getBytes("UTF-8"));
			}
			finally {
				IOUtils.closeQuietly(gzipOutputStream);
				IOUtils.closeQuietly(out);
			}
			
			final byte[] byteArray = out.toByteArray();
			response().setContentType("application/json");
			response().setHeader("Content-Encoding", "deflate");
			response().setHeader("Content-Length", byteArray.length + "");
			
			return ok(byteArray);
		}
		return ok(jsonText);
	}
	
	private static Timestamp fillTimestamp(final String pts) {
		final StringBuilder master = new StringBuilder("2000-01-01 00:00:00");
		for (int i=0; i<pts.length(); i++) {
			master.setCharAt(i, pts.charAt(i));
		}
		return Timestamp.valueOf(master.toString());
	}
	
}
