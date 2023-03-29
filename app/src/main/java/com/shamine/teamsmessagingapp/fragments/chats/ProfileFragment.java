package com.shamine.teamsmessagingapp.fragments.chats;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.R;
import com.shamine.teamsmessagingapp.databinding.FragmentProfileBinding;
import com.shamine.teamsmessagingapp.retrofit.RetrofitApiClient;
import com.shamine.teamsmessagingapp.room.repositories.UserRepository;
import com.shamine.teamsmessagingapp.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileFragment extends Fragment {
    private ChatActivity chatActivity;
    private static final int PICK_PROFILE_PIC_REQUEST_CODE = 48;
    public static final String ACTION_PICK_PROFILE_PIC_REQUEST_CODE = "ACTION_PICK_PROFILE_PIC_REQUEST_CODE";
    private BroadcastReceiver profileBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        chatActivity = (ChatActivity) requireActivity();
        setupProfileReceiver();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateProfileInfo();
        requestPermissions();
        binding.layoutChangePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestPermissions()) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/jpeg");
                    startActivityForResult(intent, PICK_PROFILE_PIC_REQUEST_CODE);
                } else {
                    Toast.makeText(chatActivity, "EXTERNAL_STORAGE permission required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateProfileInfo() {
        binding.txtFullName.setText(chatActivity.getLoggedInUser().getName());
        String username = "@" + chatActivity.getLoggedInUser().getUsername();
        binding.txtUsername.setText(username);
        binding.txtEmail.setText(chatActivity.getLoggedInUser().getEmail());
        if (chatActivity.getLoggedInUser().getPicUrl() != null) {
            String picUrl = RetrofitApiClient.IMAGE_BASE_URL + chatActivity.getLoggedInUser().getPicUrl();

            Log.d("PIC", picUrl);


            Picasso.get()
                    .load(picUrl)
                    .placeholder(R.drawable.temp_user)
                    .resize(500, 500)
                    .centerCrop()
                    .onlyScaleDown()
                    .error(R.drawable.temp_user)
                    .into(binding.civProfilePic);
        } else {
            binding.civProfilePic.setImageResource(R.drawable.temp_user);
        }
    }

    private boolean requestPermissions() {
        final boolean[] allGranted = {false};

        String[] permissionsToRequest;

        if (Build.VERSION.SDK_INT >= 33) {
            permissionsToRequest = new String[] {Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissionsToRequest = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }

        Dexter.withContext(chatActivity)
                .withPermissions(permissionsToRequest)
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

    private void setupProfileReceiver() {
        profileBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent i) {
                if (i.getAction() != null && i.getAction().equals(ACTION_PICK_PROFILE_PIC_REQUEST_CODE)) {
                    updateProfileInfo();
                }
            }
        };

        chatActivity.registerReceiver(profileBroadcastReceiver, new IntentFilter(ACTION_PICK_PROFILE_PIC_REQUEST_CODE));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final BottomSheetDialog dialog = new BottomSheetDialog(chatActivity);
        View dialogView = View.inflate(chatActivity, R.layout.dialog_edit_text, null);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);

        final EditText edtText = dialogView.findViewById(R.id.edtText);
        final TextView txtTitle = dialogView.findViewById(R.id.txtTitle);
        final TextView btnCancel = dialogView.findViewById(R.id.btnCancel);
        final TextView btnSubmit = dialogView.findViewById(R.id.btnSubmit);
        final String DISPLAY_NAME = "Display Name", USERNAME = "Username";

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        UserRepository repository = new UserRepository(chatActivity.getApplication());
        int itemId = item.getItemId();
        if (itemId == R.id.menuChangeUsername) {
            txtTitle.setText(USERNAME);
            edtText.setHint(USERNAME);
            edtText.setText(chatActivity.getLoggedInUser().getUsername());
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    String newUsername = edtText.getText().toString().trim();
                    if (Utils.isValidUsername(newUsername)) {
                        chatActivity.displayDotsDialog("Updating username");
                        repository.updateProfileDetails(chatActivity, null, newUsername);
                    } else {
                        Toast.makeText(chatActivity, "Enter a valid username", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
        } else if (itemId == R.id.menuEditDisplayName) {
            txtTitle.setText(DISPLAY_NAME);
            edtText.setHint(DISPLAY_NAME);
            edtText.setText(chatActivity.getLoggedInUser().getName());
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    String newName = edtText.getText().toString().trim();
                    if (Utils.isValidName(newName)) {
                        chatActivity.displayDotsDialog("Updating display name");
                        repository.updateProfileDetails(chatActivity, newName, null);
                    } else {
                        Toast.makeText(chatActivity, "Enter a valid name", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        chatActivity.setTitle(R.string.profile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PROFILE_PIC_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            chatActivity.displayDotsDialog("Please wait");
            UserRepository repository = new UserRepository(chatActivity.getApplication());
            repository.changeProfilePicture(chatActivity, imageUri);
        }
    }

    @Override
    public void onDestroy() {
        if (profileBroadcastReceiver != null) {
            chatActivity.unregisterReceiver(profileBroadcastReceiver);
        }
        super.onDestroy();
    }
}
