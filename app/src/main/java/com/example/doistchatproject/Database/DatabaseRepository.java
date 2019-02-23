package com.example.doistchatproject.Database;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.example.doistchatproject.Database.Tables.AttachmentTableImpl;
import com.example.doistchatproject.Database.Tables.MessageTableImpl;
import com.example.doistchatproject.Database.Tables.UsersTableImpl;
import com.example.doistchatproject.Model.AttachmentDto;
import com.example.doistchatproject.Model.DataDto;
import com.example.doistchatproject.Model.MessageDetailsDto;
import com.example.doistchatproject.Model.UsersDetailsDto;
import com.example.doistchatproject.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class DatabaseRepository {

    private final Resources resources;
    private final Context context;

    public DatabaseRepository(Resources resources, Context context) {
        this.resources = resources;
        this.context = context;
    }

    public void readJsonAndPopulateTables() {
        final InputStream raw = resources.openRawResource(R.raw.data);
        final Reader rd = new BufferedReader(new InputStreamReader(raw));
        final Gson gson = new Gson();
        final DataDto obj = gson.fromJson(rd, DataDto.class);

        populateMessageTable(obj.getMessages());
        populateUsersTable(obj.getUsers());
    }

    private void populateMessageTable(@NonNull List<MessageDetailsDto> messageDetailsDtoList) {

        final MessageTableImpl messageTable = DatabaseModule.messageTable(context);

        for(int i = 0; i < messageDetailsDtoList.size(); i++) {

            MessageDetailsDto messageDetailsDto = messageDetailsDtoList.get(i);
            messageTable.add(messageDetailsDto);

            if (messageDetailsDto.getAttachments() != null && messageDetailsDto.getAttachments().size() > 0){
                addAllAttachmentsToDatabase(messageDetailsDto.getId(), messageDetailsDto.getAttachments());
            }
        }
    }

    private void populateUsersTable(@NonNull List<UsersDetailsDto> usersDetailsDtoList) {
        final UsersTableImpl usersTable = DatabaseModule.usersTable(context);

        for(int i = 0; i < usersDetailsDtoList.size(); i++) {
            usersTable.add(usersDetailsDtoList.get(i));
        }
    }

    private void addAllAttachmentsToDatabase(int messageId, List<AttachmentDto> attachmentDtoList) {

        final AttachmentTableImpl attachmentTable = DatabaseModule.attachmentTable(context);

        for(int i = 0; i < attachmentDtoList.size(); i++) {
            attachmentTable.add(messageId, attachmentDtoList.get(i));
        }
    }

    public List<MessageDetailsDto> getMessageOffSet(int startingPoint) {
        final  MessageTableImpl messageTable = DatabaseModule.messageTable(context);
        return messageTable.getMessageRange(context, startingPoint);
    }

    public UsersDetailsDto getUserName(String id) {

        final UsersTableImpl usersTable = DatabaseModule.usersTable(context);
        return usersTable.getUser(id);
    }

    public void deleteMessageAndRespectiveAttachment(String id) {
        final MessageTableImpl messageTable = DatabaseModule.messageTable(context);
        messageTable.deleteMessageEntry(id);
        final AttachmentTableImpl attachmentTable = DatabaseModule.attachmentTable(context);
        attachmentTable.deleteAllAttachmentsFromMessage(id);
    }

    public void deleteAttachment(String id) {
        final AttachmentTableImpl attachmentTable = DatabaseModule.attachmentTable(context);
        attachmentTable.deleteAttachmentEntry(id);
    }

    public int getMessageIdFromAttachment(String attachmentId) {
        final AttachmentTableImpl attachmentTable = DatabaseModule.attachmentTable(context);
        return attachmentTable.getMessageID(attachmentId);
    }

    public MessageDetailsDto getMessage(int id) {
        final MessageTableImpl messageTable = DatabaseModule.messageTable(context);
        return messageTable.getMessage(String.valueOf(id));
    }
}
