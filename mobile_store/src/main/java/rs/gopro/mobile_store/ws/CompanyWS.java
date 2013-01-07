package rs.gopro.mobile_store.ws;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

import rs.gopro.mobile_store.ws.util.NTLMSchemeFactory;

/**
 * Testing purpose only!
 * @author vladimirm
 *
 */
public class CompanyWS extends AsyncTask<Void, Void, Void> {
	
	private String company;
	
	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCompanyWS() {
		HttpClient httpclient = new DefaultHttpClient();        
        ((AbstractHttpClient) httpclient).getAuthSchemes().register(AuthPolicy.NTLM, new NTLMSchemeFactory());

        NTCredentials creds = new NTCredentials("mcevich", "plavanjkomp54", "", "mcevich-PC");
        
        ((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials(new AuthScope("192.168.0.104", 7047), creds);
        HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 5000); 
        HttpPost httppost = new HttpPost("http://192.168.0.104:7047/DynamicsNAV/WS/SystemService");
        httppost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
        
        try {
            StringEntity se = new StringEntity( "<?xml version=\"1.0\" encoding=\"utf-8\"?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:sys=\"urn:microsoft-dynamics-schemas/nav/system/\"><soapenv:Header/><soapenv:Body><sys:Companies/></soapenv:Body></soapenv:Envelope>", HTTP.UTF_8);
            se.setContentType("text/xml");
            httppost.addHeader("SOAPAction", "urn:microsoft-dynamics-schemas/nav/system/:Companies");
            httppost.setEntity(se); 

            HttpResponse httpresponse = httpclient.execute(httppost);
            HttpEntity resEntity = httpresponse.getEntity();
            
            return "Status OK: \n" + EntityUtils.toString(resEntity);
        }catch (ClientProtocolException e) {
            e.printStackTrace();
            return "Error";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error";
        }
	}

	@Override
	protected Void doInBackground(Void... params) {
		setCompany(getCompanyWS());
		return null;
	}


}
