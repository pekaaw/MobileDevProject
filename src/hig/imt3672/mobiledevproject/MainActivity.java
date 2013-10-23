package hig.imt3672.mobiledevproject;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private static final int DIALOG_ADD_ROOM = 0;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		Button button = (Button) findViewById(R.id.add_room_btn);
//		button.setOnClickListener( new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());
//				View dialogView = getLayoutInflater().inflate(R.layout.add_name_dialog, null);
//				
//				builder.setView( dialogView )
//					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// code to insert new room							
//						}
//					})
//					.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//						
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							dialog.cancel();
//						}
//					});
//				
//				AlertDialog dialog = builder.create();
//				dialog.show();
//				
//				
////				View popupView = getLayoutInflater().inflate(R.layout.add_name_popup, null);
////				PopupWindow popup = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
////				popup.setAnimationStyle(android.R.style.Animation_Dialog);
////				popup.showAtLocation(popupView, Gravity.CENTER, 0, 0);
////				
////				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////				
////				PopupWindow addNamePopup = new PopupWindow( inflater.inflate(R.layout.add_name_popup, null, false) );
////				addNamePopup.showAtLocation(parent, Gravity.CENTER, 0, 0);
//				
//			}
//		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static class MyDialogFragment extends DialogFragment {
		int mNum;
		
		static MyDialogFragment newInstance( int num ) {
			MyDialogFragment f = new MyDialogFragment();
			
			Bundle args = new Bundle();
			args.putInt("num", num);
			f.setArguments(args);
			
			return f;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mNum = getArguments().getInt("num");
			
			// Pick a style based on the num.
			int style = DialogFragment.STYLE_NORMAL;
			int theme = android.R.style.Theme_Holo;
			switch ((mNum-1)%6) {
				case 1: style = DialogFragment.STYLE_NO_TITLE; break;
				case 2: style = DialogFragment.STYLE_NO_FRAME; break;
				case 3: style = DialogFragment.STYLE_NO_INPUT; break;
				case 4: style = DialogFragment.STYLE_NORMAL; break;
			}
			
			switch ((mNum-1)%6) {
				case 4: theme = android.R.style.Theme_Holo; break;
				case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
				case 6: theme = android.R.style.Theme_Holo_Light; break;
				case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
				case 8: theme = android.R.style.Theme_Holo_Light; break;				
			}

			setStyle(style, theme);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
			View view = inflater.inflate( R.layout.add_name_dialog, container, false );
			View textView = view.findViewById(R.id.add_room_instruction);
			
			Button button = (Button) view.findViewById(R.id.add_room_ok);
			button.setOnClickListener( new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					((FragmentDialog) getActivity()).showDialog();
					
				}
			});
		}
	}
}
