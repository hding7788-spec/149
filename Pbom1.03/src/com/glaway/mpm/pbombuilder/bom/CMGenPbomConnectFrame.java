package com.glaway.mpm.pbombuilder.bom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.tree.dialog.GenPbomDialog;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

public class CMGenPbomConnectFrame extends JFrame implements ActionListener {


	private static final long serialVersionUID = 1L;
	private static final CmLogger log = CmLogger.getLogger(CMGenPbomConnectFrame.class.getName());

	public static String docId;
	public static String containerId;
	public static String from="";//启动来源：windchill -WC,工艺编辑器-PE
	public static void main(String[] args) {
		try {
			LookUtils.setLookAndTheme(new Plastic3DLookAndFeel(), new CmTheme());
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		if(args==null){
			docId = "486334";
			containerId = "74524";
		}
		if(args!=null&&args.length>=2){
			String tdocId =  args[0];
			if("null".equals(tdocId)||"".equals(tdocId)){
				docId = null;
			}else{
				docId = tdocId;
			}
			containerId = args[2];
		}
		log.info("docId:"+docId  +"  containerId: "+containerId);



		new CMGenPbomConnectFrame();
	}



	public CMGenPbomConnectFrame() {
		super("登录");
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
//		rms.setUserName("wcadmin");
//		rms.setPassword("wcadmin");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		startApplication(this);
	}

	public void actionPerformed(ActionEvent e) {

	}

	/**
	 * 根据application显示正确的jws
	 *
	 * @param application
	 */
	private static void startApplication(CMGenPbomConnectFrame owner) {
		GenPbomDialog dialog = new GenPbomDialog(owner,"生成PBOM");
	}

}
