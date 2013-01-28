package com.foxinmy.jycore.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 反射帮助类
 * 
 * @author jy.hu 2012-11-8
 */
public class ReflectionUtil {

	private static final String METHOD_GETTER_PREFIX = "get";
	private static final String METHOD_SETTER_PREFIX = "set";
	private static final String INTEGER_TYPE_NAME = "int,integer";
	private static final String LONG_TYPE_NAME = "long";
	private static final String BOOLEAN_TYPE_NAME = "boolean";

	// 获取包包名
	public static String getPackageName(Object obj) {
		return obj.getClass().getPackage().getName();
	}

	// 获取字段的泛型参数类型
	public static Class<?> getFieldGenericType(Object obj, String fieldName) {
		Field field = getField(obj, fieldName);
		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			return (Class<?>) ((ParameterizedType) type)
					.getActualTypeArguments()[0];
		}
		return null;
	}

	// 调用get方法
	public static Object invokeGetterMethod(Object obj, String propertyName) {
		try {
			String getterMethodName = new StringBuilder(2)
					.append(METHOD_GETTER_PREFIX)
					.append(StringUtil.capitalize(propertyName)).toString();
			return obj.getClass().getMethod(getterMethodName).invoke(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 调用set方法
	public static void invokeSetterMethod(Object obj, String propertyName,
			Object setterValue) {
		Class<?> setterClass = setterValue.getClass();
		invokeSetterMethod(obj, propertyName, setterValue, setterClass);
	}

	public static void invokeSetterMethod(Object obj, String propertyName,
			Object setterValue, Class<?> setterClass) {
		String setterMethodName = new StringBuilder(2)
				.append(METHOD_SETTER_PREFIX)
				.append(StringUtil.capitalize(propertyName)).toString();
		try {
			Method method = obj.getClass().getMethod(setterMethodName,
					setterClass);
			Field field = getField(obj, propertyName);
			String type = field.getType().getSimpleName();
			if (INTEGER_TYPE_NAME.indexOf(type) > -1)
				method.invoke(obj, Integer.parseInt(setterValue.toString()));
			else if (LONG_TYPE_NAME.equals(type))
				method.invoke(obj, Long.parseLong(setterValue.toString()));
			else if (BOOLEAN_TYPE_NAME.equals(type))
				method.invoke(obj, Boolean.parseBoolean(setterValue.toString()));
			else
				method.invoke(obj, setterValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取字段的值
	public static Object getFieldValue(Object obj, String fieldName) {
		try {
			return getField(obj, fieldName).get(obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 设置字段的值
	public static void setFieldValue(Object obj, String fieldName,
			String fieldValue) {
		try {
			getField(obj, fieldName).set(obj, fieldValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取字段的类型
	public static Class<?> getFieldType(Object obj, String fieldName) {
		return getField(obj, fieldName).getType();
	}

	// 获取字段对象
	private static Field getField(Object obj, String fieldName) {
		Field field = null;
		try {
			field = obj.getClass().getDeclaredField(fieldName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (field != null && !field.isAccessible()) {
			field.setAccessible(true);
		}
		return field;
	}
}