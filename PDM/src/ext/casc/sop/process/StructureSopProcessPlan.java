package ext.casc.sop.process;

import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.glaway.mpm.util.*;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.operation.MPMOperation;
import com.ptc.windchill.mpml.processplan.operation.MPMOperationMaster;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMProcessMaterialMaster;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMToolingMaster;
import ext.casc.sop.constants.SopConstants;
import ext.casc.util.IBAUtility;
import org.jdom.Element;
import wt.doc.WTDocument;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class StructureSopProcessPlan {

    private String tempFilePath = PropertiesUtil.getWTHome() + File.separator + "temp" + File.separator + java.util.UUID.randomUUID().toString() + File.separator;
    private WTDocument document;
    private WTPart wtPart;
    private WTContainer wtContainer;
    private String folder = "";



    public StructureSopProcessPlan(WTDocument document, WTPart wtPart) {
        this.document = document;
        this.wtPart = wtPart;
        this.wtContainer = document.getContainer();
    }


    /**
     * 实例化主方法
     */
    public void structure() throws Exception{

        	folder = this.document.getFolderPath();
        	String[] f = folder.split("/");
        	folder = SopConstants.SOP_FOLDOR_PROCESS + f[3] + "/" + f[4];
            WTUser modifier = (WTUser) document.getModifier().getObject();
            if (modifier != null) {
                SessionHelper.manager.setPrincipal(modifier.getAuthenticationName());
            }
            String docVersion = document.getVersionIdentifier().getValue();
            if (docVersion.startsWith("space")) {
                String zipFileName = WTDocumentUtil.downloadDocumentPrimaryToTemp(document, tempFilePath);
                String subFileName = zipFileName.substring(0, zipFileName.lastIndexOf("."));
                Element rootElement = getRootElement(zipFileName, subFileName);
                if ("technics".equals(rootElement.getName())) {
                    Element technicsEle = rootElement.getChild("QMFawTechnicsInfo");
                    String version = document.getIterationDisplayIdentifier().toString();
                    MPMProcessPlan processPlan = createProcessPlan(technicsEle, version);
                    // 处理工艺的子节点
                    for (Element rootChildElement : (List<Element>) technicsEle.getChildren()) {
                        if ("steps".equals(rootChildElement.getName())) {
                            structureSteps(processPlan, rootChildElement);
                        }
                    }
                }
            }

    }

    /**
     * 实例化工序
     *
     * @param processPlan
     * @param stepElement
     * @throws Exception
     */
    private void structureSteps(MPMProcessPlan processPlan, Element stepElement) throws Exception {
        Element stepAttrElement = stepElement.getChild("QMProcedureInfo");
        // 创建工序
        MPMOperation operation = createOperation(stepAttrElement);
        // 回写工序的oid
        stepAttrElement.setAttribute("oid", Util.getStringOid(operation));
        // 创建工序与工艺的关联 ，工序号需要统一的三位数
        String stepNumber = stepAttrElement.getAttributeValue("stepNumber");
        int numberLength = 3 - stepNumber.length();
        for (int i = 0; i < numberLength; i++) {
            stepNumber = 0 + stepNumber;
        }
        MPMProcessPlanUtil.createMPMOperationUsageLink(processPlan, (MPMOperationMaster) operation.getMaster(), stepNumber);

        // 处理工序的子节点
        for (Element stepChildElement : (List<Element>) stepAttrElement.getChildren()) {
            if ("paces".equals(stepChildElement.getName())) {
                //工步
                structureSubSteps(operation, stepChildElement);
            } else if ("materials".equals(stepChildElement.getName())) {
                //工艺辅料
                structureMaterials(operation, stepChildElement);
            } else if ("equips".equals(stepChildElement.getName())) {
                //设备
                structureEquipments(operation, stepChildElement);
            } else if ("tools".equals(stepChildElement.getName())) {
                //工装
                structureTools(operation, stepChildElement);
            } else if ("parts".equals(stepChildElement.getName())) {
                //参装件
                structureParts(operation, stepChildElement);
            } else if ("images".equals(stepChildElement.getName())) {
                //简图
                structureImages(operation, stepChildElement);
            } else if ("sdashboard".equals(stepChildElement.getName())) {
                //标准仪器仪表
                structureTools2(operation, stepChildElement, "QMSDashboardInfo");
            } else if ("unsdashboard".equals(stepChildElement.getName())) {
                //非标准仪器仪表
                structureTools2(operation, stepChildElement, "QMUnSDashboardInfo");
            } else if ("knifeTools".equals(stepChildElement.getName())) {
                //刀具
                structureTools2(operation, stepChildElement, "QMKnifeToolInfo");
            } else if ("measures".equals(stepChildElement.getName())) {
                //量具
                structureTools2(operation, stepChildElement, "QMMeasureInfo");
            }
        }
    }

    /**
     * 创建工序
     *
     * @param stepAttrElement
     * @return
     * @throws Exception
     */
    private MPMOperation createOperation(Element stepAttrElement) throws Exception {
        String name = stepAttrElement.getAttributeValue("stepName");
        String stepNumber = stepAttrElement.getAttributeValue("stepNumber");
        if (name == null || "".equals(name)) {
            name = stepNumber;
        }
        MPMOperation operation = MPMProcessPlanUtil.createOperation(null, name, wtContainer, TypeNameConstants.OPERATION_TYPE_NAME, folder + "/工序");
        if (operation != null) {
            operation = (MPMOperation) Util.setLifecycle(operation, Constants.RELEASED);
        }
        return operation;
    }

    /**
     * 创建工步
     *
     * @param operation
     * @param subStepElement
     * @throws Exception
     */
    private void structureSubSteps(MPMOperation operation, Element subStepElement) throws Exception {

        for (Element subStepAttrElement : (List<Element>) subStepElement.getChildren("QMProcedureInfo")) {
            GLLogger.debug(this, "--subStepAttrElement--" + subStepAttrElement.getName());
            // 创建工步
            MPMOperation subOperation = createSubOperation(subStepAttrElement);
            // 回写工步的oid
            subStepAttrElement.setAttribute("oid", Util.getStringOid(subOperation));
            // 工步关联到工序
            try {
                MPMProcessPlanUtil.createMPMOperationUsageLink(operation, (MPMOperationMaster) subOperation.getMaster(),
                        subStepAttrElement.getAttributeValue("stepNumber"));
            } catch (WTException e) {
                e.printStackTrace();
            }

            // 处理工步的子节点
            for (Element subStepChildElement : (List<Element>) subStepAttrElement.getChildren()) {
                GLLogger.debug(this, "--subStepChildElement--" + subStepChildElement.getName());
                if ("materials".equals(subStepChildElement.getName())) {
                    //工艺辅料
                    structureMaterials(subOperation, subStepChildElement);
                } else if ("equips".equals(subStepChildElement.getName())) {
                    //设备
                    structureEquipments(subOperation, subStepChildElement);
                } else if ("tools".equals(subStepChildElement.getName())) {
                    //工装
                    structureTools(subOperation, subStepChildElement);
                } else if ("parts".equals(subStepChildElement.getName())) {
                    //参装件
                    structureParts(subOperation, subStepChildElement);
                } else if ("images".equals(subStepChildElement.getName())) {
                    //简图
                    structureImages(subOperation, subStepChildElement);
                } else if ("sdashboard".equals(subStepChildElement.getName())) {
                    //标准仪器仪表
                    structureTools2(subOperation, subStepChildElement, "QMSDashboardInfo");
                } else if ("unsdashboard".equals(subStepChildElement.getName())) {
                    //非标准仪器仪表
                    structureTools2(subOperation, subStepChildElement, "QMUnSDashboardInfo");
                } else if ("knifeTools".equals(subStepChildElement.getName())) {
                    //刀具
                    structureTools2(subOperation, subStepChildElement, "QMKnifeToolInfo");
                } else if ("measures".equals(subStepChildElement.getName())) {
                    //量具
                    structureTools2(subOperation, subStepChildElement, "QMMeasureInfo");
                }
            }
        }
    }

    /**
     * 创建工步
     *
     * @param subStepAttrElement
     * @return
     * @throws Exception
     */
    private MPMOperation createSubOperation(Element subStepAttrElement) throws Exception {
        String name = subStepAttrElement.getAttributeValue("stepNumber");
        MPMOperation subOperation = MPMProcessPlanUtil.createSubOperation(null, name, wtContainer,
                TypeNameConstants.SUBOPERATION_TYPE_NAME, folder + "/工步");
        if (subOperation != null) {
            subOperation = (MPMOperation) Util.setLifecycle(subOperation, Constants.RELEASED);
        }
        return subOperation;
    }

    /**
     * 获取根节点
     *
     * @param zipFileName
     * @param subFileName
     * @return
     * @throws FileNotFoundException
     */
    private Element getRootElement(String zipFileName, String subFileName) throws FileNotFoundException {
        ApacheZipUtil.decompress(tempFilePath + zipFileName, tempFilePath + subFileName);
        File xmlFile = new File(tempFilePath + subFileName + File.separator + subFileName + ".xml");
        InputStream inputStream = new FileInputStream(xmlFile);
        SWXMLUtil xmlUtil = new SWXMLUtil(inputStream);
        return xmlUtil.getRootElement();
    }

    /**
     * 关联工艺辅料
     *
     * @param object
     * @param materialElement
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void structureMaterials(Object object, Element materialElement) throws Exception {

        for (Element materialAttrElement : (List<Element>) materialElement.getChildren("QMMaterialInfo")) {
            GLLogger.debug(this, "--materialAttrElement--" + materialAttrElement.getName());
            // 获取工艺辅料
            String oid = materialAttrElement.getAttributeValue("oid");
            MPMProcessMaterial processMaterial = (MPMProcessMaterial) Util
                    .getObjectByOid(MPMProcessMaterial.class, oid);
            if (null == processMaterial) {
                WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
                if (null == part) {
                    continue;
                }
                MPMProcessPlanUtil.createOperationToOperatedPartLink((MPMOperation) object, (WTPartMaster) part.getMaster());
            } else {
                // 工艺辅料关联到工步或者工序
                MPMProcessPlanUtil.createMPMOperationToConsumableLink((MPMOperation) object, (MPMProcessMaterialMaster) processMaterial.getMaster());
            }
        }
    }

    /**
     * 关联设备
     *
     * @param object
     * @param equipmentElement
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void structureEquipments(Object object, Element equipmentElement) throws Exception {
        for (Element equipmentAttrElement : (List<Element>) equipmentElement.getChildren("QMEquipmentInfo")) {
            GLLogger.debug(this, "--equipmentAttrElement--" + equipmentAttrElement.getName());
            // 获取装备
            String number = equipmentAttrElement.getAttributeValue("number");
            MPMToolingMaster toolingMaster = MPMResourceUtil.getMPMToolingMasterByNumber(number);
            if (null == toolingMaster) {
                continue;
            }
            // 装备关联到工序或者工步
            MPMProcessPlanUtil.createMPMOperationToConsumableLink((MPMOperation) object, toolingMaster);
        }
    }

    /**
     * 关联工装、工量具、刀具
     *
     * @param object
     * @param toolElement
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void structureTools(Object object, Element toolElement) throws Exception {

        for (Element toolAttrElement : (List<Element>) toolElement.getChildren("QMToolInfo")) {
            // 获取工装
            MPMTooling tooling = (MPMTooling) Util.getObjectByOid(MPMTooling.class, toolAttrElement.getAttributeValue("oid"));
            if (null == tooling) {
                continue;
            }
            // 工装关联到工序或者工步
            MPMProcessPlanUtil.createMPMOperationToConsumableLink((MPMOperation) object, (MPMToolingMaster) tooling.getMaster());

        }
    }

    /**
     * @param object
     * @param toolElement
     * @param name
     * @throws Exception
     */
    private void structureTools2(Object object, Element toolElement, String name) throws Exception {

        for (Element toolAttrElement : (List<Element>) toolElement.getChildren(name)) {
            // 获取工装
            MPMTooling tooling = (MPMTooling) Util.getObjectByOid(MPMTooling.class, toolAttrElement.getAttributeValue("oid"));
            if (null == tooling) {
                continue;
            }
            // 工装关联到工序或者工步
            MPMProcessPlanUtil.createMPMOperationToConsumableLink((MPMOperation) object, (MPMToolingMaster) tooling.getMaster());

        }
    }

    /**
     * 关联零件
     *
     * @param object
     * @param partElement
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void structureParts(Object object, Element partElement) throws Exception {
        for (Element partAttrElement : (List<Element>) partElement.getChildren("QMPartInfo")) {
            // 获取零件
            String partNumber = partAttrElement.getAttributeValue("partNumber");
            WTPartMaster partMaster = WTPartUtil.getWTPartMasterByNumber(partNumber);
            if (null == partMaster) {
                continue;
            }
            // 零件关联到工序或工步
            if (partMaster != null) {
                MPMProcessPlanUtil.createOperationToOperatedPartLink((MPMOperation) object, partMaster);
            }
        }
    }

    /**
     * 关联图片
     *
     * @param object
     * @param imageElement
     */
    @SuppressWarnings("unchecked")
    private void structureImages(Object object, Element imageElement) {
        for (Element imageAttrElement : (List<Element>) imageElement.getChildren("DrawingInfo")) {
            GLLogger.debug(this, "--imageAttrElement--" + imageAttrElement.getName());

        }
    }

    /**
     * 创建工艺计划
     *
     * @param technicsEle
     * @param version
     * @return
     * @throws Exception
     */
    private MPMProcessPlan createProcessPlan(Element technicsEle, String version) throws Exception {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        String name = technicsEle.getAttributeValue("technicsName");
        String technicsNumber = technicsEle.getAttributeValue("technicsNumber");
        String pplanNumber = technicsEle.getAttributeValue("pplanNumber");
        MPMProcessPlan processPlan = MPMProcessPlanUtil.createMPMProcessPlan(technicsNumber, name, wtContainer, SopConstants.SOP_TYPE_SOPPROCESSPLAN_FULL, folder, version);
        IBAHolder holder = processPlan;
        IBAUtility iba = new IBAUtility();
        iba.setIBAValue("PPNUMBER", pplanNumber);
        iba.updateAttributeContainer(holder);
        iba.updateIBAHolder(processPlan);
        if (null != processPlan) {
            processPlan = (MPMProcessPlan) Util.setLifecycle(processPlan, Constants.RELEASED);
            // 回写工艺的oid
            technicsEle.setAttribute("oid", Util.getStringOid(processPlan));
            // 创建工艺和零件的关联
            MPMProcessPlanUtil.createMPMPartToProcessPlanLink(wtPart, processPlan);
            // 创建工艺与压缩包的关联
            MPMProcessPlanUtil.createMPMDocumentDescribeLink(processPlan, document);
        }
        SessionServerHelper.manager.setAccessEnforced(flag);
        return processPlan;
    }
}
