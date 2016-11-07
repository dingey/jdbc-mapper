package com.di.jdbc.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author di
 */
@Target({ java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface JoinColumn {
	public abstract String name();

	public abstract String referencedColumnName();

	public abstract boolean unique();

	public abstract boolean nullable();

	public abstract boolean insertable();

	public abstract boolean updatable();

	public abstract String columnDefinition();

	public abstract String table();

	public abstract ForeignKey foreignKey();
}