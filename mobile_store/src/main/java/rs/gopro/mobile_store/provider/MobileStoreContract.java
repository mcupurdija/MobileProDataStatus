package rs.gopro.mobile_store.provider;

import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class MobileStoreContract {

	public static final String CONTENT_AUTHORITY = "rs.gopro.mobile_store.provider.mobile_store_provider";
	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	private static final String PATH_USERS = "users";
	private static final String PATH_INVOICES = "invoices";
	private static final String PATH_CUSTOMERS = "customers";
	private static final String PATH_SEARCH = "search";
	private static final String PATH_ITEMS = "items";
	private static final String PATH_VISITS = "visits";
	private static final String PATH_SALE_ORDERS = "sale_orders";
	private static final String PATH_SALE_ORDER_LINES = "sale_order_lines";
	private static final String PATH_WITH_CUSTOMER = "with_customer";
	private static final String PATH_SALE_ORDER_SEARCH_CUSTOM = "custom_search";

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
		String CUSTOMER_ID = "customer_id";
		String LINE_NO = "line_no";
		String ENTRY_TYPE = "entry_type";
		String ODOMETER = "odometer";
		String DEPARTURE_TIME = "departure_time";
		String ARRIVAL_TIME = "arrival_time";
		String VISIT_RESULT = "visit_result";
		String NOTE = "note";
		// it has audit columns
	}


	public interface InvoicesColumns {
		String INVOICE_NO = "invoice_no";
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

	public interface CustomersColumns {
		String CUSTOMER_NO = "customer_no";
		String NAME = "name";
		String NAME_2 = "name2";
		String ADRESS_ID = "address_id";
		String CITY = "city";
		String POST_CODE = "post_code";
		String PHONE = "phone";
		String MOBILE = "mobile";
		String EMAIL = "email";
		String COMPANY_ID = "company_id";
		String PRIMARY_CONTACT_ID = "primary_contact_id";
		String VAR_REG_NO = "vat_reg_no";
		String CREDIT_LIMIT_LCY = "credit_limit_lcy";
		String BALANCE_LCY = "balance_lcy";
		String BALANCE_DUE_LCY = "balance_due_lcy";
		String PAYMENT_TERMS_CODE = "payment_terms_code";
		String PRIORITY = "priority";
		String GLOBAL_DIMENSION = "global_dimension";
		String CHANNEL_ORAN = "channel_oran";
		String BLOCKED_STATUS = "blocked_status";
	}

	public interface ItemsColumns {
		String ITEM_NO = "item_no";
		String DESCRIPTION = "description";
		String DESCRIPTION2 = "description2";
		String UNIT_OF_MEASURE = "unit_of_measure";
		String CATEGORY_CODE = "category_code";
		String GROUP_CODE = "group_code";
		String CAMPAIGN_STATUS = "campaign_status";
		String CONNECTED_SPEC_SHIP_ITEM = "connected_spec_ship_item";
		String UNIT_SALES_PRICE_EUR = "unit_sales_price_eur";
		String UNIT_SALES_PRICE_DIN = "unit_sales_price_din";
		String CAMPAIGN_CODE = "campaign_code";
		String CMPAIGN_START_DATE = "cmpaign_start_date";
		String CAMPAIGN_END_DATE = "campaign_end_date";
		String CREATED_DATE = "created_date";
		String CREATED_BY = "created_by";
		String UPDATED_DATE = "updated_date";
		String UPDATED_BY = "updated_by";
	}
	
	public interface SaleOrdersColumns {
		String SALES_ORDER_NO = "sales_order_no";
		String DOCUMENT_TYPE = "document_type";
		String CUSTOMER_ID = "customer_id";
		String ORDER_DATE = "order_date";
		String LOCATION_CODE = "location_code";
		String SHORTCUT_DIMENSION_1_CODE = "shortcut_dimension_1_code";
		String CURRENCY_CODE = "currency_code";
		String EXTERNAL_DOCUMENT_NO = "external_document_no";
		String QUOTE_NO = "quote_no";
		String BACKORDER_SHIPMENT_STATUS = "backorder_shipment_status";
		String ORDER_STATUS_FOR_SHIPMENT = "order_status_for_shipment";
		String FIN_CONTROL_STATUS = "fin_control_status";
		String ORDER_CONDITION_STATUS = "order_condition_status";
		String USED_CREDIT_LIMIT_BY_EMPLOYEE = "used_credit_limit_by_employee";
		String ORDER_VALUE_STATUS = "order_value_status";
		String QUOTE_REALIZED_STATUS = "quote_realized_status";
		String SPECIAL_QUOTE = "special_quote";
		String QUOTE_VALID_DATE_TO = "quote_valid_date_to";
		String CUST_USES_TRANSIT_CUST = "cust_uses_transit_cust";
		String SALES_PERSON_ID = "sales_person_id";
		String CUSTOMER_ADDRESS_ID = "customer_address_id";
		String CONTACT_PHONE = "contact_phone";
		String PAYMENT_OPTION = "payment_option";
		String CHECK_STATUS_PHONE = "check_status_phone";
		String TOTAL = "total";
		String TOTAL_DISCOUNT = "total_discount";
		String TOTAL_PDV = "total_pdv";
		String TOTAL_ITEMS = "total_items";
		String HIDE_REBATE = "hide_rebate";
		String FURTHER_SALE = "further_sale";
		String NOTE1 = "note1";
		String NOTE2 = "note2";
		String NOTE3 = "note3";
	}
	
	public interface SaleOrderLinesColumns {
		String SALE_ORDER_ID = "sale_order_id";
		String LINE_NO = "line_no";
		String ITEM_ID = "item_id";
		String QUANTITY = "quantity";
		String UNIT_OF_MEASURE = "unit_of_measure";
		String PRICE = "price";
		String MIN_DISCOUNT = "min_discount";
		String MAX_DISCOUNT = "max_discount";
		String REAL_DISCOUNT = "real_discount";
		String LINE_TOTAL = "line_total";
		String LINE_ORIGIN = "line_origin";
		String PRICE_EUR = "price_eur";
		String CAMPAIGN_STATUS = "campaign_status";
		String VERIFY_STATUS = "verify_status";
		String PRICE_DISCOUNT_STATUS = "price_discount_status";
		String QUANTITY_AVAILABLE_STATUS = "quantity_available_status";
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

	public static class Invoices implements InvoicesColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVOICES).build();

		public static Uri buildInvoicesUri(String invoicesId) {
			return CONTENT_URI.buildUpon().appendPath(invoicesId).build();
		}

		public static String getInvoicesId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = Invoices.POSTING_DATE + " ASC";
	}

	public static class Customers implements CustomersColumns, BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMERS).build();

		public static Uri buildCustomersUri(String customerId) {
			return CONTENT_URI.buildUpon().appendPath(customerId).build();
		}

		public static Uri buildNoUri() {
			return CONTENT_URI.buildUpon().appendEncodedPath(CUSTOMER_NO).build();
		}

		public static Uri buildSearchUri(String query) {
			return CONTENT_URI.buildUpon().appendPath(query).appendPath(CUSTOMER_NO).build();
		}

		public static Uri buildCustomSearchUri(String text, String status) {
			return CONTENT_URI.buildUpon().appendPath(text).appendPath(status).appendPath(CUSTOMER_NO).build();
		}

		public static String getCustomersId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getSearchQuery(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getCustomSearchFirstParamQuery(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getCustomSearchSecondParamQuery(Uri uri) {
			return uri.getPathSegments().get(2);
		}

		public static final String DEFAULT_SORT = Customers.CUSTOMER_NO + " ASC";
	}

	public static class Items implements ItemsColumns, BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();

		public static Uri buildNoUri() {
			return CONTENT_URI.buildUpon().appendEncodedPath(ITEM_NO).build();
		}

		public static String getItemNo(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static String getCustomSearchFirstParamQuery(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getCustomSearchSecondParamQuery(Uri uri) {
			return uri.getPathSegments().get(2);
		}
		

		public static final String DEFAULT_SORT = ItemsColumns.ITEM_NO + " ASC";

		public static String getItemStatus(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static Uri buildCustomSearchUri(String text, String status) {
			return CONTENT_URI.buildUpon().appendPath(text).appendPath(status).appendPath(ITEM_NO).build();
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
        public static final String DEFAULT_SORT = "visits." + Visits.CREATED_DATE + " DESC";
		
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
	 * Sale orders contract
	 * @author vladimirm
	 *
	 */
	public static class SaleOrders implements SaleOrdersColumns, BaseColumns, AuditColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SALE_ORDERS).build();
		 /** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = SaleOrders.ORDER_DATE + " DESC";
		
		public static final String CONTENT_TYPE =
				"vnd.android.cursor.dir/vnd.mobile_store.sale_orders";
		public static final String CONTENT_ITEM_TYPE =
				"vnd.android.cursor.item/vnd.mobile_store.sale_orders";
		
		public static Uri buildSaleOrderUri(String saleOrderId){
			return CONTENT_URI.buildUpon().appendPath(saleOrderId).build();
		}

		public static String getSaleOrderId(Uri uri){
			return  uri.getPathSegments().get(1);
		}

		public static Uri buildCustomSearchUri(String text, String status) {
			return CONTENT_URI.buildUpon().appendPath(text).appendPath(status).appendPath(PATH_SALE_ORDER_SEARCH_CUSTOM).build();
		}
		
		public static String getCustomSearchFirstParamQuery(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getCustomSearchSecondParamQuery(Uri uri) {
			return uri.getPathSegments().get(2);
		}

		public static String getSaleOrderDocType(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}

	/**
	 * Sale order lines contract
	 * @author vladimirm
	 *
	 */
	public static class SaleOrderLines implements SaleOrderLinesColumns, BaseColumns, AuditColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SALE_ORDER_LINES).build();
		 /** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = SaleOrderLines.CREATED_DATE + " DESC";
		
		public static final String CONTENT_TYPE =
				"vnd.android.cursor.dir/vnd.mobile_store.sale_order_lines";
		public static final String CONTENT_ITEM_TYPE =
				"vnd.android.cursor.item/vnd.mobile_store.sale_order_lines";
		
		public static Uri buildSaleOrderLineUri(String saleOrderLine){
			return CONTENT_URI.buildUpon().appendPath(saleOrderLine).build();
		}

		public static String getSaleOrderLineId(Uri uri){
			return  uri.getPathSegments().get(1);
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
