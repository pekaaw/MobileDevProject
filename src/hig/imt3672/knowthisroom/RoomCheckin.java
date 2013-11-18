/*
package hig.imt3672.knowthisroom;

import java.util.List;

public class RoomCheckin {
	double level_margin;
	double room_margin;
	int size;
	int[] difference;
	int valid;
	
	RoomCheckin() {
		level_margin = 2.0;
		room_margin = 0.8;
		size = Db.size();
		difference = new int[size];
		for(int i = 0; i < size; i++) {
			//We set the default value to an arbitrary value that it can never be naturally.
			difference[i] = 1024;
			valid = 0;
		}
	}
	
	public void Checkin(long towerId, long towerStrength) {
		
		//Rooms
		for(int i = 0; i < Db.size()) {
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
}
*/