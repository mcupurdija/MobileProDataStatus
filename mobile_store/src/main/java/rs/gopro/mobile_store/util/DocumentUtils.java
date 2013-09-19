package rs.gopro.mobile_store.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

public class DocumentUtils {

	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat codeDbDate = new SimpleDateFormat("ddMMyy");
	
	public DocumentUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static Date getStartOfDay(Date date) {
	    return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.DATE);
	}

	public static String generateSaleOrderDeviceNo(String salesPersonNo) {
		final Date today = new Date();
		return "L"+salesPersonNo+"-"+codeDbDate.format(today)+org.apache.commons.lang3.time.DateUtils.getFragmentInSeconds(today, Calendar.DATE);
	}
	
	public static String generateClonedSaleOrderDeviceNo(String salesPersonNo) {
		final Date today = new Date();
		return "LC"+salesPersonNo+"-"+codeDbDate.format(today)+org.apache.commons.lang3.time.DateUtils.getFragmentInSeconds(today, Calendar.DATE);
	}
}
