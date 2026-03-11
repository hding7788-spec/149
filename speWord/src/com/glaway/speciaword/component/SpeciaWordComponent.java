package com.glaway.speciaword.component;

import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;

import com.glaway.speciaword.dialog.BasicToleranceSymbolDialog;
import com.glaway.speciaword.dialog.CircleSymbolDialog;
import com.glaway.speciaword.dialog.ExpressionSymbolDialog;
import com.glaway.speciaword.dialog.FormToleranceSymbolDialog;
import com.glaway.speciaword.dialog.GenHuSymbolDialog;
import com.glaway.speciaword.dialog.PartToleranceSymbolDialog;
import com.glaway.speciaword.dialog.RoughSymbolDialog;
import com.glaway.speciaword.dialog.SpecSymbolDialog;
import com.glaway.speciaword.dialog.SpecialCodesDialog;
import com.glaway.speciaword.dialog.UpOrDownLogoSymbolDialog;

/**
 * @author mosesx
 * @date 2013-4-23
 * @version V1.0
 * 
 *          特殊字符组件
 */
public class SpeciaWordComponent extends JTextPane {

	private static final long serialVersionUID = 1L;
	private JPopupMenu popupMenu;
	private JMenuItem insertCircleSymbol;//
	private JMenuItem insertExpressionSymbol;//
	private JMenuItem insertRoughSymbol;//
	private JMenuItem insertUpOrDownLogoSymbol;//
	private JMenuItem insertBasicToleranceSymbol;//
	private JMenuItem insertPartToleranceSymbol;//
	private JMenuItem insertFormToleranceSymbol;//
	private JMenuItem insertSpecialCode;//
	private JMenuItem insertGenHuSymbol;//
	// 插入符号
	private JMenuItem insertSpecSymbol;//
	private JMenuItem insertGyCs;//

	private ExtendedHTMLEditorKit editorKit;
	private HTMLDocument document;
	private CustomHyperlinkClickInterface hyperlinkAction;
	private CheckTextContentInterface checkTextContentLengthInterface;
	private CheckDocumentChangeInterface checkDocumentChangeInterface;
	private final JTextArea sourceText = new JTextArea();
	private boolean isCanInput = true;
	private String backHTMLText = "";// 备份html文本信息
	
	private String imageFolder;
	
	public SpeciaWordComponent(String imageFolder) {
		super();
		this.imageFolder = imageFolder;
		initComponent();
		// 设置初始值： 否则格式有问题
		// TODO
		// 哈飞数据没有要求
		// setText("<p>&#160;</p>");
	}

	public String getImageFolder() {
		return imageFolder;
	}

	public void setImageFolder(String imageFolder) {
		this.imageFolder = imageFolder;
	}

	@SuppressWarnings("serial")
	private void initComponent() {
		editorKit = new ExtendedHTMLEditorKit(this);
		document = (ExtendedHTMLDocument) editorKit.createDefaultDocument();
		document.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
		document.setPreservesUnknownTags(false);
		this.setEditorKit(editorKit);
		this.setDocument(document);
		this.setMargin(new Insets(2, 2, 2, 2));
		this.setContentType("text/html;charset=utf-8");
		
		// initStyle();

		popupMenu = new JPopupMenu();

		insertCircleSymbol = new JMenuItem("插入带圈字符");
		insertExpressionSymbol = new JMenuItem("插入分式");
		insertRoughSymbol = new JMenuItem("插入表面粗糙度");
		insertUpOrDownLogoSymbol = new JMenuItem("插入上下标");
		insertBasicToleranceSymbol = new JMenuItem("插入基本公差");
		insertPartToleranceSymbol = new JMenuItem("插入局部公差");
		insertFormToleranceSymbol = new JMenuItem("插入形位公差");
		insertGenHuSymbol = new JMenuItem("插入根号弧形");
		insertSpecialCode = new JMenuItem("插入特殊符号");
		insertSpecSymbol = new JMenuItem("插入符号");
		insertGyCs = new JMenuItem("插入工艺参数");

		popupMenu.add(insertCircleSymbol);
		popupMenu.add(insertExpressionSymbol);
		popupMenu.add(insertRoughSymbol);
		popupMenu.add(insertUpOrDownLogoSymbol);
		// popupMenu.add(insertBasicToleranceSymbol);
		popupMenu.add(insertPartToleranceSymbol);
		popupMenu.add(insertFormToleranceSymbol);
		popupMenu.add(insertGenHuSymbol);
		popupMenu.add(insertSpecialCode);
		popupMenu.add(insertSpecSymbol);
		popupMenu.add(insertGyCs);

		MenuItemAction menuItemAction = new MenuItemAction(this);
		insertCircleSymbol.addActionListener(menuItemAction);
		insertExpressionSymbol.addActionListener(menuItemAction);
		insertRoughSymbol.addActionListener(menuItemAction);
		insertUpOrDownLogoSymbol.addActionListener(menuItemAction);
		insertBasicToleranceSymbol.addActionListener(menuItemAction);
		insertFormToleranceSymbol.addActionListener(menuItemAction);
		insertPartToleranceSymbol.addActionListener(menuItemAction);
		insertGenHuSymbol.addActionListener(menuItemAction);
		insertSpecialCode.addActionListener(menuItemAction);
		insertSpecSymbol.addActionListener(menuItemAction);
		insertGyCs.addActionListener(menuItemAction);

		this.add(popupMenu);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					// 不可编辑 不能弹出菜单
					if (isEditable()) {
						popupMenu.show(SpeciaWordComponent.this, e.getX(), e.getY());
					}
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				addMouseEvent(true);
			}
		});

		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				handleUnderLine();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				handleUnderLine();
			}

			@SuppressWarnings("static-access")
			@Override
			public void keyPressed(KeyEvent e) {
				handleUnderLine();
				// backHTMLText = getText();
				// doCheckText();
				// 处理DELETE/BACKSPACE删除事件
				char keyChar = e.getKeyChar();
				if (keyChar == e.VK_DELETE) {
					// 获取光标位置
					int mouseStation = SpeciaWordComponent.this.getCaretPosition();
					Document d = SpeciaWordComponent.this.getDocument();
					if (d != null) {
						ElementIterator ei = new ElementIterator(d);
						Element element;
						AttributeSet as;
						AttributeSet anchor;
						String href;
						while ((element = ei.next()) != null) {
							if (element.isLeaf()) {
								// 获取元素在文档中的位置
								int eStart = element.getStartOffset();
								int eEnd = element.getEndOffset();
								as = element.getAttributes();
								anchor = (AttributeSet) as.getAttribute(HTML.Tag.A);
								href = (anchor != null) ? (String) anchor.getAttribute(HTML.Attribute.HREF) : null;
								if (href != null) {
									// 判断此处为超链接
									if (mouseStation == eStart) {
										SpeciaWordComponent.this.select(eStart, eEnd);
									}
								}
								// 删除图片
								Object obj = as.getAttribute(HTML.Attribute.SRC);
								if (obj != null) {
									// 删除本地图片
									// if (mouseStation == eStart) {
									// File file = new File(obj.toString());
									// if (file.exists()) {
									// file.delete();
									// }
									// }
								}
							}
						}
					}
				} else if (keyChar == e.VK_BACK_SPACE) {
					// 获取光标位置
					int mouseStation = SpeciaWordComponent.this.getCaretPosition();
					Document d = SpeciaWordComponent.this.getDocument();
					if (d != null) {
						ElementIterator ei = new ElementIterator(d);
						Element element;
						AttributeSet as;
						AttributeSet anchorA;
						String href;
						while ((element = ei.next()) != null) {
							if (element.isLeaf()) {
								// 获取元素在文档中的位置
								int eStart = element.getStartOffset();
								int eEnd = element.getEndOffset();
								as = element.getAttributes();
								// 删除超链接
								anchorA = (AttributeSet) as.getAttribute(HTML.Tag.A);
								href = (anchorA != null) ? (String) anchorA.getAttribute(HTML.Attribute.HREF) : null;
								if (href != null) {
									// 判断此处为超链接
									if (mouseStation == eEnd) {
										SpeciaWordComponent.this.select(eStart, eEnd);
									}
								}
								// 删除图片
								Object obj = as.getAttribute(HTML.Attribute.SRC);
								if (obj != null) {
									// 删除本地图片
									// if (mouseStation == eEnd) {
									// File file = new File(obj.toString());
									// if (file.exists()) {
									// file.delete();
									// }
									// }
								}
							}
						}
					}
				} else if (keyChar == e.VK_LEFT) {
					// 键盘方向键<--
				} else if (keyChar == e.VK_RIGHT) {
					// 键盘方向键-->
				} else if (keyChar == e.VK_ENTER) {
					// insertBrToTextPane();
					// insertSpecialCode("\tr");
					insertBreak();
				} else if (keyChar == e.VK_SPACE) {
					// insertSpaceToTextPane();
				}
			}
		});

		this.getInputMap().put(KeyStroke.getKeyStroke(' '), "nbsp");
		this.getActionMap().put("nbsp", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				replaceSelection("\u00A0");
			}
		});

		// KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
		// textArea.getInputMap().put(enter, "none");

		// this.getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty,
		// "\n");
		// ((AbstractDocument) this.getDocument()).setDocumentFilter(new
		// HtmlLineBreakDocumentFilter());
	}

	// appears to only affect user keystrokes - not getText() and setText() as
	// claimed
	// class HtmlLineBreakDocumentFilter extends DocumentFilter {
	// @Override
	// public void insertString(DocumentFilter.FilterBypass fb, int offs, String
	// str, AttributeSet a)
	// throws BadLocationException {
	// super.insertString(fb, offs, str.replaceAll("\n", "\n\r"), a); // works
	// }
	//
	// @Override
	// public void replace(FilterBypass fb, int offs, int length, String str,
	// AttributeSet a)
	// throws BadLocationException {
	// super.replace(fb, offs, length, str.replaceAll("\n", "\n\r"), a); //
	// works
	// }
	// }

	private void doCheckText() {
		if (isCanInput) {
			isCanInput = fireCheckTextContentLengthAction(getText());
		}
		if (!isCanInput) {
			setText(backHTMLText);
			refreshOnUpdate();
		}

		isCanInput = true;
	}

	/**
	 * 鼠标事件处理
	 * 
	 * @param flag
	 *            是否执行超链接点击事件
	 */
	private void addMouseEvent(boolean flag) {
		// 获取光标位置
		int mouseStation = SpeciaWordComponent.this.getCaretPosition();
		Document d = SpeciaWordComponent.this.getDocument();
		if (d != null) {
			ElementIterator ei = new ElementIterator(d);
			Element element;
			AttributeSet as;
			AttributeSet anchor;
			String href;
			while ((element = ei.next()) != null) {
				if (element.isLeaf()) {
					// 获取元素在文档中的位置
					int eStart = element.getStartOffset();
					int eEnd = element.getEndOffset();
					if (eStart < mouseStation && mouseStation < eEnd) {
						as = element.getAttributes();
						anchor = (AttributeSet) as.getAttribute(HTML.Tag.A);
						href = (anchor != null) ? (String) anchor.getAttribute(HTML.Attribute.HREF) : null;
						if (href != null) {
							if (flag) {
								// 点击超练级校验完毕
								// 执行自定义点击事件
								fireCustomHyperlinkClickAction(href);
							}
							// 鼠标位置移至元素结尾 防止非法输入
							SpeciaWordComponent.this.setCaretPosition(eEnd);
						}
					}
				}
			}
		}
	}

	/**
	 * 添加自定义菜单
	 * 
	 * @param newMenu
	 */
	public void addCustomMenu(JMenuItem newMenu) {
		popupMenu.add(newMenu);
	}

	/**
	 * 添加分割线
	 * 
	 */
	public void addSeparator() {
		popupMenu.addSeparator();
	}

	/**
	 * 添加自定义超链接监听器
	 * 
	 * @param listener
	 */
	public void addCustomHyperlinkListener(CustomHyperlinkClickInterface listener) {
		hyperlinkAction = listener;
	}

	/***
	 * 执行自定义点击事件
	 */
	private void fireCustomHyperlinkClickAction(String href) {
		if (hyperlinkAction != null) {
			hyperlinkAction.doAction(href);
		}
	}

	/**
	 * 添加自定义校验字符长度
	 * 
	 * @param listener
	 */
	public void addCheckTextContentLengthListener(CheckTextContentInterface listener) {
		checkTextContentLengthInterface = listener;
		// 设置图片存储位置
//		CommonHelper.setLocalImageFolder(checkTextContentLengthInterface.localImagePath());
	}
	
	/**
	 * 监听文档对象变化
	 * 
	 * @param objCheckDocumentChangeInterface
	 */
	public void addCheckDocumentChangeListener(CheckDocumentChangeInterface objCheckDocumentChangeInterface) {
		checkDocumentChangeInterface = objCheckDocumentChangeInterface;
		document.addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				checkDocumentChangeInterface.documentContentChange(getText());
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
		        checkDocumentChangeInterface.documentContentChange(getText());
		        
		        repaint();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				checkDocumentChangeInterface.documentContentChange(getText());
			}
		});
	}

	/***
	 * 执行校验长度方法：textPane内容长度发生变化时执行此方法
	 * 
	 * @param href
	 *            内容字符串
	 * @return true校验通过/false校验失败
	 */
	public boolean fireCheckTextContentLengthAction(String href) {
		if (checkTextContentLengthInterface != null) {
			// TODO
			return checkTextContentLengthInterface.checkContentLength(href);
		}
		return true;
	}

	@Override
	public void paste() {

		//复制粘贴不换行问题：获取剪贴板中的内容，过滤掉所有富文本信息，只截取string数据
		try {
			// // 获取剪贴板 并对内容过滤
//			 Toolkit toolkit = Toolkit.getDefaultToolkit();
//			 Clipboard clipboard = toolkit.getSystemClipboard();
//			 Transferable tran = clipboard.getContents(null);
//			 // 只拿到string类型的数据
//			 String clipboardContent = (String)
//			 tran.getTransferData(DataFlavor.stringFlavor);
//			 
//			 clipboardContent = clipboardContent.replaceAll(" ", "\u00A0");
//			 // 将内容放到系统剪切板
//			 Transferable tText = new StringSelection(clipboardContent);
//			 clipboard.setContents(tText, null);
//			
//			 backHTMLText = getText();

			super.paste();
			// // 校验长度是否合法
			// doCheckText();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initStyle() {
		// 设置字体
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setFontSize(attr, 20);

		StyledEditorKit k = getStyledEditorKit(this);
		MutableAttributeSet inputAttributes = k.getInputAttributes();
		inputAttributes.addAttributes(attr);
	}

	protected static final StyledDocument getStyledDocument(JEditorPane e) {
		Document d = e.getDocument();
		if (d instanceof StyledDocument) {
			return (StyledDocument) d;
		}
		throw new IllegalArgumentException("document must be StyledDocument");
	}

	protected static final StyledEditorKit getStyledEditorKit(JEditorPane e) {
		EditorKit k = e.getEditorKit();
		if (k instanceof StyledEditorKit) {
			return (StyledEditorKit) k;
		}
		throw new IllegalArgumentException("EditorKit must be StyledEditorKit");
	}

	/**
	 * 刷新试图
	 */
	public void refreshOnUpdate() {
		int caretPos = this.getCaretPosition();
//		this.setText(this.getText());
		sourceText.setText(this.getText());
		this.setText(sourceText.getText());
		this.setCaretPosition(caretPos);
		this.repaint();
	}

	/***
	 * 
	 * @param strSrc
	 */
	private void insertImageToTextPane(String strSrc) {
		if (null != strSrc) {
			try {
				backHTMLText = this.getText();
				int iPosition = this.getCaretPosition();
//				String imageTag = "<img align='center' src=\"" + strSrc + "\"></img>";
				String imageTag = "<img src=\"" + strSrc + "\"></img>";
				editorKit.insertHTML(document, iPosition, imageTag, 0, 0, HTML.Tag.IMG);
				this.setCaretPosition(iPosition + 1);
				doCheckText();
				refreshOnUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 *
	 * @param strSrc
	 */
	private void insertGyCsToTextPane(String strSrc) {
		if (null != strSrc) {
			try {
				backHTMLText = this.getText();
				int iPosition = this.getCaretPosition();
				String imageTag = "<img src=\"" + strSrc + "\"></img>";
				editorKit.insertHTML(document, iPosition, imageTag, 0, 0, HTML.Tag.IMG);
				this.setCaretPosition(iPosition + 1);
				doCheckText();
				refreshOnUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 *
	 * @param strSrc
	 */
	private void insertBrToTextPane() {
		try {
			backHTMLText = this.getText();
			int iPosition = this.getCaretPosition();
			editorKit.insertHTML(document, iPosition, "<BR>", 0, 0, HTML.Tag.BR);
			// editorKit.insertHTML(document, iPosition, "<P>", 0, 0,
			// HTML.Tag.P);
			// document.insertString(iPosition, "\n", getCharacterAttributes());
			this.setCaretPosition(iPosition + 1);
			doCheckText();
			refreshOnUpdate();

			// this.setText(this.getText());
			// sourceText.setText(this.getText());
			// this.setText(sourceText.getText());
			this.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void insertBreak() {
		try {
			int caretPos = this.getCaretPosition();
			editorKit.insertHTML(document, caretPos, "<BR>", 0, 0, HTML.Tag.BR);
			this.setCaretPosition(caretPos + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 
	 * @param strSrc
	 */
	// private void insertSpaceToTextPane() {
	// try {
	// String spaceTag = "&nbsp;";
	// backHTMLText = this.getText();
	// int iPosition = this.getCaretPosition();
	// document.insertString(iPosition, spaceTag, getCharacterAttributes());
	// this.setCaretPosition(iPosition);
	// doCheckText();
	// refreshOnUpdate();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/***
	 * 插入超链接
	 * 
	 * @param strSrc
	 */
	public void insertHLinkToTextPane(Map<String, String> strATagAtrr) {
		if (!strATagAtrr.isEmpty()) {
			try {
				backHTMLText = this.getText();
				Iterator<Map.Entry<String, String>> iter = strATagAtrr.entrySet().iterator();
				String aTag = "<a";
				while (iter.hasNext()) {
					Entry<String, String> entry = iter.next();
					if (entry.getKey().equals("name")) {
						continue;
					}
					aTag = aTag + " " + entry.getKey() + "=\"" + entry.getValue() + "\" ";
				}
				aTag = aTag + ">" + strATagAtrr.get("name") + "</a>";
				int iPosition = this.getCaretPosition();
				editorKit.insertHTML(document, iPosition, aTag, 0, 0, HTML.Tag.A);
				this.setCaretPosition(iPosition + 1);
				doCheckText();
				refreshOnUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * 插入特殊字符
	 * 
	 * @param strSrc
	 */
	public void insertSpecialCode(String text) {
		insertSelectText(text);
	}

	/***
	 * 插入字符
	 * 
	 * @param strSrc
	 */
	public void insertSelectText(String text) {
		if (null != text && !text.equals("")) {
			try {
				backHTMLText = this.getText();
				int iPosition = this.getCaretPosition();
				insertSelectText(text, iPosition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/***
	 * 插入字符,到指定位置
	 * 
	 * @param strSrc
	 */
	public void insertSelectText(String text, int index) {
		try {
			backHTMLText = this.getText();
			int position = this.getCaretPosition();
			if (index == 0 && position > index) {
				index = position;
			} else {
				index = Math.min(position, index);
			}
			document.insertString(index, text, getCharacterAttributes());
			// document.insertString(position, text, getCharacterAttributes());
			this.setCaretPosition(index + text.length());
			// this.setCaretPosition(position + text.length());
			doCheckText();
			refreshOnUpdate();
			requestFocusInWindow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 插入超链接后 移除A标签样式
	 */
	private void handleUnderLine() {
		StyledEditorKit kit = getStyledEditorKit(this);
		MutableAttributeSet attr = kit.getInputAttributes();
		@SuppressWarnings("rawtypes")
		Enumeration enumObj = attr.getAttributeNames();
		while (enumObj.hasMoreElements()) {
			Object objAttr = enumObj.nextElement();
			if (objAttr.toString().equals("a")) {
				attr.removeAttribute(objAttr);
			}
		}
	}

	@Override
	public String getText() {
		try {
			String dataStr = super.getText().trim().replaceAll("\"", "'");
			// 处理img标签未关闭情况
			dataStr = imgTagComp(dataStr, "<img");
			dataStr = imgTagComp(dataStr, "<IMG");

			// System.out.println("==super.getText()==" + dataStr);
//			 dataStr = filterUselessTag(dataStr);
			 dataStr = checkPtagRight(dataStr);

			int start = 0;
			int end = 0;
			final StringBuffer buffer = new StringBuffer();
			while (start > -1) {
				int t = dataStr.indexOf("&#");
				if (t > -1) {
					int system = 10;// 进制
					if (start == 0) {
						buffer.append(dataStr.substring(0, t));
						if (start != t)
							start = t;
					}
					end = dataStr.indexOf(";", start + 2);
					String charStr = "";
					if (end != -1) {
						charStr = dataStr.substring(start + 2, end);
						// 判断进制
						char s = charStr.charAt(0);
						if (s == 'x' || s == 'X') {
							system = 16;
							charStr = charStr.substring(1);
						}
					}// 转换
					try {
						if (!charStr.equals("160")) {// 空格的十进制数字是160--做特殊处理
							char letter = (char) Integer.parseInt(charStr, system);
							buffer.append(new Character(letter).toString());
						} else {
							buffer.append("&nbsp;");
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
					// 处理当前unicode字符到下一个unicode字符之间的非unicode字符
					start = dataStr.indexOf("&#", end);
					if (start - end > 1) {
						buffer.append(dataStr.substring(end + 1, start));
					}
					// 处理最后面的非unicode字符
					if (start == -1) {
						int length = dataStr.length();
						if (end + 1 != length) {
							buffer.append(dataStr.substring(end + 1, length));
						}
					}
				} else {
					buffer.append(dataStr);
					start = -1;
				}
			}
			return buffer.toString();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return super.getText();
	}

	private String imgTagComp(String data, String splitStr) {
		String[] arrs = data.split(splitStr);
		String html = arrs[0];
		for (int i = 1; i < arrs.length; i++) {
			String subHtml = splitStr;
			String s = arrs[i];
			char[] cs = s.toCharArray();
			int js = 0;
			for (char c : cs) {
				if (js == 2) {
					if (c == '>') {
						subHtml += "/>";
						js++;
					} else {
						subHtml += c;
					}
				} else {
					subHtml += c;
					if (c == '"') {
						js++;
					}
				}
			}
			html += subHtml;
		}

		return html;
	}

	private String filterUselessTag(String html) {
		StringBuffer buffer = new StringBuffer();
		int start = 0;
		// int end = 0;
		int index = html.indexOf("<font");
		if (index < 0) {
			buffer.append(html);
		} else {
			while (start > -1) {
				int temp = html.indexOf("<font");
				buffer.append(html.substring(0, temp));
				start = html.indexOf(">", temp);
				html = html.substring(start + 1);

				temp = html.indexOf("</font>");
				buffer.append(html.substring(0, temp));
				html = html.substring(temp + 7);

				if (html.indexOf("<font") < 0) {
					buffer.append(html);
					start = -1;
				}

			}
		}

		return buffer.toString();
	}

	private String checkPtagRight(String html) {
		StringBuffer buffer = new StringBuffer();
		int start = 0;
		// int end = 0;
		int index = html.indexOf("<p");
		int iBr = html.indexOf("<br>");
		if (index < 0 && iBr > 0) {
//			buffer.append(html);
//			//不存在P标签则视为p丢失
//			//TODO
//			//丛<body>开始找
//			int iBody = html.indexOf("<body>");
//			buffer.append(html.substring(0, iBody + 6)).append("<p style='margin-top:5'>");
//			//判断是否存在<br>
//			int brIndex = html.indexOf("<br>");
//			if(0 < brIndex){
//				
//			}
			//将所有<BR>替换为</p><p>
			html = html.replaceAll("<br>", "</p><p style='margin-top:5'>");
			//丛<body>开始找
			int iBody = html.indexOf("<body>");
//			buffer.append(html.substring(0, iBody + 6)).append("<p style='margin-top:5'>");
			
			html = html.substring(0, iBody + 6) + "<p style='margin-top:5'>" + html.substring(iBody + 7);
			int iCBody = html.indexOf("</body>");
			html = html.substring(0, iCBody) + "</p>" + html.substring(iCBody);
//			System.out.println("====<P>丢失-------checkPtagRight-----");
//			System.out.println(html);
			return html;
		} else if(index > 0) {
			while (start > -1) {
				int temp = html.indexOf("<p");
				buffer.append(html.substring(0, temp)).append("<p style='margin-top:5'>");
				start = html.indexOf(">", temp);
				html = html.substring(start + 1);

				if (html.indexOf("<p") < 0) {
					buffer.append(html);
					start = -1;
				}
			}
		}else{
			return html;
		}

		return buffer.toString();
	}

	// @Override
	// public String getText() {
	// String dataStr = super.getText().replaceAll("\"", "'");
	// // 处理img标签未关闭情况
	// dataStr = imgTagComp(dataStr, "<img");
	// dataStr = imgTagComp(dataStr, "<IMG");
	// return dataStr;
	// }

	/**
	 * 
	 * @author mosesx
	 * @date 2013-4-23
	 * @version V1.0
	 */
	private class MenuItemAction implements ActionListener {

		private SpeciaWordComponent spComponent;
		public MenuItemAction(SpeciaWordComponent comp){
			spComponent = comp;
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source.equals(insertCircleSymbol)) {
				CircleSymbolDialog circleSymbolDialog = new CircleSymbolDialog("circleWord",getImageFolder());
				insertImageToTextPane(circleSymbolDialog.getImageSrc());
			} else if (source.equals(insertExpressionSymbol)) {
				ExpressionSymbolDialog expressionSymbolDialog = new ExpressionSymbolDialog("expression",getImageFolder());
				insertImageToTextPane(expressionSymbolDialog.getImageSrc());
			} else if (source.equals(insertRoughSymbol)) {
				RoughSymbolDialog roughSymbolDialog = new RoughSymbolDialog("rough",getImageFolder());
				insertImageToTextPane(roughSymbolDialog.getImageSrc());
			} else if (source.equals(insertUpOrDownLogoSymbol)) {
				UpOrDownLogoSymbolDialog upOrDownLogoSymbolDialog = new UpOrDownLogoSymbolDialog("updown",getImageFolder());
				insertImageToTextPane(upOrDownLogoSymbolDialog.getImageSrc());
			} else if (source.equals(insertBasicToleranceSymbol)) {
				BasicToleranceSymbolDialog basicToleranceSymbolDialog = new BasicToleranceSymbolDialog("basicTolerance",getImageFolder());
				insertImageToTextPane(basicToleranceSymbolDialog.getImageSrc());
			} else if (source.equals(insertFormToleranceSymbol)) {
				FormToleranceSymbolDialog formToleranceDialog = new FormToleranceSymbolDialog("formTolerance",getImageFolder());
				insertImageToTextPane(formToleranceDialog.getImageSrc());
			} else if (source.equals(insertPartToleranceSymbol)) {
				PartToleranceSymbolDialog partToleranceSymbolDialog = new PartToleranceSymbolDialog("partTolerance",getImageFolder());
				insertImageToTextPane(partToleranceSymbolDialog.getImageSrc());
			} else if (source.equals(insertSpecialCode)) {
				/*SpecialCodesDialog specialCodesDialog = */new SpecialCodesDialog(spComponent);
				//insertSpecialCode(specialCodesDialog.getSpecialCode());
			} else if (source.equals(insertSpecSymbol)) {
				SpecSymbolDialog specSymbolDialog = new SpecSymbolDialog("specWord",getImageFolder());
				insertImageToTextPane(specSymbolDialog.getImageSrc());
			}else if(source.equals(insertGenHuSymbol)){
				GenHuSymbolDialog specSymbolDialog = new GenHuSymbolDialog("genhu",getImageFolder());
				insertImageToTextPane(specSymbolDialog.getImageSrc());
			}
			else if(source.equals(insertGyCs)){
				GyCsDialog gycsDialog = new GyCsDialog("insertGyCs");
				insertGyCsToTextPane(gycsDialog.getImageSrc());
			}
		}
	}
}
