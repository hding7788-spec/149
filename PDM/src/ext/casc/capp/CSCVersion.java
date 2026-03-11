package ext.casc.capp;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.Foldered;
import wt.inf.container.WTContainerHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleServerHelper;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.util.WTException;
import wt.vc.IterationIdentifier;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.Versioned;
import ext.casc.constants.Constants;
import ext.casc.util.CSCUtil;

public class CSCVersion {
	public static Versioned setVersionIteration(Versioned versioned,String version,String iteration){
		try {
			Folder folder = null;
			if(versioned instanceof Foldered)
				folder = FolderHelper.getFolder((Foldered)versioned);
			
			versioned = VersionControlHelper.service.newVersion(versioned);			
			MultilevelSeries multilevelseries = MultilevelSeries.newMultilevelSeries("wt.vc.VersionIdentifier", version);							
		    VersionIdentifier versionidentifier = VersionIdentifier.newVersionIdentifier(multilevelseries);        
		    VersionControlHelper.setVersionIdentifier(versioned, versionidentifier);		
		    if(iteration == null)
		    	iteration = "";
		    iteration = iteration.trim();
		    if(iteration.equals(""))
		    	iteration = "0";
		    IterationIdentifier it = IterationIdentifier.newIterationIdentifier(Series.newSeries("wt.vc.IterationIdentifier", iteration));
		    VersionControlHelper.setIterationIdentifier(versioned, it);
		    if(versioned instanceof Foldered){
		    	Foldered foldered = (Foldered)versioned;
		    	FolderHelper.assignLocation(foldered, folder);
		    }
		    LifeCycleHelper.setLifeCycle((WTDocument)versioned, LifeCycleHelper.service.getLifeCycleTemplate("149_Doc_LC",WTContainerHelper.service.getByPath("/")));
		    State state = State.toState(Constants.STATE_APPROVED);
            LifeCycleServerHelper.setState((LifeCycleManaged)versioned,state);
		    versioned = (Versioned)PersistenceHelper.manager.save(versioned);
		    return versioned;		    		   
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("VersionUtil.class Error, Method=setVersionIteration Message=" + e.getMessage());
		}		
		return null;
	}
	
	public static Versioned createNewVersionObject(Versioned versioned,String version){
		return setVersionIteration(versioned,version,"0");
	}
	
	public static Versioned revise(String oid){
		Versioned versionObj = null;
		try {
			ReferenceFactory rf = new ReferenceFactory();
			versionObj = (Versioned)rf.getReference(oid).getObject();
			if(versionObj == null)
				return versionObj;
			String current_version = versionObj.getVersionIdentifier().getValue();
			int new_version = Integer.parseInt(current_version) + 1;
			return (Versioned)createNewVersionObject(versionObj, new_version+"");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionObj;
	}
	
	//Enhanced by Ring 20080429
	//Revise the version by any version or iteration
	public static Versioned revise(String oid, String version, String iteration){
		Versioned versionObj = null;
		try{
			ReferenceFactory rf = new ReferenceFactory();
			versionObj = (Versioned)rf.getReference(oid).getObject();
			if(versionObj == null)			return versionObj;
			if(iteration == null)	iteration = "0";
			versionObj = (Versioned)setVersionIteration(versionObj, version, iteration);
		}catch(Exception e){
			e.printStackTrace();
		}
		return versionObj;
	}
	
	public static void main(String[] args) throws WTException {
		WTPart part = (WTPart) CSCUtil.getObjectByOid("VR:wt.part.WTPart:314573");
		setVersionIteration(part, "B", "1");
	}

}
