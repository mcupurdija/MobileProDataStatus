package rs.gopro.mobile_store.util;

import android.annotation.SuppressLint;
import java.io.IOException;
import java.text.DecimalFormat;
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
	// no need for warning here
	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat pickerDate = new SimpleDateFormat("dd-MM-yyyy");
	// no need for warning here
	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat pickerTime = new SimpleDateFormat("HH:mm");
	// no need for warning here
	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat localDbDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat navisionDbDate = new SimpleDateFormat("MM/dd/yy");
	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat navisionDbDateTime = new SimpleDateFormat("MM/dd/yy hh:mm:ss aaa");
	
	public static Date getWsDummyDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(1900, 0, 1);
		return calendar.getTime();
	}
	
	public static String marshaleDate(Date date) {
		return marshalDate.format(date);
	}
	
	public static Date unMarshaleDate(String date) throws IOException {
		try {
			return marshalDate.parse(date);
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Bad date format", e);
			throw new IOException("Bad date format", e);
		}
	}

	public static String  formatDatePickerDate(int year, int month, int day){
		DecimalFormat twoDigitFormat = new DecimalFormat("00"); 
		return "" + twoDigitFormat.format(day) + "-" + twoDigitFormat.format(month+1) + "-" + year;
	}
	
	public static Date getPickerDate(String pickerString) {
		try {
			return pickerDate.parse(pickerString);
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Bad date format", e);
			return null;
		}
	}
	
	public static String formatPickerInputForDb(String pickerString) {
		Date pickerD = getPickerDate(pickerString);
		return formatDbDate(pickerD);
	}
	
	public static String formatPickerTimeInputForDb(String pickerString) {
		String dateTime = marshalDate.format(new Date()) + " " + pickerString + ":00";
		Date pickerD = getLocalDbDate(dateTime);
		return formatDbDate(pickerD);
	}
	
	public static String formatDbTimeForPresentation(String dbTime) {
		if (dbTime == null) return "";
		Date localDbTime = getLocalDbDate(dbTime);
		return pickerTime.format(localDbTime);
	}
	
	public static String formatDbDateForPresentation(String dbDate) {
		if (dbDate == null) return "";
		Date localDbDate = getLocalDbDate(dbDate);
		return pickerDate.format(localDbDate);
	}
	
	public static String formatDateFromNavisionToDB(String dbDate) {
		if (dbDate == null) return "";
		Date localDbDate = getNavisionDate(dbDate);
		//return pickerDate.format(localDbDate);
		return formatDbDate(localDbDate);
	}
	
	public static String formatDateTimeFromNavisionToDB(String dbDate, String time) {
		if (dbDate == null) return "";
		Date localDbDate = getNavisionDateTime(dbDate + " " + time);
		return formatDbDate(localDbDate);
	}
	
	public static Date getLocalDbDate(String dbDateString) {
		try {
			return localDbDate.parse(dbDateString);
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Bad date format", e);
			return null;
		}
	}
	
	public static Date getNavisionDate(String dbDateString) {
		try {
			return navisionDbDate.parse(dbDateString);
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Bad date format", e);
			return null;
		}
	}
	
	public static Date getNavisionDateTime(String dbDateString) {
		try {
			return navisionDbDateTime.parse(dbDateString);
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Bad date format", e);
			return null;
		}
	}
	
	
	
	public static String formatDbDate(Date dbDate) {
		// it can be passed null by another parser that caught error
		if (dbDate == null) return null;
		return localDbDate.format(dbDate);
	}
	
}
