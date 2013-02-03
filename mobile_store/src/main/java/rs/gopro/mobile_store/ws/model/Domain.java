package rs.gopro.mobile_store.ws.model;

import java.util.List;

import rs.gopro.mobile_store.ws.util.RowItemDataHolder;
import android.content.ContentValues;

public abstract class Domain {
	public abstract String[] getCSVMappingStrategy();
	public abstract ContentValues getContentValues();
	public abstract List<RowItemDataHolder> getRowItemsForRepalce();
	
}
