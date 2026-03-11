package ext.casc.importdata.process;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.access.AccessAdminUtil;
import ext.casc.importdata.productRB;
import ext.casc.nc.bean.GLNCPartMapping;
import ext.casc.util.DBUtil;
import ext.casc.util.WTUtil;
import ext.sast.common.fc.CmPersistenceHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import wt.httpgw.URLFactory;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.session.SessionHelper;
import wt.util.WTException;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.UUID;

public class ImportNCMapProcessor implements Serializable {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ImportNCMapProcessor.class);

    private static final long serialVersionUID = 1L;
    private static String MYRESOURCE = "ext.casc.importdata.productRB";

    /**
     * 导入工艺参数
     *
     * @param cb
     * @return
     * @throws WTException
     */
    public static FormResult importNCMap(NmCommandBean cb) throws WTException {
        File temp_file = (File) cb.getRequest().getAttribute("file");
        File temp_xlsFile = (File) cb.getRequest().getAttribute("file2");
        ResourceBundle rb = ResourceBundle.getBundle(MYRESOURCE);
        FormResult form = new FormResult();
        try {
			WTUser curentuser = (WTUser) SessionHelper.getPrincipal();

			boolean hasAccess = false;
        	WTGroup group = AccessAdminUtil.getGroupByName("部门_物资部");
			if(group!=null){
				if(group.isMember(curentuser)){
					hasAccess = true;
				}
			}else{
				 group = AccessAdminUtil.getGroupByName("物资部");
				 if(group!=null){
						if(group.isMember(curentuser)){
							hasAccess = true;
						}
				}
			}
			String returnValue = "";
			if(hasAccess){
				returnValue = importData(temp_xlsFile);
			}else{
				returnValue ="无导入权限!";
			}
            if (returnValue != null && !returnValue.equals("")) {
                String info = "";
                if (returnValue.charAt(0) == '1') {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES2) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";

                } else {
                    info = "<TABLE width='100%' border=0 cellpadding=0 cellspacing=0><tr><font color='#FF0000'><b>" + rb.getString(productRB.IMPORTERRORMESSAGE_TITLES) + "</b></font></tr><br>" + returnValue + "<tr><td><input type=button name='S1' value='" + rb.getString(productRB.IMPORTDATA_CLOSE) + "' onclick=\"javascript:window.close();\"></td></tr></TABLE><script>document.body.style.bgColor='#F5F6F0';</script>";
                }
                form.setStatus(FormProcessingStatus.FAILURE);
                URLFactory urlfactory = new URLFactory();
                cb.getRequest().getSession().putValue("errorInfo", info);
                String url = urlfactory.getBaseHREF() + "netmarkets/jsp/ext/casc/importdata/importDataInfo.jsp?";
                form.setURL(url);
                form.setNextAction(FormResultAction.FORWARD);
                return form;
            } else {
                form.setStatus(FormProcessingStatus.SUCCESS);
                FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "数据导入成功");
                form.addFeedbackMessage(message);
                form.setNextAction(FormResultAction.REFRESH_OPENER);
                return form;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return form;
        } finally {
            if (temp_file != null) temp_file.delete();
        }
    }

    private static String importData(File temp_xlsFile) {
        Transaction tx = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(temp_xlsFile);
            Workbook workbook = WorkbookFactory.create(fileInputStream);
            Sheet sheet = workbook.getSheetAt(0);
            tx = new Transaction();
            tx.start();
            String synchTime = String.valueOf(System.currentTimeMillis());
            //StringBuilder insql = new StringBuilder("");
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue; // Skip the header row
                }
                GLNCPartMapping glncPartMapping = new GLNCPartMapping();
                glncPartMapping.setKeyId(UUID.randomUUID().toString());
                String oldPartNumber = WTUtil.getCellStringValue(row.getCell(0));
               //insql.append(oldPartNumber);
                //insql.append(",");

                glncPartMapping.setOldPartNumber(oldPartNumber);
                glncPartMapping.setNewPartNumber(WTUtil.getCellStringValue(row.getCell(1)));
                glncPartMapping.setState("启用");
                glncPartMapping.setSynchtime(synchTime);

                DBUtil.deleteByTableAndKey("GLNCPartMapping","OLDPARTNUMBER",oldPartNumber);

                CmPersistenceHelper.manager.save(glncPartMapping);

            }
           /* if(insql.length()>0){
                String oldNumbers  = insql.deleteCharAt(insql.length()-1).toString();
                DBUtil.batchDeleteByTableInKey("GLNCPartMapping","OLDPARTNUMBER",oldNumbers);
            }
*/
            tx.commit();
            tx = null;



            return "";
        }catch(Exception e){
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }


}
