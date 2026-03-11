package com.glaway.mpm.qmIntf.equipment;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observer;

import javax.swing.tree.TreePath;

import com.glaway.mpm.model.Equipment;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class EpTreeMouseAdapter extends MouseAdapter {
	private EpTree epTree;
	public CommonObservable observable;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	public EpTreeMouseAdapter(EpTree epTree) {
		observable = new CommonObservable(1);
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
			if (object instanceof EpNode) {
				EpNode node = (EpNode) object;
				if (e.getButton() == 3) {
					// if (node.getParent() instanceof EpTreeNode) {
					EpTreeMouseAdapter.this.epTree.popupMenu.show(EpTreeMouseAdapter.this.epTree, e.getX(), e.getY());
					// }
				} else {
					if (e.getClickCount() == 2) {
						Equipment equipment = node.getEquipment();
						observable.setChanged();
						observable.notifyObservers(SwitchUtil.javaBeanToHashMap(equipment));
					}
				}
			}
		}
	}
}