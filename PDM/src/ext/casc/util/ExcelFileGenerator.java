/**
 * 系统数据导出Excel 生成器
 * @version 1.0
 */
package ext.casc.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;

import java.io.OutputStream;
import java.util.ArrayList;

public class ExcelFileGenerator {

	private final int SPLIT_COUNT = 1500; //Excel每个工作簿的行数

	private ArrayList fieldName = null; //excel标题数据集

	private ArrayList fieldData = null; //excel数据内容	

	private HSSFWorkbook workBook = null;

	/**
	 * 构造器
	 * @param fieldName 结果集的字段名
	 * @param data
	 */
	public ExcelFileGenerator(ArrayList fieldName, ArrayList fieldData) {

		this.fieldName = fieldName;
		this.fieldData = fieldData;
	}

	/**
	 * 创建HSSFWorkbook对象
	 * @return HSSFWorkbook
	 */
	public HSSFWorkbook createWorkbook() {

		workBook = new HSSFWorkbook();
		//定义数据大小
		int rows = fieldData.size();
		int sheetNum = 0;

		//计算excel分页
		if (rows % SPLIT_COUNT == 0) {
			sheetNum = rows / SPLIT_COUNT;
		} else {
			sheetNum = rows / SPLIT_COUNT + 1;
		}

		for (int i = 1; i <= sheetNum; i++) {
			HSSFSheet sheet = workBook.createSheet("Page " + i);
			HSSFRow headRow = sheet.createRow((short) 0); 
			for (int j = 0; j < fieldName.size(); j++) {
				HSSFCell cell = headRow.createCell((short) j);
				//添加样式
//				cell.setCellType(HSSFCell.CELL_TYPE_STRING);
//				cell.setEncoding(HSSFCell.ENCODING_UTF_16);
				//添加样式
				//设置每列的宽度
				sheet.setColumnWidth((short)j, (short)6000);
				//创建一个样式的对象
				HSSFCellStyle cellStyle = workBook.createCellStyle();
				
			    //创建一个font的对象，使用workBook的对象
				HSSFFont font = workBook.createFont();
				//设置字体为粗体
				font.setBold(true);
				//设置字体为红色
				short color = HSSFColor.RED.index;
				font.setColor(color);
				//向样式中添加字体的设置
				cellStyle.setFont(font);
				if(fieldName.get(j) != null){
					//样式加给列
					cell.setCellStyle(cellStyle);
					cell.setCellValue((String) fieldName.get(j));
				}else{
					//样式加给列
					cell.setCellStyle(cellStyle);
					cell.setCellValue("-");
				}
			}

			for (int k = 0; k < (rows < SPLIT_COUNT ? rows : SPLIT_COUNT); k++) {
				if (((i - 1) * SPLIT_COUNT + k) >= rows)
					break;
				HSSFRow row = sheet.createRow((short) (k + 1));
				//将数据内容放入excel单元格
				ArrayList rowList = (ArrayList) fieldData.get((i - 1) * SPLIT_COUNT + k);
				for (int n = 0; n < rowList.size(); n++) {
					HSSFCell cell = row.createCell((short) n);
//					cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					if(rowList.get(n) != null){
						cell.setCellValue((String) rowList.get(n).toString());
					}else{
						cell.setCellValue("");
					}
				}
			}
		}
		return workBook;
	}

	public void expordExcel(OutputStream os) throws Exception {
		workBook = createWorkbook();
		workBook.write(os);
		os.close();
	}

}
