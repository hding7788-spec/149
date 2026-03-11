/**
 * 南京国睿信维软件有限公司
 */
package ext.casc.webservice;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 类功能：
 *
 * @author hding
 * @date 2020/6/24
 */
public class WebServiceCommandFactory {

    private static Map<String, WebServiceCommand> services = new ConcurrentHashMap<String, WebServiceCommand>();

    private WebServiceCommandFactory() {
    }

    public static void register(String methodName, WebServiceCommand wscommand) {
        services.put(methodName, wscommand);
    }

    public static WebServiceCommand getWebServiceCommand(String methodName) {
        return services.get(methodName);
    }
}
