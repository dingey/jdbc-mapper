package com.di.jdbc.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author di
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
	public abstract String name();

	public abstract String columnList();

	public abstract boolean unique();
}