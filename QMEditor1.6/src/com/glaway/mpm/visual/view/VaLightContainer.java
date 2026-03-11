package com.glaway.mpm.visual.view;

import java.io.Serializable;

import wt.fc.PersistenceHelper;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.util.WTException;

import com.glaway.mpm.visual.util.VaSearchHelper;

/**
 * <br>Created on 2011-2-22
 * @author Dennis Huang - ���ٽ�
 */
public class VaLightContainer implements Serializable { 
   private static final long serialVersionUID = -7438524094580412921L;
   
   private String name;
   private long id;
   private WTContainerRef containerRef;
   
   public VaLightContainer(String name) {
      WTContainer container = null;
	try {
		container = VaSearchHelper.getContainer(name);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

      initialize(container);
   }
   
   public VaLightContainer(long id) {
      WTContainer container = null;
	try {
		container = (WTContainer) VaSearchHelper.search(WTContainer.class, id);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      
      initialize(container);
   }
   
   private void initialize(WTContainer container) {
      if (container == null)
         throw new RuntimeException("�Ҳ���ָ����Ƶ�����:" + name);
      
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
      VaLightContainer other = (VaLightContainer) obj;
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
      return "VaLightContainer [id=" + id + ", name=" + name + "]";
   }
}
