package com.example.doistchatproject.Callbacks;

import android.support.annotation.NonNull;

import com.example.doistchatproject.Model.AttachmentDto;

public interface OnDeleteAttachmentListener {
    void onDeleteItemListener(@NonNull AttachmentDto attachmentDto);
}
