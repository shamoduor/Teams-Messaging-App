package com.shamine.teamsmessagingapp.room.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.shamine.teamsmessagingapp.room.entities.MessagePrivate;
import com.shamine.teamsmessagingapp.room.repositories.PrivateMessageRepository;

import java.util.List;

public class PrivateMessageViewModel extends AndroidViewModel {
    private final PrivateMessageRepository repository;
    private final LiveData<List<MessagePrivate>> privateMessagesLiveData;
    private final MutableLiveData<SenderRecipient> senderRecipientMutableLiveData = new MutableLiveData<>();

    public PrivateMessageViewModel(@NonNull Application application) {
        super(application);
        repository = new PrivateMessageRepository(application);
        privateMessagesLiveData = Transformations.switchMap(senderRecipientMutableLiveData,
                (a -> repository.getPrivateMessagesLiveData(a.getSenderId(), a.getRecipientId())));
    }

    public LiveData<List<MessagePrivate>> getUnreceivedMessages() {
        return repository.getUnreceivedMessages();
    }

    public LiveData<List<MessagePrivate>> getPrivateMessagesLiveData() {
        return privateMessagesLiveData;
    }

    public void setSenderIdAndRecipientId(int senderId, int recipientId) {
        senderRecipientMutableLiveData.setValue(new SenderRecipient(senderId, recipientId));
    }

    static class SenderRecipient {
        private final int senderId;
        private final int recipientId;

        SenderRecipient(int senderId, int recipientId) {
            this.senderId = senderId;
            this.recipientId = recipientId;
        }

        public int getSenderId() {
            return senderId;
        }

        public int getRecipientId() {
            return recipientId;
        }
    }
}
