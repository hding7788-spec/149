package ext.casc.cadsign.wcserver;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.ObjectIdentifier;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipalReference;
import wt.pds.oracle81.OracleDataSource;
import wt.project.Role;
import wt.query.ClassAttribute;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.SubSelectExpression;
import wt.util.CollationKeyFactory;
import wt.util.SortedEnumeration;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.Iterated;
import wt.vc.IterationIdentifier;
import wt.vc.IterationInfo;
import wt.vc.VersionIdentifier;
import wt.vc.Versioned;
import wt.vc.config.LatestConfigSpec;
import wt.workflow.definer.WfTemplateObject;
import wt.workflow.engine.WfProcess;
import wt.workflow.work.Ballots;
import wt.workflow.work.WfAssignedActivity;
import wt.workflow.work.WfAssignment;
import wt.workflow.work.WfBallot;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkItemLink;
/**
 * <p>
 * Description:
 * </p>
 * 
 * @author:Oscar Zhong
 * @time: May 28, 2010 11:36:24 AM
 * @version 1.0
 */

public class CADSignUpdate implements RemoteAccess {

	// 签名完毕的flag,用来标识签名表信息,据此更新文档附件
	private static int hasSignFlag = 1;

	private static int hasUpdateDocFlag = 2;

	private static String wcFTPSignPath = "";

	private static String wcTempPath = "";
	
	private static String signFTPSignFolder = "";

	private static final String CLASSNAME = CADSignUpdate.class.getName();

	private static final String SIGNXML = "structure.xml";
	static {
		try {
			WTProperties wtProp = WTProperties.getLocalProperties();
			String wt_home = wtProp.getProperty("wt.home");
			wcFTPSignPath = CADSignHelper
							.getValueProperties("windchill.ftp.ftpbasepath");
			
			if (!wcFTPSignPath.endsWith("/") && !wcFTPSignPath.endsWith("\\")) {
				wcFTPSignPath = wcFTPSignPath + java.io.File.separator;
			}
			
			signFTPSignFolder = CADSignHelper
			.getValueProperties("sign.ftp.signFolder");
			
			String signFolder = CADSignHelper
					.getValueProperties("windchill.ftp.signFolder");
			//wcFTPSignPath = wcFTPSignPath + signFolder + java.io.File.separator;
			wcTempPath = wt_home
					+ CADSignHelper
							.getValueProperties("windchill.download.temppath");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("严重错误,获取wt.home值失败！");
		}
	}

	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		try {
			rms.invoke("scanSignTable", CLASSNAME, null, null, null);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 扫描签名表查找已签完名的压缩包并进行处理 11:38:30 AM
	 * 
	 * @throws SQLException
	 */
	public static void scanSignTable() {

		try{
			// 获取数据库所有签名完毕需要上传附件的记录
			String sql = "select ZIPNAME from signrequest where FLAG="
					+ hasSignFlag +" and SIGNTYPE='dwg'";
			Connection conn = OracleDataSource.getOracleDataSource()
					.getConnection();
			Statement state = conn.createStatement();
			ResultSet rs = state.executeQuery(sql);
			while (rs.next()) {
				String zipName = rs.getString("ZIPNAME");
				// 构建本地硬盘目录
				String zipLocalPath = wcFTPSignPath + signFTPSignFolder + File.separator + zipName + ".zip";
				File tempFile = new File(zipLocalPath);
				if(!tempFile.exists())
				{
					System.out.println("文件"+zipLocalPath+"不存在");
					continue;	
				}
				
				// 解压该文件夹
				String folder = wcTempPath + File.separator + zipName;
				ZipFileUtil.UnZipFile(zipLocalPath, folder);
				// 找到里面的xml文件
				String xmlPath = "";
				if (!folder.endsWith("/") && !folder.endsWith("\\")) {
					folder = folder + File.separator;
					xmlPath = folder + SIGNXML;
				} else {
					xmlPath = folder + SIGNXML;
				}
				// 处理,读取xml信息，并将对应的dwg文件传到文档附件
				parseXMLAndUpdateDoc(xmlPath);
				// 删除这个临时文件夹
				ZipFileUtil.deleteFiles(new File(xmlPath).getParent());
				// 修改数据信息
				String updateSql = "update signrequest set FLAG = "
						+ hasUpdateDocFlag + " where ZIPNAME='" + zipName + "'";
				System.out.println("updateSql=" + updateSql);
				Connection conn1 = OracleDataSource.getOracleDataSource()
						.getConnection();
				conn1.setAutoCommit(false);
				Statement state1 = conn1.createStatement();
				state1.executeUpdate(updateSql);
				conn1.commit();
				state1.close();
				conn1.close();
			}
			rs.close();
			state.close();
			conn.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	private static void parseXMLAndUpdateDoc(String structureXMLPath)
			throws Exception {
		SAXBuilder sax = new SAXBuilder();
		FileInputStream fis = new FileInputStream(structureXMLPath);
		Document doc = sax.build(fis);
		Element root = doc.getRootElement(); // root
		// CADDocument节点
		List list = root.getChildren();
		for (int i = 0; i < list.size(); i++) {
			Element cadele = (Element) list.get(i);
			String docNumber = cadele.getAttributeValue("DocID");
			String bigVersion = cadele.getAttributeValue("Version");
			String smallVersion = cadele.getAttributeValue("Iteration");
			String contentFile = cadele.getAttributeValue("ContentFileNmae");
			String contentFileLocalPath = new File(structureXMLPath)
					.getParent()
					+ File.separator + contentFile;
			WTDocument wtdoc = getDocument(docNumber, bigVersion, smallVersion);
			// 上传附件
			setDocAppData(wtdoc, contentFileLocalPath, true);
		}
		fis.close();
	}

	/*
	 * public static void main(String[] args){ String file =
	 * "D:\\XAC2Workspace\\XAC2_SVN_2010_02_08\\src\\ext\\xac2\\cadsign\\wcserver\\structure.xml";
	 * try { parseXMLAndUpdateDoc(file,"d:\\"); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */

	/**
	 * 获取指定大小版本和编号的文档对象
	 */
	public static WTDocument getDocument(String number, String version,
			String iteration) {
		if (number == null)
			return null;
		LatestConfigSpec latestconfigspec = null;
		QuerySpec queryspec;
		try {
			queryspec = new QuerySpec(WTDocument.class);
			queryspec.appendWhere(new SearchCondition(WTDocument.class,
					WTDocument.NUMBER, SearchCondition.EQUAL, number
							.toUpperCase(), false));

			if (version != null) {
				queryspec.appendAnd();
				queryspec.appendWhere(new SearchCondition(WTDocument.class,
						Versioned.VERSION_IDENTIFIER + "."
								+ VersionIdentifier.VERSIONID,
						SearchCondition.EQUAL, version, false));
				if (iteration != null) {
					queryspec.appendAnd();
					queryspec.appendWhere(new SearchCondition(WTDocument.class,
							Iterated.ITERATION_IDENTIFIER + "."
									+ IterationIdentifier.ITERATIONID,
							SearchCondition.EQUAL, iteration, false));
				} else {
					queryspec.appendAnd();
					queryspec.appendWhere(new SearchCondition(WTDocument.class,
							Iterated.ITERATION_INFO + "."
									+ IterationInfo.LATEST, "TRUE"));
				}
			} else {
				latestconfigspec = new LatestConfigSpec();
				latestconfigspec.appendSearchCriteria(queryspec);
			}
			QueryResult queryresult = PersistenceHelper.manager.find(queryspec);
			if (latestconfigspec != null)
				queryresult = latestconfigspec.process(queryresult);
			if (queryresult.size() == 1) {
				WTDocument doc = (WTDocument) queryresult.nextElement();
				return doc;
			}
			if (queryresult.size() > 1) {
				System.err.println("无法获得唯一文档对象。。。");
			}
		} catch (QueryException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void setDocAppData(WTDocument wtdocument, String filePath,
			Boolean deleteOld) throws Exception {

		File attFile = new File(filePath);
		File newNameFile = new File(attFile.getParent() + File.separator
				+ "Print_" + attFile.getName());
		
		ContentHolder contentHolder = ContentHelper.service
				.getContents((ContentHolder) wtdocument);
		Vector apps = ContentHelper.getApplicationData(contentHolder);
		if (deleteOld) {
			if (apps != null && apps.size() > 0) {
				for (int i = 0; i < apps.size(); i++) {
					ApplicationData applicationdata = (ApplicationData) apps
							.elementAt(i);
					if(applicationdata.getFileName().equals(newNameFile.getName()))
					{
						ContentServerHelper.service.deleteContent(contentHolder,
								applicationdata);
						contentHolder = (ContentHolder) PersistenceHelper.manager
							.refresh(contentHolder);
					}
				}
			}
		}
		ContentHolder holder = contentHolder;
		
		attFile.renameTo(newNameFile);
		if (newNameFile.exists()) {
			ApplicationData app_data = ApplicationData
					.newApplicationData(holder);
			app_data.setFileName(newNameFile.getName());
			app_data.setUploadedFromPath(newNameFile.getAbsolutePath());
			app_data.setRole(ContentRoleType.SECONDARY);
			app_data.setFileSize(newNameFile.length());
			holder = (ContentHolder) PersistenceHelper.manager.refresh(holder);
			if (newNameFile.exists()) {
				app_data = ContentServerHelper.service.updateContent(holder,
						app_data, newNameFile.getPath());
			}
			/*
			 * // 删除临时文件 if (attFile.exists()) { newNameFile.delete(); }
			 */
		} else {
			System.out.println("不存在文件 ....." + filePath);
		}
	}
   
	public static Hashtable getReviews(ObjectReference self) throws WTException {
		WfProcess wfp = (WfProcess) self.getObject();
		WfTemplateObject wftemplateobject = null;
		String activityName = "";
		String roleStr = "";
		String rolePrincipalName = "";
		String roleComments = "";
		String endTime = "";
		Hashtable reviewHashtable = new Hashtable();
		Hashtable tmpReviewHashtable = new Hashtable();
		ReferenceFactory rf = new ReferenceFactory();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.SSS");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
		QueryResult qr = getWorkItems(wfp);
		String array[] = new String[qr.size()];
		// 按时间排序,取最新一个
		CollationKeyFactory timeKeyFact = new CollationKeyFactory() {
			public String getCollationString(Object o) {
				if (!(o instanceof Persistable)
						|| !PersistenceHelper.isPersistent(o))
					return "";
				return ((Persistable) o).getPersistInfo().getModifyStamp()
						.toString();
			}
		};
		boolean activityFlag = false;
		while (qr.hasMoreElements()) {
			WorkItem witem = (WorkItem) qr.nextElement();
			if (witem.getSource().getObject() instanceof WfAssignedActivity) {
				WfAssignedActivity wfactivity = (WfAssignedActivity) witem
						.getSource().getObject();
				wftemplateobject = (WfTemplateObject) wfactivity.getTemplate()
						.getObject();
				activityName = wfactivity.getName();
				Enumeration enAssignments = wfactivity.getAssignments();
				while (enAssignments.hasMoreElements()) {
					WfAssignment wa = (WfAssignment) enAssignments
							.nextElement();
					// 获取投票意见
					HashMap mBallots = new HashMap();
					QueryResult qrBallots = PersistenceHelper.manager.navigate(
							wa, Ballots.BALLOT_ROLE, Ballots.class, true);
					while (qrBallots.hasMoreElements()) {
						WfBallot ballot = (WfBallot) qrBallots.nextElement();
						Vector events = ballot.getEventList();
						String uid = rf.getReferenceString(ballot.getVoter());
						StringBuffer buf = new StringBuffer();
						for (int k = 0; events != null && k < events.size(); k++) {
							if (buf.length() > 0)
								buf.append(",");
							buf.append(events.get(k));
						}
						mBallots.put(uid, buf.toString());
					}
					// 取每个工作项项的角色、人名
					QueryResult qrItems = PersistenceHelper.manager.navigate(
							wa, WorkItemLink.WORK_ITEM_ROLE,
							WorkItemLink.class, true);
					Enumeration enItems = new SortedEnumeration(qrItems,
							timeKeyFact, SortedEnumeration.DESCENDING);
					while (enItems.hasMoreElements()) {
						Hashtable tmpHashtable = new Hashtable();
						WorkItem wi = (WorkItem) enItems.nextElement();
						String comment = wi.getContext().getTaskComments();
						if (!wi.isComplete())
							continue; 

						Role role = wi.getRole();
						WTPrincipalReference user = wi.getOwnership()
								.getOwner();
						Timestamp time = wi.getPersistInfo().getModifyStamp();
						tmpHashtable.put("activityName", activityName);
						tmpHashtable.put("rolePrincipalName", user
								.getFullName());
						tmpHashtable.put("rolePrincipalId", user.getName());
						tmpHashtable.put("endTime", sdf1.format(time));
						tmpHashtable.put("roleComments", comment);
						tmpReviewHashtable.put(sdf.format(time) + ","
								+ activityName + "," + user.getFullName(),
								tmpHashtable);
					}
				}
			}
		}
		Map.Entry[] set = getSortedHashtable(tmpReviewHashtable);
		for (int j = 0; j < set.length; j++) {
			Hashtable newHashtable = (Hashtable) tmpReviewHashtable.get(set[j]
					.getKey().toString());
			String newActivity = (String) newHashtable.get("activityName");
			if (!reviewHashtable.containsKey(newActivity)) {
				reviewHashtable.put(newActivity, newHashtable);
			}
		}
		return reviewHashtable;
	}

	public static QueryResult getWorkItems(WfProcess wfprocess)
			throws WTException {
		return getWorkItems(wfprocess, false);
	}

	public static QueryResult getWorkItems(WfProcess wfprocess, boolean flag)
			throws WTException {
		QueryResult queryresult;
		QuerySpec queryspec = new QuerySpec();
		queryspec.setAdvancedQueryEnabled(true);
		int i = queryspec.appendClassList(WfAssignedActivity.class, false);
		queryspec.appendSelectAttribute(
				"thePersistInfo.theObjectIdentifier.id", i, false);
		queryspec.appendWhere(new SearchCondition(WfAssignedActivity.class,
				"parentProcessRef.key", "=", getOid(wfprocess)));
		SubSelectExpression subselectexpression = new SubSelectExpression(
				queryspec);
		QuerySpec queryspec1 = new QuerySpec(WorkItem.class);
		queryspec1.setAdvancedQueryEnabled(true);
		if (flag)
			queryspec1.appendWhere(new SearchCondition(WorkItem.class,
					"completedBy", !flag), 0);
		ClassAttribute classattribute = new ClassAttribute(WorkItem.class,
				"source.key.id");
		if (flag)
			queryspec1.appendAnd();
		SearchCondition searchcondition = new SearchCondition(classattribute,
				"IN", subselectexpression);
		queryspec1.appendWhere(searchcondition, 0);
		queryresult = PersistenceServerHelper.manager.query(queryspec1);
		return queryresult;
	}

	private static ObjectIdentifier getOid(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof ObjectReference)
			return (ObjectIdentifier) ((ObjectReference) obj).getKey();
		else
			return PersistenceHelper.getObjectIdentifier((Persistable) obj);
	}

	public static Map.Entry[] getSortedHashtable(Hashtable h) {
		Set set = h.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set
				.size()]);
		Arrays.sort(entries, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				Object key1 = ((Map.Entry) arg1).getKey();
				Object key2 = ((Map.Entry) arg0).getKey();
				return ((Comparable) key1).compareTo(key2);
			}
		});
		return entries;
	}
}
