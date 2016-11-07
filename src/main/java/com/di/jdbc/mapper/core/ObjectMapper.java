package com.di.jdbc.mapper.core;

import java.sql.Connection;
import java.util.List;

import com.di.jdbc.mapper.util.ConnectionUtil;
import com.di.jdbc.mapper.util.MillionUtil;
import com.di.jdbc.mapper.util.ObjectMapperUtil;

/**
 * @author di
 */
public class ObjectMapper extends PrepareStatementMapper {
	ObjectMapper() {
		super();
	}

	ObjectMapper(String fileName) {
		super(fileName);
	}

	public <T> void insert(T o) {
		Connection c = ConnectionUtil.getConn(fileName);
		ObjectMapperUtil.insertPrepareSql(o, c, false);
		ConnectionUtil.returnConn(fileName, c);
	}

	public <T> void insertReturnKey(T o) {
		Connection c = ConnectionUtil.getConn(fileName);
		ObjectMapperUtil.insertSql(o, c, false);
		ConnectionUtil.returnConn(fileName, c);
	}

	public <T> void update(T o) {
		Connection c = ConnectionUtil.getConn(fileName);
		ObjectMapperUtil.updatePrepareSql(o, c, false);
		ConnectionUtil.returnConn(fileName, c);
	}

	public <T> void insertMillionObjects(List<T> os, int sqlSize,int batchSize) {
		Connection c = ConnectionUtil.getConn(fileName);
		MillionUtil.insertsPreSql(os,c,sqlSize,batchSize);
		ConnectionUtil.returnConn(fileName, c);
		c = null;
	}
}
