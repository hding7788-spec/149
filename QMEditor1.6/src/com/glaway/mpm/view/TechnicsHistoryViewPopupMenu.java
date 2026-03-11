package com.glaway.mpm.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.glaway.mpm.qmIntf.technics.TechnicsPreview;

import com.glaway.mpm.util.IconUtil;

public class TechnicsHistoryViewPopupMenu extends JPopupMenu implements
		ActionListener {

	public JMenuItem copy = new JMenuItem("复制");

	public JMenuItem reviewTechnics = new JMenuItem("工艺预览");

	private NewTechnicsHistoryView frame;

	private TechnicsTreePanel_View treePanel;

	public TechnicsHistoryViewPopupMenu(NewTechnicsHistoryView frame,
			TechnicsTreePanel_View treePanel) {
		super();
		this.frame = frame;
		this.treePanel = treePanel;

		setDefaultLightWeightPopupEnabled(false);
		setLightWeightPopupEnabled(false);

		add(copy);
		copy.addActionListener(this);

		add(reviewTechnics);
		reviewTechnics.addActionListener(this);

		initMenuItemIcon();
	}

	// 设置菜单项图标
	private void initMenuItemIcon() {
		copy.setIcon(IconUtil.getImageIcon(IconUtil.COPY));
		reviewTechnics.setIcon(IconUtil.getImageIcon(IconUtil.PREVIEW));
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == copy) {
			try {
				frame.copy();
			} catch (Exception e) {
				
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "复制工艺出现错误！", "提示",
						JOptionPane.INFORMATION_MESSAGE);
			}
		} else if (event.getSource() == reviewTechnics) {
			try {
				frame.newTechnicsHistorySelect.reviewTechnics();
			} catch (Exception e) {
				
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "复制工艺出现错误！", "提示",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	public void setMenuState(XWTreeObject xo) {
		if (xo == null) {
			copy.setEnabled(false);
		}
		if (xo instanceof XWTechnicsTreeObject) {
			copy.setEnabled(false);
		} else if (xo instanceof XWStepTreeObject) {
			copy.setEnabled(true);
		}
	}
}