package hig.imt3672.knowthisroom;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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

	public void postToPage(final PlusClient client, final String room, final List<String> BSSID) {
		
		Thread workload = new Thread() {
			public void run() {
				
				/*
				 * Thanks a lot to the programmer that shared this code:
				 * http://www.androidsnippets.com/executing-a-http-post-request-with-httpclient
				 */
		
				// Get the link we will post data to
				jsonPage = prefs.getString("text_weblink", "");

				// Do some logging for testing purposes
				Log.d("#JsonPoster#room", room );
				Log.d("#JsonPoster#jsonPage", jsonPage );
				for( String id : BSSID ) {
					Log.d("#JsonPoster#bssid", id);
				}
				
				// If there is no webpage set, we return
				if ( jsonPage.isEmpty() ) {
					Log.d("PostToPage", "Jsonpage not set");
					return;
				}
				
				try {

					// The Json object that we are posting
					JSONObject jayson = new JSONObject();
					
					// Get the current time
					Calendar time = Calendar.getInstance();
					//SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
					DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
					String date = dateFormat.format( time.getTime() );
					
					// Fill the Json with data
					jayson.put("user", client.getAccountName());
					jayson.put("room", room);
					jayson.put("time", date);
					
					// Add the bssid's to the jsonobject
					JSONArray bssids = new JSONArray();
					for (int i = 0; i < BSSID.size() && i < 5; i++) {
						bssids.put( BSSID.get(i) );
					}
					jayson.put("BSSID", bssids);
					
					// The data to post
					String jsonString = jayson.toString(4);
				
					try {
						// Instantiate a new client
						HttpClient webClient = new DefaultHttpClient();
						
						// Url to post to
						String postURL = jsonPage;
						
						// Create a post-instance pointing to the url
						HttpPost post = new HttpPost(postURL);
						
						// Create a list of encoded data
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("data", jsonString));
						UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
						
						// Attach the data to the HttpPost-instance
						post.setEntity(ent);
						
						// Execute the call
						HttpResponse responsePOST = webClient.execute(post);
						
						// Get the respons
						HttpEntity resEntity = responsePOST.getEntity();
						
						// If we got a response, log this
						if (resEntity != null) {    
						    Log.i("RESPONSE",EntityUtils.toString(resEntity));
						}
					
					// catch exceptions
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
					
				// catch more exceptions
				} catch (JSONException e1) {
					e1.printStackTrace();
				} finally {
					
				}
				
			} // end of run()
			
		}; // end of thread definition
		
		workload.start();
		
	} // end of postToPage()
}
