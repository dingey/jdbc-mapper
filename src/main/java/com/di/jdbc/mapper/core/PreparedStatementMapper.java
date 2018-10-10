package com.di.jdbc.mapper.core;

import java.util.List;
import java.util.Map;

public interface PreparedStatementMapper extends StatementMapper {
	/**
	 * 执行预编译的sql语句，args参数，返回成功或失败。
	 *
	 * @param preparedStatement 预编译的prepareStatement语句
	 * @param args 参数值
	 * @return 影响的行数
	 */
	boolean execute(String preparedStatement, Object... args);

	/**
	 * 执行sql语句，获取结果集。
	 *
	 * @param preparedStatement statement语句
	 * @param resultClass 结果类型
	 * @param args 参数值
	 * @return 一行几率
	 */
	<T> T get(String preparedStatement, Class<T> resultClass, Object... args);

	/**
	 * 执行sql语句，批量获取结果集。
	 *
	 * @param preparedStatement statement语句
	 * @param resultClass 结果类型
	 * @param args 参数值
	 * @return list对象
	 */
	<T> List<T> list(String preparedStatement, Class<T> resultClass, Object... args);

	/**
	 * 执行sql语句，将两列值映射为map,第一列key,第二列value。
	 *
	 * @param sql statement语句
	 * @param args 参数值
	 * @return map对象
	 */
	Map<Object, Object> listToMap(String sql, Object... args);
}
