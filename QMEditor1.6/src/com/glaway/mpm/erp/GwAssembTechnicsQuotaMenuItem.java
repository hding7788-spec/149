package com.glaway.mpm.erp;

import com.glaway.mpm.util.ValueCache;
import com.glaway.mpm.view.*;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Element;

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.*;

public class GwAssembTechnicsQuotaMenuItem extends CmMenuItem {

	private static final long serialVersionUID = 1L;
	private NewTechnicsPart frame;
	private XWTreeNode node;
	String title = "";

	public GwAssembTechnicsQuotaMenuItem(NewTechnicsPart frame, XWTreeNode node) {
		this.setText("装配工艺定额");
		this.setIconStr("view2d.png");
		this.frame = frame;
		this.node = node;
		setEnabled(true);
	}

	private boolean displayValidate(XWTreeNode node) {
		if(node != null) {
			XWTreeObject treeObject = node.getObject();
			if(treeObject instanceof TechnicsMessageTreeObject) {
				TechnicsMessageTreeObject obj = (TechnicsMessageTreeObject)treeObject;
				Element element = obj.getTechnicsDocument().getRootElement();
				String state = element.attributeValue("lifecycle");
				if("正在工作".equals(state) || "修改中".equals(state)) {
					return true;
				}
				String techNumber = element.attributeValue("technicsNumber");
				String cldestate = "";
				try {
					cldestate = TechnicsIntf.getCLDEState(techNumber);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if("已批准".equals(cldestate)) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	protected void actionPerformed(ActionEvent evt) {
		node = frame.technicsTreePanel.getSelectedTreeNode();
		String techniceNumber = "";
		if (node != null) {
			XWTreeObject xo = node.getObject();
			if ((xo instanceof XWTechnicsTreeObject)) {
				Element element = xo.getTreeCellData();
				techniceNumber = element.attributeValue("technicsNumber");
			}
		}
		title = "【" + techniceNumber + "】" + "装配工艺定额";

		//判断是否已打开某一个定额窗口
		boolean isHasP = GwPartTechQuotaDialog.isHas();
		boolean isHasA = GwAssembTechQuotaDialog.isHas();
		if(isHasP || isHasA){
			return;
		}

		final VaActionProgressBar progressBar = new VaActionProgressBar(frame, "装配工艺定额", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
				Map<String, List<XWTreeNode>>  map=new HashMap<String, List<XWTreeNode>>();
				List<XWTreeNode> standradList=new ArrayList<XWTreeNode>();// 标准件
				List<XWTreeNode> componentList=new ArrayList<XWTreeNode>();// 元器件
				List<XWTreeNode> materialList=new ArrayList<XWTreeNode>();//金属材料
				List<XWTreeNode> nonMaterialList=new ArrayList<XWTreeNode>();//非金属材料
				List<XWTreeNode> compoundList=new ArrayList<XWTreeNode>();//复合材料
				List<XWTreeNode> jdclList=new ArrayList<XWTreeNode>();//机电材料
				List<XWTreeNode> hgpList=new ArrayList<XWTreeNode>();//火工品
				XWTreeNode selectedTreeNode = frame.xwPartTreePanel.getSelectedTreeNode();
				Enumeration<XWTreeNode> enu = selectedTreeNode.getP().children();
				while(enu.hasMoreElements()) {
					XWTreeNode partNode = enu.nextElement();
					XWTreeObject treeObj = partNode.getObject();
					if(treeObj instanceof XWPartTreeObject) {
						XWPartTreeObject partObj = (XWPartTreeObject)treeObj;
						Element partEle = partObj.getTreeCellData();
						String mtype = partEle.attributeValue("MTYPE");
						String number = partEle.attributeValue("partNumber");
						String ctype = partEle.attributeValue("CTYPE");
						ValueCache.allPartsType.put(number, mtype);
						if ("标准件".equals(mtype)||"标准件".equals(ctype)) {
							standradList.add(partNode);
						}else if("元器件".equals(mtype)||"元器件".equals(ctype)){
							componentList.add(partNode);
						}else if("金属材料".equals(mtype)||"金属材料".equals(ctype)){
							materialList.add(partNode);
						}else if("非金属材料".equals(mtype)||"非金属材料".equals(ctype)){
							nonMaterialList.add(partNode);
						}else if("复合材料".equals(mtype)||"复合材料".equals(ctype)){
							compoundList.add(partNode);
						}else if("机电材料".equals(mtype)||"机电材料".equals(ctype)){
							jdclList.add(partNode);
						}else if("火工品".equals(mtype)||"火工品".equals(ctype)){
							hgpList.add(partNode);
						}
					}
				}
				map.put("standradList",standradList);
				map.put("componentList",componentList);
				map.put("materialList",materialList);
				map.put("nonMaterialList",nonMaterialList);
				map.put("compoundList",compoundList);
				map.put("jdclList",jdclList);
				map.put("hgpList",hgpList);
				GwAssembTechQuotaDialog.getInstance(frame,title,node,map);
//				new GwAssembTechQuotaDialog(frame,title,node,map);
				progressBar.setHeaderMessage("数据加载完成！");
				progressBar.finish();
				progressBar.setVisible(false);
			}
		};
		thread.start();
		progressBar.setVisible(true);
	}
}
