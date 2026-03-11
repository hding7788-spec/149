package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import org.apache.commons.collections.CollectionUtils;

import wt.fc.ObjectIdentifier;
import wt.part.WTPart;

import com.glaway.mpm.visual.control.VaMathUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.pview.VaEPViewPVSGenerator;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.pview.VaPViewGenerator;
import com.glaway.mpm.visual.view.pview.VaPViewImpl;
import com.glaway.mpm.visual.view.pview.VaPviewInstance;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.ui.VaPViewScenesPanel;
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

public class DPViewStructureGenerator implements VaPViewGenerator {
	private static final VaLogger		log					= VaLogger.getLogger(DPViewStructureGenerator.class);

	private VaTree						tree;
	private static volatile boolean		isInterrupted		= false;
	private List<ObjectIdentifier>		treeNodeOidKeyList	= new ArrayList<ObjectIdentifier>();
	public static HashMap<Long, String>	id2url				= new HashMap<Long, String>(32);

	private Structure					structure;
	private ShapeScene					shapeScene;
	private VaPViewImpl					pviewImpl;

	public DPViewStructureGenerator(VaTree tree) throws Exception, ActorShutdownException, MessageProtocolException,
			InvalidActorException {
		this.tree = tree;

		// ���ɵ�PV����,Ϊ�µĽṹ׼���ռ�
		VaTreeNode root = tree.getRoot();

		resetChildNodePvWorld(root);

		DPViewStructureGenerator.isInterrupted = false;

		if (!tree.getRoot().isLeaf()) {
//			 initOidKeyList(tree.getRoot());
//			 try {
//				 HashMap<Long, URL> urlMap = VaPartStructureUtil.getPViewURLHashMap(treeNodeOidKeyList,VaConstants.PVIEW_OL);
//				 if(urlMap!=null){
//					 Set<Entry<Long, URL>>  entry = urlMap.entrySet();
//					 Iterator<Entry<Long, URL>> it = entry.iterator();
//					 while(it.hasNext()){
//						 Entry<Long, URL> en = it.next();
//						 id2url.put(en.getKey(), en.getValue().toExternalForm());
//					 }
//				 }
//			 } catch (RemoteException e) {
//			 log.error(e);
//			 } catch (InvocationTargetException e) {
//			 log.error(e.getMessage());
//			 log.error(e.getCause());
//			 }
		}
	}

	/**
	 * �����һ�ο��ӻ���¼�����ڵ��ϵ�����
	 *
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
	private void getOidKeysFromTreeNode(VaTreeNode treeNode, Collection<ObjectIdentifier> oidKeys,
			Collection<Long> excepts) {
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

	int		i	= 0;
	String	ff	= "fail";

	public void generatePVStructure(String finishFrame) {
		try {
//			start(finishFrame);
//			 start(ff);
			log.info("finishFrame="+finishFrame);
			 if (i != 1) {
				 ff = finishFrame;
				 Thread.sleep(3000);
				 start(finishFrame);
				 i++;
			 }
		} catch (Exception e) {
			log.error(e);
			// try {
			// log.debug("failed at first time. try again");
			// start(finishFrame);
			// } catch (Exception e1) {
			// log.error(e1);
			// try {
			// log.debug("Still failed... Sucks. and try");
			// start(finishFrame);
			// } catch (Exception e2) {
			// log.error(e2);
			// }
			// }
		}
	}

	private void start(String finishFrame) throws Exception {
		pviewImpl = VaPViewFactory.getPViewImpl4DP();
		pviewImpl.resetPvWorld(this);
		pviewImpl.waitforClientIntialized();
		log.debug("============================inPview Generate===================");
		this.structure = pviewImpl.getStructure();

		this.shapeScene = pviewImpl.getPanelContext().getShapeScene();
		VaTreeNode rootNode = tree.getRoot();

		if (rootNode.getPart().getNumber() == "") {
			Enumeration<VaTreeNode> node = rootNode.children();
			while (node.hasMoreElements()) {
				rootNode = node.nextElement();
				break;
			}
		}

		ComponentNode theRootComponentNode;

		theRootComponentNode = buildComponentNode(rootNode.getPart().getName());
		if (CollectionUtils.isNotEmpty(rootNode.get_pviewInstanceProperties())) {
			for (String[] property : rootNode.get_pviewInstanceProperties())
				theRootComponentNode.AddProperty(property[0], property[1], property[2]);
		}
		pviewImpl.getStructure().SetRoot(theRootComponentNode);
		rootNode.set_pviewComponentNode(theRootComponentNode);

		processChildren(rootNode,"-1");

//		this.processTreeRoot(rootNode);

		ShapeView shapeView = pviewImpl.getPanelContext().getShapeView();
		AsyncEventCB zoomAllAsyncEvent = pviewImpl.getAsyncEvent(finishFrame);

		shapeView.ZoomAll(zoomAllAsyncEvent.GetAsyncEventIf());
	}

	private void processChildren(VaTreeNode parentNode, String pPath) throws Exception {
		if (isInterrupted)
			return;

		@SuppressWarnings("unchecked")
		Enumeration<VaTreeNode> children = parentNode.children();
		while (children.hasMoreElements()) {
			VaTreeNode child = children.nextElement();
			// PviewTask.postTask("mainframe.setStatus","正在处理节点" + child);
			if (child.isSelected()) {

				String childPath = child.getOccpath();// .replace("0+0", pPath);
				if (child.isLeaf() || child.getPart().getNumber().endsWith(".PRT")) {
					// Location location = getPviewLocation(child.getMatrix());
					String tempPath = "";
					if ("true".equals(VaContext.getPbomSaved())) {
						VaTreeNode tempNode = child;
						int count = 0;
						while (tempNode != null && !"-1".equals(tempNode.getOccpath()) && count < 50) {
							tempPath = "/" + tempNode.getOccpath() + tempPath;
							tempNode = (VaTreeNode) tempNode.getParent();
							count++;
						}
						if (count == 49) {
							tempPath = "";
						}
					} else {
						tempPath = childPath.replaceFirst("-1", "");
						tempPath = tempPath.replace("+", "/");
					}

					Instance instemp = null;
					DefaultMutableTreeNode mrTreeRoot = (DefaultMutableTreeNode)VaEPViewPVSGenerator.mirrorTreeForCad.getModel().getRoot();
					Enumeration<DefaultMutableTreeNode> mrTreeChildren = mrTreeRoot.breadthFirstEnumeration();

					while(mrTreeChildren.hasMoreElements()){
						VaPviewInstance mrChild = (VaPviewInstance)(mrTreeChildren.nextElement()).getUserObject();
						String nodeIdpath = mrChild.getIdPath();
						if(nodeIdpath.equals(tempPath)){
							instemp = mrChild.getInstance();
							break;
						}
					}
					Location location = getPviewLocation(child.getMatrix());
					if (instemp != null) {
						location = instemp.GetComponentInstance().GetLocation();
					}
					ComponentNode componentNode = getComponentNode(child);
					ComponentInstance componentInstance = getComponentInstance(parentNode.get_pviewComponentNode(),
							componentNode, child.getOccpath(), child.getPart().getNumber() + " "
									+ child.getPart().getVersion());
					componentInstance.SetLocation(location);
					if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
						for (String[] property : child.get_pviewInstanceProperties())
							componentInstance.AddProperty(property[0], property[1], property[2]);
					}
					ShapeInstance shapeInstance = getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
							componentInstance);
					if (shapeInstance != null) {
						// shapeInstance.SetLocation(location);

						shapeInstance.SetVisibility(true);

						tree.addPVMapping(shapeInstance.GetInstance(), child);
					}

					child.set_pviewComponentInstance(componentInstance);
					child.set_pviewComponentNode(componentNode);
					child.set_pviewShapeInstance(shapeInstance);

					JTree mrTree = VaEPViewPVSGenerator.mirrorTreeForCad;
					DefaultMutableTreeNode dftRoot = (DefaultMutableTreeNode) mrTree.getModel().getRoot();
					Enumeration<DefaultMutableTreeNode> mrChildren = dftRoot.breadthFirstEnumeration();
					while (mrChildren.hasMoreElements()) {
						DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) mrChildren
								.nextElement();
						if (defaultMutableTreeNode.getLevel() <= 3) {
							VaPviewInstance mrInst = (VaPviewInstance) defaultMutableTreeNode.getUserObject();
							if (mrInst.getInstance() == instemp && !defaultMutableTreeNode.isLeaf()) {
								processChildrenInstanceForMrTree(defaultMutableTreeNode, componentNode);
								break;
							}
						}
					}

				} else {
					String tempPath = childPath.replaceFirst("-1", "");
					tempPath = tempPath.replace("+", "/");
					Instance instemp = null;
					DefaultMutableTreeNode mrTreeRoot = (DefaultMutableTreeNode)VaEPViewPVSGenerator.mirrorTreeForCad.getModel().getRoot();
					Enumeration<DefaultMutableTreeNode> mrTreeChildren = mrTreeRoot.breadthFirstEnumeration();

					while(mrTreeChildren.hasMoreElements()){
						VaPviewInstance mrChild = (VaPviewInstance)(mrTreeChildren.nextElement()).getUserObject();
						String nodeIdpath = mrChild.getIdPath();
						if(nodeIdpath.equals(tempPath)){
							instemp = mrChild.getInstance();
							break;
						}
					}
					Location location = getPviewLocation(child.getMatrix());
					if (instemp != null) {
						location = instemp.GetComponentInstance().GetLocation();
					}
					ComponentNode componentNode = buildComponentNode(child.getPart().getNumber());
					ComponentInstance componentInstance = getComponentInstance(parentNode.get_pviewComponentNode(),
							componentNode, child.getOccpath(), child.getPart().getNumber() + " "
									+ child.getPart().getVersion());
					componentInstance.SetLocation(location);
					if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
						for (String[] property : child.get_pviewInstanceProperties())
							componentInstance.AddProperty(property[0], property[1], property[2]);
					}
					// ShapeInstance shapeInstance =
					// getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
					// componentInstance);
					// if (shapeInstance != null) {
					// shapeInstance.SetVisibility(true);
					// tree.addPVMapping(shapeInstance.GetInstance(), child);
					// }
					child.set_pviewComponentNode(componentNode);
					child.set_pviewComponentInstance(componentInstance);
					child.set_pviewShapeInstance(null);

					processChildren(child, pPath);
				}
			}
		}
	}

	private void processChildrenInstanceForMrTree(DefaultMutableTreeNode mrTreeNode, ComponentNode parentNode) {

		try {
			Enumeration<DefaultMutableTreeNode> children = mrTreeNode.children();

			while (children.hasMoreElements()) {
				DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) children.nextElement();
				VaPviewInstance ins = (VaPviewInstance) defaultMutableTreeNode.getUserObject();
				if (defaultMutableTreeNode.isLeaf()) {
					Location location = ins.getLocation();
					ComponentNode componentNode = getComponentNodeForBigAssemble(ins);
					ComponentInstance componentInstance = getComponentInstance(parentNode, componentNode,
							""+ins.hashCode(), ins.getName());
					componentInstance.SetLocation(location);

//					if (CollectionUtils.isNotEmpty(getInstanceProperties(ins.getInstance()))) {
//						for (String[] property : getInstanceProperties(ins.getInstance()))
//							componentInstance.AddProperty(property[0], property[1], property[2]);
//					}

					ShapeInstance shapeInstance = getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
							componentInstance);

					if (shapeInstance != null) {

						shapeInstance.SetVisibility(true);

					}
				} else {
					Location location = ins.getLocation();
					ComponentNode componentNode = getComponentNodeForBigAssemble(ins);
					ComponentInstance componentInstance = getComponentInstance(parentNode, componentNode,
							""+ins.hashCode(), ins.getName());
					componentInstance.SetLocation(location);

//					if (CollectionUtils.isNotEmpty(getInstanceProperties(ins.getInstance()))) {
//						for (String[] property : getInstanceProperties(ins.getInstance()))
//							componentInstance.AddProperty(property[0], property[1], property[2]);
//					}

					ShapeInstance shapeInstance = getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
							componentInstance);

					if (shapeInstance != null) {
						shapeInstance.SetVisibility(true);
					}

					processChildrenInstanceForMrTree(defaultMutableTreeNode, componentNode);
				}
			}
		} catch (MessageProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ComponentNode getComponentNodeForBigAssemble(VaPviewInstance temIns) throws Exception {
		ComponentNode ret = buildComponentNode(temIns.getIdPath());
		String olFile = "";
//		if (temIns.GetComponentNode() != null && temIns.GetComponentNode().GetShapeSource() != null) {
			olFile = new File(System.getProperty("java.io.tmpdir") + "\\" + VaContext.getCurrentPartOid() + "\\")
					.getAbsolutePath() + File.separator + temIns.getOlFileSource();

			log.debug("shapeSource : " + olFile);

//		}
		ret.SetShapeSource(olFile, 0, 0, 0, 0, 0, 0);
		return ret;
	}

	   private void processTreeRoot(VaTreeNode parent) {
		   if (isInterrupted)
		    return;
		        processChildren(parent);
		        Enumeration<VaTreeNode>  children = parent.children();
		        while(children.hasMoreElements()){
			        VaTreeNode child =  children.nextElement();
			  		      log.debug("正在处理节点" + child);
			  		      try {
			  		         if (child.isSelected()) {
			  		        	 if (child.children().hasMoreElements()) {
			  		               // Treat an assembly
			  		               ComponentNode cnParent = parent.get_pviewComponentNode();
			  		               ComponentNode cnChild;
			  		               cnChild = getComponentNode(child);
			  		               ComponentInstance ciChild = getComponentInstance(cnParent, cnChild, child.getOccId(),child.getPart().getName());
			  		               if (ciChild != null) {
			  		                  Instance instChild = ciChild.GetInstance();
			  		                  ShapeInstance siChild = shapeScene.CreateShapeInstance(instChild);
			  		                  if (siChild != null) {
			  		                     tree.addPVMapping(siChild.GetInstance(), child);
			  		                     siChild.SetVisibility(child.isSelected());
			  		                     child.set_pviewComponentNode(cnChild);
			  		                     child.set_pviewComponentInstance(ciChild);
			  		                     child.set_pviewShapeInstance(siChild);
			  		                  }
			  							if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
			  								for (String[] property : child.get_pviewInstanceProperties())
			  									ciChild.AddProperty(property[0], property[1], property[2]);
			  							}
			  		               }
			  		            }
			  		            processChildren(child);
			  		         }
			  		      } catch (Exception e) {
			  		         e.printStackTrace();
			  		      }
		        }

		   }

		   private void processChildren(VaTreeNode parent) {
			  if (isInterrupted)
		         return;
		      Enumeration children = parent.children();
		      while (children.hasMoreElements()) {
		         VaTreeNode child = (VaTreeNode) (children.nextElement());
		         if (child.isSelected()) {
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
		                ciChild = getComponentInstance(cnParent, cnChild, child.getOccId(),child.getPart().getName());
		                child.set_pviewComponentInstance(ciChild);
		             }
		             if (ciChild != null) {
		                ShapeInstance siChild = shapeScene.CreateShapeInstance(ciChild.GetInstance());
		                if (siChild != null) {
		                   tree.addPVMapping(siChild.GetInstance(), child);
		                   siChild.SetVisibility(true);
		                   child.set_pviewShapeInstance(siChild);
		                }
							if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
  								for (String[] property : child.get_pviewInstanceProperties())
  									ciChild.AddProperty(property[0], property[1], property[2]);
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
		                     ciChild = getComponentInstance(cnParent, cnChild, child.getOccId(),child.getPart().getName());
		                     child.set_pviewComponentInstance(ciChild);
		                  }
		                  if (ciChild != null) {
		                     ShapeInstance siChild = shapeScene.CreateShapeInstance(ciChild.GetInstance());
		                     if (siChild != null) {
		                        tree.addPVMapping(siChild.GetInstance(), child);
		                        siChild.SetVisibility(true);
		                        child.set_pviewShapeInstance(siChild);
		                     }
		                     if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
	  								for (String[] property : child.get_pviewInstanceProperties())
	  									ciChild.AddProperty(property[0], property[1], property[2]);
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
		                        ciChild = getComponentInstance(cnParent, cnChild, child.getOccId(),child.getPart().getName());
		                        child.set_pviewComponentInstance(ciChild);
		                     }
		                     if (ciChild != null) {
		                        ciChild.SetLocation(location);
		    					if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
		    						for (String[] property : child.get_pviewInstanceProperties())
		    							ciChild.AddProperty(property[0], property[1], property[2]);
		    					}
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


//	private void processChildren(VaTreeNode parentNode, String pPath) throws Exception {
//		if (isInterrupted)
//			return;
//
//		@SuppressWarnings("unchecked")
//		Enumeration<VaTreeNode> children = parentNode.children();
//		while (children.hasMoreElements()) {
//			VaTreeNode child = children.nextElement();
//			// PviewTask.postTask("mainframe.setStatus","正在处理节点" + child);
//			if (child.isSelected() && !"assistant".equals(child.getPart().getType())) {
//
//				String childPath = child.getOccpath();//.replace("0+0", pPath);
//				if (child.isLeaf()) {
////					Location location = getPviewLocation(child.getMatrix());
////					String tempPath = "";
////					if("true".equals(VaContext.getPbomSaved())){
////						VaTreeNode tempNode = child;
////						int count = 0;
////						while(tempNode != null && !"-1".equals(tempNode.getOccpath()) && count < 50){
////							tempPath = "/"+tempNode.getOccpath() + tempPath;
////							tempNode = (VaTreeNode)tempNode.getParent();
////							count ++;
////						}
////						if(count == 49){
////							tempPath = "";
////						}
////					}else{
////						tempPath = childPath.replaceFirst("-1", "");
////						tempPath = tempPath.replace("+", "/");
////					}
////
////					Instance instemp = VaEPViewPVSGenerator.insMap.get(tempPath);
//					Matrix4d m4d = child.getMatrix();
//					log.info("Node="+child.getPart().getName()+ " Matrix4d="+ m4d);
//					Location location = getPviewLocation(m4d);
////					if(instemp != null){
////						location = instemp.GetComponentInstance().GetLocation();
////					}
//					ComponentNode componentNode = getComponentNode(child);
//					ComponentInstance componentInstance = getComponentInstance(parentNode.get_pviewComponentNode(),
//							componentNode, child.getOccId(),
//							child.getPart().getNumber() + " " + child.getPart().getVersion());
//					componentInstance.SetLocation(location);
//					if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
//						for (String[] property : child.get_pviewInstanceProperties())
//							componentInstance.AddProperty(property[0], property[1], property[2]);
//					}
//					ShapeInstance shapeInstance = getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
//							componentInstance);
//					if (shapeInstance != null) {
//						shapeInstance.SetLocation(location);
//						shapeInstance.SetVisibility(true);
//						tree.addPVMapping(shapeInstance.GetInstance(), child);
//					}
//
//					child.set_pviewComponentInstance(componentInstance);
//					child.set_pviewComponentNode(componentNode);
//					child.set_pviewShapeInstance(shapeInstance);
//
//				} else {
////					String tempPath = childPath.replaceFirst("-1", "");
////					tempPath = tempPath.replace("+", "/");
////					Instance instemp = VaEPViewPVSGenerator.insMap.get(tempPath);
//					Matrix4d m4d = child.getMatrix();
//					log.info("Node="+child.getPart().getName()+ " Matrix4d="+ m4d);
//					Location location = getPviewLocation(m4d);
////					if(instemp != null){
////						location = instemp.GetComponentInstance().GetLocation();
////					}
//					ComponentNode componentNode = buildComponentNode(child.getOccId());
//					ComponentInstance componentInstance = getComponentInstance(parentNode.get_pviewComponentNode(),
//							componentNode, child.getOccId(),
//							child.getPart().getNumber() + " " + child.getPart().getVersion());
//					componentInstance.SetLocation(location);
//					if (CollectionUtils.isNotEmpty(child.get_pviewInstanceProperties())) {
//						for (String[] property : child.get_pviewInstanceProperties())
//							componentInstance.AddProperty(property[0], property[1], property[2]);
//					}
//					ShapeInstance shapeInstance = getShapeInstance(pviewImpl.getPanelContext().getShapeScene(),
//							componentInstance);
//					if (shapeInstance != null) {
//						shapeInstance.SetVisibility(true);
//						shapeInstance.SetLocation(location);
//						tree.addPVMapping(shapeInstance.GetInstance(), child);
//					}
//					child.set_pviewComponentNode(componentNode);
//					child.set_pviewComponentInstance(componentInstance);
//					child.set_pviewShapeInstance(shapeInstance);
//
//					processChildren(child, pPath);
//				}
//
//			}
//		}
//	}

	//
	// public static Location getPviewLocation(Matrix4d matrix) throws Exception
	// {
	// Vector3d v = CmMathUtil.matrice4ToTrans(matrix);
	// Matrix3d d;
	// if(!(matrix.determinant() > 0.0d)) {
	// Vector3d angles = new Vector3d();
	// angles.x = Math.atan2(matrix.m21,matrix.m22);
	// angles.y = -Math.asin(matrix.m20);
	// angles.z = Math.atan2(-matrix.m10,matrix.m00);
	// d = CmMathUtil.anglesToMatrice(angles);
	// Matrix3d PIrotation = new Matrix3d();
	// PIrotation.rotY(Math.PI);
	// d.mul(PIrotation); // Rotates of PI
	// d.mul(-1D); // mirroring
	// } else {
	// d = CmMathUtil.matrix4ToMatrix3(matrix);
	// }
	// // ML start fixed pview issues for volvo
	// float[] f = new float[9];
	// f = CmMathUtil.getOrientationFromMatrix4d(matrix);
	// FMat33 theFMat33;
	// if(f == null) {
	// float m00 = Double.valueOf(d.m00).floatValue();
	// float m01 = Double.valueOf(d.m01).floatValue();
	// float m02 = Double.valueOf(d.m02).floatValue();
	// float m10 = Double.valueOf(d.m10).floatValue();
	// float m11 = Double.valueOf(d.m11).floatValue();
	// float m12 = Double.valueOf(d.m12).floatValue();
	// float m20 = Double.valueOf(d.m20).floatValue();
	// float m21 = Double.valueOf(d.m21).floatValue();
	// float m22 = Double.valueOf(d.m22).floatValue();
	// theFMat33 = new FMat33(m00,m01,m02,m10,m11,m12,m20,m21,m22);
	// } else {
	// theFMat33 = new FMat33(f[0],f[1],f[2],f[3],f[4],f[5],f[6],f[7],f[8]);
	// }
	// // ML end fixed pview issues for volvo
	// DPoint3D thePoint3D = new DPoint3D(v.x,v.y,v.z);
	// Location theLocation = new Location();
	// theLocation.Set(theFMat33,thePoint3D);
	// return theLocation;
	// }

	// private ComponentNode getComponentNode(VaTreeNode node) throws Exception
	// {
	// ComponentNode ret = buildComponentNode(node.getPart().getNumber());
	// String olFile = node.getUserObject().toString();
	// olFileList.add(olFile);
	// ret.SetShapeSource(olFile,0,0,0,0,0,0);
	// return ret;
	// }
	//
	// private ComponentInstance getComponentInstance(ComponentNode
	// aParentCN,ComponentNode aChildCN,String instanceId,String instanceName)
	// throws Exception {
	// ComponentInstance ret = aParentCN.AddComponentNode(aChildCN,instanceId);
	// if(ret != null)
	// ret.SetName(instanceName);
	//
	// return ret;
	// }
	//
	// private ShapeInstance getShapeInstance(ShapeScene
	// shapeScene,ComponentInstance aComponentInstance) throws Exception {
	// ShapeInstance theShapeInstance =
	// shapeScene.CreateShapeInstance(aComponentInstance.GetInstance());
	// return theShapeInstance;
	// }
	//
	// private ComponentNode buildComponentNode(String id) throws Exception {
	// ComponentNode ret =
	// pviewImpl.getStructure().CreateComponentNode(id,(byte) 'a');
	// return ret;
	// }
	//

	private ComponentNode getComponentNode(VaTreeNode node) throws Exception {
		ComponentNode ret = buildComponentNode(node.getOccId());
		log.debug(node.getPart().getNumber() + "   " + node.getPart().getOid());
		if (node.getPart() != null && node.getPart().getOid() > 0) {
			String url  = id2url.get(node.getPart().getOid());
			if (url != null){
				log.debug(url.toString());
				// String shapeSource = url.toString().replace("file://", "");
				log.debug("shapeSource : " + url);
				ret.SetShapeSource(url, 0, 0, 0, 1, 1, 1);
			}
		}

		return ret;
	}

	private ComponentNode buildComponentNode(String id) throws Exception {
		ComponentNode ret = structure.CreateComponentNode(id, (byte) 'a');
		return ret;
	}

//	private void processTreeRoot(VaTreeNode parent) throws Exception {
//		if (isInterrupted)
//			return;
//
//		VaTreeNode child = tree.getRoot();
//
//		// �ӽڵ㱻ѡ�У������ӽڵ㱾��Ŀ��ӻ���Ϣ�Ѿ�������
//		// CmTaskHelper.postTask("mainframe.setStatus", "���ڴ���ڵ�" + child);
//		if (child.isSelected()) {
//			if (!child.isLeaf()) {
//				log.debug("+++++++++++++++++++++child.PView...........................");
//				// Treat an assembly
//				ComponentNode cnParent = parent.get_pviewComponentNode();
//				ComponentNode cnChild;
//
//				cnChild = getComponentNode(child);
//
//				ComponentInstance ciChild = getComponentInstance(cnParent, cnChild, child.getOccId(), "PBOM");
//				cnParent.RemoveChild(ciChild);
//				ciChild = getComponentInstance(cnParent, cnChild, child.getOccId(), "PBOM");
//				if (ciChild != null) {
//					Instance instChild = ciChild.GetInstance();
//					ShapeInstance siChild = shapeScene.CreateShapeInstance(instChild);
//
//					if (siChild != null) {
//						log.debug("+++++++++++++++++++++tree.addPVMapping(siChild.GetInstance(), child);...........................");
//						tree.addPVMapping(siChild.GetInstance(), child);
//						child.set_pviewComponentNode(cnChild);
//						child.set_pviewComponentInstance(ciChild);
//						child.set_pviewShapeInstance(siChild);
//					}
//				}
//			}
//			// Launch recurse
//			processChildren(child, "");
//		}
//	}

	// private void processChildren(VaTreeNode parent) throws Exception {
	// if (DPViewStructureGenerator.isInterrupted)
	// return;
	//
	// Enumeration children = parent.children();
	// while (children.hasMoreElements()) {
	// VaTreeNode child = (VaTreeNode) (children.nextElement());
	// // if(child.getOccId().equals("0")){
	// // continue;
	// // }
	// // �ӽڵ㱻ѡ�У������ӽڵ㱾��Ŀ��ӻ���Ϣ�Ѿ�������
	// // CmTaskHelper.postTask("mainframe.setStatus", "���ڴ���ڵ�" + child);
	// if (child.isSelected()) {
	// if (!child.isLeaf()) {
	//
	// // Treat an assembly
	// ComponentNode cnParent = parent.get_pviewComponentNode();
	// ComponentNode cnChild = getComponentNode(child);
	//
	// ComponentInstance ciChild = getComponentInstance(cnParent, cnChild,
	// child.getOccId(),child.getPart().getNumber());
	// if (ciChild != null) {
	// Instance instChild = ciChild.GetInstance();
	// ShapeInstance siChild = shapeScene.CreateShapeInstance(instChild);
	//
	// if (siChild != null) {
	// tree.addPVMapping(siChild.GetInstance(), child);
	// siChild.SetVisibility(child.isSelected());
	// child.set_pviewComponentNode(cnChild);
	// child.set_pviewComponentInstance(ciChild);
	// child.set_pviewShapeInstance(siChild);
	// }
	// }
	// } else {
	// // Treat a leaf
	// ComponentNode cnParent = parent.get_pviewComponentNode();
	// Location location = getPviewLocation(child.getMatrix());
	// ComponentNode cnChild = (ComponentNode) getComponentNode(child);
	// if (cnChild != null) {
	// child.set_pviewComponentNode(cnChild);
	// ComponentInstance ciChild = null;
	// if (child.get_pviewComponentInstance() == null) {
	// ciChild = getComponentInstance(cnParent, cnChild,
	// child.getOccId(),child.getPart().getNumber());
	// if (ciChild != null) {
	// child.set_pviewComponentInstance(ciChild);
	// ciChild.SetLocation(location);
	// ShapeInstance siChild = getShapeInstance(shapeScene, ciChild);
	// // Add the mapping
	// if (siChild != null) {
	// // Ml removed
	// siChild.SetLocation(location);
	// tree.addPVMapping(siChild.GetInstance(), child);
	// siChild.SetVisibility(child.isSelected());
	// child.set_pviewShapeInstance(siChild);
	// }
	// }
	// }
	// }
	// }
	// // Launch recurse
	// processChildren(child);
	// }
	// }
	// }

	private ComponentInstance getComponentInstance(ComponentNode aParentCN, ComponentNode aChildCN, String instanceId,
			String instanceName) throws Exception {
		log.debug("instanceId - " + instanceId + " - " + aParentCN + " - " + aChildCN+"   instanceName="+instanceName);
		ComponentInstance ret = null;
		try {
			ret = aParentCN.AddComponentNode(aChildCN, instanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(ret!=null)
			ret.SetName(instanceName);
		return ret;
	}

	private ShapeInstance getShapeInstance(ShapeScene shapeScene, ComponentInstance aComponentInstance)
			throws Exception {
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

	/**
	 * @return
	 */
	public VaPViewImpl getPviewImpl() {
		return pviewImpl;
	}
}
