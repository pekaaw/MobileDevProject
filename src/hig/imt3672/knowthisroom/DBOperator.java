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

public class DBOperator { // Handles normal usage of the m_database
	// singleton
	static DBOperator m_instance;

	// Database fields
	private SQLiteDatabase m_database;
	private ExtendedSQLLiteHelper dbHelper;
	private String[] allRooms = { ExtendedSQLLiteHelper.ROOM_COLUMN_ID,
			ExtendedSQLLiteHelper.ROOM_COLUMN_NAME };

	public DBOperator(Context context) {
		dbHelper = new ExtendedSQLLiteHelper(context);
	}

	public void open() throws SQLException {
		m_database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	// ::::::::::::Singleton:::::::::::::::::::::::
	public static DBOperator getInstance() {
		if (m_instance == null) {
			Log.d("DBOperator", "You have not created an instance.");
			return null;
		}
		return m_instance;
	}

	public synchronized static void createInstance(Context context) {
		if (m_instance != null) {
			Log.d("DBOperator", "An instance already exists.");
			return;
		}
		m_instance = new DBOperator(context);
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

		// Open m_database if not open already
		if( ! m_database.isOpen() ) {
			open();
		}
		
		/* long wifiInsertId = */
		m_database.insert(ExtendedSQLLiteHelper.WIFI_ROOM_TABLE, null, wifiValues);
	
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
		if( !m_database.isOpen() ) {
			open();
		}
		
		m_database.insert(ExtendedSQLLiteHelper.CELLTOWER_TABLE, null, cellValues);

		return true;
	}

	public DBRoomEntry createRoom(String room, Bundle cellTowerBundle) {
		// open db
		if( ! m_database.isOpen() ) {
			open();
		}
		
		ContentValues roomValues = new ContentValues();
		roomValues.put(ExtendedSQLLiteHelper.ROOM_COLUMN_NAME, room);
		long insertId = m_database.insert(ExtendedSQLLiteHelper.ROOM_TABLE, null,
				roomValues);
		Cursor cursor = m_database.query(ExtendedSQLLiteHelper.ROOM_TABLE,
				allRooms, ExtendedSQLLiteHelper.ROOM_COLUMN_ID + " = "
						+ insertId, null, null, null, null);

		cursor.moveToFirst();
		DBRoomEntry newRoom = cursorToDBRoomEntry(cursor);
		cursor.close();
		updateRoom(newRoom, cellTowerBundle);

		return newRoom;
	}

	public void deleteRoom(DBRoomEntry room) {
		long id = room.getId();
		System.out.println("Room deleted with id: " + id);
		
		if( ! m_database.isOpen() ) {
			open();
		}
		try {
			m_database.delete(ExtendedSQLLiteHelper.ROOM_TABLE,
					ExtendedSQLLiteHelper.ROOM_COLUMN_ID + " = " + id, null);
	
			m_database.delete(ExtendedSQLLiteHelper.CELLTOWER_TABLE,
					ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID + " = " + id,
					null);
	
			m_database.delete(ExtendedSQLLiteHelper.WIFI_ROOM_TABLE,
					ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID + " = " + id,
					null);
		} catch( IllegalStateException e ) {
			e.printStackTrace();
		}

	}

	public void updateRoom(DBRoomEntry room, Bundle cellTowerBundle) {
		// this must only be run when you are certain you are in the right room

		WifiSensor wifi = WifiSensor.getInstance();
		List<ScanResult> networks = wifi.GetNetworks();
		List<DBWifiInRoomEntry> networkListToAdd = new ArrayList<DBWifiInRoomEntry>();
		List<DBWifiInRoomEntry> networkList = new ArrayList<DBWifiInRoomEntry>();
		List<DBWifiInRoomEntry> DBnetworkList = new ArrayList<DBWifiInRoomEntry>();

		for (ScanResult item : networks) {
			networkList.add(new DBWifiInRoomEntry(item.BSSID, room.getId(),
					item.level));
		}
		
		if( !m_database.isOpen() ) {
			open();
		}
		
		DBnetworkList = this.getWifi(room.getId());

		// prepare list of new networks
		networkListToAdd = getDifferenceWifi(DBnetworkList, networkList);

		// add new networks
		for (DBWifiInRoomEntry item : networkListToAdd) {
			insertWifi(item.getId(), room.getId(), item.getMin(), item.getMax());

		}
		// prepare list to add new ones and update min and max str for existing
		for (ScanResult item : networks) {
			updateWifi(item.BSSID, room.getId(), item.level);
		}
		DBnetworkList = this.getWifi(room.getId());

		// for each celltower {
		long cellTowerId = cellTowerBundle.getInt("CellID");
		long towerStrength = cellTowerBundle.getInt("Strength");
		if (!cellExists(room.getId(), cellTowerId)) {
			insertCell(cellTowerId, room.getId(), towerStrength);
		} else {
			updateCell(room, cellTowerId, towerStrength);
		}

	}

	public boolean updateWifi(String BSID, long roomId, int signalStrenght) {
		if( ! m_database.isOpen() ) {
			open();
		}
		
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

		Cursor cursor = m_database.rawQuery(WIFI_GET_MAX, null);// new String[] {
																// BSID,
		// String(roomId) });
		cursor.moveToFirst();
		long DBmax = cursorToLong(cursor);

		cursor = m_database.rawQuery(WIFI_GET_MIN, null);// new String[] { BSID,
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
		m_database.update(ExtendedSQLLiteHelper.WIFI_ROOM_TABLE, wifiValues,
				WHERE_STATEMENT, null);

		return true;
	}

	public boolean updateCell(DBRoomEntry room, long cellId, long signalStrenght) {
		
		// Open m_database if not open
		if( !m_database.isOpen() ) {
			open();
		}
		
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

		Cursor cursor = m_database.rawQuery(CELLTOWER_GET_MAX, null);
		cursor.moveToFirst();
		long DBmax = cursorToLong(cursor);
		cursor = m_database.rawQuery(CELLTOWER_GET_MIN, null);
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
		m_database.update(ExtendedSQLLiteHelper.CELLTOWER_TABLE, cellValues,
				GET_CELLTOWER, null);
		return true;
	}

	private String String(long roomId) {
		return null;
	}

	public List<DBRoomEntry> getAllDBRoomEntries() {
		List<DBRoomEntry> rooms = new ArrayList<DBRoomEntry>();
		
		// Open m_database if not open
		if( !m_database.isOpen() ) {
			open();
		}

		try {
		
			Cursor cursor = m_database.query(ExtendedSQLLiteHelper.ROOM_TABLE,
					allRooms, null, null, null, null,
					ExtendedSQLLiteHelper.ROOM_COLUMN_NAME + " COLLATE NOCASE");// +
																				// " ASC");
	
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				DBRoomEntry room = cursorToDBRoomEntry(cursor);
				rooms.add(room);
				cursor.moveToNext();
			}
			
			// Make sure to close the cursor
			cursor.close();
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} 

		return rooms;
	}

	private long cursorToLong(Cursor cursor) {
		return cursor.getLong(0);
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
		
		if( !m_database.isOpen() ) {
			open();
		}
		
		List<DBCelltowerEntry> list = new ArrayList<DBCelltowerEntry>();

		String whereStatement = (ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID
				+ "=" + roomId + " AND "
				+ ExtendedSQLLiteHelper.CELLTOWER_COLUMN_TOWER_ID + "=" + ID);

		Cursor cursor = m_database.query(ExtendedSQLLiteHelper.CELLTOWER_TABLE,
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
			return false;// no hits in m_database
	}

	public List<DBCelltowerEntry> getCellTowers(long roomId) {
		
		if( !m_database.isOpen() ) {
			open();
		}
		
		List<DBCelltowerEntry> returnList = new ArrayList<DBCelltowerEntry>();

		String whereStatement = ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID
				+ "=" + roomId;

		Cursor cursor = m_database.query(ExtendedSQLLiteHelper.CELLTOWER_TABLE,
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
		tower.setTowerId(cursor.getLong(0));
		tower.setRoomId(cursor.getLong(1));
		tower.setMin(cursor.getLong(2));
		tower.setMax(cursor.getLong(3));

		return tower;
	}

	// :::::::::cursor for wifi::::::::::
	// TODO GETWIFI
	public List<DBWifiInRoomEntry> getWifi(long roomId) {
		List<DBWifiInRoomEntry> returnList = new ArrayList<DBWifiInRoomEntry>();

		String whereStatement = (ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID
				+ "=" + roomId);

		if( !m_database.isOpen() ) {
			open();
		}
		
		Cursor cursor = m_database.query(ExtendedSQLLiteHelper.WIFI_ROOM_TABLE,
				null, whereStatement, null, null, null,
				ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MIN + " DESC");

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

	/**
	 * Returns a list of strings with the BSID of each wifi network stored for a
	 * specific room.
	 * <p>
	 * 
	 * @param roomId
	 *            Id of the room
	 * @param numbersOfRooms
	 *            How many wifi-bsID's to return
	 * @return List<String> with BSID's as strings
	 */
	public List<String> getWifiBsIDs(long roomId, int numbersOfRooms) {

		// open db
		if( !m_database.isOpen() ) {
			open();
		}

		// List to return - with name of BSID's
		List<String> wifiNameList = new ArrayList<String>();

		// Specify statement and run query
		String whereStatement = (ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID
				+ "=" + roomId);
		Cursor cursor = m_database.query(ExtendedSQLLiteHelper.WIFI_ROOM_TABLE,
				null, whereStatement, null, null, null,
				ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MIN + " DESC");

		// if we got nothing from the db, return the empty list.
		if( cursor.getCount() == 0 ) {
			return wifiNameList;
		}

		// go to start of cursor and initialize counter to 0
		cursor.moveToFirst();
		int counter = 0;

		// run through to we get to end
		while (!cursor.isAfterLast() && (counter <= numbersOfRooms)) {

			// find wifi and add the string of the BSID to the list
			DBWifiInRoomEntry wifi = cursorToDBWifiInRoomEntry(cursor);
			wifiNameList.add(wifi.getId());

			// then move on to next position in cursor and count up
			cursor.moveToNext();
			counter++;
		}

		// close cursor
		cursor.close();

		// wifiNameList should now be #numberOfRooms long
		return wifiNameList;
	}

	public boolean wifiExists(long roomId, String BsID) {
		// i was hoping to do this on the server but i can't find a way to do
		// that
		List<DBWifiInRoomEntry> list = new ArrayList<DBWifiInRoomEntry>();
		
		if( !m_database.isOpen() ) {
			open();
		}

		String whereStatement = (ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID
				+ "=" + String(roomId) + "AND"
				+ ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_WIFI_ID + "=" + BsID);

		Cursor cursor = m_database.query(ExtendedSQLLiteHelper.CELLTOWER_TABLE,
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
			return false;// no hits in m_database
	}

	private DBWifiInRoomEntry cursorToDBWifiInRoomEntry(Cursor cursor) {
		DBWifiInRoomEntry wifi = new DBWifiInRoomEntry();
		wifi.setId(cursor.getString(0));
		wifi.setRoom(cursor.getLong(1));
		wifi.setMax(cursor.getLong(2));
		wifi.setMin(cursor.getLong(3));

		return wifi;
	}

	private List<DBWifiInRoomEntry> getDifferenceWifi(
			List<DBWifiInRoomEntry> dbList, List<DBWifiInRoomEntry> inList) {

		List<DBWifiInRoomEntry> returnList = new ArrayList<DBWifiInRoomEntry>();
		boolean exists;
		for (DBWifiInRoomEntry inItem : inList) {
			exists = false;
			for (DBWifiInRoomEntry oldItem : dbList) {
				if (inItem.getId().equals(oldItem.getId())) {
					exists = true;
				}
			}
			if (exists == false) {
				returnList.add(inItem);
			}
		}

		return returnList;
	}

	// :::::::::::LIST TYPE SPESIFIC END:::::::::::::::
}
