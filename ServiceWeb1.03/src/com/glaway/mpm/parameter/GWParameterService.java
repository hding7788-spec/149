package com.glaway.mpm.parameter;

import java.util.List;
import java.util.Vector;

import com.glaway.mpm.parameter.model.GWParamTableTypeMaster;
import com.glaway.mpm.parameter.model.data.CmParamTableType;
import com.glaway.mpm.parameter.model.data.CmParameterType;
import com.glaway.mpm.parameter.model.data.CmTechnicsType;

public interface GWParameterService {
	public abstract List<CmParameterType> loadParameterType();
	public abstract CmParameterType loadParameterType(String technicsType);
	public abstract List<CmTechnicsType> loadParamTableType();
	public abstract CmTechnicsType loadSimpleParamTableType(String technicsType);
	public abstract CmParameterType createParameterType(CmParameterType parameterType);
	public abstract void deleteParameterType(CmParameterType parameterType);
	public abstract CmParameterType saveParameterType(CmParameterType parameterType) throws Exception;
	public abstract List<CmTechnicsType> queryTechnicsTypes();
	public abstract CmParamTableType createParamTableType(CmParamTableType paramTableType);
	public abstract CmParamTableType saveParamTableType(CmParamTableType paramTableType);
	public abstract CmParamTableType queryCmParameterTableType(String gwkey);
	public abstract List<GWParamTableTypeMaster> queryAllParamTableTypeMasters();
	public abstract Vector<Vector<String>> queryParamsByTableId(String tableName, String tableId, String paramTableTypeIid);
	public abstract CmParamTableType getCommonParamTableType(CmParamTableType paramTableType);
	public abstract String queryMaxEnname();
	public abstract String queryMaxNameFromTypeMaster();
}
