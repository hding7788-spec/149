package ext.casc.changeDept;

import com.glaway.mpm.util.ApacheZipUtil;
import com.glaway.mpm.util.PropertiesUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.ptc.core.meta.common.TypeIdentifier;
import ext.casc.constants.PDMConfig;
import ext.casc.util.IBAUtility;
import ext.casc.util.SoftTypeUtil;
import ext.casc.util.Tools;
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
import wt.pds.StatementSpec;
import wt.pom.Transaction;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.type.TypedUtility;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.config.LatestConfigSpec;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChangeProcessDeptUtil implements RemoteAccess {
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
				server.invoke("excute", ChangeProcessDeptUtil.class.getName(), null, argTypes, args);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            return;
        }else{
            boolean enforce = wt.session.SessionServerHelper.manager.setAccessEnforced(false);
            try {

                WTProperties wtProperties = WTProperties.getLocalProperties();
                String codebasePath = wtProperties.getProperty("wt.codebase.location");
                String filePath = codebasePath + File.separator + "ext"
                        + File.separator + "casc"
                        + File.separator + "conf" + File.separator + "config_149.xml";
                // FileInputStream inputStream = new FileInputStream(new File(filePath));
                SAXReader reader = new SAXReader();
                Document dmdocument = reader.read(new File(filePath));
                org.dom4j.Element rootElement = dmdocument.getRootElement();
                org.dom4j.Element changeDeptConfig  = rootElement.element("ChangeProcessDeptConfig");
                List<org.dom4j.Element> configs = changeDeptConfig.elements("convertMap");
                for(org.dom4j.Element e:configs) {
                    String srcDept = e.attributeValue("fromValue");
                    String destDept = e.attributeValue("toValue");
                    System.out.println("转换配置:"+srcDept+"---->"+destDept);
                    if(Tools.isNull(srcDept)||Tools.isNull(destDept)){
                        return ;
                    }

                    Set<Long> flags = new HashSet<Long>();

                    ArrayList<TypeIdentifier> list = SoftTypeUtil.getChildTypes("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN", null);
                    for (TypeIdentifier ti : list) {
                        SoftTypeUtil.getTypeIdentifierDefinition(ti).getDisplay();
                        String type = ti.toString().substring(7);
                        TypeDefinitionReference tdr = ClientTypedUtility.getTypeDefinitionReference(type);
                        long typeId = 0;
                        if (tdr != null) {
                            typeId = tdr.getKey().getBranchId();
                        }

                        QuerySpec qs = new QuerySpec();
                        qs.setAdvancedQueryEnabled(true);
                        int ibaHolderIndex = qs.appendClassList(WTDocument.class, true);

                        qs.appendWhere(new SearchCondition(WTDocument.class,
                                        "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, typeId),
                                new int[]{0});
                        //qs.appendAnd();
                        //qs.appendWhere(new SearchCondition(WTDocument.class,WTDocument.LATEST_ITERATION, SearchCondition.IS_TRUE), new int[] { 0 });
                    /*qs.appendAnd();
                    qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.EQUAL, "APPROVED", true));
                    qs.appendAnd();*/
                        if(containerId!=null){
                            qs.appendAnd();
                            qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.CONTAINER_ID, SearchCondition.EQUAL,containerId), new int[] {ibaHolderIndex});
                        }else{
                            return ;
                        }

                        if (srcDept != null && !"".equals(srcDept)) {
                            qs.appendAnd();
                            int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
                            int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
                            // Latest Iteration
                            SearchCondition scLatestIteration = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
                            // String Value With IBA Holder
                            SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, WTAttributeNameIfc.ID_NAME);
                            // String Value With Definition
                            SearchCondition scJoinStringValueStringDefinition = new SearchCondition(wt.iba.value.StringValue.class, "definitionReference.key.id", StringDefinition.class,
                                    WTAttributeNameIfc.ID_NAME);
                            qs.appendWhere(scLatestIteration, ibaHolderIndex);
                            qs.appendAnd();
                            qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, ibaHolderIndex);
                            qs.appendAnd();
                            qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);
                            SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME, SearchCondition.EQUAL, "DEPT");
                            SearchCondition scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.EQUAL, srcDept.toUpperCase());
                            qs.appendAnd();
                            qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
                            qs.appendAnd();
                            qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
                        }

                        qs = new LatestConfigSpec().appendSearchCriteria(qs);
                        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
                        while(qr.hasMoreElements()){
                            Object[] obj = (Object[]) qr.nextElement();
                            WTDocument document = (WTDocument)obj[0];

	                        boolean  checkedOut = WorkInProgressHelper.isCheckedOut((Workable) document);
	                        if(!checkedOut){
	                            if(!flags.contains(document.getPersistInfo().getObjectIdentifier().getId())){
	                                System.out.println("转换工艺:"+document.getNumber()+" "+document.getVersionIdentifier().getValue());
	                                if(replaceProcessXML(document,srcDept,destDept)){
	                                    document = (WTDocument) PersistenceHelper.manager.refresh(document);
	                                    IBAUtility iba = new IBAUtility(document);
	                                    iba.setIBAValue("DEPT", destDept);
	                                    document = (WTDocument) iba.updateAttributeContainer(document);
	                                    iba.updateIBAHolder(document);
	                                }
	                                flags.add(document.getPersistInfo().getObjectIdentifier().getId());
	                            }
                            }
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

    private static void appendIBAValue(QuerySpec qs,int ibaHolderIndex,String key ,String srcDept,String queryType) throws QueryException {
        int ibaStringValueIndex = qs.appendClassList(wt.iba.value.StringValue.class, false);
        int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
        // Latest Iteration
        SearchCondition scLatestIteration = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
        // String Value With IBA Holder
        SearchCondition scJoinStringValueIBAHolder = new SearchCondition(wt.iba.value.StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, WTAttributeNameIfc.ID_NAME);
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
        if(ChangePbomDeptUtil.QUERYTYPE_EQUAL.equals(queryType)) {
             scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.EQUAL, srcDept.toUpperCase());
        }else  if(ChangePbomDeptUtil.QUERYTYPE_LEFTLIKE.equals(queryType)){
            scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE,"%"+ srcDept.toUpperCase());
        }else if(ChangePbomDeptUtil.QUERYTYPE_RIGHTLIKE.equals(queryType)){
            scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE, srcDept.toUpperCase()+"%");
        }else if(ChangePbomDeptUtil.QUERYTYPE_ALLLIKE.equals(queryType)){
            scStringValueValue1 = new SearchCondition(wt.iba.value.StringValue.class, _StringValue.VALUE, SearchCondition.LIKE, "%"+srcDept.toUpperCase()+"%");
        }
        qs.appendAnd();
        qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
        qs.appendAnd();
        qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
    }


    private static boolean  replaceProcessXML(
    		WTDocument doc,String srcDept,String destDept) {
    	Transaction trans = new Transaction();
    	 FileOutputStream fileOutputStream = null;
    	 InputStream inputStream = null;
    	 FileInputStream fileInputStream = null;
        try{
			trans.start();

            TypeIdentifier identifier = TypedUtility.getTypeIdentifier(doc);
            String typename = identifier.getTypename();
            String flag = "";
            if ("wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149.reportTechnics".equals(typename)) {
                flag ="baobiao";
            }
            String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator
                    + java.util.UUID.randomUUID().toString() + File.separator;
            String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(doc, tempFilePath);

            if(!zipFileName.endsWith(".zip")) return false;

            String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
            ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
            File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
            if(!xmlFile.exists()) return false;

            inputStream = new FileInputStream(xmlFile);
            SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
            Element rootElement = xmlUtil.getRootElement();
            if ("baobiao".equals(flag)) {
                Element techEle = (Element) rootElement.getChildren("XWReportTechnicsInfo").get(0);
                techEle.setAttribute("DEPT", destDept);

            } else {

                Element techEle = (Element) rootElement.getChildren("QMFawTechnicsInfo").get(0);
                techEle.setAttribute("DEPT", destDept);

                Element elIBAAttibutes = techEle.getChild("IBAAttibutes");
                if(elIBAAttibutes!=null){
                    List<Element> attributes = elIBAAttibutes.getChildren("attribute");
                    for(Element attribute:attributes){
                        if("部门".equals(attribute.getAttributeValue("key"))){
                        	String value = attribute.getAttributeValue("value");
                        	if(srcDept.equals(value)){
                        		attribute.setAttribute("value", destDept);
                        	}
                        }
                    }
                }
                List<Element> elQMProcedureInfos =  techEle.getChild("steps").getChildren("QMProcedureInfo");
                for(Element elQMProcedureInfo:elQMProcedureInfos){
                	String workShop = elQMProcedureInfo.getAttributeValue("workShop");
                	if(srcDept.equals(workShop)){
                        elQMProcedureInfo.setAttribute("workShop", destDept);
                	}
                }

            }
            // 替换工艺xml，打包工艺文件夹，上传工艺压缩包
            fileOutputStream = new FileOutputStream(new File(tempFilePath + subFileName + File.separator + subFileName
                    + ".xml"), false);
            Format format = Format.getPrettyFormat();
            format.setEncoding("GBK");
            XMLOutputter xmlOutput = new XMLOutputter(format);
            xmlOutput.output(xmlUtil.getDocument(), fileOutputStream);

            boolean compressflag = ApacheZipUtil.compress(tempFilePath + subFileName, tempFilePath + zipFileName);
            // 重新上传PBOM的XML

            if (compressflag) {
                /*WTDocumentUtil.setPrimaryForDocument(doc, zipFileName, new FileInputStream(new File(tempFilePath
                        + zipFileName)));*/
            	fileInputStream = 	 new FileInputStream(tempFilePath
                         + zipFileName);
				ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
				data = ContentServerHelper.service.updateContent((ContentHolder) doc, data,fileInputStream); // 更新内容

            }
            trans.commit();
			trans = null;
        }catch (Exception e){

            e.printStackTrace();
            return false;
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
        return true;

    }
}
