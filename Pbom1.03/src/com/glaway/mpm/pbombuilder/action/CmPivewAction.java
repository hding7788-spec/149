package com.glaway.mpm.pbombuilder.action;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.glaway.mpm.pbombuilder.bom.CmMBomMainFrame;
import com.glaway.mpm.pbombuilder.bom.CmTaskExecutor;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.pview.CmPViewGenerator;
import com.glaway.mpm.pbombuilder.tree.CmTree;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmTaskExecutorCallback;
import com.glaway.mpm.pbombuilder.util.CmUtil;
import com.glaway.mpm.pbombuilder.util.PviewTask;
import com.ptc.pview.pvapi.SelectionObserver;

/**
 * <br>
 * Created on 2012-10-15
 *
 * @author chenyunlong
 */
public class CmPivewAction extends CmAction implements CmTaskExecutor {

	private static final long serialVersionUID = -2493860500873421587L;
	private static final CmLogger log = CmLogger.getLogger(CmPivewAction.class
			.getName());
	private boolean executorActive;
	private static CmActionProgressBar animFrame = null;

	private Class generatorClass;
	private Class soClass;
	private CmTree tree;

	public static final int EBOMTREE = 0;
	public static final int MBOMTREE = 1;
	public static String actFinishAnimFrame = "CmPivewAction.finishAnimFrame";
	private ImageIcon pviewImage = new ImageIcon(
			CmUtil.getImageFromServer("creo.png"));
	private Window mainOwner;
	private boolean flag = true;

	private CmPViewGenerator generator;

	public CmPivewAction(Window mainOwner, CmTree tree, Class generatorClass,
			Class soClass) {
		setIcon(pviewImage);
		setToolTipText("显示可视化");
		this.generatorClass = generatorClass;
		this.soClass = soClass;
		this.mainOwner = mainOwner;
		this.tree = tree;
		 try {
			 generator = (CmPViewGenerator) generatorClass.getConstructor(
			 CmTree.class).newInstance(tree);
		 } catch (IllegalArgumentException e) {
		 e.printStackTrace();
		 } catch (SecurityException e) {
		 e.printStackTrace();
		 } catch (InstantiationException e) {
		 e.printStackTrace();
		 } catch (IllegalAccessException e) {
		 e.printStackTrace();
		 } catch (InvocationTargetException e) {
		 e.printStackTrace();
		 } catch (NoSuchMethodException e) {
		 e.printStackTrace();
		 }
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent evt) {
		if (!flag) {
			return;
		}
		flag = false;
		int nbSelNodes = 0;

		log.debug("generate pvs:::;");
		if (tree == null)
			return;
		Enumeration<CmTreeNode> enume = tree.getRoot().depthFirstEnumeration();
		while (enume.hasMoreElements()) {
			CmTreeNode node = enume.nextElement();
			if (node.isSelected()) {
				nbSelNodes++;
				break;
			}
		}

		if (nbSelNodes > 0) {
			try {

				Thread createPVRunner = new Thread() {
					public void run() {
						try {
							PviewTask.registerTaskExecutor(actFinishAnimFrame,
									CmPivewAction.this, true);
						} catch (CmTaskException e) {
							flag = true;
						}

						try {
//							CmPViewGenerator generator = (CmPViewGenerator) generatorClass
//									.getConstructor(CmTree.class).newInstance(
//											tree);
//							SelectionObserver so = (SelectionObserver) soClass
//									.getConstructor(CmTree.class).newInstance(
//											tree);
//
//							generator
//									.generatePVStructure(CmPivewAction.actFinishAnimFrame);
//							generator.getPviewImpl().initObsContext(so);
//							generator.getPviewImpl().setSelectionObserver(so);



							SelectionObserver so = (SelectionObserver) soClass
									.getConstructor(CmTree.class).newInstance(tree);

							generator
									.generatePVStructure(CmPivewAction.actFinishAnimFrame);
//							generator.getPviewImpl().initObsContext(so);
							generator.getPviewImpl().setSelectionObserver(so);

						}catch(UnsatisfiedLinkError e){

							System.out.println("-----------------");
							e.printStackTrace();
							flag = true;
							finishAnimFrameForException(null,null,null);
						}catch(NoClassDefFoundError e){
							System.out.println("-----------------");
							e.printStackTrace();
							flag = true;
							finishAnimFrameForException(null,null,null);
						}
						catch (Exception e) {
							e.printStackTrace();
							flag = true;
							finishAnimFrameForException(null,null,null);
						}
						tree.repaint();
						flag = true;
						CmMBomMainFrame.getMainFrame()
								.setPviewInitialized(true);
					}
				};
				animFrame = new CmActionProgressBar(this, mainOwner, "可视化装配",
						"正在进行可视化装配", "正在进行Product View可视化装配，请等待...");
				createPVRunner.start();
				animFrame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
				flag = true;
			}
		} else {
			setEnabled(true);
			flag = true;
		}

	}

	public boolean isExecutorActive() {
		return executorActive;
	}

	public void setExecutorActive(boolean executorActive) {
		this.executorActive = executorActive;
	}

	public synchronized void finishAnimFrame(Object render, Object params,
			CmTaskExecutorCallback callback) {
		if (animFrame != null) {
			try {

				 if (generator instanceof CmEPViewPVSGenerator) {
					 ((CmEPViewPVSGenerator) generator).linkPviewToTree();
				 }
				animFrame.finish();
				animFrame = null;

				PviewTask.postTask("mainframe.setStatus", "可视化装载完成");
			} catch (Throwable tt) {
			} finally {
				animFrame = null;
				CmPivewAction.this.setExecutorActive(false);
			}
		}
	}
	public synchronized void finishAnimFrameForException(Object render, Object params,
			CmTaskExecutorCallback callback) {
		if (animFrame != null) {
			try {

				 if (generator instanceof CmEPViewPVSGenerator) {
					 ((CmEPViewPVSGenerator) generator).linkPviewToTree();
				 }
				animFrame.finish();
				animFrame = null;

				PviewTask.postTask("mainframe.setStatus", "可视化装载异常,可能没有安装CREO VIEW 2.0");
			} catch (Throwable tt) {
			} finally {
				animFrame = null;
				CmPivewAction.this.setExecutorActive(false);
			}
		}
	}
	public static void closeProcessBaer() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (animFrame != null) {
					animFrame.finish();
					animFrame = null;
				}
			}
		});
	}
}
