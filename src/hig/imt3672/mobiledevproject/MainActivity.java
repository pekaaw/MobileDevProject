package hig.imt3672.mobiledevproject;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements AddRoomDialog.Communicator{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
					
		findViewById(R.id.add_room_btn)
			.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
						
					// For adding a room, open dialog to supply name
					addRoomNameDialog(v);
				}
				
			});
		
		findViewById(R.id.delete_room_btn)
			.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// For deleting a room, open a dialog to confirm
					deleteRoomDialog(v);
				}
			});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
		//addRoomDialog.getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.add_room);
	}
	
	public void deleteRoomDialog(View view) {
		//FragmentManager manager = getFragmentManager();
		
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
		// TODO: Integrity check on name
		// TODO: Add name to new instance, then add celltower and wifi networks.
		
		// Toast to debug purposes. To be deleted..
		Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
	}

}
















