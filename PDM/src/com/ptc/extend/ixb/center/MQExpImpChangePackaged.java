package com.ptc.extend.ixb.center;

import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import wt.change2.WTChangeOrder2;
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
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.StandardVersionControlService;

import com.ptc.extend.ixb.CmExpImpChangePackagedAffectLink;
import com.ptc.extend.ixb.CmExpImpChangePackagedResultLink;
import com.ptc.extend.ixb.CmExpImpConstraints;
import com.ptc.extend.ixb.CmExpImpSearchHelper;
import com.ptc.extend.ixb.CmExporter;
import com.ptc.extend.ixb.CmImporter;
import com.ptc.extend.ixb.ErrorImportObject;
import com.ptc.extend.util.ObjectProperty;

import ext.ases.changepackaged.ChangePackaged;
import ext.casc.change.ChangeHelper;
import ext.casc.constants.Constants;
import ext.casc.util.IBAHelper;

public class MQExpImpChangePackaged extends MQExpImpPersistable {

    public MQExpImpChangePackaged(CmExporter expHdl) throws WTException {
        super(expHdl);
    }

    public MQExpImpChangePackaged(CmImporter impHdl, String fname)
            throws WTException {
        super(impHdl, fname);
    }

    public String getRootTag() {
        return MQExpImpConstants.XML_MQECO;
    }

    @Override
    public void exportObject(Object obj) throws WTException {
    	if (!(obj instanceof WTChangeOrder2))
			throw new WTException("Object not WTChangeOrder2.");
		WTChangeOrder2 order = (WTChangeOrder2) obj;
		logger("==>Export WTChangeOrder2:"
				+ ObjectProperty.getObjectDisplay(order));
		exportAttribute(order);
		this.expHdl.addExportedObject(order);
		logger("==>Export Linkage of WTChangeOrder2:"
				+ ObjectProperty.getObjectDisplay(order));

		ArrayList list = ChangeHelper.getChangeAffectItem(order);
		if (list.size() > 0) {
			list.add(order);
			new CmExpImpChangePackagedAffectLink(order, this.expHdl,"MQ")
					.exportObject(list);

		}
		list = ChangeHelper.getChangeResultItem(order);
		if (list.size() > 0) {
			list.add(order);
			new CmExpImpChangePackagedResultLink(order, this.expHdl,"MQ")
					.exportObject(list);
		}
    }

    private void exportAttribute(WTChangeOrder2 order) throws WTException {
		exportUfidAttribute(order, this.root);
		exportLocalIdAttribute(order, this.root);
		exportContainerPathAttribute(order, this.root);
		exportChangeOrder2Attribute(order, this.root);
		exportDomainFolderAttribute(order, this.root);
		exportLifecycleAttribute(order, this.root);
		exportTeamAttribute(order, this.root);
		exportTypeDefinitionAttribute(order, this.root);
		exportContentItemAttribute(order, this.root);
		reallyStore();
	}

	private void exportChangeOrder2Attribute(WTChangeOrder2 order,
			IxbElement ixbelement) throws WTException {
		try {
			ixbelement.addValue("number", order.getNumber());
			ixbelement.addValue("name", order.getName());
			ixbelement.addValue("description",
					emptyIfNull(order.getDescription()));
			String complex = order.getChangeNoticeComplexity().getDisplay(Locale.CHINA);
			ixbelement.addValue("complex", emptyIfNull(complex));

			ixbelement.addValue("creator", emptyIfNull(order.getCreatorName()));
			ixbelement.addValue("createtime",
					String.valueOf(order.getCreateTimestamp().getTime()));
			ixbelement.addValue("modifytime",
					String.valueOf(order.getModifyTimestamp().getTime()));
			String profession = IBAHelper.getIBAStringValue(order, "PROFESSION");
			ixbelement.addValue("profession", emptyIfNull(profession));

			String dept = IBAHelper.getIBAStringValue(order, "DEPT");
			ixbelement.addValue("department", emptyIfNull(dept));

			String phasecode = IBAHelper.getIBAStringValue(order, "PHASE_CODE");
			ixbelement.addValue("phaseInfo", emptyIfNull(phasecode));

			String changetype = IBAHelper.getIBAStringValue(order, "CHANGETYPE");
			ixbelement.addValue("changetype", emptyIfNull(changetype));

			String affectedpage = IBAHelper.getIBAStringValue(order, "AFFECTEDPAGE");
			ixbelement.addValue("affectedpage", emptyIfNull(affectedpage));
			ixbelement.addValue("designer", emptyIfNull(order.getCreatorFullName()));
			ixbelement.addValue("originDomainName", MQExpImpConstants.VALUE_DOMAINNAME);
	        ixbelement.addValue("securityLevel", "10");
	        ixbelement.addValue("owner", order.getOwnership().getOwner().getName());
		} catch (Exception exception) {
			logger("Exception in exportChangeOrder2Attribute, ob=<"
					+ ObjectProperty.getObjectDisplay(order) + ">");
			processException(exception);
		}
	}

    @Override
    public Object importObject() throws WTException {
        // TODO Auto-generated method stub
        Object obj = importAttribute();
        if (obj != null){
        	if(obj instanceof ErrorImportObject){
        	}else{
                this.impHdl.pubImportedObject(obj, getRemoteId());
        	}
        }
        return obj;
    }

    private Object importAttribute() throws WTException {
        try {

            ChangePackaged changePackaged = (ChangePackaged) CmExpImpSearchHelper.getChangePackagedByNumber(this.number);
			/*if (changePackaged == null) {
				if(this.number.contains("_KYGYHQ")){
					String tmpNum = this.number.replaceAll("_KYGYHQ","_KYJSHQ");
					changePackaged = (ChangePackaged) CmExpImpSearchHelper.getChangePackagedByNumber(tmpNum);
				}
			}*/

			//add by hding　　跨域工艺会签更改单附件更新时，同时更新跨域技术会签更改单父件，相反同理
			if(this.number.contains("_KYGYHQ")){
				String tmpNum = this.number.replaceAll("_KYGYHQ","_KYJSHQ");
				ChangePackaged changePackaged_KYJSHQ = (ChangePackaged) CmExpImpSearchHelper.getChangePackagedByNumber(tmpNum);
				if(changePackaged_KYJSHQ!=null){
					changePackaged_KYJSHQ = (ChangePackaged) importContentItemAttribute(changePackaged_KYJSHQ, this.root);
				}
			}else if(this.number.contains("_KYJSHQ")){
				String tmpNum = this.number.replaceAll("_KYJSHQ","_KYGYHQ");
				ChangePackaged changePackaged_KYGYHQ = (ChangePackaged) CmExpImpSearchHelper.getChangePackagedByNumber(tmpNum);
				if(changePackaged_KYGYHQ!=null){
					changePackaged_KYGYHQ = (ChangePackaged) importContentItemAttribute(changePackaged_KYGYHQ, this.root);
				}
			}
            if (changePackaged != null) {
                logger.log("==>Import ChangePackaged number=<" + this.number + ">  already imported, Go On Edit!");
                this.impHdl.putInExistedHashtable(getRemoteId(), changePackaged);
                changePackaged = (ChangePackaged) importContentItemAttribute(changePackaged, this.root);
                WTContainerRef wtcontainerref = changePackaged.getContainerReference();
                //处理借用件问题，先把导入的包的容器先保存在导入处理类中
                if (wtcontainerref!=null) {
                	this.impHdl.setWTContainerRef(wtcontainerref);
    			}
                return changePackaged;
            } else {
                logger.log("==>Create new Object: ChangePackaged number=<" + this.number + ">");
                changePackaged = createNewObject();
            }
            if (changePackaged != null) {
                this.impHdl.putInNewCreatedHashtable(getRemoteId(), changePackaged);
                logger.log("==>Import ChangePackaged number=<" + this.number + "> OK!");
            }

            return changePackaged;
        } catch (Exception exception) {
        	logger.log("Exception in ChangePackaged, fname=<" + getPfilename() + ">");
            ErrorImportObject eo = new ErrorImportObject();
            eo.setNumber(this.number);
            eo.setXmlName(getPfilename());
            eo.setType("ChangePackaged");
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

    private ChangePackaged createNewObject() throws WTException {
        ChangePackaged changePackaged = null;
        Transaction tx = new Transaction();
        MethodContext methodcontext = MethodContext.getContext();
        try {
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
            else modifyStamp = new Timestamp(nowtime);
            WTContainerRef wtcontainerref = getWTContainerRef(this.root);
            //处理借用件问题，先把导入的包的容器先保存在导入处理类中
            if (wtcontainerref!=null) {
            	this.impHdl.setWTContainerRef(wtcontainerref);
			}else{
				return null;
			}
            tx.start();
            changePackaged = ChangePackaged.newChangePackaged();
            changePackaged = (ChangePackaged) importLifecycleAttribute(changePackaged, this.root);

            changePackaged = importChangePackagedAttribute(changePackaged, this.root);

            changePackaged = (ChangePackaged) importDomainFolderAttribute(changePackaged, this.root);

            changePackaged = (ChangePackaged) importTypeDefinitionAttribute(changePackaged, this.root, this.root);
            changePackaged.setContainerReference(wtcontainerref);
            methodcontext.put("ixb_store_object_context/key", changePackaged);
            WTArrayList wtarraylist = new WTArrayList(1);
            wtarraylist.add(changePackaged);
            Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY, true);
            Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
            WTHashSet wthashset = new WTHashSet();
            wthashset.add(changePackaged);
            Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
            changePackaged = (ChangePackaged) PersistenceServerHelper.manager.store(changePackaged, createStamp,
                    modifyStamp);
            methodcontext.remove("ixb_store_object_context/key");
            tx.commit();
            tx = null;
            changePackaged = (ChangePackaged) importIBAAttribute(changePackaged, this.root);
            changePackaged = (ChangePackaged) importContentItemAttribute(changePackaged, this.root);
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
            methodcontext.remove("ixb_store_object_context/key");
        }
        return changePackaged;
    }

    private ChangePackaged importChangePackagedAttribute(Object obj, IxbElement ixbelement) throws WTException {
        ChangePackaged changePackaged = (ChangePackaged) obj;
        try {
            String s = getElementValue(ixbelement, "number");
            if (s != null)
                changePackaged.setNumber(s);
            s = getElementValue(ixbelement, "name");
            if (s != null){
            	String originDomainName = getElementValue("originDomainName");
            	String name = s+"("+originDomainName+")";
                changePackaged.setName(name);
            }
            s = getElementValue(ixbelement, "description");
            if (s != null) {
                changePackaged.setDescription(s);
            }

            s = getElementValue(ixbelement, "complex");
            if (s != null) {
                changePackaged.setComplex(s);
            }
            s = getElementValue(ixbelement, "profession");
            if (s != null) {
                changePackaged.setProfession(s);
            }
            s = getElementValue(ixbelement, "changetype");
            if (s != null) {
                changePackaged.setChangetype(s);
            }
            s = getElementValue(ixbelement, "phasecode");
            if (s != null) {
                changePackaged.setPhasecode(s);
            }
            s = getElementValue(ixbelement, "affectedpage");
            if (s != null) {
                changePackaged.setAffectdpage(s);
            }
            s = getElementValue(ixbelement, "department");
            if (s != null) {
                changePackaged.setDepartment(s);
            }

            s = getElementValue(ixbelement, "avidmtype");
            if (s != null) {
                changePackaged.setAvidmtype(s);
            }

            s = getElementValue(ixbelement, "requestpriority");
            if (s != null) {
                changePackaged.setRequestpriority(s);
            }

            s = getElementValue(ixbelement, "changereason");
            if (s != null) {
                changePackaged.setChangereason(s);
            }
            s = getElementValue(ixbelement, "template");
            if (s != null) {
                changePackaged.setTemplate(s);
            }
            s = getElementValue(ixbelement, "changeleixing");
            if (s != null) {
                changePackaged.setChangeleixing(s);
            }
            s = getElementValue(ixbelement, "cost");
            if (s != null) {
                changePackaged.setCost(s);
            }
            s = getElementValue(ixbelement, "requesttime");
            if (s != null) {
                changePackaged.setRequesttime(s);
            }
            s = getElementValue(ixbelement, "pindex");
            if (s != null) {
                changePackaged.setPindex(s);
            }
            s = getElementValue(ixbelement, "filenumber");
            if (s != null) {
                changePackaged.setFilenumber(s);
            }
            s = getElementValue(ixbelement, "secret");
            if (s != null) {
                changePackaged.setSecret(s);
            }
            s = getElementValue(ixbelement, "responsor");
            if (s != null) {
                changePackaged.setResponsor(s);
            }
            s = getElementValue(ixbelement, "guancanghao");
            if (s != null) {
                changePackaged.setGuancanghao(s);
            }
            s = getElementValue(ixbelement, "edittime");
            if (s != null) {
                changePackaged.setEdittime(s);
            }
            s = getElementValue(ixbelement, "remark");
            if (s != null) {
                changePackaged.setRemark(s);
            }

            s = getElementValue(ixbelement, "startphasename");
            if (s != null) {
                changePackaged.setStartphasename(s);
            }
            s = getElementValue(ixbelement, "targetphasename");
            if (s != null) {
                changePackaged.setTargetphasename(s);
            }

            IxbElement implement = ixbelement.getElement("ImplementAdvise");
            if (implement !=null) {
            	 s = getElementValue(implement, "implement");
                 if (s != null) {
                     changePackaged.setImplement(s);
                 }
			}
            return changePackaged;
        } catch (Exception e) {
            logger.log("Exception in importChangePackagedAttribute, fname=<" + getPfilename() + ">");
            processException(e);
        }
        return changePackaged;
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