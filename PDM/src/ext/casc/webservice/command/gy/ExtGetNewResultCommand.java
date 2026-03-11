/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice.command.gy;

import cn.hutool.core.util.StrUtil;
import ext.casc.analysisActivity.bean.AnalysisObjEntry;
import ext.casc.analysisActivity.bean.GWDealProductRecord;
import ext.casc.analysisActivity.helper.AnalysisConstant;
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
import wt.pom.Transaction;

import java.util.List;

/**
 * 类功能：修改已制品计调员负责人
 * <p>
 * 影响分析编号、图号、新的负责人
 *
 * @author chenjianhui
 * @date 2024/06/26
 */
@Component
public class ExtGetNewResultCommand implements WebServiceCommand, InitializingBean {
    // 方法标识
    public static final String METHOD_NAME = "getNewResult";

    private static String PARA_ANALYSISNUMBER = "analysisNumber";
    private static String PARA_PARTID = "partId";
    private static String PARA_TYPE = "type";//YZP/ZJWX

    @Override
    public String execute(String params) {
        AnalysisSyncLogger logger = AnalysisSyncLogger.getInstance();
        JSONObject rtnMsgObj = new JSONObject();
        String errorMsg = "";
        JSONObject jparams = null;
        try {
            jparams = new JSONObject(params);
            logger.log("getNewResult jparams=" + jparams);
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
                AnalysisObjEntry zaizhipin = null;
                AnalysisObjEntry yizhipin = null;
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
                    zaizhipin = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, AnalysisConstant.TYPE_ZAIZHIPIN);
                    yizhipin = AnalysisUtil.getAnalysisObjEntry(analysisNumber, partId, AnalysisConstant.TYPE_YIZHIPIN);
                    if(zaizhipin == null) {
                        errorMsg += "未查询到当前影响分析单部件ID为" + partId + "的在制品条目";
                    }
                    if(yizhipin == null) {
                        errorMsg += "未查询到当前影响分析单部件ID为" + partId + "的已制品条目";
                    }
                }
                if(StrUtil.isEmpty(errorMsg) && zaizhipin != null && yizhipin != null) {
                    if("YZP".equals(type)) {
                        List<GWDealProductRecord> list = ExtGetProductDealResultCommand.queryERPDealRecordDatas(partId, analysisNumber, AnalysisConstant.SOURCE_YIZHIPIN);
                        for(GWDealProductRecord record : list) {
                            record.setStatus("");
                            record.setFinishTime("");
                            record.setComments("WAITNC");
                            CmPersistenceHelper.manager.update(record);
                        }
                    } else if("ZJWX".equals(type)) {
                        List<GWDealProductRecord> list = ExtGetProductDealResultCommand.queryERPDealRecordDatas(partId, analysisNumber, AnalysisConstant.SOURCE_ZAIZHIPIN);
                        for(GWDealProductRecord record : list) {
                            CmPersistenceHelper.manager.delete(record);
                        }
                    }
                    //校验制品
                    if(AnalysisUtil.checkProductDealResult(zaizhipin, true)) {
                        zaizhipin.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                    } else {
                        zaizhipin.setDealStatus(AnalysisConstant.DEAL_STATUS_WORKING);
                    }
                    CmPersistenceHelper.manager.update(zaizhipin);
                    if(AnalysisUtil.checkProductDealResult(yizhipin, true)) {
                        yizhipin.setDealStatus(AnalysisConstant.DEAL_STATUS_FINISH);
                    } else {
                        yizhipin.setDealStatus(AnalysisConstant.DEAL_STATUS_WORKING);
                    }
                    CmPersistenceHelper.manager.update(yizhipin);
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
            logger.log("getNewResult返回结果：" + rtnMsgObj);
        }
        return rtnMsgObj.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WebServiceCommandFactory.register(METHOD_NAME, this);
    }

}
