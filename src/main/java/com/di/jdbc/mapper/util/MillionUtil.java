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
import com.di.jdbc.mapper.annotation.Table;

/**
 * @author di
 */
public class MillionUtil {
	public static <T> void insertsPreSql(List<T> os, Connection c,int sqlSize,int batchSize) {
		if(sqlSize>os.size()){
			System.err.println("error : sqlsize can't large than list.size()");
			return;
		}
		T o = os.get(0);
		StringBuilder s = new StringBuilder("insert into ");
		if (o.getClass().isAnnotationPresent(Table.class)) {
			s.append(o.getClass().getAnnotation(Table.class).name());
		} else {
			s.append(o.getClass().getSimpleName());
		}
		s.append("(");
		try {
			int count = 0;
			List<String> fs = new ArrayList<>();
			for (Field f : o.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (f.isAnnotationPresent(Column.class)) {
					s.append(f.getAnnotation(Column.class).name()).append(",");
					fs.add(f.getName());
					count++;
				} else {
					s.append(f.getName()).append(",");
					fs.add(f.getName());
					count++;
				}
			}
			s = new StringBuilder(s.toString().substring(0, s.length() - 1));
			s.append(")values");
			for (int j = 0; j < sqlSize - 1; j++) {
				s.append("(");
				for (int i = 0; i < count - 1; i++) {
					s.append("?").append(",");
				}
				s.append("?),");
			}
			s.append("(");
			for (int i = 0; i < count - 1; i++) {
				s.append("?").append(",");
			}
			s.append("?)");
			PreparedStatement ps = null;			
			int offset0 = 0;
			while (offset0 < os.size()) {
				ps = c.prepareStatement(s.toString());
				int offset = 0;
				List<T> os0=os.subList(offset0, (offset0+sqlSize)>os.size()?os.size():(offset0+sqlSize));
				for (T t : os0) {
					for (int i = 0; i < fs.size(); i++) {
						Field f = t.getClass().getDeclaredField(fs.get(i));
						f.setAccessible(true);
						offset++;
						ps.setObject(offset, f.get(t));
					}					
				}
				ps.addBatch();
				offset0+=sqlSize;
				ps.executeBatch();
			}
			if (ps != null) {
				ps.close();
				ps = null;
			}
		} catch (IllegalArgumentException | SQLException | NoSuchFieldException | SecurityException
				| IllegalAccessException e) {
			System.err.println(s.toString());
			e.printStackTrace();
		}
	}

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

}
