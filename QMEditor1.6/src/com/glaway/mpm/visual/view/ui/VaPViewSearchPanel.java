package com.glaway.mpm.visual.view.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.media.j3d.BoundingBox;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import javax.vecmath.Point3d;

import com.glaway.mpm.task.CmTaskExecutorCallback;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.pview.VaPViewImpl;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.ptc.pview.dg.FBox;

public class VaPViewSearchPanel extends VaAbstractPanel {

	private static final long serialVersionUID = 1L;
	
	private VaPViewScenesPanel	scenesPanel;
	//private String				name;
	private JPanel				bboxSearchPanel;
	private JPanel				partSearchPanel;
	private JTabbedPane			tabPane;

	//private final int			textWidth	= 10;
	// bbox search field
	private JFormattedTextField	Xmin;
	private JFormattedTextField	Ymin;
	private JFormattedTextField	Zmin;
	private JFormattedTextField	Xmax;
	private JFormattedTextField	Ymax;
	private JFormattedTextField	Zmax;
	private JLabel				X1;
	private JLabel				Y1;
	private JLabel				Z1;
	private JLabel				X2;
	private JLabel				Y2;
	private JLabel				Z2;

	// partSearch field
	private JLabel				lblPartName;
	private JTextField			txtPartName;
	private JButton				btnSearch;
	private VaTreeNode			selNode;
	
	private VaActionProgressBar waitProgressBar = null;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		VaPViewSearchPanel p = new VaPViewSearchPanel("12");
		frame.add(p);
		frame.setSize(800, 600);
		frame.setVisible(true);
	}

	/**参装件上半区域CoreView*/
	public VaPViewSearchPanel(String name) {
		super();

		//this.name = name;
		scenesPanel = new VaPViewScenesPanel(name, this);
		tabPane = new JTabbedPane();
		this.setLayout(new BorderLayout());
		this.add(scenesPanel, BorderLayout.CENTER);
		try {
			registerTaskExecutor();
		} catch (CmTaskException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initSearchPanel();
		initPartSearchPanel();
		this.add(tabPane, BorderLayout.NORTH);
		tabPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				JTabbedPane p = (JTabbedPane) arg0.getSource();
				if (p.getSelectedIndex() == 0) {
					VaPViewFactory.getPViewImpl4MBOM().registerMarkupObs();
				} else {
					VaPViewFactory.getPViewImpl4MBOM().unregisterMarkupObs();
					VaPViewFactory.getPViewImpl4MBOM().removeBbox();
					resetSearchXYZ();
				}

			}
		});
	}

	private void initPartSearchPanel() {
		partSearchPanel = new JPanel();
		partSearchPanel.setLayout(new FlowLayout());
		lblPartName = new JLabel("零件名称：");
		txtPartName = new JTextField(20);
		btnSearch = new JButton("搜索");

		btnSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (txtPartName.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "请点选图形中的零件");
				} else {
					Thread partSearchRunner = new Thread() {
						@SuppressWarnings("unchecked")
						public void run() {
							try {
								VaPViewImpl impl = VaPViewFactory.getPViewImpl(VaPViewFactory.PV_NAME_MBOM);
								List<BoundingBox> boxes = new ArrayList<BoundingBox>();
								if (VaPViewSearchPanel.this.selNode != null) {
									if (VaPViewSearchPanel.this.selNode.isLeaf()) {
										boxes.add(impl.getBoundingBox(VaPViewSearchPanel.this.selNode.get_pviewComponentInstance().GetInstance().GetIDPath()));
//										boxes.add(VaPViewSearchPanel.this.selNode.get_pviewComponentNode()
//												.GetShapeSource().GetBBox());
										impl.searchPVInstance("part", boxes);
									} else {
										Enumeration<VaTreeNode> children = VaPViewSearchPanel.this.selNode
												.breadthFirstEnumeration();
										while (children.hasMoreElements()) {
											VaTreeNode child = children.nextElement();
											if (child.isLeaf()) {
												boxes.add(impl.getBoundingBox(VaPViewSearchPanel.this.selNode.get_pviewComponentInstance().GetInstance().GetIDPath()));
//												boxes.add(VaPViewSearchPanel.this.selNode.get_pviewComponentNode()
//														.GetShapeSource().GetBBox());
											}
										}
										impl.searchPVInstance("part", boxes);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};
					
					waitProgressBar = new VaActionProgressBar(null, "零件搜寻", "正在进行零件搜寻", "正在进行零件搜寻，请等待...");
					waitProgressBar.setVisible(true);
					partSearchRunner.start();
				}
			}
		});

		txtPartName.setText("");
		txtPartName.setEditable(false);

		partSearchPanel.add(lblPartName);
		partSearchPanel.add(txtPartName);
		partSearchPanel.add(btnSearch);

		tabPane.addTab("零件搜寻", partSearchPanel);
		tabPane.setVisible(false);
	}

	/**
	 * 
	 */
	private void initSearchPanel() {
		this.bboxSearchPanel = new JPanel();
		GridLayout gl = new GridLayout();

		gl.setRows(2);
		gl.setHgap(2);
		gl.setVgap(5);
		this.bboxSearchPanel.setLayout(gl);

		DecimalFormat decimalFormat = new DecimalFormat("0.00000000");
		NumberFormatter fmt = new NumberFormatter(decimalFormat);
		fmt.setAllowsInvalid(false);

		Xmin = new JFormattedTextField(fmt);
		Ymin = new JFormattedTextField(fmt);
		Zmin = new JFormattedTextField(fmt);
		Xmax = new JFormattedTextField(fmt);
		Ymax = new JFormattedTextField(fmt);
		Zmax = new JFormattedTextField(fmt);
		Xmin.setToolTipText("输数字后可使用+/-键设置正负");
		Ymin.setToolTipText("输数字后可使用+/-键设置正负");
		Zmin.setToolTipText("输数字后可使用+/-键设置正负");
		Xmax.setToolTipText("输数字后可使用+/-键设置正负");
		Ymax.setToolTipText("输数字后可使用+/-键设置正负");
		Zmax.setToolTipText("输数字后可使用+/-键设置正负");
		Xmin.setValue(0);
		Ymin.setValue(0);
		Zmin.setValue(0);
		Xmax.setValue(1);
		Ymax.setValue(1);
		Zmax.setValue(1);

		X1 = new JLabel("X1");
		Y1 = new JLabel("Y1");
		Z1 = new JLabel("Z1");
		X2 = new JLabel("X2");
		Y2 = new JLabel("Y2");
		Z2 = new JLabel("Z2");

		X1.setOpaque(true);
		X1.setHorizontalAlignment(JLabel.RIGHT);
		Y1.setHorizontalAlignment(JLabel.RIGHT);
		Z1.setHorizontalAlignment(JLabel.RIGHT);
		X2.setHorizontalAlignment(JLabel.RIGHT);
		Y2.setHorizontalAlignment(JLabel.RIGHT);
		Z2.setHorizontalAlignment(JLabel.RIGHT);

		X1.setPreferredSize(new Dimension(10, 10));
		Y1.setPreferredSize(new Dimension(20, 10));
		Z1.setPreferredSize(new Dimension(30, 10));
		X2.setPreferredSize(new Dimension(10, 10));
		Y2.setPreferredSize(new Dimension(20, 10));
		Z2.setPreferredSize(new Dimension(30, 10));

		JLabel sepLabel = new JLabel();
		sepLabel.setSize(60, 10);
		JLabel sepLabel2 = new JLabel();
		sepLabel2.setSize(60, 10);
		JButton btnCreBBox = new JButton("范围创建");
		JButton btnSearch = new JButton("搜寻");

		bboxSearchPanel.add(X1);
		bboxSearchPanel.add(Xmin);
		bboxSearchPanel.add(Y1);
		bboxSearchPanel.add(Ymin);
		bboxSearchPanel.add(Z1);
		bboxSearchPanel.add(Zmin);
		bboxSearchPanel.add(sepLabel);
		bboxSearchPanel.add(btnCreBBox);

		bboxSearchPanel.add(X2);
		bboxSearchPanel.add(Xmax);
		bboxSearchPanel.add(Y2);
		bboxSearchPanel.add(Ymax);
		bboxSearchPanel.add(Z2);
		bboxSearchPanel.add(Zmax);
		bboxSearchPanel.add(sepLabel2);
		bboxSearchPanel.add(btnSearch);
		// toolBar.add(X1);
		// toolBar.add(Xmin);
		// toolBar.add(Y1);
		// toolBar.add(Ymin);
		// toolBar.add(Z1);
		// toolBar.add(Zmin);
		// toolBar.add(X2);
		// toolBar.add(Xmax);
		// toolBar.add(Y2);
		// toolBar.add(Ymax);
		// toolBar.add(Z2);
		// toolBar.add(Zmax);

		btnCreBBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				VaPViewImpl impl = VaPViewFactory.getPViewImpl(VaPViewFactory.PV_NAME_MBOM);
				impl.createBoundingBox(VaPViewSearchPanel.this);
				VaPViewFactory.getPViewImpl4MBOM().registerMarkupObs();
			}
		});
		btnSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				VaPViewImpl impl = VaPViewFactory.getPViewImpl(VaPViewFactory.PV_NAME_MBOM);
				if (impl.getBbm() == null) {
					JOptionPane.showMessageDialog(null, "请先创建范围再进行搜索!");
					return;
				}
				
				Thread boxSearchRunner = new Thread() {
					public void run() {
						VaPViewImpl impl = VaPViewFactory.getPViewImpl(VaPViewFactory.PV_NAME_MBOM);
						impl.searchPVInstance("bbox", null);
					}
				};
				
		        waitProgressBar = new VaActionProgressBar(null, "空间搜寻", "正在进行空间搜寻", "正在进行空间搜寻，请等待...");
				waitProgressBar.setVisible(true);
				boxSearchRunner.start();
			}
		});
		tabPane.addTab("空间搜寻", bboxSearchPanel);
	}

	public BoundingBox getSearchBoxValue() {
		BoundingBox bb = new BoundingBox();
		Point3d p1 = new Point3d();
		Point3d p2 = new Point3d();
		p1.x = Double.parseDouble(this.Xmin.getValue() + "");
		p1.y = Double.parseDouble(this.Ymin.getValue() + "");
		p1.z = Double.parseDouble(this.Zmin.getValue() + "");
		p2.x = Double.parseDouble(this.Xmax.getValue() + "");
		p2.y = Double.parseDouble(this.Ymax.getValue() + "");
		p2.z = Double.parseDouble(this.Zmax.getValue() + "");
		bb.setLower(p1);
		bb.setUpper(p2);
		return bb;
	}

	public void setSearchBoxValue(BoundingBox bbox) {
		Point3d p1 = new Point3d();
		Point3d p2 = new Point3d();
		bbox.getLower(p1);
		bbox.getUpper(p2);
		this.Xmin.setValue(roundValue(p1.x));
		this.Ymin.setValue(roundValue(p1.y));
		this.Zmin.setValue(roundValue(p1.z));
		this.Xmax.setValue(roundValue(p2.x));
		this.Ymax.setValue(roundValue(p2.y));
		this.Zmax.setValue(roundValue(p2.z));
	}

	private double roundValue(double d) {
		BigDecimal b = new BigDecimal(d);
		double f1 = b.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue();
		return f1;
	}

	public void showSearch(boolean visible, String type) {

		tabPane.setVisible(visible);
		if(!visible){
			VaPViewFactory.getPViewImpl4MBOM().removeBbox();
			resetSearchXYZ();
		}
		this.updateUI();
	}

	@Override
	protected void initDimension() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initActions() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initComponents() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void initLayout() {
		// TODO Auto-generated method stub
		add(scenesPanel);
	}

	@Override
	protected void loadInitDatas() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {
		CmTaskHelper.registerTaskExecutor("VaPViewSearchPanel.setPartName", this, true);
		CmTaskHelper.registerTaskExecutor("VaPViewSearchPanel.closeWaitProgressBar", this, true);
	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	public void setPartName(Object sender, Object params, CmTaskExecutorCallback callback) {
		List<VaTreeNode> list = (List<VaTreeNode>) params;
		if (list != null && list.size() > 0 && list.size() == 1) {
			this.txtPartName.setText(list.get(0).toString());
			selNode = list.get(0);
		} else {
			this.txtPartName.setText("");
		}

	}

	/**
	 * 重置搜索的坐标
	 * @author sun_youfei
	 * @date  2013-11-23
	 *
	 */
	public void resetSearchXYZ(){
		Xmin.setValue(0);
		Ymin.setValue(0);
		Zmin.setValue(0);
		Xmax.setValue(1);
		Ymax.setValue(1);
		Zmax.setValue(1);
	}
	
	/**
	 * 空间搜寻和零件搜寻后关闭等待框
	 * @author sun_youfei
	 * @date  2013-11-28
	 * @param render
	 * @param params
	 * @param callback
	 *
	 */
	public void closeWaitProgressBar(Object render, Object params, CmTaskExecutorCallback callback) {
		if (waitProgressBar != null) {
			try {
				waitProgressBar.finish();
				waitProgressBar = null;
			} catch (Throwable tt) {
				tt.printStackTrace();
			} finally {
				waitProgressBar = null;
			}
		}
	}
}
