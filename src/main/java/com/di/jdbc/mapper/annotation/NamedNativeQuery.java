package com.di.jdbc.mapper.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author di
 */
@Target({ java.lang.annotation.ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NamedNativeQuery {
	public abstract String name();

	public abstract String query();

	public abstract QueryHint[] hints() default {};

	public abstract Class<?> resultClass() default Class.class;

	public abstract String resultSetMapping() default "";
}
