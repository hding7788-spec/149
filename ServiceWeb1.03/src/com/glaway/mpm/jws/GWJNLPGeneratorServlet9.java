package com.glaway.mpm.jws;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import wt.taglib.util.ClientJarManager;
import wt.util.WTProperties;

public class GWJNLPGeneratorServlet9 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String version = request.getParameter("version");
		if (version != null)
		{
			response.setHeader("x-java-jnlp-version-id", version);
		}
		HttpSession session = request.getSession();
		WTProperties wt_properties = WTProperties.getLocalProperties();
		String codebase = wt_properties.getServerCodebase().toString();
		String minimumJreVersion = wt_properties.getProperty("wt.taglib.util.plugin.attr.jreversion", "1.5");
		String sessionCookieName = wt_properties.getProperty("wt.servlet.sessionCookie", "JSESSIONID");
		String suggestedJreVersion = "1.6+";
		String jreDownloadURL = "http://java.sun.com/products/autodl/j2se";

		// ////////////////////////
		// set content type to JNLP
		// /////////////////////////
		response.setContentType("application/x-java-jnlp-file");
		response.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();

		// ////////////////////////
		// get request parameters
		// /////////////////////////
		String title = request.getParameter("title");
		String vendor = request.getParameter("vendor");
		String description = request.getParameter("description");
		String short_description = request.getParameter("short_description");
		String icon_href = request.getParameter("icon");
		String vm_args = request.getParameter("vm_args");
		String mainclass = request.getParameter("mainclass");
		String appletclass = request.getParameter("appletclass");

		String all_perm = request.getParameter("allperm");

		String sid = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
		{
			for (int x = 0; x < cookies.length; x++)
			{
				if (sessionCookieName.equalsIgnoreCase(cookies[x].getName()))
				{
					// use session id from sessionCookieName value
					sid = cookies[x].getValue();
					break;
				}
			}
		}
		if (sid == null)
		{
			// didn't find session id in cookies, default to session.getId();
			sid = session.getId();
		}
		String host_name = request.getRemoteHost();
		if (host_name != null)
		{
			host_name = request.getRemoteAddr();
		}

		String documentBase = request.getParameter("documentbase");
		if (documentBase == null)
			documentBase = codebase;

		String jars_str = request.getParameter("jars");
		String[] jars = null;
		if (jars_str != null)
			jars = jars_str.split(",");

		String exts_str = request.getParameter("exts");
		String[] exts = null;
		if (exts_str != null)
			exts = exts_str.split(",");
		//add by yundong
		String exts_str32 = request.getParameter("exts32");
		String[] exts32 = null;
		if (exts_str32 != null)
			exts32 = exts_str32.split(",");

		String exts_str64 = request.getParameter("exts64");
		String[] exts64 = null;
		if (exts_str64 != null)
			exts64 = exts_str64.split(",");
		//end

		String exts_sec_str = request.getParameter("exts_sec");
		String[] exts_sec = null;
		if (exts_sec_str != null)
			exts_sec = exts_sec_str.split(",");

		String params_str = request.getParameter("params");
		String[] params = null;
		if (params_str != null)
			params = params_str.split(",");

		String width_str = request.getParameter("width");
		String height_str = request.getParameter("height");
		int width = 100;
		int height = 100;
		try
		{
			width = Integer.parseInt(width_str);
			height = Integer.parseInt(height_str);
		} catch (NumberFormatException e)
		{
			// nothing
		}

		// TO_DO: rework all of this to use proper XML output techniques rather
		// than simply assuming no <, >, &, etc, characters!

		// ////////////////////////////////////////////
		// create JNLP file
		// ///////////////////////////////////////////
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); // be
																	// explicit
																	// about
																	// encoding
		// JNLP header
		out.println("<jnlp spec=\"1.0+\" codebase=\"" + codebase
		// + "\" href=\"" + request.getRequestURL() + "?" +
		// request.getQueryString()
				+ "\">");
		// JNLP Information
		out.println(" <information>");
		if (title != null)
			out.println("  <title>" + title + "</title>");
//		if(vendor != null) {
			out.println(" <vendor>上海航天设备制造总厂与南京国睿信维软件有限公司</vendor>");
//		}
		out.println(" <homepage href=\"\"/>");
		if (description != null)
			out.println("  <description>" + description + "</description>");
		if (short_description != null)
			out.println("  <description kind=\"short\">" + short_description
					+ "</description>");
		if (icon_href != null)
			out.println("  <icon href=\"" + icon_href + "\"/>");
		// out.println(" <offline-allowed/>"); //offline doesn't make sense for
		// Windchill apps
		out.println(" </information>");
		// JNLP Security
		// Do we require permission for this particular apps
		if ((all_perm != null) && (all_perm.equals("1")))
		{
			out.println(" <security>");
			out.println("      <all-permissions/>");
			out.println(" </security>");
		}

		// JNLP Resource - define the default required browser (prefered)
		out.println(" <resources>");

		// extract the -D properties from vm_args so they can be added as
		// <property> elements
		List<String> property_vm_args = new ArrayList<String>();

		String temp_vm_args = vm_args;
		if (temp_vm_args != null)
		{
			while (true)
			{
				int found_idx = temp_vm_args.indexOf("-D");

				// if we don't find a -D property then break
				if (found_idx == -1)
					break;

				int next_prop_idx = temp_vm_args.indexOf("-D", found_idx + 1);
				int next_vmarg_idx = temp_vm_args.indexOf("-X", found_idx + 1);
				String property = null;

				if (next_prop_idx >= 0
						&& (next_prop_idx < next_vmarg_idx || next_vmarg_idx == -1))
				{
					// after our current -D property, the next property is
					// another -D property
					// copy the current -D property up the the start of the next
					// one
					property = temp_vm_args.substring(found_idx,
							next_prop_idx - 1);
				} else if (next_vmarg_idx >= 0
						&& (next_vmarg_idx < next_prop_idx || next_prop_idx == -1))
				{
					// after our current -D property, the next property is a -X
					// property
					// copy the current -D property up the the start of the -X
					// property
					property = temp_vm_args.substring(found_idx,
							next_vmarg_idx - 1);
				} else
				{
					// We did not find either another -D or -X property
					// assume the current -D property is at the last property
					// and copy the remaining temp string
					property = temp_vm_args.substring(found_idx);
				}

				// remove the current -D property from the temp string, trimming
				// the string with each loop
				temp_vm_args = temp_vm_args.replace(property, "");

				// purge the '-D' characters from the property and add it to our
				// list
				property_vm_args.add(property.replace("-D", ""));
			}
		}
		out.print(" <j2se version=\"" + suggestedJreVersion + "\" href=\""
				+ jreDownloadURL + "\"");
		if (vm_args != null)
			out.print(" java-vm-args=\"" + vm_args + "\"");
		out.println(" />");

		// define the minumum java version
		out.print(" <j2se version=\"" + minimumJreVersion + "\"");
		if (vm_args != null)
			out.print(" java-vm-args=\"" + vm_args + "\"");
		out.println(" />");

		// create <property> elements for any -D properties specified in vm_args
		for (String property : property_vm_args)
		{
			int eq_idx = property.indexOf('=');
			String key = property.substring(0, eq_idx);
			String value = property.substring(eq_idx + 1);
			out.println("<property name=\"" + key + "\" value=\"" + value
					+ "\" />");
		}

		if (jars != null)
		{
			for (String jar : jars)
			{
				// OK, maybe this jar is having depedencies, so let's discover
				// them and also bring back the version on the server
				List<String> listJar = ClientJarManager
						.getCacheArchiveList(jar);
				List<String> listVersion = ClientJarManager
						.getCacheVersionList(jar);
				for (int i = 0; i < listJar.size(); i++)
				{
					out.print(" <jar href=\"" + listJar.get(i) + "\" ");
					if ((listVersion != null)
							&& (listVersion.size() == listJar.size()))
						out.print(" version=\"" + listVersion.get(i) + "\"");
					out.println(" />");
				}
			}
		}
		// //////////////////////////////////////////////////////////////////////////
		// Extension jars that doesn't require particular security (are not
		// signed)
		// ////////////////////////////////////////////////////////////////////////////
		if (exts != null)
		{
			for (String ext : exts)
			{
				List<String> listJar = ClientJarManager
						.getCacheArchiveList(ext);
				List<String> listVersion = ClientJarManager
						.getCacheVersionList(ext);
				if (ext.endsWith(".jar"))
					out.println("   <extension name=\""
							+ ext.substring(0, ext.indexOf('.'))
							+ "\" version=\"" + listVersion.get(0)
							+ "\" href=\"" + codebase
							+ "servlet/GWJNLPGeneratorServlet/"
							+ ext.substring(0, ext.indexOf('.'))
							+ ".jnlp?title="
							+ ext.substring(0, ext.indexOf('.')) + "&jars="
							+ ext + "&version=" + listVersion.get(0)
							+ "&dummy=dummy.jnlp \"/>");
				else
					out.println("   <extension name=\""
							+ ext.substring(0, ext.indexOf('.')) + "\" href=\""
							+ ext.substring(ext.indexOf('.') + 1) + "\"/>");
			}
		}
		// //////////////////////////////////////////////////////////////////////////
		// Extension jars that need security (signed)
		// ////////////////////////////////////////////////////////////////////////////
		if (exts_sec != null)
		{
			for (String ext_sec : exts_sec)
			{
				if (ext_sec.endsWith(".jar"))
				{
					out.println("   <extension name=\""
							+ ext_sec.substring(0, ext_sec.indexOf('.'))
							+ "\" href=\"" + codebase
							+ "servlet/JNLPGeneratorServlet/"
							+ ext_sec.substring(0, ext_sec.indexOf('.'))
							+ ".jnlp?title="
							+ ext_sec.substring(0, ext_sec.indexOf('.'))
							+ "&jars=" + ext_sec + "&allperm=1"
							+ "&dummy=dummy.jnlp\"/>");
				} else
				{
					out.println("   <extension name=\""
							+ ext_sec.substring(0, ext_sec.indexOf('.'))
							+ "\" href=\""
							+ ext_sec.substring(ext_sec.indexOf('.') + 1)
							+ "\"/>");
				}
			}
		}
		out.println(" </resources>");

		if (exts64 != null)
		{
			out.println("<resources os=\"Windows\" arch=\"amd64\">");
			for (String ext64 : exts64)
			{
				List<String> listJar = ClientJarManager
						.getCacheArchiveList(ext64);
				List<String> listVersion = ClientJarManager
						.getCacheVersionList(ext64);
				if (ext64.endsWith(".jar"))
					out.println("   <extension name=\""
							+ ext64.substring(0, ext64.indexOf('.'))
							+ "\" version=\"" + listVersion.get(0)
							+ "\" href=\"" + codebase
							+ "servlet/GWJNLPGeneratorServlet/"
							+ ext64.substring(0, ext64.indexOf('.'))
							+ ".jnlp?title="
							+ ext64.substring(0, ext64.indexOf('.')) + "&jars="
							+ ext64 + "&version=" + listVersion.get(0)
							+ "&dummy=dummy.jnlp \"/>");
				else
					out.println("   <extension name=\""
							+ ext64.substring(0, ext64.indexOf('.')) + "\" href=\""
							+ ext64.substring(ext64.indexOf('.') + 1) + "\"/>");
			}
			out.println(" </resources>");
		}

		if (exts64 != null)
		{
			out.println("<resources os=\"Windows\" arch=\"x86_64\">");
			for (String ext64 : exts64)
			{
				List<String> listJar = ClientJarManager
						.getCacheArchiveList(ext64);
				List<String> listVersion = ClientJarManager
						.getCacheVersionList(ext64);
				if (ext64.endsWith(".jar"))
					out.println("   <extension name=\""
							+ ext64.substring(0, ext64.indexOf('.'))
							+ "\" version=\"" + listVersion.get(0)
							+ "\" href=\"" + codebase
							+ "servlet/GWJNLPGeneratorServlet/"
							+ ext64.substring(0, ext64.indexOf('.'))
							+ ".jnlp?title="
							+ ext64.substring(0, ext64.indexOf('.')) + "&jars="
							+ ext64 + "&version=" + listVersion.get(0)
							+ "&dummy=dummy.jnlp \"/>");
				else
					out.println("   <extension name=\""
							+ ext64.substring(0, ext64.indexOf('.')) + "\" href=\""
							+ ext64.substring(ext64.indexOf('.') + 1) + "\"/>");
			}
			out.println(" </resources>");
		}

		if (exts32 != null)
		{
			out.println("<resources os=\"Windows\" arch=\"i386\">");
			for (String ext32 : exts32)
			{
				List<String> listJar = ClientJarManager
						.getCacheArchiveList(ext32);
				List<String> listVersion = ClientJarManager
						.getCacheVersionList(ext32);
				if (ext32.endsWith(".jar"))
					out.println("   <extension name=\""
							+ ext32.substring(0, ext32.indexOf('.'))
							+ "\" version=\"" + listVersion.get(0)
							+ "\" href=\"" + codebase
							+ "servlet/GWJNLPGeneratorServlet/"
							+ ext32.substring(0, ext32.indexOf('.'))
							+ ".jnlp?title="
							+ ext32.substring(0, ext32.indexOf('.')) + "&jars="
							+ ext32 + "&version=" + listVersion.get(0)
							+ "&dummy=dummy.jnlp \"/>");
				else
					out.println("   <extension name=\""
							+ ext32.substring(0, ext32.indexOf('.')) + "\" href=\""
							+ ext32.substring(ext32.indexOf('.') + 1) + "\"/>");
			}
			out.println(" </resources>");
		}

		if (exts32 != null)
		{
			out.println("<resources os=\"Windows\" arch=\"x86\">");
			for (String ext32 : exts32)
			{
				List<String> listJar = ClientJarManager
						.getCacheArchiveList(ext32);
				List<String> listVersion = ClientJarManager
						.getCacheVersionList(ext32);
				if (ext32.endsWith(".jar"))
					out.println("   <extension name=\""
							+ ext32.substring(0, ext32.indexOf('.'))
							+ "\" version=\"" + listVersion.get(0)
							+ "\" href=\"" + codebase
							+ "servlet/GWJNLPGeneratorServlet/"
							+ ext32.substring(0, ext32.indexOf('.'))
							+ ".jnlp?title="
							+ ext32.substring(0, ext32.indexOf('.')) + "&jars="
							+ ext32 + "&version=" + listVersion.get(0)
							+ "&dummy=dummy.jnlp \"/>");
				else
					out.println("   <extension name=\""
							+ ext32.substring(0, ext32.indexOf('.')) + "\" href=\""
							+ ext32.substring(ext32.indexOf('.') + 1) + "\"/>");
			}
			out.println(" </resources>");
		}

		// //////////////////////////////////////
		// applet definition
		// /////////////////////////////////////
		if (appletclass != null)
		{
			out.println("  <applet-desc " + "name=\"" + title + "\" "
					+ "documentbase=\"" + codebase + documentBase + "\" "
					+ "main-class=\"" + appletclass + "\" " + "width=\""
					+ width + "\" " + "height=\"" + height + " \" " + ">");
			if (params != null)
			{
				for (String param : params)
				{
					out.println("   <param name=\""
							+ param.substring(0, param.indexOf('='))
							+ "\" value=\""
							+ param.substring(param.indexOf('=') + 1) + "\"/>");
				}
				// force hostname, sid & sessioncookie in the parameters as
				// those are use by the JNLPLauncher
				if (sessionCookieName != null)
					out.println("   <param name=\"sessionCookie\" value\""
							+ sessionCookieName + "\"/>");
				if (sid != null)
					out.println("   <param name=\"sid\" value=\"" + sid
							+ "\"/>");
				if (host_name != null)
					out.println("   <param name=\"hostname\" value=\""
							+ host_name + "\"/>");
			}
			out.println("  </applet-desc>");
			// //////////////////////////////////////////
			// application definition
			// ///////////////////////////////////////////
		} else if (mainclass != null)
		{
			out.println("  <application-desc " + "main-class=\"" + mainclass
					+ "\">");
			if (params != null)
			{
				for (String param : params)
				{
					out.println("   <argument>" + param + "</argument>");
				}
			}
			// force hostname & sid & authetication & sessioncookie in the
			// parameters as those are use by the JNLPLauncher
			if (sessionCookieName != null)
				out.println("   <argument>sessionCookie=" + sessionCookieName
						+ "</argument>");
			if (sid != null)
				out.println("   <argument>sid=" + sid + "</argument>");
			if (host_name != null)
				out.println("   <argument>hostname=" + host_name
						+ "</argument>");
			out.println("  </application-desc>");
		} else
		{
			out.println("  <component-desc/>");
		}
		out.println(" </jnlp>");
	}
}
