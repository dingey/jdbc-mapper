package com.di.jdbc.mapper.core.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.di.jdbc.mapper.core.StatementMapper;

public class StatementMapperImpl extends TransactionMapperImpl implements StatementMapper {

	@Override
	public int execute(String statement) {
		Connection c = connection();
		Statement st = null;
		try {
			st = c.createStatement();
			return st.executeUpdate(statement);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(st, null, null);
			returnConnection(c);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String statement, Class<T> resultClass) {
		Connection c = connection();
		Statement st = null;
		ResultSet set = null;
		try {
			st = c.createStatement();
			set = st.executeQuery(statement);
			if (set.wasNull()) {
				return null;
			} else {
				set.next();
			}
			if (resultClass == Map.class) {
				return (T) BeanUtil.resultSet(set);
			} else {
				return (T) BeanUtil.resultSet(set, resultClass);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(st, null, set);
			returnConnection(c);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list(String sql, Class<T> resultClass) {
		Connection c = connection();
		Statement st = null;
		ResultSet set = null;
		try {
			st = c.createStatement();
			set = st.executeQuery(sql);
			if (resultClass == Map.class || resultClass == HashMap.class || resultClass == LinkedHashMap.class) {
				return (List<T>) BeanUtil.resultSets(set);
			} else {
				return BeanUtil.resultSets(set, resultClass);
			}
		} catch (SQLException e) {
			throw new RuntimeException(sql, e);
		} finally {
			close(st, null, set);
			returnConnection(c);
		}
	}

	@Override
	public Map<Object, Object> listToMap(String sql) {
		Connection c = connection();
		Statement st = null;
		ResultSet set = null;
		try {
			st = c.createStatement();
			set = st.executeQuery(sql);
			Map<Object, Object> m = new LinkedHashMap<>();
			if (!set.wasNull()) {
				while (set.next()) {
					m.put(set.getObject(1), set.getObject(2));
				}
			}
			return m;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(st, null, set);
			returnConnection(c);
		}
	}
}
