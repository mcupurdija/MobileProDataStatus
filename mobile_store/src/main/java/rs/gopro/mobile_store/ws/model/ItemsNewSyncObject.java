package rs.gopro.mobile_store.ws.model;

import java.util.Date;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import android.database.Cursor;
import android.os.Parcel;

public class ItemsNewSyncObject extends ItemsSyncObject {

	public static String TAG = "ItemsNewSyncObject";
	
	public static final Creator<ItemsNewSyncObject> CREATOR = new Creator<ItemsNewSyncObject>() {

		@Override
		public ItemsNewSyncObject createFromParcel(Parcel source) {
			return new ItemsNewSyncObject(source);
		}

		@Override
		public ItemsNewSyncObject[] newArray(int size) {
			return new ItemsNewSyncObject[size];
		}

	};
	
	public ItemsNewSyncObject() {
		super();
	}

	public ItemsNewSyncObject(Parcel source) {
		super(source);
	}

	public ItemsNewSyncObject(String mCSVString, String mItemNoa46, Integer mOverstockAndCampaignOnly, String mSalespersonCode, Date mDateModified) {
		super(mCSVString, mItemNoa46, mOverstockAndCampaignOnly, mSalespersonCode, mDateModified);
	}
	
	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	protected void calculateDateModified() {
		super.calculateDateModified();
		
		Date maxDate = null;
		Cursor cursor = context.getContentResolver().query(MobileStoreContract.SyncLogs.CONTENT_URI, new String[] { "MAX(DATE(" + SyncLogs.CREATED_DATE + "))" }, SyncLogs.SYNC_OBJECT_ID+"=? AND "+SyncLogs.SYNC_OBJECT_STATUS+"=?", new String[] { getTag(), SyncStatus.SUCCESS.toString() }, null);
		if (cursor.moveToFirst()) {
			maxDate = DateUtils.getLocalDbShortDate(cursor.getString(0));
		} else {
			maxDate = DateUtils.getWsDummyDate();
		}
		setmDateModified(maxDate);
	}
}
