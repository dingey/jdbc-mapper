package com.di.jdbc.mapper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * @author di
 */
public class JdbcConfig {
	private String driverClassName;
	private String username;
	private String password;
	private String url;
	private String fileName;
	private int initPoolSize;
	private int maxPoolSize;

	public JdbcConfig() {
		fileName = "jdbc.properties";
		this.init(fileName);
	}

	public JdbcConfig(String fileName) {
		this.init(fileName);
	}

	public void init(String fileName) {
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
			return;
		}
		this.fileName = fileName;
		this.driverClassName = prop.getProperty("driverClassName");
		this.url = prop.getProperty("url");
		this.username = prop.getProperty("username");
		this.password = prop.getProperty("password");
		this.initPoolSize = Integer
				.valueOf(prop.getProperty("initPoolSize") == null ? "1" : prop.getProperty("initPoolSize"));
		this.maxPoolSize = Integer
				.valueOf(prop.getProperty("maxPoolSize") == null ? "5" : prop.getProperty("maxPoolSize"));
		if (driverClassName == null)
			System.err.println("driverClassName is null or not found");
		if (url == null)
			System.err.println("url is null or not found");
		if (username == null)
			System.err.println("username is null or not found");
		if (password == null)
			System.err.println("password is null or not found");
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getInitPoolSize() {
		return initPoolSize;
	}

	public void setInitPoolSize(int initPoolSize) {
		this.initPoolSize = initPoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

}
