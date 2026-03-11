package com.glaway.mpm.print.ui;


import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmPrintRecordInfoBean;
import com.glaway.mpm.print.data.CmPrintRecordQueryBean;
import com.glaway.mpm.print.helper.MPMPrintHelper;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.util.FilePrintUtil;
import com.glaway.mpm.print.util.LoadPrintConfigurations;
import com.glaway.mpm.print.util.LocalPrintUtil;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.DateChooser;
import com.glaway.mpm.util.DateUtil;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
/**
 *
 * @author lkc
 *
 */
public class ReprintMainPanel extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = -844703563059372197L;

	/** 文件编号 */
	private JLabel fileNumberLabel;
	/** 文件名称 */
	private JLabel fileNameLabel;
	/** 文件类型 */
	private JLabel fileTypeLabel;
	/** 文件版本*/
	private JLabel versionLabel;
	/** 阶段标记*/
	private JLabel phaseCodeLabel;
	/** 打印日期 */
	private JCheckBox printDateCheck;
	/** 打印开始日期 */
	private JLabel printStartDateLabel;
	/** 打印结束日期 */
	private JLabel printEndDateLabel;

	/** 文件编号--文本框 */
	private JTextField fileNumberValue;
	/** 文件名称--文本框 */
	private JTextField fileNameValue;
	/** 文件类型--下拉框 */
	private JComboBox fileTypeValue;
	/** 文件版本--文本框 */
	private JTextField versionValue;
	/** 阶段标记--下拉框 */
	private JComboBox phaseCodeValue;
	/** 打印开始日期 */
	private JTextField printStartDateValue;
	/** 打印结束日期 */
	private JTextField printEndDateValue;

	/** 清空搜索条件 */
	private JButton clearConditionButton;
	/** 查询 */
	private JButton searchButton;
	//重新打印
	private JButton reprintButton;
	//关闭
	private JButton closeButton;
	private JCheckBox allCheck;
	private JTable table;
	private DefaultTableModel tableModel;
	private JPanel topPanel;
	private JScrollPane tablePanel;
	private JPanel centerPanel;
	private List<CmPrintRecordInfoBean> list;

	public ReprintMainPanel() {
		initComponents();
		initLayout();
		initListener(this);
		initUI();
	}
	public void initComponents(){
		topPanel = new JPanel();
		tablePanel = new JScrollPane();
		centerPanel = new JPanel();
		list = new ArrayList<CmPrintRecordInfoBean>();
		String[] fileTypeSeal = LoadPrintConfigurations.getInstance().getPrintFileTypeValue();
		String[] phaseCode = LoadPrintConfigurations.getInstance().getPhaseCodeValue();

		fileNameLabel = new JLabel("文件名称 :");
		fileNumberLabel = new JLabel("文件编号 :");
		fileTypeLabel = new JLabel("文件类型 :");
		versionLabel = new JLabel("版本 :");
		phaseCodeLabel = new JLabel("阶段标记 :");
		printDateCheck = new JCheckBox("打印日期:");
		printDateCheck.setSelected(true);
		printStartDateLabel = new JLabel("开始日期:");
		printEndDateLabel = new JLabel("结束日期:");

		fileNumberValue = new JTextField();
		fileNameValue = new JTextField();
		fileTypeValue = new JComboBox(fileTypeSeal);
		versionValue = new JTextField();
		phaseCodeValue = new JComboBox(phaseCode);
		printStartDateValue = new JTextField("1970/01/01");
		printEndDateValue = new JTextField(DateUtil.getTodayDate());

		clearConditionButton = new JButton("清空搜索条件");
		searchButton = new JButton("查询");
		reprintButton = new JButton("重新打印");
		closeButton = new JButton("关闭");
		allCheck = new JCheckBox();
		allCheck.setText("全选");

		DateChooser printStartChooser = DateChooser.getInstance("yyyy/MM/dd");
		DateChooser printEndChooser = DateChooser.getInstance("yyyy/MM/dd");
		printStartChooser.register(printStartDateValue);
		printEndChooser.register(printEndDateValue);

		fileNumberValue.setPreferredSize(new Dimension(100, 25));
        fileNameValue.setPreferredSize(new Dimension(100, 25));
        fileTypeValue.setPreferredSize(new Dimension(100, 25));
        phaseCodeValue.setPreferredSize(new Dimension(100, 25));
        versionValue.setPreferredSize(new Dimension(100, 25));

        printStartDateValue.setPreferredSize(new Dimension(120, 25));
		printEndDateValue.setPreferredSize(new Dimension(120, 25));

        searchButton.setPreferredSize(new Dimension(100, 25));
        reprintButton.setPreferredSize(new Dimension(100, 25));
        closeButton.setPreferredSize(new Dimension(100, 25));

        String[] tableColumn = {"", "oid", "批次", "条码", "文件编号", "文件名称", "文件类型", "版本", "阶段标记", "密级", "打印时间", "打印人", "分发部门","领取时间", "领取人", "状态"};
		String[][] tableData = {};
		tableModel = new DefaultTableModel(tableData,tableColumn)
		{
			private static final long serialVersionUID = -5817421228650378228L;

			public boolean isCellEditable(int row, int column) {
				if(column == 0){
					return true;
				}else{
					return false;
				}
			}

		};
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 20));
		table.setRowHeight(30);
		table.getColumnModel().getColumn(0).setCellEditor(table.getDefaultEditor(Boolean.class));
		table.getColumnModel().getColumn(0).setCellRenderer(table.getDefaultRenderer(Boolean.class));
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		table.getColumnModel().getColumn(1).setMaxWidth(0);
		table.getColumnModel().getColumn(1).setMinWidth(0);
		table.getColumnModel().getColumn(1).setPreferredWidth(0);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setMaxWidth(0);
		table.getColumnModel().getColumn(2).setMinWidth(0);
		table.getColumnModel().getColumn(2).setPreferredWidth(0);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(3).setPreferredWidth(140);
		table.getColumnModel().getColumn(4).setPreferredWidth(140);
		table.getColumnModel().getColumn(5).setPreferredWidth(180);
		table.getColumnModel().getColumn(6).setPreferredWidth(60);
		table.getColumnModel().getColumn(7).setPreferredWidth(60);
		table.getColumnModel().getColumn(8).setPreferredWidth(60);
		table.getColumnModel().getColumn(9).setPreferredWidth(50);
		table.getColumnModel().getColumn(10).setPreferredWidth(80);
		table.getColumnModel().getColumn(11).setPreferredWidth(60);
		table.getColumnModel().getColumn(12).setPreferredWidth(60);
		table.getColumnModel().getColumn(13).setPreferredWidth(80);
		table.getColumnModel().getColumn(14).setPreferredWidth(60);
		table.getColumnModel().getColumn(15).setPreferredWidth(60);
		DefaultTableCellRenderer render = (DefaultTableCellRenderer) table.getDefaultRenderer(getClass());
		render.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		tablePanel.setViewportView(table);
	}

	public void initLayout(){
		topPanel.setLayout(new GridBagLayout());
		GridBagConstraints topGrid = new GridBagConstraints();

		topGrid.gridx = 0;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 20, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topPanel.add(fileNumberLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(versionLabel, topGrid);
		topGrid.gridy = 4;
		topPanel.add(printDateCheck, topGrid);

		topGrid.gridx = 1;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 5);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(fileNumberValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(versionValue, topGrid);
		topGrid.gridy = 4;
		topGrid.gridwidth = 2;
		JPanel printStartDate = new JPanel();
		printStartDate.add(printStartDateLabel);
		printStartDate.add(printStartDateValue);
		topPanel.add(printStartDate, topGrid);


		topGrid.gridx = 3;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.gridwidth = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topPanel.add(fileNameLabel, topGrid);
		topGrid.gridy = 2;
		topPanel.add(phaseCodeLabel, topGrid);

		topGrid.gridx = 4;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 5);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(fileNameValue, topGrid);
		topGrid.gridy = 2;
		topPanel.add(phaseCodeValue, topGrid);
		topGrid.gridy = 4;
		topGrid.insets = new Insets(10, -50, 10, 5);
		JPanel printEndDate = new JPanel();
		printEndDate.add(printEndDateLabel);
		printEndDate.add(printEndDateValue);
		topPanel.add(printEndDate, topGrid);

		topGrid.gridx = 6;
		topGrid.gridy = 0;
		topGrid.gridwidth = 1;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHEAST;
		topPanel.add(fileTypeLabel, topGrid);

		topGrid.gridx = 7;
		topGrid.gridy = 0;
		topGrid.weightx = 1;
		topGrid.weighty = 1;
		topGrid.insets = new Insets(10, 10, 10, 10);
		topGrid.anchor = GridBagConstraints.NORTHWEST;
		topPanel.add(fileTypeValue, topGrid);

		topGrid.gridx = 8;
		topGrid.gridy = 2;
		topGrid.insets = new Insets(10, 0, 10, 2);
		topPanel.add(clearConditionButton, topGrid);

		topGrid.gridx = 9;
		topGrid.gridy = 2;
		topGrid.insets = new Insets(10, 2, 10, 2);
		topPanel.add(searchButton, topGrid);

		centerPanel.setLayout(new GridBagLayout());
		GridBagConstraints centerGrid = new GridBagConstraints();

		centerGrid.gridx = 0;
		centerGrid.gridy = 0;
		centerGrid.gridwidth = 1;
		centerGrid.gridheight = 1;
		centerGrid.anchor = GridBagConstraints.NORTHWEST;
		centerGrid.insets = new Insets(10, 10, 10, 0);
		centerPanel.add(reprintButton, centerGrid);

		centerGrid.gridy = 1;
		centerGrid.gridwidth = 11;
		centerGrid.gridheight = 1;
		centerGrid.weightx = 1;
		centerGrid.weighty = 1;
		centerGrid.fill = GridBagConstraints.BOTH;
		centerPanel.add(tablePanel, centerGrid);

		centerGrid.gridx = 0;
		centerGrid.gridy = 2;
		centerGrid.gridwidth = 1;
		centerGrid.gridheight = 1;
		centerGrid.fill = GridBagConstraints.NORTHWEST;
		centerPanel.add(allCheck, centerGrid);

		centerGrid.gridx = 9;
		centerGrid.gridy = 2;
		centerGrid.gridwidth = 1;
		centerGrid.gridheight = 1;
		centerGrid.weightx = 0;
		centerGrid.weighty = 0;
		centerGrid.anchor = GridBagConstraints.NORTHEAST;
		centerGrid.fill = GridBagConstraints.NORTHEAST;
		centerPanel.add(closeButton, centerGrid);

	    this.add(topPanel);
        this.add(centerPanel);
		SpringLayout springLayout = new SpringLayout();
        springLayout.putConstraint(SpringLayout.NORTH, topPanel, 0, SpringLayout.NORTH, this.getContentPane());
        springLayout.putConstraint(SpringLayout.WEST, topPanel, 0, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, topPanel, 0, SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.NORTH, centerPanel, 0, SpringLayout.SOUTH, topPanel);
        springLayout.putConstraint(SpringLayout.WEST, centerPanel, 0, SpringLayout.WEST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.EAST, centerPanel, 0, SpringLayout.EAST, this.getContentPane());
        springLayout.putConstraint(SpringLayout.SOUTH, centerPanel, 0, SpringLayout.SOUTH, this.getContentPane());
        this.setLayout(springLayout);
	}

	public void initListener(final JFrame frame){
		clearConditionButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				clearCondition();
			}
		});

		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				tableModel.setRowCount(0);
				final CmPrintRecordQueryBean cmPrintRecordQueryBean = getConditionValues();
				//对version进行校验
				boolean versionFormat = MPMPrintHelper.verifyVersionDataFormat(cmPrintRecordQueryBean.getVersion());
				if(!versionFormat){
					CommonUIUtil.showMessageDialog(null, "版本填写格式错误，请重新填写！");
					return;
				}
				final VaActionProgressBar progressBar = new VaActionProgressBar(
						frame, "搜索", "正在搜索,请等待...", "搜索中");
		        Thread thread = new Thread() {
		        	public void run(){
		        		List<String> listVR = new ArrayList<String>();
						try {
							list = getQueryReprintInfo(cmPrintRecordQueryBean);
							for (CmPrintRecordInfoBean cmPrintRecordInfoBean1 : list) {
								String docVR = cmPrintRecordInfoBean1.getDocVR();
								String batch = cmPrintRecordInfoBean1.getBatch();
								String barCode = cmPrintRecordInfoBean1.getBarCode();
								String fileNumber = cmPrintRecordInfoBean1.getFileNumber();
								String fileName = cmPrintRecordInfoBean1.getFileName();
								String fileType = cmPrintRecordInfoBean1.getFileType();
								String docVersion = cmPrintRecordInfoBean1.getDocVersion();
								String phaseCode = cmPrintRecordInfoBean1.getPhaseCode();
								String secret  = cmPrintRecordInfoBean1.getSecret();
								String printDate = cmPrintRecordInfoBean1.getPrintDate();
								String printUser = cmPrintRecordInfoBean1.getPrintUser();
								String getDept = cmPrintRecordInfoBean1.getGetDept();
								String getDate = cmPrintRecordInfoBean1.getGetDate();
								String getUser = cmPrintRecordInfoBean1.getGetUser();
								String state = cmPrintRecordInfoBean1.getFileState();
								String[] value = {Boolean(false), docVR, batch, barCode, fileNumber, fileName, fileType, docVersion, phaseCode, secret, printDate, printUser, getDept, getDate, getUser, state};
								tableModel.addRow(value);
								listVR.add(docVR);
							}
						} catch (RemoteException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						}
						progressBar.finish();
		                progressBar.setVisible(false);
		        	}
		        };
		        thread.start();
		        progressBar.setVisible(true);
			}
		});

		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = e.getActionCommand();
				if(str == "关闭"){
				   int result = JOptionPane.showConfirmDialog(getContentPane(),"是否关闭当前页面？","提示",JOptionPane.YES_NO_OPTION);
				   if(result == JOptionPane.YES_OPTION){
					   System.exit(0);
				   }
				}
			}
		});

		this.addWindowListener(new WindowAdapter() {
			   public void windowClosing(WindowEvent e) {
			   int close = JOptionPane.showConfirmDialog(getContentPane(), "是否关闭当前页面？", "提示",JOptionPane.YES_NO_OPTION);
			   if (close == JOptionPane.YES_OPTION) {
			     System.exit(0);
			    }else{
					   setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
				   }
			 }
		});
		allCheck.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if(allCheck.isSelected()){
					for(int i=0;i<table.getRowCount();i++){
						table.getModel().setValueAt(true, i, 0);
					}
				}else{
					for(int i=0;i<table.getRowCount();i++){
						table.getModel().setValueAt(false, i, 0);
					}
				}
			}
		});
		reprintButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<CmPrintRecordInfoBean> listBean = new ArrayList<CmPrintRecordInfoBean>();
				Map<String,String> map = new HashMap<String,String>();
				Map<String,String> deptmap = new HashMap<String,String>();
				List<String> list = new ArrayList<String>();
				List<CmAttachment> attachList = null;
				int tableRow = tableModel.getRowCount();
				for(int i = 0; i < tableRow; i ++){
					if("true".equals(String.valueOf((tableModel.getValueAt(i, 0))))){
						String QRName = CommonUtil.objectToString(table.getModel().getValueAt(i, 3));
						if(QRName.contains("/")){
							QRName = QRName.replace("/", "~");
						}
						String oid = CommonUtil.objectToString(table.getModel().getValueAt(i, 1));
						String batch = CommonUtil.objectToString(table.getModel().getValueAt(i, 2));
						String dept = CommonUtil.objectToString(table.getModel().getValueAt(i, 12));
						String id = oid + "|" + QRName;
						list.add(id);
						map.put(id, batch);
						deptmap.put(id, dept);
						CmPrintRecordInfoBean cmPrintRecordInfoBean = new CmPrintRecordInfoBean();
						cmPrintRecordInfoBean.setDocVR(oid);
						cmPrintRecordInfoBean.setFileNumber(CommonUtil.objectToString(table.getModel().getValueAt(i, 4)));
						if(QRName.contains("~")){
							QRName = QRName.replace("~", "/");
						}
						cmPrintRecordInfoBean.setBarCode(QRName);
						cmPrintRecordInfoBean.setGetUser(CommonUtil.objectToString(table.getModel().getValueAt(i, 14)));
						listBean.add(cmPrintRecordInfoBean);
					}
				}
				if(listBean != null && !listBean.isEmpty()){
					try {
						attachList = PrintToWCIntf.getPdfByOid(list);
						if(attachList == null || attachList.isEmpty()){
							return;
						}
						String pdfTempPath = FileUtil.makeTmpDir(PrintConstants.FOLDER_PDFTEMP);
//						StringBuffer buf = new StringBuffer();
						List<String> pdfList = new ArrayList<String>();
						//获取受控章
						byte[] sealByte = MPMPrintProcessor.getImageByte("shoukong.jpg");
						String sealPath = pdfTempPath + File.separator + "shoukong.jpg";//受控章
						FileUtil.writeBytes(sealPath, sealByte);
						int countPages = 0;
						for (CmAttachment attachment : attachList) {
							String fileName = attachment.getFileName();
							String filePath = pdfTempPath + File.separator + fileName;//pdf路径
							String fileType = attachment.getTemplateType();
							String QRName = fileName.substring(0, fileName.indexOf("_"));
							if(QRName.contains("/")){
								QRName = QRName.replace("/", "~");
							}
							String oid = attachment.getNumber();
							String batch = map.get(oid + "|" + QRName);
							String dept = deptmap.get(oid + "|" + QRName);
							FileUtil.writeBytes(filePath, attachment.getBytes());
							byte[] imgs = MPMPrintProcessor.getImageByte(QRName);
							String Qr = pdfTempPath + File.separator + QRName + ".jpg";
							FileUtil.writeBytes(Qr, imgs);

							int countPage = LocalPrintUtil.generatePDF(filePath, Qr, sealPath, batch,fileType,QRName,dept);
							countPages = countPages+countPage;
							pdfList.add(filePath);
//							try {
//								Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler  " + filePath);
//							} catch (Exception e1) {
//								buf.append(attachment.getFileName() + ",");
//							}
						}
//						if (buf.toString().length() > 1) {
//							CommonUIUtil.showMessageDialog(null, "打开" + buf.toString().substring(0, buf.toString().length() - 1) + "文件出错！");
//						}
						if(pdfList.size()>1&&countPages>500){
							CommonUIUtil.showMessageDialog(null, "打印失败！"+"\n"+"原因：所选择打印文件的总页数超过500页，不允许打印，请重新选择后再执行打印！");
							return;
						}
						try {
							//打印
							String mergePdfFilePath = FilePrintUtil.mergePdf(pdfList);
							FilePrintUtil.print(mergePdfFilePath);
							String time = getCurrentTime();
							updateReprintInfo(listBean, time);
							CommonUIUtil.showMessageDialog(null, "打印成功！");
						} catch (IOException e1) {
							CommonUIUtil.showMessageDialog(null, "打印失败！");
							e1.printStackTrace();
						} catch (PrinterException e1) {
							CommonUIUtil.showMessageDialog(null, "打印失败！");
							e1.printStackTrace();
						}catch (Exception e1) {
							CommonUIUtil.showMessageDialog(null, "打印失败！");
							e1.printStackTrace();
						}
					} catch (RemoteException e2) {
						e2.printStackTrace();
					} catch (InvocationTargetException e2) {
						e2.printStackTrace();
					}
				}else{
					JOptionPane.showMessageDialog(getContentPane(), "未选择需要重新打印的文件！");
				}
			}
		});
	}


	public void initUI(){

		this.setTitle("重新打印");
		int width = Toolkit.getDefaultToolkit().getScreenSize().width;
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setVisible(true);
		this.setLocationRelativeTo(null);

	}
	private String Boolean(boolean b) {
		return null;
	}
	public void clearCondition(){
		fileNumberValue.setText("");
		fileNameValue.setText("");
		versionValue.setText("");
		phaseCodeValue.setSelectedIndex(0);
		printDateCheck.setSelected(false);
		printStartDateValue.setText("");
		printEndDateValue.setText("");

	}

	public CmPrintRecordQueryBean getConditionValues(){

		CmPrintRecordQueryBean cmPrintRecordQueryBean = new CmPrintRecordQueryBean();
		cmPrintRecordQueryBean.setFileNumber(CommonUtil.objectToString(fileNumberValue.getText()));
		cmPrintRecordQueryBean.setFileName(CommonUtil.objectToString(fileNameValue.getText()));
		cmPrintRecordQueryBean.setFileType(CommonUtil.objectToString(fileTypeValue.getSelectedItem()));
		cmPrintRecordQueryBean.setVersion(CommonUtil.objectToString(versionValue.getText()));
		cmPrintRecordQueryBean.setPhaseCode(CommonUtil.objectToString(phaseCodeValue.getSelectedItem()));
		if("true".equals(String.valueOf(printDateCheck.isSelected()))){
			cmPrintRecordQueryBean.setPrintStartDate(CommonUtil.objectToString(printStartDateValue.getText()));
			cmPrintRecordQueryBean.setPrintEndDate(CommonUtil.objectToString(printEndDateValue.getText()));
		}
		return cmPrintRecordQueryBean;

	}

	public List<CmPrintRecordInfoBean> getQueryReprintInfo(CmPrintRecordQueryBean cmPrintRecordQueryBean) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.queryReprintInfo(cmPrintRecordQueryBean);
	}
	public String getLatestDocumentVersionByNumber(String number) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getLatestDocumentVersionByNumber(number);
	}
	public String getNumberByOid(String oid) throws RemoteException, InvocationTargetException{
		return PrintToWCIntf.getNumberByOid(oid);
	}

	public String getCurrentTime(){
		Date nowDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String time = sdf.format(nowDate);
		return time;
	}

	public void updateReprintInfo(List<CmPrintRecordInfoBean> listBean, String time) throws RemoteException, InvocationTargetException{
		PrintToWCIntf.updateReprintInfo(listBean, time);
	}
}
