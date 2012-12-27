package rs.gopro.mobile_store.ws;

import java.util.Date;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalDate;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import rs.gopro.mobile_store.util.LogUtils;
import rs.gopro.mobile_store.util.UIUtils;
import rs.gopro.mobile_store.ws.mappings.ItemsRequest;
import rs.gopro.mobile_store.ws.util.HttpTransportApache;
import rs.gopro.mobile_store.ws.util.MarshaleDateNav;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
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
	
	public static final String EXTRA_HTTP_VERB       = "rs.gopro.mobile_store.extra.EXTRA_HTTP_VERB";
    public static final String EXTRA_WS_PARAMS       = "rs.gopro.mobile_store.extra.EXTRA_WS_PARAMS";
    public static final String EXTRA_RESULT_RECEIVER = "rs.gopro.mobile_store.extra.EXTRA_RESULT_RECEIVER";

	private static String EXTRA_WS_NAMESPACE 	= "urn:microsoft-dynamics-schemas/codeunit/MobileDeviceSync";//"rs.gopro.mobile_store.extra.EXTRA_WS_NAMESPACE"; // "urn:microsoft-dynamics-schemas/nav/system/";
	private static String EXTRA_WS_METHOD_NAME 	= "GetItems";//"rs.gopro.mobile_store.extra.EXTRA_WS_METHOD_NAME"; // "Companies";
	private static String EXTRA_WS_SOAP_ACTION 	= "urn:microsoft-dynamics-schemas/codeunit/MobileDeviceSync/:GetItems";//"rs.gopro.mobile_store.extra.EXTRA_WS_SOAP_ACTION"; // "urn:microsoft-dynamics-schemas/nav/system/:Companies";
	private static String EXTRA_WS_URL 			= "http://sqlserver.gopro.rs:7047/Wurth/WS/Wurth%20-%20Development/Codeunit/MobileDeviceSync";//"rs.gopro.mobile_store.extra.EXTRA_WS_URL"; // "http://192.168.1.100:7047/DynamicsNAV/WS/SystemService";
    
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
        Uri    action = intent.getData();
        Bundle extras = intent.getExtras();
        
//        if (extras == null || action == null || !extras.containsKey(EXTRA_RESULT_RECEIVER)) {
//            // Extras contain our ResultReceiver and data is our REST action.  
//            // So, without these components we can't do anything useful.
//        	LogUtils.LOGE(TAG, "You did not pass extras or data with the Intent.");
//            
//            return;
//        }
		
        // We default to GET if no verb was specified.
        String 		   url 		= extras.getString(EXTRA_WS_URL);
        Bundle         params   = extras.getParcelable(EXTRA_WS_PARAMS);
        ResultReceiver receiver = extras.getParcelable(EXTRA_RESULT_RECEIVER);
        
//        ItemsRequest itemsRequest = new ItemsRequest();
//        
//        itemsRequest.setpDateModified();
//        itemsRequest.setpCampaignStatus(Integer.valueOf(-1));
        
        PropertyInfo pi0 = new PropertyInfo();
        pi0.setName("pCSVString");
        pi0.setValue(null);
        pi0.setType(String.class);
        
        PropertyInfo pi01 = new PropertyInfo();
        pi01.setName("pItemNoa46");
        pi01.setValue(null);
        pi01.setType(String.class);
        
        PropertyInfo pi = new PropertyInfo();
        pi.setName("pDateModified");
        pi.setValue(UIUtils.getDate("2010-01-01"));
        pi.setType(Date.class);
        
        PropertyInfo pi1 = new PropertyInfo();
        pi1.setName("pOverstockAndCampaignOnly");
        pi1.setValue(Integer.valueOf(-1));
        pi1.setType(Integer.class);
        
        SoapObject request = new SoapObject(EXTRA_WS_NAMESPACE, EXTRA_WS_METHOD_NAME);
        request.addProperty(pi0);
        request.addProperty(pi01);
        request.addProperty(pi);
        request.addProperty(pi1);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		new MarshaleDateNav().register(envelope);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportApache androidHttpTransport = new HttpTransportApache(EXTRA_WS_URL, 5000, new NTCredentials("wurthtest", "remote", "", "gopro"), new AuthScope(null, -1));
        
        androidHttpTransport.debug = true;
        try {            
            // Here we define our base request object which we will
            // send to our REST service via HttpClient.
        	androidHttpTransport.call(EXTRA_WS_SOAP_ACTION, envelope);
        	//androidHttpTransport.call()
        	SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
        	//itemsRequest.setpCSVString();
        	Bundle resultData = new Bundle();
        	resultData.putString(SOAP_RESULT, result.toString()); //result.getProperty(1).toString()
            receiver.send(1, resultData);
        } catch (Exception e) {
        	LogUtils.LOGE(TAG, "Error during soap request!", e);
        }
        
	}

}
