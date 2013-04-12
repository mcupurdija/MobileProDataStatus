package rs.gopro.mobile_store.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class DocumentUtils {

	@SuppressLint("SimpleDateFormat")
	private final static SimpleDateFormat codeDbDate = new SimpleDateFormat("ddMMyyHHss");
	
	public DocumentUtils() {
		// TODO Auto-generated constructor stub
	}

	public static String generateSaleOrderDeviceNo(String salesPersonNo) {
		return "L"+salesPersonNo+"-"+codeDbDate.format(new Date());
	}
	
	public static String generateClonedSaleOrderDeviceNo(String salesPersonNo) {
		return "LC"+salesPersonNo+"-"+codeDbDate.format(new Date());
	}
}
