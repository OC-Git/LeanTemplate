package controllers.admin;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.admin.index;
import authenticate.admin.AdminSecured;

@Security.Authenticated(AdminSecured.class)
public class Application extends Controller {

	public static Result index() {
		return ok(index.render());
	}

	public static Result dummy() {
		return ok("Application::dummy");
	}

}