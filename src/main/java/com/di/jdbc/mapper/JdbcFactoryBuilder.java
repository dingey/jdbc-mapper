package com.di.jdbc.mapper;

import java.util.Map;

import javax.sql.DataSource;

import com.di.jdbc.mapper.core.impl.JdbcMapperImpl;

public class JdbcFactoryBuilder implements JdbcFactory {
    @SuppressWarnings("unused")
    private static final String DATA_SOURCE_TYPE_NAMES[] = {"org.apache.tomcat.jdbc.pool.DataSource", "com.zaxxer.hikari.HikariDataSource",
            "org.apache.commons.dbcp.BasicDataSource", "org.apache.commons.dbcp2.BasicDataSource"};
    @SuppressWarnings("unused")
    private Map<?, ?> properties;
    DataSource dataSource;

    JdbcFactoryBuilder(DataSource dataSource) {
        if (dataSource == null) {
            // create default dataSource
        } else {
            this.dataSource = dataSource;
        }
    }

    @Override
    public JdbcMapper getMapper() {
        return JdbcMapperImpl.create(dataSource);
    }

}
