package hig.imt3672.mobiledevproject;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements AddRoomDialog.Communicator{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
			
		Button button = (Button) findViewById(R.id.add_room_btn);
		button.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					
				// custom dialog
				showDialog(v);
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void showDialog(View view) {
		FragmentManager manager = getFragmentManager();
		AddRoomDialog addRoomDialog = new AddRoomDialog();
		addRoomDialog.show(manager, "Add a new room.");
	}
	
//	@Override
//	public void onDialogMessage(String message) {
//		// TODO Auto-generated method stub
//		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//	}
	
	@Override
	public void onAddRoomNameRecieved(String name) {
		// TODO: Integrity check on name
		// TODO: Add name to new instance, then add celltower and wifi networks.
		
		// Toast to debug purposes. To be deleted..
		Toast.makeText(this, name, Toast.LENGTH_SHORT).show();
	}
//	public void showDialog() {
//		AlertDialog alertDialog = new AlertDialog.Builder(getBaseContext()).create();
//		alertDialog.setTitle("Alert Dialog");
//		alertDialog.setMessage("Welcome!");
//		alertDialog.setIcon(R.drawable.ic_launcher);
//
//		alertDialog.show();
//	}

}
















