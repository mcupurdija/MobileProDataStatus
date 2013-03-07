package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;

import rs.gopro.mobile_store.util.exceptions.CSVParseException;
import android.content.ContentResolver;
import android.os.Parcel;

public class NextMobDeviceSalesOrderNoSyncObject extends SyncObject {

	public static String TAG = "NextMobDeviceSalesOrderNoSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.NEXT_MOB_DEVICE_SALES_ORDER_SYNC_ACTION";

	public static final Creator<NextMobDeviceSalesOrderNoSyncObject> CREATOR = new Creator<NextMobDeviceSalesOrderNoSyncObject>() {

		@Override
		public NextMobDeviceSalesOrderNoSyncObject createFromParcel(Parcel source) {
			return new NextMobDeviceSalesOrderNoSyncObject(source);
		}

		@Override
		public NextMobDeviceSalesOrderNoSyncObject[] newArray(int size) {
			return new NextMobDeviceSalesOrderNoSyncObject[size];
		}

	};

	public NextMobDeviceSalesOrderNoSyncObject() {
		super();
	}

	public NextMobDeviceSalesOrderNoSyncObject(Parcel parcel) {
		super(parcel);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getWebMethodName() {
		return "GetNextMobDeviceSalesOrderNo";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		return properties;
	}

	@Override
	protected int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
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
			SoapObject soapResponse) throws CSVParseException {
		// TODO Auto-generated method stub
		return 0;
	}

}
