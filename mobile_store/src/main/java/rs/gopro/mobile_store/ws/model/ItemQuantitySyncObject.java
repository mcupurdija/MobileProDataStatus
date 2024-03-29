package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.os.Parcel;

public class ItemQuantitySyncObject extends SyncObject {

	public ItemQuantitySyncObject(String mItemNoa46, String mLocationCode, Integer mCampaignStatus) {
		super();
		this.mItemNoa46 = mItemNoa46;
		this.mLocationCode = mLocationCode;
		this.mCampaignStatus = mCampaignStatus;
	}

	private static String TAG = "ItemQuantitySyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.ITEM_QUANTITY_SYNC_ACTION";

	private String mItemNoa46;
	private String mLocationCode;
	private Integer mCampaignStatus;

	public static final Creator<ItemQuantitySyncObject> CREATOR = new Creator<ItemQuantitySyncObject>() {

		@Override
		public ItemQuantitySyncObject createFromParcel(Parcel source) {
			return new ItemQuantitySyncObject(source);
		}

		@Override
		public ItemQuantitySyncObject[] newArray(int size) {
			return new ItemQuantitySyncObject[size];
		}

	};

	public ItemQuantitySyncObject() {
		super();
	}

	public ItemQuantitySyncObject(Parcel source) {
		// there is reading of properties here
		super(source);

		setmItemNoa46(source.readString());
		setmLocationCode(source.readString());
		setmCampaignStatus(source.readInt());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getmItemNoa46());
		dest.writeString(getmLocationCode());
		dest.writeInt(getmCampaignStatus());
	}

	@Override
	public String getWebMethodName() {
		return "GetItemQuantity";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();

		PropertyInfo pItemNoa46 = new PropertyInfo();
		pItemNoa46.setName("pItemNoa46");
		pItemNoa46.setValue(mItemNoa46);
		pItemNoa46.setType(String.class);
		properies.add(pItemNoa46);

		PropertyInfo pCSVString = new PropertyInfo();
		pCSVString.setName("pLocationCode");
		pCSVString.setValue(mLocationCode);
		pCSVString.setType(String.class);
		properies.add(pCSVString);

		PropertyInfo pOverstockAndCampaignOnly = new PropertyInfo();
		pOverstockAndCampaignOnly.setName("pCampaignStatus");
		pOverstockAndCampaignOnly.setValue(mCampaignStatus);
		pOverstockAndCampaignOnly.setType(Integer.class);
		properies.add(pOverstockAndCampaignOnly);

		return properies;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getmItemNoa46() {
		return mItemNoa46;
	}

	public void setmItemNoa46(String mItemNoa46) {
		this.mItemNoa46 = mItemNoa46;
	}

	public Integer getmCampaignStatus() {
		return mCampaignStatus;
	}

	public void setmCampaignStatus(Integer mCampaignStatus) {
		this.mCampaignStatus = mCampaignStatus;
	}

	public String getmLocationCode() {
		return mLocationCode;
	}

	public void setmLocationCode(String mLocationCode) {
		this.mLocationCode = mLocationCode;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}
}
