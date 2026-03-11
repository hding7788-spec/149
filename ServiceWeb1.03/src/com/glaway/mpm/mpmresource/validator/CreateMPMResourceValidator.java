package com.glaway.mpm.mpmresource.validator;

import java.util.ArrayList;

import wt.fc.Persistable;
import wt.folder.Cabinet;
import wt.folder.SubFolder;
import wt.inf.container.WTContainer;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.inf.team.ContainerTeamManaged;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.project.Role;
import wt.session.SessionHelper;

import com.glaway.mpm.mpmresource.Constants;
import com.ptc.core.ui.validation.DefaultSimpleValidationFilter;
import com.ptc.core.ui.validation.UIValidationCriteria;
import com.ptc.core.ui.validation.UIValidationKey;
import com.ptc.core.ui.validation.UIValidationStatus;
import com.ptc.windchill.mpml.resource.MPMTooling;

public class CreateMPMResourceValidator extends DefaultSimpleValidationFilter {
	private static String CLASSNAME = CreateMPMResourceValidator.class.getCanonicalName();

	@Override
	public UIValidationStatus preValidateAction(UIValidationKey key, UIValidationCriteria criteria) {
		UIValidationStatus status = UIValidationStatus.HIDDEN;

	   Persistable po = criteria.getContextObject().getObject();
		//GLLogger.debug(CLASSNAME, po);
		if (po instanceof Cabinet) {
		    try{
		      WTUser   curentuser = (WTUser)SessionHelper.getPrincipal();
			Cabinet cabinet = (Cabinet) po;
			if (Constants.mpmResourceLibraryName.equals(cabinet.getContainerName())) {
			    WTContainer container = cabinet.getContainer();
			    ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged)container);
                Role role = Role.toRole("LIBRARY MANAGER");
                if(role==null){
                    status = UIValidationStatus.HIDDEN;
                }
                ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                for(WTPrincipalReference reference:arrayList){
                    Object object2 = reference.getPrincipal();
                    if(object2 instanceof WTUser){
                        WTUser user = (WTUser)object2;
                        if(user.getName().equals(curentuser.getName())){
                            status = UIValidationStatus.ENABLED;
                        }
                    }
                }
                String folderPath = cabinet.getFolderPath();
                Role zhuren = Role.toRole("ZHURENGONGYISHI");
                ArrayList<WTPrincipalReference> zhurenList = containerTeam.getAllPrincipalsForTarget(zhuren);
                for(WTPrincipalReference reference:zhurenList){
                    Object object2 = reference.getPrincipal();
                    if(object2 instanceof WTUser){
                        WTUser user = (WTUser)object2;
                        if(user.getName().equals(curentuser.getName())){
                            if (folderPath.contains(Constants.DMSBNAME)) {
                                status = UIValidationStatus.ENABLED;
                            }
                        }
                    }
                }

            }
		    }catch(Exception e){
                e.printStackTrace();
            }

		} else if (po instanceof SubFolder) {
		    try{
		        WTUser   curentuser = (WTUser)SessionHelper.getPrincipal();
		        SubFolder folder = (SubFolder) po;
	            if (Constants.mpmResourceLibraryName.equals(folder.getContainerName())) {
	                WTContainer container = folder.getContainer();
	                ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged)container);
	                Role role = Role.toRole("LIBRARY MANAGER");
	                if(role==null){
	                    status = UIValidationStatus.HIDDEN;
	                }
	                ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
	                for(WTPrincipalReference reference:arrayList){
	                    Object object2 = reference.getPrincipal();
	                    if(object2 instanceof WTUser){
	                        WTUser user = (WTUser)object2;
	                        if(user.getName().equals(curentuser.getName())){
	                            status = UIValidationStatus.ENABLED;
	                        }
	                    }
	                }
	                String folderPath = folder.getFolderPath();
	                Role zhuren = Role.toRole("ZHURENGONGYISHI");
	                ArrayList<WTPrincipalReference> zhurenList = containerTeam.getAllPrincipalsForTarget(zhuren);
	                for(WTPrincipalReference reference:zhurenList){
	                    Object object2 = reference.getPrincipal();
	                    if(object2 instanceof WTUser){
	                        WTUser user = (WTUser)object2;
	                        if(user.getName().equals(curentuser.getName())){
	                            if (folderPath.contains(Constants.DMSBNAME)) {
	                                status = UIValidationStatus.ENABLED;
	                            }
	                        }
	                    }
	                }
	            }

		    }catch(Exception e){
		        e.printStackTrace();
		    }

		}else if (po instanceof WTPart) {
		    try{
                WTUser   curentuser = (WTUser)SessionHelper.getPrincipal();
                WTPart folder = (WTPart) po;

                if (Constants.mpmResourceLibraryName.equals(folder.getContainerName())) {
                    WTContainer container = folder.getContainer();
                    ContainerTeam containerTeam = ContainerTeamHelper.service.getContainerTeam((ContainerTeamManaged)container);
                    Role role = Role.toRole("LIBRARY MANAGER");
                    if(role==null){
                        status = UIValidationStatus.HIDDEN;
                    }
                    ArrayList<WTPrincipalReference> arrayList = containerTeam.getAllPrincipalsForTarget(role);
                    for(WTPrincipalReference reference:arrayList){
                        Object object2 = reference.getPrincipal();
                        if(object2 instanceof WTUser){
                            WTUser user = (WTUser)object2;
                            if(user.getName().equals(curentuser.getName())){
                                status = UIValidationStatus.ENABLED;
                            }
                        }
                    }
                    String folderPath = folder.getFolderPath();
                    Role zhuren = Role.toRole("ZHURENGONGYISHI");
                    ArrayList<WTPrincipalReference> zhurenList = containerTeam.getAllPrincipalsForTarget(zhuren);
                    for(WTPrincipalReference reference:zhurenList){
                        Object object2 = reference.getPrincipal();
                        if(object2 instanceof WTUser){
                            WTUser user = (WTUser)object2;
                            if(user.getName().equals(curentuser.getName())){
                                if (folderPath.contains(Constants.DMSBNAME)) {
                                    status = UIValidationStatus.ENABLED;
                                }
                            }
                        }
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

		return status;
	}
}
