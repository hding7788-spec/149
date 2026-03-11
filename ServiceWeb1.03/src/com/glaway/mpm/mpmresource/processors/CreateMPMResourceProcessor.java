package com.glaway.mpm.mpmresource.processors;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wt.folder.Cabinet;
import wt.folder.Folder;
import wt.folder.SubFolder;
import wt.iba.value.IBAHolder;
import wt.inf.container.WTContainer;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.Constants;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.PropertiesConfigs;
import com.glaway.mpm.util.PropertiesUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.MPMPlant;
import com.ptc.windchill.mpml.resource.MPMProcessMaterial;
import com.ptc.windchill.mpml.resource.MPMSkill;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMWorkCenter;

public class CreateMPMResourceProcessor extends DefaultObjectFormProcessor {
	private static final String CLASSNAME = CreateMPMResourceProcessor.class.getName();
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.MPMRESOURCE_TYPE_TO_FOLDER_CONFIG_PATH);

	@Override
	public FormResult setResultNextAction(FormResult arg0, NmCommandBean arg1, List<ObjectBean> arg2)
			throws WTException {
		HashMap<String, String> textMap = arg1.getText();
		HashMap<String, List<String>> comboBoxMap = arg1.getComboBox();
		HashMap<String, String> textAreaMap = arg1.getTextArea();
		GLLogger.debug(CLASSNAME, "comboBoxMap--" + comboBoxMap);
		GLLogger.debug(CLASSNAME, "textAreaMap--" + textAreaMap);
		String mpmResourceType = arg1.getTextParameter("mpmResourceType");
		String typeName = arg1.getTextParameter("typeName");
		GLLogger.debug(CLASSNAME, "mpmResourceType--" + mpmResourceType);
		GLLogger.debug(CLASSNAME, "typeName--" + typeName);
		GLLogger.debug(CLASSNAME, "textMap--" + textMap);
		String number = textMap.get(AttributeConstants.number);
		if(number==null){
			number = (String) arg1.getOldText().get(AttributeConstants.number);
		}
		String name = textMap.get(AttributeConstants.name);
		List<String> list = comboBoxMap.get(AttributeConstants.ZZCJ);
		String ZZCJ="";
		if (list!=null) {
		     ZZCJ = list.get(0);
        }
		if(number == null && name == null) {
			number = textMap.get("name_col_name");
			name = textMap.get("number_col_number");
		}

		String description = textAreaMap.get(AttributeConstants.remarkKey);
		String englishName = textMap.get(AttributeConstants.ENGLISHNAME);
		if(englishName == null) {
		    englishName = textMap.get("name_col_englishname");
		}
		String zhizaodanwei = "";
		Map map = arg1.getRequest().getParameterMap();
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = String.valueOf(iterator.next());
            if (key.endsWith(AttributeConstants.zhizaodanwei)) {
                zhizaodanwei = arg1.getRequest().getParameter(key);
                GLLogger.debug(CLASSNAME, "zhizaodanwei--" + zhizaodanwei);
            }
        }

		WTContainer container = arg1.getContainer();
		//String folderPath = propertiesUtil.getProperty(typeName);
		String folderPath = "";
		NmOid nmOid = arg1.getPageOid();
        Object object = nmOid.getRefObject();
        Folder folder = null;
        if (object instanceof SubFolder) {
            folder = (SubFolder)object;
        } else if (object instanceof Cabinet) {
            folder = (Cabinet)object;
        }
        if(folder != null) {
            folderPath = folder.getFolderPath();
        }

		try {
			if (mpmResourceType.equals(Constants.GJ)) {
			    MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName,description);
			    setRemarkValue(tooling,description);
			    setEnglishNameValue(tooling, englishName);
			    setLJIBA(tooling, typeName, textMap,comboBoxMap);
			    MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) tooling.getMaster());
			} else if (mpmResourceType.equals(Constants.DJ)) {
			    MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName,description);
			    setRemarkValue(tooling,description);
			    setEnglishNameValue(tooling, englishName);
			    setDJIBA(tooling, typeName, textMap,comboBoxMap);
			    MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) tooling.getMaster());
			} else if (mpmResourceType.equals(Constants.ZZDW)) {
			    MPMPlant plant = MPMResourceUtil.createPlant(number, name, container, folderPath, typeName,description);
			    //setRemarkValue(plant,description);
			    setEnglishNameValue(plant, englishName);
			} else if (mpmResourceType.equals(Constants.GW)) {
			    MPMWorkCenter workCenter = MPMResourceUtil.createWorkSpace(number, name, container, folderPath, typeName,description);
			    //setRemarkValue(workCenter,description);
			    setEnglishNameValue(workCenter, englishName);
			    MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) workCenter.getMaster());
			} else if (mpmResourceType.equals(Constants.GZhong)) {
			    MPMSkill skill = MPMResourceUtil.createSkill(number, name, container, folderPath, typeName,description);
			    setRemarkValue(skill,description);
			    setEnglishNameValue(skill, englishName);
			    MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) skill.getMaster());
			} else if (mpmResourceType.equals(Constants.SB)) {
				MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName,description);
				setRemarkValue(tooling,description);
				setSBIBA(tooling, typeName, textMap,comboBoxMap);
				 setEnglishNameValue(tooling, englishName);
				MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) tooling.getMaster());
			} else if (mpmResourceType.equals(Constants.GYFL)) {
				MPMProcessMaterial material = MPMResourceUtil.createProcessMaterial(number, name, container, folderPath, typeName,description);
				setRemarkValue(material,description);
				setEnglishNameValue(material, englishName);
				setGYFLIBA(material, typeName, textMap);
			} else if (mpmResourceType.equals(Constants.GXMC)) {
			    MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName,description);
			    setRemarkValue(tooling,description);
			    setZzcjValue(tooling, ZZCJ);
			    setEnglishNameValue(tooling, englishName);
			    MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) tooling.getMaster());
			} else if (mpmResourceType.equals(Constants.GYCYY)) {
			    MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName,description);
			    setRemarkValue(tooling,description);
			    setEnglishNameValue(tooling, englishName);
			    MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) tooling.getMaster());
			} else if (mpmResourceType.equals(Constants.DMSB)) {
			  if ("null".equals(description)||description==null) {
			      description="";
			     }
                MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName,description);
                setRemarkValue(tooling,description);
                setEnglishNameValue(tooling, englishName);
                MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) tooling.getMaster());

			} else if (mpmResourceType.equals(Constants.LJ)) {
			    MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName,description);
			    setRemarkValue(tooling,description);
			    setEnglishNameValue(tooling, englishName);
			    setLJIBA(tooling, typeName, textMap, comboBoxMap);
			    MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) tooling.getMaster());
            } else if (mpmResourceType.equals(Constants.GZhuang)) {
            	if(number.startsWith("A%")){
            		MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName,description);
            		setRemarkValue(tooling,description);
            		setEnglishNameValue(tooling, englishName);
            		setGZhuangIBA(tooling, typeName, textMap,comboBoxMap);
            		MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) tooling.getMaster());
            	}else{
            		throw new WTException("工装编号必须以“A%”开头，请重新填写！");
            	}
            }else if (mpmResourceType.equals(Constants.GWWH)) {
            	MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName,description);
            	setRemarkValue(tooling,description);
            	setEnglishNameValue(tooling, englishName);
            	setGWWHIBA(tooling, typeName, textMap,comboBoxMap);
            	MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) tooling.getMaster());
            }  else if (mpmResourceType.equals(Constants.YQYB)) {
			    MPMTooling tooling = MPMResourceUtil.createTooling(number, name, container, folderPath, typeName,description);
			    setRemarkValue(tooling,description);
			    setEnglishNameValue(tooling, englishName);
			    setLJIBA(tooling, typeName, textMap,comboBoxMap);
			    MPMResourceUtil.createUsingLinkWithPlant(zhizaodanwei,(WTPartMaster) tooling.getMaster());
            }
		} catch (Exception e) {
			e.printStackTrace();
			FormResult formResult = new FormResult();
			formResult.setStatus(FormProcessingStatus.FAILURE);
			formResult.addException(e);
			formResult.setNextAction(FormResultAction.NONE);
			return formResult;
		}

		return super.setResultNextAction(arg0, arg1, arg2);
	}

	private static void setGYFLIBA(MPMProcessMaterial material, String typeName, Map<String, String> textMap)
			throws WTException, WTPropertyVetoException, RemoteException {
		Map<String, String> ibaMap = new HashMap<String, String>();
		ibaMap.put(AttributeConstants.mindex, textMap.get(AttributeConstants.mindex));
		ibaMap.put(AttributeConstants.csize, textMap.get(AttributeConstants.csize));
		ibaMap.put(AttributeConstants.jstj, textMap.get(AttributeConstants.jstj));
		ibaMap.put(AttributeConstants.fjtj, textMap.get(AttributeConstants.fjtj));
		ibaMap.put(AttributeConstants.jldw, textMap.get(AttributeConstants.jldw));
		IBAHelper helper = new IBAHelper(material);
		helper.setIBAValue(material, ibaMap);

	}

    private static void setGZhuangIBA(MPMTooling tooling, String typeName, Map<String, String> textMap,HashMap<String, List<String>> comboBoxMap)
            throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(AttributeConstants.frocktype, textMap.get(AttributeConstants.frocktype));
        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, ibaMap);
    }

    private static void setGWWHIBA(MPMTooling tooling, String typeName, Map<String, String> textMap,HashMap<String, List<String>> comboBoxMap)
            throws WTException, WTPropertyVetoException, RemoteException {
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(AttributeConstants.workplace, textMap.get(AttributeConstants.workplace));
        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, ibaMap);
    }

    private static void setRemarkValue(IBAHolder ibaHolder,String value) throws WTException, WTPropertyVetoException, RemoteException{
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(AttributeConstants.remarkKey, value);
        IBAHelper helper = new IBAHelper(ibaHolder);
        helper.setIBAValue(ibaHolder, ibaMap);
    }

    private static void setEnglishNameValue(IBAHolder ibaHolder,String value) throws WTException, WTPropertyVetoException, RemoteException{
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(AttributeConstants.ENGLISHNAME, value);
        IBAHelper helper = new IBAHelper(ibaHolder);
        helper.setIBAValue(ibaHolder, ibaMap);
    }
    private static void setZzcjValue(IBAHolder ibaHolder,String value) throws WTException, WTPropertyVetoException, RemoteException{
        Map<String, String> ibaMap = new HashMap<String, String>();
        ibaMap.put(AttributeConstants.ZZCJ, value);
        IBAHelper helper = new IBAHelper(ibaHolder);
        helper.setIBAValue(ibaHolder, ibaMap);
    }

	private static void setSBIBA(MPMTooling tooling, String typeName, Map<String, String> textMap,HashMap<String, List<String>> comboBoxMap) throws WTException,
			WTPropertyVetoException, RemoteException {
		Map<String, String> ibaMap = new HashMap<String, String>();
		ibaMap.put(AttributeConstants.mindex, textMap.get(AttributeConstants.mindex));
		ibaMap.put(AttributeConstants.csize, textMap.get(AttributeConstants.csize));
		//ibaMap.put(AttributeConstants.equipmentType, comboBoxMap.get(AttributeConstants.equipmentType).get(0));
		IBAHelper helper = new IBAHelper(tooling);
		helper.setIBAValue(tooling, ibaMap);
	}

	private static void setDJIBA(MPMTooling tooling, String typeName,
			Map<String, String> textMap,
			HashMap<String, List<String>> comboBoxMap) throws WTException,
			WTPropertyVetoException, RemoteException {
		Map<String, String> ibaMap = new HashMap<String, String>();
		ibaMap.put(AttributeConstants.knifetype, textMap.get(AttributeConstants.knifetype));
		ibaMap.put(AttributeConstants.cmat, textMap.get(AttributeConstants.cmat));
		ibaMap.put(AttributeConstants.rkzj, textMap.get(AttributeConstants.rkzj));
		ibaMap.put(AttributeConstants.jczj, textMap.get(AttributeConstants.jczj));
		ibaMap.put(AttributeConstants.rkcd, textMap.get(AttributeConstants.rkcd));
		ibaMap.put(AttributeConstants.zcd, textMap.get(AttributeConstants.zcd));
		ibaMap.put(AttributeConstants.gc, textMap.get(AttributeConstants.gc));
		ibaMap.put(AttributeConstants.zxjgcc, textMap.get(AttributeConstants.zxjgcc));
		ibaMap.put(AttributeConstants.zdjgcc, textMap.get(AttributeConstants.zdjgcc));
		ibaMap.put(AttributeConstants.jgxs, textMap.get(AttributeConstants.jgxs));
		ibaMap.put(AttributeConstants.jklx, textMap.get(AttributeConstants.jklx));
		ibaMap.put(AttributeConstants.jsbz, textMap.get(AttributeConstants.jsbz));
		ibaMap.put(AttributeConstants.rkyjbj, textMap.get(AttributeConstants.rkyjbj));
		ibaMap.put(AttributeConstants.cs, textMap.get(AttributeConstants.cs));
		IBAHelper helper = new IBAHelper(tooling);
		helper.setIBAValue(tooling, ibaMap);
	}

	private static void setLJIBA(MPMTooling tooling, String typeName,
			Map<String, String> textMap,
			HashMap<String, List<String>> comboBoxMap) throws WTException,
			WTPropertyVetoException, RemoteException {
		Map<String, String> ibaMap = new HashMap<String, String>();
		ibaMap.put(AttributeConstants.mindex, textMap.get(AttributeConstants.mindex));
		ibaMap.put(AttributeConstants.csize, textMap.get(AttributeConstants.csize));
		IBAHelper helper = new IBAHelper(tooling);
		helper.setIBAValue(tooling, ibaMap);
	}
}
