package com.glaway.mpm.pbombuilder.tree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * <br>Created on 2012-10-23
 * @author chenyunlong
 */
public class CmInputStreamData implements Serializable {
   private static final long          serialVersionUID = -4794648134162711470L;
   private transient InputStream      is;
   private volatile transient boolean finished;

   public CmInputStreamData(InputStream is) {
      this.is = is;
      this.finished = true;
   }

   public InputStream getInputStream() {
      while (!isFinished()) {
         try {
            Thread.sleep(500);
         } catch (InterruptedException e) {
            // Ignore
         }
      }
      return is;
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      int c = -1;
      byte[] buff = new byte[1024];
      while ((c = is.read(buff, 0, 1024)) != -1) {
         out.write(buff, 0, c);
      }
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      finished = false;
//      System.out.println("coming to CmInputStreamData readObject finished=false");
      
      in.defaultReadObject();
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      int c = -1;
      byte[] buf = new byte[1024];
      while ((c = in.read(buf, 0, 1024)) != -1) {
         bos.write(buf, 0, c);
      }

      is = new ByteArrayInputStream(bos.toByteArray());
      finished = true;
//      System.out.println("coming to CmInputStreamData readObject finished=true");
   }

   public boolean isFinished() {
      return finished;
   }
}
