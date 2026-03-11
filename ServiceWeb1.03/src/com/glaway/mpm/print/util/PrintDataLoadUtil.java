package com.glaway.mpm.print.util;

import java.util.List;


import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;

public class PrintDataLoadUtil {

	public static List<CmPrintInfoBean> loadPrintApplication(String oid) throws Exception {
		return PrintDataBuildUtil.getPrintApplication(oid);
	}

	public static List<CmPrintInfoBean> loadPaperFile(String oid, String category) {
		return PrintDataQueryUtil.loadPaperFile(oid, category);
	}

	public static List<CmPrintInfoBean> searchSealPlus(CmPrintQueryBean cmPrintQueryBean, String category) {
		return PrintDataBuildUtil.searchSealPlus(cmPrintQueryBean, category);
	}
}
