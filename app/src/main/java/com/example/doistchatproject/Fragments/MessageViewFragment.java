package com.example.doistchatproject.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doistchatproject.Callbacks.OnDeleteAttachmentListener;
import com.example.doistchatproject.Callbacks.OnDeleteMessageItemListener;
import com.example.doistchatproject.Callbacks.OnItemClickListener;
import com.example.doistchatproject.Callbacks.OnLoadMoreListener;
import com.example.doistchatproject.Database.DatabaseRepository;
import com.example.doistchatproject.MessageAdapter;
import com.example.doistchatproject.Model.AttachmentDto;
import com.example.doistchatproject.Model.MessageDetailsDto;
import com.example.doistchatproject.R;

import java.util.ArrayList;
import java.util.List;

public class MessageViewFragment extends Fragment {

    private final static int LOAD_DATA_STARTING_POINT = 0;
    public final static String THUMBNAIL_PARAM = "thumbnailUrl";
    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private DatabaseRepository databaseRepository;

    private List<MessageDetailsDto> messageDetailsDtoList;

    protected Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.layout_fragment_message_view, container, false);

        this.databaseRepository = new DatabaseRepository(getResources(), getContext());
        this.messageDetailsDtoList = new ArrayList<>();
        this.handler = new Handler();

        return result;
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        loadData(LOAD_DATA_STARTING_POINT);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MessageAdapter(messageDetailsDtoList, mRecyclerView, databaseRepository, getActivity());
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);
        ((DefaultItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        processAttachmentClickedAction();
        processLoadMore();
        processDeleteMessageAction();
        processDeleteAttachmentAction();

    }

    /**
     * Load more messages to messageList to be displayed
     */
    private void processLoadMore() {
        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                messageDetailsDtoList.add(null);
                mAdapter.notifyItemInserted(messageDetailsDtoList.size() - 1);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        messageDetailsDtoList.remove(messageDetailsDtoList.size() - 1);
                        mAdapter.notifyItemRemoved(messageDetailsDtoList.size());
                        int start = messageDetailsDtoList.size();

                        loadData(start + 1);

                        mAdapter.notifyItemInserted(messageDetailsDtoList.size());
                        mAdapter.setLoaded();
                    }
                }, 200);
            }
        });

    }

    private void processAttachmentClickedAction() {
        mAdapter.setAttachmentOnClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull String item) {

                Fragment fragment = new AttachmentViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString(THUMBNAIL_PARAM, item);
                fragment.setArguments(bundle);

                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.general_placeholder, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void processDeleteMessageAction() {
        mAdapter.setDeleteMessageItemListener(new OnDeleteMessageItemListener() {
            @Override
            public void onDeleteMessageItemListener(@NonNull MessageDetailsDto messageDetailsDto) {

                databaseRepository.deleteMessageAndRespectiveAttachment(String.valueOf(messageDetailsDto.getId()));
                messageDetailsDtoList.remove(messageDetailsDto);

                //delete with id
                mAdapter.notifyItemRemoved(messageDetailsDtoList.size());
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void processDeleteAttachmentAction() {

        mAdapter.setOnDeleteAttachmentListener(new OnDeleteAttachmentListener() {
            @Override
            public void onDeleteItemListener(@NonNull AttachmentDto attachmentDto) {

               final int messageID = databaseRepository.getMessageIdFromAttachment(attachmentDto.getId());

                final MessageDetailsDto messageDetailsFromBD = databaseRepository.getMessage(messageID);

                for (MessageDetailsDto messageDetailsDto : messageDetailsDtoList) {

                    if (messageDetailsDto.getId() == messageDetailsFromBD.getId()) {
                        messageDetailsDto.getAttachments().remove(attachmentDto);
                    }
                }

                databaseRepository.deleteAttachment(attachmentDto.getId());

                mAdapter.notifyItemRemoved(messageDetailsDtoList.size());
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    // Load initial messages to messageDetailsList
    private void loadData(int startingPoint) {

        final List<MessageDetailsDto> messageDetailsDtosDatabase = databaseRepository.getMessageOffSet(startingPoint);

        for (MessageDetailsDto messageDetailsDto : messageDetailsDtosDatabase) {
            messageDetailsDtoList.add(messageDetailsDto);
        }
    }
}
