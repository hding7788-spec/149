package com.glaway.mpm.erp;

import javax.swing.JPanel;

public abstract class CmAbstractPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private volatile boolean executorActive;

	public CmAbstractPanel() {
		super();
	}

	protected abstract void initDimension();

	protected abstract void initActions();

	protected abstract void initComponents();

	protected abstract void initLayout();

	protected abstract void loadInitDatas();

	public boolean isExecutorActive() {
		return this.executorActive;
	}

	public void setExecutorActive(boolean executorActive) {
		this.executorActive = executorActive;
	}
}
