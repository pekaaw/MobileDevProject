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
	DBRoomEntry m_room = null;
	int m_listID;
		
	static DeleteRoomDialog newInstance( DBRoomEntry room, int listID ) {
		DeleteRoomDialog deleteRoomDialog = new DeleteRoomDialog();
		
		Bundle args = new Bundle();
		args.putLong("id", room.getId());
		args.putString("name", room.getName());
		args.putInt("listID", listID);
		
		deleteRoomDialog.setArguments(args);
		
		return deleteRoomDialog;
	}
	
	@Override
	public void onAttach(Activity activity) {
		communicator = (Communicator) activity;
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		// Recreate room from Bundle
		m_room = new DBRoomEntry();
		m_room.setId(getArguments().getLong("id"));
		m_room.setName(getArguments().getString("name"));
		m_listID = getArguments().getInt("listID");

		super.onCreate(savedInstanceState);
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
	public void onStart() {
		getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.delete_room);
		super.onStart();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.delete_room_ok :
			dismiss();
			communicator.onDeleteCommandReceived(true, m_room, m_listID);		// Deletion is hereby done!
			break;
		case R.id.delete_room_cancel :
			dismiss();
			communicator.onDeleteCommandReceived(false, null, -1);	// Do not delete!
			break;

		}
	}
	
	interface Communicator {
		public void onDeleteCommandReceived(Boolean command, DBRoomEntry room, int listID);
	}
}
