package com.foxinmy.jycore.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jy.hu , 2012-10-25
 */
public class StringUtil {

	private final static String REGEX_ALL_BLANK = "(^\\s*)|(\\s*$)";
	private final static String REGEX_LEFT_BLANK = "^\\s*";
	private final static String REGEX_RIGHT_BLANK = "\\s*$";

	private final static String BLANK_STRING = "";
	private final static DateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private final static int TRIM_RIHGT = 1;
	private final static int TRIM_LEFT = 2;

	// 判断字符串是否为空
	public static boolean isEmpty(String arg) {
		return arg == null || arg.length() == 0;
	}

	// 判断字符串是否为空
	public static boolean isBlank(String arg) {
		return isEmpty(arg)
				|| arg.replaceAll(REGEX_ALL_BLANK, BLANK_STRING).length() == 0;
	}

	// 清除字符串空格
	public static String trim(String val, int trim) {
		if (isBlank(val))
			return BLANK_STRING;
		switch (trim) {
		case TRIM_RIHGT:
			return val.replaceAll(REGEX_RIGHT_BLANK, BLANK_STRING);
		case TRIM_LEFT:
			return val.replaceAll(REGEX_LEFT_BLANK, BLANK_STRING);
		default:
			return val.replaceAll(REGEX_ALL_BLANK, BLANK_STRING);
		}
	}

	// 格式化日期
	public static String formatDate(Date date) {
		if (date == null)
			return BLANK_STRING;
		return dateFormat.format(date);
	}

	// 首位字母
	public static String capitalize(String arg) {
		if (isBlank(arg))
			return BLANK_STRING;
		char title = arg.charAt(0);
		int len = arg.length();
		if (Character.isTitleCase(title))
			return arg;
		return new StringBuilder(len).append(Character.toTitleCase(title))
				.append(arg.substring(1)).toString();
	}

	public static void main(String[] args) {
		System.out.println(isBlank(" 的 "));
		System.out.println(trim("  的   ", TRIM_RIHGT));
	}
}
