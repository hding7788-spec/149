package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.visual.view.VaTheme;
import com.glaway.mpm.visual.view.action.VaAction;
import com.glaway.mpm.visual.view.action.VaPViewZoomAll;
import com.glaway.mpm.visual.view.action.VaPViewZoomSelected;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.pview.VaPViewImpl;
import com.glaway.mpm.visual.view.ui.VaAbstractPanel;
import com.glaway.mpm.visual.view.ui.VaPViewScenePanel;

public class DpPViewScenesPanel extends VaAbstractPanel {
	private static final long serialVersionUID = -8722967403147623505L;

	private String name;
	 private VaAction actZoomAll;
	 private VaAction actZoomSelected;
	
//	private JButton open;
//	private JButton close;
//	private JButton save;
//	private JButton saveAsPvs;
	
	private JButton btnZoomAll;
	private JButton btnZoomSelected;

	private VaPViewScenePanel panelContext;
	private VaPViewImpl vaPViewImpl;

	public DpPViewScenesPanel(String name) {
		super();
		this.name = name;
		try {
	         initUI();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
	}

	@Override
	protected void initActions() {
//		saveAsPvs = new JButton("保存为pvs");
//		open = new JButton("开启注释");
//		close = new JButton("关闭注释");
//		save = new JButton("保存注释");

		 actZoomAll = new VaPViewZoomAll(name);
		 actZoomSelected = new VaPViewZoomSelected(name);
	}

	@Override
	protected void initComponents() {
		 btnZoomAll = new JButton(actZoomAll);
		 btnZoomAll.setForeground(Color.WHITE);

		 btnZoomSelected = new JButton(actZoomSelected);
		 btnZoomSelected.setForeground(Color.WHITE);

		vaPViewImpl = VaPViewFactory.getPViewImpl(name);
		panelContext = vaPViewImpl.getPanelContext();
	}

	@Override
	protected void initLayout() {
		this.setLayout(new BorderLayout());
		this.add(buildToolBar(), BorderLayout.NORTH);
		this.add(panelContext, BorderLayout.CENTER);
	}

	private JToolBar buildToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setBackground(VaTheme.VA_TURQUOISE);
		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);
		// toolBar.setBackground(new ColorUIResource(102, 146, 181));
		// toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0,
		// Color.WHITE));
		// toolBar.setFloatable(false);
		// toolBar.setRollover(true);

//		toolBar.add(saveAsPvs);
//		toolBar.add(open);
//		toolBar.add(close);
//		toolBar.add(save);

//		saveAsPvs.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				vaPViewImpl.saveAsPvs();
//			}
//		});
//		
//		open.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				vaPViewImpl.openAnnotation(vaTree);
//			}
//		});
//
//		close.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				vaPViewImpl.closeAnnotation();
//			}
//		});
//
//		save.addActionListener(new ActionListener() {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				vaPViewImpl.saveAnnotation();
//			}
//		});

		 toolBar.add(btnZoomAll);
		 toolBar.add(btnZoomSelected);
		return toolBar;
	}

	@Override
	protected void initDimension() {
	}

	@Override
	protected void loadInitDatas() {
	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {
		// TODO Auto-generated method stub
		
	}
}
