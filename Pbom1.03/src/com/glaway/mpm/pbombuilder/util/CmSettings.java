package com.glaway.mpm.pbombuilder.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author ylchen
 * 
 */
public class CmSettings {
	public static final String SECTION_MBOM = "PBOM";
	public static final String SECTION_APPURL = "APPURL";
	public static final String SECTION_AO = "AO";
	public static final String SECTION_EFFECTIVITY = "Effectivity";

	public static final String SECTION_CAD = "CAD";

	public static final String SECTION_SPSBOM = "SPSBOM";

	public static final String SECTION_ViewBom = "viewBom";
	public static final String EDIT_ATTR = "editAttribute";

	private static Map<String, Map> settings = new HashMap<String, Map>();
	private Map<String, String> sectionSettings = null;

	static {
		try {
			loadSettings();
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private CmSettings(Map<String, String> section) {
		if (section == null)
			section = new HashMap<String, String>();
		sectionSettings = section;
	}

	public Map<String, String> getSectionMap() {
		return sectionSettings == null ? new HashMap<String, String>() : sectionSettings;
	}

	public static CmSettings getSection(Map<String, String> sectionSettings) {
		return new CmSettings(sectionSettings);
	}

	@SuppressWarnings("unchecked")
	public static CmSettings getSection(String section) {
		Map<String, String> result = settings.get(section.trim());
		return new CmSettings(result);
	}

	public Set keySet() {
		return sectionSettings.keySet();
	}

	public String get(String key) {
		return (String) sectionSettings.get(key);
	}

	public String get(String key, String defaultVal) {
		String val = (String) sectionSettings.get(key);
		return val == null ? defaultVal : val;
	}

	public int get(String key, int defaultVal) {
		String val = (String) sectionSettings.get(key);
		if (val == null)
			return defaultVal;

		return Integer.parseInt(val);
	}

	public String[] get(String key, String[] defaultVal) {
		String val = (String) sectionSettings.get(key);
		if (val == null)
			return defaultVal;

		return val.split("[,]");
	}

	private static final Set<String> TRUES = new HashSet<String>(Arrays
			.asList(new String[] { "TRUE", "YES", "ON", "1" }));

	public boolean get(String key, boolean defaultVal) {
		String val = (String) sectionSettings.get(key);
		if (val == null)
			return defaultVal;

		return TRUES.contains(val.toUpperCase()) ? true : false;
	}

	private static void loadSettings() throws Exception {
		String path = System.getProperty("user.dir");
		InputStream fis = CmSettings.class.getResourceAsStream("/com/glaway/mpm/conf/jws.ini");
		BufferedReader r = new BufferedReader(new InputStreamReader(fis, "GB18030"));

		String line;
		String section = null;
		Map<String, String> sectMap = null;
		while ((line = r.readLine()) != null) {
			if (line.startsWith("#") || line.startsWith(";"))
				continue;

			if (line.startsWith("[") && line.indexOf("]") > 0) {
				section = line;
				sectMap = new HashMap<String, String>();
				settings.put(section, sectMap);
				settings.put(section.substring(1, line.length() - 1).trim(), sectMap);
				continue;
			}

			if (section == null)
				continue;

			int deliPos = line.indexOf("=");
			if (deliPos < 0)
				continue;
			String key = line.substring(0, deliPos).trim();
			String val = line.substring(deliPos + 1, line.length()).trim();
			if (key.startsWith("\"") && key.endsWith("\""))
				key = key.substring(1, key.length() - 1);
			if (val.startsWith("\"") && val.endsWith("\""))
				val = val.substring(1, val.length() - 1);
			sectMap.put(key, val);
		}
	}
}
