package ext.sast.common.fc;

import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;

import java.io.Serializable;


/**
 * 自定义表的搜索规格定义
 *
 * @author Dennis Huang
 */
public class CmQuerySpec implements Serializable {
    private static final long serialVersionUID = 8406168680067944943L;
 

    public static final String EQUAL = "=";

    public static final String NOT_EQUAL = "!=";

    public static final String LESS_THAN = "<";

    public static final String GREATER_THAN = ">";
    
    public static final String NO_LESS_THAN = ">=";
    
    public static final String NOT_MORE = "<=";

    public static final String LIKE = " LIKE ";
    
    public static final String IS_NULL = " IS NULL ";
    
    public static final String NOT_NULL = " IS NOT NULL ";

    private StringBuffer querySpec;

    private StringBuffer whereClause;

    private StringBuffer orderByClause;
    
    private StringBuffer groupBy;
    
    private StringBuffer concat;
    

    private Class selectClass;

    /**
     * 构造函数
     *
     * @param selectClass
     *            必须为CmPersistable的子类
     * @throws Exception
     */
    public CmQuerySpec(Class selectClass) throws Exception {
        if (!CmPersistable.class.isAssignableFrom(selectClass))
            throw new Exception("指定的参数必须为'" + CmPersistable.class.getName() + "'的子类");
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
        if (value instanceof String)
            appendWhere(field, logic, (String) value);
        else if (value instanceof Long)
            appendWhere(field, logic, ((Long) value).longValue());
        else
            throw new Exception("不支持的数据类型value.class=" + (value == null ? "(null)" : value.getClass().getName()));
    }

    /**
     * 构造in()子句<br>
     *
     * @param field
     *            字段名
     *
     * @param value
     *            字段值
     */
    public void appendIn(String field, String value) {
        if (this.whereClause == null)
            this.whereClause = new StringBuffer();

        this.whereClause.append(field).append(" in(").append(value).append(")");
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
        if(logic.equals(this.LIKE)){
        	this.whereClause.append("(").append(field).append(logic).append("'%").append(value).append("%')");
        }else{
          	this.whereClause.append("(").append(field).append(logic).append("'").append(value).append("')");
        }
    }
   
    /**
     * 构造DateWHERE子句<br>
     *
     * @param field
     *            字段名
     * @param logic
     *            操作符，如<code>=、<>、LIKE</code>等
     * @param value
     *            字段值
     */
    public void appendDateWhere(String field, String logic, String value) {
        if (this.whereClause == null)
            this.whereClause = new StringBuffer();
        this.whereClause.append("("+field).append(logic).append("to_date('"+value+"','YYYY-MM-DD')").append(")");
    }
    /**
     * 构造MonthWHERE子句<br>
     *
     * @param field
     *            字段名
     * @param value
     *            字段值
     */
    public void appendMonthWhere(String field, String value) {
        if (this.whereClause == null)
            this.whereClause = new StringBuffer();
        this.whereClause.append("(concat("+field+") <= '"+value+"' and concat("+field+") > TO_CHAR(add_months(to_date('"+value+"','yyyymm'),-3),'yyyymm'))" );
    }
    /**
     * 构造concat()函数查询时间小于当前时间的数据<br>
     *
     * @param field
     *            字段名
     * @param logic
     *            操作符，如<code>=、<>、LIKE</code>等
     * @param value
     *            字段值
     */
    public void appendConcat(String field, String logic, String value) {
    	 if (this.concat == null){
             this.concat = new StringBuffer();
    	 }
       	 this.concat.append("(concat(").append(field).append(")||'01000000')");
       	 this.appendDateWhere(this.concat.toString(),logic,value+"01000000");
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
    
    public void appendWhere(String field, String logic){
    	if (this.whereClause == null)
            this.whereClause = new StringBuffer();

        this.whereClause.append("(").append(field).append(logic).append(")");
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
     * 构造GROUP BY子句<br>
     *
     * @param field
     *            字段名
     */
    public void appendGroupBy(String field) {
        if (this.groupBy== null){
            this.groupBy = new StringBuffer();
        }
        this.groupBy.append(field);
    }

    /**
     * 返回当前查询的CmPersistence子类的类对象
     *
     * @return 当前查询的CmPersistence子类的类对象
     */
    public Class getSelectClass() {
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
            this.querySpec.append("SELECT * FROM ").append(CmPersistenceHelper.getTableName(selectClass));
            if (this.whereClause != null)
                this.querySpec.append(" WHERE ").append(this.whereClause);
            if (this.orderByClause != null)
                this.querySpec.append(" ORDER BY ").append(this.orderByClause);
        }

        return this.querySpec.toString();
    }
}

