package com.di.jdbc.mapper.core;

import java.util.Map;

import com.di.jdbc.mapper.annotation.NamedNativeQueries;
import com.di.jdbc.mapper.annotation.NamedNativeQuery;
import com.di.jdbc.mapper.annotation.Table;
import com.di.jdbc.mapper.util.Camel;
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
		return prepareQueryPager(preSql, args, pageNum, pageSize, t);
	}

	public <T> Pager<T> selectPagerByExample(Object e, int pageNum, int pageSize, Class<T> t) {
		return prepareQueryPager(ExampleUtil.selectByExample(e, t), null, pageNum, pageSize, t);
	}
	
	public <T extends Map<String, Object>, E> Pager<E> wherePager(T t, Class<E> e,int pageNum,int pageSize, Boolean ignoreNull) {
		StringBuilder s = new StringBuilder();
		s.append("select * from ");
		if (e.getClass().isAnnotationPresent(Table.class)) {
			s.append(e.getClass().getAnnotation(Table.class).name());
		} else {
			s.append(e.getClass().getSimpleName());
		}
		s.append(" where 1=1");
		Object[] args = new Object[t.size()];
		int i = 0;
		for (String k : t.keySet()) {
			Object v = t.get(k);
			if (ignoreNull && v != null) {
				s.append(" and ").append(k);
				args[i] = v;
				i++;
			} else if (!ignoreNull) {
				s.append(" and ").append(k);
				args[i] = v;
				i++;
			}
		}
		Object[] args1 = new Object[i];
		for (int j = 0; j < args1.length; j++) {
			args1[j] = args[j];
		}
		return prepareQueryPager(s.toString(), args1, pageNum, pageSize, e);
	}
	
	public <T> Pager<T> pagerAll(int pageNum, int pageSize, Class<T> t) {
		StringBuilder s = new StringBuilder();
		s.append("select * from ");
		if (t.isAnnotationPresent(Table.class)) {
			s.append(t.getAnnotation(Table.class).name());
		} else {
			s.append(Camel.toUnderline(t.getSimpleName()));
		}
		String preSql=s.toString();
		Pager<T> p = new Pager<>();
		String sql0 = "select count(0) " + preSql.substring(preSql.indexOf("from"));
		p.setPageNum(pageNum);
		p.setPageSize(pageSize);
		p.setTotal(this.prepareQueryForSingleValue(sql0, null, long.class));
		p.setList(prepareQueryForList(PagerSqlUtil.getPreparePageSql(preSql, pageNum, pageSize, fileName), null, t));
		return p;
	}
}
