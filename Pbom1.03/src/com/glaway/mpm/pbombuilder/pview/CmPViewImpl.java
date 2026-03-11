package com.glaway.mpm.pbombuilder.pview;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.plaf.ColorUIResource;
import com.glaway.mpm.pbombuilder.action.CmPVActor;
import com.glaway.mpm.pbombuilder.action.CmPropertiesVisitorImpl;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.panel.CmPViewAScenePanel;
import com.glaway.mpm.pbombuilder.util.CmTaskInfo;
import com.glaway.mpm.pbombuilder.util.PviewTask;
import com.ptc.pview.dg.Color;
import com.ptc.pview.pvapi.OrientationsObserver;
import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.pvapi.ShapeSceneObserver;
import com.ptc.pview.pvapps.PviewInit;
import com.ptc.pview.pvkapp.AnnoSetTable;
import com.ptc.pview.pvkapp.AnnoSetTableEntry;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.AsyncEventIfImpl;
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
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.MessageProtocolException;

public class CmPViewImpl {
   private static final CmLogger  log                   = CmLogger.getLogger(CmPViewImpl.class.getName());

   // pview define
   private final static PviewInit pviewInit             = new PviewInit();
   private final static boolean   pviewInstalled;
   private final static boolean   pviewInitialized;

   public static boolean   isInPViewMode         = false;
   private static String    controlActorName;

   private String                 name;
   private CmPVActor              actor;
   private Kernel                 kernel;
   private EmbeddedControl        embeddedControl;

   private volatile boolean       pviewClientIntialized = false;

   private World                  world;
   private PVWindow               pvWindow;
   private Window                 theWindow;

   private Structure              structure;

   private CmPViewAScenePanel     panelContext;

   private SelectionObserver selectionObserver;
   private CmPropertiesVisitorImpl propertiesVisitor;

// 视图，方位，注释集加载相关

	Orientations					orientation;
	AnnoSetTable					annoSetTable;
	MyOrientationsObserver			orientObs;
	MyInstanceVisitorEvents			ive;
	InstanceVisitor					instVisitor;

	Tree							tree;
	ArrayList<ViewStateSource>		listOfViewStates		= new ArrayList();
	ArrayList<String>				listOfOrientationsName	= new ArrayList();
	ArrayList<OrientationType>		listOfOrientationsType	= new ArrayList();
	ArrayList<AnnoSetTableEntry>	listOfAnnotationsName	= new ArrayList();

   static {
      pviewInstalled = pviewInit.IsPviewInstalled();
      pviewInitialized = pviewInstalled ? pviewInit.Start("webserver") : false;
   }

   public boolean isPviewClientIntialized() {
      return pviewClientIntialized;
   }

   public void waitforClientIntialized() {
      while (!isPviewClientIntialized()) {
         try {
            StackTraceElement ste = new Throwable().getStackTrace()[1];
            log.debug("waitforClientIntialized in ", ste.getClassName(), ".", ste.getMethodName(), "().", ste.getLineNumber());
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

   CmPViewImpl(String name) {
      this.name = name;

      pviewClientIntialized = false;

      panelContext = new CmPViewAScenePanel(name);
   }

   public synchronized void initPView() {
      try {
         if (actor == null) {
            actor = new CmPVActor();
            kernel = actor.getKernel();
            embeddedControl = kernel.GetEmbeddedControl();
         }

         initPviewWorld(panelContext);
      } catch (Exception e) {
         log.error(e);
      }
   }

   private synchronized void initPviewWorld(CmPViewAScenePanel panel) throws ConnectionLostException, ActorShutdownException,
      Exception, InvalidActorException {
      if (theWindow == null)
         theWindow = kernel.GetWindow();
      if (pvWindow == null)
         pvWindow = new PVWindow(theWindow, panel);

      if (world == null) {
		world = embeddedControl.CreateWorld("pvkernel");
         controlActorName = isInPViewMode ? "pview" : "thumbnail";
         world.SetControlActor(controlActorName);
         world.SetParentWindow(pvWindow.GetWindow());
      }

      structure = world.CreateStructure();
      world.CreateTree();

      embeddedControl.SetAutoLoad("auto");
//      embeddedControl.SetBackgroundColor(240, 240);
      embeddedControl.SetBackgroundColor(0xAEAEB2, 0x2C90FF);
      AsyncEventCB asyncEvent = new CmAsyncEvent("Initialise");
      actor.ManageObject(asyncEvent.GetAsyncEventIf());
      embeddedControl.Initialise(asyncEvent.GetAsyncEventIf());
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

   private void cleanupPvWorld() throws ConnectionLostException, ActorShutdownException, Exception, InvalidActorException {
      if (world != null) {
//		world.ClearControlActor(controlActorName);
		world.RemoveContent();
//		world.Destroy();
//        world = null;
      }
   }

   public void resetPvWorld() throws ConnectionLostException, ActorShutdownException, Exception, InvalidActorException {
      pviewClientIntialized = false;
      log.debug("正在清理缓存");
      cleanupPvWorld();
      Thread.sleep(1000);
      log.debug("缓存清理完毕，初始化Pview");
      initPviewWorld(panelContext);
   }

   public Structure getStructure() {
      return structure;
   }

   public CmPViewAScenePanel getPanelContext() {
      return panelContext;
   }

   public void setPanelContext(CmPViewAScenePanel panelContext) {
      this.panelContext = panelContext;
   }

   public void initObsContext(SelectionObserver so) {
      try {
         SelectionController sc = panelContext.getShapeScene().GetSelectionController();
         so = actor.getSelectionObserver(so);
         sc.RegisterObserver(so.GetObjectId(), so.GetOwner());

      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public AsyncEventCB getAsyncEvent(String reason) {
      return actor.getAsyncEvent(reason);
   }

   public class CmAsyncEvent extends AsyncEventCB {
      CmAsyncEvent(String reason) {
         m_description = reason;
      }

      public void OnProgress(long progress) {}

      public void OnComplete(long status) {
         try {
            if (m_description == "Initialise") {
               if (status == 0) {
            	   if(pviewClientIntialized)
            		   return;
                  if (panelContext.getShapeView() != null)
                     panelContext.getShapeView().Destroy();
                  if (panelContext.getShapeScene() != null)
                     panelContext.getShapeScene().Destroy();

                  if (isInPViewMode) {
                     theWindow = kernel.GetWindow();
                     pvWindow = new PVWindow(theWindow, panelContext);
                     embeddedControl.AttachUIToStructure();
                     isInPViewMode=false;
                  }

                  CmHttpDownloadProtocolHandlerEvents dphe = new CmHttpDownloadProtocolHandlerEvents();
                  RemoteIf theRemoteIf = kernel.GetRemoteInterface();
                  actor.GetProtocolHandler(dphe, "http:https", theRemoteIf);
                  dphe.setRemoteIf(theRemoteIf);

                  ShapeScene shapeScene = world.CreateShapeScene();
                  ShapeView shapeView = shapeScene.CreateShapeView(theWindow);

                 float[] colorCompsBlue = new ColorUIResource(0xAE, 0xAE, 0xB2).getRGBComponents(null);
                  float[] colorCompsTurquoise = new ColorUIResource(0x2C, 0x90, 0xFF).getRGBComponents(null);
                  shapeView.SetGradientBackgroundColor(//
                     new Color(colorCompsTurquoise[0], colorCompsTurquoise[1], colorCompsTurquoise[2], colorCompsTurquoise[3]), //
                     new Color(colorCompsBlue[0], colorCompsBlue[1], colorCompsBlue[2], colorCompsBlue[3])//
                     );


                  panelContext.setShapeScene(shapeScene);
                  panelContext.setShapeView(shapeView);

                  propertiesVisitor = actor.getPropertiesVisitor();

                  pviewClientIntialized = true;
               } else {
                  System.exit(0);
               }
            }else if (m_description == "fileopened") {
            	fileOpened();
              }
            else if ((this.m_description != null)
					&& (this.m_description.endsWith(".finishAnimFrame"))) {
				System.out.println(this.m_description);
				CmPViewImpl.this.fileOpened();
				try {
					Thread.sleep(0L);
					PviewTask.sendTask(CmTaskInfo.newCmTaskInfo(
							this.m_description, this, null), null);
				} catch (CmTaskException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}}
            else if (m_description == "ZoomAll") {
               if (status == 0) {
                  System.out.println("zoom all complete");
               } else {
                  System.out.println("zoom all failed");
               }
            }
         } catch (java.lang.Exception ex) {
            ex.printStackTrace();
         }
      }

      private String m_description;
   }

   public static PviewInit getPviewInit() {
      return pviewInit;
   }

   public World getWorld(){
	   return this.world;
   }

   /**
	 * 打开pvs
	 */
	public void openFile(String filepath,String finishAnimFrameTaskId) {
		log.debug("openFile : " + filepath);
		try {
//			if(filepath.endsWith(".pvs"))
//			embeddedControl.URLOpen(filepath, "", "", getAsyncEventIf(finishAnimFrameTaskId));
//			else{

				embeddedControl.URLOpen(filepath, "", "", this.getAsyncEventIf(finishAnimFrameTaskId));
//			}
//			embeddedControl.URLOpen(filepath, "", "", null);//getAsyncEventIf("fileopened"));
		} catch (Throwable x) {
			x.printStackTrace();
		}


	}

	void fileOpened(){
		try {
			log.debug("fileOpened");

			orientation = this.world.GetOrientations();

			annoSetTable = this.world.GetAnnoSetTable();

			orientObs = new MyOrientationsObserver();
			this.actor.ManageObject(orientObs);
			orientation.RegisterObserver(orientObs.GetObjectId(), orientObs.GetOwner());

			ive = new MyInstanceVisitorEvents();
			instVisitor = this.actor.getInstanceVisitor(ive);

			for (int i = 0; i < annoSetTable.GetNumEntries(); i++) {
				log.debug("annotations : " + annoSetTable.GetAnnoSetTableEntry(i).GetName());
				this.listOfAnnotationsName.add(annoSetTable.GetAnnoSetTableEntry(i));
			}

			Instance root = embeddedControl.GetWorld().GetTree().GetRoot();

			if (!root.GetName().endsWith(".ol")) {
				root.Visit(instVisitor, 0);
			}
			embeddedControl.AttachUIToStructure();

			////////////////
			ShapeScene ss = embeddedControl.GetWorld().GetFirstShapeScene();
			while(ss == null){
				log.debug("waiting for ol assembling!");
				Thread.sleep(100);
				ss = embeddedControl.GetWorld().GetFirstShapeScene();
			}
			panelContext.setShapeScene(ss);
			panelContext.setShapeView(ss.GetShapeView(0));
			SelectionController sc = ss
					.GetSelectionController();
			SelectionObserver so = this.getSelectionObserver();
			so = this.actor.getSelectionObserver(so);
			sc.RegisterObserver(so.GetObjectId(), so.GetOwner());

			//panelContext.getShapeView().ZoomAll(getAsyncEvent("CmPivewAction.finishAnimFrame").GetAsyncEventIf());
		} catch (MessageProtocolException e) {
			e.printStackTrace();
		} catch (ActorShutdownException e) {
			e.printStackTrace();
		} catch (InvalidActorException e) {
			e.printStackTrace();
		} catch (ConnectionLostException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public AsyncEventIfImpl getAsyncEventIf(String reason) {
		AsyncEventCB asyncEvent = new CmAsyncEvent(reason);
		AsyncEventIfImpl impl = asyncEvent.GetAsyncEventIf();
		actor.ManageObject(impl);
		return impl;
	}

	public SelectionObserver getSelectionObserver() {
		return selectionObserver;
	}

	public void setSelectionObserver(SelectionObserver selectionObserver) {
		this.selectionObserver = selectionObserver;
	}
	public String savePVS(String pvsPath) throws Exception {
		Structure s = world.GetStructure();
		String retPath = null;
		if(s == null || s.IsEmpty()) {
			JOptionPane.showMessageDialog(null,"未加载图形，不能保存pvs");
			return null;
		} else {
			retPath = world.SaveStructure(pvsPath,true,getAsyncEventIf("savePVS"));
		}
		return retPath;
	}

	public CmPropertiesVisitorImpl getPropertiesVisitor(Collection<String[]> properties) {
		propertiesVisitor.setProperties(properties);
		return propertiesVisitor;
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
				viewStatelistMenu.getPopupMenu().setLightWeightPopupEnabled(false);

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
				orientationslistMenu.getPopupMenu().setLightWeightPopupEnabled(false);
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
				annotationslistMenu.getPopupMenu().setLightWeightPopupEnabled(false);
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

			AnnoSetTableEntry selAnnotationName = (AnnoSetTableEntry) CmPViewImpl.this.listOfAnnotationsName
					.get(Integer.parseInt(itm.getName()));
			log.debug("Setting annotation : " + selAnnotationName);
			try {
				CmPViewImpl.this.world.GetFirstShapeScene().ApplyAnnotations(selAnnotationName,
						CmPViewImpl.this.world.GetTree(), null);
			} catch (Throwable x) {
				x.printStackTrace();
				CmPViewImpl.this.log.debug(x);
			}
			log.debug("setAnno");
		}
	}

	class MyInstanceVisitorEvents extends InstanceVisitorEvents {
		ArrayList<Instance>	list	= new ArrayList();

		public MyInstanceVisitorEvents() {
		}

		public boolean Visit(Instance inst, Instance parent, int depth) {
			try {
				MyViewStateVisitorEvents vsve = new MyViewStateVisitorEvents();
				ViewStateVisitor viewStateVisitor = CmPViewImpl.this.actor.getViewStateVisitor(vsve);

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
			log.debug("Orientation name: " + name + ", Orientation type: " + type);
			CmPViewImpl.this.listOfOrientationsName.add(name);
			CmPViewImpl.this.listOfOrientationsType.add(type);
		}

		protected void OnOrientationDeleted(String name, OrientationType type) {
		}

		protected void OnOrientationUpdated(String name, OrientationType type) {
		}

		protected void OnDefaultOrientChanged(String name, OrientationType type) {
			log.debug("MyOrientationObserver.OnDefaultOrientChanged: " + name + "  --  " + type);
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
				CmPViewImpl.this.listOfViewStates.add(viewStateSource);
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

			String selOrientationName = (String) CmPViewImpl.this.listOfOrientationsName.get(Integer.parseInt(itm
					.getName()));
			OrientationType selOrientationType = (OrientationType) CmPViewImpl.this.listOfOrientationsType.get(Integer
					.parseInt(itm.getName()));
			log.debug("Setting orientation: " + selOrientationName + ", with type: " + selOrientationType);
			try {
				Orientations orientation = CmPViewImpl.this.world.GetOrientations();
				ShapeView shView = CmPViewImpl.this.world.GetFirstShapeScene().GetShapeView(0);
				orientation.SetOrientation(shView, selOrientationName, selOrientationType);
			} catch (Throwable localThrowable) {
			}
		}
	}

	class ViewStateSceneObserver extends ShapeSceneObserver {
		private ShapeScene	m_shapeScene;

		ViewStateSceneObserver(ShapeScene shapeScene) {
			this.m_shapeScene = shapeScene;
		}

		protected void OnBeginUpdate() {
		}

		protected void OnEndUpdate() {
		}

		protected void OnShapeInstanceVisibility(ShapeInstance shapeInstance, long visible) {
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

				ViewStateSource selViewState = (ViewStateSource) CmPViewImpl.this.listOfViewStates.get(Integer
						.parseInt(itm.getName()));

				AsyncEventCB viewStateAsyncEv = CmPViewImpl.this.actor.getAsyncEvent("setViewState");

				CmPViewImpl.this.world.GetFirstShapeScene().GetShapeView(0)
						.SetViewState(selViewState, viewStateAsyncEv.GetAsyncEventIf());
			} catch (Throwable localThrowable) {
				log.error(localThrowable.getStackTrace());
			}
		}
	}

}
