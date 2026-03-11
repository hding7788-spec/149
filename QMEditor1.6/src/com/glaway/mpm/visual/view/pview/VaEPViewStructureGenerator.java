package com.glaway.mpm.visual.view.pview;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import wt.fc.ObjectIdentifier;
import wt.part.WTPart;

import com.glaway.mpm.visual.conf.VaConstants;
import com.glaway.mpm.visual.control.VaMathUtil;
import com.glaway.mpm.visual.control.VaPartStructureUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.ptc.pview.dg.DPoint3D;
import com.ptc.pview.dg.FMat33;
import com.ptc.pview.dg.Location;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.ComponentInstance;
import com.ptc.pview.pvkapp.ComponentNode;
import com.ptc.pview.pvkapp.Instance;
import com.ptc.pview.pvkapp.ShapeInstance;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeView;
import com.ptc.pview.pvkapp.Structure;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;


public class VaEPViewStructureGenerator implements VaPViewGenerator {
   private static final VaLogger     log                = VaLogger.getLogger(VaEPViewStructureGenerator.class);

   private VaTree                    tree;
   private static volatile boolean   isInterrupted      = false;
   private List<ObjectIdentifier>    treeNodeOidKeyList = new ArrayList<ObjectIdentifier>();
   private static HashMap<Long, URL> id2url             = new HashMap<Long, URL>(32);

   private Structure                 structure;
   private ShapeScene                shapeScene;
   private VaPViewImpl               pviewImpl;

   public VaEPViewStructureGenerator(VaTree tree) throws Exception, ActorShutdownException, MessageProtocolException, InvalidActorException {
      this.tree = tree;

      //���ɵ�PV����,Ϊ�µĽṹ׼���ռ�
      VaTreeNode root = tree.getRoot();
      resetChildNodePvWorld(root);

      VaEPViewStructureGenerator.isInterrupted = false;

      if (!tree.getRoot().isLeaf()) {
         initOidKeyList(tree.getRoot()); // ��ȡ�������еı�ѡ�е�Ҷ�ڵ�

         try {
            id2url.putAll(VaPartStructureUtil.getPViewURLHashMap(treeNodeOidKeyList, VaConstants.PVIEW_OL));
         } catch (RemoteException e) {
            log.error(e);
         } catch (InvocationTargetException e) {
            log.error(e.getMessage());
            log.error(e.getCause());
         }
      }
   }
   /**
    * �����һ�ο��ӻ���¼�����ڵ��ϵ�����
    * @param node
    */
   private void resetChildNodePvWorld(VaTreeNode node) {
      node.set_pviewComponentNode(null);
      node.set_pviewComponentInstance(null);
      node.set_pviewShapeInstance(null);
      Enumeration<VaTreeNode> childs = node.children();
      while (childs.hasMoreElements()) {
         VaTreeNode child = childs.nextElement();
         resetChildNodePvWorld(child);
      }
   }
   private void initOidKeyList(VaTreeNode treeNode) {
      getOidKeysFromTreeNode(treeNode, treeNodeOidKeyList, id2url.keySet());
   }

   @SuppressWarnings("unchecked")
   private void getOidKeysFromTreeNode(VaTreeNode treeNode, Collection<ObjectIdentifier> oidKeys, Collection<Long> excepts) {
      if (treeNode.isLeaf() && treeNode.getPart() != null) {
         long oid = treeNode.getPart().getOid();
         if (oid > 0 && !excepts.contains(oid))
            oidKeys.add(new ObjectIdentifier(WTPart.class, treeNode.getPart().getOid()));
      } else {
         Enumeration<VaTreeNode> children = (Enumeration<VaTreeNode>) treeNode.children();
         while (children.hasMoreElements()) {
            VaTreeNode child = children.nextElement();
            if (child.isSelected()) {
               getOidKeysFromTreeNode(child, oidKeys, excepts);
            }
         }
      }
   }

   public void generatePVStructure(String finishFrame) {
      pviewImpl = VaPViewFactory.getPViewImpl4MBOM();
      try {
         pviewImpl.resetPvWorld(this);
      } catch (ConnectionLostException e1) {
         e1.printStackTrace();
      } catch (ActorShutdownException e1) {
         e1.printStackTrace();
      } catch (MessageProtocolException e1) {
         e1.printStackTrace();
      } catch (InvalidActorException e1) {
         e1.printStackTrace();
      }
      pviewImpl.waitforClientIntialized();
      log.debug("============================inPview Generate===================");
      this.structure = pviewImpl.getStructure();
      this.shapeScene = pviewImpl.getPanelContext().getShapeScene();
      try {
         VaTreeNode theRoot = new VaTreeNode("Dummy Root");//tree.getRoot();

         ComponentNode theRootComponentNode = getComponentNode(theRoot);
         theRoot.set_pviewComponentNode(theRootComponentNode);
         structure.SetRoot(theRootComponentNode);

         processTreeRoot(theRoot);

         ShapeView shapeView = pviewImpl.getPanelContext().getShapeView();
         AsyncEventCB zoomAllAsyncEvent = pviewImpl.getAsyncEvent(finishFrame);
         shapeView.ZoomAll(zoomAllAsyncEvent.GetAsyncEventIf());
         //highLight(tree.getRoot());
      
      } catch (Exception e) {
         log.error(e);
      }
   }

   private ComponentNode getComponentNode(VaTreeNode node) throws Exception {
      ComponentNode ret = buildComponentNode(node.getOccId());
      if (node.getPart() != null && node.getPart().getOid() > 0) {
         URL url = id2url.get(node.getPart().getOid());
         if (url != null)
            ret.SetShapeSource(url.toExternalForm(), 0, 0, 0, 1, 1, 1);
      }

      return ret;
   }

   private ComponentNode buildComponentNode(String id) throws Exception {
      ComponentNode ret = structure.CreateComponentNode(id, (byte) 'a');
      return ret;
   }

   private void processTreeRoot(VaTreeNode parent) {
      if (VaEPViewStructureGenerator.isInterrupted)
         return;

      VaTreeNode child = tree.getRoot();

      log.debug("正在处理节点" + child);
//      CmTaskHelper.postTask("mainframe.setStatus", "正在处理节点" + child);
      try {
         if (child.isSelected()) {
        	 if (child.children().hasMoreElements()) {
            	log.debug("+++++++++++++++++++++child.PView...........................");
               // Treat an assembly
               ComponentNode cnParent = parent.get_pviewComponentNode();
               ComponentNode cnChild;

               cnChild = getComponentNode(child);

               ComponentInstance ciChild = getComponentInstance(cnParent, cnChild, child.getOccId());
               if (ciChild != null) {
                  Instance instChild = ciChild.GetInstance();
                  ShapeInstance siChild = shapeScene.CreateShapeInstance(instChild);

                  if (siChild != null) {
                	  log.debug("+++++++++++++++++++++tree.addPVMapping(siChild.GetInstance(), child);...........................");
                     tree.addPVMapping(siChild.GetInstance(), child);
                     siChild.SetVisibility(child.isSelected());
                     child.set_pviewComponentNode(cnChild);
                     child.set_pviewComponentInstance(ciChild);
                     child.set_pviewShapeInstance(siChild);
                  }
//                  child.set_pviewComponentNode(cnChild);
//                  child.set_pviewComponentInstance(ciChild);
//                  child.set_pviewShapeInstance(siChild);
               }
            }
            // Launch recurse
            processChildren(child);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void processChildren(VaTreeNode parent) {
      if (VaEPViewStructureGenerator.isInterrupted)
         return;

      Enumeration children = parent.children();
      while (children.hasMoreElements()) {
         VaTreeNode child = (VaTreeNode) (children.nextElement());

         // 子节点被选中，或者子节点本身的可视化信息已经创建好
//         CmTaskHelper.postTask("mainframe.setStatus", "正在处理节点" + child);
         if (child.isSelected()) {
//         if (child.isSelected()|| child.get_pviewComponentInstance() != null) {
//            try {
//               if (!child.isLeaf()) {
//                  // Treat an assembly
//                  ComponentNode cnParent = parent.get_pviewComponentNode();
//                  ComponentNode cnChild = getComponentNode(child);
//                  log.debug("child.getMatrix()= "+child.getMatrix());
//                  Location location = getPviewLocation(child.getMatrix());
//                  ComponentInstance ciChild = getComponentInstance(cnParent, cnChild, child.getOccId());
//                  if (ciChild != null) {
//                     Instance instChild = ciChild.GetInstance();
//                     ShapeInstance siChild = shapeScene.CreateShapeInstance(instChild);
//                     ciChild.SetLocation(location);
//                     if (siChild != null) {
//                        tree.addPVMapping(siChild.GetInstance(), child);
//                        siChild.SetVisibility(child.isSelected());
//                        siChild.SetLocation(location);
//                        child.set_pviewComponentNode(cnChild);
//                        child.set_pviewComponentInstance(ciChild);
//                        child.set_pviewShapeInstance(siChild);
//                     }
//                  }
//               } else {
//                  // Treat a leaf
//                  ComponentNode cnParent = parent.get_pviewComponentNode();
//                  Location location = getPviewLocation(child.getMatrix());
//                  ComponentNode cnChild = (ComponentNode) getComponentNode(child);
//                  if (cnChild != null) {
//                     child.set_pviewComponentNode(cnChild);
//                     ComponentInstance ciChild = null;
//                     if (child.get_pviewComponentInstance() == null) {
//                        ciChild = getComponentInstance(cnParent, cnChild, child.getOccId());
//                        if (ciChild != null) {
//                           child.set_pviewComponentInstance(ciChild);
//                           ciChild.SetLocation(location);
//                           ShapeInstance siChild = getShapeInstance(shapeScene, ciChild);
//                           // Add the mapping
//                           if (siChild != null) {
//                              // Ml removed
//                        	  siChild.SetLocation(location);
//                              tree.addPVMapping(siChild.GetInstance(), child);
//                              siChild.SetVisibility(child.isSelected());
//                              child.set_pviewShapeInstance(siChild);
//                           }
//                        }
//                     }
//                  }
//               }
//            } catch (MessageProtocolException e) {
//               e.printStackTrace();
//            } catch (ActorShutdownException e) {
//               e.printStackTrace();
//            } catch (InvalidActorException e) {
//               e.printStackTrace();
//            } catch (ConnectionLostException e) {
//               e.printStackTrace();
//            } catch (Exception e) {
//               e.printStackTrace();
//            }
//            // Launch recurse
//            processChildren(child);
        	 processChildPview(child,parent);
         }
      }
   }

   public void processChildPview(VaTreeNode child,VaTreeNode parent){
	   log.debug("正在处理选中节点" + child);
       try {
          if (child.children().hasMoreElements()) { // Treat an assembly                  
             ComponentNode cnParent = parent.get_pviewComponentNode();
             ComponentNode cnChild = child.get_pviewComponentNode();
             if (cnChild == null) {
                cnChild = getComponentNode(child);
                child.set_pviewComponentNode(cnChild);
             }

             ComponentInstance ciChild = child.get_pviewComponentInstance();
             if (ciChild == null) {
                ciChild = getComponentInstance(cnParent, cnChild, child.getOccId());
                child.set_pviewComponentInstance(ciChild);
             }
             if (ciChild != null) {
                ShapeInstance siChild = shapeScene.CreateShapeInstance(ciChild.GetInstance());
                if (siChild != null) {
                   tree.addPVMapping(siChild.GetInstance(), child);
                   siChild.SetVisibility(true);
                   child.set_pviewShapeInstance(siChild);
                }
             }
          } else { // Treat a leaf
       	   processChildrenPiview(child,parent);
          }
       } catch (IOException e) {
          e.printStackTrace();
       } catch (ActorShutdownException e) {
          e.printStackTrace();
       } catch (InvalidActorException e) {
          e.printStackTrace();
       } catch (ConnectionLostException e) {
          e.printStackTrace();
       } catch (Exception e) {
          e.printStackTrace();
       }
       processChildren(child);
   }
   
   public void processChildrenPiview(VaTreeNode child,VaTreeNode parent){
	   if(child.isSelected()){
		   try {
               if (child.children().hasMoreElements()) { // Treat an assembly                  
                  ComponentNode cnParent = parent.get_pviewComponentNode();
                  ComponentNode cnChild = child.get_pviewComponentNode();
                  if (cnChild == null) {
                     cnChild = getComponentNode(child);
                     child.set_pviewComponentNode(cnChild);
                  }

                  ComponentInstance ciChild = child.get_pviewComponentInstance();
                  if (ciChild == null) {
                     ciChild = getComponentInstance(cnParent, cnChild, child.getOccId());
                     child.set_pviewComponentInstance(ciChild);
                  }
                  if (ciChild != null) {
                     ShapeInstance siChild = shapeScene.CreateShapeInstance(ciChild.GetInstance());
                     if (siChild != null) {
                        tree.addPVMapping(siChild.GetInstance(), child);
                        siChild.SetVisibility(true);
                        child.set_pviewShapeInstance(siChild);
                     }
                  }
               } else { // Treat a leaf
                  ComponentNode cnParent = parent.get_pviewComponentNode();
                  Location location = getPviewLocation(child.getMatrix());
                  ComponentNode cnChild = child.get_pviewComponentNode();
                  if (cnChild == null) {
                     cnChild = (ComponentNode) getComponentNode(child);
                     child.set_pviewComponentNode(cnChild);
                  }
                  if (cnChild != null) {
                     ComponentInstance ciChild = child.get_pviewComponentInstance();
                     if (ciChild == null) {
                        ciChild = getComponentInstance(cnParent, cnChild, child.getOccId());
                        child.set_pviewComponentInstance(ciChild);
                     }
                     if (ciChild != null) {
                        ciChild.SetLocation(location);
                        ShapeInstance siChild = getShapeInstance(shapeScene, ciChild);
                        if (siChild != null) {
                           siChild.SetLocation(location);
                           tree.addPVMapping(siChild.GetInstance(), child);
                           siChild.SetVisibility(true);
                           child.set_pviewShapeInstance(siChild);
                        }
                     }
                  }
               }
            } catch (IOException e) {
               e.printStackTrace();
            } catch (ActorShutdownException e) {
               e.printStackTrace();
            } catch (InvalidActorException e) {
               e.printStackTrace();
            } catch (ConnectionLostException e) {
               e.printStackTrace();
            } catch (Exception e) {
               e.printStackTrace();
            }
	   }
   }
   private ComponentInstance getComponentInstance(ComponentNode aParentCN, ComponentNode aChildCN, String instanceId) throws Exception {
//      log.debug("instanceId - " + instanceId +" - " + aParentCN+" - " + aChildCN);
	      ComponentInstance ret = aParentCN.AddComponentNode(aChildCN, instanceId);
	      if(null != ret){
	    	  ret.SetName(instanceId);
	      }
	      return ret;
   }

   private ShapeInstance getShapeInstance(ShapeScene shapeScene, ComponentInstance aComponentInstance) throws Exception {
      ShapeInstance theShapeInstance = shapeScene.CreateShapeInstance(aComponentInstance.GetInstance());
      return theShapeInstance;
   }

   private static Location getPviewLocation(Matrix4d matrix) throws Exception {
      Vector3d v = VaMathUtil.matrice4ToTrans(matrix);
      Matrix3d d;

      // ML start Symmetry Handling
      if (!(matrix.determinant() > 0.0d)) {
         // if the part is right symmetric or the matrix4d determinant is
         // non-positive
         log.debug("Part is right symmetric or its matrix has a non positive determinant.");
         Vector3d angles = new Vector3d();
         angles.x = Math.atan2(matrix.m21, matrix.m22);
         angles.y = -Math.asin(matrix.m20);
         angles.z = Math.atan2(-matrix.m10, matrix.m00);
         d = VaMathUtil.anglesToMatrice(angles);

         Matrix3d PIrotation = new Matrix3d();
         PIrotation.rotY(Math.PI);
         d.mul(PIrotation); // Rotates of PI
         d.mul(-1D); // mirroring
      } else {
         d = VaMathUtil.matrix4ToMatrix3(matrix);
      }

      // ML start fixed pview issues for volvo

      float[] f = new float[9];
      f = VaMathUtil.getOrientationFromMatrix4d(matrix);
      FMat33 theFMat33;
      if (f == null) {
         float m00 = Double.valueOf(d.m00).floatValue();
         float m01 = Double.valueOf(d.m01).floatValue();
         float m02 = Double.valueOf(d.m02).floatValue();
         float m10 = Double.valueOf(d.m10).floatValue();
         float m11 = Double.valueOf(d.m11).floatValue();
         float m12 = Double.valueOf(d.m12).floatValue();
         float m20 = Double.valueOf(d.m20).floatValue();
         float m21 = Double.valueOf(d.m21).floatValue();
         float m22 = Double.valueOf(d.m22).floatValue();
         theFMat33 = new FMat33(m00, m01, m02, m10, m11, m12, m20, m21, m22);
      } else {
         theFMat33 = new FMat33(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8]);
      }

      DPoint3D thePoint3D = new DPoint3D(v.x, v.y, v.z);
      Location theLocation = new Location();
      theLocation.Set(theFMat33, thePoint3D);
      return theLocation;
   }

   public static void desactivate(ShapeInstance shapeInstance) {
      if (shapeInstance != null) {
         try {
            shapeInstance.SetVisibility(false);
         } catch (MessageProtocolException e) {
            e.printStackTrace();
         } catch (ActorShutdownException e) {
            e.printStackTrace();
         } catch (InvalidActorException e) {
            e.printStackTrace();
         } catch (ConnectionLostException e) {
            e.printStackTrace();
         }
      }
   }

   public static void activate(ShapeInstance shapeInstance) {
      if (shapeInstance != null) {
         try {
            shapeInstance.SetVisibility(true);
         } catch (MessageProtocolException e) {
            e.printStackTrace();
         } catch (ActorShutdownException e) {
            e.printStackTrace();
         } catch (InvalidActorException e) {
            e.printStackTrace();
         } catch (ConnectionLostException e) {
            e.printStackTrace();
         }
      }
   }

   public void highLight(VaTreeNode node) {
	      if (node.get_pviewShapeInstance() != null) {
	         try {
	        	 node.get_pviewShapeInstance().SetHighlight(node.isUsed());
	        	 int count = node.getChildCount();

	     			for (int i = 0; i < count; i++) {
	     				VaTreeNode child = (VaTreeNode) node.getChildAt(i);
	     				highLight(child);
	     			}

	         } catch (MessageProtocolException e) {
	            e.printStackTrace();
	         } catch (ActorShutdownException e) {
	            e.printStackTrace();
	         } catch (InvalidActorException e) {
	            e.printStackTrace();
	         } catch (ConnectionLostException e) {
	            e.printStackTrace();
	         }
	      }
	   }


   /**
    * @return
    */
   public VaPViewImpl getPviewImpl() {
      return pviewImpl;
   }
}
