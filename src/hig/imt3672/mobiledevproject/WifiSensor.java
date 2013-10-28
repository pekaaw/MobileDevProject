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
 * 
 * HELP:
 * If you are unfamiliar with what a ScanResult contains, this document is okay:
 * http://developer.android.com/reference/android/net/wifi/ScanResult.html*/

package hig.imt3672.mobiledevproject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;

public class WifiSensor extends Service {
	WifiManager wifi;
	List<ScanResult> networks;

	public List<ScanResult> onStartCommand(int startId) {
		networks = wifi.getScanResults();
		Collections.sort(networks, new Comparator<ScanResult>() {
			public int compare(ScanResult s1, ScanResult s2) {
				return s1.BSSID.compareToIgnoreCase(s2.BSSID);
			}
		});
		return networks;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
