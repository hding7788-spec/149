package com.glaway.mpm.qmIntf.frock;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.glaway.mpm.qmIntf.common.model.CommonTextField;
import com.glaway.mpm.util.JavaUtil;

public class FrockTableInfo extends JPanel {
	private static final long serialVersionUID = 1L;
	private Dimension dimension = new Dimension(120, 25);

	private JLabel productNumberLabel = new JLabel("产品代号:");
	private CommonTextField productNumber = new CommonTextField(dimension,
			false);

	private JLabel productionNumberLabel = new JLabel("投产付数:");
	private CommonTextField productionNumber = new CommonTextField(dimension,
			false);

	private JLabel isRegularlyToolsLabel = new JLabel("常用工装:");
	private CommonTextField isRegularlyTools = new CommonTextField(dimension,
			false);

	private JLabel isTestPartLabel = new JLabel("试模件:");
	private CommonTextField isTestPart = new CommonTextField(dimension, false);

	private JLabel insertPartLabel = new JLabel("镶件:");
	private CommonTextField insertPart = new CommonTextField(dimension, false);

	private JLabel isReviewLabel = new JLabel("评审:");
	private CommonTextField isReview = new CommonTextField(dimension, false);

	private JLabel isCommonToolsLabel = new JLabel("共用工装:");
	private CommonTextField isCommonTools = new CommonTextField(dimension,
			false);

	private JLabel workShopLabel = new JLabel("使用单位:");
	private CommonTextField workShop = new CommonTextField(dimension, false);

	private JLabel partNumberLabel = new JLabel("零件图号:");
	private CommonTextField partNumber = new CommonTextField(dimension, false);

	private JLabel wholePartNumberLabel = new JLabel("整件图号:");
	private CommonTextField wholePartNumber = new CommonTextField(dimension,
			false);

	private JLabel inworkLabel = new JLabel("拟制:");
	private CommonTextField inwork = new CommonTextField(dimension, false);

	private JLabel reviewLabel = new JLabel("审核:");
	private CommonTextField review = new CommonTextField(dimension, false);

	private JLabel signLabel = new JLabel("会签:");
	private CommonTextField sign = new CommonTextField(dimension, false);

	private JLabel approveLabel = new JLabel("批准:");
	private CommonTextField approve = new CommonTextField(dimension, false);

	private JLabel attachmentLabel = new JLabel("附件:");

	private JPanel panel = new JPanel();

	// 初始化值
	public void initData(Map map) {
		if (map == null) {
			return;
		}
		List<String> audit = (List<String>) map.get("audit");
		if (audit != null && audit.size() == 4) {
			inwork.setText(audit.get(0));
			review.setText(audit.get(1));
			sign.setText(audit.get(2));
			approve.setText(audit.get(3));
		} else {
			inwork.setText("");
			review.setText("");
			sign.setText("");
			approve.setText("");
		}

		Class<FrockTableInfo> clazz = FrockTableInfo.class;
		Field[] fields = clazz.getDeclaredFields();
		if (fields != null) {
			for (Field field : fields) {
				String fieldName = field.getName();
				if (field.getType().equals(CommonTextField.class)
						&& !fieldName.endsWith("Label")
						&& !fieldName.equals("inwork")
						&& !fieldName.equals("review")
						&& !fieldName.equals("sign")
						&& !fieldName.equals("approve")) {
					try {
						String methodName = JavaUtil
								.getSetMethodName(fieldName);
						Method method = clazz.getDeclaredMethod(methodName,
								String.class);
						if (method != null) {
							method.invoke(this, JavaUtil.transferBoolean(String
									.valueOf(map.get(fieldName))));
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public FrockTableInfo(Map<String, String> map) {
		initData(map);

		panel.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints();
		c1.fill = GridBagConstraints.PAGE_START;
		c1.anchor = GridBagConstraints.NORTHWEST;
		c1.insets = new Insets(5, 5, 5, 5);
		c1.gridx = 1;
		c1.gridy = 1;
		panel.add(productNumberLabel, c1);
		c1.gridx = 2;
		panel.add(productNumber, c1);
		c1.gridx = 3;
		panel.add(productionNumberLabel, c1);
		c1.gridx = 4;
		panel.add(productionNumber, c1);
		c1.gridx = 5;
		panel.add(isRegularlyToolsLabel, c1);
		c1.gridx = 6;
		panel.add(isRegularlyTools, c1);
		c1.gridx = 7;
		panel.add(isTestPartLabel, c1);
		c1.gridx = 8;
		panel.add(isTestPart, c1);

		c1.gridx = 1;
		c1.gridy = 2;
		panel.add(insertPartLabel, c1);
		c1.gridx = 2;
		panel.add(insertPart, c1);
		c1.gridx = 3;
		panel.add(isReviewLabel, c1);
		c1.gridx = 4;
		panel.add(isReview, c1);
		c1.gridx = 5;
		panel.add(isCommonToolsLabel, c1);
		c1.gridx = 6;
		panel.add(isCommonTools, c1);
		c1.gridx = 7;
		panel.add(workShopLabel, c1);
		c1.gridx = 8;
		panel.add(workShop, c1);

		c1.gridx = 1;
		c1.gridy = 3;
		panel.add(partNumberLabel, c1);
		c1.gridx = 2;
		panel.add(partNumber, c1);
		c1.gridx = 3;
		panel.add(wholePartNumberLabel, c1);
		c1.gridx = 4;
		panel.add(wholePartNumber, c1);
		c1.gridx = 5;

		// c1.gridx = 1;
		// c1.gridy = 4;
		// panel.add(attachmentLabel, c1);
		// c1.gridx = 2;
		// c1.gridwidth = 4;
		// JLabel label = new JLabel("123");
		// panel.add(label, c1);

		c1.gridx = 1;
		c1.gridy = 5;
		panel.add(inworkLabel, c1);
		c1.gridx = 2;
		panel.add(inwork, c1);
		c1.gridx = 3;
		panel.add(reviewLabel, c1);
		c1.gridx = 4;
		panel.add(review, c1);
		c1.gridx = 5;
		panel.add(signLabel, c1);
		c1.gridx = 6;
		panel.add(sign, c1);
		c1.gridx = 7;
		panel.add(approveLabel, c1);
		c1.gridx = 8;
		panel.add(approve, c1);

		setLayout(new BorderLayout());

		add(panel, BorderLayout.WEST);

	}

	public void setProductNumber(String productNumber) {
		this.productNumber.setText(productNumber);
	}

	public void setProductionNumber(String productionNumber) {
		this.productionNumber.setText(productionNumber);
	}

	public void setIsRegularlyTools(String isRegularlyTools) {
		this.isRegularlyTools.setText(isRegularlyTools);
	}

	public void setIsTestPart(String isTestPart) {
		this.isTestPart.setText(isTestPart);
	}

	public void setInsertPart(String isReview) {
		this.insertPart.setText(isReview);
	}

	public void setIsReview(String isReview) {
		this.isReview.setText(isReview);
	}

	public void setIsCommonTools(String isCommonTools) {
		this.isCommonTools.setText(isCommonTools);
	}

	public void setWorkShop(String workShop) {
		this.workShop.setText(workShop);
	}

	public void setPartNumber(String partNumber) {
		this.partNumber.setText(partNumber);
	}

	public void setWholePartNumber(String wholePartNumber) {
		this.wholePartNumber.setText(wholePartNumber);
	}

	public void setInwork(String inwork) {
		this.inwork.setText(inwork);
	}

	public void setReview(String review) {
		this.review.setText(review);
	}

	public void setSign(String sign) {
		this.sign.setText(sign);
	}

	public void setApprove(String approve) {
		this.approve.setText(approve);
	}

}
