package com.example.doistchatproject.Callbacks;

import android.support.annotation.NonNull;

import com.example.doistchatproject.Model.MessageDetailsDto;

public interface OnDeleteMessageItemListener {
    void onDeleteMessageItemListener(@NonNull MessageDetailsDto messageDetailsDto);
}
