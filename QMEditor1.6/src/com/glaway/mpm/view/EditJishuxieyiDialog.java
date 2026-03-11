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
public class EditJishuxieyiDialog extends JDialog{
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
	private Element parentElement;
	private JButton newLiulan=new JButton("浏览");
	private JLabel liulan=new JLabel("  ");
	private JButton newBianji=new JButton("编辑");
   private String version;
   public static  String jsxyNumber;
   public  boolean flag;
   public static String name;
   public static String number;
   private JTextField mingcheng= new JTextField();
	public EditJishuxieyiDialog(NewTechnicsPart parent,
			XWTreeNode node) {
		super(parent, true);
		this.frame = parent;
		treeNode=node;
		setTitle("修改技术协议");
		setIconImage(new ImageIcon(getClass().getResource("/images/technics.gif")).getImage());
		Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension2.getWidth() - 500) / 2 , (int) (dimension2.getHeight() - (625)) / 2, 500, 580);
		XWTreeObject obj = treeNode.getObject();
		if (obj instanceof JsxyDocTreeObject) {
			 name = ((JsxyDocTreeObject) obj).getName();
			 number = ((JsxyDocTreeObject) obj).getNumber();
		}

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
            if (file==null) {
            	JOptionPane.showMessageDialog(frame, "请先选择主要内容！", "提示",JOptionPane.INFORMATION_MESSAGE);
			}else{
				FileInputStream is;
				try {
					String fileName = file.getName();
					String jsxyName = mingcheng.getText();
					is = new FileInputStream(file);
					byte[] bytes = IOUtils.toByteArray(is);

					flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("setJsxyDocPrimay",
       						new Class[] { byte[].class,String.class,String.class,String.class }, new Object[] {bytes,number, fileName});
					if (flag) {
						JOptionPane.showMessageDialog(frame, "修改成功！", "提示",JOptionPane.INFORMATION_MESSAGE);
						dispose();
					}else{
						JOptionPane.showMessageDialog(frame, "修改失败！", "提示",JOptionPane.INFORMATION_MESSAGE);
					dispose();

					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			}
		});
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});


		//设置网格布局管理器参数
				final GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new Insets(5, 5, 0, 0);
				gridBagConstraints.gridwidth = 2;

				//第一行：技术协议名称
				JLabel mindex_label = new JLabel("技术协议名称");
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.gridwidth = 1;
				panel.add(mindex_label, gridBagConstraints);

				mingcheng.setText(name);
				mingcheng.setEditable(false);
				mingcheng.setPreferredSize(new Dimension(300, 23));
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.insets = new Insets(5, 5, 0, 5);
				panel.add(mingcheng, gridBagConstraints);

				//第二行：选择主要内容
				final JLabel pindex_label = new JLabel("选择主要内容");
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.gridwidth = 1;
				panel.add(pindex_label, gridBagConstraints);

				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.gridwidth = 1;
				newLiulan.setPreferredSize(new Dimension(70, 23));
				newLiulan.setMinimumSize(new Dimension(70, 23));
				newLiulan.setMaximumSize(new Dimension(70, 23));
				panel.add(newLiulan,gridBagConstraints);
				newLiulan.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
                     if (file==null) {
                    		JFileChooser chooser = new JFileChooser();
							chooser.setCurrentDirectory(new File("."));
							chooser.setMultiSelectionEnabled(false);
							FileNameExtensionFilter filter = new FileNameExtensionFilter(".doc", "doc");
							chooser.setFileFilter(filter);
							int result = chooser.showOpenDialog(frame);
							if(result == JFileChooser.APPROVE_OPTION){
								  file = chooser.getSelectedFile();
						}
					}else{
						int flag = JOptionPane.showConfirmDialog(frame, "确定选择新的主要内容吗？", "确认", JOptionPane.OK_CANCEL_OPTION);
						if (flag == JOptionPane.YES_OPTION) {
							JFileChooser chooser = new JFileChooser();
							chooser.setCurrentDirectory(new File("."));
							chooser.setMultiSelectionEnabled(false);
							FileNameExtensionFilter filter = new FileNameExtensionFilter(".doc", "doc");
							chooser.setFileFilter(filter);
							int result = chooser.showOpenDialog(frame);
							if(result == JFileChooser.APPROVE_OPTION){
								  file = chooser.getSelectedFile();
						}
					}
					}
					}
				});


				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 2;
				gridBagConstraints.gridwidth = 1;
				panel.add(liulan, gridBagConstraints);

				gridBagConstraints.gridx = 1;
				gridBagConstraints.gridy = 2;
				gridBagConstraints.gridwidth = 1;
				newBianji.setPreferredSize(new Dimension(70, 23));
				newBianji.setMinimumSize(new Dimension(70, 23));
				newBianji.setMaximumSize(new Dimension(70, 23));
				panel.add(newBianji,gridBagConstraints);

				newBianji.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						 if (file==null) {
		                    	JOptionPane.showMessageDialog(frame, "请先选择主要内容！", "提示",JOptionPane.INFORMATION_MESSAGE);
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

	public static String getJsxyNumber() {
		return jsxyNumber;
	}
	public static File getFile() {
		return file;
	}
}
