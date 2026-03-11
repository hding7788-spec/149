/*jadclipse*/// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3) radix(10) lradix(10)
// Source File Name:   SiteAdminTableBuilder.java

package com.ptc.windchill.principal.site.mvc.builders;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import wt.log4j.LogR;
import wt.org.WTUser;
import wt.util.WTException;

import com.ptc.core.components.util.RequestHelper;
import com.ptc.jca.mvc.components.JcaColumnConfig;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.mvc.util.ClientMessage;
import com.ptc.mvc.util.ClientMessageSource;
import com.ptc.netmarkets.util.beans.NmHelperBean;
import com.ptc.windchill.principal.user.UserCommands;

@ComponentBuilder("principalAdmin.site.listAdmins")
public class SiteAdminTableBuilder extends AbstractComponentBuilder
{

    public SiteAdminTableBuilder()
    {
    }

    public ArrayList buildComponentData(ComponentConfig componentconfig, ComponentParams componentparams)
        throws WTException
    {
        NmHelperBean nmhelperbean = ((JcaComponentParams)componentparams).getHelperBean();
        ArrayList<WTUser> list  = UserCommands.listAdmin(nmhelperbean.getNmCommandBean());
        ArrayList<WTUser> resultList = new ArrayList<WTUser> ();
        for(WTUser u :list){
        	if(!u.getName().equals("Administrator")){
        		resultList.add(u);
        	}

        }
        return resultList;
    }

    public ComponentConfig buildComponentConfig(ComponentParams componentparams)
        throws WTException
    {
        NmHelperBean nmhelperbean = ((JcaComponentParams)componentparams).getHelperBean();
        RequestHelper.setBrowserWinTitle(nmhelperbean.getRequest(), new ClientMessage("com.ptc.core.ui.navigationRB", "WIN_TITLE_SITE_TAB_ADMIN"), false);
        ComponentConfigFactory componentconfigfactory = getComponentConfigFactory();
        TableConfig tableconfig = componentconfigfactory.newTableConfig();
        tableconfig.setType("wt.org.WTUser");
        tableconfig.setLabel(messageSource.getMessage("SITE_ADMIN"));
        tableconfig.setHelpContext("SiteAdminAdminAbout");
        tableconfig.setActionModel("admin toolbar");
        tableconfig.setSelectable(true);
        JcaColumnConfig jcacolumnconfig = (JcaColumnConfig)componentconfigfactory.newColumnConfig("type_icon", false);
        tableconfig.addComponent(jcacolumnconfig);
        JcaColumnConfig jcacolumnconfig1 = (JcaColumnConfig)componentconfigfactory.newColumnConfig("name", false);
        jcacolumnconfig1.setInfoPageLink(true);
        tableconfig.addComponent(jcacolumnconfig1);
        JcaColumnConfig jcacolumnconfig2 = (JcaColumnConfig)componentconfigfactory.newColumnConfig("infoPageAction", false);
        tableconfig.addComponent(jcacolumnconfig2);
        JcaColumnConfig jcacolumnconfig3 = (JcaColumnConfig)componentconfigfactory.newColumnConfig("nmActions", false);
        jcacolumnconfig3.setActionModel("org admin row action");
        tableconfig.addComponent(jcacolumnconfig3);
        if(log.isDebugEnabled())
            log.debug((new StringBuilder()).append("Configured tableConfig : ").append(tableconfig).toString());
        return tableconfig;
    }


    private static final Logger log = LogR.getLogger(com.ptc.windchill.principal.site.mvc.builders.SiteAdminTableBuilder.class.getName());
    private static final String WIN_TITLE_RESOURCE = "com.ptc.core.ui.navigationRB";
    private final ClientMessageSource messageSource = getMessageSource("com.ptc.windchill.principal.user.userResource");

}


/*
	DECOMPILATION REPORT

	Decompiled from: C:\ptc\Windchill_10.0\Windchill\srclib\wnc\PrincipalAdmin.jar
	Total time: 3447 ms
	Jad reported messages/errors:
The class file version is 50.0 (only 45.3, 46.0 and 47.0 are supported)
	Exit status: 0
	Caught exceptions:
*/