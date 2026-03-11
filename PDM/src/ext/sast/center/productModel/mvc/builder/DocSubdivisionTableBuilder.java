package ext.sast.center.productModel.mvc.builder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;

import ext.sast.center.productModel.bean.DocDsubdivisionInfo;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionServerHelper;
import wt.util.WTException;

@ComponentBuilder("ext.sast.center.productModel.mvc.builder.DocSubdivisionTableBuilder")
public class DocSubdivisionTableBuilder extends AbstractComponentBuilder{
	//private final ClientMessageSource messageSource = getMessageSource("ext.sast.center.resource.CustomResource");
	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		List<DocDsubdivisionInfo> list = new ArrayList<DocDsubdivisionInfo>();
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("select m.innerId,m.comeFromSiteName,m.docTypeName,m.docTypeInnerName,m.localDocTypeName,m.localDocTypeInnerName from DOCSUBDIVISION m");
			pstmt = conn.prepareStatement(sb.toString());
			ResultSet set = pstmt.executeQuery();
			while(set.next()) {
				DocDsubdivisionInfo info = new DocDsubdivisionInfo();
				info.setInnerId(set.getString("innerId"));
				info.setComeFromSiteName(set.getString("comeFromSiteName"));
				info.setDocTypeName(set.getString("docTypeName"));
				info.setDocTypeInnerName(set.getString("docTypeInnerName"));
				info.setLocalDocTypeName(set.getString("localDocTypeName"));
				info.setLocalDocTypeInnerName(set.getString("localDocTypeInnerName"));
				list.add(info);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig table = factory.newTableConfig();
		table.setLabel("文档细分");
		table.setSelectable(true);
		table.setSingleSelect(true);
		table.setShowCount(true);
		table.setMenubarName("docSubdivisionAction");

		ColumnConfig col = factory.newColumnConfig("innerId", true);
		col.setHidden(true);
		col.setWidth(200);
		table.addComponent(col);
		
		col = factory.newColumnConfig("comeFromSiteName", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel("来源域");
		table.addComponent(col);

		col = factory.newColumnConfig("docTypeName", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel("源文件类型");
		table.addComponent(col);

		col = factory.newColumnConfig("docTypeInnerName", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel("源文件类型标识");
		table.addComponent(col);

		col = factory.newColumnConfig("localDocTypeName", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel("本地接收类型");
		table.addComponent(col);
		
		col = factory.newColumnConfig("localDocTypeInnerName", true);
		col.setWidth(200);
		col.setDataUtilityId("ModelInfoDatautility");
		col.setLabel("本地接收类型标识");
		table.addComponent(col);
		return table;
	}

}
