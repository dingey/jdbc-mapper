package com.di.jdbc.mapper.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import com.di.jdbc.mapper.annotation.Column;
import com.di.jdbc.mapper.annotation.NamedNativeQueries;
import com.di.jdbc.mapper.annotation.NamedNativeQuery;
import com.di.jdbc.mapper.annotation.TableField;
import com.di.jdbc.mapper.util.ConnectionUtil;
import com.di.jdbc.mapper.util.ResultSetUtil;
import com.di.jdbc.mapper.util.SqlUtil;

/**
 * @author di
 */
public class PrepareStatementMapper extends StatementMapper {
	public PrepareStatementMapper() {
		super();
	}

	public PrepareStatementMapper(String fileName) {
		super(fileName);
	}

	public boolean prepareExecute(String preSql, Object[] args) {
		boolean b = false;
		Connection c = ConnectionUtil.getConn(fileName);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = c.prepareStatement(preSql);
			if (args != null) {
				for (int i = 1; i <= args.length; i++) {
					ps.setObject(i, args[i]);
				}
			}
			b = ps.execute();
		} catch (Exception e) {
			System.err.println("sql: " + preSql);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (ps != null) {
					ps.close();
					ps = null;
				}
				ConnectionUtil.returnConn(fileName, c);
				c = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return b;
	}

	public List<HashMap<String, Object>> prepareQueryForMap(String preSql, Object[] args) {
		List<HashMap<String, Object>> res = Collections.emptyList();
		Connection c = ConnectionUtil.getConn(fileName);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = c.prepareStatement(preSql);
			if (args != null) {
				for (int i = 1; i <= args.length; i++) {
					ps.setObject(i, args[i]);
				}
			}
			rs = ps.executeQuery();
			res = ResultSetUtil.resultSetToMapList(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (ps != null) {
					ps.close();
					ps = null;
				}
				ConnectionUtil.returnConn(fileName, c);
				c = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	public <T> List<T> prepareQueryForList(String preSql, Object[] args, Class<T> resultClass, String tableName) {
		List<T> list = new ArrayList<>();
		Connection c = ConnectionUtil.getConn(fileName);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = c.prepareStatement(preSql);
			if (args != null) {
				for (int i = 1; i <= args.length; i++) {
					ps.setObject(i, args[i - 1]);
				}
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				T obj = resultClass.newInstance();
				Field[] fs = obj.getClass().getDeclaredFields();
				for (Field f : fs) {
					f.setAccessible(true);
					if (f.isAnnotationPresent(TableField.class)) {
						if (tableName != null) {
							f.set(obj, tableName);
						}
						continue;
					}
					String column = f.getName();
					if (f.isAnnotationPresent(Column.class)) {
						column = f.getAnnotation(Column.class).name();
					}
					SqlUtil.setFieldValue(obj, f, rs, column);
				}
				list.add(obj);
			}
		} catch (Exception e) {
			System.err.println(preSql);
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (ps != null) {
					ps.close();
					ps = null;
				}
				ConnectionUtil.returnConn(fileName, c);
				c = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public <T> T prepareQueryForObject(String sql, Object[] args, Class<T> resultClass, String tableName) {
		List<T> list = prepareQueryForList(sql, args, resultClass, tableName);
		return list.isEmpty() ? null : list.get(0);
	}

	@SuppressWarnings("unchecked")
	public <T> T prepareQueryForSingleValue(String preSql, Object[] args, Class<T> resultClass) {
		Connection c = ConnectionUtil.getConn(fileName);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = c.prepareStatement(preSql);
			if (args != null) {
				for (int i = 1; i <= args.length; i++) {
					ps.setObject(i, args[i - 1]);
				}
			}
			rs = ps.executeQuery();
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
				if (ps != null) {
					ps.close();
					ps = null;
				}
				ConnectionUtil.returnConn(fileName, c);
				c = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public <T> List<T> prepareNamedQueryForList(String namedQueryName, Object[] args, Class<T> resultClass,
			String tableName) {
		String preSql = "";
		if (resultClass.isAnnotationPresent(NamedNativeQueries.class)
				|| resultClass.isAnnotationPresent(NamedNativeQuery.class)) {
			for (NamedNativeQuery q : resultClass.getAnnotation(NamedNativeQueries.class).value()) {
				if (q.name().equals(namedQueryName)) {
					preSql = q.query();
					break;
				}
			}
			if (resultClass.isAnnotationPresent(NamedNativeQuery.class)
					&& resultClass.getAnnotation(NamedNativeQuery.class).name().equals(namedQueryName)) {
				preSql = resultClass.getAnnotation(NamedNativeQuery.class).query();
			}
		}
		if (tableName != null) {
			String s1 = preSql.substring(0, preSql.indexOf("from") + 4);
			if (preSql.indexOf("where") != -1) {
				String s2 = preSql.substring(preSql.indexOf("where"));
				preSql = s1 + " " + tableName + " " + s2;
			} else {
				preSql = s1 + " " + tableName;
			}
		}
		return this.prepareQueryForList(preSql, args, resultClass, tableName);
	}

}
