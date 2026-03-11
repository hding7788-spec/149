/**
 * Created on 2008-8-10
 * @author Dennis Huang
 */
package ext.sast.common.fc;

import ext.sast.common.util.too.Debug;
import wt.method.MethodContext;
import wt.pom.WTConnection;

import java.sql.*;
import java.util.Map;

/**
 * 自定义的CmPersistable子类的数据库维护的具体实现管理器<br>
 * Created on 2008-8-10
 *
 * @author Dennis Huang
 */
@SuppressWarnings("unchecked")
public final class CmStandardPersistenceManager implements CmPersistenceManager {
    private static final long serialVersionUID = -1314012247912103782L;

    private boolean SQL_VERBOSE = true;

    /**
     * 数据搜索，返回CmPersistable对象的结果集
     *
     * @param qs
     *            查询规格
     * @return 返回CmPersistable对象的结果集
     * @throws Exception
     */
    public CmQueryResult find(CmQuerySpec qs) throws Exception {
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(qs.toString());
        try {
            ResultSet rs = stmt.executeQuery();
            CmQueryResult qr = new CmQueryResult();
            while (rs.next()) {
                CmPersistable p = (CmPersistable) qs.getSelectClass().newInstance();
                qr.append(p.getObject(rs));
            }
            return qr;
        } finally {
            stmt.close();
        }
    }

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
    public CmPersistable find(Class klass, Object keyId) throws Exception {
        checkKeyId(keyId);

        CmQuerySpec qs = new CmQuerySpec(klass);
        qs.appendWhere(CmPersistable.KEY_ID, CmQuerySpec.EQUAL, keyId);

        CmQueryResult qr = find(qs);

        return qr.hasNext() ? (CmPersistable) qr.next() : null;
    }

    /**
     * 指定的CmPersistable对象p是否已经存在
     *
     * @param p
     *            CmPersistable子类实例对象
     * @return p存在则返回true，否则返回false
     * @throws Exception
     */
    public boolean isPersistence(CmPersistable p) throws Exception {
        if (p == null)
            return false;

        return find(p.getClass(), p.getKeyId()) != null;
    }

    /**
     * 保存CmPersistable对象p，如果p不存在，则在数据库中插入；如果p已经存在，则在数据库中更新
     *
     * @param p
     *            　CmPersistable子类实例对象
     * @throws Exception
     */
    public void save(CmPersistable p) throws Exception {
        if (p != null) {
            if (isPersistence(p))
                update(p);
            else
                insert(p);
        }
    }

    public void save(CmPersistable p,boolean needAutoCommit) throws Exception {
        if (p != null) {
            if (isPersistence(p))
                update(p,needAutoCommit);
            else
                insert(p,needAutoCommit);
        }
    }

    /**
     * 在数据库中插入CmPersistable对象p
     *
     * @param p
     *            　CmPersistable子类实例对象
     * @throws Exception
     */
    public void insert(CmPersistable p) throws Exception {
        insert(p,true);
    }

    public void insert(CmPersistable p,boolean needAutoCommit) throws Exception {
        if (p != null) {
            Object keyId = p.getKeyId();
            checkKeyId(keyId);

            Map paramsMap = p.getCreateMap();
//            paramsMap.put(CmPersistable.KEY_ID, p.getKeyId());
            String[] fieldNames = (String[]) paramsMap.keySet().toArray(new String[0]);

            StringBuffer sql = new StringBuffer();
            sql.append("INSERT INTO ").append(CmPersistenceHelper.getTableName(p.getClass())).append("(");
            for (int i = fieldNames.length - 1; i >= 0; i--)
                sql.append(fieldNames[i]).append(i == 0 ? ')' : ',');
            sql.append(" VALUES (");
            for (int i = fieldNames.length - 1; i >= 0; i--)
                sql.append(i == 0 ? " ?)" : " ?,");

            if (SQL_VERBOSE)
                Debug.P("insert sql>>>", sql);

            Connection conn = getConnection();
            boolean autoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(needAutoCommit);
                PreparedStatement stmt = conn.prepareStatement(sql.toString());
                int idx = 1;
                for (int i = fieldNames.length - 1; i >= 0; i--) {
                    Object value = paramsMap.get(fieldNames[i]);
                    if(value == null){
                        stmt.setNull(idx++, Types.VARCHAR);
                    }else if (value instanceof String) {
                        stmt.setObject(idx++, paramsMap.get(fieldNames[i]));
                    } else if (value instanceof Timestamp) {
                        stmt.setTimestamp(idx++, (Timestamp)value);
                    }else if (value instanceof Long) {
                        stmt.setLong(idx++, (Long)value);
                    } else if(value instanceof Integer) {
                        stmt.setInt(idx++, (Integer)value);
                    }
                }


                int updateCount = stmt.executeUpdate();
                stmt.close();
                if (SQL_VERBOSE)
                    Debug.P(">>>insert ", updateCount, " record.");
            } finally {
                conn.setAutoCommit(autoCommit);
            }
        }
    }

    /**
     * 更新数据库中已经存在的CmPersistable对象p
     *
     * @param p
     *            　CmPersistable子类实例对象
     * @throws Exception
     */
    public void update(CmPersistable p) throws Exception {
       update(p,true);
    }

    public void update(CmPersistable p,boolean needAutoCommit) throws Exception {
        if (p != null) {
            Object keyId = p.getKeyId();
            checkKeyId(keyId);

            Map paramsMap = p.getUpdateMap();
            paramsMap.remove(CmPersistable.KEY_ID);
            String[] fieldNames = (String[]) paramsMap.keySet().toArray(new String[0]);

            StringBuffer sql = new StringBuffer();
            sql.append("UPDATE ").append(CmPersistenceHelper.getTableName(p.getClass())).append(" SET ");
            for (int i = fieldNames.length - 1; i >= 0; i--)
                sql.append(fieldNames[i]).append(i == 0 ? "=? " : "=?,");
            sql.append("WHERE ").append(CmPersistable.KEY_ID).append("=?");

            if (SQL_VERBOSE)
                Debug.P("update sql>>>", sql);

            Connection conn = getConnection();
            boolean autoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(needAutoCommit);

                PreparedStatement stmt = conn.prepareStatement(sql.toString());

                int idx = 1;
                for (int i = fieldNames.length - 1; i >= 0; i--)
                    stmt.setObject(idx++, paramsMap.get(fieldNames[i]));
                stmt.setObject(idx++, keyId);

                int updateCount = stmt.executeUpdate();
                stmt.close();
                if (SQL_VERBOSE)
                    Debug.P(">>>update ", updateCount, " record.");
            } finally {
                conn.setAutoCommit(autoCommit);
            }
        }
    }

    /**
     * 在数据库中删除指定的CmPersistable对象p
     *
     * @param p
     *            　CmPersistable子类实例对象
     * @throws Exception
     */
    public void delete(CmPersistable p) throws Exception {
        delete(p,true);
    }

    public void delete(CmPersistable p,boolean needAutoCommit) throws Exception {
        if (p != null) {
            Object keyId = p.getKeyId();
            checkKeyId(keyId);

            StringBuffer sql = new StringBuffer();
            sql.append("DELETE FROM ").append(CmPersistenceHelper.getTableName(p.getClass()));
            sql.append(" WHERE ").append(CmPersistable.KEY_ID).append("=?");

            if (SQL_VERBOSE)
                Debug.P("delete sql>>>", sql);

            Connection conn = getConnection();
            boolean autoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(needAutoCommit);

                PreparedStatement stmt = conn.prepareStatement(sql.toString());
                stmt.setObject(1, keyId);

                int updateCount = stmt.executeUpdate();
                stmt.close();
                if (SQL_VERBOSE)
                    Debug.P(">>>delete ", updateCount, " record.");
            } finally {
                conn.setAutoCommit(autoCommit);
            }
        }
    }

    /**
     * 检查指定的CmPersistable的主键值keyId是否满足条件<br>
     * <ul>
     * <li>keyId不为空
     * <li>keyId或者为String类型，或者为Long类型，目前还不支持其它的数据类型做为主键
     * </ul>
     *
     * @param keyId
     *            CmPersistable具体实例对象的主键值
     * @throws Exception
     */
    private void checkKeyId(Object keyId) throws Exception {
        if (keyId == null || !(keyId instanceof String || keyId instanceof Long))
            throw new Exception("可持续性对象KeyId不能为空，且必须为String或Long类型: keyId.class="
                    + (keyId == null ? "(null)" : keyId.getClass().getName()));
    }

    /**
     * 获取数据库Connection
     *
     * @return 数据库Connection
     * @throws Exception
     */
    private Connection getConnection() throws Exception {
        MethodContext mc = MethodContext.getContext();
        Connection conn = ((WTConnection) mc.getConnection()).getConnection();
        return conn;
    }
}
