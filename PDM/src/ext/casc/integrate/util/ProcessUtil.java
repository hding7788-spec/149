package ext.casc.integrate.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import wt.fc.ObjectVector;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.value.StringValue;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.TableColumn;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import wt.vc.config.LatestConfigSpec;

import com.glaway.mpm.model.TempObject;
import com.glaway.mpm.util.DBConnUtil;
import com.ptc.windchill.mpml.processplan.MPMPartToProcessPlanLink;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;

import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;

public class ProcessUtil {
	//根据产品图号获取关联的工艺规程列表(默认最新版本)
	 public static List getProcessPlan(WTPart  part) throws WTException {

		 if (part == null) {
	            return null;
	        }
	        List list = new ArrayList();
	        MPMProcessPlan plan = null;
	        try {
	            QueryResult linkQR = MPMProcessPlanHelper.service.getPartToProcessPlanLinks(part);
	            ObjectVector ov = new ObjectVector();
	            for (; linkQR.hasMoreElements(); ov.addElement(plan)) {
	                MPMPartToProcessPlanLink planLink = (MPMPartToProcessPlanLink) linkQR.nextElement();
	                plan = planLink.getProcessPlan();
	            }

	            LatestConfigSpec lcsConfigSpec = new LatestConfigSpec();
	            QueryResult qResult = new QueryResult(ov);
	            for (qResult = lcsConfigSpec.process(qResult); qResult.hasMoreElements(); list.add((MPMProcessPlan) qResult
	                    .nextElement()))
	                ;
	        } catch (WTException e) {
	            e.printStackTrace();
	        }
	        return list;
	  }

	 /*根据产品图号及工艺类型获取工艺规程列表
	  * number:产品图号
	  * fileType:规程类型- 主工艺/辅工艺
	  * versionTpye:版本类型
	  * version:版本
	 */
	 public static QueryResult getProcessPlanByPartNumType(String number,String fileTypeB,String fileTypeC,String versionType,String version) throws WTException{
		 QueryResult qResult = new QueryResult();
		 if(versionType.equals(Constants.LABEL_VERSION_BATCH))
			 qResult = getProcessPlanByBatch(number,version,fileTypeB,fileTypeC);
		 return qResult;
	 }


	 /*获取批次版本关联的工艺规程
	  * batchVerison:批次版本值
	  * fileTypeB:规程类型- 正式工艺/临时工艺
	  * fileTypeC:规程类型-主工艺/辅工艺
	  * batchVersion:批次版本
	  * */
	 public static QueryResult getProcessPlanByBatch(String number,String batchVersion,String fileTypeB,String fileTypeC) throws WTException{
		 List list = new ArrayList();
		 QueryResult qResult = null;
		 WTPart part = (WTPart)WCUtil.getPartByNumber(number);
		 if (part == null) {
	            return null;
	        }
	        MPMProcessPlan plan = null;
	        try {
	            QueryResult linkQR = MPMProcessPlanHelper.service.getPartToProcessPlanLinks(part);
	            ObjectVector ov = new ObjectVector();
	            while(linkQR.hasMoreElements()){
	            	 MPMPartToProcessPlanLink planLink = (MPMPartToProcessPlanLink) linkQR.nextElement();
	            	 MPMProcessPlan processPlan = planLink.getProcessPlan();
		            	//根据批次和规程类型(主/辅)筛选出目标工艺规程
		            	IBAUtility utility = new IBAUtility(processPlan);

		            	String processTypeB = utility.getIBAValue("GYWJLB");//查寻是否正式工艺/临时工艺
		            	String processTypeC = utility.getIBAValue("ZFGYLB");//查寻是否主工艺/辅工艺
		            	String batch = utility.getIBAValue("BATCH");// 查寻批次信息
		            	//初始化正式/临时工艺
		            	if(processTypeB.equals(Constants.PROCESS_TYPEB_FORMAL))
		            		processTypeB = Constants.LABEL_PROCESS_TYPEB_FORMAL;
		            	else if(processTypeB.equals(Constants.PROCESS_TYPEB_TEMP))
		            		processTypeB = Constants.LABEL_PROCESS_TYPEB_TEMP;
		            	else
		            		processTypeB = Constants.LABEL_PROCESS_TYPEB_ALL;

		            	//初始化主/辅工艺
		            	if(processTypeC.equals(Constants.PROCESS_TYPEC_PRIMARY))
		            		processTypeC = Constants.LABEL_PROCESS_TYPEC_PRIMARY;
		            	else if(processTypeC.equals(Constants.PROCESS_TYPEC_ASSIST))
		            		processTypeC = Constants.LABEL_PROCESS_TYPEC_ASSIST;
		            	else
		            		processTypeC = Constants.LABEL_PROCESS_TYPEC_ALL;

		            	//正式工艺文件获取(默认主工艺,一般仅一份)
		            	if(fileTypeB.equals(Constants.LABEL_PROCESS_TYPEB_FORMAL)&&fileTypeB.equals(processTypeB)){
		            		if(processTypeC.equals(Constants.LABEL_PROCESS_TYPEC_PRIMARY)&&batch.equals(batchVersion))
		            			ov.addElement(processPlan);
		            	}
		            	//临时工艺文件获取
		            	if(fileTypeB.equals(Constants.LABEL_PROCESS_TYPEB_TEMP)&&fileTypeB.equals(processTypeB)){
		            		ov.addElement(processPlan);
		            	}
		            	//默认获取所有工艺文件
		            	if(fileTypeB.equals(Constants.LABEL_PROCESS_TYPEB_ALL)){
		            		if(batchVersion.equals(""))
		            			ov.addElement(processPlan);
		            		else if(batch.equals(batchVersion))
		            			ov.addElement(processPlan);
		            	}


		            }

	            //最新版本工艺文件列表
	            LatestConfigSpec lcsConfigSpec = new LatestConfigSpec();
	            qResult = new QueryResult(ov);
	            qResult = lcsConfigSpec.process(qResult);
            } catch (WTException e) {
	            e.printStackTrace();
	        }

		 return qResult;
	 }

	 //根据工艺规程编号获取工艺规程()
	  public static MPMProcessPlan getProcessPlanByNumber(String number) throws WTException {
	        MPMProcessPlan pplan = null;
	        QueryResult qr = null;
	        QuerySpec qs = new QuerySpec(MPMProcessPlan.class);
	        qs.appendWhere(new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, number,
	                false));
	        qr = PersistenceHelper.manager.find((StatementSpec) qs);
	        LatestConfigSpec lcs = new LatestConfigSpec();
	        qr = lcs.process(qr);
	        if (qr.hasMoreElements()) {
	            pplan = (MPMProcessPlan) qr.nextElement();
	        }
	        return pplan;

	    }

	  /*根据工艺文件编号和批次号查询最新版本的工艺文件
	   *
	   *
	   * */
	  public static MPMProcessPlan getProcessPlanByNumBatch(String number,String batch) throws WTException, WTPropertyVetoException{
		  	MPMProcessPlan pplan = null;
	        QuerySpec qSpec = new QuerySpec();
	        int index0 = qSpec.addClassList(MPMProcessPlan.class, true);
	        int index1 = qSpec.addClassList(StringValue.class, false);
	        int index2 = qSpec.addClassList(StringDefinition.class, false);

	        String[] aliases = new String[3];
	        aliases[0] = qSpec.getFromClause().getAliasAt(index0);
	        aliases[1] = qSpec.getFromClause().getAliasAt(index1);
	        aliases[2] = qSpec.getFromClause().getAliasAt(index2);

	        TableColumn tc0 = new TableColumn(aliases[0], "ida2a2");
	        TableColumn tc1 = new TableColumn(aliases[0], "number");
	        TableColumn tc2 = new TableColumn(aliases[1], "IDA3A4");
	        TableColumn tc3 = new TableColumn(aliases[1], "IDA3A6");
	        TableColumn tc4 = new TableColumn(aliases[1], "value");
	        TableColumn tc5 = new TableColumn(aliases[2], "IDA2A2");
	        TableColumn tc6 = new TableColumn(aliases[2], "name");

	        qSpec.appendWhere(new SearchCondition(tc0, "=", tc2), new int[] { index0, index1 });
	        qSpec.appendAnd();
	        qSpec.appendWhere(new SearchCondition(tc3, "=", tc5), new int[] { index1, index2 });
	        qSpec.appendAnd();
	        qSpec.appendWhere(new SearchCondition(tc1, "=", new ConstantExpression(number)), new int[] { index0 });
	        qSpec.appendAnd();
	        //批次信息
	        qSpec.appendWhere(new SearchCondition(tc4, "=", new ConstantExpression(batch)), new int[] { index1 });
	        qSpec.appendAnd();
	        qSpec.appendWhere(new SearchCondition(tc6, "=", new ConstantExpression("BATCH")), new int[] { index2 });
//	    	qSpec.appendAnd();
//	    	//工艺文件编号信息
//	        qSpec.appendWhere(new SearchCondition(tc4, "=", new ConstantExpression(number)), new int[] { index1 });
//	        qSpec.appendAnd();
//	        qSpec.appendWhere(new SearchCondition(tc6, "=", new ConstantExpression("GYWJBH")), new int[] { index2 });


	        // System.out.println("----sql:"+qSpec.toString());
	        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
            LatestConfigSpec lcs = new LatestConfigSpec();
            qResult = lcs.process(qResult);
            if (qResult.hasMoreElements()) {
            	pplan = (MPMProcessPlan) qResult.nextElement();
            }
		  return pplan;
	  }

	  //迭代获取工艺规程关联的所有工序
	  public static List<MPMOperation> getAllMpmOperationsByMPMProPlan(MPMProcessPlan processPlan) throws WTException {
	        QueryResult qResult = getMPMOperationUsageLinkByMpmPr(processPlan);
	        List<MPMOperation> list = new ArrayList<MPMOperation>();
	        while (qResult.hasMoreElements()) {
	            MPMOperationUsageLink link = (MPMOperationUsageLink) qResult.nextElement();
	            Persistable persistable = link.getRoleBObject();
	            if (persistable instanceof MPMOperationMaster) {
	                MPMOperationMaster master = (MPMOperationMaster) persistable;
	                QueryResult qResult2 = VersionControlHelper.service.allVersionsOf(master);
	                if (qResult2.hasMoreElements()) {
	                    MPMOperation operation = (MPMOperation) qResult2.nextElement();
	                    if (!list.contains(operation)) {
	                        list.add(operation);
	                    }
	    //149一期不获取工步信息，即只获取单级工序即可
	   //                 getAllChildMpmoOperations(operation, list);
	                }
	            }
	        }
	        return list;
	    }

	  public static QueryResult getMPMOperationUsageLinkByMpmPr(MPMProcessPlan plan) throws WTException {
	        QuerySpec qSpec = new QuerySpec(MPMOperationUsageLink.class);
	        int[] index = { 0 };
	        long longId = PersistenceHelper.getObjectIdentifier(plan).getId();
	        SearchCondition sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleAObjectRef.key.id",
	                SearchCondition.EQUAL, longId);
	        qSpec.appendWhere(sCondition, index);
	        return PersistenceHelper.manager.find((StatementSpec) qSpec);
	   }

	  public static List<MPMOperation> getAllChildMpmoOperations(MPMOperation operation, List<MPMOperation> list)
      	throws WTException {
		  QueryResult qResult = getMPMOperationUsageLinkByMpmOper(operation);
		  while (qResult.hasMoreElements()) {
		      MPMOperationUsageLink link = (MPMOperationUsageLink) qResult.nextElement();
		      Persistable persistable = link.getRoleBObject();
		      if (persistable instanceof MPMOperationMaster) {
		          MPMOperationMaster master = (MPMOperationMaster) persistable;
		          QueryResult qResult2 = VersionControlHelper.service.allVersionsOf(master);
		          if (qResult2.hasMoreElements()) {
		              MPMOperation childOperation = (MPMOperation) qResult2.nextElement();
		              if (!list.contains(childOperation)) {
		                  list.add(childOperation);
		              }
		              getAllChildMpmoOperations(childOperation, list);
		          }
		      }
		  }
		  return list;
	  }

	    public static QueryResult getMPMOperationUsageLinkByMpmOper(MPMOperation operation) throws WTException {
	        QuerySpec qSpec = new QuerySpec(MPMOperationUsageLink.class);
	        int[] index = { 0 };
	        long longId = PersistenceHelper.getObjectIdentifier(operation).getId();
	        SearchCondition sCondition = new SearchCondition(MPMOperationUsageLink.class, "roleAObjectRef.key.id",
	                SearchCondition.EQUAL, longId);
	        qSpec.appendWhere(sCondition, index);
	        return PersistenceHelper.manager.find((StatementSpec) qSpec);
	    }
	    public static List<TempObject> getAllTechnicsNumber(String technicsNumber, String version){
	    	List<TempObject> technicsNumberList = new ArrayList<TempObject>();
	    	TempObject tempObj = new TempObject();
	    	tempObj.setNumber(technicsNumber);
	    	tempObj.setVersion(version);
	    	technicsNumberList.add(tempObj);
			DBConnUtil conn = null;
			try {
				conn= new DBConnUtil();
				String sql = "select DISTINCT FZTECHNICSNUMBER,FZTECHNICSVERSION from GL_ZHUFULINK where ZZTECHNICSNUMBER='"+technicsNumber+"' and ZZTECHNICSVERSION='"+version+"'";
				ResultSet rs = conn.executeQuery(sql);
				while(rs.next()){
					String fzTechnicsNumber = rs.getString("FZTECHNICSNUMBER");
					String fzTechnicsVersion = rs.getString("FZTECHNICSVERSION");
					tempObj = new TempObject();
					tempObj.setNumber(fzTechnicsNumber);
					tempObj.setVersion(fzTechnicsVersion);
					technicsNumberList.add(tempObj);
				}
				conn.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
 			}
		return technicsNumberList;
	    }
}
