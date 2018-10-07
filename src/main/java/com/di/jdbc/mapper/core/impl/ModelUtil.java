package com.di.jdbc.mapper.core.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.di.jdbc.mapper.annotation.Column;
import com.di.jdbc.mapper.annotation.Id;
import com.di.jdbc.mapper.annotation.Table;
import com.di.jdbc.mapper.annotation.Transient;
import com.di.jdbc.mapper.annotation.TypeHandler;
import com.di.jdbc.mapper.annotation.TypeHandler.BaseTypeHandler;

@SuppressWarnings("rawtypes")
public class ModelUtil {
	private static Map<Class<?>, List<Field>> FIELD = new HashMap<>();
	private static Map<Class<?>, Field> ID = new HashMap<>();
	private static Map<Field, Method> GET = new LinkedHashMap<>();
	private static Map<Field, Method> SET = new LinkedHashMap<>();
	private static Map<Field, String> FIELD_COLUMN = new LinkedHashMap<>();
	private static Map<Class<?>, Map<String, Field>> COLUMN_REF = new LinkedHashMap<>();
	private static Map<Field, TypeHandler> TYPE_HANDLE = new LinkedHashMap<>();

	static Field id(Class<?> cl) {
		Field id = ID.get(cl);
		if (id == null) {
			for (Field f : getField(cl)) {
				if (f.isAnnotationPresent(Id.class)) {
					ID.put(cl, f);
					id = f;
					break;
				}
			}
		}
		return id;
	}

	static Method getMethod(Field f) {
		return GET.get(f);
	}

	static Method setMethod(Field f) {
		return SET.get(f);
	}

	static String column(Field f) {
		return FIELD_COLUMN.get(f);
	}

	static Map<String, Field> columnRef(Class c, String column) {
		return COLUMN_REF.get(c);
	}

	static <T extends TypeHandler> TypeHandler typeHandler(Field f) {
		return TYPE_HANDLE.get(f);
	}

	public static List<Field> getDeclaredFields(Class<?> t) {
		List<Field> fields = new ArrayList<>();
		for (Class<?> clazz = t; clazz != Object.class && clazz != Class.class && clazz != Field.class; clazz = clazz.getSuperclass()) {
			try {
				for (Field f : clazz.getDeclaredFields()) {
					int modifiers = f.getModifiers();
					if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isNative(modifiers) && !Modifier.isTransient(modifiers)) {
						fields.add(f);
						String n = Character.toUpperCase(f.getName().charAt(0)) + f.getName().substring(1);
						if (f.getType() == boolean.class || f.getType() == Boolean.class) {
							GET.put(f, clazz.getDeclaredMethod("is" + n));
						} else {
							GET.put(f, clazz.getDeclaredMethod("get" + n));
						}
						SET.put(f, clazz.getDeclaredMethod("set" + n, f.getType()));
					}
				}
			} catch (Exception e) {
			}
		}
		return fields;
	}

	static List<Field> getField(Class<?> cl) {
		if (FIELD.get(cl) != null)
			return FIELD.get(cl);
		List<Field> fs = new ArrayList<>();
		Map<String, Field> cols = new HashMap<>();
		for (Field f : getDeclaredFields(cl)) {
			if (!f.isAccessible())
				f.setAccessible(true);
			if (f.isAnnotationPresent(Transient.class)) {
				continue;
			}
			if (f.isAnnotationPresent(Id.class)) {
				ID.put(cl, f);
			}
			fs.add(f);
			String column = snakeCase(f.getName());
			try {
				if (f.isAnnotationPresent(Column.class)) {
					Column ac = f.getAnnotation(Column.class);
					if (!ac.value().isEmpty()) {
						column = ac.value();
					} else if (!ac.name().isEmpty()) {
						column = ac.name();
					}
					if (ac.typeHandler() != BaseTypeHandler.class) {
						TypeHandler handler = ac.typeHandler().newInstance();
						TYPE_HANDLE.put(f, handler);
					}
				}
				cols.put(column, f);
				FIELD_COLUMN.put(f, column);

			} catch (SecurityException | InstantiationException | IllegalAccessException e) {
			}
		}
		FIELD.put(cl, fs);
		return fs;
	}

	public static String snakeCase(String camelCase) {
		if (camelCase != null && !camelCase.trim().isEmpty()) {
			char[] cs = camelCase.toCharArray();
			StringBuilder sb = new StringBuilder();
			sb.append(Character.toLowerCase(cs[0]));
			for (int i = 1; i < cs.length; i++) {
				if (Character.isUpperCase(cs[i])) {
					sb.append("_").append(Character.toLowerCase(cs[i]));
				} else {
					sb.append(camelCase.toCharArray()[i]);
				}
			}
			return sb.toString();
		} else {
			return camelCase;
		}
	}

	static <T> String table(T t) {
		if (t.getClass().isAnnotationPresent(Table.class) && !t.getClass().getAnnotation(Table.class).name().isEmpty()) {
			return t.getClass().getAnnotation(Table.class).name();
		}
		return snakeCase(t.getClass().getSimpleName());
	}
}
