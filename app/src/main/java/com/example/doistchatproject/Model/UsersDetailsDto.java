package com.example.doistchatproject.Model;

import java.io.Serializable;

public class UsersDetailsDto implements Serializable {

    private int id;
    private String name;
    private String avatarId;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public UsersDetailsDto(int id, String name, String avatarId) {
        this.id = id;
        this.name = name;
        this.avatarId = avatarId;
    }
}
