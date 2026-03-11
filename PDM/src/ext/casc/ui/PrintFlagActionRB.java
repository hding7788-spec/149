package ext.casc.ui;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;
@RBUUID("ext.casc.ui.PrintFlagActionRB")
public class PrintFlagActionRB extends WTListResourceBundle{
	@RBEntry("设置打印状态")
    public static final String PRINT_FLAG = "custom.editPrintStateWizard.description";

	@RBEntry("批量设置打印")
    public static final String DOWNLOADPRINT = "custom.DownloadPrint.description";

	@RBEntry("导出EXCEL")
    public static final String SEARCHWORKITEMDOWNLOAD = "custom.searchWorkItemDownload.description";

	@RBEntry("修改部件名称")
    public static final String CustomRename = "custom.CustomRename.description";




}
