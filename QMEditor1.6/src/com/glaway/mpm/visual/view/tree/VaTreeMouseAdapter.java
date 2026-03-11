package com.glaway.mpm.visual.view.tree;

import com.glaway.mpm.qmIntf.fittingTool.view.FittingsDistributionFrame;
import com.glaway.mpm.visual.bean.VaLightPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.tree.menu.*;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

/**
 * <br>
 * Created on 2011-3-17
 *
 * @author Alex.Huang - ����
 */
public class VaTreeMouseAdapter extends JPopupMenu implements MouseListener {
	private static final long serialVersionUID = 2275535113893449843L;

	private static final VaLogger log = VaLogger
			.getLogger(VaTreeMouseAdapter.class.getName());

	private VaTree tree;
	private Window owner;
	private VaTreeNode currNode;
	private List<VaTreeNode> selectedNodes;

	private int selectTreeCount;

	public VaMenuItemFactory itemFactory;

	/**
	 * @param owner
	 *            ָ�Ҽ�˵����е������ owner
	 */
	public VaTreeMouseAdapter(Window owner, VaMenuItemFactory itemFactory) {
		super();
		this.owner = owner;
		this.itemFactory = itemFactory;
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

	public void addItems(JMenuItem[] itemArray) {
		if (itemArray == null)
			return;
		for (JMenuItem item : itemArray)
			this.add(item);
	}

	/**
	 *MouseListener�ӿڷ���
	 */
	public void mousePressed(MouseEvent e) {
//		log.debug("mousePressed");
//		this.tree = (VaTree) e.getSource();
//		int row = tree.getRowForLocation(e.getX(), e.getY());
//		TreePath currPath = tree.getPathForRow(row);
//		TreePath[] paths = tree.getSelectionPaths();
//		List<TreePath> pathList = new Vector<TreePath>();
//		if (paths != null)
//			for (TreePath path : paths) {
//				pathList.add(path);
//			}
//
//		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0
//				&& currPath != null) {
//			VaTreeNode node = (VaTreeNode) currPath.getLastPathComponent();
//			this.currNode = node;
//			if (!pathList.contains(currPath)) {
//				if (node != node.getRoot()) {
//					tree.setSelectionPath(currPath);
//				}
//			}
//			if (configureUI())
//				this.show(e.getComponent(), e.getX(), e.getY());
//		}
//		if(e.getButton()==1){
//			//放开代码
////			if(currPath!=null){
////				VaTreeNode node = (VaTreeNode) currPath.getLastPathComponent();
////				FittingsDistributionFrame.bomLable.setText(getNodeAttribute(node));
////			}
//			VaTreeNode node = (VaTreeNode) currPath.getLastPathComponent();
//			FittingsDistributionFrame.bomLable.setText(getNodeAttribute(node));
//		}

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
		setEnable(currNode.isUsed(),items);
		addItems(items);
		if (items.length > 0)
			return true;
		else
			return false;
	}

	public void setEnable(boolean flag,JMenuItem[] items){
		for (JMenuItem item : items){
			if(item instanceof VaEomZhuangCopyNodeMenuItem){
				if(currNode.getzCount() > 0){
					item.setEnabled(true);
				}else{
					item.setEnabled(false);
				}
			}else if(item instanceof VaEomChaiCopyNodeMenuItem){
				if(currNode.getcCount() > 0){
					item.setEnabled(true);
				}else{
					item.setEnabled(false);
				}
			}
//			if(item instanceof VaEomZhuangCopyNodeMenuItem || item instanceof VaEomChaiCopyNodeMenuItem){
//			item.setEnabled(flag);
//			}
			if(item instanceof VaEBomPackupNodeMenuItem){
				if(currNode.getPart() != null && currNode.getPart().getAmount() > 1){
					item.setEnabled(false);
				}else{
					item.setEnabled(true);
				}
			}
			if(item instanceof VaEBomUnpackNodeMenuItem){
				if(currNode.getPart() != null && currNode.getPart().getAmount() > 1){
					item.setEnabled(true);
				}else{
					item.setEnabled(false);
				}
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		 log.debug("1:mouseClick");
//		this.tree = (VaTree) e.getSource();
//		int row = tree.getRowForLocation(e.getX(), e.getY());
//		TreePath currPath = tree.getPathForRow(row);
//
//		if (currPath != null) {
//			VaTreeNode node = (VaTreeNode) currPath.getLastPathComponent();
//			if (node != node.getRoot()) {
////				tree.setSelectionPath(currPath);
//				/*Vector bboxs = node.getBboxes();
//				if (bboxs != null) {
//					for (Object obj : bboxs) {
//						BoundingBox bbox = (BoundingBox) obj;
//						Point3d lower = new Point3d();
//						Point3d upper = new Point3d();
//						bbox.getLower(lower);
//						bbox.getUpper(upper);
//
//						VaMainframe main = VaMainframe.getInstance();
//						main.setX1(String.valueOf(lower.getX()));
//						main.setX2(String.valueOf(upper.getX()));
//						main.setY1(String.valueOf(lower.getY()));
//						main.setY2(String.valueOf(upper.getY()));
//						main.setZ1(String.valueOf(lower.getZ()));
//						main.setZ2(String.valueOf(upper.getZ()));
//					}
//				}*/
//			}
//			else{
//				log.debug("clearSelection");
//				tree.setSelectionPath(null);
//			}
//		}


		log.debug("mousePressed");
		this.tree = (VaTree) e.getSource();
		int row = tree.getRowForLocation(e.getX(), e.getY());
		TreePath currPath = tree.getPathForRow(row);
		TreePath[] paths = tree.getSelectionPaths();
		List<TreePath> pathList = new Vector<TreePath>();
		if (paths != null)
			for (TreePath path : paths) {
				pathList.add(path);
			}

		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0
				&& currPath != null) {
			VaTreeNode node = (VaTreeNode) currPath.getLastPathComponent();
			this.currNode = node;
			if (!pathList.contains(currPath)) {
				if (node != node.getRoot()) {
					tree.setSelectionPath(currPath);
				}
			}
			if (configureUI())
				this.show(e.getComponent(), e.getX(), e.getY());
		}
		if(e.getButton()==1){
			//放开代码
//			if(currPath!=null){
//				VaTreeNode node = (VaTreeNode) currPath.getLastPathComponent();
//				FittingsDistributionFrame.bomLable.setText(getNodeAttribute(node));
//			}
			VaTreeNode node = (VaTreeNode) currPath.getLastPathComponent();
			FittingsDistributionFrame.bomLable.setText(getNodeAttribute(node));
		}
	}

	public void mouseEntered(MouseEvent e) {
		// log.debug("2");
	}

	public void mouseExited(MouseEvent e) {
		// log.debug("3");
	}

	public void mouseReleased(MouseEvent e) {
		// log.debug("4");
	}

	public String getNodeAttribute(VaTreeNode node){
		StringBuffer buffer = new StringBuffer();
		VaLightPart part = node.getPart();
		int i = tree.getSelectionCount();
		buffer.append("编号：" + part.getNumber() + ";  ");
		buffer.append("名称：" + part.getName() + ";  ");
		if (!"".equals(node.getFlag())&&!"null".equals(node.getFlag())&&node.getFlag()!=null&&("GYDENEWPART".equals(node.getFlag())||"GYDEMATCHPART".equals(node.getFlag())
			||"LJZYCLDENEWPART".equals(node.getFlag())||"ZPZYCLDENEWPART".equals(node.getFlag()))) {
		    if (!isEmpty(node.getDw2())) {
		        buffer.append("单位:" + node.getDw2()+ ";  ");
            }
		    if (!isEmpty(node.getDw())) {
		        buffer.append("主计量单位:" + node.getDw() + ";  ");
            }
		    if (!isEmpty(node.getBzh())) {
                buffer.append("标准号:" + node.getBzh() + ";  ");
            }
            if (!isEmpty(node.getJstj())) {
                buffer.append("技术条件:" + node.getJstj() + ";  ");
            }
		    if (!isEmpty(node.getXhph())) {
		        buffer.append("型号牌号:" + node.getXhph() + ";  ");
            }
		    if (!isEmpty(node.getGg())) {
                buffer.append("规格:" + node.getGg() + ";  ");
            }
		    if (!isEmpty(node.getSccj())) {
                buffer.append("生产厂家:" + node.getSccj() + ";  ");
            }
		    if (!isEmpty(node.getZldj())) {
                buffer.append("质量等级:" + node.getZldj() + ";  ");
            }
		    if (!isEmpty(node.getFzxs())) {
                buffer.append("封装形式:" + node.getFzxs() + ";  ");
            }
		    if (!isEmpty(node.getDcstxyq())) {
                buffer.append("电参数特选要求:" + node.getDcstxyq() + ";  ");
            }
		    if (!isEmpty(node.getComment())) {
                buffer.append("备注:" + node.getComment() + ";  ");
            }
		    buffer.append("\r\n");
            buffer.append("当前选择数量: " + i);
        }else if (!"".equals(node.getFlag())&&!"null".equals(node.getFlag())&&node.getFlag()!=null&&("SJZYKGYDENEWPART".equals(node.getFlag())||"SJZYKGYDEMATCHPART".equals(node.getFlag()))) {
            if (!isEmpty(node.getDataType())) {
                buffer.append("零组件生产类型:" + node.getDataType()+ ";  ");
            }
            if (!isEmpty(node.getBzh())) {
                buffer.append("标准号:" + node.getBzh() + ";  ");
            }
            if (!isEmpty(node.getJstj())) {
                buffer.append("技术条件:" + node.getJstj() + ";  ");
            }
            if (!isEmpty(node.getJxxndjhyd())) {
                buffer.append("机械性能等级:" + node.getJxxndjhyd() + ";  ");
            }
            if (!isEmpty(node.getBmcl())) {
                buffer.append("表面处理:" + node.getBmcl() + ";  ");
            }
            if (!isEmpty(node.getRcl())) {
                buffer.append("热处理:" + node.getRcl() + ";  ");
            }
            if (!isEmpty(node.getCpxs())) {
                buffer.append("产品形式:" + node.getCpxs() + ";  ");
            }
            if (!isEmpty(node.getCpdj())) {
                buffer.append("产品等级:" + node.getCpdj() + ";  ");
            }
            if (!isEmpty(node.getBnxs())) {
                buffer.append("扳令形式:" + node.getBnxs() + ";  ");
            }
            if (!isEmpty(node.getSfjk())) {
                buffer.append("是否进口:" + node.getSfjk() + ";  ");
            }

            if (!isEmpty(node.getComment())) {
                buffer.append("备注:" + node.getComment() + ";  ");
            }
            buffer.append("\r\n");
            buffer.append("当前选择数量: " + i);
        }else{
		    if (!isEmpty(part.getVersion())) {
			    buffer.append("版本：" + part.getE_version() + ";  ");
		    }
			if (!isEmpty(part.getEu_number())) {
				buffer.append("图号：" + part.getEu_number() + ";  ");
			}
			if (part.isKey()){
				buffer.append("关键件："+ "是;  ");
			}else{
				buffer.append("关键件："+ "否;  ");
			}
			if (!isEmpty(part.getDutu())) {
				buffer.append("镀涂：" + part.getDutu() + ";  ");
			}
			if (!isEmpty(part.getRemark())) {
				buffer.append("备注：" + part.getRemark() + ";  ");
			}
			if (!isEmpty(String.valueOf(part.getUseCount()))) {
				buffer.append("关重件标识：" + String.valueOf(part.getUseCount()) + ";  ");
			}
			if (!isEmpty(String.valueOf(part.getProductionQuantity()))) {
				buffer.append("当前阶段：" + String.valueOf(part.getProductionQuantity()) + ";  ");
			}
			if (!isEmpty(part.getProductionRatio())) {
				buffer.append("设计单位：" + part.getProductionRatio() + ";  ");
			}
			if (!isEmpty(part.getRate())) {
				buffer.append("设计者：" + part.getRate() + ";  ");
			}
			if (!isEmpty(part.getMaterialNumber())) {
					buffer.append("材料编号：" + part.getMaterialNumber() + ";  ");
			}
			if (!isEmpty(part.getMaterialName())) {
				buffer.append("材料名称：" + part.getMaterialName() + ";  ");
		    }
			if (!isEmpty(part.getMaterialBrand())) {
					buffer.append("材料牌号：" + part.getMaterialBrand() + ";  ");
			}
			if (!isEmpty(part.getBackupRate())) {
				buffer.append("工艺备份比例：" + part.getBackupRate() + ";  ");
		    }
			if (!isEmpty(part.getMaxBackupCount())) {
				buffer.append("最大备份数：" + part.getMaxBackupCount() + ";  ");
		    }
			if (!isEmpty(part.getBackupReason())) {
				buffer.append("备份原因：" + part.getBackupReason() + ";  ");
		    }
			if (!isEmpty(part.getWorkShop())) {
				buffer.append("制造单位：" + part.getWorkShop() + ";  ");
		    }
			if (!isEmpty(part.getOutsourcingUnits())) {
				buffer.append("建议外协单位：" + part.getOutsourcingUnits() + ";  ");
		    }
			if (!isEmpty(part.getMaterialCrision())) {
				buffer.append("材料标准号：" + part.getMaterialCrision() + ";  ");
		    }
			if (!isEmpty(part.getResponser())) {
				buffer.append("工艺负责人：" + part.getResponser() + ";  ");
		    }
			if (!isEmpty(part.getResponserGroup())) {
				buffer.append("工艺负责组：" + part.getResponserGroup() + ";  ");
		    }
		//	if (!isEmpty(String.valueOf(part.getContainerId()))) {
		//		buffer.append("牌号：" + String.valueOf(part.getContainerId()) + ";  ");
		  //  }
			if (!isEmpty(part.getEu_version())) {
				buffer.append("零件的EBOM图号版本：" + part.getEu_version() + ";  ");
		    }
			if (part.isChange()){
				buffer.append("工艺更改："+ "是;  ");
			}else{
				buffer.append("工艺更改："+ "否;  ");
			}
			if (!isEmpty(part.getMiddleIndex())) {
				buffer.append("工艺中间件编号：" + part.getMiddleIndex() + ";  ");
		    }
			if (!isEmpty(part.getMtype())) {
				buffer.append("零组件生产类型：" + part.getMtype() + ";  ");
		    }
			if (!isEmpty(part.getCsize())) {
				buffer.append("规格：" + part.getCsize() + ";  ");
		    }
			if (!isEmpty(part.getXhph())) {
				buffer.append("型号牌号：" + part.getXhph() + ";  ");
		    }
			buffer.append("\r\n");
            buffer.append("当前选择数量: " + i);
        }
			return buffer.toString();
	}


	public static boolean isEmpty(String obj) {
		if (null == obj || "".equals(obj.trim())) {
			return true;
		} else {
			return false;
		}
	}
}
