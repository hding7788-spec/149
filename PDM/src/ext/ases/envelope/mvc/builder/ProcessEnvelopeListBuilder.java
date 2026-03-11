package ext.ases.envelope.mvc.builder;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTException;

import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.mvc.components.AbstractComponentBuilder;
import com.ptc.mvc.components.ComponentBuilder;
import com.ptc.mvc.components.ComponentConfig;
import com.ptc.mvc.components.ComponentConfigFactory;
import com.ptc.mvc.components.ComponentParams;
import com.ptc.mvc.components.TableConfig;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.constants.Constants;
import ext.casc.preview.Preview;

@ComponentBuilder("ext.ases.envelope.mvc.builder.ProcessEnvelopeListBuilder")
public class ProcessEnvelopeListBuilder extends AbstractComponentBuilder {

    @Override
    public Object buildComponentData(ComponentConfig arg0, ComponentParams params) throws Exception {
        NmCommandBean commandBean = ((JcaComponentParams)params).getHelperBean().getNmCommandBean();
        WTContainer container = commandBean.getContainer();
        WTUser currentuser = (WTUser)SessionHelper.getPrincipal();
        ContainerTeamManaged teamManaged = (ContainerTeamManaged) container;
        ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam(teamManaged);
        Vector<Role> vector = containerTeam.getRoles();
        Iterator<Role> iterator = vector.iterator();
        Role role = null;
        boolean flag = false;
        while (iterator.hasNext()) {
            role = iterator.next();
            String name = role.getDisplay(Locale.CHINA);
            if (Constants.ROLE_PRODUCTMANAGER.equals(name)) {
                ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
                for (WTPrincipalReference ref : allUser) {
                    Persistable per = ref.getObject();
                    if (per instanceof WTUser) {
                        WTUser user = (WTUser) per;
                        if (currentuser.getName().equals(user.getName())) {
                            flag = true;
                        }
                    }
                }
            }
            if (Constants.ROLE_ZHURENGONGYISHI.equals(name)) {
                ArrayList<WTPrincipalReference> allUser = containerTeam.getAllPrincipalsForTarget(role);
                for (WTPrincipalReference ref : allUser) {
                    Persistable per = ref.getObject();
                    if (per instanceof WTUser) {
                        WTUser user = (WTUser) per;
                        if (currentuser.getName().equals(user.getName())) {
                            flag = true;
                        }
                    }
                }
            }
        }

        List qResult = queryAllWaiBuDatasForUser(container, currentuser,flag);
        return qResult;
    }

    @Override
    public ComponentConfig buildComponentConfig(ComponentParams arg0) throws WTException {
        ComponentConfigFactory factory = getComponentConfigFactory();
        TableConfig table = factory.newTableConfig();
        table.setSelectable(true);
        table.setLabel("外部数据包");
        //table.setActionModel("EnvelopeList_table_toolbar");
        table.addComponent(factory.newColumnConfig("name", true));
        table.addComponent(factory.newColumnConfig("number", true));
        table.addComponent(factory.newColumnConfig("creator", false));
        table.addComponent(factory.newColumnConfig("thePersistInfo.createStamp", false));
        table.addComponent(factory.newColumnConfig("thePersistInfo.modifyStamp", false));
        table.addComponent(factory.newColumnConfig("state", false));
        table.addComponent(factory.newColumnConfig("description", false));
        return table;
    }

    private static QueryResult queryEnvelopeForUser(WTContainer container,WTUser user,boolean flag) throws WTException, RemoteException{
        long userId = PersistenceHelper.getObjectIdentifier(user).getId();
        long containerId = PersistenceHelper.getObjectIdentifier(container).getId();
        QuerySpec qSpec = new QuerySpec(ProcessEnvelope.class);
        int[] index = {0};

        SearchCondition sCondition = new SearchCondition(ProcessEnvelope.class,"containerReference.key.id",SearchCondition.EQUAL,containerId);
        qSpec.appendWhere(sCondition,index);
        qSpec.appendAnd();

        qSpec.appendOpenParen();
        TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference("ext.ases.envelope.ProcessEnvelope|casc.sast.149.APPROVEFORM");
		long typeId = 0;
		if (tdr != null) {
			typeId = tdr.getKey().getBranchId();
		}
		long typeId2 = 0;
		TypeDefinitionReference tdr2 = ClientTypedUtility.getTypeDefinitionReference("ext.ases.envelope.ProcessEnvelope|casc.sast.149.RELEASEFORM");
		if (tdr2 != null) {
			typeId2 = tdr2.getKey().getBranchId();
		}
		qSpec.appendWhere(new SearchCondition(ProcessEnvelope.class,
				    "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
				     new int[]{0});
		qSpec.appendOr();
		qSpec.appendWhere(new SearchCondition(ProcessEnvelope.class,
			    "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId2),
			     new int[]{0});
		qSpec.appendCloseParen();

        if (!flag) {
            qSpec.appendAnd();
            SearchCondition sCondition2 = new SearchCondition(ProcessEnvelope.class,"creator.key.id",SearchCondition.EQUAL,userId);
            qSpec.appendWhere(sCondition2,index);
        }
        return PersistenceHelper.manager.find((StatementSpec)qSpec);
    }
    private static QueryResult queryChangePackedForUser(WTContainer container,WTUser user,boolean flag) throws WTException{
        long userId = PersistenceHelper.getObjectIdentifier(user).getId();
        long containerId = PersistenceHelper.getObjectIdentifier(container).getId();
        QuerySpec qSpec = new QuerySpec(ChangePackaged.class);
        int[] index = {0};

        SearchCondition sCondition = new SearchCondition(ChangePackaged.class,"containerReference.key.id",SearchCondition.EQUAL,containerId);
        qSpec.appendWhere(sCondition,index);
        if (!flag) {
            qSpec.appendAnd();
            SearchCondition sCondition2 = new SearchCondition(ChangePackaged.class,"creator.key.id",SearchCondition.EQUAL,userId);
            qSpec.appendWhere(sCondition2,index);
        }
        return PersistenceHelper.manager.find((StatementSpec)qSpec);
    }
    private static QueryResult queryPreviewForUser(WTContainer container,WTUser user,boolean flag) throws WTException{
        long userId = PersistenceHelper.getObjectIdentifier(user).getId();
        long containerId = PersistenceHelper.getObjectIdentifier(container).getId();
        QuerySpec qSpec = new QuerySpec(Preview.class);
        int[] index = {0};

        SearchCondition sCondition = new SearchCondition(Preview.class,"containerReference.key.id",SearchCondition.EQUAL,containerId);
        qSpec.appendWhere(sCondition,index);
        if (!flag) {
            qSpec.appendAnd();
            SearchCondition sCondition2 = new SearchCondition(Preview.class,"creator.key.id",SearchCondition.EQUAL,userId);
            qSpec.appendWhere(sCondition2,index);
        }
        return PersistenceHelper.manager.find((StatementSpec)qSpec);
    }

    private static List queryAllWaiBuDatasForUser(WTContainer container,WTUser user,boolean flag) throws WTException, RemoteException{
    	List result = new ArrayList();
    	QueryResult qr  = queryEnvelopeForUser(container,user,flag);
    	while(qr.hasMoreElements()){
    		result.add(qr.nextElement());
    	}

    	qr  = queryChangePackedForUser(container,user,flag);
    	while(qr.hasMoreElements()){
    		result.add(qr.nextElement());
    	}
    	return result;

    }
}
