package com.example.doistchatproject.Model;

import java.io.Serializable;
import java.util.List;

public class DataDto implements Serializable {

    private List<MessageDetailsDto> messages;
    private List<UsersDetailsDto> users;

    public List<MessageDetailsDto> getMessages() {
        return messages;
    }

    public List<UsersDetailsDto> getUsers() {
        return users;
    }

}
