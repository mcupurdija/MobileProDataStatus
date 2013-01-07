package rs.gopro.mobile_store.ws.model;

import android.content.ContentValues;

public abstract class Domain {
	public abstract String[] getCSVMappingStrategy();
	public abstract ContentValues getContentValues();
}
