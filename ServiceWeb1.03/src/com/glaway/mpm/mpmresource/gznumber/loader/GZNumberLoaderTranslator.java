package com.glaway.mpm.mpmresource.gznumber.loader;

import java.util.HashMap;

import com.glaway.mpm.mpmresource.gznumber.bean.RequestInfoBean;
import com.glaway.mpm.mpmresource.gznumber.bean.RequestInfoContained;
import com.glaway.mpm.mpmresource.gznumber.number.GZNumber;
import com.glaway.mpm.mpmresource.gznumber.number.GZNumberInfoContained;


public class GZNumberLoaderTranslator implements GZNumberTranslated{
	private GZNumberInfoContained numberInfo;
	private RequestInfoBean requestInfo;
	
	public static final short ROW_BEGIN = 1;
	
	public static final short COL_NUMBER = 0;
	public static final short COL_NAME = 1;
	public static final short COL_CLASS = 2;
	public static final short COL_REQUESTOR = 3;
	public static final short COL_DATE = 4;
	public static final short COL_FLAG = 5;
	public static final short COL_DESC = 6;
	public static final short COL_SEQ = 7;
	
	public GZNumberLoaderTranslator(){
		numberInfo = new GZNumber();
		requestInfo = new RequestInfoBean();
	}
	
	public GZNumberTranslated translate(HashMap record){
		String value_number = (String) record.get("ROW " + COL_NUMBER);
		String value_name = (String) record.get("ROW " + COL_NAME );
		String value_class = (String) record.get("ROW " + COL_CLASS );
		String value_requestor = (String) record.get("ROW " + COL_REQUESTOR);
		String value_date =  record.get("ROW " + COL_DATE)==null?"":record.get("ROW " + COL_DATE).toString();
		String value_flag = (String) record.get("ROW " + COL_FLAG);
		String value_desc = (String) record.get("ROW " + COL_DESC);
		String value_seq = (String) record.get("ROW " + COL_SEQ);
		
		numberInfo.setNumber(value_number);
		numberInfo.setClasspath(value_class);
		numberInfo.setSeq(Integer.parseInt(value_seq));
		numberInfo.setFlag(Integer.parseInt(value_flag));
		
		requestInfo.setObjectname(value_name);
		requestInfo.setRequestor(value_requestor);
		requestInfo.setDatetime(value_date);
		requestInfo.setCanceldesc("");
		requestInfo.setRequestdesc(value_desc);

		return this;
	}

	public RequestInfoContained getRequestInfo() {
		return requestInfo;
	}

	public GZNumberInfoContained getNumberInfo(){
		return numberInfo;
	}

	public short getRowBegin() {
		return this.ROW_BEGIN;
	}
}
