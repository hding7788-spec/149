package com.glaway.mpm.visual.view.pview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.glaway.mpm.visual.bean.VaInputStreamData;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.visual.util.VaUtil;
import com.ptc.pview.pvloader.ProtocolHandlerEvents;
import com.ptc.pview.pvloader.RemoteIf;

public class VaHttpDownloadProtocolHandlerEvents extends ProtocolHandlerEvents {
   private static final String   CLASSNAME = VaHttpDownloadProtocolHandlerEvents.class.getName();
   private static final VaLogger log       = VaLogger.getLogger(CLASSNAME);

   private static int            threadCount;
   RemoteIf                      remoteIf;
   private ExecutorService       execPool;

   static {
      threadCount = 15;//CmSettings.getSection(CmSettings.SECTION_MBOM).get("pview.download.threadCount", 15);
   }

   public VaHttpDownloadProtocolHandlerEvents() {
      execPool = Executors.newFixedThreadPool(threadCount);
      log.debug("HttpDownloadProtocolHandlerEvents done !!");
   }

   synchronized public void DownloadFile(String purl0, String diskfile0, String handle0) {
      final String purl = purl0;
      final String diskfile = diskfile0;
      final String handle = handle0;
//      CmTaskHelper.postTask("mainframe.setStatus", "��������: " + purl0);

      Thread downloadRunner = new Thread() {
         public void run() {
            try {
               VaLogger.getLogger(CLASSNAME).debug("downloadAuthURLData: purl=" + purl);
            	
               VaInputStreamData isd = VaUtil.downloadAuthURLData(purl, true);
               
               
               InputStream i = isd.getInputStream();
               VaLogger.getLogger(CLASSNAME).debug("downloadAuthURLData: ret=" + (isd.getInputStream() != null));

               if (i != null) {
                  File fw = new File(diskfile);
                  byte buf[] = new byte[32768];
                  FileOutputStream fos = new FileOutputStream(fw);
                  int n;
                  while ((n = i.read(buf)) >= 0) {
                     fos.write(buf, 0, n);
                  }
                  fos.close();
               }
//               URL url = new URL(purl);
//               URLConnection urlconnection = url.openConnection();
//               File fw = new File(diskfile);
//               urlconnection.setRequestProperty("Accept-Encoding", "gzip");
//               String enc = urlconnection.getHeaderField("content-encoding");
//               InputStream i = null;
//               if (enc == null || enc.indexOf("gzip") == -1)
//                  i = urlconnection.getInputStream();
//               else
//                  i = new GZIPInputStream(urlconnection.getInputStream());
//               byte buf[] = new byte[32768];
//               FileOutputStream fos = new FileOutputStream(fw);
//               //             Adler32 checksum = new Adler32();
//               //             CRC32 checksum = new CRC32();
//               int n;
//               while ((n = i.read(buf)) >= 0) {
//                  fos.write(buf, 0, n);
//               }
//               fos.close();
               finishedDownload(handle);
            } catch (java.lang.Exception e) {
               log.error(e);
            }
         }

      };
      execPool.execute(downloadRunner);

      log.debug("downloadRunner.exec() act pool size: " + ((ThreadPoolExecutor) execPool).getActiveCount() + ", compl task count: "
         + ((ThreadPoolExecutor) execPool).getCompletedTaskCount());
   }

   synchronized public void finishedDownload(String handle) {
      try {
         remoteIf.DownloadFinished(handle);
         log.debug("成功下载");
//         if (((ThreadPoolExecutor) execPool).getActiveCount() == 1)
//            CmTaskHelper.postTask("mainframe.setStatus", "�Ѿ������������");
      } catch (java.lang.Exception e) {
         log.error(e);
      }
   }

   public void UploadFile(String arg0, String arg1, String arg2) {
//      log.debug("Upload not supported");
      throw new RuntimeException("Upload not supported");
   }

   public RemoteIf getRemoteIf() {
      return remoteIf;
   }

   public void setRemoteIf(RemoteIf remoteIf) {
      this.remoteIf = remoteIf;
   }
}
