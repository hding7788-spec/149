package com.glaway.speciaword.common;

/***
 * 
 * @author meixin
 * 
 */
public class SWConstant {

	/** 带圈字符选项 */
	public static final String[] CIRCLESYMBOL_COMBOBOX_VALUE = new String[] { "检", "订货方检",
			"持证操作 CERTIFICATION OPERATION", "关键工序", "控制点", "！" };

	/** 表面粗糙度选项 **/
	public static final String[] ROUGHSYMBOL_COMBOBOX_VALUE = new String[] { "", "1600", "800", "400", "200", "100",
			"50", "25", "12.5", "6.3", "3.2", "1.6", "0.8", "0.4", "0.2", "0.1", "0.05", "0.025" };

	/** 尺寸前缀选项 **/
	public static final String[] BASICTOLERANCESYMBOL_SIZE_PRE = new String[] { "", "Φ", "φ", "δ", "σ" };

	/** 尺寸后缀选线 **/
	public static final String[] BASICTOLERANCESYMBOL_SIZE_POST = new String[] { "", "°", "℃", "℉", "′", "″", "％", "‰",
			"㎜", "㎝", "㎞", "㎡", "㎎", "㎏", "㏄", "㏎", "㏕" };

	/** 输入形式选项 */
	public static final String[] BASICTOLERANCESYMBOL_INPUT_FORM = new String[] { "代号", "偏差", "配合" };

	/** 输出形式选项 **/
	public static final String[] BASICTOLERANCESYMBOL_OUTPUT_FORM = new String[] { "代号", "偏差", "(偏差)", "代号偏差", "代号(偏差)" };

	/** 相关原则 **/
	public static final String[] FormToleranceSymbol_PRINCIPLE = new String[] { "", "(P)", "(M)", "(E)", "(L)", "(F)",
			"(S)" };

	/** 形式设定选项 **/
	public static final String[] FormToleranceSymbol_FORM = new String[] { "", "(+)", "(-)", "(＜)", "(＞)" };

	/** 基准选项 **/
	public static final String[] FormToleranceSymbol_BASIC = new String[] { "", "(M)", "(E)", "(L)" };
}
