package com.glaway.mpm.pbombuilder.data;

import java.util.Comparator;

import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;

/**
 * <br>Created on 2013-3-18
 * @author chenyunlong
 */
public class CmTreeNodeComparator implements Comparator<CmTreeNode> {
   public static final int LESS = -1;
   public static final int EQUAL = 0;
   public static final int GREATER = 1;
   public static final int UNDECIDABLE = 999;
 
   public int compare(CmTreeNode o1, CmTreeNode o2) {
      if (CmCommonNodeUtil.isEqualOfOccpath(o1, o2) 
//    		  && checkNodeHasPositionId(o1)
    		  ) {
         return EQUAL;
      }
      return UNDECIDABLE;
   }
//   /**
//    * 判断零件的是否有位号
//    * 
//    * 没有三维图的零件的位号为0或者负数
//    * @author chenyunlong
//    * @date  2013-7-24
//    * @param node
//    * @return
//    *
//    */
//   public static boolean checkNodeHasPositionId(CmTreeNode node){
//	   boolean flag = true;
//	   if(null == node.getOccpath()){
//		   return false;
//	   }
//	   int index = node.getOccpath().lastIndexOf("+");
//	   if(index !=-1 && (
//			   "+-".equals(node.getOccpath().substring(index,index+2))
//			   || "+0".equals(node.getOccpath().substring(index,index+2)))){
//		   flag = false;
//	   }
//	   return flag;
//   }
   
   /**
    * 判断零件的是否有位号
    * 
    * 没有三维图的零件的位号为0或者负数
    * @author chenyunlong
    * @date  2013-7-24
    * @param node
    * @return
    *
    */
   public static boolean checkNodeHasPositionId(CmTreeNode node){
	   boolean flag = true;
	   if(null == node.getOccpath()){
		   return false;
	   }
	   int index = node.getOccpath().lastIndexOf("+");
	   if(index !=-1 ){
		   String occpath = node.getOccpath().substring(index+1);
		   if(occpath.indexOf("-")>0){
			   flag = false;
		   }
	   }
	   return flag;
   }
}