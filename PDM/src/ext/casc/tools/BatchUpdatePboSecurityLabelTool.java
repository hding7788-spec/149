package ext.casc.tools;

import cn.hutool.core.util.StrUtil;
import ext.casc.securitymgr.SecurityLabelDataHelper;
import ext.casc.util.IBAHelper;
import ext.casc.util.WCUtil;
import wt.fc.*;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.pdmlink.PDMLinkProduct;
import wt.query.*;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class BatchUpdatePboSecurityLabelTool implements RemoteAccess {
	public  static Map<String,String> miji = new HashMap<String,String>();

	static{
		miji.put("秘密", "MIMI");
		miji.put("秘密★", "MIMI");
		miji.put("秘密★10年", "MIMI");

		miji.put("机密", "JIMI");
		miji.put("机密★", "JIMI");
		miji.put("机密★20年", "JIMI");
	}

	public static void process() {
		try {
			int index[] = { 0 };
			QuerySpec spec = new QuerySpec(PDMLinkProduct.class);
			spec.setAdvancedQueryEnabled(true);
			ClassAttribute attribute = new ClassAttribute(PDMLinkProduct.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			spec.appendOpenParen();
			spec.appendWhere(new SearchCondition(attribute, SearchCondition.IN, WCUtil.getStringIBAQuery("SECRET", "秘密")), index);
			spec.appendOr();
			spec.appendWhere(new SearchCondition(attribute, SearchCondition.IN, WCUtil.getStringIBAQuery("SECRET", "机密")), index);
			spec.appendCloseParen();
			QueryResult result = PersistenceHelper.manager.find(spec);
			Long[] ids = new Long[result.size()];
			int i = 0;
			System.out.println("安全标签工具>>>>>>>>共查询到" + result.size() + "个涉密产品");
			while(result.hasMoreElements()){
				PDMLinkProduct linkProduct = (PDMLinkProduct) result.nextElement();
				ids[i] = PersistenceHelper.getObjectIdentifier(linkProduct).getId();
				i++;
			}
			//查询产品库所有部件
			QuerySpec qs = new QuerySpec(WTPart.class);
			qs.setAdvancedQueryEnabled(true);
			qs.appendWhere(new SearchCondition(new ClassAttribute(WTPart.class, WTPart.CONTAINER_ID), SearchCondition.IN, new ArrayExpression(ids)));
			qs.appendAnd();
			qs.appendOpenParen();
			ClassAttribute caId = new ClassAttribute(WTPart.class, Persistable.PERSIST_INFO + "." + PersistInfo.OBJECT_IDENTIFIER + "." + ObjectIdentifier.ID);
			qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, WCUtil.getStringIBAQueryByLike("SECRET", "秘密")), index);
			qs.appendOr();
			qs.appendWhere(new SearchCondition(caId, SearchCondition.IN, WCUtil.getStringIBAQueryByLike("SECRET", "机密")), index);
			qs.appendCloseParen();
			QueryResult qr = PersistenceHelper.manager.find(qs);
			System.out.println("安全标签工具>>>>>>>>共查询到" + qr.size() + "个部件");
			int count = 0;
			while (qr.hasMoreElements()) {
				WTPart part = (WTPart) qr.nextElement();
				String secret = IBAHelper.getIBAStringValue(part, "SECRET");
				if(StrUtil.isNotEmpty(secret) && miji.containsKey(secret)) {
					SecurityLabelDataHelper.setSecurityLabels(part, "MIJI", miji.get(secret));
					count++;
				}
			}
			System.out.println("安全标签工具>>>>>>>>共设置了" + count + "个涉密部件安全标签");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		String username = "wcadmin";
		String passwd = "Admin@149.941";
		if (args.length >= 2) {
			username = args[0];
			passwd = args[1];
			if (username == null)
				username = "wcadmin";
			if (passwd == null)
				passwd = "Admin@149.941";
		}
		System.out.println("------user:"+username+"    password:"+passwd);
		rms.setUserName(username);
		rms.setPassword(passwd);
		if (!RemoteMethodServer.ServerFlag) {
			Class<?>[] types = null;
			Object[] vals = null;
			types = new Class<?>[] {};
			vals = new Object[] {};
			if (types != null && vals != null) {
				try {
					rms.invoke("process", BatchUpdatePboSecurityLabelTool.class.getName(), null, types, vals);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}else{
			BatchUpdatePboSecurityLabelTool.process();

		}
	}



}
