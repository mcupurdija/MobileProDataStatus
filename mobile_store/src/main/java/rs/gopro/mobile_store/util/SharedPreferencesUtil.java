package rs.gopro.mobile_store.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {

	private static final String SALE_PERSON_ID = "sale_person_id";
	private static final String SALE_PERSON_NO = "sale_person_no";
	private static final String USER_LOGIN_STATUS = "user_login_status";
	private static final String USER_ROLE = "user_role";

	public static String getSalePersonId(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, Context.MODE_PRIVATE);
		return sharedPreferences.getString(SALE_PERSON_ID, null);
	}

	public static void addSalePersonId(Context context, String salePersonId) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(SALE_PERSON_ID, salePersonId);
		editor.commit();
	}

	public static void setUserLoginStatus(Context context, Boolean loginStatus) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(USER_LOGIN_STATUS, loginStatus);
		editor.commit();
	}

	public static Boolean isUserLoggedIn(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(USER_LOGIN_STATUS, false);
	}
	
	public static void cleanSessionPrefs(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, Context.MODE_PRIVATE);
		 Editor editor = sharedPreferences.edit();
		 editor.clear();
		 editor.commit();
	}
	
	public static void addUserRole(Context context, String userRole){
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(USER_ROLE,userRole);
		editor.commit();
	}

	public static String getUserRole(Context context){
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, Context.MODE_PRIVATE);
		return sharedPreferences.getString(USER_ROLE, "USER");

	}

	public static void addSalePersonNo(Context applicationContext,
			String salesPersonNo) {
		SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(ApplicationConstants.SESSION_PREFS, Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();
		editor.putString(SALE_PERSON_NO, salesPersonNo);
		editor.commit();
	}
	
	public static String getSalePersonNo(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstants.SESSION_PREFS, Context.MODE_PRIVATE);
		return sharedPreferences.getString(SALE_PERSON_NO, null);
	}
}
