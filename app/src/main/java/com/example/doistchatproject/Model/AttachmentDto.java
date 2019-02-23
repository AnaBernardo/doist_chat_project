package com.example.doistchatproject.Model;

import java.io.Serializable;

public class AttachmentDto implements Serializable {

    private String id;
    private String title;
    private String url;
    private String thumbnailUrl;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public AttachmentDto (String id, String title, String url, String thumbnailUrl) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }
}
