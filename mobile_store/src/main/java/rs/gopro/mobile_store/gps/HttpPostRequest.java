package rs.gopro.mobile_store.gps;

import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpPostRequest {

	public static void postData(String jsonEntity) throws Exception {

		HttpClient httpClient = new DefaultHttpClient();

		try {
			URL url = new URL("http://192.168.1.159/api/gps");
			HttpPost httpPost = new HttpPost(url.toURI());
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setEntity(new StringEntity(jsonEntity, "UTF-8"));
			
			HttpResponse result = httpClient.execute(httpPost);
			System.out.println("GpsData POST result: " + result.getStatusLine().getStatusCode());

		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}

	}

}