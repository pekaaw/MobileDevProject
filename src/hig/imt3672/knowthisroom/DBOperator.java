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
import android.util.Log;

public class DBOperator { // Handles normal usage of the database
	// singleton
	static DBOperator mInstance;

	// Database fields
	private SQLiteDatabase database;
	private ExtendedSQLLiteHelper dbHelper;
	private String[] allRooms = { ExtendedSQLLiteHelper.ROOM_COLUMN_ID,
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

	// ::::::::::::Singleton:::::::::::::::::::::::
	public static DBOperator getInstance() {
		if (mInstance == null) {
			Log.d("DBOperator", "You have not created an instance.");
			return null;
		}
		return mInstance;
	}

	public static void createInstance(Context context) {
		if (mInstance != null) {
			Log.d("DBOperator", "An instance already exists.");
			return;
		}
		mInstance = new DBOperator(context);
	}

	// :::::::::::::LIST TYPE SPESIFIC BEGIN::::::::::::::::::::::

	private boolean insertWifi(String wifiBSID, long roomId, long strengthMin,
			long strengthMax) {
		ContentValues wifiValues = new ContentValues();
		wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID, roomId);
		// <-is as it should be

		wifiValues
				.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_WIFI_ID, wifiBSID);
		// <-NEEDS ID FROM ACTUAL WIFI (BSID)

		wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MAX, strengthMax);
		// <-set a default value?

		wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MIN, strengthMin);
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

		// for each celltower {
		long cellTowerId = cellTowerBundle.getInt("CellID");
		long towerStrength = cellTowerBundle.getInt("Strength");
		insertCell(cellTowerId, insertId, towerStrength);
		// }

		cursor.moveToFirst();
		DBRoomEntry newRoom = cursorToDBRoomEntry(cursor);
		cursor.close();
		updateRoom(newRoom);
		return newRoom;
	}

	public void deleteRoom(DBRoomEntry room) {
		long id = room.getId();
		updateRoom(room);
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
		// this must only be run when you are certain you are in the right room
		// this is still missing update for celltowers(18.11.2013)
		WifiSensor wifi = WifiSensor.getInstance();
		List<ScanResult> networks = wifi.GetNetworks();
		List<DBWifiInRoomEntry> networkListToAdd = new ArrayList<DBWifiInRoomEntry>();
		List<DBWifiInRoomEntry> networkList = new ArrayList<DBWifiInRoomEntry>();
		List<DBWifiInRoomEntry> DBnetworkList = new ArrayList<DBWifiInRoomEntry>();

		for (ScanResult item : networks) {
			networkList.add(new DBWifiInRoomEntry(item.BSSID, room.getId(),
					item.level));
		}
		DBnetworkList = this.getWifi(room.getId());

		// prepare list of new networks
		networkListToAdd = getDifferenceWifi(networkList, DBnetworkList);

		// add new networks
		for (DBWifiInRoomEntry item : networkListToAdd) {
			insertWifi(item.getId(), room.getId(), item.getMin(), item.getMax());

		}
		// prepare list to add new ones and update min and max str for existing
		for (ScanResult item : networks) {
			updateWifi(item.BSSID, room.getId(), item.level);
		}
		DBnetworkList = this.getWifi(room.getId());
	}

	public boolean updateWifi(String BSID, long roomId, int signalStrenght) {

		// CHECK IF THE WIFI EXISTS BEFORE YOU RUN THIS FUNCTION

		ContentValues wifiValues = new ContentValues();
		// Database sql get-statements
		String WHERE_STATEMENT = ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_WIFI_ID
				+ " = '" + BSID + "' AND "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID + " = "
				+ roomId;

		String WIFI_GET_MAX = "SELECT "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MAX + " FROM "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_TABLE + " WHERE "
				+ WHERE_STATEMENT;

		String WIFI_GET_MIN = "SELECT "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MIN + " FROM "
				+ ExtendedSQLLiteHelper.WIFI_ROOM_TABLE + " WHERE "
				+ WHERE_STATEMENT;

		Cursor cursor = database.rawQuery(WIFI_GET_MAX, null);// new String[] {
																// BSID,
		// String(roomId) });
		cursor.moveToFirst();
		long DBmax = cursorToLong(cursor);
		cursor = database.rawQuery(WIFI_GET_MIN, null);// new String[] { BSID,
		// String(roomId) });
		cursor.moveToFirst();
		long DBmin = cursorToLong(cursor);

		if (signalStrenght > DBmax) {
			wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MAX,
					signalStrenght);
		} else if (signalStrenght < DBmin) {

			wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MIN,
					signalStrenght);
		} else {
			return false;
		}
		database.update(ExtendedSQLLiteHelper.WIFI_ROOM_TABLE, wifiValues,
				WHERE_STATEMENT, null);
		return true;
	}

	public boolean updateCell(DBRoomEntry room, long cellId, long signalStrenght) {

		// CHECK IF THE CELLTOWER EXISTS BEFORE YOU RUN THIS FUNCTION

		ContentValues cellValues = new ContentValues();
		long roomId = room.getId();
		// Database sql get-statements
		String GET_CELLTOWER = ExtendedSQLLiteHelper.CELLTOWER_COLUMN_TOWER_ID
				+ " = '" + cellId + "'AND "
				+ ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID + " = "
				+ roomId;

		String CELLTOWER_GET_MAX = "SELECT "
				+ ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MAX + " FROM "
				+ ExtendedSQLLiteHelper.CELLTOWER_TABLE + " WHERE "
				+ GET_CELLTOWER;

		String CELLTOWER_GET_MIN = "SELECT "
				+ ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MIN + " FROM "
				+ ExtendedSQLLiteHelper.CELLTOWER_TABLE + " WHERE "
				+ GET_CELLTOWER;

		Cursor cursor = database.rawQuery(CELLTOWER_GET_MAX, null);
		cursor.moveToFirst();
		long DBmax = cursorToLong(cursor);
		cursor = database.rawQuery(CELLTOWER_GET_MIN, null);
		cursor.moveToFirst();
		long DBmin = cursorToLong(cursor);

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
				GET_CELLTOWER, null);
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

	private long cursorToLong(Cursor cursor) {
		return cursor.getLong(0);
	}

	private String cursorToString(Cursor cursor) {
		return cursor.getString(0);
	}

	// :::::::cursor for rooms::::::::::
	private DBRoomEntry cursorToDBRoomEntry(Cursor cursor) {
		DBRoomEntry room = new DBRoomEntry();
		room.setId(cursor.getLong(0));
		room.setName(cursor.getString(1));
		return room;
	}

	// ::::::::cursor for towers:::::::::::
	public boolean cellExists(long roomId, long ID) {
		List<DBCelltowerEntry> list = new ArrayList<DBCelltowerEntry>();

		String whereStatement = (ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID
				+ "=" + roomId + "AND"
				+ ExtendedSQLLiteHelper.CELLTOWER_COLUMN_TOWER_ID + "=" + ID);

		Cursor cursor = database.query(ExtendedSQLLiteHelper.CELLTOWER_TABLE,
				null, whereStatement, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DBCelltowerEntry tower = cursorToDBCelltowerEntry(cursor);
			list.add(tower);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();

		if (list.size() > 1)
			return true; // error more than one hit
		else if (list.size() == 1)
			return true; // one hit
		else
			return false;// no hits in database
	}

	public List<DBCelltowerEntry> getCellTowers(long roomId) {
		List<DBCelltowerEntry> returnList = new ArrayList<DBCelltowerEntry>();

		String whereStatement = ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID
				+ "=" + roomId;

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

		// this order might be
		// WRONG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		DBCelltowerEntry tower = new DBCelltowerEntry();
		tower.setId(cursor.getLong(0));
		tower.setRoom(cursor.getLong(1));
		tower.setMin(cursor.getLong(2));
		tower.setMax(cursor.getLong(3));

		return tower;
	}

	// :::::::::cursor for wifi::::::::::
	public List<DBWifiInRoomEntry> getWifi(long roomId) {
		List<DBWifiInRoomEntry> returnList = new ArrayList<DBWifiInRoomEntry>();

		String whereStatement = (ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID
				+ "=" + roomId);

		Cursor cursor = database.query(ExtendedSQLLiteHelper.WIFI_ROOM_TABLE,
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

	public boolean wifiExists(long roomId, String BsID) {
		// i was hoping to do this on the server but i can't find a way to do
		// that
		List<DBWifiInRoomEntry> list = new ArrayList<DBWifiInRoomEntry>();

		String whereStatement = (ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID
				+ "=" + String(roomId) + "AND"
				+ ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_WIFI_ID + "=" + BsID);

		Cursor cursor = database.query(ExtendedSQLLiteHelper.CELLTOWER_TABLE,
				null, whereStatement, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DBWifiInRoomEntry wifi = cursorToDBWifiInRoomEntry(cursor);
			list.add(wifi);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();

		if (list.size() > 1)
			return true; // error more than one hit
		else if (list.size() == 1)
			return true; // one hit
		else
			return false;// no hits in database
	}

	private DBWifiInRoomEntry cursorToDBWifiInRoomEntry(Cursor cursor) {
		DBWifiInRoomEntry wifi = new DBWifiInRoomEntry();
		wifi.setId(cursor.getString(0));
		wifi.setRoom(cursor.getLong(1));
		wifi.setMin(cursor.getLong(2));
		wifi.setMax(cursor.getLong(3));

		return wifi;
	}

	private List<DBCelltowerEntry> getDifferenceCell(
			List<DBCelltowerEntry> list1, List<DBCelltowerEntry> list2) {
		// must not be used without on entire set, only lists specific to ONE
		// room
		// list1 must be the one with the celltowers that should be added
		List<DBCelltowerEntry> returnList = new ArrayList<DBCelltowerEntry>();
		boolean exists;
		for (DBCelltowerEntry item1 : list1) {
			exists = false;
			for (DBCelltowerEntry item2 : list2) {
				if (item1.getId() == item2.getId()) {
					exists = true;
				}
			}
			if (exists == false) {
				returnList.add(item1);

			}

		}

		return returnList;
	}

	private List<DBWifiInRoomEntry> getDifferenceWifi(
			List<DBWifiInRoomEntry> list1, List<DBWifiInRoomEntry> list2) {

		List<DBWifiInRoomEntry> returnList = new ArrayList<DBWifiInRoomEntry>();
		boolean exists;
		if (list1.size() > list2.size()) {
			for (DBWifiInRoomEntry item1 : list1) {
				exists = false;
				for (DBWifiInRoomEntry item2 : list2) {
					if (item1.getId().equals(item2.getId())) {
						exists = true;
					}
				}
				if (exists == false) {
					returnList.add(item1);
				}
			}
		} else {
			for (DBWifiInRoomEntry item2 : list2) {
				exists = false;
				for (DBWifiInRoomEntry item1 : list1) {
					if (item2.getId() == item1.getId()) {
						exists = true;
					}
				}
				if (exists == false) {
					returnList.add(item2);
				}
			}

		}

		return returnList;
	}

	// :::::::::::LIST TYPE SPESIFIC END:::::::::::::::
}
