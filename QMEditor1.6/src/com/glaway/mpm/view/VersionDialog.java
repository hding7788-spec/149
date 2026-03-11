package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class VersionDialog extends JDialog {

	JButton button=new JButton("确定");
	JPanel dialogPanel=new JPanel();
	JPanel topPanel=new JPanel(){

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Image image;
			try {
				image = ImageIO.read(VersionDialog.class.getResource(("/image/companylogo.png")));
				g.drawImage(image, 0, 0, this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};
	JPanel secondPanel=new JPanel();
	JPanel bottomPanel=new JPanel();
	JTextArea  warnning=new JTextArea ("警告：本计算机程序受版权法及国际条约的保护，未经授权擅自复制、传播、修改本程序的部分或全部，将承受严厉的民事和刑事处罚，对已知的违反者将给予法律范围内的全面制裁。");
	JLabel url=new JLabel();
	JPanel second2Panel=new JPanel();


	public VersionDialog(){

		Properties pros=readProperties("/CopyRight.properties");

		dialogPanel.setLayout(new GridBagLayout());
		GridBagConstraints g=new GridBagConstraints();
		topPanel.setPreferredSize(new Dimension(355,80));
		secondPanel.setPreferredSize(new Dimension(350,120));
		Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        url.setFont(Font.getFont("微软雅黑"));

		url.setText("<html><u><font color='blue'>©"+year+"&nbsp;&nbsp;&nbsp;Glawaysoft&nbsp;&nbsp;&nbsp;Corporation</font></u></html>");
		button.setFont(Font.getFont("微软雅黑"));
		g.gridx=0;
		g.gridy=0;
		g.anchor=GridBagConstraints.NORTH;
		dialogPanel.add(topPanel,g);
		g.gridy=1;
		g.gridheight=2;
		dialogPanel.add(secondPanel,g);
		g.gridy=4;
		g.gridheight=1;
		dialogPanel.add(bottomPanel,g);
		second2Panel.setLayout(new GridBagLayout());
		secondPanel.setLayout(new BorderLayout());
		secondPanel.add(second2Panel,BorderLayout.WEST);
		warnning.setPreferredSize(new Dimension(350,80));
		warnning.setFont(Font.getFont("楷体"));
		warnning.setOpaque(false);
		warnning.setLineWrap(true);
		warnning.setEditable(false);

		Enumeration<?> en = pros.propertyNames();
		int i=-1;
        while (en.hasMoreElements()) {
         String key = (String) en.nextElement();
         String value = pros.getProperty (key);
         if(value!=null&&!"".equals(value)){
        	 i++;
            JLabel label1=new JLabel(key+"：");
   			JLabel label2=new JLabel(value);
   			label1.setFont(Font.getFont("微软雅黑"));
   			label2.setFont(Font.getFont("微软雅黑"));
   			g.gridx=0;
			g.gridy=i;
			g.anchor=GridBagConstraints.WEST;
			second2Panel.add(label1,g);
			g.gridx=1;
			second2Panel.add(label2,g);

         }
           }

		bottomPanel.setLayout(new GridBagLayout());
		g.gridx=0;
		g.gridy=0;
		g.gridwidth=2;
		g.anchor=GridBagConstraints.CENTER;
		bottomPanel.add(warnning,g);
		g.gridx=0;
		g.gridy=1;
		g.gridwidth=1;
		g.insets=new Insets(0, 42, 20, 42);
		bottomPanel.add(url,g);

		g.gridx=1;
		g.gridy=1;
		bottomPanel.add(button,g);
		add(dialogPanel);
		setSize(new Dimension(430, 360));
		setModal(true);
		setResizable(false);
		setTitle("关于工艺规程管理器");
		setLocationRelativeTo(null);

		url.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});

		url.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Runtime.getRuntime().exec("cmd.exe /c start www.glaway.com");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	 public  Properties readProperties(String filePath) {
	     Properties props = new Properties();
	        try {
	        	InputStream inputStream = this.getClass().getResourceAsStream(filePath);
	        	BufferedReader bf = new BufferedReader(new    InputStreamReader(inputStream));
	        	props.load(bf);

	        } catch (Exception e) {
	         e.printStackTrace();
	        }
	        	return props;
	    }

	 public void showDialog(){
		 setVisible(true);
	 }
}
