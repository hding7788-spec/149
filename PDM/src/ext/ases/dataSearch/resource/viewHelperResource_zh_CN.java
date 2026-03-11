package ext.ases.dataSearch.resource;

import java.util.ListResourceBundle;

public class viewHelperResource_zh_CN extends ListResourceBundle {
	public static final String DATATRANSFERTITLE = "DATATRANSFERTITLE";
	public static final String NAME = "NAME";
	public static final String NUMBER = "NUMBER";
	public static final String PACKAGE_NUM = "PACKAGE_NUM";
	public static final String SEND_DATE = "SEND_DATE";
	public static final String SEND_TYPE = "SEND_TYPE";
	public static final String SENDINDUEFORM = "SENDINDUEFORM";
	public static final String CRAFTSIGN = "CRAFTSIGN";
	public static final String PRODUCT_CODE = "PRODUCT_CODE";
	public static final String FROM = "FROM";
	public static final String TO = "TO";
	public static final String OBJ_VERSION = "OBJ_VERSION";
	public static final String SEARCH = "SEARCH";
	static final Object[][] contents = { { "DATATRANSFERTITLE", "搜索条件" },
			{ "NAME", "名称" }, { "NUMBER", "编号" }, { "PACKAGE_NUM", "单号" },
			{ "SEND_DATE", "发送时间" }, { "SEND_TYPE", "发放类型" },
			{ "SENDINDUEFORM", "正式发放" }, { "CRAFTSIGN", "工艺会签" },
			{ "PRODUCT_CODE", "产品代号" }, { "FROM", "自" }, { "TO", "至" },
			{ "OBJ_VERSION", " 版本" }, { "SEARCH", "搜索" },
			{ "SEARCH_RESULT", "搜索结果" },
			{ "dataTransfer.searchBtn.description", "搜索" },
			{ "dataTransfer.searchBtn.title", "搜索" },
			{ "dataTransfer.searchBtn.tooltip", "搜索" },
			{ "navigation.datasendrecord.activetooltip", "活动主要选项卡: 数据发放管理" },
			{ "navigation.datasendrecord.description", "数据发放管理" },
			{ "navigation.datasendrecord.tooltip", "主要选项卡: 数据发放管理" } };

	public Object[][] getContents() {
		return contents;
	}
}