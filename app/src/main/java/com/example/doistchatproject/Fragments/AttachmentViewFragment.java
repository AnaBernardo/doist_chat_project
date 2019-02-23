package com.example.doistchatproject.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.doistchatproject.GlideUtils.GlideLoadImages;
import com.example.doistchatproject.R;

import static com.example.doistchatproject.Fragments.MessageViewFragment.THUMBNAIL_PARAM;

public class AttachmentViewFragment extends Fragment {

    private String item;
    private static final String DEFAULT = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       final View result = inflater.inflate(R.layout.layout_fragment_attachment, container, false);

       final Bundle bundle = this.getArguments();
        if (bundle != null) {
            item = bundle.getString(THUMBNAIL_PARAM, DEFAULT);
        }

        return result;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ImageView lock = view.findViewById(R.id.back_image);
        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        });

       final ImageView imageView = view.findViewById(R.id.attachment_full_imageview);
       GlideLoadImages.loadAttachmentImage(getContext(), item, imageView);
    }
}
