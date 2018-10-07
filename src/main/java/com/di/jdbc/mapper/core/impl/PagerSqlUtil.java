package com.di.jdbc.mapper.core.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

public class PagerSqlUtil {
	static ThreadLocal<Integer> pageArgs1 = new ThreadLocal<>();
	static ThreadLocal<Integer> pageArgs2 = new ThreadLocal<>();

	public static String getPageSql(String sql, int PageNum, int pageSize, Connection con) {
		try {
			String sqlType = con.getMetaData().getDatabaseProductName();
			StringBuilder s = new StringBuilder();
			switch (sqlType) {
			case "MySQL":
				s.append(sql).append(" limit ").append((PageNum - 1) * pageSize).append(",").append(pageSize);
				break;
			case "Oracle":
				s.append("select * from ( select tmp_page.*, rownum row_id from ( ").append(sql);
				s.append(" ) tmp_page where rownum <= ").append(PageNum * pageSize).append(" ) where row_id >").append((PageNum - 1) * pageSize);
				break;
			default:
				s.append(sql);
				break;
			}
			return s.toString();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	public static String getPreparePageSql(String sql, Object[] args, int PageNum, int pageSize, Connection con) {
		try {
			String sqlType = con.getMetaData().getDatabaseProductName();
			StringBuilder s = new StringBuilder();
			switch (sqlType) {
			case "MySQL":
				s.append(sql).append(" limit ?,?");
				setPageArgs((PageNum - 1) * pageSize, pageSize);
				break;
			case "Oracle":
				s.append("select * from ( select tmp_page.*, rownum row_id from ( ").append(sql);
				s.append(" ) tmp_page where rownum <= ? ) where row_id >?");
				setPageArgs(PageNum * pageSize, (PageNum - 1) * pageSize);
				break;
			default:
				s.append(sql);
				break;
			}
			return s.toString();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private static void setPageArgs(int args1, int args2) {
		pageArgs1.set(args1);
		pageArgs2.set(args2);
	}

	static Object[] getPageArgs(Object[] args) {
		if (args == null) {
			return new Integer[] { pageArgs1.get(), pageArgs2.get() };
		} else {
			Object[] tmp = Arrays.copyOf(args, args.length + 2);
			tmp[tmp.length - 2] = pageArgs1.get();
			tmp[tmp.length - 1] = pageArgs2.get();
			return tmp;
		}
	}

}
