package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserCommandEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserListener;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserNavigationEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowOpeningEvent;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserWindowWillOpenEvent;

import com.glaway.mpm.qmIntf.technics.template.TemplateBuilder;
import com.glaway.mpm.task.CmTaskExecutorCallback;
import com.glaway.mpm.task.CmTaskInfo;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaGuiUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.visual.view.ui.VaAbstractDialog;

public class DpCreoAnnoDialog extends VaAbstractDialog {

	private JWebBrowser					browser;
	private static final VaLogger		logger		= VaLogger.getLogger();
	String								basePath	= System.getProperty("java.io.tmpdir");
	private static VaActionProgressBar	animFrame	= null;
	private boolean						initCreo;
	private boolean						resetNeeded;

	static {
		if (!NativeInterface.isOpen())
			NativeInterface.open();
	}

	public DpCreoAnnoDialog(Vector vec, Window owner) {
		super(owner);
		initUI();
		this.getContentPane().add(browser);

		this.setSize(800, 600);
		this.setBounds(VaGuiUtil.getScreenCenter(this.getWidth(), this.getHeight()));
		this.setModal(true);
		this.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
		if (initCreo) {

			initCreo = false;
			this.animFrame = new VaActionProgressBar(null, null, "加载图形", "加载图形", "正在进行Creo View可视化，请等待...");

			this.animFrame.setSize(400, 300);
			this.animFrame.setVisible(true);
		}
		try {

			PVRunner createPVRunner = new PVRunner(vec);
			createPVRunner.start();

			this.validate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				Object o = browser.executeJavascriptWithResult("return confirm('请在退出前保存注释集,是否退出?')");
				logger.debug("ret:"+o.toString());
				if(!(Boolean)o)
				{
					return;
				}
				
				super.windowClosing(arg0);
			}
		});
	}

	public static void main(String[] args) {
		Vector v = new Vector();
		v.add("D:\\\\model\\\\chooser_complete_asm.pvz");

		DpCreoAnnoDialog d = new DpCreoAnnoDialog(v, null);
		d.setVisible(true);
	}

	class PVRunner extends Thread {
		private Vector	v;

		PVRunner(Vector vecFilePath) {
			v = vecFilePath;
		}

		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						CmTaskHelper.registerTaskExecutor("DpCreoAnnoDialog.finishAnimFrame", DpCreoAnnoDialog.this,
								true);
					} catch (CmTaskException e) {
						e.printStackTrace();
					}
					String pvFile = v.get(0).toString().replaceAll("\\\\", "\\\\\\\\");
					String url = getCreoUrl(pvFile);
					browser.navigate(url);
					try {
						CmTaskHelper.sendTask(CmTaskInfo.newCmTaskInfo("DpCreoAnnoDialog.finishAnimFrame",
								DpCreoAnnoDialog.this, null), null);
					} catch (CmTaskException e) {
						e.printStackTrace();
					}
				}
			});
		}
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
	}

	String getCreoUrl(String pvFile) {

		Map root = new HashMap();
		root.put("creoPath", "'" + pvFile + "'");

		File file = new File(basePath + "temp");
		if (!file.exists()) {
			file.mkdir();
		}

		String rePath = this.getClass().getResource(this.getClass().getSimpleName() + ".class").getFile();

		logger.debug("rePath : " + rePath);
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
					logger.debug("Jar Entry Name : " + name);

					if (name.equals("resource/pvlaunch.js") || name.equals("resource/pvUtils.js")) {
						logger.debug(file.getAbsolutePath());
						CommonUtil.writeInputStreamToFile(jarFile.getInputStream(entry), file.getAbsolutePath()
								+ File.separator + name.substring(name.lastIndexOf("/") + 1));

					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// File file2 = new
		// File(CreoBrowser.class.getResource("resource").getFile());
		// for (int i = 0; i < file2.list().length; i++) {
		// logger.debug("Resource " + i +" : " + file2.list()[i]);
		// }
		// FileUtil.copyDirectory(file2, basePath+"temp");

		TemplateBuilder builder = new TemplateBuilder();
		if (builder.makeCreoPanel("creoAnnotation.html", basePath + "temp", root, "creoAnnotation.html")) {
			logger.debug("basePath====" + basePath + "temp\\creoAnnotation.html");
			return basePath + "temp\\creoAnnotation.html";
		}

		return "";
	}

	@Override
	protected void initDimension() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initActions() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initComponents() {
		browser = new JWebBrowser();
		browser.setBarsVisible(false);
		browser.setStatusBarVisible(false);
		browser.addWebBrowserListener(new WebBrowserAdapter() {
			
			@Override
			public void windowWillOpen(WebBrowserWindowWillOpenEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowOpening(WebBrowserWindowOpeningEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WebBrowserEvent arg0) {
				// TODO Auto-generated method stub
				DpCreoAnnoDialog.this.dispose();
			}
			
			@Override
			public void titleChanged(WebBrowserEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void statusChanged(WebBrowserEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void locationChanging(WebBrowserNavigationEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void locationChanged(WebBrowserNavigationEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void locationChangeCanceled(WebBrowserNavigationEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void loadingProgressChanged(WebBrowserEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void commandReceived(WebBrowserCommandEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	protected void initLayout() {
		setLayout(new BorderLayout());
		add(browser, BorderLayout.CENTER);
	}

	@Override
	protected void loadInitDatas() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void registerTaskExecutor() throws CmTaskException {

	}

	@Override
	protected void unregisterTaskExecutor() throws CmTaskException {
		CmTaskHelper.unregisterTaskExecutor(this);

	}
}
