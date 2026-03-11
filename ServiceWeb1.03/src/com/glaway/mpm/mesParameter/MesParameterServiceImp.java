package com.glaway.mpm.mesParameter;

import java.util.Vector;

import com.glaway.mpm.parameter.GWParameterTableTypeManager;
import com.glaway.mpm.parameter.model.data.CmParamTableType;

public class MesParameterServiceImp implements MesParameterService{

	@Override
	public Vector<Object> getTechnicsByTechnicNumber(String technicNumber) {
		return MesParameterTypeManager.getTechnicsByTechnicNumber(technicNumber);
	}

	@Override
	public CmParamTableType getCommonParamTableType(CmParamTableType paramTableType) {
		return GWParameterTableTypeManager.getCommonParamTableType(paramTableType);
	}
}
