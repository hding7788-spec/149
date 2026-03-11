package com.glaway.mpm.erp.component;

import com.glaway.mpm.erp.ErpToWCIntf;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

/**
 * 选择物资分类
 */
public class SetWZFLDialog extends JDialog implements ActionListener {
	private JDialog dialog;
  private  JPanel panel;
  private JPanel topPanel= new JPanel();
  private JPanel bottomPanel= new JPanel();
  private JScrollPane jScrollPane= new JScrollPane();
  private JComboBox box12=new JComboBox();
  private  JButton ok= new JButton("确定");
  private  JButton cancel= new JButton("取消");
  public  static boolean flag=false;
  public static String content=null;
  public static String nodeName;
  public JTree tree;
	public SetWZFLDialog(NewTechnicsPart frame, String nodeName) throws Exception {
		super(frame, "选择物资分类", true);
		this.nodeName=nodeName;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		panel = new JPanel();
		add(topPanel,BorderLayout.CENTER);
		add(bottomPanel,BorderLayout.SOUTH);
		init();
		this.dialog=this;

	}

	/**
	 * 获取树结构
	 * @return
	 */
	private DefaultMutableTreeNode getTreeNode(Map<String, List<String>> map,DefaultMutableTreeNode top){
		String value = top.getUserObject().toString();
		List<String> list = map.get(value);
		if(list!=null){
			for (int i = 0; i <list.size() ; i++) {
				String s = list.get(i);
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(s);
				top.add(node);
				getTreeNode(map,node);
			}
		}
		return top;
	}
	private void init() {
		Map<String, List<String>> wzflInfo = ErpToWCIntf.getWZFLInfo(nodeName);
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(nodeName);
		getTreeNode(wzflInfo,top);
		 tree = new JTree(top);
		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
		int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
		JScrollPane jsp = new JScrollPane(tree,v,h);
		topPanel.setLayout(new BorderLayout());
		topPanel.add(jsp,BorderLayout.CENTER);
		bottomPanel.add(ok, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 15, 15), 0, 0));
		bottomPanel.add(cancel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 15, 15), 0, 0));


		ok.addActionListener(this);
		cancel.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ok)// 确定
		{
			DefaultMutableTreeNode  lastSelectedPathComponent = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			content= lastSelectedPathComponent.toString();
			dispose();
		}

		if(e.getSource() == cancel){
		dispose();
		}
	}
	public String showDialog() {
	setSize(600, 800);
	SwingUtil.setMiddle(this);
	setResizable(false);
	setVisible(true);
	return content;
}

}
