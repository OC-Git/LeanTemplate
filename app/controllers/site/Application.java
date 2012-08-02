package controllers.site;

import java.util.List;

import models.DbImage;
import models.Ding;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import authenticate.site.SiteSecured;
import de.objectcode.play2.plugin.monitoring.Aggregator;
import de.objectcode.play2.plugin.monitoring.infoadapter.impl.BoneCPInfoAdapter;
import de.objectcode.play2.plugin.monitoring.infoadapter.impl.linux.SwapProcFSInfoAdapter;

public class Application extends Controller {

	static int counter;
	
	public static Result index() {
		// FIXME fetch!!
		List<Ding> dings = Ding.find.all();
		// Falls der folgende Dummy-Code nicht durchgeführt wird, sind die Relationen in Ding (wie Ding.image) im View nicht geladen - obwohl sie EAGER sind!
	    String s = "";
		for (Ding ding : dings) {
			DbImage image = ding.getImage();
			if (image!=null) {
				// hier wird z.B. ausgegeben: RawImage [id=2, width=0, height=0, mimetype=null]
				// nützt noch nichts, da RawImage.toString direkt auf die Felder geht
				s+=image.getThumbnail();
				// width=0
//				s+=image.getThumbnail().width;
				// erst jetzt ist das Thumbnail geladen und initialisiert: width=260!
				s+=image.getThumbnail().getWidth();
				// => EBean lädt das Objekt immer lazy und kann deshalb (natürlich) nur den Zugriff auf Methoden abfangen!
				// => public Fields sind ein Antipattern in Play!! 
			}
		} 
		return ok(views.html.site.index.render (dings));
	}
	
	@Security.Authenticated(SiteSecured.class)
	public static Result dummy() throws Exception {
//		final BoneCPInfoAdapter s = new BoneCPInfoAdapter();
//		return ok(""+ s.getCreatedConnectionCount() + "," + s.getFreeConnectionCount() + ", " + s.getLeasedCounnectionCount());
		
		counter++;
		if (counter % 3 == 0) {
			Thread.sleep(1000);
			throw new RuntimeException("THIS IS A TEST");
		}
		
		return ok("dummy:" + Aggregator.get());
	}

}