package ext.casc.sop.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.avalon.framework.container.ContainerUtil;
import org.dom4j.Element;

import wt.doc.WTDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.PersistInfo;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerHelper;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.session.SessionServerHelper;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;

import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.TypeUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.foundation.container.common.ContainerUtility;
import com.ptc.extend.ixb.center.MQExpImpObject;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.bean.DocSopLinkBean;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.util.QueryUtil;
import ext.casc.sop.util.SopUtil;
import ext.casc.util.DocUtil;

@ComponentBuilder("SearchSop_table_id_technicsQuote")
public class YinYongSopBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		String work = String.valueOf(params.getParameter("work"));
		ArrayList<DocSopLinkBean> list = new ArrayList<DocSopLinkBean>();

		if ("search".equals(work)) {
			NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
			String contain = ProcessUtil.getNotNullParam(params.getParameter("containerTypeList"));// 上下文
			String technicsNumber = ProcessUtil.getNotNullParam(params.getParameter("technicsNumber"));// 工艺文件编号
			String technicsName = ProcessUtil.getNotNullParam(params.getParameter("technicsName"));// 工艺文件名称
			String sopTechnicsNumber = ProcessUtil.getNotNullParam(params.getParameter("sopTechnicsNumber"));// sop文件编号
			String sopTechnicsName = ProcessUtil.getNotNullParam(params.getParameter("sopTechnicsName"));// sop文件编号
			int index = 0;
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("SELECT * FROM GL_SOPDOCLINK ");
			if((contain != null && !"".equals(contain))){
				String containerName = ((WTContainer)QueryUtil.getObjectByOid(contain)).getName();
				sqlBuilder.append("WHERE DOCCONTAINER = '").append(containerName).append("'");
				index++;
			}
			if(technicsNumber != null && !"".equals(technicsNumber)){
				if(index==0){
					sqlBuilder.append("WHERE DOCNUMBER LIKE '%").append(technicsNumber).append("%'");
				}else{
					sqlBuilder.append("AND DOCNUMBER LIKE '%").append(technicsNumber).append("%'");
				}
				index++;
			}
			if(technicsName != null && !"".equals(technicsName)){
				if(index==0){
					sqlBuilder.append("WHERE DOCNAME LIKE '%").append(technicsName).append("%'");
				}else{
					sqlBuilder.append("AND DOCNAME LIKE '%").append(technicsName).append("%'");
				}
				index++;
			}
			if(sopTechnicsNumber != null && !"".equals(sopTechnicsNumber)){
				if(index==0){
					sqlBuilder.append("WHERE SOPNUMBER LIKE '%").append(sopTechnicsNumber).append("%'");
				}else{
					sqlBuilder.append("AND SOPNUMBER LIKE '%").append(sopTechnicsNumber).append("%'");
				}
			}
			if(sopTechnicsName != null && !"".equals(sopTechnicsName)){
				if(index==0){
					sqlBuilder.append("WHERE SOPNAME LIKE '%").append(sopTechnicsName).append("%'");
				}else{
					sqlBuilder.append("AND SOPNAME LIKE '%").append(sopTechnicsName).append("%'");
				}
			}
			boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
			DBConnUtil dbUtil = null;
			ResultSet rs = null;
			try {
				dbUtil = new DBConnUtil();
				rs = dbUtil.executeQuery(sqlBuilder.toString());
				while (rs.next()) {
					DocSopLinkBean bean = new DocSopLinkBean();
					String SopNumber1 = rs.getString("SOPNUMBER");
					String SopName1 = rs.getString("SOPNAME");
					String SopNum1 = rs.getString("SOPNUM");
					String DocNumber1 = rs.getString("DOCNUMBER");
					String DocNum1 = rs.getString("DOCNUM");
					String DocName1 = rs.getString("DOCNAME");
					String DocVersion1 = rs.getString("DOCVERSION");
					String SopVersion1 = rs.getString("SOPVERSION");
					bean.setSopNumber(SopNumber1);
					bean.setSopName(SopName1);
					bean.setSopNum(SopNum1);
					bean.setDocNumber(DocNumber1);
					bean.setDocName(DocName1);
					bean.setDocNum(DocNum1);
					bean.setDocVersion(DocVersion1);
					bean.setSopVersion(SopVersion1);
					list.add(bean);
				}
			} catch (Exception e1) {
				throw new WTException(e1);
			} finally {
				SessionServerHelper.manager.setAccessEnforced(flag);
				try {
					if (dbUtil != null) {
						dbUtil.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams params) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		TableConfig tableConfig = factory.newTableConfig();
		tableConfig.setId("SearchSop_table_id_technicsQuote");
		// tableConfig.setComponentMode(ComponentMode.VIEW);
		tableConfig.setSelectable(true);
		tableConfig.setConfigurable(true);

		tableConfig.setActionModel("custom_SearchWorkItem_actions");
		tableConfig.setLabel("工艺引用SOP文件汇总表");

		ColumnConfig sopNumber = factory.newColumnConfig("sopNumber", true);
		sopNumber.setAutoSize(true);
		sopNumber.setLabel("SOP编号");
		tableConfig.addComponent(sopNumber);

		ColumnConfig sopName = factory.newColumnConfig("sopName", true);
		sopName.setAutoSize(true);
		sopName.setLabel("SOP名称");
		tableConfig.addComponent(sopName);

		ColumnConfig technicsNumber = factory.newColumnConfig("docNumber", true);
		technicsNumber.setAutoSize(true);
		technicsNumber.setLabel("工艺规程编号");
		tableConfig.addComponent(technicsNumber);

		ColumnConfig technicsName = factory.newColumnConfig("docName", true);
		technicsName.setAutoSize(true);
		technicsName.setLabel("工艺规程名称");
		tableConfig.addComponent(technicsName);

		ColumnConfig banben = factory.newColumnConfig("yinyongSopVersion", true);
		banben.setAutoSize(true);
		banben.setLabel("工艺规程版本");
		banben.setDataUtilityId("SopObjectDataUtility");
		tableConfig.addComponent(banben);

		ColumnConfig gyrwState = factory.newColumnConfig("yinyongSopState", true);
		gyrwState.setAutoSize(true);
		gyrwState.setLabel("工艺规程状态");
		gyrwState.setDataUtilityId("SopObjectDataUtility");
		tableConfig.addComponent(gyrwState);

		ColumnConfig bianzhizhe = factory.newColumnConfig("yinyongSopBianZhiZhe", true);
		bianzhizhe.setAutoSize(true);
		bianzhizhe.setLabel("编制者");
		bianzhizhe.setDataUtilityId("SopObjectDataUtility");
		tableConfig.addComponent(bianzhizhe);

		ColumnConfig cldexinghao = factory.newColumnConfig("yinyongSopPiZhunTime", true);
		cldexinghao.setAutoSize(true);
		cldexinghao.setLabel("批准时间");
		cldexinghao.setDataUtilityId("SopObjectDataUtility");
		tableConfig.addComponent(cldexinghao);

		return tableConfig;
	}

}
