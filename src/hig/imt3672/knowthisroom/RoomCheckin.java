package hig.imt3672.knowthisroom;

import java.util.List;

import android.net.wifi.ScanResult;

public class RoomCheckin {
	WifiSensor WifiManager;
	List<ScanResult> Wifis;

	double level_margin;
	double room_margin;
	int[] differenceMin;
	int[] differenceMax;
	DBOperator Db;

	List<DBCelltowerEntry> Celltowers;
	List<DBWifiInRoomEntry> DBWifis;

	long roomTowerId;
	long roomTowerMin;
	long roomTowerMax;

	RoomCheckin() {
		WifiManager = WifiSensor.getInstance();
		Db = DBOperator.getInstance();

		level_margin = 2.0;
		room_margin = 0.2;
	}

	public List<DBRoomEntry> GetRooms(long towerId, long towerStrength) {
		List<DBRoomEntry> Rooms = Db.getAllDBRoomEntries();
		int i = 0;

		if (Rooms.size() < 1) {
			return null;
		}

		while (i < Rooms.size()) {
			long roomId = Rooms.get(i).getId();
			Celltowers = Db.getCellTowers(roomId);

			for (int j = 0; j < Celltowers.size(); j++) {
				roomTowerId = Celltowers.get(j).getId();
				roomTowerMin = Celltowers.get(j).getMin();
				roomTowerMax = Celltowers.get(j).getMax();
				if (towerId == roomTowerId && towerStrength <= roomTowerMax
						&& towerStrength >= roomTowerMin) {
					break;
				}
			}

			if (towerId == roomTowerId && towerStrength <= roomTowerMax
					&& towerStrength >= roomTowerMin) {
				i++;
			} else {
				Rooms.remove(i);
			}
		}
		return Rooms;
	}

	public DBRoomEntry GetRoom(List<DBRoomEntry> RoomEntries) {
		int valid = 0;
		Wifis = WifiManager.GetNetworks();

		if (RoomEntries.size() < 1) {
			return null;
		}

		do {
			int i = 0;
			while (i != RoomEntries.size()) {
				long roomId = RoomEntries.get(i).getId();
				DBWifis = Db.getWifi(roomId);

				differenceMin = new int[DBWifis.size()];
				differenceMax = new int[DBWifis.size()];
				for (int j = 0; j < DBWifis.size(); j++) {
					// We set the default value to an arbitrary value that it
					// can never be naturally.
					differenceMin[j] = 1024;
					differenceMax[j] = 1024;
				}

				for (int j = 0; j < DBWifis.size(); j++) {
					for (int k = 0; k < Wifis.size(); k++) {
						if (DBWifis.get(j).getId() == Wifis.get(k).BSSID) {
							differenceMin[j] = (int) (DBWifis.get(j).getMin() - Wifis
									.get(k).level);
							differenceMax[j] = (int) (DBWifis.get(j).getMax() - Wifis
									.get(k).level);
						}
					}
				}

				for (int j = 0; j < DBWifis.size(); j++) {
					if (differenceMin[j] == 1024) {
						continue;
					}
					if (differenceMin[j] < level_margin
							&& differenceMin[j] > level_margin * -1) {
						valid++;
						continue;
					}
					if (differenceMax[j] == 1024) {
						continue;
					}
					if (differenceMax[j] < level_margin
							&& differenceMax[j] > level_margin * -1) {
						valid++;
						continue;
					}
				}

				if (valid / DBWifis.size() > (1 - room_margin)) {
					i++;
				} else {
					RoomEntries.remove(i);
				}
			}

			if (RoomEntries.size() <= 1) {
				level_margin = level_margin / 2;
				room_margin = room_margin / 2;
			}
		} while (RoomEntries.size() <= 1);
		return RoomEntries.get(0);
	}
}
