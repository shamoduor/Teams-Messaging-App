package com.shamine.teamsmessagingapp.fragments.chats;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.adapters.autocomplete.AutoCompleteContacts;
import com.shamine.teamsmessagingapp.adapters.recyclerview.ContactAddToGroupAdapter;
import com.shamine.teamsmessagingapp.databinding.FragmentCreateGroupBinding;
import com.shamine.teamsmessagingapp.retrofit.request.CreateGroupRequest;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.repositories.ChatGroupRepository;
import com.shamine.teamsmessagingapp.room.viewmodel.ContactViewModel;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupFragment extends Fragment {
    private ChatActivity chatActivity;

    private FragmentCreateGroupBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatActivity = (ChatActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_group, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ContactAddToGroupAdapter adapterRecyclerView = new ContactAddToGroupAdapter(binding.txtBtnCreate, "Create Group");
        AutoCompleteContacts adapterAutoComplete = new AutoCompleteContacts(chatActivity, new AutoCompleteContacts.EventListener() {
            @Override
            public void onItemSelected(Contact contact, int index) {
                binding.autoCompleteTextView.setText("");
                for (int a = 0; a < adapterRecyclerView.getContacts().size(); a++) {
                    Contact c = adapterRecyclerView.getContacts().get(a);
                    if (c.getUserId() == contact.getUserId() && !c.isChecked()) {
                        c.setChecked(true);
                        adapterRecyclerView.notifyItemChanged(a);
                        adapterRecyclerView.updateButtonText();
                        break;
                    }
                }
                Utils.dismissKeypad(chatActivity);
            }
        });

        binding.autoCompleteTextView.setAdapter(adapterAutoComplete);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(chatActivity));
        binding.recyclerView.setAdapter(adapterRecyclerView);

        ContactViewModel contactViewModel = new ViewModelProvider(CreateGroupFragment.this).get(ContactViewModel.class);
        contactViewModel.setLoggedInUserId(chatActivity.getLoggedInUser().getUserId());
        contactViewModel.getContactsLiveData().observe(getViewLifecycleOwner(), new Observer<List<Contact>>() {
            @Override
            public void onChanged(List<Contact> contacts) {
                adapterRecyclerView.getContacts().clear();
                adapterAutoComplete.getContactList().clear();
                if (contacts != null && !contacts.isEmpty()) {
                    adapterRecyclerView.getContacts().addAll(contacts);
                    adapterAutoComplete.getContactList().addAll(contacts);
                }

                adapterRecyclerView.notifyDataSetChanged();
                adapterAutoComplete.notifyDataSetChanged();
            }
        });

        binding.cardBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupName = binding.edtGroupName.getText().toString().trim();
                int groupSize = adapterRecyclerView.getCheckedCount();

                if (Utils.stringNullOrEmpty(groupName)) {
                    Toast.makeText(chatActivity, "Enter an appropriate group name", Toast.LENGTH_LONG).show();
                } else if (groupSize < 1) {
                    Toast.makeText(chatActivity, "Select at least one contact to be a group member", Toast.LENGTH_LONG).show();
                } else {
                    AlertDialog.Builder conBuilder = Utils.constructAlertDialog(chatActivity,
                            "Create group",
                            "Proceed to create group: <b>" + groupName +
                                    "</b> with <b>" + groupSize + "</b> member" + (groupSize > 1 ? "s" : "") + "?");
                    conBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            List<Integer> memberIds = new ArrayList<>();
                            memberIds.add(chatActivity.getLoggedInUser().getUserId());
                            for (Contact c : adapterRecyclerView.getContacts()) {
                                if (c.isChecked()) {
                                    memberIds.add(c.getUserId());
                                }
                            }
                            chatActivity.displayDotsDialog("Creating group");
                            ChatGroupRepository repository = new ChatGroupRepository(chatActivity.getApplication());
                            repository.createChatGroup(chatActivity, new CreateGroupRequest(groupName, memberIds));
                        }
                    });
                    conBuilder.show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        chatActivity.setTitle(R.string.create_new_group);
    }
}
