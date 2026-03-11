package com.glaway.mpm.visual.view.tree;

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

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;


/**
 * <br>Created on 2012-10-19
 * @author chenyunlong
 */
public class VaTreeDragListeners extends TransferHandler implements DragGestureListener, DragSourceListener, DropTargetListener {
   private static final long     serialVersionUID = 5982914048396545207L;
//   private static final CmLogger log              = CmLogger.getLogger(CmTreeDragListeners.class);
   private VaTree                tree             = null;
   private BufferedImage         ghostImage       = null;
   private Vector<VaTreeNode>    dragTreeNodes    = null;
   private VaTreeNode            parentTreeNode   = null;
   private Rectangle2D           ghostRect        = new Rectangle2D.Float();
   private Point                 lastPoint        = new Point();
   private TreePath              lastPath;
   private Point                 ptOffset         = new Point();

   private VaDetect              detect;
   private VaTreeNodeMerger      merger;

   public VaTreeDragListeners(VaTree tree, VaDetect detect, VaTreeNodeMerger merger) {
      this.tree = tree;
      this.detect = detect;
      this.merger = merger;
   }

   public void dragGestureRecognized(DragGestureEvent e) {
      TreePath[] paths = tree.getSelectionPaths();
      if (paths == null || paths.length <= 0)
         return;
      dragTreeNodes = new Vector<VaTreeNode>();

      for (TreePath path : paths) {
         VaTreeNode tempNode = (VaTreeNode) path.getLastPathComponent();
         if (tempNode.isRoot())
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
         panel.paint(g2);
         g2.dispose();
      }
//      log.debug(dragTreeNodes.size());
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
      parentTreeNode = (VaTreeNode) path.getLastPathComponent();

      merger.setParent(parentTreeNode);
      List<VaTreeNode> objList = new ArrayList<VaTreeNode>();
      objList.add(parentTreeNode);

      if (!detect.detectObject(objList)) {
         return false;
      }
      List<VaTreeNode> dragNodes = detect.detectSource(dragTreeNodes);
      merger.doMerger(dragNodes);
      return true;
   }
}
