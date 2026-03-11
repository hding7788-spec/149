package com.glaway.mpm.parameter.constants;

public class ParameterConstants {

	/** 生成参数表类型的数据库表时需要默认创建的列  */
	public static String[][] DEFAULTCOLUMNS = {
			{"GWKEY", "GWKEY"},
			{"TECHNICSNUMBER", "TECHNICSNUMBER"},
			{"OBJTYPE", "OBJTYPE"},
			{"OBJNUMBER", "OBJNUMBER"},
			{"PARAMETERTABLETYPEID", "参数表类型ID"},
			{"SEQUENCE", "SEQUENCE"},
			{"BSOID", "BSOID"},
			{"VERSION", "VERSION"}};

	/** 生成参数表类型的数据库表时需要默认创建的列  */
	public static String[][] DEFAULTMESCOLUMNS = {
			{"GWKEY", "GWKEY"},
			{"TECHNICSNUMBER", "TECHNICSNUMBER"},
			{"PRODUCTNUMBER", "PRODUCTNUMBER"},
			{"ISZF","ISZF"},
			{"LUKAHAO", "LUKAHAO"},
			{"GXPK", "GXPK"},
			{"OBJTYPE", "OBJTYPE"},
			{"OBJNUMBER", "OBJNUMBER"},
			{"PARAMETERTABLETYPEID", "参数表类型ID"},
			{"SEQUENCE", "SEQUENCE"},
			{"BSOID", "BSOID"},
			{"VERSION", "VERSION"}};

	/** 参数表默认列在JTable中不需要显示的列  */
	public static final String[] DEFAULTNOTSHOWCOLUMNS = {"GWKEY", "TECHNICSNUMBER", "OBJTYPE", "OBJNUMBER", "PARAMETERTABLETYPEID","SEQUENCE","BSOID","VERSION"};
	/** 参数表默认列在JTable中不需要编辑的列  */
	public static final String[] DEFAULTNOTEDITABLECOLUMNS = {};


	//管理类型
	/** 参数类型管理 */
	public static final int PARAMETER_TYPE = 0;
	/** 参数表格类型管理 */
	public static final int PARAMETER_TABLE = 1;
	/** 模板参数表格管理 */
	public static final int PARAMETER_TEMPLATETABLE = 2;

	//属性字段
	//参数表属性字段
	public static final String TABLE_COLUMN_NAME = "NAME";
	public static final String TABLE_COLUMN_CHINANAME = "CHINANAME";
	public static final String TABLE_COLUMN_TECHNICSTYPEID = "TECHNICSTYPEID";
	public static final String TABLE_COLUMN_OBJECTTYPE = "OBJECTTYPE";
	public static final String TABLE_COLUMN_ISCOMMON = "ISCOMMON";

	//数据库字段显示名称
	public static final String TABLE_COLUMN_CHINA_ORDERNO = "顺序号";
	public static final String TABLE_COLUMN_CHINA_PARAMTYPENAME = "参数类型名";

	//表格列数据类型：
	public static final String[] COLUMN_DATATYPE = {"字符串","BLOB","布尔型","图片"};
//	public static final String[] COLUMN_DATATYPE = {"字符串","小数","整数","BLOB","布尔型","图片"};
	public static final String TABLE_COLUMN_DATATYPE_CHAR = "字符串";
	public static final String TABLE_COLUMN_DATATYPE_DECIMAL = "小数";
	public static final String TABLE_COLUMN_DATATYPE_INTEGER = "整数";
	public static final String TABLE_COLUMN_DATATYPE_BLOB = "BLOB";
	public static final String TABLE_COLUMN_DATATYPE_BOOLEAN = "布尔型";
	public static final String TABLE_COLUMN_DATATYPE_PICTURE = "图片";

	/** 保存带特殊符号信息的参数值时用于替换特殊符号中的路径，读取时再替换回来。 */
	public static final String REPLACE_STR = "@@@";
	/** 保存带特殊符号信息的参数值时用于替换特殊符号中的单引号，读取时再替换回来。 */
	public static final String REPLACE_SINGLEQUOTE = "@#@";

	public static boolean isShow(String columnName) {
		boolean flag = true;
		for (String name : DEFAULTNOTSHOWCOLUMNS) {
			if (name.equals(columnName)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	public static boolean isEditable(String columnName) {
		boolean flag = true;
		for (String name : DEFAULTNOTEDITABLECOLUMNS) {
			if (name.equals(columnName)) {
				flag = false;
				break;
			}
		}
		return flag;
	}

}
