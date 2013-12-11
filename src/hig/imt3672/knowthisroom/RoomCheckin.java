package hig.imt3672.knowthisroom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class RoomCheckin {
	DBRoomEntry mRegisteredRoom;
	
	static RoomCheckin mInstance;
	Context mContext;
	int Invalid;
	
	CellTowerData Tower;
	WifiSensor WifiManager;
	List<ScanResult> Wifis;
	
	Integer MAX_ALGORITHM_ENQUIRIES = 15;
	
	double SIGNAL_UPPER_MARGIN = 5.0;
	double SIGNAL_LOWER_MARGIN = 5.0;

	double local_upper_margin;
	double local_lower_margin;
	double room_margin = 0.35;
	DBOperator Db;

	List<DBCelltowerEntry> Celltowers;
	List<DBWifiInRoomEntry> DBWifis;

	long roomTowerId;
	long roomTowerMin;
	long roomTowerMax;

	//Singleton get instance
	public static RoomCheckin getInstance() {
		if(mInstance == null) {
			Log.d("RoomCheckin","You have not created an instance.");
			return null;
		}
		Log.d("RoomCheckin","Obtained instance.");
		return mInstance;
	}
	
	//Since we require a context we create our instance separately
	public static void createInstance(Context context) {
		if(mInstance != null) {
			Log.d("RoomCheckin","An instance already exists.");
			return;
		}
		mInstance = new RoomCheckin(context);
		Log.d("RoomCheckin","Created instance.");
	}
	
	//Constructor private so we cannot create a new instance outside of createInstance,
	//which will only create it once.
	private RoomCheckin(Context context) {
		Invalid = 1024;	//Setting a constant, unattainable value.
		
		mContext = context.getApplicationContext();
		WifiManager = WifiSensor.getInstance();
		Db = DBOperator.getInstance();
	}

	//Filters out rooms based on celltower data
	public List<DBRoomEntry> GetRooms() {
		Tower = ServiceHandler.getInstance().m_CellTowerHander.mCellTowerData;
		Bundle Towerinfo = Tower.getCellTowerBundle();
		Integer towerId = Towerinfo.getInt("CellID");
		Integer towerStrength = Towerinfo.getInt("Strength");
		Integer towerNoise = Towerinfo.getInt("cellNoise");
		
		Log.d("Checkin","Tower is " + Integer.toString( towerId ) 
				+ " with strength " + Integer.toString( towerStrength )
						+ ", " + Integer.toString( towerNoise ) + " noise."
						);
		
		List<DBRoomEntry> Rooms = Db.getAllDBRoomEntries();
		int i = 0;

		if (Rooms.size() < 1) {
			return null;
		}

		while (i < Rooms.size()) {
			long roomId = Rooms.get(i).getId();
			Celltowers = Db.getCellTowers(roomId);

			for (int j = 0; j < Celltowers.size(); j++) {
				roomTowerId = Celltowers.get(j).getTowerId();
				roomTowerMin = Celltowers.get(j).getMin();
				roomTowerMax = Celltowers.get(j).getMax();
				if (towerId == roomTowerId 
						&& (towerStrength+towerNoise) >= roomTowerMax
						&& (towerStrength-towerNoise) <= roomTowerMin) {
					break;
				}
			}

			// at this point we either have the last room or a room that
			// gave match (have at least one matching celltower). If we
			// don't pass this test the room gets removed, but if we pass
			// it, then the while-loop will check next item.
			// By removing like this we will at the end only have left
			// the rooms that had matching celltowers in range.
			// !!NOTE!! lower number means stronger signal !!NOTE!!
			if (towerId == roomTowerId 
					&& (towerStrength+towerNoise) >= roomTowerMax
					&& (towerStrength-towerNoise) <= roomTowerMin) {
				i++;
			} else {
				Rooms.remove(i);
			}
		}
		return Rooms;
	}

	//Filters out a list of rooms to leave it with 0 or 1 rooms
	public DBRoomEntry GetRoom(List<DBRoomEntry> RoomEntries) {
		
		// If input it not valid, return an empty DBRoomEntry
		if( RoomEntries == null )
		{
			return null;
			//return new DBRoomEntry();
		}
		
		Log.d("#GetRoom#","Rooms found: " + RoomEntries.size() );
		
		String dbBSSID;
		String myBSSID;
		Long dbWifiMax;
		Long dbWifiMin;
		Integer myWifiLevel;
		Integer algoritmEnquiryCounter = 0;
				
		local_upper_margin = SIGNAL_UPPER_MARGIN;
		local_lower_margin = SIGNAL_UPPER_MARGIN;
		
		int valid = 0;
		Wifis = WifiManager.GetNetworks();
		List<ScanResult> presentWifiList = new ArrayList<ScanResult>();
		presentWifiList.addAll(Wifis);

		//If no rooms exist at all we'll just return
		if(RoomEntries.size() < 1) {
					return null;
		}
		
		//If more than one rooms exist we will loop through, halving the acceptable margin
		//until presumably only 1 room remain
		do {
			int i = 0;
			
			//We loop through the rooms. Could not use a "for" loop as the contents changes
			//its indexes as we remove rooms from our check.
			while (i != RoomEntries.size()) {
				long roomId = RoomEntries.get(i).getId();
				DBWifis = Db.getWifi(roomId);

				// Find valid wifi's that fits the data in db
				//And we can begin to compare wifis... We first test to see 
				//if it's between "minimum" and "maximum" values
				for (int j = 0; j < DBWifis.size(); j++) {
					for (int k = 0; k < presentWifiList.size(); k++) {
						
						// Get BSSID's to compare
						dbBSSID = DBWifis.get(j).getId();
						myBSSID = presentWifiList.get(k).BSSID;
						
						if ( dbBSSID.equals( myBSSID ) ) {

							// Get different signal strengths
							dbWifiMin = DBWifis.get(j).getMin();
							dbWifiMax = DBWifis.get(j).getMax();
							myWifiLevel = presentWifiList.get(k).level;

							//If the wifi is valid its value remains "Invalid" internally
							//so we don't check it again later, thus it cannot add "valid++" twice.

							Boolean withinUpperLimit = myWifiLevel >= dbWifiMin - local_lower_margin;
							Boolean withinLowerLimit = myWifiLevel <= dbWifiMax + local_upper_margin;

							if ( withinUpperLimit && withinLowerLimit ) {
								valid++;
								
								// We found a wifi from DB in this place, don't look for more
								k = presentWifiList.size();
								continue;
							}
							
							// We found a wifi from DB in this place, don't look for more
							k = presentWifiList.size();

						} // end if equal BSSID
					} // end for presentWifis
				} // end for dbWifis

				//Since our loop removes the elements we loop through,
				//we don't want to jump to "next" loop if a room has been removed
				//as we have moved the "next" to the position of the "current"
				float prosentageValid = (float) valid / (float) DBWifis.size();
				float prosentageValidLimit = 1 - (float) room_margin;
				if ( prosentageValid > prosentageValidLimit ) {
					i++;
				} else {
					RoomEntries.remove(i);
				}
			}

			//If we have more than one room we raise the bar of accepted margin
			if (RoomEntries.size() > 1) {
				local_upper_margin = local_upper_margin * 0.9;
				local_lower_margin = local_lower_margin * 0.9;
				room_margin = room_margin * 0.99;
			}
			
			// ensure that the algorithm don't run wild
			if( algoritmEnquiryCounter >= MAX_ALGORITHM_ENQUIRIES ) {
				// the algorithm can't decide, exit function with null
				return null;
			}
			algoritmEnquiryCounter += 1;
			
		} while(RoomEntries.size() > 1);
		
		//If we have no hits we're done here.
		if(RoomEntries.size() < 1) {
			return null;
		}
		
		//Test that the found room is a different room.
		if(RoomEntries.get(0) != mRegisteredRoom) {
			mRegisteredRoom = RoomEntries.get(0);
		}
		
		return mRegisteredRoom;
	}
}
