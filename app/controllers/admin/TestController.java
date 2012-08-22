package controllers.admin;

import java.sql.Timestamp;
import java.util.Random;

import models.monitoring.MonitorFine;

import com.avaje.ebean.Ebean;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import authenticate.admin.AdminSecured;

@Security.Authenticated(AdminSecured.class)
public class TestController extends Controller {

	public static Result clearReportingTables() {
		Ebean.createSqlUpdate("delete from monitor_fine").execute();
		Ebean.createSqlUpdate("delete from monitor_exceptions_fine").execute();
		Ebean.createSqlUpdate("delete from monitor_response_time_fine").execute();
		return ok("cleared");
	}

	public static Result createDummyData() {

//		Ebean.beginTransaction();
		
		final String[] nodes = new String[] { "127.0.0.1", "172.16.0.100", "172.16.0.101", "192.168.178.1",
				"192.168.178.2" };

		Random r = new Random();
		
		// generate one year, one record per minute
		long stopDate = System.currentTimeMillis() * 1000 * 60 * 60 * 24 * 365;
		int count = 0;
		stopDate = stopDate / 2;
		
		for (long i = System.currentTimeMillis(); i <= stopDate; i += 1000 * 60) {
			final Timestamp now = new Timestamp(i);
			count ++; 
			if (count % 10 == 0) {
				System.out.println("CREATING FOR DATE=" + now);
			}
			
			Ebean.beginTransaction();
			for (final String node : nodes) {
				
				int requestCount = r.nextInt(200);
				int responseTime = r.nextInt(500);
				int exceptionsSum = r.nextInt(3);
				int dbConnections = 10 + r.nextInt(20);
				int dbLeased = 0;
				int heapUsed = 99999;
				int heapMax = 100000;
				int gcCount = 5 + r.nextInt(50);
				int gcAvg = 10 + r.nextInt(50);
				float load = r.nextFloat();
				int threads = 50 + r.nextInt(100);
				
				MonitorFine mf = new MonitorFine();
				mf.setTimestamp(now);
				mf.setDbConnectionsLeased(dbLeased);
				mf.setDbConnectionsOpen(dbConnections);
				mf.setExceptionsSum(exceptionsSum);
				mf.setGcCount(gcCount);
				mf.setGcTimeAvg(gcAvg);
				mf.setHeapFree(0);
				mf.setHeapMax(heapMax);
				mf.setHeapUsed(heapUsed);
				mf.setLoadAvg(load);
				mf.setNodeId(node);
				mf.setRequestCount(requestCount);
				mf.setResponseTimeAvg(responseTime);
				mf.setSwapUsed(9999);
				mf.setThreadCount(threads);
				
				mf.save();
			}
			Ebean.commitTransaction();
			Ebean.endTransaction();
		}
		
//		Ebean.commitTransaction();
//		Ebean.endTransaction();

		return ok("DDDDDDDDddcreated");
	}

}
