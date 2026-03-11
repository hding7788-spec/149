package com.glaway.mpm.flowchart;

import com.glaway.mpm.util.IconUtil;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

public class LinePopupMenu extends JPopupMenu {
	private TechnicsRouteJPanel panel;
	private SingleLineUnit unit;
	private JMenuItem addNoteLine = new JMenuItem("添加备注");

	private JMenuItem deleteLine = new JMenuItem("清除选中线段");

	public LinePopupMenu(TechnicsRouteJPanel panel) {
		this.panel = panel;

		add(this.addNoteLine);
		this.addNoteLine.setIcon(IconUtil
				.getImageIcon("/images/coding_update.gif"));
		this.addNoteLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new LinePopupMenu.NoteJDialog(LinePopupMenu.this.unit);
			}
		});
		addSeparator();
		add(this.deleteLine);
		this.deleteLine.setIcon(IconUtil
				.getImageIcon("/images/delete_edit.gif"));
		this.deleteLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LinePopupMenu.this.unit.deleteSelf(LinePopupMenu.this.panel);
				LinePopupMenu.this.panel.repaint();
			}
		});
	}

	public void setSingleLineUnit(SingleLineUnit unit) {
		this.unit = unit;
	}

	protected class NoteJDialog extends JDialog {
		private JTextField noteField = new JTextField();

		private JButton okButton = new JButton("确定");
		private JButton cancelButton = new JButton("取消");

		protected NoteJDialog(SingleLineUnit unit) {
			super();
			setTitle("提示");
			LinePopupMenu.this.unit = unit;

			Container container = getContentPane();
			container.setLayout(new GridBagLayout());

			JLabel label = new JLabel("添加备注");

			container.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D,
					13, 0, new Insets(10, 10, 5, 5), 0, 0));
			container.add(this.noteField, new GridBagConstraints(1, 0, 1, 1,
					1.0D, 0.0D, 17, 2, new Insets(10, 5, 5, 10), 0, 0));

			JPanel panel = new JPanel();
			container.add(panel, new GridBagConstraints(1, 3, 3, 1, 1.0D, 0.0D,
					10, 2, new Insets(10, 10, 10, 10), 0, 0));
			panel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0D,
					0.0D, 10, 2, new Insets(0, 0, 0, 0), 0, 0));
			this.okButton.setPreferredSize(new Dimension(80, 23));
			this.okButton.setMinimumSize(new Dimension(80, 23));
			this.okButton.setMaximumSize(new Dimension(80, 23));
			panel.add(this.okButton, new GridBagConstraints(0, 1, 1, 1, 0.0D,
					0.0D, 10, 0, new Insets(0, 5, 0, 5), 0, 0));
			this.cancelButton.setPreferredSize(new Dimension(80, 23));
			this.cancelButton.setMinimumSize(new Dimension(80, 23));
			this.cancelButton.setMaximumSize(new Dimension(80, 23));
			panel.add(this.cancelButton, new GridBagConstraints(0, 2, 1, 1,
					0.0D, 0.0D, 10, 0, new Insets(0, 5, 0, 5), 0, 0));

			this.noteField.setText(unit.note);

			this.okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LinePopupMenu.NoteJDialog.this.setUnitNote();
				}
			});
			this.cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					LinePopupMenu.NoteJDialog.this.dispose();
				}
			});
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			setBounds((int) (dimension.getWidth() - 300.0D) / 2,
					(int) (dimension.getHeight() - 110.0D) / 2, 300, 110);
			setVisible(true);
		}

		private void setUnitNote() {
			LinePopupMenu.this.unit.note = this.noteField.getText();
			LinePopupMenu.this.panel.repaint();
			dispose();
		}
	}
}
