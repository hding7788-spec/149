package ext.casc.mpm.process;

import com.glaway.mpm.constants.TypeNameConstants;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.mpmresource.helper.MPMResourceHelper;
import com.glaway.mpm.util.*;
import com.glaway.mpm.util.IBAHelper;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.common.PartCommonHelper;
import ext.casc.fileprint.FilePrintUtil2;
import ext.casc.integrate.util.BomUtil;
import ext.casc.mpm.GyCsServerHelper;
import ext.casc.mpm.route.ProcedureRectangleUnit;
import ext.casc.mpm.route.ProcedureRectangleUnitUtil;
import ext.casc.part.CSCPart;
import ext.casc.part.mvc.builder.GenerateMatchHistoryJson;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.*;
import ext.casc.version.VersionCommonHelper;
import ext.sast.common.fc.CmPersistable;
import ext.sast.common.fc.CmPersistenceHelper;
import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONObject;
import org.xml.sax.SAXException;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.Workable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UID;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TechnicsGenerator {
    public static  String TECHNICSTYPE = "technicsType";//*必填 下拉 工艺类型：从typesMap中取中文名称
    public static  String ZFFLAG = "ZFFLAG";//*必填 下拉 主辅工艺：枚举值Z、F
    public static  String DEPT = "DEPT";//*必填 下拉部门：从depts获取
    public static  String PPLANTYPE = "PPLANTYPE";//*必填 下拉 工艺文件类别 枚举值：正式工艺文件、临时工艺文件
    public static  String LINSHINUM = "LINSHINUM";//非必填，PPLANTYPE为临时工艺才展示该栏位，填写顺序号
    public static  Map<String, String> typesMap = null;
    public static  Map<String, String> pplanIdMap = null;
    public static  List<String> depts = null;
    public static   Set<String> setPartIbas = new HashSet<String>();
    public static  String GYZS_CHENGXUHAO = "程序号";
    public static  String GYZS_SHOUDONGHANJIE = "手动焊接";
    public static  String GYZS_XIAXIANGONGZHUANG = "下陷工装";
    public static  String GYZS_GONGXUMOBAN = "工序模板";
    public static  final String OPEN_PARAM = "【";
	public static  final String PARAM_SPIT = "：";
    public static  final String CLOSE_PARAM = "】";
	/*public static   Map<String,GLProcessParams> PARAMS_MEIJU = new HashMap<String,GLProcessParams>();
	public static   Map<String,GLProcessParams> PARAMS_GONGXUTEMPLATE = new HashMap<String,GLProcessParams>();
	public static   Map<String,GLProcessParams> PARAMS_GONGZHUANG = new HashMap<String,GLProcessParams>();

*/
    static {
        typesMap = ProcessEditorToWCIntfRMI.getMPMPPlanSubTypes();
        pplanIdMap = ProcessEditorToWCIntfRMI.getPPlanID();
        depts = MPMResourceHelper.getAllMPMPlant();
        setPartIbas.add("PHASE_CODE");
        setPartIbas.add("PINDEX");
        setPartIbas.add("MINDEX");
        setPartIbas.add("KEYCOMPONENT");
        setPartIbas.add("CINDEX");
        setPartIbas.add("CMAT_UP");
        setPartIbas.add("CMAT_DOWN");
		/*List<GLProcessParams> list = ProcessUtil.queryGLProcessParams("枚举参数", "","");
		for(GLProcessParams params:list){
			PARAMS_MEIJU.put(params.getGyName(),params);
		}

		List<GLProcessParams> zhishiList = ProcessUtil.queryGLProcessParams("知识参数", "","");
		for(GLProcessParams params:zhishiList){
			if("工序模板映射".equals(params.getKnowledgeType())){
				PARAMS_GONGXUTEMPLATE.put(params.getGyName(),params);
			}else if("工装".equals(params.getKnowledgeType())){
				PARAMS_GONGZHUANG.put(params.getGyName(),params);
			}
		}*/


	}
	public static String technicsGenerator(List<WTPart> parts,Map<String,Object> attris) {
        StringBuilder sb = new StringBuilder("");
		for(WTPart p:parts){
		    try {
                technicsGenerator(p, attris);
            }catch (Exception e){
                sb.append(p.getNumber());
                sb.append("生成工艺异常信息："+e.getLocalizedMessage());
                e.printStackTrace();
				if("已存在正式主工艺文件，不能重复提交或生成。".equals(e.getLocalizedMessage())){
					break;
				}
            }
		}
		if(sb.length()==0){
		    return "";
        }else{
            sb.append("零件的工艺生成失败！");
            return sb.toString();
        }
	}


    public static String technicsGenerator(WTPart part,Map<String,Object> attris)throws Exception {
        InputStream input = null;
        try {
            String partNumber = part.getNumber();
            //String partVersion = part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue();
            Map<String, String> params = new HashMap<String, String>();
            params.put("partNumber", partNumber);
            //params.put("partVersion", partVersion);
			HashMap<String,WTDocument> needRevise = (HashMap<String,WTDocument>)attris.get("needRevise");
            List<GLProcessParamValues> paramValues = GyCsServerHelper.queryProcessParamInstances(params);
            if (!paramValues.isEmpty()) {
                ReferenceFactory rf = new ReferenceFactory();
                String templateId = paramValues.get(0).getTemplateId();
                WTDocument templateDoc = (WTDocument) rf.getReference(templateId).getObject();
				if(!templateDoc.isLatestIteration()){
					templateDoc = (WTDocument) rf.getReference("VR:wt.doc.WTDocument:"+templateDoc.getBranchIdentifier()).getObject();
				}
				String technicsNumber ="";

				if(needRevise.containsKey(partNumber)){
					technicsNumber = needRevise.get(partNumber).getNumber();
				}else{
					technicsNumber = idGenerator();
				}

				String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "gyParamTemplates" + File.separator + technicsNumber;
				FileUtil.deleteSubFile(tempFilePath);
				File tempFile = new File(tempFilePath);
                File xmlFile = null;

                tempFile.mkdirs();
                String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(templateDoc, tempFilePath);
                ApacheZipUtil.decompress(tempFilePath +File.separator+ zipFileName, tempFilePath);

                FileUtil.deleteFile(tempFilePath +File.separator+ zipFileName);


                File newXmlFile = new File(tempFilePath+File.separator+technicsNumber+".xml");
                String subFileName = templateDoc.getName();
                xmlFile = new File(tempFilePath + File.separator + subFileName + ".xml");
				xmlFile =getRealFile(tempFilePath,xmlFile);
				if(newXmlFile.exists()){
					FileUtil.deleteFile(newXmlFile);
				}


				xmlFile.renameTo(newXmlFile);

                Document doc = XmlUtility.getDocument(newXmlFile);
                Element techElement = XmlUtility.getTechnicsElement(doc);
                String technicsType = (String)attris.get(TECHNICSTYPE);
                String pplantype =  (String)attris.get(PPLANTYPE);
                String zfflag =  (String)attris.get(ZFFLAG);
                String tempNum =  (String)attris.get(LINSHINUM);
                if(Tools.isNull(tempNum)){
                    tempNum="1";
                }

                String pplanNumber = "Rz/"+part.getNumber()+zfflag+pplanIdMap.get(technicsType);
                if("临时工艺文件".equals(pplantype)){
                    pplanNumber = pplanNumber+"("+tempNum+")";
                }
                String pplanName = technicsType+"规程";
                String dept =(String) attris.get(DEPT);
                if(Tools.isNull(dept)){
                    dept = ProcessEditorToWCIntfRMI.getUsertechnicsGroupNameRMI();
                }

				WTDocument document = null;

				if(needRevise.containsKey(partNumber)){
					WTDocument oldDoc = needRevise.get(partNumber);
					Workable workable = WorkInProcessUtil.checkout(oldDoc);
					document  = (WTDocument) WorkInProcessUtil.checkin(workable);
					XmlUtility.setAttributeValue(techElement, "version", VersionCommonHelper.getVersion(document));
				}else{
					XmlUtility.setAttributeValue(techElement, "version", "space.1");
				}
                String technicsName = pplanName+"("+pplanNumber+")";
                XmlUtility.setAttributeValue(techElement, "technicsName", technicsName);
                XmlUtility.setAttributeValue(techElement, "PPLANID", pplanIdMap.get(technicsType));
                XmlUtility.setAttributeValue(techElement, "pplanNumber", pplanNumber);
                XmlUtility.setAttributeValue(techElement, "pplanName", pplanName);
                XmlUtility.setAttributeValue(techElement, "PPLANTYPE", pplantype);
                XmlUtility.setAttributeValue(techElement, "ZFFLAG", zfflag);
                XmlUtility.setAttributeValue(techElement, "DEPT", dept);
                XmlUtility.setAttributeValue(techElement, "technicsNumber", technicsNumber);
                XmlUtility.setAttributeValue(techElement, "PPLANID", pplanIdMap.get(technicsType));
                XmlUtility.setAttributeValue(techElement, "technicsType", technicsType);


                XmlUtility.setAttributeValue(techElement, "partNumber", part.getNumber());
                XmlUtility.setAttributeValue(techElement, "partName", part.getName());
                XmlUtility.setAttributeValue(techElement, "partVersion", WTUtil.getVersion(part));
                XmlUtility.setAttributeValue(techElement, "partOid", part.getPersistInfo().getObjectIdentifier().getId()+"");

                for(String s:setPartIbas){
                    setXmlAttriByIBA(part,s,techElement);

                }


                //WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
                List<String>  userInfos  =  ProcessEditorToWCIntfRMI.getCurrentUserInfoRMI();
                XmlUtility.setAttributeValue(techElement, "creator",userInfos.get(0));
                XmlUtility.setAttributeValue(techElement, "creatorOid", userInfos.get(1));
                XmlUtility.setAttributeValue(techElement, "creatorDisplay", userInfos.get(2));

                Element gyde = XmlUtility.getTechnicsDEElement(techElement);
                if(gyde != null) {
                    List<Element> newParts = XmlUtility.getTechnicsGYDENewPart(gyde);
                    for (Element element : newParts) {
                        XmlUtility.setAttributeValue(element, "parentNumber", XmlUtility.getAttributeValue(techElement, "partNumber"));
                    }
                    //工艺定额（设计资源库）
                    List<Element> SjzykNewParts=XmlUtility.getTechnicsSJZYKGYDENewPart(gyde);
                    if(null!=SjzykNewParts){
                        for (Element element : SjzykNewParts) {
                            XmlUtility.setAttributeValue(element, "partNumber", XmlUtility.getAttributeValue(techElement, "partNumber"));
                        }
                    }
                }



                Map<String,String> replaces = new LinkedHashMap<String, String>();


				Map<String,GLProcessParams> useMeiJus = new HashMap<String,GLProcessParams>();
				Map<String,GLProcessParams> useGongxuTemp = new HashMap<String,GLProcessParams>();
				Map<String,GLProcessParams> useGongZhuang = new HashMap<String,GLProcessParams>();

				Map<String,GLProcessParamValues> paramValuesMap  = new HashMap<String,GLProcessParamValues>();
				List<GLProcessParams> usePuTongZhishi = new ArrayList<GLProcessParams>();
				List<GLProcessParams> peiTaoZhishi = new ArrayList<GLProcessParams>();
				List<GLProcessParamDefinition> processParamDefinitions = GyCsServerHelper.queryGLProcessParamDefinition(templateId);

				Map<String,GLProcessParamValues> valuesMap = new HashMap<>();
				for(GLProcessParamValues pv:paramValues){
					valuesMap.put(pv.getGyParamName(),pv);
				}
				for(GLProcessParamDefinition glProcessParamDefinition:processParamDefinitions){

					GLProcessParams paramsDef= GyCsServerHelper.getProcessParamDefinitionByName(glProcessParamDefinition.getGyParamName(),true);
					StringBuilder newGysc = new StringBuilder("【");
					StringBuilder oldGysc = new StringBuilder("【");
					oldGysc.append(glProcessParamDefinition.getGyParamName()).append("：").append("】");

					if(paramsDef!=null){
						if("枚举参数".equals(paramsDef.getParameterCategory())){
							useMeiJus.put(glProcessParamDefinition.getGyParamName(),paramsDef);
							replaces.put(oldGysc.toString(), "");
						}else if("知识参数".equals(paramsDef.getParameterCategory())){
							if("工序模板映射".equals(paramsDef.getKnowledgeType())){
								useGongxuTemp.put(glProcessParamDefinition.getGyParamName(),paramsDef);
								replaces.put(oldGysc.toString(), "");
							}else if("工装".equals(paramsDef.getKnowledgeType())){
								useGongZhuang.put(glProcessParamDefinition.getGyParamName(),paramsDef);
								replaces.put(oldGysc.toString(), "");
							}else if("普通知识".equals(paramsDef.getKnowledgeType())){
								usePuTongZhishi.add(paramsDef);
							}else if("配套表".equals(paramsDef.getKnowledgeType())){
								peiTaoZhishi.add(paramsDef);
								replaces.put(oldGysc.toString(), "");
							}
						}
					}
					GLProcessParamValues pv = valuesMap.get(glProcessParamDefinition.getGyParamName());
					if(pv==null)  continue;

					paramValuesMap.put(pv.getGyParamName(), pv);
					if(!"/".equals(pv.getParamValue())) {
						newGysc.append(pv.getGyParamName()).append("：").append(pv.getParamValue());
						if (paramsDef != null && !Tools.isNull(paramsDef.getUnit())) {
							newGysc.append(paramsDef.getUnit());
						}
						if (!Tools.isNull(pv.getGongChengZhi())) {
							newGysc.append("；");
							newGysc.append("公称值：");
							newGysc.append(pv.getGongChengZhi());

						}
						if (!Tools.isNull(pv.getShangPianCha())) {
							newGysc.append("；");
							newGysc.append("上偏差：");
							newGysc.append(pv.getShangPianCha());
						}
						if (!Tools.isNull(pv.getXiaPianCha())) {
							newGysc.append("；");
							newGysc.append("下偏差：");
							newGysc.append(pv.getXiaPianCha());
						}
						if (!Tools.isNull(pv.getJiZhun1())) {
							newGysc.append("；");
							newGysc.append("基准1：");
							newGysc.append(pv.getJiZhun1());
						}
						if (!Tools.isNull(pv.getJiZhun2())) {
							newGysc.append("；");
							newGysc.append("基准2：");
							newGysc.append(pv.getJiZhun2());
						}
						if (!Tools.isNull(pv.getJiZhun3())) {
							newGysc.append("；");
							newGysc.append("基准3：");
							newGysc.append(pv.getJiZhun3());
						}

						newGysc.append("】");

						replaces.put(oldGysc.toString(), newGysc.toString());

						if (paramsDef != null && !Tools.isNull(paramsDef.getUnit())) {
							StringBuilder oldGysc2 = new StringBuilder("【");
							oldGysc2.append(pv.getGyParamName()).append("：").append(paramsDef.getUnit()).append("】");
							replaces.put(oldGysc2.toString(), newGysc.toString());
						}
						if (paramsDef != null && "是".equals(paramsDef.getIsOnlyValue())) {
							if (!Tools.isNull(paramsDef.getUnit())) {
								replaces.put(oldGysc.toString(), pv.getParamValue() + paramsDef.getUnit());
							} else {
								replaces.put(oldGysc.toString(), pv.getParamValue());
							}
						}
					}else{
						if (paramsDef != null && !Tools.isNull(paramsDef.getUnit())) {
							StringBuilder oldGysc2 = new StringBuilder("【");
							oldGysc2.append(pv.getGyParamName()).append("：").append(paramsDef.getUnit()).append("】");
							replaces.put(oldGysc2.toString(),"");
						}
						replaces.put(oldGysc.toString(), "");

					}
				}



				for(GLProcessParams putongParam:usePuTongZhishi){
					String knowledgeName = putongParam.getGyName();
					String matchKnowledgeName = "【"+knowledgeName+"】";
					String matchKnowledgeName2 = "【"+knowledgeName+"：】";
					String newGycs = processCommonZhiShiCanshu(putongParam,paramValuesMap);
					if(!Tools.isNull(newGycs)){
						//newGycs = "【"+knowledgeName+"："+newGycs+"】";
						replaces.put(matchKnowledgeName,newGycs);
						replaces.put(matchKnowledgeName2,newGycs);
					}
				}

                //【有效电流：】






                List stepList = XmlUtility.getAllSteps(techElement);

				List<String> removeSteps = new ArrayList<String>();
				List<String> removeStepNumbers = new ArrayList<String>();
                for (Iterator it = stepList.iterator(); it.hasNext(); ) {
                    Element stepElement = (Element) it.next();
                    Element procedureContent = stepElement.element("procedureContent");
                    if(procedureContent!=null){
                    	String text =   procedureContent.getText();
                        if(text!=null){
							boolean isDelete = false;
							for(String key :useMeiJus.keySet()){
								String sfregex = "【"+key+".*?】";
								Pattern sfpattern = Pattern.compile(sfregex);
								Matcher sfMatcher = sfpattern.matcher(text);
								while (sfMatcher.find()) {
									String sfMatched = sfMatcher.group();
									String bsoID = stepElement.attributeValue("bsoID");
									String stepNumber = stepElement.attributeValue("stepNumber");
									isDelete = processGongxuShiFou(stepElement,useMeiJus.get(key),sfMatched,paramValuesMap);
									if(isDelete){
										removeSteps.add(bsoID);
										removeStepNumbers.add(stepNumber);
										break;
									}else{
										String newText = text.replaceAll(sfMatched,"");
										procedureContent.setText(newText);
									}
								}
								if(isDelete){
									break;
								}
							}

                            if(!isDelete){
								for(String key :useGongxuTemp.keySet()){
									Pattern  gongxuMBpattern = Pattern.compile("【"+key+"：】");
									Matcher templateMatcher = gongxuMBpattern.matcher(text);
									boolean isFind = false;
									while (templateMatcher.find()) {
										//String gxTpMatched = templateMatcher.group();
										processGongxuTemplate(stepElement,paramValuesMap,useGongxuTemp.get(key),replaces,tempFilePath);
										isFind = true;
										break;
									}
									if(isFind){
										break;
									}
								}

                            }
                        }
                    }
                }

				//删除工艺状态表
				List<Element> stateTables = XmlUtility.getTechnicsStateTables(techElement);
				if(stateTables!=null) {
					for (Element stateTable : stateTables) {
						Element gyztE = stateTable.element("gyzt");
						String gyzt = gyztE.getText();
						for(String key :useMeiJus.keySet()) {
							String sfregex = "【" + key + ".*?】";
							Pattern sfpattern = Pattern.compile(sfregex);
							Matcher sfMatcher = sfpattern.matcher(gyzt);
							while (sfMatcher.find()) {
								String sfMatched = sfMatcher.group();
								 processStateTableShiFou(stateTable,useMeiJus.get(key),sfMatched,paramValuesMap);
							}
						}
					}
				}

				List<WTPartUsageLink>  childMParts = PartCommonHelper.getWTPartUsageLinkByRoleA(part);
				List<GLProcessOtherKnowledge>  processOtherKnowledges =  processGongYiDingE(techElement,paramValuesMap);
				List<GLProcessKnowledge>  canZhuangProcessKnowledges =  processPeiTaoKnowledge(techElement,paramValuesMap,technicsType,peiTaoZhishi);
				if(!canZhuangProcessKnowledges.isEmpty()||!processOtherKnowledges.isEmpty()||!childMParts.isEmpty()){
					processCanZhuang(techElement,canZhuangProcessKnowledges,processOtherKnowledges,childMParts);
				}
				processPeiTaoList(techElement,childMParts);

				processGongZhuang(techElement,paramValuesMap,technicsType,useGongZhuang);


				//if(removeSteps.size()>0){
					processGongXuIndexAndRoute(techElement,tempFilePath);
				//}

				OutputFormat format = OutputFormat.createCompactFormat();
				format.setEncoding("GBK");
				java.io.StringWriter stringWriter = new java.io.StringWriter();
				XMLWriter writer = new XMLWriter(stringWriter, format);
				writer.write(doc);
				String xmlStringFormatting = stringWriter.toString();
				Set<Map.Entry<String, String>> entrySet = replaces.entrySet();
				for(Map.Entry<String, String> entry : entrySet){
					String oldValue = entry.getKey();
					String newValue = entry.getValue();
					xmlStringFormatting  = xmlStringFormatting.replaceAll(oldValue,newValue);
				}

				Document newDocument = XmlUtility.getDocument(xmlStringFormatting.getBytes());

                XmlUtility.saveDocument(newDocument, newXmlFile);

				deleteTemplateFile(tempFilePath);

                ApacheZipUtil.compress(tempFilePath , tempFilePath +".zip");
				if(needRevise.containsKey(partNumber)){

				}else{
					String folderPath = LoadConfig.getInstance().getTechnicsDocPrefixPath()+technicsType;
					String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
					String doctype = null;

					for(int j=0;j<technicsTypes[1].length;j++){
						if(technicsTypes[1][j].equals(technicsType)){
							doctype = technicsTypes[5][j];
							break;
						}
					}
					if("SOPDoc".equals(doctype)){
						folderPath = part.getFolderPath().trim();
						String[] str = folderPath.split("/");
						folderPath = SopConstants.SOP_FOLDOR_PROCESS+str[3]+"/"+str[4];
					}

					//如果doctype为空，则为报表类工艺文件
					if(doctype == null) {
						doctype = LoadConfig.getInstance().getReportTechnicsType();
						folderPath = LoadConfig.getInstance().getReportTechnicsFolderPath();
					}
					document = WTDocumentUtil.createDocument(technicsNumber,technicsName, part.getContainer(), folderPath, LoadConfig.getInstance().getLocalDomainName()+"."+doctype);
					if("正式工艺文件".equals(pplantype)&&"Z".equals(zfflag)){
						List<WTDocument> documents =  BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
						for(WTDocument tempDoc:documents) {
							String pplantype1 = ext.casc.util.IBAHelper.getIBAStringValue(tempDoc, "PPLANTYPE");
							String zfflag1 = ext.casc.util.IBAHelper.getIBAStringValue(tempDoc, "ZFFLAG");
							if ("正式工艺文件".equals(pplantype1) && "Z".equals(zfflag1)) {
								throw new WTException("已存在正式主工艺文件，不能重复提交或生成。");
							}
						}

					}
					WTPartUtil.createWTPartDescribeLink(part, document);
				}
				input = new FileInputStream(tempFilePath +".zip");
				document = WTDocumentUtil.setPrimaryForDocument(document, technicsNumber + ".zip", input);

				Map<String,String > ibaMap = new HashMap<String, String>();
				ibaMap.put("DEPT",dept);
				ibaMap.put("MINDEX","");
				ibaMap.put("PINDEX","");
				ibaMap.put("SECRET",XmlUtility.getAttributeValue(techElement,"SECRET"));
				ibaMap.put("KEYCOMPONENT","");
				ibaMap.put("PHASE_CODE","");
				ibaMap.put("PPNUMBER",pplanNumber);
				ibaMap.put("PPLANTYPE",pplantype);
				ibaMap.put("PPNAME",pplanName);
				ibaMap.put("ZFFLAG",zfflag);
				ibaMap.put("CINDEX","");
				ibaMap.put("BATCH","");
				//ibaMap.put("PAGE",XmlUtility.getAttributeValue(techElement,"pageSize")==null?"":XmlUtility.getAttributeValue(techElement,"pageSize"));
				IBAHelper attrHelper = new IBAHelper(document);
				attrHelper.setIBAValue(document, ibaMap);
				document = (WTDocument) PersistenceHelper.manager.refresh(document);
				FilePrintUtil2.decompressZip(document);
            }
        }finally {
            if(input!=null){
                input.close();
            }
        }
        return "";

    }

	private static void deleteTemplateFile(String tempFilePath) {
		File deleteFile = new File(tempFilePath);
		File[] files = deleteFile.listFiles();
		for(File file:files){
			if(file.getName().endsWith(".zip")||file.getName().endsWith("_参数化工序.xml")){
				file.delete();
			}
		}
	}

	private static void processGongXuIndexAndRoute(Element newTechElement, String tempFilePath) {
		List<Element> steps = XmlUtility.getAllSteps(newTechElement);
		int procedureNumber = 10;
		String preBsoId = "";
		String preStep = "";
		String nextBsoId = "";
		int index = 0;
		List<ProcedureRectangleUnit> items = new ArrayList<ProcedureRectangleUnit>();
		for(Element step : steps) {

			if(index==0){
				String nowBsoId= new UID().toString();
				XmlUtility.setAttributeValue(step,"bsoID",nowBsoId);
				XmlUtility.setAttributeValue(step,"preBsoID","");
				preBsoId = nowBsoId;

				nextBsoId= new UID().toString();
				XmlUtility.setAttributeValue(step,"nextBsoID",nextBsoId);
			}else if(index > 0){
				String nowBsoId = nextBsoId;
				XmlUtility.setAttributeValue(step,"bsoID",nowBsoId);
				XmlUtility.setAttributeValue(step,"preBsoID",preBsoId);

				nextBsoId= new UID().toString();
				XmlUtility.setAttributeValue(step,"nextBsoID",nextBsoId);

				preBsoId = nowBsoId;
			}

			if(index == steps.size()-1){
				XmlUtility.setAttributeValue(step,"nextBsoID","");
			}
			XmlUtility.setAttributeValue(step,"stepNumber",String.valueOf(procedureNumber));
			XmlUtility.setAttributeValue(step,"preStep",preStep);

			preStep =procedureNumber+"_"+XmlUtility.getAttributeValue(step,"stepName");

			ProcedureRectangleUnit p = new ProcedureRectangleUnit();
			p.setId(XmlUtility.getAttributeValue(step,"bsoID"));
			p.setNumber(procedureNumber);
			p.setName(XmlUtility.getAttributeValue(step,"stepName"));
			String workShop = XmlUtility.getAttributeValue(step,"workShop");
			if(Tools.isNull(workShop))  workShop = "";
			p.setWorkShop(workShop );
			p.setPreProcedureID(XmlUtility.getAttributeValue(step,"preBsoID"));
			p.setNextProcedureID(XmlUtility.getAttributeValue(step,"nextBsoID"));
			p.setDescription("");
			items.add(p);
			procedureNumber += 10;
			index ++;
		}
		deleteOldFile(tempFilePath);

		ProcedureRectangleUnitUtil.generateImages(items,tempFilePath);
		String xmlFile = tempFilePath + File.separator + "technics_route.xml";
        try {
            ProcedureRectangleUnitUtil.generateXML(items,xmlFile);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }

    }

	private static void deleteOldFile(String tempFilePath) {
		File file = new File(tempFilePath);
		if(file.exists()){
			File[] files = file.listFiles();
			for(File f:files){
				if(f.getName().endsWith("jpg")||"technics_route.xml".equals(f.getName())){
					f.delete();
				}
			}
		}
	}

	private static List<GLProcessKnowledge> processPeiTaoKnowledge(Element newTechElement, Map<String, GLProcessParamValues> paramValuesMap,String zhuanye,List<GLProcessParams> peiTaoZhishi) {
		List<GLProcessParams> peitaoList =GyCsServerHelper.getProcessParamsByTypeAndZhuanYe("配套表",zhuanye);
		List<GLProcessKnowledge> processKnowledgeList = new ArrayList<GLProcessKnowledge>();
		Set<String> filter = new HashSet<>();
		for(GLProcessParams peitao:peiTaoZhishi){
			filter.add(peitao.getGyName());
		}
		for(GLProcessParams peitaoParam:peitaoList){
			if(!filter.contains(peitaoParam.getGyName())){
				continue;
			}
			boolean isDaoguanPeiTao = false;
			String input = 	peitaoParam.getKnowledgeInferencePara();
			if(Tools.isTrimNull(input)){
				continue;
			}
			String output = peitaoParam.getKnowledgeOutputPara();
			String[] ins = input.split("\\|");
			String[] outs  = output.split("\\|");
			int partNumberCol = ins.length+1;
			int countCol  = ins.length+3;
			int deTypeCol  = ins.length+4;
			int unitCol  = ins.length+4;
			int xlccCol  = ins.length+3;
			int kzjsCol  = ins.length+4;
			int gydeCol  = -1;
			for(int i = 0;i<outs.length;i++){
				if(outs[i].contains("编码")||outs[i].contains("物资")||outs[i].contains("编号")){
					partNumberCol = ins.length+ i +1;
				}else if(outs[i].contains("数量")){
					countCol = ins.length+ i +1;
				}else if(outs[i].contains("定额类型")){
					deTypeCol = ins.length+ i +1;
				}else if(outs[i].contains("单位")){
					unitCol = ins.length+ i +1;
				}else if(outs[i].contains("下料尺寸")){
					xlccCol = ins.length+ i +1;
				}else if(outs[i].contains("可制件数")){
					kzjsCol = ins.length+ i +1;
				}else if(outs[i].contains("工艺定额")){
					gydeCol = ins.length+ i +1;
				}
			}
			Map<String,String> queryParams = new HashMap<String,String>();

			queryParams.put("SHEETNAME",peitaoParam.getGyName());
			for(int i=0;i<ins.length;i++){
				if(!Tools.isTrimNull(ins[i])){
					if("设计定额".equals(ins[i])||ins[i].contains("导管长度")||ins[i].contains("范围")){
						isDaoguanPeiTao = true;
					}else{
						GLProcessParamValues pv1 = paramValuesMap.get(ins[i]);
						if(pv1!=null) {
							queryParams.put("COLUMN"+(i+1),pv1.getParamValue());
						}else{
							System.out.println("processPeiTaoKnowledge # 未找到参数值："+ins[i] );
						}
					}
				}
			}
			if(queryParams.size()==1){
				continue;
			}

			List<CmPersistable> list = GyCsServerHelper.queryGLObjects(GLProcessKnowledge.class, queryParams);
			if(!list.isEmpty()){
				Element gyde =  null;
				if(gydeCol==-1){
					gyde = XmlUtility.getTechnicsDEElement(newTechElement);
					if("钣金工艺".equals(zhuanye)){
						gyde = XmlUtility.getTechnicsCLDEElement(newTechElement);
					}
				}

				Element peiTaoEle = XmlUtility.getPeiTaoListTableElement(newTechElement);


				for(CmPersistable p:list) {
					GLProcessKnowledge ok = (GLProcessKnowledge) p;
					if(gydeCol!=-1){
						Method	getColumnMethod = null;
						String gydeType  = null;
						try {
							getColumnMethod = ok.getClass().getMethod("getColumn"+ gydeCol);
							gydeType  = (String) getColumnMethod.invoke(ok);
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
						if("零件工艺定额".equals(gydeType)){
							gyde = XmlUtility.getTechnicsCLDEElement(newTechElement);
						}else if("装配工艺定额".equals(gydeType)){
							gyde = XmlUtility.getTechnicsDEElement(newTechElement);
						}
					}
                    if(isDaoguanPeiTao){
						if(paramValuesMap.get("导管长度") == null) {
							continue;
						}
						if(range(ok,paramValuesMap.get("导管长度"))){
							emptyCanShu(ok, partNumberCol, countCol, deTypeCol,gyde, peiTaoEle,unitCol,paramValuesMap,xlccCol,kzjsCol);
							if("是".equals(peitaoParam.getIsCanZhuang())||Tools.isNull(peitaoParam.getIsCanZhuang())){
								processKnowledgeList.add(ok);
							}
							break;
						}
					}else{
						emptyCanShu(ok, partNumberCol, countCol,deTypeCol, gyde, peiTaoEle,unitCol, paramValuesMap,xlccCol,kzjsCol);
						if("是".equals(peitaoParam.getIsCanZhuang())||Tools.isNull(peitaoParam.getIsCanZhuang())) {
							processKnowledgeList.add(ok);
						}
						/*String newWZNumber ;
						String sl ;
						try {

							Map<String,String> extAttris = new HashMap<String, String>();
							Method getColumnMethod = ok.getClass().getMethod("getColumn"+partNumberCol);
							newWZNumber = (String) getColumnMethod.invoke(ok);
							extAttris.put("partNumber",newWZNumber);

							getColumnMethod = ok.getClass().getMethod("getColumn"+countCol);
							sl = (String) getColumnMethod.invoke(ok);
							extAttris.put("sl",sl);

							ok.setExtAttris(extAttris);
						} catch (InvocationTargetException e) {
							throw new RuntimeException(e);
						} catch (NoSuchMethodException e) {
							throw new RuntimeException(e);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}

						Element newPart = DocumentHelper.createElement("NewPart");

						JSONObject jsonObject = GenerateMatchHistoryJson.getWzJsonData(ok.getColumn4());

						XmlUtility.setAttributeValue(newPart, "chbm",newWZNumber);
						XmlUtility.setAttributeValue(newPart, "sjbm", newWZNumber);
						XmlUtility.setAttributeValue(newPart, "chmc", newWZNumber);

						XmlUtility.setAttributeValue(newPart, "sl",sl);

						XmlUtility.setAttributeValue(newPart, "dw", "个");
						XmlUtility.setAttributeValue(newPart, "dw2", "个");
						XmlUtility.setAttributeValue(newPart, "tabType", getTabType(ok.getColumn4()));
						if(jsonObject!=null){
							XmlUtility.setAttributeValue(newPart, "chmc", jsonObject.optString("wzName"));

						}

						partElements.add(newPart);

						Element pte = createPeiTaoElement(ok);
						peiTaoEle.add(pte);*/

					}



				}
			}
		}
		return processKnowledgeList;
	}

	private static void emptyCanShu(GLProcessKnowledge ok, int partNumberCol, int countCol, int deTypeCol, Element dingeEle, Element peiTaoEle, int unitCol, Map<String, GLProcessParamValues> paramValuesMap, int xlccCol, int kzjsCol) {
		String newWZNumber ;
		String sl ;
		String deType ;
		String unit ;
		String xlcc ;
		String kzjs ;
		try {

			Map<String,String> extAttris = new HashMap<String, String>();
			Method getColumnMethod = ok.getClass().getMethod("getColumn"+ partNumberCol);
			newWZNumber = (String) getColumnMethod.invoke(ok);
			extAttris.put("partNumber",newWZNumber);

			getColumnMethod = ok.getClass().getMethod("getColumn"+ countCol);
			sl = (String) getColumnMethod.invoke(ok);
			extAttris.put("sl",sl);

			getColumnMethod = ok.getClass().getMethod("getColumn"+ deTypeCol);
			deType = (String) getColumnMethod.invoke(ok);
			extAttris.put("deType",deType);

			getColumnMethod = ok.getClass().getMethod("getColumn"+ unitCol);
			unit = (String) getColumnMethod.invoke(ok);

			if(Tools.isNull(unit)){
				unit = "个";
			}
			extAttris.put("dw",unit);

			getColumnMethod = ok.getClass().getMethod("getColumn"+ xlccCol);
			xlcc = (String) getColumnMethod.invoke(ok);
			extAttris.put("xlcc",sl);

			getColumnMethod = ok.getClass().getMethod("getColumn"+ kzjsCol);
			kzjs = (String) getColumnMethod.invoke(ok);
			extAttris.put("kzjs",sl);

			ok.setExtAttris(extAttris);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		Element partElements = null;
		String recordType = null;
		if(!Tools.isNull(deType)){
			if(deType.contains("原材料")){
				partElements = dingeEle.element("YCLDE");
				if (partElements == null) {
					partElements = dingeEle.addElement("YCLDE");
				}
				recordType = "ycldeRecord";
			}else if(deType.contains("主要材料")){
				partElements = dingeEle.element("ZYCLDE");
				if (partElements == null) {
					partElements = dingeEle.addElement("ZYCLDE");
				}
				recordType = "zycldeRecord";
			}else if(deType.contains("试件")){
				partElements = dingeEle.element("SJYCLDE");
				if (partElements == null) {
					partElements = dingeEle.addElement("SJYCLDE");
				}
				recordType = "sjycldeRecord";
			}
		}
		if(partElements==null){
			partElements = dingeEle.element("ZYCLDE");
			if (partElements == null) {
				partElements = dingeEle.addElement("ZYCLDE");
			}
			recordType = "zycldeRecord";
		}

		sl = getRealSl(ok, paramValuesMap, sl,"sl");
		xlcc = getRealSl(ok, paramValuesMap, xlcc,"xlcc");
		kzjs = getRealSl(ok, paramValuesMap, kzjs,"kzjs");

		Element newPart = DocumentHelper.createElement(recordType);

		JSONObject jsonObject = GenerateMatchHistoryJson.getWzJsonData(newWZNumber);

		XmlUtility.setAttributeValue(newPart, "chbm",newWZNumber);
		XmlUtility.setAttributeValue(newPart, "sjbm", newWZNumber);
		XmlUtility.setAttributeValue(newPart, "chmc", newWZNumber);
		if("zycldeRecord".equals(recordType)){
			XmlUtility.setAttributeValue(newPart, "sl",sl);
		}else if("ycldeRecord".equals(recordType)){
			XmlUtility.setAttributeValue(newPart, "xlcc",xlcc);
			XmlUtility.setAttributeValue(newPart, "kzjs",kzjs);
		}else if("sjycldeRecord".equals(recordType)){
			XmlUtility.setAttributeValue(newPart, "sjcc",xlcc);
			XmlUtility.setAttributeValue(newPart, "sjkzjs",kzjs);
			XmlUtility.setAttributeValue(newPart, "sjsl",sl);
		}

		XmlUtility.setAttributeValue(newPart, "dw", unit);
		XmlUtility.setAttributeValue(newPart, "dw2", unit);
		XmlUtility.setAttributeValue(newPart, "tabType", getTabType(newWZNumber));
		if(jsonObject!=null){
			XmlUtility.setAttributeValue(newPart, "chmc", jsonObject.optString("wzName"));
			XmlUtility.setAttributeValue(newPart, "parentNumber","");//"上级图号"
			XmlUtility.setAttributeValue(newPart, "number",newWZNumber);//"图号"
			XmlUtility.setAttributeValue(newPart, "dw2", unit);//"单位"
			XmlUtility.setAttributeValue(newPart, "xhph", jsonObject.optString("xhph"));//"型号牌号"
			XmlUtility.setAttributeValue(newPart, "gg", jsonObject.optString("xhgg"));//"规格"
			XmlUtility.setAttributeValue(newPart, "jstj", jsonObject.optString("bzh"));//"技术条件"
			XmlUtility.setAttributeValue(newPart, "bzh", jsonObject.optString("bzh"));
			XmlUtility.setAttributeValue(newPart, "sccj", jsonObject.optString("gys"));//"生产厂家"
			XmlUtility.setAttributeValue(newPart, "dw",unit);//"主计量单位"
			XmlUtility.setAttributeValue(newPart, "fjtj", jsonObject.optString("fjtj"));//"附加条件"
			XmlUtility.setAttributeValue(newPart, "lwgggccc", "");//"螺纹规格/公称尺寸"
			XmlUtility.setAttributeValue(newPart, "jxxndj", jsonObject.optString("jxxndj"));//"机械性能等级"
			XmlUtility.setAttributeValue(newPart, "zldj", jsonObject.optString("zldj"));//"质量等级"
			XmlUtility.setAttributeValue(newPart, "fzxs",  jsonObject.optString("fzxs"));//"封装形式"
			XmlUtility.setAttributeValue(newPart, "jddj",  jsonObject.optString("jddj"));//"精度等级"
			XmlUtility.setAttributeValue(newPart, "wzlb", jsonObject.optString("partType"));//"物资类别"
			XmlUtility.setAttributeValue(newPart, "wzlbbm", "");//"物资类别编码"
			XmlUtility.setAttributeValue(newPart, "gyztrcl", jsonObject.optString("bmcl"));//热处理
			XmlUtility.setAttributeValue(newPart, "zl", jsonObject.optString("jdmgdj"));//"重量"
			XmlUtility.setAttributeValue(newPart, "zcsm", jsonObject.optString("jddj"));//"贮存寿命"
			XmlUtility.setAttributeValue(newPart, "tnt", jsonObject.optString("smdj"));//"TNT"
			XmlUtility.setAttributeValue(newPart, "dcstxyq", jsonObject.optString("dcstxyq"));//"电参数特选要求"
			XmlUtility.setAttributeValue(newPart, "comment", "");//备注
			XmlUtility.setAttributeValue(newPart, "dataFrom", "sjzyk");//数据来源
		}

		partElements.add(newPart);
		if(!deType.contains("原材料")){
			Element pte = createPeiTaoElement(ok);
			peiTaoEle.add(pte);
		}

	}

	private static String getRealSl(GLProcessKnowledge ok, Map<String, GLProcessParamValues> paramValuesMap, String sl,String key) {
		if(sl !=null && sl.startsWith("【")&& sl.endsWith("】")){
			String slParamName = sl.substring(1, sl.length()-1);
			if(paramValuesMap.get(slParamName)!=null){
				sl = paramValuesMap.get(slParamName).getParamValue();
				ok.getExtAttris().put(key, sl);
			}
		}
		return sl;
	}

	private static boolean range(GLProcessKnowledge ok, GLProcessParamValues pv) {
		String range = ok.getColumn3();
		String splits = ",";
		if(range.contains("~")){
			splits = "~";
		}else if(range.contains("，")){
			splits = "，";
		}
		String[] ss = range.split(splits);
		String s1 = ss[0].substring(0,1);
		String s2 = ss[0].substring(1);
		String s3 = ss[1].substring(0,ss[1].length()-1);
		String s4 = ss[1].substring(ss[1].length()-1);
		float pValue = Float.parseFloat(pv.getParamValue());
		float v2 = Float.parseFloat(s2);
		float v3 = Float.parseFloat(s3);
		if (pValue>v2&&pValue<v3){
			return true;
		}
		if("[".equals(s1)&&v2==pValue){
			return true;
		}
		if("]".equals(s4)&&v3==pValue){
			return true;
		}
		return false;
	}

	private static void processPeiTaoList(Element newTechElement, List<WTPartUsageLink> links) {
		Element peiTaoEle = XmlUtility.getPeiTaoListTableElement(newTechElement);
		for(WTPartUsageLink link:links) {
			WTPartMaster childMaster = (WTPartMaster) link.getRoleBObject();
			WTPart  part = CSCPart.getPartByNumberAndViewName(childMaster.getNumber(), "Manufacturing");
			String mtype = "";
			String gysl = "1";
			String csize = "";
			String xhph = "";
			String jstj = "";
            try {
				mtype =  IBAHelper.getIBAValue(part,"MTYPE");
				csize =  IBAHelper.getIBAValue(part,"CSIZE");
				xhph =  IBAHelper.getIBAValue(part,"XHPH");
				jstj =  IBAHelper.getIBAValue(part,"JSTJ");
				gysl =  IBAHelper.getIBAValue(link,"GYSL");
            } catch (WTException e) {
               e.printStackTrace();
            }
            Element element = DocumentHelper.createElement("PeiTaoElement");
			XmlUtility.setAttributeValue(element, "number",part.getNumber());
			XmlUtility.setAttributeValue(element, "name", part.getName());
			XmlUtility.setAttributeValue(element, "MTYPE",mtype);
			XmlUtility.setAttributeValue(element, "useCount", gysl);
			XmlUtility.setAttributeValue(element, "XHPH", xhph);
			XmlUtility.setAttributeValue(element, "JSTJ", jstj);
			XmlUtility.setAttributeValue(element, "bzh", jstj);
			XmlUtility.setAttributeValue(element, "CSIZE", csize);
			XmlUtility.setAttributeValue(element, "GG", csize);
			XmlUtility.setAttributeValue(element, "version", VersionCommonHelper.getVersion(part));
			XmlUtility.setAttributeValue(element, "dataFrom","pbom");
			XmlUtility.setAttributeValue(element, "dw", "个");
			XmlUtility.setAttributeValue(element, "comment", "");

			peiTaoEle.add(element);


		}

	}

	private static void processCanZhuang(Element newTechElement,List<GLProcessKnowledge>  processKnowledges,List<GLProcessOtherKnowledge>  processOtherKnowledges,List<WTPartUsageLink> links) {
		try{
			List<Element> allSteps = XmlUtility.getAllSteps(newTechElement);
			boolean findCanZhuangDian = false;
			for (Element step : allSteps) {
				//removeStepsBsoID(step,removeSteps);

				List<Element> paces = XmlUtility.getAllPaces(step);
				for(Element pace :paces){
					String isCanZhuangDian = XmlUtility.getAttributeValue(pace,"isCanZhuangDian");
					if("true".equals(isCanZhuangDian)){
						for(GLProcessKnowledge processKnowledge:processKnowledges){
							Map<String,String> extAttris = processKnowledge.getExtAttris();
							String sl = "1.0";
							String dw = "个";
							if(extAttris!=null && extAttris.containsKey("sl")&&extAttris.containsKey("dw")){
								sl = extAttris.get("sl");
								dw = extAttris.get("dw");
							}
							Element czPart = DocumentHelper.createElement("QMPartInfo");
							String partNumber = processKnowledge.getExtAttris().get("partNumber");
							JSONObject jsonObject = GenerateMatchHistoryJson.getWzJsonData(partNumber);

							XmlUtility.setAttributeValue(czPart, "partNumber", partNumber);
							XmlUtility.setAttributeValue(czPart, "partName",partNumber);

							XmlUtility.setAttributeValue(czPart, "ZCMARK","Z");
							if(jsonObject!=null){
								XmlUtility.setAttributeValue(czPart, "partName",jsonObject.optString("wzName"));
								XmlUtility.setAttributeValue(czPart, "XHPH",jsonObject.optString("xhph"));
								XmlUtility.setAttributeValue(czPart, "GG",jsonObject.optString("xhgg"));
								XmlUtility.setAttributeValue(czPart, "bzh",jsonObject.optString("bzh"));
								XmlUtility.setAttributeValue(czPart, "JSTJ",jsonObject.optString("bzh"));
								String dataType = getTabType(partNumber);
								if("标准紧固件".equals(dataType)) {
									dataType = "标准件";
								}
								XmlUtility.setAttributeValue(czPart, "dataType",dataType);

							}

							XmlUtility.setAttributeValue(czPart, "DW", dw);
							XmlUtility.setAttributeValue(czPart, "DW2", dw);
							XmlUtility.setAttributeValue(czPart, "occId", partNumber);
							XmlUtility.setAttributeValue(czPart, "useCount",sl);
							XmlUtility.addParts(pace,czPart);
						}
						for(GLProcessOtherKnowledge processOtherKnowledge:processOtherKnowledges){
							Element czPart = DocumentHelper.createElement("QMPartInfo");
							String partNumber = processOtherKnowledge.getColumn5();
							JSONObject jsonObject = GenerateMatchHistoryJson.getWzJsonData(partNumber);

							XmlUtility.setAttributeValue(czPart, "partNumber", partNumber);
							XmlUtility.setAttributeValue(czPart, "partName", processOtherKnowledge.getColumn6());
							XmlUtility.setAttributeValue(czPart, "ZCMARK","Z");
							if(jsonObject!=null){
								XmlUtility.setAttributeValue(czPart, "partName",jsonObject.optString("wzName"));
								XmlUtility.setAttributeValue(czPart, "XHPH",jsonObject.optString("xhph"));
								XmlUtility.setAttributeValue(czPart, "GG",jsonObject.optString("xhgg"));
								XmlUtility.setAttributeValue(czPart, "bzh",jsonObject.optString("bzh"));
								XmlUtility.setAttributeValue(czPart, "JSTJ",jsonObject.optString("bzh"));
							}

							XmlUtility.setAttributeValue(czPart, "DW", processOtherKnowledge.getColumn8());
							XmlUtility.setAttributeValue(czPart, "DW2", processOtherKnowledge.getColumn8());
							XmlUtility.setAttributeValue(czPart, "occId", processOtherKnowledge.getColumn5());
							XmlUtility.setAttributeValue(czPart, "useCount","1.0");
							XmlUtility.addParts(pace,czPart);
						}
						for(WTPartUsageLink link:links){
							WTPartMaster childMaster = (WTPartMaster)link.getRoleBObject();
							if(childMaster!=null){
								String gysl  = IBAHelper.getIBAValue(link,"GYSL");
								if(Tools.isTrimNull(gysl)){
									gysl = link.getQuantity().getAmount()+"";
								}
								Element czPart = DocumentHelper.createElement("QMPartInfo");

								WTPart  part = CSCPart.getPartByNumberAndViewName(childMaster.getNumber(), "Manufacturing");

								XmlUtility.setAttributeValue(czPart, "partNumber", childMaster.getNumber());
								XmlUtility.setAttributeValue(czPart, "partName", childMaster.getName());
								XmlUtility.setAttributeValue(czPart, "ZCMARK","Z");
								if(part!=null){
									String xhph = ext.casc.util.IBAHelper.getIBAStringValue(part,"XHPH");
									String csize = ext.casc.util.IBAHelper.getIBAStringValue(part,"CSIZE");
									String jstj = ext.casc.util.IBAHelper.getIBAStringValue(part,"JSTJ");
									XmlUtility.setAttributeValue(czPart, "XHPH", xhph);
									XmlUtility.setAttributeValue(czPart, "GG",csize);
									XmlUtility.setAttributeValue(czPart, "CSIZE",csize);
									XmlUtility.setAttributeValue(czPart, "bzh",jstj);
									XmlUtility.setAttributeValue(czPart, "JSTJ",jstj);
									XmlUtility.setAttributeValue(czPart, "MTYPE",ext.casc.util.IBAHelper.getIBAStringValue(part,"MTYPE"));
								}

								XmlUtility.setAttributeValue(czPart, "DW", "个");
								XmlUtility.setAttributeValue(czPart, "DW2", "个");
								XmlUtility.setAttributeValue(czPart, "occId", childMaster.getNumber());
								XmlUtility.setAttributeValue(czPart, "useCount",gysl);
								XmlUtility.addParts(pace,czPart);
							}

						}
						findCanZhuangDian = true;
						break;
					}
				}
				if(findCanZhuangDian){
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void removeStepsBsoID(Element step, List<String> removeSteps) {
		String preBsoID = step.attributeValue("preBsoID");
		String nextBsoID = step.attributeValue("nextBsoID");
		for(String removeStepId :removeSteps){
			if(!Tools.isNull(preBsoID)&&preBsoID.contains(removeStepId)){
				if(preBsoID.equals(removeStepId)){
					XmlUtility.setAttributeValue(step, "preBsoID", "");
					XmlUtility.setAttributeValue(step, "preStep", "");

				}else{
					String newPreBsoID=preBsoID.replaceAll(removeStepId+",","").replaceAll(","+removeStepId,"");
					XmlUtility.setAttributeValue(step, "preBsoID", newPreBsoID);
					XmlUtility.setAttributeValue(step, "preStep", "");
				}

			}

			if(!Tools.isNull(nextBsoID)&&nextBsoID.contains(removeStepId)){
				if(nextBsoID.equals(removeStepId)){
					XmlUtility.setAttributeValue(step, "nextBsoID", "");
				}else{
					String newNextBsoID=nextBsoID.replaceAll(removeStepId+",","").replaceAll(","+removeStepId,"");
					XmlUtility.setAttributeValue(step, "nextBsoID", newNextBsoID);
				}

			}
		}
	}

	private static List<GLProcessOtherKnowledge> processGongYiDingE(Element newTechElement, Map<String, GLProcessParamValues> paramValuesMap) {
    	GLProcessParamValues pv1 = paramValuesMap.get("导管材料");
    	GLProcessParamValues pv2 = paramValuesMap.get("焊丝牌号");
    	GLProcessParamValues pv3 = paramValuesMap.get("焊丝规格");
		List<GLProcessOtherKnowledge> processOtherKnowledgeList = new ArrayList<GLProcessOtherKnowledge>();
    	if(pv1!=null&&pv2!=null&&pv3!=null ) {
    		String v1 = pv1.getParamValue();
    		String v2 = pv2.getParamValue();
    		String v3 = pv3.getParamValue();
    		if(!Tools.isNull(v1)&&!Tools.isNull(v2)&&!Tools.isNull(v3)){
    			Map<String,String> queryParams = new HashMap<String,String>();
    			queryParams.put("COLUMN1", v1);
    			queryParams.put("COLUMN2", v2);
    			queryParams.put("COLUMN3", v3);
    			List<CmPersistable> list = GyCsServerHelper.queryGLObjects(GLProcessOtherKnowledge.class, queryParams);
    			if(!list.isEmpty()){
    				List<GLProcessOtherKnowledge> sjycldeList = new ArrayList<GLProcessOtherKnowledge>();
    				List<GLProcessOtherKnowledge> ycldeList = new ArrayList<GLProcessOtherKnowledge>();
    				List<GLProcessOtherKnowledge> zycldeList = new ArrayList<GLProcessOtherKnowledge>();

    				for(CmPersistable p:list){
    					GLProcessOtherKnowledge ok = (GLProcessOtherKnowledge)p;
						processOtherKnowledgeList.add(ok);
    					if("原材料".equals(ok.getColumn4())){
    						ycldeList.add(ok);
    					}else if("主要材料".equals(ok.getColumn4())){
    						zycldeList.add(ok);
    					}else if("试件原材料".equals(ok.getColumn4())){
    						sjycldeList.add(ok);
    					}
    				}


					Element clde = XmlUtility.getTechnicsCLDEElement(newTechElement);
			        Element peiTaoEle = XmlUtility.getPeiTaoListTableElement(newTechElement);

					if (!sjycldeList.isEmpty()) {
						Element sjyclde = XmlUtility.getTechnicsSJYCLDEElement(clde);
						for (GLProcessOtherKnowledge k : sjycldeList) {
							Element sjycldeRecord = DocumentHelper.createElement("sjycldeRecord");
							XmlUtility.setAttributeValue(sjycldeRecord, "chbm", k.getColumn5());
							XmlUtility.setAttributeValue(sjycldeRecord, "sjbm", k.getColumn5());
							XmlUtility.setAttributeValue(sjycldeRecord, "chmc", k.getColumn6());

							XmlUtility.setAttributeValue(sjycldeRecord, "sjcc", "1");
							XmlUtility.setAttributeValue(sjycldeRecord, "sjkzjs", "1");
							XmlUtility.setAttributeValue(sjycldeRecord, "sjsl", k.getColumn7());
							XmlUtility.setAttributeValue(sjycldeRecord, "sl", k.getColumn7());

							XmlUtility.setAttributeValue(sjycldeRecord, "dw", k.getColumn8());
							XmlUtility.setAttributeValue(sjycldeRecord, "dw2", k.getColumn8());
							XmlUtility.setAttributeValue(sjycldeRecord, "tabType", getTabType(k.getColumn5()));
							sjyclde.add(sjycldeRecord);

							Element pte = createPeiTaoElement(k);
							peiTaoEle.add(pte);

						}

					}
					if (!ycldeList.isEmpty()) {
						Element yclde = XmlUtility.getTechnicsYCLDEElement(clde);
						for (GLProcessOtherKnowledge k : ycldeList) {
							Element ycldeRecord = DocumentHelper.createElement("ycldeRecord");
							XmlUtility.setAttributeValue(ycldeRecord, "chbm", k.getColumn5());
							XmlUtility.setAttributeValue(ycldeRecord, "sjbm", k.getColumn5());
							XmlUtility.setAttributeValue(ycldeRecord, "chmc", k.getColumn6());

							XmlUtility.setAttributeValue(ycldeRecord, "sjcc", "1");
							XmlUtility.setAttributeValue(ycldeRecord, "sjkzjs", "1");
							XmlUtility.setAttributeValue(ycldeRecord, "sjsl", k.getColumn7());
							XmlUtility.setAttributeValue(ycldeRecord, "sl", k.getColumn7());

							XmlUtility.setAttributeValue(ycldeRecord, "dw", k.getColumn8());
							XmlUtility.setAttributeValue(ycldeRecord, "dw2", k.getColumn8());
							XmlUtility.setAttributeValue(ycldeRecord, "tabType",getTabType(k.getColumn5()));
							yclde.add(ycldeRecord);

							Element pte = createPeiTaoElement(k);
							peiTaoEle.add(pte);

						}

					}
					if (!zycldeList.isEmpty()) {
						Element zyclde = XmlUtility.getTechnicsZYCLDEElement(clde);
						for (GLProcessOtherKnowledge k : zycldeList) {
							Element zycldeRecord = DocumentHelper.createElement("zycldeRecord");
							XmlUtility.setAttributeValue(zycldeRecord, "chbm", k.getColumn5());
							XmlUtility.setAttributeValue(zycldeRecord, "sjbm", k.getColumn5());
							XmlUtility.setAttributeValue(zycldeRecord, "chmc", k.getColumn6());

							XmlUtility.setAttributeValue(zycldeRecord, "sjcc", "1");
							XmlUtility.setAttributeValue(zycldeRecord, "sjkzjs", "1");
							XmlUtility.setAttributeValue(zycldeRecord, "sjsl", k.getColumn7());
							XmlUtility.setAttributeValue(zycldeRecord, "sl", k.getColumn7());
							XmlUtility.setAttributeValue(zycldeRecord, "dw", k.getColumn8());
							XmlUtility.setAttributeValue(zycldeRecord, "dw2", k.getColumn8());
							XmlUtility.setAttributeValue(zycldeRecord, "tabType", getTabType(k.getColumn5()));

							zyclde.add(zycldeRecord);

							Element pte = createPeiTaoElement(k);
							peiTaoEle.add(pte);

						}
					}

    			}

    		}

    	}
		return processOtherKnowledgeList;
	}
	private static Element createPeiTaoElement(GLProcessOtherKnowledge k) {
		 Element element = DocumentHelper.createElement("PeiTaoElement");
         XmlUtility.setAttributeValue(element, "number",k.getColumn5());
         XmlUtility.setAttributeValue(element, "name", k.getColumn6());
         XmlUtility.setAttributeValue(element, "MTYPE", k.getColumn9());
         XmlUtility.setAttributeValue(element, "useCount",  k.getColumn7());
         XmlUtility.setAttributeValue(element, "XHPH", "");
         XmlUtility.setAttributeValue(element, "CSIZE", "");
         XmlUtility.setAttributeValue(element, "version", "");
         XmlUtility.setAttributeValue(element, "dw", k.getColumn8());
         XmlUtility.setAttributeValue(element, "comment", "");
		 return element;
	}
	private static Element createPeiTaoElement(GLProcessKnowledge k) {
		Element element = DocumentHelper.createElement("PeiTaoElement");
		String partNumber = k.getExtAttris().get("partNumber");
		XmlUtility.setAttributeValue(element, "number",partNumber);
		XmlUtility.setAttributeValue(element, "MTYPE", k.getExtAttris().get("deType"));
		XmlUtility.setAttributeValue(element, "useCount", k.getExtAttris().get("sl"));
		XmlUtility.setAttributeValue(element, "XHPH", "");
		XmlUtility.setAttributeValue(element, "CSIZE", "");
		XmlUtility.setAttributeValue(element, "version", "");
		XmlUtility.setAttributeValue(element, "dw", "个");
		XmlUtility.setAttributeValue(element, "comment", "");
		JSONObject jsonObject = GenerateMatchHistoryJson.getWzJsonData(partNumber);
		if(jsonObject!=null){
			XmlUtility.setAttributeValue(element, "name", jsonObject.optString("wzName"));
			XmlUtility.setAttributeValue(element, "useCount", k.getExtAttris().get("sl"));
			XmlUtility.setAttributeValue(element, "XHPH", jsonObject.optString("xhph"));
			XmlUtility.setAttributeValue(element, "CSIZE",  jsonObject.optString("xhgg"));
			XmlUtility.setAttributeValue(element, "jstj", jsonObject.optString("bzh"));
			XmlUtility.setAttributeValue(element, "gys", jsonObject.optString("gys"));
			XmlUtility.setAttributeValue(element, "partNumber", "");
			XmlUtility.setAttributeValue(element, "zldj",  jsonObject.optString("zldj"));

		}

		return element;
	}
	private static String getTabType(String column5) {
		if(!Tools.isNull(column5)){
			if(column5.startsWith("A")){
				return "电子元器件";
			}
			if(column5.startsWith("B")){
				return "标准紧固件";
			}
			if(column5.startsWith("C")){
				return "金属材料";
			}
			if(column5.startsWith("D")){
				return "非金属刺啦";
			}
			if(column5.startsWith("E")){
				return "复合材料";
			}
			if(column5.startsWith("F")){
				return "机电材料";
			}
			if(column5.startsWith("G")){
				return "火工品";
			}
		}
		return null;
	}
	private static void processGongZhuang(Element newTechElement, Map<String, GLProcessParamValues> paramValuesMap,String zhuanye,Map<String,GLProcessParams> useGongZhuang) {
		//List<GLProcessParams> params = GyCsServerHelper.getProcessParamsByTypeAndZhuanYe("工装",zhuanye);
		for(GLProcessParams gongzhuzngParam :useGongZhuang.values()){
			String input = 	gongzhuzngParam.getKnowledgeInferencePara();
			//String output = gongzhuzngParam.getKnowledgeOutputPara();
			if(Tools.isTrimNull(input)){
				continue;
			}
			String[] ins = input.split("\\|");
			//String[] outs  = output.split("\\|");
			Map<String,String> queryParams = new HashMap<String,String>();
			queryParams.put("SHEETNAME", gongzhuzngParam.getGyName());
			for(int i=0;i<ins.length;i++){
				GLProcessParamValues pv1 = paramValuesMap.get(ins[i]);
				if(pv1 != null) {
					queryParams.put("COLUMN"+(i+1), pv1.getParamValue());
				}
			}
			if(queryParams.size() == 1) break;

			List<CmPersistable> list = GyCsServerHelper.queryGLObjects(GLProcessKnowledge.class, queryParams);
			if(!list.isEmpty()){
				List<Element> listSteps = XmlUtility.getAllSteps(newTechElement);
				if(listSteps!=null &&!listSteps.isEmpty()){
					for (Iterator it = listSteps.iterator(); it.hasNext(); ) {
						Element stepElement = (Element) it.next();
						extractedGongZhuang(gongzhuzngParam, stepElement, list, ins);
						Element pacesElement = stepElement.element("paces");
						if (pacesElement != null) {
							List<Element> pacesList = pacesElement.elements("QMProcedureInfo");
							if (pacesList != null && !pacesList.isEmpty()) {
								for (Element pace : pacesList) {
									extractedGongZhuang(gongzhuzngParam, pace, list, ins);
								}
							}
						}

					}
				}
			}else{
				/*List<Element> listSteps = XmlUtility.getAllSteps(newTechElement);
				if(listSteps!=null &&!listSteps.isEmpty()){
					for (Iterator it = listSteps.iterator(); it.hasNext(); ) {
						Element stepElement = (Element) it.next();
						emptyCanShu(gongzhuzngParam, stepElement);
						Element pacesElement = stepElement.element("paces");
						if (pacesElement != null) {
							List<Element> pacesList = pacesElement.elements("QMProcedureInfo");
							if (pacesList != null && !pacesList.isEmpty()) {
								for (Element pace : pacesList) {
									emptyCanShu(gongzhuzngParam, pace);
								}
							}
						}

					}
				}*/
			}
		}
	}

	private static void emptyCanShu(GLProcessParams gongzhuzngParam, Element stepElement) {
		Element procedureContent = stepElement.element("procedureContent");
		String text = procedureContent.getText();
		procedureContent.setText(text.replaceAll("【"+ gongzhuzngParam.getGyName()+"：】",""));
	}

	private static void extractedGongZhuang(GLProcessParams gongzhuzngParam, Element stepElement, List<CmPersistable> list, String[] ins) {
		Element procedureContent = stepElement.element("procedureContent");
		if (procedureContent != null) {
			String text = procedureContent.getText();
			if (text != null) {
				String sfregex = "【"+ gongzhuzngParam.getGyName()+".*?】";
				Pattern sfpattern = Pattern.compile(sfregex);
				Matcher sfMatcher = sfpattern.matcher(text);
				while (sfMatcher.find()) {
					String sfMatched = sfMatcher.group();
					for(CmPersistable p: list){
						GLProcessKnowledge pk = (GLProcessKnowledge)p;
						try {
							Method getColumnMethod = pk.getClass().getMethod("getColumn"+( ins.length+1));
							String toolNumber  = (String) getColumnMethod.invoke(pk);
							if(!Tools.isNull(toolNumber)){
								MPMTooling to = MPMResourceUtil.getMPMToolingByNumber(toolNumber.toUpperCase(), TypeNameConstants.GZhuang);
								if(to==null){
									System.out.println("工艺批量生成工装未找到："+toolNumber);
									continue;
								}
								//MPMResourceUtil.getMPMToolingByLikeNumberAndName(toolNumber,"" , TypeNameConstants.GZhuang);
								Element toolE = XmlUtility.createTool();
								//QMToolInfo bsoID="" toolNum="GZZZ" toolName="工装2111" toolStdNum="" toolSpec="" toolType="" useCount="12" oid="3949126" frockType="122355" csize="" EnglishName="11" />
								XmlUtility.setAttributeValue(toolE, "toolNum", toolNumber);
								XmlUtility.setAttributeValue(toolE, "toolName", to.getName());
								XmlUtility.setAttributeValue(toolE, "useCount", "1");
								XmlUtility.setAttributeValue(toolE, "csize", "");
								XmlUtility.setAttributeValue(toolE, "mindex", "");
								XmlUtility.setAttributeValue(toolE, "oid", to.getPersistInfo().getObjectIdentifier().getId()+"");
								XmlUtility.addTool(stepElement, toolE);
							}
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (WTPropertyVetoException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (WTException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							throw new RuntimeException(e);
						} catch (NoSuchMethodException e) {
							throw new RuntimeException(e);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
					procedureContent.setText(text.replaceAll(sfMatched,""));
				}
			}
		}
	}

	private static void commonReplace(String gycsMatched, Map<String, String> replaces) {
    	if(gycsMatched.contains("：")){
    		String[] ss = gycsMatched.split("：");
    		String key  = ss[0].substring(1);
    		StringBuilder oldGysc = new StringBuilder("【");
        	oldGysc.append(key).append("：").append("】");
    		if(!"】".equals(ss[1].trim()) && replaces.containsKey(oldGysc.toString())){
            	String newGycs = replaces.get(oldGysc.toString());
                replaces.put(gycsMatched,newGycs);
    		}
    	}
	}
	private static void processGongxuTemplate(Element stepElement, Map<String, GLProcessParamValues> paramValuesMap, GLProcessParams gongXuParam,Map<String,String> replaces,String processFilePath) {
    	//String[] ss = gxTpMatched.split("：");

    	//String stepName = ss[0].substring(1,ss[0].length()-4);
		//List<GLProcessParams> params = GyCsServerHelper.getProcessParamsByType("工序模板映射");
		String input = 	gongXuParam.getKnowledgeInferencePara();
		String output = gongXuParam.getKnowledgeOutputPara();
		if(Tools.isTrimNull(input)){
			return;
		}
		String[] ins = input.split("\\|");
		String[] outs  = output.split("\\|");
		Map<String,String> queryParams = new HashMap<String,String>();
		queryParams.put("SHEETNAME", gongXuParam.getGyName());
		for(int i=0;i<ins.length;i++){
			if(!Tools.isTrimNull(ins[i])){
				GLProcessParamValues pv1 = paramValuesMap.get(ins[i]);
				if(pv1!=null){
					queryParams.put("COLUMN"+(i+1),pv1.getParamValue());
				}
			}
		}
		if(queryParams.size()==1){
			System.out.println("工序模板映射缺少条件。");
			return;
		}
		int gxTempColumn = 2;
		for(int i = 0;i<outs.length;i++){
			if(outs[i].contains("编码")||outs[i].contains("编号")){
				gxTempColumn = ins.length+ i +1;
			}
		}
		List<CmPersistable> list = GyCsServerHelper.queryGLObjects(GLProcessKnowledge.class, queryParams);
		if(!list.isEmpty()){
			GLProcessKnowledge ok = (GLProcessKnowledge) list.get(list.size()-1);
			if(ok!=null){
				String templdateNumber = (String)Tools.getReflectMethodValue(ok,"getColumn"+gxTempColumn);
				if(Tools.isTrimNull(templdateNumber)){
					return;
				}
				WTDocument templateDoc = DocUtil.getDoc(templdateNumber, false);
				if(templateDoc==null) return ;
				String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "gyGxParamTemplates" + File.separator + templateDoc.getNumber();

				String zipFileName;
				try {
					zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(templateDoc, tempFilePath);
					ApacheZipUtil.decompress(tempFilePath +File.separator+ zipFileName, tempFilePath);
					File gxTmpFile = new File(tempFilePath+File.separator+templateDoc.getName()+".xml");

					File gxTmpContentFile = new File(tempFilePath);
					if(gxTmpContentFile.exists()){
						FilesUtil.copyDirectiory(tempFilePath,processFilePath);
					}
					gxTmpFile =getRealFile(tempFilePath,gxTmpFile);

					if(gxTmpFile.exists()){
						Document doc = XmlUtility.getDocument(gxTmpFile);

						OutputFormat format = OutputFormat.createCompactFormat();
						format.setEncoding("GBK");
						java.io.StringWriter stringWriter = new java.io.StringWriter();
						XMLWriter writer = new XMLWriter(stringWriter, format);
						writer.write(doc);
						String xmlStringFormatting = stringWriter.toString();
						Set<Map.Entry<String, String>> entrySet = replaces.entrySet();
						for(Map.Entry<String, String> entry : entrySet){
							String oldValue = entry.getKey();
							String newValue = entry.getValue();
							xmlStringFormatting  = xmlStringFormatting.replaceAll(oldValue,newValue);
						}
						Document newDocument = XmlUtility.getDocument(xmlStringFormatting.getBytes());
						Element rootElement = newDocument.getRootElement();
					   /* Element parent =  stepElement.getParent();
						String stepNumber = XmlUtility.getAttributeValue(stepElement, "stepNumber");
						String bsoID = XmlUtility.getAttributeValue(stepElement, "bsoID");
						String preBsoID = XmlUtility.getAttributeValue(stepElement, "preBsoID");
						String nextBsoID = XmlUtility.getAttributeValue(stepElement, "nextBsoID");


						XmlUtility.setAttributeValue(rootElement, "stepNumber", stepNumber);
						XmlUtility.setAttributeValue(rootElement, "bsoID", bsoID);
						XmlUtility.setAttributeValue(rootElement, "preBsoID", preBsoID);
						XmlUtility.setAttributeValue(rootElement, "nextBsoID", nextBsoID);*/

						//stepElement.setText(rootElement.getText());
						List<Element> es = rootElement.elements();
						XmlUtility.removeAllChildElements(stepElement);
						for(Element e:es){
							rootElement.remove(e);
							stepElement.add(e);
						}


					}

				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (PropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

	}

	private static boolean processGongxuShiFou(Element stepElement, GLProcessParams processParam, String sfMatched, Map<String, GLProcessParamValues> paramValuesMap) {
		String or = processParam.getOutputRules();
		String inferencePara = processParam.getKnowledgeInferencePara();
		String expValue = null;
		if(!Tools.isTrimNull(inferencePara)){
			SplitResult splitResult = ParameterSplitter.split(inferencePara);
			String[] parameters = splitResult.getParameters();
			String delimiter = splitResult.getDelimiter();
			int allCount = 0;
			int zeroCount=0;
			for(String parameter:parameters){
				GLProcessParamValues processParamValues = paramValuesMap.get(parameter);
				if(processParamValues!=null){
					allCount ++;
					if("/".equals(processParamValues.getParamValue())){
						zeroCount ++;
					}
				}
			}
			if (Tools.isNull(delimiter)){
				if(zeroCount ==1){
					expValue = "否";
				}else if(zeroCount ==0){
					expValue = "是";
				}
			}else{
				if("||".equals(delimiter)){
					if(allCount ==zeroCount){
						expValue = "否";
					}else{
						expValue = "是";
					}
				}else if("&".equals(delimiter)){
					if(zeroCount>0){
						expValue = "否";
					}else{
						expValue = "是";
					}
				}

			}
		}
		if(!Tools.isNull(or)){
			if(expValue==null){
				GLProcessParamValues meijuValue = paramValuesMap.get(processParam.getGyName());
				if(meijuValue!=null) {
					expValue = meijuValue.getParamValue();
				}
			}

			if(or.contains("删除工序")){
				if(expValue!=null){
					if(or.trim().startsWith(expValue)){
						stepElement.getParent().remove(stepElement);
						return true;
					}
				}
			}

		}

		return false;

	}

	private static void processStateTableShiFou(Element stateTableElement, GLProcessParams processParam, String sfMatched, Map<String, GLProcessParamValues> paramValuesMap) {
		String or = processParam.getOutputRules();
		String inferencePara = processParam.getKnowledgeInferencePara();
		String expValue = null;
		if(!Tools.isNull(inferencePara)){
			SplitResult splitResult = ParameterSplitter.split(inferencePara);
			String[] parameters = splitResult.getParameters();
			String delimiter = splitResult.getDelimiter();
			int allCount = 0;
			int zeroCount=0;
			for(String parameter:parameters){
				GLProcessParamValues processParamValues = paramValuesMap.get(parameter);
				if(processParamValues!=null){
					allCount ++;
					if("/".equals(processParamValues.getParamValue())){
						zeroCount ++;
					}
				}
			}
			if (Tools.isNull(delimiter)){
				if(zeroCount ==1){
					expValue = "否";
				}else if(zeroCount ==0){
					expValue = "是";
				}
			}else{
				if("||".equals(delimiter)){
					if(allCount ==zeroCount){
						expValue = "否";
					}else{
						expValue = "是";
					}
				}else if("&".equals(delimiter)){
					if(zeroCount>0){
						expValue = "否";
					}else{
						expValue = "是";
					}
				}

			}
		}
		if(!Tools.isNull(or)){
			if(or.contains("删除状态表")){
				if(expValue==null){
					GLProcessParamValues meijuValue = paramValuesMap.get(processParam.getGyName());
					if(meijuValue!=null) {
						expValue = meijuValue.getParamValue();
					}
				}
				if(expValue!=null){
					if(or.trim().startsWith(expValue)){
						stateTableElement.getParent().remove(stateTableElement);
					}
				}

			}
		}
	}
	private static String processCommonZhiShiCanshu(GLProcessParams commonKnowledge, Map<String,GLProcessParamValues> paramValuesMap) {

		String kif = commonKnowledge.getKnowledgeInferencePara();
		String kof = commonKnowledge.getKnowledgeOutputPara();
		String outputRules = commonKnowledge.getOutputRules();
		if(Tools.isNull(outputRules)){
			outputRules = "";
		}

		if(!Tools.isNull(kif)&&!Tools.isNull(outputRules)){
			String[] ss = kif.split("\\|");

			Map<String,String > queryParams = new HashMap<String, String>();
			for(int i=0;i<ss.length;i++){
				GLProcessParamValues pv = paramValuesMap.get(ss[i]);
				if(pv!=null&&!Tools.isNull(pv.getParamValue())){
					queryParams.put("COLUMN"+(i+1), pv.getParamValue());
				}
			}
			if(!queryParams.isEmpty()){
				queryParams.put("SHEETNAME", commonKnowledge.getGyName());
				GLProcessKnowledge kl = (GLProcessKnowledge)GyCsServerHelper.queryGLObject(GLProcessKnowledge.class, queryParams);
				if(kl!=null){
					String outPutValue = outputRules;
					for(int i=0;i<ss.length;i++){
						String columnName = ss[i].trim();
						String v = getColumnValue(kl,"getColumn"+(i+1));
						if(!Tools.isNull(v)){
							GLProcessParams paramsDef= GyCsServerHelper.getProcessParamDefinitionByName(columnName,true);
							if(paramsDef!=null&&!Tools.isNull(paramsDef.getUnit())){
								v = v + paramsDef.getUnit();
							}
							outPutValue = outPutValue.replaceAll(OPEN_PARAM+columnName+CLOSE_PARAM,OPEN_PARAM+columnName+PARAM_SPIT +v+CLOSE_PARAM);
						}
					}
					if(!Tools.isNull(kof)){
						String[] ssOut = kof.split("\\|");
						for(int i=0;i<ssOut.length;i++){
							String columnName = ssOut[i].trim();;
							String v = getColumnValue(kl,"getColumn"+(ss.length+i+1));
							if(!Tools.isNull(v)){
								GLProcessParams paramsDef= GyCsServerHelper.getProcessParamDefinitionByName(columnName,true);
								if(paramsDef!=null&&!Tools.isNull(paramsDef.getUnit())){
									v = v + paramsDef.getUnit();
								}
								outPutValue = outPutValue.replaceAll(OPEN_PARAM+columnName+CLOSE_PARAM,OPEN_PARAM+columnName+PARAM_SPIT +v+CLOSE_PARAM);
							}
						}

					}
					return outPutValue;

				}
			}
		}
		return outputRules;
	}

    private static String getColumnValue(GLProcessKnowledge kl,String methodName){

    	Class clazz = kl.getClass();
    	try {
    		Method method =	clazz.getMethod(methodName);
    		Object o = method.invoke(kl);
    		if(o==null){
    			return "";
    		}else{
        		return o.toString();

    		}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return "";
    }

	private static void setXmlAttriByIBA(WTPart part, String s, Element techElement) throws WTException {
    	 String value = IBAHelper.getIBAValue(part, s);
         XmlUtility.setAttributeValue(techElement, s, value);
	}

    /**
     * 时间戳为ID
     * @return
     */
    private  static  String idGenerator() throws WTException {
        String seq = GenSequeneUtil.genSeqNumber("TECHNICS_SEQ",new DecimalFormat("000000000000"));
        String newId = "9"+seq;
        return newId;
    }
    //防止Linux操作系统不支持中文，获取不到文件
    public static File getRealFile(String tempFilePath,File xmlFile){
		if(!xmlFile.exists()){
			//technics_route.xml排除
			File folder = new File(tempFilePath);
			File[] files = folder.listFiles();
			for(File f:files){
				if(f.getName().endsWith("xml") &&!"technics_route.xml".equals(f.getName())){
					return f;
				}
			}
			File escapeFile = new File(tempFilePath+File.separator+ StringEscapeUtils.escapeJava(xmlFile.getName()));
			if(escapeFile.exists()){
				return escapeFile;
			}
		}
		return xmlFile;
	}

    public static void structureGyTempldateParams(WTDocument templateDoc)throws Exception{
        String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "gyParamTemplates" + File.separator + templateDoc.getNumber();
        File tempFile = new File(tempFilePath);
        File xmlFile = null;

        tempFile.mkdirs();
        WTDocumentUtil.downloadDocumentPrimaryToTemp(templateDoc, tempFilePath);
        ApacheZipUtil.decompress(tempFilePath + File.separator+templateDoc.getName()+".zip", tempFilePath);

        String subFileName = templateDoc.getName();
        xmlFile = new File(tempFilePath + File.separator + subFileName + ".xml");

		xmlFile =getRealFile(tempFilePath,xmlFile);

        Document doc = XmlUtility.getDocument(xmlFile);

        OutputFormat format = OutputFormat.createCompactFormat();
        format.setEncoding("GBK");
        java.io.StringWriter stringWriter = new java.io.StringWriter();
        XMLWriter writer = new XMLWriter(stringWriter, format);
        writer.write(doc);
        String xmlStringFormatting = stringWriter.toString();
        Set<String> gycsSet = new LinkedHashSet<String>();
        String regex = "【.*?】";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(xmlStringFormatting);
        while (matcher.find()) {
            String gycs = matcher.group();

			String[] ss = gycs.split("：");
			String key = ss[0].substring(1);
			gycsSet.add(key);


        }
		int i = 1;
        for(String key :gycsSet){
            GLProcessParams processParams = (GLProcessParams)GyCsServerHelper.queryGLObject(GLProcessParams.class,GLProcessParams.GYNAME,key);
            if(processParams!=null){
                GLProcessParamDefinition definition = new GLProcessParamDefinition();
                definition.setKeyId(PersistenceCommonHelper.getOid(templateDoc)+"_"+processParams.getGyNumber());
                definition.setTemplateId(PersistenceCommonHelper.getOid(templateDoc));
                definition.setGyParamName(processParams.getGyName());
                definition.setGyParamNumber(processParams.getGyNumber());
                definition.setGyParamType(processParams.getParameterCategory());
                definition.setEnumValues(processParams.getEnumValues());
                CmPersistenceHelper.manager.save(definition);
				i++;
            }
        }
    }


}
