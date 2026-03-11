package ext.casc.util;

import wt.fc.WTObject;
import wt.iba.value.IBAHolder;
import wt.method.RemoteAccess;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class GwIBAUtil implements Serializable, RemoteAccess {
    public static IBAHolder copyIBAValues(IBAHolder ibaHolder1,IBAHolder ibaHolder2 ) throws WTException, RemoteException, WTPropertyVetoException, ClassNotFoundException {
        Hashtable allIBAValues = IBAHelper.getAllIBAValues((WTObject) ibaHolder1);

        IBAUtility ibaUtility = new IBAUtility(ibaHolder2);
        Set< Map.Entry<String,String>> entrySet = allIBAValues.entrySet();
        for(Map.Entry<String,String> entry:entrySet){
            ibaUtility.setIBAValue(entry.getKey(),entry.getValue());
        }
        ibaHolder2 =  ibaUtility.updateAttributeContainer(ibaHolder2);
        ibaUtility.updateIBAHolder(ibaHolder2);
        return ibaHolder2;
    }

    public static IBAHolder copyIBAValues(IBAHolder ibaHolder1,IBAHolder ibaHolder2 ,Map<String,Object> copyIBAs) throws WTException, RemoteException, WTPropertyVetoException, ClassNotFoundException {
        Hashtable allIBAValues = IBAHelper.getAllIBAValues((WTObject) ibaHolder1);

        IBAUtility ibaUtility = new IBAUtility(ibaHolder2);
        Set< Map.Entry<String,String>> entrySet = allIBAValues.entrySet();
        for(Map.Entry<String,String> entry:entrySet){
            if(copyIBAs.containsKey(entry.getKey())){
                ibaUtility.setIBAValue(entry.getKey(),(String)copyIBAs.get(entry.getKey()));
            }else{
                ibaUtility.setIBAValue(entry.getKey(),entry.getValue());
            }
        }
        ibaHolder2 =  ibaUtility.updateAttributeContainer(ibaHolder2);
        ibaUtility.updateIBAHolder(ibaHolder2);
        return ibaHolder2;
    }
}
