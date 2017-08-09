package com.di.jdbc.mapper.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.di.jdbc.mapper.annotation.Column;
import com.di.jdbc.mapper.annotation.Id;
import com.di.jdbc.mapper.annotation.Table;
import com.di.jdbc.mapper.util.CacheMapper;
import com.di.jdbc.mapper.util.CacheMapper.Func;
import com.di.jdbc.mapper.util.Camel;
import com.di.jdbc.mapper.util.ConnectionUtil;
import com.di.jdbc.mapper.util.MillionUtil;
import com.di.jdbc.mapper.util.ObjectMapperUtil;
import com.di.jdbc.mapper.util.ReflectUtil;

/**
 * @author di
 */
public class ObjectMapper extends PrepareStatementMapper {
	ObjectMapper() {
		super();
	}

	ObjectMapper(String fileName) {
		super(fileName);
	}

	public <T> void insert(T o) {
		Connection c = ConnectionUtil.getConn(fileName);
		ObjectMapperUtil.insertPrepareSql(o, c, false);
		ConnectionUtil.returnConn(fileName, c);
	}

	public <T> void insertReturnKey(T o) {
		Connection c = ConnectionUtil.getConn(fileName);
		ObjectMapperUtil.insertSql(o, c, false);
		ConnectionUtil.returnConn(fileName, c);
	}

	public <T> void update(T o) {
		Connection c = ConnectionUtil.getConn(fileName);
		ObjectMapperUtil.updatePrepareSql(o, c, false);
		ConnectionUtil.returnConn(fileName, c);
	}

	public <T> void insertMillionObjects(List<T> os, int sqlSize, int batchSize) {
		Connection c = ConnectionUtil.getConn(fileName);
		MillionUtil.insertsPreSql(os, c, sqlSize, batchSize);
		ConnectionUtil.returnConn(fileName, c);
		c = null;
	}

	public <T extends Map<String, Object>, E> List<E> where(T t, Class<E> e, Boolean ignoreNull) {
		StringBuilder s = new StringBuilder();
		s.append("select * from ");
		if (e.isAnnotationPresent(Table.class)) {
			s.append(e.getAnnotation(Table.class).name());
		} else {
			s.append(Camel.toUnderline(e.getSimpleName()));
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
		return prepareQueryForList(s.toString(), args1, e);
	}

	public <T> T get(Object id, Class<T> resultClass) {
		String sql = CacheMapper.getCacheSqlByClass("get", resultClass, true, new Func() {
			@Override
			public String apply() {
				StringBuilder s = new StringBuilder();
				s.append("select * from ");
				if (resultClass.isAnnotationPresent(Table.class)) {
					s.append(resultClass.getAnnotation(Table.class).name());
				} else {
					s.append(Camel.toUnderline(resultClass.getSimpleName()));
				}
				s.append(" where ");
				for (Field f : ReflectUtil.getCommonFields(resultClass)) {
					if (f.isAnnotationPresent(Id.class)) {
						if (f.isAnnotationPresent(Column.class)) {
							s.append(f.getAnnotation(Column.class).name());
						} else {
							s.append(Camel.toUnderline(f.getName()));
						}
						s.append("=?");
						break;
					}
				}
				return s.toString();
			}
		});
		return prepareQueryForObject(sql, new Object[] { id }, resultClass);
	}

	public <T> List<T> findAll(Class<T> resultClass) {
		String sql = CacheMapper.getCacheSqlByClass("findAll", resultClass, true, new Func() {
			@Override
			public String apply() {
				StringBuilder s = new StringBuilder();
				s.append("select * from ");
				if (resultClass.isAnnotationPresent(Table.class)) {
					s.append(resultClass.getAnnotation(Table.class).name());
				} else {
					s.append(Camel.toUnderline(resultClass.getSimpleName()));
				}
				return s.toString();
			}
		});
		return queryForList(sql, resultClass);
	}
}
