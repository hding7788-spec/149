package ext.casc.constants;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ibm.icu.text.MessageFormat;
import com.ptc.windchill.mpml.resource.MPMPlant;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.library.WTLibrary;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;

/**
 * @author Liluwen 查询部门，工艺资源库下目录制造单位下的所有内容
 * @date 2025年11月5日下午4:12:08
 */
public class QueryDepartmentUtils {
	private static Logger LOGGER=Logger.getLogger(QueryDepartmentUtils.class);
	
	public static List<String> getElectronicDepartment(){
		List<String> list=new ArrayList<String>();
		try {
			WTLibrary library=QueryDepartmentUtils.getWTLibrary("工艺资源库");
			if(library==null) {
				return list;
			}
			
			Folder folder = FolderHelper.service.getFolder("/Default/分发部门/电子部门", WTContainerRef.newWTContainerRef(library));
			if(folder==null) {
				return list;
			}
			QueryResult qr=FolderHelper.service.findFolderContents(folder);
			while(qr.hasMoreElements()) {
				Object object=qr.nextElement();
				if(object instanceof MPMPlant) {
					MPMPlant plant=(MPMPlant)object;
					list.add(plant.getName());
				}
			}
			
		} catch (WTException e) {
			e.printStackTrace();
		}
		LOGGER.debug(MessageFormat.format("getAllDepartment list {0}", list));
		return list;
	}
	
	public static List<String> getAllDepartment(){
		List<String> list=new ArrayList<String>();
		try {
			WTLibrary library=QueryDepartmentUtils.getWTLibrary("工艺资源库");
			if(library==null) {
				return list;
			}
			
			Folder folder = FolderHelper.service.getFolder("/Default/分发部门/纸质部门", WTContainerRef.newWTContainerRef(library));
			if(folder==null) {
				return list;
			}
			QueryResult qr=FolderHelper.service.findFolderContents(folder);
			while(qr.hasMoreElements()) {
				Object object=qr.nextElement();
				if(object instanceof MPMPlant) {
					MPMPlant plant=(MPMPlant)object;
					list.add(plant.getName());
				}
			}
			
		} catch (WTException e) {
			e.printStackTrace();
		}
		LOGGER.debug(MessageFormat.format("getAllDepartment list {0}", list));
		return list;
	}
	
	public static  WTLibrary getWTLibrary(String name) throws WTException {
		WTLibrary library = null;
		boolean flag = true;
		try {
			flag = SessionServerHelper.manager.isAccessEnforced();
			SessionServerHelper.manager.setAccessEnforced(false);

			QuerySpec qs = new QuerySpec(WTLibrary.class);
			SearchCondition sc = new SearchCondition(WTLibrary.class, WTLibrary.NAME, SearchCondition.EQUAL, name);
			qs.appendWhere(sc,new int[]{0});
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.size() > 0) {
				library = (WTLibrary) qr.nextElement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}

		return library;
	}
}
