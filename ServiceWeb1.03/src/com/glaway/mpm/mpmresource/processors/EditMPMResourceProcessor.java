package com.glaway.mpm.mpmresource.processors;

import com.glaway.mpm.mpmresource.AttributeConstants;
import com.glaway.mpm.mpmresource.TypeNameConstants;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.WorkInProcessUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.mpml.resource.*;
import wt.fc.PersistenceHelper;
import wt.iba.value.IBAHolder;
import wt.part.WTPartMaster;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.Workable;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditMPMResourceProcessor extends CustomerObjectFormProcessor {
    protected String redirectURL = null;

    @Override
    public FormResult doOperation(NmCommandBean arg0, List<ObjectBean> arg1) throws WTException {
        FormResult formResult = new FormResult();
        HashMap<String, String> newText = arg0.getText();
        System.out.println("---------newText----"+newText);
        HashMap<String, String> oldText = arg0.getOldText();
        System.out.println("---------oldText----"+oldText);

        HashMap<String, List<String>> newComboBox = arg0.getComboBox();
        HashMap<String, List<String>> oldComboBox = arg0.getOldComboBox();
        HashMap<String, String> newTextAreaMap = arg0.getTextArea();
        HashMap<String, String> oldTextAreaMap = arg0.getOldTextArea();
        HashMap<String, List<String>> newChecked = arg0.getChecked();

        NmOid pageNmOid = arg0.getPageOid();
        Object pageObject = pageNmOid.getRef();

        NmOid actionOid = arg0.getActionOid();
        Object actionObj = actionOid.getRef();
        try {
            Workable workable = WorkInProcessUtil.checkout((Workable) actionObj);
            String newNumber = newText.get(AttributeConstants.number);
            String oldNumber = oldText.get(AttributeConstants.number);
            String newName = newText.get(AttributeConstants.name);
            String oldName = oldText.get(AttributeConstants.name);
//            if (workable instanceof MPMTooling) {
//                MPMTooling mpmTooling1 = (MPMTooling) workable;
//                String typeName1 = TypedUtility.getTypeIdentifier(mpmTooling1).getTypename();
//                if(typeName1.equals(TypeNameConstants.CSXM)){
//                	oldName = oldComboBox.get("ParametersName").get(0);
//                	newName = newComboBox.get("ParametersName").get(0);
//                }
//            }
            if(newNumber!=null){
            	if (!newNumber.equals(oldNumber)) {
            		MPMResourceUtil.changeWTPartMasterNumber((WTPartMaster) workable.getMaster(), newNumber);
            	}
            }
            if(newName!=null){
            	if (!newName.equals(oldName)) {
            		MPMResourceUtil.changeWTPartMasterName((WTPartMaster) workable.getMaster(), newName);
            	}
            }

            if (workable instanceof MPMPlant) {
                MPMPlant mpmPlant = (MPMPlant) workable;
                mpmPlant = (MPMPlant) PersistenceHelper.manager.save(mpmPlant);
                changeRemarkValue(mpmPlant,newTextAreaMap,oldTextAreaMap);
            } else if (workable instanceof MPMProcessMaterial) {
                MPMProcessMaterial mpmProcessMaterial = (MPMProcessMaterial) workable;
                changeRemarkValue(mpmProcessMaterial,newTextAreaMap,oldTextAreaMap);
                changeGYFLAttribute(mpmProcessMaterial, newText, oldText);
            } else if (workable instanceof MPMSkill) {
                MPMSkill mpmSkill = (MPMSkill) workable;
                changeRemarkValue(mpmSkill,newTextAreaMap,oldTextAreaMap);
            } else if (workable instanceof MPMTooling) {
                MPMTooling mpmTooling = (MPMTooling) workable;
                String newEnglishName = newText.get(AttributeConstants.ENGLISHNAME);
                String oldEnglishName = oldText.get(AttributeConstants.ENGLISHNAME);
                if( newEnglishName!=null && oldEnglishName!=null && !"null".equals(newEnglishName)){
                	if(!newEnglishName.equals(oldEnglishName)){
                		 IBAHelper helper = new IBAHelper(mpmTooling);
                         helper.setIBAValue("EnglishName", newText.get("EnglishName"));
                         helper.updateAttributeContainer(mpmTooling);
                         helper.updateIBAHolder(mpmTooling);
                         mpmTooling = (MPMTooling) PersistenceHelper.manager.refresh(mpmTooling);
                	}
                }

                changeRemarkValue(mpmTooling,newTextAreaMap,oldTextAreaMap);
                String typeName = TypedUtility.getTypeIdentifier(mpmTooling).getTypename();
                if (typeName.contains(TypeNameConstants.SB)) {
                    changeSBAttribute(mpmTooling, newText, oldText, typeName,newComboBox,oldComboBox);
                } else if (typeName.contains(TypeNameConstants.GZhuang)) {
                    changeSpecGZhuangAttribute(mpmTooling, newText, oldText, typeName, newComboBox, oldComboBox);
                } else if (typeName.contains(TypeNameConstants.LJ)) {
                	changeLJAttribute(mpmTooling, newText, oldText, typeName, newComboBox, oldComboBox);
                } else if (typeName.contains(TypeNameConstants.DJ)) {
                	changeDJAttribute(mpmTooling, newText, oldText, typeName, newComboBox, oldComboBox);
                } else if (typeName.contains(TypeNameConstants.GJ)) {
                	changeLJAttribute(mpmTooling, newText, oldText, typeName, newComboBox, oldComboBox);
                } else if (typeName.contains(TypeNameConstants.YQYB)) {
                	changeLJAttribute(mpmTooling, newText, oldText, typeName, newComboBox, oldComboBox);
                }else if (typeName.contains(TypeNameConstants.GXMC)) {
                	changeGXMCAttribute(mpmTooling, newText, oldText, typeName, newComboBox, oldComboBox);
                }else if (typeName.equals(TypeNameConstants.CSXM)) {
                	changeCSXMAttribute(mpmTooling, newText, oldText, typeName, newComboBox, oldComboBox);
                }else if (typeName.contains(TypeNameConstants.CZGW)) {
                	changeCZGWAttribute(mpmTooling, newText, oldText, typeName, newComboBox, oldComboBox);
                }else if (typeName.contains(TypeNameConstants.ZYLB)) {
                	changeZYLBAttribute(mpmTooling, newText, oldText, typeName, newTextAreaMap, oldTextAreaMap);
                }else if (typeName.equals(TypeNameConstants.CSXMMC)) {
                	changeCSXMMCAttribute(mpmTooling, newText, oldText, typeName, newComboBox, oldComboBox);
                }else if (typeName.contains(TypeNameConstants.WZLB)) {
                	changeWZLBAttribute(mpmTooling, newText, oldText, typeName, newComboBox, oldComboBox);
                }

            } else if (workable instanceof MPMWorkCenter) {
                MPMWorkCenter mpmWorkCenter = (MPMWorkCenter) workable;
                changeRemarkValue(mpmWorkCenter,newTextAreaMap,oldTextAreaMap);
            }

            if (newChecked.get("idCheckIn") != null) {
                workable = WorkInProcessUtil.checkin(workable);
            }
            // 判断页面对象是否改变
            if (pageObject.equals(actionObj)) {
                if (!actionObj.equals(workable)) {
                    this.redirectURL = getURL(workable, "");
                }
            }
        } catch (WTPropertyVetoException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return super.doOperation(arg0, arg1);
    }

    private static void changeRemarkValue(IBAHolder ibaHolder, HashMap<String, String> newTextAreaMap,
            HashMap<String, String> oldTextAreaMap) throws WTException, WTPropertyVetoException, RemoteException {
        IBAHelper helper = new IBAHelper(ibaHolder);
        Map<String, String> ibaMap = new HashMap<String, String>();

               if (newTextAreaMap.get(AttributeConstants.remarkKey)!=null&&oldTextAreaMap.get(AttributeConstants.remarkKey)!=null) {
            if (!newTextAreaMap.get(AttributeConstants.remarkKey).equals(oldTextAreaMap.get(AttributeConstants.remarkKey))) {
                ibaMap.put(AttributeConstants.remarkKey, newTextAreaMap.get(AttributeConstants.remarkKey));
            }
            System.out.println("--------ibaMap-------"+ibaMap);
            helper.setIBAValue(ibaHolder, ibaMap);
        }

    }

    private static void changeGYFLAttribute(MPMProcessMaterial mpmProcessMaterial, Map<String, String> newText,
            Map<String, String> oldText) throws WTException, WTPropertyVetoException, RemoteException {
        IBAHelper helper = new IBAHelper(mpmProcessMaterial);
        Map<String, String> ibaMap = new HashMap<String, String>();

//        if (!newText.get(AttributeConstants.clph).equals(oldText.get(AttributeConstants.clph))) {
//            ibaMap.put(AttributeConstants.clph, newText.get(AttributeConstants.clph));
//        }
//        if (!newText.get(AttributeConstants.clgg).equals(oldText.get(AttributeConstants.clgg))) {
//            ibaMap.put(AttributeConstants.clgg, newText.get(AttributeConstants.clgg));
//        }
//        if (!newText.get(AttributeConstants.clbz).equals(oldText.get(AttributeConstants.clbz))) {
//            ibaMap.put(AttributeConstants.clbz, newText.get(AttributeConstants.clbz));
//        }
//        if (!newText.get(AttributeConstants.jldw).equals(oldText.get(AttributeConstants.jldw))) {
//            ibaMap.put(AttributeConstants.jldw, newText.get(AttributeConstants.jldw));
//        }

        newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        helper.setIBAValue(mpmProcessMaterial, newText);
    }

    private static void changeSpecGZhuangAttribute(MPMTooling tooling, Map<String, String> newText,
            Map<String, String> oldText,
                String typeName, HashMap<String, List<String>> newComboBox, HashMap<String, List<String>> oldComboBox)
            throws WTException, WTPropertyVetoException, RemoteException {
        IBAHelper helper = new IBAHelper(tooling);
//        Map<String, String> ibaMap = new HashMap<String, String>();
//
//        if (!newText.get(AttributeConstants.frocktype).equals(oldText.get(AttributeConstants.frocktype))) {
//            ibaMap.put(AttributeConstants.frocktype, newText.get(AttributeConstants.frocktype));
//        }

        newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        helper.setIBAValue(tooling, newText);
    }

    private static void changeSBAttribute(MPMTooling tooling, Map<String, String> newText, Map<String, String> oldText,
            String typeName,HashMap<String, List<String>> newComboBox,HashMap<String, List<String>> oldComboBox) throws WTException, WTPropertyVetoException, RemoteException {

//        Map<String, String> ibaMap = new HashMap<String, String>();
//        if (!newText.get(AttributeConstants.mindex).equals(oldText.get(AttributeConstants.mindex))) {
//            ibaMap.put(AttributeConstants.mindex, newText.get(AttributeConstants.mindex));
//        }
//
//        if (!newText.get(AttributeConstants.csize).equals(oldText.get(AttributeConstants.csize))) {
//            ibaMap.put(AttributeConstants.csize, newText.get(AttributeConstants.csize));
//        }
//
//        if(oldComboBox.get(AttributeConstants.equipmentType)!=null) {
//        	if (!newComboBox.get(AttributeConstants.equipmentType).get(0).equals(oldComboBox.get(AttributeConstants.equipmentType).get(0))) {
//                ibaMap.put(AttributeConstants.equipmentType, newComboBox.get(AttributeConstants.equipmentType).get(0));
//            }
//        }

        newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, newText);
    }

    private static void changeDJAttribute(MPMTooling tooling, Map<String, String> newText, Map<String, String> oldText,
            String typeName,HashMap<String, List<String>> newComboBox,HashMap<String, List<String>> oldComboBox) throws WTException, WTPropertyVetoException, RemoteException {

//        Map<String, String> ibaMap = new HashMap<String, String>();
//        if (newText.get(AttributeConstants.knifetype) != null
//        		&& !newText.get(AttributeConstants.knifetype).equals(oldText.get(AttributeConstants.knifetype))) {
//            ibaMap.put(AttributeConstants.knifetype, newText.get(AttributeConstants.knifetype));
//        }
//        if (newText.get(AttributeConstants.csize) != null
//        		&& !newText.get(AttributeConstants.csize).equals(oldText.get(AttributeConstants.csize))) {
//            ibaMap.put(AttributeConstants.csize, newText.get(AttributeConstants.csize));
//        }

    	newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, newText);
    }

    private static void changeLJAttribute(MPMTooling tooling, Map<String, String> newText, Map<String, String> oldText,
            String typeName,HashMap<String, List<String>> newComboBox,HashMap<String, List<String>> oldComboBox) throws WTException, WTPropertyVetoException, RemoteException {

//        Map<String, String> ibaMap = new HashMap<String, String>();
//        if (newText.get(AttributeConstants.mindex) != null
//        		&& !newText.get(AttributeConstants.mindex).equals(oldText.get(AttributeConstants.mindex))) {
//            ibaMap.put(AttributeConstants.mindex, newText.get(AttributeConstants.mindex));
//        }
//        if (newText.get(AttributeConstants.csize) != null
//        		&& !newText.get(AttributeConstants.csize).equals(oldText.get(AttributeConstants.csize))) {
//            ibaMap.put(AttributeConstants.csize, newText.get(AttributeConstants.csize));
//        }

        newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, newText);
        helper.updateAttributeContainer(tooling);
        helper.updateIBAHolder(tooling);
        tooling = (MPMTooling) PersistenceHelper.manager.refresh(tooling);

    }

    private static void changeGXMCAttribute(MPMTooling tooling, Map<String, String> newText, Map<String, String> oldText,
            String typeName,HashMap<String, List<String>> newComboBox,HashMap<String, List<String>> oldComboBox) throws WTException, WTPropertyVetoException, RemoteException {

//        Map<String, String> ibaMap = new HashMap<String, String>();
//        if (newText.get(AttributeConstants.mindex) != null
//        		&& !newText.get(AttributeConstants.mindex).equals(oldText.get(AttributeConstants.mindex))) {
//            ibaMap.put(AttributeConstants.mindex, newText.get(AttributeConstants.mindex));
//        }
//        if (newText.get(AttributeConstants.csize) != null
//        		&& !newText.get(AttributeConstants.csize).equals(oldText.get(AttributeConstants.csize))) {
//            ibaMap.put(AttributeConstants.csize, newText.get(AttributeConstants.csize));
//        }

        newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, newText);
        helper.setIBAValue("ZZCJ", newComboBox.get("ZZCJ").get(0));
        helper.setIBAValue("IsSheBeiGS", newComboBox.get("IsSheBeiGS").get(0));
        //helper.setIBAValue("SpecializedType", oldComboBox.get("SpecializedType").get(0));
        helper.updateAttributeContainer(tooling);
        helper.updateIBAHolder(tooling);
        tooling = (MPMTooling) PersistenceHelper.manager.refresh(tooling);

    }

    private static void changeZYLBAttribute(MPMTooling tooling, Map<String, String> newText, Map<String, String> oldText,
            String typeName,HashMap<String, String> newTextAreaMap,HashMap<String, String> oldTextAreaMap) throws WTException, WTPropertyVetoException, RemoteException {

        newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, newText);
        helper.updateAttributeContainer(tooling);
        helper.updateIBAHolder(tooling);
        tooling = (MPMTooling) PersistenceHelper.manager.refresh(tooling);

    }

    private static void changeCSXMAttribute(MPMTooling tooling, Map<String, String> newText, Map<String, String> oldText,
            String typeName,HashMap<String, List<String>> newComboBox,HashMap<String, List<String>> oldComboBox) throws WTException, WTPropertyVetoException, RemoteException {

        newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, newText);
        //helper.setIBAValue("SpecializedType", newComboBox.get("SpecializedType").get(0));
        //helper.setIBAValue("ProceduceName", newComboBox.get("ProceduceName").get(0));
        //helper.setIBAValue("ParametersName", newComboBox.get("ParametersName").get(0));
        //helper.setIBAValue("MaterialCategory", newComboBox.get("MaterialCategory").get(0));
        helper.setIBAValue("CANSHUZHI", newText.get("CANSHUZHI"));
        helper.updateAttributeContainer(tooling);
        helper.updateIBAHolder(tooling);
        tooling = (MPMTooling) PersistenceHelper.manager.refresh(tooling);

    }


    private static void changeCZGWAttribute(MPMTooling tooling, Map<String, String> newText, Map<String, String> oldText,
            String typeName,HashMap<String, List<String>> newComboBox,HashMap<String, List<String>> oldComboBox) throws WTException, WTPropertyVetoException, RemoteException {


        newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, newText);
        helper.setIBAValue("ZZCJ", newComboBox.get("ZZCJ").get(0));
        helper.updateAttributeContainer(tooling);
        helper.updateIBAHolder(tooling);
        tooling = (MPMTooling) PersistenceHelper.manager.refresh(tooling);

    }

    private static void changeCSXMMCAttribute(MPMTooling tooling, Map<String, String> newText, Map<String, String> oldText,
            String typeName,HashMap<String, List<String>> newComboBox,HashMap<String, List<String>> oldComboBox) throws WTException, WTPropertyVetoException, RemoteException {


        newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, newText);
        //helper.setIBAValue("SpecializedType", newComboBox.get("SpecializedType").get(0));
        helper.updateAttributeContainer(tooling);
        helper.updateIBAHolder(tooling);
        tooling = (MPMTooling) PersistenceHelper.manager.refresh(tooling);

    }

    private static void changeWZLBAttribute(MPMTooling tooling, Map<String, String> newText, Map<String, String> oldText,
            String typeName,HashMap<String, List<String>> newComboBox,HashMap<String, List<String>> oldComboBox) throws WTException, WTPropertyVetoException, RemoteException {


        newText.remove("number");
    	newText.remove("name");
    	System.out.println("------newText---"+newText);

        IBAHelper helper = new IBAHelper(tooling);
        helper.setIBAValue(tooling, newText);
        //helper.setIBAValue("SpecializedType", newComboBox.get("SpecializedType").get(0));
        helper.updateAttributeContainer(tooling);
        helper.updateIBAHolder(tooling);
        tooling = (MPMTooling) PersistenceHelper.manager.refresh(tooling);

    }




}
