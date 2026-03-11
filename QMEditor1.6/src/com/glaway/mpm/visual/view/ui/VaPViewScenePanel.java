package com.glaway.mpm.visual.view.ui;

import java.awt.Panel;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.pview.VaPViewFactory;
import com.glaway.mpm.visual.view.pview.VaPViewImpl;
import com.glaway.mpm.visual.view.tree.VaTreeModelLinkage;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeView;

public class VaPViewScenePanel extends Panel {
	private static final long serialVersionUID = -4066773024146918225L;

	private String name;
	private ShapeScene shapeScene;
	private ShapeView shapeView;
	private static VaLogger logger = VaLogger.getLogger(VaPViewScenePanel.class);
	
	public VaPViewScenePanel(String name) {
		super();
		this.name = name;
	}

	public void addNotify() {
		super.addNotify();
		logger.debug("panel pview init");
		if (VaPViewImpl.isPviewInitialized()
				&& VaPViewFactory.getPViewImpl(name) != null) {
//			VaPViewFactory.getPViewImpl(name).initPView();
		}
	}

	public ShapeScene getShapeScene() {
		return shapeScene;
	}

	public void setShapeScene(ShapeScene shapeScene) {
		this.shapeScene = shapeScene;
	}

	public ShapeView getShapeView() {
		return shapeView;
	}

	public void setShapeView(ShapeView shapeView) {
		this.shapeView = shapeView;
	}

//	public void cleanup() {
//		try {
//			if (this.shapeScene != null) {
//				this.shapeScene.RemoveAllShapeInstances();
//			}
//			if (this.structure != null) {
//				this.structure.RemoveAllComponents();
//			}
//		} catch (Exception localException) {
//			localException.printStackTrace();
//			return;
//		}
//	}
}
