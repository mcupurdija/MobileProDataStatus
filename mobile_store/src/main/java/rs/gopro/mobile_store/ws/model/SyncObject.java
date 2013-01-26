package rs.gopro.mobile_store.ws.model;

import java.util.List;

import org.ksoap2.serialization.PropertyInfo;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.util.exceptions.SOAPResponseException;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;

public abstract class SyncObject implements Parcelable {

	protected static String WS_ENTRY_POINT = "/codeunit/MobileDeviceSync";

	protected static String WS_SCHEME = "urn:microsoft-dynamics-schemas";

	protected static String WS_NAMESPACE = WS_SCHEME + WS_ENTRY_POINT;

	protected static String WS_NAVISION_CODEUNIT_NAME = "/Codeunit/MobileDeviceSync";

	protected static String WS_SERVER_ADDRESS = "http://10.94.1.5:7047/DynamicsNAV/WS";// "http://sqlserver.gopro.rs:7047/Wurth/WS";

	//private String mCompany = "Wurth%20-%20Development";
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

	public abstract void logSyncStart(ContentResolver contentResolver);

	public abstract List<PropertyInfo> getSOAPRequestProperties();

	public abstract void saveSOAPResponse(Object response, ContentResolver contentResolver) throws SOAPResponseException;

	public abstract void logSyncEnd(ContentResolver contentResolver);

	public String getResult() {
		return result;
	}
	
	
	public abstract String getBroadcastAction();

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
