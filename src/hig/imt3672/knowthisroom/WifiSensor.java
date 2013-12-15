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
	static WifiSensor mInstance;

	Context mContext;
	WifiManager wifi;
	List<ScanResult> networks;
	int size;

	public static WifiSensor getInstance() {
		if (mInstance == null) {
			Log.d("WifiSensor", "You have not created an instance.");
			return null;
		}
		Log.d("WifiSensor", "Obtained instance.");
		return mInstance;
	}

	public synchronized static void createInstance(Context context) {
		if (mInstance != null) {
			Log.d("WifiSensor", "An instance already exists.");
			return;
		}
		mInstance = new WifiSensor(context);
		Log.d("WifiSensor", "Created instance.");
	}

	private WifiSensor(Context context) {
		mContext = context.getApplicationContext();
		wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		if (!wifi.isWifiEnabled()) {
			wifi.setWifiEnabled(true);
		}
		wifi.startScan();

		Log.d("WifiSensor", "Registering listener.");
		mContext.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent intent) {
				Log.d("WifiSensor", "Networks found!");
				networks = wifi.getScanResults();
				size = networks.size();
				Log.d("WifiSensor", "Networks: " + Integer.toString(size));
			}
		}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	/*
	 * TIP: For info on ScanResult:
	 * http://developer.android.com/reference/android/net/wifi/ScanResult.html
	 */

	public List<ScanResult> GetNetworks() {
		if (mContext == null) {
			Log.d("WifiSensor", "No context provided. Returning.");
			return null;
		}

		Log.d("WifiSensor", "Networks: " + Integer.toString(size));
		if (size != 0) {
			// Sorts by the BSSID of the ScanResults.
			Collections.sort(networks, new Comparator<ScanResult>() {
				public int compare(ScanResult s1, ScanResult s2) {
					return s1.BSSID.compareToIgnoreCase(s2.BSSID);
				}
			});

			Log.d("WifiSensor", "Sorted " + Integer.toString(networks.size())
					+ " networks.");
		}
		return networks;
	}

	public int GetSize() {
		return size;
	}

	/**
	 * Start wifi scan from outside this class
	 * 
	 * @author PK
	 */
	public void startScan() {
		wifi.startScan();
	}
}
