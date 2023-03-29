package com.shamine.teamsmessagingapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;

import com.shamine.teamsmessagingapp.ChatActivity;

import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class Utils {
    public static boolean isValidName(String name) {
        String nameRegex = "^[\\p{L} .'-]+$";

        Pattern pattern = Pattern.compile(nameRegex);
        if (name == null) {
            return false;
        }
        return pattern.matcher(name).matches();
    }

    public static boolean isValidEmailAddress(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }

    public static boolean isValidUsername(String username) {
        String nameRegex = "^(?=.{3,20}$)(?![.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![.])$";

        Pattern pattern = Pattern.compile(nameRegex);
        if (username == null) {
            return false;
        }
        return pattern.matcher(username).matches();
    }

    public static boolean stringNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static AlertDialog constructDotsDialog(Context context, String message) {
        AlertDialog fetchingDialog = new SpotsDialog.Builder().setContext(context).build();
        fetchingDialog.setMessage(message);
        fetchingDialog.setCancelable(false);
        return fetchingDialog;
    }

    public static AlertDialog.Builder constructAlertDialog(Context context, String title, String htmlMessage) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Html.fromHtml(htmlMessage))
                .setNegativeButton("Cancel", null);
    }

    public static void dismissKeypad(ChatActivity chatActivity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) chatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && chatActivity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(chatActivity.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
    }

    public static int getRecyclerViewColumnCount(ChatActivity chatActivity, int phone, int tablet, int xTablet) {
        if (chatActivity.isTablet()) {
            return chatActivity.isXLargeTablet() ? xTablet : tablet;
        } else {
            return phone;
        }
    }

    public static String encrypt(String subject, String sentTime, String sender, String receiver) {
        return ElCrypto.crypto(subject, sentTime, sender, receiver, true);
    }

    public static String decrypt(String subject, String sentTime, String sender, String receiver) {
        return ElCrypto.crypto(subject, sentTime, sender, receiver, false);
    }

    public static String authCrypt(String strToEncrypt, String sentTime) {
        return ElCrypto.authCrypto(strToEncrypt, sentTime, true);
    }

    public static String authDecrypt(String strToDecrypt, String sentTime) {
        return ElCrypto.authCrypto(strToDecrypt, sentTime, false);
    }

    public static String getFormattedTime(Long timeInMillis) {
        int minutes = timeInMillis.intValue() / (1000 * 60);
        StringBuilder sb = new StringBuilder();

        if (minutes > 0) {
            sb.append(minutes);
            sb.append(":");
        }
        int seconds = (timeInMillis.intValue() % (1000 * 60)) / 1000;
        if (minutes > 0 && seconds < 10) {
            sb.append("0");
        }
        sb.append(seconds);
        return sb.toString();
    }

    public static void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) {
            int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.POST_NOTIFICATIONS);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                String[] permissionArray = {android.Manifest.permission.POST_NOTIFICATIONS};
                ActivityCompat.requestPermissions(activity, permissionArray, 573);
            }
        }

    }
}
