package hig.imt3672.knowthisroom;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class AddRoomDialog extends DialogFragment implements View.OnClickListener {
	
	View m_view = null;
	Communicator communicator; 
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		communicator = (Communicator) activity;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.add_room);
		super.onStart();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Log.d("onCreate", "Dialog created.");
		
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().setTitle(R.string.add_room);
				
		m_view = inflater.inflate(R.layout.add_name_dialog, null);
		
		// Make buttons clickable
		m_view.findViewById(R.id.add_room_ok).setOnClickListener(this);
		m_view.findViewById(R.id.add_room_cancel).setOnClickListener(this);
		
		setCancelable(false);
		
		return m_view;

	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
			case R.id.add_room_ok :
				dismiss();
				EditText namefield = (EditText) m_view.findViewById(R.id.add_room_name);
				String name = namefield.getText().toString();
				communicator.onAddRoomNameRecieved(name);
				break;
			case R.id.add_room_cancel :
				dismiss();
				Toast.makeText( ( getActivity() ), "No room added.", Toast.LENGTH_LONG).show();
				//communicator.onAddRoomNameRecieved("Hello from OK?");
				break;
		}
	} // end onClick
	
	interface Communicator {
		public void onAddRoomNameRecieved(String name);
	}

} // end AddRoomDialog
