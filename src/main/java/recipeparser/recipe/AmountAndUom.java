package recipeparser.recipe;

/**
 * Stores amount and uom strings seperately after split
 * 
 * @author NicholasChristou
 *
 */
public class AmountAndUom {
	private String amount;
	private String uom;
	
	public AmountAndUom(String amount, String uom) 
	{
		this.amount = amount;
		this.uom = uom;
	}
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getUom() {
		return uom;
	}
	public void setUom(String uom) {
		this.uom = uom;
	}
}
