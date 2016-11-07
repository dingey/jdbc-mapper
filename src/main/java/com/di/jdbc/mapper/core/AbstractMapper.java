package com.di.jdbc.mapper.core;

import com.di.jdbc.mapper.util.ConnectionUtil;

/**
 * @author di
 */
public class AbstractMapper {
	String fileName = "jdbc.properties";

	public AbstractMapper() {
		this.init(fileName);
	}

	public AbstractMapper(String fileName) {
		this.fileName = fileName;
		this.init(fileName);
	}

	protected void init(String fileName) {
		ConnectionUtil.init(fileName);
	}
}
