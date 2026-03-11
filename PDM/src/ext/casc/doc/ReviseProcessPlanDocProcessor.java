package ext.casc.doc;

import com.glaway.mpm.processplan.helper.ZhuFuLinkUtil;
import com.glaway.mpm.util.SWXMLUtil;
import com.glaway.mpm.util.WTDocumentUtil;
import com.glaway.mpm.util.WTPartUtil;
import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.commands.PartDocServiceCommand;
import com.ptc.wvs.server.util.PublishUtils;
import ext.casc.util.IBAHelper;
import wt.content.ContentHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.representation.Representation;
import wt.representation.RepresentationHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.type.TypedUtility;
import wt.util.WTException;
import wt.vc.*;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import java.beans.PropertyVetoException;
import java.util.List;

/**
 * @describe used to submit approval workFlow process
 * @author Long,XiuChuan
 * @since 2012/7/23
 *
 */
public class ReviseProcessPlanDocProcessor extends DefaultObjectFormProcessor {
    public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
        FormResult formresult = super.doOperation(commandBean, objectBeans);
        Object actionObj = commandBean.getActionOid().getRefObject();
        boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
        System.out.println("actionObj" + actionObj);
        Transaction tx = new Transaction();
        Versioned newDoc = null;
        try {
            if (actionObj instanceof WTDocument) {//文档提交签审
                WTDocument doc = (WTDocument) actionObj;

            	QueryResult docqr =  VersionControlHelper.service.allIterationsOf(doc.getMaster());
            	if(docqr.hasMoreElements()){
            		doc =(WTDocument) docqr.nextElement();
            	}

                String typeName = TypedUtility.getTypeIdentifier(doc).getTypename();
                if(typeName.contains("PROCESS_PLAN")){
                	QueryResult qr = PartDocServiceCommand.getAssociatedDescParts(doc);
            		if(qr.hasMoreElements()) {
        				WTPart part = (WTPart)qr.nextElement();
        				WTUser creator = (WTUser)doc.getModifier().getObject();
        				SessionHelper.manager.setPrincipal(creator.getAuthenticationName());

        				// 取最新大版本中的最新一个非一次性版本作为版本编号基础
        				QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc);
        				Versioned vMax = (Versioned) qr2.nextElement();

        				// 取统一大版本中的最新一个非一次性版本作为新版内容基础
        				Versioned vBase = null;
        				qr2 = VersionControlHelper.service.allVersionsFrom(doc);
        				while (qr2.hasMoreElements()) {
        					vBase = (Versioned) qr2.nextElement();
        					if (!(vBase instanceof OneOffVersioned) || !VersionControlHelper.isAOneOff((OneOffVersioned) vBase))
        						break;
        				}
        				if (vBase == null) // 应该不可能的错误
        					throw new Exception("Unknown Error, no normal version found.");

        				// 检查同一大版本中最新一个非一次性版本是否被检出
        				if (vBase instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) vBase))
        					throw new Exception("选中版本的最新非先行更改版本正被检出，不能进行修订!");

        				// 取其下一版本号和初始小版本号创建新版本
        				tx.start();
        				// 新建并保存修订版本
        				VersionIdentifier vi = VersionControlHelper.nextVersionId(vMax);
        				IterationIdentifier ii = VersionControlHelper.firstIterationId(vMax);
        				newDoc = VersionControlHelper.service.newVersion(vMax, vi, ii);
        				newDoc = (WTDocument) PersistenceHelper.manager.store(newDoc);


        				// 替换该版本的主体文件
        				WTDocumentUtil.setPrimaryForDocument((WTDocument) newDoc, (WTDocument) vBase);

        				part = WTPartUtil.getLatestPartByNumberAndView(part, "Manufacturing");
        				WTPartUtil.createWTPartDescribeLink(part, (WTDocument) newDoc);

        				//修改工艺文件信息
        				SWXMLUtil.updateTechnicsInfo((WTDocument)newDoc,creator);
        				tx.commit();
        				tx = null;

						String beforeVersion = doc.getVersionIdentifier().getValue();
						String versionInfo = newDoc.getVersionIdentifier().getValue();
						String zfflag = IBAHelper.getIBAStringValue(doc, "ZFFLAG");
						if ("F".equals(zfflag)) {
							ZhuFuLinkUtil.copyZFLinkF(versionInfo, beforeVersion, doc);
						} else if ("Z".equals(zfflag)) {
							ZhuFuLinkUtil.copyZFLinkZ(versionInfo, beforeVersion, doc);
						}
        				//删除可视化
        				deleteRepresentation((WTDocument)newDoc);
        			}

                }else{
                	WTUser creator = (WTUser)doc.getModifier().getObject();
    				SessionHelper.manager.setPrincipal(creator.getAuthenticationName());
    				// 取最新大版本中的最新一个非一次性版本作为版本编号基础
    				QueryResult qr2 = VersionControlHelper.service.allVersionsOf(doc);
    				Versioned vMax = (Versioned) qr2.nextElement();
    				// 取统一大版本中的最新一个非一次性版本作为新版内容基础
    				Versioned vBase = null;
    				qr2 = VersionControlHelper.service.allVersionsFrom(doc);
    				while (qr2.hasMoreElements()) {
    					vBase = (Versioned) qr2.nextElement();
    					if (!(vBase instanceof OneOffVersioned) || !VersionControlHelper.isAOneOff((OneOffVersioned) vBase))
    						break;
    				}
    				if (vBase == null) // 应该不可能的错误
    					throw new Exception("Unknown Error, no normal version found.");

    				// 检查同一大版本中最新一个非一次性版本是否被检出
    				if (vBase instanceof Workable && WorkInProgressHelper.isCheckedOut((Workable) vBase))
    					throw new Exception("选中版本的最新非先行更改版本正被检出，不能进行修订!");

    				// 取其下一版本号和初始小版本号创建新版本
    				tx.start();
    				// 新建并保存修订版本
    				VersionIdentifier vi = VersionControlHelper.nextVersionId(vMax);
    				IterationIdentifier ii = VersionControlHelper.firstIterationId(vMax);
    				newDoc = VersionControlHelper.service.newVersion(vMax, vi, ii);
    				newDoc = (WTDocument) PersistenceHelper.manager.store(newDoc);
    				tx.commit();
    				tx = null;

    				//删除修订后对象的可视化文件
    				deleteRepresentation((WTDocument)newDoc);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if (tx != null)
				tx.rollback();

            FeedbackMessage message = new FeedbackMessage();
            message.addMessage("操作成功");
            formresult.addFeedbackMessage(message);
            formresult.setNextAction(FormResultAction.NONE);
            SessionServerHelper.manager.setAccessEnforced(flag);
        }
        return formresult;
    }
    /**
     * 删除文档可视化 add by zhuhao 20170509
     * @param doc
     * @throws WTException
     */
    public void deleteRepresentation(WTDocument doc)
       		throws WTException {
    		QueryResult qr = PublishUtils.getRepresentations(doc);
//    		QueryResult qr =  RepresentationHelper.service.getRepresentations(doc);
    		while(qr.hasMoreElements()){
    			Representation representation = (Representation) qr.nextElement();
    			if (representation != null) {
    				try{
    					representation = (Representation) ContentHelper.service.getContents(representation);
    					RepresentationHelper.service.deleteRepresentation(representation);
    				}catch(PropertyVetoException pve){
    					pve.printStackTrace();
    				}
    			}
    		}

    }
}
