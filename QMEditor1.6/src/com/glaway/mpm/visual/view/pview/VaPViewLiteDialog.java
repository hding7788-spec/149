package com.glaway.mpm.visual.view.pview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.vc.views.View;

import com.glaway.mpm.task.CmTaskExecutorCallback;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaGuiUtil;
import com.glaway.mpm.visual.util.VaSearchHelper;
import com.glaway.mpm.visual.view.action.VaAction;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.action.VaPViewZoomAll;
import com.glaway.mpm.visual.view.action.VaPViewZoomSelected;
import com.glaway.mpm.visual.view.ui.VaAbstractDialog;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

public class VaPViewLiteDialog extends VaAbstractDialog {
	private static final long serialVersionUID = 2685289616311556306L;
	private static final VaLogger logger = VaLogger.getLogger();

	private String name;
	private VaAction actZoomAll;
	private VaAction actZoomSelected;

	private JButton btnZoomAll;
	private JButton btnZoomSelected;

	private JLabel labelStatus; // �ײ�״̬��

	private boolean resetNeeded;

	 private VaActionProgressBar animFrame;

	public VaPViewLiteDialog(Window owner, String name) {
		super(owner);
		// this.animFrame = null;
		this.resetNeeded = false;
		this.name = name;
		this.setModal(false);
		try {
			this.initUI();
		} catch (Exception e) {
			 logger.error(e);
		}

		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

	}

	@Override
	protected void initActions() {
		actZoomAll = new VaPViewZoomAll(name);
		actZoomSelected = new VaPViewZoomSelected(name);
	}

	@Override
	protected void initComponents() {
		btnZoomAll = new JButton(actZoomAll);
		btnZoomAll.setForeground(Color.WHITE);

		btnZoomSelected = new JButton(actZoomSelected);
		btnZoomSelected.setForeground(Color.WHITE);

		labelStatus = new JLabel(" PViewLite 9.1: ");
		labelStatus.setBorder(new EtchedBorder());
	}

	@Override
	protected void initDimension() {
		this.setBounds(VaGuiUtil.getScreenCenter(600, 480));
	}

	@Override
	protected void initLayout() {
		setLayout(new BorderLayout());
		getContentPane().add(buildToolBar(), BorderLayout.NORTH);
		getContentPane().add(VaPViewFactory.getPViewImpl(name).getPanelContext(), BorderLayout.CENTER);
		getContentPane().add(labelStatus, BorderLayout.SOUTH);
	}

	private JToolBar buildToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(Color.GRAY);// CmTheme.CM_TURQUOISE);
		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		toolBar.add(btnZoomAll);
		toolBar.add(btnZoomSelected);
		return toolBar;
	}

	@Override
	protected void loadInitDatas() {
	}

	 @Override
	 protected void registerTaskExecutor() throws CmTaskException {
	 CmTaskHelper.registerTaskExecutor("mainframe.setStatus",
	 VaPViewLiteDialog.this, false);
	 }
	
	 @Override
	 protected void unregisterTaskExecutor() throws CmTaskException {
	 CmTaskHelper.unregisterTaskExecutor(this);
	 }

	public void showPViewLite(String caption, VaPViewNode root) {
		this.setTitle(caption == null ? "���ӻ�����" : caption);
		this.setVisible(true);

		final VaPViewImpl impl = VaPViewFactory.getPViewImpl(name);
		if (this.resetNeeded) {
			try {
				impl.resetPvWorld(null);
			} catch (ConnectionLostException e) {
				logger.error(e);
			} catch (ActorShutdownException e) {
				logger.error(e);
			} catch (MessageProtocolException e) {
				logger.error(e);
			} catch (InvalidActorException e) {
				logger.error(e);
			}
		}

		final VaPViewNode treeRoot = root;
		try {
			Thread createPVRunner = new Thread() {
				public void run() {
					// try {
					// CmTaskHelper.registerTaskExecutor("CmPViewLiteDialog.finishAnimFrame",
					// CmPViewLiteDialog.this, true);
					// } catch (CmTaskException e) {
					// logger.error(e);
					// }

					try {
						VaPViewNodeGenerator generator = new VaPViewNodeGenerator(treeRoot, impl.getName());
						generator.generatePVStructure("VaPViewLiteDialog.finishAnimFrame");
					} catch (Exception e) {
						logger.error(e);
					}
				}
			};
			// this.animFrame = new CmActionProgressBar(null,
			// CmPViewLiteDialog.this, "���ӻ�", "���ڽ��п��ӻ�װ��",
			// "���ڽ���Product View���ӻ�װ�䣬��ȴ�...");
			// this.animFrame.setVisible(true);
			this.resetNeeded = true;

			createPVRunner.start();
		} catch (Exception e) {
			// logger.error(e);
		}
	}

	 public synchronized void finishAnimFrame(Object render, Object params,
	 CmTaskExecutorCallback callback) {
	 if (animFrame != null) {
	 try {
	 animFrame.finish();
	 animFrame = null;
	 } catch (Throwable tt) {
	 } finally {
	 animFrame = null;
	 }
	 }
	 }

	public void setStatus(Object render, Object param) {
		StringBuffer buf = new StringBuffer(" PViewLite 9.1: ");
		if (param instanceof Object[]) {
			Object[] params = (Object[]) param;
			for (Object obj : params)
				buf.append(String.valueOf(obj));
		} else
			buf.append(String.valueOf(param));

		labelStatus.setText(buf.toString());
	}

	public static void main(String[] args) throws Exception {
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin");

		// String number = "CI_C1F01-5312-002";
		final String number = "AL2_907_1460";
		// String number = "5311C01027P71";
		// WTPart part = WCUtil.findPart(number, null);
		// View view = VaSearchHelper.getViewByName("Design");
		
		// dialog.showPViewLite("wanghaoyu1", root);

		// Thread.sleep(3000);

		JFrame frame = new JFrame();
		frame.setSize(800, 600);
		FlowLayout layout = new FlowLayout();
		layout.setAlignment(FlowLayout.LEFT);

		frame.setLayout(layout);
		JButton btn = new JButton("button1");
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				View view = VaSearchHelper.getViewByName("Design");
//				WTPart part = VaSearchHelper
//						.getPartByNumberAndView(number, view.getPersistInfo().getObjectIdentifier().getId());
//				VaPViewNode root = VaPViewLiteUtil.buildPViewStructureTree(part);
//
//				VaPViewLiteDialog dialog = VaPViewLiteUtil.getPViewLiteDialog("test");
//				dialog.showPViewLite("wanghaoyu", root);
			}
		});

		JButton btn1 = new JButton("button2");
		btn1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				View view = VaSearchHelper.getViewByName("Design");
//				WTPart part = VaSearchHelper
//						.getPartByNumberAndView(number, view.getPersistInfo().getObjectIdentifier().getId());
//				VaPViewNode root = VaPViewLiteUtil.buildPViewStructureTree(part);
//
//				VaPViewLiteDialog dialog = VaPViewLiteUtil.getPViewLiteDialog("test2");
//				dialog.showPViewLite("wanghaoyu2", root);
			}
		});
		JButton btn2 = new JButton("button3");
		btn2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				View view = VaSearchHelper.getViewByName("Design");
//				WTPart part = VaSearchHelper
//						.getPartByNumberAndView(number, view.getPersistInfo().getObjectIdentifier().getId());
//				VaPViewNode root = VaPViewLiteUtil.buildPViewStructureTree(part);
//
//				VaPViewLiteDialog dialog = VaPViewLiteUtil.getPViewLiteDialog("test3");
//				dialog.showPViewLite("wanghaoyu3", root);
			}
		});
		JButton btn3 = new JButton("button4");
		btn3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				View view = VaSearchHelper.getViewByName("Design");
//				WTPart part = VaSearchHelper
//						.getPartByNumberAndView(number, view.getPersistInfo().getObjectIdentifier().getId());
//				VaPViewNode root = VaPViewLiteUtil.buildPViewStructureTree(part);
//
//				VaPViewLiteDialog dialog = VaPViewLiteUtil.getPViewLiteDialog("test4");
//				dialog.showPViewLite("wanghaoyu4", root);
			}
		});
		
		frame.add(btn);
		frame.add(btn1);
		frame.add(btn2);
		frame.add(btn3);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		// number = "CI_C1F01-5311-001";
		// part = WCUtil.findPart(number, null);
		// root = CmPViewLiteUtil.buildPViewStructureTree(part);
		//
		// dialog = CmPViewLiteUtil.getPViewLiteDialog("test");
		// dialog.showPViewLite("���Դ���2", root);

		// Thread.sleep(2000);
		// System.exit(0);
	}
}
