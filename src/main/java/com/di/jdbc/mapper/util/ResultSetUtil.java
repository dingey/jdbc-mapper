package com.di.jdbc.mapper.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author di
 */
public class ResultSetUtil {
	public static List<HashMap<String, Object>> resultSetToMapList(ResultSet res) throws SQLException {
		List<HashMap<String, Object>> list = new ArrayList<>();
		while (res.next()) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			ResultSetMetaData rsmd = res.getMetaData();
			int columnCount = rsmd.getColumnCount();
			for (int i = 1; i <= columnCount; i++) {
				String colName = rsmd.getColumnLabel(i);
				if (colName == null) {
					colName = rsmd.getColumnName(i);
				}
				m.put(colName, res.getObject(i));
			}
			list.add(m);
		}
		return list;
	}
}
