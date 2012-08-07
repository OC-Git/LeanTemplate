package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.avaje.ebean.annotation.PrivateOwned;

@SuppressWarnings("serial")
@Entity
public class ShoppingCart extends CrudModel<ShoppingCart> {

    public static final ModelFinder find = new ModelFinder();

    @OneToOne
    private User user;
    
	@OneToMany(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@PrivateOwned
	private List<ShoppingCartEntry> entries = new ArrayList<ShoppingCartEntry>();

	public double getVersandkosten() {
		return 3.59;
	}
	
	public double getPriceNetto() {
		double p = 0;
		for (ShoppingCartEntry entry : entries) {
			p+=entry.getPrice();
		}
		return p;
	}
	
	public double getMwst() {
		return getPriceNetto()*0.19;
	}
	
	public double getPriceBrutto() {
		return getPriceNetto()+getVersandkosten()+getMwst();
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<ShoppingCartEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<ShoppingCartEntry> entries) {
		this.entries = entries;
	}

	@Override
	public String getLabel() {
		return user.getLabel();
	}
	
	@Override
	public CrudFinder<ShoppingCart> getCrudFinder() {
		return find;
	}
    
	public static class ModelFinder extends CrudFinder<ShoppingCart> {
		
		public ModelFinder() {
			super(new Finder<Long, ShoppingCart>(Long.class, ShoppingCart.class),"user.name");
		}

	}

}
