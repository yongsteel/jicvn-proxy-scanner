package com.jicvn.proxy.utils;

public class IntUtils {

	private IntUtils() {
	}
	
	public static int plusOne(Integer value) {
		return valueOf(value) + 0x1;
	}

	public static int valueOf(Integer value) {
		return valueOf(value, 0);
	}

	public static int valueOf(Integer value, Integer defalut) {
		if (value == null)
			return defalut;
		return value;
	}
	
	public static long currentTimeMillis() {
		return System.currentTimeMillis();
	}

	public static int currentTimeSecond() {
		return currentTimeSecond(currentTimeMillis());
	}
	
	public static int currentTimeSecond(long millis) {
		Long timeSecond = millis / 1000;
		return timeSecond.intValue();
	}

	public static long timeSecondToTimeMillis(int timeSecond) {
		return (long) timeSecond * 1000L;
	}
}
