package com.di.jdbc.mapper;

import javax.sql.DataSource;

public interface JdbcFactory {
    JdbcMapper getMapper();

    public static JdbcFactory build(DataSource dataSource) {
        return new JdbcFactoryBuilder(dataSource);
    }
}
