package ext.casc.util;

import wt.fc.PersistenceHelper;
import wt.util.WTException;

import java.text.DecimalFormat;

public class GenSequeneUtil {
    public static String genSeqNumber(String seqName, DecimalFormat format) throws WTException{

        String seqNo = "";
        try{
            seqNo = PersistenceHelper.manager.getNextSequence(seqName);
        }catch (WTException e){
            e.printStackTrace();
        }
        return format.format(Integer.parseInt(seqNo));
    }
}
