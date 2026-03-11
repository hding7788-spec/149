package com.glaway.mpm.qmIntf.equipment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.model.EpType;
import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.resource.Constants;
import com.glaway.mpm.util.CommonObserver;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.JavaUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class EpTreePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static VaLogger logger = VaLogger.getLogger(EpTreePanel.class);
	private EpTree epTree;
	private JScrollPane jScrollPanel = new JScrollPane();
	private EpInfoPanel epInfoPanel = new EpInfoPanel();
	private JButton defineOrder = new JButton("自定义排序");
	private JButton resetOrder = new JButton("还原默认排序");
	private NewTechnicsPart frame;

	public static void main(String[] args) {
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin");
		SwingUtil.setLookAndFeel();
		JFrame frame = new JFrame();
		frame.setSize(500, 600);
		frame.setLocation(800, 200);
		frame.setLayout(new BorderLayout());
		EpTreePanel panel = new EpTreePanel(null);
		panel.addObserver(new CommonObserver());
		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		SwingUtil.setMiddle(frame);
	}

	public EpTreePanel(NewTechnicsPart frame) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("初始化设备树");
		this.frame = frame;
		init();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成初始化设备树");
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {

	}

	private JToolBar buildToolBar() {
		JToolBar toolBar = new JToolBar();
		// toolBar.setBackground(VaTheme.VA_TURQUOISE);
		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		// button.setBackground(VaTheme.VA_TURQUOISE);
		// button.setForeground(Color.WHITE);
		toolBar.add(defineOrder);
		toolBar.add(resetOrder);
		// button.setIcon(icon);
		return toolBar;
	}

	private void setHobby() {
		String epHobby = FileUtil.generateHobbyPath();
		if (!new File(epHobby).exists()) {
			return;
		}
		EpTreeNode rootNode = epTree.getRoot();
		Enumeration children = rootNode.children();
		Map<String, EpTreeNode> map = new HashMap<String, EpTreeNode>();
		while (children.hasMoreElements()) {
			Object obj = children.nextElement();
			if(obj instanceof EpTreeNode){
				EpTreeNode epTreeNode = (EpTreeNode) obj;
				map.put(epTreeNode.getName(), epTreeNode);
			}

		}
		BufferedReader br = FileUtil.getBufferedReaderByDirectPath(epHobby,"gbk");
		String line;
		rootNode.removeAllChildren();
		try {
			while ((line = br.readLine()) != null) {
				EpTreeNode epTreeNode = map.get(line);
				if (epTreeNode != null) {
					rootNode.add(epTreeNode);
				}
			}
			epTree.updateUI();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			JavaUtil.closeStream(br);
		}
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		epTree = EpTreeXmlUtil.generateEpTreeRoot(null, epInfoPanel,frame);
		//epTree = EpTreeXmlUtil.generateEpTree(ResourceIntf.getAllEquipments(), epInfoPanel);
		epTree.setRootVisible(true);
		//setHobby();
		// SwingUtil.expandAll(epTree);
		// SwingUtil.expandBeforeLeaf(epTree, epTree.getRoot(), EpNode.class);
		jScrollPanel.setViewportView(epTree);
		defineOrder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String epHobby = FileUtil.generateHobbyPath();
				writePropertiesFileByTree(epHobby);
				JDialog dialog = new CommonDialog(frame);
				Container container = dialog.getContentPane();
				dialog.setResizable(true);
				container.add(new EpSetSelectPanel(dialog,frame));
				dialog.setTitle("自定义排序");
				SwingUtil.setMiddle(dialog);
				dialog.setVisible(true);
				//setHobby();
			}
		});

		resetOrder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (SwingUtil.showConfirmDialog("确认还原默认排序？", Constants.TIP, 2) == 0) {
					String epHobby = FileUtil.generateHobbyPath();
					writePropertiesFileByType(epHobby);
					setHobby();
					epTree.updateUI();
				}
			}
		});
		add(buildToolBar(), BorderLayout.NORTH);
		add(jScrollPanel, BorderLayout.CENTER);
		add(epInfoPanel, BorderLayout.SOUTH);
	}

	public void addObserver(Observer o) {
		epTree.getMouseAdapter().addObserver(o);
	}

	public void deleteObservers() {
		epTree.getMouseAdapter().deleteObservers();
	}

	public void writePropertiesFileByTree(String epHobby) {
		FileUtil.createFile(epHobby);
		BufferedWriter bw = FileUtil.getBufferWriter(epHobby, "gbk");
		Enumeration epTreeNode = epTree.getRoot().children();
		while (epTreeNode.hasMoreElements()) {
			EpTreeNode treeNode = (EpTreeNode) epTreeNode.nextElement();
			try {
				bw.write(treeNode.getName());
				bw.newLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		JavaUtil.closeStream(bw);
	}

	public void writePropertiesFileByType(String epHobby) {
		FileUtil.createFile(epHobby);
		BufferedWriter bw = FileUtil.getBufferWriter(epHobby, "gbk");
		EpType epType = ResourceIntf.getAllEquipments();
		try {
			for (EpType type : epType.getEpTypes()) {
				bw.write(type.getName());
				bw.newLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		JavaUtil.closeStream(bw);
	}

}