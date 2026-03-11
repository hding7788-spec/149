package com.glaway.mpm.qmIntf.equipment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.glaway.mpm.model.Equipment;
import com.glaway.mpm.model.Frock;
import com.glaway.mpm.qmIntf.frock.FrockCardDetailDialog;
import com.glaway.mpm.qmIntf.resourceTree.model.FkNode;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.SwitchUtil;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.wcIntf.ImageIntf;

public class EpTreePopupMenu extends JPopupMenu {

	private EpTree epTree;
	private JMenuItem addEpMenu = new JMenuItem("增加");
	private CommonObservable observable;

	public EpTreePopupMenu(EpTree epTree, CommonObservable observable) {
		this.observable = observable;
		this.epTree = epTree;
		this.add(addEpMenu);
		initAction();
	}

	private void initAction() {
		addEpMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath path = EpTreePopupMenu.this.epTree.getSelectionPath();
				Object obj = path.getLastPathComponent();
				if ((obj instanceof EpNode)) {
					EpNode node = (EpNode) obj;
					Equipment equipment = node.getEquipment();
					observable.setChanged();
					observable.notifyObservers(SwitchUtil
							.javaBeanToHashMap(equipment));
				}
			}
		});
	}
}
