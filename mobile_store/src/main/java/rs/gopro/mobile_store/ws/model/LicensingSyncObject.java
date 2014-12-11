package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.provider.MobileStoreContract.Licensing;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Parcel;

public class LicensingSyncObject extends SyncObject {
	
	public static String TAG = "LicensingSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.LICENSING_SYNC_ACTION";
	
	private String licenceNo;

	public static final Creator<LicensingSyncObject> CREATOR = new Creator<LicensingSyncObject>() {

		@Override
		public LicensingSyncObject createFromParcel(Parcel source) {
			return new LicensingSyncObject(source);
		}

		@Override
		public LicensingSyncObject[] newArray(int size) {
			return new LicensingSyncObject[size];
		}

	};
	
	public LicensingSyncObject(Parcel parcel) {
		super(parcel);
		setLicenceNo(parcel.readString());

	}
	
	public LicensingSyncObject() {
		super();
	}

	public LicensingSyncObject(String licenceNo) {
		super();
		this.licenceNo = licenceNo;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(getStatusMessage());
		dest.writeString(getLicenceNo());
	}

	@Override
	public String getWebMethodName() {
		return "FGetCurrentLicenceNo";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		
		PropertyInfo pLicenseNo = new PropertyInfo();
		pLicenseNo.setName("licenceNo");
		pLicenseNo.setValue(licenceNo);
		pLicenseNo.setType(String.class);
		properties.add(pLicenseNo);

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
		
		ContentValues cv = new ContentValues();
		cv.put(Licensing.LICENSE_NO, soapResponse.toString());
		cv.put(Licensing.LAST_SYNC_DATE, DateUtils.toDbDate(new Date()));
		
		return contentResolver.update(Licensing.CONTENT_URI, cv, Tables.LICENSING + "." + Licensing._ID + "=?", new String[] { "1" });
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException {
		return 0;
	}

	public String getLicenceNo() {
		return licenceNo;
	}

	public void setLicenceNo(String licenceNo) {
		this.licenceNo = licenceNo;
	}

}
