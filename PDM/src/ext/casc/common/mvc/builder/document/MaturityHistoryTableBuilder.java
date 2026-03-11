package ext.casc.common.mvc.builder.document;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.*;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;

@ComponentBuilder("ext.casc.common.mvc.builder.document.MaturityHistoryTableBuilder")
public class MaturityHistoryTableBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams arg1)
            throws Exception {
        NmCommandBean cb = ((JcaComponentParams) arg1).getHelperBean().getNmCommandBean();
        Object p = cb.getPageOid().getRefObject();
        List list = new ArrayList();
        EPMDocument edoc = (EPMDocument) p;
        QueryResult qr = PersistenceHelper.manager.navigate(edoc, "theProcessEnvelope", EnvelopeMemberLink.class, false);
        while(qr.hasMoreElements()) {
            EnvelopeMemberLink link=(EnvelopeMemberLink)qr.nextElement();
            ProcessEnvelope pe = link.getProcessEnvelope();
            pe.setDescription(link.getDescription());
            pe.setTopObject(link.getImplementadvise());
            list.add(pe);
        }
        return list;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setLabel("成熟度历史记录");

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        table.addComponent(icon);

        ColumnConfig numberConfig = factory.newColumnConfig("number", false);
        numberConfig.setLabel("预审单编号");
        numberConfig.setInfoPageLink(true);
        numberConfig.setAutoSize(true);
        table.addComponent(numberConfig);

        ColumnConfig nameConfig = factory.newColumnConfig("name", false);
        nameConfig.setLabel("预审单名称");
        nameConfig.setAutoSize(true);
        table.addComponent(nameConfig);

        ColumnConfig modelMaturity = factory.newColumnConfig("description", false);
        modelMaturity.setLabel("模型成熟度");
        modelMaturity.setAutoSize(true);
        table.addComponent(modelMaturity);

        ColumnConfig maturityReason = factory.newColumnConfig("topObject", false);
        maturityReason.setLabel("成熟度变化原因");
        maturityReason.setAutoSize(true);
        table.addComponent(maturityReason);

        ColumnConfig zhuren = factory.newColumnConfig("maturityUser", false);
        zhuren.setLabel("审批人");
        zhuren.setAutoSize(true);
        zhuren.setDataUtilityId("PreviewDataUtility");
        table.addComponent(zhuren);

        ColumnConfig shijian = factory.newColumnConfig("maturityTime", false);
        shijian.setLabel("完成时间");
        shijian.setAutoSize(true);
        shijian.setDataUtilityId("PreviewDataUtility");
        table.addComponent(shijian);

        return table;
    }

}
