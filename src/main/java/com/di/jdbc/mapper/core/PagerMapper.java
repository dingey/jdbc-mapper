package com.di.jdbc.mapper.core;

import java.util.Map;

public interface PagerMapper extends ObjectMapper {
	/**
	 * 执行分页查询。
	 *
	 * @param statement statement语句
	 * @param pageNum 当前页
	 * @param pageSize 每页大小
	 * @return 分页数据
	 */
	@SuppressWarnings("rawtypes")
	Pager<Map> page(String statement, int pageNum, int pageSize);

	/**
	 * 执行分页查询。
	 *
	 * @param statement statement语句
	 * @param pageNum 当前页
	 * @param pageSize 每页大小
	 * @param args 参数值
	 * @return 分页数据
	 */
	@SuppressWarnings("rawtypes")
	Pager<Map> page(String preparedStatement, int pageNum, int pageSize, Object... args);

	/**
	 * 执行分页查询。
	 *
	 * @param statement statement语句
	 * @param pageNum 当前页
	 * @param pageSize 每页大小
	 * @return 分页数据
	 */
	<T> Pager<T> page(String statement, int pageNum, int pageSize, Class<T> entity);

	/**
	 * 执行分页查询。
	 *
	 * @param preparedStatement preparedStatement语句
	 * @param pageNum 当前页
	 * @param pageSize 每页大小
	 * @param args 参数值
	 * @return 分页数据
	 */
	<T> Pager<T> page(String preparedStatement, int pageNum, int pageSize, Class<T> entity, Object... args);
}
