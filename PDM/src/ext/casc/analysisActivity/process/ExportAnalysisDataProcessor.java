package ext.casc.analysisActivity.process;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.PropertiesUtil;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.helper.AnalysisConstant;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.util.IBAHelper;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.*;
import wt.change2.WTAnalysisActivity;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.util.WTException;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ExportAnalysisDataProcessor implements Serializable {

    private static final long serialVersionUID = 1L;

    public static File export(String type, String oid) throws WTException, IOException {
        ReferenceFactory rf = new ReferenceFactory();
        String path = PropertiesUtil.getLocalCodeBase() + File.separator + "templates" + File.separator + "analysis";
        String tempPath = PropertiesUtil.getTempPath();
        File p = new File(path);
        if(!p.exists()) {
            p.mkdirs();
        }
        String oPath = path + File.separator + "受影响列表.xlsx";
        String tPath = tempPath + File.separator + DateTime.now().toDateStr() + "_" + "受影响列表.xlsx";
        if(AnalysisConstant.PRODUCT.equals(type)) {
            oPath = path + File.separator + "受影响制品列表.xlsx";
            tPath = tempPath + File.separator + DateTime.now().toDateStr() + "_" + "受影响制品列表.xlsx";
        }

        FileUtils.copyFile(new File(oPath), new File(tPath));
        File file = new File(tPath);
        if(file == null) {
            return null;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            XSSFSheet sheet = workbook.getSheetAt(0);

            WTAnalysisActivity activity = (WTAnalysisActivity) rf.getReference(oid).getObject();
            Map<String,String> products = new HashMap<String,String>();
            List<AnalysisObjEntry> entries = AnalysisUtil.getAnalysisObjEntries(activity.getNumber(), null, type);
            XSSFRow row = null;
            int i = 1;
            for(AnalysisObjEntry entry : entries) {
                String verOid = entry.getVerOid();
                Persistable persistable = null;
                try {
                    persistable = rf.getReference("VR:" + verOid).getObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(persistable != null) {
                    if(persistable instanceof WTPart && AnalysisConstant.TYPE_PBOM.equals(type)) {
                        WTPart part = (WTPart) persistable;
                        row = sheet.createRow(i++);
                        writeCellValue(row, 0, part.getNumber());
                        writeCellValue(row, 1, part.getName());
                        writeCellValue(row, 2, part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());
                        writeCellValue(row, 3, part.getState().getState().getDisplay(Locale.CHINA));
                        writeCellValue(row, 4, part.getModifier().getFullName());
                        writeCellValue(row, 5, part.getModifyTimestamp().toLocaleString());
                        writeCellValue(row, 6, "");
                        writeCellValue(row, 7, entry.getAffected());
                        writeCellValue(row, 8, entry.getAffectedGyy());
                        String userId = entry.getResponser();
                        String userName = "";
                        if(StrUtil.isNotEmpty(userId)) {
                            try {
                                Persistable object = rf.getReference(userId).getObject();
                                if(object != null && object instanceof WTUser) {
                                    WTUser user = (WTUser) object;
                                    userName = user.getFullName() + "(" + user.getName() + ")";
                                }
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                        writeCellValue(row, 9, userName);
                        writeCellValue(row, 10, entry.getRequirement());
                        writeCellValue(row, 11, entry.getCompleteTime());
                        writeCellValue(row, 12, "");
                        writeCellValue(row, 13, entry.getDealStatus());
                    } else if(persistable instanceof WTDocument && AnalysisConstant.TYPE_TECHNICS.equals(type)) {
                        WTDocument document = (WTDocument) persistable;
                        row = sheet.createRow(i++);
                        writeCellValue(row, 0, document.getNumber());
                        writeCellValue(row, 1, document.getName());
                        writeCellValue(row, 2, document.getVersionIdentifier().getValue() + "." + document.getIterationIdentifier().getValue());
                        writeCellValue(row, 3, document.getState().getState().getDisplay(Locale.CHINA));
                        writeCellValue(row, 4, document.getModifier().getFullName());
                        writeCellValue(row, 5, document.getModifyTimestamp().toLocaleString());
                        writeCellValue(row, 6, IBAHelper.getIBAStringValue(document, "DEPT"));
                        writeCellValue(row, 7, entry.getAffected());
                        writeCellValue(row, 8, entry.getAffectedGyy());
                        String userId = entry.getResponser();
                        String userName = "";
                        if(StrUtil.isNotEmpty(userId)) {
                            try {
                                Persistable object = rf.getReference(userId).getObject();
                                if(object != null && object instanceof WTUser) {
                                    WTUser user = (WTUser) object;
                                    userName = user.getFullName() + "(" + user.getName() + ")";
                                }
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                        writeCellValue(row, 9, userName);
                        writeCellValue(row, 10, entry.getRequirement());
                        writeCellValue(row, 11, entry.getCompleteTime());
                        writeCellValue(row, 12, entry.getRelatedOrder());
                        writeCellValue(row, 13, entry.getDealStatus());
                    } else if(persistable instanceof WTPart && AnalysisConstant.PRODUCT.equals(type)) {
                        if(products.containsKey(verOid)){
                            continue;
                        }else {
                            products.put(verOid, verOid);
                        }
                        WTPart part = (WTPart) persistable;
                        Map<String, String> productMap = AnalysisUtil.getProductMap(activity.getNumber(), verOid);
                        row = sheet.createRow(i++);
                        writeCellValue(row, 0, part.getNumber());
                        writeCellValue(row, 1, part.getName());
                        writeCellValue(row, 2, productMap.get("zaizhipin"));
                        writeCellValue(row, 3, productMap.get("zcount"));
                        writeCellValue(row, 4, productMap.get("zrepaircount"));
                        String userId = productMap.get("responser");
                        String userName = "";
                        if(StrUtil.isNotEmpty(userId)) {
                            try {
                                Persistable object = rf.getReference(userId).getObject();
                                if(object != null && object instanceof WTUser) {
                                    WTUser user = (WTUser) object;
                                    userName = user.getFullName() + "(" + user.getName() + ")";
                                }
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                        writeCellValue(row, 5, userName);
                        writeCellValue(row, 6, productMap.get("zdealstatus"));
                        writeCellValue(row, 7, productMap.get("yizhipin"));
                        writeCellValue(row, 8, productMap.get("ycount"));
                        writeCellValue(row, 9, productMap.get("yrepaircount"));
                        String jidiaoyuanId = productMap.get("jidiaoyuan");
                        String jidiaoName = "";
                        if(StrUtil.isNotEmpty(jidiaoyuanId)) {
                            try {
                                Persistable object = rf.getReference(jidiaoyuanId).getObject();
                                if(object != null && object instanceof WTUser) {
                                    WTUser user = (WTUser) object;
                                    jidiaoName = user.getFullName() + "(" + user.getName() + ")";
                                }
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                        }
                        writeCellValue(row, 10, jidiaoName);
                        writeCellValue(row, 11, productMap.get("ydealstatus"));
                        writeCellValue(row, 12, entry.getRequirement());
                        writeCellValue(row, 13, entry.getCompleteTime());
                    }
                }
            }

            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            outputStream.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                is.close();
            }
        }
        return file;
    }

    private static void writeCellValue(XSSFRow row, int col, String value) {
        XSSFCell cell = row.createCell(col, XSSFCell.CELL_TYPE_STRING);
        cell.setCellValue(new XSSFRichTextString(value));
    }

}