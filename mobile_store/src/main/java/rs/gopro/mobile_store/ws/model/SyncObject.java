package rs.gopro.mobile_store.ws.model;

import java.util.List;

import org.ksoap2.serialization.PropertyInfo;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.exceptions.SOAPResponseException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

public abstract class SyncObject implements Parcelable {

	protected static String WS_ENTRY_POINT = "/codeunit/MobileDeviceSync";

	protected static String WS_SCHEME = "urn:microsoft-dynamics-schemas";

	protected static String WS_NAMESPACE = WS_SCHEME + WS_ENTRY_POINT;

	protected static String WS_NAVISION_CODEUNIT_NAME = "/Codeunit/MobileDeviceSync";

	protected static String WS_SERVER_ADDRESS = "http://10.94.1.5:7047/DynamicsNAV/WS";// "http://sqlserver.gopro.rs:7047/Wurth/WS";

	//uri for update, no need in Parcel
	protected Uri currentUri;
	private String statusMessage;
	protected String result;
	protected Context context;

	public SyncObject() {
	}

	public SyncObject(Parcel source) {
		setStatusMessage(source.readString());
	}


	public String getNamespace() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		String wsSchema = sharedPreferences.getString(context.getString(R.string.key_ws_schema), null);
		String entyPoint = sharedPreferences.getString(context.getString(R.string.key_ws_entry_point), null);
		return wsSchema + entyPoint;
	}

	public String getUrl() {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			String serverAddress = sharedPreferences.getString(context.getString(R.string.key_ws_server_address), null);
			String navisionCodeUnit = sharedPreferences.getString(context.getString(R.string.key_ws_navisition_codeunit), null);
			return serverAddress + "/"+ getCompany() + navisionCodeUnit;
	}

	protected String getCompany() {
			SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
			String company = sharedPreferences.getString(context.getString(R.string.key_company), null);
			System.out.println("COMPANY: "+company);
			return company;	
	}

	public String getSoapAction() {
		return getNamespace() + "/:" + getWebMethodName();
	}

	public abstract String getWebMethodName();

	public abstract List<PropertyInfo> getSOAPRequestProperties();

	public abstract void saveSOAPResponse(Object response, ContentResolver contentResolver) throws SOAPResponseException;

	/**
	 * Tag represent name of class
	 * @return
	 */
	public abstract String getTag();
	
	public abstract String getBroadcastAction();

	public void logSyncStart(ContentResolver contentResolver) {
		Integer maxBatch = Integer.valueOf(0);
		Cursor cursor = contentResolver.query(SyncLogs.buildSyncLogsObjectIdUri(getTag()), new String[] { "MAX("+SyncLogs.SYNC_OBJECT_BATCH+")" }, null, null, null);
		if (cursor.moveToFirst()) {
			maxBatch = cursor.getInt(0);
		}
		maxBatch = maxBatch + 1;
		ContentValues values = new ContentValues();
		values.put(SyncLogs.SYNC_OBJECT_NAME, getTag());
		values.put(SyncLogs.SYNC_OBJECT_ID, getTag());
		values.put(SyncLogs.SYNC_OBJECT_STATUS, ApplicationConstants.SyncStatus.IN_PROCCESS.toString());
		values.put(SyncLogs.SYNC_OBJECT_BATCH, maxBatch);
		currentUri = contentResolver.insert(SyncLogs.CONTENT_URI, values);
	}
	
	
	public void logSyncEnd(ContentResolver contentResolver, SyncStatus syncStatus) {
		ContentValues values = new ContentValues();
		values.put(SyncLogs.SYNC_OBJECT_STATUS, syncStatus.toString());
		contentResolver.update(currentUri, values, null, null);
	}


	public String getResult() {
		return result;
	}
	
	


	public String getStatusMessage() {
		return statusMessage;
	}

	protected void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
