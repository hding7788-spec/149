package com.glaway.mpm.qmIntf.participatePart;

import com.glaway.mpm.qmIntf.fittingTool.view.FittingsDistributionFrame;
import com.glaway.mpm.task.CmTaskExecutor;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaGuiUtil;
import com.glaway.mpm.visual.view.VaContext;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.pview.VaPViewImpl;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.tree.VaTreeSelectionListener;
import com.glaway.mpm.visual.view.ui.VaEBomTreePanel;
import com.glaway.mpm.visual.view.ui.VaPVNotInstalledPanel;
import com.glaway.mpm.visual.view.ui.VaPViewScenesPanel;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.method.RemoteMethodServer;

import javax.media.j3d.BoundingBox;
import javax.swing.*;
import javax.vecmath.Point3d;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class ParticipatePartAddDialog extends JFrame implements CmTaskExecutor{

	private static final long serialVersionUID = 1L;
	// for CmTaskExecutor -> start
	private volatile boolean executorActive;
	// for CmTaskExecutor -> end
	private static final VaLogger log = VaLogger.getLogger(VaTreeSelectionListener.class);
	private static ParticipatePartAddDialog frame = null;
	private static VaActionProgressBar animFrame = null;
	public static final int TEXTFIELD_SIZE = 15;
	public static final int TEXTFIELD_SIZE2 = 30;
	private String curPartNumber;
	private static FittingsDistributionFrame  frameFit;
	public static List<String> morePartList;

	public String getCurPartNumber() {
		return curPartNumber;
	}

	public void setCurPartNumber(String curPartNumber) {
		this.curPartNumber = curPartNumber;
	}

	public static void showDialog(Map<String, String> map1,final NewTechnicsPart parentFrame) {


//		JLabel imageLbl = new JLabel(new ImageIcon(ParticipatePartAddDialog.class.getResource("/image/wait.gif")));
//		JProgressBar pb = new JProgressBar();
//		pb.setIndeterminate(true);
//		progressBar.setLayout(new BorderLayout());
////		progressBar.add(imageLbl,BorderLayout.CENTER);
//		progressBar.getContentPane().add(new JButton("test"),BorderLayout.CENTER);
//		progressBar.setBounds(VaGuiUtil.getScreenCenter(166,66));
//		progressBar.getContentPane().setBackground(Color.black);
//		progressBar.setVisible(true);
		final HashMap<String, String> map = (HashMap<String, String>)map1;
		Thread runThread = new Thread(){
			public void run(){
				log.debug("Input Map:" + map);
				log.debug("XML path:" + map.get("xmlPath"));
				if (map != null && map.get("partNumber") != null) {
					VaContext.setCurrentPartNumber(map.get("partNumber"));
					VaContext.setCurrentPartOid(map.get("oid"));
					VaContext.setPbomSaved(map.get("isStructrueSaved"));
				}
//				ParticipatePartAddDialog.getInstance().setBounds(VaGuiUtil.getScreenCenter(1000, 600));
				if(frameFit != null && frameFit.isVisible()){
					JOptionPane.showMessageDialog(parentFrame, "参装工具只能开启一个，请关闭上次开启的参装工具!");
					animFrame.finish();
					animFrame.dispose();
					return;
				}
				else if(frameFit != null && !frameFit.isVisible()){
					frameFit.closeWindow();
					frameFit = null;
				}
				frameFit = new FittingsDistributionFrame(map, parentFrame);
				frameFit.setBounds(VaGuiUtil.getScreenCenter(frameFit.getWidth(),frameFit.getHeight()));
				frameFit.setVisible(true);
				morePartList = FittingsDistributionFrame.checkIsHasMoreParts();
//				List<String> moreHasEpmPart = FittingsDistributionFrame.checkIsHasMoreParts();
//				if(moreHasEpmPart.size() > 0){
//					JOptionPane.showMessageDialog(null, moreHasEpmPart.toString() + "多装");
//				}
//				VaTreeNode root = ParticipatePartAddDialog.getInstance().jScrollPane1.getTree().getRoot();
//				ParticipatePartAddDialog.getInstance().setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
//				if (map != null && map.get("xmlPath") != null) {
//					String filePath = map.get("xmlPath");
//					ParticipatePartAddDialog.getInstance().setUsedNodeTree(root, ParticipatePartAddDialog.getInstance().getPPartFromXML(filePath));
//				}
//				ParticipatePartAddDialog.getInstance().setVisible(true);
//				ParticipatePartAddDialog.getInstance().jScrollPane1.getTree().repaint();
				animFrame.finish();
				animFrame.dispose();
			}
		};


		animFrame = new VaActionProgressBar(frameFit, "加载装配工具","加载装配工具","正在加载装配工具，请稍候...");
		runThread.start();
		animFrame.setVisible(true);
	}

	public static synchronized ParticipatePartAddDialog getInstance() {
		if (frame == null) {
			frame = new ParticipatePartAddDialog();
			frame.setSize(1000, 600);
//			frame.setAlwaysOnTop(true);
		}
		return frame;
	}

	/**
	 * Creates new form VaMainframe
	 */
	private ParticipatePartAddDialog() {
		this.setEnabled(false);
		initComponents();
		this.setEnabled(true);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {

		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel4 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();

		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		jLabel10 = new javax.swing.JLabel();

		jLabel11 = new javax.swing.JLabel();
		jLabel12 = new javax.swing.JLabel();
		jLabel13 = new javax.swing.JLabel();

		jTextField1 = new javax.swing.JTextField(TEXTFIELD_SIZE);
		jTextField2 = new javax.swing.JTextField(TEXTFIELD_SIZE);
		jTextField3 = new javax.swing.JTextField(TEXTFIELD_SIZE);
		jTextField4 = new javax.swing.JTextField(TEXTFIELD_SIZE);
		jTextField5 = new javax.swing.JTextField(TEXTFIELD_SIZE);
		jTextField6 = new javax.swing.JTextField(TEXTFIELD_SIZE);
		jTextField7 = new javax.swing.JTextField(TEXTFIELD_SIZE2);
		jTextField8 = new javax.swing.JTextField(TEXTFIELD_SIZE2);
		jTextField9 = new javax.swing.JTextField(TEXTFIELD_SIZE2);

		jTextField11 = new javax.swing.JTextField(TEXTFIELD_SIZE);
		jTextField12 = new javax.swing.JTextField(TEXTFIELD_SIZE);
		jTextField13 = new javax.swing.JTextField(TEXTFIELD_SIZE);

		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jButton3 = new javax.swing.JButton();

		jPanel5 = new javax.swing.JPanel();
		jPanel6 = new javax.swing.JPanel();
		jPanel7 = new javax.swing.JPanel();
		jPanel1 = new javax.swing.JPanel();

		jSplitPane1 = new javax.swing.JSplitPane();

		jScrollPane1 = new VaEBomTreePanel(this);// new
		// javax.swing.JScrollPane();

		jPanel3 = new javax.swing.JPanel();
		// jPanel2 = new //javax.swing.JPanel();
		/** init pview **/

		if (VaPViewImpl.isPviewInitialized()) {
			Thread createPPRunner = new Thread() {
				public void run() {
					try {
						jPanel2 = new VaPViewScenesPanel(VaPViewFactory.PV_NAME_MBOM,null);

						jSplitPane1.setRightComponent(jPanel2);

					} catch (Exception ex) {
						ex.printStackTrace();
					}

				}
			};
			createPPRunner.start();
		} else
			jSplitPane1.setRightComponent(new VaPVNotInstalledPanel());
		// setBorder(new EmptyBorder(0, 0, 0, 0));

		jTabbedPane4 = new javax.swing.JTabbedPane();

		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		// setPreferredSize(new java.awt.Dimension(800, 600));

//		jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
//		jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
//		jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

		// jTextField1.setText("jTextField1");
		jLabel1.setText("X1");

		jLabel2.setText("X2");

		// jTextField2.setText("jTextField2");

		jLabel3.setText("Y1");

		jLabel4.setText("Y2");

		// jTextField3.setText("jTextField3");

		// jTextField4.setText("jTextField4");

		// jTextField5.setText("jTextField5");

		// jTextField6.setText("jTextField6");

		jLabel5.setText("Z1");

		jLabel6.setText("Z2");

		jButton1.setText("搜寻");
		jButton2.setText("搜寻");
		jButton3.setText("搜寻");

		jLabel7.setText("参考");
		jLabel8.setText("实例");
		jLabel9.setText("距离");

		jLabel11.setText("零件编号");
		jLabel12.setText("零件名称");
		jLabel13.setText("零件版本");

		jButton1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButtonPerformed(e);
			}
		});
		jButton2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButtonPerformed(e);
			}
		});
		jButton3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchButtonPerformed(e);
			}
		});

		jLabel10.setText("mm");

		javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
		jPanel7.setLayout(jPanel7Layout);
		jPanel7Layout.setHorizontalGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel7Layout.createSequentialGroup().addContainerGap().addGroup(
								jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
										jPanel7Layout.createSequentialGroup().addComponent(jLabel9).addGap(18, 18, 18)
												.addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
														javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jLabel10).addPreferredGap(
														javax.swing.LayoutStyle.ComponentPlacement.RELATED, 456,
														Short.MAX_VALUE).addComponent(jButton2)).addGroup(
										jPanel7Layout.createSequentialGroup().addGroup(
												jPanel7Layout.createParallelGroup(
														javax.swing.GroupLayout.Alignment.LEADING).addGroup(
														jPanel7Layout.createSequentialGroup().addComponent(jLabel7)
																.addGap(18, 18, 18).addComponent(jTextField7,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
														.addGroup(
																jPanel7Layout.createSequentialGroup().addComponent(
																		jLabel8).addGap(18, 18, 18).addComponent(
																		jTextField8,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
												.addGap(0, 0, Short.MAX_VALUE))).addContainerGap(14,
								javax.swing.GroupLayout.PREFERRED_SIZE)));
		jPanel7Layout.setVerticalGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						jPanel7Layout.createSequentialGroup().addContainerGap().addGroup(
								jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
										jPanel7Layout.createSequentialGroup().addGroup(
												jPanel7Layout.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
														jLabel7).addComponent(jTextField7,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
												jPanel7Layout.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
														jLabel8).addComponent(jTextField8,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
												javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
												jPanel7Layout.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
														jLabel9).addComponent(jTextField9,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel10))
												.addGap(0, 0, Short.MAX_VALUE)).addGroup(
										javax.swing.GroupLayout.Alignment.TRAILING,
										jPanel7Layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
												.addComponent(jButton2))).addContainerGap()));

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setAutoCreateGaps(true);
		jPanel4Layout.setAutoCreateContainerGaps(true);

		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createSequentialGroup().addGroup(
				jPanel4Layout.createParallelGroup().addComponent(jLabel1).addComponent(jLabel2)).addGroup(
				jPanel4Layout.createParallelGroup().addComponent(jTextField1).addComponent(jTextField2)).addGroup(
				jPanel4Layout.createParallelGroup().addComponent(jLabel3).addComponent(jLabel4)).addGroup(
				jPanel4Layout.createParallelGroup().addComponent(jTextField4).addComponent(jTextField3)).addGroup(
				jPanel4Layout.createParallelGroup().addComponent(jLabel5).addComponent(jLabel6)).addGroup(
				jPanel4Layout.createParallelGroup().addComponent(jTextField5).addComponent(jTextField6))
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 300, Short.MAX_VALUE)
				.addComponent(jButton1));

		jPanel4Layout.setVerticalGroup(jPanel4Layout.createSequentialGroup().addGap(20).addGroup(
				jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1)
						.addComponent(jTextField1).addComponent(jLabel3).addComponent(jTextField3)
						.addComponent(jLabel5).addComponent(jTextField5)).addGap(20).addGroup(
				jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2)
						.addComponent(jTextField2).addComponent(jLabel4).addComponent(jTextField4)
						.addComponent(jLabel6).addComponent(jTextField6).addComponent(jButton1)));

		jTabbedPane1.addTab("空间范围", jPanel4);
		jTabbedPane1.addTab("临近空间", jPanel7);

		javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setAutoCreateGaps(true);
		jPanel5Layout.setAutoCreateContainerGaps(true);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout.createSequentialGroup().addGroup(
				jPanel5Layout.createParallelGroup().addComponent(jLabel11).addComponent(jLabel12)
						.addComponent(jLabel13)).addGroup(
				jPanel5Layout.createParallelGroup().addComponent(jTextField11).addComponent(jTextField12).addComponent(
						jTextField13)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 300,
				Short.MAX_VALUE).addComponent(jButton3));
		jPanel5Layout.setVerticalGroup(jPanel5Layout.createSequentialGroup().addGroup(
				jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel11)
						.addComponent(jTextField11)).addGroup(
				jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel12)
						.addComponent(jTextField12)).addGroup(
				jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel13)
						.addComponent(jTextField13).addComponent(jButton3)));

		jTabbedPane1.addTab("属性", jPanel5);

		// javax.swing.GroupLayout jPanel6Layout = new
		// javax.swing.GroupLayout(jPanel6);
		// jPanel6.setLayout(jPanel6Layout);
		// jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		// .addGap(0, 819, Short.MAX_VALUE));
		// jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		// .addGap(0, 100, Short.MAX_VALUE));

		// jTabbedPane1.addTab("其它", jPanel6);

		// javax.swing.GroupLayout jPanel3Layout = new
		// javax.swing.GroupLayout(jPanel3);
		// jPanel3.setLayout(jPanel3Layout);
		// jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		// .addGap(0, 100, Short.MAX_VALUE));
		// jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		// .addGap(0, 365, Short.MAX_VALUE));

		// jScrollPane1.setViewportView(jPanel3);

		jSplitPane1.setLeftComponent(jScrollPane1);

		// javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
		// jPanel2);
		// jPanel2.setLayout(jPanel2Layout);
		// jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(
		// javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 790,
		// Short.MAX_VALUE));
		// jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(
		// javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 356,
		// Short.MAX_VALUE));

		// ////

		// /////
		// jSplitPane1.setRightComponent(jPanel2);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE).addGroup(
						jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
								jPanel1Layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(
										jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 367,
										javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE))));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE).addGroup(
						jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
								jPanel1Layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(
										jTabbedPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 68,
										javax.swing.GroupLayout.PREFERRED_SIZE).addGap(0, 0, Short.MAX_VALUE))));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addGroup(
						layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addComponent(jTabbedPane1)).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				layout.createSequentialGroup().addContainerGap().addComponent(jTabbedPane1,
						javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel1,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE).addContainerGap()));

		pack();
	}// </editor-fold>

	public void setX1(String x1) {
		jTextField1.setText(x1);
	}

	public String getX1() {
		return jTextField1.getText();
	}

	public void setX2(String x2) {
		jTextField2.setText(x2);
	}

	public String getX2() {
		return jTextField2.getText();
	}

	public void setY1(String x1) {
		jTextField3.setText(x1);
	}

	public String getY1() {
		return jTextField3.getText();
	}

	public void setY2(String x2) {
		jTextField4.setText(x2);
	}

	public String getY2() {
		return jTextField4.getText();
	}

	public void setZ1(String x1) {
		jTextField5.setText(x1);
	}

	public String getZ1() {
		return jTextField5.getText();
	}

	public void setZ2(String x2) {
		jTextField6.setText(x2);
	}

	public String getZ2() {
		return jTextField6.getText();
	}

	public void setReferenceString(String str) {
		jTextField7.setText(str);
	}

	public String getReferenceString() {
		return jTextField7.getText();
	}

	public void setInstanceString(String str) {
		jTextField8.setText(str);
	}

	public String getInstanceString() {
		return jTextField8.getText();
	}

	public void setDistanceString(String str) {
		jTextField9.setText(str);
	}

	public String getDistanceString() {
		return jTextField9.getText();
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {
		/*
		 * Set the Nimbus look and feel
		 */
		// <editor-fold defaultstate="collapsed"
		// desc=" Look and feel setting code (optional) ">
		/*
		 * If Nimbus (introduced in Java SE 6) is not available, stay with the
		 * default look and feel. For details see
		 * http://download.oracle.com/javase
		 * /tutorial/uiswing/lookandfeel/plaf.html
		 */
		// try {
		// for (javax.swing.UIManager.LookAndFeelInfo info :
		// javax.swing.UIManager
		// .getInstalledLookAndFeels()) {
		// if ("Nimbus".equals(info.getName())) {
		// javax.swing.UIManager.setLookAndFeel(info.getClassName());
		// break;
		// }
		// }
		// } catch (ClassNotFoundException ex) {
		// java.util.logging.Logger.getLogger(VaMainframe.class.getName())
		// .log(java.util.logging.Level.SEVERE, null, ex);
		// } catch (InstantiationException ex) {
		// java.util.logging.Logger.getLogger(VaMainframe.class.getName())
		// .log(java.util.logging.Level.SEVERE, null, ex);
		// } catch (IllegalAccessException ex) {
		// java.util.logging.Logger.getLogger(VaMainframe.class.getName())
		// .log(java.util.logging.Level.SEVERE, null, ex);
		// } catch (javax.swing.UnsupportedLookAndFeelException ex) {
		// java.util.logging.Logger.getLogger(VaMainframe.class.getName())
		// .log(java.util.logging.Level.SEVERE, null, ex);
		// }
		// </editor-fold>

		/*
		 * Create and display the form
		 */
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin");
		// ParticipatePartAddDialog.showDialog(null, null);
//		ParticipatePartAddDialog.getInstance().setVisible(true);
//		HashMap map = new HashMap<String, String>();
//		map.put("oid", "584762");
//		map.put("partNumber", "AL2_907_1460");
////		map.put("oid", "584916");
////		map.put("partNumber", "AL2_850_760");
//		map.put("xmlPath", "C:\\Users\\Administrator\\Desktop\\AL2_850_760`波束选择板装配工艺`装配工艺`AL2_850_760`多基地面雷达.xml");

				JFrame fr = new JFrame();
		JButton btn = new JButton("test");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				HashMap map = new HashMap<String, String>();
				map.put("oid", "1530056");//1123638
//				map.put("oid", "1123638");
				map.put("partNumber", "AL2_907_1460");
				map.put("xmlPath",
						"C:\\Users\\wanghaoyu\\Desktop\\AL2_907_1460`副天线和差选束开关装配工艺`装配工艺`AL2_907_1460`多基地面雷达.xml");

				ParticipatePartAddDialog.showDialog(map,null);

			}
		});
		fr.add(btn);
		fr.setSize(400,300);
		fr.setVisible(true);

	}

	private void setUsedNodeTree(VaTreeNode node,Vector usedList){

		if (usedList != null && usedList.contains(node.getOccId())) {
			node.setUsed(true);
		}
		else{
			node.setUsed(false);
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			VaTreeNode child = (VaTreeNode) node.getChildAt(i);
			setUsedNodeTree(child, usedList);
		}
	}

	private Vector getPPartFromXML(String filePath) {
		try {
			Vector v = new Vector();
			SAXReader reader = new SAXReader();
			InputStream is = new FileInputStream(filePath);
			org.dom4j.Document document = reader.read(is);

			// 获得节点的命名空间
			String uri = document.getRootElement().getNamespaceURI();
			// 将uri存入map中
			HashMap map = new HashMap();
			map.put("xx", uri);

			org.dom4j.XPath xpath = DocumentHelper
					.createXPath("/xx:technics/xx:QMFawTechnicsInfo/xx:steps/xx:QMProcedureInfo/xx:paces/xx:QMProcedureInfo/xx:parts/xx:QMPartInfo");
			xpath.setNamespaceURIs(map);

			List list = xpath.selectNodes(document);
			for (int i = 0; i < list.size(); i++) {
				// Node node = (Node)list.get(i);//转型为Node
				Element e = (Element) list.get(i);// 转型为Element
				log.debug(e.attributeValue("occId"));
				if (!e.attributeValue("occId").trim().equals("")) {
					String[] occIds = e.attributeValue("occId").split(",");
					for (int j = 0; j < occIds.length; j++) {
						v.add(occIds[j]);
					}
				}

			}
			log.debug(v);
			return v;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public void searchButtonPerformed(ActionEvent e) {
		log.debug("searchButtonPerformed");
		VaTreeNode root = jScrollPane1.getTree().getRoot();
		if (e.getSource().equals(jButton1)) {
			log.debug("jButton1 search");
			String x1s = getX1();
			String x2s = getX2();
			String y1s = getY1();
			String y2s = getY2();
			String z1s = getZ1();
			String z2s = getZ2();

			if (isSpace(x1s) && isSpace(x2s) && isSpace(y1s) && isSpace(y2s) && isSpace(z1s) && isSpace(z2s)) {
				log.debug("空间范围坐标值全部为空，不进行检索操作！");
				return;
			}

			Point3d lower = new Point3d();
			Point3d upper = new Point3d();
			lower.setX(Double.valueOf(isSpace(x1s) ? "0.0" : x1s) / 1000.0);
			lower.setY(Double.valueOf(isSpace(y1s) ? "0.0" : y1s) / 1000.0);
			lower.setZ(Double.valueOf(isSpace(z1s) ? "0.0" : z1s) / 1000.0);
			upper.setX(Double.valueOf(isSpace(x2s) ? "0.0" : x2s) / 1000.0);
			upper.setY(Double.valueOf(isSpace(y2s) ? "0.0" : y2s) / 1000.0);
			upper.setZ(Double.valueOf(isSpace(z2s) ? "0.0" : z2s) / 1000.0);

			BoundingBox bbox = new BoundingBox();
			bbox.setLower(lower);
			bbox.setUpper(upper);

			List<BoundingBox> bboxs = new ArrayList<BoundingBox>();
			bboxs.add(bbox);
			check(root, bboxs);

		} else if (e.getSource().equals(jButton2)) {
			log.debug("jButton2 search");
			String referenceStr = getReferenceString();
			String instanceStr = getInstanceString();
			String distanceStr = getDistanceString();

			List<BoundingBox> bboxs = new ArrayList<BoundingBox>();
			getBondingBoxes(root, referenceStr, instanceStr, bboxs);
			double distance = 0.0;
			boolean flag = false;
			if (distanceStr != null && distanceStr.trim().length() > 0) {
				distance = Double.valueOf(distanceStr) / 1000.0;
				flag = true;
			}
			if (flag) {
				for (BoundingBox box : bboxs) {
					Point3d lower = new Point3d();
					Point3d upper = new Point3d();
					box.getLower(lower);
					box.getUpper(upper);

					lower.setX(lower.getX() - distance);
					lower.setY(lower.getY() - distance);
					lower.setZ(lower.getZ() - distance);

					upper.setX(upper.getX() + distance);
					upper.setY(upper.getY() + distance);
					upper.setZ(upper.getZ() + distance);

					box.setLower(lower);
					box.setUpper(upper);
				}
			}
			check(root, bboxs);
		} else if (e.getSource().equals(jButton3)) {
			log.debug("jButton3 search");
			String partNum = this.jTextField11.getText().trim();
			String partName = this.jTextField12.getText().trim();
			String partVer = this.jTextField13.getText().trim();
			log.debug("input " + partNum + "," + partName + "," + partVer);
			if (partNum.equals("") && partName.equals("") && partVer.equals("")) {
				JOptionPane.showMessageDialog(null, "请输入查询条件!");
				return;
			}

			checkAttr(root, partNum, partName, partVer);
		}
		jScrollPane1.getTree().repaint();
	}

	private void getBondingBoxes(VaTreeNode node, String referenceStr, String instanceStr, List<BoundingBox> bboxs) {
		boolean isInstanceStrNull = true;
		if (instanceStr != null && instanceStr.trim().length() > 0) {
			isInstanceStrNull = false;
		}
		if (node.getPart().getNumber().equals(referenceStr)) {
			Vector vec = node.getBboxes();
			if (!isInstanceStrNull) {
				if (node.getOccId().equals(instanceStr)) {
					if (vec != null && vec.size() >= 1) {
						BoundingBox box = (BoundingBox) vec.get(0);
						if (box != null) {
							Point3d lower = new Point3d();
							Point3d upper = new Point3d();
							box.getLower(lower);
							box.getUpper(upper);
							BoundingBox boxtoAdd = new BoundingBox(lower, upper);
							bboxs.add(boxtoAdd);
						}
					}
				}
			} else {
				if (vec != null && vec.size() >= 1) {
					BoundingBox box = (BoundingBox) vec.get(0);
					if (box != null) {
						Point3d lower = new Point3d();
						Point3d upper = new Point3d();
						box.getLower(lower);
						box.getUpper(upper);
						BoundingBox boxtoAdd = new BoundingBox(lower, upper);
						bboxs.add(boxtoAdd);
					}
				}
			}
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			VaTreeNode temp = (VaTreeNode) node.getChildAt(i);
			getBondingBoxes(temp, referenceStr, instanceStr, bboxs);
		}
	}

	private void check(VaTreeNode node, List<BoundingBox> bboxs) {
		Vector vec = node.getBboxes();
		node.setSelected(false);
		if (vec != null) {
			BoundingBox tocheckbbox = (BoundingBox) vec.get(0);
			for (BoundingBox box : bboxs) {
				// if (tocheckbbox.intersect(box)) {
				if (box.intersect(tocheckbbox)) {
					node.setSelected(true);
				}
			}
		}
		int count = node.getChildCount();
		for (int i = 0; i < count; i++) {
			VaTreeNode child = (VaTreeNode) node.getChildAt(i);
			check(child, bboxs);
		}
	}

	private void checkAttr(VaTreeNode node, String pNumber, String pName, String pVersion) {

		node.setSelected(false);
		log.debug("node:" + node.getPart().getName() + "," + pNumber + "," + pName + "," + pVersion);
		log.debug("node info:" + node.getPart().getName() + "," + node.getPart().getNumber() + ","
				+ node.getPart().getVersion());

		boolean flag1 = node.getPart().getNumber().indexOf(pNumber) > -1 ? true : false;
		boolean flag2 = node.getPart().getName().indexOf(pName) > -1 ? true : false;
		boolean flag3 = node.getPart().getVersion().indexOf(pVersion) > -1 ? true : false;

		node.setSelected(flag1 && flag2 && flag3);

		int count = node.getChildCount();
		if(!(flag1 && flag2 && flag3))
		{
			for (int i = 0; i < count; i++) {
				VaTreeNode child = (VaTreeNode) node.getChildAt(i);
				checkAttr(child, pNumber, pName, pVersion);
			}
		}
	}

	private boolean isSpace(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JButton jButton3;

	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;

	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;

	private javax.swing.JPanel jPanel1;
	// private javax.swing.JPanel jPanel2;
	private VaPViewScenesPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JLabel jLabel10;

	// 属性搜索
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel12;
	private javax.swing.JLabel jLabel13;

	private VaEBomTreePanel jScrollPane1;
	private javax.swing.JSplitPane jSplitPane1;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JTabbedPane jTabbedPane4;
	private javax.swing.JTextField jTextField1;
	private javax.swing.JTextField jTextField2;
	private javax.swing.JTextField jTextField3;
	private javax.swing.JTextField jTextField4;
	private javax.swing.JTextField jTextField5;
	private javax.swing.JTextField jTextField6;
	private javax.swing.JTextField jTextField7;
	private javax.swing.JTextField jTextField8;
	private javax.swing.JTextField jTextField9;

	// 属性搜索
	private javax.swing.JTextField jTextField11;
	private javax.swing.JTextField jTextField12;
	private javax.swing.JTextField jTextField13;
	// End of variables declaration

	@Override
	public boolean isExecutorActive() {
		// TODO Auto-generated method stub
		return executorActive;
	}

	@Override
	public void setExecutorActive(boolean executorActive) {
		// TODO Auto-generated method stub
		this.executorActive = executorActive;
	}
}
