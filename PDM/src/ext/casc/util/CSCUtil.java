package ext.casc.util;

import java.rmi.RemoteException;
import java.util.ArrayList;

import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;

import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.WTReference;
import wt.util.WTException;

public class CSCUtil {

	public static WTReference getReferenceByOid(String oid) throws WTException{
		   ReferenceFactory referencefactory = new ReferenceFactory();
		   WTReference wtreference = referencefactory.getReference( oid );
		   return wtreference;
	}

	public static Persistable getObjectByOid(String oid) throws WTException{
		   WTReference wtreference = getReferenceByOid(oid);
		   return wtreference.getObject();
	}

	public static ArrayList<String> sortByInitial(ArrayList<String> arrayStr){
		ArrayList resultArray = new ArrayList();

		String[] strArray = new String[arrayStr.size()];

		arrayStr.toArray(strArray);
		String strTemp;
		int n = strArray.length - 1;
		for (int i = 0; i < strArray.length; i++) {
			for (int j = 0; j < n; j++) {
				if(strArray[j].compareTo(strArray[j+1]) > 0){
					strTemp = strArray[j];
					strArray[j] = strArray[j+1];
					strArray[j+1] = strTemp;
				}
			}
			n--;
		}
		for(int i = 0; i < strArray.length; i++) {
			resultArray.add(strArray[i]);
		}

		return resultArray;
	}

	public static String convertObidToOid(String obid) {
		String oid = "";
		oid += obid.substring(0, obid.indexOf(":") + 1);
		obid = obid.substring(obid.indexOf(":") + 1);
		oid += obid.substring(0, obid.indexOf(":") + 1);
		obid = obid.substring(obid.indexOf(":") + 1);
		oid += obid.substring(0, obid.indexOf(":"));
		return oid;
	}
	public static String getObjectType(String oid) throws RemoteException, WTException{
		Persistable object = getObjectByOid(oid);
		String type = TypeIdentifierUtilityHelper.service .getTypeIdentifier(object).toString();
		return type;
	}

	public static String trim(String s) {
		String v = s.replaceAll("	", "");
		v = v.replaceAll("　", "");
		return v;
	}

	public static void main(String[] args) {
		ArrayList arrayStr = new ArrayList<String>();
		arrayStr.add("002-ENGLAND");
		arrayStr.add("001-USA");
		arrayStr.add("001-US");
		arrayStr.add("1002-KOREA");
		arrayStr.add("03-CHINA");

		arrayStr = sortByInitial(arrayStr);
		for (int i = 0; i < arrayStr.size(); i++) {
			System.out.println(arrayStr.get(i));
		}

	}
}
