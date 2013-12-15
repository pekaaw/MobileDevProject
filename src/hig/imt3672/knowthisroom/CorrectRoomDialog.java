package hig.imt3672.knowthisroom;

import java.util.List;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CorrectRoomDialog extends DialogFragment implements
		View.OnClickListener{

	DBRoomEntry m_room = null;
	int m_listID;
	View m_view;
	List<DBRoomEntry> list_of_rooms;
	ArrayAdapter<DBRoomEntry> adapter_room_list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		list_of_rooms = DBOperator.getInstance().getAllDBRoomEntries();
		adapter_room_list = new ArrayAdapter<DBRoomEntry>( MainActivity.getInstance(),
				android.R.layout.simple_list_item_1, list_of_rooms );
		
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		getDialog().getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		getDialog().setTitle(R.string.update_room_headline);
		
		m_view = inflater.inflate(R.layout.correct_room, null);
		m_view.findViewById(R.id.add_room).setOnClickListener(this);
		
		return m_view;
	}

	@Override
	public void onStart() {
		getDialog().getWindow().setFeatureDrawableResource(
				Window.FEATURE_LEFT_ICON, R.drawable.logo );
		
		ListView listView = (ListView) m_view.findViewById(R.id.current_rooms);
		listView.setAdapter(adapter_room_list);
		
		listView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id ) {
				
				DBRoomEntry room = (DBRoomEntry) parent
						.getItemAtPosition(position);
				
				dismiss();

				MainActivity.getInstance().updateRoomDialog(view, room, position);
			}
		});

		
		super.onStart();
	}
	
	@Override
	public void onClick(View v) {
		switch( v.getId() ) {
		case R.id.add_room:
			dismiss();		
			MainActivity.getInstance().addRoomNameDialog(null);
		}
		
	}
	
}
