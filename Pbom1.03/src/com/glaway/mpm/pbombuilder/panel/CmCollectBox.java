package com.glaway.mpm.pbombuilder.panel;

import java.awt.event.ActionEvent;
import java.util.List;
import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.tree.CmTreeNodeMerger;
/**
 * 这是一个用于获取数据并对数据进行处理的调度盒子
 * 你需要一个UI界面来生成数据，这个UI应该是一个CmAbstractNewObjectDialog的实现类
 * 另外还需要一个CmTreeNodeMerger类的实现类，用来处理生成的数据。
 * 关联：CmAbstractNewObjectDialog  CmTreeNodeMerger
 * <br>Created on 2011-3-16
 * @author Alex.Huang - 黄勇
 */
public class CmCollectBox {
   private CmAbstractNewObjectDialog dialog;
   private CmTreeNodeMerger            merger;

   public CmCollectBox(CmAbstractNewObjectDialog<?> dialog, CmTreeNodeMerger<?> merger) {
      this.merger = merger;
      this.dialog = dialog;
      this.dialog.setBox(this);
   }

   public void doWork() {
      if (this.dialog != null)
         this.dialog.setVisible(true);
   }

   public CmAction getConfirmAction() {
      return new NewObjectConfirmAction();
   }

   public CmAction getCancelAction() {
      return new NewObjectCancelAction();
   }

   class NewObjectConfirmAction extends CmAction {
      private static final long serialVersionUID = -8898351241639336628L;

      public NewObjectConfirmAction() {
         super("确定");
      }

      @Override
      public void actionPerformed(ActionEvent evt) {
         try {
          List part = dialog.collectObjects();
            if(part==null||part.size()<=0)return;
            if(merger!=null)
               merger.doMerger(part);
            else{
//               CmLogger.getLogger().debug("merger对象为空!");
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         dialog.dispose();
      }

   }

   class NewObjectCancelAction extends CmAction {
      private static final long serialVersionUID = -4099128691302704566L;

      public NewObjectCancelAction() {
         super("取消");
      }

      @Override
      public void actionPerformed(ActionEvent evt) {
         dialog.dispose();
      }

   }

}
