
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
			}
			
			if(RoomEntries.size() <= 1) {
				level_margin = level_margin/2;
				room_margin = room_margin/2;
			}
		} while(RoomEntries.size() <= 1);
		return null;
	}
	
	/*
	public void Checkin(long towerId, long towerStrength) {
		
		//Rooms
		for(int i = 0; i < size; i++) {
			//Skip rooms that don't match the towerId
			if(Db.room[i].towerId != towerId) {continue;}
			
			//Skip rooms with different celltower strength.
			if(Db.room[i].towerStrength != towerStrength) {continue;}
			
			//Browse room networks
			for(int j = 0; j < Db.room[i].networks.size(); j++) {
				
				//Find if the room network is in results
				for(int k = 0; k < networks.size(); k++) {
					//Skip to next result if the BSSID isn't the correct one
					if(Db.room[i].networks.get(j).BSSID != networks.get(k).BSSID) {continue;} 
					
					difference[j] = Db.room[i].networks.get(j).level - networks.get(k).level;
				}
				
				
			}
			
			//Check if room networks are within valid range (or exist at all) 
			for(int j = 0; j < size; j++) {
				if(difference[j] == 1024) {continue;}
				if(difference[j] < level_margin &&
				   difference[j] > level_margin*-1) {valid++; continue;}
			}
			
			//Check if the room is compatible with the results. If yes return.
			if(valid/size > room_margin) {return room;}
		}
	}
	*/
}
