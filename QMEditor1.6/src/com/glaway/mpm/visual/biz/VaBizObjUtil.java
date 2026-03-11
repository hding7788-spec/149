package com.glaway.mpm.visual.biz;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.media.j3d.BoundingBox;
import javax.media.j3d.Transform3D;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.method.RemoteMethodServer;
import wt.part.PartUsesOccurrence;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartUsageLink;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.util.WTException;
import wt.vc.wip.Workable;

import com.glaway.mpm.visual.bean.VaEPartInstance;
import com.glaway.mpm.visual.bean.VaEPartMaster;
import com.glaway.mpm.visual.bean.VaInstanceData;
import com.glaway.mpm.visual.bean.VaInstanceData4Part;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.bean.VaLightType;
import com.glaway.mpm.visual.bean.VaPartUsesOcc;
import com.glaway.mpm.visual.bean.VaPartWithOcc;
import com.glaway.mpm.visual.control.VaMathUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaSearchHelper;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.ui.VaMainframe;

public class VaBizObjUtil {
	private static final String		SERVER_CLASS	= "ext.ideal.samc.jws.server.CmBizObjUtilSvr";
	private static final VaLogger	log				= VaLogger.getLogger(VaBizObjUtil.class);
	private static Vector3d			vectNull		= new Vector3d(0.0d, 0.0d, 0.0d);
	// private static final CmLightType TYPE_CI;
	// private static final CmLightType TYPE_DLO;

	static {
		// String typeCI =
		// CmSettings.getSection(CmSettings.SECTION_MBOM).get("mbom.instanceData.type.CI",
		// "DCI");
		// TYPE_CI = CmTypeHelper.getLightType(typeCI, true);
		// String typeDLO =
		// CmSettings.getSection(CmSettings.SECTION_MBOM).get("mbom.instanceData.type.DLO",
		// "DLO");
		// TYPE_DLO = CmTypeHelper.getLightType(typeDLO, true);
	}

	public static int getPartSum(VaLightPart ciLightPart, VaLightPart lightPart, String jch) throws Exception,
			InvocationTargetException {
		String method = "getPartSum";
		Class<?>[] types = { VaLightPart.class, VaLightPart.class, String.class };
		Object[] args = { ciLightPart, lightPart, jch };

		return (Integer) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
	}

	public static Vector searchCIUse(String jch, VaLightPart part) throws Exception, InvocationTargetException {
		String method = "searchCIUse";
		Class<?>[] types = { String.class, VaLightPart.class };
		Object[] args = { jch, part };

		return (Vector) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
	}

	public static WTPart getWTPartForDoc(WTDocument doc) {
		WTPart part = null;
		QueryResult qr = null;
		try {
			qr = WTPartHelper.service.getDescribesWTParts(doc);
		} catch (WTException e) {
			e.printStackTrace();
		}
		if (qr.hasMoreElements()) {
			part = (WTPart) qr.nextElement();

		}
		return part;
	}

	public static WTPart createWTPart(VaLightType lightType, String number, String name, WTContainer container,
			String viewName, String eff) throws RemoteException, InvocationTargetException {
		String method = "createWTPart";
		Class<?>[] types = { VaLightType.class, String.class, String.class, WTContainer.class, String.class,
				String.class };
		Object[] args = { lightType, number, name, container, viewName, eff };

		return (WTPart) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
	}

	public static WTPart createAOWTPart(VaLightType lightType, String number, String name, WTContainer container,
			String viewName, String eff, String zw) throws RemoteException, InvocationTargetException {
		String method = "createAOWTPart";
		Class<?>[] types = { VaLightType.class, String.class, String.class, WTContainer.class, String.class,
				String.class, String.class };
		Object[] args = { lightType, number, name, container, viewName, eff, zw };
		return (WTPart) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
	}

	public static WTPart createSubSPSWTPart(VaLightType lightType, String number, String name, WTContainer container,
			String viewName, String eff, String upper) throws RemoteException, InvocationTargetException {
		String method = "createSubSPSWTPart";
		Class<?>[] types = { VaLightType.class, String.class, String.class, WTContainer.class, String.class,
				String.class, String.class };
		Object[] args = { lightType, number, name, container, viewName, eff, upper };
		return (WTPart) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
	}

	public static WTPart createGYZJWTPart(VaLightType lightType, String number, String name, String quantity,
			String nextAssy, String aoh, WTContainer container, String viewName, String eff) throws RemoteException,
			InvocationTargetException {
		String method = "createGYZJWTPart";
		Class<?>[] types = { VaLightType.class, String.class, String.class, String.class, String.class, String.class,
				WTContainer.class, String.class, String.class };
		Object[] args = { lightType, number, name, quantity, nextAssy, aoh, container, viewName, eff };

		return (WTPart) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
	}

	public static void updateGongWei(VaLightPart aoPart, VaLightPart ZwLightPart) throws Exception {
		String method = "updateGongWei";
		Class<?>[] types = { VaLightPart.class, VaLightPart.class };
		Object[] args = { aoPart, ZwLightPart };
		RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
	}

	public static WTPart reNameWTPartAndWTDocument(WTPart part, String name_part, String number_part, WTDocument doc,
			String name_doc, String number_doc) throws Exception {
		String method = "reNameWTPartAndWTDocument";

		Class<?>[] types = { WTPart.class, String.class, String.class, WTDocument.class, String.class, String.class };
		Object[] args = { part, name_part, number_part, doc, name_doc, number_doc };

		return (WTPart) RemoteMethodServer.getDefault().invoke(method, SERVER_CLASS, null, types, args);
	}

	public static WTPart getWTPartFromLightPart(VaLightPart lightPart) {
		WTPart ret = null;

		if (lightPart != null && lightPart.getOid() > 0)
			try {
				ret = (WTPart) VaSearchHelper.search(WTPart.class, lightPart.getOid());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return ret;
	}

	public static VaLightPart buildVaLightPartFromWTPart(WTPart part) {
		VaLightPart ret = VaLightPart.EMPTY_PART;
		if (part != null) {
			String revision = part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue();
			log.debug("part : " + part.getName() + "|| view : " + part.getViewName());
			ret = VaLightPart.newLightPart(part.getNumber(), part.getName(), revision, PersistenceHelper
					.getObjectIdentifier(part.getContainer()).getId());

			try {
				String type = ClientTypedUtility.getTypeIdentifier(part).toExternalForm();
				if (type.startsWith("WCTYPE|"))
					type = type.substring("WCTYPE|".length());
				if (type != null)
					ret.setType(type);
			} catch (WTException e) {
				log.error(e);
			}

			int status = 0;
			long oid = PersistenceHelper.getObjectIdentifier(part).getId();
			long masterOid = PersistenceHelper.getObjectIdentifier(part.getMaster()).getId();
			String state = "";
			try {
				state = part.getLifeCycleState().getDisplay(SessionHelper.getLocale());
			} catch (WTException e) {
				log.error(e);
			}
			Timestamp modifyTime = PersistenceHelper.getModifyStamp(part);
			URL pvURL = null;
			ret.setStatus(status);
			ret.setOid(oid);
			ret.setMasterOid(masterOid);
			ret.setState(state);
			ret.setModifyTime(modifyTime);
			ret.setPvURL(pvURL);
			ret.setView(part.getViewName());
		}
		return ret;
	}

	public static VaInstanceData buildInstanceData(WTPart part, WTPartUsageLink link, PartUsesOccurrence occ) {
		VaLightPart lightPart = buildVaLightPartFromWTPart(part);
		return buildInstanceData(lightPart, link, occ);
	}

	public static VaInstanceData buildInstanceData(VaLightPart lightPart, WTPartUsageLink link, PartUsesOccurrence occ) {
		VaInstanceData ret = null;
		// if (TYPE_CI.getExtType().equals(lightPart.getType()))
		// ret = new CmInstanceData4CI();
		// else if (TYPE_DLO.getExtType().equals(lightPart.getType()))
		// ret = new CmInstanceData4LO();
		// else
		// ret = new CmInstanceData4Part();
		ret = new VaInstanceData4Part();
		// /
		ret.setPartId(lightPart.getOid());
		ret.setMasterId(lightPart.getMasterOid());
		if (link != null) {
			long linkId = PersistenceHelper.getObjectIdentifier(link).getId();
			ret.setLinkId(linkId);
		}
		if (occ != null) {
			long occId = PersistenceHelper.getObjectIdentifier(occ).getId();
			if (occ.getComponentID() == null) {
				ret.setOccId(-1);
			} else {
				ret.setOccId(occ.getComponentID());
			}
//			ret.setOccId(occ.getComponentID());
			ret.setOccIdentifierId(occ.getUsesOccurrenceIdentifier()); // XXX
																		// 需要确认getUsesOccurrenceIdentifier的用途
		}

		return ret;
	}

	// public static ContentHolder updateContent(ContentHolder contentHolder,
	// CmInputStreamData data, ContentRoleType type, String fileName)
	// throws Exception {
	// Class<?>[] types = new Class[]{ContentHolder.class,
	// CmInputStreamData.class, ContentRoleType.class, String.class};
	// Object[] values = new Object[]{contentHolder, data, type, fileName};
	//
	// return (ContentHolder) CmLightweightServiceHelper.invoke("updateContent",
	// SERVER_CLASS, null, types, values);
	// }

	// public static WTPart updatePartContent(WTPart part, CmInputStreamData
	// dataXML, ContentRoleType roleType) throws Exception {
	// return (WTPart) updateContent(part, dataXML, roleType, part.getNumber() +
	// ".xml");
	// Class[] aclass = new Class[]{WTPart.class, CmInputStreamData.class,
	// ContentRoleType.class};
	// Object[] aobj = new Object[]{part, dataXML, roleType};
	// return (WTPart)
	// RemoteMethodServer.getDefault().invoke("updatePartContent", SERVER_CLASS,
	// null, aclass, aobj);
	// }

	// public static WTDocument updateDocumentContent(WTDocument doc,
	// CmInputStreamData dataXML, ContentRoleType roleType) throws Exception {
	// return (WTDocument) updateContent(doc, dataXML, roleType, doc.getNumber()
	// + ".xml");
	// Class[] aclass = new Class[]{WTDocument.class, CmInputStreamData.class,
	// ContentRoleType.class};
	// Object[] aobj = new Object[]{doc, dataStream, roleType};
	// return (WTDocument)
	// RemoteMethodServer.getDefault().invoke("updateDocumentContent",
	// SERVER_CLASS, null, aclass, aobj);
	// }

	// 下载主件或附件
	// public static CmInputStreamData downloadXMLData(ContentHolder holder,
	// ContentRoleType rolType) throws Exception {
	// Class[] aclass = new Class[]{ContentHolder.class, ContentRoleType.class};
	// Object[] aobj = new Object[]{holder, rolType};
	// return (CmInputStreamData)
	// RemoteMethodServer.getDefault().invoke("downloadXMLData", SERVER_CLASS,
	// null, aclass, aobj);
	// }

	// public static CmSearchResultBean buildSearchResultBean(WTDocument doc) {
	// CmSearchResultBean ret = new CmSearchResultBean(false, doc.getNumber(),
	// doc.getName(), doc.getVersionIdentifier().getValue());
	// return ret;
	// }
	//
	// public static CmSearchResultBean buildSearchResultBean(WTPart part) {
	// CmSearchResultBean ret = new CmSearchResultBean(false, part.getNumber(),
	// part.getName(), part.getVersionIdentifier().getValue());
	// return ret;
	// }
	//
	// public static CmSearchGongZhuangResultBean
	// buildSearchGongZhuangResultBean(WTPart part) {
	// CmSearchGongZhuangResultBean ret =
	// new CmSearchGongZhuangResultBean(false, part.getNumber(), part.getName(),
	// part.getVersionIdentifier().getValue());
	// return ret;
	// }
	//
	// public static CmFileChooserResultBean buildFileChooserResultBean(File
	// file) {
	// CmFileChooserResultBean ret = new CmFileChooserResultBean(false,
	// file.getAbsolutePath(), file.getName(), true);
	// return ret;
	// }

	/**
	 * 上传附件
	 *
	 * @param holder
	 * @param bean
	 * @param isData
	 * @param roleType
	 * @throws Exception
	 */
	// public static void upLoadFile(ContentHolder holder,
	// CmFileChooserResultBean bean, CmInputStreamData isData, ContentRoleType
	// roleType)
	// throws Exception {
	// Class[] aclass = new Class[]{ContentHolder.class,
	// CmFileChooserResultBean.class, CmInputStreamData.class,
	// ContentRoleType.class};
	// Object[] aobj = new Object[]{holder, bean, isData, roleType};
	// RemoteMethodServer.getDefault().invoke("upLoadFile", SERVER_CLASS, null,
	// aclass, aobj);
	// }
	//
	// public static void upLoadSMGFile(ContentHolder holder,
	// CmFileChooserResultBean bean, CmInputStreamData isData, ContentRoleType
	// roleType)
	// throws Exception {
	// Class[] aclass = new Class[]{ContentHolder.class,
	// CmFileChooserResultBean.class, CmInputStreamData.class,
	// ContentRoleType.class};
	// Object[] aobj = new Object[]{holder, bean, isData, roleType};
	// RemoteMethodServer.getDefault().invoke("upLoadSMGFile", SERVER_CLASS,
	// null, aclass, aobj);
	// }

	/**
	 * 获取附件信息列表
	 *
	 * @param holder
	 * @param roleType
	 * @return
	 * @throws Exception
	 */
	// @SuppressWarnings("unchecked")
	// public static List<CmFileChooserResultBean>
	// downLoadFileList(ContentHolder holder, ContentRoleType roleType) throws
	// Exception {
	// Class[] aclass = new Class[]{ContentHolder.class, ContentRoleType.class};
	// Object[] aobj = new Object[]{holder, roleType};
	// return (List<CmFileChooserResultBean>)
	// RemoteMethodServer.getDefault().invoke("downLoadFileList", SERVER_CLASS,
	// null, aclass, aobj);
	// }

	/**
	 * 删除附件列表
	 *
	 * @param holder
	 * @param roleType
	 * @param list
	 * @throws Exception
	 * @throws InvocationTargetException
	 */
	// public static void deleteFileList(ContentHolder holder, ContentRoleType
	// roleType, List<CmFileChooserResultBean> list) throws Exception,
	// InvocationTargetException {
	// Class[] aclass = new Class[]{ContentHolder.class, ContentRoleType.class,
	// List.class};
	// Object[] aobj = new Object[]{holder, roleType, list};
	// RemoteMethodServer.getDefault().invoke("deleteFileList", SERVER_CLASS,
	// null, aclass, aobj);
	// }

	/**
	 * 下载附件实体文件
	 *
	 * @param holder
	 * @param roleType
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	// public static Map<String, Object> downLoadFile(ContentHolder holder,
	// ContentRoleType roleType, CmFileChooserResultBean bean) throws Exception
	// {
	// Class[] aclass = new Class[]{ContentHolder.class, ContentRoleType.class,
	// CmFileChooserResultBean.class};
	// Object[] aobj = new Object[]{holder, roleType, bean};
	// return (Map<String, Object>)
	// RemoteMethodServer.getDefault().invoke("downLoadFile", SERVER_CLASS,
	// null, aclass, aobj);
	// }
	public static Workable checkInWorkable(Workable able) throws Exception {
		Class[] aclass = new Class[] { Workable.class, boolean.class };
		Object[] aobj = new Object[] { able, true };
		return (Workable) RemoteMethodServer.getDefault().invoke("checkInOROutWorkable", SERVER_CLASS, null, aclass,
				aobj);
	}

	public static Workable checkOutWorkable(Workable able) throws Exception {
		Class[] aclass = new Class[] { Workable.class, boolean.class };
		Object[] aobj = new Object[] { able, false };
		return (Workable) RemoteMethodServer.getDefault().invoke("checkInOROutWorkable", SERVER_CLASS, null, aclass,
				aobj);
	}

	public static VaTreeNode buildTree(VaPartWithOcc root) {
		VaTreeNode ret = new VaTreeNode(new VaEPartInstance(root.getPart()));

		int rootOccSize = root.getOccurences().size();
		if (rootOccSize > 0) {
			VaPartUsesOcc usesOcc = root.getOccurences().get(0);
			usesOcc.addTreeNode(ret);

			ret.setMatrix(usesOcc.getMatrix());
			ret.setOccId(usesOcc.getInstanceData().getOccId() + "");

			if (null == ret.getParent()) {
				ret.setOccpath(ret.getOccId());
			} else if (((VaTreeNode) (ret.getParent())).getOccpath() == null) {
				ret.setOccpath(((VaTreeNode) ret.getParent()).getOccId() + "+" + ret.getOccId());
			} else {
				ret.setOccpath(((VaTreeNode) ret.getParent()).getOccpath() + "+" + ret.getOccId());
			}

			build(root, rootOccSize, null);
		}
		return ret;
	}

	public static void build(VaPartWithOcc father, int extOccSize, BoundingBox box) {
		if (father == null)
			return;

		Vector<VaPartWithOcc> children = father.getChildren();
		int childSize = children.size();

		if (childSize == 0) {
			Vector wvsBoxes = father.getWvsBboxes();
			int nbBoxes = 0;
			int effectiveBoxes = 0;
			String[] objBoxes = new String[0];
			Vector3d[] objRotations = new Vector3d[0];
			Vector3d[] objTranslations = new Vector3d[0];
			if (wvsBoxes != null) // Representable found
			{
				nbBoxes = wvsBoxes.size();
				objBoxes = new String[nbBoxes];
				objTranslations = new Vector3d[nbBoxes];
				objRotations = new Vector3d[nbBoxes];
				for (int ii = 0; ii < nbBoxes; ii++) {
					String desc[] = (String[]) (wvsBoxes.get(ii));
					String description = desc[0];
					if (description != null) {
						objBoxes[effectiveBoxes] = description;
						String descpos = desc[1];
						if ((descpos == null) || descpos.equals("")) {
							objRotations[effectiveBoxes] = vectNull;
							objTranslations[effectiveBoxes] = vectNull;
						} else {
							StringTokenizer sk = new StringTokenizer(desc[1]);
							Vector3d trans = new Vector3d(Double.parseDouble(sk.nextToken()), Double.parseDouble(sk
									.nextToken()), Double.parseDouble(sk.nextToken()));
							Vector3d rot = new Vector3d(Double.parseDouble(sk.nextToken()), Double.parseDouble(sk
									.nextToken()), Double.parseDouble(sk.nextToken()));
							objRotations[effectiveBoxes] = rot;
							objTranslations[effectiveBoxes] = trans;
						}
						effectiveBoxes++;
					} // end description not null
				} // end for
			} // end Representable found - wvsBoxes not null
			Vector occ = father.getOccurences();
			int occsize = occ.size();
			for (int i = 0; i < occsize; i++) {
				VaPartUsesOcc father_occ = (VaPartUsesOcc) occ.get(i);
				Vector treens = father_occ.getTreeNodes();
				int treenssize = treens.size();
				for (int k = 0; k < treenssize; k++) {
					VaTreeNode father_occ_treen = (VaTreeNode) (treens.get(k));
					boolean testBoxes = true;
					Vector boxes = null;
					if (effectiveBoxes != 0) {
						boxes = new Vector();
						Matrix4d matrix = father_occ_treen.getMatrix();
						testBoxes = false;
						for (int j = 0; j < effectiveBoxes; j++) {
							String s = objBoxes[j];
							StringTokenizer bbox = new StringTokenizer(s);
							if (bbox.countTokens() >= 6) {
								double xmin = Double.parseDouble(bbox.nextToken());
								double ymin = Double.parseDouble(bbox.nextToken());
								double zmin = Double.parseDouble(bbox.nextToken());
								double xmax = Double.parseDouble(bbox.nextToken());
								double ymax = Double.parseDouble(bbox.nextToken());
								double zmax = Double.parseDouble(bbox.nextToken());
								Point3d corner1 = new Point3d(xmin, ymin, zmin);
								Point3d corner2 = new Point3d(xmax, ymax, zmax);
								BoundingBox objBox = new BoundingBox(corner1, corner2);
								Matrix4d matBox = VaMathUtil.vectorsToMatrice4(objRotations[j], objTranslations[j]);
								Matrix4d newMat = VaMathUtil.combineMatrix4(matrix, matBox);
								Vector3d newTrans = VaMathUtil.matrice4ToTrans(newMat);
								Matrix3d mat3 = VaMathUtil.matrix4ToMatrix3(newMat);
								Transform3D transf = new Transform3D(mat3, newTrans, 1);
								objBox.transform(transf);
								boxes.add(objBox);
								log.debug(" DICUtilTree::objBox = " + objBox);
								if ((box == null) || (objBox.intersect(box))) { // coincidence
									testBoxes = true;
									log.debug(" DICUtilTree::    intersected-------------->");
								} else
									log.debug(" DICUtilTree::    NOT INTERSECTED!");
							} else
								log.debug(" DICUtilTree::    bbox in wrong format: " + s);
						} // end for
					} // end if nbBoxes != 0
					if (testBoxes) {
						if (boxes != null && boxes.size() == 0)
							boxes = null; // In order to display or not the
											// Filter by proximity pop-up menu
						father_occ_treen.setBboxes(boxes);
					} else // no coincidence - We set it as killed child ==>
							// will be removed when cleaning
					{
						// father_occ_treen.setHasKilledChild(true);
					}
				} // end for
			}
			return;
		}

		if (childSize > 0) {
			for (int childIndex = 0; childIndex < childSize; childIndex++) {
				VaPartWithOcc child = children.get(childIndex);
				VaLightPart lightPart = child.getPart();

				Vector<VaPartUsesOcc> fatherUsesOccVec = father.getOccurences();
				int fatherOccSize = fatherUsesOccVec.size();
				for (int fatherOccIndex = 0; fatherOccIndex < fatherOccSize; fatherOccIndex++) {
					VaPartUsesOcc fatherUsesOcc = fatherUsesOccVec.get(fatherOccIndex);
					Vector<VaTreeNode> fatherUsesTreeNodes = fatherUsesOcc.getTreeNodes();

					Vector<VaPartUsesOcc> childUsesOccVec = child.getOccurences();
					int childOccSize = childUsesOccVec.size();
					for (VaPartUsesOcc occ : childUsesOccVec) {
						log.debug(occ.getInstanceId());
					}
					int fatherUsesTreeNodeSize = fatherUsesTreeNodes.size();
					for (int i = fatherUsesTreeNodeSize - extOccSize; i < fatherUsesTreeNodeSize; i++) {
						VaTreeNode fatherUsesTreeNode = fatherUsesTreeNodes.get(i);
						Matrix4d fatherMatrix = fatherUsesTreeNode.getMatrix();
						for (int k = 0; k < childOccSize; k++) {
							VaPartUsesOcc childUsesOcc = childUsesOccVec.get(k);
							Matrix4d relMatrix = childUsesOcc.getMatrix();
							Matrix4d newMatrix = VaMathUtil.combineMatrix4(fatherMatrix, relMatrix);

							VaTreeNode childNode;
							// TODO 需要重构，表示按数量显示的标准件
							if (childOccSize == 1 && child.getQuantity() > 1 && child.getChildren().isEmpty()) { // 叶子节点数量大于1，只有1个实例
								// if (childOccSize == 1 &&
								// child.getChildren().isEmpty()) { //
								// 叶子节点数量大于1，只有1个实例
								childNode = new VaTreeNode(new VaEPartMaster(lightPart, child.getQuantity()));
								log.debug("occId1 : " + childNode.getOccId());
							} else
								childNode = new VaTreeNode(new VaEPartInstance(lightPart));
							childNode.setMatrix(newMatrix);
							log.debug("Matrix : " + newMatrix);
							childNode.setRelativeMatrix(relMatrix);
							log.debug("Rel Matrix : " + relMatrix);
							childNode.setOccId(childUsesOcc.getInstanceData().getOccId() + "");
							fatherUsesTreeNode.add(childNode);

							childUsesOcc.addTreeNode(childNode);

							if ((((VaTreeNode) childNode.getParent()).getOccpath()) == null) {
								childNode.setOccpath(((VaTreeNode) childNode.getParent()).getOccId() + "+"
										+ childNode.getOccId());
							} else {
								childNode.setOccpath(((VaTreeNode) childNode.getParent()).getOccpath() + "+"
										+ childNode.getOccId());
							}
						}
					}
				}
				build(child, fatherOccSize, box);
			}
		}
	}
}
