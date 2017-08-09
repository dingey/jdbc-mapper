package com.di.jdbc.mapper.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.di.jdbc.mapper.annotation.Column;
import com.di.jdbc.mapper.util.ConnectionUtil;
import com.di.jdbc.mapper.util.ReflectUtil;
import com.di.jdbc.mapper.util.ResultSetUtil;
import com.di.jdbc.mapper.util.SqlUtil;

/**
 * @author di
 */
public class StatementMapper extends AbstractMapper {

	public StatementMapper() {
		super();
	}

	public StatementMapper(String fileName) {
		super(fileName);
	}

	public boolean execute(String sql) {
		boolean b = false;
		Connection c = ConnectionUtil.getConn(fileName);
		Statement st = null;
		ResultSet rs = null;
		try {
			st = c.createStatement();
			b = st.execute(sql);
		} catch (Exception e) {
			System.err.println("sql: " + sql);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (st != null) {
					st.close();
					st = null;
				}
				ConnectionUtil.returnConn(fileName, c);
				c = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return b;
	}

	public List<HashMap<String, Object>> queryForMap(String sql) {
		List<HashMap<String, Object>> res = Collections.emptyList();
		Connection c = ConnectionUtil.getConn(fileName);
		Statement st = null;
		ResultSet rs = null;
		try {
			st = c.createStatement();
			rs = st.executeQuery(sql);
			res = ResultSetUtil.resultSetToMapList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (st != null) {
					st.close();
					st = null;
				}
				ConnectionUtil.returnConn(fileName, c);
				c = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	public <T> List<T> queryForList(String sql, Class<T> resultClass) {
		List<T> list = new ArrayList<>();
		Connection c = ConnectionUtil.getConn(fileName);
		Statement st = null;
		ResultSet rs = null;
		try {
			st = c.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				T obj = resultClass.newInstance();
				for (Field f : ReflectUtil.getCommonFields(resultClass)) {
					f.setAccessible(true);
					String column = f.getName();
					if (f.isAnnotationPresent(Column.class)) {
						column = f.getAnnotation(Column.class).name();
					}
					SqlUtil.setFieldValue(obj, f, rs, column);
				}
				list.add(obj);
			}
		} catch (Exception e) {
			System.err.println(sql);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (st != null) {
					st.close();
					st = null;
				}
				ConnectionUtil.returnConn(fileName, c);
				c = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public <T> T queryForObject(String sql, Class<T> resultClass) {
		List<T> list = queryForList(sql, resultClass);
		return list.isEmpty() ? null : list.get(0);
	}

	@SuppressWarnings("unchecked")
	public <T> T queryForSingleValue(String sql, Class<T> resultClass) {
		Connection c = ConnectionUtil.getConn(fileName);
		Statement st = null;
		ResultSet rs = null;
		try {
			st = c.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				return (T) SqlUtil.getResultSetTypeByClassType(resultClass, rs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (st != null) {
					st.close();
					st = null;
				}
				ConnectionUtil.returnConn(fileName, c);
				c = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
