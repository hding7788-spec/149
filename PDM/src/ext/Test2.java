/**
 *
 */
package ext;

import com.glaway.mpm.util.DateUtil;
import com.glaway.mpm.util.WTPartUtil;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import ext.casc.workflow.CmWorkflowHelper;
import wt.fc.IdentityHelper;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.views.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cfire
 *
 */
public class Test2  {
	public static void main(String[] args) throws IOException {
        try {
            test("A20250327-0");
        } catch (WTException e) {
            throw new RuntimeException(e);
        } catch (WTPropertyVetoException e) {
            throw new RuntimeException(e);
        }

    }

	public static void test(String number) throws WTException, WTPropertyVetoException {
		WTPart parentPart =  WTPartUtil.getLatestPartByNumberAndView(number,"Manufacturing");
		String currentDateStr = "yyyyMMdd";

		if(parentPart!=null) {

			List<WTPart> allPart = new ArrayList<WTPart>();
			allPart.add(parentPart);
			DownloadTechnicsReportUtil.getAllChildPart(parentPart, allPart);
			for(WTPart p:allPart){
				WTPartMaster master = (WTPartMaster) p.getMaster();
				WTPartMasterIdentity idy = (WTPartMasterIdentity) master.getIdentificationObject();
				String newNumber = p.getNumber()+"_DEL"+currentDateStr;
				idy.setNumber(newNumber+ "3");
				master = (WTPartMaster) IdentityHelper.service.changeIdentity(master, idy);
				System.out.println(newNumber);
			}
		}
	}
}
