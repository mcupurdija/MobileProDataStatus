package rs.gopro.mobile_store.provider;

import rs.gopro.mobile_store.provider.MobileStoreContract.Contacts;
import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Invoices;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrderLines;
import rs.gopro.mobile_store.provider.MobileStoreContract.SaleOrders;
import rs.gopro.mobile_store.provider.MobileStoreContract.Users;
import rs.gopro.mobile_store.provider.MobileStoreContract.Visits;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.SelectionBuilder;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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

	private static final int CUSTOMERS = 120;
	private static final int CUSTOMERS_ID = 121;
	private static final int CUSTOMERS_NO = 122;
	private static final int CUSTOMERS_SEARCH = 123;
	private static final int CUSTOMERS_CUSTOM_SEARCH = 124;
	private static final int CUSTOMERS_BY_STATUS = 125;
	private static final int CUSTOMERS_BY_SALES_PERSON = 126;

	private static final int ITEMS = 130;
	private static final int ITEMS_NO = 131;
	private static final int ITEMS_BY_STATUS = 132;
	private static final int ITEMS_CUSTOM_SEARCH = 133;
	
	private static final int SALE_ORDERS = 140;
	private static final int SALE_ORDER_CUSTOM_SEARCH = 141;
	private static final int SALE_ORDER_BY_STATUS = 142;
	private static final int SALE_ORDERS_LIST = 143;

	private static final int VISITS = 200;
	private static final int VISIT_ID = 201;
	private static final int VISITS_WITH_CUSTOMER = 202;
	
	private static final int SALE_ORDER_LINES_FROM_ORDER = 300;
	
	private static final int CONTACTS = 400;
	private static final int CONTACTS_ID = 401;
	

	private static final UriMatcher mobileStoreURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		String authority = MobileStoreContract.CONTENT_AUTHORITY;
		mobileStoreURIMatcher.addURI(authority, "users", USERS);
		mobileStoreURIMatcher.addURI(authority, "users/#", USERS_ID);
		mobileStoreURIMatcher.addURI(authority, "users/username", USERNAME);

		mobileStoreURIMatcher.addURI(authority, "invoices", INVOICES);
		mobileStoreURIMatcher.addURI(authority, "invoices/#", INVOICES_ID);

		mobileStoreURIMatcher.addURI(authority, "customers", CUSTOMERS);
		mobileStoreURIMatcher.addURI(authority, "customers_by_sales_person/*", CUSTOMERS_BY_SALES_PERSON);
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
		mobileStoreURIMatcher.addURI(authority, "items/*/item_no",
				ITEMS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "items/#/item_no",
				ITEMS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "items/*/#/item_no",
				ITEMS_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "items/#/*/item_no",
				ITEMS_CUSTOM_SEARCH);

		mobileStoreURIMatcher.addURI(authority, "visits", VISITS);
		mobileStoreURIMatcher.addURI(authority, "visits/#", VISIT_ID);
		mobileStoreURIMatcher.addURI(authority, "visits/with_customer", VISITS_WITH_CUSTOMER);
		
		mobileStoreURIMatcher.addURI(authority, "sale_orders", SALE_ORDERS);
		mobileStoreURIMatcher.addURI(authority, "sale_orders_list/*", SALE_ORDERS_LIST);
		//custom_search
		mobileStoreURIMatcher.addURI(authority, "sale_orders/*/custom_search", SALE_ORDER_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "sale_orders/#/custom_search", SALE_ORDER_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "sale_orders/*/#/custom_search", SALE_ORDER_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "sale_orders/#/*/custom_search", SALE_ORDER_CUSTOM_SEARCH);
		
		mobileStoreURIMatcher.addURI(authority, "sale_order_lines_from_order/#", SALE_ORDER_LINES_FROM_ORDER);
		
		mobileStoreURIMatcher.addURI(authority, "contacts", CONTACTS);
		mobileStoreURIMatcher.addURI(authority, "contacts/*", CONTACTS_ID );
		/*mobileStoreURIMatcher.addURI(authority, "contacts/custom_search", CONTACTS_ALL);
		mobileStoreURIMatcher.addURI(authority, "contacts/custom_search/*", CONTACTS_CUSTOM_SEARCH);*/
		//mobileStoreURIMatcher.addURI(authority, "contacts/custom_search/#", CONTACTS_CUSTOM_SEARCH);
												   
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
		case SALE_ORDERS_LIST:
			return SaleOrders.CONTENT_TYPE;
		case SALE_ORDER_LINES_FROM_ORDER:
			return SaleOrderLines.CONTENT_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		//LogUtils.log(Log.VERBOSE, TAG, "insert(uri = " + uri + ")");
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
			id = database.insertOrThrow(Tables.CONTACTS,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Contacts.buildContactsUri("" + id);
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
			return builder.where(selection, selectionArgs).query(database,
					projection, sortOrder);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		LogUtils.log(Log.VERBOSE, TAG, "update(uri = " + uri + ")");
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		SelectionBuilder builder = buildSimpleSelection(uri);
		int updatedRows = builder.where(selection, selectionArgs).update(
				database, values);
		getContext().getContentResolver().notifyChange(uri, null);
		return updatedRows;
	}

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
			return builder.addTable(Tables.VISITS)
					.where(Tables.VISITS + "." + Visits._ID + "=?", visitId);
		case SALE_ORDERS:
			return builder.addTable(Tables.SALE_ORDERS);
		case CONTACTS_ID:
			final String contactId = Contacts.getContactsId(uri);
			return builder.addTable(Tables.CONTACTS)
					.where(Tables.CONTACTS+"." + Contacts._ID + "=?", contactId);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
		System.out.println("URI: " + uri);
		final SelectionBuilder builder = new SelectionBuilder();
		switch (match) {
		case USERS_ID:
			String userId = Users.getUserId(uri);
			return builder.addTable(Tables.USERS).where(Users._ID + "=?",
					userId);
		case USERNAME:
			return builder.addTable(Tables.USERS);
		case INVOICES_ID:
			String invoicesId = Invoices.getInvoicesId(uri);
			return builder.addTable(Tables.INVOICES).where(Invoices._ID + "=?",
					invoicesId);
		case INVOICES:
			return builder.addTable(Tables.INVOICES);
		case CUSTOMERS:
			return builder.addTable(Tables.CUSTOMERS).where(
					Customers.BLOCKED_STATUS + "= ?", new String[] { "1" });
		case CUSTOMERS_BY_SALES_PERSON:
			final String salesPersonIdOnCustomer = Customers.getCustomersSalesPersonId(uri);
			return builder.addTable(Tables.CUSTOMERS)
					.where(Customers.SALES_PERSON_ID + "=?", salesPersonIdOnCustomer);
		case CUSTOMERS_ID:
			String customerId = Customers.getCustomersId(uri);
			return builder.addTable(Tables.CUSTOMERS)
					.where(Customers._ID + "=?", customerId);
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
					Items.CAMPAIGN_CODE + "= ?", new String[] { itemStat });
		case ITEMS_CUSTOM_SEARCH:
			String itemCustom = Items.getCustomSearchFirstParamQuery(uri);
			String itemStatus = Items.getCustomSearchSecondParamQuery(uri);
			return builder
					.addTable(Tables.ITEMS)
					.where(Items.ITEM_NO + " like ? OR " + Items.DESCRIPTION
							+ " like ? ",
							new String[] { itemCustom + "%",
									"%" + itemCustom + "%" })
					.where(Items.CAMPAIGN_CODE + "= ?",
							new String[] { itemStatus });
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
		case SALE_ORDERS:
			return builder.addTable(Tables.SALE_ORDERS_JOIN_CUSTOMERS)
					.mapToTable(SaleOrders._ID, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SALES_ORDER_NO, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.DOCUMENT_TYPE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CUSTOMER_ID, Tables.SALE_ORDERS)
					.mapToTable(Visits.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME_2, Tables.CUSTOMERS)
					.mapToTable(SaleOrders.ORDER_DATE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.LOCATION_CODE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SHORTCUT_DIMENSION_1_CODE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CURRENCY_CODE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.EXTERNAL_DOCUMENT_NO, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.QUOTE_NO, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.BACKORDER_SHIPMENT_STATUS, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.ORDER_STATUS_FOR_SHIPMENT, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.FIN_CONTROL_STATUS, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.ORDER_CONDITION_STATUS, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.USED_CREDIT_LIMIT_BY_EMPLOYEE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.ORDER_VALUE_STATUS, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.QUOTE_REALIZED_STATUS, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SPECIAL_QUOTE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.QUOTE_VALID_DATE_TO, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CUST_USES_TRANSIT_CUST, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SALES_PERSON_ID, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CUSTOMER_ADDRESS_ID, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CONTACT_PHONE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.PAYMENT_OPTION, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CHECK_STATUS_PHONE, Tables.SALE_ORDERS)
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
			return builder.addTable(Tables.SALE_ORDERS_JOIN_CUSTOMERS)
					.mapToTable(SaleOrders._ID, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SALES_PERSON_ID, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.SALES_ORDER_NO, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.DOCUMENT_TYPE, Tables.SALE_ORDERS)
					.mapToTable(SaleOrders.CUSTOMER_ID, Tables.SALE_ORDERS)
					.mapToTable(Visits.CUSTOMER_NO, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME, Tables.CUSTOMERS)
					.mapToTable(Visits.NAME_2, Tables.CUSTOMERS)
					.mapToTable(SaleOrders.ORDER_DATE, Tables.SALE_ORDERS)
					.where(Tables.SALE_ORDERS + "." + SaleOrders.SALES_PERSON_ID+ "=?", salesPersonId);
		case SALE_ORDER_BY_STATUS:
			String saleOrderDocType = SaleOrders.getSaleOrderDocType(uri);
			return builder.addTable(Tables.SALE_ORDERS)
					.where(SaleOrders.DOCUMENT_TYPE+ "= ? ",new String[] {saleOrderDocType} );
		case SALE_ORDER_CUSTOM_SEARCH:
			String saleCustomParam = SaleOrders.getCustomSearchFirstParamQuery(uri);
			String saleDocType = SaleOrders.getCustomSearchSecondParamQuery(uri);
			return builder.addTable(Tables.SALE_ORDERS)
					.where(SaleOrders.SALES_ORDER_NO + " like ? ", new String[] {"%"+saleCustomParam+ "%"})
					.where(SaleOrders.DOCUMENT_TYPE+ "= ? ",new String[] {saleDocType} );
		case SALE_ORDER_LINES_FROM_ORDER:
			final String salesOrderId = SaleOrderLines.getSaleOrderLineId(uri);
			return builder.addTable(Tables.SALE_ORDER_LINES_JOIN_ITEMS)
					.mapToTable(SaleOrderLines._ID, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.SALE_ORDER_ID, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.ITEM_NO, Tables.ITEMS)
					.mapToTable(SaleOrderLines.DESCRIPTION, Tables.ITEMS)
					.mapToTable(SaleOrderLines.DESCRIPTION2, Tables.ITEMS)
					.mapToTable(SaleOrderLines.LINE_NO, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.QUANTITY, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.PRICE_EUR, Tables.SALE_ORDER_LINES)
					.mapToTable(SaleOrderLines.REAL_DISCOUNT, Tables.SALE_ORDER_LINES)
					.where(SaleOrderLines.SALE_ORDER_ID+"=?", salesOrderId);
		case CONTACTS_ID:
			String contactsCustomSearch = Contacts.getContactsCustomSearch(uri);
			return builder.addTable(Tables.CONTACTS)
				.where(Contacts.CONTACT_NO + " like ?  or "+ Contacts.NAME +" like ?", new String[] {"%"+contactsCustomSearch+"%", "%"+contactsCustomSearch+"%"});
		case CONTACTS:
			return builder.addTable(Tables.CONTACTS);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
}
