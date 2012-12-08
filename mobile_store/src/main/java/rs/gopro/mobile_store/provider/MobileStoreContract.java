package rs.gopro.mobile_store.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class MobileStoreContract {
												
	public static final String CONTENT_AUTHORITY = "rs.gopro.mobile_store.provider.mobile_store_provider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	private static final String PATH_USERS = "users";
	private static final String PATH_VISITS = "visits";

	public interface AuditColumns {
		String CREATED_DATE = "created_date";
		String CREATED_BY = "created_by"; 
		String UPDATED_DATE = "updated_date";
		String UPDATED_BY = "updated_by"; 
	}
	
	public interface UsersColumns {
		String USERNAME = "username";
		String PASSWORD = "pass";
		String SALES_PERSON_ID = "sales_person_id";
		String LAST_LOGIN = "last_login";
	}
	
	public interface VisitsColumns {
		// it has sales_person_id
		String VISIT_DATE = "visit_date";
		// it has customer_id 
		// it has customer_name
		String LINE_NO = "line_no";
		String ENTRY_TYPE = "entry_type";
		String ODOMETER = "odometer";
		String DEPARTURE_TIME = "departure_time";
		String ARRIVAL_TIME = "arrival_time";
		String VISIT_RESULT = "visit_result";
		String NOTE = "note";
		// it has audit columns
	}

	public interface CustomersColumns {
		String CUSTOMER_ID = "customer_id";
		String CUSTOMER_NAME = "customer_name";
	}
	
	public interface SalesPersonsColumns {
		String SALES_PERSON_ID = "sales_person_id";
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
	
	public static class Visits implements VisitsColumns, SalesPersonsColumns, CustomersColumns, AuditColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VISITS).build();
	}

}
