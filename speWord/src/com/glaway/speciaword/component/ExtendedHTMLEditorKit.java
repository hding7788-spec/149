package com.glaway.speciaword.component;

import javax.swing.SizeRequirements;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;
import javax.swing.text.html.StyleSheet;

public class ExtendedHTMLEditorKit extends HTMLEditorKit {
	private static final long serialVersionUID = 1L;

	SpeciaWordComponent objSpeciaWordComponent;

	public ExtendedHTMLEditorKit(SpeciaWordComponent conponent) {
		objSpeciaWordComponent = conponent;
	}

	@Override
	public ViewFactory getViewFactory() {
		return new HTMLFactoryExtended(objSpeciaWordComponent);
	}

	// @Override
	// public void write(Writer out, Document doc, int pos, int len) throws IOException, BadLocationException {
	// if (doc instanceof HTMLDocument) {
	// ExtendedHTMLWriter w = new ExtendedHTMLWriter(out, (HTMLDocument) doc, pos, len);
	// w.write();
	// } else {
	// super.write(out, doc, pos, len);
	// }
	// }

	@Override
	public Document createDefaultDocument() {
		StyleSheet styles = getStyleSheet();
		StyleSheet ss = new StyleSheet();
		ss.addStyleSheet(styles);
//		ss.addRule("p{margin-top:5px;}");
		// 设置字体
//		MutableAttributeSet attr = new SimpleAttributeSet();
//		StyleConstants.ParagraphConstants.
//		StyleConstants.setFontSize(attr, 20); 
//		ss.addAttributes(new SimpleAttributeSet(), attr);
		
		ExtendedHTMLDocument doc = new ExtendedHTMLDocument(ss, objSpeciaWordComponent);
		doc.setParser(getParser());
		doc.setAsynchronousLoadPriority(4);
		doc.setTokenThreshold(100);
		return doc;
	}

	public static class HTMLFactoryExtended extends HTMLFactory implements ViewFactory {
		private SpeciaWordComponent SpeciaWordComponent;

		public HTMLFactoryExtended(SpeciaWordComponent conponent) {
			SpeciaWordComponent = conponent;
		}

		@Override
		public View create(Element elem) {
			Object obj = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
			if (obj instanceof HTML.Tag) {
				HTML.Tag tagType = (HTML.Tag) obj;
				if (tagType == HTML.Tag.IMG) {
					return new RelativeImageView(elem);
				} /*
				 * else if (tagType == HTML.Tag.P) { return new WrapParagraphView(elem); }
				 */

			}
			View v = super.create(elem);
			if (v instanceof InlineView) {
				return new InlineView(elem) {
					@Override
					public int getBreakWeight(int axis, float pos, float len) {
						return GoodBreakWeight;
					}

					@Override
					public View breakView(int axis, int p0, float pos, float len) {
						if (axis == View.X_AXIS) {
							checkPainter();
							int p1 = getGlyphPainter().getBoundedPosition(this, p0, pos, len);
							if (p0 == getStartOffset() && p1 == getEndOffset()) {
								return this;
							}
							return createFragment(p0, p1);
						}
						return this;
					}
				};
			} else if (v instanceof ParagraphView) {
				return new ParagraphView(elem) {
					@Override
					protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
						if (r == null) {
							r = new SizeRequirements();
						}
						float min = layoutPool.getMinimumSpan(axis);
						r.minimum = (int) min;
						r.preferred = Math.max(r.minimum, SpeciaWordComponent.getSize().width - 2);// (int) pref);
						r.maximum = Integer.MAX_VALUE;
						r.alignment = 0.0f;
						return r;
					}

				};
			}
			return v;
		}
	}
}
