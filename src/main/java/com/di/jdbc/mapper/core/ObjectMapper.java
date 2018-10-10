package com.di.jdbc.mapper.core;

import java.util.List;

public interface ObjectMapper extends PreparedStatementMapper {
	/**
	 * 插入一条记录，如果有自增主键请设置GeneratedValue。
	 *
	 * @param t
	 * @return 影响的行数
	 */
	<T> int insert(T t);

	/**
	 * 插入一条记录忽略值，如果有自增主键请设置GeneratedValue。
	 *
	 * @param t
	 * @return 影响的行数
	 */
	<T> int insertSelective(T t);

	/**
	 * 修改一条记录。
	 *
	 * @param t 参数对象
	 * @return 影响的行数
	 */
	<T> int update(T t);

	/**
	 * 修改一条记录忽略空值。
	 *
	 * @param t 参数对象
	 * @return 影响行数
	 */
	<T> int updateSelective(T t);

	/**
	 * 执行statement语句，返回一条记录映射为指定类型
	 *
	 * @param statement statement语句
	 * @param resultClass 结果类
	 * @return 单个对象
	 */
	<T> T get(String statement, Class<T> resultClass);

	/**
	 * 执行statement语句，返回一条记录映射为指定类型
	 *
	 * @param statement statement语句
	 * @param resultClass 结果类
	 * @param args 参数值
	 * @return 单个对象
	 */
	<T> T get(String statement, Class<T> resultClass, Object... args);

	/**
	 * 执行statement语句，返回多条记录。
	 *
	 * @param statement statement语句
	 * @param resultClass 结果类
	 * @return list对象
	 */
	<T> List<T> list(String statement, Class<T> resultClass);

	/**
	 * 执行prepareStatement语句，返回多条记录。
	 *
	 * @param prepareStatement 预编译的prepareStatement语句
	 * @param resultClass 结果类型
	 * @param args 参数值
	 * @return list对象
	 */
	<T> List<T> list(String prepareStatement, Class<T> resultClass, Object... args);
}
