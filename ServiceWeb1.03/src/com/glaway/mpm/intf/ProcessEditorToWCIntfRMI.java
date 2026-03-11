package com.glaway.mpm.intf;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.cache.ProcessCache;
import com.glaway.mpm.change.qchange.helper.ChangeProcessPlanStructure;
import com.glaway.mpm.constants.*;
import com.glaway.mpm.importdata.StandardImportService;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.mesParameter.model.GLZhuFuLink;
import com.glaway.mpm.model.*;
import com.glaway.mpm.model.data.CmAttachment;
import com.glaway.mpm.model.data.CmTreeNode;
import com.glaway.mpm.model.data.CmUser;
import com.glaway.mpm.mpmresource.gzcard.GZCardHelper;
import com.glaway.mpm.mpmresource.gznumber.number.GZNumberManager;
import com.glaway.mpm.mpmresource.helper.MPMResourceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwStandardPersistenceManager;
import com.glaway.mpm.pbom.helper.PBOMHelper;
import com.glaway.mpm.print.util.MBAUtil;
import com.glaway.mpm.processplan.ProcessPlanStructure;
import com.glaway.mpm.processplan.helper.ProcessPlanHelper;
import com.glaway.mpm.processplan.helper.ZhuFuLinkUtil;
import com.glaway.mpm.sjzyk.SjzykBean;
import com.glaway.mpm.sjzyk.SjzykDBUtil;
import com.glaway.mpm.util.Constant;
import com.glaway.mpm.util.IBAHelper;
import com.glaway.mpm.util.ReferenceFactory;
import com.glaway.mpm.util.*;
import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.query.common.QueryException;
import com.ptc.windchill.enterprise.change2.commands.RelatedChangesQueryCommands;
import com.ptc.windchill.enterprise.doc.commands.RelatedObjectsCommand;
import com.ptc.windchill.mpml.processplan.MPMProcessPlan;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanMaster;
import com.ptc.windchill.mpml.processplan.MPMProcessPlanMasterIdentity;
import com.ptc.windchill.mpml.resource.*;
import com.ptc.wvs.server.util.PublishUtils;
import ext.ases.envelope.ProcessEnvelope;
import ext.casc.change.CSCChange;
import ext.casc.doc.SubmitApprovalProcessor;
import ext.casc.fileprint.FilePrintUtil;
import ext.casc.fileprint.cache.GLFilePrintData;
import ext.casc.fileprint.cache.GLFilePrintDataHelper;
import ext.casc.integrate.process.ProcessService;
import ext.casc.integrate.util.BomUtil;
import ext.casc.mpm.process.GLProcessParams;
import ext.casc.mpm.process.TechnicsGenerator;
import ext.casc.persistence.PersistenceCommonHelper;
import ext.casc.process.ProcessTask;
import ext.casc.process.ProcessTaskItem;
import ext.casc.process.util.ProcessUtil;
import ext.casc.product.model.Batch;
import ext.casc.report.technics.DownloadTechnicsReportUtil;
import ext.casc.sop.constants.SopConstants;
import ext.casc.sop.process.StructureSopProcessPlan;
import ext.casc.sop.util.SopWorkflowUtil;
import ext.casc.system.SystemConfigurationUtil;
import ext.casc.util.*;
import ext.casc.workflow.PrintHelper;
import ext.casc.workflow.WorkflowHelper;
import ext.sast.catalog.GLCatalog;
import ext.sast.common.fc.CmPersistenceHelper;
import ext.sast.common.fc.CmQueryResult;
import ext.sast.common.fc.CmQuerySpec;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONObject;
import wt.change2.*;
import wt.content.*;
import wt.doc.*;
import wt.enterprise.Master;
import wt.epm.EPMDocument;
import wt.fc.*;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTKeyedMap;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.FolderNotFoundException;
import wt.httpgw.URLFactory;
import wt.iba.definition.IBADefinitionException;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.IBAHolder;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.inf.team.ContainerTeam;
import wt.inf.team.ContainerTeamHelper;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleServerHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.lifecycle.State;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.occurrence.OccurrenceHelper;
import wt.org.*;
import wt.part.*;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.pds.oracle81.OracleDataSource;
import wt.pom.Transaction;
import wt.pom.WTConnection;
import wt.project.Role;
import wt.query.*;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;
import wt.vc.Iterated;
import wt.vc.VersionControlHelper;
import wt.vc.Versioned;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;
import wt.viewmarkup.DerivedImage;
import wt.viewmarkup.ViewMarkUpHelper;
import wt.viewmarkup.Viewable;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;

import javax.vecmath.Matrix4d;
import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProcessEditorToWCIntfRMI implements RemoteAccess {

	private static VaLogger logger = VaLogger.getLogger(ProcessEditorToWCIntfRMI.class.getName());

	private static int index[] = { 0 };
	private static final String CLASSNAME = ProcessEditorToWCIntfRMI.class.getName();
	private static PropertiesUtil propertiesUtil = new PropertiesUtil(PropertiesConfigs.QIAN_LONG_CONFIG_PATH);
	private static PropertiesUtil typeToFolderPropertiesUtil = new PropertiesUtil(PropertiesConfigs.MPMRESOURCE_TYPE_TO_FOLDER_CONFIG_PATH);

	private static final String SUCCESS = "success";
	private static final String FAILED = "failed";
	private static final String ERRORMESSAGE = "errormessage";
	private static final String LIFECYCLE = "lifecycle";
	private static final String VERSION = "version";
	// 材料定额配置文件名称
	private static String MATERIAL_QUOTA_FILE_NAME = "MaterialQuotaFile";
	// 材料定额配置文件当前的版本
	private static String MATERIAL_QUOTA_FILE_VERSION = "";
	// 材料定额中间对象
	private static List<MaterialCal> MATERIALCAL_LIST = null;
	public static int BBLGY = 1;
	public static int JSXY = 2;

	public static String genTechnicsNumber(){
		String seqNo = "";
		try{
			seqNo = PersistenceHelper.manager.getNextSequence("TECHNICSCOMMON_SEQ");
		}catch (WTException e){
			e.printStackTrace();
		}
		return seqNo;
	}
	/**
	 * 通过零件获取POM XML文件
	 *
	 * @param partNumber
	 * @return
	 * @author qianlong
	 * @date 2012-11-1
	 */

	public static byte[] getPBOMXmlRMI(String oid) {
		GLLogger.debug(CLASSNAME, "--oid-" + oid);
		byte[] bytes = null;
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			if (part == null) {
				System.out.println(oid + "零件已被删除");
				return null;
			}
			bytes = PBOMHelper.getBOMXml(part, Constants.pbomDocEndwith);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		System.out.println(String.valueOf(bytes));
		return bytes;
	}

	/**
	 * 通过零件上传PBOM xml
	 *
	 * @param oid
	 * @param bytes
	 * @author qianlong
	 * @date 2012-11-5
	 */
	public static boolean savePBOMXmlRMI(String oid, byte[] bytes) {
		GLLogger.debug(CLASSNAME, "--oid-" + oid);
		boolean flag = false;
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			PBOMHelper.saveBOMXml(part, bytes, Constants.pbomDocEndwith);
			transaction.commit();
			transaction = null;
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return flag;
	}

	/**
	 * 获取查询设备结果
	 *
	 * @return
	 * @author qianlong
	 * @date 2012-11-1
	 */

	public static List<Equipment> getEquipmentsRMI(String number, String name) {
		GLLogger.debug(CLASSNAME, "--number-" + number + "--name-" + name);
		List<Equipment> list = new ArrayList<Equipment>();
		number = Util.formatSearchString(number);
		name = Util.formatSearchString(name);
		try {
			List<MPMTooling> equipmentList = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.SB);
			for (MPMTooling tooling : equipmentList) {
				Equipment equipment = MPMResourceHelper.getEquipment(tooling);
				list.add(equipment);
			}
			Util.sort(list);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Equipment> getEquipments(Map<String, String> map, String library) {
		GLLogger.debug(CLASSNAME, "-map-" + map + "--library-" + library);
		List<Equipment> list = new ArrayList<Equipment>();
		try {
			if (map == null) {
				List<MPMTooling> knifeList = MPMResourceUtil.getAllKnifes(TypeNameConstants.SB);
				for (MPMTooling tooling : knifeList) {
					Equipment knifeTool = MPMResourceHelper.getEquipment(tooling);
					list.add(knifeTool);
				}
				Util.sort(list);
			} else {
				String name = Util.formatSearchString(map.get("name"));
				String number = Util.formatSearchString(map.get("number"));
				map.remove("name");
				map.remove("number");

				List<MPMTooling> knifeList = MPMResourceUtil.getKnifes(number, name, map, TypeNameConstants.SB);
				for (MPMTooling tooling : knifeList) {
					Equipment knifeTool = MPMResourceHelper.getEquipment(tooling);
					list.add(knifeTool);
				}
				Util.sort(list);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取查询标准仪器仪表结果
	 *
	 * @return
	 * @author LongXiuChuan
	 * @date 2014-03-13
	 */

	public static List<Dashboard> getSDashboards(String number, String name) {
		GLLogger.debug(CLASSNAME, "--number-" + number + "--name-" + name);
		List<Dashboard> list = new ArrayList<Dashboard>();
		number = Util.formatSearchString(number);
		name = Util.formatSearchString(name);
		try {
			List<MPMTooling> equipmentList = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.YQYB);
			for (MPMTooling tooling : equipmentList) {
				Dashboard dashboard = MPMResourceHelper.getDashboard(tooling);
				list.add(dashboard);
			}
			Util.sort(list);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Dashboard> getSDashboards(Map<String, String> map, String library) {
		GLLogger.debug(CLASSNAME, "-map-" + map + "--library-" + library);
		List<Dashboard> list = new ArrayList<Dashboard>();
		try {
			if (map == null) {
				List<MPMTooling> knifeList = MPMResourceUtil.getAllSDashboards(TypeNameConstants.YQYB);
				for (MPMTooling tooling : knifeList) {
					Dashboard knifeTool = MPMResourceHelper.getDashboard(tooling);
					list.add(knifeTool);
				}
				Util.sort(list);
			} else {
				String name = Util.formatSearchString(map.get("name"));
				String number = Util.formatSearchString(map.get("number"));
				map.remove("name");
				map.remove("number");

				List<MPMTooling> knifeList = MPMResourceUtil.getSDashboards(number, name, map, TypeNameConstants.YQYB);
				for (MPMTooling tooling : knifeList) {
					Dashboard knifeTool = MPMResourceHelper.getDashboard(tooling);
					list.add(knifeTool);
				}
				Util.sort(list);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取查询标准仪器仪表结果
	 *
	 * @return
	 * @author LongXiuChuan
	 * @date 2014-03-13
	 */

	public static List<UnSDashboard> getUnSDashboards(String number, String name) {
		GLLogger.debug(CLASSNAME, "--number-" + number + "--name-" + name);
		List<UnSDashboard> list = new ArrayList<UnSDashboard>();
		number = Util.formatSearchString(number);
		name = Util.formatSearchString(name);
		try {
			List<MPMTooling> equipmentList = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.YQYB);
			for (MPMTooling tooling : equipmentList) {
				UnSDashboard dashboard = MPMResourceHelper.getUnSDashboard(tooling);
				list.add(dashboard);
			}
			Util.sort(list);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<UnSDashboard> getUnSDashboards(Map<String, String> map, String library) {
		GLLogger.debug(CLASSNAME, "-map-" + map + "--library-" + library);
		List<UnSDashboard> list = new ArrayList<UnSDashboard>();
		try {
			if (map == null) {
				List<MPMTooling> knifeList = MPMResourceUtil.getAllUnSDashboards(TypeNameConstants.YQYB);
				for (MPMTooling tooling : knifeList) {
					UnSDashboard knifeTool = MPMResourceHelper.getUnSDashboard(tooling);
					list.add(knifeTool);
				}
				Util.sort(list);
			} else {
				String name = Util.formatSearchString(map.get("name"));
				String number = Util.formatSearchString(map.get("number"));
				map.remove("name");
				map.remove("number");

				List<MPMTooling> knifeList = MPMResourceUtil.getUnSDashboards(number, name, map, TypeNameConstants.YQYB);
				for (MPMTooling tooling : knifeList) {
					UnSDashboard knifeTool = MPMResourceHelper.getUnSDashboard(tooling);
					list.add(knifeTool);
				}
				Util.sort(list);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Object> getFrocksRMI(String number, String name, String type) {
		GLLogger.debug(CLASSNAME, "--number-" + number + "--name-" + name);
		List<Object> list = new ArrayList<Object>();
		number = Util.formatSearchString(number);
		name = Util.formatSearchString(name);
		try {
			if ("1".equals(type)) {
				List<MPMTooling> frockList = MPMResourceUtil.getMPMToolingByLikeNumberAndName(number, name, TypeNameConstants.GZhuang);
				for (MPMTooling tooling : frockList) {
					Frock frock = MPMResourceHelper.getFrock(tooling);
					list.add(frock);
				}
			} else if ("2".equals(type)) {
				List<MPMTooling> toolList = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.GJ);
				for (MPMTooling tooling : toolList) {
					Tool tool = MPMResourceHelper.getTool(tooling);
					list.add(tool);
				}
			} else if ("3".equals(type)) {
				List<MPMTooling> toolList = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.LJ);
				for (MPMTooling tooling : toolList) {
					Tool tool = MPMResourceHelper.getTool(tooling);
					list.add(tool);
				}
			} else if ("4".equals(type)) {
				List<MPMTooling> knifeList = MPMResourceUtil.getMPMToolingByLike(number, name, TypeNameConstants.DJ);
				for (MPMTooling tooling : knifeList) {
					KnifeTool knifeTool = MPMResourceHelper.getKnifeTool(tooling);
					list.add(knifeTool);
				}
			}
			Util.sort(list);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return list;
	}

	/* 查询工位结果 add by zhuhao 2017.10.17 */
	public static List<WorkPlace> getWorkPlaceRMI(Map<String, String> map, String library) {
		List<WorkPlace> list = new ArrayList<WorkPlace>();
		try {
			if (map == null) {
				List<MPMTooling> List = MPMResourceUtil.getAllMeasures(TypeNameConstants.GWWH);
				for (MPMTooling tooling : List) {
					WorkPlace Tool = MPMResourceHelper.getWorkplace(tooling);
					list.add(Tool);
				}
				Util.sort(list);
			} else {
				String name = Util.formatSearchString(map.get("name"));
				String number = Util.formatSearchString(map.get("number"));
				map.remove("name");
				map.remove("number");

				List<MPMTooling> List = MPMResourceUtil.getMeasures(number, name, map, TypeNameConstants.GWWH);
				for (MPMTooling tooling : List) {
					WorkPlace Tool = MPMResourceHelper.getWorkplace(tooling);
					list.add(Tool);
				}
				Util.sort(list);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Tool> getMeasures(Map<String, String> map, String library) {
		GLLogger.debug(CLASSNAME, "-map-" + map + "--library-" + library);
		List<Tool> list = new ArrayList<Tool>();
		try {
			if (map == null) {
				List<MPMTooling> knifeList = MPMResourceUtil.getAllMeasures(TypeNameConstants.LJ);
				for (MPMTooling tooling : knifeList) {
					Tool knifeTool = MPMResourceHelper.getTool(tooling);
					list.add(knifeTool);
				}
				Util.sort(list);
			} else {
				String name = Util.formatSearchString(map.get("name"));
				String number = Util.formatSearchString(map.get("number"));
				map.remove("name");
				map.remove("number");

				List<MPMTooling> knifeList = MPMResourceUtil.getMeasures(number, name, map, TypeNameConstants.LJ);
				for (MPMTooling tooling : knifeList) {
					Tool knifeTool = MPMResourceHelper.getTool(tooling);
					list.add(knifeTool);
				}
				Util.sort(list);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<KnifeTool> getKnifes(Map<String, String> map, String library) {
		GLLogger.debug(CLASSNAME, "-map-" + map + "--library-" + library);
		List<KnifeTool> list = new ArrayList<KnifeTool>();
		try {
			if (map == null) {
				List<MPMTooling> knifeList = MPMResourceUtil.getAllKnifes(TypeNameConstants.DJ);
				for (MPMTooling tooling : knifeList) {
					KnifeTool knifeTool = MPMResourceHelper.getKnifeTool(tooling);
					list.add(knifeTool);
				}
				Util.sort(list);
			} else {
				String name = Util.formatSearchString(map.get("name"));
				String number = Util.formatSearchString(map.get("number"));
				map.remove("name");
				map.remove("number");

				List<MPMTooling> knifeList = MPMResourceUtil.getKnifes(number, name, map, TypeNameConstants.DJ);
				for (MPMTooling tooling : knifeList) {
					KnifeTool knifeTool = MPMResourceHelper.getKnifeTool(tooling);
					list.add(knifeTool);
				}
				Util.sort(list);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @param number
	 * @return
	 * @author qianlong
	 * @date 2013-8-15
	 */
	public static Frock getFrockByNumberRMI(String number) {
		Frock frock = null;
		try {
			MPMTooling tooling = MPMResourceUtil.getMPMToolingByNumber(number.replace("\\.", "-"), TypeNameConstants.GZhuang);
			frock = MPMResourceHelper.getFrock(tooling);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return frock;
	}

	/**
	 * 获取查询工艺辅料结果
	 *
	 * @return
	 * @author qianlong
	 * @date 2012-11-1
	 */

	public static List<Material> getMaterialsRMI(Map<String, String> map, String library) {
		GLLogger.debug(CLASSNAME, "-map-" + map + "--library-" + library);
		List<Material> list = new ArrayList<Material>();
		try {
			Map<String, String> libraryMap = new HashMap<String, String>();
			String value = propertiesUtil.getProperty("material-library");
			for (String str : value.split(";")) {
				String[] libraryArray = str.split(",");
				if (libraryArray.length == 2) {
					libraryMap.put(libraryArray[0], libraryArray[1]);
				}
			}
			WTContainer container = WTContainerUtil.getLibraryByName(libraryMap.get(library));
			if ("1".equals(library)) {
				if (map == null) {
					QueryResult result = WTPartUtil.getAllMPMProcessMaterial(container);
					while (result.hasMoreElements()) {
						MPMProcessMaterial processMaterial = (MPMProcessMaterial) result.nextElement();
						Material material = MPMResourceHelper.getMaterial(processMaterial);
						list.add(material);
					}
				} else {
					String name = Util.formatSearchString(map.get("name"));
					GLLogger.debug(CLASSNAME, "library---" + libraryMap.get(library));
					String number = Util.formatSearchString(map.get("number"));
					map.remove("name");
					map.remove("number");

					QueryResult result = MPMResourceUtil.getProcessMaterialByIBANameConatiner(number, name, map, container);
					while (result.hasMoreElements()) {
						MPMProcessMaterial processMaterial = (MPMProcessMaterial) result.nextElement();
						Material material = MPMResourceHelper.getMaterial(processMaterial);
						list.add(material);
					}
				}
			} else {
				String name = Util.formatSearchString(map.get("name"));
				String number = Util.formatSearchString(map.get("number"));
				GLLogger.debug(CLASSNAME, "library---" + libraryMap.get(library));

				QueryResult result = WTPartUtil.getPartByLikeNumberNameContainer(number, name, container);
				while (result.hasMoreElements()) {
					WTPart part = (WTPart) result.nextElement();
					Material material = MPMResourceHelper.getMaterial(part);
					list.add(material);
				}
			}
			Util.sort(list);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取所有的工装工装工装
	 *
	 * @author qianlong
	 * @date 2012-12-15
	 */
	public static FkType getAllFrocksRMI() {
		// Date startDate = new Date();
		// FkType fkType = null;
		// try {
		// fkType = new FkType("工装");
		// MPMResourceHelper.getAllFrocks(fkType);
		// } catch (WTException e) {
		// e.printStackTrace();
		// } catch (WTPropertyVetoException e) {
		// e.printStackTrace();
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
		// Date endDate = new Date();
		// System.out.println("frock:" + (endDate.getTime() -
		// startDate.getTime()));
		// return fkType;

		return get812AllFrocksRMI();
	}

	public static FkType get812AllFrocksRMI() {
		if(ProcessCache.GET812ALLFROCKSRMI!=null){
			return ProcessCache.GET812ALLFROCKSRMI;
		}
		FkType fkType = null;
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(Constants.rootFolder + "/" + Constants.mpmResourceFolderName[6], WTContainerRef.newWTContainerRef(container));
			fkType = new FkType(folder.getName());
			MPMResourceHelper.getFrockFolder(folder, fkType);
		} catch (WTException e) {
			e.printStackTrace();
		}
		ProcessCache.GET812ALLFROCKSRMI = fkType;
		return fkType;
	}

	public static ToolType get812AllToolsRMI() {
		if(ProcessCache.GET812ALLTOOLSRMI!=null){
			return ProcessCache.GET812ALLTOOLSRMI;
		}
		ToolType toolType = null;
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(Constants.rootFolder + "/" + Constants.mpmResourceFolderName[5], WTContainerRef.newWTContainerRef(container));
			toolType = new ToolType(folder.getName());
			MPMResourceHelper.getToolFolder(folder, toolType);
			List<Tool> tools = MPMResourceHelper.getToolByType(folder.getFolderPath());
			toolType.setTools(tools);
		} catch (WTException e) {
			e.printStackTrace();
		}
		ProcessCache.GET812ALLTOOLSRMI = toolType;
		return toolType;
	}

	public static ToolType get812AllMeasuresRMI() {
		if(ProcessCache.GET812ALLMEASURESRMI!=null){
			return ProcessCache.GET812ALLMEASURESRMI;
		}
		ToolType toolType = null;
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(Constants.rootFolder + "/" + Constants.mpmResourceFolderName[10], WTContainerRef.newWTContainerRef(container));
			toolType = new ToolType(folder.getName());
			MPMResourceHelper.getToolFolder(folder, toolType);
			List<Tool> tools = MPMResourceHelper.getToolByType(folder.getFolderPath());
			toolType.setTools(tools);

		} catch (WTException e) {
			e.printStackTrace();
		}
		ProcessCache.GET812ALLMEASURESRMI = toolType;
		return toolType;
	}

	public static EpType get812AllEquipmentsRMI() {
		if(ProcessCache.GET812ALLEQUIPMENTSRMI!=null){
			return ProcessCache.GET812ALLEQUIPMENTSRMI;
		}
		EpType epType = null;
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(Constants.rootFolder + "/" + Constants.mpmResourceFolderName[3], WTContainerRef.newWTContainerRef(container));
			epType = new EpType(folder.getName());
			MPMResourceHelper.getEquipmentFolder(folder, epType);
			List<Equipment> equipments = MPMResourceHelper.getEquipmentByType(folder.getFolderPath());
			epType.setEquipments(equipments);
		} catch (WTException e) {
			e.printStackTrace();
		}
		ProcessCache.GET812ALLEQUIPMENTSRMI = epType;
		return epType;
	}

	public static DashboardType getAllDashboards() {
		if(ProcessCache.getAllDashboards!=null){
			return ProcessCache.getAllDashboards;
		}
		DashboardType epType = null;
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(Constants.rootFolder + "/" + Constants.mpmResourceFolderName[11], WTContainerRef.newWTContainerRef(container));
			epType = new DashboardType(folder.getName());
			MPMResourceHelper.getDashboardFolder(folder, epType);
			List<Dashboard> dashboards = MPMResourceHelper.getDashboardByType(folder.getFolderPath());
			epType.setDashboards(dashboards);
		} catch (WTException e) {
			e.printStackTrace();
		}
		ProcessCache.getAllDashboards =epType;
		return epType;
	}

	public static UnSDashboardType getAllUnSDashboards() {
		if(ProcessCache.getAllUnSDashboards!=null){
			return ProcessCache.getAllUnSDashboards;
		}
		UnSDashboardType dashboardType = null;
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(Constants.rootFolder + "/" + Constants.mpmResourceFolderName[12], WTContainerRef.newWTContainerRef(container));
			dashboardType = new UnSDashboardType(folder.getName());
			MPMResourceHelper.getUnSDashboardFolder(folder, dashboardType);
			List<UnSDashboard> dashboards = MPMResourceHelper.getUnSDashboardByType(folder.getFolderPath());
			dashboardType.setDashboards(dashboards);
		} catch (WTException e) {
			e.printStackTrace();
		}
		ProcessCache.getAllUnSDashboards = dashboardType;
		return dashboardType;
	}

	/**
	 * 获取所有的刀具
	 *
	 * @author qianlong
	 * @date 2012-12-15
	 */
	public static KtType getAllKnifeToolsRMI() {
		// Map<TypeIdentifier, List<TypeIdentifier>> typeMap = new
		// HashMap<TypeIdentifier, List<TypeIdentifier>>();
		// TypeIdentifier typeIdentifier =
		// TypedUtility.getTypeIdentifier(TypeNameConstants.DJ);
		// KtType ktType = null;
		// try {
		// ktType = new KtType(TypedUtility.getLocalizedTypeName(typeIdentifier,
		// Locale.CHINA));
		// TypeUtil.getAllChildTypes(typeMap, typeIdentifier);
		// MPMResourceHelper.getAllKnifeTools(typeMap, typeIdentifier, ktType);
		// } catch (WTException e) {
		// e.printStackTrace();
		// } catch (WTPropertyVetoException e) {
		// e.printStackTrace();
		// } catch (RemoteException e) {
		// e.printStackTrace();
		// }
		// return ktType;

		return get812AllKnifeRMI();
	}

	public static KtType get812AllKnifeRMI() {
		if(ProcessCache.GET812ALLKNIFERMI!=null){
			return ProcessCache.GET812ALLKNIFERMI;
		}
		KtType ktType = null;
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(Constants.rootFolder + "/" + Constants.mpmResourceFolderName[4], WTContainerRef.newWTContainerRef(container));
			ktType = new KtType(folder.getName());
			MPMResourceHelper.getKnifeFolder(folder, ktType);
		} catch (WTException e) {
			e.printStackTrace();
		}
		ProcessCache.GET812ALLKNIFERMI = ktType;
		return ktType;
	}

	/**
	 * 获取工艺辅件类型
	 *
	 * @return
	 * @author qianlong
	 * @date 2013-6-8
	 */
	public static MtType getMaterialTypesRMI() {
		MtType mtType = null;
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(Constants.rootFolder + "/" + Constants.mpmResourceFolderName[7], WTContainerRef.newWTContainerRef(container));
			mtType = new MtType(folder.getName());
			MPMResourceHelper.getMaterialFolder(folder, mtType);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return mtType;
	}

	/**
	 * 获取某个材料类型下的所有的材料
	 *
	 * @param typeName
	 * @return
	 * @author qianlong
	 * @date 2013-6-8
	 */
	@SuppressWarnings("deprecation")
	public static List<Material> getMaterialsByTypeRMI(String typePath) {
		GLLogger.debug(CLASSNAME, "typePath----" + typePath);
		List<Material> materialList = new ArrayList<Material>();
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(typePath, WTContainerRef.newWTContainerRef(container));
			QueryResult result = FolderHelper.service.findFolderContents(folder, MPMProcessMaterial.class);

			while (result.hasMoreElements()) {
				MPMProcessMaterial processMaterial = (MPMProcessMaterial) result.nextElement();
				Material material = MPMResourceHelper.getMaterial(processMaterial);
				materialList.add(material);
			}
			Util.sort(materialList);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return materialList;
	}

	/**
	 * 获取查询制造单位和其关联的工种、工位的结果
	 *
	 * @return
	 * @author qianlong
	 * @date 2012-11-1
	 */

	public static List<Object> getAllWorkShopsRMI() {
		Date startDate = new Date();
		Map<String, WorkShop> workshopMap = new HashMap<String, WorkShop>();
		List<ShopType> allShopTypeList = new ArrayList<ShopType>();
		Map<String, ShopType> allShopTypeMap = new HashMap<String, ShopType>();
		// Map<WorkShop, List<ShopType>> allWorkShopToShopTypeMap = new
		// HashMap<WorkShop, List<ShopType>>();

		try {
			// 获取所有的工种及其下面的结构
			MPMResourceHelper.getAllShopType(allShopTypeMap, allShopTypeList);

			QueryResult result = MPMResourceUtil.getAllPlant();
			while (result.hasMoreElements()) {
				MPMPlant plant = (MPMPlant) result.nextElement();
				WorkShop workShop = MPMResourceHelper.getWorkShop(plant);

				// List<ShopType> shopTypeList = new ArrayList<ShopType>();
				// List<PdName> pdNameList = new ArrayList<PdName>();
				List<WorkSpace> workSpaceList = new ArrayList<WorkSpace>();
				List<Skill> skillList = new ArrayList<Skill>();
				// List<EpType> epTypeList = new ArrayList<EpType>();
				// Map<TypeIdentifier, List<Equipment>> equipMap = new
				// HashMap<TypeIdentifier, List<Equipment>>();
				// Map<String, List<Equipment>> nameEquipMap = new
				// HashMap<String, List<Equipment>>();
				//
				// List<ToolType> toolTypeList = new ArrayList<ToolType>();
				// List<ToolType> measureTypeList = new ArrayList<ToolType>();
				// Map<TypeIdentifier, List<Tool>> toolMap = new
				// HashMap<TypeIdentifier, List<Tool>>();
				// Map<TypeIdentifier, List<Tool>> measureMap = new
				// HashMap<TypeIdentifier, List<Tool>>();
				// Map<String, List<Tool>> nameToolMap = new HashMap<String,
				// List<Tool>>();
				// Map<String, List<Tool>> nameMeasureMap = new HashMap<String,
				// List<Tool>>();
				QueryResult qr = MPMResourceUtil.getChildPart(plant);
				while (qr.hasMoreElements()) {
					WTPart childPart = (WTPart) qr.nextElement();
					// 获取所有的工位
					if (childPart instanceof MPMWorkCenter) {
						MPMWorkCenter workCenter = (MPMWorkCenter) childPart;
						WorkSpace workSpace = MPMResourceHelper.getWorkSpace(workCenter);
						workSpaceList.add(workSpace);
					}
					// 工种
					else if (childPart instanceof MPMSkill) {
						MPMSkill mpmskill = (MPMSkill) childPart;
						System.out.println("======mpmskill.getName()==========" + mpmskill.getName());
						Skill skill = MPMResourceHelper.getSkill(mpmskill);
						skillList.add(skill);
					}
					// 获取所有的设备,工量具和工序名称
					else if (childPart instanceof MPMTooling) {
						MPMTooling tooling = (MPMTooling) childPart;
						TypeIdentifier identifier = TypedUtility.getTypeIdentifier(tooling);
						// // 设备
						// if
						// (identifier.getTypename().contains(TypeNameConstants.SB))
						// {
						// // MPMResourceHelper.getEquipmentByWorkShop(tooling,
						// identifier, equipMap, nameEquipMap);
						// }
						// // 工具
						// else if
						// (identifier.getTypename().contains(TypeNameConstants.GJ))
						// {
						// MPMResourceHelper.getToolByWorkShop(tooling,
						// identifier, toolMap, nameToolMap);
						// }
						// //量具
						// else
						// if(identifier.getTypename().contains(TypeNameConstants.LJ)){
						// MPMResourceHelper.getToolByWorkShop(tooling,
						// identifier,measureMap, nameMeasureMap);
						// }
						// // 工序名称
						// else
						// if
						// (identifier.getTypename().contains(TypeNameConstants.GXMC))
						// {
						// MPMResourceHelper.getPdNameByWorkShop(tooling,
						// shopTypeList, pdNameList, allShopTypeMap);
						// }
					}
				}
				// MPMResourceHelper.getEquipStructureType(equipMap,
				// nameEquipMap, epTypeList);
				// MPMResourceHelper.getToolStructureType(toolMap, nameToolMap,
				// toolTypeList);
				// MPMResourceHelper.getMeasureStructureType(measureMap,
				// nameMeasureMap, measureTypeList);
				// Util.sort(epTypeList);
				// Util.sort(toolTypeList);
				// Util.sort(pdNameList);
				// Util.sort(workSpaceList);
				// workShop.setEpTypes(epTypeList);
				// workShop.setToolTypes(toolTypeList);
				// workShop.setMeasureTypes(measureTypeList);
				// workShop.setPdNames(pdNameList);
				workShop.setWorkSpaces(workSpaceList);
				workShop.setSkillList(skillList);
				workshopMap.put(workShop.getName(), workShop);
				// allWorkShopToShopTypeMap.put(workShop, shopTypeList);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		// 排序
		List<WorkShop> workshopList = new ArrayList<WorkShop>();
		String array[] = { "一厂", "二厂", "三厂", "四厂", "料", "微", "艺", "质", "外", "木", "装", "一部", "二部", "三部", "四部", "五部", "六部", "八部", "九部", "外办", "车" };
		for (String str : array) {
			WorkShop workShop = workshopMap.get(str);
			if (workShop != null) {
				workshopList.add(workShop);
				workshopMap.remove(str);
			}
		}
		for (String str : workshopMap.keySet()) {
			workshopList.add(workshopMap.get(str));
		}
		// 返回list
		List<Object> list = new ArrayList<Object>();
		list.add(workshopList);
		list.add(allShopTypeList);
		// list.add(allWorkShopToShopTypeMap);

		Date endDate = new Date();
		System.out.println("workshop:" + (endDate.getTime() - startDate.getTime()));
		return list;
	}

	/**
	 * @return
	 * @author qianlong
	 * @date 2012-12-14
	 */
	public static List<WorkShop> getAllEquipmentByWorkShopRMI() {
		List<WorkShop> workshopList = new ArrayList<WorkShop>();
		try {
			QueryResult result = MPMResourceUtil.getAllPlant();
			while (result.hasMoreElements()) {
				MPMPlant plant = (MPMPlant) result.nextElement();
				WorkShop workShop = MPMResourceHelper.getWorkShop(plant);

				List<EpType> epTypeList = new ArrayList<EpType>();
				Map<TypeIdentifier, List<Equipment>> map = new HashMap<TypeIdentifier, List<Equipment>>();
				Map<String, List<Equipment>> nameEquipMap = new HashMap<String, List<Equipment>>();

				QueryResult qrResult = MPMResourceUtil.getChildPart(plant);
				while (qrResult.hasMoreElements()) {
					WTPart childPart = (WTPart) qrResult.nextElement();
					// 获取所有的设备
					if (childPart instanceof MPMTooling) {
						MPMTooling tooling = (MPMTooling) childPart;
						Equipment equipment = MPMResourceHelper.getEquipment(tooling);
						TypeIdentifier identifier = TypedUtility.getTypeIdentifier(tooling);
						List<Equipment> equipmentList = map.get(identifier);
						if (equipmentList == null) {
							equipmentList = new ArrayList<Equipment>();
							map.put(identifier, equipmentList);
						}
						equipmentList.add(equipment);
						String typeName = TypedUtility.getLocalizedTypeName(identifier, Locale.CHINA);
						List<Equipment> equipmentList1 = nameEquipMap.get(typeName);
						if (equipmentList1 == null) {
							equipmentList1 = new ArrayList<Equipment>();
							nameEquipMap.put(typeName, equipmentList1);
						}
						equipmentList1.add(equipment);
					}
				}

				// MPMResourceHelper.getStructureType(map, nameEquipMap, 1,
				// "com.nriet.Equipment", epTypeList);
				workShop.setEpTypes(epTypeList);

				workshopList.add(workShop);
			}
		} catch (WTException e) {

			e.printStackTrace();
		}
		return workshopList;
	}

	/**
	 * 获取整件名称和编号
	 *
	 * @return
	 * @author qianlong
	 * @date 2012-11-1
	 */
	public static List<TempObject> getPartAttributesRMI(String number, String name) {
		GLLogger.debug(CLASSNAME, "--number-" + number + "--name-" + name);
		List<TempObject> list = new ArrayList<TempObject>();

		try {
			List<WTPart> partList = new ArrayList<WTPart>();
			number = Util.formatSearchString(number);
			name = Util.formatSearchString(name);
			GLLogger.debug(CLASSNAME, number + "---" + name);
			partList = WTPartUtil.getWholePart(number, name);
			for (WTPart part : partList) {
				TempObject tempObject = new TempObject();
				tempObject.setOid(Util.getStringOid(part));
				tempObject.setNumber(part.getNumber());
				tempObject.setName(part.getName());
				list.add(tempObject);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取工艺名称和编号
	 *
	 * @return
	 * @author qianlong
	 * @date 2012-11-1
	 */
	public static List<TempObject> searchTechnicsAttributesRMI(String number, String name, String type) {
		GLLogger.debug(CLASSNAME, "--number-" + number + "--name-" + name + "--type-" + type);
		List<TempObject> list = new ArrayList<TempObject>();
		try {
			// number = Util.formatSearchString(number);
			name = Util.formatSearchString(name);
			if (null != type) {
				type = type.trim();
				// if ("1".equals(type)) {
				// type = TypeNameConstants.ASSEMBLE_PROCESSPLAN_TYPE_NAME;
				// } else if ("2".equals(type)) {
				// type = TypeNameConstants.PART_PROCESSPLAN_TYPE_NAME;
				// } else {
				// type = "";
				// }
				int t = Integer.parseInt(type);
				if (t == 0) {
					type = "";
				} else {
					type = "com.ptc.windchill.mpml.processplan.MPMProcessPlan|" + LoadConfig.getInstance().getLocalDomainName() + "." + LoadConfig.getInstance().getTechnicsType()[2][t - 1];

				}
			}
			List<MPMProcessPlan> processPlanList = MPMProcessPlanUtil.getMPMProcessPlanByLikeNumberNameType(number, name, type);
			for (MPMProcessPlan processPlan : processPlanList) {
				TempObject tempObject = new TempObject();
				tempObject.setOid(Util.getStringOid(processPlan));
				tempObject.setName(processPlan.getName());
				tempObject.setLifecycle(processPlan.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
				tempObject.setVersion(processPlan.getIterationDisplayIdentifier().toString());
				tempObject.setType(processPlan.getType());

				IBAHelper ibaHelper = new IBAHelper(processPlan);
				tempObject.setNumber(ibaHelper.getIBAValue("PPNUMBER"));
				tempObject.setDocNumber(processPlan.getNumber());

				list.add(tempObject);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<TempObject> searchTechnicsAttributesRMI2(String number, String name, String type) {
		GLLogger.debug(CLASSNAME, "--number-" + number + "--name-" + name + "--type-" + type);
		List<TempObject> list = new ArrayList<TempObject>();
		try {
			number = Util.formatSearchString(number);
			name = Util.formatSearchString(name);
			if (null != type) {
				type = type.trim();
				int t = Integer.parseInt(type);
				if (t == 0) {
					type = null;
				} else {
					type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149." + LoadConfig.getInstance().getTechnicsType()[5][t - 1];

				}
			}
			QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, type);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				TempObject tempObject = new TempObject();
				tempObject.setOid(Util.getStringOid(document));
				tempObject.setName(document.getName());
				tempObject.setLifecycle(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
				tempObject.setVersion(document.getIterationDisplayIdentifier().toString());
				tempObject.setType(TypeHelper.getLocalizedTypeString(document, Locale.CHINA));

				IBAHelper ibaHelper = new IBAHelper(document);
				tempObject.setNumber(ibaHelper.getIBAValue("PPNUMBER"));
				tempObject.setDocNumber(document.getNumber());

				list.add(tempObject);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取典型/通用工艺名称和编号
	 *
	 * @return
	 * @author LongXiuChuan
	 * @date 2014-5-28
	 */
	public static List<TempObject> searchTechnicsAttributesRMI2(String number, String name, String type, boolean isGX) {
		GLLogger.debug(CLASSNAME, "--number-" + number + "--name-" + name + "--type-" + type);
		boolean falg = SessionServerHelper.manager.setAccessEnforced(false);
		String user = null;
		List<TempObject> list = new ArrayList<TempObject>();
		try {

			try {
				user = SessionHelper.manager.getPrincipal().getName();
				SessionHelper.manager.setAdministrator();
			} catch (Exception e) {
			}
			number = Util.formatSearchString(number);
			name = Util.formatSearchString(name);
			Vector<WTDocument> all = new Vector<WTDocument>();
			if ("全部".equals(type)) {
				type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.DX_PROCESS_DOC";
				QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, type);
				if (qr.size() > 0) {
					all.addAll(qr.getObjectVectorIfc().getVector());
				}

				type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TY_PROCESS_DOC";
				qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, type);
				if (qr.size() > 0) {
					all.addAll(qr.getObjectVectorIfc().getVector());
				}

				type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.GUOJIABIAOZHUN";
				qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, type);
				if (qr.size() > 0) {
					all.addAll(qr.getObjectVectorIfc().getVector());
				}
				if (!isGX) {
					type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149.DIANXING_PROCESSPLAN";
					qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, type);
					if (qr.size() > 0) {
						Vector<WTDocument> documentList = qr.getObjectVectorIfc().getVector();
						Vector<WTDocument> approveDocList = new Vector<WTDocument>();
						for (WTDocument document : documentList) {
							String state = document.getState().toString();
							if (state.equals("APPROVED")) {
								approveDocList.add(document);
							}
						}
						all.addAll(approveDocList);
					}
				}
			} else if ("典型工艺".equals(type)) {
				type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.DX_PROCESS_DOC";
				QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, type);
				if (qr.size() > 0) {
					all.addAll(qr.getObjectVectorIfc().getVector());
				}
			} else if ("通用工艺".equals(type)) {
				type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.TY_PROCESS_DOC";
				QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, type);
				if (qr.size() > 0) {
					all.addAll(qr.getObjectVectorIfc().getVector());
				}
			} else if ("国家标准".equals(type)) {
				type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.GUOJIABIAOZHUN";
				QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, type);
				if (qr.size() > 0) {
					all.addAll(qr.getObjectVectorIfc().getVector());
				}
			} else if ("newTypicalTechnic".equals(type) || "结构化典型工艺".equals(type)) {
				type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149.DIANXING_PROCESSPLAN";
				QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, type);
				if (qr.size() > 0) {
					Vector<WTDocument> documentList = qr.getObjectVectorIfc().getVector();
					Vector<WTDocument> approveDocList = new Vector<WTDocument>();
					for (WTDocument document : documentList) {
						String state = document.getState().toString();
						if (state.equals("APPROVED")) {
							approveDocList.add(document);
						}
					}
					all.addAll(approveDocList);
				}
			}
			for (WTDocument document : all) {
				TempObject tempObject = new TempObject();
				tempObject.setOid(Util.getStringOid(document));
				tempObject.setName(document.getName());
				tempObject.setLifecycle(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
				tempObject.setVersion(document.getIterationDisplayIdentifier().toString());
				String tyttpe = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
				if (tyttpe.endsWith("casc.sast.149.DIANXING_PROCESSPLAN")) {
					tempObject.setType("结构化典型工艺");
				} else {
					tempObject.setType(TypedUtility.getLocalizedTypeName(document, Locale.CHINA));
				}
				tempObject.setDocNumber(document.getNumber());

				IBAHelper ibaHelper = new IBAHelper(document);
				tempObject.setNumber(ibaHelper.getIBAValue("PPNUMBER"));

				list.add(tempObject);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(falg);
			if (!"".equals(user)) {
				try {
					SessionHelper.manager.setPrincipal(user);
				} catch (Exception e) {
				}
			}
		}

		return list;
	}

	public static List<TempObject> searchMainMakeTechnics(String number, String name, int typeIndex, String pplanType) {
		GLLogger.debug(CLASSNAME, "--number-" + number + "--name-" + name + "--type-" + typeIndex);
		List<TempObject> list = new ArrayList<TempObject>();
		try {
			number = Util.formatSearchString(number);
			name = Util.formatSearchString(name);
			pplanType = Util.formatSearchString(pplanType);
			Vector<WTDocument> all = new Vector<WTDocument>();
			String[] docType = LoadConfig.getInstance().getTechnicsType()[5];
			String technicType = "";
			if (typeIndex == 0) {
				for (int i = 0; i < docType.length; i++) {
					technicType = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149." + docType[i];
					QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, technicType);
					if (qr.size() > 0) {
						Vector<WTDocument> documentList = qr.getObjectVectorIfc().getVector();
						Vector<WTDocument> approveDocList = new Vector<WTDocument>();
						for (WTDocument document : documentList) {
							IBAUtility iba = new IBAUtility(document);
							String planType = Util.formatSearchString(iba.getIBAValue("PPLANTYPE"));
							String zfFlag = iba.getIBAValue("ZFFLAG");
							if (zfFlag != null && zfFlag.equals("Z") && planType.equals(pplanType)) {
								approveDocList.add(document);
							}
						}
						all.addAll(approveDocList);
					}
				}
			}
			if (typeIndex > 0) {
				technicType = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149." + docType[typeIndex - 1];
				QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberAndNameType(number, name, technicType);
				if (qr.size() > 0) {
					Vector<WTDocument> documentList = qr.getObjectVectorIfc().getVector();
					Vector<WTDocument> approveDocList = new Vector<WTDocument>();
					for (WTDocument document : documentList) {
						IBAUtility iba = new IBAUtility(document);
						String planType = Util.formatSearchString(iba.getIBAValue("PPLANTYPE"));
						String zfFlag = iba.getIBAValue("ZFFLAG");
						if (zfFlag != null && zfFlag.equals("Z") && planType.equals(pplanType)) {
							approveDocList.add(document);
						}
					}
					all.addAll(approveDocList);
				}
			}
			for (WTDocument document : all) {
				TempObject tempObject = new TempObject();
				tempObject.setOid(Util.getStringOid(document));
				tempObject.setName(document.getName());
				tempObject.setLifecycle(document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
				tempObject.setVersion(document.getIterationDisplayIdentifier().toString());
				tempObject.setType(TypedUtility.getLocalizedTypeName(document, Locale.CHINA));
				tempObject.setDocNumber(document.getNumber());

				IBAHelper ibaHelper = new IBAHelper(document);
				tempObject.setNumber(ibaHelper.getIBAValue("PPNUMBER"));

				list.add(tempObject);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 通过工艺,或者工艺zip包文件,获取工艺压缩包
	 *
	 * @param oid
	 * @return
	 * @author qianlong
	 * @date 2012-11-9
	 */
	public static List<Vector<Object>> searchTechnicsRMI(String oid) {
		GLLogger.debug(CLASSNAME, "oid---" + oid);

		List<Vector<Object>> list = new ArrayList<Vector<Object>>();
		try {
			WTDocument document = null;
			MPMProcessPlan processPlan = (MPMProcessPlan) Util.getObjectByOid(MPMProcessPlan.class, oid);
			if (processPlan == null) {
				document = (WTDocument) Util.getObjectByOid(WTDocument.class, oid);
			} else {
				document = MPMProcessPlanUtil.getWTDocumentByProcessPlan(processPlan);
			}
			list = ProcessPlanHelper.getProcessZip(document);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "list---" + list);
		return list;
	}

	public static List<Vector<Object>> getTechnicsByPartRMI(Map map) {
		return getTechnicsByPartRMI(map,null);
	}

	public static List<Vector<Object>> getTechnicsByPartRMI(Map map,Map<String,WTGroup> aclGroupMap) {
		GLLogger.debug(CLASSNAME, "-map-" + map);
		String technicsType = (String) map.get("technicsType");
		String isEditable = (String) map.get("isEditable");
		String reportTech = null;
		if ("report".equals((String) map.get("reportTechnics"))) {
			reportTech = "report";
		}
		if ("report".equals(reportTech)) {
			try {
				WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, (String) map.get("oid"));
				return ProcessPlanHelper.getReportProcessZip(part);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<Vector<Object>> list = new ArrayList<Vector<Object>>();
		if (technicsType == null || "".equals(technicsType)) {
			String[] types = LoadConfig.getInstance().getTechnicsType()[1];
			try {
				WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, (String) map.get("oid"));
				for (int i = 0; i < types.length; i++) {
					List<Vector<Object>> tempList = ProcessPlanHelper.getProcessZip(part, reportTech, types[i], (String) map.get("category"), isEditable,aclGroupMap);
					if (tempList != null) {
						list.addAll(tempList);
					}
				}

				// 下载报表类工艺文件
				String docType = LoadConfig.getInstance().getReportTechnicsType();
				List<Vector<Object>> tempList = ProcessPlanHelper.getProcessZip(part, reportTech, docType, (String) map.get("category"), isEditable);
				if (tempList != null) {
					list.addAll(tempList);
				}
			} catch (WTException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		} else {
			try {
				WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, (String) map.get("oid"));
				list = ProcessPlanHelper.getProcessZip(part, null, technicsType, (String) map.get("category"), isEditable);
			} catch (WTException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}

		GLLogger.debug(CLASSNAME, "list---" + list);
		return list;
	}

	public static Map<String,WTGroup> getAclGroupByGyType() throws WTException {
		WTUser currentUser = (WTUser)SessionHelper.manager.getPrincipal();
		return ProcessPlanHelper.getAclGroupByGyType(currentUser);
	}
	/**
	 * 材料定额统计使用
	 *
	 */
	@SuppressWarnings("unchecked")
	public static List<Vector<Object>> getApprovedTechnicsByPartRMI(Map map) {
		GLLogger.debug(CLASSNAME, "-map-" + map);
		String technicsType = (String) map.get("technicsType");
		String isEditable = (String) map.get("isEditable");
		String reportTechnics = (String) map.get("reportTechnics");
		String reportTech = null;
		if ("report".equals(reportTechnics)) {
			reportTech = "report";
		}
		List<Vector<Object>> list = new ArrayList<Vector<Object>>();
		if (technicsType == null || "".equals(technicsType)) {
			String[] types = LoadConfig.getInstance().getTechnicsType()[1];
			try {
				WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, (String) map.get("oid"));
				for (int i = 0; i < types.length; i++) {
					List<Vector<Object>> tempList = ProcessPlanHelper.getApprovedProcessZip(part, reportTech, types[i], (String) map.get("category"), isEditable);
					if (tempList != null) {
						list.addAll(tempList);
					}
				}

				// 下载报表类工艺文件
				String docType = LoadConfig.getInstance().getReportTechnicsType();
				List<Vector<Object>> tempList = ProcessPlanHelper.getApprovedProcessZip(part, reportTech, docType, (String) map.get("category"), isEditable);
				if (tempList != null) {
					list.addAll(tempList);
				}
			} catch (WTException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		} else {
			try {
				WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, (String) map.get("oid"));
				list = ProcessPlanHelper.getApprovedProcessZip(part, null, technicsType, (String) map.get("category"), isEditable);
			} catch (WTException e) {
				e.printStackTrace();
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}

		GLLogger.debug(CLASSNAME, "list---" + list);
		return list;
	}

	/**
	 * 上传工艺压缩包
	 *
	 * @param oid
	 * @param name
	 * @param bytes
	 * @return
	 * @author qianlong
	 * @date 2012-11-21
	 */
	public static HashMap<String, String> uploadTechnicsRMI(byte[] bytes, Map<String, String> map, String xmlVersion, String note) {
		GLLogger.debug(CLASSNAME, "-oid-" + map.get("oid") + "-technicsNumber-" + map.get("technicsNumber") + "--technicsName-" + map.get("technicsName") + "--partType-" + map.get("partType")
				+ "--pplanNumber--" + map.get("pplanNumber"));
		HashMap<String, String> returnMap = new HashMap<String, String>();
		SessionServerHelper.manager.setAccessEnforced(false);
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, map.get("oid"));

			WTDocument document = ProcessPlanHelper.uploadProcessZip(part, map.get("technicsNumber"), map.get("technicsName"), map.get("technicsType"), bytes, note);

			/*
			 * IBAHelper iba = new IBAHelper(document);
			 * iba.setIBAValue(document, "PPNUMBER", map.get("pplanNumber"));
			 * iba.updateAttributeContainer(document);
			 * iba.updateIBAHolder(document);
			 */

			Map ibaMap = new HashMap();
			if (map.get("pplanNumber") != null && !"".equals(map.get("pplanNumber"))) {
				ibaMap.put("PPNUMBER", map.get("pplanNumber"));
			}
			if (map.get("CINDEX") != null && !"".equals(map.get("CINDEX"))) {
				ibaMap.put("CINDEX", map.get("CINDEX"));
			}
			if (map.get("MINDEX") != null && !"".equals(map.get("MINDEX"))) {
				ibaMap.put("MINDEX", map.get("MINDEX"));
			}
			if (map.get("PINDEX") != null && !"".equals(map.get("PINDEX"))) {
				ibaMap.put("PINDEX", map.get("PINDEX"));
			}
			if (map.get("PPLANTYPE") != null && !"".equals(map.get("PPLANTYPE"))) {
				ibaMap.put("PPLANTYPE", map.get("PPLANTYPE"));
			}
			if (map.get("ZFFLAG") != null && !"".equals(map.get("ZFFLAG"))) {
				ibaMap.put("ZFFLAG", map.get("ZFFLAG"));
			}
			if (map.get("DEPT") != null && !"".equals(map.get("DEPT"))) {
				ibaMap.put("DEPT", map.get("DEPT"));
			}
			if (map.get("PHASE_CODE") != null && !"".equals(map.get("PHASE_CODE"))) {
				ibaMap.put("PHASE_CODE", map.get("PHASE_CODE"));
			}
			if (map.get("KEYCOMPONENT") != null && !"".equals(map.get("KEYCOMPONENT"))) {
				ibaMap.put("KEYCOMPONENT", map.get("KEYCOMPONENT"));
			}
			// 批次号，add by liangbo
			if (map.get("BATCH") != null) {
				ibaMap.put("BATCH", map.get("BATCH"));
			}

			if (map.get("BIAOSHI") != null&& !"".equals(map.get("BIAOSHI"))){
				ibaMap.put("BIAOSHI", map.get("BIAOSHI"));
			}
			// 是否材料定额，add by hding
			if (map.get("isCLDE") != null && !"".equals(map.get("isCLDE"))) {
				ibaMap.put("isCLDE", map.get("isCLDE"));
			}
			// SOP文件编号
			if (map.get("SopNumber") != null && !"".equals(map.get("SopNumber"))) {
				ibaMap.put("SopNumber", map.get("SopNumber"));
			}
			// 专业类别
			if (map.get("SpecializedType") != null && !"".equals(map.get("SpecializedType"))) {
				ibaMap.put("SpecializedType", map.get("SpecializedType"));
			}
			// 工序名称
			if (map.get("ProceduceName") != null && !"".equals(map.get("ProceduceName"))) {
				ibaMap.put("ProceduceName", map.get("ProceduceName"));
			}
			// 操作岗位
			if (map.get("OperationJob") != null && !"".equals(map.get("OperationJob"))) {
				ibaMap.put("OperationJob", map.get("OperationJob"));
			}
			// 定制区域
			if (map.get("CustomArea") != null && !"".equals(map.get("CustomArea"))) {
				ibaMap.put("CustomArea", map.get("CustomArea"));
			}
			// 参数项目
			if (map.get("Parameters") != null && !"".equals(map.get("Parameters"))) {
				ibaMap.put("Parameters", map.get("Parameters"));
			}
			// 密级
			if (map.get("SECRET") != null && !"".equals(map.get("SECRET"))) {
				ibaMap.put("SECRET", map.get("SECRET"));
			}
			// 期限
			if (map.get("Term") != null && !"".equals(map.get("Term"))) {
				ibaMap.put("Term", map.get("Term"));
			}
			// 专业代号
			if (map.get("ProfessionalCode") != null && !"".equals(map.get("ProfessionalCode"))) {
				ibaMap.put("ProfessionalCode", map.get("ProfessionalCode"));
			}
			// 工序简号
			if (map.get("GONGXUJIANHAO") != null && !"".equals(map.get("GONGXUJIANHAO"))) {
				ibaMap.put("GONGXUJIANHAO", map.get("GONGXUJIANHAO"));
			}
			// 工序简号
			if (map.get("department") != null && !"".equals(map.get("department"))) {
				String s = map.get("department");
				ibaMap.put("SopDepartment", map.get("department"));
			}
			if (map.get("description") != null && !"".equals(map.get("description"))) {
				String s = map.get("description");
				document.setDescription(s);
			}

			if (map.get("bzyjNum") != null&& !"".equals(map.get("bzyjNum"))){
				ibaMap.put("bzyjNum", map.get("bzyjNum"));
			}
			if (map.get("bzyjName") != null&& !"".equals(map.get("bzyjName"))){
				ibaMap.put("bzyjName", map.get("bzyjName"));
			}
			if (map.get("yyfl") != null&& !"".equals(map.get("yyfl"))){
				ibaMap.put("yyfl", map.get("yyfl"));
			}
			IBAHelper attrHelper = new IBAHelper(document);
			attrHelper.setIBAValue(document, ibaMap);

			document = (WTDocument) PersistenceHelper.manager.refresh(document);

			// 新增更改类型，更改后工艺文档要与对应更改单关联 begin
			String changeOrderOid = map.get("changeOrderOid");
			if (changeOrderOid != null && !"null".equals(changeOrderOid) && !"".equals(changeOrderOid)) {
				WTChangeOrder2 changeOrder = (WTChangeOrder2) Util.getObjectByOid(ChangeOrder2.class, changeOrderOid);
				WTDocument afterDoc = null;
				List cas = CSCChange.getReleatedCA(changeOrder, false);
				for (int j = 0; j < cas.size(); j++) {
					WTChangeActivity2 ca = (WTChangeActivity2) cas.get(j);
					ArrayList<WTObject> afters = CSCChange.getCAResultItem(ca);
					for (int i = 0; i < afters.size(); i++) {
						WTObject after = afters.get(i);
						if (after instanceof WTDocument) {
							afterDoc = (WTDocument) after;
						}
					}
				}
				if (afterDoc == null) {
					System.out.println("afterDoc=====null");
					Vector v = new Vector();
					v.add(document);
					// 关联修订工艺文档到更改单的更改后文件
					QueryResult ecaResult = ChangeHelper2.service.getChangeActivities(changeOrder);
					while (ecaResult.hasMoreElements()) {
						WTChangeActivity2 activity2 = (WTChangeActivity2) ecaResult.nextElement();
						ChangeHelper2.service.storeAssociations(ChangeRecord2.class, activity2, v);
					}
				}
			}
			// end

			returnMap.put("oid", Util.getStringOid(document));
			returnMap.put(LIFECYCLE, document.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
			returnMap.put(VERSION, document.getIterationDisplayIdentifier().toString());
			returnMap.put(SUCCESS, SUCCESS);
			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			returnMap.put(SUCCESS, FAILED);
			returnMap.put(ERRORMESSAGE, "上载工艺规程错误");
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		GLLogger.debug(CLASSNAME, "returnMap---" + returnMap);
		return returnMap;
	}

	/**
	 * 再次上传工艺文件压缩包
	 *
	 * @param oid
	 * @param name
	 * @param bytes
	 * @return
	 * @author caolei
	 * @date 2015-11-04
	 */

	public static HashMap<String, String> reUploadTechnicsRMI(byte[] bytes, Map<String, String> map) {
		GLLogger.debug(CLASSNAME, "-oid-" + map.get("oid") + "-technicsNumber-" + map.get("technicsNumber") + "--technicsName-" + map.get("technicsName") + "--partType-" + map.get("partType")
				+ "--pplanNumber--" + map.get("pplanNumber"));
		HashMap<String, String> returnMap = new HashMap<String, String>();
		SessionServerHelper.manager.setAccessEnforced(false);
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, map.get("oid"));

			// part =
			// (WTPart)CmExpImpSearchHelper.searchLatestIteratedByNumberAndView(WTPart.class,part.getNumber(),"Manufacturing");

			WTDocument document = ProcessPlanHelper.reUploadProcessZip(part, map.get("technicsNumber"), map.get("technicsName"), map.get("technicsType"), bytes);

			IBAHelper iba = new IBAHelper(document);
			iba.setIBAValue(document, "PAGE", String.valueOf(map.get("page")));
			iba.updateAttributeContainer(document);
			iba.updateIBAHolder(document);
			document = (WTDocument) PersistenceHelper.manager.refresh(document);

			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			returnMap.put(SUCCESS, FAILED);
			returnMap.put("erro", "再次上载工艺规程错误");
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
		GLLogger.debug(CLASSNAME, "returnMap---" + returnMap);
		return returnMap;
	}

	/**
	 * 获取中间模型零件的.prt图档
	 *
	 * @param map
	 * @author qianlong
	 * @date 2012-11-26
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static List getMidModelCADRMI(Map<String, String> map) {
		GLLogger.debug(CLASSNAME, "--map--" + map);
		List list = new ArrayList();
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, map.get("oid"));
			part = WTPartUtil.getLatestPartByNumberAndView(part, Constants.design);
			GLLogger.debug(CLASSNAME, "-part-" + part);
			if (part == null) {
				return list;
			}
			EPMDocument epmDocument = WTPartUtil.get3DEPMDocumentByPart(part);
			GLLogger.debug(CLASSNAME, "-epmDocument-" + epmDocument);
			if (epmDocument == null) {
				return list;
			}
			FormatContentHolder formatContentHolder = (FormatContentHolder) ContentHelper.service.getContents(epmDocument);
			ContentItem contentItem = ContentHelper.service.getPrimary(formatContentHolder);
			GLLogger.debug(CLASSNAME, "-contentItem-" + contentItem);
			if ((contentItem != null) && (contentItem instanceof ApplicationData)) {
				byte[] aps = WTDocumentUtil.applicationDataToByte((ApplicationData) contentItem);
				list.add(epmDocument.getCADName());
				list.add(aps);
			}

		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "-list-" + list);
		return list;
	}

	/**
	 * 获取整件及其下皆所有的.prt图档
	 *
	 * @return
	 * @author qianlong
	 * @date 2012-11-24
	 */
	@SuppressWarnings({ "deprecation", "unchecked" })
	public static List getAnimationCADRMI(Map<String, String> map) {
		GLLogger.debug(CLASSNAME, "--map--" + map);
		List list = new ArrayList();
		Map<String, byte[]> streamMap = new HashMap<String, byte[]>();
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, map.get("oid"));
			part = WTPartUtil.getLatestPartByNumberAndView(part, Constants.design);
			GLLogger.debug(CLASSNAME, "--part--" + part);
			if (part == null) {
				return list;
			}
			EPMDocument epmDocument = WTPartUtil.get3DEPMDocumentByPart(part);
			GLLogger.debug(CLASSNAME, "--epmDocument--" + epmDocument);
			if (epmDocument == null) {
				return list;
			}
			Vector<EPMDocument> vector = new Vector<EPMDocument>();
			vector.add(epmDocument);
			EPMDocumentUtil.getAllChildreanEPMDocument(epmDocument, vector);

			for (EPMDocument object : vector) {
				FormatContentHolder formatContentHolder = (FormatContentHolder) ContentHelper.service.getContents(object);
				ContentItem contentItem = ContentHelper.service.getPrimary(formatContentHolder);
				if ((contentItem == null) || (!(contentItem instanceof ApplicationData))) {
					continue;
				}
				byte[] aps = WTDocumentUtil.applicationDataToByte((ApplicationData) contentItem);
				if (aps == null) {
					continue;
				}
				streamMap.put(object.getCADName(), aps);
			}
			list.add(epmDocument.getCADName());
			list.add(streamMap);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "-streamMap-" + streamMap);
		return list;
	}

	/**
	 * 获取图档和注释的名称
	 *
	 * @param map
	 * @author qianlong
	 * @date 2012-11-26
	 */

	public static Map<List<Object>, Map<String, byte[]>> getCADAndMarkupNameRMI(Map<String, String> map) {
		GLLogger.debug(CLASSNAME, "-map-" + map);
		Map<List<Object>, Map<String, byte[]>> nameMap = new HashMap<List<Object>, Map<String, byte[]>>();
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, map.get("oid"));
			part = WTPartUtil.getLatestPartByNumberAndView(part, Constants.design);
			GLLogger.debug(CLASSNAME, "--part--" + part);
			if (part == null) {
				return nameMap;
			}
			EPMDocument epmDocument = WTPartUtil.get3DEPMDocumentByPart(part);
			GLLogger.debug(CLASSNAME, "--epmDocument--" + epmDocument);
			if (epmDocument == null) {
				return nameMap;
			}
			List<EPMDocument> docList = EPMDocumentUtil.getMiddleModelEPMDocument(part.getNumber());
			docList.add(epmDocument);
			for (EPMDocument epmDoc : docList) {
				QueryResult repResult = PublishUtils.getRepresentations(epmDoc);
				List<Object> list = new ArrayList<Object>();
				Map<String, byte[]> markupMap = new HashMap<String, byte[]>();
				while (null != repResult && repResult.hasMoreElements()) {
					Object object = repResult.nextElement();
					if (object instanceof Representation) {
						if (object instanceof DerivedImage) {
							Representation representation = (Representation) ContentHelper.service.getContents((DerivedImage) object);
							Map<ContentRoleType, String> endWithMap = new HashMap<ContentRoleType, String>();
							endWithMap.put(ContentRoleType.THUMBNAIL, ".jpg");
							Map<String, byte[]> repMap = EPMDocumentUtil.getRepByRoleAndEndWith(representation, endWithMap);
							for (String str : repMap.keySet()) {
								list.add(str);
								list.add(repMap.get(str));
							}
							list.add(Util.getStringOid(epmDoc));
						}
					}
				}
				nameMap.put(list, markupMap);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "-nameMap-" + nameMap);
		return nameMap;
	}

	/**
	 * 获取图档和注释
	 *
	 * @param map
	 * @author qianlong
	 * @date 2012-11-26
	 */

	public static Map<Map<String, byte[]>, Map<String, byte[]>> getCADAndMarkupRMI(Map<String, List<String>> map) {
		GLLogger.debug(CLASSNAME, "-map-" + map);
		Map<Map<String, byte[]>, Map<String, byte[]>> cadMap = new HashMap<Map<String, byte[]>, Map<String, byte[]>>();
		try {
			for (String str : map.keySet()) {
				Persistable persistable = null;
				persistable = (Persistable) Util.getObjectByOid(EPMDocument.class, str);
				if (persistable == null) {
					persistable = (Persistable) Util.getObjectByOid(WTPart.class, str);
				}
				QueryResult repResult = PublishUtils.getRepresentations(persistable);
				Map<String, byte[]> repMap = new HashMap<String, byte[]>();
				Map<String, byte[]> markupMap = new HashMap<String, byte[]>();
				while (null != repResult && repResult.hasMoreElements()) {
					Object object = repResult.nextElement();
					if (object instanceof Representation) {
						if (object instanceof DerivedImage) {
							Representation representation = (Representation) ContentHelper.service.getContents((DerivedImage) object);
							Map<ContentRoleType, String> endWithMap = new HashMap<ContentRoleType, String>();
							endWithMap.put(ContentRoleType.THUMBNAIL, ".jpg");
							endWithMap.put(ContentRoleType.SECONDARY, ".ol");
							endWithMap.put(ContentRoleType.PRODUCT_VIEW_ED, ".pvs");
							repMap = EPMDocumentUtil.getRepByRoleAndEndWith(representation, endWithMap);
						}
					}
				}
				cadMap.put(repMap, markupMap);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {

			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "-cadMap-" + cadMap);
		return cadMap;
	}

	/**
	 * 获取PVS 用于pbom和参装工具图档的显示
	 *
	 * @param map
	 * @return
	 * @author qianlong
	 * @date 2013-7-16
	 */
	public static Map<String, byte[]> getPVSAndMarkupRMI(Map<String, String> map) {
		Map<String, byte[]> pvsMap = new HashMap<String, byte[]>();
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, map.get("oid"));
			String viewName = map.get("viewName");
			if (!part.getViewName().equals(viewName)) {
				part = WTPartUtil.getLatestPartByNumberAndView(part, viewName);
			}
			QueryResult repResult = EPMDocumentUtil.getRepresentations(part);
			while (null != repResult && repResult.hasMoreElements()) {
				Object object = repResult.nextElement();
				if (object instanceof Representation) {
					if (object instanceof DerivedImage) {
						Representation representation = (Representation) ContentHelper.service.getContents((DerivedImage) object);
						Map<ContentRoleType, String> endWithMap = new HashMap<ContentRoleType, String>();
						endWithMap.put(ContentRoleType.SECONDARY, ".ol");
						endWithMap.put(ContentRoleType.PRODUCT_VIEW_ED, ".pvs");
						pvsMap = EPMDocumentUtil.getRepByRoleAndEndWith(representation, endWithMap);
					}
					String name = "";
					for (String str : pvsMap.keySet()) {
						if (str.endsWith(".pvs")) {
							name = str.replace(".pvs", "");
							break;
						}
					}
					if (!name.equals("") && object instanceof Viewable) {
						QueryResult markupResult = ViewMarkUpHelper.service.getMarkUps((Viewable) object);
						Map<String, byte[]> markupMap = EPMDocumentUtil.getMarkupImgNoSmallImg(markupResult, name + ".etb", null, false);
						pvsMap.putAll(markupMap);
					}

				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return pvsMap;
	}

	/**
	 * 获取资源的简图
	 *
	 * @param map
	 * @return
	 * @author qianlong
	 * @date 2013-2-21
	 */
	public static byte[] getResourceImageRMI(String oid) {
		byte[] imageByte = null;
		try {
			Map<String, byte[]> repMap = new HashMap<String, byte[]>();
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			part = WTPartUtil.getLatestPartByNumberAndView(part, Constants.design);
			QueryResult repResult = EPMDocumentUtil.getRepresentations(part);
			while (null != repResult && repResult.hasMoreElements()) {
				Object object = repResult.nextElement();
				if (object instanceof Representation) {
					// 获取图档简图
					if (object instanceof DerivedImage) {
						Representation representation = (Representation) ContentHelper.service.getContents((DerivedImage) object);
						Map<ContentRoleType, String> endWithMap = new HashMap<ContentRoleType, String>();
						endWithMap.put(ContentRoleType.THUMBNAIL, null);
						repMap = EPMDocumentUtil.getRepByRoleAndEndWith(representation, endWithMap);
					}
				}
			}
			if (repMap != null && repMap.size() != 0) {
				Iterator<byte[]> iterator = repMap.values().iterator();
				if (iterator.hasNext()) {
					imageByte = iterator.next();
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return imageByte;
	}

	/**
	 * 通过零件获取零件的可视化图档使用creoView打开的url
	 *
	 * @param partOid
	 * @author qianlong
	 * @date 2013-3-28
	 */
	public static String getCreoViewUrlRMI(String oid) {
		String creoViewUrl = "";
		try {
			WTContainer container = null;
			Persistable persistable = null;
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			if (part == null) {
				EPMDocument epmDocument = (EPMDocument) Util.getObjectByOid(EPMDocument.class, oid);
				if (epmDocument == null) {
					return creoViewUrl;
				} else {
					persistable = epmDocument;
					container = epmDocument.getContainer();
				}

			} else {
				persistable = part;
				container = part.getContainer();
			}

			QueryResult repResult = PublishUtils.getRepresentations(persistable);
			if (repResult == null || repResult.size() == 0) {
				return creoViewUrl;
			}
			Persistable paramPersistable = (Persistable) repResult.nextElement();

			String viewUrl = "";
			String repOid = "";
			if ((paramPersistable instanceof Representation)) {
				repOid = com.ptc.wvs.server.util.Util.SandR(PublishUtils.getRefFromObject(paramPersistable), ":", "%3A");
				viewUrl = PublishUtils.getPreferedViewURL((Representation) paramPersistable, true);
			}
			if ("".equals(creoViewUrl) && !"".equals(viewUrl)) {
				String url = new StringBuilder().append(viewUrl).append("&objref=").append(repOid).toString();
				creoViewUrl = new StringBuilder().append(PropertiesUtil.getHttpCodeBase() + "/wtcore/jsp/wvs/edrview.jsp").append("?url=").append(url).append("&ContainerOid=OR%3A")
						.append(com.ptc.wvs.server.util.Util.SandR(container.toString(), ":", "%3A")).toString();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return creoViewUrl;
	}

	/**
	 * 获取所有工艺模板类型和其下的模板
	 *
	 * @return
	 * @author qianlong
	 * @date 2012-11-15
	 */
	public static TpType getProcessTemplatesRMI(String type) {
		GLLogger.debug(CLASSNAME, "-type-" + type);
		TpType tpType = null;
		try {
			String[][] types = LoadConfig.getInstance().getTechnicsType();

			String typeFolderName = null;// propertiesUtil.getProperty(type);
			for (int i = 0; i < types[0].length; i++) {
				if (types[0][i].equals(type)) {
					typeFolderName = types[1][i];
					break;
				}
			}
			String folderName = propertiesUtil.getProperty("process-template-save-folder");
			String containerName = propertiesUtil.getProperty("process-template-save-container");
			WTContainer container = WTContainerUtil.getContainerByName(containerName);
			String folderPath = "/Default/" + folderName + "/" + typeFolderName;
			GLLogger.debug(CLASSNAME, "--folderPath-" + folderPath);
			Folder folder = StandardImportService.getFolder("/Default/" + folderName + "/" + typeFolderName, container);
			if (folder != null) {
				tpType = new TpType(folder.getName());
				QueryResult queryResult = FolderHelper.service.findSubFolders(folder);
				if (queryResult.hasMoreElements()) {
					MPMResourceHelper.getTemplateFolder(queryResult, tpType);
				} else {

					List<ProcessTemplate> templateList = new ArrayList<ProcessTemplate>();
					QueryResult fileResult = FolderHelper.service.findFolderContents(folder, WTDocument.class);
					while (fileResult.hasMoreElements()) {
						WTDocument document = (WTDocument) fileResult.nextElement();
						ProcessTemplate processTemplate = new ProcessTemplate();
						processTemplate.setOid(Util.getStringOid(document));
						processTemplate.setNumber(document.getNumber());
						processTemplate.setName(document.getName());
						templateList.add(processTemplate);
					}
					tpType.setProcessTemplates(templateList);

				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return tpType;
	}

	/**
	 * 获取所有的工序模板类型及旗下的模板
	 *
	 * @return
	 * @author qianlong
	 * @date 2013-3-18
	 */
	public static TpType getStepTemplatesRMI(String type) {
		TpType tpType = null;
		try {
			String[][] types = LoadConfig.getInstance().getTechnicsType();

			String typeFolderName = null;// propertiesUtil.getProperty(type);
			for (int i = 0; i < types[0].length; i++) {
				if (types[0][i].equals(type)) {
					typeFolderName = types[1][i];
					break;
				}
			}
			String folderName = propertiesUtil.getProperty("step-template-save-folder");
			String containerName = propertiesUtil.getProperty("step-template-save-container");
			WTContainer container = WTContainerUtil.getContainerByName(containerName);
			String folderPath = "/Default/" + folderName + "/" + typeFolderName;
			GLLogger.debug(CLASSNAME, "--folderPath-" + folderPath);
			Folder folder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
			if (folder != null) {
				tpType = new TpType(folder.getName());
				QueryResult queryResult = FolderHelper.service.findSubFolders(folder);
				if (queryResult.hasMoreElements()) {
					MPMResourceHelper.getTemplateFolder(queryResult, tpType);
				} else {
					List<ProcessTemplate> templateList = new ArrayList<ProcessTemplate>();
					QueryResult fileResult = FolderHelper.service.findFolderContents(folder, WTDocument.class);
					while (fileResult.hasMoreElements()) {
						WTDocument document = (WTDocument) fileResult.nextElement();
						ProcessTemplate processTemplate = new ProcessTemplate();
						processTemplate.setOid(Util.getStringOid(document));
						processTemplate.setNumber(document.getNumber());
						processTemplate.setName(document.getName());
						templateList.add(processTemplate);
					}
					tpType.setProcessTemplates(templateList);
				}

			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return tpType;
	}

	public static List<TpType> getAllStepTemplatesRMI() {
		List<TpType> tpTypes = new ArrayList<TpType>();
		try {
			String[][] types = LoadConfig.getInstance().getTechnicsType();
			String[] typeStr = types[1];
			for (String typeFolderName : typeStr) {
				TpType tpType = null;
				String folderName = propertiesUtil.getProperty("step-template-save-folder");
				String containerName = propertiesUtil.getProperty("step-template-save-container");
				WTContainer container = WTContainerUtil.getContainerByName(containerName);
				String folderPath = "/Default/" + folderName + "/" + typeFolderName;
				Folder folder = null;
				try {
					folder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
				} catch (WTException e) {
					folder = null;
				}
				if (folder != null) {
					tpType = new TpType(folder.getName());
					QueryResult queryResult = FolderHelper.service.findSubFolders(folder);
					if (queryResult.hasMoreElements()) {
						MPMResourceHelper.getTemplateFolder(queryResult, tpType);
					} else {
						List<ProcessTemplate> templateList = new ArrayList<ProcessTemplate>();
						QueryResult fileResult = FolderHelper.service.findFolderContents(folder, WTDocument.class);
						while (fileResult.hasMoreElements()) {
							WTDocument document = (WTDocument) fileResult.nextElement();
							ProcessTemplate processTemplate = new ProcessTemplate();
							processTemplate.setOid(Util.getStringOid(document));
							processTemplate.setNumber(document.getNumber());
							processTemplate.setName(document.getName());
							templateList.add(processTemplate);
						}
						tpType.setProcessTemplates(templateList);
					}

				}
				if(tpType != null){
					tpTypes.add(tpType);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return tpTypes;
	}

	/**
	 * 通过文件下载模板
	 *
	 * @return
	 * @author ylshao
	 * @date 2012-11-1
	 */
	public static Vector<Object> downloadProcessTemplateRMI(String oid) {
		GLLogger.debug(CLASSNAME, "----oid-" + oid);
		Vector<Object> vector = new Vector<Object>();
		try {
			WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, oid);
			ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
			GLLogger.debug(CLASSNAME, "----ApplicationData-" + data);
			if (data != null) {
				byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
				vector.add(document.getName());
				vector.add(bytes);
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "----vector-" + vector);
		return vector;
	}

	/**
	 * 上传工艺模板
	 *
	 * @param oid
	 * @return
	 * @author qianlong
	 * @date 2012-11-15
	 */
	public static String uploadProcessTemplateRMI(List<String> locationList, String fileName, byte[] bytes) {
		GLLogger.debug(CLASSNAME, "----location-" + locationList + "--fileName--" + fileName);
		String message = "";
		Transaction transaction = new Transaction();
		try {
			if (null == fileName || "".equals(fileName.trim())) {
				return message = "上传的模板名称是空的!";
			}
			if (null == bytes) {
				return message = "上传的模板内容是空的!";
			}
			String folderName = propertiesUtil.getProperty("process-template-save-folder");
			String containerName = propertiesUtil.getProperty("process-template-save-container");
			String documentType = propertiesUtil.getProperty("process-template-save-document-type");
			String location = MPMResourceHelper.getFolderPath(locationList, folderName);

			GLLogger.debug(CLASSNAME, "----location-" + location);
			GLLogger.debug(CLASSNAME, "----containerName-" + containerName);
			GLLogger.debug(CLASSNAME, "----documentType-" + documentType);
			WTContainer container = WTContainerUtil.getContainerByName(containerName);
			Folder folder = null;

			try {
				folder = FolderUtil.getFolder(location, WTContainerRef.newWTContainerRef(container));
				// folder = FolderHelper.service.getFolder(location,
				// WTContainerRef.newWTContainerRef(container));
			} catch (Exception e) {
				e.printStackTrace();
				message = "类型已经不存在,请重新选取类型上传模板!";
				return message;
			}

			List<WTDocument> list = WTDocumentUtil.getDocumnetByNameAndType(fileName, documentType);
			int tag = 0;
			for (WTDocument document : list) {
				if (document.getLocation().equals(location)) {
					if(document.getModifier().getPrincipal().getName().equals(SessionHelper.getPrincipal().getName())){
						PersistenceHelper.manager.delete(document);
					}else{
						tag = 1;
					}
					break;
				}
			}
			transaction.start();
			if (tag == 1) {
				message = "模板名称已被其他用户占用，请修改模板名称再重新上传!";
			} else {
				WTDocument doc = WTDocumentUtil.createDocument(fileName, container, folder, documentType);
				WTDocumentUtil.setPrimaryForDocument(doc, fileName + ".zip", bytes);
			}
			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			message = "上传失败";
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		GLLogger.debug(CLASSNAME, "----message-" + message);
		return message;
	}

	/**
	 * 上传工序模板
	 *
	 * @param oid
	 * @return
	 * @author qianlong
	 * @date 2012-11-15
	 */
	public static String uploadStepTemplateRMI(List<String> locationList, String fileName, byte[] bytes) {
		GLLogger.debug(CLASSNAME, "----location-" + locationList + "--fileName--" + fileName);
		String message = "";
		Transaction transaction = new Transaction();
		try {
			if (null == fileName || fileName.trim().equals("")) {
				return message = "上传的模板名称是空的!";
			}
			if (null == bytes) {
				return message = "上传的模板内容是空的!";
			}
			String folderName = propertiesUtil.getProperty("step-template-save-folder");
			String containerName = propertiesUtil.getProperty("step-template-save-container");
			String documentType = propertiesUtil.getProperty("step-template-save-document-type");
			String location = MPMResourceHelper.getFolderPath(locationList, folderName);

			GLLogger.debug(CLASSNAME, "----location-" + location);
			GLLogger.debug(CLASSNAME, "----containerName-" + containerName);
			GLLogger.debug(CLASSNAME, "----documentType-" + documentType);
			WTContainer container = WTContainerUtil.getContainerByName(containerName);
			Folder folder = null;
			try {
				folder = FolderUtil.getFolder(location, WTContainerRef.newWTContainerRef(container));
				// folder = FolderHelper.service.getFolder(location,
				// WTContainerRef.newWTContainerRef(container));
			} catch (Exception e) {
				e.printStackTrace();
				message = "类型已经不存在,请重新选取类型上传模板!";
				return message;
			}

			List<WTDocument> list = WTDocumentUtil.getDocumnetByNameAndType(fileName, documentType);
			int tag = 0;
			for (WTDocument document : list) {
				if (document.getLocation().equals(location)) {
					if(document.getModifier().getPrincipal().getName().equals(SessionHelper.getPrincipal().getName())){
						PersistenceHelper.manager.delete(document);
					}else{
						tag = 1;
					}
					break;
				}
			}
			transaction.start();
			if (tag == 1) {
				message = "模板名称已被其他用户占用，请修改模板名称再重新上传!";
			} else {
				WTDocument doc = WTDocumentUtil.createDocument(fileName, container, folder, documentType);
				WTDocumentUtil.setPrimaryForDocument(doc, fileName + ".zip", bytes);
				HashMap<String, WTDocument> data1 = new HashMap<String, WTDocument>();
				boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
				SubmitApprovalProcessor.initiateWfProcess("标准工序入库流程", data1, container, doc);
				SessionServerHelper.manager.setAccessEnforced(flag);

			}
			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			message = "上传失败";
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		GLLogger.debug(CLASSNAME, "----message-" + message);
		return message;
	}

	/**
	 * 工艺常用语入库
	 *
	 * @param map
	 * @return success 添加成功，其他情况直接返回描述信息
	 * @author fly
	 * @date 2012-11-13
	 */
	public static List<Object> addToCsLibraryRMI(String skillOid, List<String> comStringList) {
		GLLogger.debug(CLASSNAME, "-skilloid-" + skillOid);
		List<Object> list = new ArrayList<Object>();
		String message = "success";
		List<String> csList = new ArrayList<String>();
		String objectType = TypeNameConstants.GYCYY;
		String folderPath = typeToFolderPropertiesUtil.getProperty(objectType);
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			MPMSkill skill = (MPMSkill) Util.getObjectByOid(MPMSkill.class, skillOid);
			if (skill == null) {
				message = "所选择的工种不存在,请刷新工种之后重新选择！";
			} else {
				WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
				for (String cs : comStringList) {
					MPMTooling commonString = MPMResourceUtil.getMPMToolingByName(cs, objectType);
					if (commonString == null) {
						commonString = MPMResourceUtil.createTooling("", cs, container, folderPath, objectType, "");
						MPMResourceUtil.createWTPartUsageLink(skill, (WTPartMaster) commonString.getMaster());
					} else {
						WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(skill, (WTPartMaster) commonString.getMaster());
						if (link != null) {
							message = "该工种下面的'" + cs + "'常用语已经存在！";
						} else {
							MPMResourceUtil.createWTPartUsageLink(skill, (WTPartMaster) commonString.getMaster());
						}
					}
				}
			}
			if ("success".equals(message)) {
				List<WTPart> children = WTPartUtil.getChildPart(skill);
				// 获取所有的工艺术语
				for (WTPart child : children) {
					TypeIdentifier typeIdentifier = TypedUtility.getTypeIdentifier(child);
					String typeName = typeIdentifier.getTypename();
					if (typeName.contains(objectType)) {
						csList.add(child.getName());
					}
				}
			}
			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			message = "上传失败";
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		list.add(message);
		list.add(csList);
		return list;
	}

	/**
	 * @Author caolei
	 * @Date 2015/6/3
	 * @Check caolei
	 * @Description 常用语入库
	 */
	public static List<Object> justAddToCsLibraryRMI(String skillOid, List<String> comStringList) {
		List<Object> list = new ArrayList<Object>();
		String message = "success";
		List<CsType> csList = new ArrayList<CsType>();
		String objectType = TypeNameConstants.GYCYY;
		// 得到工艺常用语路径
		String folderPath = typeToFolderPropertiesUtil.getProperty(objectType);

		Transaction transaction = new Transaction();
		try {
			transaction.start();
			// 得到容器
			WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
			for (String cooms : comStringList) {
				// 将常用语名称和类型名称分别取出来
				String[] s = cooms.split("\\|");
				String cs = s[0];
				String typeString = s[1];
				StringBuffer buff = new StringBuffer(folderPath);
				buff.append("/");
				buff.append(typeString);
				folderPath = buff.toString();

				getFolder(folderPath, WTContainerRef.newWTContainerRef(container));

				// 得到常用语对象
				MPMTooling commonString = MPMResourceUtil.getMPMToolingByName(cs, objectType);
				if (commonString == null) {
					commonString = MPMResourceUtil.createTooling("", cs, container, folderPath, objectType, "");
				} else {
					message = cs + "'常用语已经存在！";
				}
			}
			if ("success".equals(message)) {
				csList = getAllCsType();
			}
			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			message = "上传失败";
			e.printStackTrace();

		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		list.add(message);
		list.add(csList);
		return list;
	}

	/**
	 * @Author caolei
	 * @Date 2015/6/3
	 * @Check caolei
	 * @Description Windchill中创建常用语类型文件夹
	 */

	public static Folder getFolder(String path, WTContainerRef wtContainerRef) throws WTException {
		Folder folder = null;
		StringTokenizer tokenizer = new StringTokenizer(path, "/");
		String subPath = "";
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			subPath = subPath + "/" + token;
			if (subPath != null && !subPath.equalsIgnoreCase("")) {
				try {
					folder = FolderHelper.service.getFolder(subPath, WTContainerRef.newWTContainerRef(wtContainerRef));
				} catch (FolderNotFoundException e) {
					boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
					folder = FolderHelper.service.createSubFolder(subPath, WTContainerRef.newWTContainerRef(wtContainerRef));
					SessionServerHelper.manager.setAccessEnforced(flag);
				}
			}
		}
		return folder;
	}

	/**
	 * @Author caolei
	 * @Date 2015/6/3
	 * @Check caolei
	 * @Description 从库中获取所有的常用语
	 */

	public static List<CsType> getAllCsType() {
		List<CsType> subTypeList = new ArrayList<CsType>();
		try {
			WTContainer container = WTContainerUtil.getContainerByName("工艺资源库");
			String objectType = TypeNameConstants.GYCYY;
			String folderPath = typeToFolderPropertiesUtil.getProperty(objectType);
			Folder folder = FolderHelper.service.getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
			if (folder != null) {
				QueryResult queryResult = FolderHelper.service.findSubFolders(folder);
				if (queryResult.hasMoreElements()) {
					MPMResourceHelper.getAllTemplateFolder(queryResult, subTypeList);
				}
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return subTypeList;
	}

	/**
	 * 获取所有的工序名称
	 *
	 * @return
	 * @author fly
	 * @date 2012-11-13
	 */
	public static HashMap<String, String> getProcessStepNamesRMI() {
		// HashMap<String, String> map = new HashMap<String, String>();
		// try {
		// QueryResult qr =
		// MPMResourceUtil.getMPMToolingByType(TypeNameConstants.GXMC);
		// while (qr.hasMoreElements()) {
		// MPMTooling tooling = (MPMTooling) qr.nextElement();
		// map.put(tooling.getName(),
		// WorkproceduceUtil.convertStr(tooling.getName()));
		// }
		// } catch (QueryException e) {
		// e.printStackTrace();
		// } catch (WTException e) {
		// e.printStackTrace();
		// } catch (RemoteException e) {
		//
		// e.printStackTrace();
		// }
		// return map;

		return get812ProcessStepNamesRMI();
	}

	public static HashMap<String, String> get812ProcessStepNamesRMI() {
		if(ProcessCache.et812ProcessStepNamesRMI!=null){
			return ProcessCache.et812ProcessStepNamesRMI;
		}
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			QueryResult qr = MPMResourceUtil.getMPMToolingByType(TypeNameConstants.GXMC);
			while (qr.hasMoreElements()) {
				MPMTooling tooling = (MPMTooling) qr.nextElement();
				map.put(tooling.getNumber(), tooling.getName());
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {

			e.printStackTrace();
		}
		ProcessCache.et812ProcessStepNamesRMI = map;
		return map;
	}

	/**
	 * 获取当前用户的信息
	 *
	 * @param @return
	 * @return List<String>
	 * @throws
	 * @author fly
	 * @Title: getCurrentUserInfoRMI
	 * @Description:
	 */
	public static List<String> getCurrentUserInfoRMI() {
		WTUser user = null;
		try {
			user = (WTUser) SessionHelper.manager.getPrincipal();
		} catch (WTException e) {

			e.printStackTrace();
		}
		ArrayList<String> userInfo = new ArrayList<String>();
		userInfo.add(user.getName());
		userInfo.add(user.getPersistInfo().getObjectIdentifier().toString());
		userInfo.add(user.getFullName());
		return userInfo;
	}

	/**
	 * 获取当前会话的用户信息 20170929 jyx
	 *
	 * @return CmUser
	 */
	public static CmUser getCurrentUser() {
		CmUser cmUser = new CmUser();
		try {
			WTUser wtUser = (WTUser) SessionHelper.getPrincipal();
			cmUser.setName(wtUser.getName());
			cmUser.setFullName(wtUser.getFullName());
			cmUser.setOid(PersistenceHelper.getObjectIdentifier(wtUser).getId());
			cmUser.setEmail(wtUser.getEMail());
			cmUser.setApproveModifyGroup(false);
			getCurrentUserDept(wtUser, cmUser);
		} catch (WTException e) {
			logger.error(e);
		}
		return cmUser;
	}

	/**
	 * 获取用户部门 20170929 jyx
	 *
	 * @return CmUser
	 */
	public static void getCurrentUserDept(WTUser wtUser, CmUser cmUser) throws WTException {
		boolean isDept = false;
		boolean isApproveModifyGroup = false;
		QueryResult qResult = MPMUtil.queryGroup();
		while (qResult.hasMoreElements()) {
			WTGroup wtGroup = (WTGroup) qResult.nextElement();
			if (wtGroup.isMember(wtUser)) {
				if (wtGroup.getName().startsWith("部门_")) {
					String department = wtGroup.getName();
					if (department.indexOf("_") > -1) {
						department = department.split("_")[1];
						cmUser.setDepartment(department);
						isDept = true;
					}
				} else if (wtGroup.getName().equals("定版工艺修改组")) {
					cmUser.setApproveModifyGroup(true);
					isApproveModifyGroup = true;
				}
			}

			if (isDept && isApproveModifyGroup)
				break;
		}
	}

	/**
	 * 判断用户是否在指定的组里面 20170929 jyx
	 *
	 * @return CmUser
	 */
	public static boolean isInGroup(CmUser user, String groupName) throws NumberFormatException, WTException {
		WTUser wtUser = getUser(user.getName());
		WTGroup wtGroup = null;
		QueryResult qResult = MPMUtil.queryGroup();
		while (qResult.hasMoreElements()) {
			wtGroup = (WTGroup) qResult.nextElement();
			if (wtGroup.getName().equals(groupName) && wtGroup.isMember(wtUser)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据名称获取用户 20170929 jyx
	 *
	 * @return CmUser
	 */
	public static WTUser getUser(String name) throws WTException {
		QuerySpec qs = new QuerySpec(WTUser.class);
		int index[] = { 0 };
		SearchCondition scCondition = new SearchCondition(WTUser.class, _WTPrincipal.NAME, SearchCondition.EQUAL, name);
		qs.appendWhere(scCondition, index);
		QueryResult result = PersistenceHelper.manager.find((StatementSpec) qs);
		if (result.hasMoreElements()) {
			return (WTUser) result.nextElement();
		}

		return null;
	}

	/**
	 * 判断工艺文件是否已经提交签审
	 *
	 * @param docNumber
	 * @return
	 * @author LongXiuChuan
	 */
	public static boolean isSubmited(String docNumber) {
		boolean flag = false;
		try {
			WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(docNumber);
			QueryResult qr = WfEngineHelper.service.getAssociatedProcesses(doc, WfState.OPEN_RUNNING, doc.getContainerReference());
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WfProcess) {
					WfProcess wf = (WfProcess) obj;
					if (wf.getName().contains(WorkflowConstants.SANJIGONGYIQIANSHENLIUCHENG) || wf.getName().contains(WorkflowConstants.WUJIGONGYIQIANSHENLIUCHENG)
							|| wf.getName().contains(WorkflowConstants.SANJIBAOBIAOLEIGONGYIQIANSHENLIUCHENG) || wf.getName().contains(WorkflowConstants.WUJIBAOBIAOLEIGONGYIQIANSHENLIUCHENG)) {
						if (WfState.OPEN_RUNNING.equals(wf.getState())) {
							flag = true;
						}
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 判断技术协议件是否已经提交签审
	 *
	 * @param docNumber
	 * @return
	 * @author Mchen
	 */
	public static Boolean isJsxySubmit(String docNumber) {
		boolean flag = false;
		try {
			WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(docNumber);
			QueryResult qr = WfEngineHelper.service.getAssociatedProcesses(doc, WfState.OPEN_RUNNING, doc.getContainerReference());
			while (qr.hasMoreElements()) {
				Object obj = qr.nextElement();
				if (obj instanceof WfProcess) {
					WfProcess wf = (WfProcess) obj;
					if (wf.getName().contains(WorkflowConstants.WAIXIEJISHUXIEYIQIANSHENLIUCHENG)) {
						if (WfState.OPEN_RUNNING.equals(wf.getState())) {
							flag = true;
							break;
						}
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return flag;

	}

	/**
	 * 判断是否更改和正在工作
	 *
	 * @param docNumber
	 * @return
	 * @author Mchen
	 */
	public static Boolean isGenGaiAndIsWorking(String docNumber) {
		boolean flag = false;
		try {
			WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(docNumber);
			String version = doc.getVersionInfo().getIdentifier().getValue();
			String state = doc.getLifeCycleState().getLocalizedMessage(Locale.CHINA);
			if ("space".equals(version) && "正在工作".equals(state)) {
				flag = true;
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return flag;

	}

	/**
	 * 启动提交工艺审核流程
	 *
	 * @param topPartOid
	 * @param partOid
	 * @param technicsName
	 * @param unite
	 * @param technicsZip
	 * @param xmlVersion
	 * @return
	 * @author qianlong
	 * @date 2013-6-14
	 */
	public static HashMap<String, String> submitSignedRMI(Map<String, String> map, byte[] technicsZip) {
		String topPartOid = map.get("topPartOid");
		String partOid = map.get("partOid");
		String taskOid = map.get("taskOid");
		String technicsName = map.get("technicsName");
		String unite = map.get("unite");
		String xmlVersion = map.get("xmlVersion");
		String technicsCategory = map.get("technicsCategory");
		String technicsType = map.get("technicsType");
		String technicsNumber = map.get("technicsNumber");
		String submitFlag = map.get("flag");
		String pplanNumber = map.get("pplanNumber");
		String docOid = map.get("docOid");
		String isSanJiGengGai = map.get("isSanJiGengGai");
		String startType = map.get("startType");
		if (docOid == null) {
			docOid = "";
		}
		String isReport = map.get("isReport");// true标识报表类工艺文件提交签审，false标识一般类工艺文件签审
		// GLLogger.debug(CLASSNAME, "-map-" + map + "--technicsZip--" +
		// technicsZip);
		boolean flag = false;
		HashMap<String, String> returnMap = new HashMap<String, String>();
		try {
			// returnMap = uploadTechnicsRMI(partOid, technicsNumber,
			// technicsName, technicsZip, "",technicsType, xmlVersion,
			// "签审",pplanNumber);
			GLLogger.debug(CLASSNAME, "success of returnMap is:" + returnMap.get(SUCCESS));
			returnMap.put(LIFECYCLE, "正在工作");
			returnMap.put(SUCCESS, SUCCESS);
			WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
			// if (returnMap.get(SUCCESS).equals(SUCCESS)) {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
			Map<String, String> variablesMap = new HashMap<String, String>();
			variablesMap.put("topPartOid", topPartOid);
			variablesMap.put("partOid", partOid);
			variablesMap.put("technicsOid", docOid);
			variablesMap.put("processType", technicsCategory);
			variablesMap.put("taskOid", taskOid);

			setProcessDocNumToProcessTaskItem(taskOid, technicsNumber);
			// variablesMap.put("unite", unite);
			// 正常工艺
			if (Constants.normalProcess.equals(technicsCategory) || Constants.sopProcess.equals(technicsCategory)) {
				if (unite.equals("common")) {
					String workFlowTemplate = WorkflowConstants.SANJIGONGYIQIANSHENLIUCHENG;
					if ("3".equals(submitFlag)) {
						/*
						 * if("true".equals(isSanJiGengGai)){ workFlowTemplate =
						 * WorkflowConstants.SANJIGONGYIGENGGAILIUCHENG; }else{
						 */
						if ("false".equals(isReport)) {
							workFlowTemplate = WorkflowConstants.SANJIGONGYIQIANSHENLIUCHENG;
						} else if ("true".equals(isReport)) {
							workFlowTemplate = WorkflowConstants.SANJIBAOBIAOLEIGONGYIQIANSHENLIUCHENG;
						}
						// }

					} else if ("5".equals(submitFlag)) {
						workFlowTemplate = WorkflowConstants.WUJIGONGYIQIANSHENLIUCHENG;
						if ("SOP".equals(startType)) {
							workFlowTemplate = SopConstants.SOP_WORKFLOW_SOPPROCESS;
						} else {
							if ("false".equals(isReport)) {
								workFlowTemplate = WorkflowConstants.WUJIGONGYIQIANSHENLIUCHENG;
							} else if ("true".equals(isReport)) {
								workFlowTemplate = WorkflowConstants.WUJIBAOBIAOLEIGONGYIQIANSHENLIUCHENG;
							}
						}
					}
					GLLogger.debug(CLASSNAME, "start " + workFlowTemplate + "......");
					flag = WorkflowUtil.startProcess(doc, workFlowTemplate, technicsNumber, variablesMap);
				} else {
					GLLogger.debug(CLASSNAME, "start 工艺合编签审流程...");
					flag = WorkflowUtil.startProcess(doc, WorkflowConstants.SANJIGONGYIQIANSHENLIUCHENG, technicsNumber, variablesMap);
				}
			}
			// 返工工艺
			else if (Constants.reworkProcess.equals(technicsCategory)) {
				variablesMap.put("technicName", technicsName);
				variablesMap.put("taskOid", taskOid);
				if (unite.equals("common")) {
					GLLogger.debug(CLASSNAME, "start 工艺返工流程审核流程...");
					flag = WorkflowUtil.startProcess(part, "工艺返工流程审核流程", technicsNumber, variablesMap);
				} else {
					GLLogger.debug(CLASSNAME, "start 工艺合编签审流程...");
					flag = WorkflowUtil.startProcess(part, "工艺审核流程", technicsNumber, variablesMap);
				}
			}
			// 临时工艺
			else if (Constants.tempProcess.equals(technicsCategory)) {
				variablesMap.put("technicName", technicsName);
				variablesMap.put("tempTaskOid", taskOid);
				if (unite.equals("common")) {
					GLLogger.debug(CLASSNAME, "start 工艺返工流程审核流程...");
					flag = WorkflowUtil.startProcess(part, "临时工艺审核流程", technicsNumber, variablesMap);
				} else {
					GLLogger.debug(CLASSNAME, "start 工艺合编签审流程...");
					flag = WorkflowUtil.startProcess(part, "工艺审核流程", technicsNumber, variablesMap);
				}
			}
			// }

			if (!flag) {
				returnMap.put(SUCCESS, FAILED);
				returnMap.put(ERRORMESSAGE, "启动审核流程 出错！");
			}
		} catch (WTException e) {
			returnMap.put(SUCCESS, FAILED);
			returnMap.put(ERRORMESSAGE, "启动审核流程 出错！");
			e.printStackTrace();
		}
		return returnMap;
	}

	private static void setProcessDocNumToProcessTaskItem(String taskOid, String technicsNumber) throws WTRuntimeException, WTException {
		if (taskOid == null || "".equals(taskOid)) {
			return;
		}
		IBAHolder op = (IBAHolder) ReferenceFactory.getObjectbyOid("OR:ext.casc.process.ProcessTaskItem:" + taskOid);
		try {
			IBAUtility iba = new IBAUtility((IBAHolder) op);
			iba.setIBAValue("PROCESSDOCNUM", technicsNumber);
			op = iba.updateAttributeContainer(op);
			iba.updateIBAHolder(op);
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
	}

	/**
	 * 材料定额提交签审
	 *
	 * @param inputMap
	 * @param technicsZip
	 * @return
	 */
	public static HashMap<String, String> submitCMatSigned(List<CMatBean> list, String technicsNumber) {
		GLLogger.debug("list= " + list);
		HashMap<String, String> returnMap = new HashMap<String, String>();
		try {
			boolean flag = false;
			WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);

			// 通过CMatBean生成EXCEL表，上传为doc的附件，在流程的签审环节需要查看该EXCEL表
			WorkflowHelper.genExcelAndAddToDocument(doc, list);

			Map<String, String> variablesMap = new HashMap<String, String>();
			returnMap.put(LIFECYCLE, "正在工作");
			returnMap.put(SUCCESS, SUCCESS);

			flag = WorkflowUtil.startProcess(doc, WorkflowConstants.CAILIAODINGELIUCHENG, technicsNumber, variablesMap);
			if (!flag) {
				returnMap.put(SUCCESS, FAILED);
				returnMap.put(ERRORMESSAGE, "启动审核流程 出错！");
			}
		} catch (Exception e) {
			returnMap.put(SUCCESS, FAILED);
			returnMap.put(ERRORMESSAGE, "启动审核流程 出错！");
			e.printStackTrace();
		}
		return returnMap;
	}

	/**
	 * 材料定额提交签审
	 *
	 * @param inputMap
	 * @param technicsZip
	 * @return
	 */
	public static HashMap<String, String> submitBatchCMatSigned(Map<String, List<CMatBean>> dataMap, Map<String, String> taskMap, String workitemOid, String partNumber) {
		GLLogger.debug("dataMap= " + dataMap);
		HashMap<String, String> returnMap = new HashMap<String, String>();
		try {
			boolean flag = false;
			ProcessEnvelope pbo = WorkflowHelper.createCLDEProcessEnvelope(dataMap, workitemOid, partNumber);
			if (pbo != null) {
				StringBuffer techNumBuffer = new StringBuffer();
				StringBuffer taskOidBuffer = new StringBuffer();
				ProcessTask processTask;
				IBAUtility ibaUtility;
				for (Map.Entry<String, String> entry : taskMap.entrySet()) {
					String technicsNumber = entry.getKey();
					String taskOid = entry.getValue();
					processTask = ProcessUtil.getProcessTask(Long.valueOf(taskOid));
					if (processTask != null) {
						ibaUtility = new IBAUtility(processTask);
						ibaUtility.setIBAValue("relatedTech", technicsNumber);
						processTask = (ProcessTask) ibaUtility.updateAttributeContainer(processTask);
						ibaUtility.updateIBAHolder(processTask);
					}
					if (techNumBuffer.toString().isEmpty()) {
						techNumBuffer.append(technicsNumber);
					} else {
						techNumBuffer.append(",").append(technicsNumber);
					}
					if (taskOidBuffer.toString().isEmpty()) {
						taskOidBuffer.append(taskOid);
					} else {
						taskOidBuffer.append(",").append(taskOid);
					}
				}
				Map<String, String> variablesMap = new HashMap<String, String>();
				variablesMap.put("taskItemOid", taskOidBuffer.toString());
				variablesMap.put("techNum", techNumBuffer.toString());
				returnMap.put(LIFECYCLE, "正在工作");
				returnMap.put(SUCCESS, SUCCESS);

				flag = WorkflowUtil.startProcess(pbo, WorkflowConstants.CAILIAODINGELIUCHENG, pbo.getName(), variablesMap);
				if (!flag) {
					returnMap.put(SUCCESS, FAILED);
					returnMap.put(ERRORMESSAGE, "启动审核流程 出错！");
				}
			} else {
				returnMap.put(SUCCESS, FAILED);
				returnMap.put(ERRORMESSAGE, "启动审核流程 出错！");
			}
		} catch (Exception e) {
			returnMap.put(SUCCESS, FAILED);
			returnMap.put(ERRORMESSAGE, "启动审核流程 出错！");
			e.printStackTrace();
		}
		return returnMap;
	}

	/**
	 * 启动工艺路线确认流程
	 *
	 * @param topPartOid
	 * @param partOid
	 * @param technicsName
	 * @param technicsZip
	 * @param xmlVersion
	 * @return
	 * @author qianlong
	 * @date 2013-6-14
	 */
	public static HashMap<String, String> routeConfirmRMI(String topPartOid, String partOid, String technicsName, String technicsType, byte[] technicsZip, String xmlVersion, String pplanNumber) {
		GLLogger.debug(CLASSNAME, "-topPartOid:" + topPartOid + "-partOid:" + partOid + "-technicsName:" + technicsName);
		boolean flag = false;
		HashMap<String, String> returnMap = new HashMap<String, String>();
		try {
			Map<String, String> hashmap = new HashMap<String, String>();
			hashmap.put("oid", partOid);
			hashmap.put("technicsName", technicsName);
			hashmap.put("technicsType", technicsType);
			hashmap.put("pplanNumber", pplanNumber);
			returnMap = uploadTechnicsRMI(technicsZip, hashmap, xmlVersion, "工艺路线确认");
			GLLogger.debug(CLASSNAME, "success flage of returnMap is :" + returnMap.get(SUCCESS));
			if (returnMap.get(SUCCESS).equals(SUCCESS)) {
				WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
				Map<String, String> map = new HashMap<String, String>();
				map.put("topPartOid", topPartOid);
				flag = WorkflowUtil.startProcess(part, "工艺路线确认流程", part.getNumber() + "---" + part.getName(), map);
				if (!flag) {
					// 启动流程出错
					returnMap.put(SUCCESS, FAILED);
					returnMap.put(ERRORMESSAGE, "启动工艺路线确认流程 出错");
				}
			} else {
				// uploadTechnics error,return error message
				return returnMap;
			}

		} catch (WTException e) {
			returnMap.put(SUCCESS, FAILED);
			returnMap.put(ERRORMESSAGE, "启动工艺路线确认流程 出错");
			e.printStackTrace();
		}
		return returnMap;
	}

	/**
	 * 启动工艺路线确认流程
	 *
	 * @param topPartOid
	 * @param partOid
	 * @param technicsName
	 * @param technicsZip
	 * @param xmlVersion
	 * @return
	 * @author qianlong
	 * @date 2013-6-14
	 */
	public static HashMap<String, String> startTechnicsUniteRMI(String topPartOid, String partOid, String technicsName, byte[] technicsZip, String technicsType, String xmlVersion, String pplanNumber) {
		GLLogger.debug(CLASSNAME, "-topPartOid:" + topPartOid + "-partOid:" + partOid + "-technicsName:" + technicsName);
		boolean flag = false;
		HashMap<String, String> returnMap = new HashMap<String, String>();
		try {
			Map<String, String> hashmap = new HashMap<String, String>();
			hashmap.put("oid", partOid);
			hashmap.put("technicsName", technicsName);
			hashmap.put("technicsType", technicsType);
			hashmap.put("pplanNumber", pplanNumber);
			returnMap = uploadTechnicsRMI(technicsZip, hashmap, xmlVersion, "工艺合编");
			GLLogger.debug(CLASSNAME, "success of returnMap is:" + returnMap.get(SUCCESS));
			if (returnMap.get(SUCCESS).equals(SUCCESS)) {
				WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
				Map<String, String> map = new HashMap<String, String>();
				map.put("topPartOid", topPartOid);
				flag = WorkflowUtil.startProcess(part, "工艺合编流程", part.getNumber() + "---" + part.getName(), map);
				if (!flag) {
					// 启动流程出错
					returnMap.put(SUCCESS, FAILED);
					returnMap.put(ERRORMESSAGE, "启动工艺路线确认流程 出错");
				}
			} else {
				return returnMap;
			}

		} catch (WTException e) {
			returnMap.put(SUCCESS, FAILED);
			returnMap.put(ERRORMESSAGE, "启动工艺路线确认流程 出错");
			e.printStackTrace();
		}
		return returnMap;
	}

	/**
	 * 获取所有工艺组名称
	 *
	 * @param partOid
	 * @return
	 * @author qianlong
	 * @date 2012-11-28
	 */
	public static List<String> getAlltechnicsGroupNamesRMI(String partOid) {
		GLLogger.debug(CLASSNAME, "-partOid:" + partOid);
		List<String> list = new ArrayList<String>();
		String[] roleNameArray = propertiesUtil.getProperty("process-role-name").split(";");
		for (String roleName : roleNameArray) {
			Role role = Role.toRole(roleName);
			list.add(role.getDisplay(Locale.CHINA));
		}
		GLLogger.debug(CLASSNAME, "list----" + list);
		return list;
	}

	public static String getUsertechnicsGroupNameRMI() {
		String name = "";
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			name = getGroupNameByUser(user);
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		System.out.println("getUsertechnicsGroupNameRMI---------name---" + name);
		return name;
	}

	public static String getGroupNameByUser(WTUser user){
		String name = "";
		try {
			String groupName = ProcessUtil.getUserGroupNameRMI(user);
			System.out.println("getUsertechnicsGroupNameRMI---------groupName---" + groupName);
			if (groupName != null) {
				if (groupName.contains("一车间") || groupName.contains("一分厂")) {
					name = "1";
				} else if (groupName.contains("2热表")) {
					name = "2热表";
				} else if (groupName.contains("非金属") ) {
					name = "2非金属";
				} else if (groupName.contains("二车间") || groupName.contains("二分厂")) {
					name = "2";
				} else if (groupName.contains("三车间") || groupName.contains("三分厂")) {
					name = "3";
				} else if (groupName.contains("四车间") || groupName.contains("四分厂")) {
					name = "4";
				} else if (groupName.contains("五车间") || groupName.contains("五分厂")) {
					name = "5";
				} else if (groupName.contains("六车间") || groupName.contains("六分厂")) {
					name = "6";
				} else if (groupName.contains("七车间") || groupName.contains("七分厂")) {
					name = "7";
				} else if (groupName.contains("八车间") || groupName.contains("八分厂")) {
					name = "8";
				} else if (groupName.contains("九车间") || groupName.contains("九分厂")) {
					name = "9";
				} else if (groupName.contains("十车间") || groupName.contains("十分厂")) {
					name = "10";
				} else {
					name = groupName;
				}
			}
		} catch(WTException e) {
			throw new RuntimeException(e);
		}
		return name;
	}

	/**
	 * 获取当前用户所在工艺组的名称
	 *
	 * @param partOid
	 * @return
	 * @author qianlong
	 * @date 2012-11-28
	 */
	public static String getUsertechnicsGroupNameRMI(String partOid) {
		GLLogger.debug(CLASSNAME, "-partOid:" + partOid);
		String name = "";
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTPrincipal currentPrincipal = SessionHelper.manager.getPrincipal();
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
			GLLogger.debug(CLASSNAME, "part----" + part);
			WTContainer container = part.getContainer();
			ContainerTeam containerTeam = null;
			GLLogger.debug(CLASSNAME, "container----" + container);
			if (container instanceof PDMLinkProduct) {
				PDMLinkProduct product = (PDMLinkProduct) container;
				containerTeam = (ContainerTeam) ContainerTeamHelper.service.getContainerTeam(product);
			} else if (container instanceof WTLibrary) {
				WTLibrary library = (WTLibrary) container;
				containerTeam = (ContainerTeam) ContainerTeamHelper.service.getContainerTeam(library);
			}
			if (containerTeam != null) {
				Vector<Role> vector = containerTeam.getRoles();
				for (Role role : vector) {
					String roleName = role.getDisplay(Locale.CHINA);
					if (roleName.contains("工艺员")) {
						List<WTPrincipalReference> list = containerTeam.getAllPrincipalsForTarget(role);
						for (WTPrincipalReference wtPrincipalReference : list) {
							WTPrincipal principal = (WTPrincipal) wtPrincipalReference.getObject();
							if (principal instanceof WTUser) {
								if (currentPrincipal.equals(principal)) {
									name = roleName.substring(0, roleName.indexOf("工艺员"));
									name = name.replaceAll("分厂", "车间");
									break;
								}
							} else if (principal instanceof WTGroup) {
								WTGroup group = (WTGroup) principal;
								if (group.isMember(currentPrincipal)) {
									name = roleName.substring(0, roleName.indexOf("工艺员"));
									name = name.replaceAll("分厂", "车间");
									break;
								}
							}

						}
					}
				}
			}

		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		GLLogger.debug(CLASSNAME, "name----" + name);
		return name;
	}

	/**
	 * 获取启动PBOM编辑器URL
	 *
	 * @param oid
	 * @return
	 * @author qianlong
	 * @date 2012-11-28
	 */
	public static String getPbomUrlRMI(String oid) {
		GLLogger.debug(CLASSNAME, "-oid-:" + oid);
		URLFactory urlf = null;
		try {
			urlf = new URLFactory();
		} catch (WTException e) {
			e.printStackTrace();
		}
		String urlStr = urlf.getBaseHREF();
		return urlStr + "app/#netmarkets/jsp/ext/glaway/mpm/startPBOME1.jsp?oid=" + oid + "&from=PE";
	}

	/**
	 * 获取windchill主页的url
	 *
	 * @return
	 * @author qianlong
	 * @date 2012-12-18
	 */
	public static String getRouteConfirmUrlRMI() {
		URLFactory urlf = null;
		try {
			urlf = new URLFactory();
		} catch (WTException e) {
			e.printStackTrace();
		}
		String urlStr = urlf.getBaseHREF();
		return urlStr + "app/#ptc1/homepage";
	}

	/**
	 * 获取查看历史URL
	 *
	 * @return
	 * @author ylshao
	 * @date 2012-11-1
	 */
	public static String getHistoryUrlRMI() {
		return "http://www.baidu.com";
	}

	/**
	 * 上载合编工艺压缩包
	 *
	 * @param topPartOid
	 * @param partOid
	 * @param technicsName
	 * @param unite
	 * @param technicsZip
	 * @return
	 * @throws WTRuntimeException
	 * @throws WTException
	 * @throws FileNotFoundException
	 * @throws PropertyVetoException
	 * @throws IOException
	 * @author qianlong
	 * @date 2012-12-5
	 */
	public static HashMap<String, String> uploadUniteTechnicsRMI(String topPartOid, String partOid, String technicsName, String unite, byte[] technicsZip, String xmlVersion)
			throws WTRuntimeException, WTException, FileNotFoundException, PropertyVetoException, IOException {
		GLLogger.debug(CLASSNAME, "-topPartOid:" + topPartOid + "-partOid:" + partOid + "-technicsName:" + technicsName);
		HashMap<String, String> returnMap = new HashMap<String, String>();
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partOid);

		if (part == null) {
			GLLogger.debug("part is null");
			returnMap.put(SUCCESS, FAILED);
			returnMap.put(ERRORMESSAGE, "part is null");
			return returnMap;
		}
		String documentType = propertiesUtil.getProperty("process-zip-document-type");
		List<WTDocument> partTechnicDocList = WTPartUtil.getDescribedDocumentByPart(part, documentType);
		if (partTechnicDocList == null || partTechnicDocList.size() == 0) {
			GLLogger.debug(part.getName() + " 的工艺为空值");
			returnMap.put(SUCCESS, FAILED);
			returnMap.put(ERRORMESSAGE, part.getName() + " 的工艺为空值");
			return returnMap;
		}

		WTDocument partTechnicDoc = partTechnicDocList.get(0);

		WTPrincipal principal = SessionHelper.manager.getPrincipal();
		String currentUserName = principal.getName();
		GLLogger.debug("currentUserName==>" + currentUserName);

		WTDocument originalDoc = null;

		QueryResult qr = WTDocumentHelper.service.getHasDependentWTDocuments(partTechnicDoc, false);
		GLLogger.debug("qr.size==>" + qr.size());
		while (qr.hasMoreElements()) {
			WTDocumentDependencyLink link = (WTDocumentDependencyLink) qr.nextElement();
			WTDocument roleAdoc = (WTDocument) link.getRoleAObject();
			String creatorName = roleAdoc.getCreatorName();
			if (currentUserName.equals(creatorName)) {
				originalDoc = roleAdoc;
				break;
			}
		}

		if (originalDoc == null) {
			String docName = null;
			if (technicsName.lastIndexOf(".") > 0) {
				docName = technicsName.substring(0, technicsName.lastIndexOf("."));
			} else {
				docName = technicsName;
			}
			WTDocument newDoc = WTDocumentUtil.createDocument(docName, part.getContainer(), propertiesUtil.getProperty("process-zip-document-folder"), documentType);// 创建所参考的文件

			newDoc = WTDocumentUtil.setPrimaryForDocument(newDoc, technicsName + ".zip", technicsZip);// 设置新建所参考文件的主物件
			returnMap.put(SUCCESS, SUCCESS);
			returnMap.put(LIFECYCLE, newDoc.getLifeCycleState().getLongDescription(Locale.CHINA));
			returnMap.put(VERSION, newDoc.getIterationDisplayIdentifier().toString());
			WTDocumentDependencyLink link = WTDocumentDependencyLink.newWTDocumentDependencyLink(newDoc, partTechnicDoc);// 建立所参考的文件
			PersistenceServerHelper.manager.insert(link);// 保存link
		} else {// 直接更新工艺规程的zip包

			originalDoc = (WTDocument) WorkInProcessUtil.checkout(originalDoc);

			originalDoc = WTDocumentUtil.setPrimaryForDocument(originalDoc, technicsName + ".zip", technicsZip);// 设置新建所参考文件的主物件
			originalDoc = (WTDocument) WorkInProcessUtil.checkin(originalDoc);
			returnMap.put(SUCCESS, SUCCESS);
			returnMap.put(LIFECYCLE, originalDoc.getLifeCycleState().getLongDescription(Locale.CHINA));
			returnMap.put(VERSION, originalDoc.getIterationDisplayIdentifier().toString());
		}

		return returnMap;
	}

	/**
	 * @param partOid
	 * @param processPlanOid
	 * @param xmlVersion
	 * @return
	 * @author fly
	 * @date 2012-12-13
	 */
	public static Vector<Object> getProcessPlanbyOidRMI(String partOid, String processPlanOid, String xmlVersion) {
		GLLogger.debug(CLASSNAME, "getProcessPlanbyOidRMI start..");
		GLLogger.debug(CLASSNAME, "processPlanOid==" + processPlanOid);
		if (processPlanOid == null)
			return null;
		Vector<Object> vector = new Vector<Object>();
		try {
			WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, processPlanOid);
			ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
			if (data != null) {
				byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
				vector.add(document.getNumber());
				vector.add(bytes);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vector;
	}

	/**
	 * 通过零件的oid查询工艺规程的版本信息
	 *
	 * @param oid
	 * @return
	 * @throws WTException
	 * @author flyW
	 * @date 2012-12-13
	 */
	public static List<List<String>> getProcessPlanHistoryRMI(String oid, String processType, String processName, String technicsType) throws WTException {
		GLLogger.debug(CLASSNAME, "getProcessPlanHistoryRMI oid=" + oid + "--processType--" + processType + "--processName--" + processName);
		List<List<String>> processPlans = new ArrayList<List<String>>();
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
		try {
			List<WTDocument> docList = ProcessPlanHelper.getProcessZipDoc(part, processName, technicsType, processType);
			for (WTDocument doc : docList) {
				QueryResult qr = VersionControlHelper.service.allIterationsOf(doc.getMaster());
				while (qr.hasMoreElements()) {
					WTDocument doc_temp = (WTDocument) qr.nextElement();
					ArrayList<String> pp = new ArrayList<String>();
					pp.add(doc_temp.getIterationDisplayIdentifier().toString());
					pp.add(doc_temp.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
					String note = doc_temp.getIterationNote();
					pp.add(note == null ? "" : note);
					pp.add(Util.getStringOid(doc_temp));
					processPlans.add(pp);
				}
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}

		return processPlans;
	}

	/**
	 * 获取新建工装申请卡时的默认信息
	 *
	 * @author qianlong
	 * @date 2013-4-8
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNewFrockInfoRMI() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Map list = GZNumberManager.getSearchGZNumber2();
			map.put("number", list);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 新建工装申请卡
	 *
	 * @return
	 * @author qianlong
	 * @date 2013-4-8
	 */
	public static Frock createFrockCardRMI(Map<String, Object> map) {
		Frock frock = null;
		String number = (String) map.get(AttributeConstants.number);
		String name = (String) map.get(AttributeConstants.name);
		InputStream inputStream = null;
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			// 创建工装申请卡

			Map<String, String> ibaMap = new HashMap<String, String>();
			ibaMap.put(AttributeConstants.insertPart, (String) map.get(AttributeConstants.insertPart));
			ibaMap.put(AttributeConstants.isReview, (String) map.get(AttributeConstants.isReview));
			ibaMap.put(AttributeConstants.isCommonTools, (String) map.get(AttributeConstants.isCommonTools));
			ibaMap.put(AttributeConstants.isTestPart, (String) map.get(AttributeConstants.isTestPart));
			ibaMap.put(AttributeConstants.workShop, (String) map.get(AttributeConstants.workShop));
			ibaMap.put(AttributeConstants.productNumber, (String) map.get(AttributeConstants.productNumber));
			ibaMap.put(AttributeConstants.productionNumber, (String) map.get(AttributeConstants.productionNumber));
			ibaMap.put(AttributeConstants.partNumber, (String) map.get(AttributeConstants.partNumber));
			ibaMap.put(AttributeConstants.wholePartNumber, (String) map.get(AttributeConstants.wholePartNumber));
			ibaMap.put(AttributeConstants.toolingRequirements, (String) map.get(AttributeConstants.toolingRequirements));
			ibaMap.put(AttributeConstants.isRegularlyTools, (String) map.get(AttributeConstants.isRegularlyTools));
			ibaMap.put(AttributeConstants.partOid, (String) map.get(AttributeConstants.partOid));
			Map<String, InputStream> fileMap = new HashMap<String, InputStream>();
			String fileName = (String) map.get("fileName");
			byte[] fileBytes = (byte[]) map.get("fileBytes");
			if (null != fileName && !"".equals(fileName) && null != fileBytes) {
				inputStream = new ByteArrayInputStream(fileBytes);
				fileMap.put(fileName, inputStream);
			}
			// 创建工装
			GZCardHelper.createGZCard(number, name, ibaMap, fileMap);
			// 创建工装资源
			Map<String, String> ibaMap1 = new HashMap<String, String>();
			ibaMap1.put(AttributeConstants.productNumber, (String) map.get(AttributeConstants.productNumber));
			ibaMap1.put(AttributeConstants.partNumber, (String) map.get(AttributeConstants.partNumber));
			ibaMap1.put(AttributeConstants.gzCardNumber, number);
			ibaMap1.put(AttributeConstants.isRegularlyTools, (String) map.get(AttributeConstants.isRegularlyTools));
			ibaMap1.put(AttributeConstants.workShop, (String) map.get(AttributeConstants.workShop));
			MPMTooling tooling = GZCardHelper.createGZResource(number.replace(".", "-"), name, ibaMap1);
			frock = MPMResourceHelper.getFrock(tooling);
			// 设定工装编号为已使用
			GZNumberManager.setNumberUsed(number);

			transaction.commit();
			transaction = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (transaction != null) {
				transaction.rollback();
			}
		}
		return frock;
	}

	/**
	 * 根据工装编号或者名称获取模糊查询的工装编号的列表
	 *
	 * @return
	 * @author qianlong
	 * @date 2013-4-24
	 */
	public static List<Map<String, Object>> showAllFrockCardRMI(Map<String, String> map) {
		String number = map.get(AttributeConstants.number);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		number = Util.formatSearchString(number);
		try {
			QueryResult queryResult = WTDocumentUtil.getDocumentByLikeNumberOrNameType(number, TypeNameConstants.gzCardTypeName);
			while (queryResult.hasMoreElements()) {
				WTDocument document = (WTDocument) queryResult.nextElement();
				list.add(GZCardHelper.getShowFrockCardinfo(document, false));
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return list;

	}

	/**
	 * 获取工装申请卡的信息和签审信息
	 *
	 * @author qianlong
	 * @date 2013-4-23
	 */
	public static Map<String, Object> showFrockCardRMI(Map<String, String> map) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String number = map.get(AttributeConstants.number);
		try {
			WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(number);
			GLLogger.debug(CLASSNAME, "showFrockCardRMI map=" + map + "--document--" + document);
			returnMap = GZCardHelper.getShowFrockCardinfo(document, true);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	/**
	 * 获取特殊符号
	 *
	 * @author qianlong
	 * @date 2013-4-22
	 */
	public static byte[] getSymbolPropertiesRMI() {
		String filePath = PropertiesUtil.getLocalCodeBase() + PropertiesConfigs.SYMBOL_CONFIG_PATH;
		return FileUtil.fileToBytes(filePath);
	}

	/**
	 * 通过文件的名称获取材料定额的配置文件
	 *
	 * @return
	 * @author fly
	 * @date 2013-5-6
	 */
	public static List<MaterialCal> getMaterialQuotaRMI() {
		return getMaterialQuotaFile(null);
	}

	/**
	 * 通过文件的名称获取材料定额的配置文件
	 *
	 * @return
	 * @author fly
	 * @date 2013-5-6
	 */
	private static List<MaterialCal> getMaterialQuotaFile(String docName) {
		if (null == docName || "".equals(docName)) {
			docName = MATERIAL_QUOTA_FILE_NAME;
		}
		WTDocument doc = WTDocumentUtil.getLastWTDocumentByName(docName);
		if (doc == null) {
			// 如果查找不到配置文件，直接返回空值
			return null;
		}
		// 判断当前doc文件的版本是否发生变化，如果没有变化返回原来的mc，如果有变化重新遍历配置文件。
		if (doc.getIterationDisplayIdentifier().equals(MATERIAL_QUOTA_FILE_VERSION)) {
			// 版本没有发生变化,
		} else {
			// 版本有变化，及时更新内存的内容
			GLLogger.debug("current MATERIAL_QUOTA_FILE_VERSION=" + doc.getIterationDisplayIdentifier());
			try {
				ApplicationData data = WTDocumentUtil.getPrimaryByDocument(doc);
				InputStream is = ContentServerHelper.service.findContentStream(data);
				// 更新内存的内容
				MATERIALCAL_LIST = MaterialQuotaFileUtil.readExcel(is);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return MATERIALCAL_LIST;
	}

	/**
	 * 通过零件的oid和工艺规程的名称查询最新的工艺规程
	 *
	 * @param
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @author fly
	 * @date 2013-06-07
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static Vector getLastProcessPlanRMI(String oid, String processName) throws WTException, PropertyVetoException {
		GLLogger.debug(CLASSNAME, "getLastProcessPlanRMI oid=" + oid);
		Vector vec = new Vector();
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, oid);
		String documentType = propertiesUtil.getProperty("process-zip-document-type");
		List<WTDocument> docList = WTPartUtil.getDescribedDocumentByPart(part, documentType);
		for (WTDocument doc : docList) {
			if (doc.getName().equals(processName)) {
				WTDocument doc_temp = (WTDocument) VersionControlHelper.service.allIterationsOf(doc.getMaster()).nextElement();
				vec.add(doc_temp.getIterationDisplayIdentifier().toString());
				vec.add(doc_temp.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
				vec.add(WTDocumentUtil.applicationDataToByte((ApplicationData) ContentHelper.service.getPrimary(doc_temp)));
				vec.add(Util.getStringOid(doc_temp));
				String note = doc_temp.getIterationNote();
				vec.add(note == null ? "" : note);
			}
		}
		return vec;
	}

	public static Vector getLastProcessDocumentRMI(String technicsNumber) throws WTException, PropertyVetoException {
		GLLogger.debug(CLASSNAME, "getLastProcessDocumentRMI technicsNumber=" + technicsNumber);
		Vector vec = new Vector();

		WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
		if (doc == null)
			return vec;
		vec.add(doc.getIterationDisplayIdentifier().toString());
		vec.add(doc.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
		vec.add(WTDocumentUtil.applicationDataToByte((ApplicationData) ContentHelper.service.getPrimary(doc)));
		vec.add(Util.getStringOid(doc));
		String note = doc.getIterationNote();
		vec.add(note == null ? "" : note);
		return vec;
	}

	/**
	 * @author qianlong
	 * @date 2013-8-16
	 */
	public static CheckTechnics checkProcessPlanRMI(CheckTechnics checkTechnics) {
		try {
			ProcessPlanHelper.checkProcessPlan(checkTechnics);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return checkTechnics;
	}

	/**
	 * 查询部门(主制单位|辅制单位)
	 *
	 * @param partId
	 * @return
	 */
	public static String[] queryDept(String partId) {
		WTContainer wtContainer = null;
		WTPart part = (WTPart) Util.searchRMI(WTPart.class, Long.parseLong(partId));
		wtContainer = part.getContainer();
		Map<String, String> bms = PbomUtil.getAllCheJianAndXiangMuBuMapGYZZ(wtContainer, PbomUtil.GROUP_GYRWFG_NAME);
		String[] bm = new String[bms.size()];
		Iterator it = bms.keySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			bm[i] = (String) it.next();
			i++;
		}
		return bm;
	}

	/**
	 * @param oid
	 * @return list[0] 需要做返工工艺,临时工艺的PartNumber oid list[1] 需要做返工工艺的PartNumber
	 *         number
	 * @throws WTException
	 * @throws WTRuntimeException
	 * @author xcLong
	 * @date 2013-9-11
	 */
	public static List<String> getTaskByIdRMI(String oid) throws WTRuntimeException, WTException {
		List<String> list = new ArrayList<String>();
		Object object = ReferenceFactory.getObjectbyOid(oid);
		WTPart part = null;
		if (object instanceof ProcessTask) {
			ProcessTask task = (ProcessTask) object;
			part = WTPartUtil.getLatestPartByNumberAndView(task.getNumber(), Constants.planning);
		}
		if (part != null) {
			list.add(Util.getStringOid(part));
			list.add(part.getNumber());
		}
		return list;
	}

	/**
	 * 启动工艺文件签审流程
	 *
	 * @param docNumber
	 *            工艺文档的编号
	 * @author LongXiuChuan
	 */
	public static void startProcessPlanWorkflow(String docNumber) {
		try {
			ProcessPlanHelper.startProcessPlanWorkflow(docNumber);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public static PdNameType get812AllPdNameTypeRMI() {
		if(ProcessCache.GET812ALLPDNAMETYPERMI !=null){
			return ProcessCache.GET812ALLPDNAMETYPERMI;
		}
		PdNameType pdNameType = null;
		try {
			WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
			Folder folder = FolderUtil.getFolder(Constants.rootFolder + "/" + Constants.mpmResourceFolderName[8], WTContainerRef.newWTContainerRef(container));
			pdNameType = new PdNameType(folder.getName());
			MPMResourceHelper.getPdNameFolder(folder, pdNameType);
		} catch (WTException e) {
			e.printStackTrace();
		}
		ProcessCache.GET812ALLPDNAMETYPERMI = pdNameType;
		return pdNameType;
	}

	/**
	 * 查询子件模型在装配体中的位置
	 *
	 * @param map
	 *            Map<Long, List<Long>> key为父件的ida2a2值，值为子件的ida2a2集合
	 * @return HashMap<Long , Matrix4d> key为部件的ida2a2值，值为该部件模型的在装配体中的位置
	 * @author xcLong
	 */
	public static HashMap<Long, Matrix4d> getMatrix4dByPartOid(Map<Long, List<Long>> map) {
		HashMap<Long, Matrix4d> Matrix4dMap = new HashMap<Long, Matrix4d>();
		try {
			String prefix = "OR:wt.part.WTPart:";
			Long longId = map.keySet().iterator().next();
			String parentOid = prefix + longId;
			WTPart parentPart = (WTPart) ReferenceFactory.getObjectbyOid(parentOid);
			if (parentPart == null) {
				GLLogger.debug(CLASSNAME, "------getMatrix4dByPartOid parentPart is not exsit for:" + longId);
				return Matrix4dMap;
			}
			List<Long> longList = map.values().iterator().next();
			for (Long ida2a2 : longList) {
				String childOid = prefix + ida2a2;
				WTPart childPart = (WTPart) ReferenceFactory.getObjectbyOid(childOid);
				if (childPart == null) {
					GLLogger.debug(CLASSNAME, "------getMatrix4dByPartOid childPart is not exsit for:" + ida2a2);
					continue;
				}
				WTPartMaster childMaster = (WTPartMaster) childPart.getMaster();
				WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(parentPart, childMaster);
				List<WTPartUsageLink> tempList = new ArrayList<WTPartUsageLink>();
				tempList.add(link);
				WTKeyedMap occHm = OccurrenceHelper.service.getUsesOccurrences(new WTArrayList(tempList));
				WTHashSet occCol = (WTHashSet) occHm.get(link);
				for (Iterator it = occCol.persistableIterator(); it.hasNext();) {
					PartUsesOccurrence partUsesOcc = (PartUsesOccurrence) it.next();
					Matrix4d m4d = new Matrix4d();
					if (partUsesOcc.isHasTransform()) {
						m4d = partUsesOcc.toMatrix4d();
					}
					Matrix4dMap.put(ida2a2, m4d);
				}
			}
		} catch (WTRuntimeException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return Matrix4dMap;
	}

	public static void deleteWTPartDescribeDocLink(String docNumber) {
		try {
			MPMProcessPlanUtil.deleteWTPartDescribeDocLink(docNumber);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
	}

	public static void removeFujianDoc(String number) {
		try {
			WTDocument doc = WTDocumentUtil.getDocumentByNumber(number);
			QueryResult qr = VersionControlHelper.service.allIterationsOf(doc.getMaster());
			while (qr.hasMoreElements()) {
				PersistenceHelper.manager.delete((WTDocument) qr.nextElement());
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Map<String, String> getMPMPPlanSubTypes() {
		if(ProcessCache.getMPMPPlanSubTypes!=null){
			return ProcessCache.getMPMPPlanSubTypes;
		}
		Map<String, String> map = new TreeMap<String, String>();
		try {
			List<TypeIdentifier> list = TypeUtil.getChildTypes(MPMProcessPlan.class.getName());
			if (list != null && list.size() != 0) {
				map.put("a", "");
				for (TypeIdentifier identifier : list) {
					String typeName = identifier.getTypename();
					String diplayName = TypedUtility.getLocalizedTypeName(identifier, Locale.CHINA);
					map.put(typeName, diplayName);
				}
			}
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		ProcessCache.getMPMPPlanSubTypes = map;
		GLLogger.debug(CLASSNAME, "------getMPMPPlanSubTypes map:" + map);
		return map;
	}

	public static Map<String, String> getAttrByMPMPlan(String typeName) {
		GLLogger.debug(CLASSNAME, "------getAttrByMPMPlan typeName:" + typeName);
		return LoadPPlanAttrConfig.getInstance().getAllAttriMapForPrefix(typeName);
	}

	public static Map<String, String> getMPMOperAttrByMPMPlan(String typeName) {
		GLLogger.debug(CLASSNAME, "------getMPMOperAttrByMPMPlan typeName:" + typeName);
		return LoadMPMOperAttrConfig.getInstance().getAllAttriMapForPrefix(typeName);
	}

	/**
	 * 获取工艺类型的特征码映射关系
	 *
	 * @return Map<String , String> key:工艺类型，value:特征码
	 */
	public static Map<String, String> getPPlanID() {
		if(ProcessCache.getPPlanID!=null){
			return ProcessCache.getPPlanID;
		}else{
			ProcessCache.getPPlanID =  LoadPPlanIDConfig.getInstance().getPPlanID();
			return ProcessCache.getPPlanID;

		}
	}

	/**
	 * 通过零部件编号查找到零部件对象，并返回需要获取的IBA属性的值
	 *
	 * @param number
	 *            零部件编号
	 * @param list
	 *            属性获取的IBA属性的名称集合
	 * @return Map<String , String> key:属性名称,value:属性值
	 */
	public static Map<String, String> getPartIBAValuesByNumber(String number, List<String> list) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			WTPart part = WTPartUtil.getLatestPartByNumberAndView(number, "Manufacturing");
			if (part != null) {
				IBAHelper ibaHelper = new IBAHelper(part);
				for (String key : list) {
					String value = ibaHelper.getIBAValue(key);
					map.put(key, value);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 通过零部件编号查找到零部件对象，并返回需要获取的IBA属性的值
	 *
	 * @param number
	 *            零部件编号
	 * @param list
	 *            属性获取的IBA属性的名称集合
	 * @return Map<String , String> key:属性名称,value:属性值
	 */
	public static Map<String, String> getPartIBAValuesByNumber2(String number, List<List<String>> list) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			WTPart part = WTPartUtil.getLatestPartByNumberAndView(number, "Manufacturing");
			if (part != null) {
				IBAHelper ibaHelper = new IBAHelper(part);
				for (List<String> l : list) {
					String value = ibaHelper.getIBAValue(l.get(5));
					map.put(l.get(5), value);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 从配置文件XML获取所有工艺类型的属性
	 *
	 * @return Map<String , List < List < String>>> key:工艺名称
	 *         value：List<List<String>>每一个工艺类型的所有属性,List<String>每一个类型的属性值
	 */
	public static Map<String, List<List<String>>> getMPMPPlanTypeAttrByXML() {
		Map<String, List<List<String>>> map = new HashMap<String, List<List<String>>>();
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			String dir = pro.getProperty("wt.codebase.location");
			String fileName = dir + File.separator + "mpmpplan_attributes.xml";
			List<List<String>> typeList = null;
			List<String> attrList = null;
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(fileName));
			Element rootElement = document.getRootElement();
			Iterator iterator = rootElement.elementIterator();
			while (iterator.hasNext()) {
				Element childElement = (Element) iterator.next();
				Iterator iterator2 = childElement.elementIterator();
				typeList = new ArrayList<List<String>>();
				while (iterator2.hasNext()) {
					attrList = new ArrayList<String>();
					Element chlElement = (Element) iterator2.next();
					if ("ibaattribute".equals(chlElement.getName())) {
						attrList.add(chlElement.attributeValue("name"));
						attrList.add(chlElement.attributeValue("display"));
						attrList.add(chlElement.attributeValue("default"));
						attrList.add(chlElement.attributeValue("valueSet"));
						attrList.add(chlElement.attributeValue("dataType"));
						attrList.add(chlElement.attributeValue("copyPartAttr"));
						attrList.add(chlElement.attributeValue("isEditor"));
						typeList.add(attrList);
					}
				}
				map.put(childElement.attributeValue("display"), typeList);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 从配置文件XML获取所有工艺类型的工序属性
	 *
	 * @return Map<String , List < List < String>>> key:工艺名称
	 *         value：List<List<String>>每一个工艺类型的所有属性,List<String>每一个类型的所有工序属性值
	 */
	public static Map<String, List<List<String>>> getMPMPPlanStepAttrByXML() {
		if(ProcessCache.GETMPMPPLANSTEPATTRBYXML !=null){
			return ProcessCache.GETMPMPPLANSTEPATTRBYXML;
		}
		Map<String, List<List<String>>> map = new HashMap<String, List<List<String>>>();
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			String dir = pro.getProperty("wt.codebase.location");
			String fileName = dir + File.separator + "mpmpplan_attributes.xml";
			List<List<String>> typeList = null;
			List<String> attrList = null;
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(fileName));
			Element rootElement = document.getRootElement();
			Iterator iterator = rootElement.elementIterator();
			while (iterator.hasNext()) {
				Element childElement = (Element) iterator.next();
				Iterator iterator2 = childElement.elementIterator();
				typeList = new ArrayList<List<String>>();
				while (iterator2.hasNext()) {
					Element chlElement = (Element) iterator2.next();
					if ("step".equals(chlElement.getName())) {
						Iterator iterator3 = chlElement.elementIterator();
						while (iterator3.hasNext()) {
							Element element = (Element) iterator3.next();
							attrList = new ArrayList<String>();
							attrList.add(element.attributeValue("name"));
							attrList.add(element.attributeValue("display"));
							attrList.add(element.attributeValue("default"));
							attrList.add(element.attributeValue("valueSet"));
							attrList.add(element.attributeValue("dataType"));
							typeList.add(attrList);
						}
					}
				}
				map.put(childElement.attributeValue("display"), typeList);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>>>>>>>>map>>>>>>>>" + map);
		ProcessCache.GETMPMPPLANSTEPATTRBYXML = map;
		return map;
	}

	/**
	 * 提交辅制工艺任务
	 *
	 * @param map
	 * @return Boolean
	 */
	public static boolean createTaskItem(Map<String, String> map) {
		boolean flag = true;
		try {
			flag = ProcessUtil.createProcessTaskItem(map);
		} catch (WTPropertyVetoException e) {
			flag = false;
			e.printStackTrace();
		} catch (WTException e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 方法功能: 校验工艺是否存在辅制工艺任务
	 *
	 * @param processNumber
	 * @param taskType
	 * @return boolean
	 * @author LB
	 * @date 2020/9/6
	 */
	public static Integer checkHasFzProcessTask(String processNumber,String taskType){
		int count = 0;
		try {
			List<ProcessTask> processTaskList = ProcessUtil.getProcessTaskByProcessNumberAndTaskType(processNumber, taskType);
			if(processTaskList != null && processTaskList.size() > 0){
				for (ProcessTask processTask : processTaskList) {
					if(!"已作废".equals(processTask.getTaskState())){
						count++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 提起辅制工艺任务时，对于同一份工艺文件、同一个车间，应该进行如下容错：
	 * 1、如果是工艺设计任务：只允许有一个正在进行的任务，否则报错已存在（如果工艺任务已完成，可以重复） 2、如果是工艺更改任务：同上
	 * 3、如果是临时工艺任务：允许同时下达多个
	 *
	 * @param map
	 * @return
	 */
	public static boolean checkProcessTask(Map<String, String> map) {
		boolean flag = true;
		try {
			flag = ProcessUtil.checkProcessTask(map);
		} catch (WTException e) {
			flag = false;
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 获取当前用户的所有工艺任务关联的part编号
	 *
	 * @return
	 */
	public static List<String> getCurrentUserTaskPartInfo() {
		List<String> list = new ArrayList<String>();
		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			QueryResult qr = ProcessUtil.queryProcessTask(user, "正在进行");
			if (qr != null) {
				WTPart part = null;
				while (qr.hasMoreElements()) {
					ProcessTaskItem taskItem = (ProcessTaskItem) qr.nextElement();
					part = ProcessUtil.getWtPartByProcessTask(taskItem.getProcessTaskId());
					if (part != null) {
						list.add(part.getNumber());
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 获取当前用户的所有工艺任务关联的部件OID
	 *
	 * @return
	 */
	public static List<String> getCurrentUserTaskPartOidInfo() {
		List<String> list = new ArrayList<String>();
		try {
			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			QueryResult qr = ProcessUtil.queryProcessTask(user, "正在进行");
			if (qr != null) {
				WTPart part = null;
				while (qr.hasMoreElements()) {
					ProcessTaskItem taskItem = (ProcessTaskItem) qr.nextElement();
					part = ProcessUtil.getWtPartByProcessTask(taskItem.getProcessTaskId());
					if (part != null) {
						list.add(part.getNumber());
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<String> getCurrentUserTaskPartOidInfo(List<String> list) {
		System.out.println("------------------getCurrentUserTaskPartOidInfo-----starting---");
		List<String> allOid = new ArrayList<String>();
		try {
			WTUser currentUser = (WTUser) SessionHelper.manager.getPrincipal();
			System.out.println("------------------user----------" + currentUser.getName());
			for (String oid : list) {
				long ida2a2 = Long.valueOf(oid);
				System.out.println("----------oid-----" + oid);
				WTPart part = WTPartUtil.getPartByOid(ida2a2);
				if (part != null) {
					ProcessTask processTask = ProcessUtil.getProcessTaskByPart(part, null);
					System.out.println("----------processTask-----" + processTask);
					if (processTask != null) {
						QueryResult qr = ProcessUtil.getAllProcessTaskItemByPTask(PersistenceHelper.getObjectIdentifier(processTask).getId());
						while (qr.hasMoreElements()) {
							ProcessTaskItem taskItem = (ProcessTaskItem) qr.nextElement();
							String role = taskItem.getExecutorRole();
							if ("工艺员".equals(role)) {
								String user = taskItem.getOwner();
								if (currentUser.getName().equals(user)) {
									part = (WTPart) VersionControlHelper.getLatestIteration(part, true);
									System.out.println(part.getNumber() + "   " + part.getViewName() + "   " + part.getVersionIdentifier().getValue() + "." + part.getIterationIdentifier().getValue());
									allOid.add(String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId()));
									break;
								}
							}
						}
					} else {// 查找当前之前的版本对象关联的工艺任务
						QueryResult qr = VersionControlHelper.service.allIterationsFrom(part);
						while (qr.hasMoreElements()) {
							part = (WTPart) qr.nextElement();
							processTask = ProcessUtil.getProcessTaskByPart(part, null);
							if (processTask != null) {
								qr = ProcessUtil.getAllProcessTaskItemByPTask(PersistenceHelper.getObjectIdentifier(processTask).getId());
								while (qr.hasMoreElements()) {
									ProcessTaskItem taskItem = (ProcessTaskItem) qr.nextElement();
									String role = taskItem.getExecutorRole();
									if ("工艺员".equals(role)) {
										String user = taskItem.getOwner();
										if (currentUser.getName().equals(user)) {
											part = (WTPart) VersionControlHelper.getLatestIteration(part, true);
											System.out.println(part.getNumber() + "   " + part.getViewName() + "   " + part.getVersionIdentifier().getValue() + "."
													+ part.getIterationIdentifier().getValue());
											allOid.add(String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId()));
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		System.out.println("------------------getCurrentUserTaskPartOidInfo-----end---");
		return allOid;
	}

	/**
	 * 通过产品ID获取该产品下的所有批次号
	 *
	 * @param oid
	 *            产品ida2a2
	 * @return List<String> 批次号集合
	 * @author longxiuchuan
	 */
	public static Vector<String> getBatchsByProductOid(long oid) {
		Vector<String> list = new Vector<String>();
		list.add("");
		try {
			PDMLinkProduct product = WTContainerUtil.getProductByOid(oid);
			if (product != null) {
				String productName = product.getName();
				List<Batch> allBatchs = DBUtil.getBatchesByProduct(String.valueOf(oid), productName);
				if (allBatchs != null) {
					for (Batch batch : allBatchs) {
						list.add(batch.getName());
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 通过产品名称获取该产品下的所有批次号
	 *
	 * @param name
	 *            产品名称
	 * @return List<String> 批次号集合
	 * @author longxiuchuan
	 */
	public static Vector<String> getBatchsByProductName(String name) {
		Vector<String> list = new Vector<String>();
		list.add("");
		try {
			PDMLinkProduct product = WTContainerUtil.getProductByName(name);
			if (product != null) {
				String productName = product.getName();
				long oid = PersistenceHelper.getObjectIdentifier(product).getId();
				List<Batch> allBatchs = DBUtil.getBatchesByProduct(String.valueOf(oid), productName);
				if (allBatchs != null) {
					for (Batch batch : allBatchs) {
						list.add(batch.getName());
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 通过指定PBOM零部件的编号，查询该PBOM零部件的批次信息
	 *
	 * @param number
	 *            指定的PBOM零部件的编号
	 * @return List<List < String>> 指定PBOM零部件的批次集合信息
	 * @author longxiuchuan
	 */
	public static List<List<String>> getPbomBatchsByPartNumber(String number) {
		List<List<String>> list = new ArrayList<List<String>>();
		try {
			WTPart part = WTPartUtil.getLatestPartByNumberAndView(number, "Manufacturing");
			if (part != null) {
				QueryResult qr = VersionControlHelper.service.allVersionsOf(part.getMaster());
				while (qr.hasMoreElements()) {
					WTPart p = (WTPart) qr.nextElement();
					String viewName = p.getViewName();
					if (!"Manufacturing".equals(viewName)) {
						continue;
					}

					IBAHelper ibaHelper = new IBAHelper(p);
					String batch = ibaHelper.getIBAValue("BATCH");
					String phase_code = ibaHelper.getIBAValue("PHASE_CODE");
					String version = p.getVersionInfo().getIdentifier().getValue() + "." + p.getIterationInfo().getIdentifier().getValue();
					List<String> batchList = new ArrayList<String>();
					batchList.add(batch);
					batchList.add(version);
					batchList.add(p.getState().getState().getDisplay(Locale.CHINA));
					batchList.add(String.valueOf(PersistenceHelper.getObjectIdentifier(p).getId()));
					batchList.add(phase_code);
					list.add(batchList);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static long getEbomOidByMbomOidRMI(String mbomOid) throws WTException {
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, mbomOid);
		long viewId = PBOMEditorToWCIntfRMI.getViewByNameRMI("Design");
		part = WTPartUtil.getPartByNumberAndView(part.getNumber(), viewId);
		long partOid = 0;
		if (part != null) {
			partOid = Util.getObjectOid(part);
		}
		return partOid;
	}

	/**
	 * 获取指定工艺输出的格式
	 *
	 * @param techType
	 *            工艺类型
	 * @param techID
	 *            工艺特征码
	 * @param techForm
	 *            工艺形式
	 * @return
	 */
	public static List<TechnicsOutputFormBean> getMPMPPlanOutputFormsXml(String techType, String techID, String techForm) {
		System.out.println("--getMPMPPlanOutputFormsXml--techType:" + techType + "   techID:" + techID + "  techForm:" + techForm);
		List<TechnicsOutputFormBean> list = new ArrayList<TechnicsOutputFormBean>();
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			String dir = pro.getProperty("wt.codebase.location");
			String fileName = dir + File.separator + "mpmpplan_outputForms.xml";
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(fileName));
			Element rootElement = document.getRootElement();
			Iterator iterator = rootElement.elementIterator();
			while (iterator.hasNext()) {
				Element techElement = (Element) iterator.next();
				String display = techElement.attributeValue("display");
				if (techType.equals(display)) {
					Iterator iterator2 = techElement.elementIterator();
					while (iterator2.hasNext()) {
						Element chlElement = (Element) iterator2.next();
						String name = chlElement.attributeValue("name");
						if (techForm.equals(name)) {
							Iterator iterator3 = chlElement.elementIterator();
							while (iterator3.hasNext()) {
								Element element = (Element) iterator3.next();
								TechnicsOutputFormBean bean = new TechnicsOutputFormBean();
								bean.setTechType(techType);
								bean.setTechID(techID);
								bean.setTechForm(techForm);
								bean.setName(element.attributeValue("name"));
								bean.setFormName(element.attributeValue("formName"));
								bean.setFormId(element.attributeValue("formId"));
								bean.setSelectable(element.attributeValue("selectable"));
								list.add(bean);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		System.out.println("------list--" + list);
		return list;
	}

	/**
	 * 获取工艺文档材料定额状态
	 *
	 * @param techNumber
	 * @return
	 */
	public static String getCLDEState(String techNumber) {
		try {
			WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(techNumber);
			if (doc != null) {
				IBAHelper ibaHelper = new IBAHelper(doc);
				return ibaHelper.getIBAValue("CLDEZT");
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Boolean uploadAttachForDocumentRMI(String documentNumber, String fileName, byte[] bytes) throws WTException {
		try {
			WTDocument document = getLatestDocumentByNumberRMI(documentNumber);
			WTDocumentUtil.uploadAttachForDocument(document, fileName, bytes);
		} catch (FileNotFoundException e) {
			GLLogger.error("FileNotFoundException", e);
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			GLLogger.error("PropertyVetoException", e);
			e.printStackTrace();
		} catch (IOException e) {
			GLLogger.error("IOException", e);
			e.printStackTrace();
		}
		return true;
	}

	public static Boolean uploadAttachForSOPRMI(String documentNumber, String fileName, byte[] bytes) throws WTException {
		try {
			WTDocument document = getLatestDocumentByNumberRMI(documentNumber);
			WTDocumentUtil.uploadAttachForSOP(document, fileName, bytes);
		} catch (FileNotFoundException e) {
			GLLogger.error("FileNotFoundException", e);
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			GLLogger.error("PropertyVetoException", e);
			e.printStackTrace();
		} catch (IOException e) {
			GLLogger.error("IOException", e);
			e.printStackTrace();
		}
		return true;
	}

	public static boolean deleteAttachForSOPRMI(String documentNumber) throws WTException {
		boolean a = true;
		try {
			WTDocument document = getLatestDocumentByNumberRMI(documentNumber);
			a = WTDocumentUtil.deleteAttachForSOP(document);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return a;
	}

	public static WTDocument uploadAttachForDocumentRMI2(WTDocument document, String fileName, byte[] bytes) throws WTException {
		WTDocument doc = null;
		try {
			doc = WTDocumentUtil.uploadAttachForDocument(document, fileName, bytes);
		} catch (FileNotFoundException e) {
			GLLogger.error("FileNotFoundException", e);
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			GLLogger.error("PropertyVetoException", e);
			e.printStackTrace();
		} catch (IOException e) {
			GLLogger.error("IOException", e);
			e.printStackTrace();
		}
		return doc;
	}

	public static WTDocument getLatestDocumentByNumberRMI(String documentNumber) throws WTException {
		WTDocument doc = null;
		doc = WTDocumentUtil.getLatestDocumentByNumber(documentNumber);
		return doc;
	}

	public static String getDocumentStateByNumber(String number) throws WTException {
		WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(number);
		if (doc != null) {
			return doc.getState().getState().getDisplay(Locale.CHINA);
		}
		return "";
	}

	public static List<WTDocument> getAllDocumentByNumber(String number) throws WTException {
		return WTDocumentUtil.getAllDocumentByNumber(number);
	}

	/**
	 * 获取文档对象主内容
	 *
	 * @param partNumber
	 * @return
	 * @author LongXiuChuan
	 * @date 2014-5-28
	 */
	public static byte[] getDocumentPrimary(String number) {
		byte[] bytes = null;
		try {
			WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(number);
			String name = document.getName();
			if (document != null) {
				ApplicationData ad = WTDocumentUtil.getPrimaryByDocument(document);
				if (ad != null && ad.getFileName().endsWith("pdf")) {
					bytes = WTDocumentUtil.applicationDataToByte(ad);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public static WTPart getPartByOid(String oid) {
		WTPart part = null;
		try {
			if (oid != null && !"".equals(oid)) {
				part = WTPartUtil.getPartByOid(Long.valueOf(oid));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return part;
	}

	public static byte[] getDwgTemplateByteRMI(String number) throws WTException, PropertyVetoException, RemoteException {
		WTDocument document = WTDocumentUtil.getDocumentByNumber(number);
		ApplicationData ad = WTDocumentUtil.getPrimaryByDocument(document);
		return WTDocumentUtil.applicationDataToByte(ad);
	}

	public static WTDocument uploadDwgDocRMI(WTContainer container, String dwgNumber, String dwgName, byte[] bytes, String fileName) {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		WTDocument document = null;
		String folderPath = "Default/02工艺文件/10工艺附图/DWG简图/";
		boolean isAdd = true;
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			if (dwgNumber == null || "".equals(dwgNumber) || "null".equalsIgnoreCase(dwgName)) {
				document = WTDocumentUtil.createDocument(null, dwgName, container, folderPath, "casc.sast.149.DWG2PDF");
			} else {
				document = WTDocumentUtil.getDocumentByNumber(dwgNumber);
				isAdd = false;
			}
			if (document == null) {
				document = WTDocumentUtil.createDocument(null, dwgName, container, folderPath, "casc.sast.149.DWG2PDF");
			}
			// if(!isAdd){
			document = (WTDocument) WorkInProcessUtil.checkout(document);
			document = WTDocumentUtil.setPrimaryForDocument(document, fileName + ".dwg", bytes);
			document = (WTDocument) WorkInProcessUtil.checkin(document);
			// }else{
			// document = WTDocumentUtil.setPrimaryForDocument(document, dwgName
			// + ".dwg", bytes);
			// }
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			document = null;
			e.printStackTrace();
		} finally {
			transaction = null;
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

		return document;
	}

	/**
	 * 上传视频类大文件 add by liangbo
	 *
	 * @param container
	 * @param largeFileNumber
	 * @param largeFileName
	 * @param bytes
	 * @return
	 */
	public static WTDocument uploadLargeFileDoc(WTContainer container, String largeFileNumber, String largeFileName, byte[] bytes) {
		System.out.println("-----uploadLargeFileDoc-----");
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		WTDocument document = null;
		String folderPath = "Default/02工艺文件/10工艺附图/附件/";
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			if (largeFileNumber == null || "".equals(largeFileNumber) || "null".equalsIgnoreCase(largeFileNumber)) {
				document = WTDocumentUtil.createDocument(null, largeFileName, container, folderPath, "casc.sast.149.FUJIAN");
			} else {
				document = WTDocumentUtil.getDocumentByNumber(largeFileNumber);
			}
			if (document == null) {
				document = WTDocumentUtil.createDocument(null, largeFileName, container, folderPath, "casc.sast.149.FUJIAN");
			}
			document = (WTDocument) WorkInProcessUtil.checkout(document);
			document = WTDocumentUtil.setPrimaryForDocument(document, largeFileName, bytes);
			document = (WTDocument) WorkInProcessUtil.checkin(document);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			document = null;
			e.printStackTrace();
		} finally {
			transaction = null;
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

		return document;
	}

	public static void deleteLargeFileDoc(String largeFileDocNumber) {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTDocument doc = WTDocumentUtil.getDocumentByNumber(largeFileDocNumber);
			PersistenceHelper.manager.delete(doc);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

	}

	public static List<WTDocument> getAllLargeFileDoc(String likeName) throws RemoteException, WTException {
		List<WTDocument> docList = new ArrayList<WTDocument>();
		// docList = WTDocumentUtil.getDocumentByType("casc.sast.149.FUJIAN");
		WTDocument doc = null;
		try {
			QueryResult qr = WTDocumentUtil.getDocumentByLikeNumberOrNameType(likeName, "casc.sast.149.FUJIAN");
			while (qr.hasMoreElements()) {
				doc = (WTDocument) qr.nextElement();
				docList.add(doc);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return docList;
	}

	public static byte[] downloadLargeFile(String docNumber) {
		ApplicationData data = null;
		byte[] bytes = null;
		try {
			WTDocument doc = WTDocumentUtil.getDocumentByNumber(docNumber);
			data = WTDocumentUtil.getPrimaryByDocument(doc);
			bytes = WTDocumentUtil.applicationDataToByte(data);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public static List<WTDocument> getDwg2pdfTemplateRMI() throws WTException, RemoteException {
		List<WTDocument> docList = null;
		String type = "casc.sast.149.DWG2PDFTemplate";
		WTContainer container = WTContainerUtil.getLibraryByName("工艺资源库");
		docList = WTDocumentUtil.getDocumentByTypeAndConatiner(container, type);
		return docList;
	}

	// 上传工序中附件面板里的文档

	public static WTDocument uploadFuJianDocRMI(WTContainer container, String dwgNumber, String dwgName, byte[] bytes) {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		WTDocument document = null;
		String folderPath = "Default/02工艺文件/10工艺附图/附件文档";
		Transaction transaction = new Transaction();
		try {
			transaction.start();
			getFolder(folderPath, WTContainerRef.newWTContainerRef(container));
			if (dwgNumber == null || "".equals(dwgNumber)) {
				document = WTDocumentUtil.createDocument(null, dwgName, container, folderPath, "casc.sast.149.FUJIAN_DOC");
			} else {
				document = WTDocumentUtil.getDocumentByNumber(dwgNumber);
			}
			if (document == null) {
				document = WTDocumentUtil.createDocument(null, dwgName, container, folderPath, "casc.sast.149.FUJIAN_DOC");
			}
			document = (WTDocument) WorkInProcessUtil.checkout(document);
			document = WTDocumentUtil.setPrimaryForDocument(document, dwgName, bytes);
			document = (WTDocument) WorkInProcessUtil.checkin(document);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			document = null;
			e.printStackTrace();
		} finally {
			transaction = null;
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

		return document;
	}

	public static WTPart getLatestParttByNumberRMI(String partNumber) throws WTException {
		WTPart part = null;
		part = WTPartUtil.getLatestPartByPartNumber(partNumber);
		return part;
	}

	public static byte[] getDwgPdfTemplateByteRMI(String number) throws RemoteException, WTException {
		WTDocument document = WTDocumentUtil.getDocumentByNumber(number);
		List<ApplicationData> adList = WTDocumentUtil.getAttachFromDocument(document);
		ApplicationData ad = null;
		String filename = null;
		for (ApplicationData data : adList) {
			filename = data.getFileName();
			if (filename.toLowerCase().endsWith(".pdf")) {
				ad = data;
			}
		}
		if (ad != null)
			return WTDocumentUtil.applicationDataToByte(ad);
		else
			return null;
	}

	public static String getPartStateByOid(String oid) {
		String state = "";
		try {
			if (oid != null && !"".equals(oid)) {
				WTPart part = WTPartUtil.getPartByOid(Long.valueOf(oid));
				if (part != null) {
					state = part.getState().getState().getDisplay(Locale.CHINA);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return state;
	}

	public static byte[] getFuJianDocByteRMI(String number) throws RemoteException, WTException {
		WTDocument document = WTDocumentUtil.getDocumentByNumber(number);
		List<ApplicationData> adList = WTDocumentUtil.getFuJianFromDocument(document);
		if (adList.get(0) != null)
			return WTDocumentUtil.applicationDataToByte(adList.get(0));
		else
			return null;
	}

	/**
	 * 更新PDS中间模型
	 *
	 * @param map
	 * @author qianlong
	 * @date 2012-11-26
	 */

	public static Map<String, Object> getNewCADRMI(String oid) {
		GLLogger.debug(CLASSNAME, "-oid-" + oid);
		Map<String, Object> cadMap = new HashMap<String, Object>();
		try {
			Versioned oldVersioned = null;
			Versioned newVersioned = null;

			oldVersioned = (Versioned) Util.getObjectByOid(EPMDocument.class, oid);
			if (null == oldVersioned) {
				oldVersioned = (WTPart) Util.getObjectByOid(WTPart.class, oid);
			}
			if (null == oldVersioned) {
				return cadMap;
			}
			String name = "";
			if (oldVersioned instanceof EPMDocument) {
				QueryResult result = VersionControlHelper.service.allVersionsOf(oldVersioned);
				newVersioned = (Versioned) result.nextElement();
				name = ((EPMDocument) newVersioned).getName();
			} else if (oldVersioned instanceof WTPart) {
				newVersioned = WTPartUtil.getLatestPartByNumberAndView((WTPart) oldVersioned, "Design");
			}
			if (null == newVersioned || oldVersioned.equals(newVersioned)) {
				return cadMap;
			}
			DerivedImage derivedImage = (DerivedImage) EPMDocumentUtil.getDefaultRepresentation((Representable) newVersioned).get(0);
			if (null == derivedImage) {
				return cadMap;
			}
			Representation representation = (Representation) ContentHelper.service.getContents(derivedImage);
			Map<ContentRoleType, String> endWithMap = new HashMap<ContentRoleType, String>();
			cadMap.putAll(EPMDocumentUtil.getRepByRoleAndEndWith(representation, endWithMap));
			if (cadMap != null && cadMap.size() != 0) {
				cadMap.put("version", newVersioned.getVersionIdentifier().getValue() + "." + newVersioned.getIterationIdentifier().getValue());
				cadMap.put("modelName", name);
				cadMap.put("oid", newVersioned.getPersistInfo().getObjectIdentifier().getId());
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "-cadMap-" + cadMap);
		return cadMap;
	}

	/**
	 * 添加pds中间模型 ，获取图档和注释
	 *
	 * @param map
	 * @author qianlong
	 * @date 2012-11-26
	 */

	public static Map<String, byte[]> getCADRMI(String oid) {
		GLLogger.debug(CLASSNAME, "-oid-" + oid);
		Map<String, byte[]> cadMap = new HashMap<String, byte[]>();
		try {
			Persistable persistable = null;
			persistable = (Persistable) Util.getObjectByOid(EPMDocument.class, oid);
			if (null == persistable) {
				persistable = (Persistable) Util.getObjectByOid(WTPart.class, oid);
			}
			if (null == persistable) {
				return cadMap;
			}
			DerivedImage derivedImage = (DerivedImage) EPMDocumentUtil.getDefaultRepresentation((Representable) persistable).get(0);
			if (null == derivedImage) {
				return cadMap;
			}
			Representation representation = (Representation) ContentHelper.service.getContents(derivedImage);
			Map<ContentRoleType, String> endWithMap = new HashMap<ContentRoleType, String>();
			cadMap = EPMDocumentUtil.getRepByRoleAndEndWith(representation, endWithMap);
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "-cadMap-" + cadMap);
		return cadMap;
	}

	/**
	 * 添加pds中间模型 ，获取图档和注释的名称，简图
	 *
	 * @param map
	 * @author qianlong
	 * @date 2012-11-26
	 */

	public static List<List<Object>> getCADNameRMI(Map<String, String> map) {
		GLLogger.debug(CLASSNAME, "-map-" + map);
		List<List<Object>> nameList = new ArrayList<List<Object>>();
		try {
			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, map.get("oid"));
			part = WTPartUtil.getLatestPartByNumberAndView(part, Constants.design);
			GLLogger.debug(CLASSNAME, "--part--" + part);
			if (null == part) {
				return nameList;
			}

			// 如果零件不存在可视化图档，就查询零件对应的EPMDocument获取可视化图档
			Representable representable = EPMDocumentUtil.getHasRepPersistable(part);
			if (null != representable) {
				EPMDocumentUtil.getCADAndMarkupName(representable, nameList, true);
			}

			// 装配零件和单个零件的中间模型的编号规则不同
			List<Representable> persistableList = new ArrayList<Representable>();
			String number = part.getNumber().replace(".", "_");
			QueryResult epmResult = EPMDocumentUtil.getEPMDocumentLikeNumber(number + "%");
			// QueryResult epmResult =
			// EPMDocumentUtil.getEPMDocumentLikeNumber(number +
			// Constants.middleModelNameContains + "%");
			while (null != epmResult && epmResult.hasMoreElements()) {
				EPMDocument epmDocument = (EPMDocument) epmResult.nextElement();
				String epmNumber = epmDocument.getNumber();
				// if (epmNumber.length() != number.length() + 11 &&
				// epmNumber.length() != number.length() + 10) {// 过滤掉已经删除的中间模型
				persistableList.add(epmDocument);
				// }
			}
			GLLogger.debug(CLASSNAME, "--persistableList--" + persistableList);
			// 获取对应的可视化图档
			for (Representable rep : persistableList) {
				EPMDocumentUtil.getCADAndMarkupName(rep, nameList, false);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		GLLogger.debug(CLASSNAME, "-nameList-" + nameList);
		return nameList;
	}

	/**
	 * 获取分类顶层节点
	 *
	 * @return List<String> 分类顶层节点集合
	 */
	public static List<String> getTopGlClassificationNode() {
		List<String> list = new ArrayList<String>();
		list.add("全部");
		try {
			Connection conn = OracleDataSource.getOracleDataSource().getConnection();
			StringBuffer selectSQL = new StringBuffer();
			selectSQL.append("select g2.nodename from glclassificationnode g1, glclassificationnode g2 where g2.topid= g1.ida2a2 and g1.topid='0'");
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement(selectSQL.toString());
			ResultSet resultset = ps.executeQuery();
			while (resultset.next()) {
				list.add(resultset.getString(1));
			}
			if(resultset!=null){
				resultset.close();
			}
			if(ps!=null){
				ps.close();
			}
			if(conn!=null){
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 获取所有选用目录名称及该选用目录的类型集合
	 *
	 * @return Map<String , List < String>>
	 *         key:选用目录名称，value:List<String>为该选用目录对应的类型
	 */
	public static Map<String, List<String>> getGlcataLog() {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> list = null;
		try {
			QuerySpec qs = new QuerySpec(GLCatalog.class);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			while (qr.hasMoreElements()) {
				GLCatalog catalog = (GLCatalog) qr.nextElement();
				String name = catalog.getCatalogname();
				String type = catalog.getCatalogtype();
				if (map.containsKey(name)) {
					list = map.get(name);
					list.add(type);
					map.put(name, list);
				} else {
					list = new ArrayList<String>();
					list.add(type);
					map.put(name, list);
				}
			}
		} catch (wt.query.QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 工艺编辑器中工艺定额，材料定额查询资源库数据接口 通过指定的条件查询数据并返回数据集合
	 *
	 * @param xyml
	 *            选用目录
	 * @param xzfl
	 *            选择分类
	 * @param number
	 *            编号
	 * @param name
	 *            名称
	 * @param ibaMap
	 *            IBA属性
	 * @return List<SjzykBean> 数据集合
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public static List<SjzykBean> queryData(String type, String xyml, String xzfl, String number, String name, Map<String, String> ibaMap, String containerName) throws RemoteException,
			InvocationTargetException {
		List<SjzykBean> list = new ArrayList<SjzykBean>();
		// QueryResult qr = WTPartUtil.queryPart(number, name, "", ibaMap);
		// WTPart part = null;
		// while(qr.hasMoreElements()) {
		// part = (WTPart)qr.nextElement();
		// list.add(SjzykUtil.createBeanByPart(part));
		// }

		try {
			View view = WTPartUtil.getViewByName("Design");
			long viewId = PersistenceHelper.getObjectIdentifier(view).getId();
			if ("标准件".equals(type)) {
				// 选用目录与零部件编号关系
				list = SjzykDBUtil.queryBzj(xyml, xzfl, number, name, ibaMap, containerName, viewId);

				// 选用目录与目录条目关联，目录条目与零部件编号关系
				if (xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
					list.addAll(SjzykDBUtil.queryBzj2(xyml, xzfl, number, name, ibaMap, containerName, viewId));
				}
			} else if ("元器件".equals(type)) {
				// 选用目录与零部件编号关系
				list = SjzykDBUtil.queryYqj(xyml, xzfl, number, name, ibaMap, containerName, viewId);

				// 选用目录与目录条目关联，目录条目与零部件编号关系
				if (xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
					list.addAll(SjzykDBUtil.queryYqj2(xyml, xzfl, number, name, ibaMap, containerName, viewId));
				}
			} else if ("原材料".equals(type) && xzfl.startsWith("03")) {
				// 选用目录与零部件编号关系
				list = SjzykDBUtil.queryJscl(xyml, xzfl, number, name, ibaMap, containerName, viewId);

				// 选用目录与目录条目关联，目录条目与零部件编号关系
				if (xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
					list.addAll(SjzykDBUtil.queryJscl2(xyml, xzfl, number, name, ibaMap, containerName, viewId));
				}
			} else if ("原材料".equals(type) && xzfl.startsWith("04")) {
				// 选用目录与零部件编号关系
				list = SjzykDBUtil.queryFjscl(xyml, xzfl, number, name, ibaMap, containerName, viewId);

				// 选用目录与目录条目关联，目录条目与零部件编号关系
				if (xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
					list.addAll(SjzykDBUtil.queryFjscl(xyml, xzfl, number, name, ibaMap, containerName, viewId));
				}
			} else if ("原材料".equals(type) && xzfl.startsWith("05")) {
				// 选用目录与零部件编号关系
				list = SjzykDBUtil.queryFhcl(xyml, xzfl, number, name, ibaMap, containerName, viewId);

				// 选用目录与目录条目关联，目录条目与零部件编号关系
				if (xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
					list.addAll(SjzykDBUtil.queryFhcl2(xyml, xzfl, number, name, ibaMap, containerName, viewId));
				}
			} else if ("原材料".equals(type) && "全部".equals(xzfl)) {
				// 只有材料定额时才有"全部"选项，标识所有的"金属材料"，"非金属材料"，"复合材料"
				list = SjzykDBUtil.queryJscl(xyml, xzfl, number, name, ibaMap, "八院金属材料库", viewId);
				if (xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
					list.addAll(SjzykDBUtil.queryJscl2(xyml, xzfl, number, name, ibaMap, "八院金属材料库", viewId));
				}

				list.addAll(SjzykDBUtil.queryFjscl(xyml, xzfl, number, name, ibaMap, "八院非金属材料库", viewId));
				if (xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
					list.addAll(SjzykDBUtil.queryFjscl2(xyml, xzfl, number, name, ibaMap, "八院非金属材料库", viewId));
				}

				list.addAll(SjzykDBUtil.queryFhcl(xyml, xzfl, number, name, ibaMap, "八院复合材料库", viewId));
				if (xyml != null && !"".equals(xyml) && !xyml.equals("全部")) {
					list.addAll(SjzykDBUtil.queryFhcl2(xyml, xzfl, number, name, ibaMap, "八院复合材料库", viewId));
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 工艺编辑器中工艺定额，材料定额查询资源库数据接口 通过指定的条件查询数据并返回数据集合
	 *
	 * @param xyml
	 *            选用目录
	 * @param xzfl
	 *            选择分类
	 * @param number
	 *            编号
	 * @param name
	 *            名称
	 * @param ibaMap
	 *            IBA属性
	 * @return List<SjzykBean> 数据集合
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public static List<SjzykBean> queryData2(String type, String xyml, String xzfl, String number, String name, Map<String, String> ibaMap, String containerName) throws RemoteException,
			InvocationTargetException {

		if (number != null && !"".equals(number) && !"null".equals(number)) {
			ibaMap.put("wtpartnumber", number);
		}
		if (name != null && !"".equals(name) && !"null".equals(name)) {
			ibaMap.put("name", name);
		}

		List<SjzykBean> list = SjzykDBUtil.querySjzykData(type, xyml, xzfl, ibaMap);
		list = processData(list);

		return list;
	}

	/**
	 * 处理编号相同而使用范围不同的数据，将范围以逗号分隔合并。
	 *
	 * @param list
	 */
	private static List<SjzykBean> processData(List<SjzykBean> list) {
		Map<String, SjzykBean> map = new HashMap<String, SjzykBean>();
		if (list != null && !list.isEmpty()) {
			String number = "";
			String scope = "";
			for (SjzykBean bean : list) {
				number = bean.getSjbm();
				if (map.containsValue(number)) {
					scope = bean.getSyfw();
					map.get(number).setSyfw(scope + "," + map.get(number).getSyfw());
				} else {
					map.put(number, bean);
				}
			}
		}

		List<SjzykBean> newList = new ArrayList<SjzykBean>();
		newList.addAll(map.values());

		return newList;
	}

	/**
	 * 读取每种类型零部件的属性
	 *
	 * @return map
	 */
	public static Map<String, List<Map<String, String>>> getAttributes() {
		if(ProcessCache.getAttributes!=null){
			return ProcessCache.getAttributes;
		}else{

			ProcessCache.getAttributes = ReadAttributesExcelHelper.readXls();
			return ProcessCache.getAttributes;


		}

	}

	/**
	 * 通过输入的前缀值查询相似的所有的值
	 *
	 * @param tableColName
	 *            中间表QUERYMIDDLE_TABLE的列名
	 * @param prefix
	 *            输入的前缀
	 * @return List<String>
	 */
	public static List<String> getValues(String tableColName) {
		System.out.println("getValues()  tableColName:" + tableColName);
		List<String> list = new ArrayList<String>();
		/*try {
			Connection conn = OracleDataSource.getOracleDataSource().getConnection();
			conn.setAutoCommit(false);
			StringBuffer selectSQL = new StringBuffer();
			selectSQL.append("select t.").append(tableColName).append(" from QUERYMIDDLE_TABLE t ");
			System.out.println(selectSQL.toString());
			PreparedStatement ps = conn.prepareStatement(selectSQL.toString());
			ResultSet resultset = ps.executeQuery();
			String value = "";
			while (resultset.next()) {
				value = resultset.getString(1);
				if (!list.contains(value)) {
					list.add(value);
				}
			}
			if(resultset!=null){
				resultset.close();
			}
			if(ps!=null){
				ps.close();
			}
			if(conn!=null){
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/

		return list;
	}

	/**
	 * 创建报表类工艺文件时获取工艺文件后五位顺序号
	 *
	 * @param userName
	 * @param type
	 * @param pindex
	 * @return
	 */
	public static String getReportTechnicsSequenceNumber(String userName, String type, String pindex) {
		int number = 1001;
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			StringBuffer selectSQL = new StringBuffer();
			if (pindex != null && !"".equals(pindex)) {
				selectSQL.append("select t.sno from SEQUENCENUMBER_TABLE t where t.pindex='" + pindex + "' and t.technicstype='" + type + "' order by t.sno desc");
			} else {
				selectSQL.append("select t.sno from SEQUENCENUMBER_TABLE t where t.technicstype='" + type + "' order by t.sno desc");
			}

			ResultSet resultset = conn.executeQuery(selectSQL.toString());
			if (resultset.next()) {
				number = resultset.getInt(1) + 1;
			}

			selectSQL = new StringBuffer();
			selectSQL.append("INSERT INTO SEQUENCENUMBER_TABLE values(" + number + ",'" + pindex + "','" + type + "','" + userName + "')");
			conn.executeUpdate(selectSQL.toString());

			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return String.valueOf(number);
	}

	/**
	 * 从配置文件XML获取所有报表类工艺类型及其所有属性
	 *
	 * @return Map<String , List < List < String>>> key:工艺名称
	 *         value：List<List<String>>每一个工艺类型的所有属性,List<String>每一个属性参数
	 */
	public static Map<String, List<List<String>>> getReportMPMPPlanAttrByXML() {
		Map<String, List<List<String>>> map = new HashMap<String, List<List<String>>>();
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			String dir = pro.getProperty("wt.codebase.location");
			String fileName = dir + File.separator + "report_mpmpplan_attributes.xml";
			List<List<String>> typeList = null;
			List<String> attrList = null;
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(fileName));
			Element rootElement = document.getRootElement();
			Iterator iterator = rootElement.elementIterator();
			while (iterator.hasNext()) {
				Element childElement = (Element) iterator.next();
				String name = childElement.attributeValue("name");
				String number = childElement.attributeValue("number");
				Iterator iterator2 = childElement.elementIterator();
				typeList = new ArrayList<List<String>>();
				while (iterator2.hasNext()) {
					Element chlElement = (Element) iterator2.next();
					attrList = new ArrayList<String>();
					attrList.add(chlElement.attributeValue("name"));
					attrList.add(chlElement.attributeValue("display"));
					attrList.add(chlElement.attributeValue("dataFrom"));
					attrList.add(chlElement.attributeValue("isEditable"));
					typeList.add(attrList);
				}
				map.put(name + ":" + number, typeList);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>>>>>>>>map>>>>>>>>" + map);
		return map;
	}

	/**
	 * 从配置文件XML获取所有报表类工艺类型及其PDF输出时的模板ID
	 *
	 * @return Map<String , String>
	 */
	public static Map<String, String> getReportMPMPPlanFormIdByXML() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			WTProperties pro = WTProperties.getLocalProperties();
			String dir = pro.getProperty("wt.codebase.location");
			String fileName = dir + File.separator + "report_mpmpplan_attributes.xml";
			SAXReader reader = new SAXReader();
			Document document = reader.read(new File(fileName));
			Element rootElement = document.getRootElement();
			Iterator iterator = rootElement.elementIterator();
			while (iterator.hasNext()) {
				Element childElement = (Element) iterator.next();
				String name = childElement.attributeValue("name");
				String formId = childElement.attributeValue("formId");
				map.put(name, formId);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>>>>>>>>map>>>>>>>>" + map);
		return map;
	}

	/**
	 * 调用服务器端接口，查询该零部件及其所有子件下的所有的工艺文件。 将工艺文件的Element存放在List中返回
	 *
	 * @param oid
	 *            partOid
	 * @return List<Element> 工艺文件Element集合
	 * @throws RemoteException
	 * @throws InvocationTargetException
	 */
	public static List<Element> getAllTechnicsElementByPart(String oid) {
		List<Element> list = new ArrayList<Element>();
		try {
			Persistable per = WCUtil.getPersistable(oid);
			if (per instanceof WTPart) {
				WTPart rootPart = (WTPart) per;
				List<WTPart> allPart = new ArrayList<WTPart>();
				allPart.add(rootPart);
				DownloadTechnicsReportUtil.getAllChildPart(rootPart, allPart);
				List<Element> techList = null;
				String mtype = "";
				for (WTPart part : allPart) {
					mtype = IBAHelper.getIBAValue(part, "MTYPE");
					if (!"自制件".equals(mtype)) {
						continue;
					}

					System.out.println("------part----" + part.getNumber() + "  " + part.getViewName());
					techList = BomUtil.getTechnicsDocumentByPart(part, null, null);
					System.out.println("------techList----" + techList.size());
					list.addAll(techList);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static List<Element> getAllTechnicsElementByPart(String oid, String userName) {
		List<Element> list = new ArrayList<Element>();
		try {
			Persistable per = WCUtil.getPersistable("OR:wt.part.WTPart:" + oid);
			if (per instanceof WTPart) {
				WTPart rootPart = (WTPart) per;
				List<WTPart> allPart = new ArrayList<WTPart>();
				allPart.add(rootPart);
				DownloadTechnicsReportUtil.getAllChildPart(rootPart, allPart);
				List<Element> techList = null;
				String mtype = "";
				for (WTPart part : allPart) {
					mtype = IBAHelper.getIBAValue(part, "MTYPE");
					if (!"自制件".equals(mtype) && !"带料委外件".equals(mtype)) {
						continue;
					}

					System.out.println("------part----" + part.getNumber() + "  " + part.getViewName());
					techList = BomUtil.getTechnicsDocumentByPart(part, null, null,userName);
					System.out.println("------techList----" + techList.size());
					list.addAll(techList);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static WTPart getLatestPartByPartNumber(String partNumber) {
		WTPart newPart = null;
		try {
			newPart = WTPartUtil.getLatestPartByPartNumber(partNumber);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newPart;

	}

	public static String getPartCTypeByNumberAndView(String partNumber, String viewName) {
		WTPart newPart = null;
		try {
			newPart = WTPartUtil.getLatestPartByNumberAndView(partNumber, viewName);
			if(newPart != null) {
				return IBAHelper.getIBAValue(newPart, "CTYPE");
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";

	}

	public static WTPart getLatestMPartByPartNumber(String partNumber) {
		WTPart newPart = null;
		try {
			newPart = WTPartUtil.getLatestPartByNumberAndView(partNumber, Constants.planning);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newPart;

	}

	public static WTPart getMPartByNumberAndVersion(String partNumber, String partVersion) {
		WTPart part = null;
		try {
			part = WTPartUtil.getMPartByNumberAndVersion(partNumber, "Manufacturing", partVersion);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return part;
	}

	public static String getAllPartOid(String number) throws WTException {
		StringBuffer allOid = new StringBuffer("");
		System.out.println("number===" + number);
		WTPart part = null;
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, number, true), index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		while (qResult.hasMoreElements()) {
			part = (WTPart) qResult.nextElement();
			String partOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
			allOid.append(partOid);
			allOid.append(",");
		}
		return allOid.toString();
	}

	public static String getAllPartOidByOid(String oid) throws WTException {
		WTPart part = WTPartUtil.getPartByOid(Long.valueOf(oid));
		StringBuffer allOid = new StringBuffer("");
		QuerySpec qSpec = new QuerySpec(WTPart.class);
		qSpec.appendWhere(new SearchCondition(WTPart.class, WTPart.NUMBER, SearchCondition.EQUAL, part.getNumber(), true), index);
		QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
		while (qResult.hasMoreElements()) {
			part = (WTPart) qResult.nextElement();
			String partOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
			allOid.append(partOid);
			allOid.append(",");
		}
		return allOid.toString();
	}

	public static String getChangeNoByTechnics(String docNo) {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);

		QuerySpec qSpec;
		try {
			WTDocument doc = DocUtil.getDoc(docNo, false);
			if (doc == null) {
				return "";
			}
			WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
			Iterator it = coll.iterator();
			if (it.hasNext()) {
				WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
				return ecn.getNumber();
			}


		} catch (wt.query.QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}

		return "";

	}
	public static String[] getChangeNoAndBiaoJiByTechnics(String docNo,String version) {
		String[] ss = new String[2];
		ss[0]="";
		ss[1]="";
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);

		Set<String> changeNos = new HashSet<String>();
		try {
			String[] versionStr = version.split("\\.");
			//WTDocument doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberVersion(WTDocument.class,docNo, version);
			WTDocument doc = DocUtil.getDoc(docNo, false);
			if (doc == null) {
				return ss;
			}
			QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
			boolean isStart = false;
			boolean isRealEcn = false;
			while(qr2.hasMoreElements()) {
				WTDocument nowDoc = (WTDocument) qr2.nextElement();
				if(versionStr[0].equals(nowDoc.getVersionIdentifier().getValue())){
					isStart = true;
					isRealEcn = true;
				}
				if(isStart){
					WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(nowDoc);
					Iterator it = coll.iterator();
					if (it.hasNext()) {
						WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
						IBAHelper iba = new IBAHelper(ecn);
						String ecntype = iba.getIBAValue("ECNTYPE");
						if ("新增更改".equals(ecntype)) {
							ss[0]=ecn.getNumber();
							ss[1]="Z";
							return ss;
						}
						if ("作废更改".equals(ecntype)) {
							ss[0]=ecn.getNumber();
							ss[1]="F";
							return ss;
						}
						if ("正常更改".equals(ecntype) || "".equals(ecntype) || ecntype == null || "null".equals(ecntype)) {
							if(isRealEcn){
								ss[0]=ecn.getNumber();
								isRealEcn = false;
							}
							changeNos.add(ecn.getNumber());
						}

					}
				}
			}

		} catch (wt.query.QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		ss[1]= RomanConverter.intToRoman(changeNos.size());
		return ss;

	}
	public static String getChangeBiaoJiByTechnics(String docNo,String version) {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);

		QuerySpec qSpec;
		Set<String> changeNos = new HashSet<String>();
		try {
			if(version==null){
				return getChangeBiaoJiByTechnics(docNo);
			}
			String[] versionStr = version.split("\\.");
			//WTDocument doc = (WTDocument) CmExpImpSearchHelper.searchLatestIteratedByNumberVersion(WTDocument.class,docNo, version);
			WTDocument doc = DocUtil.getDoc(docNo, false);
			if (doc == null) {
				return "";
			}
			QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc.getMaster());
			boolean isStart = false;
			while(qr2.hasMoreElements()) {
				WTDocument nowDoc = (WTDocument) qr2.nextElement();
				if(versionStr.equals(nowDoc.getVersionIdentifier().getValue())){
					isStart = true;
				}
				if(isStart){
					WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
					Iterator it = coll.iterator();
					if (it.hasNext()) {
						WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
						IBAHelper iba = new IBAHelper(ecn);
						String ecntype = iba.getIBAValue("ECNTYPE");
						if ("新增更改".equals(ecntype)) {
							return "Z";
						}
						if ("作废更改".equals(ecntype)) {
							return "F";
						}
						if ("正常更改".equals(ecntype) || "".equals(ecntype) || ecntype == null || "null".equals(ecntype)) {
							changeNos.add(ecn.getNumber());
						}

					}
				}
			}

		} catch (wt.query.QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return RomanConverter.intToRoman(changeNos.size());
	}

	public static String getChangeBiaoJiByTechnics(String docNo) {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);

		QuerySpec qSpec;
		Set<String> changeNos = new HashSet<String>();
		try {
			WTDocument doc = DocUtil.getDoc(docNo, false);
			if (doc == null) {
				return "";
			}
			WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(doc);
			Iterator it = coll.iterator();
			if (it.hasNext()) {
				WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
				IBAHelper iba = new IBAHelper(ecn);
				String ecntype = iba.getIBAValue("ECNTYPE");
				if ("新增更改".equals(ecntype)) {
					return "Z";
				}
				if ("作废更改".equals(ecntype)) {
					return "F";
				}

				if ("正常更改".equals(ecntype) || "".equals(ecntype) || ecntype == null || "null".equals(ecntype)) {
					changeNos.add(ecn.getNumber());
				}

			}

			qSpec = new QuerySpec(MPMProcessPlan.class);
			int[] index = { 0 };
			SearchCondition sCondition = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, docNo);
			qSpec.appendWhere(sCondition, index);
			QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
			while (qResult.hasMoreElements()) {
				MPMProcessPlan p = (MPMProcessPlan) qResult.nextElement();
				QueryResult qr2 = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices(p);
				if (qr2.hasMoreElements()) {
					WTChangeOrder2 ecn = (WTChangeOrder2) qr2.nextElement();
					IBAHelper iba = new IBAHelper(ecn);
					String ecntype = iba.getIBAValue("ECNTYPE");
					if ("正常更改".equals(ecntype) || "".equals(ecntype) || ecntype == null || "null".equals(ecntype)) {
						changeNos.add(ecn.getNumber());
					}
				}
				WTCollection coll2 = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(p);
				Iterator it2 = coll2.iterator();
				if (it2.hasNext()) {
					WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it2.next()).getObject();
					IBAHelper iba = new IBAHelper(ecn);
					String ecntype = iba.getIBAValue("ECNTYPE");
					if ("正常更改".equals(ecntype) || "".equals(ecntype) || ecntype == null || "null".equals(ecntype)) {
						changeNos.add(ecn.getNumber());
					}
				}
			}

		} catch (wt.query.QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		/*
		 * I - 1 II - 2 III - 3 IV - 4 V – 5 VI - 6 VII – 7 VIII - 8 IX - 9 X –
		 * 10 XI – 11 XII – 12 XIII – 13 XIV – 14 XV – 15 XVI – 16
		 */
		return RomanConverter.intToRoman(changeNos.size());
	}

	public static boolean getRenwuLeiXing(String oid) throws WTException {

		// OR:ext.casc.process.ProcessTaskItem:2716535
		if (oid != null && !"null".equals(oid) && !"".equals(oid)) {

			String value = "OR:ext.casc.process.ProcessTaskItem:" + oid;
			wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
			WTReference reference = factory.getReference(value);
			if (reference != null && reference.getObject() != null) {
				ProcessTaskItem taskItem = (ProcessTaskItem) reference.getObject();
				if ("报表类工艺编制".equals(taskItem.getTaskItemName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static String getTaskItemName(String oid) throws WTException {
		if (oid != null && !"null".equals(oid) && !"".equals(oid)) {
			String value = "OR:ext.casc.process.ProcessTaskItem:" + oid;
			wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
			WTReference reference = factory.getReference(value);
			if (reference != null && reference.getObject() != null) {
				ProcessTaskItem taskItem = (ProcessTaskItem) reference.getObject();
				return taskItem.getTaskItemName();
			}
		}
		return null;

	}

	public static String getBaoBiaoLeiXing(String oid) throws WTException {
		String style = null;
		// OR:ext.casc.process.ProcessTaskItem:2716535
		if (oid != null && !"null".equals(oid) && !"".equals(oid)) {
			String value = "OR:ext.casc.process.ProcessTaskItem:" + oid;
			wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
			WTReference reference = factory.getReference(value);
			ProcessTaskItem taskItem = (ProcessTaskItem) reference.getObject();
			if ("报表类工艺编制".equals(taskItem.getTaskItemName())) {
				IBAUtility ibaUtil = new IBAUtility(taskItem);
				style = ibaUtil.getIBAValue("TECHNICSREPORTSTYLE");

			}
		}
		return style;
	}

	public static String getNumber(Integer type, String pre) {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getNumber";
			Class[] types = { Integer.class, String.class };
			Object[] vals = { type, pre };
		}
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		StringBuilder isql = new StringBuilder("insert into ");
		StringBuilder usql = new StringBuilder("update ");

		if (BBLGY == type) {
			sql.append("GL_BBLGY_SEQ ").append("WHERE PRE='").append(pre).append("'");
			isql.append(" GL_BBLGY_SEQ (PRE,NUM) values ('").append(pre).append("',");
			usql.append(" GL_BBLGY_SEQ set NUM=");
		}
		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				num = 10001;
				isql.append(num).append(")");
				conn.executeUpdate(isql.toString());
			} else {
				num = num + 1;
				usql.append(num).append(" WHERE PRE='").append(pre).append("'");
				conn.executeUpdate(usql.toString());
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Long.toString(num);
	}

	/**
	 * 报表类工艺获取流水号
	 *
	 * @param type
	 *            1:查询；2:更新
	 * @param pre
	 * @return
	 * @throws WTException
	 */
	public static String getSeqNumber(Integer type, String pre) throws WTException {
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		sql.append("GL_BBLGY_SEQ ").append("WHERE PRE='").append(pre).append("'");
		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			conn.start();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				num = 10001;
				if (2 == type) {
					// 更新
					StringBuilder insertSql = new StringBuilder();
					insertSql.append("INSERT INTO GL_BBLGY_SEQ(PRE,NUM) ")//
							.append("VALUES('").append(pre).append("',")//
							.append("'").append(num).append("')");
					conn.executeUpdate(insertSql.toString());
					conn.commit();
				}
			} else {
				num = num + 1;
				if (2 == type) {
					// 更新
					StringBuilder updateSql = new StringBuilder();
					updateSql.append("UPDATE GL_BBLGY_SEQ SET NUM='").append(num).append("'")//
							.append(" WHERE PRE='").append(pre).append("'");
					conn.executeUpdate(updateSql.toString());
					conn.commit();
				}
			}
		} catch (Exception e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			throw new WTException("更新报表类工艺流水号失败！");
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Long.toString(num);
	}

	public static String getJsxyNumber(Integer type, String pre) {
		if (!RemoteMethodServer.ServerFlag) {
			String method = "getNumber";
			Class[] types = { Integer.class, String.class };
			Object[] vals = { type, pre };
		}
		StringBuilder sql = new StringBuilder("select NUM FROM ");
		StringBuilder isql = new StringBuilder("insert into ");
		StringBuilder usql = new StringBuilder("update ");

		sql.append("GL_BBLGY_SEQ ").append("WHERE PRE='").append(pre).append("'");
		if (JSXY == type) {
			isql.append(" GL_BBLGY_SEQ (PRE,NUM) values ('").append(pre).append("',");
			usql.append(" GL_BBLGY_SEQ set NUM=");
		}
		DBConn conn = null;
		long num = 0;
		try {
			conn = new DBConn();
			ResultSet rs = conn.executeQuery(sql.toString());
			if (rs.next()) {
				num = rs.getLong("NUM");
			}
			if (num == 0) {
				num = 10001;
				isql.append(num).append(")");
				conn.executeUpdate(isql.toString());
			} else {
				num = num + 1;
				usql.append(num).append(" WHERE PRE='").append(pre).append("'");
				conn.executeUpdate(usql.toString());
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return Long.toString(num);
	}

	public static List<String> getProcessTaskItemByPartNumber2(String docNumber, String number, String user, String style) {
		List<ProcessTaskItem> list = new ArrayList<ProcessTaskItem>();
		List<String> resultList = new ArrayList<String>();
		String taskType = "";
		try {
			WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(docNumber);
			if(document != null) {
				String pplanType = IBAHelper.getIBAValue(document, "PPLANTYPE");
				if("正式工艺文件".equals(pplanType)){
					taskType = "工艺设计任务";
				} else if("临时工艺文件".equals(pplanType)) {
					taskType = "临时工艺任务";
				}
			}
			QuerySpec qs = new QuerySpec(ProcessTaskItem.class);
			SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			qs.appendAnd();
			qs.appendSearchCondition(new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.OWNER, SearchCondition.EQUAL, user, false));
			qs.appendAnd();
			qs.appendSearchCondition(new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.TASK_ITEM_STATE, SearchCondition.EQUAL, "正在进行", false));
			if(StrUtil.isNotEmpty(taskType)) {
				qs.appendAnd();
				sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.TASK_TYPE, SearchCondition.EQUAL, taskType, false);
				qs.appendSearchCondition(sc);
			}
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				ProcessTaskItem partMaster = (ProcessTaskItem) qr.nextElement();
				if (!"".equals(partMaster.getOwner()) && !"null".equals(partMaster.getOwner()) && partMaster.getOwner() != null) {
					if ("common".equals(style)) {
						if (partMaster.getOwner().equals(user) && partMaster.getTaskItemState().equals("正在进行")
								&& (partMaster.getTaskItemName().endsWith("编制") || partMaster.getTaskItemName().endsWith("任务")) && !partMaster.getTaskType().equals("报表类工艺任务")) {
							String docNum = "";
							try {
								IBAUtility iba = new IBAUtility(partMaster);
								docNum = iba.getIBAValue("PROCESSDOCNUM");

								if (docNum != null && !"".equals(docNum)) {
									WTDocument doc = WTDocumentUtil.getDocumentByNumber(docNum);
									if (doc == null) {
										list.add(partMaster);
									}
								} else {
									list.add(partMaster);
								}
							} catch (WTException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					} else if ("report".equals(style)) {
						if (partMaster.getOwner().equals(user) && partMaster.getTaskItemState().equals("正在进行")
								&& (partMaster.getTaskItemName().endsWith("编制") || partMaster.getTaskItemName().endsWith("任务")) && partMaster.getTaskType().equals("报表类工艺任务")) {
							list.add(partMaster);
						}
					}

				}
			}

			IBAUtility ibaUtility;
			ProcessTask processTask;
			for (ProcessTaskItem processTaskItem : list) {
				processTask = ProcessUtil.getProcessTask(processTaskItem.getProcessTaskId());
				ibaUtility = new IBAUtility(processTask);
				String relatedTech = ibaUtility.getIBAValue("relatedTech");
				wt.fc.ReferenceFactory refefence = new wt.fc.ReferenceFactory();
				String workItemOid = refefence.getReferenceString(processTaskItem);
				SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd");
				String endDate = dFormat.format(processTask.getEndDate());
				String result = processTaskItem.getNumber() + "  " + processTaskItem.getTaskItemName() + "(" + processTask.getCreatorFullName() + ")" + "(" + endDate + ")" + "@@" + workItemOid;
				if (docNumber.equals(relatedTech)) {
					resultList.add(0, result);
				} else {
					resultList.add(result);
				}
			}
		} catch (wt.query.QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultList;
	}

	public static List<ProcessTaskItem> getProcessTaskItemByPartNumber(String number, String user, String style) {
		ArrayList list = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(ProcessTaskItem.class);
			SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				ProcessTaskItem partMaster = (ProcessTaskItem) qr.nextElement();
				if (!"".equals(partMaster.getOwner()) && !"null".equals(partMaster.getOwner()) && partMaster.getOwner() != null) {
					if ("common".equals(style)) {
						if (partMaster.getOwner().equals(user) && partMaster.getTaskItemState().equals("正在进行")
								&& (partMaster.getTaskItemName().endsWith("编制") || partMaster.getTaskItemName().endsWith("任务")) && !partMaster.getTaskType().equals("报表类工艺任务")) {
							String docNum = "";
							try {
								IBAUtility iba = new IBAUtility(partMaster);
								docNum = iba.getIBAValue("PROCESSDOCNUM");

								if (docNum != null && !"".equals(docNum)) {
									WTDocument doc = WTDocumentUtil.getDocumentByNumber(docNum);
									if (doc == null) {
										list.add(partMaster);
									}
								} else {
									list.add(partMaster);
								}
							} catch (WTException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					} else if ("report".equals(style)) {
						if (partMaster.getOwner().equals(user) && partMaster.getTaskItemState().equals("正在进行")
								&& (partMaster.getTaskItemName().endsWith("编制") || partMaster.getTaskItemName().endsWith("任务")) && partMaster.getTaskType().equals("报表类工艺任务")) {
							list.add(partMaster);
						}
					}

				}
			}
		} catch (wt.query.QueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	public static List<ProcessTaskItem> getProcessTaskItemByPartNumber(String number, String user) {
		ArrayList list = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(ProcessTaskItem.class);
			SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				ProcessTaskItem partMaster = (ProcessTaskItem) qr.nextElement();
				if (partMaster.getOwner().equals(user) && partMaster.getTaskItemState().equals("正在进行") && (partMaster.getTaskItemName().endsWith("编制") || partMaster.getTaskItemName().endsWith("任务"))
						&& !partMaster.getTaskType().equals("报表类工艺任务")) {

					list.add(partMaster);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public static List<ProcessTaskItem> getProcessTaskItemByPartNumber(String number) {
		ArrayList list = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(ProcessTaskItem.class);
			SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				ProcessTaskItem partMaster = (ProcessTaskItem) qr.nextElement();
				list.add(partMaster);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;
	}

	public static String getProcessTaskItemOidByPartNumber(String number, String taskItem) {
		ArrayList list = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(ProcessTaskItem.class);
			SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			if (taskItem != null) {
				qs.appendAnd();
				SearchCondition sc2 = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.TASK_ITEM_NAME, SearchCondition.EQUAL, taskItem, false);
				qs.appendSearchCondition(sc2);
			}

			qs.appendAnd();
			SearchCondition sc3 = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.TASK_ITEM_STATE, SearchCondition.NOT_EQUAL, "已作废", false);
			qs.appendSearchCondition(sc3);

			// qs.appendAnd();
			// SearchCondition sc3 = new
			// SearchCondition(ProcessTaskItem.class,
			// ProcessTaskItem.TASK_ITEM_STATE, SearchCondition.EQUAL, "正在进行",
			// false);
			// qs.appendSearchCondition(sc3);

			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				ProcessTaskItem partMaster = (ProcessTaskItem) qr.nextElement();
				return partMaster.getPersistInfo().getObjectIdentifier().getId() + "";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String getProcessTaskItemOidByPartNumber(String number, String taskItem, String partVersion) {
		ArrayList list = new ArrayList();
		try {
			QuerySpec qs = new QuerySpec(ProcessTaskItem.class);
			SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);

			if (taskItem != null) {
				qs.appendAnd();
				SearchCondition sc2 = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.TASK_ITEM_NAME, SearchCondition.EQUAL, taskItem, false);
				qs.appendSearchCondition(sc2);
			}
			qs.appendAnd();
			SearchCondition sc3 = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.TASK_ITEM_STATE, SearchCondition.NOT_EQUAL, "已作废", false);
			qs.appendSearchCondition(sc3);

			if (partVersion != null && partVersion.contains(".")) {
				String[] s = partVersion.split("\\.");
				if (s.length >= 1) {
					String version = s[0];
					if (version != null && !"".equals(version)) {
						qs.appendAnd();
						SearchCondition sc2 = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.VERSION, SearchCondition.LIKE, version + "%", false);
						qs.appendSearchCondition(sc2);
					}
				}
			}
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				ProcessTaskItem partMaster = (ProcessTaskItem) qr.nextElement();
				return partMaster.getPersistInfo().getObjectIdentifier().getId() + "";
			} else {
				return getProcessTaskItemOidByPartNumber(number, taskItem);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static JSONObject getFilePrintJson(WTObject pbo) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		if(pbo instanceof  WTDocument){
			WTDocument doc =(WTDocument)pbo;
			GLFilePrintData filePrintData = GLFilePrintDataHelper.getObject(doc);
			if(filePrintData!=null){
				return new JSONObject(filePrintData.getPrintData());
			}
		}
		QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
		if (qrProcs.hasMoreElements()) {
			WfProcess proc = (WfProcess) qrProcs.nextElement();
			Hashtable hashtable = PrintHelper.getPrintInfo(pbo, proc);
			Set<Map.Entry<String, Hashtable<String, String>>> entrys = hashtable.entrySet();
			for(Map.Entry<String, Hashtable<String, String>> entry:entrys) {
				Hashtable<String, String> signInfoTable = entry.getValue();
				JSONObject signInfo = new JSONObject(signInfoTable);
				return signInfo;
			}
		}
		SessionServerHelper.manager.setAccessEnforced(enforce);
		return null;
	}

	public static Hashtable getFilePrintinf(WTObject pbo) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		QueryResult qrProcs = WfEngineHelper.service.getAssociatedProcesses(pbo, null, null);
		if (qrProcs.hasMoreElements()) {
			WfProcess proc = (WfProcess) qrProcs.nextElement();

			Hashtable hashtable = PrintHelper.getPrintInfo(pbo, proc);

			return hashtable;

		}
		SessionServerHelper.manager.setAccessEnforced(enforce);
		return null;
	}
	/*
	 * data:2015.12.4 author:chenming 更具文档编号和版本获得文档，遍历文档并找出这些文档中是否有外协技术协议
	 */
	public static Boolean getDocRefStyle(String number, String version) {
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				String space = doc.getVersionInfo().getIdentifier().getValue() + "." + doc.getIterationInfo().getIdentifier().getValue();
				if (space.equals(version)) {
					QueryResult result = PersistenceHelper.manager.navigate(doc, WTDocumentDependencyLink.DEPENDS_ON_ROLE, WTDocumentDependencyLink.class, true);
					while (result.hasMoreElements()) {
						WTDocument refDoc = (WTDocument) result.nextElement();
						String style = TypeHelper.getLocalizedTypeString(refDoc, Locale.CHINA);
						if ("工艺技术协议".equals(style)) {
							return true;
						}
					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;

	}

	public static ArrayList getAllJsxyByContainer(String number, String name) throws WTException {

		ArrayList list = new ArrayList();
		QuerySpec qs = new QuerySpec(WTDocument.class);
		if (number != null && !"".equals(number) && !"null".equals(number)) {
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, "%" + number + "%", true));
		}
		if (name != null && !"".equals(name) && !"null".equals(name)) {
			if (number != null && !"".equals(number) && !"null".equals(number)) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + name + "%", true));
			} else {
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + name + "%", true));
			}
		}
		QueryResult qr = PersistenceHelper.manager.find(qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument doc = (WTDocument) qr.nextElement();
			String style = TypeHelper.getLocalizedTypeString(doc, Locale.CHINA);
			if ("工艺技术协议".equals(style)) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("number", doc.getNumber());
				map.put("name", doc.getName());
				map.put("version", doc.getVersionInfo().getIdentifier().getValue() + "." + doc.getIterationInfo().getIdentifier().getValue());
				list.add(map);
			}

		}
		return list;

	}

	public static String setDocumentAttachment(byte[] bytes, String number, String name, String jsxyNumber, Map map) throws WTException, FileNotFoundException, PropertyVetoException, IOException {
		String xhjh = "";
		String dept = (String) map.get("DEPT");
		String mindex = (String) map.get("MINDEX");
		String pindex = (String) map.get("PINDEX");
		String prtdex = (String) map.get("PRTDEX");
		String prtname = (String) map.get("PRTNAME");
		String phase_code = (String) map.get("PHASE_CODE");
		String batch = (String) map.get("BATCH");
		String secret = (String) map.get("SECRET");
		String name1 = (String) map.get("name");
		WTContainer docContainer;
		String docNumber = null;
		// TypeDefinitionReference tdr =
		// ClientTypedUtility.getTypeDefinitionReference("casc.sast.149.TECHNOLOGY_AGREEMENT");
		QuerySpec qs = new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
		qs.appendSearchCondition(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		LatestConfigSpec lcs = new LatestConfigSpec();
		qr = lcs.process(qr);
		while (qr.hasMoreElements()) {
			WTDocument doc = (WTDocument) qr.nextElement();
			docContainer = doc.getContainer();
			IBAHelper ibaHelper = new IBAHelper((IBAHolder) docContainer);
			xhjh = ibaHelper.getIBAValue("XHJH");
			if (!"".equals(xhjh) & !"null".equals(xhjh) & xhjh != null) {
				jsxyNumber = "RZ/" + xhjh + "-JX-";
				docNumber = getJsxyNumber(2, jsxyNumber);
				docNumber = jsxyNumber + docNumber;
				WTDocument wtdoc = WTDocumentUtil.createDocument(docNumber, name1, docContainer, "/Default/02工艺文件/14技术协议", "casc.sast.149.TECHNOLOGY_AGREEMENT");
				wtdoc = WTDocumentUtil.setPrimaryForDocument(wtdoc, name, bytes);
				// WTDocumentMaster master = (WTDocumentMaster)
				// wtdoc.getMaster();
				// WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity)
				// master.getIdentificationObject();
				// idy.setName(name1);
				IBAHelper iba = new IBAHelper(wtdoc);
				iba.setIBAValue("DEPT", dept);
				iba.setIBAValue("MINDEX", mindex);
				iba.setIBAValue("PINDEX", pindex);
				iba.setIBAValue("PRTDEX", prtdex);
				iba.setIBAValue("PRTNAME", prtname);
				iba.setIBAValue("PHASE_CODE", phase_code);
				iba.setIBAValue("BATCH", batch);
				iba.setIBAValue("SECRET", secret);
				iba.updateAttributeContainer(wtdoc);
				iba.updateIBAHolder(wtdoc);
				// wtdoc = (WTDocument)
				// PersistenceHelper.manager.refresh(wtdoc);
				// RelatedObjectsCommand.createDocDependency(doc, wtdoc);
				WTDocumentDependencyLink link = WTDocumentDependencyLink.newWTDocumentDependencyLink(doc, wtdoc);
				PersistenceServerHelper.manager.insert(link);
				return docNumber;
			} else {
				return "";
			}

		}

		return docNumber;
		// TODO Auto-generated method stub

	}

	/*
	 * author:chenming data:2015.12.10
	 *
	 * *
	 */
	public static Boolean hasGengggaiProcessDoc(String number, String version, String create) {
		try {
			String[] ss = version.split("\\.");
			WTPart part = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, number, ss[0], "Manufacturing");

			if(part != null){
				QueryResult qr = RelatedChangesQueryCommands.getRelatedAffectingChangeNotices(part);
				if (qr.hasMoreElements()) {
					WTChangeOrder2 ecn = (WTChangeOrder2) qr.nextElement();
					if(ecn != null){
						String state = ecn.getState().getState().getDisplay(Locale.CHINA);
						if(("正在工作").equals(state) || ("修改中").equals(state)){
							return true;
						}
					}
				}

				QueryResult documents = WTPartHelper.service.getDescribedByDocuments(part);
				LatestConfigSpec lcs = new LatestConfigSpec();
				documents = lcs.process(documents);
				while (documents.hasMoreElements()) {
					Object obj = documents.nextElement();
					if (obj instanceof WTDocument) {
						WTDocument doc = (WTDocument) obj;
						doc = (WTDocument) VersionControlHelper.service.getLatestIteration(doc, true);
						String name = doc.getModifierName();
						String state = doc.getState().getState().getDisplay(Locale.CHINA);
						if (!"space".equals(doc.getVersionInfo().getIdentifier().getValue()) && create.equals(name) && ("正在工作".equals(state) || "修改中".equals(state))) {
							return true;
						}
					}
				}
			}

			/*
			 * QuerySpec qs = new QuerySpec(WTPart.class); SearchCondition sc =
			 * new SearchCondition(WTPart.class, WTPart.NUMBER,
			 * SearchCondition.EQUAL, number, false);
			 * qs.appendSearchCondition(sc); QueryResult qr =
			 * PersistenceHelper.manager.find(qs); LatestConfigSpec lcs = new
			 * LatestConfigSpec(); qr = lcs.process(qr); if
			 * (qr.hasMoreElements()) { WTPart part = (WTPart) qr.nextElement();
			 * if (version.equals(part.getVersionInfo().getIdentifier()
			 * .getValue() + "." +
			 * part.getIterationInfo().getIdentifier().getValue())) {
			 * QueryResult documents = WTPartHelper.service
			 * .getDescribedByDocuments(part); while
			 * (documents.hasMoreElements()) { WTDocument doc = (WTDocument)
			 * documents.nextElement(); String name = doc.getModifierName();
			 * String state = doc.getState().getState()
			 * .getDisplay(Locale.CHINA); if
			 * (!"space".equals(doc.getVersionInfo()
			 * .getIdentifier().getValue()) && create.equals(name) &&
			 * ("正在工作".equals(state) || "修改中".equals(state))) { return true; } }
			 * } }
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static Boolean HasSuccessCreateJsxyDoc(String number, String technicsNumber) {
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, technicsNumber, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				QuerySpec qs1 = new QuerySpec(WTDocument.class);
				SearchCondition sc1 = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
				qs1.appendSearchCondition(sc1);
				QueryResult qr1 = PersistenceHelper.manager.find(qs1);
				LatestConfigSpec lcs = new LatestConfigSpec();
				qr1 = lcs.process(qr1);
				if (qr1.hasMoreElements()) {
					WTDocument wtdoc = (WTDocument) qr1.nextElement();
					WTDocumentDependencyLink link = WTDocumentDependencyLink.newWTDocumentDependencyLink(doc, wtdoc);
					PersistenceServerHelper.manager.insert(link);
					return true;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;

	}

	public static Map getJsxyNameAndVersionByNumber(String number) {
		Map map = new HashMap<String, String>();
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				map.put("name", doc.getName());
				map.put("version", doc.getVersionInfo().getIdentifier().getValue() + "." + doc.getIterationInfo().getIdentifier().getValue());
				return map;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return map;
		// TODO Auto-generated method stub

	}

	/**
	 * 根据document的number获得WTDocument，最后获得该document获得所有的技术协议 author：mchen
	 * date：2016.1.27
	 */
	public static ArrayList<Map<String, String>> getJsxyDocByTechnicsNumber(String number) {
		ArrayList list = new ArrayList<Map<String, String>>();
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				QueryResult result = PersistenceHelper.manager.navigate(doc, WTDocumentDependencyLink.DEPENDS_ON_ROLE, WTDocumentDependencyLink.class, true);
				LatestConfigSpec lsc = new LatestConfigSpec();
				result = lsc.process(result);
				while (result.hasMoreElements()) {
					WTDocument refDoc = (WTDocument) result.nextElement();
					refDoc = WTDocumentUtil.getLatestDocumentByNumber(refDoc.getNumber());
					String style = TypeHelper.getLocalizedTypeString(refDoc, Locale.CHINA);
					Map map = new HashMap<String, String>();
					if ("工艺技术协议".equals(style)) {
						map.put("number", refDoc.getNumber());
						map.put("name", refDoc.getName());
						map.put("version", refDoc.getVersionInfo().getIdentifier().getValue() + "." + refDoc.getIterationInfo().getIdentifier().getValue());
					}
					list.add(map);
				}
				return list;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return list;

	}

	public static String getLifeStateByDocNumber(String number) {
		String state = "";
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				state = doc.getState().getState().getDisplay(Locale.CHINA);
				return state;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return state;

	}

	public static Boolean setJsxyDocPrimay(byte[] bytes, String number, String fileName) {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				doc = WTDocumentUtil.setPrimaryForDocument(doc, fileName, bytes);

				Folder folder = WorkInProgressHelper.service.getCheckoutFolder();
				CheckoutLink checkoutLink = WorkInProgressHelper.service.checkout(doc, folder, "");
				doc = (WTDocument) checkoutLink.getWorkingCopy();
				WorkInProgressHelper.service.checkin(doc, "");
				// Versioned result =
				// VersionControlHelper.service.newVersion((Versioned) doc);
				// doc = (WTDocument)
				// PersistenceHelper.manager.save((Persistable) result);
				SessionServerHelper.manager.setAccessEnforced(enforce);
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		SessionServerHelper.manager.setAccessEnforced(enforce);
		return false;

	}

	public static Boolean HasJsxyDocXiuDing(String number) {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			boolean flag = false;
			WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(number);
			String state = doc.getLifeCycleState().getLocalizedMessage(Locale.CHINA);
			if ("已批准".equals(state)) {
				Versioned result = VersionControlHelper.service.newVersion((Versioned) doc);
				doc = (WTDocument) PersistenceHelper.manager.save((Persistable) result);
				SessionServerHelper.manager.setAccessEnforced(enforce);
				return true;
			} else {
				SessionServerHelper.manager.setAccessEnforced(enforce);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		SessionServerHelper.manager.setAccessEnforced(enforce);
		return false;
	}

	public static Boolean decompressZip1(String number) {

		try {
			boolean flag = false;
			WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(number);

			Map<String, String> variablesMap = new HashMap<String, String>();
			flag = WorkflowUtil.startProcess(doc, WorkflowConstants.WAIXIEJISHUXIEYIQIANSHENLIUCHENG, number, variablesMap);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * author:chenming date:2016.1.13 根据oid找到文档类型
	 *
	 * @throws WTException
	 * @throws WTRuntimeException
	 */
	public static String getDocStyleByDocOid(String docOid) throws WTRuntimeException, WTException {
		wt.fc.ReferenceFactory factory = new wt.fc.ReferenceFactory();
		Persistable persistable = factory.getReference(docOid).getObject();
		if (persistable instanceof WTDocument) {
			WTDocument doc = (WTDocument) persistable;
			String style = TypeHelper.getLocalizedTypeString(doc, Locale.CHINA);
			return style;
		}
		return "";
	}

	/**
	 * author:mchen date:2016-1-27 comment:创建工装申请单
	 *
	 * @throws Exception
	 */
	public static Boolean CreateGongZhuangsqd(Map map) throws Exception {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		File file = (File) map.get("file");

		String bianhao = (String) map.get("bianhao");
		String mingcheng = (String) map.get("mingcheng");
		String xinghao = (String) map.get("xinghao");
		String chanpinxinghao = (String) map.get("chanpinxinghao");
		String shiyongbumen = (String) map.get("shiyongbumen");
		String chanpintuhao = (String) map.get("chanpintuhao");
		String chanpinmingcheng = (String) map.get("chanpinmingcheng");
		String gongxuhao = (String) map.get("gongxuhao");
		String gongxumingcheng = (String) map.get("gongxumingcheng");
		String gongzhuangleibie = (String) map.get("gongzhuangleibie");
		String zhizaoshuliang = (String) map.get("zhizaoshuliang");
		String shiyongshijian = (String) map.get("shiyongshijian");
		String jianyishejibumen = (String) map.get("jianyishejibumen");
		String yaoqiushejiwanchengshijian = (String) map.get("yaoqiushejiwanchengshijian");
		String yaoqiushengchanwanchengshijian = (String) map.get("yaoqiushengchanwanchengshijian");
		String shenqingyuanyin = (String) map.get("shenqingyuanyin");
		String jstj = (String) map.get("jstj");
		String technicsNumber = (String) map.get("technicsNumber");
		String version = (String) map.get("version");
		QuerySpec qs = new QuerySpec(WTDocument.class);
		SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, technicsNumber, false);
		qs.appendSearchCondition(sc);
		QueryResult qr = PersistenceHelper.manager.find(qs);
		while (qr.hasMoreElements()) {
			WTDocument doc = (WTDocument) qr.nextElement();
			String banben = doc.getVersionInfo().getIdentifier().getValue() + "." + doc.getIterationInfo().getIdentifier().getValue();
			if (banben.equals(version)) {
				WTContainer docContainer = doc.getContainer();
				WTDocument wtdoc = WTDocumentUtil.createDocument(bianhao, mingcheng, docContainer, "/Default/04工装数据/工装申请单", "casc.sast.149.GONGZHUANGSHENQINGDAN");

				if (file != null) {
					FileInputStream is;
					is = new FileInputStream(file);
					String name = file.getName().replace(".doc", "");
					byte[] bytes = IOUtils.toByteArray(is);
					wtdoc = WTDocumentUtil.setPrimaryForDocument(wtdoc, name, bytes);
				}
				wtdoc = (WTDocument) PersistenceHelper.manager.refresh(wtdoc);
				IBAHelper ibaHelper = new IBAHelper(wtdoc);
				ibaHelper.setIBAValue(wtdoc, "MINDEX", xinghao);
				ibaHelper.setIBAValue(wtdoc, "PINDEX", chanpinxinghao);
				ibaHelper.setIBAValue(wtdoc, "SHIYONGBUMEN", shiyongbumen);
				ibaHelper.setIBAValue(wtdoc, "CINDEX", chanpintuhao);
				ibaHelper.setIBAValue(wtdoc, "PNAME", chanpinmingcheng);
				ibaHelper.setIBAValue(wtdoc, "GONGXUHAO", gongxuhao);
				ibaHelper.setIBAValue(wtdoc, "GONGXUMINGCHENG", gongxumingcheng);
				ibaHelper.setIBAValue(wtdoc, "YAOQIUSHENGCHANWANCHENGSHIJIAN", yaoqiushengchanwanchengshijian);
				ibaHelper.setIBAValue(wtdoc, "FROCKTYPE", gongzhuangleibie);
				ibaHelper.setIBAValue(wtdoc, "ZHIZAOSHULIANG", zhizaoshuliang);
				ibaHelper.setIBAValue(wtdoc, "SHIYONGSHIJIAN", shiyongshijian);
				ibaHelper.setIBAValue(wtdoc, "JIANYISHEJIBUMEN", jianyishejibumen);
				ibaHelper.setIBAValue(wtdoc, "YAOQIUSHEJIWANCHENGSHIJIAN", yaoqiushejiwanchengshijian);
				ibaHelper.setIBAValue(wtdoc, "SHENQINGYUANYIN", zhizaoshuliang);
				ibaHelper.setIBAValue(wtdoc, "JSTJ", shiyongshijian);
				ibaHelper.updateAttributeContainer(wtdoc);
				ibaHelper.updateIBAHolder(wtdoc);
				wtdoc = (WTDocument) PersistenceHelper.manager.refresh(wtdoc);
				WTDocumentDependencyLink link = WTDocumentDependencyLink.newWTDocumentDependencyLink(doc, wtdoc);
				PersistenceServerHelper.manager.insert(link);
				SessionServerHelper.manager.setAccessEnforced(enforce);
				return true;
			}

		}

		SessionServerHelper.manager.setAccessEnforced(enforce);
		return false;

	}

	public static ArrayList<Map<String, String>> getGzsqdDocByTechnicsNumber(String number) {
		ArrayList list = new ArrayList<Map<String, String>>();
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				QueryResult result = PersistenceHelper.manager.navigate(doc, WTDocumentDependencyLink.DEPENDS_ON_ROLE, WTDocumentDependencyLink.class, true);
				LatestConfigSpec lsc = new LatestConfigSpec();
				result = lsc.process(result);
				while (result.hasMoreElements()) {
					WTDocument refDoc = (WTDocument) result.nextElement();
					refDoc = WTDocumentUtil.getLatestDocumentByNumber(refDoc.getNumber());
					String style = TypeHelper.getLocalizedTypeString(refDoc, Locale.CHINA);
					Map map = new HashMap<String, String>();
					if ("工装申请单".equals(style)) {
						map.put("number", refDoc.getNumber());
						map.put("name", refDoc.getName());
						map.put("version", refDoc.getVersionInfo().getIdentifier().getValue() + "." + refDoc.getIterationInfo().getIdentifier().getValue());
					}
					list.add(map);
				}
				return list;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return list;

	}

	/**
	 * detail:根据产品的名称获得contain，然后根据contain，取得型号简号这个软属性
	 *
	 * @throws WTException
	 */
	public static String getXhjhByContainer(String name) throws WTException {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		String xhjh = "";

		// WTContainer container = WTContainerUtil.getProductByName(name);
		WTPart part = WTPartUtil.getPartByNumberAndView(name, Constant.PBOM_VIEW);
		WTContainer container = part.getContainer();
		if (container != null) {
			IBAHelper ibaHelper = new IBAHelper((IBAHolder) container);
			xhjh = ibaHelper.getIBAValue("XHJH");
			SessionServerHelper.manager.setAccessEnforced(enforce);
			return xhjh;
		}
		SessionServerHelper.manager.setAccessEnforced(enforce);
		return xhjh;
	}

	public static Map<String, String> getJsxyDetailByJsxyNumber(String number) {
		Map map = new HashMap<String, String>();
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument refDoc = (WTDocument) qr.nextElement();
				String style = TypeHelper.getLocalizedTypeString(refDoc, Locale.CHINA);
				if ("工艺技术协议".equals(style)) {
					WTDocumentMaster master = (WTDocumentMaster) refDoc.getMaster();
					WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
					String name = idy.getName();
					String jsxyNumber = idy.getNumber();
					IBAHelper ibaHelper = new IBAHelper(refDoc);
					String bianzhibumen = ibaHelper.getIBAValue("DEPT");
					String xinghaodaihao = ibaHelper.getIBAValue("MINDEX");
					String chanpindaihao = ibaHelper.getIBAValue("PINDEX");
					String bujiandaihao = ibaHelper.getIBAValue("PRTDEX");
					String bujianmingcheng = ibaHelper.getIBAValue("PRTNAME");
					String jieduanbiaoji = ibaHelper.getIBAValue("PHASE_CODE");
					String pici = ibaHelper.getIBAValue("BATCH");
					String miji = ibaHelper.getIBAValue("SECRET");
					String creator = refDoc.getCreatorFullName();
					String state = refDoc.getLifeCycleState().getLocalizedMessage(Locale.CHINA);
					map.put("name", name);
					map.put("jsxyNumber", jsxyNumber);
					map.put("bianzhibumen", bianzhibumen);
					map.put("xinghaodaihao", xinghaodaihao);
					map.put("chanpindaihao", chanpindaihao);
					map.put("bujiandaihao", bujiandaihao);
					map.put("jieduanbiaoji", jieduanbiaoji);
					map.put("pici", pici);
					map.put("miji", miji);
					map.put("bujianmingcheng", bujianmingcheng);
					map.put("creator", creator);
					map.put("state", state);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

	public static Boolean downLoadJsxyDoc(String number, String path) {
		Boolean flagBoolean = false;
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				ApplicationData appData = (ApplicationData) ContentHelper.service.getPrimary(doc);
				byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);
				File src = new File(path);
				if (!src.exists()) {
					src.mkdirs();
				}
				OutputStream fos = null;
				String fileName = appData.getFileName();
				if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
					fos = new FileOutputStream(path + appData.getFileName());
				} else {
					fos = new FileOutputStream(path + appData.getFileName() + ".doc");
				}
				fos.write(bytes);
				fos.close();
				flagBoolean = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return flagBoolean;
	}

	public static String getAppDataByNumber(String number) {
		String fileName = "";
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				ApplicationData appData = (ApplicationData) ContentHelper.service.getPrimary(doc);
				fileName = appData.getFileName();
				return fileName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;

	}

	public static Boolean EditJsxyDoc(Map map, File file) {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		String number = (String) map.get("number");
		String name = (String) map.get("name");
		String jieduanbiaoji = (String) map.get("jieduanbiaoji");
		String pici = (String) map.get("pici");
		String miji = (String) map.get("miji");
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
				WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
				idy.setName(name);
				master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
				IBAHelper ibaHelper = new IBAHelper(doc);
				ibaHelper.setIBAValue(doc, "SECRET", miji);
				ibaHelper.setIBAValue(doc, "PHASE_CODE", jieduanbiaoji);
				ibaHelper.setIBAValue(doc, "BATCH", pici);
				ibaHelper.updateAttributeContainer(doc);
				ibaHelper.updateIBAHolder(doc);
				doc = (WTDocument) PersistenceHelper.manager.refresh(doc);

				if (file != null && file.exists()) {
					FileInputStream is;
					is = new FileInputStream(file);
					String fileName = file.getName().replace(".doc", "");
					byte[] bytes = IOUtils.toByteArray(is);
					doc = WTDocumentUtil.setPrimaryForDocument(doc, fileName, bytes);
				}
				SessionServerHelper.manager.setAccessEnforced(enforce);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		SessionServerHelper.manager.setAccessEnforced(enforce);
		return false;

	}

	public static Boolean deleteJsxyLink(String parentNumber, String number) {
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, parentNumber, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			if (qr.hasMoreElements()) {
				WTDocument parentDoc = (WTDocument) qr.nextElement();
				String name = parentDoc.getName();
				String versionString = parentDoc.getVersionIdentifier().getValue() + "." + parentDoc.getIterationIdentifier().getValue();
				QuerySpec qs1 = new QuerySpec(WTDocument.class);
				SearchCondition sc1 = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number, false);
				qs1.appendSearchCondition(sc1);
				QueryResult qr1 = PersistenceHelper.manager.find(qs1);
				LatestConfigSpec lcs11 = new LatestConfigSpec();
				qr1 = lcs11.process(qr1);
				if (qr1.hasMoreElements()) {
					WTDocument doc = (WTDocument) qr1.nextElement();
					WTDocumentDependencyLink wtDocumentDependencyLink = RelatedObjectsCommand.getWTDocumentDependencyLink(parentDoc, doc);
					PersistenceServerHelper.manager.remove(wtDocumentDependencyLink);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static Boolean hasZhuZhi(String partNumber) {
		try {
			WTPart part = WTPartUtil.getPartByNumberAndView(partNumber, Constants.planning);
			QueryResult qr = WTPartHelper.service.getDescribedByWTDocuments(part, true);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				WTDocument document = (WTDocument) qr.nextElement();
				// 排除报表类工艺
				if (TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("PROCESS_PLAN")
						&& !TypedUtilityServiceHelper.service.getTypeIdentifier(document).toString().contains("reportTechnics")) {
					// WTDocument doc =
					// (WTDocument)VersionControlHelper.service.getLatestIteration(document,
					// true);
					WTDocument doc = null;
					QueryResult qrdoc = VersionControlHelper.service.allIterationsOf(document.getMaster());
					if (qrdoc.hasMoreElements()) {
						doc = (WTDocument) qrdoc.nextElement();
					}
					IBAHelper iba = new IBAHelper(doc);
					String PPLANTYPE = iba.getIBAValue("PPLANTYPE");
					String ZFFLAG = iba.getIBAValue("ZFFLAG");
					String state = doc.getState().toString();
					if ("OBSOLESCENCE".equals(state)) {
						continue;
					}
					if ("正式工艺文件".equals(PPLANTYPE) && "Z".equals(ZFFLAG)) {
						return true;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}

	public static Boolean setTechnicsNumberBynumber(String technicsNumber, String pplanName, String pplanNumber) {
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, technicsNumber, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			if (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
				WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
				String name = pplanName + "(" + pplanNumber + ")";
				idy.setName(name);
				master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	public static Boolean setMPMPROCESSNameBynumber(String technicsNumber, String pplanName, String pplanNumber) {
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QuerySpec qs = new QuerySpec(MPMProcessPlan.class);
			SearchCondition sc = new SearchCondition(MPMProcessPlan.class, MPMProcessPlan.NUMBER, SearchCondition.EQUAL, technicsNumber, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			if (qr.hasMoreElements()) {
				MPMProcessPlan doc = (MPMProcessPlan) qr.nextElement();
				MPMProcessPlanMaster master = (MPMProcessPlanMaster) doc.getMaster();
				MPMProcessPlanMasterIdentity idy = (MPMProcessPlanMasterIdentity) master.getIdentificationObject();
				String name = pplanName + "(" + pplanNumber + ")";
				idy.setName(name);
				master = (MPMProcessPlanMaster) IdentityHelper.service.changeIdentity(master, idy);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}

		return false;

	}

	/**
	 * 根据工序的名称获得PDM中的工序主制车间 author:Mchen date:2016.4.25
	 */
	public static String getChejianByProceduceName(String name) {
		String chejian = "";
		try {
			QuerySpec qs = new QuerySpec(WTPart.class);
			SearchCondition sc = new SearchCondition(WTPart.class, WTPart.NAME, SearchCondition.EQUAL, name, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			if (qr.hasMoreElements()) {
				WTPart mpmar = (WTPart) qr.nextElement();
				IBAHelper ibaHelper = new IBAHelper(mpmar);
				chejian = ibaHelper.getIBAValue("ZZCJ");
				return chejian;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chejian;

	}

	/**
	 * 获取所有工序名称的相关信息 author：Mchen date：2016/4/29
	 *
	 * @throws WTException
	 */
	public static Map<String, String> getPdNameDescribe() throws WTException {
		String zzcj = "";
		Map<String, String> map = new HashMap<String, String>();
		WTContainer container = WTContainerUtil.getContainerByName(Constants.mpmResourceLibraryName);
		Folder folder = FolderUtil.getFolder(Constants.rootFolder + "/" + Constants.mpmResourceFolderName[8], WTContainerRef.newWTContainerRef(container));
		QueryResult result = FolderHelper.service.findFolderContents(folder, MPMTooling.class);
		while (result.hasMoreElements()) {
			MPMTooling tooling = (MPMTooling) result.nextElement();
			IBAHelper ibaHelper = new IBAHelper(tooling);
			zzcj = ibaHelper.getIBAValue("ZZCJ");
			map.put(tooling.getName(), zzcj);
		}
		return map;
	}

	public static WTPart getRelatedPartByReportTechnisName(String name) {
		WTPart part = null;
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.EQUAL, name, false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs1 = new LatestConfigSpec();
			qr = lcs1.process(qr);
			if (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(doc);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return part;
	}

	/**
	 * 根据工艺编号，获得工序element集合
	 *
	 * @param number
	 * @return
	 * @throws WTException
	 * @throws PropertyVetoException
	 * @throws DocumentException
	 */
	public static List<Element> getProceduresOfTechnic(String number) throws WTException, PropertyVetoException, DocumentException {
		WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(number);
		ApplicationData appData = WTDocumentUtil.getPrimaryByDocument(doc);
		byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);

		String tempFilePath = PropertiesUtil.getTempPath() + File.separator + "tempDinge" + File.separator + String.valueOf(new Date().getTime()) + File.separator;
		FileUtil.writeBytes(tempFilePath, appData.getFileName(), bytes);
		ApacheZipUtil.decompress(tempFilePath + appData.getFileName(), tempFilePath + number);
		File xmlFile = new File(tempFilePath + number + File.separator + number + ".xml");
		List<Element> procedureList = getAllProcedures(xmlFile);
		return procedureList;
	}

	/**
	 * 保存至主辅关联表
	 *
	 * @param fzTechnicsNumber
	 * @param zzTechnicsNumber
	 * @param procedureLink
	 * @return
	 * @throws WTException
	 */
	public static Boolean saveZhuFuLink(String fzTechnicsNumber, String fzVersion, String zzTechnicsNumber, String zzVersion, String picihao, Map<String, String> procedureLink) throws WTException {
		DBConnUtil conn = null;
		if (picihao == null || picihao.equals("")) {
			picihao = "无";
		}
		try {
			conn = new DBConnUtil();
			int index = 0;
			String deleteSql = "delete from GL_ZHUFULINK where FZTECHNICSNUMBER='" + fzTechnicsNumber + "' and FZTECHNICSVERSION='" + fzVersion +
			// "' and ZZTECHNICSNUMBER='"+ zzTechnicsNumber +
			// "' and ZZTECHNICSVERSION='" + zzVersion +
					"' and PICIHAO='" + picihao + "'";
			conn.executeUpdate(deleteSql);
			for (Map.Entry<String, String> entry : procedureLink.entrySet()) {
				String fzProcedureNumber = entry.getKey();
				String zzProcedureNumber = entry.getValue();

				String gwkey = UUID.randomUUID().toString();
				String tableNames = "GWKEY,FZTECHNICSNUMBER,FZTECHNICSVERSION,FZPROCEDURENUMBER,ZZTECHNICSNUMBER,ZZTECHNICSVERSION,ZZPROCEDURENUMBER,PICIHAO";
				String values = "'" + gwkey + "','" + fzTechnicsNumber + "','" + fzVersion + "','" + fzProcedureNumber + "','" + zzTechnicsNumber + "','" + zzVersion + "','" + zzProcedureNumber
						+ "','" + picihao + "'";
				String insertSql = "insert into GL_ZHUFULINK (" + tableNames + ") values (" + values + ")";

				conn.executeUpdate(insertSql);
				index++;
			}
			conn.commit();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				logger.error(e);
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断辅工艺是否关联主工艺
	 *
	 * @param fzTechnicsNumber
	 * @param fzVersion
	 * @param picihao
	 * @return
	 * @throws WTException
	 */
	public static Boolean hasMainMakeTechnics(String fzTechnicsNumber, String fzVersion, String picihao) throws WTException {
		DBConnUtil conn = null;
		if (picihao == null || picihao.equals("")) {
			picihao = "无";
		}
		try {
			conn = new DBConnUtil();
			// 主辅关联-原
			// String selectSql =
			// "select count(*) from GL_ZHUFULINK where FZTECHNICSNUMBER='" +
			// fzTechnicsNumber
			// + "' and FZTECHNICSVERSION='" + fzVersion
			// + "' and PICIHAO='" + picihao + "'";
			// 主辅关联-现
			String selectSql = "select count(*) from GL_ZHUFULINKMASTER where FZTECHNICSNUMBER='" + fzTechnicsNumber + "' and FZTECHNICSVERSION='" + fzVersion + "'";
			ResultSet rs = conn.executeQuery(selectSql);
			if (rs.next()) {
				int i = rs.getInt(1);
				if (i != 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 获得主辅工艺间工序对应map
	 *
	 * @param fzTechnicsNumber
	 * @param fzVersion
	 * @param picihao
	 * @return
	 */
	public static Map<String, String> getZhuFuLink(String fzTechnicsNumber, String fzVersion, String picihao, String zzTechnicsNumber, String zzTechnicsVersion) {
		Map<String, String> zhufuLink = new HashMap<String, String>();
		if (picihao == null || picihao.equals("")) {
			picihao = "无";
		}
		DBConnUtil conn = null;
		try {
			conn = new DBConnUtil();
			String sql = "select FZTECHNICSNUMBER,FZPROCEDURENUMBER,ZZTECHNICSNUMBER,ZZPROCEDURENUMBER from GL_ZHUFULINK where FZTECHNICSNUMBER='" + fzTechnicsNumber + "' and FZTECHNICSVERSION='"
					+ fzVersion + "' and PICIHAO='" + picihao + "'";
			// "' and ZZTECHNICSNUMBER='" + zzTechnicsNumber +
			// "' and ZZTECHNICSVERSION='" + zzTechnicsVersion + "'";
			ResultSet rs = conn.executeQuery(sql);
			while (rs.next()) {
				String fzProcedureNumber = rs.getString("FZPROCEDURENUMBER");
				String zzProcedureNumber = rs.getString("ZZPROCEDURENUMBER");
				String fzTechNumber = rs.getString("FZTECHNICSNUMBER");
				String zzTechNumber = rs.getString("ZZTECHNICSNUMBER");
				zhufuLink.put(fzProcedureNumber, zzProcedureNumber);
				zhufuLink.put(fzTechNumber, zzTechNumber);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return zhufuLink;
	}

	public static List<Element> getAllProcedures(File xmlFile) throws DocumentException {
		if (!xmlFile.exists()) {
			System.out.println(xmlFile + " is not exist!");
			return null;
		}
		SAXReader reader = new SAXReader();
		Document document = reader.read(xmlFile);
		Element rootElement = document.getRootElement();
		Element technics = rootElement.element("QMFawTechnicsInfo");
		if (technics == null) {
			return null;
		}
		// steps节点
		Element steps = technics.element("steps");
		// 工序节点
		List<Element> procedureList = steps.elements("QMProcedureInfo");
		return procedureList;
	}

	public static byte[] getPrintPdf(String number) {
		byte[] bytes = null;
		try {
			WTDocument document = WTDocumentUtil.getLatestDocumentByNumber(number);
			String name = document.getName();
			if (document != null) {
				ContentHolder holder = ContentHelper.service.getContents(document);
				Vector apps = ContentHelper.getApplicationData(holder);
				for (Enumeration e = apps.elements(); e.hasMoreElements();) {
					ApplicationData contentItem = (ApplicationData) e.nextElement();
					String applicationdataRole = contentItem.getRole().toString();
					if (!"SECONDARY".equalsIgnoreCase(contentItem.getRole().toString()))
						continue;// 不是附件
					if (contentItem.getFileName().startsWith("Print_")) {
						bytes = WTDocumentUtil.applicationDataToByte(contentItem);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bytes;
	}

	public static String getUsageLinkCountByNumber(String parentNumber, String childNumber) throws WTException {
		String gysl = "";
		WTPart parentPart = WTPartUtil.getPartByNumberAndView(parentNumber, Constant.PBOM_VIEW);
		WTPart childPart = WTPartUtil.getPartByNumberAndView(childNumber, Constant.PBOM_VIEW);
		if (parentPart != null && childPart != null) {
			WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(parentPart, (WTPartMaster) childPart.getMaster());
			if (link != null) {
				gysl = IBAHelper.getIBAValue(link, "GYSL");
			}
		}
		return gysl;
	}

	public static String getGYSLOfPart(String fPartNumber, String fPartVersion, String childNumber) throws WTException {
		String gysl = "1";
		WTPart parentPart = getPartByNumberAndVersion2(fPartNumber, fPartVersion);
		WTPart childPart = WTPartUtil.getPartByNumberAndView(childNumber, Constant.PBOM_VIEW);
		if (parentPart != null && childPart != null) {
			WTPartUsageLink link = WTPartUtil.getWTPartUsageLink(parentPart, (WTPartMaster) childPart.getMaster());
			if (link != null) {
				gysl = IBAHelper.getIBAValue(link, "GYSL");
			}
		}
		return gysl;
	}

	public static List<Map<String, String>> getHasPbomXmlParentPartIda2a2List(String partOid) {
		List<Map<String, String>> parentPartOidList = new ArrayList<Map<String, String>>();
		String parentPartOid = "";
		try {
			WTPart part = WTPartUtil.getPartByOid(Long.valueOf(partOid));
			String number = part.getNumber();
			parentPartOidList = WTPartUtil.getHasPbomXmlParentPartIda2a2List(part, parentPartOid, false);
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parentPartOidList;
	}

	public static String getEnglishNameByGxmc(String name) {
		String type = "com.ptc.windchill.mpml.resource.MPMTooling|casc.sast.149.ProceduceName";
		String englishName = "";
		try {
			MPMTooling mpmProcedure = MPMResourceUtil.getMPMToolingByName(name, type);
			IBAHelper helper = new IBAHelper(mpmProcedure);
			englishName = helper.getIBAValue("EnglishName");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return englishName;
	}

	public static Object[][] getReportDatas(List<String> partOids, String type) {
		try {

			return TenchnicsReportUtil.getReportDatas(partOids, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object[][] getReportDatas(String partOid, String type) {
		try {

			// List<String> partOids = new ArrayList<String>();
			// partOids.add(partOid);
			/*
			 * byte[] pbomBytes = getPBOMXmlRMI(partOid); if(pbomBytes!=null){
			 * getPartOids(pbomBytes,partOids); }
			 */

			WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
			if (part == null)
				return null;
			part = (WTPart) ProcessPlanHelper.searchLatestIteratedByNumberVersionView(WTPart.class, part.getNumber(), part.getVersionInfo().getIdentifier().getValue(), "Manufacturing");
			IBAUtility utility = new IBAUtility(part);
			String batch = utility.getIBAValue("BATCH");// 零部件批次
			List<WTPart> partList = new ArrayList<WTPart>();
			partList.add(part);

			getAllParts(part, partList, batch);
			return new TenchnicsReportUtil2().getReportDatas(partList, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void getAllParts(WTPart part, List<WTPart> partList, String batch) throws WTException {
		QueryResult qr = WTPartHelper.service.getUsesWTPartMasters(part);
		while (qr.hasMoreElements()) {
			WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
			WTPartMaster part1 = (WTPartMaster) link.getRoleBObject();
			WTPart latePart = null;
			latePart = getLatestPartByBatchView(part1, batch, "Manufacturing"); // 获得与父部件相同视图的子部件
			if (latePart != null) {
				if (!partList.contains(latePart)) {
					partList.add(latePart);
				}
				getAllParts(latePart, partList, batch);
			}

		}

	}

	public static WTPart getLatestPartByBatchView(Master master, String batchVersion, String viewName) throws WTException {
		QueryResult queryResult = VersionControlHelper.service.allIterationsOf(master);
		while (queryResult.hasMoreElements()) {
			Object object = queryResult.nextElement();
			if (object instanceof WTPart) {
				WTPart part = (WTPart) object;
				if (batchVersion == null || "".equals(batchVersion)) {// 如果父件没有批次，则获取最新M视图零件
					if ((part.getViewName().equals(viewName))) {
						return part;
					}
				} else {
					IBAUtility utility = new IBAUtility(part);
					String batch = utility.getIBAValue("BATCH");// 零部件批次
					if (batch == null)
						batch = "";
					if ((part.getViewName().equals(viewName)) && batch.equals(batchVersion)) {
						return part;
					}
				}

			}
		}
		return null;
	}

	public static Object[][] getReportDatas(String partOid, String type, String startType) {
		try {
			if ("cache".equals(startType)) {
				return TenchnicsReportUtil.getTempDatas(partOid, type);
			}
			List<String> partOids = new ArrayList<String>();
			partOids.add(partOid);
			byte[] pbomBytes = getPBOMXmlRMI(partOid);
			if (pbomBytes != null) {
				getPartOids(pbomBytes, partOids);
			}
			return TenchnicsReportUtil.getReportDatas(partOids, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object[][] getReportDatastemp(String partOid, String type) {
		try {
			List<String> partOids = new ArrayList<String>();
			partOids.add(partOid);
			byte[] pbomBytes = getPBOMXmlRMI(partOid);
			if (pbomBytes != null) {
				getPartOids(pbomBytes, partOids);
			}
			Object[][] os = TenchnicsReportUtil.getReportDatas(partOids, type);

			TenchnicsReportUtil.insertTempDatas((String[][]) os, type);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void getPartOids(byte[] pbomBytes, List<String> partOids) throws Exception {
		if (pbomBytes != null) {
			Document document = PbomUtil.getDocument(pbomBytes);
			Element rootElement = document.getRootElement();
			Element partRootElement = rootElement.element("parts");
			Element partElement = partRootElement.element("QMPartInfo");
			Element children = partElement.element("childs");
			if (children != null) {
				List<Element> childPartElements = children.elements("QMPartInfo");
				if (childPartElements != null && childPartElements.size() != 0) {
					for (Element e : childPartElements) {
						getPartOids(e, partOids);
					}
				}
			}
		}
	}

	private static void getPartOids(Element partElement, List<String> partOids) throws Exception {
		String oid = partElement.attributeValue("oid");
		if (!partOids.contains(oid)) {
			partOids.add(oid);
		}
		byte[] childbytes = getPBOMXmlRMI(oid);
		if (childbytes != null) {
			getPartOids(childbytes, partOids);
		} else {
			Element children = partElement.element("childs");
			if (children != null) {
				List<Element> childPartElements = children.elements("QMPartInfo");
				if (childPartElements != null && childPartElements.size() != 0) {
					for (Element e : childPartElements) {
						getPartOids(e, partOids);
					}
				}
			}
		}

	}

	/**
	 * 获取所有的工艺类型
	 *
	 * @return
	 * @throws InvocationTargetException
	 * @throws RemoteException
	 */
	public static Vector<String> getAllMPMSkill() throws RemoteException, InvocationTargetException {
		return MPMUtil.getAllMPMSkill();
	}

	public static Vector<String> getProductMindex() throws WTException {
		return WTContainerUtil.getAllProductMindex();
	}

	public static String getECNType(String id) {
		String oid = "OR:wt.change2.WTChangeOrder2:" + id;
		try {
			Object o = WCUtil.getPersistable(oid);
			if (o != null && o instanceof WTChangeOrder2) {
				WTChangeOrder2 ecn = (WTChangeOrder2) o;
				IBAHelper iba = new IBAHelper(ecn);
				String ecntype = iba.getIBAValue("ECNTYPE");
				return ecntype;
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取检验汇总表的地址
	 *
	 * @param technicsNumber
	 * @return
	 * @throws IOException
	 * @author jyx
	 * @date 2018-5-22
	 */
	public static String getshowCheckOutTableUrl(String technicsNumber) throws IOException {
		String urlBase = WTProperties.getLocalProperties().getProperty("java.rmi.server.hostname");
		String webAPP = WTProperties.getLocalProperties().getProperty("wt.webapp.name");
		String url = "http://" + urlBase + "/" + webAPP;
		url += "/netmarkets/jsp/ext/glaway/mpm/checkTable/showCheckOutTable2.jsp?technicsNumber=" + technicsNumber;
		return url;
	}

	/***
	 * 更新主内容
	 *
	 * @param bytes
	 * @param technicsNumber
	 * @param version
	 * @return
	 */
	public static Boolean uploadPrimaryOfDocument(byte[] bytes, String technicsNumber, String version) {
		boolean flag = false;
		boolean flg = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			WTDocument document = WTDocumentUtil.getDocumentByNumberAndVersion(technicsNumber, version);
			ApplicationData app = WTDocumentUtil.getPrimaryByDocument(document);
			WTPrincipalReference principal = app.getModifiedBy();
			WTDocumentUtil.setPrimaryForDocument(document, technicsNumber + ".zip", bytes);
			document = (WTDocument) PersistenceHelper.manager.refresh(document);
			app = WTDocumentUtil.getPrimaryByDocument(document);
			app.setModifiedBy(principal);
			// PersistenceHelper.manager.save(document);
			PersistenceServerHelper.manager.update(app);
			flag = true;
		} catch (Exception e) {
			flag = false;
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flg);
		}
		return flag;
	}

	public static String checkTechnicsIsLatest(Map<String, String> technicsMap) {
		StringBuffer sb = new StringBuffer();
		try {
			if (technicsMap != null) {
				for (Map.Entry<String, String> entry : technicsMap.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					WTDocument doc = WTDocumentUtil.getDocumentByNumber(key);
					String version = doc.getIterationDisplayIdentifier().toString();
					if (!version.equals(value)) {
						sb.append(doc.getName() + "的最新版本为：" + version + " 与目录中版本：" + value + " 不一致\n");
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static List<CmTreeNode> getPartRelatedTechnics(String partNumber, String partVersion) throws Exception {
		List<CmTreeNode> cmTreeNodeList = new ArrayList<CmTreeNode>();
		Map<String, String> ibaMap = new HashMap<String, String>();
		CmTreeNode cmTreeNode = null;
		WTPart part = getPartByNumberAndVersion(partNumber, partVersion);
		List<WTDocument> documentList = WTPartUtil.getDescribedDocumentByPart(part, "casc.sast.149.PROCESS_PLAN");
		for (WTDocument document : documentList) {
			String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
			if (docType.contains("casc.sast.149.reportTechnics")) {
				continue;
			}
			String number = document.getNumber();
			String name = document.getName();
			String version = document.getIterationDisplayIdentifier().toString();
			String phase = IBAHelper.getIBAValue(document, "PHASE_CODE");
			String keycomponent = IBAHelper.getIBAValue(document, "KEYCOMPONENT");
			String zzcj = IBAHelper.getIBAValue(document, "ZZCJ");
			String enditemin = IBAHelper.getIBAValue(document, "ENDITEMIN");
			String mindex = IBAHelper.getIBAValue(document, "MINDEX");
			ibaMap.put("PHASE_CODE", phase);
			ibaMap.put("KEYCOMPONENT", keycomponent);
			ibaMap.put("ZZCJ", zzcj);
			ibaMap.put("ENDITEMIN", enditemin);
			ibaMap.put("MINDEX", mindex);

			cmTreeNode = new CmTreeNode();
			cmTreeNode.setNumber(number);
			cmTreeNode.setName(name);
			cmTreeNode.setVersion(version);
			cmTreeNode.setIbaAttributes(ibaMap);
			cmTreeNodeList.add(cmTreeNode);
		}
		return cmTreeNodeList;
	}

	public static List<CmAttachment> getTechnicsCmAttachment(List<String> technicsNumberList) {
		List<CmAttachment> cmAttachmentList = new ArrayList<CmAttachment>();
		CmAttachment cmAttachment;
		WTDocument document;
		ApplicationData data;
		IBAHelper ibaHelper;
		try {
			for (String technicsNumber : technicsNumberList) {
				document = WTDocumentUtil.getDocumentByNumber(technicsNumber);
				ibaHelper = new IBAHelper(document);
				data = WTDocumentUtil.getPrimaryByDocument(document);
				if (data != null) {
					byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
					cmAttachment = new CmAttachment();
					cmAttachment.setNumber(technicsNumber);
					cmAttachment.setOrderNo(ibaHelper.getIBAValue("PPNUMBER"));
					cmAttachment.setFileName(data.getFileName());
					cmAttachment.setBytes(bytes);
					cmAttachmentList.add(cmAttachment);
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return cmAttachmentList;
	}

	public static WTPart getPartByNumberAndVersion(String partNumber, String version) throws WTException {
		WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber, Constant.PBOM_VIEW);
		if (part != null) {
			QueryResult allIterations = VersionControlHelper.service.allVersionsFrom((Versioned) part);
			if (allIterations != null) {
				while (allIterations.hasMoreElements()) {
					WTPart temp = (WTPart) allIterations.nextElement();
					if (temp.getIterationDisplayIdentifier().toString().startsWith(version)) {
						return temp;
					}
				}
			}
		}
		return null;
	}

	public static WTPart getPartByNumberAndVersion2(String partNumber, String version) throws WTException {
		WTPart part = WTPartUtil.getLatestPartByNumberAndView(partNumber, Constant.PBOM_VIEW);
		if (part != null) {
			QueryResult allIterations = VersionControlHelper.service.allIterationsOf(part.getMaster());
			if (allIterations != null) {
				while (allIterations.hasMoreElements()) {
					WTPart temp = (WTPart) allIterations.nextElement();
					if (temp.getIterationDisplayIdentifier().toString().startsWith(version) && Constant.PBOM_VIEW.equals(temp.getViewName())) {
						return temp;
					}
				}
			}
		}
		return null;
	}

	public static byte[] getPbomBytesByTechnicsOid(String technicsOid) throws Exception {

		WTDocument document = (WTDocument) Util.getObjectByOid(WTDocument.class, technicsOid);
		WTPart part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(document);
		// WTPart part =
		// MPMProcessPlanUtil.getMPMProcessplanRelatedPart(mpmProcessPlan);
		byte[] bytes = null;
		if (part != null) {
			String parentPartOid = "";
			if (WTPartUtil.isHasPbomXml(part)) {
				parentPartOid = String.valueOf(PersistenceHelper.getObjectIdentifier(part).getId());
			} else {
				parentPartOid = WTPartUtil.getHasPbomXmlParentPartIda2a2(part, parentPartOid, false);
			}
			WTPart parentPart = (WTPart) Util.getObjectByOid(WTPart.class, parentPartOid);
			bytes = PBOMHelper.getBOMXml(parentPart, "-pbom.xml");
		}

		return bytes;

	}

	public static List<CmAttachment> getPbomRelatedTechnics(String partOid, String technicsOid) throws Exception {
		List<CmAttachment> cmAttachmentList = new ArrayList<CmAttachment>();
		CmAttachment cmAttachment;
		WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
		List<WTDocument> documentList = WTPartUtil.getDescribedDocumentByPart(part, "casc.sast.149.PROCESS_PLAN");
		for (WTDocument document : documentList) {
			String docOid = String.valueOf(document.getPersistInfo().getObjectIdentifier().getId());
			if (docOid.equals(technicsOid)) {
				ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
				if (data != null) {
					byte[] bytes = WTDocumentUtil.applicationDataToByte(data);
					cmAttachment = new CmAttachment();
					cmAttachment.setNumber(document.getNumber());
					cmAttachment.setFileName(data.getFileName());
					cmAttachment.setBytes(bytes);
					cmAttachmentList.add(cmAttachment);
				}
			}
		}
		return cmAttachmentList;
	}

	public static List<GLZhuFuLink> getZFLinks(String zzTechnicsNumber, String version) {
		if (version.contains(".")) {
			version = version.substring(0, version.indexOf("."));
		}
		ProcessService processService = new ProcessService();
		List<GLZhuFuLink> zhuFuLinks = processService.getAllFProcessPlan(zzTechnicsNumber, version);
		return zhuFuLinks;
	}

	public static TempObject getSamePplanNumberDxPlan(String pplanNumber) throws Exception {
		WTDocument document = getDxDocumentByIba(pplanNumber);
		TempObject technics = null;
		if (document != null) {
			technics = new TempObject();
			technics.setNumber(pplanNumber);
			technics.setDocNumber(document.getNumber());
			technics.setVersion(document.getIterationDisplayIdentifier().toString());
		}
		return technics;
	}

	public static WTDocument getDxDocumentByIba(String pplanNumber) throws Exception {
		WTDocument document = null;
		String type = "wt.doc.WTDocument|casc.sast.149.PROCESS_DOC|casc.sast.149.PROCESS_PLAN|casc.sast.149.DIANXING_PROCESSPLAN";
		QuerySpec querySpec = new QuerySpec(WTDocument.class);
		querySpec.setAdvancedQueryEnabled(true);
		ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
		TypeUtil.getTypeQuery(WTDocument.class, type, querySpec);
		querySpec.appendAnd();
		querySpec.appendOpenParen();
		SubSelectExpression subSelectExpression = WTDocumentUtil.getStringIBAQuery("PPNUMBER", pplanNumber);
		querySpec.appendWhere(new SearchCondition(caId, SearchCondition.IN, subSelectExpression), index);
		querySpec.appendCloseParen();
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec) querySpec);
		queryResult = new LatestConfigSpec().process(queryResult);
		if (queryResult.hasMoreElements()) {
			document = (WTDocument) queryResult.nextElement();
		}
		return document;
	}

	public static Boolean checkTechnicsState(String technicsNumber) throws WTException, RemoteException {
		boolean flag = Boolean.FALSE;
		WTDocument document = WTDocumentUtil.getDocumentByNumber(technicsNumber);
		if (document != null) {
			String state = document.getState().getState().getDisplay(Locale.CHINA);
			if ("正在工作".equals(state) || "修改中".equals(state)) {
				flag = Boolean.TRUE;
			}
		} else {
			flag = Boolean.TRUE;
		}
		return flag;
	}

	/**
	 * add by zengYao start
	 *
	 * @throws WTException
	 */
	public static List<Map<String, Object>> getZhuFuLinkByFPlan(String fPlanNumber, String version) throws WTException {
		List<Map<String, Object>> resultMaps = new ArrayList<Map<String, Object>>();
		if (fPlanNumber != null && version != null) {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("SELECT * FROM GL_ZHUFULINKMASTER WHERE FZTECHNICSNUMBER='").append(fPlanNumber).append("'").append(" AND FZTECHNICSVERSION='").append(version).append("'");
			boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodcontext = MethodContext.getContext();
			WTConnection wtconnection = null;
			DBConnUtil dbUtil = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try {
				Map<String, List<Element>> cache = new HashMap<String, List<Element>>();
				dbUtil = new DBConnUtil();
				rs = dbUtil.executeQuery(sqlBuilder.toString());
				WTDocument doc = null;
				String cacheKey = null;
				String stepKey = null;
				List<Element> allStepElement = null;
				Map<String, Map<String, Object>> tempMap = new HashMap<String, Map<String, Object>>();
				while (rs.next()) {
					Map<String, Object> map = new HashMap<String, Object>();
					String docNumber = rs.getString("ZZTECHNICSNUMBER");
					String zPlanVersion = rs.getString("ZZTECHNICSVERSION");
					String zStepBSOID = rs.getString("ZZPROCEDUREBSOID");
					doc = WTDocumentUtil.getWTDocument(docNumber, null, zPlanVersion, null);
					if (doc == null) {
						continue;
					}
					stepKey = docNumber + "&" + zStepBSOID;
					String gwKey = rs.getString("GWKEY");
					cacheKey = docNumber + "&" + zPlanVersion;
					if (cache.containsKey(cacheKey)) {
						allStepElement = cache.get(cacheKey);
					} else {
						allStepElement = ZhuFuLinkUtil.getProceduresOfTechnic(doc);
						cache.put(cacheKey, allStepElement);
					}
					MainPlanProcedure stepModel = ZhuFuLinkUtil.getStepModelByBSOID(zStepBSOID, allStepElement);
					String zPlanName = doc != null ? doc.getName() : "";
					String technicNumber = doc != null ? IBAHelper.getAnyIBAValueOfObject(doc, "PPNUMBER") : "";
					zPlanVersion = doc != null ? doc.getIterationDisplayIdentifier().toString() : "";
					map.put("gwKey", gwKey);
					map.put("docNumber", docNumber);
					map.put("technicNumber", technicNumber);
					map.put("technicName", zPlanName);
					map.put("procedureLabel", stepModel);
					map.put("version", zPlanVersion);
					map.put("picihao", rs.getString("PICIHAO"));
					map.put("creator", rs.getString("CREATOR"));
					map.put("createTime", rs.getString("CREATETIME"));
					if (tempMap.containsKey(stepKey)) {
						Map<String, Object> beforeMap = tempMap.get(stepKey);
						String newKey = beforeMap.get("gwKey") + "," + gwKey;
						if (ZhuFuLinkUtil.versionComparator.compare(zPlanVersion, (String) beforeMap.get("version")) > 0) {
							map.put("gwKey", newKey);
							tempMap.put(stepKey, map);
						} else {
							beforeMap.put("gwKey", newKey);
						}
					} else {
						tempMap.put(stepKey, map);
					}
				}
				resultMaps.addAll(tempMap.values());
			} catch (Exception e1) {
				throw new WTException(e1);
			} finally {
				SessionServerHelper.manager.setAccessEnforced(flag);
				try {
					if (dbUtil != null) {
						dbUtil.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return resultMaps;
	}

	public static List<TempObject> searchTechnics(String number, String name, String technicType, String zfFlag, String[] states) throws WTException {
		if (number != null && number.length() > 0) {
			number = Util.formatSearchString(number);
		}
		if (name != null && name.length() > 0) {
			name = Util.formatSearchString(name);
		}
		List<TempObject> list = null;
		String type = "casc.sast.149.PROCESS_PLAN";
		boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QueryResult docResult = WTDocumentUtil.getWTDocumentByLikeNumAndName(number, name, null, null);
			if (docResult != null) {
				list = new ArrayList<TempObject>();
				Object nextElement = null;
				WTDocument doc = null;
				String softType = null;
				String planType = null;
				String flag = null;
				while (docResult.hasMoreElements()) {
					nextElement = docResult.nextElement();
					if (nextElement instanceof WTDocument) {
						doc = (WTDocument) nextElement;
						softType = TypeUtil.getSoftType(doc, true);
						if (softType != null && softType.contains(type)) {
							if (states == null || Arrays.asList(states).contains(doc.getLifeCycleState().toString())) {
								planType = IBAHelper.getAnyIBAValueOfObject(doc, "PPLANTYPE");
								flag = IBAHelper.getAnyIBAValueOfObject(doc, "ZFFLAG");
								if (zfFlag.equals(flag)) {
									TempObject tempObject = new TempObject();
									tempObject.setOid(Util.getStringOid(doc));
									tempObject.setName(doc.getName());
									tempObject.setLifecycle(doc.getLifeCycleState().getLocalizedMessage(Locale.CHINA));
									tempObject.setVersion(doc.getIterationDisplayIdentifier().toString());
									tempObject.setType(TypeHelper.getLocalizedTypeString(doc, Locale.CHINA));

									IBAHelper ibaHelper = new IBAHelper(doc);
									tempObject.setNumber(ibaHelper.getIBAValue("PPNUMBER"));
									tempObject.setDocNumber(doc.getNumber());
									tempObject.setOccId(ibaHelper.getIBAValue("BATCH")); // 保存批次号
									list.add(tempObject);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new WTException(e);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(accessFlag);
		}
		return list;
	}

	public static void deleteZhuFuLinkByID(String[] gwKeyArray) {
		if (gwKeyArray != null && gwKeyArray.length > 0) {
			String sql = "DELETE FROM GL_ZHUFULINKMASTER WHERE GWKEY=?";
			GwStandardPersistenceManager manager = new GwStandardPersistenceManager();
			for (String id : gwKeyArray) {
				manager.deleteBySql(sql, id);
			}
		} else {
			logger.debug("==》参数gwKeyArray is empth");
		}
	}

	public static String saveZhuFuLink(List<Map<String, String>> dataMap) throws WTException {
		String result = "FAILD";
		if (dataMap != null && dataMap.size() > 0) {
			DBConnUtil dbUtil = null;
			try {
				StringBuilder checkMsg = new StringBuilder();
				dbUtil = new DBConnUtil();
				dbUtil.start();
				ResultSet rs = null;
				for (Map<String, String> row : dataMap) {
					// 检查主制工序是否已经关联其他辅制工艺
					String zTechnicsNum = row.get("ZZTECHNICSNUMBER");
					String zTechnicsVer = row.get("ZZTECHNICSVERSION");
					String zTechnicsBsoid = row.get("ZZPROCEDUREBSOID");
					String fTechnicsNum = row.get("FZTECHNICSNUMBER");

					String zPlanNum = row.get("ZPLANNUMBER");
					String zStepName = row.get("ZPROCEDURELABEL");

					StringBuilder sqlBuilder = new StringBuilder();
					sqlBuilder.append("SELECT * FROM GL_ZHUFULINKMASTER");
					sqlBuilder.append(" WHERE ");
					sqlBuilder.append("ZZTECHNICSNUMBER='").append(zTechnicsNum).append("' AND ");
					sqlBuilder.append("ZZTECHNICSVERSION='").append(zTechnicsVer).append("' AND ");
					sqlBuilder.append("ZZPROCEDUREBSOID='").append(zTechnicsBsoid).append("'");
					rs = dbUtil.executeQuery(sqlBuilder.toString());
					while (rs.next()) {
						if (!rs.getString("FZTECHNICSNUMBER").equals(fTechnicsNum)) {
							checkMsg.append(String.format("主制工艺【%s】下的工序【%s】已关联辅制工艺，请移除！", zPlanNum, zStepName));
						}
					}
				}
				if (checkMsg.toString().length() == 0) {
					for (Map<String, String> row : dataMap) {
						String gwKey = row.get("GWKEY");
						row.remove("GWKEY");
						row.remove("ZPLANNUMBER");
						row.remove("ZPROCEDURELABEL");
						if (gwKey != null && gwKey.length() > 0) {
							String[] keys = gwKey.split("\\,");
							for (String key : keys) {
								// 更新
								ZhuFuLinkUtil.updateById(dbUtil, key, row);
							}
						} else {
							// 插入
							// row.put("GWKEY", UUID.randomUUID().toString());
							ZhuFuLinkUtil.insertOneRow(dbUtil, row);
						}
					}
				} else {
					return checkMsg.toString();
				}
				dbUtil.commit();
				result = "SUCCESS";
			} catch (Exception e) {
				try {
					dbUtil.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				result = e.getLocalizedMessage();
				throw new WTException(e);
			} finally {
				if (dbUtil != null) {
					try {
						dbUtil.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			result = "无需要保存的数据";
		}
		return result;
	}

	/**
	 * 主制关联复制加载现有关联
	 *
	 * @param params
	 * @throws Exception
	 */
	public static Map<String, Map<String, String>> searchZFLinkByMainPlan(Map<String, String> params) throws Exception {
		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			resultMap = ZhuFuLinkUtil.queryLink(params);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		return resultMap;
	}

	public static String addZhuFuLinkByZplan(Map<String, String> params) throws WTException {
		String result = "FAILD";
		if (params != null && params.size() > 0) {
			String zDocNum = params.get("ZZTECHNICSNUMBER");
			String zVersion = params.get("ZZTECHNICSVERSION");
			String bsoid = params.get("ZZPROCEDUREBSOID");
			if (zDocNum.length() != 0 && zVersion.length() != 0 && bsoid.length() != 0) {
				DBConnUtil dbUtil = null;
				try {
					WTDocument doc = WTDocumentUtil.getWTDocument(zDocNum, null, zVersion, null);
					dbUtil = new DBConnUtil();
					dbUtil.start();
					Map<String, String> deleteCondition = new HashMap<String, String>();
					deleteCondition.put("ZZTECHNICSNUMBER", zDocNum);
					deleteCondition.put("ZZTECHNICSVERSION", zVersion);
					deleteCondition.put("ZZPROCEDUREBSOID", bsoid);
					ZhuFuLinkUtil.delete(dbUtil, deleteCondition);
					ZhuFuLinkUtil.insertOneRow(dbUtil, params);
					if ("已批准".equals(doc.getLifeCycleState().getDisplay(Locale.CHINA))) {
						ZhuFuLinkUtil.syncZFLink(doc);
					}
					dbUtil.commit();
					result = "SUCCESS";
				} catch (Exception e) {
					if (dbUtil != null) {
						try {
							dbUtil.rollback();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
					result = "添加辅制工艺异常\n" + e.getLocalizedMessage();
					throw new WTException(e);
				} finally {
					if (dbUtil != null) {
						try {
							dbUtil.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				logger.error(String.format("添加辅制工艺失败！参数错误{zDocNum[%s],zVersion[%s],zLabel[%s]}", zDocNum, zVersion, bsoid));
			}
		}
		return result;
	}

	/**
	 * 记录主辅工艺关联记录
	 *
	 * @param params
	 * @return
	 * @throws WTException
	 */
	public static String recordZhuFuLink(Map<String, String> params) throws WTException {
		String result = "FAILD";
		if (params != null && params.size() > 0) {
			DBConnUtil dbUtil = null;
			try {
				dbUtil = new DBConnUtil();
				dbUtil.start();
				ZhuFuLinkUtil.addRecord(dbUtil, params);
				dbUtil.commit();
				result = "SUCCESS";
			} catch (Exception e) {
				if (dbUtil != null) {
					try {
						dbUtil.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				result = "添加主辅关联记录异常\n" + e.getLocalizedMessage();
				throw new WTException(e);
			} finally {
				if (dbUtil != null) {
					try {
						dbUtil.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

		}
		return result;
	}

	/**
	 * 记录主辅工艺关联记录
	 *
	 * @param params
	 * @return
	 * @throws WTException
	 */
	public static String recordZhuFuLink(GLZhuFuLink glZhuFuLink) throws WTException {
		String result = "FAILD";
		if (glZhuFuLink != null) {
			DBConnUtil dbUtil = null;
			try {
				dbUtil = new DBConnUtil();
				dbUtil.start();
				ZhuFuLinkUtil.addRecord(dbUtil, glZhuFuLink);
				dbUtil.commit();
				result = "SUCCESS";
			} catch (Exception e) {
				if (dbUtil != null) {
					try {
						dbUtil.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
				result = "添加主辅关联记录异常\n" + e.getLocalizedMessage();
				throw new WTException(e);
			} finally {
				if (dbUtil != null) {
					try {
						dbUtil.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}

		}
		return result;
	}

	public static List<GLZhuFuLink> getAllZhufuLink(String fDocNum, String fVersion) {
		if (fVersion.contains(".")) {
			fVersion = fVersion.split(".")[0];
		}
		List<GLZhuFuLink> glZhuFuLinkList = new ArrayList<GLZhuFuLink>();
		GLZhuFuLink glZhuFuLink;
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT * FROM GL_ZHUFULINKMASTER WHERE FZTECHNICSNUMBER='").append(fDocNum).append("'").append(" AND FZTECHNICSVERSION='").append(fVersion).append("'");
		DBConnUtil dbUtil = null;
		WTDocument doc;
		String cacheKey = null;
		String stepKey = null;
		List<Element> allStepElement = null;
		try {
			Map<String, List<Element>> cache = new HashMap<String, List<Element>>();
			dbUtil = new DBConnUtil();
			ResultSet resultSet = dbUtil.executeQuery(sqlBuilder.toString());
			while (resultSet.next()) {

				String docNumber = resultSet.getString("ZZTECHNICSNUMBER");
				String zPlanVersion = resultSet.getString("ZZTECHNICSVERSION");
				String zStepBSOID = resultSet.getString("ZZPROCEDUREBSOID");
				doc = WTDocumentUtil.getWTDocument(docNumber, null, zPlanVersion, null);
				if (doc == null) {
					continue;
				}
				stepKey = docNumber + "&" + zStepBSOID;
				String gwKey = resultSet.getString("GWKEY");
				cacheKey = docNumber + "&" + zPlanVersion;
				if (cache.containsKey(cacheKey)) {
					allStepElement = cache.get(cacheKey);
				} else {
					allStepElement = ZhuFuLinkUtil.getProceduresOfTechnic(doc);
					cache.put(cacheKey, allStepElement);
				}
				MainPlanProcedure stepModel = ZhuFuLinkUtil.getStepModelByBSOID(zStepBSOID, allStepElement);
				String stepNumber = stepModel.toString().split("_")[0];
				String stepName = stepModel.toString().split("_")[1];
				glZhuFuLink = new GLZhuFuLink();
				glZhuFuLink.setZstepNumber(stepNumber);
				glZhuFuLink.setZstepName(stepName);
				glZhuFuLink.setZstepBsoid(zStepBSOID);
				glZhuFuLink.setZztechnicsnumber(docNumber);
				glZhuFuLink.setZztechnicsversion(zPlanVersion);
				glZhuFuLink.setFztechnicsnumber(resultSet.getString("FZTECHNICSNUMBER"));
				glZhuFuLink.setFztechnicsversion(resultSet.getString("FZTECHNICSVERSION"));
				glZhuFuLink.setCreator(resultSet.getString("CREATOR"));
				glZhuFuLink.setCreateTime(resultSet.getString("CREATETIME"));
				glZhuFuLinkList.add(glZhuFuLink);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return glZhuFuLinkList;
	}

	public static List<GLZhuFuLink> getRecordList(String technicsNumber, String version, String stepNumber) {
		return ZhuFuLinkUtil.getRecord(technicsNumber, version, stepNumber);
	}

	public static String deleteZhuFuLink(Map<String, String> params) throws WTException {
		String result = "FAILD";
		if (params != null && params.size() > 0) {
			DBConnUtil dbUtil = null;
			try {
				dbUtil = new DBConnUtil();
				dbUtil.start();
				ZhuFuLinkUtil.delete(dbUtil, params);
				String zNumber = params.get("ZZTECHNICSNUMBER");
				String zVersion = params.get("ZZTECHNICSVERSION");
				if (zNumber != null && zNumber.length() > 0 && zVersion != null && zVersion.length() > 0) {
					WTDocument doc = WTDocumentUtil.getWTDocument(zNumber, null, zVersion, null);
					if ("已批准".equals(doc.getLifeCycleState().getDisplay(Locale.CHINA))) {
						ZhuFuLinkUtil.syncZFLink(doc);
					}
				}
				dbUtil.commit();
				result = "SUCCESS";
			} catch (Exception e) {
				try {
					if (dbUtil != null) {
						dbUtil.rollback();
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				result = "删除失败!\n" + e.getLocalizedMessage();
				throw new WTException(e);
			} finally {
				if (dbUtil != null) {
					try {
						dbUtil.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			result = "删除主辅link记录失败；删除条件为空";
			logger.debug("删除条件为空，不允许删除(会清空GL_ZHUFULINK表中数据)");
		}
		return result;
	}

	public static String checkDocExitAndModifier(String docNum) throws WTException {
		String result = "EXCEPTION";
		try {
			WTDocument doc = WTDocumentUtil.getDocumentByNumber(docNum);
			if (doc != null) {
				result = doc.getModifierName();
			} else {
				result = "NOTEXIST";
			}
		} catch (RemoteException e) {
			result = "EXCEPTION";
			throw new WTException(e);
		}
		return result;
	}

	/**
	 * 通过partNumber获得实时容器名称
	 *
	 * @param partNumber
	 * @return
	 * @throws WTException
	 */
	public static String getProductNumberAndName(String partNumber) throws WTException {
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		WTPart wtPart = WTPartUtil.getPartByNumberAndView(partNumber, "Manufacturing");
		WTContainer container = wtPart.getContainer();
		String name = container.getName();
		SessionServerHelper.manager.setAccessEnforced(flag);
		return name;
	}

	/** add by zengYao end */

	public static Map<String, String> getAllProcessTask(String docNumber, String partOid, String user) throws WTException, RemoteException {
		List<ProcessTask> processTaskList = new ArrayList<ProcessTask>();
		WTPart part = null;
		if(StrUtil.isNotEmpty(partOid)){
			part = (WTPart) ReferenceFactory.getObjectbyOid("wt.part.WTPart:" + partOid);
		}else {
			WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(docNumber);
			if(doc != null) {
				part = WTDocumentUtil.getLatestDescribesWTPartsByDocument(doc);
			}
		}
		if (part != null) {
			QuerySpec qs = new QuerySpec(ProcessTaskItem.class);
			SearchCondition sc = new SearchCondition(ProcessTaskItem.class, ProcessTaskItem.NUMBER, SearchCondition.EQUAL, part.getNumber(), false);
			qs.appendSearchCondition(sc);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				ProcessTaskItem taskItem = (ProcessTaskItem) qr.nextElement();
				ProcessTask processTask = ProcessUtil.getProcessTask(taskItem.getProcessTaskId());
				String owner = taskItem.getOwner();
				if (owner != null && !owner.isEmpty() && !"null".equals(owner) && owner.equals(user)) {
					IBAUtility iba = new IBAUtility(taskItem);
					String docNum = iba.getIBAValue("PROCESSDOCNUM");
					if (docNum != null && !"".equals(docNum)) {
						if (docNum.equals(docNumber)) {
							processTaskList.clear();
							processTaskList.add(processTask);
							break;
						}
					} else {
						if (!processTaskList.contains(processTask)) {
							processTaskList.add(processTask);
						}
					}
				}
			}
		}
		Map<String, String> resultMap = new HashMap<String, String>();
		for (ProcessTask processTask : processTaskList) {
			if (processTask == null) {
				continue;
			}
			if ("工艺更改任务".equals(processTask.getTaskType()) || "报表类工艺任务".equals(processTask.getTaskType())) {
				continue;
			}

			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd");
			String endDate = dFormat.format(processTask.getEndDate());
			String processTaskId = String.valueOf(processTask.getPersistInfo().getObjectIdentifier().getId());
			String disPlay = processTask.getNumber() + "-" + processTask.getTaskType() + "(" + processTask.getCreatorFullName() + ")" + "(" + endDate + ")";
			resultMap.put(processTaskId, disPlay);
		}
		return resultMap;
	}

	public static boolean isHasChangeOrder(String docNumber) throws WTException, RemoteException {
		WTDocument document = WTDocumentUtil.getDocumentByNumber(docNumber);
		WTCollection coll = RelatedChangesQueryCommands.getRelatedResultingChangeNotices(document);
		Iterator it = coll.iterator();
		if (it.hasNext()) {
			WTChangeOrder2 ecn = (WTChangeOrder2) ((ObjectReference) it.next()).getObject();
			if (ecn != null) {
				return true;
			}
		}
		return false;
	}

	public static String getNextVersion(String technicsNumber) throws WTException, WTPropertyVetoException {
		String nextVersion = null;
		WTDocument wtDocument = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
		if (wtDocument != null) {
			String version = VersionControlHelper.getVersionIdentifier((Versioned) wtDocument).getValue();
			String iterate = VersionControlHelper.getIterationIdentifier((Iterated) wtDocument).getValue();
			String nextIter = VersionControlHelper.nextIterationId((Iterated) wtDocument).getValue();
			System.out.println("nextIter======================" + nextIter);
			int nextIterate = Integer.valueOf(iterate) + 1;
			nextVersion = version + "." + nextIterate;
		}
		return nextVersion;
	}

	public static boolean checkIsHasEpm(String partOid) throws WTException {
		boolean flag = false;
		WTPart part = getPartByOid(partOid);
		if (part != null) {
			QueryResult epmDocumentByPart = WTPartUtil.getEPMDocumentByPart(part);
			if (epmDocumentByPart.hasMoreElements()) {
				flag = true;
			}
		}
		return flag;
	}

	public static String getUnMindexByProductName(String name) throws WTException {
		PDMLinkProduct product = WTContainerUtil.getProductByName(name);
        String unMindex = "";
        if(product!=null){
        	try {
				IBAUtility utility = new IBAUtility(product);
				unMindex = utility.getIBAValue("UNMINDEX");
			} catch (WTException e) {
				e.printStackTrace();
			}
        }
        return unMindex;
	}

	/**
	 * 方法功能: 获取部件及其子件下所有工艺
	 *
	 * @param partOid
	 * @param userName
	 * @return java.util.Map<java.lang.String, org.dom4j.Element>
	 * @author LB
	 * @date 2020/9/21
	 */
	public static List<Element> getAllCldeProcessPlan(String partOid, String userName) throws WTException, RemoteException {
		List<Element> techEleList = getAllTechnicsElementByPart(partOid, userName);
		return techEleList;
	}


	public static ArrayList<ArrayList<String>> getCheckFileList(String type, String technicsNumber,String stepNum,String paceNum){
		boolean falg = SessionServerHelper.manager.setAccessEnforced(false);
		ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();
		try {
			int index[] = { 0 };
			QuerySpec qs = new QuerySpec(WTDocument.class);
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, ProcessPlanConstants.LIFECYCLE_EN_OBSOLESCENCE), index);
			qs.appendAnd();
			qs.setAdvancedQueryEnabled(true);
			TypeUtil.getTypeQuery(WTDocument.class, "casc.sast.149.BaiYuDocument", qs);
			ClassAttribute caId = new ClassAttribute(WTDocument.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			if (technicsNumber != null && !"".equals(technicsNumber)) {
				qs.appendAnd();
				qs.appendOpenParen();

				AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("PPNUMBER");
				if (addv == null) {
					throw new IBADefinitionException("No IBA Definition: " + "PPNUMBER");
				}
				long ibaDefId = addv.getObjectID().getId();
				QuerySpec qs2 = new QuerySpec();
				int idx = qs2.appendClassList(StringValue.class, false);
				qs2.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
				qs2.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
				qs2.appendAnd();
				qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, technicsNumber, true), new int[] { idx });

				SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index);
				qs.appendCloseParen();

			}
			if (stepNum != null) {
				qs.appendAnd();
				qs.appendOpenParen();

				AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("stepNum");
				if (addv == null) {
					throw new IBADefinitionException("No IBA Definition: " + "stepNum");
				}
				long ibaDefId = addv.getObjectID().getId();
				QuerySpec qs2 = new QuerySpec();
				int idx = qs2.appendClassList(StringValue.class, false);
				qs2.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
				qs2.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
				qs2.appendAnd();
				if("pace".equals(type)){
					qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, stepNum, true), new int[] { idx });
				} else if("doc".equals(type)) {
					qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.IS_NULL, true), new int[] { idx });
				}

				SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index);
				qs.appendCloseParen();

			}
			if (paceNum != null) {
				qs.appendAnd();
				qs.appendOpenParen();

				AttributeDefDefaultView addv = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("paceNum");
				if (addv == null) {
					throw new IBADefinitionException("No IBA Definition: " + "paceNum");
				}
				long ibaDefId = addv.getObjectID().getId();
				QuerySpec qs2 = new QuerySpec();
				int idx = qs2.appendClassList(StringValue.class, false);
				qs2.appendSelect(new ClassAttribute(StringValue.class, "theIBAHolderReference.key.id"), new int[] { idx }, false);
				qs2.appendWhere(new SearchCondition(StringValue.class, "definitionReference.key.id", SearchCondition.EQUAL, ibaDefId), new int[] { idx });
				qs2.appendAnd();
				if("pace".equals(type)){
					qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.EQUAL, paceNum, true), new int[] { idx });
				} else if("doc".equals(type)) {
					qs2.appendWhere(new SearchCondition(StringValue.class, StringValue.VALUE2, SearchCondition.IS_NULL, true), new int[] { idx });
				}

				SubSelectExpression stringIBAQuery = new SubSelectExpression(qs2);
				qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, stringIBAQuery), index);
				qs.appendCloseParen();
			}
			qs.appendOrderBy(new OrderBy(new ClassAttribute(WTDocument.class,
					WTDocument.CREATE_TIMESTAMP), false), new int[] { 0 });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			int row = 1;
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				if(doc!=null){
					ArrayList<String> list = new ArrayList<String>();
					list.add(0,row+"");
					list.add(1,doc.getNumber());
					list.add(2,doc.getName());
					list.add(3,doc.getIterationDisplayIdentifier().toString());
					list.add(4,doc.getCreatorFullName());
					list.add(5,doc.getModifierFullName());
					list.add(6,ProcessUtil.formatTime(doc.getCreateTimestamp().getTime()));
					list.add(7,ProcessUtil.formatTime(doc.getModifyTimestamp().getTime()));
					list.add(8,doc.getNumber());
					list.add(9,IBAHelper.getIBAValue(doc,"tableType"));
					list.add(10,IBAHelper.getIBAValue(doc,"DEPT"));
					list.add(11,doc.getDescription());
					allList.add(list);
					row++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(falg);
		}
		return allList;
	}

	public static Map<String,String> getFileURLByDocNumber(String docNumber) {
		boolean falg = SessionServerHelper.manager.setAccessEnforced(false);
		Map<String,String> map = new HashMap<String, String>();
		FileOutputStream fos = null;
		try {

			WTDocument doc = WTDocumentUtil.getDocumentByNumber(docNumber);
			if(doc!=null){
				String hostName ="pdm.149.sast.casc";
				try {
					WTProperties prop = WTProperties.getLocalProperties();
					hostName = prop.getProperty("java.rmi.server.hostname");
				} catch (IOException e) {
					e.printStackTrace();
				}
				IBAUtility utility = new IBAUtility(doc);
				String qbyFileName = utility.getIBAValue("qbyName");
				String mjsonFileName = utility.getIBAValue("moreJsonName");
				String ojsonFileName = utility.getIBAValue("oneJsonName");
				String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp" + File.separator +
						"aikeshengData" + File.separator + docNumber;
				File fileDir = new File(filePath);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
				while (qr.hasMoreElements()) {
					ApplicationData appData = (ApplicationData) qr.nextElement();
					String fileName = appData.getFileName();
					if (qbyFileName.equals(fileName) || mjsonFileName.equals(fileName) || ojsonFileName.equals(fileName)) {
						byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);
						String path = fileDir + File.separator + fileName;
						File file = new File(path);
						if(file != null && file.exists()){
							FileUtil.deleteFile(file);
						}
						fos = new FileOutputStream(file);
						fos.write(bytes);
						fos.flush();
						file = new File(path);
						if(file!=null){
							String windchill = "http://"+hostName+"/aikeshengData/";
							String fileURL = windchill + docNumber + "/" + fileName;
							if(fileName.equals(qbyFileName)){
								map.put("qby",fileURL);
							}else if(fileName.equals(mjsonFileName)){
								map.put("mjson",fileURL);
							}else if(fileName.equals(ojsonFileName)){
								map.put("ojson",fileURL);
							}
						}
					}
				}
				return map;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			SessionServerHelper.manager.setAccessEnforced(falg);
		}
		return null;
	}

	//2024.3.1删除白羽表改为设置状态为已作废 刷新查新忽略已作废
	public static String deleteSchemaDataByNumber(String number) {
		boolean falg = SessionServerHelper.manager.setAccessEnforced(false);
		WTDocument doc = null;
		try {
			int index[] = { 0 };
			QuerySpec qs = new QuerySpec(WTDocument.class);
			SearchCondition sc = new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number);
			qs.appendWhere(sc, new int[] { 0 });
			qs.appendAnd();
			TypeUtil.getTypeQuery(WTDocument.class, "casc.sast.149.BaiYuDocument", qs);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			while (qr.hasMoreElements()) {
				doc = (WTDocument) qr.nextElement();
				if(doc!=null){
//					PersistenceHelper.manager.delete(doc);
					LifeCycleServerHelper.service.setState((LifeCycleManaged) doc, State.toState(ProcessPlanConstants.LIFECYCLE_EN_OBSOLESCENCE));
					doc = (WTDocument) PersistenceHelper.manager.refresh(doc);
					return "";
				}else{
					return "未找到相应文档！";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			SessionServerHelper.manager.setAccessEnforced(falg);
		}
		return "删除文档失败";
	}

	public static HashMap<String,ArrayList<HashMap<String,String>>> getPhotoRecordList(Map<String, String> map) {
		HashMap<String,ArrayList<HashMap<String,String>>> returnMap = new HashMap<String, ArrayList<HashMap<String,String>>>();
		try {
			String number = map.get("number");
			String name = map.get("name");
			String productNumber = map.get("productNumber");
			String psyq = map.get("psyq");
			String pbzz = map.get("pbzz");
			String psdxType = map.get("psdxType");
			String photoType= map.get("photoType");
			String PDCJBH= map.get("PDCJBH");
			String PDCJMC= map.get("PDCJMC");
			Map<String, Object> attributeMap = new HashMap<String, Object>();
			if (productNumber!=null && !"".equals(productNumber)) {
				attributeMap.put("productNumber",productNumber);
			}
			if (psyq!=null && !"".equals(psyq)) {
				attributeMap.put("psyq",psyq);
			}
			if (pbzz!=null && !"".equals(pbzz)) {
				attributeMap.put("pbzz",pbzz);
			}
			if (psdxType!=null && !"".equals(psdxType)) {
				attributeMap.put("psdxType",psdxType);
			}
			if (photoType!=null && !"".equals(photoType)) {
				attributeMap.put("photoType",photoType);
			}
			if (PDCJBH!=null && !"".equals(PDCJBH)) {
				attributeMap.put("PDCJBH",PDCJBH);
			}
			if (PDCJMC!=null && !"".equals(PDCJMC)) {
				attributeMap.put("PDCJMC",PDCJMC);
			}
			QuerySpec qs = new QuerySpec(WTDocument.class);
			TypeUtil.getTypeQuery(WTDocument.class,"casc.sast.149.PhotoTemplate", qs);
			if(number!=null && !"".equals(number)){
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.LIKE, "%" + number + "%"), index);
			}
			if(name!=null && !"".equals(name)){
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NAME, SearchCondition.LIKE, "%" + name + "%"), index);
			}
			if (attributeMap.size() != 0) {
				MPMResourceUtil.querySAttributeValue(WTDocument.class, qs, attributeMap);
			}
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
			qr = new LatestConfigSpec().process(qr);
			while (qr.hasMoreElements()) {
				WTDocument doc = (WTDocument) qr.nextElement();
				ArrayList<HashMap<String,String>> docList = new ArrayList<HashMap<String,String>>();
				List<WTDocument> list = WTDocumentUtil.getAllDocumentByNumber(doc.getNumber());
				for (WTDocument document : list) {
					HashMap<String,String> hashMap = new HashMap<String, String>();
					if(document!=null){
						hashMap.put("number",document.getNumber());
						hashMap.put("name",document.getName());
						hashMap.put("version",document.getIterationDisplayIdentifier().toString());
						hashMap.put("productNumber", (String) MBAUtil.getValue(document,"productNumber"));
						hashMap.put("psyq", (String) MBAUtil.getValue(document,"psyq"));
						hashMap.put("pbzz", (String) MBAUtil.getValue(document,"pbzz"));
						hashMap.put("psdxType", (String) MBAUtil.getValue(document,"psdxType"));
						hashMap.put("photoType", (String) MBAUtil.getValue(document,"photoType"));
						hashMap.put("PDCJBH", (String) MBAUtil.getValue(document,"PDCJBH"));
						hashMap.put("PDCJMC", (String) MBAUtil.getValue(document,"PDCJMC"));

						docList.add(hashMap);
					}
				}
				returnMap.put(doc.getNumber(),docList);
			}
		} catch (wt.query.QueryException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return returnMap;
	}

	public static byte[] getImageBytesByNumberAndVersion(String number, String version) {
		byte[] bytes = null;
		WTDocument document = null;
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number),new int[] { 0 });
			qs.setAdvancedQueryEnabled(true);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTDocument temp = (WTDocument) qr.nextElement();
				if (temp.getIterationDisplayIdentifier().toString().equals(version)) {
					document = temp;
				}
			}
			if(document!=null){
				ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
				if (data == null) {
					return null;
				}
				InputStream is = ContentServerHelper.service.findContentStream(data);
				bytes = FileUtil.fileToBytes(is);

			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public static String getImageNameByNumberAndVersion(String number, String version) {
		String name = null;
		WTDocument document = null;
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number),new int[] { 0 });
			qs.setAdvancedQueryEnabled(true);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				WTDocument temp = (WTDocument) qr.nextElement();
				if (temp.getIterationDisplayIdentifier().toString().equals(version)) {
					document = temp;
				}
			}
			if(document!=null){
				ApplicationData data = WTDocumentUtil.getPrimaryByDocument(document);
				if (data == null) {
					return null;
				}
				InputStream is = ContentServerHelper.service.findContentStream(data);
				name = data.getFileName();

			}
		} catch (WTException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
		return name;
	}

	public static Map<String, Object> getPhotoConfig() throws WTException, RemoteException {
		Map<String,Object> config = new HashMap<String, Object>();
		config.put("photoValueMap",ext.casc.constants.Constants.photoValueMap);
		config.put("photoValueList",ext.casc.constants.Constants.photoValueList);
		config.put("photoTypeList",ext.casc.constants.Constants.photoTypeList);
		return config;
	}


	public static Map<String,String> getFileURLByDocOid(String oid) {
		boolean falg = SessionServerHelper.manager.setAccessEnforced(false);
		Map<String,String> map = new HashMap<String, String>();
		FileOutputStream fos = null;
		try {
			WTDocument doc = WTDocumentUtil.getWTDocumentByOid(oid);
			if(doc!=null){
				String hostName ="pdm.149.sast.casc";
				try {
					WTProperties prop = WTProperties.getLocalProperties();
					hostName = prop.getProperty("java.rmi.server.hostname");
				} catch (IOException e) {
					e.printStackTrace();
				}
				IBAUtility utility = new IBAUtility(doc);
				String qbyFileName = utility.getIBAValue("qbyName");
				String mjsonFileName = utility.getIBAValue("moreJsonName");
				String ojsonFileName = utility.getIBAValue("oneJsonName");
				String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp" + File.separator +
						"aikeshengData" + File.separator + doc.getNumber();
				File fileDir = new File(filePath);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.SECONDARY);
				while (qr.hasMoreElements()) {
					ApplicationData appData = (ApplicationData) qr.nextElement();
					String fileName = appData.getFileName();
					if (qbyFileName.equals(fileName) || mjsonFileName.equals(fileName) || ojsonFileName.equals(fileName)) {
						byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);
						String path = fileDir + File.separator + fileName;
						File file = new File(path);
						if(file != null && file.exists()){
							FileUtil.deleteFile(file);
						}
						fos = new FileOutputStream(file);
						fos.write(bytes);
						fos.flush();
						file = new File(path);
						if(file!=null){
							String windchill = "http://"+hostName+"/aikeshengData/";
							String fileURL = windchill + doc.getNumber() + "/" + fileName;
							if(fileName.equals(qbyFileName)){
								map.put("qby",fileURL);
							}else if(fileName.equals(mjsonFileName)){
								map.put("mjson",fileURL);
							}else if(fileName.equals(ojsonFileName)){
								map.put("ojson",fileURL);
							}
						}
					}
				}
				return map;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			SessionServerHelper.manager.setAccessEnforced(falg);
		}
		return null;
	}

	public static void reName(String technicsNumber, String newName) throws WTException, RemoteException {
		WTDocument doc = WTDocumentUtil.getDocumentByNumber(technicsNumber);
	    if(doc!=null &&!doc.getName().equals(newName)){
			WTDocumentMaster master = (WTDocumentMaster) doc.getMaster();
			WTDocumentMasterIdentity idy = (WTDocumentMasterIdentity) master.getIdentificationObject();
			try {
				idy.setName(newName);
			} catch (WTPropertyVetoException e) {
				e.printStackTrace();
			}
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, idy);
		}
	}

	public static String  getBaiYuImageBytesByNumber(String number) {
		String images= "";
		WTDocument document = null;
		FileOutputStream fos = null;
		try {
			QuerySpec qs = new QuerySpec(WTDocument.class);
			qs.appendWhere(new SearchCondition(WTDocument.class, WTDocument.NUMBER, SearchCondition.EQUAL, number),new int[] { 0 });
			qs.setAdvancedQueryEnabled(true);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			qr = new LatestConfigSpec().process(qr);
			if (qr.hasMoreElements()) {
				document = (WTDocument) qr.nextElement();

				QueryResult appdataQr = ContentHelper.service.getContentsByRole(document, ContentRoleType.SECONDARY);
				while (appdataQr.hasMoreElements()) {
					ApplicationData appData = (ApplicationData) appdataQr.nextElement();
					String fileName = appData.getFileName();
					if ("original.zip".equals(fileName)) {

						String filePath = PropertiesUtil.getTempPath() + File.separator + "IXBExpImp" + File.separator +
								"aikeshengData" + File.separator + document.getNumber();
						byte[] bytes = WTDocumentUtil.applicationDataToByte(appData);
						File fileDir = new File(filePath);
						if (!fileDir.exists()) {
							fileDir.mkdirs();
						}
						String path = fileDir + File.separator + fileName;
						File file = new File(path);
						if(file != null && file.exists()){
							FileUtil.deleteFile(file);
						}
						fos = new FileOutputStream(file);
						fos.write(bytes);
						fos.flush();

						ApacheZipUtil.decompress(path,filePath);
						File imagesFolder = new File(filePath);
						int index = 0;
						if(imagesFolder.exists()){
							File[] listFiles = imagesFolder.listFiles();
							for(File listFile:listFiles){
								if(listFile.getName().endsWith("jpg")){
									//images = images+listFile.getName()+";";
									index++;
								}
							}
						}
						for(int i=0;i<index;i++){
							images = images+"image"+(i+1)+".jpg;";
						}
						//String windchill = "http://"+hostName+"/aikeshengData/";
						//fileUrl = windchill + document.getNumber() + "/image.jpg" ;
						return images;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(fos!=null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return images;
	}

	public static synchronized List<String> getZDGongyiFuJia() {
		if(ProcessCache.ZD_GONGYIFUJIA==null){
			try {
				ProcessCache.ZD_GONGYIFUJIA = new ArrayList<String>();
				DBConnUtil conn = new DBConnUtil();
				String sql = "SELECT DICTIONARYID FROM TechnicsMaterialLink WHERE  TECHNICSMATERIALID =concat('ext.ases.techMaterial.TechnicsMaterial:', (SELECT IDA2A2 FROM TECHNICSMATERIAL WHERE TECHNICSMATERIALNUMBER='ZD_字典_工艺附加信息'))";
				ResultSet rs = conn.executeQuery(sql);
				while(rs.next()){
					String dictionaryid = rs.getString("DICTIONARYID");
					ProcessCache.ZD_GONGYIFUJIA.add(dictionaryid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ProcessCache.ZD_GONGYIFUJIA;
		}else{
			return ProcessCache.ZD_GONGYIFUJIA;
		}

	}

	/**
	 * 上传工艺参数化模板
	 *
	 * @return
	 * @author cjh
	 * @date 2023-10-25
	 */
	public static String uploadProcessParamTemplateRMI(String fileName, byte[] bytes, String dept, String field, String productType, String templateType) {
		GLLogger.debug(CLASSNAME, "--fileName--" + fileName);
		String message = "";
		Transaction transaction = new Transaction();
		try {
			if (null == fileName || "".equals(fileName.trim())) {
				return "上传的参数化模板名称是空的!";
			}
			if (null == bytes) {
				return "上传的参数化模板内容是空的!";
			}
			String folderName = "";
			String containerName = "";
			String documentType = "";
			if("technics".equals(templateType)){
				folderName = propertiesUtil.getProperty("param-process-template-save-folder");
				containerName = propertiesUtil.getProperty("param-process-template-save-container");
				documentType = propertiesUtil.getProperty("param-process-template-save-document-type");
			} else if("step".equals(templateType)) {
				folderName = propertiesUtil.getProperty("param-procedure-template-save-folder");
				containerName = propertiesUtil.getProperty("param-procedure-template-save-container");
				documentType = propertiesUtil.getProperty("param-procedure-template-save-document-type");
			}
			if(StringUtils.isEmpty(folderName) || StringUtils.isEmpty(containerName) || StringUtils.isEmpty(documentType)) {
				message = "参数相关配置文件读取失败，请联系管理员！";
				return message;
			}

			String location = Constants.rootFolder + "/" + folderName;

			GLLogger.debug(CLASSNAME, "----location-" + location);
			GLLogger.debug(CLASSNAME, "----containerName-" + containerName);
			GLLogger.debug(CLASSNAME, "----documentType-" + documentType);
			WTContainer container = WTContainerUtil.getContainerByName(containerName);
			Folder folder = null;
			try {
				folder = FolderUtil.getFolder(location, WTContainerRef.newWTContainerRef(container));
			} catch (Exception e) {
				e.printStackTrace();
				message = "类型已经不存在,请重新选取类型上传模板!";
				return message;
			}

			List<WTDocument> list = WTDocumentUtil.getDocumnetByNameAndType(fileName, documentType);
			int tag = 0;
			for (WTDocument document : list) {
				if (document.getLocation().equals(location)) {
					if(document.getModifier().getPrincipal().getName().equals(SessionHelper.getPrincipal().getName())){
						String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(document);
						if (docType.endsWith("casc.sast.149.TechnicsParamTemplate")) {
							DBUtil.deleteByTableAndKey("GLPROCESSPARAMVALUES","TEMPLATEID", PersistenceCommonHelper.getOid(document));
						}
						PersistenceHelper.manager.delete(document);

					}else{
						tag = 1;
					}
					break;
				}
			}
			transaction.start();
			WTDocument doc = null;
			if (tag == 1) {
				message = "模板名称已被其他用户占用，请修改模板名称再重新上传!";
			} else {
				doc = WTDocumentUtil.createDocument(fileName, container, folder, documentType);
				WTDocumentUtil.setPrimaryForDocument(doc, fileName + ".zip", bytes);
				IBAHelper.setIBAStringValue(doc,"DEPT",dept);
				IBAHelper.setIBAStringValue(doc,"speciality",field);
				IBAHelper.setIBAStringValue(doc,"productType",productType);
			}
			transaction.commit();
			transaction = null;

			if(doc!=null){
				TechnicsGenerator.structureGyTempldateParams(doc);
			}
		} catch (Exception e) {
			message = "上传失败";
			e.printStackTrace();
		} finally {
			if (transaction != null) {
				transaction.rollback();
			}
		}
		GLLogger.debug(CLASSNAME, "----message-" + message);
		return message;
	}

	public static  List<String[]> queryTechnicsParsms(String numberValue,String nameValue,String type,String special)  {
		List<String[]> result = new ArrayList<String[]>();
		try {
            CmQuerySpec qs = new CmQuerySpec(GLProcessParams.class);
            qs.appendWhere("1", CmQuerySpec.EQUAL, "1");
            if(!Tools.isNull(numberValue)){
            	qs.appendAnd();
                qs.appendWhere(GLProcessParams.GYNUMBER, CmQuerySpec.LIKE, "%"+numberValue+"%");
            }

            if(!Tools.isNull(nameValue)){
            	qs.appendAnd();
                qs.appendWhere(GLProcessParams.GYNAME, CmQuerySpec.LIKE, "%"+nameValue+"%");
            }

            if(!Tools.isNull(type)&&!"全部".equals(type)){
            	qs.appendAnd();
                qs.appendWhere(GLProcessParams.PARAMETER_CATEGORY, CmQuerySpec.EQUAL, type);
            }
            if(!Tools.isNull(special)&&!"全部".equals(special)){
            	qs.appendAnd();
                qs.appendWhere(GLProcessParams.PROCESS_CATEGORY, CmQuerySpec.EQUAL, special);
            }
            CmQueryResult qr = CmPersistenceHelper.manager.find(qs);
            int index = 1;
            while (qr.hasNext()) {
            	String[] values = new String[6];
            	GLProcessParams params =  (GLProcessParams) qr.next();
            	values[0] = index+"";
            	values[1] = params.getGyNumber();
            	values[2] = params.getGyName();
            	values[3] = params.getParameterCategory();
            	values[4] = params.getProcessCategory();
            	values[5] = params.getUnit();
            	result.add(values );
            	index ++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

		return result;

	}

	public static List<String> getAllDeptsRMI() {
		return ext.casc.constants.Constants.allChejian;
	}

	public static Map<String, String> getSystemConfiguration() {
		return SystemConfigurationUtil.getAllSystemConfigurationBean();
	}
	
	/** 
	  * @Description: 设置文档状态
	  * @date 2025年10月29日下午6:54:49
	  * @author Liluwen
	  * @param docNumber
	  * @param status
	  * @return  
	  * @return 
	*/
	public static boolean setDocumentStatus(String docNumber,String status) {
		boolean flag = false;
		try {
			WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(docNumber);
			if(doc==null) {
				return flag;
			}
			LifeCycleManaged lm = (LifeCycleManaged) doc;
			LifeCycleTemplate lt = (LifeCycleTemplate) lm.getLifeCycleTemplate().getObject();
			Vector states = LifeCycleHelper.service.findStates(lt);
			State approvedStatus=State.toState(status);
			if (states.contains(approvedStatus)) {
				LifeCycleHelper.service.setLifeCycleState(lm, approvedStatus);
				flag=true;
			}
			doc=(WTDocument)PersistenceHelper.manager.refresh(doc);
			
			
			WTPart part=WTDocumentUtil.getLatestDescribesWTPartsByDocument(doc);
			if(part!=null) {
				//实例化工艺
				instantiationProcess(doc, part);
				//删除电子签名
				FilePrintUtil.removePrintAttachement(doc);
				//电子签名
				FilePrintUtil.writeProcessReviewtoPDF(doc);
				//同步主辅关联
				ZhuFuLinkUtil.syncZFLink(doc);
				//手动录入参数项目创建
				SopWorkflowUtil.saveInputParameters(doc);
				//建立SOP与参数项目关联
				SopWorkflowUtil.saveDocParametersLinks(doc);
				//建立工艺与依据文件关联
				SopWorkflowUtil.saveDocGJBZLinks(doc);
				//建立工艺与SAP关联
				SopWorkflowUtil.saveTecSopLinks(doc);
			}
			
			
		} catch (WTException e) {
			e.printStackTrace();
			flag=false;
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			flag=false;
			return flag;
		}
		return flag;
	}
	
	/** 
	  * @Description: 实例化工艺
	  * @date 2025年10月30日下午4:31:28
	  * @author Liluwen
	  * @param doc
	  * @param part
	  * @throws Exception
	  * @throws WTException  
	  * @return 
	*/
	private static void instantiationProcess(WTDocument doc, WTPart part) throws Exception, WTException {
		String masterVersion = part.getVersionIdentifier().getValue();

		// 实例化工艺，space版本实例化工艺
		if ("space".equals(masterVersion)) {
			ProcessPlanStructure processPlanStructure = new ProcessPlanStructure(doc, "", "");
			processPlanStructure.structureProcessPlan();
		} else { // 非space实例化工艺
			WTChangeOrder2 changeOrder = getRelatedChangeOrder(doc);
			if (changeOrder == null) {
				ProcessPlanStructure processPlanStructure = new ProcessPlanStructure(doc, "", "");
				processPlanStructure.structureProcessPlan();
			} else {
				String ecnType = WorkflowHelper.getEcnType(changeOrder);
				if (!"作废更改".equals(ecnType)) {
					boolean isSop = SopWorkflowUtil.isSop(doc);
					if (isSop) {
						StructureSopProcessPlan structure = new StructureSopProcessPlan(doc, part);
						structure.structure();
					} else {
						String topPartOid = part.getPersistInfo().getObjectIdentifier().getId() + "";
						String technicsOid = doc.getPersistInfo().getObjectIdentifier().getId() + "";
						String processType = "";
						ChangeProcessPlanStructure pps = new ChangeProcessPlanStructure(topPartOid, technicsOid,
								processType);
						pps.structureProcessPlan();
					}
				}
			}
		}
	}
	
	/** 
	  * @Description: 获取工艺文件关联的变更单
	  * @date 2025年10月31日下午2:31:51
	  * @author Liluwen
	  * @param doc
	  * @return  
	  * @return 
	*/
	private static WTChangeOrder2 getRelatedChangeOrder(WTDocument doc) {
		WTChangeOrder2 changeOrder=null;
		boolean enforce = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			QueryResult qur = ChangeHelper2.service.getUniqueImplementedChangeOrders(doc); 
			while(qur.hasMoreElements()) {
				changeOrder = (WTChangeOrder2) qur.nextElement();
			}
		} catch (ChangeException2 e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}finally {
			SessionServerHelper.manager.setAccessEnforced(enforce);
		}
		return changeOrder;
	}

	/**
	 * 方法功能: 获取部件及其子件下所有主工艺
	 *
	 * @param partOid
	 * @param userName
	 * @return java.util.Map<java.lang.String, org.dom4j.Element>
	 * @author cjh
	 * @date 2025年9月4日
	 */
	public static List<Element> getAllZhuZhiProcessPlan(String partOid, String userName) throws WTException, RemoteException {
		List<Element> techEleList = MPMProcessPlanUtil.getAllZhuZhiTechnicsElementByPart(partOid, userName);
		return techEleList;
	}

	/**
	 * 提交辅制工艺任务
	 *
	 * @param map
	 * @return Boolean
	 */
	public static List<String> createTaskItemBatch(Map<String, Map<String, String>> allMap) {
		List<String> list = new ArrayList<String>();
		for(String technicsNumber : allMap.keySet()) {
			boolean flag = true;
			Map<String, String> map = allMap.get(technicsNumber);
			try {
				flag = ProcessUtil.createProcessTaskItem(map);
			} catch(WTPropertyVetoException e) {
				flag = false;
				e.printStackTrace();
			} catch(WTException e) {
				flag = false;
				e.printStackTrace();
			}
			if(!flag) {
				list.add(technicsNumber);
			}
		}
		return list;
	}


	/**
	 * 方法功能: 获取部件及其子件下所有本人可以编辑的工艺
	 *
	 * @param partOid
	 * @param userName
	 * @return java.util.Map<java.lang.String, org.dom4j.Element>
	 * @author cjh
	 * @date 2025年9月4日
	 */
	public static List<Element> getAllInWorkProcessPlan(String partOid, String userName, String signedType) throws WTException, RemoteException {
		List<Element> techEleList = MPMProcessPlanUtil.getAllInWorkTechnicsElementByPart(partOid, userName, signedType);
		return techEleList;
	}

	/**
	 * 启动提交工艺审核流程
	 *
	 * @param topPartOid
	 * @param partOid
	 * @param technicsName
	 * @param unite
	 * @param technicsZip
	 * @param xmlVersion
	 * @return
	 * @author qianlong
	 * @date 2013-6-14
	 */
	public static HashMap<String, Map<String, String>> submitSignedBatchRMI(Map<String, Map<String, String>> inputMaps) {
		HashMap<String, Map<String, String>> returnMap = new HashMap<String, Map<String, String>>();
		for(String technicsNumber : inputMaps.keySet()) {
			Map<String, String> map = inputMaps.get(technicsNumber);
			String topPartOid = map.get("topPartOid");
			String partOid = map.get("partOid");
			String taskOid = map.get("taskOid");
			String technicsName = map.get("technicsName");
			String unite = map.get("unite");
			String xmlVersion = map.get("xmlVersion");
			String technicsCategory = map.get("technicsCategory");
			String technicsType = map.get("technicsType");
			technicsNumber = map.get("technicsNumber");
			String submitFlag = map.get("flag");
			String pplanNumber = map.get("pplanNumber");
			String docOid = map.get("docOid");
			String isSanJiGengGai = map.get("isSanJiGengGai");
			String startType = map.get("startType");
			if(docOid == null) {
				docOid = "";
			}
			String isReport = map.get("isReport");// true标识报表类工艺文件提交签审，false标识一般类工艺文件签审
			boolean flag = false;
			Map<String, String> returnMapItem = new HashMap<String, String>();
			try {
				returnMapItem.put(LIFECYCLE, "正在工作");
				returnMapItem.put(SUCCESS, SUCCESS);
				WTDocument doc = WTDocumentUtil.getLatestDocumentByNumber(technicsNumber);
				WTPart part = (WTPart) Util.getObjectByOid(WTPart.class, partOid);
				Map<String, String> variablesMap = new HashMap<String, String>();
				variablesMap.put("topPartOid", topPartOid);
				variablesMap.put("partOid", partOid);
				variablesMap.put("technicsOid", docOid);
				variablesMap.put("processType", technicsCategory);
				variablesMap.put("taskOid", taskOid);

				setProcessDocNumToProcessTaskItem(taskOid, technicsNumber);
				// 正常工艺
				if(Constants.normalProcess.equals(technicsCategory) || Constants.sopProcess.equals(technicsCategory)) {
					if(unite.equals("common")) {
						String workFlowTemplate = WorkflowConstants.SANJIGONGYIQIANSHENLIUCHENG;
						if("3".equals(submitFlag)) {
							if("false".equals(isReport)) {
								workFlowTemplate = WorkflowConstants.SANJIGONGYIQIANSHENLIUCHENG;
							} else if("true".equals(isReport)) {
								workFlowTemplate = WorkflowConstants.SANJIBAOBIAOLEIGONGYIQIANSHENLIUCHENG;
							}

						} else if("5".equals(submitFlag)) {
							workFlowTemplate = WorkflowConstants.WUJIGONGYIQIANSHENLIUCHENG;
							if("SOP".equals(startType)) {
								workFlowTemplate = SopConstants.SOP_WORKFLOW_SOPPROCESS;
							} else {
								if("false".equals(isReport)) {
									workFlowTemplate = WorkflowConstants.WUJIGONGYIQIANSHENLIUCHENG;
								} else if("true".equals(isReport)) {
									workFlowTemplate = WorkflowConstants.WUJIBAOBIAOLEIGONGYIQIANSHENLIUCHENG;
								}
							}
						}
						GLLogger.debug(CLASSNAME, "start " + workFlowTemplate + "......");
						flag = WorkflowUtil.startProcess(doc, workFlowTemplate, technicsNumber, variablesMap);
					} else {
						GLLogger.debug(CLASSNAME, "start 工艺合编签审流程...");
						flag = WorkflowUtil.startProcess(doc, WorkflowConstants.SANJIGONGYIQIANSHENLIUCHENG, technicsNumber, variablesMap);
					}
				}
				// 返工工艺
				else if(Constants.reworkProcess.equals(technicsCategory)) {
					variablesMap.put("technicName", technicsName);
					variablesMap.put("taskOid", taskOid);
					if(unite.equals("common")) {
						GLLogger.debug(CLASSNAME, "start 工艺返工流程审核流程...");
						flag = WorkflowUtil.startProcess(part, "工艺返工流程审核流程", technicsNumber, variablesMap);
					} else {
						GLLogger.debug(CLASSNAME, "start 工艺合编签审流程...");
						flag = WorkflowUtil.startProcess(part, "工艺审核流程", technicsNumber, variablesMap);
					}
				}
				// 临时工艺
				else if(Constants.tempProcess.equals(technicsCategory)) {
					variablesMap.put("technicName", technicsName);
					variablesMap.put("tempTaskOid", taskOid);
					if(unite.equals("common")) {
						GLLogger.debug(CLASSNAME, "start 工艺返工流程审核流程...");
						flag = WorkflowUtil.startProcess(part, "临时工艺审核流程", technicsNumber, variablesMap);
					} else {
						GLLogger.debug(CLASSNAME, "start 工艺合编签审流程...");
						flag = WorkflowUtil.startProcess(part, "工艺审核流程", technicsNumber, variablesMap);
					}
				}

				if(!flag) {
					returnMapItem.put(SUCCESS, FAILED);
					returnMapItem.put(ERRORMESSAGE, "工艺" + pplanNumber + "启动审核流程 出错！");
				}
			} catch(WTException e) {
				returnMapItem.put(SUCCESS, FAILED);
				returnMapItem.put(ERRORMESSAGE, "工艺" + pplanNumber + "启动审核流程 出错！");
				e.printStackTrace();
			}
			returnMap.put(pplanNumber, returnMapItem);
		}
		return returnMap;
	}

	public static List<Map<String, String>> searchUsers(String name) {
		List<Map<String, String>> userList = new ArrayList<>();
		try {
			List<WTUser> users = UserUtil.getWTUsersLikeNameAndFullName(name);
			for(WTUser user : users) {
				Map<String, String> userMap = new HashMap<>();
				userMap.put("name", user.getName());
				userMap.put("fullName", user.getFullName());
				userList.add(userMap);
			}
		} catch(WTException e) {
			e.printStackTrace();
		}
		return userList;
	}

}
