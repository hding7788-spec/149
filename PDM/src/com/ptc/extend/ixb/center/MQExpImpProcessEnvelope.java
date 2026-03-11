package com.ptc.extend.ixb.center;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.ptc.extend.ixb.CmExpImpEnvelopeMemberLink;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.extend.ixb.CmExporter;
import com.ptc.extend.ixb.CmImporter;
import com.ptc.extend.ixb.ErrorImportObject;
import com.ptc.extend.util.ObjectProperty;

import ext.ases.envelope.ProcessEnvelope;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.facade.ixb.IxbElement;
import wt.fc.Persistable;
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

public class MQExpImpProcessEnvelope extends MQExpImpPersistable {

    public MQExpImpProcessEnvelope(CmExporter expHdl) throws WTException {
        super(expHdl);
    }

    public MQExpImpProcessEnvelope(CmImporter impHdl, String fname)
            throws WTException {
        super(impHdl, fname);
    }

    public String getRootTag() {
        return MQExpImpConstants.XML_MQECA;
    }

    public void exportObject(Object obj) throws WTException {
        if (!(obj instanceof ProcessEnvelope))
            throw new WTException("Object not ProcessEnvelope.");
        ProcessEnvelope pe = (ProcessEnvelope) obj;
		logger("==>Export ProcessEnvelope:"
				+ ObjectProperty.getObjectDisplay(pe));
		exportAttribute(pe);
		this.expHdl.addExportedObject(pe);
		logger("==>Export Linkage of ProcessEnvelope:"
				+ ObjectProperty.getObjectDisplay(pe));
		try {

			ArrayList list = CmExpImpSearchHelper
					.searchAllEnvelopeMemberLink(pe);
			if (list.size() > 0)
				new CmExpImpEnvelopeMemberLink(pe, this.expHdl)
						.exportObject(list);
		} catch (Exception e) {
			logger("==>Exception export Link for ob=<"
					+ ObjectProperty.getObjectDisplay(pe) + ">");
		}
    }

    private void exportAttribute(ProcessEnvelope pe) throws WTException {
		exportUfidAttribute(pe, this.root);
		exportLocalIdAttribute(pe, this.root);
		exportContainerPathAttribute(pe, this.root);
		exportProcessEnvelopeAttribute(pe, this.root);
		exportDomainFolderAttribute(pe, this.root);

		exportLifecycleAttribute(pe, this.root);
//		exportTeamAttribute(pe, this.root);
		exportTypeDefinitionAttribute(pe, this.root);
		exportContentItemAttribute(pe, this.root);
		reallyStore();
	}

    private void exportProcessEnvelopeAttribute(Object obj,
			IxbElement ixbelement) throws WTException {
		try {
			ProcessEnvelope pe = (ProcessEnvelope) obj;
			ixbelement.addValue("number", pe.getNumber());
			ixbelement.addValue("name", pe.getName());
			ixbelement
					.addValue("description", emptyIfNull(pe.getDescription()));
			ixbelement.addValue("creator", emptyIfNull(pe.getCreatorName()));
			ixbelement.addValue("designer", emptyIfNull(pe.getCreatorFullName()));
			ixbelement.addValue("designCompany", MQExpImpConstants.VALUE_DOMAINNAME);
			ixbelement.addValue("createtime",
					String.valueOf(pe.getCreateTimestamp().getTime()));
			ixbelement.addValue("modifytime",
					String.valueOf(pe.getModifyTimestamp().getTime()));

			ixbelement.addValue("originDomainName", MQExpImpConstants.VALUE_DOMAINNAME);
	        ixbelement.addValue("securityLevel", "10");
	        ixbelement.addValue("owner", pe.getOwnership().getOwner().getName());
		} catch (Exception exception) {
			logger("Exception in ExpImpForProcessEnvelopeAttribute, ob=<"
					+ ObjectProperty.getObjectDisplay(obj) + ">");
			processException(exception);
		}
	}

    public Object importObject() throws WTException {
        Object obj = importAttribute();
        if (obj != null){
        	if(obj instanceof ErrorImportObject){
        	}else{
                this.impHdl.pubImportedObject(obj, getRemoteId());
        	}
        }
        return obj;
    }

    public WTArrayList importObjects() throws WTException {
        WTArrayList list = new WTArrayList();
        list.add((Persistable) importObject());
        return list;
    }

    private Object importAttribute() throws WTException {
        try {
            if (!isTypeDefinitionImported()) {
                logger.log("==>WARNING:Import ProcessEnvelope number=<" + this.number
                        + ">  TypeDefinition not imported, SKIP!");
                return null;
            }
			String modelType = getElementValue(this.root, "modelType");
            String s = "WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.APPROVEFORM";
            if("DisOrder".equals(modelType)){
				s = "WCTYPE|ext.ases.envelope.ProcessEnvelope|casc.sast.149.RELEASEFORM";
			}
            ProcessEnvelope pe = (ProcessEnvelope) CmExpImpSearchHelper.getProcessEnvelopeByNumber(this.number,s);
            if (pe != null) {
                logger.log("==>Import ProcessEnvelope number=<" + this.number + ">  already imported, Go On Edit!");
                pe = (ProcessEnvelope)importPDFContentItem(pe, this.root);
                WTContainerRef wtcontainerref = pe.getContainerReference();
                //处理借用件问题，先把导入的包的容器先保存在导入处理类中
                if (wtcontainerref!=null) {
                	this.impHdl.setWTContainerRef(wtcontainerref);
    			}
                this.impHdl.putInExistedHashtable(getRemoteId(), pe);
                return pe;
            } else {
                logger.log("==>Create new Object: ProcessEnvelope number=<" + this.number + ">");
                pe = createNewObject();
            }
            if (pe != null) {
                this.impHdl.putInNewCreatedHashtable(getRemoteId(), pe);
                HashMap hmap = buildOrignalInfo();
                logger.log("==>Import ProcessEnvelope number=<" + this.number + "> OK!");
            }

            return pe;
        } catch (Exception exception) {
            logger.log("Exception in ProcessEnvelope, fname=<" + getPfilename() + ">");
            ErrorImportObject eo = new ErrorImportObject();
            eo.setNumber(this.number);
            eo.setXmlName(getPfilename());
            eo.setType("ProcessEnvelope");
			if(exception.getLocalizedMessage()!=null&&exception.getLocalizedMessage().contains("未找到上下文")){
				String productId = getElementValue(this.root, "productId");
				eo.setMessage(this.number+"149PDM系统找不到相应的型号:"+productId+",请联系系统管理员创建并映射对应型号");
			} else{
				eo.setMessage(exception.getLocalizedMessage());
			}
            exception.printStackTrace();
            return eo;
        }
    }

    private ProcessEnvelope importProcessEnvelopeAttribute(Object obj, IxbElement ixbelement) throws WTException {
        ProcessEnvelope pe = (ProcessEnvelope) obj;
        try {
            String s = getElementValue(ixbelement, "number");
            if (s != null)
                pe.setNumber(s);
            String s1 = getElementValue(ixbelement, "name");
            if (s1 != null)
                pe.setName(s1+"("+this.getSendFrom()+")");
            String s2 = getElementValue(ixbelement, "description");
            if (s2 != null) {
                pe.setDescription(s2);
            }
            return pe;
        } catch (Exception e) {
            logger.log("Exception in importProcessEnvelopeAttribute, fname=<" + getPfilename() + ">");
            processException(e);
        }
        return pe;
    }

	private ProcessEnvelope createNewObject() throws WTException {
		ProcessEnvelope pe = null;

		MethodContext methodcontext = MethodContext.getContext();

		long nowtime = Calendar.getInstance().getTimeInMillis();

		String createStampStr = getElementValue(this.root, "createStamp");
		Timestamp createStamp;
		if (createStampStr != null)
			createStamp = new Timestamp(Long.parseLong(createStampStr));
		else {
			createStamp = new Timestamp(nowtime);
		}
		String modifyStampStr = getElementValue(this.root, "modifyStamp");
		Timestamp modifyStamp;
		if (modifyStampStr != null)
			modifyStamp = new Timestamp(Long.parseLong(modifyStampStr));
		else
			modifyStamp = new Timestamp(nowtime);
		WTContainerRef wtcontainerref = getWTContainerRef(this.root);
		// 处理借用件问题，先把导入的包的容器先保存在导入处理类中
		if (wtcontainerref != null) {
			this.impHdl.setWTContainerRef(wtcontainerref);
		} else {
			return null;
		}

		Transaction tx = new Transaction();
		try {
			tx.start();
			pe = ProcessEnvelope.newProcessEnvelope();
			pe = (ProcessEnvelope) importLifecycleAttribute(pe, this.root);

			pe = importProcessEnvelopeAttribute(pe, this.root);

			pe = (ProcessEnvelope) importDomainFolderAttribute(pe, this.root);

			pe = (ProcessEnvelope) importTypeDefinitionAttribute(pe, this.root, this.root);
			pe.setContainerReference(wtcontainerref);
			methodcontext.put("ixb_store_object_context/key", pe);
			WTArrayList wtarraylist = new WTArrayList(1);
			wtarraylist.add(pe);
			Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
			Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
			WTHashSet wthashset = new WTHashSet();
			wthashset.add(pe);
			Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
			pe = (ProcessEnvelope) PersistenceServerHelper.manager.store(pe, createStamp, modifyStamp);
			methodcontext.remove("ixb_store_object_context/key");
			tx.commit();
			tx = null;
			pe = (ProcessEnvelope) importIBAAttribute(pe, this.root);
			pe = (ProcessEnvelope) importContentItemAttribute(pe, this.root);
		} catch (Exception e) {
			if ((e instanceof WTException))
				throw ((WTException) e);
			throw new WTException(e);
		} finally {
			if (tx != null)
				tx.rollback();
			methodcontext.remove("ixb_store_object_context/key");
		}
		return pe;
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