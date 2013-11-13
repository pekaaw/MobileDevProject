package hig.imt3672.knowthisroom;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class DetailsRoomDialog extends DialogFragment implements View.OnClickListener{
	
//	Communicator communicator;
	DBRoomEntry m_room;
	
	public DetailsRoomDialog() {
		m_room = null;
	}
	
	public void initiate( DBRoomEntry room ) {
		m_room = room;
	}
	
	@Override
	public void onStart() {
		// TODO: create icon for viewing details about a room and set it here instead of delete_room
		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.delete_room);
		super.onStart();
	}
	
	@Override
	public void onAttach(Activity activity) {
//		communicator = (Communicator) activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		// Make headline like "Details of "+roomName
		String headline = getString(R.string.detailed_room_headline) + m_room.getName();
		
		// Set title and commit the icon-change
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().setTitle(headline);
		
		// Inflate layout and activate the delete-button
		View m_view = inflater.inflate(R.layout.detailed_room, null);
		m_view.findViewById(R.id.delete_button).setOnClickListener(this);
		
		return m_view;
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.delete_button ) {
			dismiss();
			FragmentManager manager = getFragmentManager();
			DeleteRoomDialog deleteRoomDialog = new DeleteRoomDialog();
			deleteRoomDialog.initiate(m_room);
			deleteRoomDialog.show(manager, "delete_room_dialog_id");
		}
//		switch(v.getId()) {
//		case R.id.delete_room:
//			dismiss();
//			communicator.onDeleteCommandReceived(true, m_room);		// Deletion is hereby done!
//			break;
//		case R.id.delete_room_cancel :
//			dismiss();
//			communicator.onDeleteCommandReceived(false, null);	// Do not delete!
//			break;
//		}
	}
	
//	interface Communicator {
//		public void onDeleteCommandReceived(Boolean command, DBRoomEntry room);
//	}
}
