package com.glaway.mpm.pbombuilder.action;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

import com.glaway.mpm.pbombuilder.exception.CmExceptionReport;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.util.CmAbstractDialog;
import com.glaway.mpm.pbombuilder.util.CmSettings;
import com.glaway.mpm.pbombuilder.util.CmUtil;

public class CmActionProgressBar extends CmAbstractDialog implements ActionListener {
	private static final long          serialVersionUID         = -3698703059913742213L;
	private static final CmLogger log              = CmLogger.getLogger(CmActionProgressBar.class.getName());

	   private JPanel                     jContentPane             = null;
	   private JPanel                     jPanelMain               = null;
	   private JPanel                     panelButtons             = null;
	   private JButton                    closeButton              = null;
	   private JButton                    cancelButton             = null;

	   private JLabel                     headerTitle              = new JLabel();
	   private JLabel                     headerMessage            = new JLabel();
	   private static JLabel              frameImageLabel          = new JLabel();
	   private JLabel                     progressStatus           = new JLabel();
	   private JProgressBar               progressBar              = new JProgressBar();
	   private int                        progressValue            = 0;
	   private long                       startTime                = System.currentTimeMillis();
	   private CmAction                   invokedFromAction        = null;
	   private static CmActionProgressBar actionProgressBar        = null;

	   private final static boolean       enablePerformanceTraceUI = CmSettings.getSection(CmSettings.SECTION_MBOM).get(//
	                                                                  "long.time.action.enablePerformanceTraceUI", //
	                                                                  true//
	                                                                  );

	   public CmActionProgressBar(CmAction invokingAction, Window owner, String title, String headerTitle, String headerMessage) {
	      super(owner);
	      setTitle(title);
	      startTime = System.currentTimeMillis();
	      invokedFromAction = invokingAction;
	      setSize(560, 290);
	      initialize();

	      setHeaderTitle(headerTitle);
	      setHeaderMessage(headerMessage);
	      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	      setVisible(true);
	      validate();
	      addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent e) {
	            closeAction();
	         }
	      });
	   }

	   public CmActionProgressBar(Frame owner, String title, String headerTitle, String headerMessage) {
	      super(owner);
	      setTitle(title);
	      startTime = System.currentTimeMillis();
	      setSize(560, 290);
	      initialize();
	      setHeaderTitle(headerTitle);
	      setHeaderMessage(headerMessage);
	      setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	      setVisible(true);
	      validate();
	      addWindowListener(new WindowAdapter() {
	         public void windowClosing(WindowEvent e) {
	            closeAction();
	         }
	      });
	   }

	   private void initialize() {
	      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	      int width = getWidth();
	      int height = getHeight();
	      int left = (screen.width - width) / 2;
	      int top = (screen.height - height) / 2;
	      setBounds(left, top, width, height); // center the frame
//	      getContentPane().add(getJContentPane());
	      setContentPane(getJContentPane());
	      setResizable(false);
	      progressBar.setIndeterminate(true);
	   }

	   private JPanel getJContentPane() {
	      if (jContentPane == null) {
	         jContentPane = new JPanel();
	         jContentPane.setLayout(new GridBagLayout());
	         jContentPane.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, SystemColor.controlLtHighlight), new MatteBorder(0, 0, 1, 0,
	            SystemColor.controlDkShadow)));
	         final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
	         gridBagConstraints_2.weighty = 0;
	         gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
	         gridBagConstraints_2.anchor = GridBagConstraints.CENTER;
	         gridBagConstraints_2.insets = new Insets(0, 0, 0, 0);
	         gridBagConstraints_2.gridx = 0;
	         gridBagConstraints_2.gridy = 0;
	         jContentPane.add(getPanelMain(), gridBagConstraints_2);

	         progressBar.setLayout(null);
	         final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
	         gridBagConstraints_1.anchor = GridBagConstraints.NORTH;
	         gridBagConstraints_1.ipady = 5;
	         gridBagConstraints_1.weighty = 1;
	         gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
	         gridBagConstraints_1.weightx = 1.0;
	         gridBagConstraints_1.insets = new Insets(10, 50, 3, 50);
	         gridBagConstraints_1.ipadx = 50;
	         gridBagConstraints_1.gridy = 3;
	         gridBagConstraints_1.gridx = 0;
	         jContentPane.add(progressBar, gridBagConstraints_1);

	         final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
	         gridBagConstraints_7.insets = new Insets(0, 0, 10, 50);
	         gridBagConstraints_7.anchor = GridBagConstraints.SOUTHEAST;
	         gridBagConstraints_7.weighty = 1.0;
	         gridBagConstraints_7.gridy = 4;
	         gridBagConstraints_7.gridx = 0;
	         jContentPane.add(getPanelButtons(), gridBagConstraints_7);
	      }
	      return jContentPane;
	   }

	   private JPanel getPanelMain() {
	      if (jPanelMain == null) {
	         jPanelMain = new JPanel();
	         jPanelMain.setLayout(new GridBagLayout());
	         jPanelMain.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, SystemColor.controlLtHighlight), new MatteBorder(0, 0, 1, 0,
	            SystemColor.controlDkShadow)));
	         jPanelMain.setBackground(Color.WHITE);

	         final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
	         gridBagConstraints_2.weighty = 0;
	         gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
	         gridBagConstraints_2.anchor = GridBagConstraints.NORTHWEST;
	         gridBagConstraints_2.insets = new Insets(0, 0, 0, 0);
	         gridBagConstraints_2.gridx = 0;
	         gridBagConstraints_2.gridy = 0;

	         headerTitle.setFont(new Font("", Font.BOLD, 12));
	         headerTitle.setText("");
	         final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
	         gridBagConstraints_4.insets = new Insets(10, 10, 0, 0);
	         gridBagConstraints_4.weightx = 1.0;
	         gridBagConstraints_4.anchor = GridBagConstraints.NORTHWEST;
	         gridBagConstraints_4.gridy = 0;
	         gridBagConstraints_4.gridx = 0;
	         jPanelMain.add(headerTitle, gridBagConstraints_4);

	         headerMessage.setText("");
	         final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
	         gridBagConstraints_5.insets = new Insets(5, 30, 0, 0);
	         gridBagConstraints_5.weighty = 1.0;
	         gridBagConstraints_5.weightx = 1.0;
	         gridBagConstraints_5.anchor = GridBagConstraints.NORTHWEST;
	         gridBagConstraints_5.gridy = 1;
	         gridBagConstraints_5.gridx = 0;
	         jPanelMain.add(headerMessage, gridBagConstraints_5);

	         frameImageLabel.setText("");
	         frameImageLabel.setIcon(new ImageIcon(CmUtil.getImageFromServer("waitc.gif")));
	         final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
	         gridBagConstraints_6.anchor = GridBagConstraints.NORTHWEST;
	         gridBagConstraints_6.gridheight = 2;
	         gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
	         gridBagConstraints_6.gridy = 0;
	         gridBagConstraints_6.gridx = 1;
	         gridBagConstraints_6.insets = new Insets(18, 18, 18, 18);
	         jPanelMain.add(frameImageLabel, gridBagConstraints_6);

	         progressStatus.setText("");
	         final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
	         gridBagConstraints_3.insets = new Insets(5, 50, 4, 0);
	         gridBagConstraints_3.anchor = GridBagConstraints.WEST;
	         gridBagConstraints_3.gridy = 1;
	         gridBagConstraints_3.gridx = 0;
	         jPanelMain.add(progressStatus, gridBagConstraints_3);

	      }
	      return jPanelMain;
	   }

	   private JPanel getPanelButtons() {
	      if (panelButtons == null) {
	         panelButtons = new JPanel();

	         panelButtons.setLayout(new GridBagLayout());
	         panelButtons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	         final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
	         gridBagConstraints_1.weighty = 0;
	         gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
	         gridBagConstraints_1.anchor = GridBagConstraints.SOUTHEAST;
	         gridBagConstraints_1.insets = new Insets(2, 8, 2, 18);
	         gridBagConstraints_1.gridx = 0;
	         gridBagConstraints_1.gridy = 0;

	         final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
	         gridBagConstraints_2.weighty = 0;
	         gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
	         gridBagConstraints_2.anchor = GridBagConstraints.SOUTHEAST;
	         gridBagConstraints_2.insets = new Insets(2, 8, 2, 18);
	         gridBagConstraints_2.gridx = 1;
	         gridBagConstraints_2.gridy = 0;

	         panelButtons.add(getBClose(), gridBagConstraints_1);
	         panelButtons.add(getBCancel(), gridBagConstraints_2);
	      }
	      return panelButtons;
	   }

	   private JButton getBClose() {
	      if (closeButton == null) {
	         closeButton = new JButton();
	         closeButton.setText("关闭");
	         closeButton.setPreferredSize(new Dimension(100, 26));
	         closeButton.setEnabled(false);
	         closeButton.setVisible(false);
	         closeButton.addActionListener(this);
	      }
	      return closeButton;
	   }

	   private JButton getBCancel() {

	      if (cancelButton == null) {
	         cancelButton = new JButton();
	         cancelButton.setText("取消");
	         cancelButton.setPreferredSize(new Dimension(100, 26));
	         cancelButton.setEnabled(true);
	         if (invokedFromAction != null && invokedFromAction.isCancelable()) {
	            cancelButton.setVisible(true);
	         }
	         cancelButton.addActionListener(this);
	      }
	      if (invokedFromAction != null && invokedFromAction.isCancelable()) {
//	         cancelButton.setVisible(true);
	      } else {
	         cancelButton.setVisible(false);
	      }
	      return cancelButton;
	   }

	   public void setHeaderTitle(String headerTitle) {
	      this.headerTitle.setText(headerTitle);
	   }

	   public void setHeaderMessage(String headerMessage) {
	      this.headerMessage.setText(headerMessage);
	   }

	   public void setProgressStatus(String progressStatus, int progressIncr) {
	      progressValue += progressIncr;
	      this.progressStatus.setText("<html>" + progressStatus + "<br>Number of parts processed : " + progressValue + "</html>");
	   }

	   public void setProgressStatus(String progressStatus) {
	      this.progressStatus.setText("<html>" + progressStatus + "</html>");
	   }

	   public void setProgressBarValue(int avalue) {
	      progressBar.setValue(avalue);
	   }

	   public void increaseProgressBarValueAlone() {
	      progressBar.setValue(progressBar.getValue() + 1);
	   }

	   public void resetProgressBarValue() {
	      progressBar.setValue(0);
	   }

	   public int getProgressBarValue() {
	      return progressBar.getValue();
	   }

	   public void setProgressBarMax(int avalue) {
	      progressBar.setMaximum(avalue);
	   }

	   public void setProgressBarIndeterminate(boolean avalue) {
	      if (avalue)
	         progressBar.setString("");
	      else
	         progressBar.setString(null);
	      progressBar.setIndeterminate(avalue);
	   }

	   public void dispose() {
	      super.dispose();
	      actionProgressBar = null;
	   }

	   public void setVisible(boolean aValue) {
	      super.setVisible(aValue);
	      if (aValue)
	         actionProgressBar = this;
	      else
	         actionProgressBar = null;
	   }

	   public void setErrorMessage(Exception anException) {
	      this.setProgressBarIndeterminate(false);
	      this.headerMessage.setText("错误");
	      this.headerMessage.setIcon(new ImageIcon(CmUtil.getImageFromServer("error.gif")));
	      headerMessage.addMouseListener(new CmLinkMouseAdapter(this, anException));
	      closeButton.setEnabled(true);
	   }

	   public void actionPerformed(ActionEvent e) {
	      if (e.getSource().equals(closeButton)) {
	         closeAction();
	      }
	      if (e.getSource().equals(cancelButton)) {
	         invokedFromAction.cancel();
	      }
	   }

	   public void setElapsedTime(String time) {
	      closeButton.setEnabled(true);
	      progressBar.setIndeterminate(false);
	      progressBar.setMaximum(1);
	      progressBar.setValue(1);
	      progressStatus.setText("已用时间：" + time);
	   }

	   private void closeAction() {
	      if (invokedFromAction != null) {
	         invokedFromAction.cancel();
	         invokedFromAction.setSourcesEnabled(true);
	      }
	      this.dispose();
	   }

	   public class CmLinkMouseAdapter extends MouseAdapter {
	      CmActionProgressBar _owner = null;
	      Exception           _exception;

	      public CmLinkMouseAdapter(CmActionProgressBar owner, Exception e) {
	         _owner = owner;
	         _exception = e;
	      }

	      public void mouseClicked(MouseEvent evt) {
	         CmExceptionReport rep = new CmExceptionReport((JDialog) _owner, _exception);

	         // Location : Center
	         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	         Dimension contSize = rep.getSize();

	         if (contSize.height > screenSize.height)
	            contSize.height = screenSize.height;

	         if (contSize.width > screenSize.width)
	            contSize.width = screenSize.width;

	         rep.setLocation((screenSize.width - contSize.width) / 2, (screenSize.height - contSize.height) / 2);
	         rep.setVisible(true);
	      }

	      public void mouseEntered(MouseEvent evt) {
	         headerMessage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	      }

	      public void mouseExited(MouseEvent evt) {
	         headerMessage.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	      }
	   }

	   public static CmActionProgressBar getActionProgressBar() {
	      return actionProgressBar;
	   }

	   public static void setActionProgressBar(CmActionProgressBar actionProgressBar) {
	      CmActionProgressBar.actionProgressBar = actionProgressBar;
	   }

	   public void finish() {
	      long theEndTime = System.currentTimeMillis();
	      cancelButton.setEnabled(false);
	      int theElapsedTimeInSecond = (int) (theEndTime - startTime) / 1000;
	      int hours, minutes, seconds;
	      hours = theElapsedTimeInSecond / 3600;
	      theElapsedTimeInSecond = theElapsedTimeInSecond - hours * 3600;
	      minutes = theElapsedTimeInSecond / 60;
	      theElapsedTimeInSecond = theElapsedTimeInSecond - minutes * 60;
	      seconds = theElapsedTimeInSecond;
	      if (startTime != -1)
	         setElapsedTime(hours + "时 " + minutes + "分 " + seconds + "秒");
	      if (enablePerformanceTraceUI){
	    	  closeAction();
	    	  log.debug("可视化装配完成!");
	      }
	      else {
	         closeButton.setEnabled(true);
	         frameImageLabel.setIcon(new ImageIcon(CmUtil.getImageFromServer("cobi_small.gif")));
	         setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	      }
	   }

	   @Override
	   protected void initActions() {}

	   @Override
	   protected void initComponents() {}

	   @Override
	   protected void initDimension() {}

	   @Override
	   protected void initLayout() {}

	   @Override
	   protected void loadInitDatas() {}

	   @Override
	   protected void registerTaskExecutor() throws CmTaskException {}

	   @Override
	   protected void unregisterTaskExecutor() throws CmTaskException {}

	}
