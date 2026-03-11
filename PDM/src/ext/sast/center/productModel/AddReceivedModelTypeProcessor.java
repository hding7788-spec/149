package ext.sast.center.productModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.common.view.PropertyHolderHelper;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.LWCTypeDefinition;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.netmarkets.model.NmSimpleOid;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.netmarkets.util.misc.NmContext;
import com.ptc.windchill.enterprise.dsvcore.server.utils.PersistableHelper;

import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class AddReceivedModelTypeProcessor extends DefaultObjectFormProcessor {
	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
		
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			ArrayList<?> selected = commandBean.getSelected();
			for(int i=0;i<selected.size();i++) {
				NmContext context = (NmContext) selected.get(i);
				NmSimpleOid sOid = (NmSimpleOid)context.getTargetOid();
				LWCTypeDefinition lwcType = (LWCTypeDefinition)PersistableHelper.findPersistable(sOid.getInternalName().toString());
				String displayType = lwcType.getDisplayIdentifier().getLocalizedMessage(Locale.CHINA);
				TypeDefinitionReadView ty = TypeDefinitionServiceHelper.service.getTypeDefView(displayType);
				String parentType = TypeDefinitionServiceHelper.service.getRootTypeDefView(ty).getName();			
				String zhType = PropertyHolderHelper.getDisplayName(ty,Locale.CHINA);
				String usType = ty.getName();
				sb = new StringBuilder();
				sb.append("select m.MODELTYPE_ID from RECEIVESCOPEMODELTYPE m where m.MODELTYPE_ID = '"+usType+"' ");
				pstmt = conn.prepareStatement(sb.toString());
				ResultSet set = pstmt.executeQuery();
				boolean has = set.next();
				pstmt.close();
				set.close();
				if(has) {
					continue;
				}else {
					sb = new StringBuilder();
					sb.append("insert into RECEIVESCOPEMODELTYPE (PARENT_MODELTYPE_ID,PARENT_MODELTYPE_NAME,MODELTYPE_ID,MODELTYPE_NAME ) values ("
							+ "?,?,?,?)");
					pstmt = conn.prepareStatement(sb.toString());
					if(parentType.contains("WTPart")){
						pstmt.setString(1, "部件");
						pstmt.setString(2, "WTPart");
					}else if(parentType.contains("WTDocument")){
						pstmt.setString(1, "文档");
						pstmt.setString(2, "WTDocument");
					}
					pstmt.setString(3, usType);
					pstmt.setString(4, zhType);
					int num = pstmt.executeUpdate();
					if(num>0) {
						conn.commit();
					}
					pstmt.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionServerHelper.manager.setAccessEnforced(flag);
		}
		
		return formresult;
	}
}
