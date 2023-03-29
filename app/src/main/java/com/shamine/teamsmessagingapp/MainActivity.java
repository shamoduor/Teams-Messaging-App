package com.shamine.teamsmessagingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.shamine.teamsmessagingapp.room.viewmodel.UserViewModel;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private Context context = MainActivity.this;
    private AlertDialog dotsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserViewModel userViewModel = new ViewModelProvider(MainActivity.this).get(UserViewModel.class);
        userViewModel.getLoggedInUserLiveData().observe(MainActivity.this, user -> {
            if (user != null) {
                toChatActivity();
            }
        });
    }

    public void toChatActivity() {
        startActivity(new Intent(context, ChatActivity.class));
        finish();
    }

    public void displayDotsDialog(String message) {
        dismissDotsDialog();
        dotsDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .build();

        dotsDialog.setMessage(message);

        if (!dotsDialog.isShowing()) {
            dotsDialog.show();
        }
    }

    public void dismissDotsDialog() {
        if (dotsDialog != null && dotsDialog.isShowing()) {
            dotsDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissDotsDialog();
        super.onDestroy();
    }
}