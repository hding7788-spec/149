package com.glaway.mpm.pbombuilder.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmHideOnCloseAction;
import com.glaway.mpm.pbombuilder.data.CmCBTreeMouseAdapter;
import com.glaway.mpm.pbombuilder.data.CmLightPartListCellRenderer;
import com.glaway.mpm.pbombuilder.data.CmLightTypeListCellRenderer;
import com.glaway.mpm.pbombuilder.data.CmScrollPaneCBTree;
import com.glaway.mpm.pbombuilder.impl.CmEBomCISearchImpl;
import com.glaway.mpm.pbombuilder.tree.CmCBTree;
import com.glaway.mpm.pbombuilder.tree.CmCBTreeNode;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmLightPartCBTreeNode;
import com.glaway.mpm.pbombuilder.tree.CmLightType;
import com.glaway.mpm.pbombuilder.util.CmCBTreeRenderer;
import com.glaway.mpm.pbombuilder.util.CmGuiUtil;
import com.glaway.mpm.pbombuilder.util.CmSettings;
import com.glaway.mpm.pbombuilder.util.CmTypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.netmarkets.model.NmOid;

/**
 * <br>Created on 2012-10-17
 * @author chenyunlong
 */
public class CmCISearchDialog extends CmAbstractNewObjectDialog<CmLightPart> {
	   private static final long         serialVersionUID = 7746998609762329242L;
//	   private static final CmLogger     log              = CmLogger.getLogger();

	   private static CmCISearchDialog   instance         = null;
	   private static CmEBomCISearchImpl impl             = CmEBomCISearchImpl.newCmEBomCISearchImpl();

	   // cached objects -> start
	   private TypeIdentifier            tiCI             = null;
	   // cached objects -> end 

	   // actions -> start
	   private CmAction                  actSearch;
	   private CmAction                  actShowStructure;

	   // actions -> end

	   // ui components -> start
	   private JComboBox                 comboType;
	   private JTextField                textNumber;
	   private JTextField                textName;

	   private JButton                   btnSearch;
	   private JList                     listResult;
	   private JButton                   btnShowStructure;

	   private JButton                   btnConfirm;
	   private JButton                   btnCancel;

	   private CmCBTree                  tree;

	   private Window                    owner;

	   // ui components -> end

	   public CmCISearchDialog(Window owner) {
	      super(owner);
	      this.owner = owner;
	      this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

	      this.setModal(true);
	      this.setTitle("查找并添加");
	   }

	   private JSplitPane getMainClientPanel() {
	      JSplitPane splitMain = new JSplitPane();

	      splitMain.setOneTouchExpandable(false);
	      splitMain.setContinuousLayout(true);
	      splitMain.setDividerSize(10);

	      splitMain.setLeftComponent(getMainClientLeftPanel());
	      splitMain.setRightComponent(getMainClientRightPanel());

	      return splitMain;
	   }

	   private JPanel getMainClientLeftPanel() {
	      JPanel ret = new JPanel(new GridBagLayout());

	      final GridBagConstraints gbc = new GridBagConstraints();
	      gbc.insets = new Insets(15, 15, 0, 5);
	      gbc.fill = GridBagConstraints.HORIZONTAL;
	      gbc.weightx = 1;
	      gbc.weighty = 0;
	      gbc.gridx = 0;

	      int row = -1;
	      gbc.insets = new Insets(5, 15, 0, 5);
	      gbc.gridy = ++row;
	      ret.add(getSearchCriteriaPanel(), gbc);

	      gbc.gridy = ++row;
	      gbc.weightx = 0;
	      gbc.fill = GridBagConstraints.NONE;
	      ret.add(btnSearch, gbc);

	      gbc.gridy = ++row;
	      gbc.weightx = 1;
	      gbc.weighty = 1;
	      gbc.fill = GridBagConstraints.BOTH;
	      ret.add(getSearchResultScrollPane(), gbc);

	      gbc.insets = new Insets(5, 15, 5, 5);
	      gbc.gridy = ++row;
	      gbc.weightx = 0;
	      gbc.weighty = 0;
	      gbc.fill = GridBagConstraints.NONE;
	      ret.add(btnShowStructure, gbc);

	      return ret;
	   }

	   private JPanel getMainClientRightPanel() {
	      CmScrollPaneCBTree scrollPaneCBTree = new CmScrollPaneCBTree();
	      tree = scrollPaneCBTree.getTree();
	      tree.setCellRenderer(new CmCBTreeRenderer());
	      tree.addMouseListener(new CmCBTreeMouseAdapter(tree));

	      JPanel ret = new JPanel(new BorderLayout());
	      ret.add(scrollPaneCBTree, BorderLayout.CENTER);
	      return ret;
	   }

	   private JScrollPane getSearchResultScrollPane() {
	      JScrollPane listPane = new JScrollPane();
	      listPane.setViewportView(listResult);
	      listPane.setPreferredSize(new Dimension(260, 200));

	      return listPane;
	   }

	   private JPanel getSearchCriteriaPanel() {
	      JPanel ret = new JPanel(new GridBagLayout());

	      final GridBagConstraints gbcLabel = new GridBagConstraints();
	      gbcLabel.insets = new Insets(0, 0, 5, 0);
	      gbcLabel.fill = GridBagConstraints.HORIZONTAL;
	      gbcLabel.weightx = 0;
	      gbcLabel.weighty = 0;
	      gbcLabel.gridx = 0;

	      final GridBagConstraints gbcContent = new GridBagConstraints();
	      gbcContent.insets = new Insets(0, 0, 5, 0);
	      gbcContent.fill = GridBagConstraints.HORIZONTAL;
	      gbcContent.weightx = 1;
	      gbcContent.weighty = 0;
	      gbcContent.gridx = 1;

	      int row = -1;
	      gbcLabel.gridy = ++row;
	      ret.add(CmGuiUtil.getLabelFor("* 类型："), gbcLabel);
	      gbcContent.gridy = row;
	      ret.add(comboType, gbcContent);

	      gbcLabel.gridy = ++row;
	      ret.add(CmGuiUtil.getLabelFor("编号："), gbcLabel);
	      gbcContent.gridy = row;
	      ret.add(textNumber, gbcContent);

	      gbcLabel.gridy = ++row;
	      ret.add(CmGuiUtil.getLabelFor("名称："), gbcLabel);
	      gbcContent.gridy = row;
	      ret.add(textName, gbcContent);

	      return ret;
	   }

	   private JPanel getActionsPanel() {
	      JPanel panelActions = new JPanel();
	      panelActions.setBorder(BorderFactory.createEtchedBorder());
	      FlowLayout flayout = new FlowLayout(FlowLayout.RIGHT);
	      flayout.setHgap(10);
	      panelActions.setLayout(flayout);
	      panelActions.add(btnConfirm);
	      panelActions.add(btnCancel);
	      return panelActions;
	   }

	   @Override
	   protected void initAction() {
	      actSearch = new CmSearchAction();
	      actShowStructure = new CmShowStructureAction();
	      actCancel = new CmHideOnCloseAction(this);
	   }

	   @Override
	   protected void initComponents() {
	      comboType = new JComboBox();
	      comboType.setRenderer(new CmLightTypeListCellRenderer());

	      textNumber = new JTextField();
	      textName = new JTextField();

	      btnSearch = new JButton(actSearch);

	      listResult = new JList();
	      listResult.setCellRenderer(new CmLightPartListCellRenderer());
	      listResult.setBorder(new BevelBorder(BevelBorder.LOWERED));
	      listResult.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	      listResult.addMouseListener(new MouseAdapter() {
	         public void mouseClicked(MouseEvent e) {
	            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
	               btnShowStructure.doClick();
	            }
	         }
	      });

	      btnShowStructure = new JButton(actShowStructure);

	      btnConfirm = new JButton(actConfirm);
	      btnCancel = new JButton(actCancel);
	   }

	   @Override
	   protected void initDimension() {
	      this.setBounds(CmGuiUtil.getScreenCenter(700, 500));
	   }

	   @Override
	   protected void initLayout() {
	      this.getContentPane().setLayout(new BorderLayout());
	      this.getContentPane().add(getMainClientPanel(), BorderLayout.CENTER);
	      this.getContentPane().add(getActionsPanel(), BorderLayout.SOUTH);
	   }

	   @Override
	   protected void loadInitDatas() {
	      CmSettings settings = CmSettings.getSection(CmSettings.SECTION_MBOM);
	      String[] types = settings.get("ebom.ci.search.types", new String[0]);
	      Vector<CmLightType> vecTypes = new Vector<CmLightType>(types.length);

	      for (String typeDisplayName : types) {
	         CmLightType lightType = CmTypeHelper.getLightType(typeDisplayName, true);
	         if (lightType != CmTypeHelper.NOT_FOUND_TYPE)
	            vecTypes.add(lightType);
	         else{
//	            JOptionPane.showMessageDialog(this, "没有找到类型："+typeDisplayName, "错误！", JOptionPane.ERROR_MESSAGE);
	         }
	      }

	      comboType.setModel(new DefaultComboBoxModel(vecTypes));
	      comboType.updateUI();
	   }

	   private class CmSearchAction extends CmAction {
	      private static final long serialVersionUID = -7133202638839171479L;

	      CmSearchAction() {
	         //super("搜索", new ImageIcon(CmUtil.getImageFromServer("search.gif")));
	         super("搜索");
	      }

	      @Override
	      public void actionPerformed(ActionEvent evt) {
	         CmLightType lightType = (CmLightType) comboType.getSelectedItem();
	         String name = textName.getText();
	         String number = textNumber.getText();

	         try {
	            Vector vecParts = impl.searchParts(lightType, number, name);
	            listResult.removeAll();
	            listResult.setListData(vecParts);
	         } catch (Exception e) {
//	            log.error(e);
	         }
	      }
	   }

	   private class CmShowStructureAction extends CmAction {
	      private static final long serialVersionUID = 2704499885842429226L;

	      CmShowStructureAction() {
	         super("显示结构");
	      }

	      @Override
	      public void actionPerformed(ActionEvent evt) {
	         CmGuiUtil.disable(evt.getSource());

//			if (listResult.getSelectedIndex() < 0) {
//				JOptionPane
//						.showMessageDialog(CmCISearchDialog.this, "请先选中一个部件！", "提示", JOptionPane.INFORMATION_MESSAGE);
//				CmGuiUtil.enable(evt.getSource());
//				return;
//			}

//			CmLightPart part = (CmLightPart) listResult.getSelectedValue();
//	         if (tiCI == null) {
//	            String typeDisplayName = CmSettings.getSection(CmSettings.SECTION_MBOM).get("ebom.structure.stop.type");
//	            tiCI = CmTypeHelper.getLightType(typeDisplayName, true).getTypeIdentifier();
//	         }
	         try {
	            CmLightPartCBTreeNode node = impl.buildStructureStopOn(435107, null);

	            CmCBTreeNode root = (CmCBTreeNode) tree.getModel().getRoot();
	            root.removeAllChildren();
	            root.add(node);

	            tree.updateUI();
	         } catch (RemoteException e1) {
//	            log.error(e1);
	         } catch (InvocationTargetException e1) {
//	            log.error(e1);
	         }

	         CmGuiUtil.enable(evt.getSource());
	      }
	   }

	   /**
	    * @return
	    */
	   @Override
	   public List<CmLightPart> collectObjects() {
	      CmCBTreeNode root = (CmCBTreeNode) tree.getModel().getRoot();

	      String ciLogicType = CmSettings.getSection(CmSettings.SECTION_MBOM).get("ebom.structure.leaf.type");
	      String extTypeCI = CmTypeHelper.getExtType(ciLogicType);

	      Vector<CmLightPart> retVec = new Vector<CmLightPart>();
	      CmCBTreeNode leaf = (CmCBTreeNode) root.getFirstLeaf();
	      while (leaf != null) {
	         if (leaf.isSelected() && leaf.getUserObject() instanceof CmLightPart) {
	            CmLightPart lightPart = (CmLightPart) leaf.getUserObject();
	            if (lightPart.getPartType().equals(extTypeCI))
	               retVec.add(lightPart);
	         }

	         leaf = (CmCBTreeNode) leaf.getNextLeaf();
	      }
	      if (retVec.isEmpty())
	         JOptionPane.showMessageDialog(this, "您没有选中任何DCI节点");

	      return retVec;
	   }
	}
