package com.glaway.mpm.sjzyk;

import com.glaway.mpm.util.WTPartUtil;
import ext.casc.util.WCUtil;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.library.WTLibrary;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;

import java.text.DateFormat;
import java.util.Date;

public class SjzykScheduleAll  implements RemoteAccess {

	private static final String[] CONTAINERS = {"八院标准紧固件库","八院元器件库","八院金属材料库","八院非金属材料库","八院复合材料库","八院机电产品库","八院火工品库"};
	private static final String[] MTYPE = {"标准件","元器件","金属材料","非金属材料","复合材料","机电产品","火工品"};


	/**
	 * 将设计资源库所有存储库中的零部件部分属性查询出来并统一写入到中间表
	 *
	 */
	public static void process() {
		System.out.println("starting query sjyzk all data ......"+DateFormat.getDateTimeInstance().format(new Date()));
		try {

			for (int i = 0; i < CONTAINERS.length ; i++) {
				System.out.println(CONTAINERS[i]);
				QueryResult qr = queryPartByContainer(CONTAINERS[i]);
				if(qr != null) {
					System.out.println(qr.size());
					while(qr.hasMoreElements()) {
						WTPart part = (WTPart)qr.nextElement();
						SjzykSchedule.updateSjzykMiddleTable(part);
					}
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 通过存储库名称，查询该存储库下所有的零部件(Design)
	 *
	 * @param containerName
	 * @return QueryResult
	 * @throws WTException
	 */
	public static QueryResult queryPartByContainer(String containerName) throws WTException {
		WTLibrary wtlib = WCUtil.getLibraryByName(containerName);
		View view = WTPartUtil.getViewByName("Design");
		if(wtlib != null) {
			long libId = PersistenceHelper.getObjectIdentifier(wtlib).getId();
			long viewId = PersistenceHelper.getObjectIdentifier(view).getId();
			QuerySpec qs = new QuerySpec(WTPart.class);
			qs.setAdvancedQueryEnabled(true);
			int[] index = { 0 };
			SearchCondition sc = new SearchCondition(WTPart.class,"containerReference.key.id",SearchCondition.EQUAL,libId);
			qs.appendWhere(sc, index);
			qs.appendAnd();
			sc = new SearchCondition(WTPart.class,"view.key.id",SearchCondition.EQUAL,viewId);
			qs.appendWhere(sc, index);
			QueryResult qr = PersistenceHelper.manager.find((StatementSpec)qs);
			LatestConfigSpec lcs = new LatestConfigSpec();
			qr = lcs.process(qr);
			return qr;
		} else {
			System.out.println(containerName+" is not exsit");
		}
		return null;
	}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SjzykScheduleAll.process();
	}



}
