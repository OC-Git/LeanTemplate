package authenticate.site;

import play.mvc.Http.Context;
import play.mvc.Result;
import authenticate.AbstractSecured;
import controllers.site.routes;

public class SiteSecured extends AbstractSecured {

	@Override
	public Result onUnauthorized(final Context _ctx) {
		super.onUnauthorized(_ctx);
		return redirect(routes.LoginController.index());
	}
}
