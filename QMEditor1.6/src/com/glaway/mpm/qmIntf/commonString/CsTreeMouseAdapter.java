package com.glaway.mpm.qmIntf.commonString;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observer;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.visual.log.VaLogger;

public class CsTreeMouseAdapter extends MouseAdapter {
	private JTree tree;
	private CommonObservable observable;
	private VaLogger logger = VaLogger.getLogger(this.getClass());

	public CsTreeMouseAdapter(JTree tree) {
		observable = new CommonObservable(0);
		this.tree = tree;
	}

	public void addObserver(Observer o) {
		observable.addObserver(o);
		logger.debug(o + " has signed to cs");
	}

	public void deleteObservers() {
		observable.deleteObservers();
		logger.debug("delete all obserbers");
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = tree.getRowForLocation(x, y);
		TreePath path = tree.getPathForRow(row);
		if (e.getClickCount() == 2) {
			if (path != null) {
				Object object = path.getLastPathComponent();
				if (object instanceof CsNode) {
					observable.setChanged();
					observable.notifyObservers(((CsNode) object).getValue());
				}
			}
		}
	}
}
