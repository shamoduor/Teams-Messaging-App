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
import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.models.ContactGroup;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AutoCompleteChats extends ArrayAdapter<ContactGroup> {
    private final List<ContactGroup> contactGroupList;
    private final EventListener listener;

    public interface EventListener {
        void onItemSelected(ContactGroup contactGroup, int index);
    }

    public AutoCompleteChats(Context context, EventListener listener) {
        super(context, 0, new ArrayList<>());
        this.contactGroupList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return contactGroupFilter;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_chat_autocomplete_row, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.txtName);
        TextView txtTopMsg = convertView.findViewById(R.id.txtTopMsg);
        CircleImageView civProfilePic = convertView.findViewById(R.id.civProfilePic);

        final ContactGroup contactGroup = getItem(position);

        if (contactGroup != null) {
            if (contactGroup.isContact()) {
                final Contact contact = contactGroup.getContactView().getContact();
                textViewName.setText(contact.getName());
                txtTopMsg.setText(contact.getLastMessage());

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
            } else if (contactGroup.isChatGroup()) {
                final ChatGroup chatGroup = contactGroup.getChatGroupView().getChatGroup();
                textViewName.setText(chatGroup.getTitle());
                txtTopMsg.setText(chatGroup.getLastMessage());
                civProfilePic.setImageResource(R.drawable.temp_user);
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemSelected(contactGroup, position);
            }
        });

        return convertView;
    }

    private final Filter contactGroupFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ContactGroup> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(contactGroupList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ContactGroup contactGroup : contactGroupList) {
                    if (contactGroup.getContactView().getContact().getName().toLowerCase().contains(filterPattern) ||
                            contactGroup.getContactView().getContact().getUsername().toLowerCase().contains(filterPattern) ||
                            (contactGroup.getChatGroupView().getChatGroup() != null
                                    && contactGroup.getChatGroupView().getChatGroup().getTitle().toLowerCase().contains(filterPattern))) {
                        suggestions.add(contactGroup);
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
                addAll((List<ContactGroup>) results.values);
            }
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            ContactGroup contactGroup = (ContactGroup) resultValue;
            if (contactGroup.isContact()) {
                Contact contact = contactGroup.getContactView().getContact();
                return contact.getName();
            } else if (contactGroup.isChatGroup()) {
                ChatGroup chatGroup = contactGroup.getChatGroupView().getChatGroup();
                return chatGroup.getTitle();
            } else {
                return "";
            }
        }
    };

    public List<ContactGroup> getContactGroupList() {
        return contactGroupList;
    }
}
