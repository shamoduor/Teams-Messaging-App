package com.shamine.teamsmessagingapp.room.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.room.MyDatabase;
import com.shamine.teamsmessagingapp.room.dao.MessagePrivateDao;
import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;
import com.shamine.teamsmessagingapp.room.models.GenericMessage;

import java.util.Date;
import java.util.List;

public class PrivateMessageRepository {
    private final MessagePrivateDao messagePrivateDao;

    public PrivateMessageRepository(Application application) {
        this.messagePrivateDao = MyDatabase.getInstance(application).messagePrivateDao();
    }

    public LiveData<List<MessagePrivate>> getPrivateMessagesLiveData(int senderId, int recipientId) {
        return messagePrivateDao.getPrivateMessagesLiveData(senderId, recipientId);
    }

    public LiveData<List<MessagePrivate>> getUnreceivedMessages() {
        return messagePrivateDao.getUnreceivedMessages();
    }

    public void updateMessageRead(ChatActivity chatActivity, MessagePrivate messagePrivate) {
        new Thread() {
            @Override
            public void run() {
                boolean save = false, sync = false;
                try {
                    if (messagePrivate.getReceivedOn() == null || messagePrivate.getReceivedOn() == 0) {
                        messagePrivate.setReceivedOn(new Date().getTime());
                        save = true;
                    }
                    if (messagePrivate.getReadOn() == null || messagePrivate.getReadOn() == 0) {
                        messagePrivate.setReadOn(new Date().getTime());
                        save = true;
                    }

                    if (save) {
                        messagePrivateDao.add(messagePrivate);
                        if (chatActivity.getLoggedInUser().getUserId() == messagePrivate.getRecipientId()) {
                            sync = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean finalSync = sync;
                chatActivity.runOnUiThread(() -> {
                    if (finalSync) {
                        GenericMessage genericMessage = new GenericMessage();
                        genericMessage.setMessagePrivate(messagePrivate);
                        chatActivity.getWebSocketClient().send(new Gson().toJson(genericMessage));
                    }
                });
            }
        }.start();
    }

    public void updateMessageReceived(ChatActivity chatActivity, MessagePrivate messagePrivate) {
        new Thread() {
            @Override
            public void run() {
                boolean sync = false;
                try {

                    if (messagePrivate.getReceivedOn() == null || messagePrivate.getReceivedOn() == 0) {
                        messagePrivate.setReceivedOn(new Date().getTime());
                        messagePrivateDao.add(messagePrivate);
                        if (chatActivity.getLoggedInUser().getUserId() == messagePrivate.getRecipientId()) {
                            sync = true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean finalSync = sync;
                chatActivity.runOnUiThread(() -> {
                    if (finalSync) {
                        GenericMessage genericMessage = new GenericMessage();
                        genericMessage.setMessagePrivate(messagePrivate);
                        chatActivity.getWebSocketClient().send(new Gson().toJson(genericMessage));
                    }
                });

            }
        }.start();
    }


}
