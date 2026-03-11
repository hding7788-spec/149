package com.glaway.mpm.parameter.designui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

public class NewCheckParamTablePanel2 extends JPanel{

	/**
	 * 检验记录表信息表
	 */
	private static final long serialVersionUID = 7308694843973540270L;

	private NewCheckParamTabbedPanel panel;
	private String dybbm_Value;
	private String bzjlx_Value;
	private String xmm_Value;
	private String tbm_Value;
	private String mxCs_Value;

	private JLabel label_1 = new JLabel("单元表表名:");
	private JLabel dybbm = new JLabel("");
	private JLabel label_2 = new JLabel("表主件类型:");
	private JLabel bzjlx = new JLabel("");
	private JLabel label_3 = new JLabel("项目名:");
	private JLabel xmm = new JLabel("");
	private JLabel label_4 = new JLabel("套表名:");
	private JLabel tbm = new JLabel("");
	private JLabel label_5 = new JLabel("每项/次数:");
	private JLabel mxcs = new JLabel("");
//	private JTable table = new JTable();
//	private DefaultTableModel tablemodel;
	private Insets insets = new Insets(0, 0, 0, 0);

	public NewCheckParamTablePanel2(String dybbm_Value, String bzjlx_Value, String xmm_Value, String tbm_Value, String mxCs_Value, NewCheckParamTabbedPanel panel, MouseAdapter mouseAdapter) {
		this.dybbm_Value = dybbm_Value;
		this.bzjlx_Value = bzjlx_Value;
		this.xmm_Value = xmm_Value;
		this.tbm_Value = tbm_Value;
		this.mxCs_Value = mxCs_Value;
		this.panel = panel;
	}

	public void setUIValues(){
		initComponents();
	}
	private void initComponents() {

		JPanel basicPanel = new JPanel();
		basicPanel.setLayout(new GridBagLayout());
		this.setBorder(new TitledBorder(null, "属性",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		//第一行
		//单元表表名
		label_1.setMaximumSize(new Dimension(130, 23));
		label_1.setMinimumSize(new Dimension(130, 23));
		label_1.setPreferredSize(new Dimension(130, 23));
		label_1.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_1, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));
		dybbm.setMaximumSize(new Dimension(130, 23));
		dybbm.setMinimumSize(new Dimension(130, 23));
		dybbm.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(dybbm, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		dybbm.setText(dybbm_Value);
		//表主键类型
		label_2.setMaximumSize(new Dimension(130, 23));
		label_2.setMinimumSize(new Dimension(130, 23));
		label_2.setPreferredSize(new Dimension(130, 23));
		label_2.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_2, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));
		bzjlx.setMaximumSize(new Dimension(130, 23));
		bzjlx.setMinimumSize(new Dimension(130, 23));
		bzjlx.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(bzjlx, new GridBagConstraints(3, 0, 1, 1, 1.0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
		bzjlx.setText(bzjlx_Value);
		//第二行
		//项目名
		label_3.setMaximumSize(new Dimension(130, 23));
		label_3.setMinimumSize(new Dimension(130, 23));
		label_3.setPreferredSize(new Dimension(130, 23));
		label_3.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_3, new GridBagConstraints(0, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));
		xmm.setMaximumSize(new Dimension(130, 23));
		xmm.setMinimumSize(new Dimension(130, 23));
		xmm.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(xmm, new GridBagConstraints(1, 1, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));
		xmm.setText(xmm_Value);
		//套表名
		label_4.setMaximumSize(new Dimension(130, 23));
		label_4.setMinimumSize(new Dimension(130, 23));
		label_4.setPreferredSize(new Dimension(130, 23));
		label_4.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_4, new GridBagConstraints(2, 1, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));
		tbm.setMaximumSize(new Dimension(130, 23));
		tbm.setMinimumSize(new Dimension(130, 23));
		tbm.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(tbm, new GridBagConstraints(3, 1, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));
		tbm.setText(tbm_Value);
		//第三行
		//每项/次数
		label_5.setMaximumSize(new Dimension(130, 23));
		label_5.setMinimumSize(new Dimension(130, 23));
		label_5.setPreferredSize(new Dimension(130, 23));
		label_5.setHorizontalAlignment(SwingConstants.RIGHT);
		basicPanel.add(label_5, new GridBagConstraints(0, 2, 1, 1, 0, 0,
				GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 5), 0, 0));
		mxcs.setMaximumSize(new Dimension(130, 23));
		mxcs.setMinimumSize(new Dimension(130, 23));
		mxcs.setPreferredSize(new Dimension(130, 23));
		basicPanel.add(mxcs, new GridBagConstraints(1, 2, 1, 1, 1.0,
				0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				insets, 0, 0));
		mxcs.setText(mxCs_Value);

//		JScrollPane scrollPane = new JScrollPane(basicPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

//		basicPanel.add(scrollPane);
		setLayout(new BorderLayout());
		add(basicPanel, BorderLayout.CENTER);




	}

}
