package hig.imt3672.knowthisroom;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ExtendedSQLLiteHelper extends SQLiteOpenHelper { // Handles
																// creation,
																// deletion and
																// structure
																// change in the
																// database
	// database____________________________________________________________

	private static final String DATABASE_NAME = "roomsposition.db";
	private static final int DATABASE_VERSION = 1;

	// rooms_______________________________________________________________
	public static final String ROOM_TABLE = "rooms";
	public static final String ROOM_COLUMN_ID = "roomID";
	public static final String ROOM_COLUMN_NAME = "name";

	// celltowers__________________________________________________________
	public static final String CELLTOWER_TABLE = "celltowers";
	public static final String CELLTOWER_COLUMN_TOWER_ID = "towerID";
	public static final String CELLTOWER_COLUMN_ROOM_ID = "roomID";
	public static final String CELLTOWER_COLUMN_MAX = "maxSignal";
	public static final String CELLTOWER_COLUMN_MIN = "minSignal";

	// wifi in rooms_______________________________________________________
	public static final String WIFI_ROOM_TABLE = "wifi_rooms";
	public static final String WIFI_ROOM_COLUMN_WIFI_ID = "bsID";
	public static final String WIFI_ROOM_COLUMN_ROOM_ID = "roomID";
	public static final String WIFI_ROOM_COLUMN_MAX = "maxSignal";
	public static final String WIFI_ROOM_COLUMN_MIN = "minSignal";

	// Database creation sql statements
	private static final String CELL_DATABASE_CREATE = "create table "
			+ CELLTOWER_TABLE + "(" + CELLTOWER_COLUMN_TOWER_ID
			+ " integer not null, " + CELLTOWER_COLUMN_ROOM_ID
			+ " integer not null, " + CELLTOWER_COLUMN_MAX
			+ " integer not null, " + CELLTOWER_COLUMN_MIN
			+ " integer not null, " + ");";
	private static final String ROOM_DATABASE_CREATE = "create table "
			+ WIFI_ROOM_COLUMN_WIFI_ID + "(" + ROOM_COLUMN_ID
			+ " autoincrement, " + ROOM_COLUMN_NAME + " text not null);";

	private static final String WIFI_ROOM_DATABASE_CREATE = "create table "
			+ WIFI_ROOM_TABLE + "(" + WIFI_ROOM_COLUMN_WIFI_ID
			+ " text not null, " + WIFI_ROOM_COLUMN_ROOM_ID
			+ " integer not null, " + WIFI_ROOM_COLUMN_MAX
			+ " integer not null, " + WIFI_ROOM_COLUMN_MIN
			+ " integer not null, " + " text not null);";

	public ExtendedSQLLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(WIFI_ROOM_DATABASE_CREATE);
		database.execSQL(ROOM_DATABASE_CREATE);
		database.execSQL(CELL_DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ExtendedSQLLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + CELL_DATABASE_CREATE);
		db.execSQL("DROP TABLE IF EXISTS " + ROOM_DATABASE_CREATE);
		db.execSQL("DROP TABLE IF EXISTS " + WIFI_ROOM_DATABASE_CREATE);
		onCreate(db);
	}

}