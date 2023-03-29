package com.shamine.teamsmessagingapp.room.views;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Relation;

import com.shamine.teamsmessagingapp.room.entities.ChatGroup;
import com.shamine.teamsmessagingapp.room.entities.MessageGroup;
import com.shamine.teamsmessagingapp.utils.Utils;

import java.util.List;

@DatabaseView("SELECT * FROM ChatGroup WHERE available = 1")
public class ChatGroupView {
    @Embedded
    private ChatGroup chatGroup;

    @Relation(parentColumn = "chatGroupId", entityColumn = "chatGroupId", entity = MessageGroup.class)
    private List<MessageGroup> groupMessages;

    public ChatGroup getChatGroup() {
        if (chatGroup.isAvailable()) {
            long lastMessageTime = 0;
            MessageGroup lastMessage = null;
            for (MessageGroup message : groupMessages) {
                if (message.getCreatedOn() > lastMessageTime) {
                    lastMessageTime = message.getCreatedOn();
                    lastMessage = message;
                }
            }
            String lastMessageString = "Click to start chatting";
            String lastMessageSenderName = "";
            if (lastMessage != null) {
                lastMessageString = Utils.decrypt(lastMessage.getContent(), String.valueOf(lastMessage.getCreatedOn()),
                        String.valueOf(lastMessage.getSenderId()), String.valueOf(lastMessage.getChatGroupId()));
                chatGroup.setLastMessageSenderId(lastMessage.getSenderId());
                lastMessageSenderName = lastMessage.getSenderName();
            }

            chatGroup.setLastMessageSenderName(lastMessageSenderName);
            chatGroup.setLastMessageTime(lastMessageTime);
            chatGroup.setLastMessage(lastMessageString);
            return chatGroup;
        } else {
            return null;
        }
    }

    public void setChatGroup(ChatGroup chatGroup) {
        this.chatGroup = chatGroup;
    }

    public List<MessageGroup> getGroupMessages() {
        return groupMessages;
    }

    public void setGroupMessages(List<MessageGroup> groupMessages) {
        this.groupMessages = groupMessages;
    }
}
