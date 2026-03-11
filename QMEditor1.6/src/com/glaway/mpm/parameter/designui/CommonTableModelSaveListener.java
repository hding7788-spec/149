package com.glaway.mpm.parameter.designui;

import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.view.NewTechnicsPart;
import com.glaway.mpm.view.TechnicsPaceJDialog;
import org.dom4j.Element;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CommonTableModelSaveListener implements TableModelListener {

	private JPanel panel;

	public CommonTableModelSaveListener(JPanel panel) {
		super();
		this.panel = panel;
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		DefaultTableModel tableModel = (DefaultTableModel) e.getSource();
		if (panel instanceof PhotoRecordTablePanel) {
			PhotoRecordTablePanel photoRecordTablePanel = (PhotoRecordTablePanel) panel;
			JTable table = photoRecordTablePanel.getTable();
			Container parentPanel = photoRecordTablePanel.getParentPanel();
			JFrame frame = photoRecordTablePanel.getFrame();
			int selectedRow = table.getSelectedRow();
			String order = (String) table.getValueAt(selectedRow,0);
			if(order!=null && !"".equals(order)){
				Element parentEle = null;
				Element techEle = null;
				if(parentPanel instanceof TechnicsPaceJDialog){
					TechnicsPaceJDialog paceJDialog = (TechnicsPaceJDialog) parentPanel;
					parentEle = paceJDialog.paceElement;
					techEle = paceJDialog.getStepElement().getParent().getParent();
				}
				if(parentEle != null){
					Element photoData = XmlUtility.getPhotoData(parentEle);
					Element photoEle = XmlUtility.getPhotoElementByBsOrder(photoData, order);
					XmlUtility.setAttributeValue(photoEle,"productNumber", String.valueOf(tableModel.getValueAt(selectedRow,4)));
					XmlUtility.setAttributeValue(photoEle,"psyq", String.valueOf(tableModel.getValueAt(selectedRow,5)));
					XmlUtility.setAttributeValue(photoEle,"pbzz", String.valueOf(tableModel.getValueAt(selectedRow,6)));
					XmlUtility.setAttributeValue(photoEle,"photoType", String.valueOf(tableModel.getValueAt(selectedRow,7)));
					XmlUtility.setAttributeValue(photoEle,"psdxType", String.valueOf(tableModel.getValueAt(selectedRow,8)));
					
					String PDCJMC = String.valueOf(tableModel.getValueAt(selectedRow,10));
					XmlUtility.setAttributeValue(photoEle,"PDCJBH", String.valueOf(PhotoRecordTablePanel.photoValueMap.get(PDCJMC)));
					XmlUtility.setAttributeValue(photoEle,"PDCJMC",PDCJMC );

				}
				((NewTechnicsPart)frame).saveProcess(techEle);
			}
		}
	}
}
