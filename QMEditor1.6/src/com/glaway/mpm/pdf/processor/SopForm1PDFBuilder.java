package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.pdf.LcmPdfPrinter;
import com.glaway.mpm.pdf.PDFBuilder;
import com.glaway.mpm.pdf.PDFUtil;

import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SopForm1PDFBuilder extends PDFBuilder {

    private Element techElement;
    private String filePath;
    private String formName;
    private List<String> templateList = new ArrayList<String>();


    public SopForm1PDFBuilder(Element techElement, String filePath, String formName) {
        this.techElement = techElement;
        this.filePath = filePath;
        this.formName = formName;
    }

    @Override
    public void buildPDF(LcmPdfPrinter printer, List<Map<String, String>> partList, Map<String, String> params) {
    	try {
            String templateFilePath = PDFUtil.getFormTemplateFolderPath() + "SopForm1.pdf";
            setEcnParams(params);
            printer.addTempl(formName, templateFilePath);
            this.setCommData(printer, formName);
            templateList.add(formName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setCommData(LcmPdfPrinter printer, String templateName) {
        printer.addText(templateName, "文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
        printer.addText(templateName, "文件名称", PDFUtil.objectToString(techElement.attributeValue("pplanName")));
        printer.addText(templateName, "版本", PDFUtil.objectToString(techElement.attributeValue("version")));
        printer.addText(templateName, "密级", PDFUtil.objectToString(techElement.attributeValue("secretLevel")));

        printer.addText(templateName, "编制", "");
        printer.addText(templateName, "校对", "");
        printer.addText(templateName, "审核", "");
        printer.addText(templateName, "标检", "");
        printer.addText(templateName, "批准", "");

        printer.addText(templateName, "会签_姓名_1", "");
        printer.addText(templateName, "会签_部门_1", "");
        printer.addText(templateName, "会签_时间_1", "");
        printer.addText(templateName, "会签_姓名_2", "");
        printer.addText(templateName, "会签_部门_2", "");
        printer.addText(templateName, "会签_时间_2", "");

        printer.addText(templateName, "更改标记_1", ecnBiaoJi);
        printer.addText(templateName, "更改单号_1", ecnNo);
        printer.addText(templateName, "更改签名_1", "");
        printer.addText(templateName, "日期_1", "");
        printer.addText(templateName, "更改标记_2", "");
        printer.addText(templateName, "更改单号_2", "");
        printer.addText(templateName, "更改签名_2", "");
        printer.addText(templateName, "日期_2", "");
        printer.addText(templateName, "更改标记_3", "");
        printer.addText(templateName, "更改单号_3", "");
        printer.addText(templateName, "更改签名_3", "");
        printer.addText(templateName, "日期_3", "");
    }

    @Override
    public String getTechFloder() {
        return filePath;
    }

    @Override
    public List<String> getTemplateList() {
        return templateList;
    }
}
