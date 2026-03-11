package ext.casc.processPlan;


import ext.casc.mpm.process.TechnicsGenerator;

import java.util.ArrayList;
import java.util.List;


public class Constants {
    public static final String MSG_1 = "存在选择零件未设置工艺参数模板，请先选择模板后再设置参数！";
    public static final String MSG_2 = "选择的行工艺参数模板不同，请重新选择！";
    public static final String YZF = "YZF";// 已作废
    public static final String IBA_DEPT = "DEPT";// 编制部门
    public static final String DOC_TYPE_TechnicsParamTemplate_FULL = "casc.sast.149.TechnicsParamTemplate";// 已作废

    public static final String PRE = "PRE_";// 前缀

    public static List<String> STATES_INTERVAL = new ArrayList<String>();
    public static List<String> STATES_DISPLAY = new ArrayList<String>();
    public static List<String> DEPTS = new ArrayList<String>();

    public static List<String> PRODUCT_TYPE = new ArrayList<String>();
    public static List<String> SPECIALITYS = new ArrayList<String>();

    /**
     * 工艺文件类别
     */
    public static List<String> PPLANTYPES = new ArrayList<String>();
    /**
     * 主辅工艺
     */
    public static List<String> ZFFLAGS = new ArrayList<String>();
    /**
     * 工艺类型
     */
    public static List<String> TECHNICSTYPES_DISPLAY = new ArrayList<String>();
    public static List<String> TECHNICSTYPES_INNER = new ArrayList<String>();

    static {


        DEPTS.add("");

        DEPTS.addAll(TechnicsGenerator.depts);

        PRODUCT_TYPE.add("");
        PRODUCT_TYPE.add("导管");
        PRODUCT_TYPE.add("钣金");
        PRODUCT_TYPE.add("贮箱");

        TECHNICSTYPES_INNER = new ArrayList( TechnicsGenerator.typesMap.keySet());
        TECHNICSTYPES_DISPLAY = new ArrayList(TechnicsGenerator.typesMap.values());


        SPECIALITYS.add("");
        SPECIALITYS.addAll(TECHNICSTYPES_DISPLAY);


        PPLANTYPES.add("正式工艺文件");
        PPLANTYPES.add("临时工艺文件");

        ZFFLAGS.add("Z");
        ZFFLAGS.add("F");




    }
}
