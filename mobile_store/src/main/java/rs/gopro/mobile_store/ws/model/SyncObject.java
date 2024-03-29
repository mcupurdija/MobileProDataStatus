package rs.gopro.mobile_store.ws.model;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.ksoap2.SoapFault;
import org.ksoap2.SoapFault12;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.provider.MobileStoreContract;
import rs.gopro.mobile_store.provider.MobileStoreContract.SyncLogs;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.DateUtils;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.exceptions.CSVParseException;
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

	protected static String WS_ENTRY_POINT = "/Codeunit/MobileDeviceSync";
	protected static String WS_SCHEME = "urn:microsoft-dynamics-schemas";
	protected static String WS_NAMESPACE = WS_SCHEME + WS_ENTRY_POINT;
	protected static String WS_NAVISION_CODEUNIT_NAME = "/Codeunit/MobileDeviceSync";
	protected static String WS_SERVER_ADDRESS = "http://10.94.1.5:7047/Wurth/WS";// "http://sqlserver.gopro.rs:7047/Wurth/WS";

	// uri for update, no need in Parcel
	protected Uri currentUri;
	private String statusMessage;
	protected String result;
	protected Context context;
	private String sessionId;
	
	protected boolean lastSyncDateNeeded = false;

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
		return serverAddress + "/" + getCompany() + navisionCodeUnit;
	}

	protected String getCompany() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		String company = sharedPreferences.getString(context.getString(R.string.key_company), null);
		System.out.println("COMPANY: " + company);
		return company;
	}

	public String getSoapAction() {
		return getNamespace() + "/:" + getWebMethodName();
	}

	/**
	 * Web service method name.
	 * @return
	 */
	public abstract String getWebMethodName();

	/**
	 * Here we are creating request by inserting each property in list.
	 * @return
	 */
	public abstract List<PropertyInfo> getSOAPRequestProperties();

	/**
	 * Tag represent name of class, important because this is tag that is inserted in sync log.
	 * @return
	 */
	public abstract String getTag();

	/**
	 * Action identifier string for for broadcasting result to caller.
	 * @return
	 */
	public abstract String getBroadcastAction();

	/**
	 * Method that handles response with one parameter.
	 * 
	 * @param contentResolver so we can work with db
	 * @param soapResponse web service response with one parameter
	 * @return
	 * @throws CSVParseException
	 */
	protected abstract int parseAndSave(ContentResolver contentResolver, SoapPrimitive soapResponse) throws CSVParseException;

	/**
	 * Method that handles response with multiple parameters.
	 * 
	 * @param contentResolver so we can work with db
	 * @param soapResponse web service response with multiple parameters
	 * @return number of changed record in db, usually not correct info
	 * @throws CSVParseException
	 */
	protected abstract int parseAndSave(ContentResolver contentResolver, SoapObject soapResponse) throws CSVParseException;
	
	/**
	 * Initial soap parser. It handles faults an separates response with one and multiple parameters.
	 * After this we are using concrete parser per class in form pasreAndSave method. 
	 * @param response
	 * @param contentResolver
	 * @throws SOAPResponseException
	 */
	public void saveSOAPResponse(SoapSerializationEnvelope response, ContentResolver contentResolver) throws SOAPResponseException {
		int inserted = 0;
//		SoapObject resp = (SoapObject) response;
//		int l = resp.getPropertyCount();
		Object resp = null;
		try {
			resp = response.getResponse();
		} catch (SoapFault esf) {
			throw new SOAPResponseException(esf.faultstring);
		}
		if (resp instanceof SoapPrimitive) {
			SoapPrimitive soapresult = (SoapPrimitive) resp;
			try {
				inserted = parseAndSave(contentResolver, soapresult);
			} catch (CSVParseException e) {
				throw new SOAPResponseException(e);
			}
			result = String.valueOf(inserted);
			LogUtils.LOGI(getTag(), "New Items inserted:" + inserted);
		} else if (resp instanceof Vector<?>) {
			SoapObject soapObject = (SoapObject) response.bodyIn;
			try {
				inserted = parseAndSave(contentResolver, soapObject);
			} catch (CSVParseException e) {
				throw new SOAPResponseException(e);
			}
			result = String.valueOf(inserted);
			LogUtils.LOGI(getTag(), "New Items inserted:" + inserted);
		} else if (resp instanceof SoapFault) {
			SoapFault result = (SoapFault) resp;
			LogUtils.LOGE(getTag(), result.faultstring);
			throw new SOAPResponseException(result.getMessage());
		} else if (resp instanceof SoapFault12) {
			SoapFault12 result = (SoapFault12) resp;
			LogUtils.LOGE(getTag(), result.faultstring);
			throw new SOAPResponseException(result.getMessage());
		}

	}

	/**
	 * Logs web service call start.
	 * @param contentResolver
	 */
	public void logSyncStart(ContentResolver contentResolver) {
		Integer maxBatch = Integer.valueOf(0);
		Cursor cursor = contentResolver.query(SyncLogs.buildSyncLogsObjectIdUri(getTag()), new String[] { "MAX(" + SyncLogs.SYNC_OBJECT_BATCH + ")" }, null, null, null);
		if (cursor.moveToFirst()) {
			maxBatch = cursor.getInt(0);
		}
		maxBatch = maxBatch + 1;
		ContentValues values = new ContentValues();
		values.put(SyncLogs.SYNC_OBJECT_ID, getTag());
		// this is UI session id, shpud be in id field ut id is used in app and name is not
		// so now we are using name for UI id
		if (getSessionId() == null) {
			values.putNull(SyncLogs.SYNC_OBJECT_NAME);
		} else {		
			values.put(SyncLogs.SYNC_OBJECT_NAME, getSessionId());
		}
		values.put(SyncLogs.SYNC_OBJECT_STATUS, ApplicationConstants.SyncStatus.IN_PROCCESS.toString());
		values.put(SyncLogs.SYNC_OBJECT_BATCH, maxBatch);
		currentUri = contentResolver.insert(SyncLogs.CONTENT_URI, values);
	}

	public Date getLastSuccessSyncDate(ContentResolver contentResolver) {
		Date maxDate = null;
		Cursor cursor = contentResolver.query(MobileStoreContract.SyncLogs.CONTENT_URI, new String[] { "MAX(DATE(" + SyncLogs.CREATED_DATE + "))" }, SyncLogs.SYNC_OBJECT_ID+"=? AND "+SyncLogs.SYNC_OBJECT_STATUS+"="+SyncStatus.SUCCESS.toString(), new String[] { getTag() }, null);
		if (cursor.moveToFirst()) {
			maxDate = DateUtils.getLocalDbDate(cursor.getString(0));
		} else {
			maxDate = DateUtils.getWsDummyDate();
		}
		return maxDate;
	}
	
	/**
	 * Logs web service call end.
	 * @param contentResolver
	 * @param syncStatus
	 */
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

	public boolean isLastSyncDateNeeded() {
		return lastSyncDateNeeded;
	}

	public void setLastSyncDateNeeded(boolean lastSyncDateNeeded) {
		this.lastSyncDateNeeded = lastSyncDateNeeded;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
