package com.di.jdbc.mapper.core;

/**
 * @author di
 */
public class JdbcMapper extends TransactionMapper {
	public JdbcMapper() {
		super();
	}

	public JdbcMapper(String fileName) {
		super(fileName);
	}
}
