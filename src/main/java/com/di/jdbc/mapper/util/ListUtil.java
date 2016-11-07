package com.di.jdbc.mapper.util;

import java.util.List;

/**
 * @author di
 */
public class ListUtil {
	public static String listValueToString(List<?> list, String separator) {
		if (list == null || list.size() == 0) {
			return "";
		} else if (list.size() == 1) {
			return String.valueOf(list.get(0));
		} else {
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < list.size() - 1; i++) {
				s.append(list.get(i)).append(separator);
			}
			s.append(list.get(list.size() - 1));
			return s.toString();
		}
	}
}
