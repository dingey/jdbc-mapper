package com.di.jdbc.mapper.core.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.di.jdbc.mapper.annotation.Column;
import com.di.jdbc.mapper.annotation.GeneratedValue;
import com.di.jdbc.mapper.annotation.Id;
import com.di.jdbc.mapper.annotation.Transient;
import com.di.jdbc.mapper.core.ObjectMapper;

public class ObjectMapperImpl extends PreparedStatementMapperImpl implements ObjectMapper {

	@Override
	public <T> int insert(T t) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(ModelUtil.table(t)).append(" ( ");
		StringBuilder cols = new StringBuilder();
		List<Object> args = new ArrayList<Object>();
		for (Field f : ModelUtil.getField(t.getClass())) {
			if (f.isAnnotationPresent(Transient.class) || (f.isAnnotationPresent(Column.class) && !f.getAnnotation(Column.class).insertable())) {
				continue;
			}
			sql.append(ModelUtil.column(f)).append(",");
			cols.append("?").append(",");
			try {
				Method m = ModelUtil.getMethod(f);
				if (m != null) {
					args.add(m.invoke(t, new Object[] {}));
				} else {
					args.add(f.get(t));
				}
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				args.add(null);
			}
		}
		sql.deleteCharAt(sql.length() - 1);
		if (cols.length() > 0) {
			cols.deleteCharAt(cols.length() - 1);
		}
		sql.append(" ) VALUES ( ").append(cols.toString()).append(" )");
		Connection c = connection();
		PreparedStatement ps = null;
		ResultSet res = null;
		try {
			Field id = ModelUtil.id(t.getClass());
			if (id != null && id.isAnnotationPresent(GeneratedValue.class)) {
				ps = c.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < args.size(); i++) {
					ps.setObject(i + 1, args.get(i));
				}
				boolean b = ps.execute();
				res = ps.getGeneratedKeys();
				if (res.next() && id != null) {
					Object v = BeanUtil.resultSet(res, id.getType());
					Method set = ModelUtil.setMethod(id);
					if (set != null) {
						set.invoke(t, v);
					} else {
						id.set(t, v);
					}
				}
				return b ? 1 : 0;
			} else {
				ps = c.prepareStatement(sql.toString());
				for (int i = 0; i < args.size(); i++) {
					ps.setObject(i + 1, args.get(i));
				}
				return ps.execute() ? 1 : 0;
			}
		} catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(sql.toString(), e);
		} finally {
			close(null, ps, null);
			returnConnection(c);
		}
	}

	@Override
	public <T> int insertSelective(T t) {
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ").append(ModelUtil.table(t)).append(" ( ");
		StringBuilder cols = new StringBuilder();
		List<Object> args = new ArrayList<Object>();
		for (Field f : ModelUtil.getField(t.getClass())) {
			if (f.isAnnotationPresent(Transient.class) || (f.isAnnotationPresent(Column.class) && !f.getAnnotation(Column.class).insertable())) {
				continue;
			}
			try {
				Method m = ModelUtil.getMethod(f);
				Object v = null;
				if (m != null) {
					v = m.invoke(t, new Object[] {});
				} else {
					v = f.get(t);
				}
				if (v == null) {
					continue;
				}
				args.add(f.get(t));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e1) {
			}
			sql.append(ModelUtil.column(f)).append(",");
			cols.append("?").append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		if (cols.length() > 0) {
			cols.deleteCharAt(cols.length() - 1);
		}
		sql.append(" ) VALUES ( ").append(cols.toString()).append(" )");
		Connection c = connection();
		PreparedStatement ps = null;
		ResultSet res = null;
		try {
			Field id = ModelUtil.id(t.getClass());
			if (id != null && id.isAnnotationPresent(GeneratedValue.class)) {
				ps = c.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);
				for (int i = 0; i < args.size(); i++) {
					ps.setObject(i + 1, args.get(i));
				}
				ps.execute();
				res = ps.getGeneratedKeys();
				if (res.next() && id != null) {
					Object v = BeanUtil.resultSet(res, id.getType());
					Method set = ModelUtil.setMethod(id);
					if (set != null) {
						set.invoke(t, v);
					} else {
						id.set(t, v);
					}
				}
			} else {
				ps = c.prepareStatement(sql.toString());
				for (int i = 0; i < args.size(); i++) {
					ps.setObject(i + 1, args.get(i));
				}
				return ps.execute() ? 1 : 0;
			}
		} catch (SQLException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(sql.toString(), e);
		} finally {
			close(null, ps, null);
			returnConnection(c);
		}
		return 0;
	}

	@Override
	public <T> int update(T t) {
		Field id = ModelUtil.id(t.getClass());
		if (id == null) {
			throw new RuntimeException(t.getClass().getName() + " no id define.");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(ModelUtil.table(t)).append(" SET ");
		List<Object> args = new ArrayList<Object>();
		for (Field f : ModelUtil.getField(t.getClass())) {
			if (f.isAnnotationPresent(Transient.class) || f.isAnnotationPresent(Id.class) || (f.isAnnotationPresent(Column.class) && !f.getAnnotation(Column.class).updatable())) {
				continue;
			}
			sql.append(ModelUtil.column(f)).append(" = ?").append(",");
			try {
				Method m = ModelUtil.getMethod(f);
				if (m != null) {
					m.invoke(t, new Object[] {});
				} else {
					args.add(f.get(t));
				}
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				args.add(null);
			}
		}
		sql.deleteCharAt(sql.length() - 1);
		Connection c = connection();
		PreparedStatement ps = null;
		try {
			sql.append(" WHERE ").append(ModelUtil.column(id)).append(" = ?");
			Method m = ModelUtil.getMethod(id);
			Object v = null;
			if (m != null) {
				v = m.invoke(t, new Object[] {});
			} else {
				v = id.get(t);
			}
			args.add(v);
			ps = c.prepareStatement(sql.toString());
			for (int i = 0; i < args.size(); i++) {
				ps.setObject(i + 1, args.get(i));
			}
			return ps.execute() ? 1 : 0;
		} catch (SQLException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(sql.toString(), e);
		} finally {
			close(null, ps, null);
			returnConnection(c);
		}
	}

	@Override
	public <T> int updateSelective(T t) {
		Field id = ModelUtil.id(t.getClass());
		if (id == null) {
			throw new RuntimeException(t.getClass().getName() + " no id define.");
		}
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(ModelUtil.table(t)).append(" SET ");
		List<Object> args = new ArrayList<Object>();
		for (Field f : ModelUtil.getField(t.getClass())) {
			if (f.isAnnotationPresent(Transient.class) || f.isAnnotationPresent(Id.class) || (f.isAnnotationPresent(Column.class) && !f.getAnnotation(Column.class).updatable())) {
				continue;
			}
			try {
				Method m = ModelUtil.getMethod(f);
				Object v = null;
				if (m != null) {
					v = m.invoke(t, new Object[] {});
				} else {
					v = f.get(t);
				}
				if (v == null) {
					continue;
				}
				args.add(v);
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e1) {
			}
			sql.append(ModelUtil.column(f)).append(" = ?").append(",");
		}
		sql.deleteCharAt(sql.length() - 1);
		Connection c = connection();
		PreparedStatement ps = null;
		try {
			sql.append(" WHERE ").append(ModelUtil.column(id)).append(" = ?");
			Method m = ModelUtil.getMethod(id);
			Object v = null;
			if (m != null) {
				v = m.invoke(t, new Object[] {});
			} else {
				v = id.get(t);
			}
			args.add(v);
			ps = c.prepareStatement(sql.toString());
			for (int i = 0; i < args.size(); i++) {
				ps.setObject(i + 1, args.get(i));
			}
			return ps.execute() ? 1 : 0;
		} catch (SQLException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(sql.toString(), e);
		} finally {
			close(null, ps, null);
			returnConnection(c);
		}
	}

	@Override
	public <T> T get(String sql, Class<T> resultClass) {
		return super.get(sql, resultClass);
	}

	@Override
	public <T> T get(String sql, Object[] args, Class<T> resultClass) {
		return super.get(sql, args, resultClass);
	}

	@Override
	public <T> List<T> list(String sql, Class<T> resultClass) {
		return super.list(sql, resultClass);
	}

	@Override
	public <T> List<T> list(String preSql, Object[] args, Class<T> resultClass) {
		return super.list(preSql, args, resultClass);
	}

}
