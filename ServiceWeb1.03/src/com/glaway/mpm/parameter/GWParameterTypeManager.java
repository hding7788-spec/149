package com.glaway.mpm.parameter;

import java.util.ArrayList;
import java.util.List;

import com.glaway.mpm.log.VaLogger;
import com.glaway.mpm.parameter.model.GWParameterType;
import com.glaway.mpm.parameter.model.data.CmParameterType;
import com.glaway.mpm.parameter.service.gwpersistable.GwPersistenceHelper;
import com.glaway.mpm.parameter.service.gwpersistable.GwQueryResult;
import com.glaway.mpm.parameter.service.gwpersistable.GwQuerySpec;
import com.glaway.mpm.util.LoadConfig;

public class GWParameterTypeManager {

	private static VaLogger logger = VaLogger.getLogger(GWParameterTypeManager.class.getName());

	protected static List<CmParameterType> loadParameterType() throws Exception {
		List<CmParameterType> list = new ArrayList<CmParameterType>();

		String[] technicsTypes = LoadConfig.getInstance().getTechnicsType()[1];
		CmParameterType parameterType = null;
		for (String technicsType : technicsTypes) {
			parameterType = convertToCmParameterType(technicsType);
			loadChildParameterType(parameterType);
			list.add(parameterType);
		}

		return list;
	}

	protected static CmParameterType loadParameterType(String technicsType) throws Exception {
		CmParameterType parameterType = convertToCmParameterType(technicsType);
		loadChildParameterType(parameterType);
		return parameterType;
	}

	/**
	 * 循环加载参数类型结构数据
	 *
	 * @param parentType
	 * @throws Exception
	 */
	protected static void loadChildParameterType(CmParameterType parentType) throws Exception {
		GwQueryResult queryResult = queryChildParameterType(parentType.getEnName());
		List<CmParameterType> childParameterTypes = new ArrayList<CmParameterType>();
		GWParameterType childGwParameterType = null;
		CmParameterType childParameterType = null;
		while (queryResult.hasNext()) {
			childGwParameterType = (GWParameterType)queryResult.next();
			childParameterType = convertToCmParameterType(childGwParameterType);
			childParameterTypes.add(childParameterType);

			loadChildParameterType(childParameterType);
		}
		parentType.setChildParameterTypes(childParameterTypes);
	}

	protected static CmParameterType convertToCmParameterType(String technicsType) {
		CmParameterType parameterType = new CmParameterType();
		parameterType.setName(technicsType);
		parameterType.setEnName(technicsType);
		parameterType.setTechnicsType(technicsType);
		return parameterType;
	}

	protected static CmParameterType convertToCmParameterType(GWParameterType gwParameterType) {
		CmParameterType parameterType = new CmParameterType();
		parameterType.setName(gwParameterType.getChinaname());
		parameterType.setEnName(gwParameterType.getEnname());
		parameterType.setNumber(gwParameterType.getNumber());
		parameterType.setOid(Long.valueOf(gwParameterType.getGwKey()));
		parameterType.setParent(gwParameterType.getParent());
		parameterType.setTechnicsType(gwParameterType.getTechnicstype());
		parameterType.setValueType(gwParameterType.getValuetype());
		return parameterType;
	}

	protected static GWParameterType convertToGWParameterType(CmParameterType parameterType) {
		GWParameterType gwParameterType = new GWParameterType();
		gwParameterType.setChinaname(parameterType.getName());
		gwParameterType.setEnname(parameterType.getEnName());
		gwParameterType.setNumber(parameterType.getNumber());
		gwParameterType.setGwKey(String.valueOf(parameterType.getOid()));
		gwParameterType.setParent(parameterType.getParent());
		gwParameterType.setTechnicstype(parameterType.getTechnicsType());
		gwParameterType.setValuetype(parameterType.getValueType());
		return gwParameterType;
	}

	/**
	 * 新建参数类型
	 *
	 * @param parameterType
	 * @return
	 */
	protected static CmParameterType createParameterType(CmParameterType parameterType) {
		if (parameterType != null) {
			GWParameterType gwParameterType = new GWParameterType();
			gwParameterType.setNumber(parameterType.getNumber());
			gwParameterType.setEnname(parameterType.getEnName());
			gwParameterType.setChinaname(parameterType.getName());
			gwParameterType.setParent(parameterType.getParent());
			gwParameterType.setTechnicstype(parameterType.getTechnicsType());
			gwParameterType.setValuetype(parameterType.getValueType());

			try {
				GwPersistenceHelper.manager.save(gwParameterType);

				gwParameterType = queryParameterType(parameterType.getEnName());
				if (gwParameterType != null) {
					parameterType.setOid(Long.valueOf(gwParameterType.getGwKey()));
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return parameterType;
	}

	/**
	 * 保存修改后的参数类型
	 *
	 * @param list
	 * @throws Exception
	 */
	protected static CmParameterType saveParameterType(CmParameterType parameterType) {
		GWParameterType gwParameterType = null;
		try {
			gwParameterType = queryParameterType(parameterType.getEnName());
			gwParameterType.setChinaname(parameterType.getName());
			gwParameterType.setValuetype(parameterType.getValueType());
			GwPersistenceHelper.manager.save(gwParameterType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parameterType;
	}

	/**
	 * 通过参数类型名称查询参数类型对象
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static GWParameterType queryParameterType(String enName) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWParameterType.class);
		qs.appendWhere(GWParameterType.ENNAME, GwQuerySpec.EQUAL, enName);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		GWParameterType gwParameterType = null;
		if (qr.hasNext()) {
			gwParameterType = (GWParameterType) qr.next();
		}
		return gwParameterType;
	}

	/**
	 * 通过参数父类型类型名称查询参数类型对象
	 *
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected static List<GWParameterType> queryParameterTypeByParent(String parentEnName) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWParameterType.class);
		qs.appendWhere(GWParameterType.PARENT, GwQuerySpec.EQUAL, parentEnName);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		List<GWParameterType> list = new ArrayList<GWParameterType>();
		while (qr.hasNext()) {
			GWParameterType gwParameterType = (GWParameterType) qr.next();
			list.add(gwParameterType);
		}
		return list;
	}

	protected static GwQueryResult queryChildParameterType(String parent) throws Exception {
		GwQuerySpec qs = new GwQuerySpec(GWParameterType.class);
		qs.appendWhere(GWParameterType.PARENT, GwQuerySpec.EQUAL, parent);
		GwQueryResult qr = GwPersistenceHelper.manager.find(qs);
		return qr;
	}

	public static void deleteParameterType(CmParameterType parameterType) {
		try {
			List<GWParameterType> childList = queryParameterTypeByParent(parameterType.getEnName());
			if (!childList.isEmpty()) {
				for (GWParameterType childParameterType : childList) {
					GwPersistenceHelper.manager.delete(childParameterType);
				}
			}

			GWParameterType gwParameterType = queryParameterType(parameterType.getEnName());
			GwPersistenceHelper.manager.delete(gwParameterType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
