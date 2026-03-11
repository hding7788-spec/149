package com.glaway.mpm.view;

import com.glaway.mpm.util.IconUtil;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PartTreePopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JMenuItem reviewTechnics = new JMenuItem("工艺预览");
	private JMenuItem autoCreateProcedure = new JMenuItem("自动生成工序工步");
	private JMenuItem refresh = new JMenuItem("刷新");
	private JMenuItem expandAll = new JMenuItem("全部展开");
	private JMenuItem copyPartName = new JMenuItem("复制零件名称");
	private JMenuItem paste = new JMenuItem("粘贴");
	private JMenuItem createTechnics = new JMenuItem("新建工艺");
	private JMenuItem createSOPTechnics = new JMenuItem("新建SOP");
	private JMenuItem createReworkTechnics = new JMenuItem("新建返工工艺");
	private JMenuItem createReportTechnics = new JMenuItem("新建报表类工艺");
	private JMenuItem createChangeMarkTechnics = new JMenuItem("新建转阶段工艺");
	private JMenuItem createTempTechnics = new JMenuItem("新建临时工艺");
	private JMenuItem createStep = new JMenuItem("新建工序");
	private JMenuItem createPace = new JMenuItem("新建工步");
	private JMenuItem delete = new JMenuItem("删除");
	private JMenuItem startPBOM = new JMenuItem("启动BOM编辑器");
	private JMenuItem downLoadTechnics = new JMenuItem("下载工艺规程");
	private JMenuItem showBatchs = new JMenuItem("查看PBOM批次");
	private JMenuItem changeParentPart = new JMenuItem("切换PBOM父节点");
	private JMenuItem showVersions = new JMenuItem("选择阶段/批次切换显示PBOM");
	private JMenuItem batchCldeSubmit = new JMenuItem("提交材料定额签审流程");
	private JMenuItem batchFzTaskSubmit = new JMenuItem("批量提交辅制工艺任务");
	private JMenuItem batchSigned3TaskSubmit = new JMenuItem("批量提交三级工艺签审");
	private JMenuItem batchSigned5TaskSubmit = new JMenuItem("批量提交五级工艺签审");
	private JMenuItem synchUpdate = new JMenuItem("同步更新工艺文件属性");
	private NewTechnicsPart frame;
	private XWPartTreePanel treePanel;

	public PartTreePopupMenu(NewTechnicsPart frame, XWPartTreePanel treePanel) {
		super();

		this.frame = frame;
		this.treePanel = treePanel;
		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);
		if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			createTechnics.addActionListener(this);
			add(createTechnics);

			createReportTechnics.addActionListener(this);
			add(createReportTechnics);

			createChangeMarkTechnics.addActionListener(this);
			add(createChangeMarkTechnics);

			//showBatchs.addActionListener(this);
			//add(showBatchs);

			showVersions.addActionListener(this);
			add(showVersions);

			changeParentPart.addActionListener(this);
			add(changeParentPart);

			batchCldeSubmit.addActionListener(this);
			add(batchCldeSubmit);

			batchFzTaskSubmit.addActionListener(this);
			add(batchFzTaskSubmit);

			batchSigned3TaskSubmit.addActionListener(this);
			add(batchSigned3TaskSubmit);

			batchSigned5TaskSubmit.addActionListener(this);
			add(batchSigned5TaskSubmit);

//			createReworkTechnics.addActionListener(this);
//			add(createReworkTechnics);
	//
//			createTempTechnics.addActionListener(this);
//			add(createTempTechnics);

			//downLoadTechnics.addActionListener(this);
			//add(downLoadTechnics);

			addSeparator();
			paste.addActionListener(this);
			add(paste);

			addSeparator();
			copyPartName.addActionListener(this);
			add(copyPartName);

			addSeparator();
			//startPBOM.addActionListener(this);
			//add(startPBOM);
			//addSeparator();
			expandAll.addActionListener(this);
			add(expandAll);

			addSeparator();
			synchUpdate.addActionListener(this);
			add(synchUpdate);
		}else{
			/**新建sop*/
			createSOPTechnics.addActionListener(this);
			add(createSOPTechnics);

			addSeparator();
			synchUpdate.addActionListener(this);
			add(synchUpdate);
		}

		initMenuItemIcon();
	}

	// 设置菜单项图标
	private void initMenuItemIcon() {
		copyPartName.setIcon(IconUtil.getImageIcon(IconUtil.COPY));
		paste.setIcon(IconUtil.getImageIcon(IconUtil.PASTE));
		delete.setIcon(IconUtil.getImageIcon(IconUtil.DELETE));
		autoCreateProcedure.setIcon(IconUtil.getImageIcon(IconUtil.AUTO_CREATE));

		createTechnics.setIcon(IconUtil.getImageIcon(IconUtil.NEW_TECHNICS));
		createSOPTechnics.setIcon(IconUtil.getImageIcon(IconUtil.NEW_TECHNICS));
		createReportTechnics.setIcon(IconUtil.getImageIcon(IconUtil.NEW_TECHNICS));
		createChangeMarkTechnics.setIcon(IconUtil.getImageIcon(IconUtil.NEW_TECHNICS));

		showBatchs.setIcon(IconUtil.getImageIcon(IconUtil.NORMALPART));
		showVersions.setIcon(IconUtil.getImageIcon(IconUtil.NORMALPART));
		changeParentPart.setIcon(IconUtil.getImageIcon(IconUtil.NORMALPART));

		createReworkTechnics.setIcon(IconUtil.getImageIcon(IconUtil.NEW_REWORKTECHNICS));

		createTempTechnics.setIcon(IconUtil.getImageIcon(IconUtil.NEW_TEMPTECHNICS));

		createStep.setIcon(IconUtil.getImageIcon(IconUtil.NEW_STEP));
		createPace.setIcon(IconUtil.getImageIcon(IconUtil.NEW_PACE));
		reviewTechnics.setIcon(IconUtil.getImageIcon(IconUtil.PREVIEW));

		batchCldeSubmit.setIcon(IconUtil.getImageIcon(IconUtil.PLANCOMPLETE));
		batchFzTaskSubmit.setIcon(IconUtil.getImageIcon(IconUtil.PLANCOMPLETE));
		batchSigned3TaskSubmit.setIcon(IconUtil.getImageIcon(IconUtil.PLANCOMPLETE));
		batchSigned5TaskSubmit.setIcon(IconUtil.getImageIcon(IconUtil.PLANCOMPLETE));
	}

	public void setShowBatchsMenuState(boolean b) {
		showBatchs.setEnabled(b);
		showVersions.setEnabled(b);
		changeParentPart.setEnabled(b);
	}

	public void setMenuState(XWTreeObject xo) {
		if (xo == null) {
			setNullSelectedStatus();
		}
		if (xo instanceof XWTechnicsTreeObject) {
			setTechnicsSelectedStatus(xo);
		} else if (xo instanceof XWStepTreeObject) {
			setStepSelectedStatus(xo);
		} else if (xo instanceof XWPaceTreeObject) {
			setPaceSelectedStatus();
		} else if (xo instanceof XWPartTreeObject) {
			setPartSelectedStatus();
			XWPartTreeObject po = (XWPartTreeObject) xo;
			String partNumber = po.getTreeCellData().attributeValue("partNumber");
			if(po.isAllowed()) {
				createTechnics.setEnabled(true);
				createReportTechnics.setEnabled(true);
				createChangeMarkTechnics.setEnabled(true);
				downLoadTechnics.setEnabled(true);
				if(frame.copyTechNumber != null) {
					paste.setEnabled(true);
				} else {
					paste.setEnabled(false);
				}
			} else {
				createTechnics.setEnabled(false);
				createReportTechnics.setEnabled(false);
				createChangeMarkTechnics.setEnabled(false);
				downLoadTechnics.setEnabled(false);
				paste.setEnabled(false);
			}

			if (partNumber.equals(frame.reworkPartNumber) && "4".equals(NewTechnicsPart.runType)) {
				createReworkTechnics.setEnabled(true);
				createTechnics.setEnabled(false);
				createReportTechnics.setEnabled(false);
				createChangeMarkTechnics.setEnabled(false);
			} else {
				createReworkTechnics.setEnabled(false);
			}

			if (partNumber.equals(frame.reworkPartNumber) && "5".equals(NewTechnicsPart.runType)) {
				createTempTechnics.setEnabled(true);
				createTechnics.setEnabled(false);
				createReportTechnics.setEnabled(false);
				createChangeMarkTechnics.setEnabled(false);
			} else {
				createTempTechnics.setEnabled(false);
			}

//			if (frame.getCopyElement() != null) {
//				paste.setEnabled(true);
//			}
		} else if (xo instanceof XWProductTreeObject) {
			setProductSelectedStatus();
		}



		if (!"".equals(frame.workItemOid)&&!"null".equals(frame.workItemOid)&&frame.workItemOid!=null) {

			boolean reportFlag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("getRenwuLeiXing",
					new Class[] { String.class }, new Object[] { frame.workItemOid });
			if (reportFlag) {
				if (xo instanceof XWPartTreeObject) {

					XWPartTreeObject po1 = (XWPartTreeObject) xo;
					if(po1.isAllowed()) {
						createReportTechnics.setEnabled(true);
						createTechnics.setEnabled(false);
						createChangeMarkTechnics.setEnabled(false);
					}else{
						createReportTechnics.setEnabled(false);
					}
				}
			}else{
				if(NewTechnicsPart.isXinZengGengGai){
					createReportTechnics.setEnabled(true);
				}else{
					createReportTechnics.setEnabled(false);
				}

				//createTechnics.setEnabled(true);
			}
		}else{
			createTechnics.setEnabled(true);
			createChangeMarkTechnics.setEnabled(true);
			if(NewTechnicsPart.isXinZengGengGai){
				createReportTechnics.setEnabled(true);
			}else{
				createReportTechnics.setEnabled(false);
			}
		}



	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == reviewTechnics) {
			try {
				frame.reviewTechnics(treePanel.getSelectedTreeNode());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "预览工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == downLoadTechnics) {
			try {
				frame.getOtherTechnics();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "下载工艺规程出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == copyPartName) {
			try {
				frame.getXWPartTreePanel().copyPartName();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "复制零件名称出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == paste) {
			try {
				//frame.pasteProductTechnics();
				//判断是否从工业任务活动页面启动的工艺编辑器，即workItemOid是否为空
				//如果为空，则不允许新建工艺文件
				if(frame.workItemOid == null || "".equals(frame.workItemOid) || "null".equals(frame.workItemOid)) {
					JOptionPane.showMessageDialog(this, "只有从工艺任务活动页面启动工艺编辑器时才允许新建工艺文件！");
					return;
				} else {
					frame.pastTechnics();
				}

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "粘贴工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == delete) {
			try {
				frame.delete();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "删除操作出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == autoCreateProcedure)// 自动生成工序工步
		{
			try {
				frame.autoCreateProcedure(treePanel.getSelectedTreeNode());
				treePanel.refreshSelectNode(true);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "自动创建工序出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == startPBOM) {
			frame.startPBOM(treePanel);
		}

		// if (event.getSource() == exportTechnics)// 工艺导入
		// {
		// try {
		// frame.exportTechnics();
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// JOptionPane.showMessageDialog(frame, "导入工艺出现错误！", "提示",
		// JOptionPane.INFORMATION_MESSAGE);
		// }
		// }
		//
		// if (event.getSource() == createZIP)// 工艺打包
		// {
		// try {
		// frame.createZIP(treePanel.getSelectedTreeNode());
		// } catch (Exception e) {
		//
		// e.printStackTrace();
		// JOptionPane.showMessageDialog(frame, "工艺打包出现错误！", "提示",
		// JOptionPane.INFORMATION_MESSAGE);
		// }
		// }

		if (event.getSource() == refresh)// 刷新
		{
			try {
				frame.refreshData(treePanel.getSelectedTreeNode());
				treePanel.refreshSelectNode(true);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "工艺刷新出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == expandAll)// 全部展开
		{
			try {
				//treePanel.expandAllNode(treePanel.getSelectedTreeNode());
				treePanel.expandAllNode2(treePanel.getSelectedTreeNode());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "工艺刷新出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == createTechnics)// 新建工艺
		{
			try {
				//判断是否从工业任务活动页面启动的工艺编辑器，即workItemOid是否为空
				//如果为空，则不允许新建工艺文件
				/*if(frame.workItemOid == null || "".equals(frame.workItemOid) || "null".equals(frame.workItemOid)) {
					JOptionPane.showMessageDialog(this, "只有从工艺任务活动页面启动工艺编辑器时才允许新建工艺文件！");
					return;
				} else {
					frame.createTechnics();
				}*/
				frame.createTechnics();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "创建工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}


		if (event.getSource() == createSOPTechnics)// 新建工艺
		{
			try {
				frame.createSOPTechnics();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "创建SOP出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}




        if(event.getSource() == createChangeMarkTechnics) {
			final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "数据加载", "数据加载中,请等待...", "数据加载中");
			final Thread thread = new Thread() {

                @Override
                public void run() {
		    	    frame.createChangeMarkTechnics(progressBar);
                    progressBar.finish();
                    progressBar.setVisible(false);
                }
			};

			thread.start();
			progressBar.setVisible(true);

        }
		if (event.getSource() == showBatchs)
		{
			try {
				new ShowPbomBatchsDialog(frame, treePanel.getSelectedTreeNode());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "查看PBOM批次信息出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == showVersions)// 新建工艺
		{
			try {
				XWTreeNode root = (XWTreeNode)treePanel.getSelectedTreeNode().getRoot();
				XWTreeNode selNode = treePanel.getSelectedTreeNode().getP();
				if(root.equals(selNode)) {
					new ShowPbomVersionsDialog(frame, treePanel.getSelectedTreeNode());
				} else {
					JOptionPane.showMessageDialog(frame, "请选择PBOM的顶层节点！", "提示", JOptionPane.INFORMATION_MESSAGE);
				}

			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "查看PBOM历史版本出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if(event.getSource() == changeParentPart){
			new ChangeParentPartDialog(frame);
		}
		if (event.getSource() == createReworkTechnics)// 新建返工工艺
		{
			try {
				frame.createReworkTechnics();
				System.out.println("createReworkTechnics");
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "创建返工工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == createTempTechnics)// 新建临时工艺
		{
			try {
				frame.createTempTechnics();
				System.out.println("createTempTechnics");
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "创建临时工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource() == createStep)// 新建工序
		{
			try {
				frame.createProdcutStep();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "创建工序出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == createPace)// 新建工步
		{
			try {
				frame.createProductPace();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "创建工步出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource() == batchCldeSubmit)// 提交材料定额签审流程
		{
			try {
				XWTreeNode selNode = treePanel.getSelectedTreeNode();
				new BatchCldeSubmitJDialog(frame,selNode);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "提交材料定额签审流程失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource() == batchFzTaskSubmit)// 批量提交辅制工艺任务
		{
			try {
				XWTreeNode selNode = treePanel.getSelectedTreeNode();
				new BatchFzTaskSubmitJDialog(frame,selNode);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "批量提交辅制工艺任务失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource() == batchSigned3TaskSubmit)// 批量提交三级工艺签审
		{
			try {
				XWTreeNode selNode = treePanel.getSelectedTreeNode();
				new BatchSignedTaskSubmitJDialog(frame,selNode,"3");
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "批量提交三级工艺签审失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource() == batchSigned5TaskSubmit)// 批量提交五级工艺签审
		{
			try {
				XWTreeNode selNode = treePanel.getSelectedTreeNode();
				new BatchSignedTaskSubmitJDialog(frame,selNode,"5");
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "批量提交五级工艺签审失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource() == createReportTechnics)// 新建报表类工艺文件
		{
			try {
				frame.createReportTechnics();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "新建报表类工艺文件出错！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource() == synchUpdate) {
			try {
				frame.getXWPartTreePanel().synchUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "同步更新工艺文件属性出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private void setTechnicsSelectedStatus(XWTreeObject node) {
		createTechnics.setEnabled(false);
		createReportTechnics.setEnabled(false);
		createChangeMarkTechnics.setEnabled(false);
		downLoadTechnics.setEnabled(false);
		createStep.setEnabled(true);
		createPace.setEnabled(false);
		if (node.getTreeCellData().attributeValue("technicsType").equals(WorkSpaceUtil.ASM_TYPE))
			autoCreateProcedure.setEnabled(true);
		else
			autoCreateProcedure.setEnabled(false);

		delete.setEnabled(true);
		refresh.setEnabled(true);
		expandAll.setEnabled(true);
		reviewTechnics.setEnabled(true);
		if (frame.getCopyElement() != null) {
			boolean sameType = node.getTreeCellData().attributeValue("technicsType").equals(frame.getTechnicsType());
			if (sameType) {
				paste.setEnabled(true);
			} else {
				paste.setEnabled(false);
			}
			paste.setEnabled(true);
		} else {
			paste.setEnabled(false);
		}
	}

	private void setStepSelectedStatus(XWTreeObject node) {
		String techType = "";
		try {
			techType = frame.getTechType(node.getTreeCellData());
		} catch (Exception e) {
			e.printStackTrace();
		}

		createTechnics.setEnabled(false);
		createChangeMarkTechnics.setEnabled(false);
		createReportTechnics.setEnabled(false);
		createStep.setEnabled(false);
		downLoadTechnics.setEnabled(false);
		createPace.setEnabled(true);
		autoCreateProcedure.setEnabled(false);
		delete.setEnabled(true);
		refresh.setEnabled(false);
		expandAll.setEnabled(false);
		paste.setEnabled(false);
		reviewTechnics.setEnabled(false);
	}

	private void setPaceSelectedStatus() {
		createTechnics.setEnabled(false);
		createChangeMarkTechnics.setEnabled(false);
		createReportTechnics.setEnabled(false);
		downLoadTechnics.setEnabled(false);
		createStep.setEnabled(false);
		createPace.setEnabled(false);
		autoCreateProcedure.setEnabled(false);
		delete.setEnabled(true);
		refresh.setEnabled(false);
		expandAll.setEnabled(false);
		paste.setEnabled(false);
		reviewTechnics.setEnabled(false);
	}

	private void setPartSelectedStatus() {
		downLoadTechnics.setEnabled(false);
		createStep.setEnabled(false);
		createPace.setEnabled(false);
		autoCreateProcedure.setEnabled(false);
		delete.setEnabled(false);
		refresh.setEnabled(false);
		expandAll.setEnabled(true);
		//paste.setEnabled(false);
		reviewTechnics.setEnabled(false);
	}

	private void setProductSelectedStatus() {
		createTechnics.setEnabled(false);
		createChangeMarkTechnics.setEnabled(false);
		createReportTechnics.setEnabled(false);
		downLoadTechnics.setEnabled(false);
		createStep.setEnabled(false);
		createPace.setEnabled(false);
		autoCreateProcedure.setEnabled(false);
		delete.setEnabled(false);
		refresh.setEnabled(false);
		expandAll.setEnabled(true);
		paste.setEnabled(false);
		reviewTechnics.setEnabled(false);
	}

	private void setNullSelectedStatus() {
		createTechnics.setEnabled(false);
		createChangeMarkTechnics.setEnabled(false);
		createReportTechnics.setEnabled(false);
		downLoadTechnics.setEnabled(false);
		createStep.setEnabled(false);
		createPace.setEnabled(false);
		autoCreateProcedure.setEnabled(false);
		delete.setEnabled(false);
		refresh.setEnabled(false);
		expandAll.setEnabled(false);
		paste.setEnabled(false);
		reviewTechnics.setEnabled(false);
	}
}