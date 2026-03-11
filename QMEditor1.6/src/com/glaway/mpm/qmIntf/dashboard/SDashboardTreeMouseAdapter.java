package com.glaway.mpm.qmIntf.dashboard;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observer;

import javax.swing.tree.TreePath;

import com.glaway.mpm.model.Dashboard;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class SDashboardTreeMouseAdapter extends MouseAdapter {
	private SDashboardTree epTree;
	public CommonObservable observable;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	public SDashboardTreeMouseAdapter(SDashboardTree epTree) {
		observable = new CommonObservable(5);
		this.epTree = epTree;
	}

	public void addObserver(Observer o) {
		observable.addObserver(o);
		logger.debug(o + " has signed to ep");
	}

	public void deleteObservers() {
		observable.deleteObservers();
		logger.debug("delete all obserbers");
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = epTree.getRowForLocation(x, y);
		TreePath path = epTree.getPathForRow(row);
		if (path != null) {
			Object object = path.getLastPathComponent();
			if (object instanceof SDashboardNode) {
				SDashboardNode node = (SDashboardNode) object;
				if (e.getButton() == 3) {
					SDashboardTreeMouseAdapter.this.epTree.popupMenu.show(SDashboardTreeMouseAdapter.this.epTree, e.getX(), e.getY());
				} else {
					if (e.getClickCount() == 2) {
						Dashboard dashboard = node.getDashboard();
						observable.setChanged();
						observable.notifyObservers(SwitchUtil.javaBeanToHashMap(dashboard));
					}
				}
			}
		}
	}
}