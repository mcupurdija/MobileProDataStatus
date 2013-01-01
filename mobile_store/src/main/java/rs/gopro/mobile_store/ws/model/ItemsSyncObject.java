package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapPrimitive;

import android.content.ContentResolver;
import android.os.Parcel;
import android.os.Parcelable;

import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.util.LogUtils;

public class ItemsSyncObject extends SyncObject {
	private static String TAG = "ItemsSyncObject";
	
	private String mCSVString;
	private String mItemNoa46;
	private Date mDateModified;
	private Integer mOverstockAndCampaignOnly;
	
	public static final Creator<ItemsSyncObject> CREATOR = new Creator<ItemsSyncObject>() {
		
		@Override
		public ItemsSyncObject createFromParcel(Parcel source) {
			return new ItemsSyncObject(source);
		}

		@Override
		public ItemsSyncObject[] newArray(int size) {
			return new ItemsSyncObject[size];
		}
		
	};
	
	public ItemsSyncObject() {
		super();
	}
	
	public ItemsSyncObject(Parcel source) {
		// there is reading of properties here
		super(source);
		
		setmCSVString(source.readString());
		setmItemNoa46(source.readString());
		setmDateModified(new Date(source.readLong()));
		setmOverstockAndCampaignOnly(source.readInt());
	}
	
	public ItemsSyncObject(String mCSVString, String mItemNoa46,
			Date mDateModified, Integer mOverstockAndCampaignOnly) {
		super();
		this.mCSVString = mCSVString;
		this.mItemNoa46 = mItemNoa46;
		this.mDateModified = mDateModified;
		this.mOverstockAndCampaignOnly = mOverstockAndCampaignOnly;
	}

	@Override
	public String getWebMethodName() {
		return "GetItems";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>(); 
		
		PropertyInfo pCSVString = new PropertyInfo();
		pCSVString.setName("pCSVString");
		pCSVString.setValue(mCSVString);
		pCSVString.setType(String.class);
		properies.add(pCSVString);
	  
		PropertyInfo pItemNoa46 = new PropertyInfo();
		pItemNoa46.setName("pItemNoa46");
		pItemNoa46.setValue(mItemNoa46);
		pItemNoa46.setType(String.class);
		properies.add(pItemNoa46);
	  
	    PropertyInfo pDateModified = new PropertyInfo();
	    pDateModified.setName("pDateModified");
	    pDateModified.setValue(mDateModified);
	    pDateModified.setType(Date.class);
	    properies.add(pDateModified);
	  
	    PropertyInfo pOverstockAndCampaignOnly = new PropertyInfo();
	    pOverstockAndCampaignOnly.setName("pOverstockAndCampaignOnly");
	    pOverstockAndCampaignOnly.setValue(mOverstockAndCampaignOnly);
	    pOverstockAndCampaignOnly.setType(Integer.class);
	    properies.add(pOverstockAndCampaignOnly);
		
	    return properies;
	}
	
	@Override
	public void logSyncStart(ContentResolver contentResolver) {
		//MobileStoreContract.Items.
		contentResolver.insert(null, null);
	}
	

	@Override
	public void saveSOAPResponse(Object response, ContentResolver contentResolver) {
		SoapPrimitive result = (SoapPrimitive)response;
		LogUtils.LOGI(TAG, result.toString());
	}

	@Override
	public void logSyncEnd(ContentResolver contentResolver) {
		// TODO Auto-generated method stub
		LogUtils.LOGI(TAG, "Sync of items finished.");
	}

	public String getmCSVString() {
		return mCSVString;
	}

	public void setmCSVString(String mCSVString) {
		this.mCSVString = mCSVString;
	}

	public String getmItemNoa46() {
		return mItemNoa46;
	}

	public void setmItemNoa46(String mItemNoa46) {
		this.mItemNoa46 = mItemNoa46;
	}

	public Date getmDateModified() {
		return mDateModified;
	}

	public void setmDateModified(Date mDateModified) {
		this.mDateModified = mDateModified;
	}

	public Integer getmOverstockAndCampaignOnly() {
		return mOverstockAndCampaignOnly;
	}

	public void setmOverstockAndCampaignOnly(Integer mOverstockAndCampaignOnly) {
		this.mOverstockAndCampaignOnly = mOverstockAndCampaignOnly;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getCompany());
		dest.writeString(getStatusMessage());
		dest.writeString(getmCSVString());
		dest.writeString(getmItemNoa46());
		dest.writeLong(getmDateModified().getTime());
		dest.writeInt(getmOverstockAndCampaignOnly());
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return null;
	}
}
