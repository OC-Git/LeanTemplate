package controllers.site;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPDataSource;
import com.jolbox.bonecp.Statistics;

import play.db.DB;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import authenticate.site.SiteSecured;
import de.objectcode.play2.plugin.monitoring.Aggregator;
import de.objectcode.play2.plugin.monitoring.infoadapter.impl.BoneCPInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.impl.linux.SwapProcFSInfoAdapter;

public class Application extends Controller {

	static int counter;
	
	public static Result index() {
		return ok(views.html.site.index.render());
	}
	
	@Security.Authenticated(SiteSecured.class)
	public static Result dummy() throws Exception {
//		final BoneCPInfoAdapter s = new BoneCPInfoAdapter();
//		return ok(""+ s.getCreatedConnectionCount() + "," + s.getFreeConnectionCount() + ", " + s.getLeasedCounnectionCount());
		
		counter++;
		if (counter % 3 == 0) {
			Thread.sleep(1000);
			throw new RuntimeException("THIS IS A TEST");
		}
		
		return ok("dummy:" + Aggregator.get());
	}

}