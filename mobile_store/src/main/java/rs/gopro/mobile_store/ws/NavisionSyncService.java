package rs.gopro.mobile_store.ws;

import java.util.Date;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import rs.gopro.mobile_store.licensing.LicenseData;
import rs.gopro.mobile_store.provider.MobileStoreContract.Licensing;
import rs.gopro.mobile_store.provider.Tables;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.exceptions.SOAPResponseException;
import rs.gopro.mobile_store.ws.model.SyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import rs.gopro.mobile_store.ws.util.HttpTransportApache;
import rs.gopro.mobile_store.ws.util.MarshaleDateNav;
import rs.gopro.mobile_store.ws.util.MarshaleDouble;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Background worker that calls SOAP web services from Navision 2009. It has
 * separate thread than UI. Results are stored in database.
 * 
 * @author vladimirm
 * 
 */
public class NavisionSyncService extends IntentService {

	private static final String TAG = LogUtils.makeLogTag(NavisionSyncService.class);

	public static final String EXTRA_WS_SYNC_OBJECT = "rs.gopro.mobile_store.EXTRA_WS_SYNC_OBJECT";
	public static final String EXTRA_WS_ALLOW_SYNC_OBJECT = "rs.gopro.mobile_store.ALLOW_SYNC";
	public static final String SYNC_RESULT = "rs.gopro.mobile_store.sync_result";
	
	// DS PROD		"tabletnav" "goprotablet" "DS"
	// LOCAL	    "wurthtest"	"remote" "gopro"
	public static final String USER_NAME = "tabletnav";
	public static final String PASS = "goprotablet";
	public static final String DOMAIN = "DS";
	
	public NavisionSyncService() {
		super(TAG);
	}

	public NavisionSyncService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// When an intent is received by this Service, this method
		// is called on a new thread.
		// Uri action = intent.getData();
		Bundle extras = intent.getExtras();
		
		boolean allowSync = extras.getBoolean(EXTRA_WS_ALLOW_SYNC_OBJECT, false);
		boolean licenseValid = false;
		String licenseNo = null;
		Cursor cursor = getContentResolver().query(Licensing.CONTENT_URI, new String[] { Licensing.LICENSE_NO }, Tables.LICENSING + "." + Licensing._ID + "=?", new String[] { "1" }, null);
		if (cursor.moveToFirst()) {
			licenseNo = cursor.getString(0);
		}
		cursor.close();
		
		if (licenseNo != null && licenseNo.length() > 0 && licenseNo.equals(LicenseData.getCurrentSerial())) {
			licenseValid = true;
		}
		
		// We default to GET if no verb was specified.
		SyncObject syncObject = extras.getParcelable(EXTRA_WS_SYNC_OBJECT);
		syncObject.setContext(getApplicationContext());
		
		SyncResult syncResult = new SyncResult();
		
		if (licenseValid || allowSync) {

			SoapObject request = new SoapObject(syncObject.getNamespace(), syncObject.getWebMethodName());

			if (syncObject.isLastSyncDateNeeded()) {
				Date lastSuccessSyncDate = syncObject.getLastSuccessSyncDate(getContentResolver());
				// TODO pass date to whoever is in needs
			}
			
			for (PropertyInfo wsProperty : syncObject.getSOAPRequestProperties()) {
				request.addProperty(wsProperty);
			}

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			new MarshaleDateNav().register(envelope);
			new MarshaleDouble().register(envelope);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);
			HttpTransportApache androidHttpTransport = new HttpTransportApache(syncObject.getUrl(), 15000, new NTCredentials(USER_NAME, PASS, "", DOMAIN), new AuthScope(null, -1));
			ContentResolver contentResolver = getContentResolver();
			androidHttpTransport.debug = false;
			try {
				syncObject.logSyncStart(contentResolver);
				// Here we define our base request object which we will
				// send to our REST service via HttpClient.
				androidHttpTransport.call(syncObject.getSoapAction(), envelope);
				syncObject.saveSOAPResponse(envelope, contentResolver);
				syncResult.setStatus(SyncStatus.SUCCESS);
				syncResult.setResult(syncObject.getResult());
				syncResult.setComplexResult(syncObject);
				syncObject.logSyncEnd(contentResolver, SyncStatus.SUCCESS);
			} catch (SOAPResponseException e) {
				syncResult.setStatus(SyncStatus.FAILURE);
				syncResult.setResult(e.getMessage());
				syncObject.logSyncEnd(contentResolver, SyncStatus.FAILURE);
				LogUtils.LOGE(TAG, "Soap request error!", e);
			} catch (Exception e) {
				syncResult.setStatus(SyncStatus.FAILURE);
				syncResult.setResult(e.getMessage());
				syncObject.logSyncEnd(contentResolver, SyncStatus.FAILURE);
				LogUtils.LOGE(TAG, "Error during soap request!", e);
			}

			Intent resultIntent = new Intent(syncObject.getBroadcastAction());
			resultIntent.putExtra(SYNC_RESULT, syncResult);

		} else {
			syncResult.setStatus(SyncStatus.FAILURE);
			syncResult.setResult("VAŠA LICENCA NIJE VALIDNA.\nMOLIMO KONTAKTIRAJTE IT PODRŠKU.");
		}
		
		Intent resultIntent = new Intent(syncObject.getBroadcastAction());
		resultIntent.putExtra(SYNC_RESULT, syncResult);
		
		LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

	}

}
