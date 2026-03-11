package com.glaway.mpm.qmIntf.viewPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import com.glaway.mpm.qmIntf.technics.template.TemplateBuilder;
import com.glaway.mpm.resource.Constants;
import com.glaway.mpm.task.CmTaskExecutorCallback;
import com.glaway.mpm.task.CmTaskInfo;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.view.action.VaAction;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.ui.VaAbstractPanel;

public class CreoViewPanel extends VaAbstractPanel {
	private static final long			serialVersionUID	= 2685289616311556306L;
	private static final VaLogger		logger				= VaLogger.getLogger();
	private static VaActionProgressBar	animFrame			= null;
	private VaAction					actZoomAll;
	private VaAction					actZoomSelected;
	private JButton						btnZoomAll;
	private JButton						btnZoomSelected;
	private JLabel						labelStatus;
	private JLabel						labelNoFile;
	private boolean						initCreo			= false;
	PVRunner							createPVRunner;
//	CreoBrowser							panel;
	String			basePath	= System.getProperty("java.io.tmpdir");
	private JWebBrowser webBrowser;
	private MyWebBrowserAdapter webBrowserAdapter;

	static {
		if(!NativeInterface.isOpen())
			NativeInterface.open();
	}

	public static void main(String[] args) {
		new CreoViewPanel();
	}

	// @Override
	// public void addNotify() {
	// logger.debug(" addNotify()!");
	//
	// super.addNotify();
	// }

	public CreoViewPanel() {
		super();
		// this.animFrame = null;
		try {
			initUI();
		} catch (CmTaskException e) {
			e.printStackTrace();
		}
	}

	protected void initActions() {

	}

	protected void initComponents() {
		btnZoomAll = new JButton(actZoomAll);
		btnZoomAll.setForeground(Color.WHITE);

		btnZoomSelected = new JButton(actZoomSelected);
		btnZoomSelected.setForeground(Color.WHITE);

		labelNoFile = new JLabel("图像显示区域");
		labelStatus = new JLabel(" CreoView : ");
		labelStatus.setBorder(new EtchedBorder());

		webBrowser = new JWebBrowser();
		webBrowser.setBarsVisible(false);
		webBrowser.setStatusBarVisible(false);
		webBrowserAdapter = new MyWebBrowserAdapter();

		webBrowser.addWebBrowserListener(webBrowserAdapter);
	}

	protected void initLayout() {

		setLayout(new BorderLayout());
		// add(buildToolBar(), BorderLayout.NORTH);
		// System.out.println(name + " " + VaPViewFactory.getPViewImpl(name));
		// add(VaPViewFactory.getPViewImpl(name).getPanelContext(),
		// BorderLayout.CENTER);
		add(labelNoFile, BorderLayout.CENTER);
		add(labelStatus, BorderLayout.SOUTH);
		add(webBrowser,BorderLayout.CENTER);
		initCreo = true;

	}

	protected void loadInitDatas() {
	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {
		// CmTaskHelper.registerTaskExecutor("mainframe.setStatus",
		// CreoViewPanel.this, false);
	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {
		CmTaskHelper.unregisterTaskExecutor(this);
	}

	public synchronized boolean setView(Vector<?> vecFilePath) {

		if (initCreo) {
			remove(labelNoFile);
			initCreo = false;
			animFrame = new VaActionProgressBar(null, null, "加载图形", "加载图形", "正在进行Creo View可视化，请等待...");

			animFrame.setSize(400, 300);
			animFrame.setVisible(true);
		}

		try {
			createPVRunner = new PVRunner(vecFilePath);
			createPVRunner.start();
			this.validate();
		} catch (Exception e) {
			logger.error(e);
			return false;
		}
		return true;
	}

	public synchronized void finishAnimFrame(Object render, Object params, CmTaskExecutorCallback callback) {
		logger.debug("finishAnimFrame");
		if (animFrame != null) {
			try {
				animFrame.finish();
				animFrame = null;
				logger.debug(".postTask");
				// CmTaskHelper.postTask("mainframe.setStatus", "可视化装载完成");
			} catch (Throwable tt) {
				tt.printStackTrace();
			} finally {
				animFrame = null;

			}
		}
//		panel.setVisible(true);
	}

	public void setStatus(Object render, Object param, CmTaskExecutorCallback callback) {
		StringBuffer buf = new StringBuffer(" creoview : ");
		if (param instanceof Object[]) {
			Object[] params = (Object[]) param;
			for (Object obj : params)
				buf.append(String.valueOf(obj));
		} else
			buf.append(String.valueOf(param));

		labelStatus.setText(buf.toString());
	}

	class PVRunner extends Thread {
		private Vector<?>	v;

		PVRunner(Vector<?> vecFilePath) {
			v = vecFilePath;
		}
		
		public void run() {
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						CmTaskHelper.registerTaskExecutor("CreoViewPanel.finishAnimFrame", CreoViewPanel.this, true);
					} catch (CmTaskException e) {
						logger.error(e);
					}
					
					String pvFile = v.get(0).toString();
					final File workSpaceFile = new File(pvFile);
					webBrowserAdapter.setWorkSpaceFile(workSpaceFile);
					pvFile = workSpaceFile.getAbsolutePath();
					pvFile = pvFile.replaceAll("\\\\","\\\\\\\\");
					String url = getCreoUrl(pvFile);
					webBrowser.navigate(url);
					try {
						CmTaskHelper.sendTask(CmTaskInfo.newCmTaskInfo("CreoViewPanel.finishAnimFrame", CreoViewPanel.this, null), null);
					} catch(CmTaskException e) {
						logger.error(e);
					}
				}
			});
		}
	}
	
	

	String getCreoUrl(String pvFile) {

		Map<String,String> root = new HashMap<String,String>();
		System.out.println(pvFile);
		root.put("creoPath", "'" + pvFile + "'");

		File file = new File(basePath + "temp");
		if (!file.exists()) {
			file.mkdir();
		}

		String rePath = this.getClass().getResource(this.getClass().getSimpleName() + ".class").getFile();

		System.out.println("rePath : " + rePath);
		if (rePath.indexOf(".jar!") > 0) {
			try {

				rePath = "jar:" + rePath.substring(0, rePath.indexOf("!") + 2);

				URL url = new URL(rePath);
				JarURLConnection con = (JarURLConnection) url.openConnection();

				// JarFile file = new JarFile(url.getFile());
				JarFile jarFile = con.getJarFile();

				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					String name = entry.getName();
					System.out.println("Jar Entry Name : " + name);

					if (name.equals("resource/pvlaunch.js") || name.equals("resource/pvUtils.js")) {
						System.out.println(file.getAbsolutePath());
						CommonUtil.writeInputStreamToFile(jarFile.getInputStream(entry), file.getAbsolutePath()
								+ File.separator + name.substring(name.lastIndexOf("/") + 1));

					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		TemplateBuilder builder = new TemplateBuilder();
		if (builder.makeCreoPanel("creo.html", basePath + "temp", root,Constants.FM_BUILD_CREO_HTML)) {
			System.out.println("basePath====" + basePath + "temp\\creo.html");
			return basePath + "temp\\creo.html";
		}

		return "";
	}

	@Override
	protected void initDimension() {
	}
	
	private class MyWebBrowserAdapter extends WebBrowserAdapter {
		private File workSpaceFile;
		
		public void setWorkSpaceFile(File workSpaceFile) {
			this.workSpaceFile = workSpaceFile;
		}
		
		@Override
		public void commandReceived(WebBrowserCommandEvent e) {
			String command = e.getCommand();
			if(command == null)
				return;
			logger.debug("Command received: " + command);
			if(command.equals("loadfinished")) { // Creo View load done. Capture the screen.
				try {
					TimeUnit.MILLISECONDS.sleep(1000);
				} catch(InterruptedException ie) {
					ie.printStackTrace();
				}
				webBrowser.executeJavascript("captureThumbnail();");
			} else if(command.equals("captureThumbnail")) { // Capture the screen done. Copy the thumbnail to workspace.
				// copy thumbnail to work space
				String tempThumbnail = (String) webBrowser.executeJavascriptWithResult("return document.getElementById('thumbnailImage').value;");
				if(tempThumbnail != null) {
					logger.debug("temp thumbnail: " + tempThumbnail);
					
					try {
						FileInputStream fis = new FileInputStream(new File(tempThumbnail));
						String thumbnailName = workSpaceFile.getName().substring(0,workSpaceFile.getName().lastIndexOf("."))+ "" + "_short.jpg";
						CommonUtil.writeInputStreamToFile(fis,workSpaceFile.getParent() + File.separator + thumbnailName);
					} catch(IOException ie) {
						logger.error("Failed to copy thumbnail gif.",ie);
					}
				} else {
					logger.error("javascript return null!!!!!!!!!");
				}
			} else {
				super.commandReceived(e);
			}
		}
	}
}