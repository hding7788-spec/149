package com.glaway.mpm.parameter.service.gwpersistable;

import org.apache.log4j.Logger;

/**
 * 自定义的CmPersistable子类的数据库维护辅助类<br>
 *
 * @author 龙秀川
 */
public class GwPersistenceHelper {
    private static final String CLASSNAME = GwPersistenceHelper.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);
    public static GwPersistenceManager manager = new GwPersistenceManagerFwd();

    private static final String ID_PREFIX = String.valueOf(System.currentTimeMillis()) + "-";

    private static long id = 0;

    /**
     * 获取系统唯一值
     *
     * @return
     */
    public synchronized static String genUniquedId() {
        return ID_PREFIX + String.valueOf(id++);
    }

    /**
     * 获取指定类在数据库中的表名
     *
     * @param klass
     *            指定的类对象
     * @return 数据库表名
     */
    public static String getTableName(Class<?> klass) {
        if (klass == null) {
        	LOGGER.error(CLASSNAME + ".getTableName(Class klass)中klass不能为空");
        }
        return getTableName(klass.getName());
    }

    /**
     * 获取指定类在数据库中的表名
     *
     * @param className
     * @return 数据库表名
     */
    public static String getTableName(String className) {
        if (className == null) {
        	LOGGER.error(CLASSNAME + ".getTableName(String className)中className不能为空");
        }

        int idx = className.lastIndexOf('.');
        return idx <= 0 ? className : className.substring(++idx);
    }
}
