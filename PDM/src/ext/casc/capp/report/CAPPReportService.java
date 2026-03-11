package ext.casc.capp.report;

import java.io.File;
import java.io.IOException;

import wt.util.WTException;
import wt.util.WTRuntimeException;


public interface CAPPReportService {
	public boolean importBOM(File file) throws WTException;
	
	/**
	 * 存货管理表导出
	 * @param file
	 */
	public File exportCHGLB(String oid) throws  Exception ;
}
