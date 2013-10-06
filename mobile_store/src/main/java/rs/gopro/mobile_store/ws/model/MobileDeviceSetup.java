package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.os.Parcel;

public class MobileDeviceSetup extends SyncObject {

	public static String TAG = "MobileDeviceSetup";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.MOBILE_DEVICE_SETUP_SYNC_ACTION";
	
	public String pCSVString;
	
	public static final Creator<MobileDeviceSetup> CREATOR = new Creator<MobileDeviceSetup>() {

		@Override
		public MobileDeviceSetup createFromParcel(Parcel source) {
			return new MobileDeviceSetup(source);
		}

		@Override
		public MobileDeviceSetup[] newArray(int size) {
			return new MobileDeviceSetup[size];
		}
	};
	
	public MobileDeviceSetup() {
		super();
	}
	
	public MobileDeviceSetup(Parcel parcel) {
		super(parcel);
		setpCSVString(parcel.readString());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(getStatusMessage());
		dest.writeString(getpCSVString());
	}

	@Override
	public String getWebMethodName() {
		return "GetMobileDeviceSetup";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properies = new ArrayList<PropertyInfo>();

		PropertyInfo cSVString = new PropertyInfo();
		cSVString.setName("pCSVString");
		cSVString.setValue(pCSVString);
		cSVString.setType(String.class);
		properies.add(cSVString);
		
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
		soapResponse.toString();
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		soapResponse.toString();
		return 0;
	}

	public String getpCSVString() {
		return pCSVString;
	}

	public void setpCSVString(String pCSVString) {
		this.pCSVString = pCSVString;
	}

}
