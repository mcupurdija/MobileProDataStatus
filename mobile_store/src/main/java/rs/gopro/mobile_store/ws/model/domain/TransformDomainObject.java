package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Generic;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class TransformDomainObject {

	public static TransformDomainObject newInstance() {
		return new TransformDomainObject();
	}

	/**
	 * Transforms domain objects to content values
	 * 
	 * @param contentResolver
	 * @param parsedDomains
	 * @return
	 */
	public <T extends Domain> ContentValues[] transformDomainToContentValues(ContentResolver contentResolver, List<T> parsedDomains) {
		List<ContentValues> valuesForDb = new ArrayList<ContentValues>();
		for (Domain domain : parsedDomains) {
			ContentValues contentValues = domain.getContentValues();
			if (domain.getRowItemsForRepalce() != null) {
				for (RowItemDataHolder dataHolder : domain.getRowItemsForRepalce()) {
					Integer recordId = getRecordId(dataHolder, contentResolver);
					contentValues.remove(dataHolder.getNoColumn());
					contentValues.put(dataHolder.getIdColumn(), recordId);
				}
			}
			valuesForDb.add(contentValues);
		}
		ContentValues[] valuesForInsert = valuesForDb.toArray(new ContentValues[valuesForDb.size()]);
		return valuesForInsert;
	}

	/**
	 * Transforms domain objects to content values with adding some default values
	 * 
	 * @param contentResolver
	 * @param parsedDomains
	 * @param additionalStaticValues
	 * @return
	 */
	public <T extends Domain> ContentValues[] transformDomainToContentValues(ContentResolver contentResolver, List<T> parsedDomains, ContentValues additionalStaticValues) {
		List<ContentValues> valuesForDb = new ArrayList<ContentValues>();
		for (Domain domain : parsedDomains) {
			ContentValues contentValues = domain.getContentValues();
			if (domain.getRowItemsForRepalce() != null) {
				for (RowItemDataHolder dataHolder : domain.getRowItemsForRepalce()) {
					Integer recordId = getRecordId(dataHolder, contentResolver);
					contentValues.remove(dataHolder.getNoColumn());
					contentValues.put(dataHolder.getIdColumn(), recordId);
				}
			}
			
			if (additionalStaticValues != null && additionalStaticValues.size() > 0) {
				contentValues.putAll(additionalStaticValues);
			}
			
			valuesForDb.add(contentValues);
		}
		ContentValues[] valuesForInsert = valuesForDb.toArray(new ContentValues[valuesForDb.size()]);
		return valuesForInsert;
	}
	
	/**
	 * Resolves _id to no
	 * 
	 * @param dataHolder
	 * @param contentResolver
	 * @return
	 */
	private Integer getRecordId(RowItemDataHolder dataHolder, ContentResolver contentResolver) {
		Integer recordId = null;
		Uri uri = Generic.buildTableUri(dataHolder.getTable());
		Cursor cursor = contentResolver.query(uri, new String[] { BaseColumns._ID }, dataHolder.getNoColumn() + "=?", new String[] { dataHolder.getNoColumnValue() }, null);
		if (cursor.moveToNext()) {
			recordId = cursor.getInt(0);
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return recordId;
	}

}
