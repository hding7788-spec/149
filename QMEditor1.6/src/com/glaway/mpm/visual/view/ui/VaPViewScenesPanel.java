package com.glaway.mpm.visual.view.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.plaf.ColorUIResource;

import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.visual.view.VaTheme;
import com.glaway.mpm.visual.view.action.VaAction;
import com.glaway.mpm.visual.view.action.VaPViewZoomAll;
import com.glaway.mpm.visual.view.action.VaPViewZoomSelected;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;

public class VaPViewScenesPanel extends VaAbstractPanel {
	private static final long	serialVersionUID	= -8722967403147623505L;

	private String				name;
	private VaAction			actZoomAll;
	private VaAction			actZoomSelected;

	private JButton				btnZoomAll;
	private JButton				btnZoomSelected;
	private JButton				btnShowSearch;

	private VaPViewScenePanel	panelContext;
	private VaPViewSearchPanel	searchPanel;
	private boolean				isShowSearch		= false;
	private JToolBar			toolBar;
	private JMenuBar			menuBar;

	public VaPViewScenesPanel(String name, VaPViewSearchPanel panel) {
		super();
		this.name = name;
		this.searchPanel = panel;
		try {
			initUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		btnShowSearch = new JButton("空间搜寻");
		btnShowSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (!isShowSearch) {
					searchPanel.showSearch(true, "bbox");
					isShowSearch = true;
//					btnShowSearch.setBackground(Color.green);
//					VaPViewFactory.getPViewImpl4MBOM().registerMarkupObs();
				} else {
					searchPanel.showSearch(false, "bbox");
					isShowSearch = false;
//					btnShowSearch.setBackground(VaTheme.VA_TURQUOISE);
//					VaPViewFactory.getPViewImpl4MBOM().unregisterMarkupObs();
				}
			}
		});

		panelContext = VaPViewFactory.getPViewImpl(name).getPanelContext();

	}

	@Override
	protected void initLayout() {
		this.setLayout(new BorderLayout());
		this.add(buildToolBar(), BorderLayout.NORTH);
		this.add(panelContext, BorderLayout.CENTER);
	}

	private JToolBar buildToolBar() {
		toolBar = new JToolBar();
		toolBar.setBackground(new ColorUIResource(102, 146, 181));
		toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE));
		toolBar.setFloatable(false);
		toolBar.setRollover(true);

		toolBar.add(btnZoomAll);
		toolBar.add(btnZoomSelected);
		toolBar.add(btnShowSearch);
		return toolBar;
	}

	public void setMenuBar(JMenuBar menu) {
		if (this.menuBar != null) {
			this.remove(menuBar);
		}
		this.menuBar = menu;
		this.add(menuBar, BorderLayout.NORTH);
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
