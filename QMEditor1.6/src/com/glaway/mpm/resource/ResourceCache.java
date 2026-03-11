package com.glaway.mpm.resource;

import com.glaway.mpm.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ylshao
 * @Description: Switch Data Area
 * @date 2012-11-9
 *
 */
public class ResourceCache {
	public static CsType csType;
	public static EpType epType;
	public static FkType fkType;
	public static ToolType toolType;
	public static ToolType measuresType;
	public static KtType ktType;
	public static MstType mstType;
	public static MtType mtType;

	public static List<WorkShop> workShops;
	public static List<ShopType> shopTypes;
	public static Map<WorkShop, List<ShopType>> allShopTypes;
	public static HashMap<String, String> processStepNames;
	public static List<MaterialCal> materialCals;

	public static PdNameType pdNameType;

	public static DashboardType dashboardType;
	public static UnSDashboardType unsdashboardType;

	public static List<String> depts;
}
