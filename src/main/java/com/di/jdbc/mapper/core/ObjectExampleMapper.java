package com.di.jdbc.mapper.core;

import java.util.List;
import com.di.jdbc.mapper.util.ExampleUtil;

/**
 * @author di
 */
public class ObjectExampleMapper extends ObjectMapper {
	ObjectExampleMapper() {
		super();
	}

	ObjectExampleMapper(String fileName) {
		super(fileName);
	}

	public <T> List<T> selectByExample(Object e, Class<T> t) {
		return queryForList(ExampleUtil.selectByExample(e, t), t);
	}

	public <T> long countByExample(Object e, Class<T> t) {
		return queryForSingleValue(ExampleUtil.countByExample(e, t), long.class);
	}

	public <T> void deleteByExample(Object e, Class<T> t) {
		execute(ExampleUtil.deleteByExample(e, t));
	}
}
