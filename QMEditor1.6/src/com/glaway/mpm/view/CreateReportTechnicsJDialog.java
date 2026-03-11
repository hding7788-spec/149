package com.glaway.mpm.view;

import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Document;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.*;

/**
 * 新建报表类工艺文件
 *
 * @author LongXiuChuan
 */
public class CreateReportTechnicsJDialog extends javax.swing.JDialog {

	private JDialog dialog;
	private NewTechnicsPart frame;
	private Document document;
	public Element techElement = null;
	private Map<String, String> typeMap = new HashMap<String, String>();
	private String technicsCategory;
	private List<String> ibaList = new ArrayList<String>();
	private javax.swing.JLabel jLabel1;//产品型号代号
	private javax.swing.JTextField	text11;
	private javax.swing.JLabel jLabel2;//产品代号
	private javax.swing.JTextField text12 ;
    private javax.swing.JLabel jLabel3;//部件代号
	private javax.swing.JTextField text13 ;
	private javax.swing.JLabel jLabel4;//部件名称
	private javax.swing.JTextField text14 ;
	private javax.swing.JLabel jLabel5;//产品阶段标记
	private javax.swing.JTextField text15;
	private javax.swing.JLabel jLabe16 ;
	private javax.swing.JComboBox box16;
	private javax.swing.JTextField text16;
	private javax.swing.JComboBox box;
	private Map<String,String> ibasMap = new HashMap<String,String>();
	private javax.swing.JLabel jLabel9 ;
	private javax.swing.JTextField text19;
	private javax.swing.JLabel jLabe110;
	private javax.swing.JComboBox box110;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JTextField  text111;
	private JPanel panel0 = new JPanel();
	private JPanel panel2 = new JPanel();
	private String partNumber;
	private Element parentElement;
	private XWTreeNode treeNode;
	public  static XWTreeNode Currentnode;
	public  static String mindex = "";
	public  static String batch = "";
	public  static String cindex="";
	public  static String pindex="";
	public  static String xhjh="";
    public  static String productName="";
    public  static String currentValue="";
	private String[] secret_array = {"公开","内部"};
//	private String[] secret_array = {"公开","内部","秘密★10年","机密★20年"};
//	private Vector<String> batchs;
//	private JComboBox pcno_box_value = null;
	private JTextField pcno_field;
	/**
	 * Creates new form CreateReportTechnicsJDialog
	 */
	public CreateReportTechnicsJDialog(NewTechnicsPart parent, Element element,
			XWTreeNode node) {
		super(parent, true);
		this.frame = parent;
		treeNode=node;
		Currentnode=node;
		parentElement=element;
		setTitle("新建报表类工艺");
		setIconImage(new ImageIcon(getClass().getResource("/images/technics.gif")).getImage());
		Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension2.getWidth() - 500) / 2 , (int) (dimension2.getHeight() - (625)) / 2, 500, 580);
		partNumber = XmlUtility.getAttributeValue(parentElement, "partNumber");
		box=new javax.swing.JComboBox(getBaobiaoStyle(node).toArray());

		//获取当前产品的批次号
//        if(batchs == null) {
//            long oid = Long.valueOf(XmlUtility.getAttributeValue(parentElement, "containerId"));
//            try {
//                batchs = TechnicsIntf.getBatchsByProductOid(oid);
//            } catch (RemoteException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            } catch (InvocationTargetException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
//            if(batchs == null) {
//                batchs = new Vector<String>();
//                batchs.add("");
//            }
//        }
//        pcno_box_value = new JComboBox(batchs);
		pcno_field = new JTextField();
		pcno_field.setText(XmlUtility.getAttributeValue(parentElement, "BATCH"));
		pcno_field.setEditable(false);
		initIBAList();
		if(ibasMap.isEmpty()) {
			try {
				ibasMap = TechnicsIntf.getPartIBAValuesByNumber(partNumber, ibaList);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}



		initComponents();

		this.dialog = this;
		init();
	}
	public void initIBAList(){
		ibaList.add("PHASE_CODE");
		ibaList.add("MINDEX");
		ibaList.add("PINDEX");
		ibaList.add("KEYCOMPONENT");
	}
	private void initLayout() {
		Container container = getContentPane();

		panel0.setLayout(new BorderLayout());
		jPanel1.setLayout(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(panel0);
		panel0.add(jPanel1,BorderLayout.CENTER);
		container.add(scrollPane);
		panel0.add(panel2,BorderLayout.SOUTH);
		panel2.setLayout(new GridBagLayout());
		panel2.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		sure.setPreferredSize(new Dimension(70, 23));
		sure.setMinimumSize(new Dimension(70, 23));
		sure.setMaximumSize(new Dimension(70, 23));
		panel2.add(sure, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 15, 15), 0, 0));
		close.setPreferredSize(new Dimension(70, 23));
		close.setMinimumSize(new Dimension(70, 23));
		close.setMaximumSize(new Dimension(70, 23));
		panel2.add(close, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 15, 15), 0, 0));
	}

	private void init() {

		Map<String, List<List<String>>> map = frame.getReportTechnicsAttriMap();
		Iterator<String> ite = map.keySet().iterator();
		while(ite.hasNext()) {
			String[] key = ite.next().split(":");
			typeMap.put(key[0], key[1]);
			typeBox.addItem(key[0]);
		}

//		if (!"".equals(frame.workItemOid)&&!"null".equals(frame.workItemOid)&&frame.workItemOid!=null) {
//
//			String reportFlag = (String) IntfUtil.getPeRemoteMethodInvoke("getBaoBiaoLeiXing",
//					new Class[] { String.class }, new Object[] { frame.workItemOid });
//			text16.setText(reportFlag);
//		}
//		text16.setEditable(false);

		box.setSelectedItem("");
		box.setEditable(false);
		XWTreeNode p = treeNode.getP();
		XWTreeObject object = p.getObject();
		if (object instanceof XWProductTreeObject) {
		    XWProductTreeObject productTreeObject=(XWProductTreeObject) object;
		    Element productElement = productTreeObject.getTreeCellData();
		    productName=productElement.attributeValue("productName");
        }
		 XWTreeObject treeObj = treeNode.getObject();

         if (treeObj instanceof XWPartTreeObject) {
             XWPartTreeObject partObj = (XWPartTreeObject) treeObj;
             Element partEle = partObj.getTreeCellData();
             mindex = partEle.attributeValue("MINDEX");
             batch = partEle.attributeValue("BATCH");
             cindex= partEle.attributeValue("CINDEX");
             pindex=partEle.attributeValue("PINDEX");


         }

         if (partNumber!=null&&!"".equals(partNumber)&&!"null".equals(partNumber)) {
             xhjh = (String) IntfUtil.getPeRemoteMethodInvoke("getXhjhByContainer",
                     new Class[] { String.class }, new Object[] { partNumber });
        }
		text111.setText(batch);
		setMiddleOnScreenWithDialog(dialog);

		dialog.setVisible(true);
	}

	private void initComponents() {
		jPanel1 = new javax.swing.JPanel();

		jLabel1 = new javax.swing.JLabel();
		text11 = new javax.swing.JTextField();
		jLabel2 = new javax.swing.JLabel();
		text12 = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		text13 = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		text14 = new javax.swing.JTextField();
		jLabel5 = new javax.swing.JLabel();
		text15= new javax.swing.JTextField();
		jLabe16 = new javax.swing.JLabel();
		text16= new javax.swing.JTextField();
		//获取到所有的车间组，然后自动定位到当前用户所在的组
		List<String> allList = new ArrayList<String>();
		Map<String,String> workShop = ResourceIntf.getWorkShops();
		if (workShop != null && workShop.size() > 0) {
			Collection<String> coll = workShop.values();
			Iterator<String> it = coll.iterator();
			while (it.hasNext()) {
				String temp = (String) it.next();
				if (temp != null && temp.trim().length() > 0) {
					allList.add(temp);
				}
			}

			Collections.sort(allList);
		}
		String groupName = "";
		box16= new javax.swing.JComboBox(allList.toArray());
		try {
			groupName = TechnicsIntf.getUsertechnicsGroupName();
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InvocationTargetException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if(allList.contains(groupName)) {
			box16.setSelectedItem(groupName);
		}
		jLabel7 = new javax.swing.JLabel();
		typeBox = new javax.swing.JComboBox();
		jLabel8 = new javax.swing.JLabel();
		numberField = new javax.swing.JTextField();
		jLabel9 = new javax.swing.JLabel();
		text19= new javax.swing.JTextField();
		jLabe110 = new javax.swing.JLabel();
		 box110= new javax.swing.JComboBox(secret_array);
		 jLabel11 = new javax.swing.JLabel();
	     text111= new javax.swing.JTextField();


		sure = new javax.swing.JButton();
		close = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		jLabel1.setText("产品型号代号           ");
		text11.setEditable(false);
		jLabel2.setText("产品代号      ");
		text12.setEditable(false);
		jLabel3.setText("部件代号");
		text13.setEditable(false);
		jLabel4.setText("部件名称");
		text14.setEditable(false);
		jLabel5.setText("产品阶段标记");
		jLabe16.setText("部门");

		jLabel7.setText("报表类型：");
		jLabel9.setText("工艺文件名称");
		jLabe110.setText("文件密级");
		jLabel11.setText("批次号");
		typeBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
//				typeBoxActionPerformed(evt);
			}
		});

		jLabel8.setText("工艺编号：");

		numberField.setEditable(false);

		sure.setText("确  定");
		sure.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				sureActionPerformed(evt);
			}
		});

		close.setText("取  消");
		close.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				closeActionPerformed(evt);
			}
		});
		initLayout();
		//设置网格布局管理器参数
				final GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new Insets(5, 5, 0, 0);
				gridBagConstraints.gridwidth = 2;

				//第一行：产品型号代号
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabel1, gridBagConstraints);


				text11.setText(ibasMap.get("MINDEX"));
				text11.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(text11, gridBagConstraints);
//------------------------------------------------

				//第二行：产品代号
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabel2, gridBagConstraints);


				text12.setText(ibasMap.get("PINDEX"));
				text12.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(text12, gridBagConstraints);
				//第三行：部件代号
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 2;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabel3, gridBagConstraints);


				text13.setText(XmlUtility.getAttributeValue(parentElement, "partNumber"));
				text13.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 2;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(text13, gridBagConstraints);
				//第四行：部件名称
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 3;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabel4, gridBagConstraints);


				text14.setText(XmlUtility.getAttributeValue(parentElement, "partName"));
				text14.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 3;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(text14, gridBagConstraints);
				//第五行：产品阶段标记
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 4;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabel5, gridBagConstraints);


				text15.setEditable(false);
				text15.setText(ibasMap.get("PHASE_CODE"));
				text15.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 4;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(text15, gridBagConstraints);

				//第六行：部门
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 5;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabe16, gridBagConstraints);


				box16.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 5;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(box16, gridBagConstraints);

				//第七行：工艺类型
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 6;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabel7, gridBagConstraints);

				box.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 6;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(box, gridBagConstraints);

				box.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                       if (xhjh!=null&&!"".equals(xhjh)&&!"null".equals(xhjh)) {
                           currentValue=xhjh;
                        }else{
                            currentValue=mindex;
                        }
                        String per="";
                        if ("工艺文件目录".equals(box.getSelectedItem())) {
                             per="Rz/" + currentValue + "-WM-";
                        }else if ("工艺路线表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-LX-";
                        }else if ("工艺装备明细表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-MXz-";
                        }else if ("仪器仪表明细表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-MXy-";
                        }else if ("非标仪器仪表、设备明细表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-MXf-";
                        }else if ("标准刀量具明细表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-MXd-";
                        }else if ("外协件明细表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-MXw-";
                        }else if ("关键工序明细表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-MXg-";
                        }else if ("材料消耗工艺定额明细表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-DEl-";
                        }else if ("辅助材料定额表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-DEf-";
                        }else if ("辅助材料定额汇总表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-DEh-";
                        }else if ("外购件（元器件、标准件）消耗工艺定额汇总表".equals(box.getSelectedItem())) {
                            per="Rz/" + currentValue + "-MXb-";
                        }
                       String number=(String) IntfUtil.getPeRemoteMethodInvoke("getSeqNumber",
                                new Class[] { Integer.class,String.class }, new Object[] {1,per});


                       if ("工艺文件目录".equals(box.getSelectedItem())) {
                           numberField.setText("Rz/" + currentValue + "-WM-" +number);
                        }else if ("工艺路线表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-LX-" +number);
                        }else if ("工艺装备明细表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-MXz-" +number);
                        }else if ("仪器仪表明细表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-MXy-" +number);
                        }else if ("非标仪器仪表、设备明细表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-MXf-" +number);
                        }else if ("标准刀量具明细表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-MXd-" +number);
                        }else if ("外协件明细表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-MXw-" +number);
                        }else if ("关键工序明细表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-MXg-" +number);
                        }else if ("材料消耗工艺定额明细表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-DEl-" +number);
                        }else if ("辅助材料定额表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-DEf-" +number);
                        }else if ("辅助材料定额汇总表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-DEh-" +number);
                        }else if ("外购件（元器件、标准件）消耗工艺定额汇总表".equals(box.getSelectedItem())) {
                            numberField.setText("Rz/" + currentValue + "-MXb-" +number);
                        }

                       text19.setText((String)box.getSelectedItem());
                    }
                });

				//第八行：工艺文件编号
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 7;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabel8, gridBagConstraints);

				numberField.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 7;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(numberField, gridBagConstraints);

				//第九行：工艺文件名称
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 8;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabel9, gridBagConstraints);

				text19.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 8;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(text19, gridBagConstraints);

				//第10行：文件密级
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 9;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabe110, gridBagConstraints);

				box110.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 9;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(box110, gridBagConstraints);


				//第10行：文件密级
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 10;
				gridBagConstraints.gridwidth = 1;
				jPanel1.add(jLabel11, gridBagConstraints);

//				pcno_box_value.setPreferredSize(new Dimension(300, 23));
//				gridBagConstraints.gridwidth = 2;
//				gridBagConstraints.gridx = 1;
//				gridBagConstraints.gridy = 10;
//				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
//				jPanel1.add(pcno_box_value, gridBagConstraints);

 				pcno_field.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 10;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				jPanel1.add(pcno_field, gridBagConstraints);



	}

	private void sureActionPerformed(java.awt.event.ActionEvent evt) {
	    String selectedItem = (String) box.getSelectedItem();
	    if ("".equals(selectedItem)) {
	        JOptionPane.showMessageDialog(dialog,"请选择正确的报表类型！" );
	        return;
        }
		final VaActionProgressBar progressBar = new VaActionProgressBar(null, "创建报表类工艺文件", "正在创建报表类工艺文件,请等待...", "数据处理中");
		Thread thread = new Thread() {
			public void run() {
//				String type = String.valueOf(typeBox.getSelectedItem());
				String type = String.valueOf(box.getSelectedItem());
				String number = numberField.getText();
				String prefix = number.substring(0, number.lastIndexOf("-") + 1);
				IntfUtil.getPeRemoteMethodInvoke("getSeqNumber",
                         new Class[] { Integer.class,String.class }, new Object[] {2, prefix});

				techElement = XmlUtility.createReportTechnics();

				XmlUtility.setAttributeValue(techElement, "technicsCategory", technicsCategory);
				XmlUtility.setAttributeValue(techElement, "unite", "common");

				String docNumber = null;
				try {
					docNumber = TechnicsIntf.genTechnicsNumber();
				} catch(InvocationTargetException e) {
					throw new RuntimeException(e);
				} catch(RemoteException e) {
					throw new RuntimeException(e);
				}
				if("".equals(docNumber)){
					JOptionPane.showMessageDialog(frame, "工艺文件流水号生成失败！", "提示", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				String pindex = "";
                String cindex="";
                String partVersion="";
				//XWTreeNode partNode = frame.xwPartTreePanel.getSelectedTreeNode();
				XWTreeObject treeObj = treeNode.getObject();
				if (treeObj instanceof XWPartTreeObject) {
					XWPartTreeObject partObj = (XWPartTreeObject) treeObj;
					Element partElement = partObj.getTreeCellData();

					List<String> list = getAttributes();
					for (String key : list) {
						XmlUtility.setAttributeValue(techElement, key, partElement.attributeValue(key));
					}

					XmlUtility.setAttributeValue(techElement, "partOid", partElement.attributeValue("oid"));
					XmlUtility.setAttributeValue(techElement, "modifyTime", XmlUtility.getCurrentTime());
					pindex = partElement.attributeValue("PINDEX");
					cindex= partElement.attributeValue("CINDEX");
					partVersion=partElement.attributeValue("version");
				}

				List<String> list = UserUtil.getCurrentUserOid();
				String creator = "";
				if (list != null && list.size() == 3) {
					creator = list.get(0);
					String creatorOid = (String) list.get(1);
					String creatorDisplay = (String) list.get(2);
					XmlUtility.setAttributeValue(techElement, "creator", creator);
					XmlUtility.setAttributeValue(techElement, "creatorOid", creatorOid);
					XmlUtility.setAttributeValue(techElement, "creatorDisplay", creatorDisplay);
				}

				// 获取工艺文件编号后五位流水码
//				try {
//					progressBar.setHeaderMessage("获取报表类工艺文件编号流水码");
//					String sno = TechnicsIntf.getReportTechnicsSequenceNumber(creator, type, pindex);
//					if(sno == null || "".equals(sno)) {
//						JOptionPane.showMessageDialog(dialog, "工艺文件编号生成流水号时出错！");
//						return;
//					}

//					number = number /*+ "-" + sno*/;
//				} catch (InvocationTargetException e) {
//					e.printStackTrace();
//					JOptionPane.showMessageDialog(dialog, "生产流水号时出错！\r\n"+e.getLocalizedMessage());
//				}

				String pplanName=text19.getText();
				String name = pplanName+"(" + number + ")";
                //业务属性
				XmlUtility.setAttributeValue(techElement, "technicsNumber", docNumber);
				XmlUtility.setAttributeValue(techElement, "pplanNumber", number);
				XmlUtility.setAttributeValue(techElement, "pplanName", pplanName);
				XmlUtility.setAttributeValue(techElement, "technicsName", name);
				XmlUtility.setAttributeValue(techElement, "pplanName", String.valueOf(text19.getText()));
				XmlUtility.setAttributeValue(techElement, "version", "space.1");
				XmlUtility.setAttributeValue(techElement, "technicsType", type);
				XmlUtility.setAttributeValue(techElement, "lifecycle", "正在工作");
				XmlUtility.setAttributeValue(techElement, "code", String.valueOf(typeMap.get(type)));
				XmlUtility.setAttributeValue(techElement, "MINDEX", text11.getText());
				XmlUtility.setAttributeValue(techElement, "PINDEX", text12.getText());
				XmlUtility.setAttributeValue(techElement, "partNumber", text13.getText());
				XmlUtility.setAttributeValue(techElement, "partName", text14.getText());
				XmlUtility.setAttributeValue(techElement, "PHASE_CODE", text15.getText());
				XmlUtility.setAttributeValue(techElement, "DEPT",(String) box16.getSelectedItem());
				XmlUtility.setAttributeValue(techElement, "CINDEX",cindex);
//				XmlUtility.setAttributeValue(techElement, "REPOERTYPE",(String)typeBox.getSelectedItem());
				XmlUtility.setAttributeValue(techElement, "REPOERTYPE",(String)box.getSelectedItem());
				XmlUtility.setAttributeValue(techElement, "description","");
				XmlUtility.setAttributeValue(techElement, "SECRET",(String)box110.getSelectedItem());
				XmlUtility.setAttributeValue(techElement, "BATCH",text111.getText());
				//其他属性
				XmlUtility.setAttributeValue(techElement, "partVersion",partVersion);
				XmlUtility.setAttributeValue(techElement, "BATCH",text111.getText());
				String key = type + ":" + String.valueOf(typeMap.get(type));

				progressBar.setHeaderMessage("正在收集 " + type + "数据......");
				//数据源为当前工艺编辑器中PBOM树工艺节点对象
				//ReportTechnicsUtil.processAttributes(techElement, frame, frame.getReportTechnicsAttriMap().get(key), type);

				//数据源为PDM系统，通过调用服务器端接口获取。
//				ReportTechnicsUtil.processAttributes2(techElement, frame, frame.getReportTechnicsAttriMap().get(key), type);

				document = writeDocument(techElement);

				progressBar.finish();
				progressBar.setVisible(false);

				dialog.setVisible(false);
			}
		};
		thread.start();
		progressBar.setVisible(true);
	}

	private Document writeDocument(Element techElement) {
		try {
			// 创建目录
			String dir = WorkSpaceUtil.createTechnicsDirectory(
					techElement.attributeValue("technicsNumber"), null, null, null,
					null, null);
			// 创建文件
			return WorkSpaceUtil.createReportTechnics(techElement, dir);
		} catch (Exception e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(frame, "创建报表类文件时出现错误！\r\n" + e1.getLocalizedMessage());
		}
		return null;
	}

	private void closeActionPerformed(java.awt.event.ActionEvent evt) {
		this.dialog.setVisible(false);
	}

	private void typeBoxActionPerformed(java.awt.event.ActionEvent evt) {
		//XWTreeNode partNode = frame.xwPartTreePanel.getSelectedTreeNode();
		XWTreeObject treeObj = treeNode.getObject();
		String pindex = "";
		if (treeObj instanceof XWPartTreeObject) {
			XWPartTreeObject partObj = (XWPartTreeObject) treeObj;
			Element partEle = partObj.getTreeCellData();
			pindex = partEle.attributeValue("PINDEX");
		}
		numberField.setText("Rz/" + pindex + "-" + typeMap.get(typeBox.getSelectedItem()));
	}

	/**
	 * 将dialog屏幕居中显示
	 *
	 * @author chenyunlong
	 * @date 2013-6-13
	 * @param dialog
	 *
	 */
	public static void setMiddleOnScreenWithDialog(JDialog dialog) {
		int windowWidth = dialog.getWidth(); // 获得窗口宽
		int windowHeight = dialog.getHeight(); // 获得窗口高
		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width; // 获取屏幕的宽
		int screenHeight = screenSize.height; // 获取屏幕的高
		dialog.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2
				- windowHeight / 2);// 设置窗口居中显示
	}

	public Document getDocument() {
		return document;
	}

	private static List<String> getAttributes() {
		List<String> list = new ArrayList<String>();
		list.add("partNumber");
		list.add("partName");
		list.add("partType");
		list.add("partVersion");
		list.add("PINDEX");
		list.add("PHASE_CODE");
		list.add("occId");
		list.add("material");
		list.add("dutu");
		list.add("remark");
		list.add("useCount");
		list.add("gysl");
		list.add("XHPHCL");
		list.add("CSIZE");
		list.add("JSTJBZH");
		list.add("CMAT_UP");
		list.add("CMAT_DOWN");
		list.add("PZGGBZH");
		list.add("JDDJ");
		list.add("CLZT");
		list.add("ZLDJ");
		list.add("ZQCLBZH");
		list.add("ZQCLBZH");
		list.add("ZQCLMC");
		list.add("XHPH");
		list.add("JSTJ");
		list.add("JBCLMC");
		list.add("CMAT");
		list.add("MTYPE");
		list.add("ZZCJ");
		list.add("FZCJ");

		//设计资源库新增属性
		//start
		list.add("SHORTNAME");
		list.add("STANDARDNUMBER");
		list.add("MECHANICALPROPERTYORHARDNESS");
		list.add("SURFACETREATMENT");
		list.add("HEATTREATMENT");
		list.add("PRODUCTFORM");
		list.add("PRODUCTLEVEL");
		list.add("PLATECSCREWFORM");
		list.add("ISIMPORT");
		list.add("SPECIALINSTRUCTION");
		list.add("MEASUREUNIT");
		list.add("TYPE");
		list.add("TYPESTANDARD");
		list.add("QUALITYLEVEL");
		list.add("TOTALSTANDARD");
		list.add("DETAILSTANDARD");
		list.add("PACKAGINGFORM");
		list.add("OUTLINESIZE");
		list.add("SPECIALCONDITION");
		list.add("EXTRACONDITION");
		list.add("MATTYPE");
		//end

		return list;

	}

	private  static List<String> getBaobiaoStyle(XWTreeNode node) {
	    XWTreeObject object = node.getObject();
	    Vector<String> vector = new Vector<String>();
	    if (object instanceof XWPartTreeObject) {
	        XWPartTreeObject obj=  (XWPartTreeObject)object;
	        int i = node.getChildCount();
	        for (int j = 0; j < i; j++) {
	            XWTreeNode childAt = (XWTreeNode) node.getChildAt(j);
	            XWTreeObject childObject = childAt.getObject();
	            if (childObject instanceof ReportTechnicsTreeObject) {
	                ReportTechnicsTreeObject reportObject=  (ReportTechnicsTreeObject) childObject;
	                Element ele = reportObject.getTreeCellData();
	                String lifecycle = ele.attributeValue("lifecycle");
	                if(!"已作废".equals(lifecycle)){
	                	String type = reportObject.getType();
	 	                vector.add(type);
	                }

                }
            }
        }
	    List<String> list = new ArrayList<String>();
	    list.add("");
	    list.add("工艺文件目录");
	    list.add("工艺路线表");
	    list.add("工艺装备明细表");
	    list.add("仪器仪表明细表");
	    list.add("非标仪器仪表、设备明细表");
	    list.add("标准刀量具明细表");
	    list.add("外协件明细表");
	    list.add("关键工序明细表");
	    list.add("材料消耗工艺定额明细表");
	    list.add("辅助材料定额表");
	    list.add("辅助材料定额汇总表");
	    list.add("外购件（元器件、标准件）消耗工艺定额汇总表");
	    list.removeAll(vector);
	    return list;
    }

	// Variables declaration - do not modify
	private javax.swing.JButton close;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JTextField numberField;
	private javax.swing.JButton sure;
	private javax.swing.JComboBox typeBox;
	// End of variables declaration
}
