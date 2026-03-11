package ext.ases.techMaterial.gwpersistable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


import wt.method.MethodContext;
import wt.pom.WTConnection;
import wt.session.SessionServerHelper;

/**
 * 自定义的GwPersistable子类的数据库维护的具体实现管理器<br>
 * 
 * @author Mchen
 */
@SuppressWarnings( { "unchecked" })
public final class GwStandardPersistenceManager implements GwPersistenceManager {
	private static final long serialVersionUID = -1314012247912103782L;

	private static final boolean SQL_VERBOSE = false;

	/**
	 * 数据搜索，返回CmPersistable对象的结果集
	 * 
	 * @param qs
	 *            查询规格
	 * @return 返回CmPersistable对象的结果集
	 * @throws Exception
	 */
	@Override
	public GwQueryResult find(GwQuerySpec qs) throws Exception {
		Connection conn = getConnection();
		ResultSet rs = null;
		PreparedStatement stmt = conn.prepareStatement(qs.toString());
		try {
			rs = stmt.executeQuery();
			GwQueryResult qr = new GwQueryResult();
			while (rs.next()) {
				GwPersistable p = (GwPersistable) qs.getSelectClass().newInstance();
				qr.append(p.getObject(rs));
			}
			return qr;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (rs != null) {
				rs.close();
			}

			// if (conn != null && !conn.isClosed()) {
			// conn.close();
			// }
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
	@Override
	public GwPersistable find(Class<?> klass, Object keyId) throws Exception {
		checkKeyId(keyId);

		GwQuerySpec qs = new GwQuerySpec(klass);
		qs.appendWhere(GwPersistable.KEY_ID, GwQuerySpec.EQUAL, keyId);

		GwQueryResult qr = find(qs);

		return qr.hasNext() ? (GwPersistable) qr.next() : null;
	}

	/**
	 * 指定的CmPersistable对象p是否已经存在
	 * 
	 * @param p
	 *            CmPersistable子类实例对象
	 * @return p存在则返回true，否则返回false
	 * @throws Exception
	 */
	@Override
	public boolean isPersistence(GwPersistable p) throws Exception {
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
	@Override
	public void save(GwPersistable p) throws Exception {
		if (p != null) {
			if (isPersistence(p))
				update(p);
			else
				insert(p);
		}
	}

	/**
	 * 在数据库中插入CmPersistable对象p
	 * 
	 * @param p
	 *            　CmPersistable子类实例对象
	 * @throws Exception
	 */
	@Override
	public void insert(GwPersistable p) throws Exception {
		if (p != null) {
			Object keyId = p.getKeyId();
			checkKeyId(keyId);
			
			Map paramsMap = p.getCreateMap();
			paramsMap.put(GwPersistable.KEY_ID, p.getKeyId());
			String[] fieldNames = (String[]) paramsMap.keySet().toArray(new String[0]);

			StringBuffer sql = new StringBuffer();
			sql.append("INSERT INTO ").append(GwPersistenceHelper.getTableName(p.getClass())).append("(");
			for (int i = fieldNames.length - 1; i >= 0; i--)
				sql.append(fieldNames[i]).append(i == 0 ? ')' : ',');
			sql.append(" VALUES (");
			for (int i = fieldNames.length - 1; i >= 0; i--)
				sql.append(i == 0 ? " ?)" : " ?,");

			if (SQL_VERBOSE)
				System.out.println("insert sql>>>" + sql);
			
			PreparedStatement pstmt = null;
			WTConnection wtConnection = null;
			Connection conn = null;
			try {
				SessionServerHelper.manager.setAccessEnforced(false);
				MethodContext methodContext = MethodContext.getContext();
				wtConnection = (WTConnection) methodContext.getConnection();
				conn = wtConnection.getConnection();
				conn.setAutoCommit(false);
				pstmt = conn.prepareStatement(sql.toString());
				
				int idx = 1;
				for (int i = fieldNames.length - 1; i >= 0; i--){
					Object obj = paramsMap.get(fieldNames[i]);
					pstmt.setObject(idx++, obj);
				}
				pstmt.execute();
				conn.commit();
				conn = null;
				wtConnection = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SessionServerHelper.manager.setAccessEnforced(true);
			}finally{
				try {
					if(pstmt != null){
						pstmt.close();
					}
					if(wtConnection != null){
						wtConnection.releaseAll();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SessionServerHelper.manager.setAccessEnforced(true);
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
	@Override
	public void update(GwPersistable p) throws Exception {
		if (p != null) {
			Object keyId = p.getKeyId();
			checkKeyId(keyId);

			Map paramsMap = p.getUpdateMap();
			paramsMap.remove(GwPersistable.KEY_ID);
			String[] fieldNames = (String[]) paramsMap.keySet().toArray(new String[0]);

			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE ").append(GwPersistenceHelper.getTableName(p.getClass())).append(" SET ");
			for (int i = fieldNames.length - 1; i >= 0; i--)
				sql.append(fieldNames[i]).append(i == 0 ? "=? " : "=?,");
			
			sql.append("WHERE ").append(GwPersistable.KEY_ID).append("=?");

			
			PreparedStatement pstmt = null;
			WTConnection wtConnection = null;
			Connection conn = null;
			try {
				SessionServerHelper.manager.setAccessEnforced(false);
				MethodContext methodContext = MethodContext.getContext();
				wtConnection = (WTConnection) methodContext.getConnection();
				conn = wtConnection.getConnection();
				conn.setAutoCommit(false);
				pstmt = conn.prepareStatement(sql.toString());
				
				int idx = 1;
				for (int i = fieldNames.length - 1; i >= 0; i--){
					Object o = paramsMap.get(fieldNames[i]);
					pstmt.setObject(idx++, o);
				}
				pstmt.setObject(idx++, keyId);
				pstmt.executeUpdate();
				conn.commit();
				conn = null;
				wtConnection = null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				SessionServerHelper.manager.setAccessEnforced(true);
			}finally{
				try {
					if(pstmt != null){
						pstmt.close();
					}
					if(wtConnection != null){
						wtConnection.releaseAll();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SessionServerHelper.manager.setAccessEnforced(true);
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
	@Override
	public void delete(GwPersistable p) throws Exception {
		if (p != null) {
			Object keyId = p.getKeyId();
			checkKeyId(keyId);

			StringBuffer sql = new StringBuffer();
			sql.append("DELETE FROM ").append(GwPersistenceHelper.getTableName(p.getClass()));
			sql.append(" WHERE ").append(GwPersistable.KEY_ID).append("=?");

			if (SQL_VERBOSE)
			System.out.println("delete sql>>>" + sql);

			deleteBySql(sql.toString(), keyId);
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
		if (keyId == null || !(keyId instanceof String || keyId instanceof Long)) {
			
		}
	}
	
	private Connection getConnection() throws Exception {
		MethodContext mc = MethodContext.getContext();
		Connection conn = ((WTConnection) mc.getConnection()).getConnection();
		return conn;
	}
	
	public void deleteBySql(String sql, Object keyId){
		PreparedStatement pstmt = null;
		WTConnection wtConnection = null;
		Connection conn = null;
		try {
			SessionServerHelper.manager.setAccessEnforced(false);
			MethodContext methodContext = MethodContext.getContext();
			wtConnection = (WTConnection) methodContext.getConnection();
			conn = wtConnection.getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.setObject(1, keyId);
			pstmt.execute();
			conn.commit();
			conn = null;
			wtConnection = null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			try {
				if(pstmt != null){
					pstmt.close();
				}
				if(wtConnection != null){
					wtConnection.releaseAll();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SessionServerHelper.manager.setAccessEnforced(true);
		}
	}
	
}
