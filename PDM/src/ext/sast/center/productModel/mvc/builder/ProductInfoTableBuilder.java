package ext.sast.center.productModel.mvc.builder;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.dsvcore.server.utils.PersistableHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.MethodContext;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.WTConnection;
import wt.query.*;
import wt.util.WTException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Wang-ya-qi
 *
 */
@ComponentBuilder("ext.sast.center.productModel.mvc.builder.ProductInfoTableBuilder")
public class ProductInfoTableBuilder extends AbstractComponentBuilder{
	private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentparams) throws Exception {
		NmCommandBean cb = ((JcaComponentParams)componentparams).getHelperBean().getNmCommandBean();
		Object localHostModel = cb.getText().get("localHostModel");
		String localHostModelStr = localHostModel == null?"":(String)localHostModel;
		Object sastModel = cb.getText().get("sastModel");
		String sastModelStr = sastModel == null ? "": (String)sastModel;
		List<PDMLinkProduct> list = new ArrayList<PDMLinkProduct>();
		try {
			boolean isNotNull = sastModel != null && sastModelStr.length()>0;
			if(isNotNull) {
				searchModelInfoBySastModel(list,localHostModelStr,sastModelStr);
			}else {
				getAllLocalHostProduct(list,localHostModelStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return list;
	}
	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel(messageSource.getMessage("PRODUCTMODELLISTTABLEBUILDER_LABEL"));
		table.setSelectable(true);
		table.setSingleSelect(true);
		table.setShowCount(true);
		table.setMenubarName("syncActionProducts");

		ColumnConfig col = factory.newColumnConfig("name", true);
		col.setWidth(400);
		col.setLabel(messageSource.getMessage("PRODUCTMODELLISTTABLEBUILDER_01"));
		table.addComponent(col);

		col = factory.newColumnConfig("sast_model_name", true);
		col.setWidth(400);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel(messageSource.getMessage("PRODUCTMODELLISTTABLEBUILDER_02"));
		table.addComponent(col);

		col = factory.newColumnConfig("sast_model_index", true);
		col.setWidth(400);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel("中心域型号代号");
		table.addComponent(col);
		return table;
	}

	@SuppressWarnings("deprecation")
	private void getAllLocalHostProduct(List<PDMLinkProduct> list,String productName) {
		try {
			QuerySpec qSpec = new QuerySpec(PDMLinkProduct.class);
			if(productName != null &&productName.length()>0) {
				productName = "%"+productName+"%";
				qSpec.appendWhere(new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME,SearchCondition.LIKE, productName));
			}
			ClassAttribute ca = new ClassAttribute(PDMLinkProduct.class, PDMLinkProduct.NAME);
	        OrderBy orderby = new OrderBy(ca, false);
			qSpec.appendOrderBy(orderby,0);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qSpec);
			while(qr.hasMoreElements()) {
				PDMLinkProduct product = (PDMLinkProduct) qr.nextElement();
				list.add(product);
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private void searchModelInfoBySastModel(List<PDMLinkProduct> list ,String localProductName,String productName) {
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet set = null;
		try {
			List<String> productNames = new ArrayList<String>();
			QuerySpec qSpec = new QuerySpec(PDMLinkProduct.class);
			if(localProductName != null && localProductName.length()>0) {
				localProductName = "%"+localProductName+"%";
				qSpec.appendWhere(new SearchCondition(PDMLinkProduct.class, PDMLinkProduct.NAME,SearchCondition.LIKE, localProductName));
			}
			ClassAttribute ca = new ClassAttribute(PDMLinkProduct.class, PDMLinkProduct.NAME);
	        OrderBy orderby = new OrderBy(ca, false);
			qSpec.appendOrderBy(orderby,0);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qSpec);
			while(qr.hasMoreElements()) {
				PDMLinkProduct product = (PDMLinkProduct) qr.nextElement();
				String productOid = PersistenceHelper.getObjectIdentifier(product).getId()+"";
				productNames.add(productOid);
			}

			StringBuilder sb = new StringBuilder();
			sb.append("select m.localHostModel,m.sastModel,m.remarks from ModelInfo m where 1=1");
			if(productName != null && productName.length()>0){
				sb.append(" and m.sastModel like '%"+productName+"%' ");
			}
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			pstmt = conn.prepareStatement(sb.toString());
			 set = pstmt.executeQuery(sb.toString());
			while(set.next()) {
				String localHostModel = set.getString("localHostModel");
				if(!productNames.contains(localHostModel)){
					continue;
				}
				PDMLinkProduct product = (PDMLinkProduct) PersistableHelper.findPersistable("OR:wt.pdmlink.PDMLinkProduct:"+localHostModel);
				list.add(product);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{

				try {
					if(pstmt!=null)
						pstmt.close();
					if(set!=null)
						set.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
