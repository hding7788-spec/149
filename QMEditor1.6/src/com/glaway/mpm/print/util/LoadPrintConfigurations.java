package com.glaway.mpm.print.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.print.ui.MPMPrintFileFrame;
import com.glaway.mpm.util.CommonUtil;

public class LoadPrintConfigurations {

	private static final String FILE = "com/glaway/mpm/print/config/conditionConfigurations.properties";
	private static LoadPrintConfigurations instance = null;
	private static final VaLogger log = VaLogger.getLogger(LoadPrintConfigurations.class);
	public synchronized static LoadPrintConfigurations getInstance() {
		instance = new LoadPrintConfigurations();
		instance.loadConfig();
		return instance;
	}

	private  Properties properties = new  Properties();

	public LoadPrintConfigurations() {
	}

	public String[] getUnitTypeSeal() {
		String[] array = null;
		String value = this.properties.getProperty("unitTypeSeal");
		array = value.split(",");
		return array;
	}
	public String[] getFileTypeSeal() {
		String[] array = null;
		String value = this.properties.getProperty("fileTypeSeal");
		array = value.split(",");
		return array;
	}
	public String[] getDistributeSeal() {
		String[] array = null;
		String value = this.properties.getProperty("distributeSeal");
		array = value.split(",");
		return array;
	}
	//文件类别
	public String[] getFileTypeValue() {
		String[] array = null;
		String value = this.properties.getProperty("fileType");
		array = value.split(",");
		return array;
	}

	//部门列表
	public String[] getDeptValue() {
		String[] array = null;
		String value = this.properties.getProperty("department_new");
		array = value.split(",");
		return array;
	}

	//打印申请文件类型
	public String[] getPrintTypeValue() {
		String[] array = null;
		String value = this.properties.getProperty("printType");
		// 针对外来文件
		log.debug("category==>>" + MPMPrintFileFrame.getCategory());
		log.debug("type==>>" + MPMPrintFileFrame.getTypeStr());
		if("WL".equals(MPMPrintFileFrame.getCategory())
				|| FilePrintUtil.getPrintType("WLWJLR").equals(MPMPrintFileFrame.getTypeStr())) {
			value = this.properties.getProperty("printType_wl");
		}
		array = value.split(",");
		return array;
	}

	//文件类型
	public String[] getPrintFileTypeValue() {
		String[] array = null;
		String value = this.properties.getProperty("printFileType");
		// 针对外来文件
		log.debug("category==>>" + MPMPrintFileFrame.getCategory());
		log.debug("type==>>" + MPMPrintFileFrame.getTypeStr());
		if("WL".equals(MPMPrintFileFrame.getCategory())
				|| FilePrintUtil.getPrintType("WLWJLR").equals(MPMPrintFileFrame.getTypeStr()) || FilePrintUtil.getPrintType("WJBDSQ").equals(MPMPrintFileFrame.getTypeStr()) ||
				FilePrintUtil.getPrintType("WJCXDY").equals(MPMPrintFileFrame.getTypeStr()) || FilePrintUtil.getPrintType("DYFFJLCX").equals(MPMPrintFileFrame.getTypeStr())) {
			value = this.properties.getProperty("printFileType_wl");
		}
		array = value.split(",");
		return array;
	}

	//封存状态
	public String[] getStorageFileTypeValue() {
		String[] array = null;
		String value = this.properties.getProperty("storageType");
		array = value.split(",");
		return array;
	}

	//阶段标记
	public String[] getPhaseCodeValue() {
		String[] array = null;
		String value = this.properties.getProperty("phaseCode");
		array = value.split(",");
		return array;
	}

	//文件状态
	public String[] getFileStateValue() {
		String[] array = null;
		String value = this.properties.getProperty("fileState");
		array = value.split(",");
		return array;
	}

	//打印状态
	public String[] getPrintStateValue() {
		String[] array = null;
		String value = this.properties.getProperty("printState");
		array = value.split(",");
		return array;
	}

	//其他报告
	public String[] getQTBGValue() {
		String[] array = null;
		String value = this.properties.getProperty("QTBG");
		array = value.split(",");
		return array;
	}
	public Map<String, String> getPrintType() {
		Map<String, String> map = new HashMap<String, String>();
		Enumeration<?> enums = this.properties.keys();
		while (enums.hasMoreElements()) {
			String key = CommonUtil.objectToString(enums.nextElement());
			String value = this.properties.getProperty(key);
			map.put(key, value);
		}
		return map;
	}

	//临时章
	public String[] getTemporarySealValue() {
		String[] array = null;
		String value = this.properties.getProperty("temporarySeal");
		array = value.split(",");
		return array;
	}

	private void loadConfig() {
		InputStream in = null;
		try {
			in = LoadPrintConfigurations.class.getClassLoader().getResourceAsStream(FILE);
			properties.load(in);
		} catch (IOException e) {
			log.error("导入配置文件：conditionConfigurations.properties异常", e);
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	//文件类别
	public String[] getTechnics_Type_Desc() {
		String[] array = null;
		String value = this.properties.getProperty("technics_type_desc");
		array = value.split(",");
		return array;
	}

	//外来单位
	public String[] getOutDeptValue() {
		String[] array = null;
		String value = this.properties.getProperty("outDept");
		array = value.split(",");
		return array;
	}

	//产品库
	public String[] getContainerValue() {
		String[] array = null;
		String value = this.properties.getProperty("container");
		array = value.split(",");
		return array;
	}

}
