package hig.imt3672.knowthisroom;

import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CorrectRoomActivity extends Activity implements OnClickListener {

	List<DBRoomEntry> list_of_rooms;
	ArrayAdapter<DBRoomEntry> adapter_room_list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView( R.layout.correct_room );
		
		list_of_rooms = DBOperator.getInstance().getAllDBRoomEntries();
		adapter_room_list = new ArrayAdapter<DBRoomEntry>(this,
				android.R.layout.simple_list_item_1, list_of_rooms);
		
		findViewById( R.id.add_room ).setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		ListView listView = (ListView) findViewById(R.id.current_rooms);
		listView.setAdapter(adapter_room_list);
		
		listView.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id ) {
				
				DBRoomEntry room = (DBRoomEntry) parent
						.getItemAtPosition(position);
				
//				FragmentManager manager = getFragmentManager();
//				UpdateRoomDialog updateRoomDialog = UpdateRoomDialog.newInstance(
//						room, position);
//				updateRoomDialog.show(manager, "update_room_dialog_id");

				MainActivity.getInstance().updateRoomDialog(view, room, position);
//				MainActivity.getInstance().detailedRoomDialog( view, room, position );
			}
		});
	}

	@Override
	public void onClick(View v) {
		if( v.getId() == R.id.add_room ) {
			MainActivity.getInstance().addRoomNameDialog(v);
		}
	}
	
}
