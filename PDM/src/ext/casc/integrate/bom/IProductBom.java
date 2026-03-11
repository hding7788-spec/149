package ext.casc.integrate.bom;


public interface IProductBom {

	/*获取图号及BOM结构的封装信息
	 * number:产品图号
	 * versionType:版本类型
	 * version:版本值
	 * expansionLevel:BOM结构展开层级
	 * relatedType:关联零部件类型
	 * bomType:bom类别-Design/Manufacturing
	 * fileTypeB:工艺规程类型-正式(FORMAL)/临时(TEMP)/默认全部(ALL)
	 * fileTypeC:工艺规程类型-主(PRIMARY)/辅(ASSIST)/默认全部(ALL)
	 * */
	public String getBomInfo(String number,String versionType,String version,int expansionLevel,String relatedType,String bomType,String fileTypeA,String fileTypeB,String fileTypeC,String productName,int ismark,String guid,String type) throws Exception;

	/*获取BOM结构信息
	 * number:产品图号
	 * versionType:版本类型
	 * version:版本值
	 * expansionLevel:BOM结构展开层级
	 * relatedType:关联零部件类型
	 * bomType:bom类别-Design/Manufacturing
	 * fileTypeB:工艺规程类型-正式(FORMAL)/临时(TEMP)/默认全部(ALL)
	 * fileTypeC:工艺规程类型-主(PRIMARY)/辅(ASSIST)/默认全部(ALL)
	 * */
	public String getBomStructure(String number,String versionType,String version,int expansionLevel,String relatedType,String bomType,String fileTypeB,String fileTypeC,String guid,String type,boolean is65) throws Exception;

}
