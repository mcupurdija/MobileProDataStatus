package rs.gopro.mobile_store.ws.model.domain;

import java.util.ArrayList;
import java.util.List;

import rs.gopro.mobile_store.provider.MobileStoreContract.Items;
import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class ItemsDomain extends Domain {
	
	public String item_no;
	public String description;
	public String unit_of_measure;
	public String category_code;
	public String group_code;

	public ItemsDomain() {
	}

	private static final String[] COLUMNS = new String[] { "item_no", "description", "unit_of_measure", "category_code", "group_code" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(Items.ITEM_NO, getItem_no());
		contentValues.put(Items.DESCRIPTION, getDescription());
		contentValues.put(Items.UNIT_OF_MEASURE, getUnit_of_measure());
		contentValues.put(Items.CATEGORY_CODE, getCategory_code());
		contentValues.put(Items.GROUP_CODE, getGroup_code());
		return contentValues;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		List<RowItemDataHolder> dataHolders = new ArrayList<RowItemDataHolder>();
		return dataHolders;
	}

	public String getItem_no() {
		return item_no;
	}

	public void setItem_no(String item_no) {
		this.item_no = item_no;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUnit_of_measure() {
		return unit_of_measure;
	}

	public void setUnit_of_measure(String unit_of_measure) {
		this.unit_of_measure = unit_of_measure;
	}

	public String getCategory_code() {
		return category_code;
	}

	public void setCategory_code(String category_code) {
		this.category_code = category_code;
	}

	public String getGroup_code() {
		return group_code;
	}

	public void setGroup_code(String group_code) {
		this.group_code = group_code;
	}

}
