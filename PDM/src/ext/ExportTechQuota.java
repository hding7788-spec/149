package ext;

import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import ext.casc.integrate.util.ZipUtil;
import ext.casc.sop.util.QueryUtil;
import ext.casc.util.SoftTypeUtil;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jdom.Element;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pdmlink.PDMLinkProduct;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTProperties;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: SAST-149-PDM
 * @description: 按产品导出工艺定额
 * @author: MChen
 * @create: 2020-12-22 10:07
 */
public class ExportTechQuota implements RemoteAccess {

    public static void main(String[] args) {
        if (!RemoteMethodServer.ServerFlag) {
            try {
                RemoteMethodServer server = RemoteMethodServer.getDefault();
                server.setUserName("wcadmin");
                server.setPassword("Admin@149.941");
                String method = "";
                Class<?>[] types = null;
                Object[] vals = null;
                method = "exportExcel";
                types = new Class<?>[]{};
                vals = new Object[]{};
                if (types != null && vals != null) {
                    server.invoke(method, ExportTechQuota.class.getName(), null, types, vals);
                } else {
                    exportExcel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 导出excel
     */
    public static void exportExcel(String oid) throws Exception {
        System.out.println("----start-------");
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        InputStream inputStream = null;
        SWXMLUtil xmlUtil = null;
        try {
            WTProperties pro = WTProperties.getLocalProperties();
            String tec_temp_dir = pro.getProperty("wt.temp");
            Map<Long, String> containerList = getContainerList(oid);
            ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
            for (Long s : containerList.keySet()) {
                String containName = containerList.get(s);
                //创建Excel文件薄
                XSSFWorkbook workbook = new XSSFWorkbook();
                //创建工作表sheeet
                Sheet sheet = workbook.createSheet(containName);
                //创建第一行
                Row row = sheet.createRow(0);
                String[] title = {"工艺文件OID", "工艺编号", "工艺文件编号","工艺名称", "版本", "创建者", "部门", "工艺类型", "工艺类别", "存货编码(wzbm)", "存货名称(wzmc)", "型号牌号(xhph)", "规格(gg)", "技术条件(jstj)", "生产厂家(sccj)","主计量单位(zjldw)", "附加条件(fjtj)",
                        "供应状态/热处理(gyztrcl)","质量等级(zldj)",
                        "单位(dw)", "封装形式(fzxs)", "精度等级(jddj)", "螺纹规格/公称尺寸(lwgg)", "机械性能等级(jxxndj)", "电参考特选要求(dcstxyq)","备注(comment)"};
                Cell cell = null;
                for (int i = 0; i < title.length; i++) {
                    cell = row.createCell(i);
                    cell.setCellValue(title[i]);
                }
                int count = 1;
                containName= new String(containName.getBytes("GBK"),System.getProperty("sun.jnu.encoding"));
                System.out.println("=====containName====" + containName);
                for (TypeIdentifier ti : list) {
                    SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
                    String type = ti.toString().substring(7);
                    TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
                    long typeId = 0;
                    if (tdr != null) {
                        typeId = tdr.getKey().getBranchId();
                    }
                    QuerySpec qs = new QuerySpec(WTDocument.class);

                    qs.appendWhere(new SearchCondition(WTDocument.class,
                                    "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
                            new int[]{0});
                    qs.appendAnd();
                    qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[]{0});
                    qs.appendAnd();
                    qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED", true));
                    qs.appendAnd();
                    qs.appendWhere(new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL, s), new int[]{0});
                    QueryResult qr = PersistenceHelper.manager.find(qs);
                    while (qr.hasMoreElements()) {
                        WTDocument document = (WTDocument) qr.nextElement();
                        boolean checkedOut = WorkInProgressHelper.isCheckedOut((Workable) document);
                        if (!checkedOut) {
                            String tecNumber = document.getNumber();
                            System.out.println("==tecNumber=="+tecNumber);
                            //定义字段
                            String techOid = document.getPersistInfo().getObjectIdentifier().getStringValue();
                            String technicsNum="";
                            String techName = "";
                            String version = "";
                            String creator = "";
                            String dept = "";
                            String techLx = "";
                            String techLb = "";
                            ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
                            if (data == null) {
                                continue;
                            }
                            String tempPath = java.util.UUID.randomUUID().toString();
                            String tecFilePath = tec_temp_dir + File.separator + tempPath + File.separator + tecNumber;
                            byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
                            ZipUtil.unZip(bytes, tecFilePath);
                            String xmlPath = tec_temp_dir + File.separator + tempPath + File.separator + tecNumber + File.separator + tecNumber + ".xml";
                            File xmlFile = new File(xmlPath);
                            if (xmlFile.exists()) {
                                String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
                                inputStream = new FileInputStream(xmlFile);
                                xmlUtil = new SWXMLUtil(inputStream);
                                Element rootElement = xmlUtil.getRootElement();
                                List<Element> qmFawTechnicsInfo = rootElement.getChildren("QMFawTechnicsInfo");
                                if (qmFawTechnicsInfo != null&&qmFawTechnicsInfo.size()>0) {
                                    Element o = qmFawTechnicsInfo.get(0);
                                    creator =getStringData(o.getAttributeValue("creatorDisplay"));
                                    technicsNum =getStringData(o.getAttributeValue("pplanNumber"));
                                    techName =getStringData(o.getAttributeValue("technicsName"));
                                    version =getStringData(o.getAttributeValue("version"));
                                    dept =getStringData(o.getAttributeValue("DEPT"));
                                    techLx =getStringData(o.getAttributeValue("technicsType"));
                                    techLb =getStringData(o.getAttributeValue("PPLANTYPE"));
                                }
                                for (Element ele : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
                                    for (Element cldeEle : (List<Element>) ele.getChildren("CLDE")) {
                                        //原材料定额
                                        for (Element ycldeEle : (List<Element>) cldeEle.getChildren("YCLDE")) {
                                            for (Element ycldeRecordEle : (List<Element>) ycldeEle.getChildren("ycldeRecord")) {
                                                String wzbm =getStringData(ycldeRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(ycldeRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(ycldeRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(ycldeRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(ycldeRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(ycldeRecordEle.getAttributeValue("sccj"));
                                                String zjldw =getStringData(ycldeRecordEle.getAttributeValue("zjldw"));
                                                String fjtj =getStringData(ycldeRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(ycldeRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(ycldeRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(ycldeRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(ycldeRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(ycldeRecordEle.getAttributeValue("jddj"));
                                                String lwgg =getStringData(ycldeRecordEle.getAttributeValue("lwgg"));
                                                String jxxndj =getStringData(ycldeRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(ycldeRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(ycldeRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                        //原材料定额
                                        for (Element zycldeEle : (List<Element>) cldeEle.getChildren("ZYCLDE")) {
                                            for (Element zycldeRecordEle : (List<Element>) zycldeEle.getChildren("zycldeRecord")) {
                                                String wzbm =getStringData(zycldeRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(zycldeRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(zycldeRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(zycldeRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(zycldeRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(zycldeRecordEle.getAttributeValue("sccj"));
                                                String zjldw =getStringData(zycldeRecordEle.getAttributeValue("zjldw"));
                                                String fjtj =getStringData(zycldeRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(zycldeRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(zycldeRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(zycldeRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(zycldeRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(zycldeRecordEle.getAttributeValue("jddj"));
                                                String lwgg =getStringData(zycldeRecordEle.getAttributeValue("lwgg"));
                                                String jxxndj =getStringData(zycldeRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(zycldeRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(zycldeRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                        //试件原材料
                                        for (Element sjycldeEle : (List<Element>) cldeEle.getChildren("SJYCLDE")) {
                                            for (Element sjycldeRecordEle : (List<Element>) sjycldeEle.getChildren("sjycldeRecord")) {
                                                String wzbm =getStringData(sjycldeRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(sjycldeRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(sjycldeRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(sjycldeRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(sjycldeRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(sjycldeRecordEle.getAttributeValue("sccj"));
                                                String zjldw =getStringData(sjycldeRecordEle.getAttributeValue("zjldw"));
                                                String fjtj =getStringData(sjycldeRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(sjycldeRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(sjycldeRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(sjycldeRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(sjycldeRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(sjycldeRecordEle.getAttributeValue("jddj"));
                                                String lwgg =getStringData(sjycldeRecordEle.getAttributeValue("lwgg"));
                                                String jxxndj =getStringData(sjycldeRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(sjycldeRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(sjycldeRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                    }

                                    //工艺定额
                                    for (Element gydeEle : (List<Element>) ele.getChildren("GYDE")) {
                                        //主要材料定额
                                        for (Element zycldeEle : (List<Element>) gydeEle.getChildren("ZYCLDE")) {
                                            for (Element zyclderecordEle : (List<Element>) zycldeEle.getChildren("zycldeRecord")) {
                                                String wzbm =getStringData(zyclderecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(zyclderecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(zyclderecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(zyclderecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(zyclderecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(zyclderecordEle.getAttributeValue("sccj"));
                                                String zjldw =getStringData(zyclderecordEle.getAttributeValue("zjldw"));
                                                String fjtj =getStringData(zyclderecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(zyclderecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(zyclderecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(zyclderecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(zyclderecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(zyclderecordEle.getAttributeValue("jddj"));
                                                String lwgg =getStringData(zyclderecordEle.getAttributeValue("lwgg"));
                                                String jxxndj =getStringData(zyclderecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(zyclderecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(zyclderecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                        //匹配
                                        for (Element matchpartEle : (List<Element>) gydeEle.getChildren("MATCHPART")) {
                                            for (Element matchpartRecordEle : (List<Element>) matchpartEle.getChildren("MatchPart")) {
                                                String wzbm =getStringData(matchpartRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(matchpartRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(matchpartRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(matchpartRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(matchpartRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(matchpartRecordEle.getAttributeValue("sccj"));
                                                String zjldw ="";
                                                String fjtj =getStringData(matchpartRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(matchpartRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(matchpartRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(matchpartRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(matchpartRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(matchpartRecordEle.getAttributeValue("jddj"));
                                                String lwgggccc =getStringData(matchpartRecordEle.getAttributeValue("lwgggccc"));
                                                String jxxndj =getStringData(matchpartRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(matchpartRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(matchpartRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgggccc, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                        //新增
                                        for (Element newpartEle : (List<Element>) gydeEle.getChildren("NEWPART")) {
                                            for (Element newpartRecordEle : (List<Element>) newpartEle.getChildren("NewPart")) {
                                                String wzbm =getStringData(newpartRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(newpartRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(newpartRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(newpartRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(newpartRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(newpartRecordEle.getAttributeValue("sccj"));
                                                String zjldw ="";
                                                String fjtj =getStringData(newpartRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(newpartRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(newpartRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(newpartRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(newpartRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(newpartRecordEle.getAttributeValue("jddj"));
                                                String lwgggccc =getStringData(newpartRecordEle.getAttributeValue("lwgggccc"));
                                                String jxxndj =getStringData(newpartRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(newpartRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(newpartRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgggccc, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }


                                        //试件原材料
                                        for (Element sjycldeEle : (List<Element>) gydeEle.getChildren("SJYCLDE")) {
                                            for (Element sjycldeRecordEle : (List<Element>) sjycldeEle.getChildren("sjycldeRecord")) {
                                                String wzbm =getStringData(sjycldeRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(sjycldeRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(sjycldeRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(sjycldeRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(sjycldeRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(sjycldeRecordEle.getAttributeValue("sccj"));
                                                String zjldw =getStringData(sjycldeRecordEle.getAttributeValue("zjldw"));
                                                String fjtj =getStringData(sjycldeRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(sjycldeRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(sjycldeRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(sjycldeRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(sjycldeRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(sjycldeRecordEle.getAttributeValue("jddj"));
                                                String lwgg =getStringData(sjycldeRecordEle.getAttributeValue("lwgg"));
                                                String jxxndj =getStringData(sjycldeRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(sjycldeRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(sjycldeRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }

                                        //资源库匹配
                                        for (Element matchpartEle : (List<Element>) gydeEle.getChildren("SJZYKMATCHPART")) {
                                            for (Element matchpartRecordEle : (List<Element>) matchpartEle.getChildren("SjzykMatchPart")) {
                                                String wzbm =getStringData(matchpartRecordEle.getAttributeValue("sjbm"));
                                                String wzmc =getStringData(matchpartRecordEle.getAttributeValue("name"));
                                                String xhph =getStringData(matchpartRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(matchpartRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(matchpartRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(matchpartRecordEle.getAttributeValue("sccj"));
                                                String zjldw ="";
                                                String fjtj =getStringData(matchpartRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(matchpartRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(matchpartRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(matchpartRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(matchpartRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(matchpartRecordEle.getAttributeValue("jddj"));
                                                String lwgggccc =getStringData(matchpartRecordEle.getAttributeValue("lwgggccc"));
                                                String jxxndj =getStringData(matchpartRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(matchpartRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(matchpartRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgggccc, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                        //资源库新增
                                        for (Element newpartEle : (List<Element>) gydeEle.getChildren("SJZYKNEWPART")) {
                                            for (Element newpartRecordEle : (List<Element>) newpartEle.getChildren("SjzykNewPart")) {
                                                String wzbm =getStringData(newpartRecordEle.getAttributeValue("sjbm"));
                                                String wzmc =getStringData(newpartRecordEle.getAttributeValue("name"));
                                                String xhph =getStringData(newpartRecordEle.getAttributeValue("ph"));
                                                String gg =getStringData(newpartRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(newpartRecordEle.getAttributeValue("bzh"));
                                                String sccj =getStringData(newpartRecordEle.getAttributeValue("gys"));
                                                String zjldw ="";
                                                String fjtj =getStringData(newpartRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(newpartRecordEle.getAttributeValue("rcl"));
                                                String zldj =getStringData(newpartRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(newpartRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(newpartRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(newpartRecordEle.getAttributeValue("jddj"));
                                                String lwgggccc =getStringData(newpartRecordEle.getAttributeValue("lwgggccc"));
                                                String jxxndj =getStringData(newpartRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(newpartRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(newpartRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgggccc, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }

                                    }
                                }

                            }


                        }
                    }
                }

                //创建一个文件
                File file = new File(PropertiesUtil.getWTHome() + File.separator + "exportTechQuota" + File.separator + "exportTechQuota_" + containName + ".xlsx");
                file.createNewFile();
                FileOutputStream stream = FileUtils.openOutputStream(file);
                workbook.write(stream);
                stream.close();

            }
            System.out.println("----end-------");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }

    /**
     * 导出excel
     */
    public static void exportExcel() throws Exception {
        System.out.println("--------start-------");
        if (!RemoteMethodServer.ServerFlag) {
            SessionHelper.manager.setPrincipal("Administrator");
            RemoteMethodServer ms = RemoteMethodServer.getDefault();
            ms.setUserName("wcadmin");
            ms.setPassword("Admin@149.941");
            try {
                ms.invoke("exportExcel", ExportTechQuota.class.getName(), null,
                        new Class[]{},
                        new Object[]{});
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        InputStream inputStream = null;
        SWXMLUtil xmlUtil = null;
        try {
            WTProperties pro = WTProperties.getLocalProperties();
            String tec_temp_dir = pro.getProperty("wt.temp");
            Map<Long, String> containerList = getContainerList();
            ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
            for (Long s : containerList.keySet()) {
                String containName = containerList.get(s);
                //创建Excel文件薄
                XSSFWorkbook workbook = new XSSFWorkbook();
                //创建工作表sheeet
                Sheet sheet = workbook.createSheet(containName);
                //创建第一行
                Row row = sheet.createRow(0);
                String[] title = {"工艺文件OID", "工艺编号", "工艺文件编号","工艺名称", "版本", "创建者", "部门", "工艺类型", "工艺类别", "存货编码(wzbm)", "存货名称(wzmc)", "型号牌号(xhph)", "规格(gg)", "技术条件(jstj)", "生产厂家(sccj)","主计量单位(zjldw)", "附加条件(fjtj)",
                        "供应状态/热处理(gyztrcl)","质量等级(zldj)",
                        "单位(dw)", "封装形式(fzxs)", "精度等级(jddj)", "螺纹规格/公称尺寸(lwgg)", "机械性能等级(jxxndj)", "电参考特选要求(dcstxyq)","备注(comment)"};
                Cell cell = null;
                for (int i = 0; i < title.length; i++) {
                    cell = row.createCell(i);
                    cell.setCellValue(title[i]);
                }
                int count = 1;
                containName= new String(containName.getBytes("GBK"),System.getProperty("sun.jnu.encoding"));
                System.out.println("=====containName====" + containName);
                for (TypeIdentifier ti : list) {
                    SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
                    String type = ti.toString().substring(7);
                    TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
                    long typeId = 0;
                    if (tdr != null) {
                        typeId = tdr.getKey().getBranchId();
                    }
                    QuerySpec qs = new QuerySpec(WTDocument.class);

                    qs.appendWhere(new SearchCondition(WTDocument.class,
                                    "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
                            new int[]{0});
                    qs.appendAnd();
                    qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[]{0});
                    qs.appendAnd();
                    qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED", true));
                    qs.appendAnd();
                    qs.appendWhere(new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL, s), new int[]{0});
                    QueryResult qr = PersistenceHelper.manager.find(qs);
                    while (qr.hasMoreElements()) {
                        WTDocument document = (WTDocument) qr.nextElement();
                        boolean checkedOut = WorkInProgressHelper.isCheckedOut((Workable) document);
                        if (!checkedOut) {
                            String tecNumber = document.getNumber();
                            System.out.println("==tecNumber=="+tecNumber);
                            //定义字段
                            String techOid = document.getPersistInfo().getObjectIdentifier().getStringValue();
                            String technicsNum="";
                            String techName = "";
                            String version = "";
                            String creator = "";
                            String dept = "";
                            String techLx = "";
                            String techLb = "";
                            ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
                            if (data == null) {
                                continue;
                            }
                            String tempPath = java.util.UUID.randomUUID().toString();
                            String tecFilePath = tec_temp_dir + File.separator + tempPath + File.separator + tecNumber;
                            byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
                            ZipUtil.unZip(bytes, tecFilePath);
                            String xmlPath = tec_temp_dir + File.separator + tempPath + File.separator + tecNumber + File.separator + tecNumber + ".xml";
                            File xmlFile = new File(xmlPath);
                            if (xmlFile.exists()) {
                                String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
                                inputStream = new FileInputStream(xmlFile);
                                xmlUtil = new SWXMLUtil(inputStream);
                                Element rootElement = xmlUtil.getRootElement();
                                List<Element> qmFawTechnicsInfo = rootElement.getChildren("QMFawTechnicsInfo");
                                if (qmFawTechnicsInfo != null&&qmFawTechnicsInfo.size()>0) {
                                    Element o = qmFawTechnicsInfo.get(0);
                                    creator =getStringData(o.getAttributeValue("creatorDisplay"));
                                    technicsNum =getStringData(o.getAttributeValue("pplanNumber"));
                                    techName =getStringData(o.getAttributeValue("technicsName"));
                                    version =getStringData(o.getAttributeValue("version"));
                                    dept =getStringData(o.getAttributeValue("DEPT"));
                                    techLx =getStringData(o.getAttributeValue("technicsType"));
                                    techLb =getStringData(o.getAttributeValue("PPLANTYPE"));
                                }
                                for (Element ele : (List<Element>) rootElement.getChildren("QMFawTechnicsInfo")) {
                                    for (Element cldeEle : (List<Element>) ele.getChildren("CLDE")) {
                                        //原材料定额
                                        for (Element ycldeEle : (List<Element>) cldeEle.getChildren("YCLDE")) {
                                            for (Element ycldeRecordEle : (List<Element>) ycldeEle.getChildren("ycldeRecord")) {
                                                String wzbm =getStringData(ycldeRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(ycldeRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(ycldeRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(ycldeRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(ycldeRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(ycldeRecordEle.getAttributeValue("sccj"));
                                                String zjldw =getStringData(ycldeRecordEle.getAttributeValue("zjldw"));
                                                String fjtj =getStringData(ycldeRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(ycldeRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(ycldeRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(ycldeRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(ycldeRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(ycldeRecordEle.getAttributeValue("jddj"));
                                                String lwgg =getStringData(ycldeRecordEle.getAttributeValue("lwgg"));
                                                String jxxndj =getStringData(ycldeRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(ycldeRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(ycldeRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                        //原材料定额
                                        for (Element zycldeEle : (List<Element>) cldeEle.getChildren("ZYCLDE")) {
                                            for (Element zycldeRecordEle : (List<Element>) zycldeEle.getChildren("zycldeRecord")) {
                                                String wzbm =getStringData(zycldeRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(zycldeRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(zycldeRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(zycldeRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(zycldeRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(zycldeRecordEle.getAttributeValue("sccj"));
                                                String zjldw =getStringData(zycldeRecordEle.getAttributeValue("zjldw"));
                                                String fjtj =getStringData(zycldeRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(zycldeRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(zycldeRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(zycldeRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(zycldeRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(zycldeRecordEle.getAttributeValue("jddj"));
                                                String lwgg =getStringData(zycldeRecordEle.getAttributeValue("lwgg"));
                                                String jxxndj =getStringData(zycldeRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(zycldeRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(zycldeRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                        //试件原材料
                                        for (Element sjycldeEle : (List<Element>) cldeEle.getChildren("SJYCLDE")) {
                                            for (Element sjycldeRecordEle : (List<Element>) sjycldeEle.getChildren("sjycldeRecord")) {
                                                String wzbm =getStringData(sjycldeRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(sjycldeRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(sjycldeRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(sjycldeRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(sjycldeRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(sjycldeRecordEle.getAttributeValue("sccj"));
                                                String zjldw =getStringData(sjycldeRecordEle.getAttributeValue("zjldw"));
                                                String fjtj =getStringData(sjycldeRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(sjycldeRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(sjycldeRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(sjycldeRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(sjycldeRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(sjycldeRecordEle.getAttributeValue("jddj"));
                                                String lwgg =getStringData(sjycldeRecordEle.getAttributeValue("lwgg"));
                                                String jxxndj =getStringData(sjycldeRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(sjycldeRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(sjycldeRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                    }

                                    //工艺定额
                                    for (Element gydeEle : (List<Element>) ele.getChildren("GYDE")) {
                                        //主要材料定额
                                        for (Element zycldeEle : (List<Element>) gydeEle.getChildren("ZYCLDE")) {
                                            for (Element zyclderecordEle : (List<Element>) zycldeEle.getChildren("zycldeRecord")) {
                                                String wzbm =getStringData(zyclderecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(zyclderecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(zyclderecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(zyclderecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(zyclderecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(zyclderecordEle.getAttributeValue("sccj"));
                                                String zjldw =getStringData(zyclderecordEle.getAttributeValue("zjldw"));
                                                String fjtj =getStringData(zyclderecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(zyclderecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(zyclderecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(zyclderecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(zyclderecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(zyclderecordEle.getAttributeValue("jddj"));
                                                String lwgg =getStringData(zyclderecordEle.getAttributeValue("lwgg"));
                                                String jxxndj =getStringData(zyclderecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(zyclderecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(zyclderecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                        //匹配
                                        for (Element matchpartEle : (List<Element>) gydeEle.getChildren("MATCHPART")) {
                                            for (Element matchpartRecordEle : (List<Element>) matchpartEle.getChildren("MatchPart")) {
                                                String wzbm =getStringData(matchpartRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(matchpartRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(matchpartRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(matchpartRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(matchpartRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(matchpartRecordEle.getAttributeValue("sccj"));
                                                String zjldw ="";
                                                String fjtj =getStringData(matchpartRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(matchpartRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(matchpartRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(matchpartRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(matchpartRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(matchpartRecordEle.getAttributeValue("jddj"));
                                                String lwgggccc =getStringData(matchpartRecordEle.getAttributeValue("lwgggccc"));
                                                String jxxndj =getStringData(matchpartRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(matchpartRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(matchpartRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgggccc, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                        //新增
                                        for (Element newpartEle : (List<Element>) gydeEle.getChildren("NEWPART")) {
                                            for (Element newpartRecordEle : (List<Element>) newpartEle.getChildren("NewPart")) {
                                                String wzbm =getStringData(newpartRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(newpartRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(newpartRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(newpartRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(newpartRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(newpartRecordEle.getAttributeValue("sccj"));
                                                String zjldw ="";
                                                String fjtj =getStringData(newpartRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(newpartRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(newpartRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(newpartRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(newpartRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(newpartRecordEle.getAttributeValue("jddj"));
                                                String lwgggccc =getStringData(newpartRecordEle.getAttributeValue("lwgggccc"));
                                                String jxxndj =getStringData(newpartRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(newpartRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(newpartRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgggccc, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }


                                        //试件原材料
                                        for (Element sjycldeEle : (List<Element>) gydeEle.getChildren("SJYCLDE")) {
                                            for (Element sjycldeRecordEle : (List<Element>) sjycldeEle.getChildren("sjycldeRecord")) {
                                                String wzbm =getStringData(sjycldeRecordEle.getAttributeValue("chbm"));
                                                String wzmc =getStringData(sjycldeRecordEle.getAttributeValue("chmc"));
                                                String xhph =getStringData(sjycldeRecordEle.getAttributeValue("xhph"));
                                                String gg =getStringData(sjycldeRecordEle.getAttributeValue("gg"));
                                                String jstj =getStringData(sjycldeRecordEle.getAttributeValue("jstj"));
                                                String sccj =getStringData(sjycldeRecordEle.getAttributeValue("sccj"));
                                                String zjldw =getStringData(sjycldeRecordEle.getAttributeValue("zjldw"));
                                                String fjtj =getStringData(sjycldeRecordEle.getAttributeValue("fjtj"));
                                                String gyztrcl =getStringData(sjycldeRecordEle.getAttributeValue("gyztrcl"));
                                                String zldj =getStringData(sjycldeRecordEle.getAttributeValue("zldj"));
                                                String dw =getStringData(sjycldeRecordEle.getAttributeValue("dw"));
                                                String fzxs =getStringData(sjycldeRecordEle.getAttributeValue("fzxs"));
                                                String jddj =getStringData(sjycldeRecordEle.getAttributeValue("jddj"));
                                                String lwgg =getStringData(sjycldeRecordEle.getAttributeValue("lwgg"));
                                                String jxxndj =getStringData(sjycldeRecordEle.getAttributeValue("jxxndj"));
                                                String dcstxyq =getStringData(sjycldeRecordEle.getAttributeValue("dcstxyq"));
                                                String comment =getStringData(sjycldeRecordEle.getAttributeValue("comment"));
                                                String[] rowDatas = new String[]{techOid, tecNumber,technicsNum,techName, version, creator, dept, techLx, techLb, wzbm, wzmc, xhph, gg, jstj, sccj,zjldw, fjtj, gyztrcl,zldj, dw, fzxs, jddj, lwgg, jxxndj, dcstxyq,comment};
                                                Row nextrow = sheet.createRow(count);
                                                for (int i = 0; i < title.length; i++) {
                                                    cell = nextrow.createCell(i);
                                                    cell.setCellValue(rowDatas[i]);
                                                }
                                                count++;
                                            }
                                        }
                                    }
                                }

                            }


                        }
                    }
                }

                //创建一个文件
                File file = new File(PropertiesUtil.getWTHome() + File.separator + "exportTechQuota" + File.separator + "exportTechQuota_" + containName + ".xlsx");
                file.createNewFile();
                FileOutputStream stream = FileUtils.openOutputStream(file);
                workbook.write(stream);
                stream.close();

            }
            System.out.println("--------end-------");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }





    /**
     * 将obj转为String
     * @param obj
     * @return
     */
    public static String getStringData(Object obj){
        if(obj==null){
            return "";
        }else{
            return String.valueOf(obj);
        }

    }
    /**
     * 获取产品oid列表
     *
     * @return
     */
    public static Map<Long, String> getContainerList() throws Exception {
        Map<Long, String> map = new HashMap<Long, String>();
        QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        while (qr.hasMoreElements()) {
            PDMLinkProduct productLink = (PDMLinkProduct) qr.nextElement();
            Long id = productLink.getPersistInfo().getObjectIdentifier().getId();
            String name = productLink.getName();
            map.put(id, name);
        }
        return map;
    }


    /**
     * 获取产品oid列表
     *
     * @return
     */
    public static Map<Long, String> getContainerList(String oid) throws Exception {
        Map<Long, String> map = new HashMap<Long, String>();
        PDMLinkProduct pdmLinkProduct = (PDMLinkProduct) QueryUtil.getObjectByOid(PDMLinkProduct.class, Long.valueOf(oid));
        if(pdmLinkProduct!=null){
            Long id = pdmLinkProduct.getPersistInfo().getObjectIdentifier().getId();
            String name = pdmLinkProduct.getName();
            map.put(id, name);
        }
        return map;
    }
}