package hig.imt3672.mobiledevproject;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class AddRoomDialog extends DialogFragment implements View.OnClickListener {
	
	View m_view = null;
	Communicator communicator; 
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		
		super.onAttach(activity);
		communicator = (Communicator) activity;
	}
	
//	@Override
//	public Dialog onCreateDialog(Bundle savedInstanceState) {
//		LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//		View view = inflater.inflate(R.layout.add_name_dialog, null);
//		
//		view.findViewById(R.id.add_room_ok).setOnClickListener(this);
//		view.findViewById(R.id.add_room_cancel).setOnClickListener(this);
//		
//		Builder dialog = new AlertDialog.Builder(getActivity());
//		dialog.setIcon(R.drawable.ic_launcher);
//		dialog.setView(view);
//		return dialog.create();
//	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Log.d("onCreate", "Dialog created.");
		
		
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
	
		getDialog().setTitle(R.string.add_room);
		
		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.add_room);
		
		m_view = inflater.inflate(R.layout.add_name_dialog, null);
		
	
		// Make buttons clickable
		m_view.findViewById(R.id.add_room_ok).setOnClickListener(this);
		m_view.findViewById(R.id.add_room_cancel).setOnClickListener(this);
		
		setCancelable(false);
		return m_view;

	}
	
	@Override
	public void onClick(View v) {
		Log.d("onClick Event", "A button was pressed.");
		switch(v.getId()) {
			case R.id.add_room_cancel :
				dismiss();
				communicator.onAddRoomNameRecieved("Hello from CANCEL?");
				break;
			case R.id.add_room_ok :
				dismiss();
				communicator.onAddRoomNameRecieved("Hello from OK?");
				break;
		}
	} // end onClick
	
	interface Communicator {
		public void onAddRoomNameRecieved(String name);
//		public void onDialogMessage(String message);
	}

} // end AddRoomDialog
