package rs.gopro.mobile_store.provider;

import java.util.ArrayList;

import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.CustomerAddresses;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.ElectronicCardCustomer;
import rs.gopro.mobile_store.provider.MobileStoreContract.Generic;
import rs.gopro.mobile_store.provider.MobileStoreContract.InvoiceLine;
import rs.gopro.mobile_store.provider.MobileStoreContract.Invoices;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrderLines;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.MobileStoreContract.SalesPerson;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.provider.MobileStoreContract.Users;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
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

	private static final int CUSTOMERS = 120;
	private static final int CUSTOMERS_ID = 121;
	private static final int CUSTOMERS_NO = 122;
	//private static final int CUSTOMERS_SEARCH = 123;
	private static final int CUSTOMERS_CUSTOM_SEARCH = 124;
	private static final int CUSTOMERS_BY_STATUS = 125;
	private static final int CUSTOMERS_BY_SALES_PERSON = 126;

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
	
	private static final int VISITS = 200;
	private static final int VISIT_ID = 201;
	private static final int VISITS_WITH_CUSTOMER = 202;
	private static final int VISITS_DATE = 203;
	

	private static final int SALE_ORDER_LINES_FROM_ORDER = 300;
	private static final int SALE_ORDER_LINE = 301;
	private static final int SALE_ORDER_LINES = 302;
	
	private static final int CONTACTS = 400;
	private static final int CONTACTS_ID = 401;
	
	private static final int CUSTOMER_ADDRESSES = 500;
	private static final int CUSTOMER_ADDRESSES_ID = 501;
	private static final int CUSTOMER_ADDRESSES_CUSTOMER_NO = 502;
	
	private static final int SYNC_LOGS_MAX = 600;
	private static final int SYNC_LOGS = 601;
	private static final int SYNC_LOGS_ID = 602;
	
	private static final int GENERIC = 700;
	
	private static final int ELECTRONIC_CARD_CUSTOMER = 800;
	private static final int ELECTRONIC_CARD_CUSTOMER_ID = 801;

	private static final int SALES_PERSONS = 900;
	private static final int SALES_PERSON_ID = 901;
	
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

		mobileStoreURIMatcher.addURI(authority, "customers", CUSTOMERS);
		mobileStoreURIMatcher.addURI(authority, "customers_by_sales_person/*",
				CUSTOMERS_BY_SALES_PERSON);
		mobileStoreURIMatcher.addURI(authority, "customers/#", CUSTOMERS_ID);
		mobileStoreURIMatcher.addURI(authority, "customers/customer_no",
				CUSTOMERS_NO);
		mobileStoreURIMatcher.addURI(authority, "customers/*/customer_no",
				CUSTOMERS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "customers/#/customer_no",
				CUSTOMERS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "customers/#/*/customer_no",
				CUSTOMERS_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "customers/*/#/customer_no",
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
		mobileStoreURIMatcher.addURI(authority, "visits/#", VISIT_ID);
		mobileStoreURIMatcher.addURI(authority, "visits/with_customer",
				VISITS_WITH_CUSTOMER);
		mobileStoreURIMatcher.addURI(authority, "visits/*/visits_date", VISITS_DATE);

		mobileStoreURIMatcher.addURI(authority, "sale_orders", SALE_ORDERS);
		mobileStoreURIMatcher.addURI(authority, "sale_orders/#", SALE_ORDER);
		mobileStoreURIMatcher.addURI(authority, "sale_orders_list/*",
				SALE_ORDERS_LIST);
		// custom_search
		mobileStoreURIMatcher.addURI(authority, "sale_orders/*/custom_search",
				SALE_ORDER_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "sale_orders/#/custom_search",
				SALE_ORDER_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority,
				"sale_orders/*/#/custom_search", SALE_ORDER_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority,
				"sale_orders/#/*/custom_search", SALE_ORDER_CUSTOM_SEARCH);

		mobileStoreURIMatcher.addURI(authority,
				"sale_order_lines", SALE_ORDER_LINES);
		mobileStoreURIMatcher.addURI(authority,
				"sale_order_lines/*", SALE_ORDER_LINE);
		
		mobileStoreURIMatcher.addURI(authority,
				"sale_order_lines_from_order/#", SALE_ORDER_LINES_FROM_ORDER);

		mobileStoreURIMatcher.addURI(authority, "contacts", CONTACTS);
		mobileStoreURIMatcher.addURI(authority, "contacts/*", CONTACTS_ID);
		
		mobileStoreURIMatcher.addURI(authority, "customer_addresses", CUSTOMER_ADDRESSES);
		mobileStoreURIMatcher.addURI(authority, "customer_addresses/customer_no/*", CUSTOMER_ADDRESSES_CUSTOMER_NO);
		mobileStoreURIMatcher.addURI(authority, "customer_addresses/*", CUSTOMER_ADDRESSES_ID);
		
		mobileStoreURIMatcher.addURI(authority, "sync_logs/#", SYNC_LOGS_ID);
		mobileStoreURIMatcher.addURI(authority, "sync_logs/*", SYNC_LOGS_ID);
		mobileStoreURIMatcher.addURI(authority, "sync_logs", SYNC_LOGS);
		
		mobileStoreURIMatcher.addURI(authority, "sync_logs/*/sync_logs_obejct_id", SYNC_LOGS_MAX);
		
		mobileStoreURIMatcher.addURI(authority, "generic/*",GENERIC);
		mobileStoreURIMatcher.addURI(authority, "electronic_card_customer", ELECTRONIC_CARD_CUSTOMER);
		mobileStoreURIMatcher.addURI(authority, "electronic_card_customer/#", ELECTRONIC_CARD_CUSTOMER_ID);
		
		mobileStoreURIMatcher.addURI(authority, "sales_persons", SALES_PERSONS);
		mobileStoreURIMatcher.addURI(authority, "sales_persons/*", SALES_PERSON_ID);
		
		/*
		 * mobileStoreURIMatcher.addURI(authority, "contacts/custom_search",
		 * CONTACTS_ALL); mobileStoreURIMatcher.addURI(authority,
		 * "contacts/custom_search/*", CONTACTS_CUSTOM_SEARCH);
		 */
		// mobileStoreURIMatcher.addURI(authority, "contacts/custom_search/#",
		// CONTACTS_CUSTOM_SEARCH);

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		LogUtils.log(Log.VERBOSE, TAG, "delete(uri = " + uri + ")");
		// int match = mobileStoreURIMatcher.match(uri);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		SelectionBuilder builder = buildSimpleSelection(uri);
		int deletedRows = builder.where(selection, selectionArgs).delete(
				database);
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
			Cursor cursor = builder.where(selection, selectionArgs).query(database,
					projection, sortOrder);
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
		case VISIT_ID: // TODO should be fixed, this is place for insert, maybe NOT, it could send item uri for filter
			final String visitId = Visits.getVisitId(uri);
			return builder.addTable(Tables.VISITS).where(
					Tables.VISITS + "." + Visits._ID + "=?", visitId);
		case SALE_ORDERS:
			return builder.addTable(Tables.SALE_ORDERS);
		case SALE_ORDER:
			final String saleOrderId = SaleOrders.getSaleOrderId(uri);
			return builder.addTable(Tables.SALE_ORDERS).where(
					Tables.SALE_ORDERS + "." + SaleOrders._ID + "=?", saleOrderId);
		case CONTACTS_ID: // TODO should be fixed, this is place for insert
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
			return builder.addTable(Tables.INVOICES);
		case INVOICE_LINES_FROM_ORDER:
			String invoiceid = InvoiceLine.getInvoiceId(uri);
			return builder.addTable(Tables.INVOICE_LINES)
					.where(InvoiceLine.INVOICES_ID + "=?", new String[] {invoiceid});
		case CUSTOMERS:
			return builder.addTable(Tables.CUSTOMERS).where(
					Customers.BLOCKED_STATUS + "= ?", new String[] { "0" }); // TODO delete this!
		case CUSTOMERS_BY_SALES_PERSON:
			final String salesPersonIdOnCustomer = Customers
					.getCustomersSalesPersonId(uri);
			return builder.addTable(Tables.CUSTOMERS).where(
					Customers.SALES_PERSON_ID + "=?", salesPersonIdOnCustomer);
		case CUSTOMERS_ID:
			String customerId = Customers.getCustomersId(uri);
			return builder.addTable(Tables.CUSTOMERS).where(
					Customers._ID + "=?", customerId);
		case CUSTOMERS_NO:
			return builder.addTable(Tables.CUSTOMERS);
		case CUSTOMERS_BY_STATUS:
			String query = Customers.getSearchQuery(uri);
			return builder.addTable(Tables.CUSTOMERS).where(
					Customers.BLOCKED_STATUS + "= ?", new String[] { query });
		case CUSTOMERS_CUSTOM_SEARCH:
			String customerCustomParam = Customers
					.getCustomSearchFirstParamQuery(uri);
			String customerStatus = Customers
					.getCustomSearchSecondParamQuery(uri);
			return builder
					.addTable(Tables.CUSTOMERS)
					.where(Customers.CUSTOMER_NO + " like ? OR "
							+ Customers.NAME + " like ?",
							new String[] { /* "%" + */
							customerCustomParam + "%",
									"%" + customerCustomParam + "%" })
					.where(Customers.BLOCKED_STATUS + "= ?",
							new String[] { customerStatus });

		case ITEMS:
			return builder.addTable(Tables.ITEMS);
		case ITEMS_BY_STATUS:
			String itemStat = Items.getItemStatus(uri);
			return builder.addTable(Tables.ITEMS).where(
					Items.CAMPAIGN_STATUS + "= ?", new String[] { itemStat });
		case ITEMS_CUSTOM_SEARCH:
			String itemCustom = Items.getCustomSearchFirstParamQuery(uri);
			String itemStatus = Items.getCustomSearchSecondParamQuery(uri);
			return builder
					.addTable(Tables.ITEMS)
					.where(Items.ITEM_NO + " like ? OR " + Items.DESCRIPTION
							+ " like ? ",
							new String[] { itemCustom + "%",
									"%" + itemCustom + "%" })
					.where(Items.CAMPAIGN_STATUS + "= ?",
							new String[] { itemStatus });
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
					.mapToTable(Visits.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME_2, Tables.CUSTOMERS)
					.mapToTable(Visits.ARRIVAL_TIME, Tables.VISITS)
					.mapToTable(Visits.DEPARTURE_TIME, Tables.VISITS)
					.mapToTable(Visits.ODOMETER, Tables.VISITS)
					.mapToTable(Visits.NOTE, Tables.VISITS);
		case VISIT_ID:
			final String visitId = Visits.getVisitId(uri);
			return builder.addTable(Tables.VISITS_JOIN_CUSTOMERS)
					.mapToTable(Visits._ID, Tables.VISITS)
					.mapToTable(Visits.SALES_PERSON_ID, Tables.VISITS)
					.mapToTable(Visits.CUSTOMER_ID, Tables.VISITS)
					.mapToTable(Visits.VISIT_DATE, Tables.VISITS)
					.mapToTable(Visits.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME_2, Tables.CUSTOMERS)
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
					.mapToTable(Visits.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME_2, Tables.CUSTOMERS)
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
		case SALE_ORDERS:
			return builder
					.addTable(Tables.SALE_ORDERS_JOIN_CUSTOMERS)
					.mapToTable(SaleOrders._ID, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SALES_ORDER_NO, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.DOCUMENT_TYPE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CUSTOMER_ID, Tables.SALE_ORDERS)
					.mapToTable(Visits.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME_2, Tables.CUSTOMERS)
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
					.mapToTable(Visits.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME_2, Tables.CUSTOMERS)
					.mapToTable(SaleOrders.ORDER_DATE, Tables.SALE_ORDERS)
					.where(Tables.SALE_ORDERS + "."
							+ SaleOrders.SALES_PERSON_ID + "=?", salesPersonId);
		case SALE_ORDER_BY_STATUS:
			String saleOrderDocType = SaleOrders.getSaleOrderDocType(uri);
			return builder.addTable(Tables.SALE_ORDERS).where(
					SaleOrders.DOCUMENT_TYPE + "= ? ",
					new String[] { saleOrderDocType });
		case SALE_ORDER_CUSTOM_SEARCH:
			String saleCustomParam = SaleOrders
					.getCustomSearchFirstParamQuery(uri);
			String saleDocType = SaleOrders
					.getCustomSearchSecondParamQuery(uri);
			return builder
					.addTable(Tables.SALE_ORDERS)
					.where(SaleOrders.SALES_ORDER_NO + " like ? ",
							new String[] { "%" + saleCustomParam + "%" })
					.where(SaleOrders.DOCUMENT_TYPE + "= ? ",
							new String[] { saleDocType });
		case SALE_ORDER_LINE:
			final String salesOrderlineId = SaleOrderLines.getSaleOrderLineId(uri);
			return builder
					.addTable(Tables.SALE_ORDER_LINES)
					.where(SaleOrderLines._ID + "=?", salesOrderlineId);
		case SALE_ORDER_LINES_FROM_ORDER:
			final String salesOrderId = SaleOrderLines.getSaleOrderLineId(uri);
			return builder
					.addTable(Tables.SALE_ORDER_LINES_JOIN_ITEMS)
					.mapToTable(SaleOrderLines._ID, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.SALE_ORDER_ID,
							Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.ITEM_NO, Tables.ITEMS)
					.mapToTable(SaleOrderLines.DESCRIPTION, Tables.ITEMS)
					.mapToTable(SaleOrderLines.DESCRIPTION2, Tables.ITEMS)
					.mapToTable(SaleOrderLines.LINE_NO, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.QUANTITY,
							Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.PRICE_EUR,
							Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.REAL_DISCOUNT,
							Tables.SALE_ORDER_LINES)
					.where(SaleOrderLines.SALE_ORDER_ID + "=?", salesOrderId);
		case CONTACTS_ID:
			String contactsCustomSearch = Contacts.getContactsCustomSearch(uri);
			return builder.addTable(Tables.CONTACTS).where(
					Contacts.CONTACT_NO + " like ?  or " + Contacts.NAME
							+ " like ?",
					new String[] { "%" + contactsCustomSearch + "%",
							"%" + contactsCustomSearch + "%" });
		case CONTACTS:
			return builder.addTable(Tables.CONTACTS);
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
			return builder.addTable(Tables.EL_CARD_CUSTOMER_JOIN_CUSTOMER_JOIN_ITEM)
					.mapToTable(ElectronicCardCustomer._ID, Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.CUSTOMER_ID , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(ElectronicCardCustomer.ITEM_ID , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.ITEM_NO, Tables.ITEMS)
					.mapToTable(ElectronicCardCustomer.JANUARY_QTY, Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.FEBRUARY_QTY, Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.MARCH_QTY, Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.APRIL_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.MAY_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.JUNE_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.JULY_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.AUGUST_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.SEPTEMBER_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.OCTOBER_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.NOVEMBER_QTY, Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.DECEMBER_QTY , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.TOTAL_SALE_QTY_CURRENT_YEAR , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.TOTAL_SALE_QTY_PRIOR_YEAR , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.TOTAL_TURNOVER_CURRENT_YEAR, Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.TOTAL_TURNOVER_PRIOR_YEAR , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.SALES_LINE_COUNTS_CURRENT_YEAR , Tables.ELECTRONIC_CARD_CUSTOMER )
					.mapToTable(ElectronicCardCustomer.SALES_LINE_COUNTS_PRIOR_YEAR , Tables.ELECTRONIC_CARD_CUSTOMER );
		case SALES_PERSONS:
			return builder.addTable(Tables.SALES_PERSONS);
		case SALES_PERSON_ID:
			final String sales_person_id = SalesPerson.getSalesPersonId(uri);
			return builder.addTable(Tables.SALES_PERSONS)
					.where(SalesPerson._ID + "=?", new String[]{ sales_person_id });
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
			tableName = Tables.CUSTOMERS;
			selectionParam = new String[] { Customers.CUSTOMER_NO};
			selectionPhrase = Customers.CUSTOMER_NO + "=?";
			break;
		case VISITS:
			tableName = Tables.VISITS;
			selectionParam =  new String[] {Visits.CUSTOMER_ID, Visits.SALES_PERSON_ID, Visits.VISIT_DATE};
			selectionPhrase =  Visits.CUSTOMER_ID + "=? and "+ Visits.SALES_PERSON_ID + "=? and "+ Visits.VISIT_DATE + "=?";
			break;
		case ELECTRONIC_CARD_CUSTOMER:
			tableName = Tables.ELECTRONIC_CARD_CUSTOMER;
			selectionParam = new String[]{ElectronicCardCustomer.CUSTOMER_ID, ElectronicCardCustomer.ITEM_ID};
			selectionPhrase = ElectronicCardCustomer.CUSTOMER_ID + "=? AND "+ ElectronicCardCustomer.ITEM_ID + "=?";
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
					affected = db.update(tableName, cv, selectionPhrase,
							selectionArgs);
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
