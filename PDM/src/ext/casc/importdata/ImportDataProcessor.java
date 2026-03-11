package ext.casc.importdata;


import java.io.File;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import wt.util.WTException;

import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

public class ImportDataProcessor extends DefaultObjectFormProcessor implements Serializable  {
    
    private static final long serialVersionUID = 1L;

    public  FormResult doOperation(NmCommandBean cb,List list) throws WTException {
  
        String file = cb.getTextParameter("uploadFileField");
        
        System.out.println("--------File:"+file);
        
        
        
        File temp_file = (File) cb.getRequest().getAttribute("uploadFileField");
        System.out.println("--------temp_file:"+temp_file.getName());
        
        //加压缩上传的文件并临时保存在服务器端，以供后续使用
       
		List<String> allFileName = ImportDataHelper.service.compressZIPData(temp_file, file);

        
        //读取EXCEL表格数据，执行创建文档操作
        
        
        String temp_path = (temp_file != null) ? temp_file.getAbsolutePath() : null;
        FormResult form = new FormResult();
        String returnValue;
			try {
				returnValue = ImportDataHelper.service.importObjects(cb,temp_file,file);
			
            if (returnValue != null && !returnValue.equals("")) {
       

            	form.setStatus(FormProcessingStatus.FAILURE);
                return form;
            } else {
                
                form.setStatus(FormProcessingStatus.SUCCESS);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "导入成功");
                form.addFeedbackMessage(message);
                form.setNextAction(FormResultAction.REFRESH_OPENER);
                return form;
            }
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

       return form;
       


    }
    
    
    	

}
