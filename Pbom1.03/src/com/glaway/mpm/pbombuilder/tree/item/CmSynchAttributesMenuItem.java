package com.glaway.mpm.pbombuilder.tree.item;

import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmMenuItem;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class CmSynchAttributesMenuItem  extends CmMenuItem{
	private static final long serialVersionUID = 1L;
	private CmTree tree;
	private CmTreeNode currNode;
	private Window owner;

	public CmSynchAttributesMenuItem(CmTree tree, CmTreeNode currNode,Window owner) {
		 this.tree = tree;
	     this.currNode = currNode;
	     this.owner = owner;
	     setText("同步更新EBOM属性");
	     setIconStr("edit.gif");
	     setEnabled(displayValidate(this.currNode));
	}

	private boolean displayValidate(CmTreeNode node) {
		CmTreeNode rootPart = (CmTreeNode)tree.getRoot().children().nextElement();
		String mtype = node.getPart().getMtype();
		if(CmCommonStringUtil.isHasFilingOfObj(node)){
			return false;
		} else if (node == node.getRoot()) {
			return false;
		} else if (rootPart.getPart().getContainerId() != node.getPart().getContainerId()) {//借用件不能修改
			return false;
		} else if("自制件".equals(mtype)||"外配套件".equals(mtype)||"带料委外件".equals(mtype)||"不带料委外件".equals(mtype)){
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		synchAttributes(currNode);
	}

	@SuppressWarnings("unchecked")
	public void synchAttributes(CmTreeNode pbomNode){
		CmTree ebomtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
		CmTreeNode root = ebomtree.getRoot();
		CmTreeNode ebomNode = null;
		ebomNode = getEbomNodeFromPbomNode(root,pbomNode,ebomNode);
		if(ebomNode == null) {
			JOptionPane.showMessageDialog(owner, "没有找到对应的EBOM对象,同步更新失败！");
		} else {
			//更新PBOM树节点属性
			pbomNode.getPart().setRemark(ebomNode.getPart().getRemark());//备注
			pbomNode.getPart().setCmat(ebomNode.getPart().getCmat());//材料
			pbomNode.getPart().setPtc_material_name(ebomNode.getPart().getPtc_material_name());//材料名称
			pbomNode.getPart().setCmat_up(ebomNode.getPart().getCmat_up());//材料上标
			pbomNode.getPart().setCmat_down(ebomNode.getPart().getCmat_down());//材料下标
			pbomNode.getPart().setSetmark(ebomNode.getPart().getSetmark());//成套件标识
			pbomNode.getPart().setAdjustable(ebomNode.getPart().getAdjustable());//可调整
			pbomNode.getPart().setPhase_code(ebomNode.getPart().getPhase_code());//当前阶段
			pbomNode.getPart().setKeycomponent(ebomNode.getPart().getKeycomponent());//关重件标识
			pbomNode.getPart().setCsize(ebomNode.getPart().getCsize());//规格
			pbomNode.getPart().setCtype(ebomNode.getPart().getCtype());//零部件分类

			//pbomNode.getPart().setMtype(ebomNode.getPart().getCtype());
			pbomNode.getPart().setSecret(ebomNode.getPart().getSecret());//密级
			pbomNode.getPart().setEnditemin(ebomNode.getPart().getEnditemin());//所属成品
			pbomNode.getPart().setMindex(ebomNode.getPart().getMindex());//所属型号
			pbomNode.getPart().setCindex(ebomNode.getPart().getCindex());//图号
			pbomNode.getPart().setPtc_common_name(ebomNode.getPart().getPtc_common_name());//中文名称
//			pbomNode.getPart().setRouting(ebomNode.getPart().getRouting());//工艺路线
//			pbomNode.getPart().setCompany(ebomNode.getPart().getCompany());//设计单位
//			pbomNode.getPart().setProduct_index(ebomNode.getPart().getProduct_index());//工装代号
//			pbomNode.getPart().setDesigner(ebomNode.getPart().getDesigner());//设计者
//			pbomNode.getPart().setZzcj(ebomNode.getPart().getZzcj());//主制车间
//			pbomNode.getPart().setFzcj(ebomNode.getPart().getFzcj());//辅制车间
//			pbomNode.getPart().setMaterial(ebomNode.getPart().getMaterial());//材料牌号
//			pbomNode.getPart().setPzggbzh(ebomNode.getPart().getPzggbzh());//种规格标准号
//			pbomNode.getPart().setJstjbzh(ebomNode.getPart().getJstjbzh());//技术条件标准号
//			pbomNode.getPart().setJddj(ebomNode.getPart().getJddj());//精度等级
//			pbomNode.getPart().setZldj(ebomNode.getPart().getZldj());//质量等级
//			pbomNode.getPart().setClzt(ebomNode.getPart().getClzt());//材料状态
//			pbomNode.getPart().setCldw(ebomNode.getPart().getCldw());//材料单位
//			pbomNode.getPart().setZqclmc(ebomNode.getPart().getZqclmc());//增强材料名称
//			pbomNode.getPart().setJbclmc(ebomNode.getPart().getJbclmc());//基体材料名称
//			pbomNode.getPart().setZqclbzh(ebomNode.getPart().getZqclbzh());//增强材料标准号
//			pbomNode.getPart().setPindex(ebomNode.getPart().getPindex());//产品代号

			//设计资源库应用改造新增属性
			//start
			pbomNode.getPart().setShortname(ebomNode.getPart().getShortname());//物资简称
			pbomNode.getPart().setYxjb(ebomNode.getPart().getYxjb());//编码优选级别
			pbomNode.getPart().setBmzt(ebomNode.getPart().getBmzt());//编码状态
			pbomNode.getPart().setBmlx(ebomNode.getPart().getBmlx());//编码类型
			pbomNode.getPart().setStandardnumber(ebomNode.getPart().getStandardnumber());//标准号
			pbomNode.getPart().setMechanicalpropertyorhardness(ebomNode.getPart().getMechanicalpropertyorhardness());//机械性能等级或硬度
			pbomNode.getPart().setSurfacetreatment(ebomNode.getPart().getSurfacetreatment());//表面处理
			pbomNode.getPart().setHeattreatment(ebomNode.getPart().getHeattreatment());//热处理
			pbomNode.getPart().setProductform(ebomNode.getPart().getProductform());//产品型式
			pbomNode.getPart().setProductlevel(ebomNode.getPart().getProductlevel());//产品等级
			pbomNode.getPart().setPlatecscrewform(ebomNode.getPart().getPlatecscrewform());//板拧形式
			pbomNode.getPart().setIsimport(ebomNode.getPart().getIsimport());//是否进口
			pbomNode.getPart().setSpecialinstruction(ebomNode.getPart().getSpecialinstruction());//特殊说明
			pbomNode.getPart().setMeasureunit(ebomNode.getPart().getMeasureunit());//计量单位
			pbomNode.getPart().setType(ebomNode.getPart().getType());//型号
			pbomNode.getPart().setTypestandard(ebomNode.getPart().getTypestandard());//型号规格
			pbomNode.getPart().setQualitylevel(ebomNode.getPart().getQualitylevel());//质量等级
			pbomNode.getPart().setTotalstandard(ebomNode.getPart().getTotalstandard());//总规范
			pbomNode.getPart().setDetailstandard(ebomNode.getPart().getDetailstandard());//详细规范
			pbomNode.getPart().setPackagingform(ebomNode.getPart().getPackagingform());//封装形式
			pbomNode.getPart().setOutlinesize(ebomNode.getPart().getOutlinesize());//外形尺寸
			pbomNode.getPart().setSpecialcondition(ebomNode.getPart().getSpecialcondition());//专用条件
			pbomNode.getPart().setExtracondition(ebomNode.getPart().getExtracondition());//附加协议
			pbomNode.getPart().setMattype(ebomNode.getPart().getMattype());//材料类型

			pbomNode.getPart().setCmatnumber(ebomNode.getPart().getCmatnumber());//材料编号
			pbomNode.getPart().setMarknumber(ebomNode.getPart().getMarknumber());//牌号
			pbomNode.getPart().setSupplystate(ebomNode.getPart().getSupplystate());//供应状态
			pbomNode.getPart().setUsestandard(ebomNode.getPart().getUsestandard());//采用标准
			//end

			//即时更新PBOM对象属性
			CmLightPart pbomPart = pbomNode.getPart();
			long pbomOid = pbomNode.getPart().getOid();

			Map<String,String> ibaMap = new HashMap<String,String>();
			ibaMap.put("CMAT", pbomPart.getCmat());
			ibaMap.put("PTC_MATERIAL_NAME", pbomPart.getPtc_material_name());
			ibaMap.put("CMAT_UP", pbomPart.getCmat_up());
			ibaMap.put("CMAT_DOWN", pbomPart.getCmat_down());
			ibaMap.put("SETMARK", pbomPart.getSetmark());
			ibaMap.put("ADJUSTABLE", pbomPart.getAdjustable());
			ibaMap.put("PHASE_CODE", pbomPart.getPhase_code());
			ibaMap.put("KEYCOMPONENT", pbomPart.getKeycomponent());
			ibaMap.put("CSIZE", pbomPart.getCsize());
			ibaMap.put("CTYPE", pbomPart.getCtype());
			ibaMap.put("SECRET", pbomPart.getSecret());
			ibaMap.put("ENDITEMIN", pbomPart.getEnditemin());
			ibaMap.put("MINDEX", pbomPart.getMindex());
			ibaMap.put("CINDEX", pbomPart.getCindex());
			ibaMap.put("PTC_COMMON_NAME", pbomPart.getPtc_common_name());
//			ibaMap.put("ROUTING", pbomPart.getRouting());
//			ibaMap.put("COMPANY", pbomPart.getCompany());
//			ibaMap.put("PRODUCT_INDEX", pbomPart.getProduct_index());
//			ibaMap.put("DESIGNER", pbomPart.getDesigner());
//			ibaMap.put("ZZJC", pbomPart.getZzcj());
//			ibaMap.put("FZCJ", pbomPart.getFzcj());
//			ibaMap.put("MATERIAL", pbomPart.getMaterial());
//			ibaMap.put("PZGGBZH", pbomPart.getPzggbzh());
//			ibaMap.put("JSTJBZH", pbomPart.getJstjbzh());
//			ibaMap.put("JDDJ", pbomPart.getJddj());
//			ibaMap.put("ZLDJ", pbomPart.getZldj());
//			ibaMap.put("CLZT", pbomPart.getClzt());
//			ibaMap.put("CLDW", pbomPart.getCldw());
//			ibaMap.put("ZQCLMC", pbomPart.getZqclmc());
//			ibaMap.put("JBCLMC", pbomPart.getJbclmc());
//			ibaMap.put("ZQCLBZH", pbomPart.getZqclbzh());
//			ibaMap.put("PINDEX", pbomPart.getPindex());

			//System.out.println("----CmSynchAttributesMenuItem.synchAttributes()-------pbomOid:"+pbomOid+"    ibaMap:"+ibaMap);
//			PBOMEditorToWCIntf.savePartIBAValue(pbomOid, ibaMap);

			JOptionPane.showMessageDialog(owner, "同步更新EBOM属性完成！");
			pbomNode.getPart().setEdit(true);
			tree.updateUI();
		}
	}

	private CmTreeNode getEbomNodeFromPbomNode(CmTreeNode root,CmTreeNode pbomNode,CmTreeNode resultNode) {
		Enumeration<CmTreeNode> ebomChilds = root.children();
		while(ebomChilds.hasMoreElements()) {
			CmTreeNode ebomNode = ebomChilds.nextElement();
			if(ebomNode.getPart().getPartNumber().equals(pbomNode.getPart().getPartNumber())){
				resultNode = ebomNode;
				return ebomNode;
			} else {
				if(resultNode == null) {
					resultNode = getEbomNodeFromPbomNode(ebomNode,pbomNode,resultNode);
				} else {
					return resultNode;
				}
			}
		}
		return resultNode;
	}

	/**
	 * 对零件进行升版本
	 * 如果零件的状态是已归档----升大版本  A.4 → B.1
	 * 如果零件的状态不是已归档 --升小版本 A.4 → A.5
	 * @author chenyunlong
	 * @date  2013-5-24
	 * @param node
	 *
	 */
	public static void updateVersion(CmTreeNode node){
		if(getNodeUpdateVersionIndex(node)>0){
			return;
		}
		String[] version = node.getPart().getVersion().split("\\.");
		if(CmCommonStringUtil.isHasFilingOfObj(node)){//已归档，升大版本
			String bigVersion = version[version.length-2];
			if("space".equals(bigVersion)) {
				version[version.length-2] = "Z";
				version[version.length-1] = "1";
				node.getPart().setVersionIndex(2);
			} else if ("Z".equals(bigVersion)) {
				version[version.length-2] = "a";
				version[version.length-1] = "1";
				node.getPart().setVersionIndex(2);
			} else {
				char[] c1 = bigVersion.toCharArray();
				char big = (char)Integer.parseInt(String.valueOf((int)c1[0]+1));
				version[version.length-2] = String.valueOf(big);
				version[version.length-1] = "1";
				node.getPart().setVersionIndex(2);
			}
		}else{//未归档，升小版本
			String smallVersion = version[version.length-1];
			version[version.length-1] = String.valueOf(Integer.valueOf(smallVersion) +1);
			node.getPart().setVersionIndex(1);
		}
		String newVerson = "";
		if(version.length==3){
			newVerson =version[0] + ".";
		}
		newVerson += version[version.length-2] + "." + version[version.length-1];
		node.getPart().setVersion(newVerson);
		if(CmProcessChangeMenuItem.isFirstVersion(node)){
			node.getPart().setChange(false);//工艺更改标识
		}else{
			node.getPart().setChange(true);
		}
		node.getPart().setReversion(true);
		node.getPart().setLifecycle("正在工作");
	}

	/**
	 * 判断零件是升大版本还是小版本
	 * 升版本类型：  0-不升版，1-升小版，2-升大版
	 * @author chenyunlong
	 * @date  2013-6-5
	 * @param node
	 * @return
	 *
	 */
	public static int getNodeUpdateVersionIndex(CmTreeNode node){
		int index =0;
		if(node.getPart().getVersionIndex()>0){
			index = node.getPart().getVersionIndex();
		}else if(node.getPart().isChangeOfStructure()){
			if(CmCommonStringUtil.isHasFilingOfObj(node)){
				index = 2;
			}else{
				index = 1;
			}
		}else if(node.getPart().isEdit()){
			index =1 ;
		}
		return index;
	}
}
