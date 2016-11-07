package com.di.jdbc.mapper.util;

/**
 * @author di
 */
public class PagerSqlUtil {
	public static String getPageSql(String sql, int PageNum, int pageSize, String fileName) {
		String sqlType = ConnectionUtil.sqlTypes.get(fileName);
		StringBuilder s = new StringBuilder();
		switch (sqlType) {
		case "mysql":
			s.append(sql).append(" limit ").append((PageNum - 1) * pageSize).append(",").append(pageSize);
			break;
		case "oracle":
			s.append("select * from ( select tmp_page.*, rownum row_id from ( ").append(sql);
			s.append(" ) tmp_page where rownum <= ").append(PageNum * pageSize).append(" ) where row_id >")
					.append((PageNum - 1) * pageSize);
			break;
		default:
			s.append(sql);
			break;
		}
		return s.toString();
	}

	public static String getPreparePageSql(String sql,int PageNum, int pageSize, String fileName) {
		String sqlType = ConnectionUtil.sqlTypes.get(fileName);
		StringBuilder s = new StringBuilder();
		switch (sqlType) {
		case "mysql":
			s.append(sql).append(" limit ").append((PageNum - 1) * pageSize).append(",").append(pageSize);
			break;
		case "oracle":
			s.append("select * from ( select tmp_page.*, rownum row_id from ( ").append(sql);
			s.append(" ) tmp_page where rownum <= ").append(PageNum * pageSize).append(" ) where row_id >")
					.append((PageNum - 1) * pageSize);
			break;
		default:
			s.append(sql);
			break;
		}
		return s.toString();
	}
}
