package ext.casc.capp.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.glaway.mpm.util.WTPartUtil;

import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.ReferenceFactory;
import wt.inf.container.WTContainerRef;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTRuntimeException;
import wt.vc.wip.WorkInProgressHelper;
import ext.casc.doc.CSCDoc;
import ext.casc.part.CSCPart;
import ext.casc.part.PackagedPartHelper;
import ext.casc.util.ExcelFileGenerator;
import ext.casc.util.IBAUtil;

public class StandardCAPPReportService  extends StandardManager implements CAPPReportService,Serializable{

	private static final long serialVersionUID = 106037350751614703L;

	public static StandardCAPPReportService newStandardCAPPReportService()throws WTException {
		StandardCAPPReportService instance = new StandardCAPPReportService();
		instance.initialize();
		return instance;
	}

	@Override
	public boolean importBOM(File file) throws WTException {
		HSSFWorkbook hssfWorkbook = null;
		try {
			hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
		HSSFRow row = null;
		HSSFRow titleRow = null;
		for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
			titleRow = sheet.getRow(1);
			row = sheet.getRow(i);
			if (row != null) {
				for (int j = 0; j < row.getLastCellNum(); j++) {
					Object titleCell = titleRow.getCell((short) j);
					Object cell = row.getCell((short) j);

				}

			}
		}
		return false;
	}

	@Override
	public synchronized File exportCHGLB(String oid) throws Exception {
		Transaction  trx = new Transaction();
        trx.start();
		File file = null;
		ArrayList<String> titles = buildTitles();
		ArrayList<ArrayList<String>> values = buildValues(oid);
		ExcelFileGenerator gen = new ExcelFileGenerator(titles,values);
		WTProperties wtp = WTProperties.getLocalProperties();
		String temp = wtp.getProperty("wt.temp");
		file = new File(temp + File.separator + "存货管理表.xls");
		gen.expordExcel(new FileOutputStream(file));
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();
		HashMap attributes = new HashMap();
		attributes.put(CSCDoc.FOLDER, "02工艺文件/90其他文档");
		attributes.put(CSCDoc.FILE_PATH, temp + File.separator + "存货管理表.xls");
		attributes.put(CSCDoc.TYPE, "wt.doc.WTDocument");
		WTContainerRef containerRef = part.getContainerReference();
		WTDocument doc = CSCDoc.createDoc(new Date().getTime()+"","存货管理","存货管理自动生成",attributes,containerRef);

		WTPartUtil.createWTPartDescribeLink(part, doc);

		trx.commit();
        trx = null;
		return file;
	}

	private ArrayList<ArrayList<String>> buildValues(String oid) throws WTRuntimeException, RemoteException, WTException {
		 List<WTPart> partList = PackagedPartHelper.getCHGLPartBOMData(oid);
		 ArrayList<ArrayList<String>> allList = new ArrayList<ArrayList<String>>();
		 for(WTPart part:partList){
			 ArrayList<String> list = new ArrayList<String>();
			 IBAUtil ibaUtil = new IBAUtil(part);
			 list.add(part.getContainer().getName());//分类编码或名称(必填)
			 list.add(ibaUtil.getIBAValue("PHASE_CODE")+"%"+ibaUtil.getIBAValue("CINDEX"));//存货编码(必填)
			 list.add(part.getName());//存货名称(必填)
			 list.add(ibaUtil.getIBAValue("CINDEX"));//图号(图号档案必填)
			 list.add("");//规格
			 list.add("");//型号牌号
			 list.add("");//技术条件
			 list.add("");	//生产单位
			 list.add("");	//封装形式
			 list.add(""); //精度等级
			 list.add("");	//质量等级
			 list.add(part.getContainer().getName());//图号型号(图号档案必填)  产品型号
			 list.add(ibaUtil.getIBAValue("PHASE_CODE"));//研制阶段(图号档案必填)
			 list.add(ibaUtil.getIBAValue("CTYPE"));//存货类型(外购,,自制,外配套，带料委外，不带料委外)
			 list.add("");//产品代号
			 list.add("");//军标号
			 list.add("");//存货简称
			 list.add("件");//主计量单位(必填)
			 list.add("");//备注
			 list.add("N");//是否辅计量管理(全部填N)
			 list.add("0");//计划价
			 list.add("Y");//是否批次管理(全部填Y）
			 list.add("Y");//是否保质期管理
			 list.add("30000");//保质期天数(保质期管理的不能填0）
			 list.add("");//附加条件

			 allList.add(list);
		 }
		return allList;
	}

	private ArrayList<String> buildTitles(){
		ArrayList<String> titles = new ArrayList<String>();
		titles.add("分类编码或名称(必填)");
		titles.add("存货编码(必填)");
		titles.add("存货名称(必填)");
		titles.add("图号(图号档案必填)");
		titles.add("规格");
		titles.add("型号牌号");
		titles.add("技术条件");
		titles.add("生产单位");
		titles.add("封装形式");
		titles.add("精度等级");
		titles.add("质量等级");
		titles.add("图号型号(图号档案必填)");
		titles.add("研制阶段(图号档案必填)");
		titles.add("存货类型(外购,,自制,外配套，带料委外，不带料委外)");
		titles.add("产品代号");
		titles.add("军标号");
		titles.add("存货简称");
		titles.add("主计量单位(必填)");
		titles.add("备注");
		titles.add("是否辅计量管理(全部填N)");
		titles.add("计划价");
		titles.add("是否批次管理(全部填Y）");
		titles.add("是否保质期管理");
		titles.add("保质期天数(保质期管理的不能填0）");
		titles.add("附加条件");
		return titles;
	}

}
