package controllers.site;

import models.Ding;
import play.mvc.Controller;
import play.mvc.Result;

public class DingController extends Controller {

	public static Result show(Long id) {
		Ding ding = Ding.find.byId(id);
		return ok(views.html.site.dingShow.render(ding));
	}

}
