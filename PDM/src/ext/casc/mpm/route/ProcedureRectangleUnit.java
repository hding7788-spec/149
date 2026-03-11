package ext.casc.mpm.route;

public class ProcedureRectangleUnit {
	private String id;
    private int x, y, width, height, number;
    private String name, workShop, type, description;
    private boolean isKey;
    private String preProcedureID, nextProcedureID;

    public ProcedureRectangleUnit(int x, int y, int width, int height, int number, String name, String workShop, String type, String description, boolean isKey, String preProcedureID, String nextProcedureID) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.number = number;
        this.name = name;
        this.workShop = workShop;
        this.type = type;
        this.description = description;
        this.isKey = isKey;
        this.preProcedureID = preProcedureID;
        this.nextProcedureID = nextProcedureID;
    }

    public ProcedureRectangleUnit(){

    }

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWorkShop() {
		return workShop;
	}
	public void setWorkShop(String workShop) {
		this.workShop = workShop;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isKey() {
		return isKey;
	}
	public void setKey(boolean isKey) {
		this.isKey = isKey;
	}
	public String getPreProcedureID() {
		return preProcedureID;
	}
	public void setPreProcedureID(String preProcedureID) {
		this.preProcedureID = preProcedureID;
	}
	public String getNextProcedureID() {
		return nextProcedureID;
	}
	public void setNextProcedureID(String nextProcedureID) {
		this.nextProcedureID = nextProcedureID;
	}


}
