package com.lingavin.tapcounter.daos;

import com.lingavin.tapcounter.TapCounterApp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = DatabaseHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "TapCounter";
	private static final int DATABASE_VERSION = 1;
	
	public DatabaseHelper() {
		super(TapCounterApp.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		final String counter = "CREATE TABLE " + CounterDao.TABLE + "(" +
				CounterDao._ID + " integer primary key, " +
				CounterDao.LABEL + " text, " +
				CounterDao.COUNT + " int, " +
				CounterDao.LOCKED + " int)"; 
		database.execSQL(counter);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

}
