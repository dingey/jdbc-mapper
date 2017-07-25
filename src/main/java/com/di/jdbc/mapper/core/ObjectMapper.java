package com.di.jdbc.mapper.core;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import com.di.jdbc.mapper.annotation.Table;
import com.di.jdbc.mapper.util.ConnectionUtil;
import com.di.jdbc.mapper.util.MillionUtil;
import com.di.jdbc.mapper.util.ObjectMapperUtil;

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

	public <T> void insertMillionObjects(List<T> os, int sqlSize,int batchSize) {
		Connection c = ConnectionUtil.getConn(fileName);
		MillionUtil.insertsPreSql(os,c,sqlSize,batchSize);
		ConnectionUtil.returnConn(fileName, c);
		c = null;
	}
	
	public <T extends Map<String, Object>, E> List<E> where(T t, Class<E> e, Boolean ignoreNull) {
		StringBuilder s = new StringBuilder();
		s.append("select * from ");
		if (e.isAnnotationPresent(Table.class)) {
			s.append(e.getAnnotation(Table.class).name());
		} else {
			s.append(e.getSimpleName());
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
}
