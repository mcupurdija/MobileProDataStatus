package rs.gopro.mobile_store.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
	public static Date getWsDummyDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(1970, 0, 1);
		return calendar.getTime();
	}
}
