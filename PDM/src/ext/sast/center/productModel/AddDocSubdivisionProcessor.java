package ext.sast.center.productModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class AddDocSubdivisionProcessor extends DefaultObjectFormProcessor {
	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("insert into DOCSUBDIVISION (innerId,comeFromSiteName,docTypeName,docTypeInnerName,localDocTypeName,localDocTypeInnerName) values"
					+ " (?,?,?,?,?,?)");
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, UUID.randomUUID().toString());
			pstmt.setString(2, "COMEFROMSITENAME");
			pstmt.setString(3, "DOCTYPENAME");
			pstmt.setString(4, "");
			pstmt.setString(5, "LOCALDOCTYPENAME");
			pstmt.setString(6, "");
			int num = pstmt.executeUpdate();
			if(num>0) {
				conn.commit();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			SessionServerHelper.manager.setAccessEnforced(flag);
			try {
				if(pstmt != null){
                    pstmt.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return formresult;
	}
}
