package com.glaway.mpm.view;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JToolBar;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.qmIntf.common.model.CommonActionButton;
import com.glaway.mpm.util.IconUtil;
import com.glaway.mpm.util.XmlUtility;

public class UniversalToolBar extends JToolBar {

	private static final long serialVersionUID = 1L;

	private NewTechnicsPart frame;
	private Action newTechnics;
	private Action newProcedure;
	private Action copy;
	private Action paste;
	private Action technicsUpload;
	private Action technicsReview;
	private Action technicsUpdate;
	private Action viewTechnicsHistory;
	private Action PBOMUpdate;
	private Action assemblageTool;
//	private Action searchFrockCard;
	private Action previousStep;
	private Action nextStep;
	private Vector datas=new Vector();

	public UniversalToolBar(NewTechnicsPart frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载工具菜单");
		this.frame = frame;
		this.setFloatable(false);
		this.setRollover(true);
		this.setMargin(new Insets(0, 5, 0, 5));
		newTechnics = new AbstractAction("", IconUtil.getImageIcon(IconUtil.NEW_TECHNICS)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					UniversalToolBar.this.frame.createTechnics();
				} catch (Exception ee) {
					ee.printStackTrace();
					JOptionPane.showMessageDialog(UniversalToolBar.this.frame,
							"创建工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};

		add(new CommonActionButton(newTechnics, "新建工艺"));

		newProcedure = new AbstractAction("", IconUtil.getImageIcon(IconUtil.NEW_STEP)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					try {
						UniversalToolBar.this.frame.createProdcutStep();
					} catch (Exception ee) {
						ee.printStackTrace();
						JOptionPane.showMessageDialog(
								UniversalToolBar.this.frame, "创建工序出现错误！", "提示",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					JOptionPane.showMessageDialog(UniversalToolBar.this.frame,
							"创建工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};

		add(new CommonActionButton(newProcedure, "新建工序"));
/**
 * @修改 王鑫磊
 * @核对 马崇奇
 * @date 2015-6-3
 */
		copy = new AbstractAction("", IconUtil.getImageIcon(IconUtil.COPY)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					try {
						UniversalToolBar.this.frame.copy();
						if(UniversalToolBar.this.frame.getLeftTab().getSelectedIndex() == 0){
							XWTreeNode node = UniversalToolBar.this.frame.getXWPartTreePanel().getSelectedTreeNode();
							if(node != null) {
								XWTreeObject object = node.getObject();
								if(object instanceof TechnicsMessageTreeObject) {
									TechnicsMessageTreeObject tmTreeObject = (TechnicsMessageTreeObject)object;
									UniversalToolBar.this.frame.copyTechNumber = tmTreeObject.getTechNumber();
									JOptionPane.showMessageDialog(UniversalToolBar.this.frame, "已复制工艺文件："+tmTreeObject.getPplanNumber());
								} else {
									JOptionPane.showMessageDialog(UniversalToolBar.this.frame, "该操作需要针对工艺进行，产品树中无选中工艺节点！");
								}
							} else {
								JOptionPane.showMessageDialog(UniversalToolBar.this.frame, "该操作需要针对工艺进行，产品树中无选中工艺节点！");
							}
						}else if(UniversalToolBar.this.frame.getLeftTab().getSelectedIndex() == 1){
							XWTreeNode node = UniversalToolBar.this.frame.getTechnicsTreePanel().getSelectedTreeNode();
							datas = UniversalToolBar.this.frame.copyDatas;
							paste.setEnabled(false);
						}

					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(
								UniversalToolBar.this.frame, "复制工序出现错误！", "提示",
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					JOptionPane.showMessageDialog(UniversalToolBar.this.frame,
							"创建工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};

		add(new CommonActionButton(copy, "复制"));

		/**
		 * @修改 王鑫磊
		 * @核对 马崇奇
		 * @date 2015-6-3
		 */
		paste = new AbstractAction("", IconUtil.getImageIcon(IconUtil.PASTE)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					if (UniversalToolBar.this.frame.getLeftTab().getSelectedIndex() == 0) {
						XWTreeNode node = UniversalToolBar.this.frame.getXWPartTreePanel().getSelectedTreeNode();
						XWTreeObject xo = node.getObject();
						if(xo instanceof XWPartTreeObject){
							UniversalToolBar.this.frame.pastTechnics();
						}
					}else if (UniversalToolBar.this.frame.getLeftTab().getSelectedIndex() == 1){
						XWTreeNode node = UniversalToolBar.this.frame.getTechnicsTreePanel().getSelectedTreeNode();
						XWTreeObject xo = node.getObject();
						if(xo instanceof XWTechnicsTreeObject){
							UniversalToolBar.this.frame.pasteProductTechnics();
						}else if(xo instanceof XWStepTreeObject){
							UniversalToolBar.this.frame.pastePace();

						}
					}

//					if (UniversalToolBar.this.frame.getLeftTab().getSelectedIndex() == 1)
//						UniversalToolBar.this.frame.pasteProductTechnics();
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(UniversalToolBar.this.frame,
							"粘贴工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};

		add(new CommonActionButton(paste, "粘贴"));

		technicsUpload = new AbstractAction("", IconUtil.getImageIcon(IconUtil.TECHNICSUPLOAD)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				UniversalToolBar.this.frame.technicsUpload();
			}
		};

		add(new CommonActionButton(technicsUpload, "工艺上载"));

		technicsReview = new AbstractAction("", IconUtil.getImageIcon(IconUtil.PREVIEW)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					XWTreeNode node = null;
					if (UniversalToolBar.this.frame.getLeftTab().getSelectedIndex() == 1) {
						node = UniversalToolBar.this.frame.getTechnicsTreePanel().getSelectedTreeNode();
						UniversalToolBar.this.frame.reviewTechnics(node);
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(UniversalToolBar.this.frame,
							"预览工艺出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
					e1.printStackTrace();
				}
			}
		};

		add(new CommonActionButton(technicsReview, "工艺预览"));

		technicsUpdate = new AbstractAction("", IconUtil.getImageIcon(IconUtil.TECHNICSUPDATE)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					UniversalToolBar.this.frame.technicsUpdate();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(UniversalToolBar.this.frame,
									"工艺更新时出现错误！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
					e1.printStackTrace();
				}
			}
		};

		add(new CommonActionButton(technicsUpdate, "工艺更新"));

		viewTechnicsHistory = new AbstractAction("",IconUtil.getImageIcon(IconUtil.VIEWHISTORYTECHNICS)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				UniversalToolBar.this.frame.qmViewHistorytechnics();
//				UniversalToolBar.this.frame.viewHistoryTechnics();
			}
		};

		add(new CommonActionButton(viewTechnicsHistory, "查看历史版本"));

		PBOMUpdate = new AbstractAction("", IconUtil.getImageIcon(IconUtil.PBOMUPDATE)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				try {
					UniversalToolBar.this.frame.updatePBOM();
					JOptionPane.showMessageDialog(UniversalToolBar.this.frame,
							"PBOM更新成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(UniversalToolBar.this.frame,
									"PBOM更新时出错！", "提示",
									JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};

		add(new CommonActionButton(PBOMUpdate, "PBOM更新"));

		assemblageTool = new AbstractAction("", IconUtil.getImageIcon(IconUtil.ASSEMBLETOOL)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				UniversalToolBar.this.frame.getTechnicsStepJPanel()
						.getPaceTable().dynamicAssemblagePicture();
			}
		};

		add(new CommonActionButton(assemblageTool, "装配工具"));

		assemblageTool.setEnabled(false);
		//
		// searchFrockCard = new AbstractAction("",
		// IconUtil.getImageIcon(IconUtil.SEARCH_PRODUCT)) {
		// private static final long serialVersionUID = 1L;
		//
		// public void actionPerformed(ActionEvent e) {
		// UniversalToolBar.this.frame.searchFrockCard();
		// }
		// };
		//
		// add(new CommonActionButton(searchFrockCard, "查看工装申请卡"));

		previousStep = new AbstractAction("", IconUtil.getImageIcon(IconUtil.PREVIOUS_STEP)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				UniversalToolBar.this.frame.previousStep();
			}
		};

		add(new CommonActionButton(previousStep, "上一个工序"));

		nextStep = new AbstractAction("", IconUtil.getImageIcon(IconUtil.NEXT_STEP)) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				UniversalToolBar.this.frame.nextStep();
			}
		};

		add(new CommonActionButton(nextStep, "下一个工序"));
		previousStep.setEnabled(false);
		nextStep.setEnabled(false);
	}
	/**
	 * @修改  王鑫磊、陈敏
	 * @核对  马崇奇
	 * @date 2015-6-3
	 */

	public void setToolBarEnabled() {
		if (frame.getLeftTab().getSelectedIndex() == 0) {
			XWTreeNode node = frame.getXWPartTreePanel().getSelectedTreeNode();
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if (xo instanceof TechnicsMessageTreeObject) {
					technicsUpload.setEnabled(true);
					technicsReview.setEnabled(true);
					technicsUpdate.setEnabled(true);
					viewTechnicsHistory.setEnabled(true);
					newTechnics.setEnabled(false);
					PBOMUpdate.setEnabled(false);
					copy.setEnabled(true);
					paste.setEnabled(false);
					assemblageTool.setEnabled(false);
					previousStep.setEnabled(false);
					nextStep.setEnabled(false);
					Document tech = frame.getCurrentTechnics();
					if (tech != null) {
						try {
							Element ele = XmlUtility.getTechnicsElement(tech);
							String lifecycle = ele.attributeValue("lifecycle");
							if (lifecycle != null && (!"".equals(lifecycle))
									&& !lifecycle.equals("拟制")
									&& !lifecycle.equals("驳回")) {
								technicsUpload.setEnabled(false);
								technicsUpdate.setEnabled(false);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if (xo instanceof XWPartTreeObject) {
					XWPartTreeObject po = (XWPartTreeObject) xo;
					if (po.isAllowed()) {
						newTechnics.setEnabled(true);
					} else {
						newTechnics.setEnabled(false);
					}
					if(UniversalToolBar.this.frame.copyTechNumber != null&&po.isAllowed()){
						paste.setEnabled(true);
					} else {
						paste.setEnabled(false);
					}
					previousStep.setEnabled(false);
					nextStep.setEnabled(false);
					assemblageTool.setEnabled(false);
					copy.setEnabled(false);
					PBOMUpdate.setEnabled(true);
					technicsUpload.setEnabled(false);
					technicsReview.setEnabled(false);
					technicsUpdate.setEnabled(false);
					viewTechnicsHistory.setEnabled(false);
				} else {
					newTechnics.setEnabled(false);
					assemblageTool.setEnabled(false);
					viewTechnicsHistory.setEnabled(false);
					copy.setEnabled(false);
					paste.setEnabled(false);
				}
			} else {
				copy.setEnabled(false);
				paste.setEnabled(false);
				technicsUpload.setEnabled(false);
				technicsReview.setEnabled(false);
				technicsUpdate.setEnabled(false);
				viewTechnicsHistory.setEnabled(false);
				newTechnics.setEnabled(false);
				assemblageTool.setEnabled(false);
				previousStep.setEnabled(false);
				nextStep.setEnabled(false);
			}
//			copy.setEnabled(false);
//			paste.setEnabled(false);
			newProcedure.setEnabled(false);
		} else if (frame.getLeftTab().getSelectedIndex() == 1) {
			XWTreeNode node = frame.getTechnicsTreePanel().getSelectedTreeNode();
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if (xo instanceof XWTechnicsTreeObject) {
					technicsReview.setEnabled(true);
					technicsUpload.setEnabled(true);
					technicsUpdate.setEnabled(true);
					assemblageTool.setEnabled(false);
					viewTechnicsHistory.setEnabled(true);
					newProcedure.setEnabled(true);
					copy.setEnabled(true);
					previousStep.setEnabled(false);
					nextStep.setEnabled(false);
					if(datas.size()==0){
						paste.setEnabled(false);
					}else{
						paste.setEnabled(true);
					}
//					for(int i = 0; i < datas.size(); i++){
//						Element element = (Element) datas.get(i);
//						if(element.attribute("operateInstruction") != null){
//							paste.setEnabled(true);
//						}else {
//							paste.setEnabled(false);
//						}
//					}
					Document tech = frame.getCurrentTechnics();

					if (tech != null) {
						try {
							Element ele = XmlUtility.getTechnicsElement(tech);
							String lifecycle = ele.attributeValue("lifecycle");
							if (lifecycle != null && (!"".equals(lifecycle)) && !lifecycle.equals("拟制")) {
								technicsUpload.setEnabled(false);
								technicsUpdate.setEnabled(false);
								newProcedure.setEnabled(false);
//								paste.setEnabled(false);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if (xo instanceof XWStepTreeObject) {
					technicsReview.setEnabled(false);
					technicsUpload.setEnabled(false);
					technicsUpdate.setEnabled(false);
					assemblageTool.setEnabled(true);
					viewTechnicsHistory.setEnabled(false);
					newProcedure.setEnabled(false);
					copy.setEnabled(true);
					previousStep.setEnabled(false);
					nextStep.setEnabled(false);
					if(node.getPreviousSibling() != null && node.getNextSibling() == null){
						previousStep.setEnabled(true);
					}else if(node.getNextSibling() != null && node.getPreviousSibling() == null){
						nextStep.setEnabled(true);
					}else if(node.getNextSibling() != null && node.getPreviousSibling() != null){
						previousStep.setEnabled(true);
						nextStep.setEnabled(true);
					}else{
						previousStep.setEnabled(false);
						nextStep.setEnabled(false);
					}
					if(UniversalToolBar.this.frame.copyPace.size()==0){
						paste.setEnabled(false);
					}else{
						paste.setEnabled(true);
					}
//					if(datas != null && datas.size() > 0){
//						for(int i = 0; i < datas.size(); i++){
//							Element element = (Element) datas.get(i);
//							if(element.attribute("operateInstruction") == null){
//								paste.setEnabled(true);
//							}else{
//								paste.setEnabled(false);
//							}
//
//						}
//					}
				}else if (xo instanceof XWPaceTreeObject){
					copy.setEnabled(true);
					assemblageTool.setEnabled(false);
					previousStep.setEnabled(false);
					nextStep.setEnabled(false);
					paste.setEnabled(false);
				} else {
					technicsReview.setEnabled(false);
					technicsUpload.setEnabled(false);
					technicsUpdate.setEnabled(false);
					viewTechnicsHistory.setEnabled(false);
					newProcedure.setEnabled(false);
					copy.setEnabled(false);
					paste.setEnabled(false);
					assemblageTool.setEnabled(false);
					previousStep.setEnabled(false);
					nextStep.setEnabled(false);
				}
			}
			newTechnics.setEnabled(false);
			PBOMUpdate.setEnabled(false);
		} else {
			newTechnics.setEnabled(false);
			newProcedure.setEnabled(false);
			copy.setEnabled(false);
			paste.setEnabled(false);
			technicsUpload.setEnabled(false);
			technicsReview.setEnabled(false);
			technicsUpdate.setEnabled(false);
			viewTechnicsHistory.setEnabled(false);
			PBOMUpdate.setEnabled(false);
			assemblageTool.setEnabled(false);
			previousStep.setEnabled(false);
			nextStep.setEnabled(false);
		}
	}


	public void setAssemblageEnabled(JTable table) {
		if (table.getSelectedRowCount() > 0) {
			assemblageTool.setEnabled(true);
		} else {
			assemblageTool.setEnabled(false);
		}
		Document tech = frame.getCurrentTechnics();
		if (tech != null) {
			try {
				Element ele = XmlUtility.getTechnicsElement(tech);
				String lifecycle = ele.attributeValue("lifecycle");
				if (lifecycle != null && (!"".equals(lifecycle)) && !lifecycle.equals("拟制") && !lifecycle.equals("驳回")) {
					assemblageTool.setEnabled(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}