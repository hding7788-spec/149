package ext.casc.product.mvc.builder;

import java.rmi.RemoteException;
import java.util.ArrayList;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionForeignKey;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;

import com.ptc.core.ui.resources.ComponentMode;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.DBUtil;

@ComponentBuilder("ext.casc.product.mvc.builder.GongyizongfanganBuilder")
public class GongyizongfanganBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig config, ComponentParams params) throws Exception {
        System.out.println("---------->");
        NmCommandBean commandbean = ((JcaComponentParams) params).getHelperBean().getNmCommandBean();
        WTContained container = commandbean.getContainer();
        String productName = "";
        if (container instanceof PDMLinkProduct) {
            return getGYZFFByContainer((WTContainer) container);

        }
        return null;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig tableConfig = factory.newTableConfig();
        tableConfig.setComponentMode(ComponentMode.VIEW);
        tableConfig.setConfigurable(false);
        tableConfig.setSelectable(true);
        tableConfig.setSingleSelect(true);
        tableConfig.setLabel("工艺总方案");
        tableConfig.setId("ext.casc.product.mvc.builder.GongyizongfanganBuilde");

        ColumnConfig icon = factory.newColumnConfig("type_icon", false);
        tableConfig.addComponent(icon);

        ColumnConfig numberColumnConfig = factory.newColumnConfig("number", true);
        numberColumnConfig.setAutoSize(true);
        numberColumnConfig.setLabel("编号");
        numberColumnConfig.setInfoPageLink(true);
        tableConfig.addComponent(numberColumnConfig);

        ColumnConfig versionColumnConfig = factory.newColumnConfig("versionInfo.identifier.versionId", true);
        versionColumnConfig.setAutoSize(true);
        versionColumnConfig.setLabel("版本");

        tableConfig.addComponent(versionColumnConfig);

        ColumnConfig nameColumnConfig = factory.newColumnConfig("name", true);
        nameColumnConfig.setAutoSize(true);
        nameColumnConfig.setLabel("名称");
        tableConfig.addComponent(nameColumnConfig);

        ColumnConfig lifeColumnConfig = factory.newColumnConfig("state.state", true);
        lifeColumnConfig.setAutoSize(true);
        lifeColumnConfig.setLabel("状态");
        tableConfig.addComponent(lifeColumnConfig);

        ColumnConfig modifyColumnConfig = factory.newColumnConfig("thePersistInfo.modifyStamp", true);
        modifyColumnConfig.setAutoSize(true);
        modifyColumnConfig.setLabel("上次修改时间");
        tableConfig.addComponent(modifyColumnConfig);

        return tableConfig;
    }

    public static ArrayList getGYZFFByContainer(WTContainer c) throws RemoteException, WTException {
        ArrayList list = new ArrayList();
        TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("wt.doc.WTDocument|casc.sast.149.GONGYIZONGFANGAN");
        long typeId = 0;
        if (tdr != null) {
            typeId = tdr.getKey().getBranchId();
        }
        QuerySpec qs = new QuerySpec(WTDocument.class);
        // qs.setAdvancedQueryEnabled(true);
        // int ibaHolderIndex = qs.appendClassList(WTDocument.class, true);
        // 过滤类型
        qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.TYPE_DEFINITION_REFERENCE + "." + TypeDefinitionReference.KEY + "." + TypeDefinitionForeignKey.BRANCH_ID,
                SearchCondition.EQUAL, typeId));
        qs.appendAnd();
        qs.appendWhere(new SearchCondition(WTDocument.class, "containerReference.key.id", SearchCondition.EQUAL, c.getPersistInfo().getObjectIdentifier().getId()));

        qs = new LatestConfigSpec().appendSearchCriteria(qs);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while (qr.hasMoreElements()) {
            WTDocument doc = (WTDocument) qr.nextElement();
            if (!list.contains(doc)) {
                list.add(doc);
            }
        }

        return list;

    }
}
