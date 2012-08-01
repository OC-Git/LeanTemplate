package de.objectcode.play2.plugin.monitoring;

import models.monitoring.LogHttpRequest;
//import authenticate.Authenticated;
import play.mvc.Action;
import play.mvc.Result;
import play.mvc.Http.Context;
import play.mvc.Http.Request;

public class RequestLogger {

	 //FIXME: add remote IP (from header and response code)
	public static Result log(final Context ctx, final Request _request, final Action<?> delegate) throws Throwable {
		
		if (!MonitoringPlugin.requestLogginEnabled) 
			return delegate.call(ctx);
		
		final LogHttpRequest log = new LogHttpRequest();
		log.setStartTime(System.currentTimeMillis());
		log.setFromIP(null);
		log.setHost(_request.host());
		log.setMethod(_request.method());
		log.setReferer(_request.getHeader("referer"));
		log.setUrl(_request.uri());
		log.setUserAgent(_request.getHeader("user-agent"));

		final Result result = delegate.call(ctx);

		// FIXME: 
//		log.setUserId(ctx.session().get(Authenticated.SESSION_KEY_UUID));
		log.setEndTime(System.currentTimeMillis());
		log.save();
		return result;
	}
}
