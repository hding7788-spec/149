package ext.casc.doc;

import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.util.IBAHelper;
import ext.casc.util.Tools;
import wt.doc.WTDocument;
import wt.util.WTException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @describe used to submit approval workFlow process
 * @author Long,XiuChuan
 * @since 2012/7/23
 *
 */
public class OpenBaiYuToolProcessor extends DefaultObjectFormProcessor {
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        Object actionObj = commandBean.getActionOid().getRefObject();
        String url = "";
        if (actionObj instanceof WTDocument) {//文档提交签审
            WTDocument doc = (WTDocument) actionObj;
            Map<String,String> fileUrl =  ProcessEditorToWCIntfRMI.getFileURLByDocNumber(doc.getNumber());
            if(fileUrl!=null){
                String qbyURL = fileUrl.get("qby");
                String mjsonURL = fileUrl.get("mjson");
                String ojsonURL = fileUrl.get("ojson");
                String stepNumber = IBAHelper.getIBAStringValue(doc,"stepNum");
                String paceNumber = IBAHelper.getIBAStringValue(doc,"paceNum");
                String technicsNumber = IBAHelper.getIBAStringValue(doc,"PPNUMBER");
                String tableType = IBAHelper.getIBAStringValue(doc,"tableType");
                try {
                    if(!Tools.isNull(tableType)){
                        tableType =  URLEncoder.encode(tableType,"UTF-8");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                url = "baiyu://schema?qbyFileAbsolutePath="+qbyURL+"&xlsxFileAbsolutePath="+mjsonURL+"&jsonFileAbsolutePath="+ojsonURL+"&technicsNumber="+technicsNumber+"&stepNum="+stepNumber+"&paceNum="+paceNumber+"&tableId="+doc.getNumber()+"&tableType="+tableType;
            }

        }
        System.out.println("baiyu url="+url);
        FeedbackMessage message = new FeedbackMessage();
        message.addMessage("操作成功");
        formresult.addFeedbackMessage(message);
        formresult.setJavascript("window.open(\"" + url + "\",\"_blank\")");
        formresult.setNextAction(FormResultAction.JAVASCRIPT);

        return formresult;
    }

}
