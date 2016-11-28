package com.di.jdbc.mapper.core;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import com.di.jdbc.mapper.util.ConnectionUtil;
import com.di.jdbc.mapper.util.SqlUtil;

/**
 * @author di
 * @since 1.0
 */
public class TransactionMapper extends ObjectPagerMapper {
	Connection con;

	public TransactionMapper() {
		super();
	}

	public TransactionMapper(String fileName) {
		super(fileName);
	}

	public void beginTransaction() throws SQLException {
		con = ConnectionUtil.getConn(getFileName());
		con.setAutoCommit(false);
	}

	public void commit() throws SQLException {
		con.commit();
		con.setAutoCommit(true);
		ConnectionUtil.returnConn(getFileName(), con);
		con = null;
	}

	public void rollback() throws SQLException {
		con.rollback();
		con.setAutoCommit(true);
		ConnectionUtil.returnConn(getFileName(), con);
		con = null;
	}

	private Connection getCon() {
		if (con == null) {
			con = ConnectionUtil.getConn(getFileName());
		}
		return con;
	}

	public void executeWithTransaction(String sql) throws SQLException {
		con = getCon();
		Statement st = con.createStatement();
		st.execute(sql);
		if (st != null) {
			st.close();
			st = null;
		}
	}

	public <T> void insertWithTransaction(T t) throws SQLException {
		con = getCon();
		Statement st = con.createStatement();
		st.execute(SqlUtil.getInsertSelecitiveSql(t));
		if (st != null) {
			st.close();
			st = null;
		}
	}

	public void updateWithTransaction(Object o) throws SQLException {
		con = getCon();
		Statement st = con.createStatement();
		st.execute(SqlUtil.getUpdateSelecitiveSql(o));
		if (st != null) {
			st.close();
			st = null;
		}
	}

	public void deleteWithTransaction(Object o) throws SQLException {
		con = getCon();
		Statement st = con.createStatement();
		st.execute(SqlUtil.getDeleteSql(o));
		if (st != null) {
			st.close();
			st = null;
		}
	}
}
