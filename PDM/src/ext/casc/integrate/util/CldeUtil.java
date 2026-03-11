package ext.casc.integrate.util;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import ext.casc.integrate.model.MaterialsBean;
import ext.casc.integrate.model.PbomErpPartBean;
import ext.casc.util.Tools;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.util.WTProperties;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CldeUtil {

	private static String wt_temp;
	private static String zip_temp_dir;

	static {
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			wt_temp = pro.getProperty("wt.temp");
			zip_temp_dir = wt_temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取指定PBOM零部件和工艺文件类别的材料信息
	 *
	 * @param part PBOM零部件
	 * @param fileTypeB 工艺文件类别：正式工艺文件、临时工艺文件
	 * @param fileTypeC 工艺文件类型：Z(主制工艺)、F(辅制工艺)
	 * @return List<MaterialsBean> 所有材料信息集合
	 * @throws DocumentException
	 */
	public static List<MaterialsBean> getCldeInfo(WTPart part,String fileTypeB,String fileTypeC) throws DocumentException {
		System.out.println("getCldeInfo-------part:"+part+"   fileTypeB:"+fileTypeB+"  fileTypeC:"+fileTypeC);
		List<MaterialsBean> materialsList = new ArrayList<MaterialsBean>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("oid", String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId()));
		map.put("partNumber", part.getNumber());
		map.put("category", "normal");
		List<Vector<Object>> list = ProcessEditorToWCIntfRMI.getApprovedTechnicsByPartRMI(map);
		List<String> xmlFileList = new ArrayList<String>();
		System.out.println("----list----"+list.size());
		if (list.size() > 0) {
			for(int i=0;i<list.size();i++) {
				Vector result = list.get(i);
				System.out.println("----result----"+result);
				if ((result != null) && (result.size() == 6)) {
					String fileName = (String) result.get(0);
					byte[] data = (byte[]) result.get(1);
					if ((data == null) || (data.length <= 0)) {
						return materialsList;
					}
					if (fileName.toLowerCase().endsWith(".zip")) {
						fileName = fileName.substring(0, fileName.length() - 4);
					}

					String zipFilePath = zip_temp_dir+File.separator+part.getNumber()+File.separator+result.get(4);
					File file = new File(zipFilePath);
					if(!file.exists()) {
						file.mkdirs();
					}

					String xmlFile = zipFilePath+File.separator+result.get(4)+".xml";
					xmlFileList.add(xmlFile);

					ZipUtil.unZip(data, zipFilePath);
				}
			}
		}

		materialsList = readXML(xmlFileList,fileTypeB,fileTypeC);

		//删除临时文件
		deleteFiles(new File(zip_temp_dir+File.separator+part.getNumber()));

		return materialsList;
	}

	public static List<MaterialsBean> getEpmtyCldeInfo(WTPart part,String fileTypeB,String fileTypeC) throws DocumentException {
		System.out.println("getCldeInfo-------part:"+part+"   fileTypeB:"+fileTypeB+"  fileTypeC:"+fileTypeC);
		List<MaterialsBean> materialsList = new ArrayList<MaterialsBean>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("oid", String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId()));
		map.put("partNumber", part.getNumber());
		map.put("category", "normal");
		List<Vector<Object>> list = ProcessEditorToWCIntfRMI.getApprovedTechnicsByPartRMI(map);
		List<String> xmlFileList = new ArrayList<String>();
		System.out.println("----list----"+list.size());
		if (list.size() > 0) {
			for(int i=0;i<list.size();i++) {
				Vector result = list.get(i);
				System.out.println("----result----"+result);
				if ((result != null) && (result.size() == 6)) {
					String fileName = (String) result.get(0);
					byte[] data = (byte[]) result.get(1);
					if ((data == null) || (data.length <= 0)) {
						return materialsList;
					}
					if (fileName.toLowerCase().endsWith(".zip")) {
						fileName = fileName.substring(0, fileName.length() - 4);
					}

					String zipFilePath = zip_temp_dir+File.separator+part.getNumber()+File.separator+result.get(4);
					File file = new File(zipFilePath);
					if(!file.exists()) {
						file.mkdirs();
					}

					String xmlFile = zipFilePath+File.separator+result.get(4)+".xml";
					xmlFileList.add(xmlFile);

					ZipUtil.unZip(data, zipFilePath);
				}
			}
		}

		materialsList = readEpmtyXML(xmlFileList,fileTypeB,fileTypeC);

		//删除临时文件
		deleteFiles(new File(zip_temp_dir+File.separator+part.getNumber()));

		return materialsList;
	}


	public static List<MaterialsBean> readXML(List<String> xmlFileList,String fileTypeB,String fileTypeC) throws DocumentException {
		List<MaterialsBean> list = new ArrayList<MaterialsBean>();
		for (String xmlFile : xmlFileList) {
			File file = new File(xmlFile);
			if(!file.exists()) {
				System.out.println(xmlFile+" is not exist!");
				continue;
			}
			SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            Iterator iterator = rootElement.elementIterator();
            while(iterator.hasNext()) {
            	boolean isHasCLDE = false;
                Element technicsElement = (Element)iterator.next();

                String pplanNumber = technicsElement.attributeValue("technicsNumber");
                String processFileNumber = technicsElement.attributeValue("pplanNumber");
                String pplanType = technicsElement.attributeValue("PPLANTYPE");
                String zfflag = technicsElement.attributeValue("ZFFLAG");
                String dept = technicsElement.attributeValue("DEPT");
                String batch = technicsElement.attributeValue("PCNO");
                System.out.println("--------pplanNumber----"+pplanNumber);
                System.out.println("--------pplanType----"+pplanType);
                System.out.println("--------zfflag----"+zfflag);
                System.out.println("--------fileTypeC----"+fileTypeC);
                System.out.println("--------fileTypeB----"+fileTypeB);
                if(pplanType==null ||zfflag==null){//这时候是报表类工艺文件
                	continue;
                }
                //筛选主工艺（Z）
                if(!"ALL".equals(fileTypeC)){
                	if(!zfflag.equals(fileTypeC)) {
                		break;
                	}
                }


                //正式工艺文件值读取主制工艺，临时工艺文件读取所有的主工艺
               // !"临时工艺文件".equals("临时工艺文件") && "临时工艺文件".equals("正式工艺文件")
				//fileTypeB=正式工艺文件  fileTypeC=Z
                if(!pplanType.equals(fileTypeB)&&fileTypeB.equals("正式工艺文件")) {
                	break;
                }
                String pplanName = "";
                String pum = technicsElement.attributeValue("pplanNumber");
				String pname = technicsElement.attributeValue("pplanName");
				if(pum!=null &&!"".equals(pum)&&pname!=null &&!"".equals(pname)){
					pplanName= pname +"("+pum+")";
				}else{
					pplanName = technicsElement.attributeValue("technicsName");
				}

                String pplanVersion = technicsElement.attributeValue("version");
                String pplanState = technicsElement.attributeValue("lifecycle");
                Iterator ite2 = technicsElement.elementIterator();
                while(ite2.hasNext()) {
                	Element cldeElement = (Element)ite2.next();
                	if("GYDE".equals(cldeElement.getName())){
						List<Element> zycldeElements = cldeElement.selectNodes("ZYCLDE/zycldeRecord");
						for(Element zyclde : zycldeElements){
							MaterialsBean bean = new MaterialsBean();
							bean.setMaterialType("外购件");
							bean.setPplanNumber(pplanNumber);
							bean.setPplanName(pplanName);
							bean.setPplanVersion(pplanVersion);
							bean.setPplanState(pplanState);
							bean.setMaterialState("");
							bean.setBatch(batch);
							bean.setProcessFileNumber(processFileNumber);
							bean.setPplanType(pplanType);
							bean.setZfflag(zfflag);
							bean.setDept(dept);

							bean.setUsingType("主要材料定额");
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
							bean.setComment(zyclde.attributeValue("comment"));
							list.add(bean);
							isHasCLDE = true;
						}
						List<Element> sjycldeElements = cldeElement.selectNodes("SJYCLDE/sjycldeRecord");
						for(Element slyclde : sjycldeElements){
							MaterialsBean bean = new MaterialsBean();
							bean.setMaterialType("外购件");
							bean.setPplanNumber(pplanNumber);
							bean.setPplanName(pplanName);
							bean.setPplanVersion(pplanVersion);
							bean.setPplanState(pplanState);
							bean.setMaterialState("");
							bean.setBatch(batch);
							bean.setProcessFileNumber(processFileNumber);
							bean.setPplanType(pplanType);
							bean.setZfflag(zfflag);
							bean.setDept(dept);

							bean.setUsingType("试件原材料定额");
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
							bean.setComment(slyclde.attributeValue("comment"));
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
                			MaterialsBean bean = new MaterialsBean();
                			bean.setMaterialType("外购件");
                			bean.setPplanNumber(pplanNumber);
                			bean.setPplanName(pplanName);
                			bean.setPplanVersion(pplanVersion);
                			bean.setPplanState(pplanState);
                			bean.setMaterialState("");
                			bean.setBatch(batch);
                			bean.setProcessFileNumber(processFileNumber);
                			bean.setPplanType(pplanType);
                			bean.setZfflag(zfflag);
							bean.setDept(dept);

                			if("YCLDE".equals(element.getName())) {
                				bean.setUsingType("原材料定额");
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
                				bean.setComment(ele.attributeValue("comment"));

                				list.add(bean);
                				isHasCLDE = true;
                    		}
                			else if ("SJYCLDE".equals(element.getName())) {
                    			bean.setUsingType("试件原材料定额");
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
                				bean.setComment(ele.attributeValue("comment"));
                    			list.add(bean);
                    			isHasCLDE = true;
                    		}
                			else if ("ZYCLDE".equals(element.getName())) {
                    			bean.setUsingType("主要材料定额");
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
                				bean.setComment(ele.attributeValue("comment"));
                    			list.add(bean);
                    			isHasCLDE = true;
                    		}
                			else if("SJZYKYCLDE".equals(element.getName())) {
                				bean.setUsingType("原材料定额");
                				bean.setChbm(ele.attributeValue("sjbm"));
                    			bean.setChmc(ele.attributeValue("name"));
                    			bean.setZjldw(ele.attributeValue("jldw"));
                    			bean.setDataFrom(ele.attributeValue("dataFrom"));

                				bean.setXlcc(ele.attributeValue("xlcc"));
                                bean.setSjkzjs(ele.attributeValue("sjkzjs"));
                                bean.setKzjs(ele.attributeValue("kzjs"));
								bean.setSl("");
								bean.setDw(ele.attributeValue("dw"));
                				bean.setComment(ele.attributeValue("comment"));
                				list.add(bean);
                				isHasCLDE = true;
                    		}
                			else if ("SJZYKSJYCLDE".equals(element.getName())) {
                    			bean.setUsingType("试件原材料定额");
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
                				bean.setComment(ele.attributeValue("comment"));
                    			list.add(bean);
                    			isHasCLDE = true;
                    		}
                			else if ("SJZYKZYCLDE".equals(element.getName())) {
                    			bean.setUsingType("主要材料定额");
                    			bean.setChbm(ele.attributeValue("sjbm"));
                    			bean.setChmc(ele.attributeValue("name"));
                    			bean.setZjldw(ele.attributeValue("jldw"));
                    			bean.setDataFrom(ele.attributeValue("dataFrom"));
                                bean.setXlcc(ele.attributeValue("xlcc"));
                                bean.setSjkzjs(ele.attributeValue("sjkzjs"));
                                bean.setKzjs(ele.attributeValue("kzjs"));
                    			bean.setSl(ele.attributeValue("sl"));
                    			bean.setDw(ele.attributeValue("dw"));
                				bean.setComment(ele.attributeValue("comment"));
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
                    	MaterialsBean bean = new MaterialsBean();
            			bean.setMaterialType("N");
            			bean.setPplanNumber(pplanNumber);
            			bean.setPplanName(pplanName);
            			bean.setPplanVersion(pplanVersion);
            			bean.setPplanState(pplanState);
            			bean.setChbm("N");
            			bean.setChmc("N");
						bean.setMaterialState("");
            			bean.setBatch(batch);
            			bean.setProcessFileNumber(processFileNumber);
            			bean.setPplanType(pplanType);
            			bean.setZfflag(zfflag);
						bean.setDept(dept);
						list.add(bean);
                    }
                }

            }
		}
		System.out.println("----------list------------"+list);
		return list;
	}

	public static List<MaterialsBean> readEpmtyXML(List<String> xmlFileList,String fileTypeB,String fileTypeC) throws DocumentException {
		List<MaterialsBean> list = new ArrayList<MaterialsBean>();
		for (String xmlFile : xmlFileList) {
			File file = new File(xmlFile);
			if(!file.exists()) {
				System.out.println(xmlFile+" is not exist!");
				continue;
			}
			SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            Iterator iterator = rootElement.elementIterator();
            while(iterator.hasNext()) {
                Element technicsElement = (Element)iterator.next();

                String pplanNumber = technicsElement.attributeValue("technicsNumber");
                String pplanType = technicsElement.attributeValue("PPLANTYPE");
                String zfflag = technicsElement.attributeValue("ZFFLAG");
                String batch = technicsElement.attributeValue("PCNO");
                System.out.println("--------pplanNumber----"+pplanNumber);
                System.out.println("--------pplanType----"+pplanType);
                System.out.println("--------zfflag----"+zfflag);
                System.out.println("--------fileTypeC----"+fileTypeC);
                System.out.println("--------fileTypeB----"+fileTypeB);
                if(pplanType==null ||zfflag==null){//这时候是报表类工艺文件
                	continue;
                }
                //筛选主工艺（Z）
                if(!zfflag.equals(fileTypeC)) {
            		break;
            	}

                //正式工艺文件值读取主制工艺，临时工艺文件读取所有的主工艺
               // !"临时工艺文件".equals("临时工艺文件") && "临时工艺文件".equals("正式工艺文件")
                if(!pplanType.equals(fileTypeB)&&fileTypeB.equals("正式工艺文件")) {
                	break;
                }
                String pplanName = "";
                String pum = technicsElement.attributeValue("pplanNumber");
				String pname = technicsElement.attributeValue("pplanName");
				if(pum!=null &&!"".equals(pum)&&pname!=null &&!"".equals(pname)){
					pplanName= pname +"("+pum+")";
				}else{
					pplanName = technicsElement.attributeValue("technicsName");
				}

                String pplanVersion = technicsElement.attributeValue("version");
                String pplanState = technicsElement.attributeValue("lifecycle");

            	if(Constants.PROCESS_TYPEB_TEMP.equals(pplanType)){//如果是临时工艺文件
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
                    	MaterialsBean bean = new MaterialsBean();
            			bean.setMaterialType("N");
            			bean.setPplanNumber(pplanNumber);
            			bean.setPplanName(pplanName);
            			bean.setPplanVersion(pplanVersion);
            			bean.setPplanState(pplanState);
            			bean.setChbm("N");
            			bean.setChmc("N");
            			bean.setMaterialState("");
            			bean.setBatch(batch);
            			list.add(bean);
                    }
                }

            }
		}
		System.out.println("----------list------------"+list);
		return list;
	}

	public static boolean readXML2(List<String> xmlFileList) throws DocumentException {
		boolean flag = false;
		for (String xmlFile : xmlFileList) {
			File file = new File(xmlFile);
			if(!file.exists()) {
				System.out.println(xmlFile+" is not exist!");
				continue;
			}
			SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            List yclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/YCLDE/ycldeRecord");
            List zyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/ZYCLDE/zycldeRecord");
            List sjyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/SJYCLDE/sjycldeRecord");
            List sjzykyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/SJZYKYCLDE/ycldeRecord");
            List sjzykzyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/SJZYKZYCLDE/zycldeRecord");
            List sjzyksjyclde = rootElement.selectNodes("/technics/QMFawTechnicsInfo/CLDE/SJZYKSJYCLDE/sjycldeRecord");

            if((yclde != null && !yclde.isEmpty())
            		|| (zyclde != null && !zyclde.isEmpty())
            		|| (sjyclde != null && !sjyclde.isEmpty())
            		|| (sjzykyclde != null && !sjzykyclde.isEmpty())
            		|| (sjzykzyclde != null && !sjzykzyclde.isEmpty())
            		|| (sjzyksjyclde != null && !sjzyksjyclde.isEmpty())) {
            	flag = true;
            }
		}
		return flag;
	}

	/**
	 * 判断工艺文件是否材料定额
	 *
	 * @param docOid
	 * @throws DocumentException
	 */
	public static boolean isClde(WTDocument document) throws DocumentException {
		String docOid = String.valueOf(PersistenceHelper.getObjectIdentifier(document).getId());
		List<Vector<Object>> list = ProcessEditorToWCIntfRMI.searchTechnicsRMI(docOid);
		List<String> xmlFileList = new ArrayList<String>();
		List<File> deleteFiles = new ArrayList<File>();
		if (list.size() > 0) {
			for(int i=0;i<list.size();i++) {
				Vector result = list.get(i);
				if ((result != null) && (result.size() == 4)) {
					String fileName = (String) result.get(0);
					byte[] data = (byte[]) result.get(1);
					if ((data == null) || (data.length <= 0)) {
						return false;
					}
					if (fileName.toLowerCase().endsWith(".zip")) {
						fileName = fileName.substring(0, fileName.length() - 4);
					}

					String zipFilePath = zip_temp_dir+File.separator+document.getNumber();
					File file = new File(zipFilePath);
					if(!file.exists()) {
						file.mkdirs();
					}

					String xmlFile = zipFilePath+File.separator+document.getNumber()+".xml";
					xmlFileList.add(xmlFile);

					ZipUtil.unZip(data, zipFilePath);

					deleteFiles.add(file);
				}
			}
		}

		boolean flag = readXML2(xmlFileList);

		//删除临时文件
		deleteFiles(new File(zip_temp_dir+File.separator+docOid));

		for(File deleteFile :deleteFiles){
			deleteFiles(deleteFile);
		}


		return flag;
	}

	public static void deleteFiles(File dir) {
		if(dir == null || !dir.exists() || !dir.isDirectory()) {
			return;
		}
		for(File file:dir.listFiles()) {
			if(file.isFile()) {
				file.delete();
			} else if(file.isDirectory()) {
				deleteFiles(file);
			}
		}
		dir.delete();
	}

	public static Object[] readXML(String xmlFile) throws DocumentException {
		Object[] beans = new Object[2];
		List<MaterialsBean> list = new ArrayList<MaterialsBean>();
		List<PbomErpPartBean> list2 = new ArrayList<PbomErpPartBean>();
		beans[0] = list;
		beans[1] = list2;
		File file = new File(xmlFile);
		if(!file.exists()) {
			System.out.println(xmlFile+" is not exist!");
			return null;
		}
		SAXReader reader = new SAXReader();
        Document document = reader.read(file);
        Element rootElement = document.getRootElement();
        Iterator iterator = rootElement.elementIterator();
        while(iterator.hasNext()) {
            Element technicsElement = (Element)iterator.next();

            String pplanNumber = technicsElement.attributeValue("technicsNumber");
            String processFileNumber = technicsElement.attributeValue("pplanNumber");
            String batch = technicsElement.attributeValue("PCNO");
            String planType = technicsElement.attributeValue("PPLANTYPE");
            String zfflag  = technicsElement.attributeValue("ZFFLAG");

            String pplanName = "";
            String pum = technicsElement.attributeValue("pplanNumber");
			String pname = technicsElement.attributeValue("pplanName");
			if(pum!=null &&!"".equals(pum)&&pname!=null &&!"".equals(pname)){
				pplanName= pname +"("+pum+")";
			}else{
				pplanName = technicsElement.attributeValue("technicsName");
			}

            String pplanVersion = technicsElement.attributeValue("version");
            String pplanState = technicsElement.attributeValue("lifecycle");
            Element cldeEle = technicsElement.element("CLDE");
            if(cldeEle != null) {
            	Element YCLDE = cldeEle.element("YCLDE");
            	Iterator ite4 = null;
            	if(YCLDE!=null){
            		ite4 = YCLDE.elementIterator();
            		while(ite4.hasNext()) {
            			Element ele = (Element)ite4.next();
            			MaterialsBean bean = new MaterialsBean();
            			bean.setMaterialType("外购件");
            			bean.setPplanNumber(pplanNumber);
            			bean.setPplanName(pplanName);
            			bean.setPplanVersion(pplanVersion);
            			bean.setPplanState(pplanState);
            			bean.setMaterialState("");
            			bean.setBatch(batch);
            			bean.setProcessFileNumber(processFileNumber);
            			bean.setUsingType("原材料定额");
        				bean.setChbm(ele.attributeValue("chbm"));
            			//bean.setChmc(ele.attributeValue("chmc"));
        				bean.setChmc("");
            			bean.setZjldw(ele.attributeValue("zjldw"));
            			bean.setDataFrom(ele.attributeValue("dataFrom"));

        				bean.setXlcc(ele.attributeValue("xlcc"));
        				bean.setKzjs(ele.attributeValue("kzjs"));

        				bean.setDw(ele.attributeValue("dw"));
        				bean.setComment(ele.attributeValue("comment"));
        				bean.setPplanType(planType);
            			bean.setZfflag(zfflag);

        				list.add(bean);
            		}
            	}


        		Element SJYCLDE = cldeEle.element("SJYCLDE");
        		if(SJYCLDE!=null){
        			ite4 = SJYCLDE.elementIterator();
            		while(ite4.hasNext()) {
            			Element ele = (Element)ite4.next();
            			MaterialsBean bean = new MaterialsBean();
            			bean.setMaterialType("外购件");
            			bean.setPplanNumber(pplanNumber);
            			bean.setPplanName(pplanName);
            			bean.setPplanVersion(pplanVersion);
            			bean.setPplanState(pplanState);
            			bean.setMaterialState("");
            			bean.setBatch(batch);
            			bean.setProcessFileNumber(processFileNumber);
            			bean.setUsingType("试件原材料定额");
            			bean.setChbm(ele.attributeValue("chbm"));
            			//bean.setChmc(ele.attributeValue("chmc"));
            			bean.setChmc("");
            			bean.setZjldw(ele.attributeValue("zjldw"));
            			bean.setDataFrom(ele.attributeValue("dataFrom"));

            			bean.setSjcc(ele.attributeValue("sjcc"));
            			bean.setSjkzjs(ele.attributeValue("sjkzjs"));
            			bean.setSjsl(ele.attributeValue("sjsl"));
            			bean.setDw(ele.attributeValue("dw"));
        				bean.setComment(ele.attributeValue("comment"));
        				bean.setPplanType(planType);
        				bean.setZfflag(zfflag);
        				list.add(bean);
            		}

        		}

        		Element ZYCLDE = cldeEle.element("ZYCLDE");
        		if(ZYCLDE!=null){
        			ite4 = ZYCLDE.elementIterator();
            		while(ite4.hasNext()) {
            			Element ele = (Element)ite4.next();
            			MaterialsBean bean = new MaterialsBean();
            			bean.setMaterialType("外购件");
            			bean.setPplanNumber(pplanNumber);
            			bean.setPplanName(pplanName);
            			bean.setPplanVersion(pplanVersion);
            			bean.setPplanState(pplanState);
            			bean.setMaterialState("");
            			bean.setBatch(batch);
            			bean.setProcessFileNumber(processFileNumber);
            			bean.setUsingType("主要材料定额");
            			bean.setChbm(ele.attributeValue("chbm"));
            			//bean.setChmc(ele.attributeValue("chmc"));
            			bean.setChmc("");
            			bean.setZjldw(ele.attributeValue("zjldw"));
            			bean.setDataFrom(ele.attributeValue("dataFrom"));

            			bean.setSl(ele.attributeValue("sl"));
            			bean.setDw(ele.attributeValue("dw"));
        				bean.setComment(ele.attributeValue("comment"));
        				bean.setPplanType(planType);
        				bean.setZfflag(zfflag);
            			list.add(bean);
            		}
        		}


        		Element SJZYKYCLDE = cldeEle.element("SJZYKYCLDE");
        		if(SJZYKYCLDE!=null){
        			ite4 = SJZYKYCLDE.elementIterator();
            		while(ite4.hasNext()) {
            			Element ele = (Element)ite4.next();
            			MaterialsBean bean = new MaterialsBean();
            			bean.setMaterialType("外购件");
            			bean.setPplanNumber(pplanNumber);
            			bean.setPplanName(pplanName);
            			bean.setPplanVersion(pplanVersion);
            			bean.setPplanState(pplanState);
            			bean.setMaterialState("");
            			bean.setBatch(batch);
            			bean.setProcessFileNumber(processFileNumber);
            			bean.setPplanType(planType);
            			bean.setZfflag(zfflag);
            			bean.setUsingType("原材料定额");
        				bean.setChbm(ele.attributeValue("sjbm"));
            			bean.setChmc(ele.attributeValue("name"));
            			bean.setZjldw(ele.attributeValue("jldw"));
            			bean.setDataFrom(ele.attributeValue("dataFrom"));

        				bean.setXlcc(ele.attributeValue("xlcc"));
        				bean.setKzjs(ele.attributeValue("kzjs"));
        				bean.setDw(ele.attributeValue("dw"));
        				bean.setComment(ele.attributeValue("comment"));
            			list.add(bean);
            		}
        		}


        		Element SJZYKSJYCLDE = cldeEle.element("SJZYKSJYCLDE");
        		if(SJZYKSJYCLDE!=null){
        			ite4 = SJZYKSJYCLDE.elementIterator();
            		while(ite4.hasNext()) {
            			Element ele = (Element)ite4.next();
            			MaterialsBean bean = new MaterialsBean();
            			bean.setMaterialType("外购件");
            			bean.setPplanNumber(pplanNumber);
            			bean.setPplanName(pplanName);
            			bean.setPplanVersion(pplanVersion);
            			bean.setPplanState(pplanState);
            			bean.setMaterialState("");
            			bean.setBatch(batch);
            			bean.setProcessFileNumber(processFileNumber);
            			bean.setPplanType(planType);
            			bean.setZfflag(zfflag);
            			bean.setUsingType("试件原材料定额");
            			bean.setChbm(ele.attributeValue("sjbm"));
            			bean.setChmc(ele.attributeValue("name"));
            			bean.setZjldw(ele.attributeValue("jldw"));
            			bean.setDataFrom(ele.attributeValue("dataFrom"));

            			bean.setSjcc(ele.attributeValue("sjcc"));
            			bean.setSjkzjs(ele.attributeValue("sjkzjs"));
            			bean.setSjsl(ele.attributeValue("sjsl"));
            			bean.setDw(ele.attributeValue("dw"));
        				bean.setComment(ele.attributeValue("comment"));
            			list.add(bean);
            		}
        		}

        		Element SJZYKZYCLDE = cldeEle.element("SJZYKZYCLDE");
        		if(SJZYKZYCLDE!=null){
        			ite4 = SJZYKZYCLDE.elementIterator();
            		while(ite4.hasNext()) {
            			Element ele = (Element)ite4.next();
            			MaterialsBean bean = new MaterialsBean();
            			bean.setMaterialType("外购件");
            			bean.setPplanNumber(pplanNumber);
            			bean.setPplanName(pplanName);
            			bean.setPplanVersion(pplanVersion);
            			bean.setPplanState(pplanState);
            			bean.setMaterialState("");
            			bean.setBatch(batch);
            			bean.setProcessFileNumber(processFileNumber);
            			bean.setPplanType(planType);
            			bean.setZfflag(zfflag);
            			bean.setUsingType("主要材料定额");
            			bean.setChbm(ele.attributeValue("sjbm"));
            			bean.setChmc(ele.attributeValue("name"));
            			bean.setZjldw(ele.attributeValue("jldw"));
            			bean.setDataFrom(ele.attributeValue("dataFrom"));

            			bean.setSl(ele.attributeValue("sl"));
            			bean.setDw(ele.attributeValue("dw"));
        				bean.setComment(ele.attributeValue("comment"));
            			list.add(bean);
            		}
        		}

            }

            Element gydeEle = technicsElement.element("GYDE");
			if(gydeEle != null) {
				Element eles = gydeEle.element("MATCHPART");
				if(eles != null) {
					List<Element> templist = eles.elements();
					if(templist != null && !templist.isEmpty()) {
						PbomErpPartBean bean = null;
						for (Element element : templist) {
							bean = new PbomErpPartBean();
							bean.setPartNumber(element.attributeValue("number"));
							bean.setChbm(element.attributeValue("chbm"));
							//bean.setChmc(element.attributeValue("chmc"));
							bean.setChmc("");
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
							bean.setTechNumber(technicsElement.attributeValue("technicsNumber"));

							 pplanNumber = technicsElement.attributeValue("pplanNumber");
							 pplanName = technicsElement.attributeValue("pplanName");
							if(pplanNumber!=null &&!"".equals(pplanNumber)&&pplanName!=null &&!"".equals(pplanName)){
								String technicsName = pplanName +"("+pplanNumber+")";
								bean.setTechName(technicsName);
							}else{
								bean.setTechName(technicsElement.attributeValue("technicsName"));
							}
							bean.setPplanNumber(technicsElement.attributeValue("pplanNumber"));

							bean.setVersion(technicsElement.attributeValue("version"));
							bean.setType("工艺定额匹配");
							bean.setPplanType(planType);
	            			bean.setZfflag(zfflag);
							list2.add(bean);
						}
					}
				}

				eles = gydeEle.element("SJZYKMATCHPART");
				if(eles != null) {
					List<Element> templist = eles.elements();
					if(templist != null && !templist.isEmpty()) {
						PbomErpPartBean bean = null;
						for (Element element : templist) {
							bean = new PbomErpPartBean();
							bean.setPartNumber(element.attributeValue("partNumber"));
							bean.setChbm(element.attributeValue("sjbm"));
							bean.setChmc(element.attributeValue("name"));

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
							bean.setTechNumber(technicsElement.attributeValue("technicsNumber"));

							 pplanNumber = technicsElement.attributeValue("pplanNumber");
							 pplanName = technicsElement.attributeValue("pplanName");
							if(pplanNumber!=null &&!"".equals(pplanNumber)&&pplanName!=null &&!"".equals(pplanName)){
								String technicsName = pplanName +"("+pplanNumber+")";
								bean.setTechName(technicsName);
							}else{
								bean.setTechName(technicsElement.attributeValue("technicsName"));
							}

							bean.setPplanNumber(technicsElement.attributeValue("pplanNumber"));
							bean.setVersion(technicsElement.attributeValue("version"));
							bean.setType("工艺定额匹配");
							bean.setPplanType(planType);
							bean.setZfflag(zfflag);
							list2.add(bean);
						}
					}
				}

				 eles = gydeEle.element("NEWPART");
				if(eles != null) {
					List<Element> templist = eles.elements();
					if(templist != null && !templist.isEmpty()) {
						PbomErpPartBean bean = null;
						for (Element element : templist) {
							bean = new PbomErpPartBean();
							bean.setPartNumber(element.attributeValue("parentNumber"));
							bean.setChbm(element.attributeValue("chbm"));
							//bean.setChmc(element.attributeValue("chmc"));
							bean.setChmc("");
							bean.setSl(element.attributeValue("sl"));//gysl
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
							bean.setTechNumber(technicsElement.attributeValue("technicsNumber"));

							pplanNumber = technicsElement.attributeValue("pplanNumber");
							pplanName = technicsElement.attributeValue("pplanName");
							if(pplanNumber!=null &&!"".equals(pplanNumber)&&pplanName!=null &&!"".equals(pplanName)){
								String technicsName = pplanName +"("+pplanNumber+")";
								bean.setTechName(technicsName);
							}else{
								bean.setTechName(technicsElement.attributeValue("technicsName"));
							}

							bean.setPplanNumber(technicsElement.attributeValue("pplanNumber"));
							bean.setVersion(technicsElement.attributeValue("version"));
							bean.setType("工艺定额新增");
							bean.setPplanType(planType);
							bean.setZfflag(zfflag);
							list2.add(bean);
						}
					}
				}

				eles = gydeEle.element("SJZYKNEWPART");
				if(eles != null) {
					List<Element> templist = eles.elements();
					if(templist != null && !templist.isEmpty()) {
						PbomErpPartBean bean = null;
						for (Element element : templist) {
							bean = new PbomErpPartBean();
							bean.setPartNumber(element.attributeValue("parentPartNumber"));
							bean.setChbm(element.attributeValue("sjbm"));
							bean.setChmc(element.attributeValue("name"));

							String sl = element.attributeValue("gysl");
							if(Tools.isNull(sl)){
								sl = element.attributeValue("sl");
							}
							bean.setSl(sl);
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
							bean.setTechNumber(technicsElement.attributeValue("technicsNumber"));

							 pplanNumber = technicsElement.attributeValue("pplanNumber");
							 pplanName = technicsElement.attributeValue("pplanName");
							if(pplanNumber!=null &&!"".equals(pplanNumber)&&pplanName!=null &&!"".equals(pplanName)){
								String technicsName = pplanName +"("+pplanNumber+")";
								bean.setTechName(technicsName);
							}else{
								bean.setTechName(technicsElement.attributeValue("technicsName"));
							}
							bean.setPplanNumber(technicsElement.attributeValue("pplanNumber"));
							bean.setVersion(technicsElement.attributeValue("version"));
							bean.setType("工艺定额新增");
							bean.setPplanType(planType);
							bean.setZfflag(zfflag);
							list2.add(bean);
						}
					}
				}

			}
        }
		return beans;
	}

}
