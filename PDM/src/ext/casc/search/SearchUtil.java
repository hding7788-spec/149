package ext.casc.search;

import java.beans.PropertyVetoException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import java.util.Iterator;

import java.util.List;

import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import wt.change2.ChangeOrder2;
import wt.change2.WTChangeOrder2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.project.Role;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.Typed;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.VersionControlHelper;
import wt.workflow.definer.UserEventVector;
import wt.workflow.engine.WfActivity;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfVotingEventAudit;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WorkItem;

import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.wvs.server.util.Util;

import ext.ases.changepackaged.ChangePackaged;
import ext.ases.envelope.EnvelopeMemberLink;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.fileprint.FilePrintUtil2;
import ext.casc.util.ExcelFileGenerator;
import ext.casc.util.IBAHelper;
import ext.casc.util.IBAUtil;
import ext.casc.workflow.TaskConfigrationHelper;



public class SearchUtil implements RemoteAccess{

	public static String tempCache="";
	public static final String PRINTKEY = "PRINT";

	/**
	 * 通过流程获取流程的签审信息
	 *
	 */
	@SuppressWarnings("deprecation")
	public static ArrayList<HashMap<String, String>> getWfProcessRecordes(Object self) throws WTException {

		ArrayList<HashMap<String, String>> historyList = new ArrayList<HashMap<String, String>>();

		ArrayList<WorkItem> wiList = getWorkItemFromProcess(self);

		//找到最后一次重新提交的时间
		long tempRestart=-1;
		for(int x=0;x<wiList.size();x++)
		{
			//获取某一个流程节点
			WorkItem wi = wiList.get(x);

			//获取流程节点的 投票  对象
			WfVotingEventAudit wfvotevent = getWfVoteWithWorkItem(wi);
			if (wfvotevent != null)
			{
				UserEventVector eventVec=wfvotevent.getEventList();
				if(eventVec!=null&&eventVec.size()>0)
				{
					if(((String)eventVec.get(0)).contains("重新提交"))
					{
						Timestamp ts1=wfvotevent.getTimestamp();
						if(ts1!=null)
						{
							long tempEach=ts1.getTime();
							if(tempEach>tempRestart)
							{
								tempRestart=tempEach;
							}
						}
					}
				}
			}
		}
		for (int i = 0; i < wiList.size(); i++)
		{
			WorkItem wi = wiList.get(i);

			//根据workitem获取投票事件
			WfVotingEventAudit wfvotevent = getWfVoteWithWorkItem(wi);
			if (wfvotevent != null)
			{
				UserEventVector eventVec=wfvotevent.getEventList();
				if(eventVec!=null&&eventVec.size()>0)
				{
					if(((String)eventVec.get(0)).contains("驳回"))
						continue;
				}
				Timestamp ts = wfvotevent.getTimestamp();
				String actTime="0";
				if(ts!=null)
				{
					if(ts.getTime()<tempRestart)
					{
						continue;
					}
					actTime=ts.getTime()+"";
				}

				//获取活动名称，比如 校对 审核 标审
				String actName = wfvotevent.getActivityName();
				WfActivity localWfActivity = (WfActivity) wi.getSource().getObject();

				//获取外部会签信息
				String signValues = (String) TaskConfigrationHelper.getProcessVariableValue(localWfActivity.getParentProcess(), "outSignInfos");
				String tempValues="";
				System.out.println("signValues===>>"+signValues);
				if(signValues!=null){
					if(signValues.contains("~")){
						String[] s1=signValues.split("~");
						if(s1.length>=2){
							tempValues=s1[1];
						}
						if(tempValues.endsWith("@")){
							tempValues=tempValues.replaceAll("@", "");
						}
						if(tempValues.contains(";")){
							String[] tempVal=tempValues.split(";");
							tempValues="";
							for(int b=0;b<tempVal.length;b++){
								if(tempVal[b].contains(":")){
									String[] tempVal2=tempVal[b].split(":",-1);
									if(tempVal2.length>=2){
										tempValues+=tempVal2[1]+",";
									}
								}
							}
						}else{
							if(tempValues.contains(":")){
								String[] tempVal2=tempValues.split(":",-1);
								tempValues="";
								if(tempVal2.length>=2){
									tempValues+=tempVal2[1]+",";
								}
							}
						}
					}
				}
					if (tempValues.endsWith(",")) {
						tempValues = tempValues.substring(0,
								tempValues.length() - 1);
					}

				//以下四句是获取投票事件的完成人
				WTPrincipalReference localWTPrincipalReference = wfvotevent.getAssigneeRef();
				WTPrincipal localWTPrincipal = (WTPrincipal) localWTPrincipalReference.getObject();
				WTUser user = (WTUser) localWTPrincipal;
				String actUserName = user.getFullName();// 活动完成者

				Role workRole = wfvotevent.getRole();
				String roleName = workRole.getFullDisplay();// 活动的角色

				String userComment = wfvotevent.getUserComment();// 活动的注解\备注

				HashMap<String, String> historyMap = new HashMap<String, String>();
				if(actName.contains("外部会签")&&signValues!=null)
				{
					historyMap.put("waibuSign", tempValues);
				}
				historyMap.put("actName", actName);
				historyMap.put("actTime", actTime);
				historyMap.put("actUserName", actUserName);
				historyMap.put("roleName", roleName);
				historyMap.put("userComment", userComment);

				historyList.add(historyMap);
			}
		}

		return historyList;
	}


	/**
	 * 根据流程获取WorkItem
	 *
	 *
	 */
	@SuppressWarnings("deprecation")
	public static ArrayList<WorkItem> getWorkItemFromProcess(Object self) throws WTException {

		ArrayList<WorkItem> wiList = new ArrayList<WorkItem>();

		QuerySpec qs = new QuerySpec();
		int wfpIdx = qs.appendClassList(WfProcess.class, false);
		int wfiIdx = qs.appendClassList(WorkItem.class, true);
		int wfaIdx = qs.appendClassList(WfAssignedActivity.class, false);

		WfProcess process = getWfProcessBySelf(self);


		qs.setAdvancedQueryEnabled(true);

		qs.appendWhere(new SearchCondition(WfProcess.class, "thePersistInfo.theObjectIdentifier.id",
				SearchCondition.EQUAL, getLongOid(process)), wfpIdx);
		qs.appendAnd();

		qs.appendWhere(new SearchCondition(WorkItem.class, "source.key.id", WfAssignedActivity.class,
				"thePersistInfo.theObjectIdentifier.id"), wfiIdx, wfaIdx);
		qs.appendAnd();

		qs.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key.id", WfProcess.class,
				"thePersistInfo.theObjectIdentifier.id"), wfaIdx, wfpIdx);

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			Persistable[] ps = (Persistable[]) qr.nextElement();
			WorkItem worki = (WorkItem) ps[0];
			wiList.add(worki);
		}

		return wiList;
	}


	/**
	 * 根据WorkItem的id获取WfVotingEventAudit
	 *
	 *
	 */
	@SuppressWarnings("deprecation")
	public static WfVotingEventAudit getWfVoteWithWorkItem(WorkItem wi) throws WTException {
		WfVotingEventAudit wfVoteEvent = null;
		QuerySpec qs = new QuerySpec();
		int wfvoteIdx = qs.addClassList(WfVotingEventAudit.class, true);

		qs.setAdvancedQueryEnabled(true);
		qs.appendWhere(new SearchCondition(WfVotingEventAudit.class, "theWorkItemReference.key.id",
				SearchCondition.EQUAL, getLongOid(wi)), wfvoteIdx);

		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		while (qr.hasMoreElements()) {
			Persistable[] ps = (Persistable[]) qr.nextElement();
			wfVoteEvent = (WfVotingEventAudit) ps[0];
		}
		return wfVoteEvent;
	}

	/**
	 * 转换流程对象，如果是活动就获取对应的流程
	 *
	 *
	 */
	public static WfProcess getWfProcessBySelf(Object self) throws WTException {

		if (self instanceof ObjectReference) {
			self = ((ObjectReference) self).getObject();
		}
		WfProcess wf = null;
		if (self instanceof WfAssignedActivity) {
			wf = ((WfAssignedActivity) self).getParentProcess();
		} else {
			wf = (WfProcess) self;
		}
		return wf;
	}

	public static long getLongOid(Persistable persistable) {
		return persistable.getPersistInfo().getObjectIdentifier().getId();
	}

	public static void main(String[] args) throws Exception {
		//getSignData("0000000727");

	}

	public static List<WfInfo> getSelfSignData (ArrayList list) throws Exception
	{
		if (!RemoteMethodServer.ServerFlag)
		{
            String method = "getSelfSignData";
            Class[] types = { ArrayList.class };
            Object[] vals = { list};
            try
            {
                RemoteMethodServer rms = RemoteMethodServer.getDefault();
                rms.invoke(method, SearchUtil.class.getName(), null, types, vals);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
		List<WfInfo> infoList=new ArrayList<WfInfo>();

		String currentUserName =((WTUser) SessionHelper.getPrincipal()).getFullName();

		int containCurrentUser = -1;

		for (int i = 0; i < list.size(); i++)
		{
			NmContext nmContext = (NmContext) list.get(i);
			String oid=nmContext.toString();
			oid=oid.substring(oid.indexOf("VR:"),oid.length()-2);
			ArrayList<HashMap<String, String>> processSign = new ArrayList<HashMap<String, String>>();
			Persistable obj = getObject(oid);
			if(obj==null)
			{
				continue;
			}
			if(obj instanceof WTPart||obj instanceof WTDocument||obj instanceof EPMDocument||obj instanceof ChangeOrder2)
			{

				WfInfo info=new WfInfo();
				String cadType="";
				if(obj instanceof WTDocument)
				{
					WTDocument doc=(WTDocument) obj;

					//获取该文档类型的内部名称
					cadType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier((Typed) doc);

					//构建一个属性帮助器
					IBAUtil iba=new IBAUtil(doc);

					//设置编制部门的属性值，软属性需要构造一个IBAUtil对象来获取属性值
					info.setDepartment(iba.getIBAValue("BZBM")==null?"":iba.getIBAValue("BZBM"));

					//设置创建时间的属性值
					info.setCreattime(doc.getCreateTimestamp().toLocaleString());

					//设置创建者的属性值
					info.setCreater(doc.getCreatorFullName());

					/*
					if(doc.getCreatorFullName() != null && doc.getCreatorFullName().equals(currentUserName))
					{
						containCurrentUser =1;
					}*/

					//设置编号的属性值
					info.setNumber(doc.getNumber());

					//设置名称的属性值
					info.setName(doc.getName());

					//设置上下文的名称
					info.setContainer(doc.getContainerName());

					//设置状态，需要首先获取生命周期状态 ，才能获取状态名称
					info.setStatus(doc.getLifeCycleState().getDisplay(Locale.CHINA));

					//设置修改时间
					info.setModifytime(doc.getModifyTimestamp().toLocaleString());

					//设置描述
					info.setDescription(doc.getDescription());

					//设置版本
					info.setVersions(doc.getVersionInfo().getIdentifier().getValue() + "."+doc.getIterationIdentifier().getValue());
				}else if(obj instanceof WTPart)
				{
					WTPart part=(WTPart) obj;
					IBAUtil iba=new IBAUtil(part);
					info.setDepartment(iba.getIBAValue("BZBM")==null?"":iba.getIBAValue("BZBM"));
					info.setCreattime(part.getCreateTimestamp().toLocaleString());
					info.setCreater(part.getCreatorFullName());
					info.setNumber(part.getNumber());
					info.setName(part.getName());
					info.setContainer(part.getContainerName());
					info.setStatus(part.getLifeCycleState().getDisplay(Locale.CHINA));
					info.setModifytime(part.getModifyTimestamp().toLocaleString());
					info.setDescription("");
					info.setVersions(part.getVersionInfo().getIdentifier().getValue() + "."+part.getIterationIdentifier().getValue());
				}else if(obj instanceof EPMDocument){
					EPMDocument empDoc=(EPMDocument) obj;
					IBAUtil iba=new IBAUtil(empDoc);
					info.setDepartment(iba.getIBAValue("BZBM")==null?"":iba.getIBAValue("BZBM"));
					//info.setAuthorApplication(empDoc.getAuthoringApplication().getDisplay());
					info.setCreattime(empDoc.getCreateTimestamp().toLocaleString());
					info.setCreater(empDoc.getCreatorFullName());
					info.setNumber(empDoc.getNumber());
					info.setName(empDoc.getName());
					info.setContainer(empDoc.getContainerName());
					info.setStatus(empDoc.getLifeCycleState().getDisplay(Locale.CHINA));
					info.setModifytime(empDoc.getModifyTimestamp().toLocaleString());
					info.setDescription("");
					info.setVersions(empDoc.getVersionInfo().getIdentifier().getValue() + "."+empDoc.getIterationIdentifier().getValue());
				}else if(obj instanceof ChangeOrder2){
					ChangeOrder2 co2=(ChangeOrder2) obj;
					info.setCreattime(co2.getCreateTimestamp().toLocaleString());
					info.setCreater(co2.getCreatorFullName());
					info.setNumber(co2.getNumber());
					info.setName(co2.getName());
					info.setContainer(co2.getContainerName());
					info.setStatus(co2.getLifeCycleState().getDisplay(Locale.CHINA));
					info.setModifytime(co2.getModifyTimestamp().toLocaleString());
					info.setDescription("");
					info.setVersions(co2.getVersionInfo().getIdentifier().getValue() + "."+co2.getIterationIdentifier().getValue());
				}
				WfProcess wf = null;
				if (!"".equals(cadType) && cadType.endsWith("CAD_DOC"))
				{
					//用CAD_DOC这种类型的对象去查询关联的 批量签审包的Link集合
					QueryResult qr=PersistenceHelper.manager.navigate(obj, "theProcessEnvelope", EnvelopeMemberLink.class,false);
					while(qr.hasMoreElements())
					{
						//获取一个Link
						EnvelopeMemberLink link=(EnvelopeMemberLink)qr.nextElement();

						//通过Link获取包对象
						ProcessEnvelope pe = link.getProcessEnvelope();

						//通过包获取流程的集合
						QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(pe, null, null);

						while (qrProcs.hasMoreElements())
						{
							wf = (WfProcess) qrProcs.nextElement();
						}
					}
				}
				else
				{
					QueryResult qrProcs = WfEngineHelper.service
							.getAssociatedProcesses(obj, null, null);

					while (qrProcs.hasMoreElements()) {
						wf = (WfProcess) qrProcs.nextElement();
					}
				}
					if (wf != null) {
						processSign = getWfProcessRecordes(wf);
					}else{
						continue;
					}
					long tempJiaodui=-1;
					long tempShenhe=-1;
					long tempNeibu=-1;
					long tempWaibu=-1;
					long tempBiaoshen=-1;
					long tempPizhun=-1;
					long tempXinghao=-1;
					long tempDayin=-1;
					String neibuhuiqian="";
					List<String> neibuList=new ArrayList<String>();
					for(int b=0;b<processSign.size();b++)
					{
						HashMap<String, String> map  = processSign.get(b);
						if(map.get("actName")!=null&&map.get("actName").contains("内部会签"))
						{
							if(!neibuList.contains(map.get("actUserName")))
							{
							neibuhuiqian+=map.get("actUserName")+",";
							neibuList.add(map.get("actUserName"));
							}
						}
					}
					if(neibuhuiqian.endsWith(",")){
						neibuhuiqian=neibuhuiqian.substring(0,neibuhuiqian.length()-1);
					}

				for (int a = 0; a < processSign.size(); a++) {
					HashMap<String, String> map  = processSign.get(a);
					System.out.println("actName===>>"+map.values());
					if(map.get("actName")!=null&&map.get("actName").contains("校对")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempJiaodui){
							tempJiaodui=Long.parseLong(map.get("actTime"));
							info.setJiaodui(map.get("actUserName"));
							}
						}
					}else if(map.get("actName")!=null&&map.get("actName").contains("审核")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempShenhe){
								tempShenhe=Long.parseLong(map.get("actTime"));
							info.setShenhe(map.get("actUserName"));
							}
						}
//						info.setShenhe(map.get("actUserName"));
					}else if(map.get("actName")!=null&&map.get("actName").contains("内部会签")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempNeibu){
								tempNeibu=Long.parseLong(map.get("actTime"));
								info.setNeibuhuiqian(neibuhuiqian);
							}
						}
//						info.setNeibuhuiqian(map.get("actUserName"));
					}else if(map.get("actName")!=null&&map.get("actName").contains("外部会签")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempWaibu){
								tempWaibu=Long.parseLong(map.get("actTime"));
								info.setWaibuhuiqian(map.get("waibuSign"));
							}
						}
//						info.setWaibuhuiqian(map.get("actUserName"));
					}else if(map.get("actName")!=null&&map.get("actName").contains("标审")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempBiaoshen){
								tempBiaoshen=Long.parseLong(map.get("actTime"));
								info.setBiaoshen(map.get("actUserName"));
							}
						}
//						info.setBiaoshen(map.get("actUserName"));
					}else if(map.get("actName")!=null&&map.get("actName").contains("批准")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempPizhun){
								tempPizhun=Long.parseLong(map.get("actTime"));
								info.setPizhun(map.get("actUserName"));
							}
						}
//						info.setPizhun(map.get("actUserName"));
					}else if(map.get("actName")!=null&&(map.get("actName").contains("型号调度")||map.get("actName").contains("设置"))){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempXinghao){
								tempXinghao=Long.parseLong(map.get("actTime"));
								info.setXinghaodiaodu(map.get("actUserName"));
							}
						}
//						info.setXinghaodiaodu(map.get("actUserName"));
					}else if(map.get("actName")!=null&&map.get("actName").contains("打印")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempDayin){
								tempDayin=Long.parseLong(map.get("actTime"));
								info.setDayin(map.get("actUserName"));
							}
						}
//						info.setDayin(map.get("actUserName"));
					}
				}
				if(info.getCreater() != null && info.getCreater().equals(currentUserName) )
				{
					containCurrentUser = 1;
				}

				if(info.getJiaodui() != null && info.getJiaodui().equals(currentUserName))
				{
					containCurrentUser = 1;
				}

				if(info.getShenhe() != null && info.getShenhe().equals(currentUserName))
				{
					containCurrentUser = 1;
				}

				if(info.getNeibuhuiqian() != null && info.getNeibuhuiqian().contains(currentUserName))
				{
					containCurrentUser = 1;
				}

				if(info.getWaibuhuiqian() != null && info.getWaibuhuiqian().contains(currentUserName))
				{
					containCurrentUser = 1;
				}

				if(info.getBiaoshen() != null && info.getBiaoshen().equals(currentUserName))
				{
					containCurrentUser = 1;
				}

				if(info.getPizhun() != null && info.getPizhun().equals(currentUserName))
				{
					containCurrentUser = 1;
				}

				if(info.getXinghaodiaodu() != null && info.getXinghaodiaodu().equals(currentUserName))
				{
					containCurrentUser = 1;
				}

				if(info.getDayin() != null && info.getDayin().equals(currentUserName))
				{
					containCurrentUser = 1;
				}

				if(1 == containCurrentUser)
				{
					infoList.add(info);
				}

				containCurrentUser = -1;


			}
		}
		return infoList;
	}


    private String tranfString(String s) {

        if (s == null)

            return "";

        if ("null".equals(s))

            return "";

        return s;

    }


    private void genDownloadDataExcelData(ArrayList<ArrayList<String>> dataList, Object obj) throws WTException {

        ArrayList<String> datas = new ArrayList<String>();

        if (obj instanceof WTDocument) {

            WTDocument o = (WTDocument) obj;

            IBAHelper iba = new IBAHelper();

            datas.add(o.getNumber());

            datas.add(tranfString(iba.getIBAStringValue(o, "CINDEX")));// 图号

            datas.add(o.getName());

            datas.add(o.getVersionInfo().getIdentifier().getValue() + "."

                    + o.getIterationInfo().getIdentifier().getValue());

            datas.add(tranfString(iba.getIBAStringValue(o, "MINDEX")));

            datas.add(tranfString(iba.getIBAStringValue(o, "DESIGNER")));

            datas.add(tranfString(iba.getIBAStringValue(o, "COMPANY")));// 设计单位

        } else if (obj instanceof EPMDocument) {

            EPMDocument o = (EPMDocument) obj;

            IBAHelper iba = new IBAHelper();

            datas.add(o.getNumber());

            datas.add(tranfString(iba.getIBAStringValue(o, "CINDEX")));// 图号

            datas.add(o.getName());

            datas.add(o.getVersionInfo().getIdentifier().getValue() + "."

                    + o.getIterationInfo().getIdentifier().getValue());

            datas.add(tranfString(iba.getIBAStringValue(o, "MINDEX")));

            datas.add(tranfString(iba.getIBAStringValue(o, "DESIGNER")));

            datas.add(tranfString(iba.getIBAStringValue(o, "COMPANY")));// 设计单位

        } else if (obj instanceof ProcessEnvelope) {

            ProcessEnvelope o = (ProcessEnvelope) obj;

            IBAHelper iba = new IBAHelper();

            datas.add(o.getNumber());

            datas.add(tranfString(iba.getIBAStringValue(o, "CINDEX")));// 图号

            datas.add(o.getName());

            datas.add("");

            datas.add(tranfString(iba.getIBAStringValue(o, "MINDEX")));

            datas.add(tranfString(iba.getIBAStringValue(o, "DESIGNER")));

            datas.add(tranfString(iba.getIBAStringValue(o, "COMPANY")));// 设计单位

        } else if (obj instanceof ChangePackaged) {

            ChangePackaged o = (ChangePackaged) obj;

            IBAHelper iba = new IBAHelper();

            datas.add(o.getNumber());

            datas.add(tranfString(iba.getIBAStringValue(o, "CINDEX")));// 图号

            datas.add(o.getName());

            datas.add("");

            datas.add(tranfString(iba.getIBAStringValue(o, "MINDEX")));

            datas.add(tranfString(iba.getIBAStringValue(o, "DESIGNER")));

            datas.add(tranfString(iba.getIBAStringValue(o, "COMPANY")));// 设计单位

        } else if (obj instanceof WTChangeOrder2) {

            WTChangeOrder2 o = (WTChangeOrder2) obj;

            IBAHelper iba = new IBAHelper();

            datas.add(o.getNumber());

            datas.add(tranfString(iba.getIBAStringValue(o, "CINDEX")));// 图号

            datas.add(o.getName());

            datas.add(o.getVersionInfo().getIdentifier().getValue() + "."

                    + o.getIterationInfo().getIdentifier().getValue());

            datas.add(tranfString(iba.getIBAStringValue(o, "MINDEX")));

            datas.add(tranfString(iba.getIBAStringValue(o, "DESIGNER")));

            datas.add(tranfString(iba.getIBAStringValue(o, "COMPANY")));// 设计单位

        }

        dataList.add(datas);



    }



	public  static String downloaDocAttachmentForPrint(WTObject obj)throws IOException, WTException, PropertyVetoException
	{
		//int haveAttachDocFlag = 0;
		try
		{
			SessionServerHelper.manager.setAccessEnforced(false);
			String tempDir = WTProperties.getLocalProperties().getProperty("wt.temp");
			String fileName = "";
            if (obj instanceof WTDocument)
            {
                /*QueryResult qr = VersionControlHelper.service.allIterationsOf(((WTDocument) obj).getMaster());
                if (qr.hasMoreElements())
                {
                    obj = (WTDocument) qr.nextElement();
                }
                fileName = ((WTDocument) obj).getName() + ".zip";*/
            	WTDocument doc =(WTDocument) obj;
            	fileName = doc.getName()+"_"+doc.getVersionInfo().getIdentifier().getValue()+".zip";
            }

            if (obj instanceof WTChangeOrder2)
            {
                QueryResult qr = VersionControlHelper.service.allIterationsOf(((WTChangeOrder2) obj).getMaster());
                if (qr.hasMoreElements())
                {
                    obj = (WTChangeOrder2) qr.nextElement();
                }
                fileName = ((WTChangeOrder2) obj).getName() + ".zip";
            }

            if (fileName.equals(""))
            {
                fileName = "obj.zip";
            }
            fileName = fileName.replaceAll("/", "_");
			ContentHolder contentholder = ContentHelper.service.getContents((ContentHolder) obj);
			Vector v = ContentHelper.getContentList(contentholder);
			File zipFile = new File(tempDir + File.separator + fileName);
			ZipOutputStream zos = new ZipOutputStream(zipFile);
			for (Object o : v)
			{
				if (o instanceof ApplicationData)
				{
					ApplicationData ad = (ApplicationData) o;
					 // 应用数据的角色
                    String applicationdataRole = ad.getRole().toString();
                    if (!applicationdataRole.equalsIgnoreCase("SECONDARY"))
                    {
                        continue;// 不是附件，处理下一个
                    }
                    if (obj instanceof WTDocument)
                    {
                    	WTDocument doc = (WTDocument)obj;

                    	 String primaryfileName = FilePrintUtil2.getPrimaryFileName((FormatContentHolder) obj);
                         String printFileName = FilePrintUtil2.getQualityFileName((FormatContentHolder) obj, primaryfileName, PRINTKEY);
                         boolean isDwg = printFileName.toUpperCase().endsWith(".DWG");
                         printFileName = Util.removeExtension(printFileName) + ".pdf";

                		 if(!ad.getFileName().equals(printFileName))
                		 {
                			 continue;
                		 }
                    }
                    if(ad.getFileName().startsWith("Print_"))
                    {
						InputStream is = ContentServerHelper.service.findContentStream(ad);
						byte[] buf = new byte[20480];
						zos.putNextEntry(new ZipEntry(ad.getFileName()));
						zos.setEncoding("gbk");
						int len = 0;
						while ((len = is.read(buf)) >= 0)
						{
							zos.write(buf, 0, len);
							//haveAttachDocFlag = 1;
						}
						is.close();
					}
				}
			}
			zos.close();
			return tempDir + File.separator + fileName;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			SessionServerHelper.manager.setAccessEnforced(true);
		}

		return "";

	}



    //获取主文件的打印附件并打包放到临时路径下
    public  static String getDownLoadPrintFileName(List<WTDocument> docList,String zipFileName) throws Exception
    {
        WTPrincipal currentuser = null;
        String tempPath = null;
        File zipFile = null;
        ZipOutputStream zos = null;
        String fileNameTemp = "";
        try
        {
	        WTPrincipal admin = SessionHelper.manager.getAdministrator();
	        currentuser = SessionContext.setEffectivePrincipal(admin);
	        tempPath = WTProperties.getLocalProperties().getProperty("wt.temp");
	        System.out.println("打印附件临时路径是："+tempPath);
	        zipFile = new File(tempPath + File.separator + zipFileName + ".zip");
	        zos = new ZipOutputStream(zipFile);
	        zos.setEncoding("GBK");
	        for(int index= 0;index < docList.size();index++)
	        {
	        	fileNameTemp = downloaDocAttachmentForPrint(docList.get(index));
	        	System.out.println("打印文件是fileNameTemp=="+fileNameTemp);
	        	if(null != fileNameTemp && !"".equals(fileNameTemp))
	        	{
	        		File f = new File(fileNameTemp);
		        	InputStream is = new BufferedInputStream(new FileInputStream(f));
		        	byte[] buf = new byte[20480];
		        	if(null != is)
		        	{
		        		zos.putNextEntry(new ZipEntry(f.getName()));
	                    zos.setEncoding("GBK");
	                    int len = 0;
	                    while ((len = is.read(buf)) >= 0)
	                    {
	                        zos.write(buf, 0, len);
	                    }
	                    is.close();
		        	}
		        	f.deleteOnExit();
	        	}


	        }
	        zos.close();
        }
        catch (WTException e)
        {
        	System.out.println("getDownLoadPrintFileName 问题1：");
            e.printStackTrace();
        }
        catch (IOException e)
        {
        	System.out.println("getDownLoadPrintFileName 问题2：");
            e.printStackTrace();
        }
        finally
        {

            SessionContext.setEffectivePrincipal(currentuser);

        }

        return tempPath + File.separator + zipFileName + ".zip";
    }


	public  String  getAttachFileNameForDownload(List<String> oidList,String zipFileName) throws Exception
	{
        WTPrincipal currentuser = null;
        String tempPath = null;
        ZipOutputStream zos = null;
        InputStream is = null;
        File zipFile = null;
        Object obj = null;
        ApplicationData ad = null;
        WTDocument wtdoc = null;
        EPMDocument epmdoc = null;
        String containername = null, adName = null;
        Iterator itOid = null;
        ReferenceFactory rf = new ReferenceFactory();

        try {

            WTPrincipal admin = SessionHelper.manager.getAdministrator();
            currentuser = SessionContext.setEffectivePrincipal(admin);
            tempPath = WTProperties.getLocalProperties().getProperty("wt.temp");
            zipFile = new File(tempPath + File.separator + zipFileName + ".zip");
            zos = new ZipOutputStream(zipFile);
            zos.setEncoding("GBK");
            byte[] buf = new byte[1024];
            itOid = oidList.iterator();
            ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
            ArrayList<String> titleList = new ArrayList<String>();
            titleList.add("编号");
            titleList.add("图号");// CINDEX
            titleList.add("名称");
            titleList.add("版本");
            titleList.add("所属型号");// MINDEX
            titleList.add("设计者");// DESIGNER
            titleList.add("设计单位");// COMPANY

            while (itOid.hasNext())
            {
                obj = rf.getReference(itOid.next().toString()).getObject();
                genDownloadDataExcelData(dataList, obj);
                ArrayList<String> datas = new ArrayList<String>();
                if (obj instanceof WTDocument)
                {

                    ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);
                    wtdoc = (WTDocument) holder;
                    containername = wtdoc.getNumber() + "_" + wtdoc.getName() + File.separator;
                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
                    Vector vec = new Vector();
                    while (qr.hasMoreElements())
                    {
                        Object objQr = qr.nextElement();
                        if (objQr instanceof ApplicationData)
                        {
                            ad = (ApplicationData) objQr;
                            adName = ad.getFileName();
                            if (adName.toUpperCase().endsWith(".DWG"))
                            {
                                vec.clear();
                                vec.add(ad);
                                break;
                            }
                            else
                            {
                                vec.add(ad);
                            }
                        }
                    }

                    for (int j = 0; j < vec.size(); j++)
                    {
                        ad = (ApplicationData) vec.get(j);
                        is = ContentServerHelper.service.findContentStream(ad);
                        if (is != null)
                        {
                            zos.putNextEntry(new ZipEntry(containername + ad.getFileName()));
                            zos.setEncoding("GBK");
                            int len = 0;
                            while ((len = is.read(buf)) >= 0)
                            {
                                zos.write(buf, 0, len);
                            }
                            is.close();
                        }
                    }

                    //把附件也下载
                    String fileNameTemp = ext.casc.fileprint.FilePrintUtil.downloadAttachmentForPrint((WTObject)obj);
                    if(null != fileNameTemp && !"".equals(fileNameTemp))
                    {
	    				File f = new File(fileNameTemp);
	    				InputStream inputStream = new BufferedInputStream(new FileInputStream(f));
	                    zos.putNextEntry(new ZipEntry(fileNameTemp));
	                    zos.setEncoding("GBK");
	                    int lenth = 0;
	                    while((lenth = inputStream.read(buf, 0, lenth)) >= 0)
	                    {
	                    	zos.write(buf, 0, lenth);
	                    }
	                    f.delete();
	                    inputStream.close();
                    }


                }
                else if (obj instanceof EPMDocument)
                {
                	/*
                    ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);

                    epmdoc = (EPMDocument) holder;

                    containername = epmdoc.getNumber() + "_" + epmdoc.getName() + File.separator;

                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);

                    while (qr.hasMoreElements()) {

                        Object objQr = qr.nextElement();

                        if (objQr instanceof ApplicationData) {

                            ad = (ApplicationData) objQr;

                            adName = ad.getFileName();

                            if(isElectronicFile(adName)){

                                is = ContentServerHelper.service.findContentStream(ad);

                                if (is != null) {

                                    zos.putNextEntry(new ZipEntry(containername + ad.getFileName()));

                                    zos.setEncoding("GBK");

                                    int len = 0;

                                    while ((len = is.read(buf)) >= 0) {

                                        zos.write(buf, 0, len);

                                    }

                                    is.close();

                                }

                            }

                        }

                    }*/



                }
                else if (obj instanceof ProcessEnvelope)
                {
                	/*
                    ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);
                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
                    while (qr.hasMoreElements()) {

                        Object objQr = qr.nextElement();

                        if (objQr instanceof ApplicationData) {

                            ad = (ApplicationData) objQr;

                            adName = ad.getFileName();

                            if (isElectronicFile(adName)) {

                                is = ContentServerHelper.service.findContentStream(ad);

                                if (is != null) {

                                    zos.putNextEntry(new ZipEntry(ad.getFileName()));

                                    zos.setEncoding("GBK");

                                    int len = 0;

                                    while ((len = is.read(buf)) >= 0) {

                                        zos.write(buf, 0, len);

                                    }

                                    is.close();

                                }

                            }

                        }

                    }*/

                }
                else if (obj instanceof ChangePackaged)
                {
                	/*
                    ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);

                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);

                    while (qr.hasMoreElements()) {

                        Object objQr = qr.nextElement();

                        if (objQr instanceof ApplicationData) {

                            ad = (ApplicationData) objQr;

                            adName = ad.getFileName();

                            if (isElectronicFile(adName)) {

                                is = ContentServerHelper.service.findContentStream(ad);

                                if (is != null) {

                                    zos.putNextEntry(new ZipEntry(ad.getFileName()));

                                    zos.setEncoding("GBK");

                                    int len = 0;

                                    while ((len = is.read(buf)) >= 0) {

                                        zos.write(buf, 0, len);

                                    }

                                    is.close();

                                }

                            }

                        }

                    }*/

                }
                else if (obj instanceof WTChangeOrder2)
                {
                	/*
                    ContentHolder holder = ContentHelper.service.getContents((ContentHolder) obj);
                    QueryResult qr = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
                    while (qr.hasMoreElements())
                    {
                        Object objQr = qr.nextElement();
                        if (objQr instanceof ApplicationData)
                        {
                            ad = (ApplicationData) objQr;
                            adName = ad.getFileName();
                            if (isElectronicFile(adName))
                            {
                                is = ContentServerHelper.service.findContentStream(ad);
                                if (is != null)
                                {
                                    zos.putNextEntry(new ZipEntry(ad.getFileName()));
                                    zos.setEncoding("GBK");
                                    int len = 0;
                                    while ((len = is.read(buf)) >= 0)
                                    {
                                        zos.write(buf, 0, len);
                                    }
                                    is.close();

                                }

                            }

                        }

                    }*/

                }

            }



            ExcelFileGenerator generator = new ExcelFileGenerator(titleList, dataList);

            File excelFile = new File(tempPath + File.separator + "812厂打印下载数据包清单.xls");

            if (!excelFile.exists()) {

                excelFile.createNewFile();

            }

            try {

                generator.expordExcel(new FileOutputStream(excelFile));

            } catch (Exception e) {

                e.printStackTrace();

            }

            is = new FileInputStream(excelFile);

            if (is != null) {

                zos.putNextEntry(new ZipEntry("812厂打印下载数据包清单.xls"));

                zos.setEncoding("GBK");

                int len = 0;

                while ((len = is.read(buf)) >= 0) {

                    zos.write(buf, 0, len);

                }

                is.close();

            }



            zos.close();

        } catch (WTException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch (PropertyVetoException e) {

            e.printStackTrace();

        } finally {

            SessionContext.setEffectivePrincipal(currentuser);

        }

        return tempPath + File.separator + zipFileName + ".zip";






	}

    private boolean isElectronicFile(String adName) {

//      if (adName.startsWith("Print_") || adName.startsWith("print_")) {

  	if(adName.toUpperCase().endsWith(".PDF")){

          return true;

      } else {

          return false;

      }

  }

	public static List<String> getShouKongDocOidList(ArrayList list) throws Exception
	{
		if(!RemoteMethodServer.ServerFlag)
		{
			String method = "getShouKongDocOidList";
			Class[] types = {ArrayList.class};
			Object[] vals = {list};

			try
			{
				RemoteMethodServer rms = RemoteMethodServer.getDefault();
				rms.invoke(method, SearchUtil.class.getName(), null, types, vals);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}

		ArrayList<String> oidList = new ArrayList<String> ();
		if(null == list )
		{
			return null;
		}

		for(int i = 0;i < list.size();i++)
		{
			NmContext nmContent = (NmContext) list.get(i);
			String oid = nmContent.toString();
			oid = oid.substring(oid.indexOf("VR:"), oid.length()-2);
			Persistable obj = getObject(oid);
			if(null == obj)
			{
				continue;
			}
			if(obj instanceof WTDocument)
			{
				oidList.add(PersistenceHelper.getObjectIdentifier((WTObject) obj).getStringValue());
			}
		}

		return oidList;

	}


	public static ArrayList<WTDocument> getShouKongDocList(NmCommandBean bean) throws Exception
	{

		ArrayList list = bean.getSelectedContextsForPopup();
		ArrayList list2 = bean.getSelectedOidForPopup();

		ArrayList docList = new ArrayList<WTDocument>();

		if(null == list )
		{
			return null;
		}

		for(int i = 0;i < list.size();i++)
		{
			NmContext nmContent = (NmContext) list.get(i);
			String oid = nmContent.toString();
			oid = oid.substring(oid.indexOf("VR:"), oid.length()-2);
			Persistable obj = getObject(oid);
			if(null == obj)
			{
				continue;
			}
			if(obj instanceof WTDocument)
			{
				docList.add(obj);
			}
		}

		return docList;

	}

	public static List<WfInfo> getGZSQDSignData(ArrayList list) throws Exception
	{
		if(!RemoteMethodServer.ServerFlag)
		{
			String method = "getGZSQDSignData";
			Class[] types = {ArrayList.class};
			Object[] vals = {list};

			try
			{
				RemoteMethodServer rms = RemoteMethodServer.getDefault();
				rms.invoke(method, SearchUtil.class.getName(), null, types, vals);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		List<WfInfo> infoList = new ArrayList<WfInfo>();
		for(int i = 0;i < list.size();i++)
		{
			NmContext nmContent = (NmContext) list.get(i);
			String oid = nmContent.toString();
			oid = oid.substring(oid.indexOf("VR:"), oid.length()-2);
			ArrayList<HashMap<String,String>> processSign = new ArrayList<HashMap<String,String>>();
			Persistable obj = getObject(oid);
			if(null == obj)
			{
				continue;
			}
			if(obj instanceof WTDocument)
			{
				String gzsqdType = "";
				WTDocument doc = (WTDocument)obj;

				//获取该文档类型的内部名称
				gzsqdType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier((Typed) doc);
				if(gzsqdType.endsWith("GZ_SQD"))
				{
					WfInfo info = new WfInfo();

					//构建一个属性帮助器
					IBAUtil iba=new IBAUtil(doc);

					//设置编制部门的属性值，软属性需要构造一个IBAUtil对象来获取属性值
					info.setDepartment(iba.getIBAValue("BZBM")==null?"":iba.getIBAValue("BZBM"));

					//设置创建时间的属性值
					info.setCreattime(doc.getCreateTimestamp().toLocaleString());

					//设置创建者的属性值
					info.setCreater(doc.getCreatorFullName());

					//设置编号的属性值
					info.setNumber(doc.getNumber());

					//设置工装编号的属性值
					info.setGongzhuangbianhao(iba.getIBAValue("GZ_NUMBER")== null?"":iba.getIBAValue("GZ_NUMBER"));

					//设置名称的属性值
					info.setName(doc.getName());

					//设置上下文的名称
					info.setContainer(doc.getContainerName());

					//设置状态，需要首先获取生命周期状态 ，才能获取状态名称
					info.setStatus(doc.getLifeCycleState().getDisplay(Locale.CHINA));

					//设置修改时间
					info.setModifytime(doc.getModifyTimestamp().toLocaleString());

					//设置描述
					info.setDescription(doc.getDescription());

					//设置版本
					info.setVersions(doc.getVersionInfo().getIdentifier().getValue() + "."+doc.getIterationIdentifier().getValue());

					WfProcess wf = null;
					QueryResult qresult = WfEngineHelper.service.getAssociatedProcesses(obj, null, null);
					long tempTime = -1;
					WfProcess tempProcess = null;
					while(qresult.hasMoreElements())
					{
						tempProcess = (WfProcess)qresult.nextElement();
						long currentTime = tempProcess.getCreateTimestamp().getTime();
						if(currentTime > tempTime)
						{
							wf = tempProcess;
							tempTime = currentTime;
						}
					}

					if(wf != null)
					{
						processSign = getWfProcessRecordes(wf);
						long tempTimeZhurengongyishi = -1;
						long tempTimeSanshi = -1;
						long tempTimeDiaodu = -1;
						long tempTimeFuzonggongyishi = -1;
						long tempTimeZonggongyishi = -1;
						long tempTimeGongzhuanguanliyuan = -1;
						long tempTimeDayin = -1;
						for(int a = 0;a < processSign.size();a++)
						{
							HashMap<String, String> map  = processSign.get(a);
							System.out.println("actName===>>"+map.values());
							if(map.get("actName")!=null&&map.get("actName").contains("主任工艺师审核"))
							{
								if(map.get("actTime")!=null&&!"".equals(map.get("actTime")))
								{
									if(Long.parseLong(map.get("actTime"))>tempTimeZhurengongyishi)
									{
										tempTimeZhurengongyishi=Long.parseLong(map.get("actTime"));
										info.setZhurengongyishi(map.get("actUserName"));
									}
								}
							}
							else if(map.get("actName")!=null&&map.get("actName").contains("三室审核"))
							{
								if(map.get("actTime")!=null&&!"".equals(map.get("actTime")))
								{
									if(Long.parseLong(map.get("actTime"))>tempTimeSanshi)
									{
										tempTimeSanshi=Long.parseLong(map.get("actTime"));
										info.setSanshi(map.get("actUserName"));
									}
								}
							}
							else if(map.get("actName")!=null&&map.get("actName").contains("科技处审核"))
							{
								if(map.get("actTime")!=null&&!"".equals(map.get("actTime")))
								{
									if(Long.parseLong(map.get("actTime"))>tempTimeDiaodu)
									{
										tempTimeDiaodu=Long.parseLong(map.get("actTime"));
										info.setXinghaodiaodu(map.get("actUserName"));
									}
								}
							}
							else if(map.get("actName")!=null&&map.get("actName").contains("(副)总工艺师审核"))
							{
								if(map.get("actTime")!=null&&!"".equals(map.get("actTime")))
								{
									if(Long.parseLong(map.get("actTime"))>tempTimeFuzonggongyishi)
									{
										tempTimeFuzonggongyishi=Long.parseLong(map.get("actTime"));
										info.setFuzonggongyishi(map.get("actUserName"));
									}
								}
							}
							else if(map.get("actName")!=null&&map.get("actName").contains("总工艺师审核"))
							{
								if(map.get("actTime")!=null&&!"".equals(map.get("actTime")))
								{
									if(Long.parseLong(map.get("actTime"))>tempTimeZonggongyishi)
									{
										tempTimeZonggongyishi=Long.parseLong(map.get("actTime"));
										info.setZonggongyishi(map.get("actUserName"));
									}
								}
							}
							else if(map.get("actName")!=null&&map.get("actName").contains("工装管理员审核"))
							{
								if(map.get("actTime")!=null&&!"".equals(map.get("actTime")))
								{
									if(Long.parseLong(map.get("actTime"))>tempTimeGongzhuanguanliyuan)
									{
										tempTimeGongzhuanguanliyuan=Long.parseLong(map.get("actTime"));
										info.setGongzhuangguanliyuan(map.get("actUserName"));
									}
								}
							}
							else if(map.get("actName")!=null&&map.get("actName").contains("工艺文件下载打印"))
							{
								if(map.get("actTime")!=null&&!"".equals(map.get("actTime")))
								{
									if(Long.parseLong(map.get("actTime"))>tempTimeDayin)
									{
										tempTimeDayin=Long.parseLong(map.get("actTime"));
										info.setDayin(map.get("actUserName"));
									}
								}
							}

						}

					}
					else
					{
						continue;
					}

					infoList.add(info);
				}

			}

		}

		return infoList;
	}



	public static List<WfInfo> getSignData(ArrayList list) throws Exception
	{
		if (!RemoteMethodServer.ServerFlag)
		{
            String method = "getSignData";
            Class[] types = { ArrayList.class };
            Object[] vals = { list};
            try
            {
                RemoteMethodServer rms = RemoteMethodServer.getDefault();
                rms.invoke(method, SearchUtil.class.getName(), null, types, vals);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
		List<WfInfo> infoList=new ArrayList<WfInfo>();
		for (int i = 0; i < list.size(); i++)
		{
			NmContext nmContext = (NmContext) list.get(i);
			String oid=nmContext.toString();
			oid=oid.substring(oid.indexOf("VR:"),oid.length()-2);
			ArrayList<HashMap<String, String>> processSign = new ArrayList<HashMap<String, String>>();
			Persistable obj = getObject(oid);
			if(obj==null)
			{
				continue;
			}
			if(obj instanceof WTPart||obj instanceof WTDocument||obj instanceof EPMDocument||obj instanceof ChangeOrder2)
			{

				WfInfo info=new WfInfo();
				String cadType="";
				if(obj instanceof WTDocument)
				{
					WTDocument doc=(WTDocument) obj;

					//获取该文档类型的内部名称
					cadType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier((Typed) doc);

					//构建一个属性帮助器
					IBAUtil iba=new IBAUtil(doc);

					//设置编制部门的属性值，软属性需要构造一个IBAUtil对象来获取属性值
					info.setDepartment(iba.getIBAValue("BZBM")==null?"":iba.getIBAValue("BZBM"));

					//设置创建时间的属性值
					info.setCreattime(doc.getCreateTimestamp().toLocaleString());

					//设置创建者的属性值
					info.setCreater(doc.getCreatorFullName());

					//设置编号的属性值
					info.setNumber(doc.getNumber());



					//设置名称的属性值
					info.setName(doc.getName());

					//设置上下文的名称
					info.setContainer(doc.getContainerName());

					//设置状态，需要首先获取生命周期状态 ，才能获取状态名称
					info.setStatus(doc.getLifeCycleState().getDisplay(Locale.CHINA));

					//设置修改时间
					info.setModifytime(doc.getModifyTimestamp().toLocaleString());

					//设置描述
					info.setDescription(doc.getDescription());

					//设置版本
					info.setVersions(doc.getVersionInfo().getIdentifier().getValue() + "."+doc.getIterationIdentifier().getValue());
				}else if(obj instanceof WTPart)
				{
					WTPart part=(WTPart) obj;
					IBAUtil iba=new IBAUtil(part);
					info.setDepartment(iba.getIBAValue("BZBM")==null?"":iba.getIBAValue("BZBM"));
					info.setCreattime(part.getCreateTimestamp().toLocaleString());
					info.setCreater(part.getCreatorFullName());
					info.setNumber(part.getNumber());
					info.setName(part.getName());
					info.setContainer(part.getContainerName());
					info.setStatus(part.getLifeCycleState().getDisplay(Locale.CHINA));
					info.setModifytime(part.getModifyTimestamp().toLocaleString());
					info.setDescription("");
					info.setVersions(part.getVersionInfo().getIdentifier().getValue() + "."+part.getIterationIdentifier().getValue());
				}else if(obj instanceof EPMDocument){
					EPMDocument empDoc=(EPMDocument) obj;
					IBAUtil iba=new IBAUtil(empDoc);
					info.setDepartment(iba.getIBAValue("BZBM")==null?"":iba.getIBAValue("BZBM"));
					//info.setAuthorApplication(empDoc.getAuthoringApplication().getDisplay());
					info.setCreattime(empDoc.getCreateTimestamp().toLocaleString());
					info.setCreater(empDoc.getCreatorFullName());
					info.setNumber(empDoc.getNumber());
					info.setName(empDoc.getName());
					info.setContainer(empDoc.getContainerName());
					info.setStatus(empDoc.getLifeCycleState().getDisplay(Locale.CHINA));
					info.setModifytime(empDoc.getModifyTimestamp().toLocaleString());
					info.setDescription("");
					info.setVersions(empDoc.getVersionInfo().getIdentifier().getValue() + "."+empDoc.getIterationIdentifier().getValue());
				}else if(obj instanceof ChangeOrder2){
					ChangeOrder2 co2=(ChangeOrder2) obj;
					info.setCreattime(co2.getCreateTimestamp().toLocaleString());
					info.setCreater(co2.getCreatorFullName());
					info.setNumber(co2.getNumber());
					info.setName(co2.getName());
					info.setContainer(co2.getContainerName());
					info.setStatus(co2.getLifeCycleState().getDisplay(Locale.CHINA));
					info.setModifytime(co2.getModifyTimestamp().toLocaleString());
					info.setDescription("");
					info.setVersions(co2.getVersionInfo().getIdentifier().getValue() + "."+co2.getIterationIdentifier().getValue());
				}
				WfProcess wf = null;
				if (!"".equals(cadType) && cadType.endsWith("CAD_DOC"))
				{
					//用CAD_DOC这种类型的对象去查询关联的 批量签审包的Link集合
					QueryResult qr=PersistenceHelper.manager.navigate(obj, "theProcessEnvelope", EnvelopeMemberLink.class,false);
					while(qr.hasMoreElements())
					{
						//获取一个Link
						EnvelopeMemberLink link=(EnvelopeMemberLink)qr.nextElement();

						//通过Link获取包对象
						ProcessEnvelope pe = link.getProcessEnvelope();

						//通过包获取流程的集合
						QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(pe, null, null);

						while (qrProcs.hasMoreElements())
						{
							wf = (WfProcess) qrProcs.nextElement();
						}
					}
				}
				else
				{
					QueryResult qrProcs = WfEngineHelper.service
							.getAssociatedProcesses(obj, null, null);

					while (qrProcs.hasMoreElements()) {
						wf = (WfProcess) qrProcs.nextElement();
					}
				}
					if (wf != null) {
						processSign = getWfProcessRecordes(wf);
					}else{
						continue;
					}
					long tempJiaodui=-1;
					long tempShenhe=-1;
					long tempNeibu=-1;
					long tempWaibu=-1;
					long tempBiaoshen=-1;
					long tempPizhun=-1;
					long tempXinghao=-1;
					long tempDayin=-1;
					String neibuhuiqian="";
					List<String> neibuList=new ArrayList<String>();
					for(int b=0;b<processSign.size();b++)
					{
						HashMap<String, String> map  = processSign.get(b);
						if(map.get("actName")!=null&&map.get("actName").contains("内部会签"))
						{
							if(!neibuList.contains(map.get("actUserName")))
							{
							neibuhuiqian+=map.get("actUserName")+",";
							neibuList.add(map.get("actUserName"));
							}
						}
					}
					if(neibuhuiqian.endsWith(",")){
						neibuhuiqian=neibuhuiqian.substring(0,neibuhuiqian.length()-1);
					}

				for (int a = 0; a < processSign.size(); a++)
				{
					HashMap<String, String> map  = processSign.get(a);
					System.out.println("actName===>>"+map.values());
					if(map.get("actName")!=null&&map.get("actName").contains("校对"))
					{
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime")))
						{
							if(Long.parseLong(map.get("actTime"))>tempJiaodui)
							{
								tempJiaodui=Long.parseLong(map.get("actTime"));
								info.setJiaodui(map.get("actUserName"));
							}
						}
					}
					else if(map.get("actName")!=null&&map.get("actName").contains("审核"))
					{
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime")))
						{
							if(Long.parseLong(map.get("actTime"))>tempShenhe)
							{
								tempShenhe=Long.parseLong(map.get("actTime"));
								info.setShenhe(map.get("actUserName"));
							}
						}
//						info.setShenhe(map.get("actUserName"));
					}else if(map.get("actName")!=null&&map.get("actName").contains("内部会签")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempNeibu){
								tempNeibu=Long.parseLong(map.get("actTime"));
								info.setNeibuhuiqian(neibuhuiqian);
							}
						}
//						info.setNeibuhuiqian(map.get("actUserName"));
					}else if(map.get("actName")!=null&&map.get("actName").contains("外部会签")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempWaibu){
								tempWaibu=Long.parseLong(map.get("actTime"));
								info.setWaibuhuiqian(map.get("waibuSign"));
							}
						}
//						info.setWaibuhuiqian(map.get("actUserName"));
					}else if(map.get("actName")!=null&&map.get("actName").contains("标审")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempBiaoshen){
								tempBiaoshen=Long.parseLong(map.get("actTime"));
								info.setBiaoshen(map.get("actUserName"));
							}
						}
//						info.setBiaoshen(map.get("actUserName"));
					}else if(map.get("actName")!=null&&map.get("actName").contains("批准")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempPizhun){
								tempPizhun=Long.parseLong(map.get("actTime"));
								info.setPizhun(map.get("actUserName"));
							}
						}
//						info.setPizhun(map.get("actUserName"));
					}else if(map.get("actName")!=null&&(map.get("actName").contains("型号调度")||map.get("actName").contains("设置"))){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempXinghao){
								tempXinghao=Long.parseLong(map.get("actTime"));
								info.setXinghaodiaodu(map.get("actUserName"));
							}
						}
//						info.setXinghaodiaodu(map.get("actUserName"));
					}else if(map.get("actName")!=null&&map.get("actName").contains("打印")){
						if(map.get("actTime")!=null&&!"".equals(map.get("actTime"))){
							if(Long.parseLong(map.get("actTime"))>tempDayin){
								tempDayin=Long.parseLong(map.get("actTime"));
								info.setDayin(map.get("actUserName"));
							}
						}
//						info.setDayin(map.get("actUserName"));
					}
				}
				infoList.add(info);
			}
		}
		return infoList;
	}

	public static Persistable getObject(String oid) throws Exception{
			Persistable obj=null;
			ReferenceFactory rf=new ReferenceFactory();
			obj=rf.getReference(oid).getObject();
			return obj;

	}

	public static String dowloadGZSQDInfoFile(List<WfInfo> list) throws Exception
	{
		OutputStream os=null;
		Workbook wb = null;
		wb = new HSSFWorkbook();
		HSSFSheet sheet = (HSSFSheet) wb.createSheet(); // 获得第一个表单
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 6000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 4000);
		sheet.setColumnWidth(8, 6000);
		sheet.setColumnWidth(9, 4000);
		sheet.setColumnWidth(10, 4000);
		sheet.setColumnWidth(11, 4000);
		sheet.setColumnWidth(12, 4000);
		sheet.setColumnWidth(13, 4000);
		sheet.setColumnWidth(14, 4000);
		sheet.setColumnWidth(15, 4000);
		sheet.setColumnWidth(16, 4000);
		sheet.setColumnWidth(17, 4000);
		sheet=setCellValue(sheet,0,0,"编号");
		sheet=setCellValue(sheet,0,1,"名称");
		sheet=setCellValue(sheet,0,2,"工装编号");

		sheet=setCellValue(sheet,0,3,"上下文");
		sheet=setCellValue(sheet,0,4,"编制部门");
		sheet=setCellValue(sheet,0,5,"创建者");

		sheet=setCellValue(sheet,0,6,"型号主任工艺师");

		sheet=setCellValue(sheet,0,7,"三室");
		sheet=setCellValue(sheet,0,8,"型号调度");

		sheet=setCellValue(sheet,0,9,"副总工艺师");

		sheet=setCellValue(sheet,0,10,"总工艺师");

		sheet=setCellValue(sheet,0,11,"工装管理员");

		sheet=setCellValue(sheet,0,12,"打印");
		sheet=setCellValue(sheet,0,13,"版本");
		sheet=setCellValue(sheet,0,14,"状态");
		sheet=setCellValue(sheet,0,15,"创建时间");
		sheet=setCellValue(sheet,0,16,"修改时间");
		sheet=setCellValue(sheet,0,17,"说明");
		for(int i=0;i<list.size();i++){
			WfInfo info=list.get(i);
			sheet=setCellValue(sheet,i+1,0,info.getNumber());
			sheet=setCellValue(sheet,i+1,1,info.getName());
			sheet=setCellValue(sheet,i+1,2,info.getGongzhuangbianhao());
			sheet=setCellValue(sheet,i+1,3,info.getContainer());

			sheet=setCellValue(sheet,i+1,4,info.getDepartment());
			sheet=setCellValue(sheet,i+1,5,info.getCreater());
			sheet=setCellValue(sheet,i+1,6,info.getZhurengongyishi());
			sheet=setCellValue(sheet,i+1,7,info.getSanshi());

			sheet=setCellValue(sheet,i+1,8,info.getXinghaodiaodu());

			sheet=setCellValue(sheet,i+1,9,info.getFuzonggongyishi());

			sheet=setCellValue(sheet,i+1,10,info.getZonggongyishi());

			sheet=setCellValue(sheet,i+1,11,info.getGongzhuangguanliyuan());


			sheet=setCellValue(sheet,i+1,12,info.getDayin());
			sheet=setCellValue(sheet,i+1,13,info.getVersions());
			sheet=setCellValue(sheet,i+1,14,info.getStatus());
			sheet=setCellValue(sheet,i+1,15,info.getCreattime());
			sheet=setCellValue(sheet,i+1,16,info.getModifytime());
			sheet=setCellValue(sheet,i+1,17,info.getDescription());
		}
		os=new FileOutputStream(tempCache);
		wb.write(os);
        os.close();
        return tempCache;
	}

	public static String downloadInfoFile(List<WfInfo> list) throws Exception{
//		WTProperties wtProp =WTProperties.getLocalProperties();
//		String wt_home=wtProp.getProperty("wt.home");
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.HOUR_OF_DAY, 8);
//		Date date = calendar.getTime();
//		String userName=SessionHelper.getPrincipal().getName();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//		String tempPath=wt_home+File.separator+"temp"+File.separator+"IXBExpImp"+File.separator+userName+"-"+sdf.format(date)+".xls";
		//String after=wt_home+File.separator+"template"+File.separator+"audit"+File.separator+"data"+File.separator+"安全审计日志.xls";
		//InputStream input =new FileInputStream(before);
		OutputStream os=null;
		Workbook wb = null;
		wb = new HSSFWorkbook();
		HSSFSheet sheet = (HSSFSheet) wb.createSheet(); // 获得第一个表单
		sheet.setColumnWidth(0, 6000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 6000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 4000);
		sheet.setColumnWidth(8, 6000);
		sheet.setColumnWidth(9, 4000);
		sheet.setColumnWidth(10, 4000);
		sheet.setColumnWidth(11, 4000);
		sheet.setColumnWidth(12, 4000);
		sheet.setColumnWidth(13, 4000);
		sheet.setColumnWidth(14, 4000);
		sheet.setColumnWidth(15, 4000);
		sheet.setColumnWidth(16, 4000);
		sheet.setColumnWidth(17, 4000);
		sheet=setCellValue(sheet,0,0,"编号");
		sheet=setCellValue(sheet,0,1,"名称");
		sheet=setCellValue(sheet,0,2,"上下文");
		sheet=setCellValue(sheet,0,3,"编制部门");
		sheet=setCellValue(sheet,0,4,"创建者");
		sheet=setCellValue(sheet,0,5,"校对");
		sheet=setCellValue(sheet,0,6,"审核");
		sheet=setCellValue(sheet,0,7,"内部会签");
		sheet=setCellValue(sheet,0,8,"外部会签");
		sheet=setCellValue(sheet,0,9,"标审");
		sheet=setCellValue(sheet,0,10,"批准");
		sheet=setCellValue(sheet,0,11,"型号调度");
		sheet=setCellValue(sheet,0,12,"打印");
		sheet=setCellValue(sheet,0,13,"版本");
		sheet=setCellValue(sheet,0,14,"状态");
		sheet=setCellValue(sheet,0,15,"创建时间");
		sheet=setCellValue(sheet,0,16,"修改时间");
		sheet=setCellValue(sheet,0,17,"说明");
		for(int i=0;i<list.size();i++){
			WfInfo info=list.get(i);
			sheet=setCellValue(sheet,i+1,0,info.getNumber());
			sheet=setCellValue(sheet,i+1,1,info.getName());
			sheet=setCellValue(sheet,i+1,2,info.getContainer());
			sheet=setCellValue(sheet,i+1,3,info.getDepartment());
			sheet=setCellValue(sheet,i+1,4,info.getCreater());
			sheet=setCellValue(sheet,i+1,5,info.getJiaodui());
			sheet=setCellValue(sheet,i+1,6,info.getShenhe());
			sheet=setCellValue(sheet,i+1,7,info.getNeibuhuiqian());
			sheet=setCellValue(sheet,i+1,8,info.getWaibuhuiqian());
			sheet=setCellValue(sheet,i+1,9,info.getBiaoshen());
			sheet=setCellValue(sheet,i+1,10,info.getPizhun());
			sheet=setCellValue(sheet,i+1,11,info.getXinghaodiaodu());
			sheet=setCellValue(sheet,i+1,12,info.getDayin());
			sheet=setCellValue(sheet,i+1,13,info.getVersions());
			sheet=setCellValue(sheet,i+1,14,info.getStatus());
			sheet=setCellValue(sheet,i+1,15,info.getCreattime());
			sheet=setCellValue(sheet,i+1,16,info.getModifytime());
			sheet=setCellValue(sheet,i+1,17,info.getDescription());
		}
		os=new FileOutputStream(tempCache);
		wb.write(os);
        os.close();
        return tempCache;
	}

	 public static HSSFSheet setCellValue(HSSFSheet sheet, int iRow, int iCol, String val){
		 HSSFRow row = sheet.getRow(iRow);
		 if(null==row){
			 row=sheet.createRow(iRow);
		 }
         HSSFCell cell=row.createCell(iCol);
         cell.setCellValue(val);
         return sheet;
	 }


//	public static Persistable getObject(String number) throws Exception{
//		Persistable obj=null;
//		QuerySpec qs=null;
//		qs=new QuerySpec(WTDocumentMaster.class);
//		SearchCondition sc=new SearchCondition(WTDocumentMaster.class,WTDocumentMaster.NUMBER,"=",number);
//		qs.appendWhere(sc);
//		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
//		if(qr.hasMoreElements()){
//			WTDocumentMaster docMaster=(WTDocumentMaster) qr.nextElement();
//			QueryResult queryResult=VersionControlHelper.service.allIterationsOf(docMaster);
//			if(queryResult.hasMoreElements()){
//				obj=(Persistable) queryResult.nextElement();
//				return obj;
//			}
//		}
//
//		qs=new QuerySpec(WTPartMaster.class);
//		sc=new SearchCondition(WTPartMaster.class,WTPartMaster.NUMBER,"=",number);
//		qs.appendWhere(sc);
//		qr = PersistenceHelper.manager.find((StatementSpec) qs);
//		if(qr.hasMoreElements()){
//			WTPartMaster partMaster=(WTPartMaster) qr.nextElement();
//			QueryResult queryResult=VersionControlHelper.service.allIterationsOf(partMaster);
//			if(queryResult.hasMoreElements()){
//				obj=(Persistable) queryResult.nextElement();
//				return obj;
//			}
//		}
//		return obj;
//	}


}
