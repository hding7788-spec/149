package com.glaway.mpm.qmIntf.material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JTable;

import com.glaway.mpm.model.MaterialCal;
import com.glaway.mpm.util.CommonUtil;
import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.visual.log.VaLogger;
import com.glaway.mpm.wcIntf.ResourceIntf;

public class MaterialUtil {
	String format = "^[0-9]+([.]{0,1}[0-9]{1,2})?$";

	private static VaLogger logger = VaLogger.getLogger(MaterialUtil.class);

	private static String SEPERATOR = "×";

	/**
	 * @Description: 计算材料定额
	 */
	public static Map<String, String> calQuota(Map<String, String> map) {
		MaterialCal materialCal = getMaterialCalByNumber(map
				.get("materialNumber"));
		if (materialCal == null) {
			logger.debug("materialCal= " + materialCal);
			SwingUtil.showMessageDialog("材料定额计算条件不足！", "提示", 2);
			return null;
		}

		// 损耗系数
		String materialQuotiety = map.get("attritionRate");
		materialQuotiety = CommonUtil.trimAllSpace(materialQuotiety);
		Double quotiety = getMaterialQuotiety(materialQuotiety, materialCal);
		if (quotiety == null) {
			logger.debug("materialQuotiety= " + materialQuotiety);
			SwingUtil.showMessageDialog("材料损耗系数不正确！", "提示", 2);
			return null;
		}
		logger.debug("材料定额系数====" + quotiety);

		// 密度
		String materialDensity = map.get("materialDensity");
		materialDensity = CommonUtil.trimAllSpace(materialDensity);
		Double density = getMaterialDensity(materialDensity, materialCal);
		if (density == null) {
			logger.debug("materialDensity= " + materialDensity);
			SwingUtil.showMessageDialog("材料密度不正确！", "提示", 2);
			return null;
		}
		logger.debug("密度====" + density);

		// 填写的单件毛坯尺寸
		String materialSpec = materialCal.getSingleSize();
		if (materialSpec == null
				|| (materialSpec = materialSpec.trim()).length() == 0) {
			logger.debug("singleSize()= " + materialCal.getSingleSize());
			SwingUtil.showMessageDialog("单件毛坯尺寸不正确！", "提示", 2);
			return null;
		}
		materialSpec = CommonUtil.trimAllSpace(materialSpec);
		logger.debug("规格====" + materialSpec);

		// 参数数组
		String[] param;
		if (materialSpec.indexOf("=") != -1) {
			param = new String[] { "L" };
		} else {
			materialSpec = CommonUtil.trimAllSpace(materialSpec);
			materialSpec = materialSpec.substring(1).trim();
			materialSpec = CommonUtil.trimAllSpace(materialSpec);
			param = materialSpec.split(SEPERATOR);
		}

		// 生成参数Map
		String singleSize = CommonUtil.trimAllSpace((map.get("singleSize")));
		Map<String, Double> params = getParams(singleSize, param);
		if (params == null) {
			logger.debug("params= " + params);
			SwingUtil.showMessageDialog("材料定额计算条件不足！", "提示", 2);
			return null;
		}

		// 公式
		String formula = materialCal.getFormula();
		if (formula == null || formula.length() == 0) {
			logger.debug("formula= " + formula);
			SwingUtil.showMessageDialog("单件毛坯尺寸不正确！", "提示", 2);
			return null;
		}
		logger.debug("公式====" + formula);
		formula = CommonUtil.trimAllSpace(formula);
		// 计算
		formula = formula.replace("ρ", density + "");
		formula = formula.replace("β", quotiety + "");
		String quota = calQuota(formula, params);

		Map<String, String> returnMap = new HashMap<String, String>();
		String unit = map.get("materialUnit");
		if (unit == null || unit.length() == 0) {
			unit = materialCal.getMaterialUnit();
		}
		returnMap.put("unit", unit);
		returnMap.put("quota", quota);
		return returnMap;

	}

	/**
	 *
	 * @Description:获取参数的Map
	 * @param @param singleSize
	 * @param @param param
	 * @param @return
	 * @return Map<String,Double>
	 */
	private static Map<String, Double> getParams(String singleSize,
			String[] param) {
		Map<String, Double> map = new HashMap<String, Double>();
		try {
			if (singleSize.indexOf("=") != -1) {
				String[] params = singleSize.split("=");
				if (params.length != 2) {
					return null;
				}
				map.put(params[0], new Double(params[1]));
			} else {
				singleSize = singleSize.substring(1);
				singleSize = CommonUtil.trimAllSpace(singleSize);
				String[] params = singleSize.split("X");
				if (params.length != param.length) {
					return null;
				}
				for (int i = 0; i < params.length; i++) {
					map.put(CommonUtil.trimAllSpace(param[i]), new Double(
							CommonUtil.trimAllSpace(params[i])));
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 *
	 * @Description: 计算材料定额
	 */
	private static String calQuota(String formula, Map<String, Double> map) {
		Set<Entry<String, Double>> set = map.entrySet();
		for (Entry<String, Double> entry : set) {
			String key = entry.getKey();
			String value = entry.getValue() + "";
			formula = formula.replaceAll(key, value);
		}
		logger.debug("计算===" + formula);
		ScriptEngineManager manager = new ScriptEngineManager();

		ScriptEngine engine = manager.getEngineByName("javascript");
		Double result = null;
		try {
			result = (Double) engine.eval(formula);
		} catch (ScriptException ex) {
			ex.printStackTrace();
		}

		return resultFormat(result);
	}

	/**
	 * 定额系数
	 */
	private static Double getMaterialQuotiety(String materialQuotiety,
			MaterialCal materialCal) {
		if (materialQuotiety == null || materialQuotiety.length() == 0) {
			materialQuotiety = materialCal.getAttritionRate();
		}
		double quotiety = 0;
		try {
			boolean flag = false;
			if (materialQuotiety.endsWith("%")) {
				flag = true;
				materialQuotiety = materialQuotiety.substring(0,
						materialQuotiety.length() - 1);
			}
			quotiety = new Double(materialQuotiety);
			if (flag) {
				quotiety = quotiety / 100;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("定额系数====" + materialQuotiety);
			return null;
		}
		return quotiety;
	}

	/**
	 * 密度
	 */
	private static Double getMaterialDensity(String materialDensity,
			MaterialCal materialCal) {
		if (materialDensity == null || materialDensity.length() == 0) {
			materialDensity = materialCal.getMaterialDensity();
		}
		double density = 0;
		try {
			materialDensity = CommonUtil.filterUnit(materialDensity);
			density = new Double(materialDensity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("密度====" + materialDensity);
			return null;
		}
		return density;
	}

	/**
	 * @Description: format
	 * @param @param quota
	 * @param @return
	 * @return String
	 */
	private static String resultFormat(double quota) {
		String returnValue = String.format("%.4f", quota);
		if ("0.0000".equals(returnValue)) {
			returnValue = "0";
		}
		return returnValue;
	}

	/**
	 * @Description: 根据物资编码获取toolTip
	 * @param @param materialNumber
	 * @param @return
	 * @return String
	 */
	public static String getToolTipByNumber(String materialNumber) {
		MaterialCal materialCal = getMaterialCalByNumber(materialNumber);
		if (materialCal != null) {
			return materialCal.getSingleSize();
		}
		return null;
	}

	/**
	 * @Description: 根据物资编码获取MaterialCal
	 * @param @param materialNumber
	 * @param @return
	 * @return String
	 */
	private static MaterialCal getMaterialCalByNumber(String materialNumber) {
		List<MaterialCal> materialCals = ResourceIntf.getMaterialCals();
		MaterialCal materialCal = null;
		if (materialCals != null) {
			// 获取最长的编号长度
			int maxLength = 0;
			for (MaterialCal temp : materialCals) {
				String number = temp.getNumber();
				if (number != null) {
					int length = number.length();
					if (maxLength < length) {
						maxLength = length;
					}
				}
			}
			// 从最长的开始匹配
			if (maxLength != 0) {
				flag: for (int i = maxLength; i > 0; i--) {
					for (MaterialCal temp : materialCals) {
						if (materialNumber.length() < i) {
							continue;
						}
						if (temp.getNumber().equals(
								materialNumber.substring(0, i - 1) + "*")) {
							materialCal = temp;
							break flag;
						}
					}
				}
			}

		}
		logger.debug("materialCal= " + materialCal);
		return materialCal;
	}

	public static Map<String, String> getOneRowMap(int row, JTable table) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("materialNumber", table.getValueAt(row, 0).toString());
		map.put("materialName", table.getValueAt(row, 1).toString());
//		map.put("materialBrand", table.getValueAt(row, 2).toString());
//		map.put("materialCrision", table.getValueAt(row, 3).toString());
//		map.put("materialCategory", table.getValueAt(row, 4).toString());
//		map.put("attritionRate", table.getValueAt(row, 5).toString());
//		map.put("materialDensity", table.getValueAt(row, 6).toString());
//		map.put("useSize", table.getValueAt(row, 7).toString());
//		map.put("singleSize", table.getValueAt(row, 8).toString());
//		map.put("singleCount", table.getValueAt(row, 9).toString());
//		map.put("materialQuota", table.getValueAt(row, 10).toString());
//		map.put("materialUnit", table.getValueAt(row, 11).toString());
//		map.put("oid", table.getValueAt(row, 12).toString());
//		map.put("number", table.getValueAt(row, 13).toString());
//		map.put("bsoID", table.getValueAt(row, 14).toString());
//		map.put("toolTip", table.getValueAt(row, 15).toString());
//		map.put("materialType", table.getValueAt(row, 16).toString());
//		map.put("materialSpec", table.getValueAt(row, 17).toString());
		//TODO 812 材料属性
		return map;
	}
}