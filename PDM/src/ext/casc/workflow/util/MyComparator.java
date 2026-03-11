package ext.casc.workflow.util;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import wt.log4j.LogR;

import com.ptc.windchill.mpml.processplan.operation.MPMOperationUsageLink;

/**
 * Sort by MPMOperation Label
 * 
 * @author Lonely
 */
public class MyComparator implements Comparator<Object> {
    private static Logger logger = LogR.getLogger(MyComparator.class.getName());

    @Override
    public int compare(Object obj1, Object obj2) {
        if ((obj1 instanceof MPMOperationUsageLink) && (obj2 instanceof MPMOperationUsageLink)) {
            MPMOperationUsageLink opr1 = (MPMOperationUsageLink) obj1;
            MPMOperationUsageLink opr2 = (MPMOperationUsageLink) obj2;
            String label1 = opr1.getOperationLabel();
            String label2 = opr2.getOperationLabel();
            return label1.compareToIgnoreCase(label2);
        } else if ((obj1 instanceof String) && (obj2 instanceof String)) {
            String label1 = String.valueOf(obj1);
            String label2 = String.valueOf(obj2);
            return label1.compareToIgnoreCase(label2);
        }
        return 0;
    }

    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}
