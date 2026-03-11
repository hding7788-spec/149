package com.glaway.mpm.view;

import com.glaway.mpm.pdf.CheckOutTableUnit;
import com.glaway.mpm.pdf.HtmlGenerator;
import com.glaway.mpm.pdf.HtmlImageGeneratorFactory;
import com.glaway.mpm.pdf.PDFUtil;
import com.glaway.mpm.pdf.processor.Form7GYFBPDFBuilder;
import com.glaway.mpm.util.*;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TemplateIntf;
import gui.ava.html.image.generator.HtmlImageGenerator;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.*;

public class SaveAsTechnicsParamTemplatJDialog extends JDialog {
	private NewTechnicsPart frame;
	private String[][] technicsTypes = LoadConfig.getInstance().getTechnicsType();
	private JLabel nameLabel = new JLabel("模板名称");
	private JTextField nameField = new JTextField();
	private JLabel paramLabel = new JLabel("_参数化");
	private JLabel deptLabel = new JLabel("部门");
	private JComboBox depts = null;
	private JLabel fieldLabel = new JLabel("专业");
	private JComboBox fields = null;
	private JLabel productTypeLabel = new JLabel("产品类型");
	private JComboBox productTypes = null;

	private JButton okButton = new JButton("确定");
	private JButton cancelButton = new JButton("取消");

	private Element techElement;

	public SaveAsTechnicsParamTemplatJDialog(NewTechnicsPart frame,
                                             Element techElement) {
		super(frame, true);
		this.frame = frame;
		this.techElement = techElement;
		// setModal(true);
		setTitle("另存为工艺参数化模板");

		//获取到所有的车间组，然后自动定位到当前用户所在的组
		List<String> allList = new ArrayList<String>();
		Map<String,String> workShop = ResourceIntf.getWorkShops();
		if (workShop != null && workShop.size() > 0) {
			Collection<String> coll = workShop.values();
			Iterator<String> it = coll.iterator();
			while (it.hasNext()) {
				String temp = (String) it.next();
				if (temp != null && temp.trim().length() > 0) {
					allList.add(temp);
				}
			}

			Collections.sort(allList);
		}
		depts = new JComboBox(allList.toArray());
		depts.setAutoscrolls(true);
		fields = new JComboBox(ValueCache.typeValues);
		//TODO 产品类型取值
		productTypes = new JComboBox(ValueCache.productTypes);

		Container container = getContentPane();
		container.setLayout(new GridBagLayout());
		container.add(nameLabel, new GridBagConstraints(0, 0, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		container.add(nameField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 5, 10), 0, 0));
		container.add(paramLabel, new GridBagConstraints(2, 0, 1, 1, 0.1, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 0, 5, 0), 0, 0));
		container.add(deptLabel, new GridBagConstraints(0, 1, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		container.add(depts, new GridBagConstraints(1, 1, 1, 1, 1.0, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 5, 10), 0, 0));
		container.add(fieldLabel, new GridBagConstraints(0, 2, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		container.add(fields, new GridBagConstraints(1, 2, 1, 1, 1.0, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 5, 10), 0, 0));
		container.add(productTypeLabel, new GridBagConstraints(0, 3, 1, 1, 0,
				0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(10, 10, 5, 5), 0, 0));
		container.add(productTypes, new GridBagConstraints(1, 3, 1, 1, 1.0, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(10, 5, 5, 10), 0, 0));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		container.add(buttonPanel, new GridBagConstraints(0, 5, 2, 1, 1.0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 10, 5, 10), 0, 0));
		buttonPanel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0,
				0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 0, 5, 5), 0, 0));
		okButton.setPreferredSize(new Dimension(80, 23));
		okButton.setMinimumSize(new Dimension(80, 23));
		okButton.setMaximumSize(new Dimension(80, 23));
		buttonPanel.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 5), 0, 0));
		cancelButton.setPreferredSize(new Dimension(80, 23));
		cancelButton.setMinimumSize(new Dimension(80, 23));
		cancelButton.setMaximumSize(new Dimension(80, 23));
		buttonPanel.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						5, 5, 5, 0), 0, 0));

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					okProcess();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "另存为模板失败！");
					e1.printStackTrace();
				}
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelProcess();
			}
		});

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) (dimension.getWidth() - 500) / 2,
				(int) (dimension.getHeight() - 130) / 2, 600, 360);
		setVisible(true);
	}

	private void okProcess() throws Exception {
		String templateName = nameField.getText().trim();
		String dept = (String) depts.getSelectedItem();
		String field = (String) fields.getSelectedItem();
		String productType = (String) productTypes.getSelectedItem();
		if (templateName.equals("")) {
			JOptionPane.showMessageDialog(this, "模板名称不能为空！");
			return;
		}
		templateName += "_参数化";

		String printDanYuanFlag = this.techElement.attributeValue("printDanYuanFlag");
        String technum = XmlUtility.getAttributeValue(this.techElement, "technicsNumber");
		if(!"否".equals(printDanYuanFlag)){
			String techPath = WorkSpaceUtil.getTechnicsDirectory(technum);
			String filePath = techPath+File.separator+"unitTable";
			File unitTableFilePath = new File(filePath);
			if(!unitTableFilePath.exists()){
				unitTableFilePath.mkdirs();
			}else{
				FileUtil.deleteSubFile(unitTableFilePath);
			}
			List<Element> stepElements = XmlUtility.getAllSteps(techElement);
			for (Element stepElement : stepElements) {
				List<CheckOutTableUnit> unitList = new ArrayList<CheckOutTableUnit>();
				String stepNumber = stepElement.attributeValue("stepNumber");
				List<Element> paces = XmlUtility.getAllPaces(stepElement);
				for(Element pace : paces){
					Element checkRecordTables= pace.element("checkRecordTables");
					if(checkRecordTables!=null) {
						List<Element> parameterTables = checkRecordTables.elements();
						for(Element parameterTable:parameterTables){
							String unitTableName = parameterTable.attributeValue("name");
							String tableType = parameterTable.attributeValue("type");
							String projectName = parameterTable.attributeValue("projectName");
							String tableName = parameterTable.attributeValue("tableName");

							List<Element> parameters = parameterTable.elements("parameter");
							for(Element parameter:parameters){
								Element values = parameter.element("values");
								CheckOutTableUnit unit = new CheckOutTableUnit();
								unit.setGongXu(stepNumber);
								unit.setGongBu(pace.attributeValue("stepNumber"));
								unit.setTableType(tableType);
								unit.setUnitTableName(unitTableName);
								unit.setProjectName(projectName);
								unit.setTableName(tableName);
								List<Element> valueList =  values.elements("value");
								for(Element value :valueList){
									String columnName = value.attributeValue("columnName");
									String attributeValue = value.element("attribute").getText();
									attributeValue = PDFUtil.objectToString(attributeValue);
									if ("检测项".equals(columnName) || "记录项".equals(columnName)) {
										unit.setJiLuXiang(attributeValue);
									} else if ("公称值".equals(columnName) || "要求".equals(columnName)) {
										unit.setYaoQiuVale(attributeValue);
									} else if ("上偏差".equals(columnName)) {
										unit.setShangPianCha(attributeValue);
									} else if ("下偏差".equals(columnName)) {
										unit.setXiaPianCha(attributeValue);
									} else if ("实测值".equals(columnName)) {
										unit.setShiCeValue(attributeValue);
									} else if ("判定/结论".equals(columnName)) {
										//无需处理
									} else if ("记录".equals(columnName)) {
										unit.setJiLu(attributeValue);
									}
								}
								unitList.add(unit);
							}
						}
					}
				}
				if(!unitList.isEmpty()){

					List<List<CheckOutTableUnit>> splitLists = Form7GYFBPDFBuilder.splitList(unitList, 16);

					int index = 1;
					for (List<CheckOutTableUnit> subList : splitLists) {

						String  imageFilePath = filePath+File.separator+stepNumber+"_"+index+".png";
						String htmlContent = HtmlGenerator.generateHtml(subList, "file:///"+techPath);
						String  htmlFilePath = filePath+File.separator+stepNumber+"_"+index+".html";
						FileWriter htmlWriter = new FileWriter(htmlFilePath);
						htmlWriter.getEncoding();
						htmlWriter.write(htmlContent);
						htmlWriter.close();
						HtmlImageGenerator imageGenerator = HtmlImageGeneratorFactory.getInstance();
						imageGenerator.loadUrl("file:///"+htmlFilePath);
						imageGenerator.getBufferedImage();
						imageGenerator.saveAsImage(imageFilePath);
						System.out.println("生成 "+stepNumber+"工序 第"+index+"个单元表图片:"+imageFilePath);
						index ++;

					}
				}
			}


		}

		String templetDirectory = WorkSpaceUtil.getTempletPath("Process_TechnicsParam");
		FileUtil.delAllFile(templetDirectory);
		File templet = copyTechnicsToParamTemplet(techElement, templetDirectory, templateName);


		String result = TemplateIntf.uploadProcessParamTemplate(templateName, getTemplateByte(templet),dept,field,productType,"technics");
		if ("".equals(result)) {
			JOptionPane.showMessageDialog(this, "工艺参数化模板“" + templateName + "”保存成功");
		} else {
			JOptionPane.showMessageDialog(this, result);
		}
		dispose();
	}

	private void cancelProcess() {
		dispose();
	}

	public static File copyTechnicsToParamTemplet(Element technicsElement,String templateDirectory,String templateName) throws Exception {
		String technicsNumber = technicsElement.attributeValue("technicsNumber");
		String tecnnicsDirectory = WorkSpaceUtil.getTechnicsDirectory(technicsNumber);
		if (tecnnicsDirectory == null)
			return null;
		File newTemplet = new File(templateDirectory + File.separator + templateName);
		if (newTemplet.exists()) {
			FilesUtil.delFolder(newTemplet.getAbsolutePath());
		} else
			newTemplet.mkdir();
		FilesUtil.copyDirectiory(tecnnicsDirectory, newTemplet.getAbsolutePath());
		String technicsXml = WorkSpaceUtil.getTechnicsPath(technicsNumber);

		File[] file = newTemplet.listFiles();

		for (int i = 0; i < file.length; i++) {
			if ((file[i].isFile()) && (technicsXml.endsWith(file[i].getName()))) {
				String sourceFileName = tecnnicsDirectory
						+ File.separator
						+ tecnnicsDirectory.substring(
						tecnnicsDirectory.lastIndexOf(File.separator),
						tecnnicsDirectory.length());
				if (!sourceFileName.toLowerCase().endsWith(".xml"))
					sourceFileName = sourceFileName + ".xml";
				FilesUtil.repalce(sourceFileName, file[i], templateName, "xml");
				break;
			}
		}
		/**移除参装件及质量记录表信息*/
		WorkSpaceUtil.removeQualityElement(newTemplet.getAbsolutePath()+File.separator+templateName+".xml",false,false);
		return newTemplet;
	}

	public byte[] getTemplateByte(File template) {
		String path = template.getPath() + ".zip";
		ApacheZipUtil.compress(template, path);
		byte[] bytes = FileUtil.readFilePathToByte(path);
		return bytes;
	}

}