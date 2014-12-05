package net.infidea.contextmon.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContextDBHelper extends SQLiteOpenHelper {
	public static final String TABLE_NAME = "contexts";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_VALUE0 = "value0";
	public static final String COLUMN_VALUE1 = "value1";
	public static final String COLUMN_VALUE2 = "value2";
	public static final String COLUMN_VALUE3 = "value3";
	public static final String COLUMN_VALUE4 = "value4";
	public static final String COLUMN_VALUE5 = "value5";

	private static final String DATABASE_NAME = "contexts.db";
	private static final int DATABASE_VERSION = 4;

	/*
	 * SQL statements
	 */
	private static final String SQL_DB_CREATE = "CREATE TABLE "
			+ TABLE_NAME + "(" 
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_TIME + " LONG, "
			+ COLUMN_TYPE + " INTEGER, "
			+ COLUMN_VALUE0 + " FLOAT, "
			+ COLUMN_VALUE1 + " FLOAT, "
			+ COLUMN_VALUE2 + " FLOAT, "
			+ COLUMN_VALUE3 + " FLOAT, "
			+ COLUMN_VALUE4 + " FLOAT, "
			+ COLUMN_VALUE5 + " FLOAT);";

	public ContextDBHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(SQL_DB_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}