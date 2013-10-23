package hig.imt3672.mobiledevproject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

public class MainActivity extends Activity {
	
	private static final int DIALOG_ADD_ROOM = 0;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button button = (Button) findViewById(R.id.add_room_btn);
		button.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
				View dialogView = getLayoutInflater().inflate(R.layout.add_name_dialog, null);
				
				builder.setView( dialogView )
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// code to insert new room							
						}
					})
					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
				
				AlertDialog dialog = builder.create();
				dialog.show();
				
				
//				View popupView = getLayoutInflater().inflate(R.layout.add_name_popup, null);
//				PopupWindow popup = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
//				popup.setAnimationStyle(android.R.style.Animation_Dialog);
//				popup.showAtLocation(popupView, Gravity.CENTER, 0, 0);
//				
//				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				
//				PopupWindow addNamePopup = new PopupWindow( inflater.inflate(R.layout.add_name_popup, null, false) );
//				addNamePopup.showAtLocation(parent, Gravity.CENTER, 0, 0);
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
