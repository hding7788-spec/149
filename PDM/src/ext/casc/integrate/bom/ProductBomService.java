package ext.casc.integrate.bom;

import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.integrate.model.*;
import ext.casc.integrate.util.BomUtil;
import ext.casc.integrate.util.CldeUtil;
import ext.casc.integrate.util.Constants;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.Tools;
import ext.casc.util.WCUtil;
import org.dom4j.DocumentException;
import wt.content.*;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.definition._AttributeHierarchyChild;
import wt.iba.value._StringValue;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;

import java.beans.PropertyVetoException;
import java.io.*;
import java.util.*;

public class ProductBomService implements IProductBom {

	/**
	 * 元器件添加属性对应字段
	 */
	private String[] addAttribute = new String[] { "bmdj", "kfzbtid", "kfzbsee", "xncs", "jdmgdj_state", "jdmgdj", "smdj" };

	@Override
	public String getBomInfo(String number,String versionType,String version,int expansionLevel,
			String relatedType,String bomType,String fileTypeA,String fileTypeB,String fileTypeC,
			String productName,int ismark,String guid,String type) throws Exception {

		// TODO Auto-generated method stub
		String validMsg = "";
		//验证用户有效性
		if(!guid.equals("")){
			return validMsg;
		}
		//验证图号有效性
		WTPart part = (WTPart)WCUtil.getPartByNumber(number);
		if(part==null){
			validMsg = "number not found";
			return validMsg;
		}
		//获取BOM结构信息
		getBomStructure(number,versionType,version,expansionLevel,relatedType,bomType,fileTypeB,fileTypeC,guid, type,false);

		//验证取值有效性
		return null;
	}
	public String getBomStructure(String number, String versionType,
								  String version, int expansionLevel, String relatedType,
								  String bomType, String fileTypeB,String fileTypeC,String guid,String type,boolean is65,String instanceId) throws Exception {
		return  getBomStructure( number, versionType,
				 version,  expansionLevel, relatedType,
				 bomType,  fileTypeB, fileTypeC, guid, type, is65);
	}

	@Override
	public String getBomStructure(String number, String versionType,
			String version, int expansionLevel, String relatedType,
			String bomType, String fileTypeB,String fileTypeC,String guid,String type,boolean is65) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("------getBomStructure----------"
				+"-----number:"+number+",versionType:"+versionType+",version:"+version
				+",expansionLevel:"+expansionLevel+",relatedType:"+relatedType
				+",bomType:"+bomType+",fileTypeB:"+fileTypeB+",fileTypeC:"+fileTypeC
				+",guid:"+guid+",type:"+type+",is65:"+is65);
		//设置管理员权限
		if(version==null||"null".equals(version)){
			version="";
		}
		wt.session.SessionHelper.manager.setAdministrator();
		StringBuffer bom = new StringBuffer();
		bom.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bom.append("<bom>");
		//验证用户有效性
		if(!guid.equals("")){
			bom.append("<exception>");
			bom.append("用户身份验证不合格");
			bom.append("</exception>");
			bom.append("</bom>");
			return bom.toString();
		}
		//验证图号有效性
		WTPart part = (WTPart)WCUtil.getPartByNumber(number);
		if(part==null){
			bom.append("<exception>");
			bom.append("所查找的编号在PDM中不存在!");
			String check = BomUtil.checkNum(number);
			if(!check.equals(""))
				bom.append("相似编号如下：").append(check);
			bom.append("</exception>");
			bom.append("</bom>");
			return bom.toString();
		}

		//若版本类型为批次(BATCH),则根据批次获取BOM结构信息,包扩材料定额
		if(versionType.equals(Constants.LABEL_VERSION_BATCH)){
			if("1".equals(type)){
				return  getBomStructureXmlFromCache(part,fileTypeB,fileTypeC);
			}else if("0".equals(type)){
				GenERPXmlQueueHelper.createProcessingQueue(number,version,expansionLevel,relatedType,bomType,fileTypeB,fileTypeC,number,is65);
				bom.append("<message>");
				bom.append("成功");
				bom.append("</message>");
				bom.append("</bom>");
				return bom.toString();
			}else{
				bom.append(getBomStructureByBatch(number,version,expansionLevel,relatedType,bomType,fileTypeB,fileTypeC,is65));
			}
		}
		bom.append("</bom>");
		return bom.toString();
	}

	public static  void genERPXml(String number, String version, Integer expansionLevel, String relatedType, String bomType, String fileTypeB, String fileTypeC, String partNumber,Boolean is65) throws Exception {
		new ProductBomService().genXmlCache(number, version, expansionLevel, relatedType, bomType, fileTypeB, fileTypeC, partNumber,is65);
	}
	public  void genXmlCache(String number, String version, int expansionLevel, String relatedType, String bomType, String fileTypeB, String fileTypeC, String partNumber,Boolean is65) throws Exception {
		WTPart part = (WTPart)WCUtil.getPartByNumber(partNumber);

		StringBuffer bom = new StringBuffer();
		bom.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bom.append("<bom>");
		bom.append(getBomStructureByBatch(number,version,expansionLevel,relatedType,bomType,fileTypeB,fileTypeC,is65));
		bom.append("</bom>");
		genXmlCache(bom.toString(),part,fileTypeB,fileTypeC);

	}

	private void genXmlCache(String xml, WTPart part,String fileTypeB,String fileTypeC) throws WTException, FileNotFoundException, PropertyVetoException, IOException  {
        String fileName = part.getNumber()+"_"+fileTypeB+"_"+fileTypeC+"_NC.xml";


        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);

		ContentHolder holder = ContentHelper.service.getContents((ContentHolder) part);
        QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
        while (qr.hasMoreElements()) {
            Object objQr = qr.nextElement();
            if (objQr instanceof ApplicationData) {
           	 ApplicationData ad = (ApplicationData) objQr;
                String adName = ad.getFileName();
                if(adName!=null&&adName.contains(fileTypeB+"_"+fileTypeC+"_NC.xml")){
                	 ContentServerHelper.service.deleteContent(holder, ad);
                }
            }
        }
        Transaction tx = new Transaction();
        tx.start();
        PersistenceHelper.manager.lockAndRefresh(part);
		 ApplicationData appData = ApplicationData.newApplicationData(part);
	     appData.setRole(ContentRoleType.SECONDARY);
	     appData.setFileName(fileName);
	    // ContentServerHelper.service.updateContent(part, appData, filePath+ File.separator+fileName);
	     ContentServerHelper.service.updateContent(part, appData,new ByteArrayInputStream(xml.getBytes()) );

	     tx.commit();
         tx = null;
         PersistenceHelper.manager.refresh(part);
         SessionServerHelper.manager.setAccessEnforced(enforce);
	}

	private String getBomStructureXmlFromCache(WTPart part, String fileTypeB, String fileTypeC) throws Exception {
		 ContentHolder holder = ContentHelper.service.getContents((ContentHolder) part);
         QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
         while (qr.hasMoreElements()) {
             Object objQr = qr.nextElement();
             if (objQr instanceof ApplicationData) {
            	 ApplicationData ad = (ApplicationData) objQr;
                 String adName = ad.getFileName();
                 if(adName!=null&&adName.contains(fileTypeB+"_"+fileTypeC+"_NC.xml")){
                	String trueFileName = "";
                	FileOutputStream fos = null;
                	try{
	             		String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp";
	                 	File fileDir = new File(filePath);
	                 	if(!fileDir.exists()){
	                 		fileDir.mkdirs();
	                 	}
	                	 byte[] bytes = WTDocumentUtil.applicationDataToByte(ad);
	                	 trueFileName =System.currentTimeMillis() + ".xml";
	                	 fos = new FileOutputStream(fileDir + File.separator + trueFileName);
	                	 fos.write(bytes);
	                	 fos.flush();
	                	 StringBuffer bom = new StringBuffer();
	              		bom.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	              		bom.append("<bom>");
              			bom.append("<fileName>");
              			bom.append(trueFileName);
              			bom.append("</fileName>");
              			bom.append("</bom>");
	              		return bom.toString();
                	}finally{
                		if(fos != null){
        					try {
        						fos.close();
        					} catch (IOException e) {
        						e.printStackTrace();
        					}
        				}
                	}


                 }
             }
         }
         StringBuffer bom = new StringBuffer();
 		bom.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
 		bom.append("<bom>");
 			bom.append("<exception>");
 			bom.append("不存在缓存xml文件,请按照要求请求生成缓存数据或等缓存数据生成后再获取！");
 			bom.append("</exception>");
 			bom.append("</bom>");
 			return bom.toString();
	}

	//默认获取批次下的最新版本BOM结构
	public String getBomStructureByBatch(String number,String batchversion, int expansionLevel, String relatedType,
			String bomType, String fileTypeB,String fileTypeC,boolean is65) throws Exception {
		 StringBuffer allBom = new StringBuffer();
		 WTPart part1 = (WTPart)WCUtil.getPartByNumber(number);
		 WTPartMaster partMaster = (WTPartMaster)part1.getMaster();
		 WTPart part = null;
		 if(fileTypeB.equals("FORMAL")){
			 part = (WTPart)BomUtil.getLatestPartByBatchView(partMaster, batchversion,bomType);//获取指定定视图的最新版本部件
		 }
		 else if(!is65&&fileTypeB.equals("TEMP")){//如果不是NC65
			 part = (WTPart)BomUtil.getLatestPartByView(partMaster,bomType);
		 }else if(is65&&fileTypeB.equals("TEMP")){//如果是NC65
			 part = (WTPart)BomUtil.getLatestPartByBatchView(partMaster, batchversion,bomType);//获取指定定视图的最新版本部件
		 }
		 if(part==null){//PDM中不存在指定批次的BOM结构
			 allBom.append("<exception>");
			 allBom.append("PDM中不存在指定批次或").append(bomType).append("视图的BOM结构!");
			 allBom.append("</exception>");
			 return allBom.toString();
		 }
		 IBAUtility utility = new IBAUtility(part);
		 String name = part.getName();//零部件名称
		 String partType = utility.getIBAValue("MTYPE");//零部件类型
		 String phase = utility.getIBAValue("PHASE_CODE");//研制阶段
		 String BATCH = utility.getIBAValue("BATCH");//批次号
		 String czzcj = utility.getIBAValue("ZZCJ");
		 String SETMARK = utility.getIBAValue("SETMARK");

		if(BATCH==null||"null".equals(BATCH)){
				BATCH= "";
			}
		 String version = part.getIterationDisplayIdentifier().toString();//版本

		 if(relatedType.equals("ALL")){
			 //初使化顶层节点
			 Map<String,String> extAttrs = new LinkedHashMap<String, String>();
			 extAttrs.put("childFactory",czzcj);
			 extAttrs.put("SETMARK",SETMARK);
			 allBom.append(createLinkXML("","","","","",number,name,version,partType,phase,"","","","","","","","","","","","","","","",BATCH,"","","","",extAttrs));
			 //获取子结构BOM信息
			 allBom.append(getAllBOMData2(part,number,batchversion,fileTypeB,fileTypeC,is65));//遍历获取所有子结构信息
		 }


		 return allBom.toString();
	}

	//获取BOM所有节点(包括物料)关系属性
	private String getAllBOMData2(WTPart parentPart,String number,String batchversion,String fileTypeB,String fileTypeC,Boolean is65) throws WTException {
		// TODO Auto-generated method stub
		System.out.println("------getAllBOMData2---number---"+number);
		StringBuffer bomData = new StringBuffer();//初始化容器

		IBAUtility parentUtility = new IBAUtility(parentPart);
		String parentPhase = parentUtility.getIBAValue("PHASE_CODE");//上级图号研制阶段
		String parentType = parentUtility.getIBAValue("MTYPE");
		String parentBATCH = parentUtility.getIBAValue("BATCH");//批次号
		String parentSETMARK = parentUtility.getIBAValue("SETMARK");
		String parentzzcj = parentUtility.getIBAValue("ZZCJ");
		//String parentfzcj = parentUtility.getIBAValue("FZCJ");
		if(parentBATCH==null||"null".equals(parentBATCH)){
			parentBATCH= "";
		}
		//String number = part.getNumber();//上级图号
		String parentName = parentPart.getName();//上级名称
		String view = parentPart.getViewName();
		String parentversion = parentPart.getIterationDisplayIdentifier().toString();//版本
    	QueryResult list = null;

    	try {

    		List<WTDocument>  documentList = ERPBomHelper.getAllApprovedTechnicsDocumentByPart(parentPart,fileTypeB,fileTypeC);
			List<GLErpPbomPartBean> erpMatchPartList =null;
			List<GLErpPbomPartBean>  erpNewPartList = null;
			List<GLErpPeiTaoPartBean>  erpPeiTaoPartBeans = null;
			List<GLErpMaterialsBean> materialsBeanList = new ArrayList<GLErpMaterialsBean>();
			List<GLErpElement> elementList = null;
			ERPCacheBean cacheBean = null;
			Transaction trx = new Transaction();
			try {
				trx.start();
				//缓存的工艺定额
				cacheBean =  ERPCacheHelper.getErpCacheList(documentList,fileTypeB);
				//未缓存的工艺定额新增
				elementList = ERPBomHelper.getElementList(cacheBean.getDocumentList());

				//未缓存的工艺定额匹配
				erpMatchPartList = ERPBomHelper.getPBOMMatchInfo(parentPart,elementList);
				//保存未缓存的工艺定额匹配
				ERPCacheHelper.saveErpMatchPartCache(erpMatchPartList);

				erpMatchPartList.addAll(cacheBean.getErpMatchPartList());//合并

				if(Constants.TYPE_ZIZHIJIAN.equals(parentType)||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(parentType)||Constants.TYPE_WAIPEITAOJIAN.equals(parentType)){
					// 未缓存的材料定额
					materialsBeanList =  ERPBomHelper.getCldeInfo(elementList);
					//保存未缓存的材料定额
					ERPCacheHelper.saveMaterialCache(materialsBeanList);

					materialsBeanList.addAll(cacheBean.getMaterialBeanList());//合并

				}
				// 未缓存的工艺定额
				erpNewPartList = ERPBomHelper.getPBOMNewPartInfo(parentPart,elementList);
				//保存未缓存的工艺定额新增
				ERPCacheHelper.saveErpNewPartCache(erpNewPartList);
				erpNewPartList.addAll(cacheBean.getErpNewPartList());//合并

				if("TEMP".equals(fileTypeB)) {
					erpPeiTaoPartBeans = ERPBomHelper.getPeiTaoPbomInfo(parentPart,elementList,batchversion);
					ERPCacheHelper.savePeiTaoPbomCache(erpPeiTaoPartBeans);
					erpPeiTaoPartBeans.addAll(cacheBean.getErpPeiTaoPartList());//合并
				}

				trx.commit();
				trx = null;

			} catch (Exception e) {
				e.printStackTrace();
				elementList = ERPBomHelper.getElementList(documentList);

				erpMatchPartList = ERPBomHelper.getPBOMMatchInfo(parentPart,elementList);
				if(Constants.TYPE_ZIZHIJIAN.equals(parentType)||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(parentType)||Constants.TYPE_WAIPEITAOJIAN.equals(parentType)){
					materialsBeanList = ERPBomHelper.getCldeInfo(elementList);
				}
				erpNewPartList = ERPBomHelper.getPBOMNewPartInfo(parentPart,elementList);

				if("TEMP".equals(fileTypeB)) {
					erpPeiTaoPartBeans = ERPBomHelper.getPeiTaoPbomInfo(parentPart,elementList,batchversion);
				}
			}finally {
				if(trx!=null)  trx.rollback();
			}

			//材料定额
			if(!materialsBeanList.isEmpty()){
				bomData.append(ERPBomHelper.getYclInfo(parentPart,materialsBeanList));
			}

    		List<String> addAttrList = null;
    		if(erpNewPartList != null && !erpNewPartList.isEmpty()) {
    			int index = 0;
    			for (GLErpPbomPartBean bean : erpNewPartList) {
    				index ++;
    				addAttrList = getAddAttrList(bean);
    				String childNumber = bean.getChbm();
    				String name = bean.getChmc();
    				String childVersion = "space.1";
    				String matemount = "1";
    				if(bean.getSl() != null && !"".equals(bean.getSl())) {
    					matemount =bean.getSl();
    				}
    				String unit = bean.getDw();
    				String dataFrom = bean.getDataFrom();
    				//生成物资使用信息

					Map<String,String> extAttrs = new LinkedHashMap<String, String>();
					extAttrs.put("parentFactory",parentzzcj);
					extAttrs.put("childFactory","");
					extAttrs.put("dept",bean.getDept());
					extAttrs.put("SETMARK",parentSETMARK);

					bomData.append(createLinkXML2(number, parentName, parentversion, parentType, parentPhase, childNumber, name, childVersion, "外购件", "", matemount, unit, "", bean.getXlcc(), bean.getKzjs(), "", "", "", matemount,
							bean.getTechNumber(), bean.getTechName(), bean.getVersion(), dataFrom, unit, "", parentBATCH, index + "", bean.getPplanNumber(), bean.getPplanType(), bean.getZfflag(),
							addAttrList,extAttrs));

				}
    		}

    		if(erpMatchPartList != null) {
				int index = 0;
				for(GLErpPbomPartBean bean: erpMatchPartList) {
					index ++;
					if(bean != null) {
						String matemount = "1";
						String unit = "";
						addAttrList = getAddAttrList(bean);
						String childNumber = bean.getChbm();//获取PDM与ERP集成得到的物资存货编码
						if(bean.getGyCount() != null && !"".equals(bean.getGyCount())) {
							matemount =bean.getGyCount();
						}
						unit = bean.getDw();
						String dataFrom = bean.getDataFrom();
						String name = bean.getChmc();

						Map<String,String> extAttrs = new LinkedHashMap<String, String>();
						extAttrs.put("parentFactory",parentzzcj);
						extAttrs.put("childFactory","");

						bomData.append(createLinkXML2(number, parentName, parentversion, parentType, parentPhase, childNumber, name, "", "外购件", "", matemount, unit, "",
								bean.getXlcc(), bean.getKzjs(), "", "", "", matemount, bean.getTechNumber(), bean.getTechName(), bean.getVersion(), dataFrom, "", "", parentBATCH, index + "", bean.getPplanNumber(),
								bean.getPplanType(), bean.getZfflag(), addAttrList,extAttrs));
					}
				}
			}

			if("TEMP".equals(fileTypeB)) {
				int index = 0;
				for(GLErpPeiTaoPartBean peiTaoPartBean:erpPeiTaoPartBeans){
					index ++;

					Map<String,String> extAttrs = new LinkedHashMap<String, String>();
					extAttrs.put("parentFactory",parentzzcj);
					extAttrs.put("childFactory",peiTaoPartBean.getZzcj());
					extAttrs.put("SETMARK",peiTaoPartBean.getSetmark());
					bomData.append(createLinkXML2(number, parentName, parentversion, parentType, parentPhase, peiTaoPartBean.getChildNumber(), peiTaoPartBean.getChildName(), peiTaoPartBean.getChildVersion(), peiTaoPartBean.getPartType(), peiTaoPartBean.getChildPhase(), peiTaoPartBean.getAmount(), peiTaoPartBean.getUnit(), "", "","", "", "", "", peiTaoPartBean.getAmount(),
							peiTaoPartBean.getTechNumber(), peiTaoPartBean.getTechName(), peiTaoPartBean.getVersion(), "配套表", peiTaoPartBean.getUnit(), "", peiTaoPartBean.getBatch(), index + "", peiTaoPartBean.getPplanNumber(), peiTaoPartBean.getPplanType(), peiTaoPartBean.getZfflag(),
							addAttrList,extAttrs));
				}

			}else{
				list = WTPartHelper.service.getUsesWTPartMasters(parentPart);

				while(list.hasMoreElements()){

					WTPartUsageLink link = (WTPartUsageLink)list.nextElement();
					WTPartMaster part1 = (WTPartMaster)link.getRoleBObject();
					System.out.println("------part1---"+part1.getNumber());
					WTPart  latePart = null;

					if(fileTypeB.equals("FORMAL")){
						latePart = BomUtil.getLatestPartByBatchView(part1, batchversion, view);
					}
					else if(!is65&&fileTypeB.equals("TEMP")){
						latePart = BomUtil.getLatestPartByView(part1, view);
					}else if(is65&&fileTypeB.equals("TEMP")){
						latePart = BomUtil.getLatestPartByBatchView(part1, batchversion, view);
					}

					if(latePart == null) {
						System.out.println("part number is:"+part1.getNumber()+" is not exsit for batch:" + batchversion);
						continue;
					}

					IBAUtility ibaUtility = new IBAUtility(latePart);

					String partType = ibaUtility.getIBAValue("MTYPE");
					if(partType==null||"".equals(partType)){
						partType = ibaUtility.getIBAValue("CTYPE");
					}

					if(partType!=null&&!partType.equals("")){
						String SETMARK = ibaUtility.getIBAValue("SETMARK");
						String childNumber = "";
						childNumber = latePart.getNumber();
						String childName = latePart.getName();
						String BATCH = ibaUtility.getIBAValue("BATCH");
						if(BATCH==null||"null".equals(BATCH)){
							BATCH= "";
						}
						String childzzcj = ibaUtility.getIBAValue("ZZCJ");
						String unit = link.getQuantity().getUnit().getDisplay(Locale.CHINA);
						String childVersion = latePart.getIterationDisplayIdentifier().toString();

						if(isMateria(partType)){

						}else if(Constants.TYPE_ZIZHIJIAN.equals(partType)
								|| Constants.TYPE_DAILIAOWEIWAIJIAN.equals(partType)
								|| Constants.TYPE_BUDAILIAOWEIWAIJIAN.equals(partType)
								|| Constants.TYPE_WAIXIEJIAN.equals(partType)
								|| Constants.TYPE_WAIPEITAOJIAN.equals(partType)){
							Map<String,String> extAttrs = new LinkedHashMap<String, String>();
							extAttrs.put("parentFactory",parentzzcj);
							extAttrs.put("childFactory",childzzcj);
							extAttrs.put("SETMARK",SETMARK);
							String amount = link.getQuantity().getAmount()+"";
							IBAUtility linkIBAUtility = new IBAUtility(link);
							//取工艺数量值
							String gysl = linkIBAUtility.getIBAValue("GYSL");
							if(gysl != null && !"".equals(gysl)) {
								amount = gysl;
							}
							String childPhase = ibaUtility.getIBAValue("PHASE_CODE");//研制阶段


							bomData.append(createLinkXML(number,parentName,parentversion,parentType,parentPhase,childNumber,childName,childVersion,partType,
									childPhase,amount,unit,"","","","","","",amount,"","","","","个","",BATCH,"","","","",extAttrs));


						}
						if(!"TEMP".equals(fileTypeB) && !"是".equals(SETMARK)){
//						if( !"是".equals(SETMARK)){
							bomData.append(getAllBOMData2(latePart,childNumber,batchversion,fileTypeB,fileTypeC,is65));//迭代获取所有的物料子节点
						}
					}
				}
			}




  		} catch (WTException e) {
  			e.printStackTrace();
  		}catch (DocumentException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

		return bomData.toString();
	}
	private List<String> getAddAttrList(GLErpPbomPartBean bean) {
		List<String> result = new ArrayList<String>();
		result.add(bean.getBmdj());
		result.add(bean.getKfzbtid());
		result.add(bean.getKfzbsee());
		result.add(bean.getXncs());
		result.add(bean.getJdmgdj_state());
		result.add(bean.getJdmgdj());
		result.add(bean.getSmdj());

		return result;
	}


    //判断是否物料类型
    private boolean isMateria(String type){
    	for(int i=0;i<Constants.TYPE_MATERIALS.length;i++){
    		if(type.equals(Constants.TYPE_MATERIALS[i]))
    			return true;
    	}
    	return false;
    }



	//生成BOM结构信息XML(为单个关系)
	public static String createLinkXML(String parentNumber,String parentName, String parentVersion,String parentType,String parentPhase,String childNumber,
			String childName,String childVersion,String childType,String childPhase,String usingAmount,String unit,String cldelb,String xlcc,
			String kzjs,String sjsl,String sjcc,String sjkzjs,String sl,
			String processFileNum,String processFileName,String processFileVersion,String source, String dw, String comment,String BATCH,String index,String processFileNumber, String pplanType, String zfflag,Map<String,String> extAttrs ){
		if(Tools.isNull(usingAmount)){
			usingAmount = sl;
		}
		if(Tools.isNull(sl)){
			sl = usingAmount;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("<link>");
		buffer.append("<parentNumber>");
		buffer.append(parentNumber);
		buffer.append("</parentNumber>");
		buffer.append("<parentName>");
		buffer.append(parentName);
		buffer.append("</parentName>");
		buffer.append("<parentVersion>");
		buffer.append(parentVersion);
		buffer.append("</parentVersion>");
		buffer.append("<parentType>");
		buffer.append(parentType);
		buffer.append("</parentType>");
		buffer.append("<parentPhase>");
		buffer.append(parentPhase);
		buffer.append("</parentPhase>");

		buffer.append("<childNumber>");
		buffer.append(childNumber);
		buffer.append("</childNumber>");
		buffer.append("<childName>");
		if(childVersion==null||"".equals(childVersion)){//NC
			buffer.append("");
		}else{
			buffer.append(childName);
		}

		buffer.append("</childName>");
		buffer.append("<childVersion>");
		buffer.append(childVersion);
		buffer.append("</childVersion>");
		buffer.append("<childType>");
		buffer.append(childType);
		buffer.append("</childType>");
		buffer.append("<childPhase>");
		buffer.append(childPhase);
		buffer.append("</childPhase>");

		buffer.append("<usingAmount>");
		buffer.append(usingAmount==null? "":usingAmount);
		buffer.append("</usingAmount>");
		buffer.append("<unit>");
		buffer.append(dw==null? "":dw);
		buffer.append("</unit>");

		buffer.append("<batch>");
		buffer.append(BATCH==null? "":BATCH);
		buffer.append("</batch>");

		buffer.append("<cldelb>");
		buffer.append(cldelb==null? "":cldelb);
		buffer.append("</cldelb>");
		buffer.append("<xlcc>");
		buffer.append(xlcc==null? "":xlcc);
		buffer.append("</xlcc>");
		buffer.append("<kzjs>");
		buffer.append(kzjs==null? "":kzjs);
		buffer.append("</kzjs>");
		buffer.append("<sjsl>");
		buffer.append(sjsl==null? "":sjsl);
		buffer.append("</sjsl>");
		buffer.append("<sjcc>");
		buffer.append(sjcc==null? "":sjcc);
		buffer.append("</sjcc>");
		buffer.append("<sjkzjs>");
		buffer.append(sjkzjs==null? "":sjkzjs);
		buffer.append("</sjkzjs>");
		buffer.append("<sl>");
		buffer.append(sl==null? "":sl);
		buffer.append("</sl>");

		buffer.append("<unit2>");
		buffer.append(dw==null? "":dw);
		buffer.append("</unit2>");

		buffer.append("<comment>");
		buffer.append(comment);
		buffer.append("</comment>");

		buffer.append("<index>");
		buffer.append(index);
		buffer.append("</index>");

		buffer.append("<processFileNum>");
		buffer.append(processFileNum);
		buffer.append("</processFileNum>");
		buffer.append("<processFileName>");
		buffer.append(processFileName);
		buffer.append("</processFileName>");
		buffer.append("<processFileVersion>");
		buffer.append(processFileVersion);
		buffer.append("</processFileVersion>");

		buffer.append("<processFileNumber>");
		buffer.append(processFileNumber);
		buffer.append("</processFileNumber>");

		buffer.append("<pplanType>");
		buffer.append(pplanType);
		buffer.append("</pplanType>");

		buffer.append("<zfflag>");
		buffer.append(zfflag);
		buffer.append("</zfflag>");


		buffer.append("<source>");
		buffer.append(source);
		buffer.append("</source>");

		Set<Map.Entry<String,String>> entrySet = extAttrs.entrySet();
		for(Map.Entry<String,String> entry:entrySet){
			String key = entry.getKey();
			String value = entry.getValue();

			buffer.append("<").append(key).append(">");
			buffer.append(value);
			buffer.append("</").append(key).append(">");
		}

	    buffer.append("</link>");

		return buffer.toString();
	}

	public static String createLinkXML(String parentNumber,String parentName, String parentVersion,String parentType,String parentPhase,String childNumber,
									   String childName,String childVersion,String childType,String childPhase,String usingAmount,String unit,String cldelb,String xlcc,
									   String kzjs,String sjsl,String sjcc,String sjkzjs,String sl,
									   String processFileNum,String processFileName,String processFileVersion,String source, String dw, String comment,String BATCH,String index,String processFileNumber, String pplanType, String zfflag ){
		if(Tools.isNull(usingAmount)){
			usingAmount = sl;
		}
		if(Tools.isNull(sl)){
			sl = usingAmount;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("<link>");
		buffer.append("<parentNumber>");
		buffer.append(parentNumber);
		buffer.append("</parentNumber>");
		buffer.append("<parentName>");
		buffer.append(parentName);
		buffer.append("</parentName>");
		buffer.append("<parentVersion>");
		buffer.append(parentVersion);
		buffer.append("</parentVersion>");
		buffer.append("<parentType>");
		buffer.append(parentType);
		buffer.append("</parentType>");
		buffer.append("<parentPhase>");
		buffer.append(parentPhase);
		buffer.append("</parentPhase>");

		buffer.append("<childNumber>");
		buffer.append(childNumber);
		buffer.append("</childNumber>");
		buffer.append("<childName>");
		if(childVersion==null||"".equals(childVersion)){//NC
			buffer.append("");
		}else{
			buffer.append(childName);
		}

		buffer.append("</childName>");
		buffer.append("<childVersion>");
		buffer.append(childVersion);
		buffer.append("</childVersion>");
		buffer.append("<childType>");
		buffer.append(childType);
		buffer.append("</childType>");
		buffer.append("<childPhase>");
		buffer.append(childPhase);
		buffer.append("</childPhase>");

		buffer.append("<usingAmount>");
		buffer.append(usingAmount==null? "":usingAmount);
		buffer.append("</usingAmount>");
		buffer.append("<unit>");
		buffer.append(dw==null? "":dw);
		buffer.append("</unit>");

		buffer.append("<batch>");
		buffer.append(BATCH);
		buffer.append("</batch>");

		buffer.append("<cldelb>");
		buffer.append(cldelb);
		buffer.append("</cldelb>");
		buffer.append("<xlcc>");
		buffer.append(xlcc==null? "":xlcc);
		buffer.append("</xlcc>");
		buffer.append("<kzjs>");
		buffer.append(kzjs==null? "":kzjs);
		buffer.append("</kzjs>");
		buffer.append("<sjsl>");
		buffer.append(sjsl==null? "":sjsl);
		buffer.append("</sjsl>");
		buffer.append("<sjcc>");
		buffer.append(sjcc==null? "":sjcc);
		buffer.append("</sjcc>");
		buffer.append("<sjkzjs>");
		buffer.append(sjkzjs==null? "":sjkzjs);
		buffer.append("</sjkzjs>");
		buffer.append("<sl>");
		buffer.append(sl==null? "":sl);
		buffer.append("</sl>");

		buffer.append("<unit2>");
		buffer.append(dw==null? "":dw);
		buffer.append("</unit2>");

		buffer.append("<comment>");
		buffer.append(comment);
		buffer.append("</comment>");

		buffer.append("<index>");
		buffer.append(index);
		buffer.append("</index>");

		buffer.append("<processFileNum>");
		buffer.append(processFileNum);
		buffer.append("</processFileNum>");
		buffer.append("<processFileName>");
		buffer.append(processFileName);
		buffer.append("</processFileName>");
		buffer.append("<processFileVersion>");
		buffer.append(processFileVersion);
		buffer.append("</processFileVersion>");

		buffer.append("<processFileNumber>");
		buffer.append(processFileNumber);
		buffer.append("</processFileNumber>");

		buffer.append("<pplanType>");
		buffer.append(pplanType);
		buffer.append("</pplanType>");

		buffer.append("<zfflag>");
		buffer.append(zfflag);
		buffer.append("</zfflag>");


		buffer.append("<source>");
		buffer.append(source);
		buffer.append("</source>");


		buffer.append("</link>");

		return buffer.toString();
	}

	public String createLinkXML2(String parentNumber,String parentName, String parentVersion,String parentType,String parentPhase,String childNumber,
			String childName,String childVersion,String childType,String childPhase,String usingAmount,String unit,String cldelb,String xlcc,
			String kzjs,String sjsl,String sjcc,String sjkzjs,String sl,
			String processFileNum,String processFileName,String processFileVersion,String source, String dw, String comment,String BATCH,String index,String processFileNumber, String pplanType, String zfflag,List<String> addAttrList,Map<String,String> extAttrs){
		if(Tools.isNull(usingAmount)){
			usingAmount = sl;
		}
		if(Tools.isNull(sl)){
			sl = usingAmount;
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("<link>");

		buffer.append("<parentNumber>");
		buffer.append(parentNumber);
		buffer.append("</parentNumber>");

		buffer.append("<parentName>");
		buffer.append(parentName);
		buffer.append("</parentName>");

		buffer.append("<parentVersion>");
		buffer.append(parentVersion);
		buffer.append("</parentVersion>");

		buffer.append("<parentType>");
		buffer.append(parentType);
		buffer.append("</parentType>");

		buffer.append("<parentPhase>");
		buffer.append(parentPhase);
		buffer.append("</parentPhase>");

		buffer.append("<childNumber>");
		buffer.append(childNumber);
		buffer.append("</childNumber>");

		buffer.append("<childName>");
		if(childVersion==null||"".equals(childVersion)){//NC
			buffer.append("");
		}else{
			buffer.append(childName==null? "":childName);
		}
		buffer.append("</childName>");

		buffer.append("<childVersion>");
		buffer.append(Tools.getNullValue(childVersion,""));
		buffer.append("</childVersion>");

		buffer.append("<childType>");
		buffer.append(Tools.getNullValue(childType,""));
		buffer.append("</childType>");

		buffer.append("<childPhase>");
		buffer.append(Tools.getNullValue(childPhase,""));
		buffer.append("</childPhase>");

		buffer.append("<usingAmount>");
		buffer.append(usingAmount==null? "":usingAmount);
		buffer.append("</usingAmount>");

		buffer.append("<unit>");
		buffer.append(Tools.getNullValue(dw,"个"));
		buffer.append("</unit>");

		buffer.append("<batch>");
		buffer.append(Tools.getNullValue(BATCH,""));
		buffer.append("</batch>");

		buffer.append("<cldelb>");
		buffer.append(Tools.getNullValue(cldelb,""));
		buffer.append("</cldelb>");

		buffer.append("<xlcc>");
		buffer.append(xlcc==null? "":xlcc);
		buffer.append("</xlcc>");

		buffer.append("<kzjs>");
		buffer.append(kzjs==null? "":kzjs);
		buffer.append("</kzjs>");

		buffer.append("<sjsl>");
		buffer.append(sjsl==null? "":sjsl);
		buffer.append("</sjsl>");

		buffer.append("<sjcc>");
		buffer.append(sjcc==null? "":sjcc);
		buffer.append("</sjcc>");

		buffer.append("<sjkzjs>");
		buffer.append(sjkzjs==null? "":sjkzjs);
		buffer.append("</sjkzjs>");

		buffer.append("<sl>");
		buffer.append(sl==null? "":sl);
		buffer.append("</sl>");

		buffer.append("<unit2>");
		buffer.append(Tools.getNullValue(dw,"个"));
		buffer.append("</unit2>");

		buffer.append("<comment>");
		buffer.append(comment);
		buffer.append("</comment>");

		buffer.append("<index>");
		buffer.append(index);
		buffer.append("</index>");

		buffer.append("<processFileNum>");
		buffer.append(processFileNum);
		buffer.append("</processFileNum>");

		buffer.append("<processFileName>");
		buffer.append(processFileName);
		buffer.append("</processFileName>");

		buffer.append("<processFileVersion>");
		buffer.append(processFileVersion);
		buffer.append("</processFileVersion>");

		buffer.append("<processFileNumber>");
		buffer.append(processFileNumber);
		buffer.append("</processFileNumber>");

		buffer.append("<pplanType>");
		buffer.append(pplanType);
		buffer.append("</pplanType>");

		buffer.append("<zfflag>");
		buffer.append(Tools.getNullValue(zfflag,""));
		buffer.append("</zfflag>");

		buffer.append("<source>");
		buffer.append(Tools.getNullValue(source,""));
		buffer.append("</source>");

		if(addAttrList!=null){
			if (addAttribute.length == addAttrList.size()) {
				for (int i = 0; i < addAttrList.size(); i++) {
					buffer.append("<").append(addAttribute[i]).append(">");
					buffer.append(addAttrList.get(i));
					buffer.append("</").append(addAttribute[i]).append(">");
				}
			}
		}

		Set<Map.Entry<String,String>> entrySet = extAttrs.entrySet();
		for(Map.Entry<String,String> entry:entrySet){
			String key = entry.getKey();
			String value = entry.getValue();

			buffer.append("<").append(key).append(">");
			buffer.append(value);
			buffer.append("</").append(key).append(">");
		}


	    buffer.append("</link>");

		return buffer.toString();
	}

	public String getProcessPlanDEInfo(String number, String pplanNumber) throws Exception {
		// TODO Auto-generated method stub
		//设置管理员权限
		wt.session.SessionHelper.manager.setAdministrator();
		StringBuffer bom = new StringBuffer();
		bom.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bom.append("<bom>");

		WTDocument doc =null;
		if(number!=null){
			 doc = (WTDocument)WCUtil.getDoc(number);
		}

		if(doc==null&&pplanNumber!=null){
			doc = WCUtil.getDocumentByIBANumber(pplanNumber);
			if(doc!=null){
				QueryResult qr2 =VersionControlHelper.service.allIterationsOf(doc.getMaster());
				if(qr2.hasMoreElements()){
					doc =  (WTDocument)qr2.nextElement();
				}
			}

		}
		if(doc==null){
			bom.append("<exception>");
			bom.append("所查找的工艺文件在PDM中不存在!");
			bom.append("</exception>");
			bom.append("</bom>");
			return bom.toString();
		}
		String xml = getProcessPlanDEInfo(doc);
		bom.append(xml).append("</bom>");
		return bom.toString();
	}




	private String getProcessPlanDEInfo(WTDocument doc) throws WTException, PropertyVetoException, IOException, DocumentException {
		ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
		byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
		WTProperties pro = WTProperties.getLocalProperties();
		String wt_temp = pro.getProperty("wt.temp");
		String zipFilePath = wt_temp+File.separator+UUID.randomUUID().toString();
		File file = new File(zipFilePath);
		if(!file.exists()) {
			file.mkdirs();
		}
		String xmlFile = zipFilePath+File.separator+doc.getNumber()+".xml";
		ZipUtil.unZip(bytes, zipFilePath);
		Object[] beans =   CldeUtil.readXML(xmlFile);
		StringBuilder yclBuffer = new StringBuilder();

		if(beans!=null){
			List<MaterialsBean> materialBean = (List<MaterialsBean>)beans[0];
			List<PbomErpPartBean> pbomErpPartBean =(List<PbomErpPartBean>) beans[1];
			if(materialBean.isEmpty()&&pbomErpPartBean.isEmpty()){
				return "<exception>不存在相应的定额信息</exception>";
			}
			QueryResult qr = WTPartHelper.service.getDescribesWTParts(doc);
			String type = "";
			String parenNum = "";
			String parentName = "";
			String parenPhase = "";
			String BATCH = "";
			String parentversion = "";
	        if (qr.hasMoreElements()) {
	            WTPart part = (WTPart) qr.nextElement();
	            IBAUtility utility = new IBAUtility(part);
				 type = utility.getIBAValue("MTYPE");
				 parenNum = part.getNumber();//材料上级图号
				 parentName = part.getName();//上级名称
				 parenPhase = utility.getIBAValue("PHASE_CODE");
			    BATCH = utility.getIBAValue("BATCH");//批次号
				if(BATCH==null||"null".equals(BATCH)){
					BATCH= "";
				}
				 parentversion = part.getIterationDisplayIdentifier().toString();//版本
	        }


			String childNumber = "";//材料编码
			String childName = "";//材料名称
			String childType = "";//材料类别
			String cldelb = "";//材料定额类别
			String xlcc = "";//下料尺寸
			String kzjs = "";//可制件数
			String sjsl = "";//试件数量
			String sjcc = "";//试件尺寸
			String sjkzjs = "";//试件可制件数
			String sl = "";//数量
			String dw = "";//单位
			String comment = "";//备注
			String processFileNum = "";//工艺文档流水号
			String processFileName = "";//工艺文件名称
			String processFileVersion = "";//工艺文件版本
			String processFileNumber = "";//工艺文件编号
			int index = 0;
			for(MaterialsBean bean:materialBean){
				index ++;
				childNumber = bean.getChbm();
				childName = bean.getChmc();
				childType = bean.getMaterialType();
				cldelb = bean.getUsingType();
				xlcc = bean.getXlcc();
				kzjs = bean.getKzjs();
				sjsl = bean.getSjsl();
				sjcc = bean.getSjcc();
				sjkzjs = bean.getSjkzjs();
				sl = bean.getSl();
				dw=bean.getDw();
				comment=bean.getComment();

				if(bean.getBatch()!=null &&!"".equals(bean.getBatch())&&!"null".equals(bean.getBatch())){
					BATCH = bean.getBatch();
				}


				if(sjkzjs==null||"null".equals(sjkzjs)){
					sjkzjs="";
				}
				processFileNumber = bean.getProcessFileNumber();
				processFileNum = bean.getPplanNumber();
				processFileName = bean.getPplanName();
				processFileVersion = bean.getPplanVersion();
				String dataFrom = bean.getDataFrom();
				yclBuffer.append(createLinkXML(parenNum,parentName,parentversion,type,parenPhase,childNumber,childName,"",childType,"",
               			"","", cldelb, xlcc,kzjs,sjsl,sjcc,sjkzjs, sl,processFileNum,processFileName,processFileVersion,dataFrom,dw,comment,BATCH,index+"",processFileNumber,bean.getPplanType(),bean.getZfflag()));
			}



			if(pbomErpPartBean != null && !pbomErpPartBean.isEmpty()) {
    			for (PbomErpPartBean bean : pbomErpPartBean) {
    				index ++;
    				childNumber = bean.getChbm();
    				String name = bean.getChmc();
    				String childVersion = "space.1";
    				String matemount = "1";
    				if(bean.getSl() != null && !"".equals(bean.getSl())) {
    					matemount =bean.getSl();
    				}
    				String unit = bean.getDw();
    				String dataFrom = bean.getDataFrom();
    				//生成物资使用信息
    				yclBuffer.append(createLinkXML(parenNum,parentName,parentversion,type,parenPhase,childNumber,name,childVersion,"外购件",
        		    		"",matemount,unit,bean.getType(),"","","","","","",bean.getTechNumber(),bean.getTechName(),bean.getVersion(),dataFrom,"","",BATCH,index+"",bean.getPplanNumber(),bean.getPplanType(),bean.getZfflag()));
				}
    		}
		}
		return yclBuffer.toString();
	}

	private WTDocument getProcessDocByPlanNumber(String pplanNumber) throws WTException {
		QuerySpec qs = new QuerySpec();
		qs.setAdvancedQueryEnabled(true);
		int ibaHolderIndex = qs.appendClassList(WTDocument.class, true);
		int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
		int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
		// Latest Iteration
		SearchCondition scLatestIteration = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
		// String Value With IBA Holder
		SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, WTAttributeNameIfc.ID_NAME);
		// String Value With Definition
		SearchCondition scJoinStringValueStringDefinition = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.key.id", StringDefinition.class,
				WTAttributeNameIfc.ID_NAME);
		qs.appendWhere(scLatestIteration, ibaHolderIndex);
		qs.appendAnd();
		qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, ibaHolderIndex);
		qs.appendAnd();
		qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);
		SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME, SearchCondition.EQUAL, "PPNUMBER");
		SearchCondition scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.EQUAL,pplanNumber);
		qs.appendAnd();
		qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
		qs.appendAnd();
		qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			WTDocument doc = (WTDocument) obj[0];
			QueryResult qr2 =VersionControlHelper.service.allIterationsOf(doc.getMaster());
			if(qr2.hasMoreElements()){
				return (WTDocument)qr2.nextElement();
			}
		}
		return null;
	}

	/**
	 * 获取PbomErpPartBean中新增属性值集合 -- add by hz 202001
	 * @param bean
	 * @return
	 */
	private List<String> getAddAttrList(PbomErpPartBean bean) {
		List<String> result = new ArrayList<String>();
		result.add(bean.getBmdj()==null? "":bean.getBmdj());
		result.add(bean.getKfzbtid()==null? "":bean.getKfzbtid());
		result.add(bean.getKfzbsee()==null? "":bean.getKfzbsee());
		result.add(bean.getXncs()==null? "":bean.getXncs());
		result.add(bean.getJdmgdj_state()==null? "":bean.getJdmgdj_state());
		result.add(bean.getJdmgdj()==null? "":bean.getJdmgdj());
		result.add(bean.getSmdj()==null? "":bean.getSmdj());

		return result;
	}
	public String getWTPartPTInfo(String number, String batch) throws Exception {
		wt.session.SessionHelper.manager.setAdministrator();
		StringBuffer bom = new StringBuffer();
		bom.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bom.append("<bom>");
		WTPart part = WCUtil.getPartByNumber(number);
		if(part==null){
			bom.append("<exception>");
			bom.append("所查找的编号在PDM中不存在!");
			String check = BomUtil.checkNum(number);
			if(!check.equals(""))
				bom.append("相似编号如下：").append(check);
			bom.append("</exception>");
			bom.append("</bom>");
			return bom.toString();
		}

		bom.append(getLevel1BomStructureByBatch(number,batch));
		bom.append("</bom>");
		return bom.toString();
	}

	private Object getLevel1BomStructureByBatch(String number , String batch) throws WTException, DocumentException {
		StringBuffer allBom = new StringBuffer();
		WTPart part1 = WCUtil.getPartByNumber(number);
		WTPartMaster partMaster = (WTPartMaster)part1.getMaster();
		WTPart part  = null;
		if(Tools.isNull(batch)){
			part = BomUtil.getLatestPartByView(partMaster, "Manufacturing");
		}else{
			part = BomUtil.getLatestPartByBatchView(partMaster,batch, "Manufacturing");
		}
		IBAUtility utility = new IBAUtility(part);
		String name = part.getName();//零部件名称
		String partType = utility.getIBAValue("MTYPE");//零部件类型
		String phase = utility.getIBAValue("PHASE_CODE");//研制阶段
		String BATCH = utility.getIBAValue("BATCH");//批次号
		String czzcj = utility.getIBAValue("ZZCJ");
		String SETMARK = utility.getIBAValue("SETMARK");

		if(BATCH==null||"null".equals(BATCH)){
			BATCH= "";
		}
		String version = part.getIterationDisplayIdentifier().toString();//版本

		Map<String,String> extAttrs = new LinkedHashMap<String, String>();
		extAttrs.put("childFactory",czzcj);
		extAttrs.put("SETMARK",SETMARK);
		allBom.append(createLinkXML("","","","","",number,name,version,partType,phase,"","","","","","","","","","","","","","","",BATCH,"","","","",extAttrs));
		//获取子结构BOM信息
		allBom.append(getLevel1BOMData(part,batch));//遍历获取所有子结构信息


		return allBom.toString();
	}

	private String getLevel1BOMData(WTPart parentPart, String batch) throws WTException {
		StringBuffer bomData = new StringBuffer();
		IBAUtility parentUtility = new IBAUtility(parentPart);
		String parentPhase = parentUtility.getIBAValue("PHASE_CODE");//上级图号研制阶段
		String parentType = parentUtility.getIBAValue("MTYPE");
		String parentBATCH = parentUtility.getIBAValue("BATCH");//批次号
		String parentSETMARK = parentUtility.getIBAValue("SETMARK");
		String parentzzcj = parentUtility.getIBAValue("ZZCJ");
		//String parentfzcj = parentUtility.getIBAValue("FZCJ");
		if(parentBATCH==null||"null".equals(parentBATCH)){
			parentBATCH= "";
		}
		String parentName = parentPart.getName();//上级名称
		String view = parentPart.getViewName();
		String number = parentPart.getNumber();
		String parentversion = parentPart.getIterationDisplayIdentifier().toString();//版本
		QueryResult list = null;
		Transaction trx = new Transaction();
		try {
			trx.start();

			List<WTDocument>  documentList = ERPBomHelper.getAllApprovedTechnicsDocumentByPart(parentPart,"TEMP","ALL");
			List<GLErpPbomPartBean> erpMatchPartList =null;
			List<GLErpPbomPartBean>  erpNewPartList = null;
			List<GLErpPeiTaoPartBean>  erpPeiTaoPartBeans = null;
			List<GLErpMaterialsBean> materialsBeanList = new ArrayList<GLErpMaterialsBean>();
			List<GLErpElement> elementList = null;
			ERPCacheBean cacheBean = null;

				//缓存的工艺定额
			cacheBean =  ERPCacheHelper.getErpCacheList(documentList,"TEMP");
			//未缓存的工艺定额新增
			elementList = ERPBomHelper.getElementList(cacheBean.getDocumentList());

			//未缓存的工艺定额匹配
			erpMatchPartList = ERPBomHelper.getPBOMMatchInfo(parentPart,elementList);
			//保存未缓存的工艺定额匹配
			ERPCacheHelper.saveErpMatchPartCache(erpMatchPartList);

			erpMatchPartList.addAll(cacheBean.getErpMatchPartList());//合并

			if(Constants.TYPE_ZIZHIJIAN.equals(parentType)||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(parentType)||Constants.TYPE_WAIPEITAOJIAN.equals(parentType)){
				// 未缓存的材料定额
				materialsBeanList =  ERPBomHelper.getCldeInfo(elementList);
				//保存未缓存的材料定额
				ERPCacheHelper.saveMaterialCache(materialsBeanList);

				materialsBeanList.addAll(cacheBean.getMaterialBeanList());//合并

			}
			// 未缓存的工艺定额
			erpNewPartList = ERPBomHelper.getPBOMNewPartInfo(parentPart,elementList);
			//保存未缓存的工艺定额新增
			ERPCacheHelper.saveErpNewPartCache(erpNewPartList);
			erpNewPartList.addAll(cacheBean.getErpNewPartList());//合并

			erpPeiTaoPartBeans = ERPBomHelper.getPeiTaoPbomInfo(parentPart,elementList,batch);
			ERPCacheHelper.savePeiTaoPbomCache(erpPeiTaoPartBeans);
			erpPeiTaoPartBeans.addAll(cacheBean.getErpPeiTaoPartList());//合并
			/*} catch (Exception e) {
				e.printStackTrace();
				elementList = ERPBomHelper.getElementList(documentList);

				erpMatchPartList = ERPBomHelper.getPBOMMatchInfo(parentPart,elementList);
				if(Constants.TYPE_ZIZHIJIAN.equals(parentType)||Constants.TYPE_DAILIAOWEIWAIJIAN.equals(parentType)||Constants.TYPE_WAIPEITAOJIAN.equals(parentType)){
					materialsBeanList = ERPBomHelper.getCldeInfo(elementList);
				}
				erpNewPartList = ERPBomHelper.getPBOMNewPartInfo(parentPart,elementList);
			}*/
			//材料定额
			if(!materialsBeanList.isEmpty()){
				bomData.append(ERPBomHelper.getYclInfo(parentPart,materialsBeanList));
			}
			List<String> addAttrList = null;
			if(erpNewPartList != null && !erpNewPartList.isEmpty()) {
				int index = 0;
				for (GLErpPbomPartBean bean : erpNewPartList) {
					index ++;
					addAttrList = getAddAttrList(bean);
					String childNumber = bean.getChbm();
					String name = bean.getChmc();
					String childVersion = "space.1";
					String matemount = "1";
					if(bean.getSl() != null && !"".equals(bean.getSl())) {
						matemount =bean.getSl();
					}
					String unit = bean.getDw();
					String dataFrom = bean.getDataFrom();
					//生成物资使用信息
					Map<String,String> extAttrs = new LinkedHashMap<String, String>();
					extAttrs.put("parentFactory",parentzzcj);
					extAttrs.put("childFactory","");
					extAttrs.put("dept",bean.getDept());
					extAttrs.put("SETMARK",parentSETMARK);

					bomData.append(createLinkXML2(number, parentName, parentversion, parentType, parentPhase, childNumber, name, childVersion, "外购件", "", matemount, unit, "", bean.getXlcc(), bean.getKzjs(), "", "", "", matemount,
							bean.getTechNumber(), bean.getTechName(), bean.getVersion(), dataFrom, unit, "", parentBATCH, index + "", bean.getPplanNumber(), bean.getPplanType(), bean.getZfflag(),
							addAttrList,extAttrs));

				}
			}

			if(erpMatchPartList != null) {
				int index = 0;
				for(GLErpPbomPartBean bean: erpMatchPartList) {
					index ++;
					if(bean != null) {
						String matemount = "1";
						String unit = "";
						addAttrList = getAddAttrList(bean);
						String childNumber = bean.getChbm();//获取PDM与ERP集成得到的物资存货编码
						if(bean.getGyCount() != null && !"".equals(bean.getGyCount())) {
							matemount =bean.getGyCount();
						}
						unit = bean.getDw();
						String dataFrom = bean.getDataFrom();
						String name = bean.getChmc();

						Map<String,String> extAttrs = new LinkedHashMap<String, String>();
						extAttrs.put("parentFactory",parentzzcj);
						extAttrs.put("childFactory","");

						bomData.append(createLinkXML2(number, parentName, parentversion, parentType, parentPhase, childNumber, name, "", "外购件", "", matemount, unit, "",
								bean.getXlcc(), bean.getKzjs(), "", "", "", matemount, bean.getTechNumber(), bean.getTechName(), bean.getVersion(), dataFrom, "", "", parentBATCH, index + "", bean.getPplanNumber(),
								bean.getPplanType(), bean.getZfflag(), addAttrList,extAttrs));
					}
				}
			}

			int index = 0;
			for(GLErpPeiTaoPartBean peiTaoPartBean:erpPeiTaoPartBeans){
				index ++;
				Map<String,String> extAttrs = new LinkedHashMap<String, String>();
				extAttrs.put("parentFactory",parentzzcj);
				extAttrs.put("childFactory","");
				bomData.append(createLinkXML2(number, parentName, parentversion, parentType, parentPhase, peiTaoPartBean.getChildNumber(), peiTaoPartBean.getChildName(), peiTaoPartBean.getChildVersion(), peiTaoPartBean.getPartType(), peiTaoPartBean.getChildPhase(), peiTaoPartBean.getAmount(), peiTaoPartBean.getUnit(), "", "","", "", "", "", peiTaoPartBean.getAmount(),
						peiTaoPartBean.getTechNumber(), peiTaoPartBean.getTechName(), peiTaoPartBean.getVersion(), "配套表", peiTaoPartBean.getUnit(), "", parentBATCH, index + "", peiTaoPartBean.getPplanNumber(), peiTaoPartBean.getPplanType(), peiTaoPartBean.getZfflag(),
						addAttrList,extAttrs));
			}


			trx.commit();
			trx = null;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(trx!=null)  trx.rollback();
		}

		return bomData.toString();
	}


	  //自制件关联原材料处理
	  //number表示材料关联父亲图号
	  //phase 表示材料关连父亲研制阶段
	    private String checkYcl(String number,String phase,WTPart part,String batchVersion,String fileTypeB,String fileTypeC) throws DocumentException{
	    	StringBuffer yclBuffer = new StringBuffer();//初始化原材料结构的容器
	    	if(fileTypeB.equals(Constants.LABEL_PROCESS_TYPEB_FORMAL))
	    		fileTypeB = Constants.PROCESS_TYPEB_FORMAL;
	    	if(fileTypeB.equals(Constants.LABEL_PROCESS_TYPEB_TEMP))
	    		fileTypeB = Constants.PROCESS_TYPEB_TEMP;
	    	if(fileTypeC.equals(Constants.LABEL_PROCESS_TYPEC_PRIMARY))
	    		fileTypeC = Constants.PROCESS_TYPEC_PRIMARY;
	    	if(fileTypeC.equals(Constants.LABEL_PROCESS_TYPEC_ASSIST))
	    		fileTypeC = Constants.PROCESS_TYPEC_ASSIST;
	    	try {
				IBAUtility utility = new IBAUtility(part);
				String type = utility.getIBAValue("MTYPE");
				String parenNum = part.getNumber();//材料上级图号
				String parentName = part.getName();//上级名称
				String parenPhase = utility.getIBAValue("PHASE_CODE");
				String BATCH = utility.getIBAValue("BATCH");//批次号
				String pzzcj = utility.getIBAValue("ZZCJ");

				if(BATCH==null||"null".equals(BATCH)){
					BATCH= "";
				}
				String parentversion = part.getIterationDisplayIdentifier().toString();//版本
				String childNumber = "";//材料编码
				String childName = "";//材料名称
				String childType = "";//材料类别
				String cldelb = "";//材料定额类别
				String xlcc = "";//下料尺寸
				String kzjs = "";//可制件数
				String sjsl = "";//试件数量
				String sjcc = "";//试件尺寸
				String sjkzjs = "";//试件可制件数
				String sl = "";//数量
				String dw = "";//单位
				String comment = "";//备注
				String processFileNum = "";//工艺文档流水号
				String processFileName = "";//工艺文件名称
				String processFileVersion = "";//工艺文件版本
				String processFileNumber = "";//工艺文件编号
				CldeUtil clde = new CldeUtil();
				List<MaterialsBean> materialBean = new ArrayList<MaterialsBean>();
				materialBean = clde.getCldeInfo(part,fileTypeB,fileTypeC);//获取材料定额信息（包括原材料定额、试件原材料定额、主要材料定额）
				int index = 0;
				for(MaterialsBean bean:materialBean){
					index ++;
					childNumber = bean.getChbm();
					childName = bean.getChmc();
					childType = bean.getMaterialType();
					cldelb = bean.getUsingType();
					xlcc = bean.getXlcc();
					kzjs = bean.getKzjs();
					sjsl = bean.getSjsl();
					sjcc = bean.getSjcc();
					sjkzjs = bean.getSjkzjs();
					sl = bean.getSl();
					dw=bean.getDw();
					comment=bean.getComment();

					if(bean.getBatch()!=null &&!"".equals(bean.getBatch())&&!"null".equals(bean.getBatch())){
						BATCH = bean.getBatch();
					}


					if(sjkzjs==null||"null".equals(sjkzjs)){
						sjkzjs="";
					}
					processFileNumber = bean.getProcessFileNumber();
					String pplanType = bean.getPplanType();

					processFileNum = bean.getPplanNumber();
					processFileName = bean.getPplanName();
					processFileVersion = bean.getPplanVersion();
					String dataFrom = bean.getDataFrom();
					String zfflag = bean.getZfflag();

					Map<String,String> extAttrs = new LinkedHashMap<String, String>();
					extAttrs.put("parentFactory",pzzcj);
					extAttrs.put("dept",bean.getDept());
					yclBuffer.append(createLinkXML(parenNum,parentName,parentversion,type,parenPhase,childNumber,childName,"",childType,"",
							sl,dw, cldelb, xlcc,kzjs,sjsl,sjcc,sjkzjs, sl,processFileNum,processFileName,processFileVersion,dataFrom,dw,comment,BATCH,index+"",processFileNumber,pplanType,zfflag,extAttrs));
				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return yclBuffer.toString();
	    }
}
