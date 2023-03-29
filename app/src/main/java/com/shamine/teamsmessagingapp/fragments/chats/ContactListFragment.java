package com.shamine.teamsmessagingapp.fragments.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.adapters.autocomplete.AutoCompleteContacts;
import com.shamine.teamsmessagingapp.adapters.recyclerview.ContactListAdapter;
import com.shamine.teamsmessagingapp.databinding.FragmentContactListBinding;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.viewmodel.ContactViewModel;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.util.List;
import java.util.Objects;

public class ContactListFragment extends Fragment {
    private ChatActivity chatActivity;
    private FragmentContactListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatActivity = (ChatActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ContactListAdapter adapterRecyclerView = new ContactListAdapter(chatActivity);
        AutoCompleteContacts adapterAutoComplete = new AutoCompleteContacts(chatActivity, new AutoCompleteContacts.EventListener() {
            @Override
            public void onItemSelected(Contact contact, int index) {
                binding.autoCompleteTextView.setText("");
                for (int a = 0; a < adapterRecyclerView.getContacts().size(); a++) {
                    Contact c = adapterRecyclerView.getContacts().get(a);
                    if (c.getUserId() == contact.getUserId()) {
                        Objects.requireNonNull(binding.recyclerView.findViewHolderForAdapterPosition(a)).itemView.performClick();
                        break;
                    }
                }
                Utils.dismissKeypad(chatActivity);
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(chatActivity));
        binding.recyclerView.setAdapter(adapterRecyclerView);
        binding.autoCompleteTextView.setAdapter(adapterAutoComplete);

        ContactViewModel contactViewModel = new ViewModelProvider(ContactListFragment.this).get(ContactViewModel.class);
        contactViewModel.setLoggedInUserId(chatActivity.getLoggedInUser().getUserId());
        contactViewModel.getContactsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                final String EMPTY = "Empty contact list, click the button below to add a new contact";
                adapterRecyclerView.getContacts().clear();
                adapterAutoComplete.getContactList().clear();
                if (contacts == null || contacts.isEmpty()) {
                    binding.autoCompleteCardView.setVisibility(View.GONE);
                    binding.txtInfo.setText(EMPTY);
                } else {
                    binding.autoCompleteCardView.setVisibility(View.VISIBLE);
                    binding.txtInfo.setText("");
                    adapterRecyclerView.getContacts().addAll(contacts);
                    adapterAutoComplete.getContactList().addAll(contacts);
                }
                adapterRecyclerView.notifyDataSetChanged();
                adapterAutoComplete.notifyDataSetChanged();
            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.actionContactListToContactAdd);
            }
        });

        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //scroll down
                if (dy > 20 && binding.fab.isExtended()) {
                    binding.fab.shrink();
                }
                //scroll up
                if (dy < -20 && !binding.fab.isExtended()) {
                    binding.fab.extend();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        chatActivity.setTitle(R.string.contact_list);
    }
}