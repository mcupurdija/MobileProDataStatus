package rs.gopro.mobile_store.ws.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.entity.ByteArrayEntity;
import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.Transport;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Extension of ksoap lib. It combines apache http protocol with ksoap serialization and NTLM authentication.
 * 
 * Code is allmost exact as in ksoap, but transport protocol implementation is changed to apache lib.
 * 
 * Only do POST requests!!!
 * 
 * @author vladimirm
 *
 */
public class HttpTransportApache extends Transport {

	private ServiceConnectionApache serviceConnection;
	private NTCredentials creds;
	private AuthScope authScope;
	
	
	public HttpTransportApache(String url, int timeout) {
		super(url, timeout);
	}
	
	public HttpTransportApache(String url, int timeout, NTCredentials creds, AuthScope authScope) {
		super(url, timeout);
		this.creds = creds;
		this.authScope = authScope;
	}
	
	public void call(String soapAction, SoapEnvelope envelope) throws IOException, XmlPullParserException {    
        call(soapAction, envelope, null);
    }
	
	@Override
	@SuppressWarnings({ "rawtypes" })
	public List call(String soapAction, SoapEnvelope envelope, List headers)
			throws IOException, XmlPullParserException {
		if (soapAction == null) {
            soapAction = "\"\"";
        }

        byte[] requestData = createRequestData(envelope);
		
        ByteArrayEntity httpEntity = new ByteArrayEntity(requestData);
        
        requestDump = debug ? new String(requestData) : null;
        responseDump = null;
            
        serviceConnection = getServiceConnection();
            
        serviceConnection.setRequestProperty("User-Agent", USER_AGENT);
        // SOAPAction is not a valid header for VER12 so do not add
        // it
        // @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
        if (envelope.version != SoapSerializationEnvelope.VER12) {
        	serviceConnection.setRequestProperty("SOAPAction", soapAction);
        }
        
        if (envelope.version == SoapSerializationEnvelope.VER12) {
        	serviceConnection.setRequestProperty("Content-Type", CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8);
        } else {
        	serviceConnection.setRequestProperty("Content-Type", CONTENT_TYPE_XML_CHARSET_UTF_8);
        }

        serviceConnection.setRequestProperty("Connection", "Keep-Alive");
        serviceConnection.setRequestProperty("Accept-Encoding", "gzip");
        //protocol sets this up
        //serviceConnection.setRequestProperty("Content-Length", "" + requestData.length);
        //no implementation
        //connection.setFixedLengthStreamingMode(requestData.length);
        
        // Pass the headers provided by the user along with the call
        if (headers != null) {
            for (int i = 0; i < headers.size(); i++) {
                HeaderProperty hp = (HeaderProperty) headers.get(i);
                serviceConnection.setRequestProperty(hp.getKey(), hp.getValue());
            }
        }
        
        // Already set in connection constructor, not an option always 
        // connection.setRequestMethod("POST");
        serviceConnection.setRequestEntity(httpEntity);
        //this sends request
        serviceConnection.connect();
        //prepare for response
        requestData = null;
        InputStream is = null;
        List<HeaderProperty> retHeaders = null;
        
        try {
            retHeaders = serviceConnection.getResponseProperties();
            boolean gZippedContent = false;
            for (int i = 0; i < retHeaders.size(); i++) {
                HeaderProperty hp = (HeaderProperty)retHeaders.get(i);
                // HTTP response code has null key
                if (null == hp.getKey()) {
                    continue;
                }
                // ignoring case since users found that all smaller case is used on some server
                // and even if it is wrong according to spec, we rather have it work..
                if (hp.getKey().equalsIgnoreCase("Content-Encoding")
                     && hp.getValue().equalsIgnoreCase("gzip")) {
                    gZippedContent = true;
                    break;
                }
            }
            if (gZippedContent) {
                /* workaround for Android 2.3 
                   (see http://stackoverflow.com/questions/5131016/)
                */
                InputStream origStream = serviceConnection.openInputStream();
                try {
                    is = (GZIPInputStream) origStream;
                } catch (ClassCastException e) {
                    is = new GZIPInputStream(origStream);
                }
            } else {
                is = serviceConnection.openInputStream();
            }
        } catch (IOException e) {
        	// don't understand this line!!!, if error stream exist it is never used
        	// because of that line is commented 
            //is = serviceConnection.getErrorStream();
            if (is == null) {
            	serviceConnection.disconnect();
                throw (e);
            }
            is.close();
        }
        
        if (debug) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[256];
                    
            while (true) {
                int rd = is.read(buf, 0, 256);
                if (rd == -1) {
                    break;
                }
                bos.write(buf, 0, rd);
            }
                    
            bos.flush();
            buf = bos.toByteArray();
            responseDump = new String(buf);
            is.close();
            is = new ByteArrayInputStream(buf);
        }
      
        parseResponse(envelope, is);
        return retHeaders;
	}

	@Override
	public String getHost() {
		String retVal = null;
        
        try {
            retVal = new URL(url).getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        return retVal;
	}

	@Override
	public String getPath() {
		String retVal = null;
        
        try {
            retVal = new URL(url).getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        return retVal;
	}

	@Override
	public int getPort() {
		int retVal = -1;
        
        try {
            retVal = new URL(url).getPort();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        
        return retVal;
	}

	@Override
	public ServiceConnectionApache getServiceConnection() throws IOException {
		if (serviceConnection == null) {
			serviceConnection = new ServiceConnectionApache(url, timeout);
			if (creds != null && authScope != null) {
				serviceConnection.setUpNtlmAuth(creds, authScope);
			}
		}
		return serviceConnection;
	}

}
