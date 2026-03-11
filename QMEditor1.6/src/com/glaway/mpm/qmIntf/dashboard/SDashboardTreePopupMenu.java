package com.glaway.mpm.qmIntf.dashboard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.glaway.mpm.model.Dashboard;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;

public class SDashboardTreePopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;
	private SDashboardTree epTree;
	private JMenuItem addEpMenu = new JMenuItem("增加");
	private CommonObservable observable;

	public SDashboardTreePopupMenu(SDashboardTree epTree, CommonObservable observable) {
		this.observable = observable;
		this.epTree = epTree;
		this.add(addEpMenu);
		initAction();
	}

	private void initAction() {
		addEpMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath path = SDashboardTreePopupMenu.this.epTree.getSelectionPath();
				Object obj = path.getLastPathComponent();
				if ((obj instanceof SDashboardNode)) {
					SDashboardNode node = (SDashboardNode) obj;
					Dashboard dashboard = node.getDashboard();
					observable.setChanged();
					observable.notifyObservers(SwitchUtil.javaBeanToHashMap(dashboard));
				}
			}
		});
	}
}
