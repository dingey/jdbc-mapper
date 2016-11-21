package com.di.jdbc.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author di
 */
@Target({ java.lang.annotation.ElementType.TYPE, java.lang.annotation.ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
	public abstract String name();

	public abstract String catalog() default "";

	public abstract String schema() default "";

	public abstract UniqueConstraint[] uniqueConstraints() default {};

	public abstract Index[] indexes() default {};
}