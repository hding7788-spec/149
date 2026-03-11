package com.glaway.mpm.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.dom4j.Element;

import wt.fc.ObjectIdentifier;
import wt.part.WTPart;

import com.glaway.mpm.qmIntf.decoratePView.showPanel.view.OpenAssemblyInPViewPanel;
import com.glaway.mpm.qmIntf.decoratePView.treePanel.DpTreePanel;
import com.glaway.mpm.visual.conf.VaConstants;
import com.glaway.mpm.visual.control.VaPartStructureUtil;

public class TechnicsPacePopupMenu extends JPopupMenu implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JMenuItem runAssembleCartoon = new JMenuItem("装配动画工具");
	private NewTechnicsPart frame;
	private TechnicsTreePanel treePanel;
	private List<ObjectIdentifier> treeNodeOidKeyList = new ArrayList<ObjectIdentifier>();
	private List<Long> childOidList = new ArrayList<Long>();
	private Map<Long,String> numberMap = new HashMap<Long,String>();

	public TechnicsPacePopupMenu(NewTechnicsPart frame,
			TechnicsTreePanel treePanel) {
		super();
		this.frame = frame;
		this.treePanel = treePanel;

		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);

		this.add(runAssembleCartoon);
		runAssembleCartoon.addActionListener(this);
		initMenuItemIcon();
	}

	// 设置菜单项图标
	private void initMenuItemIcon() {
		Icon icon = new ImageIcon(DpTreePanel.class.getResource("/image/productView.gif"));
		runAssembleCartoon.setIcon(icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {

		if (event.getSource() == runAssembleCartoon)// 启动3D
		{
			try {
				//frame.runAssembleCartoonProgram();
				JTree tree = treePanel.getTree();
				TreePath  path = tree.getSelectionPath();
				XWTreeNode paceNode = (XWTreeNode)path.getLastPathComponent();
				Element  element=(( XWTreeNode)paceNode.getParent().getParent()).getObject().getTreeCellData();
//				System.out.println("=======element========"+element.asXML());
//				partNumber="800199000022161" partName="电感器" parentPartNumber="XX-00"
				String parentNumber = element.attributeValue("partNumber");
				String parentOid = element.attributeValue("partOid");
				System.out.println("parentNumber================"+ parentNumber);
				System.out.println("parentOid================"+ parentOid);
				//treeNodeOidKeyList.add(new ObjectIdentifier(WTPart.class, Long.valueOf(parentOid).longValue()));
				System.out.println("topPartNumber=============="+ element.attributeValue("parentPartNumber"));
				Enumeration resourseChildren = paceNode.children();
				while(resourseChildren.hasMoreElements()){
					XWTreeNode resourseNode  = (XWTreeNode)resourseChildren.nextElement();
					System.out.println("=======resourseNode========"+resourseNode);
					Enumeration typeChildren = resourseNode.children();
					while(typeChildren.hasMoreElements()){
						XWTreeNode typeNode =(XWTreeNode)	typeChildren.nextElement();
						XWTreeObject  treeObject = typeNode.getObject();
						System.out.println("=======treeObject========"+treeObject);
						if(treeObject instanceof PartMessageTreeObject){
							PartMessageTreeObject partMessageTreeObject = (PartMessageTreeObject)treeObject;
//							System.out.println("=======xml========"+partMessageTreeObject.getTreeCellData().asXML());
							String oid = partMessageTreeObject.getTreeCellData().attributeValue("oid");
							treeNodeOidKeyList.add(new ObjectIdentifier(WTPart.class, Long.valueOf(oid).longValue()));
							System.out.println("=======oid========"+oid);
							childOidList.add(Long.valueOf(oid));
							String partIdentify = partMessageTreeObject.getPartIdentify();
							System.out.println("=======partIdentify========"+partIdentify);
							String childNumber = partIdentify.substring(0, partIdentify.indexOf("("));
							System.out.println("=======childNumber========"+childNumber);
							numberMap.put(Long.valueOf(oid), childNumber);
						}
					}
				}
				if(childOidList.isEmpty()){
					JOptionPane.showMessageDialog(frame, "该工步没有参装件！", "提示", JOptionPane.INFORMATION_MESSAGE);
				} else {
					Map<Long,List<Long>> map = new HashMap<Long,List<Long>>();
					map.put(Long.valueOf(parentOid), childOidList);
					HashMap<Long, URL> urlMap = VaPartStructureUtil.getPViewURLHashMap(treeNodeOidKeyList, VaConstants.PVIEW_OL);
					System.out.println("=======urlMap========"+urlMap);
					OpenAssemblyInPViewPanel app = new OpenAssemblyInPViewPanel("Product View",urlMap, map,numberMap);
			        app.runPView();
				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "启动装配动画工具出现错误！", "提示", JOptionPane.INFORMATION_MESSAGE);
			}
		}

	}



}