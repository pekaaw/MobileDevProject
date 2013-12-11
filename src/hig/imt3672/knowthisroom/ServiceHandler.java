package hig.imt3672.knowthisroom;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.plus.PlusClient;

public class ServiceHandler extends Service implements ConnectionCallbacks,
		OnConnectionFailedListener, MainActivity.toService {

	// static reference to self
	private static ServiceHandler m_instance;

	// PlusClient variables
	ProgressDialog m_ConnectionProgressDialog;
	PlusClient m_PlusClient;
	ConnectionResult m_ConnectionResult;

	// Classes that the service is responsible for:
	CellTowerHandler m_CellTowerHander = null;
	GplusHandler m_GplusHandler = null;

	// other variables
	WifiSensor m_WifiSensor = null;
	DBOperator m_Database = null;
	RoomCheckin m_RoomFinder = null;
	private static Timer m_scheduledTask = null;
	JsonPoster m_jsonPoster = null;

	// run algorithm each 60 second
	static long TIMER_LENGTH_MILLISECONDS = 60000;

	// Wait 2 seconds to retrieve networks
	static long TIME_TO_GET_NETWORKS = 2000;

	// Empty constructor
	public ServiceHandler() {
	}

	@Override
	public void onCreate() {

		// make static reference here
		m_instance = this;

		// Initialize the google PlusClient
		m_PlusClient = new PlusClient.Builder(this, this, this)
				.setActions("http://schemas.google.com/CheckInActivity")
				.setScopes(Scopes.PLUS_LOGIN).build();

		m_GplusHandler = new GplusHandler();
		m_jsonPoster = new JsonPoster(this);

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		m_PlusClient.connect();

		if (intent == null) {
			Log.d("###", "Service started with a null-intent.");
			return Service.START_NOT_STICKY;
		}

		// Make sure instances of singletons is instantiated
		WifiSensor.createInstance(this);
		DBOperator.createInstance(this);
		RoomCheckin.createInstance(this);

		// Get the singletons that we need a handle to
		m_WifiSensor = WifiSensor.getInstance();
		m_Database = DBOperator.getInstance();
		m_RoomFinder = RoomCheckin.getInstance();

		// Initialize classes the service is responsible for
		m_CellTowerHander = new CellTowerHandler();
		final TelephonyManager manager = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
		manager.listen(m_CellTowerHander.mTelListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
						| PhoneStateListener.LISTEN_SERVICE_STATE
						| PhoneStateListener.LISTEN_CELL_LOCATION);

		m_CellTowerHander.m_ResultReceiver = intent
				.getParcelableExtra("receiver");
		m_CellTowerHander.mCellTowerData.addObserver(m_CellTowerHander);

		// schedule a task, each 1 minute we want the algorithm to run,
		// compare networks and find room, post if different than before.
		m_scheduledTask = new Timer();
		m_scheduledTask.scheduleAtFixedRate(new StartFindThisRoomAction(), 0,
				TIMER_LENGTH_MILLISECONDS);

		Log.d("###", "Service started.");

		return Service.START_NOT_STICKY;
	}

	/*
	 * Must-be-implemented method of a Service (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/*
	 * This runs when the service is stopped and destroyed.
	 */
	@Override
	public void onDestroy() {
		m_PlusClient.disconnect();
		Log.d("###", "Service stopped.");
		super.onDestroy();
	}

	/**
	 * getInstance()
	 * <p>
	 * Returns a static pointer to this service. We can call it a hack, but it
	 * is used in MainActivity for communication between the activity and the
	 * service.
	 * 
	 * @return this instance of ServiceHandler
	 */
	public static ServiceHandler getInstance() {
		return m_instance;
	}

	public PlusClient getPlusClient() {
		return m_PlusClient;
	}

	public void setConnectionResult(ConnectionResult m_ConnectionResult) {
		this.m_ConnectionResult = m_ConnectionResult;
	}

	/*
	 * Called when there was an error connecting the PlusClient to the service.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d("#Service#", "Connection failed.");

		// this may resolt in a loop if mainactivity is not found
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(MainActivity.getInstance(),
						GplusHandler.REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				m_PlusClient.connect();
			}
		}

		m_ConnectionResult = result;
	}

	/**
	 * onConnected
	 * <p>
	 * This method is called when a connection to G+ has been established
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d("#Service#", "Got connected.");
		m_GplusHandler.postRoom("Know This Room", m_PlusClient);

	}

	/**
	 * onDisconnected
	 * <p>
	 * This method is called if a connection to G+ has failed
	 */
	@Override
	public void onDisconnected() {
		Log.d("#Service#", "Got disconnected.");

	}

	/**
	 * Connect to Google Plus through the PlusClient
	 */
	@Override
	public void gplus_connect() {
		Log.d("Service", "connection! we look for you");
		m_PlusClient.connect();

	}

	/**
	 * Disconnect from Google Plus through the PlusClient TODO: This doesn't
	 * work.
	 */
	@Override
	public void gplus_disconnect() {
		Log.d("#Service#", "Try to disconnect.");
		m_PlusClient.disconnect();
	}

	/**
	 * StartFindThisRoomAction
	 * <p>
	 * Run a network scan, wait for TIME_TO_GET_NETWORKS, then start
	 * FindThisRoomAction()
	 * 
	 * @author Pekaaw
	 * 
	 */
	class StartFindThisRoomAction extends TimerTask {

		@Override
		public void run() {
			m_WifiSensor.startScan();
			Timer startAction = new Timer();
			startAction
					.schedule(new FindThisRoomAction(), TIME_TO_GET_NETWORKS);
		}
	}

	/**
	 * FindThisRoomAction
	 * <p>
	 * This is what we want the service to do at a given interval. 1. compare
	 * network data to find what room we're in 2. if different from before 2.1
	 * change room 2.2 post to Internet
	 * 
	 * @author Pekaaw
	 * 
	 */
	public class FindThisRoomAction extends TimerTask {

		@Override
		public void run() {
			// ToDo: Fill with action, run algorithm and do stuff accordingly
			DBRoomEntry room = findRoom();
			sendRoomToActivity(room);
		}
	}

	DBRoomEntry findRoom() {
		// Get roomFinder
		RoomCheckin roomFinder = RoomCheckin.getInstance();

		// find room we're in
		DBRoomEntry room = roomFinder.GetRoom(roomFinder.GetRooms());

		if (room == null) {
			// create a "I don't know"-room
			room = new DBRoomEntry();
			room.setId(0);
			room.setName(getString(R.string.update_room_not_found));
		}

		Log.d("#FindThisRoomAction#", room.getName());

		return room;
	}

	void sendRoomToActivity(DBRoomEntry room) {

		// Put name to bundle and attach bundle to message
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("roomName", room.getName());
		msg.setData(bundle);

		// Send the message to a handler in the activity
		MainActivity.getInstance().setRoomHandler.sendMessage(msg);
		// Sends the thing stuff to the json poster dude
		m_jsonPoster.postToPage(m_PlusClient, room.getName(),
				m_Database.getWifiBsIDs(room.getId(), 4));
	}

}
