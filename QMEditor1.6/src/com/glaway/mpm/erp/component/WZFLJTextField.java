/*
 * 南京国睿信维软件有限公司
 */
package com.glaway.mpm.erp.component;

import com.glaway.mpm.view.NewTechnicsPart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * 类功能：
 *
 * @author MChen
 * @date 2021/3/18
 */
public class WZFLJTextField  extends JTextField {
    String nodeName;
    NewTechnicsPart frame;
   public WZFLJTextField(NewTechnicsPart frame,String nodeName){
        this.frame=frame;
       addMouseListener(new WZFLJEdit());
       setEditable(false);
       this.nodeName=nodeName;
    }

    class WZFLJEdit implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount()==1){
                try {
                    SetWZFLDialog dialog = new SetWZFLDialog(frame,nodeName);
                    String s = dialog.showDialog();
                    setText(s);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

}
