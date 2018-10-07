package com.di.jdbc.mapper.core.impl;

public class DbTypeUtil {
	static String get(String url) {
		String type = "";
		if (url.startsWith("jdbc:mysql")) {
			type = "com.mysql.jdbc.Driver";
		} else if (url.startsWith("jdbc:oracle")) {
			type = "oracle.jdbc.OracleDriver";
		} else if (url.startsWith("jdbc:postgresql")) {
			type = "org.postgresql.Driver";
		} else if (url.startsWith("jdbc:sqlserver")) {
			type = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if (url.startsWith("jdbc:db2")) {
			type = "com.ibm.db2.jcc.DB2Driver";
		} else if (url.startsWith("jdbc:sybase")) {
			type = "com.sysbase.jdbc.SybDriver";
		} else if (url.startsWith("jdbc:informix-sqli")) {
			type = "com.informix.jdbc.IfxDriver";
		}
		return type;
	}
}
