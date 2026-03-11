package com.glaway.mpm.parameter.commonui;

public class CheckValue {
	public boolean bolValue = false;
	public String value = null;

	public CheckValue() {
	}

	public CheckValue(boolean bolValue, String value) {
		this.bolValue = bolValue;
		this.value = value;
	}

	@Override
	public String toString() {
		return "CheckValue [bolValue=" + bolValue + ", value=" + value + "]";
	}
	
}
