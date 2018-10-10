package com.di.jdbc.mapper;

import javax.sql.DataSource;

import com.di.jdbc.mapper.core.NamedMapper;

public interface JdbcMapper extends NamedMapper {
	public static JdbcMapper build(DataSource dataSource) {
		return JdbcFactory.build(dataSource).getMapper();
	}
}