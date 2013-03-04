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
	private static final String PATH_INVOICES_SEARCH = "invoices_search";
	private static final String PATH_CUSTOMERS = "customers";
	private static final String PATH_CUSTOMERS_BY_SALES_PERSON = "customers_by_sales_person";
	//private static final String PATH_SEARCH = "search";
	private static final String PATH_ITEMS = "items";
	private static final String PATH_ITEM_SEARCH = "item_search";
	private static final String PATH_ITEM_NO = "item_no";
	private static final String PATH_VISITS = "visits";
	private static final String PATH_PLANNED_VISITS_EXPORT = "visits_planned_export";
	private static final String PATH_REALIZED_VISITS_EXPORT = "visits_realized_export";
	private static final String PATH_SALE_ORDERS = "sale_orders";
	private static final String PATH_SALE_ORDERS_LIST = "sale_orders_list";
	private static final String PATH_SALE_ORDERS_EXPORT = "sale_orders_export";
	private static final String PATH_SALE_ORDERS_SALDO = "sale_orders_saldo";
	private static final String PATH_SALE_ORDER_LINES = "sale_order_lines";
	private static final String PATH_SALE_ORDER_LINES_FROM_ORDER = "sale_order_lines_from_order";
	private static final String PATH_SALE_ORDER_LINES_EXPORT = "sale_order_lines_export";
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
	private static final String PATH_SALES_PERSON = "sales_persons";
	private static final String PATH_GENRIC = "generic";
	private static final String PATH_ELECT_CARD_CUSTOMER = "electronic_card_customer";
	private static final String PATH_CUSTOMER_TRADE_AGREEMENT = "customer_trade_agreement";
	private static final String PATH_SENT_ORDERS = "sent_orders";
	private static final String PATH_SENT_ORDERS_STATUS = "sent_orders_status";
	private static final String PATH_SENT_ORDERS_STATUS_SEARCH = "custom_search";

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
		String POTENTIAL_CUSTOMER = "potential_customer";
		String LINE_NO = "line_no";
		String ENTRY_TYPE = "entry_type";
		String ODOMETER = "odometer";
		String DEPARTURE_TIME = "departure_time";
		String ARRIVAL_TIME = "arrival_time";
		String VISIT_RESULT = "visit_result";
		String NOTE = "note";
		String SYNC_OBJECT_BATCH = "sync_object_batch";
		String VISIT_TYPE = "visit_type";
		String IS_SENT = "is_sent";
		String IS_DELETED = "is_deleted";
		// it has audit columns
	}

	public interface InvoicesColumns {
		String INVOICE_NO = "invoice_no";
		String CUSTOMER_ID = "customer_id";
		String POSTING_DATE = "posting_date";
		//fetch from customer SALES_PERSON_ID 
		//String SALES_PERSON_ID = "sales_person_id";
		String DUE_DATE = "due_date";
		String ORIGINAL_AMOUNT = "original_amount";
		String REMAINING_AMOUNT = "remaining_amount";
		String DOCUMENT_TYPE = "document_type";
		String OPEN = "open";
		String PRICES_INCLUDE_VAT = "prices_include_vat";
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
		
		String SALES_LCY = "sales_lcy";
		String GROSS_PROFIT_PFEP = "gross_profit_pfep";
		String TURNOVER_IN_LAST_3M = "turnover_in_last_3m";
		String TURNOVER_IN_LAST_6M = "turnover_in_last_6m";
		String TURNOVER_IN_LAST_12M = "turnover_in_last_12m";
		String TURNOVER_GENERATED_3 = "turnover_generated_3";
		String TURNOVER_GENERATED_2 = "turnover_generated_2";
		String TURNOVER_GENERATED_1 = "turnover_generated_1";
		String NUMBER_OF_DIFF_ITEMS_3 = "number_of_diff_items_3";
		String NUMBER_OF_DIFF_ITEMS_2 = "number_of_diff_items_2";
		String NUMBER_OF_DIFF_ITEMS_1 = "number_of_diff_items_1";
		String ORSY_SHELF_COUNT_AT_CUST = "orsy_shelf_count_at_cust";
		String CUSTOMER_12_MONTHS_PLAN = "customer_12_months_plan";
		String AVARAGE_PAYMENT_DAYS = "avarage_payment_days";
		String NUMBER_OF_SALESPERSONS_WORKING_WITH_CUSTOMER = "number_of_salespersons_working_with_customer";
		String DAYS_SINCE_OLDEST_OPEN_INVOICE = "days_since_oldest_open_invoice";
		String NEXT_15_DAYS_INVOICE_DUE_AMOUNT = "next_15_days_invoice_due_amount";
		String NEXT_15_DAYS_DUE_INVOICE_COUNT = "next_15_days_due_invoice_count";
		String FINANCIAL_CONTROL_STATUS = "financial_control_status";	
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
		String SALES_ORDER_DEVICE_NO = "sales_order_device_no";
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
		String CUSTOMER_BUSINESS_UNIT_CODE = "customer_business_unit_code";
		String NOTE1 = "note1";
		String NOTE2 = "note2";
		String NOTE3 = "note3";
	}

	public interface SaleOrderLinesColumns {
		String SALE_ORDER_ID = "sale_order_id";
		String LINE_NO = "line_no";
		String ITEM_ID = "item_id";
		String QUANTITY = "quantity";
		String QUANTITY_AVAILABLE = "quantity_available";
		String UNIT_OF_MEASURE = "unit_of_measure";
		String PRICE = "price";
		String MIN_DISCOUNT = "min_discount";
		String MAX_DISCOUNT = "max_discount";
		String REAL_DISCOUNT = "real_discount";
		String LINE_TOTAL = "line_total";
		String LINE_ORIGIN = "line_origin";
		String PRICE_EUR = "price_eur";
		String LINE_CAMPAIGN_STATUS = "campaign_status";
		String VERIFY_STATUS = "verify_status";
		String QUOTE_REFUSED_STATUS = "quote_refused_status";
		String PRICE_DISCOUNT_STATUS = "price_discount_status";
		String QUANTITY_AVAILABLE_STATUS = "quantity_available_status";
		String BACKORDER_STATUS = "backorder_status";
		String AVAILABLE_TO_WHOLE_SHIPMENT = "available_to_whole_shipment";
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
		String TYPE = "document_type";
		String NO = "item_no";
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
		String DEFAULT_LOCATION = "default_location";
		String ALLOW_LOCATION_CHANGE_ON_MDSD = "allow_location_change_on_mdsd";
		String TEAM_PARTNER = "team_partner";
		String ACCESS_TO_ALL_CUSTOMERS = "access_to_all_customers";
		String INVOICE_QUERY_NUMBER_OF_DAYS = "invoice_query_number_of_days";
		String PASSWORD = "password";
	}
	
	public interface ElectronicCardCustomerColumns {
		String CUSTOMER_ID = "customer_id";
		String ITEM_ID = "item_id";
		String JANUARY_QTY = "january_qty";
		String FEBRUARY_QTY = "february_qty";
		String MARCH_QTY = "march_qty";
		String APRIL_QTY = "april_qty";
		String MAY_QTY = "may_qty";
		String JUNE_QTY = "june_qty";
		String JULY_QTY = "july_qty";
		String AUGUST_QTY = "august_qty";
		String SEPTEMBER_QTY = "september_qty";
		String OCTOBER_QTY = "october_qty";
		String NOVEMBER_QTY = "november_qty";
		String DECEMBER_QTY = "december_qty";
		String TOTAL_SALE_QTY_CURRENT_YEAR = "total_sale_qty_current_year";
		String TOTAL_SALE_QTY_PRIOR_YEAR = "total_sale_qty_prior_year";
		String TOTAL_TURNOVER_CURRENT_YEAR = "total_turnover_current_year";
		String TOTAL_TURNOVER_PRIOR_YEAR = "total_turnover_prior_year";
		String SALES_LINE_COUNTS_CURRENT_YEAR = "sales_line_counts_current_year";
		String SALES_LINE_COUNTS_PRIOR_YEAR = "sales_line_counts_prior_year";
		String LAST_LINE_DISCOUNT = "last_line_discount";
		String CREATED_DATE = "created_date";
		String CREATED_BY = "created_by";
		String UPDATED_DATE = "updated_date";
		String UPDATED_BY = "updated_by";
	}
	
	public interface CustomerTradeAgreemntColumns {
		String CUSTOMER_ID = "customer_id";
		String ENTRY_TYPE = "entry_type";
		String CODE = "code";
		String MINIMUM_QUANTITY = "minimum_quantity";
		String STARTING_DATE = "starting_date";
		String ENDING_DATE = "ending_date";
		String ACTUAL_DISCOUNT = "actual_discount";
		String CREATED_DATE = "created_date";
		String CREATED_BY = "created_by";
		String UPDATED_DATE = "updated_date";
		String UPDATED_BY = "updated_by";
	}
	
	public interface SentOrdersStatusColumns {
		String DOCUMENT_TYPE = 	"document_type";
		String SENT_ORDER_NO = 	"sent_order_no";
		String CUSTOMER_ID = 	"customer_id";
		String ORDER_DATE = 	"order_date";
		String ORDER_STATUS_FOR_SHIPMENT = 	"order_status_for_shipment";
		String FIN_CONTROL_STATUS = 	"fin_control_status";
		String ORDER_CONDITION_STATUS = 	"order_condition_status";
		String USED_CREDIT_LIMIT_BY_EMPLOYEE = 	"used_credit_limit_by_employee";
		String ORDER_VALUE_STATUS = 	"order_value_status";
		String SPECIAL_QUOTE = 	"special_quote";
		String PRICES_INCLUDE_VAT = 	"prices_include_vat";
		String SALES_PERSON_ID = 	"sales_person_id"; 
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

	public static class CustomerTradeAgreemnt implements CustomerTradeAgreemntColumns, BaseColumns, CustomersColumns{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CUSTOMER_TRADE_AGREEMENT).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.customer_trade_agreement";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.customer_trade_agreement";
		
		public static Uri buildUri(String id) {
			return CONTENT_URI.buildUpon().appendPath(id).build();
		}
		
		public static String getId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static final String DEFAULT_SORT = Tables.CUSTOMER_TRADE_AGREEMENT + "." + CustomerTradeAgreemnt.CREATED_DATE + " ASC";
	
	} 
	
	public static class ElectronicCardCustomer implements ElectronicCardCustomerColumns, ItemsColumns, CustomersColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ELECT_CARD_CUSTOMER).build();
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.electronic_card_customer";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.electronic_card_customer";
		
		public static Uri buildUri(String id) {
			return CONTENT_URI.buildUpon().appendPath(id).build();
		}
		
		public static String getECCId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
		
		public static final String DEFAULT_SORT = Tables.ELECTRONIC_CARD_CUSTOMER + "." + ElectronicCardCustomerColumns.CREATED_DATE + " ASC";
	}

	
	
	
	public static class SalesPerson implements SalesPersonsColumns, BaseColumns{
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SALES_PERSON).build();
		
		public static Uri buildSalesPersonUri(int salesPersonId) {
			return CONTENT_URI.buildUpon().appendPath(String.valueOf(salesPersonId)).build();
		}
		
		public static String getSalesPersonId(Uri uri) {
			return uri.getPathSegments().get(1);
		}
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

	public static class Invoices implements InvoicesColumns, BaseColumns, SalesPersonsColumns, CustomersColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVOICES).build();

		public static Uri buildInvoicesUri(String invoicesId) {
			return CONTENT_URI.buildUpon().appendPath(invoicesId).build();
		}

		public static String getInvoicesId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static Uri buildCustomSearchUri(String customerNo, String status, String type) {
			return CONTENT_URI.buildUpon().appendPath(customerNo).appendPath(status).appendPath(type).appendPath(PATH_INVOICES_SEARCH).build();
		}
		
		public static String getCustomSearchFirstParamQuery(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getCustomSearchSecondParamQuery(Uri uri) {
			return uri.getPathSegments().get(2);
		}
		public static String getCustomSearchThirdParamQuery(Uri uri){
			return uri.getPathSegments().get(3);
		}
		
		/** Default "ORDER BY" clause. */
		public static final String DEFAULT_SORT = Invoices.POSTING_DATE + " DESC";
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
		public static Uri buildItemNoUri(String itemNo) {
			return CONTENT_URI.buildUpon().appendPath(PATH_ITEM_NO).appendPath(itemNo).build();
		}
		public static String getItemId(Uri uri){
			return uri.getPathSegments().get(1);
		}
		
		public static String getItemNo(Uri uri) {
			return uri.getPathSegments().get(2);
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
		public static Uri buildAutocompleteSearchUri(String text) {
			return CONTENT_URI.buildUpon().appendPath(text).appendPath(PATH_ITEM_SEARCH).build();
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

		public static Uri buildVisitsPlannedExport() {
			return CONTENT_URI.buildUpon().appendPath(PATH_PLANNED_VISITS_EXPORT).build();
		}
		
		public static Uri buildVisitsRealizedExport() {
			return CONTENT_URI.buildUpon().appendPath(PATH_REALIZED_VISITS_EXPORT).build();
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

		public static Uri buildSaleOrderExport() {
			return CONTENT_URI.buildUpon().appendPath(PATH_SALE_ORDERS_EXPORT).build();
		}
		
		public static Uri buildSaleOrderSaldo() {
			return CONTENT_URI.buildUpon().appendPath(PATH_SALE_ORDERS_SALDO).build();
		}
		
		public static String getSalesPersonId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getSaleOrderId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static Uri buildCustomSearchUri(String text, String status, String type) {
			return CONTENT_URI.buildUpon().appendPath(text).appendPath(status).appendPath(type).appendPath(PATH_SALE_ORDER_SEARCH_CUSTOM).build();
		}

		public static String getCustomSearchFirstParamQuery(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getCustomSearchSecondParamQuery(Uri uri) {
			return uri.getPathSegments().get(2);
		}
		public static String getCustomSearchThirdParamQuery(Uri uri){
			return uri.getPathSegments().get(3);
		}

		public static String getSaleOrderDocType(Uri uri) {
			return uri.getPathSegments().get(1);
		}
	}
	
	public static class SentOrders implements SaleOrdersColumns, BaseColumns, CustomersColumns, AuditColumns, SalesPersonsColumns {

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SENT_ORDERS).build();
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

		public static Uri buildSaleOrderExport() {
			return CONTENT_URI.buildUpon().appendPath(PATH_SALE_ORDERS_EXPORT).build();
		}
		
		public static String getSalesPersonId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getSaleOrderId(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static Uri buildCustomSearchUri(String text, String status, String type) {
			return CONTENT_URI.buildUpon().appendPath(text).appendPath(status).appendPath(type).appendPath(PATH_SALE_ORDER_SEARCH_CUSTOM).build();
		}

		public static String getCustomSearchFirstParamQuery(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getCustomSearchSecondParamQuery(Uri uri) {
			return uri.getPathSegments().get(2);
		}
		public static String getCustomSearchThirdParamQuery(Uri uri){
			return uri.getPathSegments().get(3);
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
		public static final String DEFAULT_SORT = "sale_order_lines." + MobileStoreContract.SaleOrderLines.LINE_NO + " ASC";

		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.sale_order_lines";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.sale_order_lines";

		public static Uri buildSaleOrderLineUri(String saleOrderLine) {
			return CONTENT_URI.buildUpon().appendPath(saleOrderLine).build();
		}

		public static Uri buildSaleOrderLineExportUri() {
			return CONTENT_URI.buildUpon().appendPath(PATH_SALE_ORDER_LINES_EXPORT).build();
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
	
	public static class SentOrdersStatus implements SentOrdersStatusColumns, BaseColumns {
		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SENT_ORDERS_STATUS).build();
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.rs.gopro.mobile_store.sent_orders_status";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.rs.gopro.mobile_store.sent_orders_status";
		
		public static final String DEFAULT_SORT = SentOrdersStatus.ORDER_DATE + " DESC";

		public static Uri buildSentOrdersStatusUri(int id) {
			return CONTENT_URI.buildUpon().appendPath(String.valueOf(id)).build();
		}
		
		public static Uri buildCustomSearchUri(String customerNo, String shipmentStatus) {
			return CONTENT_URI.buildUpon().appendPath(customerNo).appendPath(shipmentStatus).appendPath(PATH_SENT_ORDERS_STATUS_SEARCH).build();
		}

		public static String getCustomSearchFirstParamQuery(Uri uri) {
			return uri.getPathSegments().get(1);
		}

		public static String getCustomSearchSecondParamQuery(Uri uri) {
			return uri.getPathSegments().get(2);
		}
		
		
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
