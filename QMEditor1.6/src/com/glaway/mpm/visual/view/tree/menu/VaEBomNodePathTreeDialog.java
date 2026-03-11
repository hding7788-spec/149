/**
 * <br>Created on 2010-11-2
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.tree.menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.visual.bean.VaLightType;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaGuiUtil;
import com.glaway.mpm.visual.util.VaTypeHelper;
import com.glaway.mpm.visual.util.VaUtil;
import com.glaway.mpm.visual.view.VaTheme;
import com.glaway.mpm.visual.view.action.VaAction;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.ui.VaAbstractDialog;

/**
 * <br>
 * Created on 2010-11-2
 * 
 * @author Alex.Huang
 */
public class VaEBomNodePathTreeDialog extends VaAbstractDialog {

	private static final long serialVersionUID = 9017100294252145180L;
	private static final VaLogger log = VaLogger
			.getLogger(VaEBomNodePathTreeDialog.class);
	private static VaEBomNodePathTreeDialog instance;
	private JTree tree;
	private VaPathScrollPane treePanel;
	private JLabel labelTile;
	private JButton btnColse;
	private VaAction actColse;
	private VaTreeNode node;

	private VaEBomNodePathTreeImpl impl = VaEBomNodePathTreeImpl
			.newVaEBomNodePathTreeImpl();

	/**
	 * @param owner
	 */

	public static VaEBomNodePathTreeDialog getEBomNodePathTreeDialog(
			Window owner, VaTreeNode node) {
		instance = new VaEBomNodePathTreeDialog(owner, node);
		return instance;
	}

	private VaEBomNodePathTreeDialog(Window owner, VaTreeNode node) {
		super(owner);
		this.node = node;
		try {
			setResizable(false);
			setModal(true);

			initUI();
		} catch (Exception e) {
			log.error(e);
		}
	}

	@Override
	protected void initActions() {
		actColse = new VaColseAction("关闭", this);
	}

	@Override
	protected void initComponents() {
		treePanel = new VaPathScrollPane();
		tree = treePanel.getTree();
		tree.setCellRenderer(new TreeCellRenderer() {
			protected JLabel label = new JLabel();

			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean sel, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				label.setForeground(Color.black);
				label.setOpaque(false);
				setEnabled(tree.isEnabled());
				VaTreeNode node = (VaTreeNode) value;

				String stringValue = tree.convertValueToText(value, sel,
						expanded, leaf, row, hasFocus);
				label.setText(stringValue);

				if (sel) {
					label.setOpaque(true);
					label.setBackground(VaTheme.VA_TURQUOISE);
					label.setForeground(Color.WHITE);
				} else {
					label.setOpaque(false);
					label.setForeground(Color.BLACK);
				}

				VaLightType lightType = VaTypeHelper.getLightType(node
						.getPart().getType(), false);
				if (lightType.getIconImage() != null)
					label.setIcon(new ImageIcon(lightType.getIconImage()));
				return label;
			}

		});
		btnColse = new JButton(actColse);
		labelTile = new JLabel();
	}

	@Override
	protected void initDimension() {
		this.setBounds(VaGuiUtil.getScreenCenter(500, 240));
	}

	@Override
	protected void initLayout() {
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(labelTile, BorderLayout.NORTH);
		this.getContentPane().add(treePanel, BorderLayout.CENTER);
		this.getContentPane().add(getActionPanel(), BorderLayout.SOUTH);
	}

	private JPanel getActionPanel() {
		JPanel ret = new JPanel(new FlowLayout(FlowLayout.CENTER));
		ret.add(btnColse);
		return ret;
	}

	@Override
	protected void loadInitDatas() {
		setTitle(node.getPart().toString());
		VaLightType lightType = VaTypeHelper.getLightType(node.getPart()
				.getType(), false);
		if (lightType.getIconImage() != null)
			setIconImage(lightType.getIconImage());

		VaTreeNode root = impl.getRootNode(node);
		tree.setModel(new DefaultTreeModel(root));
		tree.updateUI();
		impl.expandAll(tree, new TreePath(root), true);
	}
	   @Override
	   protected void registerTaskExecutor() throws CmTaskException {}

	   @Override
	   protected void unregisterTaskExecutor() throws CmTaskException {}
	static class VaColseAction extends VaAction {
	      private static final long serialVersionUID = 6268256551327189248L;
	      VaAbstractDialog          owner;

	      public VaColseAction(String label, VaAbstractDialog owner) {
	         super(label);
	         this.owner = owner;
	      }

	      @Override
	      public void actionPerformed(ActionEvent evt) {
	         owner.dispose();
	      }

	   }

}

class VaPathScrollPane extends JScrollPane {
	private static final long serialVersionUID = 5561801173555889325L;
	private Image image;

	public VaPathScrollPane() {
		super();
		setOpaque(true);
		getViewport().setOpaque(false);
		image = VaUtil.getImageFromServer("background.gif");
		JTree tree = new JTree();
		tree.setOpaque(false);
		tree.setRowHeight(16);
		setViewportView(tree);

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		setBackground(Color.WHITE);
		if (image != null) {
			int height = image.getHeight(this);
			int width = image.getWidth(this);

			if (height != -1 && height > getHeight())
				height = getHeight();

			if (width != -1 && width > getWidth())
				width = getWidth();

			int x = (int) (((double) (getWidth() - width)) / 2.0);
			int y = (int) (((double) (getHeight() - height)) / 2.0);
			g.drawImage(image, x, y, width, height, this);
		}
	}

	public JTree getTree() {
		return (JTree) getViewport().getView();
	}

}