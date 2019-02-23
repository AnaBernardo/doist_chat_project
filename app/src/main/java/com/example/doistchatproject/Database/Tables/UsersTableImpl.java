package com.example.doistchatproject.Database.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.example.doistchatproject.Model.UsersDetailsDto;

public class UsersTableImpl {

    static final String TABLE_NAME = "USERS_ITEM";

    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_AVATAR_ID = "avatarUrl";

    public static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_AVATAR_ID + " TEXT NOT NULL "
            + ");";

    private final SQLiteOpenHelper dbHelper;

    public UsersTableImpl(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @NonNull
    private ContentValues createDownloadContent(@NonNull UsersDetailsDto usersDetailsDto) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, usersDetailsDto.getId());
        values.put(COLUMN_NAME, usersDetailsDto.getName());
        values.put(COLUMN_AVATAR_ID, usersDetailsDto.getAvatarId());

        return values;
    }

    private static final String[] PROJECTION = new String[] {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_AVATAR_ID
    };

    public void add(@NonNull UsersDetailsDto usersDetailsDto) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = createDownloadContent(usersDetailsDto);
        db.insertWithOnConflict(TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    private Cursor get(String whereClause, String[] params) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(TABLE_NAME, PROJECTION, whereClause, params, null, null, null);
    }

    /**
     * Get specific user -> From userID
     * @param userID
     * @return
     */
    public UsersDetailsDto getUser(@NonNull String userID) {

        UsersDetailsDto retval = null;
        final Cursor cursor = get(COLUMN_ID + " = ?", new String[] { userID });
        try {
            if (cursor.moveToFirst()) {

                final String column_name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                final String column_avatar = cursor.getString(cursor.getColumnIndex(COLUMN_AVATAR_ID));
                final int column_id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));

                retval = new UsersDetailsDto(column_id, column_name, column_avatar);

            }
        } finally {
            cursor.close();
        }
        return retval;
    }
}
