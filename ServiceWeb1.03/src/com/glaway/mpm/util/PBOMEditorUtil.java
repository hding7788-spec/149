package com.glaway.mpm.util;

import java.util.ArrayList;
import java.util.List;

import wt.part.WTPart;
import wt.util.WTException;

public class PBOMEditorUtil {
	public static List<String> getUp_Parts(String number,String source) throws WTException{
		List<String> list = new ArrayList<String>();
		
		WTPart part = null;
		
		part = WTPartUtil.getLatestPartByNumberAndView(number, source);
		
		if(part!=null){
			List<WTPart> tempList = new ArrayList<WTPart>();
			WTPartUtil.getAllUp_Part(part, list, tempList);
		}
		return list;
	}
}
