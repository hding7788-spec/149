package ext.casc.product.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBNameException;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.casc.product.resource.productResource")
@RBNameException
// Grandfathered by conversion
public class productResource_zh_CN extends WTListResourceBundle {

	@RBEntry("EBOM数据批量导入")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_1 = "custom.productstructure.description";

	@RBEntry("EBOM数据批量导入")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_2 = "custom.productstructure.title";

	@RBEntry("二维图纸数据导入")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_3 = "custom.CADdocument.description";

	@RBEntry("二维图纸数据导入标题")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_4 = "custom.CADdocument.title";

	@RBEntry("产品关联文档导入")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_5 = "custom.document.description";

	@RBEntry("文档数据导入标题")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_6 = "custom.document.title";

	@RBEntry("历史数据导入")
	@RBComment("Label of submenu containing actions for creating various types of objects")
	public static final String PRIVATE_CONSTANT_7 = "object.sub_importdata.description";

	@RBEntry("历史数据导入标题")
	@RBComment("Label of submenu containing actions for creating various types of objects")
	public static final String PRIVATE_CONSTANT_8 = "object.sub_importdata.title";

	@RBEntry("*请选择EXCEL表文件：")
	public static final String IMPORTDATA_EXCEL_TITLE = "1";

	@RBEntry("在执行导入前，请选择相应的SOP资源文件：")
	public static final String IMPORTDATA_NOTICE = "2";
	
	@RBEntry("新建产品")
	public static final String CUSTOMPRODUCT_CUSTOMCREATEPRODUCTWIZARD_1= "customProduct.customCreateProduct.title";
	@RBEntry("新建产品")
	public static final String CUSTOMPRODUCT_CUSTOMCREATEPRODUCTWIZARD_2= "customProduct.customCreateProduct.tooltip";
	@RBEntry("新建产品")
	public static final String CUSTOMPRODUCT_CUSTOMCREATEPRODUCTWIZARD_3= "customProduct.customCreateProduct.description";
	@RBEntry("prodcontext_create.gif")
	public static final String CUSTOMPRODUCT_CUSTOMCREATEPRODUCTWIZARD_4 = "customProduct.customCreateProduct.icon";

}
