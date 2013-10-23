package hig.imt3672.mobiledevproject;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
			
		Button button = (Button) findViewById(R.id.add_room_btn);
		button.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// custom dialog
				Dialog dialog = new Dialog(getApplicationContext());
				dialog.setContentView(R.layout.add_name_dialog);
				dialog.show();
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
















