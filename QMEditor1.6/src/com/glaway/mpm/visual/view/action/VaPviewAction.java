/**
 * <br>Created on 2011-3-15
 * @author Alex.Huang - ����
 */
package com.glaway.mpm.visual.view.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import javax.swing.ImageIcon;

import com.glaway.mpm.qmIntf.fittingTool.view.FittingsDistributionFrame;
import com.glaway.mpm.task.CmTaskExecutor;
import com.glaway.mpm.task.CmTaskExecutorCallback;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.pview.VaEPViewPVSGenerator;
import com.glaway.mpm.visual.view.pview.VaEPViewStructureGenerator;
import com.glaway.mpm.visual.view.pview.VaPViewGenerator;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.InvalidActorException;

/**
 *
 */
public class VaPviewAction extends VaAction implements CmTaskExecutor {
	private VaLogger					log					= VaLogger.getLogger();
	private static final long			serialVersionUID	= -2493860500873421587L;
	private static VaActionProgressBar	animFrame			= null;
	private boolean						executorActive;

	private Class						generatorClass;
	private Class						soClass;
	private VaTree						tree;
	private VaTree						stree;

	public static final int				EBOMTREE			= 0;
	public static final int				MBOMTREE			= 1;
	public static String				actFinishAnimFrame	= "VaPviewAction.finishAnimFrame";
	private ImageIcon					pviewImage			= new ImageIcon(VaPviewAction.class
																	.getResource("/image/productView.gif"));	// CmUtil.getImageFromServer("productView.gif"));
	private Window						mainOwner;
	private VaPViewGenerator			generator;

	public VaPviewAction(Window mainOwner, VaTree tree, VaTree sTree, Class generatorClass, Class soClass) {
		log.debug(pviewImage.toString());
		setIcon(pviewImage);
		setToolTipText("显示可视化");
		this.generatorClass = generatorClass;
		this.soClass = soClass;
		this.mainOwner = mainOwner;
		this.tree = tree;
		this.stree = sTree;
		try {
			generator = new VaEPViewPVSGenerator(tree, sTree);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doit() {
		log.debug("%%%%%%%%%%%%%%%%%%%%%%%% DO IT !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		try {

			Thread createPVRunner = new Thread() {
				public void run() {
					log.debug("%%%%%%%%%%%%%%%%%%%%%%%% createPVRunner start !!!!!!!!!!!!dfdfdfd!!!!!!!!!!!!!!!!");
					try {
						CmTaskHelper.registerTaskExecutor(actFinishAnimFrame, VaPviewAction.this, true);
					} catch (CmTaskException e) {

					}

					try {
						// VaPViewGenerator generator = (VaPViewGenerator)
						// generatorClass.getConstructor(VaTree.class).newInstance(tree);
						// SelectionObserver so = (SelectionObserver)
						// soClass.getConstructor(VaTree.class).newInstance(tree);

						log.debug("%%%%%%%%%%%%%%%%%%%%%%%% DO IT IN !!!!!!!!!!!!dfdfdfd!!!!!!!!!!!!!!!!");

						generator.generatePVStructure(actFinishAnimFrame);
						// generator.getPviewImpl().initObsContext(so);
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
		((FittingsDistributionFrame)mainOwner).setPviewInitialized(true);
		// log.debug("actionPerformed");
		setEnabled(false);
		// log.debug("actionPerformed=====1");
		int nbSelNodes = 0;

		if (tree == null)
			return;

		Enumeration<VaTreeNode> enume = tree.getRoot().depthFirstEnumeration();
		while (enume.hasMoreElements()) {
			VaTreeNode node = enume.nextElement();
			if (node.isSelected()) {
				nbSelNodes++;
				break;
			}
		}
		 if (nbSelNodes > 0) {
			try {

				Thread createPVRunner = new Thread() {
					public void run() {
						// log.debug("%%%%%%%%%%%%%%%%%%%%%%%% createPVRunner start !!!!!!!!!!!!dfdfdfd!!!!!!!!!!!!!!!!");
						try {
							CmTaskHelper.registerTaskExecutor(actFinishAnimFrame, VaPviewAction.this, true);
						} catch (CmTaskException e) {

						}

						try {
//							generator = (VaPViewGenerator) generatorClass.getConstructor(VaTree.class).newInstance(tree);
//							SelectionObserver so = (SelectionObserver) soClass.getConstructor(VaTree.class).newInstance(
//									tree);
//							// log.debug("%%%%%%%%%%%%%%%%%%%%%%%% DO IT IN !!!!!!!!!!!!dfdfdfd!!!!!!!!!!!!!!!!");
//							generator.generatePVStructure(actFinishAnimFrame);
//							generator.getPviewImpl().initObsContext(so);
//							generator.getPviewImpl().setSelectionObserver(so);
//							generator.getPviewImpl().removeBbox();

							// VaPViewGenerator generator = (VaPViewGenerator)
							// generatorClass.getConstructor(VaTree.class).newInstance(tree);
							SelectionObserver so = (SelectionObserver) soClass.getConstructor(VaTree.class).newInstance(
									tree);
							// log.debug("%%%%%%%%%%%%%%%%%%%%%%%% DO IT IN !!!!!!!!!!!!dfdfdfd!!!!!!!!!!!!!!!!");
							generator.generatePVStructure(actFinishAnimFrame);
							// generator.getPviewImpl().initObsContext(so);
							generator.getPviewImpl().setSelectionObserver(so);
							generator.getPviewImpl().removeBbox();



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
				if (generator instanceof VaEPViewPVSGenerator) {
					((VaEPViewPVSGenerator) generator).linkPviewToTree();
				}
				animFrame.finish();
				animFrame = null;

				// CmTaskHelper.postTask("mainframe.setStatus", "���ӻ�װ�����");
			} catch (Throwable tt) {
				tt.printStackTrace();
			} finally {
				animFrame = null;
				VaPviewAction.this.setExecutorActive(false);
				setEnabled(true);
			}
		}
	}
}
