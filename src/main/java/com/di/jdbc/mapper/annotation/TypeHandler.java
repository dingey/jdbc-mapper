package com.di.jdbc.mapper.annotation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author d
 */
public interface TypeHandler<T> {

    public void setNonNullParameter(PreparedStatement preparedStatement, int columnIndex, T value);

    public Object getNullableResult(ResultSet resultSet, String columnName) throws SQLException;

    public Object getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException;

    public static class BaseTypeHandler<T> implements TypeHandler<T> {

	public Object getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
	    return resultSet.getObject(columnName);
	}

	public Object getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
	    return resultSet.getObject(columnIndex);
	}

	@Override
	public void setNonNullParameter(PreparedStatement preparedStatement, int columnIndex, T value) {
	    try {
		preparedStatement.setObject(columnIndex, value);
	    } catch (SQLException e) {
		throw new RuntimeException(e);
	    }
	}
    }

}
