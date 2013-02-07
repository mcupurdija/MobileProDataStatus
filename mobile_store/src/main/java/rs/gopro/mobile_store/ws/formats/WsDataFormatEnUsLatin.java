package rs.gopro.mobile_store.ws.formats;

import android.annotation.SuppressLint;

import java.text.DecimalFormat;
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
	private static SimpleDateFormat dateWsFormat = new SimpleDateFormat("dd/MM/yy");
	private static java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
	
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
	
}
