package rs.gopro.mobile_store.ws;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import rs.gopro.mobile_store.R;
import rs.gopro.mobile_store.util.ApplicationConstants;
import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.ApplicationConstants.SyncStatus;
import rs.gopro.mobile_store.ws.model.ItemsSyncObject;
import rs.gopro.mobile_store.ws.model.SyncObject;
import rs.gopro.mobile_store.ws.model.SyncResult;
import rs.gopro.mobile_store.ws.util.HttpTransportApache;
import rs.gopro.mobile_store.ws.util.MarshaleDateNav;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

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
	public static final String SYNC_RESULT = "rs.gopro.mobile_store.sync_result";

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

		// We default to GET if no verb was specified.
		SyncObject syncObject = extras.getParcelable(EXTRA_WS_SYNC_OBJECT);
		syncObject.setContext(getApplicationContext());

		SoapObject request = new SoapObject(syncObject.getNamespace(), syncObject.getWebMethodName());

		for (PropertyInfo wsProperty : syncObject.getSOAPRequestProperties()) {
			request.addProperty(wsProperty);
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		new MarshaleDateNav().register(envelope);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);

		HttpTransportApache androidHttpTransport = new HttpTransportApache(syncObject.getUrl(), 5000, new NTCredentials("wurthtest", "remote", "", "gopro"), new AuthScope(null, -1));// "wurthtest",
																																														// "remote",
																																														// "",
																																														// "gopro"
		ContentResolver contentResolver = getContentResolver();
		androidHttpTransport.debug = true;
		SyncResult syncResult = new SyncResult();
		try {
			syncObject.logSyncStart(contentResolver);
			// Here we define our base request object which we will
			// send to our REST service via HttpClient.
			androidHttpTransport.call(syncObject.getSoapAction(), envelope);
			syncObject.saveSOAPResponse(envelope.getResponse(), contentResolver);
			syncResult.setStatus(SyncStatus.SUCCESSED);
			syncResult.setResult(syncObject.getResult());
			syncObject.logSyncEnd(contentResolver, SyncStatus.SUCCESSED);
		} catch (Exception e) {
			syncResult.setStatus(SyncStatus.FAILURE);
			syncResult.setResult(syncObject.getResult());
			syncObject.logSyncEnd(contentResolver, SyncStatus.FAILURE);
			LogUtils.LOGE(TAG, "Error during soap request!", e);
		}

		Intent resultIntent = new Intent(syncObject.getBroadcastAction());
		resultIntent.putExtra(SYNC_RESULT, syncResult);

		LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);

	}

}
