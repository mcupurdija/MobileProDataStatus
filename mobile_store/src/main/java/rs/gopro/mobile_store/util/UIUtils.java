package rs.gopro.mobile_store.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import rs.gopro.mobile_store.BuildConfig;
//import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;

public class UIUtils {

	//@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void setActivatedCompat(View view, boolean activated) {
		if (hasHoneycomb()) {
			view.setActivated(activated);
		}
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	private static final long sAppLoadTime = System.currentTimeMillis();

	public static long getCurrentTime(final Context context) {
		if (BuildConfig.DEBUG) {
			return context.getSharedPreferences("mock_data", Context.MODE_PRIVATE).getLong("mock_current_time", System.currentTimeMillis()) + System.currentTimeMillis() - sAppLoadTime;
		} else {
			return System.currentTimeMillis();
		}
	}

	public static boolean isSameDay(long time1, long time2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTimeInMillis(time1);
		cal2.setTimeInMillis(time2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}
	
	/**
	 * Format yyyy-MM-dd HH:MM:SS
	 * @param date
	 * @return
	 */
	public static Date getDateTime(String date){
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
			return dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Format yyyy-MM-dd
	 * @param date
	 * @return
	 */
	public static Date getDate(String date){
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			return dateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Format dd. MM. yyyy
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MM. yyyy");
		return dateFormat.format(date);
	}
	
	/**
	 * Format dd. MM. yyyy HH:MM:SS
	 * @param date
	 * @return
	 */
	public static String formatDateTime(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd. MM. yyyy HH:MM:SS");
		return dateFormat.format(date);
	}
	
	public static String  formatingDate(int year, int month, int day){
		    return "" + year + "-" + month + "-" + day;
	}
}
