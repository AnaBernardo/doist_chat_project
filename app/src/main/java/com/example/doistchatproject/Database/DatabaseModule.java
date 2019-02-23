package com.example.doistchatproject.Database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.example.doistchatproject.Database.Tables.AttachmentTableImpl;
import com.example.doistchatproject.Database.Tables.MessageTableImpl;
import com.example.doistchatproject.Database.Tables.UsersTableImpl;

public class DatabaseModule {

    private static final String DB_NAME = "twist";

    private static SQLiteOpenHelper helper(@NonNull Context context) {
        return new DbHelper(context, DB_NAME);
    }

    public static AttachmentTableImpl attachmentTable(@NonNull Context context) {
        return new AttachmentTableImpl(helper(context));
    }

    public static MessageTableImpl messageTable(@NonNull Context context) {
        return new MessageTableImpl(helper(context));
    }

    public static UsersTableImpl usersTable(@NonNull Context context) {
        return new UsersTableImpl(helper(context));
    }
}
