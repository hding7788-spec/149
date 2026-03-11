package ext.casc.sop.mvc.builders;

import com.glaway.mpm.util.DBConnUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.*;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.sop.constants.SopConstants;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.util.WTException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ComponentBuilder("ext.casc.sop.mvc.builders.RelatedSopAndTechnicsBuilder")
public class RelatedSopAndTechnicsBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
        List<WTDocument> documentList = null;
        NmCommandBean cb = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
        NmOid nmOid = cb.getPageOid();
        Object object = nmOid.getRefObject();
        if(object instanceof WTDocument){
            WTDocument wtDocument = (WTDocument) object;
            long id = wtDocument.getPersistInfo().getObjectIdentifier().getId();
            documentList = getAllRelatedSopDoc(id);
        }
        return documentList;
    }

    private List<WTDocument> getAllRelatedSopDoc(long id) {
        List<WTDocument> documentList = new ArrayList<WTDocument>();
        DBConnUtil dbConnUtil = null;
        ResultSet resultSet = null;
        try {
            dbConnUtil = new DBConnUtil();
            String sql = "select * from GL_DOCPARAMETERSLINK where PARAMETEROID="+id+" and OBJTYPE='"+SopConstants.SOP_TYPE_GUOJIABIAOZHUN +"'";
            resultSet = dbConnUtil.executeQuery(sql);
            while (resultSet.next()){
                String technicsnumber = resultSet.getString("TECHNICSNUMBER");
                String technicsversion = resultSet.getString("TECHNICSVERSION");
                WTDocument document = WTDocumentUtil.getDocumentByNumberAndVersion(technicsnumber, technicsversion);
                if(document != null){
                    documentList.add(document);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(resultSet != null){
                try {
                    resultSet.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
            if(dbConnUtil != null){
                try {
                    dbConnUtil.close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return documentList;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {

        ComponentConfigFactory factory = getComponentConfigFactory();
        JcaTableConfig tableConfig = (JcaTableConfig) factory.newTableConfig();
        tableConfig.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
        tableConfig.setSelectable(false);

        tableConfig.setLabel("关联SOP操作规程");

        ColumnConfig columnConfig = factory.newColumnConfig(WTDocumentMaster.NUMBER, false);
        columnConfig.setLabel("编号");
        tableConfig.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig(WTDocumentMaster.NAME, false);
        columnConfig.setLabel("名称");
        tableConfig.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig("version", false);
        columnConfig.setLabel("版本");
        tableConfig.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig(WTDocument.CONTAINER, false);
        columnConfig.setLabel("上下文");
        tableConfig.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig(WTDocument.STATE, false);
        columnConfig.setLabel("状态");
        tableConfig.addComponent(columnConfig);

        columnConfig = factory.newColumnConfig(WTDocument.MODIFY_TIMESTAMP, false);
        columnConfig.setLabel("上次修改时间");
        tableConfig.addComponent(columnConfig);

        return tableConfig;
    }

}
