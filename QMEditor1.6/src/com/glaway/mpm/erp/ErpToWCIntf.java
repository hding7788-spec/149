package com.glaway.mpm.erp;

import com.glaway.mpm.pbom.db.Wzk;
import wt.method.RemoteMethodServer;
import wt.util.WTException;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class ErpToWCIntf {

    public static void main(String[] args) {
        List list = ErpToWCIntf.queryWzk("01", "01", null, null, null, null, null, null, null, null, null);
        System.out.println(list.size());
    }

    /**
     * 查询物资
     *
     * @param dbtype  数据库类型 01：优选物资库    02：ERP物资库
     * @param wzlb    物资类别  01：元器件  02：标准紧固件 03：金属材料 04：非金属材料 05：复合材料 06：机电产品 07：火工品
     * @param wzbm    物资编码
     * @param wzmc    物资名称
     * @param xhphcl  型号/牌号/材料
     * @param gg      规格
     * @param jstj    技术条件
     * @param sccj    生产厂家
     * @param zjldw   主计量单位
     * @param fjtj    附加条件
     * @param gyztrcl 供应状态/热处理
     * @return List<Wzk> 物资列表
     */
    public static List<Wzk> queryWzk(String dbtype, String wzlb, String wzbm, String wzmc, String xhphcl, String gg,
                                     String jstj, String sccj, String zjldw, String fjtj, String gyztrcl) {
        Class[] cls = new Class[]{String.class, String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class};
        Object[] obj = new Object[]{dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj, gyztrcl};

        return (List<Wzk>) remoteMetnodInvoke("queryWzk2", cls, obj);

    }

    public static List<Wzk> queryWzkEx(String dbtype, String wzlb, String wzbm, String wzmc, String xhphcl, String gg,
                                       String jstj, String sccj, String zjldw, String fjtj, String gyztrcl) {
        Class[] cls = new Class[]{String.class, String.class, String.class, String.class, String.class, String.class,
                String.class, String.class, String.class, String.class, String.class};
        Object[] obj = new Object[]{dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj, gyztrcl};

        return (List<Wzk>) remoteMetnodInvoke("queryWzk3", cls, obj);

    }

    /**
     * 更加物资编码查询物资
     *
     * @param dbtype
     * @param invcode
     * @return
     */
    public static Wzk getWzkByInvcode(String dbtype, String invcode) {
        return (Wzk) remoteMetnodInvoke("getWzkByInvcode", new Class[]{String.class, String.class}, new Object[]{dbtype, invcode});
    }

    private static Object remoteMetnodInvoke(String methodName, Class[] classArray, Object[] objectArray) {
        Object object = null;
        RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
        try {
            object = methodServer.invoke(methodName, "com.glaway.mpm.intf.ERPToWCInfRMI", null, classArray,
                    objectArray);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static String genDocSavePbomExcel(String containerId, String docNumber, List<Wzk> dataList) {
        return (String) remoteMetnodInvoke("genDocSavePbomExcel", new Class[]{String.class, String.class, List.class}, new Object[]{containerId, docNumber, dataList});
    }

    public static String editDocSavePbomExcel(String docNumber, List<Wzk> dataList) {
        return (String) remoteMetnodInvoke("editDocSavePbomExcel", new Class[]{String.class, List.class}, new Object[]{docNumber, dataList});
    }

    public static boolean isExistDocument(String docNumber) {
        return (Boolean) remoteMetnodInvoke("isExistDocument", new Class[]{String.class}, new Object[]{docNumber});
    }

    public static List<Wzk> queryExcelPbom(String docId) {
        if (docId == null) return null;
        return (List<Wzk>) remoteMetnodInvoke("queryExcelPbom", new Class[]{String.class}, new Object[]{docId});
    }

    public static byte[] savePbomToExcel(String fileName, List<Wzk> dataList) {
        return (byte[]) remoteMetnodInvoke("savePbomToExcel", new Class[]{String.class, List.class}, new Object[]{fileName, dataList});
    }

    public static List getTechMaterialInfo(String type, Map<String, String> map) {
        return (List) remoteMetnodInvoke("getTechMaterialInfo", new Class[]{String.class, Map.class}, new Object[]{type, map});
    }

    public static List getRealTechMaterialInfo(String type, Map<String, String> map) {
        return (List) remoteMetnodInvoke("getRealTechMaterialInfo", new Class[]{String.class, Map.class}, new Object[]{type, map});
    }

    public static Object createTechniMaterial(Map<String, String> map, String type,String number) {
        return (Object) remoteMetnodInvoke("createTechniMaterial", new Class[]{Map.class, String.class,String.class}, new Object[]{map, type,number});
    }

    public static Object getTMELinkByWzbm(String wzbm, String type) {
        return (Object) remoteMetnodInvoke("getTMELinkByWzbm", new Class[]{String.class, String.class}, new Object[]{wzbm, type});
    }

    public static void saveTechnicaQuotaInfo(Set<String> set, String technicsDocNumber, String type) throws InvocationTargetException, RemoteException {
        remoteMetnodInvoke("saveTechnicaQuotaInfo", new Class[]{Set.class, String.class, String.class}, new Object[]{set, technicsDocNumber, type});
    }

    public static void updateTechnicaQuotaInfo(String docOid, String technicsDocNumber) throws InvocationTargetException, RemoteException {
        remoteMetnodInvoke("updateTechnicaQuotaInfo", new Class[]{String.class, String.class}, new Object[]{docOid, technicsDocNumber});
    }

    /**
     * 方法功能:
     * 检验工艺物资名称是否合法
     *
     * @param map
     * @return java.util.List
     * @author MChen
     * @date 2021/2/23
     */
    public static String validateTmInfo(Map<String, String> map) {
        return (String) remoteMetnodInvoke("validateTmInfo", new Class[]{Map.class}, new Object[]{map});
    }

    /**
     * 获取物资分类信息
     *
     * @return
     */
    public static Map<String, List<String>> getWZFLInfo(String nodeName) {
        return (Map<String, List<String>>) remoteMetnodInvoke("getWZFLInfo", new Class[]{String.class}, new Object[]{nodeName});
    }

    /**
     * 判断条目是否有物资分类
     *
     * @param ids
     * @param type
     * @return
     */
    public static String validateWZFLData(String[] ids, String type) {
        return (String) remoteMetnodInvoke("validateWZFLData", new Class[]{String[].class, String.class}, new Object[]{ids, type});

    }

    /**
     * 更新工艺物资条目对应的物资分类
     *
     * @param ids
     * @param type
     * @return
     */
    public static void updateWZFLData(String[] ids, String type, String wzfl) {
        remoteMetnodInvoke("updateWZFLData", new Class[]{String[].class, String.class, String.class}, new Object[]{ids, type, wzfl});

    }

    /**
     * 获取资源库相关属性
     *
     * @param partNumberList
     * @param ibaAttrMap
     * @return
     */
    public static Map<String, Map<String, String>> getResourcePartAttr(List<String> partNumberList, Map<String, String> ibaAttrMap) {
        return (Map<String, Map<String, String>>) remoteMetnodInvoke("getResourcePartAttr", new Class[]{List.class, Map.class}, new Object[]{partNumberList, ibaAttrMap});

    }

    /**
     * 获取工艺物资名称所有字典名称
     *
     * @return
     */
    public static Map<String, Vector<String>> getAllTechncisMaterialDic() throws SQLException {
        return (Map<String, Vector<String>>) remoteMetnodInvoke("getAllTechncisMaterialDic", new Class[]{}, new Object[]{});
    }

    /**
     * 获取唯一编码
     *
     * @return
     */
    public static String getTechnicsMaterialEntriesNum() {
        return (String) remoteMetnodInvoke("getTechnicsMaterialEntriesNum", new Class[]{}, new Object[]{});
    }

    /**
     * 调用设计资源库申请编码
     * @return
     */
    public static String supplyNum(Map<String, String> attrMap,Map<String, String> ibaMap){
        return (String) remoteMetnodInvoke("supplyNum", new Class[]{Map.class,Map.class}, new Object[]{attrMap,ibaMap});

    }


    /**
     * 获取设计编码对应的物资条目信息
     *
     * @param partNumberList
     * @param ibaAttrMap
     * @return
     */
    public static List<Object> getTechnicsMaterialInfoByPartNum(List<String> partNumberList, String partType) throws WTException {
        return ( List<Object>) remoteMetnodInvoke("getTechnicsMaterialInfoByPartNum", new Class[]{List.class,String.class}, new Object[]{partNumberList,partType});
    }

    public static Object getTMELinkBySjbm(String sjbm, String type) {
        return (Object) remoteMetnodInvoke("getTMELinkBySjbm", new Class[]{String.class, String.class}, new Object[]{sjbm, type});
    }
}
