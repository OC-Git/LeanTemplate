package controllers.admin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.cache.Cache;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Security;
import play.mvc.Http.Request;
import play.mvc.Result;

import authenticate.admin.AdminSecured;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

@Security.Authenticated(AdminSecured.class) 
public class MonitoringController extends Controller {

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

	public static Result getDistinctNodesJson() {
		ArrayNode nodeList = (ArrayNode) Cache.get("MonitoringController.distinctNodes");
		if (nodeList == null) {
			nodeList = Json.newObject().arrayNode();
			for (final SqlRow r : Ebean.createSqlQuery("select distinct node_id as node from monitor_fine").findList()) {
				nodeList.add(r.getString("node"));
			}
			Cache.set("MonitoringController.distinctNodes", nodeList, 180);
		}
		return ok(nodeList);
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
	
	public static Result statsJson(final String from, final String to, final String resolutionName, final String byNode) throws IOException {
		
		// get form data: 
		// from-date, to-date, resolution
		// for single node ? or all ? 
		
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
		bf.append(
				"sum(request_count) as request_count, " +
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
				"from monitor_fine " +
				"where timestamp >= :from_date and timestamp  <= :to_date " + 
				"group by ").append(groupBy);

		if (groupByNodes) bf.append(", node_id");
		bf.append(" order by ").append(groupBy);

		final List<SqlRow> rowList = Ebean.createSqlQuery(bf.toString()).setParameter("from_date", fromTs)
				.setParameter("to_date", toTs).findList();

		final ArrayNode nodeList = Json.newObject().arrayNode();
		for (final SqlRow row : rowList) {
			final ObjectNode node = Json.newObject();
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
			node.put("loadAvg", row.getLong("load_avg"));
			node.put("threadCount", row.getLong("thread_count"));
			nodeList.add(node);
		}

		return deflateJson(nodeList.toString());
	}
	
	private static Result deflateJson(final String jsonText) throws IOException {
		final Request request = request();
		final String acceptEncoding = request.getHeader("Accept-Encoding");
		
		if (acceptEncoding != null && Pattern.compile("(?:^|[^a-zA-Z0-9])deflate(?:[^a-zA-Z0-9]|$)").matcher(acceptEncoding).find()) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream((int) (jsonText.length() * 0.75));
			final OutputStream gzipOutputStream = new DeflaterOutputStream(out);
			
			gzipOutputStream.write(jsonText.getBytes("UTF-8"));
			gzipOutputStream.close();
			out.close();
			
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
