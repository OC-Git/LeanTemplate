package controllers.site;

import java.util.List;

import models.Ding;
import models.ShoppingCart;
import models.ShoppingCartEntry;
import models.User;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import authenticate.Authenticated;
import authenticate.site.SiteSecured;

public class DingController extends Controller {

	public static Result show(Long id) {
		Ding ding = Ding.find.byId(id);
		return ok(views.html.site.dingShow.render(ding));
	}

}
