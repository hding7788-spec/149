/**
 * 
 */
package com.glaway.speciaword.component;

import java.io.IOException;
import java.io.Writer;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLWriter;

/**
 * @author MosesX
 * @2013-7-1
 * 
 */
public class ExtendedHTMLWriter extends HTMLWriter {

	/**
	 * @param w
	 * @param doc
	 * @param pos
	 * @param len
	 */
	public ExtendedHTMLWriter(Writer w, HTMLDocument doc, int pos, int len) {
		super(w, doc, pos, len);
	}

	/**
	 * @param w
	 * @param doc
	 */
	public ExtendedHTMLWriter(Writer w, HTMLDocument doc) {
		super(w, doc);
	}

	@Override
	protected void output(char[] chars, int start, int length) throws IOException {
		super.output(chars, start, length);
	}
}
