package controllers;

import static controllers.forms.FlashScope.ERROR;
import static controllers.forms.FlashScope.WARN;
import models.User;
import play.Logger;
import play.api.templates.Html;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import authenticate.Authenticated;
import controllers.forms.LoginForm;

public class DefaultLoginController extends Controller {

	protected static Form<LoginForm> loginForm = Form.form(LoginForm.class);

	public static interface LoginLogicCallback {
		public Result perform(User loginUser, Form<LoginForm> _form);
		public Html getErrorHtml(Form<LoginForm> form);
	}

	public static Result authenticate(final LoginLogicCallback loginLogicCallback) {
		final Form<LoginForm> bindForm = loginForm.bindFromRequest();
		Logger.info("DefaultLoginController::authenticate " + bindForm);

		if (bindForm.hasErrors()) {
			flash(ERROR, "Fehler beim Ausfüllen des Formulars!");
			return badRequest(loginLogicCallback.getErrorHtml(bindForm));
		}

		final LoginForm myForm = bindForm.get();
		Logger.info("DefaultLoginController: got valid form binding=" + myForm);

		// find user by email.
		final User loginUser = User.find.byLabel(myForm.getEmail());
		if (loginUser == null) {
			Logger.info("DefaultLoginController:: user not found for loginName=" + myForm.getEmail());
			flash(WARN, "Benutzer oder Passwort falsch!");
			return badRequest(loginLogicCallback.getErrorHtml(bindForm));
		}

		// password matches ?
		if (!Authenticated.isCorrectPasswordForLogin(loginUser, myForm.getPassword())) {
			Logger.info("DefaultLoginController:: invalid password for user=" + loginUser);
			flash(WARN, "Benutzer oder Passwort falsch!");
			return badRequest(loginLogicCallback.getErrorHtml(bindForm));
		}
		
		if (! loginUser.isValidated()) {
			Logger.info("DefaultLoginController:: user not validated for user=" + loginUser);
			flash(WARN, "Benutzer oder Passwort falsch!");
			return badRequest(loginLogicCallback.getErrorHtml(bindForm));
		}		

		final Result logicResult = loginLogicCallback.perform(loginUser, bindForm);
		if (logicResult != null) return logicResult;

		Authenticated.loginUser(loginUser);
		final String newPath = Authenticated.getOrgRequestPathOnUnauthorized() != null ? Authenticated
				.getOrgRequestPathOnUnauthorized() : "/";

		return redirect(newPath);
	}
	

}
