package hig.imt3672.knowthisroom;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class DeleteRoomDialog extends DialogFragment implements View.OnClickListener{
	
	Communicator communicator;
	DBRoomEntry m_room;
	
	public DeleteRoomDialog() {
		m_room = null;
	}
	
	public void initiate( DBRoomEntry room ) {
		m_room = room;
	}
	
	@Override
	public void onStart() {
		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.delete_room);
		super.onStart();
	}
	
	@Override
	public void onAttach(Activity activity) {
		communicator = (Communicator) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().setTitle(R.string.delete_room_headline);
		
		View m_view = inflater.inflate(R.layout.delete_name_dialog, null);
		m_view.findViewById(R.id.delete_room_ok).setOnClickListener(this);
		m_view.findViewById(R.id.delete_room_cancel).setOnClickListener(this);
		
		return m_view;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.delete_room_ok :
			dismiss();
			communicator.onDeleteCommandReceived(true, m_room);		// Deletion is hereby done!
			break;
		case R.id.delete_room_cancel :
			dismiss();
			communicator.onDeleteCommandReceived(false, null);	// Do not delete!
			break;

		}
	}
	
	interface Communicator {
		public void onDeleteCommandReceived(Boolean command, DBRoomEntry room);
	}
}
