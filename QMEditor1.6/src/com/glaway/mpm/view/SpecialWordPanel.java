package com.glaway.mpm.view;

import com.glaway.mpm.qmIntf.commonString.CsSearchDialog;
import com.glaway.mpm.qmIntf.tecparam.TechnicsParamSearchDialog;
import com.glaway.mpm.resource.CopyCache;
import com.glaway.mpm.util.CommonObservable;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.speciaword.common.CommonHelper;
import com.glaway.speciaword.component.EditorPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Observable;

public class SpecialWordPanel extends EditorPane {

	private JMenuItem pasteUseSize = new JMenuItem("粘贴零件名称");
	private JMenuItem pasteStepName = new JMenuItem("粘贴下料尺寸");
	private JMenuItem terminologyItem = new JMenuItem("插入常用语");
	private JMenuItem cyyItem = new JMenuItem("常用语快速查询");
	private JMenuItem techincsParam = new JMenuItem("插入工艺参数");

	private JFrame frame;
	private JDialog dialog;
	private Component component;
	private String imageFolder;

	public SpecialWordPanel(JFrame frame, boolean flag,String imageFolder) {
		super(flag, imageFolder);
		this.imageFolder = imageFolder;
		this.frame = frame;
		this.component = frame;
		init();

	}

	public SpecialWordPanel(JDialog dialog, boolean flag,String imageFolder) {
		super(flag, imageFolder);
		this.dialog = dialog;
		this.component = dialog;
		init();
	}

	public SpecialWordPanel(JFrame frame, String imageFolder) {
		super(imageFolder);
		this.frame = frame;
		this.component = frame;
		init();
	}

	public SpecialWordPanel(JDialog dialog, String imageFolder) {
		super(imageFolder);
		this.dialog = dialog;
		this.component = dialog;
		init();
	}

	private void init() {
		addCustomMenu();
		initAction();
	}

	private void addCustomMenu() {
		addCustomMenu(pasteUseSize);
		addCustomMenu(terminologyItem);
		addCustomMenu(pasteStepName);
		addCustomMenu(cyyItem);
		addCustomMenu(techincsParam);
	}

	@Override
	public void setText(String strText) {
		String content = CommonHelper.replaceReadSeperator(strText, this.imageFolder);
		if(content.contains("<!--EndFragment-->\n")){
			content = content.replace("<!--EndFragment-->\n","");
			content = content.replace("<!--EndFragment-->","");
            }
		super.setText(content);
	}

	public void initAction() {
		pasteUseSize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String partName = CopyCache.partName;
					if (partName != null) {
						insertText(partName);
					} else {
						JOptionPane.showMessageDialog(component, "请先复制零件名称！",
								"提示", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});

		terminologyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String commonString = null;
					String terminologyXMLPath = WorkSpaceUtil
							.getPersonalTerminologyDirectory();
					CsSearchDialog dia;
					if (dialog == null) {
						dia = new CsSearchDialog(terminologyXMLPath, frame);
					} else {
						dia = new CsSearchDialog(terminologyXMLPath, dialog);
					}
					commonString = dia.showDialog();
					if (commonString == null) {
						commonString = "";
					}
					insertText(commonString);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});

		pasteStepName.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String userSize = CopyCache.userSize;
					if (userSize != null) {
						insertText(userSize);
					} else {
						JOptionPane.showMessageDialog(component, "请先复制下料尺寸！",
								"提示", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		});



		cyyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, Event.CTRL_MASK));

		cyyItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				new CyyDialog((NewTechnicsPart) frame);

			  }
			});

		techincsParam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					String param = null;
					TechnicsParamSearchDialog dia;
					if(dialog == null) {
						dia = new TechnicsParamSearchDialog(frame);
					} else {
						dia = new TechnicsParamSearchDialog(dialog);
					}
					param = dia.showDialog();
					if(param == null) {
						param = "";
					}
					insertText(param);
				} catch(Exception ee) {
					ee.printStackTrace();
				}
			}
		});

	}

	@Override
	public void update(Observable o, Object arg) {
		if (o != null && o instanceof CommonObservable) {
			CommonObservable co = (CommonObservable) o;
			int type = co.getType();
			if (type == 0) {
				if (arg != null && arg instanceof String) {
					String commonString = (String) arg;
					insertText(commonString);
				}
			}
		}
	}
}
