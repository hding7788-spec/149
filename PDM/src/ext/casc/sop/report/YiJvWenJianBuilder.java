package ext.casc.sop.report;

import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.UserUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.process.util.ProcessUtil;
import ext.casc.sop.bean.DocParametersLinkBean;
import ext.casc.sop.constants.SopConstants;
import wt.org.WTUser;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@ComponentBuilder("SearchSop_table_id_technicsYiJv")
public class YiJvWenJianBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
		String work = String.valueOf(params.getParameter("work"));
		ArrayList<DocParametersLinkBean> list = new ArrayList<DocParametersLinkBean>();
		if ("search".equals(work)) {
			NmCommandBean commandBean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
			String bianzhizhe = ProcessUtil.getNotNullParam(params.getParameter("bianzhizhe"));// 上下文
			String tectype = ProcessUtil.getNotNullParam(params.getParameter("tectype"));// 工艺类型
			String gistNumber = ProcessUtil.getNotNullParam(params.getParameter("gistNumber"));// 依据文件编号
			String gistName = ProcessUtil.getNotNullParam(params.getParameter("gistName"));// 依据文件名称
			String technicsNumber = ProcessUtil.getNotNullParam(params.getParameter("sopTechnicsNumber"));// 工艺文件编号
			String technicsName = ProcessUtil.getNotNullParam(params.getParameter("sopTechnicsName"));// 工艺文件名称
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("SELECT * FROM GL_DOCPARAMETERSLINK WHERE OBJTYPE ='").append(SopConstants.SOP_TYPE_GUOJIABIAOZHUN).append("'");
			if (bianzhizhe != null && !"".equals(bianzhizhe)) {
				bianzhizhe = bianzhizhe.split(",")[0].split("=")[1];
				WTUser user = UserUtil.getWTUserByName(bianzhizhe);
				String fullNasme = user.getFullName();
				sqlBuilder.append("AND BIANZHIZHE = '").append(fullNasme).append("'");
            }
			if(tectype !=null && !"".equals(tectype)){
				String type = "";
				if(tectype.equals(SopConstants.JSP_SEARCH_GISTTECHNICS)){
					type = "GIST";
				}else if(tectype.equals(SopConstants.JSP_SEARCH_SOPTECHNICS)){
					type = "SOP";
				}
				sqlBuilder.append("AND TECTYPE ='").append(type).append("'");
			}
			if (gistNumber != null && !"".equals(gistNumber)) {
				sqlBuilder.append("AND GISTNUMBER LIKE '%").append(gistNumber).append("%'");
			}
			if (gistName != null && !"".equals(gistName)) {
				sqlBuilder.append("AND GISTNAME LIKE '%").append(gistName).append("%'");
			}
			if (technicsNumber != null && !"".equals(technicsNumber)) {
				sqlBuilder.append("AND PPNUMBER LIKE '%").append(technicsNumber).append("%'");
			}
			if (technicsName != null && !"".equals(technicsName)) {
				sqlBuilder.append("AND TECHNICSNAME LIKE '%").append(technicsName).append("%'");
			}
			boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
			DBConnUtil dbUtil = null;
			ResultSet rs = null;
			try {
				dbUtil = new DBConnUtil();
				rs = dbUtil.executeQuery(sqlBuilder.toString());
				while (rs.next()) {
					DocParametersLinkBean bean = new DocParametersLinkBean();
					String gistNumber1 = rs.getString("GISTNUMBER");
					String gistName1 = rs.getString("GISTNAME");
					String bianzhizhe1 = rs.getString("BIANZHIZHE");
					String sopNumber1 = rs.getString("PPNUMBER");
					String sopName1 = rs.getString("TECHNICSNAME");
					String tecNumber = rs.getString("TECHNICSNUMBER");
					String tecVision = rs.getString("TECHNICSVERSION");
					bean.setGistNumber(gistNumber1);
					bean.setGistName(gistName1);
					bean.setBianzhizhe(bianzhizhe1);
					bean.setPpnumber(sopNumber1);
					bean.setTechnicsName(sopName1);
					bean.setTechnicsNumber(tecNumber);
					bean.setTechnicsVersion(tecVision);
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
		tableConfig.setId("SearchSop_table_id_technicsYiJv");
		// tableConfig.setComponentMode(ComponentMode.VIEW);
		tableConfig.setSelectable(true);
		tableConfig.setConfigurable(true);

		tableConfig.setActionModel("custom_SearchWorkItem_actions");
		tableConfig.setLabel("依据文件引用情况汇总表");

		ColumnConfig yiJvNumber = factory.newColumnConfig("gistNumber", true);
		yiJvNumber.setAutoSize(true);
		yiJvNumber.setLabel("依据文件编号");
		tableConfig.addComponent(yiJvNumber);

		ColumnConfig yiJvName = factory.newColumnConfig("gistName", true);
		yiJvName.setAutoSize(true);
		yiJvName.setLabel("依据文件名称");
		tableConfig.addComponent(yiJvName);

		ColumnConfig technicsNumberYiJv = factory.newColumnConfig("ppnumber", true);
		technicsNumberYiJv.setAutoSize(true);
		technicsNumberYiJv.setLabel("工艺文件编号");
		tableConfig.addComponent(technicsNumberYiJv);

		ColumnConfig technicsNameYiJv = factory.newColumnConfig("technicsName", true);
		technicsNameYiJv.setAutoSize(true);
		technicsNameYiJv.setLabel("工艺文件名称");
		tableConfig.addComponent(technicsNameYiJv);

		ColumnConfig banben = factory.newColumnConfig("yiJvSopVersion", true);
		banben.setAutoSize(true);
		banben.setLabel("版本");
		banben.setDataUtilityId("SopObjectDataUtility");
		tableConfig.addComponent(banben);

		ColumnConfig yiJvState = factory.newColumnConfig("yiJvSopState", true);
		yiJvState.setAutoSize(true);
		yiJvState.setLabel("状态");
		yiJvState.setDataUtilityId("SopObjectDataUtility");
		tableConfig.addComponent(yiJvState);

		ColumnConfig bianzhizheYiJv = factory.newColumnConfig("bianzhizhe", true);
		bianzhizheYiJv.setAutoSize(true);
		bianzhizheYiJv.setLabel("编制者");
		tableConfig.addComponent(bianzhizheYiJv);

		ColumnConfig yiJvPiZhun = factory.newColumnConfig("yiJvSopPiZhun", true);
		yiJvPiZhun.setAutoSize(true);
		yiJvPiZhun.setLabel("批准时间");
		yiJvPiZhun.setDataUtilityId("SopObjectDataUtility");
		tableConfig.addComponent(yiJvPiZhun);

		return tableConfig;
	}

}
