package com.glaway.mpm.qmIntf.dashboard;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observer;

import javax.swing.tree.TreePath;

import com.glaway.mpm.model.UnSDashboard;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class UnSDashboardTreeMouseAdapter extends MouseAdapter {
	private UnSDashboardTree epTree;
	public CommonObservable observable;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	public UnSDashboardTreeMouseAdapter(UnSDashboardTree epTree) {
		observable = new CommonObservable(6);
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
			if (object instanceof UnSDashboardNode) {
				UnSDashboardNode node = (UnSDashboardNode) object;
				if (e.getButton() == 3) {
					// if (node.getParent() instanceof EpTreeNode) {
					UnSDashboardTreeMouseAdapter.this.epTree.popupMenu.show(UnSDashboardTreeMouseAdapter.this.epTree, e.getX(), e.getY());
					// }
				} else {
					if (e.getClickCount() == 2) {
						UnSDashboard dashboard = node.getUnSDashboard();
						observable.setChanged();
						observable.notifyObservers(SwitchUtil.javaBeanToHashMap(dashboard));
					}
				}
			}
		}
	}
}