package hig.imt3672.knowthisroom;

import android.util.Log;

import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.model.moments.ItemScope;
import com.google.android.gms.plus.model.moments.Moment;

public class GplusHandler {
	
    public static final int REQUEST_CODE_RESOLVE_ERR = 9000;

	/*
	 * @Override protected void onCreate(Bundle savedInstanceState) {
	 * super.onCreate(savedInstanceState);
	 * 
	 * mPlusClient = new PlusClient.Builder(this, this, this).setActions(
	 * "http://schemas.google.com/CheckInActivity").build();
	 * mConnectionProgressDialog = new ProgressDialog(this); String[]
	 * dialogue_text = getResources().getStringArray( R.array.gplus_logon);
	 * Random rand = new Random(); int number =
	 * rand.nextInt(dialogue_text.length);
	 * mConnectionProgressDialog.setMessage(dialogue_text[number]);
	 * mPlusClient.connect();
	 * 
	 * }
	 */
	/*
	 * @Override protected void onStart() { super.onStart();
	 * mPlusClient.connect(); }
	 * 
	 * @Override protected void onStop() { super.onStop();
	 * mPlusClient.disconnect(); }
	 */
	/*
	 * @Override public void onConnectionFailed(ConnectionResult result) {
	 * Log.d("onConnectionFailed", "Couldn't connect"); if
	 * (mConnectionProgressDialog.isShowing()) { // The user clicked the sign-in
	 * button already. Start to resolve // connection errors. Wait until
	 * onConnected() to dismiss the // connection dialog. if
	 * (result.hasResolution()) { try { result.startResolutionForResult(this,
	 * REQUEST_CODE_RESOLVE_ERR); } catch (SendIntentException e) {
	 * mPlusClient.connect(); } } } // Save the result and resolve the
	 * connection failure upon a user click. mConnectionResult = result; }
	 * 
	 * @Override protected void onActivityResult(int requestCode, int
	 * responseCode, Intent intent) { if (requestCode ==
	 * REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
	 * mConnectionResult = null; mPlusClient.connect(); } }
	 */

	public void postRoom(String roomName, PlusClient plusClient) {
		// https://developers.google.com/+/api/moment-types/checkin-activity
		// https://developers.google.com/+/api/latest/activities
		// https://developers.google.com/+/api/latest/moments/insert
		// key=AIzaSyBptPnBueMjNC92boYOEmH_oWS4sBR_UGU
		// mPlusClient.writeMoment(moment);

		// Builds the item for the moment
		ItemScope itemScope = new ItemScope.Builder()
				.setId(roomName)
				.setType("http://schema.org/Thing")
				.setName(roomName)
				.setDescription("I just went into room: " + roomName)
				//.setText("I just went into room: " + roomName)
				.build();

		// Builds a moment
		Moment moment = new Moment.Builder()
				.setType("http://schemas.google.com/CheckInActivity")
				.setTarget(itemScope)
				.build();
		Log.d("Moment", moment.toString());
		plusClient.writeMoment(moment);
	}
}