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
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.models.GenericContact;
import com.shamine.teamsmessagingapp.room.repositories.ContactRepository;
import com.shamine.teamsmessagingapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
    private final List<Contact> contacts;
    private final ChatActivity chatActivity;

    public ContactListAdapter(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        this.contacts = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Contact contact = contacts.get(position);
        holder.txtFullName.setText(contact.getName());
        holder.txtUsername.setVisibility(View.GONE);

        String picUrl = RetrofitApiClient.IMAGE_BASE_URL + contact.getPicUrl();
        Picasso.get()
                .load(picUrl)
                .placeholder(R.drawable.temp_user)
                .resize(300, 300)
                .centerCrop()
                .onlyScaleDown()
                .error(R.drawable.temp_user)
                .into(holder.civProfilePic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayListDialog(contact, view);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void displayListDialog(final Contact contact, View view) {
        AlertDialog.Builder listBuilder = new AlertDialog.Builder(chatActivity);

        List<ListItem> listItems = new ArrayList<>();
        listItems.add(new ListItem(ListItem.KEY_VIEW_CHAT, "View Chat"));
        listItems.add(new ListItem(ListItem.KEY_DELETE_CONTACT, "Delete Contact"));

        List<String> items = new ArrayList<>();
        for (ListItem l : listItems) {
            items.add(l.getValue());
        }

        listBuilder.setItems(items.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (listItems.get(i).getKey()) {
                    case ListItem.KEY_VIEW_CHAT:
                        Bundle bundle = new Bundle();
                        GenericContact gc = new GenericContact();
                        gc.setContact(contact);
                        gc.setChatGroup(null);

                        bundle.putString(ChatsFragment.CONTACT_KEY, new Gson().toJson(gc));
                        Navigation.findNavController(view).navigate(R.id.actionContactListToChats, bundle);
                        break;
                    case ListItem.KEY_DELETE_CONTACT:
                        AlertDialog.Builder conBuilder = Utils.constructAlertDialog(chatActivity,
                                "Delete Contact",
                                "Proceed to delete <b>" + contact.getName() + " (@" + contact.getUsername() + ")" + "</b>?");

                        conBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                chatActivity.displayDotsDialog("Deleting contact");

                                ContactRepository repository = new ContactRepository(chatActivity.getApplication());
                                repository.deleteContact(chatActivity, contact.getUserId());
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

    static class ListItem {
        private static final int KEY_VIEW_CHAT = 12;
        private static final int KEY_DELETE_CONTACT = 11;

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
        TextView txtFullName, txtUsername;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            civProfilePic = itemView.findViewById(R.id.civProfilePic);
        }
    }
}
