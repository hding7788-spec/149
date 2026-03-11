package com.glaway.mpm.pbombuilder.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmCommonSearchAction;
import com.glaway.mpm.pbombuilder.action.CmEPVSelectionObserver;
import com.glaway.mpm.pbombuilder.action.CmEPViewPVSGenerator;
import com.glaway.mpm.pbombuilder.action.CmPivewAction;
import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeBackProductAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCollapseAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeExpandAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreePackageAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeSearchAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeUnPackageAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeViewProductAction;
import com.glaway.mpm.pbombuilder.util.CmTaskExecutorCallback;
import com.glaway.mpm.pbombuilder.util.PviewTask;

/**
 * <br>Created on 2012-10-16
 * @author chenyunlong
 */
public class CmEBomTreePanel extends CmAbstractPanel {
   private static final long     serialVersionUID = 7452042908382287580L;
   private static final CmLogger log              = CmLogger.getLogger(CmEBomTreePanel.class);
   private CmScrollPaneTree      scrollTreePane;
   private JToolBar              toolBar;

   // actions -> start
   private CmAction              actShowInProductView;
   // actions -> end

   private JButton               btnShowInProductView;
   private List<CmTreeNode>      nodeList         = new ArrayList<CmTreeNode>();
   private List<CmTreeNode>      nodeFromPrvList  = new ArrayList<CmTreeNode>();
   private Window                mainOwner;
   public static CmTree ebomTree;

   private EbomTreeExpandAction ebomTreeExpandAction;
   private JButton               expandButton;
   private EbomTreeCollapseAction ebomTreeCollapseAction;
   private JButton               collapseButton;
   private EbomTreePackageAction ebomTreePackageAction;
   private JButton               packageButton;
   private EbomTreeUnPackageAction ebomTreeUnPackageAction;
   private JButton               unPackageButton;
   private EbomTreeViewProductAction ebomTreeViewProductAction;
   private JButton               viewProductButton;
   private EbomTreeBackProductAction ebomTreeBackProductAction;
   private JButton               backProductButton;
   private EbomTreeSearchAction ebomTreeSearchAction;
   private JButton ebomSearchButton;
   public static JTextField ebomSearchText;

   public List<CmTreeNode> getSelectedCopyNodeList(Object sender, Object params, CmTaskExecutorCallback callback) {
      return nodeList;
   }

   @SuppressWarnings("unchecked")
   public void setSelectedCopyNodeList(Object sender, Object params, CmTaskExecutorCallback callback) {
      this.nodeList = (List<CmTreeNode>) params;
   }

   public List<CmTreeNode> getSelectedPViewNodeList(Object sender, Object params, CmTaskExecutorCallback callback) {
      return nodeFromPrvList;
   }

   @SuppressWarnings("unchecked")
   public void setSelectedPViewNodeList(Object sender, Object params, CmTaskExecutorCallback callback) {
      this.nodeFromPrvList = (List<CmTreeNode>) params;
   }

   public CmEBomTreePanel(Window owner) {
      super();
      this.mainOwner = owner;
      try {
         initUI();
      } catch (CmTaskException e) {
         log.error(e);
      }



   }

   @Override
   protected void initActions() {

   }
   @Override
   protected void initDimension() {
      this.setMinimumSize(new Dimension(180, 360));
   }
   @Override
   protected void initLayout() {
      this.setLayout(new BorderLayout());
      this.add(toolBar, BorderLayout.NORTH);
      this.add(scrollTreePane, BorderLayout.CENTER);
   }
   @Override
   protected void loadInitDatas() {}
   @Override
   protected void initComponents() {
	   CmConnectFrame.startAnimFrame.setHeaderMessage("加载EBOM工具栏按钮");
	   scrollTreePane = new CmScrollPaneTree(new CmTreeNode("EBOM"));
	      //显示可视化
	   actShowInProductView = new CmPivewAction(mainOwner, getTree(), CmEPViewPVSGenerator.class, CmEPVSelectionObserver.class);
//	   actShowInProductView = new CmPivewAction(mainOwner, getTree(), CmEPViewStructureGenerator.class, CmEPVSelectionObserver.class);
	   btnShowInProductView = new JButton(actShowInProductView);
	   btnShowInProductView.setBackground(CmTheme.CM_TURQUOISE);
	   btnShowInProductView.setForeground(Color.WHITE);
	   //展开
	   ebomTreeExpandAction=new EbomTreeExpandAction(getTree());
	   expandButton=new JButton(ebomTreeExpandAction);
	   expandButton.setForeground(Color.WHITE);
	   expandButton.putClientProperty("hideActionText", true);
	   //收起
	   ebomTreeCollapseAction=new EbomTreeCollapseAction(getTree());
	   collapseButton=new JButton(ebomTreeCollapseAction);
	   collapseButton.setForeground(Color.WHITE);
	   collapseButton.putClientProperty("hideActionText", true);
	 //打包
	   ebomTreePackageAction=new EbomTreePackageAction(getTree());
	   packageButton=new JButton(ebomTreePackageAction);
	   packageButton.setForeground(Color.WHITE);
	   packageButton.putClientProperty("hideActionText", true);
	   //拆包
	   ebomTreeUnPackageAction=new EbomTreeUnPackageAction(getTree());
	   unPackageButton=new JButton(ebomTreeUnPackageAction);
	   unPackageButton.setForeground(Color.WHITE);
	   unPackageButton.putClientProperty("hideActionText", true);
	   //查看产品结构
	   ebomTreeViewProductAction=new EbomTreeViewProductAction(getTree());
	   viewProductButton=new JButton(ebomTreeViewProductAction);
	   viewProductButton.setForeground(Color.WHITE);
	   viewProductButton.putClientProperty("hideActionText", true);
	   //返回EBOM
	   ebomTreeBackProductAction=new EbomTreeBackProductAction(getTree());
	   backProductButton=new JButton(ebomTreeBackProductAction);
	   backProductButton.setForeground(Color.WHITE);
	   backProductButton.putClientProperty("hideActionText", true);
	 //快速查询
		ebomTreeSearchAction =new EbomTreeSearchAction(getTree());
		ebomSearchButton = new JButton(ebomTreeSearchAction);
		ebomSearchButton.setForeground(Color.WHITE);
		ebomSearchButton.putClientProperty("hideActionText", true);
		ebomSearchText = new JTextField();
		ebomSearchText.setSize(new Dimension(10, 20));
		ebomSearchText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					CmCommonSearchAction.bomTreeSearchAction(ebomSearchText.getText(), getTree(), "EBOM");
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

	   toolBar = buildToolBar();
	   ebomTree=getTree();

	   CmConnectFrame.startAnimFrame.setHeaderMessage("完成加载EBOM工具栏按钮");
   }

   @Override
   protected void registerTaskExecutor() throws CmTaskException {
//	   log.info("CmEBomTreePanel.registerTaskExecutor()");
	   PviewTask.registerTaskExecutor("CmEBomTreePanel.getSelectedPViewNodeList", this, true);
	   PviewTask.registerTaskExecutor("CmEBomTreePanel.setSelectedPViewNodeList", this, true);
	   PviewTask.registerTaskExecutor("CmEBomTreePanel.getSelectedCopyNodeList", this, true);
	   PviewTask.registerTaskExecutor("CmEBomTreePanel.setSelectedCopyNodeList", this, true);
   }

   @Override
   protected void unregisterTaskExecutor() throws CmTaskException {
	   PviewTask.unregisterTaskExecutor(this);
   }

   public CmTree getTree() {
      return scrollTreePane.getTree();
   }

   private JToolBar buildToolBar() {
	      JToolBar toolBar = new JToolBar();
	      toolBar.setBackground(CmTheme.CM_TURQUOISE);
	      toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE));
	      toolBar.setFloatable(false);
	      toolBar.setRollover(true);
	      toolBar.add(expandButton);
	      toolBar.add(collapseButton);
	      toolBar.add(unPackageButton);
	      toolBar.add(packageButton);
	      toolBar.add(btnShowInProductView);
//	      toolBar.add(viewProductButton);
//	      toolBar.add(backProductButton);
	      toolBar.add(ebomSearchText);
		  toolBar.add(ebomSearchButton);
	      return toolBar;
	   }
}