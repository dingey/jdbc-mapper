package com.di.jdbc.mapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public interface JdbcConfig {
	String getDriverClassName();

	String getUsername();

	String getPassword();

	String getUrl();

	int getInitSize();

	int getMaxSize();

	static class JdbcConfigImpl implements JdbcConfig {
		private String driver;
		private String url;
		private String username;
		private String password;
		private int initSize;
		private int maxSize;

		private JdbcConfigImpl() {
			super();
		}

		@Override
		public String getDriverClassName() {
			return driver;
		}

		@Override
		public String getUsername() {
			return username;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public String getUrl() {
			return url;
		}

		@Override
		public int getInitSize() {
			return initSize;
		}

		@Override
		public int getMaxSize() {
			return maxSize;
		}

	}

	public static JdbcConfig createDefault(String fileName) {
		Properties prop = new Properties();
		boolean b = false;
		Exception e = null;
		try {
			prop.load(new FileInputStream(fileName));
		} catch (IOException e1) {
			b = true;
			e = e1;
			try {
				prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
				b = false;
			} catch (NullPointerException e2) {
				b = true;
			} catch (IOException e3) {
				b = true;
				e = e3;
			}
		}
		if (b) {
			throw new RuntimeException(fileName + "(系统找不到指定的文件。)", e);
		}
		JdbcConfigImpl j = new JdbcConfigImpl();
		j.driver = prop.getProperty("driverClassName");
		j.url = prop.getProperty("url");
		j.username = prop.getProperty("username");
		j.password = prop.getProperty("password");
		j.initSize = Integer.valueOf(prop.getProperty("initPoolSize") == null ? "1" : prop.getProperty("initPoolSize"));
		j.maxSize = Integer.valueOf(prop.getProperty("maxPoolSize") == null ? "10" : prop.getProperty("maxPoolSize"));
		if (j.url == null)
			throw new RuntimeException("url is null or not found");
		if (j.username == null)
			throw new RuntimeException("username is null or not found");
		if (j.password == null)
			throw new RuntimeException("password is null or not found");
		return j;
	}
}
