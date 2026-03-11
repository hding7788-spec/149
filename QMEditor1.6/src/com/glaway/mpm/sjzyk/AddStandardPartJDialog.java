package com.glaway.mpm.sjzyk;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
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

import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class AddStandardPartJDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JDialog pdialog;
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
    private JComboBox jComboBox3;
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
    private JTextField jTextField10;
    private SjzykJComboBox nameBox;
    private JTextField jTextField3;
    private SjzykJComboBox bzhBox;
    private SjzykJComboBox csizeBox;
    private JTextField jTextField6;
    private JTextField jTextField7;
    private JTextField jTextField8;
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
    public AddStandardPartJDialog(NewTechnicsPart frame, XWTreeNode node, JDialog pdialog, boolean modal,VaActionProgressBar progressBar) {
        super(frame, modal);
        this.frame = frame;
        this.node = node;
        this.pdialog = pdialog;
        initComponents();
        init();

        progressBar.finish();
		progressBar.setVisible(false);
    }

    private void init() {
    	setTitle("从设计资源库查询添加标准件");
    	setHideColumn(jTable1, 19);
    	setHideColumn(jTable2, 22);
    	CommonUtil.setTableStyle(jTable1);
    	CommonUtil.setTableStyle(jTable2);
    	TableColumn tableColumn = jTable2.getColumn("*单位");
        tableColumn.setCellEditor(new DefaultCellEditor(CommonUtil.getDWJComboBox()));
        setMiddleOnScreenWithDialog(this);

        tableHeader = new String [] {
                "序号", "编号", "名称", "物资简称", "标准号", "规格", "材料", "机械性能等级或硬度", "表面处理", "热处理"
                , "产品形式", "产品等级", "扳拧形式", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object"
                ,"供应商"
            };

        tableColWidth = new int[]{50,150,150,100,150,100,100,130,100,100,100,100,100,70,70,100,70,70,70,50,70};

        setMenuBar = new MyJMenuBar();
        //setJMenuBar(setMenuBar);

        try {
        	jComboBox1.addItem("全部");
			map = TechnicsIntf.getGlcataLog();
			if(map != null) {
				for(String name:map.keySet()) {
					List<String> list = (List<String>)map.get(name);
					if(list.contains("标准件") || list.contains("全部")) {
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
					if(value.startsWith("02")) {
						if(value.indexOf("_")>-1) {
							tValue = value.substring(value.indexOf("_")+1, value.length());
							code = value.substring(0, value.indexOf("_"));
						}
						jComboBox2.addItem(tValue);
						vMap.put(tValue, code);
					} else if (value.equals("全部")) {
						jComboBox2.addItem(value);
						vMap.put(value, value);
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

        try {
        	List<String> nameList = TechnicsIntf.getValues("NAME");
        	nameBox = new SjzykJComboBox(nameList);

			List<String> bzhList = TechnicsIntf.getValues("STANDARDNUMBER");
			bzhBox = new SjzykJComboBox(bzhList);

			List<String> csizeList = TechnicsIntf.getValues("CSIZE");
			csizeBox = new SjzykJComboBox(csizeList);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jComboBox3 = new javax.swing.JComboBox();
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
        setTitle("从设计资源库查询添加标准件");

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

        jLabel6.setText("标准号：");

        jLabel7.setText("规格：");

        jLabel8.setText("材料：");

        jLabel9.setText("产品形式：");

        jLabel10.setText("产品等级：");

        jLabel11.setText("扳拧形式：");

        jLabel12.setText("特殊说明：");

        jLabel13.setText("是否进口：");

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
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(csizeBox)
                            .addComponent(jTextField9))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nameBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(0, 0, 0)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bzhBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addGap(0, 0, 0)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton6)))
                .addGap(119, 119, 119))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField1, csizeBox, jTextField9});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField10, nameBox, jTextField6});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {bzhBox, jTextField8});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
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
                    .addComponent(bzhBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(csizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("查询结果列表"));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "编号", "名称", "物资简称", "标准号"
                , "规格", "材料", "机械性能等级或硬度", "表面处理", "热处理"
                , "产品形式", "产品等级", "扳拧形式", "是否进口", "计量单位"
                , "特殊说明", "优选级别", "编码类型", "编码状态", "object"
                ,"供应商"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                , java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                , java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                , java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
                , java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
                , false, false, false, false, false
                , false, false, false, false, false
                , false, false, false, false, false
                ,false
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
        jTable1.getColumnModel().getColumn(3).setMinWidth(50);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(4).setMinWidth(100);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(5).setMinWidth(50);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setMinWidth(50);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(7).setMinWidth(50);
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(130);
        jTable1.getColumnModel().getColumn(8).setMinWidth(50);
        jTable1.getColumnModel().getColumn(8).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(9).setMinWidth(50);
        jTable1.getColumnModel().getColumn(9).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(10).setMinWidth(50);
        jTable1.getColumnModel().getColumn(10).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(11).setMinWidth(50);
        jTable1.getColumnModel().getColumn(11).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(12).setMinWidth(50);
        jTable1.getColumnModel().getColumn(12).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(13).setMinWidth(50);
        jTable1.getColumnModel().getColumn(13).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(14).setMinWidth(50);
        jTable1.getColumnModel().getColumn(14).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(15).setMinWidth(50);
        jTable1.getColumnModel().getColumn(15).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(16).setMinWidth(50);
        jTable1.getColumnModel().getColumn(16).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(17).setMinWidth(50);
        jTable1.getColumnModel().getColumn(17).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(18).setMinWidth(50);
        jTable1.getColumnModel().getColumn(18).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(19).setMinWidth(50);
        jTable1.getColumnModel().getColumn(19).setPreferredWidth(50);
        jTable1.getColumnModel().getColumn(20).setMinWidth(50);
        jTable1.getColumnModel().getColumn(20).setPreferredWidth(50);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("选择结果列表"));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "上级图号", "编号", "名称", "物资简称"
                , "*工艺数量", "*单位", "标准号", "规格", "材料"
                , "机械性能等级及硬度", "表面处理", "热处理", "产品形式",
                "产品等级"
                , "扳拧形式", "是否进口", "计量单位", "特殊说明",
                "优选级别"
                , "编码类型", "编码状态", "object","供应商","备注"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                , java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                , java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                , java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
                , java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
                , true, true, false, false, false
                , false, false, false, false, false
                , false, false, false, false, false
                , false, false, false, false,true
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
        jTable2.getColumnModel().getColumn(1).setMinWidth(50);
        jTable2.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTable2.getColumnModel().getColumn(2).setMinWidth(100);
        jTable2.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable2.getColumnModel().getColumn(3).setMinWidth(100);
        jTable2.getColumnModel().getColumn(3).setPreferredWidth(150);
        jTable2.getColumnModel().getColumn(4).setMinWidth(50);
        jTable2.getColumnModel().getColumn(4).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(5).setMinWidth(50);
        jTable2.getColumnModel().getColumn(5).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(6).setMinWidth(50);
        jTable2.getColumnModel().getColumn(6).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(7).setMinWidth(100);
        jTable2.getColumnModel().getColumn(7).setPreferredWidth(150);
        jTable2.getColumnModel().getColumn(8).setMinWidth(50);
        jTable2.getColumnModel().getColumn(8).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(9).setMinWidth(50);
        jTable2.getColumnModel().getColumn(9).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(10).setMinWidth(50);
        jTable2.getColumnModel().getColumn(10).setPreferredWidth(130);
        jTable2.getColumnModel().getColumn(11).setMinWidth(50);
        jTable2.getColumnModel().getColumn(11).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(12).setMinWidth(50);
        jTable2.getColumnModel().getColumn(12).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(13).setMinWidth(50);
        jTable2.getColumnModel().getColumn(13).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(14).setMinWidth(50);
        jTable2.getColumnModel().getColumn(14).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(15).setMinWidth(50);
        jTable2.getColumnModel().getColumn(15).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(16).setMinWidth(50);
        jTable2.getColumnModel().getColumn(16).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(17).setMinWidth(50);
        jTable2.getColumnModel().getColumn(17).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(18).setMinWidth(50);
        jTable2.getColumnModel().getColumn(18).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(19).setMinWidth(50);
        jTable2.getColumnModel().getColumn(19).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(20).setMinWidth(50);
        jTable2.getColumnModel().getColumn(20).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(21).setMinWidth(50);
        jTable2.getColumnModel().getColumn(21).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(22).setMinWidth(50);
        jTable2.getColumnModel().getColumn(22).setPreferredWidth(50);
        jTable2.getColumnModel().getColumn(23).setMinWidth(50);
        jTable2.getColumnModel().getColumn(23).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(24).setMinWidth(50);
        jTable2.getColumnModel().getColumn(24).setPreferredWidth(100);

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

        jButton2.setText("添  加");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                add(evt);
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
    	progressBar = new VaActionProgressBar(null,this, "查询添加标准件", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
		    	String xyml = String.valueOf(jComboBox1.getSelectedItem());
		    	String xzfl = String.valueOf(jComboBox2.getSelectedItem());
		    	String code = vMap.get(xzfl);
		    	String number = jTextField1.getText();//编号
		    	String name = String.valueOf(nameBox.getSelectedItem());//名称
		    	String wzjc = jTextField3.getText();//物资简称
		    	String bzh = String.valueOf(bzhBox.getSelectedItem());//标准号
		    	String gg = String.valueOf(csizeBox.getSelectedItem());//规格
		    	String cl = jTextField6.getText();//材料
		    	String cpxs = jTextField7.getText();//产品形式
		    	String cpdj = jTextField8.getText();//产品等级
		    	String bnxs = jTextField9.getText();//扳拧形式
		    	String tssm = jTextField10.getText();//特殊说明
		    	String sfjk = String.valueOf(jComboBox3.getSelectedItem());//是否进口

		    	Map<String,String> ibaMap = new HashMap<String,String>();
		    	if(wzjc != null && !"".equals(wzjc) && !"null".equals(wzjc)) {
		    		ibaMap.put("SHORTNAME", wzjc);
		    	}
		    	if(bzh != null && !"".equals(bzh) && !"null".equals(bzh)) {
		    		ibaMap.put("STANDARDNUMBER", bzh);
		    	}
		    	if(gg != null && !"".equals(gg) && !"null".equals(gg)) {
		    		ibaMap.put("CSIZE", gg);
		    	}
		    	if(cl != null && !"".equals(cl) && !"null".equals(cl)) {
		    		ibaMap.put("CMAT", cl);
		    	}
		    	if(cpxs != null && !"".equals(cpxs) && !"null".equals(cpxs)) {
		    		ibaMap.put("PRODUCTFORM", cpxs);
		    	}
		    	if(cpdj != null && !"".equals(cpdj) && !"null".equals(cpdj)) {
		    		ibaMap.put("PRODUCTLEVEL", cpdj);
		    	}
		    	if(bnxs != null && !"".equals(bnxs) && !"null".equals(bnxs)) {
		    		ibaMap.put("PLATECSCREWFORM", bnxs);
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

		    	//根据查询条件执行后台查询
		    	List<SjzykBean> list = null;
				try {
					boolean flag = setMenuBar.isMiddleTable();
					System.out.println("is middle table:"+flag);
					if(flag) {
						progressBar.setHeaderMessage("系统后台正在通过中间表查询数据！");
						list = TechnicsIntf.queryData2("标准件",xyml, code, number, name, ibaMap, "八院标准紧固件库");
					} else {
						progressBar.setHeaderMessage("系统后台正在通过多表查询数据！");
						list = TechnicsIntf.queryData("标准件",xyml, code, number, name, ibaMap, "八院标准紧固件库");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(frame, "查询数据时出错，请联系管理员！");
					e.printStackTrace();
				}

		    	progressBar.setHeaderMessage("查询数据完成！");

		    	long start = System.currentTimeMillis();
		    	//将查询结果写入查询结果表
		    	processQueryResultData(list);
		    	long end = System.currentTimeMillis();
		    	System.out.println("process data times:"+(end-start));

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
    	//"编号", "名称", "物资简称", "标准号", "规格", "材料", "机械性能等级或硬度", "表面处理", "热处理",
		//"产品形式", "产品等级", "扳拧形式", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object"

		Object[][] tableBody = new Object[list.size()][tableHeader.length];
		SjzykBean sjzykBean = null;
		for(int i=0;i<list.size();i++) {
			sjzykBean = list.get(i);
			tableBody[i][0] = (i+1);
			tableBody[i][1] = sjzykBean.getSjbm();
			tableBody[i][2] = sjzykBean.getName();
			tableBody[i][3] = sjzykBean.getWzjc();
			tableBody[i][4] = sjzykBean.getBzh();
			tableBody[i][5] = sjzykBean.getGg();
			tableBody[i][6] = sjzykBean.getCl();
			tableBody[i][7] = sjzykBean.getJxxndjhyd();
			tableBody[i][8] = sjzykBean.getBmcl();
			tableBody[i][9] = sjzykBean.getRcl();
			tableBody[i][10] = sjzykBean.getCpxs();
			tableBody[i][11] = sjzykBean.getCpdj();
			tableBody[i][12] = sjzykBean.getBnxs();
			tableBody[i][13] = sjzykBean.getSfjk();
			tableBody[i][14] = sjzykBean.getJldw();
			tableBody[i][15] = sjzykBean.getTssm();
			tableBody[i][16] = sjzykBean.getBmyyjb();
			tableBody[i][17] = sjzykBean.getBmlx();
			tableBody[i][18] = sjzykBean.getBmzt();
			tableBody[i][19] = sjzykBean;
			tableBody[i][20] = sjzykBean.getGys();
		}

		tableModel.setDataVector(tableBody, tableHeader);
		for (int i = 1; i < tableHeader.length; i++) {
			jTable1.getColumn(tableHeader[i]).setPreferredWidth(tableColWidth[i]);
		}
		setHideColumn(jTable1, 19);

		CommonUtil.setTableStyle(jTable1);

//		Vector<String> vector = null;
//    	for (SjzykBean sjzykBean : list) {
//    		//过滤重复的数据
////    		if(numbers.contains(sjzykBean.getSjbm())) {
////    			continue;
////    		}
////    		numbers.add(sjzykBean.getSjbm());
//    		//新增一行
//			vector = new Vector<String>();
//			for (int i = 0; i < tableModel.getColumnCount(); i++) {
//				vector.add("");
//			}
//			tableModel.addRow(vector);
//			//写入数据
//			int rows = jTable1.getRowCount();
//			int n = rows - 1;
//			tableModel.setValueAt(rows, n, 0);
//			tableModel.setValueAt(sjzykBean.getSjbm(), n, 1);
//			tableModel.setValueAt(sjzykBean.getName(), n, 2);
//			tableModel.setValueAt(sjzykBean.getWzjc(), n, 3);
//			tableModel.setValueAt(sjzykBean.getBzh(), n, 4);
//			tableModel.setValueAt(sjzykBean.getGg(), n, 5);
//			tableModel.setValueAt(sjzykBean.getCl(), n, 6);
//			tableModel.setValueAt(sjzykBean.getJxxndjhyd(), n, 7);
//			tableModel.setValueAt(sjzykBean.getBmcl(), n, 8);
//			tableModel.setValueAt(sjzykBean.getRcl(), n, 9);
//			tableModel.setValueAt(sjzykBean.getCpxs(), n, 10);
//			tableModel.setValueAt(sjzykBean.getCpdj(), n, 11);
//			tableModel.setValueAt(sjzykBean.getBnxs(), n, 12);
//			tableModel.setValueAt(sjzykBean.getSfjk(), n, 13);
//			tableModel.setValueAt(sjzykBean.getJldw(), n, 14);
//			tableModel.setValueAt(sjzykBean.getTssm(), n, 15);
//			tableModel.setValueAt(sjzykBean.getBmyyjb(), n, 16);
//			tableModel.setValueAt(sjzykBean.getBmlx(), n, 17);
//			tableModel.setValueAt(sjzykBean.getBmzt(), n, 18);
//			tableModel.setValueAt(sjzykBean, n, 19);
//		}

    	jPanel3.setBorder(BorderFactory.createTitledBorder("查询结果列表:"+list.size()));

    	progressBar.setHeaderMessage("数据加载完成！");
    }
    public static String objectToString(Object obj) {
        if (null == obj || "".equals(obj)) {
            return "";
        } else {
            return String.valueOf(obj);
        }
    }
    private void add(ActionEvent evt) {
    	int rows = jTable2.getRowCount();
    	if(rows < 1) {
    		JOptionPane.showMessageDialog(this, "没有需要添加的数据！");
    		return ;
    	}
    	DefaultTableModel tableModel = (DefaultTableModel) jTable2.getModel();
    	if(!CommonUtil.checkValueIsNull(tableModel, new int[]{5,6})) {
    		JOptionPane.showMessageDialog(this, "工艺数量、单位不能为空！");
			return;
    	}
//    	if(!CommonUtil.checkIsNumeric(tableModel, new int[]{5})) {
//    		JOptionPane.showMessageDialog(this, "工艺数量只能填写0-9的数字！！");
//			return;
//    	}

        for (int i = 0; i < rows; i++) {
            String num = objectToString(tableModel.getValueAt(i, 5));
            String unit = objectToString(tableModel.getValueAt(i, 6));
            if(!CommonUtil.checkGysl(num,unit)){
                JOptionPane.showMessageDialog(this, "第" + (i+1) + "行工艺数量填写不规范：\r\n单位为“个”，“只”，“件”的数量只能为整数，其他的只能为最多三位小数");
                return;
            }
        }

    	List<Object> list = new ArrayList<Object>();
    	SjzykBean bean = null;
    	for(int i=0 ; i<rows; i++) {
    		bean = (SjzykBean)tableModel.getValueAt(i, 22);
    		bean.setGysl(String.valueOf(tableModel.getValueAt(i, 5)));
    		bean.setDw(String.valueOf(tableModel.getValueAt(i, 6)));
    		bean.setGys(String.valueOf(tableModel.getValueAt(i, 23)));
    		bean.setComment(String.valueOf(tableModel.getValueAt(i, 24)));
    		bean.setDataType("标准件");
    		list.add(bean);
    	}

    	((ShowNewPartJDialog)pdialog).setValues(list,"BZJ");

    	this.setVisible(false);
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

    private void clear(ActionEvent evt) {
    	jTextField1.setText("");
    	//nameBox.setText("");
    	jTextField3.setText("");
    	//bzhBox.setText("");
    	//csizeBox.setText("");
    	jTextField6.setText("");
    	jTextField7.setText("");
    	jTextField8.setText("");
    	jTextField9.setText("");
    	jTextField10.setText("");
    }

    private void addSelected(MouseEvent evt) {
    	int b1 = evt.getButton();
    	if(MouseEvent.BUTTON1 == b1 ) {
    		if(evt.getClickCount() == 2) {
    			int selr = jTable1.getSelectedRow();
    			if(selr < 0) {
    				JOptionPane.showMessageDialog(this, "请选择查询结果列表中的数据！");
    				return ;
    			}

    			//判断是否已经被选用
    			if(isAdded()) {
    				JOptionPane.showMessageDialog(this, "这条数据已经被选用，不能重复选择！");
    				return ;
    			}

    			SjzykBean bean = (SjzykBean)jTable1.getModel().getValueAt(selr, 19);

    			if(!"启用".equals(bean.getBmzt())) {
    				JOptionPane.showMessageDialog(this, "该编码状态不是处于启用状态，不能使用！");
    				return ;
    			}

    			node = frame.technicsTreePanel.getSelectedTreeNode();
    			String parentPartNumber = String.valueOf(node.getObject().getTreeCellData().attributeValue("partNumber"));
    			bean.setParentPartNumber(parentPartNumber);
    			String partNumber=String.valueOf(node.getObject().getTreeCellData().attributeValue("partNumber"));
    			bean.setPartNumber(partNumber);
    			setRowValues(bean);
    		}
    	}
    }

    private void loadData() {
    	DefaultTableModel tableModel = (DefaultTableModel)jTable2.getModel();
    	tableModel.setRowCount(0);
    	List<SjzykBean> list = ((ShowNewPartJDialog)pdialog).getDataByDataType("BZJ");
    	for (SjzykBean bean : list) {
    		setRowValues(bean);
		}
    }

    private void setRowValues(SjzykBean bean) {
    	//新增一行
		DefaultTableModel tableModel = (DefaultTableModel)jTable2.getModel();
		Vector<String> vector = new Vector<String>();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
//		tableModel.addRow(vector);
		int rowCount = tableModel.getRowCount();
		if(rowCount > 0){
			tableModel.insertRow(0, vector);
		}else{
			tableModel.addRow(vector);
		}
		//写入数据
		//"序号", "上级图号", "编号", "名称", "物资简称", "*工艺数量", "*单位", "标准号", "规格", "材料", "机械性能等级及硬度",
		//"表面处理", "热处理", "产品形式", "产品等级", "扳拧形式", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object"
		int rows = jTable2.getRowCount();
//		int n = rows - 1;
		tableModel.setValueAt(rows, 0, 0);
		tableModel.setValueAt(bean.getPartNumber(), 0, 1);
		tableModel.setValueAt(bean.getSjbm(), 0, 2);
		tableModel.setValueAt(bean.getName(), 0, 3);
		tableModel.setValueAt(bean.getWzjc(), 0, 4);
		if(bean.getGysl() != null) {
			tableModel.setValueAt(bean.getGysl(), 0, 5);
			tableModel.setValueAt(bean.getDw(), 0, 6);
		}
		tableModel.setValueAt(bean.getBzh(), 0, 7);
		tableModel.setValueAt(bean.getGg(), 0, 8);
		tableModel.setValueAt(bean.getCl(), 0, 9);
		tableModel.setValueAt(bean.getJxxndjhyd(), 0, 10);
		tableModel.setValueAt(bean.getBmcl(), 0, 11);
		tableModel.setValueAt(bean.getRcl(), 0, 12);
		tableModel.setValueAt(bean.getCpxs(), 0, 13);
		tableModel.setValueAt(bean.getCpdj(), 0, 14);
		tableModel.setValueAt(bean.getBnxs(), 0, 15);
		tableModel.setValueAt(bean.getSfjk(), 0, 16);
		tableModel.setValueAt(bean.getJldw(), 0, 17);
		tableModel.setValueAt(bean.getTssm(), 0, 18);
		tableModel.setValueAt(bean.getBmyyjb(), 0, 19);
		tableModel.setValueAt(bean.getBmlx(), 0, 20);
		tableModel.setValueAt(bean.getBmzt(), 0, 21);
		tableModel.setValueAt(bean, 0, 22);
		tableModel.setValueAt(bean.getGys(), 0, 23);
    }

    private boolean isAdded() {
    	String number = String.valueOf(jTable1.getModel().getValueAt(jTable1.getSelectedRow(), 1));
    	int rows = jTable2.getRowCount();
    	if(rows > 0) {
    		for (int i = 0 ; i < rows ; i++) {
				if(number.equals(String.valueOf(jTable2.getModel().getValueAt(i, 2)))) {
					return true;
				}
			}
    	}
    	return false;
    }

    private void setHideColumn(JTable table, int index) {
		table.getColumnModel().getColumn(index).setMinWidth(0);
		table.getColumnModel().getColumn(index).setMaxWidth(0);
	}

    public static void setMiddleOnScreenWithDialog(JDialog dialog){
		int windowWidth = dialog.getWidth();                     //获得窗口宽
	    int windowHeight = dialog.getHeight();                   //获得窗口高
	    Toolkit kit = Toolkit.getDefaultToolkit();              //定义工具包
	    Dimension screenSize = kit.getScreenSize();             //获取屏幕的尺寸
	    int screenWidth = screenSize.width;                     //获取屏幕的宽
	    int screenHeight = screenSize.height;                   //获取屏幕的高
	    dialog.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);//设置窗口居中显示
	}
}
