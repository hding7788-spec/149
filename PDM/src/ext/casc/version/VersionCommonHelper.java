package ext.casc.version;

import com.glaway.mpm.util.ReferenceFactory;
import wt.enterprise.RevisionControlled;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;

public class VersionCommonHelper {
    public static String getVersion(RevisionControlled version ){
        return version.getVersionIdentifier().getValue()+"."+version.getIterationIdentifier().getValue();
    }

    public static String getVersionAndView(WTPart part ){
        return part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue()+"("+part.getViewName()+")";
    }

    public static String getVersionId(RevisionControlled version ){
        return "VR:"+version.getClass().getName()+":"+version.getBranchIdentifier();
    }

    public static String getVR(RevisionControlled version ) throws WTException{
        return ReferenceFactory.getVR(version);
    }

    public static long getBranchIdentifier(RevisionControlled version ){
        return version.getBranchIdentifier();
    }

    public static Versioned getPreVersionObj(Versioned version ) throws WTException {
        QueryResult qr = VersionControlHelper.service.allVersionsOf(version);
        Versioned preVersion = version;
        int i = 0;
        while (qr.hasMoreElements()) {
            Versioned tmpVersion = (Versioned) qr.nextElement();
            if(i==1){
                preVersion = tmpVersion;
                break;
            }
            i++;

        }
        return preVersion;
    }

}
