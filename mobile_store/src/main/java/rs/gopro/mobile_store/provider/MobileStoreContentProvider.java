package rs.gopro.mobile_store.provider;


import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Invoices;
import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
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
	private static final String TAG = LogUtils.makeLogTag(MobileStoreContentProvider.class);

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
	
	private static final int ITEMS = 130;
	private static final int ITEMS_NO = 131;
	private static final int ITEMS_BY_STATUS = 132;
	private static final int ITEMS_CUSTOM_SEARCH = 133;
	
	private static final int SALE_ORDER = 140;

	private static final int VISITS = 200;
	private static final int VISIT_ID = 201;
	private static final int VISITS_WITH_CUSTOMER = 202;
	
	

	
	private static final UriMatcher mobileStoreURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static {
		String authority = MobileStoreContract.CONTENT_AUTHORITY;
		mobileStoreURIMatcher.addURI(authority, "users", USERS);
		mobileStoreURIMatcher.addURI(authority, "users/#", USERS_ID);
		mobileStoreURIMatcher.addURI(authority, "users/username", USERNAME);

		mobileStoreURIMatcher.addURI(authority, "invoices", INVOICES);
		mobileStoreURIMatcher.addURI(authority, "invoices/#", INVOICES_ID);
		
		mobileStoreURIMatcher.addURI(authority, "customers",CUSTOMERS);
		mobileStoreURIMatcher.addURI(authority, "customers/#",CUSTOMERS_ID);
		mobileStoreURIMatcher.addURI(authority, "customers/customer_no",CUSTOMERS_NO);
		mobileStoreURIMatcher.addURI(authority, "customers/*/customer_no",CUSTOMERS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "customers/#/customer_no",CUSTOMERS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "customers/#/*/customer_no",CUSTOMERS_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "customers/*/#/customer_no",CUSTOMERS_CUSTOM_SEARCH);
		
		mobileStoreURIMatcher.addURI(authority, "items", ITEMS);
		mobileStoreURIMatcher.addURI(authority, "items/*/item_no", ITEMS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "items/#/item_no", ITEMS_BY_STATUS);
		mobileStoreURIMatcher.addURI(authority, "items/*/#/item_no", ITEMS_CUSTOM_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "items/#/*/item_no", ITEMS_CUSTOM_SEARCH);

		mobileStoreURIMatcher.addURI(authority, "visits", VISITS);
		mobileStoreURIMatcher.addURI(authority, "visits/#", VISIT_ID);
		mobileStoreURIMatcher.addURI(authority, "visits/with_customer", VISITS_WITH_CUSTOMER);
		
		mobileStoreURIMatcher.addURI(authority, "sale_orders",SALE_ORDER);
	}


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		LogUtils.log(Log.VERBOSE, TAG, "delete(uri = " + uri + ")");
		//int match = mobileStoreURIMatcher.match(uri);
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
            case VISITS:
            	return Visits.CONTENT_TYPE;
            case VISIT_ID:
            	return Visits.CONTENT_TYPE;
            case VISITS_WITH_CUSTOMER:
            	return Visits.CONTENT_TYPE;
            default:
            	return null;
                //TODO Throw exception when other tables get content_type
            	//throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		LogUtils.log(Log.VERBOSE,TAG, "insert(uri = " + uri + ")");
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		int match = mobileStoreURIMatcher.match(uri);
		long id = 0;
		switch (match) {
		case USERS:
			id = database.insertOrThrow(Tables.USERS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Users.buildUsersUri("" + id);
		case INVOICES:
			id = database.insertOrThrow(Tables.INVOICES,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return  Invoices.buildInvoicesUri(""+id);
		case CUSTOMERS:
			id = database.insertOrThrow(Tables.CUSTOMERS,null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Customers.buildCustomersUri(""+id);
		case VISITS:
			id = database.insertOrThrow(Tables.VISITS, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return  Visits.buildVisitUri(""+id);
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
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		int match = mobileStoreURIMatcher.match(uri);
		switch (match) {
		default:
			SelectionBuilder builder = buildExpandedSelection(uri, match);
			return builder.where(selection, selectionArgs).query(database, projection, sortOrder);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		LogUtils.log(Log.VERBOSE,TAG, "delete(uri = " + uri + ")");
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		SelectionBuilder builder = buildSimpleSelection(uri);
		int updatedRows = builder.where(selection, selectionArgs).update(database, values);
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
		case  INVOICES_ID:
			return builder.addTable(Invoices._ID);
		case VISIT_ID:
			return builder.addTable(Visits._ID);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
		System.out.println("URI: "+uri);
		final SelectionBuilder builder = new SelectionBuilder();
		switch (match) {
		case USERS_ID:
			String userId = Users.getUserId(uri);
			return builder.addTable(Tables.USERS).where(Users._ID + "=?", userId);
		case USERNAME:
			return builder.addTable(Tables.USERS);
		case INVOICES_ID:
			String invoicesId = Invoices.getInvoicesId(uri);
			return builder.addTable(Tables.INVOICES).where(Invoices._ID+ "=?", invoicesId);
		case INVOICES:
			return builder.addTable(Tables.INVOICES);
		case CUSTOMERS:
			return builder.addTable(Tables.CUSTOMERS)
					.where(Customers.BLOCKED_STATUS + "= ?", new String[]{ "1"});
		case CUSTOMERS_NO:
			return builder.addTable(Tables.CUSTOMERS);
			
		case  CUSTOMERS_BY_STATUS:
			String query = Customers.getSearchQuery(uri);
			return builder.addTable(Tables.CUSTOMERS)
					.where(Customers.BLOCKED_STATUS + "= ?", new String[]{ query});
		case CUSTOMERS_CUSTOM_SEARCH:
			String customerCustomParam = Customers.getCustomSearchFirstParamQuery(uri);
			String customerStatus = Customers.getCustomSearchSecondParamQuery(uri);
			return builder.addTable(Tables.CUSTOMERS)
			.where(Customers.CUSTOMER_NO + " like ? OR "+ Customers.NAME + " like ?", new String[] { /*"%" +*/ customerCustomParam + "%", "%" + customerCustomParam + "%"})
			.where(Customers.BLOCKED_STATUS + "= ?", new String[] {customerStatus});
			
		
		case ITEMS:
			return builder.addTable(Tables.ITEMS);
		case ITEMS_BY_STATUS:
			String itemStat = Items.getItemStatus(uri);
			return builder.addTable(Tables.ITEMS)
			.where(Items.CAMPAIGN_STATUS + "= ?", new String[]{itemStat});
		case ITEMS_CUSTOM_SEARCH: 
			String  itemCustom = Items.getCustomSearchFirstParamQuery(uri);
			String  itemStatus = Items.getCustomSearchSecondParamQuery(uri);
			return builder.addTable(Tables.ITEMS)
			.where(Items.ITEM_NO + " like ? OR "+Items.DESCRIPTION + " like ? ", new String [] {itemCustom + "%", "%" + itemCustom + "%"} )
			.where(Items.CAMPAIGN_STATUS + "= ?", new String[]{itemStatus});
		case VISIT_ID:
			return builder.addTable(Tables.VISITS);
		case SALE_ORDER:
			return builder.addTable(Tables.SALE_ORDERS);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
}
