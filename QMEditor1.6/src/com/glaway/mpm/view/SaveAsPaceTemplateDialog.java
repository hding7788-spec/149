package com.glaway.mpm.view;

import com.glaway.mpm.controller.StepTemplateCopyHandler;
import com.glaway.mpm.util.WorkSpaceUtil;
import org.dom4j.Element;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SaveAsPaceTemplateDialog extends JDialog implements ActionListener {
    private JTextField textField;
    private String templateName;

    private NewTechnicsPart frame;
    private Element paceElement;
    private int index;

    public SaveAsPaceTemplateDialog(NewTechnicsPart frame, Element pace, int index) {
        super(frame, "存为工步模板", true);
        this.frame = frame;
        paceElement = pace;
        this.index = index;

        setResizable(false);
        setLocationRelativeTo(null);

        getContentPane().setLayout(new GridBagLayout());
        final JLabel label = new JLabel();
        label.setText("模板名称");
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.insets = new Insets(10, 10, 3, 0);
        getContentPane().add(label, gridBagConstraints);

        textField = new JTextField();
        final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
        gridBagConstraints_1.gridx = 1;
        gridBagConstraints_1.gridy = 0;
        gridBagConstraints_1.gridwidth = 2;
        gridBagConstraints_1.ipadx = 253;
        gridBagConstraints_1.insets = new Insets(7, 5, 0, 10);
        getContentPane().add(textField, gridBagConstraints_1);

        final JButton button = new JButton();
        button.setText("确定");
        final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
        gridBagConstraints_2.gridx = 1;
        gridBagConstraints_2.gridy = 1;
        gridBagConstraints_2.ipadx = 12;
        gridBagConstraints_2.insets = new Insets(6, 120, 11, 0);
        getContentPane().add(button, gridBagConstraints_2);
        getRootPane().setDefaultButton(button);

        final JButton button_1 = new JButton();
        button_1.setText("取消");
        final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
        gridBagConstraints_3.gridx = 2;
        gridBagConstraints_3.gridy = 1;
        gridBagConstraints_3.ipadx = 12;
        gridBagConstraints_3.insets = new Insets(6, 6, 11, 10);
        getContentPane().add(button_1, gridBagConstraints_3);

        button.addActionListener(this);
        button_1.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.trim().equals("确定")) {
            templateName = textField.getText();
            templateName = toSemiangle(templateName);

            if (templateName.trim().equals("")) {
                JOptionPane.showMessageDialog(this, "        您还没有输入信息！", "提示",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                okProcess();
            }
        }
        if (cmd.equals("取消")) {
            dispose();
        }
    }

    public void showDialog() {
        setSize(350, 100);
        setVisible(true);
    }

    public static String toSemiangle(String src) {
        if (src == null)
            return null;
        if (src.length() == 0)
            return "";
        char[] c = src.toCharArray();
        for (int index = 0; index < c.length; index++) {
            if (c[index] == 12288) {
                c[index] = (char) 32;
            } else if (c[index] > 65280 && c[index] < 65375) {
                c[index] = (char) (c[index] - 65248);
            }
        }
        return String.valueOf(c);
    }

    private void okProcess() {
        File templateFile = null;
        try {
            String templetDirectory = WorkSpaceUtil.getPaceRootPath();
            File directory = new File(templetDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            if (templetDirectory != null) {
                File file = new File(templetDirectory);
                File[] array = file.listFiles();
                for (int i = 0; i < array.length; i++) {
                    String temp = array[i].getName();
                    if (temp.equalsIgnoreCase(templateName)) {
                        int result = JOptionPane.showConfirmDialog(frame,
                                "已存在的工步模板名，是否覆盖？", "提示",
                                JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.NO_OPTION) {
                            return;
                        } else {
                            WorkSpaceUtil.delete(array[i]);
                            break;
                        }
                    }
                }

                String path = templetDirectory + "\\" + templateName;
                templateFile = new File(path);
                templateFile.mkdirs();
//                /**移除参装件信息*/
//                if (paceElement != null) {
//                    Element parts = paceElement.element("parts");
//                    if (parts != null) {
//                        paceElement.remove(parts);
//                    }
//                }
                // 开始复制具体文件数据
                StepTemplateCopyHandler.createStepTemplate(path, templateName,
                        paceElement);
                dispose();
                JOptionPane.showMessageDialog(this, "另存为工步模板成功！", "提示",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (templateFile != null) {
                try {
                    WorkSpaceUtil.delete(templateFile);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            JOptionPane.showMessageDialog(this, "另存为工步模板出现错误！", "提示",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
