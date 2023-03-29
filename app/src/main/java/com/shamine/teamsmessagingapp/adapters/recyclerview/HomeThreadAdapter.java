package com.shamine.teamsmessagingapp.adapters.recyclerview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.fragments.chats.ChatsFragment;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.models.ContactGroup;
import com.shamine.teamsmessagingapp.room.models.GenericContact;
import com.shamine.teamsmessagingapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeThreadAdapter extends RecyclerView.Adapter<HomeThreadAdapter.ViewHolder> {
    private final List<ContactGroup> contactGroupList;
    private final ChatActivity chatActivity;

    public HomeThreadAdapter(ChatActivity chatActivity) {
        this.contactGroupList = new ArrayList<>();
        this.chatActivity = chatActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thread_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ContactGroup contactGroup = contactGroupList.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);

        if (contactGroup.isContact()) {
            final Contact contact = contactGroup.getContactView().getContact();
            holder.txtName.setText(contact.getName());
            if (Utils.stringNullOrEmpty(contact.getLastMessage()) || contact.getLastMessageTime() == 0) {
                holder.txtMsgTime.setText("");
            } else {
                holder.txtMsgTime.setText(sdf.format(new Date(contact.getLastMessageTime())));
            }
            String lastMessage = (contact.getLastMessageSenderId() == chatActivity.getLoggedInUser().getUserId() ? "You: " : "") + contact.getLastMessage();
            holder.txtTopMsg.setText(lastMessage);

            if (contactGroup.getNumNewMsgs() > 0) {
                holder.txtNumNew.setText(contactGroup.getNumNewMsgs());
                holder.txtNumNew.setVisibility(View.VISIBLE);
            } else {
                holder.txtNumNew.setVisibility(View.GONE);
            }

            setPicture(contact.getPicUrl(), holder.civProfilePic, false);

            holder.itemView.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                GenericContact genericContact = new GenericContact();
                genericContact.setContact(contact);
                genericContact.setChatGroup(null);
                bundle.putString(ChatsFragment.CONTACT_KEY, new Gson().toJson(genericContact));
                Navigation.findNavController(view).navigate(R.id.actionHomeToChats, bundle);
            });
        } else if (contactGroup.isChatGroup()) {
            ChatGroup chatGroup = contactGroup.getChatGroupView().getChatGroup();
            holder.txtName.setText(chatGroup.getTitle());
            if (Utils.stringNullOrEmpty(chatGroup.getLastMessage()) || chatGroup.getLastMessageTime() == 0) {
                holder.txtMsgTime.setText("");
            } else {
                holder.txtMsgTime.setText(sdf.format(new Date(chatGroup.getLastMessageTime())));
            }

            String lastMessage = (chatGroup.getLastMessageSenderId() == chatActivity.getLoggedInUser().getUserId() ? "You" : chatGroup.getLastMessageSenderName()) + ": " + chatGroup.getLastMessage();
            holder.txtTopMsg.setText(lastMessage);

            setPicture(chatGroup.getPicUrl(), holder.civProfilePic, true);

            if (contactGroup.getNumNewMsgs() > 0) {
                holder.txtNumNew.setText(contactGroup.getNumNewMsgs());
                holder.txtNumNew.setVisibility(View.VISIBLE);
            } else {
                holder.txtNumNew.setVisibility(View.GONE);
            }

            holder.itemView.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                GenericContact genericContact = new GenericContact();
                genericContact.setChatGroup(chatGroup);
                genericContact.setContact(null);
                bundle.putString(ChatsFragment.CONTACT_KEY, new Gson().toJson(genericContact));
                Navigation.findNavController(view).navigate(R.id.actionHomeToChats, bundle);
            });
        }
    }

    @Override
    public int getItemCount() {
        return contactGroupList.size();
    }

    public List<ContactGroup> getContactGroupList() {
        return contactGroupList;
    }

    private void setPicture(String picUrl, CircleImageView civProfilePic, boolean isGroup) {
        int defaultPic = isGroup ? R.drawable.ic_group : R.drawable.temp_user;

        if (!Utils.stringNullOrEmpty(picUrl)) {
            Picasso.get()
                    .load(RetrofitApiClient.IMAGE_BASE_URL + picUrl)
                    .placeholder(defaultPic)
                    .resize(300, 300)
                    .centerCrop()
                    .onlyScaleDown()
                    .error(defaultPic)
                    .into(civProfilePic);
        } else {
            civProfilePic.setImageResource(defaultPic);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civProfilePic;
        TextView txtName, txtTopMsg, txtMsgTime, txtNumNew;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civProfilePic = itemView.findViewById(R.id.civProfilePic);
            txtName = itemView.findViewById(R.id.txtName);
            txtTopMsg = itemView.findViewById(R.id.txtTopMsg);
            txtMsgTime = itemView.findViewById(R.id.txtMsgTime);
            txtNumNew = itemView.findViewById(R.id.txtNumNew);
        }
    }
}
