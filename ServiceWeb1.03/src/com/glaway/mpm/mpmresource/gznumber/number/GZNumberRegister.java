package com.glaway.mpm.mpmresource.gznumber.number;

import com.glaway.mpm.mpmresource.gznumber.bean.PropertiesBean;

public class GZNumberRegister {
	private PropertiesBean pb;
	private FormatedNumber fnumber;
	public static final GZNumberRegisterManager manager;

	public GZNumberRegister(PropertiesBean pb, FormatedNumber fnumber) {
		this.pb = pb;
		this.fnumber = fnumber;
	}

	static {
		manager = new GZNumberRegisterManager();
	}

	public void registerNumbers(int volumn, String name) throws Exception {
		Sequence seq;
		GZNumber gnumber;

		manager.registerNumbers(pb, fnumber, volumn, name);
	}
}
