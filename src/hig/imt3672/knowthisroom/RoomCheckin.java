
package hig.imt3672.knowthisroom;

import java.util.List;

import android.net.wifi.ScanResult;

public class RoomCheckin {
	WifiSensor WifiManager;
	List<ScanResult> Wifis;
	
	double level_margin;
	double room_margin;
	int[] difference;
	DBOperator Db;
	
	List<DBRoomEntry> Rooms;
	List<DBCelltowerEntry> Celltowers;
	List<DBWifiInRoomEntry> DBWifis;
	
	long roomTowerId;
	long roomTowerStrength;
	
	RoomCheckin() {
		WifiManager = WifiSensor.getInstance();
		Db = DBOperator.getInstance();
		
		level_margin = 2.0;
		room_margin = 0.2;
	}
	
	public List<DBRoomEntry> GetRooms(long towerId, long towerStrength) {
		Rooms = Db.getAllDBRoomEntries();
		for(int i = 0; i < Rooms.size(); i++) {
			long roomId = Rooms.get(i).getId();
			Celltowers = Db.getCellTowers(roomId);
			
			for(int j=0; j < Celltowers.size(); j++) {
				roomTowerId = Celltowers.get(j).getId();
				roomTowerStrength = Celltowers.get(j).getStr();
				if(towerId == roomTowerId && towerStrength == roomTowerStrength) {
					break;
				}
			}
			
			if(towerId == roomTowerId && towerStrength == roomTowerStrength) {
				continue;
			}
			else {
				Rooms.remove(i);
			}
		}
		return Rooms;
	}
	
	public DBRoomEntry GetRoom(List<DBRoomEntry> RoomEntries) {
		int valid = 0;
		Wifis = WifiManager.GetNetworks();
		
		do {
			for(int i = 0; i < RoomEntries.size(); i++) {
				long roomId = Rooms.get(i).getId();
				DBWifis = Db.getWifi(roomId);
				
				difference = new int[DBWifis.size()];
				for(int j = 0; j < DBWifis.size(); j++) {
					//We set the default value to an arbitrary value that it can never be naturally.
					difference[j] = 1024;
				}
				
				for(int j = 0; j < DBWifis.size(); j++) {
					for(int k = 0; k < Wifis.size(); k++) {
						if(DBWifis.get(j).getId() == Wifis.get(k).BSSID) {
							difference[j] = (int) (DBWifis.get(j).getStr() - Wifis.get(k).frequency);
						}
					}
				}
				
				for(int j = 0; j < DBWifis.size(); j++) {
					if(difference[j] == 1024) {continue;}
					if(difference[j] < level_margin &&
					   difference[j] > level_margin*-1) {valid++; continue;}
				}
				
				if(valid/DBWifis.size() > (1-room_margin)) {continue;}
				else {RoomEntries.remove(i);}
			}
			
			if(RoomEntries.size() <= 1) {
				level_margin = level_margin/2;
				room_margin = room_margin/2;
			}
		} while(RoomEntries.size() <= 1);
		return RoomEntries.get(0);
	}
}
