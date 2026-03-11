package com.glaway.mpm.pdf;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HtmlGenerator {
    private final  static String ALLHTMLREGEX = "<[^>]+>";
    private final  static String IMGREGEX = "<img[^>]*?src=['\\\"]file:///C:[^>]*?>";
    private final  static String LINKREGEX = "<link[^>]*?href=['\\\"]file:///C:[^>]*?>";
    private final  static String FONTREGSTART = "<font[^>]*?>";
    private final  static String FONTREGEND = "</font>";
    private final  static String SPANREGSTART = "<span[^>]*?>";
    private final  static String SPANREGEND = "</span>";
    private final  static String METAREG = "<meta[^>]*?>";

    public static void main(String[] args) {
        // 示例数据
        List<CheckOutTableUnit> units = new ArrayList<CheckOutTableUnit>();


        // 生成 HTML
        String htmlContent = generateHtml(units,"");

        // 保存 HTML 文件
        String filePath = "output.html";
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(htmlContent);
            System.out.println("HTML 文件生成成功: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成 HTML 字符串
     */
    public static String generateHtml(List<CheckOutTableUnit> units,String imagePath)  {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n")
                .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gbk\"/>\n")
                .append("<title>查看检验汇总表</title>\n")
                .append("<style type=\"text/css\">\n")
                .append("body { margin: 0px; width: 1200px; min-width: 100px; max-width: 1200px; height: 80%; }\n")
                .append(".bttr { color: #2222fF; background-color: #cFcFcF; }\n")
                .append(".bttd { background-color: #ccdFf1; }\n")
                .append("</style>\n</head>\n<body>\n")
                .append("<div id=\"checkResource\" style=\"margin-top:10px;overflow:hidden\">\n")

                .append("<div>\n<table id=\"table1_content\" border=\"1\" width=\"1200px\" margin-top=\"20px\">\n")
                .append("<tr class=\"bttr\">\n")
                .append("<th width=\"1%\">工序</th>\n<th width=\"1%\">工步</th>\n<th width=\"3%\">表主件类型</th>\n")
                .append("<th width=\"3%\">单元表表名</th>\n<th width=\"3%\">项目名</th>\n<th width=\"3%\">套表名</th>\n")
                .append("<th width=\"24%\">记录项/检测项</th>\n<th width=\"24%\">要求值/公称值</th>\n<th width=\"3%\">记录</th>\n")
                .append("<th width=\"3%\">上偏差</th>\n<th width=\"3%\">下偏差</th>\n<th width=\"3%\">实测值</th>\n</tr>\n");

        // 遍历数据，生成表格内容
        for (CheckOutTableUnit unit : units) {
            html.append("<tr>\n")
                    .append("<td align=\"center\">").append(unit.getGongXu()).append("</td>\n")
                    .append("<td align=\"center\">").append(unit.getGongBu()).append("</td>\n")
                    .append("<td align=\"center\">").append(unit.getTableType()).append("</td>\n")
                    .append("<td align=\"center\">").append(unit.getUnitTableName()).append("</td>\n")
                    .append("<td align=\"center\">").append(unit.getProjectName()).append("</td>\n")
                    .append("<td align=\"center\">").append(unit.getTableName()).append("</td>\n")
                    .append("<td align=\"center\">").append(removeHtmlTagsWithOutImage(unit.getJiLuXiang()).replace("@#$%^", imagePath)).append("</td>\n")
                    .append("<td align=\"center\">").append(removeHtmlTagsWithOutImage(unit.getYaoQiuVale()).replace("@#$%^", imagePath)).append("</td>\n")
                    .append("<td align=\"center\">").append(unit.getJiLu()).append("</td>\n")
                    .append("<td align=\"center\">").append(unit.getShangPianCha()).append("</td>\n")
                    .append("<td align=\"center\">").append(unit.getXiaPianCha()).append("</td>\n")
                    .append("<td align=\"center\">").append(unit.getShiCeValue()).append("</td>\n")
                    .append("</tr>\n");
        }

        // 结束 HTML
        html.append("</table>\n</div>\n</div>\n</body>\n</html>");
        return html.toString();
    }

    public static String removeHtmlTags(String html) {
        Pattern pattern = Pattern.compile(ALLHTMLREGEX);
        return pattern.matcher(html).replaceAll("");
    }

    public static String removeHtmlTagsWithOutImage(String html) {
        String  cleanedHtml = html.replaceAll(LINKREGEX, "");

        cleanedHtml = cleanedHtml.replaceAll(FONTREGSTART, "");

        cleanedHtml = cleanedHtml.replaceAll(FONTREGEND, "");

        cleanedHtml = cleanedHtml.replaceAll(SPANREGSTART, "");

        cleanedHtml = cleanedHtml.replaceAll(SPANREGEND, "");

        cleanedHtml = cleanedHtml.replaceAll(METAREG, "");
        return cleanedHtml;
    }

    public static String removeImageTags(String html) {
        String cleanedHtml = html.replaceAll(IMGREGEX, "");

        cleanedHtml = cleanedHtml.replaceAll(LINKREGEX, "");

        cleanedHtml = cleanedHtml.replaceAll(FONTREGSTART, "");

        cleanedHtml = cleanedHtml.replaceAll(FONTREGEND, "");

        cleanedHtml = cleanedHtml.replaceAll(SPANREGSTART, "");

        cleanedHtml = cleanedHtml.replaceAll(SPANREGEND, "");

        cleanedHtml = cleanedHtml.replaceAll(METAREG, "");
        return cleanedHtml;
    }



}
