// Generated StandardDownloadService%4B4FF5330311: ??? 01/18/10 22:34:20
/* bcwti
 *
 * Copyright (c) 2008 Parametric Technology Corporation (PTC). All Rights
 * Reserved.
 *
 * This software is the confidential and proprietary information of PTC
 * and is subject to the terms of a software license agreement. You shall
 * not disclose such confidential information and shall use it only in accordance
 * with the terms of the license agreement.
 *
 * ecwti
 */

package ext.ases.envelope;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.log4j.LogR;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTProperties;

//##begin StandardDownloadService%4B4FF5330311.doc preserve=no
/**
 *
 * <p>
 * Use the <code>newStandardDownloadService</code> static factory method(s),
 * not the <code>StandardDownloadService</code> constructor, to construct
 * instances of this class.  Instances must be constructed using the static
 * factory(s), in order to ensure proper initialization of the instance.
 * <p>
 *
 *
 * @version   1.0
 **/
//##end StandardDownloadService%4B4FF5330311.doc

public class StandardDownloadService extends StandardManager implements DownloadService, Serializable {


   // --- Attribute Section ---


   private static final String RESOURCE = "ext.ases.envelope.envelopeResource";
   private static final String CLASSNAME = StandardDownloadService.class.getName();

   //##begin user.attributes preserve=yes
   private static final Logger log;
   public static ArrayList printList;
   public static ArrayList primaryList;
   public static ArrayList attachmentList;
   public static ArrayList wvsList;
   //##end user.attributes

   //##begin static.initialization preserve=yes

    static {
       try {
          log = LogR.getLogger(StandardDownloadService.class.getName());
       }
       catch (Exception e) {
          throw new ExceptionInInitializerError(e);
       }
    }
   //##end static.initialization


   // --- Operation Section ---

   //##begin getConceptualClassname%getConceptualClassnameg.doc preserve=no
   /**
    * Returns the conceptual (modeled) name for the class.
    *
    * <BR><BR><B>Supported API: </B>false
    *
    * @deprecated
    *
    * @return    String
    **/
   //##end getConceptualClassname%getConceptualClassnameg.doc

   public String getConceptualClassname() {
      //##begin getConceptualClassname%getConceptualClassnameg.body preserve=no

      return CLASSNAME;
      //##end getConceptualClassname%getConceptualClassnameg.body
   }

   //##begin newStandardDownloadService%newStandardDownloadServicef.doc preserve=no
   /**
    * Default factory for the class.
    *
    * @return    StandardDownloadService
    * @exception wt.util.WTException
    **/
   //##end newStandardDownloadService%newStandardDownloadServicef.doc

   public static StandardDownloadService newStandardDownloadService()
            throws WTException {
      //##begin newStandardDownloadService%newStandardDownloadServicef.body preserve=no

      StandardDownloadService instance = new StandardDownloadService();
      instance.initialize();
      return instance;
      //##end newStandardDownloadService%newStandardDownloadServicef.body
   }

   //##begin user.operations preserve=yes
   public String getContents( Vector vector, String s, String type )
            throws WTException {
		ArrayList arraylist = new ArrayList();
		printList = new ArrayList();
		primaryList = new ArrayList();
		attachmentList = new ArrayList();
		wvsList = new ArrayList();
		String fileName1 = new String();
		WTProperties wtp = null;
		String wtTemp = null;
		try{
			wtp = WTProperties.getLocalProperties();
			wtTemp = wtp.getProperty("wt.temp");
		
			File zipDir = new File(wtTemp+File.separator+s);
			log.debug("zipDir is:"+zipDir);
			if(!zipDir.exists())
				zipDir.mkdirs();
			File zipFile = new File(zipDir + File.separator + s + "_" + type + ".zip" );//File.createTempFile("tmp",".zip",zipDir);zipFile.cr
			log.debug("zipFile is:"+zipFile);
			fileName1 = zipFile.getName();
			log.debug("fileName is:"+fileName1);
			ZipOutputStream zos = new ZipOutputStream(zipFile);
			log.debug("zos is:"+zos);
			byte[] buf = new byte[1024];
			for (int i = 0; i < vector.size(); i++) {
				ContentHolder holder = (ContentHolder)vector.elementAt(i);
				InputStream is = null;
				String name = new String();
				String number = new String();
				String version = new String();
				String iteration = new String();
				if(holder instanceof WTDocument)
				{
					name = ((WTDocument)holder).getName();
					number = ((WTDocument)holder).getNumber();
					version = ((WTDocument)holder).getVersionIdentifier().getValue();
					iteration = ((WTDocument)holder).getIterationIdentifier().getValue();
				}
				if(holder instanceof EPMDocument)
				{
					name = ((EPMDocument)holder).getName();
					number = ((EPMDocument)holder).getNumber();
					version = ((EPMDocument)holder).getVersionIdentifier().getValue();
					iteration = ((EPMDocument)holder).getIterationIdentifier().getValue();
				}
					
				String pathname="";
				pathname = s + File.separator
								+ number + "_"
								+ name + "_"
								+ version + "_"
								+ iteration + File.separator;
				if(type.equalsIgnoreCase("ALL")||type.equalsIgnoreCase("Primary")){	
					primaryList = getPrimaryApplicationData(holder);
					for(int p=0;p<primaryList.size();p++)
					{
						ApplicationData ad = (ApplicationData)primaryList.get(p);
						try {
							is = ContentServerHelper.service.findContentStream(ad);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						
						if (is != null) {
							String fileName = ad.getFileName();
							if(fileName.equals("{$CAD_NAME}"))
	                        {
	                            EPMDocument epm = (EPMDocument)holder;
	                            fileName = epm.getCADName();
	                        }
							String entry = pathname + fileName;	
							log.debug("entry is:"+entry);
							// 向Zip中添加新文件
							zos.putNextEntry(new ZipEntry(entry));
							int len = 0;
							while ((len = is.read(buf)) >= 0){
								zos.write(buf, 0, len);
							}
							is.close();
						}
					}

				}
				if(type.equalsIgnoreCase("ALL")||type.equalsIgnoreCase("Print")){	
					printList = getPrintApplicationData(holder);
					for(int p1=0;p1<printList.size();p1++)
					{
						ApplicationData ad = (ApplicationData)printList.get(p1);
						try {
							is = ContentServerHelper.service.findContentStream(ad);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						
						if (is != null) {
							String fileName = ad.getFileName();
							String entry = pathname + fileName;	
							log.debug("entry is:"+entry);
							// 向Zip中添加新文件
							zos.putNextEntry(new ZipEntry(entry));
							int len = 0;
							while ((len = is.read(buf)) >= 0){
								zos.write(buf, 0, len);
							}
							is.close();
						}
					}
				}
				if(type.equalsIgnoreCase("ALL")||type.equalsIgnoreCase("WVS")){	
					wvsList = getWVSApplicationData(holder);
					for(int w=0;w<wvsList.size();w++)
					{
						ApplicationData ad = (ApplicationData)wvsList.get(w);
						try {
							is = ContentServerHelper.service.findContentStream(ad);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						
						if (is != null) {
							String fileName = ad.getFileName();
							String entry = pathname + "wvs" + File.separator + fileName;	
							log.debug("entry is:"+entry);
							// 向Zip中添加新文件
							zos.putNextEntry(new ZipEntry(entry));
							int len = 0;
							while ((len = is.read(buf)) >= 0){
								zos.write(buf, 0, len);						
							}
							is.close();
						}
					}
				}
				if(type.equalsIgnoreCase("ALL")||type.equalsIgnoreCase("Attachment")){	
					attachmentList = getAttachmentApplicationData(holder);
					for(int a=0;a<attachmentList.size();a++)
					{
						ApplicationData ad = (ApplicationData)attachmentList.get(a);
						try {
							is = ContentServerHelper.service.findContentStream(ad);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						
						if (is != null) {
							String fileName = ad.getFileName();
							String entry = pathname + fileName;	
							log.debug("entry is:"+entry);
							// 向Zip中添加新文件
							zos.putNextEntry(new ZipEntry(entry));
							int len = 0;
							while ((len = is.read(buf)) >= 0){
								zos.write(buf, 0, len);
							}
							is.close();
						}
					}
				}
			}
			zos.close();
		}catch(IOException ie){

		}
		return fileName1;	
   }
   
   //获得主要内容
   public ArrayList getPrimaryApplicationData(ContentHolder holder)
   	throws WTException {
   		ArrayList arraylist = new ArrayList();
   		ContentItem contentitem = null;
   		FormatContentHolder formatcontentholder = null;
        try
        {
            formatcontentholder = (FormatContentHolder)ContentHelper.service.getContents((FormatContentHolder)holder);
            contentitem = ContentHelper.getPrimary(formatcontentholder);
        }
        catch(java.beans.PropertyVetoException propertyvetoexception)
        {
            System.err.println(propertyvetoexception);
        }
        if(contentitem instanceof ApplicationData)
        {
            ApplicationData applicationdata = (ApplicationData)contentitem;
            String fileNameDisplayString = applicationdata.getFileName();
            arraylist.add(applicationdata);
        }
        return arraylist;
   	}
   	
   	//获得可视化
   	public ArrayList getWVSApplicationData(ContentHolder holder)
   		throws WTException {
   		ArrayList arraylist = new ArrayList();
   		ContentItem contentitem = null;
        Representation representation = RepresentationHelper.service.getDefaultRepresentation((Representable)holder);
        if (representation != null) {
        	try{
            	representation = (Representation) ContentHelper.service.getContents(representation);
        	}catch(PropertyVetoException pve){
        	}
            Vector vector1 = ContentHelper.getContentList(representation);
            
            for (int l = 0; l < vector1.size(); l++) {
                contentitem = (ContentItem) vector1.elementAt(l);
                if (contentitem instanceof ApplicationData) {
                	arraylist.add((ApplicationData)contentitem);
                }
            }
        }
        return arraylist;
   	}
   	
   	//获得附件打印文件
   	public ArrayList getPrintApplicationData(ContentHolder holder)
   		throws WTException {
   		ArrayList arraylist = new ArrayList();
        ContentHolder holder1 = null;
        try{
             holder1 = ContentHelper.service.getContents(holder);
    	}catch(PropertyVetoException pve){
    	}
		Vector v = ContentHelper.getApplicationData(holder1);
		for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
        	ApplicationData ad =(ApplicationData) e.nextElement();
        	String fileName = ad.getFileName();
        	if(fileName.indexOf("Print")>-1){
        		arraylist.add(ad);
        	}
		}
		return arraylist;  
   }
   
   //获得附件
   	public ArrayList getAttachmentApplicationData(ContentHolder holder)
   		throws WTException {
   		ArrayList arraylist = new ArrayList();
        ContentHolder holder1 = null;
        try{
             holder1 = ContentHelper.service.getContents(holder);
    	}catch(PropertyVetoException pve){
    	}
		Vector v = ContentHelper.getApplicationData(holder1);
		for (Enumeration e = v.elements() ; e.hasMoreElements() ;) {
        	ApplicationData ad =(ApplicationData) e.nextElement();
			arraylist.add(ad);
		}
		return arraylist;  
   }
   

   //##end user.operations
}
