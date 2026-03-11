package com.ptc.extend.ixb;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import ext.ases.changepackaged.ChangePackaged;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
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

import com.ptc.extend.util.ObjectProperty;

import ext.ases.changerequest.ChangeRequest;

public class CmExpImpChangeRequest extends CmExpImpPersistable {

    public CmExpImpChangeRequest(CmExporter expHdl) throws WTException {
        super(expHdl);
    }

    public CmExpImpChangeRequest(CmImporter impHdl, String fname)
            throws WTException {
        super(impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_WTCHANGEREQUEST;
    }

    @Override
    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ChangeRequest))
            throw new WTException("Object not ChangeRequest.");
        ChangeRequest cr = (ChangeRequest) obj;
        logger.log("==>Export ChangeRequest:" + ObjectProperty.getObjectDisplay(cr));
        exportChangeRequestAttribute(cr, this.root);
        exportContentItemAttribute(cr, this.root);
        reallyStore();

    }

    private void exportChangeRequestAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            ChangeRequest cr = (ChangeRequest) obj;
            ixbelement.addValue("number", cr.getNumber());
            ixbelement.addValue("name", cr.getName());
        } catch (Exception exception) {
            logger.log("Exception in exportChangeRequestAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj)
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

            ChangeRequest cr =  CmExpImpSearchHelper
                    .getChangeRequestByNumber(this.number);
            if(cr == null) {
                String tmpNumber = this.number;
                if(tmpNumber.contains("_KYGYHQ") || tmpNumber.contains("_KYJSHQ")) {
                    tmpNumber = this.number.replaceAll("_KYGYHQ", "").replaceAll("_KYJSHQ", "");
                    cr = CmExpImpSearchHelper.getChangeRequestByNumber(tmpNumber);
                }
                if(cr == null) {
                    cr = CmExpImpSearchHelper.getChangeRequestByNumber(tmpNumber + "_KYGYHQ");
                }
                if(cr == null) {
                    cr = CmExpImpSearchHelper.getChangeRequestByNumber(tmpNumber + "_KYJSHQ");
                }
            }
            if (cr != null) {
                logger.log("==>Import ChangeRequest number=<" + this.number + ">  already imported, Go On Edit!");
                this.impHdl.putInExistedHashtable(getRemoteId(), cr);
                cr = (ChangeRequest) importContentItemAttribute(cr, this.root);
                cr = (ChangeRequest) importLifecycleAttribute(cr, this.root);

                WTContainerRef wtcontainerref = cr.getContainerReference();
                //处理借用件问题，先把导入的包的容器先保存在导入处理类中
                if (wtcontainerref!=null) {
                	this.impHdl.setWTContainerRef(wtcontainerref);
    			}
                return cr;
            } else {
                logger.log("==>Create new Object: ChangeRequest number=<" + this.number + ">");
                cr = createNewObject();
            }
            if (cr != null) {
                this.impHdl.putInNewCreatedHashtable(getRemoteId(), cr);
                logger.log("==>Import ChangeRequest number=<" + this.number + "> OK!");
            }

            return cr;
        } catch (Exception exception) {
            logger.log("Exception in ChangeRequest, fname=<" + getPfilename() + ">");
            processException(exception);
        }
        return null;
    }

    private ChangeRequest createNewObject() throws WTException {
        ChangeRequest cr = null;
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
            if (wtcontainerref!=null) {
                this.impHdl.setWTContainerRef(wtcontainerref);
            }else{
                String objectContainerPath = getElementValue(this.root, "objectContainerPath");
                if (objectContainerPath!=null && objectContainerPath.indexOf("PDMLinkProduct") > 0) {
                    int index = objectContainerPath.lastIndexOf("=");
                    String productName = objectContainerPath.substring(index + 1, objectContainerPath.length());
                    throw new WTException("149PDM系统找不到相应的型号:"+productName+",请联系系统管理员创建并映射对应型号！");
                }else{
                    throw new WTException("上面级打包信息错误，无对应的型号信息！");
                }
            }

            tx.start();
            cr = ChangeRequest.newChangeRequest();
            cr = (ChangeRequest) importLifecycleAttribute(cr, this.root);

            cr = importChangeRequestAttribute(cr, this.root);

            cr = (ChangeRequest) importDomainFolderAttribute(cr, this.root);

            cr = (ChangeRequest) importTypeDefinitionAttribute(cr, this.root, this.root);
            cr.setContainerReference(wtcontainerref);
            methodcontext.put("ixb_store_object_context/key", cr);
            WTArrayList wtarraylist = new WTArrayList(1);
            wtarraylist.add(cr);
            Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
            Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
            WTHashSet wthashset = new WTHashSet();
            wthashset.add(cr);
            Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
            cr = (ChangeRequest) PersistenceServerHelper.manager.store(cr, createStamp,
                    modifyStamp);
            methodcontext.remove("ixb_store_object_context/key");
            tx.commit();
            tx = null;
            cr = (ChangeRequest) importIBAAttribute(cr, this.root);
            cr = (ChangeRequest) importContentItemAttribute(cr, this.root);
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
            methodcontext.remove("ixb_store_object_context/key");
        }
        return cr;
    }

    private ChangeRequest importChangeRequestAttribute(Object obj, IxbElement ixbelement) throws WTException {
        ChangeRequest cr = (ChangeRequest) obj;
        try {
            String s = getElementValue(ixbelement, "number");
            if (s != null )
                cr.setNumber(s);
            s = getElementValue(ixbelement, "name");
            if (s != null)
                cr.setName(s);
            s = getElementValue(ixbelement, "description");
            if (s != null) {
                cr.setDescription(s);
            }

            s = getElementValue(ixbelement, "avidmtype");
            if (s != null) {
                cr.setAvidmtype(s);
            }


            s = getElementValue(ixbelement, "requesttype");
            if (s != null) {
                cr.setRequesttype(s);
            }
            s = getElementValue(ixbelement, "requestproprity");
            if (s != null) {
                cr.setRequestproprity(s);
            }
            s = getElementValue(ixbelement, "template");
            if (s != null) {
                cr.setTemplate(s);
            }
            s = getElementValue(ixbelement, "solution");
            if (s != null) {
                cr.setSolution(s);
            }
            s = getElementValue(ixbelement, "remark");
            if (s != null) {
                cr.setRemark(s);
            }
            s = getElementValue(ixbelement, "cost");
            if (s != null) {
                cr.setCost(s);
            }
            IxbElement implement = ixbelement.getElement("ImplementAdvise");
            if (implement !=null) {
            	 s = getElementValue(implement, "implement");
                 if (s != null) {
                     cr.setImplement(s);
                 }
			}
            return cr;
        } catch (Exception e) {
            logger.log("Exception in importcrAttribute, fname=<" + getPfilename() + ">");
            processException(e);
        }
        return cr;
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