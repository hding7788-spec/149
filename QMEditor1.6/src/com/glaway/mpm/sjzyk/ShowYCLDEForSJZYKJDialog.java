package com.glaway.mpm.sjzyk;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.ObjectTransfer;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.view.XWTreeObject;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class ShowYCLDEForSJZYKJDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private NewTechnicsPart frame;
    private XWTreeNode node;

    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JButton jButton5;
    private JButton jButton6;
    private JComboBox jComboBox1;
    private JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private JLabel jLabel1;
    private JLabel jLabel10;
    private JLabel jLabel11;
    private JLabel jLabel12;
    private JLabel jLabel13;
    private JLabel jLabel2;
    private JLabel jLabel3;
    private JLabel jLabel4;
    private JLabel jLabel5;
    private JLabel jLabel6;
    private JLabel jLabel7;
    private JLabel jLabel8;
    private JLabel jLabel9;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel3;
    private JPanel jPanel4;
    private JPanel jPanel5;
    private JScrollPane jScrollPane1;
    private JScrollPane jScrollPane2;
    private JTable jTable1;
    private JTable jTable2;
    private JTextField jTextField1;
    private SjzykJComboBox nameBox;
    private JTextField jTextField3;
    private JTextField jTextField4;
    private JTextField jTextField5;
    private SjzykJComboBox markNumberBox;
    private SjzykJComboBox csizeBox;
    private SjzykJComboBox useStandardBox;
    private JTextField jTextField9;

    private MyJMenuBar setMenuBar;

    private Map<String,List<String>> map;
    private static VaActionProgressBar progressBar;
    private Map<String,String> vMap;
    private String[] tableHeader;
    private int[] tableColWidth;

	/**
     * Creates new form AddStandardPartJDialog
     */
    public ShowYCLDEForSJZYKJDialog(NewTechnicsPart frame, XWTreeNode node, boolean modal, VaActionProgressBar progressBar) {
    	super(frame, modal);
        this.frame = frame;
        this.node = node;
        initComponents();
        init();
        intSetValue();
        progressBar.finish();
		progressBar.setVisible(false);
    }
    private void intSetValue() {
    	Element treeCellData = node.getObject().getTreeCellData();
    	jTextField1.setText(treeCellData.attributeValue("CMAT"));
//    	nameBox.setSelectedItem(treeCellData.attributeValue("PTC_MATERIAL_NAME"));
    	nameBox.setSelectedItem("132131");
    	csizeBox.setSelectedItem(treeCellData.attributeValue("PZGGZH"));
    	markNumberBox.setSelectedItem(treeCellData.attributeValue("XHPH"));
	}
	private void init() {
    	setTitle("原材料定额");
    	setHideColumn(jTable1, 16);
    	setHideColumn(jTable2, 19);
    	CommonUtil.setTableStyle(jTable1);
    	CommonUtil.setTableStyle(jTable2);
    	TableColumn tableColumn = jTable2.getColumn("*单位");
        tableColumn.setCellEditor(new DefaultCellEditor(CommonUtil.getDWJComboBox()));
        setMiddleOnScreenWithDialog(this);

        tableHeader = new String [] {
        		"序号", "编号", "名称", "物资简称", "材料类型", "换算率", "系数", "牌号", "规格", "采用标准", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object","供应状态","精度","质量特征","品种规格标准","供应商"
            };

        tableColWidth = new int[]{50,150,150,100,100,100,100,100,100,100,70,70,100,70,70,70,50,70,50,100,100,70};

        setMenuBar = new MyJMenuBar();
        //setJMenuBar(setMenuBar);

        try {
        	jComboBox1.addItem("全部");
			map = TechnicsIntf.getGlcataLog();
			if(map != null) {
				for(String name:map.keySet()) {
					List<String> list = (List<String>)map.get(name);
					if(list.contains("原材料") || list.contains("全部")) {
						jComboBox1.addItem(name);
					}
				}
			}

			List<String> list = TechnicsIntf.getTopGlClassificationNode();
			if(list != null) {
				vMap = new HashMap<String,String>();
				String tValue = "";
				String code = "";
				for(String value:list) {
					if(value.startsWith("03")
							|| value.startsWith("04")
							|| value.startsWith("05")
							|| value.equals("全部")) {
						//去掉代号加下划线,比如:02_标准件
						if(!value.equals("全部")) {
							if(value.indexOf("_")>-1) {
								tValue = value.substring(value.indexOf("_")+1, value.length());
								code = value.substring(0, value.indexOf("_"));
							}
							jComboBox2.addItem(tValue);
							vMap.put(tValue, code);
						} else {
							jComboBox2.addItem(value);
							vMap.put(value, value);
						}
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

        loadData();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

    	jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();

        try {
        	List<String> nameList = TechnicsIntf.getValues("NAME");
			nameBox = new SjzykJComboBox(nameList);

			List<String> markNumberList = TechnicsIntf.getValues("MARKNUMBER");
			markNumberBox = new SjzykJComboBox(markNumberList);

			List<String> csizeList = TechnicsIntf.getValues("CSIZE");
			csizeBox = new SjzykJComboBox(csizeList);

			List<String> useStandardList = TechnicsIntf.getValues("USESTANDARD");
			useStandardBox = new SjzykJComboBox(useStandardList);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jComboBox3 = new javax.swing.JComboBox();
        jComboBox4 = SjzykUtil.getDWJComboBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("原材料定额");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("查询条件"));

        jLabel1.setText("选用目录：");

//        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                selectGlcatalog(evt);
//            }
//        });

        jLabel2.setText("选择分类：");

        jLabel3.setText("编号：");

        jLabel4.setText("名称：");

        jLabel5.setText("物资简称：");

        jLabel6.setText("换算率：");

        jLabel7.setText("系数：");

        jLabel8.setText("牌号：");

        jLabel9.setText("规格：");

        jLabel10.setText("采用标准：");

        jLabel11.setText("特殊说明：");

        jLabel12.setText("是否进口：");

        jLabel13.setText("计量单位：");

        jButton1.setText("查  询");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                search(evt);
            }
        });

        jButton6.setText("清  空");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clear(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "否", "是" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(jTextField5)
                            .addComponent(jTextField9))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(0, 0, 0)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(5, 5, 5)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel9)
                                                .addComponent(jLabel13)))
                                        .addGap(0, 0, 0)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(csizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(nameBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(markNumberBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(0, 0, 0)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(useStandardBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(114, 114, 114)
                                .addComponent(jLabel2)
                                .addGap(0, 0, 0)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(123, 123, 123)
                                        .addComponent(jButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton6))
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(65, 65, 65)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(174, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField1, jTextField5, jTextField9});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nameBox, markNumberBox});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField4, useStandardBox});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jButton6))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(nameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(markNumberBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(csizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(useStandardBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("查询结果列表"));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "编号", "名称", "物资简称", "材料类型", "换算率", "系数", "牌号", "规格", "采用标准", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object","供应状态","精度","质量特征","品种规格标准","供应商"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class , java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            ,java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,false,false,false,false,false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                addSelected(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getColumn(0).setMinWidth(50);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(1).setMinWidth(100);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(2).setMinWidth(100);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(3).setMinWidth(100);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setMinWidth(50);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(5).setMinWidth(50);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setMinWidth(50);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(7).setMinWidth(50);
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(8).setMinWidth(50);
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(9).setMinWidth(50);
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(10).setMinWidth(50);
        jTable1.getColumnModel().getColumn(10).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(11).setMinWidth(50);
        jTable1.getColumnModel().getColumn(11).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(12).setMinWidth(50);
        jTable1.getColumnModel().getColumn(12).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(13).setMinWidth(50);
        jTable1.getColumnModel().getColumn(13).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(14).setMinWidth(50);
        jTable1.getColumnModel().getColumn(14).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(15).setMinWidth(50);
        jTable1.getColumnModel().getColumn(15).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(16).setMinWidth(50);
        jTable1.getColumnModel().getColumn(16).setPreferredWidth(50);

        jTable1.getColumnModel().getColumn(17).setMinWidth(70);
        jTable1.getColumnModel().getColumn(17).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(18).setMinWidth(50);
        jTable1.getColumnModel().getColumn(18).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(19).setMinWidth(70);
        jTable1.getColumnModel().getColumn(19).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(20).setMinWidth(100);
        jTable1.getColumnModel().getColumn(20).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(21).setMinWidth(70);
        jTable1.getColumnModel().getColumn(21).setPreferredWidth(70);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("选择结果列表"));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "编号", "名称", "物资简称", "*下料尺寸", "*可制件数", "*单位", "材料类型", "换算率", "系数"
                , "牌号", "规格", "采用标准", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object"
                ,"供应状态","精度","质量等级","品种规格标准"
                ,"供应商","备注"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                , java.lang.String.class, java.lang.String.class, java.lang.String.class,java.lang.String.class
                , java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                , java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
                , java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                , java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, true, true, false, false, false
                , false, false, false, false, false, false, false, false, false, false
                ,false, false, false, false,false,true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable2);
        jTable2.getColumnModel().getColumn(0).setMinWidth(50);
        jTable2.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable2.getColumnModel().getColumn(1).setMinWidth(100);
        jTable2.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTable2.getColumnModel().getColumn(2).setMinWidth(100);
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable2.getColumnModel().getColumn(3).setMinWidth(50);
        jTable2.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(4).setMinWidth(50);
        jTable2.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(5).setMinWidth(50);
        jTable2.getColumnModel().getColumn(5).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(6).setMinWidth(50);
        jTable2.getColumnModel().getColumn(6).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(7).setMinWidth(100);
        jTable2.getColumnModel().getColumn(7).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(8).setMinWidth(50);
        jTable2.getColumnModel().getColumn(8).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(9).setMinWidth(50);
        jTable2.getColumnModel().getColumn(9).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(10).setMinWidth(50);
        jTable2.getColumnModel().getColumn(10).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(11).setMinWidth(50);
        jTable2.getColumnModel().getColumn(11).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(12).setMinWidth(50);
        jTable2.getColumnModel().getColumn(12).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(13).setMinWidth(50);
        jTable2.getColumnModel().getColumn(13).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(14).setMinWidth(50);
        jTable2.getColumnModel().getColumn(14).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(15).setMinWidth(50);
        jTable2.getColumnModel().getColumn(15).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(16).setMinWidth(50);
        jTable2.getColumnModel().getColumn(16).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(17).setMinWidth(50);
        jTable2.getColumnModel().getColumn(17).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(18).setMinWidth(50);
        jTable2.getColumnModel().getColumn(18).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(19).setMinWidth(50);
        jTable2.getColumnModel().getColumn(19).setPreferredWidth(50);
        jTable2.getColumnModel().getColumn(20).setMinWidth(50);
        jTable2.getColumnModel().getColumn(20).setPreferredWidth(50);

        jTable2.getColumnModel().getColumn(21).setMinWidth(70);
        jTable2.getColumnModel().getColumn(21).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(22).setMinWidth(50);
        jTable2.getColumnModel().getColumn(22).setPreferredWidth(50);
        jTable2.getColumnModel().getColumn(23).setMinWidth(100);
        jTable2.getColumnModel().getColumn(23).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(24).setMinWidth(50);
        jTable2.getColumnModel().getColumn(24).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(25).setMinWidth(50);
        jTable2.getColumnModel().getColumn(25).setPreferredWidth(100);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
        );

        jButton2.setText("保  存");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save(evt);
            }
        });

        jButton3.setText("删除选择行");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSelected(evt);
            }
        });

        jButton4.setText("删除全部");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAll(evt);
            }
        });

        jButton5.setText("取  消");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(50, 50, 50)
                .addComponent(jButton3)
                .addGap(45, 45, 45)
                .addComponent(jButton4)
                .addGap(51, 51, 51)
                .addComponent(jButton5)
                .addGap(46, 46, 46))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void selectGlcatalog(ActionEvent evt) {
    	List<String> list = map.get(String.valueOf(jComboBox1.getSelectedItem()));
    	jComboBox2.removeAllItems();
    	if(list != null) {
    		for (String string : list) {
    			jComboBox2.addItem(string);
			}
    	}
    }

    private void search(ActionEvent evt) {
    	DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
		tableModel.setRowCount(0);
		jPanel3.setBorder(BorderFactory.createTitledBorder("查询结果列表:0"));
    	progressBar = new VaActionProgressBar(null,this, "原材料定额", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
				String xyml = String.valueOf(jComboBox1.getSelectedItem());
		    	String xzfl = String.valueOf(jComboBox2.getSelectedItem());
		    	String code = vMap.get(xzfl);
		    	String number = jTextField1.getText();//编号
		    	String name = String.valueOf(nameBox.getSelectedItem());//名称
		    	String wzjc = jTextField3.getText();//物资简称
		    	String hsl = jTextField4.getText();//换算率
		    	String xs = jTextField5.getText();//系数
		    	String ph = String.valueOf(markNumberBox.getSelectedItem());//牌号
		    	String gg = String.valueOf(csizeBox.getSelectedItem());//规格
		    	String cybz = String.valueOf(useStandardBox.getSelectedItem());//采用标准
		    	String tssm = jTextField9.getText();//特殊说明
		    	String sfjk = String.valueOf(jComboBox3.getSelectedItem());//是否进口
		    	String jldw = String.valueOf(jComboBox4.getSelectedItem());//计量单位

		    	Map<String,String> ibaMap = new HashMap<String,String>();
		    	if(wzjc != null && !"".equals(wzjc) && !"null".equals(wzjc)) {
		    		ibaMap.put("SHORTNAME", wzjc);
		    	}
		    	if(hsl != null && !"".equals(hsl) && !"null".equals(hsl)) {
		    		ibaMap.put("RATEOFCONVERSION", hsl);
		    	}
		    	if(gg != null && !"".equals(gg) && !"null".equals(gg)) {
		    		ibaMap.put("CSIZE", gg);
		    	}
		    	if(xs != null && !"".equals(xs) && !"null".equals(xs)) {
		    		ibaMap.put("RATIO", xs);
		    	}
		    	if(ph != null && !"".equals(ph) && !"null".equals(ph)) {
		    		ibaMap.put("MARKNUMBER", ph);
		    	}
		    	if(cybz != null && !"".equals(cybz) && !"null".equals(cybz)) {
		    		ibaMap.put("USESTANDARD", cybz);
		    	}
		    	if(tssm != null && !"".equals(tssm) && !"null".equals(tssm)) {
		    		ibaMap.put("SPECIALINSTRUCTION", tssm);
		    	}
		    	if(sfjk != null && !"".equals(sfjk) && !"null".equals(sfjk)) {
		    		if("是".equals(sfjk)) {
		    			sfjk = "Y";
		    		} else {
		    			sfjk = "N";
		    		}
		    		ibaMap.put("ISIMPORT", sfjk);
		    	}
		    	if(jldw != null && !"".equals(jldw) && !"null".equals(jldw)) {
		    		ibaMap.put("MEASUREUNIT", jldw);
		    	}

		    	//根据查询条件执行后台查询
		    	List<SjzykBean> list = null;
				try {
					String containerName = "";
					if(code.startsWith("03")) {
						containerName = "八院金属材料库";
					} else if (code.startsWith("04")) {
						containerName = "八院非金属材料库";
					} else if (code.startsWith("05")) {
						containerName = "八院复合材料库";
					}
					boolean flag = setMenuBar.isMiddleTable();
					System.out.println("is middle table:"+flag);
					if(flag) {
						progressBar.setHeaderMessage("系统后台正在通过中间表查询数据！");
						list = TechnicsIntf.queryData2("原材料", xyml, code, number, name, ibaMap, containerName);
					} else {
						progressBar.setHeaderMessage("系统后台正在通过多表查询数据！");
						list = TechnicsIntf.queryData("原材料", xyml, code, number, name, ibaMap, containerName);
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, "查询数据时出错，请联系管理员！");
					e.printStackTrace();
				}

		    	progressBar.setHeaderMessage("查询数据完成！");

		    	//将查询结果数据显示在查询结果表中
		    	processQueryResultData(list);

		    	progressBar.finish();
				progressBar.setVisible(false);
			}
		};
		thread.start();
		progressBar.setVisible(true);
    }

    private void processQueryResultData(List<SjzykBean> list) {
		if(list == null || list.isEmpty()) {
    		JOptionPane.showMessageDialog(this, "没有查询到符合条件数据！");
    		return ;
    	}

		progressBar.setHeaderMessage("数据加载！");

		DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
		List<String> numbers = new ArrayList<String>();

		//"序号", "编号", "名称", "物资简称", "材料类型", "换算率", "系数", "牌号", "规格",
		//"采用标准", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object"
		Object[][] tableBody = new Object[list.size()][tableHeader.length];
		SjzykBean sjzykBean = null;
		for(int i=0;i<list.size();i++) {
			sjzykBean = list.get(i);
			tableBody[i][0] = (i+1);
			tableBody[i][1] = sjzykBean.getSjbm();
			tableBody[i][2] = sjzykBean.getName();
			tableBody[i][3] = sjzykBean.getWzjc();
			tableBody[i][4] = sjzykBean.getCllx();
			tableBody[i][5] = sjzykBean.getHsl();
			tableBody[i][6] = sjzykBean.getXs();
			tableBody[i][7] = sjzykBean.getPh();
			tableBody[i][8] = sjzykBean.getGg();
			tableBody[i][9] = sjzykBean.getCybz();
			tableBody[i][10] = sjzykBean.getSfjk();
			tableBody[i][11] = sjzykBean.getJldw();
			tableBody[i][12] = sjzykBean.getTssm();
			tableBody[i][13] = sjzykBean.getBmyyjb();
			tableBody[i][14] = sjzykBean.getBmlx();
			tableBody[i][15] = sjzykBean.getBmzt();
			tableBody[i][16] = sjzykBean;
			tableBody[i][17] = sjzykBean.getGyzt();
			tableBody[i][18] = sjzykBean.getJd();
			tableBody[i][19] = sjzykBean.getZltz();
			tableBody[i][20] = sjzykBean.getPzggbz();
			tableBody[i][21] = sjzykBean.getGys();
		}

		tableModel.setDataVector(tableBody, tableHeader);
		for (int i = 1; i < tableHeader.length; i++) {
			jTable1.getColumn(tableHeader[i]).setPreferredWidth(tableColWidth[i]);
		}
		setHideColumn(jTable1, 16);

		CommonUtil.setTableStyle(jTable1);

//		Vector<String> vector = null;
//    	for (SjzykBean sjzykBean : list) {
//    		//过滤重复的数据
//    		if(numbers.contains(sjzykBean.getSjbm())) {
//    			continue;
//    		}
//    		numbers.add(sjzykBean.getSjbm());
//    		//新增一行
//			vector = new Vector<String>();
//			for (int i = 0; i < tableModel.getColumnCount(); i++) {
//				vector.add("");
//			}
//			tableModel.addRow(vector);
//			//写入数据
//			//"序号", "编号", "名称", "物资简称", "材料类型", "换算率", "系数", "牌号", "规格",
//			//"采用标准", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object"
//			int rows = jTable1.getRowCount();
//			int n = rows - 1;
//			tableModel.setValueAt(rows, n, 0);
//			tableModel.setValueAt(sjzykBean.getSjbm(), n, 1);
//			tableModel.setValueAt(sjzykBean.getName(), n, 2);
//			tableModel.setValueAt(sjzykBean.getWzjc(), n, 3);
//			tableModel.setValueAt(sjzykBean.getCllx(), n, 4);
//			tableModel.setValueAt(sjzykBean.getHsl(), n, 5);
//			tableModel.setValueAt(sjzykBean.getXs(), n, 6);
//			tableModel.setValueAt(sjzykBean.getPh(), n, 7);
//			tableModel.setValueAt(sjzykBean.getGg(), n, 8);
//			tableModel.setValueAt(sjzykBean.getCybz(), n, 9);
//			tableModel.setValueAt(sjzykBean.getSfjk(), n, 10);
//			tableModel.setValueAt(sjzykBean.getJldw(), n, 11);
//			tableModel.setValueAt(sjzykBean.getTssm(), n, 12);
//			tableModel.setValueAt(sjzykBean.getBmyyjb(), n, 13);
//			tableModel.setValueAt(sjzykBean.getBmlx(), n, 14);
//			tableModel.setValueAt(sjzykBean.getBmzt(), n, 15);
//			tableModel.setValueAt(sjzykBean, n, 16);
//		}

    	jPanel3.setBorder(BorderFactory.createTitledBorder("查询结果列表:"+list.size()));

    	progressBar.setHeaderMessage("数据加载完成！");
    }

    private void save(ActionEvent evt) {
    	DefaultTableModel tableModel = (DefaultTableModel) jTable2.getModel();
    	if(!CommonUtil.checkValueIsNull(tableModel, new int[]{4,5,6})) {
    		JOptionPane.showMessageDialog(this, "下料尺寸、可制件数、单位不能为空！");
			return;
    	}
    	if(!CommonUtil.checkIsNumeric(tableModel, new int[]{5})) {
    		JOptionPane.showMessageDialog(this, "可制件数只能填写0-9的数字！！");
			return;
    	}

    	int flag = JOptionPane.showConfirmDialog(this, "确定保存吗？","确认", JOptionPane.OK_CANCEL_OPTION);
		if(flag==JOptionPane.YES_OPTION){
			if (node != null) {
				XWTreeObject xo = node.getObject();
				if ((xo instanceof XWTechnicsTreeObject)) {
					Element element = xo.getTreeCellData();
					Element clde = XmlUtility.getTechnicsCLDEElement(element);
					Element yclde = XmlUtility.getChildElements(clde, "SJZYKYCLDE");
					if(yclde == null) {
						yclde = DocumentHelper.createElement("SJZYKYCLDE");
					} else {
						XmlUtility.deleteAllChildElements(yclde);
					}
					SjzykBean bean = null;
					for(int i=0;i<jTable2.getRowCount();i++){
						bean = (SjzykBean)tableModel.getValueAt(i, 19);
						bean.setXlcc(String.valueOf(tableModel.getValueAt(i, 4)));
						bean.setKzjs(String.valueOf(tableModel.getValueAt(i, 5)));
						bean.setDw(String.valueOf(tableModel.getValueAt(i, 6)));
						bean.setGys(String.valueOf(tableModel.getValueAt(i, 24)));
						bean.setComment(String.valueOf(tableModel.getValueAt(i, 25)));
						Element newPartEle = CommonUtil.createPartElement(i, bean, "ycldeRecord");
						yclde.add(newPartEle);
					}

					String techPath = WorkSpaceUtil.getTechnicsDirectory(element.attributeValue("technicsNumber"));
					String xmlFilePath = techPath + File.separator + element.attributeValue("technicsNumber")+".xml";
					System.out.println("---------xmlFilePath-----"+xmlFilePath);
					try {
						//clde.add(yclde);
						XmlUtility.saveDocument(element.getDocument(), xmlFilePath);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(element);
					if (!frame.editTechnics.contains(technics)) {
						frame.editTechnics.add(technics);
					}
				}
			}
			this.setVisible(false);
		}
    }

    private void loadData() {
    	if (node != null) {
			XWTreeObject xo = node.getObject();
			if ((xo instanceof XWTechnicsTreeObject)) {
				Element element = xo.getTreeCellData();
				Element clde = XmlUtility.getTechnicsCLDEElement(element);
				if(clde == null) {
					return ;
				}
				List<SjzykBean> beans = new ArrayList<SjzykBean>();
				List<Element> list = XmlUtility.getTechnicsSJZYKYCLDE(clde);
				if(list != null && !list.isEmpty()) {
					for (Element ele : list) {
						beans.add(CommonUtil.creatSjzykBean(ele));
					}
				}

				for (SjzykBean bean : beans) {
					setRowValues(bean);
				}
			}
		}
    }

    private void removeSelected(ActionEvent evt) {
    	int sels = jTable2.getSelectedRows().length;
    	if(sels  == 0) {
    		JOptionPane.showMessageDialog(this, "请选择需要移除的数据！");
    	} else {
    		DefaultTableModel tableModel = (DefaultTableModel) jTable2.getModel();
    		for (int i = 0; i < sels; i++) {
    			tableModel.removeRow(jTable2.getSelectedRow());
    		}
    	}
    }

    private void removeAll(ActionEvent evt) {
    	int rows = jTable2.getRowCount();
    	if(rows == 0) {
    		JOptionPane.showMessageDialog(this, "选择结果列表中没有数据！");
    	} else {
    		DefaultTableModel tableModel = (DefaultTableModel) jTable2.getModel();
    		tableModel.setRowCount(0);
    	}
    }

    private void close(ActionEvent evt) {
        this.setVisible(false);
    }

    private void addSelected(MouseEvent evt) {
    	int b1 = evt.getButton();
    	if(MouseEvent.BUTTON1 == b1 ) {
    		if(evt.getClickCount() == 2) {
    			int selr = jTable1.getSelectedRow();
    			if(selr == -1) {
    				JOptionPane.showMessageDialog(this, "请选择查询结果列表中的数据！");
    				return ;
    			}

    			//判断是否已经被选用
    			if(isAdded()) {
    				JOptionPane.showMessageDialog(this, "这条数据已经被选用，不能重复选择！");
    				return ;
    			}

    			SjzykBean bean = (SjzykBean)jTable1.getModel().getValueAt(selr, 16);

    			if(!"启用".equals(bean.getBmzt())) {
    				JOptionPane.showMessageDialog(this, "该编码状态不是处于启用状态，不能使用！");
    				return ;
    			}

    			setRowValues(bean);
    		}
    	}
    }

    private void setRowValues(SjzykBean bean) {
    	//新增一行
		DefaultTableModel tableModel = (DefaultTableModel)jTable2.getModel();
		Vector<String> vector = new Vector<String>();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);
		//写入数据
		//"序号", "编号", "名称", "物资简称", "*下料尺寸", "*可制件数", "*单位", "材料类型", "换算率",
		//"系数", "牌号", "规格", "采用标准", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object"
		int rows = jTable2.getRowCount();
		int n = rows - 1;
		tableModel.setValueAt(rows, n, 0);
		tableModel.setValueAt(bean.getSjbm(), n, 1);
		tableModel.setValueAt(bean.getName(), n, 2);
		tableModel.setValueAt(bean.getWzjc(), n, 3);
		tableModel.setValueAt(bean.getXlcc(), n, 4);
		tableModel.setValueAt(bean.getKzjs(), n, 5);
		tableModel.setValueAt(bean.getDw(), n, 6);
		tableModel.setValueAt(bean.getCllx(), n, 7);
		tableModel.setValueAt(bean.getHsl(), n, 8);
		tableModel.setValueAt(bean.getXs(), n, 9);
		tableModel.setValueAt(bean.getPh(), n, 10);
		tableModel.setValueAt(bean.getGg(), n, 11);
		tableModel.setValueAt(bean.getCybz(), n, 12);
		tableModel.setValueAt(bean.getSfjk(), n, 13);
		tableModel.setValueAt(bean.getJldw(), n, 14);
		tableModel.setValueAt(bean.getTssm(), n, 15);
		tableModel.setValueAt(bean.getBmyyjb(), n, 16);
		tableModel.setValueAt(bean.getBmlx(), n, 17);
		tableModel.setValueAt(bean.getBmzt(), n, 18);
		tableModel.setValueAt(bean, n, 19);

		tableModel.setValueAt(bean.getGyzt(), n, 20);
		tableModel.setValueAt(bean.getJd(), n, 21);
		tableModel.setValueAt(bean.getZltz(), n, 22);
		tableModel.setValueAt(bean.getPzggbz(), n, 23);
		tableModel.setValueAt(bean.getGys(), n, 24);
		tableModel.setValueAt(bean.getComment(), n, 25);
    }

    private boolean isAdded() {
    	String number = String.valueOf(jTable1.getModel().getValueAt(jTable1.getSelectedRow(), 1));
    	int rows = jTable2.getRowCount();
    	if(rows > 0) {
    		for (int i = 0 ; i < rows ; i++) {
				if(number.equals(String.valueOf(jTable2.getModel().getValueAt(i, 1)))) {
					return true;
				}
			}
    	}
    	return false;
    }

    private void clear(ActionEvent evt) {
    	jTextField1.setText("");
    	//nameBox.setText("");
    	jTextField3.setText("");
    	jTextField4.setText("");
    	jTextField5.setText("");
    	//markNumberBox.setText("");
    	//csizeBox.setText("");
    	//useStandardBox.setText("");
    	jTextField9.setText("");
    }

    private void setHideColumn(JTable table, int index) {
		table.getColumnModel().getColumn(index).setMinWidth(0);
		table.getColumnModel().getColumn(index).setMaxWidth(0);
	}

    public static void setMiddleOnScreenWithDialog(JDialog dialog){
		int windowWidth = dialog.getWidth();                    //获得窗口宽
	    int windowHeight = dialog.getHeight();                  //获得窗口高
	    Toolkit kit = Toolkit.getDefaultToolkit();              //定义工具包
	    Dimension screenSize = kit.getScreenSize();             //获取屏幕的尺寸
	    int screenWidth = screenSize.width;                     //获取屏幕的宽
	    int screenHeight = screenSize.height;                   //获取屏幕的高
	    dialog.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);//设置窗口居中显示
	}
}
