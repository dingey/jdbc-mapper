package com.di.jdbc.mapper.util;

/**
 * @author di
 */
public class SqlParam {
	private String preSql;
	private Object[] args;

	public SqlParam() {
	}

	public SqlParam(String preSql, Object[] args) {
		super();
		this.preSql = preSql;
		this.args = args;
	}

	public String getPreSql() {
		return preSql;
	}

	public void setPreSql(String preSql) {
		this.preSql = preSql;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

}
