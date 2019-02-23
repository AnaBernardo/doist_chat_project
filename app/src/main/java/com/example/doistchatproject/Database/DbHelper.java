package com.example.doistchatproject.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.example.doistchatproject.Database.Tables.AttachmentTableImpl;
import com.example.doistchatproject.Database.Tables.MessageTableImpl;
import com.example.doistchatproject.Database.Tables.UsersTableImpl;

public class DbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	public DbHelper(@NonNull Context context, @NonNull String name) {
		super(context, name, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MessageTableImpl.DATABASE_CREATE); // message
		db.execSQL(AttachmentTableImpl.DATABASE_CREATE); // attachment in messages
		db.execSQL(UsersTableImpl.DATABASE_CREATE); // users
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
