package com.di.jdbc.mapper.core.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.di.jdbc.mapper.core.TransactionMapper;

public class TransactionMapperImpl implements TransactionMapper {

    ThreadLocal<Connection> con = new ThreadLocal<>();
    DataSource dataSource;

    TransactionMapperImpl() {
    }

    @Override
    public void begin() throws SQLException {
        if (con.get() == null) {
            Connection c = dataSource.getConnection();
            c.setAutoCommit(false);
            con.set(c);
        }
    }

    @Override
    public void commit() throws SQLException {
        Connection c = con.get();
        if (c != null) {
            c.commit();
            con.remove();
            c = null;
        }
    }

    @Override
    public void rollback() throws SQLException {
        Connection c = con.get();
        if (c != null) {
        	c.rollback();
            c.setAutoCommit(true);
            con.remove();
            c = null;
        }
    }

    Connection connection() {
        Connection c = con.get();
        if (c == null) {
            try {
                c = dataSource.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return c;
    }

    void returnConnection(Connection c) {
        if (con.get() == null) {
            try {
                c.close();
                c=null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            c = null;
        }
    }

    void close(Statement statement, PreparedStatement preparedStatement, ResultSet set) {
        try {
            if (set != null && !set.isClosed()) {
                set.close();
                set = null;
            }
            if (statement != null && !statement.isClosed()) {
                statement.close();
                statement = null;
            }
            if (preparedStatement != null && !preparedStatement.isClosed()) {
                preparedStatement.close();
                preparedStatement = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
