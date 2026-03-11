package com.glaway.mpm.pbombuilder.data;

import java.io.Serializable;

import wt.fc.PersistenceHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;

import com.glaway.mpm.pbombuilder.util.CmSearchHelper;

/**
 * <br>Created on 2011-11-22
 * @author chenyunlong
 */
public class CmLightContainer implements Serializable { 
   private static final long serialVersionUID = -7438524094580412921L;
   
   private String name;
   private long id;
   private WTContainerRef containerRef;
   
   public CmLightContainer(String name) {
      WTContainer container = CmSearchHelper.getContainer(name);

      initialize(container);
   }
   
   public CmLightContainer(long id) {
      WTContainer container = null;
	try {
		container = (WTContainer) CmSearchHelper.search(WTContainer.class, id);
	} catch (Exception e) {
		e.printStackTrace();
	}
      
      initialize(container);
   }
   
   private void initialize(WTContainer container) {
      if (container == null)
         throw new RuntimeException("找不到指定名称的容器:" + name);
      
      this.name = container.getName();
      this.id = PersistenceHelper.getObjectIdentifier(container).getId();
      try {
         this.containerRef = WTContainerRef.newWTContainerRef(container);
      } catch (WTException e) {
         // ignore
      }
   }

   public String getName() {
      return name;
   }

   public long getId() {
      return id;
   }

   public WTContainerRef getContainerRef() {
      return containerRef;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      CmLightContainer other = (CmLightContainer) obj;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "CmLightContainer [id=" + id + ", name=" + name + "]";
   }
}
