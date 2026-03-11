package ext.casc.product;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTGroup;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pdmlink.PDMLinkProduct;
import wt.project.Role;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.OrderByExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WTPrincipalUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.components.util.GroupUtils;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.CSCPrincipal;

public class DeleteAllQuitUsers extends DefaultObjectFormProcessor {
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
		FeedbackMessage message = null;
		try {
			System.out.println("------DeleteAllQuitUsers--begin----");
			PDMLinkProduct product = null;
			QuerySpec qs = new QuerySpec(PDMLinkProduct.class);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			System.out.println("product==========================size=" + qr.size());
			while (qr.hasMoreElements()) {
				product = (PDMLinkProduct) qr.nextElement();
				System.out.println("productName============" + product.getName());
				ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged) product);
				if (containerTeam != null) {
					Vector<Role> roles = containerTeam.getRoles();
					for (Role role : roles) {
						if (role.toString().equals("SHARED TEAM MANAGER")) {
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 != null) {
									if (object2 instanceof WTUser) {
										WTUser user = (WTUser) object2;
										WTUser wcadmin = UserUtil.getWTUserByName("Administrator");
										String fullName = user.getFullName();
										if (fullName.contains("离职")) {
											System.out.println("----------remove user:" + user.getFullName() + " from " + role);
											if (arrayList.size() == 1) {
												ContainerTeamHelper.service.addMember(containerTeam, role, wcadmin);
												ContainerTeamHelper.service.removeMember(containerTeam, role, user);
											}
										}
									} else if (object2 instanceof WTGroup) {
										WTGroup group = (WTGroup) object2;
										Enumeration enumeration = WTPrincipalUtil.getAllMembers(group);
										while (enumeration.hasMoreElements()) {
											Object object = enumeration.nextElement();
											if (object instanceof WTUser) {
												WTUser user = (WTUser) object;
												WTUser wcadmin = UserUtil.getWTUserByName("wcadmin");
												String fullName = user.getFullName();
												if (fullName.contains("离职")) {
													System.out.println("----------remove user:" + user.getFullName() + " from " + role);
													if (arrayList.size() == 1) {
														ContainerTeamHelper.service.addMember(containerTeam, role, wcadmin);
														ContainerTeamHelper.service.removeMember(containerTeam, role, user);
													}
												}
											}
										}
									}
								}
							}
						} else {
							ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
							for (WTPrincipalReference reference : arrayList) {
								Object object2 = reference.getPrincipal();
								if (object2 != null) {
									if (object2 instanceof WTUser) {
										WTUser user = (WTUser) object2;
										String fullName = user.getFullName();
										if (fullName.contains("离职")) {
											System.out.println("----------remove user:" + user.getFullName() + " from " + role);
											ContainerTeamHelper.service.removeMember(containerTeam, role, user);
										}
									} else if (object2 instanceof WTGroup) {
										WTGroup group = (WTGroup) object2;
										Enumeration enumeration = WTPrincipalUtil.getAllMembers(group);
										while (enumeration.hasMoreElements()) {
											Object object = enumeration.nextElement();
											if (object instanceof WTUser) {
												WTUser user = (WTUser) object;
												String fullName = user.getFullName();
												if (fullName.contains("离职")) {
													System.out.println("----------remove user:" + user.getFullName() + " from " + role);
													group.removeMember(user);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			message = new FeedbackMessage();
			message.addMessage("成功移除所有已离职人员!");
			System.out.println("------DeleteAllQuitUsers--end----");
		} catch (Exception e) {
			message = new FeedbackMessage();
			message.addMessage("移除所有已离职人员失败!");
			e.printStackTrace();
		} finally {
			formresult.addFeedbackMessage(message);
			formresult.setNextAction(FormResultAction.NONE);
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		return formresult;
	}
}
