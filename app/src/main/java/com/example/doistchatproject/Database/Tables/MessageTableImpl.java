package com.example.doistchatproject.Database.Tables;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.example.doistchatproject.Database.DatabaseModule;
import com.example.doistchatproject.Model.AttachmentDto;
import com.example.doistchatproject.Model.MessageDetailsDto;

import java.util.ArrayList;
import java.util.List;

public class MessageTableImpl {

	static final String LOAD_MESSAGES_PLUS_NUMBER = "20";

	static final String TABLE_NAME = "MESSAGE_ITEM";

	static final String COLUMN_ID = "messageID";
	static final String COLUMN_USER_ID = "userID";
	static final String COLUMN_CONTENT = "content";

	public static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
			+ COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_USER_ID + " INTEGER NOT NULL, "
			+ COLUMN_CONTENT + " TEXT NOT NULL "
			+ ");";

	private static final String[] PROJECTION = new String[] {
			COLUMN_ID,
			COLUMN_USER_ID,
			COLUMN_CONTENT
	};

	private final SQLiteOpenHelper dbHelper;

	/**
	 * Initiates an instance with the given DB helper.
	 *
	 * @param dbHelper The DB instance to be used to access the database.
	 */
	public MessageTableImpl(SQLiteOpenHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	@NonNull
	private ContentValues createDownloadContent(@NonNull MessageDetailsDto messageDetailsDto) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, messageDetailsDto.getId());
		values.put(COLUMN_USER_ID, messageDetailsDto.getUserId());
		values.put(COLUMN_CONTENT, messageDetailsDto.getContent());

		return values;
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(@NonNull MessageDetailsDto messageDetailsDto) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		final ContentValues values = createDownloadContent(messageDetailsDto);

		db.insertWithOnConflict(TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);
		db.close();
	}

	private Cursor get(@NonNull String whereClause, @NonNull String[] params) {
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.query(TABLE_NAME, PROJECTION, whereClause, params, null, null, null);
	}

	/**
	 * Delete message -> From message id
	 * @param id
	 * @return
	 */
	public boolean deleteMessageEntry(@NonNull String id) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		final boolean result = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[] { id }) > 0;
		db.close();
		return result;
	}

	/**
	 * Return message between starting point plus LOAD_MESSAGES_PLUS_NUMBER constant -> From starting point
	 * @return
	 */
	public List<MessageDetailsDto> getMessageRange(@NonNull Context context, @NonNull int startingPoint) {
		final SQLiteDatabase db = dbHelper.getReadableDatabase();

		List<MessageDetailsDto> retval = new ArrayList<>();

		Cursor cursor = db.query(
				TABLE_NAME,
				null,
				null,
				null,
				null,
				null,
				null,
				String.valueOf(startingPoint) + "," + LOAD_MESSAGES_PLUS_NUMBER);
		try {
			if (cursor.moveToFirst()) {
				do {

					final int column_id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
					final int column_userID = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
					final String column_content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));

					retval.add(new MessageDetailsDto(column_id, column_userID, column_content, getMessageAttachment(context, String.valueOf(column_id))));

				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}

		return retval;
	}

	/**
	 * Get specific message -> From message id
	 * @param id
	 * @return
	 */
	public MessageDetailsDto getMessage(String id) {

		MessageDetailsDto retval = null;
		final Cursor cursor = get(COLUMN_ID + " = ?", new String[] { id });
		try {
			if (cursor.moveToFirst()) {

				final String column_content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
				final int column_userID = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
				final int column_id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));

				retval = new MessageDetailsDto(column_id, column_userID, column_content, null);

			}
		} finally {
			cursor.close();
		}
		return retval;
	}

	/**
	 * Get specific attachment -> From message id
	 * @param context
	 * @param idMessage
	 * @return
	 */
	private List<AttachmentDto> getMessageAttachment(@NonNull Context context, @NonNull String idMessage) {

		final AttachmentTableImpl attachmentTable = DatabaseModule.attachmentTable(context);
		return attachmentTable.getAttachmentsFromMessage(String.valueOf(idMessage));
	}

}
