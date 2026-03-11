package ext.casc.dfmRule.sign.resource;

import wt.util.resource.RBComment;
import wt.util.resource.RBEntry;
import wt.util.resource.RBPseudo;
import wt.util.resource.RBUUID;
import wt.util.resource.WTListResourceBundle;

/**
 
 * @author xuetao

 *
 */

@RBUUID("ext.casc.dfmRule.sign.resource.SubmitSignRB")
public final class SubmitSignRB extends WTListResourceBundle {

	@RBEntry("showComit")
	public static final String submitSignOn_0 = "submit.submitSignOn.description";

	@RBEntry("showComit")
	public static final String submitSignOn_1 = "submit.submitSignOn.tooltip";

	@RBEntry("dataMonitor_new.png")
	@RBPseudo(false)
	@RBComment("DO NOT TRANSLATE")
	public static final String submitSignOn_2 = "submit.submitSignOn.icon";

	@RBEntry("height=800,width=600")
	@RBPseudo(false)
	@RBComment("DO NOT TRANSLATE")
	public static final String submitSignOn_3 = "submit.submitSignOn.moreurlinfo";

}
