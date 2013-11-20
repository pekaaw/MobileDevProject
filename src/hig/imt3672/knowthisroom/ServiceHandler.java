package hig.imt3672.knowthisroom;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;

public class ServiceHandler extends Service implements ConnectionCallbacks, OnConnectionFailedListener, MainActivity.toService {

	// static reference to self
	private static ServiceHandler m_instance;
	
	// PlusClient variables
	ProgressDialog m_ConnectionProgressDialog;
	PlusClient m_PlusClient;
	ConnectionResult m_ConnectionResult;
	
	
	public PlusClient getPlusClient() {
		return m_PlusClient;
	}

	public void setConnectionResult(ConnectionResult m_ConnectionResult) {
		this.m_ConnectionResult = m_ConnectionResult;
	}

	// Classes that the service is responsible for:
	CellTowerHandler m_CellTowerHander = null;
	GplusHandler m_GplusHandler = null;
	
	// Empty constructor
	public ServiceHandler() {}
	
	@Override
	public void onCreate() {
		
		// make static reference here
		m_instance = this;
		
		// Initialize the google PlusClient
		m_PlusClient = new PlusClient.Builder(this, this, this)
			.setActions("http://schemas.google.com/CheckInActivity")
			.setScopes(Scopes.PLUS_LOGIN)
			.build();
		
		m_GplusHandler = new GplusHandler();

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		m_PlusClient.connect();

		if( intent == null ) {
			Log.d("###", "Service started with a null-intent.");
			return Service.START_NOT_STICKY;
		}
		
		WifiSensor.createInstance(this);
		DBOperator.createInstance(this);
		
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
		return null;
	}
	
	public static ServiceHandler getInstance() {
		return m_instance;
	}

	@Override
	public void onDestroy() {
		m_PlusClient.disconnect();
		Log.d("###", "Service stopped.");
		super.onDestroy();
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d("#Service#", "Connection failed.");
		
		// this may resolt in a loop if mainactivity is not found
        if (result.hasResolution()) {
        	try {
        		result.startResolutionForResult(MainActivity.getInstance(), GplusHandler.REQUEST_CODE_RESOLVE_ERR);
        	} catch (SendIntentException e) {
        		m_PlusClient.connect();
    		}
        }


		m_ConnectionResult = result;		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		m_GplusHandler.postRoom("Jakob", m_PlusClient);

		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gplus_connect() {
		Log.d("Service", "connection! we look for you");
		m_PlusClient.connect();
				
	}

	@Override
	public void gplus_disconnect() {
		// TODO Auto-generated method stub
		
	}

}
