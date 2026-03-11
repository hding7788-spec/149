package ext.casc.util;

import com.glaway.mpm.util.WTPrincipalUtil;
import wt.org.WTUser;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.Enumeration;

public class WTUserUtil {
    public static String getGroupShortName(WTUser user) throws WTException {
        Enumeration groups = user.parentGroupNames();
        String returnName = "";
        while (groups.hasMoreElements()) {
            String groupName = (String) groups.nextElement();
            if (groupName.startsWith("部门")) {
                if (groupName != null) {
                    if (groupName.contains("一车间") || groupName.contains("一分厂")) {
                       return  "1";
                    } else if (groupName.contains("2热表")) {
                        return "2热表";
                    } else if (groupName.contains("非金属") ) {
                        return "2非金属";
                    } else if (groupName.contains("二车间") || groupName.contains("二分厂")) {
                        return "2";
                    } else if (groupName.contains("三车间") || groupName.contains("三分厂")) {
                        return "3";
                    } else if (groupName.contains("四车间") || groupName.contains("四分厂")) {
                        return "4";
                    } else if (groupName.contains("五车间") || groupName.contains("五分厂")) {
                        return "5";
                    } else if (groupName.contains("六车间") || groupName.contains("六分厂")) {
                        return "6";
                    } else if (groupName.contains("七车间") || groupName.contains("七分厂")) {
                        return "7";
                    } else if (groupName.contains("八车间") || groupName.contains("八分厂")) {
                        return "8";
                    } else if (groupName.contains("九车间") || groupName.contains("九分厂")) {
                        return "9";
                    } else if (groupName.contains("十车间") || groupName.contains("十分厂")) {
                        return "10";
                    } else {
                        if(groupName.contains("_")) {
                            returnName = groupName.substring(groupName.lastIndexOf("_") + 1);
                        }
                    }
                }
            }
        }
        return returnName;
    }

    public static String getDeptShortName(WTUser user) throws WTException {
        ArrayList<String> list = WTPrincipalUtil.getUserInGorupName(user);
        for(String groupName : list) {
            if(groupName.startsWith("部门")) {
                if(groupName.contains("一车间") || groupName.contains("一分厂")) {
                    return "1";
                } else if(groupName.contains("二车间") || groupName.contains("二分厂")) {
                    return "2";
                } else if(groupName.contains("三车间") || groupName.contains("三分厂")) {
                    return "3";
                } else if(groupName.contains("四车间") || groupName.contains("四分厂")) {
                    return "4";
                } else if(groupName.contains("五车间") || groupName.contains("五分厂")) {
                    return "5";
                } else if(groupName.contains("六车间") || groupName.contains("六分厂")) {
                    return "6";
                } else if(groupName.contains("七车间") || groupName.contains("七分厂")) {
                    return "7";
                } else if(groupName.contains("八车间") || groupName.contains("八分厂")) {
                    return "8";
                } else if(groupName.contains("九车间") || groupName.contains("九分厂")) {
                    return "9";
                } else if(groupName.contains("十车间") || groupName.contains("十分厂")) {
                    return "10";
                } else if(groupName.contains("事业一部")) {
                    return "事业一部";
                } else if(groupName.contains("事业二部")) {
                    return "事业二部";
                } else if(groupName.contains("事业三部")) {
                    return "事业三部";
                } else if(groupName.contains("事业四部")) {
                    return "事业四部";
                } else if(groupName.contains("事业五部")) {
                    return "事业五部";
                } else if(groupName.contains("协配中心")) {
                    return "协配中心";
                } else if(groupName.contains("试验中心")) {
                    return "试验中心";
                }
            }
        }
        return "";
    }
}
