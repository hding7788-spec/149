package com.glaway.mpm.sjzyk;

import javax.swing.JComboBox;

public class SjzykUtil {

	public static JComboBox getDWJComboBox() {
		JComboBox comboBox = new JComboBox();
		comboBox.addItem("");
		comboBox.addItem("个");
        comboBox.addItem("只");
        comboBox.addItem("件");
        comboBox.addItem("升");
        comboBox.addItem("豪升");
        comboBox.addItem("立方米");
        comboBox.addItem("平方米");
        comboBox.addItem("米");
        comboBox.addItem("克");
        comboBox.addItem("千克");
        return comboBox;
	}

}
