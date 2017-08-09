package com.di.jdbc.mapper.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.di.jdbc.mapper.annotation.Column;
import com.di.jdbc.mapper.annotation.Id;
import com.di.jdbc.mapper.annotation.Transient;

/**
 * @author d
 */
public class CacheMapper {
	static final HashMap<String, Row> cacheMap = new HashMap<>();
	static final HashMap<String, String> sqlMap = new HashMap<>();

	public static <T> String getCacheSql(String key, T t, boolean cachable, Func<T> func) {
		if (!cachable) {
			return func.apply(t);
		}
		String id = t.getClass().getName() + "#" + key;
		if (!sqlMap.containsKey(id)) {
			String apply = func.apply(t);
			sqlMap.put(id, apply);
		}
		return sqlMap.get(id);
	}

	public static <T> Row getCacheRowMapper(Class<T> t) {
		if (!cacheMap.containsKey(t.getName())) {
			Row r = new Row();
			r.setName(Camel.toUnderline(t.getSimpleName()));
			List<Col> cols = new ArrayList<>();
			List<Col> alls = new ArrayList<>();
			for (Field f : ReflectUtil.getCommonFields(t)) {
				if (f.isAnnotationPresent(Id.class)) {
					Col c = new Col();
					c.setField(f);
					if (f.isAnnotationPresent(Column.class)) {
						c.setName(f.getAnnotation(Column.class).name());
					} else {
						c.setName(Camel.toUnderline(f.getName()));
					}
					r.setId(c);
					alls.add(c);
				} else if (f.isAnnotationPresent(Column.class)) {
					Col c = new Col(f.getAnnotation(Column.class).name(), f);
					cols.add(c);
					alls.add(c);
				} else if (!f.isAnnotationPresent(Transient.class)) {
					Col c = new Col(Camel.toUnderline(f.getName()), f);
					cols.add(c);
					alls.add(c);
				}
			}
			r.setCols(cols);
			r.setAlls(alls);
			cacheMap.put(t.getName(), r);
		}
		return cacheMap.get(t.getName());
	}

	public static class Row {
		private String name;
		private Col id;
		private List<Col> cols;
		private List<Col> alls;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Col getId() {
			return id;
		}

		public void setId(Col id) {
			this.id = id;
		}

		public List<Col> getCols() {
			return cols;
		}

		public void setCols(List<Col> cols) {
			this.cols = cols;
		}

		public List<Col> getAlls() {
			return alls;
		}

		public void setAlls(List<Col> alls) {
			this.alls = alls;
		}

	}

	public static class Col {
		public Col() {
		}

		public Col(String name, Field field) {
			super();
			this.name = name;
			this.field = field;
		}

		private String name;
		private Field field;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Field getField() {
			return field;
		}

		public void setField(Field field) {
			this.field = field;
		}

	}

	public interface Func<T> {
		String apply(T t);
	}
}
