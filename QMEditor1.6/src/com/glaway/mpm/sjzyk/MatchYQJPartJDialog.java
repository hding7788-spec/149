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
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class MatchYQJPartJDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JDialog pdialog;
	private NewTechnicsPart frame;
    private XWTreeNode node;

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField21;
    private SjzykJComboBox nameBox;
    private javax.swing.JTextField jTextField3;
    private SjzykJComboBox typeBox;
    private SjzykJComboBox typeStandardBox;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;

    private MyJMenuBar setMenuBar;

    private Map<String,List<String>> map;
    private static VaActionProgressBar progressBar;
    private Map<String,String> vMap;
    private String[] tableHeader;
    private int[] tableColWidth ;

	/**
     * Creates new form AddStandardPartJDialog
     */
    public MatchYQJPartJDialog(NewTechnicsPart frame, XWTreeNode node, JDialog pdialog, boolean modal,VaActionProgressBar progressBar) {
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
    	setTitle("PBOM元器件匹配设计编码");
    	setHideColumn(jTable1, 19);
    	setHideColumn(jTable2, 23);
    	CommonUtil.setTableStyle(jTable1);
    	CommonUtil.setTableStyle(jTable2);
    	TableColumn tableColumn = jTable2.getColumn("*单位");
        tableColumn.setCellEditor(new DefaultCellEditor(CommonUtil.getDWJComboBox()));
        setMiddleOnScreenWithDialog(this);

        tableHeader = new String [] {
        		"序号", "编号", "名称", "物资简称", "型号", "型号规格"
        		, "质量等级", "总规范", "详细规范", "封装形式", "外形尺寸", "专用条件"
        		, "附加协议", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型"
        		, "编码状态", "object","供应商"
        		,"编码等级","抗辐指标TID","抗辐指标SEE","性能参数","是否静电敏感","静电敏感等级","湿敏等级"
            };

        tableColWidth = new int[]{50,150,150,100,100,100,100,100,100,100,100,100,100,70,70,100,70,70,70,50,70,100,140,140,100,140,140,100};

        setMenuBar = new MyJMenuBar();
        //setJMenuBar(setMenuBar);

        try {
        	jComboBox1.addItem("全部");
			map = TechnicsIntf.getGlcataLog();
			if(map != null) {
				for(String name:map.keySet()) {
					List<String> list = (List<String>)map.get(name);
					if(list.contains("元器件") || list.contains("全部")) {
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
					if(value.startsWith("01")) {
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

        try {
        	List<String> nameList = TechnicsIntf.getValues("NAME");
			nameBox = new SjzykJComboBox(nameList);

			List<String> typeList = TechnicsIntf.getValues("TYPE");
			typeBox = new SjzykJComboBox(typeList);

			List<String> typeStandardList = TechnicsIntf.getValues("TYPESTANDARD");
			typeStandardBox = new SjzykJComboBox(typeStandardList);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
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
        jButton1 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel15 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jTextField14 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jTextField16 = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextField17 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextField18 = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTextField19 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jTextField21 = new javax.swing.JTextField();
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

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("查询条件"));

        jLabel1.setText("选用目录:");

//        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                selectGlcatalog(evt);
//            }
//        });

        jLabel2.setText("选择分类:");

        jLabel3.setText("编号:");

        jLabel4.setText("名称:");

        jLabel5.setText("物资简称:");

        jLabel6.setText("型号:");

        jLabel7.setText("型号规格:");

        jLabel8.setText("质量等级:");

        jLabel9.setText("专用条件:");

        jLabel10.setText("附加协议:");

        jLabel11.setText("外形尺寸:");

        jLabel12.setText("是否进口:");

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

        jLabel13.setText("详细规范:");

        jLabel14.setText("封装形式:");

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "", "否", "是" }));

        jLabel15.setText("总规范:");

        jLabel16.setText("特殊说明:");
        
        jLabel17.setText("编码等级:");
        
        jLabel18.setText("抗辐指标TID:");
        
        jLabel19.setText("抗辐指标SEE:");
        
        jLabel20.setText("性能参数:");
        
        jLabel21.setText("是否静电敏感:");
        
        jLabel22.setText("静电敏感等级:");
        
        jLabel23.setText("湿敏等级:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel23, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(typeStandardBox)
                            .addComponent(jTextField9, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(jTextField16, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(jTextField21, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        	.addComponent(nameBox, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                        	.addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(jTextField17, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(jTextField13, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .addComponent(jTextField18, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        	.addComponent(typeBox, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        	.addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                        	.addComponent(jTextField14, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                        	.addComponent(jTextField19, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(5, 5, 5)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)))
                .addContainerGap(69, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jTextField1, typeStandardBox, jTextField9});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {nameBox, jTextField6});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {typeBox, jTextField8});

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
                    .addComponent(typeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7)
                    .addComponent(typeStandardBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18)
                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20)
                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel23)
                    .addComponent(jTextField21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("查询结果列表"));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "编号", "名称", "物资简称", "型号", "型号规格", "质量等级",
                "总规范", "详细规范", "封装形式", "外形尺寸", "专用条件", "附加协议",
                "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object","供应商"
                ,"编码等级","抗辐指标TID","抗辐指标SEE","性能参数","是否静电敏感","静电敏感等级","湿敏等级"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, false, false, false, false,false,
                false, false, false, false, false, false, false,
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
        jTable1.getColumnModel().getColumn(20).setMinWidth(70);
        jTable1.getColumnModel().getColumn(20).setPreferredWidth(70);
        jTable1.getColumnModel().getColumn(21).setMinWidth(100);
        jTable1.getColumnModel().getColumn(21).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(22).setMinWidth(140);
        jTable1.getColumnModel().getColumn(22).setPreferredWidth(140);
        jTable1.getColumnModel().getColumn(23).setMinWidth(140);
        jTable1.getColumnModel().getColumn(23).setPreferredWidth(140);
        jTable1.getColumnModel().getColumn(24).setMinWidth(100);
        jTable1.getColumnModel().getColumn(24).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(25).setMinWidth(140);
        jTable1.getColumnModel().getColumn(25).setPreferredWidth(140);
        jTable1.getColumnModel().getColumn(26).setMinWidth(140);
        jTable1.getColumnModel().getColumn(26).setPreferredWidth(140);
        jTable1.getColumnModel().getColumn(27).setMinWidth(100);
        jTable1.getColumnModel().getColumn(27).setPreferredWidth(100);
        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("选择结果列表"));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "序号", "图号", "编号", "名称", "物资简称", "设计数量", "*工艺数量",
                "*单位", "型号", "型号规格", "质量等级", "总规范", "详细规范", "封装形式",
                "外形尺寸", "专用条件", "附加协议", "是否进口", "计量单位", "特殊说明", "优选级别",
                "编码类型", "编码状态", "object","供应商","备注",
                "编码等级","抗辐指标TID","抗辐指标SEE","性能参数","是否静电敏感","静电敏感等级","湿敏等级"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, true, true, false,
                false, false, false, false, false, false, false, false, false,
                false, false, false, false, false, false, false, true,
                false, false, false, false, false, false, false
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
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selected(evt);
            }
        });
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
        jTable2.getColumnModel().getColumn(7).setMinWidth(50);
        jTable2.getColumnModel().getColumn(7).setPreferredWidth(70);
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
        jTable2.getColumnModel().getColumn(13).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(14).setMinWidth(50);
        jTable2.getColumnModel().getColumn(14).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(15).setMinWidth(50);
        jTable2.getColumnModel().getColumn(15).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(16).setMinWidth(50);
        jTable2.getColumnModel().getColumn(16).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(17).setMinWidth(50);
        jTable2.getColumnModel().getColumn(17).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(18).setMinWidth(50);
        jTable2.getColumnModel().getColumn(18).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(19).setMinWidth(50);
        jTable2.getColumnModel().getColumn(19).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(20).setMinWidth(50);
        jTable2.getColumnModel().getColumn(20).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(21).setMinWidth(50);
        jTable2.getColumnModel().getColumn(21).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(22).setMinWidth(50);
        jTable2.getColumnModel().getColumn(22).setPreferredWidth(70);
        jTable2.getColumnModel().getColumn(23).setMinWidth(50);
        jTable2.getColumnModel().getColumn(23).setPreferredWidth(50);
        jTable2.getColumnModel().getColumn(24).setMinWidth(50);
        jTable2.getColumnModel().getColumn(24).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(25).setMinWidth(50);
        jTable2.getColumnModel().getColumn(25).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(26).setMinWidth(50);
        jTable2.getColumnModel().getColumn(26).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(27).setMinWidth(50);
        jTable2.getColumnModel().getColumn(27).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(28).setMinWidth(50);
        jTable2.getColumnModel().getColumn(28).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(29).setMinWidth(50);
        jTable2.getColumnModel().getColumn(29).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(30).setMinWidth(50);
        jTable2.getColumnModel().getColumn(30).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(31).setMinWidth(50);
        jTable2.getColumnModel().getColumn(31).setPreferredWidth(100);
        jTable2.getColumnModel().getColumn(32).setMinWidth(50);
        jTable2.getColumnModel().getColumn(32).setPreferredWidth(100);
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
                .addGap(55, 55, 55)
                .addComponent(jButton5)
                .addGap(44, 44, 44))
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
    }// </editor-fold>

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
    	progressBar = new VaActionProgressBar(null,this, "查询匹配元器件", "正在加载数据,请等待...", "数据加载中");
		Thread thread = new Thread() {
			public void run() {
				String xyml = String.valueOf(jComboBox1.getSelectedItem());
		    	String xzfl = String.valueOf(jComboBox2.getSelectedItem());
		    	String code = vMap.get(xzfl);
		    	String number = jTextField1.getText();//编号
		    	String name = String.valueOf(nameBox.getSelectedItem());//名称
		    	String wzjc = jTextField3.getText();//物资简称
		    	String xh = String.valueOf(typeBox.getSelectedItem());//型号
		    	String xhgg = String.valueOf(typeStandardBox.getSelectedItem());//型号规格
		    	String zldj = jTextField6.getText();//质量等级
		    	String zytj = jTextField7.getText();//专用条件
		    	String fjxx = jTextField8.getText();//附加协议
		    	String wxcc = jTextField9.getText();//外形尺寸
		    	String sfjk = String.valueOf(jComboBox3.getSelectedItem());//是否进口
		    	String xxgf = jTextField11.getText();//详细规范
		    	String fzxs = jTextField12.getText();//封装形式
		    	String zgf = jTextField13.getText();//总规范
		    	String tssm = jTextField14.getText();//特殊说明
		    	String bmdj = jTextField15.getText();//编码等级
		    	String kfzbtid = jTextField16.getText();//抗辐指标TID
		    	String kfzbsee = jTextField17.getText();//抗辐指标SEE
		    	String xncs = jTextField18.getText();//性能参数
		    	String sfjdmg = jTextField19.getText();//是否静电敏感
		    	String jdmgdj = jTextField20.getText();//经典敏感等级
		    	String smdj = jTextField21.getText();//湿敏等级

		    	Map<String,String> ibaMap = new HashMap<String,String>();
		    	if(wzjc != null && !"".equals(wzjc) && !"null".equals(wzjc)) {
		    		ibaMap.put("SHORTNAME", wzjc);
		    	}
		    	if(xh != null && !"".equals(xh) && !"null".equals(xh)) {
		    		ibaMap.put("TYPE", xh);
		    	}
		    	if(xhgg != null && !"".equals(xhgg) && !"null".equals(xhgg)) {
		    		ibaMap.put("TYPESTANDARD", xhgg);
		    	}
		    	if(zldj != null && !"".equals(zldj) && !"null".equals(zldj)) {
		    		ibaMap.put("QUALITYLEVEL", zldj);
		    	}
		    	if(zytj != null && !"".equals(zytj) && !"null".equals(zytj)) {
		    		ibaMap.put("SPECIALCONDITION", zytj);
		    	}
		    	if(fjxx != null && !"".equals(fjxx) && !"null".equals(fjxx)) {
		    		ibaMap.put("EXTRACONDITION", fjxx);
		    	}
		    	if(wxcc != null && !"".equals(wxcc) && !"null".equals(wxcc)) {
		    		ibaMap.put("OUTLINESIZE", wxcc);
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
		    	if(zgf != null && !"".equals(zgf) && !"null".equals(zgf)) {
		    		ibaMap.put("TOTALSTANDARD", zgf);
		    	}
		    	if(xxgf != null && !"".equals(xxgf) && !"null".equals(xxgf)) {
		    		ibaMap.put("DETAILSTANDARD", xxgf);
		    	}
		    	if(fzxs != null && !"".equals(fzxs) && !"null".equals(fzxs)) {
		    		ibaMap.put("PACKAGINGFORM", fzxs);
		    	}
		    	if (bmdj != null && !"".equals(bmdj) && !"null".equals(bmdj)) {
					ibaMap.put("BMDJ", bmdj);
				}
				if (kfzbtid != null && !"".equals(kfzbtid) && !"null".equals(kfzbtid)) {
					ibaMap.put("KFZBTID", kfzbtid);
				}
				if (kfzbsee != null && !"".equals(kfzbsee) && !"null".equals(kfzbsee)) {
					ibaMap.put("KFZBSEE", kfzbsee);
				}
				if (xncs != null && !"".equals(xncs) && !"null".equals(xncs)) {
					ibaMap.put("XNCS", xncs);
				}
				if (sfjdmg != null && !"".equals(sfjdmg) && !"null".equals(sfjdmg)) {
					ibaMap.put("JDMGDJ_STATE", sfjdmg);
				}
				if (jdmgdj != null && !"".equals(jdmgdj) && !"null".equals(jdmgdj)) {
					ibaMap.put("JDMGDJ", jdmgdj);
				}
				if (smdj != null && !"".equals(smdj) && !"null".equals(smdj)) {
					ibaMap.put("SMDJ", smdj);
				}
		    	
		    	//根据查询条件执行后台查询
				List<SjzykBean> list = null;
				try {
					boolean flag = setMenuBar.isMiddleTable();
					System.out.println("is middle table:"+flag);
					if(flag) {
						progressBar.setHeaderMessage("系统后台正在通过中间表查询数据！");
						list = TechnicsIntf.queryData2("元器件", xyml, code, number, name, ibaMap, "八院元器件库");
					} else {
						progressBar.setHeaderMessage("系统后台正在通过多表查询数据！");
						list = TechnicsIntf.queryData("元器件", xyml, code, number, name, ibaMap, "八院元器件库");
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
		//"序号", "编号", "名称", "物资简称", "型号", "型号规格", "质量等级", "总规范", "详细规范", "封装形式",
		//"外形尺寸", "专用条件", "附加协议", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object"
		Object[][] tableBody = new Object[list.size()][tableHeader.length];
		SjzykBean sjzykBean = null;
		for(int i=0;i<list.size();i++) {
			sjzykBean = list.get(i);
			tableBody[i][0] = (i+1);
			tableBody[i][1] = sjzykBean.getSjbm();
			tableBody[i][2] = sjzykBean.getName();
			tableBody[i][3] = sjzykBean.getWzjc();
			tableBody[i][4] = sjzykBean.getXh();
			tableBody[i][5] = sjzykBean.getXhgg();
			tableBody[i][6] = sjzykBean.getZldj();
			tableBody[i][7] = sjzykBean.getZgf();
			tableBody[i][8] = sjzykBean.getXxgf();
			tableBody[i][9] = sjzykBean.getFzxs();
			tableBody[i][10] = sjzykBean.getWxcc();
			tableBody[i][11] = sjzykBean.getZytj();
			tableBody[i][12] = sjzykBean.getFjxy();
			tableBody[i][13] = sjzykBean.getSfjk();
			tableBody[i][14] = sjzykBean.getJldw();
			tableBody[i][15] = sjzykBean.getTssm();
			tableBody[i][16] = sjzykBean.getBmyyjb();
			tableBody[i][17] = sjzykBean.getBmlx();
			tableBody[i][18] = sjzykBean.getBmzt();
			tableBody[i][19] = sjzykBean;
			tableBody[i][20] = sjzykBean.getGys();
			tableBody[i][21] = sjzykBean.getBmdj();
			tableBody[i][22] = sjzykBean.getKfzbtid();
			tableBody[i][23] = sjzykBean.getKfzbsee();
			tableBody[i][24] = sjzykBean.getXncs();
			tableBody[i][25] = sjzykBean.getJdmgdj_state();
			tableBody[i][26] = sjzykBean.getJdmgdj();
			tableBody[i][27] = sjzykBean.getSmdj();
		}

		tableModel.setDataVector(tableBody, tableHeader);
		for (int i = 1; i < tableHeader.length; i++) {
			jTable1.getColumn(tableHeader[i]).setPreferredWidth(tableColWidth[i]);
		}
		setHideColumn(jTable1, 19);

		CommonUtil.setTableStyle(jTable1);


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

    private void save(ActionEvent evt) {
    	int rows = jTable2.getRowCount();
    	if(rows < 1) {
    		JOptionPane.showMessageDialog(this, "没有需要添加的数据！");
    		return ;
    	}
    	DefaultTableModel tableModel = (DefaultTableModel) jTable2.getModel();
    	if(!CommonUtil.checkValueIsNull(tableModel, new int[]{6,7})) {
    		JOptionPane.showMessageDialog(this, "工艺数量、单位不能为空！");
			return;
    	}
//    	if(!CommonUtil.checkIsNumeric(tableModel, new int[]{6})) {
//    		JOptionPane.showMessageDialog(this, "工艺数量只能填写0-9的数字！！");
//			return;
//    	}
        for (int i = 0; i < rows; i++) {
            String num = objectToString(tableModel.getValueAt(i, 6));
            String unit = objectToString(tableModel.getValueAt(i, 7));
            if(!CommonUtil.checkGysl(num,unit)){
                JOptionPane.showMessageDialog(this, "第" + (i+1) + "行工艺数量填写不规范：\r\n单位为“个”，“只”，“件”的数量只能为整数，其他的只能为最多三数");
                return;
            }
        }

    	SjzykBean bean = null;
    	for(int i=0 ; i<rows; i++) {
    		List<Object> list = new ArrayList<Object>();
    		//"序号", "图号", "编号", "名称", "物资简称", "设计数量", "*工艺数量", "*单位", "型号", "型号规格", "质量等级",
    		//"总规范", "详细规范", "封装形式", "外形尺寸", "专用条件", "附加协议", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "供应商","备注","object"
    		bean = (SjzykBean)tableModel.getValueAt(i, 23);
    		bean.setSjbm(String.valueOf(tableModel.getValueAt(i, 2)));
    		bean.setName(String.valueOf(tableModel.getValueAt(i, 3)));
    		bean.setWzjc(String.valueOf(tableModel.getValueAt(i, 4)));
    		bean.setGysl(String.valueOf(tableModel.getValueAt(i, 6)));
    		bean.setDw(String.valueOf(tableModel.getValueAt(i, 7)));
    		bean.setXh(String.valueOf(tableModel.getValueAt(i, 8)));
    		bean.setXhgg(String.valueOf(tableModel.getValueAt(i, 9)));
    		bean.setZldj(String.valueOf(tableModel.getValueAt(i, 10)));
    		bean.setZgf(String.valueOf(tableModel.getValueAt(i, 11)));
    		bean.setXxgf(String.valueOf(tableModel.getValueAt(i, 12)));
    		bean.setFzxs(String.valueOf(tableModel.getValueAt(i, 13)));
    		bean.setWxcc(String.valueOf(tableModel.getValueAt(i, 14)));
    		bean.setZytj(String.valueOf(tableModel.getValueAt(i, 15)));
    		bean.setFjxy(String.valueOf(tableModel.getValueAt(i, 16)));
    		bean.setSfjk(String.valueOf(tableModel.getValueAt(i, 17)));
    		bean.setJldw(String.valueOf(tableModel.getValueAt(i, 18)));
    		bean.setTssm(String.valueOf(tableModel.getValueAt(i, 19)));
    		bean.setBmyyjb(String.valueOf(tableModel.getValueAt(i, 20)));
    		bean.setBmlx(String.valueOf(tableModel.getValueAt(i, 21)));
    		bean.setBmzt(String.valueOf(tableModel.getValueAt(i, 22)));
    		bean.setGys(String.valueOf(tableModel.getValueAt(i, 24)));
    		bean.setComment(String.valueOf(tableModel.getValueAt(i, 25)));
    		bean.setDataType("元器件");
    		// 保存元器件新增属性 add by hz 2020/1/
    		bean.setBmdj(String.valueOf(tableModel.getValueAt(i, 26)));
    		bean.setKfzbtid(String.valueOf(tableModel.getValueAt(i, 27)));
    		bean.setKfzbsee(String.valueOf(tableModel.getValueAt(i, 28)));
    		bean.setXncs(String.valueOf(tableModel.getValueAt(i, 29)));
    		bean.setJdmgdj_state(String.valueOf(tableModel.getValueAt(i, 30)));
    		bean.setJdmgdj(String.valueOf(tableModel.getValueAt(i, 31)));
    		bean.setSmdj(String.valueOf(tableModel.getValueAt(i, 32)));
    		
    		list.add(bean);
    		((ShowMatchPartJDialog)pdialog).setValues(list,"YQJ");
    	}


    	this.setVisible(false);
    }

    private void loadData(){
    	ShowMatchPartJDialog matchDialog = ((ShowMatchPartJDialog)pdialog);
    	List<SjzykBean> list = matchDialog.getDataByDataType("YQJ");
    	for (SjzykBean bean : list) {
    		addRowValue(bean);
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

    private void clear(ActionEvent evt) {
    	jTextField1.setText("");//编号
    	//nameBox.setText("");//名称
    	jTextField3.setText("");//物资简称
    	//typeBox.setText("");//型号
    	//typeStandardBox.setText("");//型号规格
    	jTextField6.setText("");//质量等级
    	jTextField7.setText("");//专用条件
    	jTextField8.setText("");//附加协议
    	jTextField9.setText("");//外形尺寸
    	jTextField11.setText("");//详细规范
    	jTextField12.setText("");//封装形式
    	jTextField13.setText("");//总规范
    	jTextField14.setText("");//特殊说明
    	jTextField15.setText("");//编码等级
    	jTextField16.setText("");//抗辐指标TID
    	jTextField17.setText("");//抗辐指标SEE
    	jTextField18.setText("");//性能参数
    	jTextField19.setText("");//是否经典敏感
    	jTextField20.setText("");//经典敏感等级
    	jTextField21.setText("");//湿敏等级
    	jComboBox3.setSelectedItem("");//是否进口
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
    			if(!isSelected()) {
    				JOptionPane.showMessageDialog(this, "请在匹配结果列表中选中需要匹配的零部件！");
    				return ;
    			}

    			SjzykBean bean = (SjzykBean)jTable1.getModel().getValueAt(selr, 19);

    			if(!"启用".equals(bean.getBmzt())) {
    				JOptionPane.showMessageDialog(this, "该编码状态不是处于启用状态，不能使用！");
    				return ;
    			}

    			setRowValues(bean);
    		}
    	}
    }

    private void selected(MouseEvent evt) {
    	int b1 = evt.getButton();
    	if(MouseEvent.BUTTON1 == b1 ) {
    		int selr = jTable2.getSelectedRow();
    		SjzykBean bean = (SjzykBean)jTable2.getModel().getValueAt(selr, 23);
    		nameBox.setSelectedItem(bean.getName());//名称
    		typeBox.setSelectedItem(bean.getXh());//型号
    		typeStandardBox.setSelectedItem(bean.getXhgg());//型号规格
    	}
    }

    private void setRowValues(SjzykBean bean) {
		DefaultTableModel tableModel = (DefaultTableModel)jTable2.getModel();

		//写入数据
		//"序号", "图号", "编号", "名称", "物资简称", "设计数量", "*工艺数量", "*单位", "型号", "型号规格", "质量等级", "总规范",
		//"详细规范", "封装形式", "外形尺寸", "专用条件", "附加协议", "是否进口", "计量单位", "特殊说明", "特殊说明", "优选级别", "编码类型", "编码状态", "object"
		int n = jTable2.getSelectedRow();
		tableModel.setValueAt(bean.getSjbm(), n, 2);
		tableModel.setValueAt(bean.getName(), n, 3);
		tableModel.setValueAt(bean.getWzjc(), n, 4);
		//tableModel.setValueAt(bean.getSjsl(), n, 5);
		if(bean.getGysl() != null) {
			tableModel.setValueAt(bean.getGysl(), n, 6);
			tableModel.setValueAt(bean.getDw(), n, 7);
		}
		tableModel.setValueAt(bean.getXh(), n, 8);
		tableModel.setValueAt(bean.getXhgg(), n, 9);
		tableModel.setValueAt(bean.getZldj(), n, 10);
		tableModel.setValueAt(bean.getZgf(), n, 11);
		tableModel.setValueAt(bean.getXxgf(), n, 12);
		tableModel.setValueAt(bean.getFzxs(), n, 13);
		tableModel.setValueAt(bean.getWxcc(), n, 14);
		tableModel.setValueAt(bean.getZytj(), n, 15);
		tableModel.setValueAt(bean.getFjxy(), n, 16);
		tableModel.setValueAt(bean.getSfjk(), n, 17);
		tableModel.setValueAt(bean.getJldw(), n, 18);
		tableModel.setValueAt(bean.getTssm(), n, 19);
		tableModel.setValueAt(bean.getBmyyjb(), n, 20);
		tableModel.setValueAt(bean.getBmlx(), n, 21);
		tableModel.setValueAt(bean.getBmzt(), n, 22);
		//tableModel.setValueAt(bean, n, 23);
		tableModel.setValueAt(bean.getGys(), n, 24);
		tableModel.setValueAt(bean.getComment(), n, 25);
		tableModel.setValueAt(bean.getBmdj(), n, 26);
		tableModel.setValueAt(bean.getKfzbtid(), n, 27);
		tableModel.setValueAt(bean.getKfzbsee(), n, 28);
		tableModel.setValueAt(bean.getXncs(), n, 29);
		tableModel.setValueAt(bean.getJdmgdj_state(), n, 30);
		tableModel.setValueAt(bean.getJdmgdj(), n, 31);
		tableModel.setValueAt(bean.getSmdj(), n, 32);
    }

    private void addRowValue(SjzykBean bean) {
    	DefaultTableModel tableModel = (DefaultTableModel)jTable2.getModel();
    	Vector<String> vector = new Vector<String>();
		for (int i = 0; i < tableModel.getColumnCount(); i++) {
			vector.add("");
		}
		tableModel.addRow(vector);

		int rows = jTable2.getRowCount();
		int n = rows - 1;

		//写入数据
		//"序号", "图号", "编号", "名称", "物资简称", "设计数量", "*工艺数量", "*单位", "型号", "型号规格", "质量等级",
		//"总规范", "详细规范", "封装形式", "外形尺寸", "专用条件", "附加协议", "是否进口", "计量单位", "特殊说明", "优选级别", "编码类型", "编码状态", "object"
		tableModel.setValueAt(rows, n, 0);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getPartNumber()), n, 1);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getSjbm()), n, 2);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getName()), n, 3);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getWzjc()), n, 4);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getSjsl()), n, 5);
		if(bean.getGysl() != null) {
			tableModel.setValueAt(bean.getGysl(), n, 6);
			tableModel.setValueAt(bean.getDw(), n, 7);
		}
		tableModel.setValueAt(CommonUtil.objectToString(bean.getXh()), n, 8);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getXhgg()), n, 9);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getZldj()), n, 10);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getZgf()), n, 11);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getXxgf()), n, 12);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getFzxs()), n, 13);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getWxcc()), n, 14);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getZytj()), n, 15);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getFjxy()), n, 16);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getSfjk()), n, 17);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getJldw()), n, 18);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getTssm()), n, 19);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getBmyyjb()), n, 20);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getBmlx()), n, 21);
		tableModel.setValueAt(CommonUtil.objectToString(bean.getBmzt()), n, 22);
		tableModel.setValueAt(bean, n, 23);
		tableModel.setValueAt(bean.getGys(), n, 24);
		tableModel.setValueAt(bean.getComment(), n, 25);
    }

    private boolean isSelected() {
    	int selr = jTable2.getSelectedRow();
    	if(selr > -1) {
    		return true;
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
