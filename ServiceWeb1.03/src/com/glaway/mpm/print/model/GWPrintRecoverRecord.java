package com.glaway.mpm.print.model;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.glaway.mpm.parameter.service.gwpersistable.GwPersistable;
import com.glaway.mpm.print.data.CmPrintInfoBean;


/**
 * 打印回收记录
 *
 * @author TUWENBIN
 *
 */
public class GWPrintRecoverRecord implements GwPersistable {

	private static final long serialVersionUID = 1L;

	/**
	 * 表列名
	 */
	public static String BARCODE = "BARCODE";							//二维码
	public static String PROCESSOID = "PROCESSOID";						//工艺文件OID
	public static String PROCESSPRINTFILEOID = "PROCESSPRINTFILEOID";	//打印文件单OID
	public static String RECOVERUSER = "RECOVERUSER";					//回收申请人
	public static String RECOVERDEPT = "RECOVERDEPT";					//回收申请部门
	public static String RECOVERQUANTITY = "RECOVERQUANTITY";			//回收申请份数
	public static String RECOVERDATE = "RECOVERDATE";					//回收申请日期
	public static String RECOVERREMARK = "RECOVERREMARK";				//退回备注
	public static String RECEIVEUSER = "RECEIVEUSER";					//收件人
	public static String RECEIVEDEPT = "RECEIVEDEPT";					//收件人部门
	public static String RECEIVEDATE = "RECEIVEDATE";					//收件日期
	public static String RECEIVEQUANTITY = "RECEIVEQUANTITY";			//收件份数

	/**
	 * 属性字段
	 */
	private String gwKey; // 主键
	private String barCode;
	private String processOid;
	private long processPrintFileOid;
	public long recoverUser;
	public String recoverDept;
	public long recoverQuantity;
	public Date recoverDate;
	public String recoverRemark;
	public long receiveUser;
	public String receiveDept;
	public Date receiveDate;
	public long receiveQuantity;

	public static int index = 1;

	public static String generateKeyId() {
		if (index >= 10000) {
			index = 1;
		}
		return String.valueOf(System.currentTimeMillis()) + index++;
	}

	@Override
	public GwPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			this.setGwKey(rs.getString(KEY_ID));
			this.setBarCode(rs.getString(BARCODE));
			this.setProcessOid(rs.getString(PROCESSOID));
			this.setProcessPrintFileOid(rs.getLong(PROCESSPRINTFILEOID));
			this.setRecoverUser(rs.getLong(RECOVERUSER));
			this.setRecoverDept(rs.getString(RECOVERDEPT));
			this.setRecoverQuantity(rs.getLong(RECOVERQUANTITY));
			this.setRecoverDate(rs.getDate(RECOVERDATE));
			this.setRecoverRemark(rs.getString(RECOVERREMARK));
			this.setReceiveUser(rs.getLong(RECEIVEUSER));
			this.setReceiveDept(rs.getString(RECEIVEDEPT));
			this.setReceiveDate(rs.getDate(RECEIVEDATE));
			this.setReceiveQuantity(rs.getLong(RECEIVEQUANTITY));
		}
		return this;
	}

	@Override
	public Object getKeyId() {
		if (this.gwKey != null && !this.gwKey.equals("")) {
			return this.gwKey;
		} else {
			String keyId = generateKeyId();
			this.setGwKey(keyId);
			return keyId;
		}
	}

	@Override
	public Map<String, Object> getUpdateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(BARCODE, barCode);
		ret.put(PROCESSOID, processOid);
		ret.put(PROCESSPRINTFILEOID, processPrintFileOid);
		ret.put(RECOVERUSER, recoverUser);
		ret.put(RECOVERDEPT, recoverDept);
		ret.put(RECOVERQUANTITY, recoverQuantity);
		ret.put(RECOVERDATE, recoverDate);
		ret.put(RECOVERREMARK, recoverRemark);
		ret.put(RECEIVEUSER, receiveUser);
		ret.put(RECEIVEDEPT, receiveDept);
		ret.put(RECEIVEDATE, receiveDate);
		ret.put(RECEIVEQUANTITY, receiveQuantity);
		return ret;
	}

	@Override
	public Map<?, ?> getCreateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(BARCODE, barCode);
		ret.put(PROCESSOID, processOid);
		ret.put(PROCESSPRINTFILEOID, processPrintFileOid);
		ret.put(RECOVERUSER, recoverUser);
		ret.put(RECOVERDEPT, recoverDept);
		ret.put(RECOVERQUANTITY, recoverQuantity);
		ret.put(RECOVERDATE, recoverDate);
		ret.put(RECOVERREMARK, recoverRemark);
		ret.put(RECEIVEUSER, receiveUser);
		ret.put(RECEIVEDEPT, receiveDept);
		ret.put(RECEIVEDATE, receiveDate);
		ret.put(RECEIVEQUANTITY, receiveQuantity);
		return ret;
	}

	public String getGwKey() {
		return gwKey;
	}

	public void setGwKey(String gwKey) {
		this.gwKey = gwKey;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getProcessOid() {
		return processOid;
	}

	public void setProcessOid(String processOid) {
		this.processOid = processOid;
	}

	public long getProcessPrintFileOid() {
		return processPrintFileOid;
	}

	public void setProcessPrintFileOid(long processPrintFileOid) {
		this.processPrintFileOid = processPrintFileOid;
	}

	public long getRecoverUser() {
		return recoverUser;
	}

	public void setRecoverUser(long recoverUser) {
		this.recoverUser = recoverUser;
	}

	public long getRecoverQuantity() {
		return recoverQuantity;
	}

	public void setRecoverQuantity(long recoverQuantity) {
		this.recoverQuantity = recoverQuantity;
	}

	public Date getRecoverDate() {
		return recoverDate;
	}

	public void setRecoverDate(Date recoverDate) {
		this.recoverDate = recoverDate;
	}

	public String getRecoverRemark() {
		return recoverRemark;
	}

	public void setRecoverRemark(String recoverRemark) {
		this.recoverRemark = recoverRemark;
	}

	public long getReceiveUser() {
		return receiveUser;
	}

	public void setReceiveUser(long receiveUser) {
		this.receiveUser = receiveUser;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public long getReceiveQuantity() {
		return receiveQuantity;
	}

	public void setReceiveQuantity(long receiveQuantity) {
		this.receiveQuantity = receiveQuantity;
	}

	public String getRecoverDept() {
		return recoverDept;
	}

	public void setRecoverDept(String recoverDept) {
		this.recoverDept = recoverDept;
	}

	public String getReceiveDept() {
		return receiveDept;
	}

	public void setReceiveDept(String receiveDept) {
		this.receiveDept = receiveDept;
	}

	@Override
	public CmPrintInfoBean clone() {
		CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();

		return cmPrintInfoBean;
	}
}
