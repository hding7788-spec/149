package ext.casc.securitymgr;

import com.ptc.core.security.utils.SecurityLabelsHelper;
import com.ptc.netmarkets.model.NmOid;

import wt.access.AccessControlServerHelper;
import wt.access.SecurityLabeled;
import wt.access.configuration.SecurityLabelsConfiguration;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.util.WTException;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author zhi xin song
 * @version 2022/9/19 11:18
 * @description
 */
public class SecurityLabelDataHelper {
    public  static Map<String,String> miji = new HashMap<String,String>();
    static SecurityLabelsConfiguration slConfiguration = null;

    static{

        miji.put("无", "GONGKAI");
        miji.put("公开", "GONGKAI");
        miji.put("内部", "NEIBU");
        miji.put("普通", "NEIBU");

        miji.put("秘密", "MIMI");
        miji.put("秘密★", "MIMI");
        miji.put("秘密★10年", "MIMI");

        miji.put("机密", "JIMI");
        miji.put("机密★", "JIMI");
        miji.put("机密★20年", "JIMI");

    }
    public static void setSecret(Object target) throws Exception {
        ReferenceFactory rf = new ReferenceFactory();
        String secretValue = null;

        if (target instanceof Persistable) {
            String pOid = rf.getReferenceString((Persistable) target);
            if (!SecurityLabelsHelper.isSecurityLabelsExposed(NmOid.newNmOid(pOid))) {
                return;
            }
            Persistable p = (Persistable) target;
            if(p instanceof WTDocument){
                WTDocument doc=(WTDocument)p;
                String lx=doc.getDisplayType().getLocalizedMessage(Locale.CHINA);
                String ibaName="SECRET";
                System.out.println("zw>>>>"+doc.getNumber()+">>>>>"+lx);
                if("CAD文档".equals(lx)){
                    ibaName="document_secret";
                }
                String version=doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue();
                secretValue = getIBAStringValue((WTObject) p, ibaName);
                System.out.println(">>>>>文档转换前"+secretValue+">>>版本+"+version);
            }
            else if(p instanceof WTPart){
                secretValue = getIBAStringValue((WTObject) p, "SECRET");
            }
            else if(p instanceof EPMDocument){
                secretValue = getIBAStringValue((WTObject) p, "SECRET");
            }

            if (secretValue == null || "".equals(secretValue)){
                secretValue = "内部";
            }

            //把密级进行标准化
            if(secretValue.contains("公开") || secretValue.contains("无") || secretValue.contains("普通")){
                secretValue = "公开";
            } else if(secretValue.contains("秘密")){
                secretValue = "秘密";
            } else if(secretValue.contains("机密")){
                secretValue = "机密";
            }
            else if (secretValue.contains("内部")){
                secretValue = "内部";
            }else{
                secretValue = "公开";
            }
            System.out.println(">>>>>文档转换后"+secretValue);
            if (p instanceof RevisionControlled) {
                RevisionControlled rc = (RevisionControlled) p;
                System.out.println("zw>>>>>miji.get(secretValue):"+miji.get(secretValue));
                System.out.println("rc------->before--->"+rc.getSecurityLabels());
                setSecurityLabels(rc, "MIJI", miji.get(secretValue));
            }
        }
    }



    public static SecurityLabeled setSecurityLabels(SecurityLabeled obj, String securityLabel, String securityValue ){
        try{
            if (RemoteMethodServer.ServerFlag) {
                if(slConfiguration == null)
                    slConfiguration = SecurityLabelsConfiguration.getSecurityLabelsConfiguration();

                if(slConfiguration!=null)
                    AccessControlServerHelper.manager.setSecurityLabel(obj, securityLabel, securityValue, true);
            }else {
                Class [] rmiArgTypes = { SecurityLabeled.class, String.class, String.class };
                Object[] rmiArgs = { obj, securityLabel, securityValue };
                RemoteMethodServer.getDefault().invoke("setSecurityLabels", SecurityLabelDataHelper.class.getName(), null, rmiArgTypes, rmiArgs);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return obj;

    }

    /**
     *
     * @param
     * @return String
     * @exception WTException
     **/

    public static String getIBAStringValue(WTObject obj, String ibaName) throws WTException {

        String value = null;
        String ibaClass = "wt.iba.definition.StringDefinition";

        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaholder = (IBAHolder) obj;
                DefaultAttributeContainer defaultattributecontainer = getContainer(ibaholder);
                if (defaultattributecontainer != null) {
                    AbstractValueView avv = getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
                    if (avv != null) {
                        value = ((StringValueDefaultView) avv).getLocalizedDisplayString();
                    }
                }
            }
        } catch (RemoteException rexp) {
            rexp.printStackTrace();
        }

        return value;

    }


    public static DefaultAttributeContainer getContainer(IBAHolder ibaholder) throws WTException, RemoteException {

        ibaholder = IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaholder);
        DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder.getAttributeContainer();

        return defaultattributecontainer;
    }

    public static AbstractValueView getIBAValueView(DefaultAttributeContainer dac, String ibaName, String ibaClass) throws WTException {

        AbstractValueView aabstractvalueview[] = null;
        AbstractValueView avv = null;

        aabstractvalueview = dac.getAttributeValues();
        for (int j = 0; j < aabstractvalueview.length; j++) {
            String thisIBAName = aabstractvalueview[j].getDefinition().getName();
//			String thisIBAValue = IBAValueUtility.getLocalizedIBAValueDisplayString(aabstractvalueview[j], LOCALE);
            String thisIBAClass = (aabstractvalueview[j].getDefinition()).getAttributeDefinitionClassName();
            if (thisIBAName.equals(ibaName) && thisIBAClass.equals(ibaClass)) {
                avv = aabstractvalueview[j];
                break;
            }
        }
        return avv;
    }



   public static void getSecurityLabels(String oid) throws WTException {
        Object p = null;
        if (oid!=null&&"".equals(oid.trim())){
           p= getReferenceByOid(oid).getObject();
        }
       if (p!=null&&p instanceof RevisionControlled) {
           RevisionControlled rc = (RevisionControlled) p;
           System.out.println("rc------->before--->" + rc.getSecurityLabels());
       }
   }

    /**
     *
     * 通过oid获取对应的WTReference
     *
     * @param oid
     * @return
     * @throws WTException
     */
    public static WTReference getReferenceByOid(String oid) throws WTException{
        ReferenceFactory referencefactory = new ReferenceFactory();
        WTReference wtreference = referencefactory.getReference( oid );
        return wtreference;
    }
}
