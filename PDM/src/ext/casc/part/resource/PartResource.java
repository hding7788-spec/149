package ext.casc.part.resource;

import wt.util.resource.WTListResourceBundle;

import wt.util.resource.RBEntry;
import wt.util.resource.RBPseudo;
import wt.util.resource.RBUUID;

@RBUUID("ext.casc.part.resource.PartResource")
public class PartResource extends WTListResourceBundle{
	@RBEntry("Bom Delete")
	public static final String PRIVATE_CONSTANT_01 = "object.bomDelete.description";
	@RBEntry("Bom Delete")
	public static final String PRIVATE_CONSTANT_02 = "object.bomDelete.title";
	@RBEntry("Bom Delete")
	public static final String PRIVATE_CONSTANT_03 = "object.bomDelete.tooltip";
	@RBEntry("netmarkets/images/delete.png")
	@RBPseudo(false)
	public static final String PRIVATE_CONSTANT_04 = "object.bomDelete.icon";
	
	@RBEntry("Whether to delete the BOM ?")
    public static final String WHETHER_DELETE_BOM = "WHETHER_DELETE_BOM";
	
	@RBEntry("删除失败！")
    public static final String EXECUTE_FAILURE = "EXECUTE_FAILURE";
	
	@RBEntry("删除成功！")
    public static final String EXECUTE_SUCCESS = "EXECUTE_SUCCESS";
	

}