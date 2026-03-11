package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.glaway.mpm.qmIntf.common.model.CommonLabel;
import com.glaway.mpm.qmIntf.common.model.CommonTextField;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class WorkItemSelectPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JPanel mainPanel = new JPanel();
	private JButton sureButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");

	public static Map<String, Object> map;

	private Map<String, List<String>> params;
	private List<String> keys = new ArrayList<String>();
	private Map<String, Object> values = new HashMap<String, Object>();

	private List<String> routes;
	private JComboBox routeComboBox;

	private JDialog dialog;

	public WorkItemSelectPanel(Map<String, List<String>> params,
			List<String> routes, JDialog dialog) {
		this.dialog = dialog;
		this.params = params;
		this.routes = routes;
		init();
	}

	private void init() {
		initLookAndFeel();
		initDimension();
		initComponents();
		initLayout();
		initActions();
		loadInitDatas();
	}

	private void initLookAndFeel() {
		setLayout(new BorderLayout());
	}

	private void initDimension() {

	}

	private void initComponents() {

	}

	private void initLayout() {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NORTHWEST;
		c.anchor = GridBagConstraints.NORTHWEST;
		// c.weighty = 1.0;
		// c.weightx = 1.0;
		mainPanel.setLayout(new GridBagLayout());

		int i = 1;
		int width = 0;
		if (params != null && params.size() != 0) {
			Set<Entry<String, List<String>>> set = params.entrySet();
			for (Entry<String, List<String>> entry : set) {
				c.insets = new Insets(10, 5, 5, 0);
				c.gridx = 1;
				c.gridy = i;
				String key = entry.getKey();
				List<String> value = entry.getValue();
				String labelDisplay = String.valueOf(value.get(0));
				CommonLabel label = new CommonLabel(labelDisplay);
				mainPanel.add(label, c);
				int width1 = label.getFontMetrics(getFont()).stringWidth(
						labelDisplay);
				if (width < width1) {
					width = width1;
				}
				keys.add(key);
				String type = String.valueOf(value.get(1));
				c.gridx = 2;
				c.insets = new Insets(10, 10, 5, 0);
				if ("String".equals(type)) {
					CommonTextField textField = new CommonTextField();
					values.put(key, textField);
					mainPanel.add(textField, c);
				} else if ("boolean".equals(type)) {
					JComboBox comboBox = SwingUtil
							.generateBooleanComboBox(null);
					values.put(key, comboBox);
					mainPanel.add(comboBox, c);
				}
				i++;
			}
		}
		if (routes != null && routes.size() != 0) {
			i++;
			c.insets = new Insets(10, 5, 5, 0);
			c.gridx = 1;
			c.gridy = i;
			JLabel label = new JLabel("路由");
			mainPanel.add(label, c);
			c.gridx = 2;
			c.insets = new Insets(10, 10, 5, 0);
			routeComboBox = SwingUtil.generateBooleanComboBox(routes);
			mainPanel.add(routeComboBox, c);
		}

		i++;
		c.insets = new Insets(35, 35, 5, 0);
		c.gridx = 1;
		c.gridy = i;
		mainPanel.add(sureButton, c);
		c.insets = new Insets(35, 110, 5, 0);
		mainPanel.add(cancelButton, c);

		Dimension dimension = new Dimension(width + 350, 45 * i + 35);
		dialog.setSize(dimension);
		dialog.setMinimumSize(dimension);
		JPanel panel = new JPanel();
		panel.add(mainPanel);
		this.add(panel, BorderLayout.WEST);
	}

	private void initActions() {
		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				map = new HashMap<String, Object>();
				if (keys != null && keys.size() != 0) {
					for (String key : keys) {
						Object obj = values.get(key);
						if (obj instanceof JComboBox) {
							JComboBox jComboBox = (JComboBox) obj;
							if (jComboBox.getItemCount() != 0) {
								String selectedItem = String.valueOf(jComboBox
										.getSelectedItem());
								if ("是".equals(selectedItem)) {
									map.put(key, Boolean.TRUE);
								} else {
									map.put(key, Boolean.FALSE);
								}
							}

						} else if (obj instanceof JTextField) {
							JTextField jTextField = (JTextField) obj;
							map.put(key, jTextField.getText());
						}
					}
				}
				if (routes != null && routes.size() != 0) {
					map.put("vote",
							String.valueOf(routeComboBox.getSelectedItem()));
				}
				dialog.dispose();
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				map = null;
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
	}

}