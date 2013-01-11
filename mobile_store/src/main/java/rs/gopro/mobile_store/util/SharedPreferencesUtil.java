package rs.gopro.mobile_store.util;

import rs.gopro.mobile_store.provider.MobileStoreContract.SalesPersonsColumns;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {

	private static final String SALE_PERSON_ID = "sale_person_id";
	private static final String USER_LOGIN_STATUS = "user_login_status";

	public static String getSalePersonId(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, context.MODE_PRIVATE);
		return sharedPreferences.getString(SALE_PERSON_ID, null);
	}

	public static void addSalePersonId(Context context, String salePersonId) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(SALE_PERSON_ID, salePersonId);
		editor.commit();
	}

	public static void setUserLoginStatus(Context context, Boolean loginStatus) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(USER_LOGIN_STATUS, loginStatus);
		editor.commit();
	}

	public static Boolean isUserLoggedIn(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(USER_LOGIN_STATUS, false);
	}
	
	public static void cleanSessionPrefs(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, context.MODE_PRIVATE);
		 Editor editor = sharedPreferences.edit();
		 editor.clear();
		 editor.commit();
	}

}
