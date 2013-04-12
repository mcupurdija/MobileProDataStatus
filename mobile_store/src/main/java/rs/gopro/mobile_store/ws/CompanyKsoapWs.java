package rs.gopro.mobile_store.ws;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import rs.gopro.mobile_store.ws.util.HttpTransportApache;
import android.os.AsyncTask;

/**
 * Testing purpose only!
 * @author vladimirm
 *
 */
public class CompanyKsoapWs extends AsyncTask<Void, Void, Void>  {
	
	private static String NAMESPACE = "urn:microsoft-dynamics-schemas/nav/system/";
	private static String METHOD_NAME = "Companies";
	private static String SOAP_ACTION = "urn:microsoft-dynamics-schemas/nav/system/:Companies";
	private static String URL = "http://192.168.1.100:7047/DynamicsNAV/WS/SystemService";
	
	private String company;
	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompanyWS() {
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportApache androidHttpTransport = new HttpTransportApache(URL, 5000, new NTCredentials("mcevich", "plavanjkomp54", "", "mcevich-PC"), new AuthScope("192.168.1.100", -1));
        
        try {
        	androidHttpTransport.call(SOAP_ACTION, envelope);
        	//androidHttpTransport.call()
	        SoapPrimitive result = (SoapPrimitive)envelope.getResponse();
	        setCompany(result.toString());
        } catch(Exception e) {
        	e.printStackTrace();
        }
        
		return null;
	}

	@Override
	protected Void doInBackground(Void... params) {
		setCompany(getCompanyWS());
		return null;
	}
}
