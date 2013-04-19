package controllers.site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Ding;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryParser.ParseException;

import play.Logger;
import play.data.Form;
import play.mvc.Result;
import play.mvc.Security;
import search.DefaultSearchController;
import search.entities.DingListener;
import authenticate.site.SiteSecured;
import de.objectcode.play2.plugin.monitoring.Aggregator;
import de.objectcode.play2.plugin.monitoring.RequestLoggerAction;

public class Application extends DefaultSearchController {

	public static final int MAX_RESULTS = 200;
	static int counter;
	static int counter2;
	
	public static Result index() throws IOException, ParseException {
		RequestLoggerAction.registerControllerMethod();
		
		final Form<SearchForm> bindForm = form.bindFromRequest();
		if (bindForm.hasErrors()) {
			flash().put("error", "Ungültige Suchanfrage");
			return badRequest(views.html.site.index.render(bindForm, null, null));
		}

		final SearchForm myForm = bindForm.get();
		if (myForm.maxRows != null && myForm.maxRows > MAX_RESULTS) {
			myForm.maxRows = MAX_RESULTS;
		}
		
		List<Ding> dings;
		final long t1 = System.currentTimeMillis();
		if (StringUtils.isEmpty(myForm.query)) {
			dings = Ding.find.finder.fetch("image").fetch("image.thumbnail").findList();
			
		} else {
			final List<Long> idList = find(myForm.query, myForm.maxRows == null ? DEFAULT_MAX_ROWS : myForm.maxRows,
					DingListener.INSTANCE, DingListener.INSTANCE.fieldNames());
			
			final long t2 = System.currentTimeMillis();
			Logger.info("Found " + idList.size() + " entries for query=" + myForm.query);
			
			final Float took = (t2 - t1) / 1000F;
			
			dings = new ArrayList<Ding>(idList.size());
			for (final Long id : idList) {
				final Ding ding = Ding.find.finder.fetch("image").fetch("image.thumbnail").where().eq("id", id).findUnique();
				if (ding == null) continue;
				dings.add(ding);
			}
		}
		final long t2 = System.currentTimeMillis();
		final Float took = (t2 - t1) / 1000F;
		
		return ok(views.html.site.index.render(bindForm, dings, took));
	}
	
	@Security.Authenticated(SiteSecured.class)
	public static Result dummy() throws Exception {
		RequestLoggerAction.registerControllerMethod();
		counter++;
		if (counter % 3 == 0) {
			counter2++;
			Thread.sleep(1000);
			if (counter2 % 2 == 0 ) throw new RuntimeException("THIS IS A TEST");
			else throw new IllegalArgumentException("THIS IS ANOTHER TEST");
		}
		
		return ok("dummy:" + Aggregator.get());
	}

}