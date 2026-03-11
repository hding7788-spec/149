package com.glaway.mpm.qmIntf.decoratePView.showPanel.view;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.glaway.mpm.qmIntf.technics.template.TemplateBuilder;
import com.glaway.mpm.task.CmTaskInfo;
import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.task.util.CmTaskHelper;
import com.glaway.mpm.util.AntTaskUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.FileUtil;

//import com.glaway.gpms.viewPanel.Cortona3DPanel.DisplayThread;
//import com.glaway.gpms.viewPanel.Cortona3DPanel.ShellThread;

public class DpCreoBrowser extends Panel {
	Display			display;
	Canvas			canvas;
	// String basePath = CreoBrowser.class.getResource("").toString()
	// .replace("file:/", "");

	String			basePath	= System.getProperty("java.io.tmpdir");
	Browser			browser;
	boolean			isLoaded	= false;
	DisplayThread	displayThread;
	String			creoPath;
	Vector			creoVec;

	// static Shell shell;

	public DpCreoBrowser() {
		System.setProperty("sun.awt.xembedserver", "true");

		displayThread = new DisplayThread();

		canvas = new Canvas();
		this.setLayout(new BorderLayout());
		this.add(canvas, BorderLayout.CENTER);
	}

	public void setView(Vector v) {
		if (isLoaded) {
			display = displayThread.getDisplay();
			System.out.println("************");
			this.creoPath = (String) v.get(0);
			creoVec = v;// javascript:loadModel('"+creoVec.get(0)+"')
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					String fileUrl = creoVec.get(0).toString();
					File viewFile = new File(fileUrl);

					if (viewFile.exists() && viewFile.isFile()) {
						System.out.println("viewFile.getAbsolutePath() : " + viewFile.getAbsolutePath());
						browser.execute("loadModel('" + viewFile.getAbsolutePath().replace("\\", "\\\\") + "')");
					} else {
						JOptionPane.showMessageDialog(null, "简图未找到");
					}

				}
			});

		} else {
			displayThread.start();

//			System.out.println("***********1111*");
			display = displayThread.getDisplay();
//			System.out.println("************");
			this.creoPath = (String) v.get(0);
			creoVec = v;
			display.syncExec(new Runnable() {
				@Override
				public void run() {
//					System.out.println("-------------");

					if (isLoaded)
						return;

//					System.out.println("++++++++++++");

					final Shell shell = SWT_AWT.new_Shell(display, canvas);
					shell.setLayout(new FillLayout(SWT.DOWN));
					browser = new Browser(shell, SWT.EMBEDDED);
					shell.addDisposeListener(new DisposeListener() {

						@Override
						public void widgetDisposed(DisposeEvent arg0) {
							browser.dispose();
						}
					});
					browser.addTitleListener(new TitleListener() {
						public void changed(TitleEvent event) {
							System.out.println("TitleEvent: " + event.title);
							shell.setText(event.title);
						}
					});
					
					browser.setUrl(getCreoUrl(creoVec));
					browser.setVisible(true);

					shell.open();

					shell.setFullScreen(true);

				}
			});

		}
	}

	String getCreoUrl(Vector vec) {

		Map root = new HashMap();
		System.out.println(vec);
		root.put("creoPath", "'" + vec.get(0).toString().replace("\\", "\\\\") + "'");

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

		// File file2 = new
		// File(CreoBrowser.class.getResource("resource").getFile());
		// for (int i = 0; i < file2.list().length; i++) {
		// System.out.println("Resource " + i +" : " + file2.list()[i]);
		// }
		// FileUtil.copyDirectory(file2, basePath+"temp");

		TemplateBuilder builder = new TemplateBuilder();
		if (builder.makeCreoPanel("creoAnnotation.html", basePath + "temp", root,"creoAnnotation.html")) {
			System.out.println("basePath====" + basePath + "temp\\creoAnnotation.html");
			return basePath + "temp\\creoAnnotation.html";
		}

		return "";
	}

	public void click(String actionId) {
		final String id = actionId;
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				browser.execute("playaction('" + id + "')");

			}
		});
	}

	public void play() {
		display.asyncExec(new Runnable() {

			@Override
			public void run() {
				browser.execute("play()");

			}
		});
	}

	private class DisplayThread extends Thread {
		private Display	display;
		Object			sem	= new Object();

		public void run() {
			synchronized (sem) {
				display = Display.getDefault();
				sem.notifyAll();
			}

			swtEventLoop();
		}

		private void swtEventLoop() {
			while (true) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}

		public Display getDisplay() {
			try {
				synchronized (sem) {
					while (display == null) {
						sem.wait();
					}
					return display;
				}
			} catch (Exception e) {
				return null;
			}

		}
	}

	public static void main(String[] args) {
		System.out.println("begins");
		JFrame frame = new JFrame();
		final DpCreoBrowser panel = new DpCreoBrowser();
		frame.setLayout(new BorderLayout());
		frame.add(panel);
		JButton button = new JButton();
		frame.add(button, BorderLayout.SOUTH);
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				panel.play();
			}
		});
		frame.setSize(1000, 800);
		frame.setVisible(true);
		frame.pack();
		System.out.println(DpCreoBrowser.class.getResource(""));
		Vector v = new Vector();
		v.add("D:\\annotest\\20\\-1\\anno.pvs");
		// v.add("D:\\\\KEEP\\\\test\\\\prt0002_process_01.pvs");
		// v.add("D:\\\\KEEP\\\\test\\\\prt0002_process_01.pvs注释集:ddd");
		// v.add("D:\\\\KEEP\\\\test\\\\prt0002_process_01.pvs注释集:kkk");
		panel.setView(v);
	}
}
