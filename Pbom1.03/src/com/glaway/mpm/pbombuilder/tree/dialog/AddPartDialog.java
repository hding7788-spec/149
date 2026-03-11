package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import wt.part.WTPart;

import com.glaway.mpm.pbom.table.KVItem;
import com.glaway.mpm.pbom.table.KVJComboBox;
import com.glaway.mpm.pbombuilder.action.CmCommonPackageAction;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.panel.CmMPartMaster;
import com.glaway.mpm.pbombuilder.tree.CmLightPart;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.tree.action.BomTreeReportAction;
import com.glaway.mpm.pbombuilder.tree.action.EbomTreeCancelAction;
import com.glaway.mpm.pbombuilder.tree.action.PbomTreeEditReportAction;
import com.glaway.mpm.pbombuilder.util.CmBizObjUtil;
import com.glaway.mpm.pbombuilder.util.CmCommonStringUtil;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.util.LoadConfig;
import com.glaway.mpm.pbombuilder.wcInterface.PBOMEditorToWCIntf;



public class AddPartDialog {

	private static final long serialVersionUID = 1L;

	private final String[] YES_OR_NO = new String[]{"是","否"};
	private Window owner;
	private CmTree tree;
	private CmTreeNode node;
	private String title;
	private WTPart parentPart;

	private JLabel partNumberLabel;
	private JTextField partNumberText;
	private JLabel partNameLabel;
	private JTextField partNameText;
	private JLabel userCountLabel;
	private JTextField userCountText;
	private JLabel isProductLable;
	private JComboBox isProductComboBox;
	private JLabel modelLabel;
	private KVJComboBox modelComboBox;
	private JLabel ctypeLable;
	private JComboBox ctypeComboBox;
	private JLabel viewLable;
	private JComboBox viewComboBox;
	private JLabel isVirtualLabel;//关重件标识
	private JComboBox isVirtualComboBox;
	private JLabel keyLabel;//关重件标识
	private JComboBox keyComboBox;

	private JLabel ctjbsLabel; //成套件标识
	private JComboBox ctjbsComboBox;

	private JLabel levelLabel;//节段
	private JComboBox levelComboBox;

	private JLabel collectPartLabel;//收集部件
	private JComboBox collectPartComboBox;

	private JLabel secretLabel;//密级
	private JComboBox secretComboBox;

	private JLabel zzdmLabel;
	private KVJComboBox zzdmComboBox;//默认追踪代码
	private JLabel unitLabel;
	private JComboBox unitComboBox;//单位


	private JLabel folerLabel;
	private JTextField folerText;

	private JButton saveButton;

	private JDialog dialog;

	public void showDialog(){
		// 新增对话框
		newJDialog();
		loadInitDatas();
		initComponents();
		initDimension();
		initActions();
		initLayout();
	}

	public void newJDialog(){
		dialog = new JDialog();
		dialog.setTitle(title);
		dialog.setSize(460, 360);
		dialog.setIconImage(CmUtil.getImageFromServer("assist.gif"));
		dialog.setResizable(false);
		dialog.setResizable(true);
		dialog.setModal(true);

		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setLayout(new BorderLayout());
	}
	public AddPartDialog(CmTree tree,Window owner,CmTreeNode node,String title) {

		this.tree = tree;
		this.node = node;
		CmLightPart lightPart = node.getPart();
		this.parentPart = CmBizObjUtil.getWTPartFromLightPart(lightPart);




	}

	protected void initActions() {
		saveButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String number = partNumberText.getText();
				String name = partNameText.getText();
				String userCount = userCountText.getText();
				StringBuffer sb = new StringBuffer();
				if(CmCommonStringUtil.isEmpty(name)){
					sb.append("部件编号不能为空\n");
				}
				if(CmCommonStringUtil.isEmpty(number)){
					sb.append("部件名称不能为空\n");
				}
				if(CmCommonStringUtil.isEmpty(userCount)){
					sb.append("使用数量不能为空\n");
				}

				if(!"".equals(sb.toString())){
					JOptionPane.showMessageDialog(owner,sb.toString() );
					return;
				}
				number = number.toUpperCase();
				long id = PBOMEditorToWCIntf.queryLatestPartIdByNumberRMI(number);

				if(id!=-1){
					JOptionPane.showMessageDialog(owner,"部件【"+number+"】已经存在" );
					return;
				}

				String view = viewComboBox.getSelectedItem().toString();
				String enditem = isProductComboBox.getSelectedItem().toString();
				String parttype = ((KVItem)modelComboBox.getSelectedItem()).getKey();
				String defaulttracecode = ((KVItem)zzdmComboBox.getSelectedItem()).getKey();
				String defaultunit = ((KVItem)unitComboBox.getSelectedItem()).getKey();
				String hidepartinstructure = collectPartComboBox.getSelectedItem().toString();
				String phantom = isVirtualComboBox.getSelectedItem().toString();
				String keycomponent = keyComboBox.getSelectedItem().toString();
				String partkit = ctjbsComboBox.getSelectedItem().toString();
				String ctype = ctypeComboBox.getSelectedItem().toString();
				String secret = secretComboBox.getSelectedItem().toString();
				String phasecode = levelComboBox.getSelectedItem().toString();
				String foler = folerText.getText();

				int flag = JOptionPane.showConfirmDialog(owner, "确定保存吗？","确认", JOptionPane.OK_CANCEL_OPTION);
				if(flag==0){
                   	HashMap<String, String> map = new HashMap<String, String>();
                   	HashMap<String, String> ibaMap = new HashMap<String, String>();
                   	map.put("PRODUCTNO", parentPart.getContainerName());
                   	map.put("FOLDER", foler);
                   	map.put("PARENTNO", parentPart.getNumber());
                   	map.put("AMOUNT", userCount);
                   	map.put("ENDITEMIN", enditem);
                   	map.put("PARTTYPE", parttype);
                   	map.put("DEFAULTTRACECODE", defaulttracecode);
                   	map.put("DEFAULTUNIT", defaultunit);
                   	map.put("HIDEPARTINSTRUCTURE",hidepartinstructure);
                   	map.put("PHANTOM", phantom);
                   	map.put("PARTKIT",  partkit);

                   	ibaMap.put("KEYCOMPONENT", keycomponent);
                   	ibaMap.put("CTYPE", ctype);
                   	map.put("VIEW", view);
                   	ibaMap.put("SCREET", secret);
                   	ibaMap.put("PHASECODE", phasecode);

					WTPart part = PBOMEditorToWCIntf.createPartForAddPart(number, name, map, ibaMap, parentPart.getContainer());
					part = PBOMEditorToWCIntf.createPlanningPartRMI(part, number, name);
					if(part ==null){
						JOptionPane.showMessageDialog(owner,"添加Part失败");
						return;
					}

					    CmLightPart newLightPart = CmBizObjUtil.buildCmLightPartFromWTPart(part);
					    newLightPart.setOperType("new");
					    newLightPart.setEdit(true);

					    CmMPartMaster master = new CmMPartMaster(newLightPart);
					    String item = part.getNumber() + "(" + part.getName()+ ") " ;
						CmTreeNode cmTreeNode = new CmTreeNode(item);
						cmTreeNode.setPart(newLightPart);
						cmTreeNode.setUserObject(master);
						String zxsl = userCount;
						int sl = 1;
						try {
							sl = Integer.parseInt(zxsl);
						} catch (NumberFormatException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						newLightPart.setUseCount(sl);
						CmCommonStringUtil.addCommonMiddleNodeWithCommonParent(tree.getRoot(),cmTreeNode,sl,node);
						EbomTreeCancelAction.addPbomTreeChange(cmTreeNode,null, "create",null);
						CmCommonStringUtil.addNodeToCheckList(cmTreeNode);
						CmCommonPackageAction common = new CmCommonPackageAction();
						common.packageOneNode(tree.getRoot(), cmTreeNode);
						PbomTreeEditReportAction.updatePbomTreeEditReport();
						BomTreeReportAction.updateBomReport();
						CmCommonStringUtil.sortTheTreeNode(tree.getRoot());
						tree.updateUI();
						dialog.dispose();
				}
			}

		});
	}

	class AddPartLabel extends JLabel{
		public AddPartLabel(String text){
			setText(text);
			this.setFont(new Font("宋体",Font.PLAIN,15));
		}
	}

	protected void initComponents() {
		partNumberLabel = new AddPartLabel("编号");
		partNumberText = new JTextField();

		partNameLabel = new AddPartLabel("名称");
		partNameText = new JTextField();

		isProductLable = new AddPartLabel("是否成品");
		isProductComboBox = new JComboBox(YES_OR_NO);
		modelLabel = new AddPartLabel("装配模式");
		modelComboBox = new KVJComboBox(LoadConfig.getInstance().getPartOFPartType());

		collectPartLabel = new AddPartLabel("收集部件");
		collectPartComboBox = new JComboBox(YES_OR_NO);
		isVirtualLabel = new AddPartLabel("虚拟制造部件");
		isVirtualComboBox = new JComboBox(YES_OR_NO);

		keyLabel = new AddPartLabel("关重件标识");
		keyComboBox = new JComboBox(LoadConfig.getInstance().getPartOFKeyComponent());

		ctjbsLabel = new AddPartLabel("成套件标识");
		ctjbsComboBox = new JComboBox(YES_OR_NO);

		ctypeLable = new AddPartLabel("零部件类型");
		ctypeComboBox = new JComboBox(LoadConfig.getInstance().getPartType());

		viewLable = new AddPartLabel("视图");
		viewComboBox = new JComboBox(LoadConfig.getInstance().getPartOFView());

		secretLabel = new AddPartLabel("密级");
		secretComboBox = new JComboBox(LoadConfig.getInstance().getPartOFSecret());

		levelLabel = new AddPartLabel("阶段");
		levelComboBox = new JComboBox(LoadConfig.getInstance().getPartOFPhase())	;

		userCountLabel = new AddPartLabel("使用数量");
		userCountText = new JTextField();
		userCountText.setText("1");

		folerLabel = new JLabel("存储文件夹路径");
		folerText = new JTextField();

		zzdmLabel = new  AddPartLabel("默认追踪代码");
		zzdmComboBox = new KVJComboBox(LoadConfig.getInstance().getPartOFZzdm())	;

		unitLabel = new  AddPartLabel("单位");
		unitComboBox = new KVJComboBox(LoadConfig.getInstance().getPartOFUnit())	;

		ImageIcon saveImage = new ImageIcon(CmUtil.getImageFromServer("save.gif"));
		saveButton = new JButton("保 存",saveImage);
		saveButton.setFont(new Font("宋体",Font.PLAIN,15));
	}

	protected void initLayout() {
		JPanel contentPanel = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		c.insets =new java.awt.Insets(5,5,5,5);
		c.fill = GridBagConstraints.BOTH;
		c.weightx=1;
		c.ipady = 18;
		contentPanel.setLayout(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;

		contentPanel.add(partNumberLabel,c);
		c.gridx = 1;
//		partNumberText.setPreferredSize(new Dimension(150, 25));
		contentPanel.add(partNumberText,c);
		c.gridx = 2;
		contentPanel.add(partNameLabel,c);
		c.gridx = 3;
//		partNumberText.setPreferredSize(new Dimension(150, 25));
		contentPanel.add(partNameText,c);
		c.gridy = 2;
		c.gridx = 0;

		contentPanel.add(isProductLable,c);
		c.gridx=1;
		contentPanel.add(isProductComboBox,c);

		c.gridx = 2;
		contentPanel.add(modelLabel,c);
		c.gridx = 3;
		contentPanel.add(modelComboBox,c);

		c.gridy = 4;
		c.gridx = 0;
		contentPanel.add(collectPartLabel,c);
		c.gridx = 1;
		contentPanel.add(collectPartComboBox,c);
		c.gridx = 2;
		contentPanel.add(isVirtualLabel,c);
		c.gridx = 3;
		contentPanel.add(isVirtualComboBox,c);


		c.gridy = 6;
		c.gridx = 0;
		contentPanel.add(keyLabel,c);
		c.gridx = 1;
		contentPanel.add(keyComboBox,c);

		c.gridx = 2;
		contentPanel.add(ctjbsLabel,c);
		c.gridx = 3;
		contentPanel.add(ctjbsComboBox,c);


		c.gridy = 8;
		c.gridx = 0;
		contentPanel.add(viewLable,c);
		c.gridx = 1;
		contentPanel.add(viewComboBox,c);

		c.gridx = 2;
		contentPanel.add(secretLabel,c);
		c.gridx = 3;
		contentPanel.add(secretComboBox,c);


		c.gridy = 10;
		c.gridx = 0;
		contentPanel.add(levelLabel,c);
		c.gridx = 1;
		contentPanel.add(levelComboBox,c);

		c.gridx = 2;
		contentPanel.add(userCountLabel,c);
		c.gridx = 3;
		contentPanel.add(userCountText,c);



		c.gridy = 12;
		c.gridx = 0;
		contentPanel.add(zzdmLabel,c);
		c.gridx = 1;
		contentPanel.add(zzdmComboBox,c);

		c.gridx = 2;
		contentPanel.add(unitLabel,c);
		c.gridx = 3;
		contentPanel.add(unitComboBox,c);


		c.gridy = 14;
		c.gridx = 0;
		contentPanel.add(ctypeLable,c);
		c.gridx = 1;
		contentPanel.add(ctypeComboBox,c);
		c.gridx = 2;
		contentPanel.add(folerLabel,c);
		c.gridx = 3;
		contentPanel.add(folerText,c);


		JPanel bottomPanel = new JPanel();
		bottomPanel.add(saveButton);

		JPanel mainJPanel = new JPanel();
		mainJPanel.setLayout(new BorderLayout());

		JPanel topJpanel= new JPanel();
		JLabel title = new JLabel("添加零部件");
		title.setFont(new Font("宋体",Font.PLAIN,25));
		title.setForeground(Color.red);
		topJpanel.add(title);
		mainJPanel.add(contentPanel,BorderLayout.CENTER);
		mainJPanel.add(bottomPanel,BorderLayout.SOUTH);
		mainJPanel.add(topJpanel,BorderLayout.NORTH);
		dialog.setContentPane(mainJPanel);
		dialog.setVisible(true);
	}
	   protected  void initDimension(){


//			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
//
//			this.setBounds(owner.getX(), owner.getY(), owner.getWidth(), owner.getHeight());
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			int width = screen.width > 1100 ? 1000 : 800;
			int height = 700;
			String maximized = "";



			if (maximized.equals("MAIN_MAXIMIZED")) {
				dialog.setBounds(0, 0, screen.width, screen.height);
			} else {
				int ancleft = (screen.width - width) / 2;
				int anctop = (screen.height - height) / 2;

				dialog.setBounds(ancleft, anctop, width, height);
			}
	   }
	protected void loadInitDatas() {
		// TODO Auto-generated method stub

	}

	protected void registerTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}

	protected void unregisterTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}

}


