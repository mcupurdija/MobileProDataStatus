package rs.gopro.mobile_store.ws.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.ksoap2.HeaderProperty;
import org.ksoap2.transport.ServiceConnection;

/**
 * Connection implementation using Apache lib.
 * 
 * Only do POST requests!!!
 * @author vladimirm
 *
 */
public class ServiceConnectionApache implements ServiceConnection  {
	
	private HttpClient httpclient;
	private HttpPost httppost;
	private HttpResponse httpresponse;
	private int timeout;
	
	public ServiceConnectionApache(String url, int timeout) {
		httpclient = new DefaultHttpClient();
		this.timeout = timeout;
		httppost = new HttpPost(url);
        httppost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
	}
	
	public void setUpNtlmAuth(NTCredentials creds, AuthScope authScope) {
		((AbstractHttpClient) httpclient).getAuthSchemes().register(AuthPolicy.NTLM, new NTLMSchemeFactory());
        ((AbstractHttpClient) httpclient).getCredentialsProvider().setCredentials(authScope, creds);
	}
	
	@Override
	public void connect() throws IOException {
		HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), timeout);
		httpresponse = httpclient.execute(httppost);
	}

	@Override
	public void disconnect() throws IOException {
		httpclient.getConnectionManager().shutdown();
	}

	@Override
	public InputStream getErrorStream() {
		HttpEntity respEentity = httpresponse.getEntity();
		try {
			return respEentity.getContent();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getHost() {
		return httppost.getURI().getHost();
	}

	@Override
	public String getPath() {
		return httppost.getURI().getPath();
	}

	@Override
	public int getPort() {
		return httppost.getURI().getPort();
	}

	@Override
	public List<HeaderProperty> getResponseProperties() throws IOException {
		Header[] headers = httpresponse.getAllHeaders();
        List<HeaderProperty> retList = new LinkedList<HeaderProperty>();

		for (int i = 0; i<headers.length; i++) {
		    String key = headers[i].getName();
		    String value = headers[i].getValue();
		
	        retList.add(new HeaderProperty(key, value));
		}

	    return retList;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		if (httpresponse == null) {
			throw new IOException("HTTPResponse not initialized!");
		}
		HttpEntity respEentity = httpresponse.getEntity();
		return respEentity.getContent();
	}

	/**
	 * Not implemented.
	 */
	@Override
	public OutputStream openOutputStream() throws IOException {
		throw new IOException("Not implemented for Apache HttpClient!");
	}

	
	public void setRequestEntity(HttpEntity entity) {
		httppost.setEntity(entity);
	}
	
	/**
	 * POST or GET, always post in this implementation
	 */
	@Override
	public void setRequestMethod(String arg0) throws IOException {
		// POST or GET, always post in this implementation
		//httppost.addHeader("SOAPAction", arg0);
	}

	@Override
	public void setRequestProperty(String key, String value) throws IOException {
		httppost.addHeader(key, value);
	}

	/**
	 * Set using headers, not here.
	 */
	@Override
	public void setFixedLengthStreamingMode(int contentLength) {
		
	}
}
