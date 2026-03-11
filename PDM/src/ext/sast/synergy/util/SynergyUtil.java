package ext.sast.synergy.util;

import ext.sast.center.record.GWMQRecordHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.pdmlink.PDMLinkProduct;
import wt.pdmlink._PDMLinkProduct;
import wt.pds.StatementSpec;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SynergyUtil {

    /**
     * 获取全部产品名称列表
     *
     * @return
     * @throws WTException
     */
    public static List<String> getAllContainer() throws WTException {
        List<String> list = new ArrayList<String>();
        QueryResult qResult = getAllProduct();
        while (qResult.hasMoreElements()) {
            PDMLinkProduct product = (PDMLinkProduct) qResult.nextElement();
            list.add(product.getName());
        }
        return list;
    }

    /**
     * 获取全部产品结果集
     *
     * @return
     * @throws WTException
     */
    public static QueryResult getAllProduct() throws WTException {
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        QuerySpec qSpec = new QuerySpec(PDMLinkProduct.class);
        ClassAttribute ca = new ClassAttribute(PDMLinkProduct.class, _PDMLinkProduct.NAME);
        OrderBy orderby = new OrderBy(ca, false);
        qSpec.appendOrderBy(orderby, new int[]{0});
        QueryResult qResult = PersistenceHelper.manager.find((StatementSpec) qSpec);
        SessionServerHelper.manager.setAccessEnforced(flag);
        return qResult;
    }

    /**
     * 字符串转MAP
     *
     * @param str
     * @return
     */
    public static Map<String, String> getStringToMap(String str) {
        String[] str1 = str.split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < str1.length; i++) {
            String[] str2 = str1[i].split("=");
            if (str2.length > 1)
                map.put(str2[0], str2[1]);
        }
        return map;
    }

    /**
     * 日期转时间戳
     *
     * @param time
     * @return
     */
    public static String covertDate(String time) {
        if (time.equals(""))
            return "";

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(date.getTime() + 24 * 60 * 60 * 1000);
    }

    public static String toOrderNumberDetail(HttpServletRequest request, HttpServletResponse response) throws WTException, IOException {
        String number = request.getParameter("number");
        String type = request.getParameter("type");
        String oid = GWMQRecordHelper.getObjectOidByNumberAndType(number,type);
        String contextPath = request.getContextPath();
        String url = contextPath +"/app/#ptc1/tcomp/infoPage?oid="+oid;
        if(oid != null && !oid.equals("")){
            response.sendRedirect(url);
        }else{
            url = null;
        }
        return url;
    }

}
