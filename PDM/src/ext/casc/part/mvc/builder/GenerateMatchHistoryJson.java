package ext.casc.part.mvc.builder;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.pbom.db.ErpDao;
import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.util.*;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.extend.util.Debug;
import ext.ases.techMaterial.gwpersistable.GwPersistenceHelper;
import ext.ases.techMaterial.gwpersistable.GwQueryResult;
import ext.ases.techMaterial.gwpersistable.GwQuerySpec;
import ext.ases.techMaterial.model.*;
import ext.casc.access.AccessAdminUtil;
import ext.casc.dingeInformation.CldeBean;
import ext.casc.dingeInformation.DingeInformationBuilder;
import ext.casc.integrate.util.BomUtil;
import ext.casc.nc.bean.GLNCPartMapping;
import ext.casc.part.bean.GLWzMatchRecord;
import ext.casc.part.cache.JsonObjectCache;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import ext.casc.version.VersionCommonHelper;
import ext.casc.workflow.WorkflowHelper;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.IconDelegate;
import wt.fc.IconDelegateFactory;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;
import wt.util.IconSelector;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.Workable;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.*;

public class GenerateMatchHistoryJson {
	public static int MAX_PAGING = 500;
	public static Map<String,String> componentMap = null;
	public static Map<String,String> standardMap = null;
	public static Map<String,String> materialMap = null;
	public static Map<String,String> nonMaterialMap = null;
	public static Map<String,String> compoundMap = null;
	public static Map<String,String> jdclMap = null;
	public static Map<String,String> hgpMap = null;
	static{
		componentMap= new HashMap<String, String>();
		componentMap.put("wzName","WZMC");
		componentMap.put("xhgg","XHGG");
		componentMap.put("zldj","ZLDJ");
		componentMap.put("gys","SCCJ");
		componentMap.put("zldw","JLDW");
		componentMap.put("bzh","ZGF");
		componentMap.put("xxgf","XXGF");
		componentMap.put("xhph","XH");
		componentMap.put("fzxs","FZXS");
		componentMap.put("wxcc","WXCC");
		componentMap.put("jxxndj","ZYTJ");
		componentMap.put("fjxy","FJXY");
		componentMap.put("dcstxyq","TSSM");
		componentMap.put("sfjk","SFJK");
		componentMap.put("kfzbtid","KFSZBTID");
		componentMap.put("kfszbsee","KFSZBSEE");
		componentMap.put("xncs","XNCS");
		componentMap.put("sfjdmg","SFJDMG");
		componentMap.put("jdmgdj","JDMGDJ");
		componentMap.put("smdj","SMDJ");
		componentMap.put("wzNumber","WZBM");
		componentMap.put("wzjc","WZJC");
		componentMap.put("yxjb","BMYXJB");
		componentMap.put("bmzt","BMZT");
		componentMap.put("bmlx","BMLX");
		componentMap.put("bmdj","BMDJ");

		standardMap = new HashMap<String, String>();
		standardMap.put("wzName","WZMC");
		standardMap.put("xhgg","GG");
		standardMap.put("bzh","BZH");
		standardMap.put("jxxndj","JXXNDJ");
		standardMap.put("jldw","JLDW");
		standardMap.put("xhph","CL");
		standardMap.put("bmcl","BMCL");
		standardMap.put("rcl","RCL");
		standardMap.put("gys","SCCJ");
		standardMap.put("cpxs","CPXS");
		standardMap.put("zldj","CPDJ");
		standardMap.put("fjxy","NBXS");
		standardMap.put("dcstxyq","TSSM");
		standardMap.put("sfjk","SFJK");
		standardMap.put("wzNumber","WZBM");
		standardMap.put("wzjc","WZJC");
		standardMap.put("yxjb","BMYXJB");
		standardMap.put("bmzt","BMZT");
		standardMap.put("bmlx","BMLX");
		standardMap.put("bmdj","BMDJ");

		materialMap= new HashMap<String, String>();
		materialMap.put("wzName","WZMC");
		materialMap.put("xhph","PH");
		materialMap.put("xhgg","GG");
		materialMap.put("rcl","GYZT");
		materialMap.put("bzh","CYBZ");
		materialMap.put("jldw","JLDW");
		materialMap.put("sfjk","SFJK");
		materialMap.put("pzggbz","PZGGBZ");
		materialMap.put("jddj","JD");
		materialMap.put("zldj","ZLTZ");
		materialMap.put("gys","SCCJ");
		materialMap.put("wzjc","WZJC");
		materialMap.put("dcstxyq","TSSM");
		materialMap.put("fzxs","HSL");
		materialMap.put("xxgf","XS");
		materialMap.put("yxjb","BMYXJB");
		materialMap.put("bmzt","BMZT");
		materialMap.put("bmlx","BMLX");
		materialMap.put("bmdj","BMDJ");
		materialMap.put("wzNumber","WZBM");


		nonMaterialMap= new HashMap<String, String>();
		nonMaterialMap.put("wzName","WZMC");
		nonMaterialMap.put("xhph","PH");
		nonMaterialMap.put("xhgg","GG");
		nonMaterialMap.put("bzh","CYBZ");
		nonMaterialMap.put("jldw","JLDW");
		nonMaterialMap.put("gys","SCCJ");
		nonMaterialMap.put("dcstxyq","TSSM");
		nonMaterialMap.put("sfjk","SFJK");
		nonMaterialMap.put("wzjc","WZJC");
		nonMaterialMap.put("fzxs","HSL");
		nonMaterialMap.put("xxgf","XS");
		nonMaterialMap.put("yxjb","BMYXJB");
		nonMaterialMap.put("bmzt","BMZT");
		nonMaterialMap.put("bmlx","BMLX");
		nonMaterialMap.put("bmdj","BMDJ");
		nonMaterialMap.put("wzNumber","WZBM");

		compoundMap= new HashMap<String, String>();
		compoundMap.put("wzName","WZMC");
		compoundMap.put("xhph","PH");
		compoundMap.put("xhgg","GG");
		compoundMap.put("bzh","CYBZ");
		compoundMap.put("jldw","JLDW");
		compoundMap.put("gys","SCCJ");
		compoundMap.put("dcstxyq","TSSM");
		compoundMap.put("sfjk","SFJK");
		compoundMap.put("wzjc","WZJC");
		compoundMap.put("fzxs","HSL");
		compoundMap.put("xxgf","XS");
		compoundMap.put("yxjb","BMYXJB");
		compoundMap.put("bmzt","BMZT");
		compoundMap.put("bmlx","BMLX");
		compoundMap.put("bmdj","BMDJ");
		compoundMap.put("wzNumber","WZBM");
		//机电材料
		jdclMap= new HashMap<String, String>();
		jdclMap.put("wzName","WZMC");
		jdclMap.put("xhph","PH");
		jdclMap.put("bzh","BZH");
		jdclMap.put("xhgg","XHGG");
		jdclMap.put("jldw","JLDW");
		jdclMap.put("gys","SCCJ");
		jdclMap.put("yxjb","BMYXJB");
		jdclMap.put("bmzt","BMZT");
		jdclMap.put("bmlx","BMLX");
		jdclMap.put("bmdj","BMDJ");
		jdclMap.put("wzNumber","WZBM");
		jdclMap.put("xncs","XNCS");
		jdclMap.put("dcstxyq","TSSM");
		jdclMap.put("sfjk","SFJK");



		//火工品
		hgpMap= new HashMap<String, String>();
		hgpMap.put("wzName","WZMC");
		hgpMap.put("bzh","BZH");
		hgpMap.put("gys","SCCJ");
		hgpMap.put("yxjb","BMYXJB");
		hgpMap.put("bmzt","BMZT");
		hgpMap.put("bmlx","BMLX");
		hgpMap.put("bmdj","BMDJ");
		hgpMap.put("wzNumber","WZBM");
		hgpMap.put("xncs","XNCS");
		hgpMap.put("dcstxyq","TSSM");
		hgpMap.put("jldw","JLDW");
		hgpMap.put("bmcl","CPDH");
		hgpMap.put("jdmgdj","ZL");
		hgpMap.put("jddj","ZCSM");
		hgpMap.put("smdj","TNT");
		hgpMap.put("xncs","XNCS");
		hgpMap.put("dcstxyq","TSSM");
		hgpMap.put("sfjk","SFJK");


	}
	private static String docIcon = null;
	public  static String getIcon(WTObject obj) throws WTException {
		if(docIcon==null){
			String imgURL = null;
			try {
				IconDelegate delegate = IconDelegateFactory.getInstance()
						.getIconDelegate(obj);
				IconSelector selector = delegate.getStandardIconSelector();
				while (!selector.isResourceKey()) {
					delegate = delegate.resolveSelector(selector);
					selector = delegate.getStandardIconSelector();
				}
				imgURL = selector.getIconKey();
			} catch (Exception e) {
				throw new WTException(e);
			}
			docIcon = imgURL;
		}
		return docIcon;
	}


    public  static JSONObject generateDocGrid(String oid )  {
		JSONObject result = new JSONObject();
		WTUser currentuser = null;
        boolean enforce = wt.session.SessionServerHelper.manager
                .setAccessEnforced(false);
		try{
			boolean isAdmin = AccessAdminUtil.isAdmin();
			currentuser = (WTUser) SessionHelper.manager.getPrincipal();
			WTPart rootPart = (WTPart) ReferenceFactory.getObjectbyOid(oid);
			List<WTPart> allPart = new ArrayList<WTPart>();
			allPart.add(rootPart);
			DownloadTechnicsReportUtil.getAllChildPart(rootPart, allPart);
			JSONArray array = new JSONArray();
			for (WTPart part : allPart) {
				List<WTDocument> documents =  BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
				for(WTDocument doc:documents){
					String state = doc.getState().toString();
					if("OBSOLESCENCE".equals(state)){
						continue;
					}
					if(!"APPROVED".equals(state) ){
						if("space".equals(doc.getVersionIdentifier().getValue())){
							continue;
						}else{
							doc =(WTDocument)VersionCommonHelper.getPreVersionObj(doc);
						}
					}

					String group = ProcessEditorToWCIntfRMI.getGroupNameByUser(currentuser);
					String docGroup = IBAHelper.getIBAStringValue(doc,"DEPT");
					String pplantype = IBAHelper.getIBAStringValue(doc,"PPLANTYPE");
					if("正式工艺文件".equals(pplantype)) {
                        if (isAdmin || doc.getModifier().getPrincipal().getName().equals(currentuser.getName())
                                || doc.getCreator().getPrincipal().getName().equals(currentuser.getName()) || group.equals(docGroup)) {
                            JSONObject jsonObject = new JSONObject();
                            String docoid = PersistenceCommonHelper.getOid(doc);
                            jsonObject.put("id", docoid);
                            jsonObject.put("icontype", "");
                            jsonObject.put("partNumber", part.getNumber());
                            jsonObject.put("partName", part.getName());
                            jsonObject.put("number", doc.getNumber() + "@" + docoid);
                            jsonObject.put("name", doc.getName());
                            jsonObject.put("modifierName", doc.getModifier().getFullName());
                            jsonObject.put("version", VersionCommonHelper.getVersion(doc));
                            jsonObject.put("matchState", IBAHelper.getIBAStringValue(doc, "matchState"));
                            array.put(jsonObject);

                        }
					}

				}
			}
			result.put("data", array);
			result.put("totalCount", array.length());
		}catch (Exception e){
			e.printStackTrace();
		}finally {
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return result;

	}




	public static JSONObject generateDocWZGrid(String oid) {
		JSONObject result = new JSONObject();
		WTUser currentuser = null;
        boolean enforce = wt.session.SessionServerHelper.manager
                .setAccessEnforced(false);
		try {
			currentuser = (WTUser) SessionHelper.manager.getPrincipal();

			JSONArray array = new JSONArray();
			WTDocument doc = (WTDocument) ReferenceFactory.getObjectbyOid(oid);
			if (!VersionControlHelper.isLatestIteration(doc)){
				doc = (WTDocument) VersionControlHelper.getLatestIteration(doc);
			}
            WTUser modifer = (WTUser) doc.getModifier().getPrincipal();
            SessionHelper.manager.setPrincipal(modifer.getAuthenticationName());
			String number = doc.getNumber();
			ApplicationData appData = WTDocumentUtil.getPrimaryByDocument(doc);
			byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);

			String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "tempDinge" + File.separator + UUID.randomUUID().toString() + File.separator;
			FileUtil.writeBytes(tempFilePath, appData.getFileName(), bytes);
			ApacheZipUtil.decompress(tempFilePath + appData.getFileName(), tempFilePath + number);
			File xmlFile = new File(tempFilePath + number + File.separator + number + ".xml");
			Map<String, List<Element>> map = DingeInformationBuilder.getDingEInformation(xmlFile);
			List<CldeBean> beanlist = DingeInformationBuilder.getDingeData(map);
			String vid = VersionCommonHelper.getVR(doc);
			Map<String,String> recordMap = getGLWzMatchRecords(vid);
			boolean needMatch = false;
			for(CldeBean cldeBean :beanlist){
				JSONObject jsonObject = new JSONObject();
				String orgWZNumber =  cldeBean.getSjbm();
                String matchWZNumber  = "";
                String needMatchtType = "0";
                if(orgWZNumber.startsWith("A")||orgWZNumber.startsWith("B")||orgWZNumber.startsWith("C")
                        ||orgWZNumber.startsWith("D")||orgWZNumber.startsWith("E")||orgWZNumber.startsWith("F")
                        ||orgWZNumber.startsWith("G")){
                    matchWZNumber  = orgWZNumber;
				}else{
                    needMatch = true;
                    needMatchtType = "1";
				}
                if("1".equals(needMatchtType)){
                    matchWZNumber  = getGLWzMatchMapping(orgWZNumber);
                }

				if(recordMap.containsKey(orgWZNumber)){
					orgWZNumber = recordMap.get(orgWZNumber);
				}

				jsonObject.put("oid", oid);
				jsonObject.put("docName", IBAHelper.getIBAStringValue(doc,"PPNUMBER"));
				jsonObject.put("needMatch", needMatchtType);
				jsonObject.put("matchWZNumber", matchWZNumber);
				jsonObject.put("gongYiWZNumber", cldeBean.getSjbm());
				jsonObject.put("orgWZNumber", orgWZNumber);
				jsonObject.put("wzName", cldeBean.getName());
				jsonObject.put("sl", cldeBean.getSl());
				jsonObject.put("dataFrom","历史定额");
				jsonObject.put("option","匹配");
				String dataType = cldeBean.getDatafrom();
				if(dataType.contains("试件原材料")){
					dataType = "sjyclde";
				}
				jsonObject.put("dataType", dataType);
				array.put(jsonObject);

			}
			if(needMatch){
                String matchState = IBAHelper.getIBAStringValue(doc,"matchState");
				if(Tools.isNull(matchState)){
                    WorkflowHelper.setIBAValue(doc,"matchState","未匹配");
                }
            }else{
				String matchState = IBAHelper.getIBAStringValue(doc,"matchState");
				if(Tools.isNull(matchState)){
					WorkflowHelper.setIBAValue(doc,"matchState","无需匹配");
				}
            }

			result.put("data", array);
			result.put("totalCount", array.length());
			FileUtil.deleteFile(new File(tempFilePath));

		}catch (Exception e){
			e.printStackTrace();
		}finally {
            if(currentuser!=null){
                try {
                    SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
                } catch (WTException e) {
                    e.printStackTrace();
                }

            }
            wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
        }
		return result;
	}

	public static Object generateWZHistoryGrid(String matchWZNumber, String gongYiWZNumber, String orgWZNumber) {
		JSONObject result = new JSONObject();
		WTUser currentuser = null;
		try{
			currentuser = (WTUser) SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAdministrator();
			JSONArray array = new JSONArray();

			if(!Tools.isNull(gongYiWZNumber)){
				JSONObject wz1  =	getWzJsonData(gongYiWZNumber);
				//wz1.put("historyType","当前物资");
				array.put(wz1);
			}
			if(!Tools.isNull(orgWZNumber)&&!orgWZNumber.equals(gongYiWZNumber)){
				JSONObject wz1  = getWzJsonData(orgWZNumber);
				//wz1.put("historyType","原始物资");
				array.put(wz1);
			}
			if(!Tools.isNull(matchWZNumber)&&!matchWZNumber.equals(gongYiWZNumber)){
				JSONObject wz1  = getWzJsonData(matchWZNumber);
				//wz1.put("historyType","新匹配物资");
				array.put(wz1);
			}

			result.put("data", array);
			result.put("totalCount", array.length());

		}catch (Exception e){
			e.printStackTrace();
		}finally {
			try {
				SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
			} catch (WTException e) {
				e.printStackTrace();
			}

		}
		return result;
	}

	public static JSONObject getWzJsonData(String number) {
		if(JsonObjectCache.wzCache.get(number)!=null){
			return JsonObjectCache.wzCache.get(number);
		}
		JSONObject object = new JSONObject();
		if(!Tools.isNull(number)){
			if(number.startsWith("A")||number.startsWith("B")||number.startsWith("C")||number.startsWith("D")
					||number.startsWith("E")||number.startsWith("F")||number.startsWith("G")){
				try {
					if(number.startsWith("A")){
						GwQuerySpec qs = new GwQuerySpec(TMEEleComponentsPartLink.class);
						qs.appendWhere(TMEEleComponentsPartLink.WZBM, GwQuerySpec.EQUAL, number);
						GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
						if (qr.hasNext()) {
							TMEEleComponentsPartLink tm = (TMEEleComponentsPartLink) qr.next();
							genJsonObject(object,tm);

						}
					}else if(number.startsWith("B")){
						GwQuerySpec qs = new GwQuerySpec(TMEStandPartLink.class);
						qs.appendWhere(TMEStandPartLink.WZBM, GwQuerySpec.EQUAL, number);
						GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
						if (qr.hasNext()) {
							TMEStandPartLink tm = (TMEStandPartLink) qr.next();
							genJsonObject(object,tm);

						}
					}else if(number.startsWith("C")){
						GwQuerySpec qs = new GwQuerySpec(TMEMetallicPartLink.class);
						qs.appendWhere(TMEMetallicPartLink.WZBM, GwQuerySpec.EQUAL, number);
						GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
						if (qr.hasNext()) {
							TMEMetallicPartLink tm = (TMEMetallicPartLink) qr.next();
							genJsonObject(object,tm);

						}
					}else if(number.startsWith("D")){
						GwQuerySpec qs = new GwQuerySpec(TMENonMetallicPartLink.class);
						qs.appendWhere(TMENonMetallicPartLink.WZBM, GwQuerySpec.EQUAL, number);
						GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
						if (qr.hasNext()) {
							TMENonMetallicPartLink tm = (TMENonMetallicPartLink) qr.next();
							genJsonObject(object,tm);
						}
					}
					else if(number.startsWith("E")){
						GwQuerySpec qs = new GwQuerySpec(TMECompoundMaterialPartLink.class);
						qs.appendWhere(TMECompoundMaterialPartLink.WZBM, GwQuerySpec.EQUAL, number);
						GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
						if (qr.hasNext()) {
							TMECompoundMaterialPartLink tm = (TMECompoundMaterialPartLink) qr.next();
							genJsonObject(object,tm);
						}
					}
					else if(number.startsWith("F")){//机电
						GwQuerySpec qs = new GwQuerySpec(TMEEleMachinePartLink.class);
						qs.appendWhere(TMEEleMachinePartLink.WZBM, GwQuerySpec.EQUAL, number);
						GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
						if (qr.hasNext()) {
							TMEEleMachinePartLink tm = (TMEEleMachinePartLink) qr.next();
							genJsonObject(object,tm);

						}
					}

					else if(number.startsWith("G")){//火工品
						GwQuerySpec qs = new GwQuerySpec(TMEExpDevicePartLink.class);
						qs.appendWhere(TMEExpDevicePartLink.WZBM, GwQuerySpec.EQUAL, number);
						GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
						if (qr.hasNext()) {
							TMEExpDevicePartLink tm = (TMEExpDevicePartLink) qr.next();
							genJsonObject(object,tm);

						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}else{
				Wzk wzk = ErpDao.getWzk("02",number);
				if(wzk!=null){
					object.put("wzNumber",number);

					object.put("wzName",wzk.getInvname());
					object.put("dengji","");
					object.put("wzjc","");
					object.put("jldw",wzk.getMeasname());
					object.put("xhph",wzk.getInvtype());
					object.put("xhgg",wzk.getInvspec());
					object.put("zldj",wzk.getDef4());
					object.put("gys",wzk.getCustname());
					String bzh = wzk.getJsgfbz();
					if(Tools.isNull(bzh)){
						bzh = wzk.getJstjname();
					}
					object.put("bzh",bzh);
					object.put("xxgf",wzk.getFjtjname());
					object.put("jxxndj",wzk.getDef14());
					object.put("dcstxyq",wzk.getDef10());
					object.put("jddj",wzk.getDef8());
					object.put("fzxs",wzk.getDef6());
					object.put("sfjk","");
					object.put("rcl",wzk.getDef13());
					object.put("bmcl","");
					object.put("sfjdmg","");
					object.put("jdmgdj","");
					object.put("kfzbtid","");
					object.put("kfszbsee","");
					object.put("smdj","");
					object.put("xncs",wzk.getDef12());
				}
			}
			if(!Tools.isNull(object.optString("wzNumber"))){
				JsonObjectCache.wzCache.put(number,object);

			}
		}
		return object;
	}

	private static void genJsonObject(JSONObject object, Object o) {
		if(o instanceof  TMEEleComponentsPartLink){//元器件
			TMEEleComponentsPartLink tm = (TMEEleComponentsPartLink)o;
			object.put("wzNumber",tm.getWzbm());
			object.put("wzName",tm.getWzmc());
			object.put("dengji",tm.getBmdj());
			object.put("wzjc",tm.getWzjc());
			object.put("jldw",tm.getJldw());
			object.put("xhph",tm.getXh());
			object.put("xhgg",tm.getXhgg());
			object.put("zldj",tm.getZldj());
			object.put("gys",tm.getSccj());
			object.put("bzh",tm.getZgf());
			object.put("xxgf",tm.getXxgf());
			object.put("jxxndj",tm.getZytj());
			object.put("dcstxyq",tm.getTssm());
			object.put("jddj","");
			object.put("wxcc",tm.getWxcc());
			object.put("fjxy",tm.getFjxy());
			object.put("fzxs",tm.getFzxs());
			object.put("sfjk",tm.getSfjk());
			object.put("rcl","");
			object.put("bmcl","");
			object.put("sfjdmg",tm.getSfjdmg());
			object.put("jdmgdj",tm.getJdmgdj());
			object.put("kfzbtid",tm.getKfszbtid());
			object.put("kfszbsee",tm.getKfszbsee());
			object.put("smdj",tm.getSmdj());
			object.put("xncs",tm.getXncs());
            object.put("bmzt",tm.getBmzt());
            object.put("partType","元器件");


        }else if(o instanceof TMEStandPartLink){//标准件
			TMEStandPartLink tm = (TMEStandPartLink) o;
			object.put("wzNumber",tm.getWzbm());
			object.put("wzName",tm.getWzmc());
			object.put("dengji",tm.getBmdj());
			object.put("wzjc",tm.getWzjc());
			object.put("jldw",tm.getJldw());
			object.put("xhph",tm.getCl());
			object.put("xhgg",tm.getGg());
			object.put("zldj",tm.getCpdj());
			object.put("gys",tm.getSccj());
			object.put("bzh",tm.getBzh());
			object.put("xxgf","");
			object.put("fjxy",tm.getNbxs());
			object.put("jxxndj",tm.getJxxndj());
			object.put("dcstxyq",tm.getTssm());
			object.put("jddj","");
			object.put("fzxs","");
			object.put("wxcc",tm.getCpxs());
			object.put("sfjk",tm.getSfjk());
			object.put("rcl",tm.getRcl());
			object.put("bmcl",tm.getBmcl());
			object.put("bmzt",tm.getBmzt());
			object.put("sfjdmg","");
			object.put("jdmgdj","");
			object.put("kfzbtid","");
			object.put("kfszbsee","");
			object.put("smdj","");
			object.put("xncs","");
			object.put("partType","标准件");

		}else if(o instanceof TMEMetallicPartLink){//金属材料
			TMEMetallicPartLink tm = (TMEMetallicPartLink) o;
			object.put("wzNumber",tm.getWzbm());
			object.put("wzName",tm.getWzmc());
			object.put("dengji",tm.getBmdj());
			object.put("wzjc",tm.getWzjc());
			object.put("jldw",tm.getJldw());
			object.put("xhph",tm.getPh());
			object.put("xhgg",tm.getGg());
			object.put("zldj","");
			object.put("gys",tm.getSccj());
			object.put("bzh",tm.getCybz());
			object.put("xxgf",tm.getXs());
			object.put("pzggbz",tm.getPzggbz());
			object.put("jxxndj","");
			object.put("dcstxyq",tm.getTssm());
			object.put("jddj",tm.getJd());
			object.put("fzxs",tm.getHsl());
			object.put("sfjk",tm.getSfjk());
			object.put("rcl",tm.getGyzt());
			object.put("bmcl",tm.getGyzt());
			object.put("sfjdmg","");
			object.put("jdmgdj","");
			object.put("kfzbtid","");
			object.put("kfszbsee","");
			object.put("smdj","");
			object.put("xncs","");
            object.put("bmzt",tm.getBmzt());
			object.put("partType","金属材料");


		}else if(o instanceof  TMENonMetallicPartLink){//非金属
			TMENonMetallicPartLink tm = (TMENonMetallicPartLink) o;

			object.put("wzNumber",tm.getWzbm());
			object.put("wzName",tm.getWzmc());
			object.put("dengji",tm.getBmdj());
			object.put("wzjc",tm.getWzjc());
			object.put("jldw",tm.getJldw());
			object.put("xhph",tm.getPh());
			object.put("xhgg",tm.getGg());
			object.put("zldj","");
			object.put("gys",tm.getSccj());
			object.put("bzh",tm.getCybz());
			object.put("xxgf",tm.getXs());
			object.put("jxxndj","");
			object.put("dcstxyq",tm.getTssm());
			object.put("jddj","");
			object.put("fzxs",tm.getHsl());
			object.put("sfjk",tm.getSfjk());
			object.put("rcl","");
			object.put("bmcl","");
			object.put("sfjdmg","");
			object.put("jdmgdj","");
			object.put("kfzbtid","");
			object.put("kfszbsee","");
			object.put("smdj","");
			object.put("xncs","");
			object.put("pzggbz","");
            object.put("bmzt",tm.getBmzt());
			object.put("partType","非金属材料");


		}else if(o instanceof  TMECompoundMaterialPartLink){//复合
			TMECompoundMaterialPartLink tm = (TMECompoundMaterialPartLink) o;

			object.put("wzNumber",tm.getWzbm());
			object.put("wzName",tm.getWzmc());
			object.put("dengji",tm.getBmdj());
			object.put("wzjc",tm.getWzjc());
			object.put("jldw",tm.getJldw());
			object.put("xhph",tm.getPh());
			object.put("xhgg",tm.getGg());
			object.put("zldj","");
			object.put("gys",tm.getSccj());
			object.put("bzh",tm.getCybz());
			object.put("xxgf",tm.getXs());
			object.put("jxxndj","");
			object.put("dcstxyq",tm.getTssm());
			object.put("jddj","");
			object.put("fzxs",tm.getHsl());
			object.put("sfjk",tm.getSfjk());
			object.put("rcl","");
			object.put("bmcl","");
			object.put("sfjdmg","");
			object.put("jdmgdj","");
			object.put("kfzbtid","");
			object.put("kfszbsee","");
			object.put("smdj","");
			object.put("xncs","");
            object.put("bmzt",tm.getBmzt());
			object.put("partType","复合材料");


		}else 	if(o instanceof TMEEleMachinePartLink){//机电材料
			TMEEleMachinePartLink tm = (TMEEleMachinePartLink) o;
			object.put("wzNumber",tm.getWzbm());
			object.put("wzName",tm.getWzmc());
			object.put("dengji",tm.getBmdj());
			object.put("wzjc","");
			object.put("jldw",tm.getJldw());
			object.put("xhph",tm.getPh());
			object.put("xhgg",tm.getXhgg());
			object.put("zldj","");
			object.put("gys",tm.getSccj());
			object.put("bzh",tm.getBzh());
			object.put("xxgf","");
			object.put("jxxndj","");
			object.put("dcstxyq",tm.getTssm());
			object.put("jddj","");
			object.put("fzxs","");
			object.put("sfjk",tm.getSfjk());
			object.put("rcl","");
			object.put("bmcl","");
			object.put("sfjdmg","");
			object.put("jdmgdj","");
			object.put("kfzbtid","");
			object.put("kfszbsee","");
			object.put("smdj","");
			object.put("xncs",tm.getXncs());
            object.put("bmzt",tm.getBmzt());
			object.put("partType","机电材料");


		}else if(o instanceof TMEExpDevicePartLink){//火工品
			TMEExpDevicePartLink tm = (TMEExpDevicePartLink) o;
			object.put("wzNumber",tm.getWzbm());
			object.put("wzName",tm.getWzmc());
			object.put("dengji",tm.getBmdj());
			object.put("wzjc","");
			object.put("jldw",tm.getJldw());
			object.put("xhph","");
			object.put("xhgg","");
			object.put("zldj","");
			object.put("gys",tm.getSccj());
			object.put("bzh",tm.getBzh());
			object.put("xxgf","");
			object.put("jxxndj","");
			object.put("dcstxyq",tm.getTssm());
			object.put("jddj",tm.getZcsm());
			object.put("fzxs","");
			object.put("sfjk","");
			object.put("rcl","");
			object.put("bmcl",tm.getCpdh());
			object.put("sfjdmg","");
			object.put("jdmgdj",tm.getZl());
			object.put("kfzbtid","");
			object.put("kfszbsee","");
			object.put("smdj",tm.getTnt());
			object.put("xncs",tm.getXncs());
            object.put("bmzt",tm.getBmzt());
			object.put("partType","火工品");


		}
	}

	public static Object generateQueryWzGrid(HttpServletRequest request) {
		String wuzileixing = request.getParameter("wuzileixing");
		Map<String,String> queryMap = null;
		if(Tools.isNull(wuzileixing)){
			return "";
		}
		JSONObject result = new JSONObject();
		Class<?> cusclass = null;
		if(wuzileixing.equals("A")){
			queryMap = componentMap;
			cusclass = TMEEleComponentsPartLink.class;

		}else if(wuzileixing.equals("B")){
			queryMap = standardMap;
			cusclass = TMEStandPartLink.class;

		}else if(wuzileixing.equals("C")){
			queryMap = materialMap;
			cusclass = TMEMetallicPartLink.class;

		}else if(wuzileixing.equals("D")){
			queryMap = nonMaterialMap;
			cusclass = TMENonMetallicPartLink.class;
		}else if(wuzileixing.equals("E")){
			queryMap = compoundMap;
			cusclass = TMECompoundMaterialPartLink.class;
		}else if(wuzileixing.equals("F")){
			queryMap = jdclMap;
			cusclass = TMEEleMachinePartLink.class;
		}else if(wuzileixing.equals("G")){
			queryMap = hgpMap;
			cusclass = TMEExpDevicePartLink.class;
		}
		try {
			JSONArray array = new JSONArray();
			GwQuerySpec qs = new GwQuerySpec(cusclass);
			Map<String,String[]> map = request.getParameterMap();
			int i = 1;
			for (String key : map.keySet()) {
				String[] values = map.get(key);
				String value = values[0];
				if(Tools.isNull(value)){
					continue;
				}
				if(!queryMap.containsKey(key)){
					continue;
				}
				if (i > 1) {
					qs.appendAnd();
				}
				qs.appendWhere(queryMap.get(key), ext.ases.techMaterial.gwpersistable.GwQuerySpec.LIKE, "%" + value + "%");
				i++;
			}
			if(i==1){
				return "";
			}
			if(!"G".equals(wuzileixing) && !"F".equals(wuzileixing)){
				qs.appendAnd();
				qs.appendWhere("BMZT", GwQuerySpec.EQUAL, "启用");
			}
			ext.ases.techMaterial.gwpersistable.GwQueryResult qr = ext.ases.techMaterial.gwpersistable.GwPersistenceHelper.manager.find(qs);

			while (qr.hasNext()) {
				Object next = qr.next();
				JSONObject jsonObject = new JSONObject();
				genJsonObject(jsonObject, next);
				array.put(jsonObject);
				if(array.length()>MAX_PAGING){
					break;
				}

			}
			result.put("data", array);
			result.put("totalCount", array.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String getWzbm(Object obj) {
		String wzbm = "";
		if (obj != null)
			try {
				Class class1 = obj.getClass();
				Method method = class1.getMethod("getWzbm", new Class[0]);
				wzbm = (String) method.invoke(obj, new Object[0]);
			} catch (Exception exception) {
				Debug.info(obj.toString() + " has no getWzbm");
				exception.printStackTrace();
			}
		return wzbm;
	}

	public static String replaceGongYiWz(HttpServletRequest request) {
		boolean enforce = wt.session.SessionServerHelper.manager
				.setAccessEnforced(false);
		String params = request.getParameter("params");
		String oid = request.getParameter("oid");
		if(Tools.isNull(params)){
			return "{failure:true,msg:'参数错误'}";
		}
		if(Tools.isNull(oid)){
			return "{failure:true,msg:'参数错误'}";
		}
		FileInputStream fileInputStream = null;
        WTUser currentuser = null;
		try {
            currentuser =  (WTUser) SessionHelper.manager.getPrincipal();
			WTDocument wtDocument = (WTDocument) ReferenceFactory.getObjectbyOid(oid);
			if(!wtDocument.isLatestIteration()){
				wtDocument =  (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberVersion(
						WTDocument.class,
						wtDocument.getNumber(),
						wtDocument.getVersionIdentifier().getValue()
				);
			}
			WTUser modifer = (WTUser) wtDocument.getModifier().getPrincipal();
            SessionHelper.manager.setPrincipal(modifer.getAuthenticationName());

			params = URLDecoder.decode(params,"UTF-8");

            String matchType = request.getParameter("matchType");
            String  matchState = "未匹配";
            if("2".equals(matchType)){
                matchState = "匹配中";
            }
            if("3".equals(matchType)){
                matchState = "已匹配";
            }
            boolean isForceReplae = false;
            if("4".equals(matchType)){
                matchState = "无需匹配";
                if(params.contains("@")){
                	isForceReplae=true;
                    matchState = "已匹配";
                }
            }

            if("2".equals(matchType)||"3".equals(matchType)||isForceReplae) {
                Workable workable = WorkInProcessUtil.checkout(wtDocument);
                WTDocument newDoc  = (WTDocument) WorkInProcessUtil.checkin(workable);

				String newDocVersionOid = VersionCommonHelper.getVR(newDoc);
                String number = wtDocument.getNumber();
                ApplicationData appData = WTDocumentUtil.getPrimaryByDocument(newDoc);
                byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);

                String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "tempDinge" + File.separator + UUID.randomUUID().toString() + File.separator;
                FileUtil.writeBytes(tempFilePath, appData.getFileName(), bytes);
                ApacheZipUtil.decompress(tempFilePath + appData.getFileName(), tempFilePath + number);
                File xmlFile = new File(tempFilePath + number + File.separator + number + ".xml");
                Document docXml = XmlUtility.getDocument(xmlFile);
                Element techElement = XmlUtility.getTechnicsElement(docXml);
                XmlUtility.setAttributeValue(techElement, "version", VersionCommonHelper.getVersion(newDoc));
                String synchTime = String.valueOf(System.currentTimeMillis());

                Map<String, String> needReplace = new HashMap<String, String>();
                List<String> needDelete = new ArrayList<String>();
				Map<String,String> needAdd = new LinkedHashMap<String, String>();
                String newOid = PersistenceCommonHelper.getOid(newDoc);
                String[] ss = params.split("~");
               int i = 1;
                for (String s : ss) {
                    if (s.contains("@")) {
                        String[] vs = s.split("@");
                        if (vs.length >= 3) {
							if("删除标记".equals(vs[1])){
								needDelete.add(vs[0].trim());
							}else if("新增标记".equals(vs[1])){
								String sl = "1";
								if(vs.length>3&&!"".equals(vs[3])){
									sl = vs[3];

								}
								needAdd.put(vs[0].trim(),sl);

							}else{
								needReplace.put(vs[1].trim(), vs[0].trim());
							}
                            GLWzMatchRecord record = new GLWzMatchRecord();
                            record.setKeyId(newOid + "_" + vs[2]+"_"+i);
                            record.setDocOid(newOid);
                            record.setDocNumber(newDocVersionOid);
                            record.setDocVersion(VersionCommonHelper.getVersion(newDoc));
                            record.setOrgWZNumber(vs[2].trim());
                            record.setMatchWZNumber(vs[0].trim());
                            record.setGongYiWZNumber(vs[1].trim());
                            record.setSynchtime(synchTime);
                            record.setCreator(currentuser.getName());
                            CmPersistenceHelper.manager.insert(record);
							i++;
                        }
                    }
                }
                replaceGongYiDingE(techElement, needReplace,needAdd);
                replacePeiTaoTable(techElement, needReplace,needDelete,needAdd);
                removeCanZhuang(techElement, needDelete);

                OutputFormat format = OutputFormat.createCompactFormat();
                format.setEncoding("GBK");
                java.io.StringWriter stringWriter = new java.io.StringWriter();
                XMLWriter writer = new XMLWriter(stringWriter, format);
                writer.write(docXml);
                String xmlStringFormatting = stringWriter.toString();

                Set<Map.Entry<String, String>> entrySet = needReplace.entrySet();
                //boolean exist = DBUtil.existData(GLWzMatchRecord.class,GLWzMatchRecord.DOCOID,oid);
                for (Map.Entry<String, String> entry : entrySet) {
                    String oldValue = "\""+entry.getKey()+"\"";
                    String newValue = "\""+entry.getValue()+"\"";
                    xmlStringFormatting = xmlStringFormatting.replaceAll(oldValue, newValue);
                   /* GLNCPartMapping glncPartMapping = getNcMapping(oldValue);
                    if (glncPartMapping == null) {
                        glncPartMapping = new GLNCPartMapping();
                        glncPartMapping.setKeyId(oldValue);
                        glncPartMapping.setOldPartNumber(oldValue);
                        glncPartMapping.setNewPartNumber(newValue);
                        glncPartMapping.setState("启用");
                        glncPartMapping.setSynchtime(synchTime);
                        CmPersistenceHelper.manager.insert(glncPartMapping);
                    }*/
                }

				for(String delete :needDelete){
					xmlStringFormatting = xmlStringFormatting.replaceAll(delete, "已删除");
				}
				Document newDocument = XmlUtility.getDocument(xmlStringFormatting.getBytes());
                XmlUtility.saveDocument(newDocument, xmlFile);
                boolean compressflag = ApacheZipUtil.compress(tempFilePath + number, tempFilePath + number + ".zip");
                if (compressflag) {
                    fileInputStream = new FileInputStream(new File(tempFilePath + number + ".zip"));
                    WTDocumentUtil.setPrimaryForDocument(newDoc, number + ".zip", fileInputStream);
                }
                newDoc = (WTDocument) PersistenceHelper.manager.refresh(newDoc);
                WorkflowHelper.setIBAValue(newDoc,"matchState",matchState);
            }else{
                WorkflowHelper.setIBAValue(wtDocument,"matchState",matchState);
            }


		}catch (Exception e){
			e.printStackTrace();
			return "{failure:true,msg:'"+e.getLocalizedMessage()+"'}";
		}finally {
			if(fileInputStream!=null){
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(currentuser!=null){
                try {
                    SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
                } catch (WTException e) {
                    e.printStackTrace();
                }

            }

            wt.session.SessionServerHelper.manager
					.setAccessEnforced(enforce);
		}

		return "{success:true}";
	}

	private static void removeCanZhuang(Element techElement, List<String> needDelete) {
		List<Element> allSteps = XmlUtility.getAllSteps(techElement);
		for (Element step : allSteps) {
			Element partElements = step.element("parts");
			if(partElements!=null){
				List<Element> parts = partElements.elements();
				for(Element part :parts){
					if(needDelete.contains(XmlUtility.getAttributeValue(part,"partNumber"))){
						partElements.remove(part);
					}
				}
			}

			List<Element> paces = XmlUtility.getAllPaces(step);
			for (Element pace : paces) {
				Element partElements2 = pace.element("parts");
				if(partElements2!=null){
					List<Element> parts = partElements2.elements();
					for(Element part :parts){
						if(needDelete.contains(XmlUtility.getAttributeValue(part,"partNumber"))){
							partElements2.remove(part);
						}
					}
				}

			}
		}
	}

	private static void replacePeiTaoTable(Element techElement, Map<String, String> needReplace, List<String> needDelete, Map<String,String> needAdd) {
		List<Element> peiTaoListTableElements = XmlUtility.getPeiTaoListTableElements(techElement);
		Element peiTaoTableElement = XmlUtility.getPeiTaoListTableElement(techElement);
		if(peiTaoListTableElements==null) return;
		for(Element element : peiTaoListTableElements){
			String number = XmlUtility.getAttributeValue(element,"number");
			if(needReplace.containsKey(number)) {
				String newNumber =needReplace.get(number);
				JSONObject wzJsonData = getWzJsonData(newNumber);
				XmlUtility.setAttributeValue(element, "number",newNumber);
				XmlUtility.setAttributeValue(element, "chmc", wzJsonData.optString("wzName"));
			}
			if(needDelete.contains(number)){
				peiTaoTableElement.remove(element);


			}
		}

		Set<Map.Entry<String,String>> entrys = needAdd.entrySet();
		for(Map.Entry<String,String> entry:entrys){
			String addNumber = entry.getKey();
			JSONObject jsonObject = getWzJsonData(addNumber);
			Element element = DocumentHelper.createElement("PeiTaoElement");
			XmlUtility.setAttributeValue(element, "number", addNumber);
			XmlUtility.setAttributeValue(element, "name", jsonObject.optString("wzName"));
			XmlUtility.setAttributeValue(element, "MTYPE", jsonObject.optString("partType"));
			XmlUtility.setAttributeValue(element, "useCount", entry.getValue());
			XmlUtility.setAttributeValue(element, "XHPH", jsonObject.optString("xhph"));
			XmlUtility.setAttributeValue(element, "CSIZE",  jsonObject.optString("xhgg"));
			XmlUtility.setAttributeValue(element, "version", "");
			XmlUtility.setAttributeValue(element, "dw", "个");
			XmlUtility.setAttributeValue(element, "comment", "");
			XmlUtility.setAttributeValue(element, "jstj", jsonObject.optString("bzh"));
			XmlUtility.setAttributeValue(element, "gys", jsonObject.optString("gys"));
			XmlUtility.setAttributeValue(element, "partNumber", "");
			XmlUtility.setAttributeValue(element, "zldj",  jsonObject.optString("zldj"));
			peiTaoTableElement.add(element);
		}


	}

	private static void replaceGongYiDingE(Element techElement, Map<String, String> needReplace, Map<String,String> needAdd) {
		Element gyde = XmlUtility.getTechnicsDEElement(techElement);
		if(gyde != null) {
			List<Element> newParts = XmlUtility.getTechnicsGYDENewPart(gyde);
			for (Element element : newParts) {
				String oldNumber = XmlUtility.getAttributeValue(element,"chbm");
				if(needReplace.containsKey(oldNumber)){
					String newNumber = needReplace.get(oldNumber);
					replaceXmlAttris(element,newNumber);
				}
			}
			List<Element> matchParts= XmlUtility.getTechnicsGYDEMatchPart(gyde);
			for (Element element : matchParts) {
				String oldNumber = XmlUtility.getAttributeValue(element,"chbm");
				if(needReplace.containsKey(oldNumber)){
					String newNumber = needReplace.get(oldNumber);
					replaceXmlAttris(element,newNumber);
				}
			}
			//工艺定额（设计资源库）
			List<Element> SjzykNewParts=XmlUtility.getTechnicsSJZYKGYDENewPart(gyde);
			if(null!=SjzykNewParts){
				for (Element element : SjzykNewParts) {
					String oldNumber = XmlUtility.getAttributeValue(element,"chbm");
					if(needReplace.containsKey(oldNumber)) {
						String newNumber = needReplace.get(oldNumber);
						replaceXmlAttris(element,newNumber);


						}
					}
				}
				//工艺定额（设计资源库）
				List<Element> sjzykMatchParts = XmlUtility.getTechnicsSJZYKGYDEMatchPart(gyde);
				if (null != sjzykMatchParts) {
					for (Element element : sjzykMatchParts) {
						String oldNumber = XmlUtility.getAttributeValue(element, "chbm");
						if (needReplace.containsKey(oldNumber)) {
							String newNumber = needReplace.get(oldNumber);
							replaceXmlAttris(element, newNumber);


						}
					}
				}
				List<Element> ycl = XmlUtility.getTechnicsYCLDE(gyde);
				for (Element element : ycl) {
					String oldNumber = XmlUtility.getAttributeValue(element, "chbm");
					if (needReplace.containsKey(oldNumber)) {
						String newNumber = needReplace.get(oldNumber);
						replaceXmlAttris(element, newNumber);

					}
				}
				List<Element> zyclde = XmlUtility.getTechnicsZYCLDE(gyde);
				for (Element element : zyclde) {
					String oldNumber = XmlUtility.getAttributeValue(element, "chbm");
					if (needReplace.containsKey(oldNumber)) {
						String newNumber = needReplace.get(oldNumber);
						replaceXmlAttris(element, newNumber);
					}
				}
				List<Element> sjyclde = XmlUtility.getTechnicsSJYCLDE(gyde);
				for (Element element : sjyclde) {
					String oldNumber = XmlUtility.getAttributeValue(element, "chbm");
					if (needReplace.containsKey(oldNumber)) {
						String newNumber = needReplace.get(oldNumber);
						replaceXmlAttris(element, newNumber);
					}
				}
			}
			Element clde = XmlUtility.getTechnicsCLDEElement(techElement);
			if (clde != null) {
				List<Element> ycl = XmlUtility.getTechnicsYCLDE(clde);
				for (Element element : ycl) {
					String oldNumber = XmlUtility.getAttributeValue(element, "chbm");
					if (needReplace.containsKey(oldNumber)) {
						String newNumber = needReplace.get(oldNumber);
						replaceXmlAttris(element, newNumber);

					}
				}
				List<Element> zyclde = XmlUtility.getTechnicsZYCLDE(clde);
				for (Element element : zyclde) {
					String oldNumber = XmlUtility.getAttributeValue(element, "chbm");
					if (needReplace.containsKey(oldNumber)) {
						String newNumber = needReplace.get(oldNumber);
						replaceXmlAttris(element, newNumber);
					}
				}
				List<Element> sjyclde = XmlUtility.getTechnicsSJYCLDE(clde);
				for (Element element : sjyclde) {
					String oldNumber = XmlUtility.getAttributeValue(element, "chbm");
					if (needReplace.containsKey(oldNumber)) {
						String newNumber = needReplace.get(oldNumber);
						replaceXmlAttris(element, newNumber);
					}
				}
				List<Element> newParts = XmlUtility.getTechnicsGYDENewPart(clde);
				for (Element element : newParts) {
					String oldNumber = XmlUtility.getAttributeValue(element, "chbm");
					if (needReplace.containsKey(oldNumber)) {
						String newNumber = needReplace.get(oldNumber);
						replaceXmlAttris(element, newNumber);
					}
				}
				//工艺定额（设计资源库）
				List<Element> SjzykNewParts = XmlUtility.getTechnicsSJZYKGYDENewPart(clde);
				if (null != SjzykNewParts) {
					for (Element element : SjzykNewParts) {
						String oldNumber = XmlUtility.getAttributeValue(element, "chbm");
						if (needReplace.containsKey(oldNumber)) {
							String newNumber = needReplace.get(oldNumber);
							replaceXmlAttris(element, newNumber);


					}
				}
			}

		}


		if(clde != null &&!needAdd.isEmpty()) {
			Element partElements = clde.element("ZYCLDE");
			Element procedureElements = techElement.element("steps");
			List steps = procedureElements.elements();
			Element firststep = null;
			Element firstpace = null;
			if(!steps.isEmpty()){
				firststep = (Element) steps.get(0);
				Element paces = firststep.element("paces");
				if(paces!=null){
					List paceList = paces.elements();
					if(!paceList.isEmpty()){
						firstpace = (Element) paceList.get(0);
					}
				}

			}
			Set<Map.Entry<String,String>> entrys = needAdd.entrySet();
			for(Map.Entry<String,String> entry:entrys){
				String addNumber = entry.getKey();
				JSONObject jsonObject = getWzJsonData(addNumber);
				Element element = DocumentHelper.createElement("zycldeRecord");
				XmlUtility.setAttributeValue(element, "parentNumber","");//"上级图号"
				XmlUtility.setAttributeValue(element, "number",addNumber);//"图号"
				XmlUtility.setAttributeValue(element, "chbm", addNumber);//"存货编码"
				XmlUtility.setAttributeValue(element, "chmc", jsonObject.optString("wzName"));//"存货名称"
				XmlUtility.setAttributeValue(element, "sl", entry.getValue());//"使用数量"
				XmlUtility.setAttributeValue(element, "dw2", "个");//"单位"
				XmlUtility.setAttributeValue(element, "xhph", jsonObject.optString("xhph"));//"型号牌号"
				XmlUtility.setAttributeValue(element, "gg", jsonObject.optString("xhgg"));//"规格"
				XmlUtility.setAttributeValue(element, "jstj", jsonObject.optString("bzh"));//"技术条件"
				XmlUtility.setAttributeValue(element, "bzh", jsonObject.optString("bzh"));
				XmlUtility.setAttributeValue(element, "sccj", jsonObject.optString("gys"));//"生产厂家"
				XmlUtility.setAttributeValue(element, "dw","个");//"主计量单位"
				XmlUtility.setAttributeValue(element, "fjtj", "");//"附加条件"
				XmlUtility.setAttributeValue(element, "lwgggccc", "");//"螺纹规格/公称尺寸"
				XmlUtility.setAttributeValue(element, "jxxndj", jsonObject.optString("jxxndj"));//"机械性能等级"
				XmlUtility.setAttributeValue(element, "zldj", jsonObject.optString("zldj"));//"质量等级"
				XmlUtility.setAttributeValue(element, "fzxs",  jsonObject.optString("fzxs"));//"封装形式"
				XmlUtility.setAttributeValue(element, "jddj",  jsonObject.optString("jddj"));//"精度等级"
				XmlUtility.setAttributeValue(element, "wzlb", jsonObject.optString("partType"));//"物资类别"
				XmlUtility.setAttributeValue(element, "wzlbbm", "");//"物资类别编码"
				XmlUtility.setAttributeValue(element, "gyztrcl", jsonObject.optString("bmcl"));//"产品代号"
				XmlUtility.setAttributeValue(element, "zl", jsonObject.optString("jdmgdj"));//"重量"
				XmlUtility.setAttributeValue(element, "zcsm", jsonObject.optString("jddj"));//"贮存寿命"
				XmlUtility.setAttributeValue(element, "tnt", jsonObject.optString("smdj"));//"TNT"
				XmlUtility.setAttributeValue(element, "dcstxyq", jsonObject.optString("dcstxyq"));//"电参数特选要求"
				XmlUtility.setAttributeValue(element, "comment", "");//备注
				XmlUtility.setAttributeValue(element, "dataFrom", "sjzyk");//数据来源
				XmlUtility.setAttributeValue(element, "option", "历史匹配新增");

				partElements.add(element);

				Element czPart = DocumentHelper.createElement("QMPartInfo");
				XmlUtility.setAttributeValue(czPart, "partNumber", addNumber);
				XmlUtility.setAttributeValue(czPart, "partName", jsonObject.optString("wzName"));
				XmlUtility.setAttributeValue(czPart, "ZCMARK","Z");
				XmlUtility.setAttributeValue(czPart, "XHPH",jsonObject.optString("xhph"));
				XmlUtility.setAttributeValue(czPart, "GG",jsonObject.optString("xhgg"));
				XmlUtility.setAttributeValue(czPart, "bzh",jsonObject.optString("bzh"));
				XmlUtility.setAttributeValue(czPart, "JSTJ",jsonObject.optString("bzh"));
				XmlUtility.setAttributeValue(czPart, "DW", "个");
				XmlUtility.setAttributeValue(czPart, "DW2", "个");
				XmlUtility.setAttributeValue(czPart, "occId",addNumber);
				XmlUtility.setAttributeValue(czPart, "useCount",entry.getValue());
				if(firstpace!=null) {
					XmlUtility.addParts(firstpace, czPart);
				}else{
					XmlUtility.addParts(firststep, czPart);
				}


			}
		}



	}


	private static void replaceXmlAttris(Element element, String newNumber) {
		newNumber = newNumber.trim();
		XmlUtility.setAttributeValue(element, "number", newNumber);
		XmlUtility.setAttributeValue(element, "chbm",newNumber);
		JSONObject wzJsonData = getWzJsonData(newNumber);
		XmlUtility.setAttributeValue(element, "chmc", wzJsonData.optString("wzName"));
		XmlUtility.setAttributeValue(element, "xhph", wzJsonData.optString("xhph"));
		XmlUtility.setAttributeValue(element, "xh", wzJsonData.optString("xhph"));
		XmlUtility.setAttributeValue(element, "gg", wzJsonData.optString("xhgg"));
		XmlUtility.setAttributeValue(element, "jstj", wzJsonData.optString("bzh"));
		XmlUtility.setAttributeValue(element, "bzh", wzJsonData.optString("bzh"));
		XmlUtility.setAttributeValue(element, "fzxs", wzJsonData.optString("fzxs"));
		XmlUtility.setAttributeValue(element, "zldj", wzJsonData.optString("zldj"));
		XmlUtility.setAttributeValue(element, "jddj", wzJsonData.optString("jddj"));
		XmlUtility.setAttributeValue(element, "jxxndj", wzJsonData.optString("jxxndj"));
		XmlUtility.setAttributeValue(element, "sccj", wzJsonData.optString("gys"));
		XmlUtility.setAttributeValue(element, "gyztrcl", wzJsonData.optString("rcl"));
		XmlUtility.setAttributeValue(element, "dcstxyq", wzJsonData.optString("dcstxyq"));
		XmlUtility.setAttributeValue(element, "gyztrcl", wzJsonData.optString("bmcl"));

	}


	public static GLNCPartMapping getNcMapping(String oldNumber) throws Exception {
		CmQuerySpec querySpec = new CmQuerySpec(GLNCPartMapping.class);
		querySpec.appendWhere(GLNCPartMapping.OLDPARTNUMBER,CmQuerySpec.EQUAL,oldNumber);
		CmQueryResult qr =CmPersistenceHelper.manager.find(querySpec);
		if(qr.hasNext()){
			return (GLNCPartMapping)qr.next();
		}
		return null;
	}

	public static Map<String,String> getGLWzMatchRecords(String oid) throws Exception {
		Map<String,String> map = new HashMap<String, String>();
		CmQuerySpec querySpec = new CmQuerySpec(GLWzMatchRecord.class);
		querySpec.appendWhere(GLWzMatchRecord.DOCNUMBER,CmQuerySpec.EQUAL,oid);
		//querySpec.appendOrderBy(GLWzMatchRecord.SYNCHTIME,true);
		CmQueryResult qr =CmPersistenceHelper.manager.find(querySpec);
		while(qr.hasNext()){
			GLWzMatchRecord record =  (GLWzMatchRecord)qr.next();
			map.put(record.getMatchWZNumber(),record.getOrgWZNumber());
		}
		return map;
	}

	public static String  getGLWzMatchMapping(String oldPartNumber) throws Exception {
		CmQuerySpec querySpec = new CmQuerySpec(GLNCPartMapping.class);
		querySpec.appendWhere(GLNCPartMapping.OLDPARTNUMBER,CmQuerySpec.EQUAL,oldPartNumber);
		CmQueryResult qr =CmPersistenceHelper.manager.find(querySpec);
		if(qr.hasNext()){
			GLNCPartMapping record =  (GLNCPartMapping)qr.next();
			return record.getNewPartNumber();
		}
		return "";
	}

	public static JSONObject generatePeiTaoTableGrid(String  oid) {
		JSONObject result = new JSONObject();
		WTUser currentuser = null;
		boolean enforce = wt.session.SessionServerHelper.manager
				.setAccessEnforced(false);
		try{
			currentuser = (WTUser) SessionHelper.manager.getPrincipal();

			JSONArray array = new JSONArray();
			WTDocument doc = (WTDocument) ReferenceFactory.getObjectbyOid(oid);
			WTUser modifer = (WTUser) doc.getModifier().getPrincipal();
			SessionHelper.manager.setPrincipal(modifer.getAuthenticationName());
			String number = doc.getNumber();
			ApplicationData appData = WTDocumentUtil.getPrimaryByDocument(doc);
			byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);

			String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "tempDinge" + File.separator + UUID.randomUUID().toString() + File.separator;
			FileUtil.writeBytes(tempFilePath, appData.getFileName(), bytes);
			ApacheZipUtil.decompress(tempFilePath + appData.getFileName(), tempFilePath + number);
			File xmlFile = new File(tempFilePath + number + File.separator + number + ".xml");
			Document docDom = XmlUtility.getDocument(xmlFile);
			Element techElement = XmlUtility.getTechnicsElement(docDom);
			List<Element> list =XmlUtility.getPeiTaoListTableElements(techElement);
			for (Element element : list) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("partNumber", element.attributeValue("number"));
				jsonObject.put("partName", element.attributeValue("name"));
				jsonObject.put("useCount", element.attributeValue("useCount"));
				jsonObject.put("dw", element.attributeValue("dw"));
				jsonObject.put("MTYPE", element.attributeValue("MTYPE"));
				jsonObject.put("XHPH", element.attributeValue("XHPH"));
				jsonObject.put("CSIZE", element.attributeValue("CSIZE"));
				jsonObject.put("comment", element.attributeValue("comment"));
				jsonObject.put("gys", element.attributeValue("gys"));
				array.put(jsonObject);

			}

			result.put("data", array);
			result.put("totalCount", array.length());
			FileUtil.deleteFile(new File(tempFilePath));

		}catch (Exception e){
			e.printStackTrace();
		}finally {
			if(currentuser!=null){
				try {
					SessionHelper.manager.setPrincipal(currentuser.getAuthenticationName());
				} catch (WTException e) {
					e.printStackTrace();
				}

			}
			wt.session.SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return result;
	}
}
