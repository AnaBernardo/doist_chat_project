package com.example.doistchatproject.Model;

import java.io.Serializable;
import java.util.List;

public class MessageDetailsDto implements Serializable {

    private int id;
    private int userId;
    private String content;
    private List<AttachmentDto> attachments;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public List<AttachmentDto> getAttachments() {
        return attachments;
    }

    public MessageDetailsDto (int id, int userId, String content, List<AttachmentDto> attachmentDtos) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.attachments = attachmentDtos;
    }
}
