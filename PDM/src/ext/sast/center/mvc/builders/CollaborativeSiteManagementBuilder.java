package ext.sast.center.mvc.builders;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bjsasc.avidm.mq.message.Based;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;

import ext.sast.center.bean.CenterSiteBean;
import ext.sast.center.synch.MQConstants;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.util.WTException;
import wt.util.WTProperties;

/**
 * @ Author     ：LB.
 * @ Date       ：Created in 2019/3/12
 * @ Description：协同站点管理页面
 * @ Modified By：
 */

@ComponentBuilder("ext.sast.center.mvc.builders.CollaborativeSiteManagementBuilder")
public class CollaborativeSiteManagementBuilder extends AbstractComponentBuilder {

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        JcaTableConfig tableConfig = (JcaTableConfig) factory.newTableConfig();
        tableConfig.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
        tableConfig.setId("ext.sast.center.mvc.builders.CollaborativeSiteManagementBuilder");
        tableConfig.setActionModel("custom_collaborativeSiteManagement_actions");
        tableConfig.setSelectable(false);
//        tableConfig.setSingleSelect(true);

        tableConfig.setLabel("协同站点管理");

        ColumnConfig columnConfig1 = factory.newColumnConfig("number", false);
        columnConfig1.setLabel("站点编号");
        tableConfig.addComponent(columnConfig1);

        ColumnConfig columnConfig2 = factory.newColumnConfig("name", false);
        columnConfig2.setLabel("站点名称");
        tableConfig.addComponent(columnConfig2);

        ColumnConfig columnConfig3 = factory.newColumnConfig("ip", false);
        columnConfig3.setLabel("IP");
        tableConfig.addComponent(columnConfig3);

        ColumnConfig columnConfig4 = factory.newColumnConfig("port", false);
        columnConfig4.setLabel("端口");
        tableConfig.addComponent(columnConfig4);

        ColumnConfig columnConfig5 = factory.newColumnConfig("version", false);
        columnConfig5.setLabel("系统版本");
        tableConfig.addComponent(columnConfig5);

        ColumnConfig columnConfig6 = factory.newColumnConfig("siteType", false);
        columnConfig6.setLabel("站点类型");
        tableConfig.addComponent(columnConfig6);

        return tableConfig;
    }

    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) {
        List<CenterSiteBean> centerSiteBeanList = new ArrayList<CenterSiteBean>();

        StringBuilder sb = new StringBuilder();
        WTConnection wtconnection = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        CenterSiteBean centerSiteBean = null;
        ResultSet set = null;

        sb.append("select m.iid,m.id,m.name,m.ip,m.port,m.version,m.academy_id,m.academy_name from SYNCHSITEINFO m");
        try {
            MethodContext methodcontext = MethodContext.getContext();
            wtconnection = (WTConnection) methodcontext.getConnection();
            conn = wtconnection.getConnection();
            pstmt = conn.prepareStatement(sb.toString());
            set = pstmt.executeQuery(sb.toString());
            while (set.next()) {
                //String iid = set.getString("iid");
                String number = set.getString("id");
                String name = set.getString("name");
                String ip = set.getString("ip");
                String port = set.getString("port");
                String version = set.getString("version");
                //String academy_id = set.getString("academy_id");
                //String academy_name = set.getString("academy_name");

                centerSiteBean = new CenterSiteBean();
                centerSiteBean.setNumber(number);
                centerSiteBean.setName(name);
                centerSiteBean.setIp(ip);
                centerSiteBean.setPort(port);
                centerSiteBean.setSiteType("外部域（本部）");
                centerSiteBean.setSysVersion(version);
                centerSiteBeanList.add(centerSiteBean);
            }

            centerSiteBean = new CenterSiteBean();
            centerSiteBean.setNumber(MQConstants.SITEID_149);
            centerSiteBean.setName(MQConstants.SITENAME_149);
            centerSiteBean.setIp(MQConstants.SITEIP_149);
            centerSiteBean.setPort(MQConstants.SITEPORT_149);
            centerSiteBean.setSiteType("本地域（本部）");
            centerSiteBean.setSysVersion(Based.SYS_VERSION_WIN10);
            centerSiteBeanList.add(centerSiteBean);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(set != null){
                    set.close();
                }
                if(pstmt != null){
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return centerSiteBeanList;

    }
}
