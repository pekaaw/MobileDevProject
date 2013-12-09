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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.plus.PlusClient;

public class JsonPoster {
	private String jsonPage;
	private Context context;
	SharedPreferences prefs;

	public JsonPoster(Context context) {
		this.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public String getJsonPage() {
		return jsonPage;
	}

	public void setJsonPage(String jsonPage) {
		this.jsonPage = jsonPage;
	}

	public void postToPage(PlusClient client, String room, List<String> BSSID) {
		jsonPage = prefs.getString("text_weblink", "");
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
			Toast.makeText(context, httpEntity.toString(), Toast.LENGTH_LONG)
					.show();

		} catch (JSONException e) {
			Log.e("Json", "Error in creating Json object");
			e.printStackTrace();
		} catch (URISyntaxException e) {
			Log.e("URI", "Error in creating URI");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.e("ClientProtocol", "Someplace network");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
