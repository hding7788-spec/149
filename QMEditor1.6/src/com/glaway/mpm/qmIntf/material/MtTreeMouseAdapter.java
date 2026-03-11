package com.glaway.mpm.qmIntf.material;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Observer;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.glaway.mpm.model.Material;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class MtTreeMouseAdapter extends MouseAdapter {
	private JTree tree;
	private CommonObservable observable;
	private VaLogger logger = VaLogger.getLogger(this.getClass());

	public MtTreeMouseAdapter(JTree tree) {
		observable = new CommonObservable(3);
		this.tree = tree;
	}

	public void addObserver(Observer o) {
		observable.addObserver(o);
		logger.debug(o + " has signed to material");
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
				if (object instanceof MtNode) {
					Material material = ((MtNode) object).getMaterial();
					Map<String, String> map = SwitchUtil.javaBeanToHashMap(material);
//					map.put("toolTip", MaterialUtil.getToolTipByNumber(material.getMaterialNumber()));
					map.put("toolTip", material.getNumber()+","+material.getNumber());
					map.put("materialType", "material");
					observable.setChanged();
					observable.notifyObservers(map);
					logger.debug("map= " + map);
				}
			}
		}
	}
}