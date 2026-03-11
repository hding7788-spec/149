package com.glaway.mpm.pbombuilder.bom;

import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.config.LatestConfigSpec;
import wt.vc.struct.StructHelper;

import com.glaway.mpm.pbombuilder.util.CmSearchHelper;
import com.ptc.core.meta.common.TypeIdentifier;

public class PBOMExport {

	public static void main(String[] args) throws Exception {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		
		
//		System.out.println("login success.");
		
		
//		try {
//			Vector v = rms.getAllInfo();
//			
//			for (int i = 0; i < v.size(); i++) {
//				MethodServerInfo msi = (MethodServerInfo)v.get(i);
//				System.out.println(msi.totalMemory);
//			}
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		
//		WTPart part = new WTPart();
		
//		WTPart c = getPartByName("wt.part.WTPart:896755");
		
//		WTPart pa = getPartByName("jx955-fs.prt");
//		System.out.println(pa.getName());
		
//		WTPart part = getLatestPartByPartNumber("AL2_907_1460");
		WTPart part = (WTPart) CmSearchHelper.search(WTPart.class, 435107);
		System.out.println(part.getName()+"&&&&&&&&&&&&&"+part.getVersionInfo().toString());
		List<WTPart> list = WTPartUtil.getChildPart(part);
		for (WTPart wtPart : list) {
			String item=wtPart.getNumber()+"("+wtPart.getName()+")"+wtPart.getVersionIdentifier().getValue();
			System.out.println(item);
		}
//		System.out.println("===============================================");
//		List<EPMDocument> epms = WTPartUtil.getEPMDocumentByPartNumber("GB_T823-1988_M2_5X5");
//		for (EPMDocument epmDocument : epms) {
//			System.out.println(epmDocument.getName());
//			
//		}
		
//		Vector v = CmSearchSvrHelper.searchPart(ti, queryLimit, number, name, modifierFullName, modifyTimeFrom, null);
		
//		WTPart p =  (WTPart)VersionControlHelper.service
//				.allVersionsOf(pa.getMaster()).nextElement();
		
//		String xml = naviagePart(p.getPersistInfo().getObjectIdentifier().toString());
//		System.out.println(xml);
		
		
	}
	private static void buildStructureStopOn(TreeNode parentNode, WTPart parent, ConfigSpec configSpec,
		      TypeIdentifier stopType, TypeIdentifier dciType) {
		      // 1. 如果是dciType则不展开，直接返回
		      // 2. parentNode不可以是stopType
		      

//		      try {
//		         QueryResult qr = WTPartHelper.service.getUsesWTParts(parent, configSpec);
//		         while (qr.hasMoreElements()) {
//		            Persistable[] objs = (Persistable[]) qr.nextElement();
//		            if (objs[1] instanceof WTPart) {
//		               WTPart part = (WTPart) objs[1];
//
//		               JTree tree = new JTree();
//		               
//		               TreeNode partNode = new TreeNode();
//
//		               parentNode.add(partNode);
//		               buildStructureStopOn(partNode, part, configSpec, stopType, dciType);
//		            }
//		         }
//		      } catch (WTException e) {
//		      }
//
//		      return parentNode;
		   }
	
	
	
	/*
	 * 根据零件编号获取最新版本最新版序的零件
	 */
	public static WTPart getLatestPartByPartNumber(String partNumber)
			throws WTException {
		WTPart part = null;
		int[] index = new int[] { 0 };

		QuerySpec qSpec = new QuerySpec(WTPart.class);
		qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER,
				SearchCondition.EQUAL, partNumber), index);
		QueryResult qResult = PersistenceHelper.manager
				.find((StatementSpec) qSpec);
		qResult = new LatestConfigSpec().process(qResult);
		while (qResult.hasMoreElements()) {
			part = (WTPart) qResult.nextElement();
		}

		return part;
	}
	
	/*
	 * 根据Master获取最新版本最新版序的零件
	 */
	public static WTPart getLatestPartByMaster(WTPartMaster partMaster)
			throws WTException {
		WTPart part = null;

		if (partMaster != null) {
			QueryResult qr = VersionControlHelper.service
					.allVersionsOf(partMaster);
			if (qr.hasMoreElements()) {
				part = (WTPart) qr.nextElement();
			}
		}

		return part;
	}
	
	public static String naviagePart(String oid) {
		String xml = "";
		try {
//			WTPart p = (WTPart)ReferenceFactory.getObjectbyOid(oid);
			
//			System.out.println(p.getName());
			
			QueryResult rlt = StructHelper.service.navigateUses(new WTPart());//p);
			while (rlt.hasMoreElements()) {
				WTPartMaster master = (WTPartMaster)rlt.nextElement();
				
				WTPart part =  (WTPart)VersionControlHelper.service
						.allVersionsOf(master).nextElement();
//				System.out.println(part.getPersistInfo().getObjectIdentifier());
				
				String str = naviagePart(part.getPersistInfo().getObjectIdentifier().toString());
		
				xml += "<Part>" + "<PartInfo><Name>"+ part.getName() +"</Name></PartInfo>";
				if(str != null && str != "")
				{
					xml += "<Parts>" + str + "</Parts>\n";
				}
				xml += "</Part>\n";
//				System.out.println(xml);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return xml;
	}
	
	public static WTPart getPartByName(String pName){
		WTPart part = null;
		QueryResult qr = null;
		QuerySpec qs = null;
		try{
			qs = new QuerySpec(WTPart.class);
			qs.appendWhere(new SearchCondition(WTPart.class,WTPart.NAME,"=",pName));
			qr = PersistenceHelper.manager.find(qs);
			
			if(qr.hasMoreElements()){
				
				part = (WTPart)qr.nextElement();
				
				//System.out.println(part.getPersistInfo().getObjectIdentifier());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return part;
	}
	
//	static final ConfigSpec
}
