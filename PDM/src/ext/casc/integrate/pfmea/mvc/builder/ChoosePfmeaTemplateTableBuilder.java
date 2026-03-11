package ext.casc.integrate.pfmea.mvc.builder;

import com.ptc.mvc.components.*;
import com.ptc.mvc.util.ClientMessageSource;
import ext.casc.integrate.pfmea.bean.TemplateBean;
import ext.casc.integrate.pfmea.util.DataBaseUtil;
import ext.sast.center.productModel.bean.SAST_PDMLinkProduct;
import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.util.WTException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChoosePfmeaTemplateTableBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
        return getAllTemplate();
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setLabel("PFMEA模板");
        table.setMenubarName("pfmeaRequestMenubar");
        table.setSelectable(true);
        table.setSingleSelect(true);
        table.setShowCount(true);

        ColumnConfig col = factory.newColumnConfig("templateId", true);
        col.setHidden(true);
        col.setWidth(200);
        table.addComponent(col);

        col = factory.newColumnConfig("templateName", true);
        col.setWidth(200);
        col.setLabel("模板名称");
        table.addComponent(col);

        return table;
    }

    private List<TemplateBean> getAllTemplate() {
        List<TemplateBean> list = new ArrayList<TemplateBean>();
        TemplateBean templateBean;
        Connection connection = null;
        PreparedStatement pstm = null;
        ResultSet resultSet = null;
        try {
            String sql = "select * from PF_TEMPLATE";
            connection = DataBaseUtil.getConnection();
            pstm = connection.prepareStatement(sql);
            resultSet = pstm.executeQuery();
            while (resultSet.next()) {
                String id = resultSet.getString("ID");
                String name = resultSet.getString("NAME");
                templateBean = new TemplateBean();
                templateBean.setTemplateId(id);
                templateBean.setTemplateName(name);
                list.add(templateBean);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pstm != null) {
                    pstm.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

}

