package com.di.jdbc.mapper.core;

import com.di.jdbc.mapper.annotation.NamedNativeQueries;
import com.di.jdbc.mapper.annotation.NamedNativeQuery;
import com.di.jdbc.mapper.util.ExampleUtil;
import com.di.jdbc.mapper.util.Pager;
import com.di.jdbc.mapper.util.PagerSqlUtil;

/**
 * @author di
 */
public class ObjectPagerMapper extends ObjectExampleMapper {
	public ObjectPagerMapper() {
		super();
	}

	public ObjectPagerMapper(String fileName) {
		super(fileName);
	}

	public <T> Pager<T> queryPager(String sql, int pageNum, int pageSize, Class<T> t) {
		Pager<T> p = new Pager<>();
		String sql0 = "select count(0) " + sql.substring(sql.indexOf("from"));
		p.setPageNum(pageNum);
		p.setPageSize(pageSize);
		p.setTotal(this.queryForSingleValue(sql0, long.class));
		p.setList(queryForList(PagerSqlUtil.getPageSql(sql, pageNum, pageSize, fileName), t));
		return p;
	}

	public <T> Pager<T> prepareQueryPager(String preSql, Object[] args, int pageNum, int pageSize, Class<T> t) {
		Pager<T> p = new Pager<>();
		String sql0 = "select count(0) " + preSql.substring(preSql.indexOf("from"));
		p.setPageNum(pageNum);
		p.setPageSize(pageSize);
		p.setTotal(this.prepareQueryForSingleValue(sql0, args, long.class));
		p.setList(prepareQueryForList(PagerSqlUtil.getPreparePageSql(preSql, pageNum, pageSize, fileName), args, t));
		return p;
	}

	public <T> Pager<T> prepareNamedQueryPager(String queryName, Object[] args, int pageNum, int pageSize, Class<T> t) {
		Pager<T> p = new Pager<>();
		String preSql = "";
		if (t.isAnnotationPresent(NamedNativeQueries.class) || t.isAnnotationPresent(NamedNativeQuery.class)) {
			if (t.getAnnotation(NamedNativeQuery.class) != null
					&& t.getAnnotation(NamedNativeQuery.class).name().equals(queryName)) {
				preSql = t.getAnnotation(NamedNativeQuery.class).query();
			} else {
				for (NamedNativeQuery nq : t.getAnnotation(NamedNativeQueries.class).value()) {
					if (nq.name().equals(queryName)) {
						preSql = nq.query();
						break;
					}
				}
			}
		}
		String sql0 = "select count(0) " + preSql.substring(preSql.indexOf("from"));
		p.setPageNum(pageNum);
		p.setPageSize(pageSize);
		p.setTotal(this.prepareQueryForSingleValue(sql0, args, long.class));
		p.setList(
				prepareQueryForList(PagerSqlUtil.getPreparePageSql(preSql, pageNum, pageSize, getFileName()), args, t));
		return p;
	}

	public <T> Pager<T> selectPagerByExample(Object e, int pageNum, int pageSize, Class<T> t) {
		return queryPager(ExampleUtil.selectByExample(e, t), pageNum, pageSize, t);
	}
}
