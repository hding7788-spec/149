package com.glaway.mpm.parameter.designui;

import com.glaway.mpm.model.UploadTechnics;
import com.glaway.mpm.parameter.service.ProcessParameterToWCIntf;
import com.glaway.mpm.release.ProcessInfoReleaseController;
import com.glaway.mpm.util.*;
import com.glaway.mpm.view.IconButton;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsPaceJDialog;
import com.glaway.mpm.wcIntf.TechnicsIntf;
import org.dom4j.Element;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

/**
 * 检验记录表测试
 * @author chenjianhui
 *
 */
public class BaiYuTestCheckFileTableJPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JFrame parentFrame;
	private JFrame frame;

	JPanel panel = new JPanel();

	private DefaultTableModel tableModel = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	private JTable table = new JTable(tableModel);

	private JButton addJButton = new IconButton("/images/button_add.png", "新增");

	private JButton quoteJButton = new IconButton("/images/button_add.png", "引用");

	private JButton refreshJButton = new IconButton("/images/refresh.png", "刷新");

	private JButton deleteJButton = new IconButton("/images/button_remove.png", "删除");

	private JButton modifyJButton = new IconButton("/images/button_open.png", "修改");

	public BaiYuTestCheckFileTableJPanel(JFrame parentFrame, JFrame frame) {
		this.parentFrame = parentFrame;
		this.frame = frame;
		jbInit();
		setName("CheckFileTable");
	}

	private void jbInit() {
		addJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openSoft();
			}
		});
		quoteJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quoteTables();
			}
		});
		refreshJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshTables();
			}
		});
		deleteJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteTables();
			}
		});
		modifyJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				modifyFile();
			}
		});
		panel.setLayout(new GridBagLayout());
		panel.add(addJButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(quoteJButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 0, 5), 0, 0));
		panel.add(refreshJButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
				5, 5, 0, 5), 0, 0));
		panel.add(deleteJButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		panel.add(modifyJButton, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 0, 5), 0, 0));
		tableModel.addColumn("序号");
		tableModel.addColumn("ID");
		tableModel.addColumn("名称");
		tableModel.addColumn("版本");
		tableModel.addColumn("创建人");
		tableModel.addColumn("修改人");
		tableModel.addColumn("创建时间");
		tableModel.addColumn("最后修改时间");
		tableModel.addColumn("docNumber");
		tableModel.addColumn("表格类型");
		tableModel.addColumn("部门");
		tableModel.addColumn("模版");

		table.setRowHeight(25);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getTableHeader().setReorderingAllowed(false);
		CommonUIUtil.hiddenCell(table,8);//隐藏爱可生数据文件编号列
		CommonUIUtil.hiddenCell(table,11);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(table.getTableHeader(), BorderLayout.PAGE_START);
		p.add(table, BorderLayout.CENTER);

		JScrollPane pane = new JScrollPane(table,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		if(parentFrame instanceof TechnicsPaceJDialog || parentFrame instanceof NewTechnicsPart){
			setLayout(new GridBagLayout());
			add(pane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
							0, 0, 0, 0), 0, 0));
			add(panel, new GridBagConstraints(1, 0, 1, 1, 0, 1.0,
					GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(
							0, 0, 0, 0), 0, 0));
		}
		if(parentFrame instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
			Element techElement = paceJDialog.getStepElement().getParent().getParent();
			String state = techElement.attributeValue("lifecycle");
			String creator = techElement.attributeValue("creator");
			boolean isApproved = (!"正在工作".equals(state) && !"修改中".equals(state)) ? true : false;
			if (isApproved) {
				setUIEnabled(false);
			} else {
				if(!creator.equals(NewTechnicsPart.currentUser)){
					setUIEnabled(false);
				}else{
					setUIEnabled(true);

				}
			}
		}

	}

	private void openSoft() {
		Element techEle = null;
		Element stepEle = null;
		Element paceEle = null;
		if(parentFrame instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
			paceEle = paceJDialog.paceElement;
			stepEle = paceJDialog.getStepElement();
			techEle = paceJDialog.getStepElement().getParent().getParent();
			String technicsNumber = techEle.attributeValue("technicsNumber");
			String stepNumber = stepEle.attributeValue("bsoID");
			String paceNumber = paceEle.attributeValue("bsoID");
			String technicsPath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
			String encodePath = technicsPath;
			try {
				 encodePath =  URLEncoder.encode(technicsPath,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String tableType = "专用表";
			try {
				tableType =  URLEncoder.encode(tableType,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			System.out.println("baiyu://schema?technicsNumber="+technicsNumber+"&stepNum="+stepNumber+"&paceNum="+paceNumber+"&technicsPath="+encodePath+"&tableType="+tableType);
			String url = "baiyu://schema?technicsNumber="+technicsNumber+"&stepNum="+stepNumber+"&paceNum="+paceNumber+"&technicsPath="+encodePath+"&tableType="+tableType;
			ProcessInfoReleaseController.openURL(url);
		} else if(parentFrame instanceof NewTechnicsPart) {
			techEle = XmlUtility.getTechnicsElement(((NewTechnicsPart)parentFrame).getCurrentTechnics());
			String technicsNumber = techEle.attributeValue("technicsNumber");
			String technicsPath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
			String encodePath = technicsPath;
			try {
				encodePath =  URLEncoder.encode(technicsPath,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String tableType = "周期表";
			try {
				tableType =  URLEncoder.encode(tableType,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			System.out.println("baiyu://schema?technicsNumber="+technicsNumber+"&technicsPath="+encodePath+"&tableType="+tableType);
			String url = "baiyu://schema?technicsNumber="+technicsNumber+"&technicsPath="+encodePath+"&tableType="+tableType;
			ProcessInfoReleaseController.openURL(url);
		}
	}

	private void quoteTables() {
		QuoteBaiyuDialog dialog = new QuoteBaiyuDialog(parentFrame);
		ArrayList<ArrayList<String>> lists = dialog.showDialog();
		if (lists != null) {
			//校验列表中是否有相同名称记录表
			for (ArrayList<String> list : lists) {
				String tableName = list.get(2);
				for(int i=0; i<tableModel.getRowCount(); i++){
					if(tableName.equals(tableModel.getValueAt(i,2))){
						SwingUtil.showMessageDialog("检验记录表中已有名称为【"+tableName+"】的表单，无法添加！", "提示", 2);
						return;
					}
				}
			}
			try {
				Element techEle = null;
				Element stepEle = null;
				Element paceEle = null;
				String technicsNumber = "";
				String stepNumber = "";
				String paceNumber = "";
				if(parentFrame instanceof TechnicsPaceJDialog){
					TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
					paceEle = paceJDialog.paceElement;
					stepEle = paceJDialog.getStepElement();
					techEle = paceJDialog.getStepElement().getParent().getParent();
					technicsNumber = techEle.attributeValue("technicsNumber");
					stepNumber = stepEle.attributeValue("bsoID");
					paceNumber = paceEle.attributeValue("bsoID");
				} else if(parentFrame instanceof NewTechnicsPart) {
					techEle = XmlUtility.getTechnicsElement(((NewTechnicsPart)parentFrame).getCurrentTechnics());
					technicsNumber = techEle.attributeValue("technicsNumber");
				}
				ArrayList<ArrayList<String>> template = ProcessParameterToWCIntf.quoteBaiyuTemplate(lists,technicsNumber,stepNumber,paceNumber);
				for (ArrayList<String> list : template) {
					int rowCount = tableModel.getRowCount();
					list.set(0,rowCount+1+"");
					Vector<Object> newValues = new Vector<Object>();
					newValues.addAll(list);
					tableModel.addRow(newValues);
				}
				saveToXml2(template);

				UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techEle);
				if (!NewTechnicsPart.editTechnics.contains(technics)) {
					NewTechnicsPart.editTechnics.add(technics);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		table.repaint();
		setTabTitle();
	}

	private void refreshTables() {
		Element techEle = null;
		Element stepEle = null;
		Element paceEle = null;
		if(parentFrame instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
			paceEle = paceJDialog.paceElement;
			stepEle = paceJDialog.getStepElement();
			techEle = paceJDialog.getStepElement().getParent().getParent();
			String technicsNumber = techEle.attributeValue("technicsNumber");
			String stepNumber = stepEle.attributeValue("bsoID");
			String paceNumber = paceEle.attributeValue("bsoID");
			ArrayList<ArrayList<String>> list = null;
			try {
				list = TechnicsIntf.getCheckFileList("pace",technicsNumber,stepNumber,paceNumber);
				steTableValues(list);
				saveToXml(list);
				table.repaint();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else if(parentFrame instanceof NewTechnicsPart) {
			techEle = XmlUtility.getTechnicsElement(((NewTechnicsPart)parentFrame).getCurrentTechnics());
			String technicsNumber = techEle.attributeValue("technicsNumber");
			ArrayList<ArrayList<String>> list = null;
			try {
				list = TechnicsIntf.getCheckFileList("doc",technicsNumber,"","");
				steTableValues(list);
				saveToXml(list);
				table.repaint();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		setTabTitle();

		UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techEle);
		if (!NewTechnicsPart.editTechnics.contains(technics)) {
			NewTechnicsPart.editTechnics.add(technics);
		}
	}

	private void deleteTables() {
		int row = table.getSelectedRow();
		if(row<0){
			SwingUtil.showMessageDialog("请选择需要删除的数据", "提示", 2);
			return;
		}
		String order = (String) tableModel.getValueAt(row,0);
		String number = (String) tableModel.getValueAt(row,1);
		Element parentEle = null;
		Element techEle = null;
		if(parentFrame instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
			parentEle = paceJDialog.paceElement;
			techEle = paceJDialog.getStepElement().getParent().getParent();
		} else if(parentFrame instanceof NewTechnicsPart) {
			parentEle = XmlUtility.getTechnicsElement(((NewTechnicsPart)parentFrame).getCurrentTechnics());
			techEle = parentEle;
		}
		if(parentEle != null){
			Element schemaData = XmlUtility.getSchemaData(parentEle);
			Element element = XmlUtility.getCheckFileElementByBsOrder(schemaData, order);
			schemaData.remove(element);
			tableModel.removeRow(row);
			int rowCount = table.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				table.setValueAt(i + 1+"", i, 0);
				String tableName = CommonUtil.objectToString(table.getValueAt(i, 2));
				Element nameElement = XmlUtility.getCheckFileElementByTableName(schemaData, tableName);
				XmlUtility.setAttributeValue(nameElement,"order", String.valueOf(i + 1));
			}
		}
		((NewTechnicsPart)frame).saveProcess(techEle);
		table.updateUI();
		try {
			String s = TechnicsIntf.deleteSchemaDataByNumber(number);
			if(!"".equals(s)){
				SwingUtil.showMessageDialog(s, "提示", 2);
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		setTabTitle();

		UploadTechnics technics = ObjectTransfer.technicsElementToUploadTechnics(techEle);
		if (!NewTechnicsPart.editTechnics.contains(technics)) {
			NewTechnicsPart.editTechnics.add(technics);
		}
	}

	private void modifyFile() {
		int row = table.getSelectedRow();
		if(row<0){
			SwingUtil.showMessageDialog("请选择需要修改的数据", "提示", 2);
			return;
		}
		String technicsNumber = "";
		String stepNumber = "";
		String paceNumber = "";
		if(parentFrame instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
			Element paceEle = paceJDialog.paceElement;
			Element stepEle = paceJDialog.getStepElement();
			Element techEle = paceJDialog.getStepElement().getParent().getParent();
			technicsNumber = techEle.attributeValue("technicsNumber");
			stepNumber = stepEle.attributeValue("bsoID");
			paceNumber = paceEle.attributeValue("bsoID");
			if(row>-1){
				String docNumber = (String) tableModel.getValueAt(row,8);
				String tableType = (String) tableModel.getValueAt(row,9);
				String template = (String) tableModel.getValueAt(row,11);

				try {
					Map<String,String> fileUrl = TechnicsIntf.getFileURLByDocNumber(docNumber);
					if(fileUrl!=null){
						String qbyURL = fileUrl.get("qby");
						String mjsonURL = fileUrl.get("mjson");
						String ojsonURL = fileUrl.get("ojson");

						String technicsPath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
						String encodePath = technicsPath;
						try {
							encodePath =  URLEncoder.encode(technicsPath,"UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						try {
							tableType =  URLEncoder.encode(tableType,"UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						String templateNumber = "";
						String templateName = "";
						if(template!=null&&template.contains("@@")){
							String[] ss = template.split("@@");
							templateNumber = ss[0];
							templateName = ss[1];
						}
						try {
							templateName =  URLEncoder.encode(templateName,"UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						System.out.println("baiyu://schema?qbyFileAbsolutePath="+qbyURL+"&xlsxFileAbsolutePath="+mjsonURL+"&jsonFileAbsolutePath="+ojsonURL+"&technicsNumber="+technicsNumber+"&stepNum="+stepNumber+"&paceNum="+paceNumber+"&tableId="+docNumber+"&technicsPath="+encodePath+"&tableType="+tableType+"&templateNumber="+templateNumber+"&templateName="+templateName);
						String url = "baiyu://schema?qbyFileAbsolutePath="+qbyURL+"&xlsxFileAbsolutePath="+mjsonURL+"&jsonFileAbsolutePath="+ojsonURL+"&technicsNumber="+technicsNumber+"&stepNum="+stepNumber+"&paceNum="+paceNumber+"&tableId="+docNumber+"&technicsPath="+encodePath+"&tableType="+tableType+"&templateNumber="+templateNumber+"&templateName="+templateName;
						ProcessInfoReleaseController.openURL(url);
					}
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} else if(parentFrame instanceof NewTechnicsPart) {
			Element techEle = XmlUtility.getTechnicsElement(((NewTechnicsPart) parentFrame).getCurrentTechnics());
			technicsNumber = techEle.attributeValue("technicsNumber");
			if(row>-1){
				String docNumber = (String) tableModel.getValueAt(row,8);
				String tableType = (String) tableModel.getValueAt(row,9);
				String template = (String) tableModel.getValueAt(row,11);

				try {
					Map<String,String> fileUrl = TechnicsIntf.getFileURLByDocNumber(docNumber);
					if(fileUrl!=null){
						String qbyURL = fileUrl.get("qby");
						String mjsonURL = fileUrl.get("mjson");
						String ojsonURL = fileUrl.get("ojson");

						String technicsPath = WorkSpaceUtil.getTechnicsPath(technicsNumber);
						String encodePath = technicsPath;
						try {
							encodePath =  URLEncoder.encode(technicsPath,"UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						try {
							tableType =  URLEncoder.encode(tableType,"UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						String templateNumber = "";
						String templateName = "";
						if(template!=null&&template.contains("@@")){
							String[] ss = template.split("@@");
							templateNumber = ss[0];
							templateName = ss[1];
						}
						try {
							templateName =  URLEncoder.encode(templateName,"UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						System.out.println("baiyu://schema?qbyFileAbsolutePath="+qbyURL+"&xlsxFileAbsolutePath="+mjsonURL+"&jsonFileAbsolutePath="+ojsonURL+"&technicsNumber="+technicsNumber+"&tableId="+docNumber+"&technicsPath="+encodePath+"&tableType="+tableType+"&templateNumber="+templateNumber+"&templateName="+templateName);
						String url = "baiyu://schema?qbyFileAbsolutePath="+qbyURL+"&xlsxFileAbsolutePath="+mjsonURL+"&jsonFileAbsolutePath="+ojsonURL+"&technicsNumber="+technicsNumber+"&tableId="+docNumber+"&technicsPath="+encodePath+"&tableType="+tableType+"&templateNumber="+templateNumber+"&templateName="+templateName;
						ProcessInfoReleaseController.openURL(url);
					}
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setUIEnabled(boolean b) {
		table.setEnabled(b);
		addJButton.setEnabled(b);
		quoteJButton.setEnabled(b);
		refreshJButton.setEnabled(b);
		deleteJButton.setEnabled(b);
		modifyJButton.setEnabled(b);
	}

	public void steTableValues(ArrayList<ArrayList<String>> lists) {
		tableModel.setRowCount(0);
		if (lists != null) {
			for (ArrayList<String> list : lists) {
				Vector<Object> newValues = new Vector<Object>();
				newValues.addAll(list);
				tableModel.addRow(newValues);
			}
		}
	}

	public void saveToXml(ArrayList<ArrayList<String>> allList) {
		Element parentEle = null;
		Element techEle = null;
		if(parentFrame instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
			parentEle = paceJDialog.paceElement;
			techEle = paceJDialog.getStepElement().getParent().getParent();
		} else if(parentFrame instanceof NewTechnicsPart) {
			parentEle = XmlUtility.getTechnicsElement(((NewTechnicsPart)parentFrame).getCurrentTechnics());
			techEle = parentEle;
		}
		if(parentEle != null){
			Element schemaData = XmlUtility.getSchemaData(parentEle);
			XmlUtility.removeAllChildElements(schemaData);
			for (ArrayList<String> list : allList) {
				Element schemaEle = schemaData.addElement(XmlUtility.SCHEMA_TAG);
				XmlUtility.setAttributeValue(schemaEle,"order",list.get(0));
				XmlUtility.setAttributeValue(schemaEle,"id",list.get(1));
				XmlUtility.setAttributeValue(schemaEle,"name",list.get(2));
				XmlUtility.setAttributeValue(schemaEle,"version",list.get(3));
				XmlUtility.setAttributeValue(schemaEle,"creator",list.get(4));
				XmlUtility.setAttributeValue(schemaEle,"mofitier",list.get(5));
				XmlUtility.setAttributeValue(schemaEle,"createTime",list.get(6));
				XmlUtility.setAttributeValue(schemaEle,"mofityTime",list.get(7));
				XmlUtility.setAttributeValue(schemaEle,"docNumber",list.get(8));
				XmlUtility.setAttributeValue(schemaEle,"tableType",list.get(9));
				XmlUtility.setAttributeValue(schemaEle,"dept",list.get(10));
				XmlUtility.setAttributeValue(schemaEle,"template",list.get(11));
			}

			/*Element paceImageElement = parentEle.element(XmlUtility.IMAGE_GROUP);
			if(paceImageElement != null){
				for (Iterator<Element> it = paceImageElement.elementIterator(XmlUtility.IMAGE_TAG); it.hasNext();) {
					Element image = it.next();
					String dataFrom = image.attributeValue("dataFrom");
					if("baiyu".equals(dataFrom)){
						paceImageElement.remove(image);
					}
				}
				Element image = paceImageElement.addElement(XmlUtility.IMAGE_TAG);
				for (ArrayList<String> list : allList) {
					String docNumber = list.get(1);

				}

			}else{

			}*/
		}
		((NewTechnicsPart)frame).saveProcess(techEle);
	}


	public void setUIValues(Vector<Element> schemaData) {
		tableModel.setRowCount(0);
		for (Element element : schemaData) {
			Vector<Object> vector = new Vector<Object>();
			vector.add(String.valueOf(element.attributeValue("order")));
			vector.add(String.valueOf(element.attributeValue("id")));
			vector.add(String.valueOf(element.attributeValue("name")));
			vector.add(String.valueOf(element.attributeValue("version")));
			vector.add(String.valueOf(element.attributeValue("creator")));
			vector.add(String.valueOf(element.attributeValue("mofitier")));
			vector.add(String.valueOf(element.attributeValue("createTime")));
			vector.add(String.valueOf(element.attributeValue("mofityTime")));
			vector.add(String.valueOf(element.attributeValue("docNumber")));
			String tableType = element.attributeValue("tableType");
			if(tableType == null || tableType.isEmpty()){
				tableType = "";
			}
			vector.add(tableType);
			String dept = element.attributeValue("dept");
			if(dept == null || dept.isEmpty()){
				dept = "";
			}
			vector.add(dept);
			vector.add(element.attributeValue("template"));
			tableModel.addRow(vector);
		}
		setTabTitle();
	}

	public void saveToXml2(ArrayList<ArrayList<String>> allList) {
		Element parentEle = null;
		Element techEle = null;
		if(parentFrame instanceof TechnicsPaceJDialog){
			TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentFrame;
			parentEle = paceJDialog.paceElement;
			techEle = paceJDialog.getStepElement().getParent().getParent();
		} else if(parentFrame instanceof NewTechnicsPart) {
			parentEle = XmlUtility.getTechnicsElement(((NewTechnicsPart)parentFrame).getCurrentTechnics());
			techEle = parentEle;
		}
		if(parentEle != null){
			Element schemaData = XmlUtility.getSchemaData(parentEle);
			for (ArrayList<String> list : allList) {
				Element schemaEle = schemaData.addElement(XmlUtility.SCHEMA_TAG);
				XmlUtility.setAttributeValue(schemaEle,"order",list.get(0));
				XmlUtility.setAttributeValue(schemaEle,"id",list.get(1));
				XmlUtility.setAttributeValue(schemaEle,"name",list.get(2));
				XmlUtility.setAttributeValue(schemaEle,"version",list.get(3));
				XmlUtility.setAttributeValue(schemaEle,"creator",list.get(4));
				XmlUtility.setAttributeValue(schemaEle,"mofitier",list.get(5));
				XmlUtility.setAttributeValue(schemaEle,"createTime",list.get(6));
				XmlUtility.setAttributeValue(schemaEle,"mofityTime",list.get(7));
				XmlUtility.setAttributeValue(schemaEle,"docNumber",list.get(8));
				XmlUtility.setAttributeValue(schemaEle,"tableType",list.get(9));
				XmlUtility.setAttributeValue(schemaEle,"dept",list.get(10));
				XmlUtility.setAttributeValue(schemaEle,"template",list.get(11));
			}
		}
		((NewTechnicsPart)frame).saveProcess(techEle);
	}

	String startType = com.glaway.mpm.EditorConfig.startType;
	public void setTabTitle() {
		if(!"SOP".equals(startType)){
			int i = tableModel.getRowCount();
			if (i > 0) {
				if (parentFrame instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentFrame).getTabbedPane().setTitleAt(14, "白羽检验记录表" + "(" + i + ")");
				} else if(parentFrame instanceof NewTechnicsPart) {
					((NewTechnicsPart) parentFrame).getNewTechnicsMasterJPanel_XW().getTabbedPane().setTitleAt(11, "白羽检验记录表" + "(" + i + ")");
				}
			} else {
				if (parentFrame instanceof TechnicsPaceJDialog) {
					((TechnicsPaceJDialog) parentFrame).getTabbedPane().setTitleAt(14, "白羽检验记录表");
				} else if(parentFrame instanceof NewTechnicsPart) {
					((NewTechnicsPart) parentFrame).getNewTechnicsMasterJPanel_XW().getTabbedPane().setTitleAt(11, "白羽检验记录表");
				}
			}
		}
	}

	public DefaultTableModel getTableModel() {
		return tableModel;
	}
}