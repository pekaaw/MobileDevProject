package hig.imt3672.knowthisroom;

import java.util.List;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements AddRoomDialog.Communicator, DeleteRoomDialog.Communicator {
	
	List<DBRoomEntry> list_of_rooms;
	ArrayAdapter<DBRoomEntry> adapter_room_list;
	DBOperator database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get a handle to a database and open it.
		database = new DBOperator(this);
		database.open();
		
		// Set a listadapter to the listview (room-list)
		list_of_rooms = database.getAllDBRoomEntries();
		adapter_room_list = new ArrayAdapter<DBRoomEntry>(this,
				android.R.layout.simple_list_item_1, list_of_rooms);
		
		ListView listView = (ListView) findViewById(R.id.listRooms);
		listView.setAdapter(adapter_room_list);

		// Let us start the CellTowerHandler service
		startService(new Intent(this, CellTowerHandler.class));

		// Roomlist onclick-events
		listView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				DBRoomEntry room = (DBRoomEntry) parent.getItemAtPosition(position);
				database.deleteRoom(room);
				adapter_room_list.remove(room);
				Toast.makeText(getApplicationContext(), "The room: '" + room.getName() + "' is deleted.", Toast.LENGTH_LONG).show();				
			}
		});
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
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
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * addRoomNameDialog - Open a dialog to name a new Room
	 * <p>
	 * For adding a room, open dialog to supply name.<br>
	 * Dialog will contain an EditText-field for the name and 
	 * the buttons OK and CANCEL.
	 * 
	 * @param view The view that was clicked to prompt the dialog.
	 */
	public void addRoomNameDialog(View view) {
		FragmentManager manager = getFragmentManager();
		AddRoomDialog addRoomDialog = new AddRoomDialog();
		addRoomDialog.show(manager, "add_room_dialog_id");
	}
	
	public void deleteRoomDialog(View view) {
		FragmentManager manager = getFragmentManager();
		DeleteRoomDialog deleteRoomDialog = new DeleteRoomDialog();
		deleteRoomDialog.show(manager, "delete_room_dialog_id");
		
	}
	
	/**
	 * onAddRoomNameRecieved(String name)
	 * <p>
	 * The addRoomDialog has closed with a result.
	 * <p>
	 * Do an integrity check on the name that was supplied and <br>
	 * create a new room. Get data from celltower and wifi networks.
	 * 
	 * @param name The name that was supplied from the dialog.
	 */
	@Override
	public void onAddRoomNameRecieved(String name) {
		// integrity check - name must not be zero or >128 long
		if( name.length() < 1 || name.length() > 128 ) {
			return;
		}
		
		DBRoomEntry addedRoom = database.createRoom(name);
		
		// respond: Message if not created, else put it into list
		if( addedRoom == null ) {
			Toast.makeText(this, "Room failed to be created.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		list_of_rooms = database.getAllDBRoomEntries();
		adapter_room_list.insert(addedRoom, 0);
		
		
		// TODO: Add name to new instance, then add celltower and wifi networks.
		
		// Toast to debug purposes. To be deleted..
	}

	/**
	 * onDeleteCommandReceived(Boolean command)
	 * <p/>
	 * A request to delete a room has been posted.
	 * <p/>
	 * Find the room identifier and delete it from the list and from the database.
	 * 
	 * @param command True upon deletion, otherwise false.
	 */
	@Override
	public void onDeleteCommandReceived(Boolean command) {
		if( command == true ) {
//			delete();	// To delete room. Remember: the identity of the room must be found somewhere...
		}
	}
	
	@Override
	protected void onDestroy() {
		database.close();
		super.onDestroy();
	}
	
}
















