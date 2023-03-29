package com.shamine.teamsmessagingapp.room.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.shamine.teamsmessagingapp.ChatActivity;
import com.shamine.teamsmessagingapp.room.MyDatabase;
import com.shamine.teamsmessagingapp.room.dao.GroupMessageDeliveryDao;
import com.shamine.teamsmessagingapp.room.dao.MessageGroupDao;
import com.shamine.teamsmessagingapp.room.entities.GroupMessageDelivery;
import com.shamine.teamsmessagingapp.room.entities.MessageGroup;
import com.shamine.teamsmessagingapp.room.models.GenericMessage;

import java.util.Date;
import java.util.List;

public class GroupMessageRepository {
    private final MessageGroupDao groupDao;

    public GroupMessageRepository(Application application) {
        this.groupDao = MyDatabase.getInstance(application).messageGroupDao();
    }

    public LiveData<List<MessageGroup>> getGroupMessagesLiveData(int senderId, int chatGroupId) {
        return groupDao.getGroupMessagesLiveData(senderId, chatGroupId);
    }

    public void saveMessage(ChatActivity chatActivity, MessageGroup messageGroup, List<GroupMessageDelivery> deliveries) {
        new Thread() {
            @Override
            public void run() {
                GroupMessageDelivery groupMessageDelivery;
                try {
                    GroupMessageDelivery deliveryToSync = null;
                    MyDatabase.getInstance(chatActivity.getApplication()).messageGroupDao().add(messageGroup);
                    if (deliveries != null && !deliveries.isEmpty()) {
                        MyDatabase.getInstance(chatActivity.getApplication()).groupMessageDeliveryDao().add(deliveries);
                        for (GroupMessageDelivery d : deliveries) {
                            if (d.getUserId() == chatActivity.getLoggedInUser().getUserId()) {
                                deliveryToSync = d;
                                deliveryToSync.setReceivedOn(new Date().getTime());
                            }
                        }
                    }
                    groupMessageDelivery = deliveryToSync;
                } catch (Exception e) {
                    e.printStackTrace();
                    groupMessageDelivery = null;
                }


                GroupMessageDelivery finalGroupMessageDelivery = groupMessageDelivery;
                chatActivity.runOnUiThread(() -> {
                    if (finalGroupMessageDelivery != null) {
                        GenericMessage genericMessage = new GenericMessage();
                        genericMessage.setGroupMessageDelivery(finalGroupMessageDelivery);
                        chatActivity.getWebSocketClient().send(new Gson().toJson(genericMessage));
                    }
                });
            }
        }.start();
    }

    public void updateMessageRead(ChatActivity chatActivity, MessageGroup messageGroup) {
        new Thread() {
            @Override
            public void run() {
                new Thread() {
                    @Override
                    public void run() {
                        GroupMessageDelivery delivery = null;
                        try {
                            GroupMessageDeliveryDao groupMessageDeliveryDao = MyDatabase.getInstance(chatActivity.getApplication()).groupMessageDeliveryDao();
                            boolean sync = false;
                            GroupMessageDelivery groupMessageDelivery = groupMessageDeliveryDao.findByUserIdAndMessageGroupId(chatActivity.getLoggedInUser().getUserId(), messageGroup.getMessageId());

                            if (groupMessageDelivery != null && groupMessageDelivery.getUserId() == chatActivity.getLoggedInUser().getUserId()) {
                                if (groupMessageDelivery.getReceivedOn() == 0) {
                                    groupMessageDelivery.setReceivedOn(new Date().getTime());
                                    sync = true;
                                }
                                if (groupMessageDelivery.getReadOn() == null || groupMessageDelivery.getReadOn() == 0) {
                                    groupMessageDelivery.setReadOn(new Date().getTime());
                                    sync = true;
                                }
                                if (sync) {
                                    groupMessageDeliveryDao.add(groupMessageDelivery);
                                    delivery = groupMessageDelivery;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        GroupMessageDelivery finalDelivery = delivery;
                        chatActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (finalDelivery != null) {
                                    GenericMessage genericMessage = new GenericMessage();
                                    genericMessage.setGroupMessageDelivery(finalDelivery);
                                    chatActivity.getWebSocketClient().send(new Gson().toJson(genericMessage));
                                }
                            }
                        });
                    }
                }.start();
            }
        }.start();
    }


}
