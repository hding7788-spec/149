package com.glaway.mpm.mpmresource.gznumber.loader;

import java.util.HashMap;

import com.glaway.mpm.mpmresource.gznumber.bean.RequestInfoContained;
import com.glaway.mpm.mpmresource.gznumber.number.GZNumberInfoContained;



public interface GZNumberTranslated {
	public GZNumberTranslated translate(HashMap record);

	public RequestInfoContained getRequestInfo();
	public GZNumberInfoContained getNumberInfo();
	public short getRowBegin();
}
