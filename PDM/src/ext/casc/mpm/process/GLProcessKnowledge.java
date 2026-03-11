package ext.casc.mpm.process;

import com.ptc.netmarkets.model.NmObject;
import com.ptc.netmarkets.model.NmSimpleOid;
import ext.sast.common.fc.CmPersistable;
import wt.util.WTException;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * 存放每行数据
 */
public class GLProcessKnowledge implements CmPersistable {
    public GLProcessKnowledge() {

    }

    private String keyId = "";
    private String xuHao = "";   //序号
    private String sheetName = "";  //sheet名称
    private String smallType = "";   //小类

    private String state = "";  //状态
    private String zhuanye = "";  //专业
    private String column1 = "";
    private String column2 = "";
    private String column3 = "";
    private String column4 = "";
    private String column5 = "";
    private String column6 = "";
    private String column7 = "";
    private String column8 = "";
    private String column9 = "";
    private String column10 = "";
    private String column11 = "";
    private String column12 = "";

    private Map<String,String> extAttris = new HashMap<String, String>();

    public static final String SHEETNAME = "sheetName";
    public static final String XUHAO = "xuHao";
    public static final String SMALLTYPE = "smallType";

    public static final String STATE = "state";
    public static final String ZHUANYE = "zhuanye";
    public static final String COLUMN1 = "column1";
    public static final String COLUMN2 = "column2";
    public static final String COLUMN3 = "column3";
    public static final String COLUMN4 = "column4";
    public static final String COLUMN5 = "column5";
    public static final String COLUMN6 = "column6";
    public static final String COLUMN7 = "column7";
    public static final String COLUMN8 = "column8";
    public static final String COLUMN9 = "column9";
    public static final String COLUMN10 = "column10";
    public static final String COLUMN11 = "column11";
    public static final String COLUMN12 = "column12";

    @Override
    public GLProcessKnowledge getObject(ResultSet rs) throws Exception {
        if (rs != null) {
            setKeyId(rs.getString(KEY_ID));
            setSheetName(rs.getString(SHEETNAME));
            setSmallType(rs.getString(SMALLTYPE));
            setXuHao(rs.getString(XUHAO));
            setState(rs.getString(STATE));
            setZhuanye(rs.getString(ZHUANYE));
            setColumn1(rs.getString(COLUMN1));
            setColumn2(rs.getString(COLUMN2));
            setColumn3(rs.getString(COLUMN3));
            setColumn4(rs.getString(COLUMN4));
            setColumn5(rs.getString(COLUMN5));
            setColumn6(rs.getString(COLUMN6));
            setColumn7(rs.getString(COLUMN7));
            setColumn8(rs.getString(COLUMN8));
            setColumn9(rs.getString(COLUMN9));
            setColumn10(rs.getString(COLUMN10));
            setColumn11(rs.getString(COLUMN11));
            setColumn12(rs.getString(COLUMN12));
        }
        return this;
    }


    @Override
    public Map<String, Object> getUpdateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(SHEETNAME, sheetName);
        ret.put(STATE, state);
        ret.put(XUHAO, xuHao);
        ret.put(SMALLTYPE, smallType);
        ret.put(ZHUANYE, zhuanye);
        ret.put(COLUMN1, column1);
        ret.put(COLUMN2, column2);
        ret.put(COLUMN3, column3);
        ret.put(COLUMN4, column4);
        ret.put(COLUMN5, column5);
        ret.put(COLUMN6, column6);
        ret.put(COLUMN7, column7);
        ret.put(COLUMN8, column8);
        ret.put(COLUMN9, column9);
        ret.put(COLUMN10, column10);
        ret.put(COLUMN11, column11);
        ret.put(COLUMN12, column12);
        return ret;
    }

    @Override
    public Map<String, Object> getCreateMap() {
        Map<String, Object> ret = new HashMap();
        ret.put(KEY_ID, keyId);
        ret.put(SHEETNAME, sheetName);
        ret.put(STATE, state);
        ret.put(XUHAO, xuHao);
        ret.put(SMALLTYPE, smallType);
        ret.put(ZHUANYE, zhuanye);
        ret.put(COLUMN1, column1);
        ret.put(COLUMN2, column2);
        ret.put(COLUMN3, column3);
        ret.put(COLUMN4, column4);
        ret.put(COLUMN5, column5);
        ret.put(COLUMN6, column6);
        ret.put(COLUMN7, column7);
        ret.put(COLUMN8, column8);
        ret.put(COLUMN9, column9);
        ret.put(COLUMN10, column10);
        ret.put(COLUMN11, column11);
        ret.put(COLUMN12, column12);
        return ret;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    @Override
    public Object getKeyId() {
        return this.keyId;
    }


    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getSmallType() {
        return smallType;
    }

    public void setSmallType(String smallType) {
        this.smallType = smallType;
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public String getColumn2() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2 = column2;
    }

    public String getColumn3() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3 = column3;
    }

    public String getColumn4() {
        return column4;
    }

    public void setColumn4(String column4) {
        this.column4 = column4;
    }

    public String getColumn5() {
        return column5;
    }

    public void setColumn5(String column5) {
        this.column5 = column5;
    }

    public String getColumn6() {
        return column6;
    }

    public void setColumn6(String column6) {
        this.column6 = column6;
    }

    public String getColumn7() {
        return column7;
    }

    public void setColumn7(String column7) {
        this.column7 = column7;
    }

    public String getColumn8() {
        return column8;
    }

    public void setColumn8(String column8) {
        this.column8 = column8;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getXuHao() {
        return xuHao;
    }

    public void setXuHao(String xuHao) {
        this.xuHao = xuHao;
    }

    public String getZhuanye() {
        return zhuanye;
    }

    public void setZhuanye(String zhuanye) {
        this.zhuanye = zhuanye;
    }

    public String getColumn9() {
        return column9;
    }

    public void setColumn9(String column9) {
        this.column9 = column9;
    }

    public String getColumn10() {
        return column10;
    }

    public void setColumn10(String column10) {
        this.column10 = column10;
    }

    public String getColumn11() {
        return column11;
    }

    public void setColumn11(String column11) {
        this.column11 = column11;
    }

    public String getColumn12() {
        return column12;
    }

    public void setColumn12(String column12) {
        this.column12 = column12;
    }
    @Override
    public String toString() {
        return "GLProcessParams{" +
                "keyId='" + keyId + '\'' +
                ", zhuanye='" + zhuanye + '\'' +
                ", column1='" + column1 + '\'' +
                ", column2='" + column2 + '\'' +
                ", column3='" + column3 + '\'' +
                ", column4='" + column4 + '\'' +
                ", column5='" + column5 + '\'' +
                ", column6='" + column6 + '\'' +
                ", column7='" + column7 + '\'' +
                ", column8='" + column8 + '\'' +
                ", column9='" + column9 + '\'' +
                ", column10='" + column10 + '\'' +
                ", column11='" + column11 + '\'' +
                ", column12='" + column12 + '\'' +
                '}';

    }

    public Map<String, String> getExtAttris() {
        return extAttris;
    }

    public void setExtAttris(Map<String, String> extAttris) {
        this.extAttris = extAttris;
    }

    public String getOid() {
        return this.keyId;
    }

    public static String getBusinessObjectReferenceFromOid(String s) {
        String s1 = null;
        if (s.contains("_")) {
            int i = s.indexOf("_");
            if (i != -1)
                s1 = s.substring(0, i);
        } else {
            s1 = s;
        }
        return s1;
    }

    public NmObject getNmObject() throws WTException {
        NmSimpleOid nmsimpleoid = new NmSimpleOid();
        nmsimpleoid.setInternalName(getOid());
        return NmObject.newNmObject(nmsimpleoid);
    }

}

