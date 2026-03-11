package com.glaway.mpm.print.listener;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.print.constants.PrintConstants;
import com.glaway.mpm.print.data.CmDistributionBean;
import com.glaway.mpm.print.data.CmImportBean;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.data.CmPrintQueryBean;
import com.glaway.mpm.print.data.CmSealBean;
import com.glaway.mpm.print.helper.MPMPrintHelper;
import com.glaway.mpm.print.helper.MPMPrintProcessor;
import com.glaway.mpm.print.service.PrintToWCIntf;
import com.glaway.mpm.print.ui.AddChangeNoticeDialog;
import com.glaway.mpm.print.ui.AddFileDialog;
import com.glaway.mpm.print.ui.AddFileOnBomDialog;
import com.glaway.mpm.print.ui.AddFileOnProcessDirectoryDialog;
import com.glaway.mpm.print.ui.AddOutFileDialog;
import com.glaway.mpm.print.ui.AddPrintApplicationDialog;
import com.glaway.mpm.print.ui.AddPrintInforDialog;
import com.glaway.mpm.print.ui.AfterChangeInfoPanel;
import com.glaway.mpm.print.ui.ButtonPanel;
import com.glaway.mpm.print.ui.ChangeNoticeInfoPanel;
import com.glaway.mpm.print.ui.FileListTable;
import com.glaway.mpm.print.ui.FilePrintMainPanel;
import com.glaway.mpm.print.ui.MPMPrintFileFrame;
import com.glaway.mpm.print.ui.OutFileConditionPanel;
import com.glaway.mpm.print.ui.RecipientsInfoPanel;
import com.glaway.mpm.print.ui.SearchBaselineDialog;
import com.glaway.mpm.print.ui.SelectBaselineOrDeptDialog;
import com.glaway.mpm.print.ui.SelectBaselineOrDeptPanel;
import com.glaway.mpm.print.util.FilePrintUtil;
import com.glaway.mpm.print.util.LocalPrintUtil;
import com.glaway.mpm.util.CommonUIUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.ExcelUtil;
import com.glaway.mpm.util.FileChooserTool;
import com.glaway.mpm.util.FileUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;

public class PrintFileButtonListener implements ActionListener {

    private ButtonPanel buttonPanel;
    private SearchBaselineDialog searchBaselineDialog;

    public PrintFileButtonListener(ButtonPanel buttonPanel) {
        this.buttonPanel = buttonPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == buttonPanel.getSelectAll()) {
            selectAll();
        } else if (obj == buttonPanel.getAddButton()) {
            add();
        } else if (obj == buttonPanel.getAddOnBomButton()) {
            addOnBom();
        } else if (obj == buttonPanel.getDeleteButton()) {
            delete();
        } else if (obj == buttonPanel.getSureButton()) {
            sure();
        } else if (obj == buttonPanel.getCancelButton()) {
            cancel();
        } else if (obj == buttonPanel.getLookUpButton()) {
            lookUp();
        } else if (obj == buttonPanel.getViewButton()) {
            view();
        } else if (obj == buttonPanel.getPrintButton()) {
            print();
        } else if (obj == buttonPanel.getSearchButton()) {
            search();
        } else if (obj == buttonPanel.getSetDeptButton()) {
            setDept();
        } else if (obj == buttonPanel.getSetSealButton()) {
            SetSeal();
        } else if (obj == buttonPanel.getSetNotPrinted()) {
            setPrintStatus("未打印");
        } else if (obj == buttonPanel.getSetToPrinted()) {
            setPrintStatus("已打印");
        } else if (obj == buttonPanel.getAddOnProcessDirectory()) {
            addOnProcessDirectory();
        } else if (obj == buttonPanel.getDistributionButton()) {
            distribution();
        } else if (obj == buttonPanel.getModifyButton()) {
            modify();
        } else if (obj == buttonPanel.getAddChangeButton()) {
            addChange();
        } else if (obj == buttonPanel.getImportButton()) {
            importExcel();
        } else if (obj == buttonPanel.getAllSetDeptButton()) {
            allSetDept();
        } else if (obj == buttonPanel.getAllSetSealButton()) {
            allSetSeal();
        } else if (obj == buttonPanel.getAllDeleteDeptButton()) {
            allDeleteDept();
        } else if (obj == buttonPanel.getAllDeleteSealButton()) {
            allDeleteSeal();
        } else if (obj == buttonPanel.getAllSetButton()) {
            allSetData();
        }


    }

    private void importExcel() {
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable fileListTable = (FileListTable) buttonPanel.getTablePanel();
//			String tableType = fileListTable.getType();
            String category = fileListTable.getCategory();
            File file = FileChooserTool.getSaveFile("xls", fileListTable);
            if (file != null) {
                if (file.isFile() && file.exists()) {
                    if (file.renameTo(file)) {
                        HSSFWorkbook workbook = ExcelUtil.getWorkbook(file);
                        HSSFSheet sheet = workbook.getSheetAt(0);
                        //获取标题 start
                        List<String> list = new ArrayList<String>();
                        HSSFRow titleRow = sheet.getRow(0);
                        for (int i = 0; i < titleRow.getLastCellNum(); i++) {
                            HSSFCell cell = titleRow.getCell(i);
                            if (cell == null) {
                                continue;
                            }
                            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                            Object cellValue = cell.getStringCellValue();
                            list.add(CommonUtil.objectToString(cellValue));
                        }
                        //获取标题 end
                        //获取内容 start
                        List<CmImportBean> beanList = new ArrayList<CmImportBean>();
                        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                            String fileNumber = "";
                            String version = "";
                            HSSFRow row = sheet.getRow(i);
                            CmImportBean cmImportBean = new CmImportBean();
                            if (row != null) {
                                for (int j = 0; j < row.getLastCellNum(); j++) {
                                    String type = list.get(j);
                                    HSSFCell cell = row.getCell(j);
                                    if (cell == null) {
                                        continue;
                                    }
                                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                                    Object cellValue = cell.getStringCellValue();
                                    if ("外来单位".equals(type)) {
                                        if ("".equals(CommonUtil.objectToString(cellValue))) {
                                            CommonUIUtil.showMessageDialog(null, "第" + i + "行外来单位填写错误!");
                                            return;
                                        }
                                        cmImportBean.setOutDept(CommonUtil.objectToString(cellValue));
                                    } else if ("文件编号".equals(type)) {
                                        if ("".equals(CommonUtil.objectToString(cellValue))) {
                                            CommonUIUtil.showMessageDialog(null, "第" + i + "行文件编号填写错误!");
                                            return;
                                        }
                                        cmImportBean.setFileNumber(CommonUtil.objectToString(cellValue));
                                        fileNumber = CommonUtil.objectToString(cellValue);
                                    } else if ("文件名称".equals(type)) {
                                        if ("".equals(CommonUtil.objectToString(cellValue))) {
                                            CommonUIUtil.showMessageDialog(null, "第" + i + "行文件名称填写错误!");
                                            return;
                                        }
                                        cmImportBean.setFileName(CommonUtil.objectToString(cellValue));
                                    } else if ("版本".equals(type)) {
                                        cmImportBean.setVersion(CommonUtil.objectToString(cellValue));
                                        version = CommonUtil.objectToString(cellValue);
                                    } else if ("阶段标记".equals(type)) {
                                        cmImportBean.setPhaseCode(CommonUtil.objectToString(cellValue));
                                    } else if ("文件类型".equals(type)) {
                                        if ("".equals(CommonUtil.objectToString(cellValue))) {
                                            CommonUIUtil.showMessageDialog(null, "第" + i + "行文件类型填写错误!");
                                            return;
                                        }
                                        cmImportBean.setFileType(CommonUtil.objectToString(cellValue));
                                    } else if ("页数".equals(type)) {
                                        cmImportBean.setPageCount(CommonUtil.objectToString(cellValue));
                                    } else if ("密级".equals(type)) {
                                        cmImportBean.setSecret(CommonUtil.objectToString(cellValue));
                                    } else if ("型号类型".equals(type)) {
                                        if ("".equals(CommonUtil.objectToString(cellValue))) {
                                            CommonUIUtil.showMessageDialog(null, "第" + i + "行型号类型填写错误!");
                                            return;
                                        }
                                        cmImportBean.setXhlx(CommonUtil.objectToString(cellValue));
                                    } else if ("更改单外来单位".equals(type)) {
                                        cmImportBean.setChangeNoticeOutDept(CommonUtil.objectToString(cellValue));
                                    } else if ("更改单成套图更改".equals(type)) {
                                        cmImportBean.setViewChange(CommonUtil.objectToString(cellValue));
                                    } else if ("更改单文件编号".equals(type)) {
                                        cmImportBean.setChangeNoticeNumber(CommonUtil.objectToString(cellValue));
                                    } else if ("更改日期".equals(type)) {
                                        cmImportBean.setChangeDate(CommonUtil.objectToString(cellValue));
                                    } else if ("更改内容".equals(type)) {
                                        cmImportBean.setChangeContent(CommonUtil.objectToString(cellValue));
                                    }
                                }
                            }
                            if ("".equals(category)) {
                                if (cmImportBean.getOutDept() == null || "".equals(cmImportBean.getOutDept())) {
                                    CommonUIUtil.showMessageDialog(null, "第" + i + "行外来单位不能为空!");
                                    return;
                                }
                            }
                            boolean repeat = MPMPrintProcessor.checkRepeatInputFile(fileNumber, version, category);
                            if (repeat) {
                                CommonUIUtil.showMessageDialog(null, "第" + i + "行文档重复导入,导入失败!");
                                return;
                            }
                            beanList.add(cmImportBean);
                        }
                        //获取内容 end
                        List<CmPrintInfoBean> list1 = MPMPrintProcessor.saveImportInfo(beanList);
                        fileListTable.setAddValues(list1);
                    } else {
                        JOptionPane.showMessageDialog(buttonPanel.getTablePanel(), "另一个程序正在使用此文件！", "提示", 1);
                    }
                }
            }
        }

    }

    private void addChange() {
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable fileListTable = (FileListTable) buttonPanel.getTablePanel();
            String type = fileListTable.getType();
            String category = fileListTable.getCategory();
            if (type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)) {
                new AddChangeNoticeDialog(fileListTable, null, category);
            }
        }
    }

    private void modify() {
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable fileListTablePanel = (FileListTable) buttonPanel.getTablePanel();
            JTable table = fileListTablePanel.getTable();
            String type = fileListTablePanel.getType();
            String category = fileListTablePanel.getCategory();
            if (type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)) {
                int selectCount = 0;
                for (int row = 0; row < table.getRowCount(); row++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                    if (isSelect) {
                        selectCount++;
                    }
                }
                if (selectCount > 1) {
                    CommonUIUtil.showMessageDialog(null, "请选择单行数据进行编辑");
                    return;
                }
                CmPrintInfoBean cmPrintInfoBean = null;
                for (int row = 0; row < table.getRowCount(); row++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                    if (isSelect) {
                        cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                    }
                }
                if (cmPrintInfoBean != null) {
                    if (!"".equals(cmPrintInfoBean.getChangeNoticeID()) && cmPrintInfoBean.getChangeNoticeID() != null) {
                        new AddChangeNoticeDialog(fileListTablePanel, cmPrintInfoBean, category);
                    } else {
                        new AddOutFileDialog(fileListTablePanel, cmPrintInfoBean, category);
                    }
                }
            }
        }
    }

    private void distribution() {
        List<String> list = new ArrayList<String>();
        JTable table = null;
        FileListTable panel = (FileListTable) buttonPanel.getTablePanel();
        table = panel.getTable();
        CommonUIUtil.stopTableCellEditing(table);
        FilePrintMainPanel parentPanel = (FilePrintMainPanel) panel.getComponent();
        RecipientsInfoPanel recipientsInfoPanel = parentPanel.getRecipientsInfoPanel();
        CmDistributionBean cmDistributionBean = recipientsInfoPanel.getConditionValues();
        if ("".equals(CommonUtil.objectToString(cmDistributionBean.getReceiptor()))) {
            CommonUIUtil.showMessageDialog(null, "请先确认身份");
            return;
        }
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        String distributionDate = df.format(day);
        cmDistributionBean.setReceiveTime(CommonUtil.objectToString(distributionDate));
        //状态判断
        for (int row = 0; row < table.getRowCount(); row++) {
            boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
            if (isSelect) {
                String stutas = CommonUtil.objectToString(table.getValueAt(row, 9));
                if (!"已打印".equals(stutas)) {
                    CommonUIUtil.showMessageDialog(null, "只能对“已打印”状态的文档进行分发");
                    return;
                }
            }
        }
        for (int row = 0; row < table.getRowCount(); row++) {
            boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
            if (isSelect) {
                String id = CommonUtil.objectToString(table.getValueAt(row, 0));
                list.add(id);
                table.setValueAt("已下发", row, 9);
                table.setValueAt(cmDistributionBean.getReceiveFile(), row, 11);
                table.setValueAt(cmDistributionBean.getReceiveTime(), row, 12);
            }
        }
        recipientsInfoPanel.setValue(cmDistributionBean);
        if (list != null && !list.isEmpty()) {
            List<String> idList = MPMPrintProcessor.setFileStatus(list, cmDistributionBean);
            MPMPrintProcessor.setPrintStatus(idList);
        } else {
            CommonUIUtil.showMessageDialog(null, "请先选择设置行");
        }
    }

    private void addOnProcessDirectory() {
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable fileListTable = (FileListTable) buttonPanel.getTablePanel();
            String type = fileListTable.getType();
            if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
                new AddFileOnProcessDirectoryDialog(fileListTable);
            } else if (type.equals(PrintConstants.TITLE_MAINPANEL_WJBDSQ)) {
                new AddFileOnProcessDirectoryDialog(fileListTable);
            }
        }
    }

    private void setPrintStatus(String status) {
        List<String> list = new ArrayList<String>();
        String printer = "";
        String printDate = "";
        JTable table = null;
        FileListTable panel = (FileListTable) buttonPanel.getTablePanel();
        table = panel.getTable();
        CommonUIUtil.stopTableCellEditing(table);
        for (int row = 0; row < table.getRowCount(); row++) {
            boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
            if (isSelect) {
                String id = CommonUtil.objectToString(table.getValueAt(row, 0));
                if ("已打印".equals(status)) {
                    printer = MPMPrintFileFrame.getCurrentUser().getName();
                    Date day = new Date();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    printDate = df.format(day);
                }
                list.add(id);
                if (MPMPrintFileFrame.dept != null && !MPMPrintFileFrame.dept.isEmpty()) {
                    table.setValueAt("已分发", row, 10);
                } else {
                    table.setValueAt(status, row, 10);
                }
                table.setValueAt(printer, row, 11);
                table.setValueAt(printDate, row, 12);
            }
        }
        if (list != null && !list.isEmpty()) {
            if (MPMPrintFileFrame.dept != null && !MPMPrintFileFrame.dept.isEmpty()) {
                MPMPrintProcessor.setPrintStatus(list, "已分发", printer, printDate);
            } else {
                MPMPrintProcessor.setPrintStatus(list, status, printer, printDate);

            }
        } else {
            CommonUIUtil.showMessageDialog(null, "请先选择设置行");
        }
    }

    private void setDept() {
        if (buttonPanel.getTablePanel() instanceof SelectBaselineOrDeptPanel) {
            SelectBaselineOrDeptPanel panel = (SelectBaselineOrDeptPanel) buttonPanel.getTablePanel();
            JTable table = panel.getTable();
            Container component = panel.getComponent();
            CommonUIUtil.stopTableCellEditing(table);
            int row = ((SelectBaselineOrDeptDialog) component).getTable().getSelectedRow();
            int column = ((SelectBaselineOrDeptDialog) component).getTable().getSelectedColumn();
            String value = "";
            for (int i = 0; i < table.getRowCount(); i++) {
                if (!"".equals(table.getValueAt(i, 2)) && table.getValueAt(i, 2) != null) {
                    if ("".equals(value)) {
                        value = table.getValueAt(i, 1) + ":" + table.getValueAt(i, 2) + "份";
                    } else {
                        value = value + "," + table.getValueAt(i, 1) + ":" + table.getValueAt(i, 2) + "份";
                    }
                }
            }
            ((SelectBaselineOrDeptDialog) component).getTable().setValueAt(value, row, column);
            ((SelectBaselineOrDeptDialog) component).getTable().editCellAt(row, column);
            ((SelectBaselineOrDeptDialog) component).dispose();
        }
    }

    private void SetSeal() {
        SelectBaselineOrDeptPanel panel = (SelectBaselineOrDeptPanel) buttonPanel.getTablePanel();
        int column = panel.getColumn();
        Container component = panel.getComponent();
        JTable table = null;
        table = ((SelectBaselineOrDeptPanel) buttonPanel.getTablePanel()).getTable();
        CommonUIUtil.stopTableCellEditing(table);
        int row = ((SelectBaselineOrDeptDialog) component).getTable().getSelectedRow();
        String value = "";
        for (int i = 0; i < table.getRowCount(); i++) {
            boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(i, 0)));
            if (isSelect) {
                String batch = CommonUtil.objectToString(table.getValueAt(i, 2));
                if ("".equals(value)) {
                    value = value + batch;
                } else {
                    value = value + "," + batch;
                }
            }
        }
//		JTable table2 = panel.getResourceTable();
        if (column == 7) {
            ((SelectBaselineOrDeptDialog) component).getTable().setValueAt(value, row, 7);
            ((SelectBaselineOrDeptDialog) component).getTable().editCellAt(row, 7);
        } else if (column == 11) {
            String batch = CommonUtil.objectToString(((SelectBaselineOrDeptDialog) component).getTable().getValueAt(row, 10));
            if (!"".equals(batch)) {
                String[] temp = batch.split(",");
                for (String str : temp) {
                    if (value.contains(str)) {
                        CommonUIUtil.showMessageDialog(null, "不能重复添加印章");
                        return;
                    }
                }
            }
            ((SelectBaselineOrDeptDialog) component).getTable().setValueAt(value, row, 11);
            ((SelectBaselineOrDeptDialog) component).getTable().editCellAt(row, 11);
        }
        ((SelectBaselineOrDeptDialog) component).dispose();

    }

    private void selectAll() {
        boolean isSelected = buttonPanel.getSelectAll().isSelected();
        JTable table = null;
        int column = -1;
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            table = ((FileListTable) buttonPanel.getTablePanel()).getTable();
            column = 1;
        } else if (buttonPanel.getTablePanel() instanceof SelectBaselineOrDeptPanel) {
            table = ((SelectBaselineOrDeptPanel) buttonPanel.getTablePanel()).getTable();
            String type = ((SelectBaselineOrDeptPanel) buttonPanel.getTablePanel()).getType();
            if (type.equals(PrintConstants.TITLE_DIALOG_DEPT)) {
                column = 0;
            } else if (type.equals(PrintConstants.TITLE_DIALOG_SEAL)) {
                column = 0;
            }
        }
        if (table != null && column >= 0) {
            for (int row = 0; row < table.getRowCount(); row++) {
                table.setValueAt(isSelected, row, column);
            }
        }
    }

    private void add() {
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable fileListTablePanel = (FileListTable) buttonPanel.getTablePanel();
            String type = fileListTablePanel.getType();
            String category = fileListTablePanel.getCategory();
            String buttonType = buttonPanel.getType();
            if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
                new AddPrintApplicationDialog(fileListTablePanel, category);
            } else if (type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)) {
                new AddOutFileDialog(fileListTablePanel, null, category);
            } else if (buttonType.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL + "add")) {
                JTable table = fileListTablePanel.getTable();
                ArrayList<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
                for (int row = 0; row < table.getRowCount(); row++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                    if (isSelect) {
                        CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                        if (cmPrintInfoBean.getFileState().equals("未打印") || cmPrintInfoBean.getFileState().equals("已遗失")) {
                            CommonUIUtil.showMessageDialog(null, "不可以添加状态为“未打印”或者“已遗失”的文档");
                            return;
                        }
                        list.add(cmPrintInfoBean);
                    }
                }
                if (list != null && !list.isEmpty()) {
                    FilePrintMainPanel filePrintMainPanel = (FilePrintMainPanel) fileListTablePanel.getComponent();
                    filePrintMainPanel.getFileTable().setAddValues(list);
                }
            } else if (type.equals(PrintConstants.TITLE_MAINPANEL_WJBDSQ)) {
                new AddPrintInforDialog(fileListTablePanel);
            } else {
                new AddFileDialog(fileListTablePanel);
            }
        } else if (buttonPanel.getTablePanel() instanceof SelectBaselineOrDeptPanel) {
            SelectBaselineOrDeptPanel panel = (SelectBaselineOrDeptPanel) buttonPanel.getTablePanel();
            panel.addOneRow();
        }
    }

    private void addOnBom() {
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable fileListTable = (FileListTable) buttonPanel.getTablePanel();
            String type = fileListTable.getType();
            if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
                new AddFileOnBomDialog(fileListTable);
            }
        }
    }

    private void delete() {
        List<Integer> list = new ArrayList<Integer>();
        JTable table = null;
        if (buttonPanel.getTablePanel() instanceof SelectBaselineOrDeptPanel) {
            SelectBaselineOrDeptPanel panel = (SelectBaselineOrDeptPanel) buttonPanel.getTablePanel();
            String type = panel.getType();
            table = panel.getTable();

            if (type.equals(PrintConstants.TITLE_DIALOG_DEPT)) {
                for (int row = 0; row < table.getRowCount(); row++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 0)));
                    if (isSelect) {
                        list.add(row);
                    }
                }
                panel.removeRow(list);
            }
        } else if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable panel = (FileListTable) buttonPanel.getTablePanel();
            String type = panel.getType();
            table = panel.getTable();
            if (table != null) {
                CommonUIUtil.stopTableCellEditing(table);
            }
            List<CmPrintInfoBean> beanList = new ArrayList<CmPrintInfoBean>();
            for (int row = 0; row < table.getRowCount(); row++) {
                boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
//					CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
//					if(cmPrintInfoBean != null){
//						boolean d = cmPrintInfoBean.isProcessfile();
//						if(d){
//							continue;
//						}
//					}
                } else if (type.equals(PrintConstants.TITLE_MAINPANEL_WLWJLR)) {
                    if (isSelect) {
                        CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                        beanList.add(cmPrintInfoBean);
                    }
                }
                if (isSelect) {
                    list.add(row);
                }
            }
            panel.removeRow(list);
            if (beanList != null && !beanList.isEmpty()) {
                MPMPrintProcessor.deleteOutFileByID(beanList);
            }
            panel.repaint();
        }
    }

    private void lookUp() {
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable fileListTablePanel = (FileListTable) buttonPanel.getTablePanel();
            JTable table = fileListTablePanel.getTable();
            String type = fileListTablePanel.getType();
            if (type.equals(PrintConstants.TITLE_MAINPANEL_ADDREQUEST)
                    || type.equals(PrintConstants.TITLE_ADDONBOM_PART_RELATE) ||
                    type.equals(PrintConstants.TITLE_MAINPANEL_YLDYSQ)) {
                //20170309_jiangyixing,将传oid改为实体bean对象
                List<CmPrintInfoBean> cmPrintInfoBeans = MPMPrintHelper.getCmPrintInfoFromForLookUp(table);
//				List<String> list = new ArrayList<String>();
//				for (int row = 0; row < table.getRowCount(); row++) {
//					boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
//					if (isSelect) {
//						String oid = CommonUtil.objectToString(table.getValueAt(row, 0));
//						list.add(oid);
//					}
//				}
                if (cmPrintInfoBeans.size() > 1) {
                    int d = CommonUIUtil.showConfirmDialog(null, "是否打开" + cmPrintInfoBeans.size() + "份进行查看？", null);
                    if (d != 0) {
                        return;
                    }
                }
                Map<String, byte[]> map = MPMPrintHelper.getPdfFileForLookUp(cmPrintInfoBeans);

                String pdfTempPath = FileUtil.makeTmpDir(PrintConstants.FOLDER_PDFTEMP);
                StringBuffer buf = new StringBuffer();
                for (String name : map.keySet()) {
                    String filePath = pdfTempPath + File.separator + "look_" + name + ".pdf";
                    FileUtil.writeBytes(filePath, map.get(name));
                    try {
                        Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler  " + filePath);
                    } catch (Exception e1) {
                        buf.append(name + ",");
                    }
                }
                if (buf.toString().length() > 1) {
                    CommonUIUtil.showMessageDialog(null, "打开" + buf.toString().substring(0, buf.toString().length() - 1) + "文件出错！");
                }
            }
        }
    }

    private void cancel() {
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            Container component = ((FileListTable) buttonPanel.getTablePanel()).getComponent();
            if (component instanceof FilePrintMainPanel) {
                MPMPrintFileFrame frame = ((FilePrintMainPanel) component).getFrame();
                frame.closeWindow();
            } else if (component instanceof AddFileDialog) {
                ((AddFileDialog) component).dispose();
            } else if (component instanceof AddPrintApplicationDialog) {
                ((AddPrintApplicationDialog) component).dispose();
            } else if (component instanceof AddFileOnBomDialog) {
                ((AddFileOnBomDialog) component).dispose();
            } else if (component instanceof AddFileOnProcessDirectoryDialog) {
                ((AddFileOnProcessDirectoryDialog) component).dispose();
            } else if (component instanceof AddPrintInforDialog) {
                ((AddPrintInforDialog) component).dispose();
            }
        } else if (buttonPanel.getTablePanel() instanceof SelectBaselineOrDeptPanel) {
            Container component = ((SelectBaselineOrDeptPanel) buttonPanel.getTablePanel()).getComponent();
            ((SelectBaselineOrDeptDialog) component).dispose();

            SearchBaselineDialog searchBaselineDialog = ((SelectBaselineOrDeptDialog) component).getSearchBaselineDialog();
            if (searchBaselineDialog != null) {
                searchBaselineDialog.dispose();
            }
        } else if (buttonPanel.getTablePanel() instanceof OutFileConditionPanel) {
            OutFileConditionPanel outFileConditionPanel = (OutFileConditionPanel) buttonPanel.getTablePanel();
            AddOutFileDialog addOutFileDialog = outFileConditionPanel.getAddOutFileDialog();
            addOutFileDialog.dispose();
        } else if (buttonPanel.getTablePanel() instanceof ChangeNoticeInfoPanel) {
            ChangeNoticeInfoPanel changeNoticeInfoPanel = (ChangeNoticeInfoPanel) buttonPanel.getTablePanel();
            AddChangeNoticeDialog addChangeNoticeDialog = changeNoticeInfoPanel.getAddChangeNoticeDialog();
            addChangeNoticeDialog.dispose();
        }
    }

    private void sure() {
        if (buttonPanel.getTablePanel() instanceof SelectBaselineOrDeptPanel) {
            SelectBaselineOrDeptPanel panel = (SelectBaselineOrDeptPanel) buttonPanel.getTablePanel();
            String type = panel.getType();
            JTable table = panel.getTable();
            CommonUIUtil.stopTableCellEditing(table);
            List<String> list = new ArrayList<String>();
            String text = "";
            int column = 0;
            //设置技术状态基线
            if (type.equals(PrintConstants.TITLE_DIALOG_BASELINE)) {
                column = 9;
                for (int row = 0; row < table.getRowCount(); row++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 2)));
                    if (isSelect) {
                        String baselineStatus = CommonUtil.objectToString(table.getValueAt(row, 1));
                        list.add(baselineStatus);
                    }
                }
                text = list.toString().substring(1, list.toString().length() - 1);
                //设置分发部门及份数
            } else if (type.equals(PrintConstants.TITLE_DIALOG_DEPT)) {
                column = 14;

                for (int row = 0; row < table.getRowCount(); row++) {
                    String dept = CommonUtil.objectToString(table.getValueAt(row, 1));
                    String count = CommonUtil.objectToString(table.getValueAt(row, 2));
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 0)));
                    if (isSelect) {
                        if ("".equals(dept) || "".equals(count)) {
                            CommonUIUtil.showMessageDialog(null, "分布部门或份数不能为空");
                            return;
                        }
                        String value = dept + "-" + count + "份";
                        list.add(value);
                    }
                }
                String repeat = getDeptValue(table);
                if (!repeat.equals("")) {
                    CommonUIUtil.showMessageDialog(null, repeat + "已存在，不允许重复");
                    return;
                }
                text = list.toString().substring(1, list.toString().length() - 1);
            }
            panel.getResourceTable().setValueAt(text, panel.getResourceTable().getSelectedRow(), column);
            panel.getResourceTable().editCellAt(panel.getResourceTable().getSelectedRow(), column);
            ((SelectBaselineOrDeptDialog) panel.getComponent()).dispose();
        } else if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable panel = (FileListTable) buttonPanel.getTablePanel();
            final JTable table = panel.getTable();
            String type = panel.getType();
            String category = panel.getCategory();
            CommonUIUtil.stopTableCellEditing(table);
            //文件添加
            if (type.equals(PrintConstants.TITLE_ADDFILE)) {
                List<CmPrintInfoBean> cmPrintInfoBeans = new ArrayList<CmPrintInfoBean>();
                for (int row = 0; row < table.getRowCount(); row++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                    if (isSelect) {
                        CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                        cmPrintInfoBeans.add(cmPrintInfoBean);

                        List<CmPrintInfoBean> ecnList = cmPrintInfoBean.getEcnList();
                        if (ecnList != null && !ecnList.isEmpty()) {
                            cmPrintInfoBeans.addAll(ecnList);
                        }
                        //添加文件并过滤出工艺文件还要添加更改单
                        //MPMPrintProcessor.addPrintFileType(cmPrintInfoBean, cmPrintInfoBeans);
                    }
                }
                AddFileDialog addFileDialog = (AddFileDialog) panel.getComponent();
                addFileDialog.getParentTablePanel().setAddValues(cmPrintInfoBeans);
                addFileDialog.dispose();
            } else if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {//文件打印申请
                CommonUIUtil.stopTableCellEditing(table);
                //文档条件校验 start
                if (category != null && !"".equals(category)) {//外来和纸质文件校验
                    String result = LocalPrintUtil.checkFileApply(table);
                    if (!"".equals(result)) {
                        CommonUIUtil.showMessageDialog(null, result);
                        return;
                    }
                } else {
                    List<String> docORList = new ArrayList<String>();//通过文档oid校对列表内是否存在相同的文档
                    String containerName = "";//校对存储库是否一致
                    List<CmPrintInfoBean> printInfoBeanList = MPMPrintFileFrame.getPrintInfoBeanList();//修改打印申请加载原来的文件用来判断分发状态
                    List<String> oidList = LocalPrintUtil.buildModifyId(printInfoBeanList);
                    for (int row = 0; row < table.getRowCount(); row++) {
                        CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                        String docOR = cmPrintInfoBean.getDocVR();
                        if (docORList.contains(docOR)) {//校对是否存在相同的文档
                            CommonUIUtil.showMessageDialog(null, "列表内有相同的文档");
                            return;
                        }
                        String deptAndCount = CommonUtil.objectToString(table.getValueAt(row, 8));
                        if ("".equals(deptAndCount)) {//校对是否未设置分发部门和份数
                            CommonUIUtil.showMessageDialog(null, "分布部门或份数不能为空");
                            return;
                        }
                        String fileState = CommonUtil.objectToString(cmPrintInfoBean.getFileState());
                        if (!"未分发".equals(fileState)) {//校对分发状态
                            if (oidList == null || oidList.isEmpty()) {
                                CommonUIUtil.showMessageDialog(null, "编号为“" + CommonUtil.objectToString(cmPrintInfoBean.getFileNumber() + "”的文档分发状态不是未分发"));
                                return;
                            } else {
                                if (!oidList.contains(docOR)) {
                                    CommonUIUtil.showMessageDialog(null, "编号为“" + CommonUtil.objectToString(cmPrintInfoBean.getFileNumber() + "”的文档分发状态不是未分发"));
                                    return;
                                }
                            }
                        }
                        String lifeCycle = CommonUtil.objectToString(cmPrintInfoBean.getLifeCycle());
                        if (!"已批准".equals(lifeCycle)) {//校对受控状态
                            CommonUIUtil.showMessageDialog(null, "编号为“" + CommonUtil.objectToString(cmPrintInfoBean.getFileNumber() + "”的文档受控状态不是已批准"));
                            return;
                        }
                        //校验存储库
                        if ("".equals(containerName)) {
                            containerName = CommonUtil.objectToString(cmPrintInfoBean.getContainerName());
                        }
//                        if (!containerName.equals(CommonUtil.objectToString(cmPrintInfoBean.getContainerName()))) {
//                            CommonUIUtil.showMessageDialog(null, "存在存储库不相同的文档");
//                            return;
//                        }
                        docORList.add(docOR);
                    }
                }
                //文档条件校验 end
                List<CmPrintInfoBean> cmPrintInfoBeans = MPMPrintHelper.getDataFormFileListTable(table, category);
                if (cmPrintInfoBeans.size() == 0) {
                    CommonUIUtil.showMessageDialog(null, "表格为空，请添加打印数据！");
                    return;
                } else {
                    String pboOid = MPMPrintFileFrame.getOid();
                    String result = MPMPrintProcessor.saveGwPrintApplyRecords(cmPrintInfoBeans, pboOid);
                    CommonUIUtil.showMessageDialog(null, result);
                }
                Container component = ((FileListTable) buttonPanel.getTablePanel()).getComponent();
                MPMPrintFileFrame frame = ((FilePrintMainPanel) component).getFrame();
                frame.closeWindow();
            } else if (type.equals(PrintConstants.TITLE_MAINPANEL_ADDREQUEST)) {
                //添加
                String msg = "";
                List<CmPrintInfoBean> temList;
                List<CmPrintInfoBean> cmPrintInfoBeans = new ArrayList<CmPrintInfoBean>();
                for (int row = 0; row < table.getRowCount(); row++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                    if (isSelect) {
                        temList = new ArrayList<CmPrintInfoBean>();
                        CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                        cmPrintInfoBean.setDistributeDeptAndCount("");
                        cmPrintInfoBean.setFileState("未分发");
                        cmPrintInfoBean.setPrintpath("零星");
                        cmPrintInfoBean.setProcessfile(false);
                        cmPrintInfoBean.setAddFormBOM(false);
                        cmPrintInfoBeans.add(cmPrintInfoBean);

                        // 外来文件无需判断是否有电子签名文件
                        if(!"WL".equals(MPMPrintFileFrame.getCategory())
                				&& !FilePrintUtil.getPrintType("WLWJLR").equals(MPMPrintFileFrame.getTypeStr())) {
                        	temList.add(cmPrintInfoBean);
                            System.out.println("=====temList==>>>"+temList);
                            Map<String, byte[]> map = MPMPrintHelper.checkIsHasPrint(temList);
                            if(map.entrySet().size() == 0){
                                if(msg.isEmpty()){
                                    msg = String.valueOf(table.getValueAt(row,2));
                                }else{
                                    msg += "," + String.valueOf(table.getValueAt(row,2));
                                }
                            }
                        }
                    }
                }
                if(!msg.isEmpty()){
                    CommonUIUtil.showMessageDialog(null, msg + "无电子签名文件，无法添加");
                    return;
                }
                table.repaint();
                AddPrintApplicationDialog addPrintApplicationDialog = (AddPrintApplicationDialog) panel.getComponent();
                addPrintApplicationDialog.getParentTablePanel().setAddValues(cmPrintInfoBeans);
                addPrintApplicationDialog.dispose();
            } else if (type.equals(PrintConstants.TITLE_ADDONBOM_PART_RELATE)) {
                //基于BOM添加
                int selectRow = 0;
                String technicsName = "";
                for (int row = 0; row < table.getRowCount(); row++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                    if (isSelect) {
                        selectRow++;
                        CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                        technicsName = cmPrintInfoBean.getMainTechnics();
                        if (!"".equals(technicsName)) {
                            break;
                        }
                    }
                }
                if (selectRow == 0) {
                    CommonUIUtil.showMessageDialog(null, "未选择条目进行添加");
                    return;
                }
                List<CmPrintInfoBean> cmPrintInfoBeans = new ArrayList<CmPrintInfoBean>();
                for (int row = 0; row < table.getRowCount(); row++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                    if (isSelect) {
                        CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                        cmPrintInfoBean.setDistributeDeptAndCount("");
                        cmPrintInfoBean.setFileState("未分发");
                        cmPrintInfoBean.setPrintpath("零星");
                        cmPrintInfoBean.setProcessfile(false);
                        cmPrintInfoBean.setAddFormBOM(true);
                        cmPrintInfoBean.setMainTechnics(technicsName);
                        cmPrintInfoBeans.add(cmPrintInfoBean);
                    }
                }
                AddFileOnBomDialog addFileOnBomDialog = (AddFileOnBomDialog) panel.getComponent();
                addFileOnBomDialog.getParentTablePanel().setAddValues(cmPrintInfoBeans);
                addFileOnBomDialog.dispose();
            } else if (type.equals(PrintConstants.TITLE_MAINPANEL_LQZZWJ)) {
                FilePrintMainPanel parentPanel = (FilePrintMainPanel) panel.getComponent();
                RecipientsInfoPanel recipientsInfoPanel = parentPanel.getRecipientsInfoPanel();
                CmDistributionBean cmDistributionBean = recipientsInfoPanel.getConditionValues();
                String oid = MPMPrintFileFrame.getOid();
                String userName = cmDistributionBean.getReceiveFile();
                CmDistributionBean newCmDistributionBean = MPMPrintProcessor.getReceiveMessage(cmDistributionBean.getReceiveFile());
                List<CmPrintInfoBean> list = MPMPrintProcessor.getReceiveData(userName, oid, category);
                recipientsInfoPanel.setValue(newCmDistributionBean);
                panel.setUIValues(list);
            } else if (type.equals(PrintConstants.TITLE_MAINPANEL_JGYZGL)) {
                ArrayList<CmSealBean> list = new ArrayList<CmSealBean>();
                StringBuffer checkBatch = new StringBuffer();
                String containerName = "";
                for (int row = 0; row < table.getRowCount(); row++) {
                    String addSeal = CommonUtil.objectToString(table.getValueAt(row, 11));
                    if ("".equals(addSeal)) {
                        CommonUIUtil.showMessageDialog(null, "设置加盖印章不能为空");
                        return;
                    }
                    CmPrintInfoBean printInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                    String sealPlus = CommonUtil.objectToString(printInfoBean.getSealPlus());
                    String pboOid = CommonUtil.objectToString(printInfoBean.getPbooid());
                    String fileNumber = printInfoBean.getFileNumber();
                    String oldPboOid = MPMPrintFileFrame.getOid();
                    if ("".equals(containerName)) {
                        containerName = CommonUtil.objectToString(printInfoBean.getContainerName());
                    }
                    if (!containerName.equals(CommonUtil.objectToString(printInfoBean.getContainerName()))) {
                        CommonUIUtil.showMessageDialog(null, "存在存储库不相同的文档");
                        return;
                    }
                    if (!"".equals(sealPlus) && !"".equals(pboOid) && !pboOid.equals(oldPboOid)) {
                        checkBatch.append("编号：" + fileNumber + "文件已存在加盖印章申请流程中，请先完成上个申请流程后再进行加盖申请！" + "\n");
                    }
					/*else if(addSeal.contains(",")){
						checkBatch.append("编号："+fileNumber+"文件存在加盖多个印章，一次加盖印章申请只能加盖一个印章！"+"\n");
					}*/
                    String oid = CommonUtil.objectToString(table.getValueAt(row, 0));
                    CmSealBean cmSealBean = new CmSealBean();
                    cmSealBean.setGwKeyId(oid);
                    cmSealBean.setName(addSeal);
                    list.add(cmSealBean);
                }
                if (!"".equals(containerName) && !"WL".equals(category) && !"ZZ".equals(category)) {
                    boolean checkContainer = MPMPrintProcessor.checkContainerRole(containerName);
                    if (!checkContainer) {
                        CommonUIUtil.showMessageDialog(null, "当前用户在列表内文档的产品库内不是主任工艺师,无法进行此操作");
                        return;
                    }
                }
                if (!"".equals(checkBatch.toString())) {
                    CommonUIUtil.showMessageDialog(null, checkBatch.toString());
                    return;
                }
                if (list == null || list.isEmpty()) {
                    CommonUIUtil.showMessageDialog(null, "列表内没有文件");
                    return;
                }
                String oid = MPMPrintFileFrame.getOid();
                String result = MPMPrintProcessor.updatePrintAddSeal(list, oid);
                CommonUIUtil.showMessageDialog(null, result);
                Container component = ((FileListTable) buttonPanel.getTablePanel()).getComponent();
                MPMPrintFileFrame frame = ((FilePrintMainPanel) component).getFrame();
                frame.closeWindow();
            } else if (type.equals(PrintConstants.TITLE_MAINPANEL_JGYZQR)) {
                //加盖印章确认 代码待完善 by zhuhao 20180124
                if ("JC".equals(category)) {
                    for (int row = 0; row < table.getRowCount(); row++) {
                        boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                        if (isSelect) {
                            CmPrintInfoBean printInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                            if ("".equals(CommonUtil.objectToString(printInfoBean.getSealPlus()))) {
                                CommonUIUtil.showMessageDialog(null, "不可以重复确认");
                                return;
                            }
                            String id = CommonUtil.objectToString(printInfoBean.getOid());
                            String batch = CommonUtil.objectToString(printInfoBean.getTemporarySeal());
                            String addBatch = CommonUtil.objectToString(printInfoBean.getSealPlus());
                            if ("".equals(addBatch)) {
                                CommonUIUtil.showMessageDialog(null, "加盖印章为空，不需要确认");
                                return;
                            }
                            String allBatch = "";
                            if (batch != null && !"".equals(batch)) {
                                allBatch = batch + "," + addBatch;
                            } else {
                                allBatch = addBatch;
                            }
                            MPMPrintProcessor.saveAddSealPlus(id, allBatch);
                            table.setValueAt(allBatch, row, 9);
                            table.setValueAt("", row, 10);
                        }
                    }
                } else {
                    Container component = ((FileListTable) buttonPanel.getTablePanel()).getComponent();
                    MPMPrintFileFrame frame = ((FilePrintMainPanel) component).getFrame();
                    frame.closeWindow();
                }
            } else if (type.equals(PrintConstants.TITLE_ADDONPROCESSDIRECTORY)) {
                //基于工艺文件目录添加
                final int row = table.getSelectedRow();
                if (row == -1) {
                    CommonUIUtil.showMessageDialog(null, "未选择行！");
                    return;
                }
                final AddFileOnProcessDirectoryDialog addFileOnProcessDirectoryDialog = (AddFileOnProcessDirectoryDialog) panel.getComponent();
                final VaActionProgressBar progressBar = new VaActionProgressBar(
                        addFileOnProcessDirectoryDialog, "搜索", "正在搜索,请等待...", "搜索中");
                Thread thread = new Thread() {
                    public void run() {
                        CmPrintInfoBean cmPrintInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                        List<CmPrintInfoBean> cmPrintInfoBeans = new ArrayList<CmPrintInfoBean>();
                        cmPrintInfoBeans = MPMPrintProcessor.getProcessFiles(cmPrintInfoBean);
                        if (cmPrintInfoBeans == null || cmPrintInfoBeans.isEmpty()) {
                            return;
                        }
                        List<CmPrintInfoBean> cmPrintInfoBeanList = new ArrayList<CmPrintInfoBean>();
                        for(CmPrintInfoBean cmPrintInfoBean1 : cmPrintInfoBeans){
                            cmPrintInfoBean1.setDistributeDeptAndCount("");
                            cmPrintInfoBean1.setFileState("未分发");
                            cmPrintInfoBeanList.add(cmPrintInfoBean1);
                        }
                        addFileOnProcessDirectoryDialog.getParentTablePanel().setAddValues(cmPrintInfoBeanList);
                        addFileOnProcessDirectoryDialog.dispose();
                        progressBar.finish();
                        progressBar.setVisible(false);
                    }
                };
                thread.start();
                progressBar.setVisible(true);
            } else if (type.equals(PrintConstants.TITLE_MAINPANEL_WJBDSQ)) {
                String pboOid = "";
                List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
                for (int row = 0; row < table.getRowCount(); row++) {
                    CmPrintInfoBean printInfoBean = null;
                    String value = CommonUtil.objectToString(table.getValueAt(row, 9));
                    if (!"".equals(value)) {
                        printInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                        if ("".equals(pboOid)) {
                            pboOid = printInfoBean.getPbooid();
                        }
                        if (pboOid == null || "".equals(pboOid)) {
                            CommonUIUtil.showMessageDialog(null, "名称为：“" + printInfoBean.getFileName() + "”的文件没有打印申请记录，无法补打！");
                            return;
                        }
                        if (!pboOid.equals(printInfoBean.getPbooid())) {
                            CommonUIUtil.showMessageDialog(null, "申请单号必须一致");
                            return;
                        }
                        printInfoBean.setOffSet(value);
                        list.add(printInfoBean);
                    } else {
                        CommonUIUtil.showMessageDialog(null, "请先设置补打信息");
                        return;
                    }
                }
                if (list == null || list.isEmpty()) {
                    CommonUIUtil.showMessageDialog(null, "请先设置补打信息");
                    return;
                }
                String result = MPMPrintProcessor.startPrintOffSet(list);
                CommonUIUtil.showMessageDialog(null, result);
                Container component = ((FileListTable) buttonPanel.getTablePanel()).getComponent();
                MPMPrintFileFrame frame = ((FilePrintMainPanel) component).getFrame();
                frame.closeWindow();
            } else if (type.equals(PrintConstants.TITLE_MAINPANEL_ADDWJBDSQ)) {
                List<CmPrintInfoBean> list = new ArrayList<CmPrintInfoBean>();
                for (int row = 0; row < table.getRowCount(); row++) {
                    boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                    if (isSelect) {
                        CmPrintInfoBean printInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                        printInfoBean.setProcessfile(false);
                        list.add(printInfoBean);
                    }
                }
                AddPrintInforDialog addPrintInforDialog = (AddPrintInforDialog) panel.getComponent();
                addPrintInforDialog.getParentTablePanel().setAddValues(list);
                addPrintInforDialog.dispose();
            } else {
                //分发部门及分数设置验证
                for (int row = 0; row < table.getRowCount(); row++) {
                    String deptAndCount = CommonUtil.objectToString(table.getValueAt(row, 14));
                    if ("".equals(deptAndCount)) {
                        CommonUIUtil.showMessageDialog(null, "分发部门或份数不能为空");
                        return;
                    }
                }
                List<CmPrintInfoBean> cmPrintInfoBeans = MPMPrintHelper.getCmPrintInfoFormFileListTable(panel.getTable());
                if (cmPrintInfoBeans.size() == 0) {
                    CommonUIUtil.showMessageDialog(null, "表格为空，请添加待打印数据！");
                } else {
                    String result = MPMPrintHelper.createGwPrintApplyRecords(cmPrintInfoBeans);
                    CommonUIUtil.showMessageDialog(null, result);
                    Container component = panel.getComponent();
                    if (component instanceof FilePrintMainPanel) {
                        MPMPrintFileFrame frame = ((FilePrintMainPanel) component).getFrame();
                        frame.closeWindow();
                    }
                }
            }
        } else if (buttonPanel.getTablePanel() instanceof OutFileConditionPanel) {
            OutFileConditionPanel outFileConditionPanel = (OutFileConditionPanel) buttonPanel.getTablePanel();
            String category = outFileConditionPanel.getCategory();
            boolean isModify = outFileConditionPanel.isModify();
            String id = "";
            if (outFileConditionPanel.getCmPrintInfoBean() != null) {
                id = outFileConditionPanel.getCmPrintInfoBean().getOid();
            }
            CmPrintQueryBean cmPrintQueryBean = outFileConditionPanel.getConditionValues();
            boolean isture = LocalPrintUtil.buildQueryBean(cmPrintQueryBean);
            if (!isture) {
                CommonUIUtil.showMessageDialog(null, "所填内容不能为空");
                return;
            }
            //对version进行校验
            boolean versionFormat = MPMPrintHelper.verifyAllVersionDataFormat(cmPrintQueryBean.getVersion());
            if (!versionFormat) {
                CommonUIUtil.showMessageDialog(null, "版本填写格式错误，请重新填写！");
                return;
            }
            if (!isModify) {
                if (MPMPrintProcessor.checkRepeatOutFile(cmPrintQueryBean, category)) {
                    CommonUIUtil.showMessageDialog(null, "编号为“" + cmPrintQueryBean.getFileNumber() + "”的文档已经被录入，不能重复录入");
                    return;
                }
            }
            try {
                CmPrintInfoBean cmPrintInfoBean = PrintToWCIntf.addOutFile(cmPrintQueryBean, isModify, id);
                AddOutFileDialog addOutFileDialog = outFileConditionPanel.getAddOutFileDialog();
                FileListTable fileListTable = addOutFileDialog.getParentTablePanel();
                if (isModify) {
                    CommonUIUtil.showMessageDialog(null, "修改成功！");
                    fileListTable.setUIValues(cmPrintInfoBean);
                } else {
                    CommonUIUtil.showMessageDialog(null, "添加成功！");
                    fileListTable.setAddValues(cmPrintInfoBean);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else if (buttonPanel.getTablePanel() instanceof ChangeNoticeInfoPanel) {
            ChangeNoticeInfoPanel changeNoticeInfoPanel = (ChangeNoticeInfoPanel) buttonPanel.getTablePanel();
            AddChangeNoticeDialog addChangeNoticeDialog = changeNoticeInfoPanel.getAddChangeNoticeDialog();
            AfterChangeInfoPanel afterChangeInfoPanel = addChangeNoticeDialog.getAfterChangeInfoPanel();
            String category = changeNoticeInfoPanel.getCategory();
            //修改
            boolean isModify = changeNoticeInfoPanel.isModify();
            String id = "";
            if (changeNoticeInfoPanel.getCmPrintInfoBean() != null) {
                id = changeNoticeInfoPanel.getCmPrintInfoBean().getOid();
            }
            CmPrintQueryBean cmPrintQueryBean1 = afterChangeInfoPanel.getConditionValues();
            CmPrintQueryBean cmPrintQueryBean2 = changeNoticeInfoPanel.getConditionValues();
            boolean isture = LocalPrintUtil.buildQueryBean(cmPrintQueryBean1);
            boolean isChange = LocalPrintUtil.buildChangeNotice(cmPrintQueryBean2);
            if (!isture || !isChange) {
                CommonUIUtil.showMessageDialog(null, "所填内容不能为空");
                return;
            }
            //对version进行校验
            boolean versionFormat = MPMPrintHelper.verifyAllVersionDataFormat(cmPrintQueryBean1.getVersion());
            if (!versionFormat) {
                CommonUIUtil.showMessageDialog(null, "版本填写格式错误，请重新填写！");
                return;
            }
            if (!isModify) {
                if (MPMPrintProcessor.checkRepeatOutFile(cmPrintQueryBean1, category)) {
                    CommonUIUtil.showMessageDialog(null, "编号为“" + cmPrintQueryBean1.getFileNumber() + "”的文档已经被录入，不能重复录入");
                    return;
                }
            }
            try {
                CmPrintInfoBean cmPrintInfoBean = PrintToWCIntf.addOutFile(cmPrintQueryBean1, isModify, id);
                MPMPrintProcessor.saveChangeNoticeInfo(cmPrintQueryBean2, cmPrintQueryBean1.getChangeNoticeID(), isModify);
                cmPrintInfoBean.setChangeNoticeNumber(cmPrintQueryBean2.getFileNumber());
                cmPrintInfoBean.setChangeDate(cmPrintQueryBean2.getChangeData());
                cmPrintInfoBean.setChangeContent(cmPrintQueryBean2.getChangeContent());
                cmPrintInfoBean.setViewChange(cmPrintQueryBean2.getViewChange());
                FileListTable fileListTable = addChangeNoticeDialog.getParentTablePanel();
                if (isModify) {
                    CommonUIUtil.showMessageDialog(null, "修改成功！");
                    fileListTable.setUIValues(cmPrintInfoBean);
                } else {
                    CommonUIUtil.showMessageDialog(null, "添加成功！");
                    fileListTable.setAddValues(cmPrintInfoBean);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void view() {
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable panel = (FileListTable) buttonPanel.getTablePanel();
            JTable table = panel.getTable();
//			String type = panel.getType();
//			String pboOid = MPMPrintFileFrame.getOid();
            List<CmAttachment> attachList = null;
            Map<String, String> map = new HashMap<String, String>();
            Map<String, String> deptmap = new HashMap<String, String>();
            //自行打印，需要先生成二维码，并签名PDF
            List<String> list = new ArrayList<String>();
            for (int row = 0; row < table.getRowCount(); row++) {
                boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                if (isSelect) {
                    CmPrintInfoBean printInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                    String QRName = CommonUtil.objectToString(printInfoBean.getQrCode());//二维码
                    if (QRName.contains("/")) {
                        QRName = QRName.replace("/", "~");
                    }
                    String batch = CommonUtil.objectToString(printInfoBean.getTemporarySeal());//批次
                    String dept = CommonUtil.objectToString(printInfoBean.getDistributeDeptAndCount());//批次
                    String oid = CommonUtil.objectToString(printInfoBean.getDocVR());
                    String id = oid + "|" + QRName;
                    list.add(id);
                    map.put(id, batch);
                    deptmap.put(id, dept);
                }
            }
            if (list == null || list.isEmpty()) {
                CommonUIUtil.showMessageDialog(null, "请先选择行");
                return;
            }
            attachList = MPMPrintHelper.getPdfFiles(list, MPMPrintFileFrame.getOid());
//				System.out.println("size1:"+attachList.size());
//				System.out.println("size2:"+beanList.size());
            String pdfTempPath = FileUtil.makeTmpDir(PrintConstants.FOLDER_PDFTEMP);
            byte[] sealByte = MPMPrintProcessor.getImageByte("shoukong.jpg");
            String sealPath = pdfTempPath + File.separator + "shoukong.jpg";//受控章
            FileUtil.writeBytes(sealPath, sealByte);
            StringBuffer buf = new StringBuffer();
            for (CmAttachment attachment : attachList) {
                String oid = attachment.getNumber();
                String fileName = attachment.getFileName();
                String fileType = attachment.getTemplateType();
                String filePath = pdfTempPath + File.separator + fileName;//pdf路径
                FileUtil.writeBytes(filePath, attachment.getBytes());//向临时文件夹写入PDF
                String QRName = fileName.substring(0, fileName.indexOf("_"));
                if (QRName.contains("/")) {
                    QRName = QRName.replace("/", "~");
                }
                String batch = map.get(oid + "|" + QRName);
                String dept = deptmap.get(oid + "|" + QRName);
                byte[] imgs = MPMPrintProcessor.getImageByte(QRName);
                String Qr = pdfTempPath + File.separator + QRName + ".jpg";
                FileUtil.writeBytes(Qr, imgs);
                LocalPrintUtil.generatePDF(filePath, Qr, sealPath, batch, fileType, QRName, dept);
                try {
                    Runtime.getRuntime().exec("rundll32.exe url.dll,FileProtocolHandler  " + filePath);
                } catch (Exception e1) {
                    buf.append(attachment.getFileName() + ",");
                }
            }
            if (buf.toString().length() > 1) {
                CommonUIUtil.showMessageDialog(null, "打开" + buf.toString().substring(0, buf.toString().length() - 1) + "文件出错！");
            }
        }
    }

    private void print() {
        if (buttonPanel.getTablePanel() instanceof FileListTable) {
            FileListTable panel = (FileListTable) buttonPanel.getTablePanel();
            JTable table = panel.getTable();
//			String type = panel.getType();
            CommonUIUtil.stopTableCellEditing(table);
            //判断是否符合打印条件 start
            int count = 0;
            for (int row = 0; row < table.getRowCount(); row++) {
                boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                if (isSelect) {
                    String state = CommonUtil.objectToString(table.getValueAt(row, 10));
                    if (!"未打印".equals(state)) {
                        CommonUIUtil.showMessageDialog(null, "已打印文件不能重复打印");
                        return;
                    }
                    count++;
                }
            }
            if (count < 1) {
                CommonUIUtil.showMessageDialog(null, "请先选择行");
                return;
            }
            int flag = CommonUIUtil.showConfirmDialog(null, "是否打印", "");
            if (flag != 0) {
                return;
            }
            //判断是否符合打印条件 end
            List<CmAttachment> attachList = null;
            Map<String, String> map = new HashMap<String, String>();
            Map<String, String> deptmap = new HashMap<String, String>();
//			List<CmPrintInfoBean> beanList = null;
            List<String> list = new ArrayList<String>();
            Set<String> secrets = new HashSet<>();
            for (int row = 0; row < table.getRowCount(); row++) {
                boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                if (isSelect) {
                    CmPrintInfoBean printInfoBean = (CmPrintInfoBean) table.getValueAt(row, table.getColumnCount() - 1);
                    String batch = CommonUtil.objectToString(printInfoBean.getTemporarySeal());
                    String dept = CommonUtil.objectToString(printInfoBean.getDistributeDeptAndCount());
                    String oid = CommonUtil.objectToString(printInfoBean.getDocVR());
                    String QRName = CommonUtil.objectToString(printInfoBean.getQrCode());
                    if (QRName.contains("/")) {
                        QRName = QRName.replace("/", "~");
                    }
                    String id = oid + "|" + QRName;
                    list.add(id);
                    map.put(id, batch);
                    deptmap.put(id, dept);
//					String printId = printInfoBean.getOid();
//					beanList = MPMPrintProcessor.getQRbarcode(printId);
                    String secret = CommonUtil.objectToString(printInfoBean.getSecret());
                    if(secret != null && !"".equals(secret)) {
                        secrets.add(secret);
                    }
                }
            }
            attachList = MPMPrintHelper.getPdfFiles(list, MPMPrintFileFrame.getOid());
            String pdfTempPath = FileUtil.makeTmpDir(PrintConstants.FOLDER_PDFTEMP);
            List<String> pdfList = new ArrayList<String>();
            byte[] sealByte = MPMPrintProcessor.getImageByte("shoukong.jpg");
            String sealPath = pdfTempPath + File.separator + "shoukong.jpg";//受控章
            FileUtil.writeBytes(sealPath, sealByte);
            int countPages = 0;
            String mergePdfName = null;
            for (CmAttachment attachment : attachList) {
                String oid = attachment.getNumber();
                String fileName = attachment.getFileName();
                String fileType = attachment.getTemplateType();
                String QRName = fileName.substring(0, fileName.indexOf("_"));
                if (QRName.contains("/")) {
                    QRName = QRName.replace("/", "~");
                }
                String batch = map.get(oid + "|" + QRName);
                String dept = deptmap.get(oid + "|" + QRName);
                String filePath = pdfTempPath + File.separator + fileName;//pdf路径
                FileUtil.writeBytes(filePath, attachment.getBytes());
                byte[] imgs = MPMPrintProcessor.getImageByte(QRName);
                String Qr = pdfTempPath + File.separator + QRName + ".jpg";
                FileUtil.writeBytes(Qr, imgs);
                int countPage = LocalPrintUtil.generatePDF(filePath, Qr, sealPath, batch, fileType, QRName, dept);
                countPages = countPages + countPage;
                pdfList.add(filePath);

                if(mergePdfName==null) {
                    if(secrets.size() == 1) {
                        mergePdfName = QRName + "_sign（" + secrets.iterator().next() + "）.pdf";
                    } else {
                        if(secrets.contains("机密")) {
                            mergePdfName = QRName + "_sign（机密）.pdf";
                        } else if(secrets.contains("秘密")) {
                            mergePdfName = QRName + "_sign（秘密）.pdf";
                        } else if(secrets.contains("内部")) {
                            mergePdfName = QRName + "_sign（内部）.pdf";
                        } else if(secrets.contains("公开") || secrets.contains("无")) {
                            mergePdfName = QRName + "_sign（公开）.pdf";
                        } else {
                            mergePdfName = QRName + "_sign.pdf";
                        }
                    }
                }
            }
            if (pdfList.size() > 1 && countPages > 500) {
                CommonUIUtil.showMessageDialog(null, "打印失败！" + "\n" + "原因：所选择打印文件的总页数超过500页，不允许打印，请重新选择后再执行打印！");
                return;
            }
            try {
                String mergePdfFilePath = FilePrintUtil.mergePdf(pdfList,mergePdfName);
                FilePrintUtil.print(mergePdfFilePath);
                setPrintStatus("已打印");
            } catch (IOException e) {
                CommonUIUtil.showMessageDialog(null, "打印失败！");
                e.printStackTrace();
            } catch (PrinterException e) {
                CommonUIUtil.showMessageDialog(null, "打印失败！");
                e.printStackTrace();
            } catch (Exception e) {
                CommonUIUtil.showMessageDialog(null, "打印失败！");
                e.printStackTrace();
            }
        }
    }

    public String getDeptValue(JTable table) {
        String repeat = "";
        List<String> list = new ArrayList<String>();
        for (int row = 0; row < table.getRowCount(); row++) {
            String dept = CommonUtil.objectToString(table.getValueAt(row, 1));
            boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 0)));
            if (isSelect) {
                if (!dept.equals("")) {
                    if (list.contains(dept)) {
                        if (!repeat.equals("")) {
                            repeat = repeat + "," + dept;
                        } else {
                            repeat = dept;
                        }
                    } else {
                        list.add(dept);
                    }
                }
            }
        }
        return repeat;
    }

    private void search() {
        if (buttonPanel.getTablePanel() instanceof SelectBaselineOrDeptPanel) {
            SelectBaselineOrDeptPanel panel = (SelectBaselineOrDeptPanel) buttonPanel.getTablePanel();
            String type = panel.getType();
            JTable table = panel.getTable();
            if (type.equals(PrintConstants.TITLE_DIALOG_BASELINE)) {
                if (searchBaselineDialog != null && searchBaselineDialog.isVisible()) {
                    searchBaselineDialog.requestFocus();
                } else {
                    searchBaselineDialog = new SearchBaselineDialog(table);
                }
                ((SelectBaselineOrDeptDialog) panel.getComponent()).setSearchBaselineDialog(searchBaselineDialog);
            }
        }
    }

    private void allDeleteSeal() {
        JPanel tablePanel = buttonPanel.getTablePanel();
        if (tablePanel instanceof FileListTable) {
            FileListTable fileListTable = (FileListTable) tablePanel;
            String type = fileListTable.getType();
            JTable table = fileListTable.getTable();
            if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
                if (table != null) {
                    CommonUIUtil.stopTableCellEditing(table);
                    for (int row = 0; row < table.getRowCount(); row++) {
                        boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                        if (isSelect) {
                            table.setValueAt("", row, 7);
                        }
                    }
                }
            }
        }
    }

    private void allDeleteDept() {
        JPanel tablePanel = buttonPanel.getTablePanel();
        if (tablePanel instanceof FileListTable) {
            FileListTable fileListTable = (FileListTable) tablePanel;
            String type = fileListTable.getType();
            JTable table = fileListTable.getTable();
            if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
                if (table != null) {
                    CommonUIUtil.stopTableCellEditing(table);
                    for (int row = 0; row < table.getRowCount(); row++) {
                        boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                        if (isSelect) {
                            table.setValueAt("", row, 8);
                        }
                    }
                }
            }
        }
    }

    private void allSetSeal() {
        JPanel tablePanel = buttonPanel.getTablePanel();
        if (tablePanel instanceof FileListTable) {
            FileListTable fileListTable = (FileListTable) tablePanel;
            String type = fileListTable.getType();
            JTable table = fileListTable.getTable();
            if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
                if (table != null) {
                    CommonUIUtil.stopTableCellEditing(table);
                    boolean checkSelect = false;
                    for (int row = 0; row < table.getRowCount(); row++) {
                        boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                        if (isSelect) {
                            checkSelect = isSelect;
                            break;
                        }
                    }
                    if (!checkSelect) {
                        CommonUIUtil.showMessageDialog(null, "请您勾选需要批量添加的列表后，再点击该按钮！");
                        return;
                    }
                    if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
                        new SelectBaselineOrDeptDialog(PrintConstants.TITLE_ALLDIALOG_SEAL, table, -1);
                    }
                }
            }
        }
    }

    private void allSetDept() {
        JPanel tablePanel = buttonPanel.getTablePanel();
        if (tablePanel instanceof FileListTable) {
            FileListTable fileListTable = (FileListTable) tablePanel;
            String type = fileListTable.getType();
            JTable table = fileListTable.getTable();
//			int rowCount = table.getRowCount();
            if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
                if (table != null) {
                    CommonUIUtil.stopTableCellEditing(table);
                    boolean checkSelect = false;
                    for (int row = 0; row < table.getRowCount(); row++) {
                        boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(row, 1)));
                        if (isSelect) {
                            checkSelect = isSelect;
                            break;
                        }
                    }
                    if (!checkSelect) {
                        CommonUIUtil.showMessageDialog(null, "请您勾选需要批量添加的列表后，再点击该按钮！");
                        return;
                    }
                    if (type.equals(PrintConstants.TITLE_MAINPANEL_REQUEST)) {
                        new SelectBaselineOrDeptDialog(PrintConstants.TITLE_ALLDIALOG_DEPT, table, -1);
                    }
                }
            }
        }
    }

    private void allSetData() {
        JPanel tablePanel = buttonPanel.getTablePanel();
        if (tablePanel instanceof SelectBaselineOrDeptPanel) {
            SelectBaselineOrDeptPanel panel = (SelectBaselineOrDeptPanel) tablePanel;
            String type = panel.getType();
            if (type.equals(PrintConstants.TITLE_ALLDIALOG_DEPT)) {
                Container component = panel.getComponent();
                JTable table = null;
                table = panel.getTable();
                CommonUIUtil.stopTableCellEditing(table);
                JTable filetable = null;
                if (component instanceof SelectBaselineOrDeptDialog) {
                    filetable = ((SelectBaselineOrDeptDialog) component).getTable();
                }
                if (filetable != null) {
                    for (int row = 0; row < filetable.getRowCount(); row++) {
                        boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(filetable.getValueAt(row, 1)));
                        if (isSelect) {
                            String deptAndCount = CommonUtil.objectToString(filetable.getValueAt(row, 8));
                            Map<String, String> deptMap = new HashMap<String, String>();
                            if (!"".equals(deptAndCount) && deptAndCount.contains(":")) {
                                if (deptAndCount.contains(",")) {
                                    String[] deptAndCountList = deptAndCount.split(",");
                                    for (int i = 0; i < deptAndCountList.length; i++) {
                                        String dept = deptAndCountList[i].substring(0, deptAndCountList[i].indexOf(":"));
                                        String count = deptAndCountList[i].substring(deptAndCountList[i].indexOf(":") + 1, deptAndCountList[i].indexOf("份"));
                                        deptMap.put(dept, count);
                                    }
                                } else {
                                    String dept = deptAndCount.substring(0, deptAndCount.indexOf(":"));
                                    String count = deptAndCount.substring(deptAndCount.indexOf(":") + 1, deptAndCount.indexOf("份"));
                                    deptMap.put(dept, count);
                                }
                            }
                            String value = "";
                            for (int i = 0; i < table.getRowCount(); i++) {
                                String dept = (String) table.getValueAt(i, 1);
                                String count = (String) table.getValueAt(i, 2);
                                if (deptMap.containsKey(dept)) {
                                    String oldCount = deptMap.get(dept);
                                    if (!"".equals(count) && count != null) {
                                        count = String.valueOf(Integer.valueOf(oldCount) + Integer.valueOf(count));
                                    } else {
                                        count = oldCount;
                                    }
                                }
                                if (!"".equals(count) && count != null) {
                                    if ("".equals(value)) {
                                        value = dept + ":" + count + "份";
                                    } else {
                                        value = value + "," + dept + ":" + count + "份";
                                    }
                                }
                            }
                            filetable.setValueAt(value, row, 8);
                        }
                    }
                    ((SelectBaselineOrDeptDialog) component).dispose();
                }
            } else if (type.equals(PrintConstants.TITLE_ALLDIALOG_SEAL)) {
                Container component = panel.getComponent();
                JTable table = null;
                table = panel.getTable();
                CommonUIUtil.stopTableCellEditing(table);
                JTable filetable = null;
                if (component instanceof SelectBaselineOrDeptDialog) {
                    filetable = ((SelectBaselineOrDeptDialog) component).getTable();
                }
                if (filetable != null) {
                    for (int row = 0; row < filetable.getRowCount(); row++) {
                        boolean isSelect = Boolean.parseBoolean(CommonUtil.objectToString(filetable.getValueAt(row, 1)));
                        if (isSelect) {
                            String oldSeal = CommonUtil.objectToString(filetable.getValueAt(row, 7));
                            Map<String, String> sealMap = new HashMap<String, String>();
                            if (!"".equals(oldSeal)) {
                                if (oldSeal.contains(",")) {
                                    String[] sealtList = oldSeal.split(",");
                                    for (int i = 0; i < sealtList.length; i++) {
                                        sealMap.put(sealtList[i], sealtList[i]);
                                    }
                                } else {
                                    sealMap.put(oldSeal, oldSeal);
                                }
                            }
                            String value = "";
                            for (int i = 0; i < table.getRowCount(); i++) {
                                boolean isTableSelect = Boolean.parseBoolean(CommonUtil.objectToString(table.getValueAt(i, 0)));
                                String batch = CommonUtil.objectToString(table.getValueAt(i, 2));
                                if (sealMap.containsKey(batch)) {
                                    isTableSelect = true;
                                }
                                if (isTableSelect) {
                                    if ("".equals(value)) {
                                        value = value + batch;
                                    } else {
                                        value = value + "," + batch;
                                    }
                                }
                            }

                            filetable.setValueAt(value, row, 7);
                        }
                    }
                    ((SelectBaselineOrDeptDialog) component).dispose();
                }
            }
        }
    }
}