package com.di.jdbc.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author di
 */
@Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyToMany {
	public abstract Class<?> targetEntity();

	public abstract CascadeType[] cascade();

	public abstract FetchType fetch();

	public abstract String mappedBy();
}