package com.glaway.speciaword.component;

import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.FormView;
import javax.swing.text.html.HTML;

/**
 * 
 * @author MosesX
 * 
 *         Form提交按钮处理方案
 * 
 */
public class ExtendedFormView extends FormView {

	public ExtendedFormView(Element elem) {
		super(elem);
	}

	@Override
	protected void submitData(String data) {
		Element form = getFormElement();
		if (form != null) {
			super.submitData(data);
		} else {
			// TODO
			// 自定义事件处理
			System.out.println("BUTTON  CLICK-----");
		}
	}

	private Element getFormElement() {
		Element elem = getElement();
		while (elem != null) {
			if (elem.getAttributes().getAttribute(StyleConstants.NameAttribute) == HTML.Tag.FORM) {
				return elem;
			}
			elem = elem.getParentElement();
		}
		return null;
	}
}
