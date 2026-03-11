package ext.ases.dataSearch.mvc.builder;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.ases.dataSearch.ObjectDetailDataBean;
import ext.casc.util.DBConn;
import org.apache.commons.lang.StringUtils;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.inf.library.WTLibrary;
import wt.pdmlink.PDMLinkProduct;
import wt.util.WTException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ComponentBuilder("ext.ases.dataSearch.mvc.builder.DataSendRecordBuilder")
public class DataSendRecordBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        // TODO Auto-generated method stub
    	NmCommandBean cb = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
    	NmOid nmOid = cb.getActionOid();

		String containerOid="";
    	if(nmOid != null){
			Object obj = nmOid.getRefObject();
			if(obj instanceof PDMLinkProduct||obj instanceof WTLibrary){
				containerOid = ((Persistable)obj).getPersistInfo().getObjectIdentifier().getStringValue();
			}
		}


		List<ObjectDetailDataBean> objDetailBeans = new ArrayList<ObjectDetailDataBean>();
		String searchFlag = "";
		searchFlag = (String) cb.getText().get("searchFlag");
		if (StringUtils.isBlank(searchFlag)) {
			return objDetailBeans;
		}
		DBConn conn = null;
		try {
			conn = new DBConn();
			String sql = getSearchSQL(cb, containerOid);
			ResultSet resultset = conn.executeQuery(sql);
			ReferenceFactory rf = new ReferenceFactory();
			while (resultset.next()) {
				String packOid = resultset.getString(1);
				Persistable p = null;
				try {
					p = rf.getReference(packOid).getObject();
				} catch (Exception e) {
					e.printStackTrace();
				}

				String objNumber = resultset.getString(2);
				String objName = resultset.getString(3);
				String version = resultset.getString(4);
				String objOid = resultset.getString(5);

				try {
					p = rf.getReference(objOid).getObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (p == null) {
					continue;
				}
				String productCode = resultset.getString(6);
				String sendType = resultset.getString(7);
				Date sendDate = resultset.getDate(8);
				String packNumber = resultset.getString(9);
				ObjectDetailDataBean objDetailBean = new ObjectDetailDataBean(
						objNumber, objName, version, objOid, sendDate, sendType,
						productCode, packNumber, packOid);
				objDetailBeans.add(objDetailBean);
			}
		}catch (Exception e){
			e.printStackTrace();
		}finally {
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return objDetailBeans;

    }

    private static String getSearchSQL(Object obj, String containerOid) {
		NmCommandBean cb = (NmCommandBean) obj;
		String number = (String) cb.getText().get("number");
		String name = (String) cb.getText().get("name");
		String sendStartDate = (String) cb.getText().get("sendStart");
		String sendEndDate = (String) cb.getText().get("sendEnd");
		String sendType = (String) cb.getText().get("sendtype");
		String productCode = (String) cb.getText().get("pc");
		String packNum = (String) cb.getText().get("packNum");
		StringBuffer selectSQL = new StringBuffer();


		selectSQL
		.append("select detail.PACKAGE_OID,detail.obj_number,detail.obj_name,detail.obj_version,detail.obj_oid,"
				+ "detail.product_code,detail.SEND_TYPE,detail.send_date,detail.PACKAGE_NUM from "
				+ "ASES_DATA_SEND_TABLE detail where detail.UNDERREVIEW='是' ");

		if(!"".equals(containerOid)){
			selectSQL.append(" and detail.containerOID='" + containerOid + "'");
		}

		if (StringUtils.isNotBlank(sendType)) {
			selectSQL.append(" and detail.SEND_TYPE='" + sendType + "'");
		}
		if (StringUtils.isNotBlank(number)) {
			selectSQL.append(" and detail.OBJ_NUMBER like '%" + number + "%'");
		}
		if (StringUtils.isNotBlank(name)) {
			selectSQL.append(" and detail.OBJ_NAME like '%" + name + "%'");
		}
		if (StringUtils.isNotBlank(productCode)) {
			selectSQL.append(" and detail.PRODUCT_CODE like '%" + productCode
					+ "%'");
		}
		if (StringUtils.isNotBlank(packNum)) {
			selectSQL
					.append(" and detail.PACKAGE_NUM like '%" + packNum + "%'");
		}
		if (StringUtils.isNotBlank(sendStartDate)) {
			selectSQL
					.append(" and detail.send_date >= to_date('"+sendStartDate+"','yyyy/mm/dd')");
		}
		if (StringUtils.isNotBlank(sendEndDate)) {
			selectSQL
					.append(" and detail.send_date <= to_date('"+sendEndDate+"','yyyy/mm/dd')");
		}

		return selectSQL.toString();
	}

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
    	NmCommandBean cb = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
    	NmOid nmOid = cb.getActionOid();
		String containerOid="";
		Object obj = null;
    	if(nmOid == null){
			nmOid = cb.getPageOid();
		}
    	if(nmOid != null){
			obj = nmOid.getRefObject();
		}
		System.out.println("obj ########## "+obj);
		if(obj != null){
			if(obj instanceof PDMLinkProduct||obj instanceof WTLibrary){
				containerOid = nmOid.toString();
			}
		}
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setLabel("数据发放条目");
		tableConfig.setActionModel("custom_export_processTask");
        ColumnConfig columnConfig1 = factory.newColumnConfig("packNum", false);
        columnConfig1.setLabel("单号");
        columnConfig1.setDataUtilityId("DataSendRecordUtility");
        tableConfig.addComponent(columnConfig1);

        ColumnConfig columnConfig2 = factory.newColumnConfig("number", false);
        columnConfig2.setLabel("编号");
        columnConfig2.setDataUtilityId("DataSendRecordUtility");
        tableConfig.addComponent(columnConfig2);

        ColumnConfig columnConfig3 = factory.newColumnConfig("name", false);
        columnConfig3.setLabel("名称");
        tableConfig.addComponent(columnConfig3);

        ColumnConfig columnConfig4 = factory.newColumnConfig("objVersion", false);
        columnConfig4.setLabel("版本");
        tableConfig.addComponent(columnConfig4);

        ColumnConfig columnConfig5 = factory.newColumnConfig("sendDate", false);
        columnConfig5.setLabel("发放时间");
        tableConfig.addComponent(columnConfig5);

        ColumnConfig columnConfig6 = factory.newColumnConfig("sendType", false);
        columnConfig6.setLabel("发放类型");
        tableConfig.addComponent(columnConfig6);

        if("".equals(containerOid)){
        	  ColumnConfig columnConfig7 = factory.newColumnConfig("container", false);
              columnConfig7.setLabel("所属产品");
              columnConfig7.setDataUtilityId("DataSendRecordUtility");
              tableConfig.addComponent(columnConfig7);
        }

        return tableConfig;
    }

}
