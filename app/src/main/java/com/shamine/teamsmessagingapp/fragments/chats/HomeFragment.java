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
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.adapters.autocomplete.AutoCompleteChats;
import com.shamine.teamsmessagingapp.adapters.recyclerview.HomeThreadAdapter;
import com.shamine.teamsmessagingapp.databinding.FragmentHomeBinding;
import com.shamine.teamsmessagingapp.room.MyDatabase;
import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.models.ContactGroup;
import com.shamine.teamsmessagingapp.room.viewmodel.ChatGroupViewModel;
import com.shamine.teamsmessagingapp.room.views.ChatGroupView;
import com.shamine.teamsmessagingapp.room.views.ContactGroupDbView;
import com.shamine.teamsmessagingapp.room.views.ContactView;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private ChatActivity chatActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatActivity = (ChatActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(chatActivity));

        HomeThreadAdapter adapterRecyclerView = new HomeThreadAdapter(chatActivity);
        binding.recyclerView.setAdapter(adapterRecyclerView);

        AutoCompleteChats adapterAutoComplete = new AutoCompleteChats(chatActivity, new AutoCompleteChats.EventListener() {
            @Override
            public void onItemSelected(ContactGroup contactGroup, int index) {
                binding.autoCompleteTextView.setText("");
                for (int i = 0; i < adapterRecyclerView.getContactGroupList().size(); i++) {
                    ContactGroup cg = adapterRecyclerView.getContactGroupList().get(i);
                    if (cg.isContact()) {
                        Contact contact = cg.getContactView().getContact();
                        if (contact.getUserId() == contactGroup.getContactView().getContact().getUserId()) {
                            Objects.requireNonNull(binding.recyclerView.findViewHolderForAdapterPosition(i)).itemView.performClick();
                            break;
                        }
                    } else if (cg.isChatGroup()) {
                        ChatGroup chatGroup = cg.getChatGroupView().getChatGroup();
                        if (chatGroup.getChatGroupId() == contactGroup.getChatGroupView().getChatGroup().getChatGroupId()) {
                            Objects.requireNonNull(binding.recyclerView.findViewHolderForAdapterPosition(i)).itemView.performClick();
                            break;
                        }
                    }
                }
                Utils.dismissKeypad(chatActivity);
            }
        });
        binding.autoCompleteTextView.setAdapter(adapterAutoComplete);

        ChatGroupViewModel chatGroupViewModel = new ViewModelProvider(HomeFragment.this).get(ChatGroupViewModel.class);
        chatGroupViewModel.getUserContactsAndGroupsLiveData().observe(getViewLifecycleOwner(), new Observer<ContactGroupDbView>() {
            @Override
            public void onChanged(ContactGroupDbView newLists) {

                adapterAutoComplete.getContactGroupList().clear();
                adapterRecyclerView.getContactGroupList().clear();
                if (newLists != null) {
                    if (newLists.getChatGroups() != null) {
                        for (ChatGroupView g : newLists.getChatGroups()) {
                            if (g.getChatGroup().isAvailable()) {
                                int numNewMsgs = MyDatabase.getInstance(chatActivity).
                                        messageGroupDao().getUnreadMessages(g.getChatGroup().getChatGroupId()).size();

                                adapterAutoComplete.getContactGroupList().
                                        add(new ContactGroup(g, g.getChatGroup().getLastMessageTime(), numNewMsgs));

                                adapterRecyclerView.getContactGroupList().
                                        add(new ContactGroup(g, g.getChatGroup().getLastMessageTime(), numNewMsgs));
                            }

                        }
                    }
                    if (newLists.getContacts() != null) {
                        for (ContactView c : newLists.getContacts()) {
                            int numNewMsgs = MyDatabase.getInstance(chatActivity).
                                    messagePrivateDao().getUnreadMessages(c.getContact().getUserId()).size();

                            adapterAutoComplete.getContactGroupList().
                                    add(new ContactGroup(c, c.getContact().getLastMessageTime(), numNewMsgs));

                            adapterRecyclerView.getContactGroupList().
                                    add(new ContactGroup(c, c.getContact().getLastMessageTime(), numNewMsgs));
                        }
                    }
                }
                if (adapterRecyclerView.getContactGroupList().isEmpty()) {
                    binding.txtInfo.setText(R.string.no_chats_found);
                } else {
                    binding.txtInfo.setText("");
                }
                Collections.sort(adapterRecyclerView.getContactGroupList(), new Comparator<ContactGroup>() {
                    @Override
                    public int compare(ContactGroup t1, ContactGroup t2) {
                        return (int) (t2.getLastMessageTime() - t1.getLastMessageTime());
                    }
                });

                adapterAutoComplete.notifyDataSetChanged();
                adapterRecyclerView.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menuSearchUserGroup) {
            chatActivity.displayToolbarSearch();
        } else if (itemId == R.id.menuViewContactList) {
            Navigation.findNavController(chatActivity, R.id.navHostFragment).navigate(R.id.actionHomeToContactList);
        } else if (itemId == R.id.menuCreateGroup) {
            Navigation.findNavController(chatActivity, R.id.navHostFragment).navigate(R.id.actionHomeToCreateGroup);
        } else if (itemId == R.id.menuViewProfileDetails) {
            Navigation.findNavController(chatActivity, R.id.navHostFragment).navigate(R.id.actionHomeToProfile);
        } else if (itemId == R.id.menuLogOut) {
            chatActivity.logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        chatActivity.setTitle(R.string.home);
    }
}
