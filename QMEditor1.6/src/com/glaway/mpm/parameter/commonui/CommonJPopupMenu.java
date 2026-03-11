package com.glaway.mpm.parameter.commonui;

import javax.swing.JPopupMenu;

public abstract class CommonJPopupMenu extends JPopupMenu {

	private static final long serialVersionUID = 1L;
	
	public CommonJPopupMenu() {
		super();
		
		initComponent();
		initMenuItemIcon();
		registerComponentAuthority();
	}

	/** 
	 * 定义菜单 
	 * 
	 */
	protected abstract void initComponent();
	
	/** 
	 * 定义菜单图标 
	 * 
	 */
	protected abstract void initMenuItemIcon();
	
	/**
	 * 注册菜单项组件的权限，以便用于权限管理。
	 */
	protected abstract void registerComponentAuthority();
}
