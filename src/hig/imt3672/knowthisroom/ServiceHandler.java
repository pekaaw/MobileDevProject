package hig.imt3672.knowthisroom;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class ServiceHandler extends Service {

	// Classes that the service is responsible for:
	CellTowerHandler m_CellTowerHander = null;
	
	public ServiceHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if( intent == null ) {
			Log.d("###", "Service started with a null-intent.");
			return Service.START_NOT_STICKY;
		}
		
		// Initialize classes the service is responsible for
		m_CellTowerHander = new CellTowerHandler();
		final TelephonyManager manager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
		manager.listen(m_CellTowerHander.mTelListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
				| PhoneStateListener.LISTEN_SERVICE_STATE
				| PhoneStateListener.LISTEN_CELL_LOCATION);

		m_CellTowerHander.m_ResultReceiver = intent.getParcelableExtra("receiver");
		m_CellTowerHander.mCellTowerData.addObserver(m_CellTowerHander);


		Log.d("###", "Service started.");
		
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
