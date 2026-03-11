package com.glaway.mpm.print.model;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.data.CmPrintInfoBean;
import com.glaway.mpm.print.util.PrintUtil;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistable;

/**
 * 打印分发记录
 *
 * @author TUWENBIN
 *
 */
public class GWPrintDistributeRecord implements GwPersistable {

	private static final long serialVersionUID = 1L;

	/**
	 * 表列名
	 */
	public static String BARCODE = "BARCODE";							//二维码
	public static String PROCESSOID = "PROCESSOID";						//工艺文件OID
	public static String DISTRIBUTEUSER = "DISTRIBUTEUSER";				//分发人
	public static String DISTRIBUTEDEPT = "DISTRIBUTEDEPT";				//分发部门
	public static String DISTRIBUTEQUANTITY = "DISTRIBUTEQUANTITY";		//分发份数
	public static String DISTRIBUTEDATE = "DISTRIBUTEDATE";				//分发日期
	public static String GETQUANTITY = "GETQUANTITY";					//领取份数
	public static String GETUSER = "GETUSER";							//领取人
	public static String GETDEPT = "GETDEPT";							//领取部门
	public static String GETDATE = "GETDATE";							//领取时间

	/**
	 * 属性字段
	 */
	private String gwKey; // 主键
	private String barCode; // 主键
	private String processOid;
	public long distributeUser;
	private String distributeDept;
	public long distributeQuantity;
	public Date distributeDate;
	public long getQuantity;
	public long getUser;
	public String getDept;
	public Date getDate;

	public static int index = 1;

	public static String generateKeyId() {
		return PrintUtil.getPrintObjNumber(PrintServerConstants.NUMBER_OBJTYPE_PRINTDISTRIBUTE, PrintServerConstants.NUMBER_OBJTYPE_PRINTDISTRIBUTE_PREFIX);
	}

	@Override
	public GwPersistable getObject(ResultSet rs) throws Exception {
		if (rs != null) {
			this.setGwKey(rs.getString(KEY_ID));
			this.setBarCode(rs.getString(BARCODE));
			this.setProcessOid(rs.getString(PROCESSOID));
			this.setDistributeUser(rs.getLong(DISTRIBUTEUSER));
			this.setDistributeDept(rs.getString(DISTRIBUTEDEPT));
			this.setDistributeQuantity(rs.getLong(DISTRIBUTEQUANTITY));
			this.setDistributeDate(rs.getDate(DISTRIBUTEDATE));
			this.setGetQuantity(rs.getLong(GETQUANTITY));
			this.setGetUser(rs.getLong(GETUSER));
			this.setGetDept(rs.getString(GETDEPT));
			this.setGetDate(rs.getDate(GETDATE));
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
		ret.put(DISTRIBUTEUSER, distributeUser);
		ret.put(DISTRIBUTEDEPT, distributeDept);
		ret.put(DISTRIBUTEQUANTITY, distributeQuantity);
		ret.put(DISTRIBUTEDATE, distributeDate);
		ret.put(GETQUANTITY, getQuantity);
		ret.put(GETUSER, getUser);
		ret.put(GETDEPT, getDept);
		ret.put(GETDATE, getDate);
		return ret;
	}

	@Override
	public Map<?, ?> getCreateMap() {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put(BARCODE, barCode);
		ret.put(PROCESSOID, processOid);
		ret.put(BARCODE, barCode);
		ret.put(PROCESSOID, processOid);
		ret.put(DISTRIBUTEUSER, distributeUser);
		ret.put(DISTRIBUTEDEPT, distributeDept);
		ret.put(DISTRIBUTEQUANTITY, distributeQuantity);
		ret.put(DISTRIBUTEDATE, distributeDate);
		ret.put(GETQUANTITY, getQuantity);
		ret.put(GETUSER, getUser);
		ret.put(GETDEPT, getDept);
		ret.put(GETDATE, getDate);
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

	public String getDistributeDept() {
		return distributeDept;
	}

	public void setDistributeDept(String distributeDept) {
		this.distributeDept = distributeDept;
	}

	public long getDistributeQuantity() {
		return distributeQuantity;
	}

	public void setDistributeQuantity(long distributeQuantity) {
		this.distributeQuantity = distributeQuantity;
	}

	public long getGetQuantity() {
		return getQuantity;
	}

	public void setGetQuantity(long getQuantity) {
		this.getQuantity = getQuantity;
	}

	public long getGetUser() {
		return getUser;
	}

	public void setGetUser(long getUser) {
		this.getUser = getUser;
	}

	public String getGetDept() {
		return getDept;
	}

	public void setGetDept(String getDept) {
		this.getDept = getDept;
	}

	public Date getGetDate() {
		return getDate;
	}

	public void setGetDate(Date getDate) {
		this.getDate = getDate;
	}

	public Date getDistributeDate() {
		return distributeDate;
	}

	public void setDistributeDate(Date distributeDate) {
		this.distributeDate = distributeDate;
	}

	public long getDistributeUser() {
		return distributeUser;
	}

	public void setDistributeUser(long distributeUser) {
		this.distributeUser = distributeUser;
	}

	@Override
	public CmPrintInfoBean clone() {
		CmPrintInfoBean cmPrintInfoBean = new CmPrintInfoBean();
		cmPrintInfoBean.setQrCode(this.barCode);
		cmPrintInfoBean.setOid(this.processOid);

		return cmPrintInfoBean;
	}
}
