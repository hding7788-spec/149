package ext.casc.report.technics;

import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.glaway.mpm.util.XmlUtility;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;
import ext.ases.changepackaged.ChangePackaged;
import ext.ases.changepackaged.ChangePackagedResultLink;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.ases.part.ASESHuiqianSignature;
import ext.casc.constants.Constants;
import ext.casc.integrate.util.BomUtil;
import ext.casc.mpm.process.*;
import ext.casc.part.CSCPart;
import ext.casc.part.SignatureHelper;
import ext.casc.process.ProcessTask;
import ext.casc.report.CldeInfoBean;
import ext.casc.report.SignatureAdviseBean;
import ext.casc.util.ExcelFileGenerator;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtility;
import ext.casc.util.Tools;
import ext.casc.workflow.signtrue.zp.HuiQianWorkFlowService;
import ext.ptc.ViewWIHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.Iterated;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.ConfigHelper;
import wt.vc.config.ConfigSpec;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;

import java.beans.PropertyVetoException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class DownloadTechnicsReportUtil {

    public static String templateDir;
    private static String tempDir;

    static {
        try {
            WTProperties pro = WTProperties.getLocalProperties();
            templateDir = pro.getProperty("wt.codebase.location") + File.separator + "ext" + File.separator + "casc" + File.separator + "report" + File.separator + "technics" + File.separator + "templates";

            tempDir = pro.getProperty("wt.temp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成产品工序工时定额汇总
     *
     * @param rootPart
     * @return File xls文件
     */
    public static File exportGongXuGongShiDingEHuiZong(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "gongxugongshi.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_产品工序工时定额汇总.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;

            //通过part获取所有关联的MPMProcessPlan对象

            Map<String, Map<String, String>> map = null;
            Map<String, String> gsMap = null;

            List<WTPart> allPart = new ArrayList<WTPart>();
            allPart.add(rootPart);
            getAllChildPart(rootPart, allPart);

            for (WTPart part : allPart) {
                //固定批次的pbom add by liangbo start 2017.03.22
                if (batch != null && !"".equals(batch)) {
                    part = getPartByBatch(part, batch);
                }
                // end
                String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
                if (!"自制件".equals(mtype)) {
                    continue;
                }

                map = getGSMap(part);

                System.out.println("------part----" + part.getNumber() + "  " + part.getViewName());

                List<WTDocument> document = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                if (document != null && !document.isEmpty()) {
                    for (WTDocument doc : document) {
                        Element techEle = BomUtil.getTechincisElement(doc, null, null);
                        String technicsName = doc.getName();
                        if (techEle != null) {
                            String number = techEle.attributeValue("technicsNumber");
                            if (map != null) {
                                gsMap = map.get(number);
                            }
                            if (gsMap == null) {
                                gsMap = new HashMap<String, String>();
                            }

                            Element stepsElement = techEle.element("steps");
                            if (stepsElement != null) {
                                List list = stepsElement.elements();
                                if (list != null && !list.isEmpty()) {
                                    for (Object object : list) {
                                        Element procedureEle = (Element) object;
                                        String stepNumber = procedureEle.attributeValue("stepNumber");

                                        int numberLength = 3 - stepNumber.length();
                                        for (int i = 0; i < numberLength; i++) {
                                            stepNumber = 0 + stepNumber;
                                        }

                                        String chejian = procedureEle.attributeValue("workShop");

                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品图号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //工艺文件名称
                                        writeCellValue(row, 3, technicsName);

                                        //工序号
                                        writeCellValue(row, 4, stepNumber);

                                        //工序名称
                                        writeCellValue(row, 5, procedureEle.attributeValue("stepName"));

                                        //车间
                                        writeCellValue(row, 6, chejian);

                                        String zjgs = gsMap.get(stepNumber + "_ZJGS");
                                        if (zjgs == null || "".equals(zjgs)) {
                                            zjgs = procedureEle.attributeValue("ZJGS");
                                        }

                                        String djgs = gsMap.get(stepNumber + "_DJGS");
                                        if (djgs == null || "".equals(djgs)) {
                                            djgs = procedureEle.attributeValue("DJGS");
                                        }
                                        //准结
                                        writeCellValue(row, 7, zjgs);

                                        //工时
                                        writeCellValue(row, 8, djgs);

                                        //状态
                                        writeCellValue(row, 14, doc.getState().getState().getDisplay(Locale.CHINA));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }

    public static File exportCaiLiaoXiaiHaoHuiZong(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "cailiaoxiaohao.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_产品材料消耗工艺定额汇总.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;

            List<WTPart> allPart = new ArrayList<WTPart>();
            //Map<String,Integer> tempbutaoNums = new HashMap<String,Integer>();
            TreeNode node = new TreeNode(null, rootPart.getNumber(), null);
            Map<String, TreeNode> treeNodes = new HashMap<String, TreeNode>();
            treeNodes.put(node.getNumber(), node);
            Map<String, Integer> butaoNums = new HashMap<String, Integer>();
            Map<String, Integer> tempbutaoNums = new HashMap<String, Integer>();
            tempbutaoNums.put(rootPart.getNumber(), 1);
            allPart.add(rootPart);
            //  TreeNode node = new TreeNode(null, rootPart.getNumber(), null);
            getAllChildPart(rootPart, allPart, tempbutaoNums, treeNodes);
            getButaoNums(butaoNums, treeNodes, tempbutaoNums);

            for (WTPart part : allPart) {
                //固定批次的pbom add by liangbo start 2017.03.22
                if (batch != null && !"".equals(batch)) {
                    part = getPartByBatch(part, batch);
                }
                // end
                String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
                if (!"自制件".equals(mtype)) {
                    continue;
                }

                System.out.println("------part----" + part.getNumber() + "  " + part.getViewName());
                List<WTDocument> document = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                if (document != null && !document.isEmpty()) {
                    for (WTDocument doc : document) {
                        Element techEle = BomUtil.getTechincisElement(doc, null, null);

//                List<Element> techList = BomUtil.getTechnicsDocumentByPart(part,null,null);
//                if(techList != null && !techList.isEmpty()) {
//        			for (Element techEle : techList) {
                        if (techEle != null) {
                            Element gyde = techEle.element("GYDE");
                            if (gyde != null) {
                                List<Element> zycldeList = gyde.selectNodes("ZYCLDE/zycldeRecord");
                                for (Element zyclde : zycldeList) {
                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品图号
                                    writeCellValue(row, 1, part.getNumber());

                                    //产品名称
                                    writeCellValue(row, 2, part.getName());

                                    //名称
                                    writeCellValue(row, 3, zyclde.attributeValue("chmc"));

                                    //牌号
                                    writeCellValue(row, 4, zyclde.attributeValue("xhph"));

                                    //规格
                                    writeCellValue(row, 5, zyclde.attributeValue("gg"));

                                    //技术条件
                                    writeCellValue(row, 6, zyclde.attributeValue("jstj"));

                                    //附加条件
                                    writeCellValue(row, 7, zyclde.attributeValue("fjtj"));

                                    //类型
                                    writeCellValue(row, 8, "主要材料");

                                    //可制件数
                                    writeCellValue(row, 9, "");

                                    //工艺定额
                                    writeCellValue(row, 10, zyclde.attributeValue("sl"));

                                    //单位
                                    writeCellValue(row, 11, zyclde.attributeValue("dw"));

                                    //状态
                                    writeCellValue(row, 12, doc.getState().getState().getDisplay(Locale.CHINA));

                                    //备注
                                    writeCellValue(row, 13, "");

                                    //套内数量
                                    writeCellValue(row, 14, butaoNums.get(part.getNumber()) + "");

                                    //主辅类型
                                    writeCellValue(row, 15, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                    //文件类型
                                    writeCellValue(row, 16, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                    //版本
                                    writeCellValue(row, 17, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                    //阶段标记
                                    writeCellValue(row, 18, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                }
                                List<Element> sjycldeList = gyde.selectNodes("SJYCLDE/sjycldeRecord");
                                for (Element sjyclde : sjycldeList) {
                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品图号
                                    writeCellValue(row, 1, part.getNumber());

                                    //产品名称
                                    writeCellValue(row, 2, part.getName());

                                    //名称
                                    writeCellValue(row, 3, sjyclde.attributeValue("chmc"));

                                    //牌号
                                    writeCellValue(row, 4, sjyclde.attributeValue("xhph"));

                                    //规格
                                    writeCellValue(row, 5, sjyclde.attributeValue("gg"));

                                    //技术条件
                                    writeCellValue(row, 6, sjyclde.attributeValue("jstj"));

                                    //附加条件
                                    writeCellValue(row, 7, sjyclde.attributeValue("fjtj"));

                                    //类型
                                    writeCellValue(row, 8, "试件原材料");

                                    //可制件数
                                    writeCellValue(row, 9, sjyclde.attributeValue("sjkzjs"));

                                    //工艺定额
                                    writeCellValue(row, 10, sjyclde.attributeValue("sjcc"));

                                    //单位
                                    writeCellValue(row, 11, sjyclde.attributeValue("dw"));

                                    //状态
                                    writeCellValue(row, 12, doc.getState().getState().getDisplay(Locale.CHINA));

                                    //备注
                                    writeCellValue(row, 13, "");

                                    //套内数量
                                    writeCellValue(row, 14, butaoNums.get(part.getNumber()) + "");

                                    //主辅类型
                                    writeCellValue(row, 15, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                    //文件类型
                                    writeCellValue(row, 16, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                    //版本
                                    writeCellValue(row, 17, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                    //阶段标记
                                    writeCellValue(row, 18, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                }
                            }
                            Element clde = techEle.element("CLDE");
                            if (clde != null) {
                                Element yclde = clde.element("YCLDE");
                                if (yclde != null) {
                                    List<Element> ycl = yclde.elements();
                                    if (ycl != null && !ycl.isEmpty()) {
                                        for (Element element : ycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //类型
                                            writeCellValue(row, 8, "原材料");

                                            //可制件数
                                            writeCellValue(row, 9, element.attributeValue("kzjs"));

                                            //工艺定额
                                            writeCellValue(row, 10, element.attributeValue("xlcc"));

                                            //单位
                                            writeCellValue(row, 11, element.attributeValue("dw"));

                                            //状态
                                            writeCellValue(row, 12, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //备注
                                            writeCellValue(row, 13, "");

                                            //套内数量
                                            writeCellValue(row, 14, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 15, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 17, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 18, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                                Element sjyclde = clde.element("SJYCLDE");
                                if (sjyclde != null) {
                                    List<Element> sjycl = sjyclde.elements();
                                    if (sjycl != null && !sjycl.isEmpty()) {
                                        for (Element element : sjycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //类型
                                            writeCellValue(row, 8, "试件原材料");

                                            //可制件数
                                            writeCellValue(row, 9, element.attributeValue("sjkzjs"));

                                            //工艺定额
                                            writeCellValue(row, 10, element.attributeValue("sjcc"));

                                            //单位
                                            writeCellValue(row, 11, element.attributeValue("dw"));

                                            //状态
                                            writeCellValue(row, 12, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //备注
                                            writeCellValue(row, 13, "");

                                            //套内数量
                                            writeCellValue(row, 14, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 15, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 17, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 18, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                                Element zyclde = clde.element("ZYCLDE");
                                if (zyclde != null) {
                                    List<Element> zycl = zyclde.elements();
                                    if (zycl != null && !zycl.isEmpty()) {
                                        for (Element element : zycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //类型
                                            writeCellValue(row, 8, "主要材料");

                                            //可制件数
                                            writeCellValue(row, 9, "");

                                            //工艺定额
                                            writeCellValue(row, 10, element.attributeValue("sl"));

                                            //单位
                                            writeCellValue(row, 11, element.attributeValue("dw"));

                                            //状态
                                            writeCellValue(row, 12, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //备注
                                            writeCellValue(row, 13, "");

                                            //套内数量
                                            writeCellValue(row, 14, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 15, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 17, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 18, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }


                                //数据来源于设计资源库
                                Element SJZYKyclde = clde.element("SJZYKYCLDE");
                                if (SJZYKyclde != null) {
                                    List<Element> ycl = SJZYKyclde.elements();
                                    if (ycl != null && !ycl.isEmpty()) {
                                        for (Element element : ycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("ph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //类型
                                            writeCellValue(row, 8, "原材料");

                                            //可制件数
                                            writeCellValue(row, 9, element.attributeValue("kzjs"));

                                            //工艺定额
                                            writeCellValue(row, 10, element.attributeValue("xlcc"));

                                            //单位
                                            writeCellValue(row, 11, element.attributeValue("dw"));

                                            //状态
                                            writeCellValue(row, 12, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //备注
                                            writeCellValue(row, 13, "");

                                            //套内数量
                                            writeCellValue(row, 14, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 15, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 17, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 18, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                                Element SJZYKsjyclde = clde.element("SJZYKSJYCLDE");
                                if (SJZYKsjyclde != null) {
                                    List<Element> sjycl = SJZYKsjyclde.elements();
                                    if (sjycl != null && !sjycl.isEmpty()) {
                                        for (Element element : sjycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("ph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //类型
                                            writeCellValue(row, 8, "试件原材料");

                                            //可制件数
                                            writeCellValue(row, 9, element.attributeValue("sjkzjs"));

                                            //工艺定额
                                            writeCellValue(row, 10, element.attributeValue("sjcc"));

                                            //单位
                                            writeCellValue(row, 11, element.attributeValue("dw"));

                                            //状态
                                            writeCellValue(row, 12, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //备注
                                            writeCellValue(row, 13, "");

                                            //套内数量
                                            writeCellValue(row, 14, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 15, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 17, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 18, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                                Element SJZYKzyclde = clde.element("SJZYKZYCLDE");
                                if (SJZYKzyclde != null) {
                                    List<Element> zycl = SJZYKzyclde.elements();
                                    if (zycl != null && !zycl.isEmpty()) {
                                        for (Element element : zycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("ph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //类型
                                            writeCellValue(row, 8, "主要材料");

                                            //可制件数
                                            writeCellValue(row, 9, "");

                                            //工艺定额
                                            writeCellValue(row, 10, element.attributeValue("sl"));

                                            //单位
                                            writeCellValue(row, 11, element.attributeValue("dw"));

                                            //状态
                                            writeCellValue(row, 12, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //备注
                                            writeCellValue(row, 13, "");

                                            //套内数量
                                            writeCellValue(row, 14, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 15, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 17, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 18, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                            }

                        }
                    }
                }
            }

            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }

    private static void getButaoNums(Map<String, Integer> butaoNums, Map<String, TreeNode> treeNodes, Map<String, Integer> tempbutaoNums) {
        Set<Entry<String, TreeNode>> set = treeNodes.entrySet();
        for (Entry<String, TreeNode> entry : set) {
            TreeNode node = entry.getValue();
            getAllGysl(node, butaoNums, tempbutaoNums, 1, node.getNumber());
        }

    }

    private static void getAllGysl(TreeNode node, Map<String, Integer> butaoNums, Map<String, Integer> tempbutaoNums, int preGysl, String nodeNumber) {
        if (node.getParents().isEmpty()) {

            if (butaoNums.get(nodeNumber) != null) {
                int otherGysl = butaoNums.get(nodeNumber);
                butaoNums.put(nodeNumber, otherGysl + preGysl);
            } else {
                butaoNums.put(nodeNumber, 1 * preGysl);
            }
        } else {
            for (TreeNode p : node.getParents()) {
                String key = p.getNumber() + "->" + node.getNumber();
                Integer tempgysl = tempbutaoNums.get(key);
                if (tempgysl != null) {
                    int gysl = tempgysl * preGysl;
                    getAllGysl(p, butaoNums, tempbutaoNums, gysl, nodeNumber);
                }


            }
        }

    }

    public static File exportFuLiaoXiaiHaoHuiZong(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "fuliaoxiaohao.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_产品辅料消耗工艺定额汇总.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;

            List<WTPart> allPart = new ArrayList<WTPart>();
            TreeNode node = new TreeNode(null, rootPart.getNumber(), null);
            Map<String, TreeNode> treeNodes = new HashMap<String, TreeNode>();
            treeNodes.put(node.getNumber(), node);
            Map<String, Integer> butaoNums = new HashMap<String, Integer>();
            Map<String, Integer> tempbutaoNums = new HashMap<String, Integer>();
            tempbutaoNums.put(rootPart.getNumber(), 1);
            allPart.add(rootPart);
            getAllChildPart(rootPart, allPart, tempbutaoNums, treeNodes);
            getButaoNums(butaoNums, treeNodes, tempbutaoNums);

            for (WTPart part : allPart) {
                //固定批次的pbom add by liangbo start 2017.03.22
                if (batch != null && !"".equals(batch)) {
                    part = getPartByBatch(part, batch);
                }
                // end
                String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
                if (!"自制件".equals(mtype)) {
                    continue;
                }

                System.out.println("------part----" + part.getNumber() + "  " + part.getViewName());

                List<WTDocument> document = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                if (document != null && !document.isEmpty()) {
                    for (WTDocument doc : document) {
                        Element techEle = BomUtil.getTechincisElement(doc, null, null);
//                List<Element> techList = BomUtil.getTechnicsDocumentByPart(part,null,null);
//                if(techList != null && !techList.isEmpty()) {
//        			for (Element techEle : techList) {
                        if (techEle != null) {
                            List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
                            //开始循环遍历所有工序
                            for (Element procedure : steps) {
                                //获取当前工序的所有工艺辅料元素
                                List<Element> mlist = procedure.selectNodes("materials/QMMaterialInfo");
                                if (mlist != null) {
                                    for (Element element : mlist) {
                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品图号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //名称
                                        writeCellValue(row, 3, element.attributeValue("materialName"));

                                        //型号
                                        writeCellValue(row, 4, element.attributeValue("mindex"));

                                        //规格
                                        writeCellValue(row, 5, element.attributeValue("csize"));

                                        //技术条件
                                        writeCellValue(row, 6, element.attributeValue("jstj"));

                                        //附加条件
                                        writeCellValue(row, 7, element.attributeValue("fjtj"));

                                        //定额数量
                                        writeCellValue(row, 8, element.attributeValue("sl"));

                                        //单位
                                        writeCellValue(row, 9, element.attributeValue("jldw"));

                                        //使用部门
                                        writeCellValue(row, 10, element.attributeValue("sycj"));

                                        //备注
                                        writeCellValue(row, 11, element.attributeValue("bz"));

                                        //状态
                                        writeCellValue(row, 12, doc.getState().getState().getDisplay(Locale.CHINA));

                                        //套内数量
                                        writeCellValue(row, 13, butaoNums.get(part.getNumber()) + "");

                                        //主辅类型
                                        writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                        //文件类型
                                        writeCellValue(row, 15, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                        //版本
                                        writeCellValue(row, 16, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                        //阶段标记
                                        writeCellValue(row, 17, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                    }
                                }

                                //获取当前工序的所有工步的工艺辅料元素
                                List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/materials/QMMaterialInfo");
                                for (int i = 0; i < stepList.size(); i++) {
                                    Element element = stepList.get(i);
                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品图号
                                    writeCellValue(row, 1, part.getNumber());

                                    //产品名称
                                    writeCellValue(row, 2, part.getName());

                                    //名称
                                    writeCellValue(row, 3, element.attributeValue("materialName"));

                                    //型号
                                    writeCellValue(row, 4, element.attributeValue("mindex"));

                                    //规格
                                    writeCellValue(row, 5, element.attributeValue("csize"));

                                    //技术条件
                                    writeCellValue(row, 6, element.attributeValue("jstj"));

                                    //附加条件
                                    writeCellValue(row, 7, element.attributeValue("fjtj"));

                                    //定额数量
                                    writeCellValue(row, 8, element.attributeValue("sl"));

                                    //单位
                                    writeCellValue(row, 9, element.attributeValue("jldw"));

                                    //使用部门
                                    writeCellValue(row, 10, element.attributeValue("sycj"));

                                    //备注
                                    writeCellValue(row, 11, element.attributeValue("bz"));

                                    //状态
                                    writeCellValue(row, 12, doc.getState().getState().getDisplay(Locale.CHINA));

                                    //套内数量
                                    writeCellValue(row, 13, butaoNums.get(part.getNumber()) + "");

                                    //主辅类型
                                    writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                    //文件类型
                                    writeCellValue(row, 15, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                    //版本
                                    writeCellValue(row, 16, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                    //阶段标记
                                    writeCellValue(row, 17, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                }
                            }
                        }
                    }
                }
            }

            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }

    public static File exportYuanQiJianXiaoHaoHuiZong(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "yuanqijian.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_产品元器件消耗工艺定额汇总.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;

//			Map<String,Integer> butaoNums = new HashMap<String,Integer>();
            TreeNode node = new TreeNode(null, rootPart.getNumber(), null);
            Map<String, TreeNode> treeNodes = new HashMap<String, TreeNode>();
            treeNodes.put(node.getNumber(), node);
            Map<String, Integer> butaoNums = new HashMap<String, Integer>();
            Map<String, Integer> tempbutaoNums = new HashMap<String, Integer>();
            tempbutaoNums.put(rootPart.getNumber(), 1);
            Map<WTPart, List<WTPart>> map = new HashMap<WTPart, List<WTPart>>();
            getAllChildPart2(rootPart, map, tempbutaoNums, treeNodes);
            getButaoNums(butaoNums, treeNodes, tempbutaoNums);

            List<WTPart> clist = null;
            Iterator<WTPart> ite = map.keySet().iterator();
            map.get(rootPart).add(rootPart);

            while (ite.hasNext()) {
                WTPart ppart = ite.next();

                clist = map.get(ppart);
                //不存在子part
                if (clist == null || clist.isEmpty()) {
                    continue;
                }
                for (WTPart part : clist) {
                    //固定批次的pbom add by liangbo start 2017.03.22
                    if (batch != null && !"".equals(batch)) {
                        part = getPartByBatch(part, batch);
                    }
                    // end
                    List<WTDocument> document = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                    if (document != null && !document.isEmpty()) {
                        for (WTDocument doc : document) {
                            Element techEle = BomUtil.getTechincisElement(doc, null, null);
                            if (techEle != null) {
                                List<Element> listEle = techEle.selectNodes("GYDE/MATCHPART/MatchPart");
                                if (listEle != null && !listEle.isEmpty()) {
                                    for (Element element : listEle) {
            		                	/*String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
            		                	if(!"元器件".equals(mtype)) {
            		                		continue;
            		                	}*/
                                        String wzlb = element.attributeValue("wzlb");//01元器件、02标准件

                                        if (wzlb == null) {
                                            continue;
                                        }
                                        if (wzlb != null && wzlb.startsWith("01")) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品编号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //精度等级
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //定额数量
                                            writeCellValue(row, 8, element.attributeValue("gyCount"));

                                            String dw = element.attributeValue("dw2");
                                            if(Tools.isNull(dw)){
                                                dw = element.attributeValue("dw");
                                            }
                                            //单位
                                            writeCellValue(row, 9,dw);

                                            //备注
                                            writeCellValue(row, 10, "");

                                            //状态
                                            writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //套内数量
                                            writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                                listEle = techEle.selectNodes("GYDE/SJZYKMATCHPART/SjzykMatchPart");
                                if (listEle != null && !listEle.isEmpty()) {
                                    for (Element element : listEle) {
                                        String dataType = element.attributeValue("dataType");
                                        if (dataType == null || !"元器件".equals(dataType)) {
                                            continue;
                                        }

                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品编号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //名称
                                        writeCellValue(row, 3, element.attributeValue("name"));

                                        //牌号
                                        writeCellValue(row, 4, element.attributeValue("ph"));

                                        //规格
                                        writeCellValue(row, 5, element.attributeValue("gg"));

                                        //技术条件
                                        writeCellValue(row, 6, element.attributeValue("bzh"));

                                        //精度等级
                                        writeCellValue(row, 7, element.attributeValue("jddj"));

                                        String sl = element.attributeValue("gysl");
                                        if(Tools.isNull(sl)){
                                            sl = element.attributeValue("sl");
                                        }
                                        //定额数量
                                        writeCellValue(row, 8, sl);

                                        //单位
                                        writeCellValue(row, 9, element.attributeValue("dw"));

                                        //备注
                                        writeCellValue(row, 10, "");

                                        //状态
                                        writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                        //套内数量
                                        writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                        //主辅类型
                                        writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                        //文件类型
                                        writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                        //版本
                                        writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                        //阶段标记
                                        writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                    }
                                }

                                listEle = techEle.selectNodes("GYDE/NEWPART/NewPart");
                                if (listEle != null && !listEle.isEmpty()) {
                                    for (Element element : listEle) {
            		                	/*String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
            		                	if(!"元器件".equals(mtype)) {
            		                		continue;
            		                	}*/
                                        String wzlb = element.attributeValue("wzlb");//01元器件、02标准件

                                        if (wzlb == null) {
                                            continue;
                                        }
                                        if (wzlb != null && wzlb.startsWith("01")) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品编号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //精度等级
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //定额数量
                                            writeCellValue(row, 8, element.attributeValue("sl"));

                                            String dw = element.attributeValue("dw2");
                                            if(Tools.isNull(dw)){
                                                dw = element.attributeValue("dw");
                                            }
                                            //单位
                                            writeCellValue(row, 9, dw);

                                            //备注
                                            writeCellValue(row, 10, "");

                                            //状态
                                            writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //套内数量
                                            writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }
                                listEle = techEle.selectNodes("GYDE/SJZYKNEWPART/SjzykNewPart");
                                if (listEle != null && !listEle.isEmpty()) {
                                    for (Element element : listEle) {
                                        String dataType = element.attributeValue("dataType");
                                        if (dataType == null || !"元器件".equals(dataType)) {
                                            continue;
                                        }

                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品编号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //名称
                                        writeCellValue(row, 3, element.attributeValue("name"));

                                        //牌号
                                        writeCellValue(row, 4, element.attributeValue("ph"));

                                        //规格
                                        writeCellValue(row, 5, element.attributeValue("gg"));

                                        //技术条件
                                        writeCellValue(row, 6, element.attributeValue("bzh"));

                                        //精度等级
                                        writeCellValue(row, 7, element.attributeValue("jddj"));
                                        String sl = element.attributeValue("gysl");
                                        if(Tools.isNull(sl)){
                                            sl = element.attributeValue("sl");
                                        }
                                        //定额数量
                                        writeCellValue(row, 8, sl);

                                        //单位
                                        writeCellValue(row, 9, element.attributeValue("dw"));

                                        //备注
                                        writeCellValue(row, 10, "");

                                        //状态
                                        writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                        //套内数量
                                        writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                        //主辅类型
                                        writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                        //文件类型
                                        writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                        //版本
                                        writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                        //阶段标记
                                        writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                    }
                                }
                            }
                        }
                    }
                }
            }


            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }


    public static File exportBiaoZhunJianXiaoHaoHuiZong(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "biaozhunjian.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_产品标准件消耗工艺定额汇总.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;
            TreeNode node = new TreeNode(null, rootPart.getNumber(), null);
            Map<String, TreeNode> treeNodes = new HashMap<String, TreeNode>();
            treeNodes.put(node.getNumber(), node);
            Map<String, Integer> butaoNums = new HashMap<String, Integer>();
            Map<String, Integer> tempbutaoNums = new HashMap<String, Integer>();
            tempbutaoNums.put(rootPart.getNumber(), 1);
            Map<WTPart, List<WTPart>> map = new HashMap<WTPart, List<WTPart>>();
            getAllChildPart2(rootPart, map, tempbutaoNums, treeNodes);
            getButaoNums(butaoNums, treeNodes, tempbutaoNums);
            map.get(rootPart).add(rootPart);
            List<WTPart> clist = null;
            Iterator<WTPart> ite = map.keySet().iterator();
            while (ite.hasNext()) {
                WTPart ppart = ite.next();

                clist = map.get(ppart);
                //不存在子part
                if (clist == null || clist.isEmpty()) {
                    continue;
                }

//            	List<Element> techList = BomUtil.getZTechnicsDocumentByPart(ppart, "FORMAL", "PRIMARY");
//                if(techList != null && !techList.isEmpty()) {
//        			for (Element techEle : techList) {
//        				if(techEle != null) {
//        					List<Element> listEle = techEle.selectNodes("GYDE/MATCHPART/MatchPart");
//        					if(listEle != null && !listEle.isEmpty()) {
//        						String partNumber = "";
//        						for (Element element : listEle) {
//        							tmap = new HashMap<String,String>();
//        							partNumber = element.attributeValue("number");
//        							tmap.put("chmc", element.attributeValue("chmc"));
//        							tmap.put("xhph", element.attributeValue("xhph"));
//        							tmap.put("gg", element.attributeValue("gg"));
//        							tmap.put("jstj", element.attributeValue("jstj"));
//        							tmap.put("fjtj", element.attributeValue("fjtj"));
//        							tmap.put("gyCount", element.attributeValue("gyCount"));
//        							tmap.put("dw2", element.attributeValue("dw2"));
//
//        							mmap.put(partNumber, tmap);
//								}
//        					}
//        				}
//        			}
//                }

                for (WTPart part : clist) {
                    //固定批次的pbom add by liangbo start 2017.03.22
                    if (batch != null && !"".equals(batch)) {
                        part = getPartByBatch(part, batch);
                    }
                    // end
                    List<WTDocument> document = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                    if (document != null && !document.isEmpty()) {
                        for (WTDocument doc : document) {
                            Element techEle = BomUtil.getTechincisElement(doc, null, null);////去掉，输出所有工艺
                            if (techEle != null) {
                                List<Element> listEle = techEle.selectNodes("GYDE/MATCHPART/MatchPart");
                                if (listEle != null && !listEle.isEmpty()) {
                                    for (Element element : listEle) {
                                        /*String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");*/
                                        String wzlb = element.attributeValue("wzlb");
                                        if (wzlb == null) {
                                            continue;
                                        }

                                        if (wzlb.startsWith("02")) {

                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品编号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //精度等级
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //定额数量
                                            writeCellValue(row, 8, element.attributeValue("gyCount"));

                                            String dw = element.attributeValue("dw2");
                                            if(Tools.isNull(dw)){
                                                dw = element.attributeValue("dw");
                                            }
                                            //单位
                                            writeCellValue(row, 9, dw);
                                            //备注
                                            writeCellValue(row, 10, "");

                                            //状态
                                            writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //套内数量
                                            writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                                listEle = techEle.selectNodes("GYDE/SJZYKMATCHPART/SjzykMatchPart");
                                if (listEle != null && !listEle.isEmpty()) {
                                    for (Element element : listEle) {
                                        /*String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");*/
                                        String dataType = element.attributeValue("dataType");
                                        if (dataType == null || !"标准件".equals(dataType)) {
                                            continue;
                                        }


                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品编号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //名称
                                        writeCellValue(row, 3, element.attributeValue("name"));

                                        //牌号
                                        writeCellValue(row, 4, element.attributeValue("ph"));

                                        //规格
                                        writeCellValue(row, 5, element.attributeValue("gg"));

                                        //技术条件
                                        writeCellValue(row, 6, element.attributeValue("bzh"));

                                        //精度等级
                                        writeCellValue(row, 7, element.attributeValue("jddj"));
                                        String sl = element.attributeValue("gysl");
                                        if(Tools.isNull(sl)){
                                            sl = element.attributeValue("sl");
                                        }
                                        //定额数量
                                        writeCellValue(row, 8, sl);

                                        //单位
                                        writeCellValue(row, 9, element.attributeValue("dw"));
                                        //备注
                                        writeCellValue(row, 10, "");

                                        //状态
                                        writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                        //套内数量
                                        writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                        //主辅类型
                                        writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                        //文件类型
                                        writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                        //版本
                                        writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                        //阶段标记
                                        writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                    }
                                }


                                listEle = techEle.selectNodes("GYDE/NEWPART/NewPart");
                                if (listEle != null && !listEle.isEmpty()) {
                                    for (Element element : listEle) {
                                        /*String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");*/
                                        String wzlb = element.attributeValue("wzlb");
                                        if (wzlb == null) {
                                            continue;
                                        }

                                        if (wzlb.startsWith("02")) {

                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品编号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //精度等级
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //定额数量
                                            writeCellValue(row, 8, element.attributeValue("sl"));

                                            String dw = element.attributeValue("dw2");
                                            if(Tools.isNull(dw)){
                                                dw = element.attributeValue("dw");
                                            }
                                            //单位
                                            writeCellValue(row, 9, dw);
                                            //备注
                                            writeCellValue(row, 10, "");

                                            //状态
                                            writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //套内数量
                                            writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                                listEle = techEle.selectNodes("GYDE/SJZYKNEWPART/SjzykNewPart");
                                if (listEle != null && !listEle.isEmpty()) {
                                    for (Element element : listEle) {
                                        /*String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");*/
                                        String dataType = element.attributeValue("dataType");
                                        if (dataType == null || !"标准件".equals(dataType)) {
                                            continue;
                                        }


                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品编号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //名称
                                        writeCellValue(row, 3, element.attributeValue("name"));

                                        //牌号
                                        writeCellValue(row, 4, element.attributeValue("ph"));

                                        //规格
                                        writeCellValue(row, 5, element.attributeValue("gg"));

                                        //技术条件
                                        writeCellValue(row, 6, element.attributeValue("bzh"));

                                        //精度等级
                                        writeCellValue(row, 7, element.attributeValue("jddj"));

                                        String sl = element.attributeValue("gysl");
                                        if(Tools.isNull(sl)){
                                            sl = element.attributeValue("sl");
                                        }
                                        //定额数量
                                        writeCellValue(row, 8, sl);

                                        //单位
                                        writeCellValue(row, 9, element.attributeValue("dw"));
                                        //备注
                                        writeCellValue(row, 10, "");

                                        //状态
                                        writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                        //套内数量
                                        writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                        //主辅类型
                                        writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                        //文件类型
                                        writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                        //版本
                                        writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                        //阶段标记
                                        writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                    }
                                }
                            }
                        }
                    }
                }
            }
            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }

    public static File exportWaiGouJianHuiZong(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "waigoujian.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_产品外购件消耗工艺定额汇总.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;
            TreeNode node = new TreeNode(null, rootPart.getNumber(), null);
            Map<String, TreeNode> treeNodes = new HashMap<String, TreeNode>();
            treeNodes.put(node.getNumber(), node);
            Map<String, Integer> butaoNums = new HashMap<String, Integer>();
            Map<String, Integer> tempbutaoNums = new HashMap<String, Integer>();
            tempbutaoNums.put(rootPart.getNumber(), 1);
            Map<WTPart, List<WTPart>> map = new HashMap<WTPart, List<WTPart>>();
            getAllChildPart2(rootPart, map, tempbutaoNums, treeNodes);
            getButaoNums(butaoNums, treeNodes, tempbutaoNums);
            map.get(rootPart).add(rootPart);
            Map<String, Map<String, String>> mmap = new HashMap<String, Map<String, String>>();
            Map<String, String> tmap = null;
            List<WTPart> clist = null;
            Iterator<WTPart> ite = map.keySet().iterator();
            while (ite.hasNext()) {
                WTPart ppart = ite.next();

                clist = map.get(ppart);
                //不存在子part
                if (clist == null || clist.isEmpty()) {
                    continue;
                }
                //固定批次的pbom add by liangbo start 2017.03.22
                if (batch != null && !"".equals(batch)) {
                    ppart = getPartByBatch(ppart, batch);
                }
                // end
                List<WTDocument> document = BomUtil.getAllWTDocumentByAllSameVersionViewPart(ppart);
                if (document != null && !document.isEmpty()) {
                    for (WTDocument doc : document) {
                        Element techEle = BomUtil.getTechincisElement(doc, null, null);
                        if (techEle != null) {
                            List<Element> listEle = techEle.selectNodes("GYDE/MATCHPART/MatchPart");
                            if (listEle != null && !listEle.isEmpty()) {
                                String partNumber = "";
                                for (Element element : listEle) {
                                    tmap = new HashMap<String, String>();
                                    partNumber = element.attributeValue("number");
                                    tmap.put("chmc", element.attributeValue("chmc"));
                                    tmap.put("xhph", element.attributeValue("xhph"));
                                    tmap.put("gg", element.attributeValue("gg"));
                                    tmap.put("jstj", element.attributeValue("jstj"));
                                    tmap.put("gyCount", element.attributeValue("gyCount"));

                                    mmap.put(partNumber, tmap);
                                }
                            }

                            listEle = techEle.selectNodes("GYDE/SJZYKMATCHPART/SjzykMatchPart");
                            if (listEle != null && !listEle.isEmpty()) {
                                String partNumber = "";
                                for (Element element : listEle) {
                                    tmap = new HashMap<String, String>();
                                    partNumber = element.attributeValue("sjbm");
                                    tmap.put("chmc", element.attributeValue("name"));
                                    tmap.put("xhph", element.attributeValue("ph"));
                                    tmap.put("gg", element.attributeValue("gg"));
                                    tmap.put("jstj", element.attributeValue("jstj"));
                                    String sl = element.attributeValue("gysl");
                                    if(Tools.isNull(sl)){
                                        sl = element.attributeValue("sl");
                                    }
                                    tmap.put("gyCount",sl);

                                    mmap.put(partNumber, tmap);
                                }
                            }


                            List<Element> newPart = techEle.selectNodes("GYDE/NEWPART/NewPart");
                            if (newPart != null && !newPart.isEmpty()) {
                                for (Element element : newPart) {
                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品编号
                                    writeCellValue(row, 1, element.attributeValue("number"));

                                    //产品名称
                                    writeCellValue(row, 2, element.attributeValue("chmc"));

                                    //牌号
                                    writeCellValue(row, 3, element.attributeValue("xhph"));

                                    //规格
                                    writeCellValue(row, 4, element.attributeValue("gg"));

                                    //技术条件
                                    writeCellValue(row, 5, element.attributeValue("jstj"));

                                    //使用数量
                                    writeCellValue(row, 6, element.attributeValue("sl"));

                                    //技术协议
                                    writeCellValue(row, 7, "");

                                    //类别
                                    writeCellValue(row, 8, getType(element.attributeValue("wzlb")));

                                    //备注
                                    writeCellValue(row, 9, "");

                                    //状态
                                    writeCellValue(row, 10, doc.getState().getState().getDisplay(Locale.CHINA));

                                    //套内数量
                                    writeCellValue(row, 11, butaoNums.get(ppart.getNumber()) + "");

                                    //主辅类型
                                    writeCellValue(row, 12, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                    //文件类型
                                    writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                    //版本
                                    writeCellValue(row, 14, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                    //阶段标记
                                    writeCellValue(row, 15, element.attributeValue("PHASE_CODE"));
                                }
                            }

                            newPart = techEle.selectNodes("GYDE/SJZYKNEWPART/SjzykNewPart");
                            if (newPart != null && !newPart.isEmpty()) {
                                for (Element element : newPart) {
                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品编号
                                    writeCellValue(row, 1, element.attributeValue("sjbm"));

                                    //产品名称
                                    writeCellValue(row, 2, element.attributeValue("name"));

                                    //牌号
                                    writeCellValue(row, 3, element.attributeValue("ph"));

                                    //规格
                                    writeCellValue(row, 4, element.attributeValue("gg"));

                                    //技术条件
                                    writeCellValue(row, 5, element.attributeValue("bzh"));

                                    String sl = element.attributeValue("gysl");
                                    if(Tools.isNull(sl)){
                                        sl = element.attributeValue("sl");
                                    }
                                    //使用数量
                                    writeCellValue(row, 6,sl);

                                    //技术协议
                                    writeCellValue(row, 7, "");

                                    //类别
                                    writeCellValue(row, 8, element.attributeValue("dataType"));

                                    //备注
                                    writeCellValue(row, 9, "");

                                    //状态
                                    writeCellValue(row, 10, doc.getState().getState().getDisplay(Locale.CHINA));

                                    //套内数量
                                    writeCellValue(row, 11, butaoNums.get(ppart.getNumber()) + "");

                                    //主辅类型
                                    writeCellValue(row, 12, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                    //文件类型
                                    writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                    //版本
                                    writeCellValue(row, 14, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                    //阶段标记
                                    writeCellValue(row, 15, element.attributeValue("PHASE_CODE"));
                                }
                            }
                        }
                    }
                }

                for (WTPart part : clist) {
                    String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
                    if ("外购件".equals(mtype)) {//不带料委外零件信息输出到Excel、外购件匹配信息输出到Excel
                        //外购件/不带料委外件不允许编工艺，故应该改为:"外购件".equals(mtype) && "不带料委外件".equals(mtype)
                        continue;
                    }
                    //add by zhuhao 2017.6.14
                    if ("不带料委外件".equals(mtype)) {
                        //不带料委外零件信息输出到Excel
                        row = sheet.createRow(index++);
                        //序号
                        writeCellValue(row, 0, String.valueOf(n++));

                        //产品图号
                        writeCellValue(row, 1, part.getNumber());

                        //产品名称
                        writeCellValue(row, 2, part.getName());

                        //牌号
                        writeCellValue(row, 3, IBAHelper.getIBAStringValue(part, "MINDEX"));

                        //规格
                        writeCellValue(row, 4, IBAHelper.getIBAStringValue(part, "STANDARD"));

                        //技术条件
                        writeCellValue(row, 5, "");

                        //使用数量?
                        writeCellValue(row, 6, "");

                        //技术协议
                        writeCellValue(row, 7, "");

                        //类别
                        writeCellValue(row, 8, IBAHelper.getIBAStringValue(part, "MTYPE"));

                        //备注
                        writeCellValue(row, 9, "");

                        //状态
                        writeCellValue(row, 10, part.getState().getState().getDisplay(Locale.CHINA));

                        //套内数量
                        writeCellValue(row, 11, butaoNums.get(part.getNumber()) + "");

                        //主辅类型
                        writeCellValue(row, 12, "");

                        //文件类型
                        writeCellValue(row, 13, "");

                        //版本
                        writeCellValue(row, 14, part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());

                        //阶段标记
                        writeCellValue(row, 15, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                    }
                    List<WTDocument> document1 = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                    if (document1 != null && !document1.isEmpty()) {
                        for (WTDocument doc : document1) {


                            Element techEle = BomUtil.getTechincisElement(doc, null, null);
                            List<Element> listEle = techEle.selectNodes("GYDE/MATCHPART/MatchPart");
                            if (listEle != null && !listEle.isEmpty()) {
                                for (Element element : listEle) {

                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品编号
                                    writeCellValue(row, 1, element.attributeValue("number"));

                                    //产品名称
                                    writeCellValue(row, 2, element.attributeValue("chmc"));

                                    //牌号
                                    writeCellValue(row, 3, element.attributeValue("xhph"));

                                    //规格
                                    writeCellValue(row, 4, element.attributeValue("gg"));

                                    //技术条件
                                    writeCellValue(row, 5, element.attributeValue("jstj"));

                                    //使用数量
                                    writeCellValue(row, 6, element.attributeValue("sl"));

                                    //技术协议
                                    writeCellValue(row, 7, "");

                                    //类别
                                    writeCellValue(row, 8, getType(element.attributeValue("wzlb")));

                                    //备注
                                    writeCellValue(row, 9, "");

                                    //状态
                                    writeCellValue(row, 10, doc.getState().getState().getDisplay(Locale.CHINA));

                                    //套内数量
                                    writeCellValue(row, 11, butaoNums.get(part.getNumber()) + "");

                                    //主辅类型
                                    writeCellValue(row, 12, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                    //文件类型
                                    writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                    //版本
                                    writeCellValue(row, 14, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                    //阶段标记
                                    writeCellValue(row, 15, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                }
                            }
                            listEle = techEle.selectNodes("GYDE/SJZYKMATCHPART/SjzykMatchPart");
                            if (listEle != null && !listEle.isEmpty()) {
                                for (Element element : listEle) {

                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品编号
                                    writeCellValue(row, 1, element.attributeValue("sjbm"));

                                    //产品名称
                                    writeCellValue(row, 2, element.attributeValue("name"));

                                    //牌号
                                    writeCellValue(row, 3, element.attributeValue("ph"));

                                    //规格
                                    writeCellValue(row, 4, element.attributeValue("gg"));

                                    //技术条件
                                    writeCellValue(row, 5, element.attributeValue("bzh"));

                                    String sl = element.attributeValue("gysl");
                                    if(Tools.isNull(sl)){
                                        sl = element.attributeValue("sl");
                                    }
                                    //使用数量
                                    writeCellValue(row, 6, sl);

                                    //技术协议
                                    writeCellValue(row, 7, "");

                                    //类别
                                    writeCellValue(row, 8, element.attributeValue("dataType"));

                                    //备注
                                    writeCellValue(row, 9, "");

                                    //状态
                                    writeCellValue(row, 10, doc.getState().getState().getDisplay(Locale.CHINA));

                                    //套内数量
                                    writeCellValue(row, 11, butaoNums.get(part.getNumber()) + "");

                                    //主辅类型
                                    writeCellValue(row, 12, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                    //文件类型
                                    writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                    //版本
                                    writeCellValue(row, 14, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                    //阶段标记
                                    writeCellValue(row, 15, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                }
                            }
                        }
                    }
                }
            }
            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }


    public static File exportWaiXieJianHuiZong(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "waixiejian.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_产品外协件消耗工艺定额汇总.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;

            List<WTPart> allPart = new ArrayList<WTPart>();
            TreeNode node = new TreeNode(null, rootPart.getNumber(), null);

            Map<String, TreeNode> treeNodes = new HashMap<String, TreeNode>();
            treeNodes.put(node.getNumber(), node);

            Map<String, Integer> butaoNums = new HashMap<String, Integer>();
            Map<String, Integer> tempbutaoNums = new HashMap<String, Integer>();
            tempbutaoNums.put(rootPart.getNumber(), 1);
            allPart.add(rootPart);
            getAllChildPart(rootPart, allPart, tempbutaoNums, treeNodes);
            getButaoNums(butaoNums, treeNodes, tempbutaoNums);

            for (WTPart part : allPart) {

                String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
                if (!"带料委外件".equals(mtype)) {
                    continue;
                }
                //固定批次的pbom add by liangbo start 2017.03.22
                if (batch != null && !"".equals(batch)) {
                    part = getPartByBatch(part, batch);
                }
                // end
                //List<WTDocument> document1 = BomUtil.getWTDocumentByPart(part,null,null);
                List<WTDocument> document1 = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                if (document1 != null && !document1.isEmpty()) {
                    for (WTDocument doc : document1) {
                        Element techEle = BomUtil.getTechincisElement(doc, null, null);
                        if (techEle != null) {
                            String jsxy = techEle.attributeValue("jsxy");
                            Element clde = techEle.element("CLDE");
                            if (clde != null) {
                                Element yclde = clde.element("YCLDE");
                                if (yclde != null) {
                                    List<Element> ycl = yclde.elements();
                                    if (ycl != null && !ycl.isEmpty()) {
                                        for (Element element : ycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //使用数量
                                            writeCellValue(row, 8, "");

                                            //技术协议
                                            writeCellValue(row, 9, jsxy);//

                                            //备注
                                            writeCellValue(row, 10, element.attributeValue("comment"));

                                            //状态
                                            writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //套内数量
                                            writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                                Element sjyclde = clde.element("SJYCLDE");
                                if (sjyclde != null) {
                                    List<Element> sjycl = sjyclde.elements();
                                    if (sjycl != null && !sjycl.isEmpty()) {
                                        for (Element element : sjycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //使用数量
                                            writeCellValue(row, 8, "");

                                            //技术协议
                                            writeCellValue(row, 9, jsxy);//

                                            //备注
                                            writeCellValue(row, 10, element.attributeValue("comment"));

                                            //状态
                                            writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //套内数量
                                            writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                                Element zyclde = clde.element("ZYCLDE");
                                if (zyclde != null) {
                                    List<Element> zycl = zyclde.elements();
                                    if (zycl != null && !zycl.isEmpty()) {
                                        for (Element element : zycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //使用数量
                                            writeCellValue(row, 8, element.attributeValue("sl"));

                                            //技术协议
                                            writeCellValue(row, 9, jsxy);//

                                            //备注
                                            writeCellValue(row, 10, element.attributeValue("comment"));

                                            //状态
                                            writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //套内数量
                                            writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }


                                //数据来源于设计资源库
                                Element SJZYKyclde = clde.element("SJZYKYCLDE");
                                if (SJZYKyclde != null) {
                                    List<Element> ycl = SJZYKyclde.elements();
                                    if (ycl != null && !ycl.isEmpty()) {
                                        for (Element element : ycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("ph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("cybz"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));


                                            //使用数量
                                            writeCellValue(row, 8, element.attributeValue("sl"));

                                            //技术协议
                                            writeCellValue(row, 9, jsxy);//

                                            //备注
                                            writeCellValue(row, 10, element.attributeValue("comment"));

                                            //状态
                                            writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //套内数量
                                            writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }

                                Element SJZYKsjyclde = clde.element("SJZYKSJYCLDE");
                                if (SJZYKsjyclde != null) {
                                    List<Element> sjycl = SJZYKsjyclde.elements();
                                    if (sjycl != null && !sjycl.isEmpty()) {
                                        for (Element element : sjycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("ph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("cybz"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //使用数量
                                            writeCellValue(row, 8, element.attributeValue("sl"));

                                            //技术协议
                                            writeCellValue(row, 9, jsxy);//

                                            //备注
                                            writeCellValue(row, 10, element.attributeValue("comment"));

                                            //状态
                                            writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //套内数量
                                            writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //版本
                                            writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //阶段标记
                                            writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        }
                                    }
                                }
                            }

                            Element SJZYKzyclde = clde.element("SJZYKZYCLDE");
                            if (SJZYKzyclde != null) {
                                List<Element> zycl = SJZYKzyclde.elements();
                                if (zycl != null && !zycl.isEmpty()) {
                                    for (Element element : zycl) {
                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品图号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //名称
                                        writeCellValue(row, 3, element.attributeValue("name"));

                                        //牌号
                                        writeCellValue(row, 4, element.attributeValue("ph"));

                                        //规格
                                        writeCellValue(row, 5, element.attributeValue("gg"));

                                        //技术条件
                                        writeCellValue(row, 6, element.attributeValue("cybz"));

                                        //附加条件
                                        writeCellValue(row, 7, element.attributeValue("fjtj"));


                                        //使用数量
                                        writeCellValue(row, 8, element.attributeValue("sl"));

                                        //技术协议
                                        writeCellValue(row, 9, jsxy);//

                                        //备注
                                        writeCellValue(row, 10, element.attributeValue("comment"));

                                        //状态
                                        writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));

                                        //套内数量
                                        writeCellValue(row, 12, butaoNums.get(part.getNumber()) + "");

                                        //主辅类型
                                        writeCellValue(row, 13, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                        //文件类型
                                        writeCellValue(row, 14, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                        //版本
                                        writeCellValue(row, 15, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                        //阶段标记
                                        writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                    }
                                }
                            }
                        }

                    }

                }

            }

            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }

    public static File exportGongyizhuangbeihuizong(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "gongyizhuangbei.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_产品所用工艺装备汇总.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;

            List<WTPart> allPart = new ArrayList<WTPart>();
            allPart.add(rootPart);
            getAllChildPart(rootPart, allPart);

            for (WTPart part : allPart) {
                String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
                if (!"自制件".equals(mtype)) {
                    continue;
                }
                //固定批次的pbom add by liangbo start 2017.03.22
                if (batch != null && !"".equals(batch)) {
                    part = getPartByBatch(part, batch);
                }
                // end
                System.out.println("------part----" + part.getNumber() + "  " + part.getViewName());

                // List<WTDocument> document = BomUtil.getWTDocumentByPart(part,null,null);
                List<WTDocument> document = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                if (document != null && !document.isEmpty()) {
                    for (WTDocument doc : document) {
                        Element techEle = BomUtil.getTechincisElement(doc, null, null);
                        if (techEle == null) continue;
                        List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
                        //开始循环遍历所有工序
                        for (Element procedure : steps) {
                            //获取当前工序的所有工艺辅料元素
                            List<Element> mlist = procedure.selectNodes("tools/QMToolInfo");
                            System.out.println("------------------------------->>>>" + mlist);
                            if (mlist != null) {
                                for (Element element : mlist) {
                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品图号
                                    writeCellValue(row, 1, part.getNumber());

                                    //产品名称
                                    writeCellValue(row, 2, part.getName());

                                    //工装编号
                                    writeCellValue(row, 3, element.attributeValue("toolNum"));

                                    //工装名称
                                    writeCellValue(row, 4, element.attributeValue("toolName"));

                                    //工装类别
                                    writeCellValue(row, 5, element.attributeValue("frockType"));

                                    //型号
                                    writeCellValue(row, 6, element.attributeValue("toolSpec"));

                                    //规格
                                    writeCellValue(row, 7, element.attributeValue("csize"));

                                    //使用数量
                                    writeCellValue(row, 8, "1");

                                    //使用部门
                                    writeCellValue(row, 9, procedure.attributeValue("workShop"));
                                    //
                                    //状态
                                    //WTDocument document = null;
                                    //document.getState().getState().getDisplay(Locale.CHINA);
                                    writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));
                                }
                            }

                            //获取当前工序的所有工步的工艺辅料元素
                            List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/tools/QMToolInfo");
                            for (int i = 0; i < stepList.size(); i++) {
                                Element element = stepList.get(i);
                                row = sheet.createRow(index++);
                                //序号
                                writeCellValue(row, 0, String.valueOf(n++));

                                //产品图号
                                writeCellValue(row, 1, part.getNumber());

                                //产品名称
                                writeCellValue(row, 2, part.getName());

                                //工装编号
                                writeCellValue(row, 3, element.attributeValue("toolNum"));

                                //工装名称
                                writeCellValue(row, 4, element.attributeValue("toolName"));

                                //工装类别
                                writeCellValue(row, 5, element.attributeValue("frockType"));

                                //型号
                                writeCellValue(row, 6, element.attributeValue("toolSpec"));

                                //规格
                                writeCellValue(row, 7, element.attributeValue("csize"));

                                //使用数量
                                writeCellValue(row, 8, "1");

                                //使用部门
                                writeCellValue(row, 9, procedure.attributeValue("workShop"));

                                //状态
                                writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));
                            }
                        }
                    }
                }
            }

            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }

    public static File exportDaoliangjuhuizong(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "daoliangju.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_产品所用刀量具汇总.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;

            List<WTPart> allPart = new ArrayList<WTPart>();
            allPart.add(rootPart);
            getAllChildPart(rootPart, allPart);

            for (WTPart part : allPart) {
                String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
                if (!"自制件".equals(mtype)) {
                    continue;
                }
                //固定批次的pbom add by liangbo start 2017.03.22
                if (batch != null && !"".equals(batch)) {
                    part = getPartByBatch(part, batch);
                }
                // end
                System.out.println("------part----" + part.getNumber() + "  " + part.getViewName());
                // List<WTDocument> document1 = BomUtil.getWTDocumentByPart(part,null,null);
                List<WTDocument> document1 = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                if (document1 != null && !document1.isEmpty()) {
                    for (WTDocument doc : document1) {
                        Element techEle = BomUtil.getTechincisElement(doc, null, null);
                        if (techEle == null) continue;
//                List<Element> techList = BomUtil.getTechnicsDocumentByPart(part,null,null);
//                if(techList != null && !techList.isEmpty()) {
//        			for (Element techEle : techList) {
                        if (techEle != null) {
                            List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
                            //开始循环遍历所有工序
                            for (Element procedure : steps) {
                                //获取当前工序的所有刀具元素
                                List<Element> klist = procedure.selectNodes("knifeTools/QMKnifeToolInfo");
                                if (klist != null) {
                                    for (Element element : klist) {
                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品图号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //编号
                                        writeCellValue(row, 3, element.attributeValue("toolNum"));

                                        //名称
                                        writeCellValue(row, 4, element.attributeValue("toolName"));

                                        //材料
                                        writeCellValue(row, 5, element.attributeValue("cmat"));

                                        //直径
                                        writeCellValue(row, 6, element.attributeValue("rkzj"));

                                        //类型
                                        writeCellValue(row, 7, element.attributeValue("jklx"));

                                        //使用数量
                                        writeCellValue(row, 8, "1");

                                        //使用部门
                                        writeCellValue(row, 9, procedure.attributeValue("workShop"));

                                        //备注
                                        writeCellValue(row, 10, "");

                                        //状态
                                        writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));
                                    }
                                }

                                //获取当前工序的所有工步的刀具元素
                                List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/knifeTools/QMKnifeToolInfo");
                                for (int i = 0; i < stepList.size(); i++) {
                                    Element element = stepList.get(i);
                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品图号
                                    writeCellValue(row, 1, part.getNumber());

                                    //产品名称
                                    writeCellValue(row, 2, part.getName());

                                    //编号
                                    writeCellValue(row, 3, element.attributeValue("toolNum"));

                                    //名称
                                    writeCellValue(row, 4, element.attributeValue("toolName"));

                                    //材料
                                    writeCellValue(row, 5, element.attributeValue("cmat"));

                                    //直径
                                    writeCellValue(row, 6, element.attributeValue("rkzj"));

                                    //类型
                                    writeCellValue(row, 7, element.attributeValue("jklx"));

                                    //使用数量
                                    writeCellValue(row, 8, "1");

                                    //使用部门
                                    writeCellValue(row, 9, procedure.attributeValue("workShop"));

                                    //备注
                                    writeCellValue(row, 10, "");

                                    //状态
                                    writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));
                                }

                                //获取当前工序的所有量具元素
                                List<Element> mlist = procedure.selectNodes("measures/QMMeasureInfo");
                                if (mlist != null) {
                                    for (Element element : mlist) {
                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品图号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //编号
                                        writeCellValue(row, 3, element.attributeValue("toolNum"));

                                        //名称
                                        writeCellValue(row, 4, element.attributeValue("toolName"));

                                        //材料
                                        writeCellValue(row, 5, element.attributeValue("cmat"));

                                        //直径
                                        writeCellValue(row, 6, element.attributeValue("rkzj"));

                                        //类型
                                        writeCellValue(row, 7, element.attributeValue("jklx"));

                                        //使用数量
                                        writeCellValue(row, 8, "1");

                                        //使用部门
                                        writeCellValue(row, 9, procedure.attributeValue("workShop"));

                                        //备注
                                        writeCellValue(row, 10, "");

                                        //状态
                                        writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));
                                    }
                                }

                                //获取当前工序的所有工步的量具元素
                                List<Element> stepmList = procedure.selectNodes("paces/QMProcedureInfo/measures/QMMeasureInfo");
                                for (int i = 0; i < stepmList.size(); i++) {
                                    Element element = stepmList.get(i);
                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品图号
                                    writeCellValue(row, 1, part.getNumber());

                                    //产品名称
                                    writeCellValue(row, 2, part.getName());

                                    //编号
                                    writeCellValue(row, 3, element.attributeValue("toolNum"));

                                    //名称
                                    writeCellValue(row, 4, element.attributeValue("toolName"));

                                    //型号
                                    writeCellValue(row, 5, element.attributeValue("pindex"));

                                    //规格
                                    writeCellValue(row, 6, element.attributeValue("csize"));

                                    //类型
                                    writeCellValue(row, 7, element.attributeValue(""));

                                    //使用数量
                                    writeCellValue(row, 8, "1");

                                    //使用部门
                                    writeCellValue(row, 9, procedure.attributeValue("workShop"));

                                    //备注
                                    writeCellValue(row, 10, "");

                                    //状态
                                    writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));
                                }
                            }
                        }
                    }
                }
            }

            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }

    public static File exportYiqiyibiaohuizong(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "yiqiyibiao.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_产品所用仪器仪表汇总.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;

            List<WTPart> allPart = new ArrayList<WTPart>();
            allPart.add(rootPart);
            getAllChildPart(rootPart, allPart);

            for (WTPart part : allPart) {
                String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
                if (!"自制件".equals(mtype)) {
                    continue;
                }
                //固定批次的pbom add by liangbo start 2017.03.22
                if (batch != null && !"".equals(batch)) {
                    part = getPartByBatch(part, batch);
                }
                // end
                System.out.println("------part--------" + part.getNumber() + "  " + part.getViewName());
                //  List<Element> techList = BomUtil.getTechnicsDocumentByPart(part,null,null);
                //List<WTDocument> document1 = BomUtil.getWTDocumentByPart(part,null,null);
                List<WTDocument> document1 = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                if (document1 != null && !document1.isEmpty()) {
                    for (WTDocument doc : document1) {
                        Element techEle = BomUtil.getTechincisElement(doc, null, null);
                        if (techEle == null) continue;
//                if(techList != null && !techList.isEmpty()) {
//        			for (Element techEle : techList) {
//        				if(techEle != null) {
                        List<Element> steps = techEle.selectNodes("steps/QMProcedureInfo");
                        //开始循环遍历所有工序
                        for (Element procedure : steps) {
                            //获取当前工序的所有标准仪器仪表元素
                            List<Element> dlist = procedure.selectNodes("sdashboard/QMSDashboardInfo");
                            if (dlist != null) {
                                for (Element element : dlist) {
                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品图号
                                    writeCellValue(row, 1, part.getNumber());

                                    //产品名称
                                    writeCellValue(row, 2, part.getName());

                                    //编号
                                    writeCellValue(row, 3, element.attributeValue("number"));

                                    //名称
                                    writeCellValue(row, 4, element.attributeValue("name"));

                                    //型号
                                    writeCellValue(row, 5, element.attributeValue("pindex"));

                                    //规格
                                    writeCellValue(row, 6, element.attributeValue("csize"));

                                    //类型
                                    writeCellValue(row, 7, "");

                                    //使用数量
                                    writeCellValue(row, 8, "1");

                                    //使用部门
                                    writeCellValue(row, 9, procedure.attributeValue("workShop"));

                                    //备注
                                    writeCellValue(row, 10, "");

                                    //状态
                                    writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));
                                }
                            }

                            //获取当前工序的所有工步的标准仪器仪表元素
                            List<Element> stepList = procedure.selectNodes("paces/QMProcedureInfo/sdashboard/QMSDashboardInfo");
                            for (int i = 0; i < stepList.size(); i++) {
                                Element element = stepList.get(i);
                                row = sheet.createRow(index++);
                                //序号
                                writeCellValue(row, 0, String.valueOf(n++));

                                //产品图号
                                writeCellValue(row, 1, part.getNumber());

                                //产品名称
                                writeCellValue(row, 2, part.getName());

                                //编号
                                writeCellValue(row, 3, element.attributeValue("number"));

                                //名称
                                writeCellValue(row, 4, element.attributeValue("name"));

                                //型号
                                writeCellValue(row, 5, element.attributeValue("pindex"));

                                //规格
                                writeCellValue(row, 6, element.attributeValue("csize"));

                                //类型
                                writeCellValue(row, 7, "");

                                //使用数量
                                writeCellValue(row, 8, "1");

                                //使用部门
                                writeCellValue(row, 9, procedure.attributeValue("workShop"));

                                //备注
                                writeCellValue(row, 10, "");

                                //状态
                                writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));
                            }

                            //获取当前工序的所有非标准仪器仪表元素
                            List<Element> mlist = procedure.selectNodes("unsdashboard/QMUnSDashboardInfo");
                            if (mlist != null) {
                                for (Element element : mlist) {
                                    row = sheet.createRow(index++);
                                    //序号
                                    writeCellValue(row, 0, String.valueOf(n++));

                                    //产品图号
                                    writeCellValue(row, 1, part.getNumber());

                                    //产品名称
                                    writeCellValue(row, 2, part.getName());

                                    //编号
                                    writeCellValue(row, 3, element.attributeValue("number"));

                                    //名称
                                    writeCellValue(row, 4, element.attributeValue("name"));

                                    //型号
                                    writeCellValue(row, 5, element.attributeValue("pindex"));

                                    //规格
                                    writeCellValue(row, 6, element.attributeValue("csize"));

                                    //类型
                                    writeCellValue(row, 7, "");

                                    //使用数量
                                    writeCellValue(row, 8, "1");

                                    //使用部门
                                    writeCellValue(row, 9, procedure.attributeValue("workShop"));

                                    //备注
                                    writeCellValue(row, 10, "");

                                    //状态
                                    writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));
                                }
                            }

                            //获取当前工序的所有工步的非标准仪器仪表元素
                            List<Element> stepdList = procedure.selectNodes("paces/QMProcedureInfo/unsdashboard/QMUnSDashboardInfo");
                            for (int i = 0; i < stepdList.size(); i++) {
                                Element element = stepdList.get(i);
                                row = sheet.createRow(index++);
                                //序号
                                writeCellValue(row, 0, String.valueOf(n++));

                                //产品图号
                                writeCellValue(row, 1, part.getNumber());

                                //产品名称
                                writeCellValue(row, 2, part.getName());

                                //编号
                                writeCellValue(row, 3, element.attributeValue("number"));

                                //名称
                                writeCellValue(row, 4, element.attributeValue("name"));

                                //型号
                                writeCellValue(row, 5, element.attributeValue("pindex"));

                                //规格
                                writeCellValue(row, 6, element.attributeValue("csize"));

                                //类型
                                writeCellValue(row, 7, "");

                                //使用数量
                                writeCellValue(row, 8, "1");

                                //使用部门
                                writeCellValue(row, 9, procedure.attributeValue("workShop"));

                                //备注
                                writeCellValue(row, 10, "");

                                //状态
                                writeCellValue(row, 11, doc.getState().getState().getDisplay(Locale.CHINA));
                            }
                        }
                    }
                }
            }


            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }

    //add by zhuhao 2017.6.13
    public static File exportChanpinbutaodingezonghui(WTPart rootPart, String batch) {
        String oPath = templateDir + File.separator + "chanpinbutaodinge.xls";
        String tPath = tempDir + File.separator + rootPart.getNumber().replace("/", "_") + "_型号产品部套定额汇总.xls";


        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 2;
            int n = 1;

            List<WTPart> allPart = new ArrayList<WTPart>();
            // Map<String,Integer> butaoNums = new HashMap<String,Integer>();
            TreeNode node = new TreeNode(null, rootPart.getNumber(), null);

            Map<String, TreeNode> treeNodes = new HashMap<String, TreeNode>();
            treeNodes.put(node.getNumber(), node);

            Map<String, Integer> butaoNums = new HashMap<String, Integer>();
            Map<String, Integer> tempbutaoNums = new HashMap<String, Integer>();
            tempbutaoNums.put(rootPart.getNumber(), 1);
            allPart.add(rootPart);
            getAllChildPart(rootPart, allPart, tempbutaoNums, treeNodes);
            getButaoNums(butaoNums, treeNodes, tempbutaoNums);
            for (WTPart part : allPart) {
                String mtype = IBAHelper.getIBAStringValue(part, "MTYPE");
                //固定批次的pbom add by liangbo start 2017.03.22
                if (batch != null && !"".equals(batch)) {
                    part = getPartByBatch(part, batch);
                }
                // end

                System.out.println("------part----" + part.getNumber() + "  " + part.getViewName());
                List<WTDocument> document = BomUtil.getAllWTDocumentByAllSameVersionViewPart(part);
                if ("不带料委外件".equals(mtype)) {
                    //不带料委外零件信息输出到Excel
                    row = sheet.createRow(index++);
                    //序号
                    writeCellValue(row, 0, String.valueOf(n++));

                    //产品编号
                    writeCellValue(row, 1, part.getNumber());

                    //产品名称
                    writeCellValue(row, 2, part.getName());

                    //名称
                    writeCellValue(row, 3, IBAHelper.getIBAStringValue(part, "PTC_MATERIAL_NAME"));

                    //牌号
                    writeCellValue(row, 4, IBAHelper.getIBAStringValue(part, "MARKNUMBER"));

                    //规格
                    writeCellValue(row, 5, IBAHelper.getIBAStringValue(part, "STANDARD"));

                    //技术条件
                    writeCellValue(row, 6, "");

                    //附加条件
                    writeCellValue(row, 7, "");

                    //精度等级
                    writeCellValue(row, 8, "");

                    //类型
                    writeCellValue(row, 9, IBAHelper.getIBAStringValue(part, "MTYPE"));

                    //技术协议
                    writeCellValue(row, 10, "");

                    //下料尺寸
                    writeCellValue(row, 11, "");

                    //生产厂家
                    writeCellValue(row, 12, "");

                    //军标号
                    writeCellValue(row, 13, "");

                    //封装形式
                    writeCellValue(row, 14, IBAHelper.getIBAStringValue(part, "PACKAGINGFORM"));

                    //质量等级
                    writeCellValue(row, 15, "");

                    //热处理
                    writeCellValue(row, 16, IBAHelper.getIBAStringValue(part, "HEATTREATMENT"));

                    //机械性能等级
                    writeCellValue(row, 17, IBAHelper.getIBAStringValue(part, "MECHANICALPROPERTYORHARDNESS"));

                    //可制件数
                    writeCellValue(row, 18, "");

                    //定额数量
                    writeCellValue(row, 19, "");

                    //单位
                    writeCellValue(row, 20, "");

                    //套内数量
                    writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                    //主辅类型
                    writeCellValue(row, 22, "");

                    //文件类型
                    writeCellValue(row, 23, "");

                    //备注
                    writeCellValue(row, 24, "");

                    //版本
                    writeCellValue(row, 25, part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());

                    //状态
                    writeCellValue(row, 26, part.getState().getState().getDisplay(Locale.CHINA));

                    //阶段标记
                    writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                }
                if (document != null && !document.isEmpty()) {
                    for (WTDocument doc : document) {
                        Element techEle = BomUtil.getTechincisElement(doc, null, null);

//                List<Element> techList = BomUtil.getTechnicsDocumentByPart(part,null,null);
//                if(techList != null && !techList.isEmpty()) {
//        			for (Element techEle : techList) {
                        if (techEle != null) {
                            Element clde = techEle.element("CLDE");
                            if (clde != null) {
                                Element yclde = clde.element("YCLDE");
                                if (yclde != null) {

                                    List<Element> ycl = yclde.elements();
                                    if (ycl != null && !ycl.isEmpty()) {
                                        for (Element element : ycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "原材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            //生产厂家
                                            writeCellValue(row, 12, element.attributeValue("sccj"));

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, element.attributeValue("kzjs"));

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("xlcc"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, "");

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("chbm"));
                                        }
                                    }
                                }

                                Element sjyclde = clde.element("SJYCLDE");
                                if (sjyclde != null) {
                                    List<Element> sjycl = sjyclde.elements();
                                    if (sjycl != null && !sjycl.isEmpty()) {
                                        for (Element element : sjycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "试件原材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("sjcc"));

                                            //生产厂家
                                            writeCellValue(row, 12, element.attributeValue("sccj"));

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, element.attributeValue("sjkzjs"));

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("sjsl"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, "");

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("chbm"));

                                        }
                                    }
                                }

                                Element zyclde = clde.element("ZYCLDE");
                                if (zyclde != null) {
                                    List<Element> zycl = zyclde.elements();
                                    if (zycl != null && !zycl.isEmpty()) {
                                        for (Element element : zycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "主要材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            //生产厂家
                                            writeCellValue(row, 12, element.attributeValue("sccj"));

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, "");

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("sl"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, "");

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("chbm"));

                                        }
                                    }
                                }


                                //数据来源于设计资源库
                                Element SJZYKyclde = clde.element("SJZYKYCLDE");
                                if (SJZYKyclde != null) {
                                    List<Element> ycl = SJZYKyclde.elements();
                                    if (ycl != null && !ycl.isEmpty()) {
                                        for (Element element : ycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            String dataType = element.attributeValue("dataType");
                                            String ph = element.attributeValue("ph");
                                            String gg = element.attributeValue("gg");
                                            String jstj = element.attributeValue("jstj");
                                            String bz = "";
                                            if ("标准件".equals(dataType)) {
                                                ph = element.attributeValue("cl");
                                                gg = element.attributeValue("gg");
                                                jstj = element.attributeValue("bzh");
                                                bz = element.attributeValue("bmcl") + "+" + element.attributeValue("cpxs") + "+" + element.attributeValue("cpdj") + "+" + element.attributeValue("bnxs") + "+" + element.attributeValue("tssm");
                                            } else if ("元器件".equals(dataType)) {
                                                ph = element.attributeValue("xh");
                                                gg = element.attributeValue("xhgg");
                                                jstj = element.attributeValue("zgf") + "+" + element.attributeValue("xxgg");
                                                bz = element.attributeValue("wxcc") + "+" + element.attributeValue("zytj") + "+" + element.attributeValue("tssm") + "+" + element.attributeValue("sfjk");
                                            }
                                            //牌号
                                            writeCellValue(row, 4, ph);

                                            //规格
                                            writeCellValue(row, 5, gg);

                                            //技术条件
                                            writeCellValue(row, 6, jstj);

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "原材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            String sccj = element.attributeValue("sccj");
                                            if (sccj == null || "".equals(sccj)) {
                                                sccj = element.attributeValue("gys");
                                            }
                                            //生产厂家
                                            writeCellValue(row, 12, sccj);

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, element.attributeValue("kzjs"));

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("xlcc"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, bz);

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("sjbm"));

                                        }
                                    }
                                }

                                Element SJZYKsjyclde = clde.element("SJZYKSJYCLDE");
                                if (SJZYKsjyclde != null) {
                                    List<Element> sjycl = SJZYKsjyclde.elements();
                                    if (sjycl != null && !sjycl.isEmpty()) {
                                        for (Element element : sjycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            String dataType = element.attributeValue("dataType");
                                            String ph = element.attributeValue("ph");
                                            String gg = element.attributeValue("gg");
                                            String jstj = element.attributeValue("jstj");
                                            String bz = "";
                                            if ("标准件".equals(dataType)) {
                                                ph = element.attributeValue("cl");
                                                gg = element.attributeValue("gg");
                                                jstj = element.attributeValue("bzh");
                                                bz = element.attributeValue("bmcl") + "+" + element.attributeValue("cpxs") + "+" + element.attributeValue("cpdj") + "+" + element.attributeValue("bnxs") + "+" + element.attributeValue("tssm");
                                            } else if ("元器件".equals(dataType)) {
                                                ph = element.attributeValue("xh");
                                                gg = element.attributeValue("xhgg");
                                                jstj = element.attributeValue("zgf") + "+" + element.attributeValue("xxgg");
                                                bz = element.attributeValue("wxcc") + "+" + element.attributeValue("zytj") + "+" + element.attributeValue("tssm") + "+" + element.attributeValue("sfjk");
                                            }
                                            //牌号
                                            writeCellValue(row, 4, ph);

                                            //规格
                                            writeCellValue(row, 5, gg);

                                            //技术条件
                                            writeCellValue(row, 6, jstj);

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "试件原材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            //生产厂家
                                            writeCellValue(row, 12, element.attributeValue("gys"));

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, element.attributeValue("sjkzjs"));

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("sjcc"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, bz);

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("sjbm"));

                                        }
                                    }
                                }

                                Element SJZYKzyclde = clde.element("SJZYKZYCLDE");
                                if (SJZYKzyclde != null) {
                                    List<Element> zycl = SJZYKzyclde.elements();
                                    if (zycl != null && !zycl.isEmpty()) {
                                        for (Element element : zycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            String dataType = element.attributeValue("dataType");
                                            String ph = element.attributeValue("ph");
                                            String gg = element.attributeValue("gg");
                                            String jstj = element.attributeValue("jstj");
                                            String bz = "";
                                            if ("标准件".equals(dataType)) {
                                                ph = element.attributeValue("cl");
                                                gg = element.attributeValue("gg");
                                                jstj = element.attributeValue("bzh");
                                                bz = element.attributeValue("bmcl") + "+" + element.attributeValue("cpxs") + "+" + element.attributeValue("cpdj") + "+" + element.attributeValue("bnxs") + "+" + element.attributeValue("tssm");
                                            } else if ("元器件".equals(dataType)) {
                                                ph = element.attributeValue("xh");
                                                gg = element.attributeValue("xhgg");
                                                jstj = element.attributeValue("zgf") + "+" + element.attributeValue("xxgg");
                                                bz = element.attributeValue("wxcc") + "+" + element.attributeValue("zytj") + "+" + element.attributeValue("tssm") + "+" + element.attributeValue("sfjk");
                                            }
                                            //牌号
                                            writeCellValue(row, 4, ph);

                                            //规格
                                            writeCellValue(row, 5, gg);

                                            //技术条件
                                            writeCellValue(row, 6, jstj);

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "主要材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            String sccj = element.attributeValue("sccj");
                                            if (sccj == null || "".equals(sccj)) {
                                                sccj = element.attributeValue("gys");
                                            }
                                            //生产厂家
                                            writeCellValue(row, 12, sccj);

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, "");

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("sl"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, bz);

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("sjbm"));

                                        }
                                    }
                                }

                            }
                            Element gyde = techEle.element("GYDE");
                            if (gyde != null) {
                                Element yclde = gyde.element("YCLDE");
                                if (yclde != null) {

                                    List<Element> ycl = yclde.elements();
                                    if (ycl != null && !ycl.isEmpty()) {
                                        for (Element element : ycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "原材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            //生产厂家
                                            writeCellValue(row, 12, element.attributeValue("sccj"));

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, element.attributeValue("kzjs"));

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("xlcc"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, "");

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("chbm"));

                                        }
                                    }
                                }

                                Element sjyclde = gyde.element("SJYCLDE");
                                if (sjyclde != null) {
                                    List<Element> sjycl = sjyclde.elements();
                                    if (sjycl != null && !sjycl.isEmpty()) {
                                        for (Element element : sjycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "试件原材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            //生产厂家
                                            writeCellValue(row, 12, element.attributeValue("sccj"));

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, element.attributeValue("sjkzjs"));

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("sjsl"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, "");

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("chbm"));

                                        }
                                    }
                                }

                                Element zyclde = gyde.element("ZYCLDE");
                                if (zyclde != null) {
                                    List<Element> zycl = zyclde.elements();
                                    if (zycl != null && !zycl.isEmpty()) {
                                        for (Element element : zycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("chmc"));

                                            //牌号
                                            writeCellValue(row, 4, element.attributeValue("xhph"));

                                            //规格
                                            writeCellValue(row, 5, element.attributeValue("gg"));

                                            //技术条件
                                            writeCellValue(row, 6, element.attributeValue("jstj"));

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "主要材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            //生产厂家
                                            writeCellValue(row, 12, element.attributeValue("sccj"));

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, "");

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("sl"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, "");

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("chbm"));

                                        }
                                    }
                                }


                                //数据来源于设计资源库
                                Element SJZYKyclde = gyde.element("SJZYKYCLDE");
                                if (SJZYKyclde != null) {
                                    List<Element> ycl = SJZYKyclde.elements();
                                    if (ycl != null && !ycl.isEmpty()) {
                                        for (Element element : ycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            String dataType = element.attributeValue("dataType");
                                            String ph = element.attributeValue("ph");
                                            String gg = element.attributeValue("gg");
                                            String jstj = element.attributeValue("jstj");
                                            String bz = "";
                                            if ("标准件".equals(dataType)) {
                                                ph = element.attributeValue("cl");
                                                gg = element.attributeValue("gg");
                                                jstj = element.attributeValue("bzh");
                                                bz = element.attributeValue("bmcl") + "+" + element.attributeValue("cpxs") + "+" + element.attributeValue("cpdj") + "+" + element.attributeValue("bnxs") + "+" + element.attributeValue("tssm");
                                            } else if ("元器件".equals(dataType)) {
                                                ph = element.attributeValue("xh");
                                                gg = element.attributeValue("xhgg");
                                                jstj = element.attributeValue("zgf") + "+" + element.attributeValue("xxgg");
                                                bz = element.attributeValue("wxcc") + "+" + element.attributeValue("zytj") + "+" + element.attributeValue("tssm") + "+" + element.attributeValue("sfjk");
                                            }
                                            //牌号
                                            writeCellValue(row, 4, ph);

                                            //规格
                                            writeCellValue(row, 5, gg);

                                            //技术条件
                                            writeCellValue(row, 6, jstj);

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "原材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            String sccj = element.attributeValue("sccj");
                                            if (sccj == null || "".equals(sccj)) {
                                                sccj = element.attributeValue("gys");
                                            }
                                            //生产厂家
                                            writeCellValue(row, 12, sccj);

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, element.attributeValue("kzjs"));

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("xlcc"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, bz);

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("sjbm"));

                                        }
                                    }
                                }

                                Element SJZYKsjyclde = gyde.element("SJZYKSJYCLDE");
                                if (SJZYKsjyclde != null) {
                                    List<Element> sjycl = SJZYKsjyclde.elements();
                                    if (sjycl != null && !sjycl.isEmpty()) {
                                        for (Element element : sjycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            String dataType = element.attributeValue("dataType");
                                            String ph = element.attributeValue("ph");
                                            String gg = element.attributeValue("gg");
                                            String jstj = element.attributeValue("jstj");
                                            String bz = "";
                                            if ("标准件".equals(dataType)) {
                                                ph = element.attributeValue("cl");
                                                gg = element.attributeValue("gg");
                                                jstj = element.attributeValue("bzh");
                                                bz = element.attributeValue("bmcl") + "+" + element.attributeValue("cpxs") + "+" + element.attributeValue("cpdj") + "+" + element.attributeValue("bnxs") + "+" + element.attributeValue("tssm");
                                            } else if ("元器件".equals(dataType)) {
                                                ph = element.attributeValue("xh");
                                                gg = element.attributeValue("xhgg");
                                                jstj = element.attributeValue("zgf") + "+" + element.attributeValue("xxgg");
                                                bz = element.attributeValue("wxcc") + "+" + element.attributeValue("zytj") + "+" + element.attributeValue("tssm") + "+" + element.attributeValue("sfjk");
                                            }
                                            //牌号
                                            writeCellValue(row, 4, ph);

                                            //规格
                                            writeCellValue(row, 5, gg);

                                            //技术条件
                                            writeCellValue(row, 6, jstj);

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "试件原材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            String sccj = element.attributeValue("sccj");
                                            if (sccj == null || "".equals(sccj)) {
                                                sccj = element.attributeValue("gys");
                                            }
                                            //生产厂家
                                            writeCellValue(row, 12, sccj);

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, element.attributeValue("sjkzjs"));

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("sjcc"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, bz);

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("sjbm"));

                                        }
                                    }
                                }

                                Element SJZYKzyclde = gyde.element("SJZYKZYCLDE");
                                if (SJZYKzyclde != null) {
                                    List<Element> zycl = SJZYKzyclde.elements();
                                    if (zycl != null && !zycl.isEmpty()) {
                                        for (Element element : zycl) {
                                            row = sheet.createRow(index++);
                                            //序号
                                            writeCellValue(row, 0, String.valueOf(n++));

                                            //产品图号
                                            writeCellValue(row, 1, part.getNumber());

                                            //产品名称
                                            writeCellValue(row, 2, part.getName());

                                            //名称
                                            writeCellValue(row, 3, element.attributeValue("name"));

                                            String dataType = element.attributeValue("dataType");
                                            String ph = element.attributeValue("ph");
                                            String gg = element.attributeValue("gg");
                                            String jstj = element.attributeValue("jstj");
                                            String bz = "";
                                            if ("标准件".equals(dataType)) {
                                                ph = element.attributeValue("cl");
                                                gg = element.attributeValue("gg");
                                                jstj = element.attributeValue("bzh");
                                                bz = element.attributeValue("bmcl") + "+" + element.attributeValue("cpxs") + "+" + element.attributeValue("cpdj") + "+" + element.attributeValue("bnxs") + "+" + element.attributeValue("tssm");
                                            } else if ("元器件".equals(dataType)) {
                                                ph = element.attributeValue("xh");
                                                gg = element.attributeValue("xhgg");
                                                jstj = element.attributeValue("zgf") + "+" + element.attributeValue("xxgg");
                                                bz = element.attributeValue("wxcc") + "+" + element.attributeValue("zytj") + "+" + element.attributeValue("tssm") + "+" + element.attributeValue("sfjk");
                                            }
                                            //牌号
                                            writeCellValue(row, 4, ph);

                                            //规格
                                            writeCellValue(row, 5, gg);

                                            //技术条件
                                            writeCellValue(row, 6, jstj);

                                            //附加条件
                                            writeCellValue(row, 7, element.attributeValue("fjtj"));

                                            //精度等级
                                            writeCellValue(row, 8, element.attributeValue("jddj"));

                                            //类型
                                            writeCellValue(row, 9, "主要材料");

                                            //技术协议
                                            writeCellValue(row, 10, "");

                                            //下料尺寸
                                            writeCellValue(row, 11, element.attributeValue("xlcc"));

                                            String sccj = element.attributeValue("sccj");
                                            if (sccj == null || "".equals(sccj)) {
                                                sccj = element.attributeValue("gys");
                                            }
                                            //生产厂家
                                            writeCellValue(row, 12, sccj);

                                            //军标号
                                            writeCellValue(row, 13, "");

                                            //封装形式
                                            writeCellValue(row, 14, element.attributeValue("fzxs"));

                                            //质量等级
                                            writeCellValue(row, 15, element.attributeValue("zldj"));

                                            //热处理
                                            writeCellValue(row, 16, element.attributeValue("gyztrcl"));

                                            //机械性能等级
                                            writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                            //可制件数
                                            writeCellValue(row, 18, "");

                                            //工艺定额
                                            writeCellValue(row, 19, element.attributeValue("sl"));

                                            //单位
                                            writeCellValue(row, 20, element.attributeValue("dw"));

                                            //套内数量
                                            writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                            //主辅类型
                                            writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                            //文件类型
                                            writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                            //备注
                                            writeCellValue(row, 24, bz);

                                            //版本
                                            writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                            //状态
                                            writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                            //阶段标记
                                            writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                            writeCellValue(row, 28, element.attributeValue("sjbm"));

                                        }
                                    }
                                }
                                List<Element> newPart = techEle.selectNodes("GYDE/NEWPART/NewPart");
                                if (newPart != null && !newPart.isEmpty()) {
                                    for (Element element : newPart) {
                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品编号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //名称
                                        writeCellValue(row, 3, element.attributeValue("chmc"));

                                        //牌号
                                        writeCellValue(row, 4, element.attributeValue("xhph"));

                                        //规格
                                        writeCellValue(row, 5, element.attributeValue("gg"));

                                        //技术条件
                                        writeCellValue(row, 6, element.attributeValue("jstj"));

                                        //附加条件
                                        writeCellValue(row, 7, element.attributeValue("fjtj"));

                                        //精度等级
                                        writeCellValue(row, 8, element.attributeValue("jddj"));

                                        //类别
                                        writeCellValue(row, 9, getType(element.attributeValue("wzlb")));

                                        //技术协议
                                        writeCellValue(row, 10, element.attributeValue("jsxy"));

                                        //下料尺寸
                                        writeCellValue(row, 11, element.attributeValue("xlcc"));

                                        //生产厂家
                                        writeCellValue(row, 12, element.attributeValue("sccj"));

                                        //军标号
                                        writeCellValue(row, 13, element.attributeValue("jbh"));

                                        //封装形式
                                        writeCellValue(row, 14, element.attributeValue("fzxs"));

                                        //质量等级
                                        writeCellValue(row, 15, element.attributeValue("zldj"));

                                        //供应状态/热处理
                                        writeCellValue(row, 16, element.attributeValue("gyzt"));

                                        //机械性能等级
                                        writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                        //可制件数
                                        writeCellValue(row, 18, element.attributeValue("kzjs"));

                                        //定额数量
                                        writeCellValue(row, 19, element.attributeValue("sl"));

                                        String dw = element.attributeValue("dw2");
                                        if(Tools.isNull(dw)){
                                            dw = element.attributeValue("dw");
                                        }
                                        //单位
                                        writeCellValue(row, 20, dw);

                                        //套内数量?
                                        writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                        //主辅类型
                                        writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                        //文件类型
                                        writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                        //备注
                                        writeCellValue(row, 24, "");

                                        //版本
                                        writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                        //状态
                                        writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                        //阶段标记
                                        writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                        writeCellValue(row, 28, element.attributeValue("chbm"));

                                    }
                                }
                                newPart = techEle.selectNodes("GYDE/SJZYKNEWPART/SjzykNewPart");
                                if (newPart != null && !newPart.isEmpty()) {
                                    for (Element element : newPart) {
                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品编号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //名称
                                        writeCellValue(row, 3, element.attributeValue("name"));

                                        String dataType = element.attributeValue("dataType");
                                        String ph = element.attributeValue("ph");
                                        String gg = element.attributeValue("gg");
                                        String jstj = element.attributeValue("bzh");
                                        String bz = "";
                                        if ("标准件".equals(dataType)) {
                                            ph = element.attributeValue("cl");
                                            gg = element.attributeValue("gg");
                                            jstj = element.attributeValue("bzh");
                                            bz = element.attributeValue("bmcl") + "+" + element.attributeValue("cpxs") + "+" + element.attributeValue("cpdj") + "+" + element.attributeValue("bnxs") + "+" + element.attributeValue("tssm");
                                        } else if ("元器件".equals(dataType)) {
                                            ph = element.attributeValue("xh");
                                            gg = element.attributeValue("xhgg");
                                            jstj = element.attributeValue("zgf") + "+" + element.attributeValue("xxgg");
                                            bz = element.attributeValue("wxcc") + "+" + element.attributeValue("zytj") + "+" + element.attributeValue("tssm") + "+" + element.attributeValue("sfjk");
                                        }
                                        //牌号
                                        writeCellValue(row, 4, ph);

                                        //规格
                                        writeCellValue(row, 5, gg);

                                        //技术条件
                                        writeCellValue(row, 6, jstj);

                                        //附加条件
                                        writeCellValue(row, 7, element.attributeValue("fjtj"));

                                        //精度等级
                                        writeCellValue(row, 8, element.attributeValue("jddj"));

                                        //类别
                                        writeCellValue(row, 9, element.attributeValue("dataType"));

                                        //技术协议
                                        writeCellValue(row, 10, "");

                                        //下料尺寸
                                        writeCellValue(row, 11, element.attributeValue("xlcc"));

                                        //生产厂家
                                        writeCellValue(row, 12, element.attributeValue("gys"));

                                        //军标号
                                        writeCellValue(row, 13, element.attributeValue("jbh"));

                                        //封装形式
                                        writeCellValue(row, 14, element.attributeValue("fzxs"));

                                        //质量等级
                                        writeCellValue(row, 15, element.attributeValue("zldj"));

                                        //供应状态/热处理
                                        writeCellValue(row, 16, element.attributeValue("gyzt"));

                                        //机械性能等级
                                        writeCellValue(row, 17, element.attributeValue("jxxndjhyd"));

                                        //可制件数
                                        writeCellValue(row, 18, element.attributeValue("kzjs"));
                                        String sl = element.attributeValue("gysl");
                                        if(Tools.isNull(sl)){
                                            sl = element.attributeValue("sl");
                                        }
                                        //定额数量
                                        writeCellValue(row, 19, sl);

                                        //单位
                                        writeCellValue(row, 20, element.attributeValue("dw"));

                                        //套内数量?
                                        writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                        //主辅类型
                                        writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                        //文件类型
                                        writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                        //备注
                                        writeCellValue(row, 24, bz);

                                        //版本
                                        writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                        //状态
                                        writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                        //阶段标记
                                        writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                        writeCellValue(row, 28, element.attributeValue("sjbm"));

                                    }
                                }


                                List<Element> listEle = techEle.selectNodes("GYDE/MATCHPART/MatchPart");
                                if (listEle != null && !listEle.isEmpty()) {
                                    for (Element element : listEle) {

                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品编号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //名称
                                        writeCellValue(row, 3, element.attributeValue("chmc"));

                                        //牌号
                                        writeCellValue(row, 4, element.attributeValue("xhph"));

                                        //规格
                                        writeCellValue(row, 5, element.attributeValue("gg"));

                                        //技术条件
                                        writeCellValue(row, 6, element.attributeValue("jstj"));

                                        //附加条件
                                        writeCellValue(row, 7, element.attributeValue("fjtj"));

                                        //精度等级
                                        writeCellValue(row, 8, element.attributeValue("jddj"));

                                        //类别
                                        writeCellValue(row, 9, getType(element.attributeValue("wzlb")));

                                        //技术协议
                                        writeCellValue(row, 10, "");

                                        //下料尺寸
                                        writeCellValue(row, 11, element.attributeValue("xlcc"));

                                        //生产厂家
                                        writeCellValue(row, 12, element.attributeValue("sccj"));

                                        //军标号
                                        writeCellValue(row, 13, element.attributeValue("jbh"));

                                        //封装形式
                                        writeCellValue(row, 14, element.attributeValue("fzxs"));

                                        //质量等级
                                        writeCellValue(row, 15, element.attributeValue("zldj"));

                                        //供应状态/热处理
                                        writeCellValue(row, 16, element.attributeValue("gyzt"));

                                        //机械性能等级
                                        writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                        //可制件数
                                        writeCellValue(row, 18, element.attributeValue("kzjs"));

                                        //定额数量
                                        writeCellValue(row, 19, element.attributeValue("sl"));

                                        String dw = element.attributeValue("dw2");
                                        if(Tools.isNull(dw)){
                                            dw = element.attributeValue("dw");
                                        }
                                        //单位
                                        writeCellValue(row, 20, dw);

                                        //套内数量
                                        writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                        //主辅类型
                                        writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                        //文件类型
                                        writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                        //备注
                                        writeCellValue(row, 24, "");

                                        //版本
                                        writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                        //状态
                                        writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                        //阶段标记
                                        writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));
                                        writeCellValue(row, 28, element.attributeValue("chbm"));

                                    }
                                }
                                listEle = techEle.selectNodes("GYDE/SJZYKMATCHPART/SjzykMatchPart");
                                if (listEle != null && !listEle.isEmpty()) {
                                    for (Element element : listEle) {

                                        row = sheet.createRow(index++);
                                        //序号
                                        writeCellValue(row, 0, String.valueOf(n++));

                                        //产品编号
                                        writeCellValue(row, 1, part.getNumber());

                                        //产品名称
                                        writeCellValue(row, 2, part.getName());

                                        //名称
                                        writeCellValue(row, 3, element.attributeValue("name"));

                                        String dataType = element.attributeValue("dataType");
                                        String ph = element.attributeValue("ph");
                                        String gg = element.attributeValue("gg");
                                        String jstj = element.attributeValue("bzh");
                                        String bz = "";
                                        if ("标准件".equals(dataType)) {
                                            ph = element.attributeValue("cl");
                                            gg = element.attributeValue("gg");
                                            jstj = element.attributeValue("bzh");
                                            bz = element.attributeValue("bmcl") + "+" + element.attributeValue("cpxs") + "+" + element.attributeValue("cpdj") + "+" + element.attributeValue("bnxs") + "+" + element.attributeValue("tssm");
                                        } else if ("元器件".equals(dataType)) {
                                            ph = element.attributeValue("xh");
                                            gg = element.attributeValue("xhgg");
                                            jstj = element.attributeValue("zgf") + "+" + element.attributeValue("xxgg");
                                            bz = element.attributeValue("wxcc") + "+" + element.attributeValue("zytj") + "+" + element.attributeValue("tssm") + "+" + element.attributeValue("sfjk");
                                        }
                                        //牌号
                                        writeCellValue(row, 4, ph);

                                        //规格
                                        writeCellValue(row, 5, gg);

                                        //技术条件
                                        writeCellValue(row, 6, jstj);

                                        //附加条件
                                        writeCellValue(row, 7, element.attributeValue("fjtj"));

                                        //精度等级
                                        writeCellValue(row, 8, element.attributeValue("jddj"));

                                        //类别
                                        writeCellValue(row, 9, element.attributeValue("dataType"));

                                        //技术协议
                                        writeCellValue(row, 10, "");

                                        //下料尺寸
                                        writeCellValue(row, 11, element.attributeValue("xlcc"));

                                        //生产厂家
                                        writeCellValue(row, 12, element.attributeValue("gys"));

                                        //军标号
                                        writeCellValue(row, 13, element.attributeValue("jbh"));

                                        //封装形式
                                        writeCellValue(row, 14, element.attributeValue("fzxs"));

                                        //质量等级
                                        writeCellValue(row, 15, element.attributeValue("zldj"));

                                        //供应状态/热处理
                                        writeCellValue(row, 16, element.attributeValue("gyzt"));

                                        //机械性能等级
                                        writeCellValue(row, 17, element.attributeValue("jxxndj"));

                                        //可制件数
                                        writeCellValue(row, 18, element.attributeValue("kzjs"));

                                        String sl = element.attributeValue("gysl");
                                        if(Tools.isNull(sl)){
                                            sl = element.attributeValue("sl");
                                        }
                                        //定额数量
                                        writeCellValue(row, 19, sl);

                                        //单位
                                        writeCellValue(row, 20, element.attributeValue("dw"));

                                        //套内数量?
                                        writeCellValue(row, 21, butaoNums.get(part.getNumber()) + "");

                                        //主辅类型
                                        writeCellValue(row, 22, IBAHelper.getIBAStringValue(doc, "ZFFLAG"));

                                        //文件类型
                                        writeCellValue(row, 23, IBAHelper.getIBAStringValue(doc, "PPLANTYPE"));

                                        //备注
                                        writeCellValue(row, 24, bz);

                                        //版本
                                        writeCellValue(row, 25, doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue());

                                        //状态
                                        writeCellValue(row, 26, doc.getState().getState().getDisplay(Locale.CHINA));

                                        //阶段标记
                                        writeCellValue(row, 27, IBAHelper.getIBAStringValue(part, "PHASE_CODE"));

                                        writeCellValue(row, 28, element.attributeValue("sjbm"));

                                    }
                                }
                            }
                        }
                    }
                }
            }

            xlsFile = new File(tPath);
            FileOutputStream outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return xlsFile;
    }

    private static void processDEXml(Element clde, HSSFSheet sheet, int index, int n, WTPart part, WTDocument doc, Map<String, Integer> butaoNums, Element techEle) throws WTException {


    }

    /**
     * 导出材料定额完成情况汇总表
     *
     * @param wtPart
     * @param batch
     * @return
     */
    public static File exportCldeWcztHz(WTPart wtPart, String batch) {
        IBAUtility ibaUtility;
        List<WTPart> partList = new ArrayList<WTPart>();
        List<CldeInfoBean> cldeInfoBeanList = null;
        try {
            partList.add(wtPart);
            getAllChildPart(wtPart, partList);
            cldeInfoBeanList = new ArrayList<CldeInfoBean>();
            CldeInfoBean cldeInfoBean;
            for (WTPart part : partList) {
                QuerySpec qs = new QuerySpec(ProcessTask.class);
                SearchCondition sc = new SearchCondition(ProcessTask.class, ProcessTask.NUMBER, SearchCondition.EQUAL, part.getNumber());
                qs.appendSearchCondition(sc);
                QueryResult qr = PersistenceHelper.manager.find(qs);
                while (qr.hasMoreElements()) {
                    ProcessTask processTask = (ProcessTask) qr.nextElement();
                    String partNumber = processTask.getNumber();//部件编号
                    String partName = processTask.getName();//部件名称
                    ibaUtility = new IBAUtility(processTask);
                    String cldeStartTime = processTask.getCreateTimestamp().toString();//材料定额开始时间
                    String cldePlanTime = ibaUtility.getIBAValue("cldePlanTime");//材料定额计划完成时间
                    if (cldePlanTime == null || cldePlanTime.isEmpty()) {
                        continue;
                    }
                    String cldeEndTime = ibaUtility.getIBAValue("cldeEndTime");//材料定额实际完成时间
                    String technicsNumber = ibaUtility.getIBAValue("relatedTech");//关联工艺
                    String completeState; //完成状态
                    String isOverDate; //是否按期完成
                    if (cldeEndTime != null && !cldeEndTime.isEmpty()) {
                        completeState = "已完成";
                        String prase;
                        if (cldePlanTime.contains("-")) {
                            prase = "yyyy-MM-dd HH:mm:ss";
                        } else {
                            prase = "yyyy/MM/dd HH:mm:ss";
                        }
                        SimpleDateFormat format = new SimpleDateFormat(prase);
                        Date planDate = format.parse(cldePlanTime);
                        if (cldeEndTime.contains("-")) {
                            prase = "yyyy-MM-dd HH:mm:ss";
                        } else {
                            prase = "yyyy/MM/dd HH:mm:ss";
                        }
                        format = new SimpleDateFormat(prase);
                        Date endDate = format.parse(cldeEndTime);
                        if (planDate.before(endDate)) {
                            isOverDate = "否";
                        } else {
                            isOverDate = "是";
                        }
                    } else {
                        completeState = "未完成";
                        isOverDate = "";
                    }
                    if (technicsNumber != null && !technicsNumber.isEmpty()) {

                        WTDocument wtDocument = WTDocumentUtil.getDocumentByNumber(technicsNumber);
                        String docNumber = wtDocument.getNumber();//工艺文件流水号
                        String docName = wtDocument.getName();//工艺文件名称
                        String version = wtDocument.getIterationDisplayIdentifier().toString();//工艺文件版本
                        ibaUtility = new IBAUtility(wtDocument);
                        String pplanNumber = ibaUtility.getIBAValue("PPNUMBER");//工艺文件编号
                        ProcessEnvelope proen = null;
                        QueryResult qrEnvelope = EnvelopeHelper.service.getEnvelopeByMemberObject(wtDocument);
                        while (qrEnvelope.hasMoreElements()) {
                            proen = ((EnvelopeMemberLink) qrEnvelope.nextElement()).getProcessEnvelope();
                            String packetNumber = proen.getNumber();//材料定额签审包编号

                            cldeInfoBean = new CldeInfoBean();
                            cldeInfoBean.setPartNumber(partNumber);
                            cldeInfoBean.setPartName(partName);
                            cldeInfoBean.setTechnicsNumber(docNumber);
                            cldeInfoBean.setTechnicsName(docName);
                            cldeInfoBean.setPplanNumber(pplanNumber);
                            cldeInfoBean.setVersion(version);
                            cldeInfoBean.setPacketNumber(packetNumber);
                            cldeInfoBean.setStartTime(cldeStartTime);
                            cldeInfoBean.setPlanTime(cldePlanTime);
                            cldeInfoBean.setEndTime(cldeEndTime);
                            cldeInfoBean.setCompleteState(completeState);
                            cldeInfoBean.setIsOverDate(isOverDate);
                            cldeInfoBeanList.add(cldeInfoBean);
                        }
                    } else {
                        cldeInfoBean = new CldeInfoBean();
                        cldeInfoBean.setPartNumber(partNumber);
                        cldeInfoBean.setPartName(partName);
                        cldeInfoBean.setTechnicsNumber("");
                        cldeInfoBean.setTechnicsNumber("");
                        cldeInfoBean.setTechnicsName("");
                        cldeInfoBean.setPplanNumber("");
                        cldeInfoBean.setVersion("");
                        cldeInfoBean.setPacketNumber("");
                        cldeInfoBean.setStartTime(cldeStartTime);
                        cldeInfoBean.setPlanTime(cldePlanTime);
                        cldeInfoBean.setEndTime(cldeEndTime);
                        cldeInfoBean.setCompleteState(completeState);
                        cldeInfoBean.setIsOverDate(isOverDate);
                        cldeInfoBeanList.add(cldeInfoBean);
                    }

//                    List<WTDocument> documentList = WTPartUtil.getTechnicsDocumentByPart(part);
//                    if(documentList != null && documentList.size() > 0){
//                        for (WTDocument wtDocument : documentList) {
//                            String docNumber = wtDocument.getNumber();//工艺文件流水号
//                            String docName = wtDocument.getName();//工艺文件名称
//                            String version = wtDocument.getIterationDisplayIdentifier().toString();//工艺文件版本
//                            ibaUtility = new IBAUtility(wtDocument);
//                            String pplanNumber = ibaUtility.getIBAValue("PPNUMBER");//工艺文件编号
//                            ProcessEnvelope proen = null;
//                            QueryResult qrEnvelope = EnvelopeHelper.service.getEnvelopeByMemberObject(wtDocument);
//                            while (qrEnvelope.hasMoreElements()) {
//                                proen = ((EnvelopeMemberLink) qrEnvelope.nextElement()).getProcessEnvelope();
//                                String packetNumber = proen.getNumber();//材料定额签审包编号
//
//                                cldeInfoBean = new CldeInfoBean();
//                                cldeInfoBean.setPartNumber(partNumber);
//                                cldeInfoBean.setPartNumber(partName);
//                                cldeInfoBean.setTechnicsNumber(docNumber);
//                                cldeInfoBean.setTechnicsNumber(docNumber);
//                                cldeInfoBean.setTechnicsName(docName);
//                                cldeInfoBean.setPplanNumber(pplanNumber);
//                                cldeInfoBean.setVersion(version);
//                                cldeInfoBean.setPacketNumber(packetNumber);
//                                cldeInfoBean.setStartTime(cldeStartTime);
//                                cldeInfoBean.setPlanTime(cldePlanTime);
//                                cldeInfoBean.setEndTime(cldeEndTime);
//                                cldeInfoBean.setCompleteState(completeState);
//                                cldeInfoBean.setIsOverDate(isOverDate);
//                                cldeInfoBeanList.add(cldeInfoBean);
//                            }
//                        }
//                    }else{
//                        cldeInfoBean = new CldeInfoBean();
//                        cldeInfoBean.setPartNumber(partNumber);
//                        cldeInfoBean.setPartNumber(partName);
//                        cldeInfoBean.setTechnicsNumber("");
//                        cldeInfoBean.setTechnicsNumber("");
//                        cldeInfoBean.setTechnicsName("");
//                        cldeInfoBean.setPplanNumber("");
//                        cldeInfoBean.setVersion("");
//                        cldeInfoBean.setPacketNumber("");
//                        cldeInfoBean.setStartTime(cldeStartTime);
//                        cldeInfoBean.setPlanTime(cldePlanTime);
//                        cldeInfoBean.setEndTime(cldeEndTime);
//                        cldeInfoBean.setCompleteState(completeState);
//                        cldeInfoBean.setIsOverDate(isOverDate);
//                        cldeInfoBeanList.add(cldeInfoBean);
//                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writeCldeInfo(cldeInfoBeanList);
    }

    private static File writeCldeInfo(List<CldeInfoBean> cldeInfoBeanList) {
        String oPath = templateDir + File.separator + "cldewczthz.xls";
        String tPath = tempDir + File.separator + "材料定额完成状态汇总表.xls";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        FileOutputStream outputStream = null;
        HSSFWorkbook hssfWorkbook = null;
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(xlsFile));
            hssfWorkbook = new HSSFWorkbook(fs);
            HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
            HSSFRow row = null;
            int index = 1;
            for (CldeInfoBean cldeInfoBean : cldeInfoBeanList) {
                row = sheet.createRow(index);
                writeCellValue(row, 0, cldeInfoBean.getPartNumber());
                writeCellValue(row, 1, cldeInfoBean.getPartName());
                writeCellValue(row, 2, cldeInfoBean.getTechnicsNumber());
                writeCellValue(row, 3, cldeInfoBean.getPplanNumber());
                writeCellValue(row, 4, cldeInfoBean.getTechnicsName());
                writeCellValue(row, 5, cldeInfoBean.getVersion());
                writeCellValue(row, 6, cldeInfoBean.getPacketNumber());
                writeCellValue(row, 7, cldeInfoBean.getStartTime());
                writeCellValue(row, 8, cldeInfoBean.getPlanTime());
                writeCellValue(row, 9, cldeInfoBean.getEndTime());
                writeCellValue(row, 10, cldeInfoBean.getCompleteState());
                writeCellValue(row, 11, cldeInfoBean.getIsOverDate());
                index++;
            }
            xlsFile = new File(tPath);
            outputStream = new FileOutputStream(xlsFile);
            hssfWorkbook.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return xlsFile;
    }


    private static void writeCellValue(HSSFRow row, int col, String value) {
        HSSFCell cell = row.createCell(col, HSSFCell.CELL_TYPE_STRING);
        cell.setCellValue(new HSSFRichTextString(value));
    }

    private static Map<String, Map<String, String>> getGSMap(WTPart part) throws WTException {
        String zhunjie = "";
        String danjian = "";
        String label = "";
        List<MPMOperationUsageLink> listOper = null;
        MPMProcessPlan ppplan = null;
        MPMOperationUsageLink link = null;
        MPMOperationMaster master = null;
        MPMOperation operation = null;
        Map<String, String> gsMap = null;
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        List allPPlan = ViewWIHelper.getProcessPlan(part);
        for (Object object : allPPlan) {
            ppplan = (MPMProcessPlan) object;
            gsMap = new HashMap<String, String>();
            listOper = ViewWIHelper.getMPMOperationUsageLinkByMpmPr(ppplan);
            for (int j = 0; j < listOper.size(); j++) {
                link = listOper.get(j);
                label = link.getOperationLabel();
                master = (MPMOperationMaster) link.getRoleBObject();
                operation = ViewWIHelper.getMpmOperation(master.getNumber());
                if (operation != null) {
                    zhunjie = IBAHelper.getIBAStringValue(operation, "ZJGS");
                    danjian = IBAHelper.getIBAStringValue(operation, "DJGS");
                    gsMap.put(label + "_ZJGS", zhunjie);
                    gsMap.put(label + "_DJGS", danjian);
                }
            }
            map.put(ppplan.getNumber(), gsMap);
        }
        return map;
    }

    private static File copyTemplate(String oPath, String tPath) {
        File oFile = new File(oPath);
        if (!oFile.exists()) {
            return null;
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = new FileInputStream(oFile);

            File tFile = new File(tPath);
            fos = new FileOutputStream(tFile);
            byte[] bytes = new byte[1024];
            int length = 0;
            while ((length = is.read(bytes)) != -1) {
                fos.write(bytes, 0, length);
            }
            fos.close();
            is.close();

            return tFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void getAllChildPart(WTPart ppart, List<WTPart> list, Map<String, Integer> butaoNums, Map<String, TreeNode> treeNodes) throws WTException {
        QueryResult qr = WTPartHelper.service.getUsesWTParts(ppart, getDefaultConfigSpec());
        WTPart cpart = null;
        while (qr.hasMoreElements()) {
            Persistable[] per = (Persistable[]) qr.nextElement();
            Persistable pper = per[1];
            if (pper instanceof WTPart) {
                cpart = (WTPart) per[1];
                String viewName = cpart.getViewName();
                if ("Design".equals(viewName)) {
                    cpart = CSCPart.getPartByNumberAndViewName(cpart.getNumber(), "Manufacturing");
                }
            } else if (pper instanceof WTPartMaster) {
                cpart = CSCPart.getPartByNumberAndViewName(((WTPartMaster) pper).getNumber(), "Manufacturing");
            }

            if (cpart != null) {
                if (!list.contains(cpart)) {
                    list.add(cpart);
                }
                TreeNode parent = treeNodes.get(ppart.getNumber());
                TreeNode child = treeNodes.get(cpart.getNumber());
                if (child == null) {
                    TreeNode node = new TreeNode(parent, cpart.getNumber(), null);
                    treeNodes.put(cpart.getNumber(), node);
                } else {
                    child.addP(parent);
                }

                int gysl = getGYSL(ppart, cpart);
                butaoNums.put(ppart.getNumber() + "->" + cpart.getNumber(), gysl);
                getAllChildPart(cpart, list, butaoNums, treeNodes);
            }
        }
    }

    public static void getAllChildPart2(WTPart ppart, Map<WTPart, List<WTPart>> map, Map<String, Integer> butaoNums, Map<String, TreeNode> treeNodes) throws WTException {
        List<WTPart> list = new ArrayList<WTPart>();
        QueryResult qr = WTPartHelper.service.getUsesWTParts(ppart, getDefaultConfigSpec());
        WTPart cpart = null;
        while (qr.hasMoreElements()) {
            Persistable[] per = (Persistable[]) qr.nextElement();
            Persistable pper = per[1];
            if (pper instanceof WTPart) {
                cpart = (WTPart) per[1];
                String viewName = cpart.getViewName();
                if ("Design".equals(viewName)) {
                    cpart = CSCPart.getPartByNumberAndViewName(cpart.getNumber(), "Manufacturing");
                }
            } else if (pper instanceof WTPartMaster) {
                cpart = CSCPart.getPartByNumberAndViewName(((WTPartMaster) pper).getNumber(), "Manufacturing");
            }
            if (cpart != null) {
                list.add(cpart);
                TreeNode parent = treeNodes.get(ppart.getNumber());
                TreeNode child = treeNodes.get(cpart.getNumber());
                if (child == null) {
                    TreeNode node = new TreeNode(parent, cpart.getNumber(), null);
                    treeNodes.put(cpart.getNumber(), node);
                } else {
                    child.addP(parent);
                }

                int gysl = getGYSL(ppart, cpart);
                butaoNums.put(ppart.getNumber() + "->" + cpart.getNumber(), gysl);
                getAllChildPart2(cpart, map, butaoNums, treeNodes);
            }
        }
        map.put(ppart, list);
    }

    public static int getGYSL(WTPart p, WTPart child) throws WTException {
        WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(p, (WTPartMaster) child.getMaster());
        if (link == null) return 0;
        IBAHelper linkHelper = new IBAHelper();
        String ibasysl = linkHelper.getIBAStringValue(link, "GYSL");
        if (ibasysl == null || "0".equals(ibasysl)) {
            return (int) link.getQuantity().getAmount();
        }
        ibasysl = ibasysl.trim();
        return Integer.parseInt(ibasysl);//
    }

    public static void getAllChildPart(WTPart ppart, List<WTPart> list) throws WTException {
        QueryResult qr = WTPartHelper.service.getUsesWTParts(ppart, getDefaultConfigSpec());
        WTPart cpart = null;
        while (qr.hasMoreElements()) {
            Persistable[] per = (Persistable[]) qr.nextElement();
            Persistable pper = per[1];
            if (pper instanceof WTPart) {
                cpart = (WTPart) per[1];
                String viewName = cpart.getViewName();
                if ("Design".equals(viewName)) {
                    cpart = CSCPart.getPartByNumberAndViewName(cpart.getNumber(), "Manufacturing");
                }
            } else if (pper instanceof WTPartMaster) {
                cpart = CSCPart.getPartByNumberAndViewName(((WTPartMaster) pper).getNumber(), "Manufacturing");
            }

            if (cpart != null) {
                if (!list.contains(cpart)) {
                    list.add(cpart);
                }

                getAllChildPart(cpart, list);
            }
        }
    }

    public static void getAllChildPart2(WTPart ppart, Map<WTPart, List<WTPart>> map) throws WTException {
        List<WTPart> list = new ArrayList<WTPart>();
        QueryResult qr = WTPartHelper.service.getUsesWTParts(ppart, getDefaultConfigSpec());
        WTPart cpart = null;
        while (qr.hasMoreElements()) {
            Persistable[] per = (Persistable[]) qr.nextElement();
            Persistable pper = per[1];
            if (pper instanceof WTPart) {
                cpart = (WTPart) per[1];
                String viewName = cpart.getViewName();
                if ("Design".equals(viewName)) {
                    cpart = CSCPart.getPartByNumberAndViewName(cpart.getNumber(), "Manufacturing");
                }
            } else if (pper instanceof WTPartMaster) {
                cpart = CSCPart.getPartByNumberAndViewName(((WTPartMaster) pper).getNumber(), "Manufacturing");
            }
            if (cpart != null) {
                list.add(cpart);
                getAllChildPart2(cpart, map);
            }
        }
        map.put(ppart, list);
    }

    private static String getType(String key) {
        if (key == null || "".equals(key)) {
            return "";
        }
        if (key.startsWith("01")) {
            return "元器件";
        } else if (key.startsWith("02")) {
            return "标准紧固件";
        } else if (key.startsWith("03")) {
            return "金属材料";
        } else if (key.startsWith("04")) {
            return "非金属材料";
        } else if (key.startsWith("05")) {
            return "复合材料";
        } else if (key.startsWith("06")) {
            return "机电材料";
        } else if (key.startsWith("07")) {
            return "火工品";
        } else if (key.startsWith("08")) {
            return "劳防、文办用品";
        } else {
            return key;
        }
    }

    private static ConfigSpec getDefaultConfigSpec() throws WTException {
        return ConfigHelper.service.getDefaultConfigSpecFor(WTPart.class);
    }

    public static File exportPeitaomingxibiao(WTDocument doc) {
        ArrayList<String> titles = buildTitles();
        WTProperties wtp;
        try {
            ArrayList<ArrayList<String>> values = buildValues(doc);
            ExcelFileGenerator gen = new ExcelFileGenerator(titles, values);
            wtp = WTProperties.getLocalProperties();
            String temp = wtp.getProperty("wt.temp");
            File file = new File(temp + File.separator + doc.getNumber() + "配套明细表.xls");
            gen.expordExcel(new FileOutputStream(file));
            return file;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    public static List<SignatureAdviseBean> getSignatureAdviseBeanList(WTObject obj, String workflowType) throws WTException {
        QueryResult qrProcs = null;
        WfProcess proc = null;
        SignatureAdviseBean signatureAdviseBean;
        List<SignatureAdviseBean> signatureAdviseBeanList = null;
        qrProcs = WfEngineHelper.service.getAssociatedProcesses(obj, null, null);
        while (qrProcs.hasMoreElements()) {
            WfProcess process = (WfProcess) qrProcs.nextElement();
            if (proc != null) {
                if (process.getStartTime().after(proc.getStartTime()) && ((process.getName().contains(Constants.WF_149APPROVAL_HUIQIAN) && "signature".equals(workflowType)) || (process.getName().contains(Constants.WF_149ECNPAKAGE) && "change".equals(workflowType)))) {
                    proc = process;
                }
            } else {
                if ((process.getName().contains(Constants.WF_149APPROVAL_HUIQIAN) && "signature".equals(workflowType)) || (process.getName().contains(Constants.WF_149ECNPAKAGE) && "change".equals(workflowType))) {
                    proc = process;
                }
            }
        }
        if (proc != null) {
            signatureAdviseBeanList = new ArrayList<SignatureAdviseBean>();
            Map<WTObject, List<ASESHuiqianSignature>> map = SignatureHelper.getSignatureFromPBO(obj, proc);
            for (Map.Entry<WTObject, List<ASESHuiqianSignature>> entry : map.entrySet()) {
                WTObject object = entry.getKey();
                List<ASESHuiqianSignature> asesHuiqianSignatureList = entry.getValue();
                for (ASESHuiqianSignature asesHuiqianSignature : asesHuiqianSignatureList) {
                    String signatureUser = "";
                    String signatureResult = "";
                    if (asesHuiqianSignature.getConclusion() != null) {
                        signatureUser = asesHuiqianSignature.getConclusion().split(" ")[0];
                        signatureResult = asesHuiqianSignature.getConclusion().split(" ")[1];
                    }
                    String signatureAdvise = asesHuiqianSignature.getOpinion();
                    String number = "";
                    String name = "";
                    String version = "";
                    String state = "";
                    String signatureDataType = "";
                    if (object instanceof WTDocument) {
                        WTDocument doc = (WTDocument) object;
                        number = doc.getNumber();
                        name = doc.getName();
                        version = doc.getIterationDisplayIdentifier().toString();
                        state = doc.getState().getState().getDisplay(Locale.CHINA);
                        signatureDataType = "文档";
                    } else if (object instanceof EPMDocument) {
                        EPMDocument epm = (EPMDocument) object;
                        number = epm.getNumber();
                        name = epm.getName();
                        version = epm.getIterationDisplayIdentifier().toString();
                        state = epm.getState().getState().getDisplay(Locale.CHINA);
                        signatureDataType = "三维模型";
                    } else if (object instanceof ChangePackaged) {
                        ChangePackaged changePackaged = (ChangePackaged) object;
                        number = changePackaged.getNumber();
                        name = changePackaged.getName();
                        state = changePackaged.getState().getState().getDisplay(Locale.CHINA);
                        signatureDataType = "更改单";
                    }
                    Map<String, String> resultMap = getUpdateStateAndImplementadvise(object, obj);
                    String updateState = resultMap.get("updateState");
                    String implementAdvise = resultMap.get("implementAdvise");

                    signatureAdviseBean = new SignatureAdviseBean();
                    signatureAdviseBean.setSignatureUser(signatureUser);
                    signatureAdviseBean.setSignatureDateType(signatureDataType);
                    signatureAdviseBean.setNumber(number);
                    signatureAdviseBean.setName(name);
                    signatureAdviseBean.setVersion(version);
                    signatureAdviseBean.setState(state);
                    signatureAdviseBean.setUpdateState(updateState);
                    signatureAdviseBean.setImplementAdvise(implementAdvise);
                    signatureAdviseBean.setSignatureResult(signatureResult);
                    signatureAdviseBean.setSignatureAdvise(signatureAdvise);
                    signatureAdviseBeanList.add(signatureAdviseBean);
                }
            }
        }
        return signatureAdviseBeanList;
    }

    public static boolean isAllAgreed(String workItemOid) throws WTException {
        boolean flag = Boolean.TRUE;
        String activityName = HuiQianWorkFlowService.getActivityNameByWorkItemOid(workItemOid);
        Map<WTObject, List<ASESHuiqianSignature>> signMap = SignatureHelper.getSignature(workItemOid);
        for (Map.Entry<WTObject, List<ASESHuiqianSignature>> entry : signMap.entrySet()) {
            List<ASESHuiqianSignature> list = entry.getValue();
            for (ASESHuiqianSignature asesHuiqianSignature : list) {
                String conclusion = asesHuiqianSignature.getConclusion();
                System.out.println("11111111111111" + conclusion);
                if (conclusion.contains("不同意")) {
                    flag = Boolean.FALSE;
                    break;
                }
            }
        }
        return flag;
    }

    public static File exportSignatureAdvise(WTObject obj, String workflowType) {
        List<String> titles = getSignatureAdviseTitles();
        String path = PropertiesUtil.getTempPath() + File.separator + "SignatureAdvise_Export.xls";
        java.io.FileOutputStream writeFile = null;
        HSSFWorkbook workbook = null;
        HSSFCellStyle style = null;
        HSSFSheet sheet = null;
        HSSFRow titleRow = null;
        try {
            workbook = new HSSFWorkbook();
            style = workbook.createCellStyle(); // 样式对象
            style.setVerticalAlignment(VerticalAlignment.CENTER); // 使用枚举值
            style.setAlignment(HorizontalAlignment.CENTER);

            sheet = workbook.createSheet("sheet1");
            List<SignatureAdviseBean> signatureAdviseBeanList = getSignatureAdviseBeanList(obj, workflowType);
            if (signatureAdviseBeanList != null) {
                for (int i = 0; i < signatureAdviseBeanList.size() + 1; i++) {
                    titleRow = sheet.createRow(i);
                    if (i == 0) {
                        for (int j = 0; j < titles.size(); j++) {
                            titleRow.createCell(j).setCellValue(titles.get(j));
                        }
                    } else {
                        titleRow.createCell(0).setCellValue(signatureAdviseBeanList.get(i - 1).getSignatureUser());
                        titleRow.createCell(1).setCellValue(signatureAdviseBeanList.get(i - 1).getSignatureDateType());
                        titleRow.createCell(2).setCellValue(signatureAdviseBeanList.get(i - 1).getNumber());
                        titleRow.createCell(3).setCellValue(signatureAdviseBeanList.get(i - 1).getName());
                        titleRow.createCell(4).setCellValue(signatureAdviseBeanList.get(i - 1).getVersion());
                        titleRow.createCell(5).setCellValue(signatureAdviseBeanList.get(i - 1).getState());
                        titleRow.createCell(6).setCellValue(signatureAdviseBeanList.get(i - 1).getUpdateState());
                        titleRow.createCell(7).setCellValue(signatureAdviseBeanList.get(i - 1).getImplementAdvise());
                        titleRow.createCell(8).setCellValue(signatureAdviseBeanList.get(i - 1).getSignatureResult());
                        titleRow.createCell(9).setCellValue(signatureAdviseBeanList.get(i - 1).getSignatureAdvise());
                    }
                }
            }
            writeFile = new java.io.FileOutputStream(path);
            workbook.write(writeFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        } finally {
            if (writeFile != null) {
                try {
                    writeFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new File(path);
    }

    private static List<String> getSignatureAdviseTitles() {
        List<String> titles = new ArrayList<String>();
        titles.add("会签人员");
        titles.add("会签数据类型");
        titles.add("文件编号");
        titles.add("文件名称");
        titles.add("版本");
        titles.add("状态");
        titles.add("更新状态");
        titles.add("落实意见");
        titles.add("会签结论");
        titles.add("会签意见");
        return titles;
    }

    public static Map<String, String> getUpdateStateAndImplementadvise(WTObject object, WTObject pbo) throws WTException {
        Map<String, String> resultMap = new HashMap<String, String>();
        //更新状态
        String updateState;
        //落实意见
        String implementAdvise;
        if (pbo instanceof ProcessEnvelope) {
            ProcessEnvelope envelope = (ProcessEnvelope) pbo;
            QueryResult qr = PersistenceHelper.manager.navigate(envelope, "theProcessEnvelope", EnvelopeMemberLink.class, false);
            while (qr.hasMoreElements()) {
                EnvelopeMemberLink link = (EnvelopeMemberLink) qr.nextElement();
                Persistable roleA = link.getRoleAObject();
                if (object.equals(roleA)) {
                    updateState = link.getDescription();
                    implementAdvise = link.getImplementadvise();
                    resultMap.put("updateState", updateState);
                    resultMap.put("implementAdvise", implementAdvise);
                    break;
                }
            }
        }
        if (pbo instanceof ChangePackaged) {
            ChangePackaged changePackaged = (ChangePackaged) pbo;
            QueryResult qr = PersistenceHelper.manager.navigate(changePackaged, "theRevisionControlled", ChangePackagedResultLink.class, false);
            while (qr.hasMoreElements()) {
                ChangePackagedResultLink link = (ChangePackagedResultLink) qr.nextElement();
                Persistable roleB = link.getRoleBObject();
                if (object.equals(roleB)) {
                    updateState = link.getDescription();
                    implementAdvise = link.getImplementadvise();
                    resultMap.put("updateState", updateState);
                    resultMap.put("implementAdvise", implementAdvise);
                    break;
                }
            }
        }
        return resultMap;
    }

    /**
     * 导出工序配套表 add by liangbo
     *
     * @param doc
     * @return
     */
    public static File exportGongXuPeiTao(WTDocument doc) {
        ArrayList<String> titles = buildGongxuPeiTaoTitles();
        WTProperties wtp;
        try {
            ArrayList<ArrayList<String>> values = buildGongXuPeiTaoValues(doc);
            ExcelFileGenerator gen = new ExcelFileGenerator(titles, values);
            wtp = WTProperties.getLocalProperties();
            String temp = wtp.getProperty("wt.temp");
            File file = new File(temp + File.separator + doc.getNumber() + "工序配套表.xls");
            gen.expordExcel(new FileOutputStream(file));
            return file;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    private static ArrayList<ArrayList<String>> buildValues(WTDocument doc) throws WTException, PropertyVetoException, DocumentException {
        ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();
        Element techEle = BomUtil.getTechincisElement(doc, null, null);
        if (techEle != null) {
            List<Element> listEle = techEle.selectNodes("PEITAOTABLE/PeiTaoElement");
            if (listEle != null && !listEle.isEmpty()) {
                for (Element element : listEle) {
                    ArrayList<String> values = new ArrayList<String>();
                    values.add(element.attributeValue("number"));
                    values.add(element.attributeValue("name"));
                    values.add(element.attributeValue("MTYPE"));
                    values.add(element.attributeValue("useCount"));
                    values.add(element.attributeValue("XHPH"));
                    values.add(element.attributeValue("CSIZE"));
                    values.add(element.attributeValue("jstj"));
                    values.add(element.attributeValue("version"));
                    values.add(element.attributeValue("dw"));
                    values.add(element.attributeValue("comment"));
                    values.add(element.attributeValue("gys"));
                    allList.add(values);
                }

            }
        }
        return allList;
    }

    private static ArrayList<String> buildTitles() {
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("编号");
        titles.add("名称");
        titles.add("零组件生产类型");
        titles.add("工艺数量");
        titles.add("型号牌号");
        titles.add("规格");
        titles.add("技术条件");
        titles.add("版本");
        titles.add("单位");
        titles.add("备注");
        titles.add("来自何处");
        return titles;
    }

    /**
     * 工序配套表列名
     *
     * @return
     */
    private static ArrayList<String> buildGongxuPeiTaoTitles() {
        ArrayList<String> titles = new ArrayList<String>();
        titles.add("工序号");
        titles.add("工步号");
        titles.add("零件编号");
        titles.add("零件名称");
        titles.add("装/拆");
        return titles;
    }

    /**
     * 工序配套表value集
     *
     * @param doc
     * @return
     * @throws WTException
     * @throws PropertyVetoException
     * @throws DocumentException
     */
    private static ArrayList<ArrayList<String>> buildGongXuPeiTaoValues(WTDocument doc) throws WTException, PropertyVetoException, DocumentException {
        ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();
        Element techEle = BomUtil.getTechincisElement(doc, null, null);
        String procedureName = "";
        String paceName = "";
        String partNumber = "";
        String partName = "";
        String zcMark = "";
        ArrayList<String> values = null;
        if (techEle != null) {
            List<Element> proceduresList = XmlUtility.getAllSteps(techEle);
            if (proceduresList != null && !proceduresList.isEmpty()) {
                for (Element procedure : proceduresList) {
                    String stepNumber = procedure.attributeValue("stepNumber");
                    String stepName = procedure.attributeValue("stepName");
                    procedureName = stepNumber + "_" + stepName;
                    Element pacesElement = procedure.element("paces");
                    if (pacesElement != null) {
                        List<Element> pacesList = pacesElement.elements("QMProcedureInfo");
                        if (pacesList != null && !pacesList.isEmpty()) {
                            for (Element pace : pacesList) {
                                paceName = pace.attributeValue("stepNumber");
                                List<Element> partList = pace.selectNodes("parts/QMPartInfo");
                                if (partList != null && !partList.isEmpty()) {
                                    for (Element qmPart : partList) {
                                        values = new ArrayList<String>();
                                        partNumber = qmPart.attributeValue("partNumber");
                                        partName = qmPart.attributeValue("partName");
                                        zcMark = qmPart.attributeValue("ZCMARK");
                                        if (zcMark.equals("C")) {
                                            zcMark = "拆";
                                        }
                                        if (zcMark.equals("Z")) {
                                            zcMark = "装";
                                        }
                                        values.add(procedureName);
                                        values.add(paceName);
                                        values.add(partNumber);
                                        values.add(partName);
                                        values.add(zcMark);
                                        allList.add(values);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return allList;
    }

    public static WTPart getPartByBatch(WTPart part, String batch) {
        WTPart wtpart = null;
        try {
            QueryResult allIterations = VersionControlHelper.service.allVersionsFrom((Versioned) part);
            if (allIterations != null) {
                while (allIterations.hasMoreElements()) {
                    wtpart = (WTPart) allIterations.nextElement();
                    String partBatch = IBAHelper.getIBAStringValue(wtpart, "BATCH");
                    if (partBatch == null) {
                        partBatch = "";
                    }
                    String state2 = wtpart.getState().toString();
                    String v1 = VersionControlHelper.getVersionIdentifier((Versioned) wtpart).getValue();
                    String v2 = VersionControlHelper.getIterationIdentifier((Iterated) wtpart).getValue();
                    System.out.println(wtpart.getNumber() + "," + state2 + "," + v1 + "," + v2);
                    if (partBatch.equals(batch)) {
                        break;
                    } else {
                        wtpart = null;
                    }
                }
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
        } catch (VersionControlException e) {
            e.printStackTrace();
        } catch (WTException e) {
            e.printStackTrace();
        }
        if (wtpart == null) return part;
        return wtpart;
    }


    //    导出工艺参数
    public static File exportGLProcessParams() {
        String oPath = templateDir + File.separator + "gongYiCanShu.xlsx";
        String tPath = tempDir + File.separator + System.currentTimeMillis() + "_工艺参数.xlsx";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        List<GLProcessParams> list = ProcessUtil.queryGLProcessParams("基础参数", "","");
        FileOutputStream outputStream = null;
        try {
            Workbook workbook = new XSSFWorkbook(new FileInputStream(xlsFile));
            CellStyle cellStyle = workbook.createCellStyle(); // 样式对象
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 使用枚举值
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN); // 下边框
            cellStyle.setBorderLeft(BorderStyle.THIN); // 左边框
            cellStyle.setBorderRight(BorderStyle.THIN); // 右边框

            exportSheet4Paras(workbook, cellStyle, list, 0);
            list = ProcessUtil.queryGLProcessParams("枚举参数", "","");
            exportSheet4Paras(workbook, cellStyle, list, 1);
            list = ProcessUtil.queryGLProcessParams("知识参数", "","");
            exportSheet4Paras(workbook, cellStyle, list, 2);

            xlsFile = new File(tPath);
            outputStream = new FileOutputStream(xlsFile);
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new RuntimeException("Error while exporting GLProcessParams.", e);
        } finally {
            try {
                outputStream.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return xlsFile;
    }

    /**
     * @return
     */
    public static File exportGLProcessKnowledge() {
        String oPath = templateDir + File.separator + "gongYiZhiShi.xlsx";
        String tPath = tempDir + File.separator + System.currentTimeMillis() + "_工艺知识.xlsx";

        File xlsFile = copyTemplate(oPath, tPath);
        if (xlsFile == null) {
            return null;
        }
        List<GLProcessLink> glProcessLinks = ProcessUtil.queryGLProcessLink();
        FileOutputStream outputStream = null;
        try {
            Workbook workbook = new XSSFWorkbook(new FileInputStream(xlsFile));
            CellStyle cellStyle = workbook.createCellStyle();             // 样式对象
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 使用枚举值
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);             // 下边框
            cellStyle.setBorderLeft(BorderStyle.THIN);               // 左边框
            cellStyle.setBorderRight(BorderStyle.THIN);              // 右边框

            exportSheet4Knowledge(workbook, cellStyle, glProcessLinks);
            exportSheet4ProcessTemplateLink(workbook, cellStyle);
            exportSheet4ProcessStepTemplateLink(workbook, cellStyle);

            xlsFile = new File(tPath);
            outputStream = new FileOutputStream(xlsFile);
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while exporting GLProcessParams.", e);
        } finally {
            try {
            	if(outputStream!=null){
            		outputStream.close();
            	}
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return xlsFile;
    }


    /**
     * 工艺模板映射
     *
     * @param workbook
     * @param cellStyle
     */
    private static void exportSheet4ProcessTemplateLink(Workbook workbook, CellStyle cellStyle) {
        List<GLProcessTemplateLink> list = ProcessUtil.queryGLProcessTemplateLink();
        int size = list.size();

        Sheet sheet = workbook.getSheet("工艺模板映射");
        if (null == sheet) {
            sheet = workbook.createSheet("工艺模板映射");
        }
        for (int i = 0; i < size; i++) {
            Row row = sheet.createRow(i + 1);  //从第二行写数据
            GLProcessTemplateLink linki = list.get(i);
            for (int j = 0; j < 7; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                switch (j) {
                    case 0:
                        cell.setCellValue(linki.getXuHao());
                        break;
                    case 1:
                        cell.setCellValue(linki.getCaiLiaoFenLei());
                        break;
                    case 2:
                        cell.setCellValue(linki.getYaXiaXian());
                        break;
                    case 3:
                        cell.setCellValue(linki.getReChuLi());
                        break;
                    case 4:
                        cell.setCellValue(linki.getBiaoMianChuLi());
                        break;
                    case 5:
                        cell.setCellValue(linki.getProcessTemplate());
                        break;
                    case 6:
                        cell.setCellValue(linki.getTemplateNumber());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 工序模板映射
     *
     * @param workbook
     * @param cellStyle
     */

    private static void exportSheet4ProcessStepTemplateLink(Workbook workbook, CellStyle cellStyle) {
        List<GLProcessStepTemplateLink> allList = ProcessUtil.queryGLProcessStepTemplateLink();

        Map<String, List<GLProcessStepTemplateLink>> listMap = ProcessUtil.splitLinkByStepName(allList);

        Sheet sheet = workbook.getSheet("工序模板映射");
        if (null == sheet) {
            sheet = workbook.createSheet("工序模板映射");
        }
        int hang = 0;
        String stepName = "";
        for (String stepNamei : listMap.keySet()) {
            if (!StringUtils.equals(stepName, stepNamei)) {
                stepName = stepNamei;
                Row row = sheet.createRow(hang);
                Cell indexCell = row.createCell(0);
                indexCell.setCellStyle(cellStyle);
                indexCell.setCellValue(stepName); // 序号从1开始
                hang++;
//                写表头
                addTitle(cellStyle, stepNamei, sheet, hang);
                hang++;
            }
            List<GLProcessStepTemplateLink> listi = listMap.get(stepName);
            int size = listi.size();
            for (int i = 0; i < size; i++) {
                Row row = sheet.createRow(hang++);
                GLProcessStepTemplateLink linki = listi.get(i);
                for (int j = 0; j < 4; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(cellStyle);
                    switch (j) {
                        case 0:
                            cell.setCellValue(linki.getXuHao());
                            break;
                        case 1:
                            cell.setCellValue(linki.getCaiLiaoFenLei());
                            break;
                        case 2:
                            if ("表面处理".equals(stepNamei)) {
                                cell.setCellValue(linki.getBiaoMianChuLi());
                            } else {
                                cell.setCellValue(linki.getGuiGe());
                            }
                            break;
                        case 3:
                            cell.setCellValue(linki.getProcessStepTemplate());
                            break;
                        case 4:
                            cell.setCellValue(linki.getTemplateNumber());
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * Adds a title to the specified sheet in the given workbook.
     *
     * @param cellStyle the cell style to apply to the title cells
     * @param stepNamei the name of the step
     * @param sheet     the sheet to add the title to
     * @param hang      the row number to add the title to
     */
    private static void addTitle(CellStyle cellStyle, String stepNamei, Sheet sheet, int hang) {
        Row row;
        Cell indexCell;
        cellStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
        row = sheet.createRow(hang);
        indexCell = row.createCell(0);
        indexCell.setCellStyle(cellStyle);
        indexCell.setCellValue("序号");

        indexCell = row.createCell(1);
        indexCell.setCellStyle(cellStyle);
        indexCell.setCellValue("材料分类");

        indexCell = row.createCell(2);
        indexCell.setCellStyle(cellStyle);
        if ("表面处理".equals(stepNamei)) {
            indexCell.setCellValue("表面处理类型");
        } else {
            indexCell.setCellValue("规格");
        }

        indexCell = row.createCell(3);
        indexCell.setCellStyle(cellStyle);
        indexCell.setCellValue("工序模板名称");

        indexCell = row.createCell(4);
        indexCell.setCellStyle(cellStyle);
        indexCell.setCellValue("工序模板编号");
    }

    private static void exportSheet4Knowledge(Workbook workbook, CellStyle cellStyle, List<GLProcessLink> glProcessLinks) {
        int size = glProcessLinks.size();
        Sheet sheeti = null;
        String heads = "";
        String[] headArray = null;
        Cell headCell = null;
        for (int i = 0; i < size; i++) {
            GLProcessLink linki = glProcessLinks.get(i);
//            生成每个sheet
            sheeti = workbook.getSheet(linki.getParaName());
            if (null == sheeti) {
                sheeti = workbook.createSheet(linki.getParaName());
            }
            heads = linki.getHeads();
            headArray = StringUtils.split(heads, ",");
            //获取此sheet关联的GLProcessKnowledge
            List<GLProcessKnowledge> knowledges = ProcessUtil.queryGLProcessKnowledge(linki.getParaName());
            Row row = sheeti.createRow(0);
            //只写表头
            for (int j = 0; j < headArray.length; j++) {
                headCell = row.createCell(j);
                headCell.setCellStyle(cellStyle);
                headCell.setCellValue(headArray[j]); // 序号从1开始
            }

            for (int j = 0; j < knowledges.size(); j++) {
                GLProcessKnowledge knowledgei = knowledges.get(j);
                row = sheeti.createRow(j + 1);
                for (int c = 0; c < 10; c++) {
                    Cell cell = row.createCell(c);
                    cell.setCellStyle(cellStyle);
                    switch (c) {
                        case 0:
                            cell.setCellValue(knowledgei.getXuHao());
                            break;
                       /* case 1:
                            cell.setCellValue(knowledgei.getState());
                            break;*/
                        case 1:
                            if (StringUtils.isNotEmpty(knowledgei.getColumn1())) {
                                cell.setCellValue(knowledgei.getColumn1());
                            } else {
                                row.removeCell(cell);
                            }
                            break;
                        case 2:
                            if (StringUtils.isNotEmpty(knowledgei.getColumn2())) {
                                cell.setCellValue(knowledgei.getColumn2());
                            } else {
                                row.removeCell(cell);
                            }
                            break;
                        case 3:
                            if (StringUtils.isNotEmpty(knowledgei.getColumn3())) {
                                cell.setCellValue(knowledgei.getColumn3());
                            } else {
                                row.removeCell(cell);
                            }
                            break;
                        case 4:
                            if (StringUtils.isNotEmpty(knowledgei.getColumn4())) {
                                cell.setCellValue(knowledgei.getColumn4());
                            } else {
                                row.removeCell(cell);
                            }
                            break;
                        case 5:
                            if (StringUtils.isNotEmpty(knowledgei.getColumn5())) {
                                cell.setCellValue(knowledgei.getColumn5());
                            } else {
                                row.removeCell(cell);
                            }
                            break;
                        case 6:
                            if (StringUtils.isNotEmpty(knowledgei.getColumn6())) {
                                cell.setCellValue(knowledgei.getColumn6());
                            } else {
                                row.removeCell(cell);
                            }
                            break;
                        case 7:
                            if (StringUtils.isNotEmpty(knowledgei.getColumn7())) {
                                cell.setCellValue(knowledgei.getColumn7());
                            } else {
                                row.removeCell(cell);
                            }
                            break;
                        case 8:
                            if (StringUtils.isNotEmpty(knowledgei.getColumn8())) {
                                cell.setCellValue(knowledgei.getColumn8());
                            } else {
                                row.removeCell(cell);
                            }
                            break;
                        default:
                            row.removeCell(cell);
                            break;
                    }
                }
            }
        }
    }

    private static void exportSheet4Paras(Workbook workbook, CellStyle cellStyle, List<GLProcessParams> list, int sheetNo) {
        Sheet jichuSheet = workbook.getSheetAt(sheetNo);
        int size = list.size();
        for (int i = 0; i < size; i++) {
            Row row = jichuSheet.createRow(i + 1);
            // 添加序号列
            Cell indexCell = row.createCell(0);
            indexCell.setCellStyle(cellStyle);
            indexCell.setCellValue(i + 1); // 序号从1开始

            GLProcessParams processParams = list.get(i);
            for (int j = 1; j < 9; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                switch (j) {
                    case 1:
                        cell.setCellValue(processParams.getGyNumber());
                        break;
                    case 2:
                        cell.setCellValue(processParams.getGyName());
                        break;
                    case 3:
                        cell.setCellValue(processParams.getParameterCategory());
                        break;
                    case 4:
                        cell.setCellValue(processParams.getProcessCategory());
                        break;
                    case 5:
                        if (0 == sheetNo) {
                            cell.setCellValue(processParams.getUnit());
                            break;
                        } else if (1 == sheetNo) {
                            cell.setCellValue(processParams.getEnumValues());
                            break;
                        } else if (2 == sheetNo) {
                            cell.setCellValue(processParams.getKnowledgeInferencePara());
                            break;
                        }
                        break;
                    case 6:
                        if (0 == sheetNo) {
                            cell.setCellValue("增加(修改)");
                            break;
                        }
                        if (1 == sheetNo) {
                            cell.setCellValue(processParams.getOutputRules());
                        } else if (2 == sheetNo) {
                            cell.setCellValue(processParams.getKnowledgeOutputPara());
                        }
                        break;
                    case 7:
                        if (1 == sheetNo) {
                            cell.setCellValue("增加(修改)");
                        } else if (2 == sheetNo) {
                            cell.setCellValue(processParams.getOutputRules());
                        }
                        break;
                    case 8:
                        if (2 == sheetNo) {
                            cell.setCellValue("增加(修改)");
                        }
                        break;
                }
            }
        }
    }

	public static File exportGLProcessOtherKnowledge() {
		String oPath = templateDir + File.separator + "ProcessOtherKnowledge.xlsx";
		return new File(oPath);
	}


}
