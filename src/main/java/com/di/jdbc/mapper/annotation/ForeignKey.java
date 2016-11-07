package com.di.jdbc.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author di
 */
@Target({})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {
	public abstract String name();

	public abstract String foreignKeyDefinition();

	public abstract ConstraintMode value();
}