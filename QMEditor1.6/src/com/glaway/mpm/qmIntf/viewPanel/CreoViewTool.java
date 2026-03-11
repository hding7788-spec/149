package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import wt.wrmf.transport.embtransport.embtransportResource;

import com.glaway.mpm.util.SwingUtil;
import com.ptc.pview.annotations.ArrowType;
import com.ptc.pview.annotations.LeaderLine;
import com.ptc.pview.dg.Color;
import com.ptc.pview.dg.DPoint3D;
import com.ptc.pview.pvapi.SelectionObserver;
import com.ptc.pview.pvapps.PviewInit;
import com.ptc.pview.pvkapp.AnnoSetTable;
import com.ptc.pview.pvkapp.AnnoSetTableEntry;
import com.ptc.pview.pvkapp.AnnotationSaveEvent;
import com.ptc.pview.pvkapp.AsyncEvent;
import com.ptc.pview.pvkapp.AsyncEventCB;
import com.ptc.pview.pvkapp.AsyncEventIfImpl;
import com.ptc.pview.pvkapp.EmbeddedControl;
import com.ptc.pview.pvkapp.Instance;
import com.ptc.pview.pvkapp.InstanceVisitor;
import com.ptc.pview.pvkapp.InstanceVisitorEvents;
import com.ptc.pview.pvkapp.InstanceVisitorImpl;
import com.ptc.pview.pvkapp.Kernel;
import com.ptc.pview.pvkapp.PVWindow;
import com.ptc.pview.pvkapp.SelectionController;
import com.ptc.pview.pvkapp.ShapeScene;
import com.ptc.pview.pvkapp.Structure;
import com.ptc.pview.pvkapp.ViewStateVisitor;
import com.ptc.pview.pvkapp.ViewStateVisitorEvents;
import com.ptc.pview.pvkapp.ViewStateVisitorImpl;
import com.ptc.pview.pvkapp.Window;
import com.ptc.pview.pvkapp.World;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.Exception;
import com.ptc.pview.utils.dom.InvalidActorException;
import com.ptc.pview.utils.dom.ManagedObject;
import com.ptc.pview.utils.dom.Message;
import com.ptc.pview.utils.dom.MessageProtocolException;
import com.ptc.pview.utils.dom.MessageQueue;

/**
 * This example demonstrates the various types of orientations and viewstates
 * that can be applied on model.
 */

public class CreoViewTool extends Panel {

	private World theWorld;
	private PviewInit pview;
	private EmbeddedControl embeddedControl;

	private Panel panel;
	private PVWindow pvWindow;
	private Window theWindow;
	private String filepath;
	private JLabel lbl;

	private String annoName;
	
	private ShapeScene shapeScene;
	private SelectionController selectionController;
	private SelectionObserver so;
	private String annoSet;
	private AnnoSetTable table;
	private AnnoSetTableEntry entry;
	private AnnotationSaveEvent saveEvent;
	private int id = 1;

	@Override
	public void addNotify() {
		// TODO Auto-generated method stub
		super.addNotify();
		// this.add(panel);
		System.out.println("add Notify");
		// this.runPView("D:\\shared\\PV-Process\\blower_complete_k01.prt");
	}

	public CreoViewTool() {
		init();
	}

	public void showImage(String fileName) {
		System.out.println("show Image::" + fileName);

		if (fileName == null) {
			lbl.setText("请添加creo图形");
			lbl.setIcon(null);
		} else {
			Icon icon = new ImageIcon(fileName);
			lbl.setIcon(icon);
			lbl.setText("");
		}
		lbl.setVisible(true);
		panel.setVisible(false);
	}

	public void init() {
		panel = new Panel();
		lbl = new JLabel();
		setLayout(new GridBagLayout());

		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		add(panel, gridBagConstraints);

		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		gridBagConstraints_1.anchor = GridBagConstraints.SOUTHEAST;
		gridBagConstraints_1.weighty = 1.0;
		gridBagConstraints_1.weightx = 1.0;
		gridBagConstraints_1.gridx = 0;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.insets = new Insets(0, 0, 0, 0);
		add(lbl, gridBagConstraints_1);

		// lbl.setVisible(false);
		// panel.setVisible(true);
		// app.setSize(800,600);
		// validate();
	}

	PViewActor pviewActor;

	public boolean runPView(String path) {

		if (path.indexOf("注释集:") > -1) {

			this.annoName = path.split("注释集:")[1];
			path = path.split("注释集:")[0];
		} else {
			this.annoName = "";
		}

		this.panel.setVisible(true);
		this.lbl.setVisible(false);
		if (pview == null) {

			pview = new PviewInit();
			if (pview.IsPviewInstalled() == false)
				return false;

			if (pview.Start("webserver") == false)
				return false;

			try {

				pviewActor = new PViewActor();
				Kernel kernel = pviewActor.getKernel();

				theWindow = kernel.GetWindow();
				pvWindow = new PVWindow(theWindow, panel);
				embeddedControl = kernel.GetEmbeddedControl();
				theWorld = embeddedControl.CreateWorld();
				theWorld.SetParentWindow(pvWindow.GetWindow());
				embeddedControl.SetAutoLoad("auto");
				
				
				
//				Tree tree = theWorld.GetTree();
//				Instance ins = tree.GetRoot();
//				ins.GetChildWithID(ins.get)
				
				
				embeddedControl.SetBackgroundColor(0xA0A0A0, 0xCCCCCC);

				theWorld.SetControlActor("thumbnail");
//				theWorld.SetControlActor("pview");
				
				pviewActor.listenForEvents();

				
				
				// pviewActor.ManageObject(arg0)
			} catch (Throwable x) {
				x.printStackTrace();
				return false;
			}
		}

		this.filepath = path;
		// this.add(panel);
		changeModel(path);
		return true;
	}

	public void changeModel(String path) {
		try {
			this.filepath = path;
			AsyncEventCB initEvent = pviewActor.getAsyncEvent("Initialise");
			embeddedControl.Initialise(initEvent.GetAsyncEventIf());
			embeddedControl.AttachUIToStructure();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void pviewInitialised() {
		try {
			AsyncEventCB urlOpenEvent = pviewActor.getAsyncEvent("URLOpen");
			
			embeddedControl.URLOpen(// "D:\\shared\\PV-Process\\blower_complete_k01.prt"
					// PART_ROOT + "/Blower/blower.pvs"
					filepath, "", "", urlOpenEvent.GetAsyncEventIf());
		
			
//			SelectionObserver so = pviewActor.getSelectionObserver();
//			shapeScene = theWorld.GetFirstShapeScene();
//            selectionController = shapeScene.GetSelectionController();
//            selectionController.RegisterObserver(so.GetObjectId(), so.GetOwner());
			
		} catch (Throwable x) {
			x.printStackTrace();
		}
	}

	public void saveaspvs(){
		try {
			theWorld.SaveStructure("f:\\111.pvs", true, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	void fileOpened() {
		try {
			// stopPView();
			// populateList ();

			shapeScene = theWorld.GetFirstShapeScene();
			table = theWorld.GetAnnoSetTable();
			selectionController = shapeScene.GetSelectionController();
			
			
//			Structure s = theWorld.GetStructure();
//			String str = s.GetAnnoSetTable().GetAnnoSetTableEntry(0).GetName();
//			System.out.println("str==" + str);
			
			int num = table.GetNumEntries();
//			for (int i = 0; i < num; i++) {
//				System.out.println("Anno Name: " + table.GetAnnoSetTableEntry(i).GetName());
//			}

			if(num > 0){
				entry = table.GetAnnoSetTableEntry(0);
				AsyncEventCB saveAnno = pviewActor.getAsyncEvent("SaveAnnotation");
				shapeScene.ApplyAnnotations(entry, theWorld.GetTree(), saveAnno.GetAsyncEventIf());
				annoSet = entry.GetName();
			} else {
				AsyncEventIfImpl createAnno = pviewActor.getAsyncEvent("SaveAnnotation").GetAsyncEventIf();
				shapeScene.CreateAnnotation(createAnno, shapeScene.GetShapeView(0), "mySet", null, null, null, null);
			}
			
			
//			AsyncEventIfImpl creatAnno = pviewActor.getAsyncEvent("createAnno").GetAsyncEventIf();
//            embeddedControl.CreateAnnoSetTableEntry("mySet", "a", "b", "c", "d", creatAnno);
			
            
//			saveEvent = pviewActor.getAnnotationSaveEvent();
//			theWorld.SetAnnotationSaveEvent(saveEvent.GetAnnotationSaveEventIf());

			
			
		} catch (Throwable x) {
			x.printStackTrace();
		}
	}

	void stopPView() {
		pview.Stop();
	}

	class PViewActor extends ManagedObject {

		public PViewActor() {
			super();
			msgQueue = new MessageQueue();
			ManageSelf(msgQueue, "Actor");
		}

		public ViewStateVisitor getViewStateVisitor(ViewStateVisitorEvents vsve) {
			ViewStateVisitorImpl vsvimpl = new ViewStateVisitorImpl();
			vsvimpl.SetEventHandler(vsve);
			ManageObject(vsvimpl);
			return vsvimpl;
		}

		public InstanceVisitor getInstanceVisitor(InstanceVisitorEvents ive) {
			InstanceVisitorImpl iv = new InstanceVisitorImpl();
			iv.SetEventHandler(ive);
			ManageObject(iv);
			return iv;
		}

		class CheckForMessages implements ActionListener {
			public void actionPerformed(ActionEvent event) {
				try {
					if (msgQueue.IsInService()) {
						Message message = msgQueue.PollMessage();
						if (message != null)
							msgQueue.ForwardMessageToHandler(message);
					} else
						msgQueue.ForwardMessageToHandler(null);
				} catch (Exception e) {
					e.printStackTrace();
					msgTimer.stop();
				}
			}
		};

		public Kernel getKernel() {
			try {
				return (Kernel) GetDistinguishedObject(Kernel.CLASS_NAME, "pvkernel");
			} catch (Throwable x) {
				x.printStackTrace();
			}
			return null;
		}

		public AsyncEventCB getAsyncEvent(String use) {
			AsyncEventCB as = new ViewAsyncEvent(use);
			ManageObject(as.GetAsyncEventIf());
			return as;
		}
		
	    public AnnotationSaveEvent getAnnotationSaveEvent(){
	        AnnotationSaveEvent ev = new  MyAnnotationSaveEvent();
	        ManageObject(ev.GetAnnotationSaveEventIf());
	        return ev;
	    }

		public void listenForEvents() {
			CheckForMessages cfm = new CheckForMessages();
			msgTimer = new Timer(10, cfm);
			msgTimer.start();
		}
		
        public SelectionObserver getSelectionObserver() {
            SelectionObserver so = new MySelectionObserver();
            ManageObject(so);
            return so;
        }
        
		public String GetObjectClass() {

			return "PViewActor";
		}

		private MessageQueue msgQueue;
		private Timer msgTimer;
	};

	class ViewAsyncEvent extends AsyncEventCB {
		ViewAsyncEvent(String reason) {
			m_description = reason;
		}

		public void OnProgress(long progress) {
		}

		public void OnComplete(long status) {
			if (m_description == "Initialise") {
				pviewInitialised();
			} else if (m_description == "URLOpen") {
				fileOpened();
			} else if (m_description == "createAnno") {
//				try {
//					System.out.println("===save===");
//					//AsyncEventIfImpl saveAnno = pviewActor.getAsyncEvent("saveAnno").GetAsyncEventIf();
//					//shapeScene.SaveAnnotation(saveAnno, shapeScene.GetShapeView(0), null, null, null, null);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			}
		}

		private String m_description;
	}
	
	class MySelectionObserver extends SelectionObserver {
		protected void OnBeginUpdate() {}

		protected void OnEndUpdate() {}

		protected void OnInsertItems(Instance[] items, long recurseMask) {
			try {
				System.out.println("len:" + items.length + "\t\t recurseMask:" + recurseMask);
				for (int i = 0; i < items.length; i++) {
					Instance inst = items[i];
					
					String name = inst.GetComponentInstance().GetName();
					String name2 = inst.GetComponentInstance().GetID();
					System.out.println("===================" + name);
					System.out.println("===================" + name2);
					String s = inst.GetProperty(null, "PART_NUMBER");
					System.out.println(s);
					
					
					
					System.out.println("objectClass:" + inst.GetObjectClass() + "\t\tname:" + inst.GetName());
					DPoint3D dPoint3D = selectionController.GetPickPoint(inst);
					com.ptc.pview.dg.DPoint3D[] points = new com.ptc.pview.dg.DPoint3D[2];
					points[0] = new com.ptc.pview.dg.DPoint3D(dPoint3D.Get(0) + 0.05, dPoint3D.Get(1), dPoint3D.Get(2) + 0.2);
					points[1] = dPoint3D;
					LeaderLine leaderLine = (LeaderLine) shapeScene.CreateAnnotation("LeaderLine");

					if (leaderLine != null) {
						shapeScene.AddAnnotation(leaderLine);
						leaderLine.SetAnnotationId(id++);
                        leaderLine.SetLineWidth(1);
                        leaderLine.SetLineColor(new Color(255 ,0 ,0 ,255));
                        leaderLine.SetPoints(points);
                        leaderLine.SetArrowType(ArrowType.HEAD_NONE_TAIL_ARROW);
						
                      
//                    	AsyncEventIfImpl saveAnno = pviewActor.getAsyncEvent("saveAnno").GetAsyncEventIf();
//    					table.SaveAnnoSetTableEntry(annoSet, saveAnno);
    					
//    					app.shapeScene.SaveAnnotation(saveAnno, app.shapeScene.GetShapeView(0), null, null, null, null);
    					
    					
						embeddedControl.AddAnnotationLabel(inst.GetName(), 10, 0x0, 0xffffff);
						
						//embeddedControl.AddAnnotation("leaderline", "");
						
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		protected void OnRemoveItems(Instance[] items, long recurseMask) {}

		protected void OnClearSelection() {}

		public String GetObjectClass() {
			return "pvapps::javatestapp::ConfigExampleSelectionObserver";
		}
    };
	
    
	class MyAnnotationSaveEvent extends com.ptc.pview.pvkapp.AnnotationSaveEvent {

		@Override
		public void OnSaveAnnotation(AnnoSetTableEntry annoSetTableEntry, AsyncEvent createEvent) {
			System.out.println("Java In onSaveAnnotation");
		}
		
		@Override
		public void OnCreateAnnotation(AnnoSetTableEntry annoSetTableEntry, AsyncEvent createEvent) {
			try {
				System.out.println("Java In onCreateAnnotation " + annoSetTableEntry.GetName() + " disk " + annoSetTableEntry.GetAstDiskFileName()
						+ " thumb " + annoSetTableEntry.GetThumbnailDiskFileName());
				createEvent.Complete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void OnDeleteAnnotation(AnnoSetTableEntry annoSetTableEntry, AsyncEvent deleteEvent) {
			System.out.println("Java In onDeleteAnnotation");
		}
		
		
	}
	
	
	public static void main(String[] args) {
		final CreoViewTool app = new CreoViewTool();

		// app.panel = new Panel();
		//
		// app.setLayout(new BorderLayout());
		// app.add(app.panel,BorderLayout.CENTER);

		// app.setSize(800, 600);
		// app.validate();
		// app.runPView("D:\\shared\\PV-Process\\blower_complete_k01.prt");
		JButton button = new JButton("show");
		final JFrame frame = new JFrame();
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// JPanel pan = new JPanel();
				// frame.add(pan, BorderLayout.CENTER);
				// pan.add(app);
				
				app.runPView("D:/resource/creo_view_api/demodata/Crank/01-2_crankshaft_asm.pvs");
//				app.runPView("D:/resource/creo_view_api/demodata/Blower/blower_all_k01_asm_2.ol");
			}
		});
		
		JButton button2 = new JButton("regist");
		JButton button3 = new JButton("unRegist");
		JButton button4 = new JButton("save");
		
		button2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(app.pviewActor != null){
						if(app.so == null){
							app.so = app.pviewActor.getSelectionObserver();
						}
						
						app.selectionController.RegisterObserver(app.so.GetObjectId(), app.so.GetOwner());
					}
				}catch(Exception e1){
					e1.printStackTrace();
				}
				
			}
		});
		
		button3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if(app.pviewActor != null && app.so != null){
						app.selectionController.UnregisterObserver(app.so.GetObjectId(), app.so.GetOwner());
						app.so = null;
						
					}
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		button4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
//					int num = app.table.GetNumEntries();
//					String setName = "mySet";
//					boolean isExist = false;
//					for (int i = 0; i < num; i++) {
//						String name = app.table.GetAnnoSetTableEntry(i).GetName();
//						if(name.equals(setName)){
//							isExist = true;
//							break;
//						}
//					}
//					
//					if(isExist){
//						System.out.println("===save===");
//						AsyncEventIfImpl saveAnno = app.pviewActor.getAsyncEvent("saveAnno").GetAsyncEventIf();
//						app.shapeScene.SaveAnnotation(saveAnno, app.shapeScene.GetShapeView(0), null, null, null, null);
//					} else {
//						System.out.println("===create===");
//						AsyncEventIfImpl createAnno = app.pviewActor.getAsyncEvent("createAnno").GetAsyncEventIf();
//						app.shapeScene.CreateAnnotation(createAnno, app.shapeScene.GetShapeView(0), setName, null, null, null, null);
//					}
//					app.saveaspvs();
					
					AsyncEventIfImpl saveAnno = app.pviewActor.getAsyncEvent("saveAnno").GetAsyncEventIf();
					app.table.SaveAnnoSetTableEntry(app.annoSet, saveAnno);
					app.shapeScene.SaveAnnotation(saveAnno, app.shapeScene.GetShapeView(0), null, null, null, null);
					
//					app.theWorld.SaveStructure("d:\\111.pvs", true, saveAnno);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		frame.setLayout(new BorderLayout());
		frame.add(app, BorderLayout.CENTER);

		JPanel west = new JPanel();
		west.setLayout(new GridLayout(4, 1));

		frame.setSize(800, 600);
		frame.add(west, BorderLayout.WEST);
		
		west.add(button);
		west.add(button2);
		west.add(button3);
		west.add(button4);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setVisible(true);
		// app.runPView("D:\\shared\\PV-Process\\blower_complete_k01.prt");
		// thread.start();
		// app.runPView("D:\\shared\\PV-Process\\blower_complete_k01.prt");
		// app.changeModel("");
	}

}
