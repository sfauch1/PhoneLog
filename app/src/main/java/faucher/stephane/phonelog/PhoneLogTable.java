package faucher.stephane.phonelog;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class PhoneLogTable {

	// Phone log table
	private static final String TABLE_PHONE_LOG = "phone_log";
	private static final String COL_ID = "ID";
	private static final String COL_PHONE_NUMBER = "Phone_Number";
	private static final String COL_TIMESTAMP_START = "Time_Stamp_Start";
	private static final String COL_TIMESTAMP_END = "Time_Stamp_End";
	private static final String COL_PHONE_STATE = "Phone_State";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_PHONE_LOG + " (" + COL_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " // primary key
			+ COL_PHONE_NUMBER + " TEXT NOT NULL, " // phone number
			+ COL_TIMESTAMP_START + " DATETIME NOT NULL, " // start time
			+ COL_TIMESTAMP_END + " DATETIME NOT NULL, " // end time
			+ COL_PHONE_STATE + " INT NOT NULL" // phone state 1=RINGING,
												// 2=OFFHOOK, 3=IDLE
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		// TODO Auto-generated method stub
		// Create table
		database.execSQL(DATABASE_CREATE);

	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		// TODO Auto-generated method stub
		database.execSQL("DROP TABLE " + TABLE_PHONE_LOG + ";");
		onCreate(database);

	}

	private SQLiteDatabase database;
	private PhoneLogDatabaseHelper myPhoneLogDatabaseHelper;

	// Create DB and table
	public PhoneLogTable(Context context) {
		myPhoneLogDatabaseHelper = new PhoneLogDatabaseHelper(context);
	}

	// Open DB
	public void open() {
		// Open DB for reading
		database = myPhoneLogDatabaseHelper.getWritableDatabase();
	}

	// Close DB
	public void close() {
		database.close();
	}

	// Insert record
	public void insertPhoneLog(PhoneLog log) {

		String phoneNumber = log.getPhoneNumber();
		long startTime = log.getStartTime();
		long endTime = log.getEndTime();
		int phoneState = log.getPhoneState();

		ContentValues values = new ContentValues();
		values.put(COL_PHONE_NUMBER, phoneNumber);
		values.put(COL_TIMESTAMP_START, startTime);
		values.put(COL_TIMESTAMP_END, endTime);
		values.put(COL_PHONE_STATE, phoneState);
		// Insert record to DB
		database.insert(TABLE_PHONE_LOG, null, values);

	}

	// Update record
	public void updatePhoneLog(int phoneState) {

		ContentValues values = new ContentValues();
		values.put(COL_TIMESTAMP_START, System.currentTimeMillis());
		values.put(COL_PHONE_STATE, phoneState);
		// Update record
		database.update(TABLE_PHONE_LOG, values, COL_ID + " = (SELECT max("
				+ COL_ID + ") FROM " + TABLE_PHONE_LOG + ")", null);

	}

	// Close record
	public void closePhoneLog() {

		int phoneState = 0;

		// Get latest phone state from last record
		Cursor c = database.rawQuery("SELECT " + COL_PHONE_STATE + " FROM "
				+ TABLE_PHONE_LOG + " WHERE " + COL_ID + " = (SELECT max("
				+ COL_ID + ") FROM " + TABLE_PHONE_LOG + ")", null);
		if (c.moveToFirst()) {
			phoneState = c.getInt(c.getColumnIndex(COL_PHONE_STATE));
		}

		// phone state still 1 so the call has not been answered
		if (phoneState == 1) {
			phoneState = 3;
		} else {
			phoneState = 2;
		}

		ContentValues values = new ContentValues();
		values.put(COL_TIMESTAMP_END, System.currentTimeMillis());
		values.put(COL_PHONE_STATE, phoneState);

		// Update record
		database.update(TABLE_PHONE_LOG, values, COL_ID + " = (SELECT max("
				+ COL_ID + ") FROM " + TABLE_PHONE_LOG + ")", null);

	}

	// Delete record
	public int deletePhoneLog(int id) {
		return database.delete(TABLE_PHONE_LOG, COL_ID + " = ?",
				new String[] { Integer.toString(id) });
	}

	// Delete all records
	public void deleteTable() {
		database.delete(TABLE_PHONE_LOG, "1", null);

	}

	public List<PhoneLog> getLogs() {

		List<PhoneLog> phoneLogs = new ArrayList<PhoneLog>();

		Cursor c = database.query(TABLE_PHONE_LOG, new String[] { COL_ID,
				COL_PHONE_NUMBER, COL_TIMESTAMP_START, COL_TIMESTAMP_END,
				COL_PHONE_STATE }, null, null, null, null, null);
		if (c.moveToFirst()) {
			do {
				int id = c.getInt(c.getColumnIndex(COL_ID));
				String phone = c.getString(c.getColumnIndex(COL_PHONE_NUMBER));
				long startTime = c.getLong(c
						.getColumnIndex(COL_TIMESTAMP_START));
				long endTime = c.getLong(c.getColumnIndex(COL_TIMESTAMP_END));
				int phoneState = c.getInt(c.getColumnIndex(COL_PHONE_STATE));

				phoneLogs.add(new PhoneLog(id, phone, startTime, endTime,
						phoneState));

			} while (c.moveToNext());
		}
		// On ferme le cursor
		c.close();

		return phoneLogs;
	}


}
