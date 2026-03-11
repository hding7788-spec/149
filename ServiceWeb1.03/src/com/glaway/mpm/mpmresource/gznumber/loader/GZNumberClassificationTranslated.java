package com.glaway.mpm.mpmresource.gznumber.loader;

import java.util.HashMap;

import com.glaway.mpm.mpmresource.gznumber.classification.GZNumberClassificationInfoContained;


public interface GZNumberClassificationTranslated {
	public GZNumberClassificationTranslated translate(HashMap record);
	public GZNumberClassificationInfoContained getClassInfo();
	public short getRowBegin();
}
