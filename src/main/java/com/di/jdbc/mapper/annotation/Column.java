package com.di.jdbc.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author di
 */
@Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
	public abstract String name();

	public abstract boolean unique() default true;

	public abstract boolean nullable() default true;

	public abstract boolean insertable() default true;

	public abstract boolean updatable() default true;

	public abstract String columnDefinition() default "";

	public abstract String table() default "";

	public abstract int length() default 64;

	public abstract int precision() default 2;

	public abstract int scale() default 0;
}