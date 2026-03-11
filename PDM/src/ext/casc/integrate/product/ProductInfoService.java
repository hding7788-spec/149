package ext.casc.integrate.product;

import ext.casc.integrate.util.BomUtil;
import ext.casc.integrate.util.Constants;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pdmlink.PDMLinkProduct;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

public class ProductInfoService implements IProductInfo {

	@Override
	public String getBomProductInfo(String number, String versionType,
			String version, String productName, int ismark, String bomType,
			String guid) throws WTException {
		System.out.println("getBomProductInfo()  number:"+number +" versionType:"
			+ " version:"+version+" productName:"+productName+" ismark:"+ismark+" bomType:"+bomType+" guid:"+guid);
		// TODO Auto-generated method stub
		//设置管理员权限
		wt.session.SessionHelper.manager.setAdministrator();
		StringBuffer productInfo = new StringBuffer();
		productInfo.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		productInfo.append("<productinfo>");
		//验证图号有效性
		WTPart part = (WTPart)WCUtil.getPartByNumber(number);
		if(part==null){
			productInfo.append("<exception>");
			productInfo.append("所查找的编号在PDM中不存在!");
			 String check = BomUtil.checkNum(number);
			 if(!check.equals(""))
				 productInfo.append("相似编号如下：").append(check);
			 productInfo.append("</exception>");
			 productInfo.append("</productinfo>");
			 return productInfo.toString();
		}
		 WTPartMaster partMaster = (WTPartMaster)part.getMaster();
		 WTPart part1 = null;
		 if(version != null && !"".equals(version)){
			 part1 = (WTPart)BomUtil.getLatestPartByBatchView(partMaster, version,bomType);//获取指定定视图的最新版本部件
		 }
		 else{
			 part1  = (WTPart)BomUtil.getLatestPartByView(partMaster,bomType);
		 }
		 if(part1==null){//PDM中不存在指定批次的BOM结构
			 productInfo.append("<exception>");
			 productInfo.append("PDM中不存在指定批次或").append(bomType).append("视图的BOM结构!");
			 productInfo.append("</exception>");
			 productInfo.append("</productinfo>");
			 return productInfo.toString();
		 }

		List<WTPart> allChild = new ArrayList<WTPart>();
		//迭代获取BOM结构所有图号信息
		IBAUtility utility = new IBAUtility(part1);
		String partType = utility.getIBAValue("MTYPE");
		if(Constants.TYPE_ZIZHIJIAN.equals(partType)
				||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
				||Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)
				||Constants.TYPE_WAIXIEJIAN.equals(partType)
				||Constants.TYPE_WAIPEITAOJIAN.equals(partType)){
			productInfo.append(createInfoXML(part1,part));
		}
		productInfo.append(getInfoByBatchView(part1,bomType,version,allChild));
		productInfo.append("</productinfo>");

		String product = productInfo.toString();
		product = product.replaceAll(":", "/colon");

		return product;
	}

	 //根据顶层图号编码获取所有子节点为自制件、外配套、带料委外件、不带料委外件的列表
	 public static String getInfoByBatchView(WTPart part,String bomType,String batchVersion,List<WTPart> allChild){
		 QueryResult list = null;
		 StringBuffer buffer = new StringBuffer();
	    	try {
	    		list = WTPartHelper.service.getUsesWTPartMasters(part);
	    		while(list.hasMoreElements()){
	    			WTPartUsageLink link = (WTPartUsageLink)list.nextElement();
	    			WTPartMaster part1 = (WTPartMaster)link.getRoleBObject();
					WTPart  latePart = null;
					if(batchVersion != null && !"".equals(batchVersion)){
						latePart =BomUtil.getLatestPartByBatchView(part1, batchVersion, bomType); //获得与父部件相同视图的子部件
					}
	    			else{
						latePart = (WTPart)BomUtil.getLatestPartByView(part1,bomType);
					}
					if(latePart==null){
						continue;
					}
	    			IBAUtility utility = new IBAUtility(latePart);
	    			String partType = utility.getIBAValue("MTYPE");//子件类型
	    			String lateversion = latePart.getIterationDisplayIdentifier().toString();//子件版本
	    			if(partType!=null&&!partType.equals("")){
	    			//	if(partType.equals(Constants.TYPE_ZIZHIJIAN)||partType.equals(Constants.TYPE_WAIPEITAOJIAN)||partType.equals(Constants.TYPE_DAILIAOWEIWAIJIAN)||partType.equals(Constants.TYPE_BUDAILIAOWEIWAIJIAN)){
	    				if(Constants.TYPE_ZIZHIJIAN.equals(partType)
	    						||Constants.TYPE_WAIPEITAOJIAN.equals(partType)
	    						||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
	    						||Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)){
	    				/*判断子列表中是否有重复节点*/
	    					if(!allChild.contains(latePart)){
	    						allChild.add(latePart);
	    						buffer.append(createInfoXML(latePart,part));
	    					}
	    				}
	    			}
	    			buffer.append(getInfoByBatchView(latePart,bomType,batchVersion,allChild));//迭代获取所有的物料子节点

	    		}
	  		} catch (WTException e) {
	  			// TODO Auto-generated catch blo
	  			e.printStackTrace();
	  		}
	    	return buffer.toString();

	 }

	 //组装单个图号信息
	 public static String createInfoXML(WTPart part,WTPart part2){
		 StringBuffer info = new StringBuffer();
		 String name = "";//名称
		 String number = "";//编号
		 String phase = "";//研制阶段
		 String cpdh = "";//产品代号
		 String product = "";//所属型号
		 String version = "";//版本
		 String partType = "";//零件类型
		 String keyComponent = "";//关重件标识
		 String enditem = "";//成套件标识
		 String borrowitem = "";//借用件标识
		 String defaulttracecode = "";//默认追踪代码
		 String phantom = "";//虚拟制造部件
		 String assemblyMode = "";//装配模式
		 String defaultUnit = "";//默认单位
		 String dstate = "";//状态
		 String dcompany = "";//研制单位
		 String ddesigner = "";//设计者
		 String BATCH = "";//批次号
		 name = part.getName();
		 number = part.getNumber();
		 version = part.getIterationDisplayIdentifier().toString();//版本
		 defaulttracecode = part.getDefaultTraceCode().getDisplay();
		 defaultUnit = part.getDefaultUnit().getDisplay();
		 IBAUtility utility;
		 IBAUtility utility1;
		 WTContainer container = part.getContainer();
		 PDMLinkProduct linkProduct = null;
		 if(container instanceof PDMLinkProduct){
			 linkProduct = (PDMLinkProduct) container;
		 }
		try {
			if(linkProduct!=null){
				utility1 = new IBAUtility(linkProduct);
				if (utility1.getIBAValue("UNMINDEX") != null) {
					product = utility1.getIBAValue("UNMINDEX");
					cpdh = utility1.getIBAValue("UNMINDEX");
				}
			}
		} catch (WTException e1) {
			e1.printStackTrace();
		}
		try {
			utility = new IBAUtility(part);
			phase = utility.getIBAValue("PHASE_CODE");
			//if(utility.getIBAValue("PINDEX")!=null)
				//cpdh = utility.getIBAValue("PINDEX");
			if(cpdh == null || "".equals(cpdh))
				cpdh = part.getContainerName();
			if("".equals(product) && utility.getIBAValue("MINDEX")!=null)
				product = utility.getIBAValue("MINDEX");
			if(utility.getIBAValue("MTYPE")!=null)
				partType =  utility.getIBAValue("MTYPE");
			if(utility.getIBAValue("KEYCOMPONENT")!=null)
				keyComponent = utility.getIBAValue("KEYCOMPONENT");
			if(utility.getIBAValue("SETMARK")!=null)
				enditem = utility.getIBAValue("SETMARK");
			//borrowitem = utility.getIBAValue("");
			//phantom = utility.getIBAValue("");
			if(utility.getIBAValue("COMPANY")!=null)
				dcompany = utility.getIBAValue("COMPANY");
			if(utility.getIBAValue("DESIGNER")!=null)
				ddesigner = utility.getIBAValue("DESIGNER");

			if(utility.getIBAValue("BATCH")!=null)
				BATCH = utility.getIBAValue("BATCH");
			if(BATCH==null||"null".equals(BATCH)||"".equals(BATCH)){
				IBAUtility ibaUtility = new IBAUtility(part2);
				String batch = ibaUtility.getIBAValue("BATCH");
				if(batch!=null && !"".equals(batch)){
					BATCH = batch;
				}
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 info.append("<product>");

		 info.append("<parentnumber>");
		 if(!part.getNumber().equals(part2.getNumber())){
			 info.append(part2.getNumber());
		 }
		 info.append("</parentnumber>");
		 info.append("<cpdh>");
		 info.append(cpdh);
		 info.append("</cpdh>");
		 info.append("<name>");
		 info.append(name);
		 info.append("</name>");
		 info.append("<number>");
		 info.append(number);
		 info.append("</number>");
		 info.append("<phase>");
		 info.append(phase);
		 info.append("</phase>");
		 info.append("<ssxh>");
		 //cjh 型号涉密改造
		 info.append(product);
		 info.append("</ssxh>");
		 info.append("<version>");
		 info.append(version);
		 info.append("</version>");
		 info.append("<partType>");
		 info.append(partType);
		 info.append("</partType>");
		 info.append("<keyComponent>");
		 info.append(keyComponent);
		 info.append("</keyComponent>");
		 info.append("<enditem>");
		 info.append(enditem);
		 info.append("</enditem>");
		 info.append("<borrowitem>");
		 info.append(borrowitem);
		 info.append("</borrowitem>");
		 info.append("<defaulttracecode>");
		 info.append(defaulttracecode);
		 info.append("</defaulttracecode>");
		 info.append("<phantom>");
		 info.append(phantom);
		 info.append("</phantom>");
		 info.append("<assemblyMode>");
		 info.append(assemblyMode);
		 info.append("</assemblyMode>");
		 info.append("<defaultUnit>");
		 info.append(defaultUnit);
		 info.append("</defaultUnit>");
		 info.append("<dstate>");
		 info.append(dstate);
		 info.append("</dstate>");
		 info.append("<dcompany>");
		 info.append(dcompany);
		 info.append("</dcompany>");
		 info.append("<ddesigner>");
		 info.append(ddesigner);
		 info.append("</ddesigner>");
		 info.append("<batch>");
		 info.append(BATCH);
		 info.append("</batch>");
		 info.append("<modifierFullName>");
		 info.append(part.getModifierFullName());
		 info.append("</modifierFullName>");
		 info.append("<creatorFullName>");
		 info.append(part.getCreatorFullName());
		 info.append("</creatorFullName>");
		 info.append("<modifyTimestamp>");
		 info.append(part.getModifyTimestamp().getTime());
		 info.append("</modifyTimestamp>");
		 info.append("<createTimestamp>");
		 info.append(part.getCreateTimestamp().getTime());
		 info.append("</createTimestamp>");
		 info.append("</product>");
		 return info.toString();
	 }
}
