package rs.gopro.mobile_store.ws.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	    return call(soapAction, envelope, headers, null);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes" })
	public List call(String soapAction, SoapEnvelope envelope, List headers, File outputFile)
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
        int contentLength = 8192;
        boolean gZippedContent = false;
        
        try {
        	//first check the response code....
            int status = serviceConnection.getResponseCode();
            if(status != 200 && status != 500) {
                throw new IOException("HTTP zahtev neuspesan. HTTP status kod: " + status);
            }
            
            retHeaders = serviceConnection.getResponseProperties();
            for (int i = 0; i < retHeaders.size(); i++) {
                HeaderProperty hp = retHeaders.get(i);
                // HTTP response code has null key
                if (null == hp.getKey()) {
                    continue;
                }
                
             // If we know the size of the response, we should use the size to initiate vars
                if (hp.getKey().equalsIgnoreCase("content-length") ) {
                    if ( hp.getValue() != null ) {
                        try {
                            contentLength = Integer.parseInt( hp.getValue() );
                        } catch ( NumberFormatException nfe ) {
                            contentLength = 8192;
                        }
                    }
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
                is = getUnZippedInputStream (
                		new BufferedInputStream(serviceConnection.openInputStream(), contentLength));
            } else {
                is = new BufferedInputStream(serviceConnection.openInputStream(),contentLength);
            }
        } catch (IOException e) {
        	if(gZippedContent) {
                is = getUnZippedInputStream(
                        new BufferedInputStream(serviceConnection.getErrorStream(),contentLength));
            } else {
                is = new BufferedInputStream(serviceConnection.getErrorStream(),contentLength);
            }
        	
        	if (debug && is != null) {
                //go ahead and read the error stream into the debug buffers/file if needed.
                readDebug(is, contentLength, outputFile);
            }
        	//we never want to drop through to attempting to parse the HTTP error stream as a SOAP response.
        	//is.close();
        	serviceConnection.disconnect();
            throw e;
        }
        
        if (debug) {
        	is = readDebug(is, contentLength, outputFile);
        }
      
        parseResponse(envelope, is);
        // release all resources 
        // input stream is will be released inside parseResponse
        // Vladimir: we don't use this, apache has another mechanism
//        os = null;
//        buf = null;
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

    private InputStream readDebug(InputStream is, int contentLength, File outputFile) throws IOException {
        OutputStream bos;
        if (outputFile != null) {
            bos = new FileOutputStream(outputFile);
        } else {
            // If known use the size if not use default value
            bos = new ByteArrayOutputStream( (contentLength > 0 ) ? contentLength : 256*1024);
        }

        byte[] buf = new byte[256];

        while (true) {
            int rd = is.read(buf, 0, 256);
            if (rd == -1) {
                break;
            }
            bos.write(buf, 0, rd);
        }

        bos.flush();
        if (bos instanceof ByteArrayOutputStream) {
            buf = ((ByteArrayOutputStream) bos).toByteArray();
        }
        bos = null;
        responseDump = new String(buf);
        is.close();
        return new ByteArrayInputStream(buf);
    }
	
    private InputStream getUnZippedInputStream(InputStream inputStream) throws IOException {
        /* workaround for Android 2.3 
           (see http://stackoverflow.com/questions/5131016/)
        */
        try {
            return (GZIPInputStream) inputStream;
        } catch (ClassCastException e) {
            return new GZIPInputStream(inputStream);
        }
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
