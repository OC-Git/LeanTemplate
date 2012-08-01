package de.objectcode.play2.plugin.monitoring;

import play.Application;
import play.Plugin;

public class MonitoringPlugin extends Plugin {

	private Application application;
	public static boolean requestLogginEnabled;

	public MonitoringPlugin(final Application application) {
		this.application = application;
	}

	@Override
	public void onStart() {
		final Boolean requestLogginEnabled = application.configuration().getBoolean("monitoring.enable_request_logging_db");
		if (requestLogginEnabled != null) MonitoringPlugin.requestLogginEnabled = requestLogginEnabled;
	}

}
