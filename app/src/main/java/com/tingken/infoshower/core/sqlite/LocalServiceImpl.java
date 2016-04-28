/**
 * 
 */
package com.tingken.infoshower.core.sqlite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tingken.infoshower.core.LocalService;

/**
 * @author Administrator
 * 
 */
public class LocalServiceImpl implements LocalService {
	private static final String TAG = "effort";

	private static final String DB_NAME = "InfoShower.db";
	private static final int DB_VERSION = 1;
	private static final String ACCOUNT_TABLE = "REG_ACCOUNT";
	private static final String[] ACCOUNT_COLUMN = { "ID", "REG_NUM", "CACHED_SERVER_ADDRESS", "OFFLINE_SERVER_PAGE" };
	private static final String CREATE_ACCOUNT_TB = "CREATE TABLE " + ACCOUNT_TABLE
			+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT," + "REG_NUM TEXT," + "CACHED_SERVER_ADDRESS TEXT,"
			+ "OFFLINE_SERVER_PAGE TEXT)";
	private static Context mContext;
	private SQLiteDatabase mSQLiteDatabase = null;
	private DatabaseHelper mDatabaseHelper = null;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_ACCOUNT_TB);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + ACCOUNT_TABLE);
			onCreate(db);
		}
	}

	public static void initialize(Context context) {
		if (mContext == null) {
			mContext = context;
		}
	}

	public LocalServiceImpl() {
		mDatabaseHelper = new DatabaseHelper(mContext);
		Log.e(TAG, "DatabaseAdapter.open");
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tingken.infoshower.core.DataSource#getAuthCode()
	 */
	@Override
	public String getAuthCode() {
		Cursor mCursor = null;
		mCursor = mSQLiteDatabase.query(false, ACCOUNT_TABLE, ACCOUNT_COLUMN, null, null, null, null, null, null);
		if (mCursor.getCount() <= 0)
			return null;
		mCursor.moveToFirst();
		while (!mCursor.isAfterLast()) {
			int columnIndex = mCursor.getColumnIndexOrThrow(ACCOUNT_COLUMN[1]);
			String value;
			try {
				value = mCursor.getString(columnIndex);
			} catch (Exception e) {
				value = null;
			}
			return value;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tingken.infoshower.core.DataSource#getCachedServerAddress()
	 */
	@Override
	public String getCachedServerAddress() {
		Cursor mCursor = null;
		mCursor = mSQLiteDatabase.query(false, ACCOUNT_TABLE, ACCOUNT_COLUMN, null, null, null, null, null, null);
		if (mCursor.getCount() <= 0)
			return null;
		mCursor.moveToFirst();
		while (!mCursor.isAfterLast()) {
			int columnIndex = mCursor.getColumnIndexOrThrow(ACCOUNT_COLUMN[2]);
			String value;
			try {
				value = mCursor.getString(columnIndex);
			} catch (Exception e) {
				value = null;
			}
			return value;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.tingken.infoshower.core.DataSource#getOfflineServerPage()
	 */
	@Override
	public String getOfflineServerPage() {
		Cursor mCursor = null;
		mCursor = mSQLiteDatabase.query(false, ACCOUNT_TABLE, ACCOUNT_COLUMN, null, null, null, null, null, null);
		if (mCursor.getCount() <= 0)
			return null;
		mCursor.moveToFirst();
		while (!mCursor.isAfterLast()) {
			int columnIndex = mCursor.getColumnIndexOrThrow(ACCOUNT_COLUMN[3]);
			String value;
			try {
				value = mCursor.getString(columnIndex);
			} catch (Exception e) {
				value = null;
			}
			return value;
		}
		return null;
	}

	@Override
	public void saveAuthCode(String regNum) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveCachedServerAddress(String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveOfflineServerPage() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAutoStart() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAutoStart(boolean autoStart) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getShowServiceAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveShowServiceAddress(String urlPrefix) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveLoginId(String loginId) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLoginId() {
		// TODO Auto-generated method stub
		return null;
	}

}
