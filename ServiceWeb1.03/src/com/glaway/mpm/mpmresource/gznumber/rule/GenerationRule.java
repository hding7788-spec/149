package com.glaway.mpm.mpmresource.gznumber.rule;

import java.util.ArrayList;

public class GenerationRule implements RuleInfoContained{
	private ArrayList fixlabel;
	private String ruleformat;
	
	public String getRuleformat() {
		return ruleformat;
	}
	public void setRuleformat(String ruleformat) {
		this.ruleformat = ruleformat;
	}
	public ArrayList getFixlabel() {
		return fixlabel;
	}
	public void setFixlabel(ArrayList fixlabel) {
		this.fixlabel = fixlabel;
	}

	
	
}
