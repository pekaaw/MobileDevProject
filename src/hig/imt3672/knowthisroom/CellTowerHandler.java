package hig.imt3672.knowthisroom;

import java.util.Observable;
import java.util.Observer;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
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
public class CellTowerHandler extends Service implements Observer {

	final CellTowerData mCellTowerData = new CellTowerData();

	Intent mIntent;
	ResultReceiver resultReceiver;

	// singleton
	final TelephonyListener mTelListener = new TelephonyListener();

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if ( intent == null ) {
			Log.d("###", "Service started with a null-intent");
			return START_NOT_STICKY;
			// START_NOT_STICKY means that when the calling activity
			// shuts down, the service will shut down as well.
			// When we want our service to continue, we want
			// START_STICKY so that the service sticks around in the
			// system, but the intent will then be called with a
			// intent like a nullptr. this will of course give a
			// nullpointerexception so we need to handle it with 
			// this if =D
		}
		setup(intent);
		// TODO remove the logging outputs for production
		Log.d("########", "CellTowerHandler Service Started");
		return Service.START_NOT_STICKY;
		// Should be Service.START_STICKY; when we want the service to
		// hang around.
	}

	private void setup(Intent intent) {
		final TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		manager.listen(mTelListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
				| PhoneStateListener.LISTEN_SERVICE_STATE
				| PhoneStateListener.LISTEN_CELL_LOCATION);

		this.resultReceiver = intent.getParcelableExtra("receiver");
		mCellTowerData.addObserver(this);

		mIntent = intent;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Responsible for managing the updates from the TelephonyManager.
	 */
	public class TelephonyListener extends PhoneStateListener {

		@Override
		public void onSignalStrengthsChanged(SignalStrength strength) {
			// each time there is an update, get the signal strength

			mCellTowerData.setSignalStrength(strength);
			// TODO debugging
			Log.d("########", "Signal strength "
					+ mCellTowerData.getSignalStrength().getGsmSignalStrength());
			super.onSignalStrengthsChanged(strength);
		}

		@Override
		public void onCellLocationChanged(CellLocation location) {

			mCellTowerData.setCellLocation(location);
			// TODO debugging
			Log.d("########", "Cell Location "
					+ mCellTowerData.getCellLocation().toString());
			super.onCellLocationChanged(location);
		}

		@Override
		public void onServiceStateChanged(ServiceState serviceState) {

			mCellTowerData.setServiceState(serviceState);

			// TODO debugging
			Log.d("########",
					"Service State " + serviceState.getOperatorAlphaLong()
							+ " " + serviceState.getOperatorNumeric());
			super.onServiceStateChanged(serviceState);
		}

	}

	@Override
	public void update(Observable observable, Object data) {
		// If the bundle is not complete we don't want to return it
		if (mCellTowerData.isEmpty()) {
			return;
		}

		final Bundle bundle = mCellTowerData.getCellTowerBundle();
		this.resultReceiver.send(100, bundle);

	}

}
