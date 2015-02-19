package rs.gopro.mobile_store.ws.model.domain;

import java.util.List;

import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public class ItemLocations extends Domain {
	
	public String location;
	public String quantity;
	
	public ItemLocations() {
	}

	private static final String[] COLUMNS = new String[] { "location", "quantity" };

	@Override
	public String[] getCSVMappingStrategy() {
		return COLUMNS;
	}

	@Override
	public ContentValues getContentValues() {
		return null;
	}

	@Override
	public List<RowItemDataHolder> getRowItemsForRepalce() {
		return null;
	}

}
