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

public class ShoppingController extends Controller {

	public static class ShoppingCartAdd {
		public long id;
	}
	
	private static Form<ShoppingCartAdd> shoppingCartAddForm = form(ShoppingCartAdd.class);
	
	@Security.Authenticated(SiteSecured.class)
	public static Result addToShoppingCart() {
		
		final Form<ShoppingCartAdd> bindForm = shoppingCartAddForm.bindFromRequest();
		if (bindForm.hasErrors()) {
			flash().put("error", "Ungültiger Request");
			return badRequest();
		}

		ShoppingCartAdd shoppingCartAdd = bindForm.get();
		
		Ding ding = Ding.find.byId(shoppingCartAdd.id);
		
		User user = Authenticated.getAuthenticatedUser();
		
		ShoppingCart cart = user.getOrCreateShoppingCart();
		
		ShoppingCartEntry entry = findExistingEntry(user, ding);
		if (entry==null) {
			entry = new ShoppingCartEntry();
			entry.setDing(ding);
			cart.getEntries().add(entry);
		}
		entry.setAmount(entry.getAmount()+1);
		user.saveOrUpdate();
		
		return redirect("/shopping/showShoppingCart");
	}

	@Security.Authenticated(SiteSecured.class)
	public static Result showShoppingCart() {
		User user = Authenticated.getAuthenticatedUser();
		user.getOrCreateShoppingCart();
		return ok(views.html.site.shoppingCart.render(user));
	}
	
	@Security.Authenticated(SiteSecured.class)
	public static Result showPayment() {
		User user = Authenticated.getAuthenticatedUser();
		return ok(views.html.site.payment.render(user));
	}

	@Security.Authenticated(SiteSecured.class)
	public static Result order() {
		return redirect("/shopping/showOrderConfirmation");
	}
	
	@Security.Authenticated(SiteSecured.class)
	public static Result showOrderConfirmation() {
		User user = Authenticated.getAuthenticatedUser();
		return ok(views.html.site.showOrderConfirmation.render(user));
	}
	
	private static ShoppingCartEntry findExistingEntry(User user, Ding ding) {
		List<ShoppingCartEntry> entries = user.getShoppingCart().getEntries();
		for (ShoppingCartEntry entry : entries) {
			if (entry.getDing().equals(ding)) {
				return entry;
			}
		}
		return null;
	}

}
