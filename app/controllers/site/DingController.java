package controllers.site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import models.Ding;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import authenticate.Authenticated;

import de.objectcode.play2.plugin.search.EntityConfig;
import de.objectcode.play2.plugin.search.SearchPlugin;

import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import search.DefaultSearchController;
import search.entities.DingListener;

public class DingController extends Controller {

	public static Result show(Long id) {
		Ding ding = Ding.find.byId(id);
		return ok(views.html.site.dingShow.render(ding));
	}

}
