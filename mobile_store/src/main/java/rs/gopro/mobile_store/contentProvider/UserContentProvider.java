package rs.gopro.mobile_store.contentProvider;


import rs.gopro.mobile_store.database.SQLiteDatabaseHelper;
import rs.gopro.mobile_store.database.util.DatabaseSpecification;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * @see ContentProvider
 * @author aleksandar
 * 
 */
public class UserContentProvider extends ContentProvider {

	private SQLiteDatabaseHelper databaseHelper;
	
	// Used for the UriMacher
		private static final int USER = 10;
		private static final int USER_ID = 20;
		private static final int USERNAME = 30;

		private static final String AUTHORITY = "rs.gopro.mobile_store.contentProvider.userContentProvider";
		public static final String BASE_PATH = "user";
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/user";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/us";

		private static final UriMatcher userURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		static {
			userURIMatcher.addURI(AUTHORITY, BASE_PATH, USER);
			userURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", USER_ID);
			userURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", USERNAME);
		}
	
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = userURIMatcher.match(uri);
		String id = uri.getLastPathSegment();
		int rowDeleted = 0;
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		switch (uriType) {
		case USER:
			rowDeleted = database.delete(DatabaseSpecification.USER_TABLE, selection, selectionArgs);
			break;
		case USER_ID:
			
			if (TextUtils.isEmpty(selection)) {
				rowDeleted = database.delete(DatabaseSpecification.USER_TABLE, DatabaseSpecification.UserColumn.USER_ID.getRepresentation() + "=" + id, null);
			} else {
				rowDeleted = database.delete(DatabaseSpecification.USER_TABLE, DatabaseSpecification.UserColumn.USER_ID.getRepresentation() + "=" + id + " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowDeleted;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		int uriType = userURIMatcher.match(uri);
		long id = 0;
		switch (uriType) {
		case USER:
			id = database.insert(DatabaseSpecification.USER_TABLE, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);	
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new SQLiteDatabaseHelper(getContext(), 5);
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(DatabaseSpecification.USER_TABLE);

		int uriType = userURIMatcher.match(uri);

		switch (uriType) {
		case USER:
			//there is no need implementation, fetch all user from table 
			break;
		case USER_ID:
			queryBuilder.appendWhere(DatabaseSpecification.UserColumn.USER_ID.getRepresentation() + "=" + uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		int uriType = userURIMatcher.match(uri);
		int updated = 0;
		switch (uriType) {
		case USER:
			updated = database.update(DatabaseSpecification.USER_TABLE, values, selection, selectionArgs);
			break;
		case USER_ID:
			String id = uri.getLastPathSegment();
			updated = database.update(DatabaseSpecification.USER_TABLE, values,DatabaseSpecification.UserColumn.USER_ID.getRepresentation() + "=" + id, null);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return 0;
	}

}
