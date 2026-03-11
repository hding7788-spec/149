package com.glaway.speciaword.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageObserver;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.event.DocumentEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Position;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import com.glaway.speciaword.common.SvgTranscoderToPng;
import com.glaway.speciaword.util.SPJavaUtil;

public class RelativeImageView extends View implements ImageObserver, MouseListener, MouseMotionListener {
	public static final String TOP = "top";
	public static final String TEXTTOP = "texttop";
	public static final String MIDDLE = "middle";
	public static final String ABSMIDDLE = "absmiddle";
	public static final String CENTER = "center";
	public static final String BOTTOM = "bottom";
	public static final String IMAGE_CACHE_PROPERTY = "imageCache";

	private static Icon sPendingImageIcon;
	private static Icon sMissingImageIcon;
	private static final String PENDING_IMAGE_SRC = "icons/ImagePendingHK.gif";
	private static final String MISSING_IMAGE_SRC = "icons/ImageMissingHK.gif";
	private static final int DEFAULT_WIDTH = 32;
	private static final int DEFAULT_HEIGHT = 32;
	private static final int DEFAULT_BORDER = 1;

	private final AttributeSet attr;
	private Element fElement;
	private Image fImage;
	private int fHeight;
	private int fWidth;
	private Container fContainer;
	private Rectangle fBounds;
	private Component fComponent;
	private Point fGrowBase;
	private boolean bLoading;

	public RelativeImageView(Element elem) {
		super(elem);
		initialize(elem);
		StyleSheet sheet = getStyleSheet();
		attr = sheet.getViewAttributes(this);
	}

	@SuppressWarnings("unchecked")
	private void initialize(Element elem) {
		synchronized (this) {
			bLoading = true;
			fWidth = 0;
			fHeight = 0;
		}
		int width = 0;
		int height = 0;
		boolean customWidth = false;
		boolean customHeight = false;
		try {
			fElement = elem;
			@SuppressWarnings("unused")
			AttributeSet attr = elem.getAttributes();
			if (isURL()) {
				URL src = getSourceURL();
				if (src != null) {
					@SuppressWarnings("rawtypes")
					Dictionary cache = (Dictionary) getDocument().getProperty(IMAGE_CACHE_PROPERTY);
					if (cache != null) {
						fImage = (Image) cache.get(src);
					} else {
						fImage = Toolkit.getDefaultToolkit().getImage(src);
					}
				}
			} else {
				String src = (String) fElement.getAttributes().getAttribute(HTML.Attribute.SRC);
				src = processSrcPath(src);
				if (src.startsWith("SPW:")) {
					String type = src.substring(src.indexOf("SPW:") + 4, src.lastIndexOf(":"));
					String[] data = src.substring(src.lastIndexOf(":") + 1).split(",");
					@SuppressWarnings("rawtypes")
					Map map = new HashMap();
					for (String str : data) {
						// 鍖呭惈鈥淿width鈥濈殑瀛楃 杞崲涓篸ouble鍨嬬殑
						if (str.contains("_width")) {
							map.put(str.substring(0, str.indexOf("=")),
									Double.parseDouble(str.substring(str.indexOf("=") + 1)));
						} else {
							String text = str.substring(str.indexOf("=") + 1);
							map.put(str.substring(0, str.indexOf("=")),SPJavaUtil.replaceAtSymbol(text));
						}
					}
					SvgTranscoderToPng svg = SvgTranscoderToPng.getInstance();
					fImage = svg.makeImage("",type, map);
					// 鏈湴鍖栨柟娉曚笉鑳芥斁鍦ㄨ繖閲�姝ゆ柟娉曚細璋冪敤澶氭
				} else {
					fImage = Toolkit.getDefaultToolkit().createImage(src);
				}

				try {
					waitForImage();
				} catch (InterruptedException ie) {
					fImage = null;
				} catch (Exception ex) {
					fImage = null;
				}

			}

			height = getIntAttr(HTML.Attribute.HEIGHT, -1);
			customHeight = (height > 0);
			if (!customHeight && fImage != null) {
				height = fImage.getHeight(this);
			}
			if (height <= 0) {
				height = DEFAULT_HEIGHT;
			}

			width = getIntAttr(HTML.Attribute.WIDTH, -1);
			customWidth = (width > 0);
			if (!customWidth && fImage != null) {
				width = fImage.getWidth(this);
			}
			if (width <= 0) {
				width = DEFAULT_WIDTH;
			}

			if (fImage != null) {
				if (customHeight && customWidth) {
					Toolkit.getDefaultToolkit().prepareImage(fImage, height, width, this);
				} else {
					Toolkit.getDefaultToolkit().prepareImage(fImage, -1, -1, this);
				}
			}
		} finally {
			synchronized (this) {
				bLoading = false;
				if (customHeight || fHeight == 0) {
					fHeight = height;
				}
				if (customWidth || fWidth == 0) {
					fWidth = width;
				}
			}
		}
	}

	private boolean isURL() {
		String src = (String) fElement.getAttributes().getAttribute(HTML.Attribute.SRC);
		return src.toLowerCase().startsWith("file") || src.toLowerCase().startsWith("http");
	}

	private String processSrcPath(String src) {
		String val = src;
		File imageFile = new File(src);
		if (imageFile.isAbsolute()) {
			return src;
		}
		boolean found = false;
		Document doc = getDocument();
		if (doc != null) {
			String pv = (String) doc.getProperty("com.hexidec.ekit.docsource");
			if (pv != null) {
				File f = new File(pv);
				val = (new File(f.getParent(), imageFile.getPath().toString())).toString();
				found = true;
			}
		}
		if (!found) {
			String imagePath = System.getProperty("system.image.path.key");
			if (imagePath != null) {
				val = (new File(imagePath, imageFile.getPath())).toString();
			}
		}
		return val;
	}

	private void waitForImage() throws InterruptedException {
		int w = fImage.getWidth(this);
		int h = fImage.getHeight(this);
		while (true) {
			int flags = Toolkit.getDefaultToolkit().checkImage(fImage, w, h, this);
			if (((flags & ERROR) != 0) || ((flags & ABORT) != 0)) {
				throw new InterruptedException();
			} else if ((flags & (ALLBITS | FRAMEBITS)) != 0) {
				return;
			}
			Thread.sleep(10);
		}
	}

	@Override
	public AttributeSet getAttributes() {
		return attr;
	}

	boolean isLink() {
		AttributeSet anchorAttr = (AttributeSet) fElement.getAttributes().getAttribute(HTML.Tag.A);
		if (anchorAttr != null) {
			return anchorAttr.isDefined(HTML.Attribute.HREF);
		}
		return false;
	}

	int getBorder() {
		return getIntAttr(HTML.Attribute.BORDER, isLink() ? DEFAULT_BORDER : 0);
	}

	int getSpace(int axis) {
		return getIntAttr((axis == X_AXIS) ? HTML.Attribute.HSPACE : HTML.Attribute.VSPACE, 0);
	}

	Color getBorderColor() {
		StyledDocument doc = (StyledDocument) getDocument();
		return doc.getForeground(getAttributes());
	}

	@SuppressWarnings("static-access")
	float getVerticalAlignment() {
		String align = (String) fElement.getAttributes().getAttribute(HTML.Attribute.ALIGN);
		if (align != null) {
			align = align.toLowerCase();
			if (align.equals(TOP) || align.equals(TEXTTOP)) {
				return 0.0f;
			} else if (align.equals(this.CENTER) || align.equals(MIDDLE) || align.equals(ABSMIDDLE)) {
				return 0.5f;
			}
		}
		return 1.0f;
	}

	boolean hasPixels(ImageObserver obs) {
		return ((fImage != null) && (fImage.getHeight(obs) > 0) && (fImage.getWidth(obs) > 0));
	}

	private URL getSourceURL() {
		String src = (String) fElement.getAttributes().getAttribute(HTML.Attribute.SRC);
		if (src == null) {
			return null;
		}
		URL reference = ((HTMLDocument) getDocument()).getBase();
		try {
			URL u = new URL(reference, src);
			return u;
		} catch (MalformedURLException mue) {
			return null;
		}
	}

	private int getIntAttr(HTML.Attribute name, int iDefault) {
		AttributeSet attr = fElement.getAttributes();
		if (attr.isDefined(name)) {
			int i;
			String val = (String) attr.getAttribute(name);
			if (val == null) {
				i = iDefault;
			} else {
				try {
					i = Math.max(0, Integer.parseInt(val));
				} catch (NumberFormatException nfe) {
					i = iDefault;
				}
			}
			return i;
		} else {
			return iDefault;
		}
	}

	@Override
	public void setParent(View parent) {
		super.setParent(parent);
		fContainer = ((parent != null) ? getContainer() : null);
		if ((parent == null) && (fComponent != null)) {
			fComponent.getParent().remove(fComponent);
			fComponent = null;
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {
		super.changedUpdate(e, a, f);
		float align = getVerticalAlignment();

		int height = fHeight;
		int width = fWidth;

		initialize(getElement());

		boolean hChanged = fHeight != height;
		boolean wChanged = fWidth != width;
		if (hChanged || wChanged || getVerticalAlignment() != align) {
			getParent().preferenceChanged(this, hChanged, wChanged);
		}
	}

	@Override
	public void paint(Graphics g, Shape a) {
		Color oldColor = g.getColor();
		fBounds = a.getBounds();
		int border = getBorder();
		int x = fBounds.x + border + getSpace(X_AXIS);
		int y = fBounds.y + border + getSpace(Y_AXIS) + 3;
		int width = fWidth;
		int height = fHeight;
		int sel = getSelectionState();

		if (!hasPixels(this)) {
			g.setColor(Color.lightGray);
			g.drawRect(x, y, width - 1, height - 1);
			g.setColor(oldColor);
			loadImageStatusIcons();
			Icon icon = ((fImage == null) ? sMissingImageIcon : sPendingImageIcon);
			if (icon != null) {
				icon.paintIcon(getContainer(), g, x, y);
			}
		}

		if (fImage != null) {
			g.drawImage(fImage, x, y, width, height, this);
		}

		Color bc = getBorderColor();
		if (sel == 2) {
			int delta = 2 - border;
			if (delta > 0) {
				x += delta;
				y += delta;
				width -= delta << 1;
				height -= delta << 1;
				border = 2;
			}
			bc = null;
			g.setColor(Color.black);
			g.fillRect(x + width - 5, y + height - 5, 5, 5);
		}

		if (border > 0) {
			if (bc != null) {
				g.setColor(bc);
			}
			for (int i = 1; i <= border; i++) {
				g.drawRect(x - i, y - i, width - 1 + i + i, height - 1 + i + i);
			}
			g.setColor(oldColor);
		}
	}

	protected void repaint(long delay) {
		if ((fContainer != null) && (fBounds != null)) {
			fContainer.repaint(delay, fBounds.x, fBounds.y, fBounds.width, fBounds.height);
		}
	}

	protected int getSelectionState() {
		int p0 = fElement.getStartOffset();
		int p1 = fElement.getEndOffset();
		if (fContainer instanceof JTextComponent) {
			JTextComponent textComp = (JTextComponent) fContainer;
			int start = textComp.getSelectionStart();
			int end = textComp.getSelectionEnd();
			if ((start <= p0) && (end >= p1)) {
				if ((start == p0) && (end == p1) && isEditable()) {
					return 2;
				} else {
					return 1;
				}
			}
		}
		return 0;
	}

	protected boolean isEditable() {
		return ((fContainer instanceof JEditorPane) && ((JEditorPane) fContainer).isEditable());
	}

	protected Color getHighlightColor() {
		JTextComponent textComp = (JTextComponent) fContainer;
		return textComp.getSelectionColor();
	}

	private static boolean sIsInc = true;
	private static int sIncRate = 100;

	@Override
	public boolean imageUpdate(Image img, int flags, int x, int y, int width, int height) {
		if ((fImage == null) || (fImage != img)) {
			return false;
		}

		if ((flags & (ABORT | ERROR)) != 0) {
			fImage = null;
			repaint(0);
			return false;
		}

		short changed = 0;
		if ((flags & ImageObserver.HEIGHT) != 0) {
			if (!getElement().getAttributes().isDefined(HTML.Attribute.HEIGHT)) {
				changed |= 1;
			}
		}
		if ((flags & ImageObserver.WIDTH) != 0) {
			if (!getElement().getAttributes().isDefined(HTML.Attribute.WIDTH)) {
				changed |= 2;
			}
		}

		synchronized (this) {
			if ((changed & 1) == 1) {
				fWidth = width;
			}
			if ((changed & 2) == 2) {
				fHeight = height;
			}
			if (bLoading) {
				return true;
			}
		}

		if (changed != 0) {
			Document doc = getDocument();
			try {
				if (doc instanceof AbstractDocument) {
					((AbstractDocument) doc).readLock();
				}
				preferenceChanged(this, true, true);
			} finally {
				if (doc instanceof AbstractDocument) {
					((AbstractDocument) doc).readUnlock();
				}
			}
			return true;
		}

		if ((flags & (FRAMEBITS | ALLBITS)) != 0) {
			repaint(0);
		} else if ((flags & SOMEBITS) != 0) {
			if (sIsInc) {
				repaint(sIncRate);
			}
		}
		return ((flags & ALLBITS) == 0);
	}

	@Override
	public float getPreferredSpan(int axis) {
		int extra = 2 * (getBorder() + getSpace(axis));
		switch (axis) {
		case View.X_AXIS:
			return fWidth + extra;
		case View.Y_AXIS:
			return fHeight + extra;
		default:
			throw new IllegalArgumentException("Invalid axis in getPreferredSpan() : " + axis);
		}
	}

	@Override
	public float getAlignment(int axis) {
		switch (axis) {
		case View.Y_AXIS:
			return getVerticalAlignment();
		default:
			return super.getAlignment(axis);
		}
	}

	@Override
	public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
		int p0 = getStartOffset();
		int p1 = getEndOffset();
		if ((pos >= p0) && (pos <= p1)) {
			Rectangle r = a.getBounds();
			if (pos == p1) {
				r.x += r.width;
			}
			r.width = 0;
			return r;
		}
		return null;
	}

	@Override
	public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
		Rectangle alloc = (Rectangle) a;
		if (x < (alloc.x + alloc.width)) {
			bias[0] = Position.Bias.Forward;
			return getStartOffset();
		}
		bias[0] = Position.Bias.Backward;
		return getEndOffset();
	}

	protected void resize(int width, int height) {
		if ((width == fWidth) && (height == fHeight)) {
			return;
		}
		fWidth = width;
		fHeight = height;
		MutableAttributeSet attr = new SimpleAttributeSet();
		attr.addAttribute(HTML.Attribute.WIDTH, Integer.toString(width));
		attr.addAttribute(HTML.Attribute.HEIGHT, Integer.toString(height));
		((StyledDocument) getDocument()).setCharacterAttributes(fElement.getStartOffset(), fElement.getEndOffset(),
				attr, false);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Dimension size = fComponent.getSize();
		if ((e.getX() >= (size.width - 7)) && (e.getY() >= (size.height - 7)) && (getSelectionState() == 2)) {
			Point loc = fComponent.getLocationOnScreen();
			fGrowBase = new Point(loc.x + e.getX() - fWidth, loc.y + e.getY() - fHeight);
			// fGrowProportionally = e.isShiftDown();
		} else {
			fGrowBase = null;
			JTextComponent comp = (JTextComponent) fContainer;
			int start = fElement.getStartOffset();
			int end = fElement.getEndOffset();
			int mark = comp.getCaret().getMark();
			int dot = comp.getCaret().getDot();
			if (e.isShiftDown()) {
				if (mark <= start) {
					comp.moveCaretPosition(end);
				} else {
					comp.moveCaretPosition(start);
				}
			} else {
				if (mark != start) {
					comp.setCaretPosition(start);
				}
				if (dot != end) {
					comp.moveCaretPosition(end);
				}
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (fGrowBase != null) {
			Point loc = fComponent.getLocationOnScreen();
			int width = Math.max(2, loc.x + e.getX() - fGrowBase.x);
			int height = Math.max(2, loc.y + e.getY() - fGrowBase.y);
			if (e.isShiftDown() && fImage != null) {
				float imgWidth = fImage.getWidth(this);
				float imgHeight = fImage.getHeight(this);
				if ((imgWidth > 0) && (imgHeight > 0)) {
					float prop = imgHeight / imgWidth;
					float pwidth = height / prop;
					float pheight = width * prop;
					if (pwidth > width) {
						width = (int) pwidth;
					} else {
						height = (int) pheight;
					}
				}
			}
			resize(width, height);
		}
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		fGrowBase = null;
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		if (me.getClickCount() == 2) {
		}
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		;
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		;
	}

	@Override
	public void mouseExited(MouseEvent me) {
		;
	}

	private Icon makeIcon(final String gifFile) throws IOException {
		InputStream resource = RelativeImageView.class.getResourceAsStream(gifFile);

		if (resource == null) {
			return null;
		}
		BufferedInputStream in = new BufferedInputStream(resource);
		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int n;
		while ((n = in.read(buffer)) > 0) {
			out.write(buffer, 0, n);
		}
		in.close();
		out.flush();

		buffer = out.toByteArray();
		if (buffer.length == 0) {
			System.err.println("WARNING : " + gifFile + " is zero-length");
			return null;
		}
		return new ImageIcon(buffer);
	}

	private void loadImageStatusIcons() {
		try {
			if (sPendingImageIcon == null) {
				sPendingImageIcon = makeIcon(PENDING_IMAGE_SRC);
			}
			if (sMissingImageIcon == null) {
				sMissingImageIcon = makeIcon(MISSING_IMAGE_SRC);
			}
		} catch (Exception e) {
			System.err.println("ImageView : Couldn't load image icons");
		}
	}

	protected StyleSheet getStyleSheet() {
		HTMLDocument doc = (HTMLDocument) getDocument();
		return doc.getStyleSheet();
	}

}
