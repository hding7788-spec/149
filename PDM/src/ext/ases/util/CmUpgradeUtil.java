package ext.ases.util;

import com.ptc.core.components.rendering.guicomponents.GUIComponentArray;
import com.ptc.core.components.rendering.guicomponents.TextDisplayComponent;
import wt.method.RemoteAccess;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class CmUpgradeUtil implements RemoteAccess, Serializable {
    /** */
    private static final long serialVersionUID = 1212276387591827667L;

    /** Customize var link ***/
    public static List<String> ASES_CUSTOMIZE_VAR_NAME = Arrays.asList(new String[] {
    		"changeFileRecover", //更改回收
    		"directDelay",//直接回收延迟文件
    		"overTime",//延迟回收逾期信息
    		"openStore",//文件启封
    		"save",//文件封存
    		"lose",//文件遗失
    		"delay",//文件延迟
    		"recover",//文件回收
            "PDFPreview",
    		"downloadLink", // 普通下载连接
    		"viewSenKe", // 森科查看
            "browserProcess",
            "gnumberLink",
            "browserPbom", // 查看提交签审的PBOM
            "keyProcessStep", // 关键工序工步
            "selectUnitLink",//选择电子会签单位
            "selectUsersLink",//选择电子会签人员
            "proxyDate",//选择电子会签人员
            "download_CS_ECN_Old", // 转阶段就数据图纸
            "download_CS_ECN_New", // 转阶段新数据
            "download_CS_ECN_Print", // 转阶段打印数据
            "openPrintApply", //打开预览打印
            "getPaperFile", // 领取纸质文件
            "changeInfo",//修改打印申请
            "sealPlus",//加盖印章确认
            "modifyAddSeal",//修改加盖印章
            "doPrint",//执行打印
            "doPrintForOffSet",//执行补打
            "getPaperFileForOffSet",//补打领用
            "setSignUserAndUnit",//设置会签单位和会签人，中心域新增
            "viewYqjOutLink",
            "transferFile"//转移文件列表
    }); // 在页面显示为link的方法

    public static boolean transferHTMLFormat(String s, String s1, boolean flag, GUIComponentArray guicomponentarray,
            int i, int j) {
        if (!needHTMLFormat(s))
            return false;

        TextDisplayComponent textdisplaycomponent = new TextDisplayComponent("");
        textdisplaycomponent.setValue(s1);
        textdisplaycomponent.setCheckXSS(false);
        guicomponentarray.addGUIComponent(textdisplaycomponent);

        textdisplaycomponent = new TextDisplayComponent("");
        textdisplaycomponent.setValue("</td></tr>");
        textdisplaycomponent.setCheckXSS(false);
        guicomponentarray.addGUIComponent(textdisplaycomponent);

        return true;
    }

    private static boolean needHTMLFormat(String s) {
        if (ASES_CUSTOMIZE_VAR_NAME.contains(s))
            return true;
        return false;
    }
}