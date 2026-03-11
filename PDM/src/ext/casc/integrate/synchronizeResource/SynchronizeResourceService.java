package ext.casc.integrate.synchronizeResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import wt.fc.IdentityHelper;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartMasterIdentity;
import wt.pom.UniquenessException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

import com.glaway.mpm.constants.Constants;
import com.glaway.mpm.constants.TypeNameConstants;
import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.tool.ImportMPMResourceTool;
import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.MPMResourceUtil;
import com.glaway.mpm.util.WTContainerUtil;
import com.lowagie.text.pdf.codec.Base64.OutputStream;
import com.ptc.windchill.mpml.resource.MPMTooling;
import com.ptc.windchill.mpml.resource.MPMToolingMaster;

import ext.casc.ixb.DataImportHandler;
import ext.casc.util.QueryHelper;

public class SynchronizeResourceService implements RemoteAccess{

	public static String sychronize(String str) throws FileNotFoundException,
			WTPropertyVetoException, IOException, DocumentException {
		 if (!RemoteMethodServer.ServerFlag) {
            String method = "sychronize";
            Class[] types = { String.class};
            Object[] vals = { str};
            RemoteMethodServer rms = RemoteMethodServer.getDefault();
            try {
				return (String)rms.invoke(method, SynchronizeResourceService.class.getName(), null, types, vals);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//		File file = new File(str);
//		SAXReader saxReader = new SAXReader();
//		saxReader.setEncoding("GBK");
		Document document = null;
		Map<String, Map<String, List<String>>> mpmResourMap = null;
		try {
//			document = saxReader.read(file);
//			document = DocumentHelper.parseText(Readfile(str));
			document = DocumentHelper.parseText(str);
			mpmResourMap = new HashMap<String, Map<String, List<String>>>();
			Element root = document.getRootElement();
			String RequestType = getRequestType(root).getTextTrim();
			System.out.println(RequestType);
			if (RequestType != null && RequestType.equals("I01")) {
				String objectType = TypeNameConstants.GZhuang;
				getAllFrockResour(root, mpmResourMap);
				SynchronizeForck(mpmResourMap, objectType);

			} else if (RequestType != null && RequestType.equals("I02")) {
				getAllEquipResour(root, mpmResourMap);
				SynchronizeEquipment(mpmResourMap);
			}
			sb.append("<result>");
			sb.append("<type>");
			sb.append("0");
			sb.append("</type>");
			sb.append("<message>");
			sb.append("</message>");
			sb.append("</result>");
		} catch (Exception e) {
			// e.printStackTrace();
			sb.append("<result>");
			sb.append("<type>");
			sb.append("1");
			sb.append("</type>");
			sb.append("<message>");
			sb.append("系统异常，请联系系统管理员！");
			sb.append("</message>");
			sb.append("</result>");
		}
		String s=sb.toString();
		return sb.toString();
	}

	// 获取解析获取所有工装
	public static void getAllFrockResour(Element root,
			Map<String, Map<String, List<String>>> mpmResourMap) {
		Element bodyElement = root.element("Body");
		List<Element> elements = bodyElement.elements();
		for (Element childElement : elements) {
			// 工装类别
			String FROCKTYPE = childElement.element("gztype").getText();
			// 编号
			String NUMBER = childElement.element("gzcode").getText();
			// 名称
			String NAME = childElement.element("gzname").getText();
			// 使用状态ID
			String STATE = childElement.element("dr").getText();
			// NC编号（唯一）
			String ncid = childElement.element("ncid").getText();

			Map<String, List<String>> attr = null;
			String folderpath = getFolderPath(TypeNameConstants.GZFloder,
					FROCKTYPE);
			if (mpmResourMap.get(folderpath) == null) {
				attr = new HashMap<String, List<String>>();
			} else {
				attr = mpmResourMap.get(folderpath);
			}
			List<String> list = new ArrayList<String>();
			list.add(NUMBER);
			list.add(NAME);
			list.add(FROCKTYPE);
			list.add(STATE);
			attr.put(ncid, list);
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i));
			}
			System.out.println(folderpath + "--" + ncid);
			mpmResourMap.put(folderpath, attr);
		}
	}

	// 获取解析获取所有设备
	public static void getAllEquipResour(Element root,
			Map<String, Map<String, List<String>>> mpmResourMap) {
		Element bodyElement = root.element("Body");
		List<Element> elements = bodyElement.elements();
		for (Element childElement : elements) {
			// 所属型号
			String MINDEX = childElement.element("xhtype").getText();
			// 编号
			String NUMBER = childElement.element("zccode").getText();
			// 名称
			String NAME = childElement.element("name").getText();
			// 规格
			String STANDARD = childElement.element("ggtype").getText();
			// 使用状态ID
			String STATE = childElement.element("dr").getText();
			// NC编号（唯一）
			String ncid = childElement.element("ncid").getText();

			Map<String, List<String>> attr = null;
//			String keynumber = NUMBER.substring(NUMBER.indexOf(0),NUMBER.indexOf(4));
			String keynumber=String.valueOf(NUMBER.toCharArray(), 0, 4);
			String folderpath = TypeNameConstants.ResourceMap.get(keynumber);
			if (folderpath == null || "".equals(folderpath)) {
				folderpath = "/Default/量具";
			}
			if (mpmResourMap.get(folderpath) == null) {
				attr = new HashMap<String, List<String>>();
			} else {
				attr = mpmResourMap.get(folderpath);
			}
			List<String> list = new ArrayList<String>();
			list.add(NUMBER);
			list.add(NAME);
			list.add(MINDEX);
			list.add(STANDARD);
			list.add(STATE);
			attr.put(ncid, list);
			mpmResourMap.put(folderpath, attr);
		}
	}

	// 得到要创建的类型：工装/设备
	public static Element getRequestType(Element root) {
		Element headElement = root.element("Head");
		return headElement.element("RequestType");
	}

	// 得到文件夹路径
	public static String getFolderPath(String path, String type) {
		StringBuffer buff = new StringBuffer(path);
		buff.append("/");
		buff.append(type);
		return buff.toString();
	}

//	public static void main(String[] args) {
//
//		try {
//			RemoteMethodServer rms=RemoteMethodServer.getDefault();
//			rms.setUserName("wcadmin");
//			rms.setPassword("wcadmin");
//			System.out.println(sychronize("c:/shb.txt"));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (WTPropertyVetoException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (WTException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	/*
	 * PDM-NC工装同步
	 */

	public static void SynchronizeForck(
			Map<String, Map<String, List<String>>> mpmResourMap,
			String objectType){

		System.out.println( "3333333333334444444444455");
		WTContainer container=null;
		try {
			container = WTContainerUtil
					.getLibraryByName(Constants.mpmResourceLibraryName);
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (String folderpath : mpmResourMap.keySet()) {
			try {
				ProcessEditorToWCIntfRMI.getFolder(folderpath,
						WTContainerRef.newWTContainerRef(container));
				for (String ncid : mpmResourMap.get(folderpath).keySet()) {
					List<String> attr = mpmResourMap.get(folderpath).get(ncid);
					//根据NCID查询
					MPMTooling frock=null;
					try {
						frock = getMpmToolingByNcid(ncid, objectType);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WTPropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(frock+"test0!!!!!!!");
					if (frock == null) {
						//根据number查询
						MPMToolingMaster master = MPMResourceUtil.getMPMToolingMasterByNumber(attr.get(0).toUpperCase());
						if(master==null){
							String tempNumber=attr.get(0);
							int n=tempNumber.indexOf("%");
							if(n>-1){
								tempNumber=tempNumber.substring(n+1);
							}else{
								tempNumber="A%"+tempNumber;
							}
							master = MPMResourceUtil.getMPMToolingMasterByNumber(tempNumber.toUpperCase());
							if(master!=null){
								GLLogger.debug("createTooling() number:" + attr.get(0) + " is exsited!");
								QueryResult qResult = VersionControlHelper.service.allVersionsOf(master);
								frock=(MPMTooling)qResult.nextElement();
								System.out.println(frock+"test2!!!!!!!");
							}else{
								try {
									frock = MPMResourceUtil.onlyCreateTooling(attr.get(0),
											attr.get(1), container, folderpath, objectType,
											"");
								} catch (WTPropertyVetoException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println(frock+"test3!!!!!!");
							}
						}else{
							GLLogger.debug("createTooling() number:" + attr.get(0) + " is exsited!");
							QueryResult qResult = VersionControlHelper.service.allVersionsOf(master);
							frock=(MPMTooling)qResult.nextElement();
							System.out.println(frock+"test5!!!!!!!");
						}
					}
						rename(attr.get(1),attr.get(0),frock);
						ImportMPMResourceTool.setMyIBAStringValue(frock,
								"FROCKTYPE", attr.get(2));
						ImportMPMResourceTool.setMyIBAStringValue(frock, "ncid",
								ncid);
					if (String.valueOf(0).equals(attr.get(3))) {
						LifeCycleHelper.service.setLifeCycleState(frock,
								State.toState("INWORK"));
					} else if (String.valueOf(1).equals(attr.get(3))) {
						LifeCycleHelper.service.setLifeCycleState(frock,
								State.toState("OBSOLESCENCE"));
					}
				}

			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * PDM-NC设备同步
	 */

	public static void SynchronizeEquipment(
			Map<String, Map<String, List<String>>> mpmResourMap){
		WTContainer container=null;
		try {
			container = WTContainerUtil
					.getLibraryByName(Constants.mpmResourceLibraryName);
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for (String folderpath : mpmResourMap.keySet()) {
			try {
				String objectType = null;
				if (folderpath.indexOf("量具") > -1) {
					objectType = TypeNameConstants.LJ;
				} else if (folderpath.indexOf("设备") > -1) {
					objectType = TypeNameConstants.SB;
				} else if (folderpath.indexOf("仪器仪表") > -1) {
					objectType = TypeNameConstants.YQYB;
				} else if (folderpath.indexOf("非标准仪器仪表") > -1) {
					objectType = TypeNameConstants.FBZYQYB;
				}
				ProcessEditorToWCIntfRMI.getFolder(folderpath,
						WTContainerRef.newWTContainerRef(container));
				for (String ncid : mpmResourMap.get(folderpath).keySet()) {
					List<String> attr = mpmResourMap.get(folderpath).get(ncid);
					MPMTooling equipment=null;
					try {
						equipment = getMpmToolingByNcid(ncid,
								objectType);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (WTPropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println(equipment+"test00!!!!!!!");
					if (equipment == null) {
						MPMToolingMaster master = MPMResourceUtil.getMPMToolingMasterByNumber(attr.get(0).toUpperCase());
						if(master!=null){
							GLLogger.debug("createTooling() number:" + attr.get(0) + " is exsited!");
							QueryResult qResult = VersionControlHelper.service.allVersionsOf(master);
							equipment=(MPMTooling)qResult.nextElement();
							System.out.println(equipment+"test02!!!!!!!");
						}else{
							//根据名称+型号+规格查询
							Map<String, String> ibamap = new HashMap<String, String>();
							String name=attr.get(1);
							ibamap.put("MINDEX", attr.get(2));
							ibamap.put("STANDARD", attr.get(3));
							try {
								equipment=getMpmToolingByNameIBA(name, ibamap);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (WTPropertyVetoException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println(equipment+"test03!!!!!!!");
							if(equipment!=null){
								continue;
							}else{
								try {
									equipment = MPMResourceUtil.onlyCreateTooling(attr.get(0),
											attr.get(1), container, folderpath, objectType,
											"");
								} catch (WTPropertyVetoException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								System.out.println(equipment+"test04!!!!!!!");
							}

						}
					}
					rename(attr.get(1),attr.get(0),equipment);
					ImportMPMResourceTool.setMyIBAStringValue(equipment,
							"MINDEX", attr.get(2));
					ImportMPMResourceTool.setMyIBAStringValue(equipment,
							"STANDARD", attr.get(3));
					ImportMPMResourceTool.setMyIBAStringValue(equipment,
							"ncid", ncid);
					if (String.valueOf(0).equals(attr.get(4))) {
						LifeCycleHelper.service.setLifeCycleState(equipment,
								State.toState("INWORK"));
					} else if (String.valueOf(1).equals(attr.get(4))) {
						LifeCycleHelper.service.setLifeCycleState(equipment,
								State.toState("OBSOLESCENCE"));
					}
				}

			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static MPMTooling getMpmToolingByNcid(String ncid, String objectType)
			throws RemoteException, WTException, WTPropertyVetoException {
		Map<String, String> ibamap = new HashMap<String, String>();
		ibamap.put("ncid",ncid);
		MPMTooling mpmtooling = QueryHelper.queryMPMToolingByIBA(ibamap);
		return mpmtooling;
	}


	public static MPMTooling getMpmToolingByNameIBA(String name,Map<String, String> ibamap)
			                 throws RemoteException, WTException, WTPropertyVetoException{
		MPMTooling mpmtooling = QueryHelper.queryMPMToolingByIBA(name, ibamap);
		return mpmtooling;
	}

	/*
	 * 更改对象的name和number
	 *
	 * @param name
	 * @param number
	 * @param obj
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static void rename(String name, String number, WTObject obj) {
		if (obj instanceof WTPart) {
			WTPart wtp = (WTPart) obj;
			WTPartMaster wtpMaster = (WTPartMaster) wtp.getMaster();

			WTPartMasterIdentity wtpMasterIdentity=null;
			try {
					wtpMasterIdentity = (WTPartMasterIdentity) wtpMaster
							.getIdentificationObject();
					if (name != null)
						wtpMasterIdentity.setName(name);
					if (number != null)
					wtpMasterIdentity.setNumber(number);
					wtpMaster = (WTPartMaster) IdentityHelper.service
									.changeIdentity(wtpMaster, wtpMasterIdentity);
				} catch (WTPropertyVetoException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			    }
		}
	}

	public static String Readfile(String str){
		StringBuffer sb=new StringBuffer();
		   try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(str)));
				String data=null;
				while((data=br.readLine())!=null){
					sb.append(data);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return sb.toString();
	}
}
