package rs.gopro.mobile_store.util;

public class ApplicationConstants {

	// **** DIALOGS ****/

	public static final int DATE_PICKER_DIALOG = 10;

	public static final String SESSION_PREFS = "SessionPrefs";
	
	public static final Integer SUCCESS = 1;
	public static final Integer FAILURE = 2;

	public static enum USER_ROLE {
		ADMIN, USER
	}

	public static enum SyncStatus{
		SUCCESS, FAILURE, IN_PROCCESS;	
	}
}
