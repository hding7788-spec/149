package com.glaway.mpm.pbombuilder.tree.action;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.item.CmProcessChangeMenuItem;
import com.glaway.mpm.pbombuilder.util.CmCommonNodeUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class PbomTreeUpdateAction extends CmAction {
	private static final long serialVersionUID = 1L;
	private CmTree ebomtree;
	private CmTree pbomtree;
	private static JButton button;
	private ImageIcon expandImage = new ImageIcon(CmUtil.getImageFromServer("ebom_update.gif"));
	private List<CmTreeNode> ebomlist;
	public static boolean isUpdate = false;

	public PbomTreeUpdateAction(CmTree etree,CmTree ptree) {
		this.ebomtree = etree;
		this.pbomtree = ptree;
		setIcon(expandImage);
		setToolTipText("同步EBOM");
		setEnabled(displayValidate());
	}

	private boolean displayValidate() {
		CmTreeNode firstPbomNode = (CmTreeNode) pbomtree.getRoot().children().nextElement();

		//如果PBOM的状态是已批准，则不允许在修改
		if(CmCommonStringUtil.isHasFilingOfObj(firstPbomNode)) {
			return false;
		}

		byte[] bytes =PBOMEditorToWCIntf.getPBOMXml(String.valueOf(firstPbomNode.getPart().getOid()));
		if(null == bytes || bytes.length==0){
			return false;
		}
		bytes = null;
		ebomlist = new ArrayList<CmTreeNode>();
		CmCommonStringUtil.getAllChildNodesFromParent(ebomtree.getRoot(), ebomlist);
		for (Iterator<CmTreeNode> it = ebomlist.iterator(); it.hasNext();) {
			CmTreeNode cmnode = it.next();
			if (cmnode.getPart().getEchangeIndex()==0
					|| cmnode.getPart().getEchangeIndex()==2
					|| cmnode.getPart().getEchangeIndex()==3){
				it.remove();
			}
		}
		if(null == ebomlist || ebomlist.size()==0){
			return false;
		}else{
			CmCommonNodeUtil comnode = new CmCommonNodeUtil();
			isUpdate = updatePbomNodeVersion((CmTreeNode) pbomtree.getRoot(),ebomlist,comnode,false);
			return isUpdate;
		}
	}

	@SuppressWarnings("unchecked")
	public boolean updatePbomNodeVersion(CmTreeNode root,List<CmTreeNode> list,CmCommonNodeUtil comnode,boolean flag){
		if(!flag && null!=list && list.size()>0){
			Enumeration children=root.children();
			while(children.hasMoreElements()){
				CmTreeNode child=(CmTreeNode) children.nextElement();
				CmTreeNode same = comnode.getSameOccpathInList(child, list);
				if(null != same){
					flag = true;
					break;
				}
				flag = updatePbomNodeVersion(child,list,comnode,flag);
				if(CmCommonStringUtil.isPackage(child)){
					for(CmTreeNode brother:child.getListNode()){
						flag = updatePbomNodeVersion(brother,list,comnode,flag);
					}
				}
			}
		}
		return flag;
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		button = (JButton) evt.getSource();
		//System.out.println("---ebomlist--"+ebomlist);
		if(null == ebomlist || ebomlist.size()==0){
			JOptionPane.showMessageDialog(pbomtree.getRootPane(), "EBOM没有同步的信息或结构！");
		}else{
			CmCommonNodeUtil comnode = new CmCommonNodeUtil();
			updatePbomNodeVersion((CmTreeNode) pbomtree.getRoot(),ebomlist,comnode);
			isUpdate = false;
			pbomtree.updateUI();
		}
		button.setEnabled(false);
	}

	@SuppressWarnings("unchecked")
	public void updatePbomNodeVersion(CmTreeNode root,List<CmTreeNode> list,CmCommonNodeUtil comnode){
		if(null!=list && list.size()>0){
			Enumeration children=root.children();
			while(children.hasMoreElements()){
				CmTreeNode child=(CmTreeNode) children.nextElement();
				CmTreeNode same = comnode.getSameOccpathInList(child, list);
				if(null != same){
					updateNodeVersion(child,same);
				}
				updatePbomNodeVersion(child,list,comnode);
				if(CmCommonStringUtil.isPackage(child)){
					for(CmTreeNode brother:child.getListNode()){
						CmTreeNode brosame = comnode.getSameOccpathInList(brother, list);
						if(null != brosame){
							updateNodeVersion(brother,brosame);
						}
						updatePbomNodeVersion(brother,list,comnode);
					}
				}
			}
		}
	}

	public void updateNodeVersion(CmTreeNode pbomnode,CmTreeNode ebomnode){
		String mtype = pbomnode.getPart().getMtype();
		String state = pbomnode.getPart().getLifecycle();
		System.out.println("--------"+pbomnode.getPart().getPartNumber()+"  ---"+state+"---"+mtype);
		if(!"正在工作".equals(state)){
			return;
		}
		if(!"自制件".equals(mtype) && !"外配套件".equals(mtype) && !"带料委外件".equals(mtype) && !"不带料委外件".equals(mtype)) {
			return;
		}
		CmTreeNode rootPart = (CmTreeNode)pbomtree.getRoot().children().nextElement();
		if (rootPart.getPart().getContainerId() != pbomnode.getPart().getContainerId()) {//借用件不能修改
			System.out.println("--------"+pbomnode.getPart().getPartNumber()+" 是借用件，不能修改属性！");
			return;
		}

		updateVersion(pbomnode);

		pbomnode.getPart().setE_version(ebomnode.getPart().getE_version());
		pbomnode.getPart().setEu_number(ebomnode.getPart().getEu_number());
		pbomnode.getPart().setEu_version(ebomnode.getPart().getEu_version());
		pbomnode.getPart().setRemark(ebomnode.getPart().getRemark());//备注
		pbomnode.getPart().setCmat(ebomnode.getPart().getCmat());//材料
		pbomnode.getPart().setPtc_material_name(ebomnode.getPart().getPtc_material_name());//材料名称
		pbomnode.getPart().setCmat_up(ebomnode.getPart().getCmat_up());//材料上标
		pbomnode.getPart().setCmat_down(ebomnode.getPart().getCmat_down());//材料下标
		pbomnode.getPart().setSetmark(ebomnode.getPart().getSetmark());//成套件标识
		pbomnode.getPart().setAdjustable(ebomnode.getPart().getAdjustable());//可调整
		pbomnode.getPart().setPhase_code(ebomnode.getPart().getPhase_code());//当前阶段
		pbomnode.getPart().setKeycomponent(ebomnode.getPart().getKeycomponent());//关重件标识
		pbomnode.getPart().setCsize(ebomnode.getPart().getCsize());//规格
		pbomnode.getPart().setCtype(ebomnode.getPart().getCtype());//零部件分类
		pbomnode.getPart().setSecret(ebomnode.getPart().getSecret());//密级
		pbomnode.getPart().setEnditemin(ebomnode.getPart().getEnditemin());//所属成品
		pbomnode.getPart().setMindex(ebomnode.getPart().getMindex());//所属型号
		pbomnode.getPart().setCindex(ebomnode.getPart().getCindex());//图号
		pbomnode.getPart().setPtc_common_name(ebomnode.getPart().getPtc_common_name());//中文名称

		//设计资源库应用改造新增属性
		//start
		pbomnode.getPart().setShortname(ebomnode.getPart().getShortname());//物资简称
		pbomnode.getPart().setYxjb(ebomnode.getPart().getYxjb());//编码优选级别
		pbomnode.getPart().setBmzt(ebomnode.getPart().getBmzt());//编码状态
		pbomnode.getPart().setBmlx(ebomnode.getPart().getBmlx());//编码类型
		pbomnode.getPart().setStandardnumber(ebomnode.getPart().getStandardnumber());//标准号
		pbomnode.getPart().setMechanicalpropertyorhardness(ebomnode.getPart().getMechanicalpropertyorhardness());//机械性能等级或硬度
		pbomnode.getPart().setSurfacetreatment(ebomnode.getPart().getSurfacetreatment());//表面处理
		pbomnode.getPart().setHeattreatment(ebomnode.getPart().getHeattreatment());//热处理
		pbomnode.getPart().setProductform(ebomnode.getPart().getProductform());//产品型式
		pbomnode.getPart().setProductlevel(ebomnode.getPart().getProductlevel());//产品等级
		pbomnode.getPart().setPlatecscrewform(ebomnode.getPart().getPlatecscrewform());//板拧形式
		pbomnode.getPart().setIsimport(ebomnode.getPart().getIsimport());//是否进口
		pbomnode.getPart().setSpecialinstruction(ebomnode.getPart().getSpecialinstruction());//特殊说明
		pbomnode.getPart().setMeasureunit(ebomnode.getPart().getMeasureunit());//计量单位
		pbomnode.getPart().setType(ebomnode.getPart().getType());//型号
		pbomnode.getPart().setTypestandard(ebomnode.getPart().getTypestandard());//型号规格
		pbomnode.getPart().setQualitylevel(ebomnode.getPart().getQualitylevel());//质量等级
		pbomnode.getPart().setTotalstandard(ebomnode.getPart().getTotalstandard());//总规范
		pbomnode.getPart().setDetailstandard(ebomnode.getPart().getDetailstandard());//详细规范
		pbomnode.getPart().setPackagingform(ebomnode.getPart().getPackagingform());//封装形式
		pbomnode.getPart().setOutlinesize(ebomnode.getPart().getOutlinesize());//外形尺寸
		pbomnode.getPart().setSpecialcondition(ebomnode.getPart().getSpecialcondition());//专用条件
		pbomnode.getPart().setExtracondition(ebomnode.getPart().getExtracondition());//附加协议
		pbomnode.getPart().setMattype(ebomnode.getPart().getMattype());//材料类型

		pbomnode.getPart().setCmatnumber(ebomnode.getPart().getCmatnumber());//材料编号
		pbomnode.getPart().setMarknumber(ebomnode.getPart().getMarknumber());//牌号
		pbomnode.getPart().setSupplystate(ebomnode.getPart().getSupplystate());//供应状态
		pbomnode.getPart().setUsestandard(ebomnode.getPart().getUsestandard());//采用标准
		//end

		pbomnode.getPart().setEdit(true);
		pbomtree.updateUI();
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
//		if(getNodeUpdateVersionIndex(node)>0){
//			return;
//		}
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
	 * 对零件进行升版本
	 * 如果零件的状态是已归档----升大版本  A.4 → B.1
	 * 如果零件的状态不是已归档 --升小版本 A.4 → A.5
	 * @author chenyunlong
	 * @date  2013-5-24
	 * @param node
	 *
	 */
	public static void updateVersion2(CmTreeNode node,String phase){
//		if(getNodeUpdateVersionIndex(node)>0){
//			return;
//		}
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
		node.getPart().setPhase_code(phase);
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
//			if(CmCommonStringUtil.isHasFilingOfObj(node)){
//				index = 2;
//			}else{
				index = 1;
//			}
		}else if(node.getPart().isEdit()){
			index =1 ;
		}
		return index;
	}
}
