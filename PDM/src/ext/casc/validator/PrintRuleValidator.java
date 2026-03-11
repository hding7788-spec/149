package ext.casc.validator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;

public class PrintRuleValidator extends DefaultSimpleValidationFilter{

	 	@Override
	    public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
	 		try {
				String id = key.getComponentID();
				WTUser currentUser = (WTUser)SessionHelper.getPrincipal();
				if (currentUser.getName().equals("Administrator") || "fileRecycling".equals(id) || "printApply".equals(id) || "sealPlus".equals(id)){
    				return UIValidationStatus.ENABLED;
    			}
//	            if ("sealPlus".equals(id)){//加盖印章
//	            	WTContainer wtContainer = PrintUtil.getContainerByName("打印分发管理库");
//					ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) wtContainer);
//	            	Role role = Role.toRole("XINGHAOGONGYISHI");
//	            	ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
//	            	if (role == null) {
//                        return UIValidationStatus.DISABLED;
//                    }
//	            	for (WTPrincipalReference reference : arrayList) {
//                        Object object2 = reference.getPrincipal();
//                        if (object2 instanceof WTUser) {
//                            WTUser user = (WTUser) object2;
//                            if (user.getName().equals(currentUser.getName())) {
//                                return UIValidationStatus.ENABLED;
//                            }
//                        }else if (object2 instanceof WTGroup) {
//							WTGroup group = (WTGroup) object2;
//							if (group.isMember(currentUser)) {
//                                return UIValidationStatus.ENABLED;
//							}
//						}
//	            	}
//	            }else
				/**
				 * --------------打印功能权限配置----------------
				 * 1、厂内工艺文件
				 *		打印申请---档案员、资料员、工艺员//2019.7.1更新，不受限
				 *		加盖印章---档案员、资料员、工艺员
				 *		文件回收---档案员、资料员、工艺员//2019.7.1更新，不受限
				 *		文件入库---档案员
				 *		文件封存---档案员
				 * 2、外来文件
				 * 		所有---档案员
				 * 3、厂内纸质文件
				 *		所有---档案员
				 * 4、印章管理
				 * 		   ---档案员
				 */
//				if("sealPlus".equals(id)){
//	            	List<String> list = new ArrayList<String>();
//	            	//工艺员
//	            	list.add("一分厂工艺员组");
//	            	list.add("二分厂工艺员组");
//	            	list.add("三分厂工艺员组");
//	            	list.add("四分厂工艺员组");
//	            	list.add("五分厂工艺员组");
//	            	list.add("六分厂工艺员组");
//	            	list.add("七分厂工艺员组");
//	            	list.add("八分厂工艺员组");
//	            	list.add("九分厂工艺员组");
//	            	list.add("十分厂工艺员组");
//	            	//资料员
//	            	list.add("一分厂资料员组");
//	            	list.add("二分厂资料员组");
//	            	list.add("三分厂资料员组");
//	            	list.add("四分厂资料员组");
//	            	list.add("五分厂资料员组");
//	            	list.add("六分厂资料员组");
//	            	list.add("七分厂资料员组");
//	            	list.add("八分厂资料员组");
//	            	list.add("九分厂资料员组");
//	            	list.add("十分厂资料员组");
//	            	//档案员
//					list.add("运载型号档案员组");
//					list.add("战术型号档案员组");
//					list.add("飞船型号档案员组");
//					list.add("新品型号档案员组");
//	            	Enumeration groups = currentUser.parentGroupNames();
//	            	while(groups.hasMoreElements()){
//	            		String gname = (String) groups.nextElement();
//	            		if(list.contains(gname)){
//	            			return UIValidationStatus.ENABLED;
//	            		}
//	            	}
//	            }else
	            	if("sealManager".equals(id) || "outsideFile".equals(id) || "insidePaperFile".equals(id) || "fileInstore".equals(id) || "fileSafekeeping".equals(id)){
					List<String> list = new ArrayList<String>();
					list.add("运载型号档案员组");
					list.add("战术型号档案员组");
					list.add("飞船型号档案员组");
					list.add("新品型号档案员组");
					Enumeration groups = currentUser.parentGroupNames();
					while(groups.hasMoreElements()){
						String gname = (String) groups.nextElement();
						if(list.contains(gname)){
							return UIValidationStatus.ENABLED;
						}
					}
				}
				if ("fileTransfer".equals(id)){
					List<String> list = new ArrayList<String>();
					list.add("一分厂资料员组");
					list.add("二分厂资料员组");
					list.add("三分厂资料员组");
					list.add("四分厂资料员组");
					list.add("五分厂资料员组");
					list.add("六分厂资料员组");
					list.add("七分厂资料员组");
					list.add("八分厂资料员组");
					list.add("九分厂资料员组");
					list.add("十分厂资料员组");
					Enumeration groups = currentUser.parentGroupNames();
					while(groups.hasMoreElements()){
						String gname = (String) groups.nextElement();
						if(list.contains(gname)){
							return UIValidationStatus.ENABLED;
						}
					}
				}
			} catch (WTException e) {
				e.printStackTrace();
			}
	 	return UIValidationStatus.HIDDEN;
	 }
}
