package com.shamine.teamsmessagingapp.adapters.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAddToGroupAdapter extends RecyclerView.Adapter<ContactAddToGroupAdapter.ViewHolder> {
    private final List<Contact> contacts;
    private final TextView txtBtnCreate;
    private final String buttonBaseText;

    public ContactAddToGroupAdapter(TextView txtBtnCreate, String buttonBaseText) {
        this.txtBtnCreate = txtBtnCreate;
        this.buttonBaseText = buttonBaseText;
        contacts = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_add_to_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        String username = "@" + contact.getUsername();
        holder.txtUsername.setText(username);
        holder.txtFullName.setText(contact.getName());
        holder.checkbox.setChecked(contact.isChecked());
        holder.topDivider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

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
                contact.setChecked(!contact.isChecked());
                holder.checkbox.setChecked(contact.isChecked());
                updateButtonText();
            }
        });

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemView.callOnClick();
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

    public int getCheckedCount() {
        int count = 0;
        for (Contact c : contacts) {
            if (c.isChecked()) {
                count++;
            }
        }

        return count;
    }

    public void updateButtonText() {
        int count = getCheckedCount();
        String buttonText = buttonBaseText + (count > 0 ? " (" + count + ")" : "");
        txtBtnCreate.setText(buttonText);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView civProfilePic;
        CheckBox checkbox;
        TextView txtFullName, txtUsername;
        View topDivider;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            civProfilePic = itemView.findViewById(R.id.civProfilePic);
            checkbox = itemView.findViewById(R.id.checkbox);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            topDivider = itemView.findViewById(R.id.topDivider);
        }
    }
}
