package com.glaway.mpm.visual.query;

import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ResultProcessor;
import wt.pds.StatementSpec;
import wt.query.template.ParameterTemplate;
import wt.query.template.ReportTemplateHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.xml.xslt.ReaderXMLSource;

/**
 */
public class QMLTemplateCache
{
   private static Properties qmlMap = new Properties();
   private static HashMap<String, StatementSpec> statementSpecMap = new HashMap<String, StatementSpec>();
    private static String wtHome;
   private static boolean isReloadable=false;
   private static boolean VERBOSE=false;
    static{
        try{ 
           wtHome = WTProperties.getLocalProperties().getProperty("wt.home"); 
           isReloadable = WTProperties.getLocalProperties().getProperty("ext.query.QMLTemplateCache.reloadable",false); 
           VERBOSE = WTProperties.getLocalProperties().getProperty("ext.query.QMLTemplateCache.verbose",false); 
        }catch(Exception ex){}
    }
    
    /**
    * Method getQMLString.
    * @param qmlPath String
    * @return String
    * @throws WTException
    */
   public static String getQMLString(String qmlPath) throws WTException {
      if(qmlMap.containsKey(qmlPath) && !isReloadable)
         return qmlMap.getProperty(qmlPath);
      synchronized(qmlMap){
         File qmlFile = new File(wtHome, "codebase/" + qmlPath.trim());
         if(qmlFile.exists()){
            try{
               StringWriter swri = new StringWriter();
               StreamHelper.copyReader2Writer(1024, new FileReader(qmlFile), swri);
               String qml = swri.toString();
               qmlMap.setProperty(qmlPath, qml);
               return qml;
            }catch(Exception ex){
               //ex.printStackTrace();
               throw new WTException(ex);
            }
         }else
            throw new WTException("qml file :" + qmlFile + " is missing on server!");
      }
   }
        
    //1. get (unbound) statement from cache (-) 
    //	-> better: get Parameter def (unbound) and cache it as well
    //2. get bound statement from cache + input arg hash (-) 
    //	-> better: bind it using cached ParamDef and StatementSpec
    // ->> open point: what about macros, constants, empty/missing valuess ?
    //3. do the query and get results (+)
    //4. get wtcollection from std result set (+)
        
    /**
     * gets an unbound (=without input args) StaementSpec from qml path
     * 
     * @param qmlPath 
     * @return wt.pds.StatementSpec unbound StatementSpec
    * @throws WTException
     */
    public static StatementSpec getStatementSpecFromCache(String qmlPath) throws WTException {
       if(statementSpecMap.containsKey(qmlPath) && !isReloadable)
         return (StatementSpec)statementSpecMap.get(qmlPath);
      synchronized(statementSpecMap){
         String qml = getQMLString(qmlPath);
         if(VERBOSE)System.out.println("in getStatementSpec; qml: "+qml);
         ReaderXMLSource xmlsource = ReportTemplateHelper.convertToReader(qml);
         ParameterTemplate[] ptpls = ReportTemplateHelper.buildParameterTemplates(xmlsource);
         Hashtable<String, Object> ptHt;
         if(ptpls != null){
            ptHt = new Hashtable<String, Object>(ptpls.length);
            for(int i = 0; i < ptpls.length; i++){
               ParameterTemplate ptpl = ptpls[i];
               if(VERBOSE) System.out.println("ParameterTemplate[" + i + "]: " + ptpl.getName() + "=" + ptpl.getDefaultValue() + " of type: " + ptpl.getType());
               Object val = ptpl.getDefaultValue();
               if(val == null){
                  val = new Object();
                  try{
//                     val = Class.forName(ptpl.getType()).newInstance();
                  }catch(Exception e){
                     val = new Object();
                     e.printStackTrace();
                  }
               }
               ptHt.put(ptpl.getName(), val);
            }
         }else
            ptHt = new Hashtable<String, Object>();
         if(VERBOSE)System.out.println("ptHt: "+ptHt);
         xmlsource = ReportTemplateHelper.convertToReader(qml);
         StatementSpec spec = ReportTemplateHelper.buildStatement(xmlsource, Locale.getDefault(), ptHt);
         statementSpecMap.put(qmlPath, spec);
         return spec;
      }
   }
    
    /**
     * @param qmlPath 
     * @param paramHash 
     * @return StatementSpec bound StatementSpec
    * @throws WTException
     */
    public static StatementSpec getStatementSpecFromCache(String qmlPath, Hashtable<?, ?> paramHash) throws WTException {
       StatementSpec spec = getStatementSpecFromCache(qmlPath);
       if(VERBOSE)System.out.println("StatementSpec before bind: "+spec+", paramHash: "+paramHash);
       String qml = getQMLString(qmlPath);
       ReportTemplateHelper.bindParameters(ReportTemplateHelper.convertToReader(qml), spec, paramHash);
       if(VERBOSE)System.out.println("StatementSpec after bind: "+spec);
       return spec;
    }
    
    /**
     * execute query and return results as QueryResult; bypassAccessControl is not supported!
     * 
     * @param qmlPath 
     * @param paramHash 
     * @return wt.fc.collections.WTArrayList 
    * @throws WTException
     */
    public static QueryResult query(String qmlPath, Hashtable<?, ?> paramHash) throws WTException {
       StatementSpec spec = getStatementSpecFromCache( qmlPath, paramHash);
       spec.setAdvancedQueryEnabled(true);       
       return PersistenceHelper.manager.find(spec);
    }

    /** 
     * execute query and return results
     * 
     * @param qmlPath 
     * @param paramHash 
     * @return wt.fc.collections.WTArrayList 
    * @throws WTException
     */
    public static ResultProcessor query(String qmlPath, Hashtable<?, ?> paramHash, ResultProcessor result) throws WTException {
       if(VERBOSE)System.out.println("QMLTemplateCache.query for qmlpath: "+qmlPath+" and args: "+paramHash);
       StatementSpec spec = getStatementSpecFromCache( qmlPath, paramHash);
       spec.setAdvancedQueryEnabled(true);
       return PersistenceHelper.manager.find(spec, result);
    }

}