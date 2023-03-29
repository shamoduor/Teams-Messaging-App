package com.shamine.teamsmessagingapp.fragments.chats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.adapters.recyclerview.ContactAddAdapter;
import com.shamine.teamsmessagingapp.databinding.FragmentContactAddBinding;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.repositories.ContactRepository;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.util.List;

public class ContactAddFragment extends Fragment {
    private ChatActivity chatActivity;
    private ContactAddAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private FragmentContactAddBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatActivity = (ChatActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact_add, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(chatActivity));
        adapter = new ContactAddAdapter(chatActivity);
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_add, menu);
        ContactRepository contactRepository = new ContactRepository(chatActivity.getApplication());
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchView.isIconified()) {
                    searchView.setIconified(false);
                }
                searchItem.collapseActionView();
                if (!Utils.stringNullOrEmpty(query)) {
                    chatActivity.displayDotsDialog("Searching");
                    contactRepository.searchContacts(chatActivity, ContactAddFragment.this, query.trim());
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void updateSearchResults(List<Contact> contacts) {
        final String EMPTY = "Empty results";
        adapter.getSearchResults().clear();
        if (contacts == null || contacts.isEmpty()) {
            binding.txtInfo.setText(EMPTY);
        } else {
            binding.txtInfo.setText("");
            adapter.getSearchResults().addAll(contacts);
        }
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        chatActivity.setTitle(getString(R.string.add_contact));
    }
}
