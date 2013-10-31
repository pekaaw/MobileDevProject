package hig.imt3672.knowthisroom;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

/**
 * Represents the service for handling Cell Tower updates. 
 */
public class CellTowerHandler extends Service {
	
	GsmCellLocation gsmCellLocation;
	SignalStrength gsmCellStrength;
	int cellID;
	int cellStrength;
	int cellNoise;

	// singleton
	final TelephonyListener mTelListener = new TelephonyListener();
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		setup();
		return Service.START_STICKY;
	}

	
	
	private void setup() {
		final TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		manager.listen(mTelListener, 
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS |
				PhoneStateListener.LISTEN_SERVICE_STATE |
				PhoneStateListener.LISTEN_CELL_INFO );
	}
	


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getTowerInfo() {

		final int[] towerInformation = { cellID, cellStrength, cellNoise };
		return towerInformation;
	}
	
	
	public class TelephonyListener extends PhoneStateListener {
		
	}
	
}
