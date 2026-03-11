package com.glaway.mpm.pdf.processor;

import com.glaway.mpm.pdf.*;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SopForm2PDFBuilder extends PDFBuilder {

    private Element techElement;
    private String techFloder = null;
    private String formName;
    private String templateName;
    // 用于记录PDF的页数
    private int page = 1;
    private String template;
    // 用于记录序号值
    private int index = 1;
    // 保存所有的PDF页面名称
    private List<String> templateList = new ArrayList<String>();

    public SopForm2PDFBuilder(Element techElement, String filePath, String formName) {
        this.techFloder = filePath;
        this.formName = formName;
        this.techElement = techElement;
    }

    @Override
    public void buildPDF(LcmPdfPrinter printer, List<Map<String, String>> partList, Map<String, String> params) {
        try {
            template = PDFUtil.getFormTemplateFolderPath() + "SopForm2.pdf";
            templateName = formName;
            List<Element> stepList = techElement.selectNodes("steps/QMProcedureInfo");
            if(stepList != null && stepList.size() > 0){
                printer.addTempl(templateName, template);
                templateList.add(templateName);
                setEcnParams(params);
                setCommData(printer, templateName);

            }
            // 开始循环遍历所有工序
            for (Element step : stepList) {
                if (index > 36) {
                    index = 1;
                    page++;
                    templateName = formName + "_" + page;
                    if(!templateList.contains(templateName)){
                        templateList.add(templateName);
                        printer.addTempl(templateName, template);
                        setEcnParams(params);
                        setCommData(printer, templateName);
                    }
                }
                writeStepElement(techElement, step, printer, index, templateName);
                index++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeStepElement(Element technics, Element stepElement, LcmPdfPrinter printer, int row, String templateName) {
        printer.addText(templateName, "顺序号_" + row, stepElement.attributeValue("stepNumber"));
        printer.addText(templateName, "操作名称_" + row, stepElement.attributeValue("stepName"));
    }


    @Override
    public void setCommData(LcmPdfPrinter printer, String templateName) {
        printer.addText(templateName, "文件编号", PDFUtil.objectToString(techElement.attributeValue("pplanNumber")));
        printer.addText(templateName, "文件名称", PDFUtil.objectToString(techElement.attributeValue("pplanName")));
        printer.addText(templateName, "版本号", PDFUtil.objectToString(techElement.attributeValue("version")));

        printer.addText(templateName, "编制", "");
        printer.addText(templateName, "编制时间", "");
        printer.addText(templateName, "校对", "");
        printer.addText(templateName, "校对时间", "");
        printer.addText(templateName, "审核", "");
        printer.addText(templateName, "审核时间", "");

        printer.addText(templateName, "会签_姓名_1", "");
        printer.addText(templateName, "会签_部门_1", "");
        printer.addText(templateName, "会签_时间_1", "");
        printer.addText(templateName, "会签_姓名_2", "");
        printer.addText(templateName, "会签_部门_2", "");
        printer.addText(templateName, "会签_时间_2", "");

        printer.addText(templateName, "更改标记", ecnBiaoJi);
        printer.addText(templateName, "更改单号", ecnNo);
        printer.addText(templateName, "更改签名", "");
        printer.addText(templateName, "日期", "");
    }

    @Override
    public String getTechFloder() {
        return techFloder;
    }

    @Override
    public List<String> getTemplateList() {
        return templateList;
    }

}
