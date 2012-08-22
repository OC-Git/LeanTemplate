package de.objectcode.play2.plugin.monitoring;

import models.monitoring.LogHttpRequest;
import play.Logger;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;

public class RequestLoggerAction extends Action<Void> {

	private static ThreadLocal<String> commingFromControllerHint = new ThreadLocal<String>();
	
	public static void registerControllerMethod(final String name) {
		commingFromControllerHint.set(name);
	}
	
	public static void registerControllerMethod() {
		final StackTraceElement[] st = Thread.currentThread().getStackTrace();
		for (StackTraceElement e : st) {
			if (e.getClassName().startsWith("controllers.")) {
				registerControllerMethod(e.getClassName() + "." + e.getMethodName());
				return;
			}
		}
	}	
	
	@Override
	public Result call(final Context _ctx) throws Throwable {
		registerControllerMethod(null);
		final long t1 = System.currentTimeMillis();
		final Aggregator agg = Aggregator.get();
		
		try {
			final Result call = delegate.call(_ctx);
			return call;
		} 
		catch (final Throwable up) {
			Logger.error("Uncaucht Exception during request=" + up, up);
			if (agg != null) agg.incrementExceptionCounter(up);
			throw up;
		} 
		finally {
			final long t2 = System.currentTimeMillis();
			final long requestDuration = t2 - t1;
			
			if (agg != null) {
				agg.incrementRequestCounter(requestDuration, commingFromControllerHint.get());
			}
			logRequest(_ctx, t1, t2);
		}
	}

	protected void logRequest(final Context _ctx, final long t1, final long t2) {
		if (!MonitoringPlugin.requestLogginEnabled) return;

		try {
			final Request request = _ctx.request();
			final LogHttpRequest log = new LogHttpRequest();
			
			log.setStartTime(t1);
			log.setEndTime(t2);
			log.setHost(request.host());
			log.setMethod(request.method());
			log.setReferer(request.getHeader("referer"));
			log.setUrl(request.uri());
			log.setUserAgent(request.getHeader("user-agent"));
	
			// FIXME:
			log.setFromIP(null);
			log.save();
		}
		catch(final Exception e) {
			Logger.error("Could not persist logRequest in DB due to " + e, e);
		}
	}

}
