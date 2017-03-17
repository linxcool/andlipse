package com.linxcool.andlipse.core;

import java.lang.reflect.Method;

public class LTypeHelper {

	public static boolean instanceOf(Class<?> cls, String name) {
		if (cls.getName().equals(name)) {
			return true;
		}
		if (cls.getSuperclass() != null) {
			return instanceOf(cls.getSuperclass(), name);
		}
		return false;
	}

	public static Method getMethod(Class<?> cls, String name) {
		try {
			Method method = cls.getDeclaredMethod(name);
			method.setAccessible(true);
			return method;
		} catch (Exception e) {
			if (cls.getSuperclass() != null) {
				return getMethod(cls.getSuperclass(), name);
			}
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invoke(Object obj, String mtdName) {
		try {
			Method mtd = getMethod(obj.getClass(), mtdName);
			return (T) mtd.invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
