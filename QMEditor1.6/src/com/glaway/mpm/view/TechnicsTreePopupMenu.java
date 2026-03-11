package com.glaway.mpm.view;

import com.glaway.mpm.EditorConfig;
import com.glaway.mpm.erp.CMErpMenu;
import com.glaway.mpm.erp.GwTechnicsQuotaMenu;
import com.glaway.mpm.erp.pbom.CMPbomErpMenu;
import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.sjzyk.CMPbomSjzykMenu;
import com.glaway.mpm.task.CreateTaskItemDialog;
import com.glaway.mpm.util.*;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;

public class TechnicsTreePopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	private static VaLogger logger = VaLogger.getLogger(TechnicsTreePopupMenu.class);

	private JMenuItem insertStep = new JMenuItem("插入工序");
	private JMenuItem submitUniteTechnics = new JMenuItem("提交合编工艺");
	private JMenuItem reviewTechnics = new JMenuItem("工艺预览");
	private JMenuItem technicsUpload = new JMenuItem("工艺上载");
	private JMenuItem viewHistory = new JMenuItem("查看历史版本");
	private JMenuItem autoCreateProcedure = new JMenuItem("自动生成工序工步");
	// private JMenuItem quickCreateProcedure = new JMenuItem("工艺路线规划");
	private JMenuItem createProcedureFlow = new JMenuItem("生成工艺路线图");
	private JMenuItem viewAssemblage = new JMenuItem("可视化装配工具");
	// private JMenuItem dynamicAssemblagePicture = new JMenuItem("动态组装图");
	private JMenuItem technicsCoWork = new JMenuItem("工艺合编");
	private JMenuItem signed = new JMenuItem("提交三级工艺签审");
	private JMenuItem signed2 = new JMenuItem("提交五级工艺签审");
	private JMenuItem cmatsigned = new JMenuItem("提交材料消耗工艺定额明细表签审");
	private JMenuItem completeTask = new JMenuItem("完成工艺任务");
	private JMenuItem technicsUpdate = new JMenuItem("工艺更新");
	private JMenuItem technicsConfirm = new JMenuItem("工艺路线确认");
	// private JMenuItem eBomSignRed = new JMenuItem("EBOM标红");
	// private JMenuItem addCreo = new JMenuItem("添加Creo图形");
	private JMenuItem saveAsTechnicsTemplet = new JMenuItem("另存为工艺模板");
	private JMenuItem saveAsTechnicsParamTemplet = new JMenuItem("另存为工艺参数化模板");
//	private JMenuItem relateTypicalProcessPlan = new JMenuItem("关联典型工艺");
	private JMenuItem relateMainMakeProcessPlan = new JMenuItem("关联主制工艺");
	private JMenuItem relateAidedProcessPlan = new JMenuItem("关联辅制工艺");
	private JMenuItem saveAs = new JMenuItem("另存为");
	private JMenuItem refresh = new JMenuItem("刷新");
	private JMenuItem expandAll = new JMenuItem("全部展开");
	private JMenuItem runCreo = new JMenuItem("启动Creo");
	private JMenuItem run3D = new JMenuItem("启动Cortona3D");
	private JMenuItem copy = new JMenuItem("复制");
	private JMenuItem paste = new JMenuItem("粘贴");
	private JMenuItem createStep = new JMenuItem("新建工序");
	private JMenuItem gongzhuangshenqingdan = new JMenuItem("工装申请单");
	private JMenuItem importTechnicsTemplate = new JMenuItem("导入工艺模板");
	private JMenuItem refreshStepNumber = new JMenuItem("刷新工序号");
	private JMenuItem createPace = new JMenuItem("新建工步");
	private JMenuItem delete = new JMenuItem("删除");
	private JMenuItem createTaskItem = new JMenuItem("提交辅制工艺任务");

	// 根据信维二期需求，添加菜单项
	private JMenu run = new JMenu("启动工具");
	private JMenuItem runDiagram = new JMenuItem("工艺简图工具");
	private JMenuItem runMiddleModule = new JMenuItem("中间模型工具");
	private JMenuItem runVisual = new JMenuItem("可视化工具");
	private JMenuItem runAssembleCartoon = new JMenuItem("装配动画工具");
	private JMenuItem makeMiddleMode = new JMenuItem("制作中间模型");
	private JMenuItem createCartoon = new JMenuItem("添加装配动画");

	private JMenuItem showCheckOutTable = new JMenuItem("查看检验汇总表");

	//基于ERP的材料定额
	private CMErpMenu erpMenu = null;
	//基于ERP的工艺定额
	private	CMPbomErpMenu pbomErpMenu = null;
	//基于设计资源库的工艺定额
	private CMPbomSjzykMenu pbomSjzykMenu = null;
	//基于设计资源库的材料定额
//	private CMSjzykMenu sjzykMenu = null;

	//工艺定额
	private GwTechnicsQuotaMenu gwTechnicsQuotaMenu=null;

	private NewTechnicsPart frame;
	private TechnicsTreePanel treePanel;

	public TechnicsTreePopupMenu(NewTechnicsPart frame, TechnicsTreePanel treePanel) {
		super();
		this.frame = frame;
		this.treePanel = treePanel;

		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);

		//gongzhuangshenqingdan.addActionListener(this);
		//add(gongzhuangshenqingdan);
		createStep.addActionListener(this);
		importTechnicsTemplate.addActionListener(this);
		refreshStepNumber.addActionListener(this);
		// create.add(createStep);
		insertStep.addActionListener(this);
		add(createStep);
		copy.addActionListener(this);
		add(copy);
		paste.addActionListener(this);
		add(paste);
		delete.addActionListener(this);
		add(delete);
		refresh.addActionListener(this);
		add(refresh);
		expandAll.addActionListener(this);
		add(expandAll);
//		add(importTechnicsTemplate);
//		add(refreshStepNumber);

		addSeparator();// 分割线

		technicsUpload.addActionListener(this);
		add(technicsUpload);
		viewHistory.addActionListener(this);
		add(viewHistory);
		saveAsTechnicsTemplet.addActionListener(this);
		add(saveAsTechnicsTemplet);
		saveAsTechnicsParamTemplet.addActionListener(this);
		add(saveAsTechnicsParamTemplet);
//		relateTypicalProcessPlan.addActionListener(this);
//		add(relateTypicalProcessPlan);
		relateMainMakeProcessPlan.addActionListener(this);
		relateAidedProcessPlan.addActionListener(this);
		if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			add(relateMainMakeProcessPlan);
			add(relateAidedProcessPlan);
		}

		addSeparator();// 分割线

		createTaskItem.addActionListener(this);
		signed.addActionListener(this);
		if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			add(createTaskItem);
			add(signed);

		}
		signed2.addActionListener(this);
		add(signed2);

		//addSeparator();// 分割线

		// 信维二期添加菜单
		//add(run);
		//run.add(runDiagram);
		//run.add(runMiddleModule);
		//run.add(runVisual);
		//run.add(runAssembleCartoon);
		runDiagram.addActionListener(this);
		runMiddleModule.addActionListener(this);
		runVisual.addActionListener(this);
		runAssembleCartoon.addActionListener(this);
		createProcedureFlow.addActionListener(this);
		viewAssemblage.addActionListener(this);
		makeMiddleMode.addActionListener(this);
		//add(createCartoon);
		//createCartoon.addActionListener(this);
		showCheckOutTable.addActionListener(this);
		if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			add(makeMiddleMode);
			add(showCheckOutTable);
		}
//		add(technicsCoWork);
//		add(submitUniteTechnics);

//		add(technicsConfirm);
//		addSeparator();
//		add(reviewTechnics);
//		addSeparator();

		//add(technicsUpdate);

//		add(completeTask);

		technicsCoWork.addActionListener(this);
		reviewTechnics.addActionListener(this);
		technicsConfirm.addActionListener(this);
		submitUniteTechnics.addActionListener(this);
		technicsUpdate.addActionListener(this);
		completeTask.addActionListener(this);
//		addSeparator();// 分割线
//		autoCreateProcedure.addActionListener(this);
//		add(autoCreateProcedure);


		//cmatsigned.addActionListener(this);
		//add(cmatsigned);

		XWTreeNode selectedNode = this.treePanel.getCurrentTechnicsNode();

		//基于ERP的材料定额
		erpMenu = new CMErpMenu(this.frame,selectedNode);

		//基于ERP的工艺定额
		pbomErpMenu = new CMPbomErpMenu(selectedNode,this.frame);

		//基于设计资源库的工艺定额
		pbomSjzykMenu = new CMPbomSjzykMenu(selectedNode,this.frame);
		//工艺定额
		gwTechnicsQuotaMenu=new GwTechnicsQuotaMenu(this.frame, selectedNode);
		if(!"SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			addSeparator();// 分割线
			String users = NewTechnicsPart.currentUser;
			add(gwTechnicsQuotaMenu);
			if(EditorConfig.ACL_POSITIVE.contains(users) || !EditorConfig.isZS){
				add(erpMenu);
				add(pbomErpMenu);
				add(pbomSjzykMenu);
			}
		}
		//基于设计资源库的材料定额
//		sjzykMenu = new CMSjzykMenu(this.frame,selectedNode);
		//add(sjzykMenu);




		initMenuItemIcon();
	}

	// 设置菜单项图标
	private void initMenuItemIcon() {
		copy.setIcon(IconUtil.getImageIcon(IconUtil.COPY));
		paste.setIcon(IconUtil.getImageIcon(IconUtil.PASTE));
		delete.setIcon(IconUtil.getImageIcon(IconUtil.DELETE));
		createProcedureFlow.setIcon(IconUtil.getImageIcon(IconUtil.CREATE_PROCEDURE_FLOW));
		autoCreateProcedure.setIcon(IconUtil.getImageIcon(IconUtil.AUTO_CREATE));
		createStep.setIcon(IconUtil.getImageIcon(IconUtil.NEW_STEP));
		importTechnicsTemplate.setIcon(IconUtil.getImageIcon(IconUtil.IMPORT_TECHNICS_TEMPLATE));
		refreshStepNumber.setIcon(IconUtil.getImageIcon(IconUtil.REFRESH_STEP_NUMBER));
		createPace.setIcon(IconUtil.getImageIcon(IconUtil.NEW_PACE));
		reviewTechnics.setIcon(IconUtil.getImageIcon(IconUtil.PREVIEW));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == reviewTechnics) {
			try {
				frame.reviewTechnicsThread(treePanel.getSelectedTreeNode());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "预览工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == insertStep) {
			try {
				frame.insertProductStep(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (event.getSource() == submitUniteTechnics) {
			frame.submitUniteTechnics();
		}

		if(event.getSource() == createTaskItem) {
			XWTreeNode node = treePanel.getSelectedTreeNode();
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if (xo instanceof XWTechnicsTreeObject) {
					XWTechnicsTreeObject xto = (XWTechnicsTreeObject) xo;
					Element technicsElement = (Element) xto.getTreeCellData();
					String zfFlag = technicsElement.attributeValue("ZFFLAG");
					if("Z".equalsIgnoreCase(zfFlag)) {
						new CreateTaskItemDialog(frame,technicsElement);
					} else {
						JOptionPane.showMessageDialog(frame, "只有主制工艺文件才能提交辅制工艺任务！", "提示", JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(this, frame.ERROR1);
				}
			} else {
				JOptionPane.showMessageDialog(this, frame.ERROR1);
			}
		}

		if (event.getSource() == technicsUpload) {
			frame.technicsUploadThread();
//			frame.newTechnicsUploadThread();
		}

		if (event.getSource() == viewHistory) {
			frame.qmViewHistorytechnics();
		}

		if (event.getSource() == makeMiddleMode)// 零部件工艺制作中间模型
		{
			try {
				frame.makeMiddleMode();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (event.getSource() == createCartoon)// 添加动画
		{
			try {
				frame.addCartoon();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (event.getSource() == technicsCoWork) {
			frame.technicsCoWork();
		}

		if (event.getSource() == technicsConfirm) {
			frame.technicsRouteConfirm();
		}

		if (event.getSource() == technicsUpdate) {
			try {
				frame.technicsUpdate();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(frame, "工艺更新时出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
				e1.printStackTrace();
			}
		}
		if (event.getSource() == signed) {
			frame.commitForSignedThread("3");
		}
		if (event.getSource() == signed2) {
			frame.commitForSignedThread("5");
		}
		if (event.getSource() == cmatsigned) {
			Document tech = frame.getCurrentTechnics();
			if(tech != null) {
				Element technics = XmlUtility.getTechnicsElement(tech);
				if(technics != null) {
					String techNumber = technics.attributeValue("technicsNumber");
					String techState = technics.attributeValue("lifecycle");
					String state = "";
					try {
						state = TechnicsIntf.getCLDEState(techNumber);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if("已批准".equals(state)) {
						JOptionPane.showMessageDialog(frame, "材料定额已经批准，不能再提交签审！", "提示", JOptionPane.INFORMATION_MESSAGE);
					} else if (!"正在工作".equals(techState)) {
						JOptionPane.showMessageDialog(frame, "工艺文件已经提交签审，材料定额不能再单独提交签审！", "提示", JOptionPane.INFORMATION_MESSAGE);
					} else {
						frame.commitForCmatSignedThread();
					}
				} else {
					JOptionPane.showMessageDialog(frame, "工艺文件不存在！", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		if (event.getSource() == completeTask) {
			frame.completeTechnicsTask();
		}

		if (event.getSource() == copy) {
			try {
				//frame.copy();
				copyTechnics();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "复制工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == paste) {
			try {
				frame.pasteProductTechnics();
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
				XWTreeNode node = treePanel.getSelectedTreeNode();
				if (node != null) {
					XWTreeObject xo = node.getObject();
					if (xo instanceof XWTechnicsTreeObject) {
						frame.autoCreateProcedure(treePanel.getSelectedTreeNode());
					}
				}
			} catch (Exception e) {

				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "自动创建工序出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == createProcedureFlow)// 生成工艺路线图
		{
			try {
				frame.createTechnicsRoute();
			} catch (Exception e) {
				e.printStackTrace();
				//JOptionPane.showMessageDialog(frame, "生成工艺路线图出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == saveAsTechnicsTemplet)// 另存为模板
		{
			XWTreeNode node = treePanel.getSelectedTreeNode();
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if (xo instanceof XWTechnicsTreeObject) {
					XWTechnicsTreeObject xto = (XWTechnicsTreeObject) xo;
					Element technicsElement = (Element) xto.getTreeCellData();
					// String technicsNumber =
					// technicsElement.attributeValue("technicsNumber");
					if(NewTechnicsPart.isTemplateCapp){
						String lifecycle = technicsElement.attributeValue("lifecycle");
						if(!"已批准".equals(lifecycle)){
							JOptionPane.showMessageDialog(this, "只有已批准的工艺才能存为模板");
						}else{
							new SaveAsTechnicsTemplatJDialog(frame, technicsElement);
						}
					}else{
						new SaveAsTechnicsTemplatJDialog(frame, technicsElement);
					}

				} else {
					JOptionPane.showMessageDialog(this, frame.ERROR1);
				}
			} else {
				JOptionPane.showMessageDialog(this, frame.ERROR1);
			}
		}
		if (event.getSource() == saveAsTechnicsParamTemplet)// 另存为工艺参数化模板
		{
			XWTreeNode node = treePanel.getSelectedTreeNode();
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if (xo instanceof XWTechnicsTreeObject) {
					XWTechnicsTreeObject xto = (XWTechnicsTreeObject) xo;
					Element technicsElement = (Element) xto.getTreeCellData();
					if(NewTechnicsPart.isTemplateCapp){
						String lifecycle = technicsElement.attributeValue("lifecycle");
						if(!"已批准".equals(lifecycle)){
							JOptionPane.showMessageDialog(this, "只有已批准的工艺才能存为模板");
						}else{
							new SaveAsTechnicsParamTemplatJDialog(frame, technicsElement);
						}
					}else{
						new SaveAsTechnicsParamTemplatJDialog(frame, technicsElement);
					}

				} else {
					JOptionPane.showMessageDialog(this, frame.ERROR1);
				}
			} else {
				JOptionPane.showMessageDialog(this, frame.ERROR1);
			}
		}
//		if(event.getSource() == relateTypicalProcessPlan)//关联典型工艺
//		{
//			XWTreeNode node = treePanel.getSelectedTreeNode();
//			Element technicsElement = null;
//			if (node != null) {
//				XWTreeObject xo = node.getObject();
//				if (xo instanceof XWTechnicsTreeObject) {
//					XWTechnicsTreeObject xto = (XWTechnicsTreeObject) xo;
//					technicsElement = (Element) xto.getTreeCellData();
//				}
//			}
//
//			Technics technics = new Technics();
//			TypicalTechnicSearchDialog typicalTechnicSearchDialog = new TypicalTechnicSearchDialog(frame, technics,technicsElement);
//			technics = typicalTechnicSearchDialog.showDialog();
//
//		}
		if(event.getSource() == relateMainMakeProcessPlan)//关联主制工艺
		{
			XWTreeNode node = treePanel.getSelectedTreeNode();
			Element technicsElement = null;
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if (xo instanceof XWTechnicsTreeObject) {
					XWTechnicsTreeObject xto = (XWTechnicsTreeObject) xo;
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
			/*
			 * Technics technics = new Technics(); MainMakeTechnicSearchDialog
			 * typicalTechnicSearchDialog = new
			 * MainMakeTechnicSearchDialog(frame, technics,technicsElement);
			 * technics = typicalTechnicSearchDialog.showDialog();
			 */
		}
		if (event.getSource() == saveAs)// 另存为
		{

		}

		if (event.getSource() == refresh)// 刷新
		{
			try {
				frame.refreshData(treePanel.getSelectedTreeNode());
				treePanel.expandAllNode(treePanel.getSelectedTreeNode());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "工艺刷新出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == expandAll)// 工艺节点全部展开
		{
			try {
				treePanel.expandTechnicsNode(treePanel.getSelectedTreeNode());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "工艺刷新出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == runCreo)// 启动Creo
		{
			frame.runCreoProgram();
		}

		if (event.getSource() == run3D)// 启动3D
		{
			frame.run3DProgram();
		}

		// 信维二期添加菜单
		if (event.getSource() == runDiagram)// 启动3D
		{
			try {
				frame.runDiagramProgram();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "启动工艺简图工具出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == runMiddleModule)// 启动3D
		{
			try {
				frame.runMiddleModuleProgram();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "启动中间模型工具出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == runVisual)// 启动3D
		{
			try {
				frame.runVisualProgram();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "启动可视化工具出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == runAssembleCartoon)// 启动3D
		{
			try {
				frame.runAssembleCartoonProgram();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "启动装配动画工具出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

		if (event.getSource() == createStep)// 新建工序
		{
			try {
				frame.createProdcutStep();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "新建工序出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource() == importTechnicsTemplate)// 导入工艺模板
		{
			try {
				frame.importTechnicsTemplate();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "导入工艺模板出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource() == refreshStepNumber)// 刷新工序号
		{
			try {
				frame.reGenerateStepNumbers();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "刷新工序号出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource() == createPace)// 新建工步
		{
			try {
				frame.createProductPace();
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "新建工步出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		if (event.getSource()==gongzhuangshenqingdan) {//工装申请单
		    try {
                frame.CreateGongZhuangShenQinDan();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "新建工装申请单出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        }

		if (event.getSource()==showCheckOutTable) {//查看检验汇总表 add by 20180508 jyx
			Document tech = frame.getCurrentTechnics();
			if(tech != null) {
				Element technics = XmlUtility.getTechnicsElement(tech);
				if(technics != null) {
					String technicsNumber = technics.attributeValue("technicsNumber");
					try {
						String checkOutTableUrl = TechnicsIntf.getshowCheckOutTableUrl(technicsNumber);
						Runtime.getRuntime().exec("cmd.exe /c start " + checkOutTableUrl);
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(frame, "查看检验汇总表出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
					}
					System.out.println("");
				}
			}
		} else if(relateAidedProcessPlan == event.getSource()) {
			// 主制关联辅制
			XWTreeNode node = treePanel.getSelectedTreeNode();
			Element technicsElement = null;
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if (xo instanceof XWTechnicsTreeObject) {
					XWTechnicsTreeObject xto = (XWTechnicsTreeObject) xo;
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
		}
	}

	private void copyTechnics() {
		XWTreeNode node = this.treePanel.getSelectedTreeNode();
		if(node != null) {
			XWTreeObject object = node.getObject();
			if(object instanceof XWTechnicsTreeObject) {
				XWTechnicsTreeObject treeObject = (XWTechnicsTreeObject)object;
				Element technicsElement = treeObject.getTreeCellData();
				frame.copyTechNumber = technicsElement.attributeValue("technicsNumber");
				JOptionPane.showMessageDialog(this.frame, "已复制工艺文件："+technicsElement.attributeValue("pplanNumber"));
			} else {
				JOptionPane.showMessageDialog(this.frame, "该操作需要针对工艺进行，产品树中无选中工艺节点！");
			}
		} else {
			JOptionPane.showMessageDialog(this.frame, "该操作需要针对工艺进行，产品树中无选中工艺节点！");
		}
	}

	public void setMenuState(XWTreeObject xo) {
		if (xo == null) {
			setNullSelectedStatus();
		}
		if (xo instanceof XWTechnicsTreeObject) {
			boolean bool = false;
			Element techEle = xo.getTreeCellData();
			UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techEle);
			if (NewTechnicsPart.downLoadTech.contains(technics)) {
				bool = true;
			}
			if (bool) {
				setTechnicsSelectedStatus();
			} else {
				setTechnicsSelectedStatus(xo);
			}

			XWTreeNode pNode = frame.getXWPartTreePanel().getSelectedTreeNode().getP();
			XWTreeObject object = pNode.getObject();
			if(object instanceof XWPartTreeObject) {
				XWPartTreeObject pObj = (XWPartTreeObject)object;
				if(!pObj.isAllowed()) {
					setTechnicsSelectedStatus();
				}
			}

			copy.setEnabled(true);
		} else if (xo instanceof XWStepTreeObject) {
			setStepSelectedStatus(xo);
		} else if (xo instanceof XWPaceTreeObject) {
			setPaceSelectedStatus();
		} else if (xo instanceof XWPartTreeObject) {
			setPartSelectedStatus();
		} else if (xo instanceof XWProductTreeObject) {
			setProductSelectedStatus();
		}
	}

	private void setTechnicsSelectedStatus(XWTreeObject node) {
	    gongzhuangshenqingdan.setEnabled(true);
		createStep.setEnabled(true);
		importTechnicsTemplate.setEnabled(true);
		createPace.setEnabled(false);
		insertStep.setEnabled(false);
		if (node.getTreeCellData().attributeValue("technicsType").equals(WorkSpaceUtil.ASM_TYPE)) {
			autoCreateProcedure.setEnabled(true);
			createCartoon.setEnabled(true);
			makeMiddleMode.setEnabled(false);
		} else {
			autoCreateProcedure.setEnabled(false);
			createCartoon.setEnabled(false);
			makeMiddleMode.setEnabled(true);
		}

		autoCreateProcedure.setEnabled(true);
		createCartoon.setEnabled(true);
		makeMiddleMode.setEnabled(true);

		submitUniteTechnics.setEnabled(true);
		createProcedureFlow.setEnabled(true);
		saveAsTechnicsTemplet.setEnabled(true);
//		relateTypicalProcessPlan.setEnabled(true);
		if("F".equals(node.getTreeCellData().attributeValue("ZFFLAG"))){
			relateMainMakeProcessPlan.setEnabled(true);
			relateAidedProcessPlan.setEnabled(false);
		}else{
			relateMainMakeProcessPlan.setEnabled(false);
			relateAidedProcessPlan.setEnabled(true);
		}
		delete.setEnabled(true);
		refresh.setEnabled(true);
		expandAll.setEnabled(true);
		createTaskItem.setEnabled(true);
		reviewTechnics.setEnabled(true);
		technicsConfirm.setEnabled(true);
		technicsCoWork.setEnabled(true);
		technicsUpload.setEnabled(true);
		technicsUpdate.setEnabled(true);

		this.erpMenu.setEnable(true);
		this.pbomErpMenu.setEnable(true);
//		this.sjzykMenu.setEnable(true);
		this.pbomSjzykMenu.setEnable(true);
		this.gwTechnicsQuotaMenu.setEnable(true);

		logger.debug("isDownload= " + NewTechnicsPart.isDownload);
		if (NewTechnicsPart.isDownload) {
			signed.setEnabled(false);
			signed2.setEnabled(false);
			cmatsigned.setEnabled(false);
			completeTask.setEnabled(true);
		} else {
			Element currentElement =  node.getTreeCellData();
			signed.setEnabled( AclUtl.sign3Enable(currentElement));

			signed2.setEnabled(true);
			cmatsigned.setEnabled(true);
			completeTask.setEnabled(false);
		}
		viewHistory.setEnabled(true);
		copy.setEnabled(false);

		if (frame.copyDatas.size() > 0) {
			boolean sameType = node.getTreeCellData()
					.attributeValue("technicsType")
					.equals(frame.getTechnicsType());
			if (sameType)
				paste.setEnabled(true);
			else
				paste.setEnabled(false);
		} else
			paste.setEnabled(false);

		Document tech = frame.getCurrentTechnics();
		if (tech != null) {
			try {
				Element ele = XmlUtility.getTechnicsElement(tech);
				String lifecycle = ele.attributeValue("lifecycle");
				String creator = ele.attributeValue("creator");
				if ((lifecycle != null && (!"".equals(lifecycle))
						&& !lifecycle.equals("正在工作")&& !lifecycle.equals("修改中"))
						||!creator.equals(NewTechnicsPart.currentUser)) {
					createStep.setEnabled(false);
					importTechnicsTemplate.setEnabled(false);
					refreshStepNumber.setEnabled(false);
					technicsCoWork.setEnabled(false);
					submitUniteTechnics.setEnabled(false);
					technicsConfirm.setEnabled(false);
					technicsUpload.setEnabled(false);
					technicsUpdate.setEnabled(false);
					signed.setEnabled(false);
					signed2.setEnabled(false);
					cmatsigned.setEnabled(false);
					completeTask.setEnabled(false);
					delete.setEnabled(false);
					paste.setEnabled(false);
					autoCreateProcedure.setEnabled(false);
					createCartoon.setEnabled(false);
//					saveAsTechnicsTemplet.setEnabled(false);
//					relateTypicalProcessPlan.setEnabled(false);
//					relateMainMakeProcessPlan.setEnabled(false);
					refresh.setEnabled(false);
					createTaskItem.setEnabled(false);
					makeMiddleMode.setEnabled(false);

					this.erpMenu.setEnable(false);
					this.pbomErpMenu.setEnable(false);
//					this.sjzykMenu.setEnable(false);
					this.pbomSjzykMenu.setEnable(false);

					this.gwTechnicsQuotaMenu.setEnable(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setStepSelectedStatus(XWTreeObject node) {
		createStep.setEnabled(false);
		importTechnicsTemplate.setEnabled(false);

		XWStepTreeObject curStep = (XWStepTreeObject) node;
		String stepNumber = curStep.getTreeCellData().attributeValue("stepNumber");
		int number = XmlUtility.getSubFigure(stepNumber);
		if (number == -1)
			insertStep.setEnabled(false);
		else {
			XWTreeNode currentNode = treePanel.getSelectedTreeNode();
			XWTreeNode nextNode = (XWTreeNode) currentNode.getNextSibling();
			if (nextNode == null) {
				insertStep.setEnabled(true);
				return;
			}
			XWStepTreeObject nextStep = (XWStepTreeObject) nextNode.getObject();
			String nextStepNumber = nextStep.getTreeCellData().attributeValue("stepNumber");
			int nextNumber = XmlUtility.getSubFigure(nextStepNumber);
			if (nextNumber == -1)
				insertStep.setEnabled(true);
			else {
				if (nextNumber == number + 1)
					insertStep.setEnabled(false);
				else
					insertStep.setEnabled(true);
			}
		}
		autoCreateProcedure.setEnabled(false);
		createProcedureFlow.setEnabled(false);
//		saveAsTechnicsTemplet.setEnabled(false);
//		relateTypicalProcessPlan.setEnabled(false);
//		relateMainMakeProcessPlan.setEnabled(false);
		delete.setEnabled(true);
		refresh.setEnabled(false);
		expandAll.setEnabled(false);
		createTaskItem.setEnabled(false);
		copy.setEnabled(true);
		paste.setEnabled(false);
		technicsUpload.setEnabled(false);
		makeMiddleMode.setEnabled(false);
		technicsUpdate.setEnabled(false);
		//viewHistory.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		cmatsigned.setEnabled(false);
		completeTask.setEnabled(false);
		createCartoon.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsCoWork.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		technicsConfirm.setEnabled(false);
//		relateAidedProcessPlan.setEnabled(false);
	}

	private void setPaceSelectedStatus() {
		createStep.setEnabled(false);
		importTechnicsTemplate.setEnabled(false);
		createPace.setEnabled(false);
		insertStep.setEnabled(false);
		autoCreateProcedure.setEnabled(false);
		createProcedureFlow.setEnabled(false);
		insertStep.setEnabled(false);
//		saveAsTechnicsTemplet.setEnabled(false);
//		relateTypicalProcessPlan.setEnabled(false);
//		relateMainMakeProcessPlan.setEnabled(false);
		delete.setEnabled(true);
		refresh.setEnabled(false);
		expandAll.setEnabled(false);
		createTaskItem.setEnabled(false);
		copy.setEnabled(false);
		paste.setEnabled(false);
		technicsUpload.setEnabled(false);
		makeMiddleMode.setEnabled(false);
		technicsUpdate.setEnabled(false);
		//viewHistory.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		cmatsigned.setEnabled(false);
		completeTask.setEnabled(false);
		createCartoon.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsCoWork.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		technicsConfirm.setEnabled(false);
//		relateAidedProcessPlan.setEnabled(false);
	}

	private void setPartSelectedStatus() {
		createStep.setEnabled(false);
		importTechnicsTemplate.setEnabled(false);
		insertStep.setEnabled(false);
		createPace.setEnabled(false);
		autoCreateProcedure.setEnabled(false);
		createProcedureFlow.setEnabled(false);
//		saveAsTechnicsTemplet.setEnabled(false);
//		relateTypicalProcessPlan.setEnabled(false);
//		relateMainMakeProcessPlan.setEnabled(false);
		delete.setEnabled(false);
		refresh.setEnabled(false);
		expandAll.setEnabled(false);
		createTaskItem.setEnabled(false);
		copy.setEnabled(false);
		paste.setEnabled(false);
		technicsUpload.setEnabled(false);
		makeMiddleMode.setEnabled(false);
		technicsUpdate.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		cmatsigned.setEnabled(false);
		completeTask.setEnabled(false);
		//viewHistory.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsCoWork.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		technicsConfirm.setEnabled(false);
		createCartoon.setEnabled(false);
//		relateAidedProcessPlan.setEnabled(false);
	}

	private void setProductSelectedStatus() {
		createStep.setEnabled(false);
		importTechnicsTemplate.setEnabled(false);
		createPace.setEnabled(false);
		insertStep.setEnabled(false);
		autoCreateProcedure.setEnabled(false);
		createProcedureFlow.setEnabled(false);
//		saveAsTechnicsTemplet.setEnabled(false);
//		relateTypicalProcessPlan.setEnabled(false);
//		relateMainMakeProcessPlan.setEnabled(false);
		delete.setEnabled(false);
		refresh.setEnabled(false);
		expandAll.setEnabled(false);
		createTaskItem.setEnabled(false);
		copy.setEnabled(false);
		paste.setEnabled(false);
		technicsUpload.setEnabled(false);
		makeMiddleMode.setEnabled(false);
		technicsUpdate.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		cmatsigned.setEnabled(false);
		completeTask.setEnabled(false);
		//viewHistory.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsCoWork.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		technicsConfirm.setEnabled(false);
		createCartoon.setEnabled(false);
//		relateAidedProcessPlan.setEnabled(false);
	}

	private void setNullSelectedStatus() {
		createStep.setEnabled(false);
		importTechnicsTemplate.setEnabled(false);
		createPace.setEnabled(false);
		insertStep.setEnabled(false);
		autoCreateProcedure.setEnabled(false);
		createProcedureFlow.setEnabled(false);
//		saveAsTechnicsTemplet.setEnabled(false);
//		relateTypicalProcessPlan.setEnabled(false);
//		relateMainMakeProcessPlan.setEnabled(false);
		delete.setEnabled(false);
		refresh.setEnabled(false);
		expandAll.setEnabled(false);
		createTaskItem.setEnabled(false);
		copy.setEnabled(false);
		paste.setEnabled(false);
		technicsUpload.setEnabled(false);
		makeMiddleMode.setEnabled(false);
		technicsUpdate.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		cmatsigned.setEnabled(false);
		completeTask.setEnabled(false);
		//viewHistory.setEnabled(false);
		reviewTechnics.setEnabled(false);
		technicsCoWork.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		technicsConfirm.setEnabled(false);
		createCartoon.setEnabled(false);
		relateAidedProcessPlan.setEnabled(false);
	}

	private void setTechnicsSelectedStatus() {
	    gongzhuangshenqingdan.setEnabled(false);
		createStep.setEnabled(false);
		importTechnicsTemplate.setEnabled(false);
		createPace.setEnabled(false);
		insertStep.setEnabled(false);
		autoCreateProcedure.setEnabled(false);
		createCartoon.setEnabled(false);
		makeMiddleMode.setEnabled(false);
		submitUniteTechnics.setEnabled(false);
		createProcedureFlow.setEnabled(false);
		saveAsTechnicsTemplet.setEnabled(true);
//		relateTypicalProcessPlan.setEnabled(false);
//		relateMainMakeProcessPlan.setEnabled(false);
		delete.setEnabled(false);
		refresh.setEnabled(false);
		expandAll.setEnabled(false);
		reviewTechnics.setEnabled(true);
		technicsConfirm.setEnabled(false);
		technicsCoWork.setEnabled(false);
		technicsUpload.setEnabled(false);
		technicsUpdate.setEnabled(false);
		signed.setEnabled(false);
		signed2.setEnabled(false);
		cmatsigned.setEnabled(false);
		completeTask.setEnabled(false);
		//viewHistory.setEnabled(false);
		copy.setEnabled(false);
		paste.setEnabled(false);
		run.setEnabled(false);
		createTaskItem.setEnabled(false);
//		relateAidedProcessPlan.setEnabled(false);
	}

}