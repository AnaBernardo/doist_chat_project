package com.example.doistchatproject.Database.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.example.doistchatproject.Model.AttachmentDto;

import java.util.ArrayList;
import java.util.List;

public class AttachmentTableImpl {

    static final String TABLE_NAME = "ATTACHMENT_ITEM";

    static final String COLUMN_MESSAGE_ID = "messageID";
    static final String COLUMN_ATTACHMENT_ID = "attachmentID";
    static final String COLUMN_TITLE = "title";
    static final String COLUMN_URL = "url";
    static final String COLUMN_THUMBNAIL_URL = "thumbnailUrl";

    public static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_MESSAGE_ID + " INTEGER NOT NULL, "
            + COLUMN_ATTACHMENT_ID + " TEXT PRIMARY KEY, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_URL + " TEXT NOT NULL, "
            + COLUMN_THUMBNAIL_URL + " TEXT NOT NULL "
            + ");";

    private static final String[] PROJECTION = new String[] {
            COLUMN_MESSAGE_ID,
            COLUMN_ATTACHMENT_ID,
            COLUMN_TITLE,
            COLUMN_URL,
            COLUMN_THUMBNAIL_URL
    };

    private final SQLiteOpenHelper dbHelper;

    public AttachmentTableImpl(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @NonNull
    private ContentValues createDownloadContent(@NonNull int messageId, @NonNull AttachmentDto attachmentDto) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE_ID, messageId);
        values.put(COLUMN_ATTACHMENT_ID, attachmentDto.getId());
        values.put(COLUMN_TITLE, attachmentDto.getTitle());
        values.put(COLUMN_URL, attachmentDto.getUrl());
        values.put(COLUMN_THUMBNAIL_URL, attachmentDto.getThumbnailUrl());

        return values;
    }

    public void add(@NonNull int messageId, @NonNull AttachmentDto attachmentDto) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final ContentValues values = createDownloadContent(messageId, attachmentDto);
        db.insertWithOnConflict(TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    private Cursor get(String whereClause, String[] params) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(TABLE_NAME, PROJECTION, whereClause, params, null, null, null);
    }

    /**
     * Get all attachments from a message -> From messageID
     * @param messageID
     * @return
     */
    public List<AttachmentDto> getAttachmentsFromMessage(@NonNull String messageID) {

        List<AttachmentDto> retval = new ArrayList<>();
        final Cursor cursor = get(COLUMN_MESSAGE_ID + " = ?", new String[] { messageID });

        try {
            if (cursor.moveToFirst()) {
                do {

                    final String column_attachment_ID = cursor.getString(cursor.getColumnIndex(COLUMN_ATTACHMENT_ID));
                    final String column_title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                    final String column_url = cursor.getString(cursor.getColumnIndex(COLUMN_URL));
                    final String column_thumbnail = cursor.getString(cursor.getColumnIndex(COLUMN_THUMBNAIL_URL));

                    retval.add(new AttachmentDto(column_attachment_ID, column_title, column_url, column_thumbnail));

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

        return retval;
    }

    /**
     * Delete specific attachment -> From attachmentID
     * @param attachmentID
     * @return
     */
    public boolean deleteAttachmentEntry(@NonNull String attachmentID) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final boolean result = db.delete(TABLE_NAME, COLUMN_ATTACHMENT_ID + " = ?", new String[] { attachmentID }) > 0;
        db.close();
        return result;
    }

    /**
     * Delete all attachments from a message -> From messageID
     * @param messageID
     * @return
     */
    public boolean deleteAllAttachmentsFromMessage(@NonNull String messageID) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final boolean result = db.delete(TABLE_NAME, COLUMN_MESSAGE_ID + " = ?", new String[] { messageID }) > 0;
        db.close();
        return result;
    }

    /**
     * Get a specific message id from an attachment -> From messageID
     * @param messageID
     * @return
     */
    public int getMessageID(@NonNull String messageID) {

        int retval = -1;
        final Cursor cursor = get(COLUMN_ATTACHMENT_ID + " = ?", new String[] { messageID });
        try {
            if (cursor.moveToFirst()) {

                retval = cursor.getInt(cursor.getColumnIndex(COLUMN_MESSAGE_ID));

            }
        } finally {
            cursor.close();
        }
        return retval;
    }

}
