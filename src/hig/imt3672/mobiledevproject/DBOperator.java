package hig.imt3672.mobiledevproject;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBOperator { // Handles normal usage of the database

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

	// :::::::::::::LIST TYPE SPESIFIC BEGIN::::::::::::::::::::::
	public DBRoomEntry createRoom(String room) {
		ContentValues roomValues = new ContentValues();
		roomValues.put(ExtendedSQLLiteHelper.ROOM_COLUMN_NAME, room);
		long insertId = database.insert(ExtendedSQLLiteHelper.ROOM_TABLE, null,
				roomValues);
		Cursor cursor = database.query(ExtendedSQLLiteHelper.ROOM_TABLE,
				allRooms, ExtendedSQLLiteHelper.ROOM_COLUMN_ID + " = "
						+ insertId, null, null, null, null);

		ContentValues wifiValues = new ContentValues();
		wifiValues
				.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_ROOM_ID, insertId);
		// <-is as it should be

		wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_WIFI_ID, room);
		// <-NEEDS ID FROM ACTUAL WIFI (BSID)

		wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MAX, room);
		// <-set a default value?

		wifiValues.put(ExtendedSQLLiteHelper.WIFI_ROOM_COLUMN_MIN, room);
		// <-set a default value?

		/* long wifiInsertId = */
		database.insert(ExtendedSQLLiteHelper.WIFI_ROOM_TABLE, null, wifiValues);

		ContentValues cellValues = new ContentValues();
		cellValues
				.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_ROOM_ID, insertId);
		// <-is as it should be

		cellValues.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_TOWER_ID, room);
		// <-NEEDS ID FROM ACTUAL TOWER

		cellValues.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MAX, room);
		// <-set a default value?

		cellValues.put(ExtendedSQLLiteHelper.CELLTOWER_COLUMN_MIN, room);
		// <-set a default value?

		/* long cellInsertId = */
		database.insert(ExtendedSQLLiteHelper.CELLTOWER_TABLE, null, cellValues);

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

	private DBRoomEntry cursorToDBRoomEntry(Cursor cursor) {
		DBRoomEntry room = new DBRoomEntry();
		room.setId(cursor.getLong(0));
		room.setName(cursor.getString(1));
		return room;
	}
	// :::::::::::LIST TYPE SPESIFIC END:::::::::::::::
}
