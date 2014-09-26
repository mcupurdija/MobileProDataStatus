package rs.gopro.mobile_store.ws.model.domain;

import java.util.List;

import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class SalesStatisticIndexDomain extends Domain {

	public static final String sortingIndexKey = "sorting_index";
	public static final String newSortingIndexKey = "new_sorting_index";
	
	public String sorting_index, new_sorting_index;
	
	private static final String[] COLUMNS = new String[] { "sorting_index", "new_sorting_index" };
	
	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(sortingIndexKey, getSorting_index());
		contentValues.put(newSortingIndexKey, getNew_sorting_index());
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		return null;
	}

	public String getSorting_index() {
		return sorting_index;
	}

	public void setSorting_index(String sorting_index) {
		this.sorting_index = sorting_index;
	}

	public String getNew_sorting_index() {
		return new_sorting_index;
	}

	public void setNew_sorting_index(String new_sorting_index) {
		this.new_sorting_index = new_sorting_index;
	}

}
