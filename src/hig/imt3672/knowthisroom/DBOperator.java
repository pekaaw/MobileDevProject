package hig.imt3672.knowthisroom;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.os.Bundle;

public class DBOperator { // Handles normal usage of the database

	// Database fields
	private SQLiteDatabase database;
	private ExtendedSQLLiteHelper dbHelper;
	private String[] allRooms = { ExtendedSQLLiteHelper.ROOM_COLUMN_ID,
			ExtendedSQLLiteHelper.ROOM_COLUMN_NAME };
	private String[] roomsById = { ExtendedSQLLiteHelper.ROOM_COLUMN_ID,
			ExtendedSQLLiteHelper.ROOM_COLUMN_NAME };

	public DBOperator(Context context) {
		dbHelper = new ExtendedSQLLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	// :::::::::::::LIST TYPE SPESIFIC BEGIN::::::::::::::::::::::
	private boolean insertWifi(String wifiBSID, long roomId, long strength) {
		ContentValues wifiValues = new ContentValues();
		wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID, roomId);
		// <-is as it should be

		wifiValues
				.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_WIFI_ID, wifiBSID);
		// <-NEEDS ID FROM ACTUAL WIFI (BSID)

		wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MAX, strength);
		// <-set a default value?

		wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MIN, strength);
		// <-set a default value?

		/* long wifiInsertId = */
		database.insert(ExtendedSQLLiteHelper.WIFI_ROOM_TABLE, null, wifiValues);
		return true;
	}

	private boolean insertCell(long towerId, long roomId, long strength) {
		ContentValues cellValues = new ContentValues();
		cellValues.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID, roomId);
		// <-is as it should be

		cellValues
				.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_TOWER_ID, towerId);
		// <-NEEDS ID FROM ACTUAL TOWER

		cellValues.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MAX, strength);
		// <-set a default value?

		cellValues.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MIN, strength);
		// <-set a default value?

		/* long cellInsertId = */
		database.insert(ExtendedSQLLiteHelper.CELLTOWER_TABLE, null, cellValues);
		return true;
	}

	public DBRoomEntry createRoom(String room, Bundle cellTowerBundle) {
		ContentValues roomValues = new ContentValues();
		roomValues.put(ExtendedSQLLiteHelper.ROOM_COLUMN_NAME, room);
		long insertId = database.insert(ExtendedSQLLiteHelper.ROOM_TABLE, null,
				roomValues);
		Cursor cursor = database.query(ExtendedSQLLiteHelper.ROOM_TABLE,
				allRooms, ExtendedSQLLiteHelper.ROOM_COLUMN_ID + " = "
						+ insertId, null, null, null, null);

		// // for each wifi {
		// WifiSensor wifi = new WifiSensor();
		// List<ScanResult> networks = wifi.GetNetworks();
		//
		// for (int i = 0; i <= networks.size(); i++) {
		// insertWifi(networks.get(i).BSSID, insertId, networks.get(i).level);
		// }

		// }

		// for each celltower {
		long cellTowerId = cellTowerBundle.getInt("CellID");
		long towerStrength = cellTowerBundle.getInt("Strength");
		insertCell(cellTowerId, insertId, towerStrength);
		// }

		cursor.moveToFirst();
		DBRoomEntry newRoom = cursorToDBRoomEntry(cursor);
		cursor.close();
		return newRoom;
	}

	public void deleteRoom(DBRoomEntry room) {
		long id = room.getId();
		System.out.println("Room deleted with id: " + id);
		database.delete(ExtendedSQLLiteHelper.ROOM_TABLE,
				ExtendedSQLLiteHelper.ROOM_COLUMN_ID + " = " + id, null);

		database.delete(ExtendedSQLLiteHelper.CELLTOWER_TABLE,
				ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID + " = " + id,
				null);

		database.delete(ExtendedSQLLiteHelper.WIFI_ROOM_TABLE,
				ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID + " = " + id,
				null);
	}

	public void updateRoom(DBRoomEntry room) {
		WifiSensor wifi = new WifiSensor();
		List<ScanResult> networks = wifi.GetNetworks();

		for (int i = 0; i <= networks.size(); i++) {
			updateWifi(networks.get(i).BSSID, room.getId(),
					networks.get(i).level);
		}

	}

	public boolean updateWifi(String BSID, long roomId, int signalStrenght) {

		// CHECK IF THE WIFI EXISTS BEFORE YOU RUN THIS FUNCTION

		ContentValues cellValues = new ContentValues();
		// Database sql get-statements
		String WIFI_GET_MAX = ("SELECT "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MAX + " FROM "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_TABLE + " WHERE "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_WIFI_ID + " = "
				+ " ? AND " + ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID + " = ?");

		String WIFI_GET_MIN = "SELECT "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MIN + " FROM "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_TABLE + " WHERE "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_WIFI_ID + " = ?"
				+ " AND " + ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID
				+ " = ?";

		String GET_CELLTOWER = ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_WIFI_ID
				+ " = ? AND " + ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID
				+ " = ?";

		Cursor cursor = database.rawQuery(WIFI_GET_MAX, new String[] { BSID,
				String(roomId) });
		long DBmax = cursorToWifiStr(cursor);
		cursor = database.rawQuery(WIFI_GET_MIN, new String[] { BSID,
				String(roomId) });
		long DBmin = cursorToWifiStr(cursor);

		if (signalStrenght > DBmax) {
			cellValues.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MAX,
					signalStrenght);
		} else if (signalStrenght < DBmin) {

			cellValues.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MIN,
					signalStrenght);
		} else {
			return false;
		}
		database.update(ExtendedSQLLiteHelper.CELLTOWER_TABLE, cellValues,
				GET_CELLTOWER, new String[] { BSID, String(roomId) });
		return true;
	}

	public boolean updateCell(DBRoomEntry room, long cellId, long signalStrenght) {

		// CHECK IF THE CELLTOWER EXISTS BEFORE YOU RUN THIS FUNCTION

		ContentValues cellValues = new ContentValues();
		long roomId = room.getId();
		// Database sql get-statements
		String CELLTOWER_GET_MAX = ("SELECT "
				+ ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MAX + " FROM "
				+ ExtendedSQLLiteHelper.CELLTOWER_TABLE + " WHERE "
				+ ExtendedSQLLiteHelper.CELLTOWER_COLUMN_TOWER_ID + " = "
				+ " ? AND " + ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID + " = ?");

		String CELLTOWER_GET_MIN = "SELECT "
				+ ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MIN + " FROM "
				+ ExtendedSQLLiteHelper.CELLTOWER_TABLE + " WHERE "
				+ ExtendedSQLLiteHelper.CELLTOWER_COLUMN_TOWER_ID + " = ?"
				+ " AND " + ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID
				+ " = ?";

		String GET_CELLTOWER = ExtendedSQLLiteHelper.CELLTOWER_COLUMN_TOWER_ID
				+ " = ? AND " + ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID
				+ " = ?";

		Cursor cursor = database.rawQuery(CELLTOWER_GET_MAX, new String[] {
				String(cellId), String(roomId) });
		long DBmax = cursorToCellStr(cursor);
		cursor = database.rawQuery(CELLTOWER_GET_MIN, new String[] {
				String(cellId), String(roomId) });
		long DBmin = cursorToCellStr(cursor);

		if (signalStrenght > DBmax) {
			cellValues.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MAX,
					signalStrenght);
		} else if (signalStrenght < DBmin) {

			cellValues.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MIN,
					signalStrenght);
		} else {
			return false;
		}
		database.update(ExtendedSQLLiteHelper.CELLTOWER_TABLE, cellValues,
				GET_CELLTOWER, new String[] { String(cellId), String(roomId) });
		return true;
	}

	private String String(long roomId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<DBRoomEntry> getAllDBRoomEntries() {
		List<DBRoomEntry> rooms = new ArrayList<DBRoomEntry>();

		Cursor cursor = database.query(ExtendedSQLLiteHelper.ROOM_TABLE,
				allRooms, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DBRoomEntry room = cursorToDBRoomEntry(cursor);
			rooms.add(room);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return rooms;
	}

	// :::::::::::::::Cursors::::::::::::::::::::::::::::
	private long cursorToID(Cursor cursor) {
		return cursor.getLong(0);
	}

	private long cursorToCellStr(Cursor cursor) {
		return cursor.getLong(1);
	}

	private long cursorToWifiStr(Cursor cursor) {
		return cursor.getLong(2);
	}

	// :::::::cursor for rooms::::::::::
	private DBRoomEntry cursorToDBRoomEntry(Cursor cursor) {
		DBRoomEntry room = new DBRoomEntry();
		room.setId(cursor.getLong(0));
		room.setName(cursor.getString(1));
		return room;
	}

	// ::::::::cursor for towers:::::::::::
	public List<DBCelltowerEntry> getCellTowers(long roomId) {
		List<DBCelltowerEntry> returnList = new ArrayList<DBCelltowerEntry>();

		String whereStatement = (ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID
				+ "=" + String(roomId));

		Cursor cursor = database.query(ExtendedSQLLiteHelper.CELLTOWER_TABLE,
				null, whereStatement, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DBCelltowerEntry tower = cursorToDBCelltowerEntry(cursor);
			returnList.add(tower);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();

		return returnList;

	}

	private DBCelltowerEntry cursorToDBCelltowerEntry(Cursor cursor) {
		DBCelltowerEntry tower = new DBCelltowerEntry();
		tower.setId(cursor.getLong(0));
		tower.setStr(cursor.getLong(1));
		tower.setRoom(cursor.getLong(2));
		return tower;
	}

	// :::::::::cursor for rooms::::::::::
	public List<DBWifiInRoomEntry> getWifi(long roomId) {
		List<DBWifiInRoomEntry> returnList = new ArrayList<DBWifiInRoomEntry>();

		String whereStatement = (ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID
				+ "=" + String(roomId));

		Cursor cursor = database.query(ExtendedSQLLiteHelper.CELLTOWER_TABLE,
				null, whereStatement, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DBWifiInRoomEntry wifi = cursorToDBWifiInRoomEntry(cursor);
			returnList.add(wifi);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();

		return returnList;

	}

	private DBWifiInRoomEntry cursorToDBWifiInRoomEntry(Cursor cursor) {
		DBWifiInRoomEntry wifi = new DBWifiInRoomEntry();
		wifi.setId(cursor.getString(0));
		wifi.setStr(cursor.getLong(1));
		wifi.setRoom(cursor.getLong(2));
		return wifi;
	}

	// :::::::::::LIST TYPE SPESIFIC END:::::::::::::::
}
