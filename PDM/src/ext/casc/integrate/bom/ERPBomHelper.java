package ext.casc.integrate.bom;

import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import ext.casc.integrate.model.GLErpMaterialsBean;
import ext.casc.integrate.model.GLErpPbomPartBean;
import ext.casc.integrate.model.GLErpPeiTaoPartBean;
import ext.casc.integrate.util.BomUtil;
import ext.casc.integrate.util.CldeUtil;
import ext.casc.integrate.util.Constants;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.Tools;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import java.beans.PropertyVetoException;
import java.io.File;
import java.util.*;

public class ERPBomHelper {
    public static List<WTDocument> getAllApprovedTechnicsDocumentByPart(WTPart part, String fileTypeB, String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
        if(Constants.LABEL_PROCESS_TYPEB_FORMAL.equals(fileTypeB))
            fileTypeB = Constants.PROCESS_TYPEB_FORMAL;
        if(Constants.LABEL_PROCESS_TYPEB_TEMP.equals(fileTypeB))
            fileTypeB = Constants.PROCESS_TYPEB_TEMP;
        if(Constants.LABEL_PROCESS_TYPEC_PRIMARY.equals(fileTypeC))
            fileTypeC = Constants.PROCESS_TYPEC_PRIMARY;
        if(Constants.LABEL_PROCESS_TYPEC_ASSIST.equals(fileTypeC))
            fileTypeC = Constants.PROCESS_TYPEC_ASSIST;

        List<WTDocument> list = new ArrayList<WTDocument>();
        QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
        LatestConfigSpec lcs = new LatestConfigSpec();
        qr = lcs.process(qr);
        ext.casc.util.IBAHelper helper = new ext.casc.util.IBAHelper();
        while (qr.hasMoreElements()) {
            WTDocument doc = (WTDocument) qr.nextElement();
            String zfFlag = helper.getIBAStringValue(doc, "ZFFLAG");
            String pplantype = helper.getIBAStringValue(doc, "PPLANTYPE");
            if(!"ALL".equals(fileTypeC)){
                if(!fileTypeC.equals(zfFlag)) {
                    continue;
                }
            }
            if(!fileTypeB.equals(pplantype)  ) {
            	continue;
            }
            QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
            while(qr2.hasMoreElements()){
                WTDocument document = (WTDocument)qr2.nextElement();
                String state =document.getState().getState().toString();
                if("OBSOLESCENCE".equals(state)){
                    break;
                }
                String CLDEZT = helper.getIBAStringValue(document, "CLDEZT");
                if(state.equals("APPROVED")||"已批准".equals(CLDEZT)){
                    //排除报表类工艺
                    if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")
                            &&!TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("reportTechnics")) {
                        list.add(document);
                        break;
                    }
                }
            }
        }
        return list;
    }

    public static List<GLErpPbomPartBean> getPBOMMatchInfo(WTPart part, List<GLErpElement> elementList) {
        List<GLErpPbomPartBean> erpPbomPartList = new  ArrayList<GLErpPbomPartBean>();
        if(elementList != null && !elementList.isEmpty()) {
            for (GLErpElement erpElement : elementList) {
                if(erpElement != null) {
                    String docoid = erpElement.getDocoid();
                    Element techEle = erpElement.getElement();
                    String planType =  techEle.attributeValue("PPLANTYPE");
                    String zfflag =  techEle.attributeValue("ZFFLAG");
                    Element gydeEle = techEle.element("GYDE");
                    if(gydeEle != null) {
                        Element eles = gydeEle.element("MATCHPART");
                        if(eles != null) {
                            List<Element> list = eles.elements();
                            if(list != null && !list.isEmpty()) {
                                GLErpPbomPartBean bean = null;
                                for (Element element : list) {
                                    String chbm = element.attributeValue("chbm");
                                    if(chbm == null || chbm.isEmpty()){
                                        continue;
                                    }
                                    bean = new GLErpPbomPartBean();
                                    bean.setPartNumber(element.attributeValue("number"));
                                    bean.setChbm(element.attributeValue("chbm"));
                                    bean.setChmc("");
                                    bean.setUseCount(element.attributeValue("useCount"));
                                    bean.setGyCount(element.attributeValue("gyCount"));
                                    bean.setXhph(element.attributeValue("xhph"));
                                    bean.setGg(element.attributeValue("gg"));
                                    bean.setJstj(element.attributeValue("jstj"));
                                    bean.setSccj(element.attributeValue("sccj"));
                                    bean.setDw(element.attributeValue("dw2"));
                                    bean.setFjtj(element.attributeValue("fjtj"));
                                    bean.setLwgggccc(element.attributeValue("lwgggccc"));
                                    bean.setJxxndj(element.attributeValue("jxxndj"));
                                    bean.setZldj(element.attributeValue("zldj"));
                                    bean.setFzxs(element.attributeValue("fzxs"));
                                    bean.setJddj(element.attributeValue("jddj"));
                                    bean.setDataFrom(element.attributeValue("dataFrom"));
                                    bean.setTechNumber(techEle.attributeValue("technicsNumber"));

                                    String pplanNumber = techEle.attributeValue("pplanNumber");
                                    String pplanName = techEle.attributeValue("pplanName");
                                    if(pplanNumber!=null &&!"".equals(pplanNumber)&&pplanName!=null &&!"".equals(pplanName)){
                                        String technicsName = pplanName +"("+pplanNumber+")";
                                        bean.setTechName(technicsName);
                                    }else{
                                        bean.setTechName(techEle.attributeValue("technicsName"));
                                    }
                                    bean.setPplanNumber(techEle.attributeValue("pplanNumber"));
                                    bean.setPplanType(planType);
                                    bean.setZfflag(zfflag);
                                    bean.setVersion(techEle.attributeValue("version"));
                                    bean.setDocoid(docoid);
                                    bean.setUsingType("工艺定额匹配");

                                    erpPbomPartList.add(bean);
                                }
                            }
                        }

                        eles = gydeEle.element("SJZYKMATCHPART");
                        if(eles != null) {
                            List<Element> list = eles.elements();
                            if(list != null && !list.isEmpty()) {
                                GLErpPbomPartBean bean = null;
                                for (Element element : list) {
                                    String chbm = element.attributeValue("sjbm");
                                    if(chbm == null || chbm.isEmpty()){
                                        continue;
                                    }
                                    bean = new GLErpPbomPartBean();
                                    bean.setPartNumber(element.attributeValue("partNumber"));
                                    bean.setChbm(element.attributeValue("sjbm"));
                                    bean.setChmc(element.attributeValue("name"));
                                    bean.setGyCount(element.attributeValue("gysl"));
                                    bean.setDw(element.attributeValue("dw"));
                                    bean.setXhph(element.attributeValue("ph"));
                                    bean.setGg(element.attributeValue("gg"));
                                    bean.setJstj(element.attributeValue("bzh"));

                                    bean.setSccj(element.attributeValue("gys"));
                                    bean.setFjtj(element.attributeValue("fjtj"));
                                    bean.setLwgggccc(element.attributeValue("lwgggccc"));
                                    bean.setJxxndj(element.attributeValue("jxxndjhyd"));
                                    bean.setZldj(element.attributeValue("zldj"));
                                    bean.setFzxs(element.attributeValue("fzxs"));
                                    bean.setJddj(element.attributeValue("jddj"));
                                    bean.setDataFrom(element.attributeValue("dataFrom"));
                                    bean.setTechNumber(techEle.attributeValue("technicsNumber"));

                                    String pplanNumber = techEle.attributeValue("pplanNumber");
                                    String pplanName = techEle.attributeValue("pplanName");
                                    if(pplanNumber!=null &&!"".equals(pplanNumber)&&pplanName!=null &&!"".equals(pplanName)){
                                        String technicsName = pplanName +"("+pplanNumber+")";
                                        bean.setTechName(technicsName);
                                    }else{
                                        bean.setTechName(techEle.attributeValue("technicsName"));
                                    }

                                    bean.setPplanNumber(techEle.attributeValue("pplanNumber"));
                                    bean.setVersion(techEle.attributeValue("version"));
                                    bean.setPplanType(planType);
                                    bean.setZfflag(zfflag);

                                    //一码通erp调用接口，发送时增加元器件新增属性 add by hz 2020/1/6
                                    bean.setBmdj(element.attributeValue("bmdj"));
                                    bean.setKfzbtid(element.attributeValue("kfzbtid"));
                                    bean.setKfzbsee(element.attributeValue("kfzbsee"));
                                    bean.setXncs(element.attributeValue("xncs"));
                                    bean.setJdmgdj_state(element.attributeValue("jdmgdj_state"));
                                    bean.setJdmgdj(element.attributeValue("jdmgdj"));
                                    bean.setSmdj(element.attributeValue("smdj"));
                                    bean.setDocoid(docoid);
                                    bean.setUsingType("工艺定额匹配");

                                    erpPbomPartList.add(bean);

                                }
                            }
                        }
                    }
                }
            }
        }
        return erpPbomPartList;
    }

    public static List<GLErpPbomPartBean> getPBOMNewPartInfo(WTPart part, List<GLErpElement> elementList) {
        List<GLErpPbomPartBean> partList = new ArrayList<GLErpPbomPartBean>();
        if(elementList != null && !elementList.isEmpty()) {
            for (GLErpElement erpElement : elementList) {
                if(erpElement != null) {
                    String docoid = erpElement.getDocoid();
                    Element techEle = erpElement.getElement();
                    String planType =  techEle.attributeValue("PPLANTYPE");
                    String zfflag =  techEle.attributeValue("ZFFLAG");
		            String dept =  techEle.attributeValue("DEPT");
                    Element gydeEle = techEle.element("GYDE");
                    if(gydeEle != null) {
						Element eles = gydeEle.element("NEWPART");
						if(eles != null) {
							List<Element> list = eles.elements();
							if(list != null && !list.isEmpty()) {
								GLErpPbomPartBean bean = null;
								for (Element element : list) {
									bean = new GLErpPbomPartBean();
									bean.setPartNumber(element.attributeValue("parentNumber"));
									bean.setChbm(element.attributeValue("chbm"));
									//bean.setChmc(element.attributeValue("chmc"));
									bean.setChmc("");
									bean.setXlcc(element.attributeValue("xlcc"));
									String kzjs = element.attributeValue("kzjs");
									if (Tools.isNull(kzjs)) {
										kzjs = element.attributeValue("sjkzjs");
									}
									bean.setKzjs(kzjs);
									bean.setSl(element.attributeValue("sl"));//gysl
									bean.setXhph(element.attributeValue("xhph"));
									bean.setGg(element.attributeValue("gg"));
									bean.setJstj(element.attributeValue("jstj"));
									bean.setSccj(element.attributeValue("sccj"));
									String  dw = element.attributeValue("dw2");
									if(Tools.isNull(dw)){
										 dw = element.attributeValue("dw");
									}
									bean.setDw(dw);
									bean.setFjtj(element.attributeValue("fjtj"));
									bean.setLwgggccc(element.attributeValue("lwgggccc"));
									bean.setJxxndj(element.attributeValue("jxxndj"));
									bean.setZldj(element.attributeValue("zldj"));
									bean.setFzxs(element.attributeValue("fzxs"));
									bean.setJddj(element.attributeValue("jddj"));
									bean.setDataFrom(element.attributeValue("dataFrom"));
									bean.setTechNumber(techEle.attributeValue("technicsNumber"));

									String pplanNumber = techEle.attributeValue("pplanNumber");
									String pplanName = techEle.attributeValue("pplanName");
									if(pplanNumber!=null &&!"".equals(pplanNumber)&&pplanName!=null &&!"".equals(pplanName)){
										String technicsName = pplanName +"("+pplanNumber+")";
										bean.setTechName(technicsName);
									}else{
										bean.setTechName(techEle.attributeValue("technicsName"));
									}

									bean.setPplanNumber(techEle.attributeValue("pplanNumber"));
									bean.setVersion(techEle.attributeValue("version"));
									bean.setPplanType(planType);
									bean.setZfflag(zfflag);
									bean.setDept(dept);
									bean.setDocoid(docoid);
                                    bean.setUsingType("工艺定额新增");
									partList.add(bean);
								}
							}
						}

						eles = gydeEle.element("SJZYKNEWPART");
						if(eles != null) {
							List<Element> list = eles.elements();
							if(list != null && !list.isEmpty()) {
								GLErpPbomPartBean bean = null;
								for (Element element : list) {
									bean = new GLErpPbomPartBean();
									bean.setPartNumber(element.attributeValue("parentPartNumber"));
									bean.setChbm(element.attributeValue("sjbm"));
									bean.setChmc(element.attributeValue("name"));
									String sl = element.attributeValue("gysl");
									if(Tools.isNull(sl)){
										sl = element.attributeValue("sl");
									}
									bean.setSl(sl);
									bean.setXlcc(element.attributeValue("xlcc"));
									String kzjs = element.attributeValue("kzjs");
									if (Tools.isNull(kzjs)) {
										kzjs = element.attributeValue("sjkzjs");
									}
									bean.setKzjs(kzjs);
									bean.setDw(element.attributeValue("dw"));
									bean.setXhph(element.attributeValue("ph"));
									bean.setGg(element.attributeValue("gg"));
									bean.setJstj(element.attributeValue("bzh"));
									bean.setSccj(element.attributeValue("gys"));
									bean.setFjtj(element.attributeValue("fjtj"));
									bean.setLwgggccc(element.attributeValue("lwgggccc"));
									bean.setJxxndj(element.attributeValue("jxxndjhyd"));
									bean.setZldj(element.attributeValue("zldj"));
									bean.setFzxs(element.attributeValue("fzxs"));
									bean.setJddj(element.attributeValue("jddj"));
									bean.setDataFrom(element.attributeValue("dataFrom"));
									bean.setTechNumber(techEle.attributeValue("technicsNumber"));

									String pplanNumber = techEle.attributeValue("pplanNumber");
									String pplanName = techEle.attributeValue("pplanName");
									if(pplanNumber!=null &&!"".equals(pplanNumber)&&pplanName!=null &&!"".equals(pplanName)){
										String technicsName = pplanName +"("+pplanNumber+")";
										bean.setTechName(technicsName);
									}else{
										bean.setTechName(techEle.attributeValue("technicsName"));
									}
									bean.setPplanNumber(techEle.attributeValue("pplanNumber"));
									bean.setVersion(techEle.attributeValue("version"));
									bean.setPplanType(planType);
									bean.setZfflag(zfflag);
									bean.setDept(dept);

									//一码通erp调用接口，发送时增加元器件新增属性 add by hz 2020/1/6
									bean.setBmdj(element.attributeValue("bmdj"));
									bean.setKfzbtid(element.attributeValue("kfzbtid"));
									bean.setKfzbsee(element.attributeValue("kfzbsee"));
									bean.setXncs(element.attributeValue("xncs"));
									bean.setJdmgdj_state(element.attributeValue("jdmgdj_state"));
									bean.setJdmgdj(element.attributeValue("jdmgdj"));
									bean.setSmdj(element.attributeValue("smdj"));
									bean.setDocoid(docoid);
                                    bean.setUsingType("工艺定额新增");

									partList.add(bean);
								}
							}
						}
					}
                }
            }
        }

        return partList;
    }

    public static String getYclInfo(WTPart latePart, List<GLErpMaterialsBean> materialsBeanList ) {
        StringBuffer yclBuffer = new StringBuffer();
        try {
            IBAUtility utility = new IBAUtility(latePart);
            String type = utility.getIBAValue("MTYPE");
            String parenNum = latePart.getNumber();//材料上级图号
            String parentName = latePart.getName();//上级名称
            String parenPhase = utility.getIBAValue("PHASE_CODE");
            String BATCH = utility.getIBAValue("BATCH");//批次号
			String pzzcj = utility.getIBAValue("ZZCJ");

            if(BATCH==null||"null".equals(BATCH)){
                BATCH= "";
            }
            String parentversion = latePart.getIterationDisplayIdentifier().toString();//版本
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
            for(GLErpMaterialsBean bean:materialsBeanList){
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
				comment=bean.getNote();

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
                yclBuffer.append(ProductBomService.createLinkXML(parenNum,parentName,parentversion,type,parenPhase,childNumber,childName,"",childType,"",
						sl,dw, cldelb, xlcc,kzjs,sjsl,sjcc,sjkzjs, sl,processFileNum,processFileName,processFileVersion,dataFrom,dw,comment,BATCH,index+"",processFileNumber,pplanType,zfflag,extAttrs));

            }
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return yclBuffer.toString();
    }

    public  static List<GLErpMaterialsBean> getCldeInfo(List<GLErpElement> elementList) {
        List<GLErpMaterialsBean> list = new ArrayList<GLErpMaterialsBean>();
        for (GLErpElement erpElement : elementList) {
                String docoid = erpElement.getDocoid();
                Element technicsElement = erpElement.getElement();
                boolean isHasCLDE = false;
                String pplanNumber = technicsElement.attributeValue("technicsNumber");
                String processFileNumber = technicsElement.attributeValue("pplanNumber");
                String pplanType = technicsElement.attributeValue("PPLANTYPE");
                String zfflag = technicsElement.attributeValue("ZFFLAG");
                String batch = technicsElement.attributeValue("PCNO");

                if(pplanType==null ||zfflag==null){//这时候是报表类工艺文件
                    continue;
                }

                String pplanName = "";
                String pum = technicsElement.attributeValue("pplanNumber");
                String pname = technicsElement.attributeValue("pplanName");
                if(pum!=null &&!"".equals(pum)&&pname!=null &&!"".equals(pname)){
                    pplanName= pname +"("+pum+")";
                }else{
                    pplanName = technicsElement.attributeValue("technicsName");
                }
                String dept = technicsElement.attributeValue("DEPT");

                String pplanVersion = technicsElement.attributeValue("version");
                String pplanState = technicsElement.attributeValue("lifecycle");
                Iterator ite2 = technicsElement.elementIterator();
                while(ite2.hasNext()) {

                	Element cldeElement = (Element)ite2.next();
                	if("GYDE".equals(cldeElement.getName())){
						List<Element> zycldeElements = cldeElement.selectNodes("ZYCLDE/zycldeRecord");
						for(Element zyclde : zycldeElements){
							GLErpMaterialsBean bean = new GLErpMaterialsBean();
							bean.setMaterialType("外购件");
							bean.setPplanNumber(pplanNumber);
							bean.setPplanName(pplanName);
							bean.setPplanVersion(pplanVersion);
							bean.setPplanState(pplanState);
							bean.setBatch(batch);
							bean.setProcessFileNumber(processFileNumber);
							bean.setPplanType(pplanType);
							bean.setZfflag(zfflag);
							bean.setDept(dept);

							bean.setUsingType("主要材料");
							bean.setChbm(zyclde.attributeValue("chbm"));
							//bean.setChmc(ele.attributeValue("chmc"));
							bean.setChmc("");
							bean.setZjldw(zyclde.attributeValue("zjldw"));
							bean.setDataFrom(zyclde.attributeValue("dataFrom"));
                            bean.setXlcc(zyclde.attributeValue("xlcc"));
                            bean.setSjkzjs(zyclde.attributeValue("sjkzjs"));
                            bean.setKzjs(zyclde.attributeValue("kzjs"));
							bean.setSl(zyclde.attributeValue("sl"));
							bean.setDw(zyclde.attributeValue("dw"));
							bean.setNote(zyclde.attributeValue("comment"));
							bean.setDocoid(docoid);
							list.add(bean);
							isHasCLDE = true;
						}
						List<Element> sjycldeElements = cldeElement.selectNodes("SJYCLDE/sjycldeRecord");
						for(Element slyclde : sjycldeElements){
							GLErpMaterialsBean bean = new GLErpMaterialsBean();
							bean.setMaterialType("外购件");
							bean.setPplanNumber(pplanNumber);
							bean.setPplanName(pplanName);
							bean.setPplanVersion(pplanVersion);
							bean.setPplanState(pplanState);
							bean.setBatch(batch);
							bean.setProcessFileNumber(processFileNumber);
							bean.setPplanType(pplanType);
							bean.setZfflag(zfflag);
							bean.setDept(dept);

							bean.setUsingType("试件原材料");
							bean.setChbm(slyclde.attributeValue("chbm"));
							bean.setChmc(slyclde.attributeValue("name"));
							bean.setZjldw(slyclde.attributeValue("jldw"));
							bean.setDataFrom(slyclde.attributeValue("dataFrom"));
                            bean.setXlcc(slyclde.attributeValue("xlcc"));
                            bean.setKzjs(slyclde.attributeValue("kzjs"));

							bean.setSjcc(slyclde.attributeValue("sjcc"));
							bean.setSjkzjs(slyclde.attributeValue("sjkzjs"));
							bean.setSjsl(slyclde.attributeValue("sjsl"));
							bean.setSl(slyclde.attributeValue("sl"));
							bean.setDw(slyclde.attributeValue("dw"));
							bean.setNote(slyclde.attributeValue("comment"));
							bean.setDocoid(docoid);

							list.add(bean);
							isHasCLDE = true;
						}
					}
                	if(!"CLDE".equals(cldeElement.getName())) {
                		continue;
                	}
                	Iterator ite3 = cldeElement.elementIterator();
                	while(ite3.hasNext()) {
                		Element element = (Element)ite3.next();
                		Iterator ite4 = element.elementIterator();
                		while(ite4.hasNext()) {
                			Element ele = (Element)ite4.next();
                			GLErpMaterialsBean bean = new GLErpMaterialsBean();
                			bean.setMaterialType("外购件");
                			bean.setPplanNumber(pplanNumber);
                			bean.setPplanName(pplanName);
                			bean.setPplanVersion(pplanVersion);
                			bean.setPplanState(pplanState);
                			bean.setBatch(batch);
                			bean.setProcessFileNumber(processFileNumber);
                			bean.setPplanType(pplanType);
                			bean.setZfflag(zfflag);
							bean.setDept(dept);
							bean.setDocoid(docoid);

                			if("YCLDE".equals(element.getName())) {
                				bean.setUsingType("原材料");
                				bean.setChbm(ele.attributeValue("chbm"));
                    			//bean.setChmc(ele.attributeValue("chmc"));
                				bean.setChmc("");
                    			bean.setZjldw(ele.attributeValue("zjldw"));
                    			bean.setDataFrom(ele.attributeValue("dataFrom"));

                				bean.setXlcc(ele.attributeValue("xlcc"));
                				bean.setKzjs(ele.attributeValue("kzjs"));
                                bean.setSjkzjs(ele.attributeValue("sjkzjs"));
								bean.setSl("");
                				bean.setDw(ele.attributeValue("dw"));
                				bean.setNote(ele.attributeValue("comment"));

                				list.add(bean);
                				isHasCLDE = true;
                    		}
                			else if ("SJYCLDE".equals(element.getName())) {
                    			bean.setUsingType("试件原材料");
                    			bean.setChbm(ele.attributeValue("chbm"));
                    			//bean.setChmc(ele.attributeValue("chmc"));
                    			bean.setChmc("");
                    			bean.setZjldw(ele.attributeValue("zjldw"));
                    			bean.setDataFrom(ele.attributeValue("dataFrom"));

                                bean.setXlcc(ele.attributeValue("xlcc"));
                                bean.setSjcc(ele.attributeValue("sjcc"));
                    			bean.setSjkzjs(ele.attributeValue("sjkzjs"));
                                bean.setKzjs(ele.attributeValue("kzjs"));
								bean.setSl(ele.attributeValue("sl"));
                    			bean.setSjsl(ele.attributeValue("sjsl"));
                    			bean.setDw(ele.attributeValue("dw"));
                				bean.setNote(ele.attributeValue("comment"));
                    			list.add(bean);
                    			isHasCLDE = true;
                    		}
                			else if ("ZYCLDE".equals(element.getName())) {
                    			bean.setUsingType("主要材料");
                    			bean.setChbm(ele.attributeValue("chbm"));
                    			//bean.setChmc(ele.attributeValue("chmc"));
                    			bean.setChmc("");
                    			bean.setZjldw(ele.attributeValue("zjldw"));
                    			bean.setDataFrom(ele.attributeValue("dataFrom"));
                                bean.setXlcc(ele.attributeValue("xlcc"));
                                bean.setSjkzjs(ele.attributeValue("sjkzjs"));
                                bean.setKzjs(ele.attributeValue("kzjs"));
                    			bean.setSl(ele.attributeValue("sl"));
                    			bean.setDw(ele.attributeValue("dw"));
                				bean.setNote(ele.attributeValue("comment"));
                    			list.add(bean);
                    			isHasCLDE = true;
                    		}
                			else if("SJZYKYCLDE".equals(element.getName())) {
                				bean.setUsingType("原材料");
                				bean.setChbm(ele.attributeValue("sjbm"));
                    			bean.setChmc(ele.attributeValue("name"));
                    			bean.setZjldw(ele.attributeValue("jldw"));
                    			bean.setDataFrom(ele.attributeValue("dataFrom"));

                				bean.setXlcc(ele.attributeValue("xlcc"));
                                bean.setSjkzjs(ele.attributeValue("sjkzjs"));
                                bean.setKzjs(ele.attributeValue("kzjs"));
								bean.setSl("");
								bean.setDw(ele.attributeValue("dw"));
                				bean.setNote(ele.attributeValue("comment"));
                				list.add(bean);
                				isHasCLDE = true;
                    		}
                			else if ("SJZYKSJYCLDE".equals(element.getName())) {
                    			bean.setUsingType("试件原材料");
                    			bean.setChbm(ele.attributeValue("sjbm"));
                    			bean.setChmc(ele.attributeValue("name"));
                    			bean.setZjldw(ele.attributeValue("jldw"));
                    			bean.setDataFrom(ele.attributeValue("dataFrom"));
                                bean.setXlcc(ele.attributeValue("xlcc"));

                                bean.setKzjs(ele.attributeValue("kzjs"));
                    			bean.setSjcc(ele.attributeValue("sjcc"));
                    			bean.setSjkzjs(ele.attributeValue("sjkzjs"));
                    			bean.setSjsl(ele.attributeValue("sjsl"));
								bean.setSl(ele.attributeValue("sl"));
                    			bean.setDw(ele.attributeValue("dw"));
                				bean.setNote(ele.attributeValue("comment"));
                    			list.add(bean);
                    			isHasCLDE = true;
                    		}
                			else if ("SJZYKZYCLDE".equals(element.getName())) {
                    			bean.setUsingType("主要材料");
                    			bean.setChbm(ele.attributeValue("sjbm"));
                    			bean.setChmc(ele.attributeValue("name"));
                    			bean.setZjldw(ele.attributeValue("jldw"));
                    			bean.setDataFrom(ele.attributeValue("dataFrom"));
                                bean.setXlcc(ele.attributeValue("xlcc"));
                                bean.setSjkzjs(ele.attributeValue("sjkzjs"));
                                bean.setKzjs(ele.attributeValue("kzjs"));
                    			bean.setSl(ele.attributeValue("sl"));
                    			bean.setDw(ele.attributeValue("dw"));
                				bean.setNote(ele.attributeValue("comment"));
                    			list.add(bean);
                    			isHasCLDE = true;
                    		}
                		}
                	}

                }

            	//if(!isHasCLDE&&Constants.PROCESS_TYPEB_TEMP.equals(pplanType)){//如果是临时工艺文件
				if(!isHasCLDE){//去掉临时工艺限制，20210818 倪勇军、范文正提出修改
            		boolean isShowN = true;
            		List listEle = technicsElement.selectNodes("GYDE/MATCHPART/MatchPart");
            		if(listEle != null && !listEle.isEmpty()) {
            			isShowN = false;
            		}
            		if(isShowN){
            			listEle = technicsElement.selectNodes("GYDE/NEWPART/NewPart");
            			if(listEle != null && !listEle.isEmpty()) {
            				isShowN = false;
                		}
            		}
            		if(isShowN){
            			listEle = technicsElement.selectNodes("GYDE/SJZYKMATCHPART/SjzykMatchPart");
            			if(listEle != null && !listEle.isEmpty()) {
            				isShowN = false;
                		}
            		}
            		if(isShowN){
            			listEle = technicsElement.selectNodes("GYDE/SJZYKNEWPART/SjzykNewPart");
            			if(listEle != null && !listEle.isEmpty()) {
            				isShowN = false;
                		}
            		}

                    if(isShowN){
                    	GLErpMaterialsBean bean = new GLErpMaterialsBean();
            			bean.setMaterialType("N");
            			bean.setPplanNumber(pplanNumber);
            			bean.setPplanName(pplanName);
            			bean.setPplanVersion(pplanVersion);
            			bean.setPplanState(pplanState);
            			bean.setChbm("N");
            			bean.setChmc("N");
            			bean.setBatch(batch);
            			bean.setProcessFileNumber(processFileNumber);
            			bean.setPplanType(pplanType);
            			bean.setZfflag(zfflag);
						bean.setDept(dept);
						bean.setDocoid(docoid);

						list.add(bean);
                    }
                }

        }
        return list;
    }


    public static List<GLErpElement> getElementList(List<WTDocument> documentList) throws WTException, PropertyVetoException, DocumentException{
        List<GLErpElement> erpElements = new ArrayList<GLErpElement>();
        if(documentList==null) return erpElements;
        for(WTDocument doc :documentList){
            ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
            if(data == null) {
                return null;
            }
            String zipFilePath = BomUtil.zip_temp_dir+ File.separator+"erpTemp"+File.separator+doc.getNumber()+File.separator+doc.getNumber();
            File techDir = new File(zipFilePath);
            if(!techDir.exists()) {
                techDir.mkdirs();
            }
            String xmlFile = zipFilePath+File.separator+doc.getNumber()+".xml";
            byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
            ZipUtil.unZip(bytes, zipFilePath);
            File file = new File(xmlFile);
            if(!file.exists()) {
                System.out.println(xmlFile+" is not exist!");
                return null;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();

            //工艺文件信息
            List pplanList = rootElement.selectNodes("//QMFawTechnicsInfo");
            for (Object object : pplanList) {
                Element element = (Element)object;
                CldeUtil.deleteFiles(new File(BomUtil.zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()));
                erpElements.add(new GLErpElement(PersistenceCommonHelper.getOid(doc),element));
            }
        }
        return erpElements;
    }


    public static List<GLErpPeiTaoPartBean> getPeiTaoPbomInfo(WTPart parentPart, List<GLErpElement> elementList,String batchversion) {
        List<GLErpPeiTaoPartBean> peitaoList = new ArrayList<GLErpPeiTaoPartBean>();

       // Map<String,WTPart>  realChilds = new HashMap<>();
        try {
           /* QueryResult tmpList = WTPartHelper.service.getUsesWTPartMasters(parentPart);
            while(tmpList.hasMoreElements()){
                WTPartUsageLink link = (WTPartUsageLink)tmpList.nextElement();
                WTPartMaster part1 = (WTPartMaster)link.getRoleBObject();
                WTPart latePart = BomUtil.getLatestPartByBatchView(part1, batchversion, "Manufacturing");
                if(latePart != null) {
                    realChilds.put(latePart.getNumber(),latePart);
                }
            }
            realChilds.put(parentPart.getNumber(),parentPart);*/

            if(elementList != null && !elementList.isEmpty()) {
                for (GLErpElement erpElement : elementList) {
                    if(erpElement != null) {
                        String docoid = erpElement.getDocoid();
                        Element techEle = erpElement.getElement();
                        String dept =  techEle.attributeValue("DEPT");
                        String pplanType = techEle.attributeValue("PPLANTYPE");
                        String zfflag = techEle.attributeValue("ZFFLAG");

                        String pplanNumber = techEle.attributeValue("pplanNumber");
                        String technicsNumber =  techEle.attributeValue("technicsNumber");
                        String pplanName = techEle.attributeValue("technicsName");
                        String pplanVersion = techEle.attributeValue("version");
                        Element peiTaoElements = techEle.element("PEITAOTABLE");
                        if(peiTaoElements != null) {
                            List<Element> list = peiTaoElements.elements();
                            GLErpPeiTaoPartBean bean = null;
                            if(list!=null){
                                 for (Element element : list) {
                                     String version = element.attributeValue("version");
                                     String dataFrom = element.attributeValue("dataFrom");
                                     if(!Tools.isNull(version)||"pbom".equals(dataFrom)){
                                        String peiTaoNumber = element.attributeValue("number");
                                         // WTPart childPart = realChilds.get(peiTaoNumber);
                                         WTPart  childPart  = WTPartUtil.getLatestPartByNumberAndView(peiTaoNumber,"Manufacturing");

                                        if(childPart == null) continue;

                                        bean = new GLErpPeiTaoPartBean();
                                        bean.setDocoid(docoid);
                                        bean.setChildNumber(peiTaoNumber);
                                        bean.setChildName(Tools.convertNull(element.attributeValue("name")));
                                        bean.setAmount(Tools.convertNull(element.attributeValue("useCount")));
                                        bean.setUnit(Tools.getNullValue(element.attributeValue("dw"),"个"));

                                        IBAUtility ibaUtility = new IBAUtility(childPart);

                                        String partType = ibaUtility.getIBAValue("MTYPE");
                                        if(partType==null||"".equals(partType)){
                                            partType = ibaUtility.getIBAValue("CTYPE");
                                        }
                                        String BATCH = ibaUtility.getIBAValue("BATCH");
                                        if(BATCH==null||"null".equals(BATCH)){
                                            BATCH= "";
                                        }
                                        String childPhase = ibaUtility.getIBAValue("PHASE_CODE");//研制阶段
                                        String childzzcj = ibaUtility.getIBAValue("ZZCJ");

                                        String nowVersion = childPart.getIterationDisplayIdentifier().toString();
                                        String setmark =   ibaUtility.getIBAValue("SETMARK");
                                        bean.setPartType(partType);
                                        bean.setBatch(Tools.convertNull(BATCH));
                                        bean.setDept(dept);
                                        bean.setChildPhase(Tools.convertNull(childPhase));
                                        bean.setChildName(childPart.getName());
                                        bean.setChildVersion(nowVersion);
                                        bean.setZzcj(childzzcj);
                                        bean.setPplanNumber(pplanNumber);
                                        bean.setTechName(pplanName);
                                        bean.setVersion(pplanVersion);
                                        bean.setTechNumber(technicsNumber);
                                        bean.setPplanType(pplanType);
                                        bean.setZfflag(zfflag);
                                        bean.setSetmark(setmark);
                                        peitaoList.add(bean);
                                    }
                                 }
                            }
                        }
                    }
                }
            }
        } catch (WTException e) {
            e.printStackTrace();
        }

        return peitaoList;
    }

    public static void getYclJson(HashMap<String,String> partAttris, List<GLErpMaterialsBean> materialsBeanList, JSONArray jsonArray) throws WTException {
        int index = 1;
        for(GLErpMaterialsBean materialsBean:materialsBeanList){
           JSONObject jsonObject = new JSONObject();
           jsonObject.put("parentNumber",partAttris.get("parentNumber"));
           jsonObject.put("parentName",partAttris.get("parentName"));
           jsonObject.put("parentVersion",partAttris.get("parentVersion"));
           jsonObject.put("parentType",partAttris.get("parentType"));
           jsonObject.put("parentPhase",partAttris.get("parentPhase"));
           jsonObject.put("childNumber",Tools.convertNull(materialsBean.getChbm()));
           jsonObject.put("childName",Tools.convertNull(materialsBean.getChmc()));
           jsonObject.put("childVersion","");
           jsonObject.put("childType",Tools.convertNull(materialsBean.getMaterialType()));
           jsonObject.put("childPhase",Tools.convertNull(partAttris.get("parentPhase")));
           jsonObject.put("usingAmount",Tools.convertNull(materialsBean.getSl()));
           jsonObject.put("unit",Tools.convertNull(materialsBean.getDw()));
           jsonObject.put("batch",Tools.convertNull(partAttris.get("parentBATCH")));
           if(materialsBean.getUsingType()!=null){
               if(materialsBean.getUsingType().endsWith("定额")){
                   jsonObject.put("cldelb",materialsBean.getUsingType().replaceAll("定额",""));
               }else{
                   jsonObject.put("cldelb",materialsBean.getUsingType());
               }
           }else{
               jsonObject.put("cldelb","材料定额");
           }
           jsonObject.put("xlcc",Tools.convertNull(materialsBean.getXlcc()));
           jsonObject.put("kzjs",Tools.convertNull(materialsBean.getKzjs()));
           jsonObject.put("sjsl",Tools.convertNull(materialsBean.getSjsl()));
           jsonObject.put("sjcc",Tools.convertNull(materialsBean.getSjcc()));
           jsonObject.put("sjkzjs",Tools.convertNull(materialsBean.getSjkzjs()));
           jsonObject.put("sl",Tools.convertNull(materialsBean.getSl()));
           jsonObject.put("unit2",Tools.convertNull(materialsBean.getDw()));
           jsonObject.put("comment",Tools.convertNull(materialsBean.getNote()));
           jsonObject.put("index",index+"");
           jsonObject.put("processFileNum",Tools.convertNull(materialsBean.getPplanNumber()));
           jsonObject.put("processFileName",Tools.convertNull(materialsBean.getPplanName()));
           jsonObject.put("processFileVersion",Tools.convertNull(materialsBean.getPplanVersion()));
           jsonObject.put("processFileNumber",Tools.convertNull(materialsBean.getProcessFileNumber()));
           jsonObject.put("pplanType",Tools.convertNull(materialsBean.getPplanType()));
           jsonObject.put("zfflag",Tools.convertNull(materialsBean.getZfflag()));
           jsonObject.put("parentFactory",Tools.convertNull(partAttris.get("parentFactory")));
           jsonObject.put("childFactory",Tools.convertNull(partAttris.get("parentFactory")));
           jsonObject.put("dept",Tools.convertNull(partAttris.get("dept")));
           jsonObject.put("SETMARK",Tools.convertNull(partAttris.get("SETMARK")));
           jsonArray.put(jsonObject);
           index++;
       }

    }

    public static void getNewPartJson(HashMap<String,String> partAttris, List<GLErpPbomPartBean> erpNewPartList, JSONArray jsonArray) {

        int index = 1;
        for(GLErpPbomPartBean erpPbomPartBean:erpNewPartList){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("parentNumber",partAttris.get("parentNumber"));
            jsonObject.put("parentName",partAttris.get("parentName"));
            jsonObject.put("parentVersion",partAttris.get("parentVersion"));
            jsonObject.put("parentType",partAttris.get("parentType"));
            jsonObject.put("parentPhase",partAttris.get("parentPhase"));
            jsonObject.put("childNumber",Tools.convertNull(erpPbomPartBean.getChbm()));
            jsonObject.put("childName",Tools.convertNull(erpPbomPartBean.getChmc()));
            jsonObject.put("childVersion", "");
            jsonObject.put("childType","外购件");
            jsonObject.put("childPhase",Tools.convertNull(partAttris.get("parentPhase")));
            jsonObject.put("usingAmount",Tools.convertNull(erpPbomPartBean.getSl()));
            jsonObject.put("unit",Tools.convertNull(erpPbomPartBean.getDw()));
            jsonObject.put("batch",Tools.convertNull(partAttris.get("parentBATCH")));
            jsonObject.put("cldelb","工艺定额新增");
            jsonObject.put("xlcc",Tools.convertNull(erpPbomPartBean.getXlcc()));
            jsonObject.put("kzjs",Tools.convertNull(erpPbomPartBean.getKzjs()));
            jsonObject.put("sjsl","");
            jsonObject.put("sjcc","");
            jsonObject.put("sjkzjs","");
            jsonObject.put("sl",Tools.convertNull(erpPbomPartBean.getSl()));
            jsonObject.put("unit2",Tools.convertNull(erpPbomPartBean.getDw()));
            jsonObject.put("comment","");
            jsonObject.put("index",index+"");
            jsonObject.put("processFileNum",Tools.convertNull(erpPbomPartBean.getTechNumber()));
            jsonObject.put("processFileName",Tools.convertNull(erpPbomPartBean.getTechName()));
            jsonObject.put("processFileVersion",Tools.convertNull(erpPbomPartBean.getVersion()));
            jsonObject.put("processFileNumber",Tools.convertNull(erpPbomPartBean.getPplanNumber()));
            jsonObject.put("dataFrom",Tools.convertNull(erpPbomPartBean.getDataFrom()));
            jsonObject.put("pplanType",Tools.convertNull(erpPbomPartBean.getPplanType()));
            jsonObject.put("zfflag",Tools.convertNull(erpPbomPartBean.getZfflag()));
            jsonObject.put("parentFactory",partAttris.get("parentFactory"));
            jsonObject.put("childFactory","");
            jsonObject.put("dept",Tools.convertNull(erpPbomPartBean.getDept()));
            jsonObject.put("SETMARK",partAttris.get("SETMARK"));
            jsonObject.put("bmdj",Tools.convertNull(erpPbomPartBean.getBmdj()));
            jsonObject.put("kfzbtid",Tools.convertNull(erpPbomPartBean.getKfzbtid()));
            jsonObject.put("kfzbsee",Tools.convertNull(erpPbomPartBean.getKfzbsee()));
            jsonObject.put("xncs",Tools.convertNull(erpPbomPartBean.getXncs()));
            jsonObject.put("jdmgdj_state",Tools.convertNull(erpPbomPartBean.getJdmgdj_state()));
            jsonObject.put("jdmgdj",Tools.convertNull(erpPbomPartBean.getJdmgdj()));
            jsonObject.put("smdj",Tools.convertNull(erpPbomPartBean.getSmdj()));
            jsonArray.put(jsonObject);
            index++;
        }

    }

    public static void getMatchPartJson(HashMap<String,String> partAttris, List<GLErpPbomPartBean> erpMatchPartList, JSONArray jsonArray) {
        int index = 1;
        for(GLErpPbomPartBean erpPbomPartBean:erpMatchPartList){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("parentNumber",partAttris.get("parentNumber"));
            jsonObject.put("parentName",partAttris.get("parentName"));
            jsonObject.put("parentVersion",partAttris.get("parentVersion"));
            jsonObject.put("parentType",partAttris.get("parentType"));
            jsonObject.put("parentPhase",partAttris.get("parentPhase"));
            jsonObject.put("childNumber",Tools.convertNull(erpPbomPartBean.getChbm()));
            jsonObject.put("childName",Tools.convertNull(erpPbomPartBean.getChmc()));
            jsonObject.put("childVersion", "");
            jsonObject.put("childType","外购件");
            jsonObject.put("childPhase",partAttris.get("parentPhase"));
            jsonObject.put("usingAmount",Tools.convertNull(erpPbomPartBean.getSl()));
            jsonObject.put("unit",Tools.convertNull(erpPbomPartBean.getDw()));
            jsonObject.put("batch",partAttris.get("parentBATCH"));
            jsonObject.put("cldelb","工艺定额匹配");
            jsonObject.put("xlcc",Tools.convertNull(erpPbomPartBean.getXlcc()));
            jsonObject.put("kzjs",Tools.convertNull(erpPbomPartBean.getKzjs()));
            jsonObject.put("sjsl","");
            jsonObject.put("sjcc","");
            jsonObject.put("sjkzjs","");
            jsonObject.put("sl",Tools.convertNull(erpPbomPartBean.getSl()));
            jsonObject.put("unit2",Tools.convertNull(erpPbomPartBean.getDw()));
            jsonObject.put("comment","");
            jsonObject.put("index",index+"");
            jsonObject.put("processFileNum",Tools.convertNull(erpPbomPartBean.getTechNumber()));
            jsonObject.put("processFileName",Tools.convertNull(erpPbomPartBean.getTechName()));
            jsonObject.put("processFileVersion",Tools.convertNull(erpPbomPartBean.getVersion()));
            jsonObject.put("processFileNumber",Tools.convertNull(erpPbomPartBean.getPplanNumber()));
            jsonObject.put("dataFrom",Tools.convertNull(erpPbomPartBean.getDataFrom()));
            jsonObject.put("pplanType",Tools.convertNull(erpPbomPartBean.getPplanType()));
            jsonObject.put("zfflag",Tools.convertNull(erpPbomPartBean.getZfflag()));
            jsonObject.put("parentFactory",partAttris.get("parentFactory"));
            jsonObject.put("childFactory",Tools.convertNull(erpPbomPartBean.getDept()));
            jsonObject.put("dept",Tools.convertNull(erpPbomPartBean.getDept()));
            jsonObject.put("SETMARK",partAttris.get("SETMARK"));
            jsonObject.put("bmdj",Tools.convertNull(erpPbomPartBean.getBmdj()));
            jsonObject.put("kfzbtid",Tools.convertNull(erpPbomPartBean.getKfzbtid()));
            jsonObject.put("kfzbsee",Tools.convertNull(erpPbomPartBean.getKfzbsee()));
            jsonObject.put("xncs",Tools.convertNull(erpPbomPartBean.getXncs()));
            jsonObject.put("jdmgdj_state",Tools.convertNull(erpPbomPartBean.getJdmgdj_state()));
            jsonObject.put("jdmgdj",Tools.convertNull(erpPbomPartBean.getJdmgdj()));
            jsonObject.put("smdj",Tools.convertNull(erpPbomPartBean.getSmdj()));
            jsonArray.put(jsonObject);
            index++;
        }
    }

    public static void getPeiTaoPbomJson(HashMap<String,String> partAttris, List<GLErpPeiTaoPartBean> erpPeiTaoPartBeans, JSONArray jsonArray) {
        int index = 1;
        for(GLErpPeiTaoPartBean erpPeiTaoPartBean:erpPeiTaoPartBeans){
            JSONObject jsonObject = new JSONObject();
            String unit;
            if("每个".equals(erpPeiTaoPartBean.getUnit())){
                unit = "个";
            }else{
                unit = erpPeiTaoPartBean.getUnit();
            }
            jsonObject.put("parentNumber",partAttris.get("parentNumber"));
            jsonObject.put("parentName",partAttris.get("parentName"));
            jsonObject.put("parentVersion",partAttris.get("parentVersion"));
            jsonObject.put("parentType",partAttris.get("parentType"));
            jsonObject.put("parentPhase",partAttris.get("parentPhase"));
            jsonObject.put("childNumber",erpPeiTaoPartBean.getChildNumber());
            jsonObject.put("childName",erpPeiTaoPartBean.getChildName());
            jsonObject.put("childVersion",erpPeiTaoPartBean.getChildVersion());
            jsonObject.put("childType",Tools.convertNull(erpPeiTaoPartBean.getPartType()));
            jsonObject.put("childPhase",Tools.convertNull(erpPeiTaoPartBean.getChildPhase()));
            jsonObject.put("usingAmount",erpPeiTaoPartBean.getAmount());
            jsonObject.put("unit",unit);
            jsonObject.put("batch",Tools.convertNull(erpPeiTaoPartBean.getBatch()));
            jsonObject.put("cldelb","配套表");
            jsonObject.put("xlcc","");
            jsonObject.put("kzjs","");
            jsonObject.put("sjsl","");
            jsonObject.put("sjcc","");
            jsonObject.put("sjkzjs","");
            jsonObject.put("sl",erpPeiTaoPartBean.getAmount());
            jsonObject.put("unit2",unit);
            jsonObject.put("comment","");
            jsonObject.put("index",index+"");
            jsonObject.put("processFileNum",erpPeiTaoPartBean.getTechNumber());
            jsonObject.put("processFileName",erpPeiTaoPartBean.getTechName());
            jsonObject.put("processFileVersion",erpPeiTaoPartBean.getVersion());
            jsonObject.put("processFileNumber",erpPeiTaoPartBean.getPplanNumber());
            jsonObject.put("pplanType",erpPeiTaoPartBean.getPplanType());
            jsonObject.put("zfflag",erpPeiTaoPartBean.getZfflag());
            jsonObject.put("dataFrom","配套表");
            jsonObject.put("parentFactory",Tools.convertNull(partAttris.get("parentFactory")));
            jsonObject.put("childFactory",Tools.convertNull(erpPeiTaoPartBean.getZzcj()));
            jsonObject.put("dept",Tools.convertNull(erpPeiTaoPartBean.getDept()));
            jsonObject.put("SETMARK",Tools.convertNull(erpPeiTaoPartBean.getSetmark()));

            jsonArray.put(jsonObject);
            index++;
        }
    }


}
