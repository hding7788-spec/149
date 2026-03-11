package ext.casc.ixb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.iba.value.IBAHolder;
import wt.inf.container.ExchangeContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.sharing.DataSharingHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.projmgmt.admin.Project2;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.ptc.extend.util.Debug;
import com.ptc.extend.util.ObjectProperty;
import com.ptc.core.meta.common.TypeIdentifier;

public class CmExpImpHelper {
	public static String ID_SEP="@";
//	public static String SHARE_CONTAINER_NAME="TestProject";
	
	private CmExpImpHelper() {}

//	public static void logger(Object s) {
//		System.out.println((new StringBuilder()).append("ExpImporter: ").append(String.valueOf(s)).toString());
//		CmExpImpLogger.getInstance().log(s);
//	}

	private static String escapeNumberString(String str) {
		return str.replace(' ', '_').replace('/', '_').replace('\\', '_');
	}
	public static String getUniqueSavePathInJar(Object obj) {
		StringBuilder sb=new StringBuilder();
		String number=ObjectProperty.getNumber(obj);
		sb.append(getObjectClassname(obj)).append(ID_SEP);
		Pattern pattern=Pattern.compile("^[0-9a-zA-Z_-]{1,20}$");
		Matcher matcher = pattern.matcher(number);
		if (matcher.matches()) {
			sb.append(escapeNumberString(ObjectProperty.getNumber(obj)));
		} else {
			sb.append("LOCALID").append(String.valueOf(((Persistable)obj).getPersistInfo().getObjectIdentifier().getId()));
		}
		return sb.toString();
	}
	public static String getObjectNormalSavePathInJar(Object obj) {
		StringBuilder sb=new StringBuilder();
//		String number=ObjectProperty.getNumber(obj);
		sb.append(getObjectClassname(obj)).append(ID_SEP);
		sb.append(escapeNumberString(ObjectProperty.getNumber(obj)));
		return sb.toString();
	}
	public static String getObjectLocalIdSavePathInJar(String remoteId) {
//		String typename=remoteId.substring(0, remoteId.indexOf(":"));
//		String type=typename.substring(typename.lastIndexOf(".")+1);
		String id=remoteId.substring(remoteId.indexOf(":")+1);
		return (new StringBuilder()).append("LOCALID").append(id).toString();
	}
	public static Iterated getLatestIterationObject(Master master) throws WTException {
        QueryResult queryResult = VersionControlHelper.service.allVersionsOf(master);
        return VersionControlHelper.getLatestIteration((Iterated)queryResult.nextElement(), true);
    }
	
	public static String getFileExtension(String s) {
		if (null == s || "".equals(s))
			return null;
		int i = s.lastIndexOf(".");
		if (i == -1)
			return null;
		else
			return s.substring(i + 1);
    }
	
	public static String getObjectClassname(Object obj) {
		String cn=obj.getClass().getName();
		int i = cn.lastIndexOf(".");
		if (i == -1)
			return cn;
		else
			return cn.substring(i + 1);
	}
	
	public static InputStream stringToInputStream(String s, String encoding) throws UnsupportedEncodingException, IOException {
		ByteArrayInputStream bytearrayinputstream = null;
        class CmByteArrayOutputStream extends ByteArrayOutputStream {
        	byte[] getBuffer() {
        		return buf;
            }

            int getCount() {
            	return count;
            }

            CmByteArrayOutputStream(int i) {
            	super(i);
            }
        }
        CmByteArrayOutputStream Cmbytearrayoutputstream = new CmByteArrayOutputStream(1024);
        OutputStreamWriter outputstreamwriter = new OutputStreamWriter(Cmbytearrayoutputstream, encoding);
        outputstreamwriter.write(s, 0, s.length());
        outputstreamwriter.close();
        byte abyte0[] = Cmbytearrayoutputstream.getBuffer();
        int i = Cmbytearrayoutputstream.getCount();
        bytearrayinputstream = new ByteArrayInputStream(abyte0, 0, i);
        return bytearrayinputstream;
	}
	
	public static String getLocalExchangeContainerURL() {
		try {
			ExchangeContainer ec=(ExchangeContainer)WTContainerHelper.getExchangeRef().getReferencedContainer();
			return ec.getInternetDomain();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "unknow";
	}
	
//	public static WTContainer getReceiveDataContainer() {
//		try {
//			String prop=WindchillProperty.getWindchillProperty("ext.ideal.ixb.import.Container", "");
//			if (prop.equals("")) {
//				Debug.P("Not data import container property defined.");
//				return null;
//			}
//			int sep=prop.indexOf("/");
//			if (sep<0) {
//				Debug.P("Import Container Property defined in wrong format.");
//				return null;
//			}
//			String classname=prop.substring(0, sep);
//			String name=prop.substring(sep+1);
//			Class klass=Class.forName(classname);
//			if (!WTContainer.class.isAssignableFrom(klass)) {
//				Debug.P("Not WTContainer Class defined.");
//				return null;
//			}
//			QuerySpec qs=new QuerySpec(klass);
//			qs.appendWhere(new SearchCondition(klass, "containerInfo.name", "=", name), new int[] {0});
//			QueryResult qr=PersistenceHelper.manager.find(qs);
//			if (qr.hasMoreElements())
//				return (WTContainer)qr.nextElement();
//			else
//				return null;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	public static Persistable getSharedPersistable(Persistable p, WTContainerRef cf) throws WTException {
		WTHashSet cc=new WTHashSet();
		cc.add(p);
		WTCollection wtc=DataSharingHelper.service.getSharedObjects(cc, cf);
		if (wtc.isEmpty())
			return null;
		else
			return (Persistable)((ObjectReference)(wtc.toArray())[0]).getObject();
	}
	
	public static boolean isShared(Persistable p, WTContainerRef cf) throws WTException {
		if (p==null || cf==null)
			return false;
		return DataSharingHelper.service.isSharedTo(p, cf);
	}
	
	public static WTContainerRef getShareContainer(String shareContainerName) throws WTException {
		QuerySpec qs=new QuerySpec(Project2.class);
		qs.appendWhere(new SearchCondition(Project2.class, Project2.PSEUDO_TYPE, SearchCondition.EQUAL, Project2.TYPE_PROJECT), new int[]{0});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Project2.class, Project2.NAME, SearchCondition.EQUAL, shareContainerName), new int[]{0});
		QueryResult qr=PersistenceHelper.manager.find(qs);
		if (qr.hasMoreElements())
			return WTContainerRef.newWTContainerRef((Project2)qr.nextElement());
		else
			return null;
	}
	
	private static boolean qualifiedWTPartType(TypeIdentifier ti) {
		String type=ti.toExternalForm();
		if (type.endsWith("UpperLevel")||type.endsWith("CI")||type.endsWith("LO"))
			return true;
		else
			return false;
	}
	
	public static void getProductChilds(WTPart part, ConfigSpec cs, WTHashSet set) throws WTException {
		QueryResult qr = WTPartHelper.service.getUsesWTParts(part, cs);
		while (qr.hasMoreElements()) {
			Persistable subnode[] = (Persistable[]) qr.nextElement();
			if (subnode[1] instanceof WTPart) {
				WTPart child=(WTPart)subnode[1];
				TypeIdentifier partti = TypedUtility.getTypeIdentifier(child);
				if (!qualifiedWTPartType(partti))
					continue;
				if (WorkInProgressHelper.isCheckedOut((Workable)child))
					continue;
				if (!set.contains((Persistable)child))
					set.add((Persistable)child);
				getProductChilds(child, cs, set);
			}
		}
	}
	
	private static void setOtherDocIterationLifecycle(Iterated obj) {
		try {
			Debug.P("Post process document after import new iteration...");
			String lstate="APPROVED";//WindchillProperty.getWindchillProperty("ext.ideal.ixb.export.lifecycle.state", CmIXBHelper.DEFAULT_DELIVERABLE_LIFECYCLE_STATE);
			WTDocument doc=(WTDocument)obj;
			QuerySpec qs=new QuerySpec(WTDocument.class);
			qs.appendWhere(new SearchCondition(WTDocument.class, "master>number", "=", doc.getNumber()), new int[] {0});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, LifeCycleManaged.LIFE_CYCLE_STATE, SearchCondition.EQUAL, State.toState(lstate)), new int[]{0});
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTDocument.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.NOT_EQUAL, PersistenceHelper.getObjectIdentifier(doc).getId()), new int[] {0});
			qs.setAdvancedQueryEnabled(true);
			//Debug.P("SQL:", qs);
			QueryResult qr=PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				LifeCycleManaged lcm=(LifeCycleManaged)qr.nextElement();
				Debug.P("Set:", ObjectProperty.getObjectDisplay(lcm));
				LifeCycleHelper.service.setLifeCycleState(lcm, State.toState("CM_YSX"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static Iterated setObjectOrignalInfo(Iterated obj, HashMap hmap) {
		try {
			Debug.P("Set Orignal Information for imported iteration...", ObjectProperty.getObjectDisplay(obj));
			if (hmap.containsKey("creator"))
				obj=(Iterated)ObjectProperty.setObjectIBAValueNoCheckout((IBAHolder)obj, "OrignalCreator", (String)hmap.get("creator"));
			if (hmap.containsKey("modifier"))
				obj=(Iterated)ObjectProperty.setObjectIBAValueNoCheckout((IBAHolder)obj, "OrignalModifier", (String)hmap.get("modifier"));
//			if (hmap.containsKey("remoteurl"))
//				obj=(Iterated)ObjectProperty.setObjectIBAValueNoCheckout((IBAHolder)obj, "OrignalSource", OrganizationHelper.getOrganizationNameFromURL((String)hmap.get("remoteurl")));
			return (Iterated)PersistenceHelper.manager.refresh(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	public static void doOperationAfterStore(Iterated obj, HashMap hmap, boolean flag) {
		setObjectOrignalInfo(obj, hmap);
		if (flag) {
			if (obj instanceof WTDocument)
				setOtherDocIterationLifecycle(obj);
		}
	}
}
