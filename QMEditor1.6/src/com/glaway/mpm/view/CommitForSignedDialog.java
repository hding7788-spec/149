package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.tree.TreeNode;

import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Element;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.wcIntf.UserIntf;
import com.ptc.wvs.livecycle.assembler.Map;

import ext.casc.process.ProcessTaskItem;
/*
 * author:chenming
 * date:2015.10.31
 *
 * */
public class CommitForSignedDialog extends JDialog implements ActionListener {
	private JDialog dialog;
  private NewTechnicsPart frame;
  private  JPanel panel;
  private JPanel topPanel= new JPanel();
  private JPanel bottomPanel= new JPanel();
  private JLabel jLabel1 = new javax.swing.JLabel();
  private JComboBox box12=new JComboBox();
  private  JButton ok= new JButton("确定");
  private  JButton cancel= new JButton("取消");
  public  static boolean flag=false;
  public static String content=null;
  private HashMap map;
	public CommitForSignedDialog(NewTechnicsPart frame) throws Exception {
		super(frame, "选择流程自动完成工艺任务", true);
		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			setTitle("选择流程自动完成SOP任务");
		}
		this.frame=frame;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		setTitle("选择流程自动生成工艺任务");
		panel = new JPanel();
		add(topPanel,BorderLayout.CENTER);
		add(bottomPanel,BorderLayout.SOUTH);
		init();
		this.dialog=this;
//		setSize(500, 250);
//		SwingUtil.setMiddle(this);
//		setResizable(false);
//		setVisible(true);
		initValue();
	}


	private void initValue() throws Exception {
		 map = new HashMap();
		 String style="";
		List<ProcessTaskItem> list = null;
		XWTreeNode node = frame.xwPartTreePanel.getSelectedTreeNode();
		XWTreeNode technicsTreenode = frame.technicsTreePanel.getSelectedTreeNode();
		String technicsTreePartNumber = null;
		String technicsNumber = "";
		if (node.getObject() instanceof TechnicsMessageTreeObject ) {
			if (technicsTreenode != null) {
				XWTreeObject xo = technicsTreenode.getObject();
				if ((xo instanceof XWTechnicsTreeObject)) {
					Element element = xo.getTreeCellData();
					technicsTreePartNumber = element.attributeValue("partNumber");
					technicsNumber = element.attributeValue("technicsNumber");
				}
			}
			XWTreeNode parent = (XWTreeNode) node.getParent();
			List<String> resultList = new ArrayList<String>();
			if (parent.getObject() instanceof XWPartTreeObject||parent.getObject() instanceof XWProductTreeObject) {
				Element data = parent.getObject().getTreeCellData();
				String partNumber=data.attributeValue("partNumber");
				if(technicsTreePartNumber!=null&&!"".equals(technicsTreePartNumber)&&!technicsTreePartNumber.equals(partNumber)){
					partNumber = technicsTreePartNumber;
				}

				// String users = UserIntf.getCurrentUserInfo().get(0);
				String users=NewTechnicsPart.currentUser;
                style="common";
				if (partNumber!=""&&partNumber!=null) {
//					 list = getProcessTaskItemByPartNumber(partNumber);
					resultList= (List)IntfUtil.getPeRemoteMethodInvoke("getProcessTaskItemByPartNumber2",
								new Class[] { String.class, String.class,String.class,String.class }, new Object[] {technicsNumber,partNumber,users,style});
				}
//				String defaultValue = TechnicsIntf.getDefaultTaskItem(technicsNumber,partNumber,users);
			}
			if(resultList!=null){

				for (int i = 0; i < resultList.size(); i++) {
					String result = resultList.get(i);
					String[] resultstr = result.split("@@");
					map.put(resultstr[0], resultstr[1]);
					box12.addItem(resultstr[0]);
				}
			}
		}
		if (node.getObject() instanceof ReportTechnicsTreeObject ) {
			XWTreeNode parent = (XWTreeNode) node.getParent();
			if (parent.getObject() instanceof XWPartTreeObject||parent.getObject() instanceof XWProductTreeObject) {
				Element data = parent.getObject().getTreeCellData();
				String partNumber=data.attributeValue("partNumber");
				// String users = UserIntf.getCurrentUserInfo().get(0);
				String users=NewTechnicsPart.currentUser;
				style="report";
				if (partNumber!=""&&partNumber!=null) {
//					 list = getProcessTaskItemByPartNumber(partNumber);
					list= (List)IntfUtil.getPeRemoteMethodInvoke("getProcessTaskItemByPartNumber",
								new Class[] { String.class,String.class,String.class }, new Object[] {partNumber,users,style});
				}
			}
			if(list!=null){

				for (int i = 0; i < list.size(); i++) {
					String name=list.get(i).getNumber()+"  "+list.get(i).getTaskItemName();
					//String oid=Long.toString(list.get(i).getProcessTaskId());
					ReferenceFactory refefence = new ReferenceFactory();
					String workItemOid = refefence.getReferenceString(list.get(i));
					map.put(name, workItemOid);
					box12.addItem(name);
				}
			}
		}



	}
	private void init() {
		if("SOP".equals(com.glaway.mpm.EditorConfig.startType)){
			jLabel1.setText("SOP编制任务");
		}else{
			jLabel1.setText("工艺任务");
		}

		topPanel.setLayout(new GridBagLayout());
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);

		// 第一行：标签
		jLabel1.setPreferredSize(new Dimension(80, 23));
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 0.0; // 标签不拉伸
		gridBagConstraints.fill = GridBagConstraints.NONE;
		topPanel.add(jLabel1, gridBagConstraints);

		// 第一行：下拉框 - 关键修改
		box12.setPreferredSize(new Dimension(650, 23));
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.weightx = 1.0; // 允许水平拉伸
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL; // 水平填充
		topPanel.add(box12, gridBagConstraints);

		// 底部按钮区域
		GridBagConstraints btnConstraints = new GridBagConstraints();
		bottomPanel.add(ok, btnConstraints);
		bottomPanel.add(cancel, btnConstraints);

		ok.addActionListener(this);
		cancel.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ok)// 确定
		{
			String key = (String) box12.getSelectedItem();
			 content = (String) map.get(key);
			 flag=true;
//			dialog.setVisible(false);
			dispose();
		}

		if(e.getSource() == cancel){
			flag=false;
			content =null;
		dispose();
		}
	}
	public String showDialog() {
	setSize(800, 400);
	SwingUtil.setMiddle(this);
//	setResizable(false);
	setVisible(true);
	return content;
}

}
