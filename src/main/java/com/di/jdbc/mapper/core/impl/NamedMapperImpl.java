package com.di.jdbc.mapper.core.impl;

import java.util.List;

import com.di.jdbc.mapper.annotation.NamedNativeQueries;
import com.di.jdbc.mapper.annotation.NamedNativeQuery;
import com.di.jdbc.mapper.core.NamedMapper;
import com.di.jdbc.mapper.core.Pager;

public class NamedMapperImpl extends PagerMapperImpl implements NamedMapper {

	@Override
	public <T> int executeByNamedQuery(String namedQueryName, Class<T> namedClass, Object... args) {
		return super.execute(getNameQuery(namedQueryName, namedClass).query(), args);
	}

	@Override
	public <T> T getByNamedQuery(String namedQueryName, Class<T> namedClass, Object... args) {
		NamedNativeQuery q = getNameQuery(namedQueryName, namedClass);
		return super.get(q.query(), namedClass, args);
	}

	@Override
	public <T> List<T> listByNamedQuery(String namedQueryName, Class<T> namedClass, Object... args) {
		NamedNativeQuery q = getNameQuery(namedQueryName, namedClass);
		return super.list(q.query(), namedClass, args);
	}

	@Override
	public <T> Pager<T> pageByNamedQuery(String namedQueryName, int pageNum, int pageSize, Class<T> namedClass, Object... args) {
		NamedNativeQuery q = getNameQuery(namedQueryName, namedClass);
		return super.page(q.query(), pageNum, pageSize, namedClass, args);
	}

	private <T> NamedNativeQuery getNameQuery(String namedQueryName, Class<T> namedClass) {
		NamedNativeQuery nq = null;
		if (namedClass.isAnnotationPresent(NamedNativeQueries.class)) {
			for (NamedNativeQuery q : namedClass.getAnnotation(NamedNativeQueries.class).value()) {
				if (q.name().equals(namedQueryName)) {
					nq = q;
					break;
				}
			}
		} else if (namedClass.isAnnotationPresent(NamedNativeQuery.class)) {
			NamedNativeQuery query = namedClass.getAnnotation(NamedNativeQuery.class);
			if (query.name().equals(namedQueryName)) {
				nq = query;
			}
		}
		if (nq == null) {
			throw new RuntimeException(namedQueryName + " not found in Class " + namedClass.getName() + ".");
		}
		return nq;
	}
}
