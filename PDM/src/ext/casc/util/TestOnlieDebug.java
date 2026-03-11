package ext.casc.util;

import com.glaway.mpm.util.ReferenceFactory;
import wt.inf.container.WTContainerHelper;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.session.SessionHelper;
import wt.util.WTException;

public class TestOnlieDebug {
    public static void main(String[] args) {
        try {
            long start1 = System.currentTimeMillis();
            WTPartUsageLink uageLink = (WTPartUsageLink) ReferenceFactory.getObjectbyOid("OR:wt.part.WTPartUsageLink:1337662");
            System.out.println(	 System.currentTimeMillis()-start1);

            start1 = System.currentTimeMillis();
            System.out.println(	uageLink.getRoleAObject());
            System.out.println(	 System.currentTimeMillis()-start1);

            long start2 = System.currentTimeMillis();
            System.out.println(	uageLink.getRoleBObject());
            System.out.println(	 System.currentTimeMillis()-start2);


            WTPart part = (WTPart) uageLink.getRoleAObject();
            start2 = System.currentTimeMillis();
            System.out.println(WTContainerHelper.service.isAdministrator(part.getContainerReference(), SessionHelper.getPrincipal()));
            System.out.println(	 System.currentTimeMillis()-start2);

            start2 = System.currentTimeMillis();
            System.out.println(part.getFolderPath());
            System.out.println(	 System.currentTimeMillis()-start2);

            start2 = System.currentTimeMillis();
            //System.out.println(FolderHelper.service.getFolder("/Default/"+part.getFolderPath(),part.getContainerReference()));
            System.out.println(	 System.currentTimeMillis()-start2);

            start2 = System.currentTimeMillis();
            System.out.println(	 System.currentTimeMillis()-start2);
        } catch (WTException e) {
            throw new RuntimeException(e);
        }
    }
}
