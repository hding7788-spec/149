package com.glaway.mpm.visual.view.action;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Window;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

import com.glaway.mpm.task.exception.CmTaskException;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaGuiUtil;
import com.glaway.mpm.visual.view.ui.VaAbstractDialog;

public class VaExceptionReport extends VaAbstractDialog {
   private static final long     serialVersionUID = 7340779628143620339L;
   private static final VaLogger log              = VaLogger.getLogger(VaExceptionReport.class);

   private VaAction              actClose;

   private JButton               btnClose;
   private JLabel                labelHeaderTitle;
   private JLabel                labelHeaderMessage;
   private JTextArea             taException;

   private Exception             exception;

   public VaExceptionReport(Window owner, Exception exception) {
      super(owner);
      this.setTitle("异常报告");
//      this.setIconImage("");//CmUtil.getImageFromServer("criticalError.png"));

      this.exception = exception;
      try {
         this.initUI();
      } catch (Exception e) {
         log.error(e);
      }
      setResizable(false);
      setModal(true);
      setVisible(true);
   }

   public void setHeaderTitle(String headerTitle) {
      this.labelHeaderTitle.setText(headerTitle);
   }

   public void setHeaderMessage(String headerMessage) {
      this.labelHeaderMessage.setText(headerMessage);
   }

   public static void main(String[] args) {
      new VaExceptionReport(null, new Exception("异常测试"));

   }

   protected JScrollPane getScrollPane() {
      return new JScrollPane(taException);
   }

   @Override
   protected void initActions() {
//      actClose = new VaDisposeOnCloseAction(this, " 关闭 ");
   }

   @Override
   protected void initComponents() {
      labelHeaderTitle = new JLabel("错误");
      labelHeaderTitle.setFont(new Font("", Font.BOLD, 12));

      labelHeaderMessage = new JLabel("发生异常，下面是异常报告信息");
      labelHeaderMessage.setVerticalAlignment(SwingConstants.TOP);

      taException = new JTextArea();
      taException.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.LIGHT_GRAY, Color.LIGHT_GRAY));
      taException.setCaretPosition(0);

      btnClose = new JButton(actClose);
      btnClose.setPreferredSize(new Dimension(60, 25));
   }

   @Override
   protected void initDimension() {
      this.setBounds(VaGuiUtil.getScreenCenter(600, 450));
   }

   @Override
   protected void initLayout() {
      getContentPane().setLayout(new BorderLayout());
      getContentPane().add(getHeaderPanel(), BorderLayout.NORTH);
      getContentPane().add(getClientPanel(), BorderLayout.CENTER);
   }

   private Component getHeaderPanel() {
      JPanel ret = new JPanel(new GridBagLayout());
      ret.setBackground(Color.WHITE);
      ret.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, SystemColor.controlLtHighlight), new MatteBorder(0, 0, 1, 0,
         SystemColor.controlLtHighlight)));

      final GridBagConstraints gbc = new GridBagConstraints();
      gbc.fill = GridBagConstraints.HORIZONTAL;
      gbc.anchor = GridBagConstraints.NORTHWEST;
      gbc.weightx = 1;
      gbc.weighty = 1;
      gbc.gridx = 0;

      gbc.insets = new Insets(10, 10, 0, 0);
      gbc.gridy = 0;
      ret.add(labelHeaderTitle, gbc);

      gbc.insets = new Insets(0, 40, 5, 0);
      gbc.gridy = 1;
      ret.add(labelHeaderMessage, gbc);

      return ret;
   }

   private Component getClientPanel() {
      JPanel ret = new JPanel(new BorderLayout());

      JScrollPane scrollPane = getScrollPane();
      scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
      ret.add(scrollPane, BorderLayout.CENTER);

      JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.TRAILING));
      panelActions.add(btnClose);
      ret.add(panelActions, BorderLayout.SOUTH);

      return ret;
   }

   @Override
   protected void loadInitDatas() {
      StringWriter sw = new StringWriter();
      exception.printStackTrace(new PrintWriter(sw));
      taException.setText(sw.toString());

      Throwable cause = exception.getCause();
      if (cause != null) {
         sw = new StringWriter();
         cause.printStackTrace(new PrintWriter(sw));
         taException.setText(taException.getText() + "\n异常原因 :\n" + sw.toString());
      }
   }

   @Override
   protected void registerTaskExecutor() throws CmTaskException {}

   @Override
   protected void unregisterTaskExecutor() throws CmTaskException {}
}

