package com.glaway.mpm.qmIntf.resourceTree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Observer;

import javax.swing.tree.TreePath;

import com.glaway.mpm.model.KnifeTool;
import com.glaway.mpm.qmIntf.resourceTree.model.KtNode;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;
import com.glaway.mpm.visual.log.VaLogger;

public class KtTreeMouseAdapter extends MouseAdapter {
	private CommonObservable observable;
	private KtTree ktTree;
	private VaLogger logger = VaLogger.getLogger(this.getClass());

	public KtTreeMouseAdapter(KtTree ktTree) {
		observable = new CommonObservable(4);
		this.ktTree = ktTree;
	}

	public void addObserver(Observer o) {
		observable.addObserver(o);
	}

	public void deleteObservers() {
		observable.deleteObservers();
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int row = ktTree.getRowForLocation(x, y);
		TreePath path = ktTree.getPathForRow(row);
		if (path != null) {
			Object value = path.getLastPathComponent();
			if(value instanceof KtNode) {
				if (e.getClickCount() == 2) {
					KtNode node = (KtNode) value;
					KnifeTool knifeTool = node.getKnifeTool();
					observable.setChanged();
					Map<String, String> map = SwitchUtil.toolToHashMap(knifeTool, "knifeTool");
					map.put("toolType", "刀具");
					logger.debug("knife tool map=" + map);
					observable.notifyObservers(map);
				}
			}
		}
	}
}
