package com.glaway.mpm.view;

import com.glaway.mpm.qmIntf.common.model.CommonDialog;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.view.action.VaActionProgressBar;

import javax.swing.*;
import java.awt.*;

public class CreateChangeMarkTechnicsDialog extends JPanel{
    private static final long serialVersionUID = 1L;
    private JDialog dialog;
    private NewTechnicsPart frame;
    private VaActionProgressBar progressBar;

    public CreateChangeMarkTechnicsDialog(NewTechnicsPart frame,VaActionProgressBar progressBar) {
        this.frame = frame;
        this.progressBar = progressBar;
        newDialog();
    }

    public void newDialog() {
        dialog = new CommonDialog(frame);
        dialog.setTitle("工艺规程列表");
        dialog.setSize(750, 550);
        SwingUtil.setMiddle(dialog);
    }

    public void showDialog() {
        Container container = dialog.getContentPane();
        CreateChangeMarkTechnicsPanel panel = new CreateChangeMarkTechnicsPanel(dialog,frame,progressBar);
        container.add(panel);

        dialog.setVisible(true);
    }

}
