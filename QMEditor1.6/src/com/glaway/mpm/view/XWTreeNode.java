package com.glaway.mpm.view;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.TechnicsUtil;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.visual.log.VaLogger;
import ext.casc.process.ProcessTaskItem;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class XWTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(XWTreeNode.class);
	private static String userOID = null;
	private XWTreeObject treeObject;
	private boolean isUsed = false;
	private static List<String> taskPartNumberList;

	static {
		List<String> user = UserUtil.getCurrentUserOid();
		if ((user != null) && (user.size() == 2)) {
			userOID = user.get(1);
		}

//		taskPartNumberList = TechnicsIntf.getCurrentUserTaskPartInfo();
//		logger.debug("当前用户的工艺任务零部件编号信息====" + taskPartNumberList);
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
		if ((this.treeObject != null) && ((this.treeObject instanceof XWPartTreeObject))) {
			XWPartTreeObject po = (XWPartTreeObject) this.treeObject;
			po.setAllowed(isUsed);
		}
		if ((this.treeObject != null) && ((this.treeObject instanceof XWPartTreeObjectForSearchTech))) {
			XWPartTreeObjectForSearchTech po = (XWPartTreeObjectForSearchTech) this.treeObject;
			po.setAllowed(isUsed);
		}
	}

	public boolean isUsed() {
		if ((this.treeObject != null) && ((this.treeObject instanceof XWPartTreeObject))) {
			XWPartTreeObject po = (XWPartTreeObject) this.treeObject;
			return po.isAllowed();
		}
		return this.isUsed;
	}

	public XWTreeNode(XWTreeObject xwObject) {
		super(xwObject);
		this.treeObject = xwObject;

		if ((this.treeObject != null) && ((this.treeObject instanceof XWPartTreeObject) || (this.treeObject instanceof  XWPartTreeObjectForSearchTech))) {
			Element part = this.treeObject.getTreeCellData();
			if (part != null) {
				String user = part.attributeValue("responser");
				String partNumber = part.attributeValue("partNumber");
				String version = part.attributeValue("version");
				String changeOid = NewTechnicsPart.changeOrderOid;
				//	if (!"".equals(NewTechnicsPart.workItemOid)&&!"null".equals(NewTechnicsPart.workItemOid)&&NewTechnicsPart.workItemOid!=null) {

//					boolean reportFlag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("getRenwuLeiXing",
//							new Class[] { String.class }, new Object[] { NewTechnicsPart.workItemOid });
				if (!EditorConfig.isZS) {//测试机任务全部放开
					setUsed(true);
				} else {
					if (NewTechnicsPart.reportTechnics) {
						String[] allpartid = NewTechnicsPart.allPartOid.split(",");
						setUsed(false);
						for (String s : allpartid) {
							if (part.attributeValue("oid").equals(s)) {
								setUsed(true);
								System.out.println(s + "<------------>");
								break;
							}
						}
					} else {
						if ("SOP".equals(com.glaway.mpm.EditorConfig.startType)) {
							if (!"".equals(NewTechnicsPart.workItemOid)) {
								boolean isUsed = SopIntf.checkProcessTaskItem(NewTechnicsPart.workItemOid, NewTechnicsPart.currentUser);
								setUsed(isUsed);
							} else {
								setUsed(true);
							}
						} else {
							List<ProcessTaskItem> list = (List<ProcessTaskItem>) IntfUtil.getPeRemoteMethodInvoke("getProcessTaskItemByPartNumber",
									new Class[]{String.class}, new Object[]{partNumber});
							for (int j = 0; j < list.size(); j++) {
								String gongyiyuan = list.get(j).getOwner();
								String taskItemName = list.get(j).getTaskItemName();
								String taskType = list.get(j).getTaskType();
								String taskItemState = list.get(j).getTaskItemState();
								if (!"报表类工艺任务".equals(taskType)) {
									if (!"".equals(gongyiyuan) && !"null".equals(gongyiyuan) && gongyiyuan != null &&
											(taskItemName.endsWith("编制") || taskItemName.endsWith("任务")) /*&& "正在进行".equals(taskItemState)*/) {
										String users = NewTechnicsPart.currentUser;
										if (gongyiyuan.equals(users)) {
											setUsed(true);
											break;
										}

									}
								}
							}

						}
						if (!this.isUsed) {
							Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("hasGengggaiProcessDoc",
									new Class[]{String.class, String.class, String.class}, new Object[]{partNumber, version, NewTechnicsPart.currentUser});
							if (flag) {
								setUsed(true);
							}
						}
					}
					//	}

				/*else{
					String[] allpartid=NewTechnicsPart.allPartOid.split(",");
					setUsed(false);
					for(String s:allpartid){
						if(part.attributeValue("oid").equals(s)){
							setUsed(true);
							System.out.println(s+"<------------>");
							break;
						}
					}
				}*/

					if (partNumber.endsWith("PROCESSPLAN")) {
						setUsed(true);
					}
					if (changeOid != null && "".equals(changeOid)) {
						setUsed(true);
					}

				}
			}
		}
	}
	public ArrayList FindAllChilden(XWTreeNode root) {
		ArrayList list=new ArrayList();
		for (int i = 0; i < root.getChildCount(); i++) {
			XWTreeNode childAt = (XWTreeNode) root.getChildAt(i);
			list.add(childAt);
			if (childAt.getChildCount()>0) {
				FindAllChilden(childAt);
			}
		}
		list.add(root);
		return list;

	}

	public XWTreeNode getC() {
		XWTreeNode nn = (XWTreeNode) getFirstChild();
		return nn;
	}

	public XWTreeNode getP() {
		return (XWTreeNode) getParent();
	}

	public XWTreeObject getObject() {
		return this.treeObject;
	}

	public void setObject(XWTreeObject obj) {
		this.treeObject = obj;
	}

	public Image getCloseImage() {
		return this.treeObject.getCloseImage();
	}

	public Image getOpenImage() {
		return this.treeObject.getOpenImage();
	}

	public String getDisplayName() {
		return this.treeObject.getDisplayName();
	}

	public String getTipNoteText() {
		return this.treeObject.getTipNoteText();
	}

	public String toString() {
		return this.treeObject.getDisplayName();
	}

	public void expand() {
		try {
			Vector datas = this.treeObject.expand();
			for (int i = 0; i < datas.size(); i++) {
				XWTreeObject child = (XWTreeObject) datas.get(i);
				addChild(child);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "工艺展开出现错误！", "提示", 1);
		}
	}

	public XWTreeNode contianOccPathChildren(XWTreeObject child) {
		int count = getChildCount();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				XWTreeNode node = (XWTreeNode) getChildAt(i);
				XWTreeObject xO = node.getObject();
				if (xO.getClass().getName().equals(child.getClass().getName())) {
					Element element = child.getTreeCellData();
					if (element == null) {
						return null;
					}
					Element newElement = node.getObject().getTreeCellData();
					if (!element.attributeValue("partNumber").equals(newElement.attributeValue("partNumber"))) {
						continue;
					}
					if (element.attributeValue("occpath") != null && element.attributeValue("occpath").equals(
									newElement.attributeValue("occpath"))) {
						return node;
					}
				}
			}
		}
		return null;
	}

	public XWTreeNode contianInChildren(XWTreeObject child) {
		int count = getChildCount();
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				XWTreeNode node = (XWTreeNode) getChildAt(i);
				XWTreeObject xO = node.getObject();
				if (xO.getClass().getName().equals(child.getClass().getName())) {
					if (xO.compareTo(child) == 0)
						return node;
				}
			}
		}
		return null;
	}

	public XWTreeNode addChild(XWTreeObject child) {
		XWTreeNode node = null;
		if (child instanceof XWPartTreeObject || child instanceof  XWPartTreeObjectForSearchTech) {
			node = contianOccPathChildren(child);
		}else if(child instanceof XWMesStepTreeObject || child instanceof XWMesPaceTreeObject){
			node = null;
		}else {
			node = contianInChildren(child);
		}
		if (node == null) {
			node = new XWTreeNode(child);
			if (child instanceof TechnicsMessageTreeObject) {
				TechnicsMessageTreeObject addingNodeTechnics = (TechnicsMessageTreeObject) child;
				String categoty = addingNodeTechnics.getTechnicsCategory();
				if ("rework".equals(categoty) || "temp".equals(categoty)) {
					if (this.getChildCount() == 0) {
						insert(node, 0);
					} else {
						Enumeration childrens = this.children();
						int index = 0;
						while (childrens.hasMoreElements()) {
							XWTreeNode childNode = (XWTreeNode) childrens.nextElement();
							XWTreeObject obj = childNode.getObject();
							if (obj instanceof TechnicsMessageTreeObject) {
								TechnicsMessageTreeObject temp = (TechnicsMessageTreeObject) obj;
								if ("rework".equals(temp.getTechnicsCategory())) {
									if ("rework".equals(addingNodeTechnics.getTechnicsCategory())) {
										int tempNumber = TechnicsUtil.getReworkNumber(temp.getTechName());
										int addingNumber = TechnicsUtil.getReworkNumber(addingNodeTechnics.getTechName());
										if (addingNumber < tempNumber) {
											index = this.getIndex(childNode);
											break;
										} else {
											++index;
											continue;
										}
									} else {
										XWTreeNode nextNode = (XWTreeNode) childNode.getNextNode();
										if (nextNode == null) {
											++index;
											break;
										}
										if (nextNode.getObject() instanceof TechnicsMessageTreeObject) {
											TechnicsMessageTreeObject nextObject = (TechnicsMessageTreeObject) nextNode.getObject();
											if ("rework".equals(nextObject.getTechnicsCategory())) {
												nextNode = (XWTreeNode) nextNode.getNextNode();
												++index;
												continue;
											} else {
												++index;
												continue;
											}
										}
									}
								} else if ("temp".equals(temp.getTechnicsCategory())
										&& "temp".equals(addingNodeTechnics.getTechnicsCategory())) {
									int tempNumber = TechnicsUtil.getTempNumber(temp.getTechName());
									int addingNumber = TechnicsUtil.getTempNumber(addingNodeTechnics.getTechName());
									if (addingNumber < tempNumber) {
										index = this.getIndex(childNode);
										break;
									} else {
										++index;
										continue;
									}
								} else {
									++index;
									continue;
								}
							} else {
								index = this.getIndex(childNode);
								break;
							}
						}
						insert(node, index);
					}
				} else {
					insert(node, 0);
				}
			} else {
				add(node);
			}
		}
		node.setParent(this);
		return node;
	}

	public void expandAll() {
		try {
			Vector datas = this.treeObject.expand();
			if (datas!=null) {
			    for (int i = 0; i < datas.size(); i++) {
			        XWTreeObject child = (XWTreeObject) datas.get(i);
//				XWTreeNode node=(XWTreeNode) child;
			        if (child instanceof GongZhuangSQDObject) {
			            XWTreeNode node=new XWTreeNode(child);
			            add(node);
			        }
			        XWTreeNode childNode = addChild(child);
			        childNode.expandAll();
			    }
            }
		} catch (Exception e) {
			e.printStackTrace();
//			JOptionPane.showMessageDialog(null, "工艺展开出现错误！", "提示", 1);
		}
	}
	public void expandAllSteps() {
		try {
			Vector datas = this.treeObject.expand();
			if (datas!=null) {
			    for (int i = 0; i < datas.size(); i++) {
			    	if(datas.get(i) instanceof XWStepTreeObject|| datas.get(i) instanceof XWPaceTreeObject
			    			|| datas.get(i) instanceof XWMesStepTreeObject|| datas.get(i) instanceof XWMesPaceTreeObject){
			    		XWTreeObject child = (XWTreeObject) datas.get(i);
			    		XWTreeNode childNode = addChild(child);
			    		childNode.expandAllSteps();
			    	}
			    }
            }
		} catch (Exception e) {
			e.printStackTrace();
//			JOptionPane.showMessageDialog(null, "工艺展开出现错误！", "提示", 1);
		}
	}
	public void expandAllZFSteps(XWTreeNode zfTechnicNode) {
		try {
			for(int i = 0; i < zfTechnicNode.getChildCount(); i++){
				XWTreeNode childNode = (XWTreeNode) zfTechnicNode.getChildAt(i);
				childNode.expandAllSteps();
			}
		} catch (Exception e) {
			e.printStackTrace();
//			JOptionPane.showMessageDialog(null, "工艺展开出现错误！", "提示", 1);
		}
	}
	public void expandAllPaces(XWTreeNode stepNode) {
		try {
			XWTreeObject stepObject = stepNode.getObject();
			Vector datas = stepObject.expand();
			if (datas!=null) {
			    for (int i = 0; i < datas.size(); i++) {
			    	if(datas.get(i) instanceof XWPaceTreeObject){
			    		XWTreeObject child = (XWTreeObject) datas.get(i);
			    		XWTreeNode node =new XWTreeNode(child);
			    		stepNode.addChild(child);
			    	}
			    }
            }
		} catch (Exception e) {
			e.printStackTrace();
//			JOptionPane.showMessageDialog(null, "工艺展开出现错误！", "提示", 1);
		}
	}
	public List<XWTreeNode> getAllSteps(){
		List<XWTreeNode> list = new ArrayList<XWTreeNode>();
		try {
			Vector datas = this.treeObject.expand();
			if (datas!=null) {
			    for (int i = 0; i < datas.size(); i++) {
			    	if(datas.get(i) instanceof XWStepTreeObject|| datas.get(i) instanceof XWPaceTreeObject
			    			|| datas.get(i) instanceof XWMesStepTreeObject|| datas.get(i) instanceof XWMesPaceTreeObject){
			    		XWTreeObject child = (XWTreeObject) datas.get(i);
			    		XWTreeNode childNode = new XWTreeNode(child);
			    		childNode.expand();
			    		list.add(childNode);
			    	}
			    }
            }
		} catch (Exception e) {
			e.printStackTrace();
//			JOptionPane.showMessageDialog(null, "工艺展开出现错误！", "提示", 1);
		}
		return list;
	}
}
