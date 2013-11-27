package rs.gopro.mobile_store.ws.formats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rs.gopro.mobile_store.util.LogUtils;
import android.annotation.SuppressLint;

public class WsDataFormatEnUsLatin {
	
	private static final String TAG = "WsDataFormatEnUsLatin";
	
	private static DecimalFormat decimalWSFormat = (DecimalFormat)NumberFormat.getInstance(Locale.US);
	private static DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getInstance();
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat dateWsFormat = new SimpleDateFormat("dd.MM.yy");
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat dateTimeWsFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat timeWsFormat = new SimpleDateFormat("HH:mm:ss");
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat dateTimeDbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat dateDbFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
	
	static {
		DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
		formatSymbols.setDecimalSeparator(',');
		formatSymbols.setGroupingSeparator('.');
		decimalFormat.setDecimalFormatSymbols(formatSymbols);
		decimalWSFormat.setDecimalFormatSymbols(formatSymbols);
    }
	
	public WsDataFormatEnUsLatin() {
	}

	public static String normalizeWsDate(String wsDate) {
		try {
			return dateFormat.format(dateWsFormat.parse(wsDate));
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Ws date not in good fromat", e);
			return wsDate;
		}
	}
	
	public static String normalizeDouble(String wsDouble) {
		try {
			return decimalFormat.format(decimalWSFormat.parse(wsDouble).doubleValue());
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Ws double not in good fromat", e);
			return wsDouble;
		}
	}
	
	public static String parseForWsDouble(Double wsDouble) {
//		try {
		return decimalWSFormat.format(wsDouble.doubleValue());
//		} catch (ParseException e) {
//			LogUtils.LOGE(TAG, "Ws double not in good fromat", e);
//			return String.valueOf(wsDouble);
//		}
	}
	
	public static Double parseForUIDouble(String uiDouble) {
		try {
			return decimalFormat.parse(uiDouble).doubleValue();
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Ui double not in good fromat", e);
			return null;
		}
	}
	
	public static Double toDoubleFromWs(String wsDouble) {
		try {
			return decimalWSFormat.parse(wsDouble).doubleValue();
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Ui double not in good fromat", e);
			return null;
		}
	}
	
	public static String toOutputWsDate(String dbDate) {
		try {
			return dateWsFormat.format(dateTimeDbFormat.parse(dbDate));
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Ws double not in good fromat", e);
			return dbDate;
		}
	}
	
	public static String toOutputWsTime(String dbDate) {
		try {
			return timeWsFormat.format(dateTimeDbFormat.parse(dbDate));
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Ws double not in good fromat", e);
			return dbDate;
		}
	}

	public static String toDbDateString(Date inputDate) {
//		try {
			return dateTimeDbFormat.format(inputDate);
//		} catch (ParseException e) {
//			LogUtils.LOGE(TAG, "Ws double not in good fromat", e);
//			return null;
//		}
	}
	
	public static String toDbDateFromWsString(String wsDate) {
		if (wsDate == null || wsDate.length() < 1) 
			return null;
		Date localWsDate = null;
		try {
			localWsDate = dateWsFormat.parse(wsDate);
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Date time format problem!", e);
		}
		if (localWsDate == null) {
			return null;
		}
		return toDbDateString(localWsDate);
	}
	
	public static String toDbTimeeFromWsString(String dbDate, String time) {
		if (dbDate == null) return null;
		Date localWsDate = null;
		try {
			localWsDate = dateTimeWsFormat.parse(dbDate + " " + time);
		} catch (ParseException e) {
			LogUtils.LOGE(TAG, "Date time format problem!", e);
		}
		if (localWsDate == null) {
			return null;
		}
		return toDbDateString(localWsDate);
	}
}
