package rs.gopro.mobile_store.provider;


import android.net.Uri;
import android.net.rtp.RtpStream;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class MobileStoreContract {

	public static final String CONTENT_AUTHORITY = "rs.gopro.mobile_store.provider.mobile_store_provider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	private static final String PATH_USERS = "users";
	private static final String PATH_INVOICES = "invoices";

	public interface UsersColumns {
		String USERNAME = "username";
		String PASSWORD = "pass";
		String SALES_PERSON_ID = "sales_person_id";
		String LAST_LOGIN = "last_login";
	}

	public interface InvoicesColumns {
		String NO = "no";
		String CUSTOMER_ID = "customer_id";
		String POSTING_DATE = "posting_date";
		String SALES_PERSON_ID = "sales_person_id";
		String DUE_DATE = "due_date";
		String TOTAL = "total";
		String TOTAL_LEFT = "total_left";
		String DUE_DATE_DAYS_LEFT = "due_date_days_left";
		String CREATED_DATE = "created_date";
		String CREATED_BY = "created_by";
		String UPDATED_DATE = "updated_date";
		String UPDATED_BY = "updated_by";

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
	
	public static class Invoices implements InvoicesColumns, BaseColumns{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVOICES).build();
		
		public static Uri buildInvoicesUri(String invoicesId){
			return CONTENT_URI.buildUpon().appendPath(invoicesId).build();
		}
		
		public static String getInvoicesId(Uri uri){
			return  uri.getPathSegments().get(1);
		}
		
		 /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = Invoices.CREATED_DATE + " ASC";
	}

}
