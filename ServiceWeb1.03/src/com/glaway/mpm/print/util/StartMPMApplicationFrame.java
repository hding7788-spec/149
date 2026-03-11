package com.glaway.mpm.print.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import wt.util.WTContext;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.print.ui.MPMPrintFileFrame;
import com.glaway.mpm.util.MPMContext;

public class StartMPMApplicationFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = -8390305415689370432L;
	private static VaLogger logger = VaLogger.getLogger(StartMPMApplicationFrame.class.getName());
	private JTextField textUser = null;
	private JPasswordField textPwd = null;
	private JButton btnClose = null;
	private JButton btnConfirm = null;

	public StartMPMApplicationFrame() {
		super("登录");
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
		JLabel lblUser = new JLabel("用户名称", SwingConstants.RIGHT);
		lblUser.setBounds(0, 15, 100, 20);
		getContentPane().add(lblUser);

		textUser = new JTextField("");
		textUser.setBounds(120, 15, 100, 30);
		getContentPane().add(textUser);

		JLabel lblPwd = new JLabel("用户密码", SwingConstants.RIGHT);
		lblPwd.setBounds(0, 50, 100, 20);
		getContentPane().add(lblPwd);

		textPwd = new JPasswordField("");
		textPwd.setBounds(120, 50, 100, 30);
		textPwd.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClose) {
			System.exit(0);
		} else if (e.getSource() == btnConfirm) {
			String userName = textUser.getText();
			String password = new String(textPwd.getPassword());

			try {
				MPMContext.logonToServer(userName, password);
			} catch (Exception exc) {
				logger.error(exc);
			}
			if (!MPMContext.getConnected())
				JOptionPane.showMessageDialog(this, "用户名称或密码错误", "登录异常", JOptionPane.WARNING_MESSAGE);
			else {
				this.dispose();
				if (MPMContext.isJWS()) {
					startApp();
				} else {
					StartMPMApplicationFrame dc = new StartMPMApplicationFrame();
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
		MPMContext.setTaskType((wtcontext.getParameter("type")));
		MPMContext.setParam1(wtcontext.getParameter("oid"));
		MPMContext.setParam2(wtcontext.getParameter("showTree"));
		MPMContext.setParam3(wtcontext.getParameter("operationOid"));
		MPMContext.setParam4(wtcontext.getParameter("remark"));
		MPMContext.setParam5(wtcontext.getParameter("infor"));
		MPMContext.setParam6(wtcontext.getParameter("pboOid"));
		MPMContext.setParam7(wtcontext.getParameter("isFromRecover"));
		MPMContext.setParam8(wtcontext.getParameter("isFromStore"));
		MPMContext.setParam9(wtcontext.getParameter("dept"));
		MPMContext.setParam10(wtcontext.getParameter("wfaOid"));
		if (MPMContext.isJWS()) {
			MPMContext.setJWS(true);
			String auth = wtcontext.getParameter("authorization");
			if (auth != null && auth.startsWith("Basic ")) {
				MPMContext.logonToServer(auth.substring(6));
			}
		} else {
		MPMContext.setUser(wtcontext.getParameter("user"));
		MPMContext.setPassword(wtcontext.getParameter("pwd"));
		if (MPMContext.getUser() != null && MPMContext.getPassword() != null) {
			try {
				MPMContext.logonToServer();
			} catch (Exception e) {
				logger.error(e);
			}
		}
		}
		if (MPMContext.getConnected()) {
			startApp();
		} else {
			StartMPMApplicationFrame dc = new StartMPMApplicationFrame();
			dc.setVisible(true);
		}
	}

	private static void startApp() {
		String application = MPMContext.getApplication();
		if ("MPMPrint".equals(application)) {
			String[] args = { MPMContext.getParam1(), MPMContext.getTaskType(), MPMContext.getParam5(), MPMContext.getParam6(), MPMContext.getParam7(), MPMContext.getParam8(),MPMContext.getParam9(),MPMContext.getParam10()};
			try {
				MPMPrintFileFrame.main(args);
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
}