package com.glaway.mpm.visual.bean;

import java.util.HashMap;
import java.util.Map;

import com.glaway.mpm.visual.util.VaXML;
import com.glaway.mpm.visual.view.tree.VaTreeNode;
import com.glaway.mpm.visual.view.tree.VaTreeNodeAttributProxy;
import com.glaway.mpm.visual.view.tree.VaTreeNodeStructureProxy;


/**
 * 
 * @author ylchen
 *
 */
public class VaXmlDataProxy {

   private static final long       serialVersionUID       = 5003012084200820319L;
   /**
    *XML总顶级节点名
    */
   public static final String      subject                = "SUBJECT";

   private Map<String, VaXmlProxy> proxyPool              = new HashMap<String, VaXmlProxy>();

   public static final String      MBOM_STRUCTURE_PROXY   = "mbom_structure";
   public static final String      SPSBOM_STRUCTURE_PROXY = "spsbom_structure";

   public static final String      MBOM_ATTRIBUTE_PROXY   = "mbom_attribut";
   public static final String      SPSBOM_ATTRIBUTE_PROXY = "spsbom_attribut";

   public static final String      AO_STRUCTURE_M_PROXY   = "ao_structure_mbom";
   public static final String      AO_STRUCTURE_E_PROXY   = "ao_structure_ebom";
   public static final String      AO_ATTRIBUT_M_PROXY    = "ao_attribut_mbom";

   private static VaXmlDataProxy   instance;

   public static VaXmlDataProxy getVaXmlDataProxy() {
      if (instance == null)
         instance = new VaXmlDataProxy();
      return instance;
   }

   private VaXmlDataProxy() {}

   public void registerStructureProxy(String key) {
      registerProxy(key,new VaTreeNodeStructureProxy());
   }

   public void registerStructureProxy(String key, VaTreeNodeStructureProxy proxy) {
      registerProxy(key,proxy);
   }

   public void registerAttributProxy(String key) {
         registerProxy(key, new VaTreeNodeAttributProxy());
   }

   public void registerAttributProxy(String key, VaTreeNodeAttributProxy proxy) {
      registerProxy(key, proxy);
   }

   private void registerProxy(String key,VaXmlProxy proxy){
      if (!proxyPool.containsKey(key)){
         proxyPool.put(key, proxy);
      }
   }

   public VaXmlProxy getProxy(String key) {
      return proxyPool.get(key);
   }

   public Map<String, VaXmlProxy> getProxyPool() {
      return proxyPool;
   }

   public VaTreeNodeStructureProxy buildStructure(String key, VaXML xml) {
      VaTreeNodeStructureProxy structure = (VaTreeNodeStructureProxy) getProxy(key);
     return  buildStructure(structure,xml);
   }

   public VaTreeNodeStructureProxy buildStructure(VaTreeNodeStructureProxy proxy, VaXML xml) {
      proxy.parse(xml);
      return proxy;
   }

   public VaTreeNodeAttributProxy buildAttribut(String key, VaTreeNode node, VaXML xml) {
      VaTreeNodeAttributProxy attribut = (VaTreeNodeAttributProxy) getProxy(key);
      return  buildAttribut(attribut,node,xml);
   }

   public VaTreeNodeAttributProxy buildAttribut(VaTreeNodeAttributProxy proxy, VaTreeNode node, VaXML xml) {
      if (xml == null || node == null)
         return proxy;
      proxy.parse(node, xml);
      return proxy;
   }

   public VaXML toXML() {
      VaXML xml = new VaXML(subject);
      return xml;
   }

   public VaXML toXML(String... key) {
      VaXML xml = new VaXML(subject);
      for (String k : key) {
         
         VaXML k_xml = this.getProxy(k).toXML();
         xml.append(k_xml);
      }
      return xml;
   }
}
