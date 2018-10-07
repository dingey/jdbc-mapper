package com.di.jdbc.mapper.annotation;

/**
 * @author d
 */
public @interface Table {
	public abstract String value() default "";

	public abstract String name() default "";
}
