/*     */ package com.glaway.mpm.view;
/*     */ 
/*     */ import com.faw_qm.speChar.speEdit.TechFrmTest;
import com.faw_qm.speChar.view.MyTextPane;

/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Insets;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.awt.event.ComponentListener;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JMenuItem;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JViewport;
import javax.swing.text.Document;
/*     */ 
/*     */ public class SpeCharPanel extends JPanel
/*     */ {
/*  26 */   private boolean isLineWrap = false;
/*     */ 
/*  28 */   private int lineWrap = 3000;
/*     */   private MyTextPane textPanel;
/*     */   private JScrollPane paneScrollPane;
/*  32 */   private static int lineHeight = 50;
/*  33 */   private static JFrame parent = null;
/*     */ 
/*  35 */   private boolean noVertical = false;
/*     */ 
/*     */   public SpeCharPanel()
/*     */   {
/*  39 */     this.isLineWrap = true;
/*  40 */     initPanel(null, true);
/*     */   }
/*     */ 
/*     */   public SpeCharPanel(boolean wrap)
/*     */   {
/*  50 */     this.isLineWrap = wrap;
/*  51 */     initPanel(null, wrap);
/*     */   }
/*     */ 
/*     */   public SpeCharPanel(JFrame parent, boolean wrap)
/*     */   {
/*  61 */     this.isLineWrap = wrap;
/*  62 */     parent = parent;
/*  63 */     initPanel(parent, wrap);
/*     */   }
/*     */ 
/*     */   public SpeCharPanel(JFrame parent, boolean wrap, boolean hasVertical)
/*     */   {
/*  69 */     this.isLineWrap = wrap;
/*  70 */     parent = parent;
/*  71 */     this.noVertical = hasVertical;
/*  72 */     initPanel(parent, wrap);
/*     */   }
/*     */ 
/*     */   public JFrame getParentFrame()
/*     */   {
/*  78 */     return parent;
/*     */   }
/*     */ 
/*     */   public boolean getLineWrap()
/*     */   {
/*  88 */     return this.isLineWrap;
/*     */   }
/*     */ 
/*     */   private void initPanel(JFrame parent, boolean wrap)
/*     */   {
/*  95 */     this.textPanel = new MyTextPane(parent, "plaintWrappedLine");
/*     */ 
/*  99 */     setFont(new Font("Dialog", 0, 14));
/*     */ 
/* 101 */     setOpaque(true);
/*     */ 
/* 103 */     this.paneScrollPane = new JScrollPane(this.textPanel);
/* 104 */     this.paneScrollPane.getViewport().setBackingStoreEnabled(true);
/* 105 */     this.paneScrollPane.setOpaque(true);
/*     */ 
/* 107 */     this.paneScrollPane.setDoubleBuffered(true);
/* 108 */     this.textPanel.setVertical(3, lineHeight);
/*     */ 
/* 111 */     if (this.isLineWrap)
/*     */     {
/* 113 */       this.paneScrollPane.setHorizontalScrollBarPolicy(31);
/*     */     }
/*     */     else
/*     */     {
/* 117 */       this.paneScrollPane.setHorizontalScrollBarPolicy(30);
/*     */     }
/* 119 */     if (this.noVertical)
/*     */     {
/* 121 */       setBorder(null);
/* 122 */       this.paneScrollPane.setBorder(null);
/* 123 */       this.textPanel.setBorder(null);
/* 124 */       this.paneScrollPane.setVerticalScrollBarPolicy(21);
/*     */     }
/*     */     else
/*     */     {
/* 128 */       this.paneScrollPane.setVerticalScrollBarPolicy(20);
/*     */     }
/*     */ 
/* 137 */     this.paneScrollPane.setPreferredSize(new Dimension(250, 155));
/* 138 */     this.paneScrollPane.setMinimumSize(new Dimension(10, 10));
/* 139 */     setSize(50, 30);
/* 140 */     setLayout(new GridBagLayout());
/* 141 */     add(this.paneScrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0D, 1.0D, 
/* 142 */       10, 1, new Insets(
/* 143 */       0, 0, 0, 0), 0, 0));
/*     */ 
/* 147 */     addComponentListener(new ComponentListener()
/*     */     {
/*     */       public void componentHidden(ComponentEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void componentMoved(ComponentEvent e)
/*     */       {
/*     */       }
/*     */ 
/*     */       public void componentResized(ComponentEvent e)
/*     */       {
/* 161 */         SpeCharPanel.this.setLineWrap();
/*     */       }
/*     */ 
/*     */       public void componentShown(ComponentEvent e)
/*     */       {
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public MyTextPane getTextPane()
/*     */   {
/* 175 */     return this.textPanel;
/*     */   }
/*     */ 
/*     */   private void setLineWrap()
/*     */   {
/* 184 */     int textWidth = getWidth();
/* 185 */     int textHeight = getHeight();
/* 186 */     if (this.isLineWrap)
/*     */     {
/* 188 */       this.textPanel.setSize(textWidth, textHeight);
/* 189 */       this.paneScrollPane.setHorizontalScrollBarPolicy(31);
/*     */     }
/*     */     else
/*     */     {
/* 193 */       this.textPanel.setSize(textWidth + this.lineWrap, textHeight);
/* 194 */       this.paneScrollPane.setHorizontalScrollBarPolicy(30);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setRows(int rows) {
/* 199 */     this.textPanel.setMaxHigh(lineHeight, rows);
/*     */   }
/*     */ 
/*     */   public void setSize(int width, int height) {
/* 203 */     this.paneScrollPane.setSize(width, height);
/* 204 */     resize(width, height);
/* 205 */     if (this.isLineWrap)
/*     */     {
/* 207 */       this.textPanel.setSize(width, height);
/* 208 */       this.paneScrollPane.setHorizontalScrollBarPolicy(31);
/*     */     }
/*     */     else
/*     */     {
/* 212 */       this.textPanel.setSize(width + this.lineWrap, height);
/* 213 */       this.paneScrollPane.setHorizontalScrollBarPolicy(30);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setFont(Font font) {
/* 218 */     if (this.textPanel != null)
/* 219 */       this.textPanel.setFont(font);
/*     */   }
/*     */ 
/*     */   public void setText(String str)
/*     */   {
/* 228 */     if (this.textPanel != null)
/*     */     {
/* 230 */       this.textPanel.setText(str);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setText(String str, int width, int height)
/*     */   {
/* 240 */     if (this.textPanel != null)
/* 241 */       this.textPanel.setText(str, width, height);
/*     */   }
/*     */ 
/*     */   public void grabFocus()
/*     */   {
/* 250 */     if (this.textPanel != null) {
/* 251 */       this.textPanel.grabFocus();
/*     */     }
/*     */     else
/*     */     {
/* 255 */       super.grabFocus();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String getText()
/*     */   {
/* 264 */     if (this.textPanel != null)
/*     */     {
/* 266 */       return this.textPanel.getAllContent();
/*     */     }
/* 268 */     return "";
/*     */   }
/*     */ 
/*     */   public void setEditable(boolean flag)
/*     */   {
/* 276 */     if (this.textPanel != null)
/* 277 */       this.textPanel.setEditable(flag);
/*     */   }
/*     */ 
/*     */   public void clearAll()
/*     */   {
/* 286 */     if (this.textPanel != null)
/* 287 */       this.textPanel.setText("");
/*     */   }
/*     */ 
/*     */   public void setInsertText(String str)
/*     */   {
/* 294 */     if ((this.textPanel != null) && (str.length() > 0))
/* 295 */       this.textPanel.setInsertText(str);
/*     */   }
/*     */ 
/*     */   public synchronized void addActionListener(ActionListener l)
/*     */   {
/* 308 */     this.textPanel.addActionListener(l);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 317 */     TechFrmTest rect = new TechFrmTest("test");
/* 318 */     rect.setLayout(new GridBagLayout());
/* 319 */     SpeCharPanel spePanel = new SpeCharPanel(rect, true);
/* 320 */     spePanel.setSize(600, 400);
/* 321 */     spePanel.setPreferredSize(new Dimension(500, 155));
/* 322 */     spePanel.setMinimumSize(new Dimension(250, 155));
/* 323 */     spePanel.setLocation(10, 10);
/* 324 */     spePanel.setEditable(true);
/* 325 */     rect.add(spePanel);
/*     */ 
/* 327 */     JButton getbtn = new JButton("获取内容");
/* 328 */     getbtn.setSize(90, 25);
/* 329 */     getbtn.setLocation(50, 300);
/*     */ 
/* 331 */     getbtn.setActionCommand("get");
/*     */ 
/* 333 */     JButton setbtn = new JButton("添加内容");
/* 334 */     setbtn.setSize(90, 25);
/* 335 */     setbtn.setLocation(50, 350);
/*     */ 
/* 337 */     setbtn.setActionCommand("set");
/*     */ 
/* 345 */     JButton clearbtn = new JButton("清除");
/* 346 */     clearbtn.setSize(90, 25);
/* 347 */     clearbtn.setLocation(50, 500);
/*     */ 
/* 349 */     clearbtn.setActionCommand("clear");
/*     */ 
/* 352 */     rect.setSize(700, 500);
/* 353 */     rect.setLocation(300, 100);
/*     */ 
/* 355 */     rect.setDefaultCloseOperation(3);
/* 356 */     rect.setVisible(true);
/*     */   }
/*     */ 
/*     */   public ArrayList getRowCount()
/*     */   {
/* 362 */     if (this.textPanel != null)
/*     */     {
/* 364 */       return this.textPanel.getAllLines();
/*     */     }
/* 367 */     return null;
/*     */   }
/*     */ 
/*     */   public void addItem(JMenuItem item)
/*     */   {
/* 376 */     if ((this.textPanel != null) && (item != null))
/*     */     {
/* 378 */       this.textPanel.addItem(item);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void insertString(String content)
/*     */   {
/* 388 */     if ((this.textPanel != null) && (content != null))
/*     */     {
/* 390 */       this.textPanel.insertStringToCurrentLoc(content);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setForeground(Color foreground)
/*     */   {
/* 402 */     if ((foreground != null) && (this.textPanel != null))
/*     */     {
/* 404 */       this.textPanel.setForeground(foreground);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Document getDocument()
/*     */   {
/* 413 */     if (this.textPanel != null)
/* 414 */       return this.textPanel.getDocument();
/* 415 */     return null;
/*     */   }
/*     */ 
/*     */   public int getSelectionStart()
/*     */   {
/* 423 */     return this.textPanel.getSelectionStart();
/*     */   }
/*     */ 
/*     */   public int getSelectionEnd()
/*     */   {
/* 431 */     return this.textPanel.getSelectionEnd();
/*     */   }
/*     */ 
/*     */   public void setSelectionStart(int start)
/*     */   {
/* 439 */     if (start < 0)
/* 440 */       start = 0;
/* 441 */     if (start <= this.textPanel.getText().length())
/*     */     {
/* 443 */       this.textPanel.setSelectionStart(start);
/*     */     }
/*     */     else
/*     */     {
/* 447 */       this.textPanel.setSelectionStart(this.textPanel.getText().length());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setSelectionEnd(int end)
/*     */   {
/* 456 */     if (end < 0) {
/* 457 */       end = 0;
/*     */     }
/* 459 */     int start = this.textPanel.getSelectionStart();
/* 460 */     if (start < 0)
/* 461 */       start = 0;
/* 462 */     if (end < start)
/* 463 */       end = start;
/* 464 */     if (end <= this.textPanel.getText().length())
/*     */     {
/* 466 */       this.textPanel.setSelectionEnd(end);
/*     */     }
/*     */     else
/*     */     {
/* 470 */       this.textPanel.setSelectionEnd(this.textPanel.getText().length());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setBackground(Color background)
/*     */   {
/* 480 */     if ((background != null) && (this.textPanel != null))
/*     */     {
/* 482 */       this.textPanel.setBackground(background);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\xuehu\Desktop\cappPart.jar
 * Qualified Name:     com.faw_qm.speChar.view.SpeCharPanel
 * JD-Core Version:    0.6.2
 */