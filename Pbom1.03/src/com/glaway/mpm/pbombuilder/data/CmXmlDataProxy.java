package com.glaway.mpm.pbombuilder.data;

import java.util.HashMap;
import java.util.Map;
import com.glaway.mpm.pbombuilder.tree.CmTreeNode;
import com.glaway.mpm.pbombuilder.util.CmXML;

/**
 * 
 * @author ylchen
 *
 */
public class CmXmlDataProxy {

   private static final long       serialVersionUID       = 5003012084200820319L;
   /**
    *XML总顶级节点名
    */
   public static final String      subject                = "SUBJECT";

   private Map<String, CmXmlProxy> proxyPool              = new HashMap<String, CmXmlProxy>();

   public static final String      MBOM_STRUCTURE_PROXY   = "mbom_structure";
   public static final String      SPSBOM_STRUCTURE_PROXY = "spsbom_structure";

   public static final String      MBOM_ATTRIBUTE_PROXY   = "mbom_attribut";
   public static final String      SPSBOM_ATTRIBUTE_PROXY = "spsbom_attribut";

   public static final String      AO_STRUCTURE_M_PROXY   = "ao_structure_mbom";
   public static final String      AO_STRUCTURE_E_PROXY   = "ao_structure_ebom";
   public static final String      AO_ATTRIBUT_M_PROXY    = "ao_attribut_mbom";

   private static CmXmlDataProxy   instance;

   public static CmXmlDataProxy getCmXmlDataProxy() {
      if (instance == null)
         instance = new CmXmlDataProxy();
      return instance;
   }

   private CmXmlDataProxy() {}

   public void registerStructureProxy(String key) {
      registerProxy(key,new CmTreeNodeStructureProxy());
   }

   public void registerStructureProxy(String key, CmTreeNodeStructureProxy proxy) {
      registerProxy(key,proxy);
   }

   public void registerAttributProxy(String key) {
         registerProxy(key, new CmTreeNodeAttributProxy());
   }

   public void registerAttributProxy(String key, CmTreeNodeAttributProxy proxy) {
      registerProxy(key, proxy);
   }

   private void registerProxy(String key,CmXmlProxy proxy){
      if (!proxyPool.containsKey(key)){
         proxyPool.put(key, proxy);
      }
   }

   public CmXmlProxy getProxy(String key) {
      return proxyPool.get(key);
   }

   public Map<String, CmXmlProxy> getProxyPool() {
      return proxyPool;
   }

   public CmTreeNodeStructureProxy buildStructure(String key, CmXML xml) {
      CmTreeNodeStructureProxy structure = (CmTreeNodeStructureProxy) getProxy(key);
     return  buildStructure(structure,xml);
   }

   public CmTreeNodeStructureProxy buildStructure(CmTreeNodeStructureProxy proxy, CmXML xml) {
      proxy.parse(xml);
      return proxy;
   }

   public CmTreeNodeAttributProxy buildAttribut(String key, CmTreeNode node, CmXML xml) {
      CmTreeNodeAttributProxy attribut = (CmTreeNodeAttributProxy) getProxy(key);
      return  buildAttribut(attribut,node,xml);
   }

   public CmTreeNodeAttributProxy buildAttribut(CmTreeNodeAttributProxy proxy, CmTreeNode node, CmXML xml) {
      if (xml == null || node == null)
         return proxy;
      proxy.parse(node, xml);
      return proxy;
   }

   public CmXML toXML() {
      CmXML xml = new CmXML(subject);
      return xml;
   }

   public CmXML toXML(String... key) {
      CmXML xml = new CmXML(subject);
      for (String k : key) {
         CmXML k_xml = this.getProxy(k).toXML();
         xml.append(k_xml);
      }
      return xml;
   }
}
