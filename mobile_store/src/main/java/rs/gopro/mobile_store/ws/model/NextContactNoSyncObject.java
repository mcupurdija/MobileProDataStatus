package rs.gopro.mobile_store.ws.model;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;

import rs.gopro.mobile_store.util.exceptions.SOAPResponseException;
import android.content.ContentResolver;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class NextContactNoSyncObject extends SyncObject {

	public static String TAG = "NextContactNoSyncObject";
	public static String BROADCAST_SYNC_ACTION = "rs.gopro.mobile_store.NEXT_CONTACT_NO_SYNC_ACTION";

	public static final Creator<NextContactNoSyncObject> CREATOR = new Creator<NextContactNoSyncObject>() {

		@Override
		public NextContactNoSyncObject createFromParcel(Parcel source) {
			return new NextContactNoSyncObject(source);
		}

		@Override
		public NextContactNoSyncObject[] newArray(int size) {
			return new NextContactNoSyncObject[size];
		}

	};

	public NextContactNoSyncObject() {
		super();
	}

	public NextContactNoSyncObject(Parcel parcel) {
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
		return "GetNextContactNo";
	}

	@Override
	public List<PropertyInfo> getSOAPRequestProperties() {
		List<PropertyInfo> properties = new ArrayList<PropertyInfo>();
		return properties;
	}

	@Override
	public void saveSOAPResponse(Object response, ContentResolver contentResolver) throws SOAPResponseException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public String getBroadcastAction() {
		return BROADCAST_SYNC_ACTION;
	}

}
