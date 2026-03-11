package com.glaway.mpm.mesDataSearch.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.dom4j.Document;
import org.dom4j.Element;

import com.glaway.mpm.mesDataSearch.helper.MesDataSearchProcesser;
import com.glaway.mpm.mesParameter.helper.MesParameterProcessor;
import com.glaway.mpm.parameter.commonui.VFlowLayout;
import com.glaway.mpm.parameter.designui.NewCommonParamTablePanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTabbedPanel;
import com.glaway.mpm.parameter.designui.NewSpecialParamTablePanel;
import com.glaway.mpm.parameter.helper.MPMParameterProcessor;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.util.FileChooserTool;
import com.glaway.mpm.util.TechnicsReleaseUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.XWTechnicsTreeObject;
import com.glaway.mpm.visual.log.VaLogger;

public class MesDataSearchMainPanel extends JPanel implements ActionListener{
	/**
	 * 主页面
	 */
	private static final long serialVersionUID = 1L;
	private static VaLogger logger = VaLogger.getLogger(MesDataSearchMainPanel.class);

	private MesDataSearchMainFrame frame;
	private JLabel processNumberLabel;
	private JTextField processNumberField;
	private JLabel productNumberLabel;
	private JTextField productNumberField;
//	private JLabel batchLabel;
//	private JTextField batchField;
//	private JButton searchButton;
//	private JButton exportButtom;
	private JSplitPane topAndBottom;
	private JSplitPane leftAndRight;
	private JPanel topPanel;
	private MesDataMainInfoPanel mainInfoPanel;

	public MesDataSearchMainPanel(MesDataSearchMainFrame frame) {
		this.frame = frame;
		initComponent();
		initLayout();
		initListener();
	}

	private void initComponent(){

		processNumberLabel = new JLabel("过程编号:");
		processNumberField = new JTextField();
		productNumberLabel = new JLabel("产品编号:");
		productNumberField = new JTextField();
//		batchLabel = new JLabel("批次号:");
//		batchField = new JTextField();
//		searchButton = new JButton("查询");
//		exportButtom = new JButton("导出");
//
		processNumberField.setText(frame.getProcessNumber());
		processNumberField.setEditable(false);
		productNumberField.setText(frame.getProductNumber());
		productNumberField.setEditable(false);
//		batchField.setEditable(false);


		topPanel = new JPanel();
		mainInfoPanel = new MesDataMainInfoPanel(frame);
		topPanel.setLayout(new VFlowLayout(0, 0, 0, true, true));

		leftAndRight = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		leftAndRight.setRightComponent(mainInfoPanel);
		leftAndRight.setDividerLocation(300);

		topAndBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		topAndBottom.setTopComponent(topPanel);
		topAndBottom.setBottomComponent(leftAndRight);
		topAndBottom.setDividerLocation(60);

		processNumberField.setPreferredSize(new Dimension(200, 30));
		productNumberField.setPreferredSize(new Dimension(200, 30));
//		batchField.setPreferredSize(new Dimension(120, 30));
//		searchButton.setPreferredSize(new Dimension(100, 30));
//		exportButtom.setPreferredSize(new Dimension(100, 30));

	}
	private void initLayout(){
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		GridBagConstraints g = new GridBagConstraints();

		g.gridy = 0;
		g.gridx = 0;
		g.anchor = GridBagConstraints.EAST;
		g.insets = new Insets(0, 20, 0, 5);
		contentPanel.add(processNumberLabel, g);
		g.gridx = 1;
		g.anchor = GridBagConstraints.WEST;
		g.insets = new Insets(0, 5, 0, 20);
		contentPanel.add(processNumberField, g);
		g.gridx = 2;
		g.insets = new Insets(0, 20, 0, 5);
		g.anchor = GridBagConstraints.EAST;
		contentPanel.add(productNumberLabel, g);
		g.gridx = 3;
		g.insets = new Insets(0, 5, 0, 20);
		g.anchor = GridBagConstraints.WEST;
		contentPanel.add(productNumberField, g);
//		g.gridx = 4;
//		g.insets = new Insets(0, 20, 0, 5);
//		g.anchor = GridBagConstraints.EAST;
//		contentPanel.add(batchLabel, g);
//		g.gridx = 5;
//		g.insets = new Insets(0, 5, 0, 20);
//		g.anchor = GridBagConstraints.WEST;
//		contentPanel.add(batchField, g);
//		g.gridx = 6;
//		g.insets = new Insets(0, 20, 0, 5);
//		g.anchor = GridBagConstraints.EAST;
//		contentPanel.add(searchButton, g);
//		g.gridx = 7;
//		g.insets = new Insets(0, 5, 0, 20);
//		g.anchor = GridBagConstraints.WEST;
//		contentPanel.add(exportButtom, g);

		setLayout(new BorderLayout());
		topPanel.add(contentPanel);
		add(topAndBottom);

	}
	public void initListener(){
//		searchButton.addActionListener(this);
//		exportButtom.addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
//		Object obj = e.getSource();
//		if(obj == searchButton){
//			MesDataTechnicSearchDialog dialog = new MesDataTechnicSearchDialog(frame, this);
//			dialog.showDialog();
//		}
//		if(obj == exportButtom){
//			try {
//				File file = FileChooserTool.getSaveFile("xls", this);
//				if (file != null) {
//					if (file.isFile() && file.exists()) {
//						if (!file.renameTo(file)) {
//							JOptionPane.showMessageDialog(this, "另一个程序正在使用此文件！", "提示", 1);
//							return;
//						}
//					}
//					String path = file.getPath();
//					if (!path.endsWith(".xls")) {
//						path = path.concat(".xls");
//					}
//					MesDataSearchProcesser.exportQualityData(mainInfoPanel, path);
//					JOptionPane.showMessageDialog(this, "模板导出成功！", "提示", 1);
//				}
//			} catch (Exception exc) {
//				exc.printStackTrace();
//				JOptionPane.showMessageDialog(this, "模板导出过程出现错误！", "提示", 1);
//			}
//		}
	}
	public boolean isProcedureExist(Document document, String produreNumber){
		XWTechnicsTreeObject technicTreeObject = new XWTechnicsTreeObject(document);
		List<Element> list = XmlUtility.getAllSteps(technicTreeObject.getTreeCellData());
		for(Element procedureElement : list){
			String procedureNumber = procedureElement.attributeValue("stepNumber");
			if(procedureNumber.equals(produreNumber)){
				return true;
			}
		}
		return false;
	}

	public JPanel getTopPanel() {
		return topPanel;
	}

	public MesDataMainInfoPanel getMainInfoPanel() {
		return mainInfoPanel;
	}
}
