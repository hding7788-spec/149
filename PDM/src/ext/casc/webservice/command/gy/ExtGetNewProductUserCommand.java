/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.gy;

import cn.hutool.core.util.StrUtil;
import com.glaway.mpm.util.UserUtil;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.helper.AnalysisUtil;
import ext.casc.analysisActivity.log.AnalysisSyncLogger;
import ext.casc.webservice.WebServiceCommand;
import ext.casc.webservice.WebServiceCommandFactory;
import ext.sast.common.fc.CmPersistenceHelper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import wt.change2.WTAnalysisActivity;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.pom.Transaction;

/**
 * 类功能：修改制品负责人
 * <p>
 * 影响分析编号、图号、新的负责人
 *
 * @author chenjianhui
 * @date 2024/06/26
 */
@Component
public class ExtGetNewProductUserCommand implements WebServiceCommand, InitializingBean {
    // 方法标识
    public static final String METHOD_NAME = "getNewProductUser";

    private static String PARA_ANALYSISNUMBER = "analysisNumber";
    private static String PARA_PARTID = "partId";
    private static String PARA_TYPE = "type";   //zproduct/yproduct
    private static String PARA_USERNAME = "userName";

    @Override
    public String execute(String params) {
        AnalysisSyncLogger logger = AnalysisSyncLogger.getInstance();
        JSONObject rtnMsgObj = new JSONObject();
        String errorMsg = "";
        JSONObject jparams = null;
        try {
            jparams = new JSONObject(params);
            logger.log("getNewProductUser jparams=" + jparams);
        } catch(JSONException e) {
            errorMsg = "参数JSON格式不正确： " + e.getLocalizedMessage();
            rtnMsgObj.put("status", "N");
            rtnMsgObj.put("result", errorMsg);
            logger.log(rtnMsgObj.toString());
            return rtnMsgObj.toString();
        }
        Transaction tran = new Transaction();
        try {
            if(StrUtil.isEmpty(errorMsg)) {
                tran.start();
                //校验数据
                String analysisNumber = jparams.optString(PARA_ANALYSISNUMBER);
                String partId = jparams.optString(PARA_PARTID);
                String type = jparams.optString(PARA_TYPE);
                String userName = jparams.optString(PARA_USERNAME);
                AnalysisObjEntry entry = null;
                WTUser user = null;
                if(StrUtil.isNotEmpty(analysisNumber)) {
                    WTAnalysisActivity activity = AnalysisUtil.getWTAnalysisActivityByNumber(analysisNumber);
                    if(activity == null) {
                        errorMsg = "未查询到编号为" + analysisNumber + "的更改影响分析。";
                        rtnMsgObj.put("status", "N");
                        rtnMsgObj.put("result", errorMsg);
                        logger.log(rtnMsgObj.toString());
                        return rtnMsgObj.toString();
                    }
                } else {
                    errorMsg = "影响分析编号不允许为空！";
                    rtnMsgObj.put("status", "N");
                    rtnMsgObj.put("result", errorMsg);
                    logger.log(rtnMsgObj.toString());
                    return rtnMsgObj.toString();
                }
                //已制品校验
                if(StrUtil.isNotEmpty(partId)) {
                    entry = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, type);
                    if(entry == null) {
                        errorMsg += "未查询到当前影响分析单部件ID为" + partId + "的制品条目";
                    }
                }
                //用户名校验
                if(StrUtil.isNotEmpty(userName)) {
                    user = UserUtil.getUser(userName);
                    if(user == null) {
                        errorMsg += "未查询到用户名为" + userName + "的用户";
                    }
                }
                if(StrUtil.isEmpty(errorMsg) && entry != null && user != null) {
                    ReferenceFactory rf = new ReferenceFactory();
                    String userId = rf.getReferenceString(user);
                    entry.setResponser(userId);
                    CmPersistenceHelper.manager.update(entry);
                }
            }
            tran.commit();
            tran = null;

        } catch(Exception e) {
            e.printStackTrace();
            errorMsg += e.getMessage();
        } finally {
            if(tran != null) {
                tran.rollback();
            }
        }
        try {
            if(errorMsg != null && !"".equals(errorMsg)) {
                rtnMsgObj.put("status", "N");
                rtnMsgObj.put("result", errorMsg);
            } else {
                rtnMsgObj.put("status", "Y");
                rtnMsgObj.put("result", "调用成功");
            }
        } catch(JSONException e) {
            e.printStackTrace();
        } finally {
            logger.log("getNewProductUser返回结果：" + rtnMsgObj);
        }
        return rtnMsgObj.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }

}
