package ext.test;

import com.glaway.mpm.util.ReferenceFactory;
import com.ptc.wvs.common.ui.Representer;
import com.ptc.wvs.common.ui.VisualizationHelper;
import ext.casc.version.VersionCommonHelper;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.WTException;
import wt.vc.VersionControlHelper;

import java.net.URL;


public class ContentHelperTest {
	public static void main(String[] args) {
		test();
    }
	public static void getDownloadURL()  {
		try {
			ContentHolder doc = (ContentHolder) ReferenceFactory.getObjectbyOid("VR:wt.doc.WTDocument:5054866");
			ContentHolder doc2 = (ContentHolder) ReferenceFactory.getObjectbyOid("VR:wt.doc.WTDocument:5054866");
			System.out.println(doc==doc2);
			System.out.println(doc.equals(doc2));
			System.out.println(System.identityHashCode(doc)+"  "+System.identityHashCode(doc2));


			/*QueryResult qr = wt.content.ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
			ApplicationData appData = null;
			if (qr.hasMoreElements()) {
				appData = (ApplicationData) qr.nextElement();
			}
			URL localUrl = ContentHelper.getDownloadURL(doc,appData,false);//永久链接*/
			//URL localUrl = wt.content.ContentHelper.service.getDownloadURL(doc,appData);//一次链接
			System.out.println("在线编译");
		} catch (WTException e) {
			throw new RuntimeException(e);
		}
	}
	public static void saveAsZIPFile()  {
		try {
			EPMDocument obj = (EPMDocument) ReferenceFactory.getObjectbyOid("VR:wt.epm.EPMDocument:981802");
			VisualizationHelper helper = new VisualizationHelper();
			QueryResult qr = helper.getRepresentations((Representable) obj);
			while (qr.hasMoreElements()) {
				Representation rep = (Representation) qr.nextElement();
				System.out.println(rep);
				new Representer().saveAsZIPFile(getRefFromObject(rep), false, true, "E:\\E盘Tmp\\"+obj.getNumber()+".zip");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static void test() {
		try {
			WTPart part = (WTPart) ReferenceFactory.getObjectbyOid("OR:wt.part.WTPart:3692269");
			QueryResult qrVersion = VersionControlHelper.service.allIterationsFrom( part);
			while (qrVersion.hasMoreElements()) {
				WTPart version = (WTPart) qrVersion.nextElement();
				System.out.println(VersionCommonHelper.getVersionAndView(version));

			}
			System.out.println("--------------------------");
			qrVersion = VersionControlHelper.service.allVersionsFrom( part);
			while (qrVersion.hasMoreElements()) {
				WTPart version = (WTPart) qrVersion.nextElement();
				System.out.println(VersionCommonHelper.getVersionAndView(version));

			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	public static String getRefFromObject(Persistable persistable) {
		try {
			wt.fc.ReferenceFactory referencefactory = new wt.fc.ReferenceFactory();
			return referencefactory.getReferenceString(ObjectReference.newObjectReference(persistable.getPersistInfo()
					.getObjectIdentifier()));
		} catch (Exception exception) {
		}
		return null;
	}
}
