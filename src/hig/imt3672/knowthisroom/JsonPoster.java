package hig.imt3672.knowthisroom;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;

import com.google.android.gms.plus.PlusClient;

public class JsonPoster {
	private String jsonPage;

	public JsonPoster() {
		// TODO Auto-generated constructor stub
	}

	public String getJsonPage() {
		return jsonPage;
	}

	public void setJsonPage(String jsonPage) {
		this.jsonPage = jsonPage;
	}

	public void postToPage(PlusClient client, string room, List<String> BSSID) {
		// If there is no webpage set, we return
		if (jsonPage.isEmpty())
			return;

		try {
			// URI for page
			URI page = new URI(jsonPage);
			// Default HTTPClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			// Url with the POST data
			HttpPost httpPostRequest = new HttpPost(page);
			// The Json object that we are posting
			JSONObject jayson = new JSONObject();
			// Time to get the current time
			Calendar time = Calendar.getInstance();
			// StringEntity to hold all the json data as a string
			StringEntity se;

			// Fill the Json with data
			jayson.put("user", client.getAccountName());
			jayson.put("room", room);
			jayson.put("time", time.get(Calendar.SECOND));
			for (int i = 0; i < BSSID.size() && i < 5; i++) {
				jayson.put("BSSID", BSSID.get(i));
			}
			// Put the Json as a string
			se = new StringEntity(jayson.toString());
			// Put it into the request
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-Type", "application/json");
			// Execute the post and get a response
			HttpResponse response = httpClient.execute(httpPostRequest);
			HttpEntity httpEntity = response.getEntity();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
