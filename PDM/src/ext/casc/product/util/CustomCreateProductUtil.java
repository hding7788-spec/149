package ext.casc.product.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import wt.fc.QueryResult;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerTemplate;
import wt.inf.template.ContainerTemplateHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.oracle81.OracleDataSource;
import wt.session.SessionHelper;
import wt.util.WTException;

public class CustomCreateProductUtil {

	/**
	 * 获取可用的产品模板
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getEnableProductTemplate() throws SQLException {

		Map<String, String> hashMap = new HashMap<String, String>();
		Connection conn = OracleDataSource.getOracleDataSource().getConnection();
		StringBuffer selectSQL = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			selectSQL.append("SELECT name from WTContainerTemplateMaster where CLASSNAMEKEYCONTAINERREFEREN='wt.inf.container.OrgContainer' and ENABLED='1'");
			ps = conn.prepareStatement(selectSQL.toString());
			rs = ps.executeQuery();
			while (rs.next()) {
				String name = rs.getString("name");
				hashMap.put(name, name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return hashMap;

	}

	/**
	 * 是否存在相同名称产品库
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static boolean hasProduct(String productName) throws Exception {
		Connection conn = OracleDataSource.getOracleDataSource().getConnection();
		StringBuffer selectSQL = new StringBuffer();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			selectSQL.append("select NAMECONTAINERINFO from PDMLINKPRODUCT where NAMECONTAINERINFO='" + productName + "'");
			ps = conn.prepareStatement(selectSQL.toString());
			rs = ps.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
		return false;

	}

	public static Map<String, String> getContainerTemplateList() throws WTException {
		Map<String, String> hashMap = new HashMap<String, String>();
		QueryResult qr = null;
		WTPrincipal wtPrincipal = SessionHelper.manager.getPrincipal();
		WTOrganization wtorganization = OrganizationServicesHelper.manager.getOrganization(wtPrincipal);
		if(wtorganization != null)
		{
			wt.inf.container.OrgContainer orgcontainer = WTContainerHelper.service.getOrgContainer(wtorganization);
			if(orgcontainer != null)
			{
				wt.inf.container.WTContainerRef wtcontainerref = WTContainerHelper.service.getOrgContainerRef(wtorganization);
				qr = ContainerTemplateHelper.service.getEnabledTemplates(wtcontainerref, PDMLinkProduct.class);
			}
		}
		if(qr.size()>0){
			while (qr.hasMoreElements()){
				WTContainerTemplate containerTemplate = (WTContainerTemplate) qr.nextElement();
				if(containerTemplate!=null){
					hashMap.put(containerTemplate.getName(),containerTemplate.getName());
				}
			}
		}
		return hashMap;
	}

}
