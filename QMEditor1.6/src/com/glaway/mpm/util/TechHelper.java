package com.glaway.mpm.util;

import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.apache.commons.io.IOUtils;
import wt.util.WTException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

public class TechHelper {

	public static void uploadAttachForDocument(String docNumber, String fileName, File file) throws WTException, IOException, InvocationTargetException {
		FileInputStream fis = new FileInputStream(file);
		try {
			byte[] bytes = IOUtils.toByteArray(fis);
			TechnicsIntf.uploadAttachForDocument(docNumber, fileName, bytes);
		} finally {
			fis.close();
		}
	}

	public static void uploadAttachForSOP(String docNumber, String fileName, File file) throws WTException, IOException, InvocationTargetException {
		FileInputStream fis = new FileInputStream(file);
		try {
			byte[] bytes = IOUtils.toByteArray(fis);
			TechnicsIntf.uploadAttachForSOP(docNumber, fileName, bytes);
		} finally {
			fis.close();
		}
	}

	public static void deleteAttachForSOP(String docNumber) {
		try {
			TechnicsIntf.deleteAttachForSOP(docNumber);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			e1.printStackTrace();
		}
	}

}
