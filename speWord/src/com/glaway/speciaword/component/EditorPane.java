package com.glaway.speciaword.component;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

import com.glaway.speciaword.common.CommonHelper;

/**
 * @author mosesx
 * @date 2013-4-23
 * @version V1.0
 */
public class EditorPane extends JScrollPane implements /* SWObserver, */Observer {

	private static final long serialVersionUID = 1L;
	private SpeciaWordComponent speciaWordComponent;
	private String imageFolder;
	
	public EditorPane(String imageFolder) {
		super(JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.imageFolder = imageFolder;
		initComponent();
	}

	public EditorPane(boolean scrollAble, String imageFolder) {
		super(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.imageFolder = imageFolder;
		initComponent();
	}

	private void initComponent() {
		speciaWordComponent = new SpeciaWordComponent(imageFolder);
		// JScrollPane sp = new JScrollPane(this);
		// sp.setSize(new Dimension(400, 400));
		// this.setLayout(new BorderLayout());
		// this.add(speciaWordComponent, BorderLayout.CENTER);
		this.getViewport().add(speciaWordComponent);
		// CommonHelper.SWOBSERVABLE_INS.addObserver(this);
		
		addCheckDocumentChangeListener(new CheckDocumentChangeInterface() {
			@Override
			public void documentContentChange(String strContent) {
				
			}
		});
	}

	@Override
	public void setSize(Dimension size) {
		speciaWordComponent.setSize(size);
		super.setSize(size);
	}

	/**
	 * 濠电儑缍�褎鎱ㄩ悙鐑樺殜妞ゅ繐瀚弳鏉库槈閺傛妯嬬憸鏉跨Ч瀹曪繝鏁撻弬銈嗗
	 * 
	 * @param newMenu
	 */
	public void addCustomMenu(JMenuItem newMenu) {
		speciaWordComponent.addCustomMenu(newMenu);
	}

	/**
	 * 闂佸湱绮敮鎺楀矗閸℃瑦鎯ラ柛娑樼摠閹崇娀鏌熼悮瀛樺闁跨噦鎷�
	 * 
	 * @param strSrc
	 */
	public void insertHLinkToTextPane(Map<String, String> strATagAtrr) {
		speciaWordComponent.insertHLinkToTextPane(strATagAtrr);
	}

	/**
	 * 监听文档对象变化
	 * 
	 * @param objCheckDocumentChangeInterface
	 */
	public void addCheckDocumentChangeListener(CheckDocumentChangeInterface objCheckDocumentChangeInterface) {
		speciaWordComponent.addCheckDocumentChangeListener(objCheckDocumentChangeInterface);
	}

	/**
	 * 濠电儑缍�褎鎱ㄩ悙鍨儱闁告稑鐡ㄩ幊鐘绘煙閹帒鍔ゆ繛鎻掑暣瀹曘儵顢楅敓鐣屽綔
	 * 
	 * @param listener
	 */
	public void addCustomHyperlinkListener(CustomHyperlinkClickInterface listener) {
		speciaWordComponent.addCustomHyperlinkListener(listener);
	}

	/**
	 * 濠电儑缍�褎鎱ㄩ悙鐑樺殜妞ゅ繐瀚弳鏉库槈閺傛妯囬柣妤�椤ㄨ偐浠﹂悾灞炬喕缂備焦顨х粻鎾诲汲鏉堛劍鍎熼柨鐔告灮閹凤拷
	 * 
	 * @param listener
	 */
	public void addCheckTextContentLengthListener(CheckTextContentInterface listener) {
		speciaWordComponent.addCheckTextContentLengthListener(listener);
	}

	/**
	 * 濠电儑缍�褎鎱ㄩ悙鍝勭闁糕剝顨滈ˉ娑氱磼閹惧懏瀚归柨鐕傛嫹
	 * 
	 */
	public void addSeparator() {
		speciaWordComponent.addSeparator();
	}

	public void setText(String strText) {
		speciaWordComponent.setText(CommonHelper.replaceReadSeperator(strText, imageFolder));
		
//		System.out.println(strText);
	}

	public String getText() {
		if (speciaWordComponent.getDocument().getLength() < 2) {
			// setText("<p>&#160;</p>");
		}
//		System.out.println("====getText====" + speciaWordComponent.getText());
		
		return speciaWordComponent.getText();
	}

	@Override
	public void setEnabled(boolean enabled) {
		speciaWordComponent.setEditable(enabled);
		super.setEnabled(enabled);
	}

	public void setTechnicsPath(String imageFolder) {
		this.imageFolder = imageFolder;
		speciaWordComponent.setImageFolder(imageFolder);
	}
	
	public String getTechnicsPath() {
		return this.imageFolder;
	}

	public void insertText(String text) {
		speciaWordComponent.insertSelectText(text);
	}

	public void insertText(String text, int index) {
		speciaWordComponent.insertSelectText(text, index);
	}

	public void setBackgroundColor(Color color) {
		speciaWordComponent.setBackground(color);
	}

	public SpeciaWordComponent getSpeciaWordComponent() {
		return speciaWordComponent;
	}

	// @Override
	// public void update(JComponent comp, String text) {
	// if (comp == this) {
	// speciaWordComponent.insertSelectText(text);
	// }
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub

	}

}
