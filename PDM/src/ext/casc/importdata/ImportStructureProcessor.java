package ext.casc.importdata;

import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

import wt.httpgw.URLFactory;
import wt.util.WTException;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class ImportStructureProcessor implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static String MYRESOURCE = "ext.casc.importdata.productRB";

    public static FormResult importObjects(NmCommandBean cb) throws WTException, RemoteException {
        // System.out.println("In-----<<<<>>>");
        String file = cb.getTextParameter("file");
        String xlsFile2 = cb.getTextParameter("file2");
    //    System.out.println("--------file:"+file);
        System.out.println("--------xlsFile2:"+xlsFile2);
        
        File temp_file = (File) cb.getRequest().getAttribute("file");
        File temp_xlsFile = (File) cb.getRequest().getAttribute("file2");
  //      System.out.println("--------temp_file:"+temp_file.getName());
        System.out.println("--------temp_xlsFile:"+temp_xlsFile.getName());
        
        //��ѹ���ϴ����ļ�����ʱ�����ڷ������ˣ��Թ�����ʹ��
     //   List<String> allFileName = ImportDataHelper.service.compressZIPData(temp_xlsFile, xlsFile2);
        
        //��ȡEXCEL�����ݣ�ִ�д����ĵ�����
        
        
        String temp_path = (temp_file != null) ? temp_file.getAbsolutePath() : null;
        ResourceBundle rb = ResourceBundle.getBundle(MYRESOURCE);
        FormResult form = new FormResult();
        try {
            String returnValue = ImportDataHelper.service.importObjects(cb, temp_xlsFile,xlsFile2);
            if (returnValue != null && !returnValue.equals("")) {
                 String info = "";
            	if (returnValue.charAt(0)=='1'){
                 info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>"
                        + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES2)
                        + "</b></font></tr><br>"
                        + returnValue
                        + "<tr><td><input type=button name='S1' value='"
                        + rb.getString(productRB.IMPORTDATA_CLOSE)
                        + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";

            	}else{
               info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>"
                        + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES)
                        + "</b></font></tr><br>"
                        + returnValue
                        + "<tr><td><input type=button name='S1' value='"
                        + rb.getString(productRB.IMPORTDATA_CLOSE)
                        + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";
            	}
                form.setStatus(FormProcessingStatus.FAILURE);
                // form.addFeedbackMessage(message);
                URLFactory urlfactory = new URLFactory();
                cb.getRequest().getSession().putValue("errorInfo", info);
                String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/importdata/importDataInfo.jsp?";
                form.setURL(url);
                form.setNextAction(FormResultAction.FORWARD);
                return form;
            } else {
                // form.setNextAction(FormResultAction.REFRESH_OPENER);
                form.setStatus(FormProcessingStatus.SUCCESS);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "产品结构数据导入成功");
                form.addFeedbackMessage(message);
                form.setNextAction(FormResultAction.REFRESH_OPENER);
                return form;
            }
        } finally {
            if (temp_file != null)
                temp_file.delete();
        }
    }

}
