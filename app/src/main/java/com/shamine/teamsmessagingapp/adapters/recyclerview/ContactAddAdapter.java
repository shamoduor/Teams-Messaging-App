package com.shamine.teamsmessagingapp.adapters.recyclerview;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.repositories.ContactRepository;
import com.shamine.teamsmessagingapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAddAdapter extends RecyclerView.Adapter<ContactAddAdapter.ViewHolder> {
    private final List<Contact> searchResults;
    private final ChatActivity chatActivity;

    public ContactAddAdapter(ChatActivity chatActivity) {
        this.chatActivity = chatActivity;
        this.searchResults = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Contact result = searchResults.get(position);
        String username = "@" + result.getUsername();
        holder.txtFullName.setText(result.getName());
        holder.txtUsername.setText(username);

        String picUrl = RetrofitApiClient.IMAGE_BASE_URL + result.getPicUrl();
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
                AlertDialog.Builder alertDialog = Utils.constructAlertDialog(chatActivity,
                        "Add Contact",
                        "Proceed to add <b>" + result.getName() + "</b> (<b>" + username + "</b>) as a contact?");
                alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        chatActivity.displayDotsDialog("Adding contact");
                        ContactRepository repository = new ContactRepository(chatActivity.getApplication());
                        repository.addContact(chatActivity, result);
                    }
                });
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public List<Contact> getSearchResults() {
        return searchResults;
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
