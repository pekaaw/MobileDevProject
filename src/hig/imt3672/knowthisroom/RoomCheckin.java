package hig.imt3672.knowthisroom;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.util.Log;

public class RoomCheckin {
	DBRoomEntry mRegisteredRoom;

	static RoomCheckin mInstance;
	Context mContext;
	int Invalid;

	CellTowerData Tower;
	WifiSensor WifiManager;
	List<ScanResult> Wifis;

	Integer MAX_ALGORITHM_ENQUIRIES = 15;

	static double CELL_UPPER_MARGIN = 1;
	static double CELL_LOWER_MARGIN = 1;
	static double SIGNAL_UPPER_MARGIN = 10;
	static double SIGNAL_LOWER_MARGIN = 10;

	Boolean find_room_locked = false;
	double local_upper_margin;
	double local_lower_margin;
	static double ROOM_MARGIN_INIT_VALUE = 0.60;
	double room_margin;
	DBOperator Db;

	List<DBCelltowerEntry> Celltowers;
	List<DBWifiInRoomEntry> DBWifis;

	long roomTowerId;
	long roomTowerWeakest;
	long roomTowerStrongest;

	// Singleton get instance
	public synchronized static RoomCheckin getInstance() {
		if (mInstance == null) {
			Log.d("RoomCheckin", "You have not created an instance.");
			return null;
		}
		Log.d("RoomCheckin", "Obtained instance.");
		return mInstance;
	}

	// Since we require a context we create our instance separately
	public synchronized static void createInstance(Context context) {
		if (mInstance != null) {
			Log.d("RoomCheckin", "An instance already exists.");
			return;
		}
		mInstance = new RoomCheckin(context);
		Log.d("RoomCheckin", "Created instance.");
	}

	// Constructor private so we cannot create a new instance outside of
	// createInstance,
	// which will only create it once.
	private RoomCheckin(Context context) {
		Invalid = 1024; // Setting a constant, unattainable value.

		mContext = context.getApplicationContext();
		WifiManager = WifiSensor.getInstance();
		Db = DBOperator.getInstance();
	}

	// Filters out rooms based on celltower data
	public List<DBRoomEntry> GetRooms() {

		if (find_room_locked) {
			return null;
		}

		find_room_locked = true;

		Tower = ServiceHandler.getInstance().m_CellTowerHander.mCellTowerData;
		Bundle Towerinfo = Tower.getCellTowerBundle();
		Integer towerId = Towerinfo.getInt("CellID");
		Integer towerStrength = Towerinfo.getInt("Strength");

		Log.d("Checkin", "Tower is " + Integer.toString(towerId)
				+ " with strength " + Integer.toString(towerStrength) + ".");

		List<DBRoomEntry> Rooms = Db.getAllDBRoomEntries();

		int i = 0;

		if (Rooms.size() < 1) {
			find_room_locked = false;
			return null;
		}

		Log.d("#GetRooms#", " ");

		while (i < Rooms.size()) {
			Boolean found = false;
			int a = 0;
			long roomId = Rooms.get(i).getId();
			Celltowers = Db.getCellTowers(roomId);
			for (int c = 0; c < Celltowers.size(); c++) {
				Log.d("#GetRooms#",
						Rooms.get(i).getName() + ":  "
								+ Long.toString(Celltowers.get(c).getTowerId()));
			}

			for (int j = 0; j < Celltowers.size(); j++) {
				roomTowerId = Celltowers.get(j).getTowerId();
				roomTowerWeakest = Celltowers.get(j).getMin();
				roomTowerStrongest = Celltowers.get(j).getMax();
				if (towerId == roomTowerId
						// in this test, lower values is strongest
						&& towerStrength >= roomTowerStrongest
								- CELL_UPPER_MARGIN
						&& towerStrength <= roomTowerWeakest
								+ CELL_LOWER_MARGIN) {
					found = true;
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
			if (found) {
				i++;
			} else {
				Rooms.remove(i);
			}
		}
		return Rooms;
	}

	// Filters out a list of rooms to leave it with 0 or 1 rooms
	public synchronized DBRoomEntry GetRoom(List<DBRoomEntry> RoomEntries) {

		// If no rooms exist at all we'll just return
		// If input it not valid, return an empty DBRoomEntry
		if (RoomEntries == null || RoomEntries.size() < 1) {
			find_room_locked = false;
			return null;
			// return new DBRoomEntry();
		}

		Log.d("#GetRoom#", " ");
		Log.d("#GetRoom#", "Rooms found: " + RoomEntries.size());
		for (int c = 0; c < RoomEntries.size(); c++) {
			Log.d("#GetRoom#", RoomEntries.get(c).getName());
		}

		String dbBSSID;
		String myBSSID;
		Long dbWifiMax;
		Long dbWifiMin;
		Integer myWifiLevel;
		Integer algoritmEnquiryCounter = 0;

		local_upper_margin = SIGNAL_UPPER_MARGIN;
		local_lower_margin = SIGNAL_UPPER_MARGIN;
		room_margin = ROOM_MARGIN_INIT_VALUE;
		int valid = 0;
		try {
			Wifis = WifiManager.GetNetworks();
		} catch (Exception e) {
			Log.d("WIfiManager", "Trouble getting Networks man!");
		}

		if (Wifis == null) {
			Log.d("No wifis", "There is noo wifis returned.");
			find_room_locked = false;
			return null;
		}

		List<ScanResult> presentWifiList = new ArrayList<ScanResult>();
		presentWifiList.addAll(Wifis);

		// If more than one rooms exist we will loop through, halving the
		// acceptable margin
		// until presumably only 1 room remain
		do {
			int i = 0;

			// We loop through the rooms. Could not use a "for" loop as the
			// contents changes
			// its indexes as we remove rooms from our check.
			while (i < RoomEntries.size()) {
				valid = 0;
				long roomId = RoomEntries.get(i).getId();
				DBWifis = Db.getWifi(roomId);

				// Find valid wifi's that fits the data in db
				// And we can begin to compare wifis... We first test to see
				// if it's between "minimum" and "maximum" values
				for (int j = 0; j < DBWifis.size(); j++) {
					for (int k = 0; k < presentWifiList.size(); k++) {

						// Get BSSID's to compare
						dbBSSID = DBWifis.get(j).getId();
						myBSSID = presentWifiList.get(k).BSSID;

						if (dbBSSID.equals(myBSSID)) {

							// Get different signal strengths
							dbWifiMin = DBWifis.get(j).getMin();
							dbWifiMax = DBWifis.get(j).getMax();
							myWifiLevel = presentWifiList.get(k).level;

							// If the wifi is valid its value remains "Invalid"
							// internally
							// so we don't check it again later, thus it cannot
							// add "valid++" twice.

							Boolean withinUpperLimit = myWifiLevel >= dbWifiMin
									- local_lower_margin;
							Boolean withinLowerLimit = myWifiLevel <= dbWifiMax
									+ local_upper_margin;

							if (withinUpperLimit && withinLowerLimit) {
								valid++;
							}

							// We found a wifi from DB in this place, don't look
							// for more
							k = presentWifiList.size();

						} // end if equal BSSID
					} // end for presentWifis
				} // end for dbWifis

				// Since our loop removes the elements we loop through,
				// we don't want to jump to "next" loop if a room has been
				// removed
				// as we have moved the "next" to the position of the "current"
				float prosentageValid = (float) valid / (float) DBWifis.size();
				float prosentageValidLimit = 1 - (float) room_margin;
				if (prosentageValid > prosentageValidLimit) {
					i++;

//					Log.d("#RoomCheckin#", "Cellsignals fit that of the registered room.");
				} else {
//					Log.d("#RoomCheckin#", RoomEntries.get(i).getName() + " is removed.");
					RoomEntries.remove(i);
				}
			}

			// If we have more than one room we raise the bar of accepted margin
			local_upper_margin = local_upper_margin * 0.99;
			local_lower_margin = local_lower_margin * 0.99;
			room_margin = room_margin * 1.0001;

			// ensure that the algorithm don't run wild
			// if (algoritmEnquiryCounter >= MAX_ALGORITHM_ENQUIRIES) {
			// // the algorithm can't decide, exit function with null
			// Log.d("#RoomCheckin#", "Algorithm ran for too long.");
			// return null;
			// }
			algoritmEnquiryCounter += 1;
//			Log.d("#RoomCheckin#", "Room_Margin: " + Double.toString(room_margin));
//			Log.d("#RoomCheckin#", "Rooms left: " + Integer.toString(RoomEntries.size()));

		} while (RoomEntries.size() > 1);

		// If we have no hits we're done here.
		if (RoomEntries.size() < 1) {
			Log.d("#RoomCheckin#",
					"Algorithm run for "
							+ Integer.toString(algoritmEnquiryCounter)
							+ " times.");
			Log.d("#RoomCheckin#", "Return null.");
			Log.d("#RoomCheckin#", " ");
			find_room_locked = false;
			return null;
		}

		// Test that the found room is a different room.
		if (RoomEntries.get(0) != mRegisteredRoom) {
			mRegisteredRoom = RoomEntries.get(0);
		}

		Log.d("#RoomCheckin#",
				"Algorithm run for " + Integer.toString(algoritmEnquiryCounter)
						+ " times.");
		Log.d("#RoomCheckin#", "Found room: " + mRegisteredRoom.getName());
		Log.d("#RoomCheckin#", " ");

		find_room_locked = false;

		return mRegisteredRoom;
	}
}
