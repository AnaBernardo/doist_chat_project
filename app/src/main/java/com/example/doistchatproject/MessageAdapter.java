package com.example.doistchatproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.doistchatproject.Callbacks.OnDeleteAttachmentListener;
import com.example.doistchatproject.Callbacks.OnDeleteMessageItemListener;
import com.example.doistchatproject.Callbacks.OnItemClickListener;
import com.example.doistchatproject.Callbacks.OnLoadMoreListener;
import com.example.doistchatproject.Database.DatabaseRepository;
import com.example.doistchatproject.GlideUtils.GlideLoadImages;
import com.example.doistchatproject.Model.AttachmentDto;
import com.example.doistchatproject.Model.MessageDetailsDto;
import com.example.doistchatproject.Model.UsersDetailsDto;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter {

    private final String CURRENT_USER_USERNAME = "ME";
    private final int VIEW_PROG = 0;
    private final int VIEW_CURRENT_USER = 1;
    private final int VIEW_OTHER_USER = 2;

    private List<MessageDetailsDto> messageDetailsDtoList;

    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private final DatabaseRepository databaseRepository;
    private final Activity activity;
    private OnItemClickListener listener;
    private OnDeleteMessageItemListener onDeleteItemListener;
    private OnDeleteAttachmentListener onDeleteAttachmentListener;

    public MessageAdapter(
            @NonNull List<MessageDetailsDto> messageDetailsDtoList,
            @NonNull RecyclerView recyclerView,
            @NonNull DatabaseRepository databaseRepository,
            @NonNull Activity activity) {

        this.messageDetailsDtoList = messageDetailsDtoList;
        this.databaseRepository = databaseRepository;
        this.activity = activity;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {

                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }


    @Override
    public int getItemViewType(int position) {

        int viewType;

        if (messageDetailsDtoList.get(position) != null) {

            if (messageDetailsDtoList.get(position).getUserId() == VIEW_CURRENT_USER) {
                viewType = VIEW_CURRENT_USER;
            } else {
                viewType = VIEW_OTHER_USER;
            }

        } else {
            viewType = VIEW_PROG;
        }

        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        switch(viewType) {
            case VIEW_CURRENT_USER:

                View view_current_user = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_row_current_user, parent, false);

                vh = new CurrentUserTypeViewHolder(view_current_user);
                break;

            case VIEW_OTHER_USER:

                View view_other_user = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_row_other_users, parent, false);

                vh = new OtherUserTypeViewHolder(view_other_user);
                break;

            default:

                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_progressbar, parent, false);

                vh = new ProgressViewHolder(v);
                break;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @NonNull int position) {

        if (holder instanceof ProgressViewHolder) {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);

        } else {

            final MessageDetailsDto messageDetailsDto = messageDetailsDtoList.get(position);
            final String messageContent = messageDetailsDto.getContent();

            if (holder instanceof CurrentUserTypeViewHolder) {

                setCurrentUserTypeRow(holder, messageDetailsDto, messageContent);

            } else if (holder instanceof OtherUserTypeViewHolder) {

                setOtherUserTypeRow(holder, messageDetailsDto, messageContent);
            }
        }
    }

    private void setCurrentUserTypeRow(
            @NonNull RecyclerView.ViewHolder holder,
            @NonNull MessageDetailsDto messageDetailsDto,
            @NonNull String messageContent) {

        final CurrentUserTypeViewHolder currentUserTypeViewHolder = (CurrentUserTypeViewHolder) holder;
        ((CurrentUserTypeViewHolder) holder).linearLayout.removeAllViews();

        currentUserTypeViewHolder.userName.setText(CURRENT_USER_USERNAME);
        currentUserTypeViewHolder.userMessage.setText(messageContent);
        currentUserTypeViewHolder.cardView.setTag(messageDetailsDto);


        if (messageDetailsDto.getAttachments() != null && messageDetailsDto.getAttachments().size() > 0) {
            setAttachmentThumbnail(currentUserTypeViewHolder, messageDetailsDto.getAttachments());
        }
    }

    private void setOtherUserTypeRow(
            @NonNull RecyclerView.ViewHolder holder,
            @NonNull MessageDetailsDto messageDetailsDto,
            @NonNull String messageContent) {

        UsersDetailsDto usersDetailsDto =  databaseRepository.getUserName(String.valueOf(messageDetailsDto.getUserId()));

        OtherUserTypeViewHolder otherUserTypeViewHolder = (OtherUserTypeViewHolder)holder;

        otherUserTypeViewHolder.userName.setText(usersDetailsDto.getName());
        otherUserTypeViewHolder.userMessage.setText(messageContent);
        otherUserTypeViewHolder.cardView.setTag(messageDetailsDto);

        loadImage(otherUserTypeViewHolder.imageView, usersDetailsDto.getAvatarId());

        ((OtherUserTypeViewHolder) holder).linearLayout.removeAllViews();


        if (messageDetailsDto.getAttachments() != null && messageDetailsDto.getAttachments().size() > 0) {
            setAttachmentThumbnail(otherUserTypeViewHolder, messageDetailsDto.getAttachments());
        }
    }

    public void setLoaded() {
        loading = false;
    }

    /**
     * Add the messageAttachment to the current layout
     * @param holder
     * @param attachmentDtoList
     */
    private void setAttachmentThumbnail(@NonNull RecyclerView.ViewHolder holder, @NonNull List<AttachmentDto> attachmentDtoList) {

        final int MARGIN_TOP = 10;
        final int MARGIN_BOTTOM = 10;

        for (AttachmentDto attachmentDto : attachmentDtoList) {

            ImageView imageLinear = (ImageView) View.inflate(
                    activity,
                    R.layout.layout_attachment_imageview, null);

            imageLinear.setPadding(0, MARGIN_TOP,0, MARGIN_BOTTOM);

            loadAttachment(attachmentDto.getThumbnailUrl(), imageLinear);
            imageLinear.setTag(attachmentDto);

            imageLinear.setOnClickListener(attachmentOnClickListener);
            imageLinear.setOnLongClickListener(rowLongPressedListener);

            if (holder instanceof CurrentUserTypeViewHolder) {
                ((CurrentUserTypeViewHolder)holder).linearLayout.addView(imageLinear);
            } else {
                ((OtherUserTypeViewHolder)holder).linearLayout.addView(imageLinear);
            }

        }
    }

    @Override
    public long getItemId(int position) {
        if (messageDetailsDtoList != null && messageDetailsDtoList.get(position) != null) {
            return (long) messageDetailsDtoList.get(position).hashCode();
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return messageDetailsDtoList.size();
    }

    /**
     * Listener -> Set delete attachment
     * @param onDeleteAttachmentListener
     */
    public void setOnDeleteAttachmentListener(@NonNull OnDeleteAttachmentListener onDeleteAttachmentListener) {
        this.onDeleteAttachmentListener = onDeleteAttachmentListener;
    }

    /**
     * Listener -> Set delete Message
     * @param onDeleteItemListener
     */
    public void setDeleteMessageItemListener(@NonNull OnDeleteMessageItemListener onDeleteItemListener) {
        this.onDeleteItemListener = onDeleteItemListener;
    }

    /**
     * Listener -> Set load more messages
     * @param onLoadMoreListener
     */
    public void setOnLoadMoreListener(@NonNull OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    /**
     * Listener -> Set attachment clicked
     * @param onItemClickListener
     */
    public void setAttachmentOnClickListener(@NonNull OnItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
    }

    /**
     * Listener -> Attachment
     */
    private View.OnClickListener attachmentOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {

            AttachmentDto attachmentDto = (AttachmentDto) v.getTag();
            listener.onItemClick(attachmentDto.getUrl());
        }
    };

    /**
     * Show alertDialog when a row is long pressed
     */
    private View.OnLongClickListener rowLongPressedListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            setAlertDialog(v);
            return false;
        }
    };

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public class CurrentUserTypeViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView userMessage;
        private LinearLayout linearLayout;
        private CardView cardView;

        public CurrentUserTypeViewHolder(View itemView) {
            super(itemView);

            this.userName = itemView.findViewById(R.id.name);
            this.userMessage = itemView.findViewById(R.id.message);
            linearLayout = itemView.findViewById(R.id.message_attachments);
            cardView = itemView.findViewById(R.id.cardview);

            cardView.setOnLongClickListener(rowLongPressedListener);
        }
    }

    public class OtherUserTypeViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView userMessage;
        private ImageView imageView;
        private LinearLayout linearLayout;
        private CardView cardView;

        public OtherUserTypeViewHolder(View itemView) {
            super(itemView);

            this.userName = itemView.findViewById(R.id.name);
            this.userMessage = itemView.findViewById(R.id.message);
            this.imageView = itemView.findViewById(R.id.profile_image);
            linearLayout = itemView.findViewById(R.id.message_attachments);
            cardView = itemView.findViewById(R.id.cardview);

            cardView.setOnLongClickListener(rowLongPressedListener);
        }
    }

    private void loadAttachment(@NonNull final String url, @NonNull final ImageView imageView) {
        GlideLoadImages.loadThumbnailImage(activity, url, imageView);
    }

    private void loadImage(@NonNull final ImageView imageView, @NonNull final String url) {
        GlideLoadImages.loadUserAvatarImage(activity, url, imageView);
    }

    private void setAlertDialog(final View v) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(activity.getString(R.string.alert_dialog_message));
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (v.getTag() instanceof MessageDetailsDto) {
                    MessageDetailsDto messageDetailsDto = (MessageDetailsDto) v.getTag();
                    onDeleteItemListener.onDeleteMessageItemListener(messageDetailsDto);

                } else if (v.getTag() instanceof AttachmentDto) {
                    AttachmentDto attachmentDto = (AttachmentDto) v.getTag();
                    onDeleteAttachmentListener.onDeleteItemListener(attachmentDto);
                }
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });

      builder.create().show();
    }
}