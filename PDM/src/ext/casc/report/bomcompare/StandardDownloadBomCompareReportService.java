package ext.casc.report.bomcompare;

import java.io.File;
import java.io.Serializable;

import ext.casc.report.BOMReprotsService;
import ext.casc.util.WCUtil;
import wt.fc.Persistable;
import wt.part.WTPart;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardDownloadBomCompareReportService extends StandardManager implements DownloadBomCompareReportService, Serializable {

	private static final long serialVersionUID = 1L;

	public static StandardDownloadBomCompareReportService newStandardDownloadBomCompareReportService() throws WTException {
		StandardDownloadBomCompareReportService service = new StandardDownloadBomCompareReportService();
		service.initialize();
		return service;
	}

	@Override
	public File export(String oid, String type) throws WTException {
		if(oid == null || "".equals(oid)) {
			System.out.println("-----oid-----"+oid);
			return null;
		}
		if(type == null || "".equals(type)) {
			System.out.println("-----type-----"+type);
			return null;
		}

		Persistable per = WCUtil.getPersistable(oid);
		if(per instanceof WTPart) {
			WTPart part = (WTPart)per;
			//add by chum 2016.02.06 begin
			if(type.equals("EBOMCompareToPBOM")) {
				return DownloadBomCompareReportUtil.exportEBOMCompareToPBOM(part);
			}else if(type.equals("ebomExport")){
				return BOMReprotsService.ebomExport(part);
			}else if(type.equals("faciExport")){ //发次BOM
				return BOMReprotsService.faciBomExport(part);
			}
//			else if(type.equals("PBOMCompareToPBOM")) {
//				return DownloadBomCompareReportUtil.exportPBOMCompareToPBOM(part);
//			}
			//add by chum 2016.02.06 end
		} else {
			System.out.println("oid not is a WTPart");
			return null;
		}

		return null;
	}

}
