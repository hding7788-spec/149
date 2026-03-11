package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.tree.CmPartMaster;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmAbstractDialog;
import com.glaway.mpm.pbombuilder.util.CmGuiUtil;

/**
 * 修改零件数量
 * <br>Created on 2012-10-29
 * @author chenyunlong
 */
public class CmChangeQuantityDialog extends CmAbstractDialog {

	private static final long serialVersionUID = -4153090542379221342L;
	   private Window            owner;
	   private CmTree            tree;
	   private CmTreeNode        currNode;
	   private JTextField        textOldQuantity;
	   private JTextField        textNewQuantity;

	   private CmAction          actConfirm;
	   private CmAction          actCancel;
	   private JButton           btnConfirm;
	   private JButton           btnCancel;

	   private CmPartMaster      partMaster;

	   public CmChangeQuantityDialog(Window owner, CmTree tree, CmTreeNode currNode) {
	      super(owner);
	      this.owner = owner;
	      this.tree = tree;
	      this.currNode = currNode;

	      this.setResizable(false);
	      this.setModal(true);
	      this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	      this.setTitle("修改零件数量");
	      try {
	         initUI();
	      } catch (CmTaskException e) {
	         e.printStackTrace();
	      }
	   }

	   /**
	    *
	    */
	   @Override
	   protected void initActions() {
	      actConfirm = new CmConfirmAction(this);
	      actCancel = new CmCancelAction(this);
	   }

	   /**
	    *
	    */
	   @Override
	   protected void initComponents() {
	      textOldQuantity = new JTextField();
	      textOldQuantity.setEnabled(false);

	      textNewQuantity = new JTextField();
	      textNewQuantity.addActionListener(new ActionListener() {
	         public void actionPerformed(ActionEvent e) {
	            if (e.getID() == 1001) {
	               btnConfirm.doClick();
	            }
	         }
	      });

	      btnConfirm = new JButton(actConfirm);
	      btnConfirm.setPreferredSize(new Dimension(45, 26));
	      btnCancel = new JButton(actCancel);
	      btnCancel.setPreferredSize(new Dimension(45, 26));
	   }

	   /**
	    *
	    */
	   @Override
	   protected void initDimension() {
	      this.setBounds(CmGuiUtil.getScreenCenter(300, 170));
	   }

	   /**
	    *
	    */
	   @Override
	   protected void initLayout() {
	      this.getContentPane().setLayout(new BorderLayout());
	      this.add(getEditAndActionPanel(), BorderLayout.CENTER);
	   }

	   private JPanel getEditAndActionPanel() {
	      JPanel ret = new JPanel();
	      ret.setLayout(new BorderLayout());
	      ret.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
	      ret.add(getEditPanel(), BorderLayout.CENTER);
	      ret.add(getActionsPanel(), BorderLayout.SOUTH);
	      return ret;
	   }

	   private JPanel getActionsPanel() {
	      JPanel panelActions = new JPanel();
	      FlowLayout flayout = new FlowLayout(FlowLayout.CENTER);
	      flayout.setHgap(35);
	      panelActions.setLayout(flayout);
	      panelActions.add(btnConfirm);
	      panelActions.add(btnCancel);
	      return panelActions;
	   }

	   private JPanel getEditPanel() {
	      JPanel panelEdit = new JPanel(new GridBagLayout());
	      final GridBagConstraints gbcLabel = new GridBagConstraints();
	      gbcLabel.insets = new Insets(10, 0, 0, 0);
	      gbcLabel.fill = GridBagConstraints.HORIZONTAL;
	      gbcLabel.weightx = 0;
	      gbcLabel.weighty = 0;
	      gbcLabel.gridx = 0;

	      final GridBagConstraints gbcContent = new GridBagConstraints();
	      gbcContent.insets = new Insets(10, 0, 0, 0);
	      gbcContent.fill = GridBagConstraints.HORIZONTAL;
	      gbcContent.weightx = 1;
	      gbcContent.weighty = 0;
	      gbcContent.gridx = 1;

	      int row = -1;
	      gbcLabel.gridy = ++row;
	      panelEdit.add(CmGuiUtil.getLabelFor("当前数量:"), gbcLabel);
	      gbcContent.gridy = row;
	      panelEdit.add(textOldQuantity, gbcContent);

	      gbcLabel.gridy = ++row;
	      panelEdit.add(CmGuiUtil.getLabelFor("修改数量为:"), gbcLabel);
	      gbcContent.gridy = row;
	      panelEdit.add(textNewQuantity, gbcContent);

	      return panelEdit;
	   }

	   /**
	    *
	    */
	   @Override
	   protected void loadInitDatas() {
	      if(this.currNode!=null&&currNode.getListNode()!=null){

	    	  textOldQuantity.setText(currNode.getListNode().size()+"");
//	         Object userObject= currNode.getUserObject();
//	         if(userObject instanceof CmPartMaster){
//	            partMaster=(CmPartMaster)userObject;
//	            textOldQuantity.setText(String.valueOf(partMaster.getQuantity()));
//	         }
	      }
	   }

	   /**
	    * @throws CmTaskException
	    */
	   @Override
	   protected void registerTaskExecutor() throws CmTaskException {}

	   /**
	    * @throws CmTaskException
	    */
	   @Override
	   protected void unregisterTaskExecutor() throws CmTaskException {}

	   class CmConfirmAction extends CmAction {

	      private static final long serialVersionUID = 8569821132898047833L;
	      private Window            owner;

	      public CmConfirmAction(Window owner) {
	         super("确定");
	         this.owner = owner;
	      }

	      @Override
	      public void actionPerformed(ActionEvent evt) {
	         super.actionPerformed(evt);

	        String quantityStr= textNewQuantity.getText();
	        int quantity=Integer.parseInt(quantityStr);
	        if(quantity<=0){
	           JOptionPane.showMessageDialog(owner, "更改数量不正确！");
	           return;
	        }
	        partMaster.setQuantity(quantity);
	        tree.updateUI();
	        owner.dispose();
	      }

	   }

	   private class CmCancelAction extends CmAction {
	      private static final long serialVersionUID = 4887181219508170051L;

	      private Window            owner;

	      public CmCancelAction(Window owner) {
	         super("取消");
	         this.owner = owner;
	      }

	      public void actionPerformed(ActionEvent evt) {
	         owner.dispose();
	      }
	   }

	   public static void main(String[] args) {
	      new CmChangeQuantityDialog(null, null, null).setVisible(true);
	   }
	}
