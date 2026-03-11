/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.pdf;

import com.glaway.mpm.pdf.CharUtil;
import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import ext.casc.util.IBAUtility;
import ext.casc.workflow.PrintHelper;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.util.WTException;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import java.io.File;
import java.util.*;

/**
 * 类功能：技术文档封面生成类
 *
 * @author LB
 * @date 2020/9/18
 */
public class TechnicDocCoverPDFBuilder extends PDFBuilder {

    private String templateName;
    private WTDocument document;

    float yjwjTempCount = 13;
    float yjwjTempCount2 = 15;
    private int row = 1;


    public TechnicDocCoverPDFBuilder(String formName, WTDocument wtDocument) {
        this.templateName = formName;
        this.document = wtDocument;
    }


    @Override
    public void buildPDF(LcmPdfPrinter printer, List<Map<String, String>> list, Map<String, String> map) {
        String template = PropertiesUtil.getLocalCodeBase() + File.separator + "ext/casc/pdf/template/TechnicDocCover.pdf";
        printer.addTempl(templateName, template);
        //填入名称
        writeDocName(document.getName(), printer);
        //填基本信息
        setCommData(printer, templateName);
        //流程信息
        writeProcessInfo(printer);
    }

    private void writeProcessInfo(LcmPdfPrinter printer) {
        try {
            WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(document);
            Iterator it = coll.iterator();
            if (it.hasNext()) {
                WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
                QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(ecn, null, null);
                while (qrProcs.hasMoreElements()) {
                    WfProcess process = (WfProcess) qrProcs.nextElement();
                    if (process.getState().equals(WfState.OPEN_RUNNING) || process.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
                        Hashtable hashtable1 = PrintHelper.getWFInfo2(ecn, process,false,false,ecn);
                        String bianzhi = PDFUtil.objectToString(hashtable1.get("SHEJI"));
                        String jiaodui = PDFUtil.objectToString(hashtable1.get("JIAODUI"));
                        String shenhe = PDFUtil.objectToString(hashtable1.get("SHENHE"));
                        String huiqian = PDFUtil.objectToString(hashtable1.get("HUIQIAN1"));

                        List<String> huiqianValues = new ArrayList<String>();
                        if(huiqian.contains(";")){
                            String[] splitHuiqian = huiqian.split(";");
                            for(String s : splitHuiqian){
                                huiqianValues.add(splitToNewValue(s));
                            }
                        }else{
                            huiqianValues.add(splitToNewValue(huiqian));
                        }
                        String biaoshen = PDFUtil.objectToString(hashtable1.get("BIAOSHEN"));
                        String pizhun = PDFUtil.objectToString(hashtable1.get("PIZHUN"));
                        printer.addText(templateName, "BIANZHI", splitToNewValue(bianzhi));
                        printer.addText(templateName, "JIAODUI", splitToNewValue(jiaodui));
                        printer.addText(templateName, "SHENHE", splitToNewValue(shenhe));
                        for (int i = 0; i < huiqianValues.size(); i++) {
                            printer.addText(templateName, "HUIQIAN_" + (i+1), huiqianValues.get(i));
                        }
                        printer.addText(templateName, "BIAOSHEN", splitToNewValue(biaoshen));
                        printer.addText(templateName, "PIZHUN", splitToNewValue(pizhun));
                        //更改单号
                        printer.addText(templateName, "GGDH", ecn.getNumber());
                    }
                }
            }else{
                QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(document, null, null);
                while (qrProcs.hasMoreElements()) {
                    WfProcess process = (WfProcess) qrProcs.nextElement();
                    if (process.getState().equals(WfState.OPEN_RUNNING) || process.getState().equals(WfState.CLOSED_COMPLETED_EXECUTED)) {
                        Hashtable hashtable = PrintHelper.getPrintInfo(document, process);
                        HashSet hashSet = new HashSet(hashtable.values());
                        if(hashSet.iterator().hasNext()){
                            Hashtable hashtable1 = (Hashtable) hashSet.iterator().next();
                            String bianzhi = PDFUtil.objectToString(hashtable1.get("SHEJIZHESHIJIAN"));
                            String jiaodui = PDFUtil.objectToString(hashtable1.get("JIAODUIZHESHIJIAN"));
                            String shenhe = PDFUtil.objectToString(hashtable1.get("SHENHEZHESHIJIAN"));
                            String huiqian = PDFUtil.objectToString(hashtable1.get("HUIQIAN1"));

                            List<String> huiqianValues = new ArrayList<String>();
                            if(huiqian.contains(";")){
                                String[] splitHuiqian = huiqian.split(";");
                                for(String s : splitHuiqian){
                                    huiqianValues.add(splitToNewValue(s));
                                }
                            }else{
                                huiqianValues.add(splitToNewValue(huiqian));
                            }
                            String biaoshen = PDFUtil.objectToString(hashtable1.get("BIAOSHENZHESHIJIAN"));
                            String pizhun = PDFUtil.objectToString(hashtable1.get("PIZHUNZHESHIJIAN"));
                            printer.addText(templateName, "BIANZHI", splitToNewValue(bianzhi));
                            printer.addText(templateName, "JIAODUI", splitToNewValue(jiaodui));
                            printer.addText(templateName, "SHENHE", splitToNewValue(shenhe));
                            for (int i = 0; i < huiqianValues.size(); i++) {
                                printer.addText(templateName, "HUIQIAN_" + (i+1), huiqianValues.get(i));
                            }
                            printer.addText(templateName, "BIAOSHEN", splitToNewValue(biaoshen));
                            printer.addText(templateName, "PIZHUN", splitToNewValue(pizhun));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String splitToNewValue(String value) {
        String newValue = "";
        if(value.contains("/")){
            String[] split = value.split("/");
            if(split.length == 3){
                newValue = split[0] + " " + split[2];
            }else{
                newValue = value;
            }
        }else{
            newValue = value;
        }
        return newValue;
    }

    @Override
    public void setCommData(LcmPdfPrinter printer, String s) {
        try {
            IBAUtility ibaUtility = new IBAUtility(document);
            printer.addText(templateName, "DH", document.getIterationDisplayIdentifier().toString());
            printer.addText(templateName, "BGQX", "");
            printer.addText(templateName, "NUMBER", document.getNumber());
            printer.addText(templateName, "SECRET", ibaUtility.getIBAValue("SECRET"));
            printer.addText(templateName, "PHASE", ibaUtility.getIBAValue("PHASE_CODE"));
        } catch (WTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTechFloder() {
        String pdfPath = PropertiesUtil.getTempPath() + File.separator + System.currentTimeMillis();
        File file = new File(pdfPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        return pdfPath;
    }

    @Override
    public List<String> getTemplateList() {
        return null;
    }

    /**
     * 方法功能: 文件名称，换行输入
     *
     * @param docName
     * @param printer
     * @return void
     * @author LB
     * @date 2020/9/20
     */
    private void writeDocName(String docName, LcmPdfPrinter printer) {
        char[] clArr = docName.toCharArray();
        float tempCount = yjwjTempCount;
        String outputValue = "";
        for (int i = 0; i < clArr.length; i++) {
            if (CharUtil.isChinese(String.valueOf(clArr[i]))) {
                //如果是中文，长度 -1
                tempCount = tempCount - 1;
            } else {
                //不是中文，长度 -0.58
                tempCount = tempCount - 0.58f;
            }
            outputValue = outputValue + clArr[i];
            if (tempCount > 0 && i < (clArr.length - 1)) {
                continue;
            }
            if (outputValue == null || "".equals(outputValue.trim())) {
                continue;
            }
            printer.addText(templateName, "NAME_" + row, outputValue);
            row++;
            tempCount = yjwjTempCount2;
            outputValue = "";
        }
    }
}
