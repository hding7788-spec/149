/**
 * Created on 2008-8-10
 * @author Dennis Huang
 */
package ext.sast.common.fc;

/**
 * 自定义的CmPersistable子类的数据库维护辅助类<br>
 * Created on 2008-8-10
 *
 * @author Dennis Huang
 */
public class CmPersistenceHelper {
    private static final String CLASSNAME = CmPersistenceHelper.class.getName();

    public static final CmPersistenceManager manager = new CmPersistenceManagerFwd();

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
    public static String getTableName(Class klass) {
        if (klass == null)
            throw new NullPointerException(CLASSNAME + ".getTableName(Class klass)中klass不能为空");
        return getTableName(klass.getName());
    }

    /**
     * 获取指定类在数据库中的表名
     *
     * @param className
     * @return 数据库表名
     */
    public static String getTableName(String className) {
        if (className == null)
            throw new NullPointerException(CLASSNAME + ".getTableName(String className)中className不能为空");

        int idx = className.lastIndexOf('.');
        return idx <= 0 ? className : className.substring(++idx);
    }
}
