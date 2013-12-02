package hig.imt3672.knowthisroom;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DetailsAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<DBWifiInRoomEntry> mWifiList;
	
	public DetailsAdapter(Context context, List<DBWifiInRoomEntry> wifiList) {
		this.mContext = context;
		this.mWifiList = wifiList;
	}
	
	/*private view holder class*/
	private class ViewHolder {
		TextView bsid;
		TextView strenght;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		
		if( convertView == null ) {
			convertView = inflater.inflate(R.layout.wifi_info, null);
			holder = new ViewHolder();
			holder.bsid = (TextView) convertView.findViewById(R.id.wifi_info_bsid);
			holder.strenght = (TextView) convertView.findViewById(R.id.wifi_info_strength);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		DBWifiInRoomEntry wifiListEntry = (DBWifiInRoomEntry) getItem(position);
		
		String strengthSpan = Long.toString( - wifiListEntry.getMin() ) + 
				Long.toString( wifiListEntry.getMax() ) + " -dBm";
		
		holder.bsid.setText( wifiListEntry.getId() );
		holder.strenght.setText( strengthSpan );
		
		return convertView;
	}

	@Override
	public int getCount() {
		return mWifiList.size();
	}

	@Override
	public Object getItem(int position) {
		return mWifiList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mWifiList.indexOf( getItem(position) );
	}

}
