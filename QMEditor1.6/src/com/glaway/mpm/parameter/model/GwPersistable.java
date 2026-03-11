package com.glaway.mpm.parameter.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Map;

/**
 * 自定义的数据库表需要继承的公共接口<br>
 *
 * @author 龙秀川 <b>示例：</b> <code><pre>
 * public class CmProtelRecord implements CmPersistable {
 *    private static final long  serialVersionUID = 1209963693892244500L;
 *
 *    public static final String TYPE_COM         = "电路板组件";
 *    public static final String TYPE_PCB         = "印制板零件";
 *    public static final String TYPE_SCH         = "电路原理图";
 *
 *    public static final String PARENT_NUMBER    = "cmParentNumber";
 *    public static final String NUMBER           = "cmNumber";
 *    public static final String NAME             = "cmName";
 *    public static final String PART_TYPE        = "cmPartType";
 *    public static final String CONTAINER_REF    = "cmContainerRef";
 *    public static final String CREATE_STAMP     = "cmCreateStamp";
 *    public static final String UPDATE_STAMP     = "cmUpdateStamp";
 *
 *    private String             keyId;
 *    private String             parentNumber;
 *    private String             number;
 *    private String             name;
 *    private String             partType;
 *    private String             containerRef;
 *    private Timestamp          createStamp;
 *    private Timestamp          updateStamp;
 *
 *    public CmProtelRecord() {}
 *
 *    public CmProtelRecord(String parentNumber, String number, String name, String partType, String containerRef) {
 *       this.parentNumber = parentNumber;
 *       this.number = number;
 *       this.name = name;
 *       this.partType = partType;
 *       this.containerRef = containerRef;
 *       this.keyId = generateKeyId();
 *    }
 *
 *    public Object getKeyId() {
 *       return keyId;
 *    }
 *
 *    public CmPersistable getObject(ResultSet rs) throws Exception {
 *       if (rs != null) {
 *          setKeyId(rs.getString(KEY_ID));
 *          setParentNumber(rs.getString(PARENT_NUMBER));
 *          setNumber(rs.getString(NUMBER));
 *          setName(rs.getString(NAME));
 *          setPartType(rs.getString(PART_TYPE));
 *          setContainerRef(rs.getString(CONTAINER_REF));
 *          setCreateStamp(rs.getTimestamp(CREATE_STAMP));
 *          setUpdateStamp(rs.getTimestamp(UPDATE_STAMP));
 *       }
 *       return this;
 *    }
 *
 *    public Map getCreateMap() {
 *       Timestamp now = new Timestamp(new Date().getTime());
 *
 *       HashMap ret = new HashMap(7);
 *       ret.put(PARENT_NUMBER, parentNumber);
 *       ret.put(NUMBER, number);
 *       ret.put(NAME, name);
 *       ret.put(PART_TYPE, partType);
 *       ret.put(CONTAINER_REF, containerRef);
 *       ret.put(CREATE_STAMP, now);
 *       ret.put(UPDATE_STAMP, now);
 *
 *       return ret;
 *    }
 *
 *    public Map getUpdateMap() {
 *       Timestamp now = new Timestamp(new Date().getTime());
 *
 *       HashMap ret = new HashMap(6);
 *       ret.put(PARENT_NUMBER, parentNumber);
 *       ret.put(NUMBER, number);
 *       ret.put(NAME, name);
 *       ret.put(PART_TYPE, partType);
 *       ret.put(CONTAINER_REF, containerRef);
 *       ret.put(UPDATE_STAMP, now);
 *
 *       return ret;
 *    }
 *
 *    private String generateKeyId() {
 *       return this.parentNumber + '-' + this.number;
 *    }
 *
 *    public String getContainerRef() {
 *       return this.containerRef;
 *    }
 *
 *    public void setContainerRef(String containerRef) {
 *       this.containerRef = containerRef;
 *    }
 *
 *    public Timestamp getCreateStamp() {
 *       return this.createStamp;
 *    }
 *
 *    public void setCreateStamp(Timestamp createStamp) {
 *       this.createStamp = createStamp;
 *    }
 *
 *    public String getName() {
 *       return this.name;
 *    }
 *
 *    public void setName(String name) {
 *       this.name = name;
 *    }
 *
 *    public String getNumber() {
 *       return this.number;
 *    }
 *
 *    public void setNumber(String number) {
 *       this.number = number;
 *    }
 *
 *    public String getParentNumber() {
 *       return this.parentNumber;
 *    }
 *
 *    public void setParentNumber(String parentNumber) {
 *       this.parentNumber = parentNumber;
 *    }
 *
 *    public String getPartType() {
 *       return this.partType;
 *    }
 *
 *    public void setPartType(String partType) {
 *       this.partType = partType;
 *    }
 *
 *    public Timestamp getUpdateStamp() {
 *       return this.updateStamp;
 *    }
 *
 *    public void setUpdateStamp(Timestamp updateStamp) {
 *       this.updateStamp = updateStamp;
 *    }
 *
 *    public void setKeyId(String keyId) {
 *       this.keyId = keyId;
 *    }
 * }
 * </pre></code>
 */
public interface GwPersistable extends Serializable {
    /**
     * 所有自定义表必须定义的主键字段名称<br>
     * 该值不能为其它值，它是CmPersistenceHelper进行数据库维护时数据对象的唯一标识
     */
    public static final String KEY_ID = "gwKeyId";

    /**
     * 根据数据库搜索返回的结果重置数据对象实例<br>
     * <i><b>注意：</b>在该方法的实现中只从rs中获取数据并设置本地值域，不要在该方法中调用rs.next()等可能影响数据集或rs指针的操作</i><br>
     * <b>示例：</b> <code><pre>
     *    public CmPersistable getObject(ResultSet rs) throws Exception {
     *       if (rs != null) {
     *          setKeyId(rs.getString(KEY_ID));
     *          setParentNumber(rs.getString(PARENT_NUMBER));
     *          setNumber(rs.getString(NUMBER));
     *          setName(rs.getString(NAME));
     *          setPartType(rs.getString(PART_TYPE));
     *          setContainerRef(rs.getString(CONTAINER_REF));
     *          setCreateStamp(rs.getTimestamp(CREATE_STAMP));
     *          setUpdateStamp(rs.getTimestamp(UPDATE_STAMP));
     *       }
     *       return this;
     *    }
     * </pre></code>
     *
     * @param rs
     *            数据库搜索后返回的一条记录
     * @return 当前CmPersistable具体子类的对象实例
     * @throws Exception
     */
    public abstract GwPersistable getObject(ResultSet rs) throws Exception;

    /**
     * 所有自定义表主键所对象的值<br>
     * <i><b>注意：</b>目前只支持java.lang.String及java.lang.Long类型的主键值</i>
     *
     * @return 对象主键键值
     */
    public abstract Object getKeyId();

    /**
     * 更新数据对象时需要更新的数据库字段名与字段值的映射<br>
     * <b>示例：</b> <code><pre>
     *    public Map getUpdateMap() {
     *       Timestamp now = new Timestamp(new Date().getTime());
     *
     *       HashMap ret = new HashMap(6);
     *       ret.put(PARENT_NUMBER, parentNumber);
     *       ret.put(NUMBER, number);
     *       ret.put(NAME, name);
     *       ret.put(PART_TYPE, partType);
     *       ret.put(CONTAINER_REF, containerRef);
     *       ret.put(UPDATE_STAMP, now);
     *
     *       return ret;
     *    }
     * </pre></code>
     *
     * @return 数据库字段名与字段值的映射
     */
    public abstract Map<?, ?> getUpdateMap();

    /**
     * 创建数据对象时需要更新的数据库字段名与字段值的映射<br>
     * <b>示例：</b> <code><pre>
     *    public Map getCreateMap() {
     *       Timestamp now = new Timestamp(new Date().getTime());
     *
     *       HashMap ret = new HashMap(7);
     *       ret.put(PARENT_NUMBER, parentNumber);
     *       ret.put(NUMBER, number);
     *       ret.put(NAME, name);
     *       ret.put(PART_TYPE, partType);
     *       ret.put(CONTAINER_REF, containerRef);
     *       ret.put(CREATE_STAMP, now);
     *       ret.put(UPDATE_STAMP, now);
     *
     *       return ret;
     *    }
     * </pre></code>
     *
     * @return 数据库字段名与字段值的映射
     */
    public abstract Map<?, ?> getCreateMap();
}
