package com.glaway.security.bean;

public class SecretObject {
	private String type;
	private String number;
	private String miji;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMiji() {
		return miji;
	}

	public void setMiji(String miji) {
		this.miji = miji;
	}

	@Override
	public String toString() {
		return "SecretObject [type=" + type + ", number=" + number + ", miji=" + miji + "]";
	}


}
