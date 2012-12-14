package rs.gopro.mobile_store.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class MobileStoreContract {
												
	public static final String CONTENT_AUTHORITY = "rs.gopro.mobile_store.provider.mobile_store_provider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	private static final String PATH_USERS = "users";
	private static final String PATH_VISITS = "visits";
	private static final String PATH_WITH_CUSTOMER = "with_customer";

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
	
	/**
	 * Visits contract.
	 * @author vladimirm
	 *
	 */
	public static class Visits implements VisitsColumns, SalesPersonsColumns, CustomersColumns, AuditColumns, BaseColumns {
		
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VISITS).build();
		 /** Default "ORDER BY" clause. */
        public static final String DEFAULT_SORT = Visits.CREATED_DATE + " DESC";
		
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.mobile_store.visit";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.mobile_store.visit";
        
        public static Uri buildVisitUri(String visitsId){
			return CONTENT_URI.buildUpon().appendPath(visitsId).build();
		}

		public static String getVisitId(Uri uri){
			return  uri.getPathSegments().get(1);
		}
		
		public static Uri buildWithCustomer() {
			return  CONTENT_URI.buildUpon().appendPath(PATH_WITH_CUSTOMER).build();
		}
	}

	/**
	 * To mark data inserted/changed/deleted from sync service and not from UI.
	 * @param uri
	 * @return
	 */
    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(
                ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
    }

    /**
     * To check if data is inserted/changed/deleted from sync service and not from UI.
     * @param uri
     * @return
     */
    public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
        return TextUtils.equals("true",
                uri.getQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER));
    }
}
