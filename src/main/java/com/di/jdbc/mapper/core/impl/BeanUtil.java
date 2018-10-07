package com.di.jdbc.mapper.core.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.di.jdbc.mapper.annotation.TypeHandler;
import com.di.jdbc.mapper.annotation.TypeHandler.BaseTypeHandler;

@SuppressWarnings("all")
public class BeanUtil {
	static <T> List<T> resultSets(ResultSet set, Class<T> clazz) throws SQLException {
		if (set.wasNull()) {
			return Collections.emptyList();
		} else {
			List<T> l = new ArrayList<>();
			while (set.next()) {
				l.add(resultSet(set, clazz));
			}
			return l;
		}
	}

	static <T> T resultSet(ResultSet set, Class<T> clazz) throws SQLException {
		if (clazz.isPrimitive() || isJavaClass(clazz)) {
			return (T) transform(set, 1, clazz);
		} else if (clazz != Class.class) {
			try {
				T t = clazz.newInstance();
				for (Field f : ModelUtil.getField(clazz)) {
					String col = ModelUtil.column(f);
					if (!contain(set, col))
						continue;
					TypeHandler th = ModelUtil.typeHandler(f);
					Object result;
					if (th != null) {
						result = th.getNullableResult(set, col);
					} else {
						result = transform(set, col, f.getType());
					}
					Method method = ModelUtil.setMethod(f);
					if (method != null)
						method.invoke(t, result);
				}
				return t;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("clazz必须声明无参构造方法。");
			} catch (SQLException e) {
				throw new RuntimeException(e.getMessage());
			} catch (IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	static Map<String, ?> resultSet(ResultSet set) {
		try {
			if (set.wasNull()) {
				return null;
			}
			ResultSetMetaData meta = set.getMetaData();
			Map<String, Object> m = new LinkedHashMap<>();
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				m.put(meta.getColumnName(i), set.getObject(i));
			}
			return m;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	static List<Map<String, ?>> resultSets(ResultSet set) {
		try {
			if (set.wasNull()) {
				return Collections.emptyList();
			}
			ResultSetMetaData meta = set.getMetaData();
			List<Map<String, ?>> l = new ArrayList<>();
			while (set.next()) {
				Map<String, Object> m = new LinkedHashMap<>();
				for (int i = 1; i <= meta.getColumnCount(); i++) {
					if (meta.isNullable(i) == 0) {
						m.put(meta.getColumnName(i), columnNoNulls(set, i, meta.getColumnTypeName(i)));
					} else {
						m.put(meta.getColumnName(i), columnNullable(set, i, meta.getColumnTypeName(i)));
					}
				}
				l.add(m);
			}
			return l;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	static void print(ResultSetMetaData meta) throws SQLException {
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			System.out.println(meta.getColumnName(i) + " : " + meta.getColumnType(i) + " : " + meta.getColumnTypeName(i) + " : " + meta.getColumnClassName(i));
		}
	}

	static Object columnValue(ResultSet set, int columnIndex) {
		try {
			if (set.getMetaData().isNullable(columnIndex) == 0) {
				return columnNoNulls(set, columnIndex, set.getMetaData().getColumnTypeName(columnIndex));
			} else {
				return columnNullable(set, columnIndex, set.getMetaData().getColumnTypeName(columnIndex));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	static Object columnNoNulls(ResultSet set, int columnIndex, String name) throws SQLException {
		if (name.contains("BIT")) {
			return set.getInt(columnIndex);
		} else if (name.contains("TINYINT")) {
			return set.getInt(columnIndex);
		} else if (name.contains("SMALLINT")) {
			return set.getInt(columnIndex);
		} else if (name.contains("MEDIUMINT")) {
			return set.getInt(columnIndex);
		} else if (name.contains("BIGINT")) {
			return set.getLong(columnIndex);
		} else if (name.contains("INT") || name.contains("INTEGER")) {
			return set.getInt(columnIndex);
		} else if (name.contains("FLOAT")) {
			return set.getFloat(columnIndex);
		} else if (name.contains("DOUBLE")) {
			return set.getDouble(columnIndex);
		} else if (name.contains("DECIMAL")) {
			return set.getBigDecimal(columnIndex);
		} else if (name.contains("VARCHAR") || name.contains("CHAR") || name.contains("text")) {
			return set.getString(columnIndex);
		} else if (name.contains("DATETIME") || name.contains("TIMESTAMP")) {
			return set.getTimestamp(columnIndex);
		} else if (name.contains("TIME")) {
			return set.getTime(columnIndex);
		} else if (name.contains("DATE")) {
			return set.getDate(columnIndex);
		} else if (name.contains("BINARY")) {
			return set.getBytes(columnIndex);
		} else if (name.contains("BLOB")) {
			return set.getBlob(columnIndex);
		} else {
			return set.getObject(columnIndex);
		}
	}

	static Object columnNullable(ResultSet set, int columnIndex, String name) throws SQLException {
		if (set.getObject(columnIndex) == null) {
			return null;
		}
		return columnNoNulls(set, columnIndex, name);
	}

	static <T> Object transform(ResultSet set, int columnIndex, Class<T> t) throws SQLException {
		if (!t.isPrimitive() && set.getMetaData().isNullable(columnIndex) == 1 && set.getObject(columnIndex) == null) {
			return null;
		} else if (t == byte.class || t == Byte.class) {
			return set.getByte(columnIndex);
		} else if (t == short.class || t == Short.class) {
			return set.getShort(columnIndex);
		} else if (t == int.class || t == Integer.class) {
			return set.getInt(columnIndex);
		} else if (t == long.class || t == Long.class) {
			return set.getLong(columnIndex);
		} else if (t == double.class || t == Double.class) {
			return set.getDouble(columnIndex);
		} else if (t == float.class || t == Float.class) {
			return set.getFloat(columnIndex);
		} else if (t == BigDecimal.class || t == BigDecimal.class) {
			return set.getBigDecimal(columnIndex);
		} else if (t == boolean.class || t == Boolean.class) {
			return set.getBoolean(columnIndex);
		} else if (t == java.sql.Date.class) {
			return set.getDate(columnIndex);
		} else if (t == java.sql.Time.class) {
			return set.getTime(columnIndex);
		} else if (t == Date.class || t == Timestamp.class) {
			return set.getTimestamp(columnIndex);
		} else if (t == String.class) {
			return set.getString(columnIndex);
		} else {
			return set.getObject(columnIndex);
		}
	}

	static <T> Object transform(ResultSet set, String columnIndex, Class<T> t) throws SQLException {
		if (set.getObject(columnIndex) == null) {
			return null;
		} else if (t == byte.class || t == Byte.class) {
			return set.getByte(columnIndex);
		} else if (t == short.class || t == Short.class) {
			return set.getShort(columnIndex);
		} else if (t == int.class || t == Integer.class) {
			return set.getInt(columnIndex);
		} else if (t == long.class || t == Long.class) {
			return set.getLong(columnIndex);
		} else if (t == double.class || t == Double.class) {
			return set.getDouble(columnIndex);
		} else if (t == float.class || t == Float.class) {
			return set.getFloat(columnIndex);
		} else if (t == BigDecimal.class || t == BigDecimal.class) {
			return set.getBigDecimal(columnIndex);
		} else if (t == boolean.class || t == Boolean.class) {
			return set.getBoolean(columnIndex);
		} else if (t == java.sql.Date.class) {
			return set.getDate(columnIndex);
		} else if (t == java.sql.Time.class) {
			return set.getTime(columnIndex);
		} else if (t == Date.class || t == Timestamp.class) {
			return set.getTimestamp(columnIndex);
		} else if (t == String.class) {
			return set.getString(columnIndex);
		} else {
			return set.getObject(columnIndex);
		}
	}

	public static boolean isJavaClass(Class<?> clz) {
		return clz != null && clz.getClassLoader() == null;
	}

	private static boolean contain(ResultSet res, String column) {
		try {
			return res.findColumn(column) > 0;
		} catch (SQLException e) {
			return false;
		}
	}
}
