package com.di.jdbc.mapper.util;

import java.lang.reflect.Field;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.di.jdbc.mapper.annotation.Column;
import com.di.jdbc.mapper.annotation.Id;
import com.di.jdbc.mapper.annotation.JoinColumn;
import com.di.jdbc.mapper.annotation.ManyToOne;
import com.di.jdbc.mapper.annotation.OneToMany;
import com.di.jdbc.mapper.annotation.Table;
import com.di.jdbc.mapper.annotation.Transient;

/**
 * @author di
 * @version 1.0
 */
public class SqlUtil {
	public static void setFieldValue(Object o, Field f, ResultSet res, String column) {
		try {
			f.set(o, getResultSetTypeByFieldType(f, res, column));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static Object getResultSetTypeByClassType(Class<?> type, ResultSet res) {
		try {
			if (type == short.class) {
				return res.getShort(1);
			} else if (type == int.class || type == java.lang.Integer.class) {
				return res.getInt(1);
			} else if (type == long.class || type == java.lang.Long.class) {
				return res.getLong(1);
			} else if (type == double.class || type == java.lang.Double.class) {
				return res.getDouble(1);
			} else if (type == float.class || type == java.lang.Float.class) {
				return res.getFloat(1);
			} else if (type == boolean.class || type == java.lang.Boolean.class) {
				return res.getBoolean(1);
			} else if (type == java.math.BigDecimal.class) {
				return res.getBigDecimal(1);
			} else if (type == java.util.Date.class) {
				return res.getTimestamp(1);
			} else if (type == String.class) {
				return res.getString(1);
			} else {
				return res.getObject(1);
			}
		} catch (SQLException e) {
			return null;
		}
	}

	public static void setFieldValue(Object o, Field f, ResultSet res) {
		try {
			f.set(o, getResultSetTypeByFieldType(f, res, 1));
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private static Object getResultSetTypeByFieldType(Field f, ResultSet res, String colName) {
		try {
			if (f.getType() == short.class) {
				return res.getShort(colName);
			} else if (f.getType() == int.class || f.getType() == java.lang.Integer.class) {
				return res.getInt(colName);
			} else if (f.getType() == long.class || f.getType() == java.lang.Long.class) {
				return res.getLong(colName);
			} else if (f.getType() == double.class || f.getType() == java.lang.Double.class) {
				return res.getDouble(colName);
			} else if (f.getType() == float.class || f.getType() == java.lang.Float.class) {
				return res.getFloat(colName);
			} else if (f.getType() == boolean.class || f.getType() == java.lang.Boolean.class) {
				return res.getBoolean(colName);
			} else if (f.getType() == java.math.BigDecimal.class) {
				return res.getBigDecimal(colName);
			} else if (f.getType() == java.util.Date.class) {
				return res.getTimestamp(colName);
			} else if (f.getType() == String.class) {
				return res.getString(colName);
			} else {
				return res.getObject(colName);
			}
		} catch (SQLException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static Object getResultSetTypeByFieldType(Field f, ResultSet res, int colIndex) {
		try {
			if (f.getType() == short.class) {
				return res.getShort(colIndex);
			} else if (f.getType() == int.class || f.getType() == java.lang.Integer.class) {
				return res.getInt(colIndex);
			} else if (f.getType() == long.class || f.getType() == java.lang.Long.class) {
				return res.getLong(colIndex);
			} else if (f.getType() == double.class || f.getType() == java.lang.Double.class) {
				return res.getDouble(colIndex);
			} else if (f.getType() == float.class || f.getType() == java.lang.Float.class) {
				return res.getFloat(colIndex);
			} else if (f.getType() == boolean.class || f.getType() == java.lang.Boolean.class) {
				return res.getBoolean(colIndex);
			} else if (f.getType() == java.math.BigDecimal.class) {
				return res.getBigDecimal(colIndex);
			} else if (f.getType() == java.util.Date.class) {
				return res.getTimestamp(colIndex);
			} else if (f.getType() == String.class) {
				return res.getString(colIndex);
			} else {
				return res.getObject(colIndex);
			}
		} catch (SQLException | IllegalArgumentException e) {
			return null;
		}
	}

	public static Object setSqlValue(Object o, Field f) {
		try {
			if (f.get(o) == null) {
				return null;
			}
			if (f.getType() == boolean.class || f.getType() == java.lang.Boolean.class) {
				return (boolean) f.get(o) ? 1 : 0;
			} else if (f.getType() == java.lang.String.class) {
				return "'" + f.get(o) + "'";
			} else if (f.getType() == java.util.Date.class) {
				return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date) f.get(o)) + "'";
			} else {
				return f.get(o);
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <T> List<T> resultSetToList(Class<T> result, ResultSet res) {
		List<T> list = new ArrayList<>();
		try {
			while (res.next()) {
				T obj = result.newInstance();
				Field[] fs = obj.getClass().getDeclaredFields();
				for (Field f : fs) {
					f.setAccessible(true);
					String column = f.getName();
					if (f.isAnnotationPresent(Column.class)) {
						column = f.getAnnotation(Column.class).name();
					}
					SqlUtil.setFieldValue(obj, f, res, column);
				}
				list.add(obj);
			}
		} catch (InstantiationException | IllegalAccessException | SecurityException | SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static String getUpdateSelecitiveSql(Object o) {
		Field fs[] = o.getClass().getDeclaredFields();
		String tabName;
		if (o.getClass().isAnnotationPresent(Table.class)) {
			tabName = o.getClass().getAnnotation(Table.class).name();
		} else {
			tabName = o.getClass().getSimpleName();
		}
		StringBuilder sql = new StringBuilder("update ").append(tabName).append(" set ");
		String idName = "id";
		Object idValue = null;
		for (Field f : fs) {
			f.setAccessible(true);
			try {
				if (f.get(o) == null) {
					continue;
				}
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
			try {
				if (f.isAnnotationPresent(Id.class)) {
					idValue = f.get(o);
					if (f.isAnnotationPresent(Column.class)) {
						idName = f.getAnnotation(Column.class).name();
					} else {
						idName = f.getName();
					}
				} else if (f.isAnnotationPresent(JoinColumn.class) && f.isAnnotationPresent(ManyToOne.class)) {
				} else if (f.isAnnotationPresent(OneToMany.class)) {
				} else if (f.isAnnotationPresent(Transient.class) || !f.getAnnotation(Column.class).updatable()) {
				} else if (f.isAnnotationPresent(Column.class)) {
					sql.append(f.getAnnotation(Column.class).name()).append("=").append(SqlUtil.setSqlValue(o, f))
							.append(",");
				} else {
					sql.append(f.getName()).append("=").append(SqlUtil.setSqlValue(o, f)).append(",");
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		String sq = sql.toString();
		sql = new StringBuilder(sq.substring(0, sq.lastIndexOf(",")));
		sql.append(" where ").append(idName).append("='").append(idValue).append("'");
		return sql.toString();
	}

	public static String getInsertSelecitiveSql(Object o) {
		Field fs[] = o.getClass().getDeclaredFields();
		String tabName;
		if (o.getClass().isAnnotationPresent(Table.class)) {
			tabName = o.getClass().getAnnotation(Table.class).name();
		} else {
			tabName = o.getClass().getSimpleName();
		}
		StringBuilder sql = new StringBuilder("insert into ").append(tabName).append(" (");
		StringBuilder s1 = new StringBuilder();
		for (Field f : fs) {
			f.setAccessible(true);
			try {
				if (f.get(o) == null) {
					continue;
				}
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
			try {
				if (f.isAnnotationPresent(JoinColumn.class) && f.isAnnotationPresent(ManyToOne.class)) {
				} else if (f.isAnnotationPresent(OneToMany.class)) {
				} else if (f.isAnnotationPresent(Transient.class) || !f.getAnnotation(Column.class).insertable()) {
				} else if (f.isAnnotationPresent(Column.class)) {
					s1.append(f.getAnnotation(Column.class).name()).append(",");
				} else {
					s1.append(f.getName()).append(",");
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		String sq = s1.toString();
		sql.append(sq.substring(0, sq.lastIndexOf(","))).append(")values(");
		for (Field f : fs) {
			f.setAccessible(true);
			try {
				if (f.get(o) == null) {
					continue;
				}
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
			try {
				if (f.isAnnotationPresent(JoinColumn.class) && f.isAnnotationPresent(ManyToOne.class)) {
				} else if (f.isAnnotationPresent(OneToMany.class)) {
				} else if (f.isAnnotationPresent(Transient.class) || !f.getAnnotation(Column.class).insertable()) {
				} else if (f.isAnnotationPresent(Column.class)) {
					sql.append(SqlUtil.setSqlValue(o, f)).append(",");
				} else {
					sql.append(SqlUtil.setSqlValue(o, f)).append(",");
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		sq = sql.toString();
		sql = new StringBuilder(sq.substring(0, sq.lastIndexOf(",")));
		sql.append(")");
		return sql.toString();
	}

	public static String getPrepareInsertSelecitiveSql(Object o) {
		StringBuilder s = new StringBuilder("insert into ");
		if (o.getClass().isAnnotationPresent(Table.class)) {
			s.append(o.getClass().getAnnotation(Table.class).name());
		} else {
			s.append(o.getClass().getSimpleName());
		}
		s.append("(");
		StringBuilder s0 = new StringBuilder();
		int count = 0;
		try {
			for (Field f : o.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (f.get(o) != null) {
					count++;
					if (f.isAnnotationPresent(Column.class)) {
						s0.append(f.getAnnotation(Column.class).name()).append(",");
					} else {
						s0.append(f.getName()).append(",");
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		s.append(s0.toString().substring(0, s0.toString().lastIndexOf(",")));
		s.append(") values (");
		if (count > 0) {
			for (int i = 0; i < count - 1; i++) {
				s.append("?").append(",");
			}
			s.append("?)");
		}
		return s.toString();
	}

	public static String getPrepareUpdateSelecitiveSql(Object o) {
		StringBuilder s = new StringBuilder("update ");
		if (o.getClass().isAnnotationPresent(Table.class)) {
			s.append(o.getClass().getAnnotation(Table.class).name());
		} else {
			s.append(o.getClass().getSimpleName());
		}
		s.append(" set ");
		StringBuilder s0 = new StringBuilder();
		String idName = "";
		try {
			for (Field f : o.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (f.get(o) != null) {
					if (f.isAnnotationPresent(Id.class)) {
						if (f.isAnnotationPresent(Column.class)) {
							idName = f.getAnnotation(Column.class).name();
						} else {
							idName = f.getName();
						}
						continue;
					}
					if (f.isAnnotationPresent(Column.class)) {
						s0.append(f.getAnnotation(Column.class).name()).append("=?,");
					} else {
						s0.append(f.getName()).append("=?,");
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		s.append(s0.toString().substring(0, s0.toString().lastIndexOf(",")));
		s.append(" where ").append(idName).append("=?");
		return s.toString();
	}

	public static void setPrepareParams(Object o, PreparedStatement pst) {
		try {
			int i = 0;
			for (Field f : o.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (f.get(o) == null || f.isAnnotationPresent(Id.class)) {
					continue;
				}
				i++;
				if (f.getType() == short.class || f.getType() == java.lang.Short.class) {
					pst.setShort(i, f.getShort(o));
				} else if (f.getType() == int.class || f.getType() == java.lang.Integer.class) {
					pst.setInt(i, f.getInt(o));
				} else if (f.getType() == long.class || f.getType() == java.lang.Long.class) {
					pst.setLong(i, f.getLong(o));
				} else if (f.getType() == double.class || f.getType() == java.lang.Double.class) {
					pst.setDouble(i, f.getDouble(o));
				} else if (f.getType() == float.class || f.getType() == java.lang.Float.class) {
					pst.setFloat(i, f.getFloat(o));
				} else if (f.getType() == boolean.class || f.getType() == java.lang.Boolean.class) {
					pst.setBoolean(i, f.getBoolean(o));
				} else if (f.getType() == java.math.BigDecimal.class) {
					pst.setObject(i, f.get(o));
				} else if (f.getType() == java.util.Date.class) {
					pst.setDate(i, new java.sql.Date(((Date) f.get(o)).getTime()));
				} else if (f.getType() == String.class) {
					pst.setString(i, (String) f.get(o));
				} else {
					pst.setObject(i, f.get(o));
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setPrepareUpdateParams(Object o, PreparedStatement pst) {
		try {
			int i = 0;
			Field id = null;
			for (Field f : o.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (f.get(o) == null || f.isAnnotationPresent(Id.class)) {
					id = f;
					continue;
				}
				i++;
				if (f.getType() == short.class || f.getType() == java.lang.Short.class) {
					pst.setShort(i, f.getShort(o));
				} else if (f.getType() == int.class || f.getType() == java.lang.Integer.class) {
					pst.setInt(i, f.getInt(o));
				} else if (f.getType() == long.class || f.getType() == java.lang.Long.class) {
					pst.setLong(i, f.getLong(o));
				} else if (f.getType() == double.class || f.getType() == java.lang.Double.class) {
					pst.setDouble(i, f.getDouble(o));
				} else if (f.getType() == float.class || f.getType() == java.lang.Float.class) {
					pst.setFloat(i, f.getFloat(o));
				} else if (f.getType() == boolean.class || f.getType() == java.lang.Boolean.class) {
					pst.setBoolean(i, f.getBoolean(o));
				} else if (f.getType() == java.math.BigDecimal.class) {
					pst.setObject(i, f.get(o));
				} else if (f.getType() == java.util.Date.class) {
					try {
						pst.setTimestamp(i, new Timestamp(((Date) f.get(o)).getTime()));
					} catch (ClassCastException e) {
						// pst.setTimestamp(i, new java.sql.Timestamp(((Date)
						// f.get(o)).getTime()));
						pst.setObject(i, f.get(o));
					}
				} else if (f.getType() == String.class) {
					pst.setString(i, (String) f.get(o));
				} else {
					pst.setObject(i, f.get(o));
				}
			}
			if (id != null) {
				pst.setObject(i + 1, id.get(o));
			}
		} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getDeleteSql(Object o) {
		String table = o.getClass().getSimpleName();
		if (o.getClass().isAnnotationPresent(Table.class)) {
			table = o.getClass().getAnnotation(Table.class).name();
		}
		String idName = "";
		Object idValue = null;
		for (Field f : o.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Id.class)) {
				f.setAccessible(true);
				if (f.isAnnotationPresent(Column.class)) {
					idName = f.getAnnotation(Column.class).name();
				} else {
					idName = f.getName();
				}
				try {
					idValue = f.get(o);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return ("delete from " + table + " where " + idName + "=" + idValue);
	}

	public static <T> String getInsertsSql(List<T> os) {
		if (os == null || os.isEmpty())
			return "";
		Object o = os.get(0);
		Field fs[] = o.getClass().getDeclaredFields();
		String tabName;
		if (o.getClass().isAnnotationPresent(Table.class)) {
			tabName = o.getClass().getAnnotation(Table.class).name();
		} else {
			tabName = o.getClass().getSimpleName();
		}
		StringBuilder sql = new StringBuilder("insert into ").append(tabName).append(" (");
		StringBuilder s1 = new StringBuilder();
		for (Field f : fs) {
			f.setAccessible(true);
			try {
				if (f.isAnnotationPresent(JoinColumn.class) && f.isAnnotationPresent(ManyToOne.class)) {
				} else if (f.isAnnotationPresent(OneToMany.class)) {
				} else if (f.isAnnotationPresent(Transient.class) || !f.getAnnotation(Column.class).insertable()) {
				} else if (f.isAnnotationPresent(Column.class)) {
					s1.append(f.getAnnotation(Column.class).name()).append(",");
				} else {
					s1.append(f.getName()).append(",");
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		String sq = s1.toString();
		sql.append(sq.substring(0, sq.lastIndexOf(","))).append(")values");
		for (Object t : os) {
			sql.append("(");
			StringBuilder s0 = new StringBuilder();
			for (Field f : t.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				try {
					if (f.isAnnotationPresent(JoinColumn.class) && f.isAnnotationPresent(ManyToOne.class)) {
					} else if (f.isAnnotationPresent(OneToMany.class)) {
					} else if (f.isAnnotationPresent(Transient.class) || !f.getAnnotation(Column.class).insertable()) {
					} else if (f.isAnnotationPresent(Column.class)) {
						s0.append(SqlUtil.setSqlValue(o, f)).append(",");
					} else {
						s0.append(SqlUtil.setSqlValue(o, f)).append(",");
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			sql.append(s0.toString().substring(0, s0.length() - 1));
			sql.append("),");
		}
		sq = sql.toString();
		sql = new StringBuilder(sq.substring(0, sq.lastIndexOf(",")));
		return sql.toString();
	}

}
