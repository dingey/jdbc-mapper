package com.di.jdbc.mapper.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.di.jdbc.mapper.annotation.Table;

/**
 * @author di
 */
public class ExampleUtil {
	public static String whereExampleSql(Object e, Class<?> t) {
		StringBuilder s = new StringBuilder();
		try {
			Field oredCriteria = e.getClass().getDeclaredField("oredCriteria");
			oredCriteria.setAccessible(true);
			List<?> ors = (List<?>) oredCriteria.get(e);
			int i = 0;
			if (ors.size() > 0) {
				for (Object o : ors) {
					i++;
					Method getCriteria = o.getClass().getDeclaredMethod("getCriteria");
					getCriteria.setAccessible(true);
					Object invoke = getCriteria.invoke(o);
					List<?> cs = (List<?>) invoke;
					if (!cs.isEmpty()) {
						s.append("(");
						for (int j = 0; j < cs.size(); j++) {
							Object c = cs.get(j);
							Field fd = c.getClass().getDeclaredField("condition");
							fd.setAccessible(true);
							Field value = c.getClass().getDeclaredField("value");
							Field secondValue = c.getClass().getDeclaredField("secondValue");
							Field noValue = c.getClass().getDeclaredField("noValue");
							Field singleValue = c.getClass().getDeclaredField("singleValue");
							Field betweenValue = c.getClass().getDeclaredField("betweenValue");
							Field listValue = c.getClass().getDeclaredField("listValue");
							value.setAccessible(true);
							secondValue.setAccessible(true);
							noValue.setAccessible(true);
							singleValue.setAccessible(true);
							betweenValue.setAccessible(true);
							listValue.setAccessible(true);
							if (noValue != null && noValue.getBoolean(c) == true) {
								s.append(fd.get(c)).append(" ");
							} else if (singleValue != null && singleValue.getBoolean(c) == true) {
								s.append(fd.get(c)).append(" ").append(value.get(c)).append(" ");
							} else if (betweenValue != null && betweenValue.getBoolean(c) == true) {
								s.append(fd.get(c)).append(" ").append(value.get(c));
								s.append(secondValue.get(c)).append(" ");
							} else if (listValue != null && listValue.getBoolean(c) == true) {
								s.append(fd.get(c)).append(" ");
								List<?> vals = (List<?>) value.get(c);
								if (!vals.isEmpty()) {
									s.append("(");
									s.append(ListUtil.listValueToString(vals, ","));
									s.append(")");
								}
							}
							if (j < cs.size() - 1) {
								s.append(" and ");
							}
						}
						s.append(")");
					}
					if (i < ors.size()) {
						s.append(" or ");
					}
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException e1) {
			e1.printStackTrace();
		}
		return s.toString();
	}

	public static String selectByExample(Object e, Class<?> t) {
		StringBuilder s = new StringBuilder();
		try {
			Field orderByClause = e.getClass().getDeclaredField("orderByClause");
			Field distinct = e.getClass().getDeclaredField("distinct");
			Field oredCriteria = e.getClass().getDeclaredField("oredCriteria");
			orderByClause.setAccessible(true);
			distinct.setAccessible(true);
			oredCriteria.setAccessible(true);
			String table = t.getSimpleName();
			if (t.isAnnotationPresent(Table.class)) {
				table = t.getDeclaredAnnotation(Table.class).name();
			}
			s.append("select * from ").append(table).append(" where 1=1 and ");
			s.append(whereExampleSql(e, t));
			s.append(orderByClause.get(e));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		return s.toString();
	}

	public static String countByExample(Object e, Class<?> t) {
		StringBuilder s = new StringBuilder();
		try {
			Field orderByClause = e.getClass().getDeclaredField("orderByClause");
			Field distinct = e.getClass().getDeclaredField("distinct");
			Field oredCriteria = e.getClass().getDeclaredField("oredCriteria");
			orderByClause.setAccessible(true);
			distinct.setAccessible(true);
			oredCriteria.setAccessible(true);
			String table = t.getSimpleName();
			if (t.isAnnotationPresent(Table.class)) {
				table = t.getDeclaredAnnotation(Table.class).name();
			}
			s.append("select count(0) from ").append(table).append(" where 1=1 and ");
			s.append(whereExampleSql(e, t));
			s.append(orderByClause.get(e));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		return s.toString();
	}
	public static <T> String deleteByExample(Object e,Class<T> t){
		StringBuilder s = new StringBuilder();
		try {
			Field orderByClause = e.getClass().getDeclaredField("orderByClause");
			Field distinct = e.getClass().getDeclaredField("distinct");
			Field oredCriteria = e.getClass().getDeclaredField("oredCriteria");
			orderByClause.setAccessible(true);
			distinct.setAccessible(true);
			oredCriteria.setAccessible(true);
			String table = t.getSimpleName();
			if (t.isAnnotationPresent(Table.class)) {
				table = t.getDeclaredAnnotation(Table.class).name();
			}
			s.append("delete from ").append(table).append(" where 1=1 and ");
			s.append(whereExampleSql(e, t));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e1) {
			e1.printStackTrace();
		}
		return s.toString();
	}
}
