package com.glaway.mpm.sop.view;

import com.glaway.mpm.sop.intf.SopIntf;
import com.glaway.mpm.sop.model.SopResourceBean;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTreeNode;

import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.SopPartUtil;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import wt.part.WTPart;
import wt.util.WTException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class InputParametersDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private NewTechnicsPart frame;
	private SopParametersJPanel sopParametersJPanel;

	private JPanel panel = new JPanel();
	private JPanel panel0 = new JPanel();
	private JPanel panel2 = new JPanel();

	private JTextField number_Value = null;
	private JTextField specializedType_value = null;
	private JTextField parametersName_Value = null;
	private JTextField proceduceName_value = null;
	private JTextField materialCategory_value = null;
	private JTextField canshuzhi_value = null;
	private JTextField remark_value = null;
	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");

	public InputParametersDialog(NewTechnicsPart parent, SopParametersJPanel sopParametersJPanel) {
		super(parent, true);
		frame = parent;
		this.sopParametersJPanel = sopParametersJPanel;
		initComponent();
		loadInitData();
		setEditable();
		initDialog();

	}

	private void setEditable() {
		number_Value.setEditable(false);
		parametersName_Value.setEditable(true);
		specializedType_value.setEditable(false);
		proceduceName_value.setEditable(false);
		remark_value.setEditable(true);
		okButton.setVisible(true);
		cancelButton.setVisible(true);
		materialCategory_value.setEditable(true);
		setResizable(false);
	}

	private void initDialog() {
		setIconImage(frame.getIconImage());
		this.setTitle("手动录入参数项目");
		setTitle("手动录入参数项目");
		setVisible(true);
	}

	private void initComponent() {
		Container container = getContentPane();
		panel0.setLayout(new BorderLayout());
		panel.setLayout(new GridBagLayout());
		JScrollPane scrollPane = new JScrollPane(panel0);
		panel0.add(panel, BorderLayout.CENTER);
		container.add(scrollPane);
		panel0.add(panel2, BorderLayout.SOUTH);
		// 设置网格布局管理器参数
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 5, 0, 0);
		gridBagConstraints.gridwidth = 2;

		// SOP第一行：工艺文件编号
		JLabel technicsSOPNumberLabel = new JLabel("编号");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		panel.add(technicsSOPNumberLabel, gridBagConstraints);
		number_Value = new JTextField();
		number_Value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.insets = new Insets(5, 5, 0, 50);
		panel.add(number_Value, gridBagConstraints);

		// 第二行：专业类别
		final JLabel specializedType_label = new JLabel("专业类别");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 1;
		panel.add(specializedType_label, gridBagConstraints);
		specializedType_value = new JTextField();
		specializedType_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(specializedType_value, gridBagConstraints);

		// 第三行：参数项目名称
		JLabel name_label = new JLabel("参数项目名称");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 1;
		panel.add(name_label, gridBagConstraints);
		parametersName_Value = new JTextField();
		parametersName_Value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(parametersName_Value, gridBagConstraints);

		// 第四行：工序名称
		final JLabel proceduceName_label = new JLabel("工序名称");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 1;
		panel.add(proceduceName_label, gridBagConstraints);
		proceduceName_value = new JTextField();
		proceduceName_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(proceduceName_value, gridBagConstraints);

		// 第五行：物资类别
		final JLabel materialCategory_label = new JLabel("物资类别");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 1;
		panel.add(materialCategory_label, gridBagConstraints);
		materialCategory_value = new JTextField();
		materialCategory_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(materialCategory_value, gridBagConstraints);

		// 第六行：参数值
		final JLabel canshuzhi_label = new JLabel("参数值");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 1;
		panel.add(canshuzhi_label, gridBagConstraints);
		canshuzhi_value = new JTextField();
		canshuzhi_value.setPreferredSize(new Dimension(300, 23));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(canshuzhi_value, gridBagConstraints);

		// 第七行：备注
		final JLabel Remark_label = new JLabel("备注");
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 1;
		panel.add(Remark_label, gridBagConstraints);
		remark_value = new JTextField();
		remark_value.setPreferredSize(new Dimension(300, 46));
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.insets = new Insets(5, 5, 0, 5);
		panel.add(remark_value, gridBagConstraints);

		// 按钮面板
		panel2.setLayout(new GridBagLayout());
		panel2.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		okButton.setPreferredSize(new Dimension(70, 23));
		okButton.setMinimumSize(new Dimension(70, 23));
		okButton.setMaximumSize(new Dimension(70, 23));
		panel2.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 15, 15), 0, 0));
		cancelButton.setPreferredSize(new Dimension(70, 23));
		cancelButton.setMinimumSize(new Dimension(70, 23));
		cancelButton.setMaximumSize(new Dimension(70, 23));
		panel2.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 15, 15), 0, 0));

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (number_Value.getText() == null || "".equals(number_Value.getText())) {
						JOptionPane.showMessageDialog(frame, "编号不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if (parametersName_Value.getText() == null || "".equals(parametersName_Value.getText())) {
						JOptionPane.showMessageDialog(frame, "参数项目名称不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if (specializedType_value.getText() == null || "".equals(specializedType_value.getText())) {
						JOptionPane.showMessageDialog(frame, "专业类别不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					if (proceduceName_value.getText() == null || "".equals(proceduceName_value.getText())) {
						JOptionPane.showMessageDialog(frame, "工序名称不能为空！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					String name = parametersName_Value.getText().trim();
					HashMap<String, String> ibaMap = new HashMap<String, String>();
					ibaMap.put(SopConstants.SOP_IBA_PARAMETERSNAME, parametersName_Value.getText().trim());
					ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, specializedType_value.getText().trim());
					ibaMap.put(SopConstants.SOP_IBA_PROCEDUCENAME, proceduceName_value.getText().trim());
					List<WTPart> wtPartList = SopIntf.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK, null, name, null, ibaMap, true, SopConstants.SOP_TYPE_PARAMETERS);
					if (wtPartList != null && wtPartList.size() > 0) {
						JOptionPane.showMessageDialog(frame, "存在相同的专业类别、参数项目名称、工序名称和物资类别组合值，请重新指定创建！", "提示", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					createParameters();
				} catch (Exception ee) {
					ee.printStackTrace();
					JOptionPane.showMessageDialog(frame, "创建参数项目中出错！", "提示", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension2.getWidth() - 500) / 2, (int) (dimension2.getHeight() - (625)) / 2, 500, 550);
	}

	private void loadInitData() {
		Element technicsEle = frame.xwPartTreePanel.getSelectedTreeElement();
		String zylb = technicsEle.attributeValue("SpecializedType");
		String gxmc = technicsEle.attributeValue("ProceduceName");
		// String number = SopIntf.getLastestNumber(SopConstants.SOP_STR_CSXM,
		// SopConstants.SOP_TYPE_PARAMETERS, "");
		// 编号
		number_Value.setText("已生成");
		// 专业类别
		specializedType_value.setText(zylb);
		// 工序名称
		proceduceName_value.setText(gxmc);
	}

	/**
	 */
	private void createParameters() {
		SopResourceBean bean = new SopResourceBean();
		bean.setOid(System.currentTimeMillis() + "");
		bean.setName(parametersName_Value.getText());
		bean.setNumber(number_Value.getText());
		bean.setSpecializedType(specializedType_value.getText());
		bean.setProcedureName(proceduceName_value.getText());
		bean.setMaterialCategory(materialCategory_value.getText());
		bean.setCanshuzhi(canshuzhi_value.getText());
		bean.setDescription(remark_value.getText());
		bean.setIsNew("true");
		List<SopResourceBean> resourceBeanList = new ArrayList<SopResourceBean>();
		resourceBeanList.add(bean);
		String msg = sopParametersJPanel.setTableValues(resourceBeanList);
		if (!msg.isEmpty()) {
			JOptionPane.showMessageDialog(this, "参数项目[" + msg + "]已存在，未完成添加!");
		}
		this.dispose();
	}

	/**
	 * 将dialog屏幕居中显示
	 */
	public static void setMiddleOnScreenWithDialog(JDialog dialog) {
		int windowWidth = dialog.getWidth(); // 获得窗口宽
		int windowHeight = dialog.getHeight(); // 获得窗口高
		Toolkit kit = Toolkit.getDefaultToolkit(); // 定义工具包
		Dimension screenSize = kit.getScreenSize(); // 获取屏幕的尺寸
		int screenWidth = screenSize.width; // 获取屏幕的宽
		int screenHeight = screenSize.height; // 获取屏幕的高
		dialog.setLocation(screenWidth / 2 - windowWidth / 2, screenHeight / 2 - windowHeight / 2);// 设置窗口居中显示
	}

}