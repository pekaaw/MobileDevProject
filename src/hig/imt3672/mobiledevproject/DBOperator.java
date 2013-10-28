package hig.imt3672.mobiledevproject;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
//import org.w3c.dom.Comment;

public class DBOperator {

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

	// /:::::::::::::::::::::::::::::LIST TYPE SPESIFIC
	// BEGIN::::::::::::::::::::::
	public DBRoomEntry createRoom(String comment) {
		ContentValues values = new ContentValues();
		values.put(ExtendedSQLLiteHelper.ROOM_COLUMN_NAME, room);
		long insertId = database.insert(ExtendedSQLLiteHelper.ROOM_TABLE, null,
				values);
		Cursor cursor = database.query(ExtendedSQLLiteHelper.ROOM_TABLE,
				allRooms, ExtendedSQLLiteHelper.ROOM_COLUMN_ID + " = "
						+ insertId, null, null, null, null);
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
	// /:::::::::::::::::::::::::::::LIST TYPE SPESIFIC
	// END::::::::::::::::::::::
}
