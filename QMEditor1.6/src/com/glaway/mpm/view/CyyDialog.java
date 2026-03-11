package com.glaway.mpm.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class CyyDialog extends JDialog {


	private JButton sure = null;
	private JButton cancle = null;
	private JPanel pan = null;
	private StepNameComboBox cyyComboBox = null;
	private NewTechnicsPart frame=null;

	public CyyDialog(NewTechnicsPart frame){
		super();
		this.frame=frame;

		initComponents();
		initLayout();
		initActions();

		setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);


	}




	public void initComponents(){
		sure = new JButton("确定");
		cancle = new JButton("取消");
		cyyComboBox = new StepNameComboBox();
		setTitle("常用语查询");
		pan =new JPanel();

	}

	public void initLayout(){
		setBounds(400, 350, 380, 100);
		cyyComboBox.setPreferredSize(new Dimension(150, 23));

		pan.setLayout(new GridBagLayout());
		pan.add(cyyComboBox, new GridBagConstraints(0, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 3), 0, 0));

		pan.add(sure, new GridBagConstraints(1, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 3), 0, 0));

		pan.add(cancle, new GridBagConstraints(2, 0, 1, 1, 0, 0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 3), 0, 0));

        add(pan);


	}


	public void initActions(){

		sure.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				String str = (String)cyyComboBox.getSelectedItem();
				if(str!=null && !"".equals(str)){

				 frame.getTechnicsStepJPanel().getSpeCharPanel().insertText(str);
				}
				setVisible(false);
				dispose();
			}


		});

		cancle.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}


		});

	}

}
