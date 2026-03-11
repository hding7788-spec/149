package ext.ases.techMaterial.util;

import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.ReferenceFactory;
import ext.ases.techMaterial.TechnicsMaterialEntries;
import ext.ases.techMaterial.bean.*;
import ext.ases.techMaterial.gwpersistable.GwPersistenceHelper;
import ext.ases.techMaterial.gwpersistable.GwQueryResult;
import ext.ases.techMaterial.gwpersistable.GwQuerySpec;
import ext.ases.techMaterial.model.*;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import ext.casc.sop.util.StringUtil;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.org.WTUser;
import wt.pds.oracle81.OracleDataSource;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.XMLType;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class TechnicsMaterialUtils {
    // 标准紧固件
    public static Map<String, String> standardInfoMap;
    // 电子元器件
    public static Map<String, String> eleComponentsInfoMap;
    // 非金属材料
    public static Map<String, String> nonmetallicInfoMap;
    // 复合材料
    public static Map<String, String> compoundMaterialInfoMap;
    // 金属材料
    public static Map<String, String> metallicInfoMap;
    // 机电材料
    public static Map<String, String> eleMachineInfoMap;
    // 火工品
    public static Map<String, String> expDeviceInfoMap;

    // 标准紧固件
    public static Map<String, String> standardSendMap;
    // 电子元器件
    public static Map<String, String> eleComponentsSendMap;
    // 非金属材料
    public static Map<String, String> nonmetallicSendMap;
    // 复合材料
    public static Map<String, String> compoundMaterialSendMap;
    // 金属材料
    public static Map<String, String> metallicSendMap;

    private static String wthome;

    private static String templateDir;
    private static String tempDir;

    static {
        try {
            WTProperties pro = WTProperties.getLocalProperties();
            wthome = pro.getProperty("wt.home", "");
            tempDir = pro.getProperty("wt.temp");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 标准紧固件
        standardInfoMap = new LinkedHashMap<String, String>();
        standardInfoMap.put("物资名称", "WZMC");
        standardInfoMap.put("规格", "GG");
        standardInfoMap.put("标准号", "BZH");
        standardInfoMap.put("机械性能等级或硬度", "JXXNDJ");
        standardInfoMap.put("计量单位", "JLDW");
        standardInfoMap.put("材料", "CL");
        standardInfoMap.put("表面处理", "BMCL");
        standardInfoMap.put("热处理", "RCL");
        standardInfoMap.put("生产厂家", "SCCJ");
        standardInfoMap.put("产品型式", "CPXS");
        standardInfoMap.put("产品等级", "CPDJ");
        standardInfoMap.put("板拧形式", "NBXS");
        standardInfoMap.put("特殊说明", "TSSM");
        standardInfoMap.put("是否进口", "SFJK");
        standardInfoMap.put("设计编码", "SJBM");
        standardInfoMap.put("物资编码", "WZBM");
        standardInfoMap.put("物资简称", "WZJC");
        standardInfoMap.put("编码优选级别", "BMYXJB");
        standardInfoMap.put("编码状态", "BMZT");
        standardInfoMap.put("编码类型", "BMLX");
        standardInfoMap.put("编码等级", "BMDJ");
        standardInfoMap.put("物资分类", "WZFL");
        // 电子元器件
        eleComponentsInfoMap = new LinkedHashMap<String, String>();
        eleComponentsInfoMap.put("物资名称", "WZMC");
        eleComponentsInfoMap.put("型号规格", "XHGG");
        eleComponentsInfoMap.put("质量等级", "ZLDJ");
        eleComponentsInfoMap.put("生产厂家", "SCCJ");
        eleComponentsInfoMap.put("计量单位", "JLDW");
        eleComponentsInfoMap.put("总规范", "ZGF");
        eleComponentsInfoMap.put("详细规范", "XXGF");
        eleComponentsInfoMap.put("型号", "XH");
        eleComponentsInfoMap.put("封装形式", "FZXS");
        eleComponentsInfoMap.put("外形尺寸", "WXCC");
        eleComponentsInfoMap.put("专用条件", "ZYTJ");
        eleComponentsInfoMap.put("附加协议", "FJXY");
        eleComponentsInfoMap.put("特殊说明", "TSSM");
        eleComponentsInfoMap.put("是否进口", "SFJK");
        eleComponentsInfoMap.put("抗辐射指标TID", "KFSZBTID");
        eleComponentsInfoMap.put("抗辐射指标SEE", "KFSZBSEE");
        eleComponentsInfoMap.put("性能参数", "XNCS");
        eleComponentsInfoMap.put("是否静电敏感", "SFJDMG");
        eleComponentsInfoMap.put("静电敏感等级", "JDMGDJ");
        eleComponentsInfoMap.put("湿敏等级", "SMDJ");
        eleComponentsInfoMap.put("设计编码", "SJBM");
        eleComponentsInfoMap.put("物资编码", "WZBM");
        eleComponentsInfoMap.put("物资简称", "WZJC");
        eleComponentsInfoMap.put("编码优选级别", "BMYXJB");
        eleComponentsInfoMap.put("编码状态", "BMZT");
        eleComponentsInfoMap.put("编码类型", "BMLX");
        eleComponentsInfoMap.put("编码等级", "BMDJ");
        eleComponentsInfoMap.put("物资分类", "WZFL");
        // 非金属材料
        nonmetallicInfoMap = new LinkedHashMap<String, String>();
        nonmetallicInfoMap.put("物资名称", "WZMC");
        nonmetallicInfoMap.put("牌号", "PH");
        nonmetallicInfoMap.put("规格", "GG");
        nonmetallicInfoMap.put("采用标准", "CYBZ");
        nonmetallicInfoMap.put("计量单位", "JLDW");
        nonmetallicInfoMap.put("工艺单位", "GYDW");
        nonmetallicInfoMap.put("生产厂家", "SCCJ");
        nonmetallicInfoMap.put("特殊说明", "TSSM");
        nonmetallicInfoMap.put("是否进口", "SFJK");
        nonmetallicInfoMap.put("物资简称", "WZJC");
        nonmetallicInfoMap.put("换算率", "HSL");
        nonmetallicInfoMap.put("系数", "XS");
        nonmetallicInfoMap.put("设计编码", "SJBM");
        nonmetallicInfoMap.put("物资编码", "WZBM");
        nonmetallicInfoMap.put("编码优选级别", "BMYXJB");
        nonmetallicInfoMap.put("编码状态", "BMZT");
        nonmetallicInfoMap.put("编码类型", "BMLX");
        nonmetallicInfoMap.put("编码等级", "BMDJ");
        nonmetallicInfoMap.put("物资分类", "WZFL");
        // 复合材料
        compoundMaterialInfoMap = new LinkedHashMap<String, String>();
        compoundMaterialInfoMap.put("物资名称", "WZMC");
        compoundMaterialInfoMap.put("牌号", "PH");
        compoundMaterialInfoMap.put("规格", "GG");
        compoundMaterialInfoMap.put("采用标准", "CYBZ");
        compoundMaterialInfoMap.put("计量单位", "JLDW");
        compoundMaterialInfoMap.put("生产厂家", "SCCJ");
        compoundMaterialInfoMap.put("特殊说明", "TSSM");
        compoundMaterialInfoMap.put("是否进口", "SFJK");
        compoundMaterialInfoMap.put("物资简称", "WZJC");
        compoundMaterialInfoMap.put("换算率", "HSL");
        compoundMaterialInfoMap.put("系数", "XS");
        compoundMaterialInfoMap.put("设计编码", "SJBM");
        compoundMaterialInfoMap.put("物资编码", "WZBM");
        compoundMaterialInfoMap.put("编码优选级别", "BMYXJB");
        compoundMaterialInfoMap.put("编码状态", "BMZT");
        compoundMaterialInfoMap.put("编码类型", "BMLX");
        compoundMaterialInfoMap.put("编码等级", "BMDJ");
        compoundMaterialInfoMap.put("物资分类", "WZFL");
        // 金属材料
        metallicInfoMap = new LinkedHashMap<String, String>();
        metallicInfoMap.put("物资名称", "WZMC");
        metallicInfoMap.put("牌号", "PH");
        metallicInfoMap.put("规格", "GG");
        metallicInfoMap.put("供应状态", "GYZT");
        metallicInfoMap.put("采用标准", "CYBZ");
        metallicInfoMap.put("计量单位", "JLDW");
        metallicInfoMap.put("工艺单位", "GYDW");
        metallicInfoMap.put("品种规格标准", "PZGGBZ");
        metallicInfoMap.put("精度", "JD");
        metallicInfoMap.put("质量特征", "ZLTZ");
        metallicInfoMap.put("生产厂家", "SCCJ");
        metallicInfoMap.put("物资简称", "WZJC");
        metallicInfoMap.put("特殊说明", "TSSM");
        metallicInfoMap.put("是否进口", "SFJK");
        metallicInfoMap.put("换算率", "HSL");
        metallicInfoMap.put("系数", "XS");
        metallicInfoMap.put("设计编码", "SJBM");
        metallicInfoMap.put("物资编码", "WZBM");
        metallicInfoMap.put("编码优选级别", "BMYXJB");
        metallicInfoMap.put("编码状态", "BMZT");
        metallicInfoMap.put("编码类型", "BMLX");
        metallicInfoMap.put("编码等级", "BMDJ");
        metallicInfoMap.put("物资分类", "WZFL");
        //机电材料
        eleMachineInfoMap = new LinkedHashMap<String, String>();
        eleMachineInfoMap.put("物资名称", "WZMC");
        eleMachineInfoMap.put("牌号", "PH");
        eleMachineInfoMap.put("标准号", "BZH");
        eleMachineInfoMap.put("型号规格", "XHGG");
        eleMachineInfoMap.put("计量单位", "JLDW");
        eleMachineInfoMap.put("生产厂家", "SCCJ");
        eleMachineInfoMap.put("设计编码", "SJBM");
        eleMachineInfoMap.put("物资编码", "WZBM");
        eleMachineInfoMap.put("编码等级", "BMDJ");
        eleMachineInfoMap.put("是否进口", "SFJK");
        eleMachineInfoMap.put("性能参数", "XNCS");
        eleMachineInfoMap.put("特殊说明", "TSSM");
        eleMachineInfoMap.put("编码状态", "BMZT");
        eleMachineInfoMap.put("物资分类", "WZFL");

        //火工品
        expDeviceInfoMap = new LinkedHashMap<String, String>();
        expDeviceInfoMap.put("物资名称", "WZMC");
        expDeviceInfoMap.put("标准号", "BZH");
        expDeviceInfoMap.put("生产厂家", "SCCJ");
        expDeviceInfoMap.put("计量单位", "JLDW");
        expDeviceInfoMap.put("设计编码", "SJBM");
        expDeviceInfoMap.put("物资编码", "WZBM");
        expDeviceInfoMap.put("编码等级", "BMDJ");
        expDeviceInfoMap.put("产品代号", "CPDH");
        expDeviceInfoMap.put("重量", "ZL");
        expDeviceInfoMap.put("贮存寿命", "ZCSM");
        expDeviceInfoMap.put("TNT", "TNT");
        expDeviceInfoMap.put("性能参数", "XNCS");
        expDeviceInfoMap.put("特殊说明", "TSSM");
        expDeviceInfoMap.put("编码状态", "BMZT");
        expDeviceInfoMap.put("物资分类", "WZFL");

        //标准件
        standardSendMap=new LinkedHashMap<String, String>();
        standardSendMap.put("WZJC", "SHORTNAME");
        standardSendMap.put("GG", "CSIZE");
        standardSendMap.put("BZH", "STANDARDNUMBER");
        standardSendMap.put("JXXNDJ", "MECHANICALPROPERTYORHARDNESS");
        standardSendMap.put("JLDW", "MEASURENIT");
        standardSendMap.put("CL", "CMAT");
        standardSendMap.put("BMCL", "SURFACETREATMENT");
        standardSendMap.put("RCL", "HEATTRATMENT");
        standardSendMap.put("SCCJ", "SUPPLIERS");
        standardSendMap.put("CPXS", "PRODUCT_FORM");
        standardSendMap.put("CPDJ", "PRODUCT_LEVEL");
        standardSendMap.put("NBXS", "PLATECSCREWRORM");
        standardSendMap.put("TSSM", "SPECAILINSTRUCTION");
        standardSendMap.put("SFJK", "ISIMPORT");
        standardSendMap.put("BMYXJB", "YXJB");
        standardSendMap.put("BMZT", "BMZT");
        standardSendMap.put("BMLX", "BMLX");
        standardSendMap.put("BMDJ", "BMDJ");
        //元器件
        eleComponentsSendMap=new LinkedHashMap<String, String>();
        eleComponentsSendMap.put("WZJC", "SHORTNAME");
        eleComponentsSendMap.put("XHGG", "TYPESTANDARD");
        eleComponentsSendMap.put("ZLDJ", "QUALITYLEVEL");
        eleComponentsSendMap.put("SCCJ", "SUPPLIERS");
        eleComponentsSendMap.put("JLDW", "MEASURENIT");
        eleComponentsSendMap.put("ZGF", "TOLALSTANDARD");
        eleComponentsSendMap.put("XXGF", "DETAILSTANDARD");
        eleComponentsSendMap.put("XH", "TYPE");
        eleComponentsSendMap.put("FZXS", "PACKAGINGFORM");
        eleComponentsSendMap.put("WXCC", "OUTLINESIZE");
        eleComponentsSendMap.put("ZYTJ", "SPECIALCONDITION");
        eleComponentsSendMap.put("FJXY", "EXTRACONDITION");
        eleComponentsSendMap.put("TSSM", "SPECAILINSTRUCTION");
        eleComponentsSendMap.put("SFJK", "ISIMPORT");
        eleComponentsSendMap.put("KFSZBTID", "KFZBTID");
        eleComponentsSendMap.put("KFSZBSEE", "KFZBSEE");
        eleComponentsSendMap.put("XNCS", "XNCS");
        eleComponentsSendMap.put("SFJDMG", "JDMGDJ_STATE");
        eleComponentsSendMap.put("JDMGDJ", "JDMGDJ");
        eleComponentsSendMap.put("SMDJ", "SMDJ");
        eleComponentsSendMap.put("BMYXJB", "YXJB");
        eleComponentsSendMap.put("BMZT", "BMZT");
        eleComponentsSendMap.put("BMLX", "BMLX");
        eleComponentsSendMap.put("BMDJ", "BMDJ");
        //金属材料
        metallicSendMap=new LinkedHashMap<String, String>();
        metallicSendMap.put("WZJC", "SHORTNAME");
        metallicSendMap.put("PH", "MARKNUMBER");
        metallicSendMap.put("GG", "CSIZE");
        metallicSendMap.put("GYZT", "SUPPLYSTATE");
        metallicSendMap.put("CYBZ", "USESTANDARD");
        metallicSendMap.put("GYDW", "MEASURENIT");
        metallicSendMap.put("PZGGBZ", "PZGGBZH");
        metallicSendMap.put("JD", "PRECISION");
        metallicSendMap.put("ZLTZ", "QUALITYCHARACTER");
        metallicSendMap.put("SCCJ", "SUPPLIERS");
        metallicSendMap.put("TSSM", "SPECAILINSTRUCTION");
        metallicSendMap.put("SFJK", "ISIMPORT");
        metallicSendMap.put("HSL", "RATEOFCONVERSION");
        metallicSendMap.put("XS", "RATIO");
        metallicSendMap.put("BMYXJB", "YXJB");
        metallicSendMap.put("BMZT", "BMZT");
        metallicSendMap.put("BMLX", "BMLX");
        metallicSendMap.put("BMDJ", "BMDJ");
        //非金属
        nonmetallicSendMap=new LinkedHashMap<String, String>();
        nonmetallicSendMap.put("WZJC", "SHORTNAME");
        nonmetallicSendMap.put("PH", "MARKNUMBER");
        nonmetallicSendMap.put("GG", "CSIZE");
        nonmetallicSendMap.put("CYBZ", "USESTANDARD");
        nonmetallicSendMap.put("GYDW", "MEASURENIT");
        nonmetallicSendMap.put("SCCJ", "SUPPLIERS");
        nonmetallicSendMap.put("TSSM", "SPECAILINSTRUCTION");
        nonmetallicSendMap.put("SFJK", "ISIMPORT");
        nonmetallicSendMap.put("HSL", "RATEOFCONVERSION");
        nonmetallicSendMap.put("XS", "RATIO");
        nonmetallicSendMap.put("BMYXJB", "YXJB");
        nonmetallicSendMap.put("BMZT", "BMZT");
        nonmetallicSendMap.put("BMLX", "BMLX");
        nonmetallicSendMap.put("BMDJ", "BMDJ");
        //复合材料
        compoundMaterialSendMap=new LinkedHashMap<String, String>();
        compoundMaterialSendMap.put("WZJC", "SHORTNAME");
        compoundMaterialSendMap.put("PH", "MARKNUMBER");
        compoundMaterialSendMap.put("GG", "CSIZE");
        compoundMaterialSendMap.put("CYBZ", "USESTANDARD");
        compoundMaterialSendMap.put("JLDW", "MEASURENIT");
        compoundMaterialSendMap.put("SCCJ", "SUPPLIERS");
        compoundMaterialSendMap.put("TSSM", "SPECAILINSTRUCTION");
        compoundMaterialSendMap.put("SFJK", "ISIMPORT");
        compoundMaterialSendMap.put("HSL", "RATEOFCONVERSION");
        compoundMaterialSendMap.put("XS", "RATIO");
        compoundMaterialSendMap.put("BMYXJB", "YXJB");
        compoundMaterialSendMap.put("BMZT", "BMZT");
        compoundMaterialSendMap.put("BMLX", "BMLX");
        compoundMaterialSendMap.put("BMDJ", "BMDJ");

    }

    /***
     * 判断是否存在链接
     *
     * @param tmOid
     * @param dicOid
     * @return
     * @throws SQLException
     */

    public static boolean isRelation(String tmOid, String dicOid) throws SQLException {
        boolean resultFlag = false;
        if (!StringUtil.isEmpty(tmOid) && !StringUtil.isEmpty(dicOid)) {
            Connection conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer selectSQL = new StringBuffer();
            PreparedStatement ps = null;
            ResultSet rs = null;
            HashMap<String, String> map = new HashMap<String, String>();
            try {
                selectSQL.append("SELECT count(1)as cusNum from TECHNICSMATERIALLINK where TECHNICSMATERIALID='" + tmOid + "' AND DICTIONARYID='" + dicOid + "'");
                ps = conn.prepareStatement(selectSQL.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    int cusNum = rs.getInt("cusNum");
                    if (cusNum != 0) {
                        resultFlag = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
        }
        return resultFlag;
    }

    /**
     * 根据工艺物资名称获取字典的id集合
     *
     * @param tmOid
     * @return
     * @throws Exception
     */
    public static List<TechnicsMaterialLink> getDicIdByTechMaterialId(String tmOid) throws Exception {
        List<TechnicsMaterialLink> list = new ArrayList<TechnicsMaterialLink>();
        if (!StringUtil.isEmpty(tmOid)) {
            GwQuerySpec qs = new GwQuerySpec(TechnicsMaterialLink.class);
            qs.appendWhere(TechnicsMaterialLink.TECHNICSMATERIALID, GwQuerySpec.EQUAL, tmOid);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TechnicsMaterialLink tm = (TechnicsMaterialLink) qr.next();
                list.add(tm);
            }
        }
        return list;
    }

    /**
     * 删除物资名称和数据字典关联link
     *
     * @param tmOid
     * @return
     * @throws SQLException
     */
    public static void deleteTmnAndtmdLink(String tmOid, String dicOid) throws SQLException {
        if (!StringUtil.isEmpty(tmOid) && !StringUtil.isEmpty(dicOid)) {
            StringBuffer selectSQL = new StringBuffer();
            DBConnUtil conn = null;
            try {
                selectSQL.append("DELETE  from TECHNICSMATERIALLINK t where t.technicsmaterialid='" + tmOid + "' and t.gwkeyid='" + dicOid + "'");
                conn = new DBConnUtil();
                conn.executeQuery(selectSQL.toString());
                conn.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }

    /***
     * 查询是否存在工艺物资名称
     *
     * @param tmName
     * @return
     * @throws SQLException
     */
    public static boolean hasTechncsiMaterialName(String tmName) throws SQLException {
        if (!StringUtil.isEmpty(tmName)) {
            Connection conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer selectSQL = new StringBuffer();
            PreparedStatement ps = null;
            ResultSet rs = null;
            HashMap<String, String> map = new HashMap<String, String>();
            try {
                selectSQL.append("SELECT * from TECHNICSMATERIAL where TECHNICSMATERIALNAME='" + tmName + "'");
                ps = conn.prepareStatement(selectSQL.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
        }
        return false;
    }

    /***
     * 根据工艺物资名称查询出名称对应的id
     *
     * @param tmName
     * @return
     * @throws SQLException
     */
    public static String getTMOidByName(String tmName) throws SQLException {
        String oid = "";
        if (!StringUtil.isEmpty(tmName)) {
            Connection conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer selectSQL = new StringBuffer();
            PreparedStatement ps = null;
            ResultSet rs = null;
            HashMap<String, String> map = new HashMap<String, String>();
            try {
                selectSQL.append("SELECT TO_CHAR(CLASSNAMEA2A2)||':'||TO_CHAR(IDA2A2) as oid from TECHNICSMATERIAL where TECHNICSMATERIALNAME='" + tmName + "'");
                ps = conn.prepareStatement(selectSQL.toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    oid = rs.getString("oid");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
        }
        return oid;
    }

    /***
     * 根据工艺物资名称查询出名称对应的id
     *
     * @param tmName
     * @return
     * @throws SQLException
     */
    public static Map<String, Object> getTechncsiMaterialOidByName(String tmName) throws SQLException {
        Map<String, Object> hashMap = new HashMap<String, Object>();
        Set<String> set = new HashSet<String>();
        if (!StringUtil.isEmpty(tmName)) {
            Connection conn = OracleDataSource.getOracleDataSource().getConnection();
            StringBuffer selectSQL = new StringBuffer();
            PreparedStatement ps = null;
            ResultSet rs = null;
            HashMap<String, String> map = new HashMap<String, String>();
            try {
                selectSQL.append("SELECT DISTINCT a.DICTIONARYID,a.TECHNICSMATERIALID from TECHNICSMATERIALLINK a ,TECHNICSMATERIAL b where "
                        + "a.TECHNICSMATERIALID=(SELECT TO_CHAR(CLASSNAMEA2A2)||':'||TO_CHAR(IDA2A2) as oid from TECHNICSMATERIAL where TECHNICSMATERIALNAME='" + tmName + "')");
                ps = conn.prepareStatement(selectSQL.toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    String technicsmaterialid = rs.getString("TECHNICSMATERIALID");
                    hashMap.put("oid", technicsmaterialid);
                    String dictionaryid = rs.getString("DICTIONARYID");
                    set.add(dictionaryid);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            hashMap.put("dicSet", set);
        }
        return hashMap;
    }

    /**
     * 生产唯一编号
     *
     * @return
     */
    public static String getUqipNumber() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate = sdf.format(new Date());
        String result = "";
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            result += random.nextInt(10);
        }
        return newDate + result;

    }

    /**
     * 去字符串的左右空格
     *
     * @param str
     * @return
     */
    public static String leftAndRightTrim(String str) {
        if (str == null || str.equals("")) {
            return str;
        } else {
            return str.replaceAll("^[　 ]+|[　 ]+$", "");
        }
    }

    /**
     * 插入工艺物资名称关联的数据
     *
     * @return
     */
    public static void insertTechnicsMaterialLink(String sql) throws SQLException {
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        Statement state = null;
        try {
            conn.setAutoCommit(false);
            state = conn.createStatement();
            state.execute(sql.toString());
            conn.commit();
        } catch (Exception e) {
            System.out.println("工艺物资名称已存在相同数据字典");
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (state != null) {
                state.close();
            }
        }

    }


    /**
     * 根据工艺物资名称的oid删除对应的数据字典
     *
     * @param tmOid
     * @return
     * @throws SQLException
     */
    public static void deleteTmnLink(String tmOid) throws SQLException {
        if (!StringUtil.isEmpty(tmOid)) {
            StringBuffer selectSQL = new StringBuffer();
            DBConnUtil conn = null;
            try {
                selectSQL.append("DELETE  from TECHNICSMATERIALLINK t where t.technicsmaterialid='" + tmOid + "'");
                conn = new DBConnUtil();
                conn.executeQuery(selectSQL.toString());
                conn.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }
    }

    /**
     * 查询容器根据名称
     *
     * @param kass
     * @param name
     * @return
     * @throws WTException
     */
    public static WTContainerRef getWTContainerRef(Class kass, String name) throws WTException {
        try {
            QuerySpec qs = new QuerySpec(kass);
            SearchCondition sc = new SearchCondition(kass, WTContainer.NAME, SearchCondition.EQUAL, name, false);
            qs.appendSearchCondition(sc);
            QueryResult qr = PersistenceHelper.manager.find(qs);
            WTContainer container;
            if (qr.hasMoreElements()) {
                container = (WTContainer) qr.nextElement();
                return WTContainerRef.newWTContainerRef(container);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 根据工艺物资条目获取标准紧固件属性集合
     *
     * @param tmOid
     * @return
     * @throws Exception
     */
    public static List<TMEStandPartLink> getTMEStandPartInfo(String tmOid) throws Exception {
        List<TMEStandPartLink> list = new ArrayList<TMEStandPartLink>();
        if (!StringUtil.isEmpty(tmOid)) {
            GwQuerySpec qs = new GwQuerySpec(TMEStandPartLink.class);
            qs.appendWhere(TMEStandPartLink.TECHNICSMATERIALENTRIESID, GwQuerySpec.EQUAL, tmOid);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMEStandPartLink tm = (TMEStandPartLink) qr.next();
                list.add(tm);
            }
        } else {
            GwQuerySpec qs = new GwQuerySpec(TMEStandPartLink.class);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMEStandPartLink tm = (TMEStandPartLink) qr.next();
                list.add(tm);
            }
        }
        return list;
    }

    /**
     * 拼接sql
     *
     * @param attrMap
     * @return
     */
    public static String appendSql(Map<String, String> attrMap) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("(");
        int i = 0;
        for (String key : attrMap.keySet()) {
            ++i;
            if (i > 1 && i <= attrMap.size()) {
                stringBuffer.append(" and ");
            }
            String value = attrMap.get(key);
            if(!"".equals(value)){
                stringBuffer.append(key + "=" + "'" + value + "'");
            }

        }

        stringBuffer.append(")");
        return stringBuffer.toString();
    }

    /**
     * 根据属性匹配相同
     *
     * @return
     * @throws SQLException
     */
    public static boolean matchSameStandardInfo(Map<String, String> attrMap, String tableName) throws SQLException {
        boolean flag = false;
        String buffer = appendSql(attrMap);
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        StringBuffer selectSQL = new StringBuffer();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            selectSQL.append("select * from " + tableName + " WHERE " + buffer);
            ps = conn.prepareStatement(selectSQL.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                String oid = rs.getString("TECHNICSMATERIALENTRIESID");
                try {
                    TechnicsMaterialEntries materialEntries = (TechnicsMaterialEntries) ReferenceFactory.getObjectbyOid(oid);
                    if(materialEntries!=null){
                        flag = true;
                    }else{
                        StringBuffer deleteSql = new StringBuffer();
                        deleteSql.append("delete from " + tableName + " WHERE TECHNICSMATERIALENTRIESID='" + oid+"'");
                        ps = conn.prepareStatement(deleteSql.toString());
                        ps.executeQuery();
                        conn.commit();
                    }
                }catch (WTRuntimeException e){
                    e.printStackTrace();
                    StringBuffer deleteSql = new StringBuffer();
                    deleteSql.append("delete from " + tableName + " WHERE TECHNICSMATERIALENTRIESID='" + oid+"'");
                    ps = conn.prepareStatement(deleteSql.toString());
                    ps.executeQuery();
                    conn.commit();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return flag;

    }

    /**
     * 保存link属性
     *
     * @param oid
     * @param attrMap
     * @throws SQLException
     */
    public static void saveTechnicsMaterialEntriesLink(String oid, Map<String, String> attrMap, String tableName) {
        String sjbm = attrMap.get("SJBM");
        if(sjbm==null || "".equals(sjbm)){
            sjbm = attrMap.get("WZBM");
        }
        StringBuffer updateBuffer = new StringBuffer();
        updateBuffer.append("update " + tableName + " set ");
        StringBuffer cloumnStringBuffer = new StringBuffer();
        StringBuffer valueStringBuffer = new StringBuffer();
        cloumnStringBuffer.append("INSERT INTO " + tableName + "( GWKEYID, TECHNICSMATERIALENTRIESID,");
        valueStringBuffer.append(" VALUES ('" + sjbm + "','" + oid + "' ,");

        int i = 0;
        for (String key : attrMap.keySet()) {
            ++i;
            if (i > 1 && i <= attrMap.size()) {
                cloumnStringBuffer.append(" , ");
                valueStringBuffer.append(" , ");
                updateBuffer.append(" , ");
            }
            String value = attrMap.get(key);
            cloumnStringBuffer.append(key);
            valueStringBuffer.append("'" + value + "'");
            updateBuffer.append(key + "='" + value + "'");
        }
        cloumnStringBuffer.append(")");
        valueStringBuffer.append(")");
        updateBuffer.append(" where GWKEYID='" + sjbm + "'");
        Connection conn1 = null;
        Statement state1 = null;
        try {
            conn1 = OracleDataSource.getOracleDataSource().getConnection();
            conn1.setAutoCommit(false);
            state1 = conn1.createStatement();
            int update = state1.executeUpdate(updateBuffer.toString());
            System.out.println("updateBuffer========="+updateBuffer);
            if(update==0){
                System.out.println("insert==========="+cloumnStringBuffer.toString() + valueStringBuffer.toString());
                state1.execute(cloumnStringBuffer.toString() + valueStringBuffer.toString());
            }
            conn1.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (state1 != null) {
                    state1.close();
                }
                if (conn1 != null) {
                    conn1.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据工艺物资条目获取标电子元器件属性集合
     *
     * @param tmOid
     * @return
     * @throws Exception
     */
    public static List<TMEEleComponentsPartLink> getEleComponentsPartInfo(String tmOid) throws Exception {
        List<TMEEleComponentsPartLink> list = new ArrayList<TMEEleComponentsPartLink>();
        if (!StringUtil.isEmpty(tmOid)) {
            GwQuerySpec qs = new GwQuerySpec(TMEEleComponentsPartLink.class);
            qs.appendWhere(TMEEleComponentsPartLink.TECHNICSMATERIALENTRIESID, GwQuerySpec.EQUAL, tmOid);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMEEleComponentsPartLink tm = (TMEEleComponentsPartLink) qr.next();
                list.add(tm);
            }
        } else {
            GwQuerySpec qs = new GwQuerySpec(TMEEleComponentsPartLink.class);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMEEleComponentsPartLink tm = (TMEEleComponentsPartLink) qr.next();
                list.add(tm);
            }
        }
        return list;
    }

    /**
     * 根据工艺物资条目获取非金属材料属性集合
     *
     * @param tmOid
     * @return
     * @throws Exception
     */
    public static List<TMENonMetallicPartLink> getNonMetallicPartInfo(String tmOid) throws Exception {
        List<TMENonMetallicPartLink> list = new ArrayList<TMENonMetallicPartLink>();
        if (!StringUtil.isEmpty(tmOid)) {
            GwQuerySpec qs = new GwQuerySpec(TMENonMetallicPartLink.class);
            qs.appendWhere(TMENonMetallicPartLink.TECHNICSMATERIALENTRIESID, GwQuerySpec.EQUAL, tmOid);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMENonMetallicPartLink tm = (TMENonMetallicPartLink) qr.next();
                list.add(tm);
            }
        } else {
            GwQuerySpec qs = new GwQuerySpec(TMENonMetallicPartLink.class);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMENonMetallicPartLink tm = (TMENonMetallicPartLink) qr.next();
                list.add(tm);
            }
        }
        return list;
    }

    /**
     * 根据工艺物资条目获取复合材料属性集合
     *
     * @param tmOid
     * @return
     * @throws Exception
     */
    public static List<TMECompoundMaterialPartLink> getCompoundMaterialPartInfo(String tmOid) throws Exception {
        List<TMECompoundMaterialPartLink> list = new ArrayList<TMECompoundMaterialPartLink>();
        if (!StringUtil.isEmpty(tmOid)) {
            GwQuerySpec qs = new GwQuerySpec(TMECompoundMaterialPartLink.class);
            qs.appendWhere(TMECompoundMaterialPartLink.TECHNICSMATERIALENTRIESID, GwQuerySpec.EQUAL, tmOid);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMECompoundMaterialPartLink tm = (TMECompoundMaterialPartLink) qr.next();
                list.add(tm);
            }
        } else {
            GwQuerySpec qs = new GwQuerySpec(TMECompoundMaterialPartLink.class);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMECompoundMaterialPartLink tm = (TMECompoundMaterialPartLink) qr.next();
                list.add(tm);
            }
        }
        return list;
    }

    /**
     * 根据工艺物资条目获取金属材料属性集合
     *
     * @param tmOid
     * @return
     * @throws Exception
     */
    public static List<TMEMetallicPartLink> getMetallicPartInfo(String tmOid) throws Exception {
        List<TMEMetallicPartLink> list = new ArrayList<TMEMetallicPartLink>();
        if (!StringUtil.isEmpty(tmOid)) {
            GwQuerySpec qs = new GwQuerySpec(TMEMetallicPartLink.class);
            qs.appendWhere(TMEMetallicPartLink.TECHNICSMATERIALENTRIESID, GwQuerySpec.EQUAL, tmOid);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMEMetallicPartLink tm = (TMEMetallicPartLink) qr.next();
                list.add(tm);
            }
        } else {
            GwQuerySpec qs = new GwQuerySpec(TMEMetallicPartLink.class);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMEMetallicPartLink tm = (TMEMetallicPartLink) qr.next();
                list.add(tm);
            }
        }
        return list;
    }

    /**
     * 根据工艺物资条目获取火工品属性集合
     *
     * @param tmOid
     * @return
     * @throws Exception
     */
    public static List<TMEExpDevicePartLink> getExpDevicePartInfo(String tmOid) throws Exception {
        List<TMEExpDevicePartLink> list = new ArrayList<TMEExpDevicePartLink>();
        if (!StringUtil.isEmpty(tmOid)) {
            GwQuerySpec qs = new GwQuerySpec(TMEExpDevicePartLink.class);
            qs.appendWhere(TMEExpDevicePartLink.TECHNICSMATERIALENTRIESID, GwQuerySpec.EQUAL, tmOid);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMEExpDevicePartLink tm = (TMEExpDevicePartLink) qr.next();
                list.add(tm);
            }
        } else {
            GwQuerySpec qs = new GwQuerySpec(TMEExpDevicePartLink.class);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMEExpDevicePartLink tm = (TMEExpDevicePartLink) qr.next();
                list.add(tm);
            }
        }
        return list;
    }

    /**
     * 根据工艺物资条目获取机电材料属性集合
     *
     * @param tmOid
     * @return
     * @throws Exception
     */
    public static List<TMEEleMachinePartLink> getEleMachinePartInfo(String tmOid) throws Exception {
        List<TMEEleMachinePartLink> list = new ArrayList<TMEEleMachinePartLink>();
        if (!StringUtil.isEmpty(tmOid)) {
            GwQuerySpec qs = new GwQuerySpec(TMEEleMachinePartLink.class);
            qs.appendWhere(TMEEleMachinePartLink.TECHNICSMATERIALENTRIESID, GwQuerySpec.EQUAL, tmOid);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMEEleMachinePartLink tm = (TMEEleMachinePartLink) qr.next();
                list.add(tm);
            }
        } else {
            GwQuerySpec qs = new GwQuerySpec(TMEEleMachinePartLink.class);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                TMEEleMachinePartLink tm = (TMEEleMachinePartLink) qr.next();
                list.add(tm);
            }
        }
        return list;
    }

    /**
     * 下载工艺资源
     *
     * @throws Exception
     */
    public static File downloadTechnicsMaterialInfo(String tmType) throws Exception {
        String path = wthome + File.separator + "codebase" + File.separator + "ext" + File.separator + "ases" + File.separator + "techMaterial" + File.separator + "template" + File.separator;
        if ("exportStandardInfo".equals(tmType)) {
            path = path + "标准紧固件.xlsx";
            java.io.FileOutputStream writeFile = null;
            XSSFWorkbook workbook = null;
            XSSFCellStyle style = null;
            XSSFSheet sheet = null;
            XSSFRow titleRow = null;
            try {
                workbook = new XSSFWorkbook();
                style = workbook.createCellStyle(); // 样式对象
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                sheet = workbook.createSheet("标准紧固件");
                List<TMEStandPartLink> list = getTMEStandPartInfo("");
                // 设备表头
                titleRow = sheet.createRow(0);
                int i = 0;
                for (String key : standardInfoMap.keySet()) {
                    System.out.println(key);
                    titleRow.createCell(i).setCellValue(key);
                    i++;
                }
                for (int i1 = 0; i1 < list.size(); i1++) {
                    XSSFRow createRow = sheet.createRow(i1 + 1);
                    TMEStandPartLink link = list.get(i1);
                    TMEStandPartLinkBean tmeStandPartLinkBean = new TMEStandPartLinkBean();
                    BeanUtils.copyProperties(link, tmeStandPartLinkBean);
                    int m = 0;
                    for (String key : standardInfoMap.keySet()) {
                        String value = standardInfoMap.get(key);
                        Object standValue = getGetMethod(tmeStandPartLinkBean, value);
                        String standStringValue = "";
                        if (StringUtil.isEmpty(standValue)) {
                            standStringValue = "";
                        } else {
                            standStringValue = String.valueOf(standValue);
                        }
                        createRow.createCell(m).setCellValue(standStringValue);
                        m++;
                    }
                }
                writeFile = new java.io.FileOutputStream(path);
                workbook.write(writeFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            } finally {
                if (writeFile != null) {
                    try {
                        writeFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new File(path);

        } else if ("exportEleComponentsInfo".equals(tmType)) {
            path = path + "电子元器件.xlsx";
            java.io.FileOutputStream writeFile = null;
            XSSFWorkbook workbook = null;
            XSSFCellStyle style = null;
            XSSFSheet sheet = null;
            XSSFRow titleRow = null;
            try {
                workbook = new XSSFWorkbook();
                style = workbook.createCellStyle(); // 样式对象
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                sheet = workbook.createSheet("电子元器件");
                List<TMEEleComponentsPartLink> list = getEleComponentsPartInfo("");
                // 设备表头
                titleRow = sheet.createRow(0);
                int i = 0;
                for (String key : eleComponentsInfoMap.keySet()) {
                    System.out.println(key);
                    titleRow.createCell(i).setCellValue(key);
                    i++;
                }
                for (int i1 = 0; i1 < list.size(); i1++) {
                    XSSFRow createRow = sheet.createRow(i1 + 1);
                    TMEEleComponentsPartLink link = list.get(i1);
                    TMEEleComponentsPartLinkBean bean = new TMEEleComponentsPartLinkBean();
                    BeanUtils.copyProperties(link, bean);
                    int m = 0;
                    for (String key : eleComponentsInfoMap.keySet()) {
                        String value = eleComponentsInfoMap.get(key);
                        Object standValue = getGetMethod(bean, value);
                        String standStringValue = "";
                        if (StringUtil.isEmpty(standValue)) {
                            standStringValue = "";
                        } else {
                            standStringValue = String.valueOf(standValue);
                        }
                        createRow.createCell(m).setCellValue(standStringValue);
                        m++;
                    }
                }
                writeFile = new java.io.FileOutputStream(path);
                workbook.write(writeFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            } finally {
                if (writeFile != null) {
                    try {
                        writeFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new File(path);

        } else if ("exportNonMetallicInfo".equals(tmType)) {
            path = path + "非金属材料.xlsx";
            java.io.FileOutputStream writeFile = null;
            XSSFWorkbook workbook = null;
            XSSFCellStyle style = null;
            XSSFSheet sheet = null;
            XSSFRow titleRow = null;
            try {
                workbook = new XSSFWorkbook();
                style = workbook.createCellStyle(); // 样式对象
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                sheet = workbook.createSheet("非金属材料");
                List<TMENonMetallicPartLink> list = getNonMetallicPartInfo("");
                // 设备表头
                titleRow = sheet.createRow(0);
                int i = 0;
                for (String key : nonmetallicInfoMap.keySet()) {
                    System.out.println(key);
                    titleRow.createCell(i).setCellValue(key);
                    i++;
                }
                for (int i1 = 0; i1 < list.size(); i1++) {
                    XSSFRow createRow = sheet.createRow(i1 + 1);
                    TMENonMetallicPartLink link = list.get(i1);
                    TMENonMetallicPartLinkBean bean = new TMENonMetallicPartLinkBean();
                    BeanUtils.copyProperties(link, bean);
                    int m = 0;
                    for (String key : nonmetallicInfoMap.keySet()) {
                        String value = nonmetallicInfoMap.get(key);
                        Object standValue = getGetMethod(bean, value);
                        String standStringValue = "";
                        if (StringUtil.isEmpty(standValue)) {
                            standStringValue = "";
                        } else {
                            standStringValue = String.valueOf(standValue);
                        }
                        createRow.createCell(m).setCellValue(standStringValue);
                        m++;
                    }
                }
                writeFile = new java.io.FileOutputStream(path);
                workbook.write(writeFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            } finally {
                if (writeFile != null) {
                    try {
                        writeFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new File(path);
        } else if ("exportCompoundMaterialInfo".equals(tmType)) {
            path = path + "复合材料.xlsx";
            java.io.FileOutputStream writeFile = null;
            XSSFWorkbook workbook = null;
            XSSFCellStyle style = null;
            XSSFSheet sheet = null;
            XSSFRow titleRow = null;
            try {
                workbook = new XSSFWorkbook();
                style = workbook.createCellStyle(); // 样式对象
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                sheet = workbook.createSheet("复合材料");
                List<TMECompoundMaterialPartLink> list = getCompoundMaterialPartInfo("");
                // 设备表头
                titleRow = sheet.createRow(0);
                int i = 0;
                for (String key : compoundMaterialInfoMap.keySet()) {
                    System.out.println(key);
                    titleRow.createCell(i).setCellValue(key);
                    i++;
                }
                for (int i1 = 0; i1 < list.size(); i1++) {
                    XSSFRow createRow = sheet.createRow(i1 + 1);
                    TMECompoundMaterialPartLink link = list.get(i1);
                    TMECompoundMaterialPartLinkBean tmeStandPartLinkBean = new TMECompoundMaterialPartLinkBean();
                    BeanUtils.copyProperties(link, tmeStandPartLinkBean);
                    int m = 0;
                    for (String key : compoundMaterialInfoMap.keySet()) {
                        String value = compoundMaterialInfoMap.get(key);
                        Object standValue = getGetMethod(tmeStandPartLinkBean, value);
                        String standStringValue = "";
                        if (StringUtil.isEmpty(standValue)) {
                            standStringValue = "";
                        } else {
                            standStringValue = String.valueOf(standValue);
                        }
                        createRow.createCell(m).setCellValue(standStringValue);
                        m++;
                    }
                }
                writeFile = new java.io.FileOutputStream(path);
                workbook.write(writeFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            } finally {
                if (writeFile != null) {
                    try {
                        writeFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new File(path);
        } else if ("exportMetallicInfo".equals(tmType)) {
            path = path + "金属材料.xlsx";
            java.io.FileOutputStream writeFile = null;
            XSSFWorkbook workbook = null;
            XSSFCellStyle style = null;
            XSSFSheet sheet = null;
            XSSFRow titleRow = null;
            try {
                workbook = new XSSFWorkbook();
                style = workbook.createCellStyle(); // 样式对象
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);

                sheet = workbook.createSheet("金属材料");
                List<TMEMetallicPartLink> list = getMetallicPartInfo("");
                // 设备表头
                titleRow = sheet.createRow(0);
                int i = 0;
                for (String key : metallicInfoMap.keySet()) {
                    System.out.println(key);
                    titleRow.createCell(i).setCellValue(key);
                    i++;
                }
                for (int i1 = 0; i1 < list.size(); i1++) {
                    XSSFRow createRow = sheet.createRow(i1 + 1);
                    TMEMetallicPartLink link = list.get(i1);
                    TMEMetallicPartLinkBean tmeStandPartLinkBean = new TMEMetallicPartLinkBean();
                    BeanUtils.copyProperties(link, tmeStandPartLinkBean);
                    int m = 0;
                    for (String key : metallicInfoMap.keySet()) {
                        String value = metallicInfoMap.get(key);
                        Object standValue = getGetMethod(tmeStandPartLinkBean, value);
                        String standStringValue = "";
                        if (StringUtil.isEmpty(standValue)) {
                            standStringValue = "";
                        } else {
                            standStringValue = String.valueOf(standValue);
                        }
                        createRow.createCell(m).setCellValue(standStringValue);
                        m++;
                    }
                }
                writeFile = new java.io.FileOutputStream(path);
                workbook.write(writeFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            } finally {
                if (writeFile != null) {
                    try {
                        writeFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new File(path);
        } else if ("eleMachineInfo".equals(tmType)) {
            path = path + "机电材料.xlsx";
            java.io.FileOutputStream writeFile = null;
            XSSFWorkbook workbook = null;
            XSSFCellStyle style = null;
            XSSFSheet sheet = null;
            XSSFRow titleRow = null;
            try {
                workbook = new XSSFWorkbook();
                style = workbook.createCellStyle(); // 样式对象
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                sheet = workbook.createSheet("机电材料");
                List<TMEEleMachinePartLink> list = getEleMachinePartInfo("");
                // 设备表头
                titleRow = sheet.createRow(0);
                int i = 0;
                for (String key : eleMachineInfoMap.keySet()) {
                    System.out.println(key);
                    titleRow.createCell(i).setCellValue(key);
                    i++;
                }
                for (int i1 = 0; i1 < list.size(); i1++) {
                    XSSFRow createRow = sheet.createRow(i1 + 1);
                    TMEEleMachinePartLink link = list.get(i1);
                    TMEEleMachinePartLinkBean tmeEleMachinePartLink = new TMEEleMachinePartLinkBean();
                    BeanUtils.copyProperties(link, tmeEleMachinePartLink);
                    int m = 0;
                    for (String key : eleMachineInfoMap.keySet()) {
                        String value = eleMachineInfoMap.get(key);
                        Object standValue = getGetMethod(tmeEleMachinePartLink, value);
                        String standStringValue = "";
                        if (StringUtil.isEmpty(standValue)) {
                            standStringValue = "";
                        } else {
                            standStringValue = String.valueOf(standValue);
                        }
                        createRow.createCell(m).setCellValue(standStringValue);
                        m++;
                    }
                }
                writeFile = new java.io.FileOutputStream(path);
                workbook.write(writeFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            } finally {
                if (writeFile != null) {
                    try {
                        writeFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new File(path);
        } else if ("expDeviceInfo".equals(tmType)) {
            path = path + "火工品.xlsx";
            java.io.FileOutputStream writeFile = null;
            XSSFWorkbook workbook = null;
            XSSFCellStyle style = null;
            XSSFSheet sheet = null;
            XSSFRow titleRow = null;
            try {
                workbook = new XSSFWorkbook();
                style = workbook.createCellStyle(); // 样式对象
                style.setVerticalAlignment(VerticalAlignment.CENTER);
                style.setAlignment(HorizontalAlignment.CENTER);
                sheet = workbook.createSheet("火工品");
                List<TMEExpDevicePartLink> list = getExpDevicePartInfo("");
                // 设备表头
                titleRow = sheet.createRow(0);
                int i = 0;
                for (String key : expDeviceInfoMap.keySet()) {
                    System.out.println(key);
                    titleRow.createCell(i).setCellValue(key);
                    i++;
                }
                for (int i1 = 0; i1 < list.size(); i1++) {
                    XSSFRow createRow = sheet.createRow(i1 + 1);
                    TMEExpDevicePartLink link = list.get(i1);
                    TMEExpDevicePartLinkBean tmeExpDevicePartLinkBean = new TMEExpDevicePartLinkBean();
                    BeanUtils.copyProperties(link, tmeExpDevicePartLinkBean);
                    int m = 0;
                    for (String key : expDeviceInfoMap.keySet()) {
                        String value = expDeviceInfoMap.get(key);
                        Object standValue = getGetMethod(tmeExpDevicePartLinkBean, value);
                        String standStringValue = "";
                        if (StringUtil.isEmpty(standValue)) {
                            standStringValue = "";
                        } else {
                            standStringValue = String.valueOf(standValue);
                        }
                        createRow.createCell(m).setCellValue(standStringValue);
                        m++;
                    }
                }
                writeFile = new java.io.FileOutputStream(path);
                workbook.write(writeFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            } finally {
                if (writeFile != null) {
                    try {
                        writeFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new File(path);
        }
        //			begin add by yfn 2023-10-13
        //        导出工艺参数
        else if ("exportGLProcessParams".equals(tmType)) {
            return DownloadTechnicsReportUtil.exportGLProcessParams();
        }
        //        导出工艺知识
        else if ("exportGLProcessKnowledge".equals(tmType)) {
            return DownloadTechnicsReportUtil.exportGLProcessKnowledge();
        }

        else if ("exportGLProcessOtherKnowledge".equals(tmType)) {

            return DownloadTechnicsReportUtil.exportGLProcessOtherKnowledge();
        }
        //			end add by yfn 2023-10-13
        return null;

    }

    /**
     * 根据属性，获取get方法
     *
     * @param ob   对象
     * @param name 属性名
     * @return
     * @throws Exception
     */
    public static Object getGetMethod(Object ob, String name) throws Exception {
        Method[] m = ob.getClass().getMethods();
        for (int i = 0; i < m.length; i++) {
            if (("get" + name).toLowerCase().equals(m[i].getName().toLowerCase())) {
                return m[i].invoke(ob);
            }
        }
        return null;
    }

    /**
     * 删除工艺物资名称
     *
     * @param tmOid
     * @throws SQLException
     */
    public static void deleteTechMaterial(String tmOid) throws SQLException {
        if (!StringUtil.isEmpty(tmOid)) {
            StringBuffer selectSQL = new StringBuffer();
            DBConnUtil conn = null;
            try {
                selectSQL.append("DELETE  from TECHNICSMATERIAL t where (classnamea2a2||':'||IDA2A2) ='" + tmOid + "'");
                conn = new DBConnUtil();
                conn.executeQuery(selectSQL.toString());
                conn.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }

    }

    /**
     * 删除工艺物资名称下所有的数据字典
     *
     * @param tmOid
     * @throws SQLException
     */
    public static void deleteAllDictionary(String tmOid) throws SQLException {
        if (!StringUtil.isEmpty(tmOid)) {
            StringBuffer selectSQL = new StringBuffer();
            DBConnUtil conn = null;
            try {
                selectSQL.append("DELETE  from TECHNICSMATERIALLINK where TECHNICSMATERIALID='" + tmOid + "'");
                conn = new DBConnUtil();
                conn.executeQuery(selectSQL.toString());
                conn.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }

    }

    /**
     * 删除工艺物资条目
     *
     * @throws SQLException
     */
    public static void deleteTechMaterialEntries(String tmeOid) throws SQLException {
        if (!StringUtil.isEmpty(tmeOid)) {
            StringBuffer selectSQL = new StringBuffer();
            DBConnUtil conn = null;
            try {
                selectSQL.append("DELETE  from TECHNICSMATERIALENTRIES t where (classnamea2a2||':'||IDA2A2) ='" + tmeOid + "'");
                conn = new DBConnUtil();
                conn.executeQuery(selectSQL.toString());
                conn.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }

    }

    /**
     * 删除条目对应的属性信息
     *
     * @throws SQLException
     */
    public static void deleteAttrInfo(String tmeOid, String descption) throws SQLException {
        if (!StringUtil.isEmpty(tmeOid) && !StringUtil.isEmpty(descption)) {
            String tableName = "";
            if ("标准紧固件".equals(descption)) {
                tableName = "TMESTANDPARTLINK";
            } else if ("电子元器件".equals(descption)) {
                tableName = "TMEELECOMPONENTSPARTLINK";
            } else if ("非金属材料".equals(descption)) {
                tableName = "TMENONMETALLICPARTLINK";
            } else if ("复合材料".equals(descption)) {
                tableName = "TMECOMPOUNDMATERIALPARTLINK";
            } else if ("金属材料".equals(descption)) {
                tableName = "TMEMETALLICPARTLINK";
            }

            StringBuffer selectSQL = new StringBuffer();
            DBConnUtil conn = null;
            try {
                selectSQL.append("DELETE  from " + tableName + " t where TECHNICSMATERIALENTRIESID='" + tmeOid + "'");
                conn = new DBConnUtil();
                conn.executeQuery(selectSQL.toString());
                conn.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }

    }

    /**
     * 根据工艺物资条目获取标电子元器件属性集合
     *
     * @return
     * @throws Exception
     */
    public static String validdateData(Set<String> set) throws Exception {
        StringBuffer resMsg = new StringBuffer();
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        StringBuffer selectSQL = new StringBuffer();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            selectSQL.append("SELECT DISTINCT TECHNICSMATERIALENTRIESNUMBE from TECHNICSMATERIALENTRIES");
            ps = conn.prepareStatement(selectSQL.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                String technicsmaterialentriesnumbe = rs.getString("TECHNICSMATERIALENTRIESNUMBE");
                if (set.contains(technicsmaterialentriesnumbe)) {
                    resMsg.append("物资编码:" + technicsmaterialentriesnumbe + "已存在\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return resMsg.toString();
    }

    /**
     * 获取工艺物资条目id和容器id
     *
     * @return
     * @throws Exception
     */
    public static Map getTMEidByNumber(String tmeNumber) throws Exception {
        HashMap<String, String> map = new HashMap<String, String>();
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        StringBuffer selectSQL = new StringBuffer();
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashSet<String> hashSet = new HashSet<String>();
        try {
            selectSQL.append("SELECT A0.IDA3CONTAINERREFERENCE AS containOid,IDA2A2 as docOid FROM TechnicsMaterialEntries A0 WHERE A0.TechnicsMaterialEntriesNumbe = '" + tmeNumber + "'");
            ps = conn.prepareStatement(selectSQL.toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                String containOid = rs.getString("containOid");
                String docOid = rs.getString("docOid");
                map.put("containOid", containOid);
                map.put("docOid", docOid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return map;
    }

    /**
     * 保存未被申请的编码信息
     *
     * @param docOid
     * @param technicsDocNumber
     */
    public static void updateTechnicaQuotaInfo(String docOid, String technicsDocNumber) throws Exception {
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        StringBuffer selectSQL = new StringBuffer();
        PreparedStatement ps = null;
        HashSet<String> hashSet = new HashSet<String>();
        try {
            selectSQL.append("UPDATE TECHNICAQUOTANUMBER SET TECHNICSOID='" + docOid + "' WHERE TECHNICSOID='" + technicsDocNumber + "' ");
            ps = conn.prepareStatement(selectSQL.toString());
            ps.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
    }

    /**
     * 方法功能:
     * 校验工艺物资名称
     *
     * @param map
     * @return java.lang.String
     * @author MChen
     * @date 2021/2/23
     */
    public static String validateTmInfo(Map<String, String> map) throws SQLException {
        StringBuffer buffer = new StringBuffer();
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashSet<String> hashSet = new HashSet<String>();
        try {
            for (String key : map.keySet()) {
                String value = map.get(key);
                StringBuffer selectSQL = new StringBuffer();
                selectSQL.append("SELECT a.GWKEYID from TECHNICSMATERIALLINK a ,TECHNICSMATERIAL b where a.TECHNICSMATERIALID='ext.ases.techMaterial.TechnicsMaterial:'||" +
                        "b.IDA2A2 and b.TECHNICSMATERIALNAME='" + key + "' and a.DICTIONARYID='" + value + "'");
                ps = conn.prepareStatement(selectSQL.toString());
                rs = ps.executeQuery();
                if (!rs.next()) {
                    buffer.append(key + "集合不存在值:" + value + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return buffer.toString();

    }

    /**
     * 获取物资分类信息
     *
     * @return
     */
    public static Map<String, List<String>> getWZFLInfo(String nodeName) throws SQLException {
        Map<String, List<String>> resultMap = new HashMap<String, List<String>>();
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashSet<String> hashSet = new HashSet<String>();
        try {
            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("SELECT b.NODENAME as pName,a.NODENAME as name from(SELECT * FROM GLCLASSIFICATIONNODE START WITH IDA2A2 = " +
                    "(SELECT IDA2A2 from GLCLASSIFICATIONNODE where NODENAME='" + nodeName + "')" +
                    "CONNECT BY PRIOR IDA2A2 = TOPID) a ,(SELECT * FROM GLCLASSIFICATIONNODE START WITH IDA2A2 =" +
                    "  (SELECT IDA2A2 from GLCLASSIFICATIONNODE where NODENAME='" + nodeName + "') CONNECT BY PRIOR IDA2A2 = TOPID )b " +
                    "where a.TOPID=b.IDA2A2");
            ps = conn.prepareStatement(selectSQL.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                String pName = rs.getString("pName");
                String name = rs.getString("name");
                if (resultMap.containsKey(pName)) {
                    List<String> nameList = resultMap.get(pName);
                    nameList.add(name);
                    resultMap.put(pName, nameList);
                } else {
                    List<String> nameList = new ArrayList<String>();
                    nameList.add(name);
                    resultMap.put(pName, nameList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return resultMap;

    }

    /**
     * 判断条目是否有物资分类
     *
     * @param ids
     * @param type
     * @return
     */
    public static String validateWZFLData(String[] ids, String type) throws SQLException {
        StringBuffer buffer = new StringBuffer();
        String tableName = "";
        if ("电子元器件".equals(type)) {
            tableName = "TMEELECOMPONENTSPARTLINK";
        } else if ("标准紧固件".equals(type)) {
            tableName = "TMESTANDPARTLINK";
        } else if ("金属材料".equals(type)) {
            tableName = "TMEMETALLICPARTLINK";
        } else if ("非金属材料".equals(type)) {
            tableName = "TMENONMETALLICPARTLINK";
        } else if ("复合材料".equals(type)) {
            tableName = "TMECOMPOUNDMATERIALPARTLINK";
        } else if ("机电材料".equals(type)) {
            tableName = "TMEELEMACHINEPARTLINK";
        } else if ("火工品".equals(type)) {
            tableName = "TMEEXPDEVICEPARTLINK";
        }
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashSet<String> hashSet = new HashSet<String>();
        try {
            for (String id : ids) {
                StringBuffer selectSQL = new StringBuffer();
                selectSQL.append("SELECT WZMC, WZFL from " + tableName + " where TECHNICSMATERIALENTRIESID='" + id + "'");
                ps = conn.prepareStatement(selectSQL.toString());
                rs = ps.executeQuery();
                while (rs.next()) {

                    String wzfl = rs.getString("WZFL");
                    String wzmc = rs.getString("WZMC");
                    if ("".equals(wzfl) || "null".equals(wzfl) || wzfl == null) {
                        buffer.append(type + "物资名称:" + wzmc + "应设置物资分类\n");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return buffer.toString();
    }

    /**
     * 更新工艺物资条目对应的物资分类
     *
     * @param ids
     * @param type
     * @return
     */
    public static void updateWZFLData(String[] ids, String type, String wzfl) throws SQLException {
        String tableName = "";
        if ("电子元器件".equals(type)) {
            tableName = "TMEELECOMPONENTSPARTLINK";
        } else if ("标准紧固件".equals(type)) {
            tableName = "TMESTANDPARTLINK";
        } else if ("金属材料".equals(type)) {
            tableName = "TMEMETALLICPARTLINK";
        } else if ("非金属材料".equals(type)) {
            tableName = "TMENONMETALLICPARTLINK";
        } else if ("复合材料".equals(type)) {
            tableName = "TMECOMPOUNDMATERIALPARTLINK";
        }
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        HashSet<String> hashSet = new HashSet<String>();
        try {
            for (String id : ids) {
                StringBuffer selectSQL = new StringBuffer();
                selectSQL.append("UPDATE " + tableName + " SET WZFL='" + wzfl + "' WHERE TECHNICSMATERIALENTRIESID='" + id + "' ");
                ps = conn.prepareStatement(selectSQL.toString());
                ps.executeUpdate();
                conn.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
    }

    /**
     * 向EDA取号
     *
     * @return
     */
    public static String sendToEda(Object pbo, Object self) {
        String msg = "发送成功";
        try {
            if (pbo instanceof WTDocument) {
                Map<String, String> map = getSendToEDANumber((WTDocument) pbo);
                ExpXmlTOEdaXml(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "发送异常";
        }
        return msg;

    }

    /**
     * 将null或者字符串null转为string的空值
     *
     * @param obj
     * @return
     */
    public static String convertToString(Object obj) {
        if (obj == null) {
            return "";
        } else {
            return String.valueOf(obj);
        }
    }

    /**
     * 拼接成EDA需要的xml
     *
     * @return
     */
    public static String ExpXmlTOEdaXml(Map<String, String> map) throws Exception {
        String msg = "发送成功";
        WTUser user = (WTUser) SessionHelper.getPrincipal();
        String email = user.getName() + "@149.sast.casc";
        for (String key : map.keySet()) {
            String type = map.get(key);
            Class cusclass = null;
            if ("电子元器件".equals(type)) {
                cusclass = TMEEleComponentsPartLink.class;
            } else if ("标准紧固件".equals(type)) {
                cusclass = TMEStandPartLink.class;
            } else if ("金属材料".equals(type)) {
                cusclass = TMEMetallicPartLink.class;
            } else if ("非金属材料".equals(type)) {
                cusclass = TMENonMetallicPartLink.class;
            } else if ("复合材料".equals(type)) {
                cusclass = TMECompoundMaterialPartLink.class;
            }
            GwQuerySpec qs = new GwQuerySpec(cusclass);
            qs.appendWhere("TECHNICSMATERIALID", GwQuerySpec.EQUAL, "ext.ases.techMaterial.TechnicsMaterialEntries:" + key);
            GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                Object tm = (Object) qr.next();
                // 将exp打包的xml解析为eda想要的xml
                String bmdj = "";
                Document document = DocumentHelper.createDocument();
                document.setXMLEncoding("UTF-8");
                Element paramsElement = document.addElement("PARAMS");
                Element basicElement = paramsElement.addElement("BASIC");
                if (tm instanceof TMEEleComponentsPartLink) {
                    TMEEleComponentsPartLink link = (TMEEleComponentsPartLink) tm;
                    String csNumber = link.getWzbm();
                    String classificationNode = link.getWzfl();
                    basicElement.addElement("CLASSIFICATIONNODE").addText(classificationNode);
                    String name = link.getWzmc();
                    basicElement.addElement("CSNUMBER").addText(csNumber);
                    basicElement.addElement("NAME").addText(name);
                    basicElement.addElement("TYPE").addText("元器件");
                    // iba
                    Element ibaElement = basicElement.addElement("IBA");
                    ibaElement.addElement("FULLNAME").addText(name);
                    for (String eleComp : eleComponentsSendMap.keySet()) {
                        String nbmc = eleComponentsSendMap.get(eleComp);
                        String value = convertToString(getGetMethod(link, eleComp));
                        if ("BMDJ".equals(eleComp)) {
                            bmdj = value;
                        }
                        if ("XH".equals(eleComp)) {
                            //型号
                            ibaElement.addElement("MODEL").addText(value);
                        } else if ("SCCJ".equals(eleComp)) {
                            //生产厂家
                            ibaElement.addElement("MANUQC").addText(value);
                            ibaElement.addElement("MANUJC").addText(value);
                        } else {
                            ibaElement.addElement(nbmc).addText(value);
                        }
                    }
                } else if (tm instanceof TMEStandPartLink) {
                    TMEStandPartLink link = (TMEStandPartLink) tm;
                    String csNumber = link.getWzbm();
                    String classificationNode = link.getWzfl();
                    basicElement.addElement("CLASSIFICATIONNODE").addText(classificationNode);
                    String name = link.getWzmc();
                    basicElement.addElement("CSNUMBER").addText(csNumber);
                    basicElement.addElement("NAME").addText(name);
                    basicElement.addElement("TYPE").addText("标准件");
                    Element ibaElement = basicElement.addElement("IBA");
                    ibaElement.addElement("FULLNAME").addText(name);
                    for (String eleComp : standardSendMap.keySet()) {
                        String nbmc = standardSendMap.get(eleComp);
                        String value = convertToString(getGetMethod(link, eleComp));
                        if ("BMDJ".equals(eleComp)) {
                            bmdj = value;
                        }
                        if ("XH".equals(eleComp)) {
                            //型号
                            ibaElement.addElement("MODEL").addText(value);
                        } else if ("SCCJ".equals(eleComp)) {
                            //生产厂家
                            ibaElement.addElement("MANUQC").addText(value);
                            ibaElement.addElement("MANUJC").addText(value);
                        } else {
                            ibaElement.addElement(nbmc).addText(value);
                        }
                    }

                } else if (tm instanceof TMEMetallicPartLink) {
                    TMEMetallicPartLink link = (TMEMetallicPartLink) tm;
                    String csNumber = link.getWzbm();
                    String classificationNode = link.getWzfl();
                    basicElement.addElement("CLASSIFICATIONNODE").addText(classificationNode);
                    String name = link.getWzmc();
                    basicElement.addElement("CSNUMBER").addText(csNumber);
                    basicElement.addElement("NAME").addText(name);
                    basicElement.addElement("TYPE").addText("金属材料");
                    Element ibaElement = basicElement.addElement("IBA");
                    ibaElement.addElement("FULLNAME").addText(name);
                    for (String eleComp : metallicSendMap.keySet()) {
                        String nbmc = metallicSendMap.get(eleComp);
                        String value = convertToString(getGetMethod(link, eleComp));
                        if ("BMDJ".equals(eleComp)) {
                            bmdj = value;
                        }
                        if ("XH".equals(eleComp)) {
                            //型号
                            ibaElement.addElement("MODEL").addText(value);
                        } else if ("SCCJ".equals(eleComp)) {
                            //生产厂家
                            ibaElement.addElement("MANUQC").addText(value);
                            ibaElement.addElement("MANUJC").addText(value);
                        } else {
                            ibaElement.addElement(nbmc).addText(value);
                        }
                    }
                } else if (tm instanceof TMENonMetallicPartLink) {
                    TMENonMetallicPartLink link = (TMENonMetallicPartLink) tm;
                    String csNumber = link.getWzbm();
                    String classificationNode = link.getWzfl();
                    basicElement.addElement("CLASSIFICATIONNODE").addText(classificationNode);
                    String name = link.getWzmc();
                    basicElement.addElement("CSNUMBER").addText(csNumber);
                    basicElement.addElement("NAME").addText(name);
                    basicElement.addElement("TYPE").addText("金属材料");
                    Element ibaElement = basicElement.addElement("IBA");
                    ibaElement.addElement("FULLNAME").addText(name);
                    for (String eleComp : nonmetallicSendMap.keySet()) {
                        String nbmc = nonmetallicSendMap.get(eleComp);
                        String value = convertToString(getGetMethod(link, eleComp));
                        if ("BMDJ".equals(eleComp)) {
                            bmdj = value;
                        }
                        if ("XH".equals(eleComp)) {
                            //型号
                            ibaElement.addElement("MODEL").addText(value);
                        } else if ("SCCJ".equals(eleComp)) {
                            //生产厂家
                            ibaElement.addElement("MANUQC").addText(value);
                            ibaElement.addElement("MANUJC").addText(value);
                        } else {
                            ibaElement.addElement(nbmc).addText(value);
                        }
                    }
                } else if (tm instanceof TMECompoundMaterialPartLink) {
                    TMECompoundMaterialPartLink link = (TMECompoundMaterialPartLink) tm;
                    String csNumber = link.getWzbm();
                    String classificationNode = link.getWzfl();
                    basicElement.addElement("CLASSIFICATIONNODE").addText(classificationNode);
                    String name = link.getWzmc();
                    basicElement.addElement("CSNUMBER").addText(csNumber);
                    basicElement.addElement("NAME").addText(name);
                    basicElement.addElement("TYPE").addText("金属材料");
                    Element ibaElement = basicElement.addElement("IBA");
                    ibaElement.addElement("FULLNAME").addText(name);
                    for (String eleComp : compoundMaterialSendMap.keySet()) {
                        String nbmc = compoundMaterialSendMap.get(eleComp);
                        String value = convertToString(getGetMethod(link, eleComp));
                        if ("BMDJ".equals(eleComp)) {
                            bmdj = value;
                        }
                        if ("XH".equals(eleComp)) {
                            //型号
                            ibaElement.addElement("MODEL").addText(value);
                        } else if ("SCCJ".equals(eleComp)) {
                            //生产厂家
                            ibaElement.addElement("MANUQC").addText(value);
                            ibaElement.addElement("MANUJC").addText(value);
                        } else {
                            ibaElement.addElement(nbmc).addText(value);
                        }
                    }
                }
                try {
                    //发送数据
                    deliverInfoToEda(document.asXML().toString(), "", bmdj, "149", email);
                } catch (Exception e) {
                    e.printStackTrace();
                    msg = "发送失败";
                }
            }
        }
        return msg;
    }

    public static String deliverInfoToEda(String partInfo, String attaUrl, String bmdj, String unit, String userName) {
        // TODO调用EDA接口
        // 发送EDA
        String endpoint = "http:// 10.112.5.21:80/alms/soa/ZYKService?wsdl";
        String backMsg = "";
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(endpoint);
            call.setOperationName(new QName("http://servcie.alms4zyk.alms.acconsys.com/", "edaPartSync"));
            call.addParameter("partInfo", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("attaUrl", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("bmdj", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("unit", XMLType.XSD_STRING, ParameterMode.IN);
            call.addParameter("userName", XMLType.XSD_STRING, ParameterMode.IN);
            call.setReturnType(XMLType.XSD_STRING);
            backMsg = (String) call.invoke(new Object[]{partInfo, attaUrl, bmdj, unit, userName});
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        System.out.println("========backMsg==========" + backMsg);
        return backMsg;
    }

    /**
     * 获取发送EDA编号集合
     *
     * @return
     */
    public static Map<String, String> getSendToEDANumber(WTDocument doc) throws SQLException {
        Map<String, String> resultMap = new HashMap<String, String>();
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String stringValue = doc.getPersistInfo().getObjectIdentifier().getStringValue();

            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("SELECT b.ida2a2 as technicsOid ,b.description as tmeType from TechnicaQuotaNumber a,TECHNICSMATERIALENTRIES b" +
                    "  where a.QUOTANUMBER=b.TECHNICSMATERIALENTRIESNUMBE and a.TECHNICSOID='" + stringValue + "'");
            ps = conn.prepareStatement(selectSQL.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                String technicsOid = rs.getString("technicsOid");
                String tmeType = rs.getString("tmeType");
                resultMap.put(technicsOid, tmeType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return resultMap;

    }

    /**
     * 获取工艺物资名称所有字典名称
     * @return
     */
    public static Map<String, Vector<String>> getAllTechncisMaterialDic() throws SQLException {

        Map<String, Vector<String>> resultMap = new HashMap<String, Vector<String>>();
        Connection conn = OracleDataSource.getOracleDataSource().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            StringBuffer selectSQL = new StringBuffer();
            selectSQL.append("select a.DICTIONARYID ,b.TECHNICSMATERIALNAME from TECHNICSMATERIALLINK a,TECHNICSMATERIAL b where a.TECHNICSMATERIALID='ext.ases.techMaterial.TechnicsMaterial:'||b.IDA2A2");
            ps = conn.prepareStatement(selectSQL.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                String dictionaryid = rs.getString("DICTIONARYID");
                String technicsmaterialname = rs.getString("TECHNICSMATERIALNAME");
                if (resultMap.containsKey(technicsmaterialname)) {
                    Vector<String> vector = resultMap.get(technicsmaterialname);
                    vector.add(dictionaryid);
                }else{
                    Vector<String> vector = new Vector<String>();
                    vector.add(dictionaryid);
                    resultMap.put(technicsmaterialname,vector);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return resultMap;
    }

}
