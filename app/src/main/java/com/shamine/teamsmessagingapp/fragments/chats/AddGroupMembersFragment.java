package com.shamine.teamsmessagingapp.fragments.chats;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.adapters.autocomplete.AutoCompleteContacts;
import com.shamine.teamsmessagingapp.adapters.recyclerview.ContactAddToGroupAdapter;
import com.shamine.teamsmessagingapp.databinding.FragmentGroupAddMembersBinding;
import com.shamine.teamsmessagingapp.retrofit.request.AddGroupMembersRequest;
import com.shamine.teamsmessagingapp.room.entities.Contact;
import com.shamine.teamsmessagingapp.room.repositories.ChatGroupMemberRepository;
import com.shamine.teamsmessagingapp.room.viewmodel.ContactViewModel;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AddGroupMembersFragment extends Fragment implements AutoCompleteContacts.EventListener {
    private ChatActivity chatActivity;
    private ContactAddToGroupAdapter adapter;

    public static String GROUP_ID_TAG = "GROUP_ID_TAG";

    private FragmentGroupAddMembersBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatActivity = (ChatActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_add_members, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int columns = Utils.getRecyclerViewColumnCount(chatActivity, 1, 2, 3);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(columns, RecyclerView.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        binding.recyclerView.setLayoutManager(layoutManager);
        adapter = new ContactAddToGroupAdapter(binding.txtBtnCard, "Add Members");
        binding.recyclerView.setAdapter(adapter);

        final ContactViewModel contactViewModel = new ViewModelProvider(AddGroupMembersFragment.this).get(ContactViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(GROUP_ID_TAG)) {
            int groupId = bundle.getInt(GROUP_ID_TAG);

            contactViewModel.setGroupUser(groupId, chatActivity.getLoggedInUser().getUserId());

            contactViewModel.getContactsExcludeGroupIdLiveData().observe(getViewLifecycleOwner(), new Observer<List<Contact>>() {
                @Override
                public void onChanged(List<Contact> contacts) {
                    if (contacts != null) {
                        AutoCompleteContacts autoCompleteContactsAdapter = new AutoCompleteContacts(chatActivity, AddGroupMembersFragment.this);
                        if (!autoCompleteContactsAdapter.getContactList().isEmpty()) {
                            autoCompleteContactsAdapter.getContactList().clear();
                        }
                        if (!adapter.getContacts().isEmpty()) {
                            adapter.getContacts().clear();
                        }

                        if (contacts.isEmpty()) {
                            String empty = "No contact found, who is not already a group member";
                            binding.txtInfo.setText(empty);
                        } else {
                            binding.txtInfo.setText("");
                            adapter.getContacts().addAll(contacts);
                            autoCompleteContactsAdapter.getContactList().addAll(contacts);
                        }

                        adapter.notifyDataSetChanged();
                        binding.autoCompleteTextView.setAdapter(autoCompleteContactsAdapter);
                    }
                }
            });

            binding.cardBtnAddMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int groupSize = adapter.getCheckedCount();

                    if (groupSize < 1) {
                        Toast.makeText(chatActivity, "Select at least one contact to be added to the group", Toast.LENGTH_LONG).show();
                    } else {
                        String plural = groupSize > 1 ? "s" : "";
                        androidx.appcompat.app.AlertDialog conDialog = new androidx.appcompat.app.AlertDialog.Builder(chatActivity).create();
                        conDialog.setTitle("Add Group Member" + plural);
                        conDialog.setMessage(Html.fromHtml("Proceed to add <b>" + groupSize +
                                "</b> member" + plural + " to the group?"));
                        conDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                List<Integer> contactIds = new ArrayList<>();
                                for (Contact c : adapter.getContacts()) {
                                    if (c.isChecked()) {
                                        contactIds.add(c.getUserId());
                                    }
                                }

                                ChatGroupMemberRepository repository = new ChatGroupMemberRepository(chatActivity.getApplication());
                                repository.addChatGroupMembers(chatActivity, new AddGroupMembersRequest(groupId, contactIds));
                            }
                        });

                        conDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        conDialog.show();
                    }
                }
            });
        } else {
            Log.e("AddGroupMembers", "Group Id or title not found");
            chatActivity.onBackPressed();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        chatActivity.setTitle("Add Group Members");
    }


    @Override
    public void onItemSelected(Contact contact, int index) {
        autoCompleteItemSelected(contact);
    }

    private void autoCompleteItemSelected(Contact contact) {
        binding.autoCompleteTextView.setText("");
        boolean found = false;
        int foundIndex = 0;
        for (int a = 0; a < adapter.getContacts().size(); a++) {
            Contact s = adapter.getContacts().get(a);
            if (s.getUserId() == contact.getUserId()) {
                found = true;
                foundIndex = a;
                break;
            }
        }

        if (found) {
            Contact foundContact = adapter.getContacts().get(foundIndex);
            if (!foundContact.isChecked()) {
                foundContact.setChecked(true);
            }
            adapter.getContacts().remove(foundIndex);
            adapter.notifyItemRemoved(foundIndex);

            adapter.getContacts().add(0, foundContact);
        } else {
            if (!contact.isChecked()) {
                contact.setChecked(true);
            }
            adapter.getContacts().add(0, contact);
        }
        adapter.notifyItemInserted(0);

        binding.txtInfo.setText("");
        adapter.updateButtonText();

        //scroll to top
        StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) binding.recyclerView.getLayoutManager();
        if (layoutManager != null) {
            layoutManager.scrollToPositionWithOffset(0, 0);
        }

        Utils.dismissKeypad(chatActivity);
    }
}
