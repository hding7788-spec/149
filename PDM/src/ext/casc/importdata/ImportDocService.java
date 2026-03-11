package ext.casc.importdata;

import java.io.File;
import java.rmi.RemoteException;
import java.util.List;
import wt.util.WTException;

import com.ptc.netmarkets.util.beans.NmCommandBean;

public interface ImportDocService {
	public String importObjects(NmCommandBean cb, File file, String tempFile,List<String> allFileName) throws WTException, RemoteException;
	public List<String> compressZIPData(File zipFile,String fileName) throws WTException;
}
