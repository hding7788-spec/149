package ext.casc.workflow.tree.mvc.builder;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.log4j.LogR;
import wt.representation.Representable;
import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ColumnConfig;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TreeConfig;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmHelperBean;

import ext.casc.workflow.tree.ExtRepsAndMarkupsTreeHandler;

@ComponentBuilder({"ext.casc.workflow.tree.mvc.builder.ExtRepsAndMarkupsBuilder"})
public class ExtRepsAndMarkupsBuilder extends AbstractComponentBuilder
{
  private static final String RESOURCE = "com.ptc.windchill.enterprise.wvs.repsAndMarkups.repsAndMarkupsResource";
  private static final Logger log = LogR.getLogger(ExtRepsAndMarkupsBuilder.class.getName());

  ClientMessageSource source = getMessageSource("com.ptc.windchill.enterprise.wvs.repsAndMarkups.repsAndMarkupsResource");

  public Object buildComponentData(ComponentConfig paramComponentConfig, ComponentParams paramComponentParams)
    throws WTException
  {

	  return new ExtRepsAndMarkupsTreeHandler();
  }

  public ComponentConfig buildComponentConfig(ComponentParams paramComponentParams)
    throws WTException
  {
    ComponentConfigFactory localComponentConfigFactory = getComponentConfigFactory();
    TreeConfig localTreeConfig = localComponentConfigFactory.newTreeConfig();
    localTreeConfig.setId("repsAndMarkupsTableId");

    NmHelperBean localNmHelperBean = ((JcaComponentParams)paramComponentParams).getHelperBean();
    HttpServletRequest localHttpServletRequest = (HttpServletRequest)localNmHelperBean.getRequest();
    localTreeConfig.setLabel(getLabel(localHttpServletRequest));
    //localTreeConfig.setActionModel("repsAndMarkups_toolbar");
    localTreeConfig.setSelectable(true);
    localTreeConfig.setHelpContext("RepsAndMarkups_help");

    ((JcaComponentConfig)localTreeConfig).setDescriptorProperty("variableRowHeight", Boolean.valueOf(true));

    ColumnConfig localColumnConfig1 = localComponentConfigFactory.newColumnConfig();
    localColumnConfig1.setId("repsAndMarkupsTypeAndName");
    localColumnConfig1.setSortable(true);
    localColumnConfig1.setExactWidth(true);
    localTreeConfig.addComponent(localColumnConfig1);

    ColumnConfig localColumnConfig2 = localComponentConfigFactory.newColumnConfig();
    localColumnConfig2.setId("nmActions");
    localColumnConfig2.setSortable(false);
    localColumnConfig2.setActionModel("repsAndMarkups_actions_column");
    localTreeConfig.addComponent(localColumnConfig2);

    ColumnConfig localColumnConfig3 = localComponentConfigFactory.newColumnConfig();
    localColumnConfig3.setId("repsAndMarkupsThumbnail");
    localColumnConfig3.setSortable(false);
    localColumnConfig3.setExactWidth(true);
    ((JcaColumnConfig)localColumnConfig3).setDescriptorProperty("justify", "center");
    localTreeConfig.addComponent(localColumnConfig3);

    ColumnConfig localColumnConfig4 = localComponentConfigFactory.newColumnConfig();
    localColumnConfig4.setId("repsAndMarkupsDescription");
    localColumnConfig4.setSortable(true);
    localColumnConfig4.setExactWidth(true);
    localTreeConfig.addComponent(localColumnConfig4);

    ColumnConfig localColumnConfig5 = localComponentConfigFactory.newColumnConfig();
    localColumnConfig5.setId("repsAndMarkupsDetails");
    localColumnConfig5.setSortable(false);
    localColumnConfig5.setExactWidth(true);
    localTreeConfig.addComponent(localColumnConfig5);

    ColumnConfig localColumnConfig6 = localComponentConfigFactory.newColumnConfig();
    localColumnConfig6.setId("repsAndMarkupsLockedBy");
    localColumnConfig6.setSortable(false);
    localColumnConfig6.setExactWidth(true);
    localTreeConfig.addComponent(localColumnConfig6);

    ColumnConfig localColumnConfig7 = localComponentConfigFactory.newColumnConfig();
    localColumnConfig7.setId("repsAndMarkupsOwner");
    localColumnConfig7.setSortable(false);
    localColumnConfig7.setExactWidth(true);
    localTreeConfig.addComponent(localColumnConfig7);

    ColumnConfig localColumnConfig8 = localComponentConfigFactory.newColumnConfig();
    localColumnConfig8.setId("repsAndMarkupsDate");
    localColumnConfig8.setSortable(false);
    localColumnConfig8.setExactWidth(true);
    localColumnConfig8.setAscending(false);

    localTreeConfig.addComponent(localColumnConfig8);
    if (log.isDebugEnabled()) {
      log.debug("Tree Config is ::::" + localTreeConfig);
    }

    return localTreeConfig;
  }

  public String getLabel(HttpServletRequest paramHttpServletRequest)
    throws WTException
  {
    String str = this.source.getMessage("TABLE_LABEL");
    ReferenceFactory localReferenceFactory = new ReferenceFactory();
    WTReference localWTReference = localReferenceFactory.getReference(paramHttpServletRequest.getParameter("oid"));
    if (!Representable.class.isAssignableFrom(localWTReference.getReferencedClass())) {
      str = this.source.getMessage("TABLE_LABEL_ALTERNATE");
    }
    return str;
  }
}