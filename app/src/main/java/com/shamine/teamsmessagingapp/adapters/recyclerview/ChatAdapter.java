package com.shamine.teamsmessagingapp.adapters.recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.room.entities.MessageGroup;
import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;
import com.shamine.teamsmessagingapp.room.models.GenericMessage;
import com.shamine.teamsmessagingapp.room.repositories.GroupMessageRepository;
import com.shamine.teamsmessagingapp.room.repositories.PrivateMessageRepository;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final int TYPE_SENDER = 66;
    private static final int TYPE_RECIPIENT = 65;

    private final List<GenericMessage> genericMessageList;
    private final ChatActivity chatActivity;
    private final PrivateMessageRepository privateMessageRepository;
    private final GroupMessageRepository groupMessageRepository;

    public ChatAdapter(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        genericMessageList = new ArrayList<GenericMessage>();
        privateMessageRepository = new PrivateMessageRepository(chatActivity.getApplication());
        groupMessageRepository = new GroupMessageRepository(chatActivity.getApplication());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SENDER:
                View viewSender = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_sender, parent, false);
                return new ViewHolder(viewSender);
            case TYPE_RECIPIENT:
                View viewRecipient = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_chat_recipient, parent, false);
                return new ViewHolder(viewRecipient);
            default:
                Log.d("ChatAdapter", "Unknown view type");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GenericMessage message = genericMessageList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, H:mm", Locale.ENGLISH);

        if (message.getMessagePrivate() != null) {
            MessagePrivate m = message.getMessagePrivate();
            String msg = Utils.decrypt(m.getContent(), String.valueOf(m.getCreatedOn()),
                    String.valueOf(m.getSenderId()), String.valueOf(m.getRecipientId()));
            holder.txtContactName.setText("");
            holder.txtContactName.setVisibility(View.GONE);
            holder.txtMessage.setText(msg);
            holder.txtTime.setText(sdf.format(new Date(m.getCreatedOn())));

            privateMessageRepository.updateMessageRead(chatActivity, m);
        } else if (message.getMessageGroup() != null) {
            MessageGroup m = message.getMessageGroup();
            String msg = Utils.decrypt(m.getContent(), String.valueOf(m.getCreatedOn()),
                    String.valueOf(m.getSenderId()), String.valueOf(m.getChatGroupId()));
            if (getItemViewType(position) == TYPE_RECIPIENT) {
                holder.txtContactName.setText(m.getSenderName());
                holder.txtContactName.setVisibility(View.VISIBLE);
            } else {
                holder.txtContactName.setText("");
                holder.txtContactName.setVisibility(View.GONE);
            }
            holder.txtMessage.setText(msg);
            holder.txtTime.setText(sdf.format(new Date(m.getCreatedOn())));

            groupMessageRepository.updateMessageRead(chatActivity, m);
        }
    }

    @Override
    public int getItemCount() {
        return genericMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        GenericMessage message = genericMessageList.get(position);

        if (message.getMessagePrivate() != null) {
            MessagePrivate msg = message.getMessagePrivate();
            return msg.getSenderId() == chatActivity.getLoggedInUser().getUserId() ? TYPE_SENDER : TYPE_RECIPIENT;
        } else if (message.getMessageGroup() != null) {
            MessageGroup msg = message.getMessageGroup();
            return msg.getSenderId() == chatActivity.getLoggedInUser().getUserId() ? TYPE_SENDER : TYPE_RECIPIENT;
        } else {
            return 0;
        }
    }

    public List<GenericMessage> getGenericMessageList() {
        return genericMessageList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTime, txtContactName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtContactName = itemView.findViewById(R.id.txtContactName);
        }
    }
}
