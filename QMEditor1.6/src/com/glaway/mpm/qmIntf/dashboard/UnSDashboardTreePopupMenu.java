package com.glaway.mpm.qmIntf.dashboard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.glaway.mpm.model.UnSDashboard;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;

public class UnSDashboardTreePopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;
	private UnSDashboardTree epTree;
	private JMenuItem addEpMenu = new JMenuItem("增加");
	private CommonObservable observable;

	public UnSDashboardTreePopupMenu(UnSDashboardTree epTree, CommonObservable observable) {
		this.observable = observable;
		this.epTree = epTree;
		this.add(addEpMenu);
		initAction();
	}

	private void initAction() {
		addEpMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath path = UnSDashboardTreePopupMenu.this.epTree.getSelectionPath();
				Object obj = path.getLastPathComponent();
				if ((obj instanceof UnSDashboardNode)) {
					UnSDashboardNode node = (UnSDashboardNode) obj;
					UnSDashboard dashboard = node.getUnSDashboard();
					observable.setChanged();
					observable.notifyObservers(SwitchUtil.javaBeanToHashMap(dashboard));
				}
			}
		});
	}
}
