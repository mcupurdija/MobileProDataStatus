package rs.gopro.mobile_store.ws.formats;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import rs.gopro.mobile_store.util.LogUtils;

public class WsDataFormatEnUsLatin {
	
	private static final String TAG = "WsDataFormatEnUsLatin";
	
	private static DecimalFormat decimalWSFormat = (DecimalFormat)NumberFormat.getInstance(Locale.US);
	private static DecimalFormat decimalFormat = (DecimalFormat)NumberFormat.getInstance();
	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat dateWsFormat = new SimpleDateFormat("dd.MM.yy");
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
}
