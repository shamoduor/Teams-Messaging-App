package com.shamine.teamsmessagingapp.adapters.autocomplete;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AutoCompleteContacts extends ArrayAdapter<Contact> {
    private final List<Contact> contactList;
    private final EventListener listener;

    public interface EventListener {
        void onItemSelected(Contact contact, int index);
    }

    public AutoCompleteContacts(Context context, EventListener listener) {
        super(context, 0, new ArrayList<>());
        this.contactList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return contactFilter;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_contact_autocompete_row, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.txtFullName);
        TextView txtUsername = convertView.findViewById(R.id.txtUsername);
        CircleImageView civProfilePic = convertView.findViewById(R.id.civProfilePic);

        final Contact contact = getItem(position);

        if (contact != null) {
            String username = "@" + contact.getUsername();
            txtUsername.setText(username);
            textViewName.setText(contact.getName());
            if (contact.getPicUrl() != null && !contact.getPicUrl().isEmpty()) {
                Picasso.get()
                        .load(RetrofitApiClient.IMAGE_BASE_URL + contact.getPicUrl())
                        .placeholder(R.drawable.temp_user)
                        .fit()
                        .centerCrop()
                        .error(R.drawable.temp_user)
                        .into(civProfilePic);
            } else {
                civProfilePic.setImageResource(R.drawable.temp_user);
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemSelected(contact, position);
            }
        });

        return convertView;
    }

    private Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Contact> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(contactList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Contact contact : contactList) {
                    if (contact.getName().toLowerCase().contains(filterPattern) ||
                            contact.getUsername().toLowerCase().contains(filterPattern)) {
                        suggestions.add(contact);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results.values != null) {
                addAll((List<Contact>) results.values);
            }
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Contact) resultValue).getName();
        }
    };

    public List<Contact> getContactList() {
        return contactList;
    }
}
