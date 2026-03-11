package com.glaway.mpm.processplan.ui;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBPseudo;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

@RBUUID("com.glaway.mpm.processplan.ui.PBOMResource")
public class ProcessplanResource extends WTListResourceBundle {

	@RBEntry("Add User")
	public static final String S1 = "processplan.addUserWizard.description";

	@RBEntry("ProcessPlan Report")
	public static final String S2 = "processplan.processPlanReport.description";

	@RBEntry("WorkTime Report")
	public static final String S3 = "processplan.workTimeReport.description";

	@RBEntry("MaterialBrand Report")
	public static final String S4 = "processplan.materialBrandReport.description";

	@RBEntry("Primary")
	public static final String S5 = "processplan.primary.description";

	@RBEntry("Assistant")
	public static final String S6 = "processplan.assistant.description";

	@RBEntry("PROCESSPLAN_ALLOCATE")
	public static final String PROCESSPLAN_ALLOCATE = "PROCESSPLAN_ALLOCATE";

	@RBEntry("Add User")
	public static final String Add_User = "Add_User";
}
