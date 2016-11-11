package com.di.jdbc.mapper.core;

import com.di.jdbc.mapper.util.ConnectionUtil;

/**
 * @author di
 */
public class AbstractMapper {
	String fileName = "jdbc.properties";

	public AbstractMapper() {
		this.init();
	}

	public AbstractMapper(String fileName) {
		this.fileName = fileName;
		this.init();
	}

	protected void init() {
		ConnectionUtil.init(getFileName());
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
