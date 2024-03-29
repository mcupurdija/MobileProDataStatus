package rs.gopro.mobile_store.provider;

import java.util.ArrayList;

import rs.gopro.mobile_store.provider.MobileStoreContract.ActionPlan;
import rs.gopro.mobile_store.provider.MobileStoreContract.AppSettings;
import rs.gopro.mobile_store.provider.MobileStoreContract.Cities;
import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerAddresses;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerBusinessUnits;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerTradeAgreemnt;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.provider.MobileStoreContract.Generic;
import rs.gopro.mobile_store.provider.MobileStoreContract.GiftItems;
import rs.gopro.mobile_store.provider.MobileStoreContract.InvoiceLine;
import rs.gopro.mobile_store.provider.MobileStoreContract.Invoices;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.ItemsColumns;
import rs.gopro.mobile_store.provider.MobileStoreContract.ItemsOnPromotion;
import rs.gopro.mobile_store.provider.MobileStoreContract.Licensing;
import rs.gopro.mobile_store.provider.MobileStoreContract.Methods;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrderLines;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.MobileStoreContract.SalesPerson;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatus;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatusLineColumns;
import rs.gopro.mobile_store.provider.MobileStoreContract.SentOrdersStatusLines;
import rs.gopro.mobile_store.provider.MobileStoreContract.ServiceOrders;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.provider.MobileStoreContract.Users;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.OrderType;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.SelectionBuilder;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @see ContentProvider
 * @author aleksandar
 * 
 */
public class MobileStoreContentProvider extends ContentProvider {
	private static final String TAG = LogUtils
			.makeLogTag(MobileStoreContentProvider.class);

	private MobileStoreDatabase databaseHelper;

	// Used for the UriMacher
	private static final int USERS = 100;
	private static final int USERS_ID = 101;
	private static final int USERNAME = 102;

	private static final int INVOICES = 110;
	private static final int INVOICES_ID = 111;
	private static final int INVOICE_LINES_FROM_ORDER = 112;
	private static final int INVOICES_SEARCH = 113;

	private static final int CUSTOMERS = 120;
	private static final int CUSTOMERS_ID = 121;
	private static final int CUSTOMERS_NO = 122;
	//private static final int CUSTOMERS_SEARCH = 123;
	private static final int CUSTOMERS_CUSTOM_SEARCH = 124;
//	private static final int CUSTOMERS_BY_STATUS = 125;
	private static final int CUSTOMERS_BY_SALES_PERSON = 126;
	private static final int POTENTIAL_CUSTOMERS_EXPORT = 127;
	private static final int UPDATE_CUSTOMERS_EXPORT = 128;
	private static final int CUSTOMERS_BY_CONTACT_COMPANY_NO = 129;

	private static final int ITEMS = 130;
	//private static final int ITEMS_NO = 131;
	private static final int ITEMS_BY_STATUS = 132;
	private static final int ITEMS_CUSTOM_SEARCH = 133;
	private static final int ITEM_NO = 134;
	private static final int ITEMS_AUTOCOMPLETE_SEARCH = 135;
	private static final int ITEM_ID = 136;

	private static final int SALE_ORDERS = 140;
	private static final int SALE_ORDER_CUSTOM_SEARCH = 141;
	private static final int SALE_ORDER_BY_STATUS = 142;
	private static final int SALE_ORDERS_LIST = 143;
	private static final int SALE_ORDER = 144;
	private static final int SALE_ORDER_EXPORT = 145;
	private static final int SENT_ORDER_CUSTOM_SEARCH = 146;
	private static final int SENT_ORDER_BY_STATUS = 147;
	private static final int SALE_ORDERS_SALDO = 148;
	private static final int SALE_ORDERS_CLONE = 149;
	
	private static final int VISITS = 200;
	private static final int VISIT_ID = 201;
	private static final int VISITS_WITH_CUSTOMER = 202;
	private static final int VISITS_DATE = 203;
	private static final int VISITS_REALIZED_EXPORT = 204;
	private static final int VISITS_PLANNED_EXPORT = 205;
	

	private static final int SALE_ORDER_LINES_FROM_ORDER = 300;
	private static final int SALE_ORDER_LINE = 301;
	private static final int SALE_ORDER_LINES = 302;
	private static final int SALE_ORDER_LINES_EXPORT = 303;
	
	private static final int CONTACTS = 400;
	private static final int CONTACTS_ID = 401;
	private static final int CONTACTS_EXPORT = 402;
	private static final int CONTACTS_SEARCH = 403;
	
	private static final int CUSTOMER_ADDRESSES = 500;
	private static final int CUSTOMER_ADDRESSES_ID = 501;
	private static final int CUSTOMER_ADDRESSES_CUSTOMER_NO = 502;
	
	private static final int SYNC_LOGS_MAX = 600;
	private static final int SYNC_LOGS = 601;
	private static final int SYNC_LOGS_ID = 602;
	
	private static final int GENERIC = 700;
	
	private static final int ELECTRONIC_CARD_CUSTOMER = 800;
	private static final int ELECTRONIC_CARD_CUSTOMER_ID = 801;
	private static final int ELECTRONIC_CARD_CUSTOMER_ITEM_FILTER = 802;

	private static final int SALES_PERSONS = 900;
	private static final int SALES_PERSON_ID = 901;
	
	private static final int CUSTOMER_TRADE_AGREEMENT = 1000;
	private static final int CUSTOMER_TRADE_AGREEMENT_ID = 1001;
	
	private static final int SENT_ORDERS_STATUS = 1100;
	private static final int SENT_ORDERS_STATUS_SEARCH = 1101;
	
	private static final int INVOICE_LINES = 1200;
	private static final int INVOICE_LINES_REPORT = 1201;
	
	private static final int SENT_ORDERS_STATUS_LINES = 1300;
	private static final int SENT_ORDERS_STATUS_LINES_FROM_ORDER = 1301;
	private static final int SENT_ORDERS_STATUS_LINES_REPORT = 1302;
	
	private static final int SERVICE_ORDERS = 1400;
	private static final int SERVICE_ORDER_ID = 1401;
	private static final int SERVICE_ORDERS_EXPORT = 1402;
	
	private static final int APP_SETTINGS = 1500;
//	private static final int APP_SETTINGS_ID = 1501;
	
	private static final int CITIES = 1600;
	private static final int CITIES_BY_CITY_NAME = 1601;
	
	private static final int CUSTOMER_BUSINESS_UNITS = 1700;
	private static final int CUSTOMER_BUSINESS_UNITS_ID = 1701;
	
	private static final int ITEMS_ON_PROMOTION = 1800;
	private static final int ITEMS_ON_PROMOTION_ITEM_NO = 1801;
	
	private static final int ACTION_PLAN = 1900;
	private static final int ACTION_PLAN_ITEM_NO = 1901;
	
	private static final int GIFT_ITEMS = 2000;
	private static final int GIFT_ITEMS_ID = 2001;
	private static final int GIFT_ITEMS_VISIT_ID = 2002;
	
	private static final int METHODS = 2100;
	private static final int METHODS_ID = 2101;
	private static final int METHODS_SEARCH = 2102;
	
	private static final int LICENSING = 2200;
	private static final int LICENSING_ID = 2201;
	
	private static final UriMatcher mobileStoreURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		String authority = MobileStoreContract.CONTENT_AUTHORITY;
		mobileStoreURIMatcher.addURI(authority, "users", USERS);
		mobileStoreURIMatcher.addURI(authority, "users/#", USERS_ID);
		mobileStoreURIMatcher.addURI(authority, "users/username", USERNAME);

		mobileStoreURIMatcher.addURI(authority, "invoices", INVOICES);
		mobileStoreURIMatcher.addURI(authority, "invoices/#", INVOICES_ID);
		mobileStoreURIMatcher.addURI(authority, "invoice_lines_from_order/#", INVOICE_LINES_FROM_ORDER);
		mobileStoreURIMatcher.addURI(authority, "invoices/*/*/*/invoices_search",
				INVOICES_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "invoices/#/*/*/invoices_search",
				INVOICES_SEARCH);
		
		mobileStoreURIMatcher.addURI(authority, "customers", CUSTOMERS);
		mobileStoreURIMatcher.addURI(authority, "customers/potential_customers_export", POTENTIAL_CUSTOMERS_EXPORT);
		mobileStoreURIMatcher.addURI(authority, "customers/update_customers_export", UPDATE_CUSTOMERS_EXPORT);
		mobileStoreURIMatcher.addURI(authority, "customers_by_sales_person/*", CUSTOMERS_BY_SALES_PERSON);
		mobileStoreURIMatcher.addURI(authority, "customers_by_contact_company_no/*", CUSTOMERS_BY_CONTACT_COMPANY_NO);
		mobileStoreURIMatcher.addURI(authority, "customers/#", CUSTOMERS_ID);
		mobileStoreURIMatcher.addURI(authority, "customers/customer_no",
				CUSTOMERS_NO);
//		mobileStoreURIMatcher.addURI(authority, "customers/*/customer_no",
//				CUSTOMERS_BY_STATUS);
//		mobileStoreURIMatcher.addURI(authority, "customers/#/customer_no",
//				CUSTOMERS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "customers/#/*/customer_no",
				CUSTOMERS_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "customers/*/#/customer_no",
				CUSTOMERS_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "customers/*/*/customer_no",
				CUSTOMERS_CUSTOM_SEARCH);

		mobileStoreURIMatcher.addURI(authority, "items", ITEMS);
		mobileStoreURIMatcher.addURI(authority, "items/item_no/*", ITEM_NO);
		mobileStoreURIMatcher.addURI(authority, "items/*/item_search",
				ITEMS_AUTOCOMPLETE_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "items/*/item_no",
				ITEMS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "items/#/item_no",
				ITEMS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "items/*/#/item_no",
				ITEMS_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "items/#/*/item_no",
				ITEMS_CUSTOM_SEARCH);
		
		mobileStoreURIMatcher.addURI(authority, "items/*", ITEM_ID);
		
		mobileStoreURIMatcher.addURI(authority, "visits", VISITS);
		mobileStoreURIMatcher.addURI(authority, "visits/visits_planned_export", VISITS_PLANNED_EXPORT);
		mobileStoreURIMatcher.addURI(authority, "visits/visits_realized_export", VISITS_REALIZED_EXPORT);
		mobileStoreURIMatcher.addURI(authority, "visits/#", VISIT_ID);
		mobileStoreURIMatcher.addURI(authority, "visits/with_customer",
				VISITS_WITH_CUSTOMER);
		mobileStoreURIMatcher.addURI(authority, "visits/*/visits_date", VISITS_DATE);

		mobileStoreURIMatcher.addURI(authority, "sale_orders", SALE_ORDERS);
		mobileStoreURIMatcher.addURI(authority,
				"sale_orders/sale_orders_export", SALE_ORDER_EXPORT);
		mobileStoreURIMatcher.addURI(authority,
				"sale_orders/sale_orders_clone", SALE_ORDERS_CLONE);
		mobileStoreURIMatcher.addURI(authority,
				"sale_orders/sale_orders_saldo", SALE_ORDERS_SALDO);
		mobileStoreURIMatcher.addURI(authority, "sale_orders/#", SALE_ORDER);
		mobileStoreURIMatcher.addURI(authority, "sale_orders_list/*",
				SALE_ORDERS_LIST);
		
		// custom_search
		mobileStoreURIMatcher.addURI(authority, "sale_orders/*/*/custom_search",
				SALE_ORDER_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "sale_orders/#/*/custom_search",
				SALE_ORDER_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority,
				"sale_orders/*/*/*/custom_search", SALE_ORDER_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority,
				"sale_orders/#/*/*/custom_search", SALE_ORDER_CUSTOM_SEARCH);
//		mobileStoreURIMatcher.addURI(authority,
//				"sale_orders_export", SALE_ORDER_EXPORT);
		
		// custom_search
		mobileStoreURIMatcher.addURI(authority, "sent_orders/*/*/custom_search",
				SENT_ORDER_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "sent_orders/#/*/custom_search",
				SENT_ORDER_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority,
				"sent_orders/*/*/*/custom_search", SENT_ORDER_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority,
				"sent_orders/#/*/*/custom_search", SENT_ORDER_CUSTOM_SEARCH);
				
		mobileStoreURIMatcher.addURI(authority,
				"sale_order_lines", SALE_ORDER_LINES);
		mobileStoreURIMatcher.addURI(authority,
				"sale_order_lines/sale_order_lines_export", SALE_ORDER_LINES_EXPORT);
		mobileStoreURIMatcher.addURI(authority,
				"sale_order_lines/*", SALE_ORDER_LINE);
		mobileStoreURIMatcher.addURI(authority,
				"sale_order_lines_from_order/#", SALE_ORDER_LINES_FROM_ORDER);

		mobileStoreURIMatcher.addURI(authority, "contacts", CONTACTS);
		mobileStoreURIMatcher.addURI(authority, "contacts/#", CONTACTS_ID);
		mobileStoreURIMatcher.addURI(authority, "contacts/contacts_export", CONTACTS_EXPORT);
		mobileStoreURIMatcher.addURI(authority, "contacts/search/*", CONTACTS_SEARCH);
		
		mobileStoreURIMatcher.addURI(authority, "customer_addresses", CUSTOMER_ADDRESSES);
		mobileStoreURIMatcher.addURI(authority, "customer_addresses/customer_no/*", CUSTOMER_ADDRESSES_CUSTOMER_NO);
		mobileStoreURIMatcher.addURI(authority, "customer_addresses/*", CUSTOMER_ADDRESSES_ID);
		
		mobileStoreURIMatcher.addURI(authority, "sync_logs/#", SYNC_LOGS_ID);
		mobileStoreURIMatcher.addURI(authority, "sync_logs/*", SYNC_LOGS_ID);
		mobileStoreURIMatcher.addURI(authority, "sync_logs", SYNC_LOGS);
		
		mobileStoreURIMatcher.addURI(authority, "sync_logs/*/sync_logs_obejct_id", SYNC_LOGS_MAX);
		
		mobileStoreURIMatcher.addURI(authority, "generic/*",GENERIC);
		mobileStoreURIMatcher.addURI(authority, "electronic_card_customer", ELECTRONIC_CARD_CUSTOMER);
		mobileStoreURIMatcher.addURI(authority, "electronic_card_customer/#", ELECTRONIC_CARD_CUSTOMER_ITEM_FILTER);
		mobileStoreURIMatcher.addURI(authority, "electronic_card_customer/*", ELECTRONIC_CARD_CUSTOMER_ITEM_FILTER);
		
		mobileStoreURIMatcher.addURI(authority, "sales_persons", SALES_PERSONS);
		mobileStoreURIMatcher.addURI(authority, "sales_persons/*", SALES_PERSON_ID);
		
		mobileStoreURIMatcher.addURI(authority, "customer_trade_agreement", CUSTOMER_TRADE_AGREEMENT);
		mobileStoreURIMatcher.addURI(authority, "customer_trade_agreement/#", CUSTOMER_TRADE_AGREEMENT_ID);
		
		mobileStoreURIMatcher.addURI(authority, "sent_orders_status", SENT_ORDERS_STATUS);
		mobileStoreURIMatcher.addURI(authority, "sent_orders_status/*/*/custom_search",
				SENT_ORDERS_STATUS_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "sent_orders_status/#/*/custom_search",
				SENT_ORDERS_STATUS_SEARCH);
		
		mobileStoreURIMatcher.addURI(authority, "invoice_lines", INVOICE_LINES);
		mobileStoreURIMatcher.addURI(authority, "invoice_lines/invoice_lines_report", INVOICE_LINES_REPORT);
		
		mobileStoreURIMatcher.addURI(authority, "sent_orders_status_lines", SENT_ORDERS_STATUS_LINES);
		mobileStoreURIMatcher.addURI(authority, "sent_orders_status_lines/sent_orders_status_lines_report", SENT_ORDERS_STATUS_LINES_REPORT);
		mobileStoreURIMatcher.addURI(authority, "sent_orders_status_lines_from_order/#", SENT_ORDERS_STATUS_LINES_FROM_ORDER);
		
		mobileStoreURIMatcher.addURI(authority, "service_orders", SERVICE_ORDERS);
		mobileStoreURIMatcher.addURI(authority, "service_orders/service_orders_export", SERVICE_ORDERS_EXPORT);
		mobileStoreURIMatcher.addURI(authority, "service_orders/*", SERVICE_ORDER_ID);
		
		mobileStoreURIMatcher.addURI(authority, "app_settings", APP_SETTINGS);
		mobileStoreURIMatcher.addURI(authority, "app_settings/*", APP_SETTINGS);
		
		mobileStoreURIMatcher.addURI(authority, "cities", CITIES);
		mobileStoreURIMatcher.addURI(authority, "cities/*", CITIES_BY_CITY_NAME);
		
		mobileStoreURIMatcher.addURI(authority, "customer_business_units", CUSTOMER_BUSINESS_UNITS);
		mobileStoreURIMatcher.addURI(authority, "customer_business_units/*", CUSTOMER_BUSINESS_UNITS_ID);
		
		mobileStoreURIMatcher.addURI(authority, "items_on_promotion", ITEMS_ON_PROMOTION);
		mobileStoreURIMatcher.addURI(authority, "items_on_promotion/*", ITEMS_ON_PROMOTION_ITEM_NO);
		
		mobileStoreURIMatcher.addURI(authority, "action_plan", ACTION_PLAN);
		mobileStoreURIMatcher.addURI(authority, "action_plan/*", ACTION_PLAN_ITEM_NO);
		
		mobileStoreURIMatcher.addURI(authority, "gift_items", GIFT_ITEMS);
		mobileStoreURIMatcher.addURI(authority, "gift_items/#", GIFT_ITEMS_ID);
		mobileStoreURIMatcher.addURI(authority, "gift_items/visits/#", GIFT_ITEMS_VISIT_ID);
		
		mobileStoreURIMatcher.addURI(authority, "methods", METHODS);
		mobileStoreURIMatcher.addURI(authority, "methods/#", METHODS_ID);
		mobileStoreURIMatcher.addURI(authority, "methods/search/#", METHODS_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "methods/search/*", METHODS_SEARCH);
		
		mobileStoreURIMatcher.addURI(authority, "licensing", LICENSING);
		mobileStoreURIMatcher.addURI(authority, "licensing/#", LICENSING_ID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		LogUtils.log(Log.VERBOSE, TAG, "delete(uri = " + uri + ")");
		// int match = mobileStoreURIMatcher.match(uri);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		SelectionBuilder builder = buildSimpleSelection(uri);
		int deletedRows = builder.where(selection, selectionArgs).delete(database);
		getContext().getContentResolver().notifyChange(uri, null);
		return deletedRows;
	}

	/** {@inheritDoc} */
	@Override
	public String getType(Uri uri) {
		final int match = mobileStoreURIMatcher.match(uri);
		switch (match) {
		case CUSTOMERS:
			return Customers.CONTENT_TYPE;
		case CUSTOMERS_ID:
			return Customers.CONTENT_ITEM_TYPE;
		case CUSTOMERS_BY_SALES_PERSON:
			return Customers.CONTENT_TYPE;
		case CUSTOMERS_BY_CONTACT_COMPANY_NO:
			return Customers.CONTENT_TYPE;
		case VISITS:
			return Visits.CONTENT_TYPE;
		case VISIT_ID:
			return Visits.CONTENT_ITEM_TYPE;
		case VISITS_WITH_CUSTOMER:
			return Visits.CONTENT_TYPE;
		case SALE_ORDERS:
			return SaleOrders.CONTENT_TYPE;
		case SALE_ORDER:
			return SaleOrders.CONTENT_ITEM_TYPE;
		case SALE_ORDERS_LIST:
			return SaleOrders.CONTENT_TYPE;
		case SALE_ORDER_LINES_FROM_ORDER:
			return SaleOrderLines.CONTENT_TYPE;
		case SALE_ORDER_LINE:
			return SaleOrderLines.CONTENT_ITEM_TYPE;
		case SALE_ORDER_LINES:
			return SaleOrderLines.CONTENT_TYPE;
		case CUSTOMER_ADDRESSES:
			return CustomerAddresses.CONTENT_TYPE;
		case CUSTOMER_ADDRESSES_ID:
			return CustomerAddresses.CONTENT_ITEM_TYPE;
		case CUSTOMER_ADDRESSES_CUSTOMER_NO:
			return CustomerAddresses.CONTENT_TYPE;
		case INVOICE_LINES_FROM_ORDER:
			return InvoiceLine.CONTENT_TYPE;
		case ELECTRONIC_CARD_CUSTOMER_ID:
			return ElectronicCardCustomer.CONTENT_TYPE;
		case ELECTRONIC_CARD_CUSTOMER_ITEM_FILTER:
			return ElectronicCardCustomer.CONTENT_TYPE;
		case CUSTOMER_TRADE_AGREEMENT_ID:
			return CustomerTradeAgreemnt.CONTENT_TYPE;
		case CUSTOMER_BUSINESS_UNITS:
			return CustomerBusinessUnits.CONTENT_TYPE;
		case CUSTOMER_BUSINESS_UNITS_ID:
			return CustomerBusinessUnits.CONTENT_TYPE;
		case ITEMS_ON_PROMOTION:
			return ItemsOnPromotion.CONTENT_TYPE;
		case ACTION_PLAN:
			return ActionPlan.CONTENT_TYPE;
		case GIFT_ITEMS:
			return ActionPlan.CONTENT_TYPE;
		case METHODS:
			return Methods.CONTENT_TYPE;
		case LICENSING:
			return Licensing.CONTENT_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// LogUtils.log(Log.VERBOSE, TAG, "insert(uri = " + uri + ")");
		System.out.println("insert(uri = " + uri + ")");
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		int match = mobileStoreURIMatcher.match(uri);
		long id = 0;
		switch (match) {
		case USERS:
			id = database.insertOrThrow(Tables.USERS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Users.buildUsersUri("" + id);
		case INVOICES:
			id = database.insertOrThrow(Tables.INVOICES, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Invoices.buildInvoicesUri("" + id);
		case CUSTOMERS:
			id = database.insertOrThrow(Tables.CUSTOMERS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Customers.buildCustomersUri("" + id);
		case VISITS:
			id = database.insertOrThrow(Tables.VISITS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Visits.buildVisitUri("" + id);
		case CONTACTS:
			id = database.insertOrThrow(Tables.CONTACTS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Contacts.buildContactsUri("" + id);
		case CUSTOMER_ADDRESSES:
			id = database.insertOrThrow(Tables.CUSTOMER_ADDRESSES, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return CustomerAddresses.buildCustomerAddressUri("" + id);
		case SALE_ORDERS:
			id = database.insertOrThrow(Tables.SALE_ORDERS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return SaleOrders.buildSaleOrderUri("" + id);
		case SALE_ORDER_LINES:
			id = database.insertOrThrow(Tables.SALE_ORDER_LINES, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return SaleOrderLines.buildSaleOrderLineUri("" + id);
		case SALE_ORDER_LINES_FROM_ORDER:
			id = database.insertOrThrow(Tables.SALE_ORDER_LINES, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return SaleOrderLines.buildSaleOrderLineUri("" + id);
		case SYNC_LOGS:
			id = database.insertOrThrow(Tables.SYNC_LOGS,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return SyncLogs.buildSyncLogsUri(""+id);
		case SALES_PERSONS:
			id = database.insertOrThrow(Tables.SALES_PERSONS,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return SalesPerson.buildSalesPersonUri((int)id);
		case SENT_ORDERS_STATUS:
			id = database.insertOrThrow(Tables.SENT_ORDERS_STATUS,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return SentOrdersStatus.buildSentOrdersStatusUri((int)id);
		case SERVICE_ORDERS:
			id = database.insertOrThrow(Tables.SERVICE_ORDERS,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return ServiceOrders.buildServiceOrderUri((int)id);
		case APP_SETTINGS:
			id = database.insertOrThrow(Tables.APP_SETTINGS,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return AppSettings.buildAppSettingsUri((int)id);
		case CITIES:
			id = database.insertOrThrow(Tables.CITIES,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Cities.buildCitiesUri((int)id);
		case CUSTOMER_BUSINESS_UNITS:
			id = database.insertOrThrow(Tables.CUSTOMER_BUSINESS_UNITS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return CustomerBusinessUnits.buildCustomerBusinessUnitsUri("" + id);
		case ITEMS_ON_PROMOTION:
			id = database.insertOrThrow(Tables.ITEMS_ON_PROMOTION,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return ItemsOnPromotion.buildItemsOnPromotionUri((int)id);
		case ACTION_PLAN:
			id = database.insertOrThrow(Tables.ACTION_PLAN,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return ActionPlan.buildActionPlanUri((int)id);
		case GIFT_ITEMS:
			id = database.insertOrThrow(Tables.GIFT_ITEMS,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return GiftItems.buildGiftItemsUri((int)id);
		case METHODS:
			id = database.insertOrThrow(Tables.METHODS,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Methods.buildMethodsUri((int)id);
		case LICENSING:
			id = database.insertOrThrow(Tables.LICENSING,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Licensing.buildLicensingUri((int)id);
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

	}

	@Override
	public boolean onCreate() {
		databaseHelper = new MobileStoreDatabase(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		int match = mobileStoreURIMatcher.match(uri);
		switch (match) {
		default:
			SelectionBuilder builder = buildExpandedSelection(uri, match);
			Cursor cursor = builder.where(selection, selectionArgs).query(database, projection, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
			return cursor;
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		System.out.println("update(uri = " + uri + ")");
		LogUtils.log(Log.VERBOSE, TAG, "update(uri = " + uri + ")");
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		SelectionBuilder builder = buildSimpleSelection(uri);
		int updatedRows = builder.where(selection, selectionArgs).update(
				database, values);
		int match = mobileStoreURIMatcher.match(uri);
		System.out.println("UPDATE MATHCER " +match);
		switch (match) {
		case SALE_ORDER:
			/**
			 * This Uri is used for select so we must use the same to do notification
			 */
			getContext().getContentResolver().notifyChange(SaleOrders.CONTENT_URI, null);
		case SYNC_LOGS_ID:
			getContext().getContentResolver().notifyChange(SyncLogs.CONTENT_URI, null);	
		default:
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return updatedRows;
	}

	/**
	 * NOTE: this is for update NOT select.
	 * @param uri
	 * @return
	 */
	private SelectionBuilder buildSimpleSelection(Uri uri) {
		final SelectionBuilder builder = new SelectionBuilder();
		final int match = mobileStoreURIMatcher.match(uri);
		switch (match) {
		case USERS_ID:
			return builder.addTable(Users._ID);
		case USERNAME:
			return builder.addTable(Users.USERNAME);
		case INVOICES_ID:
			return builder.addTable(Invoices._ID);
		case VISITS:
			return builder.addTable(Tables.VISITS);
		case VISIT_ID:
			final String visitId = Visits.getVisitId(uri);
			return builder.addTable(Tables.VISITS).where(Tables.VISITS + "." + Visits._ID + "=?", visitId);
		case SALE_ORDERS:
			return builder.addTable(Tables.SALE_ORDERS);
		case SALE_ORDER:
			final String saleOrderId = SaleOrders.getSaleOrderId(uri);
			return builder.addTable(Tables.SALE_ORDERS).where(
					Tables.SALE_ORDERS + "." + SaleOrders._ID + "=?", saleOrderId);
		case SALE_ORDER_LINES:
			return builder.addTable(Tables.SALE_ORDER_LINES);
		case CONTACTS_ID:
			final String contactId = Contacts.getContactsId(uri);
			return builder.addTable(Tables.CONTACTS).where(
					Tables.CONTACTS + "." + Contacts._ID + "=?", contactId);
		case CUSTOMER_ADDRESSES:
			return builder.addTable(Tables.CUSTOMER_ADDRESSES);
		case SALE_ORDER_LINES_FROM_ORDER:
			final String saleOrderIdFromLine = SaleOrderLines.getSaleOrderId(uri);
			return builder.addTable(Tables.SALE_ORDER_LINES).where(
					Tables.SALE_ORDER_LINES + "." + SaleOrderLines.SALE_ORDER_ID + "=?", saleOrderIdFromLine);
		case SYNC_LOGS_ID:
			String syncId = SyncLogs.getSyncLogId(uri);
			return builder.addTable(Tables.SYNC_LOGS)
				.where(SyncLogs._ID + "=?", new String[]{syncId} );
		case SALES_PERSONS:
			return builder.addTable(Tables.SALES_PERSONS);
		case ITEMS: 
			return builder.addTable(Tables.ITEMS);
		case CUSTOMERS:
			return builder.addTable(Tables.CUSTOMERS);
		case CUSTOMERS_ID:
			String customerId = Customers.getCustomersId(uri);
			return builder.addTable(Tables.CUSTOMERS)
					.where(Customers._ID + "=?", new String[]{customerId});
		case INVOICES:
			return builder.addTable(Tables.INVOICES);
		case SERVICE_ORDERS:
			return builder.addTable(Tables.SERVICE_ORDERS);
		case APP_SETTINGS:
			return builder.addTable(Tables.APP_SETTINGS);
		case SENT_ORDERS_STATUS:
			return builder.addTable(Tables.SENT_ORDERS_STATUS);
		case SENT_ORDERS_STATUS_LINES:
			return builder.addTable(Tables.SENT_ORDERS_STATUS_LINES);
		case CITIES:
			return builder.addTable(Tables.CITIES);
		case CUSTOMER_BUSINESS_UNITS:
			return builder.addTable(Tables.CUSTOMER_BUSINESS_UNITS);
		case ITEMS_ON_PROMOTION:
			return builder.addTable(Tables.ITEMS_ON_PROMOTION);
		case ACTION_PLAN:
			return builder.addTable(Tables.ACTION_PLAN);
		case GIFT_ITEMS:
			return builder.addTable(Tables.GIFT_ITEMS);
		case GIFT_ITEMS_ID:
			String giftItemId = uri.getLastPathSegment();
			return builder.addTable(Tables.GIFT_ITEMS).where(Tables.GIFT_ITEMS + "." + GiftItems._ID + "=?", new String[] { giftItemId });
		case METHODS:
			return builder.addTable(Tables.METHODS);
		case METHODS_ID:
			String methodId = uri.getLastPathSegment();
			return builder.addTable(Tables.METHODS).where(Tables.METHODS + "." + Methods._ID + "=?", new String[] { methodId });
		case LICENSING:
			return builder.addTable(Tables.LICENSING);
		case LICENSING_ID:
			String licenseId = uri.getLastPathSegment();
			return builder.addTable(Tables.LICENSING).where(Tables.LICENSING + "." + Licensing._ID + "=?", new String[] { licenseId });
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	/**
	 * This is for select statement.
	 * @param uri
	 * @param match
	 * @return
	 */
	private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
		System.out.println("URI: " + uri);
		System.out.println(match);
		final SelectionBuilder builder = new SelectionBuilder();
		String customer_no = null;
		
		switch (match) {
		case USERS_ID:
			String userId = Users.getUserId(uri);
			return builder.addTable(Tables.USERS).where(Users._ID + "=?",
					userId);
		case USERNAME:
			return builder.addTable(Tables.USERS_JOIN_USERS_ROLE).
					mapToTable(Users._ID, Tables.USERS).
					mapToTable(Users.USERNAME, Tables.USERS).
					mapToTable(Users.PASSWORD, Tables.USERS).
					mapToTable(Users.LAST_LOGIN, Tables.USERS).
					mapToTable(Users.SALES_PERSON_ID, Tables.USERS).		
					mapToTable(Users.NAME, Tables.USERS_ROLE);
		case INVOICES_ID:
			String invoicesId = Invoices.getInvoicesId(uri);
			return builder.addTable(Tables.INVOICES).where(Invoices._ID + "=?",
					invoicesId);
		case INVOICES:
			return builder.addTable(Tables.INVOICES_JOIN_CUSTOMER);
		case INVOICE_LINES_FROM_ORDER:
			String invoiceid = InvoiceLine.getInvoiceId(uri);
			return builder.addTable(Tables.INVOICE_LINES)
					.where(InvoiceLine.INVOICES_ID + "=?", new String[] {invoiceid});
		case CUSTOMERS:
			return builder.addTable(Tables.CUSTOMERS);//.where(
					//Customers.BLOCKED_STATUS + "= ?", new String[] { "0" }); // TODO delete this!
		case POTENTIAL_CUSTOMERS_EXPORT:
			return builder.addTable(Tables.CUSTOMERS_EXPORT);
		case UPDATE_CUSTOMERS_EXPORT:
			return builder.addTable(Tables.CUSTOMERS_UPDATE_EXPORT);
		case CUSTOMERS_BY_SALES_PERSON:
			final String salesPersonIdOnCustomer = Customers.getCustomersSalesPersonId(uri);
			return builder.addTable(Tables.CUSTOMERS).where(
					Customers.SALES_PERSON_ID + "=?", salesPersonIdOnCustomer);
		case CUSTOMERS_BY_CONTACT_COMPANY_NO:
			final String contactCompanyNo = Customers.getContactCompanyNo(uri);
			return builder.addTable(Tables.CUSTOMERS).where(
					Customers.CONTACT_COMPANY_NO + "=?", contactCompanyNo);
		case CUSTOMERS_ID:
			String customerId = Customers.getCustomersId(uri);
			return builder.addTable(Tables.CUSTOMERS).where(
					Customers._ID + "=?", customerId);
		case CUSTOMERS_NO:
			return builder.addTable(Tables.CUSTOMERS);
//		case CUSTOMERS_BY_STATUS:
//			String query = Customers.getSearchQuery(uri);
//			return builder.addTable(Tables.CUSTOMERS).where(
//					Customers.BLOCKED_STATUS + "= ?", new String[] { query });
		case CUSTOMERS_CUSTOM_SEARCH:
			String customerCustomParam = Customers.getCustomSearchFirstParamQuery(uri);
			String customerStatus = Customers.getCustomSearchSecondParamQuery(uri);
			builder.addTable(Tables.CUSTOMERS);
			if (customerCustomParam != null && customerCustomParam.length() > 0 && !customerCustomParam.equals("noNoOrName")) {
				builder
					.where(Customers.CUSTOMER_NO + " like ? OR " + Customers.NAME + " like ? OR " + Customers.NAME_2 + " like ?",
							new String[] { /* "%" + */customerCustomParam + "%", "%" + customerCustomParam + "%", "%" + customerCustomParam + "%" });
			}
			if (customerStatus != null && customerStatus.length() > 0 && !customerStatus.equals("-1") && !customerStatus.equals("0")) {
				builder.where(Customers.CUSTOMER_TYPE + "= ?", new String[] { customerStatus });
			}
			return builder;
		case ITEMS:
			return builder.addTable(Tables.ITEMS);
		case ITEMS_BY_STATUS:
			String itemStat = Items.getItemStatus(uri);
			return builder.addTable(Tables.ITEMS);
		case ITEMS_CUSTOM_SEARCH:
			String itemCustom = Items.getCustomSearchFirstParamQuery(uri);
			String itemStatus = Items.getCustomSearchSecondParamQuery(uri);
			return builder
					.addTable(Tables.ITEMS)
					.where(Items.ITEM_NO + " like ? OR " + Items.DESCRIPTION + " like ? ", new String[] { itemCustom + "%", "%" + itemCustom + "%" });
		case ITEMS_AUTOCOMPLETE_SEARCH:
			String itemCustomSearch = Items.getCustomSearchFirstParamQuery(uri);
			return builder
					.addTable(Tables.ITEMS)
					.where(Items.ITEM_NO + " like ? OR " + Items.DESCRIPTION
							+ " like ? ",
							new String[] { itemCustomSearch + "%",
									"%" + itemCustomSearch + "%" });
		case ITEM_NO:
			String itemNo = Items.getItemNo(uri);
			return builder.addTable(Tables.ITEMS)
					.where(Items.ITEM_NO + "=?",itemNo);
		case ITEM_ID:
			String itemId = Items.getItemId(uri);
			return builder.addTable(Tables.ITEMS)
					.where(Items._ID + "=?",itemId);
		case VISITS:
			return builder.addTable(Tables.VISITS_JOIN_CUSTOMERS)
					.mapToTable(Visits._ID, Tables.VISITS)
					.mapToTable(Visits.SALES_PERSON_ID, Tables.VISITS)
					.mapToTable(Visits.CUSTOMER_ID, Tables.VISITS)
					.mapToTable(Visits.VISIT_DATE, Tables.VISITS)
					.mapToTable(Customers.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Customers.NAME, Tables.CUSTOMERS)
					.mapToTable(Customers.NAME_2, Tables.CUSTOMERS)
					.mapToTable(Visits.ENTRY_TYPE, Tables.VISITS)
					.mapToTable(Visits.VISIT_STATUS, Tables.VISITS)
					.mapToTable(Visits.ARRIVAL_TIME, Tables.VISITS)
					.mapToTable(Visits.DEPARTURE_TIME, Tables.VISITS)
					.mapToTable(Visits.ODOMETER, Tables.VISITS)
					.mapToTable(Visits.NOTE, Tables.VISITS)
					.mapToTable(Visits.IS_SENT, Tables.VISITS);
		case VISIT_ID:
			final String visitId = Visits.getVisitId(uri);
			return builder.addTable(Tables.VISITS_JOIN_CUSTOMERS)
					.mapToTable(Visits._ID, Tables.VISITS)
					.mapToTable(Visits.SALES_PERSON_ID, Tables.VISITS)
					.mapToTable(Visits.CUSTOMER_ID, Tables.VISITS)
					.mapToTable(Visits.VISIT_DATE, Tables.VISITS)
					.mapToTable(Customers.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Customers.NAME, Tables.CUSTOMERS)
					.mapToTable(Customers.NAME_2, Tables.CUSTOMERS)
					.mapToTable(Visits.ARRIVAL_TIME, Tables.VISITS)
					.mapToTable(Visits.DEPARTURE_TIME, Tables.VISITS)
					.mapToTable(Visits.ODOMETER, Tables.VISITS)
					.mapToTable(Visits.NOTE, Tables.VISITS)
					.where(Tables.VISITS + "." + Visits._ID + "=?", visitId);
		case VISITS_DATE:
			String visitDate = Visits.getDateOfVisit(uri); 
			return builder.addTable(Tables.VISITS_JOIN_CUSTOMERS)
					.mapToTable(Visits._ID, Tables.VISITS)
					.mapToTable(Visits.SALES_PERSON_ID, Tables.VISITS)
					.mapToTable(Visits.CUSTOMER_ID, Tables.VISITS)
					.mapToTable(Visits.VISIT_DATE, Tables.VISITS)
					.mapToTable(Customers.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Customers.NAME, Tables.CUSTOMERS)
					.mapToTable(Customers.NAME_2, Tables.CUSTOMERS)
					.mapToTable(Visits.ARRIVAL_TIME, Tables.VISITS)
					.mapToTable(Visits.DEPARTURE_TIME, Tables.VISITS)
					.mapToTable(Visits.ODOMETER, Tables.VISITS)
					.mapToTable(Visits.NOTE, Tables.VISITS).where(
					Visits.VISIT_DATE + " >= ? ",
					new String[] { visitDate });
		case SALE_ORDER:
			String saleOrderId = SaleOrders.getSaleOrderId(uri);
			return builder.addTable(Tables.SALE_ORDERS)
					.where(Tables.SALE_ORDERS + "." + SaleOrders._ID + "=?", saleOrderId);
		case SALE_ORDERS_CLONE:
			return builder.addTable(Tables.SALE_ORDERS);
		case SALE_ORDERS:
			return builder
					.addTable(Tables.SALE_ORDERS_JOIN_CUSTOMERS)
					.mapToTable(SaleOrders._ID, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SALES_ORDER_NO, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.DOCUMENT_TYPE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CUSTOMER_ID, Tables.SALE_ORDERS)
					.mapToTable(Customers.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Customers.NAME, Tables.CUSTOMERS)
					.mapToTable(Customers.NAME_2, Tables.CUSTOMERS)
					.mapToTable(SaleOrders.ORDER_DATE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.LOCATION_CODE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SHORTCUT_DIMENSION_1_CODE,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CURRENCY_CODE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.EXTERNAL_DOCUMENT_NO,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.QUOTE_NO, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.BACKORDER_SHIPMENT_STATUS,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.ORDER_STATUS_FOR_SHIPMENT,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.FIN_CONTROL_STATUS,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.ORDER_CONDITION_STATUS,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.USED_CREDIT_LIMIT_BY_EMPLOYEE,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.ORDER_VALUE_STATUS,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.QUOTE_REALIZED_STATUS,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SPECIAL_QUOTE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.QUOTE_VALID_DATE_TO,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CUST_USES_TRANSIT_CUST,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SALES_PERSON_ID, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SELL_TO_ADDRESS_ID,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SHIPP_TO_ADDRESS_ID,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CONTACT_PHONE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.PAYMENT_OPTION, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CHECK_STATUS_PHONE,
							Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.TOTAL, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.TOTAL_DISCOUNT, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.TOTAL_PDV, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.TOTAL_ITEMS, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.HIDE_REBATE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.FURTHER_SALE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.NOTE1, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.NOTE2, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.NOTE3, Tables.SALE_ORDERS);
		case SALE_ORDERS_LIST:
			final String salesPersonId = SaleOrders.getSalesPersonId(uri);
			return builder
					.addTable(Tables.SALE_ORDERS_JOIN_CUSTOMERS)
					.mapToTable(SaleOrders._ID, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SALES_PERSON_ID, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SALES_ORDER_NO, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.DOCUMENT_TYPE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CUSTOMER_ID, Tables.SALE_ORDERS)
					.mapToTable(Customers.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Customers.NAME, Tables.CUSTOMERS)
					.mapToTable(Customers.NAME_2, Tables.CUSTOMERS)
					.mapToTable(SaleOrders.ORDER_DATE, Tables.SALE_ORDERS)
					.where(Tables.SALE_ORDERS + "."
							+ SaleOrders.SALES_PERSON_ID + "=?", salesPersonId);
		case SALE_ORDER_BY_STATUS:
			//String saleOrderDocType = SaleOrders.getSaleOrderDocType(uri);
			String saleOrderDocType = SaleOrders.getCustomSearchFirstParamQuery(uri);
			String noType = SaleOrders.getCustomSearchSecondParamQuery(uri);
			OrderType orderType = ApplicationConstants.OrderType.find(noType);
			String condition = Tables.SALE_ORDERS+"."+SaleOrders.SALES_ORDER_NO  + " is null or "+Tables.SALE_ORDERS+"."+SaleOrders.SALES_ORDER_NO  + " = ''";
			if(OrderType.SENT_ORDER.equals(orderType)){
				condition = Tables.SALE_ORDERS+"."+SaleOrders.SALES_ORDER_NO  + " is not null and "+Tables.SALE_ORDERS+"."+SaleOrders.SALES_ORDER_NO  + " <> ''";
			}
			return builder.addTable(Tables.SALE_ORDERS_JOIN_CUSTOMERS)
					.where(
					Tables.SALE_ORDERS+"."+SaleOrders.DOCUMENT_TYPE + "= ?",new String[] { saleOrderDocType })
					.where(condition, new String []{});
		case SALE_ORDER_CUSTOM_SEARCH:
			String saleCustomParam = SaleOrders
					.getCustomSearchFirstParamQuery(uri);
			String saleDocType = SaleOrders
					.getCustomSearchSecondParamQuery(uri);
			String saleOrderType = SaleOrders
					.getCustomSearchThirdParamQuery(uri);
			OrderType orderTypeSale = ApplicationConstants.OrderType
					.find(saleOrderType);
			String additionalCondition = Tables.SALE_ORDERS + "."
					+ SaleOrders.SALES_ORDER_NO + " is null";
			if (OrderType.SENT_ORDER.equals(orderTypeSale)) {
				additionalCondition = Tables.SALE_ORDERS + "."
						+ SaleOrders.SALES_ORDER_NO + " is not null";
			}
			builder.addTable(Tables.SALE_ORDERS_JOIN_CUSTOMERS);
			if (saleCustomParam != null && saleCustomParam.length() > 0 && !saleCustomParam.equals("noCustomer")) {
				builder.where(Tables.CUSTOMERS + "." + Customers.CUSTOMER_NO
						+ " like ? OR " + Tables.CUSTOMERS + "." + Customers.NAME + " like ?", new String[] { "%" + saleCustomParam + "%", "%" + saleCustomParam + "%" });
			}
			if (saleDocType != null && saleDocType.length() > 0 && !saleDocType.equals("-1")) {
				builder.where(Tables.SALE_ORDERS + "." + SaleOrders.DOCUMENT_TYPE
						+ "= ? ", new String[] { saleDocType });
			}
			builder.where(additionalCondition, new String[] {});
			return builder;
		case SENT_ORDER_BY_STATUS:
			//String saleOrderDocType = SaleOrders.getSaleOrderDocType(uri);
			String saleOrderDocType1 = SaleOrders.getCustomSearchFirstParamQuery(uri);
			String noType1 = SaleOrders.getCustomSearchSecondParamQuery(uri);
			OrderType orderType1 = ApplicationConstants.OrderType.find(noType1);
			String condition1 = Tables.SALE_ORDERS+"."+SaleOrders.SALES_ORDER_NO  + " is null";
			if(OrderType.SENT_ORDER.equals(orderType1)){
				condition1 = Tables.SALE_ORDERS+"."+SaleOrders.SALES_ORDER_NO  + " is not null";
			}
			return builder.addTable(Tables.SALE_ORDERS_JOIN_CUSTOMERS)
					.where(
					Tables.SALE_ORDERS+"."+SaleOrders.DOCUMENT_TYPE + "= ?",new String[] { saleOrderDocType1 })
					.where(condition1, new String []{});
		case SENT_ORDER_CUSTOM_SEARCH:
			String saleCustomParam1 = SaleOrders
					.getCustomSearchFirstParamQuery(uri);
			String saleDocType1 = SaleOrders
					.getCustomSearchSecondParamQuery(uri);
			String saleOrderType1 = SaleOrders.getCustomSearchThirdParamQuery(uri);
			OrderType orderTypeSale1 = ApplicationConstants.OrderType.find(saleOrderType1);
			String additionalCondition1 = Tables.SALE_ORDERS+"."+SaleOrders.SALES_ORDER_NO  + " is null";
			if(OrderType.SENT_ORDER.equals(orderTypeSale1)){
				additionalCondition1 = Tables.SALE_ORDERS+"."+SaleOrders.SALES_ORDER_NO  + " is not null";
			}
			return builder
					.addTable(Tables.SALE_ORDERS_JOIN_CUSTOMERS)
					.where(Tables.SALE_ORDERS+"."+SaleOrders.SALES_ORDER_NO + " like ? ",
							new String[] { "%" + saleCustomParam1 + "%" })
					.where(Tables.SALE_ORDERS+"."+SaleOrders.DOCUMENT_TYPE + "= ? ",new String[] { saleDocType1 })
					.where(additionalCondition1, new String []{});
			
		case SALE_ORDER_LINE:
			final String salesOrderlineId = SaleOrderLines.getSaleOrderLineId(uri);
			return builder
					.addTable(Tables.SALE_ORDER_LINES)
					.where(SaleOrderLines._ID + "=?", salesOrderlineId);
		case SALE_ORDER_LINES:
			return builder.addTable(Tables.SALE_ORDER_LINES);
		case SALE_ORDER_LINES_FROM_ORDER:
			final String salesOrderId = SaleOrderLines.getSaleOrderLineId(uri);
			return builder
					.addTable(Tables.SALE_ORDER_LINES_JOIN_ITEMS)
					.mapToTable(SaleOrderLines._ID, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.SALE_ORDER_ID, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.ITEM_NO, Tables.ITEMS)
					.mapToTable(SaleOrderLines.DESCRIPTION, Tables.ITEMS)
					.mapToTable(SaleOrderLines.LINE_NO, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.QUANTITY, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.PRICE_EUR, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.REAL_DISCOUNT, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.MAX_DISCOUNT, Tables.SALE_ORDER_LINES)
					.where(SaleOrderLines.SALE_ORDER_ID + "=?", salesOrderId);
		case CONTACTS_ID:
			final String contactId = Contacts.getContactsId(uri);
			return builder.addTable(Tables.CONTACTS).where(
					Tables.CONTACTS + "." + Contacts._ID + "=?", contactId);
		case CONTACTS_SEARCH:
			String contactsCustomSearch = Contacts.getContactsCustomSearch(uri);
			builder.addTable(Tables.CONTACTS);
			if (contactsCustomSearch != null && contactsCustomSearch.trim().length() > 0 && !contactsCustomSearch.equals("noNoOrName")) {	
				builder.where(
						Contacts.NAME + " LIKE ?",
						new String[] { "%" + contactsCustomSearch + "%" });
			}
			return builder;
		case CONTACTS:
			return builder.addTable(Tables.CONTACTS);
		case CONTACTS_EXPORT:
			return builder.addTable(Tables.CONTACTS_EXPORT)
					.mapToTable(MobileStoreContract.Contacts.CONTACT_NO, Tables.CONTACTS)
					.mapToTable(MobileStoreContract.Contacts.NAME, Tables.CONTACTS)
					.mapToTable(MobileStoreContract.Contacts.NAME2, Tables.CONTACTS)
					.mapToTable(MobileStoreContract.Contacts.PHONE, Tables.CONTACTS)
					.mapToTable(MobileStoreContract.Contacts.MOBILE_PHONE, Tables.CONTACTS)
					.mapToTable(MobileStoreContract.SalesPerson.SALE_PERSON_NO, Tables.SALES_PERSONS)
					.mapToTable(MobileStoreContract.Contacts.EMAIL, Tables.CONTACTS)
					.mapToTable(MobileStoreContract.Contacts.COMPANY_NO, Tables.CONTACTS)
					.mapToTable(MobileStoreContract.Contacts.DEPARTMENT, Tables.CONTACTS)
					.mapToTable(MobileStoreContract.Contacts.POSITION, Tables.CONTACTS);
		case CUSTOMER_ADDRESSES:
			return builder.addTable(Tables.CUSTOMER_ADDRESSES);
		case CUSTOMER_ADDRESSES_CUSTOMER_NO:
			final String customerNo = CustomerAddresses.getSearchByCustomerNo(uri);
			return builder.addTable(Tables.CUSTOMER_ADDRESSES)
					.where(CustomerAddresses.CUSTOMER_NO + " like ?", new String[] { customerNo });
		case SYNC_LOGS_MAX:
			final String  syncObjectId = SyncLogs.getSyncLogObjectId(uri);
			return builder.addTable(Tables.SYNC_LOGS)
					.where(SyncLogs.SYNC_OBJECT_ID + "=?", new String[]{syncObjectId});
		case SYNC_LOGS :
			return builder.addTable(Tables.SYNC_LOGS);
		case GENERIC :
			final String tableName = Generic.getTableName(uri);
			return builder.addTable(tableName);
		case ELECTRONIC_CARD_CUSTOMER :
			return builder.addTable(Tables.EL_CARD_CUSTOMER_JOIN_CUSTOMER_JOIN_ITEM);
//					.mapToTable(ElectronicCardCustomer._ID, Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.CUSTOMER_ID , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.CUSTOMER_NO, Tables.CUSTOMERS)
//					.mapToTable(ElectronicCardCustomer.ITEM_ID , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.ITEM_NO, Tables.ITEMS)
//					.mapToTable(ElectronicCardCustomer.JANUARY_QTY, Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.FEBRUARY_QTY, Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.MARCH_QTY, Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.APRIL_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.MAY_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.JUNE_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.JULY_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.AUGUST_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.SEPTEMBER_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.OCTOBER_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.NOVEMBER_QTY, Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.DECEMBER_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.TOTAL_SALE_QTY_CURRENT_YEAR , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.TOTAL_SALE_QTY_PRIOR_YEAR , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.TOTAL_TURNOVER_CURRENT_YEAR, Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.TOTAL_TURNOVER_PRIOR_YEAR , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.SALES_LINE_COUNTS_CURRENT_YEAR , Tables.ELECTRONIC_CARD_CUSTOMER )
//					.mapToTable(ElectronicCardCustomer.SALES_LINE_COUNTS_PRIOR_YEAR , Tables.ELECTRONIC_CARD_CUSTOMER );
		case ELECTRONIC_CARD_CUSTOMER_ITEM_FILTER :
			String itemFilter = uri.getLastPathSegment();
			builder.addTable(Tables.EL_CARD_CUSTOMER_JOIN_CUSTOMER_JOIN_ITEM);
			builder.where(Tables.ITEMS + "." + Items.ITEM_NO + " LIKE ? OR " + Tables.ITEMS + "." + Items.DESCRIPTION + " LIKE ?", new String[] { "%" + itemFilter + "%", "%" + itemFilter + "%" });
			return builder;
		case SALES_PERSONS:
			return builder.addTable(Tables.SALES_PERSONS);
		case SALES_PERSON_ID:
			final String sales_person_id = SalesPerson.getSalesPersonId(uri);
			return builder.addTable(Tables.SALES_PERSONS)
					.where(SalesPerson._ID + "=?", new String[]{ sales_person_id });
		case SALE_ORDER_EXPORT:
			return builder.addTable(Tables.SALE_ORDERS_EXPORT)
					.mapToTable(MobileStoreContract.SaleOrders.DOCUMENT_TYPE, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.Customers.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(MobileStoreContract.SaleOrders.LOCATION_CODE, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.SHORTCUT_DIMENSION_1_CODE, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.EXTERNAL_DOCUMENT_NO, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.QUOTE_NO, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SalesPerson.SALE_PERSON_NO, Tables.SALES_PERSONS)
					//.mapToTable(MobileStoreContract.SaleOrders.SELL_TO_ADDRESS_ID, Tables.SALE_ORDERS)
					//.mapToTable(MobileStoreContract.SaleOrders.SHIPP_TO_ADDRESS_ID, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.CustomerAddresses.ADDRESS_NO, "ca1", "sell_to_address_no")
					.mapToTable(MobileStoreContract.CustomerAddresses.ADDRESS_NO, "ca2", "shipp_to_address_no")
					.mapToTable(MobileStoreContract.SaleOrders.REQUESTED_DELIVERY_DATE, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.CUST_USES_TRANSIT_CUST, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.CONTACT_NAME, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.CONTACT_PHONE, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.HIDE_REBATE, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.FURTHER_SALE, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.PAYMENT_OPTION, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.BACKORDER_SHIPMENT_STATUS, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.QUOTE_REALIZED_STATUS, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.ORDER_CONDITION_STATUS, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.SHIPMENT_METHOD_CODE, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.CUSTOMER_BUSINESS_UNIT_CODE, Tables.SALE_ORDERS);
		case SALE_ORDER_LINES_EXPORT:
			return builder.addTable(Tables.SALE_ORDER_LINES_EXPORT)
					.mapToTable(MobileStoreContract.SaleOrders.DOCUMENT_TYPE, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrders.SALES_ORDER_DEVICE_NO, Tables.SALE_ORDERS)
					.mapToTable(MobileStoreContract.SaleOrderLines.LINE_NO, Tables.SALE_ORDER_LINES)
					.mapToTable(MobileStoreContract.Items.ITEM_NO, Tables.ITEMS)
					.mapToTable(MobileStoreContract.SaleOrderLines.LOCATION_CODE, Tables.SALE_ORDER_LINES)
					.mapToTable(MobileStoreContract.SaleOrderLines.QUANTITY, Tables.SALE_ORDER_LINES)
					.mapToTable(MobileStoreContract.SaleOrderLines.PRICE, Tables.SALE_ORDER_LINES)
					.mapToTable(MobileStoreContract.SaleOrderLines.REAL_DISCOUNT, Tables.SALE_ORDER_LINES)
					.mapToTable(MobileStoreContract.SaleOrderLines.QUOTE_REFUSED_STATUS, Tables.SALE_ORDER_LINES)
					.mapToTable(MobileStoreContract.SaleOrderLines.BACKORDER_STATUS, Tables.SALE_ORDER_LINES)
					.mapToTable(MobileStoreContract.SaleOrderLines.AVAILABLE_TO_WHOLE_SHIPMENT, Tables.SALE_ORDER_LINES);
		case CUSTOMER_TRADE_AGREEMENT:
			return builder.addTable(Tables.CUSTOMER_AGREEMENT_JOIN_CUSTOMER)
					.mapToTable(CustomerTradeAgreemnt._ID, Tables.CUSTOMER_TRADE_AGREEMENT )
					.mapToTable(CustomerTradeAgreemnt.CUSTOMER_ID , Tables.CUSTOMER_TRADE_AGREEMENT )
					.mapToTable(CustomerTradeAgreemnt.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(CustomerTradeAgreemnt.ENTRY_TYPE, Tables.CUSTOMER_TRADE_AGREEMENT)
					.mapToTable(CustomerTradeAgreemnt.CODE, Tables.CUSTOMER_TRADE_AGREEMENT)
					.mapToTable(CustomerTradeAgreemnt.MINIMUM_QUANTITY, Tables.CUSTOMER_TRADE_AGREEMENT)
					.mapToTable(CustomerTradeAgreemnt.STARTING_DATE, Tables.CUSTOMER_TRADE_AGREEMENT)
					.mapToTable(CustomerTradeAgreemnt.ENDING_DATE, Tables.CUSTOMER_TRADE_AGREEMENT)
					.mapToTable(CustomerTradeAgreemnt.ACTUAL_DISCOUNT, Tables.CUSTOMER_TRADE_AGREEMENT);
		case VISITS_PLANNED_EXPORT:
			return builder.addTable(Tables.VISITS_EXPORT)
					.mapToTable(MobileStoreContract.SalesPerson.SALE_PERSON_NO, Tables.SALES_PERSONS)
					.mapToTable(MobileStoreContract.Visits.VISIT_DATE, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.ARRIVAL_TIME, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.DEPARTURE_TIME, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.POTENTIAL_CUSTOMER, Tables.VISITS)
					.mapToTable(MobileStoreContract.Customers.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(MobileStoreContract.Visits.NOTE, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.IS_DELETED, Tables.VISITS);
		case VISITS_REALIZED_EXPORT:
			return builder.addTable(Tables.VISITS_EXPORT)
					.mapToTable(MobileStoreContract.SalesPerson.SALE_PERSON_NO, Tables.SALES_PERSONS)
					.mapToTable(MobileStoreContract.Visits.VISIT_DATE, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.ARRIVAL_TIME, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.DEPARTURE_TIME, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.VISIT_RESULT, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.POTENTIAL_CUSTOMER, Tables.VISITS)
					.mapToTable(MobileStoreContract.Customers.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(MobileStoreContract.Visits.ODOMETER, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.NOTE, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.ADDRESS_NO, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.LATITUDE, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.LONGITUDE, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.ACCURACY, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.LATITUDE_END, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.LONGITUDE_END, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.ACCURACY_END, Tables.VISITS)
					.mapToTable(MobileStoreContract.Visits.VALID_LOCATION, Tables.VISITS);
		case SALE_ORDERS_SALDO:
			return  builder.addTable(Tables.SALE_ORDERS_SALDO);
		case INVOICES_SEARCH:
			customer_no = Invoices
					.getCustomSearchFirstParamQuery(uri);
			String invoiceOpenType = SaleOrders
					.getCustomSearchSecondParamQuery(uri);
			String invoiceDocType = SaleOrders.getCustomSearchThirdParamQuery(uri);

			builder
				.addTable(Tables.INVOICES_JOIN_CUSTOMER);
			if (!customer_no.equals("noCustomer")) {
				builder.where(Tables.CUSTOMERS+"." + MobileStoreContract.Customers.CUSTOMER_NO + " like ? OR " + 
						MobileStoreContract.Customers.NAME + " like ?",
							new String[] { "%" + customer_no + "%", "%" + customer_no + "%" });
			}
			if (!invoiceDocType.equals("-1")) {
				builder.where(Tables.INVOICES+"."+MobileStoreContract.Invoices.DOCUMENT_TYPE + " = ? ",new String[] { invoiceDocType });
			}
			if (!invoiceOpenType.equals("-1")) {
				builder.where(Tables.INVOICES+"."+MobileStoreContract.Invoices.OPEN + " = ? ",new String[] { invoiceOpenType });
			}
			return builder;
		case SENT_ORDERS_STATUS:
			return builder.addTable(Tables.SENT_ORDERS_STATUS_JOIN_CUSTOMERS);
		case SENT_ORDERS_STATUS_SEARCH:
			customer_no = SentOrdersStatus
					.getCustomSearchFirstParamQuery(uri);
			String shipmentStatus = SentOrdersStatus
					.getCustomSearchSecondParamQuery(uri);

			builder
				.addTable(Tables.SENT_ORDERS_STATUS_JOIN_CUSTOMERS);
			if (!customer_no.equals("noCustomer")) {
				builder.where(Tables.CUSTOMERS+"."+MobileStoreContract.Customers.CUSTOMER_NO + " like ?  OR " + Tables.CUSTOMERS + "." + MobileStoreContract.Customers.NAME + " like ?",
							new String[] { "%" + customer_no + "%", "%" + customer_no + "%" });
			}
			if (!shipmentStatus.equals("-1")) {
				if (shipmentStatus.equals("0")) {
					builder.where(Tables.SENT_ORDERS_STATUS+"."+MobileStoreContract.SentOrdersStatus.ORDER_STATUS_FOR_SHIPMENT + " >= ? ", new String[] { "0" });
				} else {
					int option = 0;
					try {
						option = Integer.valueOf(shipmentStatus);
					} catch (Exception e) {
						LogUtils.LOGE(TAG, "", e);
					}			
					builder.where(Tables.SENT_ORDERS_STATUS+"."+MobileStoreContract.SentOrdersStatus.ORDER_STATUS_FOR_SHIPMENT + " = ? ", new String[] { String.valueOf((option-1)) });
				}
			}
			return builder;
		case INVOICE_LINES:
			return builder.addTable(Tables.INVOICE_LINES);
		case SENT_ORDERS_STATUS_LINES:
			return builder.addTable(Tables.SENT_ORDERS_STATUS_LINES);
		case SENT_ORDERS_STATUS_LINES_FROM_ORDER:
			return builder.addTable(Tables.SENT_ORDERS_STATUS_LINES)
					.where(SentOrdersStatusLines.SENT_ORDER_STATUS_ID + " = ? ", new String[] { SentOrdersStatusLines.getSentOrdersStatusId(uri) });
		case SENT_ORDERS_STATUS_LINES_REPORT:
			return builder.addTable(Tables.SENT_ORDERS_STATUS_LINES_REPORT);
		case INVOICE_LINES_REPORT:
			return builder.addTable(Tables.INVOICE_LINES_REPORT);
		case SERVICE_ORDERS:
			return builder.addTable(Tables.SERVICE_ORDERS);
		case APP_SETTINGS:
			return builder.addTable(Tables.APP_SETTINGS);
		case SERVICE_ORDER_ID:
			return builder.addTable(Tables.SERVICE_ORDERS)
					.where(Tables.SERVICE_ORDERS+".id=?", new String[] { ServiceOrders.getServiceOrderId(uri) });
		case SERVICE_ORDERS_EXPORT:
			return builder.addTable(Tables.SERVICE_ORDERS_EXPORT);
		case CITIES:
			return builder.addTable(Tables.CITIES);
		case CITIES_BY_CITY_NAME:
			String citySearchParam = Cities.getCustomSearchFirstParamQuery(uri);
			return builder.addTable(Tables.CITIES).where(Cities.CITY + " like ? OR " + Cities.ZIP + " like ?", new String[] {citySearchParam + "%", citySearchParam + "%"});
		case CUSTOMER_BUSINESS_UNITS:
			return builder.addTable(Tables.CUSTOMER_BUSINESS_UNITS);
		case CUSTOMER_BUSINESS_UNITS_ID:
			final String businessUnitId = uri.getLastPathSegment();
			return builder.addTable(Tables.CUSTOMER_BUSINESS_UNITS)
					.where(CustomerBusinessUnits._ID + "=?", new String[] { businessUnitId });
		case ITEMS_ON_PROMOTION:
			return builder.addTable(Tables.ITEMS_ON_PROMOTION_JOIN_ITEMS);
		case ITEMS_ON_PROMOTION_ITEM_NO:
			final String promoItem = ItemsOnPromotion.getCustomSearchFirstParamQuery(uri);
			return builder.addTable(Tables.ITEMS_ON_PROMOTION_JOIN_ITEMS).where(Tables.ITEMS + "." + ItemsColumns.ITEM_NO + " LIKE ? OR " + Tables.ITEMS + "." + ItemsColumns.DESCRIPTION + " LIKE ?", new String[] { promoItem + "%", "%" + promoItem + "%" });
		case ACTION_PLAN:
			return builder.addTable(Tables.ACTION_PLAN_JOIN_ITEMS);
		case ACTION_PLAN_ITEM_NO:
			final String actionPlanItem = ActionPlan.getCustomSearchFirstParamQuery(uri);
			return builder.addTable(Tables.ACTION_PLAN_JOIN_ITEMS).where(Tables.ITEMS + "." + ItemsColumns.ITEM_NO + " LIKE ? OR " + Tables.ITEMS + "." + ItemsColumns.DESCRIPTION + " LIKE ?", new String[] { actionPlanItem + "%", "%" + actionPlanItem + "%" });
		case GIFT_ITEMS:
			return builder.addTable(Tables.GIFT_ITEMS_JOIN_ITEMS_CUSTOMERS_VISITS);
		case GIFT_ITEMS_ID:
			final String giftItemsId = uri.getLastPathSegment();
			return builder.addTable(Tables.GIFT_ITEMS_JOIN_ITEMS_CUSTOMERS_VISITS).where(Tables.GIFT_ITEMS + "." + BaseColumns._ID + "=?", new String[] { giftItemsId });
		case GIFT_ITEMS_VISIT_ID:
			final String giftItemsVisitId = uri.getLastPathSegment();
			return builder.addTable(Tables.GIFT_ITEMS_JOIN_ITEMS_CUSTOMERS_VISITS).where(Tables.GIFT_ITEMS + "." + GiftItems.VISIT_ID + "=?", new String[] { giftItemsVisitId });
		case METHODS:
			return builder.addTable(Tables.METHODS_JOIN_ITEMS_JOIN_CUSTOMERS);
		case METHODS_ID:
			final String methodId = uri.getLastPathSegment();
			return builder.addTable(Tables.METHODS_JOIN_ITEMS_JOIN_CUSTOMERS).where(Tables.METHODS + "." + BaseColumns._ID + "=?", new String[] { methodId });
		case METHODS_SEARCH:
			String methodSearchParam = uri.getLastPathSegment();
			return builder.addTable(Tables.METHODS_JOIN_ITEMS_JOIN_CUSTOMERS).where(Tables.ITEMS + "." + Items.ITEM_NO + " like ? OR " + Tables.ITEMS + "." + Items.DESCRIPTION + " like ?", new String[] { "%" + methodSearchParam + "%", "%" + methodSearchParam + "%"});
		case LICENSING:
			return builder.addTable(Tables.LICENSING);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	/**
	 * Apply the given set of {@link ContentProviderOperation}, executing inside
	 * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
	 * any single one fails.
	 */
	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		final SQLiteDatabase db = databaseHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[numOperations];
			for (int i = 0; i < numOperations; i++) {
				results[i] = operations.get(i).apply(this, results, i);
			}
			db.setTransactionSuccessful();
			return results;
		} catch (Exception e) {
			LogUtils.LOGE(TAG, "Error during apply batch", e);
			throw new OperationApplicationException(e);
		} finally {
			db.endTransaction();
		}
	}

	/** {@inheritDoc} */
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		// TODO possible performance drawback because triggers if there is lot of data, maybe we need another approach than triggers
		final SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String selectionPhrase = "";
		String[] selectionParam = null;
		String tableName = "";
		String[] selectionArgs = null;
		// one method for all tables that needs this, this is used in whole provider implementation
		int match = mobileStoreURIMatcher.match(uri);
		switch (match) {
		case USERS:
			tableName = Tables.USERS;
			selectionParam = new String[] { Users.USERNAME };
			selectionPhrase = Users.USERNAME + "=?";
			break;
		case ITEMS:
			tableName = Tables.ITEMS;
			selectionParam = new String[] { Items.ITEM_NO};
			selectionPhrase = Items.ITEM_NO + "=?";
			break;
		case CUSTOMERS:
			// this check for potential customers
			if (values.length > 0 && values[0].containsKey(Customers._ID)) {
				// here we are updating potential customer that is earlier saved in db before sending
				tableName = Tables.CUSTOMERS;
				selectionParam = new String[] { Customers._ID};
				selectionPhrase = Customers._ID + "=?";
			} else {
				tableName = Tables.CUSTOMERS;
				selectionParam = new String[] { Customers.CUSTOMER_NO, Customers.CONTACT_COMPANY_NO};
				selectionPhrase = Customers.CUSTOMER_NO + "=? or "+ Customers.CUSTOMER_NO + "=?";
			}
			break;
		case VISITS:
			tableName = Tables.VISITS;
			selectionParam =  new String[] {Visits.CUSTOMER_ID, Visits.SALES_PERSON_ID, Visits.VISIT_DATE, Visits.ARRIVAL_TIME};
			selectionPhrase =  Visits.CUSTOMER_ID + "=? and "+ Visits.SALES_PERSON_ID + "=? and "+ Visits.VISIT_DATE + "=? and "+ Visits.ARRIVAL_TIME + "=?";
			break;
		case ELECTRONIC_CARD_CUSTOMER:
			tableName = Tables.ELECTRONIC_CARD_CUSTOMER;
			selectionParam = new String[]{ElectronicCardCustomer.CUSTOMER_ID, ElectronicCardCustomer.ITEM_ID};
			selectionPhrase = ElectronicCardCustomer.CUSTOMER_ID + "=? AND "+ ElectronicCardCustomer.ITEM_ID + "=?";
			break;
		case CUSTOMER_TRADE_AGREEMENT:
			tableName = Tables.CUSTOMER_TRADE_AGREEMENT;
			selectionParam = new String[]{CustomerTradeAgreemnt.CUSTOMER_ID, CustomerTradeAgreemnt.CODE};
			selectionPhrase =  CustomerTradeAgreemnt.CUSTOMER_ID + "=? AND " + CustomerTradeAgreemnt.CODE + "=?";
			break;
		case INVOICES:
			tableName = Tables.INVOICES;
			selectionParam = new String[]{Invoices.ENTRY_NO};
			selectionPhrase = Invoices.ENTRY_NO+ "=?";
			break;
		case INVOICE_LINES:
			tableName = Tables.INVOICE_LINES;
			selectionParam = new String[]{InvoiceLine.INVOICES_ID, InvoiceLine.LINE_NO};
			selectionPhrase = InvoiceLine.INVOICES_ID+ "=? AND " + InvoiceLine.LINE_NO + "=?";
			break;
		case SENT_ORDERS_STATUS_LINES:
			tableName = Tables.SENT_ORDERS_STATUS_LINES;
			selectionParam = new String[]{SentOrdersStatusLineColumns.SENT_ORDER_STATUS_ID, SentOrdersStatusLineColumns.LINE_NO, SentOrdersStatusLineColumns.DOCUMENT_TYPE};
			selectionPhrase = SentOrdersStatusLineColumns.SENT_ORDER_STATUS_ID+ "=? AND " + SentOrdersStatusLineColumns.LINE_NO + "=? AND "+SentOrdersStatusLineColumns.DOCUMENT_TYPE+"=?";
			break;
		case SALE_ORDERS:
			tableName = Tables.SALE_ORDERS;
			selectionParam = new String[]{SaleOrders.SALES_ORDER_DEVICE_NO};
			selectionPhrase = SaleOrders.SALES_ORDER_DEVICE_NO+ "=?";
			break;
		case SALE_ORDER_LINES:
			tableName = Tables.SALE_ORDER_LINES;
			selectionParam = new String[]{SaleOrderLines.SALE_ORDER_ID, SaleOrderLines.LINE_NO};
			selectionPhrase =  SaleOrderLines.SALE_ORDER_ID + "=? AND " + SaleOrderLines.LINE_NO + "=?";
			break;
		case CUSTOMER_ADDRESSES:
			tableName = Tables.CUSTOMER_ADDRESSES;
			selectionParam = new String[] {CustomerAddresses.ADDRESS_NO, CustomerAddresses.CUSTOMER_NO};
			selectionPhrase = CustomerAddresses.ADDRESS_NO + "=? AND " + CustomerAddresses.CUSTOMER_NO + "=?";
			break;
		case SENT_ORDERS_STATUS:
			tableName = Tables.SENT_ORDERS_STATUS;
			selectionParam = new String[] {SentOrdersStatus.SENT_ORDER_NO};
			selectionPhrase = SentOrdersStatus.SENT_ORDER_NO + "=?";
			break;
		case CONTACTS:
			tableName = Tables.CONTACTS;
			selectionParam = new String[] { Contacts.CONTACT_NO };
			selectionPhrase = Contacts.CONTACT_NO + "=?";
			break;
		case CUSTOMER_BUSINESS_UNITS:
			tableName = Tables.CUSTOMER_BUSINESS_UNITS;
			selectionParam = new String[] {CustomerBusinessUnits.UNIT_NO, CustomerBusinessUnits.CUSTOMER_NO};
			selectionPhrase = CustomerBusinessUnits.UNIT_NO + "=? AND " + CustomerBusinessUnits.CUSTOMER_NO + "=?";
			break;
		case ITEMS_ON_PROMOTION:
			tableName = Tables.ITEMS_ON_PROMOTION;
			selectionParam = new String[] {ItemsOnPromotion.ITEM_NO, ItemsOnPromotion.BRANCH_CODE};
			selectionPhrase = ItemsOnPromotion.ITEM_NO + "=? AND " + ItemsOnPromotion.BRANCH_CODE + "=?";
			break;
		case ACTION_PLAN:
			tableName = Tables.ACTION_PLAN;
			selectionParam = new String[] {ActionPlan.CUSTOMER_NO, ActionPlan.BUSINESS_UNIT_NO, ActionPlan.ITEM_NO};
			selectionPhrase = ActionPlan.CUSTOMER_NO + "=? AND " + ActionPlan.BUSINESS_UNIT_NO + "=? AND " + ActionPlan.ITEM_NO + "=?";
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		// bulk insert
		// idea is that one transaction opens database file only once, 
		// which gives lot of performance
		int rowsAdded = 0;
		long rowId;
		db.beginTransaction();
		try {
			for (ContentValues cv : values) {
				selectionArgs = new String[selectionParam.length];
				for(int i  = 0; i<selectionArgs.length; i++){
					selectionArgs[i] = cv.getAsString(selectionParam[i]);
				}
				int affected = 0;
				try {
					affected = db.update(tableName, cv, selectionPhrase, selectionArgs);
				} catch (SQLiteConstraintException e) {
					LogUtils.LOGE(TAG, "Error during bulk update", e);
				}
				if (affected == 0) {
					rowId = db.insert(tableName, null, cv);
					if (rowId > 0)
						rowsAdded++;
				}
			}
			db.setTransactionSuccessful();
		} catch (SQLException ex) {
			LogUtils.LOGE(TAG, "Error during bulk insert", ex);
		} finally {
			db.endTransaction();
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAdded;
	}
}
