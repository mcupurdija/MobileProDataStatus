package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.os.Parcel;

public class ItemAvailPerLocationSyncObject extends SyncObject {
	
	public static String TAG = "ItemAvailPerLocationSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.ITEM_AVAILABILITY_PER_LOCATION_SYNC_ACTION";

	private String pItemNo;
	private String pLocationCode;
	private Integer pAllLocations;
	private String pAvailQtyTxt;
	
	public static final Creator<ItemAvailPerLocationSyncObject> CREATOR = new Creator<ItemAvailPerLocationSyncObject>() {

		@Override
		public ItemAvailPerLocationSyncObject createFromParcel(Parcel source) {
			return new ItemAvailPerLocationSyncObject(source);
		}

		@Override
		public ItemAvailPerLocationSyncObject[] newArray(int size) {
			return new ItemAvailPerLocationSyncObject[size];
		}

	};
	
	public ItemAvailPerLocationSyncObject() {
		super();
	}

	public ItemAvailPerLocationSyncObject(String pItemNo, String pLocationCode,
			Integer pAllLocations, String pAvailQtyTxt) {
		super();
		this.pItemNo = pItemNo;
		this.pLocationCode = pLocationCode;
		this.pAllLocations = pAllLocations;
		this.pAvailQtyTxt = pAvailQtyTxt;
	}
	
	public ItemAvailPerLocationSyncObject(Parcel parcel) {
		super(parcel);
		setpItemNo(parcel.readString());
		setpLocationCode(parcel.readString());
		setpAllLocations(parcel.readInt());
		setpAvailQtyTxt(parcel.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpItemNo());
		dest.writeString(getpLocationCode());
		dest.writeInt(getpAllLocations());
		dest.writeString(getpLocationCode());
	}

	@Override
	public String getWebMethodName() {
		return "GetItemAvailPerLocation";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		
		PropertyInfo pItemNoInfo = new PropertyInfo();
		pItemNoInfo.setName("pItemNo");
		pItemNoInfo.setValue(pItemNo);
		pItemNoInfo.setType(String.class);
		properties.add(pItemNoInfo);
		
		PropertyInfo pLocationCodeInfo = new PropertyInfo();
		pLocationCodeInfo.setName("pLocationCode");
		pLocationCodeInfo.setValue(pLocationCode);
		pLocationCodeInfo.setType(String.class);
		properties.add(pLocationCodeInfo);
		
		PropertyInfo pAllLocationsInfo = new PropertyInfo();
		pAllLocationsInfo.setName("pAllLocations");
		pAllLocationsInfo.setValue(pAllLocations);
		pAllLocationsInfo.setType(Integer.class);
		properties.add(pAllLocationsInfo);
		
		PropertyInfo pAvailQtyTxtInfo = new PropertyInfo();
		pAvailQtyTxtInfo.setName("pAvailQtyTxt");
		pAvailQtyTxtInfo.setValue(pAvailQtyTxt);
		pAvailQtyTxtInfo.setType(String.class);
		properties.add(pAvailQtyTxtInfo);
		
		return properties;
	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		
		this.pAvailQtyTxt = soapResponse.toString();
		
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		return 0;
	}

	public String getpItemNo() {
		return pItemNo;
	}

	public void setpItemNo(String pItemNo) {
		this.pItemNo = pItemNo;
	}

	public String getpLocationCode() {
		return pLocationCode;
	}

	public void setpLocationCode(String pLocationCode) {
		this.pLocationCode = pLocationCode;
	}

	public Integer getpAllLocations() {
		return pAllLocations;
	}

	public void setpAllLocations(Integer pAllLocations) {
		this.pAllLocations = pAllLocations;
	}

	public String getpAvailQtyTxt() {
		return pAvailQtyTxt;
	}

	public void setpAvailQtyTxt(String pAvailQtyTxt) {
		this.pAvailQtyTxt = pAvailQtyTxt;
	}

}
