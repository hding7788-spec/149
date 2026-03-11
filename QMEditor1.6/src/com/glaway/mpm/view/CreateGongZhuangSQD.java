package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.dom4j.Element;

import com.glaway.mpm.util.DateChooser;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.wcIntf.ResourceIntf;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class CreateGongZhuangSQD extends JDialog{
    private Element parentElement;
    private NewTechnicsPart frame;
    private XWTreeNode treeNode;
    private JPanel panel= new JPanel();;
    private JPanel panelCenter= new JPanel();;
    private JPanel panelSouth= new JPanel();;
    private javax.swing.JButton sure=new JButton("确定");
    private javax.swing.JButton close=new JButton("取消");
    private JTextField bianhao_Value = new JTextField();
    private static JTextField name_value = new JTextField();
//    飞行器部、运载部、武器一部、武器二部、新产品部
    private String[] secret_array = {"飞行器部","运载部","武器一部","武器二部","新产品部"};
    private JComboBox xinghao= new JComboBox(secret_array);
    private JTextField cpxh_value = new JTextField();
    private JComboBox box16 ;
    private JTextField tuhao_value = new JTextField();
    private JTextField chanpinName_value = new JTextField();
    private JTextField gongxuhao_value = new JTextField();
    private JTextField gongxuName_value = new JTextField();
    private String[] gongzhuangStyle = {"A","B","C"};
    private JComboBox gongzhuangStyle_value = new JComboBox(gongzhuangStyle);
    private JTextField zhizaoshuliang_value = new JTextField();
    private JTextField shiyongshijian_value = new JTextField();
    private JTextField shejiwanchengshijian_value = new JTextField();
    private JTextField shengchanwanchengshijian_value = new JTextField();
    private JComboBox shijibumen;
    private JTextArea  yongtu=new JTextArea();
    private JTextArea  jishutiaojian=new JTextArea();
    private JTextField fujian_value = new JTextField();
    private File file;
    public CreateGongZhuangSQD(NewTechnicsPart parent, Element element,
            XWTreeNode node) {
            super(parent, true);
            parentElement=element;
            this.frame = parent;
            treeNode=node;
            setTitle("工装申请单");
            Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
            setBounds((int) (dimension2.getWidth() - 500) / 2 , (int) (dimension2.getHeight() - (625)) / 2, 750, 580);
            initComponents();

            setVisible(true);


    }
    private void initComponents() {

        DateChooser dateChooser1 = DateChooser.getInstance("yyyy/MM/dd");
        DateChooser dateChooser2 = DateChooser.getInstance("yyyy/MM/dd");
        Container container = getContentPane();
        JScrollPane scrollPane = new JScrollPane(panel);
        container.add(scrollPane);
        panel.setLayout(new BorderLayout());
        panelCenter.setLayout(new GridBagLayout());
        panel.add(panelCenter,BorderLayout.CENTER);
        panel.add(panelSouth,BorderLayout.SOUTH);

        panelSouth.setLayout(new GridBagLayout());
        panelSouth.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        sure.setPreferredSize(new Dimension(70, 23));
        sure.setMinimumSize(new Dimension(70, 23));
        sure.setMaximumSize(new Dimension(70, 23));
        panelSouth.add(sure, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 15, 15), 0, 0));
        close.setPreferredSize(new Dimension(70, 23));
        close.setMinimumSize(new Dimension(70, 23));
        close.setMaximumSize(new Dimension(70, 23));
        panelSouth.add(close, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
                        5, 5, 15, 15), 0, 0));

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
        String groupName = "";
        box16= new javax.swing.JComboBox(allList.toArray());
        shijibumen= new javax.swing.JComboBox(allList.toArray());
        try {
            groupName = TechnicsIntf.getUsertechnicsGroupName();
        } catch (RemoteException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        } catch (InvocationTargetException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        if(allList.contains(groupName)) {
            box16.setSelectedItem(groupName);
            shijibumen.setSelectedItem(groupName);
        }


        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(5, 5, 0, 0);
        gridBagConstraints.gridwidth = 15;

        JLabel bianhao_label = new JLabel("编号");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(bianhao_label, gridBagConstraints);

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format2 = format.format(date);

        bianhao_Value.setText(format2);
        bianhao_Value.setEditable(false);
        bianhao_Value.setPreferredSize(new Dimension(100, 23));
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(bianhao_Value, gridBagConstraints);

        JLabel name_label = new JLabel("名称");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(name_label, gridBagConstraints);

        name_value.setText(bianhao_Value.getText()+"工装申请单");
        name_value.setEditable(false);
        name_value.setPreferredSize(new Dimension(100, 23));
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(name_value, gridBagConstraints);

        JLabel xinghao_label = new JLabel("型号");
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(xinghao_label, gridBagConstraints);

        xinghao.setPreferredSize(new Dimension(100, 23));
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(xinghao, gridBagConstraints);

        JLabel chanpinxinghao_label = new JLabel("产品型号");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(chanpinxinghao_label, gridBagConstraints);

        cpxh_value.setPreferredSize(new Dimension(100, 23));
        cpxh_value.setText("");
        cpxh_value.setEditable(false);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(cpxh_value, gridBagConstraints);

        JLabel shiyongbumen_label = new JLabel("使用部门");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(shiyongbumen_label, gridBagConstraints);

        box16.setPreferredSize(new Dimension(100, 23));
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(box16, gridBagConstraints);

        JLabel tuhao_label = new JLabel("产品图号");
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(tuhao_label, gridBagConstraints);

        tuhao_value.setPreferredSize(new Dimension(100, 23));
        tuhao_value.setText("");
        tuhao_value.setEditable(false);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(tuhao_value, gridBagConstraints);

        JLabel chanpinName_label = new JLabel("产品名称");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(chanpinName_label, gridBagConstraints);

        chanpinName_value.setPreferredSize(new Dimension(100, 23));
        chanpinName_value.setText("");
        chanpinName_value.setEditable(false);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(chanpinName_value, gridBagConstraints);

        JLabel gongxuhao_label = new JLabel("工序号");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(gongxuhao_label, gridBagConstraints);

        gongxuhao_value.setPreferredSize(new Dimension(100, 23));
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(gongxuhao_value, gridBagConstraints);

        JLabel gongxuName_label = new JLabel("工序名称");
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(gongxuName_label, gridBagConstraints);

        gongxuName_value.setPreferredSize(new Dimension(100, 23));
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(gongxuName_value, gridBagConstraints);

        JLabel gongzhuangStyle_label = new JLabel("工装类别");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(gongzhuangStyle_label, gridBagConstraints);

        gongzhuangStyle_value.setPreferredSize(new Dimension(100, 23));
        gongzhuangStyle_value.setEditable(false);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(gongzhuangStyle_value, gridBagConstraints);

        JLabel zhizaoShuliang_label = new JLabel("制造数量");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(zhizaoShuliang_label, gridBagConstraints);

        zhizaoshuliang_value.setPreferredSize(new Dimension(100, 23));
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(zhizaoshuliang_value, gridBagConstraints);

        JLabel shiyongshijian_label = new JLabel("使用时间");
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(shiyongshijian_label, gridBagConstraints);

        shiyongshijian_value.setPreferredSize(new Dimension(100, 23));
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(shiyongshijian_value, gridBagConstraints);

        JLabel shejibumen_label = new JLabel("建议设计部门");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(shejibumen_label, gridBagConstraints);

        shijibumen.setPreferredSize(new Dimension(100, 23));
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(shijibumen, gridBagConstraints);

        JLabel shejiwanchengshijian_label = new JLabel("要求设计完成时间");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(shejiwanchengshijian_label, gridBagConstraints);


        shejiwanchengshijian_value.setPreferredSize(new Dimension(100, 23));
        dateChooser1.register(shejiwanchengshijian_value);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(shejiwanchengshijian_value, gridBagConstraints);


        JLabel shengchanwanchengshijian_label = new JLabel("要求生产完成时间");
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(shengchanwanchengshijian_label, gridBagConstraints);

        shengchanwanchengshijian_value.setPreferredSize(new Dimension(100, 23));
        dateChooser2.register(shengchanwanchengshijian_value);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(shengchanwanchengshijian_value, gridBagConstraints);


        JLabel yongtu_label = new JLabel("申请原因及用途");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(yongtu_label, gridBagConstraints);

        yongtu.setPreferredSize(new Dimension(450, 60));
        gridBagConstraints.gridwidth = 14;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(yongtu, gridBagConstraints);

        JLabel jishutioajian_label = new JLabel("工装技术条件和操作要求");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(jishutioajian_label, gridBagConstraints);

        jishutiaojian.setPreferredSize(new Dimension(450, 60));
        gridBagConstraints.gridwidth = 14;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(jishutiaojian, gridBagConstraints);

        JLabel fujian_label = new JLabel("附件");
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(fujian_label, gridBagConstraints);

        fujian_value.setPreferredSize(new Dimension(100, 23));
        fujian_value.setEditable(false);
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new Insets(5, 5, 0, 5);
        panelCenter.add(fujian_value, gridBagConstraints);

        JButton xuanze = new JButton("选择");
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 1;
        panelCenter.add(xuanze, gridBagConstraints);
        xuanze.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setMultiSelectionEnabled(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter(".doc", "doc");
                chooser.setFileFilter(filter);
                int result = chooser.showOpenDialog(frame);
                if(result == JFileChooser.APPROVE_OPTION){
                      file = chooser.getSelectedFile();
                      fujian_value.setText(file.getName());
            }
            }
        });
        sure.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
          HashMap<Object,Object> map = new HashMap<Object, Object>();
          map.put("bianhao", bianhao_Value.getText());
          map.put("mingcheng", name_value.getText());
          map.put("xinghao", xinghao.getSelectedItem());
          map.put("chanpinxinghao", cpxh_value.getText());
          map.put("shiyongbumen", box16.getSelectedItem());
          map.put("chanpintuhao", tuhao_value.getText());
          map.put("chanpinmingcheng", chanpinName_value.getText());
          map.put("gongxuhao", gongxuhao_value.getText());
          map.put("gongxumingcheng", gongxuName_value.getText());
          map.put("gongzhuangleibie", gongzhuangStyle_value.getSelectedItem());
          map.put("zhizaoshuliang", zhizaoshuliang_value.getText());
          map.put("shiyongshijian", shiyongshijian_value.getText());
          map.put("jianyishejibumen", shijibumen.getSelectedItem());
          map.put("yaoqiushejiwanchengshijian", shejiwanchengshijian_value.getText());
          map.put("yaoqiushengchanwanchengshijian", shengchanwanchengshijian_value.getText());
          map.put("shenqingyuanyin", yongtu.getText());
          map.put("jstj", jishutiaojian.getText());
          map.put("file", file);
          XWTreeNode node = frame.getTechnicsTreePanel().getSelectedTreeNode();
          if (node!=null) {
              XWTreeObject object = node.getObject();
              if (object instanceof XWTechnicsTreeObject) {
                  Element technicElement = object.getTreeCellData();
                  map.put("technicsNumber", technicElement.attributeValue("technicsNumber"));
                  map.put("version", technicElement.attributeValue("version"));
            }
        }
          if (file==null) {
              int flag1 = JOptionPane.showConfirmDialog(frame, "确定不选择附件吗？", "确认", JOptionPane.OK_CANCEL_OPTION);
              if (flag1 == JOptionPane.CANCEL_OPTION) {
                  return;
              }
        }
          boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("CreateGongZhuangsqd",
                  new Class[] { Map.class}, new Object[] { map});
          if (flag) {
              JOptionPane.showMessageDialog(frame, "创建工装申请单成功！", "提示",JOptionPane.INFORMATION_MESSAGE);
              dispose();

        }else{
            JOptionPane.showMessageDialog(frame, "创建工装申请单失败！", "提示",JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }

            }
        });
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();


            }
        });

    }
    public static  String getGongZhuangSQDName() {
        return  (String)name_value.getText();

    }
 public static void main(String[] args) {
    CreateGongZhuangSQD createGongZhuangSQD = new CreateGongZhuangSQD(null, null, null);
    createGongZhuangSQD.setVisible(true);
}
}
