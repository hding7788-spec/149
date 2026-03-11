package com.glaway.mpm.pbombuilder.tree.pbom;

 import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.glaway.mpm.pbombuilder.util.LoadConfig;

 public class IsFzbmRenderer
   implements TableCellRenderer
 {
   private JPanel panel = new JPanel();
   private JCheckBox box = new JCheckBox();

   public Rectangle getBound()
   {
     return this.panel.getBounds();
   }

   public Component getTableCellRendererComponent(JTable table, Object arg1, boolean arg2, boolean arg3, int row, int column)
   {
     this.box.setSelected(false);
     this.panel.setLayout(new GridBagLayout());
     this.panel.add(this.box, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 10, 0, new Insets(0, 0, 0, 0), 0, 0));
     if ((arg1 != null) )
     {
       if (arg1.toString().equalsIgnoreCase("true"))
       {
         this.box.setSelected(true);
       }
     }

     if (arg2)
     {
       this.panel.setForeground(table.getSelectionForeground());
       this.panel.setBackground(table.getSelectionBackground());
       this.box.setForeground(table.getSelectionForeground());
       this.box.setBackground(table.getSelectionBackground());
     }
     else
     {
       this.panel.setForeground(table.getForeground());
       this.panel.setBackground(table.getBackground());
       this.box.setForeground(table.getForeground());
       this.box.setBackground(table.getBackground());
     }

         String ctype = (String)table.getValueAt(row, 2);
         if(!"自制件".equals(ctype)){
        	 this.panel.setBackground(Color.lightGray);

         }else{
         	String zzbm = (String)table.getValueAt(row, 3);
         	String []	plants = LoadConfig.getInstance().getMainPlant();

        	int ccol = -1;
//        	if(!CmCommonStringUtil.isEmpty(plant))
        	for(int i=0;i<plants.length;i++){
        		if(plants[i].equals(zzbm)){
        			ccol = i;
        			break;
        		}
        	}
        	boolean flag = true;
        	if(column == (ccol+3)){
    			flag = false;
    		}
        	if(!flag)
        		 this.panel.setBackground(Color.lightGray);
         }


     return this.panel;
   }

   public void setBackground(Color color)
   {
     this.box.setBackground(color);
     this.panel.setBackground(color);
   }
 }

