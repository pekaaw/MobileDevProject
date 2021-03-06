package hig.imt3672.knowthisroom;

import java.util.List;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ListView;

public class DetailsRoomDialog extends DialogFragment implements
		View.OnClickListener {

	DBRoomEntry m_room = null;
	int m_listID;
	ListView m_listView = null;

	static DetailsRoomDialog newInstance(DBRoomEntry room, int listID) {

		DetailsRoomDialog drd = new DetailsRoomDialog();

		// Supply arguments
		Bundle args = new Bundle();
		args.putLong("id", room.getId());
		args.putString("name", room.getName());
		args.putInt("listID", listID);
		drd.setArguments(args);

		return drd;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// Recreate the room-item
		m_room = new DBRoomEntry();
		m_room.setId(getArguments().getLong("id"));
		m_room.setName(getArguments().getString("name"));
		m_listID = getArguments().getInt("listID");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Ask the wifisensor to start a new scan. In this way we can have some
		// fresh data if the user wants to update wifi-info for the room.
		Utils.startWifiScan();
		
		// Set title and commit the icon-change
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);

		// Make Dialog headline like "Details of "+roomName
		String headline = getString(R.string.detailed_room_headline)
				+ m_room.getName();
		getDialog().setTitle(headline);

		// Inflate layout and activate the delete-button
		View m_view = inflater.inflate(R.layout.details_room_dialog, null);
		m_view.findViewById(R.id.delete_button).setOnClickListener(this);
		m_view.findViewById(R.id.update_button).setOnClickListener(this);

		List<DBWifiInRoomEntry> wifiList;

		try {
			wifiList = DBOperator.getInstance().getWifi(m_room.getId());
		} catch (Exception e) {
			Log.d("#DetailsRoomDialog#", "Exception: " + e.getMessage());
			return m_view;
		}

		m_listView = (ListView) m_view
				.findViewById(R.id.details_room_wifi_list);

		DetailsAdapter adapter = new DetailsAdapter(getActivity(), wifiList);
		m_listView.setAdapter(adapter);

		return m_view;
	}

	@Override
	public void onStart() {
		// TODO: create icon for viewing details about a room and set it here
		// instead of delete_room
		getDialog().getWindow().setFeatureDrawableResource(
				Window.FEATURE_LEFT_ICON, R.drawable.info_room);
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		
		// for the delete button: Create a deleteDialog and show it
		if (v.getId() == R.id.delete_button) {
			dismiss();
			FragmentManager manager = getFragmentManager();
			DeleteRoomDialog deleteRoomDialog = DeleteRoomDialog.newInstance(
					m_room, m_listID);
			deleteRoomDialog.show(manager, "delete_room_dialog_id");
		}
		
		// for the update button: Create a updateRoomDialog and show it
		if (v.getId() == R.id.update_button) {
			dismiss();
			FragmentManager manager = getFragmentManager();
			UpdateRoomDialog updateRoomDialog = UpdateRoomDialog.newInstance(
					m_room, m_listID);
			updateRoomDialog.show(manager, "update_room_dialog_id");
		}
	}
}
