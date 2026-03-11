package ext.casc.workflow;

import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.util.MPMProcessPlanUtil;
import com.ptc.core.meta.common.impl.TypeIdentifierUtilityHelper;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanHelper;
import ext.ases.envelope.EnvelopeHelper;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.doc.CSCDoc;
import ext.casc.fileprint.FilePrintUtil;
import ext.casc.number.ASESNumberUtil;
import ext.casc.part.SignatureHelper;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import ext.casc.workflow.signtrue.zp.SignatureGYZZXMLParser;
import ext.casc.workflow.signtrue.zp.SignatureRecord;
import ext.casc.workflow.signtrue.zp.SignatureService;
import ext.csc.utilities.principal.CSCPrincipal;
import org.apache.log4j.Logger;
import wt.associativity.NCServerHolder;
import wt.change2.ChangeException2;
import wt.change2.ChangeHelper2;
import wt.change2.Changeable2;
import wt.change2.WTChangeOrder2;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.doc.WTDocumentDependencyLink;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.fc.collections.WTCollection;
import wt.inf.container.WTContained;
import wt.log4j.LogR;
import wt.method.RemoteAccess;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.*;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTStandardDateFormat;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.workflow.engine.*;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfBallot;
import wt.workflow.work.WorkItem;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class PrintHelper implements RemoteAccess{
	public static List<String> processTemps = new ArrayList<String>();
	public static List<String> hasWfBlockProcess = new ArrayList<String>();
    public static int PRINT_TUYANG = 1;
    public static int PRINT_DOCUMENT = 2;
    public static int PRINT_ECN = 3;
    public static int PRINT_JISHUTONGZHIDAN = 4;
    public static int PRINT_CHANGEPHASE = 5;

    private static final Logger log;
	private static final int PRINT_JISHUXIEYI = 6;
	private static final int PRINT_GONGYIJISHUTONGZHIDAN = 7;
	private static final int PRINT_WXJSXY = 8;
	private static final int PRINT_GYFA = 9;
	private static final int PRINT_GYZFA = 10;

    static {
        try {
        	processTemps.add(Constants.WFN_PROCESS_DOCUMENT);
        	processTemps.add(Constants.WFN_PROCESS_WAIXIEDOCUMENT);
        	processTemps.add(Constants.WFN_PROCESS_GONGYIFANGAN);
        	processTemps.add(Constants.WFN_PROCESS_TONGYONGDOCUMENT);
        	processTemps.add(Constants.WFN_PROCESS_GONGYIFANGANBAOGAO);
        	hasWfBlockProcess.add("零部件签审流程");
        	hasWfBlockProcess.add("ECN流程");
        	hasWfBlockProcess.add("文档签审流程");
            log = LogR.getLogger(PrintHelper.class.getName());
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Hashtable getPrintInfo(Object pbo, Object self) throws Exception {
        Hashtable printTable = new Hashtable();
        boolean isChangePhaseECN = false;
        if (pbo instanceof WTChangeOrder2) {
            String type = TypeIdentifierUtilityHelper.service.getTypeIdentifier(pbo).toString();
            if (type.indexOf("CHANGEPHASE_ECN") > -1) {
                isChangePhaseECN = true;
            }
        }

        Hashtable ht = null;
        Vector<Object> printPBOVector = getPrintObjVector(pbo);
        for (int i = 0; i < printPBOVector.size(); i++) {
            Object obj = printPBOVector.get(i);
            log.debug("*********obj:"+obj);
            boolean isPartReviewProcess = false;

            // 检查该流程是否为 零部件签审流程， 如果是 则需要对会签意见作特别处理
            if (pbo instanceof ProcessEnvelope) {
                String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(pbo).toString();
                if (objectType.indexOf("casc.sast.149.APPROVEFORM") > -1) {
                    isPartReviewProcess = true;
                }
            }

            // 检查该流程是否为 变更流程， 如果是 则需要对会签意见作特别处理
            String objectType ="";
            if (pbo instanceof WTChangeOrder2) {
                objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(pbo).toString();
                log.debug("------objectType:" + objectType);
                if (objectType.indexOf("CHANGE_ECN") > -1||objectType.indexOf("PROCESS_ECN") > -1) {
                    isPartReviewProcess = true;
                }
            }
            log.debug("------isPartReviewProcess:" + isPartReviewProcess);
            ht = getWFInfo2(obj, (WfProcess) self, isPartReviewProcess, isChangePhaseECN, pbo);

            String objOid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
            ReferenceFactory rf = new ReferenceFactory();
            Object clonePbo = rf.getReference(objOid).getObject();
            if (clonePbo instanceof WTDocument) {
                WTDocument wtd = (WTDocument) clonePbo;
                log.debug("clonePbo name is " + wtd.getName() + " clonePbo version is "
                        + wtd.getVersionIdentifier().getValue() + "." + wtd.getIterationIdentifier().getValue());
            }

            Set set = ht.keySet();
            Iterator iter = set.iterator();
            log.debug(" ======================= Object [" + objOid + "] Print Info=======================");
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String value = (String) ht.get(key);
                log.debug(key + " = " + value);
            }
            log.debug(" ======================= End =======================");
            printTable.put(objOid, ht);
        }

        // printInfoList.add(printPBOVector);
        // printInfoList.add(ht);

        return printTable;
    }

    public static Hashtable getPrintTempSign(Object pbo, Map<String, String> map) throws Exception {
        Hashtable printTable = new Hashtable();

        Hashtable ht = null;
        Vector<Object> printPBOVector = getPrintObjVector(pbo);
        for (int i = 0; i < printPBOVector.size(); i++) {
            Object obj = printPBOVector.get(i);
            log.debug("*********obj:"+obj);
            ht = getSignatureInfo(pbo, map);
            String objOid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
            ReferenceFactory rf = new ReferenceFactory();
            Object clonePbo = rf.getReference(objOid).getObject();
            if (clonePbo instanceof WTDocument) {
                WTDocument wtd = (WTDocument) clonePbo;
                log.debug("clonePbo name is " + wtd.getName() + " clonePbo version is "
                        + wtd.getVersionIdentifier().getValue() + "." + wtd.getIterationIdentifier().getValue());
            }

            Set set = ht.keySet();
            Iterator iter = set.iterator();
            log.debug(" ======================= Object [" + objOid + "] Print Info=======================");
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String value = (String) ht.get(key);
                log.debug(key + " = " + value);
            }
            log.debug(" ======================= End =======================");
            printTable.put(objOid, ht);
        }

        return printTable;
    }

    /**
     *自定义电子签名构造hashtable
    * @author zhuhao
    * @date 2018-10-29
    * @param pbo
    * @param map
    * @return
     */
    @SuppressWarnings("unchecked")
	private static Hashtable getSignatureInfo(Object pbo, Map<String, String> map) {
    	 Hashtable wfInfoTable = new Hashtable();
    	 wfInfoTable.put("SHEJIZHESHIJIAN", map.get("编制者"));
    	 wfInfoTable.put("SHEJI", map.get("编制者"));
    	 wfInfoTable.put("SHEJISHIJIAN", map.get("编制者时间"));

         wfInfoTable.put("GENGGAI", map.get("GENGGAI"));
         wfInfoTable.put("GENGGAISHIJIAN", map.get("GENGGAISHIJIAN"));

    	 wfInfoTable.put("JIAODUIZHESHIJIAN", map.get("校对者"));
    	 wfInfoTable.put("JIAODUI", map.get("校对者"));
    	 wfInfoTable.put("JIAODUISHIJIAN", map.get("校对者时间"));

    	 wfInfoTable.put("SHENHEZHESHIJIAN", map.get("审核者"));
    	 wfInfoTable.put("SHENHE", map.get("审核者"));
    	 wfInfoTable.put("SHENHESHIJIAN", map.get("审核者时间"));

    	 wfInfoTable.put("BIAOSHENZHESHIJIAN", map.get("标审者"));
    	 wfInfoTable.put("BIAOSHEN", map.get("标审者"));
    	 wfInfoTable.put("BIAOSHENSHIJIAN", map.get("标审者时间"));

    	 wfInfoTable.put("PIZHUNZHESHIJIAN", map.get("批准者"));
    	 wfInfoTable.put("PIZHUN", map.get("批准者"));
    	 wfInfoTable.put("PIZHUNSHIJIAN", map.get("批准者时间"));
    	 String value = "";
    	 for(int i = 0;i < 5; i++){
    		 String c = String.valueOf(i + 1);
    		 String huiqian = map.get(c);
    		 if(huiqian == null || "".equals(huiqian)){
    			 break;
    		 }
        	 String huiqianshijian = map.get(c + "时间");
        	 String huiqianbumen = map.get(c + "部门");
        	 if(!"".equals(value)){
    			 value = value + ";";
    		 }
        	 value = value + huiqian + "/" + huiqianbumen + "/" + huiqianshijian;
    	 }
    	 wfInfoTable.put("HUIQIAN1", value);
    	 wfInfoTable.put("HUIQIAN1ZHESHIJIAN", value);
    	 wfInfoTable.put("NEIBUHUIQIAN", value);
    	 wfInfoTable.put("ZIDINGYI", "true");
    	 wfInfoTable.put("ecnBiaoJi",map.get("ecnBiaoJi")==null ? "":map.get("ecnBiaoJi"));
         wfInfoTable.put("ecnNumber",map.get("ecnNumber")==null ? "":map.get("ecnNumber"));
		return wfInfoTable;
	}

	public static Vector<Object> getPrintObjVector(Object pbo) throws WTException {
        Vector<Object> objV = new Vector<Object>();
        if (pbo instanceof WTDocument) {
            objV.add(pbo);
        } else if (pbo instanceof ProcessEnvelope) {
            List<RevisionControlled> rcList = EnvelopeHelper.service.getAllMembers((ProcessEnvelope) pbo);
            for (int i = 0; i < rcList.size(); i++) {
                RevisionControlled rc = rcList.get(i);
                if (!(rc instanceof WTPart)) {
                    objV.add(rc);
                }
            }
        } else if (pbo instanceof WTChangeOrder2) {
            objV.add(pbo);
            List<WTObject> wtoList = ChangeHelper.getChangeResultItem((WTChangeOrder2) pbo);
            for (int i = 0; i < wtoList.size(); i++) {
                WTObject wto = wtoList.get(i);
                if (!(wto instanceof WTPart)) {
                    objV.add(wto);
                }
            }
           /* WTObject targetObj = getReleatedDocByECN((WTChangeOrder2)pbo);
            if(targetObj != null){
            	objV.add(targetObj);
            }*/
        }
        return objV;
    }

    static HashMap<String, Integer> reviewPathMap;
    static {
        reviewPathMap = new HashMap<String, Integer>();
        reviewPathMap.put("工艺通知单驳回修改", new Integer(1));
        reviewPathMap.put("工艺通知单提交签审", new Integer(1));
        reviewPathMap.put("编制", new Integer(1));
        reviewPathMap.put("校对", new Integer(2));
        reviewPathMap.put("审核", new Integer(3));
        reviewPathMap.put("内部会签", new Integer(4));
        reviewPathMap.put("外部会签", new Integer(5));
        reviewPathMap.put("工艺会签", new Integer(6));
        reviewPathMap.put("工艺会签汇总", new Integer(7));
        reviewPathMap.put("标审", new Integer(8));
        reviewPathMap.put("复审", new Integer(9));
        reviewPathMap.put("批准", new Integer(10));
    }

    public static Hashtable getWFInfo2(Object object, WfProcess wf, boolean isPartReviewProcess,
            boolean isChangePhaseECN, Object pbo) throws Exception {
        Hashtable wfInfoTable = new Hashtable();
        Hashtable infoList = new Hashtable();
        List activityList = new ArrayList();


        boolean isPROCESS_NOTICE = false;//工艺技术通知单/技术协议、总方案分方案、只签名称
        if (object instanceof WTDocument) {
            String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(object).toString();
            if (objectType.indexOf("PROCESS_NOTICE") > -1||objectType.indexOf("TECHNOLOGY_AGREEMENT") > -1
            		||objectType.indexOf("GONGYIZONGFANGAN") > -1||objectType.indexOf("GONGYIFENFANGAN") > -1) {//工艺技术通知单
            	isPROCESS_NOTICE = true;
            }
        }
        activityList.clear();
        activityList = PrintHelper.getActivities(wf, activityList);

        //WfBlock wfblock = PrintHelper.getBlock(wf);
        boolean isHasWfBlock = false;
        if(hasWfBlockProcess.contains(wf.getTemplate().getName())){
        	List<WfBlock> allWfBlocks = PrintHelper.getAllBlock(wf);
        	 for (WfBlock wfBlock : allWfBlocks) {
                 PrintHelper.getActivities(wfBlock, activityList);
             }
        	 isHasWfBlock = true;
        }

        // activityList new function

//        if (wfblock != null){
//            activityList = PrintHelper.getActivities(wfblock, activityList);
//        }

        int currReviewStateNum = 999;
        WfAssignedActivity wfaactivity = PrintHelper.getLatestCompleteActivity(wf, activityList,isHasWfBlock);
        if (reviewPathMap.containsKey(wfaactivity.getName())) {
            currReviewStateNum = reviewPathMap.get(wfaactivity.getName());
            if (currReviewStateNum == 2) {
                // //标审重新送审
                // if(IntChongXinSongShen == WorkflowConstants.ASES_EXPRESSION_BIAOSHEN_NONE)
                // currReviewStateNum = 9;
                // //批准重新送审
                // else if(IntChongXinSongShen == WorkflowConstants.ASES_EXPRESSION_PIZHUN_NONE)
                // currReviewStateNum = 10;
                // else
                // //如果是校对，自动往前承认到审核
                currReviewStateNum = 3;
            }

        }
        log.debug("currReviewStateNum=" + currReviewStateNum);

        boolean hasPizhun = false;
        Iterator iterator1 = activityList.iterator();
        do {
            if (!iterator1.hasNext()) {
                break;
            }
            WfAssignedActivity wfactivity = (WfAssignedActivity) iterator1.next();
            log.debug("任务 " + wfactivity.getName() + " 的状态是" + wfactivity.getState());
            if (wfactivity.getName().indexOf("批准") > -1) {
                hasPizhun = true;
            }
        } while (true);

        log.debug("--------hasPizhun:"+hasPizhun);
        log.debug("--------isPartReviewProcess:"+isPartReviewProcess);

        List<String> userList = SignatureService.getHQObjectLinkUsers(pbo, object);
        List<String> userNameList = new ArrayList<String>();
        for (String userOid : userList) {
            if (!"".equals(userOid.trim())) {
                WTUser tempUser = (WTUser)WCUtil.getPersistable(userOid);
                userNameList.add(tempUser.getFullName());
            }
        }
        Timestamp afterEndTimeLine = null;
        Iterator iterator = activityList.iterator();
        do {
            if (!iterator.hasNext()) {
                break;
            }
            WfAssignedActivity wfactivity = (WfAssignedActivity) iterator.next();
            System.out.println("任务 " + wfactivity.getName() + " 的状态是" + wfactivity.getState());

            if ("OPEN_RUNNING".equals(wfactivity.getState().toString())) {
                System.out.println("---------OPEN_RUNNING  pass");
                continue;
            }

            Hashtable tmpHashtable = new Hashtable();
            String activityName = wfactivity.getName();

            if (activityName == null) {
                activityName = "";
            }
            String rolePrincipalName = getPrincipalName(wfactivity,pbo,object);
            String principal = getOnlyPrincipalName(wfactivity);
            String endTime = "";
            if (rolePrincipalName == null){
                rolePrincipalName = "";
            }
            if (wfactivity.getStartTime() != null && wfactivity.getEndTime() != null) {
                endTime = WTStandardDateFormat.format(wfactivity.getEndTime(), "yyyy-MM-dd");
            } else {
                endTime = "";
            }

            if (activityName.equalsIgnoreCase("校对")) {
                afterEndTimeLine =  wfactivity.getEndTime();
            }
            if (activityName.equalsIgnoreCase("编制")) {
                if (object instanceof WTDocument) {
                    //更改编制人为修改者
                    principal = ((WTDocument) object).getModifierFullName();
                    if (principal.equals("Administrator")) {
                        principal = ((WTDocument) object).getCreatorFullName();
                    }
                    rolePrincipalName = principal + "/" + getUserDepartment(((WTDocument) object).getModifierName())
                            + "/" + endTime;
                } else if (object instanceof EPMDocument) {
                    principal = ((EPMDocument) object).getModifierFullName();
                    if (principal.equals("Administrator")) {
                        principal = ((EPMDocument) object).getCreatorFullName();
                    }
                    rolePrincipalName = principal + "/" + getUserDepartment(((EPMDocument) object).getModifierName())
                            + "/" + endTime;
                }

                tmpHashtable.put("principal", principal);
                tmpHashtable.put("rolePrincipalName", rolePrincipalName);
                tmpHashtable.put("endTime", endTime);
            } else if(activityName.equalsIgnoreCase("外部会签")
            		&&(processTemps.contains(wf.getTemplate().getName())
            		||Constants.WFN_PROCESS_DOCUMENT.equals(wf.getTemplate().getName())
            		||Constants.WFN_PROCESS_WAIXIEDOCUMENT.equals(wf.getTemplate().getName())
            		||Constants.WFN_PROCESS_GONGYIFANGAN.equals(wf.getTemplate().getName()))){

	            	tmpHashtable.put("principal", principal);
	                tmpHashtable.put("endTime", endTime);
	                ProcessData processData = wfactivity.getContext();
	                ArrayList outSignList = (ArrayList)processData.getValue("outSignInfo");
	                String tempStr = "";
	                if(outSignList!=null){
	                	for(int i = 0; i < outSignList.size(); i++){
	                		String s = "";
	    	           		NmOid oid = (NmOid)outSignList.get(i);
	    	           		if(oid!=null){
	    	           			HashMap map = oid.getAdditionalInfo();//signName/signCompany/signDate/signRemark
	    	           			if(map!=null){
			    	           		s = map.get("signName")+"/"+map.get("signCompany")+"/"+map.get("signDate");
			    	           		tempStr = tempStr + s + ";";
	    	           			}
	    	           		}

	                	}
	                }
	                tmpHashtable.put("rolePrincipalName", tempStr);
            	/*else if(Constants.WFN_WUJIPROCESSWF.equals(wf.getTemplate().getName())||Constants.WFN_PROCESS_ECN.equals(wf.getTemplate().getName())){
            		tmpHashtable.put("principal", principal);
                    tmpHashtable.put("endTime", endTime);
                    ProcessData processData = wfactivity.getContext();
                    String signValues = (String)processData.getValue("outSignInfos");
                    String tempStr = getoutSignValues(signValues,object,endTime);
                    tmpHashtable.put("rolePrincipalName", tempStr);
            	}*/
            }else if(activityName.equalsIgnoreCase("工艺会签")){
                tmpHashtable.put("principal", principal);
                tmpHashtable.put("endTime", endTime);

                WfProcess process = wfactivity.getParentProcess();
                //零部件签审流程和ECN流程需要对"工艺会签"作特殊处理
                if (process.getName().contains(Constants.WF_PART_APPROVAL)||process.getName().contains(Constants.WF_SJ_ECN)) {
                  //过滤掉不是自己会签的对象
                    if (rolePrincipalName.contains(";")) {
                        String[] str = rolePrincipalName.split(";");
                        String tempStr = "";
                        for (String string : str) {
                            for (String tempString : userNameList) {
                                if (string.contains(tempString)) {
                                    if ("".equals(tempStr)) {
                                        tempStr = string;
                                    }else {
                                        tempStr = tempStr+";"+string;
                                    }
                                }
                            }
                        }
                        tmpHashtable.put("rolePrincipalName", tempStr);
                    }else {
                        for (String tempString : userNameList) {
                            if (rolePrincipalName.contains(tempString)) {
                                tmpHashtable.put("rolePrincipalName", rolePrincipalName);
                            }
                        }
                    }
                }else {
                    tmpHashtable.put("rolePrincipalName", rolePrincipalName);
                }
            }else if(activityName.equalsIgnoreCase("内部会签")){
                if(afterEndTimeLine!=null){
                   Timestamp nbhqTime =  wfactivity.getEndTime();
                    if(afterEndTimeLine.after(nbhqTime)){
                        continue;
                    }
                }
                tmpHashtable.put("principal", principal);
                tmpHashtable.put("rolePrincipalName", rolePrincipalName);
                tmpHashtable.put("endTime", endTime);

            } else {
                tmpHashtable.put("principal", principal);
                tmpHashtable.put("rolePrincipalName", rolePrincipalName);
                tmpHashtable.put("endTime", endTime);
            }
            if (tmpHashtable.get("rolePrincipalName")!=null) {
                infoList.put(activityName, tmpHashtable);
            }

        } while (true);

        // 得到会签意见
        Map<String, String> signMap =  new HashMap<String,String>();
        if (wf.getName().contains(Constants.WF_PART_APPROVAL)
                || wf.getName().contains(Constants.WF_SJ_ECN)) {
        	  signMap = SignatureHelper.getSignatureByWTObject(wf, (WTObject) object);
        }

        log.debug("signMap is; " + signMap.size());
        log.debug("signMap is; " + signMap);
        Set set = signMap.keySet();
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = (String) signMap.get(key);
            log.debug("*** " + key + " = " + value + " ***");
        }
        log.debug("signMap 内部会签 is; " + signMap.get("内部会签"));
        log.debug("signMap 外部会签 is; " + signMap.get("外部会签"));
        log.debug("signMap "+Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG+" is; " + signMap.get(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG));
        log.debug("signMap 工艺会签 is; " + signMap.get("工艺会签"));
        log.debug("signMap 工艺会签汇总 is; " + signMap.get("工艺会签汇总"));
        // 放入各个节点信息
        for (Object key : infoList.keySet()) {
            Hashtable value = (Hashtable) infoList.get(key);
            log.debug("key:" + key + "|value:" + value);
            if (reviewPathMap.containsKey(key) && reviewPathMap.get(key).intValue() > currReviewStateNum){
                continue;
            }
            if (key.equals("编制")||key.equals("工艺通知单提交签审")) {
                wfInfoTable.put("SHEJIZHESHIJIAN", value.get("rolePrincipalName"));

                wfInfoTable.put("SHEJI", value.get("rolePrincipalName"));
                wfInfoTable.put("SHEJISHIJIAN", value.get("endTime"));

                if(isPROCESS_NOTICE){//如果是工艺技术通知单，则只签名称，不需要时间
                	 wfInfoTable.put("SHEJI", value.get("principal"));
                }

                if(object instanceof WTDocument){
                	WTDocument d = (WTDocument)object;
                	String typeName = TypedUtility.getTypeIdentifier(d).getTypename();
                	WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(d);
               	  	Iterator it = coll.iterator();
               	  	boolean hasECN= false;
               	  	if (it.hasNext()) {
               	  		WTChangeOrder2 ecn =(WTChangeOrder2) ((ObjectReference) it.next()).getObject();
               	  		hasECN = true;
               	  	}
    				if(hasECN&&typeName.contains("PROCESS_PLAN")) {
    					Object genggai = value.get("rolePrincipalName");
    					if(genggai!=null&&genggai.toString().contains("/")){
    						String[] ss = genggai.toString().split("/");
    						wfInfoTable.put("GENGGAI", ss[0]);
                            wfInfoTable.put("GENGGAISHIJIAN", value.get("endTime"));
    					}

    				}


                }



            } else if (key.equals("参与人")) {//工艺技术协议新增
                wfInfoTable.put("CANYURENZHESHIJIAN", value.get("rolePrincipalName"));
                wfInfoTable.put("CANYUREN", value.get("rolePrincipalName"));
                wfInfoTable.put("CANYURENSHIJIAN", value.get("endTime"));

            } else if (key.equals("校对")) {
                wfInfoTable.put("JIAODUIZHESHIJIAN", value.get("rolePrincipalName"));
                wfInfoTable.put("JIAODUI", value.get("rolePrincipalName"));
                wfInfoTable.put("JIAODUISHIJIAN", value.get("endTime"));

                if(isPROCESS_NOTICE){//如果是工艺技术通知单，则只签名称，不需要时间
               	 	wfInfoTable.put("JIAODUI", value.get("principal"));
                }

            } else if (key.equals("审核")) {
                wfInfoTable.put("SHENHEZHESHIJIAN", value.get("rolePrincipalName"));
                wfInfoTable.put("SHENHE", value.get("rolePrincipalName"));
                wfInfoTable.put("SHENHESHIJIAN", value.get("endTime"));

                if(isPROCESS_NOTICE){//如果是工艺技术通知单，则只签名称，不需要时间
               	 	wfInfoTable.put("SHENHE", value.get("principal"));
                }


            } else if (key.equals("内部会签")) {
            	String NEIBUHUIQIAN = signMap.get("内部会签");
            	if((pbo instanceof WTChangeOrder2&&Constants.WFN_PROCESS_ECN.equals(wf.getTemplate().getName()))
                        ||(pbo instanceof WTChangeOrder2&&Constants.WFN_DOCUMENT_ECN.equals(wf.getTemplate().getName()))
            			||(pbo instanceof WTDocument&&Constants.WFN_WUJIPROCESSWF.equals(wf.getTemplate().getName()))
            			||(pbo instanceof WTDocument&&Constants.WFN_SANJIPROCESSWF.equals(wf.getTemplate().getName()))
            			||(pbo instanceof WTDocument&&Constants.WFN_PROCESS_WAIXIEDOCUMENT.equals(wf.getTemplate().getName()))
            			||(pbo instanceof WTDocument&&processTemps.contains(wf.getTemplate().getName()))
            			||(pbo instanceof WTDocument&&Constants.WFN_PROCESS_DOCUMENT.equals(wf.getTemplate().getName()))
            			||(pbo instanceof WTDocument&&"技术协议签审流程".equals(wf.getTemplate().getName())
                        ||(pbo instanceof WTDocument&&Constants.WFN_WUJIREPORTPROCESSWF.equals(wf.getTemplate().getName())))
                        ||(pbo instanceof WTDocument&&SopConstants.SOP_WORKFLOW_SOPPROCESS.equals(wf.getTemplate().getName()))){
            		wfInfoTable.put("HUIQIAN1ZHESHIJIAN", value.get("rolePrincipalName"));
                    wfInfoTable.put("HUIQIAN1", value.get("rolePrincipalName"));
                    wfInfoTable.put("HUIQIAN1SHIJIAN",  value.get("endTime"));

                    wfInfoTable.put("NEIBUHUIQIAN", value.get("rolePrincipalName"));
            	}else{
                     if(NEIBUHUIQIAN==null||"".equals(NEIBUHUIQIAN)){
                         continue;
                     }
                     wfInfoTable.put("HUIQIAN1ZHESHIJIAN", value.get("rolePrincipalName"));
                     wfInfoTable.put("HUIQIAN1", value.get("rolePrincipalName"));
                     wfInfoTable.put("HUIQIAN1SHIJIAN", value.get("endTime"));

                  // 检查该流程是否为 零部件签审流程 或 更改流程， 如果是 则需要对会签意见作特别处理
                     if ((object instanceof WTDocument || object instanceof EPMDocument) && (!isPartReviewProcess)) {
                         wfInfoTable.put("NEIBUHUIQIAN", value.get("rolePrincipalName"));
                     } else if ((object instanceof WTDocument || object instanceof EPMDocument || object instanceof WTChangeOrder2)
                             && (isPartReviewProcess)) {
                         wfInfoTable.put("NEIBUHUIQIAN", NEIBUHUIQIAN);
                     }
            	}



            } else if (key.equals("外部会签")) {
            	if((pbo instanceof WTDocument&&Constants.WFN_WUJIREPORTPROCESSWF.equals(wf.getTemplate().getName()))){
            		wfInfoTable.put("HUIQIAN2ZHESHIJIAN", value.get("rolePrincipalName"));
                    wfInfoTable.put("HUIQIAN2", value.get("rolePrincipalName"));
                    wfInfoTable.put("HUIQIAN2SHIJIAN", value.get("endTime"));
                    wfInfoTable.put("WAIBUHUIQIAN",  value.get("rolePrincipalName"));

            	}else if(pbo instanceof WTChangeOrder2&&
            			Constants.WFN_PROCESS_ECN.equals(wf.getTemplate().getName())
            			||(pbo instanceof WTDocument&&Constants.WFN_WUJIPROCESSWF.equals(wf.getTemplate().getName()))){
            		wfInfoTable.put("HUIQIAN2ZHESHIJIAN", value.get("rolePrincipalName"));
                    wfInfoTable.put("HUIQIAN2", value.get("rolePrincipalName"));
                    wfInfoTable.put("HUIQIAN2SHIJIAN", value.get("endTime"));
                    wfInfoTable.put("WAIBUHUIQIAN",  value.get("rolePrincipalName"));
            	}else if(processTemps.contains(wf.getTemplate().getName())){
            		wfInfoTable.put("HUIQIAN2ZHESHIJIAN", value.get("rolePrincipalName"));
                    wfInfoTable.put("HUIQIAN2", value.get("rolePrincipalName"));
                    wfInfoTable.put("HUIQIAN2SHIJIAN", value.get("endTime"));
                    wfInfoTable.put("WAIBUHUIQIAN",  value.get("rolePrincipalName"));
            	}else{
            		String WAIBUHUIQIAN = signMap.get("外部会签");
                    if (WAIBUHUIQIAN == null||"".equals(WAIBUHUIQIAN)) {
                        continue;
                    }
                    wfInfoTable.put("HUIQIAN2ZHESHIJIAN", value.get("rolePrincipalName"));
                    wfInfoTable.put("HUIQIAN2", value.get("rolePrincipalName"));
                    wfInfoTable.put("HUIQIAN2SHIJIAN", value.get("endTime"));
                    wfInfoTable.put("WAIBUHUIQIAN", WAIBUHUIQIAN);
            	}


            } else if (key.equals(Constants.ACTIVITYNAME_ZPGYHQ)) {
                //wfInfoTable.put("GONGYIHUIQIAN", value.get("rolePrincipalName"));
                log.debug("rolePrincipalName:" + value.get("rolePrincipalName"));
                // 检查该流程是否为 零部件签审流程 或 更改流程， 如果是 则需要对会签意见作特别处理
                if ((object instanceof WTDocument || object instanceof EPMDocument) && (!isPartReviewProcess)) {
                    //wfInfoTable.put("GONGYIHUIQIAN", value.get("rolePrincipalName"));
                } else if ((object instanceof WTDocument || object instanceof EPMDocument || object instanceof WTChangeOrder2)
                        && (isPartReviewProcess)) {
                    String GONGYIHUIQIAN = signMap.get(Constants.ACTIVITYNAME_ZPGYHQ);
                    if (GONGYIHUIQIAN == null) {
                        GONGYIHUIQIAN = "";
                    }
                    //wfInfoTable.put("GONGYIHUIQIAN", GONGYIHUIQIAN);
                    log.debug("GONGYIHUIQIAN:" + GONGYIHUIQIAN);
                }
            } else if (key.equals(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG)) {
                String GONGYIHUIQIAN = signMap.get("工艺会签");
                log.debug("GONGYIHUIQIAN:" + GONGYIHUIQIAN);
                if (GONGYIHUIQIAN == null || "".equals(GONGYIHUIQIAN)) {
                    wfInfoTable.put("GONGYIHUIQIANSHIJIAN", value.get("rolePrincipalName"));
                    log.debug("rolePrincipalName:" + value.get("rolePrincipalName"));
                    // 检查该流程是否为 零部件签审流程 或 更改流程， 如果是 则需要对会签意见作特别处理
                    if ((object instanceof WTDocument || object instanceof EPMDocument) && (!isPartReviewProcess)) {
                        wfInfoTable.put("GONGYIHUIQIANSHIJIAN", value.get("rolePrincipalName"));
                    } else if ((object instanceof WTDocument || object instanceof EPMDocument || object instanceof WTChangeOrder2)
                            && (isPartReviewProcess)) {
                        String ZHIPAIGONGYIZHUZHANGSHIJIAN = signMap.get(Constants.ACTIVITYNAME_ZHIPAIGONGYIZUZHANG);
                        if (ZHIPAIGONGYIZHUZHANGSHIJIAN == null) {
                            ZHIPAIGONGYIZHUZHANGSHIJIAN = "";
                        }
                        wfInfoTable.put("GONGYIHUIQIANSHIJIAN", ZHIPAIGONGYIZHUZHANGSHIJIAN);
                        log.debug("ZHIPAIGONGYIZHUZHANGSHIJIAN:" + ZHIPAIGONGYIZHUZHANGSHIJIAN);
                    }
                }
            } else if (key.equals("工艺会签")) {// 工艺会签
                String GONGYIHUIQIAN = signMap.get("工艺会签");
                if (GONGYIHUIQIAN == null||"".equals(GONGYIHUIQIAN)) {
                    continue;
                }
                //wfInfoTable.put("GONGYIHUIQIANZHESHIJIAN", value.get("rolePrincipalName"));
                log.debug("rolePrincipalName:" + value.get("rolePrincipalName"));

                if (object instanceof WTDocument) {//图样的文档签审流程，需要对会签做特殊处理
                    String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(object).toString();
                    log.debug(">>>>>>>>>>objectType:"+objectType);
                    if (objectType.indexOf("DRAWING_DOC") > -1) {
                        wfInfoTable.put("GONGYIHUIQIAN", GONGYIHUIQIAN);
                    }else {
                        if (!isPartReviewProcess) {
                            String rolePrincipalName = String.valueOf(value.get("rolePrincipalName"));
                            if (GONGYIHUIQIAN!=null&&GONGYIHUIQIAN.contains(";")) {//处理多个工艺员会签
                                String str1 = GONGYIHUIQIAN.substring(0, GONGYIHUIQIAN.indexOf(";"));
                                String str2 = GONGYIHUIQIAN.substring(GONGYIHUIQIAN.indexOf(";")+1, GONGYIHUIQIAN.length());
                                wfInfoTable.put("GONGYIHUIQIANSHIJIAN", str1);
                                wfInfoTable.put("GONGYIHUIQIAN", str2);
                            }else {
                                wfInfoTable.put("GONGYIHUIQIANSHIJIAN", rolePrincipalName);
                            }
                        } else if (isPartReviewProcess) {
                            String rolePrincipalName = String.valueOf(value.get("rolePrincipalName"));
                            if (GONGYIHUIQIAN!=null&&GONGYIHUIQIAN.contains(";")) {
                                String str1 = GONGYIHUIQIAN.substring(0, GONGYIHUIQIAN.indexOf(";"));
                                String str2 = GONGYIHUIQIAN.substring(GONGYIHUIQIAN.indexOf(";")+1, GONGYIHUIQIAN.length());
                                wfInfoTable.put("GONGYIHUIQIANSHIJIAN", str1);
                                wfInfoTable.put("GONGYIHUIQIAN", str2);
                            }else {
                                wfInfoTable.put("GONGYIHUIQIANSHIJIAN", rolePrincipalName);
                            }

                            log.debug("GONGYIHUIQIANSHIJIAN:" + GONGYIHUIQIAN);
                        }
                    }
                }else {
                 // 检查该流程是否为 零部件签审流程 或 更改流程， 如果是 则需要对会签意见作特别处理
                    if ((object instanceof EPMDocument) && (!isPartReviewProcess)) {
                        String rolePrincipalName = String.valueOf(value.get("rolePrincipalName"));
                        if (GONGYIHUIQIAN!=null&&GONGYIHUIQIAN.contains(";")) {//处理多个工艺员会签
                            String str1 = GONGYIHUIQIAN.substring(0, GONGYIHUIQIAN.indexOf(";"));
                            String str2 = GONGYIHUIQIAN.substring(GONGYIHUIQIAN.indexOf(";")+1, GONGYIHUIQIAN.length());
                            wfInfoTable.put("GONGYIHUIQIANSHIJIAN", str1);
                            wfInfoTable.put("GONGYIHUIQIAN", str2);
                        }else {
                            wfInfoTable.put("GONGYIHUIQIANSHIJIAN", rolePrincipalName);
                        }
                    } else if ((object instanceof EPMDocument || object instanceof WTChangeOrder2)
                            && (isPartReviewProcess)) {
                        String rolePrincipalName = String.valueOf(value.get("rolePrincipalName"));
                        if (GONGYIHUIQIAN!=null&&GONGYIHUIQIAN.contains(";")) {
                            String str1 = GONGYIHUIQIAN.substring(0, GONGYIHUIQIAN.indexOf(";"));
                            String str2 = GONGYIHUIQIAN.substring(GONGYIHUIQIAN.indexOf(";")+1, GONGYIHUIQIAN.length());
                            wfInfoTable.put("GONGYIHUIQIANSHIJIAN", str1);
                            wfInfoTable.put("GONGYIHUIQIAN", str2);
                        }else {
                            wfInfoTable.put("GONGYIHUIQIANSHIJIAN", rolePrincipalName);
                        }

                        log.debug("GONGYIHUIQIANSHIJIAN:" + GONGYIHUIQIAN);
                    }
                }
            } else if (key.equals("工艺会签汇总")) {// 工艺会签汇总
                if (object instanceof WTDocument) {
                    String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(object).toString();
                    log.debug(">>>>>>>>>>objectType:"+objectType);
                    if (objectType.indexOf("DRAWING_DOC") > -1) {
                        wfInfoTable.put("GONGYIHUIQIANHUIZONG", value.get("rolePrincipalName"));
                    }
                }
            } else if (key.equals("标审")) {
                wfInfoTable.put("BIAOSHENZHESHIJIAN", value.get("rolePrincipalName"));
                wfInfoTable.put("BIAOSHEN", value.get("rolePrincipalName"));
                wfInfoTable.put("BIAOSHENSHIJIAN", value.get("endTime"));

                if(isPROCESS_NOTICE){//如果是工艺技术通知单，则只签名称，不需要时间
               	 	wfInfoTable.put("BIAOSHEN", value.get("principal"));
                }

            } else if (key.equals("复审")) {
                wfInfoTable.put("FUSHENZHESHIJIAN", value.get("rolePrincipalName"));
                wfInfoTable.put("FUSHEN", value.get("rolePrincipalName"));
                wfInfoTable.put("FUSHENSHIJIAN", value.get("endTime"));
            } else if (key.equals("批准")) {
                wfInfoTable.put("PIZHUNZHESHIJIAN", value.get("rolePrincipalName"));
                wfInfoTable.put("PIZHUN", value.get("rolePrincipalName"));
                wfInfoTable.put("PIZHUNSHIJIAN", value.get("endTime"));

                if(isPROCESS_NOTICE){//如果是工艺技术通知单，则只签名称，不需要时间
               	 	wfInfoTable.put("PIZHUN", value.get("principal"));
                }
            }
        }

        //处理历史数据，处理完后删掉该代码
       /* if (pbo instanceof WTChangeOrder2) {
        	WTChangeOrder2 ecn = (WTChangeOrder2)pbo;
        	if(ecn.getNumber().equals("YGRZ621-0402")){
	        	if(wfInfoTable.get("PIZHUNZHESHIJIAN")==null||"".equals(wfInfoTable.get("PIZHUNZHESHIJIAN"))){
	        		wfInfoTable.put("PIZHUNZHESHIJIAN","王辉均/武器项目一部/2016-01-27");
	                wfInfoTable.put("PIZHUN", "王辉均/武器项目一部/2016-01-27");
	                wfInfoTable.put("PIZHUNSHIJIAN","2016-01-27");
	        	}
        	}
        }*/




        // 获取对象的基本信息,并放入HashTable
        // 文档(除图样)
        int print_mode = 0;
        if (object instanceof WTDocument) {
            String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(object).toString();
            log.debug("---------objectType:"+objectType);
            if (objectType.indexOf("DRAWING_DOC") > -1) {
                print_mode = PRINT_TUYANG;
            } else if (objectType.indexOf("TECHNOTICE_DOC") > -1) {
                print_mode = PRINT_JISHUTONGZHIDAN;
            } else if (objectType.indexOf("TECHNOLOGY_AGREEMENT") > -1) {
                print_mode = PRINT_JISHUXIEYI;
            }  else if (objectType.indexOf("PROCESS_NOTICE") > -1) {//工艺技术通知单
                print_mode = PRINT_GONGYIJISHUTONGZHIDAN;
            } else if (objectType.indexOf("casc.sast.149.GONGYIFENFANGAN") > -1 || objectType.indexOf("casc.sast.149.GONGYIFENXICEHUAZONGJIE") > -1
                    ||objectType.indexOf("casc.sast.149.GONGYIDINGXING") > -1 || objectType.indexOf("casc.sast.149.GONGYIJIANDING") > -1
                    || objectType.indexOf("casc.sast.149.TEST_REPORT") > -1 ) {
                print_mode = PRINT_GYFA;
            }  else if (objectType.indexOf("casc.sast.149.GONGYIZONGFANGAN") > -1 || objectType.indexOf("casc.sast.149.JISHUKETI") > -1) {
                print_mode = PRINT_GYZFA;
            } else {
                print_mode = PRINT_DOCUMENT;
            }
            WTDocument document = (WTDocument) object;
           // wfInfoTable.put("VERSIONITE", document.getIterationDisplayIdentifier().toString());
        } else if (object instanceof EPMDocument) {
            print_mode = PRINT_TUYANG;
        } else if (object instanceof WTChangeOrder2) {
            print_mode = PRINT_ECN;
        }

        if (print_mode == PRINT_DOCUMENT) {
            String NUMBER = "";
            String NAME = "";
            String DOCTYPE = "";
            String JDBJ = "";
            String MIJI = "";
            String DEPT = "";
            String SUMMARY = "";
            String KEYWORD = "";
            String ECN_NUMBER = "";
            String ECN_MODIFYTIME = "";
            String ECN_MODIFIER = "";
            String VERSION = "";
            String VERSIONITE="";
            String MODIFIER = "";
            String MODIFYTIME = "";

            WTDocument doc = (WTDocument) object;

            // 编号 文档 Number NUMBER
            NUMBER = doc.getNumber();
            // 名称 文档 Name NAME
            NAME = doc.getName();
            // 文件类型
            DOCTYPE = IBAHelper.getSoftType(doc);
            log.debug("--------DOCTYPE:" + DOCTYPE);
            if (DOCTYPE.equals("DRAWING_DOC")){
                DOCTYPE = "图样";
            } else if (DOCTYPE.equals("FORM_DOC")){
                DOCTYPE = "文字或表格类设计文件";
            } else if (DOCTYPE.equals("RESEARCH_DOC")){
                DOCTYPE = "研试文件";
            } else if (DOCTYPE.equals("SOFTWARE_DOC")){
                DOCTYPE = "软件文档";
            }else if (DOCTYPE.equals("QA_REPORT")){
                DOCTYPE = "质量报告";
            }
            // 阶段标记 文档 PHASE_CODE JDBJ
            JDBJ = IBAHelper.getIBAStringValue((WTDocument) object, "PHASE_CODE");
            if (JDBJ == null) {
                JDBJ = "";
            }
            // 密级 文档 SECRET MIJI
            MIJI = IBAHelper.getIBAStringValue(doc, "SECRET");

            if (MIJI != null && MIJI.indexOf("内部") > -1)
                MIJI = "内部";
            if (MIJI != null && MIJI.indexOf("秘密") > -1)
                MIJI = "秘密★10年";
            if (MIJI != null && MIJI.indexOf("机密") > -1)
                MIJI = "秘密★20年";
            if (MIJI == null || "无".equals(MIJI)) {
                MIJI = "公开";
            }

            // 单位 文档 DEPT
            DEPT = IBAHelper.getIBAStringValue(doc, "DEPT");
            if (DEPT == null) {
                DEPT = "";
            } else if (DEPT.indexOf("<部门>") > -1) {
                DEPT = DEPT.substring(4);
            }
            // 内容摘要 文档 SUMMARY SUMMERY
            SUMMARY = IBAHelper.getIBAStringValue(doc, "SUMMARY");
            if (SUMMARY == null) {
                SUMMARY = "";
            }
            // 主题词 文档 KEYWORD KEYWORD
            KEYWORD = IBAHelper.getIBAStringValue(doc, "KEYWORD");
            if (KEYWORD == null) {
                KEYWORD = "";
            }

            // 更改单号 文档 ECN_Number ECN_NUMBER
            // 更改日期 文档 ECN_ModifyTime ECN_MODIFYTIME
            // 更改人 文档 ECN_Modifier ECN_MODIFIER
            QueryResult qrECN = ChangeHelper2.service.getUniqueImplementedChangeOrders(doc);
            while (qrECN.hasMoreElements()) {
                WTChangeOrder2 ecn = (WTChangeOrder2) qrECN.nextElement();
                String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(ecn).toString();
                if (objectType.indexOf("CHANGE_ECN") > 0) {
                    ECN_NUMBER = ecn.getNumber();
                    ECN_MODIFYTIME = ecn.getModifyTimestamp().toLocaleString();
                    ECN_MODIFIER = ecn.getModifierName();
                }
            }
            // 更改标记 文档 被更改对象的版本 VERSION
            WTDocument previours_doc = (WTDocument) ChangeHelper.getPredecessorObject(doc);
            if (previours_doc != null) {
                VERSION = previours_doc.getVersionIdentifier().getValue();
                VERSIONITE = previours_doc.getIterationDisplayIdentifier().toString();
            }

            if (VERSION == null || "".equals(VERSION)) {
                VERSION = doc.getVersionIdentifier().getValue();
                VERSIONITE = doc.getIterationDisplayIdentifier().toString();

            }
            /*String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
            if(docType.contains("PROCESS_PLAN") &&!docType.contains("reportTechnics")){
                VERSIONITE = doc.getIterationDisplayIdentifier().toString();
            }*/
            // 编写人 文档 Modifier MODIFIER
            MODIFIER = doc.getModifierFullName();
            // 编写日期 文档 ModifyTime MODIFYTIME
            MODIFYTIME = doc.getModifyTimestamp().toLocaleString();

            wfInfoTable.put("NUMBER", NUMBER);
            wfInfoTable.put("NAME", NAME);
            wfInfoTable.put("DOCTYPE", DOCTYPE);
            wfInfoTable.put("MIJI", MIJI);
            wfInfoTable.put("DEPT", DEPT);
            wfInfoTable.put("SUMMARY", SUMMARY);
            wfInfoTable.put("KEYWORD", KEYWORD);
            wfInfoTable.put("ECN_NUMBER", ECN_NUMBER);
            wfInfoTable.put("ECN_MODIFYTIME", ECN_MODIFYTIME);
            wfInfoTable.put("ECN_MODIFIER", ECN_MODIFIER);
            wfInfoTable.put("VERSION", VERSION);
            wfInfoTable.put("MODIFIER", MODIFIER);
            wfInfoTable.put("MODIFYTIME", MODIFYTIME);
//            wfInfoTable.put("VERSIONITE", VERSIONITE);


            // 阶段标记 文档 PHASE_CODE JDBJ
            JDBJ = IBAHelper.getIBAStringValue((WTObject) object, "PHASE_CODE");
            if (JDBJ == null) {
                JDBJ = "";
            }


            // 标识
            wfInfoTable.put("BIAOZHI", JDBJ);
        } else if (print_mode == PRINT_TUYANG) {
            String NUMBER = "";
            String JDBJ = "";
            String VERSION = "";
            if (object instanceof WTDocument){
                NUMBER = ((WTDocument) object).getNumber();
                VERSION = ((WTDocument) object).getVersionIdentifier().getValue();
            } else if (object instanceof EPMDocument){
                NUMBER = ((EPMDocument) object).getNumber();
                VERSION = ((EPMDocument) object).getVersionIdentifier().getValue();
            }
            if (NUMBER.indexOf(".DRW") > -1)
                NUMBER = NUMBER.substring(0, NUMBER.indexOf(".DRW"));

            // 阶段标记 文档 PHASE_CODE JDBJ
            JDBJ = IBAHelper.getIBAStringValue((WTObject) object, "PHASE_CODE");
            if (JDBJ == null) {
                JDBJ = "";
            }

            wfInfoTable.put("VERSION", VERSION);
            jdbjProcess(wfInfoTable, JDBJ);

            //wfInfoTable.put("FILENUMBER", NUMBER);

            // 转阶段时在图纸上写转阶段更改单信息
            if (JDBJ.startsWith("C")) {
                // 标识
                wfInfoTable.put("BIAOZHI1", JDBJ);
                String version = VersionControlHelper.getVersionIdentifier((Versioned) object).getValue();
                String iteration = VersionControlHelper.getIterationIdentifier((Iterated) object).getValue();

                // 版本
                wfInfoTable.put("EDITION1", version + "." + iteration);

                // 变更单
                if (isChangePhaseECN)
                    wfInfoTable.put("CSECNNUMBER1", ((WTChangeOrder2) pbo).getNumber());
            } else if (JDBJ.startsWith("S") || JDBJ.startsWith("Z")) {
                // 标识
                wfInfoTable.put("BIAOZHI2", JDBJ);
                String version = VersionControlHelper.getVersionIdentifier((Versioned) object).getValue();
                String iteration = VersionControlHelper.getIterationIdentifier((Iterated) object).getValue();

                // 版本
                wfInfoTable.put("EDITION2", version + "." + iteration);

                // 变更单
                if (isChangePhaseECN)
                    wfInfoTable.put("CSECNNUMBER2", ((WTChangeOrder2) pbo).getNumber());
            } else if (JDBJ.startsWith("D")) {
                // 标识
                wfInfoTable.put("BIAOZHI3", JDBJ);
                String version = VersionControlHelper.getVersionIdentifier((Versioned) object).getValue();
                String iteration = VersionControlHelper.getIterationIdentifier((Iterated) object).getValue();

                // 版本
                wfInfoTable.put("EDITION3", version + "." + iteration);

                // 变更单
                if (isChangePhaseECN)
                    wfInfoTable.put("CSECNNUMBER3", ((WTChangeOrder2) pbo).getNumber());
            }

        } else if (print_mode == PRINT_ECN) {
            String ECN_NUMBER = "";
            String PINDEX = "";
            // String RELEASEDEPT = "";
            String PHASE_CODE = "";
            String SECRETLEVEL = "";

            WTChangeOrder2 ecn = (WTChangeOrder2) object;

            // 更改单号 更改 编号
            ECN_NUMBER = ecn.getNumber();

            // 产品代号 更改 产品代号 (取第一个改后数据的所属产品的软属性)
            ArrayList resultItems = ChangeHelper.getChangeResultItem(ecn);
            for (int i = 0; i < resultItems.size(); i++) {
                Changeable2 changeable = (Changeable2) resultItems.get(i);
                if (changeable instanceof WTObject) {
                    PINDEX = IBAHelper.getIBAStringValue((WTObject) changeable, "ENDITEMIN");
                    SECRETLEVEL = IBAHelper.getIBAStringValue((WTObject) changeable, "SECRET");
                    if (PINDEX != null) {
                        break;
                    }
                }
            }

            //工艺文件更改单的产品代号为 工艺文件关联部件的编号getChangeablesBefore
            ArrayList affectItems = ChangeHelper.getChangeAffectItem(ecn);
            for (int i = 0; i < affectItems.size(); i++) {
                Changeable2 changeable = (Changeable2) affectItems.get(i);
                if (changeable instanceof MPMProcessPlan) {
                	QueryResult qr =  MPMProcessPlanHelper.service.getWTParts((MPMProcessPlan)changeable,NCServerHolder.makeForLatestConfigSpec());
                	if(qr.hasMoreElements()){
                		WTPart p = (WTPart)qr.nextElement();
                		PINDEX = IBAHelper.getIBAStringValue(p, "PINDEX");
                	}
                }
            }
            if (PINDEX == null||"".equals(PINDEX)) {
            	affectItems = ChangeHelper.getChangeResultItem(ecn);
                for (int i = 0; i < affectItems.size(); i++) {
                    Changeable2 changeable = (Changeable2) affectItems.get(i);
                    if (changeable instanceof MPMProcessPlan) {
                    	QueryResult qr =  MPMProcessPlanHelper.service.getWTParts((MPMProcessPlan)changeable,NCServerHolder.makeForLatestConfigSpec());
                    	if(qr.hasMoreElements()){
                    		WTPart p = (WTPart)qr.nextElement();
                    		PINDEX = IBAHelper.getIBAStringValue(p, "PINDEX");
                    	}
                    }
                }
            }
            if (PINDEX == null) {
                PINDEX = "";
            }
            // 阶段标记
            PHASE_CODE = IBAHelper.getIBAStringValue(ecn, "PHASE_CODE");
            if (PHASE_CODE == null) {
                PHASE_CODE = "";
            }
            if (SECRETLEVEL!=null) {
                wfInfoTable.put("SECRETLEVEL", SECRETLEVEL);
            }
            if (PINDEX!=null) {
                wfInfoTable.put("PINDEX", PINDEX);
            }
            if (PHASE_CODE!=null) {
                wfInfoTable.put("PHASE_CODE", PHASE_CODE);
            }
            //wfInfoTable.put("SECRETLEVEL", SECRETLEVEL);
            wfInfoTable.put("ECN_NUMBER", ECN_NUMBER);

            // wfInfoTable.put("RELEASEDEPT", RELEASEDEPT);


        } else if (print_mode == PRINT_JISHUTONGZHIDAN) {
            String NUMBER = "";
            String JDBJ = "";
            // String RELEASEDEPT = "";
            String CHGREASON = "";
            String PRODUCTNUMBER = "";
            String SYMBOL = "";
            String SECRET = "";
            String EFFDATE = "";

            WTDocument doc = (WTDocument) object;

            // 编号 文档 Number NUMBER
            NUMBER = doc.getNumber();
            // 阶段标记 文档 PHASE_CODE JDBJ
            JDBJ = IBAHelper.getIBAStringValue((WTDocument) object, "PHASE_CODE");
            if (JDBJ == null) {
                JDBJ = "";
            }
            // 原因
            CHGREASON = IBAHelper.getIBAStringValue((WTDocument) object, "CHGREASON");
            if (CHGREASON == null) {
                CHGREASON = "";
            }
            // 发往单位
            // Vector<String> releseHistory = ReleaseHelper.getReleaseHistoryVector(doc);
            // for (int i = 0; i < releseHistory.size(); i++) {
            // String strReleaseHistory = releseHistory.get(i);
            // if(strReleaseHistory != null){
            // String[] strReleaseItems = strReleaseHistory.split(";;;qqq");
            // if(strReleaseItems.length > 4){
            // if(RELEASEDEPT.equals("")){
            // RELEASEDEPT = strReleaseItems[0] + "/" + strReleaseItems[2];
            // }else{
            // RELEASEDEPT = RELEASEDEPT + ";" + strReleaseItems[0] + "/" + strReleaseItems[2];
            // }
            // }
            // }
            // }

            // 产品代号
            String containerName = ((WTContained) doc).getContainerName();
            PDMLinkProduct pdmlinkproduct = ASESNumberUtil.getPDMLinkProductByName(containerName);
            PRODUCTNUMBER = IBAHelper.getIBAStringValue(pdmlinkproduct, "PRODUCTNUMBER");

            // 修正往hashtable中放空值的null Pointer exception
            if (PRODUCTNUMBER == null) {
                PRODUCTNUMBER = "";
            }

            // 代号
            SYMBOL = IBAHelper.getIBAStringValue((WTDocument) object, "SYMBOL");
            if (SYMBOL == null) {
                SYMBOL = "";
            }
            // 密级
            SECRET = IBAHelper.getIBAStringValue((WTDocument) object, "SECRET");
            if (SECRET != null && SECRET.indexOf("内部") > -1)
                SECRET = "    内部";
            if (SECRET != null && SECRET.indexOf("秘密") > -1)
                SECRET = "  秘密★10年";
            if (SECRET != null && SECRET.indexOf("机密") > -1)
                SECRET = "  秘密★20年";
            if (SECRET == null || "无".equals(SECRET)) {
                SECRET = "公开";
            }
            // 有效期限
            EFFDATE = IBAHelper.getIBAStringValue((WTDocument) object, "EFFDATE");
            if (EFFDATE == null) {
                EFFDATE = "";
            }
            wfInfoTable.put("NUMBER", NUMBER);
            wfInfoTable.put("JDBJ", JDBJ);
            // wfInfoTable.put("RELEASEDEPT", RELEASEDEPT);
            wfInfoTable.put("CHGREASON", CHGREASON);
            wfInfoTable.put("PRODUCTNUMBER", PRODUCTNUMBER);
            wfInfoTable.put("SYMBOL", SYMBOL);
            wfInfoTable.put("SECRET", SECRET);
            wfInfoTable.put("EFFDATE", EFFDATE);
        } else if (print_mode == PRINT_CHANGEPHASE) {
            String NUMBER = "";
            String JDBJ = "";
            String RELEASEDEPT = "";
            String CHGREASON = "";

            WTDocument doc = (WTDocument) object;

            // 编号 文档 Number NUMBER
            NUMBER = doc.getNumber();
            // 阶段标记 文档 PHASE_CODE JDBJ
            JDBJ = IBAHelper.getIBAStringValue((WTDocument) object, "PHASE_CODE");
            if (JDBJ == null) {
                JDBJ = "";
            }
            // 原因
            CHGREASON = IBAHelper.getIBAStringValue((WTDocument) object, "CHGREASON");
            if (JDBJ == null) {
                JDBJ = "";
            }
            //
            // Vector<String> releseHistory = ReleaseHelper.getReleaseHistoryVector(doc);
            // for (int i = 0; i < releseHistory.size(); i++) {
            // String strReleaseHistory = releseHistory.get(i);
            // if(strReleaseHistory != null){
            // String[] strReleaseItems = strReleaseHistory.split(";;;qqq");
            // if(strReleaseItems.length > 4){
            // if(RELEASEDEPT.equals("")){
            // RELEASEDEPT = strReleaseItems[0] + "/" + strReleaseItems[2];
            // }else{
            // RELEASEDEPT = RELEASEDEPT + ";" + strReleaseItems[0] + "/" + strReleaseItems[2];
            // }
            // }
            // }
            // }
            //
            // wfInfoTable.put("NUMBER", NUMBER);
            // wfInfoTable.put("JDBJ", JDBJ);
            // wfInfoTable.put("RELEASEDEPT", RELEASEDEPT);
            // wfInfoTable.put("CHGREASON", CHGREASON);
        } else if(print_mode == PRINT_JISHUXIEYI) {
            String ECN_CHANGETYPE = "";
            String ECN_NUMBER = "";
            String ECN_MODIFIER = "";
            String ECN_MODIFYTIME = "";
            WTDocument docNow = (WTDocument) object;
            String JDBJ = IBAHelper.getIBAStringValue(docNow, "PHASE_CODE");
            if(JDBJ == null) {
                JDBJ = "";
            }
            jdbjProcess(wfInfoTable, JDBJ);
            //产品代号
            String PINDEX = IBAHelper.getIBAStringValue(docNow, "PINDEX");
            if(PINDEX == null) {
                PINDEX = "";
            }

            String MIJI = IBAHelper.getIBAStringValue(docNow, "SECRET");

            if(MIJI != null && MIJI.indexOf("内部") > -1)
                MIJI = "内部";
            if(MIJI != null && MIJI.indexOf("秘密") > -1)
                MIJI = "秘密★10年";
            if(MIJI != null && MIJI.indexOf("机密") > -1)
                MIJI = "秘密★20年";
            if(MIJI == null || "无".equals(MIJI)) {
                MIJI = "公开";
            }

            // 更改单号 文档 ECN_Number ECN_NUMBER
            // 更改日期 文档 ECN_ModifyTime ECN_MODIFYTIME
            // 更改标记 文档 CHANGETYPE ECN_CHANGETYPE
            QueryResult qrECN = ChangeHelper2.service.getUniqueImplementedChangeOrders(docNow);
            while(qrECN.hasMoreElements()) {
                WTChangeOrder2 ecn = (WTChangeOrder2) qrECN.nextElement();
                String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(ecn).toString();
                if(objectType.indexOf("DOCUMENT_ECN") > 0) {
                    ECN_CHANGETYPE = IBAHelper.getIBAStringValue(ecn,"CHANGETYPE");
                    ECN_NUMBER = ecn.getNumber();
                    ECN_MODIFIER = docNow.getModifierFullName();
                    ECN_MODIFYTIME = WTStandardDateFormat.format(docNow.getModifyTimestamp(), "yyyy-MM-dd");
                }
            }

            String NUMBER = docNow.getNumber();

            wfInfoTable.put("NUMBER", NUMBER);
            wfInfoTable.put("JDBJ", JDBJ);
            wfInfoTable.put("PINDEX", PINDEX);
            wfInfoTable.put("SECRET", MIJI);
            wfInfoTable.put("VERSION_NOW", docNow.getVersionIdentifier().getValue() + "." + docNow.getIterationIdentifier().getValue());
            wfInfoTable.put("ECN_CHANGETYPE", ECN_CHANGETYPE);
            wfInfoTable.put("ECN_NUMBER", ECN_NUMBER);
            wfInfoTable.put("ECN_MODIFIER", ECN_MODIFIER);
            wfInfoTable.put("ECN_MODIFYTIME", ECN_MODIFYTIME);
        } else if(print_mode == PRINT_GONGYIJISHUTONGZHIDAN) {
            String JDBJ = IBAHelper.getIBAStringValue((WTDocument) object, "PHASE_CODE");
            if(JDBJ == null) {
                JDBJ = "";
            }
            if(object instanceof WTDocument) {
                WTDocument processNotice = ((WTDocument) object);
                wfInfoTable.put("NUMBER", processNotice.getNumber());
                processNoticeProcess(wfInfoTable, processNotice, "MINDEX");
                processNoticeProcess(wfInfoTable, processNotice, "PINDEX");
                processNoticeProcess(wfInfoTable, processNotice, "REPLACE");
                processNoticeProcess(wfInfoTable, processNotice, "FAWANGDANWEI");
                processNoticeProcess(wfInfoTable, processNotice, "TONGZHIBIAOTI");
                processNoticeProcess(wfInfoTable, processNotice, "CHGREASON");
                //processNoticeProcess(wfInfoTable, processNotice,"EFFDATE");
                String EFFDATE = IBAHelper.getIBAValueOfObject(processNotice, "EFFDATE");
                if(EFFDATE != null && !"".equals(EFFDATE)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date d = sdf.parse(EFFDATE);
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
                    String v = sdf2.format(d);
                    wfInfoTable.put("EFFDATE", v);
                } else {
                    EFFDATE = IBAHelper.getIBAValueOfObject(processNotice, "EFFDATE2");
                    if(EFFDATE == null) EFFDATE = "";
                    wfInfoTable.put("EFFDATE", EFFDATE);
                }

                String MIJI = IBAHelper.getIBAStringValue((WTDocument) object, "SECRET");
                if(MIJI != null && MIJI.indexOf("内部") > -1)
                    MIJI = "内部";
                if(MIJI != null && MIJI.indexOf("秘密") > -1)
                    MIJI = "秘密★10年";
                if(MIJI != null && MIJI.indexOf("机密") > -1)
                    MIJI = "秘密★20年";
                if(MIJI == null || "无".equals(MIJI)) {
                    MIJI = "公开";
                }
                wfInfoTable.put("SECRET", MIJI);

                //1486378114201_Z.2_电装工艺规程(Rz/2Z05(13213213))
                String PROCESSDOCNUM = "";
                try {
                    QuerySpec qs = new QuerySpec(WTDocumentDependencyLink.class);
                    qs.appendWhere(new SearchCondition(WTDocumentDependencyLink.class, "roleAObjectRef.key.id", "=",
                            PersistenceHelper.getObjectIdentifier(processNotice).getId()), new int[1]);
                    qs.setAdvancedQueryEnabled(true);
                    QueryResult qr = PersistenceHelper.manager.find(qs);
                    while(qr.hasMoreElements()) {
                        WTDocumentDependencyLink link = (WTDocumentDependencyLink) qr.nextElement();
                        WTDocument document = (WTDocument) link.getRoleBObject();
                        PROCESSDOCNUM += document.getNumber() + "_" + document.getName() + "_" + document.getIterationDisplayIdentifier().toString() + ";";
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
                if(PROCESSDOCNUM != null && !"".equals(PROCESSDOCNUM)) {
                    wfInfoTable.put("PROCESSDOCNUM", " 受影响工艺规程：" + PROCESSDOCNUM);

                   /* String[] ss = PROCESSDOCNUM.split("_");
                    if(ss.length>2){
                        String docNumber= ss[0];
                        String docVersion = ss[1];
                        String[] vv = docVersion.split(".");
                        if(vv.length==2){
                            String v1 = vv[0];
                            String v2 = vv[1];
                             WTDocument doc = (WTDocument) CmExpImpSearchHelper.searchIteratedByNumberVersionIteration(WTDocument.class,docNumber,v1,v2);
                        }
                    }*/
                }

            }
            jdbjProcess(wfInfoTable, JDBJ);

        } else if(print_mode == PRINT_GYFA) {
            String JDBJ = IBAHelper.getIBAStringValue((WTDocument) object, "PHASE_CODE");
            if(JDBJ == null) {
                JDBJ = "";
            }
            jdbjProcess(wfInfoTable, JDBJ);
            //产品代号
            String PINDEX = IBAHelper.getIBAStringValue((WTDocument) object, "PINDEX");
            if(PINDEX == null) {
                PINDEX = "";
            }

            String MIJI = IBAHelper.getIBAStringValue((WTDocument) object, "SECRET");

            if(MIJI != null && MIJI.indexOf("内部") > -1)
                MIJI = "内部";
            if(MIJI != null && MIJI.indexOf("秘密") > -1)
                MIJI = "秘密★10年";
            if(MIJI != null && MIJI.indexOf("机密") > -1)
                MIJI = "秘密★20年";
            if(MIJI == null || "无".equals(MIJI)) {
                MIJI = "公开";
            }

            String NUMBER = ((WTDocument) object).getNumber();

            wfInfoTable.put("NUMBER", NUMBER);
            wfInfoTable.put("NAME", ((WTDocument) object).getName());
            wfInfoTable.put("JDBJ", JDBJ);
            wfInfoTable.put("PINDEX", PINDEX);
            wfInfoTable.put("SECRET", MIJI);
            wfInfoTable.put("VERSIONITE", ((WTDocument) object).getIterationDisplayIdentifier().toString());
        } else if(print_mode == PRINT_GYZFA) {
            WTDocument doc = (WTDocument) object;
            String ECN_NUMBER = "";
            String JDBJ = IBAHelper.getIBAStringValue(doc, "PHASE_CODE");
            //产品代号
            String PINDEX = IBAHelper.getIBAStringValue((WTDocument) object, "PINDEX");
            if(PINDEX == null) {
                PINDEX = "";
            }

            String MIJI = IBAHelper.getIBAStringValue((WTDocument) object, "SECRET");

            if(MIJI != null && MIJI.indexOf("内部") > -1)
                MIJI = "内部";
            if(MIJI != null && MIJI.indexOf("秘密") > -1)
                MIJI = "秘密★10年";
            if(MIJI != null && MIJI.indexOf("机密") > -1)
                MIJI = "秘密★20年";
            if(MIJI == null || "无".equals(MIJI)) {
                MIJI = "公开";
            }

            // 更改单号 文档 ECN_Number ECN_NUMBER
            QueryResult qrECN = ChangeHelper2.service.getUniqueImplementedChangeOrders(doc);
            while(qrECN.hasMoreElements()) {
                WTChangeOrder2 ecn = (WTChangeOrder2) qrECN.nextElement();
                String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(ecn).toString();
                if(objectType.indexOf("DOCUMENT_ECN") > 0) {
                    ECN_NUMBER = ecn.getNumber();
                }
            }
            wfInfoTable.put("NUMBER", doc.getNumber());
            wfInfoTable.put("NAME", doc.getName());
            wfInfoTable.put("JDBJ", JDBJ);
            wfInfoTable.put("PINDEX", PINDEX);
            wfInfoTable.put("SECRET", MIJI);
            wfInfoTable.put("VERSIONITE", ((WTDocument) object).getIterationDisplayIdentifier().toString());
            wfInfoTable.put("ECN_NUMBER", ECN_NUMBER);


        }
        return wfInfoTable;
    }
    private static void processNoticeProcess(Hashtable wfInfoTable, WTDocument processNotice, String key) throws WTException {
    	String value = IBAHelper.getIBAStringValue(processNotice, key);
    	if(value!=null)
    		wfInfoTable.put(key, value);
	}

	public static void jdbjProcess(Hashtable wfInfoTable ,String JDBJ){
    	 if (JDBJ.startsWith("M") || JDBJ.startsWith("Y")) {
             wfInfoTable.put("JDBJ1", JDBJ);
             wfInfoTable.put("JDBJ2", "  ");
             wfInfoTable.put("JDBJ3", "  ");
             wfInfoTable.put("JDBJ4", "  ");
         } else if (JDBJ.startsWith("C")) {
             wfInfoTable.put("JDBJ1", "  ");
             wfInfoTable.put("JDBJ2", JDBJ);
             wfInfoTable.put("JDBJ3", "  ");
             wfInfoTable.put("JDBJ4", "  ");
         } else if (JDBJ.startsWith("S") || JDBJ.startsWith("Z")) {
             wfInfoTable.put("JDBJ1", "  ");
             wfInfoTable.put("JDBJ2", "  ");
             wfInfoTable.put("JDBJ3", JDBJ);
             wfInfoTable.put("JDBJ4", "  ");
         } else if (JDBJ.startsWith("D")) {
             wfInfoTable.put("JDBJ1", "  ");
             wfInfoTable.put("JDBJ2", "  ");
             wfInfoTable.put("JDBJ3", "  ");
             wfInfoTable.put("JDBJ4", JDBJ);
         }else if (JDBJ.startsWith("G")) {
             wfInfoTable.put("JDBJ1", "  ");
             wfInfoTable.put("JDBJ2", "  ");
             wfInfoTable.put("JDBJ3", "  ");
             wfInfoTable.put("JDBJ5", JDBJ);
         } else if (JDBJ.startsWith("P")) {
             wfInfoTable.put("JDBJ1", "  ");
             wfInfoTable.put("JDBJ2", "  ");
             wfInfoTable.put("JDBJ3", "  ");
             wfInfoTable.put("JDBJ6", JDBJ);
         }
    }
    public  static String getoutSignValues(String signValues, Object object,String endtime) throws WTException {
    	String str = "";
    	if(signValues != null){

    		String oid  = PersistenceHelper.getObjectIdentifier((Persistable) object).toString();
			String signInfo = getSignVlue(signValues,oid);
    		if(signInfo.contains(";")){
    			String[] allSigns = signInfo.split(";");
    			for(String ss: allSigns){
    				if(ss.contains(":")){
        				String[] signs = ss.split(":");
        				String department = " ";
        				if(signs.length > 1){
        					department = signs[1];
        				}
        				if(str.equals("")){
        					str = department + "/" + signs[0]+ "/" + endtime;
        				}else{
        					str = str+";"+department + "/" + signs[0]+ "/" + endtime;
        				}
        			}
    			}
    		}else{
    			if(signInfo.contains(":")){
    				String[] signs = signInfo.split(":");
    				str = signs[1] + "/" + signs[0]+ "/" + endtime;
    			}
    		}
		}
    	return str;
	}

	private String getZZCJ( Object pbo,Object object){
    	String zzcj = null;
     	SignatureGYZZXMLParser parser =  new SignatureGYZZXMLParser((ContentHolder)pbo);
     	String oid = PersistenceHelper.getObjectIdentifier((Persistable) object).toString();
     	SignatureRecord record = parser.getMap3().get(oid);
     	if(record!=null)
     		zzcj = record.getZhuzhichejian();
     	if(zzcj!=null&&!"".equals(zzcj)){
     		String[] zzs = zzcj.split("-");
     		zzcj = zzs[0];
     	}

     	if("1".equals(zzcj)){
			zzcj = "一车间";
     	}else if("2".equals(zzcj)){
			zzcj = "二车间";
     	}else if("3".equals(zzcj)){
			zzcj = "三车间";
     	}else if("4".equals(zzcj)){
			zzcj = "四车间";
     	}else if("5".equals(zzcj)){
			zzcj = "五车间";
     	}else if("6".equals(zzcj)){
			zzcj = "六车间";
     	}else if("7".equals(zzcj)){
			zzcj = "七车间";
     	}else if("8".equals(zzcj)){
			zzcj = "八车间";
     	}else if("项".equals(zzcj)){
			zzcj = "项目部";
     	}
     	return zzcj;
    }

    public static List getActivities(WfProcess wfprocess, List activityList) throws WTException {
        Enumeration enumeration = WfEngineHelper.service.getProcessSteps(wfprocess, null);
        while (enumeration.hasMoreElements()) {
            WfActivity wfactivity = (WfActivity) enumeration.nextElement();
            if (wfactivity instanceof WfAssignedActivity) {
                WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
                activityList.add(wfassignedactivity);
            }
        }
        return activityList;
    }

    // ywu 2011.3.13
    public static WfAssignedActivity getLatestCompleteActivity(WfProcess wfprocess, List activityList,boolean isHasWfBlock)
            throws Exception {
    	List<WfBlock> allWfBlocks =  new ArrayList<WfBlock>();
    	if(isHasWfBlock){
    		allWfBlocks = PrintHelper.getAllBlock(wfprocess);
    	}

        Class class1 = WfAssignedActivity.class;
        QuerySpec qs = new QuerySpec();
        int activityIndx = qs.appendClassList(WfAssignedActivity.class, true);
        long[] blockAndProcessIds = new long[allWfBlocks.size()+1];
        long id = wfprocess.getPersistInfo().getObjectIdentifier().getId();
        blockAndProcessIds[0] = id;
        for(int i=0;i<allWfBlocks.size();i++){
        	long blockid = allWfBlocks.get(i).getPersistInfo().getObjectIdentifier().getId();
        	blockAndProcessIds[i+1] = blockid;
        }
        ClassAttribute qs_ca1 = new ClassAttribute(
        		class1,  "parentProcessRef.key.id");
		 SearchCondition qs_sc1 = new SearchCondition(qs_ca1,
				 SearchCondition.IN,
				 new ArrayExpression(blockAndProcessIds));
		qs.appendWhere(qs_sc1, new int[]{0});

		/*
		 * qs.appendAnd();
		ClassAttribute qs_ca2 = new ClassAttribute(
        		class1,  "parentProcessRef.key.id");
		 SearchCondition qs_sc2 = new SearchCondition(qs_ca1,
				 SearchCondition.IN,
				 new ArrayExpression(blockAndProcessIds));
		 qs.appendWhere(qs_sc2, new int[]{0});
		 */
        ClassAttribute clsAttr = new ClassAttribute(WfAssignedActivity.class, WfAssignedActivity.END_TIME);
        OrderBy order = new OrderBy((OrderByExpression) clsAttr, true);
        qs.appendOrderBy(order);

        QueryResult qr = PersistenceHelper.manager.find(qs);

        while (qr.hasMoreElements()) {
            Object obj = qr.nextElement();
            if (obj instanceof Persistable[]) {
                Persistable[] ps = (Persistable[]) obj;

                if (ps[0] instanceof WfAssignedActivity){
                	WfAssignedActivity activity = (WfAssignedActivity) ps[0];
                	return activity;
                }

            }else{
            	Persistable p = (Persistable) obj;
                return (WfAssignedActivity) p;
            }
        }
        return null;

    }

    public static WfAssignedActivity getLatestCompleteActivity(WfProcess wfprocess, List activityList)
            throws Exception {
    	List<WfBlock> allWfBlocks =  new ArrayList<WfBlock>();
    	allWfBlocks = PrintHelper.getAllBlock(wfprocess);
        Class class1 = WfAssignedActivity.class;
        QuerySpec qs = new QuerySpec();
        int activityIndx = qs.appendClassList(WfAssignedActivity.class, true);
        long[] blockAndProcessIds = new long[allWfBlocks.size()+1];
        long id = wfprocess.getPersistInfo().getObjectIdentifier().getId();
        blockAndProcessIds[0] = id;
        for(int i=0;i<allWfBlocks.size();i++){
        	long blockid = allWfBlocks.get(i).getPersistInfo().getObjectIdentifier().getId();
        	blockAndProcessIds[i+1] = blockid;
        }
        ClassAttribute qs_ca1 = new ClassAttribute(
        		class1,  "parentProcessRef.key.id");
		 SearchCondition qs_sc1 = new SearchCondition(qs_ca1,
				 SearchCondition.IN,
				 new ArrayExpression(blockAndProcessIds));
		qs.appendWhere(qs_sc1, new int[]{0});

		/*
		 * qs.appendAnd();
		ClassAttribute qs_ca2 = new ClassAttribute(
        		class1,  "parentProcessRef.key.id");
		 SearchCondition qs_sc2 = new SearchCondition(qs_ca1,
				 SearchCondition.IN,
				 new ArrayExpression(blockAndProcessIds));
		 qs.appendWhere(qs_sc2, new int[]{0});
		 */
        ClassAttribute clsAttr = new ClassAttribute(WfAssignedActivity.class, WfAssignedActivity.END_TIME);
        OrderBy order = new OrderBy((OrderByExpression) clsAttr, true);
        qs.appendOrderBy(order);

        QueryResult qr = PersistenceHelper.manager.find(qs);

        while (qr.hasMoreElements()) {
            Object obj = qr.nextElement();
            if (obj instanceof Persistable[]) {
                Persistable[] ps = (Persistable[]) obj;

                if (ps[0] instanceof WfAssignedActivity){
                	WfAssignedActivity activity = (WfAssignedActivity) ps[0];
                	return activity;
                }

            }else{
            	Persistable p = (Persistable) obj;
                return (WfAssignedActivity) p;
            }
        }
        return null;

    }

    public static List getActivities(WfBlock wfblock, List activityList) throws WTException {
        Enumeration enumeration = WfEngineHelper.service.getProcessSteps(wfblock, null);
        while (enumeration.hasMoreElements()) {
            WfActivity wfactivity = (WfActivity) enumeration.nextElement();
            if (wfactivity instanceof WfAssignedActivity) {
                WfAssignedActivity wfassignedactivity = (WfAssignedActivity) wfactivity;
                activityList.add(wfassignedactivity);
            }
        }
        return activityList;
    }

    public static QueryResult getWorkItems(WfProcess wfprocess, boolean flag) throws Exception {
        QueryResult queryresult;
        QuerySpec queryspec = new QuerySpec();
        queryspec.setAdvancedQueryEnabled(true);
        int i = queryspec.appendClassList(WfAssignedActivity.class, false);
        queryspec.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", i, false);
        queryspec.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key", "=",
                getOid(wfprocess)));
        SubSelectExpression subselectexpression = new SubSelectExpression(queryspec);
        QuerySpec queryspec1 = new QuerySpec(WorkItem.class);
        queryspec1.setAdvancedQueryEnabled(true);
        if (flag)
            queryspec1.appendWhere(new SearchCondition(WorkItem.class, "completedBy", !flag), 0);
        ClassAttribute classattribute = new ClassAttribute(WorkItem.class, "source.key.id");
        if (flag)
            queryspec1.appendAnd();
        SearchCondition searchcondition = new SearchCondition(classattribute, "IN", subselectexpression);
        queryspec1.appendWhere(searchcondition, 0);
        TableColumn ca = new TableColumn("A0", "createstampa2");
        OrderBy orderBy = new OrderBy(ca, false);
        queryspec1.appendOrderBy(orderBy, new int[0]);
        queryresult = PersistenceServerHelper.manager.query(queryspec1);
        return queryresult;

    }

    public static QueryResult getWorkItems(WfBlock wfblock, boolean flag) throws Exception {
        QueryResult queryresult;
        QuerySpec queryspec = new QuerySpec();
        queryspec.setAdvancedQueryEnabled(true);
        int i = queryspec.appendClassList(WfAssignedActivity.class, false);
        queryspec.appendSelectAttribute("thePersistInfo.theObjectIdentifier.id", i, false);
        queryspec.appendWhere(new SearchCondition(WfAssignedActivity.class, "parentProcessRef.key", "=",
                getOid(wfblock)));
        SubSelectExpression subselectexpression = new SubSelectExpression(queryspec);
        QuerySpec queryspec1 = new QuerySpec(WorkItem.class);
        queryspec1.setAdvancedQueryEnabled(true);
        if (flag)
            queryspec1.appendWhere(new SearchCondition(WorkItem.class, "completedBy", !flag), 0);
        ClassAttribute classattribute = new ClassAttribute(WorkItem.class, "source.key.id");
        if (flag)
            queryspec1.appendAnd();
        SearchCondition searchcondition = new SearchCondition(classattribute, "IN", subselectexpression);
        queryspec1.appendWhere(searchcondition, 0);
        TableColumn ca = new TableColumn("A0", "createstampa2");
        OrderBy orderBy = new OrderBy(ca, false);
        queryspec1.appendOrderBy(orderBy, new int[0]);
        queryresult = PersistenceServerHelper.manager.query(queryspec1);
        return queryresult;

    }


    public static List<WfBlock> getAllBlock(WfProcess wfprocess) throws Exception {
        List<WfBlock> allWfBlocks = new ArrayList<WfBlock>();
        QuerySpec queryspec = new QuerySpec(WfRequesterActivity.class);
        queryspec.appendWhere(new SearchCondition(WfRequesterActivity.class, "parentProcessRef.key.id",SearchCondition.EQUAL, wfprocess.getPersistInfo().getObjectIdentifier().getId()), 0);
        QueryResult queryresult = PersistenceServerHelper.manager.query(queryspec);
        while (queryresult.hasMoreElements()) {
        	WfRequesterActivity ra = (WfRequesterActivity) queryresult.nextElement();
        	Object pr = ra.getPerformerRef().getObject();
        	if(pr instanceof WfBlock){
        		allWfBlocks.add((WfBlock)pr);
        	}
        }
        return allWfBlocks;
    }

    private static ObjectIdentifier getOid(Object obj) {
        if (obj == null)
            return null;
        if (obj instanceof ObjectReference)
            return (ObjectIdentifier) ((ObjectReference) obj).getKey();
        else return PersistenceHelper.getObjectIdentifier((Persistable) obj);
    }

    public static String getPrincipalName(WfActivity wfa,Object pbo,Object obj2) throws Exception {
        String field = "";
        String str = "";
        Enumeration en1 = null;
        Enumeration en2 = null;
        en1 = ((WfAssignedActivity) wfa).getAssignments();

        for (int i = 0; en1 != null && en1.hasMoreElements(); i++) {
            WfAssignment wfassignment = (WfAssignment) en1.nextElement();
            en2 = wfassignment.checkBallotStatus().elements();
            for (int j = 0; en2 != null && en2.hasMoreElements(); j++) {
                WfBallot wfballot = (WfBallot) en2.nextElement();
                WTPrincipal wtp = wfballot.getVoter().getPrincipal();

                String endtime = "";
                if (wfassignment.getModifyTimestamp() != null) {
                    endtime = WTStandardDateFormat.format(wfassignment.getModifyTimestamp(), "yyyy-MM-dd");
                }

                if (wtp instanceof WTUser){
                    if ("外部会签".equals(wfa.getName())&&(processTemps.contains(wfa.getParentProcess().getTemplate().getName())
                    		||Constants.WFN_PROCESS_DOCUMENT.equals(wfa.getParentProcess().getTemplate().getName()))) {
                    	ProcessData processData = wfa.getContext();
                        ArrayList outSignList = (ArrayList)processData.getValue("outSignInfo");
                        String tempStr = "";
                        if(outSignList!=null){
                        	for(int k = 0; k < outSignList.size(); k++){
                        		String s = "";
            	           		NmOid oid = (NmOid)outSignList.get(k);
            	           		if(oid!=null){
            	           			HashMap map = oid.getAdditionalInfo();//signName/signCompany/signDate/signRemark
            	           			if(map!=null){
            	           				s = map.get("signName")+"/"+map.get("signCompany")+"/"+map.get("signDate");
                    	           		tempStr = tempStr + s + ";";
            	           			}
            	           		}

                        	}
                        }
                        str = tempStr;
                    }  else if ("外部会签".equals(wfa.getName())&&
                    		(Constants.WFN_PROCESS_ECN.equals(wfa.getParentProcess().getTemplate().getName())
                    		||Constants.WFN_WUJIPROCESSWF.equals(wfa.getParentProcess().getTemplate().getName())
                    		||Constants.WFN_WUJIREPORTPROCESSWF.equals(wfa.getParentProcess().getTemplate().getName()))) {
                    	String signValues = (String) TaskConfigrationHelper.getProcessVariableValue(wfa.getParentProcess(), "outSignInfos");
//                    	Object obj = TaskConfigrationHelper.getActivityVariableValue(wfa, "primaryBusinessObject");

//                    	if(obj instanceof WTDocument){
//                    		WTDocument doc = (WTDocument) obj;
                    		String oid  = PersistenceHelper.getObjectIdentifier((Persistable) obj2).toString();
                    		if(pbo instanceof WTChangeOrder2){
                    			WTChangeOrder2 changeOrder2 = (WTChangeOrder2) pbo;
                				WTObject targetObj = getReleatedDocByECN((WTChangeOrder2) pbo);
                				if(targetObj != null){
                					oid  = PersistenceHelper.getObjectIdentifier((Persistable) targetObj).toString();
                				}else{
                    			    QueryResult qResult = ChangeHelper2.service.getChangeablesAfter(changeOrder2);
                                      while (qResult.hasMoreElements()) {
                                           Object object = qResult.nextElement();
                                           if(object instanceof Persistable){
                                    	      oid  = PersistenceHelper.getObjectIdentifier((Persistable) object).toString();
                                    	    break;
                                     }
                                 }
                				}
                    		}
                    		if(signValues != null){
                    			String signInfo = getSignVlue(signValues,oid);
                        		if(signInfo.contains(";")){
                        			String[] allSigns = signInfo.split(";");
                        			for(String ss: allSigns){
                        				if(ss.contains(":")){
                            				String[] signs = ss.split(":");
                            				String department = " ";
                            				if(signs.length > 1){
                            					department = signs[1];
                            				}
                            				if(str.equals("")){
                            					str = department + "/" + signs[0]+ "/" + endtime;
                            				}else{
                            					str = str+";"+department + "/" + signs[0]+ "/" + endtime;
                            				}
                            			}
                        			}
                        		}else{
                        			if(signInfo.contains(":")){
                        				String[] signs = signInfo.split(":");
                        				if(signs.length>1){
                                            str = signs[1] + "/" + signs[0]+ "/" + endtime;
                                        }
                        			}
                        		}
                    		}

//                    	}
                        System.out.println("工艺文件更改单>>>>>>>>>>signValues:"+signValues+"   obj:"+obj2+"---"+str);;
                    }else  if ("外部会签".equals(wfa.getName())&&
                    		!Constants.WFN_PROCESS_DOCUMENT.equals(wfa.getParentProcess().getTemplate().getName())) {
                        String proxy = (String)TaskConfigrationHelper.getActivityVariableValue(wfa,"proxy");
                        String department = (String)TaskConfigrationHelper.getActivityVariableValue(wfa,"proxyDepartment");
                        System.out.println(">>>>>>>>>>proxy:"+proxy+"   department:"+department);
                        str = proxy + "/" + department+ "/" + endtime;
                    }  else {
                        str = ((WTUser) wtp).getFullName().toString() + "/" + getUserDepartment(((WTUser) wtp).getName()) + "/" + endtime;
                    }
                } else if (wtp instanceof WTGroup){
                    str = ((WTGroup) wtp).getName().toString();
                }
                if (str != null && str.length() > 0) {
                    if (field == ""){
                        field = str;
                    } else {
                        field = field + ";" + str;
                    }
                }
            }
        }
        return field;
    }

    public static String getPrincipalName(WfActivity wfa) throws Exception {
        String field = "";
        String str = "";
        Enumeration en1 = null;
        Enumeration en2 = null;
        en1 = ((WfAssignedActivity) wfa).getAssignments();

        for (int i = 0; en1 != null && en1.hasMoreElements(); i++) {
            WfAssignment wfassignment = (WfAssignment) en1.nextElement();
            en2 = wfassignment.checkBallotStatus().elements();
            for (int j = 0; en2 != null && en2.hasMoreElements(); j++) {
                WfBallot wfballot = (WfBallot) en2.nextElement();
                WTPrincipal wtp = wfballot.getVoter().getPrincipal();

                String endtime = "";
                if (wfassignment.getModifyTimestamp() != null) {
                    endtime = WTStandardDateFormat.format(wfassignment.getModifyTimestamp(), "yyyy-MM-dd");
                }

                if (wtp instanceof WTUser){
                    if ("外部会签".equals(wfa.getName())&&!processTemps.contains(wfa.getParentProcess().getTemplate().getName())) {
                        String proxy = (String)TaskConfigrationHelper.getActivityVariableValue(wfa,"proxy");
                        String department = (String)TaskConfigrationHelper.getActivityVariableValue(wfa,"proxyDepartment");
                        System.out.println(">>>>>>>>>>proxy:"+proxy+"   department:"+department);
                        str = proxy + "/" + department+ "/" + endtime;
                    } else if ("外部会签".equals(wfa.getName())
                    	&&(processTemps.contains(wfa.getParentProcess().getTemplate().getName())
                    			||Constants.WFN_PROCESS_DOCUMENT.equals(wfa.getParentProcess().getTemplate().getName()))) {
                    	ProcessData processData = wfa.getContext();
                        ArrayList outSignList = (ArrayList)processData.getValue("outSignInfo");
                        String tempStr = "";
                        if(outSignList!=null){
                        	for(int k = 0; k < outSignList.size(); k++){
                        		String s = "";
            	           		NmOid oid = (NmOid)outSignList.get(k);
            	           		HashMap map = oid.getAdditionalInfo();//signName/signCompany/signDate/signRemark
            	           		s = map.get("signName")+"/"+map.get("signCompany")+"/"+map.get("signDate");
            	           		tempStr = tempStr + s + ";";
                        	}
                        }
                        str = tempStr;
                    } else {
                        str = ((WTUser) wtp).getFullName().toString() + "/" + getUserDepartment(((WTUser) wtp).getName()) + "/" + endtime;
                    }
                } else if (wtp instanceof WTGroup){
                    str = ((WTGroup) wtp).getName().toString();
                }
                if (str != null && str.length() > 0) {
                    if (field == ""){
                        field = str;
                    } else {
                        field = field + ";" + str;
                    }
                }
            }
        }
        return field;
    }

    private static String getSignVlue(String values, String oid) throws WTException{
    		String value = values;
		 String[] data = values.split("@");
	        for (String oneValue : data) {
	            if (oneValue != null && !"".equals(oneValue)) {
	                String[] str = oneValue.split("~");
	                String objNumber = str[0];
	                Persistable per = WCUtil.getPersistable(oid);
                   String number = "";
                   if(per instanceof WTDocument){
                   	WTDocument doc = (WTDocument) per;
                   	number = doc.getNumber();
                   }
	                if(objNumber.equals(number)){
	                	value = str[1];
	                	break;
	                }
	            }
	        }
	        return value;
	}

	public static String getOnlyPrincipalName(WfActivity wfa) throws Exception {
        String field = "";
        String str = "";
        Enumeration en1 = null;
        Enumeration en2 = null;
        en1 = ((WfAssignedActivity) wfa).getAssignments();

        for (int i = 0; en1 != null && en1.hasMoreElements(); i++) {
            WfAssignment wfassignment = (WfAssignment) en1.nextElement();
            en2 = wfassignment.checkBallotStatus().elements();
            for (int j = 0; en2 != null && en2.hasMoreElements(); j++) {
                WfBallot wfballot = (WfBallot) en2.nextElement();
                WTPrincipal wtp = wfballot.getVoter().getPrincipal();

                if (wtp instanceof WTUser)
                    str = ((WTUser) wtp).getFullName().toString();
                else if (wtp instanceof WTGroup)
                    str = ((WTGroup) wtp).getName().toString();
                if (str != null && str.length() > 0) {
                    if (field == "")
                        field = str;
                    else field = field + "," + str;
                }
            }
        }
        return field;
    }

    public static String getUserDepartment(String username) throws WTException {
        String result = "";
        WTUser user = CSCPrincipal.getUserByName(username);
        if (user == null) {
            log.debug("No users anymore!");
            return "";
        }

        Enumeration groups = user.parentGroupNames();
        while (groups.hasMoreElements()) {
            String gname = (String) groups.nextElement();
            if (gname.indexOf("部门") > -1&&gname.indexOf("分厂") > -1 && gname.indexOf("工艺组长") < 0){
                return gname.substring(3);
            }else if (gname.indexOf("部门") > -1 && gname.indexOf("工艺组长") < 0){
            	return gname.substring(3);
        	}
        }
        return "";
    }

    public static void main(String[] args) throws Exception {
        /*
         * WfProcess wf = CSCProcess.getProcessByName("ASES_ECN流程_00081");
         * WTDocument doc = CSCDoc.getDoc("测试图样0008");
         *
         * Hashtable ht = PrintHelper.getWFInfo(doc, wf);
         * Set set = ht.keySet();
         * Iterator iter = set.iterator();
         * while(iter.hasNext()){
         * String key = (String)iter.next();
         * String value = (String)ht.get(key);
         * log.debug("key = " + key);
         * log.debug("value = " + value);
         * }
         */

        log.debug(getUserDepartment(args[0]));
    }


    public static WTObject getReleatedDocByECN(WTChangeOrder2 obj) throws WTException{
    	WTObject returnObj = null;
		try {
			String ecnType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(obj);
			if(ecnType.contains("|")){
				ecnType = ecnType.substring(ecnType.lastIndexOf("|")+1,ecnType.length());
		    }
			if(!ecnType.equals("casc.sast.149.PROCESS_ECN")&&!ecnType.equals("casc.sast.149.Process_reportTechnics_ECN") ){
				return returnObj;
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
    	if(obj instanceof WTChangeOrder2) {
			try {
				WTChangeOrder2 ecn = (WTChangeOrder2)obj;
				QueryResult qResult = ChangeHelper2.service.getChangeablesBefore(ecn);
				while (qResult.hasMoreElements()) {
					Object object = qResult.nextElement();
					if (object instanceof MPMProcessPlan) {
						MPMProcessPlan pplan = (MPMProcessPlan)object;
						String number = pplan.getNumber();
						QueryResult qr = MPMProcessPlanHelper.service.getWTParts(pplan, NCServerHolder.makeForLatestConfigSpec());
						if(qr.hasMoreElements()) {
							WTPart part = (WTPart)qr.nextElement();
							QueryResult allIter = VersionControlHelper.service.allIterationsOf(part.getMaster());
							while(allIter.hasMoreElements()) {
								part = (WTPart)allIter.nextElement();
//								QueryResult qr2 = WTPartHelper.service.getDescribedByWTDocuments(part, true);
								QueryResult qr2 = getDescrptionDocByPart(part);
						        while (qr2.hasMoreElements()) {
						        	Object[] qObj = (Object[])qr2.nextElement();
//						        	WTDocument doc = (WTDocument) qObj[0];
						            WTDocument document = (WTDocument) qObj[0];
//						            String version1 = document.getVersionIdentifier().getValue()+"."+document.getIterationIdentifier().getValue();
						            String typeName = TypedUtility.getTypeIdentifier(document).getTypename();
						            if (typeName.contains("PROCESSPLAN")||typeName.contains("reportTechnics")) {
						            	String docNumber = document.getNumber();
						            	if(docNumber.equals(number)) {
						            		returnObj = document;
						            		return document;
						            	}
						            }
						        }
							}
						}else{
	        				//处理工艺实例化和零件没关联的bug，历史数据
	        				WTDocument doc = CSCDoc.getDoc(number);
	        				if(doc!=null){
	        					QueryResult partqr = WTPartHelper.service.getDescribesWTParts(doc);
	        			        if (partqr.hasMoreElements()) {
	        			            WTPart wtPart = (WTPart) partqr.nextElement();
	        			            String version = wtPart.getVersionIdentifier().getValue();
	        						WTPart part = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class,wtPart.getNumber(),version,wtPart.getViewName());
	        						try {
										MPMProcessPlanUtil.createMPMPartToProcessPlanLink(part, pplan);
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

	        			        }
	        			        returnObj = doc;
			            		return doc;
	        				}

	        			}
					}
				}
			} catch (ChangeException2 e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (PersistenceException e) {
				e.printStackTrace();
				throw new WTException(e);
			} catch (WTException e) {
				e.printStackTrace();
				throw e;
			}
		}
    	return returnObj;
    }


    public static WTChangeOrder2 getReleatedECNByDoc(WTDocument doc) throws WTException{
    	try {
    		QuerySpec qSpec = new QuerySpec(MPMProcessPlan.class);
			int[] index = { 0 };
	         SearchCondition sCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER,SearchCondition.EQUAL, doc.getNumber());
	         qSpec.appendWhere(sCondition, index);
	         QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
	         //LatestConfigSpec lcs = new LatestConfigSpec();
	        // qResult = lcs.process(qResult);
	         if (qResult.hasMoreElements()) {
	        	  MPMProcessPlan plan = (MPMProcessPlan) qResult.nextElement();

	        	  QueryResult allPlans = VersionControlHelper.service.allIterationsOf(plan.getMaster());

	        	  while(allPlans.hasMoreElements()){
	        		  MPMProcessPlan pp = (MPMProcessPlan) allPlans.nextElement();
		        	  QueryResult qr2 = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices( pp);
		        	  if (qr2.hasMoreElements()) {
		        		  WTChangeOrder2 ecn = (WTChangeOrder2) qr2.nextElement();
		        		  return ecn;
		        	  }
	        	  }
	         }

		} catch (wt.query.QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    public static QueryResult getDescrptionDocByPart(WTPart part) throws WTException {
    	Long ida3a4 =  PersistenceHelper.getObjectIdentifier(part).getId();
    	QuerySpec qSpec = new QuerySpec();
    	int index0 = qSpec.appendClassList(WTDocument.class, true);
        int index1 = qSpec.addClassList(WTPartDescribeLink.class, false);
        String[] aliases = new String[2];
        aliases[0] = qSpec.getFromClause().getAliasAt(index0);
        aliases[1] = qSpec.getFromClause().getAliasAt(index1);

        TableColumn tc0 = new TableColumn(aliases[0], "ida2a2");
//        TableColumn tc1 = new TableColumn(aliases[1], "ida3a4");
        TableColumn tc2 = new TableColumn(aliases[1], "ida3b5");
        qSpec.appendWhere(new SearchCondition(tc0, "=", tc2), new int[] { index0, index1 });

        qSpec.appendAnd();
        qSpec.setAdvancedQueryEnabled(true);
        SearchCondition searchcondition = new SearchCondition(WTPartDescribeLink.class, "roleAObjectRef.key.id", "=", ida3a4);
        qSpec.appendWhere(searchcondition,new int[] {index1});
//        TableColumn ca = new TableColumn("A1", "createstampa2");
//        OrderBy orderBy = new OrderBy(ca, false);
//        qSpec.appendOrderBy(orderBy, new int[0]);
        qSpec.appendOrderBy(new OrderBy(new ClassAttribute(WTPartDescribeLink.class,WTPartDescribeLink.CREATE_TIMESTAMP), true), new int[] {index1});
        QueryResult queryresult = PersistenceServerHelper.manager.query(qSpec);
        return queryresult;

    }

	public static Hashtable getPrePrintInfo(Object pbo, Object self) throws Exception {
		Hashtable printTable = new Hashtable();
        boolean isChangePhaseECN = false;
        if (pbo instanceof WTChangeOrder2) {
            String type = TypeIdentifierUtilityHelper.service.getTypeIdentifier(pbo).toString();
            if (type.indexOf("CHANGEPHASE_ECN") > -1) {
                isChangePhaseECN = true;
            }
        }

        Hashtable ht = null;
        Vector<Object> printPBOVector = getPrintObjVector(pbo);
        for (int i = 0; i < printPBOVector.size(); i++) {
            Object obj = printPBOVector.get(i);
            log.debug("*********obj:"+obj);
            boolean isPartReviewProcess = false;

            // 检查该流程是否为 零部件签审流程， 如果是 则需要对会签意见作特别处理
            if (pbo instanceof ProcessEnvelope) {
                String objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(pbo).toString();
                if (objectType.indexOf("casc.sast.149.APPROVEFORM") > -1) {
                    isPartReviewProcess = true;
                }
            }

            // 检查该流程是否为 变更流程， 如果是 则需要对会签意见作特别处理
            String objectType ="";
            if (pbo instanceof WTChangeOrder2) {
                objectType = TypeIdentifierUtilityHelper.service.getTypeIdentifier(pbo).toString();
                log.debug("------objectType:" + objectType);
                if (objectType.indexOf("CHANGE_ECN") > -1||objectType.indexOf("PROCESS_ECN") > -1) {
                    isPartReviewProcess = true;
                }
            }
            log.debug("------isPartReviewProcess:" + isPartReviewProcess);

            if(objectType.indexOf("PROCESS_ECN") > -1){
            		ht = FilePrintUtil.getPrePrintInfo();
            		//ht = AddSignInfor(ht);
            }else if(objectType.indexOf("PROCESS_NOTICE") > -1){
        		ht = FilePrintUtil.getPrePrintInfo();
        		//ht = AddSignInfor(ht);
            }else if(objectType.indexOf("DOCUMENT_ECN") > -1){
                ht = FilePrintUtil.getPrePrintInfo();
            }

            String objOid = PersistenceHelper.getObjectIdentifier((Persistable) obj).toString();
            ReferenceFactory rf = new ReferenceFactory();
            Object clonePbo = rf.getReference(objOid).getObject();
            if (clonePbo instanceof WTDocument) {
                WTDocument wtd = (WTDocument) clonePbo;
                log.debug("clonePbo name is " + wtd.getName() + " clonePbo version is "
                        + wtd.getVersionIdentifier().getValue() + "." + wtd.getIterationIdentifier().getValue());
            }

            Set set = ht.keySet();
            Iterator iter = set.iterator();
            log.debug(" ======================= Object [" + objOid + "] Print Info=======================");
            while (iter.hasNext()) {
                String key = (String) iter.next();
                String value = (String) ht.get(key);
                log.debug(key + " = " + value);
            }
            log.debug(" ======================= End =======================");
            printTable.put(objOid, ht);
        }
        return  printTable;

	}


}
