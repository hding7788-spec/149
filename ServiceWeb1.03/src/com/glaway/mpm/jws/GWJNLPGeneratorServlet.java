package com.glaway.mpm.jws;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import wt.jmx.core.XMLDumper;
import wt.taglib.util.ClientJarManager;
import wt.util.WTProperties;

public class GWJNLPGeneratorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public GWJNLPGeneratorServlet() {
	}

	protected void doGet(HttpServletRequest var1, HttpServletResponse var2) throws ServletException, IOException {
		String var3 = var1.getParameter("version");
		if (var3 != null) {
			var3 = var3.replace('\r', ' ').replace('\n', ' ');
			var2.setHeader("x-java-jnlp-version-id", var3);
		}

		var2.setDateHeader("Last-Modified", System.currentTimeMillis());
		HttpSession var4 = var1.getSession();
		WTProperties var5 = WTProperties.getLocalProperties();
		String var6 = WTProperties.getServerCodebase().toString();
		String var7 = var5.getProperty("wt.taglib.util.plugin.attr.jreversion", "1.7");
		String var8 = var5.getProperty("wt.servlet.sessionCookie", "JSESSIONID");
		String var9 = var5.getProperty("wt.servlet.sessionCookiePattern", (String)null);
		String var10 = var5.getProperty("wt.taglib.util.plugin.attr.java_version", "1.7+");
		boolean var11 = var5.getProperty("wt.applet.jws.deploy.protocolAuth", var5.getProperty("wt.auth.legacySupport.enabled", false));
		String var12 = "http://java.sun.com/products/autodl/j2se";
		var2.setContentType("application/x-java-jnlp-file");
		var2.setCharacterEncoding("UTF-8");
		String var13 = var1.getParameter("title");
		String var14 = var1.getParameter("description");
		String var15 = var1.getParameter("short_description");
		String var16 = var1.getParameter("icon");
		String var17 = var1.getParameter("vm_args");
		String var18 = var1.getParameter("mainclass");
		String var19 = var1.getParameter("appletclass");
		boolean var20 = "1".equals(var1.getParameter("allperm"));
		StringBuilder var21 = null;
		String var22 = null;
		Cookie[] var23 = var1.getCookies();
		String var28;
		if (var23 != null) {
			Cookie[] var24 = var23;
			int var25 = var23.length;

			for(int var26 = 0; var26 < var25; ++var26) {
				Cookie var27 = var24[var26];
				var28 = var27.getName();
				if (var28.equalsIgnoreCase(var8) || var9 != null && var28.matches(var9)) {
					if (var21 == null) {
						var21 = new StringBuilder();
					} else {
						var21.append("; ");
					}

					var21.append(var28 + "=" + var27.getValue());
				}
			}
		}

		if (var21 != null) {
			var22 = var21.toString();
		} else {
			var22 = var8 + "=" + var4.getId();
		}

		String var60 = var1.getRemoteHost();
		if (var60 != null) {
			var60 = var1.getRemoteAddr();
		}

		String var61 = var1.getParameter("documentbase");
		if (var61 != null) {
			try {
				new URL(var61);
			} catch (MalformedURLException var58) {
				var61 = var6 + var61;
			}
		} else {
			var61 = var6;
		}

		String var62 = var1.getParameter("jars");
		String[] var63 = null;
		if (var62 != null) {
			var63 = var62.split(",");
		}

		var28 = var1.getParameter("exts");
		LinkedHashSet var29 = new LinkedHashSet();
		if (var28 != null) {
			var29.addAll(Arrays.asList(var28.split(",")));
		}

		String var30 = var1.getParameter("exts_sec");
		LinkedHashSet var31 = new LinkedHashSet();
		if (var30 != null) {
			var31.addAll(Arrays.asList(var30.split(",")));
		}

		String[] var32 = var1.getParameterValues("params");
		if (var32 == null) {
			var32 = var1.getParameterValues("p");
		}

		String var33;
		if (var32 != null && var32.length == 1) {
			var33 = var32[0];
			if (var33 != null) {
				var32 = var33.split(",");
			}
		}

		var33 = var1.getParameter("width");
		String var34 = var1.getParameter("height");
		int var35 = 100;
		int var36 = 100;

		try {
			var35 = Integer.parseInt(var33);
			var36 = Integer.parseInt(var34);
		} catch (NumberFormatException var57) {
		}

		boolean var37 = "true".equals(var1.getParameter("omitSid"));
		String var38 = var1.getParameter("jnlpFilename");
		if (var38 == null) {
			var38 = var1.getPathInfo();
		}

		if (var38 == null) {
			var38 = var1.getParameter("title");
		}

		if (var38 == null && var63 != null && var63.length > 0) {
			var38 = var63[0].replace(".jar", ".jnlp");
		}

		if (var38 != null && !var38.isEmpty()) {
			var2.setHeader("Content-Disposition", "attachment; filename=" + getSafeJnlpFileName(var1.getHeader("User-Agent"), var38));
		}

		ContentHandler var39 = XMLDumper.getSerializer(var2.getWriter(), true, System.getProperty("line.separator"));

		try {
			var39.startDocument();
			AttributesImpl var40 = new AttributesImpl();
			XMLDumper.addAttribute(var40, "spec", "1.0+");
			XMLDumper.addAttribute(var40, "codebase", var6);
			XMLDumper.startXMLElement(var39, "jnlp", var40);
			XMLDumper.startXMLElement(var39, "information");
			if (var13 != null) {
				XMLDumper.sendSimpleElemToXML(var39, "title", var13);
			}

			XMLDumper.sendSimpleElemToXML(var39, "vendor", "上海航天设备制造总厂&南京国睿信维软件有限公司");
			var40 = new AttributesImpl();
			XMLDumper.addAttribute(var40, "href", "");
			XMLDumper.startXMLElement(var39, "homepage", var40);
			XMLDumper.endXMLElement(var39, "homepage");
			if (var14 != null) {
				XMLDumper.sendSimpleElemToXML(var39, "description", var14);
			}

			if (var15 != null) {
				var40 = new AttributesImpl();
				XMLDumper.addAttribute(var40, "kind", "short");
				XMLDumper.startXMLElement(var39, "description", var40);
				XMLDumper.sendTextNodeToXML(var39, var15);
				XMLDumper.endXMLElement(var39, "description");
			}

			if (var16 != null) {
				var40 = new AttributesImpl();
				XMLDumper.addAttribute(var40, "href", var16);
				XMLDumper.startXMLElement(var39, "icon", var40);
				XMLDumper.endXMLElement(var39, "icon");
			}

			XMLDumper.endXMLElement(var39, "information");
			if (var20) {
				XMLDumper.startXMLElement(var39, "security");
				XMLDumper.sendSimpleElemToXML(var39, "all-permissions", (String)null);
				XMLDumper.endXMLElement(var39, "security");
			}

			XMLDumper.startXMLElement(var39, "resources");
			ArrayList var41 = new ArrayList();
			String var42 = var17;
			int var44;
			int var45;
			String var46;
			if (var17 != null) {
				while(true) {
					int var43 = var42.indexOf("-D");
					if (var43 == -1) {
						break;
					}

					var44 = var42.indexOf("-D", var43 + 1);
					var45 = var42.indexOf("-X", var43 + 1);
					if (var44 < 0 || var44 >= var45 && var45 != -1) {
						if (var45 < 0 || var45 >= var44 && var44 != -1) {
							var46 = var42.substring(var43);
						} else {
							var46 = var42.substring(var43, var45 - 1);
						}
					} else {
						var46 = var42.substring(var43, var44 - 1);
					}

					var42 = var42.replace(var46, "");
					var41.add(var46.replace("-D", ""));
				}
			}

			var40 = new AttributesImpl();
			XMLDumper.addAttribute(var40, "version", var10);
			XMLDumper.addAttribute(var40, "href", var12);
			if (var17 != null) {
				XMLDumper.addAttribute(var40, "java-vm-args", var17);
			}

			XMLDumper.startXMLElement(var39, "j2se", var40);
			XMLDumper.endXMLElement(var39, "j2se");
			var40 = new AttributesImpl();
			XMLDumper.addAttribute(var40, "version", var7);
			if (var17 != null) {
				XMLDumper.addAttribute(var40, "java-vm-args", var17);
			}

			XMLDumper.startXMLElement(var39, "j2se", var40);
			XMLDumper.endXMLElement(var39, "j2se");
			Iterator var64 = var41.iterator();

			String var47;
			while(var64.hasNext()) {
				String var66 = (String)var64.next();
				var45 = var66.indexOf(61);
				var46 = var66.substring(0, var45);
				var47 = var66.substring(var45 + 1);
				var40 = new AttributesImpl();
				XMLDumper.addAttribute(var40, "name", var46);
				XMLDumper.addAttribute(var40, "value", var47);
				XMLDumper.startXMLElement(var39, "property", var40);
				XMLDumper.endXMLElement(var39, "property");
			}

			List var49;
			String var74;
			if (var63 != null) {
				LinkedHashMap var65 = new LinkedHashMap();
				String[] var68 = var63;
				var45 = var63.length;

				for(int var73 = 0; var73 < var45; ++var73) {
					var47 = var68[var73];
					List var48 = ClientJarManager.getCacheArchiveList(var47);
					var49 = ClientJarManager.getCacheVersionList(var47);
					boolean[] var50 = ClientJarManager.getSignedJarStates(var47);
					int var51 = var48.size();
					boolean var52 = var51 == var49.size();
					boolean var53 = var51 == var50.length;

					for(int var54 = 0; var54 < var51; ++var54) {
						String var55 = (String)var48.get(var54);
						if (var53 && var50[var54] != var20) {
							if (var50[var54]) {
								var31.add(var55);
							} else {
								var29.add(var55);
							}
						} else {
							var65.put(var55, var52 ? (String)var49.get(var54) : null);
						}
					}
				}

				Iterator var69 = var65.entrySet().iterator();

				while(var69.hasNext()) {
					Map.Entry var71 = (Map.Entry)var69.next();
					var46 = (String)var71.getKey();
					var47 = "";
					if (var11) {
						try {
							new URL(var46);
						} catch (MalformedURLException var56) {
							var47 = "protocolAuth/";
						}
					}

					var74 = (String)var71.getValue();
					var40 = new AttributesImpl();
					XMLDumper.addAttribute(var40, "href", var47 + var46);
					if (var74 != null) {
						XMLDumper.addAttribute(var40, "version", var74);
					}

					XMLDumper.startXMLElement(var39, "jar", var40);
					XMLDumper.endXMLElement(var39, "jar");
				}
			}

			var64 = Arrays.asList(var29, var31).iterator();

			while(var64.hasNext()) {
				Collection var70 = (Collection)var64.next();
				boolean var72 = var70 == var31;
				Iterator var75 = var70.iterator();

				while(var75.hasNext()) {
					var47 = (String)var75.next();
					var74 = var47.substring(0, var47.indexOf(46));
					var49 = ClientJarManager.getCacheVersionList(var47);
					String var76 = (String)var49.get(0);
					var40 = new AttributesImpl();
					XMLDumper.addAttribute(var40, "name", var74);
					if (var47.endsWith(".jar")) {
						XMLDumper.addAttribute(var40, "version", var76);
						XMLDumper.addAttribute(var40, "href", var6 + "servlet/JNLPGeneratorServlet/" + var74 + ".jnlp?title=" + var74 + "&version=" + var76 + (var72 ? "&allperm=1&jars=" : "&jars=") + var47 + "&extn=.jnlp");
					} else {
						XMLDumper.addAttribute(var40, "href", var47.substring(var47.indexOf(46) + 1));
					}

					XMLDumper.startXMLElement(var39, "extension", var40);
					XMLDumper.endXMLElement(var39, "extension");
				}
			}

			XMLDumper.endXMLElement(var39, "resources");
			String[] var67;
			if (var19 != null) {
				var40 = new AttributesImpl();
				XMLDumper.addAttribute(var40, "name", var13);
				XMLDumper.addAttribute(var40, "documentbase", var61);
				XMLDumper.addAttribute(var40, "main-class", var19);
				XMLDumper.addAttribute(var40, "width", Integer.toString(var35));
				XMLDumper.addAttribute(var40, "height", Integer.toString(var36));
				XMLDumper.startXMLElement(var39, "applet-desc", var40);
				if (var32 != null) {
					var67 = var32;
					var44 = var32.length;

					for(var45 = 0; var45 < var44; ++var45) {
						var46 = var67[var45];
						var40 = new AttributesImpl();
						XMLDumper.addAttribute(var40, "name", var46.substring(0, var46.indexOf(61)));
						XMLDumper.addAttribute(var40, "value", var46.substring(var46.indexOf(61) + 1));
						XMLDumper.startXMLElement(var39, "param", var40);
						XMLDumper.endXMLElement(var39, "param");
					}

					if (!var37) {
						if (var8 != null) {
							var40 = new AttributesImpl();
							XMLDumper.addAttribute(var40, "name", "sessionCookie");
							XMLDumper.addAttribute(var40, "value", var22);
							XMLDumper.startXMLElement(var39, "param", var40);
							XMLDumper.endXMLElement(var39, "param");
						}

						if (var60 != null) {
							var40 = new AttributesImpl();
							XMLDumper.addAttribute(var40, "name", "hostname");
							XMLDumper.addAttribute(var40, "value", var60);
							XMLDumper.startXMLElement(var39, "param", var40);
							XMLDumper.endXMLElement(var39, "param");
						}
					}
				}

				XMLDumper.endXMLElement(var39, "applet-desc");
			} else if (var18 != null) {
				var40 = new AttributesImpl();
				XMLDumper.addAttribute(var40, "main-class", var18);
				XMLDumper.startXMLElement(var39, "application-desc", var40);
				if (var32 != null) {
					var67 = var32;
					var44 = var32.length;

					for(var45 = 0; var45 < var44; ++var45) {
						var46 = var67[var45];
						XMLDumper.sendSimpleElemToXML(var39, "argument", var46);
					}
				}

				if (!var37) {
					if (var22 != null) {
						XMLDumper.sendSimpleElemToXML(var39, "argument", "sessionCookie=" + var22);
					}

					if (var60 != null) {
						XMLDumper.sendSimpleElemToXML(var39, "argument", "hostname=" + var60);
					}
				}

				XMLDumper.endXMLElement(var39, "application-desc");
			} else {
				XMLDumper.sendSimpleElemToXML(var39, "component-desc", (String)null);
			}

			XMLDumper.endXMLElement(var39, "jnlp");
		} catch (SAXException var59) {
			throw new ServletException(var59);
		}
	}

	private static String getSafeJnlpFileName(String var0, String var1) {
		boolean var2 = var0 != null && var0.contains("Windows");
		StringBuilder var3 = new StringBuilder(var1);
		int var4 = var3.length();

		for(int var5 = 0; var5 < var4; ++var5) {
			char var6 = var3.charAt(var5);
			switch (var6) {
				case ' ':
				case ';':
					if (var2) {
						break;
					}
				case '"':
				case '*':
				case '/':
				case ':':
				case '<':
				case '>':
				case '?':
				case '\\':
				case '|':
					var3.setCharAt(var5, '_');
			}
		}

		String var7 = var3.toString();
		return var7.endsWith(".jnlp") ? var7 : var7 + ".jnlp";
	}
}