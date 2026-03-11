package com.glaway.mpm.qmIntf.frock;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import com.glaway.mpm.qmIntf.common.model.CommonComboBox;
import com.glaway.mpm.util.JTableUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.LabelRender;

public class FrockCardInfoPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel mainPanel = new JPanel();

	public FrockTableInfo frockTableInfo = null;

	private JTable frockInfoTable = new JTable();

	private JPanel frockInfoPanel = new JPanel();

	private JLabel titleLabel = new JLabel();

	private JPanel topPanel = new JPanel();
	private JPanel imagePanel = new JPanel();
	private JPanel imageTopPanel = new JPanel();
	private JLabel imageLabel = new JLabel();

	private JPanel toolingRequirementsPanel = new JPanel();
	private JComboBox imageComboBox = new CommonComboBox();
	private JLabel title = new JLabel("工装设计要求:");
	private JTextArea toolingRequirements = new JTextArea(1, 10);

	private Dimension imageDimension = new Dimension(380, 260);

	private Map<String, byte[]> imagesMap;

	public FrockCardInfoPanel(Map<String, String> map) {
		frockTableInfo = new FrockTableInfo(map);
		frockTableInfo.setBorder(new TitledBorder(null, "详细信息",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		init();
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initLayout();
		initComponents();
		initActions();
		loadInitDatas();
	}

	private void initLookAndFeel() {

	}

	private void initDimension() {
		frockInfoPanel.setPreferredSize(new Dimension(700, 80));
		frockInfoPanel.setMinimumSize(new Dimension(700, 80));
	}

	private void initComponents() {
		this.frockInfoTable.setCellSelectionEnabled(true);
		frockInfoTable.setDefaultRenderer(Object.class, new LabelRender(true));
	}

	private void initLayout() {
		setLayout(new BorderLayout());
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(titleLabel, BorderLayout.NORTH);
		mainPanel.add(frockInfoPanel, BorderLayout.CENTER);

		frockInfoPanel.setLayout(new BorderLayout());
		frockInfoPanel.add(topPanel, BorderLayout.NORTH);
		frockInfoPanel.add(frockTableInfo, BorderLayout.CENTER);
		frockTableInfo.setBackground(imagePanel.getBackground());
		topPanel.setLayout(new BorderLayout());
		topPanel.add(imagePanel, BorderLayout.CENTER);
		topPanel.add(toolingRequirementsPanel, BorderLayout.EAST);

		JPanel panel = new JPanel();
		panel.setPreferredSize(imageDimension);
		panel.setMinimumSize(imageDimension);
		panel.add(imageLabel);

		imageTopPanel.setLayout(new BorderLayout());
		JPanel panel2 = new JPanel();
		panel2.add(new JLabel("图片："));
		panel2.add(imageComboBox);
		imageComboBox.setPreferredSize(new Dimension(100, 23));
		imageTopPanel.add(panel2, BorderLayout.WEST);

		imagePanel.setLayout(new BorderLayout());
		imagePanel.add(imageTopPanel, BorderLayout.NORTH);
		imagePanel.add(panel, BorderLayout.CENTER);

		toolingRequirementsPanel.setLayout(new BorderLayout());
		title.setMaximumSize(new Dimension(100, 23));
		toolingRequirementsPanel.add(title, BorderLayout.NORTH);
		toolingRequirementsPanel.add(toolingRequirements, BorderLayout.CENTER);

		toolingRequirements.setPreferredSize(imageDimension);
		toolingRequirements.setMinimumSize(imageDimension);
		toolingRequirements.setBackground(imagePanel.getBackground());
		// JPanel panel1 = new JPanel();
		// navigatePanel.setLayout(new BorderLayout());
		// navigatePanel.add(panel1, BorderLayout.EAST);
		// panel1.setLayout(new GridBagLayout());
		// GridBagConstraints c = new GridBagConstraints();
		// c.fill = GridBagConstraints.BOTH;
		// c.anchor = GridBagConstraints.NORTHEAST;
		// c.insets = new Insets(10, 0, 5, 0);
		// c.gridx = 1;
		// panel1.add(previousButton, c);
		// c.insets = new Insets(10, 0, 5, 0);
		// c.gridx = 2;
		// panel1.add(nextButton, c);
		// c.gridx = 3;
		// panel1.add(addButton, c);
	}

	private void loadInitDatas() {
		Object[][] tableValue = new Object[8][5];
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 5; j++) {
				tableValue[i][j] = "";
			}
		}
		frockInfoTable.setModel(JTableUtil.getModel(tableValue, new String[] {
				"", "", "", "", "" }));
		frockInfoTable.getTableHeader().setVisible(false);
	}

	private void initActions() {
		imageComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (imagesMap == null) {
					return;
				}
				String imageName = String.valueOf(imageComboBox
						.getSelectedItem());
				byte[] bytes = imagesMap.get(imageName);
				if (bytes == null) {
					imageLabel.setIcon(new ImageIcon());
				} else {
					imageLabel.setIcon(SwingUtil.scaleImage(
							new ImageIcon(bytes), imageDimension.width,
							imageDimension.height));
				}
			}
		});
	}

	public void initData(Map returnMap) {
		toolingRequirements.setText(String.valueOf(returnMap
				.get("toolingRequirements")));

		imageComboBox.removeAllItems();

		imagesMap = (Map<String, byte[]>) returnMap.get("fileBytes");

		if (imagesMap != null) {
			for (String imageName : imagesMap.keySet()) {
				imageComboBox.addItem(imageName);
			}
		}

	}
}