package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

/**
 * <br>Created on 2011-3-15
 * @author Alex.Huang - ����
 */

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.glaway.mpm.task.CmTaskExecutor;
import com.glaway.mpm.task.CmTaskExecutorCallback;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.action.VaAction;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.pview.VaPViewGenerator;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.ptc.pview.pvapi.SelectionObserver;

/**
 * <br>
 * Created on 2011-3-15
 * 
 * @author Alex.Huang - ����
 */
public class DpPivewAction extends VaAction implements CmTaskExecutor {
	private VaLogger					log					= VaLogger.getLogger();
	private static final long			serialVersionUID	= -2493860500873421587L;

	private boolean						executorActive;
	private static VaActionProgressBar	animFrame			= null;

	private Class						generatorClass;
	private Class						soClass;
	private VaTree						tree;

	public static final int				EBOMTREE			= 0;
	public static final int				MBOMTREE			= 1;
	public static String				actFinishAnimFrame	= "VaPivewAction.finishAnimFrame";
	private ImageIcon					pviewImage			= new ImageIcon(
																	DpPivewAction.class
																			.getResource("/image/productView.gif"));	// CmUtil.getImageFromServer("productView.gif"));
	private Window						mainOwner;

	public DpPivewAction(Window mainOwner, VaTree tree, Class generatorClass, Class soClass) {
		setIcon(pviewImage);
		setToolTipText("显示可视化");
		this.generatorClass = generatorClass;
		this.soClass = soClass;
		this.mainOwner = mainOwner;
		this.tree = tree;
	}

	public void doit() {
		log.debug("%%%%%%%%%%%%%%%%%%%%%%%% DO IT !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		try {

			Thread createPVRunner = new Thread() {
				public void run() {
					log.debug("%%%%%%%%%%%%%%%%%%%%%%%% createPVRunner start !!!!!!!!!!!!dfdfdfd!!!!!!!!!!!!!!!!");
					try {
						CmTaskHelper.registerTaskExecutor(actFinishAnimFrame, DpPivewAction.this, true);
					} catch (CmTaskException e) {

					}

					try {
						VaPViewGenerator generator = (VaPViewGenerator) generatorClass.getConstructor(VaTree.class)
								.newInstance(tree);
						SelectionObserver so = (SelectionObserver) soClass.getConstructor(VaTree.class).newInstance(
								tree);

						log.debug("%%%%%%%%%%%%%%%%%%%%%%%% DO IT IN !!!!!!!!!!!!dfdfdfd!!!!!!!!!!!!!!!!");

						generator.generatePVStructure(actFinishAnimFrame);
						generator.getPviewImpl().initObsContext(so);
					} catch (Exception e) {
						e.printStackTrace();
					}
					tree.repaint();
				}
			};
			animFrame = new VaActionProgressBar(this, mainOwner, "可视化装配", "正在进行可视化装配", "正在进行Product View可视化装配，请等待...");

			createPVRunner.start();
			animFrame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent evt) {
		log.debug("actionPerformed");
		setEnabled(false);
		log.debug("actionPerformed=====1");
		int nbSelNodes = 0;

		if (tree == null)
			return;

		log.debug("actionPerformed=======2");
		Enumeration<VaTreeNode> enume = tree.getRoot().depthFirstEnumeration();
		while (enume.hasMoreElements()) {
			VaTreeNode node = enume.nextElement();
			if (node.isSelected()) {
				nbSelNodes++;
				break;
			}
		}
		log.debug("actionPerformed=====3");
		if (nbSelNodes > 0) {
			log.debug("actionPerformed=====4");
			try {

				Thread createPVRunner = new Thread() {
					public void run() {
						log.debug("%%%%%%%%%%%%%%%%%%%%%%%% createPVRunner start !!!!!!!!!!!!dfdfdfd!!!!!!!!!!!!!!!!");
						try {
							CmTaskHelper.registerTaskExecutor(actFinishAnimFrame, DpPivewAction.this, true);
						} catch (CmTaskException e) {

						}

						try {
							VaPViewGenerator generator = (VaPViewGenerator) generatorClass.getConstructor(VaTree.class)
									.newInstance(tree);
							SelectionObserver so = (SelectionObserver) soClass.getConstructor(VaTree.class)
									.newInstance(tree);
							log.debug("%%%%%%%%%%%%%%%%%%%%%%%% DO IT IN !!!!!!!!!!!!dfdfdfd!!!!!!!!!!!!!!!!");
							generator.generatePVStructure(actFinishAnimFrame);
							generator.getPviewImpl().initObsContext(so);
						} catch (Exception e) {
							e.printStackTrace();
						}
						tree.repaint();
					}
				};
				animFrame = new VaActionProgressBar(this, mainOwner, "可视化装配", "正在进行可视化装配",
						"正在进行Product View可视化装配，请等待...");

				createPVRunner.start();
				animFrame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		setEnabled(true);
	}

	public boolean isExecutorActive() {
		return executorActive;
	}

	public void setExecutorActive(boolean executorActive) {
		this.executorActive = executorActive;
	}

	public synchronized void finishAnimFrame(Object render, Object params, CmTaskExecutorCallback callback) {
		if (animFrame != null) {
			try {
				animFrame.finish();
				animFrame = null;

				// CmTaskHelper.postTask("mainframe.setStatus", "���ӻ�װ�����");
			} catch (Throwable tt) {
			} finally {
				animFrame = null;
				DpPivewAction.this.setExecutorActive(false);
			}
		}
	}
}
