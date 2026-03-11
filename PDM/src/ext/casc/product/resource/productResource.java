package ext.casc.product.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBNameException;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.casc.product.resource.productResource")
@RBNameException
// Grandfathered by conversion
public class productResource extends WTListResourceBundle {

	@RBEntry("EBOM Data Import")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_1 = "custom.productstructure.description";

	@RBEntry("EBOM Data Import")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_2 = "custom.productstructure.title";

	@RBEntry("CADdocument")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_3 = "custom.CADdocument.description";

	@RBEntry("CADdocumenttitle")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_4 = "custom.CADdocument.title";

	@RBEntry("document")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_5 = "custom.document.description";

	@RBEntry("documenttitle")
	@RBComment("Used as the text for the label of Product's importdata action")
	public static final String PRIVATE_CONSTANT_6 = "custom.document.title";

	@RBEntry("importdata")
	@RBComment("Label of submenu containing actions for creating various types of objects")
	public static final String PRIVATE_CONSTANT_7 = "object.sub_importdata.description";

	@RBEntry("importdatatitle")
	@RBComment("Label of submenu containing actions for creating various types of objects")
	public static final String PRIVATE_CONSTANT_8 = "object.sub_importdata.title";
	
	@RBEntry("新建产品")
	public static final String CUSTOMPRODUCT_CUSTOMCREATEPRODUCTWIZARD_1= "customProduct.customCreateProduct.title";
	@RBEntry("新建产品")
	public static final String CUSTOMPRODUCT_CUSTOMCREATEPRODUCTWIZARD_2= "customProduct.customCreateProduct.tooltip";
	@RBEntry("新建产品")
	public static final String CUSTOMPRODUCT_CUSTOMCREATEPRODUCTWIZARD_3= "customProduct.customCreateProduct.description";
	@RBEntry("prodcontext_create.gif")
	public static final String CUSTOMPRODUCT_CUSTOMCREATEPRODUCTWIZARD_4 = "customProduct.customCreateProduct.icon";

}
