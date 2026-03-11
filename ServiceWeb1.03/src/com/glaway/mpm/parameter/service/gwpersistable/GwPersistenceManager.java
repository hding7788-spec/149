package com.glaway.mpm.parameter.service.gwpersistable;

import java.io.Serializable;

/**
 * 自定义的CmPersistable子类的数据库维护接口<br>
 *
 * @author 龙秀川
 */
public interface GwPersistenceManager extends Serializable {
    /**
     * 数据搜索，返回CmPersistable对象的结果集
     *
     * @param qs
     *            查询规格
     * @return 返回CmPersistable对象的结果集
     * @throws Exception
     * @throws Exception
     */
    public abstract GwQueryResult find(GwQuerySpec qs) throws Exception;

    /**
     * 数据搜索，返回CmPersistable对象
     *
     * @param klass
     *            指定的CmPersistable的具体子类
     * @param keyId
     *            klass对应对象的唯一标识（主键值）
     * @return 返回CmPersistable对象，如果找不到，返回null
     * @throws Exception
     */
    public abstract GwPersistable find(Class<?> klass, Object keyId) throws Exception;

    /**
     * 指定的CmPersistable对象p是否已经存在
     *
     * @param p
     *            CmPersistable子类实例对象
     * @return p存在则返回true，否则返回false
     * @throws Exception
     */
    public abstract boolean isPersistence(GwPersistable p) throws Exception;

    /**
     * 保存CmPersistable对象p，如果p不存在，则在数据库中插入；如果p已经存在，则在数据库中更新
     *
     * @param p
     *            　CmPersistable子类实例对象
     * @throws Exception
     */
    public abstract void save(GwPersistable p) throws Exception;

    /**
     * 在数据库中插入CmPersistable对象p
     *
     * @param p
     *            　CmPersistable子类实例对象
     * @throws Exception
     */
    public abstract void insert(GwPersistable p) throws Exception;

    /**
     * 更新数据库中已经存在的CmPersistable对象p
     *
     * @param p
     *            　CmPersistable子类实例对象
     * @throws Exception
     */
    public abstract void update(GwPersistable p) throws Exception;

    /**
     * 在数据库中删除指定的CmPersistable对象p
     *
     * @param p
     *            　CmPersistable子类实例对象
     * @throws Exception
     */
    public abstract void delete(GwPersistable p) throws Exception;
}
