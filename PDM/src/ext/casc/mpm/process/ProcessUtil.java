package ext.casc.mpm.process;

import ext.casc.dfmRule.util.GeneralUtil;
import ext.casc.doc.CSCDoc;
import ext.casc.util.IBAUtility;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import org.apache.commons.lang3.StringUtils;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.iba.definition.StringDefinition;
import wt.iba.definition._AttributeHierarchyChild;
import wt.iba.value.StringValue;
import wt.iba.value._StringValue;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.type.ClientTypedUtility;
import wt.type.TypeDefinitionReference;
import wt.util.WTAttributeNameIfc;
import wt.util.WTStandardDateFormat;
import wt.vc.config.LatestConfigSpec;

import java.sql.Timestamp;
import java.util.*;

public class ProcessUtil {

    public static final String[] HEAD_PROCESS_STEP_LINK_GuRongReChuLi = {"序号", "材料分类", "规格", "工序模板"};
    public static final String[] HEAD_PROCESS_STEP_LINK_RenGongShiXiaoReChuLi = {"序号", "材料分类", "规格", "工序模板"};
    public static final String[] HEAD_PROCESS_STEP_LINK_BiaoMianChuLi = {"序号", "材料分类", "表面处理类型", "工序模板"};

    public static List<GLProcessParams> getProcessParamsList(String number) {
        List<GLProcessParams> result = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParams.class);
            qs.appendWhere(GLProcessParams.GYNUMBER, CmQuerySpec.EQUAL, number);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                result.add((GLProcessParams) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public static List<GLProcessParams> queryGLProcessParams(String parameterCategory, String gyName, String gyNumber) {
        List<GLProcessParams> list = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParams.class);
            boolean needAnd = false;
            if (StringUtils.isNotEmpty(parameterCategory)) {
                needAnd = true;
                qs.appendWhere(GLProcessParams.PARAMETER_CATEGORY, CmQuerySpec.EQUAL, parameterCategory);
            }
            if (StringUtils.isNotEmpty(gyName)) {
                if (needAnd) {
                    qs.appendAnd();
                }
                qs.appendWhere(GLProcessParams.GYNAME, CmQuerySpec.EQUAL, gyName);
            }
            if (StringUtils.isNotEmpty(gyNumber)) {
                if (needAnd) {
                    qs.appendAnd();
                }
                qs.appendWhere(GLProcessParams.GYNUMBER, CmQuerySpec.EQUAL, gyNumber);
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                list.add((GLProcessParams) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    public static List<GLProcessTemplateLink> queryGLProcessTemplateLink() {
        List<GLProcessTemplateLink> list = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessTemplateLink.class);
            qs.appendOrderBy(" TO_NUMBER(xuhao) ", false);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                list.add((GLProcessTemplateLink) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    public static List<GLProcessStepTemplateLink> queryGLProcessStepTemplateLink() {
        List<GLProcessStepTemplateLink> list = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessStepTemplateLink.class);
            qs.appendOrderBy(" TO_NUMBER(xuhao) ", false);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                list.add((GLProcessStepTemplateLink) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static Map<String, List<GLProcessStepTemplateLink>> splitLinkByStepName(List<GLProcessStepTemplateLink> list) {
        Map<String, List<GLProcessStepTemplateLink>> result = new HashMap();

        // 遍历原始 List
        for (GLProcessStepTemplateLink link : list) {
            String stepName = link.getStepName(); // 获取 stepName 属性
            // 如果已经存在该 stepName 的子 List，将该对象添加到子 List 中
            if (result.containsKey(stepName)) {
                result.get(stepName).add(link);
            }
            // 如果不存在该 stepName 的子 List，创建一个新的子 List，并将该对象添加到子 List 中
            else {
                result.put(stepName, new ArrayList<GLProcessStepTemplateLink>());
                result.get(stepName).add(link);
            }
        }
        return result;
    }

    public static List<GLProcessLink> queryGLProcessLink() {
        List<GLProcessLink> list = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessLink.class);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                list.add((GLProcessLink) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static List<GLProcessKnowledge> queryGLProcessKnowledge(String sheetName) {
        List<GLProcessKnowledge> list = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessKnowledge.class);
            if (StringUtils.isNotEmpty(sheetName)) {
                qs.appendWhere(GLProcessKnowledge.SHEETNAME, CmQuerySpec.EQUAL, sheetName);
            }
            qs.appendOrderBy(GLProcessKnowledge.XUHAO, false);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                list.add((GLProcessKnowledge) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    /**
     * Queries WTDocuments by IBA attributes.
     *
     * @param identityMap a map of identity attributes
     *                    WTDocument.DOC_TYPE:wt.doc.WTDocument|casc.sast.149.DESIGN_DOC|casc.sast.149.TECHNOTICE_DOC
     *                    WTDocument.NUMBER:
     *                    TDocument.NAME
     *                    WTDocument.CREATE_TIMESTAMP + "_FROM"
     *                    WTDocument.CREATE_TIMESTAMP + "_TO"
     * @param ibaMap      a map of IBA attributes
     *                    state:
     *                    dept:
     *                    speciality:
     *                    productType:
     * @return a list of WTDocuments that match the given IBA attributes
     */
    public static List<WTDocument> queryDocByIBA(Map<String, String> identityMap, Map<String, String> ibaMap) {
        List<WTDocument> list = new ArrayList<WTDocument>();
        SessionServerHelper.manager.setAccessEnforced(false);
        boolean flag = true;
        try {
            QuerySpec qs = new QuerySpec();
            qs.setAdvancedQueryEnabled(true);
            int ibaHolderIndex = qs.appendClassList(WTDocument.class, true);


            String number = identityMap.get(WTDocument.NUMBER);
            String name = identityMap.get(WTDocument.NAME);
            String createStamp_from = identityMap.get(WTDocument.CREATE_TIMESTAMP + "_FROM");
            String createStamp_to = identityMap.get(WTDocument.CREATE_TIMESTAMP + "_TO");
            String type = identityMap.get(WTDocument.DOC_TYPE);
            TypeDefinitionReference gywj = ClientTypedUtility.getTypeDefinitionReference(type);
            long id = 0;
            if (gywj != null) {
                id = gywj.getKey().getBranchId();
            }
            qs.appendWhere(new SearchCondition(WTDocument.class, "typeDefinitionReference.key.branchId", SearchCondition.EQUAL, id), ibaHolderIndex);


            if (number != null && !"".equals(number)) {
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, "%" + number + "%"), ibaHolderIndex);
            }
            if (StringUtils.isNotEmpty(name)) {
                qs.appendAnd();
                qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + name + "%"), ibaHolderIndex);
            }

            if (StringUtils.isNotEmpty(createStamp_from)) {
                Date dateFrom = WTStandardDateFormat.parse(createStamp_from, "yyyy/M/d");
                qs.appendAnd();
                SearchCondition sc1 = new SearchCondition(WTDocument.class, WTDocument.CREATE_TIMESTAMP, SearchCondition.GREATER_THAN_OR_EQUAL, new Timestamp(dateFrom.getTime()));
                qs.appendSearchCondition(sc1);
            }
            if (StringUtils.isNotEmpty(createStamp_to)) {
                Date dateFrom1 = WTStandardDateFormat.parse(createStamp_to, "yyyy/M/d");
                qs.appendAnd();
                SearchCondition sc11 = new SearchCondition(WTDocument.class, WTDocument.CREATE_TIMESTAMP, SearchCondition.LESS_THAN_OR_EQUAL, new Timestamp(dateFrom1.getTime()));
                qs.appendSearchCondition(sc11);
            }
            if (ibaMap.size() > 0) {
                for (String ibaname : ibaMap.keySet()) {

                    if (ibaMap.get(ibaname) != null && !ibaMap.get(ibaname).isEmpty()) {
                    	String ibaValue = ibaMap.get(ibaname).trim();
                        int ibaStringValueIndex = qs.appendClassList(StringValue.class, false);
                        int ibaStringDefinitionIndex = qs.appendClassList(StringDefinition.class, false);
                        // Latest Iteration
                        qs.appendAnd();

                        SearchCondition scLatestIteration = new SearchCondition(WTDocument.class, WTAttributeNameIfc.LATEST_ITERATION, SearchCondition.IS_TRUE);
                        // String Value With IBA Holder
                        SearchCondition scJoinStringValueIBAHolder = new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTDocument.class, WTAttributeNameIfc.ID_NAME);
                        // String Value With Definition
                        SearchCondition scJoinStringValueStringDefinition = new SearchCondition(StringValue.class, "definitionReference.key.id", StringDefinition.class, WTAttributeNameIfc.ID_NAME);
                        qs.appendWhere(scLatestIteration, ibaHolderIndex);
                        qs.appendAnd();
                        qs.appendWhere(scJoinStringValueIBAHolder, ibaStringValueIndex, ibaHolderIndex);
                        qs.appendAnd();
                        qs.appendWhere(scJoinStringValueStringDefinition, ibaStringValueIndex, ibaStringDefinitionIndex);

                        // String Definition 软属性名称
                        SearchCondition scStringDefinitionName1 = new SearchCondition(StringDefinition.class, _AttributeHierarchyChild.NAME, SearchCondition.EQUAL, ibaname);
                        // String Value 软属性值
                        SearchCondition scStringValueValue1 = new SearchCondition(StringValue.class, _StringValue.VALUE, SearchCondition.EQUAL,ibaValue );

                        qs.appendAnd();
                        qs.appendWhere(scStringDefinitionName1, ibaStringDefinitionIndex);
                        qs.appendAnd();
                        qs.appendWhere(scStringValueValue1, ibaStringValueIndex);
                    }
                }
            }
            qs = new LatestConfigSpec().appendSearchCriteria(qs);
            QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
            while (qr.hasMoreElements()) {
                Object[] obj = (Object[]) qr.nextElement();
                list.add((WTDocument) obj[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return list;
    }

    public static List getGLProcessParamDefinition(String templateId) {
        List<GLProcessParamDefinition> list = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParamDefinition.class);
            if (StringUtils.isNotEmpty(templateId)) {
                qs.appendWhere(GLProcessParamDefinition.TEMPLATEID, CmQuerySpec.EQUAL, templateId);
            }
            qs.appendOrderBy(GLProcessParamDefinition.GYPARAMNAME, false);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                list.add((GLProcessParamDefinition) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static List<GLProcessTemplateLink> getGLProcessTemplateLink(WTPart part) {
        List<GLProcessTemplateLink> list = new ArrayList();
        try {
            IBAUtility iba = new IBAUtility(part);
            String caiLiaoFenLei = iba.getIBAValue("SURFACETREATMENT");
            caiLiaoFenLei = StringUtils.isEmpty(caiLiaoFenLei) ? "" : caiLiaoFenLei;
            String yaXiaXian = iba.getIBAValue("yaXiaXian");
            yaXiaXian = StringUtils.isEmpty(yaXiaXian) ? "" : yaXiaXian;
            String reChuLi = iba.getIBAValue("HEATTREATMENT");
            reChuLi = StringUtils.isEmpty(reChuLi) ? "" : reChuLi;
            String biaoMianChuLi = iba.getIBAValue("SURFACETREATMENT");
            biaoMianChuLi = StringUtils.isEmpty(biaoMianChuLi) ? "" : biaoMianChuLi;
            CmQuerySpec qs = new CmQuerySpec(GLProcessTemplateLink.class);

            qs.appendWhere(GLProcessTemplateLink.CAILIAOFENLEI, CmQuerySpec.EQUAL, caiLiaoFenLei);
            qs.appendAnd();
            qs.appendWhere(GLProcessTemplateLink.YAXIAXIAN, CmQuerySpec.EQUAL, yaXiaXian);
            qs.appendAnd();
            qs.appendWhere(GLProcessTemplateLink.RECHULI, CmQuerySpec.EQUAL, reChuLi);
            qs.appendAnd();
            qs.appendWhere(GLProcessTemplateLink.BIAOMIANCHULI, CmQuerySpec.EQUAL, biaoMianChuLi);

            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                list.add((GLProcessTemplateLink) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }


    /**
     * 查询工艺参数值表
     *
     * @param partNumber
     * @param partVersion
     * @param templateId
     * @return
     */
    public static List<GLProcessParamValues> queryGLProcessParamValues(String partNumber, String partVersion, String templateId, String gyParamNumber) {
        List<GLProcessParamValues> result = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParamValues.class);
            qs.appendWhere(GLProcessParamValues.PART_NUMBER, CmQuerySpec.EQUAL, partNumber);
            qs.appendAnd();
            qs.appendWhere(GLProcessParamValues.PART_VERSION, CmQuerySpec.EQUAL, partVersion);

            if (StringUtils.isNotEmpty(templateId)) {
                qs.appendAnd();
                qs.appendWhere(GLProcessParamValues.TEMPLATE_ID, CmQuerySpec.EQUAL, templateId);
            }
            if (StringUtils.isNotEmpty(gyParamNumber)) {
                qs.appendAnd();
                qs.appendWhere(GLProcessParamValues.GY_PARAM_NUMBER, CmQuerySpec.EQUAL, gyParamNumber);
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                result.add((GLProcessParamValues) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public static List<GLProcessParamValues> queryGLProcessParamValues2(String partNumber, String partVersion,  String gyParamNumber,  String gyParamName) {
        List<GLProcessParamValues> result = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParamValues.class);
            qs.appendWhere(GLProcessParamValues.PART_NUMBER, CmQuerySpec.EQUAL, partNumber);
            qs.appendAnd();
            qs.appendWhere(GLProcessParamValues.PART_VERSION, CmQuerySpec.EQUAL, partVersion);

            if (StringUtils.isNotEmpty(gyParamNumber)) {
                qs.appendAnd();
                qs.appendWhere(GLProcessParamValues.GY_PARAM_NUMBER, CmQuerySpec.EQUAL, gyParamNumber);
            }
            if (StringUtils.isNotEmpty(gyParamName)) {
                qs.appendAnd();
                qs.appendWhere(GLProcessParamValues.GY_PARAM_NAME, CmQuerySpec.EQUAL, gyParamName);
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                result.add((GLProcessParamValues) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public static List<GLProcessParamValues> queryGLProcessParamValues(String partNumber, String gyParamNumber) {
        List<GLProcessParamValues> result = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParamValues.class);
            qs.appendWhere(GLProcessParamValues.PART_NUMBER, CmQuerySpec.EQUAL, partNumber);

            if (StringUtils.isNotEmpty(gyParamNumber)) {
                qs.appendAnd();
                qs.appendWhere(GLProcessParamValues.GY_PARAM_NUMBER, CmQuerySpec.EQUAL, gyParamNumber);
            }

            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                result.add((GLProcessParamValues) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public static boolean saveOrUpdateGLProcessParamValues(GLProcessParamValues glProcessParamValues) {
        try {

            //补充glProcessParamValues表的其他不重要字段，比如工艺名称，模板名称等等。用以完善glProcessParamValues表数据
            if(StringUtils.isNotEmpty(glProcessParamValues.getGyParamNumber()) && StringUtils.isEmpty(glProcessParamValues.getGyParamName())){
                List<GLProcessParams> glProcessParams = queryGLProcessParams("", "",  glProcessParamValues.getGyParamNumber());
                if(!glProcessParams.isEmpty()){
                    glProcessParamValues.setGyParamName(glProcessParams.get(0).getGyName());
                }
            }

            if(StringUtils.isNotEmpty(glProcessParamValues.getTemplateId()) && StringUtils.isEmpty(glProcessParamValues.getTemplateName())){
               WTDocument doc =(WTDocument) GeneralUtil.getObjectByOid(glProcessParamValues.getTemplateId());
                glProcessParamValues.setTemplateName(doc.getName());
            }

            CmPersistenceHelper.manager.save(glProcessParamValues);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String transformTemplateOid(String selectTemplates) {
        if (selectTemplates == null || selectTemplates.trim().isEmpty()) {
            return "";
        }
        if(selectTemplates.startsWith("OR:")){
            return selectTemplates;
        }
        String[] templateParts = StringUtils.split(selectTemplates, "_");
        // 添加判空条件
        if (templateParts.length < 2 || templateParts[1].trim().isEmpty()) {
            return "";
        }
        String templateNumber = templateParts[1].trim();
        String docIdentifier = "OR:" + PersistenceHelper.getObjectIdentifier(CSCDoc.getDoc(templateNumber));
        return docIdentifier;
    }

}
