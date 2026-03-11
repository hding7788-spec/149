package ext.casc.changeRequest;

import com.glaway.mpm.util.IBAHelper;
import ext.casc.listener.ExtStandardListenerService;
import wt.change2.WTChangeRequest2;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import java.util.Enumeration;
import java.util.Map;

public class Change2Util {

	/**
	 * 方法功能: 通过编号获取更改请求
	 *
	 * @author cjh
	 * @date 2024/3/28
	 */
	public static WTChangeRequest2 getWTChangeRequest2ByNumber(String number){
		try {
			QuerySpec qs = new QuerySpec(WTChangeRequest2.class);
			qs.appendWhere(new SearchCondition(WTChangeRequest2.class, WTChangeRequest2.NUMBER, SearchCondition.EQUAL, number), new int[]{0});
			qs = new LatestConfigSpec().appendSearchCriteria(qs);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			while (qr.hasMoreElements()) {
				WTChangeRequest2 ecr = (WTChangeRequest2) qr.nextElement();
				if(ecr != null) {
					return ecr;
				}
			}
		} catch(QueryException e) {
			e.printStackTrace();
		} catch(WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 方法功能: 根据规则获取更改请求编号
	 *
	 * @author cjh
	 * @date 2024/3/28
	 */
	public static String getECRNumber (WTContainer container){
		String numberString = "";
		try {
			String bumenLeibieString = "";
			String xhlxString = "";
			Map<String, String> map = ExtStandardListenerService.getDescriptionByGroup();
			WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
			Enumeration groups = currentUser.parentGroups(false);
			while (groups.hasMoreElements()) {
				WTPrincipalReference principalRef = (WTPrincipalReference) groups.nextElement();
				WTGroup group = (WTGroup) principalRef.getPrincipal();
				String description = group.getDescription();
				if (!"".equals(description) && !"null".equals(description) && description != null) {
					if (map.containsKey(description)) {
						bumenLeibieString = map.get(description);
						break;
					}
				}
			}
			if ("".equals(bumenLeibieString)) {
				bumenLeibieString = "67";
			}
			com.glaway.mpm.util.IBAHelper ibaHelper2 = new IBAHelper((IBAHolder) container);
			xhlxString = ibaHelper2.getIBAValue("XHLX");

			if ("运载型号".equals(xhlxString)) {
				xhlxString = "1";
			} else if ("飞船型号".equals(xhlxString)) {
				xhlxString = "2";
			} else if ("战术型号".equals(xhlxString)) {
				xhlxString = "3";
			} else if ("其他".equals(xhlxString)) {
				xhlxString = "4";
			} else {
				xhlxString = "";
			}
			if ("".equals(xhlxString)) {
				long gengaiNumber = ExtStandardListenerService.getGengaiNumber(1, "YSrz273");
				numberString = "YSrz" + "273" + "-" + Long.toString(gengaiNumber);
			} else {
				long gengaiNumber = ExtStandardListenerService.getGengaiNumber(1, "YSrz" + bumenLeibieString + xhlxString);
				numberString = "YSrz" + bumenLeibieString + xhlxString + "-" + gengaiNumber;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return numberString;
	}

}
