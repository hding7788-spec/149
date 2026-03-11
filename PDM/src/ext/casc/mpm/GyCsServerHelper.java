package ext.casc.mpm;

import ext.casc.mpm.process.GLProcessParamDefinition;
import ext.casc.mpm.process.GLProcessParamValues;
import ext.casc.mpm.process.GLProcessParams;
import ext.casc.util.Tools;
import ext.sast.common.fc.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class GyCsServerHelper {
	private static Map<String,GLProcessParams> paramDefinitionMap = new HashMap<String, GLProcessParams>();
	private static Map<String,GLProcessParams> paramDefinitionNameMap = new HashMap<String, GLProcessParams>();
	private static Map<String,GLProcessParamDefinition> templateParamDefinitionMap = new HashMap<String, GLProcessParamDefinition>();

	public static GLProcessParams getProcessParamDefinition (String paramNumber ){
		return getProcessParamDefinition(paramNumber,true);

	}

    public static List<GLProcessParams> getProcessParamsByType(String type ){
        Map<String,String> queryParams = new HashMap<String, String>();
        queryParams.put(GLProcessParams.PARAMETER_CATEGORY,"知识参数");
        queryParams.put(GLProcessParams.KNOWLEDGETYPE,type);
        List<GLProcessParams> ps = queryGLObjects(GLProcessParams.class,queryParams);
        return ps;
    }

    public static List<GLProcessParams> getProcessParamsByTypeAndZhuanYe(String type ,String zhuanye){
        Map<String,String> queryParams = new HashMap<String, String>();
        queryParams.put(GLProcessParams.PARAMETER_CATEGORY,"知识参数");
        queryParams.put(GLProcessParams.KNOWLEDGETYPE,type);
        queryParams.put(GLProcessParams.PROCESS_CATEGORY,zhuanye);
        List<GLProcessParams> ps = queryGLObjects(GLProcessParams.class,queryParams);
        return ps;
    }

	public static GLProcessParams getProcessParamDefinition (String paramNumber ,boolean cache){
		if(cache){
			if(paramDefinitionMap.get(paramNumber)!=null){
				return paramDefinitionMap.get(paramNumber);
			}

			CmPersistable p = queryGLObject(GLProcessParams.class,GLProcessParams.GYNUMBER,paramNumber);
			if(p!=null){
				paramDefinitionMap.put(paramNumber, (GLProcessParams)p);
				return (GLProcessParams)p;
			}else{
				return null;
			}
		}else{
			CmPersistable p = queryGLObject(GLProcessParams.class,GLProcessParams.GYNUMBER,paramNumber);
			return (GLProcessParams)p;
		}


	}

    public static List<GLProcessParams> queryAllProcessParams (){
        List list = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParams.class);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                list.add(qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;

    }


    public static List<GLProcessParams> queryAllProcessParams (List<CommonQueryParams> params){
        List list = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParams.class);
            boolean needAdd = false;
            for(CommonQueryParams param:params){
                if(needAdd){
                    qs.appendAnd();
                }
                qs.appendWhere(param.getKey(),param.getFuhao(),param.getValue());
                needAdd= true;
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                list.add(qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;

    }


    public static List<GLProcessParamValues> queryProcessParamInstances(Map<String,String> params) throws Exception {
        List<GLProcessParamValues> list = new ArrayList();
        CmQuerySpec qs = new CmQuerySpec(GLProcessParamValues.class);
        boolean needAdd = false;
        Set<String> keys = params.keySet();
        for(String key:keys){
            if(needAdd){
                qs.appendAnd();
            }
            qs.appendWhere(key,CmQuerySpec.EQUAL,params.get(key));
            needAdd= true;
        }
        CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
        while (qr.hasNext()) {
            list.add((GLProcessParamValues) qr.next());
        }
        return list;
    }

    public static List queryGLObjects(Class clazz , Map<String,String> queryParams) {
        List list = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(clazz);
            qs.appendWhere("1",CmQuerySpec.EQUAL,"1");
            Set<Map.Entry<String, String>> entrySet = queryParams.entrySet();

            for(Map.Entry<String, String> entry:entrySet){
                if(!Tools.isNull(entry.getValue())){
                    qs.appendAnd();
                    qs.appendWhere(entry.getKey(),CmQuerySpec.EQUAL,entry.getValue());
                }
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                list.add(qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static CmPersistable queryGLObject(Class clazz , Map<String,String> queryParams) {
        try {
            CmQuerySpec qs = new CmQuerySpec(clazz);
            qs.appendWhere("1",CmQuerySpec.EQUAL,"1");
            Set<Map.Entry<String, String>> entrySet = queryParams.entrySet();

            for(Map.Entry<String, String> entry:entrySet){
                if(!Tools.isNull(entry.getValue())){
                    qs.appendAnd();
                    qs.appendWhere(entry.getKey(),CmQuerySpec.EQUAL,entry.getValue());
                }
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            if (qr.hasNext()) {
                return (CmPersistable)qr.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static CmPersistable queryGLObject(Class clazz ,String key ,String value) {
        try {
            CmQuerySpec qs = new CmQuerySpec(clazz);
            qs.appendWhere(key,CmQuerySpec.EQUAL,value);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                return  (CmPersistable)qr.next();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static List queryGLObjects(Class clazz ,String key ,String value) {
    	 List list = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(clazz);
            qs.appendWhere(key,CmQuerySpec.EQUAL,value);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                 list.add(qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public static GLProcessParamDefinition getGLProcessParamDefinition(String templateId, String paramNumber) {
        String key = templateId+"_"+paramNumber;
        if(templateParamDefinitionMap.get(key)!=null){
            return templateParamDefinitionMap.get(key);
        }
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParamDefinition.class);
            qs.appendWhere(GLProcessParamDefinition.TEMPLATEID, CmQuerySpec.EQUAL, templateId);
            qs.appendAnd();
            qs.appendWhere(GLProcessParamDefinition.GYPARAMNUMBER, CmQuerySpec.EQUAL, paramNumber);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            if (qr.hasNext()) {
                GLProcessParamDefinition p =  (GLProcessParamDefinition) qr.next();
                if(p!=null){
                    templateParamDefinitionMap.put(key, p);
                    return p;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static List<GLProcessParamDefinition> queryGLProcessParamDefinition(String templateId) {
        List<GLProcessParamDefinition> result = new ArrayList();
        try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParamDefinition.class);
            qs.appendWhere(GLProcessParamDefinition.TEMPLATEID, CmQuerySpec.EQUAL, templateId);
            //qs.appendOrderBy("ROWNUM", false);
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            while (qr.hasNext()) {
                result.add((GLProcessParamDefinition) qr.next());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static GLProcessParams getProcessParamDefinitionByName (String paramName ,boolean cache){
        if(cache){
            if(paramDefinitionNameMap.get(paramName)!=null){
                return paramDefinitionNameMap.get(paramName);
            }

            CmPersistable p = queryGLObject(GLProcessParams.class,GLProcessParams.GYNAME,paramName);
            if(p!=null){
                paramDefinitionNameMap.put(paramName, (GLProcessParams)p);
                return (GLProcessParams)p;
            }else{
                return null;
            }
        }else{
            CmPersistable p = queryGLObject(GLProcessParams.class,GLProcessParams.GYNUMBER,paramName);
            return (GLProcessParams)p;
        }


    }
}
