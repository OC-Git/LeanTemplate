package controllers.site;

import global.GenericDbImageController;
import play.mvc.Controller;
import play.mvc.Result;

public class DbImageController extends Controller {

	public static Result image(Long id) {
		return GenericDbImageController.image(id);
	}

	public static Result upload() {
		return GenericDbImageController.upload();
	}

}
