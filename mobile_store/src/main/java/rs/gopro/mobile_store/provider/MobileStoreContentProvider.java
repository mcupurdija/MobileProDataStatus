package rs.gopro.mobile_store.provider;

import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Customers;
import rs.gopro.mobile_store.provider.MobileStoreContract.Invoices;
import rs.gopro.mobile_store.provider.MobileStoreContract.Users;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.SelectionBuilder;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.net.NetworkInfo.DetailedState;
import android.text.TextUtils;
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
		mobileStoreURIMatcher.addURI(authority, "customers/no",CUSTOMERS_NO);
		mobileStoreURIMatcher.addURI(authority, "customers/*/no",CUSTOMERS_SEARCH);
		mobileStoreURIMatcher.addURI(authority, "customers/#/no",CUSTOMERS_SEARCH);
	}


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		LogUtils.log(Log.VERBOSE, TAG, "delete(uri = " + uri + ")");
		int match = mobileStoreURIMatcher.match(uri);
		SQLiteDatabase database = databaseHelper.getWritableDatabase();

		SelectionBuilder builder = buildSimpleSelection(uri);
		int deletedRows = builder.where(selection, selectionArgs).delete(database);
		getContext().getContentResolver().notifyChange(uri, null);
		return deletedRows;
	}

	@Override
	public String getType(Uri uri) {
		return null;
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
			return builder.addTable(Tables.CUSTOMERS);
		case CUSTOMERS_NO:
			return builder.addTable(Tables.CUSTOMERS);
		case CUSTOMERS_SEARCH:
			String query = Customers.getSearchQuery(uri);
			return builder.addTable(Tables.CUSTOMERS)
			.where(Customers.NO + " like ? OR "+ Customers.NAME + " like ?", new String[] { /*"%" +*/ query + "%", "%" + query + "%"});
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}
}
