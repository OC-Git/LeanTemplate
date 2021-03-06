package controllers.site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Ding;

import org.apache.lucene.queryParser.ParseException;

import play.Logger;
import play.data.Form;
import play.mvc.Result;
import search.DefaultSearchController;
import search.entities.DingListener;
import de.objectcode.play2.plugin.monitoring.RequestLoggerAction;

public class DingSearchController extends DefaultSearchController {

	public static final int MAX_RESULTS = 200;
	
	public static Result index() {
		RequestLoggerAction.registerControllerMethod("DingSearchController.index");
		return ok(views.html.site.dingSearch.render(form, null, null));
	}

	public static Result search() throws IOException, ParseException {

		final Form<SearchForm> bindForm = form.bindFromRequest();
		if (bindForm.hasErrors()) {
			flash().put("error", "Ungültige Suchanfrage");
			return badRequest(views.html.site.dingSearch.render(bindForm, null, null));
		}

		final SearchForm myForm = bindForm.get();
		if (myForm.maxRows != null && myForm.maxRows > MAX_RESULTS) {
			myForm.maxRows = MAX_RESULTS;
		}
		
		final long t1 = System.currentTimeMillis();
		final List<Long> idList = find(myForm.query, myForm.maxRows == null ? DEFAULT_MAX_ROWS : myForm.maxRows,
				DingListener.INSTANCE, DingListener.INSTANCE.fieldNames());
		
		final long t2 = System.currentTimeMillis();
		Logger.info("Found " + idList.size() + " entries for query=" + myForm.query);
		
		final Float took = (t2 - t1) / 1000F;
		
		final List<Ding> dingList = new ArrayList<Ding>(idList.size());
		for (final Long id : idList) {
			final Ding ding = Ding.find.byId(id);
			if (ding == null) continue;
			dingList.add(ding);
		}
		return ok(views.html.site.dingSearch.render(bindForm, dingList, took));
	}
}
