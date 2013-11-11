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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiSensor {
	Context mContext;
	WifiManager wifi;
	List<ScanResult> networks;
	static WifiSensor mInstance;
	
	public static WifiSensor getInstance() {
		if(mInstance == null) {
			mInstance = new WifiSensor();
		}
		return mInstance;
	}
	
	private WifiSensor() {
		mContext = null;
		networks = null;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	/*
	 * TIP: For info on ScanResult:
	 * http://developer.android.com/reference/android/net/wifi/ScanResult.html
	 */

	public List<ScanResult> GetNetworks() {
		if(mContext == null) {
			Log.d("WifiSensor", "No context provided. Returning.");
			return null;
		}
		
		Log.d("WifiSensor", "Registering listener.");
		mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) 
            {
            	networks = wifi.getScanResults();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		
		Log.d("WifiSensor","Networks: " + Integer.toString(networks.size()));
		if(networks.size() != 0) {
			// Sorts by the BSSID of the ScanResults.
			Collections.sort(networks, new Comparator<ScanResult>() {
				public int compare(ScanResult s1, ScanResult s2) {
					return s1.BSSID.compareToIgnoreCase(s2.BSSID);
				}
			});
	
			Log.i("WifiSensor", "Sorted " + Integer.toString(networks.size())
					+ " networks.");
		}
		return networks;
	}
}
