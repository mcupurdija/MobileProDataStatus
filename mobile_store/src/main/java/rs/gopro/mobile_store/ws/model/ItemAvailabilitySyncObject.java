package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.os.Parcel;

public class ItemAvailabilitySyncObject extends SyncObject {

	private static String TAG = "ItemAvailabilitySyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.ITEM_AVAILABILITY_SYNC_ACTION";
	
	private String pItemNo;
	private String pAvailableQtyPerLocFilterTxt;
	private String pOutstandingPurchaseLinesTxt;
	
	public static final Creator<ItemAvailabilitySyncObject> CREATOR = new Creator<ItemAvailabilitySyncObject>() {

		@Override
		public ItemAvailabilitySyncObject createFromParcel(Parcel source) {
			return new ItemAvailabilitySyncObject(source);
		}

		@Override
		public ItemAvailabilitySyncObject[] newArray(int size) {
			return new ItemAvailabilitySyncObject[size];
		}

	};
	
	public ItemAvailabilitySyncObject() {
		super();
	}

	public ItemAvailabilitySyncObject(String itemNo) {
		super();
		setpItemNo(itemNo);
	}
	
	public ItemAvailabilitySyncObject(Parcel source) {
		super(source);
		setpItemNo(source.readString());
		setpAvailableQtyPerLocFilterTxt(source.readString());
		setpOutstandingPurchaseLinesTxt(source.readString());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpItemNo());
		dest.writeString(getpAvailableQtyPerLocFilterTxt());
		dest.writeString(getpOutstandingPurchaseLinesTxt());
	}

	@Override
	public String getWebMethodName() {
		return "GetItemAvailabilityPerLocation";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();

		PropertyInfo pItemNoa46 = new PropertyInfo();
		pItemNoa46.setName("pItemNoa46");
		pItemNoa46.setValue(getpItemNo());
		pItemNoa46.setType(String.class);
		properies.add(pItemNoa46);
		
		PropertyInfo availableQtyPerLocFilterTxt = new PropertyInfo();
		availableQtyPerLocFilterTxt.setName("pAvailableQtyPerLocFilterTxt");
		availableQtyPerLocFilterTxt.setValue(getpAvailableQtyPerLocFilterTxt());
		availableQtyPerLocFilterTxt.setType(String.class);
		properies.add(availableQtyPerLocFilterTxt);
		
		PropertyInfo outstandingPurchaseLinesTxt = new PropertyInfo();
		outstandingPurchaseLinesTxt.setName("pOutstandingPurchaseLinesTxt");
		outstandingPurchaseLinesTxt.setValue(getpOutstandingPurchaseLinesTxt());
		outstandingPurchaseLinesTxt.setType(String.class);
		properies.add(outstandingPurchaseLinesTxt);
		
		return properies;
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
	protected int parseAndSave(ContentResolver contentResolver,
			SoapPrimitive soapResponse) throws CSVParseException {
		//setpAvailableQtyPerLocFilterTxt(soapResponse.toString());
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		setpAvailableQtyPerLocFilterTxt(soapResponse.getPropertyAsString("pAvailableQtyPerLocFilterTxt"));
		setpOutstandingPurchaseLinesTxt(soapResponse.getPropertyAsString("pOutstandingPurchaseLinesTxt"));
		return 0;
	}

	public String getpItemNo() {
		return pItemNo;
	}

	public void setpItemNo(String pItemNo) {
		this.pItemNo = pItemNo;
	}

	public String getpAvailableQtyPerLocFilterTxt() {
		return pAvailableQtyPerLocFilterTxt;
	}

	public void setpAvailableQtyPerLocFilterTxt(String pAvailableQtyPerLocFilterTxt) {
		this.pAvailableQtyPerLocFilterTxt = pAvailableQtyPerLocFilterTxt;
	}

	public String getpOutstandingPurchaseLinesTxt() {
		return pOutstandingPurchaseLinesTxt;
	}

	public void setpOutstandingPurchaseLinesTxt(String pOutstandingPurchaseLinesTxt) {
		this.pOutstandingPurchaseLinesTxt = pOutstandingPurchaseLinesTxt;
	}

}
