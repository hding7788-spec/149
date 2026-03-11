package ext.casc.integrate.chanbao;

import com.glaway.mpm.util.WTDocumentUtil;
import ext.casc.util.IBAHelper;
import ext.sast.center.processor.InvokeRestServiceProcessor;
import wt.change2.ChangeHelper2;
import wt.change2.WTChangeOrder2;
import wt.doc.WTDocument;
import wt.fc.QueryResult;
import wt.fc.WTObject;
import wt.part.WTPart;
import wt.util.WTException;

import java.net.URLEncoder;

public class ChanBaoSynchUtil {
    String url = "http://10.125.192.56/plm/api/v2/qdp/public/pdm/workflow-document";
    public String sendControlProcessMsg(WTObject pbo){
        String result = "";
        if(pbo instanceof WTDocument){
            WTDocument doc = (WTDocument) pbo;
            try {
                WTPart wtpart =  WTDocumentUtil.getLatestDescribesWTPartsByDocument(doc);
                if(wtpart!=null){
                   // String cbKongZhiDian = IBAHelper.getIBAStringValue(wtpart,"cbKongZhiDian");
                    //if("是".equals(cbKongZhiDian)) {
                        result = InvokeRestServiceProcessor.invokeRestService(url + "?number=" + URLEncoder.encode(wtpart.getNumber(), "UTF-8"));
                   // }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }else  if(pbo instanceof WTChangeOrder2){
            WTChangeOrder2 ecn = (WTChangeOrder2) pbo;
            QueryResult qResult = null;
            try {
                qResult = ChangeHelper2.service.getChangeablesAfter(ecn);
                while(qResult.hasMoreElements()){
                    WTObject object = (WTObject) qResult.nextElement();
                    if(object instanceof  WTDocument){
                        WTDocument doc = (WTDocument) object;
                        WTPart wtpart =  WTDocumentUtil.getLatestDescribesWTPartsByDocument(doc);
                        if(wtpart!=null){
                            //String cbKongZhiDian = IBAHelper.getIBAStringValue(wtpart,"cbKongZhiDian");
                            //if("是".equals(cbKongZhiDian)) {
                                result = InvokeRestServiceProcessor.invokeRestService(url + "?number=" + URLEncoder.encode(wtpart.getNumber(), "UTF-8"));
                            //}
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
