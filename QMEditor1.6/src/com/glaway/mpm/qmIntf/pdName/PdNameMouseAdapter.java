package com.glaway.mpm.qmIntf.pdName;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.visual.log.VaLogger;

public class PdNameMouseAdapter extends MouseAdapter {
	private JTree tree;
	private CommonObservable observable;
	private VaLogger logger = VaLogger.getLogger(this.getClass());

	public PdNameMouseAdapter(JTree tree) {
		observable = new CommonObservable(4);
		this.tree = tree;
	}

	public void addObserver(Observer o) {
		observable.addObserver(o);
		logger.debug(o + " has signed to tool");
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
//				if (object instanceof ShopTypeNode) {
//					Map<String, String> map = new HashMap<String, String>();
//					ShopTypeNode shopTypeNode = (ShopTypeNode) object;
//
//					ShopType shopType = shopTypeNode.getShopType();
//
//					PdNameNode pdNameNode = (PdNameNode) shopTypeNode
//							.getParent();
//					PdName pdName = pdNameNode.getPdName();
//
//					WorkShopNode workShopNode = (WorkShopNode) pdNameNode
//							.getParent();
//					WorkShop workShop = workShopNode.getWorkShop();
//
//					map.put("pdName", pdName.getName());
//					map.put("shortCut", pdName.getShortcut());
//					map.put("shopTypeName", shopType.getName());
//					map.put("shopTypeNumber", shopType.getNumber());
//					map.put("workShopName", workShop.getName());
//					map.put("workShopNumber", workShop.getNumber());
//					logger.debug("map=" + map);
//					observable.setChanged();
//					observable.notifyObservers(map);
//				}
				if(object instanceof SkillNode){
					SkillNode node = (SkillNode)object;
					Map<String, String> map = new HashMap<String, String>();
//					map.put("pdName", pdName.getName());
//					map.put("shortCut", pdName.getShortcut());
//					map.put("shopTypeName", shopType.getName());
//					map.put("shopTypeNumber", shopType.getNumber());
//					map.put("workShopName", workShop.getName());
//					map.put("workShopNumber", workShop.getNumber());
					logger.debug("map=" + map);
					observable.setChanged();
					observable.notifyObservers(map);
				}else if(object instanceof PdNameNode){
					PdNameNode pdName = (PdNameNode)object;
					String name = pdName.getName();
//					Map<String, String> map = new HashMap<String, String>();
//					map.put("pdName", pdName.getName());
//					map.put("shortCut", pdName.getShortcut());
//					map.put("shopTypeName", shopType.getName());
//					map.put("shopTypeNumber", shopType.getNumber());
//					map.put("workShopName", workShop.getName());
//					map.put("workShopNumber", workShop.getNumber());
//					logger.debug("map=" + map);
					logger.debug("PdNameNode=" + name);
					observable.setChanged();
					observable.notifyObservers(name);
				}
			}
		}
	}
}