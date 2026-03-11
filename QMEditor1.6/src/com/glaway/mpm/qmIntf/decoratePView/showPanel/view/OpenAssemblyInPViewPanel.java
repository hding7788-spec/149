package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import com.glaway.mpm.visual.bean.VaInputStreamData;
import com.glaway.mpm.visual.control.VaMathUtil;
import com.glaway.mpm.visual.util.VaUtil;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import com.ptc.pview.dg.DPoint3D;
import com.ptc.pview.dg.FMat33;
import com.ptc.pview.dg.Location;
import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.pvapi.ShapeSceneObserver;
import com.ptc.pview.pvapi.TreeObserver;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.ComponentInstance;
import com.ptc.pview.pvkapp.ComponentNode;
import com.ptc.pview.pvkapp.Instance;
import com.ptc.pview.pvkapp.Kernel;
import com.ptc.pview.pvkapp.PVWindow;
import com.ptc.pview.pvkapp.ShapeInstance;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.ShapeView;
import com.ptc.pview.pvkapp.Structure;
import com.ptc.pview.pvloader.ProtocolHandler;
import com.ptc.pview.pvloader.ProtocolHandlerEvents;
import com.ptc.pview.pvloader.RemoteIf;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.ManagedObject;
import com.ptc.pview.utils.dom.Message;
import com.ptc.pview.utils.dom.MessageProtocolException;
import com.ptc.pview.utils.dom.MessageQueue;

public class OpenAssemblyInPViewPanel extends OpenAssemblyInPViewBase {
	private static final long serialVersionUID = 1L;
	private static String PART_ROOT = "C:/demodata";

	HashMap<Long, URL> urlMap = new HashMap<Long, URL>();
	HashMap<Long, Matrix4d> matrix4dMap = new HashMap<Long, Matrix4d>();
	private Map<Long,String> numberMap = new HashMap<Long,String>();

	boolean removedAxle;
	boolean removedWheel1;
	boolean removedWheel2;
	boolean removedAll;

	ArrayList instList;

	public OpenAssemblyInPViewPanel(String title, HashMap<Long, URL> urlMap, Map<Long, List<Long>> map,Map<Long,String> numberMap) {
		super(title);
		this.urlMap = urlMap;
		try {
			this.matrix4dMap = TechnicsIntf.getMatrix4dByPartOid(map);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.numberMap = numberMap;
		System.out.println(">>>>>>>>urlMap:"+this.urlMap);
		System.out.println(">>>>>>>>matrix4dMap:"+this.matrix4dMap);
		System.out.println(">>>>>>>>numberMap:"+this.numberMap);

	}

	MyActor myActor;

	synchronized public boolean runPView() {

		if (pview.IsPviewInstalled() == false)
			return false;

		if (pview.Start("webserver") == false)
			return false;

		try {
			myActor = new MyActor();
			kernel = myActor.getKernel();
			theWindow = kernel.GetWindow();
			pvWindow = new PVWindow(theWindow, panel);
			// Get a handle to the embedded control to initialise the
			// application.
			embeddedControl = kernel.GetEmbeddedControl();
			// Create a single instance of the World
			theWorld = embeddedControl.CreateWorld();

			theWorld.SetParentWindow(pvWindow.GetWindow());
			theWorld.SetControlActor("pview");
			structure = theWorld.CreateStructure();
			if (structure == null) {
				System.out.println("\n- Structure is null.");
				return false;
			}

			tree = theWorld.CreateTree();
			TreeObserver treeObserver = myActor.getTreeObserver();
			tree.RegisterObserver(treeObserver.GetObjectId(), treeObserver.GetOwner());

			embeddedControl.SetAutoLoad("auto");

			embeddedControl.SetBackgroundColor(240, 240);

			myActor.listenForEvents();

			AsyncEventCB initEvent = myActor.getAsyncEvent("Initialise");
			embeddedControl.Initialise(initEvent.GetAsyncEventIf());

			addWindowListener(new WindowAdapter() {
				/**
				 * Catch when the user closes the application so ProductView is
				 * shutdown correctly.
				 */
				public void windowClosing(WindowEvent e) {
					pviewShutdown();
					// System.exit(0);
					OpenAssemblyInPViewPanel.this.dispose();
				}
			});
		} catch (Throwable x) {
			return false;
		}
		return true;
	}

	public boolean runPViewNext() {
		try {
			embeddedControl.AttachUIToStructure();
			PopulateStructure(structure);
			scene = theWorld.GetFirstShapeScene();
			if (scene != null) {
				view = scene.GetShapeView(0);
				AsyncEventCB zoomAllEvent = myActor.getAsyncEvent("- ZoomAll():");
				view.ZoomAll(zoomAllEvent.GetAsyncEventIf());
				so = myActor.getShapeSceneObserver(scene);
				scene.RegisterObserver(so.GetObjectId(), so.GetOwner());
			}
		} catch (Throwable x) {
			return false;
		}
		return true;
	}

	/**
	 * Create a structure with a root "参装件"
	 */
	private boolean PopulateStructure(Structure s) {
		try {
			ComponentNode rootcn = s.CreateComponentNode("参装件", (byte) 'a');
			s.SetRoot(rootcn);
			rootcn = s.GetRoot();
			Iterator<Long> iterator = urlMap.keySet().iterator();
			while(iterator.hasNext()) {
				long ida2a2 = iterator.next();
				ComponentNode componentNode = structure.CreateComponentNode(numberMap.get(ida2a2), (byte) 'a');
				System.out.println("--------url:"+urlMap.get(ida2a2).toExternalForm());
				componentNode.SetShapeSource(urlMap.get(ida2a2).toExternalForm(), 0, 0, 0, 1, 1, 1);
				ComponentInstance componentInstance = rootcn.AddComponentNode(componentNode, numberMap.get(ida2a2));
				Location location = getPviewLocation(matrix4dMap.get(ida2a2));
				componentInstance.SetLocation(location);
			}
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	class MyActor extends ManagedObject {
		public MyActor() {
			super();
			queue = new MessageQueue();
			ManageSelf(queue, "Actor");
		}

		public ProtocolHandler GetProtocolHandler(ProtocolHandlerEvents phe,
				String protocols, RemoteIf rif) {
			ProtocolHandler ph = new ProtocolHandler(phe, protocols, rif);
			ManageObject(ph);
			return ph;
		}

		/**
		 * This class is required for ProductView message processing.
		 */
		class CheckForMessages implements ActionListener {

			public void actionPerformed(ActionEvent event) {

				try {

					if (queue.IsInService()) {
						Message m = queue.PollMessage();
						if (m != null) {
							System.out.println("Message found: " + m.toString());
							queue.ForwardMessageToHandler(m);
						}
					} else {
						queue.ForwardMessageToHandler(null);
					}
				} catch (InvalidActorException e) {
					msgTimer.stop();
				} catch (ConnectionLostException e) {
					msgTimer.stop();
				} catch (MessageProtocolException e) {
					msgTimer.stop();
				} catch (ActorShutdownException e) {
					msgTimer.stop();
				}
			}
		};

		public AsyncEventCB getAsyncEvent(String use) {
			AsyncEventCB as = new BuildAssemblyExampleAsyncEvent(use);
			ManageObject(as.GetAsyncEventIf());
			return as;
		}

		public Kernel getKernel() {
			try {
				return (Kernel) GetDistinguishedObject(Kernel.CLASS_NAME, "pvkernel");
			} catch (Throwable x) {
			}
			return null;
		}

		public TreeObserver getTreeObserver() {
			TreeObserver treeObserver = new BuildAssemblyExampleTreeObserver();
			ManageObject(treeObserver);
			return treeObserver;
		}

		public ShapeSceneObserver getShapeSceneObserver(ShapeScene scene) {
			ShapeSceneObserver sceneObserver = new BuildAssemblyExampleSceneObserver(
					scene);
			ManageObject(sceneObserver);
			return sceneObserver;
		}

		public SelectionObserver getSelectionObserver() {
			SelectionObserver so = new MySelectionObserver();
			ManageObject(so);
			return so;
		}

		public void listenForEvents() {
			System.out.println("listenForEvents() adding timer to check for messages\n");
			CheckForMessages cfm = new CheckForMessages();
			msgTimer = new Timer(10, cfm);
			msgTimer.start();
		}

		public String GetObjectClass() {
			return "pvexamples::pvexamplesutilities::MyActor";
		}

		private MessageQueue queue;
		private Timer msgTimer;
	};

	public class BuildAssemblyExampleAsyncEvent extends AsyncEventCB {
		BuildAssemblyExampleAsyncEvent(String reason) {
			m_description = reason;
		}

		public void OnProgress(long progress) {
		}

		public void OnComplete(long status) {
			try {
				if (m_description == "Initialise") {
					MyProtocolHandlerEvents dphe = new MyProtocolHandlerEvents();
			        RemoteIf theRemoteIf = kernel.GetRemoteInterface();
					myActor.GetProtocolHandler(dphe, "http:https", theRemoteIf);
			        dphe.setRemoteIf(theRemoteIf);
					runPViewNext();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		private String m_description;

	}

	class BuildAssemblyExampleTreeObserver extends TreeObserver {
		protected void OnBeginUpdate() {
			System.out.println("OpenAssemblyInPViewPanelTreeObserver.OnBeginUpdate()");
		}

		protected void OnEndUpdate() {
			System.out.println("OpenAssemblyInPViewPanelTreeObserver.OnEndUpdate()");
		}

		protected void OnInstanceCreate(Instance instance, Instance parent,
				String name) {
			System.out.println("OpenAssemblyInPViewPanelTreeObserver.OnInstanceCreate()" + name);
		}

		protected void OnInstanceRemove(Instance instance, Instance parent) {
			System.out.println("- OpenAssemblyInPViewPanelTreeObserver.OnInstanceRemove() <<<<<<<<<<------------");
			if (removedAll) {
				try {
					System.out.println("- ######## Unload all SI from the scene ########");
					removedAll = false;
					ShapeScene scene = theWorld.GetFirstShapeScene();
					scene.RemoveAllShapeInstances();
				} catch (Throwable x) {
					x.printStackTrace();
				}
			}
		}

		protected void OnInstanceName(Instance instance) {
		}

		protected void OnInstanceLocation(Instance instance) {
		}

		public String GetObjectClass() {
			return "pvapps::javatestapp::BuildAssemblyExampleTreeObserver";
		}
	}

	class BuildAssemblyExampleSceneObserver extends ShapeSceneObserver {
		BuildAssemblyExampleSceneObserver(ShapeScene scene) {
			m_shapeScene = scene;
		}

		protected void OnBeginUpdate() {
			System.out.println("OpenAssemblyInPViewPanelSceneObserver.OnBeginUpdate()");
		}

		protected void OnEndUpdate() {
			System.out.println("OpenAssemblyInPViewPanelSceneObserver.OnEndUpdate()");
		}

		protected void OnShapeInstanceCreate(ShapeInstance shapeInstance) {
			System.out.println("OpenAssemblyInPViewPanelSceneObserver.OnShapeInstanceCreate()<---------------------------------------------");
		}

		protected void OnShapeInstanceRemove(ShapeInstance shapeInstance) {
			System.out.println("OpenAssemblyInPViewPanelSceneObserver.OnShapeInstanceRemove()");
		}

		protected void OnShapeInstanceVisibility(ShapeInstance shapeInstance, long visible) {
		}

		protected void OnShapeInstanceHighlight(ShapeInstance shapeInstance, long highlighted) {
			System.out.println("OpenAssemblyInPViewPanelSceneObserver.OnShapeInstanceHighlight()");
		}

		protected void OnShapeInstanceLocation(ShapeInstance shapeInstance) {
			System.out.println("OpenAssemblyInPViewPanelSceneObserver.OnShapeInstanceLocatio()");
		}

		protected void OnShapeViewCreate(ShapeView shapeView) {
			System.out.println("OpenAssemblyInPViewPanelSceneObserver.OnShapeViewCreate()");
		}

		protected void OnShapeViewRemove1(ShapeView shapeView) {
			System.out.println("OpenAssemblyInPViewPanelSceneObserver.OnShapeViewRemove()");
		}

		public String GetObjectClass() {
			return "pvapps::javatestapp::OpenAssemblyInPViewPanelSceneObserver";
		}

		private ShapeScene m_shapeScene;
	}

	 static class MyProtocolHandlerEvents extends  ProtocolHandlerEvents{
	        RemoteIf remoteIf;
	        synchronized public void
	        DownloadFile(String purl,  String diskfile, String handle) {
	            try {
	            	VaInputStreamData isd = VaUtil.downloadAuthURLData(purl, true);
	                InputStream i = isd.getInputStream();
	                if (i != null) {
	                   File fw = new File(diskfile);
	                   byte buf[] = new byte[32768];
	                   FileOutputStream fos = new FileOutputStream(fw);
	                   int n;
	                   while ((n = i.read(buf)) >= 0) {
	                      fos.write(buf, 0, n);
	                   }
	                   fos.close();
	                }
	                finishedDownload(handle);
	            } catch (java.io.FileNotFoundException e){
	                finishedDownload(handle);
	            }
	            catch (java.lang.Exception e){
	                e.printStackTrace();
	            }
	        }

	        synchronized public void finishedDownload(String handle){
	            try {
	                remoteIf.DownloadFinished(handle);
	            } catch (java.lang.Exception e){
	            }
	        }

	        synchronized public void
	        UploadFile(String purl, String diskfile, String handle){
	        }

	 	   public RemoteIf getRemoteIf() {
	 	      return remoteIf;
	 	   }

	 	   public void setRemoteIf(RemoteIf remoteIf) {
	 	      this.remoteIf = remoteIf;
	 	   }
	    };

	private static Location getPviewLocation(Matrix4d matrix) throws Exception {
		Vector3d v = VaMathUtil.matrice4ToTrans(matrix);
		Matrix3d d;

		// ML start Symmetry Handling
		if (!(matrix.determinant() > 0.0d)) {
			// if the part is right symmetric or the matrix4d determinant is
			// non-positive
			Vector3d angles = new Vector3d();
			angles.x = Math.atan2(matrix.m21, matrix.m22);
			angles.y = -Math.asin(matrix.m20);
			angles.z = Math.atan2(-matrix.m10, matrix.m00);
			d = VaMathUtil.anglesToMatrice(angles);

			Matrix3d PIrotation = new Matrix3d();
			PIrotation.rotY(Math.PI);
			d.mul(PIrotation); // Rotates of PI
			d.mul(-1D); // mirroring
		} else {
			d = VaMathUtil.matrix4ToMatrix3(matrix);
		}

		// ML start fixed pview issues for volvo

		float[] f = new float[9];
		f = VaMathUtil.getOrientationFromMatrix4d(matrix);
		FMat33 theFMat33;
		if (f == null) {
			float m00 = Double.valueOf(d.m00).floatValue();
			float m01 = Double.valueOf(d.m01).floatValue();
			float m02 = Double.valueOf(d.m02).floatValue();
			float m10 = Double.valueOf(d.m10).floatValue();
			float m11 = Double.valueOf(d.m11).floatValue();
			float m12 = Double.valueOf(d.m12).floatValue();
			float m20 = Double.valueOf(d.m20).floatValue();
			float m21 = Double.valueOf(d.m21).floatValue();
			float m22 = Double.valueOf(d.m22).floatValue();
			theFMat33 = new FMat33(m00, m01, m02, m10, m11, m12, m20, m21, m22);
		} else {
			theFMat33 = new FMat33(f[0], f[1], f[2], f[3], f[4], f[5], f[6], f[7], f[8]);
		}

		DPoint3D thePoint3D = new DPoint3D(v.x, v.y, v.z);
		Location theLocation = new Location();
		theLocation.Set(theFMat33, thePoint3D);
		return theLocation;
	}
}
