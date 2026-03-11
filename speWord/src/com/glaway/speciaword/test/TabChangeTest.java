/**
 * 2014-6-9
 */
package com.glaway.speciaword.test;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.glaway.speciaword.component.CheckTextContentInterface;
import com.glaway.speciaword.component.EditorPane;

/**
 * @author MosesX
 * 2014-6-9
 */
public class TabChangeTest extends JFrame {

	private static final long serialVersionUID = 1L;

	public TabChangeTest(){
		this.setSize(400, 400);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((screenSize.width - this.getPreferredSize().width) / 2,
				(screenSize.height - this.getPreferredSize().height) / 2);
		this.setTitle("TabChangeTest");
		
		JTabbedPane tabPane = new JTabbedPane();
		final EditorPane pane = new EditorPane("D://text");
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
				int length = html.length();
				if (length > 4000) {
					JOptionPane.showMessageDialog(TabChangeTest.this, "字符长度超出范围!");
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
				
				pane.setText(pane.getText());
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
		
		
		JPanel pp1 = new JPanel();
		JPanel pp2 = new JPanel();
		
		pp1.setLayout(new BorderLayout());
		pp1.add(new JScrollPane(pane),BorderLayout.CENTER);
		pp1.add(butPanle,BorderLayout.SOUTH);
		
		
		tabPane.add("特殊字符",pp1);
		tabPane.add("Others",pp2);
		
		this.getContentPane().add(tabPane, BorderLayout.CENTER);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new TabChangeTest();
	}
}
