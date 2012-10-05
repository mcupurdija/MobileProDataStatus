package rs.gopro.mobile_store.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class MobileStoreContract {
												
	public static final String CONTENT_AUTHORITY = "rs.gopro.mobile_store.provider.mobile_store_provider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	private static final String PATH_USERS = "users";

	public interface UsersColumns {
		String USERNAME = "username";
		String PASSWORD = "pass";
		String SALES_PERSON_ID = "sales_person_id";
		String LAST_LOGIN = "last_login";
	}

	public static class Users implements UsersColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

		public static Uri buildUsersUri(String userId) {
			return CONTENT_URI.buildUpon().appendPath(userId).build();
		}
		
		public static Uri buildUsernameUri() {
			return CONTENT_URI.buildUpon().appendEncodedPath(USERNAME).build();
		}
		
		public static String getUserId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getUsername(Uri uri) {
			return uri.getPathSegments().get(1);
		}

	}

}
