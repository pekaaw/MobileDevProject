package hig.imt3672.knowthisroom;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DetailFragment extends Fragment {
	
	DBRoomEntry m_room;
	View m_view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		m_view = inflater.inflate(R.layout.detailed_room, container, false);
		return m_view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if( m_room != null ) {
			TextView nameView = (TextView) getView().findViewById(R.id.detailed_room_name);
			nameView.setText( m_room.getName() );
			
			Button delete_button = (Button) getView().findViewById(R.id.delete_button);
			delete_button.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					//getBaseActivity().deleteRoomDialog(view, m_room);
					
				}
			});
			super.onActivityCreated(savedInstanceState);
		}
	}

	public void setRoom(DBRoomEntry room) {
		m_room = room;
	}

}
