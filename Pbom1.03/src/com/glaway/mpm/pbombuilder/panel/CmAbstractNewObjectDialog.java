package com.glaway.mpm.pbombuilder.panel;

import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JDialog;
import com.glaway.mpm.pbombuilder.action.CmAction;


/**
 * 本类是一个JDialog的子类与CmNewObjectBox联合使用时，使用本类父接口的collectObjects方法来获取数据
 * 当在本UI界面上执行actConfirm的动作时，CmNewObjectBox会调用collectObjects以取得数据
 * 关联：CmNewObjectBox ， CmTreeNodeMerger
 * <br>Created on 2012-10-16
 * @author chenyunlong
 */
@SuppressWarnings("rawtypes")
public abstract class CmAbstractNewObjectDialog<T> extends JDialog implements CmNewObject {
	private static final long serialVersionUID = 1L;
	CmCollectBox box;
   JButton        btnConfirm;
   JButton        btnCancel;

   CmAction       actConfirm;
   CmAction       actCancel;  ;

   /**
    * 本抽象类提供确定、取消按钮
    * @param owner
    */
   public CmAbstractNewObjectDialog(Window owner) {
      super(owner);
   }

   public void setBox(CmCollectBox box) {
      if (box != null) {
         this.box = box;
         this.actConfirm = this.box.getConfirmAction();
         this.actCancel = this.box.getCancelAction();
         btnConfirm = new JButton(this.actConfirm);
         btnCancel = new JButton(this.actCancel);
      }
      initUI();
   }

   protected abstract void initAction();

   protected abstract void initLayout();

   protected abstract void initDimension();

   protected abstract void initComponents();

   protected abstract void loadInitDatas();

   protected void initUI() {
      initAction();
      initComponents();
      initDimension();
      initLayout();
      loadInitDatas();
   }

}
