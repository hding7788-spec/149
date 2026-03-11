package com.glaway.mpm.pbombuilder.tree;

import java.awt.AlphaComposite;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

import com.glaway.mpm.pbombuilder.data.CmDetect;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.item.CmPasteNodeMenuItem;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

/**
 * <br>Created on 2012-10-19
 * @author chenyunlong
 */
public class CmTreeDragListeners extends TransferHandler implements DragGestureListener, DragSourceListener, DropTargetListener {
   private static final long     serialVersionUID = 5982914048396545207L;
   private static final CmLogger log              = CmLogger.getLogger(CmTreeDragListeners.class);
   private CmTree                tree             = null;
   private BufferedImage         ghostImage       = null;
   private Vector<CmTreeNode>    dragTreeNodes    = null;
   private CmTreeNode            parentTreeNode   = null;
   private Rectangle2D           ghostRect        = new Rectangle2D.Float();
   private Point                 lastPoint        = new Point();
   private TreePath              lastPath;
   private Point                 ptOffset         = new Point();

   private CmDetect              detect;
   private CmTreeNodeMerger      merger;
   private CmTree pbomTree;
   private CmTree ebomTree;

   public CmTreeDragListeners(CmTree tree,CmTree pbomtree, CmDetect detect, CmTreeNodeMerger merger) {
      this.detect = detect;
      this.merger = merger;
      this.ebomTree = tree;
      this.pbomTree = pbomtree;
   }

   public void dragGestureRecognized(DragGestureEvent e) {
	   CmTree cmtree=(CmTree) e.getComponent();
	   TreePath[] paths = null;  
	   if("EBOM".equals(cmtree.getRoot().toString())){
		   paths = ebomTree.getSelectionPaths();
		   this.tree =ebomTree;
	   }
	   else{
		   paths = pbomTree.getSelectionPaths(); 
		   this.tree = pbomTree;
	   }
	   if (paths == null || paths.length <= 0)
		   return;
      dragTreeNodes = new Vector<CmTreeNode>();

      for (TreePath path : paths) {
         CmTreeNode tempNode = (CmTreeNode) path.getLastPathComponent();
         if (tempNode.isRoot() 
        		 || ("PBOM".equals(this.tree.getRoot().toString()) && null ==((CmTreeNode)tempNode.getParent()).getParent()) 
        		 || "assistant".equals(tempNode.getPart().getPartType())
        		 || CmCommonStringUtil.isHasFilingOfParent(tempNode)
        		 || CmCommonStringUtil.isPackageOfParent(tempNode))
            continue;
         dragTreeNodes.add(tempNode);
         Rectangle raPath = tree.getPathBounds(path);
         Point ptDragOrigin = e.getDragOrigin();
         int row = tree.getRowForLocation(ptDragOrigin.x, ptDragOrigin.y);
         JPanel panel =
            (JPanel) (tree.getCellRenderer().getTreeCellRendererComponent(tree, path.getLastPathComponent(), false, tree.isExpanded(path), tree
               .getModel().isLeaf(path.getLastPathComponent()), row, false));

         panel.setSize((int) raPath.getWidth(), (int) raPath.getHeight());
         //�ֶ����õ�ƫ����
         ptOffset.x = 30;
         ptOffset.y = 130;
         this.ghostImage = new BufferedImage((int) raPath.getWidth(), (int) raPath.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
         Graphics2D g2 = ghostImage.createGraphics();
         g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));
//         panel.paint(g2); //拖拽节点的影子
         g2.dispose();
      }
      try {
         e.startDrag(new Cursor(Cursor.HAND_CURSOR),// 
            ghostImage,//
            new Point(5, 5),//
            new StringSelection("--"),//
            this//
            );
      } catch (InvalidDnDOperationException exception) {
      }
   }

   public void dragDropEnd(DragSourceDropEvent arg0) {
      this.ghostImage = null;
   }

   public void dragEnter(DragSourceDragEvent arg0) {

   }

   public void dragEnter(DropTargetDragEvent arg0) {

   }

   public void dragExit(DragSourceEvent arg0) {

      this.ghostImage = null;
   }

   public void dragExit(DropTargetEvent arg0) {

      this.ghostImage = null;
   }

   public void dragOver(DragSourceDragEvent arg0) {

   }

   public void dragOver(DropTargetDragEvent droptargetdragevent) {
      Point pt = droptargetdragevent.getLocation();
      if (pt.equals(lastPoint)) {
         return;
      }
      if (ghostImage != null) {
         Graphics2D g2 = (Graphics2D) tree.getGraphics();

         if (!DragSource.isDragImageSupported()) {
            tree.paintImmediately(ghostRect.getBounds());
            ghostRect.setRect(pt.x - ptOffset.x, pt.y - ptOffset.y, ghostImage.getWidth(), ghostImage.getHeight());
            g2.drawImage((ghostImage), AffineTransform.getTranslateInstance(ghostRect.getX(), ghostRect.getY()), null);
         }
      }
      TreePath path = tree.getClosestPathForLocation(pt.x, pt.y);
      if (!(path == lastPath)) {
         lastPath = path;
      }
   }

   public void drop(DropTargetDropEvent droptargetdropevent) {
      droptargetdropevent.dropComplete(true);
      droptargetdropevent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
      ghostImage = null;
      tree.repaint();
   }

   public void dropActionChanged(DragSourceDragEvent arg0) {}

   public void dropActionChanged(DropTargetDragEvent arg0) {}

   /*
    * TransferHandler
    * 
    * */
   public boolean canImport(TransferHandler.TransferSupport support) {
      if (dragTreeNodes == null)
         return false;
      return dragTreeNodes.size() > 0;
   }

   public boolean importData(TransferHandler.TransferSupport support) {
      if (!canImport(support)) {
         return false;
      }
      JTree.DropLocation dropLocation = (JTree.DropLocation) support.getDropLocation();
      TreePath path = dropLocation.getPath();
      parentTreeNode = (CmTreeNode) path.getLastPathComponent();

      merger.setParent(parentTreeNode);
      List<CmTreeNode> objList = new ArrayList<CmTreeNode>();
      objList.add(parentTreeNode);

      if (!detect.detectObject(objList)) {
         return false;
      }
      List<CmTreeNode> dragNodes = detect.detectSource(dragTreeNodes);
      if(dragNodes.size()==1 && "assistant".equals(dragNodes.get(0).getPart().getPartType())){
    	  JOptionPane.showMessageDialog(tree.getRootPane(), "工艺辅件不能拖拽！");
    	  return false;
      }
      else if(CmPasteNodeMenuItem.isMoveToTheChildNode(dragNodes, objList.get(0))){
    	  //节点不能向该节点的子节点拖拽，工艺中间件下可以挂载任何零件
      	JOptionPane.showMessageDialog(tree.getRootPane(), "不能向自己的子节点拖拽！");
      	return false;
      }
      else if("PBOM".equals(this.tree.getRoot().toString()) && isMoveToParentNode(dragNodes, objList.get(0))){
    	//不能向该节点的直接上级节点拖拽
    	  JOptionPane.showMessageDialog(tree.getRootPane(), "不能向和自己相同的父节点拖拽！");
    	  return false;
      }
      else if(CmPasteNodeMenuItem.isMoveToBrotherNode(dragNodes, objList.get(0))){
    	  //不能向该节点的直接上级或兄弟节点
    	  JOptionPane.showMessageDialog(tree.getRootPane(), "不能向和自己相同的节点拖拽！");
    	  return false;
      }
      else if(CmCommonNodeUtil.checkIsInnerOpertion(objList.get(0), dragNodes)
    		  && CmPasteNodeMenuItem.checkNodeHasChildInList(objList.get(0), dragNodes)){
    	  JOptionPane.showMessageDialog(tree.getRootPane(), "相同结构内部节点不能拖拽！");
    	  return false;
      }
      else if(CmCommonStringUtil.isPackage(objList.get(0)) || CmCommonStringUtil.isPackageOfParent(objList.get(0))){
    	//当前节点或某个父节点是打包结构，不能进行拖拽
    	  JOptionPane.showMessageDialog(tree.getRootPane(), "不能向打包结构拖拽！");
    	  return false;
      }
      else if(!CmCommonNodeUtil.checkNodeHasStructure(objList.get(0)) && !"PBOM".equals(objList.get(0).getParent().toString())){
    	  JOptionPane.showMessageDialog(tree.getRootPane(), "当前节点只能挂工艺辅件！");
    	  return false;
      }else if("PBOM".equals(this.tree.getRoot().toString()) 
    		  && CmCommonStringUtil.isEqual(dragNodes.get(0).getPart().getParentPartNumber(), objList.get(0).getPart().getPartNumber())
			  && objList.get(0).getOccpath().indexOf(dragNodes.get(0).getOccpath().substring(0, dragNodes.get(0).getOccpath().lastIndexOf("+")))==-1){
    	//相同组件下，不存在相同零件的不同实例互换
    	  JOptionPane.showMessageDialog(tree.getRootPane(), "相同组件下的相同零件不能互换位置！");
    	  return false;
      }
      else {
    	  int i = CmCommonNodeUtil.checkIsMoveToParent(objList.get(0), dragNodes.get(0));
    	  if(i==0){
    		  merger.doMerger(dragNodes);
    	  }else if(i==1){
    		//相同组件下，不存在相同零件的不同实例互换
        	  JOptionPane.showMessageDialog(tree.getRootPane(), "中间件只添加相同父节点下同级的零件！");
        	  return false;
    	  }else if(i==2){
    		//相同组件下，不存在相同零件的不同实例互换
        	  JOptionPane.showMessageDialog(tree.getRootPane(), "零件的调整 只往上级调整不往下级调整！");
        	  return false;
    	  }
      }
      return true;
   }
   /**
	 * 不能向自己的直接父节点拖拽
	 * @author chenyunlong
	 * @date  2013-4-18
	 * @param list
	 * @param currentNode
	 * @return
	 *
	 */
	public boolean isMoveToParentNode(List<CmTreeNode> list,CmTreeNode currentNode){
		boolean flag=false;
		for(CmTreeNode cmnode:list){
			if(CmCommonStringUtil.checkNodeIsSame((CmTreeNode)cmnode.getParent(), currentNode)){
				flag=true;
				break;
			}
		}
		return flag;
	}
}
