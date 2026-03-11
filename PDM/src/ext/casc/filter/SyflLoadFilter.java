package ext.casc.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ext.csc.utilities.principal.CSCPrincipal;

import wt.inf.container.ExchangeContainer;
import wt.inf.container.WTContainerHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

public class SyflLoadFilter implements Filter {
	
	private FilterConfig filterConfig;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		try{
			String uri = ((HttpServletRequest)req).getServletPath() + (((HttpServletRequest)req).getPathInfo() == null ? "" : ((HttpServletRequest)req).getPathInfo());
			System.out.println(uri);
			if(uri.indexOf("servlet/WindchillGW")>-1||uri.indexOf("/servlet/ProwtGW")>-1||uri.indexOf("servlet/Navigation")>-1||uri.indexOf("/servlet/WindchillAuthGW")>-1||
					uri.indexOf("netmarkets/jsp/auditing")>-1||uri.indexOf("netmarkets/jsp/ext/casc/audit")>-1||uri.indexOf("/netmarkets/jsp/util/calPopup.jsp")>-1
					||uri.indexOf("netmarkets/jsp/rule/list.jsp")>-1
					||uri.indexOf("/netmarkets/jsp/util")>-1
					||uri.indexOf("/netmarkets/jsp/preference")>-1
					||uri.indexOf("/netmarkets/jsp/view")>-1
					||uri.indexOf("/netmarkets/jsp/workitem/workflowTaskbar.jsp")>-1
					||uri.indexOf("/calender/calenderMgmt.jsp")>-1
				||uri.indexOf("netmarkets/jsp/replication")>-1){
				filterChain.doFilter(req, response);
				return;
			}
			WTPrincipal p = SessionHelper.manager.getPrincipal();
			if (p instanceof WTUser) {
				WTUser user = (WTUser) p;
				if (isSysadmin(user)) {
					RequestDispatcher dis = req.getRequestDispatcher("/netmarkets/jsp/ext/casc/syfl/sysManager.jsp");
					dis.forward(req, response);
				} else if (isSecurityadmin(user)) {
					RequestDispatcher dis = req.getRequestDispatcher("/netmarkets/jsp/ext/casc/syfl/busManager.jsp");
					dis.forward(req, response);
				} else if (isAuditadmin(user)) {
					RequestDispatcher dis = req.getRequestDispatcher("/netmarkets/jsp/ext/casc/syfl/sjManager.jsp");
					dis.forward(req, response);
				} else {
					filterChain.doFilter(req, response);
				}
			}
		}catch (Exception e) {
			filterChain.doFilter(req, response);
			e.printStackTrace();
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	} 

	public static boolean isSysadmin(WTUser user) throws WTException{
		WTGroup group = CSCPrincipal.getGroupByName("系统管理员组");
		if(group==null) return false;
		return group.isMember(user);
	}
	
	public static boolean isSecurityadmin(WTUser user) throws WTException{
		WTGroup group = CSCPrincipal.getGroupByName("安全管理员组");
		if(group==null) return false;
		return group.isMember(user);
	}
	
	public static boolean isAuditadmin(WTUser user) throws WTException{
		WTGroup group = CSCPrincipal.getGroupByName("安全审计管理员组");
		if(group==null) return false;
		return group.isMember(user);
	}
}
