package ext.casc.changeDept;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.definition._AttributeHierarchyChild;
import wt.iba.value._StringValue;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.pbom.helper.PBOMHelper;
import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.glaway.mpm.util.WTDocumentUtil;

import ext.casc.constants.PDMConfig;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;

public class ChangePbomDeptUtil implements RemoteAccess {
    public static void main(String[] args) throws WTException {
    	if(args.length<=0){
			System.out.println("产品库ID不能空！");
			return ;
		}
    	RemoteMethodServer rms = RemoteMethodServer.getDefault();
    	rms.setUserName("wcadmin");
		rms.setPassword(PDMConfig.WCADMIN_PASSWORD);

        excute(Long.parseLong(args[0])) ;
    }
    public static void excute(Long containerId) throws WTException {

        if (!RemoteMethodServer.ServerFlag) {
            Class[] argTypes = {Long.class};
            Object[] args = {containerId};
            try {
            	SessionHelper.manager.setPrincipal("administrator");

            	RemoteMethodServer server = RemoteMethodServer.getDefault();
				server.setUserName("wcadmin");
				//				server.setPassword("wcadmin");
				server.setPassword(PDMConfig.WCADMIN_PASSWORD);
				server.invoke("excute", ChangePbomDeptUtil.class.getName(), null, argTypes, args);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }else{
            boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
            try {

                WTProperties wtProperties = WTProperties.getLocalProperties();
                String codebasePath = wtProperties.getProperty("wt.codebase.location");
                String filePath = codebasePath + File.separator + "ext"
                        + File.separator + "casc"
                        + File.separator + "conf" + File.separator + "config_149.xml";
                // FileInputStream inputStream = new FileInputStream(new File(filePath));
                SAXReader reader = new SAXReader();
                Document document = reader.read(new File(filePath));
                org.dom4j.Element rootElement = document.getRootElement();
                org.dom4j.Element changeDeptConfig  = rootElement.element("ChangePbomDeptConfig");
                List<org.dom4j.Element> configs = changeDeptConfig.elements("convertMap");
                for(org.dom4j.Element e:configs){
                    String srcDept = e.attributeValue("fromValue");
                    String destDept = e.attributeValue("toValue");
                    System.out.println("转换配置:"+srcDept+"---->"+destDept);
                    if(Tools.isNull(srcDept)||Tools.isNull(destDept)){
                        return ;
                    }

                    Set<Long> flags = new HashSet<Long>();

                    QuerySpec qs = new QuerySpec();
                    qs.setAdvancedQueryEnabled(true);
                    int ibaHolderIndex = qs.appendClassList(WTPart.class, true);
                    View view = ViewHelper.service.getView("Manufacturing");
                    qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id",
                            "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[ibaHolderIndex]);
                    if(containerId!=null){
                        qs.appendAnd();
                        qs.appendWhere(new SearchCondition(WTPart.class, WTPart.CONTAINER_ID, SearchCondition.EQUAL, containerId), new int[] {ibaHolderIndex});
                    }else{
                        return ;
                    }
                    //3   3-   -3   -3-
                    if (srcDept != null && !"".equals(srcDept)) {
                        qs.appendAnd();
                        int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
                        int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
                        // Latest Iteration
                        SearchCondition scLatestIteration = new SearchCondition(WTPart.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
                        // String Value With IBA Holder
                        SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id", WTPart.class, WTAttributeNameIfc.ID_NAME);
                        // String Value With Definition
                        SearchCondition scJoinStringValueStringDefinition = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.key.id", StringDefinition.class,
                                WTAttributeNameIfc.ID_NAME);
                        qs.appendWhere(scLatestIteration, ibaHolderIndex);
                        qs.appendAnd();
                        qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, ibaHolderIndex);
                        qs.appendAnd();
                        qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);
                        SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME, SearchCondition.EQUAL,"ROUTING" );
                        SearchCondition scStringValueValue1 = null;
                        scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.EQUAL, srcDept.toUpperCase());
                        SearchCondition  scStringValueValue2 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE, srcDept.toUpperCase()+"-%");
                        SearchCondition  scStringValueValue3 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE, "%-"+srcDept.toUpperCase()+"-%");
                        SearchCondition  scStringValueValue4 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE, "%-"+srcDept.toUpperCase());


                        qs.appendAnd();
                        qs.appendOpenParen();
                        qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
                        qs.appendAnd();
                        qs.appendOpenParen();
                        qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
                        qs.appendOr();
                        qs.appendWhere(scStringValueValue2, ibaStringValueIndex);
                        qs.appendOr();
                        qs.appendWhere(scStringValueValue3, ibaStringValueIndex);
                        qs.appendOr();
                        qs.appendWhere(scStringValueValue4, ibaStringValueIndex);
                        qs.appendCloseParen();

                        qs.appendOr();
                        qs.appendOpenParen();
                        SearchCondition scStringDefinitionName2 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME, SearchCondition.EQUAL,"ZZCJ" );
                        SearchCondition  scStringValueValue5 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.EQUAL, srcDept.toUpperCase());
                        qs.appendWhere(scStringDefinitionName2, ibaStringDefinitionIndex);
                        qs.appendAnd();
                        qs.appendWhere(scStringValueValue5, ibaStringValueIndex);
                        qs.appendCloseParen();

                        qs.appendOr();
                        qs.appendOpenParen();
                        SearchCondition scStringDefinitionName3 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME, SearchCondition.EQUAL,"FZCJ" );
                        SearchCondition  scStringValueValue6 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.EQUAL, srcDept.toUpperCase());
                        qs.appendWhere(scStringDefinitionName3, ibaStringDefinitionIndex);
                        qs.appendAnd();
                        qs.appendWhere(scStringValueValue6, ibaStringValueIndex);
                        qs.appendCloseParen();

                        qs.appendCloseParen();

                        //qs.appendAnd();
                        //qs.appendOpenParen();
                        //appendIBAValue(qs,ibaHolderIndex,"ROUTING",srcDept,QUERYTYPE_ALLLIKE);
                        // qs.appendCloseParen();
                    }
                    qs = new LatestConfigSpec().appendSearchCriteria(qs);
                    QueryResult qr = PersistenceHelper.manager.find(qs);
                    while(qr.hasMoreElements()){
                        Object[] obj = (Object[]) qr.nextElement();
                        WTPart  part = (WTPart)obj[0];
                        if(!flags.contains(part.getPersistInfo().getObjectIdentifier().getId())){
                        	 System.out.println("转换零件:"+part.getNumber()+" "+part.getVersionIdentifier().getValue());
                             processPart(part,srcDept,destDept);
                             flags.add(part.getPersistInfo().getObjectIdentifier().getId());

                        }

                    }
                }


            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            } finally {
                SessionServerHelper.manager.setAccessEnforced(enforce);
            }
        }


    }
    public static void processPart (WTPart part ,String srcDept,String destDept) throws WTException, RemoteException, WTPropertyVetoException {
        String ZZCJ = IBAHelper.getIBAStringValue(part,"ZZCJ");
        String FZCJ = IBAHelper.getIBAStringValue(part,"FZCJ");
        String ROUTING = IBAHelper.getIBAStringValue(part,"ROUTING");

        if(replacePbomXML(part,srcDept,destDept)){
            //ZZCJ  FZCJ  ROUTING
            com.glaway.mpm.util.IBAHelper attrHelper = new com.glaway.mpm.util.IBAHelper(part);
            Map ibaMap = new HashMap();
            if(srcDept.equals(ZZCJ)){
                ibaMap.put("ZZCJ",destDept);
            }

            if(FZCJ!=null ){
                if(FZCJ.equals(srcDept)){
                    ibaMap.put("FZCJ",destDept);
                }else if(FZCJ.startsWith(srcDept+"-")){
                    ibaMap.put("FZCJ",FZCJ.replaceAll(srcDept+"-", destDept+"-"));
                }else if(FZCJ.endsWith("-"+srcDept)){
                    ibaMap.put("FZCJ",FZCJ.replaceAll("-"+srcDept, "-"+destDept));
                }else if(FZCJ.contains("-"+srcDept+"-")){
                    ibaMap.put("FZCJ",FZCJ.replaceAll("-"+srcDept+"-", "-"+destDept+"-"));
                }
            }
            if(ROUTING!=null ){
                if(ROUTING.equals(srcDept)){
                    ibaMap.put("ROUTING",destDept);
                }else if(ROUTING.startsWith(srcDept+"-")){
                    ibaMap.put("ROUTING",ROUTING.replaceAll(srcDept+"-", destDept+"-"));
                }else if(ROUTING.endsWith("-"+srcDept)){
                    ibaMap.put("ROUTING",ROUTING.replaceAll("-"+srcDept, "-"+destDept));
                }else if(ROUTING.contains("-"+srcDept+"-")){
                    ibaMap.put("ROUTING",ROUTING.replaceAll("-"+srcDept+"-", "-"+destDept+"-"));
                }
            }
            attrHelper.setIBAValue(part, ibaMap);
        }
    }
    public static String QUERYTYPE_EQUAL = "EQUAL";
    public static String QUERYTYPE_LEFTLIKE = "LEFTLIKE";
    public static String QUERYTYPE_RIGHTLIKE = "RIGHTLIKE";
    public static String QUERYTYPE_ALLLIKE = "ALLLIKE";

    private static void appendIBAValue(QuerySpec qs,int ibaHolderIndex,String key ,String srcDept,String queryType) throws QueryException {
        int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
        int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
        // Latest Iteration
        SearchCondition scLatestIteration = new SearchCondition(WTPart.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
        // String Value With IBA Holder
        SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id", WTPart.class, WTAttributeNameIfc.ID_NAME);
        // String Value With Definition
        SearchCondition scJoinStringValueStringDefinition = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.key.id", StringDefinition.class,
                WTAttributeNameIfc.ID_NAME);
        qs.appendWhere(scLatestIteration, ibaHolderIndex);
        qs.appendAnd();
        qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, ibaHolderIndex);
        qs.appendAnd();
        qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);
        SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME, SearchCondition.EQUAL,key );
        SearchCondition scStringValueValue1 = null;
        if(QUERYTYPE_EQUAL.equals(queryType)) {
             scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.EQUAL, srcDept.toUpperCase());
        }else  if(QUERYTYPE_LEFTLIKE.equals(queryType)){
            scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE,"%"+ srcDept.toUpperCase());
        }else if(QUERYTYPE_RIGHTLIKE.equals(queryType)){
            scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE, srcDept.toUpperCase()+"%");
        }else if(QUERYTYPE_ALLLIKE.equals(queryType)){
            scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE, "%"+srcDept.toUpperCase()+"%");
        }
        qs.appendAnd();
        qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
        qs.appendAnd();
        qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
    }


    private static boolean  replacePbomXML(WTPart part,String srcDept,String destDept) {
        boolean flag = true;

        WTDocument document =null;
		try {
			document = PBOMHelper.getBOMXmlDoc(part, Constants.pbomDocEndwith);
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (PropertyVetoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        if(document!=null){
        	Transaction trans = new Transaction();
        	 FileOutputStream fileOutputStream = null;
        	 FileInputStream fileInputStream =  null;
        	 InputStream inputStream = null;
        	 try{
    			trans.start();
                String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
                        + java.util.UUID.randomUUID().toString() + File.separator;
                String pbomXmlName =downloadDocumentPrimaryToTemp(document, tempFilePath);
                String pbomXmlFilePath = tempFilePath+document.getNumber();
                File xmlFile = new File(pbomXmlFilePath);
                inputStream = new FileInputStream(xmlFile);

                SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
                Element rootElement = xmlUtil.getRootElement();
                Element element = rootElement.getChild("parts").getChild("QMPartInfo");
                if(element!=null){
                    updateElement(element,srcDept,destDept);
                }
                processChildNode(element,srcDept,destDept);

                fileOutputStream = new FileOutputStream(new File(pbomXmlFilePath), false);
                Format format = Format.getPrettyFormat();
                format.setEncoding("GBK");
                XMLOutputter xmlOutput = new XMLOutputter(format);
                xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);
               // WTDocumentUtil.setPrimaryForDocument(document, pbomXmlName, new FileInputStream(new File(pbomXmlFilePath)));
                ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
                fileInputStream =  new FileInputStream(pbomXmlFilePath);
				data = ContentServerHelper.service.updateContent((ContentHolder) document, data, fileInputStream);
				 trans.commit();
			     trans = null;
        	 }catch (Exception e){
                 e.printStackTrace();
                 flag = false;
             }finally{
            	 if (trans != null) {
     				trans.rollback();
     			}
         		try {
     				ApacheZipUtil.closeOutputStream(fileOutputStream);
     	    		ApacheZipUtil.closeInputStream(inputStream);
     	    		ApacheZipUtil.closeInputStream(fileInputStream);

     			} catch (IOException e) {
     				// TODO Auto-generated catch block
     				e.printStackTrace();
     			}
             }
        }

        return flag;

    }

    public static String downloadDocumentPrimaryToTemp(WTDocument doc, String tempPath) throws WTException,
            PropertyVetoException {
        ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
        if (data == null) {
            return null;
        }
        InputStream is = ContentServerHelper.service.findContentStream(data);
        String appFileName = data.getFileName();
        File file = new File(tempPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(tempPath+ doc.getNumber()));
            int i = 0;
            byte abyte[] = new byte[8192];
            while ((i = is.read(abyte, 0, abyte.length)) >= 0) {
                fos.write(abyte, 0, i);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return appFileName;
    }
    private static void processChildNode(Element partInfo,String srcDept,String destDept){
        Element childsElement = partInfo.getChild("childs");
        if(childsElement!=null) {
            List<Element> childPartInfoList = childsElement.getChildren("QMPartInfo");
            for (Element childPartInfo : childPartInfoList) {
                updateElement(childPartInfo,srcDept,destDept);
                processChildNode(childPartInfo,srcDept,destDept);
            }
        }

    }
    private static void  updateElement(Element element,String srcDept,String destDept){
        String ZZCJ = element.getAttributeValue("ZZCJ");
        if(srcDept.equals(ZZCJ)){
            element.setAttribute("ZZCJ",destDept);
        }
        String FZCJ = element.getAttributeValue("FZCJ");
        if(FZCJ!=null ){
        	if(FZCJ.equals(srcDept)){
        		element.setAttribute("FZCJ",destDept);
        	}else if(FZCJ.startsWith(srcDept+"-")){
        		element.setAttribute("FZCJ",FZCJ.replaceAll(srcDept+"-", destDept+"-"));
        	}else if(FZCJ.endsWith("-"+srcDept)){
        		element.setAttribute("FZCJ",FZCJ.replaceAll("-"+srcDept, "-"+destDept));
        	}else if(FZCJ.contains("-"+srcDept+"-")){
        		element.setAttribute("FZCJ",FZCJ.replaceAll("-"+srcDept+"-", "-"+destDept+"-"));
        	}
        }
    }
}
