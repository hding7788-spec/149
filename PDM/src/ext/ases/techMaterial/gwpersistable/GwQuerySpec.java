package ext.ases.techMaterial.gwpersistable;
import java.io.Serializable;

import org.apache.log4j.Logger;
/**
 * 自定义表的搜索规格定义
 *
 * @author 龙秀川
 */
public class GwQuerySpec implements Serializable {
    private static final long serialVersionUID = 8406168680067944943L;
    private static final Logger LOGGER = Logger.getLogger(GwQuerySpec.class.getName());
    public static final String EQUAL = "=";

    public static final String LESS_THAN = "<";

    public static final String GREATER_THAN = ">";

    public static final String NOT_EQUAL = "<>";

    public static final String LIKE = " LIKE ";
    
    public static final String IS_NULL = " IS NULL ";
    
    public static final String IS_NOT_NULL = " IS NOT NULL ";

    public static final String IN = " IN ";
    
    public static final String NOT_IN = " NOT IN ";
    
    private StringBuffer querySpec;

    private StringBuffer whereClause;

    private StringBuffer orderByClause;

    private Class<?> selectClass;

    /**
     * 构造函数
     *
     * @param selectClass
     *            必须为CmPersistable的子类
     * @throws Exception
     */
    public GwQuerySpec(Class<?> selectClass) throws Exception {
        if (!GwPersistable.class.isAssignableFrom(selectClass)) {
        	LOGGER.error("指定的参数必须为'" + GwPersistable.class.getName() + "'的子类");
        }
        this.querySpec = new StringBuffer();
        this.whereClause = null;
        this.selectClass = selectClass;
    }

    /**
     * 在WHERE子句后增加<code><b> AND </b></code>连接关键字
     *
     */
    public void appendAnd() {
        if (this.whereClause != null && this.whereClause.length() > 0)
            this.whereClause.append(" AND ");
    }

    /**
     * 在WHERE子句后增加<code><b> OR </b></code>连接关键字
     *
     */
    public void appendOr() {
        if (this.whereClause != null && this.whereClause.length() > 0)
            this.whereClause.append(" OR ");
    }

    /**
     * 在WHERE子句后增加<code><b> NOT </b></code>连接关键字
     *
     */
    public void appendNot() {
        if (this.whereClause != null && this.whereClause.length() > 0)
            this.whereClause.append(" NOT ");
    }

    /**
     * 在WHERE子句后增加左括号'<code><b> ( </b></code>'
     *
     */
    public void appendOpenParen() {
        if (this.whereClause != null && this.whereClause.length() > 0)
            this.whereClause.append('(');
    }

    /**
     * 在WHERE子句后增加右括号'<code><b> ) </b></code>'
     *
     */
    public void appendCloseParen() {
        if (this.whereClause != null && this.whereClause.length() > 0)
            this.whereClause.append(')');
    }

    /**
     * 构造WHERE子句<br>
     *
     * @param field
     *            字段名
     * @param logic
     *            操作符，如<code>=、<>、LIKE</code>等
     * @param value
     *            字段值，目前只支持<code>java.lang.String</code>及<code>java.lang.Long</code>类型
     * @exception 当value不是
     *                <code>java.lang.String</code>及<code>java.lang.Long</code>类型时，抛出异常
     */
    public void appendWhere(String field, String logic, Object value) throws Exception {
        if (value instanceof String){
        	appendWhere(field, logic, (String) value);
        } else if (value instanceof Long) {
        	appendWhere(field, logic, ((Long) value).longValue());
        } else {
        	LOGGER.error("不支持的数据类型value.class=" + (value == null ? "(null)" : value.getClass().getName()));
        }
    }

    /**
     * 构造WHERE子句<br>
     *
     * @param field
     *            字段名
     * @param logic
     *            操作符，如<code>=、<>、LIKE</code>等
     * @param value
     *            字段值
     */
    public void appendWhere(String field, String logic, String value) {
        if (this.whereClause == null)
            this.whereClause = new StringBuffer();
        if(logic.equals(IS_NULL) || logic.equals(IS_NOT_NULL)){
        	this.whereClause.append("(").append(field).append(logic).append(")");
        }
        else{
            this.whereClause.append("(").append(field).append(logic).append("'").append(value).append("')");
        }

    }
    
    public void appendWhere(String field, String logic, String value,boolean ignoreCase) {
        if (this.whereClause == null)
            this.whereClause = new StringBuffer();
        if(logic.equals(IS_NULL) || logic.equals(IS_NOT_NULL)){
        	this.whereClause.append("(").append(field).append(logic).append(")");
        }
        else if(ignoreCase){
            this.whereClause.append("(").append("LOWER(").append(field).append(")").append(logic).append("LOWER('").append(value).append("'))");
        }
        else{
        	this.whereClause.append("(").append(field).append(logic).append("'").append(value).append("')");
        }

    }
    /**
     * 构造WHERE子句<br>
     *
     * @param field
     *            字段名
     * @param logic
     *            操作符，如<code>=、<>、LIKE</code>等
     * @param value
     *            字段值
     */
    public void appendWhere(String field, String logic, long value) {
        if (this.whereClause == null)
            this.whereClause = new StringBuffer();

        this.whereClause.append("(").append(field).append(logic).append(value).append(")");
    }

    /**
     * 构造WHERE子句<br>
     *
     * @param field
     *            字段名
     * @param logic
     *            操作符，如<code>=、<>、LIKE</code>等
     * @param value
     *            字段值
     */
    public void appendWhere(String field, String logic, boolean value) {
        appendWhere(field, logic, value ? 1L : 0L);
    }

    /**
     * 构造ORDER BY子句<br>
     *
     * @param field
     *            字段名
     * @param desc
     *            true表示降序，false表示升序
     */
    public void appendOrderBy(String field, boolean desc) {
        if (this.orderByClause == null)
            this.orderByClause = new StringBuffer();

        if (this.orderByClause.length() > 0)
            this.orderByClause.append(", ");
        this.orderByClause.append(field).append(' ').append(desc ? "DESC" : "ASC");
    }

    /**
     * 返回当前查询的CmPersistence子类的类对象
     *
     * @return 当前查询的CmPersistence子类的类对象
     */
    public Class<?> getSelectClass() {
        return selectClass;
    }

    /**
     * 返回当前查询的sql语句
     *
     * @return 当前查询的sql语句
     */
    @Override
	public String toString() {
    	
    	if (!this.querySpec.toString().startsWith("SELECT")) {
	    		this.querySpec.append("SELECT * FROM ").append(GwPersistenceHelper.getTableName(selectClass));
	        if (this.whereClause != null)
	            this.querySpec.append(" WHERE ").append(this.whereClause);
	        if (this.orderByClause != null)
	            this.querySpec.append(" ORDER BY ").append(this.orderByClause);
    	}
        
    	return this.querySpec.toString();
    }
    
    /**
     * 工序名称查询
     *
     * @return 当前查询的sql语句
     */
    public String getSeqNamesql(String index ,String type) {
    	if (!this.querySpec.toString().startsWith("SELECT")) {
	    	this.querySpec.append("SELECT t.* FROM MPMStandardSequence t,MPMSubTerminologyType f WHERE t.parentkey = f.glkeyid AND f.termname IN (");
	    	this.querySpec.append(type);
	    	this.querySpec.append(") AND ((t.TERMINDEX LIKE '"+index+"'");
	    	this.querySpec.append(") OR (t.TERMNAME LIKE '"+index+"'");
	    	this.querySpec.append("))");
    	}
    	return this.querySpec.toString();
    }
}
