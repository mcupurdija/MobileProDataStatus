package rs.gopro.mobile_store.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class SelectionBuilder {

	private static final String TAG = LogUtils.makeLogTag(SelectionBuilder.class);

	private String builderTable = null;
	private Map<String, String> builderProjectionMap = new HashMap<String, String>();
	private StringBuilder builderSelection = new StringBuilder();
	private ArrayList<String> builderSelectionArgs = new ArrayList<String>();

	/**
	 * Reset builder
	 */
	public SelectionBuilder reset() {
		builderTable = null;
		builderSelection.setLength(0);
		builderSelectionArgs.clear();
		return this;
	}

	/**
	 * Add selection clause and arguments
	 */
	public SelectionBuilder where(String selection, String... selectionArgs) {
		if (TextUtils.isEmpty(selection)) {
			if (selectionArgs != null && selectionArgs.length > 0) {
				throw new IllegalArgumentException("Selection required when including arguments");
			}
			// if selection is empty
			return this;
		}

		if (builderSelection.length() > 0) {
			builderSelection.append(" AND ");
		}

		builderSelection.append('(').append(selection).append(')');
		if (selectionArgs != null) {
			Collections.addAll(builderSelectionArgs, selectionArgs);
		}

		return this;
	}

	public SelectionBuilder addTable(String table) {
		builderTable = table;
		return this;
	}

	private void assertTable() {
		if (builderTable == null) {
			throw new IllegalStateException("Table not specified");
		}
	}

	public SelectionBuilder mapToTable(String column, String table) {
		builderProjectionMap.put(column, table + "." + column);
		return this;
	}

	public SelectionBuilder mapToTable(String column, String table, String alias) {
		builderProjectionMap.put(alias, table + "." + column + " AS " + alias);
		return this;
	}
	
	public SelectionBuilder map(String fromColumn, String toClause) {
		builderProjectionMap.put(fromColumn, toClause + " AS " + fromColumn);
		return this;
	}

	public String getSelection() {
		return builderSelection.toString();
	}

	/**
	 * Return selection arguments for current internal state.
	 * 
	 * @see #getSelection()
	 */
	public String[] getSelectionArgs() {
		return builderSelectionArgs.toArray(new String[builderSelectionArgs.size()]);
	}

	private void mapColumns(String[] columns) {
		for (int i = 0; i < columns.length; i++) {
			final String target = builderProjectionMap.get(columns[i]);
			if (target != null) {
				columns[i] = target;
			}
		}
	}

	@Override
	public String toString() {
		return "SelectionBuilder[table=" + builderTable + ", selection=" + getSelection() + ", selectionArgs=" + Arrays.toString(getSelectionArgs()) + "]";
	}

	/**
	 * Execute query using the current internal state as {@code WHERE} clause.
	 */
	public Cursor query(SQLiteDatabase db, String[] columns, String orderBy) {
		return query(db, columns, null, null, orderBy, null);
	}

	/**
	 * Execute query using the current internal state as {@code WHERE} clause.
	 */
	public Cursor query(SQLiteDatabase db, String[] columns, String groupBy, String having, String orderBy, String limit) {
		assertTable();
		if (columns != null)
			mapColumns(columns);
		LogUtils.log(Log.INFO, TAG, "query(columns=" + Arrays.toString(columns) + ") " + this);
		return db.query(builderTable, columns, getSelection(), getSelectionArgs(), groupBy, having, orderBy, limit);
	}

	/**
	 * Execute update using the current internal state as {@code WHERE} clause.
	 */
	public int update(SQLiteDatabase db, ContentValues values) {
		assertTable();
		LogUtils.log(Log.INFO, TAG, "update() " + this);
		return db.update(builderTable, values, getSelection(), getSelectionArgs());
	}

	/**
	 * Execute delete using the current internal state as {@code WHERE} clause.
	 */
	public int delete(SQLiteDatabase db) {
		assertTable();
		LogUtils.log(Log.INFO, TAG, "delete() " + this);
		return db.delete(builderTable, getSelection(), getSelectionArgs());
	}

}
