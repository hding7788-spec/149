package com.glaway.mpm.qmIntf.frock;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observer;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class FkTreeMouseAdapter extends MouseAdapter {

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	private JTree tree;
	private CommonObservable observable;

	public FkTreeMouseAdapter(JTree tree) {
		observable = new CommonObservable(2);
		this.tree = tree;
	}

	protected void addObserver(Observer o) {
		observable.addObserver(o);
		logger.debug(o + " has signed to fk");
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
				if (object instanceof FkNode) {
					observable.setChanged();
					observable.notifyObservers(SwitchUtil
							.javaBeanToHashMap(((FkNode) object).getFrock()));
				}
			}
		}
	}
}