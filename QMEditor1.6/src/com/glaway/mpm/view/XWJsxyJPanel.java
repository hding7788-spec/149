package com.glaway.mpm.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.collections.map.HashedMap;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.glaway.mpm.qmIntf.technics.entity.Image;
import com.glaway.mpm.util.IntfUtil;
import com.glaway.mpm.util.UserUtil;
import com.glaway.mpm.util.WorkSpaceUtil;
import com.glaway.mpm.util.XmlUtility;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;
import com.glaway.mpm.wcIntf.TechnicsIntf;

public class XWJsxyJPanel<E, V> extends JPanel {
    private NewTechnicsPart frame;
    private JPanel panel = new JPanel();
    private JLabel bianhao=new JLabel("编号:");
    private JTextField bianhaoValue=new JTextField();
    private JLabel mingcheng=new JLabel("名称:");
    private JTextField mingchengValue=new JTextField();
    private JLabel bianzhibumen=new JLabel("编制部门:");
    private JTextField bianzhibumenValue=new JTextField();
    private JLabel xinghaodaihao=new JLabel("型号代号:");
    private JTextField xinghaodaihaoValue=new JTextField();
    private JLabel chanpindaihao=new JLabel("产品代号:");
    private JTextField chanpindaihaoValue=new JTextField();
    private JLabel bujiandaihao=new JLabel("部件代号:");
    private JTextField bujiandaihaoValue=new JTextField();
    private JLabel bujianmingcheng=new JLabel("部件名称:");
    private JTextField bujianmingchengValue=new JTextField();
    private String[] jieduan={"Y","M","C","S","Z","D","G","P","-"};
    final JComboBox jieduanbiaojiValue = new JComboBox(jieduan);
    private JLabel jieduanbiaoji=new JLabel("阶段标记:");
    private JLabel pici=new JLabel("批次:");
    private JComboBox piciValue;
    private JLabel miji=new JLabel("密级:");
    private String[] secret_array = {"公开","内部"};
//    private String[] secret_array = {"公开","内部","秘密★10年","机密★20年"};
    final JComboBox mijiValue = new JComboBox(secret_array);
    private JLabel creator=new JLabel("创建者:");
    public JTextField creatorValue=new JTextField();
    private JLabel state=new JLabel("生命周期状态:");
    private JTextField stateValue=new JTextField();
    private  Map map;
    private JLabel xyjsxy=new JLabel("当前系统中的技术协议:");
    private JButton chakanButton=new JButton("查看");
    private JLabel newJsxy=new JLabel("重新选择技术协议:");
    private JTextField newJsxyValue=new JTextField();
    private JButton xuanzeButton=new JButton("选择");
    private JButton saveButton=new JButton("保   存");
    private Vector<String> batchs;
    public File file;
	 public XWJsxyJPanel(NewTechnicsPart frame) {
	     this.frame=frame;

	     XWTreeNode selectedTreeNode = frame.xwPartTreePanel.getSelectedTreeNode();
         XWTreeNode parent2 = (XWTreeNode) selectedTreeNode.getParent().getParent();
         Element pElement = parent2.getObject().getTreeCellData();
        //获取当前产品的批次号
        if(batchs == null) {
            long oid = Long.valueOf(XmlUtility.getAttributeValue(pElement, "containerId"));
            try {
                batchs = TechnicsIntf.getBatchsByProductOid(oid);
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (InvocationTargetException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if(batchs == null) {
                batchs = new Vector<String>();
                batchs.add("");
            }
        }
        piciValue = new JComboBox(batchs);
	     XWTreeNode treeNode = frame.xwPartTreePanel.getSelectedTreeNode();
	     XWTreeObject object = treeNode.getObject();
	     if (object instanceof JsxyDocTreeObject) {
	         JsxyDocTreeObject jsxy=(JsxyDocTreeObject) object;
	         String number = jsxy.getNumber();
	          map = (Map) IntfUtil.getPeRemoteMethodInvoke("getJsxyDetailByJsxyNumber",
                     new Class[] { String.class }, new Object[] { number });
        }
	     if (map.size()>0) {
            initData(map);
        }


        initcomponent();
        initButton();
    }
    private void initButton() {
        saveButton.setEnabled(false);
        List list = UserUtil.getCurrentUserOid();
        String name = (String) list.get(2);
      if (name.equals(creatorValue.getText())&&("正在工作".equals(stateValue.getText())||"修改中".equals(stateValue.getText()))) {
          saveButton.setEnabled(true);
    }else{
        saveButton.setEnabled(false);
    }
    }
    private void initData(Map map) {
      String name=  (String) map.get("name");
      String jsxyNumber=  (String) map.get("jsxyNumber");
      String bianzhibumen=  (String) map.get("bianzhibumen");
      String xinghaodaihao=  (String) map.get("xinghaodaihao");
      String chanpindaihao=  (String) map.get("chanpindaihao");
      String bujiandaihao=  (String) map.get("bujiandaihao");
      String jieduanbiaoji=  (String) map.get("jieduanbiaoji");
      String pici=  (String) map.get("pici");
      String miji=  (String) map.get("miji");
      String bujianmingcheng=  (String) map.get("bujianmingcheng");
      String creator=  (String) map.get("creator");
      String state=  (String) map.get("state");

      mingchengValue.setText(name);
      bianhaoValue.setText(jsxyNumber);
      bianzhibumenValue.setText(bianzhibumen);
      xinghaodaihaoValue.setText(xinghaodaihao);
      chanpindaihaoValue.setText(chanpindaihao);
      bujiandaihaoValue.setText(bujiandaihao);
      bujianmingchengValue.setText(bujianmingcheng);
      jieduanbiaojiValue.setSelectedItem(jieduanbiaoji);
      piciValue.setSelectedItem(pici);
      mijiValue.setSelectedItem(miji);
      creatorValue.setText(creator);
      stateValue.setText(state);

    }
    public void initcomponent() {
        setLayout(new GridBagLayout());
        add(panel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 5, 0, 5), 0, 0));
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new TitledBorder(null, "技术协议信息", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        final GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new Insets(5, 5, 5, 10);
        gridBagConstraints.gridwidth = 20;
        //编号
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        panel.add(bianhao, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        bianhaoValue.setEditable(false);
        bianhaoValue.setPreferredSize(new Dimension(130, 23));
        panel.add(bianhaoValue, gridBagConstraints);

      //名称
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        panel.add(mingcheng, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        mingchengValue.setPreferredSize(new Dimension(130, 23));
        panel.add(mingchengValue, gridBagConstraints);

      //编制部门
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        panel.add(bianzhibumen, gridBagConstraints);

        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        bianzhibumenValue.setEditable(false);
        bianzhibumenValue.setPreferredSize(new Dimension(130, 23));
        panel.add(bianzhibumenValue, gridBagConstraints);

        //型号代号
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        panel.add(xinghaodaihao, gridBagConstraints);

        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        xinghaodaihaoValue.setEditable(false);
        xinghaodaihaoValue.setPreferredSize(new Dimension(130, 23));
        panel.add(xinghaodaihaoValue, gridBagConstraints);

      //产品代号
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        panel.add(chanpindaihao, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        chanpindaihaoValue.setEditable(false);
        chanpindaihaoValue.setPreferredSize(new Dimension(130, 23));
        panel.add(chanpindaihaoValue, gridBagConstraints);

      //部件代号
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        panel.add(bujiandaihao, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        bujiandaihaoValue.setEditable(false);
        bujiandaihaoValue.setPreferredSize(new Dimension(130, 23));
        panel.add(bujiandaihaoValue, gridBagConstraints);

      //部件名称
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        panel.add(bujianmingcheng, gridBagConstraints);

        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        bujianmingchengValue.setEditable(false);
        bujianmingchengValue.setPreferredSize(new Dimension(130, 23));
        panel.add(bujianmingchengValue, gridBagConstraints);

        //阶段标记
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        panel.add(jieduanbiaoji, gridBagConstraints);

        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        jieduanbiaojiValue.setPreferredSize(new Dimension(130, 23));
        panel.add(jieduanbiaojiValue, gridBagConstraints);

        //批次
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        panel.add(pici, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
//        piciValue.setEditable(false);
        piciValue.setPreferredSize(new Dimension(130, 23));
        panel.add(piciValue, gridBagConstraints);

      //密级
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        panel.add(miji, gridBagConstraints);

        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
//        mijiValue.setEditable(false);
        mijiValue.setPreferredSize(new Dimension(130, 23));
        panel.add(mijiValue, gridBagConstraints);

        //创建者
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        panel.add(creator, gridBagConstraints);

        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        creatorValue.setEditable(false);
        creatorValue.setPreferredSize(new Dimension(130, 23));
        panel.add(creatorValue, gridBagConstraints);

        //生命周期状态
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        panel.add(state, gridBagConstraints);

        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        stateValue.setEditable(false);
        stateValue.setPreferredSize(new Dimension(130, 23));
        panel.add(stateValue, gridBagConstraints);


        //查看系统中的技术协议
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        panel.add(xyjsxy, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        chakanButton.setPreferredSize(new Dimension(50, 23));
        panel.add(chakanButton, gridBagConstraints);


        chakanButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final VaActionProgressBar progressBar = new VaActionProgressBar(
                        frame, "查看主要内容", "正在查找 主要内容,请等待...", "正在打开主要内容....");
                Thread thread = new Thread() {
                    public void run() {
            XWTreeNode treeNode = frame.xwPartTreePanel.getSelectedTreeNode();
            XWTreeObject object = treeNode.getObject();
            if (object instanceof JsxyDocTreeObject) {
                String number = ((JsxyDocTreeObject) object).getNumber();
                String jsxyPath = WorkSpaceUtil.getJsxyPath(number);
                Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("downLoadJsxyDoc",
                        new Class[] { String.class,String.class }, new Object[] {number,jsxyPath});

                String name = (String) IntfUtil.getPeRemoteMethodInvoke("getAppDataByNumber",
                      new Class[] { String.class }, new Object[] {number});
               if (flag) {


                if (!"".equals(name)) {
                    if (name.endsWith(".doc")) {
                        jsxyPath=jsxyPath+name;
                    }else {
                        jsxyPath=jsxyPath+name+".doc";
                    }
                }else{
                    JOptionPane.showMessageDialog(frame, "此技术协议没有关联主要内容！", "提示",JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                File file = new File(jsxyPath);
                try {
                    Runtime.getRuntime().exec(
                            "rundll32.exe url.dll,FileProtocolHandler  "
                                    +file );
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
               }else{
                   JOptionPane.showMessageDialog(frame, "下载关联的主要内容出现错误，请检查是否有主要内容！", "提示",JOptionPane.INFORMATION_MESSAGE);
                   return;
               }
            }

            progressBar.finish();
            progressBar.setVisible(false);
            }
                };

        thread.start();
        progressBar.setVisible(true);
            }
        });

        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        panel.add(newJsxy, gridBagConstraints);

        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        newJsxyValue.setEditable(false);
        newJsxyValue.setPreferredSize(new Dimension(130, 23));
        panel.add(newJsxyValue, gridBagConstraints);

        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        xuanzeButton.setPreferredSize(new Dimension(50, 23));
        panel.add(xuanzeButton, gridBagConstraints);
        xuanzeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(new File("."));
                chooser.setMultiSelectionEnabled(false);
                FileNameExtensionFilter filter = new FileNameExtensionFilter(".doc", "doc");
                chooser.setFileFilter(filter);
                int result = chooser.showOpenDialog(frame);
                if(result == JFileChooser.APPROVE_OPTION){
                      file = chooser.getSelectedFile();
                      newJsxyValue.setText(file.getName());
            }
            }
        });
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        saveButton.setPreferredSize(new Dimension(70, 33));
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String number = bianhaoValue.getText();
                String name = mingchengValue.getText();
                String nameTrim = name.trim();
                if (nameTrim.equals("")) {
                    JOptionPane.showMessageDialog(frame, "请输入名称！", "提示",JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                String jieduanbiaoji = (String) jieduanbiaojiValue.getSelectedItem();
                String pici = (String)piciValue.getSelectedItem();
                String miji = (String) mijiValue.getSelectedItem();
                Map<Object, Object> map = new HashMap<Object, Object>();
                map.put("number", number);
                map.put("name", name);
                map.put("jieduanbiaoji", jieduanbiaoji);
                map.put("pici", pici);
                map.put("miji", miji);
                Boolean flag = (Boolean) IntfUtil.getPeRemoteMethodInvoke("EditJsxyDoc",
                        new Class[] { Map.class,File.class }, new Object[] { map,file });
                if (flag) {
                    JOptionPane.showMessageDialog(frame, "修改技术协议成功！", "提示",JOptionPane.INFORMATION_MESSAGE);
                }
                XWTreeNode selectedTreeNode = (XWTreeNode) frame.xwPartTreePanel.getSelectedTreeNode().getParent();
                XWTreeObject object = selectedTreeNode.getObject();
                if (object instanceof TechnicsMessageTreeObject) {
                    frame.xwPartTreePanel.getTree().collapsePath(new TreePath(((DefaultTreeModel) frame.xwPartTreePanel.getTree().getModel()).getPathToRoot(selectedTreeNode)));
                    selectedTreeNode.expand();
                    frame.xwPartTreePanel.getTree().updateUI();
                }

//                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) frame.xwPartTreePanel.getSelectedTreeNode().getParent();
//
//                if(!treeNode.isLeaf()){
//                    DefaultTreeModel model = (DefaultTreeModel) frame.xwPartTreePanel.getTree().getModel();
//                    frame.xwPartTreePanel.getTree().collapsePath(new TreePath( model.getPathToRoot(treeNode)));
//                    frame.xwPartTreePanel.getTree().updateUI();
//
////                }
//                if(!selectedTreeNode.isLeaf()){
//                    DefaultTreeModel model = (DefaultTreeModel) frame.xwPartTreePanel.getTree().getModel();
//                    frame.xwPartTreePanel.getTree().expandPath(new TreePath(model.getPathToRoot(selectedTreeNode)));
//                }


            }
        });

        panel.add(saveButton, gridBagConstraints);




    }
public static void main(String[] args) {
    XWJsxyJPanel jxJPanel=new XWJsxyJPanel(null);
    jxJPanel.setVisible(true);
    jxJPanel.setSize(200, 300);
    }

}
