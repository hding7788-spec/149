package com.glaway.mpm.intf;

import java.util.List;

import wt.method.RemoteAccess;

import com.glaway.mpm.mesDataSearch.MesDataSearchProcesser;
import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.parameter.model.data.CmParamTableType;

public class ProcessMESDataSearchToWCIntfRMI implements RemoteAccess {

	public static List<TempObject> getTechnicsNumberList(String lukahao, String productNumber, String batch, String tuhao){
		return MesDataSearchProcesser.getTechnicsNumberList(lukahao, productNumber, batch, tuhao);
	}

	public static CmParamTableType getCommonParamTableTypeForDataSearch(String technicsNumber, String productNumber, String lukahao){
		return MesDataSearchProcesser.getCommonParamTableTypeForDataSearch(technicsNumber, productNumber, lukahao);
	}
	public static List<CmParamTableType> getMesDataSearchParamTableTypes(String technicsNumber, String productNumber, String lukahao){
		return MesDataSearchProcesser.getMesDataSearchParamTableTypes(technicsNumber, productNumber, lukahao);
	}
}
