package com.glaway.mpm.pbombuilder.tree.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import com.glaway.mpm.pbombuilder.action.CmAction;
import com.glaway.mpm.pbombuilder.action.CmActionProgressBar;
import com.glaway.mpm.pbombuilder.action.CmPViewZoomAll;
import com.glaway.mpm.pbombuilder.exception.CmTaskException;
import com.glaway.mpm.pbombuilder.gui.CmTheme;
import com.glaway.mpm.pbombuilder.impl.CmPViewNode;
import com.glaway.mpm.pbombuilder.log.CmLogger;
import com.glaway.mpm.pbombuilder.pview.Cm2DPViewNodeGenerator;
import com.glaway.mpm.pbombuilder.pview.CmPViewFactory;
import com.glaway.mpm.pbombuilder.pview.CmPViewImpl;
import com.glaway.mpm.pbombuilder.pview.CmPViewNodeGenerator;
import com.glaway.mpm.pbombuilder.pview.CmPViewZoomSelected;
import com.glaway.mpm.pbombuilder.util.CmAbstractDialog;
import com.glaway.mpm.pbombuilder.util.CmGuiUtil;
import com.glaway.mpm.pbombuilder.util.CmTaskExecutorCallback;
import com.glaway.mpm.pbombuilder.util.PviewTask;
import com.ptc.pview.utils.dom.ActorShutdownException;
import com.ptc.pview.utils.dom.ConnectionLostException;
import com.ptc.pview.utils.dom.InvalidActorException;

/**
 * <br>Created on 2012-11-21
 * @author chenyunlong
 */
public class CmPViewLiteDialog extends CmAbstractDialog {
	   private static final long     serialVersionUID = 2685289616311556306L;
	   private static final CmLogger logger           = CmLogger.getLogger();

	   private String                name;
	   private CmAction              actZoomAll;
	   private CmAction              actZoomSelected;

	   private JButton               btnZoomAll;
	   private JButton               btnZoomSelected;

	   private JLabel                labelStatus;                            // 底部状态栏

	   private boolean               resetNeeded;

	   private CmActionProgressBar   animFrame;

	   public CmPViewLiteDialog(Window owner, String name) {
	      super(owner);
	      this.animFrame = null;
	      this.resetNeeded = false;
	      this.name = name;
	      this.setModal(false);
	      try {
	         this.initUI();
	      } catch (CmTaskException e) {
	         logger.error(e);
	      }

	      this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	     
	   }

	   @Override
	   protected void initActions() {
	      actZoomAll = new CmPViewZoomAll(name);
	      actZoomSelected = new CmPViewZoomSelected(name);
	   }

	   @Override
	   protected void initComponents() {
	      btnZoomAll = new JButton(actZoomAll);
	      btnZoomAll.setForeground(Color.WHITE);

	      btnZoomSelected = new JButton(actZoomSelected);
	      btnZoomSelected.setForeground(Color.WHITE);

	      labelStatus = new JLabel(" Creoview 2.0: ");
	      labelStatus.setBorder(new EtchedBorder());
	   }

	   @Override
	   protected void initDimension() {
	      this.setBounds(CmGuiUtil.getScreenCenter(600, 480));
	   }

	   @Override
	   protected void initLayout() {
		   JPanel panel=new JPanel();
		   panel.setLayout(new BorderLayout());
		   panel.add(buildToolBar(), BorderLayout.NORTH);
		   panel.add(CmPViewFactory.getPViewImpl(name).getPanelContext(), BorderLayout.CENTER);
		   panel.add(labelStatus, BorderLayout.SOUTH);
	       setContentPane(panel);
	   }

	   private JToolBar buildToolBar() {
	      JToolBar toolBar = new JToolBar();
	      toolBar.setBackground(CmTheme.CM_TURQUOISE);
	      toolBar.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.WHITE));
	      toolBar.setFloatable(false);
	      toolBar.setRollover(true);

	      toolBar.add(btnZoomAll);
	      toolBar.add(btnZoomSelected);
	      return toolBar;
	   }

	   @Override
	   protected void loadInitDatas() {}

	   @Override
	   protected void registerTaskExecutor() throws CmTaskException {
	      PviewTask.registerTaskExecutor("mainframe.setStatus", CmPViewLiteDialog.this, false);
	   }

	   @Override
	   protected void unregisterTaskExecutor() throws CmTaskException {
		   PviewTask.unregisterTaskExecutor(this);
	   }

	   public void showPViewLite(String caption, CmPViewNode root) {
	      this.setTitle(caption == null ? "可视化窗口" : caption);
	      this.setVisible(true);
	      this.setSize(new Dimension(1000, 800));

	      final CmPViewImpl impl = CmPViewFactory.getPViewImpl(name);
	      if (this.resetNeeded) {
	    	  try {
				impl.resetPvWorld();
			} catch (ConnectionLostException e) {
				e.printStackTrace();
			} catch (ActorShutdownException e) {
				e.printStackTrace();
			} catch (InvalidActorException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	      }

	      final CmPViewNode treeRoot = root;
	      try {
	         Thread createPVRunner = new Thread() {
	            public void run() {
	               try {
	            	   PviewTask.registerTaskExecutor("CmPViewLiteDialog.finishAnimFrame", CmPViewLiteDialog.this, true);
	               } catch (CmTaskException e) {
	                  logger.error(e);
	               }

	               try {
	                  CmPViewNodeGenerator generator = new CmPViewNodeGenerator(treeRoot, impl.getName());
	                  generator.generatePVStructure("CmPViewLiteDialog.finishAnimFrame");
	               } catch (Exception e) {
	                  logger.error(e);
	               }
	            }
	         };
	         this.animFrame = new CmActionProgressBar(null, CmPViewLiteDialog.this, "可视化", "正在进行可视化装配", "正在进行Product View可视化装配，请等待...");
	         this.animFrame.setVisible(true);
	         this.resetNeeded = true;

	         createPVRunner.start();
	      } catch (Exception e) {
	         logger.error(e);
	      }
	   }
	   
	   public void show2DPViewLite(String caption) {
		      this.setTitle(caption == null ? "可视化窗口" : caption);
		      this.setVisible(true);
		      this.setSize(new Dimension(1000, 800));

		      final CmPViewImpl impl = CmPViewFactory.getPViewImpl(name);
		      if (this.resetNeeded) {
		    	  try {
		    		  logger.debug("resetPvWorld");
					impl.resetPvWorld();
				} catch (ConnectionLostException e) {
					e.printStackTrace();
				} catch (ActorShutdownException e) {
					e.printStackTrace();
				} catch (InvalidActorException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
		      }

		      try {
		         Thread createPVRunner = new pvRunner(name);
		         this.animFrame = new CmActionProgressBar(null, CmPViewLiteDialog.this, "可视化", "正在进行可视化装配", "正在进行Product View可视化装配，请等待...");
		         this.animFrame.setVisible(true);
		         this.resetNeeded = true;

		         createPVRunner.start();
		      } catch (Exception e) {
		         logger.error(e);
		      }
		   }

	   public synchronized void finishAnimFrame(Object render, Object params, CmTaskExecutorCallback callback) {
	      if (animFrame != null) {
	         try {
	            animFrame.finish();
	            animFrame = null;
	         } catch (Throwable tt) {
	         } finally {
	            animFrame = null;
	         }
	         this.setVisible(true);
	      }
	   }

	   public void setStatus(Object render, Object param, CmTaskExecutorCallback callback) {
	      StringBuffer buf = new StringBuffer(" Creoview 2.0: ");
	      if (param instanceof Object[]) {
	         Object[] params = (Object[]) param;
	         for (Object obj : params)
	            buf.append(String.valueOf(obj));
	      } else
	         buf.append(String.valueOf(param));

	      labelStatus.setText(buf.toString());
	   }
	   
	   class pvRunner extends Thread{
		   private String n ;
		   public pvRunner(String na) {
			   this.n = na;
		}
		   public void run() {
               try {
            	   PviewTask.registerTaskExecutor("CmPViewLiteDialog.finishAnimFrame", CmPViewLiteDialog.this, true);
               } catch (CmTaskException e) {
                  logger.error(e);
               }

               try {
            	   Cm2DPViewNodeGenerator generator = new Cm2DPViewNodeGenerator(this.n);
                  generator.generatePVStructure("C:\\Users\\Administrator\\Desktop\\visualization_-_aerial.dwg","CmPViewLiteDialog.finishAnimFrame");
               } catch (Exception e) {
                  logger.error(e);
               }
            }
	   }
	}

