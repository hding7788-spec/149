package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.TreeNode;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import wt.doc.WTDocument;
import wt.util.WTException;

import com.glaway.mpm.qmIntf.template.TemplateSearchDialog;
import com.glaway.mpm.util.BomXMLUtil;
import com.glaway.mpm.util.FileChooserTool;
import com.glaway.mpm.util.FilesUtil;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.TechnicsReleaseUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WorkInProcessUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.wcIntf.TechnicsIntf;
/*   author:chenming
 *   data:2015.12.07
 *   comment：新建技术协议弹出窗口
 * */
public class CreateJishuxieyiDialog<V> extends JDialog{
	public static File file;
	private JDialog dialog;
	private NewTechnicsPart frame;
	private Document document;
	public Element techElement = null;
	private XWTreeNode treeNode;
	private JPanel panel0 = new JPanel();
	private JPanel panel2 = new JPanel();
	private javax.swing.JPanel panel =new JPanel();
	private javax.swing.JButton sure=new JButton("确定");
	private javax.swing.JButton close=new JButton("取消");
	private Map<String,String> ibasMap = new HashMap<String,String>();
	private List<String> ibaList = new ArrayList<String>();
	private String partNumber;
	private JTextField mindex_Value = new JTextField();
	private JTextField pindex_value = new JTextField();
	private JTextField keycomponent_value = new JTextField();
	private JTextField phase_code_value = new JTextField();
	private JTextField dept_value = new JTextField();
	private String[] pplantype_array = {"正式工艺文件","临时工艺文件"};
	private String[] zfflag_array = {"Z","F"};
	private String[] secret_array = {"公开","内部"};
//	private String[] secret_array = {"公开","内部","秘密★10年","机密★20年"};
	private String[] jieduan={"Y","M","C","S","Z","D","G","P","-"};
	private String[] department_array = {"1","2","3","4","5","6","7","8","项"};
	private JTextField zfflag_value = new JTextField();
	final JTextField pplantype_value = new JTextField();
	private JTextField pplanid_value = new JTextField();
	private JComboBox pcno_box_value = null;
	final JLabel tempno_label = new JLabel("临时工艺顺序号");
	final JTextField tempno_value = new JTextField();
	private JLabel pcno_label = new JLabel("批次号");
	private JTextField pcno_value = new JTextField();
	final JLabel technicsFileNo_label = new JLabel("工艺文件顺序号");
	private JTextField technicsFileNo_value = new JTextField();
	private String[] pplanForms = {"非表格化","表格化","外协"};
	private JTextField pplanForms_value = new JTextField();
	private static final String ID = "Rz";
	private Vector<String> batchs;
	private Element parentElement;
	private  JLabel technicsNameLabel = new JLabel("技术协议名称");
	public static JTextField technicsNameText  = new JTextField();
	private JLabel newJsxyLabel=new JLabel("新建技术协议");
	private JButton newLiulan=new JButton("浏览");
	private JLabel newJsxyLabel1=new JLabel("       ");
	private JButton newBianji=new JButton("编辑");
	private JLabel jsxyLabel=new JLabel("选择现有的技术协议");
	private JButton liulan=new JButton("浏览");
   private String technicsNumber;
   private String version;
   public static  String jsxyNumber;
   public static String flag;
   public static Boolean CreateFlag=false;
   final JComboBox secret_value = new JComboBox(secret_array);
   final JComboBox jieduan_value = new JComboBox(jieduan);
   final JLabel phase_code_label = new JLabel("产品阶段标记");
   final JLabel dept_label = new JLabel("部门");
   final JLabel secret_label = new JLabel("文件密级");

	public CreateJishuxieyiDialog(NewTechnicsPart parent, Element element,
			XWTreeNode node) {
		super(parent, true);
		parentElement=element;
		this.frame = parent;
		treeNode=node;
		setTitle("新建技术协议");
		setIconImage(new ImageIcon(getClass().getResource("/images/technics.gif")).getImage());
		Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension2.getWidth() - 500) / 2 , (int) (dimension2.getHeight() - (625)) / 2, 500, 580);
		//获取零部件的IBA属性值
		partNumber = XmlUtility.getAttributeValue(parentElement, "partNumber");
		technicsNumber = XmlUtility.getAttributeValue(parentElement, "technicsNumber");
		version = XmlUtility.getAttributeValue(parentElement, "version");
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

                 XWTreeNode selectedTreeNode = frame.xwPartTreePanel.getSelectedTreeNode();
                 XWTreeNode parent2 = (XWTreeNode) selectedTreeNode.getParent();
                 Element pElement = parent2.getObject().getTreeCellData();
				//获取当前产品的批次号
		        if(batchs == null) {
		            long oid = Long.valueOf(XmlUtility.getAttributeValue(pElement, "containerId"));
		            try {
		                batchs = TechnicsIntf.getBatchsByProductOid(oid);
		            } catch (RemoteException e1) {
		                // TODO Auto-generated catch block
		                e1.printStackTrace();
		            } catch (InvocationTargetException e1) {
		                // TODO Auto-generated catch block
		                e1.printStackTrace();
		            }
		            if(batchs == null) {
		                batchs = new Vector<String>();
		                batchs.add("");
		            }
		        }
		        pcno_box_value = new JComboBox(batchs);

		initComponents();


	}
	private void initComponents() {
		Container container = getContentPane();
		JScrollPane scrollPane = new JScrollPane(panel0);
		container.add(scrollPane);
		panel0.setLayout(new BorderLayout());
		panel.setLayout(new GridBagLayout());
		panel0.add(panel,BorderLayout.CENTER);
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

		sure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    String pageSize =XmlUtility.getAttributeValue(parentElement, "pageSize");

			    if ("null".equals(pageSize)||pageSize==null) {
			        JOptionPane.showMessageDialog(frame, "请先上载工艺,再创建技术协议！", "提示",JOptionPane.INFORMATION_MESSAGE);
			        return;
                }
			    Map<String, String> map = new HashMap<String, String>();
			    map.put("DEPT", dept_value.getText());
			    map.put("MINDEX", mindex_Value.getText());
			    map.put("PINDEX", pindex_value.getText());
			    map.put("PRTDEX", XmlUtility.getAttributeValue(parentElement, "partNumber"));
			    map.put("PRTNAME", XmlUtility.getAttributeValue(parentElement, "partName"));
			    map.put("PHASE_CODE", (String)jieduan_value.getSelectedItem());
			    map.put("BATCH", (String)pcno_box_value.getSelectedItem());
			    map.put("SECRET", (String)secret_value.getSelectedItem());
			    map.put("name",technicsNameText.getText());
				if (file!=null) {
				    String text = technicsNameText.getText().trim();
	                if (text==null||"".equals(text)||"null".equals(text)) {
	                    JOptionPane.showMessageDialog(frame, "请填入相应的技术协议名称！", "提示",JOptionPane.INFORMATION_MESSAGE);
	                }
					InputStream is;
					String mindex = "";
					try {
						is = new FileInputStream(file);
						byte[] bytes = IOUtils.toByteArray(is);
						String name=file.getName();
						name=name.replace(".doc", "");
						XWTreeNode selectedTreeNode = frame.xwPartTreePanel.getSelectedTreeNode();
						XWTreeObject object = selectedTreeNode.getObject();
						if (object instanceof TechnicsMessageTreeObject) {
						    TechnicsMessageTreeObject technicsObj = (TechnicsMessageTreeObject) object;
						    Element treeCellData = technicsObj.getTreeCellData();
						    mindex = treeCellData.attributeValue("MINDEX");
                        }
						String jsxyNumber="";
						 flag = (String) IntfUtil.getPeRemoteMethodInvoke("setDocumentAttachment",
           						new Class[] { byte[].class,String.class,String.class,String.class,Map.class }, new Object[] {bytes,technicsNumber,name,jsxyNumber,map });
						if ("".equals(flag)||"null".equals(flag)||flag==null) {
							JOptionPane.showMessageDialog(frame, "创建技术协议失败,请检查该产品下的型号简号属性是否为空！", "提示",JOptionPane.INFORMATION_MESSAGE);
							dispose();
						}else{
							JOptionPane.showMessageDialog(frame, "创建技术协议成功！", "提示",JOptionPane.INFORMATION_MESSAGE);
							dispose();
						}
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}else if(!"".equals(jsxyNumber)&&!"null".equals(jsxyNumber)&&jsxyNumber!=null){
					int flag1 = JOptionPane.showConfirmDialog(frame, "确定选择现有的技术协议？", "确认", JOptionPane.OK_CANCEL_OPTION);
					if (flag1 == JOptionPane.YES_OPTION) {


						if (!"".equals(jsxyNumber)&&!"null".equals(jsxyNumber)&&jsxyNumber!=null) {
							CreateFlag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("HasSuccessCreateJsxyDoc",
	           						new Class[] {String.class,String.class,String.class,Map.class}, new Object[] {jsxyNumber,technicsNumber });
							if (CreateFlag) {
								JOptionPane.showMessageDialog(frame, "创建技术协议成功！", "提示",JOptionPane.INFORMATION_MESSAGE);
								dispose();
							}else{
								JOptionPane.showMessageDialog(frame, "创建技术协议失败！", "提示",JOptionPane.INFORMATION_MESSAGE);
								dispose();
							}

						}else{
							JOptionPane.showMessageDialog(frame, "创建技术协议失败！", "提示",JOptionPane.INFORMATION_MESSAGE);
							dispose();
						}
					}
				}else{
				    JOptionPane.showMessageDialog(frame, "请选择相应的技术协议！", "提示",JOptionPane.INFORMATION_MESSAGE);
				}
				setButtonState(true);
			}
		});
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    jsxyNumber="";
			    file=null;
			    setButtonState(true);
				dispose();
			}
		});


		//设置网格布局管理器参数
				final GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new Insets(5, 5, 0, 0);
				gridBagConstraints.gridwidth = 2;

				//第一行：产品型号代号
				JLabel mindex_label = new JLabel("产品型号代号");
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.gridwidth = 1;
				panel.add(mindex_label, gridBagConstraints);

				//mindex_Value.setEditable(false);
				mindex_Value.setText(ibasMap.get("MINDEX"));
				mindex_Value.setEditable(false);
				mindex_Value.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				panel.add(mindex_Value, gridBagConstraints);

				//第二行：产品代号
				final JLabel pindex_label = new JLabel("产品代号");
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.gridwidth = 1;
				panel.add(pindex_label, gridBagConstraints);

				pindex_value.setEditable(false);
				pindex_value.setText(ibasMap.get("PINDEX"));
				pindex_value.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				panel.add(pindex_value, gridBagConstraints);

				//第三行：设计图样代号
				final JLabel partNumber_label = new JLabel("设计图样代号");
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 2;
				gridBagConstraints.gridwidth = 1;
				panel.add(partNumber_label, gridBagConstraints);

				JTextField partNumber_value = new JTextField();
				partNumber_value.setEditable(false);
				partNumber_value.setText(XmlUtility.getAttributeValue(parentElement, "CINDEX"));
				partNumber_value.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 2;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				panel.add(partNumber_value, gridBagConstraints);

				//第四行：设计图样名称
				final JLabel partName_label = new JLabel("设计图样名称");
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 3;
				gridBagConstraints.gridwidth = 1;
				panel.add(partName_label, gridBagConstraints);

				JTextField partName_value = new JTextField();
				partName_value.setEditable(false);
				partName_value.setText(XmlUtility.getAttributeValue(parentElement, "partName"));
				partName_value.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 3;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				panel.add(partName_value, gridBagConstraints);

//				//第五行：关重件标记
//				final JLabel keycomponent_label = new JLabel("关重件标记");
//				gridBagConstraints.gridx = 0;
//				gridBagConstraints.gridy = 4;
//				gridBagConstraints.gridwidth = 1;
//				panel.add(keycomponent_label, gridBagConstraints);

//				keycomponent_value.setText(ibasMap.get("KEYCOMPONENT"));
////				keycomponent_value.setText(ibasMap.get("KEYCOMPONENT"));
//				keycomponent_value.setPreferredSize(new Dimension(300, 23));
//				gridBagConstraints.gridwidth = 2;
//				gridBagConstraints.gridx = 1;
//				gridBagConstraints.gridy = 4;
//				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
//				panel.add(keycomponent_value, gridBagConstraints);

				//第六行：产品阶段标记

				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 5;
				gridBagConstraints.gridwidth = 1;
				panel.add(phase_code_label, gridBagConstraints);

				jieduan_value.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 5;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				panel.add(jieduan_value, gridBagConstraints);


				//第七行：部门

				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 6;
				gridBagConstraints.gridwidth = 1;
				panel.add(dept_label, gridBagConstraints);

				dept_value.setPreferredSize(new Dimension(150, 23));
				dept_value.setText(parentElement.attributeValue("DEPT"));
				dept_value.setEditable(false);
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 6;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				panel.add(dept_value, gridBagConstraints);


				//第十四行：工艺顺序号
				technicsFileNo_label.setVisible(false);
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 13;
				gridBagConstraints.gridwidth = 1;
				panel.add(technicsFileNo_label, gridBagConstraints);

				technicsFileNo_value.setVisible(false);
				technicsFileNo_value.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 13;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				panel.add(technicsFileNo_value, gridBagConstraints);

//				//第十五行：工艺文件编号
//				JLabel  technicsFileCodeLabel = new JLabel("技术协议编号");
//				JTextField technicsFileCodeText = new JTextField();
//				technicsFileCodeText.setEditable(true);
//				technicsFileCodeText.setText(parentElement.attributeValue("pplanNumber"));
//				technicsFileCodeText.setPreferredSize(new Dimension(300, 23));
//				gridBagConstraints.gridx = 0;
//				gridBagConstraints.gridy = 14;
//				gridBagConstraints.gridwidth = 1;
//				panel.add(technicsFileCodeLabel,gridBagConstraints);
//				gridBagConstraints.gridx = 1;
//				gridBagConstraints.gridy = 14;
//				gridBagConstraints.gridwidth = 2;
//				panel.add(technicsFileCodeText,gridBagConstraints);


				//第十六行：工艺文件名称
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 15;
				gridBagConstraints.gridwidth = 1;
				panel.add(technicsNameLabel,gridBagConstraints);
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 15;
				gridBagConstraints.gridwidth = 2;
				technicsNameText.setText("");
				technicsNameText.setPreferredSize(new Dimension(300, 23));
				panel.add(technicsNameText,gridBagConstraints);


				//第十七行：新建技术协议
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 16;
				gridBagConstraints.gridwidth = 1;
				panel.add(newJsxyLabel,gridBagConstraints);
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 16;
				gridBagConstraints.gridwidth = 2;
				newLiulan.setPreferredSize(new Dimension(70, 23));
				newLiulan.setMinimumSize(new Dimension(70, 23));
				newLiulan.setMaximumSize(new Dimension(70, 23));
				panel.add(newLiulan,gridBagConstraints);
				newLiulan.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if ("".equals(jsxyNumber)||"null".equals(jsxyNumber)||jsxyNumber==null) {
						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new File("."));
						chooser.setMultiSelectionEnabled(false);
						FileNameExtensionFilter filter = new FileNameExtensionFilter(".doc", "doc");
						chooser.setFileFilter(filter);
						int result = chooser.showOpenDialog(frame);
						if(result == JFileChooser.APPROVE_OPTION){
							  file = chooser.getSelectedFile();
							  if (file!=null&&!"".equals(file)&&!"null".equals(file)) {
							      setButtonState(true);
                            }
						}
						}else{
							int flag = JOptionPane.showConfirmDialog(frame, "确定新建技术协议？", "确认", JOptionPane.OK_CANCEL_OPTION);
							if (flag == JOptionPane.YES_OPTION) {
								JFileChooser chooser = new JFileChooser();
								chooser.setCurrentDirectory(new File("."));
								chooser.setMultiSelectionEnabled(false);
								FileNameExtensionFilter filter = new FileNameExtensionFilter(".doc", "doc");
								chooser.setFileFilter(filter);
								int result = chooser.showOpenDialog(frame);
								if(result == JFileChooser.APPROVE_OPTION){
									  file = chooser.getSelectedFile();
									  if (file!=null&&!"".equals(file)&&!"null".equals(file)) {
		                                  setButtonState(true);
		                            }
							}
								jsxyNumber=null;
						}
					}
					}
				});

				//第十八行：新建技术协议1
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 17;
				gridBagConstraints.gridwidth = 1;
				panel.add(newJsxyLabel1,gridBagConstraints);
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 17;
				gridBagConstraints.gridwidth = 2;
				newBianji.setPreferredSize(new Dimension(70, 23));
				newBianji.setMinimumSize(new Dimension(70, 23));
				newBianji.setMaximumSize(new Dimension(70, 23));
				panel.add(newBianji,gridBagConstraints);

				newBianji.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.out.println("cmcmcmccmmc------------->>>"+file);
                    if (file==null) {
                    	JOptionPane.showMessageDialog(frame, "请先新建技术协议！", "提示",JOptionPane.INFORMATION_MESSAGE);
                    	return ;
					}else{
						try {
							Runtime.getRuntime().exec(
									"rundll32.exe url.dll,FileProtocolHandler  "
											+file );
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}

					}
				});


				//第十九行：选择现有技术协议
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 18;
				gridBagConstraints.gridwidth = 1;
				panel.add(jsxyLabel,gridBagConstraints);
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 18;
				gridBagConstraints.gridwidth = 2;
				liulan.setPreferredSize(new Dimension(70, 23));
				liulan.setMinimumSize(new Dimension(70, 23));
				liulan.setMaximumSize(new Dimension(70, 23));
				panel.add(liulan,gridBagConstraints);
                liulan.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (file!=null) {
							int flag = JOptionPane.showConfirmDialog(frame, "确定放弃新建技术协议？", "确认", JOptionPane.OK_CANCEL_OPTION);
							if (flag == JOptionPane.YES_OPTION) {
//                               file.deleteOnExit();
								file=null;

                               CreatePDMJsxyTable table= new CreatePDMJsxyTable(frame, treeNode);
                               jsxyNumber=CreatePDMJsxyTable.number;
                               if (jsxyNumber!=null&&!"".equals(jsxyNumber)&&!"null".equals(jsxyNumber)) {
                                   setButtonState(false);
                            }
							}
						}else{
//							 ArrayList list = (ArrayList) IntfUtil.getPeRemoteMethodInvoke("getAllJsxyByContainer",
//	               						new Class[] { String.class }, new Object[] { parentElement.attributeValue("productName")});
	                               CreatePDMJsxyTable table= new CreatePDMJsxyTable(frame, treeNode);
	                               jsxyNumber=CreatePDMJsxyTable.number;
	                               if (jsxyNumber!=null&&!"".equals(jsxyNumber)&&!"null".equals(jsxyNumber)) {
	                                   setButtonState(false);
	                            }

						}

					}
				});


				//第二十行：文件密级

				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 19;
				gridBagConstraints.gridwidth = 1;
				panel.add(secret_label, gridBagConstraints);

				secret_value.setPreferredSize(new Dimension(150, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 19;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				secret_value.setEditable(false);
				panel.add(secret_value, gridBagConstraints);

				//第二十一行：批次号
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 20;
				gridBagConstraints.gridwidth = 1;
				panel.add(pcno_label, gridBagConstraints);

				pcno_box_value.setPreferredSize(new Dimension(150, 23));
				pcno_box_value.setEditable(false);
//				pcno_box_value.setText(parentElement.attributeValue("PCNO"));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 20;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				panel.add(pcno_box_value, gridBagConstraints);

				Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
				setBounds((int) (dimension2.getWidth() - 500) / 2 , (int) (dimension2.getHeight() - (625)) / 2, 500, 625);

				setVisible(true);


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
	public void initIBAList(){
		ibaList.add("PHASE_CODE");
		ibaList.add("MINDEX");
		ibaList.add("PINDEX");
		ibaList.add("KEYCOMPONENT");
	}

	public static void setFile(File file) {
        CreateJishuxieyiDialog.file = file;
    }
    public static void setJsxyNumber(String jsxyNumber) {
        CreateJishuxieyiDialog.jsxyNumber = jsxyNumber;
    }
    public static String getJsxyNumber() {
		return jsxyNumber;
	}

	public static File getFile() {
		return file;
	}
	public void setButtonState(Boolean flag) {
	    phase_code_label.setEnabled(flag);
	    jieduan_value.setEnabled(flag);
	    technicsNameLabel.setEnabled(flag);
	    technicsNameText.setEnabled(flag);
	    secret_label.setEnabled(flag);
	    secret_value.setEnabled(flag);
	    pcno_label.setEnabled(flag);
	    pcno_box_value.setEnabled(flag);
    }
	static String getJSxyName() {
        return technicsNameText.getText();
    }
}
