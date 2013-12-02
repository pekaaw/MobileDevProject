package hig.imt3672.knowthisroom;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class UpdateRoomDialog extends DialogFragment implements
		View.OnClickListener {

	Communicator communicator;
	DBRoomEntry m_room = null;
	int m_listID;

	static UpdateRoomDialog newInstance(DBRoomEntry room, int listID) {
		UpdateRoomDialog updateRoomDialog = new UpdateRoomDialog();

		Bundle args = new Bundle();
		args.putLong("id", room.getId());
		args.putString("name", room.getName());
		args.putInt("listID", listID);

		updateRoomDialog.setArguments(args);

		return updateRoomDialog;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().setTitle(R.string.update_room_headline);

		View m_view = inflater.inflate(R.layout.update_name_dialog, null);
		m_view.findViewById(R.id.update_room_ok).setOnClickListener(this);
		m_view.findViewById(R.id.update_room_cancel).setOnClickListener(this);

		return m_view;
	}

	@Override
	public void onStart() {
		getDialog().getWindow().setFeatureDrawableResource(
				Window.FEATURE_LEFT_ICON, R.drawable.logo);
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_room_ok:
			dismiss();
			communicator.onUpdateCommandReceived(true, m_room, m_listID); // Deletion
																			// is
																			// hereby
																			// done!
			break;
		case R.id.update_room_cancel:
			dismiss();
			communicator.onUpdateCommandReceived(false, null, -1); // Do not
																	// update!
			break;

		}
	}

	interface Communicator {
		void onUpdateCommandReceived(Boolean command, DBRoomEntry room,
				int listID);
	}
}
