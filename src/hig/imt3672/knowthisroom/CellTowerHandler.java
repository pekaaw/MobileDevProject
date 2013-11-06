package hig.imt3672.knowthisroom;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Represents the service for handling Cell Tower updates.
 * 
 * TODO: recording cell location changes is TOO frequent, and we may not want to
 * do that
 */
public class CellTowerHandler extends Service {

	CellLocation mCellLocation;
	SignalStrength mSignalStrength;
	ServiceState mServiceState;

	Intent mIntent;

	// singleton
	final TelephonyListener mTelListener = new TelephonyListener();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		setup(intent);
		// TODO remove the logging outputs for production
		Log.d("########", "CellTowerHandler Service Started");
		return Service.START_STICKY;
	}

	private void setup(Intent intent) {
		final TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		manager.listen(mTelListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
				| PhoneStateListener.LISTEN_SERVICE_STATE
				| PhoneStateListener.LISTEN_CELL_LOCATION);

		// This one is from API level 17, we do not use it then
		// PhoneStateListener.LISTEN_CELL_INFO );

		mIntent = intent;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public Bundle getTowerInfo() {
		Bundle towerInfo = new Bundle();

		towerInfo.putString("CellID", mCellLocation.toString());
		towerInfo.putInt("Strength", mSignalStrength.getGsmSignalStrength());
		towerInfo.putInt("cellNoise", mSignalStrength.getGsmBitErrorRate());

		return towerInfo;
	}

	/**
	 * Responsible for managing the updates from the TelephonyManager.
	 */
	public class TelephonyListener extends PhoneStateListener {

		@Override
		public void onSignalStrengthsChanged(SignalStrength strength) {
			// each time there is an update, get the signal strength
			mSignalStrength = strength;
			// TODO debugging
			Log.d("########",
					"Signal strength " + mSignalStrength.getGsmSignalStrength());
			super.onSignalStrengthsChanged(strength);

			mIntent.replaceExtras(getTowerInfo());
			sendBroadcast(mIntent);
		}

		@Override
		public void onCellLocationChanged(CellLocation location) {
			mCellLocation = location;
			// TODO debugging
			Log.d("########", "Cell Location " + mCellLocation.toString());
			super.onCellLocationChanged(location);
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {
			mServiceState = serviceState;
			// TODO debugging
			Log.d("########",
					"Service State " + serviceState.getOperatorAlphaLong()
							+ " " + serviceState.getOperatorNumeric());
			super.onServiceStateChanged(serviceState);
		}

	}

}
