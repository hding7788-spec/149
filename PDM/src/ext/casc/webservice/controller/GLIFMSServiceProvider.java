package ext.casc.webservice.controller;

import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 杨方宁
 * 通过Restful向IFMS系統提供接口服务的入口类
 */
@Controller
@RequestMapping("/glWebService")
public class GLIFMSServiceProvider {
    public final static org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(GLIFMSServiceProvider.class);

    /**
     * 方法功能:创建、更新及作废工艺资源
     * 集成工单-01
     *
     * @param request
     * @param response
     * @return void
     * @author liaojun
     * @date 2020-7-30
     */
    @RequestMapping(value = "/assignProcessResourceSpec", method = RequestMethod.GET)
    public void assignProcessResourceSpec(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String parameter = request.getParameter("parameter");
        try {
            response.getWriter().write("hello world");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 方法功能:获取PDM系统工艺类别（工艺类型）的基本信息。
     * 调用 http://pdm.800.sast.casc/Windchill/app/glWebService/getProcessTypeInfo
     * 集成工单-01
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/getProcessTypeInfo", method = RequestMethod.GET)
    public void getProcessTypeInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        try {
            response.getWriter().write("hello world");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 方法功能:下达“外包技术要求编制任务通知”
     * 调用 http://pdm.800.sast.casc/Windchill/app/glWebService/assignOutsourceSpecificationWorkoutTaskNotice
     * 集成工单-09
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/assignOutsourceSpecificationWorkoutTaskNotice", method = RequestMethod.GET)
    @ResponseBody
    public void assignOutsourceSpecificationWorkoutTaskNotice(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        String parameter = request.getParameter("parameter");
        try {
            response.getWriter().write(parameter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
