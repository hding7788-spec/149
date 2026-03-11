package com.glaway.mpm.tool;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.pbom.db.ErpDao;
import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;

public class ImportMaterials implements RemoteAccess {
	private static final String CLASSNAME = ImportMaterials.class.getName();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RemoteMethodServer methodServer = RemoteMethodServer.getDefault();
		methodServer.setUserName("wcadmin");
		methodServer.setPassword("wcadmin");
		try {
			methodServer.invoke("importMaterials", CLASSNAME, null, new Class[] {}, new Object[] {});
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static void importMaterials()throws WTException, WTPropertyVetoException, RemoteException{
		String objectType = "com.ptc.windchill.mpml.resource.MPMProcessMaterial";
		WTLibrary library = getLibraryByName("工艺资源库");
		String folder = "";
		MPMProcessMaterial material = null;
		String number = "";
		String name = "";
		String dbtype = "02";
		String wzlb = null;
		List<Wzk> dataList = null;
		Wzk wzk = null;
		for(int i=5001;i<=5002;i++){
			wzlb = ""+i	;
			dataList = ErpDao.queryWzk(dbtype,wzlb);
			if(dataList!=null)
			for(int j=0;j<dataList.size();j++){
				wzk = dataList.get(j);
				number = wzk.getInvcode();
				name = wzk.getInvname();
				folder = ErpDao.genFolderByWzkClass(dbtype, wzk.getInvclasscode());
				folder = folder.replace("Default", "Default/工艺辅料");
				System.out.println("number="+number + " name="+name+ " folder="+ folder);
				material = MPMResourceUtil.createProcessMaterial(number, name, library, folder, objectType,"");

				setMyIBAStringValue(material, AttributeConstants.MATERIAL_CLPH, wzk.getInvtype());
				setMyIBAStringValue(material, AttributeConstants.MATERIAL_CLGG, wzk.getInvspec());
				setMyIBAStringValue(material, AttributeConstants.MATERIAL_CLBZ, wzk.getDef2());
				setMyIBAStringValue(material, AttributeConstants.MATERIAL_JLDW, wzk.getMeasname());
//				setMyIBAStringValue(material, "Description", "");
			}
		}
	}

	private static void setMyIBAStringValue(WTObject object, String attributeName, String attributeValue)
			throws WTException {
		if (attributeValue == null || "".equals(attributeValue.trim()) || "null".equals(attributeValue.trim())) {
			return;
		} else {
			IBAHelper.setIBAStringValue(object, attributeName, attributeValue);
		}

	}

	public static WTLibrary getLibraryByName(String name) throws WTException {
		WTLibrary library = null;

		QuerySpec qs = new QuerySpec(WTLibrary.class);
		qs.appendWhere(new SearchCondition(WTLibrary.class, WTLibrary.NAME, SearchCondition.EQUAL, name),
				new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			library = (WTLibrary) qr.nextElement();
		}
		return library;
	}
}
