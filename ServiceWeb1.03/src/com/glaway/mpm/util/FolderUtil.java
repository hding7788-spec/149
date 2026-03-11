package com.glaway.mpm.util;

import java.io.Serializable;

import wt.doc.WTDocument;
import wt.fc.collections.WTValuedHashMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.method.RemoteAccess;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class FolderUtil implements RemoteAccess, Serializable {



	/**
	 * 获取文件夹
	 *
	 * @author qianlong
	 * @date 2013-4-2
	 * @param folderPath
	 * @param containerRef
	 * @return
	 * @throws WTException
	 *
	 */
	public static Folder getFolder(String folderPath, WTContainerRef containerRef) throws WTException {
		System.out.println("-getFolder----folderPath--"+containerRef.getName()+":"+folderPath);
		SessionServerHelper.manager.setAccessEnforced(false);
		Folder folder = null;
		if(folderPath==null){
			folderPath="Default/";
		}
		try {
			folder = FolderHelper.service.getFolder(folderPath, containerRef);
		} catch (WTException e) {
			folder = FolderHelper.service.saveFolderPath(folderPath, containerRef);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return folder;
	}
	/**
	 * 设置路径，若路径不存在，则自动创建
	 * @param folderPath
	 * @param wtdoc
	 * @throws WTException
	 */
	public static void setDocFolder(String folderPath, WTDocument wtdoc) throws WTException {
		Folder folder = null;
		try {
			folder = FolderHelper.service.getFolder(folderPath, wtdoc.getContainerReference());
		} catch (WTException e) {
			e.printStackTrace();
		}
		if (folder == null) {
			folder = FolderHelper.service.createSubFolder(folderPath, wtdoc.getContainerReference());
		}
		if (folder != null) {
			WTValuedHashMap map = new WTValuedHashMap();
			map.put(wtdoc, folder);
			FolderHelper.assignLocations(map);
		}
	}

}
