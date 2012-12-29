package rs.gopro.mobile_store.util;

import android.annotation.SuppressLint;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
	public static String TAG = "DateUtils";
	
	// no need for warning here
	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat marshalDate = new SimpleDateFormat("yyyy-MM-dd");
	
	public static Date getWsDummyDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(1970, 0, 1);
		return calendar.getTime();
	}
	
	public static String marshaleDate(Date date) {
		return marshalDate.format(date);
	}
	
	public static Date unMarshaleDate(String date) throws IOException {
		try {
			return marshalDate.parse(date);
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Bad date format");
			throw new IOException("Bad date format", e);
		}
	}
}
