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
import com.shamine.teamsmessagingapp.room.models.ContactGroup;
import com.shamine.teamsmessagingapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactGroupAdapter extends ArrayAdapter<ContactGroup> {
    private final List<ContactGroup> contactGroupList;
    private EventListener listener;

    public ContactGroupAdapter(Context context, EventListener listener) {
        super(context, 0, new ArrayList<>());
        this.contactGroupList = new ArrayList<>();
        this.listener = listener;
    }

    public interface EventListener {
        void onItemSelected(ContactGroup contactGroup, int index);
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

        final ContactGroup cg = getItem(position);

        if (cg != null) {
            String picUrl = "";
            txtUsername.setVisibility(cg.isContact() ? View.VISIBLE : View.GONE);
            if (cg.isContact()) {
                String username = "@" + cg.getContactView().getContact().getUsername();
                txtUsername.setText(username);
                textViewName.setText(cg.getContactView().getContact().getName());
                if (!Utils.stringNullOrEmpty(cg.getContactView().getContact().getPicUrl())) {
                    picUrl = cg.getContactView().getContact().getPicUrl();
                }
            } else if (cg.isChatGroup()) {
                textViewName.setText(cg.getChatGroupView().getChatGroup().getTitle());
            }

            if (!Utils.stringNullOrEmpty(picUrl)) {
                Picasso.get()
                        .load(RetrofitApiClient.IMAGE_BASE_URL + picUrl)
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
                listener.onItemSelected(cg, position);
            }
        });

        return convertView;
    }

    private Filter contactFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ContactGroup> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(contactGroupList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ContactGroup cg : contactGroupList) {
                    if (cg.isContact() && (cg.getContactView().getContact().getName().toLowerCase().contains(filterPattern) ||
                            cg.getContactView().getContact().getUsername().toLowerCase().contains(filterPattern))) {
                        suggestions.add(cg);
                    } else if (!cg.isContact() && cg.isChatGroup() && cg.getChatGroupView().getChatGroup().getTitle().toLowerCase().contains(filterPattern)) {
                        suggestions.add(cg);
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
            if (((ContactGroup) resultValue).isContact()) {
                return ((ContactGroup) resultValue).getContactView().getContact().getName();
            } else if (((ContactGroup) resultValue).isChatGroup()) {
                return ((ContactGroup) resultValue).getChatGroupView().getChatGroup().getTitle();
            } else {
                return "";
            }
        }
    };

    public List<ContactGroup> getContactGroupList() {
        return contactGroupList;
    }
}
