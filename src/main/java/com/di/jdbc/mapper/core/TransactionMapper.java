package com.di.jdbc.mapper.core;

import java.sql.SQLException;

public interface TransactionMapper {
    void begin() throws SQLException;

    void commit() throws SQLException;

    void rollback() throws SQLException;
}
