package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.flowchart.TechnicsRouteJPanel;
import com.glaway.mpm.flowchart.TechnicsRouteUnit;
import com.glaway.mpm.flowchart.TechnicsRouteUtil;
import com.glaway.mpm.qmIntf.viewPanel.Cortona3DPanel;
import com.glaway.mpm.qmIntf.viewPanel.CreoViewPanel;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;

public class NewTechnicsHistoryView extends JDialog {
	private Document technicsDocument;

	private Element technicsElement;

	private NewTechnicsPart frame;

	private JPanel imagePanel = new JPanel();

	private JPanel dataPanel = new JPanel();

	private JPanel contentPane = new JPanel();

	private JPanel foregoingPanel;

	// 用于分割结构树和内容面板 薛凯 2012\10\15
	private JSplitPane jSplitPane = new JSplitPane();

	// 用于分割工艺、工序内容编辑面板和图形显示区域 薛凯 2012\10\15
	private JSplitPane contentSplitPane = new JSplitPane();

	private JPanel rightJPanel = new JPanel();
	private JPanel leftJPanel = new JPanel();

	private JTabbedPane treeJTabbedPane = new JTabbedPane();

	private JPanel tecnicsPanel = new JPanel();

	private JTabbedPane tecnicsJTabbedPane = new JTabbedPane();

	private TechnicsRouteJPanel technicsRouteJPanel;

	private TechnicsTreePanel_View techTreePanel = new TechnicsTreePanel_View(
			this);

	private NewTechnicsMasterJPanel_View technicsMasterJPanel;

	private TechnicsStepJPanel_View technicsStepJPanel;

	private CreoViewPanel creopanel;

	private Cortona3DPanel panel3D;

	public NewTechnicsHistorySelect newTechnicsHistorySelect;

	private String technicsFlodName;

	public NewTechnicsHistoryView(Document technicsDocument,
			NewTechnicsPart frame,
			NewTechnicsHistorySelect newTechnicsHistorySelect,
			String technicsFlodName, String title) {
		super(frame);
		this.frame = frame;
		this.technicsFlodName = technicsFlodName;
		this.newTechnicsHistorySelect = newTechnicsHistorySelect;
		this.technicsRouteJPanel = new TechnicsRouteJPanel(this.frame);
		this.technicsDocument = technicsDocument;
		this.technicsMasterJPanel = new NewTechnicsMasterJPanel_View(frame);
		setModal(true);
		try {
			technicsElement = XmlUtility.getTechnicsElement(technicsDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setTitle(title);

		technicsStepJPanel = new TechnicsStepJPanel_View(this,this.frame);
		technicsStepJPanel.setUIEnabled(false);

		// setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BorderLayout layout = new BorderLayout(5, 5);
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		setSize(width, height - 40);
		// setBounds(width/2-width/3, height/2-height/3, width*2/3, height*2/3);

		setIconImage(new ImageIcon(getClass().getResource("/images/technics.gif")).getImage());

		contentPane = (JPanel) getContentPane();

		contentPane.setLayout(new BorderLayout());

		contentPane.add(jSplitPane, BorderLayout.CENTER);
		jSplitPane.setOneTouchExpandable(true);
		jSplitPane.setDividerSize(10);
		jSplitPane.setMinimumSize(new Dimension(100, 226));
		jSplitPane.setContinuousLayout(true);
		jSplitPane.setDividerLocation(280);
		jSplitPane.add(leftJPanel, JSplitPane.LEFT);

		leftJPanel.setLayout(new GridBagLayout());
		leftJPanel.setBorder(null);
		leftJPanel.setDebugGraphicsOptions(0);
		leftJPanel.setMinimumSize(new Dimension(100, 224));
		leftJPanel.setPreferredSize(new Dimension(240, 524));
		leftJPanel.add(treeJTabbedPane, new GridBagConstraints(0, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		treeJTabbedPane.setBorder(BorderFactory.createEtchedBorder());

		creopanel = new CreoViewPanel();
		panel3D = new Cortona3DPanel();
		imagePanel.setLayout(new GridBagLayout());
		imagePanel.setBackground(Color.white);
		imagePanel.add(creopanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		imagePanel.add(panel3D, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		creopanel.setVisible(false);
		panel3D.setVisible(false);

		treeJTabbedPane.add(techTreePanel, "工艺树");
		try {
			techTreePanel.hangTechnicsDocument(technicsDocument);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		jSplitPane.add(rightJPanel, JSplitPane.RIGHT);

		// 工艺界面分为三部分
		rightJPanel.setLayout(new BorderLayout());
		rightJPanel.add(contentSplitPane, BorderLayout.CENTER);
		contentSplitPane.setMinimumSize(new Dimension(100, 226));
		contentSplitPane.setContinuousLayout(true);
		contentSplitPane.setOneTouchExpandable(true);
		contentSplitPane.setDividerSize(10);
		contentSplitPane.setDividerLocation(1060);
		// 添加中间的工序内容面板
		contentSplitPane.add(tecnicsPanel, JSplitPane.LEFT);
		// 添加右侧的图形展示面板
		contentSplitPane.add(imagePanel, JSplitPane.RIGHT);

		tecnicsPanel.setLayout(new GridBagLayout());
		tecnicsPanel.add(tecnicsJTabbedPane, new GridBagConstraints(0, 0, 1, 1,
				1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5), 0, 0));
		tecnicsJTabbedPane.add(dataPanel, "工艺信息");
		tecnicsJTabbedPane.add(new JScrollPane(technicsRouteJPanel,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), "工艺路线");
		try {
			File routeFile = new File(technicsFlodName + File.separator + TechnicsRouteUtil.TECHNICS_ROUTE_XML);
			if (routeFile.exists()) {
				Vector<TechnicsRouteUnit> vector = TechnicsRouteUtil.getUnits(XmlUtility.getDocument(routeFile));
				System.out.println("=======vector==========" + vector.size());
				technicsRouteJPanel.setDrawingUnits(vector);
				technicsRouteJPanel.viewAdjusting();
				technicsRouteJPanel.setEventEnabled(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		tecnicsJTabbedPane.setSelectedIndex(0);
		dataPanel.setLayout(new GridBagLayout());
		dataPanel.add(technicsMasterJPanel, new GridBagConstraints(0, 0, 1, 1,
				1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
				new Insets(5, 5, 5, 5), 0, 0));
		technicsMasterJPanel.setUIValues(technicsElement);

		setVisible(true);
	}

	public Document getDocument() {
		return technicsDocument;
	}

	public Element getElement() {
		return technicsElement;
	}

	public void showContent(int index) {
		if (index == 0) {
			tecnicsJTabbedPane.setSelectedIndex(0);
		} else if (index == 1) {
			tecnicsJTabbedPane.setSelectedIndex(1);
		}
	}

	public void treeSelectedValueChanged(XWTreeNode newone) {
		System.out
				.println("treeSelectedValueChanged=====treeSelectedValueChanged");
		if (tecnicsJTabbedPane.getSelectedIndex() == 1)
			return;
		try {
			// 比较旧节点数据是否发生变化
			if (newone != null) {
				showData(newone);
			}
		} catch (Exception ee) {
			ee.printStackTrace();
			JOptionPane.showMessageDialog(NewTechnicsHistoryView.this,
					ee.getMessage(), "提示", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void showData(XWTreeNode newone) throws Exception {
		int index = 0;
		if (tecnicsJTabbedPane != null)
			index = tecnicsJTabbedPane.getSelectedIndex();
		System.out.println("tecnicsJTabbedPane======="
				+ tecnicsJTabbedPane.getTabCount());
		if (index < 0 || index > 1)
			index = 0;
		if (newone != null) {
			XWTreeObject treeObject = newone.getObject();
			Element data = treeObject.getTreeCellData();
			System.out.println("data=========" + data);
			if (treeObject instanceof XWTechnicsTreeObject)// 当前选择工艺
			{
				show3DPane(null);
				clearRightContent();
				String curType = XmlUtility.getAttributeValue(technicsElement, "technicsType");
				technicsMasterJPanel.clearUI();
				foregoingPanel = technicsMasterJPanel;
				dataPanel.add(technicsMasterJPanel, new GridBagConstraints(0,
						0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
						GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
				technicsMasterJPanel.setUIValues(data);
				// if(tecnicsJTabbedPane.getTabCount() == 1)
				// tecnicsJTabbedPane.add(new
				// JScrollPane(technicsRouteJPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),"工艺路线");
				// technicsMasterJPanel.setNode(newone);
				// if(tecnicsJTabbedPane.getTabCount() > 0)
				// tecnicsJTabbedPane.setSelectedIndex(index);
				// technicsMasterJPanel.setMaterialPartVisible(curType);
				if (tecnicsJTabbedPane.getTabCount() > 0)
					tecnicsJTabbedPane.setTitleAt(0, "工艺信息");
				if (curType != null && curType.equals(WorkSpaceUtil.ASM_TYPE))// 装配
				{
					String wrlFile = data.attributeValue("wrlFile");
					if (wrlFile != null && wrlFile.trim().length() > 0) {
						creopanel.setVisible(false);
						panel3D.setVisible(true);
						System.out.println("工艺————自动创建工艺文件000==" + wrlFile);
						String path = technicsFlodName + "\\" + wrlFile
								+ ".wrl";
						System.out.println("3D图形文件路径为=====" + path);
						panel3D.setView(path);
						panel3D.play();

					} else {
						show3DPane(null);
					}
				} else// 零部件
				{
					panel3D.setVisible(false);
					creopanel.setVisible(true);
				}
				technicsMasterJPanel.repaint();
				dataPanel.repaint();
				imagePanel.repaint();
				tecnicsPanel.repaint();
			}
			if (treeObject instanceof XWStepTreeObject) {
				show3DPane(null);
				clearRightContent();
				String curType = XmlUtility.getAttributeValue(technicsElement,
						"technicsType");
				technicsStepJPanel.clearUI();
				foregoingPanel = technicsStepJPanel;
				dataPanel.add(technicsStepJPanel, new GridBagConstraints(0, 0,
						1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
						GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
				// technicsStepJPanel.setTechType(curType);
				// technicsStepJPanel.setPartVisible(curType);
				technicsStepJPanel.setUIValues(data);
				technicsStepJPanel.setNode(newone);
				technicsStepJPanel.setUIEnabled(false);
				// if(tecnicsJTabbedPane.getTabCount() > 0)
				// tecnicsJTabbedPane.setSelectedIndex(index);
				if (tecnicsJTabbedPane.getTabCount() > 0)
					tecnicsJTabbedPane.setTitleAt(0, "工序信息");
				// if(tecnicsJTabbedPane.getTabCount() == 1)
				// tecnicsJTabbedPane.add(new
				// JScrollPane(technicsRouteJPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),"工艺路线");
				if (curType != null && curType.equals(WorkSpaceUtil.ASM_TYPE))// 装配
				{
					String cortonaID = data.attributeValue("cortonaID");
					if (cortonaID != null && cortonaID.trim().length() > 0) {
						System.out.println("工序————自动创建工艺文件222==" + cortonaID);
						creopanel.setVisible(false);
						panel3D.setVisible(true);
						show3DPane(cortonaID);
					} else {
						show3DPane(null);
					}
				} else// 零部件
				{
					panel3D.setVisible(false);
					creopanel.setVisible(true);
				}
				technicsStepJPanel.repaint();
				dataPanel.repaint();
				imagePanel.repaint();
				tecnicsPanel.repaint();
			}
		}
	}

	public void clearRightContent() {
		dataPanel.removeAll();
		foregoingPanel = null;
		technicsMasterJPanel.clearUI();
		technicsStepJPanel.clearUI();

		tecnicsPanel.repaint();
		repaint();
	}

	public String getCurrentUser() {
		String creatorOid = "";
		List userlist = UserUtil.getCurrentUserOid();
		if (userlist != null && userlist.size() == 3) {
			creatorOid = (String) userlist.get(1);
		}
		return creatorOid;
	}

	public void show3DPane(String actionID) {
		try {
			creopanel.setVisible(false);
			panel3D.setVisible(true);
			if (technicsDocument != null) {
				Element techEle = XmlUtility
						.getTechnicsElement(technicsDocument);
				String techNumber = techEle.attributeValue("technicsNumber");
				String wrlFile = techEle.attributeValue("wrlFile");
				if (wrlFile != null && wrlFile.trim().length() > 0) {
					String path = technicsFlodName + File.separator + wrlFile
							+ ".wrl";
					System.out.println("3D图形文件路径为=====" + path);
					panel3D.setView(path);
					panel3D.click(actionID);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void expand(XWTreeNode node) throws Exception {
		if (node != null) {
			node.removeAllChildren();
			techTreePanel.expandAllNode(node);
		}
	}

	public void show2DPane(Vector v) {
		panel3D.setVisible(false);
		creopanel.setVisible(true);
		creopanel.setView(v);
	}

	public void copy() throws Exception {
		frame.copyDatas.clear();
		System.out.println("**************copy*******************");
		XWTreeNode node = null;
		Vector vec = techTreePanel.getSelectedPaths();
		if (vec != null && vec.size() > 0) {
			frame.technicsType = techTreePanel.getCurrentTechnicsNode()
					.getObject().getTreeCellData()
					.attributeValue("technicsType");
			for (int i = 0; i < vec.size(); i++) {
				node = (XWTreeNode) vec.get(i);
				XWTreeObject xo = node.getObject();
				if (xo != null && xo instanceof XWStepTreeObject) {
					XWStepTreeObject xto = (XWStepTreeObject) xo;
					frame.copyDatas.add(xto.getTreeCellData().clone());
				}
			}
		}
	}
}