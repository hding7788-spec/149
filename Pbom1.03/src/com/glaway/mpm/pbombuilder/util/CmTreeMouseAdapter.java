package com.glaway.mpm.pbombuilder.util;

import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.data.CmMenuItemFactory;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.item.CmCancelMenuItem;
import com.glaway.mpm.pbombuilder.wcInterface.ErpToWCIntf;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.*;

/**
 * <br>
 * 鼠标在树上的点击事件
 * Created on 2012-10-27
 *
 * @author chenyunlong
 */
public class CmTreeMouseAdapter extends JPopupMenu implements MouseListener {
	private static final long serialVersionUID = 2275535113893449843L;

	private static final CmLogger log = CmLogger.getLogger(CmTreeMouseAdapter.class.getName());

	private CmTree tree;
	private Window owner;
	private CmTreeNode currNode;
	public CmMenuItemFactory itemFactory;
	private Map<String, String> ibaKeyNameMap = new HashMap<String, String>();// 需要增加显示的软属性map
	private List<String> ibaKeyList = new ArrayList<String>();

	/**
	 * @param owner
	 *            指右键菜单项中弹出框的 owner
	 */
	public CmTreeMouseAdapter(Window owner, CmMenuItemFactory itemFactory) {
		super();
		this.owner = owner;
		this.itemFactory = itemFactory;
		addIbaNameList();
		initUI();
	}

	protected void initUI() {
		initActions();
		initComponents();
	}

	protected void initActions() {

	}

	protected void initComponents() {

	}
	
	protected void addIbaNameList() {
		ibaKeyNameMap.put("BMDJ", "编码等级");
		ibaKeyNameMap.put("KFZBTID", "抗辐指标TID");
		ibaKeyNameMap.put("KFZBSEE", "抗辐指标SEE");
		ibaKeyNameMap.put("XNCS", "性能参数");
		ibaKeyNameMap.put("JDMGDJ_STATE", "是否静电敏感");
		ibaKeyNameMap.put("JDMGDJ", "经典敏感等级");
		ibaKeyNameMap.put("SMDJ", "湿敏等级");

		for (String key : ibaKeyNameMap.keySet()) {
			ibaKeyList.add(key);
		}
	}

	public void addItems(JMenuItem[] itemArray) {
		if (itemArray == null)
			return;
		for (JMenuItem item : itemArray)
			this.add(item);
	}

	/**
	 *MouseListener接口方法
	 */
	public void mousePressed(MouseEvent e) {
		this.setVisible(false);
		this.show(false);
		new CmCancelMenuItem();
		this.tree = (CmTree) e.getSource();

		//cancelCreoviewShow(CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree().getRoot());
		//cancelCreoviewShow(CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree().getRoot());
		int row = tree.getRowForLocation(e.getX(), e.getY());
		TreePath currPath = tree.getPathForRow(row);
		if (null != currPath) {
			CmTreeNode currnode = (CmTreeNode) currPath.getLastPathComponent();
			currnode.setCreoClick(true);
			currnode.setIspackage(true);
			CmMBomMainFrame.pbomLable.setText(changeNodeAttribute(currnode));
		}
		TreePath[] paths = tree.getSelectionPaths();
		List<TreePath> pathList = new Vector<TreePath>();
		int num  = 0;
		if (paths != null)
			for (TreePath path : paths) {
				num++;
				pathList.add(path);
				if(num>999){
					break;
				}
			}

		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0 && currPath != null) {
			CmTreeNode node = (CmTreeNode) currPath.getLastPathComponent();
			this.currNode = node;
			if (!pathList.contains(currPath)) {
				if (node != node.getRoot()) {
					tree.setSelectionPath(currPath);
				}
			}
			if (configureUI())
				this.show(true);
				this.show(e.getComponent(), e.getX(), e.getY());
		}

	}

	private void initMenu() {
		for (Component comp : this.getComponents()) {
			this.remove(comp);
		}
	}

	private boolean configureUI() {
		initMenu();
		if (itemFactory == null)
			return false;
		JMenuItem[] items = itemFactory.createMenuItem(tree, currNode, owner);
		//JMenu[] menus = itemFactory.createMenu(tree, currNode, owner);
		addItems(items);
//		if(menus!=null){
//			for(int i=0;i<menus.length;i++){
//				this.add(menus[i]);
//			}
//		}
		if (items.length > 0)
			return true;
		else
			return false;
	}

	public void mouseClicked(MouseEvent e) {/* MouseListener接口方法 */
	}

	public void mouseEntered(MouseEvent e) {/* MouseListener接口方法 */
	}

	public void mouseExited(MouseEvent e) {/* MouseListener接口方法 */
	}

	public void mouseReleased(MouseEvent e) {/* MouseListener接口方法 */
	}

	public String changeNodeAttribute(CmTreeNode node) {
		if(null==node.getParent()){
			return "EBOM:\r\nPBOM:";
		}

		String rootName=CmCommonStringUtil.getRootNode(node).toString();
		if("EBOM".equals(rootName)){
			String ebom=getNodeAttribute(node,"EBOM");
			CmTreeNode linkNode=getLinkNode(node.getOccId(),rootName);
			String pbom=null;
			if(null==linkNode){
				pbom="PBOM: ";
			}
			else{
				pbom=getNodeAttribute(linkNode,"PBOM");
			}
			return ebom+pbom;
		} else{
			String ebom=null;
			String pbom=null;
			CmTreeNode linkNode=getLinkNode(node.getOccId(),rootName);
			if(null==linkNode){
				ebom="EBOM: \r\n";
				pbom=getNodeAttribute(node,"PBOM");
			}
			else{
				ebom=getNodeAttribute(linkNode,"EBOM");
				pbom=getNodeAttribute(node,"PBOM");
			}
			return ebom+pbom;
		}
	}

	public String getNodeAttribute(CmTreeNode node,String bomName){
		StringBuffer buffer = new StringBuffer();
		CmLightPart part = node.getPart();
		buffer.append( bomName+ ":");
		buffer.append("编号：" + part.getPartNumber() + ";  ");
		buffer.append("名称：" + part.getPartName() + ";  ");
		if (!CmCommonStringUtil.isEmpty(part.getVersion())) {
			buffer.append("版本：" + CmCommonStringUtil.emptyToString(part.getVersion()) + ";  ");
		}
		if("PBOM".equals(bomName)){
			if(CmCommonStringUtil.isPackage(node)) {
				buffer.append("数量：" + (node.getListNode().size()+1) + ";  ");
			} else {
				buffer.append("数量：" + "1;  ");
			}
			if(CmCommonStringUtil.isPackage(node)) {
				if (!CmCommonStringUtil.isEmpty(part.getGysl())) {
					buffer.append("工艺数量：" + part.getGysl() + ";  ");
				} else {
					buffer.append("工艺数量：" + (node.getListNode().size()+1) + ";  ");
				}
			} else {
				if (!CmCommonStringUtil.isEmpty(part.getGysl())) {
					buffer.append("工艺数量：" + part.getGysl() + ";  ");
				} else {
					buffer.append("工艺数量：" + "1;  ");
				}
			}
			if (!CmCommonStringUtil.isEmpty(part.getCindex())) {
				buffer.append("图号：" + part.getCindex() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getPhase_code())) {
				buffer.append("当前阶段：" + part.getPhase_code() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getZzcj())) {
				buffer.append("主制车间：" + part.getZzcj() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getFzcj())) {
				buffer.append("辅制车间：" + part.getFzcj() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getMtype())) {
				buffer.append("零组件生产类型：" + part.getMtype() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getKeycomponent())) {
				buffer.append("关重件标识：" + part.getKeycomponent() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getBatch())) {
				buffer.append("批次号：" + part.getBatch() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getMattype())) {
				buffer.append("材料类型：" + part.getMattype() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getSetmark())) {
				buffer.append("成套件标识：" + part.getSetmark() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getAdjustable())) {
				buffer.append("可调整：" + part.getAdjustable() + ";  ");
			}
			Wzk wzk = null;
			if(!CmCommonStringUtil.isEmpty(part.getWzk().getInvcode())){
				buffer.append("存货编号：" + part.getWzk().getInvcode() + ";  ");

				wzk = ErpToWCIntf.getWzkByInvcode("02", part.getWzk().getInvcode());
				if(wzk!=null){
					buffer.append("存货名称：" + wzk.getInvname() + ";  ");
					if(!CmCommonStringUtil.isEmpty(part.getWzk().getClcode())){
						buffer.append("材料编号：" + part.getWzk().getClcode() + ";  ");
						//wzk = part.getWzk();
					}
					if(!CmCommonStringUtil.isEmpty(part.getWzk().getClName())){
						buffer.append("材料名称：" + part.getWzk().getClName() + ";  ");
					}
					buffer.append("型号牌号：" + CmCommonStringUtil.emptyToString(wzk.getInvtype()) + ";  ");
					buffer.append("规格：" + CmCommonStringUtil.emptyToString(wzk.getInvspec() )+ ";  ");
					buffer.append("技术条件：" + CmCommonStringUtil.emptyToString(wzk.getJsgfbz()) + ";  ");
					buffer.append("生产厂家：" + CmCommonStringUtil.emptyToString(wzk.getCustname()) + ";  ");
					buffer.append("主计量单位：" + CmCommonStringUtil.emptyToString(wzk.getMeasname()) + ";  ");
					buffer.append("附加条件：" + CmCommonStringUtil.emptyToString(wzk.getFjtjname()) + ";  ");
					buffer.append("螺纹规格/公称尺寸：" + CmCommonStringUtil.emptyToString(wzk.getDef12()) + ";  ");
					buffer.append("机械性能等级：" + CmCommonStringUtil.emptyToString(wzk.getDef14()) + ";  ");
					buffer.append("质量等级：" + CmCommonStringUtil.emptyToString(wzk.getZldj()) + ";  ");
					buffer.append("封装形式：" + CmCommonStringUtil.emptyToString(wzk.getDef6()) + ";  ");
					buffer.append("精度等级：" + CmCommonStringUtil.emptyToString(wzk.getDef8()) + ";  ");
				}
			}

			if (!CmCommonStringUtil.isEmpty(part.getCtype())) {
				//标准件
				if("标准件".equals(part.getCtype())) {
					buffer.append("物资简称：" + CmCommonStringUtil.emptyToString(part.getShortname()) + ";  ");
					buffer.append("标准号：" + CmCommonStringUtil.emptyToString(part.getStandardnumber())+ ";  ");
					buffer.append("规格：" + CmCommonStringUtil.emptyToString(part.getCsize()) + ";  ");
					buffer.append("材料：" + CmCommonStringUtil.emptyToString(part.getCmat()) + ";  ");
					buffer.append("机械性能等级或硬度：" + CmCommonStringUtil.emptyToString(part.getMechanicalpropertyorhardness()) + ";  ");
					buffer.append("表面处理：" + CmCommonStringUtil.emptyToString(part.getSurfacetreatment()) + ";  ");
					buffer.append("热处理：" + CmCommonStringUtil.emptyToString(part.getHeattreatment()) + ";  ");
					buffer.append("产品型式：" + CmCommonStringUtil.emptyToString(part.getProductform()) + ";  ");
					buffer.append("产品等级：" + CmCommonStringUtil.emptyToString(part.getProductlevel()) + ";  ");
					buffer.append("板拧形式：" + CmCommonStringUtil.emptyToString(part.getPlatecscrewform()) + ";  ");
					buffer.append("是否进口：" + CmCommonStringUtil.emptyToString(part.getIsimport()) + ";  ");
					buffer.append("计量单位：" + CmCommonStringUtil.emptyToString(part.getMeasureunit()) + ";  ");
					buffer.append("特殊说明：" + CmCommonStringUtil.emptyToString(part.getSpecialinstruction()) + ";  ");
					
				}
				//元器件
				if("元器件".equals(part.getCtype())) {
					Map<String, String> pIbaValues = PBOMEditorToWCIntf.getIbaValues(part.getPartNumber(), "Manufacturing", ibaKeyList);
					buffer.append("物资简称：" + CmCommonStringUtil.emptyToString(part.getShortname()) + ";  ");
					buffer.append("型号：" + CmCommonStringUtil.emptyToString(part.getType())+ ";  ");
					buffer.append("型号规格：" + CmCommonStringUtil.emptyToString(part.getTypestandard()) + ";  ");
					buffer.append("质量等级：" + CmCommonStringUtil.emptyToString(part.getQualitylevel()) + ";  ");
					buffer.append("总规范：" + CmCommonStringUtil.emptyToString(part.getTotalstandard()) + ";  ");
					buffer.append("详细规范：" + CmCommonStringUtil.emptyToString(part.getDetailstandard()) + ";  ");
					buffer.append("封装形式：" + CmCommonStringUtil.emptyToString(part.getPackagingform()) + ";  ");
					buffer.append("外形尺寸：" + CmCommonStringUtil.emptyToString(part.getOutlinesize()) + ";  ");
					buffer.append("专用条件：" + CmCommonStringUtil.emptyToString(part.getSpecialcondition()) + ";  ");
					buffer.append("附加协议：" + CmCommonStringUtil.emptyToString(part.getExtracondition()) + ";  ");
					buffer.append("是否进口：" + CmCommonStringUtil.emptyToString(part.getIsimport()) + ";  ");
					buffer.append("计量单位：" + CmCommonStringUtil.emptyToString(part.getMeasureunit()) + ";  ");
					buffer.append("特殊说明：" + CmCommonStringUtil.emptyToString(part.getSpecialinstruction()) + ";  ");
					
					for (String key : pIbaValues.keySet()) {
						buffer.append(ibaKeyNameMap.get(key) + "：" + pIbaValues.get(key) + ";  ");
					}
				}
				//自制件
				if("自制件".equals(part.getCtype())) {
					buffer.append("材料编号：" + CmCommonStringUtil.emptyToString(part.getCmatnumber()) + ";  ");
					buffer.append("牌号：" + CmCommonStringUtil.emptyToString(part.getMarknumber())+ ";  ");
					buffer.append("供应状态：" + CmCommonStringUtil.emptyToString(part.getSupplystate()) + ";  ");
					buffer.append("采用标准：" + CmCommonStringUtil.emptyToString(part.getUsestandard()) + ";  ");
				}
			}

			buffer.append("\r\n");
			buffer.append("PBOM状态：" + part.getLifecycle());
		}
		if("EBOM".equals(bomName)){
			if (!CmCommonStringUtil.isEmpty(part.getCindex())) {
				buffer.append("图号：" + part.getCindex() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getPindex())) {
				buffer.append("产品代号：" + part.getPindex() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getMindex())) {
				buffer.append("所属型号：" + part.getMindex() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getSecret())) {
				buffer.append("密级：" + part.getSecret() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getSetmark())) {
				buffer.append("成套件标识：" + part.getSetmark() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getCtype())) {
				buffer.append("零部件分类：" + part.getCtype() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getKeycomponent())) {
				buffer.append("关重件标识：" + part.getKeycomponent() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getPhase_code())) {
				buffer.append("当前阶段：" + part.getPhase_code() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getCompany())) {
				buffer.append("设计单位：" + part.getCompany() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getDesigner())) {
				buffer.append("设计者：" + part.getDesigner() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getPtc_material_name())) {
				buffer.append("材料名称：" + part.getPtc_material_name() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getCmat_up())) {
				buffer.append("材料上标：" + part.getCmat_up() + ";  ");
			}
			if (!CmCommonStringUtil.isEmpty(part.getCmat_down())) {
				buffer.append("材料下标：" + part.getCmat_down() + ";  ");
			}

			//自制件
			if("自制件".equals(part.getCtype())) {
				if (!CmCommonStringUtil.isEmpty(part.getCmatnumber())) {
					buffer.append("材料编号：" + part.getCmatnumber() + ";  ");
				}
				if (!CmCommonStringUtil.isEmpty(part.getMarknumber())) {
					buffer.append("牌号：" + part.getMarknumber() + ";  ");
				}
				if (!CmCommonStringUtil.isEmpty(part.getSupplystate())) {
					buffer.append("供应状态：" + part.getSupplystate() + ";  ");
				}
				if (!CmCommonStringUtil.isEmpty(part.getUsestandard())) {
					buffer.append("采用标准：" + part.getUsestandard() + ";  ");
				}
			}
			buffer.append("\r\n");
		}
		return buffer.toString();
	}

	public CmTreeNode getLinkNode(String occId,String partName){
		if("EBOM".equals(partName)){
			CmTree mtree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getMBomTree();
			return getLinkNodeFromTree(mtree.getRoot(),occId);
		}
		else{
			CmTree etree = CmMBomMainFrame.getMainFrame().getSplitRightClient().getEBomTree();
			return getLinkNodeFromTree(etree.getRoot(),occId);
		}
	}
	/**
	 * 取消图形的已选择的显示
	 * @date  2012-12-14
	 *
	 */
	@SuppressWarnings("unchecked")
	public void cancelCreoviewShow(CmTreeNode root){
			try {
				if(null!=root.get_pviewShapeInstance()){
					root.get_pviewShapeInstance().SetHighlight(false);
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
			Enumeration children=root.children();
			while(children.hasMoreElements()){
				CmTreeNode child=(CmTreeNode) children.nextElement();
				if(CmCommonStringUtil.isPackage(child)){
					for(CmTreeNode brother:child.getListNode()){
						cancelCreoviewShow(brother);
					}
				}
				cancelCreoviewShow(child);
			}
	}

	public String emptyToNumber(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return "0";
		} else {
			return String.valueOf(obj);
		}
	}
	@SuppressWarnings("unchecked")
	public CmTreeNode getLinkNodeFromTree(CmTreeNode root, String occId) {
		Enumeration children = root.children();
		while (children.hasMoreElements()) {
			CmTreeNode child = (CmTreeNode) children.nextElement();
			if (child.getOccId().equals(occId)) {
				return child;
			} else if (null != child.getListNode() && child.getListNode().size() > 0) {
				for (CmTreeNode brother : child.getListNode()) {
					if (brother.getOccId().equals(occId)) {
						return brother;
					}
				}
			}
			CmTreeNode node = getLinkNodeFromTree(child, occId);
			if (null != node) {
				return node;
			}
		}
		return null;
	}
}
