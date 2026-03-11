package com.glaway.mpm.processplan.checkouttable;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.processplan.checkouttable.checkbean.*;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.WTDocumentUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.util.WTProperties;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class CheckOutUtil {

    public static String getXmlPath(String technicsNumber) throws IOException {
        WTProperties wtProperties = WTProperties.getLocalProperties();
        String tempPath = wtProperties.getProperty("wt.codebase.location");
        String xmlPath = tempPath + File.separator + "temp" + File.separator + "checkouttable"
                + File.separator + technicsNumber + File.separator + technicsNumber + ".xml";
        return xmlPath;
    }

    /**
     * 获取工艺文件XML路径
     *
     * @param technicsNumber
     * @return
     * @author jyx
     * @date 2018-5-9
     */
    public static String getTechincsXMLPath(String technicsNumber) {
        try {
            WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
            ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
            byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
            String technicsDirectory = createTechnicsDirectory(technicsNumber);
            TechnicsZipUtil.unZip(bytes, technicsDirectory, technicsDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String createTechnicsDirectory(String fileName)
            throws Exception {
        WTProperties wtProperties = WTProperties.getLocalProperties();
        String tempPath = wtProperties.getProperty("wt.codebase.location");
        String dir = tempPath + File.separator + "temp" + File.separator + "checkouttable" + File.separator + fileName;
        File file = new File(dir);
        file.mkdirs();
        return dir;
    }

    public static List<CheckRecordBean> getCheckRecords(String technicsXmlPath, String basePath, String stepName) {
        String stepNumberLink = "";
        if(stepName != null){
            stepNumberLink = stepName.substring(0, stepName.indexOf("_"));
        }
        List<CheckRecordBean> checkRecordBeanList = new ArrayList<CheckRecordBean>();
        CheckRecordBean checkRecordBean;
        Document document = null;
        try {
            SAXReader sax = new SAXReader();
            document = sax.read(new File(technicsXmlPath));
            Element root = document.getRootElement();
            List<Element> stepElementList = root.selectNodes("QMFawTechnicsInfo/steps/QMProcedureInfo");
            for (Element stepElement : stepElementList) {
                String stepNumber = stepElement.attributeValue("stepNumber");
                if (stepNumber.equals(stepNumberLink) || stepName == null) {
                    List<Element> paceElementList = stepElement.selectNodes("paces/QMProcedureInfo");
                    for (Element paceElement : paceElementList) {
                        String paceNumber = paceElement.attributeValue("stepNumber");
                        String jyffms = paceElement.elementText("checkmethodContent");
                        jyffms = Html2Text(jyffms);
                        jyffms = jyffms.replace("@#$%^", basePath);
                        jyffms = jyffms.replace("\\", "/");
                        String jynrms = paceElement.elementText("checkcontentContent");
                        jynrms = Html2Text(jynrms);
                        jynrms = jynrms.replace("@#$%^", basePath);
                        jynrms = jynrms.replace("\\", "/");
                        List<Element> parameterTableList = paceElement.selectNodes("checkRecordTables/parameterTable");
                        for (Element parameterTable : parameterTableList) {
                            String dybbm = parameterTable.attributeValue("name");
                            String bzjlx = parameterTable.attributeValue("type");
                            String xmm = parameterTable.attributeValue("projectName");
                            String tbm = parameterTable.attributeValue("tableName");
                            List<Element> parameterList = parameterTable.selectNodes("parameter");
                            for (Element parameterEle : parameterList) {
                                checkRecordBean = new CheckRecordBean();
                                checkRecordBean.setStepNumber(stepNumber);
                                checkRecordBean.setPaceNumber(paceNumber);
                                checkRecordBean.setDybbm(dybbm);
                                checkRecordBean.setBzjlx(bzjlx);
                                checkRecordBean.setXmm(xmm);
                                checkRecordBean.setTbm(tbm);
                                checkRecordBean.setJcffms(jyffms);
                                checkRecordBean.setJcnrms(jynrms);
                                if ("检测类".equals(bzjlx)) {
                                    checkRecordBean.setJl("/");
                                } else if ("记录类".equals(bzjlx)) {
                                    checkRecordBean.setSpc("/");
                                    checkRecordBean.setXpc("/");
                                    checkRecordBean.setScz("/");
                                }
                                checkRecordBean.setQsxx("");
                                List<Element> valueList = parameterEle.selectNodes("values/value");
                                for (Element value : valueList) {
                                    String columnName = value.attributeValue("columnName");
                                    String attributeValue = value.element("attribute").getText();
                                    attributeValue = getValue(attributeValue, basePath);
                                    attributeValue = PDFUtil.objectToString(attributeValue);
                                    if ("检测项".equals(columnName) || "记录项".equals(columnName)) {
                                        checkRecordBean.setJlx_jcx(attributeValue);
                                    } else if ("公称值".equals(columnName) || "要求".equals(columnName)) {
                                        checkRecordBean.setYqz_gcz(attributeValue);
                                    } else if ("上偏差".equals(columnName)) {
                                        checkRecordBean.setSpc(attributeValue);
                                    } else if ("下偏差".equals(columnName)) {
                                        checkRecordBean.setXpc(attributeValue);
                                    } else if ("实测值".equals(columnName)) {
                                        checkRecordBean.setScz(attributeValue);
                                    } else if ("判定/结论".equals(columnName)) {
                                        //无需处理
                                    } else if ("记录".equals(columnName)) {
                                        checkRecordBean.setJl(attributeValue);
                                    }
                                }
                                checkRecordBeanList.add(checkRecordBean);
                            }

                        }
                    }
                }else{

                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return checkRecordBeanList;
    }

    private static String filterColumnValue(String value, String checkType, String columnName) {
        String str = "";
        if ("检测类".equals(checkType)) {
            if ("记录".equals(columnName)) {
                str = "//";
            } else {
                str = (value == null ? "" : value);
            }
        } else if ("纪录类".equals(checkType)) {
            if ("上偏差".equals(columnName) || "下偏差".equals(columnName) || "实测值".equals(columnName)) {
                str = "//";
            } else {
                str = (value == null ? "" : value);
            }
        }
        return str;
    }

    private static String getValue(String value, String basePath) {
        value = Html2Text(value);
        value = value.replace("@#$%^", basePath);
        value = value.replace("\\", "/");
        return value;
    }

    /**
     * 获取检验记录表的集合
     *
     * @param TechincsXMLPath
     * @author jyx
     * @date 2018-5-9
     */
    public static List<ProjectNameBean> getXMLCheckOutTbaleList(String techincsXMLPath, String tableName, String tableType, String basePath) {
        List<ProjectNameBean> projectNameBeanList = new ArrayList<ProjectNameBean>();
        try {
            SAXReader sax = new SAXReader();
            Document document = sax.read(new File(techincsXMLPath));
            Element root = document.getRootElement();
            Map<String, List<StepNOBean>> stepNOBeanMap = new HashMap<String, List<StepNOBean>>();//一道工艺下面的工序集合，key值是单元表的项目名称
            List<Element> stepElementList = root.selectNodes("QMFawTechnicsInfo/steps/QMProcedureInfo");
            for (Element stepEle : stepElementList) {
                String stepNum = parseName(stepEle, "stepNumber");
                StepNOBean stepNOBean = null;
                List<StepNOBean> stepNOBeanlist = null;
                StepNOBean jcStepNOBean = new StepNOBean();
                Map<String, List<PaceNoBean>> paceNoBeanBeanMap = new HashMap<String, List<PaceNoBean>>();//一道工序下面的工步集合，key值是单元表的项目名称
                List<Element> paceElementList = stepEle.selectNodes("paces");
                for (Element temp2 : paceElementList) {
                    paceNoBeanBeanMap = getPaceNoBeanMap(temp2, techincsXMLPath, tableName, tableType, stepNum, basePath);
                    break;
                }
                Set<Entry<String, List<PaceNoBean>>> entrySet = paceNoBeanBeanMap.entrySet();
                for (Entry<String, List<PaceNoBean>> entry : entrySet) {
                    stepNOBeanlist = new ArrayList<StepNOBean>();
                    stepNOBean = new StepNOBean();
                    stepNOBean.setStepNo(stepNum);
                    String projectName = entry.getKey();
                    stepNOBean.setProjectName(projectName);
                    List<PaceNoBean> list = paceNoBeanBeanMap.get(projectName);
                    stepNOBean.setPaceNoBeanlist(list);
                    int tableUnitCount = 0;
                    for (PaceNoBean paceNoBean2 : list) {
                        tableUnitCount = tableUnitCount + paceNoBean2.getTableUnitCount();
                    }
                    stepNOBean.setTableUnitCount(tableUnitCount);

                    stepNOBeanlist.add(stepNOBean);
                    if (stepNOBeanMap.containsKey(projectName)) {
                        List<StepNOBean> list2 = stepNOBeanMap.get(projectName);
                        list2.addAll(stepNOBeanlist);
                        stepNOBeanMap.put(projectName, list2);
                    } else {
                        stepNOBeanMap.put(projectName, stepNOBeanlist);
                    }
                }
            }
//			for (Element element : XmlUtil.getElementsByName(root, "QMFawTechnicsInfo")) {
//				for (Element temp : XmlUtil.getElementsByName(element, "steps")) {
//					for (Element temp1 : XmlUtil.getElementsByName(temp, "QMProcedureInfo")) {
//						String stepNum = parseName(temp1, "stepNumber");
//						StepNOBean stepNOBean = null;
//						List<StepNOBean> stepNOBeanlist = null;
//						StepNOBean jcStepNOBean = new StepNOBean();
//						Map<String, List<PaceNoBean>> paceNoBeanBeanMap = new HashMap<String, List<PaceNoBean>>();//一道工序下面的工步集合，key值是单元表的项目名称
//						for (Element temp2 : XmlUtil.getElementsByName(temp1, "paces")) {
//							paceNoBeanBeanMap = getPaceNoBeanMap(temp2, techincsXMLPath, tableName, tableType, stepNum,basePath);
//							break;
//						}
//						Set<Entry<String, List<PaceNoBean>>> entrySet = paceNoBeanBeanMap.entrySet();
//						for (Entry<String, List<PaceNoBean>> entry : entrySet) {
//							stepNOBeanlist = new ArrayList<StepNOBean>();
//							stepNOBean = new StepNOBean();
//							stepNOBean.setStepNo(stepNum);
//							String projectName = entry.getKey();
//							stepNOBean.setProjectName(projectName);
//							List<PaceNoBean> list = paceNoBeanBeanMap.get(projectName);
//							stepNOBean.setPaceNoBeanlist(list);
//							int tableUnitCount = 0;
//							for (PaceNoBean paceNoBean2 : list) {
//								tableUnitCount = tableUnitCount+paceNoBean2.getTableUnitCount();
//							}
//							stepNOBean.setTableUnitCount(tableUnitCount);
//
//							stepNOBeanlist.add(stepNOBean);
//							if(stepNOBeanMap.containsKey(projectName)){
//								List<StepNOBean> list2 = stepNOBeanMap.get(projectName);
//								list2.addAll(stepNOBeanlist);
//								stepNOBeanMap.put(projectName, list2);
//							}else{
//								stepNOBeanMap.put(projectName, stepNOBeanlist);
//							}
//						}
//					}
//				}
//			}
            Set<Entry<String, List<StepNOBean>>> entrySet = stepNOBeanMap.entrySet();
            ProjectNameBean projectNameBean = null;
            for (Entry<String, List<StepNOBean>> entry : entrySet) {
                String projectName = entry.getKey();
                List<StepNOBean> list = stepNOBeanMap.get(projectName);
                projectNameBean = new ProjectNameBean();
                projectNameBean.setProjectName(projectName);
                projectNameBean.setStepNOBeanlist(list);
                int tableUnitCount = 0;
                for (StepNOBean stepNOBean2 : list) {
                    tableUnitCount = tableUnitCount + stepNOBean2.getTableUnitCount();
                }
                projectNameBean.setTableUnitCount(tableUnitCount);

                projectNameBeanList.add(projectNameBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return projectNameBeanList;
    }

    /**
     * 获取工步单元表
     *
     * @param TechincsXMLPath
     * @param tableName
     * @param tableType
     * @author zhuhao
     * @date 2018-5-10
     */
    public static Map<String, List<PaceNoBean>> getPaceNoBeanMap(Element temp2, String techincsXMLPath, String tableName, String tableType, String stepNum, String basePath) {
        Map<String, List<PaceNoBean>> paceNoBeanBeanMap = new HashMap<String, List<PaceNoBean>>();//一道工已下面的工步集合，key值是单元表的项目名称
        List<Element> elementList = temp2.selectNodes("QMProcedureInfo");
        for (Element temp3 : elementList) {
            List<PaceNoBean> paceNoBeanList = null;
            PaceNoBean paceNoBean = null;
            String paceNum = parseName(temp3, "stepNumber");
            String checkmethodContent = "";
            String checkcontentContent = "";
            Element checkmethodContentElement = temp3.element("checkmethodContent");
            if (checkmethodContentElement != null) {
                checkmethodContent = checkmethodContentElement.getText();
                checkmethodContent = Html2Text(checkmethodContent);
                checkmethodContent = checkmethodContent.replace("@#$%^", basePath);
                checkmethodContent = checkmethodContent.replace("\\", "/");
            }
            Element checkcontentContentElement = temp3.element("checkcontentContent");
            if (checkcontentContentElement != null) {
                checkcontentContent = checkcontentContentElement.getText();
                checkcontentContent = Html2Text(checkcontentContent);
                checkcontentContent = checkcontentContent.replace("@#$%^", basePath);
                checkcontentContent = checkcontentContent.replace("\\", "/");
            }

            Map<String, List<CheckOutTableListBean>> checkOutTableListBeanMap = new HashMap<String, List<CheckOutTableListBean>>();//一道工步下面的单元表集合，key值是单元表的项目名称
            List<CheckOutTableListBean> checkOutTableListBeans = null;
            for (Element temp4 : (List<Element>) temp3.selectNodes("checkRecordTables")) {

                CheckOutTableListBean checkOutTableListBean = null;

                for (Element temp5 : (List<Element>) temp4.selectNodes("parameterTable")) {//该循环是以一个单元表的形式，循环一次，代表工步的一个单元表
                    checkOutTableListBean = new CheckOutTableListBean();
                    String dybbm_Value = parseName(temp5, "name");
                    String bzjlx_Value = parseName(temp5, "type");
                    String xmm_Value = parseName(temp5, "projectName");
                    String tbm_Value = parseName(temp5, "tableName");
                    String mxCs_Value = parseName(temp5, "eachName");
                    String uuid_Value = parseName(temp5, "bsoID");

                    String gcz_Value = "";
                    String spc_Value = "";
                    String xpc_Value = "";
                    String yq_Value = "";
                    String jyl_Value = "";
                    String jll_Value = "";
                    String scz_Value = "";
                    List<CheckOutTableBean> checkOutTableBeanlist = new ArrayList<CheckOutTableBean>();
                    CheckOutTableBean checkOutTableBean = null;
                    if (tableName.equals(tbm_Value.trim()) && tableType.equals(bzjlx_Value.trim())) {

                        for (Element temp6 : (List<Element>) temp5.selectNodes("parameter/values")) {
//							for (Element temp7 : XmlUtil.getElementsByName(temp6, "values")) {
                            checkOutTableBean = new CheckOutTableBean();
                            for (Element temp8 : (List<Element>) temp6.selectNodes("value")) {
                                if ("检测项".equals(parseName(temp8, "columnName"))) {
                                    for (Element temp9 : (List<Element>) temp8.selectNodes("attribute")) {
                                        jyl_Value = temp9.getText();
                                    }
                                }
                                if ("记录项".equals(parseName(temp8, "columnName"))) {
                                    for (Element temp9 : (List<Element>) temp8.selectNodes("attribute")) {
                                        jll_Value = temp9.getText();
                                        jll_Value = Html2Text(jll_Value);
                                        jll_Value = jll_Value.replace("@#$%^", basePath);
                                        jll_Value = jll_Value.replace("\\", "/");
                                        //jll_Value = PDFUtil.Html2Text(jll_Value).trim();
                                    }
                                }
                                if ("公称值".equals(parseName(temp8, "columnName"))) {
                                    for (Element temp9 : (List<Element>) temp8.selectNodes("attribute")) {
                                        gcz_Value = temp9.getText();
                                    }
                                }
                                if ("上偏差".equals(parseName(temp8, "columnName"))) {
                                    for (Element temp9 : (List<Element>) temp8.selectNodes("attribute")) {
                                        spc_Value = temp9.getText();
                                    }
                                }
                                if ("下偏差".equals(parseName(temp8, "columnName"))) {
                                    for (Element temp9 : (List<Element>) temp8.selectNodes("attribute")) {
                                        xpc_Value = temp9.getText();
                                    }
                                }
                                if ("要求".equals(parseName(temp8, "columnName"))) {
                                    for (Element temp9 : (List<Element>) temp8.selectNodes("attribute")) {
                                        yq_Value = temp9.getText();
                                        yq_Value = Html2Text(yq_Value);
                                        yq_Value = yq_Value.replace("@#$%^", basePath);
                                        yq_Value = yq_Value.replace("\\", "/");
                                    }
                                }
                                if ("实测值".equals(parseName(temp8, "columnName"))) {
                                    for (Element temp9 : (List<Element>) temp8.selectNodes("attribute")) {
                                        scz_Value = temp9.getText();
                                        scz_Value = Html2Text(scz_Value);
                                        scz_Value = scz_Value.replace("@#$%^", basePath);
                                        scz_Value = scz_Value.replace("\\", "/");
                                    }
                                }
//								}
                                checkOutTableBean.setProjectName(xmm_Value);//项目名
                                checkOutTableBean.setType(bzjlx_Value);//类型
                                checkOutTableBean.setName(dybbm_Value);//单元表表名
                                checkOutTableBean.setTableName(tbm_Value);//套表名
                                checkOutTableBean.setJlxValue(jll_Value);//记录项
                                checkOutTableBean.setYqzValue(yq_Value);//要求值
                                checkOutTableBean.setSczValue(scz_Value);//实测值
                                checkOutTableBean.setSpcValue(spc_Value);//上偏差
                                checkOutTableBean.setXpcValue(xpc_Value);//下偏差

                                checkOutTableBean.setQsxxValue("");//签审信息
                                checkOutTableBean.setStepNO(stepNum);//工序号
                                checkOutTableBean.setPaceNo(paceNum);//工步号
                                checkOutTableBean.setCheckcontentContent(checkcontentContent);//检验内容描述
                                checkOutTableBean.setCheckmethodContent(checkmethodContent);//检验方法描述
                                if ("检测类".equals(tableType)) {
                                    checkOutTableBean.setJlxValue(jyl_Value);
                                    checkOutTableBean.setYqzValue(gcz_Value);
                                }
                                checkOutTableBeanlist.add(checkOutTableBean);
                            }
                        }
                    } else {
                        continue;
                    }
                    if (checkOutTableBeanlist.size() > 0) {
                        checkOutTableListBean.setProjectName(xmm_Value);
                        checkOutTableListBean.setUnitName(dybbm_Value);
                        checkOutTableListBean.setCheckOutTableBeanList(checkOutTableBeanlist);
                        checkOutTableListBean.setTableUnitCount(checkOutTableBeanlist.size());

                        checkOutTableListBeans = new ArrayList<CheckOutTableListBean>();
                        checkOutTableListBeans.add(checkOutTableListBean);
                        String projectName = checkOutTableListBean.getProjectName();
                        if (checkOutTableListBeanMap.containsKey(projectName)) {//按照单元表的项目名称区分开
                            List<CheckOutTableListBean> list = checkOutTableListBeanMap.get(projectName);
                            list.addAll(checkOutTableListBeans);
                            checkOutTableListBeanMap.put(projectName, list);
                        } else {
                            checkOutTableListBeanMap.put(projectName, checkOutTableListBeans);
                        }
                    }
                }

            }
            Set<Entry<String, List<CheckOutTableListBean>>> entrySet = checkOutTableListBeanMap.entrySet();
            //将单元格统计到对应的工步中，并按项目名区分
            for (Entry<String, List<CheckOutTableListBean>> entry : entrySet) {
                paceNoBeanList = new ArrayList<PaceNoBean>();
                paceNoBean = new PaceNoBean();
                paceNoBean.setPeceNo(paceNum);
                String projectName = entry.getKey();
                paceNoBean.setProjectName(projectName);
                List<CheckOutTableListBean> list = checkOutTableListBeanMap.get(projectName);
                paceNoBean.setCheckOutTableListBeanlist(list);
                int tableUnitCount = 0;
                for (CheckOutTableListBean checkOutTableListBean2 : list) {
                    tableUnitCount = tableUnitCount + checkOutTableListBean2.getTableUnitCount();
                }
                paceNoBean.setTableUnitCount(tableUnitCount);
                paceNoBeanList.add(paceNoBean);
                if (paceNoBeanBeanMap.containsKey(projectName)) {
                    List<PaceNoBean> list2 = paceNoBeanBeanMap.get(projectName);
                    list2.addAll(paceNoBeanList);
                    paceNoBeanBeanMap.put(projectName, list2);
                } else {
                    paceNoBeanBeanMap.put(projectName, paceNoBeanList);
                }
            }
        }
        return paceNoBeanBeanMap;
    }


    public static String parseName(Element element, String type) {
        Attribute attr = element.attribute(type);
        return attr == null ? null : attr.getValue();
    }

    /**
     * 获取套表名称
     *
     * @param TechincsXMLPath
     * @param tableType
     * @return
     * @author zhuhao
     * @date 2018-5-10
     */
    public static List<String> getXMLCheckOutTbaleNameList(String TechincsXMLPath, String tableType) {
        SAXReader sax = new SAXReader();
        List<String> tableNames = new ArrayList<String>();
        try {
            Document document = sax.read(new File(TechincsXMLPath));
            Element root = document.getRootElement();
            Map<String, String> map = new HashMap<String, String>();
            List<Element> elementList = root.selectNodes("QMFawTechnicsInfo/steps/QMProcedureInfo/paces/QMProcedureInfo/checkRecordTables/parameterTable");
            for (Element element : elementList) {
                String bzjlx_Value = parseName(element, "type");
                String tbm_Value = parseName(element, "tableName");
                if (tableType.equals(bzjlx_Value.trim())) {
                    map.put(tbm_Value, tbm_Value);
                }
            }
//			for (Element element : XmlUtil.getElementsByName(root, "QMFawTechnicsInfo")) {
//				for (Element temp : XmlUtil.getElementsByName(element, "steps")) {
//					for (Element temp1 : XmlUtil.getElementsByName(temp, "QMProcedureInfo")) {
//						for (Element temp2 : XmlUtil.getElementsByName(temp1, "paces")) {
//							for (Element temp3 : XmlUtil.getElementsByName(temp2, "QMProcedureInfo")) {
//								for (Element temp4 : XmlUtil.getElementsByName(temp3, "checkRecordTables")) {
//									for (Element temp5 : XmlUtil.getElementsByName(temp4, "parameterTable")) {
//										String bzjlx_Value = parseName(temp5, "type");
//										String tbm_Value = parseName(temp5, "tableName");
//										if(tableType.equals(bzjlx_Value.trim())){
//											map.put(tbm_Value, tbm_Value);
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
            Set<Entry<String, String>> entrySet = map.entrySet();
            for (Entry<String, String> entry : entrySet) {
                tableNames.add(entry.getKey());
            }
            Collections.sort(tableNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableNames;
    }

    public static List<String> getAllStepName(String TechincsXMLPath) {
        SAXReader sax = new SAXReader();
        List<String> stepNames = new ArrayList<String>();
        Document document = null;
        String stepNumber;
        String stepName;
        try {
            document = sax.read(new File(TechincsXMLPath));
            Element root = document.getRootElement();
            List<Element> stepElementList = root.selectNodes("QMFawTechnicsInfo/steps/QMProcedureInfo");
            for (Element stepElement : stepElementList) {
                stepNumber = stepElement.attributeValue("stepNumber");
                stepName = stepElement.attributeValue("stepName");
                stepNames.add(stepNumber + "_" + stepName);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return stepNames;
    }

    public static String Html2Text(String inputString) {
        if(inputString == null || "".equals(inputString)){
            return "";
        }
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;

        try {
            List<String> listHtmlS = new ArrayList<String>();
            // 定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>}
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

            // 定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>}
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";

            // 定义图片HTML标签的正则表达式
            String regEx_html = "<img src=[^>]+>";

            // 定义HTML标签的正则表达式
            String regEx_html2 = "<[^>]+>";

            listHtmlS.add(regEx_script);
            listHtmlS.add(regEx_style);
            listHtmlS.add("</*head[^>]*>");
            //listHtmlS.add("<[^<]*head>");
            listHtmlS.add("</*html[^>]*>");
            listHtmlS.add("</*body[^>]*>");
            //listHtmlS.add("</*p[^>]*>");
            listHtmlS.add("</*font[^>]*>");
            listHtmlS.add("</*span[^>]*>");
            listHtmlS.add("</*link[^>]*>");
            listHtmlS.add("</*meta[^>]*>");
            for (int i = 0; i < listHtmlS.size(); i++) {
                String htmlall = listHtmlS.get(i);
                p_html = Pattern.compile(htmlall, Pattern.CASE_INSENSITIVE);
                m_html = p_html.matcher(htmlStr);
                htmlStr = m_html.replaceAll(""); // 过滤html标签
            }
            textStr = htmlStr.trim();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return textStr;// 返回文本字符串
    }

    /**
     * 获取工艺文件XML路径
     *
     * @param technicsNumber
     * @param technicsVersion
     * @return
     * @author cjh
     * @date 2023-4-23
     */
    public static String getTechincsXMLPathByNumberAndVersion(String technicsNumber, String technicsVersion) {
        String pdfName = "";
        try {
            WTDocument document = null;
            if(technicsVersion == null || "".equals(technicsVersion)) {
                document = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
            } else {
                document = WTDocumentUtil.getDocumentByNumberAndAllVersion(technicsNumber, technicsVersion);
            }
            String ppnumber = IBAHelper.getIBAValue(document, "PPNUMBER");
            if(ppnumber != null && !"".equals(ppnumber)){
                pdfName = ppnumber + "_" + document.getIterationDisplayIdentifier().toString();
            }else{
                pdfName = technicsNumber + "_" + document.getIterationDisplayIdentifier().toString();
            }
            ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
            byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
            String technicsDirectory = createTechnicsDirectory(technicsNumber);
            TechnicsZipUtil.unZip(bytes, technicsDirectory, technicsDirectory);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return pdfName;
    }


    public static List<SummaryRecordBean> getPhotoRecords(String technicsXmlPath, String stepName) {
        String stepNumberLink = "";
        if(stepName != null){
            stepNumberLink = stepName.substring(0, stepName.indexOf("_"));
        }
        List<SummaryRecordBean> summaryRecordBeans = new ArrayList<SummaryRecordBean>();
        SummaryRecordBean summaryRecordBean;
        Document document = null;
        try {
            SAXReader sax = new SAXReader();
            document = sax.read(new File(technicsXmlPath));
            Element root = document.getRootElement();
            List<Element> stepElementList = root.selectNodes("QMFawTechnicsInfo/steps/QMProcedureInfo");
            for (Element stepElement : stepElementList) {
                String stepNumber = stepElement.attributeValue("stepNumber");
                if (stepNumber.equals(stepNumberLink) || stepName == null) {
                    List<Element> paceElementList = stepElement.selectNodes("paces/QMProcedureInfo");
                    for (Element paceElement : paceElementList) {
                        String paceNumber = paceElement.attributeValue("stepNumber");
                        List<Element> photoRecords = paceElement.selectNodes("photoRecords/photoRecord");
                        for (Element photoTable : photoRecords) {
                            String photoName = photoTable.attributeValue("photoName");
                            String psyq = photoTable.attributeValue("psyq");
                            String pbzz = photoTable.attributeValue("pbzz");
                            summaryRecordBean = new SummaryRecordBean();
                            summaryRecordBean.setStepNumber(stepNumber);
                            summaryRecordBean.setPaceNumber(paceNumber);
                            summaryRecordBean.setName(photoName);
                            summaryRecordBean.setPsyq(psyq);
                            summaryRecordBean.setPbzz(pbzz);

                            String localFileName = photoTable.attributeValue("localFileName");
                            String photoNumber = photoTable.attributeValue("photoNumber");
                            String photoVersion = photoTable.attributeValue("photoVersion");
                            String filePath = technicsXmlPath.substring(0, technicsXmlPath.lastIndexOf(File.separator));
                            String targetName = filePath + File.separator + "photoTemplate" + File.separator + photoNumber;
                            if("".equals(localFileName)){
                                File photoFile = new File(targetName);
                                if(!photoFile.exists()) {
                                    photoFile.mkdirs();
                                }
                                byte[] bytes = ProcessEditorToWCIntfRMI.getImageBytesByNumberAndVersion(photoNumber, photoVersion);
                                if(bytes!=null){
                                    String name = ProcessEditorToWCIntfRMI.getImageNameByNumberAndVersion(photoNumber, photoVersion);
                                    ByteArrayInputStream is = new ByteArrayInputStream(bytes);
                                    writeInputStreamToFile(is,targetName + File.separator + name);
                                    summaryRecordBean.setPhotoUrl(File.separator + photoNumber + File.separator + name);
                                }
                            }
                            summaryRecordBeans.add(summaryRecordBean);
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return summaryRecordBeans;
    }

    public static List<SummaryRecordBean> getBaiyuRecords(String technicsXmlPath, String stepName, String type) {
        String stepNumberLink = "";
        if(stepName != null){
            stepNumberLink = stepName.substring(0, stepName.indexOf("_"));
        }
        List<SummaryRecordBean> summaryRecordBeans = new ArrayList<SummaryRecordBean>();
        SummaryRecordBean summaryRecordBean;
        Document document = null;
        try {
            SAXReader sax = new SAXReader();
            document = sax.read(new File(technicsXmlPath));
            Element root = document.getRootElement();
            if(stepName == null){
                Element technics = root.element("QMFawTechnicsInfo");
                List<Element> tecBaiyuRecords = technics.selectNodes("schemaData/schemaInfo");
                for (Element element : tecBaiyuRecords) {
                    String number = element.attributeValue("id");
                    String name = element.attributeValue("name");
                    summaryRecordBean = new SummaryRecordBean();
                    summaryRecordBean.setStepNumber("");
                    summaryRecordBean.setPaceNumber("");
                    summaryRecordBean.setNumber(number);
                    summaryRecordBean.setName(name);
                    summaryRecordBeans.add(summaryRecordBean);
                }
                if("TEC".equals(type)){
                    return summaryRecordBeans;
                }
            }
            List<Element> stepElementList = root.selectNodes("QMFawTechnicsInfo/steps/QMProcedureInfo");
            for (Element stepElement : stepElementList) {
                String stepNumber = stepElement.attributeValue("stepNumber");
                if (stepNumber.equals(stepNumberLink) || stepName == null) {
                    List<Element> paceElementList = stepElement.selectNodes("paces/QMProcedureInfo");
                    for (Element paceElement : paceElementList) {
                        String paceNumber = paceElement.attributeValue("stepNumber");
                        List<Element> baiyuRecords = paceElement.selectNodes("schemaData/schemaInfo");
                        for (Element element : baiyuRecords) {
                            String number = element.attributeValue("id");
                            String name = element.attributeValue("name");
                            summaryRecordBean = new SummaryRecordBean();
                            summaryRecordBean.setStepNumber(stepNumber);
                            summaryRecordBean.setPaceNumber(paceNumber);
                            summaryRecordBean.setNumber(number);
                            summaryRecordBean.setName(name);
                            summaryRecordBeans.add(summaryRecordBean);
                        }
                    }
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return summaryRecordBeans;
    }

    /**
     * 获取工艺文件XML路径
     *
     * @param technicsNumber
     * @return
     * @author jyx
     * @date 2018-5-9
     */
    public static String getTechincsPdfName(String technicsNumber,String technicsVersion) {
        try {
            WTDocument document = null;
            if(technicsVersion == null || "".equals(technicsVersion)){
                document = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
            }else {
                document = WTDocumentUtil.getDocumentByNumberAndAllVersion(technicsNumber, technicsVersion);
            }
            ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
            byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
            String technicsDirectory = createTechnicsDirectory(technicsNumber);
            TechnicsZipUtil.unZip(bytes, technicsDirectory, technicsDirectory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 写文件
     *
     * @param is
     * @param destPath
     */
    public static void writeInputStreamToFile(InputStream is, String destPath) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(new FileOutputStream(destPath));
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = bis.read(b)) != -1) {
                bos.write(b, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
