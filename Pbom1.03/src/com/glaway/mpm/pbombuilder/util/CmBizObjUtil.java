package com.glaway.mpm.pbombuilder.util;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.data.CmEPartInstance;
import com.glaway.mpm.pbombuilder.data.CmPartUsesOcc;
import com.glaway.mpm.pbombuilder.data.CmPartWithOcc;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.panel.CmEPartMaster;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmScrollPaneTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.wcInterface.ErpToWCIntf;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.PartUsesOccurrence;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartUsageLink;
import wt.util.WTException;

import javax.vecmath.Matrix4d;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;


public class CmBizObjUtil {
	private static final CmLogger log = CmLogger.getLogger(CmBizObjUtil.class.getName());
	private static long componentId=-1;
	private static List<Map<String,Object>> byteslist = new ArrayList<Map<String,Object>>();
	private static Map<String,CmTreeNode> nodeMap = new HashMap<String,CmTreeNode>();
	private static Map<String,CmTreeNode> copyMap = new HashMap<String,CmTreeNode>();
	private static Map<String,Integer> occMap = new HashMap<String,Integer>();

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

	public static WTPart getWTPartFromLightPart(CmLightPart lightPart) {
		WTPart ret = null;
		if (lightPart != null && lightPart.getOid() > 0)
			try {
				ret = (WTPart) CmSearchHelper.search(WTPart.class, lightPart.getOid());
			} catch (Exception e) {
				e.printStackTrace();
			}
		return ret;
	}

	public static CmLightPart buildCmLightPartFromWTPart(WTPart part) {
		CmLightPart ret = CmLightPart.EMPTY_PART;
		if (part != null) {
			String revision = part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue();
			ret = CmLightPart.newLightPart(part.getNumber(), part.getName(), revision, PersistenceHelper
					.getObjectIdentifier(part.getContainer()).getId());
			int status = 0;
			long oid = PersistenceHelper.getObjectIdentifier(part).getId();
			long masterOid = PersistenceHelper.getObjectIdentifier(part.getMaster()).getId();

			Timestamp modifyTime = PersistenceHelper.getModifyStamp(part);
			CmIBAHelper helper = new CmIBAHelper(part);
//			String important = helper.getIBAValue("ImportantCategory");
//			if (null != important && "关键件".equals(important)) {
//				ret.setKey(true);
//				ret.setEbomKey(true);
//			} else {
//				ret.setKey(false);
//				ret.setEbomKey(false);
//			}

			//TODO 149 属性
			ret.setMaterialType(helper.getIBAValue("CTYPE"));
			ret.setCtype(helper.getIBAValue("CTYPE"));//零部件分类
			ret.setZzcj(helper.getIBAValue("ZZCJ"));
			ret.setFzcj(helper.getIBAValue("FZCJ"));
			ret.setPindex(helper.getIBAValue("PINDEX"));//产品代号
			ret.setMtype(helper.getIBAValue("MTYPE"));
			ret.setXhph(helper.getIBAValue("XHPH"));
			ret.setJstj(helper.getIBAValue("JSTJ"));
			ret.setCsize(helper.getIBAValue("CSIZE"));
			ret.setCmat(helper.getIBAValue("CMAT"));
			ret.setPtc_material_name(helper.getIBAValue("PTC_MATERIAL_NAME"));//材料名称
			ret.setCmat_up(helper.getIBAValue("CMAT_UP"));//材料上标
			ret.setCmat_down(helper.getIBAValue("CMAT_DOWN"));//材料下标
			ret.setMaterial(helper.getIBAValue("MATERIAL"));//材料
			ret.setPzggbzh(helper.getIBAValue("PZGGBZH"));
			ret.setJstjbzh(helper.getIBAValue("JSTJBZH"));
			ret.setJddj(helper.getIBAValue("JDDJ"));
			ret.setClzt(helper.getIBAValue("CLZT"));
			ret.setZldj(helper.getIBAValue("ZLDJ"));
			ret.setCldw(helper.getIBAValue("CLDW"));
			ret.setZqclmc(helper.getIBAValue("ZQCLMC"));
			ret.setJbclmc(helper.getIBAValue("JBCLMC"));
			ret.setZqclbzh(helper.getIBAValue("ZQCLBZH"));

			ret.setCindex(helper.getIBAValue("CINDEX"));//图号
			ret.setMindex(helper.getIBAValue("MINDEX"));//所属型号
			ret.setSecret(helper.getIBAValue("SECRET"));//密级
			ret.setSetmark(helper.getIBAValue("SETMARK"));//成套件标识
			ret.setAdjustable(helper.getIBAValue("ADJUSTABLE"));//可调整
			ret.setKeycomponent(helper.getIBAValue("KEYCOMPONENT"));//关重件标识
			ret.setPhase_code(helper.getIBAValue("PHASE_CODE"));//当前阶段
			ret.setCompany(helper.getIBAValue("COMPANY"));//设计单位
			ret.setDesigner(helper.getIBAValue("DESIGNER"));//设计者

			//设计资源库应用改造新增属性
			//start
			ret.setShortname(helper.getIBAValue("SHORTNAME"));//物资简称
			ret.setYxjb(helper.getIBAValue("YXJB"));//编码优选级别
			ret.setBmzt(helper.getIBAValue("BMZT"));//编码状态
			ret.setBmlx(helper.getIBAValue("BMLX"));//编码类型
			ret.setStandardnumber(helper.getIBAValue("STANDARDNUMBER"));//标准号
			ret.setMechanicalpropertyorhardness(helper.getIBAValue("MECHANICALPROPERTYORHARDNESS"));//机械性能等级或硬度
			ret.setSurfacetreatment(helper.getIBAValue("SURFACETREATMENT"));//表面处理
			ret.setHeattreatment(helper.getIBAValue("HEATTREATMENT"));//热处理
			ret.setProductform(helper.getIBAValue("PRODUCTFORM"));//产品型式
			ret.setProductlevel(helper.getIBAValue("PRODUCTLEVEL"));//产品等级
			ret.setPlatecscrewform(helper.getIBAValue("PLATECSCREWFORM"));//板拧形式
			ret.setIsimport(helper.getIBAValue("ISIMPORT"));//是否进口
			ret.setSpecialinstruction(helper.getIBAValue("SPECIALINSTRUCTION"));//特殊说明
			ret.setMeasureunit(helper.getIBAValue("MEASUREUNIT"));//计量单位
			ret.setType(helper.getIBAValue("TYPE"));//型号
			ret.setTypestandard(helper.getIBAValue("TYPESTANDARD"));//型号规格
			ret.setQualitylevel(helper.getIBAValue("QUALITYLEVEL"));//质量等级
			ret.setTotalstandard(helper.getIBAValue("TOTALSTANDARD"));//总规范
			ret.setDetailstandard(helper.getIBAValue("DETAILSTANDARD"));//详细规范
			ret.setPackagingform(helper.getIBAValue("PACKAGINGFORM"));//封装形式
			ret.setOutlinesize(helper.getIBAValue("OUTLINESIZE"));//外形尺寸
			ret.setSpecialcondition(helper.getIBAValue("SPECIALCONDITION"));//专用条件
			ret.setExtracondition(helper.getIBAValue("EXTRACONDITION"));//附加协议
			ret.setMattype(helper.getIBAValue("MATTYPE"));//材料类型
			ret.setCmatnumber(helper.getIBAValue("NUMBER"));//材料编号
			ret.setMarknumber(helper.getIBAValue("MARKNUMBER"));//牌号
			ret.setSupplystate(helper.getIBAValue("SUPPLYSTATE"));//供应状态
			ret.setUsestandard(helper.getIBAValue("USESTANDARD"));//采用标准
			//end

			String fzbm = helper.getIBAValue("FZCJ");

			String[] plants = LoadConfig.getInstance().getMainPlant();

			Map<String,Boolean> plantMap = new HashMap<String,Boolean>();

			for(int i=0;i<plants.length;i++){
				if(null!=fzbm&&!"".equals(fzbm)){
					String [] fzbms = fzbm.split("-");
					if(fzbms!=null){
						for(int j=0;j<fzbms.length;j++){
							if(plants[i].equals(fzbms[j])){
								plantMap.put(plants[i], true);
								break;
							}
						}
					}
				}else{
					plantMap.put(plants[i], false);
				}
			}
			ret.setSecondePlant(plantMap);


			//生命週期暫時取EBOM的
			String lifeCycle = part.getLifeCycleState().getDisplay(Locale.CHINA);
			URL pvURL = null;
			ret.setStatus(status);
			ret.setOid(oid);
			ret.setMasterOid(masterOid);
			ret.setModifyTime(modifyTime);
			ret.setPvURL(pvURL);
			ret.setLifecycle(lifeCycle);
			String partType = "WTPart";//PBOMEditorToWCIntf.getTypeName(part)
			ret.setPartType(partType);// PurchasedPart（外购件）
			ret.setE_version(revision);
			// 设置图档属性
//			try {
//				EPMDocument epmDocument = CmSearchHelper.getEPMDocumentByPart(part);
//				if (epmDocument != null) {
//					ret.setEu_number(epmDocument.getNumber());
//					ret.setEu_version(epmDocument.getVersionIdentifier().getValue() + "."
//							+ epmDocument.getIterationIdentifier().getValue());
//				}
//			} catch (WTException e) {
//				e.printStackTrace();
//			}

		}
		return ret;
	}

	public static CmInstanceData buildInstanceDataRoot(CmLightPart lightPart, WTPartUsageLink link, PartUsesOccurrence occ) {
		CmInstanceData ret = null;
		ret = new CmInstanceData4Part();
		String number = lightPart.getPartNumber();
		ret.setPartId(lightPart.getOid());
		ret.setMasterId(lightPart.getMasterOid());
		if (link != null) {
			long linkId = PersistenceHelper.getObjectIdentifier(link).getId();
			ret.setLinkId(linkId);
		}
		if (occ != null) {
//			if(null == occ.getComponentID()){
//				ret.setOccId(0l);
//			}else{
//				ret.setOccId(occ.getComponentID());
//			}
//			long occId = PersistenceHelper.getObjectIdentifier(occ).getId();
			if(null == occ.getComponentID()){
				if(occ.getName() == null || "".equals(occ.getName())) {
					ret.setOccId(PersistenceHelper.getObjectIdentifier(occ).getId()+"");
				} else {
					ret.setOccId(occ.getName());
				}
			}else{
				ret.setOccId(occ.getComponentID()+"");
			}

			if("".equals(ret.getOccId().trim())){
				Integer occid = occMap.get(number);
				if(occid == null){
					occid = 0;
				}
				ret.setOccId(number + "$@"+ (++occid));
				occMap.put(number, occid);
			}

			ret.setOccIdentifierId(occ.getUsesOccurrenceIdentifier());
			// 需要确认getUsesOccurrenceIdentifier的用途
		}else{
			ret.setOccId("-1");
		}
		return ret;
	}

	public static CmInstanceData buildInstanceData(CmLightPart lightPart, WTPartUsageLink link, PartUsesOccurrence occ) {
		CmInstanceData ret = null;
		ret = new CmInstanceData4Part();

		ret.setPartId(lightPart.getOid());
		ret.setMasterId(lightPart.getMasterOid());
		String number = lightPart.getPartNumber();
		if (link != null) {
			long linkId = PersistenceHelper.getObjectIdentifier(link).getId();
			ret.setLinkId(linkId);
		}
		if (occ != null) {
//			if(null == occ.getComponentID()){
//				ret.setOccId(0l);
//			}else{
//				ret.setOccId(occ.getComponentID());
//			}
//			long occId = PersistenceHelper.getObjectIdentifier(occ).getId();
			if(null == occ.getComponentID()){
				if(occ.getName() == null || "".equals(occ.getName())) {
					ret.setOccId(PersistenceHelper.getObjectIdentifier(occ).getId()+"");
				} else {
					ret.setOccId(occ.getName());
				}
			}else{
				ret.setOccId(occ.getComponentID()+"");

			}

			if("".equals(ret.getOccId().trim())){
				Integer occid =  occMap.get(number);
				if(occid == null){
					occid = 0;
				}
				ret.setOccId(number + "$@"+ (++occid));
				occMap.put(number, occid);
			}
			ret.setOccIdentifierId(occ.getUsesOccurrenceIdentifier());
			// 需要确认getUsesOccurrenceIdentifier的用途
		}else{
			Integer occid =  occMap.get(number);
			if(occid == null){
				occid = 0;
			}
			ret.setOccId(number + "$@"+ (++occid));
			occMap.put(number, occid);
		}
		return ret;
	}
	/**
	 * 将EBOM复制到PBOM
	 * @author chenyunlong
	 * @date  2013-7-25
	 * @param node
	 * @return
	 *
	 */
//	public static CmTreeNode convertEBomNode2MBomNode(CmTreeNode node) {
//		CmTreeNode ret = CmCommonStringUtil.copyCmTreeNode(node);
//		// 递归处理子节点
//		Enumeration children = node.children();
//		while (children.hasMoreElements()) {
//			CmTreeNode child = (CmTreeNode) children.nextElement();
//			byte[] bytes = null;
//			CmTreeNode childNode = null;
//			WTPart part = CmCommonStringUtil.isHasPlanning(child, false);
//			if(null != part){
//				bytes =PBOMEditorToWCIntf.getPBOMXml(String.valueOf(part.getPersistInfo().getObjectIdentifier().getId()));
//			}
//			if(null == bytes || bytes.length == 0){
//				childNode = convertEBomNode2MBomNode(child);
//				ret.add(childNode);
//			}else{
//				CmXmlUtil xml = new CmXmlUtil();
//				childNode = xml.buildTreeWithBytes(bytes);
//				childNode.setOccId(child.getOccId());
//				childNode.setOccpath(child.getOccpath());
//				childNode.getPart().setParentPartNumber(child.getPart().getParentPartNumber());
//				ret.add(childNode);
////				rebuildOccpath(childNode);
//				rebuildOccpathFromXmlToTree(childNode,child.getOccpath());
//				addChildNode(childNode);
//			}
//		}
//		return ret;
//	}

	public static CmTreeNode convertEBomNode2MBomNode(CmTreeNode node, boolean isMbom) {
		if (!isMbom) { // EBOM
			CmTreeNode ret = CmCommonStringUtil.copyCmTreeNode(node);
			CmTreeNode mret = CmScrollPaneTree.pbomMap.get(ret);
			if (mret != null) {
				ret.getPart().setHasXml(mret.getPart().isHasXml());
			}
			// 递归处理子节点
			Enumeration children = node.children();
			while (children.hasMoreElements()) {
				CmTreeNode child = (CmTreeNode) children.nextElement();
				CmTreeNode childNode = null;
				childNode = convertEBomNode2MBomNode(child, isMbom);
				mret = CmScrollPaneTree.pbomMap.get(ret);
				if (mret != null) {
					childNode.getPart().setHasXml(mret.getPart().isHasXml());
				}
				ret.add(childNode);

			}
			return ret;
		} else { // MBOM
			// CmTreeNode ret = CmCommonStringUtil.copyCmTreeNode(node);
			// 递归处理子节点
			byte[] pbytes = null;
			CmTreeNode pchildNode = null;
			WTPart ppart = CmCommonStringUtil.isHasPlanning(node, false);
			if (null != ppart) {
				pbytes = PBOMEditorToWCIntf.getPBOMXml(String.valueOf(ppart.getPersistInfo().getObjectIdentifier().getId()));
			}
			if (null == pbytes || pbytes.length == 0) {
				pchildNode = CmCommonStringUtil.copyCmTreeNode(node);
				if (null != ppart) {
					CmLightPart cmLighePart = buildCmLightPartFromWTPart(ppart);
					//设置pbom工艺数量和Ebom一致
					cmLighePart.setGysl(String.valueOf(node.getPart().getUseCount()));
					cmLighePart.setUseCount_805(String.valueOf(node.getPart().getUseCount()));
					pchildNode.setPart(cmLighePart);
//					CmIBAHelper helper = new CmIBAHelper(ppart);
//					try {
//						String clbm = helper.getIBAValue(ppart, "CLBM");
//						Wzk wzk = ErpToWCIntf.getWzkByInvcode("01", clbm);
//						if (wzk != null) {
//							pchildNode.getPart().setWzk(wzk);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
				}

				Enumeration children = node.children();
				while (children.hasMoreElements()) {
					CmTreeNode child = (CmTreeNode) children.nextElement();
					byte[] bytes = null;
					CmTreeNode childNode = null;
					WTPart part = CmCommonStringUtil.isHasPlanning(child, false);
					if(null != part){
						bytes =PBOMEditorToWCIntf.getPBOMXml(String.valueOf(part.getPersistInfo().getObjectIdentifier().getId()));
					}
					if(null == bytes || bytes.length == 0){
						childNode = convertEBomNode2MBomNode(child,isMbom);
						pchildNode.add(childNode);
					}else{
						CmXmlUtil xml = new CmXmlUtil();
						childNode = xml.buildTreeWithBytes(bytes);
						childNode.setOccId(child.getOccId());
						childNode.setOccpath(child.getOccpath());
						childNode.getPart().setParentPartNumber(child.getPart().getParentPartNumber());
						//设置pbom工艺数量和Ebom一致
						childNode.getPart().setGysl(String.valueOf(node.getPart().getUseCount()));
						childNode.getPart().setUseCount_805(String.valueOf(node.getPart().getUseCount()));
						pchildNode.add(childNode);
						copyNewNodeAndRebuildOccpath(childNode,pchildNode.getOccpath());
//						rebuildOccpath(childNode);
						rebuildOccpathFromXmlToTree(childNode,child.getOccpath());
						addChildNode(childNode);
					}
				}

			}else{
				CmXmlUtil xml = new CmXmlUtil();
				pchildNode = xml.buildTreeWithBytes(pbytes);
				pchildNode.getPart().setHasXml(true);
				pchildNode.setOccId(node.getOccId());
				pchildNode.setOccpath(node.getOccpath());
				pchildNode.getPart().setParentPartNumber(node.getPart().getParentPartNumber());
				//设置pbom工艺数量和Ebom一致
				pchildNode.getPart().setGysl(String.valueOf(node.getPart().getUseCount()));
				pchildNode.getPart().setUseCount_805(String.valueOf(node.getPart().getUseCount()));
				copyNewNodeAndRebuildOccpath(pchildNode,pchildNode.getOccpath());
				rebuildOccpathFromXmlToTree(pchildNode,pchildNode.getOccpath());
				CmBizObjUtil.addChildNode(pchildNode);
			}
			return pchildNode;


//			return ret;
		}



	}

	public static CmTreeNode convertEBomNode2MBomNode2(CmTreeNode node,boolean isMbom) {
		if(!isMbom){ // EBOM
			CmTreeNode ret = CmCommonStringUtil.copyCmTreeNode(node);
//			copyNewNodeAndRebuildOccpath(ret,ret.getOccpath());
			WTPart ppart = CmCommonStringUtil.isHasPlanning(node, false);
			if(null!=ppart){
				ret.setPart(buildCmLightPartFromWTPart(ppart)) ;
				CmIBAHelper helper = new CmIBAHelper(ppart);
				try {
					String clbm = helper.getIBAValue(ppart, "CLBM");
					Wzk wzk = ErpToWCIntf.getWzkByInvcode("01", clbm);
					if(wzk!=null){
						ret.getPart().setWzk(wzk);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			CmTreeNode mret = CmScrollPaneTree.pbomMap.get(ret);
			if(mret!=null){
				ret.getPart().setHasXml(mret.getPart().isHasXml());
			}

			// 递归处理子节点
			Enumeration children = node.children();
			while (children.hasMoreElements()) {
				CmTreeNode child = (CmTreeNode) children.nextElement();
				CmTreeNode childNode = null;
				childNode = convertEBomNode2MBomNode(child,isMbom);
				mret = CmScrollPaneTree.pbomMap.get(ret);
				if(mret!=null){
					childNode.getPart().setHasXml(mret.getPart().isHasXml());
				}
				ret.add(childNode);

			}
			return ret;
		}else{ //MBOM
//			CmTreeNode ret = CmCommonStringUtil.copyCmTreeNode(node);
			// 递归处理子节点

			byte[] pbytes = null;
			CmTreeNode pchildNode = null;
			WTPart ppart = CmCommonStringUtil.isHasPlanning(node, false);
			if(null != ppart){
				pbytes =PBOMEditorToWCIntf.getPBOMXml(String.valueOf(ppart.getPersistInfo().getObjectIdentifier().getId()));
			}

			if(null== pbytes|| pbytes.length==0){
				pchildNode = CmCommonStringUtil.copyCmTreeNode(node);
				String gysl = pchildNode.getPart().getGysl();
				if(null!=ppart){
					CmLightPart bpart = buildCmLightPartFromWTPart(ppart);
					if(bpart.getGysl()==null||"".equals(bpart.getGysl())){
						bpart.setGysl(gysl);
					}
					pchildNode.setPart(bpart) ;

//					CmIBAHelper helper = new CmIBAHelper(ppart);
//					try {
//						String clbm = helper.getIBAValue(ppart, "CLBM");
//						Wzk wzk = ErpToWCIntf.getWzkByInvcode("01", clbm);
//						if(wzk!=null){
//							pchildNode.getPart().setWzk(wzk);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}

				}

				Enumeration children = node.children();
				while (children.hasMoreElements()) {
					CmTreeNode child = (CmTreeNode) children.nextElement();
					byte[] bytes = null;
					CmTreeNode childNode = null;
					WTPart part = CmCommonStringUtil.isHasPlanning(child, false);
					if(null != part){
						bytes =PBOMEditorToWCIntf.getPBOMXml(String.valueOf(part.getPersistInfo().getObjectIdentifier().getId()));
					}
					if(null == bytes || bytes.length == 0){
						childNode = convertEBomNode2MBomNode(child,isMbom);
						pchildNode.add(childNode);

					}else{
						CmXmlUtil xml = new CmXmlUtil();
						childNode = xml.buildTreeWithBytes(bytes);
						childNode.setOccId(child.getOccId());
						childNode.setOccpath(child.getOccpath());
						childNode.getPart().setParentPartNumber(child.getPart().getParentPartNumber());
						pchildNode.add(childNode);
						copyNewNodeAndRebuildOccpath(childNode,pchildNode.getOccpath());
//						rebuildOccpath(childNode);
						rebuildOccpathFromXmlToTree(childNode,child.getOccpath());
						addChildNode(childNode);

					}
				}

			}else{
				CmXmlUtil xml = new CmXmlUtil();
				pchildNode = xml.buildTreeWithBytes(pbytes);

				pchildNode.getPart().setHasXml(true);
				pchildNode.setOccId(node.getOccId());
				pchildNode.setOccpath(node.getOccpath());
				pchildNode.getPart().setParentPartNumber(node.getPart().getParentPartNumber());
				copyNewNodeAndRebuildOccpath(pchildNode,pchildNode.getOccpath());
				rebuildOccpathFromXmlToTree(pchildNode,pchildNode.getOccpath());
				CmBizObjUtil.addChildNode(pchildNode);
			}


			return pchildNode;


//			return ret;
		}



	}


	public static void  setMatrixPaste(CmTreeNode pchildNode){
		CmXmlUtil.getMatrixFromEbom(pchildNode);
		Enumeration<CmTreeNode> en = pchildNode.children();
		CmTreeNode node = null;
		while(en.hasMoreElements()){
			node = en.nextElement();
			if(node.isIspackage()){
				for(CmTreeNode cNode:node.getListNode()){
					setMatrixPaste(cNode);
				}
			}else{
				setMatrixPaste(node);
			}
		}
	}


//	/**{
//	 * 将EBOM复制到PBOM
//	 * @author chenyunlong
//	 * @date  2013-7-25
//	 * @param node
//	 * @return
//	 *
//	 */
//	public CmTreeNode convertEBomNode2MBomNode(CmTreeNode node,boolean isOpen) {
//		CmTreeNode ret = CmCommonStringUtil.copyCmTreeNode(node);
//		boolean flag = CmCommonStringUtil.updateNodeOfPlanning(ret, true, true);
//		if(!flag){
//			JOptionPane.showMessageDialog(null, "下列EBOM树上节点:\n"+ret.getPart().getPartNumber()+"\n创建Planning视图失败，请联系管理员！");
//			if (CmContext.isJWS())
//				JWSUtil.shutdown();
//			System.exit(0);
//		}
//		// 递归处理子节点
//		Enumeration children = node.children();
//		while (children.hasMoreElements()) {
//			CmTreeNode child = (CmTreeNode) children.nextElement();
//			CmTreeNode childNode = copyMap.get(child.getPart().getPartNumber());
//			if(null != childNode){
//				CmTreeNode newChild = copyNewNodeAndRebuildOccpath(childNode,childNode.getOccpath());
//				convertNewNode2Old(newChild,child,isOpen,ret,false);
//				saveAllNodeList(newChild,false);
//			}else{
//				childNode = nodeMap.get(child.getPart().getPartNumber());
//				if(null != childNode){
//					childNode = copyNewNodeAndRebuildOccpath(childNode,childNode.getOccpath());
//					convertNewNode2Old(childNode,child,isOpen,ret,true);
//					saveAllNodeList(childNode,false);
//				}else{
//					byte[] bytes = isOpen?PBOMEditorToWCIntf.getPBOMXml(child.getPart().getPartNumber(),String.valueOf(child.getPart().getOid()),child.getPart().getVersion(),isOpen):null;
//					if(null == bytes || bytes.length == 0){
//						childNode = convertEBomNode2MBomNode(child,isOpen);
//						if(childNode.children().hasMoreElements()){
//							addDifferentNodeToMap(childNode);
//						}
//						ret.add(childNode);
//						saveAllNodeList(childNode,false);
//					}else{
//						if(!CmConnectFrame.bigassemble){
//							CmXmlUtil xml = new CmXmlUtil();
//							childNode = xml.buildTreeWithBytes(bytes,new CmBizObjUtil(), isOpen);
//							nodeMap.put(childNode.getPart().getPartNumber(), childNode);
//							CmXmlUtil.bytesmap.put(childNode.getPart().getPartNumber(), "true");
//							convertNewNode2Old(childNode,child,isOpen,ret,true);
//							saveAllNodeList(childNode,true);
//						}else{
//							childNode = CmCommonStringUtil.copyCmTreeNode(child);
//							convertNewNode2Old(childNode,child,isOpen,ret,false);
//							if(!CmCommonStringUtil.updateNodeOfPlanning(childNode, true, true)){
//								JOptionPane.showMessageDialog(null, "下列EBOM树上节点:\n"+childNode.getPart().getPartNumber()+"\n创建Planning视图失败，请联系管理员！");
//								if (CmContext.isJWS())
//									JWSUtil.shutdown();
//								System.exit(0);
//							}
//							childNode.getPart().setHasXml(true);
//							CmXmlUtil.bytesmap.put(childNode.getPart().getPartNumber(), "true");
//							CmScrollPaneTree.saveNodeToList(childNode,true);
//						}
//					}
//				}
//			}
//		}
//		return ret;
//	}
//
//




	/**
	 * 由于新打开的xml的第一个节点的occid都是为-1，需要重新拼装occpath
	 * @author chenyunlong
	 * @date  2013-7-15
	 * @param parent
	 *
	 */
	public static void rebuildOccpathFromXmlToTree(CmTreeNode parent,String parentOccpath){
		Enumeration children = parent.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if(!"assistant".equals(child.getPart().getPartType())){
				if(child.getOccpath().indexOf("-1+")==-1){
					child.setOccpath(parentOccpath+"+"+child.getOccpath());
				}else if(!"assistant".equals(parent.getPart().getPartType())
						&& !"middle2".equals(parent.getPart().getPartType())
						&& !"mp".equals(parent.getPart().getPartType())
						&& !"zuhe".equals(parent.getPart().getPartType())){
					child.setOccpath(child.getOccpath().replace("-1+", parentOccpath+"+"));
				}
				rebuildOccpathFromXmlToTree(child,parentOccpath);
			}
		}
	}

//	@SuppressWarnings("unchecked")
//	public static void rebuildOccpath(CmTreeNode parent){
//		Enumeration children = parent.children();
//		while(children.hasMoreElements()){
//			CmTreeNode child = (CmTreeNode) children.nextElement();
//			if(!"assistant".equals(child.getPart().getPartType())){
//				if(Long.valueOf(child.getOccId())>=0){
//					child.setOccpath(parent.getOccpath()+"+"+child.getOccId());
//				}else{
//					if(componentId >= Integer.valueOf(child.getOccId())){
//						componentId = Integer.valueOf(child.getOccId());
//					}
//					long id = componentId--;
//					child.setOccpath(parent.getOccpath()+"+"+id);
//				}
//				log.debug(child.getPart().getPartNumber() + "   occpath=" + child.getOccpath());
//				rebuildOccpath(child);
//			}
//		}
//	}
	/**
	 * 首先找到叶子节点，根据叶子节点中的isHasXml确定该节点是否有xml，如果有xml，继续添加xml里面的结构
	 * @author chenyunlong
	 * @date  2013-7-16
	 * @param parent
	 *
	 */
	public static void addChildNode(CmTreeNode parent){
		List<CmTreeNode> list = new ArrayList<CmTreeNode>();
		CmCommonStringUtil.getAllChildNodesFromParent(parent, list);
//		if(list.size() ==0 && null == getNodeXML(parent)){
		if(list.size() ==0 && null == getNodeXML(parent.getPart().getPartNumber())){
			addChildNodeFromXML(parent,(CmTreeNode)parent.getParent());
		}else{
			for (Iterator<CmTreeNode> it = list.iterator(); it.hasNext();) {
				CmTreeNode cmnode = it.next();
				if (!cmnode.getPart().isHasXml()){
					it.remove();
				}
			}
			for(int i=0;i<list.size();i++){
				addChildNodeFromXML(list.get(i),(CmTreeNode)list.get(i).getParent());
			}
		}
	}

	/**
	 * 根据获取到的xml，添加xml中的节点到BOM
	 * @author chenyunlong
	 * @date  2013-7-25
	 * @param child
	 *
	 */
	public static void addChildNodeFromXML(CmTreeNode child,CmTreeNode ret){
		CmTreeNode childNode = null;
//		byte[] bytes = getNodeXML(child);
		byte[] bytes = getNodeXML(child.getPart().getPartNumber());
		if(null == bytes || bytes.length == 0){
			WTPart part = CmCommonStringUtil.isHasPlanning(child, false);
			if(null != part){
				bytes =PBOMEditorToWCIntf.getPBOMXml(String.valueOf(part.getPersistInfo().getObjectIdentifier().getId()));
			}
		}
		if(null != bytes && bytes.length > 0){
//			addBytesToList(child,bytes);
			addBytesToList(child.getPart().getPartNumber(),child.getPart().getOid()+"",bytes);
			CmXmlUtil xml = new CmXmlUtil();
			childNode = xml.buildTreeWithBytes(bytes);
			childNode.setOccId(child.getOccId());
			childNode.setOccpath(child.getOccpath());
			childNode.getPart().setParentPartNumber(child.getPart().getParentPartNumber());

			if(ret==null){

			}else{
				copyNewNodeAndRebuildOccpath(childNode,ret.getOccpath());
			}

//			rebuildOccpath(childNode);
			rebuildOccpathFromXmlToTree(childNode,child.getOccpath());
			CmTreeNode parent = (CmTreeNode) child.getParent();
			child.removeFromParent();
			parent.add(childNode);
			addChildNode(childNode);
		}
	}
	public static CmTreeNode copyNewNodeAndRebuildOccpath(CmTreeNode old,String oldoccpath){
		CmTreeNode newnode =CmCommonStringUtil.copyCmTreeNode(old);
		newnode.setOccpath(newnode.getOccpath().replace(oldoccpath, "-1"));
		Enumeration children=old.children();
		while(children.hasMoreElements()){
			CmTreeNode child=(CmTreeNode) children.nextElement();
			newnode.add(copyNewNodeAndRebuildOccpath(child,oldoccpath));
		}
		return newnode;
	}
	/**
	 * 获取零件的xml，是从byteslist中获取，不是从windchill服务器中获取
	 *
	 * @author chenyunlong
	 * @date  2013-7-25
	 * @param child
	 * @return
	 *
	 */
	public static byte[] getNodeXML(String partNumber){
		byte[] bytes = null;
		for(Map<String,Object> map:byteslist){
			if(CmCommonStringUtil.isEqual(String.valueOf(map.get("partNumber")), partNumber)){
				bytes = (byte[]) map.get("bytes");
			}
		}
		return bytes;
	}

	public static void addBytesToList(String partNumber,String oid,byte[] bytes){
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("partNumber", partNumber);
		map.put("oid", oid);
		map.put("bytes", bytes);
		byteslist.add(map);
	}

//	public static byte[] getNodeXML(CmTreeNode child){
//		byte[] bytes = null;
//		for(Map<String,Object> map:byteslist){
//			if(CmCommonStringUtil.isEqual(String.valueOf(map.get("partNumber")), child.getPart().getPartNumber())){
//				bytes = (byte[]) map.get("bytes");
//			}
//		}
//		return bytes;
//	}
//
//	public static void addBytesToList(CmTreeNode child,byte[] bytes){
//		Map<String,Object> map = new HashMap<String,Object>();
//		map.put("partNumber", child.getPart().getPartNumber());
//		map.put("oid", child.getPart().getOid()+"");
//		map.put("bytes", bytes);
//		byteslist.add(map);
//	}

	public static CmTreeNode buildTree(CmPartWithOcc root) {
		CmTreeNode ret = new CmTreeNode(new CmEPartInstance(root.getPart()));

		int rootOccSize = root.getOccurences().size();
		if (rootOccSize > 0) {
			CmPartUsesOcc usesOcc = root.getOccurences().get(0);
			usesOcc.addTreeNode(ret);

			ret.setMatrix(usesOcc.getMatrix());
			ret.setOccId(usesOcc.getInstanceData().getOccId()+"");
			buildOccpath(ret,true);
			build(root, ret);
//			build(root, rootOccSize);
		}
		return ret;
	}

	public static void build(CmPartWithOcc father, int extOccSize) {
		if (father == null)
			return;

		Vector<CmPartWithOcc> children = father.getChildren();
		int childSize = children.size();
		if (childSize > 0) {
			for (int childIndex = 0; childIndex < childSize; childIndex++) {
				CmPartWithOcc child = children.get(childIndex);
				CmLightPart lightPart = child.getPart();

				Vector<CmPartUsesOcc> fatherUsesOccVec = father.getOccurences();
				int fatherOccSize = fatherUsesOccVec.size();
				for (int fatherOccIndex = 0; fatherOccIndex < fatherOccSize; fatherOccIndex++) {
					CmPartUsesOcc fatherUsesOcc = fatherUsesOccVec.get(fatherOccIndex);
					Vector<CmTreeNode> fatherUsesTreeNodes = fatherUsesOcc.getTreeNodes();

					Vector<CmPartUsesOcc> childUsesOccVec = child.getOccurences();
					int childOccSize = childUsesOccVec.size();
					int fatherUsesTreeNodeSize = fatherUsesTreeNodes.size();
					for (int i = fatherUsesTreeNodeSize - extOccSize; i < fatherUsesTreeNodeSize; i++) {
						CmTreeNode fatherUsesTreeNode = fatherUsesTreeNodes.get(i);
						Matrix4d fatherMatrix = fatherUsesTreeNode.getMatrix();
						for (int k = 0; k < childOccSize; k++) {
							CmPartUsesOcc childUsesOcc = childUsesOccVec.get(k);
							Matrix4d relMatrix = childUsesOcc.getMatrix();
							Matrix4d newMatrix = CmMathUtil.combineMatrix4(fatherMatrix, relMatrix);

							CmTreeNode childNode;
							// 需要重构，表示按数量显示的标准件
							if (childOccSize == 1 && child.getQuantity() > 1 && child.getChildren().isEmpty()) { // 叶子节点数量大于1，只有1个实例
								// if (childOccSize == 1 &&
								// child.getChildren().isEmpty()) { //
								// 叶子节点数量大于1，只有1个实例
								childNode = new CmTreeNode(new CmEPartMaster(lightPart, child.getQuantity()));
							} else
								childNode = new CmTreeNode(new CmEPartInstance(lightPart));

							childNode.setMatrix(newMatrix);
							childNode.setRelativeMatrix(relMatrix);
							childNode.setOccId(childUsesOcc.getInstanceData().getOccId()+"");
							fatherUsesTreeNode.add(childNode);
							childUsesOcc.addTreeNode(childNode);
							buildOccpath(childNode,false);
							childNode.getPart().setParentPartNumber(((CmTreeNode)childNode.getParent()).getPart().getPartNumber());
						}
					}
				}
				build(child, fatherOccSize);
			}
		}
	}

	public static void build(CmPartWithOcc father,CmTreeNode root){
		if (father == null)
			return;
		Vector<CmPartWithOcc> children = father.getChildren();
		int childSize = children.size();
		if (childSize > 0) {
			for (int childIndex = 0; childIndex < childSize; childIndex++) {
				CmPartWithOcc child = children.get(childIndex);
				CmLightPart lightPart = child.getPart();
				Vector<CmPartUsesOcc> brotherUsesOccVec = child.getOccurences();
				int brotherOccSize = brotherUsesOccVec.size();
				for (int brotherindex = 0; brotherindex < brotherOccSize; brotherindex++) {
					CmPartUsesOcc brotherUsesOcc = brotherUsesOccVec.get(brotherindex);
					CmTreeNode childNode = buildTreeNode(brotherUsesOcc,root,lightPart,child);
					build(child,childNode);
				}
			}
		}
	}

	public static CmTreeNode buildTreeNode(CmPartUsesOcc childUsesOcc,CmTreeNode fatherUsesTreeNode,CmLightPart lightPart,CmPartWithOcc child){
		Matrix4d relMatrix = childUsesOcc.getMatrix();
		Matrix4d newMatrix = CmMathUtil.combineMatrix4(fatherUsesTreeNode.getMatrix(), relMatrix);

		CmTreeNode childNode;
		// 需要重构，表示按数量显示的标准件
		if (child.getQuantity() > 1 && child.getChildren().isEmpty()) { // 叶子节点数量大于1，只有1个实例
			// if (childOccSize == 1 &&
			// child.getChildren().isEmpty()) { //
			// 叶子节点数量大于1，只有1个实例
			childNode = new CmTreeNode(new CmEPartMaster(lightPart, child.getQuantity()));
		} else
			childNode = new CmTreeNode(new CmEPartInstance(lightPart));

		childNode.setMatrix(newMatrix);
		childNode.setRelativeMatrix(relMatrix);
		childNode.setOccId(childUsesOcc.getInstanceData().getOccId()+"");
		fatherUsesTreeNode.add(childNode);
		childUsesOcc.addTreeNode(childNode);
		buildOccpath(childNode,false);
		childNode.getPart().setParentPartNumber(((CmTreeNode)childNode.getParent()).getPart().getPartNumber());
		return childNode;
	}

	public static void buildOccpath(CmTreeNode ret,boolean flag){
		if(null == ret.getParent() && flag){
			ret.setOccpath(ret.getOccId());
		}else if(CmCommonStringUtil.isEmpty(((CmTreeNode)ret.getParent()).getOccpath())){
			ret.setOccpath(((CmTreeNode)ret.getParent()).getOccId() +"+" +ret.getOccId());
		}else{
			ret.setOccpath(((CmTreeNode)ret.getParent()).getOccpath() +"+" +ret.getOccId());
		}
	}

	public void resetComponentId(){
		componentId = -1;
	}
//	public static CmTreeNode copyNewNodeAndRebuildOccpath(CmTreeNode newnode,String oldoccpath){
////		CmTreeNode newnode =CmCommonStringUtil.copyCmTreeNode(old);
//		newnode.setOccpath(newnode.getOccpath().replace(oldoccpath, "-1"));
//		Enumeration children=newnode.children();
//		while(children.hasMoreElements()){
//			CmTreeNode child=(CmTreeNode) children.nextElement();
//			newnode.add(copyNewNodeAndRebuildOccpath(child,oldoccpath));
//		}
//		return newnode;
//	}
	public void clearData(){
		nodeMap = new HashMap<String,CmTreeNode>();
		copyMap = new HashMap<String,CmTreeNode>();
	}

}
