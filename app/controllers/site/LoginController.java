package controllers.site;

import models.User;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Result;
import controllers.DefaultLoginController;
import controllers.forms.FlashScope;
import controllers.forms.LoginForm;

public class LoginController extends DefaultLoginController {

	public static Result index() {
		return ok(views.html.site.login.render(loginForm));
	}
	
	public static Result authenticate() {
		final LoginLogicCallback c = new LoginLogicCallback() {

			@Override
			public Result perform(final User loginUser, Form<LoginForm> _form) {
				// anybody (currently this is "user" and "admin") can login here 
				return null;
			}

			@Override
			public Html getErrorHtml(Form<LoginForm> _form) {
				return views.html.site.login.render(_form);
			}
		};
		return authenticate(c);
	}
	
	public static Result signOut() {
		session().clear();
		flash().put(FlashScope.SUCCESS, "Sie wurden ausgeloggt.");
		return redirect(routes.Application.index());
	}
}
