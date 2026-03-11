package com.glaway.mpm.mesParameter;

import java.util.Vector;

import com.glaway.mpm.parameter.model.data.CmParamTableType;

public interface MesParameterService {

	public abstract Vector<Object> getTechnicsByTechnicNumber(String technicNumber);

	public CmParamTableType getCommonParamTableType(CmParamTableType paramTableType);

}
