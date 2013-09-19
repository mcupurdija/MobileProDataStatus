package rs.gopro.mobile_store.ws.model;

import java.util.Date;

import android.os.Parcel;

public class ItemsOverstockSyncObject extends ItemsSyncObject {

	public static String TAG = "ItemsOverstockSyncObject";
	
	public static final Creator<ItemsOverstockSyncObject> CREATOR = new Creator<ItemsOverstockSyncObject>() {

		@Override
		public ItemsOverstockSyncObject createFromParcel(Parcel source) {
			return new ItemsOverstockSyncObject(source);
		}

		@Override
		public ItemsOverstockSyncObject[] newArray(int size) {
			return new ItemsOverstockSyncObject[size];
		}

	};
	
	public ItemsOverstockSyncObject() {
		super();
	}

	public ItemsOverstockSyncObject(Parcel source) {
		super(source);
	}

	public ItemsOverstockSyncObject(String mCSVString, String mItemNoa46, Integer mOverstockAndCampaignOnly, String mSalespersonCode, Date mDateModified) {
		super(mCSVString, mItemNoa46, mOverstockAndCampaignOnly, mSalespersonCode, mDateModified);
	}
	
	@Override
	public String getTag() {
		return TAG;
	}
}
