package ext.casc.workflow.tree;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import wt.util.WTException;

import com.ptc.core.components.descriptor.ModelContext;
import com.ptc.core.components.factory.AbstractDataUtility;
import com.ptc.core.components.rendering.guicomponents.StringInputComponent;
import com.ptc.netmarkets.model.NmOid;

public class TableColumnDataUtility extends AbstractDataUtility{

	@SuppressWarnings({ "rawtypes", "deprecation" })
	public Object getDataValue(String paramString, Object paramObject, ModelContext paramModelContext) throws WTException{
		NmOid oid = (NmOid)paramObject;
		StringInputComponent strInput = new StringInputComponent();
		strInput.setEditable(true);
		strInput.setName(paramString + "_" + oid.toString());
		HashMap map = oid.getAdditionalInfo();
		if(map != null){
			strInput.setValue((String)map.get(paramString));
		}
		return strInput;
	}

    /**
     * 字符串（请正确输入格式）转换成时间(java.util.Date)
     * @author ace
     * @param date
     * @param format
     * @return
     */
    public static Date stringToDate(String str,String format) {
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
    	Date date;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			date = new Date();
		}
        return date;
    }

}
