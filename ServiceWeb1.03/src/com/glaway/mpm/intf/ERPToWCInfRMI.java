package com.glaway.mpm.intf;

import com.glaway.mpm.intf.workproceduce.ErpPbomExcel;
import com.glaway.mpm.pbom.db.ERPService;
import com.glaway.mpm.pbom.db.Wzk;
import com.glaway.mpm.util.*;
import ext.ases.techMaterial.TechnicsMaterialEntries;
import ext.ases.techMaterial.bean.*;
import ext.ases.techMaterial.gwpersistable.GwPersistenceHelper;
import ext.ases.techMaterial.gwpersistable.GwQueryResult;
import ext.ases.techMaterial.gwpersistable.GwQuerySpec;
import ext.ases.techMaterial.model.*;
import ext.ases.techMaterial.util.TechnicsMaterialUtils;
import ext.casc.sop.util.StringUtil;
import ext.casc.util.Tools;
import net.sf.jxls.exception.ParsePropertyException;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.soap.rpc.Call;
import org.apache.soap.rpc.Parameter;
import org.apache.soap.rpc.Response;
import org.apache.soap.transport.http.SOAPHTTPConnection;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.BeanUtils;
import wt.content.ApplicationData;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.container._WTContainer;
import wt.inf.library.WTLibrary;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.beans.PropertyVetoException;
import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class ERPToWCInfRMI implements RemoteAccess {

    /**
     * 查询物资
     *
     * @param dbtype   数据库类型 01：优选物资库    02：ERP物资库
     * @param wzlb     物资类别  01：元器件  02：标准紧固件 03：金属材料 04：非金属材料 05：复合材料 06：机电产品 07：火工品
     * @param wzbm     物资编码
     * @param wzmc     物资名称
     * @param xhphcl   型号/牌号/材料
     * @param gg       规格
     * @param jstj     技术条件
     * @param sccj     生产厂家
     * @param zjldw    主计量单位
     * @param fjtj     附加条件
     * @param lwgggccc 螺纹规格/公称尺寸
     * @param jxxndj   机械性能等级
     * @param zldj     质量等级
     * @param fzxs     封装形式
     * @param jddj     精度等级
     * @return List<Wzk> 物资列表
     */
    public static List<Wzk> queryWzk(String dbtype, String wzlb, String wzbm, String wzmc, String xhphcl, String gg,
                                     String jstj, String sccj, String zjldw, String fjtj, String lwgggccc, String jxxndj, String zldj, String fzxs, String jddj) {
        ERPService service = new ERPService();
        return service.queryWzk(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj, lwgggccc, jxxndj, zldj, fzxs, jddj);
    }

    public static List<Wzk> queryWzk2(String dbtype, String wzlb, String wzbm, String wzmc, String xhphcl, String gg,
                                      String jstj, String sccj, String zjldw, String fjtj, String lwgggccc, String jxxndj, String zldj, String fzxs, String jddj) {
        ERPService service = new ERPService();
        return service.queryWzk2(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj, lwgggccc, jxxndj, zldj, fzxs, jddj);
    }

    /**
     * 查询物资
     *
     * @param dbtype  数据库类型 01：优选物资库    02：ERP物资库
     * @param wzlb    物资类别  01：元器件  02：标准紧固件 03：金属材料 04：非金属材料 05：复合材料 06：机电产品 07：火工品
     * @param wzbm    物资编码
     * @param wzmc    物资名称
     * @param xhphcl  型号/牌号/材料
     * @param gg      规格
     * @param jstj    技术条件
     * @param sccj    生产厂家
     * @param zjldw   主计量单位
     * @param fjtj    附加条件
     * @param gyztrcl 供应状态/热处理
     * @return List<Wzk> 物资列表
     */
    public static List<Wzk> queryWzk2(String dbtype, String wzlb, String wzbm, String wzmc, String xhphcl, String gg,
                                      String jstj, String sccj, String zjldw, String fjtj, String gyztrcl) {
        ERPService service = new ERPService();
        return service.queryWzk2(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj, gyztrcl);
    }

    /**
     * 查询 原材料/试件原材料 物资
     *
     * @param dbtype  数据库类型 01：优选物资库    02：ERP物资库
     * @param wzlb    物资类别    03：金属材料 04：非金属材料 05：复合材料 08：劳防、文版用品 0：全部
     * @param wzbm    物资编码
     * @param wzmc    物资名称
     * @param xhphcl  型号/牌号/材料
     * @param gg      规格
     * @param jstj    技术条件
     * @param sccj    生产厂家
     * @param zjldw   主计量单位
     * @param fjtj    附加条件
     * @param gyztrcl 供应状态/热处理
     * @return List<Wzk> 物资列表
     */
    public static List<Wzk> queryWzk3(String dbtype, String wzlb, String wzbm, String wzmc, String xhphcl, String gg,
                                      String jstj, String sccj, String zjldw, String fjtj, String gyztrcl) {
        ERPService service = new ERPService();
        return service.queryWzk3(dbtype, wzlb, wzbm, wzmc, xhphcl, gg, jstj, sccj, zjldw, fjtj, gyztrcl);
    }

    public static Wzk getWzkByInvcode(String dbtype, String invcode) {
        ERPService service = new ERPService();
        Wzk wzk = service.getWzkByInvcode(dbtype, invcode);
        return wzk;
    }

    public static boolean isExistDocument(String docNumber) throws WTException {
        WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(docNumber);
        if (document == null) {
            return false;
        } else {
            return true;
        }
    }


    public static String genDocSavePbomExcel(String containerId, String docNumber, List<Wzk> dataList) throws ParsePropertyException, InvalidFormatException, WTException, PropertyVetoException, IOException {
        WTContainer container = (WTContainer) Util.searchRMI(WTContainer.class, Long.parseLong(containerId));
        File file = ErpPbomExcel.pbomWriteExcel(docNumber, dataList);
        InputStream is = new FileInputStream(file);
        String folderPath = LoadConfig.getInstance().getPbomFloder();
        String documentType = LoadConfig.getInstance().getPbomDocumentType();
        byte[] bytes = IOUtils.toByteArray(is);
        WTDocument document = null;
        try {
            document = WTDocumentUtil.createDocument(docNumber, container, folderPath, documentType);
            document = WTDocumentUtil.setPrimaryForDocument(document, docNumber + ".xls", bytes);
        } finally {
            is.close();
        }
        if (file.exists()) {
            file.delete();
        }
        String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(document).getId());
        return oid;
    }

    public static String editDocSavePbomExcel(String docId, List<Wzk> dataList) throws WTException, FileNotFoundException, PropertyVetoException, IOException, ParsePropertyException, InvalidFormatException {
        WTDocument document = (WTDocument) Util.searchRMI(WTDocument.class, Long.parseLong(docId));
        File file = ErpPbomExcel.pbomWriteExcel(document.getName(), dataList);
        InputStream is = new FileInputStream(file);
        try {
            byte[] bytes = IOUtils.toByteArray(is);
            document = (WTDocument) WorkInProcessUtil.checkout(document);
            document = WTDocumentUtil.setPrimaryForDocument(document, document.getName() + ".xls", bytes);
            WorkInProcessUtil.checkin(document);
        } finally {
            is.close();
        }

        if (file.exists()) {
            file.delete();
        }

        String oid = String.valueOf(PersistenceHelper.getObjectIdentifier(document).getId());

        return oid;

    }


    public static List<Wzk> queryExcelPbom(String docId) throws WTException, PropertyVetoException, IOException {
        InputStream is = null;
        List<Wzk> list = null;
        WTDocument document = (WTDocument) Util.searchRMI(WTDocument.class, Long.parseLong(docId));
        ApplicationData ad = WTDocumentUtil.getPrimaryByDocument(document);
        try {
            is = WTDocumentUtil.applicationDataToInputStream(ad);
            list = ErpPbomExcel.pbomReadExcel(is);
        } finally {
            if (is != null) is.close();
        }
        return list;
    }

    public static byte[] savePbomToExcel(String fileName, List<Wzk> dataList) {
        try {
            return ErpPbomExcel.savePbomToExcel(fileName, dataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void refreshCache(String dbtype, String wzlb) {
        GLLogger.debug("ERPService.refreshCacheWzk 开始");
        ERPService.refreshCacheWzk(dbtype, wzlb);
        GLLogger.debug("ERPService.refreshCacheWzk 结束");
    }

    public static List getTechMaterialInfo(String type, Map<String, String> map) {
        List<Object> resultList = new ArrayList<Object>();
        try {
            Class<?> cusclass = null;
            if ("元器件".equals(type)) {
                cusclass = TMEEleComponentsPartLink.class;
            } else if ("标准紧固件".equals(type)) {
                cusclass = TMEStandPartLink.class;
            } else if ("金属材料".equals(type)) {
                cusclass = TMEMetallicPartLink.class;
            } else if ("非金属材料".equals(type)) {
                cusclass = TMENonMetallicPartLink.class;
            } else if ("复合材料".equals(type)) {
                cusclass = TMECompoundMaterialPartLink.class;
            } else if ("机电材料".equals(type)) {
                cusclass = TMEEleMachinePartLink.class;
            } else if ("火工品".equals(type)) {
                cusclass = TMEExpDevicePartLink.class;
            }
            if (map.size() == 0) {
                return resultList;
            }
            ext.ases.techMaterial.gwpersistable.GwQuerySpec qs = new ext.ases.techMaterial.gwpersistable.GwQuerySpec(cusclass);
            int i = 1;
            for (String key : map.keySet()) {
                String value = map.get(key);
                if("isC".equals(key)){
                    if(!"true".equals(value) && !"机电材料".equals(type) && !"火工品".equals(type)){
                        if (i > 1) {
                            qs.appendAnd();
                        }
                        qs.appendWhere("BMDJ", GwQuerySpec.NOT_EQUAL, "C");
                    }
                }else{
                    if (i > 1) {
                        qs.appendAnd();
                    }
                    qs.appendWhere(key, ext.ases.techMaterial.gwpersistable.GwQuerySpec.LIKE, "%" + value + "%");
                }
                i++;
            }
            if(!"机电材料".equals(type) && !"火工品".equals(type)){
                qs.appendAnd();
                qs.appendWhere("BMZT", GwQuerySpec.EQUAL, "启用");
            }

            ext.ases.techMaterial.gwpersistable.GwQueryResult qr = ext.ases.techMaterial.gwpersistable.GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                Object next = qr.next();
                if (next instanceof TMEEleComponentsPartLink) {
                    TMEEleComponentsPartLink obj = (TMEEleComponentsPartLink) next;
                    TMEEleComponentsPartLinkBean bean = new TMEEleComponentsPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMEStandPartLink) {
                    TMEStandPartLink obj = (TMEStandPartLink) next;
                    TMEStandPartLinkBean bean = new TMEStandPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMEMetallicPartLink) {
                    TMEMetallicPartLink obj = (TMEMetallicPartLink) next;
                    TMEMetallicPartLinkBean bean = new TMEMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMENonMetallicPartLink) {
                    TMENonMetallicPartLink obj = (TMENonMetallicPartLink) next;
                    TMENonMetallicPartLinkBean bean = new TMENonMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMECompoundMaterialPartLink) {
                    TMECompoundMaterialPartLink obj = (TMECompoundMaterialPartLink) next;
                    TMECompoundMaterialPartLinkBean bean = new TMECompoundMaterialPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMEEleMachinePartLink) {
                    TMEEleMachinePartLink obj = (TMEEleMachinePartLink) next;
                    TMEEleMachinePartLinkBean bean = new TMEEleMachinePartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMEExpDevicePartLink) {
                    TMEExpDevicePartLink obj = (TMEExpDevicePartLink) next;
                    TMEExpDevicePartLinkBean bean = new TMEExpDevicePartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    public static List getRealTechMaterialInfo(String type, Map<String, String> map) {
        List<Object> resultList = new ArrayList<Object>();
        try {
            Class<?> cusclass = null;
            if ("元器件".equals(type)) {
                cusclass = TMEEleComponentsPartLink.class;
            } else if ("标准紧固件".equals(type)) {
                cusclass = TMEStandPartLink.class;
            } else if ("金属材料".equals(type)) {
                cusclass = TMEMetallicPartLink.class;
            } else if ("非金属材料".equals(type)) {
                cusclass = TMENonMetallicPartLink.class;
            } else if ("复合材料".equals(type)) {
                cusclass = TMECompoundMaterialPartLink.class;
            } else if ("机电材料".equals(type)) {
                cusclass = TMEEleMachinePartLink.class;
            } else if ("火工品".equals(type)) {
                cusclass = TMEExpDevicePartLink.class;
            }
            if (map.size() == 0) {
                return resultList;
            }
            ext.ases.techMaterial.gwpersistable.GwQuerySpec qs = new ext.ases.techMaterial.gwpersistable.GwQuerySpec(cusclass);
            int i = 1;
            for (String key : map.keySet()) {
                if (i > 1 && i <= map.size()) {
                    qs.appendAnd();
                }
                String value = map.get(key);
                qs.appendWhere(key, GwQuerySpec.EQUAL, value);
                i++;
            }
            ext.ases.techMaterial.gwpersistable.GwQueryResult qr = ext.ases.techMaterial.gwpersistable.GwPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                Object next = qr.next();
                if (next instanceof TMEEleComponentsPartLink) {
                    TMEEleComponentsPartLink obj = (TMEEleComponentsPartLink) next;
                    TMEEleComponentsPartLinkBean bean = new TMEEleComponentsPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMEStandPartLink) {
                    TMEStandPartLink obj = (TMEStandPartLink) next;
                    TMEStandPartLinkBean bean = new TMEStandPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMEMetallicPartLink) {
                    TMEMetallicPartLink obj = (TMEMetallicPartLink) next;
                    TMEMetallicPartLinkBean bean = new TMEMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMENonMetallicPartLink) {
                    TMENonMetallicPartLink obj = (TMENonMetallicPartLink) next;
                    TMENonMetallicPartLinkBean bean = new TMENonMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMECompoundMaterialPartLink) {
                    TMECompoundMaterialPartLink obj = (TMECompoundMaterialPartLink) next;
                    TMECompoundMaterialPartLinkBean bean = new TMECompoundMaterialPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMEEleMachinePartLink) {
                    TMEEleMachinePartLink obj = (TMEEleMachinePartLink) next;
                    TMEEleMachinePartLinkBean bean = new TMEEleMachinePartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMEExpDevicePartLink) {
                    TMEExpDevicePartLink obj = (TMEExpDevicePartLink) next;
                    TMEExpDevicePartLinkBean bean = new TMEExpDevicePartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                }
            }
        } catch (Exception e) {

        }
        return resultList;
    }

    /**
     * 根据属性创建相应的工艺物资条目
     *
     * @return
     */
    public static Object createTechniMaterial(Map<String, String> conditionMap, String type,String number) throws WTException, WTPropertyVetoException, SQLException {
        boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
        //wt.session.SessionHelper.manager.setAdministrator();
        Object linkInfo = null;
        String user = wt.session.SessionHelper.manager.getPrincipal().getName();
        String cusclass = "";
        String cusType = "";
        Class cusClass = null;
        if ("元器件".equals(type)) {
            cusclass = "TMEELECOMPONENTSPARTLINK";
            cusType = "电子元器件";
            cusClass = TMEEleComponentsPartLink.class;
        } else if ("标准紧固件".equals(type)) {
            cusclass = "TMESTANDPARTLINK";
            cusType = "标准紧固件";
            cusClass = TMEStandPartLink.class;
        } else if ("金属材料".equals(type)) {
            cusclass = "TMEMETALLICPARTLINK";
            cusType = "金属材料";
            cusClass = TMEMetallicPartLink.class;
        } else if ("非金属材料".equals(type)) {
            cusclass = "TMENONMETALLICPARTLINK";
            cusType = "非金属材料";
            cusClass = TMENonMetallicPartLink.class;
        } else if ("复合材料".equals(type)) {
            cusclass = "TMECOMPOUNDMATERIALPARTLINK";
            cusType = "复合材料";
            cusClass = TMECompoundMaterialPartLink.class;
        } else if ("机电材料".equals(type)) {
            cusclass = "TMEELEMACHINEPARTLINK";
            cusType = "机电材料";
            cusClass = TMEEleMachinePartLink.class;
        } else if ("火工品".equals(type)) {
            cusclass = "TMEEXPDEVICEPARTLINK";
            cusType = "火工品";
            cusClass = TMEExpDevicePartLink.class;
        }
        TechnicsMaterialEntries material = null;
        try {
            material = TechnicsMaterialEntries.newTechnicsMaterial();
            // 设置生命周期为已发布
            LifeCycleState materialState = LifeCycleState.newLifeCycleState();
            materialState.setState(State.toState("APPROVED"));
            material.setState(materialState);
            material.setName(conditionMap.get("WZMC"));
            material.setNumber(number);
            material.setDescription(cusType);
            WTContainerRef wtContainerRef = getWTContainerRef(WTContainer.class, "工艺物资信息库");
            long nowtime = Calendar.getInstance().getTimeInMillis();
            Timestamp createStamp = new Timestamp(nowtime);
            material.setContainerReference(wtContainerRef);
            Folder folder = FolderHelper.service.getFolder("/Default/工艺物资条目/"+cusType, wtContainerRef);
            FolderHelper.assignLocation(material, folder);
            material = (TechnicsMaterialEntries) PersistenceServerHelper.manager.store(material, createStamp, createStamp);
            String id = material.getPersistInfo().getObjectIdentifier().getStringValue();
            // 建立关联关系
            conditionMap.put("WZBM", number);
            TechnicsMaterialUtils.saveTechnicsMaterialEntriesLink(id, conditionMap, cusclass);
            linkInfo = getLinkInfo(id, cusClass);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(enforce);
        }
        return linkInfo;
    }

    /**
     * 根据工艺物资条目id获取详细信息
     *
     * @return
     * @throws Exception
     */
    public static Object getLinkInfo(String oid, Class cusClass) throws Exception {
        Object next = null;
        if (!StringUtil.isEmpty(oid)) {
            GwQuerySpec qs = new GwQuerySpec(cusClass);
            qs.appendWhere("TECHNICSMATERIALENTRIESID", GwQuerySpec.EQUAL, oid);
            ext.ases.techMaterial.gwpersistable.GwQueryResult qr = ext.ases.techMaterial.gwpersistable.GwPersistenceHelper.manager.find(qs);
            if (qr.hasNext()) {
                next = (Object) qr.next();
                if (next instanceof TMEEleComponentsPartLink) {
                    TMEEleComponentsPartLink obj = (TMEEleComponentsPartLink) next;
                    TMEEleComponentsPartLinkBean bean = new TMEEleComponentsPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEStandPartLink) {
                    TMEStandPartLink obj = (TMEStandPartLink) next;
                    TMEStandPartLinkBean bean = new TMEStandPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEMetallicPartLink) {
                    TMEMetallicPartLink obj = (TMEMetallicPartLink) next;
                    TMEMetallicPartLinkBean bean = new TMEMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMENonMetallicPartLink) {
                    TMENonMetallicPartLink obj = (TMENonMetallicPartLink) next;
                    TMENonMetallicPartLinkBean bean = new TMENonMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMECompoundMaterialPartLink) {
                    TMECompoundMaterialPartLink obj = (TMECompoundMaterialPartLink) next;
                    TMECompoundMaterialPartLinkBean bean = new TMECompoundMaterialPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEEleMachinePartLink) {
                    TMEEleMachinePartLink obj = (TMEEleMachinePartLink) next;
                    TMEEleMachinePartLinkBean bean = new TMEEleMachinePartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEExpDevicePartLink) {
                    TMEExpDevicePartLink obj = (TMEExpDevicePartLink) next;
                    TMEExpDevicePartLinkBean bean = new TMEExpDevicePartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                }
            }
        }
        return null;
    }

    /**
     * 查询容器根据名称
     *
     * @param kass
     * @param name
     * @return
     * @throws WTException
     */
    public static WTContainerRef getWTContainerRef(Class kass, String name) throws WTException {
        try {
            WTLibrary lib = null;
            QuerySpec qs = new QuerySpec(WTLibrary.class);
            qs.appendWhere(new SearchCondition(WTLibrary.class, _WTContainer.NAME, SearchCondition.EQUAL, name, false));
            QueryResult qr = PersistenceHelper.manager.find(qs);
            WTContainer container;
            if (qr.hasMoreElements()) {
                container = (WTContainer) qr.nextElement();
                return WTContainerRef.newWTContainerRef(container);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 根据工艺物资条目id获取详细信息
     *
     * @return
     * @throws Exception
     */
    public static Object getTMELinkByWzbm(String wzbm, String type) throws Exception {
        Class<?> cusclass = null;
        if ("元器件".equals(type)) {
            cusclass = TMEEleComponentsPartLink.class;
        } else if ("标准紧固件".equals(type)) {
            cusclass = TMEStandPartLink.class;
        } else if ("金属材料".equals(type)) {
            cusclass = TMEMetallicPartLink.class;
        } else if ("非金属材料".equals(type)) {
            cusclass = TMENonMetallicPartLink.class;
        } else if ("复合材料".equals(type)) {
            cusclass = TMECompoundMaterialPartLink.class;
        } else if ("机电材料".equals(type)) {
            cusclass = TMEEleMachinePartLink.class;
        } else if ("火工品".equals(type)) {
            cusclass = TMEExpDevicePartLink.class;
        }
        if (!StringUtil.isEmpty(wzbm)) {
            GwQuerySpec qs = new GwQuerySpec(cusclass);
            qs.appendWhere("WZBM", GwQuerySpec.EQUAL, wzbm);
            ext.ases.techMaterial.gwpersistable.GwQueryResult qr = ext.ases.techMaterial.gwpersistable.GwPersistenceHelper.manager.find(qs);
            if (qr.hasNext()) {
                Object next = (Object) qr.next();
                if (next instanceof TMEEleComponentsPartLink) {
                    TMEEleComponentsPartLink obj = (TMEEleComponentsPartLink) next;
                    TMEEleComponentsPartLinkBean bean = new TMEEleComponentsPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEStandPartLink) {
                    TMEStandPartLink obj = (TMEStandPartLink) next;
                    TMEStandPartLinkBean bean = new TMEStandPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEMetallicPartLink) {
                    TMEMetallicPartLink obj = (TMEMetallicPartLink) next;
                    TMEMetallicPartLinkBean bean = new TMEMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMENonMetallicPartLink) {
                    TMENonMetallicPartLink obj = (TMENonMetallicPartLink) next;
                    TMENonMetallicPartLinkBean bean = new TMENonMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMECompoundMaterialPartLink) {
                    TMECompoundMaterialPartLink obj = (TMECompoundMaterialPartLink) next;
                    TMECompoundMaterialPartLinkBean bean = new TMECompoundMaterialPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEEleMachinePartLink) {
                    TMEEleMachinePartLink obj = (TMEEleMachinePartLink) next;
                    TMEEleMachinePartLinkBean bean = new TMEEleMachinePartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEExpDevicePartLink) {
                    TMEExpDevicePartLink obj = (TMEExpDevicePartLink) next;
                    TMEExpDevicePartLinkBean bean = new TMEExpDevicePartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                }
            }
        }
        return null;
    }

    /**
     * 保存未被申请的编码信息
     *
     * @param set
     * @param technicsDocNumber
     */
    public static void saveTechnicaQuotaInfo(Set<String> set, String technicsDocNumber, String type) throws Exception {
        WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(technicsDocNumber);
        String docOid = "";
        if (doc == null) {
            docOid = technicsDocNumber;
        } else {
            docOid = doc.getPersistInfo().getObjectIdentifier().getStringValue();
        }
        //先将docOid关联的数据清空
        ext.ases.techMaterial.gwpersistable.GwQuerySpec qs = new ext.ases.techMaterial.gwpersistable.GwQuerySpec(TechnicaQuotaNumber.class);
        qs.appendWhere("TECHNICSOID", GwQuerySpec.EQUAL, docOid);
        qs.appendAnd();
        qs.appendWhere("TECHMATERIALTYPE", GwQuerySpec.EQUAL, type);
        ext.ases.techMaterial.gwpersistable.GwQueryResult qr = ext.ases.techMaterial.gwpersistable.GwPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            TechnicaQuotaNumber next = (TechnicaQuotaNumber) qr.next();
            GwPersistenceHelper.manager.delete(next);
        }
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String time = simpleDateFormat.format(date);
        WTUser currentUser = (WTUser) SessionHelper.getPrincipal();
        for (String key : set) {
            TechnicaQuotaNumber technicaQuotaNumber = new TechnicaQuotaNumber();
            technicaQuotaNumber.setQuotanumber(key);
            technicaQuotaNumber.setTechnicsoid(docOid);
            technicaQuotaNumber.setTmcreatetime(time);
            technicaQuotaNumber.setTechmaterialtype(type);
            technicaQuotaNumber.setTmcreator(currentUser.getFullName());
            GwPersistenceHelper.manager.save(technicaQuotaNumber);
        }

    }


    /**
     * 保存未被申请的编码信息
     *
     * @param docOid
     * @param technicsDocNumber
     */
    public static void updateTechnicaQuotaInfo(String docOid, String technicsDocNumber) throws Exception {
        TechnicsMaterialUtils.updateTechnicaQuotaInfo(docOid, technicsDocNumber);
    }


    /**
     * 方法功能:
     * 校验工艺物资名称
     *
     * @param map
     * @return java.lang.String
     * @author MChen
     * @date 2021/2/23
     */
    public static String validateTmInfo(Map<String, String> map) throws SQLException {
        return TechnicsMaterialUtils.validateTmInfo(map);
    }


    /**
     * 获取物资分类信息
     *
     * @return
     */
    public static Map<String, List<String>> getWZFLInfo(String nodeName) throws SQLException {
        return TechnicsMaterialUtils.getWZFLInfo(nodeName);
    }

    /**
     * 判断条目是否有物资分类
     *
     * @param ids
     * @param type
     * @return
     */
    public static String validateWZFLData(String[] ids, String type) throws SQLException {
        return TechnicsMaterialUtils.validateWZFLData(ids, type);
    }

    /**
     * 更新工艺物资条目对应的物资分类
     *
     * @param ids
     * @param type
     * @return
     */
    public static void updateWZFLData(String[] ids, String type, String wzfl) throws SQLException {
        TechnicsMaterialUtils.updateWZFLData(ids, type, wzfl);
    }

    /**
     * 获取资源库相关属性
     *
     * @param partNumberList
     * @param ibaAttrMap
     * @return
     */
    public static Map<String, Map<String, String>> getResourcePartAttr(List<String> partNumberList, Map<String, String> ibaAttrMap) throws WTException {
        HashMap<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        for (int i = 0; i < partNumberList.size(); i++) {
            String partNumber = partNumberList.get(i);
            WTPart designPart = WTPartUtil.getLatestPartByNumberAndView(partNumber, "Design");
            if (designPart != null) {
                HashMap<String, String> ibaMap = new HashMap<String, String>();
                IBAHelper ibaHelper = new IBAHelper(designPart);
                for (String key : ibaAttrMap.keySet()) {
                    String s = ibaAttrMap.get(key);
                    String ibaValue = ibaHelper.getIBAValue(s);
                    ibaMap.put(key, ibaValue);
                }
                map.put(partNumber, ibaMap);
            }

        }
        return map;
    }

    /**
     * 获取工艺物资名称所有字典名称
     *
     * @return
     */
    public static Map<String, Vector<String>> getAllTechncisMaterialDic() throws SQLException {
        return TechnicsMaterialUtils.getAllTechncisMaterialDic();

    }


    /**
     * 获取唯一编码
     *
     * @return
     */
    public static String getTechnicsMaterialEntriesNum() {
        return TechnicsMaterialUtils.getUqipNumber();
    }

    /**
     * 调用设计资源库申请编码
     *
     * @return
     */
    public static String supplyNum(Map<String, String> attrMap, Map<String, String> ibaMap) throws WTException {
        WTUser principal = (WTUser) SessionHelper.getPrincipal();
        String userName = principal.getName() + "@149.sast.casc";
        String msg = "";
        Document document = DocumentHelper.createDocument();
        try {
            document.setXMLEncoding("UTF-8");
            Element paramsElement = document.addElement("PARAMS");
            Element basicElement = paramsElement.addElement("BASIC");
            String csnumber = attrMap.get("csnumber");
            String wzmc = attrMap.get("wzmc");
            String wzfl = attrMap.get("wzfl");
            String type = attrMap.get("type");
            String classNum = attrMap.get("classNum");
            String className = attrMap.get("className");
            basicElement.addElement("CSNUMBER").addText(csnumber);
            basicElement.addElement("NAME").addText(wzmc);
            basicElement.addElement("TYPE").addText(type);
            basicElement.addElement("CLASSIFICATIONNODE").addText(wzfl);
            // iba
            Element ibaElement = basicElement.addElement("IBA");
            ibaElement.addElement("CLASSNUM").addText(classNum);
            ibaElement.addElement("CLASSNAME").addText(className);
            ibaElement.addElement("CSNUMBER").addText(csnumber);
            ibaElement.addElement("SCOPE").addText("运载");
            ibaElement.addElement("DATASOURCE").addText("149厂");
            ibaElement.addElement("BMDJ").addText("C");
            for (String key : ibaMap.keySet()) {
                String value = ibaMap.get(key);
                if(Tools.isNull(value)){
                    value = "";
                }
                ibaElement.addElement(key).addText(value);
            }
            System.out.println("=====applyNum===" + document.asXML().toString());
            String partInfo = document.asXML().toString();
            URL url = new URL("http://10.112.1.209/Windchill/servlet/RPC");
            SOAPHTTPConnection st = new SOAPHTTPConnection();
            st.setUserName("wcadmin");
            st.setPassword("wcadmin");
            Vector params = new Vector();
            HashMap inputparams = new HashMap();
            inputparams.put("partInfo", partInfo);
            inputparams.put("attaUrl", "");
            inputparams.put("bmdj", "C");
            inputparams.put("unit", "149");
            inputparams.put("userName", userName);
            Iterator it = inputparams.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = (String) inputparams.get(key);
                params.addElement(new Parameter(key, java.lang.String.class, value, null));
            }
            String soapName = "zykApplyNumber";
            Call call = new Call();
            call.setSOAPTransport(st);
            call.setTargetObjectURI("urn:ie-soap-rpc:com.infoengine.soap");
            call.setMethodName(soapName);
            call.setEncodingStyleURI("http://schemas.xmlsoap.org/soap/encoding/");
            call.setParams(params);
            Response resp;
            resp = call.invoke(url, "urn:ie-soap-rpc:com.infoengine.soap!" + soapName);
            if (resp.generatedFault()) {
                org.apache.soap.Fault fault = resp.getFault();
                msg = fault.getFaultString();
            } else {
                Parameter ret = resp.getReturnValue();
                Object result = ret.getValue();
                msg = result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = "调用接口异常";
        }
        return msg;

    }

    /**
     * 获取设计编码对应的物资条目信息
     *
     * @param partNumberList
     * @param ibaAttrMap
     * @return
     */
    public static List<Object> getTechnicsMaterialInfoByPartNum(List<String> partNumberList, String partType) throws WTException {
        List<Object> resultList = new ArrayList<Object>();
        Class<?> cusclass = null;
        if ("元器件".equals(partType)) {
            cusclass = TMEEleComponentsPartLink.class;
        } else if ("标准紧固件".equals(partType)) {
            cusclass = TMEStandPartLink.class;
        } else if ("金属材料".equals(partType)) {
            cusclass = TMEMetallicPartLink.class;
        } else if ("非金属材料".equals(partType)) {
            cusclass = TMENonMetallicPartLink.class;
        } else if ("复合材料".equals(partType)) {
            cusclass = TMECompoundMaterialPartLink.class;
        }
        if (partNumberList.size() == 0) {
            return resultList;
        }
        try {
            GwQuerySpec qs = new GwQuerySpec(cusclass);
            qs.appendWhere("SJBM", GwQuerySpec.EQUAL, partNumberList.get(0));
            for (int i = 1; i < partNumberList.size(); i++) {
                qs.appendOr();
                qs.appendWhere("SJBM", GwQuerySpec.EQUAL, partNumberList.get(i));
            }
            GwQueryResult result = GwPersistenceHelper.manager.find(qs);
            while (result.hasNext()) {
                Object next = result.next();
                if (next instanceof TMEEleComponentsPartLink) {
                    TMEEleComponentsPartLink obj = (TMEEleComponentsPartLink) next;
                    TMEEleComponentsPartLinkBean bean = new TMEEleComponentsPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMEStandPartLink) {
                    TMEStandPartLink obj = (TMEStandPartLink) next;
                    TMEStandPartLinkBean bean = new TMEStandPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMEMetallicPartLink) {
                    TMEMetallicPartLink obj = (TMEMetallicPartLink) next;
                    TMEMetallicPartLinkBean bean = new TMEMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMENonMetallicPartLink) {
                    TMENonMetallicPartLink obj = (TMENonMetallicPartLink) next;
                    TMENonMetallicPartLinkBean bean = new TMENonMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                } else if (next instanceof TMECompoundMaterialPartLink) {
                    TMECompoundMaterialPartLink obj = (TMECompoundMaterialPartLink) next;
                    TMECompoundMaterialPartLinkBean bean = new TMECompoundMaterialPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    resultList.add(bean);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 根据工艺物资条目id获取详细信息
     *
     * @return
     * @throws Exception
     */
    public static Object getTMELinkBySjbm(String sjbm, String type) throws Exception {
        Class<?> cusclass = null;
        if ("元器件".equals(type)) {
            cusclass = TMEEleComponentsPartLink.class;
        } else if ("标准紧固件".equals(type)) {
            cusclass = TMEStandPartLink.class;
        } else if ("金属材料".equals(type)) {
            cusclass = TMEMetallicPartLink.class;
        } else if ("非金属材料".equals(type)) {
            cusclass = TMENonMetallicPartLink.class;
        } else if ("复合材料".equals(type)) {
            cusclass = TMECompoundMaterialPartLink.class;
        } else if ("机电材料".equals(type)) {
            cusclass = TMEEleMachinePartLink.class;
        } else if ("火工品".equals(type)) {
            cusclass = TMEExpDevicePartLink.class;
        }
        if (!StringUtil.isEmpty(sjbm)) {
            GwQuerySpec qs = new GwQuerySpec(cusclass);
            qs.appendWhere("SJBM", GwQuerySpec.EQUAL, sjbm);
            ext.ases.techMaterial.gwpersistable.GwQueryResult qr = ext.ases.techMaterial.gwpersistable.GwPersistenceHelper.manager.find(qs);
            if (qr.hasNext()) {
                Object next = (Object) qr.next();
                if (next instanceof TMEEleComponentsPartLink) {
                    TMEEleComponentsPartLink obj = (TMEEleComponentsPartLink) next;
                    TMEEleComponentsPartLinkBean bean = new TMEEleComponentsPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEStandPartLink) {
                    TMEStandPartLink obj = (TMEStandPartLink) next;
                    TMEStandPartLinkBean bean = new TMEStandPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEMetallicPartLink) {
                    TMEMetallicPartLink obj = (TMEMetallicPartLink) next;
                    TMEMetallicPartLinkBean bean = new TMEMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMENonMetallicPartLink) {
                    TMENonMetallicPartLink obj = (TMENonMetallicPartLink) next;
                    TMENonMetallicPartLinkBean bean = new TMENonMetallicPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMECompoundMaterialPartLink) {
                    TMECompoundMaterialPartLink obj = (TMECompoundMaterialPartLink) next;
                    TMECompoundMaterialPartLinkBean bean = new TMECompoundMaterialPartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEEleMachinePartLink) {
                    TMEEleMachinePartLink obj = (TMEEleMachinePartLink) next;
                    TMEEleMachinePartLinkBean bean = new TMEEleMachinePartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                } else if (next instanceof TMEExpDevicePartLink) {
                    TMEExpDevicePartLink obj = (TMEExpDevicePartLink) next;
                    TMEExpDevicePartLinkBean bean = new TMEExpDevicePartLinkBean();
                    BeanUtils.copyProperties(obj, bean);
                    return bean;
                }
            }
        }
        return null;
    }
}
