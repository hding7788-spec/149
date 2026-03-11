package ext.casc.report.technics;

import java.io.File;

import wt.util.WTException;

public interface DownloadTechnicsReportService {
	public File export(String oid,String type, String batch) throws WTException;
}
