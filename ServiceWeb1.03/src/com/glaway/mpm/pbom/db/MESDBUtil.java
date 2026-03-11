package com.glaway.mpm.pbom.db;

import com.glaway.mpm.util.GLLogger;
import com.glaway.mpm.util.LoadConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collections;
import java.util.List;


public class MESDBUtil {


	protected static ApplicationContext applicationContext;
	static{
		    GLLogger.debug("开始加载spring配置：spring_bean_erp.xml");
			applicationContext =  new ClassPathXmlApplicationContext("classpath:spring_bean_mes.xml");
			GLLogger.debug("完成加载spring配置：spring_bean_erp.xml");
	}

	private JdbcTemplate jdbcTemplate;

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

//	private SimpleJdbcTemplate simpleJdbcTemplate;

	private JdbcTemplate simpleJdbcTemplate;
	private  static MESDBUtil mesDBUtil = null;

	public static void main(String[] args) {
		MESDBUtil.getDBUtil("03");
	}

	/**
	 * 根据数据库类型 获取数据库操作工具
	 * @param dbtype
	 * @return
	 */
	public static MESDBUtil getDBUtil(String dbtype) {

		if(mesDBUtil==null){
			mesDBUtil = new MESDBUtil("03");
		}
		return mesDBUtil;

	}

	private MESDBUtil(){

	}

	private  MESDBUtil(String dbtype) {
		GLLogger.debug("开始设置jdbc模板,数据库为： "+dbtype);
		String dbName[] = LoadConfig.getInstance().getDBBeanName(dbtype);
		GLLogger.debug("beanName=> jdbcTemplate:"+ dbName[0]+" namedParameterJdbcTemplate:"+dbName[1] + " simpleJdbcTemplate:" +dbName[2] );
		jdbcTemplate = (JdbcTemplate)applicationContext.getBean(dbName[0]);
		namedParameterJdbcTemplate =  (NamedParameterJdbcTemplate)applicationContext.getBean(dbName[1]);
		simpleJdbcTemplate = (JdbcTemplate) applicationContext.getBean(dbName[2]);
		GLLogger.debug("完成设置jdbc模板,数据库为： "+dbtype);
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}


	public List queryForList(String sql, Object[] params, Class clazz) {
		try {
			// 使用新API并处理null参数
			return jdbcTemplate.query(sql, params != null ? params : new Object[0], BeanPropertyRowMapper.newInstance(clazz));
		} catch (EmptyResultDataAccessException e) {
			return Collections.emptyList(); // 空结果返回空集合
		}
	}

	public Object queryForObject(String sql, Object[] params, Class clazz) {
		List objects = queryForList(sql, params, clazz);
		return objects.size() >= 1 ? objects.get(0) : null;
	}

	public Object queryForObject(String sql, Class clazz) {
		return queryForObject(sql, null, clazz);
	}

	public List queryForList(String sql, Class clazz) {
			return queryForList(sql, null, clazz);
	}

	public long getCount(String sql, Object[] params) {
		try {
			return jdbcTemplate.queryForObject(sql, params, Long.class);
		} catch (EmptyResultDataAccessException e) {
			return 0L; // 当查询结果为空时返回0
		} catch (IncorrectResultSizeDataAccessException e) {
			return jdbcTemplate.queryForObject(sql, params, Long.class); // 处理单行结果
		}
	}
	public int update(String sql, Object[] params) {
		//GLLogger.debug("开始查询数据");
		//GLLogger.debug("sql："+sql);
		try{
			return simpleJdbcTemplate.update(sql, params);
		}finally{
			//GLLogger.debug("完成查询数据");
		}
	}

}
