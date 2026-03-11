package ext.casc.workflow.signtrue.zp;

public class RecordData {
	private String number = "";
	private String name= "";
	private String version= "";
	private String state= "";
	private String implement= "";
	private String cart= "";
	
	@Override
	public String toString() {
		 return "{number:'"+number+"',name:'" + name + "',version:'" + version + "',state:'"+state+"',implement:'"+implement+"',cart:'"+cart+"'}";
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getImplement() {
		return implement;
	}
	public void setImplement(String implement) {
		this.implement = implement;
	}
	public String getCart() {
		return cart;
	}
	public void setCart(String cart) {
		this.cart = cart;
	}
	
}
