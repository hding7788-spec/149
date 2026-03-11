package com.glaway.mpm.parameter;

import java.util.List;
import java.util.Vector;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterType;
import com.glaway.mpm.parameter.model.data.CmTechnicsType;

public class GWParameterServiceImp implements GWParameterService {

	private static VaLogger logger = VaLogger.getLogger(GWParameterServiceImp.class.getName());

	@Override
	public List<CmParameterType> loadParameterType() {
		try {
			return GWParameterTypeManager.loadParameterType();
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public CmParameterType loadParameterType(String technicsType) {
		try {
			return GWParameterTypeManager.loadParameterType(technicsType);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public CmParameterType createParameterType(CmParameterType parameterType) {
		return GWParameterTypeManager.createParameterType(parameterType);
	}

	@Override
	public CmParameterType saveParameterType(CmParameterType parameterType) {
		return GWParameterTypeManager.saveParameterType(parameterType);
	}

	@Override
	public List<CmTechnicsType> queryTechnicsTypes() {
		return GWParameterTableTypeManager.queryTechnicsTypes();
	}

	@Override
	public CmParamTableType createParamTableType(CmParamTableType paramTableType) {
		return GWParameterTableTypeManager.createParamTableType(paramTableType);
	}

	@Override
	public CmParamTableType saveParamTableType(CmParamTableType paramTableType) {
		return GWParameterTableTypeManager.saveParamTableType(paramTableType);
	}

	@Override
	public CmParamTableType queryCmParameterTableType(String gwkey) {
		return GWParameterTableTypeManager.queryCmParameterTableType(gwkey);
	}

	@Override
	public List<GWParamTableTypeMaster> queryAllParamTableTypeMasters() {
		return GWParameterTableTypeManager.queryAllParamTableTypeMasters();
	}

	@Override
	public Vector<Vector<String>> queryParamsByTableId(String tableName, String tableId, String paramTableTypeIid) {
		return GWParameterTableTypeManager.queryParamsByTableId(tableName, tableId, paramTableTypeIid);
	}

	@Override
	public List<CmTechnicsType> loadParamTableType() {
		try {
			return GWParameterTableTypeManager.loadParamTableType();
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}
	@Override
	public CmTechnicsType loadSimpleParamTableType(String technicsType) {
		try {
			return GWParameterTableTypeManager.loadSimpleParamTableType(technicsType);
		} catch (Exception e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public CmParamTableType getCommonParamTableType(CmParamTableType paramTableType) {
		return GWParameterTableTypeManager.getCommonParamTableType(paramTableType);
	}

	@Override
	public void deleteParameterType(CmParameterType parameterType) {
		GWParameterTypeManager.deleteParameterType(parameterType);
	}

	@Override
	public String queryMaxEnname() {
		return GWParameterTableTypeManager.queryMaxEnname();
	}

	@Override
	public String queryMaxNameFromTypeMaster() {

		return GWParameterTableTypeManager.queryMaxNameFromTypeMaster();
	}

}
