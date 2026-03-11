package ext.casc.workflow.util;

import com.glaway.mpm.constants.DocumentConstants;
import com.glaway.mpm.print.constants.PrintServerConstants;
import com.glaway.mpm.print.util.PrintDataBuildUtil;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.IBAHelper;
import ext.casc.util.IBAUtil;
import ext.casc.util.WCUtil;
import ext.casc.workflow.TaskConfigrationHelper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.pds.oracle81.OracleDataSource;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PrintDistributionHelper {

	 public static synchronized void setSelDepartValue(String workItemOid, String values) throws WTException, SQLException, RemoteException {
		 if(values == null || "".equals(values)){
			 return;
		 }
		 WorkItem wi = (WorkItem) WCUtil.getPersistable(workItemOid);
		 ObjectReference activity = wi.getSource();
		 WfAssignedActivity a =  (WfAssignedActivity) activity.getObject();
		 if(a.getName().equals("设置打印分发信息")){
			 WfProcess process =  a.getParentProcess();
			 // values值的格式为：wt.epm.EPMDocument:99141~一室:6;三室:3@wt.epm.EPMDocument:99108~二室:8;六室:2@
			 String pboOid = "";
			 if(TaskConfigrationHelper.getPBOByWfProcess(process) instanceof WTChangeOrder2){
				 WTChangeOrder2 co = (WTChangeOrder2) TaskConfigrationHelper.getPBOByWfProcess(process);
				 pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(co).getId());
			 }else if(TaskConfigrationHelper.getPBOByWfProcess(process) instanceof WTDocument){
				 WTDocument co = (WTDocument) TaskConfigrationHelper.getPBOByWfProcess(process);
				 pboOid = String.valueOf(PersistenceHelper.getObjectIdentifier(co).getId());
			 }
			 PrintDataBuildUtil.checkRepeatForAutoPrintApplyBypboOid(pboOid);//检验是否重复写入数据库
			 Connection conn = OracleDataSource.getOracleDataSource().getConnection();
			 conn.setAutoCommit(false);
			 Statement state = conn.createStatement();
			 List<String> data = new ArrayList<String>();
//			 String[] data = null;
			 if(values.contains("※")){
				 data = java.util.Arrays.asList(values.split("※"));
			 }else{
				 data.add(values);
			 }
			 for (String value : data) {
				 if (value != null && !"".equals(value)) {
					 String objOid = value.substring(0, value.indexOf("○"));
					 Persistable per = WCUtil.getPersistable(objOid);
					 String number = "";
					 String name = "";
					 String ver = "";
					 String vr = "";
					 String pscode = "";
					 String SECRET = "";
					 String APPLIER = "";
					 String DocType = "";
					 if (per instanceof WTChangeOrder2) {
						 WTChangeOrder2 doc = (WTChangeOrder2) per;
						 number = doc.getNumber();
						 name = doc.getName();
						 ver = doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue();
//	                     vr = new ReferenceFactory().getReferenceString(VersionControlHelper.service.getLatestIteration(doc, false));
						 vr = PrintServerConstants.OID_WTCHANGEORDER2 + doc.getPersistInfo().getObjectIdentifier().getId();
						 IBAUtil ibaUtil = new IBAUtil(doc);
						 pscode = CommonUtil.objectToString(ibaUtil.getIBAValue("PHASE_CODE"));
						 SECRET = CommonUtil.objectToString(ibaUtil.getIBAValue("SECRET"));
						 APPLIER = CommonUtil.objectToString(wi.getOwnership().getOwner().getName());
						 DocType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
						 if(DocType.contains("casc.sast.149.PROCESS_ECN")){
							 DocType = "工艺更改单";
						 } else if(DocType.contains("casc.sast.149.DOCUMENT_ECN")) {
							 DocType = "文档更改单";
						 }
					 }else if(per instanceof WTDocument){
						 WTDocument doc = (WTDocument) per;
						 IBAHelper helper = new IBAHelper(doc);
						 number = helper.getIBAValue(DocumentConstants.IBA_NUMBER);
						 if("".equals(number) || number == null){
							 number = doc.getNumber();
						 }
						 number = doc.getNumber();
						 name = doc.getName();
						 ver = doc.getVersionIdentifier().getValue() + "." + doc.getIterationIdentifier().getValue();
//		                 vr = new ReferenceFactory().getReferenceString(VersionControlHelper.service.getLatestIteration(doc, false));
						 vr = PrintServerConstants.OID_WTDOCUMENT + doc.getPersistInfo().getObjectIdentifier().getId();
						 IBAUtil ibaUtil = new IBAUtil(doc);
						 pscode = CommonUtil.objectToString(ibaUtil.getIBAValue("PHASE_CODE"));
						 SECRET = CommonUtil.objectToString(ibaUtil.getIBAValue("SECRET"));
						 APPLIER = CommonUtil.objectToString(wi.getOwnership().getOwner().getName());
						 DocType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
						 if(DocType.contains("casc.sast.149.GONGYIZONGFANGAN")){
							 DocType = "工艺总方案";
						 }else if(DocType.contains("casc.sast.149.GONGYIFENFANGAN")){
							 DocType = "工艺分方案";
						 }else if(DocType.contains("casc.sast.149.PROCESS_NOTICE")){
							 DocType = "工艺技术通知单";
						 }else if(DocType.contains("casc.sast.149.TECHNOLOGY_AGREEMENT")){
							 DocType = "工艺技术协议";
						 }else if(DocType.contains("casc.sast.149.PROCESS_PLAN")){
							 DocType = "工艺规程";
						 }else{
							 DocType = "其他";
						 }
					 }
					 String yz = value.substring(value.indexOf("●") + 1, value.length());
					 String selValue = value.substring(value.indexOf("○") + 1, value.indexOf("●"));
					 if(selValue.contains(";")){
						 selValue = selValue.replace(";", ",");
					 }
					 String uuid1 = UUID.randomUUID().toString();
					 StringBuffer sql = new StringBuffer();
					 sql.append(" INSERT INTO  GWPRINTAPPLYRECORD ");
					 sql.append(" (GWKEYID,DOCVR,DOCNUMBER,DOCNAME,VERSION,PHASECODE,SECRET,BATCH,APPLIER,APPLYDATE,PRINTSTATUS,DISMESSAGE,TECHNICSNUMBER,FILETYPE,PBOOID,OUTDEPT,PROCESSFILE)");
					 sql.append(" VALUES('"+uuid1+"','" + vr+"','" + number+"','" + name+"','" + ver+"','" + pscode+"','" + SECRET+"','" );
					 sql.append( yz+ "','"+APPLIER+"','"+wi.getCreateTimestamp()+"','"+"分发中"+"','"+selValue+"','"+ number +"','"+DocType+"','"+ pboOid +"','"+ "厂内电子','"+ "false" +"')");
					 System.out.println("---->>>sql:"+sql.toString());
					 state.executeUpdate(sql.toString());
					 String dept = "";
					 String count = "";
					 String sql2 = "";
					 if(selValue.contains(",")){
						 String[] dac = selValue.split(",");
						 for(int i = 0;i < dac.length;i++){
							 String uuid2 = UUID.randomUUID().toString();
							 dept = dac[i].substring(0, dac[i].lastIndexOf(":"));
							 count = dac[i].substring(dac[i].lastIndexOf(":")+1, dac[i].length()-1);
							 String value2 = "'"+uuid2+"','"  + vr + "','" + uuid1 + "','" + dept + "','" + count + "'";
							 sql2 = "insert into GWPRINTDISTRIBUTERECORD (GWKEYID,DOCVR,APPLYRECORDID,DISTRIBUTEDEPT,DISTRIBUTEQUANTITY) " +
									 "values("+ value2 +")";
							 state.executeUpdate(sql2);
							 saveGWPRINTBARCODE(count,uuid2,dept,state,yz);
						 }
					 }else{
						 String uuid2 = UUID.randomUUID().toString();
						 dept = selValue.substring(0, selValue.lastIndexOf(":"));
						 count = selValue.substring(selValue.lastIndexOf(":")+1, selValue.length()-1);
						 String value2 = "'"+uuid2+"','"  + vr + "','" + uuid1 + "','" + dept + "','" + count + "'";
						 sql2 = "insert into GWPRINTDISTRIBUTERECORD (GWKEYID,DOCVR,APPLYRECORDID,DISTRIBUTEDEPT,DISTRIBUTEQUANTITY) " +
								 "values("+ value2 +")";
						 state.executeUpdate(sql2);
						 saveGWPRINTBARCODE(count,uuid2,dept,state,yz);
					 }
				 }
			 }
			 conn.commit();
			 if(conn != null){
				 conn.close();
			 }
		 }
	 }

	 private static void saveGWPRINTBARCODE(String count, String uuid2, String dept, Statement state,String yz) throws SQLException {
			//保存到GWPRINTBARCODE
			int printCount = Integer.parseInt(count);
			for(int j = 0;j < printCount;j++){
				String uuid3 = UUID.randomUUID().toString();
				String value3 = "'"+uuid3+"','"  + uuid2 + "','" + dept + "','" + "未打印" + "','" + yz + "'";
				String sql3 = "insert into GWPRINTBARCODE (GWKEYID,APPLYRECORDID,GDEPT,FILESTATUS,BATCH) " +
						"values("+ value3 +")";
				state.executeUpdate(sql3);
			}

		}
	 public static String getSelDepartValue(String number) {
		  String values = "";
	        try {
	            Connection conn = OracleDataSource.getOracleDataSource().getConnection();
	            StringBuffer selectSQL = new StringBuffer();
	            selectSQL.append("select DISMESSAGE from GWPRINTAPPLYRECORD  where DOCNUMBER='"+number+"' ");
	            conn.setAutoCommit(false);
	            PreparedStatement ps = conn.prepareStatement(selectSQL.toString());
	            ResultSet resultset = ps.executeQuery();
	            while (resultset.next()) {
	            	values = resultset.getString(1);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	       return values;
	}
	 public static String getSelYinZhangValue(String number) {
		  String values = "";
	        try {
	            Connection conn = OracleDataSource.getOracleDataSource().getConnection();
	            StringBuffer selectSQL = new StringBuffer();
	            selectSQL.append("select BATCH from GWPRINTAPPLYRECORD  where DOCNUMBER='"+number+"' ");
	            conn.setAutoCommit(false);
	            PreparedStatement ps = conn.prepareStatement(selectSQL.toString());
	            ResultSet resultset = ps.executeQuery();
	            while (resultset.next()) {
	            	values = resultset.getString(1);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	       return values;
	}

	 public static List<String> getAllYinZhang() {
		 List<String> values = new ArrayList<String>();
	        try {
	            Connection conn = OracleDataSource.getOracleDataSource().getConnection();
	            StringBuffer selectSQL = new StringBuffer();
	            selectSQL.append("SELECT SEALNAME FROM GWYINZHANG ");
	            conn.setAutoCommit(false);
	            PreparedStatement ps = conn.prepareStatement(selectSQL.toString());
	            ResultSet resultset = ps.executeQuery();
	            while (resultset.next()) {
	                String yingzhangName = resultset.getString(1);
	                values.add(yingzhangName);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	       return values;
	}

	 public static List<String> getAllPrintDept(){
		 List<String> AllPrintDept = new ArrayList<String>();
		 WTProperties wtProperties;
		 try {
			wtProperties = WTProperties.getLocalProperties();
			String codebasePath = wtProperties.getProperty("wt.codebase.location");
	        String filePath = codebasePath + File.separator + "ext"
	                 + File.separator + "casc"
	                 + File.separator + "conf" + File.separator + "config_149.xml";
	        SAXReader reader = new SAXReader();
			Document document;
			document = reader.read(new File(filePath));
			Element rootElement = document.getRootElement();
			Element deptConfig  = rootElement.element("PrintDeptConfig");
			List<Element> configs = deptConfig.elements("config");
			for(Element e:configs){
				Element dept = e.element("dept");
				String sdept = dept.getText();
				AllPrintDept.add(sdept);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return AllPrintDept;
	 }
}
