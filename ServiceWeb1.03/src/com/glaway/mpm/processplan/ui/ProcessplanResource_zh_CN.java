package com.glaway.mpm.processplan.ui;

import wt.util.resource.RBEntry;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.glaway.mpm.processplan.ui.PBOMResource")
public class ProcessplanResource_zh_CN extends WTListResourceBundle {

	@RBEntry("添加人员")
	public static final String S1 = "processplan.addUserWizard.description";

	@RBEntry("工艺报表")
	public static final String S2 = "processplan.processPlanReport.description";

	@RBEntry("工时定额统计报表")
	public static final String S3 = "processplan.workTimeReport.description";

	@RBEntry("材料定额统计报表")
	public static final String S4 = "processplan.materialBrandReport.description";

	@RBEntry("工艺主物料汇总")
	public static final String S5 = "processplan.primary.description";

	@RBEntry("工艺辅料汇总")
	public static final String S6 = "processplan.assistant.description";

	@RBEntry("合编工艺派工")
	public static final String PROCESSPLAN_ALLOCATE = "PROCESSPLAN_ALLOCATE";

	@RBEntry("添加人员")
	public static final String Add_User = "Add_User";

}
