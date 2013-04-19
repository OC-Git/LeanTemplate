package authenticate.admin;

import models.Role;
import models.User;
import play.mvc.Http.Context;
import play.mvc.Result;
import authenticate.AbstractSecured;
import authenticate.Authenticated;
import controllers.admin.routes;

public class AdminSecured extends AbstractSecured {

	@Override
	public String getUsername(final Context ctx) {
		final User loggedinUser = Authenticated.getAuthenticatedUser(ctx.session());
		if (loggedinUser != null && loggedinUser.getRole() == Role.admin) return String.valueOf(loggedinUser.getId());
		return null;
	}

	@Override
	public Result onUnauthorized(final Context _ctx) {
		super.onUnauthorized(_ctx);
		return redirect(routes.LoginController.index());
	}

}
