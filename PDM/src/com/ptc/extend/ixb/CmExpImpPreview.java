package com.ptc.extend.ixb;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.ptc.extend.util.ObjectProperty;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.epm.EPMDocument;
import wt.facade.ixb.IxbElement;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTHashSet;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.StandardVersionControlService;
import ext.casc.preview.Preview;

public class CmExpImpPreview extends CmExpImpPersistable {

    public CmExpImpPreview(CmExporter expHdl) throws WTException {
        super(expHdl);
    }

    public CmExpImpPreview(CmImporter impHdl, String fname)
            throws WTException {
        super(impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_PREVIEW;
    }

    @Override
    public void exportObject(Object obj) throws WTException {
    	if (!(obj instanceof Preview))
            throw new WTException("Object not ChangePackaged.");
    	Preview cp = (Preview) obj;
        logger.log("==>Export Preview:" +cp.getNumber());
        exportAttribute(cp, this.root);
        exportContentItemAttribute(cp, this.root);
        reallyStore();

    }

    private void exportAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
        	Preview cp = (Preview) obj;
            ixbelement.addValue("number", cp.getNumber());
            ixbelement.addValue("name", cp.getName());
        } catch (Exception exception) {
            logger.log("Exception in PreviewAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj)
                    + ">");
            processException(exception);
        }
    }



    @Override
    public Object importObject() throws WTException {
        // TODO Auto-generated method stub
        Object obj = importAttribute();
        if (obj != null)
            this.impHdl.pubImportedObject(obj, getRemoteId());
        return obj;
    }

    private Object importAttribute() throws WTException {
        try {

        	Preview preview = (Preview) CmExpImpSearchHelper
                    .getPreviewByNumber(this.number);
            if (preview != null) {
                logger.log("==>Import Preview number=<" + this.number + ">  already imported, Go On Edit!");
                this.impHdl.putInExistedHashtable(getRemoteId(), preview);
                preview = (Preview) importPDFContentItem(preview, this.root);
                WTContainerRef wtcontainerref = preview.getContainerReference();
                //处理借用件问题，先把导入的包的容器先保存在导入处理类中
                if (wtcontainerref!=null) {
                	this.impHdl.setWTContainerRef(wtcontainerref);
    			}
                return preview;
            } else {
                logger.log("==>Create new Object: preview number=<" + this.number + ">");
                preview = createNewObject();
            }
            if (preview != null) {
                this.impHdl.putInNewCreatedHashtable(getRemoteId(), preview);
                logger.log("==>Import preview number=<" + this.number + "> OK!");
            }

            return preview;
        } catch (Exception exception) {
            logger.log("Exception in preview, fname=<" + getPfilename() + ">");
            processException(exception);
        }
        return null;
    }

    private Preview createNewObject() throws WTException {
    	Preview preview = null;
        Transaction tx = new Transaction();
        MethodContext methodcontext = MethodContext.getContext();
        try {
            long nowtime = Calendar.getInstance().getTimeInMillis();

            String createStampStr = getElementValue(this.root, "createtime");
            Timestamp createStamp;
            if (createStampStr != null)
                createStamp = new Timestamp(Long.parseLong(createStampStr));
            else {
                createStamp = new Timestamp(nowtime);
            }
            String modifyStampStr = getElementValue(this.root, "modifytime");
            Timestamp modifyStamp;
            if (modifyStampStr != null)
                modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
            else modifyStamp = new Timestamp(nowtime);
            WTContainerRef wtcontainerref = getWTContainerRef(this.root);
            //处理借用件问题，先把导入的包的容器先保存在导入处理类中
            if (wtcontainerref!=null) {
            	this.impHdl.setWTContainerRef(wtcontainerref);
			}
            tx.start();
            preview = Preview.newPreview();
            preview = (Preview) importLifecycleAttribute(preview, this.root);

            preview = importPreviewAttribute(preview, this.root);

            preview = (Preview) importDomainFolderAttribute(preview, this.root);

            preview = (Preview) importTypeDefinitionAttribute(preview, this.root, this.root);
            preview.setContainerReference(wtcontainerref);
            methodcontext.put("ixb_store_object_context/key", preview);
            WTArrayList wtarraylist = new WTArrayList(1);
            wtarraylist.add(preview);
            Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
            Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
            WTHashSet wthashset = new WTHashSet();
            wthashset.add(preview);
            Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
            preview = (Preview) PersistenceServerHelper.manager.store(preview, createStamp,
                    modifyStamp);
            methodcontext.remove("ixb_store_object_context/key");
            tx.commit();
            tx = null;
            preview = (Preview) importIBAAttribute(preview, this.root);
            preview = (Preview) importContentItemAttribute(preview, this.root);
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
            methodcontext.remove("ixb_store_object_context/key");
        }
        return preview;
    }

    private Preview importPreviewAttribute(Object obj, IxbElement ixbelement) throws WTException {
    	Preview preview = (Preview) obj;
        try {
            String s = getElementValue(ixbelement, "number");
            if (s != null )
            	preview.setNumber(s);
            s = getElementValue(ixbelement, "name");
            if (s != null)
            	preview.setName(s);
            s = getElementValue(ixbelement, "description");
            if (s != null) {
            	preview.setDescription(s);
            }
            s = getElementValue(ixbelement, "designer");
            if (s != null) {
            	preview.setDesigner(s);
            }
            s = getElementValue(ixbelement, "designCompany");
            if (s != null) {
            	preview.setDesignCompany(s);
            	preview.setName(preview.getName()+"("+sendFrom+")");
            }
            return preview;
        } catch (Exception e) {
            logger.log("Exception in importcrAttribute, fname=<" + getPfilename() + ">");
            processException(e);
        }
        return preview;
    }

    @Override
    public WTArrayList importObjects() throws WTException {
        // TODO Auto-generated method stub
        return null;
    }

    private ContentHolder importPDFContentItem(Object obj, IxbElement ixbelement) throws WTException {
      	 ContentHolder contentholder = (ContentHolder) obj;
           Transaction tx = null;
           try {
               Enumeration enumeration = ixbelement.getElements("contentItem");
               if ((enumeration == null) || (!enumeration.hasMoreElements()))
                   return contentholder;
               tx = new Transaction();
               tx.start();
               contentholder = (ContentHolder) PersistenceHelper.manager.lockAndRefresh(contentholder);
               boolean enforce = SessionServerHelper.manager
                       .setAccessEnforced(false);
               ContentHolder holder = ContentHelper.service.getContents(contentholder);
               Vector vector = ContentHelper.getContentList(holder);
               List<String> existFiles = new ArrayList<String>();
               if (vector != null) {
                   for (int i = 0; i < vector.size(); i++) {
                       ContentItem contentitem1 = (ContentItem) vector
                               .elementAt(i);
                       if (contentitem1 instanceof ApplicationData) {
                           ApplicationData data = (ApplicationData) contentitem1;
                           if (data.getFileName().startsWith("Print_")) {
                          	 existFiles.add(data.getFileName());
                           }
                       }

                   }
               }
               while (enumeration.hasMoreElements()) {
                   IxbElement ixbelement2 = (IxbElement) enumeration.nextElement();
                   String s1 = getElementValue(ixbelement2, "contentType");
                   String fileName = getElementValue(ixbelement2, "fileName");
                   if (s1.equals("ApplicationData")&&fileName.startsWith("Print_")&&!existFiles.contains(fileName))
                       importPDFAppDataContent(contentholder, ixbelement2);
                   else logger.log("importContentHolder: unknown type of content item:<" + s1 + ">");
               }
               if ((contentholder instanceof FormatContentHolder))
                   contentholder = ContentServerHelper.service.updateHolderFormat((FormatContentHolder) contentholder);
               contentholder = (ContentHolder) PersistenceHelper.manager.refresh(contentholder);
               tx.commit();
               tx = null;
           } catch (Exception e) {
               logger.log("Exception in exportContentItemAttribute, ob=<" + obj + ">");
               processException(e);
           } finally {
               if (tx != null)
                   tx.rollback();
           }
           return contentholder;
   	}

   	 private void importPDFAppDataContent(ContentHolder contentholder, IxbElement ixbelement) throws WTException {
   	        ApplicationData applicationdata = ApplicationData.newApplicationData(contentholder);
   	        populateContentItemData(applicationdata, ixbelement);
   	        try {
   	            String s = getElementValue(ixbelement, "fileName");
   	            String s1 = getElementValue(ixbelement, "toolName");
   	            String s2 = getElementValue(ixbelement, "toolVersion");
   	            String s3 = getElementValue(ixbelement, "fileVersion");
   	            if (s != null)
   	                applicationdata.setFileName(s);
   	            if (s3 != null)
   	                applicationdata.setFileVersion(s3);
   	            if (s1 != null)
   	                applicationdata.setToolName(s1);
   	            if (s2 != null)
   	                applicationdata.setToolVersion(s2);
   	            String s4 = getElementValue(ixbelement, "contentId");
   	            InputStream inputstream = this.impHdl.getContentAsInputStream(s4);
   	            if (inputstream != null)
   	                applicationdata = ContentServerHelper.service
   	                        .updateContent(contentholder, applicationdata, inputstream);
   	        } catch (WTPropertyVetoException wtpropertyvetoexception) {
   	            throw new WTException(wtpropertyvetoexception);
   	        } catch (PropertyVetoException propertyvetoexception) {
   	            throw new WTException(propertyvetoexception);
   	        } catch (Exception exception) {
   	            throw new WTException(exception);
   	        }
   	    }

}