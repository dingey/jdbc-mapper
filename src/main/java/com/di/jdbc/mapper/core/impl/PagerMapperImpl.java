package com.di.jdbc.mapper.core.impl;

import java.util.List;
import java.util.Map;

import com.di.jdbc.mapper.core.Pager;
import com.di.jdbc.mapper.core.PagerMapper;

public class PagerMapperImpl extends ObjectMapperImpl implements PagerMapper {

	@SuppressWarnings("rawtypes")
	@Override
	public Pager<Map> page(String statement, int pageNum, int pageSize) {
		String sql0 = "select count(0) " + statement.substring(statement.indexOf("from"));
		String pageSql = PagerSqlUtil.getPageSql(statement, pageNum, pageSize, connection());
		List<Map> list = list(pageSql, Map.class);
		return new SimplePager<Map>(pageNum, pageSize, super.get(sql0, int.class), list);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Pager<Map> page(String preparedStatement, Object[] args, int pageNum, int pageSize) {
		String sql0 = "select count(0) " + preparedStatement.substring(preparedStatement.indexOf("from"));
		String pageSql = PagerSqlUtil.getPreparePageSql(preparedStatement, args, pageNum, pageSize, connection());
		args = PagerSqlUtil.getPageArgs(args);
		List<Map> list = list(pageSql, args, Map.class);
		return new SimplePager<Map>(pageNum, pageSize, super.get(sql0, int.class), list);
	}

	@Override
	public <T> Pager<T> page(String sql, int pageNum, int pageSize, Class<T> resultClass) {
		String sql0 = "select count(0) " + sql.substring(sql.indexOf("from"));
		String pageSql = PagerSqlUtil.getPageSql(sql, pageNum, pageSize, connection());
		List<T> list = list(pageSql, resultClass);
		return new SimplePager<T>(pageNum, pageSize, super.get(sql0, int.class), list);
	}

	@Override
	public <T> Pager<T> page(String preparedStatement, Object[] args, int pageNum, int pageSize, Class<T> resultClass) {
		String sql0 = "select count(0) " + preparedStatement.substring(preparedStatement.indexOf("from"));
		String pageSql = PagerSqlUtil.getPageSql(preparedStatement, pageNum, pageSize, connection());
		List<T> list = list(pageSql, args, resultClass);
		return new SimplePager<T>(pageNum, pageSize, super.get(sql0, args, int.class), list);
	}
}
