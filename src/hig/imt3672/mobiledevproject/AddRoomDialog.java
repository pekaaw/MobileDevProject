package hig.imt3672.mobiledevproject;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AddRoomDialog extends DialogFragment implements View.OnClickListener {
	
	View m_view = null;
	Communicator communicator; 
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		communicator = (Communicator) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Log.d("onCreate", "Dialog created.");
		
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
