package ext.casc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import wt.method.RemoteAccess;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.ptc.netmarkets.util.table.NmDefaultHTMLTable;


public class AssignmentHelper implements RemoteAccess, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	public static final String ELEC_CONFIG_PATH = "codebase" + File.separator + "ext" + File.separator + "casc" + File.separator + "outerdoc" + File.separator
			+ "electronic.xls";

	public static NmDefaultHTMLTable getConfigReleaseDocTable(List<Map<String, String>> departList, List<String> releaseList) throws WTPropertyVetoException {
		NmDefaultHTMLTable table = new NmDefaultHTMLTable();
		table.setName("发放单位");
		table.setRowSelectable(false);
		table.addColumn("");
		table.addColumn("单位简称");
		table.addColumn("单位全称");

		table.getColumnModel().getColumn(0).setSortable(false);
		table.getColumnModel().getColumn(1).setSortable(false);
		table.getColumnModel().getColumn(2).setSortable(false);

		for (int i = 0; i < departList.size(); i++) {
			Map<String, String> tempMap = departList.get(i);
			String dwjc = tempMap.get("单位简称");
			String dwqc = tempMap.get("单位全称");
			String value = dwjc;
			String checked = "";

			if(releaseList.contains(dwjc)){
				checked = "checked";
			}
			String checkbox_str = "<input type='radio' name='departCheck' value='" + value + "'" + checked + ">";
			NmTableGUIComponent checkboxComponent = new NmTableGUIComponent(checkbox_str);
			table.addCellValue(i, 0, checkboxComponent);
			table.addCellValue(i, 1, dwjc);
			table.addCellValue(i, 2, dwqc);
		}

		table.setScrollable(true);

		return table;
	}


	/**多发放改造之后，为方便历史流程只能选择149厂，新流程选择新配置的厂所而添加
	 * @return
	 */
	public static List<Map<String, String>> getElecConfig() {
		List<Map<String, String>> deptList = null;
		String wtHome = null;
		try {
			wtHome = WTProperties.getLocalProperties().getProperty("wt.home");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("wtHome = "+wtHome);
		if (wtHome != null) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(wtHome + File.separator + ELEC_CONFIG_PATH);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			if (fis != null) {
				System.out.println("wtHomeaa = "+wtHome);
				deptList = readExcel(fis);
			}
		}

		Object[] tempMap = deptList.toArray();
//		Arrays.sort(tempMap, new ChineseComparator3());

		deptList = new ArrayList<Map<String, String>>();
		for (int i = 0; i < tempMap.length; i++) {
			if (tempMap[i] instanceof HashMap) {
				HashMap<String, String> map = (HashMap<String, String>) tempMap[i];
				deptList.add(map);
			}
		}

		return deptList;
	}

	@SuppressWarnings("deprecation")
	public static List<Map<String, String>> readExcel(InputStream is) {
		List<Map<String, String>> excelData = new ArrayList<Map<String, String>>();
		List<String> title = null;
		HSSFWorkbook hssfworkbook = null;
		HSSFSheet hssfsheet = null;

		try {
			hssfworkbook = new HSSFWorkbook(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (hssfworkbook == null) {
			return excelData;
		}

		int count = hssfworkbook.getNumberOfSheets();

		for (int i = 0; i < count; i++) {
			title = new ArrayList<String>();
			hssfsheet = hssfworkbook.getSheetAt(i);
			if (hssfsheet == null) {
				continue;
			}

			int rowLength = hssfsheet.getLastRowNum();
			int cellLength;

			HSSFRow hssfrowFirst = hssfsheet.getRow(0);

			if (hssfrowFirst == null) {

				continue;
			}

			cellLength = hssfrowFirst.getLastCellNum();

			for (int r = 0; r < cellLength; r++) {
				HSSFCell hssfcell = hssfrowFirst.getCell((short) r);
				title.add(hssfcell.getStringCellValue().trim());
			}

			for (int j = 1; j <= rowLength; j++) {
				HSSFRow hssfrow = hssfsheet.getRow(j);
				Map<String, String> map = new HashMap<String, String>();
				for (int k = 0; k < cellLength; k++) {
					HSSFCell hssfcell = hssfrow.getCell((short) k);
					if (hssfcell == null) {
						map.put(title.get(k), "");
						break;
					}
					int type = hssfcell.getCellType();

					switch (type) {
					case 0:
						map.put(title.get(k), Integer.toString((int) hssfcell.getNumericCellValue()));
						break;
					case 1:
						map.put(title.get(k), hssfcell.getStringCellValue().trim());
						break;
					case 3:
						map.put(title.get(k), hssfcell.getStringCellValue());
						break;
					}
				}
				excelData.add(map);
			}
		}

		return excelData;
	}







}
