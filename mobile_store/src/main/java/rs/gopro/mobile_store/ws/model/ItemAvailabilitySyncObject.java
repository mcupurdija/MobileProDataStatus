package rs.gopro.mobile_store.ws.model;

import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.os.Parcel;

public class ItemAvailabilitySyncObject extends SyncObject {

	private String pItemNo;
	private String pAvailableQtyPerLocFilterTxt;
	
	public ItemAvailabilitySyncObject() {
		// TODO Auto-generated constructor stub
	}

	public ItemAvailabilitySyncObject(Parcel source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getWebMethodName() {
		return "GetItemAvailability";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTag() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBroadcastAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapPrimitive soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver,
			SoapObject soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

}
