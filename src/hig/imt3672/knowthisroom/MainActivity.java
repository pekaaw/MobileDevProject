package hig.imt3672.knowthisroom;

import java.util.List;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		AddRoomDialog.Communicator, DeleteRoomDialog.Communicator,
		UpdateRoomDialog.Communicator {

	static MainActivity m_instance;

	List<DBRoomEntry> list_of_rooms;
	ArrayAdapter<DBRoomEntry> adapter_room_list;
	Bundle gsmCellData;
	DBOperator database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// make static reference here
		m_instance = this;

		// get a handle to a database and open it.
		DBOperator.createInstance(getBaseContext());
		database = DBOperator.getInstance();
		database.open();

		// Set a listadapter to the listview (room-list)
		list_of_rooms = database.getAllDBRoomEntries();
		adapter_room_list = new ArrayAdapter<DBRoomEntry>(this,
				android.R.layout.simple_list_item_1, list_of_rooms);

		// We create a GsmService intent and start it here:
		final GSMResultReceiver resultReceiver = new GSMResultReceiver(null);
		final Intent i = new Intent(this, ServiceHandler.class);
		// final Intent i = new Intent(this, CellTowerHandler.class);
		i.putExtra("receiver", resultReceiver);
		startService(i);
		WifiSensor.createInstance(this);
		// GplusHandler mPlus = new GplusHandler();
		// mPlus.init();
		// mPlus.postRoom("Jakob");
	}

	@Override
	protected void onStart() {

		ListView listView = (ListView) findViewById(R.id.listRooms);
		listView.setAdapter(adapter_room_list);

		// // Let us start the CellTowerHandler service
		// startService(new Intent(this, CellTowerHandler.class));

		// Roomlist onclick-events
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				DBRoomEntry room = (DBRoomEntry) parent
						.getItemAtPosition(position);
				detailedRoomDialog(view, room, position);
				// FragmentTransaction fragmentAction =
				// getFragmentManager().beginTransaction();
				// fragmentAction.add(R.id.main_fragment_frame,
				// detailedFragment);
				// fragmentAction.commit();
				//
				// DBRoomEntry room = (DBRoomEntry)
				// parent.getItemAtPosition(position);
				// detailedFragment.setRoom(room);

				// TextView roomName = (TextView)
				// listRoomFragment.getView().findViewById(R.id.detailed_room_name);
				// roomName.setText(room.getName());

				// Log.d("pkdata","detailedFragment: " + ((detailedFragment ==
				// null) ? "null" : "not null"));
				//
				// Log.d("pkdata", "room: " + ((room != null) ? "not null" :
				// "null" ) + "ting");
				// try {
				// detailedFragment.setRoom(room);
				// }
				// catch (Exception e) {
				// Log.d("pkdata", "setRoom: " + e.getMessage());
				// }

				// deleteRoomDialog(view, room);
			}
		});

		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add_room_menu_btn:
			addRoomNameDialog(item.getActionView());
			return true;

		case R.id.plus_connection:
			Log.d("#MainActivity#", "try to connect");
			try {
				ServiceHandler.getInstance().gplus_connect();
			} catch (Exception e) {
				// Do nothing. What happend was probably that the service had
				// yet to be created so getInstance returned null
				// @nullPointerException
			}
			return true;

		case R.id.plus_disconnection:
			try {
				ServiceHandler.getInstance().gplus_disconnect();
			} catch (Exception e) {
				// Do nothing.
				Log.d("#MainActivity#", "gplus_disconnect() could not be executed.");
			}
			return true;
			
		case R.id.action_settings:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static MainActivity getInstance() {
		return m_instance;
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode,
			Intent intent) {
		/**
		 * If we previously failed to connect to G+ and response now is OK, we
		 * set the connectionResult in the service to null and try to connect
		 * again. [ I don't know what I'm doing here... ]
		 */
		if (requestCode == GplusHandler.REQUEST_CODE_RESOLVE_ERR
				&& responseCode == RESULT_OK) {
			try {
				ServiceHandler.getInstance().setConnectionResult(null);
				ServiceHandler.getInstance().getPlusClient().connect();
			} catch (Exception e) {
				// in case something fails, don't make phone explode (hopefully)
			}
		}
	}

	/**
	 * addRoomNameDialog - Open a dialog to name a new Room
	 * <p>
	 * For adding a room, open dialog to supply name.<br>
	 * Dialog will contain an EditText-field for the name and the buttons OK and
	 * CANCEL.
	 * 
	 * @param view
	 *            The view that was clicked to prompt the dialog.
	 */
	public void addRoomNameDialog(View view) {
		FragmentManager manager = getFragmentManager();
		AddRoomDialog addRoomDialog = new AddRoomDialog();
		addRoomDialog.show(manager, "add_room_dialog_id");
	}

	/**
	 * Create and show a deleteRoomDialog
	 * 
	 * @param view
	 *            The view that was clicked
	 * @param room
	 *            The room we want to delete
	 * @param listID
	 *            Position in the roomlist - to delete proper list-item
	 */
	public void deleteRoomDialog(View view, DBRoomEntry room, int listID) {
		FragmentManager manager = getFragmentManager();
		DeleteRoomDialog deleteRoomDialog = DeleteRoomDialog.newInstance(room,
				listID);
		deleteRoomDialog.show(manager, "delete_room_dialog_id");
	}

	public void updateRoomDialog(View view, DBRoomEntry room, int listID) {
		FragmentManager manager = getFragmentManager();
		UpdateRoomDialog updateRoomDialog = UpdateRoomDialog.newInstance(room,
				listID);
		updateRoomDialog.show(manager, "update_room_dialog_id");
	}

	/**
	 * Create and show a detailedRoomDialog
	 * 
	 * @param view
	 *            The view that was clicked
	 * @param room
	 *            The room that we want to see details of
	 * @param listID
	 *            Position in the list - needed to delete from list if room is
	 *            deleted.
	 */
	public void detailedRoomDialog(View view, DBRoomEntry room, int listID) {
		FragmentManager manager = getFragmentManager();
		DetailsRoomDialog detailsRoomDialog = DetailsRoomDialog.newInstance(
				room, listID);
		detailsRoomDialog.show(manager, "detailed_room_dialog_id");
	}

	/**
	 * onAddRoomNameRecieved(String name)
	 * <p>
	 * The addRoomDialog has closed with a result.
	 * <p>
	 * Do an integrity check on the name that was supplied and <br>
	 * create a new room. Get data from celltower and wifi networks.
	 * 
	 * @param name
	 *            The name that was supplied from the dialog.
	 */
	@Override
	public void onAddRoomNameRecieved(String name) {
		// integrity check - name must not be zero or >128 long
		if (name.length() < 1 || name.length() > 128) {
			return;
		}

		if (gsmCellData == null) {
			Toast.makeText(this, "Please try again, not enough data recieved",
					Toast.LENGTH_SHORT).show();
			return;
		}

		DBRoomEntry addedRoom = database.createRoom(name, gsmCellData);

		// respond: Message if not created, else put it into list
		if (addedRoom == null) {
			Toast.makeText(this, "Room failed to be created.",
					Toast.LENGTH_SHORT).show();
			return;
		}

		list_of_rooms = database.getAllDBRoomEntries();
		adapter_room_list.insert(addedRoom, 0);

		// TODO: Add name to new instance, then add celltower and wifi networks.

		// Toast to debug purposes. To be deleted..

	}
	
	/**
	 * Display what room we think we're in
	 * @param nameOfRoom The name we want to display
	 */
	public void setFoundRoomToName( String nameOfRoom ) {
		TextView displayedRoom = (TextView) findViewById( R.id.found_room_name );
		
		// try to set text ( expects NullPointerException if string is null )
		try{
			displayedRoom.setText( nameOfRoom );
		}
		catch( NullPointerException e ) {
			Log.d("#MainActivity#", "setFoundRoomToName: " + e.getMessage() );
		}
	}

	/**
	 * onDeleteCommandReceived(Boolean command)
	 * <p/>
	 * A request to delete a room has been posted.
	 * <p/>
	 * Find the room identifier and delete it from the list and from the
	 * database.
	 * 
	 * @param command
	 *            True upon deletion, otherwise false.
	 */
	@Override
	public void onDeleteCommandReceived(Boolean command, DBRoomEntry room,
			int listID) {
		if (command == true) {

			database.deleteRoom(room);
			DBRoomEntry listRoom = adapter_room_list.getItem(listID);
			adapter_room_list.remove(listRoom);

			Toast.makeText(this,
					"The room: '" + room.getName() + "' is deleted.",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onUpdateCommandReceived(Boolean command, DBRoomEntry room,
			int listID) {
		if (command == true) {

			if (gsmCellData == null) {
				Toast.makeText(this,
						"Please try again, not enough data recieved",
						Toast.LENGTH_SHORT).show();
				return;
			}

			database.updateRoom(room, gsmCellData);

			Toast.makeText(this,
					"The room: '" + room.getName() + "' was updated.",
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onDestroy() {
		database.close();
		super.onDestroy();
	}

	// Small class for receiving the bundle of Cell Tower Data
	class GSMResultReceiver extends ResultReceiver {

		public GSMResultReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			// Int to filter out to make sure that the data is ours
			if (resultCode == 100) {
				gsmCellData = resultData;
			}
		}
	}

	interface toService {
		public void gplus_connect();

		public void gplus_disconnect();
	}

}
