package com.glaway.mpm.mainTest;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;

import wt.method.RemoteMethodServer;

import com.glaway.mpm.qmIntf.common.model.CommonFrame;
import com.glaway.mpm.qmIntf.commonString.CsMaintainDialog;
import com.glaway.mpm.qmIntf.commonString.CsSearchDialog;
import com.glaway.mpm.qmIntf.decoratePView.DecoratePViewDialog;
import com.glaway.mpm.qmIntf.equipment.EquipmentSearchDialog;
import com.glaway.mpm.qmIntf.frock.FrockCardApplyDialog;
import com.glaway.mpm.qmIntf.material.MaterialSearchDialog;
import com.glaway.mpm.qmIntf.symbol.SymbolAddDialog;
import com.glaway.mpm.qmIntf.technics.TechnicsSearchDialog;
import com.glaway.mpm.qmIntf.template.StepTemplateMaintainDialog;
import com.glaway.mpm.qmIntf.template.StepTemplateSearchDialog;
import com.glaway.mpm.qmIntf.template.TemplateMaintainDialog;
import com.glaway.mpm.qmIntf.template.TemplateSearchDialog;
import com.glaway.mpm.qmIntf.viewPanel.CreoModelDialog;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ImageIntf;

/**
 * @author ylshao
 * @ClassName: MainTest
 * @Description: for test
 * @date 2012-11-20
 *
 */
public class MainTest extends CommonFrame {
	private static final long serialVersionUID = 1L;

	private VaLogger logger = VaLogger.getLogger(this.getClass());

	String csPath = "D:\\mpm\\resource\\terminology";
	String xmlPath = "C:\\Users\\ylshao\\Desktop\\三维工艺\\xml\\工艺XML标签及格式定义(最新).xml";
	String templatePath = "D:\\mpm\\templet";
	String stepTemplatePath = "D:\\mpm\\procedureTemlet";
	String desktopPath = "C:\\Users\\ylshao\\Desktop\\";

	private JButton jButton1 = new JButton("常用语维护");
	private JButton jButton2 = new JButton("常用语选择");

	private JButton jButton3 = new JButton("模板库选择");
	private JButton jButton4 = new JButton("模板库维护");
	private JButton jButton5 = new JButton("搜索设备");
	private JButton jButton6 = new JButton("搜索材料");
	private JButton jButton7 = new JButton("搜索工装");
	private JButton jButton8 = new JButton("搜索整件");
	private JButton jButton9 = new JButton("搜索工艺");
	private JButton jButton10 = new JButton("pds中间模型");
	private JButton jButton11 = new JButton("装配预览");
	private JButton jButton12 = new JButton("插入符号");

	private JButton jButton13 = new JButton("工装申请卡");

	private JButton jButton14 = new JButton("工序模板维护");
	private JButton jButton15 = new JButton("工序模板选择");
	private JButton jButton16 = new JButton("工序模板选择");

	private JButton jButton17 = new JButton("获取图档");

	public static void main(String[] args) {
		SwingUtil.setLookAndFeel();
		RemoteMethodServer.getDefault().setUserName("wcadmin");
		RemoteMethodServer.getDefault().setPassword("wcadmin");
		new MainTest();

	}

	public void initAction() {
		//
		jButton1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CsMaintainDialog dialog = new CsMaintainDialog(csPath, null);
				dialog.showDialog();
			}
		});
		//
		jButton2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CsSearchDialog dialog = new CsSearchDialog(csPath, new JFrame());
				logger.debug(dialog.showDialog());
			}
		});
		//

		jButton3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TemplateSearchDialog dialog = new TemplateSearchDialog(
						templatePath, "partTemplate", null);
				Vector<?> vector = dialog.showDialog();
				logger.debug(vector);
			}
		});
		jButton4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TemplateMaintainDialog dialog = new TemplateMaintainDialog(
						templatePath, null);
				dialog.showDialog();
			}
		});
		// 1
		jButton5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EquipmentSearchDialog dialog = new EquipmentSearchDialog(null,
						new JFrame());
				Vector<Map<String, String>> vector = dialog.showDialog();
				logger.debug(vector);
			}
		});
		// 2
		jButton6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MaterialSearchDialog dialog = new MaterialSearchDialog("",
						"22", "33", false, new JFrame(), null);
				Vector<Map<String, String>> vector = dialog.showDialog();
				logger.debug(vector);
			}
		});
		// 3
		jButton7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				FrockSearchDialog dialog = new FrockSearchDialog(new JFrame(), null);
//				Vector<Map<String, String>> vector = dialog.showDialog();
//				logger.debug(vector);
			}
		});
		jButton8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// PartSearchDialog dialog = new PartSearchDialog();
				// byte[] bytes = dialog.showDialog();
				// logger.debug(bytes);
			}
		});
		jButton9.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TechnicsSearchDialog dialog = new TechnicsSearchDialog(null);
				dialog.showDialog();
			}
		});
		jButton10.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("oid", "608187");
				map.put("partNumber", "AL2_907_1460");
				// map.put("oid", "663489");//没数据
				// map.put("partNumber", "AL8_034_5084_QIAN");

				map.put("filePath",
						"D:\\mpm\\technics\\AL2_907_1460`副天线和差选束开关装配工艺`装配工艺`AL2_907_1460`多基地面雷达\\AL2_907_1460`副天线和差选束开关装配工艺`装配工艺`AL2_907_1460`多基地面雷达.xml");
				CreoModelDialog dialog = new CreoModelDialog(map, null);
				dialog.showDialog();
			}
		});
		jButton11.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("oid", "584836");
				map.put("partNumber", "AL2_907_1460");
				map.put("technicsPath", "AL2_907_1460");
				map.put("xmlPath", xmlPath);
				map.put("stepNumber", "工序号2");
				map.put("paceNumber", "工步号bbb");
				map.put("procedureContent", "工步内容。。。");
				DecoratePViewDialog dialog = new DecoratePViewDialog(map);
				dialog.showDialog();
			}
		});
		jButton12.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				SymbolAddDialog dialog = new SymbolAddDialog(null);
				logger.debug(dialog.showDialog());

			}
		});
		jButton13.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				FrockCardApplyDialog dialog = new FrockCardApplyDialog(null,
						null);
				dialog.showDialog();
			}
		});
		jButton14.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StepTemplateMaintainDialog dialog = new StepTemplateMaintainDialog(
						stepTemplatePath, null);
				dialog.showDialog();
			}
		});
		jButton15.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StepTemplateSearchDialog dialog = new StepTemplateSearchDialog(
						stepTemplatePath, "partTemplate", null);
				Vector<?> vector = dialog.showDialog();
				if (vector != null && vector.size() == 2) {
					FileUtil.writeBytes("d://a.zip", (byte[]) vector.get(1));
				}
			}
		});
		jButton16.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StepTemplateSearchDialog dialog = new StepTemplateSearchDialog(
						stepTemplatePath, "assembleTemplate", null);
				Vector<?> vector = dialog.showDialog();
				if (vector != null && vector.size() == 2) {
					FileUtil.writeBytes("d://a.zip", (byte[]) vector.get(1));
				}
			}
		});
		jButton17.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String url = ImageIntf.getCreoViewUrl("584836");
				CommonUtil.openURL(url);
			}
		});
	}

	public MainTest() {
		this.setLayout(new GridLayout(3, 3));
		this.add(jButton1);
		this.add(jButton2);
		this.add(jButton3);
		this.add(jButton4);
		this.add(jButton5);
		this.add(jButton6);
		this.add(jButton7);
		this.add(jButton8);
		this.add(jButton9);
		this.add(jButton10);
		this.add(jButton11);
		this.add(jButton12);
		this.add(jButton13);
		this.add(jButton14);
		this.add(jButton15);
		this.add(jButton16);
		this.add(jButton17);

		initAction();
		this.setVisible(true);
		this.setSize(800, 700);
		this.setLocation(500, 200);
	}
}