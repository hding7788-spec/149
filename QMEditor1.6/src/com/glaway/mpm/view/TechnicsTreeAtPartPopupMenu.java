package com.glaway.mpm.view;

import com.glaway.mpm.qmIntf.template.copyTechinics.TemplateSearchDialogForCopyTechnics;
import com.glaway.mpm.task.CreateTaskItemDialog;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

public class TechnicsTreeAtPartPopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static VaLogger logger = VaLogger.getLogger(TechnicsTreeAtPartPopupMenu.class);

	private JMenuItem technicsCoWork = new JMenuItem("工艺合编");
	private JMenuItem submitUniteTechnics = new JMenuItem("提交工艺合编");
	private JMenuItem technicsConfirm = new JMenuItem("工艺路线确认");
	private JMenuItem reviewTechnics = new JMenuItem("工艺预览");
	private JMenuItem technicsUpload = new JMenuItem("工艺上载");
	private JMenuItem relateMainMakeProcessPlan = new JMenuItem("关联主制工艺");
	private JMenuItem relateAidedProcessPlan = new JMenuItem("关联辅制工艺");
	private JMenuItem technicsUpdate = new JMenuItem("工艺更新");
	private JMenuItem signed = new JMenuItem("提交三级工艺签审");
	private JMenuItem signed2 = new JMenuItem("提交五级工艺签审");
	private JMenuItem completeTask = new JMenuItem("完成工艺任务");
	private JMenuItem viewHistory = new JMenuItem("查看历史版本");
	private JMenuItem copy = new JMenuItem("复制");
	private JMenuItem delete = new JMenuItem("删除");
	private JMenuItem createTaskItem = new JMenuItem("提交辅制工艺任务");
	private JMenuItem pdfReviewTechnics = new JMenuItem("PDF工艺预览");
	private JMenuItem jishuxieyi = new JMenuItem("新建技术协议");
	private JMenuItem editNumber = new JMenuItem("修改工艺文件编号");
	private JMenuItem copyTechnicsFromTemlate = new JMenuItem("从模板复用工艺");
	private JMenuItem controlExternalProcess = new JMenuItem("外协工艺一键受控");

	private NewTechnicsPart frame;

	private XWPartTreePanel treePanel;

	public TechnicsTreeAtPartPopupMenu(NewTechnicsPart frame,
			XWPartTreePanel treePanel) {
		super();

		this.frame = frame;
		this.treePanel = treePanel;
		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);

//		add(technicsCoWork);
//		technicsCoWork.addActionListener(this);
//		add(submitUniteTechnics);
//		submitUniteTechnics.addActionListener(this);
//		add(technicsConfirm);
//		technicsConfirm.addActionListener(this);
//		addSeparator();
		add(editNumber);
		editNumber.addActionListener(this);

        jishuxieyi.addActionListener(this);
		add(reviewTechnics);
		reviewTechnics.addActionListener(this);
		add(pdfReviewTechnics);
		pdfReviewTechnics.addActionListener(this);
		addSeparator();

		add(technicsUpload);
		technicsUpload.addActionListener(this);
		//add(technicsUpdate);
		//technicsUpdate.addActionListener(this);
		relateMainMakeProcessPlan.addActionListener(this);
		relateAidedProcessPlan.addActionListener(this);


		addSeparator();
		signed.addActionListener(this);
		add(signed2);
		signed2.addActionListener(this);
		addSeparator();

		createTaskItem.addActionListener(this);
//		add(completeTask);
//		completeTask.addActionListener(this);
		add(viewHistory);
		viewHistory.addActionListener(this);
		addSeparator();

		copy.addActionListener(this);
		add(copy);

		delete.addActionListener(this);
		add(delete);
		
		
		if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			add(relateAidedProcessPlan);
			add(relateMainMakeProcessPlan);
			add(createTaskItem);
			add(signed);
			add(jishuxieyi);
		}
		controlExternalProcess.addActionListener(this);
		add(controlExternalProcess);
		addSeparator();
		copyTechnicsFromTemlate.addActionListener(this);
		add(copyTechnicsFromTemlate);

		initMenuItemIcon();

	}

	// 设置菜单项图标
	private void initMenuItemIcon() {
		reviewTechnics.setIcon(IconUtil.getImageIcon(IconUtil.PREVIEW));
		copy.setIcon(IconUtil.getImageIcon(IconUtil.COPY));
		delete.setIcon(IconUtil.getImageIcon(IconUtil.DELETE));
		editNumber.setIcon(IconUtil.getImageIcon(IconUtil.EDITNUMBER));
	}

	public void setAllMenuState() {
	    editNumber.setEnabled(false);
		jishuxieyi.setEnabled(false);
		 technicsCoWork.setEnabled(false);
		 submitUniteTechnics.setEnabled(false);
		 technicsConfirm.setEnabled(false);
		 reviewTechnics.setEnabled(false);
		 technicsUpload.setEnabled(false);
		 technicsUpdate.setEnabled(false);
		 signed.setEnabled(false);
		 signed2.setEnabled(false);
		 controlExternalProcess.setEnabled(false);
		 completeTask.setEnabled(false);
		 viewHistory.setEnabled(false);
		 copy.setEnabled(false);
		 delete.setEnabled(false);
		 createTaskItem.setEnabled(false);
		 pdfReviewTechnics.setEnabled(false);
	}
	public void setMenuState(XWTreeObject xo) {
		if (xo == null) {
			setNullSelectedStatus();
		}
		if (xo instanceof TechnicsMessageTreeObject) {
			XWTreeNode node = frame.getTechnicsTreePanel().getSelectedTreeNode();
			System.out.println(node);
			if (node == null) {
				setTechnicsSelectedStatus(null);
			} else {
				if (NewTechnicsPart.reportTechnics) {
					setAllMenuState();
				}else{
					XWTreeObject obj = node.getObject();
					setTechnicsSelectedStatus(obj);
				}
			}
		} else if (xo instanceof XWStepTreeObject) {
			setStepSelectedStatus(xo);
		} else if (xo instanceof XWPaceTreeObject) {
			setPaceSelectedStatus();
		} else if (xo instanceof XWPartTreeObject) {
			setPartSelectedStatus();
			XWPartTreeObject po = (XWPartTreeObject) xo;
		} else if (xo instanceof XWProductTreeObject) {
			setProductSelectedStatus();
		} else if (xo instanceof ReportTechnicsTreeObject) {
			if (NewTechnicsPart.reportTechnics||NewTechnicsPart.isXinZengGengGai) {
				setReportTechnicsSelectedStatus(xo);
			}else{
				setAllMenuState();
			}
		}

	}

	public void setReworkMenuState(XWTreeObject xo) {
		if (xo == null) {
			setNullSelectedStatus();
		}
		if (xo instanceof TechnicsMessageTreeObject) {
			XWTreeNode node = frame.getTechnicsTreePanel().getSelectedTreeNode();
			System.out.println(node);
			setReworkTechnicsSelectedStatus();
		} else if (xo instanceof XWStepTreeObject) {
			setStepSelectedStatus(xo);
		} else if (xo instanceof XWPaceTreeObject) {
			setPaceSelectedStatus();
		} else if (xo instanceof XWPartTreeObject) {
			setPartSelectedStatus();
			XWPartTreeObject po = (XWPartTreeObject) xo;
		} else if (xo instanceof XWProductTreeObject) {
			setProductSelectedStatus();
		}


	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == reviewTechnics) {
			try {
				frame.reviewTechnicsThread(treePanel.getSelectedTreeNode());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "预览工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (event.getSource() == pdfReviewTechnics) {
			try {
				frame.pdfReviewTechnicsThread(treePanel.getSelectedTreeNode());
			} catch (Exception e) {

				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "预览工艺出现错误！", "提示",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (event.getSource() == submitUniteTechnics) {
			frame.submitUniteTechnics();
		} else if (event.getSource() == technicsCoWork) {
			frame.technicsCoWork();
		} else if (event.getSource() == technicsConfirm) {
			frame.technicsRouteConfirm();
		} else if (event.getSource() == technicsUpload) {
//			frame.newTechnicsUploadThread();
			frame.technicsUploadThread();
		} else if (event.getSource() == technicsUpdate) {
			try {
				frame.technicsUpdate();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "工艺更新时出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
				e1.printStackTrace();
			}
		}else if(event.getSource() == relateMainMakeProcessPlan){
			//关联主制工艺原逻辑
//			XWTreeNode node = treePanel.getSelectedTreeNode();
//			Element technicsElement = null;
//			if (node != null) {
//				XWTreeObject xo = node.getObject();
//				if (xo instanceof TechnicsMessageTreeObject) {
//					TechnicsMessageTreeObject xto = (TechnicsMessageTreeObject) xo;
//					technicsElement = (Element) xto.getTreeCellData();
//				}
//			}
//
//			Technics technics = new Technics();
//			MainMakeTechnicSearchDialog typicalTechnicSearchDialog = new MainMakeTechnicSearchDialog(frame, technics,technicsElement);
//			technics = typicalTechnicSearchDialog.showDialog();

			//关联主制工艺现逻辑
			XWTreeNode node = treePanel.getSelectedTreeNode();
			Element technicsElement = null;
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if (xo instanceof TechnicsMessageTreeObject) {
					TechnicsMessageTreeObject xto = (TechnicsMessageTreeObject) xo;
					technicsElement = (Element) xto.getTreeCellData();
				}
			}

			if (technicsElement != null) {
				boolean flag = true;
				String version = technicsElement.attributeValue("version");
				String lifecycle = technicsElement.attributeValue("lifecycle");
				String docNumber = technicsElement.attributeValue("technicsNumber");
				if ((lifecycle != null && (!"".equals(lifecycle)) && !lifecycle.equals("正在工作")
						&& !lifecycle.equals("修改中")) || !version.startsWith("space")) {
					try {
						boolean isHasChangeOrder = TechnicsIntf.isHasChangeOrder(docNumber);
						if(isHasChangeOrder){
							flag = false;
						}else{
							flag = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				try {
					String result = TechnicsIntf.checkDocExitAndModifier(technicsElement.attributeValue("technicsNumber"));
					if (!"EXCEPTION".equals(result) && !"NOTEXIST".equals(result)) {
						if(flag) {
							flag = NewTechnicsPart.currentUser.equals(result) ? true : false;
						}
						new AssociatedZZPlanDialog(technicsElement, frame, flag);
					} else {
						JOptionPane.showMessageDialog(frame, "该工艺文件为初次创建且未上载，请先上载！", "错误", JOptionPane.WARNING_MESSAGE);
					}
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame, "校验系统是否存在该工艺文档出错", "系统异常", JOptionPane.WARNING_MESSAGE);
				}
			}
		} else if (event.getSource() == signed) {
			frame.commitForSignedThread("3");
		} else if (event.getSource() == signed2) {
			frame.commitForSignedThread("5");
		} else if (event.getSource() == createTaskItem) {
			XWTreeNode node = treePanel.getSelectedTreeNode();
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if (xo instanceof TechnicsMessageTreeObject) {
					TechnicsMessageTreeObject xto = (TechnicsMessageTreeObject) xo;
					Document tech = xto.getTechnicsDocument();
					Element technicsElement = XmlUtility.getTechnicsElement(tech);
					String zfFlag = technicsElement.attributeValue("ZFFLAG");
					if(!"Z".equals(zfFlag)) {
						JOptionPane.showMessageDialog(frame, "只有主制工艺文件才能提交辅制工艺任务！", "提示", JOptionPane.INFORMATION_MESSAGE);
					} else {
						new CreateTaskItemDialog(frame,technicsElement);
					}
				} else {
					JOptionPane.showMessageDialog(this, frame.ERROR1);
				}
			} else {
				JOptionPane.showMessageDialog(this, frame.ERROR1);
			}
		}else if(event.getSource() == relateAidedProcessPlan){
			// 主制关联辅制
			XWTreeNode node = treePanel.getSelectedTreeNode();
			Element technicsElement = null;
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if (xo instanceof TechnicsMessageTreeObject) {
					TechnicsMessageTreeObject xto = (TechnicsMessageTreeObject) xo;
					technicsElement = (Element) xto.getTreeCellData();
				}
			}

			try {
				boolean flag = true;
				String result = TechnicsIntf.checkDocExitAndModifier(technicsElement.attributeValue("technicsNumber"));
				if (!"EXCEPTION".equals(result) && !"NOTEXIST".equals(result)) {
					flag = NewTechnicsPart.currentUser.equals(result) ? true : false;
					new AssociatedFZPlanDialog(technicsElement, frame, flag);
				} else {
					JOptionPane.showMessageDialog(frame, "该工艺文件为初次创建且未上载，请先上载！", "错误", JOptionPane.WARNING_MESSAGE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "校验系统是否存在该工艺文档出错", "系统异常", JOptionPane.WARNING_MESSAGE);
			}
		} else if (event.getSource() == completeTask) {
			frame.completeTechnicsTask();
		} else if (event.getSource() == viewHistory) {
			frame.qmViewHistorytechnics();
		} else if (event.getSource() == copy) {
			try {
				copyTechnics();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "粘贴工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (event.getSource() == delete) {
			try {
				XWTreeNode node = frame.xwPartTreePanel.getSelectedTreeNode();
				XWTreeObject obj = node.getObject();
				if(obj instanceof ReportTechnicsTreeObject) {
					frame.deleteReportTechnicsNode(node);
				} else {
					frame.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "删除操作出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}else if (event.getSource() ==jishuxieyi) {
			XWTreeNode node = frame.xwPartTreePanel.getSelectedTreeNode();
			XWTreeObject obj = node.getObject();
			if (obj instanceof TechnicsMessageTreeObject) {
				TechnicsMessageTreeObject xto = (TechnicsMessageTreeObject) obj;
				Document tech = xto.getTechnicsDocument();
				Element element = XmlUtility.getTechnicsElement(tech);
				CreateJishuxieyiDialog jishuxieyiDialog = new CreateJishuxieyiDialog(frame, element, node);
//				frame.xwPartTreePanel.addReportTechnicsNode(node, technicsNumber);
				String jsxyNumber = CreateJishuxieyiDialog.getJsxyNumber();
				File file = CreateJishuxieyiDialog.getFile();
				if ((!"".equals(jsxyNumber)&&!"null".equals(jsxyNumber)&&jsxyNumber!=null)) {
					Map<String,String> map = (Map) IntfUtil.getPeRemoteMethodInvoke("getJsxyNameAndVersionByNumber",
        						new Class[] { String.class }, new Object[] {jsxyNumber });

					if (!CreateJishuxieyiDialog.CreateFlag) {
                        return;
                    }
					String name = map.get("name");
					String version = map.get("version");
					JsxyDocTreeObject jsxyDoc1=new JsxyDocTreeObject(jsxyNumber,name, version);
//					this.frame.xwPartTreePanel.addJsxyDocNode(node, jsxyDoc1);
					if (node != null) {
                        XWTreeNode tnode = new XWTreeNode(jsxyDoc1);
                        node.add(tnode);
                        XWTreeNode node1 = (XWTreeNode)this.treePanel.getTree().getLastSelectedPathComponent();
                        if(!node1.isLeaf()){
                            this.treePanel.getTree().collapsePath(new TreePath(((DefaultTreeModel) this.treePanel.getTree().getModel()).getPathToRoot(node)));
                        }
                         this.treePanel.getTree().expandPath(new TreePath(( (TreePath) this.treePanel.getTree().getSelectionPath()).getPath()));
                         this.treePanel.getTree().updateUI();
                }
					CreateJishuxieyiDialog.setJsxyNumber("");
				}else if (!"".equals(file)&&!"null".equals(file)&&file!=null) {
					String flag = CreateJishuxieyiDialog.flag;
					if (flag==null) {
						return;
					}
					JsxyDocTreeObject jsxyDoc=new JsxyDocTreeObject(flag,CreateJishuxieyiDialog.getJSxyName(), "space.1");
					if (node != null) {
						XWTreeNode tnode = new XWTreeNode(jsxyDoc);
						node.add(tnode);
						XWTreeNode node1 = (XWTreeNode)this.treePanel.getTree().getLastSelectedPathComponent();
		                if(!node1.isLeaf()){
		                    this.treePanel.getTree().collapsePath(new TreePath(((DefaultTreeModel) this.treePanel.getTree().getModel()).getPathToRoot(node)));
		                }
						 this.treePanel.getTree().expandPath(new TreePath(( (TreePath) this.treePanel.getTree().getSelectionPath()).getPath()));
						 this.treePanel.getTree().updateUI();
				}
					CreateJishuxieyiDialog.setFile(null);

				}

			}
			XWTreeNode selectNode = frame.xwPartTreePanel.getSelectedTreeNode();
			frame.xwPartTreePanel.updateUI();
			selectNode.expandAll();



			}else if (event.getSource() == editNumber) {
			    XWTreeNode node = frame.xwPartTreePanel.getSelectedTreeNode();
			    EditTechnicsNumberDialog dialog = new EditTechnicsNumberDialog(frame, node);
			    String text = dialog.getValue().getText();
			    if ("".equals(text.trim())) {
			        JOptionPane.showMessageDialog(this.frame, "请填写工艺文件编号");
			        return;
                }
			    XWTreeObject object = node.getObject();
			    if (object instanceof TechnicsMessageTreeObject) {
			        TechnicsMessageTreeObject obj =(TechnicsMessageTreeObject) object;
			        Element data = obj.getTreeCellData();
			        String technicsNumber = data.attributeValue("technicsNumber");
			        String pplanName= data.attributeValue("pplanName");
			        Document document = WorkSpaceUtil.getTechnicsDocumentByTechnicsNumber(technicsNumber);
			        Element techElement = XmlUtility.getTechnicsElement(document);
			        String number = data.attributeValue("pplanNumber");
			        if (number.equals(text)) {
			            JOptionPane.showMessageDialog(this.frame, "您未改动工艺文件编号");
	                    return;
                    }else{
                        XmlUtility.setAttributeValue(techElement, "pplanNumber", text);
                        XmlUtility.setAttributeValue(techElement, "technicsName", pplanName+"("+text+")");

                        ((NewTechnicsPart)frame).saveProcess(techElement);
                        frame.technicsTreePanel.hangTechnicsDocument(document);
                        Boolean flag=(Boolean) IntfUtil.getPeRemoteMethodInvoke("setTechnicsNumberBynumber",
                                new Class[] { String.class,String.class,String.class }, new Object[] {technicsNumber,pplanName,text});
                        Boolean flag1=(Boolean) IntfUtil.getPeRemoteMethodInvoke("setMPMPROCESSNameBynumber",
                                new Class[] { String.class,String.class,String.class }, new Object[] {technicsNumber,pplanName,text});
                    }
                }
            }else if (event.getSource() == copyTechnicsFromTemlate) {
				XWTreeNode node = frame.xwPartTreePanel.getSelectedTreeNode();
				XWTreeObject obj = node.getObject();
				if (obj instanceof TechnicsMessageTreeObject) {
					TechnicsMessageTreeObject xto = (TechnicsMessageTreeObject) obj;
					Document tech = xto.getTechnicsDocument();
					TemplateSearchDialogForCopyTechnics dialog = new TemplateSearchDialogForCopyTechnics(tech,frame);
					dialog.showDialog();
				}

			} else if (event.getSource() == controlExternalProcess) {
				int reslut = JOptionPane.showConfirmDialog(this, "是否一键受控？", "提示", 0);
		        if (reslut == 0) {
		        	frame.commitControlExternalProcess();
		        }
				
			}
	}

	private void copyTechnics() {
		XWTreeNode node = this.treePanel.getSelectedTreeNode();
		if(node != null) {
			XWTreeObject object = node.getObject();
			if(object instanceof TechnicsMessageTreeObject) {
				TechnicsMessageTreeObject tmTreeObject = (TechnicsMessageTreeObject)object;
				frame.copyTechNumber = tmTreeObject.getTechNumber();
				JOptionPane.showMessageDialog(this.frame, "已复制工艺文件："+tmTreeObject.getPplanNumber());
			} else {
				JOptionPane.showMessageDialog(this.frame, "该操作需要针对工艺进行，产品树中无选中工艺节点！");
			}
		} else {
			JOptionPane.showMessageDialog(this.frame, "该操作需要针对工艺进行，产品树中无选中工艺节点！");
		}
	}

	private void setTechnicsSelectedStatus(XWTreeObject node) {
//		XWTreeNode node1 = frame.getTechnicsTreePanel().getSelectedTreeNode();
//		node1.setUsed(true);
		jishuxieyi.setEnabled(false);
		technicsCoWork.setEnabled(true);
		technicsConfirm.setEnabled(true);
		reviewTechnics.setEnabled(true);
		technicsUpload.setEnabled(true);
		if(node!=null){
			if( "F".equals(node.getTreeCellData().attributeValue("ZFFLAG"))){
				relateMainMakeProcessPlan.setEnabled(true);
				relateAidedProcessPlan.setEnabled(false);
			}else{
				relateMainMakeProcessPlan.setEnabled(false);
				relateAidedProcessPlan.setEnabled(true);
			}
		}

		technicsUpdate.setEnabled(true);
		editNumber.setEnabled(true);
		delete.setEnabled(true);
		copyTechnicsFromTemlate.setEnabled(true);

		logger.debug("isDownload= " + NewTechnicsPart.isDownload);
		if (NewTechnicsPart.isDownload) {
			signed.setEnabled(false);
			signed2.setEnabled(false);
			controlExternalProcess.setEnabled(false);
			completeTask.setEnabled(true);
		} else {
			Element currentElement =  node.getTreeCellData();
			signed.setEnabled( AclUtl.sign3Enable(currentElement));
			signed2.setEnabled(true);
			controlExternalProcess.setEnabled(true);
			completeTask.setEnabled(false);
		}
		viewHistory.setEnabled(true);
		submitUniteTechnics.setEnabled(true);
		if (node == null) {
			submitUniteTechnics.setEnabled(false);
		}
		createTaskItem.setEnabled(true);
		Document tech = frame.getCurrentTechnics();
		if (tech != null) {
			try {
				Element ele = XmlUtility.getTechnicsElement(tech);
				String lifecycle = ele.attributeValue("lifecycle");
				if (lifecycle != null && (!"".equals(lifecycle))
						&& !lifecycle.equals("正在工作") && !lifecycle.equals("修改中")) {
					technicsCoWork.setEnabled(false);
					submitUniteTechnics.setEnabled(false);
					technicsConfirm.setEnabled(false);
					technicsUpload.setEnabled(false);
					delete.setEnabled(false);
					technicsUpdate.setEnabled(false);
					signed.setEnabled(false);
					signed2.setEnabled(false);
					controlExternalProcess.setEnabled(false);
					completeTask.setEnabled(false);
					//createTaskItem.setEnabled(false);
					editNumber.setEnabled(false);
					copyTechnicsFromTemlate.setEnabled(false);
				}

				String creator = ele.attributeValue("creator");
				if(!creator.equals(NewTechnicsPart.currentUser)) {
				    editNumber.setEnabled(false);
					technicsCoWork.setEnabled(false);
					submitUniteTechnics.setEnabled(false);
					technicsConfirm.setEnabled(false);
					technicsUpload.setEnabled(false);
					delete.setEnabled(false);
					technicsUpdate.setEnabled(false);
					signed.setEnabled(false);
					signed2.setEnabled(false);
					controlExternalProcess.setEnabled(false);
					completeTask.setEnabled(false);
					createTaskItem.setEnabled(false);
					copyTechnicsFromTemlate.setEnabled(false);
				}

				XWTreeNode pNode = this.treePanel.getSelectedTreeNode().getP();
				XWPartTreeObject partObject = (XWPartTreeObject)pNode.getObject();
				if(!partObject.isAllowed()) {
					technicsUpload.setEnabled(false);
					delete.setEnabled(false);
					technicsUpdate.setEnabled(false);
					signed.setEnabled(false);
					signed2.setEnabled(false);
					controlExternalProcess.setEnabled(false);
					//createTaskItem.setEnabled(false);
				}
				if (node!=null) {

					Element treeCellData = node.getTreeCellData();
					String number = treeCellData.attributeValue("technicsNumber");
					String version = treeCellData.attributeValue("version");
					String isTabular = treeCellData.attributeValue("isTabular");
					//Boolean docRefStyle = (Boolean) IntfUtil.getPeRemoteMethodInvoke("getDocRefStyle",
					//		new Class[] { String.class,String.class }, new Object[] { number,version });
					if ("外协".equals(isTabular)/*&&docRefStyle*/) {
						jishuxieyi.setEnabled(true);
					}
				}


			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setReportTechnicsSelectedStatus(XWTreeObject node) {
	    editNumber.setEnabled(false);
	    jishuxieyi.setEnabled(false);
		pdfReviewTechnics.setEnabled(true);
		technicsCoWork.setEnabled(false);
		copy.setEnabled(false);
		technicsCoWork.setEnabled(false);
		technicsConfirm.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsUpload.setEnabled(true);
		delete.setEnabled(true);
		technicsUpdate.setEnabled(false);
		createTaskItem.setEnabled(false);
		logger.debug("isDownload= " + NewTechnicsPart.isDownload);
		if (NewTechnicsPart.isDownload) {
			signed.setEnabled(false);
			signed2.setEnabled(false);
			controlExternalProcess.setEnabled(false);
			completeTask.setEnabled(false);
		} else {
			signed.setEnabled(false);
			signed2.setEnabled(true);
			controlExternalProcess.setEnabled(true);
			completeTask.setEnabled(false);
		}
		viewHistory.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		if (node == null) {
			submitUniteTechnics.setEnabled(false);
		}
		//createTaskItem.setEnabled(false);
//		Document tech = frame.getCurrentTechnics();
		XWTreeNode treeNode = frame.xwPartTreePanel.getSelectedTreeNode();
		if (treeNode != null) {
			try {
			    Element ele =treeNode.getObject().getTreeCellData();
//				Element ele = XmlUtility.getTechnicsElement(tech);
				String lifecycle = ele.attributeValue("lifecycle");
				if (lifecycle != null && (!"".equals(lifecycle))
						&& !lifecycle.equals("正在工作") && !lifecycle.equals("修改中")) {
					technicsCoWork.setEnabled(false);
					submitUniteTechnics.setEnabled(false);
					technicsConfirm.setEnabled(false);
					technicsUpload.setEnabled(false);
					delete.setEnabled(false);
					technicsUpdate.setEnabled(false);
					signed.setEnabled(false);
					signed2.setEnabled(false);
					controlExternalProcess.setEnabled(false);
					completeTask.setEnabled(false);
					//createTaskItem.setEnabled(false);
				}

				String creator = ele.attributeValue("creator");
				if(!creator.equals(NewTechnicsPart.currentUser)) {
					technicsCoWork.setEnabled(false);
					submitUniteTechnics.setEnabled(false);
					technicsConfirm.setEnabled(false);
					technicsUpload.setEnabled(false);
					delete.setEnabled(false);
					technicsUpdate.setEnabled(false);
					signed.setEnabled(false);
					signed2.setEnabled(false);
					controlExternalProcess.setEnabled(false);
					completeTask.setEnabled(false);
					createTaskItem.setEnabled(false);
				}

				XWTreeNode pNode = this.treePanel.getSelectedTreeNode().getP();
				XWPartTreeObject partObject = (XWPartTreeObject)pNode.getObject();
				if(!partObject.isAllowed()) {
					technicsUpload.setEnabled(false);
					delete.setEnabled(false);
					technicsUpdate.setEnabled(false);
					signed.setEnabled(false);
					signed2.setEnabled(false);
					controlExternalProcess.setEnabled(false);
					//createTaskItem.setEnabled(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setReworkTechnicsSelectedStatus() {
		technicsCoWork.setEnabled(false);
		technicsConfirm.setEnabled(false);
		reviewTechnics.setEnabled(true);
		technicsUpload.setEnabled(true);
		delete.setEnabled(true);
		technicsUpdate.setEnabled(true);
		logger.debug("isDownload= " + NewTechnicsPart.isDownload);
		Document tech = frame.getCurrentTechnics();
		if (NewTechnicsPart.isDownload) {
			signed.setEnabled(false);
			signed2.setEnabled(false);
			controlExternalProcess.setEnabled(false);
			completeTask.setEnabled(true);
		} else {
			if (tech != null) {
				signed.setEnabled( AclUtl.sign3Enable(XmlUtility.getTechnicsElement(tech)));
			}else{
				signed.setEnabled(true);
			}
			signed2.setEnabled(true);
			controlExternalProcess.setEnabled(true);
			completeTask.setEnabled(false);
		}
		viewHistory.setEnabled(true);
		submitUniteTechnics.setEnabled(false);
		createTaskItem.setEnabled(true);

		if (tech != null) {
			try {
				Element ele = XmlUtility.getTechnicsElement(tech);
				String lifecycle = ele.attributeValue("lifecycle");
				if (lifecycle != null && (!"".equals(lifecycle))
						&& !lifecycle.equals("正在工作") && !lifecycle.equals("修改中")) {
					technicsCoWork.setEnabled(false);
					submitUniteTechnics.setEnabled(false);
					technicsConfirm.setEnabled(false);
					technicsUpload.setEnabled(false);
					delete.setEnabled(false);
					technicsUpdate.setEnabled(false);
					signed.setEnabled(false);
					signed2.setEnabled(false);
					controlExternalProcess.setEnabled(false);
					completeTask.setEnabled(false);
					//createTaskItem.setEnabled(false);
				}

				String creator = ele.attributeValue("creator");
				if(!creator.equals(NewTechnicsPart.currentUser)) {
					technicsCoWork.setEnabled(false);
					submitUniteTechnics.setEnabled(false);
					technicsConfirm.setEnabled(false);
					technicsUpload.setEnabled(false);
					delete.setEnabled(false);
					technicsUpdate.setEnabled(false);
					signed.setEnabled(false);
					signed2.setEnabled(false);
					controlExternalProcess.setEnabled(false);
					completeTask.setEnabled(false);
					createTaskItem.setEnabled(false);
				}

				XWTreeNode pNode = this.treePanel.getSelectedTreeNode().getP();
				XWPartTreeObject partObject = (XWPartTreeObject)pNode.getObject();
				if(!partObject.isAllowed()) {
					technicsUpload.setEnabled(false);
					delete.setEnabled(false);
					technicsUpdate.setEnabled(false);
					signed.setEnabled(false);
					signed2.setEnabled(false);
					controlExternalProcess.setEnabled(false);
					//createTaskItem.setEnabled(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setStepSelectedStatus(XWTreeObject node) {
	    editNumber.setEnabled(false);
		technicsCoWork.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		technicsConfirm.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsUpload.setEnabled(false);
		delete.setEnabled(false);
		technicsUpdate.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		controlExternalProcess.setEnabled(false);
		completeTask.setEnabled(false);
		viewHistory.setEnabled(false);
		//createTaskItem.setEnabled(false);
	}

	private void setPaceSelectedStatus() {
	    editNumber.setEnabled(false);
		technicsCoWork.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		technicsConfirm.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsUpload.setEnabled(false);
		delete.setEnabled(false);
		technicsUpdate.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		controlExternalProcess.setEnabled(false);
		completeTask.setEnabled(false);
		viewHistory.setEnabled(false);
		//createTaskItem.setEnabled(false);
	}

	private void setPartSelectedStatus() {
	    editNumber.setEnabled(false);
		technicsCoWork.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		technicsConfirm.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsUpload.setEnabled(false);
		delete.setEnabled(false);
		technicsUpdate.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		controlExternalProcess.setEnabled(false);
		completeTask.setEnabled(false);
		viewHistory.setEnabled(false);
		//createTaskItem.setEnabled(false);
	}

	private void setProductSelectedStatus() {
	    editNumber.setEnabled(false);
		technicsCoWork.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		technicsConfirm.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsUpload.setEnabled(false);
		delete.setEnabled(false);
		technicsUpdate.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		controlExternalProcess.setEnabled(false);
		completeTask.setEnabled(false);
		viewHistory.setEnabled(false);
		//createTaskItem.setEnabled(false);
	}

	private void setNullSelectedStatus() {
		technicsCoWork.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		technicsConfirm.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsUpload.setEnabled(false);
		delete.setEnabled(false);
		technicsUpdate.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		controlExternalProcess.setEnabled(false);
		completeTask.setEnabled(false);
		viewHistory.setEnabled(false);
		createTaskItem.setEnabled(false);
	}
}