package com.di.jdbc.mapper.core.impl;

import javax.sql.DataSource;

import com.di.jdbc.mapper.JdbcMapper;

public class JdbcMapperImpl extends NamedMapperImpl implements JdbcMapper {

	JdbcMapperImpl(DataSource dataSource) {
		super.dataSource = dataSource;
	}

	public static JdbcMapperImpl create(DataSource dataSource) {
		return new JdbcMapperImpl(dataSource);
	}
}
