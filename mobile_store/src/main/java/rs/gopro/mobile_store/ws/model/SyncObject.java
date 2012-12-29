package rs.gopro.mobile_store.ws.model;

import java.util.List;

import org.ksoap2.serialization.PropertyInfo;

import android.content.ContentResolver;
import android.os.Parcel;
import android.os.Parcelable;

public abstract class SyncObject implements Parcelable {
	
	protected static String WS_ENTRY_POINT = "/codeunit/MobileDeviceSync";
	
	protected static String WS_SCHEME = "urn:microsoft-dynamics-schemas";
	
	protected static String WS_NAMESPACE = WS_SCHEME+WS_ENTRY_POINT;

	protected static String WS_NAVISION_CODEUNIT_NAME = "/Codeunit/MobileDeviceSync";
	
	protected static String WS_SERVER_ADDRESS = "http://sqlserver.gopro.rs:7047/Wurth/WS";
	
	private String mCompany = "Wurth%20-%20Development";
	
	public SyncObject() {
	}
	
	public SyncObject(Parcel source) {
		setCompany(source.readString());
	}
	
	public SyncObject(String company) {
		mCompany = company;
	}
	
	public String getNamespace() {
		return WS_NAMESPACE;
	}
	
	
	public String getUrl() {
		return WS_SERVER_ADDRESS+"/"+getCompany()+WS_NAVISION_CODEUNIT_NAME;
	}
	
	protected String getCompany() {
		return mCompany;
	}
	
	public void setCompany(String company) {
		mCompany = company;
	}
	
	public String getSoapAction() {
		return  getNamespace()+"/:"+getWebMethodName();
	}
	
	public abstract String getWebMethodName();
	public abstract void logSyncStart(ContentResolver contentResolver);
	public abstract List<PropertyInfo> getSOAPRequestProperties();
	public abstract void saveSOAPResponse(Object response, ContentResolver contentResolver);
	public abstract void logSyncEnd(ContentResolver contentResolver);
}
