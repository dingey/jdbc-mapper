package com.di.jdbc.mapper.util;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
 */
public class ObjectMapperUtil {
	public static <T> boolean insertPrepareSql(T o, Connection c, boolean ignoreNull) {
		boolean b = false;
		StringBuilder s = new StringBuilder("insert into ");
		if (o.getClass().isAnnotationPresent(Table.class)) {
			s.append(o.getClass().getAnnotation(Table.class).name());
		} else {
			s.append(o.getClass().getSimpleName());
		}
		s.append("(");
		List<Object> as = new ArrayList<>();
		Field id = null;
		try {
			int count = 0;
			for (Field f : o.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (ignoreNull) {
					if (f.get(o) == null) {
						continue;
					}
				}
				if (f.isAnnotationPresent(Id.class)) {
					id = f;
				}
				if (f.isAnnotationPresent(Column.class)) {
					s.append(f.getAnnotation(Column.class).name()).append(",");
					count++;
					as.add(f.get(o));
				} else {
					s.append(f.getName()).append(",");
					count++;
					as.add(f.get(o));
				}
			}
			s = new StringBuilder(s.toString().substring(0, s.length() - 1));
			s.append(")values(");
			for (int i = 0; i < count - 1; i++) {
				s.append("?").append(",");
			}
			s.append("?)");
			PreparedStatement ps = null;
			ResultSet res = null;
			try {
				ps = c.prepareStatement(s.toString(), Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < as.size(); i++) {
					ps.setObject(i + 1, as.get(i));
				}
				b = ps.execute();
				res = ps.getGeneratedKeys();
				if (res.next() && id != null) {
					SqlUtil.setFieldValue(o, id, res);
				}
			} catch (SQLException e) {
				try {
					c.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.err.println("sql:" + s.toString());
				e.printStackTrace();
			} finally {
				try {
					if (res != null) {
						res.close();
						res = null;
					}
					if (ps != null) {
						ps.close();
						ps = null;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			System.err.println(s.toString());
			e.printStackTrace();
		}
		return b;
	}

	public static <T> boolean updatePrepareSql(T o, Connection c, boolean ignoreNull) {
		boolean b = false;
		StringBuilder s = new StringBuilder("update ");
		if (o.getClass().isAnnotationPresent(Table.class)) {
			s.append(o.getClass().getAnnotation(Table.class).name());
		} else {
			s.append(o.getClass().getSimpleName());
		}
		s.append(" set ");
		List<Object> as = new ArrayList<>();
		Field id = null;
		try {
			for (Field f : o.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (ignoreNull) {
					if (f.get(o) == null) {
						continue;
					}
				}
				if (f.isAnnotationPresent(Id.class)) {
					id = f;
					continue;
				}
				if (f.isAnnotationPresent(Column.class)) {
					s.append(f.getAnnotation(Column.class).name()).append("=?,");
					as.add(f.get(o));
				} else {
					s.append(f.getName()).append("=?,");
					as.add(f.get(o));
				}
			}
			s = new StringBuilder(s.toString().substring(0, s.length() - 1));
			s.append(" where ");
			if (id.isAnnotationPresent(Column.class)) {
				s.append(id.getAnnotation(Column.class).name()).append("=?");
			} else {
				s.append(id.getName()).append("=?");
			}
			as.add(id.get(o));
			PreparedStatement ps = null;
			try {
				System.out.println(s.toString());
				ps = c.prepareStatement(s.toString());
				for (int i = 0; i < as.size(); i++) {
					ps.setObject(i + 1, as.get(i));
				}
				b = ps.execute();
			} catch (SQLException e) {
				try {
					c.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				System.err.println("sql:" + s.toString());
				e.printStackTrace();
			} finally {
				try {
					if (ps != null) {
						ps.close();
						ps = null;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			System.err.println(s.toString());
			e.printStackTrace();
		}
		return b;
	}

	public static <T> boolean insertSql(T o, Connection c, boolean ignoreNull) {
		boolean b = false;
		Field fs[] = o.getClass().getDeclaredFields();
		String tabName;
		if (o.getClass().isAnnotationPresent(Table.class)) {
			tabName = o.getClass().getAnnotation(Table.class).name();
		} else {
			tabName = o.getClass().getSimpleName();
		}
		StringBuilder sql = new StringBuilder("insert into ").append(tabName).append(" (");
		StringBuilder s1 = new StringBuilder();
		Field idField = null;
		for (Field f : fs) {
			f.setAccessible(true);
			try {
				if (f.isAnnotationPresent(Id.class)) {
					idField = f;
				}
				if (f.get(o) == null) {
					continue;
				}
			} catch (IllegalArgumentException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
			try {
				if (f.isAnnotationPresent(JoinColumn.class) && f.isAnnotationPresent(ManyToOne.class)) {
				} else if (f.isAnnotationPresent(OneToMany.class)) {
				} else if (f.isAnnotationPresent(Transient.class)) {
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
				} else if (f.isAnnotationPresent(Transient.class)) {
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
		Statement st = null;
		ResultSet res = null;
		try {
			st = c.createStatement();
			b=st.execute(sql.toString(), Statement.RETURN_GENERATED_KEYS);
			res = st.getGeneratedKeys();
			if (res.next() && idField != null) {
				SqlUtil.setFieldValue(o, idField, res);
			}
		} catch (SQLException | IllegalArgumentException e) {
			try {
				c.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.err.println("sql:" + sql.toString());
			e.printStackTrace();
		} finally {
			try {
				if (res != null) {
					res.close();
					res = null;
				}
				if (st != null) {
					st.close();
					st = null;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return b;
	}
}
