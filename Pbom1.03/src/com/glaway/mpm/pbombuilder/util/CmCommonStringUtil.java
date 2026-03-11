package com.glaway.mpm.pbombuilder.util;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.*;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import wt.fc.PersistenceHelper;
import wt.part.WTPart;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;
import java.util.*;

public class CmCommonStringUtil {
	private static final CmLogger log = CmLogger.getLogger(CmCommonStringUtil.class.getName());
	private static List<CmTreeNode> nodeList;
	private static List<CmTreeNode> planingList = new ArrayList<CmTreeNode>();

	/**
	 * 是否为空
	 *
	 * @date 2012-12-7
	 * @param obj
	 * @return
	 *
	 */
	public static boolean isEmpty(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return true;
		} else {
			return false;
		}
	}

	public static String booleanToString(boolean flag) {
		if (flag) {
			return "Y";
		} else {
			return "N";
		}
	}

	public static String emptyToNumber(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return "0";
		} else {
			return String.valueOf(Long.parseLong(obj));
		}
	}


	public static String emptyToNumber2(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return "0";
		} else {
			return String.valueOf(obj.trim());
		}
	}

	public static String emptyToString(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return "";
		} else {
			return obj;
		}
	}

	public static long emptyToLong(String obj) {
		if (null == obj || "".equals(obj)) {
			return 0;
		} else {
			return Long.parseLong(obj);
		}
	}

	public int StringToASCII(String str){
		char[] c1 = str.toCharArray();
		return (int)c1[0];
	}

	public char ASCIIToString(String number){
		 return (char)Integer.parseInt(number);
	}

	/**
	 * 找出最上层的节点
	 *
	 * @date 2012-12-7
	 * @param node
	 * @return
	 *
	 */
	public static CmTreeNode getRootNode(CmTreeNode node) {
		if(null==node){
			return null;
		}
		if (node.getParent() != null) {
			return getRootNode((CmTreeNode) node.getParent());
		} else {
			return node;
		}
	}

	public static boolean isNewNode(String partType) {
		if ("middle".equals(partType) || "assistant".equals(partType)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 两个零件的类型相同
	 * @param ebom
	 * @param pbom
	 * @return
	 *
	 */
	public static boolean isCommonPartType(CmTreeNode ebom, CmTreeNode pbom){
		if((isNewNode(ebom.getPart().getPartType()) && isNewNode(pbom.getPart().getPartType())) || (!isNewNode(ebom.getPart().getPartType()) && !isNewNode(pbom.getPart().getPartType()))){
			return true;
		}
		else{
			return false;
		}
	}

	/**
	 * 是否是同样的零件
	 *
	 * @param ebom
	 * @param pbom
	 * @return
	 *
	 */
	public static boolean isCommonNode(CmTreeNode ebom, CmTreeNode pbom) {
		if (null != ebom && null != pbom && isCommonPart(ebom.getPart(), pbom.getPart())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 是否是完全相同的零件
	 *
	 * @param ebom
	 * @param pbom
	 * @return
	 *
	 */
	public static boolean isSameNode(CmTreeNode ebom, CmTreeNode pbom) {
		if(isCommonNode(ebom, pbom)){
			if(null!=ebom.getOccId() && ebom.getOccId().equals(pbom.getOccId())){
				return true;
			}
			else if(null==ebom.getOccId() && null==pbom.getOccId() && isSameNode((CmTreeNode)ebom.getParent(),(CmTreeNode)pbom.getParent())){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	public static boolean isSameNodeNew(CmTreeNode ebom, CmTreeNode pbom) {
		if(isCommonNode(ebom, pbom)){
			if(isCommonNode((CmTreeNode)ebom.getParent(),(CmTreeNode)pbom.getParent())){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	/**
	 * 是兄弟节点
	 * @author chenyunlong
	 * @date  2013-4-15
	 * @param ebom
	 * @param pbom
	 * @return
	 *
	 */
	public static boolean isCommon(CmTreeNode ebom, CmTreeNode pbom) {
		return isCommonPart(ebom.getPart(), pbom.getPart());
	}
	/**
	 * 是兄弟节点但不是相同的节点
	 * @author chenyunlong
	 * @date  2013-3-29
	 * @param ebom
	 * @param pbom
	 * @return
	 *
	 */
	public static boolean isCommonButNotSame(CmTreeNode ebom, CmTreeNode pbom){
		if(isCommon(ebom,pbom)){
			if(null!=ebom.getOccId() && ebom.getOccId().equals(pbom.getOccId())){
				return false;
			}
			else{
				return true;
			}
		}
		else{
			return false;
		}
	}

	public static boolean isCommonPart(CmLightPart epart, CmLightPart part) {
		if(null==epart || null==part){
			return false;
		}
		else if (isEqual(epart.getPartNumber(),part.getPartNumber())
				&& isEqual(epart.getPartName(),part.getPartName())
				&& isEqual(epart.getPartType(),part.getPartType())) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isSamePart(CmLightPart epart, CmLightPart part){
		if (isCommonPart(epart,part)
//				&& epart.isKey() == part.isKey()
//				&& epart.isSpecial() == part.isSpecial()
//				&& epart.isSpaceBorneTable() == part.isSpaceBorneTable()
//				&& isEqual(epart.getWorkShop(), part.getWorkShop())
//				&& isEqual(epart.getOutsourcingUnits(),part.getOutsourcingUnits())
//				&& isEqual(epart.getMaterialType(), part.getMaterialType())
//				&& isEqual(epart.getBackupRate(), part.getBackupRate())
//				&& isEqual(epart.getMaxBackupCount(), part.getMaxBackupCount())
//				&& isEqual(epart.getBackupReason(), part.getBackupReason())
//				&& isEqual(epart.getRemark(), part.getRemark())
				&& isEqual(epart.getMtype(), part.getMtype())
				&& isEqual(epart.getKeycomponent(), part.getKeycomponent())
				&& isEqual(epart.getZzcj(),part.getFirstPlant())
				&& isEqual(epart.getFzcj(),part.getSecondePlantStr())
				&& isEqual(epart.getWzk().getInvcode(),part.getWzk().getInvcode())
				//&& isEqual(epart.getWzk().getClcode(),part.getWzk().getClcode())
				//&& isEqual(epart.getWzk().getZxsl(),part.getWzk().getZxsl())
				//&& isEqual(epart.getWzk().getMpcc(),part.getWzk().getMpcc())
				&& isEqual(epart.getVersion(), part.getVersion())
				) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 辅件的数量是否有修改
	 * @author chenyunlong
	 * @date  2013-4-11
	 * @param assist
	 * @return
	 *
	 */
//	public static void checkAssistCountIsEdit(CmTreeNode assist){
//		List<CmTreeNode> list=CmScrollPaneTree.pbomlist;
//		for (CmTreeNode pbom : list) {
//			if ("assistant".equals(pbom.getPart().getPartType()) &&  isCommon(assist, pbom)) {
//				if (assist.getPart().getProductionQuantity()!=pbom.getPart().getProductionQuantity()
//						|| !isEqual(assist.getPart().getProductionRatio(), pbom.getPart().getProductionRatio())) {
//					assist.getPart().setEditOfAssistCount(true);
//				}
//				else{
//					assist.getPart().setEditOfAssistCount(false);
//				}
//			}
//		}
//	}

	/**
	 * 辅件的数量是否有修改
	 *
	 * @author chenyunlong
	 * @date 2013-4-11
	 * @param assist
	 * @return
	 *
	 */
	public static void checkAssistCountIsEdit(CmTreeNode assist) {
		CmTreeNode seNode = CmScrollPaneTree.pbomMap.get(assist);
		if (seNode != null) {
			if ("assistant".equals(seNode.getPart().getPartType())) {
				if (assist.getPart().getProductionQuantity() != seNode
						.getPart().getProductionQuantity()
						|| !isEqual(assist.getPart().getProductionRatio(),
								seNode.getPart().getProductionRatio())) {
					assist.getPart().setEditOfAssistCount(true);
				} else {
					assist.getPart().setEditOfAssistCount(false);
				}
			}
		}
	}

	public static boolean isSamePartWithoutVersion(CmLightPart epart, CmLightPart part){
		System.out.println("===============isSamePartWithoutVersion end====================================");
		if (isCommonPart(epart,part)
//				&& epart.isKey() == part.isKey()
//				&& epart.isSpecial() == part.isSpecial()
//				&& epart.isSpaceBorneTable() == part.isSpaceBorneTable()
//				&& isEqual(epart.getWorkShop(), part.getWorkShop())
//				&& isEqual(epart.getOutsourcingUnits(),part.getOutsourcingUnits())
//				&& isEqual(epart.getMaterialType(), part.getMaterialType())
//				&& isEqualWithNumber(epart.getBackupRate(), part.getBackupRate())
//				&& isEqualWithNumber(epart.getMaxBackupCount(), part.getMaxBackupCount())
//				&& isEqual(epart.getBackupReason(), part.getBackupReason())
//				&& isEqual(epart.getRemark(), part.getRemark())
				&& isEqual(epart.getMtype(), part.getMtype())
				&& isEqual(epart.getKeycomponent(), part.getKeycomponent())
				&& isEqual(epart.getZzcj(),part.getZzcj())
				&& isEqual(epart.getFzcj(),part.getFzcj())
				&& isEqual(epart.getWzk().getInvcode(),part.getWzk().getInvcode())
//				&& isEqual(epart.getWzk().getClcode(),part.getWzk().getClcode())
//				&& isEqual(epart.getWzk().getZxsl(),part.getWzk().getZxsl())
//				&& isEqual(epart.getWzk().getMpcc(),part.getWzk().getMpcc())
				) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEqual(String obj1, String obj2) {
		if (emptyToString(obj1).equals(emptyToString(obj2))) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEqualWithNumber(String obj1, String obj2){
		if (emptyToNumber(obj1).equals(emptyToNumber(obj2))) {
			return true;
		} else {
			return false;
		}
	}

	public static List<CmTreeNode> getBomNodeList(CmTree tree) {
		nodeList = new ArrayList<CmTreeNode>();
		getBomTreeNode(tree.getRoot());
		return nodeList;
	}

	@SuppressWarnings("unchecked")
	public static void getBomTreeNode(CmTreeNode root) {
		Enumeration ebom = root.children();
		while (ebom.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) ebom.nextElement();
			nodeList.add(child);
			getBomTreeNode(child);
			if (isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					nodeList.add(brother);
					getBomTreeNode(brother);
				}
			}
		}
	}

	public static boolean isPackage(CmTreeNode node) {
		if (null != node.getListNode() && node.getListNode().size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 当前节点的某个父节点是打包结构，返回true
	 * @author chenyunlong
	 * @date  2013-4-2
	 * @param node
	 * @return
	 *
	 */
	public static boolean isPackageOfParent(CmTreeNode node){
		if(null ==node.getParent() || null==node.getParent().getParent()){
			return false;
		}
		else if(isPackage((CmTreeNode) node.getParent())){
			return true;
		}
		else{
			return isPackageOfParent((CmTreeNode) node.getParent());
		}
	}
	/**
	 * 返回父节点的打包节点
	 * @author chenyunlong
	 * @date  2013-4-10
	 * @param node
	 * @return
	 *
	 */
	public static CmTreeNode getParentPackageNode(CmTreeNode node){
		if(null ==node.getParent()){
			return null;
		}else if(isPackage((CmTreeNode)node.getParent())){
			return (CmTreeNode)node.getParent();
		}
		else{
			return getParentPackageNode((CmTreeNode)node.getParent());
		}
	}
	/**
	 * 在list中是否拥有相同的零件
	 * @author chenyunlong
	 * @date  2013-4-25
	 * @param node
	 * @param list
	 * @return
	 *
	 */
	public static boolean checkHasCommonNodeInList(CmTreeNode node, List<CmTreeNode> list) {
		boolean flag = false;
		for (CmTreeNode cmnode : list) {
			if (isCommonNode(node, cmnode)) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	/**
	 * 在list中是否拥有完全相同的节点
	 * @author chenyunlong
	 * @date  2013-4-25
	 * @param node
	 * @param list
	 * @return
	 *
	 */
	public static boolean checkHasSameNodeInList(CmTreeNode node, List<CmTreeNode> list) {
		boolean flag = false;
		for (CmTreeNode cmnode : list) {
			if (isSameNode(node,cmnode)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public static CmLightPart copyCmlightPart(CmLightPart oldpart) {
		CmLightPart newpart = new CmLightPart();
		newpart.setOid(oldpart.getOid());
		newpart.setPartNumber(oldpart.getPartNumber());
		newpart.setPartName(oldpart.getPartName());
		newpart.setPartType(oldpart.getPartType());
		newpart.setDutu(oldpart.getDutu());
		newpart.setRemark(oldpart.getRemark());
		newpart.setUseCount(oldpart.getUseCount());
		////设置pbom工艺数量和Ebom一致
		String gysl = oldpart.getGysl();
		if(gysl!=null&&!"".equals(gysl)&&!"null".equals(gysl)){
			newpart.setGysl(gysl);
		}else{
			newpart.setGysl(String.valueOf(oldpart.getUseCount()));
		}

		//设置805设计数量
        newpart.setUseCount_805(String.valueOf(oldpart.getUseCount()));

		newpart.setProductionQuantity(oldpart.getProductionQuantity());
		newpart.setProductionRatio(oldpart.getProductionRatio());
		newpart.setRate(oldpart.getRate());
		newpart.setKey(oldpart.isKey());
		newpart.setSpecial(oldpart.isSpecial());
		newpart.setEbomKey(oldpart.isEbomKey());
		newpart.setSpaceBorneTable(oldpart.isSpaceBorneTable());
		newpart.setMaterialType(oldpart.getMaterialType());
		newpart.setBackupRate(oldpart.getBackupRate());
		newpart.setMaxBackupCount(oldpart.getMaxBackupCount());
		newpart.setBackupReason(oldpart.getBackupReason());
		newpart.setWorkShop(oldpart.getWorkShop());
		newpart.setOutsourcingUnits(oldpart.getOutsourcingUnits());
		newpart.setMaterialNumber(oldpart.getMaterialNumber());
		newpart.setMaterialName(oldpart.getMaterialName());
		newpart.setMaterialBrand(oldpart.getMaterialBrand());
		newpart.setMaterialCrision(oldpart.getMaterialCrision());
		newpart.setResponser(oldpart.getResponser());
		newpart.setResponserGroup(oldpart.getResponserGroup());
		newpart.setLifecycle(oldpart.getLifecycle());
		newpart.setE_version(oldpart.getE_version());
		newpart.setEu_number(oldpart.getEu_number());
		newpart.setEu_version(oldpart.getEu_version());
		newpart.setVersion(oldpart.getVersion());
		newpart.setOperType(oldpart.getOperType());
		newpart.setContainerId(oldpart.getContainerId());
		newpart.setMove(oldpart.isMove());
		newpart.setMiddleIndex(oldpart.getMiddleIndex());
		newpart.setEchangeIndex(oldpart.getEchangeIndex());
		newpart.setParentPartNumber(oldpart.getParentPartNumber());
		newpart.setHasXml(oldpart.isHasXml());
		newpart.setModifyXmlPartNumber(oldpart.getModifyXmlPartNumber());
		newpart.setChange(oldpart.isChange());
		newpart.setChangeOfStructure(oldpart.isChangeOfStructure());
		newpart.setEditWithChild(oldpart.isEditWithChild());
		newpart.setParentPath(oldpart.getParentPath());

		newpart.setStructureSaved(oldpart.isStructureSaved());

		//TODO 149
		//newpart.setWzk(oldpart.getWzk());
		newpart.setFirstPlant(oldpart.getFirstPlant());
		//newpart.setSecondePlant(oldpart.getSecondePlant());
		newpart.setZzcj(oldpart.getZzcj());
		newpart.setFzcj(oldpart.getFzcj());
		newpart.setMtype(oldpart.getMtype());
		newpart.setCtype(oldpart.getCtype());
		newpart.setPindex(oldpart.getPindex());
		newpart.setXhph(oldpart.getXhph());
		newpart.setJstj(oldpart.getJstj());
		newpart.setCsize(oldpart.getCsize());

		newpart.setCmat(oldpart.getCmat());
		newpart.setPtc_material_name(oldpart.getPtc_material_name());//材料名称
		newpart.setCmat_up(oldpart.getCmat_up());//材料上标
		newpart.setCmat_down(oldpart.getCmat_down());//材料下标
		newpart.setMaterial(oldpart.getMaterial());
		newpart.setPzggbzh(oldpart.getPzggbzh());
		newpart.setJstjbzh(oldpart.getJstjbzh());
		newpart.setJddj(oldpart.getJddj());
		newpart.setClzt(oldpart.getClzt());
		newpart.setZldj(oldpart.getZldj());
		newpart.setCldw(oldpart.getCldw());
		newpart.setZqclmc(oldpart.getZqclmc());
		newpart.setJbclmc(oldpart.getJbclmc());
		newpart.setZqclbzh(oldpart.getZqclbzh());

		newpart.setCindex(oldpart.getCindex());//图号
		newpart.setMindex(oldpart.getMindex());//所属型号
		newpart.setSecret(oldpart.getSecret());//密级
		newpart.setSetmark(oldpart.getSetmark());//成套件标识
		newpart.setAdjustable(oldpart.getAdjustable());//可调整
		newpart.setKeycomponent(oldpart.getKeycomponent());//关重件标识
		newpart.setPhase_code(oldpart.getPhase_code());//当前阶段
		newpart.setCompany(oldpart.getCompany());//设计单位
		newpart.setDesigner(oldpart.getDesigner());//设计者

		newpart.setWzk(copyWzk((oldpart.getWzk())));
		newpart.setSecondePlant(copySecondePlant(oldpart.getSecondePlant()));

		//设计资源库新增属性
		//start
		newpart.setShortname(oldpart.getShortname());//物资简称
		newpart.setYxjb(oldpart.getYxjb());//编码优选级别
		newpart.setBmzt(oldpart.getBmzt());//编码状态
		newpart.setBmlx(oldpart.getBmlx());//编码类型
		newpart.setStandardnumber(oldpart.getStandardnumber());//标准号
		newpart.setMechanicalpropertyorhardness(oldpart.getMechanicalpropertyorhardness());//机械性能等级或硬度
		newpart.setSurfacetreatment(oldpart.getSurfacetreatment());//表面处理
		newpart.setHeattreatment(oldpart.getHeattreatment());//热处理
		newpart.setProductform(oldpart.getProductform());//产品型式
		newpart.setProductlevel(oldpart.getProductlevel());//产品等级
		newpart.setPlatecscrewform(oldpart.getPlatecscrewform());//板拧形式
		newpart.setIsimport(oldpart.getIsimport());//是否进口
		newpart.setSpecialinstruction(oldpart.getSpecialinstruction());//特殊说明
		newpart.setMeasureunit(oldpart.getMeasureunit());//计量单位
		newpart.setType(oldpart.getType());//型号
		newpart.setTypestandard(oldpart.getTypestandard());//型号规格
		newpart.setQualitylevel(oldpart.getQualitylevel());//质量等级
		newpart.setTotalstandard(oldpart.getTotalstandard());//总规范
		newpart.setDetailstandard(oldpart.getDetailstandard());//详细规范
		newpart.setPackagingform(oldpart.getPackagingform());//封装形式
		newpart.setOutlinesize(oldpart.getOutlinesize());//外形尺寸
		newpart.setSpecialcondition(oldpart.getSpecialcondition());//专用条件
		newpart.setExtracondition(oldpart.getExtracondition());//附加协议
		newpart.setMattype(oldpart.getMattype());//材料类型

		newpart.setCmatnumber(oldpart.getCmatnumber());//材料编号
		newpart.setMarknumber(oldpart.getMarknumber());//牌号
		newpart.setSupplystate(oldpart.getSupplystate());//供应状态
		newpart.setUsestandard(oldpart.getUsestandard());//采用标准
		//end

		return newpart;
	}

	public static Wzk copyWzk(Wzk wzk){
		Wzk newWzk = new Wzk();
		newWzk.setInvclasscode(wzk.getInvclasscode());
		newWzk.setInvclassname(wzk.getInvclassname());
		newWzk.setInvcode(wzk.getInvcode());
		newWzk.setInvspec(wzk.getInvspec());
		newWzk.setInvtype(wzk.getInvtype());
		newWzk.setClcode(wzk.getClcode());
		newWzk.setClmc(wzk.getClmc());

		newWzk.setJsgfbz(wzk.getJsgfbz());
		newWzk.setFjtjname(wzk.getFjtjname());
		newWzk.setDef12(wzk.getDef12());
		newWzk.setDef14(wzk.getDef14());
		newWzk.setDef13(wzk.getDef13());
		newWzk.setZldj(wzk.getZldj());

		return newWzk;
	}

	public static Map<String, Boolean> copySecondePlant(Map<String, Boolean> plant){
		Map<String, Boolean> newPlant = new HashMap<String,Boolean>();
		if(plant!=null){
			newPlant.putAll(plant);
		}
		return newPlant;
	}

	public static CmTreeNode copyCmTreeNode(CmTreeNode node) {
		CmTreeNode newnode = new CmTreeNode(getTreeItem(node.getPart()));
		newnode.setMatrix(node.getMatrix());
		newnode.setOccId(node.getOccId());
		newnode.setOccpath(node.getOccpath());
		newnode.setSelected(node.isSelected());
		newnode.setPart(copyCmlightPart(node.getPart()));
		if (isPackage(node)) {
			List<CmTreeNode> list = new ArrayList<CmTreeNode>();
			for (CmTreeNode brother : node.getListNode()) {
				list.add(copyNewNode(brother));
			}
			newnode.setListNode(list);
		}
		return newnode;
	}

	public static String getTreeItem(CmLightPart part) {
		String item = part.getPartNumber() + "(" + part.getPartName() + ") ";
		if(!isEmpty(part.getVersion())){
			item+=part.getVersion();
		}
		return item;
	}

	/**
	 * 创建新的节点
	 * @param node
	 * @return
	 *
	 */
	public static CmTreeNode createNewNode(CmTreeNode node){
		CmTreeNode newnode = new CmTreeNode(getTreeItem(node.getPart()));
		newnode.setMatrix(node.getMatrix());
		newnode.setRelativeMatrix(node.getRelativeMatrix());
		newnode.setOccId(node.getOccId());
		newnode.setOccpath(node.getOccpath());
		newnode.setInstanceIdentifier(node.getInstanceIdentifier());
		newnode.setPart(copyCmlightPart(node.getPart()));
		return newnode;
	}

	/**
	 * 父节点是否已归档
	 *
	 * @date 2012-12-10
	 * @return
	 *
	 */
	public static boolean isHasFilingOfParent(CmTreeNode node) {
		CmTreeNode parent = (CmTreeNode)node.getParent();
		if(null == parent){
			return false;
		}else if ("PBOM".equals(getRootNode(parent).toString()) && "已批准".equals(parent.getPart().getLifecycle())) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 当前节点是否已归档
	 *
	 * @date 2012-12-10
	 * @return
	 *
	 */
	public static boolean isHasFilingOfObj(CmTreeNode node) {
		if ("已批准".equals(node.getPart().getLifecycle())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * PBOM节点的编辑状态
	 *
	 * @param node
	 * @param type
	 *
	 */
	public static void checkPbomTreeNodeIsEdit(CmTreeNode node) {
		checkNodeIsEdit(node);
		if (isPackage(node)) {
			for (CmTreeNode brother : node.getListNode()) {
//				checkPbomTreeNodeIsEdit(brother);
				brother.getPart().setEdit(node.getPart().isEdit());
			}
		}
	}

//	public static void checkNodeIsEdit(CmTreeNode node) {
//		List<CmTreeNode> list=CmScrollPaneTree.pbomlist;
//		for (CmTreeNode pbom : list) {
//			if (isSameNode(node, pbom)) {
//				if (isSamePartWithoutVersion(node.getPart(), pbom.getPart())) {
//					node.getPart().setEdit(false);
//				} else {
//					// 此节点被编辑过
//					node.getPart().setEdit(true);
//				}
//				break;
//			}
//		}
//	}

	public static void checkNodeIsEdit(CmTreeNode node) {
//		if ("new".equals(node.getPart().getOperType())) {
//			return;
//		}
		CmTreeNode pbom = CmScrollPaneTree.pbomMap.get(node);
		if (pbom != null) {
			if (isSameNode(node, pbom)) {
				if (isSamePartWithoutVersion(node.getPart(), pbom.getPart())) {
					node.getPart().setEdit(false);
				} else {
					// 此节点被编辑过
					node.getPart().setEdit(true);
				}
			}
		}
	}
	public static void addNodeToCheckList(CmTreeNode node) {
//		copyNodeToList(node,(CmTreeNode)node.getParent(), CmScrollPaneTree.pbomlist,true);
		copyNodeToList(node, (CmTreeNode) node.getParent(),
				CmScrollPaneTree.pbomMap, true, true,false);

		Enumeration<CmTreeNode> en =  node.children();
		while(en.hasMoreElements()){
			copyNodeToList(en.nextElement(), node,
					CmScrollPaneTree.pbomMap, true, true,false);
		}
	}

	/**
	 * 将节点保存到原始的节点list中去
	 * @param node
	 * @param pbomlist
	 *
	 */
//	public static void copyNodeToList(CmTreeNode node,CmTreeNode parent, List<CmTreeNode> pbomlist,boolean flag) {
//		CmTreeNode newnode = createNewNode(node);
//		CmTreeNode same = checkTheNodeIsInList(newnode, pbomlist);
//		if (null== same) {
//			if(flag){
//				node.getPart().setOperType("new");
//				newnode.getPart().setOperType("new");
//			}
//			CmTreeNode cmparent=null;
//			for(CmTreeNode cmnode:pbomlist){
//				if(CmCommonNodeUtil.checkHasSameOccpath(cmnode, parent)){
//					cmparent = cmnode;
//					break;
//				}
//			}
//			newnode.setParent(cmparent);
//			newnode.setMatrix(node.getMatrix());
//			newnode.setRelativeMatrix(node.getRelativeMatrix());
//			newnode.setOccId(node.getOccId());
//			newnode.setOccpath(node.getOccpath());
//			newnode.setInstanceIdentifier(node.getInstanceIdentifier());
//			newnode.setPart(copyCmlightPart(node.getPart()));
//			CmCommonNodeUtil.setTreeNodeFzbm(newnode);
//			pbomlist.add(newnode);
//
//		} else {
//			if(isEmpty(node.getPart().getOperType()) && "new".equals(same.getPart().getOperType())){
//				node.getPart().setOperType("new");
//			}
//			checkNodeIsEdit(node);
//		}
//		if (isPackage(node)) {
//			for (CmTreeNode brother : node.getListNode()) {
//				if(!checkHasSameNodeInList(brother, pbomlist)){
//					copyNodeToList(brother,(CmTreeNode)node.getParent(),pbomlist,flag);
//				}
//				else{
//					checkNodeIsEdit(brother);
//				}
//			}
//		}
//	}
	/**
	 * 将节点保存到原始的节点list中去
	 *
	 * @param node
	 * @param pbomlist
	 *
	 */
	public static void copyNodeToList(CmTreeNode node, CmTreeNode parent,
			HashMap<CmTreeNode, CmTreeNode> pbomlist, boolean flag, boolean needCheckIsEdit,boolean isInit) {
		CmTreeNode newnode = createNewNode(node);
		CmTreeNode same = pbomlist.get(node);
		if (null == same) {
			if (flag) {
				node.getPart().setOperType("new");
				newnode.getPart().setOperType("new");
			}
			CmTreeNode cmparent = pbomlist.get(parent);
			newnode.setParent(cmparent);
			newnode.setMatrix(node.getMatrix());
			newnode.setRelativeMatrix(node.getRelativeMatrix());
			newnode.setOccId(node.getOccId());
			newnode.setOccpath(node.getOccpath());
			newnode.setInstanceIdentifier(node.getInstanceIdentifier());
			if (CmScrollPaneTree.partMap == null) {
				CmScrollPaneTree.partMap = new HashMap<String, CmLightPart>();
			}
			CmLightPart cpart = CmScrollPaneTree.partMap.get(node.getPart().getPartNumber());
			if (cpart == null) {
				CmScrollPaneTree.partMap.put(node.getPart().getPartNumber(), copyCmlightPart(node.getPart()));
				newnode.setPart(copyCmlightPart(node.getPart()));
			} else {
				newnode.setPart(copyCmlightPart(cpart));
			}
			pbomlist.put(node, newnode);

			//   start
			if (CmScrollPaneTree.structureList == null) {
				CmScrollPaneTree.structureList = new HashMap<CmTreeNode, List<CmTreeNode>>();
			}

			if (CmScrollPaneTree.nodeMap == null) {
				CmScrollPaneTree.nodeMap = new HashMap<String, CmTreeNode>();
			}
			CmTreeNode nodeM = CmScrollPaneTree.nodeMap.get(node.getPart().getPartNumber());

			if (nodeM == null) {
				CmScrollPaneTree.nodeMap.put(node.getPart().getPartNumber(), node);
				List<CmTreeNode> slist = CmScrollPaneTree.structureList.get(node);

				if (slist == null) {
					slist = new ArrayList<CmTreeNode>();
					Enumeration en = node.children();
					while (en.hasMoreElements()) {
						CmTreeNode cnode = (CmTreeNode) en.nextElement();
						slist.add(cnode);
						if (isPackage(cnode)) {
							for (CmTreeNode bNode : cnode.getListNode()) {
								slist.add(bNode);
							}
						}
					}
					CmScrollPaneTree.structureList.put(node, slist);
				}
			} else {
				List<CmTreeNode> slist = CmScrollPaneTree.structureList.get(node);
				if (slist == null) {
					slist = new ArrayList<CmTreeNode>();
					List<CmTreeNode> mlist = CmScrollPaneTree.structureList.get(nodeM);
					if(mlist == null){
						Enumeration en = node.children();
						while (en.hasMoreElements()) {
							CmTreeNode cnode = (CmTreeNode) en.nextElement();
							slist.add(cnode);
							if (isPackage(cnode)) {
								for (CmTreeNode bNode : cnode.getListNode()) {
									slist.add(bNode);
								}
							}
						}
						CmScrollPaneTree.structureList.put(node, slist);
					}else{
						CmScrollPaneTree.structureList.put(node, mlist);
					}
				}
			}
			//end
		} else {
			if (isEmpty(node.getPart().getOperType()) && "new".equals(same.getPart().getOperType())) {
				node.getPart().setOperType("new");
				CmCommonStringUtil.updateNodeOperType(node, "new");
			}
			if (needCheckIsEdit) {
				checkNodeIsEdit(node);
			}
		}
		if (isPackage(node)) {
			for (CmTreeNode brother : node.getListNode()) {
				if (pbomlist.get(brother) == null) {
					copyNodeToList(brother, (CmTreeNode) node.getParent(), pbomlist, flag, needCheckIsEdit,isInit);
				} else {
					checkNodeIsEdit(brother);
				}
			}
		}
	}

	public void updateRelateOperation(CmTreeNode node,CmTree pbomTree){
		checkPbomTreeNodeIsEdit(node);
		PbomTreeEditReportAction.updatePbomTreeEditReport();
		BomTreeReportAction.updateBomReport();
		if(null!=pbomTree){
			pbomTree.updateUI();
		}
	}
	/**
	 * 判断节点是否在list中
	 * @author chenyunlong
	 * @date  2013-4-24
	 * @param node
	 * @param editList
	 * @return
	 *
	 */
	public static CmTreeNode checkTheNodeIsInList(CmTreeNode node,List<CmTreeNode> editList){
		CmTreeNode comNode=null;
		for (CmTreeNode cmnode : editList) {
			if (("assistant".equals(cmnode.getPart().getPartType()) && isCommonPart(cmnode.getPart(), node.getPart()))
					|| ("middle2".equals(cmnode.getPart().getPartType()) && checkIsSameMiddleNode(node, cmnode))
					|| !isNewNode(cmnode.getPart().getPartType()) && checkNodeIsSame(cmnode, node)) {
				comNode=cmnode;
				break;
			}
		}
		return comNode;
	}

	/**
	 * 节点是否被移动过
	 * @date  2013-1-29
	 * @param node
	 *
	 */
//	public static void isMoved(CmTreeNode node){
//		List<CmTreeNode> list= CmScrollPaneTree.pbomlist;
//		for (CmTreeNode pbom : list) {
//			boolean flag = false;
//			if (checkNodeIsSame(node, pbom)) {
//				if (null!=pbom.getParent() && null!=node.getParent() && checkNodeIsSame((CmTreeNode)node.getParent(), (CmTreeNode)pbom.getParent())) {
//					node.getPart().setMove(false);
//				} else {
//					// 此节点被移动过
//					node.getPart().setMove(true);
//					flag = true;
//				}
//				if (isSamePartWithoutVersion(node.getPart(), pbom.getPart())) {
//					node.getPart().setEdit(false);
//				} else {
//					// 此节点被编辑过
//					node.getPart().setEdit(true);
//				}
//				if(isPackage(node)){
//					for(CmTreeNode brother:node.getListNode()){
//						for (CmTreeNode pbom2 : list) {
//							if (checkNodeIsSame(brother, pbom2)) {
//								if (null!=pbom2.getParent() && null!=node.getParent() && checkNodeIsSame((CmTreeNode)node.getParent(), (CmTreeNode)pbom2.getParent())) {
//									node.getPart().setMove(false);
//								} else {
//									// 此节点被移动过
//									brother.getPart().setMove(true);
//								}
//								if (isSamePartWithoutVersion(brother.getPart(), pbom2.getPart())) {
//									brother.getPart().setEdit(false);
//								} else {
//									// 此节点被编辑过
//									brother.getPart().setEdit(true);
//								}
//							}
//						}
//					}
//				}
//			}
//			if(flag && node.getPart().isMove()){
//				break;
//			}
//		}
//	}

	/**
	 * 节点是否被移动过
	 *
	 * @date 2013-1-29
	 * @param node
	 *
	 */
	public static void isMoved(CmTreeNode node) {
		CmTreeNode pbom = CmScrollPaneTree.pbomMap.get(node);
		if (pbom != null) {
			boolean flag = false;
			if (checkNodeIsSame(node, pbom)) {
				if (null != pbom.getParent()
						&& null != node.getParent()
						&& checkNodeIsSame((CmTreeNode) node.getParent(),
								(CmTreeNode) pbom.getParent())) {
					node.getPart().setMove(false);
				} else {
					// 此节点被移动过
					node.getPart().setMove(true);
					flag = true;
				}
				if (isSamePartWithoutVersion(node.getPart(), pbom.getPart())) {
					node.getPart().setEdit(false);
				} else {
					// 此节点被编辑过
					node.getPart().setEdit(true);
				}
				if (isPackage(node)) {
					for (CmTreeNode brother : node.getListNode()) {
						CmTreeNode pbom2 = CmScrollPaneTree.pbomMap
								.get(brother);
						if (pbom2 != null) {
							if (checkNodeIsSame(brother, pbom2)) {
								if (null != pbom2.getParent()
										&& null != node.getParent()
										&& checkNodeIsSame(
												(CmTreeNode) node.getParent(),
												(CmTreeNode) pbom2.getParent())) {
									node.getPart().setMove(false);
								} else {
									// 此节点被移动过
									brother.getPart().setMove(true);
								}
								if (isSamePartWithoutVersion(brother.getPart(),
										pbom2.getPart())) {
									brother.getPart().setEdit(false);
								} else {
									// 此节点被编辑过
									brother.getPart().setEdit(true);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 判断节点的结构是否改变
	 * @author chenyunlong
	 * @date  2013-5-25
	 * @param node
	 * @param isParent  是检查当前节点的结构还是父节点的结构
	 *
	 */
//	@SuppressWarnings("unchecked")
//	public static void checkNodeIsChangeOfStructure(CmTreeNode node,boolean isParent){
//		CmTreeNode obj = null;
//		if(isParent){
//			obj = (CmTreeNode) node.getParent();
//		}else{
//			obj = node;
//		}
//		List<CmTreeNode> pbomList= CmScrollPaneTree.pbomlist;
//		List<CmTreeNode> oldlist = new ArrayList<CmTreeNode>();
//		for(CmTreeNode pbom: pbomList){
//			if(null !=pbom.getParent()
//					&& !"new".equals(pbom.getPart().getOperType())
//					&& CmCommonNodeUtil.checkHasSameOccpath((CmTreeNode)pbom.getParent(), obj)){
//				oldlist.add(pbom);
//			}
//		}
//		List<CmTreeNode> newlist = new ArrayList<CmTreeNode>();
//		Enumeration children = obj.children();
//		while(children.hasMoreElements()){
//			CmTreeNode child = (CmTreeNode) children.nextElement();
//			newlist.add(child);
//			if(isPackage(child)){
//				for(CmTreeNode brother:child.getListNode()){
//					newlist.add(brother);
//				}
//			}
//		}
//		if(oldlist.size() != newlist.size()){
//			obj.getPart().setChangeOfStructure(true);
//			if(isPackage(obj)){
//				for(CmTreeNode brother:obj.getListNode()){
//					brother.getPart().setChangeOfStructure(true);
//				}
//			}
//		}else{
//			boolean flag = false;
//			if(oldlist.size()==0){
//				flag = true;
//			}else{
//				for(int i=0;i<oldlist.size();i++){
//					if(flag){
//						flag = false;
//					}
//					for (Iterator<CmTreeNode> it = newlist.iterator(); it.hasNext();) {
//						CmTreeNode cmnode = it.next();
//						if (CmCommonNodeUtil.checkNodeIsCommon(cmnode, oldlist.get(i))){
//							it.remove();
//							flag = true;
//							break;
//						}
//					}
//					if(!flag){
//						break;
//					}
//				}
//			}
//			if(flag){
//				obj.getPart().setChangeOfStructure(false);
//				if(isPackage(obj)){
//					for(CmTreeNode brother:obj.getListNode()){
//						brother.getPart().setChangeOfStructure(false);
//					}
//				}
//			}else{
//				obj.getPart().setChangeOfStructure(true);
//				if(isPackage(obj)){
//					for(CmTreeNode brother:obj.getListNode()){
//						brother.getPart().setChangeOfStructure(true);
//					}
//				}
//			}
//		}
//	}

	/**
	 * 判断节点的结构是否改变
	 *
	 * @author chenyunlong
	 * @date 2013-5-25
	 * @param node
	 * @param isParent 是检查当前节点的结构还是父节点的结构
	 */
	@SuppressWarnings("unchecked")
	public static void checkNodeIsChangeOfStructure(CmTreeNode node, boolean isParent) {
		CmTreeNode obj = null;
		if (isParent) {
			obj = (CmTreeNode) node.getParent();
		} else {
			obj = node;
		}

		List<CmTreeNode> oldlist = CmScrollPaneTree.structureList.get(obj);
		if (oldlist == null) {
			oldlist = new ArrayList<CmTreeNode>();
		}
		List<CmTreeNode> newlist = new ArrayList<CmTreeNode>();
		Enumeration children = obj.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			newlist.add(child);
			if (isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					newlist.add(brother);
				}
			}
		}
		if (oldlist.size() != newlist.size()) {
			obj.getPart().setChangeOfStructure(true);
			obj.getPart().setEditWithChild(true);
//			obj.getPart().setEdit(true);
			if (isPackage(obj)) {
				for (CmTreeNode brother : obj.getListNode()) {
					brother.getPart().setChangeOfStructure(true);
					brother.getPart().setEditWithChild(true);
					brother.getPart().setEdit(true);
				}
			}
		} else {
			boolean flag = false;
			if (oldlist.size() == 0) {
				flag = true;
			} else {
				for (int i = 0; i < oldlist.size(); i++) {
					if (flag) {
						flag = false;
					}
					for (Iterator<CmTreeNode> it = newlist.iterator(); it.hasNext();) {
						CmTreeNode cmnode = it.next();
						if (CmCommonNodeUtil.checkNodeIsCommon(cmnode, oldlist.get(i))) {
							it.remove();
							flag = true;
							break;
						}
					}
					if (!flag) {
						break;
					}
				}
			}
			if (flag) {
				obj.getPart().setChangeOfStructure(false);
				if (isPackage(obj)) {
					for (CmTreeNode brother : obj.getListNode()) {
						brother.getPart().setChangeOfStructure(false);
					}
				}
			} else {
				obj.getPart().setChangeOfStructure(true);
				obj.getPart().setEditWithChild(true);
//				obj.getPart().setEdit(true);
				if (isPackage(obj)) {
					for (CmTreeNode brother : obj.getListNode()) {
						brother.getPart().setChangeOfStructure(true);
						brother.getPart().setEditWithChild(true);
//						brother.getPart().setEdit(true);
					}
				}
			}
		}
	}

	public static List<CmTreeNode> getAllChildNodesFromParent(CmTreeNode parent,List<CmTreeNode> list){
		Enumeration children = parent.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			list.add(child);
			getAllChildNodesFromParent(child, list);
			if (isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					list.add(brother);
					getAllChildNodesFromParent(brother, list);
				}
			}
		}
		return list;
	}
	/**
	 * PBOM结构按照编号排序的功能
	 * @date  2013-2-20
	 * @param cmnode
	 *
	 */
	public static void sortTheTreeNode(CmTreeNode cmnode){
		sortTheList(cmnode);
		Enumeration children = cmnode.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (isPackage(child)) {
				for (CmTreeNode brother : child.getListNode()) {
					sortTheTreeNode(brother);
				}
			}
			sortTheTreeNode(child);
		}
	}

	public static void sortTheList(CmTreeNode cmnode){
		if(cmnode.children().hasMoreElements()){
			List<CmTreeNode> list = new ArrayList<CmTreeNode>();
			Enumeration children = cmnode.children();
			while (children.hasMoreElements()) {
				CmTreeNode child = (CmTreeNode) children.nextElement();
				list.add(child);
			}

			if(list.size()>1){
				for(CmTreeNode node:list){
					node.removeFromParent();
				}
				Collections.sort(list, new Comparator<CmTreeNode>(){

					@Override
					public int compare(CmTreeNode o1, CmTreeNode o2) {
						return o1.getPart().getPartNumber() .compareTo(o2.getPart().getPartNumber());
					}
				});

				for(CmTreeNode sortnode:list){
					cmnode.add(sortnode);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static CmTreeNode getPbomPlanning(CmTreeNode node,CmTreeNode unplanningNode) {
		boolean flag=updateNodeOfPlanning(node,true);
		if(!flag){
			unplanningNode = node;
		}
		if (isPackage(node)) {
			for (CmTreeNode brother : node.getListNode()) {
				unplanningNode = getPbomPlanning(brother,unplanningNode);
			}
		}
		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			unplanningNode = getPbomPlanning(child,unplanningNode);
		}
		return unplanningNode;
	}

	public static boolean updateNodeOfPlanning(CmTreeNode node,boolean flag) {
		CmTreeNode common = null;
		for(CmTreeNode cm:planingList){
			if(isCommon(cm, node)){
				common = cm;
				break;
			}
		}
		if(null == common){

			log.debug(node.getPart().getPartNumber()+"========EBOM  oid==========="+node.getPart().getOid());
			WTPart part = PBOMEditorToWCIntf.getPlanningPart(node.getPart().getPartNumber(), String.valueOf(node.getPart()
					.getOid()), CmConnectFrame.planningOid,flag);

			if(null!=part){
				log.debug("====****========PBOM  oid========******==="+part.getPersistInfo().getObjectIdentifier().getId());
				log.debug("==****===PBOM  oid====EBOM  oid=******==="+node.getPart().getOid()==part.getPersistInfo().getObjectIdentifier().getId()+"");
				node.getPart().setOid(Long.valueOf(part.getPersistInfo().getObjectIdentifier().getId()));
				long containerId = PersistenceHelper.getObjectIdentifier(part.getContainer()).getId();
				node.getPart().setContainerId(containerId);
				node.getPart().setLifecycle(part.getLifeCycleState().getDisplay(Locale.CHINA));
				node.getPart().setVersion(part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());
				planingList.add(node);
				return true;
			}
			else{
				log.debug("====&&&&&&&&****节点"+node.toString()+"*******更新planning视图失败************&&&&&&&&&==");

				return false;

			}
		}else{
			node.getPart().setOid(common.getPart().getOid());
			node.getPart().setContainerId(common.getPart().getContainerId());
			node.getPart().setLifecycle(common.getPart().getLifecycle());
			node.getPart().setVersion(common.getPart().getVersion());
			return true;
		}
	}
	/**
	 * 判断第一个节点是否有planning
	 * @author chenyunlong
	 * @date  2013-3-21
	 * @param node
	 * @param flag
	 * @return
	 *
	 */
	public static WTPart isHasPlanning(CmTreeNode node,boolean flag){
		WTPart part = PBOMEditorToWCIntf.getPlanningPart(node.getPart().getPartNumber(), String.valueOf(node.getPart()
				.getOid()), CmConnectFrame.planningOid,flag);
		return part;
	}

	/**
	 * 更新多个节点的操作状态：删除（移除、设置虚拟件、剪切）、变更（属性、节点、属性和节点）、新增（中间件、辅件、粘贴）
	 * @param node
	 * @param type
	 *
	 */
	@SuppressWarnings("unchecked")
	public static void updateNodeOperType(CmTreeNode node, String type) {
		updateOperType(node, type);
		Enumeration children = node.children();
		while (children.hasMoreElements()) {
			updateNodeOperType((CmTreeNode) children.nextElement(), type);
		}
	}
	/**
	 * 更新单个节点的操作状态：删除（移除、设置虚拟件、剪切）、变更（属性、节点、属性和节点）、新增（中间件、辅件、粘贴）
	 *
	 * 节点的变更有粘贴操作产生
	 * @param node
	 * @param type
	 *
	 */
//	public static void updateOperType(CmTreeNode node, String type) {
//		if (!"new".equals(node.getPart().getOperType()) && null!=CmScrollPaneTree.pbomlist) {
//			List<CmTreeNode> list = new ArrayList<CmTreeNode>();
//			if(isPackage(node)){
//				for(CmTreeNode brother:node.getListNode()){
//					list.add(brother);
//				}
//			}
//			list.add(node);
//			for(CmTreeNode cmnode:CmScrollPaneTree.pbomlist){
//				if(isCommon(node, cmnode)){
//					CmTreeNode objnode = getNodeFromList(cmnode,list);
//					if(null != objnode){
//						objnode.getPart().setOperType(type);
//						cmnode.getPart().setOid(node.getPart().getOid());
//						cmnode.getPart().setOperType(type);
//					}
//				}
//			}
//		}
//	}

	/**
	 * 更新单个节点的操作状态：删除（移除、设置虚拟件、剪切）、变更（属性、节点、属性和节点）、新增（中间件、辅件、粘贴）
	 *
	 * 节点的变更有粘贴操作产生
	 *
	 * @param node
	 * @param type
	 *
	 */
	public static void updateOperType(CmTreeNode node, String type) {
		if (!"new".equals(node.getPart().getOperType())
				&& null != CmScrollPaneTree.pbomMap) {
			List<CmTreeNode> list = new ArrayList<CmTreeNode>();
			if (isPackage(node)) {
				for (CmTreeNode brother : node.getListNode()) {
					list.add(brother);
				}
			}
			list.add(node);
			for (CmTreeNode cmnode : CmScrollPaneTree.pbomMap.values()) {
				if (isCommon(node, cmnode)) {
					CmTreeNode objnode = getNodeFromList(cmnode, list);
					if (null != objnode) {
						objnode.getPart().setOperType(type);
						cmnode.getPart().setOid(node.getPart().getOid());
//						cmnode.setAfterDeleteParentOid(node
//								.getAfterDeleteParentOid());// 删除节点信息记录
//						cmnode.setAfterDeleteParentNode(node
//								.getAfterDeleteParentNode());// 删除节点信息
						cmnode.getPart().setOperType(type);
					}
				}
			}
		}
	}


	public static void setIsNewTop(CmTreeNode node) {
		if (null != CmScrollPaneTree.pbomMap) {
			List<CmTreeNode> list = new ArrayList<CmTreeNode>();
			if (isPackage(node)) {
				for (CmTreeNode brother : node.getListNode()) {
					list.add(brother);
				}
			}
			list.add(node);
			CmTreeNode initNode;
			for(CmTreeNode n:list){
				initNode = CmScrollPaneTree.pbomMap.get(n);
				if(initNode!=null){
					initNode.setNewTopNode(true);
					n.setNewTopNode(true);
				}
			}
		}
	}

	public static void setIsChangeCountNode(CmTreeNode node) {
		if (null != CmScrollPaneTree.pbomMap) {
			List<CmTreeNode> list = new ArrayList<CmTreeNode>();
			if (isPackage(node)) {
				for (CmTreeNode brother : node.getListNode()) {
					list.add(brother);
				}
			}
			list.add(node);
			CmTreeNode initNode;
			for(CmTreeNode n:list){
				initNode = CmScrollPaneTree.pbomMap.get(n);
				if(initNode!=null){
					initNode.setChangeCountNode(true);
					n.setChangeCountNode(true);
				}
			}
		}
	}

	public static void setIsNewTop(List<CmTreeNode> nodeList){
		if(nodeList!=null)
		for(int i=0;i<nodeList.size();i++){
			setIsNewTop(nodeList.get(i));
		}
	}

	public static boolean isCanDelNode(CmTreeNode node){
		if(node.isChangeCountNode()){
			return false;
		}

		List<Boolean> isCanDelList = new ArrayList<Boolean>();
		getIsCanDelList(node,isCanDelList);
		if(isCanDelList.isEmpty()){
			return true;
		}else{
			return false;
		}


	}

	public static boolean isChangeCount(CmTreeNode node){

		List<Boolean> isCanDelList = new ArrayList<Boolean>();
		getIsCanDelList(node,isCanDelList);
		if(isCanDelList.isEmpty()){
			return true;
		}else{
			return false;
		}


	}

	public static void getIsCanDelList(CmTreeNode node,List<Boolean> isCanDelList){
		CmTreeNode pNode = (CmTreeNode)node.getParent();
		if(pNode!=null){
			if(pNode.isNewTopNode()){
				isCanDelList.add(false);
			}else{
				getIsCanDelList(pNode,isCanDelList);
			}

		}
	}

	public static void updateNodeOperTypeExpand(CmTreeNode node, String type) {
		updateOperType(node, type);
	}





	/**
	 * 从list中查找出对应的节点
	 * @date  2013-2-27
	 * @param node
	 * @param list
	 * @return
	 *
	 */
	public static CmTreeNode getNodeFromList(CmTreeNode node,List<CmTreeNode> list){
		CmTreeNode obj=null;
		for(CmTreeNode brother:list){
			if(isSameNode(node, brother)){
				obj = brother;
				list.remove(brother);
				break;
			}
		}
		return obj;
	}

	public static int getCommonNodeSizeFromList(CmTreeNode node,List<CmTreeNode> list){
		int i=0;
		for(CmTreeNode brother:list){
			if(isCommon(node, brother)){
				i++;
			}
		}
		return i;
	}

	public static CmCancelNode createNewCancelNode(CmTreeNode node,CmTreeNode newparent,boolean isEbom,String operType){
		List<CmTreeNode> childList=new ArrayList<CmTreeNode>();
		CmTreeNode objnode= copyNewNode(node);
		Enumeration children=objnode.children();
		while(children.hasMoreElements()){
			childList.add((CmTreeNode)children.nextElement());
		}
		CmCancelNode cancel=new CmCancelNode();
		cancel.setObjNode(objnode);
		cancel.setObjindex(objnode.getPart().getMiddleIndex());
		cancel.setParentNode((CmTreeNode)node.getParent());
		cancel.setParetnindex(((CmTreeNode)node.getParent()).getPart().getMiddleIndex());
		cancel.setChildNode(childList);
		cancel.setNewParentNode(newparent);
		if(!isEmpty(operType)){
			cancel.setOperation(operType);
		}else if(isEbom){
			cancel.setOperation("create");
		}else{
			cancel.setOperation("move");
		}
		return cancel;
	}

	/**
	 * 节点的复制
	 * @date  2013-2-21
	 * @param old
	 * @return
	 *
	 */
	public static CmTreeNode copyNewNode(CmTreeNode old){
		CmTreeNode newnode =copyCmTreeNode(old);
		Enumeration children=old.children();
		while(children.hasMoreElements()){
			CmTreeNode child=(CmTreeNode) children.nextElement();
			newnode.add(copyNewNode(child));
		}
		return newnode;
	}
	/**
	 * 是否存在父子节点
	 * @author chenyunlong
	 * @date  2013-3-26
	 * @param paths
	 * @return
	 *
	 */
	public static CmTreeNode isExistParentAndChildNode(TreePath[] paths){
		CmTreeNode cmtreenode = null;
		for (int i = 0; i < paths.length; i++) {
			CmTreeNode node = (CmTreeNode) paths[i].getLastPathComponent();
			for(int j = i+1; j < paths.length; j++){
				CmTreeNode cmnode = (CmTreeNode) paths[j].getLastPathComponent();
				CmTreeNode parent = (CmTreeNode) cmnode.getParent();
				if (node.getOccId().equals(parent.getOccId()) && !checkAllChildIsExist(paths,parent)) {
					cmtreenode = node;
					break;
				}
			}
			if(null != cmtreenode){
				break;
			}
		}
		return cmtreenode;
	}
	/**
	 * 某一节点的子零件是否全部都选中
	 * @author chenyunlong
	 * @date  2013-4-15
	 * @param paths
	 * @param parent
	 * @return
	 *
	 */
	public static boolean checkAllChildIsExist(TreePath[] paths,CmTreeNode parent){
		boolean flag = true;
		Enumeration children = parent.children();
		while(children.hasMoreElements()){
			CmTreeNode child = (CmTreeNode) children.nextElement();
			boolean isExit = false;
			for(TreePath cmpath:paths){
				CmTreeNode node = (CmTreeNode) cmpath.getLastPathComponent();
				if (isSameNode(node,child)) {
					isExit = true;
					break;
				}
			}
			if(!isExit){
				flag = false;
				break;
			}
		}
		return flag;
	}
	/**
	 * 如果父节点相同，删除一个节点，那个这些节点下的节点都应该被删除
	 * @author chenyunlong
	 * @date  2013-3-28
	 * @param node
	 * @param deleteNode
	 * @param parent
	 *
	 */
	@SuppressWarnings("unchecked")
	public static void deleteAssistWithCommonParent(CmTreeNode root,CmTreeNode deleteNode,CmTreeNode parent){
		   Enumeration children = root.children();
		      while (children.hasMoreElements()) {
		         CmTreeNode child = (CmTreeNode) children.nextElement();
		         if(CmCommonNodeUtil.checkNodeIsCommon(child, parent)){
		        	 Enumeration item = child.children();
				      while (item.hasMoreElements()) {
				    	  CmTreeNode cmchild = (CmTreeNode) item.nextElement();
				    	  if(CmCommonNodeUtil.checkNodeIsCommon(cmchild, deleteNode)){
				    		  cmchild.removeFromParent();
			        		  break;
				    	  }
				      }
				      if(isPackage(child)){
				    	  for(CmTreeNode bro:child.getListNode()){
				    		  Enumeration broitem = bro.children();
						      while (broitem.hasMoreElements()) {
						    	  CmTreeNode brochild = (CmTreeNode) broitem.nextElement();
						    	  if(CmCommonNodeUtil.checkNodeIsCommon(brochild, deleteNode)){
						    		  brochild.removeFromParent();
					        		  break;
						    	  }
						      }
				    	  }
				      }
				      checkNodeIsChangeOfStructure(child, false);
		         }else{
		        	 deleteAssistWithCommonParent(child,deleteNode,parent);
		         }
		         if(isPackage(child)){
		        	 for(CmTreeNode brother:child.getListNode()){
		        		 deleteAssistWithCommonParent(brother,deleteNode,parent);
		        	 }
		         }
		   }
	   }
	/**
	 * 如果节点相同，在某个其中某个节点下新增一个辅件，那么其它节点都应该新增这个辅件
	 * @author chenyunlong
	 * @date  2013-3-28
	 * @param node
	 * @param addNode
	 * @param parent
	 *
	 */
	public static void addCommonAssistNodeWithCommonParent(CmTreeNode node,CmTreeNode addNode,CmTreeNode parent){
		Enumeration en = node.children();
	      while (en.hasMoreElements()) {
	         CmTreeNode item = (CmTreeNode) en.nextElement();
	         if(isCommon(item, parent)){
	        	 if(null != addNode.getParent()){
	        		 CmTreeNode assist = copyCmTreeNode(addNode);
	        		 item.add(assist);
	        	 }
	        	 else{
	        		 item.add(addNode);
	        	 }
	        	 if(isPackage(item)){
	        		 for(CmTreeNode brother:item.getListNode()){
	        			 if(null != addNode.getParent()){
	        				 CmTreeNode assist = copyCmTreeNode(addNode);
	        				 brother.add(assist);
	    	        	 }
	    	        	 else{
	    	        		 brother.add(addNode);
	    	        	 }
	        		 }
	        	 }
	        	 checkNodeIsChangeOfStructure(item, false);
	         }
	         addCommonAssistNodeWithCommonParent(item,addNode,parent);
	         if(isPackage(item)){
	        	 for(CmTreeNode cm:item.getListNode()){
	        		 addCommonAssistNodeWithCommonParent(cm,addNode,parent);
	        	 }
	         }
	      }
	}

	/**
	 * 如果节点相同，在某个其中某个节点下新增一个中间件，那么其它节点都应该新增这个中间件，如果中间件的数量有多少，就新增多少个
	 * @author chenyunlong
	 * @date  2013-4-9
	 * @param node
	 * @param addNode
	 * @param useCount 中间件数量
	 * @param parent
	 *
	 */
	public static void addCommonMiddleNodeWithCommonParent(CmTreeNode node,CmTreeNode addNode,int useCount,CmTreeNode parent){
		Enumeration en = node.children();
	      while (en.hasMoreElements()) {
	         CmTreeNode item = (CmTreeNode) en.nextElement();
	         if(isCommon(item, parent)){
	        	 for(int i=0;i<useCount;i++){
	        		 if(null != addNode.getParent()){
	        			 CmTreeNode middle = copyCmTreeNode(addNode);
	        			 middle.getPart().setMiddleIndex(CmCommonStringUtil.emptyToNumber2(item.getPart().getMiddleIndex()) +"10"+i);
	        			 item.add(middle);
    	        		 addOccpathOfNewNode(middle);
    	        		 CmCommonStringUtil.addNodeToCheckList(middle);
	        		 }
	        		 else{
	        			 addNode.getPart().setMiddleIndex(CmCommonStringUtil.emptyToNumber2(item.getPart().getMiddleIndex()) +"10"+i);
	        			 item.add(addNode);
	        			 addOccpathOfNewNode(addNode);
	        			 CmCommonStringUtil.addNodeToCheckList(addNode);
	        		 }
	        		 checkNodeIsChangeOfStructure(item, false);
	        	 }
	        	 if(isPackage(item)){
	        		 for(CmTreeNode brother:item.getListNode()){
	        			 for(int i=0;i<useCount;i++){
	        				 if(null != addNode.getParent()){
	        					 CmTreeNode middle = copyCmTreeNode(addNode);
	        					 middle.getPart().setMiddleIndex(CmCommonStringUtil.emptyToNumber2(brother.getPart().getMiddleIndex()) +"10"+i);
		        				 brother.add(middle);
		    	        		 addOccpathOfNewNode(middle);
		    	        		 CmCommonStringUtil.addNodeToCheckList(middle);
		    	        	 }
		    	        	 else{
		    	        		 addNode.getPart().setMiddleIndex(CmCommonStringUtil.emptyToNumber2(brother.getPart().getMiddleIndex()) +"10"+i);
		    	        		 brother.add(addNode);
		    	        		 addOccpathOfNewNode(addNode);
		    	        		 CmCommonStringUtil.addNodeToCheckList(addNode);
		    	        	 }
	    	        	 }
	        			 checkNodeIsChangeOfStructure(brother, false);
	        		 }
	        	 }
	         }
	         addCommonMiddleNodeWithCommonParent(item,addNode,useCount,parent);
	      }
	}

	/**
	 * 如果节点相同，在某个其中某个节点下新增一个中间件，那么其它节点都应该新增这个中间件，如果中间件的数量有多少，就新增多少个
	 * @author LongXiuChuan
	 * @date  2014-2-20
	 * @param node
	 * @param addNode
	 * @param useCount 中间件数量
	 * @param parent
	 *
	 */
	public static void addCommonMiddleNodeWithCommonParent2(CmTreeNode node,CmTreeNode addNode,int useCount,CmTreeNode parent){
		Enumeration en = node.children();
	      while (en.hasMoreElements()) {
	         CmTreeNode item = (CmTreeNode) en.nextElement();
	         if(isCommon(item, parent)){
	        	 for(int i=0;i<useCount;i++){
	        		 if(null != addNode.getParent()){
	        			 CmTreeNode middle = copyCmTreeNode(addNode);
	        			 middle.getPart().setMiddleIndex(CmCommonStringUtil.emptyToNumber2(item.getPart().getMiddleIndex()) +"10"+i);
	        			 middle.getPart().setUseCount(useCount);
	        			 item.add(middle);
    	        		 addOccpathOfNewNode(middle);
    	        		 CmCommonStringUtil.addNodeToCheckList(middle);
	        		 }
	        		 else{
	        			 addNode.getPart().setMiddleIndex(CmCommonStringUtil.emptyToNumber2(item.getPart().getMiddleIndex()) +"10"+i);
	        			 item.add(addNode);
	        			 addOccpathOfNewNode(addNode);
	        			 CmCommonStringUtil.addNodeToCheckList(addNode);
	        		 }
	        		 checkNodeIsChangeOfStructure(item, false);
	        	 }
	        	 if(isPackage(item)){
	        		 for(CmTreeNode brother:item.getListNode()){
	        			 for(int i=0;i<useCount;i++){
	        				 if(null != addNode.getParent()){
	        					 CmTreeNode middle = copyCmTreeNode(addNode);
	        					 middle.getPart().setMiddleIndex(CmCommonStringUtil.emptyToNumber2(brother.getPart().getMiddleIndex()) +"10"+i);
	        					 middle.getPart().setUseCount(useCount);
		        				 brother.add(middle);
		    	        		 addOccpathOfNewNode(middle);
		    	        		 CmCommonStringUtil.addNodeToCheckList(middle);
		    	        	 }
		    	        	 else{
		    	        		 addNode.getPart().setMiddleIndex(CmCommonStringUtil.emptyToNumber2(brother.getPart().getMiddleIndex()) +"10"+i);
		    	        		 brother.add(addNode);
		    	        		 addOccpathOfNewNode(addNode);
		    	        		 CmCommonStringUtil.addNodeToCheckList(addNode);
		    	        	 }
	    	        	 }
	        			 checkNodeIsChangeOfStructure(brother, false);
	        		 }
	        	 }
	         }
	         addCommonMiddleNodeWithCommonParent(item,addNode,useCount,parent);
	      }
	}

	/**
	 * 新增的工艺辅件或中间件的occpath要重新生成
	 * @author chenyunlong
	 * @date  2013-4-16
	 * @param obj
	 * @param parent
	 *
	 */
	public static void addOccpathOfNewNode(CmTreeNode obj){
		CmTreeNode parent = (CmTreeNode) obj.getParent();
		if(isEmpty(parent.getOccpath())){
			obj.setOccpath(parent.getOccId() +"+" +obj.getOccId());
		}else{
			obj.setOccpath(parent.getOccpath() +"+" +obj.getOccId());
		}
		obj.getPart().setParentPartNumber(parent.getPart().getPartNumber());
	}

	public static CmTreeNode getBrotherPackageNode(CmTreeNode node,CmTreeNode parent,int parentLevel){
		CmTreeNode node1 = node;
		CmTreeNode node2 = parent;
		for(int i=parentLevel+1;i<=node.getLevel();i++){
			node1 =getParentNode(node,node.getLevel(),i);
			if(null == node1){
				node2 = getChildNode(parent,node);
				break;
			}
			node2 = getChildNode(node2,node1);
		}
		return node2;
	}

	public static CmTreeNode getParentNode(CmTreeNode node,int objlevel,int paretnLevel){
		CmTreeNode parent = null;
		for(int i=objlevel;i>paretnLevel;i--){
			parent = (CmTreeNode) node.getParent();
		}
		return parent;
	}

	public static CmTreeNode getChildNode(CmTreeNode node,CmTreeNode childNode){
		CmTreeNode child = null;
		Enumeration children = node.children();
		while(children.hasMoreElements()){
			child =(CmTreeNode)children.nextElement();
			if((isPackage(child)&& isCommon(child, childNode))|| (!isPackage(child) &&isSameNode(child, childNode))){
				break;
			}
		}
		return child;
	}

	/**
	 * 判断两个节点是否完全相等
	 * 完全相同的零件occid肯定相同
	 * 位置是否相同，要判断他们的父节点的occpath是否相同
	 * @author chenyunlong
	 * @date  2013-4-18
	 * @param cmnode
	 * @param node
	 * @return
	 *
	 */
	public static boolean checkNodeIsSame(CmTreeNode cmnode,CmTreeNode node){
		if(isEqual(getParentOccpath(cmnode),getParentOccpath(node))){
			if(isNewNode(cmnode.getPart().getPartType()) && isEqual(cmnode.getPart().getPartNumber(), node.getPart().getPartNumber()) ){
				return true;
			}else if(!isNewNode(cmnode.getPart().getPartType()) && isEqual(cmnode.getOccId(), node.getOccId())){
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}

	/**
	 * 判断两个节点是否是同层兄弟节点,这两个节点有可能是同一个节点
	 * 相同的零件oid肯定要相同
	 * 是否在同一层，要看两个零件的父节点的occpath是否相同
	 * @author chenyunlong
	 * @date  2013-4-18
	 * @param cmnode
	 * @param node
	 * @return
	 *
	 */
	public static boolean checkNodeIsCommon(CmTreeNode cmnode,CmTreeNode node){
		if(isCommonPart(cmnode.getPart(),node.getPart())
				&& isEqual(getParentOccpath(cmnode),getParentOccpath(node))){
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 判断两个节点是否是同层兄弟节点，这两个节点不可能是同一个节点
	 * 相同的零件oid肯定要相同
	 * 是否在同一层，要看两个零件的父节点的occpath是否相同
	 * @author chenyunlong
	 * @date  2013-4-18
	 * @param cmnode
	 * @param node
	 * @return
	 *
	 */
	public static boolean checkNodeIsCommonButNotSame(CmTreeNode cmnode,CmTreeNode node){
		if(isEqual(cmnode.getPart().getPartNumber(),node.getPart().getPartNumber())
				&& null != cmnode.getParent()
				&& null != node.getParent()
				&& CmCommonNodeUtil.isEqualOfOccpath((CmTreeNode)cmnode.getParent(), (CmTreeNode)node.getParent())
				&& (isNewNode(cmnode.getPart().getPartType())
						||(!isNewNode(cmnode.getPart().getPartType())
								&&!isEqual(cmnode.getOccId(), node.getOccId())))){
			return true;
		}
		else{
			return false;
		}
	}

	public static String getParentOccpath(CmTreeNode cmnode){
		String occpath = cmnode.getOccpath();
		if(isEmpty(occpath)){
			return null;
		}else if( occpath.lastIndexOf("+") >0){
			return occpath.substring(0, occpath.lastIndexOf("+"));
		}
		else{
			return occpath;
		}
	}
	/**
	 * 节点是否是PBOM树上的节点
	 * @author chenyunlong
	 * @date  2013-4-18
	 * @param node
	 * @return
	 *
	 */
	public static boolean checkisPbomNode(CmTreeNode node){
		CmTreeNode root = getRootNode(node);
		if(null==root){
			return false;
		}else if("PBOM".equals(root.toString())){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 判断某个节点是否可以打包
	 * @author chenyunlong
	 * @date  2013-4-22
	 * @param node
	 * @param childList
	 * @return
	 *
	 */
	public static boolean checkNodeIsPackage(CmTreeNode node,CmTreeNode cmnode,List<CmTreeNode> childList){
		if(null == childList || childList.size()==0){
			childList = new ArrayList<CmTreeNode>();
			Enumeration children = node.getParent().children();
			while(children.hasMoreElements()){
				CmTreeNode child = (CmTreeNode) children.nextElement();
				childList.add(child);
			}
		}
		if("assistant".equals(node.getPart().getPartType())){
			return false;
		}else if("middle".equals(node.getPart().getPartType())){
			List<CmTreeNode> middleList = new ArrayList<CmTreeNode>();
			for(CmTreeNode cm:childList){
				if(checkNodeIsSame(cm, node)){
					middleList.add(cm);
				}
			}
			return middleList.size()>1?true:false;
		}else{
			boolean flag = false;
			for(CmTreeNode cm:childList){
				if(checkNodeIsCommonButNotSame(cm, node)){
					flag = true;
					break;
				}
			}
			return flag;
		}
	}
	/**
	 * 判断字符串中是否含有中文
	 * @author chenyunlong
	 * @date  2013-4-22
	 * @param str
	 * @return
	 *
	 */
	public static  boolean checkHasChinaese(String str){
		String num=str.trim();
		boolean flag = false;
		for(int i=0;i<num.trim().length();i++){
			if(num.substring(i, i+1).matches("[\\u4e00-\\u9fa5]+")){
				flag = true;
				break;
			}
		}
		return flag;
	}
	/**
	 * 判断是否是相同的中间件
	 * @author chenyunlong
	 * @date  2013-4-23
	 * @param node
	 * @param cmnode
	 * @return
	 *
	 */
	public static boolean checkIsCommonMiddleNodeButNotSame(CmTreeNode node,CmTreeNode cmnode){
		if("middle".equals(node.getPart().getPartType())
				&& isCommonPart(node.getPart(), cmnode.getPart())
				&& !isEqual(node.getPart().getMiddleIndex(), cmnode.getPart().getMiddleIndex())){
			return true;
		}
		else{
			return false;
		}

	}
	/**
	 * 判断是否是完全相同的中间件
	 * 考虑位置和索引
	 * @author chenyunlong
	 * @date  2013-4-23
	 * @param node
	 * @param cmnode
	 * @return
	 *
	 */
	public static boolean checkIsSameMiddleNode(CmTreeNode node,CmTreeNode cmnode){
		if("middle".equals(node.getPart().getPartType())
				&& isCommonPart(node.getPart(), cmnode.getPart())
				&& CmCommonNodeUtil.isEqualOfOccpath(node, cmnode)
				&& isEqual(node.getPart().getMiddleIndex(), cmnode.getPart().getMiddleIndex())){
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * 将dialog屏幕居中显示
	 * @author chenyunlong
	 * @date  2013-6-13
	 * @param dialog
	 *
	 */
	public static void setMiddleOnScreenWithDialog(JDialog dialog){
		int windowWidth = dialog.getWidth();                     //获得窗口宽
	    int windowHeight = dialog.getHeight();                   //获得窗口高
	    Toolkit kit = Toolkit.getDefaultToolkit();              //定义工具包
	    Dimension screenSize = kit.getScreenSize();             //获取屏幕的尺寸
	    int screenWidth = screenSize.width;                     //获取屏幕的宽
	    int screenHeight = screenSize.height;                   //获取屏幕的高
	    dialog.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);//设置窗口居中显示
	}


	public static void node2StructureMap(CmTreeNode node,HashMap<CmTreeNode,CmTreeNode> structrueMap	) {
		List<CmTreeNode> nodeList = new ArrayList<CmTreeNode>();

		if(CmCommonStringUtil.isPackage(node)){
			nodeList.addAll(node.getListNode());
		}
		nodeList.add(node);
		for(CmTreeNode n:nodeList){
			Enumeration<CmTreeNode> en =  n.children();
			CmTreeNode cNode = null;
			while(en.hasMoreElements()){
					cNode = en.nextElement();
					structrueMap.put(cNode, cNode);
					if(CmCommonStringUtil.isPackage(cNode)){
						for(CmTreeNode pNode:cNode.getListNode()){
							structrueMap.put(pNode, pNode);
						}
					}
					node2StructureMap(cNode,structrueMap);
			}
		}


	}
}
