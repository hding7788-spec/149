package ext.casc.doc;

import com.glaway.mpm.constants.XMLConstants;
import com.glaway.mpm.intf.ProcessEditorToWCIntfRMI;
import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.model.data.CmUser;
import com.glaway.mpm.util.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.constants.Constants;
import ext.casc.integrate.util.ZipUtil;
import org.apache.commons.io.IOUtils;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import wt.content.ApplicationData;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.inf.container.WTContainer;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.session.SessionServerHelper;
import wt.type.TypedUtilityServiceHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * @describe used to submit III sign
 * @author chenjianhui
 * @since 2022/1/20
 *
 */
public class SubmitSign3Processor extends DefaultObjectFormProcessor {
    private static VaLogger logger = VaLogger.getLogger(SubmitSign3Processor.class.getName());

    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        Object actionObj = commandBean.getActionOid().getRefObject();
        HashMap<String, EPMDocument> data = new HashMap<String, EPMDocument>();
        HashMap<String, WTDocument> data1 = new HashMap<String, WTDocument>();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        String wf = Constants.WF_START_ERROR;
        try {
            if (actionObj instanceof WTDocument) {//文档提交签审
                WTDocument doc = (WTDocument) actionObj;
                WTContainer container = doc.getContainer();
                String docType = TypedUtilityServiceHelper.service.getExternalTypeIdentifier(doc);
                if (docType.indexOf("casc.sast.149.DIANZHUANG_PROCESSPLAN") != -1) {//PFMEA
                    SubmitApprovalProcessor.initiateWfProcess("三级工艺文件签审流程", data1, container, doc);
                    wf = "三级工艺文件签审流程" + Constants.WF_START_MSG;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FeedbackMessage message = new FeedbackMessage();
            message.addMessage(wf);
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.NONE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
    }
}
