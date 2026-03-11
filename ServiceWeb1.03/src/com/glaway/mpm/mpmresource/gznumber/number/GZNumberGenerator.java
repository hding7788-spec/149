package com.glaway.mpm.mpmresource.gznumber.number;

import com.glaway.mpm.mpmresource.gznumber.bean.RequestInfoContained;
import com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationInfoContained;
import com.glaway.mpm.mpmresource.gznumber.rule.RuleInfoContained;


public class GZNumberGenerator {
	private GZNumberClassificationInfoContained classification;
	private RuleInfoContained rule;
	private RequestInfoContained rb;

	public GZNumberGenerator(GZNumberClassificationInfoContained classification, RuleInfoContained rule,
			RequestInfoContained rb) {
		this.classification = classification;
		this.rule = rule;
		this.rb = rb;
	}

	public FormatedNumber generateNumber() {
		FormatedNumber result = new FormatedNumber();
		result.setClassification((GZNumberClassificationInfoContained) classification);
		result.setDateTime(rb.getDatetime());
		result.setRequestDesc(rb.getRequestdesc());
		result.setRequestor(rb.getRequestor());

		String ruleFormat = rule.getRuleformat();
		String numberFormat = ruleFormat.replaceAll("<ClassificationValue>", classification.getObjectclassvalue());

		result.setValue(numberFormat);

		return result;
	}
}
