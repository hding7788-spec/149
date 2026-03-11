package com.glaway.mpm.consCheck;

import com.glaway.mpm.model.ConsCheckRecord;
import com.glaway.mpm.model.ConsCheckTree;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.qmIntf.template.TpNode;
import com.glaway.mpm.qmIntf.template.TpTree;
import com.glaway.mpm.qmIntf.template.TpTreeNode;
import com.glaway.mpm.util.SwingUtil;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;

public class ConsCheckRecordsMainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

//	private JPanel mainPanel;
	private JScrollPane jScrollPane;
	public static TpTree tpTree;
	public static JPopupMenu menu;
	public ConsCheckRecordsMainFrame frame;

	public ConsCheckRecordsMainPanel(ConsCheckRecordsMainFrame frame) {
		this.frame = frame;
		init();
	}

	private void init() {
		initComponents();
		initLayout();
	}

	private void initComponents() {
		jScrollPane = new JScrollPane();
		menu = new JPopupMenu();
		JMenuItem addTree = new JMenuItem("新建文件夹");
		JMenuItem modifyTree = new JMenuItem("修改文件夹");
		JMenuItem deleteTree = new JMenuItem("删除文件夹");
		JMenuItem addJCRecord = new JMenuItem("新建检测类项目配置");
		JMenuItem addJLRecord = new JMenuItem("新建记录类项目配置");
		JMenuItem addBGRecord = new JMenuItem("新建套表配置");
		JMenuItem modifyRecord = new JMenuItem("修改配置名称");
		JMenuItem deleteRecord = new JMenuItem("删除项目配置");
		menu.add(addTree);
		menu.add(modifyTree);
		menu.add(deleteTree);
		if(frame.getPRO().equals(frame.getTypeStr())){
			menu.add(addJCRecord);
			menu.add(addJLRecord);
		}else{
			menu.add(addBGRecord);
		}
		menu.add(modifyRecord);
		menu.add(deleteRecord);
		addTree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath treePath = tpTree.getSelectionPath();
				Object obj = treePath.getLastPathComponent();
				if(obj instanceof TpTreeNode){
					TpTreeNode treeNode = (TpTreeNode) obj;
					String gwkeyid = treeNode.getStepType();
					String fName = JOptionPane.showInputDialog(jScrollPane,"请输入文件夹名称");
					if(fName != null && !"".equals(fName)){
						if(fName.contains("_")){
							JOptionPane.showMessageDialog(null, "名称不允许含有下划线！", "提示", 1);
							return;
						}
						try {
							String newId = ProcessParameterToWCIntf.addConsCheckTree(gwkeyid,fName);
							if(!"".equals(newId)){
								TpTreeNode newNode = new TpTreeNode(fName, newId);
								treeNode.add(newNode);
							}
							tpTree.updateUI();
						} catch(InvocationTargetException ex) {
							throw new RuntimeException(ex);
						} catch(RemoteException ex) {
							throw new RuntimeException(ex);
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "请选择文件夹节点新增文件夹！", "提示", 1);
				}
			}
		});
		modifyTree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath treePath = tpTree.getSelectionPath();
				Object obj = treePath.getLastPathComponent();
				if(obj instanceof TpTreeNode){
					TpTreeNode treeNode = (TpTreeNode) obj;
					if("tpTree".equals(((TpTreeNode)treeNode.getParent()).getName())){
						JOptionPane.showMessageDialog(null, "不允许修改顶级文件夹！", "提示", 1);
						return;
					}
					String gwkeyid = treeNode.getStepType();
					String nodeName = treeNode.getName();
					String fName = JOptionPane.showInputDialog(jScrollPane,"请输入文件夹名称", nodeName);
					if(fName != null && !"".equals(fName) && !nodeName.equals(fName)) {
						if(fName.contains("_")){
							JOptionPane.showMessageDialog(null, "名称不允许含有下划线！", "提示", 1);
							return;
						}
						try {
							String result = ProcessParameterToWCIntf.modifyConsCheckTree(gwkeyid, fName);
							if("sucess".equals(result)) {
								treeNode.setName(fName);
							}
							tpTree.updateUI();
						} catch(InvocationTargetException ex) {
							throw new RuntimeException(ex);
						} catch(RemoteException ex) {
							throw new RuntimeException(ex);
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "请选择文件夹节点修改文件夹！", "提示", 1);
				}
			}
		});
		deleteTree.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath treePath = tpTree.getSelectionPath();
				Object obj = treePath.getLastPathComponent();
				if(obj instanceof TpTreeNode){
					TpTreeNode treeNode = (TpTreeNode) obj;
					if("tpTree".equals(((TpTreeNode)treeNode.getParent()).getName())){
						JOptionPane.showMessageDialog(null, "不允许删除顶级文件夹！", "提示", 1);
						return;
					}
					int isDelete = JOptionPane.showConfirmDialog(null, "确定要删除所选文件夹吗？", "确定", JOptionPane.YES_NO_OPTION);
					if(isDelete == JOptionPane.YES_NO_OPTION){
						String gwkeyid = treeNode.getStepType();
						if(gwkeyid != null && !"".equals(gwkeyid)){
							try {
								String result = ProcessParameterToWCIntf.deleteConsCheckTree(gwkeyid, frame.getTypeStr());
								if("sucess".equals(result)){
									((TpTreeNode) treeNode.getParent()).remove(treeNode);
								}
								tpTree.updateUI();
							} catch(InvocationTargetException ex) {
								throw new RuntimeException(ex);
							} catch(RemoteException ex) {
								throw new RuntimeException(ex);
							}
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "请选择文件夹节点删除文件夹！", "提示", 1);
				}
			}
		});
		addJCRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath treePath = tpTree.getSelectionPath();
				Object obj = treePath.getLastPathComponent();
				if(obj instanceof TpTreeNode){
					TpTreeNode treeNode = (TpTreeNode) obj;
					String gwkeyid = treeNode.getStepType();
					String fName = JOptionPane.showInputDialog(jScrollPane,"请输入配置名");
					if(fName != null && !"".equals(fName)) {
						if(fName.contains("_")){
							JOptionPane.showMessageDialog(null, "名称不允许含有下划线！", "提示", 1);
							return;
						}
						try {
							String newId = ProcessParameterToWCIntf.addConsCheckNode(gwkeyid, fName, "检测类", frame.getTypeStr());
							if(!"".equals(newId)) {
								TpNode newNode = new TpNode(newId, null, fName + "(检测类)");
								treeNode.add(newNode);
							}
							tpTree.updateUI();
						} catch(InvocationTargetException ex) {
							throw new RuntimeException(ex);
						} catch(RemoteException ex) {
							throw new RuntimeException(ex);
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "请选择文件夹节点新增配置！", "提示", 1);
				}
			}
		});
		addJLRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath treePath = tpTree.getSelectionPath();
				Object obj = treePath.getLastPathComponent();
				if(obj instanceof TpTreeNode){
					TpTreeNode treeNode = (TpTreeNode) obj;
					String gwkeyid = treeNode.getStepType();
					String fName = JOptionPane.showInputDialog(jScrollPane,"请输入配置名");
					if(fName != null && !"".equals(fName)) {
						if(fName.contains("_")){
							JOptionPane.showMessageDialog(null, "名称不允许含有下划线！", "提示", 1);
							return;
						}
						try {
							String newId = ProcessParameterToWCIntf.addConsCheckNode(gwkeyid, fName, "记录类", frame.getTypeStr());
							if(!"".equals(newId)) {
								TpNode newNode = new TpNode(newId, null, fName + "(记录类)");
								treeNode.add(newNode);
							}
							tpTree.updateUI();
						} catch(InvocationTargetException ex) {
							throw new RuntimeException(ex);
						} catch(RemoteException ex) {
							throw new RuntimeException(ex);
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "请选择文件夹节点新增配置！", "提示", 1);
				}
			}
		});
		addBGRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath treePath = tpTree.getSelectionPath();
				Object obj = treePath.getLastPathComponent();
				if(obj instanceof TpTreeNode){
					TpTreeNode treeNode = (TpTreeNode) obj;
					String gwkeyid = treeNode.getStepType();
					String fName = JOptionPane.showInputDialog(jScrollPane,"请输入配置名");
					if(fName != null && !"".equals(fName)) {
						if(fName.contains("_")){
							JOptionPane.showMessageDialog(null, "名称不允许含有下划线！", "提示", 1);
							return;
						}
						try {
							String newId = ProcessParameterToWCIntf.addConsCheckNode(gwkeyid, fName, null, frame.getTypeStr());
							if(!"".equals(newId)) {
								TpNode newNode = new TpNode(newId, null, fName);
								treeNode.add(newNode);
							}
							tpTree.updateUI();
						} catch(InvocationTargetException ex) {
							throw new RuntimeException(ex);
						} catch(RemoteException ex) {
							throw new RuntimeException(ex);
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "请选择文件夹节点新增配置！", "提示", 1);
				}
			}
		});
		modifyRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath treePath = tpTree.getSelectionPath();
				Object obj = treePath.getLastPathComponent();
				if(obj instanceof TpNode){
					TpNode tpNode = (TpNode) obj;
					String name = tpNode.getTemplate().getName();
					String type = "";
					if(frame.getPRO().equals(frame.getTypeStr())) {
						type = name.substring(name.length() - 5);
						name = name.substring(0, name.length() - 5);
					}
					String gwkeyid = tpNode.getTemplate().getOid();
					String fName = JOptionPane.showInputDialog(jScrollPane,"请输入配置名",name);
					if(fName != null && !"".equals(fName) && !name.equals(fName)) {
						if(fName.contains("_")){
							JOptionPane.showMessageDialog(null, "名称不允许含有下划线！", "提示", 1);
							return;
						}
						try {
							String result = ProcessParameterToWCIntf.modifyConsCheckNode(gwkeyid, fName, frame.getTypeStr());
							if("sucess".equals(result)) {
								if(frame.getPRO().equals(frame.getTypeStr())) {
									tpNode.getTemplate().setName(fName + type);
								} else {
									tpNode.getTemplate().setName(fName);
								}
							}
							tpTree.updateUI();
						} catch(InvocationTargetException ex) {
							throw new RuntimeException(ex);
						} catch(RemoteException ex) {
							throw new RuntimeException(ex);
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "请选择配置节点修改配置！", "提示", 1);
				}
			}
		});
		deleteRecord.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath treePath = tpTree.getSelectionPath();
				Object obj = treePath.getLastPathComponent();
				if(obj instanceof TpNode){
					int isDelete = JOptionPane.showConfirmDialog(null, "确定要删除所选配置吗？", "确定", JOptionPane.YES_NO_OPTION);
					if(isDelete == JOptionPane.YES_NO_OPTION){
						TpNode tpNode = (TpNode) obj;
						String gwkeyid = tpNode.getTemplate().getOid();
						if(gwkeyid != null && !"".equals(gwkeyid)){
							try {
								String result = ProcessParameterToWCIntf.deleteConsCheckNode(gwkeyid,frame.getTypeStr());
								if("sucess".equals(result)){
									((TpTreeNode)tpNode.getParent()).remove(tpNode);
								}
								tpTree.updateUI();
							} catch(InvocationTargetException ex) {
								throw new RuntimeException(ex);
							} catch(RemoteException ex) {
								throw new RuntimeException(ex);
							}
						}
					}
				}else{
					JOptionPane.showMessageDialog(null, "请选择配置节点删除项目配置！", "提示", 1);
				}
			}
		});
		tpTree = ToTpTree();
		tpTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int x = e.getX();
				int y = e.getY();
				if(e.getButton()==MouseEvent.BUTTON3){
					TreePath pathForLocation = tpTree.getPathForLocation(x, y);
					if(pathForLocation != null){
						Object path = pathForLocation.getLastPathComponent();
						if(path instanceof TpTreeNode || path instanceof TpNode){
							tpTree.setSelectionPath(pathForLocation);
							menu.show(tpTree, x, y);
						}
					}
				}
			}
		});
		tpTree.updateUI();
		SwingUtil.expandAll(tpTree);
		jScrollPane.setViewportView(tpTree);
	}

	private void initLayout() {
		int width = 770;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height/2-80;
		setSize(width, height);
		jScrollPane.getViewport().setBackground(Color.WHITE);
		jScrollPane.setPreferredSize(new Dimension(width, height));
//		mainPanel.setLayout(new BorderLayout(0, 1));
//		mainPanel.add(jScrollPane);
		this.setLayout(new BorderLayout());
		this.add(jScrollPane);
	}

	public TpTree ToTpTree() {
		TpTree tpTree = null;
		try {
			ConsCheckTree checkTree = ProcessParameterToWCIntf.packageConsCheckTree(frame.getTypeStr());
			TpTreeNode rootNode = new TpTreeNode("tpTree");
			TpTreeNode node = new TpTreeNode(checkTree.getName(),checkTree.getGekeyid());
			convertToTpTree(checkTree,node);
			rootNode.add(node);
			tpTree = new TpTree(rootNode);
		} catch(InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch(RemoteException e) {
			throw new RuntimeException(e);
		}
		return tpTree;
	}

	public void convertToTpTree(ConsCheckTree checkTree,TpTreeNode tpTreeNode){
		List<ConsCheckRecord> records = checkTree.getRecords();
		if(records != null && records.size()>0){
			for(ConsCheckRecord record : records) {
				tpTreeNode.add(new TpNode(record.getGwkeyid(), null,record.getName()));
			}
		}
		List<ConsCheckTree> trees = checkTree.getTrees();
		if(trees != null && trees.size()>0){
			for(ConsCheckTree tree : trees) {
				TpTreeNode treeNode = new TpTreeNode(tree.getName(),tree.getGekeyid());
				convertToTpTree(tree,treeNode);
				tpTreeNode.add(treeNode);
			}
		}
	}
}
