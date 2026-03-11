package ext.casc.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class ExcelUtil {
    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                return false;
            }
        }
        return true;
    }

    public static Object getValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        Object obj = null;
        switch (cell.getCellType()) {
            case 4:
                obj = cell.getBooleanCellValue();
                break;
            case 5:
                obj = cell.getErrorCellValue();
                break;
            case 0:
                cell.setCellType(1);
                obj = cell.getStringCellValue();
                break;
            case 1:
                obj = cell.getStringCellValue();
                break;
            case 2:
                obj = cell.getCellFormula();
                break;
            default:
                break;
        }
        if (obj == null) {
            obj = "";
        }
        return obj;
    }
}
