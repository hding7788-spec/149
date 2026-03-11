package ext.casc.integrate.util;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.*;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.wvs.server.util.PublishUtils;
import ext.casc.integrate.model.PbomErpPartBean;
import ext.casc.util.*;
import ext.casc.util.IBAHelper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.part.*;
import wt.pds.StatementSpec;
import wt.query.*;
import wt.representation.Representation;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.ViewHelper;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class BomUtil {

	public static String wt_temp;
	public static String zip_temp_dir;
	static int index[] = { 0 };

	static {
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			wt_temp = pro.getProperty("wt.temp");
			zip_temp_dir = wt_temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*根据图号获取最新版本(批次)的part

	*/
	 public static WTPart getPartByTuHao(String cindex) throws WTException, WTPropertyVetoException {
	        WTPart part = null;
	        QuerySpec qSpec = new QuerySpec();
	        int index0 = qSpec.addClassList(WTPart.class, true);
	        int index1 = qSpec.addClassList(StringValue.class, false);
	        int index2 = qSpec.addClassList(StringDefinition.class, false);

	        String[] aliases = new String[3];
	        aliases[0] = qSpec.getFromClause().getAliasAt(index0);
	        aliases[1] = qSpec.getFromClause().getAliasAt(index1);
	        aliases[2] = qSpec.getFromClause().getAliasAt(index2);

	        TableColumn tc0 = new TableColumn(aliases[0], "ida2a2");
	        TableColumn tc1 = new TableColumn(aliases[1], "IDA3A4");
	        TableColumn tc2 = new TableColumn(aliases[1], "IDA3A6");
	        TableColumn tc3 = new TableColumn(aliases[1], "value");
	        TableColumn tc4 = new TableColumn(aliases[2], "IDA2A2");
	        TableColumn tc5 = new TableColumn(aliases[2], "name");
	      //TableColumn tc6 = new TableColumn(aliases[0], "number");//part的编码

	        qSpec.appendWhere(new SearchCondition(tc0, "=", tc1), new int[] { index0, index1 });
	        qSpec.appendAnd();
	        qSpec.appendWhere(new SearchCondition(tc2, "=", tc4), new int[] { index1, index2 });
	        qSpec.appendAnd();
	        qSpec.appendWhere(new SearchCondition(tc3, "=", new ConstantExpression(cindex)), new int[] { index1 });
	        qSpec.appendAnd();
	        qSpec.appendWhere(new SearchCondition(tc5, "=", new ConstantExpression("CINDEX")), new int[] { index2 });
	        // qSpec.appendGroupBy(tc6, new int[] { index0 }, false);//按part的编码进行组合排序
	        // System.out.println("----sql:"+qSpec.toString());
	        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
	        if (qResult.hasMoreElements()) {
	            Persistable[] persistables = (Persistable[]) qResult.nextElement();
	            part = (WTPart) persistables[0];
	            WTPartStandardConfigSpec cs = WTPartStandardConfigSpec.newWTPartStandardConfigSpec();
	            wt.vc.views.View view = ViewHelper.service.getView("Manufacturing");
	            cs.setView(view);
	            QueryResult results = ConfigHelper.service.filteredIterationsOf(part.getMaster(), cs);
	            LatestConfigSpec lcs = new LatestConfigSpec();
	            results = lcs.process(results);
	            if (results.hasMoreElements()) {
	                part = (WTPart) results.nextElement();
	            }
	        }
	        return part;
	    }

	//根据图号/批次获取最新版本的part
	 public static WTPart getPartByTuHaoBatch(String cindex,String batchVersion) throws WTException, WTPropertyVetoException {
	        WTPart part = null;
	        QuerySpec qSpec = new QuerySpec();
	        int index0 = qSpec.addClassList(WTPart.class, true);
	        int index1 = qSpec.addClassList(StringValue.class, false);
	        int index2 = qSpec.addClassList(StringDefinition.class, false);

	        String[] aliases = new String[3];
	        aliases[0] = qSpec.getFromClause().getAliasAt(index0);
	        aliases[1] = qSpec.getFromClause().getAliasAt(index1);
	        aliases[2] = qSpec.getFromClause().getAliasAt(index2);

	        TableColumn tc0 = new TableColumn(aliases[0], "ida2a2");
	        TableColumn tc1 = new TableColumn(aliases[1], "IDA3A4");
	        TableColumn tc2 = new TableColumn(aliases[1], "IDA3A6");
	        TableColumn tc3 = new TableColumn(aliases[1], "value");
	        TableColumn tc4 = new TableColumn(aliases[2], "IDA2A2");
	        TableColumn tc5 = new TableColumn(aliases[2], "name");

	        qSpec.appendWhere(new SearchCondition(tc0, "=", tc1), new int[] { index0, index1 });
	        qSpec.appendAnd();
	        qSpec.appendWhere(new SearchCondition(tc2, "=", tc4), new int[] { index1, index2 });
	        qSpec.appendAnd();
	        qSpec.appendWhere(new SearchCondition(tc3, "=", new ConstantExpression(cindex)), new int[] { index1 });
	        qSpec.appendAnd();
	        qSpec.appendWhere(new SearchCondition(tc5, "=", new ConstantExpression("CINDEX")), new int[] { index2 });
	        // System.out.println("----sql:"+qSpec.toString());
	        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
	        if (qResult.hasMoreElements()) {
	            Persistable[] persistables = (Persistable[]) qResult.nextElement();
	            part = (WTPart) persistables[0];
	            WTPartStandardConfigSpec cs = WTPartStandardConfigSpec.newWTPartStandardConfigSpec();
	            wt.vc.views.View view = ViewHelper.service.getView("Manufacturing");
	            cs.setView(view);
	            QueryResult results = ConfigHelper.service.filteredIterationsOf(part.getMaster(), cs);
	            LatestConfigSpec lcs = new LatestConfigSpec();
	            results = lcs.process(results);
	            if (results.hasMoreElements()) {
	                part = (WTPart) results.nextElement();
	            }
	        }
	        return part;
	    }

	 //根据顶层图号编码获取所有子节点为自制件、外配套、带料委外件、不带料委外件的列表
	 public static void getAllChildByBatchView(WTPart part,String bomType,String batchVersion,List<WTPart> allChild){

		 QueryResult list = null;

	    	try {
	    		list = WTPartHelper.service.getUsesWTPartMasters(part);
	    		while(list.hasMoreElements()){
	    			WTPartUsageLink link = (WTPartUsageLink)list.nextElement();
	    			WTPartMaster part1 = (WTPartMaster)link.getRoleBObject();
	    			WTPart latePart =getLatestPartByBatchView(part1, batchVersion, bomType); //获得与父部件相同视图的子部件
	    			IBAUtility utility = new IBAUtility(latePart);
	    			String partType = utility.getIBAValue("MTYPE");//子件类型
	    			String lateversion = latePart.getIterationDisplayIdentifier().toString();//子件版本
	    			if(partType!=null&&!partType.equals("")){
	    				if(partType.equals(Constants.TYPE_ZIZHIJIAN)||partType.equals(Constants.TYPE_WAIPEITAOJIAN)||partType.equals(Constants.TYPE_DAILIAOWEIWAIJIAN)||partType.equals(Constants.TYPE_BUDAILIAOWEIWAIJIAN)){
	    					/*判断子列表中是否有重复节点*/
	    					if(!allChild.contains(latePart))
	    					   allChild.add(latePart);
	    				}

	    				getAllChildByBatchView(latePart,bomType,batchVersion,allChild);//迭代获取所有的物料子节点
	    			}

	    		}
	  		} catch (WTException e) {
	  			// TODO Auto-generated catch block
	  			e.printStackTrace();
	  		}

	 }



	 //根据批次、视图查询最新版本的part
	 public static WTPart getLatestPartByBatchView(Master master,String batchVersion, String viewName) throws WTException {
	        QueryResult queryResult = VersionControlHelper.service.allIterationsOf(master);
	        while (queryResult.hasMoreElements()) {
	            Object object = queryResult.nextElement();
	            if (object instanceof WTPart) {
	                WTPart part = (WTPart) object;
	                IBAUtility utility = new IBAUtility(part);
	                String batch = utility.getIBAValue("BATCH");//零部件批次
	                String partType = utility.getIBAValue("MTYPE");//零部件批次
					if(partType==null||"".equals(partType)){
						partType = utility.getIBAValue("CTYPE");
					}
	                if(batch==null)    batch = "";
	                if(isMateria(partType)){
	                	 if((part.getViewName().equals(viewName))) {
	  		               return part;
	  		            }

	                }
	                if(!ext.casc.util.Tools.isNull(batchVersion)){
	                	if((part.getViewName().equals(viewName))&& batch.equals(batchVersion)) {
	 		               return part;
	 		            }
	                }else{
	                	if((part.getViewName().equals(viewName))) {
		  		               return part;
		  		         }
	                }


	            }
	        }
	        return null;
	    }

	 public static boolean isMateria(String type){
	    	for(int i=0;i<Constants.TYPE_MATERIALS.length;i++){
	    		if(Constants.TYPE_MATERIALS[i].equals(type))
	    			return true;
	    	}
	    	return false;
	    }
	 //根据视图查询最新版本的part
	 public static WTPart getLatestPartByView(Master master,String viewName) throws WTException {
	        QueryResult queryResult = VersionControlHelper.service.allIterationsOf(master);
	        while (queryResult.hasMoreElements()) {
	            Object object = queryResult.nextElement();
	            if (object instanceof WTPart) {
	                WTPart part = (WTPart) object;
		            if((part.getViewName().equals(viewName))) {
		               return part;
		            }
	            }
	        }
	        return null;
	    }



	//查找编号相似列表
	public static String checkNum(String number){
		StringBuffer buffer = new StringBuffer();
		DBConn conn = null;
		try {
			conn = new DBConn();
            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("select part.wtpartnumber from WTPartMaster part where part.wtpartnumber like '%"+number+"%'");
            ResultSet resultset = conn.executeQuery(selectSQL.toString());
            while (resultset.next()) {
                String num = resultset.getString(1);
                buffer.append(num).append(";");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return buffer.toString();
	}

	public static void getProcessFileDirectoryByPart(WTPart part,String fileTypeB,String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
		List<Element> techList = getZTechnicsDocumentByPart(part,fileTypeB,fileTypeC);
		if(techList != null && !techList.isEmpty()) {
			for (Element techEle : techList) {
				if(techEle != null) {

				}
			}
		}
	}

	public static List<PbomErpPartBean> getPBOMMatchInfo(WTPart part,String fileTypeB,String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
		List<PbomErpPartBean> listMap = new  ArrayList<PbomErpPartBean>();
		List<Element> techList = getApprovedZTechnicsDocumentByPart(part,fileTypeB,fileTypeC);
		if(techList != null && !techList.isEmpty()) {
			for (Element techEle : techList) {
				if(techEle != null) {
		            String planType =  techEle.attributeValue("PPLANTYPE");
		            String zfflag =  techEle.attributeValue("ZFFLAG");
		            String dept =  techEle.attributeValue("DEPT");
					Element gydeEle = techEle.element("GYDE");
					if(gydeEle != null) {
						Element eles = gydeEle.element("MATCHPART");
						if(eles != null) {
							List<Element> list = eles.elements();
							if(list != null && !list.isEmpty()) {
								PbomErpPartBean bean = null;
								for (Element element : list) {
									String chbm = element.attributeValue("chbm");
									if(chbm == null || chbm.isEmpty()){
										continue;
									}
									bean = new PbomErpPartBean();
									bean.setPartNumber(element.attributeValue("number"));
									bean.setChbm(element.attributeValue("chbm"));
									//bean.setChmc(element.attributeValue("chmc"));
									bean.setChmc("");
									bean.setXlcc(element.attributeValue("xlcc"));
									String kzjs = element.attributeValue("kzjs");
									if (Tools.isNull(kzjs)) {
										kzjs = element.attributeValue("sjkzjs");
									}
									bean.setKzjs(kzjs);
									bean.setUseCount(element.attributeValue("useCount"));
									bean.setGyCount(element.attributeValue("gyCount"));
									bean.setXhph(element.attributeValue("xhph"));
									bean.setGg(element.attributeValue("gg"));
									bean.setJstj(element.attributeValue("jstj"));
									bean.setSccj(element.attributeValue("sccj"));
									String dw = element.attributeValue("dw2");
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
									bean.setPplanType(planType);
									bean.setZfflag(zfflag);
									bean.setDept(dept);
									bean.setVersion(techEle.attributeValue("version"));

									listMap.add(bean);
								}
							}
						}

						eles = gydeEle.element("SJZYKMATCHPART");
						if(eles != null) {
							List<Element> list = eles.elements();
							if(list != null && !list.isEmpty()) {
								PbomErpPartBean bean = null;
								for (Element element : list) {
									String chbm = element.attributeValue("sjbm");
									if(chbm == null || chbm.isEmpty()){
										continue;
									}
									bean = new PbomErpPartBean();
									bean.setPartNumber(element.attributeValue("partNumber"));
									bean.setChbm(element.attributeValue("sjbm"));
									bean.setChmc(element.attributeValue("name"));
									bean.setXlcc(element.attributeValue("xlcc"));
									String kzjs = element.attributeValue("kzjs");
									if (Tools.isNull(kzjs)) {
										kzjs = element.attributeValue("sjkzjs");
									}
									bean.setKzjs(kzjs);
									String sl = element.attributeValue("gysl");
									if(Tools.isNull(sl)){
										sl = element.attributeValue("sl");
									}
									bean.setGyCount(sl);
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

									listMap.add(bean);
								}
							}
						}
					}
				}
			}
		}

		return listMap;
	}

	public static List<PbomErpPartBean> getPBOMNewPartInfo(WTPart part,String fileTypeB,String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
		List<PbomErpPartBean> partList = new ArrayList<PbomErpPartBean>();
		List<Element> techList = getApprovedZTechnicsDocumentByPart(part,fileTypeB,fileTypeC);
		if(techList != null && !techList.isEmpty()) {
			for (Element techEle : techList) {
				if(techEle != null) {
		            String planType =  techEle.attributeValue("PPLANTYPE");
		            String zfflag =  techEle.attributeValue("ZFFLAG");
		            String dept =  techEle.attributeValue("DEPT");
					Element gydeEle = techEle.element("GYDE");
					if(gydeEle != null) {
						Element eles = gydeEle.element("NEWPART");
						if(eles != null) {
							List<Element> list = eles.elements();
							if(list != null && !list.isEmpty()) {
								PbomErpPartBean bean = null;
								for (Element element : list) {
									bean = new PbomErpPartBean();
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
									partList.add(bean);
								}
							}
						}

						eles = gydeEle.element("SJZYKNEWPART");
						if(eles != null) {
							List<Element> list = eles.elements();
							if(list != null && !list.isEmpty()) {
								PbomErpPartBean bean = null;
								for (Element element : list) {
									bean = new PbomErpPartBean();
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

	/**
	 * 获取该PART的主工艺文档对象
	 *
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws DocumentException
	 */
	public static List<Element> getZTechnicsDocumentByPart(WTPart part,String fileTypeB,String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
		GLLogger.debug("--------getDescribedDocumentByPart----part:"+part.getNumber()+"  fileTypeB:"+fileTypeB+"  fileTypeC:"+fileTypeC);
		List<Element> list = new ArrayList<Element>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();

			//排除报表类工艺
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")
					&&!TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("reportTechnics")) {
				document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
				Element ele = getZTechincisElement(document,fileTypeB,fileTypeC);
				if(ele != null) {
					list.add(ele);
				}
			}
		}
		return list;
	}

	public static List<Element> getApprovedZTechnicsDocumentByPart(WTPart part,String fileTypeB,String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
		List<Element> list = new ArrayList<Element>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			QueryResult qr2 = VersionControlHelper.service.allVersionsOf(document.getMaster());
			while(qr2.hasMoreElements()){
				WTDocument doc = (WTDocument)qr2.nextElement();
				String state =doc.getState().getState().toString();
				if("OBSOLESCENCE".equals(state)){
					break;
				}
				ext.casc.util.IBAHelper helper = new ext.casc.util.IBAHelper();
				String CLDEZT = helper.getIBAStringValue(doc, "CLDEZT");
				if("APPROVED".equals(state)||"已批准".equals(CLDEZT)){
					//排除报表类工艺
					if (TypedUtilityServiceHelper.service.getTypeIdentifier(doc).toString().contains("PROCESS_PLAN")
							&&!TypedUtilityServiceHelper.service.getTypeIdentifier(doc).toString().contains("reportTechnics")) {
						Element ele = getZTechincisElement(doc,fileTypeB,fileTypeC);
						if(ele != null) {
							list.add(ele);
						}
					}
					break;
				}
			}
		}
		return list;
	}


	/**
	 * 获取该PART的工艺文档对象
	 *
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws DocumentException
	 */
	public static List<Element> getTechnicsDocumentByPart(WTPart part,String fileTypeB,String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
		GLLogger.debug("--------getDescribedDocumentByPart----part:"+part.getNumber()+"  fileTypeB:"+fileTypeB+"  fileTypeC:"+fileTypeC);
		List<Element> list = new ArrayList<Element>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")) {
				document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
				Element ele = getTechincisElement(document,fileTypeB,fileTypeC);
				if(ele != null) {
					list.add(ele);
				}
			}
		}
		return list;
	}

	/**
	 * 获取该PART的工艺文档对象
	 *
	 * @param part
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws DocumentException
	 */
	public static List<Element> getTechnicsDocumentByPart(WTPart part,String fileTypeB,String fileTypeC, String userName) throws WTException, PropertyVetoException, DocumentException {
		GLLogger.debug("--------getDescribedDocumentByPart----part:"+part.getNumber()+"  fileTypeB:"+fileTypeB+"  fileTypeC:"+fileTypeC);
		List<Element> list = new ArrayList<Element>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			if(!userName.equals(document.getModifierName())){
				continue;
			}
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")) {
				document = DocUtil.getDoc(document.getNumber(), false);
				//document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
				String state = document.getState().getState().getDisplay(Locale.CHINA);
				Element ele = getTechincisElement(document,fileTypeB,fileTypeC);
				XmlUtility.setAttributeValue(ele, "lifecycle" ,state);
				if(ele != null) {
					list.add(ele);
				}
			}
		}
		return list;
	}

	public static List<Element> getTechnicsDocumentWithOutReportByPart(WTPart part,String fileTypeB,String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
		GLLogger.debug("--------getDescribedDocumentByPart----part:"+part.getNumber()+"  fileTypeB:"+fileTypeB+"  fileTypeC:"+fileTypeC);
		List<Element> list = new ArrayList<Element>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")
					&&!TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("reportTechnics")) {
				document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
				Element ele = getTechincisElement(document,fileTypeB,fileTypeC);
				if(ele != null) {
					list.add(ele);
				}
			}
		}
		return list;
	}

	public static List<WTDocument> getAllApprovedTempTechnics(WTPart part, String technicsType) throws WTException{
		List<WTDocument> list = new ArrayList<WTDocument>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			System.out.println(document.getNumber());
			//是工艺文件，不是报表类工艺
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")
					&&!TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("reportTechnics")) {
				document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
				Versioned vBase = null;
				QueryResult qr2  = VersionControlHelper.service.allVersionsOf(document.getMaster());
				//所有版本
				while (qr2.hasMoreElements()) {
					vBase = (Versioned) qr2.nextElement();
					WTDocument doc = (WTDocument)vBase;
					String version = doc.getVersionInfo().getIdentifier().getValue();
					String state = doc.getState().toString();
					System.out.println(state);
					//状态为已批准
					if("APPROVED".equals(state) && !"space".equals(version)){
						list.add(doc);
					}
				}
			}
			}
		return list;
	}

	public static List<WTDocument> getLatestAllApprovedTempTechnics(WTPart part, String technicsType) throws WTException{
		List<WTDocument> list = new ArrayList<WTDocument>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			System.out.println(document.getNumber());
			//是工艺文件，不是报表类工艺
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")
					&&!TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("reportTechnics")) {
				document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
				String version = document.getVersionInfo().getIdentifier().getValue();
				String state = document.getState().toString();
				System.out.println(state);
				//状态为已批准
				if("APPROVED".equals(state) && !"space".equals(version)){
					list.add(document);
				}
			}
			}
		return list;
	}

	public static List<WTDocument> getTechnicsCatalogs(WTPart part, String technicsType) throws WTException{
		List<WTDocument> list = new ArrayList<WTDocument>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			System.out.println(document.getNumber());
			//是工艺文件且是报表类工艺
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")
					&&TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("reportTechnics")
					&&document.getName().contains("工艺文件目录")) {
				document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
				list.add(document);
				}
			}
		return list;
	}

	public static List<WTChangeOrder2> getAllChangeOrder2(WTPart part) throws WTException {
		List<WTChangeOrder2> changeOrderList = new ArrayList<WTChangeOrder2>();
		List<WTDocument> docList = getAllApprovedTempTechnics(part, "");
		for (WTDocument document : docList) {
			QueryResult qr2 = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices(document);
			if (qr2.hasMoreElements()) {
				WTChangeOrder2 ecn = (WTChangeOrder2) qr2.nextElement();
				changeOrderList.add(ecn);
			}
			WTCollection coll2 = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(document);
			Iterator it2 = coll2.iterator();
			if (it2.hasNext()) {
				WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it2.next()).getObject();
				changeOrderList.add(ecn);
			}
		}
		return changeOrderList;
	}

	public static List<WTChangeOrder2> getAllChangeOrder2ByLastestDoc(WTPart part) throws WTException {
		List<WTChangeOrder2> changeOrderList = new ArrayList<WTChangeOrder2>();
		List<WTDocument> docList = getLatestAllApprovedTempTechnics(part, "");
		for (WTDocument document : docList) {
			QueryResult qr2 = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices(document);
			if (qr2.hasMoreElements()) {
				WTChangeOrder2 ecn = (WTChangeOrder2) qr2.nextElement();
				changeOrderList.add(ecn);
			}
			WTCollection coll2 = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(document);
			Iterator it2 = coll2.iterator();
			if (it2.hasNext()) {
				WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it2.next()).getObject();
				changeOrderList.add(ecn);
			}
		}
		return changeOrderList;
	}

	/*public static List<WTDocument> getWTDocumentByPart(WTPart part,String fileTypeB,String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
		GLLogger.debug("--------getDescribedDocumentByPart----part:"+part.getNumber()+"  fileTypeB:"+fileTypeB+"  fileTypeC:"+fileTypeC);
		List<WTDocument> list = new ArrayList<WTDocument>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")) {
				document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
				list.add(document);
			}
		}
		return list;
	}*/


	public static List<WTDocument> getAllWTDocumentByAllSameVersionViewPart(WTPart part) throws WTException, PropertyVetoException, DocumentException {
		WTPart newpart = (WTPart)ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class,part.getNumber(),part.getVersionInfo().getIdentifier().getValue(),"Manufacturing");

		List<WTDocument> docList  = new ArrayList<WTDocument>();
		QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr2 = lcs.process(qr2);
		while (qr2.hasMoreElements()) {
			WTDocument document = (WTDocument) qr2.nextElement();
			String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
			if (typeName.contains("PROCESS_PLAN")&&!typeName.contains("reportTechnics")) {
				try {
					document = (WTDocument)WCUtil.getIteratedByMaster(document.getMaster());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//只获取关联的最新版本
				if(docList.isEmpty() || !WTPartUtil.checkNumber(docList,document.getNumber())) {
					docList.add(document);
				}
			}
		}
		/*QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")) {
				document = (WTDocument)VersionControlHelper.service.getLatestIteration(document, true);
				list.add(document);
			}
		}*/

		return docList;
	}

	public static List<WTDocument> getAllWTDocumentAndReportProcessByAllSameVersionViewPart(WTPart part) throws WTException, PropertyVetoException, DocumentException {
		WTPart newpart = (WTPart)ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class,part.getNumber(),part.getVersionInfo().getIdentifier().getValue(),"Manufacturing");
		List<WTDocument> docList  = new ArrayList<WTDocument>();
		QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(newpart, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr2 = lcs.process(qr2);
		while (qr2.hasMoreElements()) {
			WTDocument document = (WTDocument) qr2.nextElement();
			String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
			if (typeName.contains("PROCESS_PLAN")) {
				try {
					document = (WTDocument)WCUtil.getIteratedByMaster(document.getMaster());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//只获取关联的最新版本
				if(docList.isEmpty() || !WTPartUtil.checkNumber(docList,document.getNumber())) {
					docList.add(document);
				}
			}
		}

		return docList;
	}
	/**
	 * 判断是否主工艺
	 * 返回null：标识工艺文件不为主工艺，或者为主工艺但
	 *
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws DocumentException
	 */
	public static Element getZTechincisElement(WTDocument doc,String fileTypeB,String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
    	//名称映射
		if(Constants.LABEL_PROCESS_TYPEB_FORMAL.equals(fileTypeB))
    		fileTypeB = Constants.PROCESS_TYPEB_FORMAL;
    	if(Constants.LABEL_PROCESS_TYPEB_TEMP.equals(fileTypeB))
    		fileTypeB = Constants.PROCESS_TYPEB_TEMP;
    	if(Constants.LABEL_PROCESS_TYPEC_PRIMARY.equals(fileTypeC))
    		fileTypeC = Constants.PROCESS_TYPEC_PRIMARY;
    	if(Constants.LABEL_PROCESS_TYPEC_ASSIST.equals(fileTypeC))
    		fileTypeC = Constants.PROCESS_TYPEC_ASSIST;

		ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
		if(data == null) {
			return null;
		}

		String zipFilePath = zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()+File.separator+doc.getNumber();
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
			//主、辅工艺
			String zfFlag = element.attributeValue("ZFFLAG");
			//正式工艺、临时工艺
			String pplantype = element.attributeValue("PPLANTYPE");

			//如果是正式工艺，则只输出正式工艺文件的主工艺，如果是临时工艺，则默认输出所有的
//			if("Z".equals(zfFlag)) {
//				return element;
//			}
			if(pplantype==null ||zfFlag==null){//这时候是报表类工艺文件
				continue;
            }

			//筛选主工艺（Z）
			if(!"ALL".equals(fileTypeC)){
	            if(!zfFlag.equals(fileTypeC)) {
	        		break;
	        	}
			}

            //正式工艺文件值读取主制工艺，临时工艺文件读取所有的主工艺
            if(fileTypeB != null && !pplantype.equals(fileTypeB) && fileTypeB.equals("正式工艺文件")) {
            	break;
            }
            //删除临时文件
    		CldeUtil.deleteFiles(new File(zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()));
            //返回符合条件的工艺文件的标识
            return element;
        }

		return null;
	}

	/**
	 *	获取工艺文件Element
	 *
	 * @param doc
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws DocumentException
	 */
	public static Element getTechincisElement(WTDocument doc,String fileTypeB,String fileTypeC) throws WTException, PropertyVetoException, DocumentException {
    	//名称映射
		if(Constants.LABEL_PROCESS_TYPEB_FORMAL.equals(fileTypeB))
    		fileTypeB = Constants.PROCESS_TYPEB_FORMAL;
    	if(Constants.LABEL_PROCESS_TYPEB_TEMP.equals(fileTypeB))
    		fileTypeB = Constants.PROCESS_TYPEB_TEMP;
    	if(Constants.LABEL_PROCESS_TYPEC_PRIMARY.equals(fileTypeC))
    		fileTypeC = Constants.PROCESS_TYPEC_PRIMARY;
    	if(Constants.LABEL_PROCESS_TYPEC_ASSIST.equals(fileTypeC))
    		fileTypeC = Constants.PROCESS_TYPEC_ASSIST;

		ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
		if(data == null) {
			return null;
		}

		String zipFilePath = zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()+File.separator+doc.getNumber();
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
        Element element = null;
        for (Object object : pplanList) {
			element = (Element)object;
			//主、辅工艺
			String zfFlag = element.attributeValue("ZFFLAG");
			//正式工艺、临时工艺
			String pplantype = element.attributeValue("PPLANTYPE");
        }

		//删除临时文件
		CldeUtil.deleteFiles(new File(zip_temp_dir+File.separator+"erpTemp"+File.separator+doc.getNumber()));

		return element;
	}


	public static String getCreoViewUrlRMI(Object obj) {
		String creoViewUrl = "";
		WTContainer container = null;
		Persistable persistable = null;
		if(obj instanceof EPMDocument){
			EPMDocument epmDocument = (EPMDocument)obj;
			if (epmDocument == null) {
				return creoViewUrl;
			} else {
				persistable = epmDocument;
				container = epmDocument.getContainer();
			}
		}else if(obj instanceof WTDocument){
			WTDocument document = (WTDocument)obj;
			if (document == null) {
				return creoViewUrl;
			} else {
				persistable = document;
				container = document.getContainer();
			}
		}


		QueryResult repResult = PublishUtils.getRepresentations(persistable);
		if (repResult == null || repResult.size() == 0) {
			return creoViewUrl;
		}
		Persistable paramPersistable = (Persistable) repResult.nextElement();

		String viewUrl = "";
		String repOid = "";
		if ((paramPersistable instanceof Representation)) {
			repOid = com.ptc.wvs.server.util.Util
					.SandR(PublishUtils.getRefFromObject(paramPersistable), ":", "%3A");
			viewUrl = PublishUtils.getPreferedViewURL((Representation) paramPersistable, true);
		}
		if ("".equals(creoViewUrl) && !"".equals(viewUrl)) {
			String url = new StringBuilder().append(viewUrl).append("&objref=").append(repOid).toString();
			creoViewUrl = new StringBuilder().append(
					PropertiesUtil.getHttpCodeBase() + "/wtcore/jsp/wvs/edrview.jsp").append("?url=").append(url)
					.append("&ContainerOid=OR%3A").append(
							com.ptc.wvs.server.util.Util.SandR(container.toString(), ":", "%3A")).toString();
		}

		return creoViewUrl;
	}
	public static String[] getChangeOrderPdf(WTChangeOrder2 changeOrder){
		String[] fileNames = new String[2];
		String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp";
		File fileDir = new File(filePath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		FileOutputStream fos = null;
		try {
			if (changeOrder != null) {
				ContentHolder holder = ContentHelper.service.getContents(changeOrder);
				Vector apps = ContentHelper.getApplicationData(holder);
				for (Enumeration e = apps.elements(); e.hasMoreElements();) {
					ApplicationData contentItem = (ApplicationData) e.nextElement();
					String applicationdataRole = contentItem.getRole().toString();
					if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
						continue;// 不是附件

					if (contentItem.getFileName().startsWith("Print_" + changeOrder.getNumber()) ) {
						byte[] bytes = WTDocumentUtil.applicationDataToByte(contentItem);
						fileNames[0] = contentItem.getFileName();
						fileNames[1] = changeOrder.getNumber() + ".pdf";
						String path = fileDir + File.separator + fileNames[1];
						File file = new File(path);
						if(file != null && file.exists()){
							FileUtil.deleteFile(file);
						}
						fos = new FileOutputStream(path);
						fos.write(bytes);
						fos.flush();
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fileNames;
	}
	public static String[] getWTDocumentPdfUrl(WTDocument document){
		String fileNames[] = new String[2];
		String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp";
		File fileDir = new File(filePath);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		FileOutputStream fos = null;
		try {
			if (document != null) {
				ContentHolder holder = ContentHelper.service.getContents(document);
				Vector apps = ContentHelper.getApplicationData(holder);
				for (Enumeration e = apps.elements(); e.hasMoreElements();) {
					ApplicationData contentItem = (ApplicationData) e.nextElement();
					String applicationdataRole = contentItem.getRole().toString();
					if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
						continue;// 不是附件

					if (contentItem.getFileName().startsWith("Print_" + document.getNumber()) ) {
						byte[] bytes = WTDocumentUtil.applicationDataToByte(contentItem);
						fileNames[0] = contentItem.getFileName();
						fileNames[1] = document.getNumber() + ".pdf";
						File file = new File(fileDir + File.separator + fileNames[1]);
						if(file != null && file.exists()){
							FileUtil.deleteFile(file);
						}
						fos = new FileOutputStream(fileDir + File.separator + fileNames[1]);
						fos.write(bytes);
						fos.flush();
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(fos != null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return fileNames;
	}
	public static EPMDocument getEPMDocumentByName(String name) throws WTException {
		EPMDocument epmDocument = null;

		QuerySpec qs = new QuerySpec(EPMDocument.class);
		qs
				.appendWhere(new SearchCondition(EPMDocument.class, EPMDocument.NAME, SearchCondition.EQUAL, name),
						index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		qr = new LatestConfigSpec().process(qr);
		if (qr.hasMoreElements()) {
			epmDocument = (EPMDocument) qr.nextElement();
		}
		return epmDocument;
	}
	public static WTPart getPartByEPMDocument(EPMDocument epm){
		WTPart part = null;
		try {
			long oid = VersionControlHelper.getBranchIdentifier(epm);
			QuerySpec qs = new QuerySpec(EPMBuildRule.class);
			qs.appendWhere(new SearchCondition(EPMBuildRule.class, "roleAObjectRef.key.branchId", "=", oid), new int[]{0});
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
			while(qr.hasMoreElements()){
				EPMBuildRule rule = (EPMBuildRule) qr.nextElement();
				Persistable per = rule.getRoleBObject();
				if(per instanceof WTPart){
					part = (WTPart) per;
				}

			}

		} catch (VersionControlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return part;
	}
	public static EPMDocument getEPMDocumentByPart(WTPart part){
		EPMDocument epm = null;
		try {
			long oid = VersionControlHelper.getBranchIdentifier(part);
			QuerySpec qs = new QuerySpec(EPMBuildRule.class);
			qs.appendWhere(new SearchCondition(EPMBuildRule.class, "roleBObjectRef.key.branchId", "=", oid), new int[]{0});
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
			while(qr.hasMoreElements()){
				EPMBuildRule rule = (EPMBuildRule) qr.nextElement();
				Persistable per = rule.getRoleAObject();
				if(per instanceof EPMDocument){
					epm = (EPMDocument) per;
				}

			}

		} catch (VersionControlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return epm;
	}

	public static List<Element> getZhuZhiTechnicsDocumentByPart(WTPart part, String userName) throws WTException, PropertyVetoException, DocumentException {
		List<Element> list = new ArrayList<Element>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			if(!userName.equals(document.getModifierName())){
				continue;
			}
			if(!"Z".equals(IBAHelper.getIBAStringValue(document,"ZFFLAG"))){
				continue;
			}
			if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")) {
				document = DocUtil.getDoc(document.getNumber(), false);
				String state = document.getState().getState().getDisplay(Locale.CHINA);
				Element ele = getTechincisElement(document, "", "");
				XmlUtility.setAttributeValue(ele, "lifecycle" ,state);
				if(ele != null) {
					list.add(ele);
				}
			}
		}
		return list;
	}

	public static List<Element> getInWorkTechnicsDocumentByPart(WTPart part, String userName, String signedType) throws WTException, PropertyVetoException, DocumentException, RemoteException {
		List<Element> list = new ArrayList<Element>();
		QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument document = (WTDocument) qr.nextElement();
			if(!userName.equals(document.getModifierName())){
				continue;
			}
			if(!"正在工作".equals(document.getState().getState().getDisplay(Locale.CHINA))){
				continue;
			}
			String docType = TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString();
			if("3".equals(signedType)){
				String pplantype = IBAHelper.getIBAStringValue(document, "PPLANTYPE");
				if("正式工艺文件".equals( pplantype)){
					String phaseCode = IBAHelper.getIBAStringValue(document, "PHASE_CODE");
					if(docType.indexOf("casc.sast.149.SHUKONG_PROCESSPLAN") < 0
							&& !"M".equals(phaseCode)) {
						continue;
					}
				}
			}
			if(docType.contains("PROCESS_PLAN")) {
				boolean submited = ProcessEditorToWCIntfRMI.isSubmited(document.getNumber());
				if(submited){
					continue;
				}
				document = DocUtil.getDoc(document.getNumber(), false);
				String state = document.getState().getState().getDisplay(Locale.CHINA);
				Element ele = getTechincisElement(document, "", "");
				XmlUtility.setAttributeValue(ele, "lifecycle", state);
				if(ele != null) {
					list.add(ele);
				}
			}
		}
		return list;
	}
}
