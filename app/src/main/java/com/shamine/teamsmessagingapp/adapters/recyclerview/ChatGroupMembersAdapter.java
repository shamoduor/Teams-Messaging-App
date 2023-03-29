package com.shamine.teamsmessagingapp.adapters.recyclerview;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.models.GenericContact;
import com.shamine.teamsmessagingapp.room.repositories.ChatGroupMemberRepository;
import com.shamine.teamsmessagingapp.room.repositories.ContactRepository;
import com.shamine.teamsmessagingapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatGroupMembersAdapter extends RecyclerView.Adapter<ChatGroupMembersAdapter.ViewHolder> {
    private final List<ChatGroupMember> chatGroupMembers;
    private final ChatActivity chatActivity;

    public ChatGroupMembersAdapter(ChatActivity chatActivity) {
        chatGroupMembers = new ArrayList<>();
        this.chatActivity = chatActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_group_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatGroupMember member = chatGroupMembers.get(position);
        final String ADMIN = "Admin";
        final String username = "@" + member.getUsername();
        holder.txtRole.setVisibility(member.isAdmin() ? View.VISIBLE : View.GONE);
        holder.txtRole.setText(ADMIN);
        holder.txtFullName.setText(member.getFullName());
        holder.txtUsername.setText(username);

        if (member.getPicUrl() != null && !member.getPicUrl().isEmpty()) {
            Picasso.get()
                    .load(RetrofitApiClient.IMAGE_BASE_URL + member.getPicUrl())
                    .placeholder(R.drawable.temp_user)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.temp_user)
                    .into(holder.civProfilePic);
        } else {
            holder.civProfilePic.setImageResource(R.drawable.temp_user);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (member.getUserId() != chatActivity.getLoggedInUser().getUserId()) {
                    displayListDialog(member, view);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatGroupMembers.size();
    }

    public List<ChatGroupMember> getChatGroupMembers() {
        return chatGroupMembers;
    }

    private void displayListDialog(ChatGroupMember member, View view) {
        boolean loggedInUserIsAdmin = false;
        for (ChatGroupMember m : chatGroupMembers) {
            if (m.isAdmin() && m.getUserId() == chatActivity.getLoggedInUser().getUserId()) {
                loggedInUserIsAdmin = true;
                break;
            }
        }

        AlertDialog.Builder listBuilder = new AlertDialog.Builder(chatActivity);
        List<ContactListAdapter.ListItem> listItems = new ArrayList<>();
        listItems.add(new ContactListAdapter.ListItem(ListItem.KEY_MESSAGE, "Message"));
        if (loggedInUserIsAdmin) {
            listItems.add(new ContactListAdapter.ListItem(ListItem.KEY_REMOVE_MEMBER, "Remove member"));
        }

        List<String> items = new ArrayList<>();
        for (ContactListAdapter.ListItem l : listItems) {
            items.add(l.getValue());
        }

        listBuilder.setItems(items.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (listItems.get(i).getKey()) {
                    case ListItem.KEY_MESSAGE:
                        Contact contact = new Contact();
                        contact.setUserId(member.getUserId());
                        contact.setName(member.getFullName());
                        contact.setUsername(member.getUsername());
                        contact.setPicUrl(member.getPicUrl());
                        contact.setLoggedInUserId(chatActivity.getLoggedInUser().getUserId());
                        ContactRepository contactRepository = new ContactRepository(chatActivity.getApplication());
                        contactRepository.addContactSilently(chatActivity, contact);

                        Bundle bundle = new Bundle();
                        GenericContact genericContact = new GenericContact();
                        genericContact.setChatGroup(null);
                        genericContact.setContact(contact);
                        bundle.putString(ChatsFragment.CONTACT_KEY, new Gson().toJson(genericContact));
                        Navigation.findNavController(view).navigate(R.id.actionGroupInfoToChats, bundle);
                        break;
                    case ListItem.KEY_REMOVE_MEMBER:
                        AlertDialog.Builder conBuilder = Utils.constructAlertDialog(chatActivity,
                                "Remove Member",
                                "Proceed to delete <b>" + member.getFullName() + " (@" + member.getUsername() + ")" + "</b>?");

                        conBuilder.setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                chatActivity.displayDotsDialog("Removing member");
                                ChatGroupMemberRepository repository = new ChatGroupMemberRepository(chatActivity.getApplication());
                                repository.removeGroupMember(chatActivity, member.getMemberId());
                            }
                        });

                        conBuilder.setNegativeButton("Cancel", null);
                        conBuilder.show();
                        break;
                }
            }
        });

        AlertDialog dialog = listBuilder.create();
        dialog.show();
    }

    private static class ListItem {
        private static final int KEY_MESSAGE = 21;
        private static final int KEY_REMOVE_MEMBER = 22;

        private final int key;
        private final String value;

        ListItem(int key, String value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civProfilePic;
        TextView txtRole, txtFullName, txtUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civProfilePic = itemView.findViewById(R.id.civProfilePic);
            txtRole = itemView.findViewById(R.id.txtRole);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtUsername = itemView.findViewById(R.id.txtUsername);
        }
    }
}
