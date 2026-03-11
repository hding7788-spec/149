package com.glaway.mpm.pbombuilder.pview;

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

import com.glaway.mpm.pbombuilder.impl.CmPViewNode;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.util.CmMathUtil;
import com.glaway.mpm.pbombuilder.util.CmPartStructureUtil;
import com.glaway.mpm.pbombuilder.util.PviewTask;
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

/**
 * <br>
 * Created on 2012-11-21
 * 
 * @author chenyunlong
 */
public class CmPViewNodeGenerator {
	private static final CmLogger log = CmLogger.getLogger();

	private CmPViewNode root;
	private String pvName;

	private List<ObjectIdentifier> treeNodeOidKeyList = new ArrayList<ObjectIdentifier>();
	private static HashMap<Long, URL> id2url = new HashMap<Long, URL>(32);

	private Structure structure;
	private ShapeScene shapeScene;

	public CmPViewNodeGenerator(CmPViewNode root, String pvName) throws Exception, ActorShutdownException, InvalidActorException {
		this.root = root;
		this.pvName = pvName;

		CmPViewImpl pviewImpl = CmPViewFactory.getPViewImpl(this.pvName);

		pviewImpl.waitforClientIntialized();
		this.structure = pviewImpl.getStructure();
		this.shapeScene = pviewImpl.getPanelContext().getShapeScene();

		this.initOidKeyList(root);
		try {
			id2url.putAll(CmPartStructureUtil.getPViewURLHashMap(treeNodeOidKeyList, ".ol"));
		} catch (RemoteException e) {
			log.error(e);
		} catch (InvocationTargetException e) {
			log.error(e.getMessage());
			log.error(e.getCause());
		}
	}

	private void initOidKeyList(CmPViewNode root) {
		getOidKeysFromTreeNode(root, treeNodeOidKeyList, id2url.keySet());
	}

	private void getOidKeysFromTreeNode(CmPViewNode node, Collection<ObjectIdentifier> oidKeys, Collection<Long> excepts) {
		if (node.isLeaf()) {
			long oid = node.getPartOid();
			if (oid > 0 && !excepts.contains(oid))
				oidKeys.add(new ObjectIdentifier(WTPart.class, oid));
		} else {
			Enumeration<CmPViewNode> children = node.children();
			while (children.hasMoreElements()) {
				CmPViewNode child = children.nextElement();
				getOidKeysFromTreeNode(child, oidKeys, excepts);
			}
		}
	}

	public void generatePVStructure(String finishAnimFrameTaskId) {
		CmPViewImpl pviewImpl = CmPViewFactory.getPViewImpl(this.pvName);
		pviewImpl.waitforClientIntialized();

		try {
			CmPViewNode dummyRoot = CmPViewLiteUtil.createDummyPViewNode("Dummy Root");
			dummyRoot.addChild(root);
			ComponentNode theRootComponentNode = getComponentNode(dummyRoot);
			dummyRoot.setComponentNode(theRootComponentNode);
			structure.SetRoot(theRootComponentNode);

			processChildren(dummyRoot);

			ShapeView shapeView = pviewImpl.getPanelContext().getShapeView();
			AsyncEventCB zoomAllAsyncEvent = pviewImpl.getAsyncEvent(finishAnimFrameTaskId);
			shapeView.ZoomAll(zoomAllAsyncEvent.GetAsyncEventIf());
		} catch (Exception e) {
			log.error(e);
		}
	}

	private ComponentNode getComponentNode(CmPViewNode node) throws Exception {
		ComponentNode ret = buildComponentNode(node.getId());
		if (node.getPartOid() > 0) {
			URL url = id2url.get(node.getPartOid());
			if (url != null)
				ret.SetShapeSource(url.toExternalForm(), 0, 0, 0, 1, 1, 1);
		}
		return ret;
	}

	private ComponentNode buildComponentNode(String id) throws Exception {
		ComponentNode ret = structure.CreateComponentNode(id, (byte) 'a');
		return ret;
	}

	private void processChildren(CmPViewNode parent) {
		Enumeration<CmPViewNode> children = parent.children();
		while (children.hasMoreElements()) {
			CmPViewNode child = children.nextElement();

			PviewTask.postTask("mainframe.setStatus", "正在处理节点" + child);
			try {
				if (!child.isLeaf()) {
					// Treat an assembly
					ComponentNode cnParent = parent.getComponentNode();
					ComponentNode cnChild = getComponentNode(child);

					ComponentInstance ciChild = getComponentInstance(cnParent, cnChild, child.getId());
					if (ciChild != null) {
						Instance instChild = ciChild.GetInstance();
						ShapeInstance siChild = shapeScene.CreateShapeInstance(instChild);

						if (siChild != null) {
							siChild.SetVisibility(true);
							child.setComponentNode(cnChild);
						}
					}
				} else {
					// Treat a leaf
					ComponentNode cnParent = parent.getComponentNode();
					Location location = getPviewLocation(child.getMatrix());
					ComponentNode cnChild = (ComponentNode) getComponentNode(child);
					if (cnChild != null) {
						child.setComponentNode(cnChild);
						ComponentInstance ciChild = null;
						ciChild = getComponentInstance(cnParent, cnChild, child.getId());
						if (ciChild != null) {
							ciChild.SetLocation(location);
							ShapeInstance siChild = getShapeInstance(shapeScene, ciChild);
							if (siChild != null) {
								siChild.SetLocation(location);
								siChild.SetVisibility(true);
							}
						}
					}
				}
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
	}

	private ComponentInstance getComponentInstance(ComponentNode aParentCN, ComponentNode aChildCN, String instanceId)
			throws Exception {
		ComponentInstance ret = aParentCN.AddComponentNode(aChildCN, instanceId);
		ret.SetName(instanceId);

		return ret;
	}

	private ShapeInstance getShapeInstance(ShapeScene shapeScene, ComponentInstance aComponentInstance)
			throws Exception {
		ShapeInstance theShapeInstance = shapeScene.CreateShapeInstance(aComponentInstance.GetInstance());

		return theShapeInstance;
	}

	private static Location getPviewLocation(Matrix4d matrix) throws Exception {
		Vector3d v = CmMathUtil.matrice4ToTrans(matrix);
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
			d = CmMathUtil.anglesToMatrice(angles);

			Matrix3d PIrotation = new Matrix3d();
			PIrotation.rotY(Math.PI);
			d.mul(PIrotation); // Rotates of PI
			d.mul(-1D); // mirroring
		} else {
			d = CmMathUtil.matrix4ToMatrix3(matrix);
		}

		// ML start fixed pview issues for volvo
		float[] f = new float[9];
		f = CmMathUtil.getOrientationFromMatrix4d(matrix);
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
}
