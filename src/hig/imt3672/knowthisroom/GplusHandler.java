package hig.imt3672.knowthisroom;

import java.util.Random;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.moments.ItemScope;
import com.google.android.gms.plus.model.moments.Moment;

public class GplusHandler extends MainActivity implements ConnectionCallbacks,
		OnConnectionFailedListener {
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	private ProgressDialog mConnectionProgressDialog;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		init();
		super.onCreate(savedInstanceState);
	}

	public void init() {
		PlusClient.Builder builder = new PlusClient.Builder(this, this, this);
		builder.setActions("http://schemas.google.com/CheckInActivity");
		builder.setScopes("PLUS_LOGIN");
		PlusClient test = builder.build();

		mPlusClient = test;
		// mPlusClient = new PlusClient.Builder(this, this, this)
		// .setActions("http://schemas.google.com/CheckInActivity")
		// .setScopes("PLUS_LOGIN").build();
		// Progress bar to be displayed if the connection failure is not
		// resolved.
		mConnectionProgressDialog = new ProgressDialog(this);
		String[] dialogue_text = getResources().getStringArray(
				R.array.gplus_logon);
		Random rand = new Random();
		int number = rand.nextInt(dialogue_text.length);
		mConnectionProgressDialog.setMessage(dialogue_text[number]);
		mPlusClient.connect();
	}

	@Override
	protected void onStart() {
		super.onStart();
		mPlusClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d("onConnectionFailed", "Couldn't connect");
		if (mConnectionProgressDialog.isShowing()) {
			// The user clicked the sign-in button already. Start to resolve
			// connection errors. Wait until onConnected() to dismiss the
			// connection dialog.
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this,
							REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}
		// Save the result and resolve the connection failure upon a user click.
		mConnectionResult = result;
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR
				&& responseCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		String accountName = mPlusClient.getAccountName();
		Log.d("onConnected", "We got " + accountName);
		Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG)
				.show();
	}

	@Override
	public void onDisconnected() {
		Log.d("GplusHandler", "disconnected");
	}

	public void postRoom(String roomName) {
		// https://developers.google.com/+/api/moment-types/checkin-activity
		// https://developers.google.com/+/api/latest/activities
		// https://developers.google.com/+/api/latest/moments/insert
		// key=AIzaSyBptPnBueMjNC92boYOEmH_oWS4sBR_UGU
		// mPlusClient.writeMoment(moment);

		// Builds the item for the moment
		ItemScope itemScope = new ItemScope.Builder().setId(roomName)
				.setType("http://schemas.google.com/CheckInActivity")
				.setName(roomName)
				.setDescription("I just went into room: " + roomName)
				.setText("I just went into room: " + roomName)

				.build();

		// Builds a moment
		Moment moment = new Moment.Builder()
				.setType("http://schemas.google.com/CheckInActivity")
				.setTarget(itemScope).build();
		Log.d("Moment", moment.toString());
		// mPlusClient.writeMoment(moment);
		// startActivityForResult(intent, int);
	}
}