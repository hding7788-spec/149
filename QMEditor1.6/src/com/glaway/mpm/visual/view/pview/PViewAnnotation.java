/*
 * @author wanghaoyu
 * @date 2013-4-23
 * 版权属 南京国睿信维软件有限公司 所有
 */


package com.glaway.mpm.visual.view.pview;

import java.io.File;
import java.util.Vector;

import javax.swing.plaf.ColorUIResource;

import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.pview.VaPViewImpl.VaAsyncEvent;
import com.glaway.mpm.visual.view.tree.VaTree;
import com.glaway.mpm.visual.view.ui.VaPViewScenePanel;
import com.ptc.pview.annotations.ArrowType;
import com.ptc.pview.annotations.LeaderLine;
import com.ptc.pview.dg.Color;
import com.ptc.pview.dg.DPoint3D;
import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.pvapps.PviewInit;
import com.ptc.pview.pvkapp.AnnoSetTable;
import com.ptc.pview.pvkapp.AnnoSetTableEntry;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.AsyncEventIfImpl;
import com.ptc.pview.pvkapp.EmbeddedControl;
import com.ptc.pview.pvkapp.Instance;
import com.ptc.pview.pvkapp.Kernel;
import com.ptc.pview.pvkapp.PVWindow;
import com.ptc.pview.pvkapp.SelectionController;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeView;
import com.ptc.pview.pvkapp.Structure;
import com.ptc.pview.pvkapp.Window;
import com.ptc.pview.pvkapp.World;
import com.ptc.pview.pvloader.RemoteIf;
import com.ptc.pview.utils.dom.Exception;

public class PViewAnnotation {
	private VaLogger log = VaLogger.getLogger();
	private static PviewInit pviewInit = new PviewInit();
	private final static boolean pviewInstalled;
	private final static boolean pviewInitialized;

	private final static boolean isInPViewMode = false;
	private final static String controlActorName = isInPViewMode ? "pview" : "thumbnail";

	private String name;
	private VaPVActor actor;
	private Kernel kernel;
	private EmbeddedControl embeddedControl;

	private volatile boolean pviewClientIntialized = false;

	private World world;
	private PVWindow pvWindow;
	private Window theWindow;
	
	private Structure structure;

	private VaPViewScenePanel panelContext;
	private AnnoSetTable table;

	private SelectionObserver selectionObserver;
	private SelectionController selectionController;
	private ShapeScene shapeScene;
	public String pvsPath = System.getProperty("user.home") + "/pvs/demo.pvs";
	public String annoSet = null;
	public boolean block = false;

	static {
		pviewInstalled = pviewInit.IsPviewInstalled();
		pviewInitialized = pviewInstalled ? pviewInit.Start("webserver") : false;
	}
	/**
	 * 把ol保存为pvs
	 */
	public void saveAsPvs() {
		try {
			File pvs = new File(pvsPath.substring(0, pvsPath.lastIndexOf("/")));
			if (!pvs.exists()) {
				pvs.mkdirs();
			}
			world.SaveStructure(pvsPath, true, getAsyncEventIf("init"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打开pvs时初始化
	 */
	public void init() {
		try {

			pviewInit = new PviewInit();
			actor = new VaPVActor();
			Kernel kernel = actor.getKernel();

			theWindow = kernel.GetWindow();
			pvWindow = new PVWindow(theWindow, panelContext);
			embeddedControl = kernel.GetEmbeddedControl();
			world = embeddedControl.CreateWorld();
			world.SetParentWindow(pvWindow.GetWindow());
			embeddedControl.SetAutoLoad("auto");
			embeddedControl.SetBackgroundColor(0xA0A0A0, 0xCCCCCC);

			world.SetControlActor("thumbnail");
			// theWorld.SetControlActor("pview");

//			embeddedControl.Initialise(getAsyncEventIf("openFile"));
			embeddedControl.AttachUIToStructure();
			pviewClientIntialized = true;
		} catch (Throwable x) {
			x.printStackTrace();
		}
	}

	/**
	 * 打开pvs
	 */
	public void openFile(String filepath, String finishAnimFrameTaskId) {

		log.debug("openFile : " + filepath);
		try {
			// if(filepath.endsWith(".pvs")){
			// log.debug("load anno");
			// embeddedControl.URLOpen(filepath, "", "",
			// getAsyncEventIf("LoadAnnotation"));}
			// else{
			// embeddedControl.URLOpen(filepath, "", "",
			// getAsyncEventIf("ZoomAll"));
			// }

			embeddedControl.URLOpen(filepath, "", "", this.getAsyncEvent(finishAnimFrameTaskId).GetAsyncEventIf());

		} catch (Throwable x) {
			x.printStackTrace();
		}
	}

	public AsyncEventCB getAsyncEvent(String reason) {
		return actor.getAsyncEvent(reason);
	}
	/**
	 * 创建或加载注释集
	 */
	public void loadAnnotationSet(String annoName,String finishAnimFrameTaskId) {
		try {
			shapeScene = this.panelContext.getShapeScene();
			table = world.GetAnnoSetTable();
			int num = table.GetNumEntries();
			log.debug("NumOfEntries : " + num);
			for (int i = 0; i < num; i++) {
				AnnoSetTableEntry entry = table.GetAnnoSetTableEntry(i);
				if(annoName.equals(entry.GetName())){
					shapeScene.ApplyAnnotations(entry, world.GetTree(),this.getAsyncEvent(finishAnimFrameTaskId).GetAsyncEventIf());
					break;
				}
			}
//			embeddedControl.LoadAnnotationSet(annoName);
//			shapeScene = world.GetFirstShapeScene();
//			this.panelContext.getShapeView().ZoomAll(this.getAsyncEvent(finishAnimFrameTaskId).GetAsyncEventIf());
//			
			
//			if (num > 0) {
//				AnnoSetTableEntry entry = table.GetAnnoSetTableEntry(0);
//				shapeScene.ApplyAnnotations(entry, world.GetTree(), getAsyncEventIf("ZoomAll"));
//				annoSet = entry.GetName();
//				// } else {
//				// annoSet = "mySet";
//				// shapeScene.CreateAnnotation(getAsyncEventIf("CreateAnnotation"),
//				// shapeScene.GetShapeView(0), annoSet, null, null, null, null);
//			}
			// annoSet = "mySet";
			// shapeScene.CreateAnnotation(getAsyncEventIf("CreateAnnotation"),
			// shapeScene.GetShapeView(0), annoSet, null, null, null, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开启添加注释
	 */
	public void openAnnotation(VaTree tree) {
		try {
			if (actor != null) {
				// this.getSelectionObserverrver()
				selectionController = shapeScene.GetSelectionController();
				if (selectionObserver == null) {
					selectionObserver = new MySelectionObserver1(tree);
					actor.ManageObject(selectionObserver);
				}
				selectionController.RegisterObserver(selectionObserver.GetObjectId(), selectionObserver.GetOwner());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 关闭添加注释
	 */
	public void closeAnnotation() {
		try {
			if (actor != null && selectionObserver != null) {
				selectionController.UnregisterObserver(selectionObserver.GetObjectId(), selectionObserver.GetOwner());
				selectionObserver = null;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 保存注释集
	 */
	public void saveAnnotation() {
		try {
			AsyncEventIfImpl saveAnno = getAsyncEventIf("saveAnno");
			table.SaveAnnoSetTableEntry(annoSet, saveAnno);
			// shapeScene.SaveAnnotation(saveAnno, shapeScene.GetShapeView(0),
			// null, null, null, null);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public Vector<String> getAnnotationSetNames() {
		try {
			shapeScene = this.panelContext.getShapeScene();
			table = world.GetAnnoSetTable();
			int num = table.GetNumEntries();
			Vector<String> names = new Vector<String>();
			if (num > 0) {
				for (int i = 0; i < num; i++) {
					names.add(table.GetAnnoSetTableEntry(i).GetName());
				}
				log.debug(names);
			}

			return names;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public AsyncEventIfImpl getAsyncEventIf(String reason) {
		AsyncEventCB asyncEvent = new VaAsyncEvent1(reason);
		AsyncEventIfImpl impl = asyncEvent.GetAsyncEventIf();
		actor.ManageObject(impl);
		return impl;
	}
	
	public class VaAsyncEvent1 extends AsyncEventCB {
		public VaAsyncEvent1(String reason) {
			m_description = reason;
		}

		public void OnProgress(long progress) {
		}

		public void OnComplete(long status) {
			try {
				if (m_description == "Initialise") {
					if (status == 0) {
						if (panelContext.getShapeView() != null)
							panelContext.getShapeView().Destroy();
						if (panelContext.getShapeScene() != null)
							panelContext.getShapeScene().Destroy();

						if (isInPViewMode) {
							theWindow = kernel.GetWindow();
							pvWindow = new PVWindow(theWindow, panelContext);
							embeddedControl.AttachUIToStructure();
						}

						VaHttpDownloadProtocolHandlerEvents dphe = new VaHttpDownloadProtocolHandlerEvents();
						RemoteIf theRemoteIf = kernel.GetRemoteInterface();
						actor.GetProtocolHandler(dphe, "http:https", theRemoteIf);
						dphe.setRemoteIf(theRemoteIf);

						shapeScene = world.CreateShapeScene();
						ShapeView shapeView = shapeScene.CreateShapeView(theWindow);

						float[] colorCompsBlue = new ColorUIResource(158, 155, 145).getRGBComponents(null);// CmTheme.CM_PVIEW1.getRGBComponents(null);
						float[] colorCompsTurquoise = new ColorUIResource(227, 225, 213).getRGBComponents(null);// CmTheme.CM_PVIEW2.getRGBComponents(null);
						shapeView.SetGradientBackgroundColor(//
								new Color(colorCompsTurquoise[0], colorCompsTurquoise[1], colorCompsTurquoise[2],
										colorCompsTurquoise[3]), //
								new Color(colorCompsBlue[0], colorCompsBlue[1], colorCompsBlue[2], colorCompsBlue[3])//
								);

						panelContext.setShapeScene(shapeScene);
						panelContext.setShapeView(shapeView);
						log.debug("initpview");
						pviewClientIntialized = true;

					} else {
						System.exit(0);
					}
				} else if (m_description == "init") {
					
				} else if (m_description == "openFile") {
					// openFile(pvsPath);
				} else if (m_description == "LoadAnnotation") {
					// loadAnnotationSet();
					
				} else if (m_description == "fileOpened") {
					// loadAnnotationSet();
//					fileOpened();
				} else if (m_description == "ZoomAll") {
					if (status == 0) {
						log.debug("zoom all complete");
					} else {
						log.debug("zoom all failed");
					}
				}
			} catch (java.lang.Exception ex) {
				ex.printStackTrace();
			}
		}

		private String m_description;
	}
	
	public class MySelectionObserver1 extends SelectionObserver {
		private VaTree vTree;

		public MySelectionObserver1(VaTree vaTree) {
			// TODO Auto-generated constructor stub
			this.vTree = vaTree;
		}

		protected void OnBeginUpdate() {
		}

		protected void OnEndUpdate() {
		}

		protected void OnInsertItems(Instance[] items, long recurseMask) {
			try {
				log.debug("len:" + items.length + "\t\t recurseMask:" + recurseMask);
				for (int i = 0; i < items.length; i++) {
					Instance inst = items[i];
					String partNumber = vTree.getNodeFromInstance(inst).getPart().getNumber();
					log.debug("objectClass:" + inst.GetObjectClass() + "\t\tname:" + partNumber);
					DPoint3D dPoint3D = selectionController.GetPickPoint(inst);
					com.ptc.pview.dg.DPoint3D[] points = new com.ptc.pview.dg.DPoint3D[2];
					points[0] = new com.ptc.pview.dg.DPoint3D(dPoint3D.Get(0), dPoint3D.Get(1), dPoint3D.Get(2) + 0.004);
					points[1] = dPoint3D;
					LeaderLine leaderLine = (LeaderLine) shapeScene.CreateAnnotation("LeaderLine");

					if (leaderLine != null) {
						shapeScene.AddAnnotation(leaderLine);
						leaderLine.SetLineWidth(1);
						leaderLine.SetLineColor(new Color(255, 0, 0, 255));
						leaderLine.SetPoints(points);
						leaderLine.SetArrowType(ArrowType.HEAD_NONE_TAIL_ARROW);

						embeddedControl.AddAnnotationLabel(partNumber, 10, 0x0, 0xffffff);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		protected void OnRemoveItems(Instance[] items, long recurseMask) {
		}

		protected void OnClearSelection() {
		}

		public String GetObjectClass() {
			return "pvapps::javatestapp::ConfigExampleSelectionObserver";
		}
	}
}
