package com.glaway.mpm.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.glaway.mpm.model.ShopType;
import com.glaway.mpm.model.Skill;
import com.glaway.mpm.model.WorkShop;
import com.glaway.mpm.resource.ResourceCache;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class ResourceUtil {
	private static List<ShopType> filterShopTypeFromWorkShop(String shopId) {
		if (shopId == null) {
			return null;
		}
		System.out.println("-----ResourceUtil.filterShopTypeFromWorkShop()--ResourceCache.allShopTypes:"+ResourceCache.allShopTypes);
		if(ResourceCache.allShopTypes == null || ResourceCache.allShopTypes.isEmpty()) {
			ResourceIntf.getInitData();
		}
		Map<WorkShop, List<ShopType>> allShopTypes = ResourceCache.allShopTypes;
		if (allShopTypes != null) {
			Set<Entry<WorkShop, List<ShopType>>> set = allShopTypes.entrySet();
			for (Entry<WorkShop, List<ShopType>> entry : set) {
				WorkShop workShop = entry.getKey();
				if (workShop.getNumber().equals(shopId)) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	public static Map<String, String> generateShopTypeMap(String shopId) {
		if(shopId == null) {
			return null;
		}
		List<ShopType> shopTypes = filterShopTypeFromWorkShop(shopId);
		Map<String, String> shopType = null;
		if (shopTypes == null) {
			return shopType;
		}
		shopType = new HashMap<String, String>();
		for (ShopType temp : shopTypes) {
			shopType.put(temp.getNumber(), temp.getName());
		}
		return shopType;
	}

	public static  Map<String,String> generate812ShopTypeMap(String shopId){
		System.out.println("----ResourceUtil.generate812ShopTypeMap()--ResourceCache.workShops:"+ResourceCache.workShops);
		if(ResourceCache.workShops.isEmpty()) {
			ResourceIntf.getInitData();
		}
		List<WorkShop> shopList = ResourceCache.workShops;
		Map<String,String> shopTypeMap = new HashMap<String,String>();
		WorkShop shop;
		if(shopList!=null)
		for(int i=0;i<shopList.size();i++){
			shop = shopList.get(i);
			if(shop.getNumber().equals(shopId)){
				List<Skill> skillList = shop.getSkillList();
				if(skillList!=null){
					for(Skill skill: skillList){
						shopTypeMap.put(skill.getNumber(), skill.getName());
					}
				}
			}

		}
		return shopTypeMap;
	}
}
