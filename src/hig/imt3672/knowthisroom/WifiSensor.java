/*CREDITS:
 * The most basic implementation was aided along with the help of this:
 * http://stackoverflow.com/questions/5452940/
 * 
 * Ordering the entries in the list was done with help from this:
 * http://stackoverflow.com/questions/9109890/
 * 
 * Learning how to implement a service can be attributed to:
 * http://developer.android.com/guide/components/services.html
 * 
 * Thanks, stackoverflow.com
 */

package hig.imt3672.knowthisroom;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiSensor extends IntentService {
	
	WifiManager wifi;
	List<ScanResult> networks;
	
	public WifiSensor() {
		super("WifiSensorService");
		// TODO Auto-generated constructor stub
	}

	/*TIP:
	 * For info on ScanResult:
	 * http://developer.android.com/reference/android/net/wifi/ScanResult.html
	 */
	
	@Override
	public void onHandleIntent(Intent intent) {
		Log.d ("WifiSensor","Starting service.");
		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		
		Log.d ("WifiSensor","Filling list.");
		networks = wifi.getScanResults();

		Log.i ("WifiSensor","There are " + Integer.toString(networks.size()) + " networks.");
		
		//Sorts by the BSSID of the ScanResults.
		Collections.sort(networks, new Comparator<ScanResult>() {
			public int compare(ScanResult s1, ScanResult s2) {
				return s1.BSSID.compareToIgnoreCase(s2.BSSID);
			}
		});
		
		
	}
}
