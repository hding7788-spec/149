package com.glaway.mpm.visual.view.pview;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import javax.media.j3d.BoundingBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.plaf.ColorUIResource;
import javax.vecmath.Point3d;

import com.glaway.mpm.task.CmTaskInfo;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.ui.VaPViewScenePanel;
import com.glaway.mpm.visual.view.ui.VaPViewSearchPanel;
import com.ptc.pview.dg.Color;
import com.ptc.pview.dg.DPoint3D;
import com.ptc.pview.dg.DVec3;
import com.ptc.pview.dg.FBox;
import com.ptc.pview.pvapi.OrientationsObserver;
import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.pvapi.ShapeSceneObserver;
import com.ptc.pview.pvapps.PviewInit;
import com.ptc.pview.pvkapp.AnnoSetTable;
import com.ptc.pview.pvkapp.AnnoSetTableEntry;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.AsyncEventIfImpl;
import com.ptc.pview.pvkapp.BoundingMarkup;
import com.ptc.pview.pvkapp.EmbeddedControl;
import com.ptc.pview.pvkapp.Instance;
import com.ptc.pview.pvkapp.InstanceVisitor;
import com.ptc.pview.pvkapp.InstanceVisitorEvents;
import com.ptc.pview.pvkapp.Kernel;
import com.ptc.pview.pvkapp.OrientationType;
import com.ptc.pview.pvkapp.Orientations;
import com.ptc.pview.pvkapp.PVWindow;
import com.ptc.pview.pvkapp.SelectionController;
import com.ptc.pview.pvkapp.ShapeInstance;
import com.ptc.pview.pvkapp.ShapeInstance_holder;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeView;
import com.ptc.pview.pvkapp.Structure;
import com.ptc.pview.pvkapp.Tree;
import com.ptc.pview.pvkapp.ViewStateSource;
import com.ptc.pview.pvkapp.ViewStateVisitor;
import com.ptc.pview.pvkapp.ViewStateVisitorEvents;
import com.ptc.pview.pvkapp.Window;
import com.ptc.pview.pvkapp.World;
import com.ptc.pview.pvloader.RemoteIf;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.DoubleHolder;
import com.ptc.pview.utils.dom.Exception;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

public class VaPViewImpl {

	private VaLogger log = VaLogger.getLogger();
	// pview define
	private static PviewInit pviewInit = new PviewInit();
	private final static boolean pviewInstalled;
	private final static boolean pviewInitialized;

	private final static boolean isInPViewMode = false;
	private final static String controlActorName = isInPViewMode ? "pview"
			: "thumbnail";

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
	public String openFilePath;

	// 视图，方位，注释集加载相关

	Orientations orientation;
	AnnoSetTable annoSetTable;
	MyOrientationsObserver orientObs;
	MyInstanceVisitorEvents ive;
	InstanceVisitor instVisitor;

	Tree tree;
	ArrayList<ViewStateSource> listOfViewStates = new ArrayList<ViewStateSource>();
	ArrayList<String> listOfOrientationsName = new ArrayList<String>();
	ArrayList<OrientationType> listOfOrientationsType = new ArrayList<OrientationType>();
	ArrayList<AnnoSetTableEntry> listOfAnnotationsName = new ArrayList<AnnoSetTableEntry>();

	// bounding box
	private BoundingMarkup bbm;
	VaPViewSearchPanel panel;
	private VaPropertiesVisitorImpl propertiesVisitor;

	VaMarkupObserver mo;

	// private List<FBox> partFboxes;
	private List<BoundingBox> partBboxes;
	private List<Instance> instList;

	static {
		pviewInstalled = pviewInit.IsPviewInstalled();
		pviewInitialized = pviewInstalled ? pviewInit.Start("webserver")
				: false;
	}

	public boolean isPviewClientIntialized() {
		return pviewClientIntialized;
	}

	public void waitforClientIntialized() {
		while (!isPviewClientIntialized()) {
			try {
				StackTraceElement ste = new Throwable().getStackTrace()[1];
				log.debug("waitforClientIntialized in ", ste.getClassName(),
						".", ste.getMethodName(), "().", ste.getLineNumber());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}

	public static boolean isPviewInitialized() {
		return pviewInitialized;
	}

	public static boolean isPviewInstalled() {
		return pviewInstalled;
	}

	public String getName() {
		return this.name;
	}

	VaPViewImpl(String name) {
		this.name = name;

		pviewClientIntialized = false;

		panelContext = new VaPViewScenePanel(name);
	}

	public synchronized void initPView() {
		try {
			if (actor == null) {
				actor = new VaPVActor();
				kernel = actor.getKernel();
				embeddedControl = kernel.GetEmbeddedControl();

			}

			initPviewWorld(panelContext);
		} catch (Exception e) {
			log.error(e);
		}
	}

	private synchronized void initPviewWorld(VaPViewScenePanel panel)
			throws ConnectionLostException, ActorShutdownException,
			MessageProtocolException, InvalidActorException {
		if (theWindow == null)
			theWindow = kernel.GetWindow();
		if (pvWindow == null)
			pvWindow = new PVWindow(theWindow, panel);

		if (world == null) {
			if (embeddedControl.GetWorld() != null) {
				world = embeddedControl.GetWorld();
			} else {
				world = embeddedControl.CreateWorld();
			}

		}
		world.SetControlActor(controlActorName);
		// if (this.name.equals(VaPViewFactory.PV_NAME_DP)) {
		// log.debug("controlActorName : " + controlActorName);
		// world.SetControlActor("pview");
		// } else {
		// world.SetControlActor(controlActorName);
		// }
		world.SetParentWindow(pvWindow.GetWindow());

		structure = world.CreateStructure();
		world.CreateTree();

		embeddedControl.SetAutoLoad("auto");
		embeddedControl.SetBackgroundColor(0xA0A0A0, 0xCCCCCC);

		embeddedControl.Initialise(getAsyncEventIf("Initialise"));
	}

	public void shutdown() {
		try {
			cleanupPvWorld();
			if (actor != null) {
				actor.shutdown();
				actor = null;
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	private void cleanupPvWorld() throws ConnectionLostException,
			ActorShutdownException, MessageProtocolException,
			InvalidActorException {
		if (world != null) {
			world.ClearControlActor(controlActorName);
			world.RemoveContent();
			world.Destroy();
			world = null;
		}

	}

	public void resetPvWorld(VaPViewGenerator ge)
			throws ConnectionLostException, ActorShutdownException,
			MessageProtocolException, InvalidActorException {
		pviewClientIntialized = false;

//		 cleanupPvWorld();

		cleanup();
		initPviewWorld(panelContext);
	}

	public void cleanup() {
		try {
			if (this.shapeScene != null) {
				this.shapeScene.RemoveAllShapeInstances();
			}
			if (this.structure != null) {
				this.structure.RemoveAllComponents();
			}
		} catch (Exception localException) {
			localException.printStackTrace();
			return;
		}
	}

	public Structure getStructure() {
		return structure;
	}

	public VaPViewScenePanel getPanelContext() {
		return panelContext;
	}

	public void setPanelContext(VaPViewScenePanel panelContext) {
		this.panelContext = panelContext;
	}

	public void initObsContext(SelectionObserver so) {
		try {
			SelectionController sc = panelContext.getShapeScene()
					.GetSelectionController();
			so = actor.getSelectionObserver(so);
			sc.RegisterObserver(so.GetObjectId(), so.GetOwner());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AsyncEventCB getAsyncEvent(String reason) {
		return actor.getAsyncEvent(reason);
	}

	public String savePVS(String pvsPath) {

		try {
			Structure s = world.GetStructure();
			String retPath = null;
			if (s == null || s.IsEmpty()) {
				JOptionPane.showMessageDialog(null, "未加载图形，不能保存pvs");
				return null;
			} else {
				retPath = world.SaveStructure(pvsPath, true,
						getAsyncEventIf("savePVS"));
			}
			return retPath;
		} catch (MessageProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void createBoundingBox(VaPViewSearchPanel panel) {
		this.panel = panel;
		BoundingBox bb = panel.getSearchBoxValue();
		Point3d p1 = new Point3d();
		Point3d p2 = new Point3d();
		bb.getLower(p1);
		bb.getUpper(p2);

		DPoint3D point = new DPoint3D((p1.x + p2.x) / 2, (p1.y + p2.y) / 2,
				(p1.z + p2.z) / 2);
		DVec3 d = new DVec3(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
		try {
			ShapeScene ss = embeddedControl.GetWorld().GetFirstShapeScene();

			if (bbm != null) {
				ss.RemoveMarkup(bbm);
				bbm = null;
			}

			bbm = ss.CreateBoundingBoxMarkup(point, d);

			// ss.CreateBoundingSphereMarkup(point, 1.0);
			// SelectionController sc = ss.GetSelectionController();
			// VaMarkupObserver so = new VaMarkupObserver();
			// this.actor.ManageObject(so);
			// bbm.RegisterObserver(so.GetObjectId(), so.GetOwner());

		} catch (MessageProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// void fileOpened() {
	// AsyncEventCB zoomAllAsyncEvent =
	// this.getAsyncEvent("CreoViewPanel.finishAnimFrame");
	// try {
	// panelContext.getShapeView().ZoomAll(zoomAllAsyncEvent.GetAsyncEventIf());
	// } catch (MessageProtocolException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ActorShutdownException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (InvalidActorException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ConnectionLostException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	public void openFile(String filepath, String finishAnimFrameTaskId) {
		try {

			this.listOfViewStates.clear();
			this.listOfOrientationsName.clear();
			this.listOfOrientationsType.clear();
			this.listOfAnnotationsName.clear();
			openFilePath = filepath;
			this.embeddedControl.URLOpen(filepath, "", "",
					getAsyncEventIf(finishAnimFrameTaskId));

			log.debug("File Path : " + filepath);
		} catch (Throwable x) {
			x.printStackTrace();
		}
	}

	void fileOpened() {
		try {
			if (this.name.equals(VaPViewFactory.PV_NAME_MBOM)) {
				// 第二次打开参装工具报错，取消Orientation的显示
				// orientation = this.world.GetOrientations();

				annoSetTable = this.world.GetAnnoSetTable();

				// orientObs = new MyOrientationsObserver();
				// this.actor.ManageObject(orientObs);
				// orientation.RegisterObserver(orientObs.GetObjectId(),
				// orientObs.GetOwner());

				ive = new MyInstanceVisitorEvents();
				instVisitor = this.actor.getInstanceVisitor(ive);

				for (int i = 0; i < annoSetTable.GetNumEntries(); i++) {
					log.debug("annotations : "
							+ annoSetTable.GetAnnoSetTableEntry(i).GetName());
					this.listOfAnnotationsName.add(annoSetTable
							.GetAnnoSetTableEntry(i));
				}

				Instance root = embeddedControl.GetWorld().GetTree().GetRoot();

				if (!root.GetName().endsWith(".ol")) {
					root.Visit(instVisitor, 0);
				}

			}
			embeddedControl.AttachUIToStructure();
			// ShapeScene ss = embeddedControl.GetWorld().GetFirstShapeScene();
			// panelContext.setShapeScene(ss);
			// panelContext.setShapeView(ss.GetShapeView(0));
			// SelectionController sc = ss.GetSelectionController();
			// SelectionObserver so = this.getSelectionObserver();
			// so = this.actor.getSelectionObserver(so);
			// sc.RegisterObserver(so.GetObjectId(), so.GetOwner());

			ShapeScene ss = embeddedControl.GetWorld().GetFirstShapeScene();
			panelContext.setShapeScene(ss);
			panelContext.setShapeView(ss.GetShapeView(0));
			SelectionController sc = ss.GetSelectionController();
			SelectionObserver so = this.getSelectionObserver();
			so = this.actor.getSelectionObserver(so);

			log.debug("ObserverRegister");
			sc.RegisterObserver(so.GetObjectId(), so.GetOwner());

		} catch (Throwable x) {
			x.printStackTrace();
		}
	}

	public BoundingBox getBoundingBox(String idpath) {
		BoundingBox bbox = new BoundingBox();
		Point3d lower = new Point3d();
		Point3d upper = new Point3d();
		DoubleHolder dhX1 = new DoubleHolder();
		DoubleHolder dhY1 = new DoubleHolder();
		DoubleHolder dhZ1 = new DoubleHolder();
		DoubleHolder dhX2 = new DoubleHolder();
		DoubleHolder dhY2 = new DoubleHolder();
		DoubleHolder dhZ2 = new DoubleHolder();

		try {
			this.embeddedControl.GetBoundingBox(idpath, dhX1, dhY1, dhZ1, dhX2,
					dhY2, dhZ2);
		} catch (MessageProtocolException e) {
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			e.printStackTrace();
		} catch (InvalidActorException e) {
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			e.printStackTrace();
		}
		log.debug("getBoundingBox : " + idpath);
		log.debug("dhX1 : " + dhX1.value);
		log.debug("dhY1 : " + dhY1.value);
		log.debug("dhZ1 : " + dhZ1.value);
		log.debug("dhX2 : " + dhX2.value);
		log.debug("dhY2 : " + dhY2.value);
		log.debug("dhZ2 : " + dhZ2.value);

		lower.x = dhX1.value;
		lower.y = dhY1.value;
		lower.z = dhZ1.value;
		upper.x = dhX2.value;
		upper.y = dhY2.value;
		upper.z = dhZ2.value;

		bbox.setLower(lower);
		bbox.setUpper(upper);

		return bbox;
	}

	public void registerMarkupObs() {
		try {
			if (embeddedControl != null) {
				ShapeScene ss = embeddedControl.GetWorld().GetFirstShapeScene();
				mo = new VaMarkupObserver();
				this.actor.ManageObject(mo);
				ss.RegisterObserver(mo.GetObjectId(), mo.GetOwner());
			}
		} catch (MessageProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeBbox() {
		if (bbm != null) {
			try {
				ShapeScene ss = embeddedControl.GetWorld().GetFirstShapeScene();
				ss.RemoveMarkup(bbm);
				bbm = null;
			} catch (MessageProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ActorShutdownException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidActorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void unregisterMarkupObs() {
		try {
			if (mo != null && embeddedControl != null) {
				ShapeScene ss = embeddedControl.GetWorld().GetFirstShapeScene();
				ss.UnregisterObserver(mo.GetObjectId(), mo.GetOwner());
			}
		} catch (MessageProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateSearchPanelBBox() {

		BoundingBox bbox = getBBoxFromBoundingMarkup(bbm);
		if (bbox != null) {
			panel.setSearchBoxValue(bbox);
		}
	}

	public void searchPVInstance(String type, List<BoundingBox> bbox) {
		// if (type.equals("bbox") && bbm == null) {
		// JOptionPane.showMessageDialog(null, "请先创建范围再进行搜索!");
		// return;
		// }
		SearchInstanceVisitorEvents sive = new SearchInstanceVisitorEvents(type);
		InstanceVisitor instVisitor = this.actor.getInstanceVisitor(sive);
		instList = new ArrayList<Instance>();
		// this.partFboxes = fbox;
		this.partBboxes = bbox;
		try {
			Tree insTree = embeddedControl.GetWorld().GetTree();
			insTree.Visit(instVisitor, 1);
			// if (!root.GetName().endsWith(".ol")) {
			// insTree.Visit(instVisitor, 0);
			// }
			// 设置选中可视化信息部件
			CmTaskInfo setPViewTaskInfo = CmTaskInfo.newCmTaskInfo(
					"VaEBomTreePanel.setSelectedForInstance", this, instList);

			// 搜寻完后将等待框关闭
			CmTaskInfo closeWaitProgressBarTaskInfo = CmTaskInfo.newCmTaskInfo(
					"VaPViewSearchPanel.closeWaitProgressBar", this, null);
			try {
				CmTaskHelper.sendTask(setPViewTaskInfo, null);
				CmTaskHelper.sendTask(closeWaitProgressBarTaskInfo, null);
			} catch (CmTaskException e) {
				e.printStackTrace();
			}
		} catch (MessageProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void checkInstance(Instance ins, String type) {
		try {
			if (ins.GetComponentNode().GetShapeSource() != null) {
				// FBox fbox =
				// ins.GetComponentNode().GetShapeSource().GetBBox();

				// if (fbox != null) {
				BoundingBox bbox = this.getBoundingBox(ins.GetIDPath());// convertFBoxToBBox(fbox);
				BoundingBox boundingMarkupBox = null;

				if (type.equals("bbox")) {
					if (bbm != null) {
						boundingMarkupBox = getBBoxFromBoundingMarkup(bbm);
					}
					ShapeInstance_holder sh = new ShapeInstance_holder();
					getWorld().GetFirstShapeScene().GetShapeInstance(ins, sh);
					ShapeInstance shapeInstance = sh.value;
					String insName = ins.GetName();
					String insNamePart = insName.split(",")[0];
					if (insNamePart.endsWith(".prt")) {
						log.debug("boundingMarkupBox : " + boundingMarkupBox);
						log.debug("bbox : " + bbox);

						if (shapeInstance != null) {
							if (boundingMarkupBox.intersect(bbox)) {
								log.debug("instance : " + insName
										+ "||||| true");
								shapeInstance.SetVisibility(true);
								// shapeInstance.SetHighlight(true);
								instList.add(shapeInstance.GetInstance());
							} else {
								log.debug("instance : " + insName
										+ "||||| false");
								shapeInstance.SetVisibility(false);
								// shapeInstance.SetHighlight(false);
								instList.remove(shapeInstance.GetInstance());
							}
						}
					}
				} else if (type.equals("part")) {
					if (partBboxes != null && partBboxes.size() > 0) {
						for (int i = 0; i < partBboxes.size(); i++) {

							boundingMarkupBox = partBboxes.get(i);// convertFBoxToBBox(partFboxes.get(i));
							ShapeInstance_holder sh = new ShapeInstance_holder();
							getWorld().GetFirstShapeScene().GetShapeInstance(
									ins, sh);
							ShapeInstance shapeInstance = sh.value;
							String insName = ins.GetName();
							String insNamePart = insName.split(",")[0];
							if (insNamePart.endsWith(".prt")) {
								log.debug("boundingMarkupBox : "
										+ boundingMarkupBox);
								log.debug("bbox : " + bbox);

								if (shapeInstance != null) {
									if (boundingMarkupBox.intersect(bbox)) {
										log.debug("instance : " + insName
												+ "||||| true");
										shapeInstance.SetVisibility(true);
										// shapeInstance.SetHighlight(true);
										instList.add(shapeInstance
												.GetInstance());
									} else {
										log.debug("instance : " + insName
												+ "||||| false");
										shapeInstance.SetVisibility(false);
										// shapeInstance.SetHighlight(false);
										instList.remove(shapeInstance
												.GetInstance());
									}
								}
							}
						}

					}
				}

			}
			// }

		} catch (MessageProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BoundingBox getBBoxFromBoundingMarkup(BoundingMarkup bm) {
		BoundingBox bbox = new BoundingBox();

		DPoint3D center = new DPoint3D(0.0, 0.0, 0.0);
		DVec3 vec = new DVec3(0.0, 0.0, 0.0);
		try {
			if (bbm != null) {
				center = bbm.GetCenter();
				vec = bbm.GetDimensions();
				Point3d p1 = new Point3d(center.Get(0) - vec.Get(0) / 2,
						center.Get(1) - vec.Get(1) / 2, center.Get(2)
								- vec.Get(2) / 2);
				Point3d p2 = new Point3d(center.Get(0) + vec.Get(0) / 2,
						center.Get(1) + vec.Get(1) / 2, center.Get(2)
								+ vec.Get(2) / 2);

				bbox.setLower(p1);
				bbox.setUpper(p2);
				return bbox;
			}
		} catch (MessageProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private BoundingBox convertFBoxToBBox(FBox fbox) {

		Point3d p1 = new Point3d(fbox.GetMin(0), fbox.GetMin(1), fbox.GetMin(2));
		Point3d p2 = new Point3d(fbox.GetMax(0), fbox.GetMax(1), fbox.GetMax(2));

		log.debug("Xmin : " + fbox.GetMin(0) + ", Ymin : " + fbox.GetMin(1)
				+ ", " + "Zmin : " + fbox.GetMin(2));
		log.debug("Xmax : " + fbox.GetMax(0) + ", Ymax : " + fbox.GetMax(1)
				+ ", " + "Zmax : " + fbox.GetMax(2));
		BoundingBox bbox = new BoundingBox(p1, p2);
		return bbox;
	}

	public class VaAsyncEvent extends AsyncEventCB {
		public VaAsyncEvent(String reason) {
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
						actor.GetProtocolHandler(dphe, "http:https",
								theRemoteIf);
						dphe.setRemoteIf(theRemoteIf);

						if (true || name.equals(VaPViewFactory.PV_NAME_DP)) {
							//放开代码
							shapeScene = world.CreateShapeScene();
							ShapeView shapeView = shapeScene
									.CreateShapeView(theWindow);

							float[] colorCompsBlue = new ColorUIResource(158,
									155, 145).getRGBComponents(null);// CmTheme.CM_PVIEW1.getRGBComponents(null);
							float[] colorCompsTurquoise = new ColorUIResource(
									227, 225, 213).getRGBComponents(null);// CmTheme.CM_PVIEW2.getRGBComponents(null);
							shapeView.SetGradientBackgroundColor(//
									new Color(colorCompsTurquoise[0],
											colorCompsTurquoise[1],
											colorCompsTurquoise[2],
											colorCompsTurquoise[3]), //
									new Color(colorCompsBlue[0],
											colorCompsBlue[1],
											colorCompsBlue[2],
											colorCompsBlue[3])//
									);

							panelContext.setShapeScene(shapeScene);
							panelContext.setShapeView(shapeView);
//							--------
						}
						log.debug("initpview");
						propertiesVisitor = actor.getPropertiesVisitor();
						pviewClientIntialized = true;

					} else {
						// System.exit(0);
						return;
					}
				} else if (m_description == "init") {

				} else if ((this.m_description != null)
						&& (this.m_description.endsWith(".finishAnimFrame"))) {
					log.debug(this.m_description);
					VaPViewImpl.this.fileOpened();
					try {
						Thread.sleep(0L);
						CmTaskHelper.sendTask(CmTaskInfo.newCmTaskInfo(
								this.m_description, this, null), null);
					} catch (CmTaskException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else if (m_description == "openFile") {
					// openFile(pvsPath);
				} else if (m_description == "fileOpened") {
					// loadAnnotationSet();
					// fileOpened();
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

	public void selectInstance(String idPath) {
		try {
			this.embeddedControl.SelectInstance(idPath);
		} catch (MessageProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public AsyncEventIfImpl getAsyncEventIf(String reason) {
		AsyncEventCB asyncEvent = new VaAsyncEvent(reason);
		AsyncEventIfImpl impl = asyncEvent.GetAsyncEventIf();
		actor.ManageObject(impl);
		return impl;
	}

	public static PviewInit getPviewInit() {
		return pviewInit;
	}

	public World getWorld() {
		try {
			return this.embeddedControl.GetWorld();
		} catch (MessageProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidActorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public VaPropertiesVisitorImpl getPropertiesVisitor(
			Collection<String[]> properties) {
		propertiesVisitor.setProperties(properties);
		return propertiesVisitor;
	}

	public SelectionObserver getSelectionObserver() {
		return selectionObserver;
	}

	public ShapeScene getShapeScene() {
		return shapeScene;
	}

	public VaPVActor getActor() {
		return actor;
	}

	public SelectionController getSelectionController() {
		return selectionController;
	}

	public void setSelectionController(SelectionController selectionController) {
		this.selectionController = selectionController;
	}

	public void setSelectionObserver(SelectionObserver selectionObserver) {
		this.selectionObserver = selectionObserver;
	}

	public void buildToolBarForFittingTool(JToolBar toolBar) {
		try {
			log.debug("init toolBar");
			// menuBar.removeAll();
			JPanel menuBar = new JPanel();
			menuBar.setLayout(new FlowLayout());
			JMenu viewStatelistMenu = new JMenu();
			JMenu orientationslistMenu = new JMenu();
			JMenu annotationslistMenu = new JMenu();

			ActionListener vsl = new ViewstateMenuSelListener();
			ActionListener otl = new OrientationsMenuSelListener();
			ActionListener atl = new AnnotationsMenuSelListener();

			ListIterator li = this.listOfViewStates.listIterator();

			int i = 0;
			while (li.hasNext()) {
				ViewStateSource item = (ViewStateSource) li.next();
				JMenuItem vss = new JMenuItem();
				vss.setText(item.GetName());
				vss.setName(i + "");
				JOptionPane.showMessageDialog(null, vss.getText());
				vss.addActionListener(vsl);
				viewStatelistMenu.add(vss);
				i++;
			}
			log.debug("ViewStateSource : " + i);
			if (viewStatelistMenu.getItemCount() > 0) {
				viewStatelistMenu.setText("视图");
				viewStatelistMenu.getPopupMenu().setLightWeightPopupEnabled(
						false);

				menuBar.add(viewStatelistMenu);
			}
			int j = 0;
			ListIterator li1 = this.listOfOrientationsName.listIterator();

			while (li1.hasNext()) {
				String name = (String) li1.next();
				JMenuItem orient = new JMenuItem();
				orient.setText(name);
				orient.setName(j + "");
				JOptionPane.showMessageDialog(null, orient.getText());
				orient.addActionListener(otl);
				orientationslistMenu.add(orient);
				j++;
			}
			log.debug("listOfOrientationsName : " + j);
			if (orientationslistMenu.getItemCount() > 0) {
				orientationslistMenu.setText("方位");
				orientationslistMenu.getPopupMenu().setLightWeightPopupEnabled(
						false);
				menuBar.add(orientationslistMenu);
			}
			int k = 0;
			ListIterator li2 = this.listOfAnnotationsName.listIterator();

			while (li2.hasNext()) {
				AnnoSetTableEntry annos = (AnnoSetTableEntry) li2.next();
				JMenuItem anno = new JMenuItem();
				anno.setText(annos.GetName());
				anno.setName(k + "");
				JOptionPane.showMessageDialog(null, anno.getText());
				anno.addActionListener(atl);
				annotationslistMenu.add(anno);
				k++;
			}
			log.debug("AnnoSetTableEntry : " + k);
			if (annotationslistMenu.getItemCount() > 0) {
				annotationslistMenu.setText("注释集");
				annotationslistMenu.getPopupMenu().setLightWeightPopupEnabled(
						false);
				menuBar.add(annotationslistMenu);
			}
			toolBar.add(menuBar);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public JMenuBar initMenuBar() {
		try {
			log.debug("init menuBar");
			JMenuBar menuBar = new JMenuBar();
			// menuBar.removeAll();
			JMenu viewStatelistMenu = new JMenu();
			JMenu orientationslistMenu = new JMenu();
			JMenu annotationslistMenu = new JMenu();

			ActionListener vsl = new ViewstateMenuSelListener();
			ActionListener otl = new OrientationsMenuSelListener();
			ActionListener atl = new AnnotationsMenuSelListener();

			ListIterator li = this.listOfViewStates.listIterator();

			int i = 0;
			while (li.hasNext()) {
				ViewStateSource item = (ViewStateSource) li.next();
				JMenuItem vss = new JMenuItem();
				vss.setText(item.GetName());
				vss.setName(i + "");

				vss.addActionListener(vsl);
				viewStatelistMenu.add(vss);
				i++;
			}
			log.debug("ViewStateSource : " + i);
			if (viewStatelistMenu.getItemCount() > 0) {
				viewStatelistMenu.setText("视图");
				viewStatelistMenu.getPopupMenu().setLightWeightPopupEnabled(
						false);

				menuBar.add(viewStatelistMenu);
			}
			int j = 0;
			ListIterator li1 = this.listOfOrientationsName.listIterator();

			while (li1.hasNext()) {
				String name = (String) li1.next();
				JMenuItem orient = new JMenuItem();
				orient.setText(name);
				orient.setName(j + "");

				orient.addActionListener(otl);
				orientationslistMenu.add(orient);
				j++;
			}
			log.debug("listOfOrientationsName : " + j);
			if (orientationslistMenu.getItemCount() > 0) {
				orientationslistMenu.setText("方位");
				orientationslistMenu.getPopupMenu().setLightWeightPopupEnabled(
						false);
				menuBar.add(orientationslistMenu);
			}
			int k = 0;
			ListIterator li2 = this.listOfAnnotationsName.listIterator();

			while (li2.hasNext()) {
				AnnoSetTableEntry annos = (AnnoSetTableEntry) li2.next();
				JMenuItem anno = new JMenuItem();
				anno.setText(annos.GetName());
				anno.setName(k + "");

				anno.addActionListener(atl);
				annotationslistMenu.add(anno);
				k++;
			}
			log.debug("AnnoSetTableEntry : " + k);
			if (annotationslistMenu.getItemCount() > 0) {
				annotationslistMenu.setText("注释集");
				annotationslistMenu.getPopupMenu().setLightWeightPopupEnabled(
						false);
				menuBar.add(annotationslistMenu);
			}

			return menuBar;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	class AnnotationsMenuSelListener implements ActionListener {
		AnnotationsMenuSelListener() {
		}

		public void actionPerformed(ActionEvent e) {
			JMenuItem itm = (JMenuItem) e.getSource();

			AnnoSetTableEntry selAnnotationName = (AnnoSetTableEntry) VaPViewImpl.this.listOfAnnotationsName
					.get(Integer.parseInt(itm.getName()));
			log.debug("Setting annotation : " + selAnnotationName);
			try {
				VaPViewImpl.this.world.GetFirstShapeScene().ApplyAnnotations(
						selAnnotationName, VaPViewImpl.this.world.GetTree(),
						null);
			} catch (Throwable x) {
				x.printStackTrace();
				VaPViewImpl.this.log.debug(x);
			}
			log.debug("setAnno");
		}
	}

	class SearchInstanceVisitorEvents extends InstanceVisitorEvents {

		private String searchType;

		public SearchInstanceVisitorEvents(String type) {
			this.searchType = type;
		}

		public boolean Visit(Instance inst, Instance parent, int depth) {
			try {

				log.debug("visit : instance name : " + inst.GetName());

				checkInstance(inst, searchType);

				// ShapeInstance_holder sh = new ShapeInstance_holder();
				// getWorld().GetFirstShapeScene().GetShapeInstance(inst, sh);
				// ShapeInstance shapeInstance = sh.value;
				//
				// if(shapeInstance != null){
				// log.debug("shapeInstance : "+shapeInstance.GetInstance().GetName());
				// shapeInstance.SetHighlight(true);
				// }
				Instance broIns = inst.GetNextSibling();
				while (broIns != null) {
					log.debug("visit : instance name : " + inst.GetName());
					checkInstance(broIns, searchType);
					// ShapeInstance_holder sh2 = new ShapeInstance_holder();
					// getWorld().GetFirstShapeScene().GetShapeInstance(broIns,sh2);
					// ShapeInstance shapeInstance2 = sh2.value;
					// if(shapeInstance2 != null){
					// log.debug("shapeInstance : "+shapeInstance2.GetInstance().GetName());
					//
					// shapeInstance2.SetHighlight(true);}
					broIns = broIns.GetNextSibling();
				}

			} catch (Throwable x) {
				x.printStackTrace();
				return false;
			}
			return true;
		}
	}

	class MyInstanceVisitorEvents extends InstanceVisitorEvents {
		ArrayList<Instance> list = new ArrayList();

		public MyInstanceVisitorEvents() {
		}

		public boolean Visit(Instance inst, Instance parent, int depth) {
			try {
				MyViewStateVisitorEvents vsve = new MyViewStateVisitorEvents();
				ViewStateVisitor viewStateVisitor = VaPViewImpl.this.actor
						.getViewStateVisitor(vsve);

				inst.GetComponentNode().Visit(viewStateVisitor);
			} catch (Throwable x) {
				return false;
			}
			return true;
		}
	}

	class MyOrientationsObserver extends OrientationsObserver {
		MyOrientationsObserver() {
		}

		protected void OnBeginUpdate() {
		}

		protected void OnEndUpdate() {
		}

		public String GetObjectClass() {
			return "pvapps::javatestapp::MyOrientationObserver";
		}

		protected void OnOrientationAdded(String name, OrientationType type) {
			log.debug("Orientation name: " + name + ", Orientation type: "
					+ type);
			VaPViewImpl.this.listOfOrientationsName.add(name);
			VaPViewImpl.this.listOfOrientationsType.add(type);
		}

		protected void OnOrientationDeleted(String name, OrientationType type) {
		}

		protected void OnOrientationUpdated(String name, OrientationType type) {
		}

		protected void OnDefaultOrientChanged(String name, OrientationType type) {
			log.debug("MyOrientationObserver.OnDefaultOrientChanged: " + name
					+ "  --  " + type);
		}
	}

	class MyViewStateVisitorEvents extends ViewStateVisitorEvents {
		public MyViewStateVisitorEvents() {
		}

		public boolean Visit(ViewStateSource viewStateSource) {
			try {
				log.debug("    - ViewState name: " + viewStateSource.GetName());
				log.debug("    - ViewState type: " + viewStateSource.GetType());
				log.debug("    -------");
				VaPViewImpl.this.listOfViewStates.add(viewStateSource);
			} catch (Throwable x) {
				return false;
			}
			return true;
		}
	}

	class OrientationsMenuSelListener implements ActionListener {
		OrientationsMenuSelListener() {
		}

		public void actionPerformed(ActionEvent e) {
			JMenuItem itm = (JMenuItem) e.getSource();

			String selOrientationName = (String) VaPViewImpl.this.listOfOrientationsName
					.get(Integer.parseInt(itm.getName()));
			OrientationType selOrientationType = (OrientationType) VaPViewImpl.this.listOfOrientationsType
					.get(Integer.parseInt(itm.getName()));
			log.debug("Setting orientation: " + selOrientationName
					+ ", with type: " + selOrientationType);
			try {
				Orientations orientation = VaPViewImpl.this.world
						.GetOrientations();
				ShapeView shView = VaPViewImpl.this.world.GetFirstShapeScene()
						.GetShapeView(0);
				orientation.SetOrientation(shView, selOrientationName,
						selOrientationType);
			} catch (Throwable localThrowable) {
			}
		}
	}

	class ViewStateSceneObserver extends ShapeSceneObserver {
		private ShapeScene m_shapeScene;

		ViewStateSceneObserver(ShapeScene shapeScene) {
			this.m_shapeScene = shapeScene;
		}

		protected void OnBeginUpdate() {
		}

		protected void OnEndUpdate() {
		}

		protected void OnShapeInstanceVisibility(ShapeInstance shapeInstance,
				long visible) {
		}

		public String GetObjectClass() {
			return "pvapps::javatestapp::ViewStateSceneObserver";
		}
	}

	class ViewStateSelectionObserver extends SelectionObserver {
		ViewStateSelectionObserver() {
		}

		protected void OnBeginUpdate() {
		}

		protected void OnEndUpdate() {
		}

		protected void OnInsertItems(Instance[] items, long recurseMask) {
		}

		protected void OnRemoveItems(Instance[] items, long recurseMask) {
		}

		protected void OnClearSelection() {
		}

		public String GetObjectClass() {
			return "pvapps::javatestapp::ViewStateSelectionObserver";
		}
	}

	class ViewstateMenuSelListener implements ActionListener {
		ViewstateMenuSelListener() {
		}

		public void actionPerformed(ActionEvent e) {
			try {
				JMenuItem itm = (JMenuItem) e.getSource();

				ViewStateSource selViewState = (ViewStateSource) VaPViewImpl.this.listOfViewStates
						.get(Integer.parseInt(itm.getName()));

				AsyncEventCB viewStateAsyncEv = VaPViewImpl.this.actor
						.getAsyncEvent("setViewState");

				VaPViewImpl.this.world
						.GetFirstShapeScene()
						.GetShapeView(0)
						.SetViewState(selViewState,
								viewStateAsyncEv.GetAsyncEventIf());
			} catch (Throwable localThrowable) {
				log.error(localThrowable.getStackTrace());
			}
		}
	}

	public BoundingMarkup getBbm() {
		return bbm;
	}

	public void setBbm(BoundingMarkup bbm) {
		this.bbm = bbm;
	}
}
