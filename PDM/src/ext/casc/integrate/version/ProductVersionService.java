package ext.casc.integrate.version;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import ext.casc.integrate.util.BomUtil;
import ext.casc.util.IBAUtility;
import ext.casc.util.WCUtil;

public class ProductVersionService implements IProductVersion{
	/* number:产品图号
	 * bomType:bom类型,Design/Manufacturing
	 *
	*/
	public String getAllBatches(String number,String bomType,String guid) throws WTException{
		StringBuffer batchBuffer = new StringBuffer();//批次信息
		batchBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		batchBuffer.append("<batchinfo>");
		if(!guid.equals("")){//验证身份有效性
			batchBuffer.append("<exception>");
			batchBuffer.append("用户身份验证不合格!");
			batchBuffer.append("</exception>");
			batchBuffer.append("</batchinfo>");
			return batchBuffer.toString();
		}
		 WTPart part = (WTPart)WCUtil.getPartByNumber(number);

		 if (part == null) {//输入的图号在PDM中不存在
			 batchBuffer.append("<exception>");
			 batchBuffer.append("所查找的编号在PDM中不存在!");
			 String check = BomUtil.checkNum(number);
			 if(!check.equals(""))
				 batchBuffer.append("相似编号如下：").append(check);
			 batchBuffer.append("</exception>");
			 batchBuffer.append("</batchinfo>");
			 return batchBuffer.toString();
	     }
		 WTPartMaster partMaster = (WTPartMaster)part.getMaster();
		 WTPart viewPart = WCUtil.getLatestPartByView(partMaster, bomType);
		 if(viewPart == null){//输入的图号在PDM中不存在指定视图的BOM
			 batchBuffer.append("<exception>");
			 batchBuffer.append("输入的图号在PDM中不存在指定视图").append(bomType).append("的BOM!");
			 batchBuffer.append("</exception>");
			 batchBuffer.append("</batchinfo>");
			 return batchBuffer.toString();
		 }
		 Set<String> list = getBatchesByPart(part);
		/*WTContained contained = part.getContainer();
		String productOid = String.valueOf(PersistenceHelper.getObjectIdentifier(contained).getId());
		List<Batch> list = DBUtil.getBatchesByProduct(productOid, contained.getContainerName());*/
        if(list.isEmpty()) {//图号在PDM中不存在批次信息
        	batchBuffer.append("<exception>");
			batchBuffer.append("输入的图号在PDM中不存在批次信息!");
			batchBuffer.append("</exception>");
			batchBuffer.append("</batchinfo>");
			return batchBuffer.toString();
        }
        else{
			for (String batch : list) {
				batchBuffer.append("<batch>");
				batchBuffer.append("<batchName>");
				batchBuffer.append(batch);
				batchBuffer.append("</batchName>");
				batchBuffer.append("</batch>");
			}
        }
		batchBuffer.append("</batchinfo>");
		return batchBuffer.toString();

	}

	private Set<String> getBatchesByPart(WTPart part) {
		Set<String> list = new HashSet<String>();
		boolean accessFlag = SessionServerHelper.manager.setAccessEnforced(false);
        try {
            QuerySpec qs = new QuerySpec(WTPart.class);
            qs.appendWhere(new SearchCondition(WTPart.class, "master>number", "=", part.getNumber()), new int[1]);

            qs.appendAnd();
            View view = ViewHelper.service.getView("Manufacturing");
            qs.appendWhere(
                    new SearchCondition(WTPart.class, "view.key.id",
                            "=", view.getPersistInfo().getObjectIdentifier().getId()), new int[1]);
            qs.setAdvancedQueryEnabled(true);
            QueryResult qr = PersistenceHelper.manager.find(qs);
        	while (qr.hasMoreElements()) {
        		WTPart p = (WTPart) qr.nextElement();
        		IBAUtility utility = new IBAUtility(p);
        		String BATCH = utility.getIBAValue("BATCH");//批次号
        		if(BATCH!=null&&!"".equals(BATCH)){
        			list.add(BATCH);
        		}
        	}
        } catch (WTException wte) {
            wte.printStackTrace();
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessFlag);
        }
        SessionServerHelper.manager.setAccessEnforced(accessFlag);
        return list;
	}


}
