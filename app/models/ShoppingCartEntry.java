package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@SuppressWarnings("serial")
@Entity
public class ShoppingCartEntry extends CrudModel<ShoppingCartEntry> {

    public static final ModelFinder find = new ModelFinder();

	@ManyToOne
	private Ding ding;
	
	private int amount;
	
	public double getPrice() {
		return ding.getPrice().doubleValue()*amount;
	}
	
	public Ding getDing() {
		return ding;
	}

	public void setDing(Ding ding) {
		this.ding = ding;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String getLabel() {
		return ding.getLabel();
	}
	
	@Override
	public CrudFinder<ShoppingCartEntry> getCrudFinder() {
		return find;
	}
    
	public static class ModelFinder extends CrudFinder<ShoppingCartEntry> {
		
		public ModelFinder() {
			super(new Finder<Long, ShoppingCartEntry>(Long.class, ShoppingCartEntry.class),"ding.name");
		}

	}

}
