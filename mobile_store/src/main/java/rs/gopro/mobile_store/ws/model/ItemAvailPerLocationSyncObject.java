package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import rs.gopro.mobile_store.ws.formats.WsDataFormatEnUsLatin;
import android.content.ContentResolver;
import android.os.Parcel;

public class ItemAvailPerLocationSyncObject extends SyncObject {
	
	public static String TAG = "ItemAvailPerLocationSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.ITEM_AVAILABILITY_PER_LOCATION_SYNC_ACTION";

	private String pItemNo;
	private String pLocationCode;
	private Integer pAllLocations;
	private String cSVString;
	private String totalQtyTxt;
	private String itemPriceTxt;
	
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
			Integer pAllLocations, String cSVString, String totalQtyTxt, String itemPriceTxt) {
		super();
		this.pItemNo = pItemNo;
		this.pLocationCode = pLocationCode;
		this.pAllLocations = pAllLocations;
		this.cSVString = cSVString;
		this.totalQtyTxt = totalQtyTxt;
		this.itemPriceTxt = itemPriceTxt;
	}
	
	public ItemAvailPerLocationSyncObject(Parcel parcel) {
		super(parcel);
		setpItemNo(parcel.readString());
		setpLocationCode(parcel.readString());
		setpAllLocations(parcel.readInt());
		setcSVString(parcel.readString());
		setTotalQtyTxt(parcel.readString());
		setItemPriceTxt(parcel.readString());
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
		dest.writeString(getcSVString());
		dest.writeString(getTotalQtyTxt());
		dest.writeString(getItemPriceTxt());
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
		
		PropertyInfo cSVStringInfo = new PropertyInfo();
		cSVStringInfo.setName("cSVString");
		cSVStringInfo.setValue(cSVString);
		cSVStringInfo.setType(String.class);
		properties.add(cSVStringInfo);
		
		PropertyInfo totalQtyTxtInfo = new PropertyInfo();
		totalQtyTxtInfo.setName("totalQtyTxt");
		totalQtyTxtInfo.setValue(totalQtyTxt);
		totalQtyTxtInfo.setType(String.class);
		properties.add(totalQtyTxtInfo);
		
		PropertyInfo itemPriceTxtInfo = new PropertyInfo();
		itemPriceTxtInfo.setName("itemPriceTxt");
		itemPriceTxtInfo.setValue(itemPriceTxt);
		itemPriceTxtInfo.setType(String.class);
		properties.add(itemPriceTxtInfo);
		
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
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		
		this.cSVString = soapResponse.getPropertyAsString("cSVString");
		this.totalQtyTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("totalQtyTxt"));
		this.itemPriceTxt = WsDataFormatEnUsLatin.normalizeDouble(soapResponse.getPropertyAsString("itemPriceTxt"));
		
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

	public String getcSVString() {
		return cSVString;
	}

	public void setcSVString(String cSVString) {
		this.cSVString = cSVString;
	}

	public String getTotalQtyTxt() {
		return totalQtyTxt;
	}

	public void setTotalQtyTxt(String totalQtyTxt) {
		this.totalQtyTxt = totalQtyTxt;
	}

	public String getItemPriceTxt() {
		return itemPriceTxt;
	}

	public void setItemPriceTxt(String itemPriceTxt) {
		this.itemPriceTxt = itemPriceTxt;
	}

}
