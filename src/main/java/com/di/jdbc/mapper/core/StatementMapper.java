package com.di.jdbc.mapper.core;

import java.util.List;
import java.util.Map;

public interface StatementMapper extends TransactionMapper {
	/**
	 * 执行sql语句，返回影响行数。
	 *
	 * @param statement statement语句
	 * @return 影响的行数
	 */
	int execute(String statement);

	/**
	 * 执行sql语句，获取结果集。
	 *
	 * @param sql statement语句
	 * @param resultClass 结果类型
	 * @return 一个值
	 */
	<T> T get(String sql, Class<T> resultClass);

	/**
	 * 执行sql语句，批量获取结果集。
	 *
	 * @param sql statement语句
	 * @param resultClass 结果类型
	 * @return list对象
	 */
	<T> List<T> list(String sql, Class<T> resultClass);
	
	/**
	 * 执行sql语句，将两列值映射为map,第一列key,第二列value。
	 *
	 * @param sql statement语句
	 * @return map对象
	 */
	Map<Object,Object> listToMap(String sql);
}
