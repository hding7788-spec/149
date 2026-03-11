package com.glaway.mpm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import wt.util.WTProperties;

public class ReadAttributesExcelHelper {

    private static final String VALUE_Y = "Y";

    private static String wt_codebase;

    static{
    	try {
			WTProperties pro = WTProperties.getLocalProperties();
			wt_codebase = pro.getProperty("wt.codebase.location");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    /**
     *
     * 解析EXCEL配置文件，读取每种类型零部件的属性
     *
     * @return map
     */
    public static Map<String, List<Map<String,String>>> readXls() {
        // 用来存放每种类型零部件属性键值对
        Map<String, List<Map<String,String>>> map = new HashMap<String, List<Map<String,String>>>();
        try {
        	String path = wt_codebase + File.separator + "com" + File.separator
        			+ "glaway" + File.separator + "mpm" + File.separator
        			+ "config" + File.separator + "attributes.xls";

            InputStream is = new FileInputStream(path);
            POIFSFileSystem poi = new POIFSFileSystem(is);
            HSSFWorkbook wb = new HSSFWorkbook(poi);
            HSSFSheet sheet = wb.getSheetAt(0);
            HSSFRow row = sheet.getRow(0);

            // 用来记录类型对应的列标记
            Map<Integer, String> flagMap = new HashMap<Integer, String>();

            //记录属性集合
            List<Map<String,String>> list = null;
            //记录属性的内部名称和显示名称
            Map<String,String> attrMap = null;

            // 标题总列数
            int colNum = row.getPhysicalNumberOfCells();
            String type = "";
            for (int i = 0; i < colNum; i++) {
                type = getCellFormatValue(row.getCell(i));
                if (type != null && !"".equals(type)) {
                    list = new ArrayList<Map<String,String>>();
                    map.put(type, list);
                    flagMap.put(i, type);
                }
            }

            // 正文内容应该从第二行开始,第一行为表头的标题
            String value = "";
            String name = "";
            String displayName = "";
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                // 从第三列开始读取,因为第一、二列为属性名称
                for (int j = 2; j < colNum; j++) {
                    value = getCellFormatValue(row.getCell(j)).trim();
                    // 如果值为Y，则表示该列类型的零部件有此属性
                    if (VALUE_Y.equalsIgnoreCase(value)) {
                        name = getCellFormatValue(row.getCell(1)).trim();
                        displayName = getCellFormatValue(row.getCell(0)).trim();
                        attrMap = new HashMap<String,String>();
                        attrMap.put(name, displayName);
                        map.get(flagMap.get(j)).add(attrMap);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 根据HSSFCell类型设置数据
     *
     * @param cell
     * @return
     */
    private static String getCellFormatValue(HSSFCell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
            // 如果当前Cell的Type为NUMERIC
            case HSSFCell.CELL_TYPE_NUMERIC:
                cellvalue = String.valueOf(cell.getNumericCellValue());
            case HSSFCell.CELL_TYPE_FORMULA: {
                // 判断当前的cell是否为Date
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // 方法2：这样子的data格式是不带带时分秒的：2011-10-12
                    Date date = cell.getDateCellValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    cellvalue = sdf.format(date);
                }
                // 如果是纯数字
                else {
                    // 取得当前Cell的数值
                    cellvalue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            }
            // 如果当前Cell的Type为STRIN
            case HSSFCell.CELL_TYPE_STRING:
                // 取得当前的Cell字符串
                cellvalue = cell.getRichStringCellValue().getString();
                break;
            // 默认的Cell值
            default:
                cellvalue = " ";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;

    }

    public static void main(String[] args) {
        Map<String, List<Map<String,String>>> map = ReadAttributesExcelHelper.readXls();
        System.out.println(map);
    }

}
