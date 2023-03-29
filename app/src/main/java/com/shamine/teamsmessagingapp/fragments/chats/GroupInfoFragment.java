package com.shamine.teamsmessagingapp.fragments.chats;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.adapters.recyclerview.ChatGroupMembersAdapter;
import com.shamine.teamsmessagingapp.databinding.FragmentGroupInfoBinding;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.retrofit.request.RenameGroupRequest;
import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.ChatGroupMember;
import com.shamine.teamsmessagingapp.room.repositories.ChatGroupRepository;
import com.shamine.teamsmessagingapp.room.viewmodel.ChatGroupMemberViewModel;
import com.shamine.teamsmessagingapp.room.viewmodel.ChatGroupViewModel;
import com.shamine.teamsmessagingapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GroupInfoFragment extends Fragment {
    private ChatActivity chatActivity;
    public final static String GROUP_KEY = "GROUP_KEY";
    private ChatGroup chatGroup = null;
    private static final int PICK_GROUP_PIC_REQUEST_CODE = 74;

    private FragmentGroupInfoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatActivity = (ChatActivity) requireActivity();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_info, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(GROUP_KEY)) {
            try {
                requestPermissions();
                chatGroup = new Gson().fromJson(bundle.getString(GROUP_KEY), ChatGroup.class);
                binding.recyclerView.setLayoutManager(new LinearLayoutManager(chatActivity));
                ChatGroupMembersAdapter adapter = new ChatGroupMembersAdapter(chatActivity);
                binding.recyclerView.setAdapter(adapter);
                updateChatGroupDetails(chatGroup, binding.imgIcon);

                ChatGroupMemberViewModel chatGroupMemberViewModel = new ViewModelProvider(GroupInfoFragment.this).get(ChatGroupMemberViewModel.class);
                ChatGroupViewModel chatGroupViewModel = new ViewModelProvider(GroupInfoFragment.this).get(ChatGroupViewModel.class);
                chatGroupMemberViewModel.getGroupMembersByGroupIdLiveData().observe(getViewLifecycleOwner(), new Observer<List<ChatGroupMember>>() {
                    @Override
                    public void onChanged(List<ChatGroupMember> chatGroupMembers) {
                        if (chatGroupMembers != null) {
                            adapter.getChatGroupMembers().clear();
                            adapter.getChatGroupMembers().addAll(chatGroupMembers);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

                chatGroupViewModel.getChatGroupByIdLiveData().observe(getViewLifecycleOwner(), new Observer<ChatGroup>() {
                    @Override
                    public void onChanged(ChatGroup chatGroup) {
                        updateChatGroupDetails(chatGroup, binding.imgIcon);
                    }
                });
                chatGroupMemberViewModel.setGroupId(chatGroup.getChatGroupId());
                chatGroupViewModel.setGroupId(chatGroup.getChatGroupId());
            } catch (Exception e) {
                e.printStackTrace();
                chatActivity.onBackPressed();
            }
        } else {
            chatActivity.onBackPressed();
        }
    }

    private void updateChatGroupDetails(ChatGroup chatGroup, ImageView imgIcon) {
        GroupInfoFragment.this.chatGroup = chatGroup;

        chatActivity.setTitle(chatGroup.getTitle() + " - Info");
        if (chatGroup.getPicUrl() != null && !chatGroup.getPicUrl().isEmpty()) {
            Picasso.get()
                    .load(RetrofitApiClient.IMAGE_BASE_URL + chatGroup.getPicUrl())
                    .placeholder(R.drawable.ic_group)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.ic_group)
                    .into(imgIcon);
        } else {
            imgIcon.setImageResource(R.drawable.ic_group);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_chat_group_info, menu);
        if (chatGroup != null) {
            boolean isAdmin = chatGroup.getCreatedBy() != 0 && chatGroup.getCreatedBy() == chatActivity.getLoggedInUser().getUserId();
            menu.findItem(R.id.menuAddMember).setVisible(isAdmin);
            menu.findItem(R.id.menuRename).setVisible(isAdmin);
            menu.findItem(R.id.menuEditIcon).setVisible(isAdmin);
            menu.findItem(R.id.menuDeleteGroup).setVisible(isAdmin);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (chatGroup != null) {
            if (item.getItemId() == R.id.menuAddMember) {
                Bundle bundle = new Bundle();
                bundle.putInt(AddGroupMembersFragment.GROUP_ID_TAG, chatGroup.getChatGroupId());
                Navigation.findNavController(chatActivity, R.id.navHostFragment).navigate(R.id.actionGroupInfoToAddGroupMembers, bundle);
            } else if (item.getItemId() == R.id.menuRename) {
                final BottomSheetDialog dialog = new BottomSheetDialog(chatActivity);
                View dialogView = View.inflate(chatActivity, R.layout.dialog_edit_text, null);
                dialog.setContentView(dialogView);
                dialog.setCancelable(false);

                final EditText edtText = dialogView.findViewById(R.id.edtText);
                final TextView txtTitle = dialogView.findViewById(R.id.txtTitle);
                final TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
                final TextView btnSubmit = dialogView.findViewById(R.id.btnSubmit);
                final String RENAME = "Rename";
                final String RENAME_GROUP = "Rename Group";
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                txtTitle.setText(RENAME_GROUP);
                final String title = Utils.stringNullOrEmpty(chatGroup.getTitle()) ? "" : chatGroup.getTitle();
                btnSubmit.setText(RENAME);
                txtTitle.setText(RENAME_GROUP);
                edtText.setHint("New Group Name");
                edtText.setText(title);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        String newTitle = edtText.getText().toString().trim();

                        if (!newTitle.isEmpty() && !(newTitle.equals(title))) {
                            androidx.appcompat.app.AlertDialog conDialog = new androidx.appcompat.app.AlertDialog.Builder(chatActivity).create();
                            conDialog.setTitle("Rename Group");
                            conDialog.setMessage("Proceed to rename group?");
                            conDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Rename", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ChatGroupRepository repository = new ChatGroupRepository(chatActivity.getApplication());
                                    repository.renameGroup(chatActivity, new RenameGroupRequest(newTitle, chatGroup.getChatGroupId()));
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
                dialog.show();
            } else if (item.getItemId() == R.id.menuEditIcon) {
                if (requestPermissions()) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    startActivityForResult(intent, PICK_GROUP_PIC_REQUEST_CODE);
                } else {
                    Toast.makeText(chatActivity, "EXTERNAL_STORAGE permission required", Toast.LENGTH_SHORT).show();
                }
            } else if (item.getItemId() == R.id.menuDeleteGroup) {
                androidx.appcompat.app.AlertDialog conDialog = new androidx.appcompat.app.AlertDialog.Builder(chatActivity).create();
                conDialog.setTitle("Delete Group");
                conDialog.setMessage(Html.fromHtml("Proceed to delete group: <b>" + chatGroup.getTitle() + "</b>?"));
                conDialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChatGroupRepository repository = new ChatGroupRepository(chatActivity.getApplication());
                        repository.deleteGroup(chatActivity, chatGroup.getChatGroupId());
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_GROUP_PIC_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            chatActivity.displayDotsDialog("Please wait");
            ChatGroupRepository repository = new ChatGroupRepository(chatActivity.getApplication());
            repository.changeGroupPicture(chatActivity, imageUri, chatGroup.getChatGroupId());
        }
    }

    private boolean requestPermissions() {
        final boolean[] allGranted = {false};
        Dexter.withContext(chatActivity)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        allGranted[0] = report.areAllPermissionsGranted();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();

        return allGranted[0];
    }
}
