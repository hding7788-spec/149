package ext.ases.techMaterial.ixb;

import com.ptc.extend.ixb.*;
import com.ptc.extend.ixb.center.MQExpImpConstants;
import com.ptc.extend.util.ObjectProperty;
import ext.casc.workflow.CmWorkflowHelper;
import wt.configuration.TraceCode;
import wt.facade.ixb.IxbElement;
import wt.fc.*;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTHashSet;
import wt.folder.FolderHelper;
import wt.generic.GenericType;
import wt.iba.value.service.MultiObjIBAValueDBService;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainerRef;
import wt.method.MethodContext;
import wt.part.*;
import wt.pom.Transaction;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.*;
import wt.vc.views.*;
import wt.wvs.VisualizationHelperFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CmExpEDAInfo {

}/*{
	public static  Map<String,String> partTypeMaps = new HashMap<String,String>();

	static{
		partTypeMaps.put("WCTYPE|wt.part.WTPart|casc.sast.BZJPart", "标准件");
		partTypeMaps.put("WCTYPE|wt.part.WTPart|com.ptc.ElectricalPart", "元器件");
	}
    public CmExpEDAInfo(CmExporter expHdl)
            throws WTException {
        super(expHdl);
    }

    public CmExpEDAInfo(CmImporter impHdl, String fname) throws WTException {
        super(impHdl, fname);
    }

    public String getRootTag() {
        return CmExpImpConstraints.XML_WTPART;
    }

    public void exportObject(Object obj) throws WTException {

        exportAttribute(obj);



        if (!(obj instanceof WTPart))
            throw new WTException("Object not WTPart.");
        WTPart part = (WTPart) obj;
        logger.log("==>Export WTPart:" + ObjectProperty.getObjectDisplay(part));
        this.expHdl.addExportedObject(part);
        logger.log("==>Export Linkage of WTPart:" + ObjectProperty.getObjectDisplay(part));

    }

    private void exportAttribute(Obj part) throws WTException {
        exportUfidAttribute(part, this.root);
        exportLocalIdAttribute(part, this.root);
        exportContainerPathAttribute(part, this.root);
        exportWTPartMasterAttribute(part, this.root);
        exportWTPartAttribute(part, this.root);
        exportDomainFolderAttribute(part, this.root);
        exportViewAttribute(part, this.root);
        exportVersionAttribute(part, this.root);
        exportLifecycleAttribute(part, this.root);
        exportTeamAttribute(part, this.root);
        exportTypeDefinitionAttribute(part, this.root);
        exportContentItemAttribute(part, this.root);
        exportIBAAttribute(part, this.root);

        exportRepresentationAttribute(part, this.root);
        reallyStore();
    }

    private void exportWTPartMasterAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            WTPart wtpart = (WTPart) obj;
            ixbelement.addValue("number", emptyIfNull(wtpart.getNumber()));
            WTPartMaster wtpartmaster = (WTPartMaster) wtpart.getMaster();
            ixbelement.addValue("name", emptyIfNull(wtpart.getName()));
            QuantityUnit quantityunit = wtpart.getDefaultUnit();
            if (quantityunit != null)
                ixbelement.addValue("defaultUnit", emptyIfNull(quantityunit.toString()));
            ixbelement.addValue("endItem", wtpart.isEndItem());
            ixbelement.addValue("defaultTraceCode", emptyIfNull(wtpart.getDefaultTraceCode().toString()));
            ixbelement.addValue("genericType", emptyIfNull(wtpartmaster.getGenericType().toString()));
        } catch (Exception exception) {
            logger.log("Exception in exportWTPartMasterAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    public void exportWTPartAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            WTPart wtpart = (WTPart) obj;
            ixbelement.addValue("partType", emptyIfNull(wtpart.getPartType().toString()));
            ixbelement.addValue("partSource", emptyIfNull(wtpart.getSource().toString()));
            String s = wtpart.getJobAuthorizationNumber();
            if (s != null)
                ixbelement.addValue("jobAuthorizationNumber", emptyIfNull(s));
            String s1 = wtpart.getContractNumber();
            if (s1 != null)
                ixbelement.addValue("contractNumber", emptyIfNull(s1));
            String s2 = wtpart.getPhase();
            if (s2 != null)
                ixbelement.addValue("phase", emptyIfNull(s2));
            Integer integer = wtpart.getMinimumRequired();
            if (integer != null)
                ixbelement.addValue("minRequired", emptyIfNull(integer.toString()));
            Integer integer1 = wtpart.getMaximumAllowed();
            if (integer1 != null)
                ixbelement.addValue("maxAllowed", emptyIfNull(integer1.toString()));
            ixbelement.addValue("creator", emptyIfNull(wtpart.getCreatorName()));
            ixbelement.addValue("createtime", String.valueOf(wtpart.getCreateTimestamp().getTime()));
            ixbelement.addValue("modifier", emptyIfNull(wtpart.getModifierName()));
            ixbelement.addValue("modifytime", String.valueOf(wtpart.getModifyTimestamp().getTime()));
            //modify by wyq
            ixbelement.addValue("originDomainName", MQExpImpConstants.VALUE_DOMAINNAME);
        } catch (Exception exception) {
            logger.log("Exception in exportWTPartAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    private String getViewName(Object obj) throws WTException {
        if (!(obj instanceof ViewManageable))
            throw new WTException("Not ViewManageable Object!");
        ViewManageable viewmanageable = (ViewManageable) obj;
        String s = viewmanageable.getViewName();
        if (s != null) {
            View view = ViewHelper.service.getView(s);
            if (view != null)
                s = view.getName();
            else throw new WTException("Not found View for Object=<" + obj + ">");
        }
        return s;
    }

    private String getVariationName(Object obj, Class class1) throws WTException {
        ViewManageable viewmanageable = (ViewManageable) obj;
        EnumeratedType enumeratedtype = ViewHelper.getVariation(viewmanageable, class1);
        return enumeratedtype != null ? enumeratedtype.toString() : null;
    }

    private void exportViewAttribute(Object obj, IxbElement ixbelement) throws WTException {
        try {
            String s = getViewName(obj);
            ixbelement.addValue("view", emptyIfNull(s));
            String s1 = getVariationName(obj, Variation1.class);
            if (s1 != null)
                ixbelement.addValue("variation1", emptyIfNull(s1));
            String s2 = getVariationName(obj, Variation2.class);
            if (s2 != null)
                ixbelement.addValue("variation2", emptyIfNull(s2));
        } catch (Exception exception) {
            logger.log("Exception in exportViewAttribute, ob=<" + ObjectProperty.getObjectDisplay(obj) + ">");
            processException(exception);
        }
    }

    public  Object importObject() throws WTException {
        Object obj = importAttribute();
        if(obj!=null && obj instanceof WTPart){
        	WTPart part = (WTPart)obj;
        	String  name=  getElementValue("name");
        	if(name!=null&&!"".equals(name)&&!"null".equals(name)&&!part.getName().equals(name)){
        		try {
					changeName(part,name);
				} catch (WTPropertyVetoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
        if (obj != null){
        	if(obj instanceof ErrorImportObject){
        	}else{
                this.impHdl.pubImportedObject(obj, getRemoteId());
        	}
        }
        return obj;
    }
    private void changeName(WTPart part, String name) throws WTException, WTPropertyVetoException {
    	WTPartMaster master = (WTPartMaster) part.getMaster();
    	WTPartMasterIdentity idy = (WTPartMasterIdentity) master.getIdentificationObject();
		idy.setName(name);
		master = (WTPartMaster) IdentityHelper.service.changeIdentity(master, idy);
	}

    public WTArrayList importObjects() throws WTException {
        WTArrayList list = new WTArrayList();
        list.add((Persistable) importObject());
        return list;
    }

    public Object importAttribute() throws WTException {
    	WTPart part =  null;
        try {
            String versionStr = this.version;
            //versionStr = getPropertiesValue(versionStr);
            versionStr = ext.sast.center.synch.MQExpImpUtil.attrConvertValue("versionInfo", versionStr, "IMP");
            WTPart newpart = null;

            part = (WTPart) CmExpImpSearchHelper.searchWTPartByNumberVersionIterationView(
                    this.number, versionStr, this.iteration,"Design");
            if (part != null) {
            	if(!part.isLatestIteration()){
            		*//*logger.log("Exception in WTPart, fname=<" + getPfilename() + ">");
                    logger.log(part.getNumber()+"小版本较低，149存在更高的小版本，不允许导入！");
                    ErrorImportObject eo = new ErrorImportObject();
                    eo.setNumber(this.number);
                    eo.setXmlName(getPfilename());
                    eo.setType("WTPart");
                    eo.setMessage(part.getNumber()+"小版本较低，149存在更高的小版本，不允许导入！");
                    return eo;*//*
            		String flag = CmWorkflowHelper.changeObjNumberOrDeleteObj(part, true);
            		if("RENAME".equals(flag)){
            			newpart = createNewObject();
                		if (newpart != null) {
                            this.impHdl.putInNewCreatedHashtable(getRemoteId(), newpart);
                            HashMap hmap = buildOrignalInfo();
                            this.impHdl.doOperationAfterStore(newpart, hmap);
                            logger.log("==>Import WTPart number=<" + this.number + "> version=" + this.version + "."
                                    + this.iteration + " OK!");
                        }
                        return newpart;
            		}
            	}else{
            		 logger.log("==>Import WTPart number=<" + this.number + "> version=" + this.version + "."
                             + this.iteration + " already imported, IGNORE!");
                     this.impHdl.putInExistedHashtable(getRemoteId(), part);
                     part = (WTPart) importIBAAttribute(part, this.root);
                     part = (WTPart) importLifecycleAttribute(part, this.root);
                     //add by hding 20171010 begin
                     //删掉默认可视化
                     try{
                     	Representation representation = VisualizationHelperFactory.HELPER.getRepresentation((Representable) part);

     	                if(representation==null){
     	                	logger.log("==> 不存在可视化，新增可视化");
     		                part = (WTPart) importRepresentationAttribute(part, this.root);
     	                }else{
     	                	logger.log("==> 存在可视化！跳过");

     	                }

                     }catch(Exception e){
                     	e.printStackTrace();
                     }
                     PersistenceServerHelper.manager.update(part);
                     //and by hding 20171010 end

                     return part;
            	}

            }
            part = (WTPart) CmExpImpSearchHelper.searchWTPartByNumberVersionView(this.number,
                    versionStr,"Design");
            if (part != null) {
            	String localIteration = part.getIterationInfo().getIdentifier().getValue();
            	if(isNumeric(this.iteration)&&isNumeric(localIteration)
            			&&Integer.parseInt(localIteration)>Integer.parseInt(this.iteration)){
            		*//*logger.log("Exception in import WTPart, fname=<" + getPfilename() + ">");
                    logger.log(part.getNumber()+"小版本较低，149存在更高的小版本，不允许导入！");
                    ErrorImportObject eo = new ErrorImportObject();
                    eo.setNumber(this.number);
                    eo.setXmlName(getPfilename());
                    eo.setType("WTPart");
                    eo.setMessage(part.getNumber()+"小版本较低，149存在更高的小版本，不允许导入！");
                    return eo;*//*
            		String flag = CmWorkflowHelper.changeObjNumberOrDeleteObj(part, true);
            		if("RENAME".equals(flag)){
            			newpart = createNewObject();
                		if (newpart != null) {
                            this.impHdl.putInNewCreatedHashtable(getRemoteId(), newpart);
                            HashMap hmap = buildOrignalInfo();
                            this.impHdl.doOperationAfterStore(newpart, hmap);
                            logger.log("==>Import WTPart number=<" + this.number + "> version=" + this.version + "."
                                    + this.iteration + " OK!");
                        }
                        return newpart;
            		}else if("DELETE".equals(flag)){
            			 part = (WTPart) CmExpImpSearchHelper.searchLatestWTPartByNumberView(this.number,"Design");
                         if (part != null) {

                         	if(part.getContainerName().startsWith("八院")){
                         		logger.log("==>八院数据跳过n: WTPart number=<" + this.number + "> version=" + this.version
                                         + "." + this.iteration);
                             	return part;
                             }


                             logger.log("==>Create new Version: WTPart number=<" + this.number + "> version=" + this.version
                                     + "." + this.iteration);
                             newpart = createNewVersion(part);
                         } else {
                             logger.log("==>Create new Object: WTPart number=<" + this.number + "> version=" + this.version
                                     + "." + this.iteration);
                             newpart = createNewObject();
                         }
                         if (newpart != null) {
                             this.impHdl.putInNewCreatedHashtable(getRemoteId(), newpart);
                             HashMap hmap = buildOrignalInfo();
                             this.impHdl.doOperationAfterStore(newpart, hmap);
                             logger.log("==>Import WTPart number=<" + this.number + "> version=" + this.version + "."
                                     + this.iteration + " OK!");
                         }
                         return newpart;
            		}

            	}else{
            		 logger.log("==>Create new Iteration: WTPart number=<" + this.number + "> version=" + this.version + "."
                             + this.iteration);
                     newpart = createNewIteration(part);
            	}
            } else {
                part = (WTPart) CmExpImpSearchHelper.searchLatestWTPartByNumberView(this.number,"Design");
                if (part != null) {

                	if(part.getContainerName().startsWith("八院")){
                		logger.log("==>八院数据跳过n: WTPart number=<" + this.number + "> version=" + this.version
                                + "." + this.iteration);
                    	return part;
                    }


                    logger.log("==>Create new Version: WTPart number=<" + this.number + "> version=" + this.version
                            + "." + this.iteration);
                    newpart = createNewVersion(part);
                } else {
                    logger.log("==>Create new Object: WTPart number=<" + this.number + "> version=" + this.version
                            + "." + this.iteration);
                    newpart = createNewObject();
                }
            }
            if (newpart != null) {
                this.impHdl.putInNewCreatedHashtable(getRemoteId(), newpart);
                HashMap hmap = buildOrignalInfo();
                this.impHdl.doOperationAfterStore(newpart, hmap);
                logger.log("==>Import WTPart number=<" + this.number + "> version=" + this.version + "."
                        + this.iteration + " OK!");
            }
            return newpart;
        } catch (Exception exception) {
            logger.log("Exception in importAttribute, fname=<" + getPfilename() + ">");
            //noticeException(exception,this.number);
            //processException(exception);
            String errorMsg = exception.getLocalizedMessage();
            if(errorMsg!=null&&errorMsg.contains("因为它已经被同步更新")){
            	logger.log(errorMsg);
            	return part;
            }else if(errorMsg!=null&&errorMsg.contains("子文件夹链接")&&errorMsg.contains("不是唯一的")){
            	logger.log(errorMsg);
            	return part;
            }else{
            	 ErrorImportObject eo = new ErrorImportObject();
                 eo.setNumber(this.number);
                 eo.setXmlName(getPfilename());
                 eo.setType("WTPart");
                 eo.setMessage(exception.getLocalizedMessage());
                 //noticeException(exception,this.number);
                 //processException(exception);
                 exception.printStackTrace();

                 logger.log(exception.getLocalizedMessage());

                 return eo;
            }

        }
    }

    private WTPart importWTPartMasterAttribute(Object obj, IxbElement ixbelement) throws WTException {
        WTPart wtpart = (WTPart) obj;
        try {
            String s2 = getElementValue(ixbelement, "genericType");
            String s3 = getElementValue(ixbelement, "endItem");
            String s4 = getElementValue(ixbelement, "defaultTraceCode");
            wtpart.setNumber(this.number);
            wtpart.setName(this.iname);
            String s5 = getElementValue(ixbelement, "defaultUnit");
            if (s5 != null)
                wtpart.setDefaultUnit(QuantityUnit.toQuantityUnit(s5));
            if (s2 != null) {
                GenericType generictype = GenericType.toGenericType(s2);
                ((WTPartMaster) wtpart.getMaster()).setGenericType(generictype);
            }
            if (s3 != null)
                wtpart.setEndItem(new Boolean(s3).booleanValue());
            if (s4 != null)
                wtpart.setDefaultTraceCode(TraceCode.toTraceCode(s4));
        } catch (Exception e) {
            logger.log("Exception in importWTPartMasterAttribute, fname=<" + getPfilename() + ">");
            processException(e);
        }
        return wtpart;
    }

    private WTPart importWTPartAttribute(Object obj, IxbElement ixbelement) throws WTException {
        WTPart wtpart = (WTPart) obj;
        try {
            String s = getElementValue(ixbelement, "partType");
            if (s != null)
                wtpart.setPartType(PartType.toPartType(s));
            String s1 = getElementValue(ixbelement, "partSource");
            if (s1 != null)
                wtpart.setSource(Source.toSource(s1));
            String s2 = getElementValue(this.root, "jobAuthorizationNumber");
            if (s2 != null)
                wtpart.setJobAuthorizationNumber(s2);
            String s3 = getElementValue(this.root, "contractNumber");
            if (s3 != null)
                wtpart.setContractNumber(s3);
            String s4 = getElementValue(this.root, "phase");
            if (s4 != null)
                wtpart.setPhase(s4);
            String s5 = getElementValue(this.root, "minRequired");
            if (s5 != null)
                try {
                    Integer integer = Integer.valueOf(s5.trim());
                    wtpart.setMinimumRequired(integer);
                } catch (NumberFormatException numberformatexception) {
                    throw new WTException(numberformatexception);
                }
            String s6 = getElementValue(this.root, "maxAllowed");
            if (s6 != null)
                try {
                    Integer integer1 = Integer.valueOf(s6.trim());
                    wtpart.setMaximumAllowed(integer1);
                } catch (NumberFormatException numberformatexception1) {
                    throw new WTException(numberformatexception1);
                }
        } catch (Exception e) {
            logger.log("Exception in importWTPartAttribute, fname=<" + getPfilename() + ">");
            processException(e);
        }
        return wtpart;
    }

    private ViewManageable importViewAttribute(Object obj, IxbElement ixbelement) throws WTException {
        ViewManageable viewmanageable = (ViewManageable) obj;
        try {
            String s = getElementValue(this.root, "view");
            String s1 = getElementValue(this.root, "variation1");
            String s2 = getElementValue(this.root, "variation2");
            if (s != null) {
                // s = this.impHdl.adjustViewName(s);
                View view = ViewHelper.service.getView(s);
                viewmanageable.setView(ViewReference.newViewReference(view));
                if (s1 != null) {
                    Variation1 variation1 = Variation1.toVariation1(s1);
                    viewmanageable.setVariation1(variation1);
                }
                if (s2 != null) {
                    Variation2 variation2 = Variation2.toVariation2(s2);
                    viewmanageable.setVariation2(variation2);
                }
            }
        } catch (Exception e) {
            logger.log("Exception in importViewAttribute, fname=<" + getPfilename() + ">");
            processException(e);
        }
        return viewmanageable;
    }

    private WTPart createNewObject() throws WTException {
        WTPart part = null;
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
            if (wtcontainerref ==null) {
            	wtcontainerref = this.impHdl.getWTContainerRef();
			}
            String containerRefName = "";
            if(wtcontainerref==null){
				///wt.inf.container.OrgContainer=sast/wt.inf.library.WTLibrary=��׼���̼���
				 String objectContainerPath = getElementValue(root, "objectContainerPath");
				 String tempPath = objectContainerPath.substring(objectContainerPath.indexOf("=")+1);;
				 String  clazz= tempPath.substring(tempPath.indexOf("/")+1,tempPath.indexOf("="));
				 containerRefName = tempPath.substring(tempPath.indexOf("=")+1);
				 wtcontainerref = impHdl.getWTContainerRef(Class.forName(clazz), containerRefName);
			}
            if(wtcontainerref==null){
            	throw new WTException(containerRefName+"产品库不存在；");
            }
            tx.start();

            part = WTPart.newWTPart();
            part = importWTPartMasterAttribute(part, this.root);
            part = (WTPart) importViewAttribute(part, this.root);
            part = (WTPart) importLifecycleAttribute(part, this.root);
            part = (WTPart) importVersionAttribute(part, this.root);

            part = importWTPartAttribute(part, this.root);
            part = (WTPart) importDomainFolderAttribute(part, this.root);

            part = (WTPart) importTypeDefinitionAttribute(part, this.root, this.root);
            part.setContainerReference(wtcontainerref);
            ((WTContained) part.getMaster()).setContainerReference(wtcontainerref);
            Mastered mastered = part.getMaster();
            PersistenceHelper.manager.save(mastered);
            methodcontext.put("ixb_store_object_context/key", part);
            WTArrayList wtarraylist = new WTArrayList(1);
            wtarraylist.add(part);
            Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY,
                    wtarraylist);
            Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
            WTHashSet wthashset = new WTHashSet();
            wthashset.add(part);
            Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
            part = (WTPart) PersistenceServerHelper.manager.store(part, createStamp, modifyStamp);
            methodcontext.remove("ixb_store_object_context/key");
            tx.commit();
            tx = null;
            part = (WTPart) importIBAAttribute(part, this.root);
         //   part = (WTPart) importClassificationNode(part, this.root);

           // part = (WTPart) importPartTypeAttribute(part, this.root);
            part = (WTPart) importContentItemAttribute(part, this.root);
            part = (WTPart) importRepresentationAttribute(part, this.root);
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
            methodcontext.remove("ixb_store_object_context/key");
        }
        return part;
    }

    *//*private WTPart importClassificationNode(WTPart part, IxbElement root) throws WTException {
    	String s = getElementValue(this.root, "ClassificationNode");
    	IBAUtility iba = new IBAUtility((IBAHolder)part);
		try {
			iba.setIBAValue( "ClassificationNode", s);
			part = (WTPart) iba.updateAttributeContainer(part);
			iba.updateIBAHolder(part);
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return part;
	}*//*

    private WTPart createNewVersion(WTPart part) throws WTException {
        WTPart newpart = null;
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
            tx.start();
            if (!VersionControlHelper.isLatestIteration(part))
                part = (WTPart) VersionControlHelper.getLatestIteration(part);
            String versionId = getElementValue(this.root, "versionInfo/versionId");
            versionId = ext.sast.center.synch.MQExpImpUtil.attrConvertValue("versionInfo", versionId, "IMP");
            // String versionLevel = getElementValue(this.root, "versionInfo/versionLevel");
            String iterationId = getElementValue(this.root, "versionInfo/iterationId");
            Series se = VersionControlHelper.getVersionIdentifier(part).getSeries();
            se.setValueWithoutValidating(versionId);
            VersionIdentifier vi = VersionIdentifier.newVersionIdentifier((MultilevelSeries) se);
            Series series = part.getIterationInfo().getIdentifier().getSeries();
            series.setValueWithoutValidating(iterationId);
            IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);
            newpart = (WTPart) VersionControlHelper.service.newVersion(part, true);
            VersionControlHelper.setIterationIdentifier(newpart, ii);
            VersionControlHelper.setVersionIdentifier(newpart, vi, false);
            newpart = (WTPart) importLifecycleAttribute(newpart);
            FolderHelper.assignLocation(newpart, FolderHelper.service.getFolder(part));
            newpart.setContainerReference(part.getContainerReference());

            methodcontext.put("ixb_store_object_context/key", newpart);
            WTArrayList wtarraylist = new WTArrayList(1);
            wtarraylist.add(newpart);
            Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY,
                    wtarraylist);
            Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
            WTHashSet wthashset = new WTHashSet();
            wthashset.add(newpart);
            Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);
            //newpart = (WTPart) importViewAttribute(newpart, this.root);
            newpart = (WTPart) VersionControlHelper.service.insertNode(newpart, null, null);
            newpart = importWTPartAttribute(newpart, this.root);

            if (PersistenceHelper.isPersistent(newpart))
                newpart = (WTPart) PersistenceHelper.manager.save(newpart);
            else newpart = (WTPart) PersistenceServerHelper.manager.store(newpart, createStamp, modifyStamp);
            methodcontext.remove("ixb_store_object_context/key");
            tx.commit();
            tx = null;
            newpart = (WTPart) importIBAAttribute(newpart, this.root);
            newpart = (WTPart) importContentItemAttribute(newpart, this.root);
            newpart = (WTPart) importRepresentationAttribute(newpart, this.root);
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
            methodcontext.remove("ixb_store_object_context/key");
        }
        return newpart;
    }

    private WTPart createNewIteration(WTPart part) throws WTException {
        WTPart newpart = null;
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
            tx.start();
            String iterationId = getElementValue(this.root, "versionInfo/iterationId");
            newpart = (WTPart) VersionControlHelper.service.newIteration(part, true);
           // newpart = (WTPart) importViewAttribute(newpart, this.root);
            Series series = part.getIterationInfo().getIdentifier().getSeries();
            series.setValueWithoutValidating(iterationId);
            IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);
            VersionControlHelper.setIterationIdentifier(newpart, ii);
            VersionControlServerHelper.setBranchIdentifier(newpart, VersionControlHelper.getBranchIdentifier(part));
            newpart.setControlBranch(VersionControlServerHelper.getControlBranch(part));
            newpart.setContainerReference(part.getContainerReference());
            FolderHelper.assignLocation(newpart, FolderHelper.service.getFolder(part));
            newpart = (WTPart) importLifecycleAttribute(newpart);

            methodcontext.put("ixb_store_object_context/key", newpart);
            WTArrayList wtarraylist = new WTArrayList(1);
            wtarraylist.add(newpart);
            Transaction.getGlobalMap().put(StandardVersionControlService.SVCS_SIGNAL_PERSISTENCE_OF_IGNORE_KEY,
                    wtarraylist);
            Transaction.getGlobalMap().put(MultiObjIBAValueDBService.DO_NOT_COPY_FORWARD_IBAS_KEY, wtarraylist);
            WTHashSet wthashset = new WTHashSet();
            wthashset.add(newpart);
            Transaction.getGlobalMap().put("PREVENT_OWNERSHIP_CHECK", wthashset);

            newpart = (WTPart) VersionControlHelper.service.insertIteration(newpart);

            newpart = importWTPartAttribute(newpart, this.root);

            if (PersistenceHelper.isPersistent(newpart))
                newpart = (WTPart) PersistenceHelper.manager.save(newpart);
            else {
                newpart = (WTPart) PersistenceServerHelper.manager.store(newpart, createStamp, modifyStamp);
            }
            methodcontext.remove("ixb_store_object_context/key");
            tx.commit();
            tx = null;
            newpart = (WTPart) importIBAAttribute(newpart, this.root);
            newpart = (WTPart) importContentItemAttribute(newpart, this.root);
            newpart = (WTPart) importRepresentationAttribute(newpart, this.root);
        } catch (Exception e) {
            if ((e instanceof WTException))
                throw ((WTException) e);
            throw new WTException(e);
        } finally {
            if (tx != null)
                tx.rollback();
            methodcontext.remove("ixb_store_object_context/key");
        }
        return newpart;
    }
}*/