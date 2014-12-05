package net.infidea.contextmon.util;

import java.util.Date;

public class TimeConverter {
	public static long toMilliseconds(long timestamp) {
		return (new Date()).getTime()+(timestamp-System.nanoTime())/1000000L;
	}
}
