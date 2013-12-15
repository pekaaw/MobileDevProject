package hig.imt3672.knowthisroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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

	public void post__ToPage( final PlusClient client, final String room, final List<String> BSSID ) {
		Thread workload = new Thread() {
			
			public void run() {
				
				Log.d("#JsonPoster#run", "Starting a POST");
				
				try {
					
					// The Json object that we are posting
					JSONObject jayson = new JSONObject();
					// Time to get the current time
					Calendar time = Calendar.getInstance();
					
					// Fill the Json with data
					try{
						jayson.put("user", client.getAccountName());
					} catch( IllegalStateException e ) {
						e.printStackTrace();
					}
					jayson.put("room", room);
					jayson.put("time", time.get(Calendar.SECOND));
					
//					for( String bssid : BSSID ) {
//						jayson.put( "BSSID", bssid );
//					}
//					
//					for (int i = 0; i < BSSID.size() && i < 5; i++) {
//						jayson.put("BSSID", BSSID.get(i));
//					}

					
					Log.d("#JsonPoster#jsonString", jayson.toString() );
					Log.d("#JsonPoster#JsonStringSize",  Integer.toString(jayson.toString().length()));

					
					URL url = new URL( prefs.getString("text_weblink", "") );
					
					HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
					
					try {
						urlConnection.setDoOutput(true);
						urlConnection.setRequestProperty("Accept-Charset", "utf-8");
						urlConnection.setRequestProperty("Content-Type", "application/json");
						urlConnection.setRequestProperty("Content-Length", Integer.toString(jayson.toString().length()));
						urlConnection.setDoInput(true);
						urlConnection.setRequestMethod("POST");
						urlConnection.connect();
						

						// write the Json-Object to the outstream
						OutputStreamWriter out = new OutputStreamWriter( urlConnection.getOutputStream() );
						out.write( jayson.toString() );
						out.flush();
						out.close();  
						
						Log.d("#JsonPoster#", "Running request");
						InputStream inputStream = urlConnection.getInputStream();
						
						// if responsecode = HTTP_OK, write response to System.out
						int HttpResult = urlConnection.getResponseCode();
						if( HttpResult == HttpURLConnection.HTTP_OK ) {
							
							Log.d("#JsonPoster#", "Responsecode: HTTP_OK");
							// Buffered reader that reads inputStream from urlConnection
							BufferedReader br = new BufferedReader( 
								new InputStreamReader( inputStream, "utf-8" ));
							
							Log.d("#JsonPoster#response", "bufferReader created" );
							
							StringBuffer buffer = new StringBuffer("");
							String line = null;
							String NL = System.getProperty("line.separator");
							
							// For each line to read, add the line and a newline to the buffer
							while ((line = br.readLine()) != null) {  
								buffer.append(line + NL);  
							}
							
							Log.d("#JsonPoster#response", "while buffer done" );
							
							// close the buffer-reader
							br.close();  
							
							String result = buffer.toString();
							
							Log.d("#JsonPoster#response", "in: " + result + " : " + Integer.toString(result.length()) );
						
							// print results to System.out
							System.out.println( "" + buffer.toString() );  
						} else {
							// or print responscode to System.out
							System.out.println(urlConnection.getResponseMessage()); 
						}

						// catch exceptions and disconnect
					} finally {
						Log.d("#JsonPoster#finally", "urlConnection.disconnect()" );
						urlConnection.disconnect();
					}
					
				// Catch exceptions
				} catch (JSONException e) {
					e.printStackTrace();
					Log.d("#JsonPoster#JsonException", e.toString() );
				} catch (MalformedURLException e) {
					Log.d("#JsonPoster#MalformedURLException", e.toString() );
					e.printStackTrace();
				} catch (IOException e) {
					Log.d("#JsonPoster#IOException", e.toString() );
					e.printStackTrace();
				}
			}
		};
		workload.start();
	}
	
	public void postToPage(final PlusClient client, final String room, final List<String> BSSID) {
		
		Thread workload = new Thread() {
			public void run() {
		
				String data = "{ 'test': 'ting', 'ekstra': 'ok' }";
				jsonPage = prefs.getString("text_weblink", "");

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
					// Time to get the current time
					Calendar time = Calendar.getInstance();
		
					// Fill the Json with data
					jayson.put("user", client.getAccountName());
					jayson.put("room", room);
					jayson.put("time", time.get(Calendar.SECOND));
					
					for (int i = 0; i < BSSID.size() && i < 5; i++) {
						jayson.put("BSSID", BSSID.get(i));
					}
					
					// The data to post
					String jsonString = jayson.toString();
				
				   try {
				        HttpClient client = new DefaultHttpClient();  
				        String postURL = "http://pekaaw.net/postJson/index.php";
				        HttpPost post = new HttpPost(postURL);
				            List<NameValuePair> params = new ArrayList<NameValuePair>();
				            params.add(new BasicNameValuePair("data", jsonString));
				            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
				            post.setEntity(ent);
				            HttpResponse responsePOST = client.execute(post);  
				            HttpEntity resEntity = responsePOST.getEntity();  
				            if (resEntity != null) {    
				                Log.i("RESPONSE",EntityUtils.toString(resEntity));
				            }
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
				} catch (JSONException e1) {
					e1.printStackTrace();
				} finally {
					
				}
				
//				
//				BufferedReader reader = null;
//				String result = "";
//				
//				try {
//					URL url = new URL(prefs.getString("text_weblink", ""));
//					
//					URLConnection conn = url.openConnection();
//					conn.setDoOutput(true);
//					
//
//					OutputStreamWriter wr = new OutputStreamWriter( conn.getOutputStream() );
//					wr.write(data);
//					wr.flush();
//					
//					reader = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
//					StringBuilder builder = new StringBuilder();
//					String getLine = null;
//					
//					while( ( getLine = reader.readLine() ) != null ) {
//						builder.append( getLine + "\n");
//					}
//					
//					result = builder.toString();
//					
//					Log.d("#JsonPoster#", "response from server: " + result );
//					
//				} catch ( Exception ex ) {
//					
//				} finally {
//					try {
//						reader.close();
//					} catch( Exception ex) {
//						
//					}
//				}

				
				
				
				
				
				
				
				
				
//				// ##########################################################################################
//		
//				jsonPage = prefs.getString("text_weblink", "");
//				
//				Log.d("#JsonPoster#room", room );
//				Log.d("#JsonPoster#jsonPage", jsonPage );
//				for( String id : BSSID ) {
//					Log.d("#JsonPoster#bssid", id);
//				}
//				// If there is no webpage set, we return
//		//		if (jsonPage.isEmpty() || BSSID.isEmpty()) {
//				if ( jsonPage.isEmpty() ) {
//					Log.d("PostToPage", "Jsonpage not set");
//					return;
//				}
//				try {
//					// URI for page
//					URI page = new URI(jsonPage);
//					// Default HTTPClient
//					DefaultHttpClient httpClient = new DefaultHttpClient();
//					// Url with the POST data
//					HttpPost httpPostRequest = new HttpPost(page);
//					// The Json object that we are posting
//					JSONObject jayson = new JSONObject();
//					// Time to get the current time
//					Calendar time = Calendar.getInstance();
//					// StringEntity to hold all the json data as a string
//					StringEntity se;
//		
//					// Fill the Json with data
//					jayson.put("user", client.getAccountName());
//					jayson.put("room", room);
//					jayson.put("time", time.get(Calendar.SECOND));
//					
//		//			for( String bssid : BSSID ) {
//		//				jayson.put( "BSSID", bssid );
//		//			}
//					for (int i = 0; i < BSSID.size() && i < 5; i++) {
//						jayson.put("BSSID", BSSID.get(i));
//					}
//					// Put the Json as a string
//					se = new StringEntity(jayson.toString());
//					
//					String toServer = null;
//					
//					try {
//						
//						BufferedReader in = new BufferedReader( new InputStreamReader( se.getContent() ) );
//						
//						StringBuffer buffer = new StringBuffer("");
//						String line = "";
//						String NL = System.getProperty("line.separator");
//						
//						while( (line = in.readLine()) != null ) {
//							buffer.append(line + NL);
//						}
//						
//						in.close();
//						
//						toServer = buffer.toString();
//					}
//					finally{
//
//					}
//		
//					if( toServer != null ) {
//						Log.d("#JsonPoster#", "Sent to server: " + toServer );
//					}
//					
//					// Put it into the request
//					httpPostRequest.setEntity(se);
//					httpPostRequest.setHeader("Accept", "application/json");
//					httpPostRequest.setHeader("Content-Type", "application/json");
//					// Execute the post and get a response
//					HttpResponse response = httpClient.execute(httpPostRequest);
//					HttpEntity httpEntity = response.getEntity();
//					//new postToWebURI().execute(httpPostRequest);
//		
//					Log.d("#JsonPoster#", "Json sent for execution.");
//					Log.i("#JsonPoster#Jayson is:", httpEntity.toString());
//					
//					String result = null;
//					
//					try {
//					
//						BufferedReader in = new BufferedReader( new InputStreamReader( httpEntity.getContent() ) );
//						
//						StringBuffer buffer = new StringBuffer("");
//						String line = "";
//						String NL = System.getProperty("line.separator");
//						
//						while( (line = in.readLine()) != null ) {
//							buffer.append(line + NL);
//						}
//						
//						in.close();
//						
//						result = buffer.toString();
//					}
//					finally{
//
//					}
//					
//					if( result != null ) {
//						Log.d("#JsonPoster#", "Response from server: " + result );
//					}
//					
//		
//				} catch (JSONException e) {
//					Log.e("Json", "Error in creating Json object");
//					e.printStackTrace();
//				} catch (URISyntaxException e) {
//					Log.e("URI", "Error in creating URI");
//					e.printStackTrace();
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				} catch (IllegalStateException e) {
//					e.printStackTrace();
//				} catch (ClientProtocolException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
			}
		};
		
		workload.start();
		
	}
	
//	private class postToWebURI extends AsyncTask<HttpPost, Integer, HttpResponse> {
//		protected HttpResponse doInBackground(HttpPost... request) {
//			try {
//				return new DefaultHttpClient().execute(request[0]);
//			} catch (ClientProtocolException e) {
//				Log.e("ClientProtocol", "Someplace network");
//				e.printStackTrace();
//				return null;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return null;
//			}
//		}
//		
//		protected void onPostExecute( HttpResponse response ) {
//			HttpEntity entity = response.getEntity();
//			Long length = response.getEntity().getContentLength();
//			Log.d("#JsonPoster#postExecute", entity.toString() + " and length: " + Long.toString(length) );
//			
//			try {
//				new readStreamTask().execute( entity.getContent() );
//			} catch (IllegalStateException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			
//		}
//	}
//	
//	private class readStreamTask extends AsyncTask<InputStream, Integer, String> {
//		protected String doInBackground( InputStream... input ) {
//			String result = "";
//			
//			try {
//				
//				BufferedReader in = new BufferedReader( new InputStreamReader( input[0] ) );
//				
//				StringBuffer buffer = new StringBuffer("");
//				String line = "";
//				String NL = System.getProperty("line.separator");
//				
//				while( (line = in.readLine()) != null ) {
//					buffer.append(line + NL);
//				}
//				
//				in.close();
//				
//				result = buffer.toString();
//			
//			} catch (IllegalStateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			return result;
//		}
//
//		protected void onPostExecute( String result ) {
//			Log.d("#JsonPoster#result", result );
//		}
//	}
//	

}
