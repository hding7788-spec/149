package ext.casc.common.mvc.builder;

import cn.hutool.core.util.StrUtil;
import com.ptc.jca.mvc.components.JcaComponentParams;
import com.ptc.jca.mvc.components.JcaTableConfig;
import com.ptc.mvc.components.*;
import com.ptc.mvc.components.ds.DataSourceMode;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import ext.casc.integrate.nc.ErpSynchHelper;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.json.JSONArray;
import org.json.JSONObject;
import wt.doc.WTDocument;
import wt.part.WTPart;
import wt.util.WTException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.rpc.encoding.XMLType;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

@ComponentBuilder("ext.casc.common.mvc.builder.SjzykPartReferencePriceTableBuilder")
public class SjzykPartReferencePriceTableBuilder extends AbstractComponentBuilder {

	@Override
	public Object buildComponentData(ComponentConfig componentConfig, ComponentParams componentParams) throws Exception {
		NmCommandBean cb = ((JcaComponentParams) componentParams).getHelperBean().getNmCommandBean();
		Object p = cb.getPageOid().getRefObject();
		Map<String,String> map = new HashMap<String,String>();
		if(p instanceof  WTPart){
			WTPart part = (WTPart) p;
			map.put("partNumber", part.getNumber());
			map.put("partName", part.getName());
			map.put("partPrice", searchPartPrice(part.getNumber()));
		}
		return map;
	}

	public static String searchPartPrice(String partNumber) {
		String url = "http://10.112.5.21:80/default/MtdocInfoImplService?wsdl";
		String price = "";
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(url);
			call.setOperationName(new QName("http://servcie.alms4zyk.alms.acconsys.com/", "getPriceInfoBymtcode"));
			call.addParameter("in0", XMLType.XSD_STRING, ParameterMode.IN);
			call.setReturnType(XMLType.XSD_STRING);
			String retrnMsg = (String) call.invoke(new Object[]{partNumber});
			System.out.println("========searchPartPrice==========  partNumber:" + partNumber + "  retrnMsg:" + retrnMsg);
			if(StrUtil.isNotEmpty(retrnMsg)){
				JSONObject jsonObject = new JSONObject(retrnMsg);
				String state = jsonObject.optString("state");
				if("0".equals(state)) {
					JSONArray value = jsonObject.getJSONArray("value");
					if(value.length() > 0) {
						JSONObject valObj = value.getJSONObject(0);
						price = valObj.optString("mtcode_price");
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return price;
	}

	@Override
	public ComponentConfig buildComponentConfig(ComponentParams componentParams) throws WTException {
		ComponentConfigFactory factory = getComponentConfigFactory();
		JcaTableConfig tableConfig = (JcaTableConfig) factory.newTableConfig();
		tableConfig.setDataSourceMode(DataSourceMode.SYNCHRONOUS);
		tableConfig.setSelectable(false);

		tableConfig.setLabel("设计资源库编码参考价格");

		ColumnConfig objectNumber = factory.newColumnConfig("partNumber", false);
		objectNumber.setLabel("编号");
		tableConfig.addComponent(objectNumber);

		ColumnConfig partName = factory.newColumnConfig("partName", false);
		partName.setLabel("名称");
		tableConfig.addComponent(partName);

		ColumnConfig partPrice = factory.newColumnConfig("partPrice", false);
		partPrice.setLabel("参考价格");
		tableConfig.addComponent(partPrice);

		return tableConfig;
	}

}
