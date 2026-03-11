package com.glaway.mpm.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.glaway.mpm.print.util.ComparatorUtil;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc._ObjectIdentifier;
import wt.fc._PersistInfo;
import wt.fc._Persistable;
import wt.folder.Cabinet;
import wt.folder.FolderHelper;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class WTContainerUtil {

	private static int index[] = { 0 };

	/**
	 * 通过名称获取物件库
	 *
	 * @author qianlong
	 * @date 2012-12-7
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static WTLibrary getLibraryByName(String name) throws WTException {
		WTLibrary library = null;

		QuerySpec qs = new QuerySpec(WTLibrary.class);
		qs.appendWhere(new SearchCondition(WTLibrary.class, WTLibrary.NAME, SearchCondition.EQUAL, name), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			library = (WTLibrary) qr.nextElement();
		}
		return library;
	}

	/**
	 * 通过名称获取产品
	 *
	 * @author qianlong
	 * @date 2012-12-7
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static PDMLinkProduct getProductByName(String name) throws WTException {
		PDMLinkProduct product = null;

		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		qs.appendWhere(new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.EQUAL, name),
				index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			product = (PDMLinkProduct) qr.nextElement();
		}
		return product;
	}

	/**
	 * 通过oid获取产品
	 *
	 * @param oid
	 * @return PDMLinkProduct
	 * @throws WTException
	 */
	public static PDMLinkProduct getProductByOid(long oid) throws WTException {
		PDMLinkProduct product = null;
		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		qs.appendWhere(new SearchCondition(PDMLinkProduct.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, oid),
				index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			product = (PDMLinkProduct) qr.nextElement();
		}
		return product;
	}

	/**
	 * 获取所有的产品
	 *
	 * @author qianlong
	 * @date 2012-12-7
	 * @param name
	 * @return
	 * @throws WTException
	 *
	 */
	public static List<PDMLinkProduct> getAllProduct() throws WTException {
		List<PDMLinkProduct> list = new ArrayList<PDMLinkProduct>();

		QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
		qs.appendWhere(new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME, SearchCondition.NOT_NULL, true),
				index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			PDMLinkProduct product = (PDMLinkProduct) qr.nextElement();
			list.add(product);
		}
		return list;
	}

	/**
	 * 根据上下文的名称获取上下文
	 *
	 * @author qianlong
	 * @date 2013-4-12
	 * @return
	 * @throws WTException
	 *
	 */
	public static WTContainer getContainerByName(String name) throws WTException {
		WTContainer product = null;

		QuerySpec qs = new QuerySpec(WTContainer.class);
		qs.appendWhere(new SearchCondition(WTContainer.class, WTContainer.NAME, SearchCondition.EQUAL, name), index);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			product = (WTContainer) qr.nextElement();
			if(product instanceof PDMLinkProduct){
				return product;
			}
		}
		return product;
	}

	/**
	 * 获取指定容器下的一级资料夹
	 *
	 * @author lbzhang
	 * @date 2013-4-25
	 * @param container
	 * @return
	 * @throws WTException
	 *
	 */
	public static ArrayList<String> getTopFolderOfWTContainer(WTContainer container) throws WTException {
		ArrayList<String> folderList = new ArrayList<String>();
		WTContainerRef containerRef = WTContainerRef.newWTContainerRef(container);
		Cabinet cabinet = FolderHelper.service.getCabinet("Default", containerRef);
		QueryResult qr = FolderHelper.service.findSubFolders(cabinet);
		while (qr.hasMoreElements()) {
			SubFolder folder = (SubFolder) qr.nextElement();
			folderList.add(folder.getName());
		}
		return folderList;
	}

	/**
	 * 查询当前使用者所属产品的名称
	 *
	 * @author lbzhang
	 * @date 2013-4-25
	 * @return
	 * @throws WTException
	 *
	 */
	public static ArrayList<String> getAllProductName() throws WTException {
		List<PDMLinkProduct> list = getAllProduct();
		ArrayList<String> products = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			products.add(list.get(i).getName());
		}
		return products;
	}


	public static void main(String[] args) {
		try {
			getContainerByName(null);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 查询所有产品的型号代号
	 * @return
	 * @throws WTException
	 */
	public static Vector<String> getAllProductMindex() throws WTException {
		Vector<String> vector = new Vector<String>();
		List<PDMLinkProduct> list = getAllProduct();
		for (PDMLinkProduct product : list) {
			String mindex = IBAHelper.getIBAValue(product, "MINDEX");
			vector.add(mindex);
		}
		ComparatorUtil.compareWTObjectName(vector);
		return vector;
	}
	/**
	 * 通过型号代号获取产品
	 *
	 * @throws WTException
	 * @throws RemoteException
	 * @throws WTPropertyVetoException
	 *
	 */
	public static WTContainer getContainerByMindex(String mindex) throws WTException, WTPropertyVetoException, RemoteException {
		WTContainer container = null;

		QuerySpec qs = new QuerySpec(WTContainer.class);
		qs.setAdvancedQueryEnabled(true);

		ClassAttribute caBaseLinetId = new ClassAttribute(WTContainer.class, _Persistable.PERSIST_INFO + "." + _PersistInfo.OBJECT_IDENTIFIER + "." + _ObjectIdentifier.ID);
		SubSelectExpression ss = IBAHelper.getStringIBAQuery("MINDEX", mindex);
		qs.appendWhere(new SearchCondition(caBaseLinetId, SearchCondition.IN, ss), index);

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if (qr.hasMoreElements()) {
			container = (WTContainer) qr.nextElement();
		}
		return container;
	}
}