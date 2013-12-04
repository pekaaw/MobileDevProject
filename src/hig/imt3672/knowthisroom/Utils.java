package hig.imt3672.knowthisroom;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Utils {

	/**
	 * Start a Wifi-scan.
	 * <p>
	 * The network card will start to create a list of visible networks.
	 * These will be published when the list is ready.
	 * @author PK
	 */
	static void startWifiScan() {
		try{
			WifiSensor.getInstance().startScan();
		}
		catch( NullPointerException e ) {
			Log.d("#Utils#", "startWifiScan says: " + e.getMessage() );
		}
	}
	
	/**
	 * Check to see if settings tells us to post to a web-server.
	 * <p>
	 * We use the context from the Service, since this will always run.
	 * @return Boolean: True if we should post to URL, else false.
	 * @author PK
	 */
	static Boolean isPostingToURL() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( ServiceHandler.getInstance() );
		Boolean isPostingToUrl = prefs.getBoolean("checkbox_weblink", false);
		
		return isPostingToUrl;
	}
	
	/**
	 * Get the URL that we should post json-data to (from SharedPreferences)
	 * @return String of URL
	 * @exception NullPointerException if not set
	 */
	static String getTargetServerURL() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( ServiceHandler.getInstance() );
		String targetServerURL = prefs.getString( "text_weblink" , null );
		
		return targetServerURL;
	}
	
	/**
	 * Check to see if settings tells us to post to G+ moments
	 * <p>
	 * We use the context from the Serice, since this will always run.
	 * @return Boolean: True if we should post to moments, else false.
	 * @author PK
	 */
	static Boolean isPostingToGplusMoment() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( ServiceHandler.getInstance() );
		Boolean isPostingToGplusMoment = prefs.getBoolean("checkbox_gplus_moments", false );
		
		return isPostingToGplusMoment;
	}
	
	/**
	 * Check to see if settings tells us to post to G+ stream
	 * <p>
	 * We use the context from the Serice, since this will always run.
	 * @return Boolean: True if we should post to stream, else false.
	 * @author PK
	 */
	static Boolean isPostingToGplusStream() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( ServiceHandler.getInstance() );
		Boolean isPostingToGplusStream = prefs.getBoolean( "checkbox_gplus_stream", false );
		
		return isPostingToGplusStream;
	}
}
