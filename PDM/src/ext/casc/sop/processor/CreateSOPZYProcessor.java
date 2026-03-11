package ext.casc.sop.processor;

import com.glaway.mpm.util.*;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMTooling;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.resource.SopMPMResourceImportProcessor;
import ext.casc.sop.util.SopPartUtil;
import ext.casc.sop.util.SopUtil;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.util.WTException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateSOPZYProcessor extends DefaultObjectFormProcessor {
	private static final String CLASSNAME = CreateSOPZYProcessor.class.getName();
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.MPMRESOURCE_TYPE_TO_FOLDER_CONFIG_PATH);

	@Override
	public FormResult setResultNextAction(FormResult arg0, NmCommandBean commandBean, List<ObjectBean> arg2) throws WTException {
		FormResult formResult = new FormResult();
		HashMap<String, String> textMap = commandBean.getText();
		HashMap<String, List<String>> comboBoxMap = commandBean.getComboBox();
		HashMap<String, String> textAreaMap = commandBean.getTextArea();
		GLLogger.debug(CLASSNAME, "comboBoxMap--" + comboBoxMap);
		GLLogger.debug(CLASSNAME, "textAreaMap--" + textAreaMap);
		String sopZYType = commandBean.getTextParameter("mpmResourceType");
		String typeName = commandBean.getTextParameter("typeName");
		GLLogger.debug(CLASSNAME, "sopZYType--" + sopZYType);
		GLLogger.debug(CLASSNAME, "typeName--" + typeName);
		GLLogger.debug(CLASSNAME, "textMap--" + textMap);
//		String number = textMap.get(SopConstants.SOP_ATTR_NUMBER);
//		if(number==null){
//			number = (String) commandBean.getOldText().get("number");
//		}
		String name = textMap.get(SopConstants.SOP_ATTR_NAME);
		if(name!=null){
			name = name.trim();
		}
		List<String> list = comboBoxMap.get(SopConstants.SOP_IBA_ZZCJ);
		String ZZCJ = "";
		if (list != null) {
			ZZCJ = list.get(0);
		}
//		if (number == null && name == null) {
//			number = textMap.get("name_col_name");
//			name = textMap.get("number_col_number");
//		}
//		if (number == null) {
//			number = (String) commandBean.getOldText().get("number");
//		}
		//TODO-SOP:编号换一种获取方式，始终获取最新的，参考SopMPMResourceImportProcessor getLastestNumber方法
		//String number = sopZYType+SopUtil.getSopZYSeqNumber(1,sopZYType);

		String description = textAreaMap.get(SopConstants.SOP_ATTR_REMARK);
		if(description!=null){
			description = description.trim();
		}
		WTContainer container = commandBean.getContainer();
		String folderPath = propertiesUtil.getProperty(typeName);
		Transaction tx = null;
		try {
			tx = new Transaction();
			tx.start();
			Map<String, String> ibaMap = null;
			MPMTooling tooling = null;
			if (sopZYType.equals(SopConstants.SOP_STR_CSXM)) {
				// 新建参数项目
				//TODO-SOP:1、获取需要校验的属性，参数项目：名称+专业类别+工序名称+物资类别+参数项目名称
				String number = getLastestNumber(SopConstants.SOP_STR_CSXM, SopConstants.SOP_TYPE_PARAMETERS, "");
				name = comboBoxMap.get(SopConstants.SOP_IBA_PARAMETERSNAME).get(0).trim();
				ibaMap = new HashMap<String, String>();
				ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, comboBoxMap.get(SopConstants.SOP_IBA_SPECIALIZEDTYPE).get(0).trim());
				ibaMap.put(SopConstants.SOP_IBA_PROCEDUCENAME, comboBoxMap.get(SopConstants.SOP_IBA_PROCEDUCENAME).get(0).trim());
				ibaMap.put(SopConstants.SOP_IBA_MATERIALCATEGORY, comboBoxMap.get(SopConstants.SOP_IBA_MATERIALCATEGORY).get(0).trim());
				ibaMap.put(SopConstants.SOP_IBA_PARAMETERSNAME, comboBoxMap.get(SopConstants.SOP_IBA_PARAMETERSNAME).get(0).trim());
				List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK,null,name,null,ibaMap,true,SopConstants.SOP_TYPE_PARAMETERS);
				if(wtPartList != null && wtPartList.size() > 0){
					throw new WTException("存在相同的专业类别、参数项目名称、工序名称和物资类别组合值，请重新指定创建！");
				}else{
					ibaMap.put(SopConstants.SOP_ATTR_REMARK, description);
					String canshuzhi = textMap.get(SopConstants.SOP_IBA_CANSHUZHI);
					if (canshuzhi != null) {
						canshuzhi = canshuzhi.trim();
					}
					ibaMap.put(SopConstants.SOP_IBA_CANSHUZHI, canshuzhi);
					tooling = MPMResourceUtil.createSOPTooling(number, name, container, folderPath, typeName);
				}
			} else if (sopZYType.equals(SopConstants.SOP_STR_GXMC)) {
				// 新建工序名称
				String number = getLastestNumber(SopConstants.SOP_STR_GXMC, SopConstants.SOP_TYPE_PROCEDUCENAME, "");
				String englishName = textMap.get(SopConstants.SOP_IBA_ENGLISHNAME);
				if (englishName == null) {
					englishName = textMap.get("name_col_englishname");
				}
				if(englishName!=null){
					englishName = englishName.trim();
				}
				ibaMap = new HashMap<String, String>();
				ibaMap.put(SopConstants.SOP_IBA_GONGXUJIANHAO, textMap.get(SopConstants.SOP_IBA_GONGXUJIANHAO).trim());
				ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, comboBoxMap.get(SopConstants.SOP_IBA_SPECIALIZEDTYPE).get(0).trim());
				List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK,null,name,null,ibaMap,true,SopConstants.SOP_TYPE_PROCEDUCENAME);
				if(wtPartList != null && wtPartList.size() > 0){
					throw new WTException("存在相同的名称、工序简号和专业类别组合值，请重新指定名称、工序简号或专业类别创建！");
				}else{
					ibaMap.put(SopConstants.SOP_ATTR_REMARK, description);
					ibaMap.put(SopConstants.SOP_IBA_ENGLISHNAME, englishName);
					ibaMap.put(SopConstants.SOP_IBA_ZZCJ, ZZCJ);
					tooling = MPMResourceUtil.createSOPTooling(number, name, container, folderPath, typeName);
				}
			} else if (sopZYType.equals(SopConstants.SOP_STR_DZQY)) {
				// 新建定制区域
				String number = getLastestNumber(SopConstants.SOP_STR_DZQY, SopConstants.SOP_TYPE_CUSTOMAREA, "");
				ibaMap = new HashMap<String, String>();
				List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK,null,name,null,ibaMap,true,SopConstants.SOP_TYPE_CUSTOMAREA);
				if(wtPartList != null && wtPartList.size() > 0){
					throw new WTException("存在相同的名称，请重新指定名称创建！");
				}else{
					ibaMap.put(SopConstants.SOP_ATTR_REMARK, description);
					tooling = MPMResourceUtil.createSOPTooling(number, name, container, folderPath, typeName);
				}
			} else if (sopZYType.equals(SopConstants.SOP_STR_CZGW)) {
				// 新建操作岗位
				String number = getLastestNumber(SopConstants.SOP_STR_CZGW, SopConstants.SOP_TYPE_OPERATIONJOB, "");
				ibaMap = new HashMap<String, String>();
				List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK,null,name,null,ibaMap,true,SopConstants.SOP_TYPE_OPERATIONJOB);
				if(wtPartList != null && wtPartList.size() > 0){
					throw new WTException("存在相同的名称，请重新指定名称创建！");
				}else{
					ibaMap.put(SopConstants.SOP_ATTR_REMARK, description);
					ibaMap.put(SopConstants.SOP_IBA_ZZCJ, ZZCJ);
					tooling = MPMResourceUtil.createSOPTooling(number, name, container, folderPath, typeName);
				}
			} else if (sopZYType.equals(SopConstants.SOP_STR_ZYLB)) {
				// 新建专业类别
				String number = getLastestNumber(SopConstants.SOP_STR_ZYLB, SopConstants.SOP_TYPE_SPECIALIZEDTYPE, "");
				ibaMap = new HashMap<String, String>();
				ibaMap.put(SopConstants.SOP_IBA_PROFESSIONALCODE, textMap.get(SopConstants.SOP_IBA_PROFESSIONALCODE).trim());
				List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK,null,name,null,ibaMap,true,SopConstants.SOP_TYPE_SPECIALIZEDTYPE);
				if(wtPartList != null && wtPartList.size() > 0){
					throw new WTException("存在相同的名称和专业代号组合值，请重新指定名称或专业代号创建！");
				}else{
					ibaMap.put(SopConstants.SOP_ATTR_REMARK, description);
					tooling = MPMResourceUtil.createSOPTooling(number, name, container, folderPath, typeName);
				}
			} else if (sopZYType.equals(SopConstants.SOP_STR_CSXMMC)) {
				// 新建参数项目名称
				String number = getLastestNumber(SopConstants.SOP_STR_CSXMMC, SopConstants.SOP_TYPE_PARAMETERSNAME, "");
				ibaMap = new HashMap<String, String>();
				ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, comboBoxMap.get(SopConstants.SOP_IBA_SPECIALIZEDTYPE).get(0).trim());
				List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK,null,name,null,ibaMap,true,SopConstants.SOP_TYPE_PARAMETERSNAME);
				if(wtPartList != null && wtPartList.size() > 0){
					throw new WTException("存在相同的名称和专业类别组合值，请重新指定名称或专业类别创建！");
				}else{
					ibaMap.put(SopConstants.SOP_ATTR_REMARK, description);
					tooling = MPMResourceUtil.createSOPTooling(number, name, container, folderPath, typeName);
				}
			} else if (sopZYType.equals(SopConstants.SOP_STR_WZLB)) {
				// 新建物资类别
				String number = getLastestNumber(SopConstants.SOP_STR_WZLB, SopConstants.SOP_TYPE_MATERIALCATEGORY, "");
				ibaMap = new HashMap<String, String>();
				ibaMap.put(SopConstants.SOP_IBA_SPECIALIZEDTYPE, comboBoxMap.get(SopConstants.SOP_IBA_SPECIALIZEDTYPE).get(0).trim());
				List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK,null,name,null,ibaMap,true,SopConstants.SOP_TYPE_MATERIALCATEGORY);
				if(wtPartList != null && wtPartList.size() > 0){
					throw new WTException("存在相同的名称和专业类别组合值，请重新指定名称或专业类别创建！");
				}else{
					ibaMap.put(SopConstants.SOP_ATTR_REMARK, description);
					tooling = MPMResourceUtil.createSOPTooling(number, name, container, folderPath, typeName);
				}
			}else if(sopZYType.equals(SopConstants.SOP_STR_CZMC)){
				// 新建操作名称
				String number = getLastestNumber(SopConstants.SOP_STR_CZMC, SopConstants.SOP_TYPE_OPERATIONNAME, "");
				ibaMap = new HashMap<String, String>();
				List<WTPart> wtPartList = SopPartUtil.searchLatestPartList(SopConstants.SOP_CONTAINER_GYZYK,null,name,null,ibaMap,true,SopConstants.SOP_TYPE_OPERATIONNAME);
				if(wtPartList != null && wtPartList.size() > 0){
					throw new WTException("存在相同的名称，请重新指定名称创建！");
				}else{
					ibaMap.put(SopConstants.SOP_ATTR_REMARK, description);
					tooling = MPMResourceUtil.createSOPTooling(number, name, container, folderPath, typeName);
				}
			}
			if (tooling != null && ibaMap != null && ibaMap.size() > 0) {
				setIBAValue(tooling, ibaMap);
			}
			tx.commit();
			tx = null;
			formResult.setStatus(FormProcessingStatus.SUCCESS);
			formResult.setNextAction(FormResultAction.NONE);
		} catch (Exception e) {
			e.printStackTrace();
			formResult.setStatus(FormProcessingStatus.FAILURE);
			formResult.addException(e);
			formResult.setNextAction(FormResultAction.NONE);
			return formResult;
		} finally {
			if (tx != null) {
				tx.rollback();
			} else {
				SopUtil.getSopZYSeqNumber(2, sopZYType);
			}
		}
		return super.setResultNextAction(arg0, commandBean, arg2);
	}

	/**
	 * 设置IBA属性
	 *
	 * @param tooling
	 * @param ibaMap
	 * @throws Exception
	 */
	private void setIBAValue(MPMTooling tooling, Map<String, String> ibaMap) throws Exception {
		IBAHelper helper = new IBAHelper(tooling);
		helper.setIBAValue(tooling, ibaMap);
	}

	private static String getLastestNumber(String pre, String typeName, String lastestNumber) throws Exception {
        lastestNumber = pre + SopUtil.getSopZYSeqNumber(1, pre);
        MPMTooling tooling = MPMResourceUtil.getMPMToolingByNumber(lastestNumber, typeName);
        if (tooling != null) {
            SopUtil.getSopZYSeqNumber(2, pre);
            lastestNumber = getLastestNumber(pre, typeName, lastestNumber);
        }
        return lastestNumber;
    }


}
