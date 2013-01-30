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
	private static final String PATH_CUSTOMERS_BY_SALES_PERSON = "customers_by_sales_person";
	//private static final String PATH_SEARCH = "search";
	private static final String PATH_ITEMS = "items";
	private static final String PATH_VISITS = "visits";
	private static final String PATH_SALE_ORDERS = "sale_orders";
	private static final String PATH_SALE_ORDERS_LIST = "sale_orders_list";
	private static final String PATH_SALE_ORDER_LINES = "sale_order_lines";
	private static final String PATH_SALE_ORDER_LINES_FROM_ORDER = "sale_order_lines_from_order";
	private static final String PATH_WITH_CUSTOMER = "with_customer";
	private static final String PATH_SALE_ORDER_SEARCH_CUSTOM = "custom_search";

	private static final String PATH_CONTACTS = "contacts";
	private static final String PATH_CUSTOMER_ADDRESSES = "customer_addresses";
	private static final String PATH_CUSTOMER_NO = "customer_no";
	//private static final String PATH_CONTACTS_SEARCH_CUSTOM = "custom_search";
	private static final String PATH_VISITS_DATE = "visits_date";
	private static final String PATH_SYNC_LOGS = "sync_logs";
	private static final String PATH_INVOICE_LINES = "invoice_lines";
	private static final String PATH_INVOICE_LINES_FROM_ORDER = "invoice_lines_from_order";
	private static final String PATH_SYNC_LOGS_ID = "sync_logs_obejct_id";
	private static final String PATH_SALES_PERSON = "sales_person";
	private static final String PATH_GENRIC = "generic";

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
		String USERS_ROLE_ID = "users_role_id";
	}
	
	public interface UsersRoleColumns{
		String NAME = "name";
		String DESC = "desc";
	}

	public interface VisitsColumns {
		// it pick sales_person_id from customer
		
		String VISIT_DATE = "visit_date";
		String CUSTOMER_ID = "customer_id";
		String LINE_NO = "line_no";
		String ENTRY_TYPE = "entry_type";
		String ODOMETER = "odometer";
		String DEPARTURE_TIME = "departure_time";
		String ARRIVAL_TIME = "arrival_time";
		String VISIT_RESULT = "visit_result";
		String NOTE = "note";
		String SYNC_OBJECT_BATCH = "sync_object_batch";
		String IS_REALIZED = "is_realized";
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
		String SYNC_OBJECT_BATCH = "sync_object_batch";
		String CREATED_DATE = "created_date";
		String CREATED_BY = "created_by";
		String UPDATED_DATE = "updated_date";
		String UPDATED_BY = "updated_by";

	}

	public interface CustomersColumns {
		// String SALES_PERSON_ID = "sales_person_id";
		String CUSTOMER_NO = "customer_no";
		String NAME = "name";
		String NAME_2 = "name2";
		String ADDRESS = "address";
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

		String SML = "sml";
		String INTERNAL_BALANCE_DUE_LCY = "internal_balance_due_lcy";
		String ADOPTED_POTENTIAL = "adopted_potential";
		String FOCUS_CUSTOMER = "focus_customer";
		String DIVISION = "division";
		String NUMBER_OF_BLUE_COAT = "number_of_blue_coat";
		String NUMBER_OF_GREY_COAT = "number_of_grey_coat";
		String SYNC_OBJECT_BATCH = "sync_object_batch";
		String SALES_PERSON_ID = "sales_person_id";
	}

	public interface ItemsColumns {
		String ITEM_NO = "item_no";
		String DESCRIPTION = "description";
		String DESCRIPTION2 = "description2";
		String UNIT_OF_MEASURE = "unit_of_measure";
		String CATEGORY_CODE = "category_code";
		String GROUP_CODE = "group_code";
		String CAMPAIGN_STATUS = "campaign_status";
		String OVERSTOCK_STATUS = "overstock_status";
		String CONNECTED_SPEC_SHIP_ITEM = "connected_spec_ship_item";
		String UNIT_SALES_PRICE_EUR = "unit_sales_price_eur";
		String UNIT_SALES_PRICE_DIN = "unit_sales_price_din";
		String CAMPAIGN_CODE = "campaign_code";
		String CMPAIGN_START_DATE = "cmpaign_start_date";
		String CAMPAIGN_END_DATE = "campaign_end_date";
		String SYNC_OBJECT_BATCH = "sync_object_batch";
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
		// String SALES_PERSON_ID = "sales_person_id";
		String SELL_TO_ADDRESS_ID = "sell_to_address_id";
		String SHIPP_TO_ADDRESS_ID = "shipp_to_address_id";
		String CONTACT_ID = "contact_id";
		String CONTACT_NAME = "contact_name";
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

	/**
	 * Contacts contract
	 * 
	 * @author acanikolic
	 * 
	 */
	public interface ContactsColumns {
		String CONTACT_NO = "contact_no";
		String CONTACT_TYPE = "contact_type";
		String NAME = "name";
		String NAME2 = "name2";
		String ADDRESS = "address";
		String CITY = "city";
		String POST_CODE = "post_code";
		String PHONE = "phone";
		String MOBILE_PHONE = "mobile_phone";
		String EMAIL = "email";
		String COMPANY_NO = "company_no";
		String COMPANY_ID = "company_id";
		String VAT_REGISTRATION = "vat_registration";
		String SALES_PERSON_ID = "sales_person_id";
		String DIVISION = "division";
		String NUMBER_OF_BLUE_COAT = "number_of_blue_coat";
		String NUMBER_OF_GREY_COAT = "number_of_grey_coat";
		String JOB_TITLE = "job_title";
		String SYNC_OBJECT_BATCH = "sync_object_batch";
		String CREATED_DATE = "created_date";
		String CREATED_BY = "created_by";
		String UPDATED_DATE = "updated_date";
		String UPDATED_BY = "updated_by";

	}

	public interface SyncLogsColumns {
		String SYNC_OBJECT_NAME = "sync_object_name";
		String SYNC_OBJECT_ID = "sync_object_id";
		String SYNC_OBJECT_STATUS = "sync_object_status";
		String SYNC_OBJECT_BATCH = "sync_object_batch";
		String CREATED_DATE = "created_date";
		String CREATED_BY = "created_by";
		String UPDATED_DATE = "updated_date";
		String UPDATED_BY = "updated_by";
	}
	
	public interface CustomerAddressesColumns {
		String ADDRESS_NO = "address_no";
		String CUSTOMER_NO = "customer_no";
		String ADDRESS = "address";
		String CITY = "city";
		String CONTANCT = "contact";
		String PHONE_NO = "phone_no";
		String POST_CODE = "post_code";
		String CREATED_DATE = "created_date";
		String CREATED_BY = "created_by";
		String UPDATED_DATE = "updated_date";
		String UPDATED_BY = "updated_by";
	}
	
	public interface InvoiceLineColumns {

		
		String INVOICES_ID = "invoices_id";
		String LINE_NO = "line_no";
		String CUSTOMER_ID = "customer_id";
		String TYPE = "type";
		String NO = "no";
		String LOCATION_CODE = "location_code";
		String DESCRIPTION = "description";
		String QUANTITY = "quantity";
		String UNIT_PRICE = "unit_price";
		String VAT_PERCENT = "vat_percent";
		String LINE_DISCOUNT_PERCENT = "line_discount_percent";
		String LINE_DISCOUNT_AMOUNT = "line_discount_amount";
		String AMOUNT_INCLUDING_VAT = "amount_including_vat";
		String INV_DISCOUNT_AMOUNT = "inv_discount_amount";
		String UNIT_OF_MEASURE_CODE = "unit_of_measure_code";
		String PRICE_INCLUDE_VAT = "price_include_vat";
		String CREATED_DATE = "created_date";
		String CREATED_BY = "created_by";
		String UPDATED_DATE = "updated_date";
		String UPDATED_BY = "updated_by";
	}
	
	public interface SalesPersonsColumns {
		String SALE_PERSON_NO = "sales_person_no";
		String SALE_PERSON_NAME = "name";
		String SALE_PERSON_NAME_2 = "name2";
		
	}
	
	public static class Generic implements BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRIC).build();
		
		public static Uri buildTableUri(String table) {
			return CONTENT_URI.buildUpon().appendPath(table).build();
		}
		
		public static String getTableName(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}

	
	public static class SalesPerson implements SalesPersonsColumns, BaseColumns{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SALES_PERSON).build();
	}
	
	public static class Users implements UsersColumns, UsersRoleColumns, BaseColumns {
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

	public static class Invoices implements InvoicesColumns, BaseColumns, SalesPersonsColumns {
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
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	public static class InvoiceLine implements InvoiceLineColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVOICE_LINES).build();
		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = "invoice_lines." + AuditColumns.CREATED_DATE + " DESC";
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.invoice_lines";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.invoice_lines";
		
		public static Uri buildInvoiceLinesUri(String invoiceId) {
			return BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVOICE_LINES_FROM_ORDER).appendPath(invoiceId).build();
		}

		public static String getInvoiceId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}
	
	

	public static class Customers implements CustomersColumns, BaseColumns, SalesPersonsColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMERS).build();

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.customer";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.customer";

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

		public static Uri getCustomersBySalesPerson(String salesPersonId) {
			return BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMERS_BY_SALES_PERSON).appendPath(salesPersonId).build();
		}

		public static String getCustomersSalesPersonId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static final String DEFAULT_SORT = Customers.CUSTOMER_NO + " ASC";
	}

	public static class Items implements ItemsColumns, BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEMS).build();

		public static Uri buildNoUri() {
			return CONTENT_URI.buildUpon().appendEncodedPath(ITEM_NO).build();
		}

		public static Uri buildItemUri(String itemId) {
			return CONTENT_URI.buildUpon().appendPath(itemId).build();
		}
		
		public static String getItemId(Uri uri){
			return uri.getPathSegments().get(1);
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
	 * 
	 * @author vladimirm
	 * 
	 */
	public static class Visits implements VisitsColumns, SalesPersonsColumns, CustomersColumns, AuditColumns, BaseColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VISITS).build();
		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = "visits." + Visits.CREATED_DATE + " DESC";

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.visit";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.visit";

		public static Uri buildVisitUri(String visitsId) {
			return CONTENT_URI.buildUpon().appendPath(visitsId).build();
		}
		public static Uri buildDateOfVisitUri(String visitsDate) {
			return CONTENT_URI.buildUpon().appendPath(visitsDate).appendPath(PATH_VISITS_DATE).build();
		}

		public static String getVisitId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		public static String getDateOfVisit(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static Uri buildWithCustomer() {
			return CONTENT_URI.buildUpon().appendPath(PATH_WITH_CUSTOMER).build();
		}
	}

	/**
	 * Sale orders contract
	 * 
	 * @author vladimirm
	 * 
	 */
	public static class SaleOrders implements SaleOrdersColumns, BaseColumns, CustomersColumns, AuditColumns, SalesPersonsColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SALE_ORDERS).build();
		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = "sale_orders." + SaleOrders.ORDER_DATE + " DESC";

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.sale_orders";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.sale_orders";

		public static Uri buildSaleOrderUri(String saleOrderId) {
			return CONTENT_URI.buildUpon().appendPath(saleOrderId).build();
		}

		public static Uri buildSaleOrdersListUri(String salesPersonId) {
			return BASE_CONTENT_URI.buildUpon().appendPath(PATH_SALE_ORDERS_LIST).appendPath(salesPersonId).build();
		}

		public static String getSalesPersonId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getSaleOrderId(Uri uri) {
			return uri.getPathSegments().get(1);
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
	 * 
	 * @author vladimirm
	 * 
	 */
	public static class SaleOrderLines implements SaleOrderLinesColumns, BaseColumns, ItemsColumns, AuditColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SALE_ORDER_LINES).build();
		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = "sale_order_lines." + AuditColumns.CREATED_DATE + " DESC";

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.sale_order_lines";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.sale_order_lines";

		public static Uri buildSaleOrderLineUri(String saleOrderLine) {
			return CONTENT_URI.buildUpon().appendPath(saleOrderLine).build();
		}

		public static String getSaleOrderLineId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static Uri buildSaleOrderLinesUri(String saleOrderId) {
			return BASE_CONTENT_URI.buildUpon().appendPath(PATH_SALE_ORDER_LINES_FROM_ORDER).appendPath(saleOrderId).build();
		}

		public static String getSaleOrderId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}

	public static class Contacts implements ContactsColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTACTS).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.contacts";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.contacts";

		public static Uri buildContactsUri(String contactId) {
			return CONTENT_URI.buildUpon().appendPath(contactId).build();
		}

		public static String getContactsId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static Uri buildCustomSearchUri(String text) {
			return CONTENT_URI.buildUpon().appendPath(text).build();
		}
		
		public static String getContactsCustomSearch(Uri uri){
			return uri.getPathSegments().get(1);
		}
		
		public static final String DEFAULT_SORT = Contacts.CONTACT_NO + " ASC";
	}

	public static class SyncLogs implements SyncLogsColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SYNC_LOGS).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.sync_logs";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.sync_logs";

		public static Uri buildSyncLogsUri(String syncId) {
			return CONTENT_URI.buildUpon().appendPath(syncId).build();
		}
		
		public static Uri buildSyncLogsObjectIdUri(String syncObjectId) {
			return CONTENT_URI.buildUpon().appendPath(syncObjectId).appendPath(PATH_SYNC_LOGS_ID).build();
		}

		public static String getSyncLogId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static String getSyncLogObjectId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static final String DEFAULT_SORT = SyncLogs.CREATED_DATE + " ASC";
	}
	
	public static class CustomerAddresses implements CustomerAddressesColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMER_ADDRESSES).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.customer_addresses";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.customer_addresses";

		public static Uri buildCustomerAddressUri(String addressId) {
			return CONTENT_URI.buildUpon().appendPath(addressId).build();
		}

		public static String getCustomerAddressId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static Uri buildCustomSearchUri(String text) {
			return CONTENT_URI.buildUpon().appendPath(text).build();
		}
		
		public static String getCustomSearch(Uri uri){
			return uri.getPathSegments().get(1);
		}
		
		public static Uri buildSearchByCustomerNoUri(String customerNo) {
			return CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMER_NO).appendPath(customerNo).build();
		}
		
		public static String getSearchByCustomerNo(Uri uri){
			return uri.getPathSegments().get(2);
		}
		
		public static final String DEFAULT_SORT = CustomerAddresses.ADDRESS_NO + " ASC";
	}
	
	/**
	 * To mark data inserted/changed/deleted from sync service and not from UI.
	 * 
	 * @param uri
	 * @return
	 */
	public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
		return uri.buildUpon().appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true").build();
	}

	/**
	 * To check if data is inserted/changed/deleted from sync service and not
	 * from UI.
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
		return TextUtils.equals("true", uri.getQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER));
	}
}
