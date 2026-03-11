package com.glaway.mpm.qmIntf.commonString;

import java.awt.BorderLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.qmIntf.symbol.SymbolPanel;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;

class TObserver implements Observer {
	private VaLogger logger = VaLogger.getLogger(getClass());

	@Override
	public void update(Observable o, Object arg) {
		logger.debug(arg);
	}

}

public class CsTreePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JScrollPane jScrollPanel;
	public static CsTree csTree;
	public static SymbolPanel symbolPanel = new SymbolPanel(true);
	private String filePath;
	private static String filePath1 = "D:\\mpm\\resource\\terminology";
	private JSplitPane splitPane = new JSplitPane();

	public static void main(String[] args) {
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin");
		SwingUtil.setLookAndFeel();
		JFrame frame = new JFrame();
		frame.setSize(500, 600);
		frame.setLocation(800, 200);
		frame.setLayout(new BorderLayout());
		CsTreePanel panel = new CsTreePanel(filePath1);
		TObserver observer = new TObserver();
		panel.addObserver(observer);
		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	public CsTreePanel(String filePath) {
		NewTechnicsPart.startAnimFrame.setHeaderMessage("加载常用语树");
		this.filePath = FileUtil.addSeperator(filePath);
		init();
		NewTechnicsPart.startAnimFrame.setHeaderMessage("完成加载常用语树");
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {

	}

	private void initComponents() {
		setLayout(new BorderLayout());
		jScrollPanel = new JScrollPane();
		csTree = CsTreeXmlUtil.justSearchCommonStrings(filePath, true);
		SwingUtil.expandBeforeLeaf(csTree, csTree.getRoot(), CsNode.class);
		csTree.updateUI();
		csTree.setRootVisible(true);
		jScrollPanel.setViewportView(csTree);

		splitPane.setContinuousLayout(true);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerSize(10);
		splitPane.setDividerLocation(SwingUtil.SCREEN_HEIGHT);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(jScrollPanel);
		splitPane.setBottomComponent(symbolPanel);
		splitPane.setAutoscrolls(true);

		add(splitPane, BorderLayout.CENTER);
	}

	public void addObserver(Observer o) {
		csTree.getMouseAdapter().addObserver(o);
		symbolPanel.addObserver(o);
	}

	public void deleteObservers() {
		csTree.getMouseAdapter().deleteObservers();
		symbolPanel.deleteObservers();
	}

	private void initLayout() {

	}

	private void loadInitDatas() {

	}

	private void initActions() {

	}

}
