package com.di.jdbc.mapper.core.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.di.jdbc.mapper.core.PreparedStatementMapper;

public class PreparedStatementMapperImpl extends StatementMapperImpl implements PreparedStatementMapper {

	@Override
	public boolean execute(String preparedStatement, Object... args) {
		Connection c = connection();
		PreparedStatement ps = null;
		try {
			ps = c.prepareStatement(preparedStatement);
			if (args != null && args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
			}
			return ps.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			close(null, ps, null);
			returnConnection(c);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String preparedStatement, Class<T> resultClass, Object... args) {
		Connection c = connection();
		PreparedStatement ps = null;
		ResultSet set = null;
		try {
			ps = c.prepareStatement(preparedStatement);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
			}
			set = ps.executeQuery();
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
			close(null, ps, set);
			returnConnection(c);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> list(String preparedStatement, Class<T> resultClass, Object... args) {
		Connection c = connection();
		PreparedStatement ps = null;
		ResultSet set = null;
		try {
			ps = c.prepareStatement(preparedStatement);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
			}
			set = ps.executeQuery();
			if (resultClass == Map.class || resultClass == HashMap.class || resultClass == LinkedHashMap.class) {
				return (List<T>) BeanUtil.resultSets(set);
			} else {
				return BeanUtil.resultSets(set, resultClass);
			}
		} catch (SQLException e) {
			throw new RuntimeException(preparedStatement, e);
		} finally {
			close(null, ps, set);
			returnConnection(c);
		}
	}

	@Override
	public Map<Object, Object> listToMap(String sql, Object... args) {
		Connection c = connection();
		PreparedStatement ps = null;
		ResultSet set = null;
		try {
			ps = c.prepareStatement(sql);
			if (args != null) {
				for (int i = 0; i < args.length; i++) {
					ps.setObject(i + 1, args[i]);
				}
			}
			set = ps.executeQuery();
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
			close(null, ps, set);
			returnConnection(c);
		}
	}
}
