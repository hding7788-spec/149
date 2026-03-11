package com.glaway.mpm.util;

import com.glaway.mpm.consCheck.ConsCheckRecordsMainFrame;
import com.glaway.mpm.dataPackage.ui.DPMesParameterMainFrame;
import com.glaway.mpm.mesDataSearch.ui.MesDataSearchMainFrame;
import com.glaway.mpm.mesParameter.ui.MesParameterMainFrame;
import com.glaway.mpm.parameter.ui.MPMParameterMainFrame;
import com.glaway.mpm.pbombuilder.bom.CMGenPbomConnectFrame;
import com.glaway.mpm.pbombuilder.bom.CmConnectDialog;
import com.glaway.mpm.pbombuilder.bom.CmConnectFrame;
import com.glaway.mpm.qmIntf.technics.RelatedTypicalTechnicsMainFrame;
import com.glaway.mpm.view.NewTechnicsPart;
import wt.util.WTContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class MPMConnectFrame extends JFrame implements ActionListener {
	private static final String CLASSNAME = MPMConnectFrame.class.getName();
	private static final long serialVersionUID = -8390305415689370432L;
	JTextField textUser = null;
	JPasswordField textPwd = null;
	JButton btnClose = null;
	JButton btnConfirm = null;

	public MPMConnectFrame() {
		super("登录");
		System.out.println(CLASSNAME+"-------------------------MPMConnectFrame---------------------login---------");
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int width = 300;
		int height = 160;
		int left = (screen.width - width) / 2;
		int top = (screen.height - height) / 2;
		setBounds(left, top, width, height);
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Color myColor = new Color(220, 220, 220);
		getContentPane().setBackground(myColor);
		JLabel lblUser = new JLabel("用户名称", JLabel.RIGHT);
		lblUser.setBounds(0, 15, 100, 20);
		getContentPane().add(lblUser);

		textUser = new JTextField("");
		textUser.setBounds(120, 15, 100, 30);
		getContentPane().add(textUser);

		JLabel lblPwd = new JLabel("用户密码", JLabel.RIGHT);
		lblPwd.setBounds(0, 50, 100, 20);
		getContentPane().add(lblPwd);

		textPwd = new JPasswordField("");
		textPwd.setBounds(120, 50, 100, 30);
		textPwd.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnConfirm.requestFocusInWindow();
					btnConfirm.doClick();
				}
			}
		});
		getContentPane().add(textPwd);

		// ---- Buttons OK and CLose
		btnConfirm = new JButton("确定");
		btnConfirm.setBounds(60, 90, 80, 30);
		btnConfirm.setEnabled(true);
		btnConfirm.setPreferredSize(new Dimension(100, 26));
		getContentPane().add(btnConfirm);
		btnConfirm.addActionListener(this);

		btnClose = new JButton("取消");
		btnClose.setBounds(160, 90, 80, 30);
		btnClose.setPreferredSize(new Dimension(100, 26));
		getContentPane().add(btnClose);
		btnClose.addActionListener(this);

		setResizable(false);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClose) {
			System.exit(0);
		} else if (e.getSource() == btnConfirm) {
			String userName = textUser.getText();
			String password = new String(textPwd.getPassword());

			try {
				MPMContext.logonToServer(userName, password);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			if (!MPMContext.getConnected())
				JOptionPane.showMessageDialog(this, "用户名称或密码错误", "登录异常", JOptionPane.WARNING_MESSAGE);
			else {
				this.dispose();
				if (MPMContext.isJWS()) {
					startApp();
				} else {
					MPMConnectFrame dc = new MPMConnectFrame();
					dc.setVisible(true);
				}
			}
		}
	}

	public static void main(String[] args) {
		WTContext wtcontext = WTContext.getContext();
		WTContext.init(args);
		MPMContext.setJWS((WTContext.getContext().getParameter("mainClass") != null));
		MPMContext.setApplication(wtcontext.getParameter("application"));
		MPMContext.setTaskType(wtcontext.getParameter("taskType"));
		MPMContext.setParam1(wtcontext.getParameter("param1"));
		MPMContext.setParam2(wtcontext.getParameter("param2"));
		MPMContext.setParam3(wtcontext.getParameter("param3"));
		MPMContext.setParam4(wtcontext.getParameter("param4"));
		MPMContext.setParam5(wtcontext.getParameter("param5"));
		MPMContext.setParam6(wtcontext.getParameter("param6"));
		MPMContext.setParam7(wtcontext.getParameter("param7"));
		MPMContext.setParam8(wtcontext.getParameter("param8"));
		MPMContext.setParam9(wtcontext.getParameter("param9"));
		MPMContext.setParam10(wtcontext.getParameter("param10"));
		if (MPMContext.isJWS()) {
			System.out.println(CLASSNAME+"----MPMContext.isJWS()----yes");
			MPMContext.setJWS(true);
			String auth = wtcontext.getParameter("authorization");
			if (auth != null && auth.startsWith("Basic ")) {
				MPMContext.logonToServer(auth.substring(6));
			}
		} else {
			System.out.println(CLASSNAME+"----MPMContext.isJWS()----no");
			MPMContext.setUser(wtcontext.getParameter("user"));
			MPMContext.setPassword(wtcontext.getParameter("pwd"));
			if (MPMContext.getUser() != null && MPMContext.getPassword() != null) {
				try {
					MPMContext.logonToServer();
				} catch (Exception e) {
					System.out.println(CLASSNAME+"----e----"+e.getLocalizedMessage());
				}
			}
		}
		System.out.println(CLASSNAME+"----MPMContext.getConnected()----"+MPMContext.getConnected());
		if (MPMContext.getConnected()) {
			startApp();
		} else {
			String application = MPMContext.getApplication();
			 if("MESParameter".equals(application)
					 || "DataPackage".equals(application)
					 || "MesDataSearch".equals(application)
					 || "RelateTypecialProcess".equals(application)){
				 startApp();
			 }else{
				 MPMConnectFrame dc = new MPMConnectFrame();
				 dc.setVisible(true);
			 }

		}
	}

	private static void startApp() {
		GLLogger.debug(CLASSNAME, "application--" + MPMContext.getApplication() + "--taskType--"
				+ MPMContext.getTaskType() + "--Param1--" + MPMContext.getParam1() + "--Param2--"
				+ MPMContext.getParam2() + "--Param3--" + MPMContext.getParam3() + "--Param4--"
				+ MPMContext.getParam4());
		String application = MPMContext.getApplication();

		if ("PBOMEditor".equals(application)) {
			String[] args = { MPMContext.getParam1(), MPMContext.getParam2() };
			CmConnectFrame.main(args);
		} else if ("EditPBOM".equals(application)) {
			String[] args = { MPMContext.getParam1(), MPMContext.getParam2() };
			CmConnectDialog.main(args);
		}else if("GENPBOM".equals(application)){
			String[] args = { MPMContext.getParam1(), MPMContext.getParam2() , MPMContext.getParam3() };
			CMGenPbomConnectFrame.main(args);
		}
		else if ("ProcessEditor".equals(application)) {
			String[] args = { MPMContext.getTaskType(), MPMContext.getParam1(), MPMContext.getParam2(),
					MPMContext.getParam3(), MPMContext.getParam4(), MPMContext.getParam5(), MPMContext.getParam6(), MPMContext.getParam7(), MPMContext.getParam8(),MPMContext.getParam9(),MPMContext.getParam10() };
			try {
				NewTechnicsPart.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if ("MPMParameter".equals(application)) {
			String[] args = { };
			try {
				MPMParameterMainFrame.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if("MESParameter".equals(application)){
			String[] args = { MPMContext.getParam1(),MPMContext.getParam2(),MPMContext.getParam3(),MPMContext.getParam4(),MPMContext.getParam5(),MPMContext.getParam6(),MPMContext.getParam7(),MPMContext.getParam8() };
			try {
				MesParameterMainFrame.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if("DataPackage".equals(application)){
			String[] args = { MPMContext.getParam1() };
			try {
				DPMesParameterMainFrame.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if("MesDataSearch".equals(application)){
			String[] args = { MPMContext.getParam1(),MPMContext.getParam2() };
			try {
				MesDataSearchMainFrame.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if ("RelateTypecialProcess".equals(application)) {
			String[] args = { MPMContext.getParam1(),MPMContext.getParam2() };
			try {
				RelatedTypicalTechnicsMainFrame.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if ("ConsCheck".equals(application)) {
			String[] args = { MPMContext.getTaskType()};
			try {
				ConsCheckRecordsMainFrame.main(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}