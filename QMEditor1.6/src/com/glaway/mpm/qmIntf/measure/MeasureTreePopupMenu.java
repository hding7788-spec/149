package com.glaway.mpm.qmIntf.measure;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.glaway.mpm.model.Frock;
import com.glaway.mpm.qmIntf.frock.FrockCardSearchDialog;
import com.glaway.mpm.qmIntf.resourceTree.model.FkNode;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class MeasureTreePopupMenu extends JPopupMenu {

	private NewTechnicsPart frame;
	private JMenuItem viewFrockCard = new JMenuItem("查看工装申请卡");
	private MeasureTree tree;

	public MeasureTreePopupMenu(NewTechnicsPart frame, MeasureTree tree) {
		this.frame = frame;
		this.tree = tree;
		this.add(viewFrockCard);
		initAction();
	}

	private void initAction() {
		viewFrockCard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TreePath path = MeasureTreePopupMenu.this.tree.getSelectionPath();
				Object obj = path.getLastPathComponent();
				if ((obj instanceof FkNode)) {
					FkNode node = (FkNode) obj;
					Frock frock = node.getFrock();
					if (frock.getFrockCardNum() != null
							&& !"".equals(frock.getFrockCardNum().trim())) {
						Map<String, String> map = new HashMap<String, String>();
						map.put("number", frock.getFrockCardNum());
						FrockCardSearchDialog dialog = new FrockCardSearchDialog(frame, null);
						dialog.showDialog(ResourceIntf.showFrockCard(map));
					}
				}
			}
		});
	}
}
