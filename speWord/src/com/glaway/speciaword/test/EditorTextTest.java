package com.glaway.speciaword.test;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.glaway.speciaword.component.CheckTextContentInterface;
import com.glaway.speciaword.component.CustomHyperlinkClickInterface;
import com.glaway.speciaword.component.EditorPane;

/**
 * @author mosesx
 * @date 2013-4-24
 * @version V1.0
 */
public class EditorTextTest extends JFrame {

	private static final long serialVersionUID = 1L;

	public EditorTextTest() throws IOException {
		this.setSize(400, 400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getPreferredSize().width) / 2,
				(screenSize.height - this.getPreferredSize().height) / 2);
		this.setTitle("TextTest");
		// 扩展右键菜单功能
		// 自定义菜单功胄1�7
		JMenuItem test1 = new JMenuItem("Test1");
		test1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				JOptionPane.showMessageDialog(null, "test 1...");
			}
		});
		// 自定义菜单功胄1�7
		JMenuItem test2 = new JMenuItem("Test2");
		test2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				JOptionPane.showMessageDialog(null, "test 2...");
			}
		});

		final EditorPane pane = new EditorPane(null);
		// pane.setSize(new Dimension(220, 220));

		// Bug处理，可以禁用组仄1�7
		// pane.setEnabled(false);

		// 自定义菜单功胄1�7
		JMenuItem test3 = new JMenuItem("插入超链掄1�7");
		final Map<String, String> map = new HashMap<String, String>();
		map.put("工程图号001", "http://www.baidu.com/symbol/001");
		test3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				pane.insertHLinkToTextPane(map);
			}
		});
		JMenuItem test4 = new JMenuItem("插入超链掄1�72");
		final Map<String, String> map2 = new HashMap<String, String>();
		map2.put("工程图号00222", /* "http://www.baidu.com/symbol/00222" */"<clselection>123123</clselection>");
		test4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
				pane.insertHLinkToTextPane(map2);
			}
		});
		// 添加自定义菜卄1�7
		pane.addSeparator();
		pane.addCustomMenu(test1);
		pane.addCustomMenu(test2);
		pane.addCustomMenu(test3);
		pane.addCustomMenu(test4);
		// 添加超链接监听器
		pane.addCustomHyperlinkListener(new CustomHyperlinkClickInterface() {
			@Override
			public void doAction(String href) {
				// 此处定义事件处理超链接点击事仄1�7
				// TODO
				JOptionPane.showMessageDialog(null, href);
			}
		});

		// 添加棄1�7测文朄1�7
		pane.addCheckTextContentLengthListener(new CheckTextContentInterface() {

			@Override
			public String localImagePath() {
				// 返回图片存储位置
				// TODO
				return "D://text";
			}

			@Override
			public boolean checkContentLength(String html) {
				// 棄1�7验html总长度是否合法：合法返回true/不合法放回false
				// 弄1�7发�1�7�自己实现校验方泄1�7
				// TODO
				// 建议使用此方法过滤多余空栄1�7
				// for (int i = 0; i < buffer.length(); i++) {
				// String temp = new String("" + buffer.charAt(i));
				// if (!(temp.equals(" ") || temp.equals("	"))) {
				//
				// System.out.print(temp);
				// }
				// }
				int length = html.length();
				if (length > 4000) {
					JOptionPane.showMessageDialog(EditorTextTest.this, "字符长度超出范围!");
					return false;
				}

				return true;
			}
		});

		Button butGet = new Button("GetText");
		butGet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println(pane.getText() + "==" + pane.getText().length());
				System.out.println("=============================================");
//				String hValue = transformHtml(pane.getText());
//				hValue = transformHtml(hValue);
//				StringReader sr = new StringReader(hValue);
//				StyleSheet st = new StyleSheet();
//				HashMap<String, Object> mm = new HashMap<String, Object>();
//				try {
//					List list = HTMLWorker.parseToList(sr, st, mm);
//					for(int i =0;i < list.size();i++){
//						Paragraph obj = (Paragraph) list.get(i);
//						List<Chunk> lstChunk = obj.getChunks();
//						for(Chunk objC : lstChunk){
//							boolean flag = objC.getAttributes().containsKey("IMAGE");
//							if(!flag){
//								System.out.println(objC.getContent());
//							}
//						}
//					}
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
			}
		});

		Button butSet = new Button("Reset");
		butSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pane.setText("");
			}
		});

		JPanel butPanle = new JPanel();
		butPanle.add(butGet);
		butPanle.add(butSet);

		this.getContentPane().add(pane, BorderLayout.CENTER);
		this.getContentPane().add(butPanle, BorderLayout.SOUTH);
		this.setVisible(true);
	}
	
	private static String transformHtml(String html) {
		Pattern pattern = Pattern.compile(".*(<img[^>][^/>]*>).*");
		Matcher matcher = pattern.matcher(html);
		while (matcher.find()) {
			String oldimg = matcher.group(1);
			String newimg = oldimg.replaceAll(">", "/>");
			html = html.replaceAll(oldimg, newimg);
		}
		return html;
	}
	
	static String filterUselessTag(String html) {
		StringBuffer buffer = new StringBuffer();
		int start = 0;
		// int end = 0;
		int index = html.indexOf("<");
		if (index < 0) {
			buffer.append(html);
		} else {
			while (start > -1) {
				int temp = html.indexOf("<");
				buffer.append(html.substring(0, temp));
				start = html.indexOf(">", temp);
				html = html.substring(start + 1);

				temp = html.indexOf(">");
				buffer.append(html.substring(0, temp));
				html = html.substring(temp + 1);

				if (html.indexOf("<") < 0) {
					buffer.append(html);
					start = -1;
				}

			}
		}

		return buffer.toString();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			new EditorTextTest();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
