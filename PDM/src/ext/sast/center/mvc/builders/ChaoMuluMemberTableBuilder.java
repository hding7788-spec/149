package ext.sast.center.mvc.builders;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.util.DBConn;
import ext.sast.center.bean.GwChaoMuluMemberLink;

public class ChaoMuluMemberTableBuilder extends AbstractComponentBuilder {
    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
        NmCommandBean cb = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
        String orderNumber = "";
        if (cb.getPageOid() != null) {
            Object obj = cb.getPageOid().getRefObject();
            if (obj instanceof ChangePackaged) {
                ChangePackaged changePackaged = (ChangePackaged) obj;
                orderNumber = changePackaged.getNumber();
            } else if (obj instanceof ProcessEnvelope) {
                ProcessEnvelope processEnvelope = (ProcessEnvelope) obj;
                orderNumber = processEnvelope.getNumber();
            }
        }
        orderNumber = orderNumber.replaceAll("_KYGYHQ","");
        orderNumber = orderNumber.replaceAll("_KYJSHQ","");
        return getYqjList(orderNumber);
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        JcaTableConfig tableConfig = (JcaTableConfig) factory.newTableConfig();
        tableConfig.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
        tableConfig.setSelectable(false);

        tableConfig.setLabel("元器件超目录清单");

        ColumnConfig columnConfig1 = factory.newColumnConfig("yqjNumber", false);
        columnConfig1.setLabel("编号");
        tableConfig.addComponent(columnConfig1);

        ColumnConfig columnConfig2 = factory.newColumnConfig("yqjName", false);
        columnConfig2.setLabel("名称");
       // columnConfig2.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(columnConfig2);

        ColumnConfig columnConfig3 = factory.newColumnConfig("yqjVersion", false);
        columnConfig3.setLabel("版本");
       // columnConfig3.setDataUtilityId("CenterWorkflowInfoUtility");
        tableConfig.addComponent(columnConfig3);

        ColumnConfig columnConfig4 = factory.newColumnConfig("bpmWorkflowNumber", false);
        columnConfig4.setLabel("所关联的元器件送审单");
        tableConfig.addComponent(columnConfig4);
        return tableConfig;
    }

    public List<GwChaoMuluMemberLink> getYqjList(String orderNumber) throws Exception {
        List<GwChaoMuluMemberLink> links = new ArrayList<GwChaoMuluMemberLink>();
        DBConn  dbConn = new DBConn();
        try {
            ResultSet rs =  dbConn.executeQuery("select * from GwChaoMuluMemberLink where orderNumber='"+orderNumber+"'");
            while(rs.next()){
                GwChaoMuluMemberLink link = new GwChaoMuluMemberLink();
                link.setOrderNumber(orderNumber);
                link.setYqjNumber(rs.getString("yqjNumber"));
                link.setYqjName(rs.getString("yqjName"));
                link.setYqjVersion(rs.getString("yqjVersion"));
                link.setBpmWorkflowid(rs.getString("bpmWorkflowid"));
                link.setBpmWorkflowNumber(rs.getString("bpmWorkflowNumber"));
                link.setExternalTypeId(rs.getString("externalTypeId"));
                links.add(link);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            dbConn.close();
        }
       return links;
    }

}
