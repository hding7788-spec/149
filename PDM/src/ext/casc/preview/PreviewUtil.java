package ext.casc.preview;

import java.util.ArrayList;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

public class PreviewUtil implements RemoteAccess {
	static String CLASSNAME = PreviewUtil.class.getName();

	public static Preview getPreviewByNumber(String number) {
		// TODO Auto-generated method stub
		try {
			QuerySpec qs = new QuerySpec(Preview.class);
			qs.appendWhere(new SearchCondition(Preview.class, Preview.NUMBER,
					"=", number), new int[1]);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Preview preview = (Preview) qr.nextElement();
				return preview;
			}
		} catch (WTException wte) {
			wte.printStackTrace();
		} finally {
		}
		return null;
	}

	public static PreviewObject searchPreviewObject(String objNumber,
			String version, String objType) {
		// TODO Auto-generated method stub
		try {
			QuerySpec qs = new QuerySpec(PreviewObject.class);
			qs.appendWhere(new SearchCondition(PreviewObject.class,
					PreviewObject.NUMBER, "=", objNumber), new int[1]);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(PreviewObject.class,
					PreviewObject.OBJ_VER, "=", version), new int[1]);
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(PreviewObject.class,
					PreviewObject.OBJ_TYPE, "=", objType), new int[1]);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				PreviewObject previewObject = (PreviewObject) qr.nextElement();
				return previewObject;
			}
		} catch (WTException wte) {
			wte.printStackTrace();
		} finally {
		}
		return null;
	}

	public static ArrayList<WTObject> getAllMembers(Preview preview)
			throws WTException {

		ArrayList<WTObject> list = new ArrayList<WTObject>();
		try {
			QueryResult qr = PersistenceHelper.manager.navigate(preview,
					"thePreviewObj", PreMemberLink.class, false);
			while (qr.hasMoreElements()) {
				PreMemberLink link = (PreMemberLink) qr.nextElement();
				PreviewObject obj = link.getPreviewObj();
				list.add(obj);
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

}