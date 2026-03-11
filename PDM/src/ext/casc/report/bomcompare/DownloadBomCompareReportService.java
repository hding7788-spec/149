package ext.casc.report.bomcompare;

import java.io.File;

import wt.util.WTException;

public interface DownloadBomCompareReportService {

	public File export(String oid,String type) throws WTException;
}
