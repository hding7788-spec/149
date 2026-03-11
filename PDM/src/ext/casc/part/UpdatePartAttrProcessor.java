package ext.casc.part;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import wt.enterprise.Master;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pom.PersistenceException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Iterated;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.model.NmOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;

public class UpdatePartAttrProcessor {
    public static FormResult execute(NmCommandBean cb) {
        FormResult form = new FormResult();
        List<WTPart> list = new ArrayList<WTPart>();
        try {
            NmOid nmOid = cb.getActionOid();
            Object object = nmOid.getRefObject();
            if (object instanceof WTPart) {
                WTPart parentPart = (WTPart) object;
                list.add(parentPart);
                // 获取所有子件(Manufacturing)
                QueryResult qr = PersistenceHelper.manager.navigate(parentPart, WTPartUsageLink.USES_ROLE,
                        WTPartUsageLink.class, false);
                while (qr.hasMoreElements()) {
                    WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();
                    WTPartMaster master = link.getUses();
                    WTPart child = (WTPart) getIteratedByMaster(master);
                    child = WCUtil.getLatestPartByView((Master) child.getMaster(), "Manufacturing");
                    if (child == null) {
                        continue;
                    }
                    list.add(child);
                }

                for (WTPart part : list) {
                    // 获取对应的Design视图的对象
                    WTPart tempPart = WCUtil.getLatestPartByView((Master) part.getMaster(), "Design");
                    // 同步属性
                    updateAttr(tempPart, part);
                }
            }

            form.setStatus(FormProcessingStatus.SUCCESS);
            FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null, null, null, "属性成功更新完毕！");
            form.addFeedbackMessage(message);
            form.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
        } catch (Exception e) {
            form.setStatus(FormProcessingStatus.FAILURE);
            FeedbackMessage message;
            try {
                message = new FeedbackMessage(FeedbackType.FAILURE, null, null, null, "属性更新失败，请联系管理员！");
                form.addFeedbackMessage(message);
            } catch (WTException e1) {
                e1.printStackTrace();
            }
            form.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);
            e.printStackTrace();
        }
        return form;
    }

    private static void updateAttr(WTPart part, WTPart part2) throws WTException, WTPropertyVetoException,
            RemoteException, ClassNotFoundException {
        if (part != null) {
            IBAUtility ibaUtility1 = new IBAUtility(part);// Design
            IBAUtility ibaUtility2 = new IBAUtility(part2);// Manufacturing
            // 备注
            String remark = ibaUtility1.getIBAValue("REMARK");
            if (remark != null) {
                ibaUtility2.setIBAValue("REMARK", remark);
            }
            // 材料
            String cmat = ibaUtility1.getIBAValue("CMAT");
            if (cmat != null) {
                ibaUtility2.setIBAValue("CMAT", cmat);
            }
            // 材料名称
            String ptc_material_name = ibaUtility1.getIBAValue("PTC_MATERIAL_NAME");
            if (ptc_material_name != null) {
                ibaUtility2.setIBAValue("PTC_MATERIAL_NAME", ptc_material_name);
            }
            // 材料上标
            String cmat_up = ibaUtility1.getIBAValue("CMAT_UP");
            if (cmat_up != null) {
                ibaUtility2.setIBAValue("CMAT_UP", cmat_up);
            }
            // 材料下标
            String cmat_down = ibaUtility1.getIBAValue("CMAT_DOWN");
            if (cmat_down != null) {
                ibaUtility2.setIBAValue("CMAT_DOWN", cmat_down);
            }
            // 成套件标识
            String setmark = ibaUtility1.getIBAValue("SETMARK");
            if (setmark != null) {
                ibaUtility2.setIBAValue("SETMARK", setmark);
            }
            // 当前阶段
            String phase_code = ibaUtility1.getIBAValue("PHASE_CODE");
            if (phase_code != null) {
                ibaUtility2.setIBAValue("PHASE_CODE", phase_code);
            }
            // 关重件标识
            String keycomponent = ibaUtility1.getIBAValue("KEYCOMPONENT");
            if (keycomponent != null) {
                ibaUtility2.setIBAValue("KEYCOMPONENT", keycomponent);
            }
            // 规格
            String csize = ibaUtility1.getIBAValue("CSIZE");
            if (csize != null) {
                ibaUtility2.setIBAValue("CSIZE", csize);
            }
            // 密级
            String secret = ibaUtility1.getIBAValue("SECRET");
            if (secret != null) {
                ibaUtility2.setIBAValue("SECRET", secret);
            }
            // 所属成品
            String enditemin = ibaUtility1.getIBAValue("ENDITEMIN");
            if (enditemin != null) {
                ibaUtility2.setIBAValue("ENDITEMIN", enditemin);
            }
            // 所属型号
            String mindex = ibaUtility1.getIBAValue("MINDEX");
            if (mindex != null) {
                ibaUtility2.setIBAValue("MINDEX", mindex);
            }
            // 图号
            String cindex = ibaUtility1.getIBAValue("CINDEX");
            if (cindex != null) {
                ibaUtility2.setIBAValue("CINDEX", cindex);
            }
            // 中文名称
            String ptc_common_name = ibaUtility1.getIBAValue("PTC_COMMON_NAME");
            if (ptc_common_name != null) {
                ibaUtility2.setIBAValue("PTC_COMMON_NAME", ptc_common_name);
            }
            // 设计单位
            String company = ibaUtility1.getIBAValue("COMPANY");
            if (company != null) {
                ibaUtility2.setIBAValue("COMPANY", company);
            }
            // 工装代号
            String product_index = ibaUtility1.getIBAValue("PRODUCT_INDEX");
            if (product_index != null) {
                ibaUtility2.setIBAValue("PRODUCT_INDEX", product_index);
            }
            // 设计者
            String designer = ibaUtility1.getIBAValue("DESIGNER");
            if (designer != null) {
                ibaUtility2.setIBAValue("DESIGNER", designer);
            }
            part2 = (WTPart) ibaUtility2.updateAttributeContainer(part2);
            ibaUtility2.updateIBAHolder(part2);
        }
    }

    public static Iterated getIteratedByMaster(Mastered mst) {
        Iterated itr = null;
        if (mst != null) {
            QueryResult qr;
            try {
                qr = VersionControlHelper.service.allIterationsOf(mst);
                if (qr.hasMoreElements()) {
                    itr = (Iterated) qr.nextElement();
                }
            } catch (PersistenceException e) {
                e.printStackTrace();
            } catch (WTException e) {
                e.printStackTrace();
            }
        }
        return itr;
    }
}
