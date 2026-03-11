package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.BorderLayout;
import java.awt.Canvas;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.ole.win32.GUID;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Cortona3DPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public static final String CORTONA_PROG_ID = "Cortona.Control.1";
	public static boolean isCortonaInstalled;
	static {
		GUID guid = new GUID();
		char[] buffer = null;
		int count = CORTONA_PROG_ID.length();
		buffer = new char[count + 1];
		CORTONA_PROG_ID.getChars(0, count, buffer, 0);

		if(COM.CLSIDFromProgID(buffer,guid) == COM.S_OK || COM.CLSIDFromString(buffer,guid) == COM.S_OK) {
			isCortonaInstalled = true;
		}
	}
	
	private ShellThread swtThread;
	private String wrlPath;
	private OleFrame frame;
	private Canvas canvas;
	private boolean initView = false;
	private String actionID;
	private Display display;
	private JLabel labelNoFile;

	public Cortona3DPanel() {
		// TODO Auto-generated constructor stub
		
		setLayout(new BorderLayout());
//		this.add(canvas, BorderLayout.CENTER);
		if(isCortonaInstalled)
			labelNoFile = new JLabel("图像显示区域");
		else
			labelNoFile = new JLabel("未检测到安装的Cortona3D软件。");
		this.add(this.labelNoFile);
//		initC3d = true;
	}

	public void connect(String wrl) {
		if(!isCortonaInstalled)
			return;
		
		this.wrlPath = wrl;
		if (this.swtThread == null) {

			this.swtThread = new ShellThread();
			this.swtThread.start();
		}

		// Wait for the Browser instance to become ready
		synchronized (this.swtThread) {
			while (this.frame == null) {
				try {
					this.swtThread.wait(100);
				} catch (InterruptedException e) {
					this.frame = null;
					this.swtThread = null;
					break;
				}
			}
		}
	}

	/**
	 * Returns the Browser instance. Will return "null" before "connect()" or
	 * after "disconnect()" has been called.
	 */
	// public Browser getBrowser() {
	// return this.swtBrowser;
	// }

	/**
	 * Stops the swt background thread.
	 */
	public void disconnect() {
		if(!isCortonaInstalled)
			return;
		
		if (swtThread != null) {
			frame = null;
			swtThread.interrupt();
			swtThread = null;
		}
	}

	/**
	 * Ensures that the SWT background thread is stopped if this canvas is
	 * removed from it's parent component (e.g. because the frame has been
	 * disposed).
	 */
	@Override
	public void removeNotify() {
		super.removeNotify();
		disconnect();
	}

	public void click(String id) {
		if(!isCortonaInstalled)
			return;
		
		if (!this.isWrlPath()) {
			return;
		}
		if(id == null || id.trim() == ""){
			return;
		}
		final String aid = id;
		// shellThread.click(id);
		System.out.println("Cortona:ActionID=======" + aid);
		actionID = aid;
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				swtThread.click(actionID);

			}
		});
	}

	public void play() {
		if(!isCortonaInstalled)
			return;
		
		if (!this.isWrlPath()) {
			return;
		}
		if(this.wrlPath == null || this.wrlPath.trim() == "")
		{
			return;
		}
		System.out.println("Cortona:play");

		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				swtThread.play();

			}
		});
	}
	public void setView(String path) {
		if(!isCortonaInstalled)
			return;
		System.out.println("setView;;path = " + path);
		if (path == null || path.trim() == "") {
			this.removeAll();
			this.add(this.labelNoFile);
			return;
		}
		if (path.equals(this.wrlPath)) {
			return;
		} else if (!this.initView) {
			System.out.println("wrlpath:====" + path);
			remove(labelNoFile);
			canvas = new Canvas();
			canvas.setSize(this.getWidth(), this.getHeight());
			this.add(canvas, BorderLayout.CENTER);
			this.connect(path);
			this.initView = true;
		} else {
			this.wrlPath = path;
			final String tempPath = path;
			
//			remove(labelNoFile);
//			this.add(canvas, BorderLayout.CENTER);
//			initC3d = false;
			
			display.asyncExec(new Runnable() {

				@Override
				public void run() {
					swtThread.setView(tempPath);
					System.out.println("thread run setView ]]]]]]]]]]]]]]]]]");

				}
			});

		}
	}
	
	private boolean isWrlPath() {
		String tempWrlPath = this.wrlPath;
		if (tempWrlPath != null && tempWrlPath.endsWith(".wrl")) {
			if (!".wrl".equals(tempWrlPath)) {
				return true;
			}
		}
		return false;
	}
	
	private class ShellThread extends Thread{
		private OleAutomation auto = null;
		private OleClientSite clientSite;

		@Override
		public void run() {

			try {
				display = new Display();
				Shell shell = SWT_AWT.new_Shell(display, canvas);
				shell.setLayout(new FillLayout());

				synchronized (this) {
					// swtBrowser = new Browser(shell, SWT.NONE);

					frame = new OleFrame(shell, SWT.NONE);

					frame.setSize(canvas.getWidth(), canvas.getHeight());
					clientSite = new OleClientSite(frame, SWT.NONE, CORTONA_PROG_ID);
					auto = new OleAutomation(clientSite);

					int[] rgdispid = auto.getIDsOfNames(new String[] { "Scene" });
					int dispIdMember = rgdispid[0];
					auto.setProperty(dispIdMember, new Variant(wrlPath));

					clientSite.doVerb(OLE.OLEIVERB_SHOW);
					shell.setSize(canvas.getWidth(), canvas.getHeight());

					this.notifyAll();
				}

				shell.open();
				while (!isInterrupted() && !shell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
				shell.dispose();
				display.dispose();
			} catch (Exception e) {
				interrupt();
			}
		}

		public void setView(String filePath) {
			int[] rgdispid = auto.getIDsOfNames(new String[] { "Scene" });
			int dispIdMember = rgdispid[0];
			auto.setProperty(dispIdMember, new Variant(filePath));
		}

		public void play() {
			if (auto == null)
				return;
			int[] rgdispid = auto.getIDsOfNames(new String[] { "Engine" });
			int dispIdMember = rgdispid[0];
			Variant result = auto.getProperty(dispIdMember);
			// System.out.println("Result: " + result.getDispatch());

			OleAutomation engineAuto = result.getAutomation();
			Variant nodesResult = engineAuto.getProperty(engineAuto.getIDsOfNames(new String[] { "Nodes" })[0]);
			// System.out.println("nodesResult: " +
			// nodesResult.getDispatch());

			OleAutomation nodesAuto = nodesResult.getAutomation();
			Variant itemResult = nodesAuto.getProperty(nodesAuto.getIDsOfNames(new String[] { "Item" })[0],
					new Variant[] { new Variant("VCR_CONTROL_SCRIPT") });
			// System.out.println("itemResult: " + itemResult);

			// if(itemResult == null)
			// {
			// return;
			// }
			OleAutomation itemAuto = itemResult.getAutomation();
			Variant fieldsResult = itemAuto.getProperty(itemAuto.getIDsOfNames(new String[] { "Fields" })[0]);
			// System.out.println("filedsResult: " + fieldsResult);
			OleAutomation fieldsAuto = fieldsResult.getAutomation();
			Variant fieldResult2 = fieldsAuto.getProperty(fieldsAuto.getIDsOfNames(new String[] { "Item" })[0],
					new Variant[] { new Variant("vcr_play") });
			// System.out.println("fieldResult: " + fieldResult);
			OleAutomation fieldAuto2 = fieldResult2.getAutomation();

			fieldAuto2.setProperty(fieldAuto2.getIDsOfNames(new String[] { "value" })[0],
					new Variant[] { new Variant("0") });
		}

		public void click(String id) {
			if (auto == null)
				return;
			int[] rgdispid = auto.getIDsOfNames(new String[] { "Engine" });
			int dispIdMember = rgdispid[0];
			Variant result = auto.getProperty(dispIdMember);
			// System.out.println("Result: " + result.getDispatch());

			OleAutomation engineAuto = result.getAutomation();
			Variant nodesResult = engineAuto.getProperty(engineAuto.getIDsOfNames(new String[] { "Nodes" })[0]);
			// System.out.println("nodesResult: " +
			// nodesResult.getDispatch());

			OleAutomation nodesAuto = nodesResult.getAutomation();
			Variant itemResult = nodesAuto.getProperty(nodesAuto.getIDsOfNames(new String[] { "Item" })[0],
					new Variant[] { new Variant("VCR_CONTROL_SCRIPT") });
			// System.out.println("itemResult: " + itemResult);

			OleAutomation itemAuto = itemResult.getAutomation();
			Variant fieldsResult = itemAuto.getProperty(itemAuto.getIDsOfNames(new String[] { "Fields" })[0]);
			// System.out.println("filedsResult: " + fieldsResult);

			OleAutomation fieldsAuto = fieldsResult.getAutomation();
			Variant fieldResult = fieldsAuto.getProperty(fieldsAuto.getIDsOfNames(new String[] { "Item" })[0],
					new Variant[] { new Variant("vcr_set_position") });

			Variant fieldResult2 = fieldsAuto.getProperty(fieldsAuto.getIDsOfNames(new String[] { "Item" })[0],
					new Variant[] { new Variant("vcr_play") });
			// System.out.println("fieldResult: " + fieldResult);

			OleAutomation fieldAuto2 = fieldResult2.getAutomation();
			Variant v = fieldAuto2.getProperty(fieldAuto2.getIDsOfNames(new String[] { "value" })[0]);
			if (v.getString() != "0") {
				fieldAuto2.setProperty(fieldAuto2.getIDsOfNames(new String[] { "value" })[0],
						new Variant[] { new Variant("0") });
			}

			boolean b2 = fieldAuto2.setProperty(fieldAuto2.getIDsOfNames(new String[] { "value" })[0],
					new Variant[] { new Variant("-1") });

			OleAutomation fieldAuto = fieldResult.getAutomation();
			boolean b = fieldAuto.setProperty(fieldAuto.getIDsOfNames(new String[] { "value" })[0],
					new Variant[] { new Variant(id) });
			// System.out.println("field set value: " + b);

		}
	}

	/**
	 * Opens a new JFrame with BrowserCanvas in it
	 */
	public static void main(String[] args) {
		GUID guid = new GUID();
		char[] buffer = null;
		int count = CORTONA_PROG_ID.length();
		buffer = new char[count + 1];
		CORTONA_PROG_ID.getChars(0, count, buffer, 0);
		
		buffer = CORTONA_PROG_ID.toCharArray();
		if (COM.CLSIDFromProgID(buffer, guid) != COM.S_OK){
			int result = COM.CLSIDFromString(buffer, guid);
			if (result != COM.S_OK)
				System.out.println("is okkkkk");
		}

	}
}
