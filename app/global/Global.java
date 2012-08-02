package global;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import models.User;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.data.format.Formatters;
import play.mvc.Action;
import play.mvc.Http.Request;
import com.avaje.ebean.Ebean;

import de.objectcode.play2.plugin.monitoring.RequestLoggerAction;

public class Global extends GlobalSettings {

    public final static String APP_NAME = "LeanTemplate"; 
	
    @SuppressWarnings("rawtypes")
	@Override
    public Action onRequest(final Request _request, final Method _actionMethod) {
    	return new RequestLoggerAction();
    }
    
	@Override
	public void onStart(final Application app) {
		Logger.debug("onStart()");
		setupLogging(app);
		createInitialDatabase(app);
		Formatters.register(Timestamp.class, new TimeStampFormatter());
		Formatters.register(java.sql.Date.class, new Html5DateFormatter());
		Formatters.register(BigDecimal.class, new BigDecimalFormatter());
		MvTest.get().startAutoUpdate();
	}
	
	private void setupLogging(final Application app) {
		if (Play.isDev() || Play.isTest()) {
//			Ebean.getServer(null).getAdminLogging().setDebugGeneratedSql(false);
		}
	}
	
	private void createInitialDatabase(final Application app)  {
		if (Ebean.find(User.class).findRowCount() != 0) {
			Logger.info("found entries in user table, skipping creation of initial database");
			return;
		}
		final File sqlFile = app.getFile("conf/database/" + getDatabaseFile(app));
		if (!sqlFile.canRead()) {
			Logger.error("File not found: " + sqlFile.getAbsolutePath());
			return;
		}
		List<String> lines;
		try {
			lines = FileUtils.readLines(sqlFile, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		for (String row : lines) {
			if (row.startsWith("--") || StringUtils.isBlank(row)) {
				continue;
			}
			Logger.info("init sql statement: " + row);
			Ebean.createSqlUpdate(row).execute();
		}
	}

	private String getDatabaseFile(final Application app) {
		if (app.isDev()) return app.configuration().getString("my.database.initdata.devel"); 
		if (app.isProd()) return app.configuration().getString("my.database.initdata.prod");
		if (app.isTest()) return app.configuration().getString("my.database.initdata.test");
		return null;
	}
	
}
