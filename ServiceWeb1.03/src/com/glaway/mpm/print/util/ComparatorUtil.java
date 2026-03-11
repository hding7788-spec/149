package com.glaway.mpm.print.util;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import wt.org.WTGroup;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;

import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMSkill;

public class ComparatorUtil {

	@SuppressWarnings("unchecked")
	public static void compareWTObjectName(List list){
		   Collections.sort(list, new Comparator() {
		         @Override
				public int compare(Object obj1, Object obj2){
		        	String s1 = "";
			        String s2 = "";
		        	if(obj1 instanceof PDMLinkProduct){
		        		s1 = ((PDMLinkProduct)obj1).getName();
		        	}else if(obj1 instanceof WTUser){
		        		s1 = ((WTUser)obj1).getName();
		        	}else if(obj1 instanceof MPMSkill){
		        		s1 = ((MPMSkill)obj1).getName();
		        	}else if(obj1 instanceof MPMPlant){
		        		s1 = ((MPMPlant)obj1).getName();
		        	}else if(obj1 instanceof MPMOperation){
		        		s1 = ((MPMOperation) obj1).getName();
		        	}else if(obj1 instanceof String){
		        		s1 = (String) obj1;
		        	}else if(obj1 instanceof WTGroup){
		        		s1 = ((WTGroup) obj1).getName();
		        	}

		        	if(obj2 instanceof PDMLinkProduct){
		        		s2 = ((PDMLinkProduct)obj2).getName();
		        	}else if(obj2 instanceof WTUser){
		        		s2 = ((WTUser)obj2).getName();
		        	}else if(obj2 instanceof MPMSkill){
		        		s2 = ((MPMSkill)obj2).getName();
		        	}else if(obj2 instanceof MPMPlant){
		        		s2 = ((MPMPlant)obj2).getName();
		        	}else if(obj1 instanceof MPMOperation){
		        		s2 = ((MPMOperation) obj2).getName();
		        	}else if(obj2 instanceof String){
		        		s2 = (String)obj2;
		        	}else if(obj2 instanceof WTGroup){
		        		s2 = ((WTGroup) obj2).getName();
		        	}

		            Comparator cmp = Collator.getInstance(Locale.CHINA);
		            return cmp.compare(s1, s2);
		         }
		      });
	   }
}
