package com.di.jdbc.mapper.util;

/**
 * @author di
 */
public class DbTypeUtil {
	public static String get(String url) {
		String type = "";
		if (url.startsWith("jdbc:mysql")) {
			type = "mysql";
		} else if (url.startsWith("jdbc:oracle")) {
			type = "oracle";
		} else if (url.startsWith("jdbc:postgresql")) {
			type = "oracle";
		} else if (url.startsWith("jdbc:sqlserver")) {
			type = "sqlserver2005";
		} else if (url.startsWith("jdbc:microsoft:sqlserver")) {
			type = "sqlserver2000";
		} else if (url.startsWith("jdbc:db2")) {
			type = "db2";
		} else if (url.startsWith("jdbc:sybase")) {
			type = "sybase";
		}
		return type;
	}
}
