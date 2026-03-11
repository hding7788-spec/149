package ext.sast.center.productModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.DefaultObjectFormProcessor;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;

import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionServerHelper;
import wt.util.WTException;

public class DeleteRowProcessor extends DefaultObjectFormProcessor {
	@Override
	public FormResult doOperation(NmCommandBean commandBean, List<ObjectBean> objectBeans) throws WTException {
		FormResult formresult = super.doOperation(commandBean, objectBeans);
		boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
		WTConnection wtconnection = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		StringBuilder sb = null;
		try {
			String selectedRowValues = commandBean.getTextParameter("selectedRowValues");
			MethodContext methodcontext = MethodContext.getContext();
			wtconnection = (WTConnection) methodcontext.getConnection();
			conn = wtconnection.getConnection();
			sb = new StringBuilder();
			sb.append("delete RECEIVEDMODELTYPE where id = '"+selectedRowValues+"'");
			pstmt = conn.prepareStatement(sb.toString());
			int num = pstmt.executeUpdate();
			if(num>0) {
				conn.commit();
			}

			
			FeedbackMessage message = new FeedbackMessage(FeedbackType.SUCCESS, null,"成功", null, "删除成功");
			formresult.addFeedbackMessage(message);
			formresult.setNextAction(FormResultAction.JAVASCRIPT);
			formresult.setJavascript("reshTableBuilder();");
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
