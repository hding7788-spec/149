package ext.casc.importdata;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("ext.casc.importdata.productRB")
public class productRB_zh_CN extends WTListResourceBundle {
    
    @RBEntry("产品结构历史数据导入")
    public static final String IMPORTDATA_TITLE = "custom.importData.title";
    
    @RBEntry("*请选择已打包的zip文件：")
    public static final String IMPORTDATA_FILES_TITLE = "0";
    
    @RBEntry("*请选择EXCEL表文件：")
    public static final String IMPORTDATA_EXCEL_TITLE = "1";
    
    @RBEntry("在执行导入前，请选择产品结构数据相关的excel文件：")
    public static final String IMPORTDATA_NOTICE = "2";
  

   
    
    @RBEntry("导入文件存在以下错误，请修改后重新执行:")
    public static final String IMPORTERRORMESSAGE_TITLES = "3";
    
    
    @RBEntry("关闭")
    public static final String IMPORTDATA_CLOSE = "4";
    
    @RBEntry("二维图纸历史数据导入")
    public static final String IMPORTCAD_TITLE = "custom.importCAD.title";
    
    @RBEntry("在执行导入前，请选择产品结构数据相关的文件：")
    public static final String  IMPORTCAD_NOTICE = "5";
    
    
    @RBEntry("文档历史数据导入")
    public static final String IMPORTDOC_TITLE = "custom.importDoc.title";

    @RBEntry("在执行导入前，请选择文档数据相关的文件：")
    public static final String  IMPORTDOC_NOTICE = "6";
    
    @RBEntry("导入文件成功，但存在以下警告信息:")
    public static final String IMPORTERRORMESSAGE_TITLES2 = "7";

    @RBEntry("下载模板")
    public static final String IMPORTDATA_DOWNLOAD_EXCEL_TITLE = "8";
}
