package com.glaway.speciaword.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.glaway.speciaword.common.CommonButton;
import com.glaway.speciaword.common.CommonHelper;
import com.glaway.speciaword.common.CommonTextField;
import com.glaway.speciaword.common.SvgTranscoderToPng;
import com.glaway.speciaword.util.SPJavaUtil;

/**
 * 
 * @author mosesx
 * @date 2013-4-25
 * @version V1.0
 */
public class PartToleranceSymbolDialog extends SpecDialog {

	private static final long serialVersionUID = 1L;

	private static final double IMAGE_WIDTH = 24.293;

	private JPanel buttonPanel = new JPanel();
	private JPanel inputPanel = new JPanel();

	private JPanel inputTopPanel = new JPanel();

	private JTextField tolerance1 = new CommonTextField();
	private JTextField tolerance2 = new CommonTextField();
	private JTextField tolerance3 = new CommonTextField();

	private JPanel inputMiddlePanel = new JPanel();

	private JPanel topPanel = new JPanel();
	private JPanel surePanel = new JPanel();

	private JPanel viewPanel = new JPanel();
	private JLabel scanView = new JLabel();

	private Dimension dimension = new Dimension(30, 30);
	private CommonButton button1 = new CommonButton("", dimension);
	private CommonButton button2 = new CommonButton("", dimension);
	private CommonButton button3 = new CommonButton("", dimension);
	private CommonButton button4 = new CommonButton("", dimension);
	private CommonButton button5 = new CommonButton("", dimension);
	private CommonButton button6 = new CommonButton("", dimension);
	private CommonButton button7 = new CommonButton("", dimension);
	private CommonButton button8 = new CommonButton("", dimension);
	private CommonButton button9 = new CommonButton("", dimension);
	private CommonButton button10 = new CommonButton("", dimension);
	private CommonButton button11 = new CommonButton("", dimension);
	private CommonButton button12 = new CommonButton("", dimension);
	private CommonButton button13 = new CommonButton("", dimension);
	private CommonButton button14 = new CommonButton("", dimension);
	private CommonButton button15 = new CommonButton("", dimension);

	private Dimension buttonDimension = new Dimension(60, 30);
	private CommonButton sureButton = new CommonButton("插入", buttonDimension);
	private CommonButton cancelButton = new CommonButton("取消", buttonDimension);
	private EventDocumentListener eventDocumentListener = new EventDocumentListener();

	private final SvgTranscoderToPng transcoder = SvgTranscoderToPng.getInstance();

	private String buttonIndex = null;

	private String imageSrc = null;

	public PartToleranceSymbolDialog(String category, String imageFolder) {
		super(category,imageFolder);
		initComponent();
	}

	public void initComponent() {
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.WEST;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(0, 10, 10, 0);
		for (int i = 1; i <= 3; i++) {
			c.gridy = i;
			for (int j = 1; j <= 5; j++) {
				c.gridx = j;
				int index = (j + (i - 1) * 5);
				buttonPanel.add(callMethod(index), c);
			}
		}

		topPanel.setLayout(new BorderLayout());
		topPanel.add(buttonPanel, BorderLayout.CENTER);

		inputPanel.setPreferredSize(new Dimension(250, 400));
		inputPanel.setLayout(new BorderLayout());

		inputPanel.add(inputTopPanel, BorderLayout.NORTH);
		inputPanel.add(inputMiddlePanel, BorderLayout.CENTER);
		inputPanel.add(surePanel, BorderLayout.SOUTH);

		inputTopPanel.setBorder(new TitledBorder(null, "公差", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		inputTopPanel.add(tolerance1);
		tolerance1.getDocument().addDocumentListener(eventDocumentListener);
		inputMiddlePanel.setBorder(new TitledBorder(null, "局部公差", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		inputMiddlePanel.add(tolerance2);
		inputMiddlePanel.add(tolerance3);
		tolerance2.getDocument().addDocumentListener(eventDocumentListener);
		tolerance3.getDocument().addDocumentListener(eventDocumentListener);
		topPanel.add(inputPanel, BorderLayout.EAST);

		buttonPanel.setBorder(new TitledBorder(null, "符号", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		setLayout(new BorderLayout());
		add(topPanel, BorderLayout.CENTER);
		add(viewPanel, BorderLayout.SOUTH);

		inputTopPanel.setPreferredSize(new Dimension(500, 65));
		inputMiddlePanel.setPreferredSize(new Dimension(500, 65));

		viewPanel.setPreferredSize(new Dimension(500, 150));
		viewPanel.add(scanView);

		viewPanel.setBorder(new TitledBorder(null, "预览", TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, null, null));

		surePanel.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.WEST;
		c1.anchor = GridBagConstraints.WEST;
		c1.gridx = 1;
		c1.gridy = 1;

		c1.insets = new Insets(10, 110, 10, 0);
		surePanel.add(sureButton, c1);

		c1.insets = new Insets(10, 180, 10, 0);
		surePanel.add(cancelButton, c1);

		sureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				imageSrc = CommonHelper.saveImageToLocal(transcoder.makeImage(getCategory(), "jbgch", getParaMap()), ""
						+ System.currentTimeMillis(),getImageFolder());
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				imageSrc = null;
				dispose();
			}
		});

		setSize(500, 380);
		setResizable(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		SPJavaUtil.setMiddle(this);
		setVisible(true);
	}

	private void changePrefixImage(ActionEvent e) {
		Object obj = e.getSource();
		if (obj instanceof CommonButton) {
			CommonButton button = (CommonButton) obj;
			buttonIndex = button.getName();
			changeScanView();
		}
	}

	/**
	 * 改变下方预览
	 */
	private void changeScanView() {
		if (null != buttonIndex) {
			scanView.setIcon(new ImageIcon(transcoder.makeImage(getCategory(), "jbgch", getParaMap())));
		}
	}

	private Map<String, Object> getParaMap() {
		Map<String, Object> mapParam = new HashMap<String, Object>();
		if (null != buttonIndex) {
			mapParam.put("model_1", buttonIndex + "");
			String tolerance1Text = tolerance1.getText();
			String tolerance2Text = tolerance2.getText();
			String tolerance3Text = tolerance3.getText();
			tolerance1Text = SPJavaUtil.replaceAtSymbol(tolerance1Text);
			mapParam.put("tolerance1", tolerance1Text);
			tolerance2Text = SPJavaUtil.replaceAtSymbol(tolerance2Text);
			mapParam.put("tolerance2", tolerance2Text);
			tolerance3Text = SPJavaUtil.replaceAtSymbol(tolerance3Text);
			mapParam.put("tolerance3", tolerance3Text);
			double tolerance1Width = CommonHelper.calculateStringToImageSize(tolerance1Text, 14).getWidth();
			double tolerance2Width = CommonHelper.calculateStringToImageSize(tolerance2Text, 14).getWidth();
			double tolerance3Width = CommonHelper.calculateStringToImageSize(tolerance3Text, 14).getWidth() + 2;
			double downWidth = tolerance2Width + tolerance3Width + 15;
			if (tolerance1Width >= downWidth) {
				mapParam.put("total_width", tolerance1Width + 5);
				mapParam.put("tolerance1_start", 0);
			} else {
				mapParam.put("tolerance1_start", (downWidth - tolerance1Width) / 2);
				mapParam.put("total_width", downWidth + 5);
			}
			mapParam.put("imageWidth", IMAGE_WIDTH);
			mapParam.put("s_line_start", tolerance2Width + 10 + IMAGE_WIDTH);
		}
		return mapParam;
	}

	public static void main(String[] args) {
	}

	public JPanel getButtonPanel() {
		return buttonPanel;
	}

	public void setButtonPanel(JPanel buttonPanel) {
		this.buttonPanel = buttonPanel;
	}

	public JPanel getInputPanel() {
		return inputPanel;
	}

	public void setInputPanel(JPanel inputPanel) {
		this.inputPanel = inputPanel;
	}

	public JPanel getViewPanel() {
		return viewPanel;
	}

	public void setViewPanel(JPanel viewPanel) {
		this.viewPanel = viewPanel;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public void setDimension(Dimension dimension) {
		this.dimension = dimension;
	}

	public CommonButton getButton1() {
		return button1;
	}

	public void setButton1(CommonButton button1) {
		this.button1 = button1;
	}

	public CommonButton getButton2() {
		return button2;
	}

	public void setButton2(CommonButton button2) {
		this.button2 = button2;
	}

	public CommonButton getButton3() {
		return button3;
	}

	public void setButton3(CommonButton button3) {
		this.button3 = button3;
	}

	public CommonButton getButton4() {
		return button4;
	}

	public void setButton4(CommonButton button4) {
		this.button4 = button4;
	}

	public CommonButton getButton5() {
		return button5;
	}

	public void setButton5(CommonButton button5) {
		this.button5 = button5;
	}

	public CommonButton getButton6() {
		return button6;
	}

	public void setButton6(CommonButton button6) {
		this.button6 = button6;
	}

	public CommonButton getButton7() {
		return button7;
	}

	public void setButton7(CommonButton button7) {
		this.button7 = button7;
	}

	public CommonButton getButton8() {
		return button8;
	}

	public void setButton8(CommonButton button8) {
		this.button8 = button8;
	}

	public CommonButton getButton9() {
		return button9;
	}

	public void setButton9(CommonButton button9) {
		this.button9 = button9;
	}

	public CommonButton getButton10() {
		return button10;
	}

	public void setButton10(CommonButton button10) {
		this.button10 = button10;
	}

	public CommonButton getButton11() {
		return button11;
	}

	public void setButton11(CommonButton button11) {
		this.button11 = button11;
	}

	public CommonButton getButton12() {
		return button12;
	}

	public void setButton12(CommonButton button12) {
		this.button12 = button12;
	}

	public CommonButton getButton13() {
		return button13;
	}

	public void setButton13(CommonButton button13) {
		this.button13 = button13;
	}

	public CommonButton getButton14() {
		return button14;
	}

	public void setButton14(CommonButton button14) {
		this.button14 = button14;
	}

	public CommonButton getButton15() {
		return button15;
	}

	public void setButton15(CommonButton button15) {
		this.button15 = button15;
	}

	private static final String IMAGE_PATH = "/com/glaway/speciaword/resource/templates/";

	private CommonButton callMethod(int index) {
		CommonButton commonButton = null;
		try {
			String methodName = "getButton" + index;
			Method method = this.getClass().getMethod(methodName, null);
			commonButton = (CommonButton) method.invoke(this, null);
			String name = "jbgch" + index;
			commonButton.setName(index + "");
			commonButton.setIcon(new ImageIcon(this.getClass().getResource(
					IMAGE_PATH + getCategory() + "/" + name + ".png")));
			commonButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					changePrefixImage(e);
				}
			});
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return commonButton;
	}

	private class EventDocumentListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			changeScanView();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			changeScanView();
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			changeScanView();
		}
	}

	public String getImageSrc() {
		return imageSrc;
	}
}
