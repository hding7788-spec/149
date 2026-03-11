package com.glaway.mpm.qmIntf.symbol;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.glaway.mpm.visual.log.VaLogger;

public class SymbolAddPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private VaLogger logger = VaLogger.getLogger(this.getClass());
	private JPanel rightPanel = new JPanel();
	private SymbolPanel symbolPanel = new SymbolPanel();

	private JButton sureButton = new JButton("插入");
	private JButton cancelButton = new JButton("取消");

	private SymbolAddDialog dialogPanel;
	private JDialog dialog;

	public SymbolAddPanel(SymbolAddDialog dialogPanel, JDialog dialog) {
		this.dialogPanel = dialogPanel;
		this.dialog = dialog;
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

	}

	private void initDimension() {

	}

	private void initComponents() {
	}

	private void initLayout() {
		this.setLayout(new GridBagLayout());

		this.setBorder(new TitledBorder(null, "选择符号",
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));
		symbolPanel.setPreferredSize(new Dimension(220, 150));
		sureButton.setPreferredSize(new Dimension(60, 25));
		cancelButton.setPreferredSize(new Dimension(60, 25));
		rightPanel.setPreferredSize(new Dimension(130, 150));

		rightPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(5, 0, 5, 5);
		c.gridx = 1;
		c.gridy = 1;
		this.add(symbolPanel, c);
		c.insets = new Insets(25, 235, 5, 5);
		this.add(sureButton, c);
		c.insets = new Insets(60, 235, 5, 5);
		this.add(cancelButton, c);
	}

	private void initActions() {
		sureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = symbolPanel.jTable.getSelectedRow();
				int column = symbolPanel.jTable.getSelectedColumn();
				logger.debug("row=" + row + "column=" + column);
				if (row != -1 && column != -1) {
					Object obj = symbolPanel.jTable.getValueAt(row, column);
					dialogPanel.returnStr = obj == null ? null : obj.toString();
					dialog.dispose();
				}
			}
		});
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dialogPanel.returnStr = null;
				dialog.dispose();
			}
		});
	}

	private void loadInitDatas() {
	}

}