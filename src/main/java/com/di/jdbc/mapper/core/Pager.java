package com.di.jdbc.mapper.core;

import java.util.List;

public interface Pager<T> {
	int getPageNum();

	int getPageSize();

	int getTotal();

	List<T> getList();
}
