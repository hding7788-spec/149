/*
 * @author wanghaoyu
 * @date 2013-4-17
 * 版权属 南京国睿信维软件有限公司 所有
 */

package com.glaway.mpm.qmIntf.fittingTool.view;

import com.glaway.mpm.qmIntf.decoratePView.treePanel.*;
import com.glaway.mpm.qmIntf.participatePart.ParticipatePartAddDialog;
import com.glaway.mpm.qmIntf.participatePart.VaClipboard;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaGuiUtil;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.tree.*;
import com.glaway.mpm.visual.view.ui.VaEBomTreePanel;
import org.dom4j.Element;
import wt.method.RemoteMethodServer;

import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.util.List;
import java.util.*;

public class FittingsDistributionFrame extends JFrame {
	private static final long		serialVersionUID	= -2609923839664727926L;

	private static final VaLogger	logger				= VaLogger.getLogger(FittingsDistributionFrame.class);
	private static List<VaTreeNode> ebomNodesList = null;
	JSplitPane						vSplit;
	JSplitPane						Split;
	FittingsChooserPanel			fcPanel;
	FittingsInProcessPanel			fipPanel;
	HashMap<String, String>			hmap;
	public static JTextArea                       bomLable;
	public static NewTechnicsPart	parentFrame;
	// static HashMap<String, String> mapValue;
	private boolean					focusGained			= true;
	private boolean					pviewInitialized	= false;
	public static String pathString;

	public FittingsDistributionFrame(HashMap<String, String> map, NewTechnicsPart parentFrame) {
		FittingsDistributionFrame.parentFrame = parentFrame;
		logger.debug("Input Map:" + map);
		logger.debug("XML path:" + map.get("xmlPath"));
		pathString=map.get("xmlPath")+"";
		if (map != null && map.get("partNumber") != null) {
			VaContext.setCurrentPartNumber(map.get("partNumber").toString());
			VaContext.setCurrentPartOid(map.get("poid").toString());
			VaContext.setCurrentTechXMLPath(map.get("xmlPath").toString());
			VaContext.setMainFrame(parentFrame);
			VaContext.setPbomSaved("true");
		}
		// this.mapValue = map;
		setTitle("参装件分配工具");
		setIconImage(new ImageIcon(FittingsDistributionFrame.class.getResource("/images/cat_icon_connect.png"))
				.getImage());

//		try {
//			this.initEbomTree();
//		} catch (NumberFormatException e1) {
//			logger.debug("initEbomTree NumberFormatException:" + e1);
//		} catch (Exception e1) {
//			logger.debug("initEbomTree Exception:" + e1);
//		}

	    bomLable = new JTextArea("");
	    bomLable.setRows(6);
	    bomLable.setLineWrap(true);
	    bomLable.setEditable(false);

	    //参装件分配打开时全屏显示 changed by liangbo 20181025
	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		setSize(1010, 700);
        JPanel toppan=new JPanel();
        toppan.setLayout(new BorderLayout());
        Split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		vSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		//加载树节点前，刷新历史数据
		try {
			refreshParticipatingData(map.get("partOid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//参装件上半区
		fcPanel = new FittingsChooserPanel(map, this);
		//参装件下半区
		fipPanel = new FittingsInProcessPanel(map, this);
		vSplit.setTopComponent(fcPanel);
		vSplit.setBottomComponent(fipPanel);



		JPanel bottompan=new JPanel();
		bottompan.setLayout(new BorderLayout());
		bottompan.add(vSplit,BorderLayout.CENTER);
        toppan.add(bomLable,BorderLayout.CENTER);
		Split.setTopComponent(toppan);
	    Split.setBottomComponent(bottompan);
	    int height = (int)screenSize.getHeight()/2-80;
	    vSplit.setDividerLocation(height);
		Split.setDividerLocation(80);


		validate();
//		setSize(1000, 800);
		this.add(Split);
		initTreeSelectionListener();
		setUsedPart(fcPanel.getPBomTree(), fipPanel.getProcessTree());

		// final HashMap<String, String> map1 = map;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				List<String> morePartList = ParticipatePartAddDialog.morePartList;
				int defaultCloseOperation = getDefaultCloseOperation();
				System.out.println(defaultCloseOperation);
				if(morePartList.size() > 0){
					if(morePartList.size()>10){
						morePartList = morePartList.subList(0, 9);
						morePartList.add("等等....");
					}
					int isSure = JOptionPane.showConfirmDialog
							(FittingsDistributionFrame.this, "图号为：" + morePartList.toString() + "的PBOM多装，是否继续关闭？","确定",JOptionPane.YES_NO_OPTION);
					if(isSure==JOptionPane.YES_OPTION){
						VaPViewFactory.shutdown(VaPViewFactory.PV_NAME_DP);
						VaPViewFactory.shutdown(VaPViewFactory.PV_NAME_MBOM);
						VaClipboard.clipboard2.clear();
						FittingsDistributionFrame.parentFrame.setFittingToolEnable(true);

						// 关闭参装工具窗口时，删除下载的图形文件
						String folderPath = System.getProperty("java.io.tmpdir") + "\\" + VaContext.getCurrentPartOid();
						File olFolder = new File(folderPath);
						if (olFolder.exists() && olFolder.isDirectory()) {
							File[] files = olFolder.listFiles();
							for (int i = 0; i < files.length; i++) {
								files[i].delete();
							}
							olFolder.delete();
						}
						setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					}else{
						setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					}
				}else{
					VaPViewFactory.shutdown(VaPViewFactory.PV_NAME_DP);
					VaPViewFactory.shutdown(VaPViewFactory.PV_NAME_MBOM);
					VaClipboard.clipboard2.clear();
					FittingsDistributionFrame.parentFrame.setFittingToolEnable(true);

					// 关闭参装工具窗口时，删除下载的图形文件
					String folderPath = System.getProperty("java.io.tmpdir") + "\\" + VaContext.getCurrentPartOid();
					File olFolder = new File(folderPath);
					if (olFolder.exists() && olFolder.isDirectory()) {
						File[] files = olFolder.listFiles();
						for (int i = 0; i < files.length; i++) {
							files[i].delete();
						}
						olFolder.delete();
					}
				}
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				try {
//					 FittingsDistributionFrame.this.parentFrame
//					 .refreshParticipateParts(map1.get("xmlPath"), null);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.windowClosed(arg0);
			}
		});
		addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowLostFocus(WindowEvent e) {
				// TODO Auto-generated method stub
				focusGained = true;
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
				// TODO Auto-generated method stub
				if (focusGained) {
					logger.debug("==================debug for focus");
					// JOptionPane.showMessageDialog(null, "focus gained");
					refreshTechSteps(fipPanel);
					focusGained = false;
				}
			}
		});


		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	/**
	 * 校验参装件是否多装
	 */
	public static List<String> checkIsHasMoreParts() {
		Map<String, Double> noEpmPartMap = new HashMap<String, Double>();
		Map<String, Object[]> czPartMap = new HashMap<String, Object[]>();
// 		Map<String, List<String>> hasEpmPartMap = new HashMap<String, List<String>>();
 		List<String> morePartList = new ArrayList<String>();
		List<String> occIdList;
		//获取ZPBOM上的节点
		VaTree zPBomTree = VaEBomTreePanel.getScrollTreePane().getVaTree();
		VaTreeNode zPBomTreeNode = zPBomTree.getRoot();
		if(zPBomTreeNode!=null &&zPBomTreeNode.getChildCount()>0){
			VaTreeNode child = (VaTreeNode) zPBomTreeNode.getChildAt(0);
			Enumeration<VaTreeNode> zpbomChildren = child.children();
			while (zpbomChildren.hasMoreElements()) {
				VaTreeNode vaTreeNode = zpbomChildren.nextElement();
				String partNumber = vaTreeNode.getPart().getNumber();
				String occid = vaTreeNode.getOccId();
				Double amount = vaTreeNode.getPart().getAmount();

//				if(vaTreeNode.isHasEpmDoc()){
//					如果是有模型的部件，通过occid检验
//					if(hasEpmPartMap.containsKey(partNumber)){
//						occIdList = hasEpmPartMap.get(partNumber);
//						if(!occIdList.contains(occid)){
//							occIdList.add(occid);
//						}
//					}else{
//						occIdList = new ArrayList<String>();
//						occIdList.add(occid);
//						hasEpmPartMap.put(partNumber,occIdList);
//					}
//				}else{
					//如果是没有模型的部件，通过数量检验
					noEpmPartMap.put(partNumber, amount);
//				}
			}

		}
		//获取FPBOM上的节点
		VaTree fPBomTree = VaEBomTreePanel.getScrollTreePane2().getVaTree();
		VaTreeNode fFPbomTreeNode = fPBomTree.getRoot();
		Enumeration<VaTreeNode> fpbomChildren = fFPbomTreeNode.children();
		while (fpbomChildren.hasMoreElements()) {
			VaTreeNode vaTreeNode = fpbomChildren.nextElement();
			String partNumber = vaTreeNode.getPart().getNumber();
			Double amount = vaTreeNode.getPart().getAmount();
			noEpmPartMap.put(partNumber,amount);
		}
		//工艺树
		DpTree dpTree = DpTreePanel.getDpTree();
		VaTreeNode dpTreeRoot = dpTree.getRoot();
		Enumeration<VaTreeNode> dpTreeChildren = dpTreeRoot.children();
		while(dpTreeChildren.hasMoreElements()){
			VaTreeNode vaTreeNode = dpTreeChildren.nextElement();
			if(vaTreeNode instanceof DpStepNode){
				DpStepNode dpStepNode = (DpStepNode) vaTreeNode;
				Enumeration<VaTreeNode> stepChildren = dpStepNode.children();
				while(stepChildren.hasMoreElements()){
					VaTreeNode stepChild = stepChildren.nextElement();
					if(stepChild instanceof DpPaceNode){
						DpPaceNode dpPaceNode = (DpPaceNode) stepChild;
						Enumeration<VaTreeNode> paceChildren = dpPaceNode.children();
						while(paceChildren.hasMoreElements()){
							VaTreeNode paceChild = paceChildren.nextElement();
							if(paceChild instanceof DpPartNode){
								DpPartNode dpPartNode = (DpPartNode) paceChild;
								String partNumber = dpPartNode.getPart().getNumber();
								String occId = dpPartNode.getOccId();
								String zcMark = dpPartNode.getPart().getZcmark();
								Double amount = dpPartNode.getPart().getAmount();
								/*if(dpPartNode.isHasEpmDoc()){
									if(hasEpmPartMap.containsKey(partNumber)){
										List<String> occIds = hasEpmPartMap.get(partNumber);
										if(!occIds.contains(occId)){
											if(!morePartList.contains(partNumber)){
												morePartList.add(partNumber);
											}
											dpPartNode.setMore(true);
											if(dpPartNode.getParent() instanceof DpStepNode){
												((DpStepNode) dpPartNode.getParent()).setMore(true);
											}else if(dpPartNode.getParent() instanceof DpPaceNode){
												((DpPaceNode) dpPartNode.getParent()).setMore(true);
												((DpStepNode)dpPartNode.getParent().getParent()).setMore(true);
											}
										}
									}else{
										if(!morePartList.contains(partNumber)){
											morePartList.add(partNumber);
										}
										dpPartNode.setMore(true);
										if(dpPartNode.getParent() instanceof DpStepNode){
											((DpStepNode) dpPartNode.getParent()).setMore(true);
										}else if(dpPartNode.getParent() instanceof DpPaceNode){
											((DpPaceNode) dpPartNode.getParent()).setMore(true);
											((DpStepNode)dpPartNode.getParent().getParent()).setMore(true);
										}
									}
								}else{*/
									if(czPartMap.containsKey(partNumber)){
										Double count = (Double) czPartMap.get(partNumber)[0];
										List<DpPartNode> partNodeList = (List<DpPartNode>) czPartMap.get(partNumber)[1];
										if("Z".equals(zcMark)){
											Object[] objects = new Object[2];
											objects[0] = CommonUtil.addDouble(count, amount);
											partNodeList.add(dpPartNode);
											objects[1] = partNodeList;
											czPartMap.put(partNumber,objects);
										} else if ("C".equals(zcMark)){
											Object[] objects = new Object[2];
											objects[0] = CommonUtil.subDouble(count, amount);
											partNodeList.add(dpPartNode);
											objects[1] = partNodeList;
											czPartMap.put(partNumber,objects);
										}
									}else{
										List<DpPartNode> dpPartNodeList = new ArrayList<DpPartNode>();
										if("Z".equals(zcMark)){
											Object[] objects = new Object[2];
											objects[0] = CommonUtil.addDouble(0, amount);
											dpPartNodeList.add(dpPartNode);
											objects[1] = dpPartNodeList;
											czPartMap.put(partNumber,objects);
										} else if ("C".equals(zcMark)){
											Object[] objects = new Object[2];
											objects[0] = CommonUtil.subDouble(0, amount);
											dpPartNodeList.add(dpPartNode);
											objects[1] = dpPartNodeList;
											czPartMap.put(partNumber,objects);
										}
									}
//								}
							}
						}

					}else if (stepChild instanceof DpPartNode){
						DpPartNode dpPartNode = (DpPartNode) stepChild;
						String partNumber = dpPartNode.getPart().getNumber();
						String occId = dpPartNode.getOccId();
						String zcMark = dpPartNode.getPart().getZcmark();
						Double amount = dpPartNode.getPart().getAmount();
						/*if(dpPartNode.isHasEpmDoc()){
							if(hasEpmPartMap.containsKey(partNumber)){
								List<String> occIds = hasEpmPartMap.get(partNumber);
								if(!occIds.contains(occId)){
									if(!morePartList.contains(partNumber)){
										morePartList.add(partNumber);
									}
									dpPartNode.setMore(true);
									if(dpPartNode.getParent() instanceof DpStepNode){
										((DpStepNode) dpPartNode.getParent()).setMore(true);
									}else if(dpPartNode.getParent() instanceof DpPaceNode){
										((DpPaceNode) dpPartNode.getParent()).setMore(true);
										((DpStepNode)dpPartNode.getParent().getParent()).setMore(true);
									}
								}
							}else{
								if(!morePartList.contains(partNumber)){
									morePartList.add(partNumber);
								}
								dpPartNode.setMore(true);
								if(dpPartNode.getParent() instanceof DpStepNode){
									((DpStepNode) dpPartNode.getParent()).setMore(true);
								}else if(dpPartNode.getParent() instanceof DpPaceNode){
									((DpPaceNode) dpPartNode.getParent()).setMore(true);
									((DpStepNode)dpPartNode.getParent().getParent()).setMore(true);
								}
							}
						}else{*/
							if(czPartMap.containsKey(partNumber)){
								Double count = (Double) czPartMap.get(partNumber)[0];
								List<DpPartNode> partNodeList = (List<DpPartNode>) czPartMap.get(partNumber)[1];
								if("Z".equals(zcMark)){
									Object[] objects = new Object[2];
									objects[0] = CommonUtil.addDouble(count, amount);
									partNodeList.add(dpPartNode);
									objects[1] = partNodeList;
									czPartMap.put(partNumber,objects);
									czPartMap.put(partNumber,objects);
								} else if ("C".equals(zcMark)){
									Object[] objects = new Object[2];
									objects[0] = CommonUtil.subDouble(count, amount);
									partNodeList.add(dpPartNode);
									objects[1] = partNodeList;
									czPartMap.put(partNumber,objects);
									czPartMap.put(partNumber,objects);
								}
							}else{
								List<DpPartNode> partNodeList = new ArrayList<DpPartNode>();
								if("Z".equals(zcMark)){
									Object[] objects = new Object[2] ;
									objects[0] = CommonUtil.addDouble(0, amount);
									partNodeList.add(dpPartNode);
									objects[1] = partNodeList;
									czPartMap.put(partNumber,objects);
								} else if ("C".equals(zcMark)){
									Object[] objects = new Object[2];
									objects[0] = CommonUtil.subDouble(0, amount);
									partNodeList.add(dpPartNode);
									objects[1] = partNodeList;
									czPartMap.put(partNumber,objects);
								}
							}
//						}
					}
				}

			}
		}
		for(Map.Entry<String ,Object[]> entry : czPartMap.entrySet()){
			String partNumber = entry.getKey();
			Object[] objects = entry.getValue();
			Double czAmount = (Double) objects[0];
//			DpPartNode dpPartNode = (DpPartNode) objects[1];
			List<DpPartNode> dpPartNodeList = (List<DpPartNode>) objects[1];
			Double totalAmount = noEpmPartMap.get(partNumber);
			if(totalAmount == null){
				morePartList.add(partNumber);
				for (DpPartNode dpPartNode : dpPartNodeList) {
					dpPartNode.setMore(true);
					if (dpPartNode.getParent() instanceof DpStepNode) {
						((DpStepNode) dpPartNode.getParent()).setMore(true);
					} else if (dpPartNode.getParent() instanceof DpPaceNode) {
						((DpPaceNode) dpPartNode.getParent()).setMore(true);
						((DpStepNode) dpPartNode.getParent().getParent()).setMore(true);
					}
				}
				continue;
			}
			if(czAmount > totalAmount){
				if(!morePartList.contains(partNumber)){
					morePartList.add(partNumber);
					for (DpPartNode dpPartNode : dpPartNodeList) {
						dpPartNode.setMore(true);
						if (dpPartNode.getParent() instanceof DpStepNode) {
							((DpStepNode) dpPartNode.getParent()).setMore(true);
						} else if (dpPartNode.getParent() instanceof DpPaceNode) {
							((DpPaceNode) dpPartNode.getParent()).setMore(true);
							((DpStepNode) dpPartNode.getParent().getParent()).setMore(true);
						}
					}
				}
			}
		}
		dpTree.updateUI();


		return morePartList;
	}

//	private void initEbomTree() throws NumberFormatException, Exception {
//		 VaTreeNode root = new VaTreeNode("EBOM");
//		 VaTree ebomTree = new VaTree(root);
//		 WTPart part = (WTPart)VaSearchHelper.search(WTPart.class,NewTechnicsPart.getEbomOid());
//		 if(part != null) {
//			 VaLightPart lightpart = VaBizObjUtil.buildVaLightPartFromWTPart(part);
//			 VaPartMerger merger = new VaPartMerger(ebomTree, root,VaContext.getMainFrame());
//			 List<VaLightPart> parts = new ArrayList<VaLightPart>();
//			 parts.add(lightpart);
//			 merger.doMerger(parts);
//		 } else {
//			 //JOptionPane.showMessageDialog(this, "没有找到oid位："+NewTechnicsPart.getEbomOid() +" 的EBOM");
//			 logger.info("没有找到oid位："+NewTechnicsPart.getEbomOid() +" 的EBOM");
//		 }
//
//		 this.getEbomNodeList(ebomTree.getRoot());
//	}

	private  void getEbomNodeList(VaTreeNode root) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			VaTreeNode child = (VaTreeNode) children.nextElement();
			if (null == ebomNodesList) {
				ebomNodesList = new ArrayList<VaTreeNode>();
			}
			ebomNodesList.add(child);
			getEbomNodeList(child);
		}
	}

	public static void getMatrixFromEbom(VaTreeNode node) {
		setMatrix(node);
		Enumeration<VaTreeNode> children = node.children();
		VaTreeNode child = null;
		while(children.hasMoreElements()){
			 child = children.nextElement();
			 getMatrixFromEbom(child);
		}
	}

	private static void setMatrix(VaTreeNode node){
		if(null != ebomNodesList && ebomNodesList.size()>0){
			for (VaTreeNode ebom : ebomNodesList) {
				logger.info("ebom.getOccId()="+ebom.getOccId() + "  node.getOccId()="+node.getOccId());
				logger.info("ebom.getOccpath()="+ebom.getOccpath() + "  node.getOccpath()="+node.getOccpath());
				if (ebom.getOccId().equals(node.getOccId())) {
					node.setMatrix(ebom.getMatrix());
					logger.info(node.getPart().getName()+ " = " + node.getMatrix());
					if (node.getPart().getVersion().equals(ebom.getPart().getVersion())) {
						node.getPart().setReversion(ebom.getPart().isReversion());
					}
				}
			}
		}
	}
	@SuppressWarnings("unchecked")
	public static void refreshTechSteps(FittingsInProcessPanel panel) {

		String path = VaContext.getCurrentTechXMLPath();
		File xmlFile = new File(path);
		if (xmlFile.exists())
			;
		{
			VaTree pTree = panel.getProcessTree();
			VaTreeNode root = pTree.getRoot();
			Enumeration<VaTreeNode> enume = root.depthFirstEnumeration();
			while (enume.hasMoreElements()) {
				VaTreeNode node = enume.nextElement();

				String pvsPath = null;
				if (node instanceof DpStepNode) {
					String stepNum = ((DpStepNode) node).getStep().getOid().replace(':', '`');
					pvsPath = xmlFile.getParent() + "\\anno\\" + stepNum + "\\anno.etb";
				} else if (node instanceof DpPaceNode) {
					String paceNum = ((DpPaceNode) node).getPace().getOid().replace(':', '`');
					String paceStepNum = ((DpStepNode) (node.getParent())).getStep().getOid().replace(':', '`');
					pvsPath = xmlFile.getParent() + "\\anno\\" + paceStepNum + "\\" + paceNum + "\\anno.etb";
				} else {
					pvsPath = null;
				}
				if (pvsPath != null) {
					File checkPvs = new File(pvsPath);
					if (checkPvs != null && checkPvs.exists()) {
						logger.debug("node.setAnno(true);");
						node.setAnno(true);
					} else {
						node.setAnno(false);
					}
				} else {
					node.setAnno(false);
				}

			}
			pTree.repaint();
		}
	}

	/**
	 * 增加工步参装件
	 * @param stepOid
	 * @param paceOid
	 * @param parts
	 */
	public static void addParts(String stepOid, String paceOid) {
		try {
			parentFrame.addParticipateParts(stepOid, paceOid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加工序参装件
	 * @param stepOid
	 * @param parts
	 */
	public static void addParts(String stepOid) {
		try {
			parentFrame.addParticipateParts(stepOid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除工步参装件
	 *
	 * @param stepOid
	 * @param paceOid
	 * @param parts
	 */
	public static void deletePart(String stepOid, String paceOid, Vector<Map<String, String>> parts) {
		parentFrame.deleteParticipateParts(stepOid, paceOid, parts);
	}

	/**
	 * 删除工序参装件
	 *
	 * @param stepOid
	 * @param parts
	 */
	public static void deletePart(String stepOid, Vector<Map<String, String>> parts) {
		parentFrame.deleteParticipateParts(stepOid, parts);
	}

	public static void main(String[] args) {
		RemoteMethodServer.getDefault().setUserName("zlb06");
		RemoteMethodServer.getDefault().setPassword("1");

		HashMap<String, String> map = new HashMap<String, String>();
		// map.put("oid", "584762");//1123638
		map.put("partOid", "1626562");
		map.put("partNumber", "AL2.2001.2");
		map.put("xmlPath", "D:\\annotest\\AL2.2001.2`al2_2001_2`装配工艺`AL2.2001.2`多基地面雷达.xml");

		FittingsDistributionFrame f = new FittingsDistributionFrame(map, null);
		f.setBounds(VaGuiUtil.getScreenCenter(f.getWidth(), f.getHeight()));
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);

	}

	@SuppressWarnings("unchecked")
	private void setUsedPart(VaTree pbomTree, VaTree processTree) {
		Comparator comtor = new VaTreeNodeModelComparator();

		VaTreeNode root = (VaTreeNode) pbomTree.getModel().getRoot(); //pbom树根节点
		Enumeration<VaTreeNode> children = root.breadthFirstEnumeration();

		while (children.hasMoreElements()) {
			VaTreeNode treeNode = (VaTreeNode) children.nextElement();
			VaTreeNode rootP = (VaTreeNode) processTree.getModel().getRoot(); //工艺树根节点

			Enumeration<VaTreeNode> childrenP = rootP.breadthFirstEnumeration();
			int count = 0;
			VaTreeNode vaTreeNode = null;
			while (childrenP.hasMoreElements()) {
				vaTreeNode = (VaTreeNode) childrenP.nextElement();
				if (comtor.compare(vaTreeNode, treeNode) == 0) {
					count ++;
				}
			}
//			if(count % 2 == 0){
//				treeNode.setUsed(false);
//			}else{
//				treeNode.setUsed(true);
//			}

		}

	}

	public void closeWindow() {
		VaPViewFactory.shutdown(VaPViewFactory.PV_NAME_MBOM);
		VaPViewFactory.shutdown(VaPViewFactory.PV_NAME_DP);
		VaClipboard.clipboard2.clear();
		try {
//			 this.parentFrame.refreshParticipateParts(
//			 this.mapValue.get("xmlPath"), null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.dispose();
	}

	/**
	 * 配置VaTree的选择监听,包含联动控制
	 */
	@SuppressWarnings("unchecked")
	private void initTreeSelectionListener() {
//		VaTree pbomETree = fcPanel.getEBomTree();
//		VaTree processETree = fipPanel.getEBomTree();
//		VaTree pbomTree = fcPanel.getPBomTree();
//		VaTree processTree = fipPanel.getProcessTree();
//		VaTree fpbomTree=fcPanel.getjScrollPane1().getScrollTreePane2().getTree();
//
//
//		pbomTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
//		processTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
//		fpbomTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
//
//		// Comparator comtor = new VaTreeNodeComparator();
//		Comparator comtor2 = new VaTreeNodeModelComparator();
//		VaTreeLinkage linkage_pbomETree = new VaDefaultTreeLinkage(comtor2).addLinkageTree(processTree).addLinkageTree(
//				pbomTree).addLinkageTree(processETree);
//		VaTreeLinkage linkage_pbomTree = new VaDefaultTreeLinkage(comtor2).addLinkageTree(pbomETree).addLinkageTree(
//				processTree).addLinkageTree(processETree);
//		VaTreeLinkage linkage_processETree = new VaDefaultTreeLinkage(comtor2).addLinkageTree(pbomETree)
//				.addLinkageTree(processTree).addLinkageTree(pbomTree);
//		VaTreeLinkage linkage_processTree = new VaDefaultTreeLinkage(comtor2).addLinkageTree(pbomETree).addLinkageTree(
//				pbomTree).addLinkageTree(processETree);
//
//		VaTreeLinkage linkage_fpbomTree = new VaDefaultTreeLinkage(comtor2).addLinkageTree(fpbomTree).addLinkageTree(
//				processTree).addLinkageTree(processETree);
//
//
//		VaTreeLinkage linkage_modelPbom = new VaTreeModelLinkage(comtor2).addLinkageTree(pbomETree);
//		VaTreeLinkage linkage_modelProcess = new VaTreeModelLinkage(comtor2).addLinkageTree(processETree)
//				.addLinkageTree(pbomTree);
//		VaTreeLinkage linkage_fmodelPbom = new VaTreeModelLinkage(comtor2).addLinkageTree(fpbomTree);
//
//
//		TreeSelectionListener treeSelectListenre_pbomETree = new VaTreeSelectionListener(linkage_pbomETree);
//		TreeSelectionListener treeSelectListenre_pbomTree = new VaTreeSelectionListener(linkage_pbomTree);
//		TreeSelectionListener treeSelectListenre_processETree = new VaTreeSelectionListener(linkage_processETree);
//		TreeSelectionListener treeSelectListenre_processTree = new VaTreeSelectionListener(linkage_processTree);
//		TreeSelectionListener treeSelectListenre_fpbomTree = new VaTreeSelectionListener(linkage_fpbomTree);
//
//
//		TreeModelListener treeModelListener_pbomtree = new VaTreeModelListener(linkage_modelPbom);
//		TreeModelListener treeModelListener_processtree = new VaTreeModelListener(linkage_modelProcess);
//		TreeModelListener treeModelListener_fpbomtree = new VaTreeModelListener(linkage_fmodelPbom);
//
//
//		pbomETree.addTreeSelectionListener(treeSelectListenre_pbomETree);
//		processETree.addTreeSelectionListener(treeSelectListenre_pbomTree);
//		processETree.addTreeSelectionListener(treeSelectListenre_fpbomTree);
//		pbomTree.addTreeSelectionListener(treeSelectListenre_processETree);
//		processTree.addTreeSelectionListener(treeSelectListenre_processTree);
//
//		pbomTree.getModel().addTreeModelListener(treeModelListener_pbomtree);
//		processTree.getModel().addTreeModelListener(treeModelListener_processtree);
//		fpbomTree.getModel().addTreeModelListener(treeModelListener_fpbomtree);
//
//		pbomETree.setLinkedProcessEBomTree(processETree);
		////////////////////////////////////////////////////////////////


		VaTree zpbomTree = fcPanel.getjScrollPane1().getScrollTreePane().getTree();
		VaTree processTree = fipPanel.getProcessTree();
		VaTree fpbomTree = fcPanel.getjScrollPane1().getScrollTreePane2().getTree();

		zpbomTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		processTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		fpbomTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);


		Comparator comtor2 = new VaTreeNodeModelComparator();
		VaTreeLinkage linkage_zpbomETree = new VaDefaultTreeLinkage(comtor2).addLinkageTree(processTree).addLinkageTree(fpbomTree);
		VaTreeLinkage linkage_fpbomTree = new VaDefaultTreeLinkage(comtor2).addLinkageTree(processTree).addLinkageTree(zpbomTree);
		VaTreeLinkage linkage_processTree = new VaDefaultTreeLinkage(comtor2).addLinkageTree(zpbomTree).addLinkageTree(fpbomTree).addLinkageTree(processTree);


		VaTreeLinkage linkage_modelzPbom = new VaTreeModelLinkage(comtor2).addLinkageTree(zpbomTree);
		VaTreeLinkage linkage_modelfPbom = new VaTreeModelLinkage(comtor2).addLinkageTree(fpbomTree);
		VaTreeLinkage linkage_modelProcess = new VaTreeModelLinkage(comtor2).addLinkageTree(processTree).addLinkageTree(zpbomTree).addLinkageTree(fpbomTree);

		TreeSelectionListener treeSelectListenre_zpbomETree = new VaTreeSelectionListener(linkage_zpbomETree);
		TreeSelectionListener treeSelectListenre_fpbomETree = new VaTreeSelectionListener(linkage_fpbomTree);
		TreeSelectionListener treeSelectListenre_processETree = new VaTreeSelectionListener(linkage_processTree);


		TreeModelListener treeModelListener_zpbomtree = new VaTreeModelListener(linkage_modelzPbom);
		TreeModelListener treeModelListener_fpbomtree = new VaTreeModelListener(linkage_modelfPbom);
		TreeModelListener treeModelListener_processtree = new VaTreeModelListener(linkage_modelProcess);


		zpbomTree.addTreeSelectionListener(treeSelectListenre_processETree);
		fpbomTree.addTreeSelectionListener(treeSelectListenre_processETree);
		processTree.addTreeSelectionListener(treeSelectListenre_zpbomETree);
		processTree.addTreeSelectionListener(treeSelectListenre_fpbomETree);

		zpbomTree.getModel().addTreeModelListener(treeModelListener_zpbomtree);
		fpbomTree.getModel().addTreeModelListener(treeModelListener_fpbomtree);
		processTree.getModel().addTreeModelListener(treeModelListener_processtree);


		zpbomTree.setLinkedProcessEBomTree(processTree);
		fpbomTree.setLinkedProcessEBomTree(processTree);
		processTree.setLinkedEBomTree(zpbomTree);
		processTree.setLinkedEBomTree(fpbomTree);

	}

	public boolean isPviewInitialized() {
		return pviewInitialized;
	}

	public void setPviewInitialized(boolean pviewInitialized) {
		this.pviewInitialized = pviewInitialized;
	}

    public static String getPathString() {
        return pathString;
    }

    public static void setPathString(String pathString) {
        FittingsDistributionFrame.pathString = pathString;
    }



	// /**
	// *配置VaTree的拖动监听
	// */
	// private void initDragListener() {// load drag listener
	// VaTree pbomTree = fcPanel.getPBomTree();
	// // VaTree processTree = fipPanel.getProcessTree();
	//
	// pbomTree.setDragEnabled(true);
	//
	// VaMBomDragNodeDetect detect = new VaMBomDragNodeDetect();
	// VaDefaultDragNodeMerger merger = new VaDefaultDragNodeMerger(processTree,
	// processTree.getRoot(), VaContext.getMainFrame());
	// // 定义源树监听
	// VaTreeDragListeners gestureListener = new VaTreeDragListeners(pbomTree,
	// detect, merger);
	// // 设置目标树TransferHandler
	// processTree.setTransferHandler(gestureListener);
	//
	// DragSource dragSource = DragSource.getDefaultDragSource();
	// dragSource.createDefaultDragGestureRecognizer(pbomTree,
	// DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);
	// new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);
	// }

	/**
	 * 方法功能:刷新参装件历史数据，确保与现在结构一致
	 *
	 * @return void
	 * @author LB
	 * @date 2020/4/15
	 */
	private void refreshParticipatingData(String partOid) throws Exception {
		XWTreeNode currentTechnicsNode = parentFrame.technicsTreePanel.getCurrentTechnicsNode();
		/*List<String> hasEpmPartNumberList = new ArrayList<String>();
		if(TechnicsIntf.checkIsHasEpm(partOid)){
//		if(true){
			List<Element> partElementList = VaTree.buildTreePbom();
			for (Element element : partElementList) {
				List<Element> childElements = element.selectNodes("childs/QMPartInfo ");
				for (Element childElement : childElements) {
					String partNumber = childElement.attributeValue("partNumber");
					if (partNumber != null) {
						hasEpmPartNumberList.add(partNumber);
					}

				}
			}
		}else{
			List<Element> partElementList = VaTree.buildTreePbom();
			for (Element element : partElementList) {
				List<Element> childElements = element.selectNodes("childs/QMPartInfo ");
				for (Element childElement : childElements) {
					String oid = childElement.attributeValue("oid");
					String partNumber = childElement.attributeValue("partNumber");
					if(TechnicsIntf.checkIsHasEpm(oid)){
						if (partNumber != null) {
							hasEpmPartNumberList.add(partNumber);
						}
					}
				}
			}
		}*/
		Element technicsElement = currentTechnicsNode.getObject().getTreeCellData();
		List<Element> stepElements = technicsElement.selectNodes("steps/QMProcedureInfo");
		for (Element stepElement : stepElements) {
			refreshQMPartInfo(stepElement);
			List<Element> paceElements = stepElement.selectNodes("paces/QMProcedureInfo");
			for (Element paceElement : paceElements) {
				refreshQMPartInfo(paceElement);
			}
		}
		parentFrame.saveProcess(technicsElement);
	}

	/**
	 * 方法功能: 刷新工序或工步下的参装信息
	 * @param element
	 * @return void
	 * @author LB
	 * @date 2020/4/15
	 */
	private void refreshQMPartInfo(Element element) {
		Map<String,Element> partElementMap = new HashMap<String, Element>();
		List<Element> partElements = element.selectNodes("parts/QMPartInfo");
		Element partsEle = element.element("parts");
		if(partsEle != null && partElements != null){
			//1、把原有历史数据移除
			for (Element partElement : partElements) {
				String partNumber = XmlUtility.getAttributeValue(partElement, "partNumber");
//				if(hasEpmPartNumberList.contains(partNumber)){
//					XmlUtility.setAttributeValue(partElement, "HASEPM", "true");
//					continue;
//				}
				String useCount = XmlUtility.getAttributeValue(partElement, "useCount");
				String occId = XmlUtility.getAttributeValue(partElement, "occId");
				if (partElementMap.containsKey(partNumber)) {
					Element partEle = partElementMap.get(partNumber);
					String count = XmlUtility.getAttributeValue(partEle, "useCount");
					double totalCount = CommonUtil.addDouble(Double.valueOf(count), Double.valueOf(useCount));
					XmlUtility.setAttributeValue(partEle, "useCount", totalCount + "");
				} else {
					if(occId.contains("$")){
						occId = occId.substring(0,occId.indexOf("$"));
					}
					XmlUtility.setAttributeValue(partElement, "occId", occId);
//					XmlUtility.setAttributeValue(partElement, "HASEPM", "false");
					partElementMap.put(partNumber, partElement);
				}
				partsEle.remove(partElement);
			}
			//2、给xml增加新的节点
			for (Map.Entry<String, Element> entry : partElementMap.entrySet()) {
				partsEle.add(entry.getValue());
			}
		}

	}
}
