package com.di.jdbc.mapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
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

	static JdbcConfig createDefault(String fileName) {
		Properties prop = new Properties();
		String path = "";
		try {
			path = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		path = path + fileName;

		try {
			prop.load(new FileInputStream(new File(path)));
		} catch (FileNotFoundException e) {
			System.err.println(path + " not found");
			try {
				if (path.indexOf("test-classes") != -1) {
					path = path.replaceFirst("test-classes", "classes");
				}
				prop.load(new FileInputStream(new File(path)));
			} catch (IOException e1) {
				System.err.println(path + " not found");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		JdbcConfigImpl j = new JdbcConfigImpl();
		j.driver = prop.getProperty("driverClassName");
		j.url = prop.getProperty("url");
		j.username = prop.getProperty("username");
		j.password = prop.getProperty("password");
		j.initSize = Integer.valueOf(prop.getProperty("initPoolSize") == null ? "1" : prop.getProperty("initPoolSize"));
		j.maxSize = Integer.valueOf(prop.getProperty("maxPoolSize") == null ? "10" : prop.getProperty("maxPoolSize"));
		if (j.url == null)
			System.err.println("url is null or not found");
		if (j.username == null)
			System.err.println("username is null or not found");
		if (j.password == null)
			System.err.println("password is null or not found");
		return j;
	}
}
