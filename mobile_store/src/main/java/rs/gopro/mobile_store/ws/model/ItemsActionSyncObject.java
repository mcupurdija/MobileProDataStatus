package rs.gopro.mobile_store.ws.model;

import java.util.Date;

import android.os.Parcel;

public class ItemsActionSyncObject extends ItemsSyncObject {

	public static String TAG = "ItemsActionSyncObject";
	
	public static final Creator<ItemsActionSyncObject> CREATOR = new Creator<ItemsActionSyncObject>() {

		@Override
		public ItemsActionSyncObject createFromParcel(Parcel source) {
			return new ItemsActionSyncObject(source);
		}

		@Override
		public ItemsActionSyncObject[] newArray(int size) {
			return new ItemsActionSyncObject[size];
		}

	};
	
	public ItemsActionSyncObject() {
		super();
	}

	public ItemsActionSyncObject(Parcel source) {
		super(source);
	}

	public ItemsActionSyncObject(String mCSVString, String mItemNoa46, Integer mOverstockAndCampaignOnly, String mSalespersonCode,
			Date mDateModified) {
		super(mCSVString, mItemNoa46, mOverstockAndCampaignOnly, mSalespersonCode, mDateModified);
	}

	@Override
	public String getTag() {
		return TAG;
	}
}
