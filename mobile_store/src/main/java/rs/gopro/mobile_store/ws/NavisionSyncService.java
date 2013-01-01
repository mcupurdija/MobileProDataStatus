package rs.gopro.mobile_store.ws;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.ws.model.SyncObject;
import rs.gopro.mobile_store.ws.util.HttpTransportApache;
import rs.gopro.mobile_store.ws.util.MarshaleDateNav;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * Background worker that calls SOAP web services from Navision 2009. It has separate thread than UI.
 * Results are stored in database.
 * @author vladimirm
 *
 */
public class NavisionSyncService extends IntentService {

	private static final String TAG = LogUtils.makeLogTag(NavisionSyncService.class);
	
    public static final String EXTRA_WS_SYNC_OBJECT      = "rs.gopro.mobile_store.EXTRA_WS_SYNC_OBJECT";
    public static final String EXTRA_RESULT_RECEIVER = "rs.gopro.mobile_store.EXTRA_RESULT_RECEIVER";

    public static final String SOAP_RESULT = "rs.gopro.mobile_store.extra.SOAP_RESULT";

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
        //Uri    action = intent.getData();
        Bundle extras = intent.getExtras();
        
        if (extras == null || !extras.containsKey(EXTRA_RESULT_RECEIVER)) { //|| action == null
            // Extras contain our ResultReceiver and data is our REST action.  
            // So, without these components we can't do anything useful.
        	LogUtils.LOGE(TAG, "You did not pass extras or data with the Intent.");
            
            return;
        }
		
        // We default to GET if no verb was specified.
        SyncObject     syncObject   = extras.getParcelable(EXTRA_WS_SYNC_OBJECT);
        ResultReceiver receiver = extras.getParcelable(EXTRA_RESULT_RECEIVER);
        
        SoapObject request = new SoapObject(syncObject.getNamespace(), syncObject.getWebMethodName());
        
        for (PropertyInfo wsProperty : syncObject.getSOAPRequestProperties()) {
        	 request.addProperty(wsProperty);
        }

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		new MarshaleDateNav().register(envelope);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportApache androidHttpTransport = new HttpTransportApache(syncObject.getUrl(), 5000, new NTCredentials("wurthtest", "remote", "", "gopro"), new AuthScope(null, -1));
        ContentResolver contentResolver = getContentResolver();
        androidHttpTransport.debug = true;
        try {            
        	syncObject.logSyncStart(contentResolver);
            // Here we define our base request object which we will
            // send to our REST service via HttpClient.
        	androidHttpTransport.call(syncObject.getSoapAction(), envelope);
        	syncObject.saveSOAPResponse(envelope.getResponse(), contentResolver);
        	//SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
        	//itemsRequest.setpCSVString();
        	Bundle resultData = new Bundle();
        	resultData.putString(SOAP_RESULT, syncObject.getResult()); //result.getProperty(1).toString()
            receiver.send(1, resultData);
        	syncObject.logSyncEnd(contentResolver);
        } catch (Exception e) {
        	syncObject.logSyncEnd(contentResolver);
        	LogUtils.LOGE(TAG, "Error during soap request!", e);
        }
        
	}

}
